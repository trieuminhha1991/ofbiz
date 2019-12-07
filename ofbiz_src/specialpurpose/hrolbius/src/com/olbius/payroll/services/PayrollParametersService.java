package com.olbius.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.PayrollWorker;
import com.olbius.payroll.util.PayrollEntityConditionUtils;
import com.olbius.util.CommonUtil;
import com.olbius.util.DateUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class PayrollParametersService {
	public static final String module = PayrollParametersService.class.getName();
    public static final String resource = "hrolbiusUiLabels";
    public static final String resourceNoti = "NotificationUiLabels";
	/*
     * Description: Create new parameter
     * */
    public static Map<String, Object> createPayrollParameters(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String strCode = (String)context.get("code");
    	/*String strName = (String)context.get("name");
    	String strPeriodTypeId = (String)context.get("periodTypeId");
    	String strDefaultValue = (String)context.get("defaultValue");
    	String strType = (String)context.get("type");
    	String strActualValue = (String)context.get("actualValue");
    	String strPartyId = (String)context.get("partyId");
    	String strDescription = (String)context.get("description");*/
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		//check whiteSpace in code
    		if(!CommonUtil.containsValidCharacter(strCode)){
    			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CodeContainsInvalidLetters", locale));
    		}
    		GenericValue payrollParam = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", strCode), false);
    		if(payrollParam != null){
    			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "PayrollParametersHaveExists", UtilMisc.toMap("code", strCode), locale));
    		}
    		
	    	// defaultValue not set will have 0 value
	    	/*if(strDefaultValue == null || strDefaultValue.isEmpty()){
	    		strDefaultValue = "0";
	    	}*/
	    	// actualValue not set will have 0 value
	    	/*if(strActualValue == null || strActualValue.isEmpty()){
	    		strActualValue = "0";
	    	}*/
	    	GenericValue tempPayrollParameter = delegator.makeValue("PayrollParameters");
	    	tempPayrollParameter.setAllFields(context, false, null, null);
    		tempPayrollParameter.create();
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "createError", new Object[] { e.getMessage() }, locale));
		}
		return result;
    }
    
    /*
     * Description: Update parameter
     * */
    public static Map<String, Object> updatePayrollParameters(DispatchContext ctx, Map<String, ? extends Object> context) {
    	String strCode = (String)context.get("code");
    	String strName = (String)context.get("name");
    	// Add Period Type
    	String strPeriodTypeId = (String)context.get("periodTypeId");
    	String strDefaultValue = (String)context.get("defaultValue");
    	String strType = (String)context.get("type");
    	String strActualValue = (String)context.get("actualValue");
    	String strPartyId = (String)context.get("partyId");
    	String strDescription = (String)context.get("description");
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	// defaultValue not set will have 0 value
    	if(strDefaultValue == null || strDefaultValue.isEmpty()){
    		strDefaultValue = "0";
    	}
    	// actualValue not set will have 0 value
    	if(strActualValue == null || strActualValue.isEmpty()){
    		strActualValue = "0";
    	}
    	try {
    		if(strPeriodTypeId == null){
    			strPeriodTypeId = "NA";
    		}
    		List<GenericValue> refType = delegator.findList("PayrollEmplParameterType", EntityCondition.makeCondition(EntityCondition.makeCondition("code", "REF"),
    																												EntityOperator.OR,
    																												EntityCondition.makeCondition("parentTypeId", "REF")), null, null, null, false);
    		List<String> refTypeList = EntityUtil.getFieldListFromEntityList(refType, "code", true);
    		GenericValue tempPayrollParameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", strCode),false);
    		String type = tempPayrollParameter.getString("type");
    		if(type != null && refTypeList.contains(type)){
    			return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotUpdateRefParameters", locale));
    		}
    		tempPayrollParameter.put("name", strName);
    		tempPayrollParameter.put("defaultValue", strDefaultValue);
    		tempPayrollParameter.put("type", strType);
    		tempPayrollParameter.put("actualValue", strActualValue);
    		tempPayrollParameter.put("partyId", strPartyId);
    		tempPayrollParameter.put("description", strDescription);
    		tempPayrollParameter.put("periodTypeId", strPeriodTypeId);
    		tempPayrollParameter.store();
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "updateError", new Object[] { e.getMessage() }, locale));
		}
		return result;
    }
    
    /* get lists payroll parameters */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPayrollParameters(DispatchContext ctx,
			Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	//EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		listSortFields.add("createdStamp DESC");
		listSortFields.add("paramCharacteristicId");
		EntityCondition tmpCond = EntityCondition.makeCondition(
				listAllConditions, EntityOperator.AND);
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			List<GenericValue> listParameters = delegator.findList("PayrollParameters", tmpCond, null, listSortFields, opts, false);
			totalRows = listParameters.size();
			if(end > listParameters.size()){
				end = listParameters.size();
			}
			listParameters = listParameters.subList(start, end);
			for(GenericValue tempGv: listParameters){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("code", tempGv.get("code"));
				tempMap.put("name", tempGv.get("name"));
				//tempMap.put("description", tempGv.get("description"));
				tempMap.put("actualValue", tempGv.get("actualValue"));
				tempMap.put("defaultValue", tempGv.get("defaultValue"));
				tempMap.put("periodTypeId", tempGv.get("periodTypeId"));
				tempMap.put("paramCharacteristicId", tempGv.getString("paramCharacteristicId"));
				String type = tempGv.getString("type");
				tempMap.put("type", type);
				tempMap.put("editable", true);
				if(type != null){
					GenericValue tempType = delegator.findOne("PayrollEmplParameterType", UtilMisc.toMap("code", type), false);
					if("REF".equals(type) || "REF".equals(tempType.getString("parentTypeId"))){
						tempMap.put("editable", false);
					}
				}
				listReturn.add(tempMap);
			}
			successResult.put("TotalRows", String.valueOf(totalRows));
		} catch (Exception e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		return successResult;
	}
    
    public static Map<String, Object> deletePayrollParameter(DispatchContext dctx, Map<String, Object> context){
    	Locale locale = (Locale)context.get("locale");
    	Delegator delegator = dctx.getDelegator();
    	String code  = (String)context.get("code");
    	try {
			GenericValue payrollParam = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			if(payrollParam == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToDelete", locale));
			}
			GenericValue payrollParamAndType = delegator.findOne("PayrollParametersAndType", UtilMisc.toMap("code", code), false);
			if("REF".equals(payrollParamAndType.getString("type")) || "REF".equals(payrollParamAndType.getString("parentTypeId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotDeletePayrollParam_RefType", locale));
			}
			
			String regEpx = "^"+ code + "[\\+\\-\\*/].*|" + code + "|.*[\\+\\-\\*/]" + code + "$" + "|.*[\\(@#\\+\\-\\*/\"]" + code + "[\\+\\-\\*/#;\\)\"].*";
			List<GenericValue> allFormula = delegator.findByAnd("PayrollFormula", null, null, false);
			boolean cannotUpdate = false;
			GenericValue formulaContainsParam = null;
			for(GenericValue tempGv: allFormula){
				String function = tempGv.getString("function");
				if(function != null){
					for(String operator: CommonUtil.listOperatorPayroll){
						function = function.replaceAll(operator, "#");
					}
					function = function.replaceAll("return", "@");
					function = function.replaceAll(" ", "");
					if(Pattern.matches(regEpx, function)){
						cannotUpdate = true;
						formulaContainsParam = tempGv;
						break;
					}
				}
			}
			if(cannotUpdate){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotDeletePayrollParam_setFormula",
						UtilMisc.toMap("code", code, "formula", formulaContainsParam.getString("code"), "formulaName", formulaContainsParam.getString("name")), locale));
			}
			List<GenericValue> emplPayrollParam = delegator.findByAnd("PayrollEmplParameters", UtilMisc.toMap("code", code), null, false);
			if(UtilValidate.isNotEmpty(emplPayrollParam)){
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(emplPayrollParam, "partyId", true);
				StringBuffer buffer = new StringBuffer();
				buffer.append(PartyUtil.getPersonName(delegator, partyIdList.get(0)));
				for(int i = 1; i < partyIdList.size(); i++){
					buffer.append(", ");
					buffer.append(PartyUtil.getPersonName(delegator, partyIdList.get(i)));
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotDeletePayrollParam_emplPayrollParam", UtilMisc.toMap("listEmplParam", buffer.toString()), locale));
			}
			payrollParam.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
    }
    
    /*
     * Description: Assign parameter to employee
     * */
    public static Map<String, Object> assignEmployeePayrollParameters(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	String strPartyId = (String)context.get("partyId");
    	String strCode = (String)context.get("code");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	String strValue = (String)context.get("value");
    	String strActualPercent = (String)context.get("actualPercent");
    	//Add period type
    	String strPeriodTypeId = (String)context.get("periodTypeId");
    	String strType = (String)context.get("type");
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		// Check if exist
    		GenericValue tempEmployeePayrollParameters = delegator.findOne("PayrollEmplParameters", UtilMisc.toMap("partyId",strPartyId,"code", strCode, "fromDate", fromDate),false);
    		if(tempEmployeePayrollParameters != null){
    			tempEmployeePayrollParameters.put("value", strValue);
    			tempEmployeePayrollParameters.put("actualPercent", strActualPercent);
    			tempEmployeePayrollParameters.put("periodTypeId", strPeriodTypeId);
    			tempEmployeePayrollParameters.put("fromDate", fromDate);
    			tempEmployeePayrollParameters.put("thruDate", thruDate);
    			tempEmployeePayrollParameters.put("type", strType);
    			tempEmployeePayrollParameters.store();
    		}else{
    			// get current timestamp
    			Date currDate = new Date();
    			Timestamp currentTimestamp = new Timestamp(currDate.getTime());
    			// if does not exist, create new
    			GenericValue tempEPPs = delegator.makeValue("PayrollEmplParameters", UtilMisc.toMap("code", strCode, 
						"partyId", strPartyId,
						"value", strValue, 
						"actualPercent", strActualPercent,
						"fromDate", fromDate,
						"thruDate", thruDate,
						"inputDate",currentTimestamp,
						"type",strType,
						"periodTypeId", strPeriodTypeId));
    			tempEPPs.create();
    		}
    		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    		result.put(ModelService.SUCCESS_MESSAGE, 
                    UtilProperties.getMessage(resourceNoti, "updateSuccessfully", locale));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "updateError", new Object[] { e.getMessage() }, locale));
		}
		return result;
    }
    
    public static Map<String, Object> createEmplPayrollParameters(DispatchContext dctx, Map<String, Object> context){
    	Locale locale = (Locale)context.get("locale");
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	fromDate = UtilDateTime.getDayStart(fromDate);
    	String partyId = (String)context.get("partyId");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	//String periodTypeId = (String)context.get("periodTypeId");
    	
    	if(thruDate != null){
    		thruDate = UtilDateTime.getDayEnd(thruDate);
    	}
    	String code = (String)context.get("code");
    	
    	List<EntityCondition> dateConds = FastList.newInstance();
    	if(thruDate != null && thruDate.before(fromDate)){
    		return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
    	}
    	if(thruDate == null){
    		dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
    													EntityOperator.OR,
    													EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
    	}else{
    		dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
    		dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
    													EntityOperator.OR,
    													EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
    	}
    	EntityCondition conditions = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId),
    																EntityOperator.AND, 
    																EntityCondition.makeCondition("code", code));
    	EntityCondition allConds = EntityCondition.makeCondition(EntityCondition.makeCondition(dateConds), EntityOperator.AND, conditions);
    	try {
    		GenericValue payrollParameter = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
			List<GenericValue> checkEmplPayrollParam = delegator.findList("PayrollEmplParameters", allConds, null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(checkEmplPayrollParam)){
				GenericValue entityErr = checkEmplPayrollParam.get(0);
				Timestamp fromDateErr = entityErr.getTimestamp("fromDate");
				Timestamp thruDateErr = entityErr.getTimestamp("thruDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateErr);
				String fromDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(fromDate);
				String fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateStr = "____";
				if(thruDate != null){
					cal.setTime(thruDate);
					thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				String thruDateErrStr = "____";
				if(thruDateErr != null){
					cal.setTime(thruDateErr);
					thruDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotAssignParameter", 
																		UtilMisc.toMap("code", payrollParameter.getString("name"), "emplName", PartyUtil.getPersonName(delegator, partyId),
																						"fromDateSet", fromDateStr, "thruDateSet", thruDateStr,
																						"fromDate", fromDateErrStr, "thruDate", thruDateErrStr),locale));
			}
			context.put("fromDate", fromDate);
			context.put("thruDate", thruDate);
			/*context.put("type", payrollParameter.getString("type"));
			if(periodTypeId == null){
				context.put("periodTypeId", payrollParameter.getString("periodTypeId"));
			}*/
			dispatcher.runSync("assignEmployeePayrollParameters", ServiceUtil.setServiceFields(dispatcher, "assignEmployeePayrollParameters", context, (GenericValue)context.get("userLogin"), (TimeZone)context.get("timeZone"), locale));
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
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
    }
    
    public static Map<String, Object> updateEmplPayrollParameters(DispatchContext dctx, Map<String, Object> context){
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	String code = (String)context.get("code");
    	String partyId = (String)context.get("partyId");
    	Locale locale = (Locale)context.get("locale");
    	Delegator delegator = dctx.getDelegator();
    	String value = (String)context.get("value");
    	try {
			GenericValue emplPayrollParam = delegator.findOne("PayrollEmplParameters", UtilMisc.toMap("partyId", partyId, "code", code, "fromDate", fromDate), false);
			if(emplPayrollParam == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("CommonUiLabels", "CommonNoRecordFound", locale));
			}
			
			List<EntityCondition> dateConds = FastList.newInstance();
			if(thruDate != null){
				if(thruDate.before(fromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
				}
				dateConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
				dateConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate));
			}else{
				dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																EntityOperator.OR,
															EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)));
			}
			EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId), EntityOperator.AND, EntityCondition.makeCondition("code", code));
			conds = EntityCondition.makeCondition(conds, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, fromDate));
			List<GenericValue> checkEntityNotValid = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(conds, EntityOperator.AND, EntityCondition.makeCondition(dateConds)), null, null, null, false);
			if(UtilValidate.isNotEmpty(checkEntityNotValid)){
				GenericValue entityNotValid = checkEntityNotValid.get(0);
				Timestamp fromDateErr = entityNotValid.getTimestamp("fromDate");
				Timestamp thruDateErr = entityNotValid.getTimestamp("thruDate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(fromDateErr);
				String fromDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				cal.setTime(fromDate);
				String fromDateUpdateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateUpdateStr = "____";
				if(thruDate != null){
					cal.setTime(thruDate);
					thruDateUpdateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				String thruDateErrStr = "____";
				if(thruDateErr != null){
					cal.setTime(thruDateErr);
					thruDateErrStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotUpdatePayrolParamDateInvalid", 
																	UtilMisc.toMap("fromDateUpdate", fromDateUpdateStr, "thruDateUpdate", thruDateUpdateStr,
																					"fromDateError", fromDateErrStr, "thruDateErr", thruDateErrStr), locale));
			}
			if(value == null){
				context.put("value", "0");
			}
			emplPayrollParam.setNonPKFields(context);
			emplPayrollParam.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	 
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
    }
    
    public static Map<String, Object> deletePayrollEmplParameters(DispatchContext dctx, Map<String, Object> context){
    	Locale locale = (Locale)context.get("locale");
    	Delegator delegator = dctx.getDelegator();
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	String partyId = (String)context.get("partyId");
    	String code = (String)context.get("code");
    	try {
			GenericValue deleteEmplParam = delegator.findOne("PayrollEmplParameters", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "code", code), false);
			if(deleteEmplParam == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToDelete", locale));
			}
			deleteEmplParam.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPayrollParameters(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	//String currDeptId = parameters.get("currDept") != null? parameters.get("currDept")[0]: null;
    	String partyGroupId = request.getParameter("partyGroupId");
    	if(partyGroupId == null){
    		partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
    	}
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		List<GenericValue> emplList = FastList.newInstance();
		//String[] emplPositionTypeId = parameters.get("emplPositionTypeId");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);	
			emplList = buildOrg.getEmployeeInOrg(delegator);
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
			retMap.put("listIterator", listReturn);
			retMap.put("TotalRows", String.valueOf(totalRows));
		
			for(GenericValue tmpGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String tempPartyId = tmpGv.getString("partyId");
				List<GenericValue> emplPayrollParameters = delegator.findList("PayrollEmplParameters", EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), EntityOperator.AND, EntityCondition.makeCondition("partyId", tempPartyId)), null, null, null, false);
				List<GenericValue> emplPos = PartyUtil.getCurrPositionTypeOfEmpl(delegator, tempPartyId);
				tempMap.put("partyId", tempPartyId);
				//GenericValue tempParty = delegator.findOne("Person", UtilMisc.toMap("partyId", tempPartyId), false);
				tempMap.put("emplName", PartyUtil.getPersonName(delegator, tempPartyId));
				if(UtilValidate.isNotEmpty(emplPos)){
					tempMap.put("emplPositionTypeId", emplPos.get(0).getString("emplPositionTypeId"));
				}
				GenericValue currDept = PartyUtil.getDepartmentOfEmployee(delegator, tempPartyId);
				if(currDept != null){
					tempMap.put("currDept", PartyHelper.getPartyName(delegator, currDept.getString("partyIdFrom"), false));
				}	
				tempMap.put("totalParameters", emplPayrollParameters.size());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListParamPosTypeGeo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	List<GenericValue> payrollParamPositionTypeList = FastList.newInstance();
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	Timestamp fromDate = null, thruDate = null;
    	TimeZone timeZone = (TimeZone)context.get("timeZone");
    	Locale locale = (Locale)context.get("locale");
    	if(fromDateStr != null){
    		fromDate = new Timestamp(Long.parseLong(fromDateStr));
    		fromDate = UtilDateTime.getDayStart(fromDate);
    	}else{
    		fromDate = UtilDateTime.getMonthStart(nowTimestamp);
    	}
    	if(thruDateStr != null){
    		thruDate = new Timestamp(Long.parseLong(fromDateStr));
    		thruDate = UtilDateTime.getDayEnd(thruDate);
    	}else{
    		thruDate = UtilDateTime.getMonthEnd(nowTimestamp, timeZone, locale);
    	}
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page + 1;
		//int end = start + size;
		
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = UtilMisc.toList("-fromDate", "emplPositionTypeId");
    	}
    	try {
    		listIterator = delegator.find("PayrollParamPositionTypeAndParameters", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		payrollParamPositionTypeList = listIterator.getPartialList(start, size);
    		totalRows = listIterator.getResultsSizeAfterPartialList();
    		listIterator.close();
    		List<Map<String, Object>> listReturn = FastList.newInstance();
    		for(GenericValue tempGv: payrollParamPositionTypeList){
    			Map<String, Object> tempMap = FastMap.newInstance();
    			String payrollParamPositionTypeId = tempGv.getString("payrollParamPositionTypeId");
    			List<GenericValue> pyrllParamPosTypeGeoInclude = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
    					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_INCLUDE"), null, false);
    			List<GenericValue> pyrllParamPosTypeGeoExclude = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
    					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", "PYRLL_EXCLUDE"), null, false);
    			List<String> includeGeoList = FastList.newInstance();
    			List<String> excludeGeoList = FastList.newInstance();
    			for(GenericValue includeGeo: pyrllParamPosTypeGeoInclude){
    				String geoId = includeGeo.getString("geoId");
    				GenericValue geoIncludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				includeGeoList.add(geoIncludeGv.getString("geoName"));
    			}
    			tempMap.put("includeGeo", StringUtils.join(includeGeoList, ", "));
    			for(GenericValue excludeGeo: pyrllParamPosTypeGeoExclude){
    				String geoId = excludeGeo.getString("geoId");
    				GenericValue geoExcludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				excludeGeoList.add(geoExcludeGv.getString("geoName"));
    			}
    			if(UtilValidate.isNotEmpty(excludeGeoList)){
    				tempMap.put("excludeGeo", StringUtils.join(excludeGeoList, ", "));
    			}
    			tempMap.put("payrollParamPositionTypeId", payrollParamPositionTypeId);
    			tempMap.put("emplPositionTypeId", tempGv.getString("emplPositionTypeId"));
    			tempMap.put("periodTypeId", tempGv.getString("periodTypeId"));
    			tempMap.put("code", tempGv.getString("code"));
    			tempMap.put("payGradeId", tempGv.getString("payGradeId"));
    			tempMap.put("roleTypeGroupId", tempGv.getString("roleTypeGroupId"));
    			tempMap.put("salaryStepSeqId", tempGv.getString("salaryStepSeqId"));
    			tempMap.put("rateTypeId", tempGv.getString("rateTypeId"));
    			tempMap.put("fromDate", tempGv.getTimestamp("fromDate"));
    			tempMap.put("thruDate", tempGv.getTimestamp("thruDate"));
    			tempMap.put("rate", tempGv.get("rate"));
    			tempMap.put("rateAmount", tempGv.get("rateAmount"));
    			tempMap.put("rateCurrencyUomId", tempGv.get("rateCurrencyUomId"));
    			listReturn.add(tempMap);
    		}
    		successResult.put("listIterator", listReturn);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListEmplPositionTypeRate service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	successResult.put("TotalRows", String.valueOf(totalRows));
    	return successResult;		
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getListParameterEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyId = request.getParameter("partyId");
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
    	listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR,
    			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp())));
    	try {
    		listIterator = delegator.find("EmplPayrollParameterAndParameters", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListParameterEmpl service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
    
    public static Map<String, Object> processPayrollParamPositionType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String uomId = (String)context.get("uomId");
		//String periodTypeId = (String)context.get("periodTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amount = (String)context.get("rateAmount");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String includeGeoId = (String)context.get("includeGeoId");
		String excludeGeoId = (String)context.get("excludeGeoId");
		String code = (String)context.get("code");
		Map<String, Object> retMap = FastMap.newInstance();
		if(thruDate!= null){
			thruDate = UtilDateTime.getDayEnd(thruDate);
			if(fromDate.after(thruDate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
			}
		}
		List<String> includeGeoList = FastList.newInstance();
		List<String> excludeGeoList = null;
		JSONArray includeGeoJson = JSONArray.fromObject(includeGeoId);
		for(int i = 0; i < includeGeoJson.size(); i++){
			includeGeoList.add(includeGeoJson.getJSONObject(i).getString("includeGeoId"));
		}
		
		if(excludeGeoId != null){
			excludeGeoList = FastList.newInstance();
			JSONArray excludeGeoJson = JSONArray.fromObject(excludeGeoId);
			for(int i = 0; i < excludeGeoJson.size(); i++){
				excludeGeoList.add(excludeGeoJson.getJSONObject(i).getString("excludeGeoId"));
			}
		}
		
		BigDecimal amountValue = new BigDecimal(amount);
		List<EntityCondition> conditions  = FastList.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		if(uomId == null){
			uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
		}
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conditions.add(EntityCondition.makeCondition("code", code));
		conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
		conditions.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		try {
			List<GenericValue> checkedEntityList = delegator.findList("PayrollParamPositionType", EntityCondition.makeCondition(conditions), null, 
					UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(checkedEntityList)){
				List<String> payrollParamPositionTypeIdList = EntityUtil.getFieldListFromEntityList(checkedEntityList, "payrollParamPositionTypeId", true);
				List<EntityCondition> payrollParamPositionTypeCondList = FastList.newInstance();
				payrollParamPositionTypeCondList.add(EntityCondition.makeCondition("payrollParamPositionTypeId", EntityJoinOperator.IN, payrollParamPositionTypeIdList));
				payrollParamPositionTypeCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				payrollParamPositionTypeCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition payrollParamPositionTypeCond = EntityCondition.makeCondition(payrollParamPositionTypeCondList); 
				
				List<GenericValue> payrollParamPositionTypeGeoApplList = delegator.findList("PyrllParamPosTypeGeoAppl", 
						payrollParamPositionTypeCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(payrollParamPositionTypeGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					for(String includeGeoIdTemp: includeGeoList){
						GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
						includeGeoName.add(includeGeo.getString("geoName"));
					}
					GenericValue pyrllParamPosTypeGeoAppl = payrollParamPositionTypeGeoApplList.get(0);
					GenericValue payrollParamPositionType = delegator.findOne("PayrollParamPositionType", 
							UtilMisc.toMap("payrollParamPositionTypeId", pyrllParamPosTypeGeoAppl.getString("payrollParamPositionTypeId")), false);
					String geoId = pyrllParamPosTypeGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp pyrllParamPosTypeFromDate = payrollParamPositionType.getTimestamp("fromDate");
					Timestamp pyrllParamPosTypeThruDate = payrollParamPositionType.getTimestamp("thruDate");
					BigDecimal rateAmount = payrollParamPositionType.getBigDecimal("rateAmount");
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(pyrllParamPosTypeFromDate.getTime());
					String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
					String thruDateErr = null;
					if(pyrllParamPosTypeThruDate != null){
						cal.setTimeInMillis(pyrllParamPosTypeThruDate.getTime());
						thruDateErr = DateUtil.getDateMonthYearDesc(cal);
					}else{
						thruDateErr = UtilProperties.getMessage("HrCommonUiLabels", "CommonAfterThat", locale);	
					}
					cal.setTimeInMillis(fromDate.getTime());
					String fromDateSub = DateUtil.getDateMonthYearDesc(cal);
					String thruDateSet = null;
					if(thruDate != null){
						cal.setTimeInMillis(thruDate.getTime());
						thruDateSet = DateUtil.getDateMonthYearDesc(cal);
					}else{
						thruDateSet = UtilProperties.getMessage("HrCommonUiLabels", "CommonAfterThat", locale);
					}
					GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
					GenericValue payrollParameters = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", code), false);
					return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "ParamForPosTypeInGeoSet",  
												UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
																"code", payrollParameters.getString("name"),
																"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", rateAmount,
																"geoErr", StringUtils.join(includeGeoName, ", "),
																"geo", geoExists.getString("geoName"),
																"emplPositionType", emplPositionType.getString("description")), locale));
				}
			}
			Map<String, Object> resultService;
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("emplPositionTypeId", emplPositionTypeId);
			serviceCtx.put("code", code);
			//serviceCtx.put("periodTypeId", periodTypeId);
			serviceCtx.put("uomId", uomId);
			serviceCtx.put("rateAmount", amountValue);
			serviceCtx.put("roleTypeGroupId", roleTypeGroupId);
			serviceCtx.put("fromDate", fromDate);
			serviceCtx.put("thruDate", thruDate);
			resultService = dispatcher.runSync("createPayrollParamPositionType", serviceCtx);
			if(ServiceUtil.isSuccess(resultService)){
				String payrollParamPositionTypeId = (String)resultService.get("payrollParamPositionTypeId");
				for(String includeGeoIdAppl: includeGeoList){
					dispatcher.runSync("createPayrollParamPositionTypeGeoAppl", 
							UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "geoId", includeGeoIdAppl, "enumId", "PYRLL_INCLUDE", "userLogin", userLogin));
				}
				if(UtilValidate.isNotEmpty(excludeGeoList)){
					for(String excludeGeoIdAppl: excludeGeoList){
						dispatcher.runSync("createPayrollParamPositionTypeGeoAppl", 
								UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "geoId", excludeGeoIdAppl, "enumId", "PYRLL_EXCLUDE", "userLogin", userLogin));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
    
    public static Map<String, Object> createPayrollParamPositionType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try {
			GenericValue newEntity = delegator.makeValue("PayrollParamPositionType");
			newEntity.setNonPKFields(context);
			newEntity.set("fromDate", fromDate);
			String payrollParamPositionTypeId = delegator.getNextSeqId("PayrollParamPositionType");
			newEntity.set("payrollParamPositionTypeId", payrollParamPositionTypeId);
			newEntity.create();
			retMap.put("payrollParamPositionTypeId", payrollParamPositionTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
    
    public static Map<String, Object> createPayrollParamPositionTypeGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue payrollParamPositionTypeGeoAppl = delegator.makeValue("PyrllParamPosTypeGeoAppl");
		payrollParamPositionTypeGeoAppl.setAllFields(context, false, null, null);
		try {
			payrollParamPositionTypeGeoAppl.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> updatePayrollParamPosTypeAndGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String payrollParamPositionTypeId = (String)context.get("payrollParamPositionTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String code = (String)context.get("code");
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		List<String> includeGeoList = (List<String>)context.get("includeGeoList");
		List<String> excludeGeoList = (List<String>)context.get("excludeGeoList");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(thruDate!= null){
			thruDate = UtilDateTime.getDayEnd(thruDate);
			if(fromDate.after(thruDate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
			}
		}
		List<EntityCondition> conditions  = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
		conditions.add(EntityCondition.makeCondition("payrollParamPositionTypeId", EntityJoinOperator.NOT_EQUAL, payrollParamPositionTypeId));
		conditions.add(EntityCondition.makeCondition("code", code));
		conditions.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		try {
			GenericValue payrollParamPositionType = delegator.findOne("PayrollParamPositionType", UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId), false);
			if(payrollParamPositionType == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			List<GenericValue> pyrllParamPosTypeList = delegator.findList("PayrollParamPositionType", EntityCondition.makeCondition(conditions), 
					null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(pyrllParamPosTypeList)){
				List<String> pyrllParamPosTypeIdList = EntityUtil.getFieldListFromEntityList(pyrllParamPosTypeList, "payrollParamPositionTypeId", true);
				List<EntityCondition> pyrllParamPosTypeCondList = FastList.newInstance();
				pyrllParamPosTypeCondList.add(EntityCondition.makeCondition("payrollParamPositionTypeId", EntityJoinOperator.IN, pyrllParamPosTypeIdList));
				pyrllParamPosTypeCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				pyrllParamPosTypeCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition pyrllParamPosTypeCond = EntityCondition.makeCondition(pyrllParamPosTypeCondList); 
				
				List<GenericValue> pyrllParamPosTypeGeoApplList = delegator.findList("PayrollParamPositionTypeAndGeo", 
						pyrllParamPosTypeCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(pyrllParamPosTypeGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					for(String includeGeoIdTemp: includeGeoList){
						GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
						includeGeoName.add(includeGeo.getString("geoName"));
					}
					GenericValue pyrllParamPosTypeGeoAppl = pyrllParamPosTypeGeoApplList.get(0);
					GenericValue pyrllParamPosType = delegator.findOne("PayrollParamPositionType", UtilMisc.toMap("payrollParamPositionTypeId", pyrllParamPosTypeGeoAppl.getString("payrollParamPositionTypeId")), false);
					String geoId = pyrllParamPosTypeGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp pyrllParamPosTypeFromDate = pyrllParamPosType.getTimestamp("fromDate");
					Timestamp pyrllParamPosTypeThruDate = pyrllParamPosType.getTimestamp("thruDate");
					BigDecimal rateAmount = pyrllParamPosType.getBigDecimal("rateAmount");
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(pyrllParamPosTypeFromDate.getTime());
					String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
					String thruDateErr = null;
					if(pyrllParamPosTypeThruDate != null){
						cal.setTimeInMillis(pyrllParamPosTypeThruDate.getTime());
						thruDateErr = DateUtil.getDateMonthYearDesc(cal);
					}else{
						thruDateErr = UtilProperties.getMessage("HrCommonUiLabels", "CommonAfterThat", locale);	
					}
					cal.setTimeInMillis(fromDate.getTime());
					String fromDateSub = DateUtil.getDateMonthYearDesc(cal);
					String thruDateSet = null;
					if(thruDate != null){
						cal.setTimeInMillis(thruDate.getTime());
						thruDateSet = DateUtil.getDateMonthYearDesc(cal);
					}else{
						thruDateSet = UtilProperties.getMessage("HrCommonUiLabels", "CommonAfterThat", locale);
					}
					return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "ParamForPosTypeInGeoNotUpdate",  
												UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
																"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", rateAmount,
																"geoErr", StringUtils.join(includeGeoName, ", "),
																"geo", geoExists.getString("geoName"),
																"emplPositionType", emplPositionType.getString("description")), locale));
				}
			}
			payrollParamPositionType.setNonPKFields(context);
			payrollParamPositionType.store();
			dispatcher.runSync("updatePyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "geoIdList", includeGeoList, "enumId", "PYRLL_INCLUDE", "userLogin", userLogin));
			dispatcher.runSync("updatePyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "geoIdList", excludeGeoList, "enumId", "PYRLL_EXCLUDE", "userLogin", userLogin));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> updatePyrllParamPosTypeGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		List<String> geoIdList = (List<String>)context.get("geoIdList");
		String enumId = (String)context.get("enumId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String payrollParamPositionTypeId = (String)context.get("payrollParamPositionTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<GenericValue> geoApplList = delegator.findByAnd("PyrllParamPosTypeGeoAppl", 
					UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", enumId), null, false);
			for(GenericValue geoAppl: geoApplList){
				String geoId = geoAppl.getString("geoId");
				if(UtilValidate.isEmpty(geoIdList) || !geoIdList.contains(geoId)){
					geoAppl.remove();
				}
			}
			if(UtilValidate.isNotEmpty(geoIdList)){
				List<String> geoIdExists = EntityUtil.getFieldListFromEntityList(geoApplList, "geoId", true);
				for(String geoId: geoIdList){
					if(!geoIdExists.contains(geoId)){
						dispatcher.runSync("createPayrollParamPositionTypeGeoAppl", 
								UtilMisc.toMap("payrollParamPositionTypeId", payrollParamPositionTypeId, "enumId", enumId, "geoId", geoId, "userLogin", userLogin));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
    
    public static Map<String, Object> getEmplParameters(DispatchContext dctx, Map<String, Object> context){
    	Map<String, Object> retMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
    	String partyId = (String)context.get("partyId");
    	Timestamp fromDate = (Timestamp)context.get("fromDate");
    	Timestamp thruDate = (Timestamp)context.get("thruDate");
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(EntityCondition.makeCondition("partyId", partyId));
    	conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
    	conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityJoinOperator.OR,
    					EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate)));
    	String paramCharacteristicId = (String)context.get("paramCharacteristicId");
    	if(paramCharacteristicId != null){
    		conditions.add(EntityCondition.makeCondition("paramCharacteristicId", paramCharacteristicId));
    	}
    	try {
			List<GenericValue> emplParam = delegator.findList("PayrollEmplAndParameters", EntityCondition.makeCondition(conditions), null, null, null, false);
			retMap.put("payrollEmplParam", emplParam);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	return retMap;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplBonusAllowances(DispatchContext dctx, Map<String, Object> context){
    	Map<String, Object> retMap = FastMap.newInstance();
    	Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	retMap.put("listIterator", listReturn);
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyGroupId = request.getParameter("partyGroupId");
    	if(partyGroupId == null){
    		partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
    	}
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
    	thruDate = UtilDateTime.getDayEnd(thruDate);
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		
		try {
			List<GenericValue> listAllowanceAndBonus = delegator.findList("PayrollParameters", 
					EntityCondition.makeCondition("paramCharacteristicId", EntityJoinOperator.IN, UtilMisc.toList("THUONG", "PHU_CAP")), null, null, null, false);
			List<String> codeList = EntityUtil.getFieldListFromEntityList(listAllowanceAndBonus, "code", true);
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			List<String> emplListId = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
			EntityCondition partyListConds = EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, emplListId);
			List<EntityCondition> conditionCommonList = FastList.newInstance();
			conditionCommonList.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
			conditionCommonList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
			conditionCommonList.add(EntityCondition.makeCondition("code", EntityJoinOperator.IN, codeList));
			EntityCondition commonConds = EntityCondition.makeCondition(conditionCommonList);
			List<GenericValue> emplParameters = delegator.findList("PayrollEmplParamatersParty", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, partyListConds), UtilMisc.toSet("partyId"), null, null, false);
			totalRows = emplParameters.size();
			if(end > totalRows){
				end = totalRows;
			}
			emplParameters = emplParameters.subList(start, end);
			for(GenericValue param: emplParameters){
				Map<String, Object> tempMap = FastMap.newInstance(); 
				String partyId = param.getString("partyId");
				EntityCondition tempPartyCond = EntityCondition.makeCondition("partyId", partyId);
				List<GenericValue> payrollEmplParam = delegator.findList("PayrollEmplAndParameters", EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, tempPartyCond), null, null, null, false);
				List<GenericValue> payrollEmplBonusParam = EntityUtil.filterByAnd(payrollEmplParam, UtilMisc.toMap("paramCharacteristicId", "THUONG"));
				List<GenericValue> payrollEmplAllowanceParam = EntityUtil.filterByAnd(payrollEmplParam, UtilMisc.toMap("paramCharacteristicId", "PHU_CAP"));
				BigDecimal amountBonus = PayrollWorker.getTotalAmountParamValue(payrollEmplBonusParam, "value");
				BigDecimal amountAllowance = PayrollWorker.getTotalAmountParamValue(payrollEmplAllowanceParam, "value");
				List<String> departmentList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				List<String> departmentName = FastList.newInstance();
				for(String departmentId: departmentList){
					GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", departmentId), false);
					departmentName.add(partyGroup.getString("groupName"));
				}
				List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				List<String> emplPositionType = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("department", StringUtils.join(departmentName, ", "));
				tempMap.put("emplPositionTypeId", StringUtils.join(emplPositionType, ", "));				
				tempMap.put("emplBonusAmount", amountBonus);
				tempMap.put("emplAllowances", amountAllowance);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
    	return retMap;
    }
}
