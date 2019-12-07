package com.olbius.basehr.kpiperfreview.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.kpiperfreview.helper.PerfReviewKPIHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;

public class PerfReviewKPI {
	public static Map<String,Object> createNewCriteriaType(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		String description = (String) context.get("description");
		String perfCriteriaTypeId = (String) context.get("perfCriteriaTypeId");
		try {
			GenericValue newCriteriaType = delegator.makeValue("PerfCriteriaType");
			newCriteriaType.set("perfCriteriaTypeId", perfCriteriaTypeId);
			newCriteriaType.set("description", description.trim());
			newCriteriaType.create();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updateCriteriaType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String perfCriteriaTypeId = (String)context.get("perfCriteriaTypeId");
	
		try {
			GenericValue perfCriteriaType = delegator.findOne("PerfCriteriaType", UtilMisc.toMap("perfCriteriaTypeId", perfCriteriaTypeId), false);
			if(perfCriteriaType == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "NotFoundCriteriaTypeToUpdate", (Locale)context.get("locale")));
			}
			String description = (String)context.get("description");
			perfCriteriaType.set("description", description.trim());
			perfCriteriaType.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String,Object> createCriteria(DispatchContext ctx, Map<String,Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
		Delegator delegator = ctx.getDelegator();
		String criteriaName = (String) context.get("criteriaName");
		String statusId = (String)context.get("statusId");
		criteriaName = criteriaName.trim();
		if(statusId == null){
			statusId = "KPI_ACTIVE";
		}
		try {
			String criteriaId = delegator.getNextSeqId("PerfCriteria");
			successResult.put("criteriaId", criteriaId);
			GenericValue perfCriteria = delegator.makeValue("PerfCriteria");
			perfCriteria.setNonPKFields(context);
			perfCriteria.put("statusId", statusId);
			perfCriteria.put("criteriaName", criteriaName);
			perfCriteria.put("criteriaId", criteriaId);
			perfCriteria.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return successResult;
	}
	
	public static Map<String, Object> updateCriteria(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		String criteriaId = (String)context.get("criteriaId");
		try {
			GenericValue criteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			if(criteria == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "NotFoundCriteriaToUpdate", (Locale)context.get("locale")));
			}
			criteria.setNonPKFields(context);
			criteria.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createEmplPosTypePerfCri(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String criteriaId = (String)context.get("criteriaId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String periodTypeId = (String)context.get("periodTypeId");
		String uomId = (String)context.get("uomId");
		BigDecimal target = (BigDecimal)context.get("target");
		try {
			GenericValue emplPosTypePerfCri = delegator.findOne("EmplPosTypePerfCri", UtilMisc.toMap("criteriaId", criteriaId, "emplPositionTypeId", emplPositionTypeId), false);
			if(emplPosTypePerfCri != null){
				emplPosTypePerfCri.set("statusId", "KPI_ACTIVE");
			}else{
				GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
				emplPosTypePerfCri = delegator.makeValue("EmplPosTypePerfCri");
				emplPosTypePerfCri.put("emplPositionTypeId", emplPositionTypeId);
				emplPosTypePerfCri.put("criteriaId", criteriaId);
				emplPosTypePerfCri.put("statusId", "KPI_ACTIVE");
				if(periodTypeId == null){
					emplPosTypePerfCri.put("periodTypeId", perfCriteria.get("periodTypeId"));
				}
				if(uomId == null){
					emplPosTypePerfCri.put("uomId", perfCriteria.get("uomId"));
				}
				if(target == null){
					emplPosTypePerfCri.put("target", perfCriteria.get("target"));
				}
			}
			emplPosTypePerfCri.setNonPKFields(context);
			delegator.createOrStore(emplPosTypePerfCri);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> deleteEmplPosTypePerfCri(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String criteriaId = (String)context.get("criteriaId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		try {
			GenericValue emplPosTypePerfCri = delegator.findOne("EmplPosTypePerfCri", UtilMisc.toMap("criteriaId", criteriaId, "emplPositionTypeId", emplPositionTypeId), false);
			if(emplPosTypePerfCri == null){
				return ServiceUtil.returnError("Cannot find KPI for position to delete");
			}
			emplPosTypePerfCri.set("statusId", "KPI_INACTIVE");
			emplPosTypePerfCri.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListEmplPosCriType(DispatchContext dctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId") != null? parameters.get("statusId")[0] : null; 
    	String emplPositionTypeId = parameters.get("emplPositionTypeId") != null? parameters.get("emplPositionTypeId")[0] : null; 
    	listSortFields.add("description");
    	listSortFields.add("criteriaName");
    	if(statusId != null){
    		listAllConditions.add(EntityCondition.makeCondition("statusId", statusId));
    	}
    	if(emplPositionTypeId != null){
    		listAllConditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
    	}
    	try {
    		listIterator = delegator.find("PerfCriteriaAndTypeAndPosType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling KPI service: " + e.toString();
			Debug.logError(e, errMsg, PerfReviewKPI.class.getName());
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> getListCriteriaType(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		opts.setDistinct(true);
		try {
			listIterator = delegator.find("PerfCriteriaType", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> getListKeyPerformanceIndicator(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Timestamp now = UtilDateTime.nowTimestamp();
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("criteriaName");
			}
			listIterator = delegator.find("PerfCriteriaAndType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String,Object> getPerfCriteriaAssessment(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Timestamp now = UtilDateTime.nowTimestamp();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<String> listOrgId = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
			List<String> departmentList = FastList.newInstance();
			for(String orgId: listOrgId){
				Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
				List<GenericValue> allDept = buildOrg.getAllDepartmentList(delegator);
				if(UtilValidate.isNotEmpty(allDept)){
					List<String> tempListId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
					departmentList.addAll(tempListId);
				}
			}
			listOrgId.addAll(departmentList);
			if(UtilValidate.isNotEmpty(listOrgId)){
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listOrgId));
			}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-fromDate");
			}
			listIterator = delegator.find("PerfCriteriaAssessmentAndPG", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKPIOfEmployee(DispatchContext dctx, Map<String, Object> context){
		Map<String,Object> successResult = FastMap.newInstance();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		
		String periodTypeId = "";
		int month_from = 0;
		int year_from = 0;
		int month_to = 0; 
		int year_to = 0;
		int quarter_from = 0;
		int year_from_quarter = 0;
		int quarter_to = 0;
		int year_to_quarter = 0;
		int year = 0;
		Timestamp fromDate_time = null;
		Timestamp thruDate_time = null;
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		
		List<Map<String,Object>> listIterator = new ArrayList<Map<String,Object>>();
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("periodTypeId")){
			periodTypeId = parameters.get("periodTypeId")[0];
		}
		
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("month_from")){
			month_from = Integer.parseInt(parameters.get("month_from")[0]);
		}
		if(parameters.containsKey("year_from") && parameters.get("year_from")[0] != null){
			year_from = Integer.parseInt(parameters.get("year_from")[0]);
		}
		if(parameters.containsKey("month_to") && parameters.get("month_to")[0] != null){
			month_to = Integer.parseInt(parameters.get("month_to")[0]);
		}
		if(parameters.containsKey("year_to") && parameters.get("year_to")[0] != null){
			year_to = Integer.parseInt(parameters.get("year_to")[0]);
		}
		if(month_from != 0 && month_to != 0 && year_from != 0 && year_to != 0){
			Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from, month_from);
			fromDate_time = UtilDateTime.getMonthStart(date_from);
			
			Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to, month_to);
			thruDate_time = UtilDateTime.getMonthEnd(date_to, timeZone, locale);
		}
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("quarter_from")){
			quarter_from = Integer.parseInt(parameters.get("quarter_from")[0]);
		}
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("year_from_quarter")){
			year_from_quarter = Integer.parseInt(parameters.get("year_from_quarter")[0]);
		}
		if(parameters.containsKey("quarter_to") && parameters.get("quarter_to")[0] != null){
			quarter_to = Integer.parseInt(parameters.get("quarter_to")[0]);
		}
		if(parameters.containsKey("year_to_quarter") && parameters.get("year_to_quarter")[0] != null){
			year_to_quarter = Integer.parseInt(parameters.get("year_to_quarter")[0]);
		}
		if(quarter_from != 0 && year_from_quarter != 0 && quarter_to != 0 && year_to_quarter != 0){
			Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from_quarter, (quarter_from-1)*3 + 1);
			Date date_from_date = new Date(date_from.getTime());
			fromDate_time = DateUtil.getDateStartOfQuarter(date_from_date);
			
			Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to_quarter, (quarter_to-1) * 3 + 1);
			Date date_to_date = new Date(date_to.getTime());
			thruDate_time = DateUtil.getDateEndOfQuarter(date_to_date);
		}
		if(parameters.containsKey("year") && parameters.get("year")[0] != null){
			year = Integer.parseInt(parameters.get("year")[0]);
			if(year != 0){
				Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year, 1);
				fromDate_time = UtilDateTime.getYearStart(date_from);
				thruDate_time = UtilDateTime.getYearEnd(date_from, timeZone, locale);
			}
		}
		
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("fromDate")){
			String fromDate_str = parameters.get("fromDate")[0];
			fromDate_time = DateUtil.convertStringTypeLongToTimestamp(fromDate_str);
		}
		if(UtilValidate.isNotEmpty(parameters) && parameters.containsKey("thruDate")){
			String thruDate_str = parameters.get("thruDate")[0];
			thruDate_time = DateUtil.convertStringTypeLongToTimestamp(thruDate_str);
		}
		
		List<Map<String,Object>> listMap_Day = PerfReviewKPIHelper.getListMapDateFromTo(fromDate_time, thruDate_time, periodTypeId);
		for (Map<String, Object> map : listMap_Day) {
			fromDate = (Timestamp) map.get("fromDate");
			thruDate = (Timestamp) map.get("thruDate");
			try {
				List<GenericValue> listKPIEnable = PerfReviewKPIHelper.getListKPIEnableByPartyId(delegator, partyId, fromDate, thruDate, periodTypeId);
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				listConds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
				listConds.add(EntityCondition.makeCondition("partyId", partyId));
				listConds.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				listConds.add(EntityCondition.makeCondition("dateReviewed", thruDate));
				List<GenericValue> listKPIWaiting = delegator.findList("PartyPerfAndCriteriaAndResult", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, opts, false);
				List<String> criteriaIdsEnable = FastList.newInstance();
				if(UtilValidate.isNotEmpty(listKPIEnable)){
					criteriaIdsEnable = EntityUtil.getFieldListFromEntityList(listKPIEnable, "criteriaId", true);
				}
				if(UtilValidate.isNotEmpty(listKPIWaiting)){
					for (GenericValue g : listKPIWaiting) {
						if(criteriaIdsEnable.contains(g.getString("criteriaId"))){
							GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", g.getString("uomId")), false);
							criteriaIdsEnable.remove(g.getString("criteriaId"));
							Map<String, Object> mapTmp = FastMap.newInstance();
							mapTmp.put("perfCriteriaTypeId", g.getString("perfCriteriaTypeId"));
							mapTmp.put("criteriaId", g.getString("criteriaId"));
							mapTmp.put("criteriaName", g.getString("criteriaName"));
							mapTmp.put("fromDate", g.getTimestamp("fromDate"));
							mapTmp.put("dateReviewed", g.getTimestamp("dateReviewed"));
							mapTmp.put("statusId", g.getString("statusId"));
							mapTmp.put("periodTypeId", g.getString("periodTypeId"));
							mapTmp.put("target", g.getBigDecimal("target"));
							mapTmp.put("uomId", g.getString("uomId"));
							mapTmp.put("weight", g.getBigDecimal("weight"));
							mapTmp.put("result", g.getBigDecimal("result"));
							mapTmp.put("comment", g.getString("comment"));
							mapTmp.put("description", g.getString("description"));
							mapTmp.put("partyId", partyId);
							mapTmp.put("description_uom", uom.getString("description"));
							listIterator.add(mapTmp);
						}
					}
					if(UtilValidate.isNotEmpty(criteriaIdsEnable)){
						for (String s : criteriaIdsEnable) {
							List<EntityCondition> listConds1 = FastList.newInstance();
							listConds1.add(EntityCondition.makeCondition("criteriaId", s));
							listConds1.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
							listConds1.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
							listConds1.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
							List<GenericValue> listTmp = delegator.findList("PartyPerfAndCriteriaAndResult", EntityCondition.makeCondition(listConds1), null, null, opts, false);
							GenericValue Tmp = EntityUtil.getFirst(listTmp);
							Map<String, Object> mapTmp = FastMap.newInstance();
							GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", Tmp.getString("uomId")), false);
							mapTmp.put("perfCriteriaTypeId", Tmp.getString("perfCriteriaTypeId"));
							mapTmp.put("criteriaId", Tmp.getString("criteriaId"));
							mapTmp.put("criteriaName", Tmp.getString("criteriaName"));
							mapTmp.put("fromDate", Tmp.getTimestamp("fromDate"));
							mapTmp.put("dateReviewed", thruDate);
							mapTmp.put("statusId", Tmp.getString("statusId"));
							mapTmp.put("periodTypeId", Tmp.getString("periodTypeId"));
							mapTmp.put("target", Tmp.getBigDecimal("target"));
							mapTmp.put("uomId", Tmp.getString("uomId"));
							mapTmp.put("weight", Tmp.getBigDecimal("weight"));
							mapTmp.put("result", null);
							mapTmp.put("comment", Tmp.getString("comment"));
							mapTmp.put("description", Tmp.getString("description"));
							mapTmp.put("partyId", partyId);
							mapTmp.put("description_uom", uom.getString("description"));
							listIterator.add(mapTmp);
						}
					}
				}else{
					for (String s : criteriaIdsEnable) {
						List<EntityCondition> listConds2 = FastList.newInstance();
						listConds2.add(EntityCondition.makeCondition("criteriaId", s));
						listConds2.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
						listConds2.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
						listConds2.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
						List<GenericValue> listTmp = delegator.findList("PartyPerfAndCriteriaAndResult", EntityCondition.makeCondition(listConds2), null, null, opts, false);
						GenericValue Tmp = EntityUtil.getFirst(listTmp);
						GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", Tmp.getString("uomId")), false);
						Map<String, Object> mapTmp = FastMap.newInstance();
						mapTmp.put("perfCriteriaTypeId", Tmp.getString("perfCriteriaTypeId"));
						mapTmp.put("criteriaId", Tmp.getString("criteriaId"));
						mapTmp.put("criteriaName", Tmp.getString("criteriaName"));
						mapTmp.put("fromDate", Tmp.getTimestamp("fromDate"));
						mapTmp.put("dateReviewed", thruDate);
						mapTmp.put("statusId", null);
						mapTmp.put("periodTypeId", Tmp.getString("periodTypeId"));
						mapTmp.put("target", Tmp.getBigDecimal("target"));
						mapTmp.put("uomId", Tmp.getString("uomId"));
						mapTmp.put("weight", Tmp.getBigDecimal("weight"));
						mapTmp.put("result", null);
						mapTmp.put("comment", null);
						mapTmp.put("description", Tmp.getString("description"));
						mapTmp.put("partyId", partyId);
						mapTmp.put("description_uom", uom.getString("description"));
						listIterator.add(mapTmp);
					}
				}
				listIterator = EntityConditionUtils.doFilter(listIterator, listAllConditions);
				listIterator = EntityConditionUtils.sortList(listIterator, listSortFields);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyKPIOverview(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> successResult = FastMap.newInstance();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyGroupId");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		if(partyGroupId != null){
			String fromDateStr = request.getParameter("fromDate");
			String thruDateStr = request.getParameter("thruDate");
			Timestamp fromDate = UtilDateTime.nowTimestamp();
			Timestamp thruDate = UtilDateTime.nowTimestamp();
			if(fromDateStr != null){
				fromDate = new Timestamp(Long.parseLong(fromDateStr));
			}
			if(thruDateStr != null){
				thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
			}
			String periodTypeId = request.getParameter("periodTypeId");
			try {
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
				List<EntityCondition> conditions = FastList.newInstance();
				if(periodTypeId != null){
					conditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				}
				conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIdList));
				EntityCondition commonConds = EntityCondition.makeCondition(conditions);
				List<GenericValue> listPartyPerfCriteria = delegator.findList("PartyPerfCriteriaSummary", commonConds, 
						UtilMisc.toSet("partyId", "criteriaId", "fullName", "firstName", "partyCode"), UtilMisc.toList("firstName"), null, false);
				
				List<String> listFieldInEntity = FastList.newInstance();
				listFieldInEntity.add("partyId");
				listFieldInEntity.add("fullName");
				listFieldInEntity.add("partyCode");
				
				List<EntityCondition> condsForFieldNotInEntity = FastList.newInstance();
				List<EntityCondition> condsForFieldInEntity = FastList.newInstance();
				EntityConditionUtils.splitFilterListCondition(listAllConditions, listFieldInEntity, condsForFieldInEntity, condsForFieldNotInEntity);
				
				List<String> sortedFieldInEntity = FastList.newInstance();
				List<String> sortedFieldNotInEntity = FastList.newInstance();
				if(listSortFields != null){
					EntityConditionUtils.splitSortedList(listSortFields, listFieldInEntity, sortedFieldInEntity, sortedFieldNotInEntity);
				}
				
				if(UtilValidate.isNotEmpty(condsForFieldInEntity)){
					listPartyPerfCriteria = EntityUtil.filterByCondition(listPartyPerfCriteria, EntityCondition.makeCondition(condsForFieldInEntity));
				}
				if(UtilValidate.isEmpty(sortedFieldInEntity)){
					sortedFieldInEntity.add("partyId");
				}
				listPartyPerfCriteria = EntityUtil.orderBy(listPartyPerfCriteria, sortedFieldInEntity);
				
				boolean isFilterAdvance = false;
				if(UtilValidate.isEmpty(condsForFieldNotInEntity) && UtilValidate.isEmpty(sortedFieldNotInEntity)){
					totalRows = listPartyPerfCriteria.size();
					if(end > listPartyPerfCriteria.size()){
						end = listPartyPerfCriteria.size();
					}
					listPartyPerfCriteria = listPartyPerfCriteria.subList(start, end);
				}else{
					isFilterAdvance = true;
				}
				if(end > listPartyPerfCriteria.size()){
					end  = listPartyPerfCriteria.size();
				}
				
				/*EntityCondition kpiNotFilledCond = EntityCondition.makeCondition("dateReviewed", null);*/
				List<EntityCondition> listCondition1=FastList.newInstance();
				listCondition1.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
				listCondition1.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listCondition1.add(EntityCondition.makeCondition("result",EntityOperator.NOT_EQUAL, null));
				EntityCondition kpiFilledCond = EntityCondition.makeCondition(listCondition1,EntityOperator.AND);
				EntityCondition kpiNotConfirmConds = EntityCondition.makeCondition(kpiFilledCond, EntityCondition.makeCondition("statusIdAppr", "KAS_WAITING"));
				EntityCondition kpiConfirmConds = EntityCondition.makeCondition(kpiFilledCond, EntityCondition.makeCondition("statusIdAppr", EntityJoinOperator.IN, UtilMisc.toList("KAS_ACCEPTED", "KAS_REJECTED")));
				for(GenericValue tempGv: listPartyPerfCriteria){
					Map<String, Object> tempMap = FastMap.newInstance();
					String partyId = tempGv.getString("partyId");
					EntityCondition partyIdConds = EntityCondition.makeCondition("partyId", partyId);
					List<GenericValue> tempListPartyPerfCriteria = delegator.findList("PartyPerfCriteriaAndResult", EntityCondition.makeCondition(partyIdConds, commonConds), null, null, null, false);
					/*List<GenericValue> kpiNotFillList = EntityUtil.filterByCondition(tempListPartyPerfCriteria, EntityCondition.makeCondition(partyIdConds, kpiNotFilledCond));*/ 
					List<GenericValue> kpiFilledList = EntityUtil.filterByCondition(tempListPartyPerfCriteria, EntityCondition.makeCondition(partyIdConds, kpiFilledCond));
					List<GenericValue> kpiNotConfirmList = EntityUtil.filterByCondition(tempListPartyPerfCriteria, EntityCondition.makeCondition(partyIdConds, kpiNotConfirmConds));
					List<GenericValue> kpiConfirmFillList = EntityUtil.filterByCondition(tempListPartyPerfCriteria, EntityCondition.makeCondition(partyIdConds, kpiConfirmConds));
					List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
					List<String> departmentNameList = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", departmentList, "partyId","groupName");
					tempMap.put("department", StringUtils.join(departmentNameList, ", "));
					tempMap.put("partyId", partyId);
					tempMap.put("partyCode", tempGv.getString("partyCode"));
					tempMap.put("partyName", tempGv.get("fullName"));
					/*tempMap.put("totalKpiNotFill", kpiNotFillList.size());*/
					tempMap.put("totalKpiFill", kpiFilledList.size());
					tempMap.put("totalKpiNotConfirm", kpiNotConfirmList.size());
					tempMap.put("totalKpiConfirm", kpiConfirmFillList.size());
					
					listIterator.add(tempMap);
				}
				if(isFilterAdvance){
					if(UtilValidate.isNotEmpty(condsForFieldNotInEntity)){
						listIterator = EntityConditionUtils.doFilter(listIterator, condsForFieldNotInEntity);
					}
					if(UtilValidate.isNotEmpty(sortedFieldNotInEntity)){
						listIterator = EntityConditionUtils.sortList(listIterator, sortedFieldNotInEntity);
					}
					totalRows = listIterator.size();
					if(end > listIterator.size()){
						end = listIterator.size();
					}
					listIterator = listIterator.subList(start, end);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
		}
		successResult.put("listIterator", listIterator);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplListKPI(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	listSortFields.add("-fromDate");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
    	try {
    		if(partyId != null){
    			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    			listIterator = delegator.find("PartyPerfCriteriaAndCriteria", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling KPI service: " + e.toString();
			Debug.logError(e, errMsg);
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDistributorReviewdKPIByAssessment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String perfCriteriaAssessmentId = parameters.get("perfCriteriaAssessmentId") != null? parameters.get("perfCriteriaAssessmentId")[0] : null;
    	try {
			GenericValue perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return successResult;
			}
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("fullName");
			}
			listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			//listAllConditions.add(EntityCondition.makeCondition("criteriaId", EntityJoinOperator.IN, UtilMisc.toList("KPI_SKU_SALE", "KPI_TURNOVER_SALE")));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "KAS_ACCEPTED"));
			Set<String> selectedField = FastSet.newInstance();
			selectedField.add("partyId");
			selectedField.add("fullName");
			selectedField.add("partyCode");
			listIterator = delegator.find("DistributorPerfCriteriaResultGroup", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), 
					null, selectedField, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getDistributorTurnoverDetailInAssess(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		String perfCriteriaAssessmentId = (String)context.get("perfCriteriaAssessmentId");
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			GenericValue perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return ServiceUtil.returnError("cannot find assessment");
			}
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("statusIdAppr", "KAS_ACCEPTED"));
			conds.add(EntityCondition.makeCondition("criteriaId", PropertiesUtil.KPI_TURN_OVER));
			conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> partyPerfCriList = delegator.findList("PartyPerfCriteriaAndResult", EntityCondition.makeCondition(conds), null, null, null, false);
			BigDecimal tempPoint = PerfReviewKPIHelper.calcKPIPoint(delegator, partyPerfCriList, PropertiesUtil.KPI_TURN_OVER);
			for(GenericValue partyPerfCri: partyPerfCriList){
				Map<String, Object> tempMap = FastMap.newInstance();
				BigDecimal target = partyPerfCri.getBigDecimal("target");
				BigDecimal result = partyPerfCri.getBigDecimal("result");
				tempMap.put("target", partyPerfCri.get("target"));
				tempMap.put("result", partyPerfCri.get("result"));
				Map<String, Object> bonusPunishmentAmountResult = PerfReviewKPIHelper.getBonusPunishmentAmountKPI(delegator, tempPoint, PropertiesUtil.KPI_TURN_OVER, thruDate, result, target);
				if(bonusPunishmentAmountResult != null){
					tempMap.put("bonusAmount", bonusPunishmentAmountResult.get("amount"));
				}
				listReturn.add(tempMap);
			}
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> getDistributorSKUDetailInAssess(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String partyId = (String)context.get("partyId");
		String perfCriteriaAssessmentId = (String)context.get("perfCriteriaAssessmentId");
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			GenericValue perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return ServiceUtil.returnError("cannot find assessment");
			}
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("statusIdAppr", "KAS_ACCEPTED"));
			conds.add(EntityCondition.makeCondition("criteriaId", PropertiesUtil.KPI_SKU));
			conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> partyPerfCriList = delegator.findList("PartyPerfCriteriaAndResult", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyPerfCriList)){
				BigDecimal tempPoint = PerfReviewKPIHelper.calcKPIPoint(delegator, partyPerfCriList, PropertiesUtil.KPI_SKU);
				GenericValue partyPerfCri = partyPerfCriList.get(0);
				List<GenericValue> listProducts = delegator.findByAnd("PartyPerfCriItemProductAndResult", UtilMisc.toMap("partyId", partyId,
																						"fromDate", partyPerfCri.get("fromDate"),
																						"criteriaId", PropertiesUtil.KPI_SKU,
																						"dateReviewed", partyPerfCri.get("dateReviewed")), UtilMisc.toList("productName"), false);
				Map<String, Object> bonusPunishmentAmountResult = PerfReviewKPIHelper.getBonusPunishmentAmountKPI(delegator, tempPoint, 
						PropertiesUtil.KPI_SKU, thruDate, partyPerfCri.getBigDecimal("result"), partyPerfCri.getBigDecimal("target"));
				if(bonusPunishmentAmountResult != null){
					successResult.put("bonusAmount", bonusPunishmentAmountResult.get("amount"));
				}
				for(GenericValue tempGv: listProducts){
					Map<String, Object> tempMap = tempGv.getFields(UtilMisc.toList("partyId", "productCode", "productName", "quantityTarget", "result"));
					listReturn.add(tempMap);
				}
			}
			successResult.put("listReturn", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPerfCriteriaAssessmentParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String,Object>> listIterator = new ArrayList<Map<String,Object>>();
    	String perfCriteriaAssessmentId = parameters.get("perfCriteriaAssessmentId") != null? parameters.get("perfCriteriaAssessmentId")[0] : null;
    	listSortFields.add("firstName");
    	if(perfCriteriaAssessmentId != null){
    		listAllConditions.add(EntityCondition.makeCondition("perfCriteriaAssessmentId", perfCriteriaAssessmentId));
    		try {
				List<GenericValue> perfCriteriaAssessmentPartyList = delegator.findList("PerfCriteriaAssessmentPartyAndRGAndCTP", 
						EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
				totalRows = perfCriteriaAssessmentPartyList.size();
				if(end > totalRows){
					end = totalRows;
				}
				perfCriteriaAssessmentPartyList = perfCriteriaAssessmentPartyList.subList(start, end);
				for(GenericValue tempGv: perfCriteriaAssessmentPartyList){
					Map<String, Object> tempMap = tempGv.getAllFields();
					String customTimePeriodId = tempGv.getString("customTimePeriodId");
					if(customTimePeriodId != null){
						GenericValue customPeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
						String parentPeriodId = customPeriod.getString("parentPeriodId");
						String periodName = CommonUtil.getMonthYearPeriodNameByCustomTimePeriod(delegator, customTimePeriodId);
						tempMap.put("periodName", periodName);
						tempMap.put("yearCustomTimePeriodId", parentPeriodId);
					}
					listIterator.add(tempMap);
				}
				successResult.put("listIterator", listIterator);
				successResult.put("TotalRows", String.valueOf(totalRows));
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
    	}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPartyPerfCriteria(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String criteriaId = (String)context.get("criteriaId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		BigDecimal weight = (BigDecimal)context.get("weight");
		List<Map<String, Object>> listProducts = (List<Map<String, Object>>)context.get("listProducts");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("statusId", "KPI_ACTIVE"));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		try {
			List<GenericValue> partyPerfCriteriaList = delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyPerfCriteriaList)){
				GenericValue errPartyKPI = partyPerfCriteriaList.get(0);
				GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
				String criteriaName = perfCriteria.getString("criteriaName");
				Timestamp fromDateErr = errPartyKPI.getTimestamp("fromDate");
				Timestamp thruDateErr = errPartyKPI.getTimestamp("thruDate");
				String errMsg = "";
				Map<String, Object> errMap = FastMap.newInstance();
				errMap.put("criteriaName", criteriaName);
				errMap.put("fromDateSet", DateUtil.getDateMonthYearDesc(fromDate));
				errMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr));
				errMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				String propertyKey = "EmplKPIIsSetFromFrom";
				if(thruDate == null && thruDateErr != null){
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplKPIIsSetFromFromThru";
				}else if(thruDate != null && thruDateErr == null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					propertyKey = "EmplKPIIsSetFromThruFrom";
				}else if(thruDate != null && thruDateErr != null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplKPIIsSetFromThruFromThru";
				}
				errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", propertyKey, errMap, locale);
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue partyPerfCriteria = delegator.makeValue("PartyPerfCriteria");
			partyPerfCriteria.setAllFields(context, false, null, null);
			partyPerfCriteria.put("fromDate", fromDate);
			partyPerfCriteria.put("statusId", "KPI_ACTIVE");
			partyPerfCriteria.put("weight", weight);
			delegator.create(partyPerfCriteria);
			if(UtilValidate.isNotEmpty(listProducts)){
				partyPerfCriteria.put("hasItem", "Y");
				partyPerfCriteria.store();
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("userLogin", context.get("userLogin"));
				ctxMap.put("partyId", partyId);
				ctxMap.put("criteriaId", criteriaId);
				ctxMap.put("fromDate", fromDate);
				for(Map<String, Object> tempMap: listProducts){
					ctxMap.put("productId", tempMap.get("productId"));
					ctxMap.put("quantityTarget", tempMap.get("quantityTarget"));
					ctxMap.put("uomId", tempMap.get("uomId"));
					Map<String, Object> resultService = dispatcher.runSync("createPartyPerfCriteriaItemProduct", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> createPartyPerfCriteriaItemProduct(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyPerfCriteriaItemProduct = delegator.makeValue("PartyPerfCriteriaItemProduct"); 
		partyPerfCriteriaItemProduct.setAllFields(context, false, null, null);
		try {
			delegator.create(partyPerfCriteriaItemProduct);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", (Locale)context.get("locale")));
	}
	
	public static Map<String, Object> updatePartyPerfCriteria(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String criteriaId = (String)context.get("criteriaId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("statusId", "KPI_ACTIVE"));
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("criteriaId", criteriaId));
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.NOT_EQUAL, fromDate));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		try {
			GenericValue partyPerfCriteria = delegator.findOne("PartyPerfCriteria", UtilMisc.toMap("partyId", partyId, "criteriaId", criteriaId, "fromDate", fromDate), false);
			if(partyPerfCriteria == null){
				return ServiceUtil.returnError("cannot find record PartyPerfCriteria to update");
			}
			List<GenericValue> partyPerfCriteriaList = delegator.findList("PartyPerfCriteria", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyPerfCriteriaList)){
				GenericValue errPartyKPI = partyPerfCriteriaList.get(0);
				GenericValue perfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
				String criteriaName = perfCriteria.getString("criteriaName");
				Timestamp fromDateErr = errPartyKPI.getTimestamp("fromDate");
				Timestamp thruDateErr = errPartyKPI.getTimestamp("thruDate");
				String errMsg = "";
				Map<String, Object> errMap = FastMap.newInstance();
				errMap.put("criteriaName", criteriaName);
				errMap.put("fromDateSet", DateUtil.getDateMonthYearDesc(fromDate));
				errMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDateErr));
				errMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				String propertyKey = "EmplKPIIsSetFromFrom";
				if(thruDate == null && thruDateErr != null){
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplKPIIsSetFromFromThru";
				}else if(thruDate != null && thruDateErr == null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					propertyKey = "EmplKPIIsSetFromThruFrom";
				}else if(thruDate != null && thruDateErr != null){
					errMap.put("thruDateSet", DateUtil.getDateMonthYearDesc(thruDate));
					errMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDateErr));
					propertyKey = "EmplKPIIsSetFromThruFromThru";
				}
				errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", propertyKey, errMap, locale);
				return ServiceUtil.returnError(errMsg);
			}
			partyPerfCriteria.setNonPKFields(context);
			//partyPerfCriteria.set("weight", weight);
			delegator.store(partyPerfCriteria);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	public static Map<String, Object> deletePartyPerfCriteria(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String criteriaId = (String)context.get("criteriaId");
		String partyId = (String)context.get("partyId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		try {
			GenericValue partyPerfCriteria = delegator.findOne("PartyPerfCriteria", 
					UtilMisc.toMap("criteriaId", criteriaId, "partyId", partyId, "fromDate", fromDate), false);
			if(partyPerfCriteria == null){
				return ServiceUtil.returnError("cannot find record PartyPerfCriteria to delete");
			}
			List<GenericValue> partyPerfCriteriaResultList = delegator.findByAnd("PartyPerfCriteriaResult", 
					UtilMisc.toMap("criteriaId", criteriaId, "partyId", partyId, "fromDate", fromDate), null, false);
			if(UtilValidate.isNotEmpty(partyPerfCriteriaResultList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotDeletePartyPerfCriteriaBecauseUsed", locale));
			}
			partyPerfCriteria.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	public static Map<String, Object> getPerfCriteriaType(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			List<GenericValue> perfCriteriaTypeList = delegator.findList("PerfCriteriaType", null, UtilMisc.toSet("perfCriteriaTypeId", "description"), UtilMisc.toList("description"), null, false);
			successResult.put("listReturn", perfCriteriaTypeList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateKPIForEmp(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listProducts = (List<Map<String, Object>>)context.get("listProducts");
		String criteriaId = (String) context.get("criteriaId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		BigDecimal result = (BigDecimal) context.get("result");
		String periodTypeId = (String) context.get("periodTypeId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyId = (String) context.get("partyId");
		Timestamp dateReviewed = (Timestamp) context.get("dateReviewed");
		Date dateReviewedDate = new Date(dateReviewed.getTime());
		
		//Date date = new Date();
        Timestamp dayOfSetKPI = UtilDateTime.getDayEnd(dateReviewed);
        Timestamp monthOfSetKPI = DateUtil.getDateEndOfMonth(dateReviewedDate);
        Timestamp yearOfSetKPI = DateUtil.getDateEndOfYear(dateReviewedDate);
        Timestamp quarterOfSetKPI = DateUtil.getDateEndOfQuarter(dateReviewedDate);
        Timestamp weekOfSetKPI = DateUtil.getDateEndOfWeek(dateReviewedDate);
		try {
			GenericValue PerfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			Map<String, Object> mapTmp = FastMap.newInstance();
			mapTmp.putAll(PerfCriteria);
			List<String> managerId = PartyUtil.getManagerOfEmpl(delegator, partyId, UtilDateTime.nowTimestamp(), userLoginId);
			if(UtilValidate.isEmpty(managerId)){
				managerId.add(partyId);
			}
			GenericValue kpi = delegator.findOne("PartyPerfCriteria", UtilMisc.toMap("partyId", partyId, "criteriaId", criteriaId, "fromDate", fromDate), false);
			if(UtilValidate.isNotEmpty(kpi)){
				if(dateReviewed.before(fromDate) || (UtilValidate.isNotEmpty(kpi.getTimestamp("thruDate")) && dateReviewed.after(kpi.getTimestamp("thruDate")))){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "InvalidTimeAssessment", locale));
				}
			}
			Map<String, Object> resultService = null;
			Timestamp dateReview = null;
			if("DAILY".equals(periodTypeId)){
				resultService = storeKPIEmpl(delegator, partyId, criteriaId, fromDate, dayOfSetKPI, locale, result);
				dateReview = dayOfSetKPI;
			}
			else if("MONTHLY".equals(periodTypeId)){
				resultService = storeKPIEmpl(delegator, partyId, criteriaId, fromDate, monthOfSetKPI, locale, result);
				dateReview = monthOfSetKPI;
			}
			else if("YEARLY".equals(periodTypeId)){
				resultService = storeKPIEmpl(delegator, partyId, criteriaId, fromDate, yearOfSetKPI, locale, result);
				dateReview = yearOfSetKPI;
			}
			else if("QUARTERLY".equals(periodTypeId)){
				resultService = storeKPIEmpl(delegator, partyId, criteriaId, fromDate, quarterOfSetKPI, locale, result);
				dateReview = quarterOfSetKPI;
			}
			else if("WEEKLY".equals(periodTypeId)){
				resultService = storeKPIEmpl(delegator, partyId, criteriaId, fromDate, weekOfSetKPI, locale, result);
				dateReview = weekOfSetKPI;
			}
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String hasItem = kpi.getString("hasItem");
			if("Y".equals(hasItem) && listProducts != null){
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("locale", locale);
				ctxMap.put("partyId", partyId);
				ctxMap.put("criteriaId", criteriaId);
				ctxMap.put("fromDate", fromDate);
				ctxMap.put("dateReviewed", dateReview);
				for(Map<String, Object> tempMap: listProducts){
					ctxMap.put("productId", tempMap.get("productId"));
					ctxMap.put("result", tempMap.get("quantityActual"));
					resultService = dispatcher.runSync("updatePartyPerfCriItemProductResult", ctxMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	public static Map<String,Object> storeKPIEmpl(Delegator delegator, String partyId, String criteriaId, Timestamp fromDate, 
			Timestamp dateCheckKPI, Locale locale, BigDecimal result){
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		try {
			GenericValue resultKPI = delegator.findOne("PartyPerfCriteriaResult", UtilMisc.toMap("partyId", partyId, "criteriaId", criteriaId,
					"fromDate", fromDate, "dateReviewed", dateCheckKPI), false);
			if(UtilValidate.isNotEmpty(resultKPI)){
				resultKPI.set("result", result);
				resultKPI.set("statusId", "KAS_WAITING");
				resultKPI.store();
			}else{
				GenericValue newKPI = delegator.makeValue("PartyPerfCriteriaResult");
				newKPI.set("partyId", partyId);
				newKPI.set("criteriaId", criteriaId);
				newKPI.set("fromDate", fromDate);
				newKPI.set("dateReviewed", dateCheckKPI);
				newKPI.set("result", result);
				newKPI.set("statusId", "KAS_WAITING");
				newKPI.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> updatePartyPerfCriItemProductResult(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue partyPerfCriItemProductResult = delegator.makeValue("PartyPerfCriItemProductResult");
		partyPerfCriItemProductResult.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(partyPerfCriItemProductResult);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKPIEmplToApprove(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//String partyId_userLogin = userLogin.getString("partyId");
		EntityListIterator listIterator = null;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		try {
			String periodTypeId = "";
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			Timestamp _fromDate = null;
			Timestamp _thruDate = null;
			int month_from = 0;
			int year_from = 0;
			int month_to = 0; 
			int year_to = 0;
			int quarter_from = 0;
			int year_from_quarter = 0;
			int quarter_to = 0;
			int year_to_quarter = 0;
			int year = 0;
			if(UtilValidate.isNotEmpty(parameters)){
				if(parameters.get("periodTypeId")[0] == null){
					return successResult;
				}else{
					periodTypeId = (String) parameters.get("periodTypeId")[0];
				}
				if(parameters.containsKey("fromDate") && parameters.get("fromDate")[0] != null){
					_fromDate = DateUtil.convertStringTypeLongToTimestamp(parameters.get("fromDate")[0]);
				}
				if(parameters.containsKey("thruDate") && parameters.get("thruDate")[0] != null){
					_thruDate = DateUtil.convertStringTypeLongToTimestamp(parameters.get("thruDate")[0]);
				}
				if(parameters.containsKey("month_from") && parameters.get("month_from")[0] != null){
					month_from =  Integer.parseInt(parameters.get("month_from")[0]);
				}
				if(parameters.containsKey("year_from") && parameters.get("year_from")[0] != null){
					year_from = Integer.parseInt(parameters.get("year_from")[0]);
				}
				if(parameters.containsKey("month_to") && parameters.get("month_to")[0] != null){
					month_to = Integer.parseInt(parameters.get("month_to")[0]);
				}
				if(parameters.containsKey("year_to") && parameters.get("year_to")[0] != null){
					year_to = Integer.parseInt(parameters.get("year_to")[0]);
				}
				if(month_from != 0 && year_from != 0 && month_to != 0 && year_to != 0){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from, month_from);
					_fromDate = UtilDateTime.getMonthStart(date_from);
					
					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to, month_to);
					_thruDate = UtilDateTime.getMonthEnd(date_to, timeZone, locale);
				}
				if(parameters.containsKey("quarter_from") && parameters.get("quarter_from")[0] != null){
					quarter_from = Integer.parseInt(parameters.get("quarter_from")[0]);
				}
				if(parameters.containsKey("year_from_quarter") && parameters.get("year_from_quarter")[0] != null){
					year_from_quarter = Integer.parseInt(parameters.get("year_from_quarter")[0]);
				}
				if(parameters.containsKey("quarter_to") && parameters.get("quarter_to")[0] != null){
					quarter_to = Integer.parseInt(parameters.get("quarter_to")[0]);
				}
				if(parameters.containsKey("year_to_quarter") && parameters.get("year_to_quarter")[0] != null){
					year_to_quarter = Integer.parseInt(parameters.get("year_to_quarter")[0]);
				}
				if(quarter_from != 0 && year_from_quarter != 0 && quarter_to != 0 && year_to_quarter != 0){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from_quarter, (quarter_from-1)*3);
					Date date_from_date = new Date(date_from.getTime());
					_fromDate = DateUtil.getDateStartOfQuarter(date_from_date);
					
					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to_quarter, (quarter_to-1) * 3);
					Date date_to_date = new Date(date_to.getTime());
					_thruDate = DateUtil.getDateEndOfQuarter(date_to_date);
				}
				if(parameters.containsKey("year") && parameters.get("year")[0] != null){
					year = Integer.parseInt(parameters.get("year")[0]);
					if(year != 0){
						Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year, 1);
						_fromDate = UtilDateTime.getYearStart(date_from);
						_thruDate = UtilDateTime.getYearEnd(date_from, timeZone, locale);
					}
				}
				
			}
			if(UtilValidate.isNotEmpty(periodTypeId)){
				/*List<GenericValue> listCurrenKPI = PerfReviewKPIHelper.getListKPIEnableByPartyId(delegator, null, _fromDate, _thruDate, periodTypeId);
				List<Timestamp> fromDate_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "fromDate", true);
				List<String> criteriaId_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "criteriaId", true);
				List<String> partyId_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "partyId", true);
				List<String> deptId_list = PartyUtil.getDepartmentOfEmployee(delegator, partyId_userLogin, UtilDateTime.nowTimestamp());
				String deptId = deptId_list.get(0);
				Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, true, false);
				List<GenericValue> party_childList = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
				List<String> partyId_childList = EntityUtil.getFieldListFromEntityList(party_childList, "partyId", true);
				List<String> partyId_childList_enable = FastList.newInstance();
				for (String s : partyId_childList) {
					if(partyId_list.contains(s)){
						partyId_childList_enable.add(s);
					}
				}*/
				List<String> emplListOfUserLogin = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), _fromDate, _thruDate);
				
				if(periodTypeId.equals("DAILY")){
					fromDate = UtilDateTime.getDayEnd(_fromDate);
					thruDate = UtilDateTime.getDayEnd(_thruDate);
				}else if(periodTypeId.equals("WEEKLY")){
					fromDate = UtilDateTime.getWeekEnd(_fromDate);
					thruDate = UtilDateTime.getWeekEnd(_thruDate);
				}else if(periodTypeId.equals("MONTHLY")){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from, month_from);
					fromDate = UtilDateTime.getMonthEnd(date_from, timeZone, locale);
					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to, month_to);
					thruDate = UtilDateTime.getMonthEnd(date_to, timeZone, locale);
				}else if(periodTypeId.equals("QUARTERLY")){
					Date _fromDate_Date = new Date(_fromDate.getTime());
					Date _thruDate_Date = new Date(_thruDate.getTime());
					fromDate = DateUtil.getDateEndOfQuarter(_fromDate_Date);
					thruDate = DateUtil.getDateEndOfQuarter(_thruDate_Date);
				}else if(periodTypeId.equals("YEARLY")){
					fromDate = UtilDateTime.getYearStart(_fromDate);
					thruDate = UtilDateTime.getYearEnd(_thruDate, timeZone, locale);
				}
				/*listAllConditions.add(EntityCondition.makeCondition("criteriaId", EntityJoinOperator.IN, criteriaId_list));
				listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.IN, fromDate_list));*/
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				listAllConditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				listAllConditions.add(EntityCondition.makeCondition("result", EntityOperator.NOT_EQUAL,null));
				listAllConditions.add(EntityCondition.makeCondition("partyId" , EntityJoinOperator.IN, emplListOfUserLogin));
				//listAllConditions.add(EntityCondition.makeCondition("result", EntityJoinOperator.NOT_EQUAL, null));
				/*List<EntityCondition> listCond_child = FastList.newInstance();
				listCond_child.add(EntityCondition.makeCondition("statusId", "KAS_WAITING"));
				listCond_child.add(EntityCondition.makeCondition("statusId", "KAS_ACCEPTED"));
				listAllConditions.add(EntityCondition.makeCondition(listCond_child, EntityJoinOperator.OR));*/
				if(UtilValidate.isEmpty(listSortFields)){
					listSortFields.add("firstName");
				}
				listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyPerfAndCriteriaAndResultAndParty",
						EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> getListKPIResultEmpl(DispatchContext ctx, Map<String, Object> context){
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//String partyId_userLogin = userLogin.getString("partyId");
		EntityListIterator listIterator = null;
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		try {
			String periodTypeId = "";
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			Timestamp _fromDate = null;
			Timestamp _thruDate = null;
			int month_from = 0;
			int year_from = 0;
			int month_to = 0;
			int year_to = 0;
			int quarter_from = 0;
			int year_from_quarter = 0;
			int quarter_to = 0;
			int year_to_quarter = 0;
			int year = 0;
			if(UtilValidate.isNotEmpty(parameters)){
				if(parameters.get("periodTypeId")[0] == null){
					return successResult;
				}else{
					periodTypeId = (String) parameters.get("periodTypeId")[0];
				}
				if(parameters.containsKey("fromDate") && parameters.get("fromDate")[0] != null){
					_fromDate = DateUtil.convertStringTypeLongToTimestamp(parameters.get("fromDate")[0]);
				}
				if(parameters.containsKey("thruDate") && parameters.get("thruDate")[0] != null){
					_thruDate = DateUtil.convertStringTypeLongToTimestamp(parameters.get("thruDate")[0]);
				}
				if(parameters.containsKey("month_from") && parameters.get("month_from")[0] != null){
					month_from =  Integer.parseInt(parameters.get("month_from")[0]);
				}
				if(parameters.containsKey("year_from") && parameters.get("year_from")[0] != null){
					year_from = Integer.parseInt(parameters.get("year_from")[0]);
				}
				if(parameters.containsKey("month_to") && parameters.get("month_to")[0] != null){
					month_to = Integer.parseInt(parameters.get("month_to")[0]);
				}
				if(parameters.containsKey("year_to") && parameters.get("year_to")[0] != null){
					year_to = Integer.parseInt(parameters.get("year_to")[0]);
				}
				if(month_from != 0 && year_from != 0 && month_to != 0 && year_to != 0){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from, month_from);
					_fromDate = UtilDateTime.getMonthStart(date_from);

					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to, month_to);
					_thruDate = UtilDateTime.getMonthEnd(date_to, timeZone, locale);
				}
				if(parameters.containsKey("quarter_from") && parameters.get("quarter_from")[0] != null){
					quarter_from = Integer.parseInt(parameters.get("quarter_from")[0]);
				}
				if(parameters.containsKey("year_from_quarter") && parameters.get("year_from_quarter")[0] != null){
					year_from_quarter = Integer.parseInt(parameters.get("year_from_quarter")[0]);
				}
				if(parameters.containsKey("quarter_to") && parameters.get("quarter_to")[0] != null){
					quarter_to = Integer.parseInt(parameters.get("quarter_to")[0]);
				}
				if(parameters.containsKey("year_to_quarter") && parameters.get("year_to_quarter")[0] != null){
					year_to_quarter = Integer.parseInt(parameters.get("year_to_quarter")[0]);
				}
				if(quarter_from != 0 && year_from_quarter != 0 && quarter_to != 0 && year_to_quarter != 0){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from_quarter, (quarter_from-1)*3);
					Date date_from_date = new Date(date_from.getTime());
					_fromDate = DateUtil.getDateStartOfQuarter(date_from_date);

					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to_quarter, (quarter_to-1) * 3);
					Date date_to_date = new Date(date_to.getTime());
					_thruDate = DateUtil.getDateEndOfQuarter(date_to_date);
				}
				if(parameters.containsKey("year") && parameters.get("year")[0] != null){
					year = Integer.parseInt(parameters.get("year")[0]);
					if(year != 0){
						Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year, 1);
						_fromDate = UtilDateTime.getYearStart(date_from);
						_thruDate = UtilDateTime.getYearEnd(date_from, timeZone, locale);
					}
				}

			}
			if(UtilValidate.isNotEmpty(periodTypeId)){
				/*List<GenericValue> listCurrenKPI = PerfReviewKPIHelper.getListKPIEnableByPartyId(delegator, null, _fromDate, _thruDate, periodTypeId);
				List<Timestamp> fromDate_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "fromDate", true);
				List<String> criteriaId_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "criteriaId", true);
				List<String> partyId_list = EntityUtil.getFieldListFromEntityList(listCurrenKPI, "partyId", true);
				List<String> deptId_list = PartyUtil.getDepartmentOfEmployee(delegator, partyId_userLogin, UtilDateTime.nowTimestamp());
				String deptId = deptId_list.get(0);
				Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, true, false);
				List<GenericValue> party_childList = buildOrg.getDirectEmployee(delegator, UtilDateTime.nowTimestamp(), null);
				List<String> partyId_childList = EntityUtil.getFieldListFromEntityList(party_childList, "partyId", true);
				List<String> partyId_childList_enable = FastList.newInstance();
				for (String s : partyId_childList) {
					if(partyId_list.contains(s)){
						partyId_childList_enable.add(s);
					}
				}*/
				List<String> emplListOfUserLogin = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), _fromDate, _thruDate);

				if(periodTypeId.equals("DAILY")){
					fromDate = UtilDateTime.getDayEnd(_fromDate);
					thruDate = UtilDateTime.getDayEnd(_thruDate);
				}else if(periodTypeId.equals("WEEKLY")){
					fromDate = UtilDateTime.getWeekEnd(_fromDate);
					thruDate = UtilDateTime.getWeekEnd(_thruDate);
				}else if(periodTypeId.equals("MONTHLY")){
					Timestamp date_from = PerfReviewKPIHelper.startDayMonth(year_from, month_from);
					fromDate = UtilDateTime.getMonthStart(date_from, timeZone, locale);
					Timestamp date_to = PerfReviewKPIHelper.startDayMonth(year_to, month_to);
					thruDate = UtilDateTime.getMonthEnd(date_to, timeZone, locale);
				}else if(periodTypeId.equals("QUARTERLY")){
					Date _fromDate_Date = new Date(_fromDate.getTime());
					Date _thruDate_Date = new Date(_thruDate.getTime());
					fromDate = DateUtil.getDateEndOfQuarter(_fromDate_Date);
					thruDate = DateUtil.getDateEndOfQuarter(_thruDate_Date);
				}else if(periodTypeId.equals("YEARLY")){
					fromDate = UtilDateTime.getYearStart(_fromDate);
					thruDate = UtilDateTime.getYearEnd(_thruDate, timeZone, locale);
				}
				List<EntityCondition> listConditions=FastList.newInstance();
				listConditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				listConditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				listConditions.add(EntityCondition.makeCondition("partyId" , EntityJoinOperator.IN, emplListOfUserLogin));
				List<GenericValue> listCriteria=delegator.findList("PartyPerfCriteria",EntityCondition.makeCondition(listConditions, EntityJoinOperator.AND),UtilMisc.toSet("partyId", "criteriaId","fromDate","thruDate"), null, null, true);
				for(GenericValue item:listCriteria)
				{
					if(item.getTimestamp("thruDate").compareTo(thruDate)>=0) {
						String partyIdCheck = item.getString("partyId");
						String criteriaIdCheck = item.getString("criteriaId");
						Timestamp fromDateCheck = item.getTimestamp("fromDate");
						GenericValue entityCriteriaResult = delegator.findOne("PartyPerfCriteriaResult", false, UtilMisc.toMap("partyId", partyIdCheck, "criteriaId", criteriaIdCheck, "fromDate", fromDateCheck, "dateReviewed", thruDate));
						if (entityCriteriaResult == null) {
							GenericValue makeCriResult = delegator.makeValue("PartyPerfCriteriaResult");
							makeCriResult.set("partyId", partyIdCheck);
							makeCriResult.set("criteriaId", criteriaIdCheck);
							makeCriResult.set("fromDate", fromDateCheck);
							makeCriResult.set("dateReviewed", thruDate);
							makeCriResult.create();
						}
					}
				}
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				listAllConditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				listAllConditions.add(EntityCondition.makeCondition("partyId" , EntityJoinOperator.IN, emplListOfUserLogin));
				if(UtilValidate.isEmpty(listSortFields)){
					listSortFields.add("firstName");
				}
				listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "PartyPerfAndCriteriaAndResultAndParty",
						EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> approveKPIEmpl(DispatchContext ctx, Map<String, Object> context){
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		String fromDate = (String) context.get("fromDate");
		String criteriaId = (String) context.get("criteriaId");
		String date = (String) context.get("dateReviewed");
		String partyId = (String) context.get("partyId");
		String periodTypeId = (String) context.get("periodTypeId");
		String comment = (String) context.get("comment");
		String statusId = (String) context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Timestamp fromDateTimestamp = DateUtil.convertStringTypeLongToTimestamp(fromDate);
		Timestamp dateTimestamp = DateUtil.convertStringTypeLongToTimestamp(date);
		Date dateDate = new Date(dateTimestamp.getTime());
		String sendNtfToEmpl = (String)context.get("sendNtfToEmpl");
		try {
			Timestamp dateReviewed = new Timestamp(dateTimestamp.getTime());
			if("DAILY".equals(periodTypeId)){
				dateReviewed = UtilDateTime.getDayEnd(dateReviewed);
			}
			if("WEEKLY".equals(periodTypeId)){
				dateReviewed = UtilDateTime.getWeekEnd(dateReviewed);
			}
			if("MONTHLY".equals(periodTypeId)){
				dateReviewed = UtilDateTime.getMonthEnd(dateReviewed, timeZone, locale);
			}
			if("QUARTERLY".equals(periodTypeId)){
				dateReviewed = DateUtil.getDateEndOfQuarter(dateDate);
			}
			if("YEARLY".equals(periodTypeId)){
				dateReviewed = UtilDateTime.getYearEnd(dateReviewed, timeZone, locale);
			}
			GenericValue PartyPerfCriteriaResult = delegator.findOne("PartyPerfCriteriaResult", UtilMisc.toMap("partyId", partyId,
					"criteriaId", criteriaId,
					"fromDate", fromDateTimestamp,
					"dateReviewed", dateReviewed), false);
			if(UtilValidate.isNotEmpty(PartyPerfCriteriaResult)){
				PartyPerfCriteriaResult.set("statusId", statusId);
				PartyPerfCriteriaResult.set("comment", comment);
				PartyPerfCriteriaResult.store();
			}else{
				return ServiceUtil.returnError("error");
			}
			if("Y".equals(sendNtfToEmpl)){
				GenericValue criteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
				String criteriaName = criteria.getString("criteriaName");
				String header = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "KPIIsApproved",
						UtilMisc.toMap("criteriaName", criteriaName, "dateReviewed", DateUtil.getDateMonthYearDesc(dateReviewed)), locale);
				String action = "ViewListEmplKPI";
				CommonUtil.sendNotify(dispatcher, locale, partyId, userLogin, header, action, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	public static Map<String, Object> pushResultDataKPI(DispatchContext ctx, Map<String, Object> context){
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		String fromDate = (String) context.get("fromDate");
		String criteriaId = (String) context.get("criteriaId");
		//String date = (String) context.get("dateReviewed");
		String thruDate = (String) context.get("thruDate");
		String partyId = (String) context.get("partyId");
		String periodTypeId = (String) context.get("periodTypeId");
		String comment = (String) context.get("comment");
		String kpiActualStr = (String) context.get("kpiActual");
		BigDecimal kpiActual = BigDecimal.ZERO;
		String statusId = "KAS_WAITING";
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Timestamp fromDateTimestamp = DateUtil.convertStringTypeLongToTimestamp(fromDate);
		//Timestamp dateTimestamp = DateUtil.convertStringTypeLongToTimestamp(date);

		//String sendNtfToEmpl = (String)context.get("sendNtfToEmpl");
		try {
			try {
				kpiActual = new BigDecimal(kpiActualStr);
			} catch (Exception e) {
				Debug.logWarning(e, "Problems parsing quantity string: " + kpiActualStr);
				kpiActual = new BigDecimal("-1");
			}
			if(UtilValidate.isNotEmpty(thruDate)){
				Timestamp dateTimestamp = DateUtil.convertStringTypeLongToTimestamp(thruDate);
				Date dateDate = new Date(dateTimestamp.getTime());
				Timestamp dateReviewed = new Timestamp(dateTimestamp.getTime());
				if("DAILY".equals(periodTypeId)){
					dateReviewed = UtilDateTime.getDayEnd(dateReviewed);
				}
				if("WEEKLY".equals(periodTypeId)){
					dateReviewed = UtilDateTime.getWeekEnd(dateReviewed);
				}
				if("MONTHLY".equals(periodTypeId)){
					dateReviewed = UtilDateTime.getMonthEnd(dateReviewed, timeZone, locale);
				}
				if("QUARTERLY".equals(periodTypeId)){
					dateReviewed = DateUtil.getDateEndOfQuarter(dateDate);
				}
				if("YEARLY".equals(periodTypeId)){
					dateReviewed = UtilDateTime.getYearEnd(dateReviewed, timeZone, locale);
				}
				GenericValue PartyPerfCriteriaResult = delegator.findOne("PartyPerfCriteriaResult", UtilMisc.toMap("partyId", partyId,
						"criteriaId", criteriaId,
						"fromDate", fromDateTimestamp,
                        "dateReviewed", dateReviewed), false);
				if(UtilValidate.isNotEmpty(PartyPerfCriteriaResult)){
					PartyPerfCriteriaResult.set("statusId", statusId);
					PartyPerfCriteriaResult.set("comment", comment);
					PartyPerfCriteriaResult.set("result", kpiActual);
					PartyPerfCriteriaResult.set("dateReviewed", dateReviewed);
					PartyPerfCriteriaResult.store();
				}else{
					GenericValue newResultKPI = delegator.makeValue("PartyPerfCriteriaResult");
					newResultKPI.set("partyId", partyId);
					newResultKPI.set("criteriaId", criteriaId);
					newResultKPI.set("fromDate", fromDateTimestamp);
					newResultKPI.set("dateReviewed", dateReviewed);
					newResultKPI.set("result", kpiActual);
					newResultKPI.set("statusId", statusId);
					newResultKPI.create();
				}
			}else{
				Map<String, Object> retMap = ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "HRKPINotHaveThruDate", locale));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}
	//accept All KPI Empl
	public static Map<String, Object> approveAllKPIEmpl(DispatchContext ctx, Map<String, Object> context){
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//String partyId_userLogin = userLogin.getString("partyId");
		List<GenericValue> listPartyKPIResults = null;
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);

		String periodTypeId = (String) context.get("periodTypeId");
		String statusId = (String) context.get("statusId");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String sendNtfToEmpl = (String)context.get("sendNtfToEmpl");
		try {
			String fromDateStr = (String) context.get("fromDate");
			String thruDateStr = (String) context.get("thruDate");
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			Timestamp fromDateD = null;
			Timestamp thruDateD = null;
			String monthFromStr = (String)context.get("monthFrom");
			int monthFrom =0;
			String yearFromStr = (String)context.get("yearFrom");
			int yearFrom =0;
			String monthToStr = (String)context.get("monthTo");
			int monthTo =0;
			String yearToStr = (String)context.get("yearTo");
			int yearTo =0;
			String quarterFromStr = (String)context.get("quarterFrom");
			int quarterFrom =0;
			String yearFromQuarterStr = (String)context.get("yearFromQuarter");
			int yearFromQuarter =0;
			String quarterToStr = (String)context.get("quarterTo");
			int quarterTo =0;
			String yearToQuarterStr = (String)context.get("yearToQuarter");
			int yearToQuarter =0;
			String yearStr = (String)context.get("year");
			int year =0;

			if(UtilValidate.isNotEmpty(fromDateStr)){
				fromDateD = DateUtil.convertStringTypeLongToTimestamp(fromDateStr);
			}
			if(UtilValidate.isNotEmpty(thruDateStr)){
				thruDateD = DateUtil.convertStringTypeLongToTimestamp(thruDateStr);
			}
			if(UtilValidate.isNotEmpty(monthFromStr)){
				monthFrom = (Integer.parseInt(monthFromStr));
			}
			if(UtilValidate.isNotEmpty(yearFromStr)){
				yearFrom = (Integer.parseInt(yearFromStr));
			}
			if(UtilValidate.isNotEmpty(monthToStr)){
				monthTo = Integer.parseInt(monthToStr);
			}
			if(UtilValidate.isNotEmpty(yearToStr)){
				yearTo = (Integer.parseInt(yearToStr));
			}
			if(monthFrom != 0 && yearFrom != 0 && monthTo != 0 && yearTo != 0){
				Timestamp dateFrom = PerfReviewKPIHelper.startDayMonth(yearFrom, monthFrom);
				fromDateD = UtilDateTime.getMonthStart(dateFrom);

				Timestamp dateTo = PerfReviewKPIHelper.startDayMonth(yearTo, monthTo);
				thruDateD = UtilDateTime.getMonthEnd(dateTo, timeZone, locale);
			}
			if(UtilValidate.isNotEmpty(quarterFromStr)){
				quarterFrom = Integer.parseInt(quarterFromStr);
			}
			if(UtilValidate.isNotEmpty(yearFromQuarterStr)){
				yearFromQuarter = Integer.parseInt(yearFromQuarterStr);
			}
			if(UtilValidate.isNotEmpty(quarterToStr)){
				quarterTo = Integer.parseInt(quarterToStr);
			}
			if(UtilValidate.isNotEmpty(yearToQuarterStr)){
				yearToQuarter = Integer.parseInt(yearToQuarterStr);
			}
			if(quarterFrom != 0 && yearFromQuarter != 0 && quarterTo != 0 && yearToQuarter != 0){
				Timestamp dateFrom = PerfReviewKPIHelper.startDayMonth(yearFromQuarter, (quarterFrom-1)*3);
				Date dateFromDate = new Date(dateFrom.getTime());
				fromDateD = DateUtil.getDateStartOfQuarter(dateFromDate);

				Timestamp dateTo = PerfReviewKPIHelper.startDayMonth(yearToQuarter, (quarterTo-1) * 3);
				Date dateToDate = new Date(dateTo.getTime());
				thruDateD = DateUtil.getDateEndOfQuarter(dateToDate);
			}
			if(UtilValidate.isNotEmpty(yearStr)){
				year = Integer.parseInt(yearStr);
				if(year != 0){
					Timestamp dateFrom = PerfReviewKPIHelper.startDayMonth(year, 1);
					fromDateD = UtilDateTime.getYearStart(dateFrom);
					thruDateD = UtilDateTime.getYearEnd(dateFrom, timeZone, locale);
				}
			}


			if(UtilValidate.isNotEmpty(periodTypeId)){
				List<String> emplListOfUserLogin = PartyUtil.getListEmplMgrByParty(delegator, userLogin.getString("userLoginId"), fromDateD, thruDateD);

				if(periodTypeId.equals("DAILY")){
					fromDate = UtilDateTime.getDayEnd(fromDateD);
					thruDate = UtilDateTime.getDayEnd(thruDateD);
				}else if(periodTypeId.equals("WEEKLY")){
					fromDate = UtilDateTime.getWeekEnd(fromDateD);
					thruDate = UtilDateTime.getWeekEnd(thruDateD);
				}else if(periodTypeId.equals("MONTHLY")){
					Timestamp dateFrom = PerfReviewKPIHelper.startDayMonth(yearFrom, monthFrom);
					fromDate = UtilDateTime.getMonthStart(dateFrom, timeZone, locale);
					Timestamp dateTo = PerfReviewKPIHelper.startDayMonth(yearTo, monthTo);
					thruDate = UtilDateTime.getMonthEnd(dateTo, timeZone, locale);
				}else if(periodTypeId.equals("QUARTERLY")){
					Date fromDateDate = new Date(fromDateD.getTime());
					Date thruDateDate = new Date(thruDateD.getTime());
					fromDate = DateUtil.getDateEndOfQuarter(fromDateDate);
					thruDate = DateUtil.getDateEndOfQuarter(thruDateDate);
				}else if(periodTypeId.equals("YEARLY")){
					fromDate = UtilDateTime.getYearStart(fromDateD);
					thruDate = UtilDateTime.getYearEnd(thruDateD, timeZone, locale);
				}
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
				listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
				listAllConditions.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				listAllConditions.add(EntityCondition.makeCondition("partyId" , EntityJoinOperator.IN, emplListOfUserLogin));
				Set<String> listSelectFields = FastSet.newInstance();
				if(UtilValidate.isEmpty(listSelectFields)){
					listSelectFields.add("partyId");
					listSelectFields.add("criteriaId");
					listSelectFields.add("fromDate");
					listSelectFields.add("dateReviewed");
					listSelectFields.add("fullName");
				}
				listPartyKPIResults = delegator.findList("PartyPerfAndCriteriaAndResult", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), listSelectFields, null, opts, false);
			}
			if(UtilValidate.isNotEmpty(listPartyKPIResults)){
				for(GenericValue listPartyKPIResult: listPartyKPIResults ){
					Timestamp dateTimestamp = (Timestamp)listPartyKPIResult.get("dateReviewed");
					Date dateDate = new Date(dateTimestamp.getTime());
					Timestamp dateReviewed = new Timestamp(dateTimestamp.getTime());
					String partyId = (String) listPartyKPIResult.get("partyId");
					String criteriaId = (String) listPartyKPIResult.get("criteriaId");
					Timestamp fromDateTimestamp = (Timestamp) listPartyKPIResult.get("fromDate");
					if("DAILY".equals(periodTypeId)){
						dateReviewed = UtilDateTime.getDayEnd(dateReviewed);
					}
					if("WEEKLY".equals(periodTypeId)){
						dateReviewed = UtilDateTime.getWeekEnd(dateReviewed);
					}
					if("MONTHLY".equals(periodTypeId)){
						dateReviewed = UtilDateTime.getMonthEnd(dateReviewed, timeZone, locale);
					}
					if("QUARTERLY".equals(periodTypeId)){
						dateReviewed = DateUtil.getDateEndOfQuarter(dateDate);
					}
					if("YEARLY".equals(periodTypeId)){
						dateReviewed = UtilDateTime.getYearEnd(dateReviewed, timeZone, locale);
					}
					GenericValue PartyPerfCriteriaResult = delegator.findOne("PartyPerfCriteriaResult", UtilMisc.toMap("partyId", partyId,
							"criteriaId", criteriaId,
							"fromDate", fromDateTimestamp,
							"dateReviewed", dateReviewed), false);
					if(UtilValidate.isNotEmpty(PartyPerfCriteriaResult)){
						PartyPerfCriteriaResult.set("statusId", statusId);
						PartyPerfCriteriaResult.store();
					}else{
						return ServiceUtil.returnError("error");
					}
					if("Y".equals(sendNtfToEmpl)){
						GenericValue criteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
						String criteriaName = criteria.getString("criteriaName");
						String header = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "KPIIsApproved",
								UtilMisc.toMap("criteriaName", criteriaName, "dateReviewed", DateUtil.getDateMonthYearDesc(dateReviewed)), locale);
						String action = "ViewListEmplKPI";
						CommonUtil.sendNotify(dispatcher, locale, partyId, userLogin, header, action, null);
					}
				}
			}else{

			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return successResult;
	}

	public static Map<String, Object> updateKPIEmplPositionType(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String criteriaId = (String) context.get("criteriaId");
		String criteriaName = (String) context.get("criteriaName");
		try {
			GenericValue PerfCriteria = delegator.findOne("PerfCriteria", UtilMisc.toMap("criteriaId", criteriaId), false);
			if(UtilValidate.isNotEmpty(PerfCriteria)){
				PerfCriteria.set("criteriaName", criteriaName);
				PerfCriteria.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		
		return successResult;
	}
	//hien tai khong su dung
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPerfCriteriaPolicy(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityListIterator listIterator = null;
		if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-fromRating");
    	}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String fromDateStr = parameters.get("fromDate") != null? parameters.get("fromDate")[0] : null;
		String thruDateStr = parameters.get("thruDate") != null? parameters.get("thruDate")[0] : null;
		try {
			if(fromDateStr != null){
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
										EntityJoinOperator.OR,
										EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
			}
			if(thruDateStr != null){
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
				listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
			}
			listIterator = delegator.find("PerfCriteriaPolicyAndRateGrade", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPerfCriteriaPolicySimple(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<String> listSortFields = FastList.newInstance();
		EntityFindOptions opts = new EntityFindOptions();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityListIterator listIterator = null;

		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String fromDateStr = parameters.get("fromDate") != null? parameters.get("fromDate")[0] : null;
		String thruDateStr = parameters.get("thruDate") != null? parameters.get("thruDate")[0] : null;
		try {
			if(fromDateStr != null){
				Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
				listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
						EntityJoinOperator.OR,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
			}
			if(thruDateStr != null){
				Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
				listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
			}
			listIterator = delegator.find("PerfCriteriaPolicy", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}


	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPerfCriteriaRateGrade(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityListIterator listIterator = null;
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("-fromRating");
		}
		try {
			listIterator = delegator.find("PerfCriteriaRateGrade", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> createPerfCriteriaRateGrade(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		String perfCriteriaRateGradeName = (String)context.get("perfCriteriaRateGradeName");
		BigDecimal fromRating = (BigDecimal)context.get("fromRating");
		BigDecimal toRating = (BigDecimal)context.get("toRating");
		perfCriteriaRateGradeName = perfCriteriaRateGradeName.trim();
		if(perfCriteriaRateGradeName.length() <= 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeNameIsOnlyContainSpace", locale));
		}
		try {
			List<GenericValue> perfCriteriaRateGradeList = delegator.findByAnd("PerfCriteriaRateGrade", UtilMisc.toMap("perfCriteriaRateGradeName", perfCriteriaRateGradeName), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaRateGradeList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeNameIsExistsed", 
						UtilMisc.toMap("perfCriteriaRateGradeName", perfCriteriaRateGradeName), locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			if(toRating != null){
				conditions.add(EntityCondition.makeCondition("fromRating", EntityJoinOperator.LESS_THAN_EQUAL_TO, toRating));
			}
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("toRating", null),
														EntityJoinOperator.OR,
														EntityCondition.makeCondition("toRating", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromRating)));
			perfCriteriaRateGradeList = delegator.findList("PerfCriteriaRateGrade", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromRating"), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaRateGradeList)){
				GenericValue perfCriteriaRateGradeCheck = perfCriteriaRateGradeList.get(0);
				String perfCriteriaRateGradeNameCheck = perfCriteriaRateGradeCheck.getString("perfCriteriaRateGradeName");
				BigDecimal fromRatingCheck = perfCriteriaRateGradeCheck.getBigDecimal("fromRating");
				BigDecimal toRatingCheck = perfCriteriaRateGradeCheck.getBigDecimal("toRating");
				String errMsg = "";
				Map<String, Object> messageMap = FastMap.newInstance();
				messageMap.put("perfCriteriaRateGradeNameCheck", perfCriteriaRateGradeNameCheck);
				messageMap.put("perfCriteriaRateGradeName", perfCriteriaRateGradeName);
				messageMap.put("fromRatingCheck", fromRatingCheck);
				messageMap.put("fromRating", fromRating);
				if(toRating != null && toRatingCheck != null){
					messageMap.put("toRating", toRating);
					messageMap.put("toRatingCheck", toRatingCheck);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationFull", 
							messageMap, locale);
				}else if(toRating != null){
					messageMap.put("toRating", toRating);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationToRating", 
							messageMap, locale);
				}else if(toRatingCheck != null){
					messageMap.put("toRatingCheck", toRatingCheck);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationToRatingCheck", 
							messageMap, locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplication", 
							messageMap, locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue perfCriteriaRateGradeNew = delegator.makeValue("PerfCriteriaRateGrade");
			perfCriteriaRateGradeNew.setNonPKFields(context);
			String perfCriteriaRateGradeId = delegator.getNextSeqId("PerfCriteriaRateGrade");
			perfCriteriaRateGradeNew.put("perfCriteriaRateGradeId", perfCriteriaRateGradeId);
			perfCriteriaRateGradeNew.create();
			retMap.put("perfCriteriaRateGradeId", perfCriteriaRateGradeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updatePerfCriteriaRateGrade(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String perfCriteriaRateGradeName = (String)context.get("perfCriteriaRateGradeName");
		BigDecimal fromRating = (BigDecimal)context.get("fromRating");
		BigDecimal toRating = (BigDecimal)context.get("toRating");
		if(perfCriteriaRateGradeName != null){
			perfCriteriaRateGradeName = perfCriteriaRateGradeName.trim();
			if(perfCriteriaRateGradeName.length() <= 0){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeNameIsOnlyContainSpace", locale));
			}
		}
		String perfCriteriaRateGradeId = (String)context.get("perfCriteriaRateGradeId");
		try {
			GenericValue perfCriteriaRateGrade = delegator.findOne("PerfCriteriaRateGrade", UtilMisc.toMap("perfCriteriaRateGradeId", perfCriteriaRateGradeId), false);
			if(perfCriteriaRateGrade == null){
				return ServiceUtil.returnError("cannot find record to update");
			}
			EntityCondition checkNameConds = EntityCondition.makeCondition(EntityCondition.makeCondition("perfCriteriaRateGradeId", EntityJoinOperator.NOT_EQUAL, perfCriteriaRateGradeId),
											EntityCondition.makeCondition("perfCriteriaRateGradeName", perfCriteriaRateGradeName));
			List<GenericValue> perfCriteriaRateGradeList = delegator.findList("PerfCriteriaRateGrade", checkNameConds, null, null, null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaRateGradeList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeNameIsExistsed", 
						UtilMisc.toMap("perfCriteriaRateGradeName", perfCriteriaRateGradeName), locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("perfCriteriaRateGradeId", EntityJoinOperator.NOT_EQUAL, perfCriteriaRateGradeId));
			if(toRating != null){
				conditions.add(EntityCondition.makeCondition("fromRating", EntityJoinOperator.LESS_THAN_EQUAL_TO, toRating));
			}
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("toRating", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("toRating", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromRating)));
			perfCriteriaRateGradeList = delegator.findList("PerfCriteriaRateGrade", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromRating"), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaRateGradeList)){
				GenericValue perfCriteriaRateGradeCheck = perfCriteriaRateGradeList.get(0);
				String perfCriteriaRateGradeNameCheck = perfCriteriaRateGradeCheck.getString("perfCriteriaRateGradeName");
				BigDecimal fromRatingCheck = perfCriteriaRateGradeCheck.getBigDecimal("fromRating");
				BigDecimal toRatingCheck = perfCriteriaRateGradeCheck.getBigDecimal("toRating");
				String errMsg = "";
				Map<String, Object> messageMap = FastMap.newInstance();
				messageMap.put("perfCriteriaRateGradeNameCheck", perfCriteriaRateGradeNameCheck);
				messageMap.put("perfCriteriaRateGradeName", perfCriteriaRateGradeName);
				messageMap.put("fromRatingCheck", fromRatingCheck);
				messageMap.put("fromRating", fromRating);
				if(toRating != null && toRatingCheck != null){
					messageMap.put("toRating", toRating);
					messageMap.put("toRatingCheck", toRatingCheck);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationFullUpdate", 
							messageMap, locale);
				}else if(toRating != null){
					messageMap.put("toRating", toRating);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationToRatingUpdate", 
							messageMap, locale);
				}else if(toRatingCheck != null){
					messageMap.put("toRatingCheck", toRatingCheck);
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationToRatingCheckUpdate", 
							messageMap, locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaRateGradeRatingRangeIsDuplicationUpdate", 
							messageMap, locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			perfCriteriaRateGrade.setNonPKFields(context);
			perfCriteriaRateGrade.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deletePerfCriteriaRateGrade(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String perfCriteriaRateGradeId = (String)context.get("perfCriteriaRateGradeId");
		try {
			GenericValue perfCriteriaRateGrade = delegator.findOne("PerfCriteriaRateGrade", UtilMisc.toMap("perfCriteriaRateGradeId", perfCriteriaRateGradeId), false);
			if(perfCriteriaRateGrade == null){
				return ServiceUtil.returnError("cannot find records to delete");
			}
			List<GenericValue> perfCriteriaPolicy = delegator.findByAnd("PerfCriteriaPolicy", UtilMisc.toMap("perfCriteriaRateGradeId", perfCriteriaRateGradeId), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaPolicy)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotDeleteBecausePerfCriteriaRateGradeIsUsed", 
						UtilMisc.toMap("perfCriteriaRateGradeName", perfCriteriaRateGrade.get("perfCriteriaRateGradeName")), locale));
			}
			perfCriteriaRateGrade.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", locale));
	}
	public static Map<String, Object> createPerfCriteriaPolicy(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String perfCriteriaRateGradeId = (String)context.get("perfCriteriaRateGradeId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale)); 
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			if(thruDate != null){
				conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			}
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityJoinOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate)));
			conditions.add(EntityCondition.makeCondition("perfCriteriaRateGradeId", perfCriteriaRateGradeId));
			List<GenericValue> perfCriteriaPolicyList = delegator.findList("PerfCriteriaPolicyAndRateGrade", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaPolicyList)){
				GenericValue perfCriteriaPolicyCheck = perfCriteriaPolicyList.get(0);
				Timestamp fromDateCheck = perfCriteriaPolicyCheck.getTimestamp("fromDate");
				Timestamp thruDateCheck = perfCriteriaPolicyCheck.getTimestamp("thruDate");
				String perfCriteriaRateGradeName = perfCriteriaPolicyCheck.getString("perfCriteriaRateGradeName");
				Map<String, Object> messageMap = FastMap.newInstance();
				messageMap.put("perfCriteriaRateGradeName", perfCriteriaRateGradeName);
				messageMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDate));
				messageMap.put("fromDateCheck", DateUtil.getDateMonthYearDesc(fromDateCheck));
				String errMsg = "";
				if(thruDate != null && thruDateCheck != null){
					messageMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDate));
					messageMap.put("thruDateCheck", DateUtil.getDateMonthYearDesc(thruDateCheck));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateFull", messageMap, locale);
				}else if(thruDate != null){
					messageMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDate));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateThruDate", messageMap, locale);
				}else if(thruDateCheck != null){
					messageMap.put("thruDateCheck", DateUtil.getDateMonthYearDesc(thruDateCheck));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateThruDateCheck", messageMap, locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicate", messageMap, locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			GenericValue perfCriteriaPolicy = delegator.makeValue("PerfCriteriaPolicy");
			perfCriteriaPolicy.setNonPKFields(context);
			String perfCriteriaPolicyId = delegator.getNextSeqId("PerfCriteriaPolicy");
			perfCriteriaPolicy.put("perfCriteriaPolicyId", perfCriteriaPolicyId);
			delegator.create(perfCriteriaPolicy);
			retMap.put("perfCriteriaPolicyId", perfCriteriaPolicyId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}

	//hien tai khong su dung
	public static Map<String, Object> updatePerfCriteriaPolicy(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String perfCriteriaRateGradeId = (String)context.get("perfCriteriaRateGradeId");
		String perfCriteriaPolicyId = (String)context.get("perfCriteriaPolicyId");
		try {
			GenericValue perfCriteriaPolicy = delegator.findOne("PerfCriteriaPolicy", UtilMisc.toMap("perfCriteriaPolicyId", perfCriteriaPolicyId), false);
			if(perfCriteriaPolicy == null){
				return ServiceUtil.returnError("cannot find record to update");
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("perfCriteriaPolicyId", EntityJoinOperator.NOT_EQUAL, perfCriteriaPolicyId));
			if(thruDate != null){
				conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			}
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityJoinOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate)));
			conditions.add(EntityCondition.makeCondition("perfCriteriaRateGradeId", perfCriteriaRateGradeId));
			List<GenericValue> perfCriteriaPolicyList = delegator.findList("PerfCriteriaPolicyAndRateGrade", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(perfCriteriaPolicyList)){
				GenericValue perfCriteriaPolicyCheck = perfCriteriaPolicyList.get(0);
				Timestamp fromDateCheck = perfCriteriaPolicyCheck.getTimestamp("fromDate");
				Timestamp thruDateCheck = perfCriteriaPolicyCheck.getTimestamp("thruDate");
				String perfCriteriaRateGradeName = perfCriteriaPolicyCheck.getString("perfCriteriaRateGradeName");
				Map<String, Object> messageMap = FastMap.newInstance();
				messageMap.put("perfCriteriaRateGradeName", perfCriteriaRateGradeName);
				messageMap.put("fromDate", DateUtil.getDateMonthYearDesc(fromDate));
				messageMap.put("fromDateCheck", DateUtil.getDateMonthYearDesc(fromDateCheck));
				String errMsg = "";
				if(thruDate != null && thruDateCheck != null){
					messageMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDate));
					messageMap.put("thruDateCheck", DateUtil.getDateMonthYearDesc(thruDateCheck));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateFullUpdate", messageMap, locale);
				}else if(thruDate != null){
					messageMap.put("thruDate", DateUtil.getDateMonthYearDesc(thruDate));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateThruDateUpdate", messageMap, locale);
				}else if(thruDateCheck != null){
					messageMap.put("thruDateCheck", DateUtil.getDateMonthYearDesc(thruDateCheck));
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateThruDateCheckUpdate", messageMap, locale);
				}else{
					errMsg = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "PerfCriteriaPolicyIsDuplicateUpdate", messageMap, locale);
				}
				return ServiceUtil.returnError(errMsg);
			}
			perfCriteriaPolicy.setNonPKFields(context);
			perfCriteriaPolicy.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createPerfCriteriaAssessment(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		if(fromDate.compareTo(thruDate) >= 0){
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "TimeEnterNotValid", locale));
		}
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		GenericValue perfCriteriaAssessment = delegator.makeValue("PerfCriteriaAssessment");
		String perfCriteriaAssessmentId = delegator.getNextSeqId("PerfCriteriaAssessment");
		retMap.put("perfCriteriaAssessmentId", perfCriteriaAssessmentId);
		perfCriteriaAssessment.setNonPKFields(context);
		perfCriteriaAssessment.put("perfCriteriaAssessmentId", perfCriteriaAssessmentId);
		try {
			delegator.create(perfCriteriaAssessment);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	public static Map<String, Object> createPerfCriteriaAssessmentParty(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String perfCriteriaAssessmentId = (String)context.get("perfCriteriaAssessmentId");
		BigDecimal salaryRate = new BigDecimal(100);
		BigDecimal allowanceRate = new BigDecimal(100);

		try {
			GenericValue perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", 
					UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotFindPerfCriteriaAssessment", locale));
			}
			String enumAssessmentTypeId = perfCriteriaAssessment.getString("enumAssessmentTypeId");
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			String currOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<String> deptList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
			if(!"KPI_ASSESS_DISTR".equals(enumAssessmentTypeId)){
				boolean isBelongCurrOrg = false;
				for(String deptId: deptList){
					if(PartyUtil.checkAncestorOfParty(delegator, currOrgId, deptId, userLogin)){
						isBelongCurrOrg = true;
						break;
					}
				}
				if(!isBelongCurrOrg){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRUiLabels", "EmplIsNotBelongOrg", 
							UtilMisc.toMap("emplName", PartyUtil.getPersonName(delegator, partyId), "orgName", PartyHelper.getPartyName(delegator, currOrgId, false)), locale));
				}
			}
			String periodTypeId = perfCriteriaAssessment.getString("periodTypeId");
			GenericValue periodType = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", periodTypeId), false);
			Long periodLength = periodType.getLong("periodLength");
			String uomId = periodType.getString("uomId");
			EntityCondition commonConds = EntityCondition.makeCondition(EntityCondition.makeCondition("statusIdAppr", "KAS_ACCEPTED"), EntityJoinOperator.AND,
										EntityCondition.makeCondition("partyId", partyId));
			if("TF_yr".equals(uomId)){
				periodLength *= 12;
			}
			GenericValue perfCriteriaAssessmentParty = delegator.makeValue("PerfCriteriaAssessmentParty");
			perfCriteriaAssessmentParty.setAllFields(context, false, null, null);

			List<EntityCondition> dateConds = FastList.newInstance();
			dateConds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			dateConds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			List<GenericValue> partyPerfCriteriaResultList = delegator.findList("PartyPerfCriteriaAndResult", EntityCondition.makeCondition(EntityCondition.makeCondition(dateConds),
																											EntityJoinOperator.AND, commonConds), null, null, null, false);
			if(UtilValidate.isNotEmpty(partyPerfCriteriaResultList)){
				Timestamp tempFromDate = fromDate;
				BigDecimal totalPoint = BigDecimal.ZERO;
				BigDecimal totalBonusAmount = BigDecimal.ZERO;
				BigDecimal totalPunishmentAmount = BigDecimal.ZERO;
				//int totalMonthAssessment = 0;
				for(int i = 1; i <= periodLength; i++){
					Timestamp tempThruDate = UtilDateTime.getMonthEnd(tempFromDate, timeZone, locale);
					if(tempThruDate.after(thruDate)){
						tempThruDate = thruDate;
					}
					if(tempFromDate.after(tempThruDate)){
						break;
					}
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, tempFromDate));
					conds.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, tempThruDate));
					List<GenericValue> tempPartyPerfCriteriaResultList = EntityUtil.filterByCondition(partyPerfCriteriaResultList, EntityCondition.makeCondition(conds));
					if(UtilValidate.isNotEmpty(tempPartyPerfCriteriaResultList)){
						List<String> criteriaIdList = EntityUtil.getFieldListFromEntityList(tempPartyPerfCriteriaResultList, "criteriaId", true);
						for(String criteriaId: criteriaIdList){
							List<GenericValue> criteriaList = EntityUtil.filterByCondition(tempPartyPerfCriteriaResultList, EntityCondition.makeCondition("criteriaId", criteriaId));
							BigDecimal tempPoint = PerfReviewKPIHelper.calcKPIPoint(delegator, criteriaList, criteriaId);
							GenericValue criteria = criteriaList.get(0);
							Map<String, Object> bonusPunishmentAmountResult = PerfReviewKPIHelper.getBonusPunishmentAmountKPI(delegator, tempPoint, criteriaId, thruDate, criteria.getBigDecimal("result"), criteria.getBigDecimal("target"));
							if(bonusPunishmentAmountResult != null){
								BigDecimal amount = (BigDecimal)bonusPunishmentAmountResult.get("amount");
								String kpiPolicyEnumId = (String)bonusPunishmentAmountResult.get("kpiPolicyEnumId");
								BigDecimal tempPointAffterCalc = (BigDecimal) bonusPunishmentAmountResult.get("tempPoint");
								if("KPI_REWARD".equals(kpiPolicyEnumId)){
									totalBonusAmount = totalBonusAmount.add(amount);
								}else if("KPI_PUNISHMENT".equals(kpiPolicyEnumId)){
									totalPunishmentAmount = totalPunishmentAmount.add(amount);
								}
							}
							BigDecimal weight = criteria.getBigDecimal("weight");
							BigDecimal tempPointCalc = weight.multiply(tempPoint);
							totalPoint = totalPoint.add(tempPointCalc);
						}
						//totalMonthAssessment++;
					}
					tempFromDate = UtilDateTime.getMonthStart(tempFromDate, 0, i);
				}
				/*if(totalMonthAssessment != 0){
					totalPoint = totalPoint.divide(new BigDecimal(totalMonthAssessment), RoundingMode.HALF_UP);
				}*/
				//String perfCriteriaRateGradeId = PerfReviewKPIHelper.getPerfCriteriaRateGradeByPoint(delegator, totalPoint);
				/*if(perfCriteriaRateGradeId != null){*/
					/*perfCriteriaAssessmentParty.put("perfCriteriaRateGradeId", perfCriteriaRateGradeId);
					List<EntityCondition> perfCriteriaPolicyConds = FastList.newInstance();
					perfCriteriaPolicyConds.add(EntityCondition.makeCondition("perfCriteriaRateGradeId", perfCriteriaRateGradeId));
					perfCriteriaPolicyConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
					perfCriteriaPolicyConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																			EntityJoinOperator.OR,
																			EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
					List<GenericValue> perfCriteriaPolicyList = delegator.findList("PerfCriteriaPolicy", EntityCondition.makeCondition(perfCriteriaPolicyConds), 
							null, UtilMisc.toList("-fromDate"), null, false);*/
					/*if(UtilValidate.isNotEmpty(perfCriteriaPolicyList)){*/
						/*GenericValue perfCriteriaPolicy = EntityUtil.getFirst(perfCriteriaPolicyList);*/
						/*perfCriteriaAssessmentParty.put("salaryRate", perfCriteriaPolicy.get("salaryRate"));
						perfCriteriaAssessmentParty.put("allowanceRate", perfCriteriaPolicy.get("allowanceRate"));*/
						perfCriteriaAssessmentParty.put("bonusAmount", totalBonusAmount);
						perfCriteriaAssessmentParty.put("punishmentAmount", totalPunishmentAmount);
						perfCriteriaAssessmentParty.put("point", totalPoint.setScale(1, RoundingMode.HALF_UP));
						perfCriteriaAssessmentParty.put("statusId", "KAS_WAITING");
						perfCriteriaAssessmentParty.put("salaryRate", salaryRate);
						perfCriteriaAssessmentParty.put("allowanceRate", salaryRate);
						/* find customTimePeriod */
						Date customTimePeriodThruDate = new Date(thruDate.getTime());
						List<EntityCondition> customTimePeriodConds = FastList.newInstance();
						customTimePeriodConds.add(EntityCondition.makeCondition("periodTypeId", "MONTHLY"));
						customTimePeriodConds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, customTimePeriodThruDate));
						customTimePeriodConds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, customTimePeriodThruDate));
						List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodConds), 
								null, UtilMisc.toList("-fromDate"), null, false);
						if(UtilValidate.isNotEmpty(customTimePeriodList)){
							String customTimePeriodId = EntityUtil.getFirst(customTimePeriodList).getString("customTimePeriodId");
							perfCriteriaAssessmentParty.put("customTimePeriodId", customTimePeriodId);
						}
					/*}*/
				/*}*/
			}
			delegator.createOrStore(perfCriteriaAssessmentParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplKPIAssessetmentGeneral(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String,Object>> listIterator = new ArrayList<Map<String,Object>>();
		String perfCriteriaAssessmentId = parameters.get("perfCriteriaAssessmentId") != null? parameters.get("perfCriteriaAssessmentId")[0] : null; 
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
		GenericValue perfCriteriaAssessment;
		try {
			perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", 
					UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotFindPerfCriteriaAssessment", locale));
			}
			listSortFields.add("criteriaName");
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
			List<GenericValue> partyPerfCriteriaList = delegator.findList("PartyPerfCriteriaAndCriteria", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
			totalRows = partyPerfCriteriaList.size();
			if(end > totalRows){
				end = totalRows;
			}
			partyPerfCriteriaList = partyPerfCriteriaList.subList(start, end);
			List<EntityCondition> perfCriteriaCondList = FastList.newInstance();
			perfCriteriaCondList.add(EntityCondition.makeCondition("partyId", partyId));
			perfCriteriaCondList.add(EntityCondition.makeCondition("statusIdAppr", "KAS_ACCEPTED"));
			EntityCondition dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate),
																		EntityJoinOperator.AND, 
																		EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			EntityCondition perfCriteriaCommonCond = EntityCondition.makeCondition(perfCriteriaCondList);
			for(GenericValue partyPerfCriteria: partyPerfCriteriaList){
				Map<String, Object> tempMap = partyPerfCriteria.getAllFields();
				String criteriaId = partyPerfCriteria.getString("criteriaId");
				EntityCondition tempCriteriaIdCond = EntityCondition.makeCondition("criteriaId", criteriaId);
				Timestamp criteriaFromDate = partyPerfCriteria.getTimestamp("fromDate");
				Timestamp criteriaThruDate = partyPerfCriteria.getTimestamp("thruDate");
				if(criteriaFromDate.before(fromDate)){
					criteriaFromDate = fromDate;
				}
				if(criteriaThruDate == null || criteriaThruDate.after(thruDate)){
					criteriaThruDate = thruDate;
				}
				EntityCondition tempDateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, criteriaThruDate),
						EntityJoinOperator.AND, 
						EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, criteriaFromDate));
				List<GenericValue> partyPerfCriteriaResultList = delegator.findList("PartyPerfCriteriaAndResult", 
						EntityCondition.makeCondition(UtilMisc.toList(perfCriteriaCommonCond, tempCriteriaIdCond, dateConds)), null, null, null, false);
				List<GenericValue> partyPerfCriteriaResultPartiallyList = EntityUtil.filterByCondition(partyPerfCriteriaResultList, tempDateConds);
				if(UtilValidate.isNotEmpty(partyPerfCriteriaResultPartiallyList)){
					//TODO recalculate sub total point
					/*BigDecimal subTotalPoint = PerfReviewKPIHelper.calcKPIPoint(delegator, partyPerfCriteriaResultPartiallyList);
					float ratio = (float)partyPerfCriteriaResultList.size() / partyPerfCriteriaResultPartiallyList.size();
					subTotalPoint = subTotalPoint.divide(new BigDecimal(ratio), RoundingMode.HALF_UP);
					tempMap.put("subTotalPoint", subTotalPoint);*/
				}
				listIterator.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplKPIAssessetmentDetail(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		String perfCriteriaAssessmentId = parameters.get("perfCriteriaAssessmentId") != null? parameters.get("perfCriteriaAssessmentId")[0] : null; 
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
		try {
			GenericValue perfCriteriaAssessment = delegator.findOne("PerfCriteriaAssessment", 
					UtilMisc.toMap("perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessment == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotFindPerfCriteriaAssessment", locale));
			}
			Timestamp fromDate = perfCriteriaAssessment.getTimestamp("fromDate");
			Timestamp thruDate = perfCriteriaAssessment.getTimestamp("thruDate");
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "KAS_ACCEPTED"));
			listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
			listAllConditions.add(EntityCondition.makeCondition("dateReviewed", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("criteriaName");
				listSortFields.add("-dateReviewed");
			}
			listIterator = delegator.find("PartyPerfAndCriteriaAndResult", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListSalesPolicyDistributor(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("salesBonusPolicyTypeId", "DISTRIBUTOR_POLICY"));
			listIterator = delegator.find("SalesBonusPolicy", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyPerfCriteriaForDistributor(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
		String fromDateStr = parameters.get("fromDate") != null? parameters.get("fromDate")[0] : null;
		if(partyId == null || fromDateStr == null){
			return successResult;
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listAllConditions.add(EntityCondition.makeCondition("fromDate", fromDate));
		listAllConditions.add(EntityCondition.makeCondition("criteriaId", "KPI_SKU_SALE"));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("productName");
		}
		try {
			listIterator = delegator.find("PartyPerfCriteriaItemProductAndProduct", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> updatePerfCriteriaAssessmentParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String perfCriteriaAssessmentId = (String)context.get("perfCriteriaAssessmentId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue perfCriteriaAssessmentParty = delegator.findOne("PerfCriteriaAssessmentParty", 
					UtilMisc.toMap("partyId", partyId, "perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessmentParty == null){
				return ServiceUtil.returnError("cannot find record PerfCriteriaAssessmentParty to update");
			}
			perfCriteriaAssessmentParty.setNonPKFields(context);
			perfCriteriaAssessmentParty.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "updateSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> deletePerfCriteriaAssessmentParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String perfCriteriaAssessmentId = (String)context.get("perfCriteriaAssessmentId");
		String partyId = (String)context.get("partyId");
		try {
			GenericValue perfCriteriaAssessmentParty = delegator.findOne("PerfCriteriaAssessmentParty", 
					UtilMisc.toMap("partyId", partyId, "perfCriteriaAssessmentId", perfCriteriaAssessmentId), false);
			if(perfCriteriaAssessmentParty == null){
				return ServiceUtil.returnError("cannot find record PerfCriteriaAssessmentParty to delete");
			}
			String statusId = perfCriteriaAssessmentParty.getString("statusId");
			if("KAS_ACCEPTED".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "CannotDeleteKPIIsApproval", (Locale)context.get("locale")));
			}
			perfCriteriaAssessmentParty.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "deleteSuccessfully", (Locale)context.get("locale")));
	}
	public static Map<String, Object> createKPIAssessPeriod(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRUiLabels", "createSuccessfully", locale));
		String uomId = (String)context.get("uomId");
		Long periodLength = (Long)context.get("periodLength");
		try {
			Long periodLengthCompare = periodLength; 
			if("TF_yr".equals(uomId)){
				periodLengthCompare *= 12;
			}
			List<GenericValue> kpiAssessmentPeriodList = delegator.findByAnd("PeriodType", UtilMisc.toMap("groupPeriodTypeId", "KPI_ASSESSMENT_PERIOD"), null, false);
			for(GenericValue kpiAssessmentPeriod: kpiAssessmentPeriodList){
				Long tempPeriodLength = kpiAssessmentPeriod.getLong("periodLength");
				String tempUomId = kpiAssessmentPeriod.getString("uomId");
				if("TF_yr".equals(tempUomId)){
					tempPeriodLength *= 12;
				}
				if(tempPeriodLength == periodLengthCompare){
					GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "KPIAssessPeriodIsExistsed", 
							UtilMisc.toMap("periodLength", periodLength, "uom", uom.get("abbreviation", locale)), locale));
				}
			}
			GenericValue periodType = delegator.makeValue("PeriodType");
			String periodTypeId = delegator.getNextSeqId("PeriodType");
			periodType.setNonPKFields(context);
			periodType.set("periodTypeId", periodTypeId);
			periodType.set("groupPeriodTypeId", "KPI_ASSESSMENT_PERIOD");
			delegator.create(periodType);
			successResult.put("periodTypeId", periodTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	};
	
	public static Map<String, Object> getPerfCriteriaByType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String perfCriteriaTypeId = (String)context.get("perfCriteriaTypeId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("perfCriteriaTypeId", perfCriteriaTypeId));
		conditions.add(EntityCondition.makeCondition("statusId", "KPI_ACTIVE"));
		try {
			List<GenericValue> perfCriteria = delegator.findList("PerfCriteria", EntityCondition.makeCondition(conditions), 
					null, UtilMisc.toList("criteriaName"), null, false);
			successResult.put("listPerfCriteria", perfCriteria);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> ntfApprovalKPI(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = null;
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		String approverPartyId = (String)context.get("approverPartyId");
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		try {
			List<String> listMgr = PartyUtil.getManagerOfEmpl(delegator, partyId, UtilDateTime.nowTimestamp(), userLogin.getString("userLoginId"));
			if(!listMgr.contains(approverPartyId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHREmploymentUiLabels", "PartyIdIsNotManageUserLogin",
						UtilMisc.toMap("partyName", PartyUtil.getPersonName(delegator, approverPartyId)), locale));
			}
			List<GenericValue> partyPerfCriteriaResultList = delegator.findByAnd("PartyPerfAndCriteriaAndResult", 
					UtilMisc.toMap("partyId", partyId, "statusId", "KAS_WAITING"), UtilMisc.toList("-dateReviewed"), false);
			if(UtilValidate.isEmpty(partyPerfCriteriaResultList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "HaveNoKpiIsWaitingApproved", locale));
			}
			int totalKPIAppr = partyPerfCriteriaResultList.size();
			String header = null;
			GenericValue partyPerfCriteriaResult = partyPerfCriteriaResultList.get(0);
			String criteriaName = partyPerfCriteriaResult.getString("criteriaName");
			if(totalKPIAppr > 1){
				header = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "ApprovalListKPIForEmpl", 
						UtilMisc.toMap("criteriaName", criteriaName, "partyName", "totalOtherKPI", String.valueOf(totalKPIAppr - 1), PartyUtil.getPersonName(delegator, partyId)), locale);
			}else{
				header = UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "ApprovalKPIForEmpl", 
						UtilMisc.toMap("criteriaName", criteriaName, "partyName", PartyUtil.getPersonName(delegator, partyId)), locale);
			}
			String action = "viewApproveKPIEmpl";
			CommonUtil.sendNotify(dispatcher, locale, approverPartyId, userLogin, header, action, null);
			successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "KPIProposalIsSent", 
					UtilMisc.toMap("totalKPIAppr", String.valueOf(totalKPIAppr)), locale));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getKpiPolicyItem(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		opts.setDistinct(true);
		EntityListIterator listIterator = null;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		try {
			String perfCriteriaPolicyId = "";
			if(parameters.containsKey("perfCriteriaPolicyId") && parameters.get("perfCriteriaPolicyId")[0] != null){
				perfCriteriaPolicyId = (String) parameters.get("perfCriteriaPolicyId")[0];
			}
			listAllConditions.add(EntityCondition.makeCondition("perfCriteriaPolicyId",perfCriteriaPolicyId));
			listIterator = delegator.find("PerfCriteriaPolicyItem", 
					EntityCondition.makeCondition(listAllConditions), null, null, null, opts);
			if(UtilValidate.isNotEmpty(listIterator)){
				totalRows = listIterator.getCompleteList().size();
				if(end > totalRows){
					end = totalRows;
				}
				List<GenericValue> listTmp = listIterator.getPartialList(start, end);
				for (GenericValue g1 : listTmp) {
					Map<String, Object> map = FastMap.newInstance();
					String kpiEnumId = g1.getString("kpiPolicyEnumId");
					GenericValue tmp = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", kpiEnumId), false);
					String description = tmp.getString("description");
					map.putAll(g1);
					map.put("description", description);
					listReturn.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		try {
			listIterator.close();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListKpiPolicy(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		opts.setDistinct(true);
		EntityListIterator listIterator = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String criteriaId = "";
			if(parameters.containsKey("criteriaId") && parameters.get("criteriaId")[0] != null){
				criteriaId = parameters.get("criteriaId")[0];
			}
			if(UtilValidate.isNotEmpty(criteriaId)){
				listAllConditions.add(EntityCondition.makeCondition("criteriaId", criteriaId));
			}
			listIterator = delegator.find("PerfCriteriaPolicy", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	public static Map<String, Object> updateKpiPolicyitem(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		BigDecimal fromRating = (BigDecimal) context.get("fromRating");
		BigDecimal toRating = (BigDecimal) context.get("toRating");
		BigDecimal amount = (BigDecimal) context.get("amount");
		String perfCriteriaPolicyId = (String) context.get("perfCriteriaPolicyId");
		long criteriaPolSeqId = (long) context.get("criteriaPolSeqId");
		String kpiPolicyEnumId = (String) context.get("kpiPolicyEnumId");
		try {
			GenericValue g = delegator.findOne("PerfCriteriaPolicyItem", UtilMisc.toMap("perfCriteriaPolicyId", perfCriteriaPolicyId, "criteriaPolSeqId", criteriaPolSeqId), false);
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityCondition.makeCondition("perfCriteriaPolicyId",perfCriteriaPolicyId));
			listCond.add(EntityCondition.makeCondition("criteriaPolSeqId", EntityJoinOperator.NOT_EQUAL, criteriaPolSeqId));
			List<GenericValue> listG1 = delegator.findList("PerfCriteriaPolicyItem", 
					EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, null, null, false);
			for (GenericValue g1 : listG1) {
				BigDecimal x = g1.getBigDecimal("fromRating");
				BigDecimal y = g1.getBigDecimal("toRating");
				boolean b = PerfReviewKPIHelper.checkSetupKpiPolicyItem(x, fromRating, y, toRating);
				if(!b){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
				}
			}
			if(UtilValidate.isNotEmpty(g)){
				g.set("fromRating", fromRating);
				g.set("toRating", toRating);
				g.set("amount", amount);
				if(UtilValidate.isNotEmpty(kpiPolicyEnumId)){
					g.set("kpiPolicyEnumId", kpiPolicyEnumId);
				}
				g.store();
			}else{
				return ServiceUtil.returnError("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return successResult;
	}
	
	public static Map<String, Object> updateKpiPolicy(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String perfCriteriaPolicyId = (String) context.get("perfCriteriaPolicyId");
		try {
			GenericValue g = delegator.findOne("PerfCriteriaPolicy", UtilMisc.toMap("perfCriteriaPolicyId", perfCriteriaPolicyId), false);
			String criteriaId = g.getString("criteriaId");
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityCondition.makeCondition("criteriaId", criteriaId));
			listCond.add(EntityCondition.makeCondition("perfCriteriaPolicyId", EntityJoinOperator.NOT_EQUAL, perfCriteriaPolicyId));
			List<GenericValue> listG = delegator.findList("PerfCriteriaPolicy",
					EntityCondition.makeCondition(listCond), null, null, null, false);
			if(UtilValidate.isNotEmpty(listG)){
				for (GenericValue gtmp : listG) {
					boolean b = PerfReviewKPIHelper.checkSetupKpiPolicy(fromDate, gtmp.getTimestamp("fromDate"), thruDate, gtmp.getTimestamp("thruDate"));
					if(!b){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseHRKeyPerfIndicatorUiLabels", "WrongSetup", locale));
					}
				}
			}
			if(UtilValidate.isNotEmpty(g)){
				g.set("fromDate", fromDate);
				g.set("thruDate", thruDate);
				g.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successResult;
	}
	
}
