package com.olbius.employment.services;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

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
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.DateUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class EmploymentServices {
	public static final String module = EmploymentServices.class.getName();
	public static final String resource = "hrolbiusUiLabels";
	public static final String resourceNoti = "NotificationUiLabels";
	public static final int maxPunishmentLevel = 2;
	public static int transactionTimeout = 3000;
	
	/**
	 * 1. Create PartyRole if not exist
	 * 2. Create Agreement
	 * 3. Create Position Fulfillment
	 * 4. Create Employment
	 * @param dpcx
	 * @param context
	 * @return
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	//FIXME Create Employment Agreement Yet
	public static Map<String, Object> createEmplAgreement(DispatchContext dpcx, Map<String, ? extends Object> context){
		//Get Delegator
		Delegator delegator = dpcx.getDelegator();
		//Get LocalDispatcher
		LocalDispatcher dispatcher = dpcx.getDispatcher();
		//Get Parameters
		String emplId = (String) context.get("partyIdTo");
		String partyIdFrom = (String)context.get("partyIdFrom");
		Locale locale = (Locale) context.get("locale");
		String agreementTypeId = (String)context.get("agreementTypeId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String agreementId = (String) context.get("agreementId");
		Timestamp agreementDate = (Timestamp)context.get("agreementDate");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String partyIdFromRepresent = (String)context.get("partyIdFromRepresent");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> agreementCtx = FastMap.newInstance();
		agreementCtx.putAll(context);
		if(agreementDate == null){
			agreementDate = UtilDateTime.nowTimestamp();
			agreementCtx.put("agreementDate", agreementDate);
		}
		if(fromDate != null && fromDate.before(agreementDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "HrolbiusRequiredValueGreatherAgreementDate", locale));
		}
		if(fromDate != null && thruDate != null && thruDate.before(fromDate)){
			return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ThruDateMustGreaterThanFromDate", locale));
		}
		// String roleTyleIdTo = (String) context.get("roleTyleIdTo");
		//final String ROLE_TYPE_ID_FROM = "INTERNAL_ORGANIZATIO";
		
		final String ROLE_TYPE_ID_TO = "EMPLOYEE";
		// Run Service createAgreement
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> createAgrResult;
		try {
			GenericValue empl = delegator.findOne("Person", UtilMisc.toMap("partyId", emplId), false);
			GenericValue partyFrom = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFrom), false);
			GenericValue partyFromRepresent = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFromRepresent), false);
			if(empl == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindParty", UtilMisc.toMap("partyId", emplId), locale));
			}
			if(partyFrom == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindPartyGroup", UtilMisc.toMap("partyId", partyIdFrom), locale));
			}
			if(partyFromRepresent == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindParty", UtilMisc.toMap("partyId", partyIdFromRepresent), locale));
			}
			// Check Agreement is already exists
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if(agreement != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "alreadyExists", locale));
			}		
			//Create PartyRole If not Exist
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", emplId, "roleTypeId", ROLE_TYPE_ID_TO, "userLogin", context.get("userLogin")));
			createAgrResult = dispatcher.runSync("createAgreement", ServiceUtil.setServiceFields(dispatcher, "createAgreement", agreementCtx, userLogin, timeZone, locale));
			agreementId = (String)createAgrResult.get("agreementId");
			result.put("agreementId", agreementId);			
			if(partyIdFromRepresent != null){
				dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyIdFromRepresent, "roleTypeId", "REPRESENT_PARTY_FROM", "userLogin", context.get("userLogin")));	
				dispatcher.runSync("createAgreementRole", UtilMisc.toMap("agreementId", agreementId, "roleTypeId", "REPRESENT_PARTY_FROM", "partyId", partyIdFromRepresent, "userLogin", context.get("userLogin")));
			}
			//create agreement term
			List<GenericValue> allTerm = FastList.newInstance();
			//set all term of employment or trial term
			if("EMPLOYMENT_AGREEMENT".equals(agreementTypeId)){
				allTerm = getAllTerm(dpcx, "EMPLOYEMENT_TERM");
			}else if("TRIAL_AGREEMENT".equals(agreementTypeId)){
				allTerm = getAllTerm(dpcx, "PROBATION_TERM");
			}
			
			for(GenericValue termType: allTerm){
				String termTypeId = termType.getString("termTypeId");
				if(termTypeId.equals("EMPL_POSITION")){
					dispatcher.runSync("createAgreementTerm", UtilMisc.toMap("agreementId", agreementId, 
							"emplPositionId", context.get("emplPositionId"), 
							"termTypeId", termTypeId, 
							"fromDate", context.get("fromDate"),
							"thruDate", context.get("thruDate"),
							"userLogin", userLogin));
				}else{
					//check whether text value of term type pass from context
					String textValue = (String)context.get(termTypeId);
					//get defaultValue of termType's textValue
					GenericValue termTypeAttr = delegator.findOne("TermTypeAttr", UtilMisc.toMap("termTypeId", termTypeId, "attrName", "defaultValue"), false);
					if(textValue == null && termTypeAttr != null){
						textValue = termTypeAttr.getString("attrValue");
					}
					if(textValue != null){
						dispatcher.runSync("createAgreementTermHR", UtilMisc.toMap("agreementId", agreementId, 
																				"textValue", textValue, 
																				"termTypeId", termTypeId, 
																				"fromDate", context.get("fromDate"),
																				"thruDate", context.get("thruDate"),
																				"userLogin", userLogin));
					}	
				}
			}
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		
		/*if(ServiceUtil.isSuccess(createAgrResult)){
			//If Create Agreement Successfully
			String emplPositionId = (String)context.get("emplPositionId");
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			
			//Get Organization Unit Where Employee work
			GenericValue emplPosition = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId),false);
			String orgUnitId = (String) emplPosition.get("partyId");
			
			//Create Employment
			
			Map<String, Object> employmentCtx = FastMap.newInstance();
			employmentCtx.put("fromDate", fromDate);
			employmentCtx.put("partyIdFrom", orgUnitId);
			employmentCtx.put("partyIdTo", emplId);
			employmentCtx.put("roleTypeIdFrom", ROLE_TYPE_ID_FROM);
			employmentCtx.put("roleTypeIdTo", ROLE_TYPE_ID_TO);
			employmentCtx.put("thruDate", thruDate);
			employmentCtx.put("userLogin", context.get("userLogin"));
			employmentCtx.put("locale", context.get("locale"));
			localDisp.runSync("createEmployment", employmentCtx);
			
			//Create Position Fulfillment
			Map<String, Object> positionFulCtx = FastMap.newInstance();
			positionFulCtx.put("emplPositionId", emplPositionId);
			positionFulCtx.put("fromDate", fromDate);
			positionFulCtx.put("thruDate", thruDate);
			positionFulCtx.put("partyId", emplId);
			positionFulCtx.put("userLogin", context.get("userLogin"));
			positionFulCtx.put("locale", context.get("locale"));
			localDisp.runSync("createEmplPositionFulfillment", positionFulCtx);
		}*/
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
		return result;
	}
	
	public static List<GenericValue> getAllTerm(DispatchContext dctx,
			String parentTermTypeId) throws GenericEntityException {
		// TODO Auto-generated method stub
		Delegator delegator = dctx.getDelegator();		
		List<GenericValue> retList = FastList.newInstance();
		List<GenericValue> allChildTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", parentTermTypeId), null, false);
		for(GenericValue tempTermType: allChildTermType){
			retList.add(tempTermType);
			List<GenericValue> tempChildTermType = delegator.findByAnd("TermType", UtilMisc.toMap("parentTypeId", tempTermType.getString("termTypeId")), null, false);
			if(UtilValidate.isNotEmpty(tempChildTermType)){
				retList.addAll(getAllTerm(dctx, tempTermType.getString("termTypeId")));
			}
		}
		return retList;
	}

	public static Map<String, Object> deleteEmplAgreement(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		try {
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if(agreement == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindAgreementToDelete", UtilMisc.toMap("agreementId", agreementId), locale));
			}
			agreement.set("thruDate", UtilDateTime.nowTimestamp());
			agreement.set("statusId", "AGREEMENT_CANCELLED");
			agreement.store();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	public static Map<String, Object> createAgreementTerm(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue agreementTerm = delegator.makeValue("AgreementTerm");
		String agreementTermId = (String)context.get("agreementTermId");		
		agreementTerm.setAllFields(context, false, null, null);
		if(agreementTermId == null){
			agreementTermId = delegator.getNextSeqId("AgreementTerm");
			agreementTerm.set("agreementTermId", agreementTermId);
		}
		try {
			agreementTerm.create();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}		
	
	/*public static Map<String, Object> createTrialAgreementAndTerms(DispatchContext dpcx, Map<String, ? extends Object> context)
			throws GenericServiceException, GenericEntityException {
		
		//Get Delegator
		Delegator delegator = dpcx.getDelegator();
		
		//Get LocalDispatcher
		LocalDispatcher localDisp = dpcx.getDispatcher();
		
		//Get Parameters
		String partyIdTo = (String) context.get("partyIdTo");
		Locale locale = (Locale) context.get("locale");
		
		// Run Service createAgreement
		Map<String, Object> createAgrResult = localDisp.runSync("createAgreement", context);
			
		if(ServiceUtil.isSuccess(createAgrResult)){
			//Create position term
			String emplPositionId = (String)context.get("emplPositionId");
			GenericValue emplPos = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId), false);
			Timestamp fromDate = (Timestamp)context.get("fromDate");
			Timestamp thruDate = (Timestamp)context.get("thruDate");
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			
			Map<String, Object> posTermCtx = FastMap.newInstance();
			posTermCtx.put("fromDate", fromDate);
			posTermCtx.put("termTypeId", "EMPL_POSITION");
			posTermCtx.put("thruDate", thruDate);
			posTermCtx.put("agreementId", createAgrResult.get("agreementId"));
			posTermCtx.put("userLogin", userLogin);
			posTermCtx.put("textValue", emplPositionId);
			
			localDisp.runAsync("createAgreementTerm", posTermCtx);
			
			//Create Trial Salary Rate term
			Double trialSalaryRate = (Double)context.get("trialSalaryRate");
			if(trialSalaryRate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "checkNull", locale));
			}
			Map<String, Object> salRateTermCtx = FastMap.newInstance();
			salRateTermCtx.put("fromDate", fromDate);
			salRateTermCtx.put("thruDate", thruDate);
			salRateTermCtx.put("termTypeId", "TRIAL_SALARY_TERM");
			salRateTermCtx.put("agreementId", createAgrResult.get("agreementId"));
			salRateTermCtx.put("userLogin", userLogin);
			salRateTermCtx.put("termValue", BigDecimal.valueOf(trialSalaryRate));
			localDisp.runAsync("createAgreementTerm", salRateTermCtx);
			
			//Create payroll parameters
			Map<String, Object> assignPara1Ctx = FastMap.newInstance();
			assignPara1Ctx.put("code", "TI_LE_THU_VIEC");
			assignPara1Ctx.put("fromDate", fromDate);
			assignPara1Ctx.put("thruDate", thruDate);
			assignPara1Ctx.put("periodTypeId", "MONTHLY");
			assignPara1Ctx.put("type", "CONSTPERCENT");
			assignPara1Ctx.put("partyId", partyIdTo);
			assignPara1Ctx.put("userLogin", userLogin);
			assignPara1Ctx.put("value", trialSalaryRate.toString());
			localDisp.runAsync("assignEmployeePayrollParameters", assignPara1Ctx);
			
			//Create payroll parameters
			//Create RateAmount
			GenericValue rateAmount = delegator.makeValue("RateAmount");
			rateAmount.put("rateTypeId", "AVERAGE_PAY_RATE");
			rateAmount.put("rateCurrencyUomId", "VND");
			rateAmount.put("periodTypeId", "MONTHLY");
			rateAmount.put("workEffortId", "_NA_");
			rateAmount.put("partyId", partyIdTo);
			rateAmount.put("emplPositionTypeId", emplPos.getString("emplPositionTypeId"));
			rateAmount.put("fromDate", fromDate);
			rateAmount.put("thruDate", thruDate);
			if(BigDecimal.valueOf((Double)context.get("salary")) == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "checkNull", locale));
			}
			rateAmount.put("rateAmount", BigDecimal.valueOf((Double)context.get("salary")));
			GenericValue rateAmountExits = delegator.findOne("RateAmount", UtilMisc.toMap("rateTypeId", "AVERAGE_PAY_RATE", "rateCurrencyUomId", "VND", "periodTypeId", "MONTHLY", "workEffortId", "_NA_", "partyId", partyIdTo, "emplPositionTypeId", emplPos.getString("emplPositionTypeId"), "fromDate", fromDate),false);
			if(rateAmountExits != null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "alreadyExists", locale));
			}
			else{
				rateAmount.create();
			}
			Map<String, Object> assignPara2Ctx = FastMap.newInstance();
			assignPara2Ctx.put("code", "LUONG_CO_BAN");
			assignPara2Ctx.put("fromDate", fromDate);
			assignPara2Ctx.put("thruDate", thruDate);
			assignPara2Ctx.put("periodTypeId", "MONTHLY");
			assignPara2Ctx.put("type", "REF");
			assignPara2Ctx.put("partyId", partyIdTo);
			assignPara2Ctx.put("userLogin", userLogin);
			String value = "RateAmount.partyId[" + partyIdTo + "](rateAmount)";
			assignPara2Ctx.put("value", value);
			localDisp.runAsync("assignEmployeePayrollParameters", assignPara2Ctx);
			
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
		return result;
	}*/
	
	public static Map<String, Object> createFullEmployment(DispatchContext dpcx, Map<String, ? extends Object> context)
			throws Exception {
		
		//Get Delegator
		Delegator delegator = dpcx.getDelegator();
		
		//Get LocalDispatcher
		LocalDispatcher localDisp = dpcx.getDispatcher();
		
		//Get Parameters
		String emplId = (String) context.get("partyIdTo");
		Locale locale = (Locale) context.get("locale");
		
		//Const
		final String ROLE_TYPE_ID_FROM = "INTERNAL_ORGANIZATIO";
		final String ROLE_TYPE_ID_TO = "EMPLOYEE";
		
		//Create PartyRole If not Exist
		Map<String, Object> partyRoleCtx = FastMap.newInstance();
		partyRoleCtx.put("partyId", emplId);
		partyRoleCtx.put("roleTypeId", ROLE_TYPE_ID_TO);
		partyRoleCtx.put("userLogin", context.get("userLogin"));
		partyRoleCtx.put("locale", context.get("locale"));
		localDisp.runSync("createPartyRole", partyRoleCtx);
		
		String emplPositionTypeId = (String)context.get("emplPositionTypeId");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
			
		//Create Employment
		Map<String, Object> employmentCtx = FastMap.newInstance();
		employmentCtx.put("fromDate", fromDate);
		employmentCtx.put("partyIdFrom", context.get("partyIdFrom"));
		employmentCtx.put("partyIdTo", emplId);
		employmentCtx.put("roleTypeIdFrom", ROLE_TYPE_ID_FROM);
		employmentCtx.put("roleTypeIdTo", ROLE_TYPE_ID_TO);
		employmentCtx.put("thruDate", thruDate);
		employmentCtx.put("userLogin", context.get("userLogin"));
		employmentCtx.put("locale", context.get("locale"));
		localDisp.runSync("createEmployment", employmentCtx);
		
		//Create PartyRelatioship
		Map<String, Object> partyRelaCtx = FastMap.newInstance();
		partyRelaCtx.put("fromDate", fromDate);
		partyRelaCtx.put("partyIdFrom", context.get("partyIdFrom"));
		partyRelaCtx.put("partyIdTo", emplId);
		partyRelaCtx.put("roleTypeIdFrom", ROLE_TYPE_ID_FROM);
		partyRelaCtx.put("roleTypeIdTo", ROLE_TYPE_ID_TO);
		partyRelaCtx.put("thruDate", thruDate);
		partyRelaCtx.put("userLogin", context.get("userLogin"));
		partyRelaCtx.put("locale", context.get("locale"));
		partyRelaCtx.put("partyRelationshipTypeId", "EMPLOYMENT");
		localDisp.runSync("createPartyRelationship", partyRelaCtx);
		
		//Create Position Fulfillment
		Map<String, Object> positionFulCtx = FastMap.newInstance();
		positionFulCtx.put("emplPositionTypeId", emplPositionTypeId);
		positionFulCtx.put("actualFromDate", fromDate);
		positionFulCtx.put("actualThruDate", thruDate);
		positionFulCtx.put("partyId", emplId);
		positionFulCtx.put("internalOrgId", context.get("partyIdFrom"));
		positionFulCtx.put("userLogin", context.get("userLogin"));
		positionFulCtx.put("locale", context.get("locale"));
		Map<String, Object> runResult = localDisp.runSync("createEmplPositionAndFulfillment", positionFulCtx);
		
		//Send Notification to create empl agreement
		Map<String, Object> notiCtx = FastMap.newInstance();
		notiCtx.put("header", "Tạo hợp đồng thử việc");
		String targetLink = "partyId=" + emplId +";"+"emplPositionId="+runResult.get("emplPositionId")
							+";" +"salary="+context.get("salary")+";"+"allowance=" + context.get("allowance") +";" + "trialSalaryRate="+context.get("trialSalaryRate")
							+";" + "fromDate="+context.get("fromDate")+";" +"thruDate="+context.get("thruDate");
		notiCtx.put("targetLink", targetLink);
		notiCtx.put("action", "EditTrialAgreement");
		notiCtx.put("state", "open");
		notiCtx.put("dateTime", new Timestamp(new Date().getTime()));
		notiCtx.put("partyId", PartyUtil.getHrmAdmin(delegator));
		notiCtx.put("userLogin", context.get("userLogin"));
		localDisp.runAsync("createNotification", notiCtx);
		
		Map<String, Object> result = FastMap.newInstance();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(resourceNoti, "createSuccessfully", locale));
		return result;
	}
	
	public static Map<String, Object> getEmplPosition(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String partyId = (String)context.get("partyId");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> retMap = FastMap.newInstance();
		retMap.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		retMap.put(ModelService.SUCCESS_MESSAGE, ModelService.SUCCESS_MESSAGE);
		try {			
			List<GenericValue> emplPosList = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
			if(UtilValidate.isNotEmpty(emplPosList)){
				GenericValue emplPos = EntityUtil.getFirst(emplPosList);
				retMap.put("emplPositionId", emplPos.getString("emplPositionId"));
				retMap.put("emplPositionTypeId", emplPos.getString("emplPositionTypeId"));
				GenericValue emplPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPos.getString("emplPositionTypeId")), false);
				retMap.put("emplPositionTypeDesc", emplPosType.getString("description"));
			}else{
				ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "EmployeeNotOccupyPosition", locale));
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> createRequestDisciplineProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("EmploymentUiLabels", "createRequestDisciplineSuccessful", locale));
		String partyId = (String)context.get("partyId");
		String approverIdProposal = (String)context.get("approverIdProposal");
		String comment = (String)context.get("comment");
		try {
			Map<String, Object> resultService = dispatcher.runSync("createEmplProposal", UtilMisc.toMap("emplProposalTypeId", "REQUEST_DISCIPLINE", "statusId", "PPSL_CREATED", "comment", comment, "userLogin", userLogin));
			String emplProposalId = (String)resultService.get("emplProposalId");	
			retMap.put("emplProposalId", emplProposalId);
			dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyId, "roleTypeId", "PPSL_DISCIPLINED", "userLogin", userLogin));
			dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", approverIdProposal, "roleTypeId", "PPSL_DECIDER", "userLogin", userLogin));
			String emplProposer = PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), true);
			//notify to approver			
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("partyId", approverIdProposal);
			ntfCtx.put("state", "open");
			ntfCtx.put("action", "ApprovalRequestDisciplineProposal");
			ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "ReviewRequestDisciplineProposal", 
											UtilMisc.toMap("emplProposer", emplProposer, 
															"disciplined", PartyHelper.getPartyName(delegator, partyId, true)), locale));
			dispatcher.runSync("createNotification", ntfCtx);
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("partyId", partyId);
			ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "RequestDisciplineProposalNotify", 
					UtilMisc.toMap("emplProposer", emplProposer), locale));
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} 
		return retMap;
	}
	
	public static Map<String, Object> updateRequestDisciplineProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		String emplProposalId = (String)context.get("emplProposalId");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		try {
			
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindProposal", UtilMisc.toMap("emplProposalId", emplProposalId), locale));
			}
			String currStatus = emplProposal.getString("statusId");
			if("PPSL_REJECTED".equals(currStatus) || "PPSL_ACCEPTED".equals(currStatus)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatus), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotApprovalProposalDone", UtilMisc.toMap("status", statusItem.getString("description")), locale));
			}
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, timeZone, locale));
			Map<String, Object> resultsService = FastMap.newInstance();
			String partyIdApprover = userLogin.getString("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			conditions.add(EntityCondition.makeCondition("partyId", partyIdApprover));
			EntityCondition commonConds = EntityCondition.makeCondition(conditions);
			List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove)), null, null, null, false);
			
			List<GenericValue> allPartyApproved = delegator.findList("EmplProposalApprovalAndRoleType", commonConds, null, null, null, false);
			for(GenericValue tempGv: emplProposalRoleType){
				List<GenericValue> partyApproverProposal = EntityUtil.filterByCondition(allPartyApproved, EntityCondition.makeCondition("roleTypeId", tempGv.getString("roleTypeId"))); 
				if(UtilValidate.isNotEmpty(partyApproverProposal)){
					for(GenericValue tempApprPpsl: partyApproverProposal){
						resultsService = dispatcher.runSync("updateEmplProposalApproval", UtilMisc.toMap("emplProposalApprovalId", tempApprPpsl.getString("emplProposalApprovalId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
					}
				}else{
					resultsService = dispatcher.runSync("createEmplProposalApproval", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyIdApprover, "roleTypeId", tempGv.getString("roleTypeId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
				}
			}
			String statusEmplProposal = (String)resultsService.get("statusId");
			Map<String, Object> ntfCtx= FastMap.newInstance();
			boolean doneEmplProposalProcess = false;
			boolean createProposalSackingEmpl = false;
			if("PPSL_REJECTED".equals(statusEmplProposal)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "RequestDisciplineProposalReject", UtilMisc.toMap("proposer", PartyHelper.getPartyName(delegator, partyIdApprover, false)), locale));
				doneEmplProposalProcess = true;				
			}else if("PPSL_ACCEPTED".equals(statusEmplProposal)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "RequestDisciplineProposalAccept", UtilMisc.toMap("proposer", PartyHelper.getPartyName(delegator, partyIdApprover, false)), locale));
				doneEmplProposalProcess = true;
				createProposalSackingEmpl = true;
			}
			if(doneEmplProposalProcess){
				ntfCtx.put("state", "open");
				ntfCtx.put("action", "ApprovalRequestDisciplineProposal");
				ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "ONE");
				List<GenericValue> emplProposalRoleTypeList = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), EntityOperator.AND, EntityCondition.makeCondition("emplProposalId", emplProposalId)), null, null, null, false);
				List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplProposalRoleTypeList, "partyId", true);
				partyNtf.remove(partyIdApprover);
				partyNtf.add(emplProposal.getString("partyId"));
				ntfCtx.put("partiesList", partyNtf);
				dispatcher.runSync("createNotification", ntfCtx);
				if(createProposalSackingEmpl){
					List<GenericValue> partyDisciplined = EntityUtil.filterByCondition(emplProposalRoleTypeList, EntityCondition.makeCondition("roleTypeId", "PPSL_DISCIPLINED"));
					if(UtilValidate.isNotEmpty(partyDisciplined)){
						ntfCtx.remove("partiesList");
						ntfCtx.put("action", "EditSackingProposal");
						ntfCtx.put("partyId", partyIdApprover);
						ntfCtx.put("targetLink", "proposedId=" + partyDisciplined.get(0).getString("partyId"));
						ntfCtx.put("ntfType", "ONE");
						ntfCtx.put("header", UtilProperties.getMessage("EmployeeUiLabels", "SendSackingProposal", UtilMisc.toMap("emplName",partyDisciplined.get(0).getString("partyId")),  locale));
						dispatcher.runSync("createNotification", ntfCtx);
					}
				}
			}
			
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createEmplProposalRoleType(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		GenericValue emplProposalRoleType = delegator.makeValue("EmplProposalRoleType");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		emplProposalRoleType.setAllFields(context, false, null, null);
		if(fromDate == null){
			emplProposalRoleType.set("fromDate", UtilDateTime.nowTimestamp());
		}
		try {
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", context.get("partyId"), "roleTypeId", context.get("roleTypeId"), "userLogin", systemUserLogin));
			emplProposalRoleType.create();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue emplProposal = delegator.makeValue("EmplProposal");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = FastMap.newInstance();
		String partyId = (String)context.get("partyId");
		emplProposal.setNonPKFields(context);
		String emplProposalId = delegator.getNextSeqId("EmplProposal");
		emplProposal.set("emplProposalId", emplProposalId);
		if(partyId == null){
			emplProposal.set("partyId", userLogin.getString("partyId"));
		}
		try {
			emplProposal.create();
			retMap.put("emplProposalId", emplProposalId);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEmplProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplProposalId = (String)context.get("emplProposalId");
		String approvalStatusId = (String)context.get("approvalStatusId");
		String roleTypeId = (String)context.get("roleTypeId");
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			String statusId = emplProposal.getString("statusId");
			if(!"PPSL_REJECTED".equals(statusId) && !"PPSL_ACCEPTED".equals(statusId)){
				if("APPL_PPSL_REJECTED".equals(approvalStatusId)){//if approver, confirmer, decider role approve with status is "APPL_PPSL_REJECTED", update status of emplProposal
					emplProposal.set("statusId", "PPSL_REJECTED");
					emplProposal.store();
					retMap.put("statusId", "PPSL_REJECTED");
				}else if("PPSL_DECIDER".equals(roleTypeId)){//else, only update status EmplProposal is PPSL_ACCEPTED if person approve have role DECIDER  
					emplProposal.set("statusId", "PPSL_ACCEPTED");
					emplProposal.store();
					retMap.put("statusId", "PPSL_ACCEPTED");
				}else if("PPSL_CREATED".equals(statusId)){
					emplProposal.set("statusId", "PPSL_IN_PROCESS");
					emplProposal.store();
				}
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createEmplProposalApproval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplProposalId = (String)context.get("emplProposalId");
		String partyId = (String)context.get("partyId");
		String roleTypeId = (String)context.get("roleTypeId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		if(partyId == null){
			partyId = userLogin.getString("partyId");
		}
		List<EntityCondition> commnonConditions = FastList.newInstance();
		commnonConditions.add(EntityUtil.getFilterByDateExpr());
		commnonConditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
		commnonConditions.add(EntityCondition.makeCondition("partyId", partyId));
		List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
		EntityCondition roleConds;
		if(roleTypeId != null){
			if(!roleTypeAllowedApprove.contains(roleTypeId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "NotAllowedApprovalProposal", UtilMisc.toMap("emplName", PartyHelper.getPartyName(delegator, partyId, true)), locale));
			}
			roleConds = EntityCondition.makeCondition("roleTypeId", roleTypeId);
		}else{
			roleConds = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove);
		}
		try {
			List<GenericValue> partyEmplProposalRoleType = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(EntityCondition.makeCondition(commnonConditions), EntityOperator.AND, roleConds), null, null, null, false);
			for(GenericValue tempGv: partyEmplProposalRoleType){
				GenericValue emplProposalApproval = delegator.makeValue("EmplProposalApproval");
				emplProposalApproval.setNonPKFields(context);
				if(roleTypeId == null){
					emplProposalApproval.set("roleTypeId", tempGv.getString("roleTypeId"));
				}
				String emplProposalApprovalId = delegator.getNextSeqId("EmplProposalApproval");
				emplProposalApproval.set("emplProposalApprovalId", emplProposalApprovalId);
				emplProposalApproval.set("approvalDate", UtilDateTime.nowTimestamp());
				emplProposalApproval.create();
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateEmplProposalApproval(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplProposalApprovalId = (String)context.get("emplProposalApprovalId");
		try {
			GenericValue emplProposalAppr = delegator.findOne("EmplProposalApproval", UtilMisc.toMap("emplProposalApprovalId", emplProposalApprovalId), false);
			emplProposalAppr.setNonPKFields(context);
			emplProposalAppr.store();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createNtfApprEmplProposal(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String roleTypeId = (String)context.get("roleTypeId");
		String emplProposalId = (String)context.get("emplProposalId");
		String header = (String)context.get("header");
		String action = (String)context.get("action");
		String targetLink = (String)context.get("targetLink");
		String ntfType = (String)context.get("ntfType");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
		try {
			List<GenericValue> emplProposalRole = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplProposalRole, "partyId", true);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("header", header);
			ntfCtx.put("action", action);
			ntfCtx.put("targetLink", targetLink);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("state", "open");
			ntfCtx.put("userLogin", userLogin);
			ntfCtx.put("ntfType", ntfType);
			ntfCtx.put("partiesList", partyNtf);
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createSackingProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String proposedId = (String)context.get("proposedId");
		Map<String, Object> retMap = FastMap.newInstance();
		
		try {
			Map<String, Object> emplProposalMap = ServiceUtil.setServiceFields(dispatcher, "createEmplProposal", context, userLogin, timeZone, locale);
			emplProposalMap.put("emplProposalTypeId", "SACKING_PROPOSAL");
			emplProposalMap.put("statusId", "PPSL_CREATED");
			Map<String, Object> resutlService = dispatcher.runSync("createEmplProposal", emplProposalMap);
			if(ServiceUtil.isSuccess(resutlService)){
				String emplProposalId = (String)resutlService.get("emplProposalId");
				retMap.put("emplProposalId", emplProposalId);
				GenericValue emplTerminationProposal = delegator.makeValue("EmplTerminationProposal");
				emplTerminationProposal.setNonPKFields(context);
				emplTerminationProposal.set("terminationTypeId", "FIRE");
				emplTerminationProposal.set("emplTerminationProposalId", emplProposalId);
				emplTerminationProposal.create();
				String managerOfProposed = PartyUtil.getManagerOfEmpl(delegator, proposedId);
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", managerOfProposed, 
						"roleTypeId", "PPSL_VIEWER", "userLogin", userLogin));
				
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", proposedId, 
																				"roleTypeId", "PPSL_PROPOSED", "userLogin", userLogin));
				String hrmAdmin = PartyUtil.getHrmAdmin(delegator);
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", hrmAdmin, 
						"roleTypeId", "PPSL_CONFIRMER", "userLogin", userLogin));
				String ceo = PartyUtil.getCEO(delegator);
				dispatcher.runSync("createEmplProposalRoleType", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", ceo, 
						"roleTypeId", "PPSL_DECIDER", "userLogin", userLogin));
				String proposed = PartyHelper.getPartyName(delegator, proposedId, true);
				String proposer = PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), true);
				String header = UtilProperties.getMessage("EmploymentUiLabels", "ApprovalSackingProposalEmpl", UtilMisc.toMap("proposer", proposer, "proposed", proposed), locale);
				String action = "ApprovalSackingProposal";
				String targetLink = "emplProposalId=" + emplProposalId;
				dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "roleTypeId", "PPSL_CONFIRMER", 
																				"header", header, "action", action, "targetLink", targetLink,
																				"userLogin", userLogin));
				Map<String, Object> ntfCtx = FastMap.newInstance();
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "NotifySackingProposalToEmployee", UtilMisc.toMap("proposer", proposer, "proposed", proposed), locale));
				ntfCtx.put("action", action);
				ntfCtx.put("targetLink", targetLink);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("state", "open");
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "ONE");
				ntfCtx.put("partiesList", UtilMisc.toList(proposedId, managerOfProposed));
				dispatcher.runSync("createNotification", ntfCtx);
			}
		} catch (GenericServiceException e) {
			
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			
			e.printStackTrace();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	
	public static Map<String, Object> updateApprovalSackingProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String emplProposalId = (String)context.get("emplProposalId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> resultService = FastMap.newInstance();
		try {
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindProposal", UtilMisc.toMap("emplProposalId", emplProposalId), locale));
			}
			dispatcher.runSync("updateNtfIfExists", ServiceUtil.setServiceFields(dispatcher, "updateNtfIfExists", context, userLogin, timeZone, locale));
			String currStatus = emplProposal.getString("statusId");
			if("PPSL_REJECTED".equals(currStatus) || "PPSL_ACCEPTED".equals(currStatus)){
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currStatus), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotApprovalProposalDone", UtilMisc.toMap("status", statusItem.getString("description")), locale));
			}
			
			String partyIdApprover = userLogin.getString("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			//conditions.add(EntityCondition.makeCondition("partyId", partyIdApprover));
			EntityCondition commonConds = EntityCondition.makeCondition(conditions);
			List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", commonConds, null, null, null, false); 
			
			List<GenericValue> partyEmplProposalRoleType = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition(
																													EntityCondition.makeCondition("partyId", partyIdApprover),
																													EntityOperator.AND,
																													EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove)));
			String statusIdEmplPpsl = null;
			List<GenericValue> allPartyApproved = delegator.findList("EmplProposalApprovalAndRoleType",EntityCondition.makeCondition(commonConds, EntityOperator.AND, EntityCondition.makeCondition("partyId", partyIdApprover)), null, null, null, false);
			for(GenericValue tempGv: partyEmplProposalRoleType){
				List<GenericValue> partyApproverProposal = EntityUtil.filterByCondition(allPartyApproved, EntityCondition.makeCondition("roleTypeId", tempGv.getString("roleTypeId")));
				if(UtilValidate.isNotEmpty(partyApproverProposal)){
					for(GenericValue tempApprPpsl: partyApproverProposal){
						resultService = dispatcher.runSync("updateEmplProposalApproval", UtilMisc.toMap("emplProposalApprovalId", tempApprPpsl.getString("emplProposalApprovalId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
					}
				}else{
					resultService = dispatcher.runSync("createEmplProposalApproval", UtilMisc.toMap("emplProposalId", emplProposalId, "partyId", partyIdApprover, "roleTypeId", tempGv.getString("roleTypeId"), "approvalStatusId", context.get("approvalStatusId"), "comment", context.get("comment"), "userLogin", userLogin));
				}
				if(resultService.get("statusId") != null){
					statusIdEmplPpsl = (String)resultService.get("statusId");
				}
			}
			
			List<GenericValue> emplProposedRoleType = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition("roleTypeId", "PPSL_PROPOSED"));
			String proposedId = null;
			String proposedName = "";
			String proposer = PartyHelper.getPartyName(delegator, emplProposal.getString("partyId"), false);
			if(UtilValidate.isNotEmpty(emplProposedRoleType)){
				proposedId = emplProposedRoleType.get(0).getString("partyId");
				proposedName = PartyHelper.getPartyName(delegator, proposedId , false);
			}
			Map<String, Object> ntfCtx = FastMap.newInstance();
			boolean doneEmplProposalProcess = false;
			if("PPSL_REJECTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "SackingProposalReject", UtilMisc.toMap("proposer", proposer, "proposed", proposedName), locale));
				doneEmplProposalProcess = true;				
			}else if("PPSL_ACCEPTED".equals(statusIdEmplPpsl)){
				ntfCtx.put("header", UtilProperties.getMessage("EmploymentUiLabels", "SackingProposalAccept", UtilMisc.toMap("proposer", proposer, "proposed", proposedName), locale));
				doneEmplProposalProcess = true;
			}
			if(doneEmplProposalProcess){
				ntfCtx.put("state", "open");
				ntfCtx.put("action", "ApprovalSackingProposal");
				ntfCtx.put("targetLink", "emplProposalId=" + emplProposalId);
				ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
				ntfCtx.put("userLogin", userLogin);
				ntfCtx.put("ntfType", "ONE");
				List<String> partyNtf = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
				partyNtf.add(emplProposal.getString("partyId"));
				ntfCtx.put("partiesList", partyNtf);
				dispatcher.runSync("createNotification", ntfCtx);
			}else{
				resultService = dispatcher.runSync("getNextRoleTypeLevelApprProposal", UtilMisc.toMap("emplProposalId", emplProposalId, "userLogin", userLogin));
				String roleTypeId = (String)resultService.get("roleTypeId");
				if(roleTypeId != null){
					String header = UtilProperties.getMessage("EmploymentUiLabels", "ApprovalSackingProposalEmpl", UtilMisc.toMap("proposer", proposer, "proposed", proposedName), locale);
					dispatcher.runSync("createNtfApprEmplProposal", UtilMisc.toMap("roleTypeId", roleTypeId, "emplProposalId", emplProposalId, "header", header, 
																					"targetLink", "emplProposalId=" + emplProposalId, "action", "ApprovalSackingProposal",
																					"userLogin", userLogin));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} catch (GeneralServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "approveSuccessfully", locale)); 
	}
	
	public static Map<String, Object> getNextRoleTypeLevelApprProposal(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String emplProposalId = (String)context.get("emplProposalId");
		List<String> roleTypeAllowedApprove = UtilMisc.toList("PPSL_APPROVER", "PPSL_CONFIRMER", "PPSL_DECIDER");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
		conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeAllowedApprove));
		EntityCondition commonConds = EntityCondition.makeCondition(conditions);
		Map<String, Object> retMap = FastMap.newInstance();
		try {
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", commonConds, null, null, null, false);
			List<String> allRoleType = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "roleTypeId", true);
			List<GenericValue> allPartyApprovedEmplProposal = delegator.findList("EmplProposalApprovalAndRoleType", commonConds, null, null, null, false);
			//party have PPSL_APPROVER role approved proposal
			List<GenericValue> partyApproverRoleApproved = EntityUtil.filterByCondition(allPartyApprovedEmplProposal, EntityCondition.makeCondition("roleTypeId","PPSL_APPROVER"));
			//party have PPSL_CONFIRMER role approved proposal
			List<GenericValue> partyConfirmerRoleApproved = EntityUtil.filterByCondition(allPartyApprovedEmplProposal, EntityCondition.makeCondition("roleTypeId","PPSL_CONFIRMER"));
			//party have PPSL_DECIDER role approved proposal
			List<GenericValue> partyDeciderRoleApproved = EntityUtil.filterByCondition(allPartyApprovedEmplProposal, EntityCondition.makeCondition("roleTypeId","PPSL_DECIDER"));
			
			if(UtilValidate.isEmpty(partyApproverRoleApproved) && allRoleType.contains("PPSL_APPROVER")){
				//if all party role PPSL_APPROVER not approved, then return "PPSL_APPROVER" 
				retMap.put("roleTypeId", "PPSL_APPROVER");
			}else{
				//party have PPSL_APPROVER role haven't approved
				List<GenericValue> partyApproverRoleNotApproved = null;
				if(UtilValidate.isNotEmpty(partyApproverRoleApproved)){
					List<String> partyApproverRoleApprovedId = EntityUtil.getFieldListFromEntityList(partyApproverRoleApproved, "partyId", true);
					partyApproverRoleNotApproved = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", "PPSL_APPROVER"), 
																																	EntityOperator.AND ,
																																	EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyApproverRoleApprovedId)));
				}				
				if(UtilValidate.isEmpty(partyApproverRoleNotApproved)){
					if(UtilValidate.isEmpty(partyConfirmerRoleApproved) && allRoleType.contains("PPSL_CONFIRMER")){
						retMap.put("roleTypeId", "PPSL_CONFIRMER");
					}else{
						//party have PPSL_CONFIRMER role haven't approved
						List<GenericValue> partyConfirmerRoleNotApproved = null;
						if(UtilValidate.isNotEmpty(partyConfirmerRoleApproved)){
							List<String> partyConfirmerRoleApprovedId = EntityUtil.getFieldListFromEntityList(partyConfirmerRoleApproved, "partyId", true);
							partyConfirmerRoleNotApproved = EntityUtil.filterByCondition(emplProposalRoleType, EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", "PPSL_CONFIRMER"), 
																																			EntityOperator.AND,
																																			EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyConfirmerRoleApprovedId)));
						}
						if(UtilValidate.isEmpty(partyConfirmerRoleNotApproved) && UtilValidate.isEmpty(partyDeciderRoleApproved)){
							retMap.put("roleTypeId", "PPSL_DECIDER");
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return retMap;
	}
	public static Map<String, Object> removeEmplFromOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String emplProposalId = (String)context.get("emplProposalId");
		String disableUserLogin = (String)context.get("disableUserLogin");
		String expireEmpl = (String)context.get("expireEmpl");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String message = "";
		try {
			GenericValue emplProposal = delegator.findOne("EmplProposal", UtilMisc.toMap("emplProposalId", emplProposalId), false);
			if(emplProposal == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "CannotFindProposal", UtilMisc.toMap("emplProposalId", emplProposalId), locale));
			}
			if(!userLogin.getString("partyId").equals(emplProposal.getString("partyId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "YouHaveNotPermission", locale));
			}
			if(!"PPSL_ACCEPTED".equals(emplProposal.getString("statusId"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ProposalNotAccepted", locale));
			}
			GenericValue emplProposalTermination = delegator.findOne("EmplTerminationProposal", UtilMisc.toMap("emplTerminationProposalId", emplProposalId), false);
			Timestamp dateTermination = emplProposalTermination.getTimestamp("dateTermination");
			if(dateTermination == null){
				dateTermination = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			}
			String isCompleteFormality = emplProposalTermination.getString("isCompleteFormality");
			if("Y".equals(isCompleteFormality)){
				return ServiceUtil.returnError(UtilProperties.getMessage("EmploymentUiLabels", "ProposalCompletedFormality", locale));
			}
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("emplProposalId", emplProposalId));
			conditions.add(EntityCondition.makeCondition("roleTypeId", "PPSL_PROPOSED"));
			List<GenericValue> emplProposalRoleType = delegator.findList("EmplProposalRoleType", EntityCondition.makeCondition(conditions), null, null, null, false);
			List<String> proposedList = FastList.newInstance();
			String proposedName = "";
			if(UtilValidate.isNotEmpty(emplProposalRoleType)){
				proposedList = EntityUtil.getFieldListFromEntityList(emplProposalRoleType, "partyId", true);
				proposedName = PartyHelper.getPartyName(delegator ,proposedList.get(0), false);
			}
			if("Y".equals(disableUserLogin)){
				List<GenericValue> userLoginList;
				for(String partyId: proposedList){
					userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId), null, false);
					for(GenericValue tempGv: userLoginList){
						tempGv.set("enabled", "N");
						tempGv.store();
					}
				}
			}
			if("Y".equals(expireEmpl)){
				//expire emplPosition
				List<GenericValue> emplPositionFul;
				List<GenericValue> employment;
				List<GenericValue> partyRel;
				List<EntityCondition> employmentConds = FastList.newInstance();
				employmentConds.add(EntityUtil.getFilterByDateExpr());
				
				for(String tempPartyId: proposedList){
					emplPositionFul = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), EntityOperator.AND, EntityCondition.makeCondition("partyId", tempPartyId)), 
																null, null, null, false);
					for(GenericValue tempGv: emplPositionFul){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
					EntityCondition tempConds = EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(), 
																				EntityOperator.AND, 
																				EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", tempPartyId), 
																												EntityOperator.OR, 
																												EntityCondition.makeCondition("partyIdFrom", tempPartyId)));
					employment = delegator.findList("Employment", tempConds, null, null, null, false);
					partyRel = delegator.findList("PartyRelationship", tempConds, null, null, null, false);
					for(GenericValue tempGv: employment){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
					for(GenericValue tempGv: partyRel){
						tempGv.set("thruDate", dateTermination);
						tempGv.store();
					}
				}
			}
			emplProposalTermination.set("isCompleteFormality", "Y");
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dateTermination.getTime());
			message = UtilProperties.getMessage("EmploymentUiLabels", "CompleteSackingEmplFormality", UtilMisc.toMap("dateTermination", cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+ 1) + "/" + cal.get(Calendar.YEAR), "proposedName", proposedName), locale);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(message);
	}
	
	/*
	 * get List Employee Leave 
	 * @param DispatchContext
	 * @param context
	 * @param return
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmployeeLeave(DispatchContext dpct,Map<String,Object> context) throws Exception{
		Delegator delegator = dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		//LocalDispatcher dispatcher = dpct.getDispatcher();
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");	
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");
		List<String> partyIds = FastList.newInstance();
		List<GenericValue> partyRoles = null;
		Map<String,Object> results = FastMap.newInstance();
		List<GenericValue> listEmpl = FastList.newInstance();
		EntityCondition mainCond = null;
		try {
			if(UtilValidate.isNotEmpty(listAllConditions)){
				mainCond = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			}
			String partyIdCeo = PartyUtil.getCEO(delegator);
			partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, false);
			List<String> partyRoleStr = EntityUtil.getFieldListFromEntityList(partyRoles, "roleTypeId", true);
			//userLogin is CEO or HeadOfHR, get all employee leave list
			if(userLogin.getString("partyId").equals(partyIdCeo) || userLogin.getString("partyId").equals(PartyUtil.getHrmAdmin(delegator))){
				if(UtilValidate.isNotEmpty(listSortFields)){
					listEmpl = delegator.findList("EmplLeave",mainCond,null,listSortFields,null,false);
				}
				listEmpl = delegator.findList("EmplLeave",mainCond,null,null,null,false);
			}else if(partyRoleStr.contains("MANAGER")){
				if(UtilValidate.isNotEmpty(partyId)){
					String managerIdOfParty = "";
					managerIdOfParty = PartyUtil.getManagerOfEmpl(delegator, partyId);
					if(userLogin.getString("partyId").equalsIgnoreCase(managerIdOfParty)){
						partyIds.add(partyId);
						context.remove("partyId");
					}else{
						return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels","ManagerNotManageEmployee",locale));
					}
				}else{
					List<GenericValue> listEmployee = FastList.newInstance();
					listEmployee = PartyUtil.getListEmployeeOfManager(delegator, userLogin.getString("partyId"));
					partyIds = EntityUtil.getFieldListFromEntityList(listEmployee, "partyIdTo", true);
				}
				if(UtilValidate.isNotEmpty(partyIds)){
					EntityCondition cond = EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,partyIds);
					List<EntityCondition> listCond = FastList.newInstance();
					if(mainCond != null){
						listCond.add(mainCond);
					}
					listCond.add(cond);
					if(UtilValidate.isNotEmpty(listSortFields)){
						listEmpl = delegator.findList("EmplLeave", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, listSortFields, null, false);
					}
					listEmpl = delegator.findList("EmplLeave", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null, null, false);
				}
			}
			if(UtilValidate.isNotEmpty(listEmpl)){
				results.put("TotalRows", String.valueOf(listEmpl.size()));
				results.put("listIterator", listEmpl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels","ManagerNotManageEmployee",locale));
			// TODO: handle exception
		}
		return results;
	}
	/*
	 * get empl list Summary
	 * @param DispatchContext
	 * @param Map<?,?> context
	 * @return
	 * 
	 * */
	public static Map<String,Object> JQgetListEmplSummary(DispatchContext dpct,Map<String,Object> context){
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = dpct.getDispatcher();
		TimeZone timeZone = TimeZone.getDefault();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dpct.getDelegator();
		List<GenericValue> ListEmployeeOfManager = FastList.newInstance();
		List<Map<String,Object>> listEmplSummary = FastList.newInstance();
		Map<String,Object> result = FastMap.newInstance();
		try {
			ListEmployeeOfManager = PartyUtil.getListEmployeeOfManager(delegator,((GenericValue) context.get("userLogin")).getString("partyId"));
			if(UtilValidate.isNotEmpty(ListEmployeeOfManager)){
				for(GenericValue empl : ListEmployeeOfManager){
					String partyId = empl.getString("partyId");
					if(UtilValidate.isNotEmpty(partyId)){
						Map<String,Object> tempMap = FastMap.newInstance();
						tempMap.put("partyId", partyId);
						
						List<GenericValue> emplPosition = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
						List<String> emplPos = FastList.newInstance();
						for(GenericValue tempPos: emplPosition){
							GenericValue emplType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", tempPos.getString("emplPositionTypeId")), false);
							emplPos.add(emplType.getString("description"));
						}
						tempMap.put("currPositionsStr", emplPos);
//						empl.put("currPositionsStr", emplPos);
						String currDept = "";
						GenericValue currDeptTmp = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
						if(UtilValidate.isNotEmpty(currDeptTmp)){
							currDept = PartyHelper.getPartyName(delegator, currDeptTmp.getString("partyIdFrom"), false);
						}else{
							currDept = "";
						}
						tempMap.put("currDept", currDept);
						//
							Timestamp fromDate = UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), timeZone, locale);
							Timestamp thruDate = UtilDateTime.getYearEnd(UtilDateTime.nowTimestamp(), timeZone, locale);
							Map<String, Object> results = FastMap.newInstance();	
							results = dispatcher.runSync("getNbrDayLeaveEmplInfo", UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate, "locale", locale, "timeZone", timeZone));
							
							tempMap.put("numberDayLeaveUnpaid", results.get("numberDayLeaveUnpaid"));
							tempMap.put("numberDayLeave", results.get("numberDayLeave"));
							tempMap.put("dayLeaveRegulation", results.get("dayLeaveRegulation"));
							tempMap.put("dayLeaveRemain", ((Long) results.get("dayLeaveRegulation")).intValue() - ((Float)results.get("numberDayLeave")).intValue());
							 
							for(int i = 0; i < 12; i++){
								
								Timestamp temFromDate = UtilDateTime.getMonthStart(fromDate, 0, i, timeZone, locale);
								Timestamp tempThruDate = UtilDateTime.getMonthEnd(temFromDate, timeZone, locale);
								results = dispatcher.runSync("getNbrDayLeaveEmp", UtilMisc.toMap("partyId", partyId, "fromDate", temFromDate, "thruDate", tempThruDate, "userLogin", userLogin));
								
								switch (i) {
									case 0:
										tempMap.put("dayLeaveJan", results.get("nbrDayLeave"));
//										empl.put("dayLeaveJan", results.get("nbrDayLeave"));
										break;
									case 1:
										tempMap.put("dayLeaveFer", results.get("nbrDayLeave"));
//										empl.put("dayLeaveFer",results.get("nbrDayLeave"));
										break;
									case 2:
										tempMap.put("dayLeaveMar", results.get("nbrDayLeave"));
//										empl.put("dayLeaveMar",results.get("nbrDayLeave"));
										break;			
									case 3:
										tempMap.put("dayLeaveApr", results.get("nbrDayLeave"));
//										empl.put("dayLeaveApr", results.get("nbrDayLeave"));
										break;
									case 4:
										tempMap.put("dayLeaveMay", results.get("nbrDayLeave"));
//										empl.put("dayLeaveMay", results.get("nbrDayLeave"));
										break;
									case 5:
										tempMap.put("dayLeaveJune", results.get("nbrDayLeave"));
//										empl.put("dayLeaveJune", results.get("nbrDayLeave"));
										break;
									case 6:
										tempMap.put("dayLeaveJuly", results.get("nbrDayLeave"));
//										empl.put("dayLeaveJuly", results.get("nbrDayLeave"));
										break;
									case 7:
										tempMap.put("dayLeaveAug", results.get("nbrDayLeave"));
//										empl.put("dayLeaveAug", results.get("nbrDayLeave"));
										break;
									case 8:
										tempMap.put("dayLeaveSep", results.get("nbrDayLeave"));
//										empl.put("dayLeaveSep", results.get("nbrDayLeave"));
										break;
									case 9:
										tempMap.put("dayLeaveOct", results.get("nbrDayLeave"));
//										empl.put("dayLeaveOct", results.get("nbrDayLeave"));
										break;
									case 10:
										tempMap.put("dayLeaveNov", results.get("nbrDayLeave"));
//										empl.put("dayLeaveNov", results.get("nbrDayLeave"));
										break;
									case 11:
										tempMap.put("dayLeaveDec", results.get("nbrDayLeave"));
//										empl.put("dayLeaveDec", results.get("nbrDayLeave"));
										break;
									default:
										break;
									}
							}
							listEmplSummary.add(tempMap);
					}
					
				}
				
			}
			
			
			if(UtilValidate.isNotEmpty(listEmplSummary)){
				result.put("listIterator", listEmplSummary);
				result.put("TotalRows", String.valueOf(listEmplSummary.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels","FatalGetListEmplSummary", locale));
			// TODO: handle exception
		}
		return result;
	}
	/*
	 * Description : get List EmplWorkOverTime JQX
	 * @param DispathContext dpct
	 * @param Map<?,?> context
	 * @return
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplWorkOverTime(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		String managerId = ((GenericValue) context.get("userLogin")).getString("partyId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listEmpl = null;
		List<GenericValue> listEmplOfManager = FastList.newInstance();
		List<String> listEmplId = FastList.newInstance();
		try {
			if(UtilValidate.isNotEmpty(managerId)){
				try {
					listEmplOfManager = PartyUtil.getListEmployeeOfManager(delegator,managerId);
					if(UtilValidate.isNotEmpty(listEmplOfManager)){
						for(GenericValue empl : listEmplOfManager){
							if(UtilValidate.isNotEmpty(empl.getString("partyId"))){
								listEmplId.add(empl.getString("partyId"));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Can't get list employee of manager!");
				}
			}
			if(UtilValidate.isNotEmpty(listEmplId)){
				listAllConditions.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,listEmplId));
			}
			if(UtilValidate.isNotEmpty(listSortFields)){
				try {
					listEmpl = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError("Can't get list employee work over time!");
				}
			}else{
				//List<String> orderBy = FastList.newInstance();
				listEmpl = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, opts);				}
			if(UtilValidate.isNotEmpty(listEmpl)){
				result.put("listIterator", listEmpl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	/*
	 * Description : get List EmplWorkOverTime JQX
	 * @param DispathContext dpct
	 * @param Map<?,?> context
	 * @return
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplWorkLate(DispatchContext dpct , Map<String,Object> context) {
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		List<GenericValue> listEmplLateSummary = FastList.newInstance();
		List<Map<String,Object>> listIterator = FastList.newInstance();
		try {
			listEmplLateSummary = delegator.findList("EmplWorkingLateSummary", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, opts, false);
			if(UtilValidate.isNotEmpty(listEmplLateSummary)){
				for(GenericValue empl : listEmplLateSummary){
					if(UtilValidate.isNotEmpty(empl.getString("partyId"))){
						Map<String,Object> map = FastMap.newInstance();
						map.put("partyId", empl.getString("partyId"));
						List<GenericValue> listReason =  delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", empl.getString("partyId"), "reasonFlag", "Y"), null, false);
						List<GenericValue> listNoReason =  delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", empl.getString("partyId"), "reasonFlag", "N"), null, false);
						map.put("reasonQuantity",listReason.size());
						map.put("NoReasonQuantity",listNoReason.size());
						listIterator.add(map);
					}
				}
			}
			if(UtilValidate.isNotEmpty(listIterator)){
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	/*
	 * get List Empl in Org
	 * @param dpct
	 * @param context
	 * @return
	 * @param Exception
	 *	
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplInOrg(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		//EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String partyId = (String) parameters.get("partyId")[0];
		EntityListIterator ListEmplInOrg = null;
		Map<String,Object> result = FastMap.newInstance();
		try {
			if(UtilValidate.isNotEmpty(partyId)){
				Organization org =	PartyUtil.buildOrg(delegator, partyId);	
				List<GenericValue> emplList = org.getEmployeeInOrg(delegator);
				List<String> emplListString = FastList.newInstance();
				emplListString = EntityUtil.getFieldListFromEntityList(emplList, "partyId", false);
				if(UtilValidate.isEmpty(emplListString)){
					emplListString.add("");
				}
				ListEmplInOrg = delegator.find("EmploymentAndPerson",EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,emplListString),null,null,null,null);
				if(UtilValidate.isNotEmpty(ListEmplInOrg)){
					result.put("listIterator", ListEmplInOrg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	} 	
	
	public static Map<String, Object> getEmplListInOrg(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		Delegator delegator = dctx.getDelegator();
		int totalRows = 0;
		String partyGroupId = (String)context.get("partyGroupId");
		if(partyGroupId == null){
			retMap.put("TotalRows", String.valueOf(totalRows));
			return retMap;
		}
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		
		int start = size * page;
		int end = start + size;
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
			totalRows = emplList.size();
			retMap.put("TotalRows", String.valueOf(totalRows));
			if(end > totalRows){
				end = totalRows;
			}
			emplList = emplList.subList(start, end);
			for(GenericValue tempGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
				if(department != null){
					String departmentId = department.getString("partyIdFrom");
					String departmentName = PartyHelper.getPartyName(delegator, departmentId, false);
					String partyName = PartyUtil.getPersonName(delegator, partyId);
					List<GenericValue> emplPos = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
					List<String> emplPosDes = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
					tempMap.put("partyId", partyId);
					tempMap.put("partyName", partyName);
					tempMap.put("emplPositionType", StringUtils.join(emplPosDes, ", "));
					tempMap.put("department", departmentName);
					listReturn.add(tempMap);
				}
						
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return retMap;
	}
 	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetEmplListInOrg(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listIterator", listReturn);
		Delegator delegator = dctx.getDelegator();
		int totalRows = 0;
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	String partyGroupId = request.getParameter("partyGroupId");
    	int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		retMap.put("listIterator", listReturn);
		if(partyGroupId != null){
			try {
				Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
				List<GenericValue> emplList = buildOrg.getEmployeeInOrg(delegator);
				totalRows = emplList.size();
				if(end > totalRows){
					end = totalRows;
				}
				emplList = emplList.subList(start, end);
				for(GenericValue tempGv: emplList){
					Map<String, Object> tempMap = FastMap.newInstance();
					String partyId = tempGv.getString("partyId");
					GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, partyId);
					if(department != null){
						String departmentId = department.getString("partyIdFrom");
						String departmentName = PartyHelper.getPartyName(delegator, departmentId, false);
						String partyName = PartyUtil.getPersonName(delegator, partyId);
						List<GenericValue> emplPos = PartyUtil.getCurrPositionTypeOfEmpl(delegator, partyId);
						List<String> emplPosDes = EntityUtil.getFieldListFromEntityList(emplPos, "description", true);
						tempMap.put("partyId", partyId);
						tempMap.put("partyName", partyName);
						tempMap.put("emplPositionType", StringUtils.join(emplPosDes, ", "));
						tempMap.put("department", departmentName);
						listReturn.add(tempMap);
					}
							
				}
			} catch(GenericEntityException e) {
				e.printStackTrace();
			}
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		return retMap;
	}
	
	/*
	 * get Detail Of Empl Working Late 
	 * @param dpct
	 * @param context
	 * @return
	 * @param Exception
	 *	
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetInfoDetailsEmplworkingLate(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String partyId = (String) parameters.get("partyId")[0];
		EntityListIterator InfoEmplWorkingLateDetail = null;
		Map<String,Object> result = FastMap.newInstance();
		try {
			listAllConditions.add( EntityCondition.makeCondition("partyId",partyId));
			if(UtilValidate.isNotEmpty(partyId)){
				InfoEmplWorkingLateDetail = delegator.find("EmplWorkingLate",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			}
			if(UtilValidate.isNotEmpty(InfoEmplWorkingLateDetail)){
				result.put("listIterator", InfoEmplWorkingLateDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	} 	
	/*
	 * get Detail Of Empl Working Late 
	 * @param dpct
	 * @param context
	 * @return
	 * @param Exception
	 *	
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetListEmplClaims(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator EmplClaimsList = null;
		Map<String,Object> result = FastMap.newInstance();
		try {
			EmplClaimsList = delegator.find("EmplClaim",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(EmplClaimsList)){
				result.put("listIterator", EmplClaimsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	/**
	 * JQgetFullNameEmployee
	 * @param DispatchContext
	 * @param Context
	 * @return
	 * @Exception
	 * 
	 * */
	public static Map<String,Object> JQgetFullNameEmployee(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = FastMap.newInstance();
		String partyId = (String) context.get("inputValue");
		try {
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue person = delegator.findOne("Person", false, UtilMisc.toMap("partyId", partyId));
				if(UtilValidate.isNotEmpty(person)){
					String fullName = "";
					fullName = UtilValidate.isNotEmpty(person.getString("lastName")) ? person.getString("lastName") : "";
					fullName += UtilValidate.isNotEmpty(person.getString("middleName")) ? " " + person.getString("middleName") : "";	
					fullName += UtilValidate.isNotEmpty(person.getString("firstName")) ? " " + person.getString("firstName") : "";
					if(UtilValidate.isNotEmpty(fullName)){
						result.put("outputValue", fullName);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Can't get full Name employee" + e.getMessage());
			// TODO: handle exception
		}
	return result;	
	}
	
	/**
	 * JQgetEmployeeInOrg
	 * @param DispatchContext
	 * @param Context
	 * @return
	 * @Exception
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> JQgetEmployeeInOrg(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		//Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,Object> result = FastMap.newInstance();
		try {
			List<GenericValue> listRole = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId","EMPLOYEE"), null, null, null, false);
			List<String> listPartyId = FastList.newInstance();
			for(GenericValue role : listRole){
				if(UtilValidate.isNotEmpty(role) && !listPartyId.contains(role.getString("partyId"))){
					listPartyId.add(role.getString("partyId"));
				}	
			}
			listAllConditions.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.IN,listPartyId));
			EntityListIterator listEmpl = delegator.find("Person", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND),null,null,listSortFields,opts);
			if(UtilValidate.isNotEmpty(listEmpl)){
				result.put("listIterator", listEmpl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal Error when get list employee in Org" + e.getMessage());
			// TODO: handle exception
		}
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmplWorkOvertimeRegis(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		Map<String,Object> result = FastMap.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
		listSortFields.add("dateRegistration");
		try {
			EntityListIterator listOvertimeWorking = delegator.find("WorkOvertimeRegistration", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			result.put("listIterator", listOvertimeWorking);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> createWorkOvertimeRegis(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Time startTime = (Time)context.get("overTimeFromDate");
		Time endTime = (Time)context.get("overTimeThruDate");
		java.sql.Date dateRegistration = (java.sql.Date)context.get("dateRegistration");
		Calendar cal = Calendar.getInstance();
		Calendar calStart = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calStart.setTime(dateRegistration);
		calEnd.setTime(dateRegistration);
		cal.setTime(startTime);
		calStart.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		calStart.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		calStart.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		cal.setTime(endTime);
		calEnd.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		calEnd.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		calEnd.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		if(calStart.after(calEnd)){
			return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "TimeBeginAfterTimeEnd", locale));
		}
		GenericValue workOvertimeRegistration = delegator.makeValue("WorkOvertimeRegistration");
		workOvertimeRegistration.setNonPKFields(context);
		String workOvertimeRegisId = delegator.getNextSeqId("WorkOvertimeRegistration");
		workOvertimeRegistration.set("workOvertimeRegisId", workOvertimeRegisId);
		try {
			workOvertimeRegistration.create();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("EmployeeUiLabels", "WorkOvertimeRegisSent", locale)); 
	}
	
	public static Map<String, Object> createEmplWorkingLateExt(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		String partyIdJson = (String)context.get("partyId");
		String dateWorkingLateStr = (String)context.get("dateWorkingLate");
		String delayTimeStr = (String)context.get("delayTime");
		JSONArray partyIdArr = JSONArray.fromObject(partyIdJson);
		Timestamp dateWorkingLate = new Timestamp(Long.parseLong(dateWorkingLateStr));
		Long delayTime = Long.parseLong(delayTimeStr);
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String reason = (String)context.get("reason");
		
			for(int i = 0; i < partyIdArr.size(); i++){
				String partyId = partyIdArr.getJSONObject(i).getString("partyId");
				Map<String, Object> ctxMap = FastMap.newInstance();
				ctxMap.put("partyId", partyId);
				ctxMap.put("dateWorkingLate", dateWorkingLate);
				ctxMap.put("delayTime", delayTime);
				ctxMap.put("statusId", statusId);
				ctxMap.put("userLogin", userLogin);
				ctxMap.put("reason", reason);
				try {
					dispatcher.runSync("createEmplWorkingLate", ctxMap);
				} catch (GenericServiceException e) {
					
					e.printStackTrace();
				}
			}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale));
	}
	
	public static Map<String, Object> createEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String partyId = (String)context.get("partyId");
		Timestamp dateWorkingLate = (Timestamp)context.get("dateWorkingLate");
		Timestamp startDate = UtilDateTime.getDayStart(dateWorkingLate);
		Timestamp endDate = UtilDateTime.getDayEnd(dateWorkingLate);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate));
		conditions.add(EntityCondition.makeCondition("dateWorkingLate", EntityJoinOperator.LESS_THAN_EQUAL_TO, endDate));
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("NotificationUiLabels", "createSuccessfully", locale)); 
		try {
			List<GenericValue> emplWorkingLate = delegator.findList("EmplWorkingLate", EntityCondition.makeCondition(conditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(emplWorkingLate)){
				GenericValue existEntity = emplWorkingLate.get(0);
				Timestamp tmpTimestamp = existEntity.getTimestamp("dateWorkingLate");
				Calendar cal = Calendar.getInstance();
				cal.setTime(tmpTimestamp);
				String dateStr = DateUtil.getDateMonthYearDesc(cal);
				String partyName = PartyUtil.getPersonName(delegator, partyId);
				return ServiceUtil.returnError(UtilProperties.getMessage("EmployeeUiLabels", "EmployeeDateWorkingLateExists", UtilMisc.toMap("dateWorkingLate", dateStr, "employeeName", partyName), locale));
			}
			GenericValue newEntity = delegator.makeValue("EmplWorkingLate");
			newEntity.setNonPKFields(context);
			String emplWorkingLateId = delegator.getNextSeqId("EmplWorkingLate");
			newEntity.set("emplWorkingLateId", emplWorkingLateId);
			newEntity.create();
			retMap.put("emplWorkingLateId", emplWorkingLateId);
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> deleteEmplWorkingLate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String emplWorkingLateId = (String)context.get("emplWorkingLateId");
		try {
			GenericValue emplWorkingLate = delegator.findOne("EmplWorkingLate", UtilMisc.toMap("emplWorkingLateId", emplWorkingLateId), false);
			if(emplWorkingLate == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "CannotFindRecordToDelete", locale));
			}
			emplWorkingLate.remove();
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("HrCommonUiLabels", "deleteSuccessfully", locale));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEmplPositionInOrg(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		String partyGroupId = request.getParameter("partyGroupId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		Map<String, Object> retMap = FastMap.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int totalRows = 0;
		int size, page = -1;
		try{
			size = Integer.parseInt(parameters.get("pagesize")[0]);
		}catch(Exception e){
			size = -1;
		}
    	try{
    		page = Integer.parseInt(parameters.get("pagenum")[0]);
    	}catch(Exception e){
    		page = -1;
    	}
		
		int start = size * page;
		int end = start + size;
		if(partyGroupId == null){
			partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> partyListGv =  buildOrg.getChildList();
			List<String> partyListId = EntityUtil.getFieldListFromEntityList(partyListGv, "partyId", true);
			partyListId.add(partyGroupId);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyListId));
			conditions.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
			
			List<GenericValue> allPositionInOrg = delegator.findList("EmplPositionSummary", EntityCondition.makeCondition(conditions), 
																		UtilMisc.toSet("partyId", "emplPositionTypeId", "totalEmplPositionId"), UtilMisc.toList("partyId"), null, false);
			if(end > allPositionInOrg.size()){
				end = allPositionInOrg.size();
			}
			totalRows = allPositionInOrg.size();
			allPositionInOrg = allPositionInOrg.subList(start, end);
			EntityCondition positionNotFulfilConds = EntityCondition.makeCondition("employeePartyId", null);
			List<EntityCondition> conds = FastList.newInstance();
			for(GenericValue tempGv: allPositionInOrg){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = tempGv.getString("partyId");
				String emplPositionTypeId = tempGv.getString("emplPositionTypeId");
				conds.clear();
				conds.add(EntityCondition.makeCondition("partyId", partyId));
				conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
				conds.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
				List<GenericValue> positionNotFulfillment = delegator.findList("AllEmplPositionAndFulfillment", 
						EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityJoinOperator.AND, positionNotFulfilConds), null, null, null, false);
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, partyId, false));
				GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				if(emplPositionType != null){
					tempMap.put("emplPositionTypeId", emplPositionTypeId);
					tempMap.put("emplPositionTypeDesc", emplPositionType.getString("description"));
				}else{
					System.err.println("partyId: " + partyId + "emplPositionTypeId: " + emplPositionTypeId);
				}
				tempMap.put("totalEmplPositionId", tempGv.getString("totalEmplPositionId"));
				tempMap.put("totalEmplPosNotFulfill", positionNotFulfillment.size());
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getEmployeeListDetailInfo(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		HttpServletRequest request = (HttpServletRequest)context.get("request");
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		String partyGroupId = request.getParameter("partyGroupId");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate);
		Delegator delegator = dctx.getDelegator();
		//GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(partyGroupId == null){
			partyGroupId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int size = Integer.parseInt(parameters.get("pagesize")[0]);
		int page = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = size * page;
		int end = start + size;
		int totalRows = 0;
		List<Map<String, Object>> listReturn = FastList.newInstance();
		try {
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
			List<GenericValue> emplList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
			if(end > emplList.size()){
				end = emplList.size();
			}
			totalRows = emplList.size();
			emplList = emplList.subList(start, end);
				
			for(GenericValue employeeGv: emplList){
				Map<String, Object> tempMap = FastMap.newInstance();
				String partyId = employeeGv.getString("partyId");
				tempMap.put("partyId", partyId);
				tempMap.put("partyName", PartyUtil.getPersonName(delegator, partyId));
				tempMap.put("gender", employeeGv.getString("gender"));
				
				java.sql.Date birthDate = employeeGv.getDate("birthDate");
				if(birthDate != null){
					tempMap.put("birthDate", birthDate.getTime());
				}
				List<GenericValue> currPos = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, partyId, fromDate, thruDate);
				List<String> emplPositionTypeDes = EntityUtil.getFieldListFromEntityList(currPos, "description", true);
				tempMap.put("emplPositionType", StringUtils.join(emplPositionTypeDes, ", "));	
				
				List<String> department = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				if(UtilValidate.isNotEmpty(department)){
					String departmentId = department.get(0);
					tempMap.put("partyGroupId", departmentId);
					tempMap.put("partyGroupName", PartyHelper.getPartyName(delegator, departmentId, false));
					//get address of department or work place of employee
					List<GenericValue> orgAddrs = PartyUtil.getPostalAddressOfOrg(delegator, departmentId, fromDate, thruDate);
					
					//get stateProvinceGeo of department
					List<String> stateProvinceGeoName = FastList.newInstance();
					for(GenericValue contactMech: orgAddrs){
						String stateProviceGeoId = contactMech.getString("stateProvinceGeoId");
						if(stateProviceGeoId != null){
							GenericValue stateProvinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProviceGeoId), false);
							stateProvinceGeoName.add(stateProvinceGeo.getString("geoName"));
						}
					}
					tempMap.put("departmentAddress", StringUtils.join(stateProvinceGeoName, "-"));
				}
				String directMgrId = PartyUtil.getManagerOfEmpl(delegator, partyId);
				tempMap.put("directManager", directMgrId);
				tempMap.put("directManagerName", PartyUtil.getPersonName(delegator, directMgrId));
				String nextMgrId = PartyUtil.getManagerOfEmpl(delegator, directMgrId);
				tempMap.put("nextManagerName", PartyUtil.getPersonName(delegator, nextMgrId));
				tempMap.put("nextMgrId", nextMgrId);
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} 
		retMap.put("TotalRows", String.valueOf(totalRows));
		retMap.put("listIterator", listReturn);
		return retMap;
	}
	
	public static Map<String, Object> getPositionByPositionTypeAndParty(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		int size, page;
		try{
			size = Integer.parseInt((String)context.get("pagesize"));
		}catch(Exception e){
			size = 0;
		}
		try{
			page = Integer.parseInt((String)context.get("pagenum"));
		}catch(Exception e){
			page = 0;
		}
		
		int start = size * page;
		int end = start + size;
		
    	int totalRows = 0;
    	String partyId = (String)context.get("partyId");
    	String emplPositionTypeId = (String)context.get("emplPositionTypeId");
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(EntityCondition.makeCondition("partyId", partyId));
    	conditions.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
    	conditions.add(EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate"));
    	try {
			List<GenericValue> emplPositionList = delegator.findList("AllEmplPositionAndFulfillment", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("employeePartyId"), null, false);
			totalRows = emplPositionList.size();
			if(end > emplPositionList.size()){
				end = emplPositionList.size();
			}
			emplPositionList = emplPositionList.subList(start, end);
			for(GenericValue tempGv: emplPositionList){
				Map<String, Object> tempMap = FastMap.newInstance();
				tempMap.put("emplPositionId", tempGv.getString("emplPositionId"));
				tempMap.put("employeePartyId", tempGv.getString("employeePartyId"));
				tempMap.put("employeePartyName", PartyUtil.getPersonName(delegator, tempGv.getString("employeePartyId")));
				Timestamp fromDate = tempGv.getTimestamp("fromDate");
				if(fromDate != null){
					tempMap.put("fromDate", fromDate.getTime());
				}
				Timestamp actualFromDate = tempGv.getTimestamp("actualFromDate");
				if(actualFromDate != null){
					tempMap.put("actualFromDate", actualFromDate.getTime());
				}
				listReturn.add(tempMap);
			}
		} catch (GenericEntityException e) {
			
			e.printStackTrace();
		}
    	retMap.put("TotalRows", String.valueOf(totalRows));
    	retMap.put("listReturn", listReturn);
		return retMap;
	}
}
