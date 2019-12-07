package com.olbius.basehr.insurance.services;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.insurance.helper.InsuranceHelper;
import com.olbius.basehr.insurance.worker.InsuranceWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class InsuranceServices {
	
	public static final String resource = "BaseHRUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		//GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		//Locale locale = (Locale)context.get("locale");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			List<EntityCondition> listConds = FastList.newInstance();
			if(UtilValidate.isEmpty(emplListId)){
				return ServiceUtil.returnError("have no employee");
			}
			listConds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId));
			listConds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			List<GenericValue> partyInsuranceSalaryList = delegator.findList("PartyInsuranceSalaryAndParty", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, UtilMisc.toList("partyId", "-fromDate"), null, false);
			
			List<String> listFieldInEntity = EntityConditionUtils.getFieldListInEntity(delegator, "PartyInsuranceSalaryAndParty");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				partyInsuranceSalaryList = EntityConditionUtils.doFilterGenericValue(partyInsuranceSalaryList, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(listSortFields)){
				sortedFieldInEntity.add("firstName");
			}
			partyInsuranceSalaryList = EntityUtil.orderBy(partyInsuranceSalaryList, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = partyInsuranceSalaryList.size();
				if(end > partyInsuranceSalaryList.size()){
					end = partyInsuranceSalaryList.size();
				}
				partyInsuranceSalaryList = partyInsuranceSalaryList.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > partyInsuranceSalaryList.size()){
				end  = partyInsuranceSalaryList.size();
			}
			
			String defaultUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			for(GenericValue partyInsuranceSalary: partyInsuranceSalaryList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = partyInsuranceSalary.getString("partyId");
				List<String> listDepartmentId = PartyUtil.getDepartmentOfEmployee(delegator, tempPartyId, fromDate, thruDate);
				List<String> listDepartmentName = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", listDepartmentId, "partyId", "groupName");
				List<GenericValue> emplPositionTypeList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, tempPartyId, fromDate, thruDate);
				List<String> emplPositionTypeListDesc = EntityUtil.getFieldListFromEntityList(emplPositionTypeList, "description", true);
				String uomId = partyInsuranceSalary.getString("uomId");
				if(uomId == null){
					uomId = defaultUomId;
				}
				Timestamp tempThruDate = partyInsuranceSalary.getTimestamp("thruDate");
				if(tempThruDate != null){
					tempMap.put("thruDate", tempThruDate.getTime());
				}
				tempMap.put("fromDate", partyInsuranceSalary.getTimestamp("fromDate").getTime());
				tempMap.put("department", StringUtils.join(listDepartmentName, ", "));
				tempMap.put("emplPositionType", StringUtils.join(emplPositionTypeListDesc, ", "));
				tempMap.put("fullName", partyInsuranceSalary.get("fullName"));
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyCode", partyInsuranceSalary.get("partyCode"));
				tempMap.put("periodTypeId", partyInsuranceSalary.getString("periodTypeId"));
				tempMap.put("amount", partyInsuranceSalary.getBigDecimal("amount"));
				tempMap.put("uomId", uomId);
				tempMap.put("partyInsSalId", partyInsuranceSalary.getString("partyInsSalId"));
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
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		retMap.put("listIterator", listReturn);
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> getHospitalByGeo(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "RetrieveHospitalListSuccess", locale));
		Delegator delegator = dctx.getDelegator();
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		EntityCondition cond = EntityCondition.makeCondition("stateProvinceGeoId", stateProvinceGeoId);
		List<GenericValue> hospitalList;
		
		try {
			hospitalList = delegator.findList("HospitalAndPostalAddress", cond, UtilMisc.toSet("hospitalId", "hospitalName", "hospitalCode"), 
					UtilMisc.toList("hospitalName"), null, false);
			retMap.put("listReturn", hospitalList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getPartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		try {
			List<GenericValue> partyInsuranceSalList = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceSalList)){
				List<String> partyInsSalIdList = EntityUtil.getFieldListFromEntityList(partyInsuranceSalList, "partyInsSalId", true);
				retMap.put("partyInsSalId", partyInsSalIdList);
			}
			retMap.put("listPartyInsSal", partyInsuranceSalList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> deletePartyInsuranceAllowancePayment(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String insAllowancePaymentDeclId = (String)context.get("insAllowancePaymentDeclId");
		String emplLeaveId = (String)context.get("emplLeaveId");
		try {
			GenericValue partyInsuranceAllowancePayment = delegator.findOne("PartyInsuranceAllowancePayment", UtilMisc.toMap("insAllowancePaymentDeclId", insAllowancePaymentDeclId, "emplLeaveId", emplLeaveId), false);
			if(partyInsuranceAllowancePayment == null){
				return ServiceUtil.returnError("cannot find partyInsuranceAllowancePayment to delete");
			}
			partyInsuranceAllowancePayment.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> updatePartyInsuranceAllowancePayment(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String insAllowancePaymentDeclId = (String)context.get("insAllowancePaymentDeclId");
		String emplLeaveId = (String)context.get("emplLeaveId");
		try {
			GenericValue partyInsuranceAllowancePayment = delegator.findOne("PartyInsuranceAllowancePayment", UtilMisc.toMap("insAllowancePaymentDeclId", insAllowancePaymentDeclId, "emplLeaveId", emplLeaveId), false);
			if(partyInsuranceAllowancePayment == null){
				return ServiceUtil.returnError("cannot find partyInsuranceAllowancePayment to update");
			}
			partyInsuranceAllowancePayment.setNonPKFields(context);
			partyInsuranceAllowancePayment.store();
			String insuranceSocialNbr = (String)context.get("insuranceSocialNbr");
			if(insuranceSocialNbr != null){
				GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
				String partyId = emplLeave.getString("partyId");
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				person.set("insuranceSocialNbr", insuranceSocialNbr);
				person.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createPartyParticipateInsurance(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();		
		GenericValue partyParticipateInsurance = delegator.makeValue("PartyParticipateInsurance");
		partyParticipateInsurance.setAllFields(context, false, null, null);
		try {
			partyParticipateInsurance.create();
			//InsuranceHelper.updatePartyParticipateInsuranceAfterCreate(dctx, context, partyParticipateInsurance);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInsuranceHealthListJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		if(fromDateStr != null){
			Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
									EntityJoinOperator.OR,
									EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
		}
		if(thruDateStr != null){
			Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
			thruDate = UtilDateTime.getDayEnd(thruDate);
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		}
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//listSortFields.add("reportId ASC");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listIterator = delegator.find("PartyHealthInsuranceAndHospitalPerson", tmpCond, null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplPosTypeInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		if(fromDateStr != null){
			Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																EntityJoinOperator.OR,
																EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate)));
		}
		if(thruDateStr != null){
			Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
		}
		try {
			listIterator = delegator.find("EmplPositionTypeAndInsuranceSalary", tmpCond, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getEmplPosTypeInsSalaryInPeriod(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		List<EntityCondition> conds = FastList.newInstance();
		try {
			List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
			for(GenericValue emplPos: emplPosList){
				conds.clear();
				Timestamp tmpFromDate = emplPos.getTimestamp("fromDate");
				Timestamp tmpThruDate = emplPos.getTimestamp("thruDate");
				if(tmpFromDate.before(fromDate)){
					tmpFromDate = fromDate;
				}
				if(thruDate != null && (tmpThruDate == null || tmpThruDate.after(thruDate))){
					tmpThruDate = thruDate;
				}
				String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
				conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
				conds.add(EntityConditionUtils.makeDateConds(tmpFromDate, tmpThruDate));
				List<GenericValue> emplPosTypeInsuranceSalaryList = delegator.findList("EmplPosTypeInsuranceSalary", EntityCondition.makeCondition(conds), null, 
						UtilMisc.toList("fromDate"), null, false);
				for(GenericValue tempEmplPosTypeInsSal: emplPosTypeInsuranceSalaryList){
					Map<String, Object> tempMap = FastMap.newInstance();
					Timestamp insFromDate = tempEmplPosTypeInsSal.getTimestamp("fromDate");
					Timestamp insThruDate = tempEmplPosTypeInsSal.getTimestamp("thruDate");
					if(insFromDate.before(tmpFromDate)){
						insFromDate = tmpFromDate;
					}
					if(tmpThruDate != null && (insThruDate == null || insThruDate.after(tmpThruDate))){
						insThruDate = tmpThruDate;
					}
					String periodTypeId = tempEmplPosTypeInsSal.getString("periodTypeId");
					BigDecimal insuranceSal = InsuranceHelper.getTotalInsuranceSalary(tempEmplPosTypeInsSal);
					tempMap.put("fromDate", insFromDate);
					tempMap.put("thruDate", insThruDate);
					tempMap.put("periodTypeId", periodTypeId);
					tempMap.put("insuranceSalary", insuranceSal);
					listReturn.add(tempMap);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createPartyInsuranceHealth(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			List<GenericValue> partyInsuranceHealthCheckList = delegator.findList("PartyHealthInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceHealthCheckList)){
				GenericValue partyInsuranceHealthExists = partyInsuranceHealthCheckList.get(0);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CreateInsuranceHealthOverlapTimePeriodError", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate), "thruDate", DateUtil.getDateMonthYearDesc(thruDate),
									   "fromDateExists", DateUtil.getDateMonthYearDesc(partyInsuranceHealthExists.getTimestamp("fromDate")),
									    "thruDateExists", DateUtil.getDateMonthYearDesc(partyInsuranceHealthExists.getTimestamp("thruDate")),
									    "insHealthCard", partyInsuranceHealthExists.get("insHealthCard"),
									    "fullName", PartyUtil.getPersonName(delegator, partyId)), locale));
			}
			GenericValue partyHealthInsurance = delegator.makeValue("PartyHealthInsurance");
			String partyHealthInsId = delegator.getNextSeqId("PartyHealthInsurance");
			partyHealthInsurance.setNonPKFields(context);
			partyHealthInsurance.set("partyHealthInsId", partyHealthInsId);
			partyHealthInsurance.create();
			successResult.put("partyHealthInsId", partyHealthInsId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> updatePartyInsuranceHealth(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyHealthInsId = (String)context.get("partyHealthInsId");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("partyHealthInsId", EntityJoinOperator.NOT_EQUAL, partyHealthInsId));
		try {
			List<GenericValue> partyInsuranceHealthCheckList = delegator.findList("PartyHealthInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceHealthCheckList)){
				GenericValue partyInsuranceHealthExists = partyInsuranceHealthCheckList.get(0);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CreateInsuranceHealthOverlapTimePeriodError", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate), "thruDate", DateUtil.getDateMonthYearDesc(thruDate),
									   "fromDateExists", DateUtil.getDateMonthYearDesc(partyInsuranceHealthExists.getTimestamp("fromDate")),
									    "thruDateExists", DateUtil.getDateMonthYearDesc(partyInsuranceHealthExists.getTimestamp("thruDate")),
									    "insHealthCard", partyInsuranceHealthExists.get("insHealthCard"),
									    "fullName", PartyUtil.getPersonName(delegator, partyId)), locale));
			}
			GenericValue partyHealthInsurance = delegator.findOne("PartyHealthInsurance", UtilMisc.toMap("partyHealthInsId", partyHealthInsId), false);
			partyHealthInsurance.setNonPKFields(context);
			partyHealthInsurance.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> deletePartyInsuranceHealth(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		String insHealthCard = (String)context.get("insHealthCard");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			GenericValue partyHealthInsurance = delegator.findOne("PartyHealthInsurance", UtilMisc.toMap("partyId", partyId, "insHealthCard", insHealthCard, "fromDate", fromDate), false);
			if(partyHealthInsurance == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "NotFoundRecordToDelete", locale));
			}
			partyHealthInsurance.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplPositionTypeInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		GenericValue emplPosTypeInsuranceSalary = delegator.makeValue("EmplPosTypeInsuranceSalary");
		emplPosTypeInsuranceSalary.setAllFields(context, false, null, null);
		try {
			emplPosTypeInsuranceSalary.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInsEmplAdjustParticipateJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	Locale locale = (Locale)context.get("locale");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String monthStr = parameters.get("month") != null? parameters.get("month")[0] : null; 
    	String yearStr = parameters.get("year") != null? parameters.get("year")[0] : null;
    	String organization = parameters.get("org") != null ? parameters.get("org")[0] : null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("firstName");
    		}
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.DATE, 1);
    		if(monthStr != null){
    			cal.set(Calendar.MONTH, Integer.parseInt(monthStr));
    		}
    		if(yearStr != null){
    			cal.set(Calendar.YEAR, Integer.parseInt(yearStr));
    		}
    		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
    		Timestamp monthStart = UtilDateTime.getMonthStart(timestamp);
    		Timestamp monthEnd = UtilDateTime.getMonthEnd(timestamp, timeZone, locale);
    		List<String> childOrg = CommonUtil.getAllPartyByParent(delegator, organization);
    		listAllConditions.add(EntityCondition.makeCondition("partyGroupId", EntityOperator.IN, childOrg));
			listAllConditions.add(EntityConditionUtils.makeDateConds(monthStart, monthEnd, "salFromDate", "salThruDate"));
			listAllConditions.add(EntityCondition.makeCondition(EntityConditionUtils.makeDateConds(monthStart, monthEnd, "insHealthFromDate", "insHealthThruDate"),
									EntityJoinOperator.OR,
									EntityCondition.makeCondition(UtilMisc.toMap("insHealthFromDate", null, "insHealthThruDate", null))));
			listAllConditions.add(EntityConditionUtils.makeDateConds(monthStart, monthEnd, "participateFromDate", "participateThruDate"));
			listIterator = delegator.find("InsuranceEmplAndDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getListInsEmplParticipatingJQ(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map<String, Object> successResult = FastMap.newInstance();
    	Map<String, Object> ctx = new HashMap<String, Object>(context);
    	ctx.put("statusId", "PARTICIPATING");
    	try {
			successResult = dispatcher.runSync("JQGetListInsuranceStatus", ctx);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> getListInsSuspendJQ(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = FastMap.newInstance();
		Map<String, Object> ctx = new HashMap<String, Object>(context);
		ctx.put("statusId", "SUSPEND_PARTICIPATE");
		try {
			successResult = dispatcher.runSync("JQGetListInsuranceStatus", ctx);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInsuranceStatusJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		String monthStr = parameters.get("month") != null? parameters.get("month")[0] : null; 
		String yearStr = parameters.get("year") != null? parameters.get("year")[0] : null; 
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		String statusId = (String)context.get("statusId");
		EntityListIterator listIterator = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		Integer month = null, year = null;
		if(monthStr != null){
			month = Integer.parseInt(monthStr);
		}else{
			month = cal.get(Calendar.MONTH);
		}
		if(yearStr != null){
			year = Integer.parseInt(yearStr);
		}else{
			year = cal.get(Calendar.YEAR);
		}
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		Timestamp monthStart = UtilDateTime.getMonthStart(timestamp);
		Timestamp previousMonthStart = UtilDateTime.getMonthStart(timestamp, 0, -1);
		Timestamp previousMonthEnd = UtilDateTime.getMonthEnd(previousMonthStart, timeZone, locale);
		listAllConditions.add(EntityCondition.makeCondition("participateFromDate", EntityJoinOperator.LESS_THAN, monthStart));
		
		if(statusId != null){
			listAllConditions.add(EntityCondition.makeCondition("statusId", statusId));
		}
		listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("participateThruDate", null),
				EntityJoinOperator.OR,
				EntityCondition.makeCondition("participateThruDate", previousMonthEnd)));
		
		listAllConditions.add(EntityConditionUtils.makeDateConds(previousMonthStart, previousMonthEnd, "salFromDate", "salThruDate"));
		listAllConditions.add(EntityCondition.makeCondition(EntityConditionUtils.makeDateConds(previousMonthStart, previousMonthEnd, "insHealthFromDate", "insHealthThruDate"),
								EntityJoinOperator.OR,
								EntityCondition.makeCondition(UtilMisc.toMap("insHealthFromDate", null, "insHealthThruDate", null))));
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listIterator = delegator.find("InsuranceEmplDetailAndMaxDate", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInsReportPartyOriginateAndDetailJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		String reportId = parameters.get("reportId") != null? parameters.get("reportId")[0] : null;
		listAllConditions.add(EntityCondition.makeCondition("reportId", reportId));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-fromDateOriginate");
		}
		try {
			listIterator = delegator.find("InsReportPartyOriginateAndDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createNewInsuranceAllowancePaymentDecl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String benefitClassTypeId = (String)context.get("benefitClassTypeId");
		List<GenericValue> listinsAllowancePaymentDecl;
		try {
			listinsAllowancePaymentDecl = delegator.findByAnd("InsuranceAllowancePaymentDecl", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "benefitClassTypeId", benefitClassTypeId), UtilMisc.toList("-sequenceNum"), false);
			Long sequenceNum = 1L;
			if(UtilValidate.isNotEmpty(listinsAllowancePaymentDecl)){
				sequenceNum += listinsAllowancePaymentDecl.get(0).getLong("sequenceNum");
			}
			GenericValue insAllowancePaymentDecl = delegator.makeValue("InsuranceAllowancePaymentDecl");
			insAllowancePaymentDecl.setNonPKFields(context);
			String insAllowancePaymentDeclId = delegator.getNextSeqId("InsuranceAllowancePaymentDecl");
			insAllowancePaymentDecl.set("insAllowancePaymentDeclId", insAllowancePaymentDeclId);
			insAllowancePaymentDecl.set("sequenceNum", sequenceNum);
			insAllowancePaymentDecl.create();
			retMap.put("insAllowancePaymentDeclId", insAllowancePaymentDeclId);
		}catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyInsuranceAllowancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String insAllowancePaymentDeclId = request.getParameter("insAllowancePaymentDeclId");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		int totalRows = 0;
		Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> insAllowancePaymentDeclList = delegator.findByAnd("PartyInsuranceAllowancePayment", UtilMisc.toMap("insAllowancePaymentDeclId", insAllowancePaymentDeclId), null, false);
			
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("emplLeaveId");
			listFieldInEntity.add("insAllowancePaymentDeclId");
			listFieldInEntity.add("benefitTypeId");
			listFieldInEntity.add("insuranceSalary");
			listFieldInEntity.add("statusConditionBenefit");
			listFieldInEntity.add("totalDayLeave");
			listFieldInEntity.add("fromDateLeave");
			listFieldInEntity.add("thruDateLeave");
			listFieldInEntity.add("accumulatedLeave");
			listFieldInEntity.add("allowanceAmount");
			listFieldInEntity.add("dayLeaveConcentrate");
			listFieldInEntity.add("dayLeaveFamily");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				insAllowancePaymentDeclList = EntityConditionUtils.doFilterGenericValue(insAllowancePaymentDeclList, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("partyId");
			}
			insAllowancePaymentDeclList = EntityUtil.orderBy(insAllowancePaymentDeclList, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = insAllowancePaymentDeclList.size();
				if(end > insAllowancePaymentDeclList.size()){
					end = insAllowancePaymentDeclList.size();
				}
				insAllowancePaymentDeclList = insAllowancePaymentDeclList.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > insAllowancePaymentDeclList.size()){
				end  = insAllowancePaymentDeclList.size();
			}
			for(GenericValue tempGv: insAllowancePaymentDeclList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String emplLeaveId = tempGv.getString("emplLeaveId");
				GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
				String partyId = emplLeave.getString("partyId");
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				String gender = person.getString("gender");
				Date birthDate = person.getDate("birthDate");
				if(birthDate != null){
					if("M".equals(gender)){
						tempMap.put("genderMale", birthDate.getTime());
					}else if("F".equals(gender)){
						tempMap.put("genderFemale", birthDate.getTime());
					}
				}
				Timestamp fromDate = emplLeave.getTimestamp("fromDate");
				tempMap.put("emplLeaveId", emplLeaveId);
				tempMap.put("insuranceSocialNbr", person.getString("insuranceSocialNbr"));
				tempMap.put("insAllowancePaymentDeclId", tempGv.getString("insAllowancePaymentDeclId"));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("benefitTypeId", tempGv.getString("benefitTypeId"));
				tempMap.put("insuranceParticipatePeriod", InsuranceHelper.getDescInsParticipatePeriod(delegator, partyId, new Date(fromDate.getTime()), locale));
				tempMap.put("insuranceSalary", tempGv.get("insuranceSalary"));
				tempMap.put("statusConditionBenefit", tempGv.get("statusConditionBenefit"));
				tempMap.put("totalDayLeave", tempGv.get("totalDayLeave"));
				tempMap.put("dayLeaveFamily", tempGv.get("dayLeaveFamily"));
				tempMap.put("dayLeaveConcentrate", tempGv.get("dayLeaveConcentrate"));
				Timestamp timeConditionBenefit = tempGv.getTimestamp("timeConditionBenefit");
				if(timeConditionBenefit != null){
					tempMap.put("timeConditionBenefit", timeConditionBenefit.getTime());
				}
				Timestamp fromDateLeave = tempGv.getTimestamp("fromDateLeave");
				if(fromDateLeave != null){
					tempMap.put("fromDateLeave", fromDateLeave.getTime());
				}
				Timestamp thruDateLeave = tempGv.getTimestamp("thruDateLeave");
				if(thruDateLeave != null){
					tempMap.put("thruDateLeave", thruDateLeave.getTime());
				}
				Double accumulatedLeave = tempGv.getDouble("accumulatedLeave");
				if(accumulatedLeave != null){
					tempMap.put("accumulatedLeave", df.format(accumulatedLeave));
				}
				tempMap.put("allowanceAmount", tempGv.get("allowanceAmount"));
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
		} 
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}
	
	public static Map<String, Object> createPartyInsuranceAllowancePaymentDecl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue partyInsuranceAllowancePayment = delegator.makeValue("PartyInsuranceAllowancePayment");
		BigDecimal allowanceAmount = (BigDecimal)context.get("allowanceAmount");
		BigDecimal dayLeaveFamily = (BigDecimal)context.get("dayLeaveFamily");
		BigDecimal dayLeaveConcentrate = (BigDecimal)context.get("dayLeaveConcentrate");
		try {
			String insAllowancePaymentDeclId = (String)context.get("insAllowancePaymentDeclId");
			String emplLeaveId = (String)context.get("emplLeaveId");
			GenericValue checkEtt = delegator.findOne("PartyInsuranceAllowancePayment", UtilMisc.toMap("emplLeaveId", emplLeaveId, "insAllowancePaymentDeclId", insAllowancePaymentDeclId), false);
			if(checkEtt != null){
				//FIXME need change message
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "PartyDeclareInInsuranceAllowancePayment", UtilMisc.toMap("employeeName", emplLeaveId), locale));
			}
			partyInsuranceAllowancePayment.setAllFields(context, false, null, null);
			String benefitTypeId = (String)context.get("benefitTypeId");
			GenericValue emplLeave = delegator.findOne("EmplLeave", UtilMisc.toMap("emplLeaveId", emplLeaveId), false);
			String partyId = emplLeave.getString("partyId");
			GenericValue insuranceAllowanceBenefitType = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false);
			String benefitClassTypeId = insuranceAllowanceBenefitType.getString("benefitClassTypeId");
			if("SICKNESS_PREGNANCY".equals(benefitClassTypeId)){
				Date dateParticipateIns = InsuranceHelper.getDateParticipateIns(delegator, partyId);
				if(dateParticipateIns == null){
					dateParticipateIns = new Date(UtilDateTime.nowTimestamp().getTime());
				}
				Map<String, Object> benefitInsuranceForPartyLeave = InsuranceWorker.getBenefitInsuranceForPartyLeave(delegator, dispatcher, userLogin, benefitTypeId, 
						emplLeaveId, dateParticipateIns, timeZone, locale);
				BigDecimal totalDayLeave = (BigDecimal)context.get("totalDayLeave");
				if(totalDayLeave == null){
					totalDayLeave = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeave");
				}
				BigDecimal totalDayLeavePaidBefore = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaidBefore");
				BigDecimal totalDayLeavePaid = (BigDecimal)context.get("totalDayLeavePaid");
				if(totalDayLeavePaid == null){
					totalDayLeavePaid = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaid");
				}
				BigDecimal insuranceSalary = (BigDecimal)context.get("insuranceSalary");
				if(insuranceSalary == null){
					insuranceSalary = (BigDecimal)benefitInsuranceForPartyLeave.get("insuranceSalary");
				}
				BigDecimal rateBenefit = (BigDecimal)benefitInsuranceForPartyLeave.get("rateBenefit");
				BigDecimal totalDayLeavePaidExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("totalDayLeavePaidExceed");
				BigDecimal insuranceSalaryExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("insuranceSalaryExceed");
				BigDecimal rateBenefitLeaveExceed = (BigDecimal)benefitInsuranceForPartyLeave.get("rateBenefitLeaveExceed");
				Double accumulatedLeave = (Double)context.get("accumulatedLeave");
				if(accumulatedLeave == null){
					accumulatedLeave = totalDayLeavePaid.doubleValue() + totalDayLeavePaidBefore.doubleValue();
					if(totalDayLeavePaidExceed != null){
						accumulatedLeave += totalDayLeavePaidExceed.doubleValue();
					}
				}
				if(allowanceAmount == null){
					allowanceAmount = InsuranceHelper.getInsuranceAllowanceAmount(insuranceSalary, totalDayLeave, rateBenefit);
					if(insuranceSalaryExceed != null){
						BigDecimal allowanceAmountExceed = InsuranceHelper.getInsuranceAllowanceAmount(insuranceSalaryExceed, totalDayLeavePaidExceed, rateBenefitLeaveExceed);
						allowanceAmount = allowanceAmount.add(allowanceAmountExceed);
					}
				}
				String isAccumulated = insuranceAllowanceBenefitType.getString("isAccumulated");
				partyInsuranceAllowancePayment.set("insuranceSalary", insuranceSalary);
				partyInsuranceAllowancePayment.set("rateBenefit", rateBenefit);
				/*partyInsuranceAllowancePayment.set("insuranceParticipatePeriod", month + "-" + year);*/
				partyInsuranceAllowancePayment.set("totalDayLeave", totalDayLeave);
				partyInsuranceAllowancePayment.set("totalDayLeavePaid", totalDayLeavePaid);
				if("Y".equals(isAccumulated)){
					partyInsuranceAllowancePayment.set("accumulatedLeave", accumulatedLeave);
				}
				if(insuranceSalaryExceed != null){
					partyInsuranceAllowancePayment.set("totalDayLeaveExceedPaid", totalDayLeavePaidExceed);
					partyInsuranceAllowancePayment.set("rateBenefitLeaveExceed", rateBenefitLeaveExceed);
					partyInsuranceAllowancePayment.set("insuranceSalaryExceed", insuranceSalaryExceed);
				}
				if("BORN_CHILD_ADOPTION".equals(benefitTypeId)){
					partyInsuranceAllowancePayment.set("nbrChildBorn", benefitInsuranceForPartyLeave.get("nbrChildBorn"));
				}
				if("PREGNANCY_LOSS".equals(benefitTypeId)){
					partyInsuranceAllowancePayment.set("monthPregnant", benefitInsuranceForPartyLeave.get("monthPregnant"));
				}
			}else if("HEALTH_IMPROVEMENT".equals(benefitClassTypeId)){
				if(allowanceAmount == null){
					 allowanceAmount = InsuranceWorker.calculateAllowanceAmountImproveHealth(delegator, 
							emplLeave.getTimestamp("fromDate"), dayLeaveConcentrate, dayLeaveFamily);
				}
			}
			partyInsuranceAllowancePayment.set("fromDateLeave", emplLeave.getTimestamp("fromDate"));
			partyInsuranceAllowancePayment.set("thruDateLeave", emplLeave.getTimestamp("thruDate"));
			partyInsuranceAllowancePayment.set("allowanceAmount", allowanceAmount);
			partyInsuranceAllowancePayment.create();			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> upLoadInsuranceExcelFile(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "hrmdoc/excelTemplate";
		//String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
		String insuranceContentTypeId = (String)context.get("contentTypeId");
		//List<GenericValue> listHrmAdminUserLogin;
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			//listHrmAdminUserLogin = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", hrmAdmin), null, false);
			//GenericValue hrmAdminUserLogin = EntityUtil.getFirst(listHrmAdminUserLogin);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("public", "Y");
			uploadedFileCtx.put("userLogin", userLogin);
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
	        if(ServiceUtil.isSuccess(resultService)){
	        	String contentId = (String)resultService.get("contentId");
	        	dispatcher.runSync("createInsuranceContent", UtilMisc.toMap("contentId", contentId, "insuranceContentTypeId", insuranceContentTypeId, "userLogin", systemUserLogin));
	        }
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> geInsuranceContentType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("description");
    	}
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
			listIterator = delegator.find("InsuranceContentType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListAllowanceBenefitType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			
			List<GenericValue> insuranceAllowanceBenefitTypes = delegator.findList("InsAllowanceBenefitType", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
//			totalRows = insuranceAllowanceBenefitTypes.size();
//			if(end > totalRows){
//				end = totalRows;
//			}
//			insuranceAllowanceBenefitTypes = insuranceAllowanceBenefitTypes.subList(start, end);
			
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("benefitTypeId");
			listFieldInEntity.add("description");
			listFieldInEntity.add("benefitTypeCode");
			listFieldInEntity.add("isIncAnnualLeave");
			listFieldInEntity.add("isAccumulated");
			listFieldInEntity.add("frequenceId");
			listFieldInEntity.add("benefitClassTypeId");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listSortFields)){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				insuranceAllowanceBenefitTypes = EntityConditionUtils.doFilterGenericValue(insuranceAllowanceBenefitTypes, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("benefitClassTypeId");
			}
			insuranceAllowanceBenefitTypes = EntityUtil.orderBy(insuranceAllowanceBenefitTypes, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = insuranceAllowanceBenefitTypes.size();
				if(end > insuranceAllowanceBenefitTypes.size()){
					end = insuranceAllowanceBenefitTypes.size();
				}
				insuranceAllowanceBenefitTypes = insuranceAllowanceBenefitTypes.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > insuranceAllowanceBenefitTypes.size()){
				end  = insuranceAllowanceBenefitTypes.size();
			}
			for(GenericValue tempGv: insuranceAllowanceBenefitTypes){
				String isIncAnnualLeave = tempGv.getString("isIncAnnualLeave");
				String isAccumulated = tempGv.getString("isAccumulated");
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("benefitTypeId", tempGv.getString("benefitTypeId"));
				tempMap.put("description", tempGv.getString("description"));
				tempMap.put("benefitTypeCode", tempGv.getString("benefitTypeCode"));
				tempMap.put("isIncAnnualLeave", "Y".equals(isIncAnnualLeave));
				tempMap.put("isAccumulated", "Y".equals(isAccumulated));
				tempMap.put("frequenceId", tempGv.getString("frequenceId"));
				tempMap.put("benefitClassTypeId", tempGv.getString("benefitClassTypeId"));
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
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRulesInsBenefitType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		int totalRows = 0;
		Locale locale = (Locale)context.get("locale");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String benefitTypeId = request.getParameter("benefitTypeId");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		try {
			List<GenericValue> insBenefitTypeRuleList = delegator.findByAnd("InsBenefitTypeRule", UtilMisc.toMap("benefitTypeId", benefitTypeId), UtilMisc.toList("benefitTypeRuleId"), false);
			totalRows = insBenefitTypeRuleList.size();
			if(end > totalRows){
				end = totalRows;
			}
			insBenefitTypeRuleList = insBenefitTypeRuleList.subList(start, end);
			for(GenericValue rule: insBenefitTypeRuleList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String benefitTypeRuleId = rule.getString("benefitTypeRuleId");
				List<String> insBenCondDescList = FastList.newInstance();
				List<String> insBenActDescList = FastList.newInstance();
				EntityCondition insBenCondition = EntityCondition.makeCondition(UtilMisc.toMap("benefitTypeId", benefitTypeId, "benefitTypeRuleId", benefitTypeRuleId));
				EntityListIterator insBenCondIte = delegator.find("InsBenefitTypeCond", insBenCondition, null, null, UtilMisc.toList("benefitTypeCondSeqId"), opts);
				GenericValue insBenCond = null, insBenAction = null;
				while((insBenCond = insBenCondIte.next()) != null){
					String inputParamEnumId = insBenCond.getString("inputParamEnumId");
					String operatorEnumId = insBenCond.getString("operatorEnumId");
					BigDecimal condValue = insBenCond.getBigDecimal("condValue");
					String insCondDes = InsuranceHelper.getInsuranceBenefitCondsDesc(delegator, inputParamEnumId, operatorEnumId, condValue);
					insBenCondDescList.add(insCondDes);
				}
				insBenCondIte.close();
				EntityListIterator insBenActionIt = delegator.find("InsBenefitTypeAction", insBenCondition, null, null, UtilMisc.toList("benefitTypeActionSeqId"), opts);
				while((insBenAction = insBenActionIt.next()) != null){
					String benefitTypeActionEnumId = insBenAction.getString("benefitTypeActionEnumId");
					String uomId = insBenAction.getString("uomId");
					BigDecimal amount = insBenAction.getBigDecimal("amount");
					BigDecimal quantity = insBenAction.getBigDecimal("quantity");
					String insActDes = InsuranceHelper.getInsuranceBenefitActionDesc(delegator, benefitTypeActionEnumId, amount, quantity, uomId, locale);
					insBenActDescList.add(insActDes);
				}
				insBenActionIt.close();
				if(UtilValidate.isEmpty(insBenCondDescList)){
					insBenCondDescList.add(UtilProperties.getMessage("CommonUiLabels", "CommonNone", locale));
				}
				if(UtilValidate.isEmpty(insBenActDescList)){
					insBenActDescList.add(UtilProperties.getMessage("CommonUiLabels", "CommonNone", locale));
				}
				tempMap.put("benefitTypeRuleConds", insBenCondDescList);
				tempMap.put("benefitTypeRuleActs", insBenActDescList);
				tempMap.put("benefitTypeId", rule.getString("benefitTypeId"));
				tempMap.put("benefitTypeRuleId", benefitTypeRuleId);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	/*public static Map<String, Object> createInsuranceContent(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		GenericValue insuranceContent = delegator.makeValue("InsuranceContent");
		insuranceContent.setAllFields(context, false, null, null);
		if(fromDate == null){
			insuranceContent.set("fromDate", UtilDateTime.nowTimestamp());
		}
		try {
			insuranceContent.create();
		} catch (GenericEntityException e){
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteInsuranceContent(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String contentId = (String)context.get("contentId");
		String insuranceContentTypeId = (String)context.get("insuranceContentTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			GenericValue insuranceContent = delegator.findOne("InsuranceContent", 
					UtilMisc.toMap("contentId", contentId, "insuranceContentTypeId", insuranceContentTypeId, "fromDate", fromDate), false);
			insuranceContent.set("thruDate", UtilDateTime.nowTimestamp());
			insuranceContent.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getHospitalList(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	int totalRows = 0;    	
    	try {
			List<GenericValue> hospitalList = delegator.findList("HospitalAndPostalAddress", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
			totalRows = hospitalList.size();
			if(end > totalRows){
				end = totalRows;
			}
			hospitalList = hospitalList.subList(start, end);
			for(GenericValue tempGv: hospitalList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				tempMap.put("hospitalName", tempGv.getString("hospitalName"));
				tempMap.put("hospitalId", tempGv.getString("hospitalId"));
				tempMap.put("hospitalCode", tempGv.getString("hospitalCode"));
				tempMap.put("stateProvinceGeoId", tempGv.getString("stateProvinceGeoId"));
				GenericValue stateProvinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", tempGv.getString("stateProvinceGeoId")), false);
				tempMap.put("stateProvinceGeoName", stateProvinceGeo.getString("geoName"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> createHospital(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue hospital = delegator.makeValue("Hospital");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		hospital.setNonPKFields(context);
		String hospitalId = delegator.getNextSeqId("Hospital");
		hospital.set("hospitalId", hospitalId);
		String stateProvinceGeoId = (String)context.get("stateProvinceGeoId");
		String districtGeoId = (String)context.get("districtGeoId");
		try {
			GenericValue stateProvinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
			Map<String, Object> ctxMap = FastMap.newInstance();
			ctxMap.put("address1", stateProvinceGeo.get("geoName"));
			ctxMap.put("city", stateProvinceGeo.get("geoName"));
			ctxMap.put("districtGeoId", districtGeoId);
			ctxMap.put("stateProvinceGeoId", stateProvinceGeoId);
			ctxMap.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("createPostalAddress", ctxMap);
			if(ServiceUtil.isSuccess(resultService)){
				hospital.set("contactMechId", resultService.get("contactMechId"));
			}
			hospital.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> settingEmplInsuranceSalaryByPosType(DispatchContext dctx, Map<String, Object> context){
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String overrideDataWay = (String)context.get("overrideDataWay");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			for(GenericValue empl: emplList){
				InsuranceHelper.settingEmplInsuranceSalaryByPosType(dispatcher, delegator, empl.getString("partyId"), fromDate, thruDate, overrideDataWay, userLogin);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createPartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyId = (String)context.get("partyId");
		String periodTypeId = (String)context.get("periodTypeId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		Locale locale = (Locale)context.get("locale");
		if(periodTypeId == null){
			periodTypeId = "MONTHLY";
		}
		try {
			List<GenericValue> partyInsuranceSals = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceSals)){
				GenericValue errEntity = partyInsuranceSals.get(0);
				Timestamp fromDateErr = errEntity.getTimestamp("fromDate");
				Timestamp thruDateErr = errEntity.getTimestamp("thruDate");
				String errMsg = "";
				if(thruDate != null){
					errMsg = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotSetInsuranceSalaryFromDateThrudate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate), 
										"thruDate", DateUtil.getDateMonthYearDesc(thruDate)), locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotSetInsuranceSalaryFromDate", UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate)), locale);
				}
				errMsg += " ";
				if(thruDateErr != null){
					errMsg += UtilProperties.getMessage("BaseHRInsuranceUiLabels", "BecauseInsuanceSalaryFromDateThruDate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr), "thruDate", DateUtil.getDateMonthYearDesc(thruDateErr), "amount", errEntity.getBigDecimal("amount")), locale);
				}else{
					errMsg += UtilProperties.getMessage("BaseHRInsuranceUiLabels", "BecauseInsuanceSalaryFromDate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr), "amount", errEntity.getBigDecimal("amount")), locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue partyInsuranceSal = delegator.makeValue("PartyInsuranceSalary");
			partyInsuranceSal.setNonPKFields(context);
			partyInsuranceSal.set("periodTypeId", periodTypeId);
			String partyInsSalId = delegator.getNextSeqId("PartyInsuranceSalary");
			partyInsuranceSal.set("partyInsSalId", partyInsSalId);
			delegator.create(partyInsuranceSal);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updatePartyInsuranceSalary(DispatchContext dctx, Map<String, Object> context){
		String partyInsSalId = (String)context.get("partyInsSalId");
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue partyInsSal = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyInsSalId", partyInsSalId), false);
			if(partyInsSal == null){
				return ServiceUtil.returnError("cannot find insurance salary to update");
			}
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			String partyId = (String)context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			conditions.add(EntityCondition.makeCondition("partyId", partyId));
			conditions.add(EntityCondition.makeCondition("partyInsSalId", EntityJoinOperator.NOT_EQUAL, partyInsSalId));
			Locale locale = (Locale)context.get("locale");
			List<GenericValue> partyInsuranceSals = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceSals)){
				GenericValue errEntity = partyInsuranceSals.get(0);
				Timestamp fromDateErr = errEntity.getTimestamp("fromDate");
				Timestamp thruDateErr = errEntity.getTimestamp("thruDate");
				String errMsg = "";
				if(thruDate != null){
					errMsg = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotSetInsuranceSalaryFromDateThrudate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate), 
										"thruDate", DateUtil.getDateMonthYearDesc(thruDate)), locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotSetInsuranceSalaryFromDate", UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate)), locale);
				}
				errMsg += " ";
				if(thruDateErr != null){
					errMsg += UtilProperties.getMessage("BaseHRInsuranceUiLabels", "BecauseInsuanceSalaryFromDateThruDate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr), "thruDate", DateUtil.getDateMonthYearDesc(thruDateErr), "amount", errEntity.getBigDecimal("amount")), locale);
				}else{
					errMsg += UtilProperties.getMessage("BaseHRInsuranceUiLabels", "BecauseInsuanceSalaryFromDate", 
							UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr), "amount", errEntity.getBigDecimal("amount")), locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			partyInsSal.setNonPKFields(context);
			partyInsSal.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createInsBenefitTypeRule(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue insBenefitTypeRule = delegator.makeValue("InsBenefitTypeRule");
		insBenefitTypeRule.setAllFields(context, false, null, null);
		Map<String, Object> retMap = ServiceUtil.returnSuccess(); 
		delegator.setNextSubSeqId(insBenefitTypeRule, "benefitTypeRuleId", 2, 1);
		try {
			delegator.create(insBenefitTypeRule);
			String benefitTypeRuleId = insBenefitTypeRule.getString("benefitTypeRuleId");
			retMap.put("benefitTypeRuleId", benefitTypeRuleId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createInsBenefitTypeCond(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess(); 
		Delegator delegator = dctx.getDelegator();
		GenericValue insBenefitTypeCond = delegator.makeValue("InsBenefitTypeCond");
		insBenefitTypeCond.setAllFields(context, false, null, null);
		delegator.setNextSubSeqId(insBenefitTypeCond, "benefitTypeCondSeqId", 2, 1);
		try {
			delegator.create(insBenefitTypeCond);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createInsBenefitTypeAction(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String benefitTypeId = (String)context.get("benefitTypeId");
		String benefitTypeRuleId = (String)context.get("benefitTypeRuleId");
		String benefitTypeActionEnumId = (String)context.get("benefitTypeActionEnumId");
		String uomId = (String)context.get("uomId");
		BigDecimal amount = (BigDecimal)context.get("amount");
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		try {
			GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", benefitTypeActionEnumId), false);
			String enumTypeId = enumeration.getString("enumTypeId");
			if("INS_BENEFIT_ACT_SAL".equals(enumTypeId) && uomId == null){
				uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			}
			GenericValue insBenefitTypeAction = delegator.makeValue("InsBenefitTypeAction");
			insBenefitTypeAction.set("benefitTypeId", benefitTypeId);
			insBenefitTypeAction.set("benefitTypeRuleId", benefitTypeRuleId);
			insBenefitTypeAction.set("uomId", uomId);
			insBenefitTypeAction.set("quantity", quantity);
			insBenefitTypeAction.set("benefitTypeActionEnumId", benefitTypeActionEnumId);
			if("INS_BE_RATE_SAL_MON".equals(benefitTypeActionEnumId)){
				insBenefitTypeAction.set("amount", amount);
			}
			delegator.setNextSubSeqId(insBenefitTypeAction, "benefitTypeActionSeqId", 2, 1);
			delegator.create(insBenefitTypeAction);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> deleteInsBenefitTypeRule(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String benefitTypeId = (String)context.get("benefitTypeId");
		String benefitTypeRuleId = (String)context.get("benefitTypeRuleId");
		Delegator delegator = dctx.getDelegator();
		try {
			Map<String, Object> keyMap = UtilMisc.toMap("benefitTypeId", benefitTypeId, "benefitTypeRuleId", benefitTypeRuleId);
			GenericValue insBenefitTypeRule = delegator.findOne("InsBenefitTypeRule", keyMap, false);
			if(insBenefitTypeRule == null){
				return ServiceUtil.returnError("cannot find InsBenefitTypeRule to delete");
			}
			List<GenericValue> insBenefitTypeConds = delegator.findByAnd("InsBenefitTypeCond", keyMap, null, false);
			List<GenericValue> insBenefitTypeActions = delegator.findByAnd("InsBenefitTypeAction", keyMap, null, false);
			for(GenericValue tempGv: insBenefitTypeConds){
				tempGv.remove();
			}
			for(GenericValue tempGv: insBenefitTypeActions){
				tempGv.remove();
			}
			insBenefitTypeRule.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createInsuranceAllowanceBenefitType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		GenericValue insuranceAllowanceBenefitType = delegator.makeValue("InsAllowanceBenefitType");
		String benefitTypeId = (String)context.get("benefitTypeId");
		try {
			if(benefitTypeId != null){
				GenericValue checkExists = delegator.findOne("InsAllowanceBenefitType", UtilMisc.toMap("benefitTypeId", benefitTypeId), false);
				if(checkExists != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAllowanceBenefitTypeExists", 
							UtilMisc.toMap("benefitTypeId", benefitTypeId), locale));
				}
			}else{
				benefitTypeId = delegator.getNextSeqId("InsAllowanceBenefitType");
			}
			insuranceAllowanceBenefitType.setNonPKFields(context);
			insuranceAllowanceBenefitType.set("benefitTypeId", benefitTypeId);
			insuranceAllowanceBenefitType.create();
			retMap.put("benefitTypeId", benefitTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> JQGetListInsuranceType(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listIterator = FastList.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		try {
			listIterator = delegator.findList("InsuranceType", null, null, null, opts, false);
			List<String> listFieldInEntity = FastList.newInstance();
			listFieldInEntity.add("insuranceTypeId");
			listFieldInEntity.add("description");
			listFieldInEntity.add("employerRate");
			listFieldInEntity.add("employeeRate");
			listFieldInEntity.add("isCompulsory");
			
			List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
			List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
			EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
			
			List<String> sortedFieldInEntity = FastList.newInstance();
			List<String> sortedFieldNotInEntity = FastList.newInstance();
			if(listSortFields != null){
				EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
			}
			
			if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
				listIterator = EntityConditionUtils.doFilterGenericValue(listIterator, condsForFieldInEntity);
			}
			if(UtilValidate.isEmpty(sortedFieldInEntity)){
				sortedFieldInEntity.add("partyId");
			}
			listIterator = EntityUtil.orderBy(listIterator, sortedFieldInEntity);
			
			boolean isFilterAdvance = false;
			if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
				totalRows = listIterator.size();
				if(end > listIterator.size()){
					end = listIterator.size();
				}
				listIterator = listIterator.subList(start, end);
			}else{
				isFilterAdvance = true;
			}
			if(end > listIterator.size()){
				end  = listIterator.size();
			}
			for (GenericValue g : listIterator) {
				Map<String, Object> tmpMap = FastMap.newInstance();
				tmpMap.put("insuranceTypeId", g.getString("insuranceTypeId"));
				tmpMap.put("description", g.getString("description"));
				double employerRate = g.getDouble("employerRate")*100;
				double employeeRate = g.getDouble("employeeRate")*100;
				tmpMap.put("employerRate", employerRate);
				tmpMap.put("employeeRate", employeeRate);
				String isCompulsory = g.getString("isCompulsory");
				boolean isCompulsoryBoo = false;
				if("Y".equals(isCompulsory)){
					isCompulsoryBoo = true;
				}
				tmpMap.put("isCompulsory", isCompulsoryBoo);
				listReturn.add(tmpMap);
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
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}
	public static Map<String, Object> updateInsuranceType(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String isCompulsory = (String) context.get("isCompulsory");
		String insuranceTypeId = (String) context.get("insuranceTypeId");
		double employerRate =(double) context.get("employerRate");
		double employeeRate =(double) context.get("employeeRate");
		try {
			GenericValue insuranceType = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", insuranceTypeId), false);
			insuranceType.set("employerRate", employerRate/100);
			insuranceType.set("employeeRate", employeeRate/100);
			if(isCompulsory.equals("true")){
				isCompulsory = "Y";
				insuranceType.set("isCompulsory", isCompulsory);
			}else{
				isCompulsory = "N";
				insuranceType.set("isCompulsory", isCompulsory);
			}
			insuranceType.store();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	public static Map<String, Object> updateHospitalNameCode(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String hospitalName = (String) context.get("hospitalName");
		String hospitalId = (String) context.get("hospitalId");
		String hospitalCode = (String) context.get("hospitalCode");
		try {
			GenericValue hospital = delegator.findOne("Hospital", UtilMisc.toMap("hospitalId", hospitalId), false);
			if(UtilValidate.isNotEmpty(hospital)){
				hospital.set("hospitalName", hospitalName);
				hospital.set("hospitalCode", hospitalCode);
				hospital.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	/*public static Map<String, Object> createPartyInsDeclDataResource(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "hrmdoc";
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("public", "N");
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String path = (String)resultService.get("path");
			Map<String, Object> dataResourceCtx = FastMap.newInstance();
			dataResourceCtx.put("objectInfo", path);
	        dataResourceCtx.put("dataResourceName", uploadFileNameStr);
	        dataResourceCtx.put("userLogin", systemUserLogin);
	        dataResourceCtx.put("dataResourceTypeId", "URL_RESOURCE");
	        dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
	        dataResourceCtx.put("isPublic", "N");
	        resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
	        if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
	        String dataResourceId = (String)resultService.get("dataResourceId");
	        GenericValue partyInsDeclDataResource = delegator.makeValue("PartyInsDeclDataResource");
	        partyInsDeclDataResource.setAllFields(context, false, null, null);
	        partyInsDeclDataResource.set("dataResourceId", dataResourceId);
	        partyInsDeclDataResource.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInsuranceParticipateReportJQ(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-fromDate");
		}
		try {
			List<GenericValue> reportList = delegator.findList("InsParticipateReportAndMonth", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
			List<GenericValue> insuranceOriginateTypeList = delegator.findByAnd("InsuranceOriginateType", null, null, false);
			totalRows = reportList.size();
			if(end > totalRows){
				end = totalRows;
			}
			reportList = reportList.subList(start, end);
			for(GenericValue report: reportList){
				Map<String, Object> tempMap = report.getAllFields();
				String reportId = report.getString("reportId");
				for(GenericValue insuranceOriginateType: insuranceOriginateTypeList){
					String insuranceOriginateTypeId = insuranceOriginateType.getString("insuranceOriginateTypeId");
					List<GenericValue> insuranceReportAndOriginateType = delegator.findByAnd("InsReportOriginateAndTypeGroup", 
							UtilMisc.toMap("reportId", reportId, "insuranceOriginateTypeId", insuranceOriginateTypeId), null, false);
					if(UtilValidate.isNotEmpty(insuranceReportAndOriginateType)){
						tempMap.put(insuranceOriginateTypeId, insuranceReportAndOriginateType.get(0).get("originateNbr"));
					}else{
						tempMap.put(insuranceOriginateTypeId, 0);
					}
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> createInsEmplAdjustParticipate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String insuranceOriginateTypeId = (String)context.get("insuranceOriginateTypeId");
		String suspendReasonId = (String)context.get("suspendReasonId");
		String isReturnSHICard = (String)context.get("isReturnSHICard");
		try {
			GenericValue insEmplAdjustParticipate = delegator.makeValue("InsEmplAdjustParticipate");
			insEmplAdjustParticipate.setNonPKFields(context);
			Timestamp previousMonthStart = UtilDateTime.getMonthStart(fromDate, 0, -1);
    		Timestamp previousMonthEnd = UtilDateTime.getMonthEnd(previousMonthStart, timeZone, locale);
			switch (insuranceOriginateTypeId){
				case "NEWLY_PARTICIPATE":
					List<GenericValue> emplAdjustParticipateList = delegator.findByAnd("InsEmplAdjustParticipate", 
							UtilMisc.toMap("partyId", partyId), null, false);
					if(UtilValidate.isNotEmpty(emplAdjustParticipateList)){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", 
								"EmployeeNewlyParticipated", UtilMisc.toMap("fullName", PartyUtil.getPersonName(delegator, partyId)), locale));
					}
					break;
				case "SUSPEND": 
				case "STOP_PARTICIPATE":
					GenericValue emplParticipating = InsuranceHelper.getEmplInsuranceLatestStatus(delegator, partyId, fromDate, timeZone, locale);
					if(emplParticipating == null || !"PARTICIPATING".equals(emplParticipating.get("statusId"))){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotSuspendStopParticipateEmployee",
								UtilMisc.toMap("fullName", PartyUtil.getPersonName(delegator, partyId), "fromDate", DateUtil.getDateMonthYearDesc(fromDate)),locale));
					}
					if(emplParticipating.get("participateThruDate") == null){
						GenericValue tempGv = delegator.findOne("InsEmplAdjustParticipate", UtilMisc.toMap("insEmplAdjustParticipateId", emplParticipating.get("insEmplAdjustParticipateId")), false);
						tempGv.put("thruDate", previousMonthEnd);
						tempGv.store();
					}
					Timestamp participateFromDate = emplParticipating.getTimestamp("participateFromDate");
					List<GenericValue> partyParticipateInsuranceList = delegator.findByAnd("PartyParticipateInsurance", 
							UtilMisc.toMap("partyId", partyId, "fromDate", participateFromDate, "statusId", "PARTICIPATING"), null, false);
					Map<String, Object> insuranceTypeMap = FastMap.newInstance();
					insuranceTypeMap.put("partyId", partyId);
					insuranceTypeMap.put("fromDate", fromDate);
					insuranceTypeMap.put("statusId", context.get("statusId"));
					insuranceTypeMap.put("thruDate", context.get("thruDate"));
					insuranceTypeMap.put("locale", locale);
					insuranceTypeMap.put("timeZone", timeZone);
					insuranceTypeMap.put("userLogin", userLogin);
					for(GenericValue tempGv: partyParticipateInsuranceList){
						tempGv.put("thruDate", previousMonthEnd);
						tempGv.store();
						insuranceTypeMap.put("insuranceTypeId", tempGv.get("insuranceTypeId"));
						dispatcher.runSync("createPartyParticipateInsurance", insuranceTypeMap);
					}
					GenericValue suspendInsReasonType = delegator.findOne("SuspendInsReasonType", UtilMisc.toMap("suspendReasonId", suspendReasonId), false);
					String isRequestReturnCard = suspendInsReasonType.getString("isRequestReturnCard");
					if(!"Y".equals(isRequestReturnCard)){
						insEmplAdjustParticipate.put("supplementFromDate", null);
						insEmplAdjustParticipate.put("supplementThruDate", null);
						insEmplAdjustParticipate.put("isReturnSHICard", "N");
						insEmplAdjustParticipate.put("isSupplement", "N");
					}else if("Y".equals(isReturnSHICard)){
						insEmplAdjustParticipate.put("supplementFromDate", null);
						insEmplAdjustParticipate.put("supplementThruDate", null);
					}
					break;
				case "REPARTICIPATE":
					GenericValue emplSuspend = InsuranceHelper.getEmplInsuranceLatestStatus(delegator, partyId, fromDate, timeZone, locale);
					if(emplSuspend == null || !"SUSPEND_PARTICIPATE".equals(emplSuspend.get("statusId"))){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "CannotReparticipateEmployee",
								UtilMisc.toMap("fullName", PartyUtil.getPersonName(delegator, partyId), "fromDate", DateUtil.getDateMonthYearDesc(fromDate)),locale));
					}
					if(emplSuspend.get("participateThruDate") == null){
						GenericValue tempGv = delegator.findOne("InsEmplAdjustParticipate", UtilMisc.toMap("insEmplAdjustParticipateId", emplSuspend.get("insEmplAdjustParticipateId")), false);
						tempGv.put("thruDate", previousMonthEnd);
						tempGv.store();
					}
					Timestamp suspendFromDate = emplSuspend.getTimestamp("fromDate");
					List<GenericValue> partySuspendInsuranceList = delegator.findByAnd("PartyParticipateInsurance", 
							UtilMisc.toMap("partyId", partyId, "fromDate", suspendFromDate, "statusId", "SUSPEND_PARTICIPATE"), null, false);
					for(GenericValue tempGv: partySuspendInsuranceList){
						tempGv.put("thruDate", previousMonthEnd);
						tempGv.store();
					}
				default:
					break;
			} 
			String insEmplAdjustParticipateId = delegator.getNextSeqId("InsEmplAdjustParticipate");
			successResult.put("insEmplAdjustParticipateId", insEmplAdjustParticipateId);
			insEmplAdjustParticipate.put("insEmplAdjustParticipateId", insEmplAdjustParticipateId);
			delegator.create(insEmplAdjustParticipate);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createInsurancePartyOriginate", context, userLogin, timeZone, locale);
			ctxMap.put("monthReport", new Long(cal.get(Calendar.MONTH)));
			ctxMap.put("yearReport", new Long(cal.get(Calendar.YEAR)));
			ctxMap.put("fromDateOriginate", fromDate);
			ctxMap.put("thruDateOriginate", context.get("thruDate"));
			Map<String, Object> resultServices = dispatcher.runSync("createInsurancePartyOriginate", ctxMap);
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createInsurancePartyOriginate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		GenericValue insurancePartyOriginate = delegator.makeValue("InsurancePartyOriginate");
		insurancePartyOriginate.setNonPKFields(context);
		String insurancePartyOriginateId = delegator.getNextSeqId("InsurancePartyOriginate");
		try {
			successResult.put("insurancePartyOriginateId", insurancePartyOriginateId);
			insurancePartyOriginate.put("insurancePartyOriginateId", insurancePartyOriginateId);
			delegator.create(insurancePartyOriginate);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createInsAdjustEmplSalaryAndJob(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		BigDecimal newAmount = (BigDecimal)context.get("amount");
		try {
			GenericValue partyInsuranceSalaryOld = InsuranceHelper.getPartyInsuranceSalaryLastest(delegator, partyId);
			if(partyInsuranceSalaryOld == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAdjustSalary_OldInfoNotFound", locale));
			}
			BigDecimal oldAmount = partyInsuranceSalaryOld.getBigDecimal("amount");
			Timestamp fromDateOld = partyInsuranceSalaryOld.getTimestamp("fromDate");
			Timestamp thruDateOld = partyInsuranceSalaryOld.getTimestamp("thruDate");
			Timestamp previousMonthEnd = UtilDateTime.getMonthEnd(UtilDateTime.getMonthStart(fromDate, 0, -1), timeZone, locale);
			if((thruDateOld == null && DateUtil.afterOrEquals(fromDateOld, fromDate)) || (thruDateOld != null && DateUtil.afterOrEquals(fromDateOld, fromDate))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "ValueNotValidWithOldInfo", locale));
			}
			if(thruDateOld == null){
				partyInsuranceSalaryOld.put("thruDate", previousMonthEnd);
				partyInsuranceSalaryOld.store();
			}
			Map<String, Object> partyInsuranceSalMap = ServiceUtil.setServiceFields(dispatcher, "createPartyInsuranceSalary", context, userLogin, timeZone, locale);
			partyInsuranceSalMap.put("partyGroupId", partyInsuranceSalaryOld.get("partyGroupId"));
			partyInsuranceSalMap.put("allowanceSeniority", context.get("allowanceSeniority") != null? context.get("allowanceSeniority"): partyInsuranceSalaryOld.get("allowanceSeniority"));
			partyInsuranceSalMap.put("allowanceSeniorityExces", context.get("allowanceSeniority") != null? context.get("allowanceSeniorityExces"): partyInsuranceSalaryOld.get("allowanceSeniorityExces"));
			partyInsuranceSalMap.put("allowancePosition", context.get("allowanceSeniority") != null? context.get("allowancePosition"): partyInsuranceSalaryOld.get("allowancePosition"));
			partyInsuranceSalMap.put("allowanceOther", context.get("allowanceSeniority") != null? context.get("allowanceOther"): partyInsuranceSalaryOld.get("allowanceOther"));
			Map<String, Object> resultServices = dispatcher.runSync("createPartyInsuranceSalary", partyInsuranceSalMap);
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
			String insuranceOriginateTypeId = null;
			if(newAmount.compareTo(oldAmount) < 0){
				insuranceOriginateTypeId = "DECREASE_SALARY";
			}else{
				insuranceOriginateTypeId = "INCREASE_SALARY";
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createInsurancePartyOriginate", context, userLogin, timeZone, locale);
			ctxMap.put("monthReport", new Long(cal.get(Calendar.MONTH)));
			ctxMap.put("yearReport", new Long(cal.get(Calendar.YEAR)));
			ctxMap.put("insuranceOriginateTypeId", insuranceOriginateTypeId);
			ctxMap.put("fromDateOriginate", fromDate);
			ctxMap.put("thruDateOriginate", context.get("thruDate"));
			resultServices = dispatcher.runSync("createInsurancePartyOriginate", ctxMap);
			if(!ServiceUtil.isSuccess(resultServices)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultServices));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> createInsuranceParticipateReport(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
		Long month = (Long)context.get("month");
		Long year = (Long)context.get("year");
		try {
			List<GenericValue> insuranceParticipateReportList = delegator.findByAnd("InsuranceParticipateReport", 
					UtilMisc.toMap("month", month, "year", year), null, false);
			if(UtilValidate.isNotEmpty(insuranceParticipateReportList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceReportIsExists", UtilMisc.toMap("month", month + 1, "year", year), locale));
			}
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.MONTH, month.intValue());
			cal.set(Calendar.YEAR, year.intValue());
			Timestamp fromDate = UtilDateTime.getMonthStart(new Timestamp(cal.getTimeInMillis()));
			Timestamp thruDate = UtilDateTime.getMonthEnd(fromDate, timeZone, locale);
			GenericValue newEntity = delegator.makeValue("InsuranceParticipateReport");
			newEntity.setNonPKFields(context);
			String reportId = delegator.getNextSeqId("InsuranceParticipateReport");
			newEntity.put("reportId", reportId);
			newEntity.put("fromDate", fromDate);
			newEntity.put("thruDate", thruDate);
			delegator.create(newEntity);
			successResult.put("reportId", reportId);
			InsuranceHelper.updateInsuranceReportPartyOriginate(delegator, month, year, reportId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> updateInsurancePartyOriginateOfReport(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String reportId = (String)context.get("reportId");
		try {
			GenericValue insuranceParticipateReport = delegator.findOne("InsuranceParticipateReport", UtilMisc.toMap("reportId", reportId), false);
			if(insuranceParticipateReport == null){
				return ServiceUtil.returnError("Cannot find report with ID: " + reportId);
			}
			InsuranceHelper.updateInsuranceReportPartyOriginate(delegator, insuranceParticipateReport.getLong("month"), insuranceParticipateReport.getLong("year"), reportId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
}
