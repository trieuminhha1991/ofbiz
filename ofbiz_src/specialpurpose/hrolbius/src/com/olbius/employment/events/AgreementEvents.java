package com.olbius.employment.events;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.recruitment.helper.RoleTyle;
import com.olbius.services.JqxWidgetSevices;
import com.olbius.util.EntityUtil;

public class AgreementEvents implements RoleTyle{
	
	public static final String module = AgreementEvents.class.getName();
	public static final String UI_LABELS = "EmploymentUiLabels";
	
	
	public static String createEmplAgreement(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		//Get Locale
		Locale locale = UtilHttp.getLocale(request);
		
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
		//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		boolean beganTransaction = false;
        boolean okay = true;
        String message = "";
        
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
	        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
	        String partyIdFrom = (String)parameters.get("partyIdFrom");
	        String roleTypeIdFrom = (String)parameters.get("roleTypeIdFrom");
	        String partyIdTo = (String)parameters.get("partyIdTo");
	        String partyIdWork = (String)parameters.get("partyIdWork");
	        String salary = (String)parameters.get("salary");
	        String emplPositionTypeId = (String)parameters.get("emplPositionTypeId");
	        Timestamp thruDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("thruDate"));
	        Timestamp fromDate = (Timestamp)JqxWidgetSevices.convert("java.sql.Timestamp", (String)parameters.get("fromDate"));
	        String agreementTypeId = (String)parameters.get("agreementTypeId");
	        String repPartyIdFrom = (String)parameters.get("repPartyIdFrom");
	        String agreementId = EntityUtil.getEmplAgreementSeqId(delegator);
	        //Create Employment Agreement
	        GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyIdTo, "roleTypeId", EMPL_ROLE), false);
	        if(UtilValidate.isEmpty(partyRole)) {
	        	/*Check if partyTo doesn't have EMPLOYEE role*/
	        	Map<String, Object> createPartyRoleCtx = FastMap.newInstance();
	        	createPartyRoleCtx.put("partyId", partyIdTo);
	        	createPartyRoleCtx.put("roleTypeId", EMPL_ROLE);
	        	createPartyRoleCtx.put("userLogin", userLogin);
	        	dispatcher.runSync("PartyRole", createPartyRoleCtx);
	        }
	        List<GenericValue> listAgreement = delegator.findList("Agreement", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", EMPL_ROLE)), null, null, null, false);
			List<GenericValue> listActiveAgreement = EntityUtil.filterByDate(listAgreement);
			if(!UtilValidate.isEmpty(listActiveAgreement)) {
				okay = false;
				message = UtilProperties.getMessage(UI_LABELS, "createdAgreement", locale);
			}
			
	        GenericValue newAgreement = delegator.makeValue("Agreement");
	        newAgreement.put("partyIdFrom", partyIdFrom);
	        newAgreement.put("roleTypeIdFrom", roleTypeIdFrom);
	        newAgreement.put("partyIdTo", partyIdTo);
	        newAgreement.put("roleTypeIdTo", EMPL_ROLE);
	        newAgreement.put("agreementTypeId", agreementTypeId);
	        newAgreement.put("thruDate", thruDate);
	        newAgreement.put("fromDate", fromDate);
	        newAgreement.put("statusId", "AGREEMENT_CREATED");
	        newAgreement.put("agreementId", agreementId);
	        newAgreement.put("agreementDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        newAgreement.create();
	        
	        //Create Agreement Attr
	        GenericValue newAgreementAttr = delegator.makeValue("AgreementAttribute");
	        newAgreementAttr.put("agreementId", agreementId);
	        newAgreementAttr.put("attrName", "representFor");
	        newAgreementAttr.put("attrValue", repPartyIdFrom);
	        newAgreementAttr.create();
        	//Create Terms
        	Map<String, Object> createAgreementTermCtx = FastMap.newInstance();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "WORK_TERM");
	        createAgreementTermCtx.put("fromDate", fromDate);
	        createAgreementTermCtx.put("thruDate", thruDate);
	        createAgreementTermCtx.put("textValue", partyIdWork);
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
	        createAgreementTermCtx.clear();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "JOB_POSITION_TERM");
	        createAgreementTermCtx.put("fromDate", fromDate);
	        createAgreementTermCtx.put("thruDate", thruDate);
	        createAgreementTermCtx.put("textValue", emplPositionTypeId);
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
	        createAgreementTermCtx.clear();
	        createAgreementTermCtx.put("agreementId", agreementId);
	        createAgreementTermCtx.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
	        createAgreementTermCtx.put("termTypeId", "SALARY_TERM");
	        createAgreementTermCtx.put("fromDate", fromDate);
	        createAgreementTermCtx.put("thruDate", thruDate);
	        createAgreementTermCtx.put("termValue", Long.parseLong(salary));
	        createAgreementTermCtx.put("userLogin", userLogin);
	        dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        
	        /*//Create EmplPositionFulfilment
	        Set<String> setEmplPosition = FastSet.newInstance();
			List<GenericValue> listEmplPosition = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId, "partyId", partyIdWork), null, false);
			for(GenericValue item : listEmplPosition) {
				setEmplPosition.add(item.getString("emplPositionId"));
			}
	        List<GenericValue> listEmplPositionFul = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityJoinOperator.IN, setEmplPosition), null, null, null, false);
	        List<GenericValue> listActiveEmplPositionFul = EntityUtil.filterByDate(listEmplPositionFul);
	        if(!UtilValidate.isEmpty(listActiveEmplPositionFul)) {
	        	for(GenericValue item: listActiveEmplPositionFul) {
	        		item.set("thruDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        		item.store();
	        	}
	        }
			Map<String, Object> createPositionAndFulCtx = FastMap.newInstance();
			createPositionAndFulCtx.put("internalOrgId", partyIdWork);
			createPositionAndFulCtx.put("partyId", partyIdTo);
			createPositionAndFulCtx.put("emplPositionTypeId", emplPositionTypeId);
			createPositionAndFulCtx.put("actualFromDate", fromDate);
			createPositionAndFulCtx.put("actualThruDate", thruDate);
			createPositionAndFulCtx.put("userLogin", userLogin);
			dispatcher.runSync("createEmplPositionAndFulfillment", createPositionAndFulCtx);
			
			//Create Employment
			EntityCondition condition =  EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", repPartyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", ORG_ROLE, "roleTypeIdTo", EMPLOYEE_ROLE));
			List<GenericValue> listEmployment = delegator.findList("Employee", condition, null, null, null, false);
			List<GenericValue> listActiveEmployment = EntityUtil.filterByDate(listEmployment);
			if(UtilValidate.isEmpty(listActiveEmployment)) {
				for(GenericValue item : listActiveEmployment) {
					item.set("thruDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        		item.store();
				}
			}
			Map<String, Object> createEmploymentCtx = FastMap.newInstance();
			createEmploymentCtx.put("partyIdFrom", repPartyIdFrom);
			createEmploymentCtx.put("roleTypeIdFrom", ORG_ROLE);
			createEmploymentCtx.put("partyIdTo", partyIdTo);
			createEmploymentCtx.put("roleTypeIdTo", EMPLOYEE_ROLE);
			createEmploymentCtx.put("userLogin", userLogin);
			createEmploymentCtx.put("fromDate", fromDate);
			createEmploymentCtx.put("thruDate", thruDate);
			dispatcher.runSync("createEmployment", createEmploymentCtx);
			
			//Create PartyRelationship
			List<GenericValue> listPartyRela = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdWork, "partyIdTo", partyIdTo, "roleTypeIdFrom", ORG_ROLE, "roleTypeIdTo", EMPLOYEE_ROLE)), null, null, null, false);
			List<GenericValue> listActivePartyRela = EntityUtil.filterByDate(listPartyRela);
			if(UtilValidate.isEmpty(listActivePartyRela)) {
				for(GenericValue item : listActivePartyRela) {
					item.set("thruDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        		item.store();
				}
			}
			Map<String, Object> createPartyRelationshipCtx = FastMap.newInstance();
			createPartyRelationshipCtx.put("partyIdFrom", partyIdWork);
			createPartyRelationshipCtx.put("roleTypeIdFrom", ORG_ROLE);
			createPartyRelationshipCtx.put("partyIdTo", partyIdTo);
			createPartyRelationshipCtx.put("roleTypeIdTo", EMPLOYEE_ROLE);
			createPartyRelationshipCtx.put("userLogin", userLogin);
			createPartyRelationshipCtx.put("fromDate", fromDate);
			createPartyRelationshipCtx.put("thruDate", thruDate);
			createPartyRelationshipCtx.put("partyRelationshipTypeId", "EMPLOYMENT");
			dispatcher.runSync("createPartyRelationship", createPartyRelationshipCtx);
			
			//Create Party Rate Amount
			List<GenericValue> listRateAmount = delegator.findList("RateAmount", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo,"emplPositionTypeId", emplPositionTypeId)), null, null, null, false);
			List<GenericValue> listActiveRateAmount = EntityUtil.filterByDate(listRateAmount);
			for(GenericValue item: listActiveRateAmount) {
				item.set("thruDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        		item.store();
			}
			Map<String, Object> createPartyRateAmountCtx = FastMap.newInstance();
			createPartyRateAmountCtx.put("rateTypeId", "_NA_");
			createPartyRateAmountCtx.put("rateCurrencyUomId", "VND");
			createPartyRateAmountCtx.put("periodTypeId", "MONTHLY");
			createPartyRateAmountCtx.put("workEffortId", "_NA_");
			createPartyRateAmountCtx.put("partyId", partyIdTo);
			createPartyRateAmountCtx.put("emplPositionTypeId", emplPositionTypeId);
			createPartyRateAmountCtx.put("fromDate", fromDate);
			createPartyRateAmountCtx.put("thruDate", thruDate);
			Long rateAmount = (Long.parseLong(salary));
			createPartyRateAmountCtx.put("rateAmount",BigDecimal.valueOf(rateAmount));
			createPartyRateAmountCtx.put("userLogin", userLogin);
			dispatcher.runSync("createPartyRateAmount", createPartyRateAmountCtx);*/
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
			okay = false;
		} catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
			okay = false;
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
			okay = false;
		}finally {
			if(!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing create agreement", null);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					message = UtilProperties.getMessage(UI_LABELS, "agr_createFail", locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				}
			}
		}
        message = UtilProperties.getMessage(UI_LABELS, "agr_createSuccess", locale);
        request.setAttribute(ModelService.SUCCESS_MESSAGE, message);
        return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String createAppendix(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		
		//Get Locale
		Locale locale = UtilHttp.getLocale(request);
		
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
		//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		boolean beganTransaction = false;
        boolean okay = true;
        String message = "";
        try {
			beganTransaction  = TransactionUtil.begin();
			//Get parameters
	        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
	        String agreementText = (String)parameters.get("agreementText");
	        String agreementId = (String)parameters.get("agreementId");
	        
	        List<Map<String, Object>> listAppendixTerm = (List<Map<String,Object>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listAppendixTerm"));
	        Map<String, Object> createAgreementItemCtx = FastMap.newInstance();
	        createAgreementItemCtx.put("agreementId", agreementId);
	        createAgreementItemCtx.put("agreementText", agreementText);
	        createAgreementItemCtx.put("agreementItemTypeId", "AGREEMENT_APPENDIX");
	        createAgreementItemCtx.put("userLogin", userLogin);
	        Map<String, Object> createAgreementItemRS = dispatcher.runSync("createAgreementItem", createAgreementItemCtx);
	        if(ServiceUtil.isSuccess(createAgreementItemRS)) {
	        	for(Map<String, Object> item: listAppendixTerm) {
	        		Map<String, Object> createAgreementTermCtx = FastMap.newInstance();
	        		createAgreementTermCtx.put("agreementId", agreementId);
	        		createAgreementTermCtx.put("agreementItemSeqId", createAgreementItemRS.get("agreementItemSeqId"));
	        		createAgreementTermCtx.put("fromDate", JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("fromDate")));
	        		createAgreementTermCtx.put("thruDate", JqxWidgetSevices.convert("java.sql.Timestamp", (String)item.get("thruDate")));
	        		createAgreementTermCtx.put("termValue", JqxWidgetSevices.convert("java.math.BigDecimal", (String)item.get("termValue")));
	        		createAgreementTermCtx.put("textValue", (String)item.get("textValue"));
	        		createAgreementTermCtx.put("userLogin", userLogin);
	        		dispatcher.runSync("createAgreementTerm", createAgreementTermCtx);
	        	}
	        }
        } catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
		} catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
		}catch (Exception e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
			message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
		}finally {
			if(!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing create agreement", null);
					message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					message = UtilProperties.getMessage(UI_LABELS, "app_createFail", locale);
					request.setAttribute(ModelService.ERROR_MESSAGE, message);
					return "error";
				}
			}
		}
        message = UtilProperties.getMessage(UI_LABELS, "app_createSuccess", locale);
        request.setAttribute(ModelService.SUCCESS_MESSAGE, message);
        return "success";
	}
}
