package com.olbius.basehr.insurance.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.importExport.ImportExportWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.EntityConditionUtils;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PersonHelper;

public class InsuranceHelper {
	public static Double getEmplRateContribution(Delegator delegator) throws GenericEntityException{
		List<GenericValue> allInsuranceType = delegator.findByAnd("InsuranceType", null, null, false);
		List<String> insuranceTypeList = EntityUtil.getFieldListFromEntityList(allInsuranceType, "insuranceTypeId", true);
		return getEmplRateContribution(delegator, insuranceTypeList);
	}
	
	public static Double getEmplRateContribution(Delegator delegator, List<String> insuranceTypeList) throws GenericEntityException{
		if(insuranceTypeList == null){
			return null;
		}
		Double retVal = 0d;
		for(String insuranceTypeId: insuranceTypeList){
			GenericValue insuranceType = delegator.findOne("InsuranceType", UtilMisc.toMap("insuranceTypeId", insuranceTypeId), false);
			Double employeeRate = insuranceType.getDouble("employeeRate");
			retVal += employeeRate;
		}
		return retVal;
	}


	//maybe delete
	public static void updatePartyParticipateInsuranceAfterCreate(DispatchContext dctx, Map<String, Object> context, GenericValue newEntity) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		String insuranceTypeId = (String)context.get("insuranceTypeId");
		String partyId = (String)context.get("partyId");
		EntityCondition commonConds = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId),
																	EntityJoinOperator.AND,
																	EntityCondition.makeCondition("insuranceTypeId", insuranceTypeId));
		EntityCondition expireEnttConds = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, fromDate),
																		EntityJoinOperator.AND,
																		EntityCondition.makeCondition("thruDate", null));
		//list contain records must update thruDate
		List<GenericValue> partyParticipateInsExpire = delegator.findList("PartyParticipateInsurance", 
				EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, expireEnttConds), null, UtilMisc.toList("fromDate"), null, false);
		Timestamp thruDate = UtilDateTime.getDayEnd(fromDate, -1L);
		for(GenericValue tempGv: partyParticipateInsExpire){
			tempGv.set("thruDate", thruDate);
			tempGv.store();
		}
		//
		List<GenericValue> ettFromDateGreatNewEtt = delegator.findList("PartyParticipateInsurance", 
				EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, 
						EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN, fromDate)), null, UtilMisc.toList("fromDate"), null, false);
		if(UtilValidate.isNotEmpty(ettFromDateGreatNewEtt)){
			Timestamp tempThruDate = ettFromDateGreatNewEtt.get(0).getTimestamp("fromDate");
			tempThruDate = UtilDateTime.getDayEnd(tempThruDate, -1L);
			newEntity.set("thruDate", tempThruDate);
			newEntity.store();
		}
	}
	
	public static BigDecimal getTotalInsuranceSalary(GenericValue partyInsuranceSalary){
		BigDecimal retVal = BigDecimal.ZERO;
		BigDecimal insuranceSalary = partyInsuranceSalary.getBigDecimal("amount");
		BigDecimal allowanceOther = partyInsuranceSalary.getBigDecimal("allowanceOther");
		BigDecimal allowancePosition = partyInsuranceSalary.getBigDecimal("allowancePosition");
		Double allowanceSeniority = partyInsuranceSalary.getDouble("allowanceSeniority");
		Double allowanceSeniorityExces = partyInsuranceSalary.getDouble("allowanceSeniorityExces");
		if(insuranceSalary == null){
			insuranceSalary = BigDecimal.ZERO;
		}
		retVal = retVal.add(insuranceSalary);
		if(allowanceOther != null){
			retVal = retVal.add(allowanceOther);
		}
		if(allowancePosition != null){
			retVal = retVal.add(allowancePosition);
		}
		if(allowanceSeniority != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniority));
			retVal = retVal.add(tmp);
		}
		if(allowanceSeniorityExces != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniorityExces));
			retVal = retVal.add(tmp);
		}
		return retVal;
	}
	
	public static BigDecimal getInsuranceSalaryByPos(GenericValue emplPosInsuranceSalary){
		BigDecimal retVal = BigDecimal.ZERO;
		BigDecimal insuranceSalary = emplPosInsuranceSalary.getBigDecimal("insuranceSalary");
		BigDecimal allowanceOther = emplPosInsuranceSalary.getBigDecimal("allowanceOther");
		BigDecimal allowancePosition = emplPosInsuranceSalary.getBigDecimal("allowancePosition");
		Double allowanceSeniority = emplPosInsuranceSalary.getDouble("allowanceSeniority");
		Double allowanceSeniorityExces = emplPosInsuranceSalary.getDouble("allowanceSeniorityExces");
		if(insuranceSalary == null){
			insuranceSalary = BigDecimal.ZERO;
		}
		retVal = retVal.add(insuranceSalary);
		if(allowanceOther != null){
			retVal = retVal.add(allowanceOther);
		}
		if(allowancePosition != null){
			retVal = retVal.add(allowancePosition);
		}
		if(allowanceSeniority != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniority));
			retVal = retVal.add(tmp);
		}
		if(allowanceSeniorityExces != null){
			BigDecimal tmp = insuranceSalary.multiply(BigDecimal.valueOf(allowanceSeniorityExces));
			retVal = retVal.add(tmp);
		}
		return retVal;
	}
	
	/*public static String getInsuranceTemplatePath(Delegator delegator, String contentTypeId) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityUtil.getFilterByDateExpr());
		conds.add(EntityCondition.makeCondition("insuranceContentTypeId", contentTypeId));
		List<GenericValue> insuranceContentList = delegator.findList("InsuranceContent", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isNotEmpty(insuranceContentList)){
			String contentId = insuranceContentList.get(0).getString("contentId");
			GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
			if(content != null){
				String dataResourceId = content.getString("dataResourceId");
				GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
				return dataResource.getString("objectInfo");
			}
		}
		return null;
	}*/

	public static String getParentRelation(Delegator delegator, String partyId) throws GenericEntityException{
		String fatherPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_FATHER");
		if(fatherPartyId != null){
			return fatherPartyId;
		}
		String motherPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_MOTHER");
		if(motherPartyId != null){
			return motherPartyId;
		}
		String guardianPartyId = PersonHelper.getPersonFamilyRelationship(delegator, partyId, "FAMILY_GUARDIAN");
		if(guardianPartyId != null){
			return guardianPartyId;
		}
		return null;	
	}

	public static void settingEmplInsuranceSalaryByPosType( LocalDispatcher dispatcher, Delegator delegator, String partyId, Timestamp fromDate, 
			Timestamp thruDate, String overrideDataWay, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
		List<Timestamp> listTimestamp = FastList.newInstance();
		for(GenericValue emplPos: emplPosList){
			Timestamp tempFromDate = emplPos.getTimestamp("fromDate");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			Timestamp tempThruDate = emplPos.getTimestamp("thruDate");
			if(thruDate != null && (tempThruDate == null || tempFromDate.after(thruDate))){
				tempThruDate = thruDate;
			}
			String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
			EntityCondition dateConds = EntityConditionUtils.makeDateConds(tempFromDate, tempThruDate);
			List<GenericValue> emplPosTypeInsuranceSalaryList = delegator.findList("EmplPosTypeInsuranceSalary", EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND,
					EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId)), null, UtilMisc.toList("fromDate"), null, false);
			for(GenericValue tempGv: emplPosTypeInsuranceSalaryList){
				Timestamp emplPosFromDate = tempGv.getTimestamp("fromDate");
				Timestamp emplPosThruDate = tempGv.getTimestamp("thruDate");
				if(emplPosFromDate.before(tempFromDate)){
					emplPosFromDate = tempFromDate;
				}
				if(tempThruDate != null && (emplPosThruDate == null || emplPosThruDate.after(tempThruDate))){
					emplPosThruDate = tempThruDate;
				}
				if(!listTimestamp.contains(emplPosFromDate)){
					listTimestamp.add(emplPosFromDate);
				}
				
				if(!listTimestamp.contains(emplPosThruDate)){
					listTimestamp.add(emplPosThruDate);
				}
			}
		}
		List<EntityCondition> listPartyInsAmountCond = FastList.newInstance();
		listPartyInsAmountCond.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		listPartyInsAmountCond.add(EntityCondition.makeCondition("partyId", partyId));
		List<GenericValue> listPartyInsAmount = delegator.findList("PartyInsuranceSalary", EntityCondition.makeCondition(listPartyInsAmountCond), null, UtilMisc.toList("fromDate"), null, false);
		for(GenericValue temppartyInsAmount: listPartyInsAmount){
			Timestamp tempFromDate = temppartyInsAmount.getTimestamp("fromDate");
			Timestamp tempThruDate = temppartyInsAmount.getTimestamp("thruDate");
			if(tempFromDate.before(fromDate)){
				tempFromDate = fromDate;
			}
			if(thruDate != null && (tempThruDate == null || tempThruDate.after(thruDate))){
				tempThruDate = thruDate;
			}
			if(!listTimestamp.contains(tempFromDate)){
				listTimestamp.add(tempFromDate);
			}
			
			if(!listTimestamp.contains(tempThruDate)){
				listTimestamp.add(tempThruDate);
			}
		}
		DateUtil.sortList(listTimestamp);
		List<Map<String, Timestamp>> listFromThruDate = DateUtil.buildMapPeriodTime(listTimestamp);
		createEmplInsuranceSalaryByPosType(delegator, dispatcher, userLogin, listPartyInsAmount, emplPosList, partyId, listFromThruDate, overrideDataWay);
	}

	private static void createEmplInsuranceSalaryByPosType(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin,
			List<GenericValue> listPartyInsAmount, List<GenericValue> emplPosList, String partyId, 
			List<Map<String, Timestamp>> listFromThruDate, String overrideDataWay) throws GenericEntityException, GenericServiceException {
		EntityCondition commonConds = EntityCondition.makeCondition("partyId", partyId);
		for(Map<String, Timestamp> tempMap: listFromThruDate){
			Timestamp fromDate = tempMap.get("fromDate");
			Timestamp thruDate = tempMap.get("thruDate");
			List<EntityCondition> dateCondList = FastList.newInstance();
			dateCondList.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
			if(thruDate != null){
				dateCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
						EntityJoinOperator.OR,
						EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
			}else{
				dateCondList.add(EntityCondition.makeCondition("thruDate", null));
			}
			EntityCondition dateConds = EntityCondition.makeCondition(dateCondList);
			List<GenericValue> partyInsAmounts = EntityUtil.filterByCondition(listPartyInsAmount, EntityCondition.makeCondition(commonConds, EntityJoinOperator.AND, dateConds));
			//check whether salary is set for employee
			if(UtilValidate.isEmpty(partyInsAmounts)){
				List<GenericValue> emplPos = EntityUtil.filterByCondition(emplPosList, dateConds);
				List<String> emplPositionTypeList = EntityUtil.getFieldListFromEntityList(emplPos, "emplPositionTypeId", true);
				EntityCondition tempConds = EntityCondition.makeCondition(dateConds, EntityJoinOperator.AND, EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.IN, emplPositionTypeList));
				List<GenericValue> emplPosTypeInsuranceSalary = delegator.findList("EmplPosTypeInsuranceSalary", tempConds, null, null, null, false);
				if(UtilValidate.isNotEmpty(emplPosTypeInsuranceSalary)){
					BigDecimal amount = BigDecimal.ZERO;
					GenericValue posTypeSal = emplPosTypeInsuranceSalary.get(0);
					for(GenericValue tempEmplPosTypeInsuranceSalary: emplPosTypeInsuranceSalary){
						BigDecimal tempAmount = getInsuranceSalaryByPos(tempEmplPosTypeInsuranceSalary);
						if("getValueLowest".equals(overrideDataWay)){
							if(tempAmount.compareTo(amount) < 0){
								amount = tempAmount;
								posTypeSal = tempEmplPosTypeInsuranceSalary;
							}
						}else{
							if(tempAmount.compareTo(amount) > 0){
								amount = tempAmount;
								posTypeSal = tempEmplPosTypeInsuranceSalary;
							}
						}
					}
					dispatcher.runSync("createPartyInsuranceSalary", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, 
																				"periodTypeId", posTypeSal.get("periodTypeId"),
																				"amount", amount,
																				"fromDate", fromDate,
																				"thruDate", thruDate));
				}
			}
		}
	}

	public static BigDecimal getPartyInsuranceSocialSalary(Delegator delegator, String partyInsSalId) throws GenericEntityException {
		GenericValue partyInsuranceSalary = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyInsSalId", partyInsSalId), false);
		BigDecimal retVal = BigDecimal.ZERO;
		if(partyInsuranceSalary.getBigDecimal("amount") != null){
			retVal = retVal.add(partyInsuranceSalary.getBigDecimal("amount"));
		}
		if(partyInsuranceSalary.getBigDecimal("allowanceSeniority") != null){
			retVal = retVal.add(partyInsuranceSalary.getBigDecimal("allowanceSeniority"));
		}
		if(partyInsuranceSalary.getBigDecimal("allowanceSeniorityExces") != null){
			retVal = retVal.add(partyInsuranceSalary.getBigDecimal("allowanceSeniorityExces"));
		}
		if(partyInsuranceSalary.getBigDecimal("allowancePosition") != null){
			retVal = retVal.add(partyInsuranceSalary.getBigDecimal("allowancePosition"));
		}
		if(partyInsuranceSalary.getBigDecimal("allowanceOther") != null){
			retVal = retVal.add(partyInsuranceSalary.getBigDecimal("allowanceOther"));
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	public static BigDecimal getAvgInsuranceSalaryInMonths(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String partyId, 
			Timestamp monthStartCalc, Double numberMonthBeforeLeave, TimeZone timeZone, Locale locale) throws GenericServiceException, GenericEntityException {
		Map<String, Object> resultService = null;
		Timestamp monthEnd = UtilDateTime.getMonthEnd(monthStartCalc, timeZone, locale);
		resultService = dispatcher.runSync("getPartyInsuranceSalary", UtilMisc.toMap("partyId", partyId, "fromDate", monthStartCalc, "thruDate", monthEnd, "userLogin", userLogin));
		BigDecimal defaultSalary = BigDecimal.ZERO;
		BigDecimal retVal = BigDecimal.ZERO;
		if(ServiceUtil.isSuccess(resultService)){
			List<String> partyInsSalIdList = (List<String>)resultService.get("partyInsSalId");
			if(UtilValidate.isNotEmpty(partyInsSalIdList)){
				for(String partyInsSalId: partyInsSalIdList){
					GenericValue partyInsuranceSalary = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyInsSalId", partyInsSalId), false);
					BigDecimal tempTotal = getTotalInsuranceSalary(partyInsuranceSalary);
					defaultSalary = defaultSalary.add(tempTotal);
				}
				defaultSalary = defaultSalary.divide(new BigDecimal(partyInsSalIdList.size()));
			}
			for(int i = 1; i <= numberMonthBeforeLeave; i++){
				Timestamp tempMonthStart = UtilDateTime.getMonthStart(monthStartCalc, 0, -i);
				Timestamp tempMonthEnd = UtilDateTime.getMonthEnd(tempMonthStart, timeZone, locale);
				resultService = dispatcher.runSync("getPartyInsuranceSalary", 
						UtilMisc.toMap("partyId", partyId, "fromDate", tempMonthStart, "thruDate", tempMonthEnd, "userLogin", userLogin));
				List<String> tempPartyInsSalIdList = (List<String>)resultService.get("partyInsSalId");
				if(UtilValidate.isNotEmpty(tempPartyInsSalIdList)){
					BigDecimal total = BigDecimal.ZERO;
					for(String partyInsSalId: tempPartyInsSalIdList){
						GenericValue partyInsuranceSalary = delegator.findOne("PartyInsuranceSalary", UtilMisc.toMap("partyInsSalId", partyInsSalId), false);
						BigDecimal tempTotal = getTotalInsuranceSalary(partyInsuranceSalary);
						total = total.add(tempTotal);
					}
					total = total.divide(new BigDecimal(partyInsSalIdList.size()));
					retVal = retVal.add(total);
				}else{
					retVal = retVal.add(defaultSalary);
				}
			}
			if(numberMonthBeforeLeave != null && numberMonthBeforeLeave > 0){
				retVal = retVal.divide(new BigDecimal(numberMonthBeforeLeave));
			}
		}else{
			return BigDecimal.ZERO;
		}
		return retVal;
	}

	public static Integer getTotalMonthParticipateIns(Delegator delegator,
			String partyId, Timestamp timestamp) throws GenericEntityException {
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		Date dateParticipateIns = person.getDate("dateParticipateIns");
		if(dateParticipateIns == null){
			return 0;
		}
		Date date = new Date(timestamp.getTime());
		Integer totalMonth = DateUtil.getMonthBetweenTwoDate(dateParticipateIns, date);
		return totalMonth;
	}
	
	public static Float getAccumulatedLeave(Delegator delegator, String benefitTypeId, String partyId, Timestamp timestamp) throws GenericEntityException {
		Float retVal = 0f;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("statusId", "LEAVE_APPROVED"));		
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, timestamp));
		conds.add(EntityCondition.makeCondition("benefitTypeId", benefitTypeId));
		List<GenericValue> emplLeaveList = delegator.findList("InsPayDeclAndPartyAndLeave", EntityCondition.makeCondition(conds), null, null, null, false);
		for(GenericValue tempGv: emplLeaveList){
			BigDecimal totalDayLeavePaid = tempGv.getBigDecimal("totalDayLeavePaid");
			BigDecimal totalDayLeaveExceedPaid = tempGv.getBigDecimal("totalDayLeaveExceedPaid");
			retVal += totalDayLeavePaid.floatValue(); 
			if(totalDayLeaveExceedPaid != null){
				retVal += totalDayLeaveExceedPaid.floatValue();
			}
		}
		return retVal;
	}
	
	public static BigDecimal getInsuranceAllowanceAmount(BigDecimal insuranceSalary, BigDecimal totalDayLeave, BigDecimal rateBenefit) {
		//26 la quy dinh so ngay lam viec trong thang
		BigDecimal retVal = insuranceSalary.multiply(totalDayLeave).divide(new BigDecimal(26), 0, RoundingMode.HALF_UP);
		retVal = retVal.multiply(rateBenefit).divide(new BigDecimal(100));
		return retVal;
	}

	public static BigDecimal getCommonSalaryMinimum(Delegator delegator,
			Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		Date fDate = new Date(fromDate.getTime());
		Date tDate = new Date(thruDate.getTime());
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, tDate));
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
				EntityJoinOperator.OR,
				EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fDate)));
		List<GenericValue> listCommonSalaryMin = delegator.findList("CommonSalaryMinimum", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isEmpty(listCommonSalaryMin)){
			return BigDecimal.ZERO;
		}
		
		return listCommonSalaryMin.get(0).getBigDecimal("amount");
	}
	
	public static String getInsuranceBenefitCondsDesc(Delegator delegator,
			String inputParamEnumId, String operatorEnumId, BigDecimal condValue) throws GenericEntityException {
		GenericValue inputParamEnum = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", inputParamEnumId), false);
		GenericValue operatorEnum = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", operatorEnumId), false);
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(inputParamEnum.getString("description"));
		strBuff.append(" ");
		strBuff.append(operatorEnum.getString("description"));
		strBuff.append(" ");
		strBuff.append(condValue.floatValue());
		return strBuff.toString();
	}

	public static String getInsuranceBenefitActionDesc(Delegator delegator,
			String benefitTypeActionEnumId, BigDecimal amount,
			BigDecimal quantity, String uomId, Locale locale) throws GenericEntityException {
		GenericValue benefitTypeActionEnum = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", benefitTypeActionEnumId), false);
		String enumTypeId = benefitTypeActionEnum.getString("enumTypeId");
		GenericValue enumType = delegator.findOne("EnumerationType", UtilMisc.toMap("enumTypeId", enumTypeId), false);
		GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
		String enumTypeDesc = enumType.getString("description");
		String uomDesc = (String)uom.get("abbreviation", locale);
		String benefitTypeActionEnumDes = benefitTypeActionEnum.getString("description");
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(enumTypeDesc);
		strBuff.append(" (");
		strBuff.append(uomDesc);
		strBuff.append("): ");
		if("INS_BENEFIT_ACT_SAL".equals(enumTypeId)){
			if(benefitTypeActionEnumDes.indexOf("X") > -1){
				benefitTypeActionEnumDes = benefitTypeActionEnumDes.replace("X", String.valueOf(quantity.floatValue()));
			}
			if(benefitTypeActionEnumDes.indexOf("Y") > -1){
				benefitTypeActionEnumDes = benefitTypeActionEnumDes.replace("Y", String.valueOf(amount.floatValue()));
			}
			strBuff.append(benefitTypeActionEnumDes);
		}else if("INS_BENEFIT_ACT_TIME".equals(enumTypeId)){
			strBuff.append(benefitTypeActionEnum.getString("description"));
			strBuff.append(" - ");
			strBuff.append(quantity.floatValue());
		}
		return strBuff.toString();
	}
	
	public static Workbook exportD02TSTemplate(Delegator delegator, String customTimePeriodId, Long sequenceNum, 
			Locale locale, HttpServletResponse response) throws GenericEntityException{
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("D02-TS");
		
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		sheet.setAutobreaks(true);
		sheet.setColumnWidth(0, 15*100);
		sheet.setColumnWidth(1, 20*220);
		sheet.setColumnWidth(2, 20*300);
		sheet.setColumnWidth(3, 20*600);
		sheet.setColumnWidth(4, 20*150);
		sheet.setColumnWidth(5, 20*100);
		sheet.setColumnWidth(6, 20*100);
		sheet.setColumnWidth(7, 20*110);
		sheet.setColumnWidth(8, 20*100);
		sheet.setColumnWidth(9, 20*190);
		sheet.setColumnWidth(10, 20*130);
		sheet.setColumnWidth(11, 20*100);
		
		Font boldCenterBorderFont10 = wb.createFont();
		boldCenterBorderFont10.setFontHeightInPoints((short) 10);
		boldCenterBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		CellStyle cell_bold_no_center_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, null, null); 
		
		CellStyle cell_bold_left_no_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, null, null); 
		
		CellStyle cell_bold_left_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		Font boldLeftFontItalic = wb.createFont();
		boldLeftFontItalic.setFontHeightInPoints((short) 10);
		boldLeftFontItalic.setItalic(true);
		CellStyle cell_italic_left_border = ImportExportWorker.getCellStyle(wb, boldLeftFontItalic, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		CellStyle cell_italic_center_border = ImportExportWorker.getCellStyle(wb, boldLeftFontItalic, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		Font boldCenterBorderFont12 = wb.createFont();
		boldCenterBorderFont12.setFontHeightInPoints((short) 12);
		boldCenterBorderFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_no_border_12 = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont12, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, null, null); 
		
		Font fontBorderNormal = wb.createFont();
		fontBorderNormal.setFontHeightInPoints((short) 10);
		CellStyle styleNormal = ImportExportWorker.getCellStyle(wb, fontBorderNormal, CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex());
		int rownum = 0;
		//CreationHelper helper = wb.getCreationHelper();
		Row row1 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		Cell orgNameTitle = row1.createCell(0);
		orgNameTitle.setCellValue(UtilProperties.getMessage("BaseHRDirectoryUiLabels", "OrgUnitName", locale));
		orgNameTitle.setCellStyle(cell_bold_left_no_border);
		
		rownum++;
		Row row2 = sheet.createRow(rownum);
		row2.setHeight((short)600);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
		Cell identifyTitleCell = row2.createCell(0);
		identifyTitleCell.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "NbrAndIdentify", locale));
		identifyTitleCell.setCellStyle(cell_bold_left_no_border);
		
		Cell mainCellTitle = row2.createCell(2);
		mainCellTitle.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceD02TSTitle", locale));
		mainCellTitle.setCellStyle(cell_bold_center_no_border_12);
		rownum++;
		Row row3 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
		Cell addressCell = row3.createCell(0);
		addressCell.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "CommonAddress", locale));
		addressCell.setCellStyle(cell_bold_left_no_border);
		Cell dateMonthYear = row3.createCell(2);
		dateMonthYear.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "D02TS_MonthYear", locale));
		dateMonthYear.setCellStyle(cell_bold_no_center_border);
		
		sheet.createRow(4);
		sheet.createRow(5);
		
		//header title, row
		rownum = 6;
		Map<Integer, String> titleFirstMegreRow = FastMap.newInstance();
		titleFirstMegreRow.put(0, UtilProperties.getMessage("BaseHRUiLabels", "HRSequenceNbr", locale));
		titleFirstMegreRow.put(1, UtilProperties.getMessage("BaseHRUiLabels", "HRFullName", locale));
		titleFirstMegreRow.put(2, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "SocialInsuranceNbrIdentify", locale));
		titleFirstMegreRow.put(3, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceEmplPositionType", locale));
		titleFirstMegreRow.put(4, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSalaryShort", locale));
		titleFirstMegreRow.put(9, UtilProperties.getMessage("BaseHRUiLabels", "FromMonthYear", locale));
		titleFirstMegreRow.put(10, UtilProperties.getMessage("BaseHRUiLabels", "HRNotes", locale));
		Row row6 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 4, 8));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 9, 9));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 10, 10));
		for(Map.Entry<Integer, String> entry: titleFirstMegreRow.entrySet()){
			int cellNbr = entry.getKey();
			String title = entry.getValue();
			Cell tempCell = row6.createCell(cellNbr);
			tempCell.setCellValue(title);
			tempCell.setCellStyle(cell_bold_center_border);
		}
		
		rownum = 7;
		Row row7 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 4, 4));
		Cell salaryCell = row7.createCell(4);
		Cell allowanceCell = row7.createCell(5);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 5, 8));
		salaryCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSalaryGroup", locale));
		allowanceCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAllowance", locale));
		salaryCell.setCellStyle(cell_bold_center_border);
		allowanceCell.setCellStyle(cell_bold_center_border);
		
		rownum = 8;
		Row row8 = sheet.createRow(rownum);
		row8.setHeight((short)600);
		Cell allowancePos = row8.createCell(5);
		Cell allowanceSeniorityExces = row8.createCell(6);
		Cell allowanceSeniority = row8.createCell(7);
		Cell allowanceOther = row8.createCell(8);
		allowancePos.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAllowancePosition", locale));
		allowancePos.setCellStyle(cell_bold_center_border);
		allowanceSeniorityExces.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAllowanceSeniorityExces", locale));
		allowanceSeniorityExces.setCellStyle(cell_bold_center_border);
		allowanceSeniority.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAllowanceSeniority", locale));
		allowanceSeniority.setCellStyle(cell_bold_center_border);
		allowanceOther.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceOtherAllowance", locale));
		allowanceOther.setCellStyle(cell_bold_center_border);
		
		rownum = 9;
		Row row9 = sheet.createRow(rownum);
		Cell cell9_0 = row9.createCell(0);
		Cell cell9_1 = row9.createCell(1);
		Cell cell9_2 = row9.createCell(2);
		Cell cell9_3 = row9.createCell(3);
		Cell cell9_4 = row9.createCell(4);
		Cell cell9_5 = row9.createCell(5);
		Cell cell9_6 = row9.createCell(6);
		Cell cell9_7 = row9.createCell(7);
		Cell cell9_8 = row9.createCell(8);
		Cell cell9_9 = row9.createCell(9);
		Cell cell9_10 = row9.createCell(10);
		cell9_0.setCellValue("A");
		cell9_1.setCellValue("B");
		cell9_2.setCellValue("1");
		cell9_3.setCellValue("2");
		cell9_4.setCellValue("3");
		cell9_5.setCellValue("4");
		cell9_6.setCellValue("5");
		cell9_7.setCellValue("6");
		cell9_8.setCellValue("7");
		cell9_9.setCellValue("8");
		cell9_10.setCellValue("9");
		cell9_0.setCellStyle(cell_bold_center_border);
		cell9_1.setCellStyle(cell_bold_center_border);
		cell9_2.setCellStyle(cell_bold_center_border);
		cell9_3.setCellStyle(cell_bold_center_border);
		cell9_4.setCellStyle(cell_bold_center_border);
		cell9_5.setCellStyle(cell_bold_center_border);
		cell9_6.setCellStyle(cell_bold_center_border);
		cell9_7.setCellStyle(cell_bold_center_border);
		cell9_8.setCellStyle(cell_bold_center_border);
		cell9_9.setCellStyle(cell_bold_center_border);
		cell9_10.setCellStyle(cell_bold_center_border);
		
		//insert data 
		List<GenericValue> insuranceDeclarationList = delegator.findByAnd("PartyAndInsuranceDeclaration", 
				UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "sequenceNum", sequenceNum), null, false);
		List<GenericValue> insDeclRisingEmpl = EntityUtil.filterByCondition(insuranceDeclarationList, 
				EntityCondition.makeCondition("insuranceParticipateTypeId", EntityJoinOperator.IN, UtilMisc.toList("ON_PARTICIPATE", "MOVE_PARTICIPATE", "NEW_PARTICIPATE")));
		rownum = 10;
		Row row10 = sheet.createRow(rownum);
		Cell cell10_1 = row10.createCell(0);
		cell10_1.setCellStyle(cell_bold_center_border);
		cell10_1.setCellValue("I");
		Cell cell10_2 = row10.createCell(1);
		cell10_2.setCellStyle(cell_bold_left_border);
		cell10_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceDeclarationRising", locale));
		ImportExportWorker.createEmptyCell(row10, 2, 10, styleNormal);
		
		rownum = 11;
		Row row11 = sheet.createRow(rownum);
		Cell cell11_1 = row11.createCell(0);
		cell11_1.setCellStyle(cell_italic_center_border);
		cell11_1.setCellValue("I.1");
		Cell cell11_2 = row11.createCell(1);
		cell11_2.setCellStyle(cell_italic_left_border);
		cell11_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceLabor", locale));
		ImportExportWorker.createEmptyCell(row11, 2, 10, styleNormal);
		
		if(UtilValidate.isNotEmpty(insDeclRisingEmpl)){
			rownum = fillListDataPartyToD02TSExcel(wb, delegator, sheet, rownum, styleNormal, insDeclRisingEmpl);
		}
		
		rownum++;
		Row emptyRow1 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow1, 0, 10, styleNormal);
		
		rownum++;
		Row rowDC = sheet.createRow(rownum);
		Cell cellRowDC_1 = rowDC.createCell(0);
		cellRowDC_1.setCellStyle(cell_italic_center_border);
		cellRowDC_1.setCellValue("I.2");
		Cell cellRowDC_2 = rowDC.createCell(1);
		cellRowDC_2.setCellStyle(cell_italic_left_border);
		cellRowDC_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSalaryShort", locale));
		ImportExportWorker.createEmptyCell(rowDC, 2, 10, styleNormal);
		
		List<GenericValue> insDeclDCList = EntityUtil.filterByCondition(insuranceDeclarationList, 
				EntityCondition.makeCondition("insuranceParticipateTypeId", EntityJoinOperator.IN, UtilMisc.toList("DC_PARTICIPATE")));
		
		if(UtilValidate.isNotEmpty(insDeclDCList)){
			rownum = fillListDataPartyToD02TSExcel(wb, delegator, sheet, rownum, styleNormal, insDeclDCList);
		}
		rownum++;
		Row emptyRow = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow, 0, 10, styleNormal);
		
		rownum++;
		Row rowInsHealth = sheet.createRow(rownum);
		Cell cellRowInsHealth_1 = rowInsHealth.createCell(0);
		cellRowInsHealth_1.setCellStyle(cell_italic_center_border);
		cellRowInsHealth_1.setCellValue("I.3");
		Cell cellRowInsHealth_2 = rowInsHealth.createCell(1);
		cellRowInsHealth_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceHealth", locale));
		cellRowInsHealth_2.setCellStyle(cell_italic_left_border);
		ImportExportWorker.createEmptyCell(rowInsHealth, 2, 10, styleNormal);
		
		rownum++;
		Row emptyRow2 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow2, 0, 10, styleNormal);
		
		rownum++;
		Row rowInsUnEmpl = sheet.createRow(rownum);
		Cell cellRowInsUnEmpl = rowInsUnEmpl.createCell(0);
		cellRowInsUnEmpl.setCellStyle(cell_italic_center_border);
		cellRowInsUnEmpl.setCellValue("I.4");
		Cell cellRowInsUnEmpl_2 = rowInsUnEmpl.createCell(1);
		cellRowInsUnEmpl_2.setCellStyle(cell_italic_left_border);
		cellRowInsUnEmpl_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceUnemployment", locale));
		ImportExportWorker.createEmptyCell(rowInsUnEmpl, 2, 10, styleNormal);
		rownum++;
		Row emptyRow3 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow3, 0, 10, styleNormal);
		
		rownum++;
		Row rowParticipateTotal = sheet.createRow(rownum);
		rowParticipateTotal.createCell(0).setCellStyle(styleNormal);
		Cell cellParticipateTotal = rowParticipateTotal.createCell(1);
		cellParticipateTotal.setCellStyle(cell_bold_left_border);
		cellParticipateTotal.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceTotalRising", locale));
		ImportExportWorker.createEmptyCell(rowParticipateTotal, 2, 10, styleNormal);
		
		rownum++;
		Row insDeclSus = sheet.createRow(rownum);
		Cell cellInsDeclSus_1 = insDeclSus.createCell(0);
		cellInsDeclSus_1.setCellStyle(cell_bold_center_border);
		cellInsDeclSus_1.setCellValue("II");
		Cell cellInsDeclSus_2 = insDeclSus.createCell(1);
		cellInsDeclSus_2.setCellStyle(cell_bold_left_border);
		cellInsDeclSus_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceDeclarationSuspend", locale));
		ImportExportWorker.createEmptyCell(insDeclSus, 2, 10, styleNormal);
		
		rownum++;
		Row rowDecreaseLabor = sheet.createRow(rownum);
		Cell cellRowDecreaseLabor_1 = rowDecreaseLabor.createCell(0);
		cellRowDecreaseLabor_1.setCellStyle(cell_italic_center_border);
		cellRowDecreaseLabor_1.setCellValue("II.1");
		Cell cellRowDecreaseLabor_2 = rowDecreaseLabor.createCell(1);
		cellRowDecreaseLabor_2.setCellStyle(cell_italic_left_border);
		cellRowDecreaseLabor_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceLabor", locale));
		ImportExportWorker.createEmptyCell(rowDecreaseLabor, 2, 10, styleNormal);
		
		List<GenericValue> insDeclReduceParticipateList = EntityUtil.filterByCondition(insuranceDeclarationList, 
				EntityCondition.makeCondition("insuranceParticipateTypeId", 
						EntityJoinOperator.IN, UtilMisc.toList("STOP_PARTICIPATE", "SUSPEND_MOVE", "SUSPEND_SICK_LONG", "SUSPEND_TS")));
		if(UtilValidate.isNotEmpty(insDeclReduceParticipateList)){
			rownum = fillListDataPartyToD02TSExcel(wb, delegator, sheet, rownum, styleNormal, insDeclReduceParticipateList);
		}
		rownum++;
		Row emptyRow4 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow4, 0, 10, styleNormal);
		
		rownum++;
		Row rowReduceDC = sheet.createRow(rownum);
		Cell cellRowReduceDC = rowReduceDC.createCell(0);
		cellRowReduceDC.setCellStyle(cell_italic_center_border);
		cellRowReduceDC.setCellValue("II.2");
		Cell cellRowReduceDC_2 = rowReduceDC.createCell(1);
		cellRowReduceDC_2.setCellStyle(cell_italic_left_border);
		cellRowReduceDC_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSalaryShort", locale));
		ImportExportWorker.createEmptyCell(rowReduceDC, 2, 10, styleNormal);
		
		List<GenericValue> insDeclReduceParticipateDCList = EntityUtil.filterByCondition(insuranceDeclarationList, 
				EntityCondition.makeCondition("insuranceParticipateTypeId", 
						EntityJoinOperator.IN, UtilMisc.toList("SUSPEND_DC")));
		if(UtilValidate.isNotEmpty(insDeclReduceParticipateDCList)){
			rownum = fillListDataPartyToD02TSExcel(wb, delegator, sheet, rownum, styleNormal, insDeclReduceParticipateDCList);
		}
		rownum++;
		Row emptyRow5 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow5, 0, 10, styleNormal);
		
		rownum++;
		Row rowInsHealthReduce = sheet.createRow(rownum);
		Cell cellRowInsHealthReduce_1 = rowInsHealthReduce.createCell(0);
		cellRowInsHealthReduce_1.setCellStyle(cell_italic_center_border);
		cellRowInsHealthReduce_1.setCellValue("II.3");
		Cell cellRowInsHealthReduce_2 = rowInsHealthReduce.createCell(1);
		cellRowInsHealthReduce_2.setCellStyle(cell_italic_left_border);
		cellRowInsHealthReduce_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceHealth", locale));
		ImportExportWorker.createEmptyCell(rowInsHealthReduce, 2, 10, styleNormal);
		
		rownum++;
		Row emptyRow6 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow6, 0, 10, styleNormal);
		
		rownum++;
		Row rowInsUnEmplReduce = sheet.createRow(rownum);
		Cell cellRowInsUnEmplReduce = rowInsUnEmplReduce.createCell(0);
		cellRowInsUnEmplReduce.setCellStyle(cell_italic_center_border);
		cellRowInsUnEmplReduce.setCellValue("II.4");
		Cell cellRowInsUnEmplReduce_2 = rowInsUnEmplReduce.createCell(1);
		cellRowInsUnEmplReduce_2.setCellStyle(cell_italic_left_border);
		cellRowInsUnEmplReduce_2.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceUnemployment", locale));
		ImportExportWorker.createEmptyCell(rowInsUnEmplReduce, 2, 10, styleNormal);
		rownum++;
		Row emptyRow7 = sheet.createRow(rownum);
		ImportExportWorker.createEmptyCell(emptyRow7, 0, 10, styleNormal);
		
		rownum++;
		Row rowParticipateTotalReduce = sheet.createRow(rownum);
		rowParticipateTotalReduce.createCell(0).setCellStyle(styleNormal);
		Cell cellParticipateTotalReduce = rowParticipateTotalReduce.createCell(1);
		cellParticipateTotalReduce.setCellStyle(cell_bold_left_border);
		cellParticipateTotalReduce.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceTotalReduce", locale));
		ImportExportWorker.createEmptyCell(rowParticipateTotalReduce, 2, 10, styleNormal);
		return wb;
	}
	
	public static int fillListDataPartyToD02TSExcel(Workbook wb, Delegator delegator, Sheet sheet, int rownum, 
			CellStyle style, List<GenericValue> listData) throws GenericEntityException{
		
		for(GenericValue tempGv: listData){
			rownum++;
			Row partyRow = sheet.createRow(rownum); 
			String partyId = tempGv.getString("partyId");
			String partyName = PartyUtil.getPersonName(delegator, partyId);
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			String insuranceSocialNbr = person.getString("insuranceSocialNbr");
			BigDecimal salary = tempGv.getBigDecimal("salary");
			String salaryStr = null;
			if(salary != null){
				salaryStr = String.format("%,.2f", salary.setScale(2, RoundingMode.CEILING));
			}
			Timestamp agreementFromDate = tempGv.getTimestamp("agreementFromDate");
			String fromDateStr = null;
			if(agreementFromDate != null){
				fromDateStr = DateUtil.getDateMonthYearDesc(agreementFromDate);
			}
			fillDataPartyParticipateInsToExcel(wb, partyRow, style, partyName, insuranceSocialNbr, tempGv.getString("jobDescription"), salaryStr, 
					tempGv.getDouble("allowancePosition"), tempGv.getDouble("allowanceSeniority"), tempGv.getDouble("allowanceSeniorityExces"), tempGv.getDouble("allowanceOther"), fromDateStr);
		}
		//rownum++;
		return rownum;
	}
	
	public static void fillDataPartyParticipateInsToExcel(Workbook wb, Row row, CellStyle style, String partyName, String insuranceSocialNbr, 
			String jobTitle, String salary, Double allowancePos, Double allowanceSeniority, Double allowanceSeniorityExces, 
			Double allowanceOther, String fromDate){
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		Cell emptyCell = row.createCell(0);
		emptyCell.setCellStyle(style);
		Cell emptyCell10 = row.createCell(10);
		emptyCell10.setCellStyle(style);
		Cell partyNameCell = row.createCell(1);
		partyNameCell.setCellValue(partyName);
		partyNameCell.setCellStyle(style);
		Cell socialNbrCell = row.createCell(2);
		socialNbrCell.setCellStyle(style);
		if(insuranceSocialNbr != null){
			socialNbrCell.setCellValue(insuranceSocialNbr);
		}
		Cell jobTitleCell = row.createCell(3);
		jobTitleCell.setCellStyle(style);
		if(jobTitle != null){
			jobTitleCell.setCellValue(jobTitle);
		}
		Cell salaryCell = row.createCell(4);
		salaryCell.setCellStyle(style);
		if(salary != null){
			salaryCell.setCellValue(salary);
		}
		Cell allowancePosCell = row.createCell(5);
		allowancePosCell.setCellStyle(style);
		if(allowancePos != null){
			allowancePosCell.setCellValue(allowancePos);
		}
		Cell allowanceSeniorityCell = row.createCell(6);
		allowanceSeniorityCell.setCellStyle(style);
		if(allowanceSeniority != null){
			allowanceSeniorityCell.setCellValue(allowanceSeniority);
		}
		Cell allowanceSeniorityExcesCell = row.createCell(7);
		allowanceSeniorityExcesCell.setCellStyle(style);
		if(allowanceSeniorityExces != null){
			allowanceSeniorityExcesCell.setCellValue(allowanceSeniorityExces);
		}
		Cell allowanceOtherCell = row.createCell(8);
		allowanceOtherCell.setCellStyle(style);
		if(allowanceOther != null){
			allowanceOtherCell.setCellValue(allowanceOther);
		}
		Cell fromDateCell = row.createCell(9);
		fromDateCell.setCellStyle(style);
		if(fromDate != null){
			fromDateCell.setCellValue(fromDate);
		}
	}

	public static Workbook exportInsuranceBenefitAllowance(Delegator delegator,
			String customTimePeriodId, String insuranceContentTypeId,
			Long sequenceNum, Locale locale, HttpServletResponse response) throws GenericEntityException {
		GenericValue insuranceContentType = delegator.findOne("InsuranceContentType", UtilMisc.toMap("insuranceContentTypeId", insuranceContentTypeId), false);
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet(insuranceContentType.getString("contentTypeName"));
		sheet.setDisplayGridlines(true);
		sheet.setPrintGridlines(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		//sheet.setAutobreaks(true);
		String title = insuranceContentType.getString("description");
		if(title == null){
			insuranceContentType.getString("contentTypeName");
		}
		int rownum = renderC6xHDHeader(wb, sheet, title, locale);
		if("C66a-HD".equals(insuranceContentTypeId) || "C67a-HD".equals(insuranceContentTypeId)){
			rownum = renderC66aC67aTableHeader(wb, sheet, rownum, locale);
		}else if("C68a-HD".equals(insuranceContentTypeId) || "C69a-HD".equals(insuranceContentTypeId)){
			rownum = renderC68aC69aTableHeader(wb, sheet, rownum, locale);
		}else if("C70a-HD".equals(insuranceContentTypeId)){
			rownum = renderC70aTableHeader(wb, sheet, rownum, locale);
		}
		List<GenericValue> benefitTypeList = delegator.findByAnd("InsAllowanceBenefitType", 
				UtilMisc.toMap("insuranceContentTypeId", insuranceContentTypeId), UtilMisc.toList("benefitTypeCode"), false);
		if(UtilValidate.isNotEmpty(benefitTypeList)){
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			conditions.add(EntityCondition.makeCondition("sequenceNum", sequenceNum));
			List<String> benefitTypeIdList = EntityUtil.getFieldListFromEntityList(benefitTypeList, "benefitTypeId", true);
			conditions.add(EntityCondition.makeCondition("benefitTypeId", EntityJoinOperator.IN, benefitTypeIdList));
			List<GenericValue> partyInsAllowancePaymentList = delegator.findList("InsPayDeclAndPartyAndLeave", 
					EntityCondition.makeCondition(conditions), null, null, null, false);
			
			if("C66a-HD".equals(insuranceContentTypeId) || "C67a-HD".equals(insuranceContentTypeId)){
				rownum = renderC66aC67aTableData(delegator, wb, sheet, partyInsAllowancePaymentList, benefitTypeList, rownum, 9, locale);
			}else if("C68a-HD".equals(insuranceContentTypeId) || "C69a-HD".equals(insuranceContentTypeId)){
				
			}else if("C70a-HD".equals(insuranceContentTypeId)){
				
			}
		}
		return wb;
	}

	private static int renderC66aC67aTableData(Delegator delegator, Workbook wb, Sheet sheet, List<GenericValue> listData, List<GenericValue> benefitTypeList, 
			int rownum, int lastCol, Locale locale) throws GenericEntityException {
		Font boldBorderFont10 = wb.createFont();
		boldBorderFont10.setFontHeightInPoints((short) 10);
		boldBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cellBoldCenterBorder = ImportExportWorker.getCellStyle(wb, boldBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		CellStyle cellBoldLeftBorder = ImportExportWorker.getCellStyle(wb, boldBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		Font normalBorderFont10 = wb.createFont();
		normalBorderFont10.setFontHeightInPoints((short) 10);
		CellStyle cellNormalCenterBorder = ImportExportWorker.getCellStyle(wb, normalBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		CellStyle cellNormalLeftBorder = ImportExportWorker.getCellStyle(wb, normalBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		for(int i = 0; i < benefitTypeList.size(); i++){
			GenericValue benefitType = benefitTypeList.get(i);
			Row row = sheet.createRow(rownum);
			Cell cell1 = row.createCell(0);
			cell1.setCellStyle(cellBoldCenterBorder);
			cell1.setCellValue(CommonUtil.getRomanNumerals(i + 1));
			Cell cell2 = row.createCell(1);
			cell2.setCellStyle(cellBoldLeftBorder);
			cell2.setCellValue(benefitType.getString("description"));
			ImportExportWorker.createEmptyCell(row, 2, lastCol, cellNormalLeftBorder);
			List<GenericValue> listDataFilterBenefitType = EntityUtil.filterByCondition(listData, 
					EntityCondition.makeCondition("benefitTypeId", benefitType.get("benefitTypeId")));
			for(int j = 0; j < listDataFilterBenefitType.size(); j++){
				GenericValue partyInsAllowancePayment = listDataFilterBenefitType.get(j);
				Map<Integer, Object> tempMapData = FastMap.newInstance();
				Map<Integer, CellStyle> tempMapStyle = FastMap.newInstance();
				rownum++;
				Row dataRow = sheet.createRow(rownum);
				String partyId = partyInsAllowancePayment.getString("partyId");
				String partyName = PartyUtil.getPersonName(delegator, partyId);
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				tempMapData.put(0, j+1);
				tempMapStyle.put(0, cellNormalCenterBorder);
				tempMapData.put(1, partyName);
				tempMapData.put(2, person.get("insuranceSocialNbr"));
				tempMapData.put(3, partyInsAllowancePayment.get("statusConditionBenefit"));
				BigDecimal insuranceSalary = partyInsAllowancePayment.getBigDecimal("insuranceSalary");
				if(insuranceSalary != null){
					tempMapData.put(4, String.format("%,.2f", insuranceSalary.setScale(2, RoundingMode.CEILING)));
				}else{
					tempMapData.put(4, null);
				}
				String insuranceParticipatePeriod = partyInsAllowancePayment.getString("insuranceParticipatePeriod");
				tempMapData.put(5, getDescInsParticipatePeriod(insuranceParticipatePeriod, locale));
				BigDecimal totalDayLeave = partyInsAllowancePayment.getBigDecimal("totalDayLeave");
				if(totalDayLeave != null){
					tempMapData.put(6, String.format("%,.1f", totalDayLeave.setScale(2)));
				}else{
					tempMapData.put(6, null);
				}
				tempMapData.put(7, partyInsAllowancePayment.get("accumulatedLeave"));
				BigDecimal allowanceAmount = partyInsAllowancePayment.getBigDecimal("allowanceAmount");
				if(allowanceAmount != null){
					tempMapData.put(8, String.format("%,.2f", allowanceAmount.setScale(2, RoundingMode.CEILING)));
				}else{
					tempMapData.put(8, null);
				}
				ImportExportWorker.writeDataToRowExcel(dataRow, tempMapData, tempMapStyle, cellNormalLeftBorder);
			}
			rownum++;
			Row emptyRow = sheet.createRow(rownum);
			ImportExportWorker.createEmptyCell(emptyRow, 0, lastCol, cellNormalLeftBorder);
			rownum++;
		}
		return rownum;
	}

	private static int renderC70aTableHeader(Workbook wb, Sheet sheet,
			int rownum, Locale locale) {
		/*Font boldCenterBorderFont10 = wb.createFont();
		boldCenterBorderFont10.setFontHeightInPoints((short) 10);
		boldCenterBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
			CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); */
		
		return rownum;
	}

	private static int renderC68aC69aTableHeader(Workbook wb, Sheet sheet,
			int rownum, Locale locale) {
		Font boldCenterBorderFont10 = wb.createFont();
		boldCenterBorderFont10.setFontHeightInPoints((short) 10);
		boldCenterBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		Row headerRow = sheet.createRow(rownum);
		headerRow.setHeight((short)600);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
		Map<Integer, String> headerTitleFirst = FastMap.newInstance();
		headerTitleFirst.put(0, UtilProperties.getMessage("BaseHRUiLabels", "HRSequenceNbr", locale));
		headerTitleFirst.put(1, UtilProperties.getMessage("BaseHRUiLabels", "HRFullName", locale));
		headerTitleFirst.put(2, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "SocialInsuranceNbrIdentify", locale));
		headerTitleFirst.put(3, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceBenefitCondition", locale));
		headerTitleFirst.put(4, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceParticipatePeriod", locale));
		headerTitleFirst.put(5, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "DateAccumulatedLeaveYTDBenefit", locale));
		headerTitleFirst.put(6, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceNumberProposal", locale));
		headerTitleFirst.put(9, UtilProperties.getMessage("BaseHRUiLabels", "HRNotes", locale));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 4, 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 5, 5));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 9, 9));
		ImportExportWorker.writeDataToRowExcel(headerRow, headerTitleFirst, null, cell_bold_center_border);
		
		rownum++;
		Row rowSecond = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 8, 8));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
		Cell nbrDayLeaveCell = rowSecond.createCell(6);
		nbrDayLeaveCell.setCellStyle(cell_bold_center_border);
		nbrDayLeaveCell.setCellValue(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRNumberDayLeave", locale));
		Cell amountNbr = rowSecond.createCell(8);
		amountNbr.setCellStyle(cell_bold_center_border);
		amountNbr.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonAmount", locale));
		
		rownum++;
		Row rowThird = sheet.createRow(rownum);
		rowThird.setHeight((short)600);
		Cell inPeriodCell = rowThird.createCell(6);
		Cell accumulatedCell = rowThird.createCell(7);
		inPeriodCell.setCellStyle(cell_bold_center_border);
		accumulatedCell.setCellStyle(cell_bold_center_border);
		inPeriodCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "ConvalescenceHome", locale));
		accumulatedCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "ConvalescenceCentral", locale));
		
		rownum++;
		Row rowFourth = sheet.createRow(rownum);
		Cell cell13_0 = rowFourth.createCell(0);
		Cell cell13_1 = rowFourth.createCell(1);
		Cell cell13_2 = rowFourth.createCell(2);
		Cell cell13_3 = rowFourth.createCell(3);
		Cell cell13_4 = rowFourth.createCell(4);
		Cell cell13_5 = rowFourth.createCell(5);
		Cell cell13_6 = rowFourth.createCell(6);
		Cell cell13_7 = rowFourth.createCell(7);
		Cell cell13_8 = rowFourth.createCell(8);
		Cell cell13_9 = rowFourth.createCell(9);
		cell13_0.setCellValue("A");
		cell13_1.setCellValue("B");
		cell13_2.setCellValue("C");
		cell13_3.setCellValue("D");
		cell13_4.setCellValue("1");
		cell13_5.setCellValue("2");
		cell13_6.setCellValue("3");
		cell13_7.setCellValue("4");
		cell13_8.setCellValue("5");
		cell13_9.setCellValue("E");
		cell13_0.setCellStyle(cell_bold_center_border);
		cell13_1.setCellStyle(cell_bold_center_border);
		cell13_2.setCellStyle(cell_bold_center_border);
		cell13_3.setCellStyle(cell_bold_center_border);
		cell13_4.setCellStyle(cell_bold_center_border);
		cell13_5.setCellStyle(cell_bold_center_border);
		cell13_6.setCellStyle(cell_bold_center_border);
		cell13_7.setCellStyle(cell_bold_center_border);
		cell13_8.setCellStyle(cell_bold_center_border);
		cell13_9.setCellStyle(cell_bold_center_border);
		return ++rownum;
	}
	
	private static int renderC66aC67aTableHeader(Workbook wb, Sheet sheet, int rownum, Locale locale) {
		Font boldCenterBorderFont10 = wb.createFont();
		boldCenterBorderFont10.setFontHeightInPoints((short) 10);
		boldCenterBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		Row row10 = sheet.createRow(rownum);
		row10.setHeight((short)600);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
		Map<Integer, String> headerTitleFirst = FastMap.newInstance();
		headerTitleFirst.put(0, UtilProperties.getMessage("BaseHRUiLabels", "HRSequenceNbr", locale));
		headerTitleFirst.put(1, UtilProperties.getMessage("BaseHRUiLabels", "HRFullName", locale));
		headerTitleFirst.put(2, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "SocialInsuranceNbrIdentify", locale));
		headerTitleFirst.put(3, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceBenefitCondition", locale));
		headerTitleFirst.put(4, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceSocialSalaryBenefit", locale));
		headerTitleFirst.put(5, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceParticipatePeriod", locale));
		headerTitleFirst.put(6, UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceNumberProposal", locale));
		headerTitleFirst.put(9, UtilProperties.getMessage("BaseHRUiLabels", "HRNotes", locale));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 3, 3));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 4, 4));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 5, 5));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 2, 9, 9));
		for(Map.Entry<Integer, String> entry: headerTitleFirst.entrySet()){
			int cellNbr = entry.getKey();
			String tempTitle = entry.getValue();
			Cell tempCell = row10.createCell(cellNbr);
			tempCell.setCellValue(tempTitle);
			tempCell.setCellStyle(cell_bold_center_border);
		}
		
		rownum++;
		Row row11 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 8, 8));
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 7));
		Cell nbrDayLeaveCell = row11.createCell(6);
		nbrDayLeaveCell.setCellStyle(cell_bold_center_border);
		nbrDayLeaveCell.setCellValue(UtilProperties.getMessage("BaseHREmployeeUiLabels", "HRNumberDayLeave", locale));
		Cell amountNbr = row11.createCell(8);
		amountNbr.setCellStyle(cell_bold_center_border);
		amountNbr.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonAmount", locale));
		
		rownum++;
		Row row12 = sheet.createRow(rownum);
		row12.setHeight((short)600);
		Cell inPeriodCell = row12.createCell(6);
		Cell accumulatedCell = row12.createCell(7);
		inPeriodCell.setCellStyle(cell_bold_center_border);
		accumulatedCell.setCellStyle(cell_bold_center_border);
		inPeriodCell.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonInPeriod", locale));
		accumulatedCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "DateAccumulatedLeaveYTD", locale));
		
		rownum++;
		Row row13 = sheet.createRow(rownum);
		Cell cell13_0 = row13.createCell(0);
		Cell cell13_1 = row13.createCell(1);
		Cell cell13_2 = row13.createCell(2);
		Cell cell13_3 = row13.createCell(3);
		Cell cell13_4 = row13.createCell(4);
		Cell cell13_5 = row13.createCell(5);
		Cell cell13_6 = row13.createCell(6);
		Cell cell13_7 = row13.createCell(7);
		Cell cell13_8 = row13.createCell(8);
		Cell cell13_9 = row13.createCell(9);
		cell13_0.setCellValue("A");
		cell13_1.setCellValue("B");
		cell13_2.setCellValue("C");
		cell13_3.setCellValue("D");
		cell13_4.setCellValue("1");
		cell13_5.setCellValue("2");
		cell13_6.setCellValue("3");
		cell13_7.setCellValue("4");
		cell13_8.setCellValue("5");
		cell13_9.setCellValue("E");
		cell13_0.setCellStyle(cell_bold_center_border);
		cell13_1.setCellStyle(cell_bold_center_border);
		cell13_2.setCellStyle(cell_bold_center_border);
		cell13_3.setCellStyle(cell_bold_center_border);
		cell13_4.setCellStyle(cell_bold_center_border);
		cell13_5.setCellStyle(cell_bold_center_border);
		cell13_6.setCellStyle(cell_bold_center_border);
		cell13_7.setCellStyle(cell_bold_center_border);
		cell13_8.setCellStyle(cell_bold_center_border);
		cell13_9.setCellStyle(cell_bold_center_border);
		return ++rownum;
	}

	private static int renderC6xHDHeader(Workbook wb, Sheet sheet, String title, Locale locale) {
		sheet.setColumnWidth(0, 15*100);
		sheet.setColumnWidth(1, 20*220);
		sheet.setColumnWidth(2, 20*300);
		sheet.setColumnWidth(3, 20*400);
		sheet.setColumnWidth(4, 20*150);
		sheet.setColumnWidth(5, 20*200);
		sheet.setColumnWidth(6, 20*100);
		sheet.setColumnWidth(7, 20*160);
		sheet.setColumnWidth(8, 20*150);
		sheet.setColumnWidth(9, 20*190);
		Font centerNoBorderFont10 =  wb.createFont();
		centerNoBorderFont10.setFontHeightInPoints((short) 10);
		CellStyle cellCenterNormalNoBorder = ImportExportWorker.getCellStyle(wb, centerNoBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.VERTICAL_CENTER, null, null); 
		
		Font boldCenterBorderFont10 = wb.createFont();
		boldCenterBorderFont10.setFontHeightInPoints((short) 10);
		boldCenterBorderFont10.setBoldweight(Font.BOLDWEIGHT_BOLD);
		ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_CENTER, 
				CellStyle.ALIGN_CENTER, null, null); 
		
		CellStyle cell_bold_left_no_border = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, null, null); 
		
		ImportExportWorker.getCellStyle(wb, boldCenterBorderFont10, CellStyle.ALIGN_LEFT, 
				CellStyle.VERTICAL_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		Font boldLeftFontItalic = wb.createFont();
		boldLeftFontItalic.setFontHeightInPoints((short) 10);
		boldLeftFontItalic.setItalic(true);
		ImportExportWorker.getCellStyle(wb, boldLeftFontItalic, CellStyle.ALIGN_LEFT, 
				CellStyle.ALIGN_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		ImportExportWorker.getCellStyle(wb, boldLeftFontItalic, CellStyle.ALIGN_CENTER, 
				CellStyle.ALIGN_CENTER, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex()); 
		
		Font boldCenterBorderFont12 = wb.createFont();
		boldCenterBorderFont12.setFontHeightInPoints((short) 12);
		boldCenterBorderFont12.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle cell_bold_center_no_border_12 = ImportExportWorker.getCellStyle(wb, boldCenterBorderFont12, CellStyle.ALIGN_CENTER, 
				CellStyle.ALIGN_CENTER, null, null); 
		
		Font fontBorderNormal = wb.createFont();
		fontBorderNormal.setFontHeightInPoints((short) 10);
		ImportExportWorker.getCellStyle(wb, fontBorderNormal, CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT, CellStyle.BORDER_THIN, IndexedColors.BLACK.getIndex());
		CellStyle styleNormalNoBorder = ImportExportWorker.getCellStyle(wb, fontBorderNormal, CellStyle.ALIGN_LEFT, CellStyle.ALIGN_LEFT, null, null);
		int rownum = 0;
		Row row0 = sheet.createRow(rownum);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		Cell orgNameTitle = row0.createCell(0);
		orgNameTitle.setCellValue(UtilProperties.getMessage("BaseHRDirectoryUiLabels", "OrgUnitName", locale) + ":");
		orgNameTitle.setCellStyle(cell_bold_left_no_border);
		
		rownum = 1;
		Row row1 = sheet.createRow(rownum);
		row1.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		Cell orgIdCell = row1.createCell(0);
		orgIdCell.setCellStyle(cell_bold_left_no_border);
		orgIdCell.setCellValue(UtilProperties.getMessage("BaseHRDirectoryUiLabels", "OrgUnitId", locale) + ":");
		
		rownum = 2;
		Row row2 = sheet.createRow(rownum);
		row2.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
		Cell contactCell = row2.createCell(0);
		contactCell.setCellStyle(cell_bold_left_no_border);
		contactCell.setCellValue(UtilProperties.getMessage("BaseHRUiLabels", "PhoneNumber", locale) + ":");
		Cell faxCell = row2.createCell(2);
		faxCell.setCellStyle(cell_bold_left_no_border);
		faxCell.setCellValue(UtilProperties.getMessage("PartyUiLabels", "PartyFaxNumber", locale) + ":");
		
		rownum = 3;
		Row row3 = sheet.createRow(rownum);
		row3.setHeight((short)600);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
		Cell mainTitle = row3.createCell(0);
		mainTitle.setCellStyle(cell_bold_center_no_border_12);
		mainTitle.setCellValue(title.toUpperCase());
		
		rownum = 4;
		Row row4 = sheet.createRow(rownum);
		row4.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
		Cell dateCell = row4.createCell(0);
		dateCell.setCellStyle(cellCenterNormalNoBorder);
		String dateCellValue = UtilProperties.getMessage("BaseHRUiLabels", "HRCommonMonth", locale);
		dateCellValue += "  " + UtilProperties.getMessage("BaseHRUiLabels", "HRCommonQuarterLowercase", locale);
		dateCellValue += "  " + UtilProperties.getMessage("BaseHRUiLabels", "HRCommonYearLowercase", locale);
		dateCell.setCellValue(dateCellValue);
		
		rownum = 5;
		Row row5 = sheet.createRow(rownum);
		row5.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 8));
		Cell accountNbrCell = row5.createCell(3);
		accountNbrCell.setCellStyle(styleNormalNoBorder);
		StringBuffer cellTitle = new StringBuffer();
		cellTitle.append(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InsuranceAccountNumber", locale)+ ": ");
		cellTitle.append("                             ");
		cellTitle.append(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "OpenedAt", locale));
		accountNbrCell.setCellValue(cellTitle.toString());
		
		rownum = 6;
		Row row6 = sheet.createRow(rownum);
		row6.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 8));
		Cell totalLaborCell = row6.createCell(3);
		totalLaborCell.setCellStyle(styleNormalNoBorder);
		StringBuffer totalLaborCellTitle = new StringBuffer();
		totalLaborCellTitle.append(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "TotalNbrLabor", locale)+ ": ");
		totalLaborCellTitle.append("                                   ");
		totalLaborCellTitle.append(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "InWhichWomen", locale)+ ": ");
		totalLaborCell.setCellValue(totalLaborCellTitle.toString());
		
		rownum = 7;
		Row row7 = sheet.createRow(rownum);
		row7.setHeight((short)300);
		sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 8));
		Cell totalSalaryFunCell = row7.createCell(3);
		totalSalaryFunCell.setCellStyle(styleNormalNoBorder);
		totalSalaryFunCell.setCellValue(UtilProperties.getMessage("BaseHRInsuranceUiLabels", "TotalSalaryFunInPeriod", locale)+ ": ");
		rownum += 2;
		return rownum;
	}

	public static Date getDateParticipateIns(Delegator delegator,
			String partyId) throws GenericEntityException {
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		Date dateParticipateIns = person.getDate("dateParticipateIns");
		return dateParticipateIns;
	}

	public static void updatePartyParticipateInsurance(Delegator delegator,
			String partyId, Date date) throws GenericEntityException {
		GenericValue party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		if(party != null){
			Date dateParticipateIns = party.getDate("dateParticipateIns");
			if(dateParticipateIns == null || dateParticipateIns.compareTo(date) != 0){
				party.set("dateParticipateIns", date);
				party.store();
			}
		}
	}

	public static String getDescInsParticipatePeriod(Delegator delegator,
			String partyId, Date thruDate, Locale locale) throws GenericEntityException {
		Date dateParticipateIns = getDateParticipateIns(delegator, partyId);
		if(dateParticipateIns != null){
			int totalMonth = DateUtil.getMonthBetweenTwoDate(dateParticipateIns, thruDate);
			Integer nbrYear = totalMonth / 12;
			Integer nbrMonth = totalMonth - 12 * nbrYear;
			StringBuffer retStr = new StringBuffer();
			if(nbrYear > 0){
				retStr.append(nbrYear);
				retStr.append(" ");
				retStr.append(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonYearLowercase", locale));
				retStr.append(" ");
			}
			if(nbrMonth > 0){
				retStr.append(nbrMonth);
				retStr.append(" ");
				retStr.append(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonMonthLowercase", locale));
			}
			return retStr.toString();
		}
		return null;
	}
	
	public static String getDescInsParticipatePeriod(
			String insuranceParticipatePeriod, Locale locale) {
		// current pattern is yy-mm
		if(insuranceParticipatePeriod == null){
			return null;
		}
		String[] insuranceParticipatePeriodArr = insuranceParticipatePeriod.split("-");
		String yearDesc = insuranceParticipatePeriodArr[0];
		String monthDesc = insuranceParticipatePeriodArr[1];
		Integer yearNbr = Integer.parseInt(yearDesc.trim());
		Integer monthNbr = Integer.parseInt(monthDesc.trim());
		StringBuffer retStr = new StringBuffer();
		if(yearNbr > 0){
			retStr.append(yearNbr);
			retStr.append(" ");
			retStr.append(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonYearLowercase", locale));
			retStr.append(" ");
		}
		if(monthNbr > 0){
			retStr.append(monthNbr);
			retStr.append(" ");
			retStr.append(UtilProperties.getMessage("BaseHRUiLabels", "HRCommonMonthLowercase", locale));
		}
		return retStr.toString();
	}

	public static GenericValue getEmplInsuranceLatestStatus(Delegator delegator, String partyId, Timestamp fromDate, TimeZone timeZone, Locale locale) throws GenericEntityException {
		List<EntityCondition> checkParticipateConds = FastList.newInstance();
		Timestamp previousMonthStart = UtilDateTime.getMonthStart(fromDate, 0, -1);
		Timestamp previousMonthEnd = UtilDateTime.getMonthEnd(previousMonthStart, timeZone, locale);
		checkParticipateConds.add(EntityCondition.makeCondition("partyId", partyId));
		checkParticipateConds.add(EntityCondition.makeCondition("participateFromDate", EntityJoinOperator.LESS_THAN, fromDate));
		//checkParticipateConds.add(EntityCondition.makeCondition("statusId", "PARTICIPATING"));
		checkParticipateConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("participateThruDate", null),
									EntityJoinOperator.OR,
								EntityCondition.makeCondition("participateThruDate", previousMonthEnd)));
		List<GenericValue> listGv = delegator.findList("InsuranceEmplDetailAndMaxDate", EntityCondition.makeCondition(checkParticipateConds), null, null, null, false);
		if(UtilValidate.isNotEmpty(listGv)){
			return listGv.get(0);
		}
		return null;
	}

	public static GenericValue getPartyInsuranceSalaryLastest(Delegator delegator, String partyId) throws GenericEntityException {
		List<GenericValue> partyInsuranceSal = delegator.findByAnd("PartyInsuranceSalary", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-fromDate"), false);
		if(UtilValidate.isNotEmpty(partyInsuranceSal)){
			return partyInsuranceSal.get(0);
		}
		return null;
	}

	public static void updateInsuranceReportPartyOriginate(Delegator delegator,
			Long month, Long year, String reportId) throws GenericEntityException {
		List<GenericValue> insurancePartyOriginateList = delegator.findByAnd("InsurancePartyOriginate", UtilMisc.toMap("monthReport", month, "yearReport", year), null, false);
		for(GenericValue insurancePartyOriginate: insurancePartyOriginateList){
			GenericValue insuranceReportPartyOriginate = delegator.makeValue("InsuranceReportPartyOriginate");
			insuranceReportPartyOriginate.put("reportId", reportId);
			insuranceReportPartyOriginate.put("insurancePartyOriginateId", insurancePartyOriginate.get("insurancePartyOriginateId"));
			delegator.createOrStore(insuranceReportPartyOriginate);
		}		
	}
}
