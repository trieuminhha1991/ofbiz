package com.olbius.baselogistics.facility;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.LogisticsServices;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsStringUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.FacilityUtil;
import com.olbius.services.JqxWidgetSevices;

public class FacilityServices {
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsErrorUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getFacilities(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Locale locale = (Locale)context.get("locale");
    	Security security = ctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<Map<String,Object>> listReturnFacilities = FastList.newInstance();
    	// security check
        if (!FacilityUtil.hasAccessRoles(security, userLogin)){
        	successResult.put("listIterator", listReturnFacilities);
			return successResult;
        }
    	String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EntityCondition tmpOwnerConditon = EntityCondition.makeCondition("ownerPartyId", companyStr);
    	listAllConditions.add(tmpOwnerConditon);
    	
    	String facilityTypeId = "WAREHOUSE"; 
    	if (parameters.get("facilityTypeId") != null && parameters.get("facilityTypeId").length > 0) {
    		facilityTypeId = (String)parameters.get("facilityTypeId")[0];
    	};
    	if (UtilValidate.isNotEmpty(facilityTypeId)){
    		EntityCondition tmpFacTypeConditon = EntityCondition.makeCondition("facilityTypeId", facilityTypeId);
        	listAllConditions.add(tmpFacTypeConditon);
    	}
    	
    	String parentFacilityId = null;
    	if (parameters.get("parentFacilityId") != null && parameters.get("parentFacilityId").length > 0){
    		parentFacilityId = (String)parameters.get("parentFacilityId")[0];
    	}
    	if (UtilValidate.isNotEmpty(parentFacilityId)){
    		EntityCondition tmpFacTypeConditon = EntityCondition.makeCondition("parentFacilityId", parentFacilityId);
        	listAllConditions.add(tmpFacTypeConditon);
    	}
    	
    	if (UtilValidate.isNotEmpty(parentFacilityId)) {
    		// view detail specific facility 
    		Boolean hasRole = FacilityUtil.hasRoles(delegator, userLogin.getString("partyId"), UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"), parentFacilityId);
			if (hasRole){
	    		List<String> childIds = FacilityUtil.getAllFacilityRecursive(delegator, parentFacilityId);
				if (!childIds.isEmpty()){
					for (String facilityId : childIds) {
						GenericValue objFacility = null;
						try {
							objFacility = delegator.findOne("FacilityAll", false, UtilMisc.toMap("facilityId", facilityId));
						} catch (GenericEntityException e) {
							return ServiceUtil.returnError("OLBIUS: findOne Facility error! " + e.toString());
						}
						Map<String, Object> row = new HashMap<String, Object>();
						row = objFacility.getAllFields();
		    			List<GenericValue> childTmps = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("parentFacilityId", facilityId)), null, null, null, false);
		    			if (!childTmps.isEmpty()){
		            		List<String> childOfIds = FacilityUtil.getAllFacilityRecursive(delegator, facilityId);
		            		List<Map<String, Object>> listToResult = FastList.newInstance();
		        			for (String facilityChildId : childOfIds) {
		        				GenericValue facilityChild = null;
								try {
									facilityChild = delegator.findOne("FacilityAll", false, UtilMisc.toMap("facilityId", facilityChildId));
								} catch (GenericEntityException e) {
									return ServiceUtil.returnError("OLBIUS: findOne Facility error! " + e.toString());
								}
								if (UtilValidate.isNotEmpty(facilityChild)) {
									listToResult.add(facilityChild.getAllFields());
								}
		        			}
		        			row.put("rowDetail", listToResult);
		        		}
		    			listReturnFacilities.add(row);
		    		}
				}
			} else {
				return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceError, "BLNotHasFacilityRole", locale));
			}
		} else {
			List<String> listFacilityIds = new ArrayList<String>();
	    	try {
	    		listFacilityIds = FacilityUtil.getFacilityIdByRole(delegator, userLogin.getString("partyId"), UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
	    	} catch (GenericEntityException e){
	    		return ServiceUtil.returnError("OLBIUS: get list facility party error");
	    	}
	    	
	    	EntityCondition idCond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
	    	listAllConditions.add(idCond);
	    	List<GenericValue> listFacilities = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "FacilityAll", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		
			for (GenericValue facility : listFacilities) {
				String facilityId = facility.getString("facilityId");
    			List<GenericValue> childs = delegator.findList("FacilityAll", EntityCondition.makeCondition(UtilMisc.toMap("parentFacilityId", facilityId)), null, null, null, false);
    			Map<String, Object> row = new HashMap<String, Object>();
    			row = facility.getAllFields();
    			if (!childs.isEmpty()){
    				List<Map<String, Object>> listToResult = FastList.newInstance();
        			for (GenericValue facilityChild : childs) {
        				listToResult.add(facilityChild.getAllFields());
        			}
        			row.put("rowDetail", listToResult);
        		}
    			listReturnFacilities.add(row);
    		}
		}
    	successResult.put("listIterator", listReturnFacilities);
    	return successResult;
	}
	
	public static Map<String, Object> getGeoAssocs(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		String geoId = (String)context.get("geoId");
		String geoAssocTypeId = (String)context.get("geoAssocTypeId");
		String geoTypeId = (String)context.get("geoTypeId");
		List<GenericValue> listGeos = new ArrayList<GenericValue>();
 		try {
			List<GenericValue> listGeoAssocs = delegator.findList("GeoAssoc", EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", geoAssocTypeId)), null, null, null, false);
			if (!listGeoAssocs.isEmpty()){
				for (GenericValue item : listGeoAssocs){
					GenericValue geo = delegator.findOne("Geo", false, UtilMisc.toMap("geoId", item.getString("geoIdTo")));
					if (geo != null && geoTypeId.equals(geo.getString("geoTypeId"))){
						listGeos.add(geo);
					}
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("get GeoAssoc error!" + e.toString());
		}
				
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listGeos", listGeos);
    	return successResult;
	}
	
	public static Map<String, Object> getFacilityOwnerables(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");
		tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, companyStr));
		tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "RENT_FACILITY"));
		if(!"null".equals(strKeySearch) && !"".equals(strKeySearch) && strKeySearch != null){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("PartyRelationShipWithPartyGroup", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			GenericValue company = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId", companyStr));
			listData.add(company);
			// Check role OWNER of party
			if (!listData.isEmpty()){
				List<GenericValue> listNotHasOwnerRoles = new ArrayList<GenericValue>();
				for (GenericValue party : listData){
					List<GenericValue> listPartyRoles = delegator.findList("PartyRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", party.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.owner"))), null, null, null, false);
					if (listPartyRoles.isEmpty()){
						listNotHasOwnerRoles.add(party);
					}
				}
				listData.removeAll(listNotHasOwnerRoles);
			}
			successResult.put("listParties", listData);
			 
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling facilityOwnerableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	
	public static Map<String, Object> getFacilityManagerables(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");
		
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listDepartments = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, "LOG_DEPARTMENT", delegator);
		
		tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
		tmpListCond.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
		tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDepartments));
		
		if(strKeySearch != null && !"".equals(strKeySearch)){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstNameTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleNameTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastNameTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstMiddleTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstLastTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleLastTo"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("PartyRelationShipWithPerson", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			listData = EntityUtil.filterByDate(listData);
			if (!listData.isEmpty()){
				List<GenericValue> listNotHasStoreKeeperRoles = new ArrayList<GenericValue>();
				for (GenericValue party : listData){
					tmpListCond = new ArrayList<EntityCondition>();
					tmpListCond.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper")));
					tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
					tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, party.getString("partyIdTo")));
					tmpListCond.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listDepartments));
					List<GenericValue> listPartyRoles = delegator.findList("PartyRelationship", EntityCondition.makeCondition(tmpListCond, EntityOperator.AND), null, null, null, false);
					listPartyRoles = EntityUtil.filterByDate(listPartyRoles);
					if (listPartyRoles.isEmpty()){
						listNotHasStoreKeeperRoles.add(party);
					}
				}
				listData.removeAll(listNotHasStoreKeeperRoles);
			}
			successResult.put("listParties", listData);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling facilityManagerableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateFacility(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String facilityId = (String) context.get("facilityId");
		GenericValue facility = null;
		Boolean update = false;
		String facilityCode = (String) context.get("facilityCode");
		List<GenericValue> listFacilityCode = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("facilityCode", EntityOperator.EQUALS, facilityCode));
		EntityCondition cond2 = EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		if (UtilValidate.isNotEmpty(facilityId)) {
			listFacilityCode = delegator.findList("Facility", EntityCondition.makeCondition(cond1, cond2), null, null, null, false);
			update = true;
			try {
				facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("getFacilityById error!" + e.toString());
			}
			if (facility == null) {
				//check facility code
				if (UtilValidate.isNotEmpty(listFacilityCode)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLNotifyFacilityCodeExists", locale));
				}
				
				facility = delegator.makeValue("Facility");
				facility.set("facilityId", facilityId);
			} else {
				if (!facilityCode.equals(facility.getString("facilityCode")) && UtilValidate.isNotEmpty(listFacilityCode)) {
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLNotifyFacilityCodeExists", locale));
				}
			}
		} else {
			listFacilityCode = delegator.findList("Facility", EntityCondition.makeCondition(cond1), null, null, null, false);
			//check facility code
			if (UtilValidate.isNotEmpty(listFacilityCode)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLNotifyFacilityCodeExists", locale));
			}
			
			facility = delegator.makeValue("Facility");
			facilityId = delegator.getNextSeqId("Facility");
			String preFix = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.prefix.facilityId");
			if (UtilValidate.isNotEmpty(preFix)) {
				facilityId = preFix + facilityId;
			}
			facility.set("facilityId", facilityId);
		}
		delegator.createOrStore(facility);
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		Timestamp fromDateManage = null;
		Timestamp thruDateManage = null;
		Timestamp openedDate = null;
		String requireLocation = "N";
		if (UtilValidate.isNotEmpty((String)context.get("requireLocation"))) {
			requireLocation = (String)context.get("requireLocation");
		}
		String requireDate = "N";
		if (UtilValidate.isNotEmpty((String)context.get("requireDate"))) {
			requireDate = (String)context.get("requireDate");
		}
		if ((String)context.get("fromDate") != null){
			Long fromDateLong = Long.valueOf((String)context.get("fromDate"));
			fromDate = new Timestamp(fromDateLong);
		}
		if ((String)context.get("thruDate") != null){
			Long thruDateLong = Long.valueOf((String)context.get("thruDate"));
			thruDate = new Timestamp(thruDateLong);
		}
		if ((String)context.get("fromDateManager") != null){
			Long fromDateManageLong = Long.valueOf((String)context.get("fromDateManager"));
			fromDateManage = new Timestamp(fromDateManageLong);
		}
		if ((String)context.get("thruDateManager") != null){
			Long thruDateManageLong = Long.valueOf((String)context.get("thruDateManager"));
			thruDateManage = new Timestamp(thruDateManageLong);
		}
		if ((String)context.get("openedDate") != null){
			Long openedDateLong = Long.valueOf((String)context.get("openedDate"));
			openedDate = new Timestamp(openedDateLong);
		}
		
		String primaryFacilityGroupId = null;
		if (UtilValidate.isNotEmpty(context.get("primaryFacilityGroupId"))) {
			primaryFacilityGroupId = (String)context.get("primaryFacilityGroupId");
		} else {
			primaryFacilityGroupId = "FACILITY_INTERNAL";
		}
		String facilityName = (String)context.get("facilityName");

		facility.set("requireLocation", requireLocation);
		facility.set("requireDate", requireDate);
		facility.set("facilityName", facilityName); 
		facility.set("facilityCode", facilityCode); 
		facility.set("facilityTypeId", context.get("facilityTypeId"));
		facility.set("primaryFacilityGroupId", primaryFacilityGroupId);
		facility.set("facilitySize", context.get("facilitySize"));
		facility.set("facilitySizeUomId", context.get("facilitySizeUomId"));
		facility.set("defaultInventoryItemTypeId", "NON_SERIAL_INV_ITEM");
		List<Object> listProductStoreId = (List<Object>)context.get("listProductStoreId");
		
		Boolean isJson = false;
    	if (!listProductStoreId.isEmpty()){
    		if (listProductStoreId.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	List<Map<String, Object>> listStores = new ArrayList<Map<String, Object>>();
		if (isJson){
			String stringJson = "["+(String)listProductStoreId.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				JSONObject item = lists.getJSONObject(i);
				Map<String, Object> map = FastMap.newInstance();
				if (item.containsKey("productStoreId")){
					if (item.getString("productStoreId") != null && !"".equals(item.getString("productStoreId"))){
						map.put("productStoreId", item.getString("productStoreId"));
						listStores.add(map);
					}
				}
			}
		} else {
			listStores = (List<Map<String, Object>>)context.get("listProductStoreId");
		}
		
		List<String> listStoreIds = new ArrayList<String>();
		if (!listStores.isEmpty()){
			for (Map<String, Object> map : listStores){
				listStoreIds.add((String)map.get("productStoreId"));
			}
		}
		if (facility.getString("productStoreId") != null){
			if (!listStoreIds.contains(facility.getString("productStoreId"))){
				facility.set("productStoreId", listStoreIds.get(0));
			}
		} else {
			facility.set("productStoreId", listStoreIds.get(0));
		}
		
		List<GenericValue> listStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		listStoreFacility = EntityUtil.filterByDate(listStoreFacility);
		if (listStoreFacility.isEmpty()){
			for (String storeId : listStoreIds){
				GenericValue newStoreFacility = delegator.makeValue("ProductStoreFacility");
				newStoreFacility.put("fromDate", UtilDateTime.nowTimestamp());
				newStoreFacility.put("productStoreId", storeId);
				newStoreFacility.put("facilityId", facilityId);
				delegator.create(newStoreFacility);
			}
		} else {
			for (GenericValue item : listStoreFacility){
				if (!listStoreIds.contains(item.getString("productStoreId"))){
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
			for (String storeId : listStoreIds){
				Boolean existed = false;
				for (GenericValue storeFac : listStoreFacility){
					if (storeId.equals(storeFac.get("productStoreId"))){
						existed = true;
						break;
					}
				}
				if (!existed){
					GenericValue newStoreFacility = delegator.makeValue("ProductStoreFacility");
					newStoreFacility.put("fromDate", UtilDateTime.nowTimestamp());
					newStoreFacility.put("productStoreId", storeId);
					newStoreFacility.put("facilityId", facilityId);
					delegator.create(newStoreFacility);
				}
			}
		}
		if (context.get("openedDate") != null){
			facility.set("openedDate", openedDate);
		} else {
			facility.set("openedDate", UtilDateTime.nowTimestamp());
		}
		facility.set("description", context.get("description"));
		
		if (context.get("imagesPath") != null){
			GenericValue facAttr = delegator.makeValue("FacilityAttribute");
			facAttr.put("facilityId", facilityId);
			facAttr.put("attrName", "imagesPath");
			facAttr.put("attrValue", context.get("imagesPath"));
			delegator.createOrStore(facAttr);
		}else {
			GenericValue facilityAttribute; 
			try {
				facilityAttribute = delegator.findOne("FacilityAttribute", false, UtilMisc.toMap("facilityId", facilityId, "attrName", "imagesPath"));
				if (facilityAttribute != null){			
					// Delete jcr file
					String path = facilityAttribute.getString("attrValue");
					path = path.substring(27, path.length());
					dispatcher.runSync("jackrabbitDeleteNode", UtilMisc.toMap("curPath", path, "public", "Y", "userLogin", userLogin, "locale", locale));
					facilityAttribute.remove();
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("operate on FacilityAttribute!" + e.toString());
			}catch (GenericServiceException e) {
				return ServiceUtil.returnError("jackrabbitDeleteNode service error!" + e.toString());
			}
		}	
		
		if (!"".equals((String)context.get("parentFacilityId")) && context.get("parentFacilityId") != null){
			facility.set("parentFacilityId", context.get("parentFacilityId"));
		}
		String strFacilityId = null;
		try {
			strFacilityId = facility.getString("facilityId");
			// create Facility
			delegator.createOrStore(facility);
			if(UtilValidate.isNotEmpty(context.get("ownerPartyId"))){
				List<GenericValue> listFacParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", (String)context.get("ownerPartyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.owner"))), null, null, null, false);
				listFacParty = EntityUtil.filterByDate(listFacParty);
				if (listFacParty.isEmpty()){
					
					List<GenericValue> listOldFacParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.owner"))), null, null, null, false);
					if (!listOldFacParty.isEmpty()){
						for (GenericValue old : listOldFacParty){
							old.put("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(old);
						}
					}
					
					facility.set("ownerPartyId", context.get("ownerPartyId"));
					facility.store();
					GenericValue facilityParty = delegator.makeValue("FacilityParty");
					facilityParty.set("facilityId", strFacilityId);
					facilityParty.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.owner"));
					facilityParty.set("fromDate", fromDate);
					facilityParty.set("thruDate", thruDate);
					facilityParty.set("partyId", context.get("ownerPartyId"));
					delegator.create(facilityParty);
				} else {
					for (GenericValue item : listFacParty){
						item.put("thruDate", thruDate);
						delegator.store(item);
					}
				}
			}
			// Create relationship with Logistics Admin (default)
			List<String> listLogAdmins =  SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.admin"), delegator);
			if (!listLogAdmins.isEmpty()){
				for (String partyId : listLogAdmins){
					List<GenericValue> listFacParty = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.admin"))), null, null, null, false);
					listFacParty = EntityUtil.filterByDate(listFacParty);
					if (listFacParty.isEmpty()){
						GenericValue facilityParty1 = delegator.makeValue("FacilityParty");
						facilityParty1.set("facilityId", strFacilityId);
						facilityParty1.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
						facilityParty1.set("fromDate", UtilDateTime.nowTimestamp());
						facilityParty1.set("partyId", partyId);
						delegator.createOrStore(facilityParty1);
						
						GenericValue facilityParty2 = delegator.makeValue("FacilityParty");
						facilityParty2.set("facilityId", strFacilityId);
						facilityParty2.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.admin"));
						facilityParty2.set("fromDate", UtilDateTime.nowTimestamp());
						facilityParty2.set("partyId", partyId);
						delegator.createOrStore(facilityParty2);
					}
				}
			}
			if(UtilValidate.isNotEmpty(context.get("managerPartyId"))){
				String manager = (String)context.get("managerPartyId");
				List<GenericValue> listFacPartyTmp = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", manager, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"))), null, null, null, false);
				listFacPartyTmp = EntityUtil.filterByDate(listFacPartyTmp);
				List<GenericValue> listStorekeeper = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", manager, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
				listStorekeeper = EntityUtil.filterByDate(listStorekeeper);
				if (!listFacPartyTmp.isEmpty() && !listStorekeeper.isEmpty()){
					for (GenericValue item1 : listFacPartyTmp){
						if (UtilValidate.isNotEmpty(fromDateManage) && !item1.getTimestamp("fromDate").equals(fromDateManage)){
							item1.set("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(item1);
							GenericValue facilityParty1 = delegator.makeValue("FacilityParty");
							facilityParty1.set("facilityId", facilityId);
							facilityParty1.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
							facilityParty1.set("fromDate",  fromDateManage);
							if (UtilValidate.isNotEmpty(thruDateManage)){
								facilityParty1.set("thruDate", thruDateManage);
							}
							facilityParty1.set("partyId", (String)context.get("managerPartyId"));
							delegator.createOrStore(facilityParty1);
						} else {
							if (UtilValidate.isNotEmpty(thruDateManage)){
								item1.set("thruDate", thruDateManage);
							}
							delegator.store(item1);
						}
					}
					for (GenericValue item2 : listStorekeeper){
						if (UtilValidate.isNotEmpty(fromDateManage) && !item2.getTimestamp("fromDate").equals(fromDateManage)){
							item2.set("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(item2);
							GenericValue facilityParty2 = delegator.makeValue("FacilityParty");
							facilityParty2.set("facilityId", facilityId);
							facilityParty2.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
							facilityParty2.set("fromDate",  fromDateManage);
							facilityParty2.set("thruDate",  thruDateManage);
							facilityParty2.set("partyId", (String)context.get("managerPartyId"));
							delegator.createOrStore(facilityParty2);
							if (UtilValidate.isNotEmpty(thruDateManage)){
								facilityParty2.set("thruDate", thruDateManage);
							}
							delegator.createOrStore(facilityParty2);
						} else {
							if (UtilValidate.isNotEmpty(thruDateManage)){
								item2.set("thruDate", thruDateManage);
							}
							delegator.store(item2);
						}
					}
				} else {
					GenericValue facilityParty1 = delegator.makeValue("FacilityParty");
					facilityParty1.set("facilityId", strFacilityId);
					facilityParty1.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
					facilityParty1.set("fromDate",  fromDateManage);
					facilityParty1.set("thruDate", thruDateManage);
					facilityParty1.set("partyId", (String)context.get("managerPartyId"));
					delegator.createOrStore(facilityParty1);
					
					GenericValue facilityParty2 = delegator.makeValue("FacilityParty");
					facilityParty2.set("facilityId", strFacilityId);
					facilityParty2.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
					facilityParty2.set("fromDate",  fromDateManage);
					facilityParty2.set("thruDate",  thruDateManage);
					facilityParty2.set("partyId", (String)context.get("managerPartyId"));
					delegator.createOrStore(facilityParty2);
				}
			}
			if (UtilValidate.isNotEmpty((context.get("listStoreKeepers")))) {
				List<String> listStorekeeperIds = new ArrayList<String>();
				List<Map<String, Object>> listTmps;
				try {
					listTmps = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", (String)context.get("listStoreKeepers"));
					if (!listTmps.isEmpty()){
						for (Map<String, Object> map : listTmps) {
							if (!listStorekeeperIds.contains((String)map.get("partyId"))){
								listStorekeeperIds.add((String)map.get("partyId"));
							}
						}
					}
					if (!listStorekeeperIds.isEmpty()) {
						for (String partyId : listStorekeeperIds) {
							List<GenericValue> listFacPartyTmp = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"))), null, null, null, false);
							listFacPartyTmp = EntityUtil.filterByDate(listFacPartyTmp);
							List<GenericValue> listStorekeeper = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", partyId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
							listStorekeeper = EntityUtil.filterByDate(listStorekeeper);
							if (!listFacPartyTmp.isEmpty() && !listStorekeeper.isEmpty()){
								for (GenericValue item1 : listFacPartyTmp){
									if (UtilValidate.isNotEmpty(fromDateManage) && !item1.getTimestamp("fromDate").equals(fromDateManage)){
										item1.set("thruDate", UtilDateTime.nowTimestamp());
										delegator.store(item1);
										GenericValue facilityParty1 = delegator.makeValue("FacilityParty");
										facilityParty1.set("facilityId", facilityId);
										facilityParty1.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
										facilityParty1.set("fromDate",  fromDateManage);
										if (UtilValidate.isNotEmpty(thruDateManage)){
											facilityParty1.set("thruDate", thruDateManage);
										}
										facilityParty1.set("partyId", partyId);
										delegator.createOrStore(facilityParty1);
									} else {
										if (UtilValidate.isNotEmpty(thruDateManage)){
											item1.set("thruDate", thruDateManage);
										}
										delegator.store(item1);
									}
								}
								for (GenericValue item2 : listStorekeeper){
									if (UtilValidate.isNotEmpty(fromDateManage) && !item2.getTimestamp("fromDate").equals(fromDateManage)){
										item2.set("thruDate", UtilDateTime.nowTimestamp());
										delegator.store(item2);
										GenericValue facilityParty2 = delegator.makeValue("FacilityParty");
										facilityParty2.set("facilityId", facilityId);
										facilityParty2.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
										facilityParty2.set("fromDate",  fromDateManage);
										facilityParty2.set("thruDate",  thruDateManage);
										facilityParty2.set("partyId", partyId);
										delegator.createOrStore(facilityParty2);
										if (UtilValidate.isNotEmpty(thruDateManage)){
											facilityParty2.set("thruDate", thruDateManage);
										}
										delegator.createOrStore(facilityParty2);
									} else {
										if (UtilValidate.isNotEmpty(thruDateManage)){
											item2.set("thruDate", thruDateManage);
										}
										delegator.store(item2);
									}
								}
							} else {
								GenericValue facilityParty1 = delegator.makeValue("FacilityParty");
								facilityParty1.set("facilityId", strFacilityId);
								facilityParty1.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"));
								facilityParty1.set("fromDate",  fromDateManage);
								facilityParty1.set("thruDate", thruDateManage);
								facilityParty1.set("partyId", partyId);
								delegator.createOrStore(facilityParty1);
								
								GenericValue facilityParty2 = delegator.makeValue("FacilityParty");
								facilityParty2.set("facilityId", strFacilityId);
								facilityParty2.set("roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
								facilityParty2.set("fromDate",  fromDateManage);
								facilityParty2.set("thruDate",  thruDateManage);
								facilityParty2.set("partyId", partyId);
								delegator.createOrStore(facilityParty2);
							}
						}
						if (update){
							EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, listStorekeeperIds);
							EntityCondition roleCond = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"));
							EntityCondition faCond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
							List<GenericValue> listOlds = delegator.findList("FacilityParty", EntityCondition.makeCondition(partyCond, roleCond, faCond), null,
									null, null, false);
							listOlds = EntityUtil.filterByDate(listOlds);
							if (!listOlds.isEmpty()){
								for (GenericValue pt : listOlds) {
									String partyId = pt.getString("partyId");
									String roleTypeId = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager");
									EntityCondition roleCond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId);
									EntityCondition partyCond2 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
									List<GenericValue> listMng = delegator.findList("FacilityParty",
											EntityCondition.makeCondition(faCond, partyCond2, roleCond2), null, null, null, false);
									listMng = EntityUtil.filterByDate(listMng);
									if (!listMng.isEmpty()){
										for (GenericValue it : listMng) {
											it.set("thruDate", UtilDateTime.nowTimestamp());
											delegator.store(it);
										}
									}
									pt.set("thruDate", UtilDateTime.nowTimestamp());
									delegator.store(pt);
								}
							}
						}
					}
				} catch (ParseException e) {
					return ServiceUtil.returnError("OLBIUS: convert list json to list object error! " + e.toString());
				}
			}
			String address = (String)context.get("address");
			String phoneNumber = (String)context.get("phoneNumber");

			if (UtilValidate.isNotEmpty(address)){
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", delegator.getNextSeqId("ContactMech"));
				contactMech.put("contactMechTypeId", "POSTAL_ADDRESS");
				delegator.create(contactMech);
				
				List<GenericValue> listCurrentContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				if (!listCurrentContactMech.isEmpty()){
					for (GenericValue contact : listCurrentContactMech){
						GenericValue oldContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contact.getString("contactMechId")));
						if ("POSTAL_ADDRESS".equals(oldContactMech.getString("contactMechTypeId"))){
							contact.put("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(contact);
						}
					}
				}
				
				List<GenericValue> listCurrentContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				listCurrentContactMechPurpose = EntityUtil.filterByDate(listCurrentContactMechPurpose);
				if (!listCurrentContactMechPurpose.isEmpty()){
					for (GenericValue contact : listCurrentContactMechPurpose){
						if ("SHIPPING_LOCATION".equals(contact.getString("contactMechPurposeTypeId")) || "SHIP_ORIG_LOCATION".equals(contact.getString("contactMechPurposeTypeId")) || "PRIMARY_LOCATION".equals(contact.getString("contactMechPurposeTypeId"))){
							contact.put("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(contact);
						}
					}
				}
				
				GenericValue facContactMech = delegator.makeValue("FacilityContactMech");
				facContactMech.put("facilityId", strFacilityId);
				facContactMech.put("contactMechId", contactMech.get("contactMechId"));
				facContactMech.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMech);
				
				GenericValue facContactMechPurpose01 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose01.put("facilityId", strFacilityId);
				facContactMechPurpose01.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose01.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
				facContactMechPurpose01.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose01);
				
				GenericValue facContactMechPurpose02 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose02.put("facilityId", strFacilityId);
				facContactMechPurpose02.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose02.put("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
				facContactMechPurpose02.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose02);
				
				GenericValue facContactMechPurpose03 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose03.put("facilityId", strFacilityId);
				facContactMechPurpose03.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose03.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				facContactMechPurpose03.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose03);
				
				GenericValue postalAddress = delegator.makeValue("PostalAddress");
				postalAddress.put("contactMechId", contactMech.get("contactMechId"));
				postalAddress.put("address1", address);
				postalAddress.put("toName", facilityName);
				postalAddress.put("countryGeoId", context.get("countryGeoId"));
				postalAddress.put("stateProvinceGeoId", context.get("provinceGeoId"));
				postalAddress.put("districtGeoId", context.get("districtGeoId"));
				postalAddress.put("wardGeoId", context.get("wardGeoId"));
				delegator.create(postalAddress);
			}
			
			if (UtilValidate.isNotEmpty(phoneNumber)){
				
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", delegator.getNextSeqId("ContactMech"));
				contactMech.put("contactMechTypeId", "TELECOM_NUMBER");
				delegator.create(contactMech);
				
				List<GenericValue> listCurrentContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				listCurrentContactMech = EntityUtil.filterByDate(listCurrentContactMech);
				if (!listCurrentContactMech.isEmpty()){
					for (GenericValue contact : listCurrentContactMech){
						GenericValue oldContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contact.getString("contactMechId")));
						if ("TELECOM_NUMBER".equals(oldContactMech.getString("contactMechTypeId"))){
							contact.put("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(contact);
						}
					}
				}
				
				List<GenericValue> listCurrentContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				listCurrentContactMechPurpose = EntityUtil.filterByDate(listCurrentContactMechPurpose);
				if (!listCurrentContactMechPurpose.isEmpty()){
					for (GenericValue contact : listCurrentContactMechPurpose){
						if ("PRIMARY_PHONE".equals(contact.getString("contactMechPurposeTypeId"))){
							contact.put("thruDate", UtilDateTime.nowTimestamp());
							delegator.store(contact);
						}
					}
				}
				
				GenericValue facContactMech = delegator.makeValue("FacilityContactMech");
				facContactMech.put("facilityId", strFacilityId);
				facContactMech.put("contactMechId", contactMech.get("contactMechId"));
				facContactMech.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMech);
				
				GenericValue facContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose.put("facilityId", strFacilityId);
				facContactMechPurpose.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				facContactMechPurpose.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose);
				
				GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
				telecomNumber.put("contactMechId", contactMech.get("contactMechId"));
				telecomNumber.put("contactNumber", phoneNumber);
				delegator.create(telecomNumber);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createFacility service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("facilityId", strFacilityId);
		return successResult;
	}
	
	public static Map<String, Object> getFacilityDetail(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		String countryGeoId = null;
		String provinceGeoId = null;
		String districtGeoId = null;
		String wardGeoId = null;
		String address = null;
		String address1 = null;
		String geoPointId=null;
        String postalAddressId=null;
		String phoneNumber = null;
		Timestamp ownFromDate = null;
		Timestamp ownThruDate = null;
		Timestamp manageFromDate = null;
		Timestamp manageThruDate = null;
		String managerName = "";
		String managerPartyId = null;
		String ownerName = "";
		GenericValue facilityDetail = null;
		List<GenericValue> listFacilityParty = FastList.newInstance();
		try {
			facilityDetail = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			List<GenericValue> facilityContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "PRIMARY_LOCATION");
			if (facilityContactMechs.isEmpty()){

				EntityCondition shipOrgLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
				EntityCondition shippingLocation = EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION");
				List<EntityCondition> listFacCond = UtilMisc.toList(shipOrgLocation, shippingLocation); 
				EntityCondition facCond = EntityCondition.makeCondition(listFacCond, EntityOperator.OR);
				facilityContactMechs = delegator.findList("FacilityContactMechPurpose", facCond, null, null, null, false);
			}
			if (!facilityContactMechs.isEmpty()){
				GenericValue contactMech = facilityContactMechs.get(0);
				GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")));
				countryGeoId = postalAddress.getString("countryGeoId");
				districtGeoId = postalAddress.getString("districtGeoId");
				provinceGeoId = postalAddress.getString("stateProvinceGeoId");
				wardGeoId = postalAddress.getString("wardGeoId");
				geoPointId=postalAddress.getString("geoPointId");
				address = postalAddress.getString("fullName");
				address1 = postalAddress.getString("address1");
                 postalAddressId=contactMech.getString("contactMechId");
				
			}
			List<String> facilityPhone = LogisticsFacilityUtil.getFacilityPrimaryPhone(delegator, facilityId, "PRIMARY_PHONE");
			if (!facilityPhone.isEmpty()){
				phoneNumber = facilityPhone.get(0);
			}
			GenericValue owner = delegator.findOne("PartyNameView", false, UtilMisc.toMap("partyId", facilityDetail.getString("ownerPartyId")));
			if (owner != null){
				ownerName = owner.getString("groupName");
				if ("".equals(ownerName)){
					ownerName = owner.getString("partyId");
				}
			}
			List<GenericValue> listOwners = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", facilityDetail.getString("ownerPartyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.owner"))), null, null, null, false);
			listOwners = EntityUtil.filterByDate(listOwners);
			GenericValue facilityPartyOwner = null;
			if (!listOwners.isEmpty()){
				facilityPartyOwner = listOwners.get(0);
			}
			if (facilityPartyOwner != null){
				ownFromDate = facilityPartyOwner.getTimestamp("fromDate");
				ownThruDate = facilityPartyOwner.getTimestamp("thruDate");
			}
			List<GenericValue> listManagers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager"))), null, null, null, false);
			listManagers = EntityUtil.filterByDate(listManagers);
			GenericValue facilityPartyManager = null;
			if (!listManagers.isEmpty()){
				for (GenericValue manager : listManagers){
					List<GenericValue> listStorekeepers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "partyId", manager.getString("partyId"), "roleTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper"))), null, null, null, false);
					listStorekeepers = EntityUtil.filterByDate(listStorekeepers);
					if (!listStorekeepers.isEmpty()){
						facilityPartyManager = listStorekeepers.get(0);
					}
				}
			}
			if (facilityPartyManager != null){
				GenericValue manager = delegator.findOne("PartyFullNameDetail", false, UtilMisc.toMap("partyId", facilityPartyManager.getString("partyId")));
				managerPartyId = facilityPartyManager.getString("partyId");
				manageFromDate = facilityPartyManager.getTimestamp("fromDate");
				manageThruDate = facilityPartyManager.getTimestamp("thruDate");
				if (manager != null){
					if (UtilValidate.isNotEmpty(manager.getString("partyCode"))){
						managerName = manager.getString("fullName") + " [" + manager.getString("partyCode") + "]";
					} else {
						managerName = manager.getString("fullName") + " [" + manager.getString("partyId") + "]";
					}
				}
			}
			
			listFacilityParty = delegator.findList("FacilityPartyDetail", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, null, null, false);
			listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
		} catch (GenericEntityException e) {
			return  ServiceUtil.returnError("getFacilityDetail error!" + e.toString());
		}
		
		List<String> listProductStoreId = new ArrayList<String>();
		List<GenericValue> listStoreFac = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		listStoreFac = EntityUtil.filterByDate(listStoreFac);
		if (!listStoreFac.isEmpty()){
			for (GenericValue storeFac : listStoreFac){
				listProductStoreId.add(storeFac.getString("productStoreId"));
			}
		}
		if (facilityDetail.getString("productStoreId") != null && !listProductStoreId.contains(facilityDetail.getString("productStoreId"))){
			listProductStoreId.add(facilityDetail.getString("productStoreId"));
		}
		String imagesPath = null;
		List<GenericValue> listAttrFac = delegator.findList("FacilityAttribute", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "attrName", "imagesPath")), null, null, null, false);
		if (!listAttrFac.isEmpty()){
			imagesPath = listAttrFac.get(0).getString("attrValue");
		}
		GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", geoPointId), false);
        String faLat=null;
        String faLng=null;
		if(UtilValidate.isNotEmpty(geoPoint)) {
            faLat = geoPoint.getString("latitude");
            faLng = geoPoint.getString("longitude");
        }
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		successResult.put("facilityId", facilityId);
		successResult.put("facilityTypeId", facilityDetail.getString("facilityTypeId"));
		successResult.put("facilityCode", facilityDetail.getString("facilityCode"));
		successResult.put("parentFacilityId", facilityDetail.getString("parentFacilityId"));
		successResult.put("facilitySizeUomId", facilityDetail.getString("facilitySizeUomId"));
		successResult.put("facilitySize", facilityDetail.getBigDecimal("facilitySize"));
		successResult.put("facilityName", facilityDetail.getString("facilityName"));
		successResult.put("requireLocation", facilityDetail.get("requireLocation"));
		successResult.put("requireDate", facilityDetail.get("requireDate"));
		successResult.put("managerName", managerName);
		successResult.put("ownerName", ownerName);
		successResult.put("countryGeoId", countryGeoId);
		successResult.put("districtGeoId", districtGeoId);
		successResult.put("provinceGeoId", provinceGeoId);
		successResult.put("wardGeoId", wardGeoId);
		successResult.put("listProductStoreId", listProductStoreId);
		successResult.put("address", address);
		successResult.put("address1", address1);
		successResult.put("geoPointId", geoPointId);
		successResult.put("faLat", faLat);
		successResult.put("faLng", faLng);
        successResult.put("postalAddressId", postalAddressId);
		successResult.put("phoneNumber", phoneNumber);
		successResult.put("openedDate", facilityDetail.getTimestamp("openedDate"));
		successResult.put("ownFromDate", ownFromDate);
		successResult.put("ownThruDate", ownThruDate);
		successResult.put("manageFromDate", manageFromDate);
		successResult.put("manageThruDate", manageThruDate);
		successResult.put("managerPartyId", managerPartyId);
		successResult.put("ownerPartyId", facilityDetail.getString("ownerPartyId"));
		successResult.put("description", facilityDetail.getString("description"));
		successResult.put("imagesPath", imagesPath);
		successResult.put("listFacilityParty", listFacilityParty);
		
		return successResult;
	}
	
	public static Map<String, Object> getFacilityContactMechs(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String)context.get("facilityId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		
		Debug.log(module + "::getFacilityContactMechs, facilityId = " + facilityId + ", contactMechPurposeTypeId = " + contactMechPurposeTypeId);
		
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(facilityId) && UtilValidate.isNotEmpty(contactMechPurposeTypeId)){
			EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
			EntityCondition condPP = EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId);
			EntityCondition condExp = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr());
			List<EntityCondition> condCtms = FastList.newInstance();
			condCtms.add(condFa);
			condCtms.add(condExp);
			
			List<GenericValue> listFacilityContactMechs = FastList.newInstance();
			try {
				listFacilityContactMechs = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(condCtms), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e.toString(), module);
				return ServiceUtil.returnError("OLBIUS: findList FacilityContactMech error!");
			}
			if (!listFacilityContactMechs.isEmpty()){
				List<String> listContactMechIds = FastList.newInstance();
				listContactMechIds = EntityUtil.getFieldListFromEntityList(listFacilityContactMechs, "contactMechId", true);
				if (!listContactMechIds.isEmpty()){
					EntityCondition condCtm = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, listContactMechIds);
					List<EntityCondition> condPPs = FastList.newInstance();
					condPPs.add(condFa);
					condPPs.add(condExp);
					condPPs.add(condPP);
					condPPs.add(condCtm);
					List<GenericValue> listFacilityContactMechPurpose = FastList.newInstance();
					Set<String> set_contact_mech = new HashSet<String>();
					try {
						listFacilityContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(condPPs), null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
						return ServiceUtil.returnError(e.getMessage());
					}
					if (!listFacilityContactMechPurpose.isEmpty()){
						for(GenericValue gv: listFacilityContactMechPurpose){
							set_contact_mech.add((String)gv.get("contactMechId"));
						}
					}
					for(String ctm: set_contact_mech){
						Debug.log(module + "::getFacilityContactMechs, for contact_mech_id " + ctm);
						List<GenericValue> listPostalAddress = FastList.newInstance();
						try {
							listPostalAddress = delegator.findList("PostalAddressDetail", 
									EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", ctm)), null, null, null, false);
						} catch (GenericEntityException e) {
							Debug.logError(e.toString(), module);
							return ServiceUtil.returnError("OLBIUS: findList PostalAddressDetail error!");
						}
						if (!listPostalAddress.isEmpty()){
							for(GenericValue pa: listPostalAddress){
								listContactMechs.add(pa);
								Debug.log(module + "::getFacilityContactMechs, got address " + pa.get("fullName") + ",  contact_mech " + pa.get("contactMechId"));
							}
						}
					}
				}
			}
		}
		result.put("facilityId", facilityId);
		result.put("listFacilityContactMechs", listContactMechs);
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getFacilityContactMech(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("contactMechTypeId");
		/*List<String> orderBy = new ArrayList<String>();
		orderBy.add("contactMechTypeId");*/
		
    	String facilityId = parameters.get("facilityId")[0];
    	List<GenericValue> listContactMechInFacility = new ArrayList<GenericValue>();
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
    		opts.setDistinct(true);
    		listContactMechInFacility = delegator.findList("ContactMechInFacility", cond, fieldToSelects, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listContactMechInFacility)){
    			for(GenericValue contactMechType : listContactMechInFacility){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String contactMechTypeId = (String)contactMechType.get("contactMechTypeId");
    				row.put("contactMechTypeId", contactMechTypeId);
    				EntityFindOptions findOptions = new EntityFindOptions();
    				findOptions.setDistinct(true);
    				Set<String> fields = UtilMisc.toSet("contactMechId", "contactMechPurposeTypeId", "countryGeoId", "address1", "stateProvinceGeoId", "countryCode");
    				fields.add("areaCode");
    				fields.add("contactNumber");
    				fields.add("infoString");
    				List<GenericValue> listDetail = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechTypeId", contactMechTypeId, "facilityId", facilityId)), fields, null, findOptions, false);
    				List<Map<String, String>> rowDetail = new ArrayList<Map<String,String>>();
    			
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
							Map<String, String> childDetail = new HashMap<String, String>();
							String contactMechId = (String) detail.get("contactMechId");
							String contactMechPurposeTypeId = (String) detail.get("contactMechPurposeTypeId");
        					String descriptionContactMechPurpuseType = (String) detail.get("descriptionContactMechPurpuseType");
        					String countryCode = (String) detail.get("countryCode");
        					String areaCode = (String) detail.get("areaCode");
        					String contactNumber = (String) detail.get("contactNumber");
        					String infoString = (String) detail.get("infoString");
        					String address1 = (String) detail.get("address1");
        					String toName = (String) detail.get("toName");
        					String attnName = (String) detail.get("attnName");
        					String address2 = (String) detail.get("address2");
        					String countryGeoId = (String) detail.get("countryGeoId");
        					String stateProvinceGeoId = (String) detail.get("stateProvinceGeoId");
        					String postalCode = (String) detail.get("postalCode");
        					String extension = (String) detail.get("extension");
        					childDetail.put("contactMechId", contactMechId);
        					childDetail.put("contactMechPurpuseTypeId", contactMechPurposeTypeId);
        					childDetail.put("descriptionContactMechPurpuseType", descriptionContactMechPurpuseType);
        					childDetail.put("countryCode", countryCode);
        					childDetail.put("areaCode", areaCode);
        					childDetail.put("contactNumber", contactNumber);
        					childDetail.put("infoString", infoString);
        					childDetail.put("address1", address1);
        					childDetail.put("toName", toName);
        					childDetail.put("attnName", attnName);
        					childDetail.put("address2", address2);
        					childDetail.put("postalCode", postalCode);
        					childDetail.put("countryGeoId", countryGeoId);
        					childDetail.put("stateProvinceGeoId", stateProvinceGeoId);
        					childDetail.put("extension", extension);
        					rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling getFacilityContactMech service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
	
	public static Map<String, Object> deleteFacilityContactMech(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String facilityId = (String)context.get("facilityId");
		GenericValue contactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contactMechId));
		List<GenericValue> listOrderItemShipGroup = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)), null, null, null, false);
		Timestamp nowTime = UtilDateTime.nowTimestamp();
		if(listOrderItemShipGroup.isEmpty()){
			if(contactMech != null){
				String contactMechTypeId = (String) contactMech.get("contactMechTypeId");
				List<GenericValue>  facilityContactMech = delegator.findList("FacilityContactMech",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechId", contactMechId)), null, null, null, false);
				facilityContactMech = EntityUtil.filterByDate(facilityContactMech);
				List<GenericValue>  facilityContactMechPurpose = delegator.findList("FacilityContactMechPurpose",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechId", contactMechId)), null, null, null, false);
				facilityContactMechPurpose = EntityUtil.filterByDate(facilityContactMechPurpose);
				if(contactMechTypeId.equals("POSTAL_ADDRESS")){
					if(facilityContactMech != null){
						for(GenericValue x : facilityContactMech){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMech);
					}
					if(facilityContactMechPurpose != null){
						for(GenericValue x : facilityContactMechPurpose){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMechPurpose);
					}
					result.put("value", "success");
				}
				if(contactMechTypeId.equals("TELECOM_NUMBER")){
					if(facilityContactMech != null){
						for(GenericValue x : facilityContactMech){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMech);
					}
					if(facilityContactMechPurpose != null){
						for(GenericValue x : facilityContactMechPurpose){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMechPurpose);
					}
					result.put("value", "success");
				}
				if(contactMechTypeId.equals("EMAIL_ADDRESS")){
					if(facilityContactMech != null){
						for(GenericValue x : facilityContactMech){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMech);					
						}
					if(facilityContactMechPurpose != null){
						for(GenericValue x : facilityContactMechPurpose){
							x.put("thruDate", nowTime);
						}
						delegator.storeAll(facilityContactMechPurpose);					}
					result.put("value", "success");
				}
			}
		}else{
			result.put("value", "exits");
		}
		return result;
	}
	
	public static Map<String, Object> loadContactMechTypePurposeList(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		Locale locale = (Locale)context.get("locale");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		GenericValue contactMechPurposeType = null;
		String contactMechPurposeTypeId = null;
		List<GenericValue> listContactTypePurpose = delegator.findList("ContactMechTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechTypeId", contactMechTypeId)), null, null, null, false);
		List<Map<String, String>> listcontactMechPurposeTypeMap = new ArrayList<Map<String,String>>();
		for (GenericValue contactTypePurpose : listContactTypePurpose) {
			if(contactTypePurpose != null){
				contactMechPurposeTypeId = (String) contactTypePurpose.get("contactMechPurposeTypeId");
				contactMechPurposeType = delegator.findOne("ContactMechPurposeType", false, UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId));
			}
			String description = (String) contactMechPurposeType.get("description", locale);
	    	Map<String, String> contactMechPurposeTypeMap =  new HashMap<String,String>(); 			
	    	contactMechPurposeTypeMap.put(contactMechPurposeTypeId, description);	  
			listcontactMechPurposeTypeMap.add(contactMechPurposeTypeMap);
			
		}
		result.put("listcontactMechPurposeTypeMap", listcontactMechPurposeTypeMap);
		return result;
	}
	
	public static Map<String, Object> createFacilityContactMechTelecomNumber(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
		String extension = (String)context.get("extension");
		if(numberOrNot(countryCode) == false || numberOrNot(areaCode) == false || numberOrNot(contactNumber) == false){
			result.put("value", "notNumber");
		}else{
			long countryCodeInt = Long.parseLong(countryCode);
			long areaCodeInt = Long.parseLong(areaCode);
			long contactNumberInt = Long.parseLong(contactNumber);
			if(countryCodeInt < 0 || areaCodeInt < 0 || contactNumberInt < 0){
				result.put("value", "notNumber");
			}else{
				Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
				contactMechId = delegator.getNextSeqId("ContactMech");
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", contactMechId);
				contactMech.put("contactMechTypeId", contactMechTypeId);
				
				GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
				facilityContactMech.put("facilityId", facilityId);
				facilityContactMech.put("contactMechId", contactMechId);
				facilityContactMech.put("fromDate", nowTimeStamp);
				
				GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
				facilityContactMechPurpose.put("facilityId", facilityId);
				facilityContactMechPurpose.put("contactMechId", contactMechId);
				facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				facilityContactMechPurpose.put("fromDate", nowTimeStamp);
				
				GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
				if(extension != null){
					if(numberOrNot(extension) == false){
						result.put("value", "notNumber");
						return result;
					}else{
						long extensionInt = Long.parseLong(extension);
						if(extensionInt < 0){
							result.put("value", "notNumber");
							return result;
						}else{
							telecomNumber.put("contactMechId", contactMechId);
							telecomNumber.put("countryCode", countryCode);
							telecomNumber.put("areaCode", areaCode);
							telecomNumber.put("contactNumber", contactNumber);
							facilityContactMech.put("extension", extension);
							delegator.create(contactMech);
							delegator.create(facilityContactMech);
							delegator.create(facilityContactMechPurpose);
							delegator.create(telecomNumber);
						}
					}
				}else{
					telecomNumber.put("contactMechId", contactMechId);
					telecomNumber.put("countryCode", countryCode);
					telecomNumber.put("areaCode", areaCode);
					telecomNumber.put("contactNumber", contactNumber);
					delegator.create(contactMech);
					delegator.create(facilityContactMech);
					delegator.create(facilityContactMechPurpose);
					delegator.create(telecomNumber);
				}
				result.put("value", "success");
			}
		}
		return result;
	}
	
	public static boolean numberOrNot(String input)
    {
        try
        {
            Long.parseLong(input);
        }
        catch(NumberFormatException ex)
        {
            return false;
        }
        return true;
    }
	
	public static Map<String, Object> createFacilityContactMechByEmailAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String) context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
		GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
		String infoString = (String) context.get("infoString");
		
		if(contactMechId == null){
			contactMechId = delegator.getNextSeqId("ContactMech");
		}
		
		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.put("contactMechId", contactMechId);
		contactMech.put("contactMechTypeId", contactMechTypeId);
		contactMech.put("infoString", infoString);
		delegator.create(contactMech);
		
		if(contactMechTypeId.equals("EMAIL_ADDRESS") == true || contactMechTypeId.equals("WEB_ADDRESS") == true|| contactMechTypeId.equals("LDAP_ADDRESS") == true){
			
			Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
			facilityContactMech.put("facilityId", facilityId);
			facilityContactMech.put("contactMechId", contactMechId);
			facilityContactMech.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMech);
			
			
			facilityContactMechPurpose.put("facilityId", facilityId);
			facilityContactMechPurpose.put("contactMechId", contactMechId);
			facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			facilityContactMechPurpose.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMechPurpose);
		}
		return result;
	}
	
	public static Map<String, Object> createContactMechEmailAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String infoString = (String)context.get("infoString");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		
		if(contactMechId == null){
			contactMechId = delegator.getNextSeqId("ContactMech");
		}
		
		GenericValue contactMech = delegator.makeValue("ContactMech");
		contactMech.put("contactMechId", contactMechId);
		contactMech.put("contactMechTypeId", contactMechTypeId);
		contactMech.put("infoString", infoString);
		delegator.create(contactMech);
		
		GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
		facilityContactMech.put("facilityId", facilityId);
		facilityContactMech.put("contactMechId", contactMechId);
		facilityContactMech.put("fromDate", nowTimeStamp);
		delegator.create(facilityContactMech);
		
		GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
		facilityContactMechPurpose.put("facilityId", facilityId);
		facilityContactMechPurpose.put("contactMechId", contactMechId);
		facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		facilityContactMechPurpose.put("fromDate", nowTimeStamp);
		delegator.create(facilityContactMechPurpose);
			
		result.put("value", "success");
		return result;
	}
	
	public static Map<String, Object> loadFacilityContactMechDetailByEdit(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String) context.get("contactMechId");
		List<GenericValue> listDetailContactMechInFacility = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)), null, null, null, false);
		result.put("listContactMechDetailByEdit" , listDetailContactMechInFacility);
		return result;
	}
	
	public static Map<String, Object> editTelecomNumberInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
		
		GenericValue telecomNumber = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId", contactMechId));
		if(telecomNumber != null){
			String countryCodeData = (String)telecomNumber.get("countryCode");
			String areaCodeData = (String)telecomNumber.get("areaCode");
			String contactNumberData = (String)telecomNumber.get("contactNumber");
			if(numberOrNot(countryCode) == false || numberOrNot(areaCodeData) == false || numberOrNot(contactNumber) == false){
				result.put("value", "notString");
			}else{
				long countryCodeLong = Long.parseLong(countryCode);
				long areaCodeLong = Long.parseLong(areaCode);
				long contactNumberLong = Long.parseLong(contactNumber);
				if(countryCode.equals(countryCodeData) && areaCode.equals(areaCodeData) && contactNumber.equals(contactNumberData)){
					result.put("value", "notEdit");
				}else{
					if(countryCodeLong < 0 || areaCodeLong < 0 || contactNumberLong < 0){
						result.put("value", "notString");
					}else{
						telecomNumber.put("countryCode", countryCode);
						telecomNumber.put("areaCode", areaCode);
						telecomNumber.put("contactNumber", contactNumber);
						delegator.store(telecomNumber);
						result.put("value", "success");
					}
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> editContactMechPostalAddressInFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String address1 = (String)context.get("address1");
		String address2 = (String)context.get("address2");
		String city = (String)context.get("city");
		String countryGeoId = (String)context.get("countryGeoId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String postalCode = (String)context.get("postalCode");
		
		GenericValue postalAddress = delegator.findOne("PostalAddress", false, UtilMisc.toMap("contactMechId", contactMechId));
		String address1Data = (String)postalAddress.get("address1");
		String address2Data = (String)postalAddress.get("address2");
		String cityData = (String)postalAddress.get("city");
		String countryGeoIdData = (String)postalAddress.get("countryGeoId");
		String stateProvinceGeoIdData = (String)postalAddress.get("stateProvinceGeoId");
		String postalCodeData = (String)postalAddress.get("postalCode");
		if(numberOrNot(postalCode) == false){
			result.put("value", "postalCodeNotNumber");
		}else{
			if(stateProvinceGeoId != null && address2 != null){
				if(address2.length() > 255){
					result.put("value", "address2MaxLength");
				}else{
					if(stateProvinceGeoIdData != null ){
						if(address2Data != null){
							if(address1.equals(address1Data) && address2.equals(address2Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && stateProvinceGeoId.equals(stateProvinceGeoIdData) && postalCode.equals(postalCodeData)){
								result.put("value", "notEdit");
							}else{
								postalAddress.put("address1", address1);
								postalAddress.put("address2", address2);
								postalAddress.put("city", city);
								postalAddress.put("countryGeoId", countryGeoId);
								postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
								postalAddress.put("postalCode", postalCode);
								delegator.store(postalAddress);
								result.put("value", "success");
							}
						}else{
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
			}else{
				if(stateProvinceGeoId == null && address2 == null){
					if(stateProvinceGeoIdData == null && address2Data == null){
						if(address1.equals(address1Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && postalCode.equals(postalCodeData)){
							result.put("value", "notEdit");
						}else{
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
				if(stateProvinceGeoId != null && address2 == null){
					if(stateProvinceGeoIdData != null && address2Data == null){
						if(address1.equals(address1Data) && city.equals(cityData) && countryGeoId.equals(countryGeoIdData) && postalCode.equals(postalCodeData)){
							result.put("value", "notEdit");
						}else{
							postalAddress.put("address1", address1);
							postalAddress.put("address2", address2);
							postalAddress.put("city", city);
							postalAddress.put("countryGeoId", countryGeoId);
							postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
							postalAddress.put("postalCode", postalCode);
							delegator.store(postalAddress);
							result.put("value", "success");
						}
					}else{
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
				if(stateProvinceGeoId == null && address2 != null){
					if(address2.length() > 255){
						result.put("value", "address2MaxLength");
					}else{
						postalAddress.put("address1", address1);
						postalAddress.put("address2", address2);
						postalAddress.put("city", city);
						postalAddress.put("countryGeoId", countryGeoId);
						postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
						postalAddress.put("postalCode", postalCode);
						delegator.store(postalAddress);
						result.put("value", "success");
					}
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getServiceFacility(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition); 
    	listAllConditions.add(tmpConditon);
    	
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String facilityTypeId = (String)parameters.get("facilityTypeId")[0];
    	if (facilityTypeId != null && !"".equals(facilityTypeId)){
    		Map<String, String> mapFacTypeCondition = new HashMap<String, String>();
    		mapFacTypeCondition.put("facilityTypeId", facilityTypeId);
    		EntityCondition tmpFacTypeConditon = EntityCondition.makeCondition(mapFacTypeCondition);
        	listAllConditions.add(tmpFacTypeConditon);
    	}
    	String parentFacilityId = null;
    	if (parameters.get("parentFacilityId") != null && parameters.get("parentFacilityId").length > 0){
    		parentFacilityId = (String)parameters.get("parentFacilityId")[0];
    	}
    	if (parentFacilityId != null && !"".equals(parentFacilityId)){
    		Map<String, String> mapFacTypeCondition = new HashMap<String, String>();
    		mapFacTypeCondition.put("parentFacilityId", parentFacilityId);
    		EntityCondition tmpFacTypeConditon = EntityCondition.makeCondition(mapFacTypeCondition);
        	listAllConditions.add(tmpFacTypeConditon);
    	}
    	String ownerPartyId = null;
    	if (parameters.get("ownerPartyId") != null && parameters.get("ownerPartyId").length > 0){
    		ownerPartyId = (String)parameters.get("ownerPartyId")[0];
    	}
    	if (ownerPartyId != null && !"".equals(ownerPartyId)){
    		Map<String, String> mapOwnerCondition = new HashMap<String, String>();
    		mapOwnerCondition.put("ownerPartyId", ownerPartyId);
    		EntityCondition tmpOwnerConditon = EntityCondition.makeCondition(mapOwnerCondition);
        	listAllConditions.add(tmpOwnerConditon);
    	} else {
            GenericValue userLogin = (GenericValue)context.get("userLogin");
        	String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		List<GenericValue> listAgent = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", company, "roleTypeIdFrom", "ACTIVATION_AGENT", "partyRelationshipTypeId", "ACTIVATION_AGENT")), null, null, null, false); 
			if (!listAgent.isEmpty()){
				List<String> listAgentPartyIds = new ArrayList<String>();
				for (GenericValue item : listAgent){
					listAgentPartyIds.add(item.getString("partyIdFrom"));
				}
				EntityCondition agent = EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, listAgentPartyIds);
				listAllConditions.add(agent);
			}
    	}
    	List<GenericValue> listFacilities = new ArrayList<GenericValue>();
    	try {
    		EntityListIterator listIterator = null;
    		listIterator = delegator.find("FacilityAll", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listFacilities = listIterator.getCompleteList();
    		listIterator.close();
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getFacilities service: " + e.toString();
			return ServiceUtil.returnError(errMsg);
		}
    	successResult.put("listIterator", listFacilities);
    	return successResult;
	}
	
	public static Map<String, Object> createFacilityContactMechPostalAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String toName = (String)context.get("toName");
		String attnName = (String)context.get("attnName");
		String address1 = (String)context.get("address1");
		String address2 = (String)context.get("address2");
		String city = (String)context.get("city");
		String countryGeoId = (String)context.get("countryGeoId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String postalCode = (String)context.get("postalCode");
		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
		
		if(contactMechId == null){
			contactMechId = delegator.getNextSeqId("ContactMech");
		}
		if(address2 != null){
			GenericValue contactMech = delegator.makeValue("ContactMech");
			contactMech.put("contactMechId", contactMechId);
			contactMech.put("contactMechTypeId", contactMechTypeId);
			delegator.create(contactMech);
			
			GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
			facilityContactMech.put("facilityId", facilityId);
			facilityContactMech.put("contactMechId", contactMechId);
			facilityContactMech.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMech);
			
			GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
			facilityContactMechPurpose.put("facilityId", facilityId);
			facilityContactMechPurpose.put("contactMechId", contactMechId);
			facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			facilityContactMechPurpose.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMechPurpose);
			
			GenericValue postalAddress = delegator.makeValue("PostalAddress");
			postalAddress.put("contactMechId", contactMechId);
			postalAddress.put("toName", toName);
			postalAddress.put("attnName", attnName);
			postalAddress.put("address1", address1);
			postalAddress.put("address2", address2);
			postalAddress.put("city", city);
			postalAddress.put("countryGeoId", countryGeoId);
			postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
			postalAddress.put("postalCode", postalCode);
			delegator.create(postalAddress);
			result.put("value", "success");
		}else{
			GenericValue contactMech = delegator.makeValue("ContactMech");
			contactMech.put("contactMechId", contactMechId);
			contactMech.put("contactMechTypeId", contactMechTypeId);
			delegator.create(contactMech);
			
			GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
			facilityContactMech.put("facilityId", facilityId);
			facilityContactMech.put("contactMechId", contactMechId);
			facilityContactMech.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMech);
			
			GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
			facilityContactMechPurpose.put("facilityId", facilityId);
			facilityContactMechPurpose.put("contactMechId", contactMechId);
			facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
			facilityContactMechPurpose.put("fromDate", nowTimeStamp);
			delegator.create(facilityContactMechPurpose);
			
			GenericValue postalAddress = delegator.makeValue("PostalAddress");
			postalAddress.put("contactMechId", contactMechId);
			postalAddress.put("toName", toName);
			postalAddress.put("attnName", attnName);
			postalAddress.put("address1", address1);
			postalAddress.put("address2", address2);
			postalAddress.put("city", city);
			postalAddress.put("countryGeoId", countryGeoId);
			postalAddress.put("stateProvinceGeoId", stateProvinceGeoId);
			postalAddress.put("postalCode", postalCode);
			delegator.create(postalAddress);
			result.put("value", "success");
		}
		return result;
	}
	
	public static Map<String, Object> getFacilityByPartyId(DispatchContext ctx, Map<String, Object> context){
		
		String partyId = (String)context.get("partyId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
	
		try {
			listFacilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", partyId)), null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("get Facility by Party error!");
		}
	
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("partyId", partyId);
		result.put("listFacilities", listFacilities);
		return result;
	}
	
	public static Map<String, Object> checkFacilityIdExisted(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String)context.get("facilityId"); 
		GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
		if(UtilValidate.isNotEmpty(facility)){
			result.put("hasExisted", true);
		} else {
			result.put("hasExisted", false);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listDepositFacilities(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			listAllConditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.EQUALS, ownerPartyId));
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.NOT_EQUAL, ownerPartyId));
			EntityListIterator listIterator = delegator.find("FacilityAndProductStore",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListDeliveryAndInventoryItemInfo(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       /*	Map<String, String> mapCondition = new HashMap<String, String>();*/
       	EntityCondition cond = EntityCondition.makeCondition();
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String orderId = parameters.get("orderId")[0];
       	EntityCondition tmpConditon = EntityCondition.makeCondition("fromOrderId", EntityOperator.EQUALS, orderId);
       	EntityCondition statusCondDelivery = EntityCondition.makeCondition("statusDeliveryId", EntityOperator.IN, UtilMisc.toList("DELI_ITEM_DELIVERED", "DELI_ITEM_EXPORTED"));  
    	/*EntityCondition statusCond = EntityCondition.makeCondition("statusIdOrder", EntityOperator.IN, UtilMisc.toList("ORDER_COMPLETED", "ORDER_IN_TRANSIT"));*/
        /*	listAllConditions.add(statusCond);*/
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(statusCondDelivery);
    	listAllConditions.add(cond);
    	List<GenericValue> listDetailInventory = new ArrayList<>();
    	/*List<GenericValue> listInventoryReceive = new ArrayList<GenericValue>();*/
     	try {
    		listIterator = delegator.find("DeliveryInventoryItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listDetailInventory = listIterator.getCompleteList();
    		listIterator.close();
    		for (GenericValue inv : listDetailInventory) {
    			/*int checkIf = 0;*/
				BigDecimal actualExportedQuantity = inv.getBigDecimal("actualExportedQuantity");
				BigDecimal quantityOnHandDiff = BigDecimal.ZERO;
				/*String productId = inv.getString("productId");*/
				String orderItemSeqId = inv.getString("fromOrderItemSeqId");
				
				for (GenericValue inv2 : listDetailInventory) {
					String orderItemSeqId2 = inv2.getString("fromOrderItemSeqId");
					if (UtilValidate.isNotEmpty(inv2.getBigDecimal("quantityOnHandDiff"))){
						if (orderItemSeqId2.equals(orderItemSeqId)) quantityOnHandDiff = quantityOnHandDiff.add(inv2.getBigDecimal("quantityOnHandDiff"));
					}
				}
				if(UtilValidate.isNotEmpty(quantityOnHandDiff)){
					inv.put("quantityRecieve", actualExportedQuantity.subtract(quantityOnHandDiff));
				}else{
					inv.put("quantityRecieve", actualExportedQuantity);
				}
				
				/*if(UtilValidate.isEmpty(listInventoryReceive)){
					listInventoryReceive.add(inv);
				}else{
					BigDecimal quantityRecieve = inv.getBigDecimal("quantityRecieve");
					for (GenericValue receive : listInventoryReceive) {
						String productIdRei = receive.getString("productId");
						BigDecimal actualExportedQuantityRe = receive.getBigDecimal("actualExportedQuantity");
						BigDecimal quantityOnHandDiffRe = receive.getBigDecimal("quantityOnHandDiff");
						BigDecimal quantityRecieveRe = receive.getBigDecimal("quantityRecieve");
						
						if(productId.equals(productIdRei)){
							int actualExportedQuantityIn = actualExportedQuantity.intValue() + actualExportedQuantityRe.intValue();
							int quantityOnHandDiffIn = 0;
							if(UtilValidate.isNotEmpty(quantityOnHandDiffRe)){
								if(UtilValidate.isNotEmpty(quantityOnHandDiff)){
									quantityOnHandDiffIn = quantityOnHandDiffRe.intValue() + quantityOnHandDiff.intValue();
								}else{
									quantityOnHandDiffIn = quantityOnHandDiffRe.intValue();
								}
							}
							int quantityRecieveIn = quantityRecieveRe.intValue() + quantityRecieve.intValue();
							receive.put("actualExportedQuantity", new BigDecimal(actualExportedQuantityIn));
							receive.put("quantityOnHandDiff", new BigDecimal(quantityOnHandDiffIn));
							receive.put("quantityRecieve", new BigDecimal(quantityRecieveIn));
							checkIf = 1;
						}else{
							checkIf = 0;
						}
					}
					if(checkIf == 0){
						listInventoryReceive.add(inv);
					}
				}*/
			}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListDeliveryAndInventoryItemInfo service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listDetailInventory);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> receiveFacilityDistributer(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		/*String org = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);*/
    	List<Object> listItemTmp = (List<Object>)context.get("listProducts");
    	Boolean isJson = false;
    	String facilityId = (String)context.get("facilityId");
    	/*String orderIdOut = (String)context.get("orderId");*/
    	
    	if (!listItemTmp.isEmpty()){  
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	String returnId = null;
    	String value = "error";
    	List<Map<String, String>> listProducts = new ArrayList<Map<String,String>>();
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				Map<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("quantity")){
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("productId")){
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("currencyUom")){
					mapItems.put("currencyUom", item.getString("currencyUom"));
				}
				if (item.containsKey("datetimeManufactured")){
					mapItems.put("datetimeManufactured", item.getString("datetimeManufactured"));
				}
				if (item.containsKey("expireDate")){
					mapItems.put("expireDate", item.getString("expireDate"));
				}
				if (item.containsKey("lotId")){
					mapItems.put("lotId", item.getString("lotId"));
				}
				if (item.containsKey("unitPrice")){
					mapItems.put("unitPrice", item.getString("unitPrice"));
				}
				if (item.containsKey("fromOrderItemSeqId")){
					mapItems.put("orderItemSeqId", item.getString("fromOrderItemSeqId"));
				}
				if (item.containsKey("fromOrderId")){
					mapItems.put("orderId", item.getString("fromOrderId"));
				}
				if (item.containsKey("returnId")){
					mapItems.put("returnId", item.getString("returnId"));
					returnId =  item.getString("returnId");
				}  
				if (item.containsKey("returnItemSeqId")){
					mapItems.put("returnItemSeqId", item.getString("returnItemSeqId"));
				}
				listProducts.add(mapItems);
			}
    	} else {
    		listProducts = (List<Map<String, String>>)context.get("listProducts");
    	}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
    	if (UtilValidate.isNotEmpty(returnId)){
    		Map<String, Object> mapReceive = FastMap.newInstance();
    		mapReceive.put("returnId", returnId);
    		mapReceive.put("userLogin", system);
    		mapReceive.put("needsInventoryReceive", "N");
    		mapReceive.put("statusId", "RETURN_ACCEPTED");
    		dispatcher.runSync("updateReturnHeader", mapReceive);
    	}
		
    	if (!listProducts.isEmpty()){
    		for (Map<String, String> item : listProducts){
    	    	Map<String, Object> inventoryItem = FastMap.newInstance();
    			Timestamp datetimeReceived = new Timestamp(new Date().getTime());
    			String orderId = null;
    			String orderItemSeqId = null;
    			String returnItemSeqId = null;
    			BigDecimal quantityReceive = null;
    			if (item.containsKey("orderId")){
    				orderId = item.get("orderId");
    			}
    			if (item.containsKey("orderItemSeqId")){
    				orderItemSeqId = item.get("orderItemSeqId");
    			}
    			if (item.containsKey("returnId")){
    				returnId = item.get("returnId");
    			}
    			if (item.containsKey("returnItemSeqId")){
    				returnItemSeqId = item.get("returnItemSeqId");
    			}
    			if (item.containsKey("expireDate")){
                    if (item.get("expireDate").equals("null")){
                        inventoryItem.put("expireDate",null);
                    } else {
                        inventoryItem.put("expireDate", new Timestamp(new Long(item.get("expireDate"))));
                    }
    			}
    			if (item.containsKey("productId")){
    				inventoryItem.put("productId", item.get("productId"));
    			}
    			if (item.containsKey("lotId")){
    				String lotId = item.get("lotId");
    				if(UtilValidate.isNotEmpty(lotId) && !lotId.equals("null")){
    					GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
    					if (UtilValidate.isEmpty(lot)){
    						GenericValue newLot = delegator.makeValue("Lot");
    						newLot.put("lotId", lotId);
    						newLot.put("creationDate", UtilDateTime.nowTimestamp());
    						delegator.create(newLot);
    					} 
    					inventoryItem.put("lotId", lotId);
    				}
    			}
    			if (item.containsKey("currencyUom")){
    				inventoryItem.put("currencyUomId", item.get("currencyUom"));
    			}
    			if (item.containsKey("datetimeManufactured")){
                    if (item.get("datetimeManufactured").equals("null")){
                        inventoryItem.put("datetimeManufactured", null);
                    } else {
                        inventoryItem.put("datetimeManufactured", new Timestamp(new Long(item.get("datetimeManufactured"))));
                    }
    			}
    			if (item.containsKey("quantity")){
    				quantityReceive = new BigDecimal(item.get("quantity"));
    			}
    			if (item.containsKey("unitPrice")){
    				inventoryItem.put("unitCost", new BigDecimal(item.get("unitPrice")));
    				inventoryItem.put("purCost", new BigDecimal(item.get("unitPrice")));
    			}
    		
    			if(UtilValidate.isNotEmpty(quantityReceive)){
    				String inventoryItemId = null;
    				if(quantityReceive.compareTo(BigDecimal.ZERO) > 0){
    	    			inventoryItem.put("datetimeReceived", datetimeReceived);
    	    			inventoryItem.put("facilityId", facilityId);
    	    			inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
    	    			inventoryItem.put("ownerPartyId", partyId);
    	    			inventoryItem.put("userLogin", system);
    	    			try {
							Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", inventoryItem);
							inventoryItemId = (String)mapTmp.get("inventoryItemId");
						} catch (GenericServiceException e){
							return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
						}
    	    			Map<String, Object> inventoryItemDetail = FastMap.newInstance();
    					inventoryItemDetail.put("orderId", orderId);
    					inventoryItemDetail.put("inventoryItemId", inventoryItemId);
    					inventoryItemDetail.put("orderItemSeqId", orderItemSeqId);
    					inventoryItemDetail.put("returnId", returnId);
    					inventoryItemDetail.put("returnItemSeqId", returnItemSeqId);
    					inventoryItemDetail.put("quantityOnHandDiff", quantityReceive);
    					inventoryItemDetail.put("availableToPromiseDiff", quantityReceive);
    					inventoryItemDetail.put("userLogin", system);
    					try {
							dispatcher.runSync("createInventoryItemDetail", inventoryItemDetail);
						} catch (GenericServiceException e){
							return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
						}
    					
    					if (UtilValidate.isNotEmpty(returnId) && UtilValidate.isNotEmpty(returnItemSeqId)){
	    					GenericValue returnItem = delegator.findOne("ReturnItem", false, UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
	    					Map<String, Object> mapReturnItemUpdate = FastMap.newInstance();
	    					mapReturnItemUpdate.put("returnId", returnId); 
	    					mapReturnItemUpdate.put("returnItemSeqId", returnItemSeqId); 
	    					if (UtilValidate.isNotEmpty(returnItem.getBigDecimal("receivedQuantity"))){
	    						mapReturnItemUpdate.put("receivedQuantity", returnItem.getBigDecimal("receivedQuantity").add(quantityReceive));
	    					} else {
	    						mapReturnItemUpdate.put("receivedQuantity", quantityReceive);
	    					}
	    					mapReturnItemUpdate.put("statusId", "RETURN_RECEIVED");
	    					mapReturnItemUpdate.put("userLogin", system);
	    					try {
	    						dispatcher.runSync("updateReturnItem", mapReturnItemUpdate);
	    					} catch (GenericServiceException e) {
	    						return ServiceUtil.returnError("OLBIUS: updateReturnItem error " + e.toString());
	    					}
    					}
    	    			value = "success";
    				}
    			}
    		}
    	}
    	if (UtilValidate.isNotEmpty(returnId)) {
    		Map<String, Object> mapReceive = FastMap.newInstance();
	    	mapReceive = FastMap.newInstance();
			mapReceive.put("returnId", returnId);
			mapReceive.put("userLogin", system);
			mapReceive.put("needsInventoryReceive", "N");
			mapReceive.put("statusId", "RETURN_RECEIVED");
			dispatcher.runSync("updateReturnHeader", mapReceive);
			
			mapReceive = FastMap.newInstance();
	    	mapReceive = FastMap.newInstance();
			mapReceive.put("returnId", returnId);
			mapReceive.put("userLogin", system);
			mapReceive.put("needsInventoryReceive", "N");
			mapReceive.put("statusId", "RETURN_COMPLETED");
			dispatcher.runSync("updateReturnHeader", mapReceive);
    	}
		
    	Map<String, Object> result = FastMap.newInstance();
    	result.put("value", value);
    	return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductReturnDistributors(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	EntityListIterator listIterator = null;
       /*	Map<String, String> mapCondition = new HashMap<String, String>();*/
       	EntityCondition cond = EntityCondition.makeCondition();
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String returnId = parameters.get("returnId")[0];
       	EntityCondition tmpConditon = EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId);
       	/*EntityCondition statusCondDelivery = EntityCondition.makeCondition("statusDeliveryId", EntityOperator.IN, UtilMisc.toList("DELI_ITEM_DELIVERED", "DELI_ITEM_EXPORTED")); */ 
    	/*EntityCondition statusCond = EntityCondition.makeCondition("statusIdOrder", EntityOperator.IN, UtilMisc.toList("ORDER_COMPLETED", "ORDER_IN_TRANSIT"));*/
        /*	listAllConditions.add(statusCond);*/
    	listAllConditions.add(tmpConditon);
    	/*listAllConditions.add(statusCondDelivery);*/
    	listAllConditions.add(cond);
    	List<GenericValue> listDetailInventory = new ArrayList<>();
    	/*List<GenericValue> listInventoryReceive = new ArrayList<GenericValue>();*/
     	try {
    		listIterator = delegator.find("ReturnInventoryItemDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listDetailInventory = listIterator.getCompleteList();
    		listIterator.close();
    		for (GenericValue inv : listDetailInventory) {
				BigDecimal returnQuantity = inv.getBigDecimal("returnQuantity");
				BigDecimal quantityOnHandDiff = inv.getBigDecimal("quantityOnHandDiff");
				if(UtilValidate.isNotEmpty(quantityOnHandDiff)){
					inv.put("quantityRecieve", returnQuantity.subtract(quantityOnHandDiff));
				}else{
					inv.put("quantityRecieve", returnQuantity);
				}
			}
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListProductReturnDistributors service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listDetailInventory);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListInventoryReachDate(DispatchContext ctx, Map<String, ? extends Object> context) {
      	Delegator delegator = ctx.getDelegator();
      	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
      	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
      	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
      	EntityCondition cond = EntityCondition.makeCondition();
      	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
      	String productId = parameters.get("productId")[0];
      	if(UtilValidate.isNotEmpty(productId)){
      		listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
      	}
       	listAllConditions.add(cond);
       	Timestamp dateCurrentTime = UtilDateTime.nowTimestamp();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	try {
			    String sqlCondition = "";
		    	sqlCondition = "(INV.OWNER_PARTY_ID = '"+partyIdByFacility+"' AND (INV.EXPIRE_DATE <= '"+dateCurrentTime+"' + INTERVAL PRF.THRESHOLDS_DATE DAY) AND (INV.EXPIRE_DATE  > '"+dateCurrentTime+"'))"; 
				listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
				List<GenericValue> listInventoryItem = delegator.findList("ProductInventoryNearDate", tmpConditon, null, listSortFields, opts, false);
				successResult.put("listIterator", listInventoryItem);	
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling jqGetListInventoryReachSale service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(e.getStackTrace().toString());
   		}
       	return successResult;
   	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListInventoryReachDateSales(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition cond = EntityCondition.makeCondition();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productId = parameters.get("productId")[0];
		if(UtilValidate.isNotEmpty(productId)){
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		}
		listAllConditions.add(cond);
		Timestamp dateCurrentTime = UtilDateTime.nowTimestamp();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String sqlCondition = "";
			sqlCondition = "(INV.OWNER_PARTY_ID = '"+partyIdByFacility+"' AND (INV.EXPIRE_DATE <= '"+dateCurrentTime+"' + INTERVAL PRF.THRESHOLDS_SALE DAY) AND (INV.EXPIRE_DATE  > '"+dateCurrentTime+"' ))"; 
			listAllConditions.add(EntityCondition.makeConditionWhere(sqlCondition));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			List<GenericValue> listInventoryItem = delegator.findList("ProductInventoryNearDate", tmpConditon, null, listSortFields, opts, false);
			successResult.put("listIterator", listInventoryItem);	
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListInventoryReachDate service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		return successResult;
	}
	
	public static Map<String, Object> loadContactMechDetailByEdit(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String) context.get("contactMechId");
		List<GenericValue> listDetailContactMechInFacility = delegator.findList("ContactMechInFacilityTypePurpose", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contactMechId)), null, null, null, false);
		result.put("listContactMechDetailByEdit" , listDetailContactMechInFacility);
		return result;
	}
	
	public static Map<String, Object> editWebAddressOrLDAPAddress(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = (String)context.get("contactMechId");
		String infoString = (String)context.get("infoString");
		
		GenericValue contactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", contactMechId));
		if(contactMech != null){
			String infoStringData = (String)contactMech.get("infoString");
			if(infoString.equals(infoStringData)){
				result.put("value", "notEdit");
			}else{
				contactMech.put("infoString", infoString);
				delegator.store(contactMech);
				result.put("value", "success");
			}
		}
		return result;
	}
	
	public static Map<String, Object> createFacilityForPartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		String facilityGroupId  =  (String) context.get("primaryFacilityGroupId");
		GenericValue facility = null;
		if(UtilValidate.isNotEmpty(facilityId)){
			try {
				facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("getFacilityById error!" + e.toString());
			}
			if (facility == null){
				facility = delegator.makeValue("Facility");
				facility.set("facilityId", facilityId);
				facility.set("facilityCode", facilityId);
			}
		} else{
			facility = delegator.makeValue("Facility");
			facilityId = delegator.getNextSeqId("Facility");
			facility.set("facilityId", facilityId);
			facility.set("facilityCode", facilityId);
		}
		
		if(UtilValidate.isNotEmpty(facilityGroupId)){
			facility.set("primaryFacilityGroupId", facilityGroupId);
		}
		
		facility.setNonPKFields(context);
		String partyId = (String)context.get("partyId");
		if (UtilValidate.isEmpty(partyId)) return ServiceUtil.returnError("OLBIUS: Cannot found partyId owner to update!");
		if (UtilValidate.isNotEmpty((String)context.get("facilityName"))){
			facility.set("facilityName", (String)context.get("facilityName"));
		} else {
			GenericValue partyGroup = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId", partyId));
			if (UtilValidate.isEmpty((partyGroup))) return ServiceUtil.returnError("OLBIUS: party " +partyId+ " Not a party group!");
			facility.set("facilityName", partyGroup.getString("groupName"));
		}
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String ownerPartyId = (String)context.get("ownerPartyId");
		if (UtilValidate.isNotEmpty(ownerPartyId)){
			if (!SecurityUtil.hasRole("OWNER", ownerPartyId, delegator)) return ServiceUtil.returnError("OLBIUS: partyId " +ownerPartyId+ " not had OWNER role!");
			facility.set("ownerPartyId", ownerPartyId);
		} else {
			facility.set("ownerPartyId", partyId);
		}
		String facilityTypeId = (String)context.get("facilityTypeId");
		if (UtilValidate.isEmpty(facilityTypeId)) facilityTypeId = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.facilityTypeId");
		if (UtilValidate.isEmpty(facilityTypeId)) return ServiceUtil.returnError("OLBIUS: Cannot found facility type to update!");
		facility.set("facilityTypeId", facilityTypeId);
		facility.set("defaultInventoryItemTypeId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "inventoryItemTypeId"));
		facility.set("openedDate", UtilDateTime.nowTimestamp());
		delegator.createOrStore(facility);
		try {
			dispatcher.runSync("addPartyToFacility", UtilMisc.toMap("userLogin", userLogin, "roleTypeId", "OWNER", "partyId", facility.getString("ownerPartyId"), "fromDate", UtilDateTime.nowTimestamp(), "facilityId", facilityId));
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: add party owner to facility error!");
		}
		List<String> listRoleTypeDefault = LogisticsStringUtil.splitKeyProperty(EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.facility.roles", delegator));
//		Boolean isOk = LogisticsPartyUtil.checkPartyHasRoles(delegator, listRoleTypeDefault, partyId);
//		if (!isOk) return ServiceUtil.returnError("OLBIUS: Party "+partyId+" not has enough basic roles");
		for (String roleTypeId : listRoleTypeDefault){
			try {
				dispatcher.runSync("addPartyToFacility", UtilMisc.toMap("userLogin", userLogin, "roleTypeId", roleTypeId, "partyId", partyId, "fromDate", UtilDateTime.nowTimestamp(), "facilityId", facilityId));
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: add party to facility error!");
			}
		}
		
		Object postalAddressId = context.get("postalAddressId");
		if (UtilValidate.isEmpty(postalAddressId)) {
			String contactMechPurposeTypeId = EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.party.contactmech.purpose", delegator);
			if (UtilValidate.isEmpty(contactMechPurposeTypeId)) contactMechPurposeTypeId = "PRIMARY_LOCATION";
			List<GenericValue> listPrimaryCTMs = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", contactMechPurposeTypeId)), null, null, null, false);
			listPrimaryCTMs = EntityUtil.filterByDate(listPrimaryCTMs);
			if (listPrimaryCTMs.isEmpty()) return ServiceUtil.returnError("OLBIUS: Contact mech purpose " +contactMechPurposeTypeId+ " of party " +partyId+ " not found!");
			GenericValue ctmId = listPrimaryCTMs.get(0);
			postalAddressId = ctmId.getString("contactMechId");
		}
		List<String> listContactMechPurpose = LogisticsStringUtil.splitKeyProperty(EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.facility.contactmech.purpose", delegator));
		if (listContactMechPurpose.isEmpty()) return ServiceUtil.returnError("OLBIUS: Contact mech purpose default for facility not found");
		try {
			for (String purposeId : listContactMechPurpose) {
				dispatcher.runSync("createFacilityContactMech", UtilMisc.toMap("userLogin", userLogin, "facilityId", facilityId, "contactMechPurposeTypeId", purposeId, "contactMechId", postalAddressId));
			}
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: create facility contact mech error!");
		}
		Object telecomNumberId = context.get("telecomNumberId");
		if (UtilValidate.isNotEmpty(telecomNumberId)) {
			//	createFacilityTelecomNumber
			try {
				dispatcher.runSync("createFacilityContactMech",
						UtilMisc.toMap("contactMechId", telecomNumberId, "contactMechPurposeTypeId", "PRIMARY_PHONE", "facilityId",  facilityId, "userLogin", userLogin));
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: create facility contact mech error!");
			}
		}
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("facilityId", facilityId);
		return result;
	}
	
	public static Map<String, Object> createContactMechTelecomNumber(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contactMechId = null;
		String facilityId = (String)context.get("facilityId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		String countryCode = (String)context.get("countryCode");
		String areaCode = (String)context.get("areaCode");
		String contactNumber = (String)context.get("contactNumber");
		String extension = (String)context.get("extension");
		if(numberOrNot(countryCode) == false || numberOrNot(areaCode) == false || numberOrNot(contactNumber) == false){
			result.put("value", "notNumber");
		}else{
			long countryCodeInt = Long.parseLong(countryCode);
			long areaCodeInt = Long.parseLong(areaCode);
			long contactNumberInt = Long.parseLong(contactNumber);
			if(countryCodeInt < 0 || areaCodeInt < 0 || contactNumberInt < 0){
				result.put("value", "notNumber");
			}else{
				Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
				contactMechId = delegator.getNextSeqId("ContactMech");
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", contactMechId);
				contactMech.put("contactMechTypeId", contactMechTypeId);
				
				GenericValue facilityContactMech = delegator.makeValue("FacilityContactMech");
				facilityContactMech.put("facilityId", facilityId);
				facilityContactMech.put("contactMechId", contactMechId);
				facilityContactMech.put("fromDate", nowTimeStamp);
				
				GenericValue facilityContactMechPurpose = delegator.makeValue("FacilityContactMechPurpose");
				facilityContactMechPurpose.put("facilityId", facilityId);
				facilityContactMechPurpose.put("contactMechId", contactMechId);
				facilityContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
				facilityContactMechPurpose.put("fromDate", nowTimeStamp);
				
				GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
				if(extension != null){
					if(numberOrNot(extension) == false){
						result.put("value", "notNumber");
						return result;
					}else{
						long extensionInt = Long.parseLong(extension);
						if(extensionInt < 0){
							result.put("value", "notNumber");
							return result;
						}else{
							telecomNumber.put("contactMechId", contactMechId);
							telecomNumber.put("countryCode", countryCode);
							telecomNumber.put("areaCode", areaCode);
							telecomNumber.put("contactNumber", contactNumber);
							facilityContactMech.put("extension", extension);
							delegator.create(contactMech);
							delegator.create(facilityContactMech);
							delegator.create(facilityContactMechPurpose);
							delegator.create(telecomNumber);
						}
					}
				}else{
					telecomNumber.put("contactMechId", contactMechId);
					telecomNumber.put("countryCode", countryCode);
					telecomNumber.put("areaCode", areaCode);
					telecomNumber.put("contactNumber", contactNumber);
					delegator.create(contactMech);
					delegator.create(facilityContactMech);
					delegator.create(facilityContactMechPurpose);
					delegator.create(telecomNumber);
				}
				result.put("value", "success");
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetFacilityPartyRole(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	EntityListIterator listIterator = null;
       	Map<String, String> mapCondition = new HashMap<String, String>();
       	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	String facilityId = null;
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listFacilitys = new ArrayList<GenericValue>();
    	if (UtilValidate.isNotEmpty(parameters.get("facilityId")) && parameters.get("facilityId").length > 0) {
    		facilityId = parameters.get("facilityId")[0];
    		listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId)); 
    		try {
            	listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "MANAGER")); 
            	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
            	
            	listIterator = delegator.find("FacilityPartyAndRoleType", cond, null, null, listSortFields, opts);
            	listFacilitys = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
        	} catch (GenericEntityException e) {
    			String errMsg = "Fatal error calling jqGetFacilityPartyRole service: " + e.toString();
    			Debug.logError(e, errMsg, module);
    			return ServiceUtil.returnError(e.getStackTrace().toString());
    		}
    	}
    	successResult.put("listIterator", listFacilitys);
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetAllFacilityParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String ownerPartyId = null;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listFacilityTmp = new ArrayList<GenericValue>();
		List<Map<String, Object>> listFacilities = new ArrayList<Map<String, Object>>();
		if (UtilValidate.isNotEmpty(parameters.get("ownerPartyId")) && parameters.get("ownerPartyId").length > 0) {
			ownerPartyId = parameters.get("ownerPartyId")[0];
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId)); 
		} else {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company)); 
		}
		try {
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = delegator.find("Facility", cond, null, null, listSortFields, opts);
			listFacilityTmp = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
			if (!listFacilityTmp.isEmpty()){
				for (GenericValue facility : listFacilityTmp) {
					String facilityId = facility.getString("facilityId");
					EntityCondition cond1 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
					EntityCondition cond2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "MANAGER");
					EntityCondition conds = EntityCondition.makeCondition(UtilMisc.toList(cond1, cond2));
	    			List<GenericValue> facilityParties = delegator.findList("FacilityPartyDetail", conds, null, UtilMisc.toList("-description"), null, false);
	    			Map<String, Object> row = new HashMap<String, Object>();
	    			row = facility.getAllFields();
	    			if (!facilityParties.isEmpty()){
	            		row = facility.getAllFields();
	        			List<Map<String, Object>> listTmps = FastList.newInstance();
	        			for (GenericValue role : facilityParties){
	        				listTmps.add(role.getAllFields());
	        			}
	        			row.put("rowDetail", listTmps);
	        		}
	    			listFacilities.add(row);
	    		}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetFacilityPartyRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		successResult.put("listIterator", listFacilities);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListInventoryExpireDate(DispatchContext ctx, Map<String, ? extends Object> context) {
      	Delegator delegator = ctx.getDelegator();
      	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
      	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
      	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
      	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
      	String productId = parameters.get("productId")[0];
      	if(UtilValidate.isNotEmpty(productId)){
          	listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
      	}
      	listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyIdByFacility));
      	listAllConditions.add(EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
      	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
      	
       	List<GenericValue> listInventoryItem = new ArrayList<GenericValue>();
       	try {
			listInventoryItem = delegator.findList("InventoryItemQOHGreateZEROGroupByDate", cond, null, listSortFields, opts, false);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling jqGetListInventoryExpireDate service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(e.getStackTrace().toString());
   		}
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	successResult.put("listIterator", listInventoryItem);
       	return successResult;
   	}
	
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> jqGetListInventoryQuantityWarning(DispatchContext ctx, Map<String, ? extends Object> context) {
      	Delegator delegator = ctx.getDelegator();
      	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
      	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
      	GenericValue userLogin = (GenericValue)context.get("userLogin");
      	List<GenericValue> listInventoryItem = FastList.newInstance();
		List<String> listFacilityManages = FastList.newInstance();
		try {
			Security security = ctx.getSecurity();
			listFacilityManages = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin, security);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListInventoryQuantityWarning service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(errMsg);
		}
		
		if (listFacilityManages.isEmpty()){
			Map<String, Object> successResult = ServiceUtil.returnSuccess();
	       	successResult.put("listIterator", listInventoryItem);
	       	return successResult; 
		}
      	listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityManages));
      	EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
      	
      	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
      	String type = null;
		if (parameters.containsKey("type") && parameters.get("type").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("type"))) {
				type = parameters.get("type")[0];
			}
		}
		String entity = "InventoryItemQuantityWarning";
		if (UtilValidate.isNotEmpty(type) && "MAX".equals(type)) {
			entity = "InventoryItemQuantityWarningMax";
		}
       	try {
			listInventoryItem = delegator.findList(entity, cond, null, listSortFields, opts, false);
       	} catch (GenericEntityException e) {
   			String errMsg = "Fatal error calling jqGetListInventoryQuantityWarning service: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(e.getStackTrace().toString());
   		}
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	successResult.put("listIterator", listInventoryItem);
       	return successResult;
   	}
	
	public static Map<String, Object> deleteFacility(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		// Check inventory related
		List<GenericValue> listInventoryItems = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listInventoryItems.isEmpty()){
			return ServiceUtil.returnError("OLBIUS_DELETE_FACILITY_DATA_RELATED");
		} 
		
		// Facility Role
		List<GenericValue> listFacilityParties = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listFacilityParties.isEmpty()){
			delegator.removeAll(listFacilityParties);
		}
		
		// Facility product store
		List<GenericValue> listFacilityStores = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listFacilityStores.isEmpty()){
			delegator.removeAll(listFacilityStores);
		}
		
		// FacilityContactMech
		List<GenericValue> listFacilityContactMechs = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listFacilityContactMechs.isEmpty()){
			delegator.removeAll(listFacilityContactMechs);
		}
		
		// FacilityContactMechPurpose
		List<GenericValue> listFacilityContactMechPurposes = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listFacilityContactMechPurposes.isEmpty()){
			delegator.removeAll(listFacilityContactMechPurposes);
		}
				
		// FacilityLocation
		List<GenericValue> listFacilityLocations = delegator.findList("FacilityLocation", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		if (!listFacilityLocations.isEmpty()){
			delegator.removeAll(listFacilityLocations);
		}
		
		List<GenericValue> listStores = delegator.findList("ProductStore", EntityCondition.makeCondition(UtilMisc.toMap("inventoryFacilityId", facilityId)), null, null, null, false);
		if (!listStores.isEmpty()){
			for (GenericValue item : listStores) {
				item.put("inventoryFacilityId", null);
				delegator.store(item);
			}
		}
		GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
		delegator.removeValue(facility);
		Map<String, Object> result = ServiceUtil.returnSuccess();
       	return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,Object> createFacilityParties(DispatchContext ctx, Map<String, Object> context){
	    List<Map<String, Object>> listFacilities = null;
	    List<Map<String, Object>> listParties = null;
	    List<Map<String, Object>> listRoleTypes = null;
	    String strListFa = (String)context.get("listFacilities");
	    String strListPty = (String)context.get("listParties");
	    String strListRole = (String)context.get("listRoleTypes");
	    Long fromDate = null;
	    Long thruDate = null;
	    if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
	    	thruDate = (Long)context.get("thruDate");
		}
	    if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
	    	fromDate = (Long)context.get("fromDate");
	    }
	    
	    try {
	    	listFacilities = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strListFa);
	    	listParties = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strListPty);
	    	listRoleTypes = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strListRole);
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	for (Map<String, Object> fa : listFacilities) {
				String facilityId = (String)fa.get("facilityId");
				for (Map<String, Object> pt : listParties) {
					String partyId = (String)pt.get("partyId");
					for (Map<String, Object> rl : listRoleTypes) {
						String roleTypeId = (String)rl.get("roleTypeId");
						Map<String, Object> map = FastMap.newInstance();
						map.put("facilityId", facilityId);
						map.put("partyId", partyId);
						map.put("roleTypeId", roleTypeId);
						map.put("fromDate", fromDate);
						map.put("thruDate", thruDate);
						map.put("userLogin", userLogin);
						try {
							dispatcher.runSync("addPartyToFacility", map);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: addPartyToFacility error! " + e.toString());
						}
						String admin = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.admin");
						String spc = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.specialist");
						String stk = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper");
						if (roleTypeId.equals(admin) || roleTypeId.equals(spc) || roleTypeId.equals(stk)) {
							String managerRole = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "roleType.manager");
							map = FastMap.newInstance();
							map.put("facilityId", facilityId);
							map.put("partyId", partyId);
							map.put("roleTypeId", managerRole);
							map.put("fromDate", fromDate);
							map.put("thruDate", thruDate);
							map.put("userLogin", userLogin);
							try {
								dispatcher.runSync("addPartyToFacility", map);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: addPartyToFacility error! " + e.toString());
							}
						}
					}
				}
			}
        } catch (ParseException e1) {
            return ServiceUtil.returnError(e1.toString());
        }
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    return result;
	}
	
    public static Map<String,Object> removePartyRoleWithFacility(DispatchContext ctx, Map<String, Object> context){
	    String facilityId = (String)context.get("facilityId");
	    String partyId = (String)context.get("partyId");
	    String roleTypeId = (String)context.get("roleTypeId");
	    Long fromDate = null;
	    if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	    	fromDate = (Long)context.get("fromDate");
	    	Map<String, Object> map = FastMap.newInstance();
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	map.put("facilityId", facilityId);
	    	map.put("partyId", partyId);
	    	map.put("roleTypeId", roleTypeId);
	    	map.put("fromDate", new Timestamp(fromDate));
	    	map.put("userLogin", userLogin);
	    	try {
				dispatcher.runSync("removePartyFromFacility", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: removePartyFromFacility error! " + e.toString());
			}
	    }
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetFacilities(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	String ownerPartyId = null;
    	Boolean distributor = SecurityUtil.hasRole("DISTRIBUTOR", userLogin.getString("partyId"), delegator);
		String parentFacility = null;
		String facilityGroupId = null;
		String facilityTypeId = null;
		String productStoreId = null;
		String primaryFacilityGroupId = null;
		String excludePartyId = null;
		
		if(parameters.get("primaryFacilityGroupId") != null && parameters.get("primaryFacilityGroupId").length > 0){
			primaryFacilityGroupId = (String)parameters.get("primaryFacilityGroupId")[0];
			listAllConditions.add(EntityCondition.makeCondition("primaryFacilityGroupId", EntityOperator.EQUALS, primaryFacilityGroupId));
		}	
		
		if(parameters.get("parentFacilityId") != null && parameters.get("parentFacilityId").length > 0){
			parentFacility = (String)parameters.get("parentFacilityId")[0];
			listAllConditions.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, parentFacility));
		}	
		if (parameters.get("productStoreId") != null && parameters.get("productStoreId").length > 0){
			productStoreId = (String)parameters.get("productStoreId")[0];
			
			EntityCondition cond1 = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId);
			List<GenericValue> listProductStoreFacility = FastList.newInstance();
			try {
				listProductStoreFacility = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition("productStoreId", productStoreId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductStoreFacility: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			listProductStoreFacility = EntityUtil.filterByDate(listProductStoreFacility);
			List<String> faIds = FastList.newInstance();
			if (!listProductStoreFacility.isEmpty()){
				faIds = EntityUtil.getFieldListFromEntityList(listProductStoreFacility, "facilityId", true);
			}
			if (!faIds.isEmpty()){
				listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, faIds));
			} else {
				listAllConditions.add(cond1);
			}
		}	
		if (parameters.get("facilityGroupId") != null && parameters.get("facilityGroupId").length > 0) {
			facilityGroupId = (String)parameters.get("facilityGroupId")[0];
    	};
    	if (parameters.get("facilityTypeId") != null && parameters.get("facilityTypeId").length > 0) {
    		facilityTypeId = (String)parameters.get("facilityTypeId")[0];
    	};
    	if (UtilValidate.isNotEmpty(facilityGroupId)) {
    		EntityCondition condGroup = EntityCondition.makeCondition("primaryFacilityGroupId", EntityOperator.EQUALS, facilityGroupId);
    		listAllConditions.add(condGroup);
		}
    	if (UtilValidate.isNotEmpty(facilityTypeId)) {
    		EntityCondition condType = EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, facilityTypeId);
    		listAllConditions.add(condType);
    	}
    	
    	if (!distributor){
			ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	} else {
	    	ownerPartyId = userLogin.getString("partyId");
    	}
    	EntityCondition tmpOwnerConditon = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId);
		
    	if(parameters.get("excludePartyId") != null && parameters.get("excludePartyId").length > 0){
    		excludePartyId = (String) parameters.get("excludePartyId")[0];
			List<GenericValue> excludeFacList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", excludePartyId), null, null, null, false);
			if(UtilValidate.isNotEmpty(excludeFacList)) {
				List<String> excludeFacIdList = FastList.newInstance();
				for(GenericValue x : excludeFacList) {
					excludeFacIdList.add(x.getString("facilityId"));
				}
	    		EntityCondition condExFac = EntityCondition.makeCondition("facilityId", EntityOperator.NOT_IN, excludeFacIdList);
	    		listAllConditions.add(condExFac);
			}
		}	
		List<String> listFacilityIds = FastList.newInstance();
		
		String deposit = null;
		if (parameters.get("deposit") != null && parameters.get("deposit").length > 0) {
			deposit = parameters.get("deposit")[0];
		}
		if (UtilValidate.isNotEmpty(deposit) && "Y".equals(deposit)){
			listFacilityIds = LogisticsFacilityUtil.getFacilityDepositAllowedView(delegator, userLogin);
		} else {
			listFacilityIds = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin);
			if (UtilValidate.isEmpty(facilityTypeId) || (UtilValidate.isNotEmpty(facilityTypeId) && !"PORT".equals(facilityTypeId))) {
				listAllConditions.add(tmpOwnerConditon);
	    	}
		}
		
		if (!listFacilityIds.isEmpty()){
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds));
		}

		List<Map<String,Object>> listReturnFacilities = FastList.newInstance();
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
    	try {
            listFacilities = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "FacilityAll", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: jqGetFacilities error! " + e.toString();
			return ServiceUtil.returnError(errMsg);
		}
    	for (GenericValue facility : listFacilities) {
			String facilityId = facility.getString("facilityId");
			List<GenericValue> childs = delegator.findList("FacilityAll", EntityCondition.makeCondition(UtilMisc.toMap("parentFacilityId", facilityId)), null, null, null, false);
			Map<String, Object> row = new HashMap<String, Object>();
			row = facility.getAllFields();
			if (!childs.isEmpty()){
				List<Map<String, Object>> listToResult = FastList.newInstance();
    			for (GenericValue facilityChild : childs) {
    				listToResult.add(facilityChild.getAllFields());
    			}
    			row.put("rowDetail", listToResult);
    		}
			listReturnFacilities.add(row);
		}
    	successResult.put("listIterator", listReturnFacilities);
    	return successResult;
	}

	public static Map<String, Object> jqGetFacilitiesForSup(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        List<GenericValue> listFacilities;
        try {
            List<GenericValue> listDitributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", userLogin.getString("userLoginId")), null, null, null, false);
            List<String> owners = EntityUtil.getFieldListFromEntityList(listDitributor, "partyId", false);
            listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.IN, owners));
            listFacilities = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "FacilityAll", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (GenericEntityException e) {
            String errMsg = "OLBIUS: getAllFacilityForSup error! " + e.toString();
            return ServiceUtil.returnError(errMsg);
        }
        successResult.put("listIterator", listFacilities);
        return successResult;
    }
    
	public static Map<String, Object> jqGetFacilitiesDisByUserLogin(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		List<GenericValue> listFacilities;
		List<EntityCondition> conditions = FastList.newInstance();
		try {
			boolean  isSearch = true;
			String userLoginPartyId = userLogin.getString("partyId");
			if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
				List<String> listDistIds = PartyWorker.getDistributorBySalesman(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listDistIds)) {
					isSearch = false;
				} else if (listDistIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
				}
			} else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
				conditions.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
			} else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
				List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listSupIds)) {
					isSearch = false;
				} else if (listSupIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
				}
			} else if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
				List<String> listDistIds = PartyWorker.getDistributorBySalesadmin(delegator, userLoginPartyId);
				if (UtilValidate.isEmpty(listDistIds)) {
					isSearch = false;
				} else if (listDistIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
				}
			} else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
				String currentOrgId = SalesUtil.getCurrentOrganization(delegator, userLogin);
				List<String> listDistIds = PartyWorker.getDistributorByOrg(delegator, currentOrgId, Boolean.FALSE);
				if (UtilValidate.isEmpty(listDistIds)) {
					isSearch = false;
				} else if (listDistIds.size() == 1) {
					conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
				} else {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
				}
			} else {
				isSearch = false;
			}
			List<GenericValue> listDitributor = delegator.findList("PartyDistributor", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> owners = EntityUtil.getFieldListFromEntityList(listDitributor, "partyId", false);
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.IN, owners));
			listFacilities = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "FacilityAll", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: getAllFacilityForSup error! " + e.toString();
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listFacilities);
		return successResult;
	}
	public static Map<String, Object> updateFacilityRequireDate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue facility ;
		String facilityId = (String) context.get("facilityId");
		String requireDate = (String) context.get("requireDate");
		try {
			facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			facility.set("requireDate", requireDate);
			delegator.store(facility);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}


    public static Map<String, Object> updateCoordinateFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();

        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        try {
            String facilityId = (String) context.get("facilityId");
            String lat = (String) context.get("lat");
            String lng = (String) context.get("lng");
            String postalAddressId = (String) context.get("postalAddressId");
            String dataSourceId = (String) context.get("dataSourceId");
            String geoPointId = (String) context.get("geoPointId");

            Debug.log(module + "::updateCoordinateFacility, facilityId = " + facilityId + ", lat = " + lat + ", lng = "
                    + ", lng = " + lng + ", postalAddressId = " + postalAddressId + ", dataSourceId = " + dataSourceId
                    + ", geoPointId = " + geoPointId);

            Double latitude = 15.7480949D;
            Double longitude = 101.4137231D;

            result.put("facilityId", facilityId);

            try {
                if (UtilValidate.isNotEmpty(lat)) {
                    latitude = Double.valueOf(lat);
                }
                if (UtilValidate.isNotEmpty(lng)) {
                    longitude = Double.valueOf(lng);
                }
            } catch (Exception e) {
                Debug.logWarning("Error when format Double", module);
                return ServiceUtil.returnError("Not valid latitude/longitude");
            }

            GenericValue contact = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", postalAddressId), false);
            if (UtilValidate.isNotEmpty(contact)) {
                String gpId = contact.getString("geoPointId");
                if (UtilValidate.isNotEmpty(gpId)) {
                    GenericValue geoPoint = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId", gpId), false);

                    if (UtilValidate.isNotEmpty(geoPoint)) {
                        geoPoint.put("latitude", latitude);
                        geoPoint.put("longitude", longitude);
                        geoPoint.put("dataSourceId", dataSourceId);

                        delegator.store(geoPoint);

                        result.put("message", "Update coordinate Success");
                        return result;
                    }
                } else {
                    GenericValue newPoint = delegator.makeValue("GeoPoint");
                    newPoint.put("geoPointId", delegator.getNextSeqId("GeoPoint"));
                    newPoint.put("latitude", latitude);
                    newPoint.put("longitude", longitude);
                    newPoint.put("dataSourceId", dataSourceId);
                    contact.put("geoPointId", newPoint.get("geoPointId"));
                    delegator.create(newPoint);
                    delegator.store(contact);

                    result.put("message", "Update coordinate Success");
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result=ServiceUtil.returnError("Not found contact");
        return result;
    }

}