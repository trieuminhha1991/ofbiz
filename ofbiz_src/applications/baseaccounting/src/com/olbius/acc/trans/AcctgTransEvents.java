package com.olbius.acc.trans;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class AcctgTransEvents {
	public static final String module = AcctgTransEvents.class.getName();
	
	public static String createAcctgTransAndEntries(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String transactionDateStr = request.getParameter("transactionDate");
		Timestamp transactionDate = new Timestamp(Long.parseLong(transactionDateStr));
		String acctgTransEntryParam = request.getParameter("acctgTransEntry");
		Map<String, Object> context = FastMap.newInstance();
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		context.put("acctgTransId", request.getParameter("acctgTransId"));
		context.put("acctgTransTypeId", request.getParameter("acctgTransTypeId"));
		context.put("transactionDate", transactionDate);
		context.put("postedDate", transactionDate);
		context.put("isPosted", request.getParameter("isPosted"));
		context.put("glFiscalTypeId", "ACTUAL");
		context.put("partyId", request.getParameter("partyId"));
		context.put("invoiceId", request.getParameter("invoiceId"));
		context.put("paymentId", request.getParameter("paymentId"));
		context.put("shipmentId", request.getParameter("shipmentId"));
		context.put("description", request.getParameter("description"));
		JSONArray acctgTransEntryJsonArr = JSONArray.fromObject(acctgTransEntryParam);
		List<GenericValue> acctgTransEntries = FastList.newInstance();
		String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		try {
			GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", orgId), false);
			String currencyUomId = partyAcctgPreference.getString("baseCurrencyUomId");
			for(int i = 0; i < acctgTransEntryJsonArr.size(); i++){
				JSONObject acctgTransEntryJson = acctgTransEntryJsonArr.getJSONObject(i);
				String amountStr = acctgTransEntryJson.getString("amount");
				GenericValue acctgTransEntry = delegator.makeValue("AcctgTransEntry");
				acctgTransEntry.put("acctgTransEntryTypeId", "_NA_");
				acctgTransEntry.put("partyId", acctgTransEntryJson.get("partyId"));
				acctgTransEntry.put("productId", acctgTransEntryJson.get("productId"));
				acctgTransEntry.put("glAccountId", acctgTransEntryJson.get("glAccountId"));
				acctgTransEntry.put("organizationPartyId", orgId);
				acctgTransEntry.put("amount", new BigDecimal(amountStr));
				acctgTransEntry.put("origAmount", new BigDecimal(amountStr));
				acctgTransEntry.put("currencyUomId", currencyUomId);
				acctgTransEntry.put("origCurrencyUomId", currencyUomId);
				acctgTransEntry.put("debitCreditFlag", acctgTransEntryJson.get("debitCreditFlag"));
				acctgTransEntry.put("reconcileStatusId", "AES_NOT_RECONCILED");
				acctgTransEntry.put("reciprocalSeqId", acctgTransEntryJson.get("reciprocalSeqId"));
				acctgTransEntries.add(acctgTransEntry);
			}
			context.put("acctgTransEntries", acctgTransEntries);
			Map<String, Object> resultService = dispatcher.runSync("createAcctgTransAndEntries", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
		} catch (GenericEntityException e) {
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
	
	public static String cancelAcctgTransOlbius(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, GeneralServiceException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String fromAcctgTransId = request.getParameter("acctgTransId");
		
		GenericValue fromAcctgTrans = delegator.findOne("AcctgTrans", UtilMisc.toMap("acctgTransId", fromAcctgTransId), false);
		if (UtilValidate.isNotEmpty(fromAcctgTrans)) {
			if (!"Y".equals(fromAcctgTrans.getString("isCanceled"))) {
				Timestamp transactionDate = fromAcctgTrans.getTimestamp("transactionDate");
				Map<String, Object> context = FastMap.newInstance();
				context.put("userLogin", userLogin);
				context.put("fromAcctgTransId", fromAcctgTransId);
				context.put("revert", "Y");
				context.put("transactionDate", transactionDate);
				Map<String, Object> resultService = null;
				try {
					resultService = dispatcher.runSync("copyAcctgTransAndEntries", context);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
				}
				if(!ServiceUtil.isSuccess(resultService)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
					return "error";
				}
				
				fromAcctgTrans.set("isCanceled", "Y");
				fromAcctgTrans.store();
				
				String acctgTransId = (String) resultService.get("acctgTransId");
				GenericValue acctgTrans = delegator.findOne("AcctgTrans", UtilMisc.toMap("acctgTransId", acctgTransId), false);
				if (UtilValidate.isNotEmpty(acctgTrans)) {
					if ("N".equals(acctgTrans.getString("isPosted"))) {
						Map<String, Object> postAcctgTransMap = FastMap.newInstance();
						postAcctgTransMap.put("userLogin", userLogin);
						postAcctgTransMap.put("acctgTransId", acctgTransId);
						try {
							resultService = dispatcher.runSync("postAcctgTrans", postAcctgTransMap);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
						}
						if(!ServiceUtil.isSuccess(resultService)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
							return "error";
						}
					}
					
					acctgTrans.refresh();
					acctgTrans.set("isCanceled", "Y");
					acctgTrans.store();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
				}
			} else {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				String errorMessage = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCThisAcctgTransIsCanceled", locale);
				request.setAttribute(ModelService.ERROR_MESSAGE, errorMessage);
				return "error";
			}
		}
		
		return "success";
	}
	
	public static String getAcctgTransEntry(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String acctgTransId = request.getParameter("acctgTransId");
		try {
			List<GenericValue> acctgTransEntryList = delegator.findList("AcctgTransEntryAndDebitCredit", EntityCondition.makeCondition("acctgTransId", acctgTransId),
					null, UtilMisc.toList("reciprocalSeqId", "acctgTransEntrySeqId"), null, false);
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("listReturn", acctgTransEntryList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getGlAccountByAccountCode(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		//String accountCode = request.getParameter("accountCode");
		//EntityCondition cond = EntityCondition.makeCondition("accountCode", EntityJoinOperator.LIKE, accountCode + "%");
		try {
			List<GenericValue> glAccountList = delegator.findList("GlAccount", null, UtilMisc.toSet("glAccountId", "accountCode", "accountName"), UtilMisc.toList("accountCode"), null, false);
			request.setAttribute("listReturn", glAccountList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String updateAcctgTransEntry(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String acctgTransId = request.getParameter("acctgTransId");
		String acctgTransEntries = request.getParameter("acctgTransEntries");
		JSONArray acctgTransEntriesJson = JSONArray.fromObject(acctgTransEntries);
		Map<String, Object> context = FastMap.newInstance();
		String userLoginId = (String)userLogin.getString("userLoginId");
		String organizationPartyId = PartyUtil.getRootOrganization(delegator, userLoginId);
		context.put("userLogin", userLogin);
		//context.put("organizationPartyId", organizationPartyId);
		
		context.put("acctgTransId", acctgTransId);
		context.put("locale", locale);
		context.put("timeZone", timeZone);
		try {
			GenericValue acctgTrans = delegator.findOne("AcctgTrans", UtilMisc.toMap("acctgTransId", acctgTransId), false);
			java.sql.Timestamp transactionDate = (java.sql.Timestamp) acctgTrans.get("transactionDate");
			
			List<Map<String, Object>> acctgTransEntriesChangeList = FastList.newInstance();
			for(int i = 0; i < acctgTransEntriesJson.size(); i++){
				JSONObject acctgTransEntryJson = acctgTransEntriesJson.getJSONObject(i);
				String amountStr = acctgTransEntryJson.getString("amount");
				String acctgTransEntrySeqId = acctgTransEntryJson.getString("acctgTransEntrySeqId");
				String debitCreditFlag = acctgTransEntryJson.getString("debitCreditFlag");
				String debitAccountCode = acctgTransEntryJson.getString("debitAccountCode");
				String creditAccountCode = acctgTransEntryJson.getString("creditAccountCode");
				String description = acctgTransEntryJson.getString("description");
				if(description != null){
					description = description.trim();
				}
				if("null".equals(description)){
					description = null;
				}
				BigDecimal amount = new BigDecimal(amountStr);
				GenericValue acctgTransEntry = delegator.findOne("AcctgTransEntry", UtilMisc.toMap("acctgTransId", acctgTransId, "acctgTransEntrySeqId", acctgTransEntrySeqId), false);
				BigDecimal prevAmount = acctgTransEntry.getBigDecimal("amount");
				
				List<GenericValue> glAccountList = null;
				if("C".equals(debitCreditFlag)){
					glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("accountCode", creditAccountCode), null, null, null, false);
				}else if("D".equals(debitCreditFlag)){
					glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("accountCode", debitAccountCode), null, null, null, false);
				}
				GenericValue glAccount = EntityUtil.getFirst(glAccountList);
				if(glAccount != null){
					String glAccountId = glAccount.getString("glAccountId");
					String oldDesc = acctgTransEntry.getString("description");
					if(!acctgTransEntry.getString("glAccountId").equals(glAccountId) || acctgTransEntry.getBigDecimal("amount").compareTo(amount) != 0 
							||(UtilValidate.isNotEmpty(description) && UtilValidate.isEmpty(oldDesc)) || (UtilValidate.isEmpty(description) && UtilValidate.isNotEmpty(oldDesc))
							||(UtilValidate.isNotEmpty(description) && UtilValidate.isNotEmpty(oldDesc) && !description.equals(oldDesc))){
						description = UtilValidate.isNotEmpty(description)? description : null;
						context.put("acctgTransEntrySeqId", acctgTransEntrySeqId);
						context.put("glAccountId", glAccountId);
						context.put("amount", amount);
						context.put("description", description);
						context.put("origAmount", amount);
						//context.put("transactionDate", transactionDate);
						//context.put("prevAmount",prevAmount);
						//context.put("debitCreditFlag", debitCreditFlag);
                        Map<String, Object> resultServices = dispatcher.runSync("updateAcctgTransEntry", context);
						if(!ServiceUtil.isSuccess(resultServices)){
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultServices));
							return "error";
						}

                        /*if (acctgTrans.getBoolean("isPosted")) {
                            Map<String, Object> inputUpdateAccumulateGlAccount = FastMap.newInstance();
                            inputUpdateAccumulateGlAccount.put("transactionDate", transactionDate);
                            inputUpdateAccumulateGlAccount.put("prevAmount", prevAmount);
                            inputUpdateAccumulateGlAccount.put("debitCreditFlag", debitCreditFlag);
                            inputUpdateAccumulateGlAccount.put("glAccountId", glAccountId);
                            inputUpdateAccumulateGlAccount.put("amount", amount);
                            inputUpdateAccumulateGlAccount.put("organizationPartyId", organizationPartyId);
                            inputUpdateAccumulateGlAccount.put("userLogin", userLogin);
                            inputUpdateAccumulateGlAccount.put("locale", locale);

                            Debug.log(module + "::updateAcctgTransEntry, organizationPartyId = " + organizationPartyId + ", prevAmount = " + prevAmount + ", new Amount = " + amount);

                            Map<String, Object> resultAccumulateGlAccountServices = dispatcher.runSync("updateAccumulateAmountGlAccountFromAcctgTransEntry", inputUpdateAccumulateGlAccount);
                            if (!ServiceUtil.isSuccess(resultAccumulateGlAccountServices)) {
                                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultAccumulateGlAccountServices));
                                return "error";
                            }
                        }*/
						Map<String, Object> acctgTransHistoryEntry = FastMap.newInstance();
						acctgTransHistoryEntry.put("acctgTransEntrySeqId", acctgTransEntrySeqId);
						acctgTransHistoryEntry.put("glAccountIdTo", glAccountId);
						acctgTransHistoryEntry.put("amountTo", amount);
						acctgTransHistoryEntry.put("descriptionTo", description);
						acctgTransHistoryEntry.put("glAccountId", acctgTransEntry.getString("glAccountId"));
						acctgTransHistoryEntry.put("amount", acctgTransEntry.get("amount"));
						acctgTransHistoryEntry.put("description", oldDesc);
						acctgTransEntriesChangeList.add(acctgTransHistoryEntry);
					}
				}
			}
			if(UtilValidate.isNotEmpty(acctgTransEntriesChangeList)){
				Map<String, Object> acctgTransHistoryMap = FastMap.newInstance();
				acctgTransHistoryMap.put("acctgTransId", acctgTransId);
				acctgTransHistoryMap.put("userLogin", userLogin);
				acctgTransHistoryMap.put("acctgTransEntriesChangeList", acctgTransEntriesChangeList);
				Map<String, Object> resultServices = dispatcher.runSync("createAcctgTransHistoryAndEntry", acctgTransHistoryMap);
				if(!ServiceUtil.isSuccess(resultServices)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultServices));
					return "error";
				}
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GenericEntityException e) {
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
	
	public static String getAcctgTransHistoryLast(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String acctgTransId = request.getParameter("acctgTransId");
		try {
			List<GenericValue> acctgTransHistoryList = delegator.findByAnd("AcctgTransHistoryLast", UtilMisc.toMap("acctgTransId", acctgTransId), UtilMisc.toList("-changeDate"), false);
			if(UtilValidate.isNotEmpty(acctgTransHistoryList)){
				GenericValue acctgTransHistory = acctgTransHistoryList.get(0);
				Timestamp changeDate = acctgTransHistory.getTimestamp("changeDate");
				String fullName = acctgTransHistory.getString("fullName");
				Calendar cal = Calendar.getInstance();
				cal.setTime(changeDate);
				StringBuffer changeDateStr = new StringBuffer();
				changeDateStr.append(cal.get(Calendar.DATE) > 9? cal.get(Calendar.DATE): "0" + cal.get(Calendar.DATE));
				changeDateStr.append("/");
				changeDateStr.append(cal.get(Calendar.MONTH) >= 9? (cal.get(Calendar.MONTH) + 1): "0" + (cal.get(Calendar.MONTH) + 1));
				changeDateStr.append("/");
				changeDateStr.append(cal.get(Calendar.YEAR));
				changeDateStr.append(" ");
				changeDateStr.append(cal.get(Calendar.HOUR_OF_DAY) > 9? cal.get(Calendar.HOUR_OF_DAY): "0" + cal.get(Calendar.HOUR_OF_DAY));
				changeDateStr.append(":");
				changeDateStr.append(cal.get(Calendar.MINUTE) > 9? cal.get(Calendar.MINUTE): "0" + cal.get(Calendar.MINUTE));
				changeDateStr.append(":");
				changeDateStr.append(cal.get(Calendar.SECOND) > 9? cal.get(Calendar.SECOND): "0" + cal.get(Calendar.SECOND));
				request.setAttribute("fullName", fullName);
				request.setAttribute("changeDate", changeDateStr.toString());
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
