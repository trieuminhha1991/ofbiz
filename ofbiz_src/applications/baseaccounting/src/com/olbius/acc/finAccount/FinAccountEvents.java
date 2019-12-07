package com.olbius.acc.finAccount;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ErrorUtils;

public class FinAccountEvents {
	public static String updateSupplierFinAccount(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String finAccountId = request.getParameter("finAccountId");
		String statusId = request.getParameter("statusId");
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("finAccountId", finAccountId);
		context.put("statusId", statusId);
		try {
			TransactionUtil.begin();
			try {
				Map<String, Object> resultService = dispatcher.runSync("updateFinAccount", context);
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				if("FNACT_ACTIVE".equals(statusId)){
					GenericValue finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
					List<EntityCondition> conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("finAccountId", EntityJoinOperator.NOT_EQUAL, finAccountId));
					conds.add(EntityCondition.makeCondition("ownerPartyId", finAccount.get("ownerPartyId")));
					conds.add(EntityCondition.makeCondition("statusId", "FNACT_ACTIVE"));
					List<GenericValue> listFinAccountExpire = delegator.findList("FinAccount", EntityCondition.makeCondition(conds), null, null, null, false);
					for(GenericValue finAccountExpire: listFinAccountExpire){
						resultService = dispatcher.runSync("updateFinAccount", UtilMisc.toMap("userLogin", userLogin, "statusId", "FNACT_MANFROZEN", "finAccountId", finAccountExpire.get("finAccountId")));
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							TransactionUtil.rollback();
							return "error";
						}
					}
				}
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
				request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
				TransactionUtil.commit();
			} catch (GenericServiceException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			} catch (GenericEntityException e) {
				e.printStackTrace();
				TransactionUtil.rollback();
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			}
		} catch (GenericTransactionException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createSupplierFinAccount(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String partyId = request.getParameter("partyId");
		String useAccount = request.getParameter("useAccount");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String statusId = null;
		if("Y".equals(useAccount)){
			statusId = "FNACT_ACTIVE";
		}else{
			statusId = "FNACT_MANFROZEN";
		}
		paramMap.put("statusId", statusId);
		paramMap.put("finAccountTypeId", "BANK_ACCOUNT");
		paramMap.put("organizationPartyId", partyId);
		paramMap.put("ownerPartyId", partyId);
		try {
			GenericValue supplier = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			String preferredCurrencyUomId = supplier.getString("preferredCurrencyUomId");
			paramMap.put("currencyUomId", preferredCurrencyUomId);
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createFinAccount", paramMap, userLogin, timeZone, locale);
			TransactionUtil.begin();
			context.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("createFinAccount", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			String finAccountId = (String)resultService.get("finAccountId");
			if("FNACT_ACTIVE".equals(statusId)){
				GenericValue finAccount = delegator.findOne("FinAccount", UtilMisc.toMap("finAccountId", finAccountId), false);
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("finAccountId", EntityJoinOperator.NOT_EQUAL, finAccountId));
				conds.add(EntityCondition.makeCondition("ownerPartyId", finAccount.get("ownerPartyId")));
				conds.add(EntityCondition.makeCondition("statusId", "FNACT_ACTIVE"));
				List<GenericValue> listFinAccountExpire = delegator.findList("FinAccount", EntityCondition.makeCondition(conds), null, null, null, false);
				for(GenericValue finAccountExpire: listFinAccountExpire){
					resultService = dispatcher.runSync("updateFinAccount", UtilMisc.toMap("userLogin", userLogin, "statusId", "FNACT_MANFROZEN", "finAccountId", finAccountExpire.get("finAccountId")));
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgcreatesuccess", locale));
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
