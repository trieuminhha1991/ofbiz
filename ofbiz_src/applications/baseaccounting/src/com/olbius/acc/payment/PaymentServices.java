package com.olbius.acc.payment;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

public class PaymentServices {
	public static Map<String, Object> checkPaymentInvoiceAppr(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String paymentId = (String)context.get("paymentId");
		Locale locale = (Locale)context.get("locale");
		try {
			List<GenericValue> paymentApplList = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("paymentId", paymentId), null, false);
			for(GenericValue paymentAppl: paymentApplList){
				String invoiceId = paymentAppl.getString("invoiceId");
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				String newStatusId = invoice.getString("newStatusId");
				if(!"INV_APPR_NEW".equals(newStatusId)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotSetStatusPaymentPaidWhenInvoiceNotApproved", UtilMisc.toMap("invoiceId", invoiceId), locale));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getListReceiveExcessMoney(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		
		Long fromDateL = (Long) context.get("filterFromDate");
		Long thruDateL = (Long) context.get("filterThruDate");
		
		Date fromDate = null, thruDate = null;
		if (fromDateL != null) {
			fromDate = new Date(fromDateL);
		} else {
			fromDate = new Date();
		}
		if (thruDateL != null) {
			thruDate = new Date(thruDateL);
		} else {
			thruDate = new Date();
		}
		
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
		OlbiusQuery queryTmp1 = new OlbiusQuery(processor);
		OlbiusQuery queryTmp2 = new OlbiusQuery(processor);
		OlbiusQuery queryTmp = new OlbiusQuery(processor);
		OlbiusQuery query = new OlbiusQuery(processor);
		ResultSet resultSet = null;
		
		queryTmp1.select("opened_by_user_login_id").select("sum(difference_amount)", "diff_amount1");
		queryTmp1.from("pos_terminal_state");
		Condition cond1 = new Condition();
		cond1.and(Condition.make("closed_date is not null"));
		cond1.and(Condition.make("difference_amount is not null"));
		cond1.and(Condition.make("opened_date", ">=", getSqlFromDate(fromDate)));
		cond1.and(Condition.make("closed_date", "<=", getSqlThruDate(thruDate)));
		cond1.and(Condition.make("difference_amount", ">", 0));
		queryTmp1.where(cond1);
		queryTmp1.groupBy("opened_by_user_login_id");
		
		queryTmp2.select("opened_by_user_login_id").select("sum(difference_amount)", "diff_amount2");
		queryTmp2.from("pos_terminal_state");
		Condition cond2 = new Condition();
		cond2.and(Condition.make("closed_date is not null"));
		cond2.and(Condition.make("difference_amount is not null"));
		cond2.and(Condition.make("opened_date", ">=", getSqlFromDate(fromDate)));
		cond2.and(Condition.make("closed_date", "<=", getSqlThruDate(thruDate)));
		cond2.and(Condition.make("difference_amount", "<", 0));
		queryTmp2.where(cond2);
		queryTmp2.groupBy("opened_by_user_login_id");
		
		queryTmp.select("opened_by_user_login_id").select("sum(coalesce(difference_amount, 0))", "diff_amount");
		queryTmp.from("pos_terminal_state");
		Condition cond = new Condition();
		cond.and(Condition.make("closed_date is not null"));
		cond.and(Condition.make("difference_amount is not null"));
		cond.and(Condition.make("opened_date", ">=", getSqlFromDate(fromDate)));
		cond.and(Condition.make("closed_date", "<=", getSqlThruDate(thruDate)));
		queryTmp.where(cond);
		queryTmp.groupBy("opened_by_user_login_id");
		
		query.select("tmp.opened_by_user_login_id", "opened_by_user_login_id").select("tmp1.diff_amount1", "diff_amount1")
			.select("tmp2.diff_amount2", "diff_amount2").select("tmp.diff_amount", "diff_amount");
		query.from(queryTmp, "tmp");
		query.join(Join.LEFT_OUTER_JOIN, queryTmp1, "tmp1", "tmp.opened_by_user_login_id = tmp1.opened_by_user_login_id");
		query.join(Join.LEFT_OUTER_JOIN, queryTmp2, "tmp2", "tmp.opened_by_user_login_id = tmp2.opened_by_user_login_id");
		
		List<Map<String, Object>> listEmployee = FastList.newInstance();
		
		try {
			try {
				resultSet = query.getResultSet();
			} catch (GenericDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while (resultSet.next()) {
					Map<String, Object> map = FastMap.newInstance();
					String openedByUserLoginId = resultSet.getString("opened_by_user_login_id");
					String fullName = "";
					List<GenericValue> partyNameView = delegator.findList("PartyNameView", 
							EntityCondition.makeCondition("partyCode", openedByUserLoginId), null, null, null, false);
					if (UtilValidate.isNotEmpty(partyNameView)) {
						String lastName = partyNameView.get(0).getString("lastName");
						String middleName = partyNameView.get(0).getString("middleName");
						String firstName = partyNameView.get(0).getString("firstName");
						fullName = lastName + " " + ((middleName != null) ? (middleName + " ") : "") + firstName;
					}
					map.put("openedByUserLoginId", openedByUserLoginId);
					map.put("fullName", fullName);
					map.put("diffAmount1", resultSet.getBigDecimal("diff_amount1"));
					map.put("diffAmount2", resultSet.getBigDecimal("diff_amount2"));
					map.put("diffAmount", resultSet.getBigDecimal("diff_amount"));
					listEmployee.add(map);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		result.put("listEmployee", listEmployee);
		
		return result;
	}
	
	public static Map<String, Object> processExcessMoney(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String currentOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> listPosTerminal = FastList.newInstance();
		
		Long fromDateL = (Long) context.get("filterFromDate");
		Long thruDateL = (Long) context.get("filterThruDate");
		Long transactionDateL = (Long) context.get("transactionDate");
		String listEmployeeStr = (String) context.get("listEmployee");
		BigDecimal totalAdjustIncre = BigDecimal.ZERO;
		BigDecimal totalAdjustDisc = BigDecimal.ZERO;
		String currencyId = "";
		
		Date fromDate = null, thruDate = null, transactionDate = null;
		if (fromDateL != null) {
			fromDate = new Date(fromDateL);
		} 
		if (thruDateL != null) {
			thruDate = new Date(thruDateL);
		}
		if (transactionDateL != null) {
			transactionDate = new Date(transactionDateL);
		}		
		
		List<String> emplList = FastList.newInstance();
		if (listEmployeeStr != null) {
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(listEmployeeStr)) {
				jsonArray = JSONArray.fromObject(listEmployeeStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					emplList.add((String) jsonArray.get(i));
				}
			}
		}
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("closedDate", EntityOperator.NOT_EQUAL, null));
		listAllConditions.add(EntityCondition.makeCondition("differenceAmount", EntityOperator.NOT_EQUAL, null));
		if (UtilValidate.isNotEmpty(emplList)) {
			listAllConditions.add(EntityCondition.makeCondition("openedByUserLoginId", EntityOperator.IN, emplList));
		}
		if (UtilValidate.isNotEmpty(fromDate)) {
			listAllConditions.add(EntityCondition.makeCondition("openedDate", EntityOperator.GREATER_THAN_EQUAL_TO, getSqlFromDate(fromDate)));
		}
		if (UtilValidate.isNotEmpty(thruDate)) {
			listAllConditions.add(EntityCondition.makeCondition("closedDate", EntityOperator.LESS_THAN_EQUAL_TO, getSqlThruDate(thruDate)));
		}
		
		try {
			listPosTerminal = delegator.findList("PosTerminalState", 
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(listPosTerminal)) {
				for (GenericValue terminal : listPosTerminal) {
					BigDecimal diffAmount = terminal.getBigDecimal("differenceAmount");
					currencyId = terminal.getString("currency");
					if (diffAmount.compareTo(BigDecimal.ZERO) > 0)
						totalAdjustIncre = totalAdjustIncre.add(diffAmount);
					else 
						totalAdjustDisc = totalAdjustDisc.add(diffAmount); 
					terminal.set("differenceAmount", BigDecimal.ZERO);
					terminal.set("amountBalance", diffAmount);
					delegator.store(terminal);
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		// Create acctgTrans
		Map<String, Object> resultService = FastMap.newInstance();
		if (totalAdjustDisc.compareTo(BigDecimal.ZERO) < 0 || totalAdjustIncre.compareTo(BigDecimal.ZERO) > 0) {
			int reciprocalId = 0;
			String reciprocalSeqId = "";			
			Map<String, Object> createAcctgTransAndEntriesMap = FastMap.newInstance();
			List<GenericValue> acctgTransEntriesExp = FastList.newInstance();
			if (totalAdjustDisc.compareTo(BigDecimal.ZERO) < 0) {
				reciprocalId += 1;
				reciprocalSeqId = UtilFormatOut.formatPaddedNumber(reciprocalId, 5);	
				GenericValue creditEntryExp = delegator.makeValue("AcctgTransEntry");
				creditEntryExp.put("debitCreditFlag", "C");
				creditEntryExp.put("organizationPartyId", currentOrgId);
				
				creditEntryExp.put("glAccountTypeId", "RPT_REFURN_EMPLOYEE");
				creditEntryExp.put("partyId", "_NA_");
				creditEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
				creditEntryExp.put("origAmount", totalAdjustDisc);
				creditEntryExp.put("origCurrencyUomId", currencyId);
				creditEntryExp.put("reciprocalSeqId", reciprocalSeqId);
				acctgTransEntriesExp.add(creditEntryExp);
				
				GenericValue debitEntryExp = delegator.makeValue("AcctgTransEntry");
				debitEntryExp.put("debitCreditFlag", "D");
				debitEntryExp.put("organizationPartyId", currentOrgId);
				debitEntryExp.put("glAccountTypeId", "ACCOUNTS_RECEIVABLE_POS");			
				debitEntryExp.put("partyId", "_NA_");
				debitEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
				debitEntryExp.put("origAmount", totalAdjustDisc);
				debitEntryExp.put("origCurrencyUomId", currencyId);
				debitEntryExp.put("reciprocalSeqId", reciprocalSeqId);
				acctgTransEntriesExp.add(debitEntryExp);
			}
			
			if (totalAdjustIncre.compareTo(BigDecimal.ZERO) > 0) {	
				reciprocalId += 1;
				reciprocalSeqId = UtilFormatOut.formatPaddedNumber(reciprocalId, 5);	
				GenericValue creditEntryExp = delegator.makeValue("AcctgTransEntry");
				creditEntryExp.put("debitCreditFlag", "C");
				creditEntryExp.put("organizationPartyId", currentOrgId);
				
				creditEntryExp.put("glAccountTypeId", "OTHER_POS_INCOME");
				creditEntryExp.put("partyId", "_NA_");
				creditEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
				creditEntryExp.put("origAmount", totalAdjustIncre);
				creditEntryExp.put("origCurrencyUomId", currencyId);
				creditEntryExp.put("reciprocalSeqId", reciprocalSeqId);
				acctgTransEntriesExp.add(creditEntryExp);
				
				GenericValue debitEntryExp = delegator.makeValue("AcctgTransEntry");
				debitEntryExp.put("debitCreditFlag", "D");
				debitEntryExp.put("organizationPartyId", currentOrgId);
				debitEntryExp.put("glAccountTypeId", "ACCOUNTS_RECEIVABLE_POS");			
				debitEntryExp.put("partyId", "_NA_");
				debitEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
				debitEntryExp.put("origAmount", totalAdjustIncre);
				debitEntryExp.put("origCurrencyUomId", currencyId);
				debitEntryExp.put("reciprocalSeqId", reciprocalSeqId);
				acctgTransEntriesExp.add(debitEntryExp);
			}
						
			createAcctgTransAndEntriesMap.put("userLogin", userLogin);
			createAcctgTransAndEntriesMap.put("glFiscalTypeId", "ACTUAL");
			createAcctgTransAndEntriesMap.put("acctgTransTypeId", "REC_CLT_EMP");
			createAcctgTransAndEntriesMap.put("partyId", "_NA_");
			createAcctgTransAndEntriesMap.put("transactionDate", transactionDate);
			createAcctgTransAndEntriesMap.put("acctgTransEntries", acctgTransEntriesExp);
			
			resultService = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			String acctgTransId = (String) resultService.get("acctgTransId");
			if (UtilValidate.isNotEmpty(listPosTerminal)) {
				for (GenericValue terminal : listPosTerminal) {
					BigDecimal amountBalance = terminal.getBigDecimal("amountBalance");
					if (!amountBalance.equals(BigDecimal.ZERO)) {
						terminal.set("acctgTransId", acctgTransId);
						delegator.store(terminal);
					}
				}
			}
		}					
		
		return result;
	}
	
	public static Map<String, Object> addAmountAppliedPayment(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		
		String paymentId = (String) context.get("paymentId");
		BigDecimal amountApplied= (BigDecimal) context.get("amountApplied");
		
		if (UtilValidate.isEmpty(amountApplied)) {
			amountApplied = BigDecimal.ZERO;
		}
		
		GenericValue payment = null;
		try {
			payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (UtilValidate.isNotEmpty(payment)) {
			BigDecimal oldAmount = payment.getBigDecimal("paymentApplied");
			if (UtilValidate.isEmpty(oldAmount)) {
				oldAmount = BigDecimal.ZERO;
			}
			BigDecimal newAmount = oldAmount.add(amountApplied);
			payment.set("paymentApplied", newAmount);
			
			try {
				payment.store();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> removeAmountAppliedPayment(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		
		String paymentId = (String) context.get("paymentId");
		String paymentApplicationId = (String) context.get("paymentApplicationId");
		
		GenericValue paymentApplication = delegator.findOne("PaymentApplicationStatus", UtilMisc.toMap("paymentApplicationId", paymentApplicationId), false);
		BigDecimal amountApplied = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(paymentApplication)) {
			amountApplied = paymentApplication.getBigDecimal("amountApplied");
		}
		
		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		if (UtilValidate.isNotEmpty(payment)) {
			BigDecimal oldAmount = payment.getBigDecimal("paymentApplied");
			if (UtilValidate.isEmpty(oldAmount)) {
				oldAmount = BigDecimal.ZERO;
			}
			BigDecimal newAmount = oldAmount.subtract(amountApplied);
			payment.set("paymentApplied", newAmount);
			
			try {
				payment.store();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> updateAmountPosTerminalState(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		String paymentId = (String) context.get("paymentId");
		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		if (UtilValidate.isNotEmpty(payment)) {
			String posTerminalStateId = payment.getString("posTerminalStateId");
			BigDecimal amount = UtilValidate.isNotEmpty(payment.getBigDecimal("amount"))
					? payment.getBigDecimal("amount") : BigDecimal.ZERO;
			String statusId = payment.getString("statusId");
			if (UtilValidate.isNotEmpty(posTerminalStateId) && !"PMNT_CANCELLED".equals(statusId)) {
				GenericValue posTerminalState = delegator.findOne("PosTerminalState", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
				if (UtilValidate.isNotEmpty(posTerminalState)) {
					BigDecimal differenceAmount = UtilValidate.isNotEmpty(posTerminalState.getBigDecimal("differenceAmount"))
							? posTerminalState.getBigDecimal("differenceAmount") : BigDecimal.ZERO;
					BigDecimal actualReceivedAmount = UtilValidate.isNotEmpty(posTerminalState.getBigDecimal("actualReceivedAmount"))
							? posTerminalState.getBigDecimal("actualReceivedAmount") : BigDecimal.ZERO;
					GenericValue listWorkShift = delegator.findOne("ListWorkShift", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
					BigDecimal amountCash = listWorkShift.getBigDecimal("amountCash");
					BigDecimal newDifferenceAmount = differenceAmount.subtract(amount);
					BigDecimal checkAmount = amountCash.add(newDifferenceAmount);
					if (checkAmount.compareTo(BigDecimal.ZERO) == 0) {
						posTerminalState.set("differenceAmount", null);
						posTerminalState.set("actualReceivedAmount", null);
					} else {
						posTerminalState.set("differenceAmount", newDifferenceAmount);
						posTerminalState.set("actualReceivedAmount", actualReceivedAmount.subtract(amount));
					}
					posTerminalState.store();
					
					context.clear();
					context.put("userLogin", userLogin);
					dispatcher.runSync("ListWorkShiftJobSchedule", context);
				}
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> updateStatusOrderReceiptNote(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String receiptId = (String) context.get("receiptId");
		
		List<GenericValue> orderReceiptNotes = delegator.findList("OrderReceiptNote", EntityCondition.makeCondition("receiptId", receiptId), null, null, null, false);
		if (UtilValidate.isNotEmpty(orderReceiptNotes)) {
			GenericValue orderReceipt = orderReceiptNotes.get(0);
			BigDecimal totalAmount = orderReceipt.getBigDecimal("amount");
			List<GenericValue> paymentReceipts = delegator.findList("PaymentReceipt", EntityCondition.makeCondition("receiptId", receiptId), null, null, null, false);
			BigDecimal amountApplied = BigDecimal.ZERO;
			if (UtilValidate.isNotEmpty(paymentReceipts)) {
				for (GenericValue payment : paymentReceipts) {
					amountApplied = amountApplied.add(payment.getBigDecimal("amountApplied"));
				}
			}
			if (amountApplied.compareTo(BigDecimal.ZERO) > 0) {
				if (amountApplied.compareTo(totalAmount) >= 0) {
					orderReceipt.set("statusId", "ORD_REC_PAID");
				} else {
					orderReceipt.set("statusId", "ORD_REC_PART_PAID");
				}
			} else {
				orderReceipt.set("statusId", "ORD_REC_NOT_PAID");
			}
			orderReceipt.set("amountApplied", amountApplied);
			orderReceipt.store();
		}
		
		return result;
	}
	
	public static Map<String, Object> updateOrderReceiptNote(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		
		String paymentId = (String) context.get("paymentId");
		GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		if (UtilValidate.isNotEmpty(payment)) {
			String statusId = payment.getString("statusId");
			if (!"PMNT_CANCELLED".equals(statusId)) {
				List<GenericValue> paymentReceipts = delegator.findList("PaymentReceipt", EntityCondition.makeCondition("paymentId", paymentId), null, null, null, false);
				if (UtilValidate.isNotEmpty(paymentReceipts)) {
					for (GenericValue item : paymentReceipts) {
						item.remove();
					}
				}
			}
		}
		
		return result;
	}
	
	protected static Timestamp getSqlFromDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}

	protected static Timestamp getSqlThruDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}
}
