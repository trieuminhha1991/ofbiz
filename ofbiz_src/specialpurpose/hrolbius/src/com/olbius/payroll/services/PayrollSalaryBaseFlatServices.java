package com.olbius.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
import org.ofbiz.entity.condition.EntityFunction;
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
import org.ofbiz.service.ServiceUtil;

import com.olbius.payroll.PayrollUtil;
import com.olbius.payroll.util.PayrollEntityConditionUtils;
import com.olbius.util.DateUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;
import com.olbius.util.PersonHelper;
import com.olbius.util.SecurityUtil;

public class PayrollSalaryBaseFlatServices {
	public static final String module = PayrollSalaryBaseFlatServices.class.getName();
	public static Map<String, Object> updatePartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String workEffortId = (String)context.get("workEffortId");
		String rateTypeId = (String)context.get("rateTypeId");
		String rateCurrencyUomId = (String)context.get("rateCurrencyUomId");
		String periodTypeId = (String)context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp effectiveFromDate = (Timestamp)context.get("effectiveFromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		
		try {
			//expire current rateAmount of employee
			GenericValue rateAmount = delegator.findOne("RateAmount", UtilMisc.toMap("workEffortId", workEffortId, "rateTypeId", rateTypeId, 
																					"rateCurrencyUomId", rateCurrencyUomId, "periodTypeId", periodTypeId,
																					"fromDate", fromDate,
																					"emplPositionTypeId", emplPositionTypeId,
																					"partyId", partyId), false);
			Timestamp thruDate = UtilDateTime.getDayEnd(effectiveFromDate, -1L);
			rateAmount.set("thruDate", thruDate);
			rateAmount.store();
			context.put("fromDate", UtilDateTime.getDayStart(effectiveFromDate));
			//create new rateAmount employee
			Map<String, Object> ctxMap = ServiceUtil.setServiceFields(dispatcher, "createPartyRateAmount", context, userLogin, timeZone, locale);
			dispatcher.runSync("createPartyRateAmount", ctxMap);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createPartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String workEffortId = (String)context.get("workEffortId");
		String rateTypeId = (String)context.get("rateTypeId");
		Locale locale = (Locale)context.get("locale");
		
		try {
			//create new rateAmount 
			GenericValue rateAmount = delegator.makeValidValue("RateAmount", context);			
			rateAmount.setAllFields(context, false, null, null);
			if(UtilValidate.isEmpty(workEffortId)){
				rateAmount.set("workEffortId", "_NA_");
			}
			if(UtilValidate.isEmail(rateTypeId)){
				rateAmount.set("rateTypeId", "_NA_");
			}
			
			//rateAmount.set("fromDate", fromDate);
			delegator.create(rateAmount);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> createPartyRateAmountSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		String uomId = (String)context.get("uomId");
		String periodTypeId = (String)context.get("periodTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amount = (String)context.get("amount");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = null;
		Locale locale = (Locale)context.get("locale");
		if(thruDateStr != null){
			thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		}
		if(thruDate != null && fromDate.after(thruDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
		}
		BigDecimal amountValue = new BigDecimal(amount);
		List<EntityCondition> conditions  = FastList.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		List<EntityCondition> dateConds = FastList.newInstance();
		try {
			//check time of employee contract
			Timestamp dateJoinCompany = PersonHelper.getDateEmplJoinOrg(delegator, partyId);
			Timestamp dateEndContract = PersonHelper.getDateEndContractOfEmpl(delegator, partyId);
			if(dateJoinCompany != null && fromDate.before(dateJoinCompany)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotSetSalaryForEmplBeforeJoinCompany", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,"dateJoinCompany", DateUtil.getDateMonthYearDesc(dateJoinCompany)), locale));
			}
			
			if(dateEndContract != null && fromDate.after(dateEndContract)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "CannotSetSalaryForEmplAfterEndContract", 
						UtilMisc.toMap("fromDate", DateUtil.getDateMonthYearDesc(fromDate) ,"dateEndContract", DateUtil.getDateMonthYearDesc(dateEndContract)), locale));
			}
			
			if(thruDate == null){
	    		/*dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	    													EntityOperator.OR,
	    													EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));*/
				EntityCondition tmpConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null),
																		EntityJoinOperator.AND,
																		EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
				dateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
															EntityJoinOperator.OR,
															tmpConds));
	    	}else{
	    		if(thruDate.before(fromDate)){
	    			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
	    		}
	    		EntityCondition condition1 = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null),
											EntityOperator.AND,
											EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
	    		condition1 = EntityCondition.makeCondition(condition1, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	    		
	    		EntityCondition condition2 = EntityCondition.makeCondition("thruDate", null);
	    		condition2 = EntityCondition.makeCondition(condition2, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
	    		dateConds.add(EntityCondition.makeCondition(condition1, EntityOperator.OR, condition2));
	    		
	    	}
			List<GenericValue> rateAmountList = delegator.findList("RateAmount", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), EntityOperator.AND, EntityCondition.makeCondition(dateConds)), null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(rateAmountList)){
				GenericValue rateAmountErr = EntityUtil.getFirst(rateAmountList);
				Timestamp rateAmountFromDate = rateAmountErr.getTimestamp("fromDate");
				Timestamp rateAmountThruDate = rateAmountErr.getTimestamp("thruDate");
				BigDecimal rateAmount = rateAmountErr.getBigDecimal("rateAmount");
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(rateAmountFromDate.getTime());
				String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
				String thruDateErr = null;
				if(rateAmountThruDate != null){
					cal.setTimeInMillis(rateAmountThruDate.getTime());
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
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "SalaryInPeriodIsSet",  UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
															"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", rateAmount), locale));
			}
			
			EntityCondition expireConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
			List<GenericValue> rateAmountExpired = delegator.findList("RateAmount", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
					EntityOperator.AND, 
								expireConds), null, UtilMisc.toList("fromDate"), null, false);
			Timestamp thruDateExpired = UtilDateTime.getDayEnd(fromDate, -1L);
			for(GenericValue tempGv: rateAmountExpired){
				tempGv.set("thruDate", thruDateExpired);
				tempGv.store();
			}
			
			
			dispatcher.runSync("createPartyRateAmount", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, 
																		"emplPositionTypeId", emplPositionTypeId,
																		"periodTypeId", periodTypeId,
																		"rateCurrencyUomId", uomId,
																		"rateAmount", amountValue,
																		"fromDate", fromDate,
																		"thruDate", thruDate));
		} catch (GenericEntityException e) { 
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> deletePartyRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String fromDateStr = (String)context.get("fromDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		context.put("fromDate", fromDate);
		GenericValue rateAmount = delegator.makeValidValue("RateAmount", context);
		try {
			rateAmount.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplPositionTypeRateConvertDate(DispatchContext dctx, Map<String, Object> context){
		String fromDateStr = (String)context.get("fromDate");
	    String thruDateStr = (String)context.get("thruDate");
	    LocalDispatcher dispatcher = dctx.getDispatcher();
	    Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
	    Timestamp thruDate = null;
	    if(thruDateStr != null){
	    	thruDate = new Timestamp(Long.parseLong(thruDateStr));
	    }
	    Map<String, Object> map = FastMap.newInstance();
	    map.putAll(context);
	    map.put("fromDate", fromDate);
	    map.put("thruDate", thruDate);
	    Map<String, Object> retMap = FastMap.newInstance();
	    try {
			retMap = dispatcher.runSync("createEmplPositionTypeRateAmount", map);
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
	    return retMap;
	}
	
	public static Map<String, Object> createEmplPositionTypeRateAmount(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//String fromDateStr = (String)context.get("fromDate");
		//String thruDateStr = (String)context.get("thruDate");
		String uomId = (String)context.get("uomId");
		String periodTypeId = (String)context.get("periodTypeId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String amount = (String)context.get("rateAmount");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		fromDate = UtilDateTime.getDayStart(fromDate);
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String includeGeoId = (String)context.get("includeGeoId");
		String excludeGeoId = (String)context.get("excludeGeoId");
		Locale locale = (Locale)context.get("locale");
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
			uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		}
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conditions.add(EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId));
		List<EntityCondition> dateConds = FastList.newInstance();
		dateConds.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		List<GenericValue> emplPositionTypeRateList;
		try {
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			emplPositionTypeRateList = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
																					EntityOperator.AND, 
																					EntityCondition.makeCondition(dateConds)), 
																					null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPositionTypeRateList)){
				List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateList, "emplPositionTypeRateId", true);
				List<EntityCondition> emplPositionTypeRateCondList = FastList.newInstance();
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.IN, emplPositionTypeRateIdList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition emplPositionTypeRateCond = EntityCondition.makeCondition(emplPositionTypeRateCondList); 
				
				List<GenericValue> emplPositionTypeRateGeoApplList = delegator.findList("EmplPositionTypeRateGeoAppl", 
						emplPositionTypeRateCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(emplPositionTypeRateGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					for(String includeGeoIdTemp: includeGeoList){
						GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
						includeGeoName.add(includeGeo.getString("geoName"));
					}
					GenericValue emplPositionTypeRateGeoAppl = emplPositionTypeRateGeoApplList.get(0);
					GenericValue emplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateGeoAppl.getString("emplPositionTypeRateId")), false);
					String geoId = emplPositionTypeRateGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp emplPosTypeRateFromDate = emplPositionTypeRate.getTimestamp("fromDate");
					Timestamp emplPosTypeRateThruDate = emplPositionTypeRate.getTimestamp("thruDate");
					BigDecimal rateAmount = emplPositionTypeRate.getBigDecimal("rateAmount");
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(emplPosTypeRateFromDate.getTime());
					String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
					String thruDateErr = null;
					if(emplPosTypeRateThruDate != null){
						cal.setTimeInMillis(emplPosTypeRateThruDate.getTime());
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
					return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "SalaryInPeriodIsSet",  
												UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
																"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", rateAmount,
																"geoErr", StringUtils.join(includeGeoName, ", "),
																"geo", geoExists.getString("geoName"),
																"emplPositionType", emplPositionType.getString("description")), locale));
				}
			}
			
			/*EntityCondition expireConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), 
					EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, fromDate));
			conditions.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
			conditions.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
			List<GenericValue> emplPosTypeRateExpired = delegator.findList("OldEmplPositionTypeRateAndGeoAppl", 
					EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), 
																			EntityOperator.AND, 
																			expireConds), null, UtilMisc.toList("fromDate"), null, false);
			List<String> emplPosTypeRateExpiredList = EntityUtil.getFieldListFromEntityList(emplPosTypeRateExpired, "emplPositionTypeRateId", true);
			Timestamp thruDateExpired = UtilDateTime.getDayEnd(fromDate, -1L);
			for(String tempEmplPositionTypeRateId: emplPosTypeRateExpiredList){
				GenericValue tempEmplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", tempEmplPositionTypeRateId), false);
				tempEmplPositionTypeRate.set("thruDate", thruDateExpired);
				tempEmplPositionTypeRate.store();
			}*/
			
			//create emplPositiontype rate by geo
			Map<String, Object> serviceCtx = FastMap.newInstance();
			serviceCtx.put("emplPositionTypeId", emplPositionTypeId);
			serviceCtx.put("roleTypeGroupId", roleTypeGroupId);
			serviceCtx.put("fromDate", fromDate);
			serviceCtx.put("thruDate", thruDate);
			serviceCtx.put("userLogin", userLogin);
			serviceCtx.put("periodTypeId", periodTypeId);
			serviceCtx.put("rateCurrencyUomId", uomId);
			serviceCtx.put("rateAmount", amountValue);
			Map<String, Object> resultService = dispatcher.runSync("createEmplPositionTypeRate", serviceCtx);
			if(ServiceUtil.isSuccess(resultService)){
				String emplPositionTypeRateId = (String)resultService.get("emplPositionTypeRateId");
				for(String tempIncludeGeoId: includeGeoList){
					dispatcher.runSync("createEmplPositionTypeRateGeoAppl", UtilMisc.toMap("geoId", tempIncludeGeoId, 
							"emplPositionTypeRateId",emplPositionTypeRateId,
							"userLogin", userLogin,
							"enumId", "PYRLL_INCLUDE"));
				}
				if(UtilValidate.isNotEmpty(excludeGeoList)){
					for(String tempExcludeGeoId: excludeGeoList){
						dispatcher.runSync("createEmplPositionTypeRateGeoAppl", UtilMisc.toMap("geoId", tempExcludeGeoId, 
								"emplPositionTypeRateId",emplPositionTypeRateId,
								"userLogin", userLogin,
								"enumId", "PYRLL_EXCLUDE"));
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
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
		return retMap; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateOldEmplPositionTypeRateAndGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		String emplPositionTypeRateId = (String)context.get("emplPositionTypeRateId");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
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
		conditions.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.NOT_EQUAL, emplPositionTypeRateId));
		conditions.add(DateUtil.getDateValidConds(fromDate, thruDate, true));
		try {
			GenericValue oldEmplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId), false);
			if(oldEmplPositionTypeRate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
			List<GenericValue> emplPositionTypeRateList = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(conditions), 
					null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isNotEmpty(emplPositionTypeRateList)){
				List<String> emplPositionTypeRateIdList = EntityUtil.getFieldListFromEntityList(emplPositionTypeRateList, "emplPositionTypeRateId", true);
				List<EntityCondition> emplPositionTypeRateCondList = FastList.newInstance();
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("emplPositionTypeRateId", EntityJoinOperator.IN, emplPositionTypeRateIdList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN, includeGeoList));
				emplPositionTypeRateCondList.add(EntityCondition.makeCondition("enumId", "PYRLL_INCLUDE"));
				EntityCondition emplPositionTypeRateCond = EntityCondition.makeCondition(emplPositionTypeRateCondList); 
				
				List<GenericValue> emplPositionTypeRateGeoApplList = delegator.findList("EmplPositionTypeRateGepAppl", 
						emplPositionTypeRateCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(emplPositionTypeRateGeoApplList)){
					List<String> includeGeoName = FastList.newInstance();
					for(String includeGeoIdTemp: includeGeoList){
						GenericValue includeGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", includeGeoIdTemp), false);
						includeGeoName.add(includeGeo.getString("geoName"));
					}
					GenericValue emplPositionTypeRateGeoAppl = emplPositionTypeRateGeoApplList.get(0);
					GenericValue emplPositionTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateGeoAppl.getString("emplPositionTypeRateId")), false);
					String geoId = emplPositionTypeRateGeoAppl.getString("geoId");
					GenericValue geoExists = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
					Timestamp emplPosTypeRateFromDate = emplPositionTypeRate.getTimestamp("fromDate");
					Timestamp emplPosTypeRateThruDate = emplPositionTypeRate.getTimestamp("thruDate");
					BigDecimal rateAmount = emplPositionTypeRate.getBigDecimal("rateAmount");
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(emplPosTypeRateFromDate.getTime());
					String fromDateErr = DateUtil.getDateMonthYearDesc(cal);
					String thruDateErr = null;
					if(emplPosTypeRateThruDate != null){
						cal.setTimeInMillis(emplPosTypeRateThruDate.getTime());
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
					return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "SalaryInPeriodIsSet",  
												UtilMisc.toMap("fromDateSet", fromDateSub, "thruDateSet", thruDateSet, 
																"fromDate", fromDateErr, "thruDate", thruDateErr, "amount", rateAmount,
																"geoErr", StringUtils.join(includeGeoName, ", "),
																"geo", geoExists.getString("geoName"),
																"emplPositionType", emplPositionType.getString("description")), locale));
				}
			}
			oldEmplPositionTypeRate.setNonPKFields(context);
			oldEmplPositionTypeRate.store();
			dispatcher.runSync("updateEmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "geoIdList", includeGeoList, "enumId", "PYRLL_INCLUDE", "userLogin", userLogin));
			dispatcher.runSync("updateEmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "geoIdList", excludeGeoList, "enumId", "PYRLL_EXCLUDE", "userLogin", userLogin));
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
	public static Map<String, Object> updateEmplPositionTypeRateGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		List<String> geoIdList = (List<String>)context.get("geoIdList");
		String enumId = (String)context.get("enumId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplPositionTypeRateId = (String)context.get("emplPositionTypeRateId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			List<GenericValue> geoApplList = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", enumId), null, false);
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
						dispatcher.runSync("createEmplPositionTypeRateGeoAppl", 
								UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", enumId, "geoId", geoId, "userLogin", userLogin));
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
	
	public static Map<String, Object> createEmplPositionTypeRate(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue emplPositionTypeRate = delegator.makeValue("OldEmplPositionTypeRate");
		emplPositionTypeRate.setNonPKFields(context);
		String emplPositionTypeRateId = delegator.getNextSeqId("OldEmplPositionTypeRate");
		emplPositionTypeRate.set("emplPositionTypeRateId", emplPositionTypeRateId);
		try {
			emplPositionTypeRate.create();
			retMap.put("emplPositionTypeRateId", emplPositionTypeRateId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createEmplPositionTypeRateGeoAppl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue emplPositionTypeRateGepAppl = delegator.makeValue("EmplPositionTypeRateGeoAppl");
		emplPositionTypeRateGepAppl.setAllFields(context, false, null, null);
		try {
			emplPositionTypeRateGepAppl.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	//FIXME maybe delete
	public static Map<String, Object> createEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		//String periodTypeId = (String)context.get("periodTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String contactMechId = (String)context.get("contactMechId");
		Locale locale = (Locale)context.get("locale");
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		try {
			GenericValue checkedEntity = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("fromDate", fromDate, /*"periodTypeId", periodTypeId,*/ "emplPositionTypeId", emplPositionTypeId, "contactMechId", contactMechId, "roleTypeGroupId", roleTypeGroupId), false);
			if(checkedEntity != null){
				GenericValue emplPosType = delegator.findOne("EmplPositionTypeId", UtilMisc.toMap("emplPositionTypeId", checkedEntity.getString("emplPositionTypeId")), false);
				Calendar cal = Calendar.getInstance();
				cal.setTime(checkedEntity.getTimestamp("fromDate"));
				String fromDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				String thruDateStr = "___";
				if(checkedEntity.getTimestamp("thruDate") != null){
					cal.setTime(checkedEntity.getTimestamp("thruDate"));
					thruDateStr = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
				}
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "EmplPositionTypeRateIsSet", UtilMisc.toMap("emplPositionType", emplPosType.getString("description"), 
																																		"fromDate", fromDateStr, "thruDate", thruDateStr), locale));
			}
			GenericValue newEntity = delegator.makeValidValue("OldEmplPositionTypeRate", context);
			newEntity.set("fromDate", fromDate);
			newEntity.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	//maybe delete
	public static Map<String, Object> getEmplPostionTypeNotSetSalary(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listEmplPositionTypeNotSet", listReturn);
		try {
			
			List<GenericValue> listEmplPosTypeSet = delegator.findList("OldEmplPositionTypeRate", EntityUtil.getFilterByDateExpr(), null, UtilMisc.toList("emplPositionTypeId"), null, false);
			//GenericValue tempPosType;
			List<GenericValue> emplPositionTypeList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(listEmplPosTypeSet)){
				List<String> emplPositionTypes = EntityUtil.getFieldListFromEntityList(listEmplPosTypeSet, "emplPositionTypeId", true);
				emplPositionTypeList = delegator.findList("EmplPositionType", EntityCondition.makeCondition(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_IN, emplPositionTypes),
																											EntityOperator.AND,
																											EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_EQUAL, "_NA_")),
																											null, UtilMisc.toList("emplPositionTypeId"), null, false);
			}else{
				emplPositionTypeList = delegator.findList("EmplPositionType", EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.NOT_EQUAL, "_NA_"), null, UtilMisc.toList("emplPositionTypeId"), null, false);
			}
			
			for(GenericValue tempGv: emplPositionTypeList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String emplPositionTypeId = tempGv.getString("emplPositionTypeId");
				tempMap.put("emplPositionTypeId", emplPositionTypeId);				
				tempMap.put("description", tempGv.getString("description"));				
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();	
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String periodTypeId = (String) context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		Locale locale = (Locale)context.get("locale");
		try {
			List<EntityCondition> commonConds = FastList.newInstance();
			commonConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
			commonConds.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
			commonConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, fromDate));
			EntityCondition dateConds;
			if(thruDate != null){
				if(thruDate.before(fromDate)){
					return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "DateEnterNotValid", locale));
				}
				dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN, fromDate), 
							EntityOperator.AND, 
							EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
			}else{
				dateConds = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
															EntityOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate));
			}
			EntityCondition conds = EntityCondition.makeCondition(EntityCondition.makeCondition(commonConds), EntityOperator.AND, dateConds); 
			List<GenericValue> checkEntityNotValid = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(conds, EntityOperator.AND, EntityCondition.makeCondition(dateConds)), null, null, null, false);
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
			GenericValue emplPosTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, /*"periodTypeId", periodTypeId,*/ "fromDate", fromDate), false);
			if(UtilValidate.isEmpty(emplPosTypeRate)){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			emplPosTypeRate.setNonPKFields(context);
			if(thruDate != null){
				thruDate = UtilDateTime.getDayEnd(thruDate);
				emplPosTypeRate.set("thruDate", thruDate);
			}
			emplPosTypeRate.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
		
	public static Map<String, Object> deleteEmpPosTypeSalary(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		//String periodTypeId = (String) context.get("periodTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue emplPosTypeRate = delegator.findOne("OldEmplPositionTypeRate", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, /*"periodTypeId", periodTypeId,*/ "fromDate", fromDate), false);
			if(UtilValidate.isEmpty(emplPosTypeRate)){
				ServiceUtil.returnError(UtilProperties.getMessage("hrCommonUiLabels", "NotFoundRecordToDelete", locale));
			}
			/*emplPosTypeRate.set("thruDate", UtilDateTime.nowTimestamp());*/
			emplPosTypeRate.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> getSalaryAmountEmpl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		List<EntityCondition> conditions = FastList.newInstance();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		EntityCondition dateConds = null;
		if(fromDate == null || thruDate == null){
			dateConds = EntityUtil.getFilterByDateExpr();
		}else{
			dateConds = PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate);
		}
		conditions.add(dateConds);
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		//add conditions about emplPositionType
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
			List<String> emplPositionTypes = EntityUtil.getFieldListFromEntityList(emplPos, "emplPositionTypeId", true);
			if(UtilValidate.isNotEmpty(emplPositionTypes)){
				conditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.IN, emplPositionTypes));
			}
			List<GenericValue> rateAmountList = delegator.findList("RateAmount", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, 
					UtilMisc.toList("-fromDate"), null, false);
			//check whether employee have set specific payroll, if not get payroll info base on emplPositionType
			//retMap.put("rateAmountList", rateAmountList);
			if(UtilValidate.isNotEmpty(rateAmountList)){
				GenericValue rateAmount = EntityUtil.getFirst(rateAmountList);
				retMap.put("workEffortId", rateAmount.getString("workEffortId")); 
				retMap.put("rateTypeId", rateAmount.getString("rateTypeId")); 
				retMap.put("rateCurrencyUomId", rateAmount.getString("rateCurrencyUomId"));
				retMap.put("periodTypeId", rateAmount.getString("periodTypeId"));
				retMap.put("fromDate", rateAmount.getTimestamp("fromDate"));
				retMap.put("thruDate", rateAmount.getTimestamp("thruDate"));
				retMap.put("rateAmount", rateAmount.getBigDecimal("rateAmount"));
				retMap.put("emplPositionTypeId", rateAmount.getString("emplPositionTypeId"));
			}else{				
				//BigDecimal tempAmount = BigDecimal.ZERO;
				List<GenericValue> emplPosTypeRate;
				List<String> departmentIdList = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				if(departmentIdList.size() > 0){
					String orgId = departmentIdList.get(0);
					List<String> roleOfOrgId = SecurityUtil.getCurrentRoles(orgId, delegator);
					List<GenericValue> roleTypeGroupList = PartyUtil.getRoleTypeGroupMemberOfListRole(delegator, roleOfOrgId, dateConds);
					if(UtilValidate.isNotEmpty(roleTypeGroupList)){
						String roleTypeGroupId = roleTypeGroupList.get(0).getString("roleTypeGroupId");
						List<GenericValue> postalAddrOrgList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
						EntityCondition roleTypeGroupConds = EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId);
						EntityCondition commonConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, roleTypeGroupConds);
						for(GenericValue tempPosType: emplPos){
							emplPosTypeRate = delegator.findList("OldEmplPositionTypeRate", EntityCondition.makeCondition(
																								EntityCondition.makeCondition("emplPositionTypeId", tempPosType.getString("emplPositionTypeId")),
																								EntityOperator.AND,
																								commonConds), null, null, null, false);
							if(UtilValidate.isNotEmpty(postalAddrOrgList)){
								GenericValue postallAddr = postalAddrOrgList.get(0);
								List<GenericValue> emplPositionTypeRateGeoList = PayrollUtil.getEmplPositionTypeRate(delegator, emplPosTypeRate, postallAddr);
								if(UtilValidate.isNotEmpty(emplPositionTypeRateGeoList)){
									GenericValue posTypeRate = EntityUtil.getFirst(emplPositionTypeRateGeoList);
									Timestamp tempFromDate = posTypeRate.getTimestamp("fromDate");
									//Timestamp tempThruDate = posTypeRate.getTimestamp("thruDate");
									//TODO need convert value of rateAmount by period type to compare
									
									retMap.put("emplPositionTypeId", posTypeRate.getString("emplPositionTypeId"));
									retMap.put("rateAmount", posTypeRate.getBigDecimal("rateAmount"));
									retMap.put("fromDate", DateUtil.getTimestampAfter(DateUtil.getTimestampAfter(tempFromDate, postallAddr.getTimestamp("fromDate")), 
																				tempPosType.getTimestamp("fromDate")));
									retMap.put("periodTypeId", posTypeRate.getString("periodTypeId"));
									retMap.put("rateCurrencyUomId", posTypeRate.getString("rateCurrencyUomId"));
									
								}else{
									retMap.put("emplPositionTypeId", tempPosType.getString("emplPositionTypeId"));
								}
							}
							
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplSalaryBaseFlat(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		//Locale locale = (Locale)context.get("locale");
		//TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<Map<String, Object>> listReturn = FastList.newInstance();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String partyIdGroupId = request.getParameter("partyGroupId");
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		String partyIdParam = (String[])parameters.get("partyId") != null? ((String[])parameters.get("partyId"))[0] : null;
    	String partyNameParam = (String[])parameters.get("partyName") != null? ((String[])parameters.get("partyName"))[0]: null;
    	Map<String, Object> retMap = FastMap.newInstance();
    	try {
    		retMap.put("listIterator", listReturn);
	    	if(partyIdGroupId == null){
	    		//emplList = PartyUtil.getEmployeeInOrg(delegator);
	    		retMap.put("TotalRows", String.valueOf(totalRows));
	    		return retMap;
	    	}
	    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
	    	Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
	    	fromDate = UtilDateTime.getDayStart(fromDate);
	    	thruDate = UtilDateTime.getDayEnd(thruDate);
	    	List<GenericValue> emplList;
    		Organization orgParty = PartyUtil.buildOrg(delegator, partyIdGroupId, true, false);
			emplList = orgParty.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			 
			if(partyIdParam != null){
				emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyIdParam + "%")));
			}
			if(partyNameParam != null){
				partyNameParam = partyNameParam.replaceAll("\\s", "");
				List<EntityCondition> tempConds = FastList.newInstance();
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameFirstNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam.toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullNameLastNameFirst"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastNameFirstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				tempConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstNameLastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + partyNameParam .toUpperCase() + "%")));
				emplList = EntityUtil.filterByOr(emplList, tempConds);			
			}
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
			retMap.put("TotalRows", String.valueOf(totalRows));
		
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String tempPartyId = tempGv.getString("partyId");
				tempMap = dispatcher.runSync("getSalaryAmountEmpl", UtilMisc.toMap("partyId", tempPartyId, "fromDate", fromDate, 
																				   "thruDate" , thruDate, "userLogin", userLogin));
				tempMap.remove("rateAmountList");
				//List<Map<String, Object>> rowDetails = FastList.newInstance();
				//tempMap.put("rowDetail", rowDetails);
				
				List<GenericValue> emplPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, tempPartyId, fromDate, thruDate);
				List<String> emplPosIdList = EntityUtil.getFieldListFromEntityList(emplPos, "emplPositionTypeId", true);
				tempMap.put("partyId", tempPartyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, tempPartyId));
				tempMap.put("allEmplPositionTypeId", emplPosIdList);
				if(UtilValidate.isNotEmpty(emplPos)){
					tempMap.put("emplPositionTypeId", emplPos.get(0).getString("emplPositionTypeId"));
				}
				List<String> deptList = PartyUtil.getDepartmentOfEmployee(delegator, tempPartyId, fromDate, thruDate);
				if(UtilValidate.isNotEmpty(deptList)){
					List<String> deptListName = FastList.newInstance();
					for(String deptId: deptList){
						deptListName.add(PartyHelper.getPartyName(delegator, deptId, false));
					}
					tempMap.put("currDept", StringUtils.join(deptListName, ", "));
				}
				listReturn.add(tempMap);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionTypeRate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	List<GenericValue> emplPositionTypeRateList = FastList.newInstance();
    	int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page + 1;
		//int end = start + size;
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
    	HttpServletRequest request = (HttpServletRequest)context.get("request");
    	String fromDateStr = request.getParameter("fromDate");
    	String thruDateStr = request.getParameter("thruDate");
    	Timestamp fromDate, thruDate;
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	if(fromDateStr != null){
    		fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	}else{
    		fromDate = UtilDateTime.getMonthStart(nowTimestamp);
    	}
    	
    	if(thruDateStr != null){
    		thruDate = new Timestamp(Long.parseLong(thruDateStr));
    	}else{
    		thruDate = UtilDateTime.getMonthEnd(nowTimestamp, timeZone, locale);
    	}
    	listAllConditions.add(tmpConditon);
    	listAllConditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
    	//listAllConditions.add(EntityUtil.getFilterByDateExpr());
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = UtilMisc.toList("emplPositionTypeId");
    		listSortFields.add("-fromDate");
    	}
    	try {
    		listIterator = delegator.find("OldEmplPositionTypeRate", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		emplPositionTypeRateList = listIterator.getPartialList(start, size);
    		totalRows = listIterator.getResultsSizeAfterPartialList();
    		listIterator.close();
    		List<Map<String, Object>> listReturn = FastList.newInstance();
    		for(GenericValue tempGv: emplPositionTypeRateList){
    			Map<String, Object> tempMap = FastMap.newInstance();
    			String emplPositionTypeRateId = tempGv.getString("emplPositionTypeRateId");
    			List<GenericValue> emplPositionTypeRateGeoInclude = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
    					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_INCLUDE"), null, false);
    			List<GenericValue> emplPositionTypeRateGeoExclude = delegator.findByAnd("EmplPositionTypeRateGeoAppl", 
    					UtilMisc.toMap("emplPositionTypeRateId", emplPositionTypeRateId, "enumId", "PYRLL_EXCLUDE"), null, false);
    			List<String> includeGeoList = FastList.newInstance();
    			List<String> excludeGeoList = FastList.newInstance();
    			for(GenericValue includeGeo: emplPositionTypeRateGeoInclude){
    				String geoId = includeGeo.getString("geoId");
    				GenericValue geoIncludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				includeGeoList.add(geoIncludeGv.getString("geoName"));
    			}
    			
    			for(GenericValue excludeGeo: emplPositionTypeRateGeoExclude){
    				String geoId = excludeGeo.getString("geoId");
    				GenericValue geoExcludeGv = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
    				excludeGeoList.add(geoExcludeGv.getString("geoName"));
    			}
    			if(UtilValidate.isNotEmpty(excludeGeoList)){
    				tempMap.put("excludeGeo", StringUtils.join(excludeGeoList, ", "));
    			}
    			tempMap.put("includeGeo", StringUtils.join(includeGeoList, ", "));	
    			tempMap.put("emplPositionTypeRateId", emplPositionTypeRateId);
    			tempMap.put("emplPositionTypeId", tempGv.getString("emplPositionTypeId"));
    			tempMap.put("periodTypeId", tempGv.getString("periodTypeId"));
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
	public static Map<String, Object> getPartyPayrollHistoryDetails(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> resultService = FastMap.newInstance();
		//Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> retMap = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)context.get("partyId");
		try {
			resultService = dispatcher.runSync("getPartyPayrollHistory", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
			List<Map<String, Object>> listRowDetails = (List<Map<String,Object>>)resultService.get("listPartyPayrollHistory");		
			List<Map<String, Object>> rowDetails = FastList.newInstance();
			retMap.put("rowDetail", rowDetails);			
			Collections.sort(listRowDetails, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					Timestamp obj1Time = (Timestamp)o1.get("fromDate");
					Timestamp obj2Time = (Timestamp)o2.get("fromDate");
					if(obj1Time.before(obj2Time)){
						return -1;
					}else if(obj1Time.after(obj2Time)){
						return 1;
					}
					return 0;
				}
			});
			for(Map<String, Object> entry: listRowDetails){
				Map<String, Object> childRowDetail = FastMap.newInstance();
				childRowDetail.put("fromDateDetail", ((Timestamp)entry.get("fromDate")).getTime());
				childRowDetail.put("thruDateDetail", entry.get("thruDate") != null? ((Timestamp)entry.get("thruDate")).getTime(): null);
				childRowDetail.put("workEffortIdDetail", entry.get("workEffortId")); 
				childRowDetail.put("rateTypeIdDetail", entry.get("rateTypeId")); 
				childRowDetail.put("rateCurrencyUomIdDetail", entry.get("rateCurrencyUomId"));
				childRowDetail.put("periodTypeIdDetail", entry.get("periodTypeId"));
				childRowDetail.put("rateAmountDetail", entry.get("rateAmount"));
				childRowDetail.put("emplPositionTypeIdDetail", entry.get("emplPositionTypeId"));
				childRowDetail.put("rateTypeIdDetail", entry.get("rateTypeId"));
				childRowDetail.put("workEffortIdDetail", entry.get("workEffortId"));
				childRowDetail.put("isBasedOnPosType", entry.get("isBasedOnPosType"));
				rowDetails.add(childRowDetail);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyPayrollHistory(DispatchContext dctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listPartyPayrollHistory", listReturn);
		Map<String, Object> resultService = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		/*Timestamp fromDateAttr = (Timestamp)context.get("fromDate");
		Timestamp thruDateAttr = (Timestamp)context.get("thruDate");*/
		
		try {
			EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
			/*if(thruDateAttr != null){
				commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateAttr));
			}
			if(fromDateAttr != null){
				commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																															EntityOperator.OR,
																															EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDateAttr)));
			}*/
			List<GenericValue> emplPositionFulList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition("employeePartyId", partyId), null, UtilMisc.toList("fromDate"), null, false);
			
			for(GenericValue tempGv: emplPositionFulList){
				Timestamp fromDate = tempGv.getTimestamp("fromDate");
				Timestamp thruDate = tempGv.getTimestamp("thruDate");
				String emplPositionTypeId = tempGv.getString("emplPositionTypeId");
				List<EntityCondition> tempConds = FastList.newInstance();
				tempConds.add(commonConds);
				if(thruDate != null){
					tempConds.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
				}
				tempConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
				tempConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
																EntityOperator.OR,
															EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)));
				List<GenericValue> tempPartyRateAmount = delegator.findList("RateAmount", EntityCondition.makeCondition(commonConds, 
																												EntityOperator.AND, 
																												EntityCondition.makeCondition(tempConds)), 
																												null, UtilMisc.toList("fromDate"), null, false);
				if(UtilValidate.isNotEmpty(tempPartyRateAmount)){
					//if salary is set directly for employee, then get rate amount of employee 					 
					Timestamp minFromDateInRateAmount = tempPartyRateAmount.get(0).getTimestamp("fromDate");
					Timestamp maxThruDateInRateAmount = tempPartyRateAmount.get(tempPartyRateAmount.size() - 1).getTimestamp("thruDate");
					//In period time that salary is not set directly, get salary is set emplPositionTypeId
					List<Map<String, Timestamp>> listFromThruDateBaseOnEmplPosType = FastList.newInstance();
					if(minFromDateInRateAmount.after(fromDate)){
						Timestamp tmpThruDate = UtilDateTime.getDayEnd(minFromDateInRateAmount, -1L);
						Map<String, Timestamp> fromThruDateBaseOnEmplPosType = FastMap.newInstance(); 
						fromThruDateBaseOnEmplPosType.put("fromDate", fromDate);
						fromThruDateBaseOnEmplPosType.put( "thruDate", tmpThruDate);
						listFromThruDateBaseOnEmplPosType.add(fromThruDateBaseOnEmplPosType);
					}
					if(maxThruDateInRateAmount != null && (thruDate == null || maxThruDateInRateAmount.before(thruDate))){
						Timestamp tmpFromDate = UtilDateTime.getDayStart(maxThruDateInRateAmount, 1);
						Map<String, Timestamp> fromThruDateBaseOnEmplPosType = FastMap.newInstance(); 
						fromThruDateBaseOnEmplPosType.put("fromDate", tmpFromDate);
						fromThruDateBaseOnEmplPosType.put( "thruDate", thruDate);
						listFromThruDateBaseOnEmplPosType.add(fromThruDateBaseOnEmplPosType);
					}
					
					//rateAmount is set directly for employee
					for(int i = 0; i < tempPartyRateAmount.size(); i++){
						Map<String, Object> tempMap = FastMap.newInstance();
						Timestamp tempRateAmountFromDate = tempPartyRateAmount.get(i).getTimestamp("fromDate");
						Timestamp tempRateAmountThruDate = tempPartyRateAmount.get(i).getTimestamp("thruDate");
						String rateCurrencyUomId = tempPartyRateAmount.get(i).getString("rateCurrencyUomId");
						String periodTypeId = tempPartyRateAmount.get(i).getString("periodTypeId");
						BigDecimal rateAmount = tempPartyRateAmount.get(i).getBigDecimal("rateAmount");
						if(tempRateAmountFromDate.before(fromDate)){
							tempRateAmountFromDate = fromDate;
						}
						if(tempRateAmountThruDate == null || (thruDate != null && tempRateAmountThruDate.after(thruDate))){
							tempRateAmountThruDate = thruDate;
						}
						if(tempRateAmountThruDate != null && i < tempPartyRateAmount.size() - 1){
							//ex: if "tempRateAmountThruDate" is 02/04/2015 and next fromDate in "tempPartyRateAmount" is 10/04/2015 
							// => 03/04/2015 (nextFromDate) to 09/04/2015, salary is set base on emplPositionType
							Timestamp nextFromDate = UtilDateTime.getDayStart(tempRateAmountThruDate, 1);
							if(nextFromDate.before(tempPartyRateAmount.get(i + 1).getTimestamp("fromDate"))){
								Map<String, Timestamp> fromThruDateBaseOnEmplPosType = FastMap.newInstance(); 
								fromThruDateBaseOnEmplPosType.put("fromDate", nextFromDate);
								fromThruDateBaseOnEmplPosType.put( "thruDate", UtilDateTime.getDayEnd(tempPartyRateAmount.get(i + 1).getTimestamp("fromDate"), -1L));
								listFromThruDateBaseOnEmplPosType.add(fromThruDateBaseOnEmplPosType);
							}
						}
						tempMap.put("fromDate", tempRateAmountFromDate);
						tempMap.put("thruDate", tempRateAmountThruDate);
						tempMap.put("rateCurrencyUomId", rateCurrencyUomId);
						tempMap.put("periodTypeId", periodTypeId);
						tempMap.put("rateAmount", rateAmount);
						tempMap.put("emplPositionTypeId", tempPartyRateAmount.get(i).getString("emplPositionTypeId"));
						tempMap.put("rateTypeId", tempPartyRateAmount.get(i).getString("rateTypeId"));
						tempMap.put("workEffortId", tempPartyRateAmount.get(i).getString("workEffortId"));
						tempMap.put("isBasedOnPosType", "N");
						listReturn.add(tempMap);
					}
					for(Map<String, Timestamp> entry: listFromThruDateBaseOnEmplPosType){
						resultService = dispatcher.runSync("getEmplPositionTypeRateInPeriod", UtilMisc.toMap("fromDate", entry.get("fromDate"), "thruDate", entry.get("thruDate"), 
								"emplPositionTypeId", emplPositionTypeId, "partyId", partyId,
								"userLogin", userLogin));
						List<GenericValue> emplPositionTypeRate = (List<GenericValue>)resultService.get("emplPositionTypeRate");
						for(GenericValue tmpGv: emplPositionTypeRate){
							Map<String, Object> tempMap = FastMap.newInstance();
							Timestamp tempFromDate = tmpGv.getTimestamp("fromDate");
							Timestamp tempThruDate = tmpGv.getTimestamp("thruDate");
							if(tempFromDate.before(entry.get("fromDate"))){
								tempFromDate = entry.get("fromDate");
							}
							if(tempThruDate == null || (entry.get("thruDate") != null && tempThruDate.after(entry.get("thruDate")))){
								tempThruDate = entry.get("thruDate");
							}
							tempMap.put("fromDate", entry.get("fromDate"));
							tempMap.put("roleTypeGroupId", entry.get("roleTypeGroupId"));
							//tempMap.put("regionGeoId", entry.get("regionGeoId"));
							//tempMap.put("stateProvinceGeoId", entry.get("stateProvinceGeoId"));
							tempMap.put("thruDate", entry.get("thruDate"));
							tempMap.put("emplPositionTypeId", tmpGv.getString("emplPositionTypeId"));
							tempMap.put("rateAmount", tmpGv.getBigDecimal("rateAmount"));
							tempMap.put("periodTypeId", tmpGv.getString("periodTypeId"));
							tempMap.put("rateCurrencyUomId", tmpGv.getString("rateCurrencyUomId"));
							tempMap.put("isBasedOnPosType", "Y");
							listReturn.add(tempMap);
						}
					}
				}else{
					resultService = dispatcher.runSync("getEmplPositionTypeRateInPeriod", UtilMisc.toMap("fromDate", fromDate, "thruDate", thruDate, 
							"emplPositionTypeId", emplPositionTypeId, "partyId", partyId,
							"userLogin", userLogin));
					List<GenericValue> emplPositionTypeRate = (List<GenericValue>)resultService.get("emplPositionTypeRate");
					for(GenericValue tmpGv: emplPositionTypeRate){
						Map<String, Object> tempMap = FastMap.newInstance();
						Timestamp tempFromDate = tmpGv.getTimestamp("fromDate");
						Timestamp tempThruDate = tmpGv.getTimestamp("thruDate");
						if(tempFromDate.before(fromDate)){
							tempFromDate = fromDate;
						}
						if(tempThruDate == null || (thruDate != null && tempThruDate.after(thruDate))){
							tempThruDate = thruDate;
						}
						tempMap.put("fromDate", tempFromDate);
						tempMap.put("thruDate", tempThruDate);
						tempMap.put("roleTypeGroupId", tmpGv.get("roleTypeGroupId"));
						//tempMap.put("regionGeoId", tmpGv.get("regionGeoId"));
						//tempMap.put("stateProvinceGeoId", tmpGv.get("stateProvinceGeoId"));
						tempMap.put("emplPositionTypeId", tmpGv.getString("emplPositionTypeId"));
						tempMap.put("rateAmount", tmpGv.getBigDecimal("rateAmount"));
						tempMap.put("periodTypeId", tmpGv.getString("periodTypeId"));
						tempMap.put("rateCurrencyUomId", tmpGv.getString("rateCurrencyUomId"));
						tempMap.put("isBasedOnPosType", "Y");
						listReturn.add(tempMap);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplPositionTypeRateInPeriod(DispatchContext dctx, Map<String, Object> context){
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String partyId = (String)context.get("partyId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		List<GenericValue> listReturn = FastList.newInstance();
		
		try {
			List<GenericValue> emplPartyRel = PartyUtil.getOrgOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
			/*EntityCondition commonConds = EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS");
			commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND,  EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));*/
			for(GenericValue tempPartyRel: emplPartyRel){
				Timestamp tempFromDate = tempPartyRel.getTimestamp("fromDate");
				Timestamp tempThruDate = tempPartyRel.getTimestamp("thruDate");
				if(tempFromDate == null || fromDate.after(tempFromDate)){
					tempFromDate = fromDate;
				}
				if(tempThruDate == null || thruDate.before(tempThruDate)){
					tempThruDate = thruDate;
				}
				String orgId = tempPartyRel.getString("partyIdFrom");
				List<EntityCondition> addrConditions = FastList.newInstance();
				addrConditions.add(EntityCondition.makeCondition("partyId", orgId));
				addrConditions.add(PayrollEntityConditionUtils.makeDateConds(tempFromDate, tempThruDate));
				List<GenericValue> orgAddrList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, tempFromDate, tempThruDate);
				//String roleTypeGroupId = PartyUtil.getRoleTypeGroupInPeriod(delegator, orgId, tempFromDate, tempThruDate);
				Map<String, Object> roleTypeGroupMap = PartyUtil.getListRoleTypeGroupInPeriod(delegator, orgId, tempFromDate, tempThruDate);
				for(Map.Entry<String, Object> entry: roleTypeGroupMap.entrySet()){
					String roleTypeGroupId = entry.getKey(); 
					EntityCondition roleTypeGroupConds = EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId);
					List<Map<String, Timestamp>> roleTypeGroupList = (List<Map<String, Timestamp>>)entry.getValue();
					for(Map<String, Timestamp> tempMap: roleTypeGroupList){
						Timestamp fromDateRoleTypeGroup = tempMap.get("fromDate");
						Timestamp thruDateRoleTypeGroup = tempMap.get("thruDate");
						if(fromDateRoleTypeGroup == null || tempFromDate.after(fromDateRoleTypeGroup)){
							fromDateRoleTypeGroup = tempFromDate;
						}
						if(thruDateRoleTypeGroup == null || tempThruDate.before(thruDateRoleTypeGroup)){
							thruDateRoleTypeGroup = tempThruDate;
						}
						for(GenericValue tempOrgAddr: orgAddrList){
							Timestamp tempAddrFromDate = tempOrgAddr.getTimestamp("fromDate");
							Timestamp tempAddrThruDate = tempOrgAddr.getTimestamp("thruDate");
							if(tempAddrFromDate == null || fromDateRoleTypeGroup.after(tempAddrFromDate)){
								tempAddrFromDate = fromDateRoleTypeGroup;
							}
							if(tempAddrThruDate == null || thruDateRoleTypeGroup.before(tempAddrThruDate)){
								tempAddrThruDate = thruDateRoleTypeGroup;
							}
							List<EntityCondition> tempConds = FastList.newInstance();
							tempConds.add(PayrollEntityConditionUtils.makeDateConds(tempAddrFromDate, tempAddrThruDate));
							tempConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
							List<GenericValue> emplPositionTypeRate = delegator.findList("OldEmplPositionTypeRate", 
									EntityCondition.makeCondition(roleTypeGroupConds, 
											EntityOperator.AND, 
											EntityCondition.makeCondition(tempConds)), null, UtilMisc.toList("fromDate"), null, false);
							List<GenericValue> tempList = PayrollUtil.getEmplPositionTypeRate(delegator, emplPositionTypeRate, tempOrgAddr);
							for(GenericValue tempGv: tempList){
								Timestamp tempGvFromDate = tempGv.getTimestamp("fromDate");
								Timestamp tempGvThruDate = tempGv.getTimestamp("thruDate");
								if(tempGvFromDate.before(tempAddrFromDate)){
									tempGv.set("fromDate", tempAddrFromDate);
								}
								if(tempGvThruDate == null || (tempAddrThruDate != null && tempGvThruDate.after(tempAddrThruDate))){
									tempGv.set("thruDate", tempAddrThruDate);
								}
								listReturn.add(tempGv);
							}
							
						}
					}
				}
			}
			retMap.put("emplPositionTypeRate", listReturn);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	/*public static Map<String, Object> updateEmplPositionTypeContactMechRate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String fromDateStr = (String)context.get("fromDate");
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		String roleTypeGroupId = (String)context.get("roleTypeGroupId");
		String contactMechId = (String)context.get("contactMechId");
		String uomId = (String)context.get("uomId");
		String thruDateStr = (String)context.get("thruDate");
		if(uomId == null){
			uomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		}
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		try {
			GenericValue updateEtt = delegator.findOne("OldEmplPositionTypeRate", 
					UtilMisc.toMap("fromDate", fromDate, "emplPositionTypeId", emplPositionTypeId, "roleTypeGroupId", roleTypeGroupId, "contactMechId", contactMechId), false);
			if(updateEtt == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToUpdate", locale));
			}
			Map<String, Object> updateMap = FastMap.newInstance();
			updateMap.putAll(context);
			updateMap.put("rateCurrencyUomId", uomId);
			updateMap.put("rateAmount", new BigDecimal((String)context.get("rateAmount")));
			if(thruDateStr != null){
				Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
				updateMap.put("thruDate", thruDate);
			}
			updateEtt.setNonPKFields(updateMap);
			updateEtt.store();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}*/
}
