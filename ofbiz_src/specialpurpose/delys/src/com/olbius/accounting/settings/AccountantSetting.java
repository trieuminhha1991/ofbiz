package com.olbius.accounting.settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Date;
import java.sql.Timestamp;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.math.BigDecimal;

import org.apache.avalon.framework.service.ServiceException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;
public class AccountantSetting{
	public static final String module = AccountantSetting.class.getName();
	private static List<GenericValue> listEmpl;
	@SuppressWarnings("unchecked")
	/*
	 * @param DispatchContext dpct
	 * @param Map context
	 * @return list employee in orgazitation by manager chiefAcountant
	 * */
	public static Map<String,Object> getListEmployeeInOrganization(DispatchContext dpct,Map<String,Object> context){
		Map<String,Object> result  = ServiceUtil.returnSuccess();
		listEmpl = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Delegator delegator = (Delegator) dpct.getDelegator();
		try {
			Organization org = null;
			Properties generalProp = UtilProperties.getProperties("general");
			String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
			try {
				 org = PartyUtil.buildOrg(delegator, defaultOrganizationPartyId, true, false);
			} catch (Exception e) {
				Debug.log(e,module,"Build Org Failed!");
			}
				
			if(UtilValidate.isNotEmpty(org)){
				listEmpl = org.getEmployeeInOrg(delegator,EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND));
			}
			result.put("listIterator", listEmpl);
			result.put("TotalRows", String.valueOf(listEmpl.size()));
		} catch (Exception e) {
			Debug.log(e,module);
		}
		return result;
	}
	
	public static Map<String,Object> getListGlAccountMember(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> results = ServiceUtil.returnSuccess();
		try {
			DynamicViewEntity view = new DynamicViewEntity();
			view.setEntityName("GlAccountAndMember");	
			view.addMemberEntity("GL", "GlAccount");
			view.addMemberEntity("MB", "GlAccountCategoryMember");
			view.addAlias("GL", "glAccountId");
			view.addAlias("GL", "accountName");
			view.addAlias("MB", "fromDate");
			view.addAlias("MB", "thruDate");
			view.addAlias("MB", "amountPercentage");
			view.addAlias("MB", "glAccountCategoryId");
			view.addViewLink("GL", "MB", Boolean.TRUE,UtilMisc.toList(new ModelKeyMap("glAccountId","glAccountId")));
			view.makeModelViewEntity(delegator);
			String glAccountCategoryId = (String) parameters.get("glAccountCategoryId")[0];
			if(glAccountCategoryId != null && !glAccountCategoryId.isEmpty()){
				listAllConditions.add(EntityCondition.makeCondition("glAccountCategoryId",glAccountCategoryId));	
			}
			opts.setDistinct(true);
			EntityListIterator listIterator = null;
			listIterator = delegator.findListIteratorByCondition(view, EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			results.put("listIterator", listIterator);
		} catch (Exception e) {
			Debug.log(e,module,"Fatal error when get list Gl Account Member cause : " + e.getMessage());
		}
		return results;
	}
	
//	public static Map<String,Object> getPriceProductAcc(DispatchContext dpct,Map<String,Object> context){
//		Map<String,Object> result = ServiceUtil.returnSuccess();
//		Delegator delegator = dpct.getDelegator();
//		String productId = (String) context.get("productId");
//		String currencyDefaultUomId = (String) context.get("currencyDefaultUomId");
//		 Map<String,Object> virtualProductPrices = new HashMap<String,Object>();
//		try {
//			if(UtilValidate.isNotEmpty(productId)){
//				Map<String,Object> mapTmp = FastMap.newInstance();
//				GenericValue product  = delegator.findOne("Product",UtilMisc.toMap("productId", productId),false);
//				mapTmp.put("product", product);
//				if(UtilValidate.isNotEmpty(product)){
//					virtualProductPrices = dpct.getDispatcher().runSync("calculateProductPrice", mapTmp);
//				}
//		       	result.put("price", (virtualProductPrices.get("price")));
//			}else {
//				return ServiceUtil.returnError("Can't missing productId when call service get price product");
//			}
//		} catch (Exception e) {
//			 Debug.logError(e, "An error occurred while getting the product prices", module);
//			return ServiceUtil.returnError(e.getMessage());
//		}
//		return result;
//	}
//	
//	public static Map<String,Object> getDescriptionTax(DispatchContext dpct,Map<String,Object> context){
//		Map<String,Object> result = ServiceUtil.returnSuccess();
//		Delegator delegator = dpct.getDelegator();
//		String id = (String) context.get("id");
//		String description = "";
//		try {
//			if(UtilValidate.isNotEmpty(id)){
//				Map<String,Object> mapTmp = FastMap.newInstance();
//				String[] valString = id.split(";");
//				String taxAuthPartyId = valString[0];
//				String taxAuthGeoId = valString[1];
//				GenericValue  geo = null;
//				if(UtilValidate.isNotEmpty(taxAuthGeoId)){
//					 geo = delegator.findOne("Geo",UtilMisc.toMap("geoId", taxAuthGeoId) , true);
//				}
//				if(UtilValidate.isNotEmpty(taxAuthPartyId)){
//					GenericValue partyView = delegator.findOne("PartyNameView",UtilMisc.toMap("partyId", taxAuthPartyId), true);
//					 description = "[ " + taxAuthPartyId+ " ]"+ (partyView.getString("firstName").isEmpty()? "" :partyView.getString("firstName") )   +" " +( partyView.getString("middleName").isEmpty() ? "" : partyView.getString("middleName").isEmpty())  + " "  +( partyView.getString("lastName").isEmpty() ? "" : partyView.getString("lastName")) + " " 
//							 +  (partyView.getString("groupName").isEmpty() ? "" : partyView.getString("groupName"))  + "-" + "[ " + geo.getString("geoId") + " ]" + (geo.getString("geoName").isEmpty() ? "" : geo.getString("geoName"));
//				}else {
//					 description = "[ " + taxAuthPartyId + " ]" + "[ " + geo.getString("geoId") + " ]" + (geo.getString("geoName").isEmpty() ? "" : geo.getString("geoName"));
//				};
//				result.put("description", description);
//			}else {
//				return ServiceUtil.returnError("Can't missing id when call service get description tax");
//			}
//		} catch (Exception e) {
//			 Debug.logError(e, "An error occurred while getting the product prices", module);
//			return ServiceUtil.returnError(e.getMessage());
//		}
//		return result;
//	}
	
	public static Map<String,Object> getListInvoicesItemTypesGlAccount(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String invItemTypePrefix = (String) (parameters.get("invItemTypePrefix") != null ? parameters.get("invItemTypePrefix")[0] : "INV");
			invItemTypePrefix += "_%";
			listAllConditions.add(EntityCondition.makeCondition("invoiceItemTypeId",EntityJoinOperator.LIKE,invItemTypePrefix));
			EntityListIterator listIterator = delegator.find("InvoicesItemTypesGlAccountDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
				Debug.logError(e,"An error occured white getting list Invoices Item Type GlAcount", module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListGLAccountChart(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	String organizationPartyId = (String) parameters.get("organizationPartyId")[0];
    	try {
    		List<EntityCondition> listCond = FastList.newInstance();
    		listCond.add(EntityCondition.makeCondition("organizationPartyId",EntityJoinOperator.NOT_EQUAL,organizationPartyId));
    		listCond.add(EntityCondition.makeCondition("organizationPartyId",null));
    		listAllConditions.add(EntityCondition.makeCondition(listCond,EntityJoinOperator.OR));
    		GenericValue glAccTax = delegator.findOne("PartyAcctgPreference",UtilMisc.toMap("partyId", organizationPartyId), false);
    		listAllConditions.add(EntityCondition.makeCondition("glTaxFormId",glAccTax.getString("taxFormId")));
    		listIterator = delegator.find("GlAccountOrgDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListGLAccountOACsData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String,Object> uploadImagesAgreementTerm(DispatchContext dpct,Map<String,Object> context) throws ServiceException{
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = FastMap.newInstance();
		java.nio.ByteBuffer imagesData = (java.nio.ByteBuffer) context.get("uploadFile");
		String _uploadedFile_fileName = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String) context.get("_uploadedFile_contentType");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Map<String,Object> mapPrepare = FastMap.newInstance();
			mapPrepare.put("userLogin",userLogin);
			mapPrepare.put("public", "Y");
			mapPrepare.put("uploadedFile", imagesData);
			mapPrepare.put("_uploadedFile_fileName", _uploadedFile_fileName);
			mapPrepare.put("_uploadedFile_contentType", _uploadedFile_contentType);
			mapPrepare.put("folder", "/delys/accounting/agreement");
			
			try {
				Map<String,Object> runSyncSv  = FastMap.newInstance();
				runSyncSv = dpct.getDispatcher().runSync("jackrabbitUploadFile", mapPrepare);
				if(ServiceUtil.isSuccess(runSyncSv)){
					String path = (String) runSyncSv.get("path");
					result.put("path", path);
				}
			} catch (Exception e) {
				Debug.logError("An error when call service jcrUpLoadFile",module);
			}
			
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		
		return result;
	}
	
	
	
	public static Map<String,Object> updateGlAccountCategoryMemberCustom(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> mapRs = ServiceUtil.returnSuccess();
		String glAccountId = (String) context.get("glAccountId");
		String glAccountCategoryId = (String) context.get("glAccountCategoryId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		BigDecimal percentage = (BigDecimal) context.get("amountPercentage");
		Locale locale = (Locale) context.get("locale");
		
		try {
			
			Calendar cal = Calendar.getInstance(locale);
			cal.set(Calendar.MILLISECOND, 0);
			cal.setTime(UtilDateTime.nowTimestamp());
			Timestamp fromDateNew = new Timestamp(cal.getTime().getTime());
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = formatDate.format(cal.getTime());
			Timestamp val = Timestamp.valueOf(date);
			if(UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(glAccountCategoryId) && UtilValidate.isNotEmpty(fromDate)){
				List<GenericValue> listGl = delegator.findByAnd("GlAccountCategoryMember", UtilMisc.toMap("glAccountId", glAccountId), null, false);	
				GenericValue glAccountMember = delegator.findOne("GlAccountCategoryMember", false, UtilMisc.toMap("glAccountId", glAccountId,"glAccountCategoryId",glAccountCategoryId,"fromDate",fromDate));
					if(UtilValidate.isNotEmpty(glAccountMember)){
						glAccountMember.set("thruDate", val);
						glAccountMember.store();
						Map<String,Object> mapTmp = FastMap.newInstance();
						mapTmp = dpct.getDispatcher().runSync("createGlAccountCategoryMember", UtilMisc.toMap("userLogin",((GenericValue)context.get("userLogin")) , "glAccountId", glAccountId, "glAccountCategoryId", glAccountCategoryId, "fromDate", val, "thruDate", thruDate,"amountPercentage",percentage));
//						GenericValue glAccountMemberTmp = delegator.makeValue("GlAccountCategoryMember");
//						glAccountMemberTmp.set("glAccountId", glAccountId);
//						glAccountMemberTmp.set("glAccountCategoryId", glAccountCategoryId);
//						glAccountMemberTmp.set("fromDate", UtilDateTime.nowTimestamp());
//						glAccountMemberTmp.set("thruDate",(UtilValidate.isEmpty(thruDate) || thruDate == null) ? null : thruDate);
//						glAccountMemberTmp.set("amountPercentage", percentage);
//						glAccountMemberTmp.create();
					}	
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An error when update Gl Account Member" + e.getMessage());
		}
		return mapRs;
	}
	
}