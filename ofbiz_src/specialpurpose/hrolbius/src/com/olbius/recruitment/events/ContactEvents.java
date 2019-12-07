package com.olbius.recruitment.events;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.JqxWidgetSevices;

public class ContactEvents {
	public static final String module = ContactEvents.class.getName();
	
	public static final String AUTH_USER = "thiep.levan@olbius.vn";
	public static final String AUTH_PASS = "thieplv1211";
	
	@SuppressWarnings("unchecked")
	public static String sendEmailToAppl(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		//Get userLogin
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
		//Get dispatcher
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        
		boolean beganTransaction = false;
        boolean okay = true;
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			beganTransaction  = TransactionUtil.begin();
			Map<String, Object> getEmailParametersCtx = FastMap.newInstance();
			getEmailParametersCtx.put("userLogin", userLogin);
			getEmailParametersCtx.put("workEffortIdTo", parameters.get("workEffortId"));
			Map<String, Object> getEmailParametersRs = dispatcher.runSync("getEmailParameters", getEmailParametersCtx);
			String emailTemplateSettingId = (String)parameters.get("emailTemplateSettingId");
			List<Map<String, String>> listApplicant = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listApplicant"));
			for(Map<String, String> item: listApplicant) {
				String partyId = item.get("partyId");
				String candidateName = PartyHelper.getPartyName(delegator, partyId, true, true);
				getEmailParametersRs.put("candidateName", candidateName);
				String emailAddress = item.get("emailAddress");
				Map<String, Object> sendMailFromTemplateSettingCtx = FastMap.newInstance();
				sendMailFromTemplateSettingCtx.put("partyId", partyId);
				sendMailFromTemplateSettingCtx.put("sendTo", emailAddress);
				sendMailFromTemplateSettingCtx.put("sendFrom", AUTH_USER);
				sendMailFromTemplateSettingCtx.put("bodyParameters", getEmailParametersRs);
				sendMailFromTemplateSettingCtx.put("emailTemplateSettingId", emailTemplateSettingId);
				sendMailFromTemplateSettingCtx.put("authUser", AUTH_USER);
				sendMailFromTemplateSettingCtx.put("authPass", AUTH_PASS);
				sendMailFromTemplateSettingCtx.put("userLogin", userLogin);
			   	Map<String, Object> sendMailFromTemplateSettingRs = dispatcher.runSync("sendMailFromTemplateSetting", sendMailFromTemplateSettingCtx);
				if(ServiceUtil.isSuccess(sendMailFromTemplateSettingRs)) {
					List<GenericValue> listWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", partyId, "workEffortId", parameters.get("workEffortId")), null, false);
					EntityUtil.getFirst(listWorkEffortPartyAss).put("availabilityStatusId", "AAS_CONTACTED");
					EntityUtil.getFirst(listWorkEffortPartyAss).store();
				}
			}
        } catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericServiceException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}finally {
			if(!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String updateAvailability(HttpServletRequest request, HttpServletResponse response) {
		//Get delegator
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        
		boolean beganTransaction = false;
        boolean okay = true;
        
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
			List<Map<String, String>> listApplicant = (List<Map<String,String>>)JqxWidgetSevices.convert("java.util.List", (String)parameters.get("listApplicant"));
			String statusId = (String)parameters.get("statusId");
			beganTransaction  = TransactionUtil.begin();
			for(Map<String, String> item: listApplicant) {
				String workEffortId = item.get("workEffortId");
				String partyId = item.get("partyId");
				List<GenericValue> listWorkEffortPartyAss = delegator.findByAnd("WorkEffortPartyAssignment", UtilMisc.toMap("partyId", partyId, "workEffortId", workEffortId), null, false);
				EntityUtil.getFirst(listWorkEffortPartyAss).put("availabilityStatusId", statusId);
				EntityUtil.getFirst(listWorkEffortPartyAss).store();
			}
        } catch (ParseException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericTransactionException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			okay = false;
		}finally {
			if(!okay) {
				try {
					TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}else {
				try {
					TransactionUtil.commit(beganTransaction);
				} catch (GenericTransactionException gte) {
					Debug.logError(gte, "Unable to rollback transaction", module);
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.ERROR_MESSAGE);
					return "error";
				}
			}
		}
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
	}
}
