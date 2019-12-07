package com.olbius.procurement;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.CommonUtil;
import com.olbius.util.SecurityUtil;


public class PlanningServices {
	public static final String module = PlanningServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";

    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListPlan(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			opts.setDistinct(true);
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("statusId ASC");
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "INTERNAL_ORGANIZATIO"));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PO_PLAN_SUBMITED"));
			EntityListIterator listIter = delegator.find("PurchasingOrderPlanDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "cannotGetListPOPlan", locale));
		}
		return successResult;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPlanDetail(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
		try {
			String partyId = (String) parameters.get("partyId")[0];
			String year = (String) parameters.get("year")[0];
			opts.setDistinct(true);
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("productId ASC");
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			listAllConditions.add(EntityCondition.makeCondition("year", year));
			Set<String> fields = UtilMisc.toSet("partyId","year","productId","productName","currencyUomId","unitCost","quantity", "quantityUomId");
			fields.add("reason");
			fields.add("poPlanSeqId");
			EntityListIterator listIter = delegator.find("PurchasingOrderPlanItemDetail", EntityCondition.makeCondition(listAllConditions), null, fields, listSortFields, opts);
			successResult.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "cannotGetListPOPlanItem", locale));
		}
		return successResult;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListPlanOrg(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			opts.setDistinct(true);
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "ORGANIZATION_PARTY"));
			EntityListIterator listIter = delegator.find("PurchasingOrderPlanDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIter);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "cannotGetListPOPlan", locale));
		}
		return successResult;
    }
    public static Map<String, Object> createPOPlan(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> products = (List<Map<String, Object>>) context.get("products");
        ModelService createAccountService = ctx.getModelService("createPOPlanHeader");
        Map<String, Object> inputMap = createAccountService.makeValid(context, ModelService.IN_PARAM);
        inputMap.put("createdByUserLogin", userLogin.getString("userLoginId"));
        inputMap.put("statusId", "PO_PLAN_CREATED");
        try{
        	dispatcher.runSync("createPOPlanHeader", inputMap);
        	String year = (String) context.get("year");
        	String partyId = (String) context.get("partyId");
        	String tmp = "";
            for(Map<String, Object> p : products){
            	Map<String, Object> ri = FastMap.newInstance();
            	ri.put("year", year);
            	ri.put("partyId", partyId);
            	ri.put("productId", p.get("productId"));
            	ri.put("quantity", new BigDecimal((String)p.get("quantity")));
            	ri.put("quantityUomId", p.get("quantityUomId"));
            	tmp = (String)  p.get("quantity");
        		if(!UtilValidate.isEmpty(tmp)){
        			ri.put("quantity", new BigDecimal(tmp));
        		}else{
        			ri.put("quantity", new BigDecimal(0));
        		}
        		tmp = (String)  p.get("unitCost");
        		if(!UtilValidate.isEmpty(tmp)){
        			ri.put("unitCost", new BigDecimal(tmp));
        		}else{
        			ri.put("unitCost", new BigDecimal(0));
        		}
            	ri.put("currencyUomId", p.get("currencyUomId"));
            	ri.put("reason", p.get("reason"));
            	ri.put("userLogin", userLogin);
            	dispatcher.runSync("createPlanItemPO", ri);
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
        return successResult;
	}
    public static Map<String, Object> createPOPlanHeader(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue poplan = delegator.makeValidValue("PurchasingOrderPlan", context);
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			poplan.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOSuccess", locale));
		return retMap;
	}
    public static Map<String, Object> updatePOPlanHeader(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String year = (String) context.get("year");
		try {
			GenericValue plan = delegator.findOne("PurchasingOrderPan", UtilMisc.toMap("partyId", partyId,"year",year), false);
			plan.setNonPKFields(context);
			plan.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
    public static Map<String, Object> createPlanItemPO(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue item = delegator.makeValidValue("PurchasingOrderPlanItem", context);
		String poPlanSeqId = delegator.getNextSeqId("PurchasingOrderPlanItem");
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			item.put("poPlanSeqId", poPlanSeqId);
			item.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "createRequirementPOSuccess", locale));
		return retMap;
	}
    public static Map<String, Object> deletePOPlanItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			String partyId = (String) context.get("partyId");
			String year = (String) context.get("year");
			String poPlanSeqId = (String) context.get("poPlanSeqId");
			GenericValue planItem = delegator.findOne("PurchasingOrderPlanItem", false, UtilMisc.toMap("partyId", partyId, "year", year, "poPlanSeqId", poPlanSeqId));
			if(planItem == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "ErrorNotFoundRecordToDelete", locale));
			}
			planItem.remove();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "deletePOPlanError", locale));
		}
		retMap.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("DelysProcurementLabels", "deletePOPlanSuccess", locale));
		return retMap;
	}
    public static Map<String, Object> updatePOPlanItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String year = (String) context.get("year");
		String poPlanSeqId = (String) context.get("poPlanSeqId");
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue plan = delegator.findOne("PurchasingOrderPlanItem", UtilMisc.toMap("partyId", partyId,"year",year, "poPlanSeqId", poPlanSeqId), false);
			plan.setNonPKFields(context);
			plan.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "updatePOPlanError", locale));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysProcurementLabels", "updatePOPlanSuccess", locale));
	}
    @SuppressWarnings("unchecked")
	public static Map<String, Object> calculatePlanItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		String curYear = CommonUtil.getCurrentYear(timeZone, locale);
		Map<String, Object> resMap = FastMap.newInstance();
		try {
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId", "PO_PLAN_SUBMITED"));
			conditionList.add(EntityCondition.makeCondition("year", curYear));
			conditionList.add(EntityCondition.makeCondition("roleTypeId", "INTERNAL_ORGANIZATIO"));
			List<GenericValue> plans = delegator.findList("PurchasingOrderPlanDetail", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
			if(plans.isEmpty()){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "ErrorPOPlanEmpty", locale));
			}
			Map<String, Object> planItemUniq = FastMap.newInstance();
			String tmp = ""; String partyId = "";
			List<GenericValue> planItems = FastList.newInstance();
			List<EntityCondition> tmpCond = FastList.newInstance();
			
			for(GenericValue plan : plans){
				tmpCond = FastList.newInstance();
				partyId = plan.getString("partyId");
				tmpCond.add(EntityCondition.makeCondition("year", curYear));
				tmpCond.add(EntityCondition.makeCondition("partyId", partyId));
				planItems = delegator.findList("PurchasingOrderPlanItemDetail",EntityCondition.makeCondition(tmpCond, EntityOperator.OR), null, null, null, false);
				if(planItems.isEmpty()){
					continue;
				}
				for(GenericValue e : planItems){
					tmp = e.getString("productId") + "-" + e.getString("quantityUomId");
					if(!planItemUniq.containsKey(tmp)){
						Map<String, Object> dem  = FastMap.newInstance();
						dem.put("productId", e.getString("productId"));
						dem.put("productName", e.getString("productName"));
						dem.put("quantity", e.get("quantity"));
						dem.put("quantityUomId", e.getString("quantityUomId"));
						planItemUniq.put(tmp, dem);
					}else{
						Map<String, Object> cur = (Map<String, Object>) planItemUniq.get(tmp);
						BigDecimal quan = (BigDecimal) cur.get("quantity");
						BigDecimal newQuan = e.getBigDecimal("quantity");
						quan = quan.add(newQuan);
						cur.put("quantity", quan);
						planItemUniq.put(tmp, cur);
					}
				}
			}
			List<Map<String, Object>> res = FastList.newInstance();
			for(String key : planItemUniq.keySet()){
				res.add((Map<String, Object>)planItemUniq.get(key));
			}
			resMap.put("results", res);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "calculatePOPlanError", locale));
		}
		return resMap;
	}
    public static Map<String, Object> calculateTotalByUom(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		String baseCurrencyUomId = (String) context.get("baseCurrencyUomId");
		String tmp = (String) context.get("data");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if(UtilValidate.isEmpty(tmp)){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "cannotCalculateCurrency", locale));
			}
			JSONArray data = JSONArray.fromObject(tmp);
			Map<String, Object> inp = FastMap.newInstance();
			BigDecimal total = new BigDecimal(0);
			for(int i = 0; i < data.size(); i++){
				JSONObject o = data.getJSONObject(i);
				inp = FastMap.newInstance();
				inp.put("userLogin", userLogin);
				inp.put("uomIdTo", baseCurrencyUomId);
				inp.put("uomId", o.getString("currencyUomId"));
				inp.put("originalValue", new BigDecimal(o.getString("unitCost")));
				inp = dispatcher.runSync("convertUom", inp);
				BigDecimal convertedValue = (BigDecimal) inp.get("convertedValue");
				BigDecimal quan = new BigDecimal(o.getString("quantity"));
				total = total.add(convertedValue.multiply(quan));
			}
			successResult.put("convertedValue", total);
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "cannotCalculateCurrency", locale));
		}
		return successResult;
    }
    
    public static void createCustomtimePeriodByPO(Delegator delegator, String customTimePeriodId, String parentPeriodId, String periodTypeId, String periodName, Date fromDate, Date thruDate, String organizationPartyId) throws GenericEntityException{
		GenericValue customTime = delegator.makeValue("CustomTimePeriod");
		customTime.put("customTimePeriodId", customTimePeriodId);
		customTime.put("parentPeriodId", parentPeriodId);
		customTime.put("periodTypeId", periodTypeId);
		customTime.put("periodName", periodName);
		customTime.put("fromDate", fromDate);
		customTime.put("thruDate", thruDate);
		customTime.put("organizationPartyId", organizationPartyId);
		
		delegator.create(customTime);
	}
	
    
    @SuppressWarnings({ "unused" })
	public static Map<String, Object> customTimePeriodWeekOfMonthByPO(DispatchContext ctx, Map<String,Object> context) throws ParseException, GenericEntityException {
	    Delegator delegator = ctx.getDelegator();
	    String strFromYear = (String)context.get("fromYear");
	    int fromYear = Integer.parseInt(strFromYear);
	    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Map<String, Object> result = new FastMap<String, Object>();
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
	    calendar.setTime(date);
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    
	    DateTime januaryMonth = new DateTime(fromYear, DateTimeConstants.JANUARY, 1, 0, 0);
 		DateTime decemerMonth = new DateTime(fromYear, DateTimeConstants.DECEMBER, 1, 0, 0);
 		DateTime firstDate = januaryMonth.dayOfMonth().withMinimumValue();
 		DateTime lastDate = decemerMonth.dayOfMonth().withMaximumValue();
	    
 		int lastDateOfMonth = lastDate.getDayOfMonth();
 		int firstDateOfMonth = firstDate.getDayOfMonth();
 		
	    calendar.set(Calendar.YEAR, fromYear);
	    calendar.set(Calendar.MONTH, 11);
    	calendar.set(Calendar.DAY_OF_MONTH, lastDateOfMonth);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	java.sql.Date endYear = new java.sql.Date(calendar.getTimeInMillis());
    	calendar.set(Calendar.MONTH, 0);
    	calendar.set(Calendar.DAY_OF_MONTH, firstDateOfMonth);
    	java.sql.Date newYear = new java.sql.Date(calendar.getTimeInMillis());
    	List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "PO_YEAR", "fromDate", newYear, "thruDate", endYear)), null, null, null, false);
    	
    	if(listCustomTimePeriod.isEmpty()){
    		String customTimePeriodIdYear = delegator.getNextSeqId("CustomTimePeriod");
        	createCustomtimePeriodByPO(delegator, customTimePeriodIdYear, null, "PO_YEAR", "Năm " + fromYear, newYear, endYear, "company");
        	
        	for (int j = 0; j < 12; j++) {
        		String customTimePeriodIdMonth = delegator.getNextSeqId("CustomTimePeriod");
        		createMonthOfYeah(delegator, fromYear, j, customTimePeriodIdMonth, customTimePeriodIdYear);
        	}
        	result.put("value", "success");
    	}else{
    		result.put("value", "exits");
    	}
	    return result;
	}
    
    
    public static void createMonthOfYeah(Delegator delegator, int year, int month, String customTimePeriodIdMonth, String customTimePeriodIdYear) throws GenericEntityException {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.MONTH, month);
    	
    	switch (month) {
		case 0:		
			DateTime januaryMonth = new DateTime(year, DateTimeConstants.JANUARY, 1, 0, 0);
	 		DateTime firstDate = januaryMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate = januaryMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth = firstDate.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth);
	 		java.sql.Date firstDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth = lastDate.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
	    	java.sql.Date endDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_FIRST_MONTH", "tháng 1", firstDayOfMonth, endDayOfMonth, "company");
			break;
		case 1:
			DateTime februaryMonth = new DateTime(year, DateTimeConstants.FEBRUARY, 1, 0, 0);
	 		DateTime firstDate2 = februaryMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate2 = februaryMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth2 = firstDate2.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth2);
	 		java.sql.Date firstDayOfMonth2 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth2 = lastDate2.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth2);
	    	java.sql.Date endDayOfMonth2 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_SECOND_MONTH", "tháng 2", firstDayOfMonth2, endDayOfMonth2, "company");
			break;
		case 2:
			DateTime marchMonth = new DateTime(year, DateTimeConstants.MARCH, 1, 0, 0);
	 		DateTime firstDate3 = marchMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate3 = marchMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth3 = firstDate3.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth3);
	 		java.sql.Date firstDayOfMonth3 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth3 = lastDate3.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth3);
	    	java.sql.Date endDayOfMonth3 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_THIRD_MONTH", "tháng 3", firstDayOfMonth3, endDayOfMonth3, "company");
			break;
		case 3:
			DateTime aprilMonth = new DateTime(year, DateTimeConstants.APRIL, 1, 0, 0);
	 		DateTime firstDate4 = aprilMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate4 = aprilMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth4 = firstDate4.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth4);
	 		java.sql.Date firstDayOfMonth4 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth4 = lastDate4.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth4);
	    	java.sql.Date endDayOfMonth4 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_FOURTH_MONTH", "tháng 4", firstDayOfMonth4, endDayOfMonth4, "company");
			break;
		case 4:
			DateTime mayMonth = new DateTime(year, DateTimeConstants.MAY, 1, 0, 0);
	 		DateTime firstDate5 = mayMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate5 = mayMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth5 = firstDate5.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth5);
	 		java.sql.Date firstDayOfMonth5 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth5 = lastDate5.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth5);
	    	java.sql.Date endDayOfMonth5 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_FIFTH_MONTH", "tháng 5", firstDayOfMonth5, endDayOfMonth5, "company");
			break;
		case 5:
			DateTime juneMonth = new DateTime(year, DateTimeConstants.JUNE, 1, 0, 0);
	 		DateTime firstDate6 = juneMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate6 = juneMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth6 = firstDate6.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth6);
	 		java.sql.Date firstDayOfMonth6 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth6 = lastDate6.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth6);
	    	java.sql.Date endDayOfMonth6 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_SIXTH_MONTH", "tháng 6", firstDayOfMonth6, endDayOfMonth6, "company");
			break;
		case 6:
			DateTime julyMonth = new DateTime(year, DateTimeConstants.JULY, 1, 0, 0);
	 		DateTime firstDate7 = julyMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate7 = julyMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth7 = firstDate7.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth7);
	 		java.sql.Date firstDayOfMonth7 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth7 = lastDate7.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth7);
	    	java.sql.Date endDayOfMonth7 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_SEVENTH_MONTH", "tháng 7", firstDayOfMonth7, endDayOfMonth7, "company");
			break;
		case 7:
			DateTime augustMonth = new DateTime(year, DateTimeConstants.AUGUST, 1, 0, 0);
	 		DateTime firstDate8 = augustMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate8 = augustMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth8 = firstDate8.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth8);
	 		java.sql.Date firstDayOfMonth8 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth8 = lastDate8.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth8);
	    	java.sql.Date endDayOfMonth8 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_EIGHTH_MONTH", "tháng 8", firstDayOfMonth8, endDayOfMonth8, "company");
			break;
		case 8:
			DateTime septemberMonth = new DateTime(year, DateTimeConstants.SEPTEMBER, 1, 0, 0);
	 		DateTime firstDate9 = septemberMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate9 = septemberMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth9 = firstDate9.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth9);
	 		java.sql.Date firstDayOfMonth9 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth9 = lastDate9.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth9);
	    	java.sql.Date endDayOfMonth9 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_NINTH_MONTH", "tháng 9", firstDayOfMonth9, endDayOfMonth9, "company");
			break;
		case 9:
			DateTime octoberMonth = new DateTime(year, DateTimeConstants.OCTOBER, 1, 0, 0);
	 		DateTime firstDate10 = octoberMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate10 = octoberMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth10 = firstDate10.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth10);
	 		java.sql.Date firstDayOfMonth10 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth10 = lastDate10.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth10);
	    	java.sql.Date endDayOfMonth10 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_TENTH_MONTH", "tháng 10", firstDayOfMonth10, endDayOfMonth10, "company");
			break;
		case 10:
			DateTime novemberMonth = new DateTime(year, DateTimeConstants.NOVEMBER, 1, 0, 0);
	 		DateTime firstDate11 = novemberMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate11 = novemberMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth11 = firstDate11.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth11);
	 		java.sql.Date firstDayOfMonth11 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth11 = lastDate11.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth11);
	    	java.sql.Date endDayOfMonth11 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_ELEVENTH_MONTH", "tháng 11", firstDayOfMonth11, endDayOfMonth11, "company");
			break;
		case 11:
			DateTime decemberMonth = new DateTime(year, DateTimeConstants.DECEMBER, 1, 0, 0);
	 		DateTime firstDate12 = decemberMonth.dayOfMonth().withMinimumValue();
	 		DateTime lastDate12 = decemberMonth.dayOfMonth().withMaximumValue();
	 		int firtDayOfMonth12 = firstDate12.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, firtDayOfMonth12);
	 		java.sql.Date firstDayOfMonth12 = new java.sql.Date(calendar.getTimeInMillis());
	 		int lastDayOfMonth12 = lastDate12.getDayOfMonth();
	 		calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth12);
	    	java.sql.Date endDayOfMonth12 = new java.sql.Date(calendar.getTimeInMillis());
	    	createCustomtimePeriodByPO(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "PO_TWELFTH_MONTH", "tháng 12", firstDayOfMonth12, endDayOfMonth12, "company");
			break;
		default:
			break;
    	}
	}
    
    @SuppressWarnings({ "unused", "unchecked" })
	public static Map<String, Object> createPlanByPO(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodPO = (String)context.get("customTimePeriodPO");
		String customTimePeriodSales = "";
		String internalId = "";
		String organizationId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", delegator);
		String planName = (String)context.get("planName");
		List<String> listProductPlanId = (List<String>)context.get("productPlanId[]");
		
		GenericValue thisCustomTimePO = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodPO), false);
		Date fromDate = null;
		if(UtilValidate.isNotEmpty(thisCustomTimePO)){
			fromDate = thisCustomTimePO.getDate("fromDate");
			List<GenericValue> thisCustomTime = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDate, "periodTypeId", "PO_YEAR")), null, null, null, false);
			if(UtilValidate.isNotEmpty(thisCustomTime))customTimePeriodSales = (String) EntityUtil.getFirst(thisCustomTime).get("customTimePeriodId");
			List<GenericValue> listCustomTimeSalesQuarter = new ArrayList<GenericValue>();
			List<GenericValue> test = new ArrayList<GenericValue>();
			GenericValue createByUserGe = (GenericValue)context.get("userLogin");
			String createByUser = (String)createByUserGe.get("userLoginId");
			String partyIdFrom = (String) createByUserGe.get("partyId");
			
			List<GenericValue> 	listPartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)), null, null, null, false);
			for (GenericValue partyRelationship : listPartyRelationship) {
				String roleTypeIdTo = (String) partyRelationship.get("roleTypeIdTo");
				GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdTo), false);
				String parentTypeId = (String) roleType.get("parentTypeId");
				if(parentTypeId != null){
					if(parentTypeId.equals("DEPARTMENT")){
						internalId = (String) partyRelationship.get("partyIdTo");
					}
				}
			}
			List<GenericValue> checkPOPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodPO, "internalPartyId", internalId)), null, null, null, false);
			if(UtilValidate.isEmpty(checkPOPlan)){
				String parentId = null;
				String productPlanType = "PO_PLAN";
				String currencyUomId = "USD";
				String modifiedBy = null;
				String supplierId = null;
				String statusId = "PLAN_CREATED";
				List<String> orderBy = new ArrayList<String>();
				orderBy.add("thruDate");
				String productPlanIdParent = CreatePOPlanHeader(delegator, parentId, productPlanType, customTimePeriodPO, currencyUomId, createByUser, modifiedBy, planName, organizationId, internalId, supplierId, statusId, null);
				for (String productPlanId : listProductPlanId) {
					GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
					productPlanHeader.put("parentProductPlanId", productPlanIdParent);
					delegator.store(productPlanHeader);
				}
				result.put("value", "success");
			}else{
				result.put("value", "exits");
			}
		} 
		else{
			List<GenericValue> checkPOPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodPO, "internalPartyId", internalId, "customTimePeriodIdOfSales", customTimePeriodSales)), null, null, null, false);
			GenericValue checkImportPlanIsTrue = EntityUtil.getFirst(checkPOPlan);
			checkImportPlanIsTrue.put("productPlanName", planName);
			delegator.store(checkImportPlanIsTrue);
			result.put("value", "exits");
		}
		return result;
	}
    
    @SuppressWarnings("unused")
	public static String CreatePOPlanHeader(Delegator delegator, String parentId, String productPlanType, String customTimePeriodId,String currencyUomId, String createByUser, String modifiedBy, String namePlan, String organizationId, String internalId, String supplierId, String statusId, String customTimePeriodSales) throws GenericEntityException{
		int result=0;
		GenericValue productPlanHeader = delegator.makeValue("ProductPlanHeader");
	    String productPlanId = delegator.getNextSeqId("ProductPlanHeader");
	    productPlanHeader.put("productPlanId", productPlanId);
	    productPlanHeader.put("parentProductPlanId", parentId);
	    productPlanHeader.put("productPlanTypeId", productPlanType);
	    productPlanHeader.put("customTimePeriodId", customTimePeriodId);
	    productPlanHeader.put("currencyUomId", currencyUomId);
	    productPlanHeader.put("createByUserLoginId", createByUser);
	    productPlanHeader.put("modifiedByUserLoginId", modifiedBy);
	    productPlanHeader.put("productPlanName", namePlan);
	    productPlanHeader.put("organizationPartyId", organizationId);
	    productPlanHeader.put("internalPartyId", internalId);
	    productPlanHeader.put("supplierPartyId", supplierId);
	    productPlanHeader.put("statusId", statusId);
	    productPlanHeader.put("customTimePeriodIdOfSales", customTimePeriodSales);
	    delegator.createOrStore(productPlanHeader);
	    return productPlanId;
    }
    
    public static Map<String, Object> loadCustomTimePeriodPO(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String periodTypeId = (String) context.get("periodTypeId");
		String parentPeriodId = (String) context.get("parentPeriodId");
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", periodTypeId, "parentPeriodId", parentPeriodId)), UtilMisc.toSet("customTimePeriodId", "parentPeriodId", "periodName"), null, null, false);
		result.put("listCustomTimePeriod", listCustomTimePeriod);
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetDetailPlanByProductPlanId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String customTimePeriodId = parameters.get("customTimePeriodId")[0];
    	String productPlanId = parameters.get("productPlanId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
    	List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue customTimePeriod : listCustomTimePeriod) {
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(customTimePeriod);
			for (GenericValue productPlanItem : listProductPlanItem) {
					listRowDetails.add(productPlanItem);
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("productCategoryId", rowDetail.getString("productCategoryId"));
				mapTmp.put("planQuantity", rowDetail.getBigDecimal("planQuantity"));
				mapTmp.put("quantityUomId", rowDetail.getString("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPlanByPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	String internalId = "";
    	GenericValue createByUserGe = (GenericValue)context.get("userLogin");
		String createByUser = (String)createByUserGe.get("userLoginId");
		String partyIdFrom = (String) createByUserGe.get("partyId");
		
		List<GenericValue> 	listPartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)), null, null, null, false);
		for (GenericValue partyRelationship : listPartyRelationship) {
			String roleTypeIdTo = (String) partyRelationship.get("roleTypeIdTo");
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdTo), false);
			String parentTypeId = (String) roleType.get("parentTypeId");
			if(parentTypeId != null){
				if(parentTypeId.equals("DEPARTMENT")){
					internalId = (String) partyRelationship.get("partyIdTo");
				}
			}
		}
		
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.EQUALS, internalId));
    		listAllConditions.add(EntityCondition.makeCondition("createByUserLoginId", EntityOperator.EQUALS, createByUser));
    		listAllConditions.add(EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, "PO_PLAN"));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("ProductPlanHeader", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    public static Map<String, Object> loadListCustomTimePeriodByCustomTimePeriodId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		List<GenericValue> listCustomTimePeriodDetail = new ArrayList<GenericValue>();
		Map<String, Object> listCustomTimePeriodDetailDetailMap = new HashMap<String, Object>();
		for (GenericValue customTimePeriod : listCustomTimePeriod) {
			String customTimePeriodIdByData = (String) customTimePeriod.get("customTimePeriodId");
			listCustomTimePeriodDetail = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodIdByData)), null, null, null, false);
			listCustomTimePeriodDetailDetailMap.put(customTimePeriodIdByData, listCustomTimePeriodDetail);
		}
		result.put("listCustomTimePeriodMap", listCustomTimePeriod);
		result.put("listCustomTimePeriodDetailMap", listCustomTimePeriodDetailDetailMap);
		return result;
		
	}
    
    @SuppressWarnings("unused")
	public static Map<String, Object> loadProductPlanHeaderByProductPlanId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanId = (String) context.get("productPlanId");
		List<GenericValue> listCustomTimePeriodDetail = new ArrayList<GenericValue>();
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		Map<String, Object> listCustomTimePeriodDetailDetailMap = new HashMap<String, Object>();
		for (GenericValue customTimePeriod : listCustomTimePeriod) {
			String customTimePeriodIdByData = (String) customTimePeriod.get("customTimePeriodId");
			listCustomTimePeriodDetail = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodIdByData)), null, null, null, false);
		}
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPlanDetailByProductPlanId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	String parentProductPlanId = parameters.get("productPlanId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("parentProductPlanId", EntityOperator.EQUALS, parentProductPlanId));
    		listAllConditions.add(EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, "PO_PLAN"));
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PLAN_CREATED"));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("ProductPlanHeader", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    public static Map<String, Object> loadProductPlanItemByCustomTimePeriodId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanId = (String) context.get("productPlanId");
		GenericValue productPlanHeaderData = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
		String statusId = (String) productPlanHeaderData.get("statusId");
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId)), null, null, null, false);
		Map<String, Object> mapData = new HashMap<String, Object>();
		if(!listProductPlanHeader.isEmpty()){
			for (GenericValue productPlanHeader : listProductPlanHeader) {
				String productPlanIdData = (String) productPlanHeader.get("productPlanId");
				String internalPartyIdData = (String) productPlanHeader.get("internalPartyId");
				List<GenericValue> listProductPlanItemData = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanIdData)), null, null, null, false);
				mapData.put(internalPartyIdData, listProductPlanItemData);
			}
		}
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
		result.put("listProductPlanItem", listProductPlanItem);
		result.put("listCustomTimePeriodMonth", listCustomTimePeriod);
		result.put("statusId", statusId);
		result.put("mapListDataProductPlanItem", mapData);
		return result;
	}
    
    public static Map<String, Object> addProductPlanItem(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String) context.get("productId");
		String productPlanId = (String) context.get("productPlanId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		GenericValue productPlanItem = null;
		for (GenericValue customTimePeriod : listCustomTimePeriod) {
			productPlanItem = delegator.makeValue("ProductPlanItem");
			BigDecimal planQuantity = new BigDecimal(0);
			String customTimePeriodIdData = (String) customTimePeriod.get("customTimePeriodId");
			delegator.setNextSubSeqId(productPlanItem, "productPlanItemSeqId", 5, 1);
			productPlanItem.put("productPlanId", productPlanId);
			productPlanItem.put("productId", productId);
			productPlanItem.put("planQuantity", planQuantity);
			productPlanItem.put("statusId", "PLAN_CREATED");
			productPlanItem.put("customTimePeriodId", customTimePeriodIdData);
			try {
				delegator.createOrStore(productPlanItem);
    		} catch (GenericEntityException e) {
    		    return ServiceUtil.returnError(e.getStackTrace().toString());
    		}
		}
		return result;
	}
    
    @SuppressWarnings({ "unchecked" })
	public static Map<String, Object> updateDataToProductPlanItemByProductId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productPlanId = (String) context.get("productPlanId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		List<String> productIdList = (List<String>)context.get("productId[]");
		List<String> firstMonthList = (List<String>)context.get("firstMonth[]");
		List<String> secondMonthList = (List<String>)context.get("secondMonth[]");
		List<String> thirdMonthList = (List<String>)context.get("thirdMonth[]");
		List<String> fourthMonthList = (List<String>)context.get("fourthMonth[]");
		List<String> fifthMonthList = (List<String>)context.get("fifthMonth[]");
		List<String> sixthMonthList = (List<String>)context.get("sixthMonth[]");
		List<String> seventhMonthList = (List<String>)context.get("seventhMonth[]");
		List<String> eighthMonthList = (List<String>)context.get("eighthMonth[]");
		List<String> ninthMonthList = (List<String>)context.get("ninthMonth[]");
		List<String> tenthMonthList = (List<String>)context.get("tenthMonth[]");
		List<String> eleventhMonthList = (List<String>)context.get("eleventhMonth[]");
		List<String> twelfthMonthList = (List<String>)context.get("twelfthMonth[]");
		List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		for (int i = 0; i < productIdList.size(); i++) {
			String productId = productIdList.get(i);
			String planQuantity1 = firstMonthList.get(i);
			BigDecimal planQuantityBig1 = new BigDecimal(planQuantity1);
			String planQuantity2 = secondMonthList.get(i);
			BigDecimal planQuantityBig2 = new BigDecimal(planQuantity2);
			String planQuantity3 = thirdMonthList.get(i);
			BigDecimal planQuantityBig3 = new BigDecimal(planQuantity3);
			String planQuantity4 = fourthMonthList.get(i);
			BigDecimal planQuantityBig4 = new BigDecimal(planQuantity4);
			String planQuantity5 = fifthMonthList.get(i);
			BigDecimal planQuantityBig5 = new BigDecimal(planQuantity5);
			String planQuantity6 = sixthMonthList.get(i);
			BigDecimal planQuantityBig6 = new BigDecimal(planQuantity6);
			String planQuantity7 = seventhMonthList.get(i);
			BigDecimal planQuantityBig7 = new BigDecimal(planQuantity7);
			String planQuantity8 = eighthMonthList.get(i);
			BigDecimal planQuantityBig8 = new BigDecimal(planQuantity8);
			String planQuantity9 = ninthMonthList.get(i);
			BigDecimal planQuantityBig9 = new BigDecimal(planQuantity9);
			String planQuantity10 = tenthMonthList.get(i);
			BigDecimal planQuantityBig10 = new BigDecimal(planQuantity10);
			String planQuantity11 = eleventhMonthList.get(i);
			BigDecimal planQuantityBig11 = new BigDecimal(planQuantity11);
			String planQuantity12 = twelfthMonthList.get(i);
			BigDecimal planQuantityBig12 = new BigDecimal(planQuantity12);
			List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productPlanId", productPlanId)), null, null, null, false);
			for (GenericValue productPlanItem : listProductPlanItem) {
				String customTimePeriodIdData = (String) productPlanItem.get("customTimePeriodId");
				for(GenericValue customTimePeriod: listCustomTimePeriod){
					String customTimePeriodIdCheck = (String) customTimePeriod.get("customTimePeriodId");
					String periodTypeId = (String) customTimePeriod.get("periodTypeId");
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_FIRST_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig1);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_SECOND_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig2);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_THIRD_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig3);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_FOURTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig4);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_FIFTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig5);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_SIXTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig6);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_SEVENTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig7);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_EIGHTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig8);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_NINTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig9);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_TENTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig10);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_ELEVENTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig11);
						delegator.store(productPlanItem);
					}
					if(customTimePeriodIdCheck.equals(customTimePeriodIdData) && periodTypeId.equals("PO_TWELFTH_MONTH")){
						productPlanItem.put("planQuantity", planQuantityBig12);
						delegator.store(productPlanItem);
					}
				}
			}
		}
		return result;
	}
    
    public static Map<String, Object> deleteProductPlanItemByProductId(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String) context.get("productId");
		String productPlanId = (String) context.get("productPlanId");
		List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "productId", productId)), null, null, null, false);
		if(!listProductPlanItem.isEmpty()){
			for (GenericValue productPlanItem : listProductPlanItem) {
				delegator.removeValue(productPlanItem);
			}
		}
		return result;
	}
    
    public static Map<String, Object> sendRequestSaleProductToPO(DispatchContext ctx, Map<String, Object> context){
   		Map<String, Object> result = new FastMap<String, Object>();
   		Delegator delegator = ctx.getDelegator();
   		LocalDispatcher dispatcher = ctx.getDispatcher();
   		String productPlanId = (String)context.get("productPlanId");
   		String roleTypeId = (String)context.get("roleTypeId");
   		String sendMessage = (String)context.get("sendMessage");
   		String action = (String)context.get("action");
   		GenericValue userLogin = (GenericValue)context.get("userLogin");
   		try {
   				GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
   				productPlanHeader.put("statusId", "PLAN_PROPOSED");
   				delegator.store(productPlanHeader);
   				
   				List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
   				if(!listProductPlanItem.isEmpty()){
   					for (GenericValue productPlanItem : listProductPlanItem) {
						productPlanItem.put("statusId", "PLAN_PROPOSED");
						delegator.store(productPlanItem);
					}
   				}
   		} catch (GenericEntityException e) {
   			e.printStackTrace();
   		}
   		try {
   			List<String> listQaAdmin = new ArrayList<String>();
   			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
   			if (!listPartyGroups.isEmpty()){
   				for (String group : listPartyGroups){
   					try {
   						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
   						listManagers = EntityUtil.filterByDate(listManagers);
   						if (!listManagers.isEmpty()){
   							for (GenericValue manager : listManagers){
   								listQaAdmin.add(manager.getString("partyIdFrom"));
   							}
   						}
   					} catch (GenericEntityException e) {
   						ServiceUtil.returnError("get Party relationship error!");
   					}
   				}
   			}
   			if(!listQaAdmin.isEmpty()){
   				for (String managerParty : listQaAdmin){
   					String targetLink = "statusId=PLAN_PROPOSED";
   					String sendToPartyId = managerParty;
   					Map<String, Object> mapContext = new HashMap<String, Object>();
   					mapContext.put("partyId", sendToPartyId);
   					mapContext.put("action", action);
   					mapContext.put("targetLink", targetLink);
   					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
   					mapContext.put("userLogin", userLogin);
   					dispatcher.runSync("createNotification", mapContext);
   				}
   			}
   		} catch (GenericServiceException e) {
   			e.printStackTrace();
   		}
   		return result;
   	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPlanProposalByPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, "PO_PLAN"));
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("ProductPlanHeader", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
    }
    
    public static Map<String, Object> approvalPlanByPartyGroup(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productPlanId = (String) context.get("productPlanId");
		GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
		if(!productPlanHeader.isEmpty()){
			productPlanHeader.put("statusId", "PLAN_APPROVED");
			delegator.store(productPlanHeader);
			List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
			if(!listProductPlanItem.isEmpty()){
				for (GenericValue productPlanItem : listProductPlanItem) {
					productPlanItem.put("statusId", "PLAN_APPROVED");
					delegator.store(productPlanItem);
				}
			}
		}
		result.put("value", "success");
		return result;
	}
    
    @SuppressWarnings({ "unused" })
	public static Map<String, Object> createPlanByPONotPO(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodPO = (String)context.get("customTimePeriodPO");
		String customTimePeriodSales = "";
		String internalId = "";
		String organizationId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", delegator);
		String planName = (String)context.get("planName");
		
		
		GenericValue thisCustomTimePO = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodPO), false);
		Date fromDate = null;
		if(UtilValidate.isNotEmpty(thisCustomTimePO)){
			fromDate = thisCustomTimePO.getDate("fromDate");
			List<GenericValue> thisCustomTime = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDate, "periodTypeId", "PO_YEAR")), null, null, null, false);
			if(UtilValidate.isNotEmpty(thisCustomTime))customTimePeriodSales = (String) EntityUtil.getFirst(thisCustomTime).get("customTimePeriodId");
			List<GenericValue> listCustomTimeSalesQuarter = new ArrayList<GenericValue>();
			List<GenericValue> test = new ArrayList<GenericValue>();
			GenericValue createByUserGe = (GenericValue)context.get("userLogin");
			String createByUser = (String)createByUserGe.get("userLoginId");
			String partyIdFrom = (String) createByUserGe.get("partyId");
			
			List<GenericValue> 	listPartyRelationship = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom)), null, null, null, false);
			for (GenericValue partyRelationship : listPartyRelationship) {
				String roleTypeIdTo = (String) partyRelationship.get("roleTypeIdTo");
				GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeIdTo), false);
				String parentTypeId = (String) roleType.get("parentTypeId");
				if(parentTypeId != null){
					if(parentTypeId.equals("DEPARTMENT")){
						internalId = (String) partyRelationship.get("partyIdTo");
					}
				}
			}
			List<GenericValue> checkPOPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodPO, "internalPartyId", internalId)), null, null, null, false);
			if(UtilValidate.isEmpty(checkPOPlan)){
				String parentId = null;
				String productPlanType = "PO_PLAN";
				String currencyUomId = "USD";
				String modifiedBy = null;
				String supplierId = null;
				String statusId = "PLAN_CREATED";
				List<String> orderBy = new ArrayList<String>();
				orderBy.add("thruDate");
				CreatePOPlanHeader(delegator, parentId, productPlanType, customTimePeriodPO, currencyUomId, createByUser, modifiedBy, planName, organizationId, internalId, supplierId, statusId, null);
				result.put("value", "success");
			}else{
				result.put("value", "exits");
			}
		} 
		else{
			List<GenericValue> checkPOPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodPO, "internalPartyId", internalId, "customTimePeriodIdOfSales", customTimePeriodSales)), null, null, null, false);
			GenericValue checkImportPlanIsTrue = EntityUtil.getFirst(checkPOPlan);
			checkImportPlanIsTrue.put("productPlanName", planName);
			delegator.store(checkImportPlanIsTrue);
			result.put("value", "exits");
		}
		return result;
	}
}
