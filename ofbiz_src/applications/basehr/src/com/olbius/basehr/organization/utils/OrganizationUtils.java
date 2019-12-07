package com.olbius.basehr.organization.utils;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class OrganizationUtils {

	public static int getNbrEmplOfOrganization(LocalDispatcher dispatcher,
			Delegator delegator, GenericValue userLogin, Organization parentOrg, List<Map<String, Object>> list) throws GenericEntityException, GenericServiceException {
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		List<GenericValue> employeeDirect = parentOrg.getDirectEmployee(delegator);
		int parentNumberEmpl = employeeDirect.size();
		Map<String, Object> resultService;
		if(UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				Map<String, Object> tempMap = FastMap.newInstance();
				String childPartyId = child.getString("partyId");
				GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", childPartyId), false);
				tempMap.put("partyId", childPartyId);
				tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, childPartyId));
				tempMap.put("comments", partyGroup.get("comments"));
				tempMap.put("partyIdFrom", parentOrg.getOrg().getString("partyId"));
				tempMap.put("partyName", partyGroup.get("groupName"));
				resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", childPartyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "userLogin", userLogin));
				String contactMechId = (String)resultService.get("contactMechId");
				if(contactMechId != null){
					tempMap.put("contactMechId", contactMechId);
					tempMap.put("postalAddress", CommonUtil.getPostalAddressDetails(delegator, contactMechId));
				}
				int childNbrEmpl = getNbrEmplOfOrganization(dispatcher, delegator, userLogin, PartyUtil.buildOrg(delegator, childPartyId, false, false), list);
				parentNumberEmpl += childNbrEmpl;
				tempMap.put("totalEmployee", childNbrEmpl);
				list.add(tempMap);
			}
		}
		return parentNumberEmpl;
	}

	public static String getPartyLogoImg(Delegator delegator, String rootPartyId) throws GenericEntityException {
		// TODO Auto-generated method stub
		Map<String, Object> partyContentMap = FastMap.newInstance();
		partyContentMap.put("partyId", rootPartyId);
		partyContentMap.put("partyContentTypeId", "LGOIMGURL");
		List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", partyContentMap, UtilMisc.toList("-fromDate"), false);
		if(UtilValidate.isNotEmpty(partyContentList)){
			GenericValue partyContent = partyContentList.get(0);
			String contentId = partyContent.getString("contentId");
			GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
			String dataResourceId = content.getString("dataResourceId");
			GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
			if(dataResource != null){
				String objectInfo = dataResource.getString("objectInfo");
				return objectInfo;
			}
		}
		return null;
	}

	public static String createPartyContent(Delegator delegator, LocalDispatcher dispatcher,
			Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		if(documentFile != null && uploadFileNameStr != null && _uploadedFile_contentType != null){
			String folder = "/hrmdoc/partyImage";
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("public", "Y");
			uploadedFileCtx.put("userLogin", context.get("userLogin"));
			Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			String path = (String)resultService.get("path");
			Map<String, Object> dataResourceCtx = FastMap.newInstance();
			dataResourceCtx.put("objectInfo", path);
	        dataResourceCtx.put("dataResourceName", uploadFileNameStr);
	        dataResourceCtx.put("userLogin", systemUserLogin);
	        dataResourceCtx.put("dataResourceTypeId", "URL_RESOURCE");
	        dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
	        dataResourceCtx.put("isPublic", "Y");
	        resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
	        String dataResourceId = (String) resultService.get("dataResourceId");
	        Map<String, Object> contentCtx = FastMap.newInstance();
	        contentCtx.put("dataResourceId", dataResourceId);
	        contentCtx.put("contentTypeId", "DOCUMENT");
	        contentCtx.put("contentName", uploadFileNameStr);
	        contentCtx.put("userLogin", systemUserLogin);
	        resultService = dispatcher.runSync("createContent", contentCtx);
	        String contentId = (String)resultService.get("contentId");
	        return contentId;
		}
		return null;
	}

	public static void createPartyPostalAddress(DispatchContext dctx,
			Map<String, Object> context, String partyId) throws GenericEntityException, GeneralServiceException, GenericServiceException {
		// TODO Auto-generated method stub
		String address1 = (String)context.get("address1");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if(address1 != null){
			String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
			GenericValue stateProviceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
			String stateProviceGeoName = stateProviceGeo.getString("geoName");
			Map<String, Object> posAddrMap = ServiceUtil.setServiceFields(dispatcher, "createPartyPostalAddress", context, 
					(GenericValue)context.get("userLogin"), (TimeZone)context.get("timeZone"), (Locale)context.get("locale"));
			posAddrMap.remove("comments");
			posAddrMap.put("partyId", partyId);
			posAddrMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			posAddrMap.put("city", stateProviceGeoName);
			posAddrMap.put("postalCode", "10000");
			dispatcher.runSync("createPartyPostalAddress", posAddrMap);
		}
	}

    public static Map<String, Object> getEmplOfOrganization(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
                                            Organization parentOrg, List<Map<String, Object>> list) throws GenericEntityException, GenericServiceException {
        List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
        List<GenericValue> employeeDirect = parentOrg.getDirectEmployee(delegator);
        List<String> returnEmplIds = EntityUtil.getFieldListFromEntityList(employeeDirect, "partyId", true);
        HashSet<String> setReturnEmplIds = new HashSet(returnEmplIds);
        Map<String, Object> resultObj = FastMap.newInstance();
        Map<String, Object> resultService;

        if(UtilValidate.isNotEmpty(orgDirectChild)){
            for(GenericValue child: orgDirectChild){
                Map<String, Object> tempMap = FastMap.newInstance();
                String childPartyId = child.getString("partyId");
                GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", childPartyId), false);
                tempMap.put("partyId", childPartyId);
                tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, childPartyId));
                tempMap.put("comments", partyGroup.get("comments"));
                tempMap.put("partyIdFrom", parentOrg.getOrg().getString("partyId"));
                tempMap.put("partyName", partyGroup.get("groupName"));
                resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("partyId", childPartyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "userLogin", userLogin));
                String contactMechId = (String)resultService.get("contactMechId");
                if(contactMechId != null){
                    tempMap.put("contactMechId", contactMechId);
                    tempMap.put("postalAddress", CommonUtil.getPostalAddressDetails(delegator, contactMechId));
                }
                Map<String, Object> childEmpl = getEmplOfOrganization(dispatcher, delegator, userLogin, PartyUtil.buildOrg(delegator, childPartyId, false, false), list);
                HashSet<String> emplIds = (HashSet<String>)childEmpl.get("childEmplIds");
                setReturnEmplIds.addAll(emplIds);
                tempMap.put("totalEmployee", (int)childEmpl.get("numberOfEmpl"));
                list.add(tempMap);
            }
        }
        resultObj.put("childEmplIds", setReturnEmplIds);
        resultObj.put("numberOfEmpl", setReturnEmplIds.size());
        return resultObj;
    }

}
