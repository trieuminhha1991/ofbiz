package com.olbius.basehr.payroll;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.olbius.basehr.payroll.worker.PayrollWorker;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.payroll.entity.EntityEmplParameters;
import com.olbius.basehr.payroll.entity.EntityParameter;

public class PayrollDataPreparation {
	public static EntityEmplParameters getEmployeeParametersCache(DispatchContext dctx, GenericValue userLogin, String orgId, String timekeepingSummaryId, 
			String strEmployeeId, Timestamp tFromDate, Timestamp tThruDate, TimeZone timeZone) throws Exception{
		// cache general parameters
		Delegator delegator = dctx.getDelegator();
		/* Huyendt test calculate new*/
		String customTimePeriodId = null;
		java.sql.Date dateFrom = new java.sql.Date(tFromDate.getTime());
		java.sql.Date dateThru = new java.sql.Date(tThruDate.getTime());

		List<EntityCondition> conditionsT = FastList.newInstance();
		conditionsT.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, dateFrom));
		conditionsT.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, dateThru));
		conditionsT.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "MONTHLY"));
		List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(conditionsT), null, null, null, false);
		if(UtilValidate.isNotEmpty(customTimePeriodList)){
			customTimePeriodId = (String) customTimePeriodList.get(0).get("customTimePeriodId");
		}
		Map<String, BigDecimal> salaryAllowanceRateMap = PayrollWorker.getSalaryAllowanceRate(delegator, customTimePeriodId, strEmployeeId);
		BigDecimal allowanceRate = salaryAllowanceRateMap.get("allowanceRate");
		//end Huyendt
		EntityExpr entityExpr1 = EntityCondition.makeCondition("actualValue",EntityJoinOperator.NOT_EQUAL, "0"); // defaultValue != 0
		EntityCondition baseSalaryConds = EntityCondition.makeCondition("code", EntityJoinOperator.EQUALS, "LUONG_CO_BAN");
		EntityCondition insuranceSalaryConds = EntityCondition.makeCondition("code", EntityJoinOperator.EQUALS, "MUC_LUONG_DONG_BH");
		EntityExpr entityExprType = EntityCondition.makeCondition(EntityCondition.makeCondition("type",EntityOperator.EQUALS, "REF"), 
																	EntityOperator.OR,
																	EntityCondition.makeCondition("parentTypeId", "REF"));
		entityExprType = EntityCondition.makeCondition(entityExprType, EntityOperator.OR, EntityCondition.makeCondition("type", "QUOTA"));
		List<GenericValue> listParameterValue = delegator.findList("PayrollParametersAndType", 
						EntityCondition.makeCondition(UtilMisc.toList(entityExpr1, entityExprType, baseSalaryConds, insuranceSalaryConds), EntityJoinOperator.OR), null, null, null, false);
		
		EntityCondition conditionDate3 = PayrollUtil.makeGTEcondition("thruDate", tFromDate);
		conditionDate3 = EntityCondition.makeCondition(conditionDate3, EntityOperator.AND, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,tThruDate));
		
		List<GenericValue> listData = null;
		EntityExpr entityExpr2 = EntityCondition.makeCondition("partyId", strEmployeeId);
		EntityCondition condition2 = EntityCondition.makeCondition(UtilMisc.toList(entityExpr2,conditionDate3 ),EntityJoinOperator.AND);
		condition2 = EntityCondition.makeCondition(condition2, EntityJoinOperator.AND, EntityCondition.makeCondition("orgId", orgId));
		listData = delegator.findList("PayrollEmplParameters", condition2 , null, null, null, false);
		/*List<GenericValue> paramEmplPosTypeContactMech = PayrollDataPreparation.getListEmplPosTypeParam(dctx, strEmployeeId, tFromDate, tThruDate, timeZone);*/
		
		EntityEmplParameters result = new EntityEmplParameters();
		List<EntityParameter> parameters = FastList.newInstance();
		
		for(GenericValue genericPam: listParameterValue){
			String strDefaultValue = (String)genericPam.get("defaultValue");
			String strType = (String)genericPam.get("type");
			String parentTypeId = genericPam.getString("parentTypeId");
			String strCode = (String)genericPam.get("code");
			String strActualValue = (String)genericPam.get("actualValue");
			if(strActualValue != null && !strActualValue.isEmpty()){
				strDefaultValue = strActualValue;
			}
			if(strType.equals("REF") || "REF".equals(parentTypeId) || "QUOTA".equals(strType) 
					|| "LUONG_CO_BAN".equals(strCode) || "MUC_LUONG_DONG_BH".equals(strCode)){
				PayrollEngine.getRefOrQuotaPayrollParameter(parameters, orgId, timekeepingSummaryId, userLogin, dctx, strEmployeeId, tFromDate, tThruDate, genericPam, salaryAllowanceRateMap, timeZone);
			}else{
				if(strType.equals("CONSTPERCENT")){
					strDefaultValue = PayrollUtil.evaluateStringExpression(strDefaultValue + "/100",false);
				} 
				
				EntityParameter parameter = new EntityParameter();
				parameter.setCode(strCode);
				parameter.setValue(strDefaultValue);
				parameters.add(parameter);
			}
		}
		
		/*=============== set parameters by position type and region and area ==================*/
		/*for(GenericValue param: paramEmplPosTypeContactMech){
			EntityParameter parameter = new EntityParameter();
			parameter.setCode(param.getString("code"));
			parameter.setFromDate(param.getTimestamp("fromDate"));
			parameter.setThruDate(param.getTimestamp("thruDate"));
			parameter.setPeriodTypeId(param.getString("periodTypeId"));
			parameter.setValue(param.getBigDecimal("rateAmount").toString());
			parameters.add(parameter);
		}*/
		
		/*============ end ==================*/
		
		for(GenericValue genericValue:listData){
			String strTmpAP = genericValue.getString("actualPercent");// maybe delete field in database, base on "type" parameters is CONST OR CONSTPERCENT to evaluate  
			GenericValue param = delegator.findOne("PayrollParameters", UtilMisc.toMap("code", genericValue.getString("code")), false);
			// check if parameter with CONSTPERCENT type
			if(param.get("type").equals("CONSTPERCENT")){
				genericValue.setString("value", PayrollUtil.evaluateStringExpression(genericValue.getString("value") + "/100"));
			}
			// put code and value to map
			String strTMPValue = null;
			if(strTmpAP == null || strTmpAP.isEmpty()){ // check if actualPercent is null 
				strTMPValue = genericValue.getString("value");
			}else if(!strTmpAP.contains(".") && strTmpAP != null && strTmpAP.length() > 3){ // if actualPercent is greater than 100%
				throw new Exception("Wrong actual percent for employee: " + strEmployeeId);
			}
			else{
				strTMPValue = genericValue.getString("actualPercent") + "/100";
			}
			if(strTMPValue.matches(".*[a-zA-Z]+.*")){
				//maybe delete
				PayrollEngine.getActualValueOfParameter(parameters, delegator, strTMPValue, strEmployeeId, genericValue.getString("code"), param.getString("periodTypeId"),tFromDate, tThruDate);
			}else{
				EntityParameter parameter = new EntityParameter();
				parameter.setCode(genericValue.getString("code"));
				parameter.setValue(strTMPValue);
				parameter.setFromDate(genericValue.getTimestamp("fromDate"));
				parameter.setThruDate(genericValue.getTimestamp("thruDate"));
				parameter.setPeriodTypeId(param.getString("periodTypeId"));
				parameter.setOrgId(orgId);
				parameters.add(parameter);
			}
		}
		result.setPartyId(strEmployeeId);
		result.setEmplParameters(parameters);
		return result;
	}
	
	/*public static List<GenericValue> getListEmplPosTypeParam(
			DispatchContext dctx, String partyId, Timestamp fromDate,
			Timestamp thruDate, TimeZone timeZone) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> retList = FastList.newInstance();
		List<GenericValue> emplPartyRel = PartyUtil.getOrgOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
		
		for(GenericValue tempGv: emplPartyRel){
			Timestamp tempFromDate = tempGv.getTimestamp("fromDate");
			Timestamp tempThruDate = tempGv.getTimestamp("thruDate");
			if(tempFromDate == null || fromDate.after(tempFromDate)){
				tempFromDate = fromDate;
			}
			if(tempThruDate == null || thruDate.before(tempThruDate)){
				tempThruDate = thruDate;
			}
			String orgId = tempGv.getString("partyIdFrom");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", orgId));
			conditions.add(PayrollEntityConditionUtils.makeDateConds(tempFromDate, tempThruDate));
			
			List<GenericValue> orgAddrList = PartyUtil.getPostalAddressOfOrg(delegator, orgId, tempFromDate, tempThruDate);
			String roleTypeGroupId = PartyUtil.getRoleTypeGroupInPeriod(delegator, orgId, tempFromDate, tempThruDate);
			EntityCondition roleTypeGroupConds = EntityCondition.makeCondition("roleTypeGroupId", roleTypeGroupId);
			
			for(GenericValue tempOrgAddr: orgAddrList){
				Timestamp tempAddrFromDate = tempOrgAddr.getTimestamp("fromDate");
				Timestamp tempAddrThruDate = tempOrgAddr.getTimestamp("thruDate");
				if(tempAddrFromDate == null || tempFromDate.after(tempAddrFromDate)){
					tempAddrFromDate = tempFromDate;
				}
				if(tempAddrThruDate == null || tempThruDate.before(tempAddrThruDate)){
					tempAddrThruDate = tempThruDate;
				}
				
				List<GenericValue> emplPosFulInPeriod = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, tempAddrFromDate, tempAddrThruDate);
				for(GenericValue tempPosTypeFul: emplPosFulInPeriod){
					List<EntityCondition> tempConds = FastList.newInstance();
					String emplPositionTypeId = tempPosTypeFul.getString("emplPositionTypeId");
					Timestamp tempPosFulFromDate = tempPosTypeFul.getTimestamp("fromDate");
					Timestamp tempPosFulThruDate = tempPosTypeFul.getTimestamp("thruDate");
					if(tempPosFulFromDate == null || tempAddrFromDate.after(tempPosFulFromDate)){
						tempPosFulFromDate = tempAddrFromDate;
					}
					if(tempPosFulThruDate == null || tempAddrThruDate.before(tempPosFulThruDate)){
						tempPosFulThruDate = tempAddrThruDate;
					}
					tempConds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
					tempConds.add(roleTypeGroupConds);
					tempConds.add(PayrollEntityConditionUtils.makeDateConds(tempPosFulFromDate, tempPosFulThruDate));
					List<GenericValue> pyrllParamPosType = delegator.findList("PayrollParamPositionTypeAndParameters", EntityCondition.makeCondition(tempConds), null, UtilMisc.toList("fromDate"), null, false);				
					retList.addAll(PayrollUtil.getParamPosTypeGeoAppl(delegator, pyrllParamPosType, tempOrgAddr));    
				}
			}
		}
		return retList;
	}*/

	public static String getParameterPeriod(Delegator delegator, String partyId, String parameter, Timestamp fromDate) throws GenericEntityException{
		GenericValue payrollParameter = null;
		payrollParameter = delegator.findOne("PayrollEmplParameters", false, UtilMisc.toMap("partyId", partyId, "code", parameter, "fromDate", fromDate));
		//Check if party haven't got this parameter
		if(payrollParameter == null){
			payrollParameter = delegator.findOne("PayrollParameters", false, UtilMisc.toMap("code", parameter));
		}
		return payrollParameter.getString("periodTypeId");
	}
}
