package com.olbius.basehr.employment.events;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.employment.helper.AgreementHelper;
import com.olbius.basehr.util.DateUtil;

public class AgreementEvents {
	public static String getAgreementEffectiveOfParty(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		HttpSession session = request.getSession(true);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		Timestamp fromDate = null;
		if(fromDateStr != null){
			fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
			paramMap.put("fromDate", fromDate);
		}
		if(thruDateStr != null){
			paramMap.put("thruDate", UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr))));
		}
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "getAgreementEffectivePartyInPeriod", 
					paramMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
			Map<String, Object> resultService = dispatcher.runSync("getAgreementEffectivePartyInPeriod", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				String agreementId = (String)resultService.get("agreementId");
				if(agreementId != null){
					String agreementCode = (String)resultService.get("agreementCode");
					request.setAttribute("agreementId", agreementId);
					request.setAttribute("agreementCode", agreementCode);
					GenericValue agreementEffective = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
					Timestamp fromDateEff = agreementEffective.getTimestamp("fromDate");
					if(fromDate != null){
						if(fromDate.equals(fromDateEff)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHREmploymentUiLabels", "NewAgreementHaveSameFromDateOldAgreement", 
									UtilMisc.toMap("agreementCode", agreementCode, "fromDate", DateUtil.getDateMonthYearDesc(fromDate)), UtilHttp.getLocale(request)));
						}
						if(fromDate.before(fromDateEff)){
							request.setAttribute("warningMessage", UtilProperties.getMessage("BaseHREmploymentUiLabels", "NewAgreementHaveFromDateLessThanOldAgreement", 
									UtilMisc.toMap("agreementCode", agreementCode, 
											"fromDate", DateUtil.getDateMonthYearDesc(fromDate),
											"fromDateEff", DateUtil.getDateMonthYearDesc(fromDateEff)), UtilHttp.getLocale(request)));
						}
					}
				}
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	
	public static String getPartyAgreementInfo(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		HttpSession session = request.getSession(true);
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = FastMap.newInstance();
		String partyId = request.getParameter("partyId");
		Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		context.put("partyIdTo", partyId);
		context.put("fromDate", fromDate);
		context.put("userLogin", userLogin);
		try {
			Map<String, Object> resultService = dispatcher.runSync("getAgreementEffectivePartyInPeriod", context);
			if(ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				String agreementId = (String)resultService.get("agreementId");
				if(agreementId != null){
					GenericValue agreementEffective = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
					Timestamp fromDateEff = agreementEffective.getTimestamp("fromDate");
					Timestamp agreementSignDate = agreementEffective.getTimestamp("agreementDate");
					String agreementDuration = AgreementHelper.getAgreementPeriod(delegator, agreementId);
					request.setAttribute("agreementCode", agreementEffective.getString("agreementCode"));
					request.setAttribute("fromDate", fromDateEff.getTime());
					request.setAttribute("agreementSignDate", agreementSignDate.getTime());
					request.setAttribute("agreementDuration", agreementDuration);
				}else{
					request.setAttribute("agreementNotSet", UtilProperties.getMessage("BaseHRUiLabels", "AgreementNotSetForEmployee", UtilHttp.getLocale(request)));
				}
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
	public static String checkUpdateAgreementStatus(HttpServletRequest request, HttpServletResponse response){
		String agreementId = request.getParameter("agreementId");
		String partyIdTo = request.getParameter("partyIdTo");
		String fromDateStr = request.getParameter("fromDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Delegator delegator = (Delegator)request.getAttribute("delegator");
		HttpSession session = request.getSession(true);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> context = FastMap.newInstance();
		context.put("partyIdTo", partyIdTo);
		context.put("agreementId", agreementId);
		context.put("userLogin", userLogin);
		context.put("fromDate", fromDate);
		try {
			Map<String, Object> resutlService = dispatcher.runSync("getAgreementChangeStatus", context);
			if(ServiceUtil.isSuccess(resutlService)){
				String agreementIdChangeStt = (String)resutlService.get("agreementIdChangeStt");
				if(agreementIdChangeStt != null){
					GenericValue agreementChangeStt = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementIdChangeStt), false);
					String statusIdChange = (String)resutlService.get("statusIdChange");
					String statusIdUpdate = (String)resutlService.get("statusIdUpdate");
					Timestamp agreementChangeSttFromDate = agreementChangeStt.getTimestamp("fromDate");
					String agreementChangeSttCode = agreementChangeStt.getString("agreementCode");
					if("EMPL_AGR_EXPIRED".equals(statusIdUpdate)){
						request.setAttribute("warningMessage", UtilProperties.getMessage("BaseHREmploymentUiLabels", 
								"AgreementUpdateExpire_FromDateOfAgreementUpdateLessThanAgreementOther", 
								UtilMisc.toMap("agreementChangeCode", agreementChangeSttCode, "fromDateAgrChange", DateUtil.getDateMonthYearDesc(agreementChangeSttFromDate),
										"fromDate", DateUtil.getDateMonthYearDesc(fromDate)), UtilHttp.getLocale(request)));
					}else if("EMPL_AGR_EXPIRED".equals(statusIdChange)){
						request.setAttribute("warningMessage", UtilProperties.getMessage("BaseHREmploymentUiLabels", 
								"AgreementExpire_FromDateOfAgreementLessThanAgreementUpdate", 
								UtilMisc.toMap("agreementChangeCode", agreementChangeSttCode, "fromDateAgrChange", DateUtil.getDateMonthYearDesc(agreementChangeSttFromDate),
										"fromDate", DateUtil.getDateMonthYearDesc(fromDate)), UtilHttp.getLocale(request)));
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}else{
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, resutlService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e){
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return "success";
	}
	
}
