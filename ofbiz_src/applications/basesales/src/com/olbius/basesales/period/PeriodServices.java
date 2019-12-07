package com.olbius.basesales.period;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;

public class PeriodServices {
	public static final String module = PeriodServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCustomTimePeriod(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	try {
    		List<String> periodTypeIds = FastList.newInstance();
    		if (parameters.containsKey("periodTypeId") && parameters.get("periodTypeId").length > 0) {
    			String periodTypeId = parameters.get("periodTypeId")[0];
    			if (UtilValidate.isNotEmpty(periodTypeId)) {
    				periodTypeIds.add(periodTypeId);
    			}
    		} else {
    			periodTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "period.type.id.sales");
    		}
    		listAllConditions.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeIds));
    		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationId));
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-fromDate");
    		}
    		listIterator = delegator.find("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListCustomTimePeriod service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> createSalesCustomTimePeriod(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Locale locale = (Locale) context.get("locale");
    	
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	//String parentPeriodId = (String) context.get("parentPeriodId");
    	//String periodName = (String) context.get("periodName");
    	String periodNumStr = (String) context.get("periodNum");
    	//String periodTypeId = (String) context.get("periodTypeId");
    	String fromDateStr = (String) context.get("fromDate");
    	String thruDateStr = (String) context.get("thruDate");
    	
    	successResult.put("customTimePeriodId", customTimePeriodId);
    	try {
    		Timestamp fromDate = null;
            Timestamp thruDate = null;
            Long periodNum = null;
            try {
            	if (UtilValidate.isNotEmpty(periodNumStr)) {
            		periodNum = Long.parseLong(periodNumStr);
            	}
    	        if (UtilValidate.isNotEmpty(fromDateStr)) {
    	        	Long fromDateL = Long.parseLong(fromDateStr);
    	        	fromDate = new Timestamp(fromDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(thruDateStr)) {
    	        	Long thruDateL = Long.parseLong(thruDateStr);
    	        	thruDate = new Timestamp(thruDateL);
    	        }
            } catch (Exception e) {
            	Debug.logError(e, UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            }
            context.remove("fromDate");
            context.remove("thruDate");
            context.remove("periodNum");
            
            context.put("fromDate", fromDate);
            context.put("thruDate", thruDate);
            context.put("periodNum", periodNum);
            
            String orgPartyId = SalesUtil.getCurrentOrganization(delegator);
            context.put("organizationPartyId", orgPartyId);
            
            Map<String, Object> contextCtx = ServiceUtil.setServiceFields(dispatcher, "createCustomTimePeriod", context, userLogin, null, locale);
            Map<String, Object> resultValue = dispatcher.runSync("createCustomTimePeriod", contextCtx);
            if (ServiceUtil.isError(resultValue)) {
            	return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
            }
            customTimePeriodId = (String) resultValue.get("customTimePeriodId");
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling createSalesCustomTimePeriod service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("customTimePeriodId", customTimePeriodId);
    	return successResult;
    }
    
    public static void createCustomtimePeriod2(Delegator delegator, String customTimePeriodId, String parentPeriodId, String periodTypeId, String periodName, Date fromDate, Date thruDate, String organizationPartyId) throws GenericEntityException{
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
    
    @SuppressWarnings("deprecation")
	public static Map<String, Object> createSalesCustomTimePeriodQuick(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException {
	    Delegator delegator = ctx.getDelegator();
	    Locale locale = (Locale) context.get("locale");
	    Map<String, Object> successReturn = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    
	    String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	    
	    String yearPeriodRaw = (String)context.get("yearPeriod");
	    int yearPeriod = Integer.parseInt(yearPeriodRaw);
	    int state = 1;
	    
	    List<GenericValue> listCreatedYear = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "SALES_YEAR", "organizationPartyId", organizationId)), null, null, null, false);
    	for (GenericValue y : listCreatedYear) {
			Date fromYearValidate = y.getDate("fromDate");
			Date thruYearValidate = y.getDate("thruDate");
			
			int fromYearValidateValue = fromYearValidate.getYear();
			int thruYearValidateValue = thruYearValidate.getYear();
			
			if((yearPeriod - 1900) == fromYearValidateValue && (yearPeriod - 1900) == thruYearValidateValue) {
				state = 0;
				break;
			}
    	}
	    if (state == 0) {
	    	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "ThisCustomTimePeriodIsExisted", locale));
	    } else {
		    Calendar calendar = Calendar.getInstance();
		    Date date = new Date();
		    calendar.setTime(date);
		    
		    calendar.set(Calendar.YEAR, yearPeriod);
		    calendar.set(Calendar.MONTH, 11);
	    	calendar.set(Calendar.DAY_OF_MONTH, 31);
	    	calendar.set(Calendar.HOUR, 0);
	    	calendar.set(Calendar.MINUTE, 0);
	    	calendar.set(Calendar.SECOND, 0);
	    	calendar.set(Calendar.MILLISECOND, 0);
	    	java.sql.Date endYear = new java.sql.Date(calendar.getTimeInMillis());
	    	calendar.set(Calendar.MONTH, 0);
	    	calendar.set(Calendar.DAY_OF_MONTH, 1);
	    	java.sql.Date newYear = new java.sql.Date(calendar.getTimeInMillis());
	    	String customTimePeriodIdYear = "SY_" + yearPeriod + "_" + organizationId; //delegator.getNextSeqId("CustomTimePeriod");
	    	
	    	createCustomtimePeriod2(delegator, customTimePeriodIdYear, null, "SALES_YEAR", "" + yearPeriod, newYear, endYear, organizationId);
	    	
	    	String customQuaterPeriodId = "";
	    	String quarterName = UtilProperties.getMessage(resource, "BSQuarter", locale);
	    	String monthName = UtilProperties.getMessage(resource, "BSMonth", locale);
	    	int quarterNum = 0;
		    for(int k = 0; k < 12; k++){
		    	if (k % 3 == 0) {
		    		// create custom time period QUARTER
		    		java.sql.Date firstDayOfQuarter = null;
			    	java.sql.Date endDayOfQuarter = null;
		    		calendar.set(Calendar.MONTH, k);
		    		calendar.set(Calendar.DAY_OF_MONTH, 1);
		    		firstDayOfQuarter = new java.sql.Date(calendar.getTimeInMillis());
		    		
		    		calendar.set(Calendar.MONTH, k + 2);
		    		int dateEndOfQuarter = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		    		calendar.set(Calendar.DAY_OF_MONTH, dateEndOfQuarter);
		    		endDayOfQuarter = new java.sql.Date(calendar.getTimeInMillis());
		    		
		    		quarterNum = k / 3 + 1;
		    		customQuaterPeriodId = "SQ_" + yearPeriod + "_" + quarterNum + "_" + organizationId; //delegator.getNextSeqId("CustomTimePeriod");
			    	createCustomtimePeriod2(delegator, customQuaterPeriodId, customTimePeriodIdYear, "SALES_QUARTER", quarterName + " " + quarterNum, firstDayOfQuarter, endDayOfQuarter, organizationId);
		    	}
		    	
		    	// create custom time period MONTH
		    	java.sql.Date firstDayOfMonth = null;
		    	java.sql.Date endDayOfMonth = null;
		    	calendar.set(Calendar.MONTH, k);
		    	calendar.set(Calendar.DAY_OF_MONTH, 1);
		    	firstDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
		    	
		    	int dateEndMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		    	calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
		    	endDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
		    	
		    	String customTimePeriodIdMonth = "SM_" + yearPeriod + "_" + (k+1) + "_" + organizationId; //delegator.getNextSeqId("CustomTimePeriod");
		    	createCustomtimePeriod2(delegator, customTimePeriodIdMonth, customQuaterPeriodId, "SALES_MONTH", monthName + " " +(k+1), firstDayOfMonth, endDayOfMonth, organizationId);
		    	
		    	/*
		    	//fix ngay 26
		    	calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
		    	java.sql.Date curMonth = new java.sql.Date(calendar.getTimeInMillis());
	
		    	calendar.set(Calendar.DAY_OF_MONTH, 1);
		    	java.sql.Date prevMonth = new java.sql.Date(calendar.getTimeInMillis());
		    	while(!curMonth.equals(prevMonth)){
		    		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		    		prevMonth = new java.sql.Date(calendar.getTimeInMillis());
		    		
		    		//fix thu 2
		    		if(dayWeek == 2){
		    			int dateW = calendar.get(Calendar.DATE);
		    			int monthW = k+1;
		    			int yearW = calendar.get(Calendar.YEAR);
		    			String customTimePeriodIdWeek = delegator.getNextSeqId("CustomTimePeriod");
		    			calendar.add(Calendar.DATE, 7);
		    			java.sql.Date endW = new java.sql.Date(calendar.getTimeInMillis());
		    			//create custom time week
		    			createCustomtimePeriod2(delegator, customTimePeriodIdWeek, customTimePeriodIdMonth, "SALES_WEEK", "" + dateW + "-" + monthW + "-" + yearW, prevMonth, endW, orgParty);
		    			calendar.add(Calendar.DATE, -7);
		    		}
		    		calendar.add(Calendar.DATE, 1);
		    	}
		    	 */
		    }
		    return successReturn;
	    }
	}
}
