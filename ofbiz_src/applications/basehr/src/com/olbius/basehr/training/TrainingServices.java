package com.olbius.basehr.training;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.common.services.CommonServices;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;
import com.olbius.common.util.EntityMiscUtil;


public class TrainingServices {
	public static final String module = TrainingServices.class.getName();
	public static final String resource = "BaseHRTrainingUiLabels";
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTrainingCourseList(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	listSortFields.add("-createdStamp");
    	try {
    		listIterator = delegator.find("TrainingCourse", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingForEmplRegister(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	listSortFields.add("-fromDate");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String partyId = userLogin.getString("partyId");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
    	try {
    		EntityCondition statusCond = EntityCondition.makeCondition("statusId", "TRAINING_PLANNED_ACC");
    		EntityCondition cond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("isPublic", "Y"), EntityJoinOperator.AND, statusCond);
    		List<GenericValue> trainingCoursePartyAttendances = delegator.findByAnd("TrainingCoursePartyAttendance", UtilMisc.toMap("partyId", partyId), null, false); 
    		if(UtilValidate.isNotEmpty(trainingCoursePartyAttendances)){
    			List<String> trainingCourseList = EntityUtil.getFieldListFromEntityList(trainingCoursePartyAttendances, "trainingCourseId", true);
    			cond1 = EntityCondition.makeCondition(cond1, EntityJoinOperator.OR, EntityCondition.makeCondition("trainingCourseId", EntityJoinOperator.IN, trainingCourseList));
    		}
    		ModelEntity modelEntity = delegator.getModelEntity("TrainingCourse");
    		
    		List<String> listFieldInEntity = modelEntity.getAllFieldNames();
    		
    		List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
    		List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
    		EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
    		
    		List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			listAllConditions.add(cond1);
			listAllConditions.add(statusCond);
    		List<GenericValue> listTrainingForEmplRegis = delegator.findList("TrainingCourse", 
    				EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
    			listTrainingForEmplRegis = EntityConditionUtils.doFilterGenericValue(listTrainingForEmplRegis, condsForFieldInEntity);
    		}
    		if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("-createdDate");
			}
    		listTrainingForEmplRegis = EntityUtil.orderBy(listTrainingForEmplRegis, sortedFieldInEntity);
    		
    		boolean isFilterAndSortAdvance = false;
    		if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				if(end > listTrainingForEmplRegis.size()){
					end = listTrainingForEmplRegis.size();
				}
				totalRows = listTrainingForEmplRegis.size();
				listTrainingForEmplRegis = listTrainingForEmplRegis.subList(start, end);
			}else{
				isFilterAndSortAdvance = true;
			}
    		if(end > listTrainingForEmplRegis.size()){
				end = listTrainingForEmplRegis.size();
			}
//    		totalRows = listTrainingForEmplRegis.size();
//    		if(end > totalRows){
//    			end = totalRows;
//    		}
//    		listTrainingForEmplRegis = listTrainingForEmplRegis.subList(start, end);
    		for(GenericValue tempGv: listTrainingForEmplRegis){
    			Map<String, Object> tempMap = tempGv.getAllFields();
    			listIterator.add(tempMap);
    			String trainingCourseId = tempGv.getString("trainingCourseId");
    			GenericValue trainingCoursePartyAtt = delegator.findOne("TrainingCoursePartyAttendance", UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), false);
    			if(trainingCoursePartyAtt != null){
    				String statusId = trainingCoursePartyAtt.getString("statusIdRegister");
    				tempMap.put("statusIdRegister", statusId);
    			}
    			
    		}
    		
    		if(isFilterAndSortAdvance){
    			if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
    				listIterator = EntityConditionUtils.doFilter(listIterator, condsForFieldNotInEntity);
    			}
    			if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
    				listIterator = EntityConditionUtils.sortList(listIterator, sortedFieldNotInEntity);
    			}
    			totalRows = listIterator.size();
    			if(end > totalRows){
    				end = totalRows;
    			}
    			listIterator = listIterator.subList(start, end);
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingProvider(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> list = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "TRAINING_PROVIDER", "partyRelationshipTypeId", "TRAINING_PROVIDER")), null, null, null, false);
//			list = EntityUtil.filterByDate(list);
			List<String> listPartyIds = EntityUtil.getFieldListFromEntityList(list, "partyIdTo", true);
			EntityCondition Cond1 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "TRAINING_PROVIDER");
			EntityCondition Cond2 = EntityCondition.makeCondition("partyId", EntityOperator.IN, listPartyIds);
			List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
			List<GenericValue> providerTrainingList = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("groupName"), null, false);
//			totalRows = providerTrainingList.size();
//			if(end > totalRows){
//				end = totalRows;
//			}
//			providerTrainingList = providerTrainingList.subList(start, end);
			
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("partyId");
			listFieldInEntity.add("groupName");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				providerTrainingList = EntityConditionUtils.doFilterGenericValue(providerTrainingList, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("partyId");
			}
			providerTrainingList = EntityUtil.orderBy(providerTrainingList, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = providerTrainingList.size();
				if(end > providerTrainingList.size()){
					end = providerTrainingList.size();
				}
				providerTrainingList = providerTrainingList.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > providerTrainingList.size()){
				end  = providerTrainingList.size();
			}
			
			Map<String, Object> ctxMap = FastMap.newInstance();
			Map<String, Object> resultService = null;
			ctxMap.put("userLogin", userLogin);
			for(GenericValue tempGv: providerTrainingList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				resultService = dispatcher.runSync("getPartyPostalAddress", ctxMap);
				String contactMechId = (String)resultService.get("contactMechId");
				if(contactMechId != null){
					GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
					tempMap.put("postalAddress", CommonUtil.getPostalAddressDetails(delegator, contactMechId));
					tempMap.put("addressContactMechId", contactMechId);
					tempMap.put("countryGeoId", postalAddr.get("countryGeoId"));
					tempMap.put("stateProvinceGeoId", postalAddr.get("stateProvinceGeoId"));
					tempMap.put("districtGeoId", postalAddr.get("districtGeoId"));
					tempMap.put("wardGeoId", postalAddr.get("wardGeoId"));
					tempMap.put("address1", postalAddr.get("address1"));
				}
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				resultService = dispatcher.runSync("getPartyEmail", ctxMap);
				contactMechId = (String)resultService.get("contactMechId");
				if(contactMechId != null){
					tempMap.put("emailAddress", resultService.get("emailAddress"));
					tempMap.put("emailContactMechId", contactMechId);
				}
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				resultService = dispatcher.runSync("getPartyTelephone", ctxMap);
				contactMechId = (String)resultService.get("contactMechId");
				if(contactMechId != null){
					tempMap.put("phoneContactMechId", contactMechId);
					StringBuilder telephoneNbr = new StringBuilder();
					if(resultService.get("countryCode") != null){
						telephoneNbr.append(resultService.get("countryCode"));
						tempMap.put("countryCode", resultService.get("countryCode"));
					}
					if(resultService.get("areaCode") != null){
						telephoneNbr.append(" ");
						telephoneNbr.append(resultService.get("areaCode"));
						tempMap.put("areaCode", resultService.get("areaCode"));
					}
					if(resultService.get("contactNumber") != null){
						telephoneNbr.append(" ");
						telephoneNbr.append(resultService.get("contactNumber"));
						tempMap.put("contactNumber", resultService.get("contactNumber"));
					}
					
					tempMap.put("primaryPhone", telephoneNbr.toString());
				}
				
				List<GenericValue> websiteUrlList = delegator.findByAnd("PartyContactWithPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_WEB_URL"), null, false);
				websiteUrlList = EntityUtil.filterByDate(websiteUrlList, UtilDateTime.nowTimestamp(), "purposeFromDate", "purposeThruDate", true);
				websiteUrlList = EntityUtil.filterByDate(websiteUrlList, UtilDateTime.nowTimestamp(), "contactFromDate", "contactThruDate", true);
				if(UtilValidate.isNotEmpty(websiteUrlList)){
					GenericValue contactMech  = websiteUrlList.get(0);
					tempMap.put("websiteContactMechId", contactMech.get("contactMechId"));
					tempMap.put("websiteUrl", contactMech.get("infoString"));
				}
				tempMap.put("partyId", partyId);
				tempMap.put("groupName", tempGv.get("groupName"));
				
				List<GenericValue> listRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "TRAINING_PROVIDER", "partyRelationshipTypeId", "TRAINING_PROVIDER")), null, null, null, false);
				if (!listRels.isEmpty()){
					tempMap.put("fromDate", listRels.get(0).getString("fromDate"));
					tempMap.put("thruDate", listRels.get(0).getString("thruDate"));
				}
				listReturn.add(tempMap);
			}
			if(isFilterAdvance){
				if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
					listReturn = EntityConditionUtils.doFilter(listReturn, condsForFieldNotInEntity);
				}
				if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
					listReturn = EntityConditionUtils.sortList(listReturn, sortedFieldNotInEntity);
				}
				totalRows = listReturn.size();
				if(end > listReturn.size()){
					end = listReturn.size();
				}
				listReturn = listReturn.subList(start, end);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingCoursePartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String trainingCourseId = parameters.get("trainingCourseId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
    		listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingPartyExpectedAttJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listAllConditions.add(EntityCondition.makeCondition("isExpectedAttend", Boolean.TRUE));
			listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmployeeAttendanceTrainingJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "TCR_ATTENDANCE"));
			listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplRegisTrainingJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listAllConditions.add(EntityCondition.makeCondition("isRegister", Boolean.TRUE));
			listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplRegisAccTraining(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listAllConditions.add(EntityCondition.makeCondition("isRegister", Boolean.TRUE));
			listAllConditions.add(EntityCondition.makeCondition("statusIdRegister", "TCR_REGIS_ACC"));
			listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListTrainingCourseSkillType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listIterator = delegator.find("TrainingCourseAndSkillType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> createTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		/*Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Timestamp nowDayStart = UtilDateTime.getDayStart(nowTimestamp);
		Timestamp nowDayEnd = UtilDateTime.getDayEnd(nowTimestamp);*/
		Map<String, Object> retMap = FastMap.newInstance();
		String trainingCourseCode = (String)context.get("trainingCourseCode");
		try {
			List<GenericValue> checkEtt = delegator.findByAnd("TrainingCourse", UtilMisc.toMap("trainingCourseCode", trainingCourseCode), null, false);
			if(UtilValidate.isNotEmpty(checkEtt)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseIsExists", UtilMisc.toMap("trainingCourseId", trainingCourseCode), locale));
			}
			GenericValue trainingCourse = delegator.makeValue("TrainingCourse");
			String trainingCourseId = delegator.getNextSeqId("TrainingCourse");
			trainingCourse.setNonPKFields(context);
			trainingCourse.put("statusId", "TRAINING_PLANNED");
			trainingCourse.put("createdDate", UtilDateTime.nowTimestamp());
			trainingCourse.set("trainingCourseId", trainingCourseId);
			trainingCourse.create();
		
			retMap.put("trainingCourseId", trainingCourseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> editTrainingCoursePurpose(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		List<String> trainingPurposeTypeIds = (List<String>)context.get("trainingPurposeTypeIds");
		String trainingCourseId = (String)context.get("trainingCourseId");
		try {
			List<GenericValue> trainingCoursePurposeList = delegator.findByAnd("TrainingCoursePurpose", UtilMisc.toMap("trainingCourseId", trainingCourseId), null, false);
			for(GenericValue tempGv: trainingCoursePurposeList){
				String trainingPurposeTypeId = tempGv.getString("trainingPurposeTypeId");
				if(!trainingPurposeTypeIds.contains(trainingPurposeTypeId)){
					tempGv.remove();
				}
			}
			List<String> trainingCoursePurposeIdExistsed = EntityUtil.getFieldListFromEntityList(trainingCoursePurposeList, "trainingPurposeTypeId", false);
			for(String tempTrainingPurposeTypeId: trainingPurposeTypeIds){
				if(!trainingCoursePurposeIdExistsed.contains(tempTrainingPurposeTypeId)){
					GenericValue trainingCoursePurpose = delegator.makeValue("TrainingCoursePurpose");
					trainingCoursePurpose.put("trainingCourseId", trainingCourseId);
					trainingCoursePurpose.put("trainingPurposeTypeId", tempTrainingPurposeTypeId);
					trainingCoursePurpose.create(); 
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String trainingCourseId = (String)context.get("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseNotFound", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			trainingCourse.setNonPKFields(context);
			trainingCourse.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> summaryTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String trainingCourseId = (String)context.get("trainingCourseId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseNotFound", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			String statusId = trainingCourse.getString("statusId");
			if(!"TRAINING_PLANNED_ACC".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotSummaryInStatus", UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
			Map<String, Object> resultService = dispatcher.runSync("updateTrainingCourse", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createTrainingCoursePartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue trainingCoursePartyAttendance = delegator.makeValue("TrainingCoursePartyAttendance");
			trainingCoursePartyAttendance.setAllFields(context, false, null, null);
			trainingCoursePartyAttendance.put("createdDate", UtilDateTime.nowTimestamp());
			delegator.createOrStore(trainingCoursePartyAttendance);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingCoursePurpose(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue trainingCoursePurpose = delegator.makeValue("TrainingCoursePurpose");
			trainingCoursePurpose.setAllFields(context, false, null, null);
			trainingCoursePurpose.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createTrainingPartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue trainingCoursePartyAttendance = delegator.makeValue("TrainingCoursePartyAttendance");
		trainingCoursePartyAttendance.setAllFields(context, false, null, null);
		try {
			trainingCoursePartyAttendance.put("createdDate", UtilDateTime.nowTimestamp());
			delegator.createOrStore(trainingCoursePartyAttendance);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updateTrainingPartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue trainingCoursePartyAttendance = delegator.findOne("TrainingCoursePartyAttendance", 
					UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), false);
			if(trainingCoursePartyAttendance == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindPartyInTrainingCourse", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId), (Locale)context.get("locale")));
			}
			trainingCoursePartyAttendance.setNonPKFields(context);
			trainingCoursePartyAttendance.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> deleteTrainingPartyAttendance(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError("cannot find training course");
			}
			GenericValue trainingCoursePartyAttendance = delegator.findOne("TrainingCoursePartyAttendance", 
					UtilMisc.toMap("trainingCourseId", trainingCourseId, "partyId", partyId), false);
			if(trainingCoursePartyAttendance == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindPartyInTrainingCourse", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId), (Locale)context.get("locale")));
			}
			String statusId = trainingCourse.getString("statusId");
			if("TRAINING_PLANNED".equals(statusId) || "TRAINING_PLANNED_REJ".equals(statusId)){
				trainingCoursePartyAttendance.remove();
			}else if("TRAINING_PLANNED_ACC".equals(statusId) || "TRAINING_SUMMARY".equals(statusId)){
				Boolean isExpectedAttend = trainingCoursePartyAttendance.getBoolean("isExpectedAttend");
				Boolean isRegister = trainingCoursePartyAttendance.getBoolean("isRegister");
				if((isRegister != null && isRegister) || (isExpectedAttend != null && isExpectedAttend)){
					trainingCoursePartyAttendance.set("statusId", null);
					trainingCoursePartyAttendance.set("resultTypeId", null);
					trainingCoursePartyAttendance.set("employeeAmount", null);
					trainingCoursePartyAttendance.set("employerAmount", null);
					trainingCoursePartyAttendance.set("employeePaid", null);
					trainingCoursePartyAttendance.set("comment", null);
					trainingCoursePartyAttendance.set("isExpectedAttend", isExpectedAttend);
					trainingCoursePartyAttendance.set("isRegister", isRegister);
					trainingCoursePartyAttendance.store();
				}else{
					trainingCoursePartyAttendance.remove();
				}
			}else{
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotDeletePartyToTrainingInStatus",
						UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createTrainingCourseSkillType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue trainingCourseSkillType = delegator.makeValue("TrainingCourseSkillType");
			trainingCourseSkillType.setAllFields(context, false, null, null);
			delegator.createOrStore(trainingCourseSkillType);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> updateTrainingCourseSkillType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String skillTypeId = (String)context.get("skillTypeId");
		try {
			GenericValue trainingCourseSkillType = delegator.findOne("TrainingCourseSkillType", 
					UtilMisc.toMap("trainingCourseId", trainingCourseId, "skillTypeId", skillTypeId), false);
			if(trainingCourseSkillType == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindSkillTypeInTrainingCourse", UtilMisc.toMap("skillTypeId", skillTypeId, "trainingCourseId", trainingCourseId), (Locale)context.get("locale")));
			}
			trainingCourseSkillType.setNonPKFields(context);
			trainingCourseSkillType.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> deleteTrainingCourseSkillType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String skillTypeId = (String)context.get("skillTypeId");
		try {
			GenericValue trainingCourseSkillType = delegator.findOne("TrainingCourseSkillType", 
					UtilMisc.toMap("trainingCourseId", trainingCourseId, "skillTypeId", skillTypeId), false);
			if(trainingCourseSkillType == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindSkillTypeInTrainingCourse", UtilMisc.toMap("skillTypeId", skillTypeId, "trainingCourseId", trainingCourseId), (Locale)context.get("locale")));
			}
			trainingCourseSkillType.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createPartyTrainingProvider(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ctxMap = FastMap.newInstance();
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDateTime"))){
			Long fromDateStr = new Long((String)context.get("fromDateTime"));
			fromDate = UtilDateTime.getDayStart(new Timestamp(fromDateStr));
			context.put("fromDate", fromDate);
		}
		if (UtilValidate.isNotEmpty(context.get("thruDateTime"))){
			Long thruDateStr = new Long((String)context.get("thruDateTime"));
			thruDate = UtilDateTime.getDayEnd(new Timestamp(thruDateStr));
			context.put("thruDate", thruDate);
		}
		
		Locale locale = (Locale)context.get("locale");
		try {
			ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyGroup", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("createPartyGroup", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			} 
			String partyId = (String)resultService.get("partyId");
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "TRAINING_PROVIDER", "userLogin", userLogin));
			ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyPostalAddress", context, userLogin, timeZone, locale);
			ctxMap.put("city", stateProvinceGeoId);
			ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			ctxMap.put("postalCode", "10000");
			ctxMap.put("partyId", partyId);
			dispatcher.runSync("createPartyPostalAddress", ctxMap);
			if(context.get("emailAddress") != null){
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyEmailAddress", context, userLogin, timeZone, locale);
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				ctxMap.put("partyId", partyId);
				resultService = dispatcher.runSync("createPartyEmailAddress", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			if(context.get("contactNumber") != null){
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyTelecomNumber", context, userLogin, timeZone, locale);
				ctxMap.put("partyId", partyId);
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				resultService = dispatcher.runSync("createPartyTelecomNumber", ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			
			String websiteUrl = (String)context.get("websiteUrl");
			if(websiteUrl != null && websiteUrl.length() > 0){
				resultService = CommonServices.createPartyContactMech(delegator, dispatcher, userLogin, partyId, websiteUrl, "PRIMARY_WEB_URL", "WEB_ADDRESS");
				if(resultService != null && !ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			ctxMap = FastMap.newInstance();
			ctxMap.put("partyIdFrom", company);
			ctxMap.put("partyIdTo", partyId);
			ctxMap.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			ctxMap.put("roleTypeIdTo", "TRAINING_PROVIDER");
			ctxMap.put("partyRelationshipTypeId", "TRAINING_PROVIDER");
			ctxMap.put("userLogin", userLogin);
			ctxMap.put("fromDate", fromDate);
			ctxMap.put("thruDate", thruDate);
			dispatcher.runSync("createPartyRelationship", ctxMap);
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updatePartyTrainingProvider(DispatchContext dctx, Map<String, Object> context){
		//Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String fromDateStr = (String)context.get("fromDateTime");
		String thruDateStr = (String)context.get("thruDateTime");
		Timestamp fromDate = null, thruDate = null;
		if(fromDateStr != null){
			fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
		}
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		try {
			Map<String, Object> ctxMap = FastMap.newInstance();
			//update partyGroup
			ctxMap = ServiceUtil.setServiceFields(dispatcher, "updatePartyGroup", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("updatePartyGroup", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			//update postalAddress
			String addressContactMechId = (String)context.get("addressContactMechId");
			String serviceName = "createPartyPostalAddress";
			ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyPostalAddress", context, userLogin, timeZone, locale);
			if(addressContactMechId != null){
				serviceName = "updatePartyPostalAddress";
				ctxMap.put("contactMechId", addressContactMechId);
			}
			ctxMap.put("city", stateProvinceGeoId);
			ctxMap.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
			ctxMap.put("postalCode", "10000");
			resultService = dispatcher.runSync(serviceName, ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			//update email
			if(context.get("emailAddress") != null){
				String emailContactMechId = (String)context.get("emailContactMechId");
				serviceName = "createPartyEmailAddress";
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyEmailAddress", context, userLogin, timeZone, locale);
				if(emailContactMechId != null){
					serviceName = "updatePartyEmailAddress";
					ctxMap.put("contactMechId", emailContactMechId);
				}
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
				resultService = dispatcher.runSync(serviceName, ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			//update telephone
			if(context.get("contactNumber") != null){
				String phoneContactMechId = (String)context.get("phoneContactMechId");
				serviceName = "createPartyTelecomNumber";
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyTelecomNumber", context, userLogin, timeZone, locale);
				if(phoneContactMechId != null){
					serviceName = "updatePartyTelecomNumber";
					ctxMap.put("contactMechId", phoneContactMechId);
				}
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				resultService = dispatcher.runSync(serviceName, ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			
			if(context.get("websiteUrl") != null){
				String websiteUrl = (String)context.get("websiteUrl");
				String websiteContactMechId = (String)context.get("websiteContactMechId");
				serviceName = "createPartyContactMech";
				ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyContactMech", context, userLogin, timeZone, locale);
				if(websiteContactMechId != null){
					serviceName = "updatePartyContactMech";
					ctxMap.put("contactMechId", websiteContactMechId);
				}
				ctxMap.put("contactMechPurposeTypeId", "PRIMARY_WEB_URL");
				ctxMap.put("contactMechTypeId", "WEB_ADDRESS");
				ctxMap.put("infoString", websiteUrl);
				resultService = dispatcher.runSync(serviceName, ctxMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
			}
			//update partyRelationship
			String partyIdFrom = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<EntityCondition> condition = FastList.newInstance();
			condition.add(EntityCondition.makeCondition("partyIdTo", partyId));
			condition.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
			condition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "TRAINING_PROVIDER"));
			List<GenericValue> partyRelList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(EntityCondition.makeCondition(condition), 
																													EntityJoinOperator.AND,
																													EntityCondition.makeCondition("fromDate", fromDate)), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyRelList)){
				GenericValue partyRel = partyRelList.get(0);
				partyRel.set("thruDate", thruDate);
				partyRel.store();
			}else{
				List<GenericValue> partyRelListDel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condition), null, null, null, false);
				for(GenericValue tempGv: partyRelListDel){
					tempGv.remove();
				}
				Map<String, Object> partyRelMap = FastMap.newInstance();
				partyRelMap.put("partyIdFrom", partyIdFrom);
				partyRelMap.put("partyIdTo", partyId);
				partyRelMap.put("fromDate", fromDate);
				partyRelMap.put("thruDate", thruDate);
				partyRelMap.put("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
				partyRelMap.put("roleTypeIdTo", "TRAINING_PROVIDER");
				partyRelMap.put("partyRelationshipTypeId", "TRAINING_PROVIDER");
				partyRelMap.put("userLogin", userLogin);
				dispatcher.runSync("createPartyRelationship", partyRelMap);
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updateEmplRegisterTraining(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String isRegisted = (String)context.get("isRegisted");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String trainingCourseId = (String)context.get("trainingCourseId");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String key = null;
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "NotFindTrainingCourseToRegister", locale));
			}
			boolean isAllowRegister = TrainingHelper.checkPartyRegisterTraining(delegator, trainingCourseId, partyId);
			if(!isAllowRegister){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "YouNotAllowedRegisterTraining", locale));
			}
			GenericValue trainingCoursePartyAttendance = delegator.findOne("TrainingCoursePartyAttendance", UtilMisc.toMap("partyId", partyId, "trainingCourseId", trainingCourseId), false);
			if(trainingCoursePartyAttendance == null){
				trainingCoursePartyAttendance = delegator.makeValue("TrainingCoursePartyAttendance");
				trainingCoursePartyAttendance.put("partyId", partyId);
				trainingCoursePartyAttendance.put("trainingCourseId", trainingCourseId);
				trainingCoursePartyAttendance.put("createdDate", UtilDateTime.nowTimestamp());
			}
			String currStatus = trainingCoursePartyAttendance.getString("statusIdRegister");
			Timestamp registerFromDate = trainingCourse.getTimestamp("registerFromDate");
			Timestamp registerThruDate = trainingCourse.getTimestamp("registerThruDate");
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String statusId = null;
			if("Y".equals(isRegisted)){
				statusId = "TCR_REGIS";
			}else if("N".equals(isRegisted)){
				statusId = "TCR_CANCEL_REGIS";
			}else{
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "EmplStatusRegisterTrainingNotValid", locale));
			}
			if("TCR_REGIS".equals(statusId)){
				if("TCR_REGIS".equals(currStatus) ){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "YouRegisteredThisTraining", locale));
				}else if("TCR_REGIS_ACC".equals(currStatus)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "YouRegisteredIsAccept", locale));
				}else if("TCR_REGIS_REJ".equals(currStatus)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "YouRegisteredIsReject", locale));
				}
				if(nowTimestamp.before(registerFromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingRegisterNotBegin", 
							UtilMisc.toMap("registerFromDate", DateUtil.getDateMonthYearDesc(registerFromDate)), locale));
				}
				if(nowTimestamp.after(registerThruDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingRegisterEnd", 
							UtilMisc.toMap("registerThruDate", DateUtil.getDateMonthYearDesc(registerThruDate)), locale));
				}
				trainingCoursePartyAttendance.set("dateRegisted", UtilDateTime.nowTimestamp());
				trainingCoursePartyAttendance.set("isRegister", Boolean.TRUE);
				key = "TrainingRegisterSuccessful";
			}else if("TCR_CANCEL_REGIS".equals(statusId)){
				String isCancelRegister = trainingCourse.getString("isCancelRegister");
				if(!"Y".equals(isCancelRegister)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "TrainingCourseNotAllowCancel", locale));
				}
				if("TCR_CANCEL_REGIS".equals(currStatus)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "YouCancelledRegisterThisTraining", locale));
				}else if("TCR_REGIS_REJ".equals(currStatus)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotCancelRegisterWhenReject", locale));
				}
				Timestamp fromDate = trainingCourse.getTimestamp("fromDate");
				Long cancelBeforeDay = trainingCourse.getLong("cancelBeforeDay");
				Timestamp deadlineCancelRegister = UtilDateTime.getDayEnd(fromDate, -cancelBeforeDay);
				if(nowTimestamp.after(deadlineCancelRegister)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotCancelRegisterAfterDate",
							UtilMisc.toMap("deadlineCancelRegister", DateUtil.getDateMonthYearDesc(deadlineCancelRegister)), locale));
				}
				key = "TrainingCancelRegisterSuccessful";
			}
			trainingCoursePartyAttendance.set("statusIdRegister", statusId);
			delegator.createOrStore(trainingCoursePartyAttendance);
			//send notify to HRADMIN
			TrainingHelper.sendNotifyApprTrainingRegister(dispatcher, delegator, partyId, statusId, trainingCourseId, trainingCourse.getString("trainingCourseName"), userLogin, locale, timeZone);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRTrainingUiLabels", key, locale));
	}
	
	public static Map<String, Object> sendApprRequestTraining(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String trainingCourseId = (String)context.get("trainingCourseId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindTrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			String statusId = trainingCourse.getString("statusId");
			if(!"TRAINING_PLANNED".equals(statusId) && !"TRAINING_PLANNED_REJ".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotSendApprReqWhenStatusInvalid", UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
			trainingCourse.set("statusId", "TRAINING_PLANNED_PPS");
			trainingCourse.store();
			String header = UtilProperties.getMessage("BaseHRTrainingUiLabels", "ApprovalTrainingCourse", 
					UtilMisc.toMap("trainingCourseName", trainingCourse.get("trainingCourseName")), locale);
			CommonUtil.sendNotifyByRoles(dispatcher, locale, PropertiesUtil.HRM_ROLE, userLogin, header, "ViewTrainingDetail", "trainingCourseId=" + trainingCourseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "SendRequestApprSuccessfully", locale));
		return retMap;
	}
	
	public static Map<String, Object> approvalTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		String statusId = (String)context.get("statusId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindTrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			String currStatusId = trainingCourse.getString("statusId");
			if(!"TRAINING_PLANNED_PPS".equals(currStatusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotApprTrainingWhenStatusInvalid", UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
			trainingCourse.set("statusId", statusId);
			trainingCourse.store();
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createTrainingCourseStatus", context, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("createTrainingCourseStatus", ctxMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgapprovesuccess", locale));
		return retMap;
	}
	
	public static Map<String, Object> createTrainingCourseStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String statusUserLogin = (String)context.get("statusUserLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		if(statusUserLogin == null){
			statusUserLogin = userLogin.getString("userLoginId");
		}
		GenericValue trainingCourseStatus = delegator.makeValue("TrainingCourseStatus");
		trainingCourseStatus.setNonPKFields(context);
		trainingCourseStatus.set("statusUserLogin", statusUserLogin);
		String trainingCourseStatusId = delegator.getNextSeqId("TrainingCourseStatus");
		trainingCourseStatus.set("trainingCourseStatusId", trainingCourseStatusId);
		trainingCourseStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
		try {
			delegator.create(trainingCourseStatus);
			retMap.put("trainingCourseStatusId", trainingCourseStatusId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> completeTrainingCourse(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String trainingCourseId = (String)context.get("trainingCourseId");
		try {
			GenericValue trainingCourse = delegator.findOne("TrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), false);
			if(trainingCourse == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotFindTrainingCourse", UtilMisc.toMap("trainingCourseId", trainingCourseId), locale));
			}
			String statusId = trainingCourse.getString("statusId");
			if(!"TRAINING_SUMMARY".equals(statusId)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRTrainingUiLabels", "CannotUpdateInStatus", UtilMisc.toMap("status", statusItem.get("description")), locale));
			}
			trainingCourse.set("statusId", "TRAINING_COMPLETED");
			trainingCourse.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
	
	public static Map<String, Object> thruTrainingProvider(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		try {
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> list = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", company, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo", "TRAINING_PROVIDER", "partyRelationshipTypeId", "TRAINING_PROVIDER")), null, null, null, false);
			list = EntityUtil.filterByDate(list);
			if (!list.isEmpty()){
				for (GenericValue item : list) {
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQListTrainingPartyRegisted(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String trainingCourseId = parameters.get("trainingCourseId")[0];
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listAllConditions.add(EntityCondition.makeCondition("trainingCourseId", trainingCourseId));
			listAllConditions.add(EntityCondition.makeCondition("isRegister", Boolean.TRUE));
			listAllConditions.add(EntityCondition.makeCondition("statusIdRegister", "TRAINING_APPROVED"));
			listIterator = delegator.find("TrainingCourseAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getTrainingCourseList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> sendNtfTrainingCourseToParty(DispatchContext ctx, Map<String, Object> context){
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> inputNtf = FastMap.newInstance();
		String trainingCourseId = (String) context.get("trainingCourseId");
		String header = UtilProperties.getMessage(resource, "", (Locale)context.get("locale"))+ " " + UtilProperties.getMessage(resource, "TraniningCourseToRegist", (Locale)context.get("locale")).toString().toLowerCase()+  ", "+ UtilProperties.getMessage(resource, "trainingCourseId", (Locale)context.get("locale")) +": [" +trainingCourseId+"]";
		inputNtf.put("partyId", (String) context.get("partyId"));
		inputNtf.put("action", "ViewTrainingDetail?trainingCourseId="+trainingCourseId);
		inputNtf.put("ntfType", "ONE");
		inputNtf.put("userLogin", (GenericValue) context.get("userLogin"));
		inputNtf.put("targetLink", "");
		inputNtf.put("header", header);
		try {
			dispatcher.runSync("createNotification", inputNtf);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			Debug.logError(e, e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: sendNtfTrainingCourseToParty error! " + e.toString());
		}
		return successResult;
	}
	
	@SuppressWarnings("unused")
	public static Map<String, Object> getAllEmployeeInOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = (String) userLogin.get("userLoginId");
		String partyGroupId = "";
		Organization buildOrg;
		Calendar cal = Calendar.getInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		
		try {
			partyGroupId = PartyUtil.getCurrentOrganization(delegator, userLoginId);
			buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("You don't manage organization " + PartyHelper.getPartyName(delegator, partyGroupId, false));
		}
		try {
			cal = Calendar.getInstance();
			java.sql.Date nowDate = new java.sql.Date(cal.getTimeInMillis());
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator, null, null, null, null);
			for(GenericValue employeeGv: emplList){
				Map<String, Object> tempMap = employeeGv.getAllFields();
				Timestamp dateJoinCompany = employeeGv.getTimestamp("dateJoinCompany");
				String department = employeeGv.getString("department");
				String emplPositionType = employeeGv.getString("emplPositionType");
				department = CommonUtil.cleanJoinStringValue(department, ",");
				emplPositionType = CommonUtil.cleanJoinStringValue(emplPositionType, ",");
				tempMap.put("emplPositionType", emplPositionType);
				tempMap.put("department", department);
				if(dateJoinCompany != null){
					int seniorityMonth = DateUtil.getMonthBetweenTwoDate(new java.sql.Date(dateJoinCompany.getTime()), nowDate);
					if(seniorityMonth > 12){
						seniorityMonth = 12;
					}
					tempMap.put("seniorityMonth", seniorityMonth);
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		successResult.put("listReturn", listReturn);
		return successResult;
	}
}

