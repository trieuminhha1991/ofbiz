package com.olbius.insurance.services;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.insurance.helper.InsuranceHelper;
import com.olbius.payroll.util.PayrollEntityConditionUtils;
import com.olbius.util.DateUtil;
import com.olbius.util.PartyUtil;

public class InsuranceServices {
	public static Map<String, Object> createInsuranceDeclaration(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			String declarationTypeId = (String)context.get("declarationTypeId");
			String customTimePeriodId = (String)context.get("customTimePeriodId");
			List<GenericValue> listInsuranceDelaration = delegator.findByAnd("InsuranceDeclaration", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "declarationTypeId", declarationTypeId), UtilMisc.toList("-sequenceNum"), false);
			Long sequenceNum = 1L;
			if(UtilValidate.isNotEmpty(listInsuranceDelaration)){
				sequenceNum += listInsuranceDelaration.get(0).getLong("sequenceNum");
			}
			GenericValue insuranceDelaration = delegator.makeValue("InsuranceDeclaration");
			insuranceDelaration.setNonPKFields(context);
			String insuranceDelarationId = delegator.getNextSeqId("InsuranceDeclaration");
			insuranceDelaration.set("insuranceDelarationId", insuranceDelarationId);
			insuranceDelaration.set("sequenceNum", sequenceNum);
			insuranceDelaration.create();
			retMap.put("insuranceDelarationId", insuranceDelarationId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPartyInsuranceDeclaration(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		String insuranceDelarationId = request.getParameter("insuranceDelarationId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		Locale locale = (Locale)context.get("locale");
		if(insuranceDelarationId != null){
			Map<String, Object> resultService;
			try {
				GenericValue insuranceDeclaration = delegator.findOne("InsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), false);
				if(insuranceDeclaration == null){
					return ServiceUtil.returnError(UtilProperties.getMessage("InsuranceUiLabels", "CannotFindInsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), locale));
				}
				String declarationTypeId = insuranceDeclaration.getString("declarationTypeId");
				String customTimePeriodId = insuranceDeclaration.getString("customTimePeriodId");
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				Date fromDateCustomTime = customTimePeriod.getDate("fromDate");
				Date thruDateCustomTime = customTimePeriod.getDate("thruDate");
				Timestamp fromDateTs = new Timestamp(fromDateCustomTime.getTime());
				Timestamp thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDateCustomTime.getTime()));
				
				EntityCondition conditionInsuranceHealth = EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDateTs),
																							EntityJoinOperator.AND,
																							EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDateTs));
				List<GenericValue> partyInsuranceDeclarationList = delegator.findByAnd("PartyInsuranceDeclaration", 
						UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), UtilMisc.toList("partyId"), false);
				totalRows = partyInsuranceDeclarationList.size();
				if(end > totalRows){
					end = totalRows;
				}
				partyInsuranceDeclarationList = partyInsuranceDeclarationList.subList(start, end);
				for(GenericValue tmpGv: partyInsuranceDeclarationList){
					Map<String, Object> tempMap = FastMap.newInstance();
					listReturn.add(tempMap);
					String partyId = tmpGv.getString("partyId");
					String partyName = PartyUtil.getPersonName(delegator, partyId);
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					tempMap.put("insuranceDelarationId", insuranceDelarationId);
					tempMap.put("partyName", partyName);
					tempMap.put("partyId", partyId);
					tempMap.put("insuranceParticipateTypeId", tmpGv.getString("insuranceParticipateTypeId"));
					tempMap.put("salary", tmpGv.getBigDecimal("salary"));
					tempMap.put("uomId", tmpGv.getString("uomId"));
					tempMap.put("ratioSalary", tmpGv.get("ratioSalary"));
					tempMap.put("allowanceSeniority", tmpGv.get("allowanceSeniority"));
					tempMap.put("allowanceSeniorityExces", tmpGv.get("allowanceSeniorityExces"));
					tempMap.put("allowancePosition", tmpGv.get("allowancePosition"));
					tempMap.put("allowanceOther", tmpGv.get("allowanceOther"));
					tempMap.put("jobDescription", tmpGv.get("jobDescription"));
					tempMap.put("insuranceSocialNbr", person.get("insuranceSocialNbr"));
					Timestamp agreementFromDate = tmpGv.getTimestamp("agreementFromDate");
					String gender = person.getString("gender");
					if("F".equals(gender)){
						tempMap.put("isWomen", "x");
					}
					if(agreementFromDate != null){
						tempMap.put("agreementFromDate", agreementFromDate.getTime());
					}
					Timestamp thruDate = tmpGv.getTimestamp("agreementThruDate");
					if(thruDate != null){
						tempMap.put("agreementThruDate", thruDate.getTime());
					}
					tempMap.put("agreementNbr", tmpGv.getString("agreementNbr"));
					EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
					List<GenericValue> insuranceHealthList = delegator.findList("PartyHealthInsuranceAndHospitalPerson", 
							EntityCondition.makeCondition(partyCond, EntityJoinOperator.AND, conditionInsuranceHealth), null, null, null, false);
					if(UtilValidate.isNotEmpty(insuranceHealthList)){
						GenericValue insuranceHealth = insuranceHealthList.get(0);
						tempMap.put("stateProvinceGeoHospital", insuranceHealth.get("stateProvinceGeoId"));
						tempMap.put("hospitalId", insuranceHealth.get("hospitalId"));
						tempMap.put("hospitalName", insuranceHealth.get("hospitalName"));
						tempMap.put("hospitalCode", insuranceHealth.get("hospitalCode"));
						tempMap.put("insHealthCard", insuranceHealth.getString("insHealthCard"));
					}
					if(!"DECLARATION_SUSPEND".equals(declarationTypeId)){
						tempMap.put("agreemenType", tmpGv.getString("agreementType"));
						
						String subjectInsuranceType = InsuranceHelper.getInsuranceTypeParticipateInDecl(delegator, insuranceDelarationId, partyId);
						tempMap.put("insuranceTypeParticipate", subjectInsuranceType);
						tempMap.put("statusSocicalInsId", InsuranceHelper.getPartyInsuranceStatus(delegator, partyId, thruDateTs));
						String proposalInsuredMonth = tmpGv.getString("proposalInsuredMonth");
						tempMap.put("proposalInsuredMonth", proposalInsuredMonth);
						String nationalityId = person.getString("nationality");
						if(!"Vietnamese".equals(nationalityId)){
							tempMap.put("nationalityId", nationalityId);
						}
						tempMap.put("ethnicOriginId", person.getString("ethnicOrigin")) ;
						tempMap.put("idNumber", person.get("idNumber"));
						Date idIssueDate = person.getDate("idIssueDate");
						if(idIssueDate != null){
							tempMap.put("idIssueDate", idIssueDate.getTime());
						}
						String idIssuePlace = tmpGv.getString("idIssuePlace");
						tempMap.put("idIssuePlace", idIssuePlace);
						Date birthDate = person.getDate("birthDate");
						if(birthDate != null){
							tempMap.put("birthDate", birthDate.getTime());
						}
						
						resultService = dispatcher.runSync("getPartyPostalAddress", 
								UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "BIRTH_CERT_LOCATION", "userLogin", userLogin));
						if(ServiceUtil.isSuccess(resultService)){
							String contactMechId = (String)resultService.get("contactMechId");
							if(contactMechId != null){
								GenericValue geoCurrRes;
								GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
								String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
								if(stateProvinceGeoId != null){
									geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
									tempMap.put("birthCertStateGeoId", geoCurrRes.getString("geoName"));
								}
								String wardGeoId = postalAddr.getString("wardGeoId");
								if(wardGeoId != null){
									geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
									tempMap.put("birthCertWardGeoId", geoCurrRes.getString("geoName"));
								}
								String districtGeoId = postalAddr.getString("districtGeoId");
								if(districtGeoId != null){
									geoCurrRes = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
									tempMap.put("birthCertDistrictGeoId", geoCurrRes.getString("geoName"));
								}
							}
						}
						
						resultService = dispatcher.runSync("getPartyPostalAddress", 
								UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PERMANENT_RESIDENCE", "userLogin", userLogin));
						if(ServiceUtil.isSuccess(resultService)){
							String contactMechId = (String)resultService.get("contactMechId");
							if(contactMechId != null){
								GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
								String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
								tempMap.put("stateGeoPermanent", stateProvinceGeoId);
								String wardGeoId = postalAddr.getString("wardGeoId");
								tempMap.put("wardGeoPermanent", wardGeoId);
								String districtGeoId = postalAddr.getString("districtGeoId");
								tempMap.put("districtGeoPermanent", districtGeoId);
								String address1Permanent = postalAddr.getString("address1");
								tempMap.put("address1Permanent", address1Permanent);
							}
						}
						resultService = dispatcher.runSync("getPartyPostalAddress", 
								UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "CURRENT_RESIDENCE", "userLogin", userLogin));
						if(ServiceUtil.isSuccess(resultService)){
							String contactMechId = (String)resultService.get("contactMechId");
							if(contactMechId != null){
								GenericValue postalAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
								String stateProvinceGeoId = postalAddr.getString("stateProvinceGeoId");
								tempMap.put("stateGeoCurrRes", stateProvinceGeoId);
								String wardGeoId = postalAddr.getString("wardGeoId");
								tempMap.put("wradGeoCurrRes", wardGeoId);
								String districtGeoId = postalAddr.getString("districtGeoId");
								tempMap.put("districtGeoCurrRes", districtGeoId);
								String address1CurrRes = postalAddr.getString("address1");
								tempMap.put("address1CurrRes", address1CurrRes);
							}
						}
						resultService = dispatcher.runSync("getPartyTelephone", 
								UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "userLogin", userLogin));
						if(ServiceUtil.isSuccess(resultService)){
							tempMap.put("phoneNumber", resultService.get("contactNumber"));
						}
						String parentRelationId = InsuranceHelper.getParentRelation(delegator, partyId);
						if(parentRelationId != null){
							tempMap.put("parentRelation", PartyUtil.getPersonName(delegator, parentRelationId));
						}
					}else{
						tempMap.put("isReducedBefore", tmpGv.getString("isReducedBefore"));
						tempMap.put("isRetInsHealthCard", tmpGv.getString("isRetInsHealthCard"));
						Date dateReturnCard = tmpGv.getDate("dateReturnCard");
						if(dateReturnCard != null){
							tempMap.put("dateReturnCard", dateReturnCard.getTime());
						}
						tempMap.put("suspendReasonId", tmpGv.getString("suspendReasonId"));
					}
					
					Timestamp agreementSignDate = tmpGv.getTimestamp("agreementSignDate");
					if(agreementSignDate != null){
						tempMap.put("agreementSignDate", agreementSignDate.getTime());
					}
					tempMap.put("rateContribution", tmpGv.get("rateContribution"));
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (GenericServiceException e) {
				e.printStackTrace();
			} 
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	public static Map<String, Object> getHospitalByGeo(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("InsuranceUiLabels", "RetrieveHospitalListSuccess", locale));
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
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPartyInsuranceDeclaration(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String insuranceDelarationId = (String)context.get("insuranceDelarationId");
		String partyId = (String)context.get("partyId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String uomId = (String)context.get("uomId");
		String insuranceSocialNbr = (String)context.get("insuranceSocialNbr");
		String statusSocicalInsId = (String)context.get("statusSocicalInsId");
		List<String> insuranceTypeList = (List<String>)context.get("insuranceTypeList");
		Timestamp agreementFromDate = (Timestamp)context.get("agreementFromDate");		
		if(uomId == null){
			EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator); 
		}
		try {
			/*GenericValue checkEntity = delegator.findOne("PartyInsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId, "partyId", partyId), false);
			if(checkEntity != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("InsuranceUiLabels", "PartyAddToInsuranceDeclaration", UtilMisc.toMap("personName", PartyUtil.getPersonName(delegator, partyId)), locale));
			}*/
			GenericValue insuranceDeclaration = delegator.findOne("InsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), false);			
			String declarationTypeId = insuranceDeclaration.getString("declarationTypeId");
			GenericValue insuranceDeclarationType = delegator.findOne("InsuranceDeclarationType", UtilMisc.toMap("declarationTypeId", declarationTypeId), false);
			String description = insuranceDeclarationType.getString("description");
			
			String customTimePeriodId = insuranceDeclaration.getString("customTimePeriodId");
			List<GenericValue> partyInsuranceDeclaredList = delegator.findByAnd("PartyAndInsuranceDeclaration", UtilMisc.toMap("partyId", partyId, "customTimePeriodId", customTimePeriodId), null, false);
			if(UtilValidate.isNotEmpty(partyInsuranceDeclaredList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("InsuranceUiLabels", "PartyAddToInsuranceDeclaration", 
						UtilMisc.toMap("personName", PartyUtil.getPersonName(delegator, partyId), "declarationType", description), locale));
			}
			GenericValue customTimePeriodGv = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(insuranceSocialNbr != null && person != null){
				person.set("insuranceSocialNbr", insuranceSocialNbr);
				person.store();
			}
			
			Date fromDate = customTimePeriodGv.getDate("fromDate");
			Date thruDate = customTimePeriodGv.getDate("thruDate");
			Timestamp fromDateTimestamp = new Timestamp(fromDate.getTime());
			Timestamp thruDateTimestamp = new Timestamp(thruDate.getTime());
			thruDateTimestamp = UtilDateTime.getDayEnd(thruDateTimestamp);
			//check whether status participate insurance is valid to declare participate or suspend 			 
			Map<String, Object> checkMap = InsuranceHelper.checkStatusSocialInsSttIsValidToDecl(dctx, context, thruDateTimestamp, fromDateTimestamp, description);
			if(checkMap != null && checkMap.get(ModelService.ERROR_MESSAGE) != null){
				return ServiceUtil.returnError((String)checkMap.get(ModelService.ERROR_MESSAGE));
			}
			GenericValue partyInsuranceDeclaration = delegator.makeValue("PartyInsuranceDeclaration");
			partyInsuranceDeclaration.setAllFields(context, false, null, null);
			partyInsuranceDeclaration.set("uomId", uomId);
			List<GenericValue> emplPositionList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDateTimestamp, thruDateTimestamp);
			List<String> emplPositionListStr = EntityUtil.getFieldListFromEntityList(emplPositionList, "description", true);
			partyInsuranceDeclaration.set("jobDescription", StringUtils.join(emplPositionListStr, ", "));
			//get base salary flat
			Map<String, Object> resultService = dispatcher.runSync("getSalaryAmountEmpl", 
					UtilMisc.toMap("partyId", partyId, 
								  "fromDate", fromDateTimestamp, 
								  "thruDate", thruDateTimestamp, 
								  "userLogin", userLogin));
			if(ServiceUtil.isSuccess(resultService)){
				BigDecimal rateAmount = (BigDecimal)resultService.get("rateAmount");
				partyInsuranceDeclaration.set("salary", rateAmount);
			}
			
			Double rateContribution = InsuranceHelper.getEmplRateContribution(delegator, insuranceTypeList);
			partyInsuranceDeclaration.set("rateContribution", rateContribution);
			partyInsuranceDeclaration.create();
			Timestamp partyParticipateInsSttFromDate = agreementFromDate;
			if(partyParticipateInsSttFromDate == null){
				partyParticipateInsSttFromDate = thruDateTimestamp;
			}
			
			//for case: declaration participate
			if(insuranceTypeList != null){
				for(String insuranceTypeId: insuranceTypeList){
					GenericValue partyInsuranceDeclInsuranceType = delegator.makeValue("PartyInsuranceDeclInsuranceType");
					partyInsuranceDeclInsuranceType.set("insuranceDelarationId", insuranceDelarationId);
					partyInsuranceDeclInsuranceType.set("partyId", partyId);
					partyInsuranceDeclInsuranceType.set("insuranceTypeId", insuranceTypeId);
					partyInsuranceDeclInsuranceType.create();
					//create partyParticipateInsurance
					InsuranceHelper.createPartyParticipateInsurance(dctx, context, insuranceTypeId, partyParticipateInsSttFromDate);
				}
			}
			
			//for case: declaration suspend
			if("DECLARATION_SUSPEND".equals(insuranceDeclaration.getString("declarationTypeId"))){
				List<GenericValue> insuranceTypeListGv = delegator.findByAnd("InsuranceType", null, null, false);
				List<String> insuranceTypeListSuspend = EntityUtil.getFieldListFromEntityList(insuranceTypeListGv, "insuranceTypeId", true);
				for(String insuranceTypeId: insuranceTypeListSuspend){
					InsuranceHelper.createPartyParticipateInsurance(dctx, context, insuranceTypeId, partyParticipateInsSttFromDate);
				}
			}
			if(statusSocicalInsId != null){
				GenericValue partyInsuranceSocialStatus = delegator.makeValue("PartyInsuranceSocialStatus");
				partyInsuranceSocialStatus.set("statusId", statusSocicalInsId);
				partyInsuranceSocialStatus.set("partyId", partyId);
				partyInsuranceSocialStatus.set("statusDatetime", fromDateTimestamp);
				partyInsuranceSocialStatus.create();
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	public static Map<String, Object> deletePartyInsuranceDecl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String insuranceDelarationId = (String)context.get("insuranceDelarationId");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue insuranceDeclaration = delegator.findOne("InsuranceDeclaration", UtilMisc.toMap("insuranceDelarationId", insuranceDelarationId), false);
			String customTimePeriodId = insuranceDeclaration.getString("customTimePeriodId");
			GenericValue customTimePeriodGv = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			Date fromDate = customTimePeriodGv.getDate("fromDate");
			Timestamp fromDateTimestamp = new Timestamp(fromDate.getTime());
			//Date thruDate = customTimePeriodGv.getDate("thruDate");
			//Timestamp thruDateTimestamp = new Timestamp(thruDate.getTime());
			//thruDateTimestamp = UtilDateTime.getDayEnd(thruDateTimestamp);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDateTimestamp));
			conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null));
			conds.add(EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDateTimestamp));
			conds.add(EntityCondition.makeCondition("insuranceTypeId", "BHXH"));
			List<GenericValue> partyParticipateInsuranceList = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(partyParticipateInsuranceList)){				
				conds.clear();
				conds.add(EntityCondition.makeCondition("partyId", partyId));
				conds.add(EntityCondition.makeCondition("insuranceTypeId", "BHXH"));
				conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDateTimestamp));
				conds.add(EntityCondition.makeCondition("thruDate", null));
				List<GenericValue> partyParticipateInsuranceNext = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(partyParticipateInsuranceNext)){
					GenericValue partyParticipateInsurance = partyParticipateInsuranceNext.get(0);
					Timestamp fromDateNext = partyParticipateInsurance.getTimestamp("fromDate");
					String statusId = partyParticipateInsurance.getString("statusId");
					GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					Calendar cal = Calendar.getInstance();
					cal.setTime(fromDateNext);
					return ServiceUtil.returnError(UtilProperties.getMessage("InsuranceUiLabels", "CannotDeletePartyDecl_DeclaredNextPeriod", 
							UtilMisc.toMap("personName", PartyUtil.getPersonName(delegator, partyId), "statusId", statusItem.getString("description"), 
										 "fromDate", DateUtil.getDateMonthYearDesc(cal)), locale));
				}
			}
			GenericValue deleteRecord = delegator.findOne("PartyInsuranceDeclaration", UtilMisc.toMap("partyId", partyId, "insuranceDelarationId", insuranceDelarationId), false);
			if(deleteRecord != null){
				List<GenericValue> partyInsDeclInsType = delegator.findByAnd("PartyInsuranceDeclInsuranceType", UtilMisc.toMap("partyId", partyId, "insuranceDelarationId", insuranceDelarationId), null, false);
				for(GenericValue tempGv: partyInsDeclInsType){
					tempGv.remove();
				}
				deleteRecord.remove();
			}
			//delete thruDate of PartyParticipateInsurance in current period (have thruDate is null)
			conds.clear();
			conds.add(EntityCondition.makeCondition("partyId", partyId));
			conds.add(EntityCondition.makeCondition("thruDate", null));
			List<GenericValue> partyParticipateInsuranceDelete = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue tempGv: partyParticipateInsuranceDelete){
				tempGv.remove();
			}
			
			//update thruDate of PartyParticipateInsurance in prev period
			List<GenericValue> insuranceTypeListGv = delegator.findByAnd("InsuranceType", null, null, false);			
			for(GenericValue insuranceType: insuranceTypeListGv){
				List<GenericValue> updateEttList = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(
						EntityCondition.makeCondition("partyId", partyId),
						EntityJoinOperator.AND,
						EntityCondition.makeCondition("insuranceTypeId", insuranceType.getString("insuranceTypeId"))), null, UtilMisc.toList("-thruDate"), null, false);
				if(UtilValidate.isNotEmpty(updateEttList)){
					GenericValue updateEtt = updateEttList.get(0);
					updateEtt.set("thruDate", null);
					updateEtt.store();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> updatePartyInsuranceDeclaration(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String insuranceDelarationId = (String)context.get("insuranceDelarationId");
		String partyId = (String)context.get("partyId");
		String insuranceSocialNbr = (String)context.get("insuranceSocialNbr");
		try {
			if(insuranceSocialNbr != null){
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				person.set("insuranceSocialNbr", insuranceSocialNbr);
				person.store();
			}
			GenericValue partyInsuranceDeclaration = delegator.findOne("PartyInsuranceDeclaration", UtilMisc.toMap("partyId", partyId, "insuranceDelarationId", insuranceDelarationId), false);
			partyInsuranceDeclaration.setNonPKFields(context);
			partyInsuranceDeclaration.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "updateSuccessfully", locale));
	}
	
	public static Map<String, Object> createPartyParticipateInsurance(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();		
		GenericValue partyParticipateInsurance = delegator.makeValue("PartyParticipateInsurance");
		partyParticipateInsurance.setAllFields(context, false, null, null);
		try {
			//TODO check before create
			partyParticipateInsurance.create();
			//update thruDate for partyParticipateInsurance
			InsuranceHelper.updatePartyParticipateInsuranceAfterCreate(dctx, context, partyParticipateInsurance);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInsuranceHealthList(DispatchContext dctx, Map<String, Object> context){
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
			Set<String> selectField = FastSet.newInstance();
			selectField.add("partyId");
			selectField.add("fullName");
			selectField.add("insHealthCard");
			selectField.add("fromDate");
			selectField.add("thruDate");
			selectField.add("stateProvinceGeoId");
			selectField.add("hospitalId");
			selectField.add("hospitalName");
			selectField.add("hospitalCode");
			listIterator = delegator.find("PartyHealthInsuranceAndHospitalPerson", tmpCond, null, selectField, listSortFields, opts);
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
				conds.add(PayrollEntityConditionUtils.makeDateConds(tmpFromDate, tmpThruDate));
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
		GenericValue partyHealthInsurance = delegator.makeValue("PartyHealthInsurance");
		partyHealthInsurance.setAllFields(context, false, null, null);
		try {
			partyHealthInsurance.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
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
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "NotFoundRecordToDelete", locale));
			}
			partyHealthInsurance.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
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
	
	public static Map<String, Object> getListEmplInsuranceOverview(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = null, thruDate = null;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		successResult.put("listIterator", listReturn);
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
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityJoinOperator.OR,
											EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
		//FIXME hard fix insuranceTypeId is BHXH
		conds.add(EntityCondition.makeCondition("insuranceTypeId", "BHXH"));
		try {
			List<GenericValue> partyParticipateInsuranceList = delegator.findList("PartyParticipateInsurance", EntityCondition.makeCondition(conds), null, UtilMisc.toList("partyId", "-fromDate"), null, false);
			for(GenericValue tempGv: partyParticipateInsuranceList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String partyId = tempGv.getString("partyId");
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				List<GenericValue> emplPosList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				if(emplPosList.size() > 0){
					tempMap.put("emplPositionTypeId", emplPosList.get(0).getString("emplPositionTypeId"));
				}
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				List<String> dept = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				tempMap.put("partyGroupId", StringUtils.join(dept, ", "));
				tempMap.put("insuranceSocialNbr", person.getString("insuranceSocialNbr"));
				tempMap.put("statusId", tempGv.getString("statusId"));
				tempMap.put("fromDate", tempGv.getTimestamp("fromDate").getTime());
				BigDecimal insuranceSalary = InsuranceHelper.getPartyInsuranceSocialSalary(delegator, partyId, fromDate, thruDate);
				tempMap.put("insuranceSalary", insuranceSalary);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return successResult;
	}
	
	public static Map<String, Object> createNewInsuranceAllowancePaymentDecl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		List<GenericValue> listinsAllowancePaymentDecl;
		try {
			listinsAllowancePaymentDecl = delegator.findByAnd("InsuranceAllowancePaymentDecl", 
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId), UtilMisc.toList("-sequenceNum"), false);
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
		}
		return retMap;
	}
	
	public static Map<String, Object> getPartyInsuranceAllowancePayment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		successResult.put("listIterator", listReturn);
		String insAllowancePaymentDeclId = request.getParameter("insAllowancePaymentDeclId");
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		try {
			List<GenericValue> insAllowancePaymentDeclList = delegator.findByAnd("PartyInsuranceAllowancePayment", UtilMisc.toMap("insAllowancePaymentDeclId", insAllowancePaymentDeclId), null, false);
			for(GenericValue tempGv: insAllowancePaymentDeclList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				String partyId = tempGv.getString("partyId");
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
				tempMap.put("insuranceSocialNbr", person.getString("insuranceSocialNbr"));
				tempMap.put("insAllowancePaymentDeclId", tempGv.getString("insAllowancePaymentDeclId"));
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("benefitTypeId", tempGv.getString("benefitTypeId"));
				tempMap.put("insuranceParticipatePeriod", tempGv.getString("insuranceParticipatePeriod"));
				tempMap.put("insuranceSalary", tempGv.get("insuranceSalary"));
				tempMap.put("statusConditionBenefit", tempGv.get("statusConditionBenefit"));
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
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
		return successResult;
	}
	
	public static Map<String, Object> createPartyInsuranceAllowancePaymentDecl(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue partyInsuranceAllowancePayment = delegator.makeValue("PartyInsuranceAllowancePayment");
		partyInsuranceAllowancePayment.setAllFields(context, false, null, null);
		try {
			partyInsuranceAllowancePayment.create();			
		} catch (GenericEntityException e) {
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
		String folder = "/hrmdoc/excelTemplate";
		String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
		String insuranceContentTypeId = (String)context.get("contentTypeId");
		List<GenericValue> listHrmAdminUserLogin;
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			listHrmAdminUserLogin = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", hrmAdmin), null, false);
			GenericValue hrmAdminUserLogin = EntityUtil.getFirst(listHrmAdminUserLogin);
			Map<String, Object> uploadedFileCtx = FastMap.newInstance();
			uploadedFileCtx.put("uploadedFile", documentFile);
			uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
			uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
			uploadedFileCtx.put("folder", folder);
			uploadedFileCtx.put("public", "Y");
			uploadedFileCtx.put("userLogin", hrmAdminUserLogin);
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
	        	dispatcher.runSync("createInsuranceContent", UtilMisc.toMap("contentId", contentId, "insuranceContentTypeId", insuranceContentTypeId, "userLogin", hrmAdminUserLogin));
	        }
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> geInsuranceContent(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
    	int page = Integer.parseInt(parameters.get("pagenum")[0]);
    	int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			List<GenericValue> insuranceContentList = delegator.findList("InsuranceContent", tmpCond, null, listSortFields, opts, false);
			totalRows = insuranceContentList.size();
			if(end > totalRows){
				end = totalRows;
			}
			insuranceContentList = insuranceContentList.subList(start, end);
			for(GenericValue tempGv: insuranceContentList){
				Map<String, Object> tempMap = FastMap.newInstance();
				listReturn.add(tempMap);
				tempMap.put("insuranceContentTypeId", tempGv.getString("insuranceContentTypeId"));
				tempMap.put("fromDate", tempGv.getTimestamp("fromDate").getTime());
				String contentId = tempGv.getString("contentId");
				tempMap.put("contentId", contentId);
				GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				if(UtilValidate.isNotEmpty(content)){
					tempMap.put("contentName", content.getString("contentName"));
					String dataResourceId = content.getString("dataResourceId");
					GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
					tempMap.put("path", dataResource.getString("objectInfo"));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listReturn);
		successResult.put("TotalRows", String.valueOf(totalRows));
		return successResult;
	}
	
	public static Map<String, Object> createInsuranceContent(DispatchContext dctx, Map<String, Object> context){
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
	}
}
