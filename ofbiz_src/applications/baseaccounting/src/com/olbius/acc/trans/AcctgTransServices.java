package com.olbius.acc.trans;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.PartyUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class AcctgTransServices {
	public static final String module = AcctgTransServices.class.getName();

	public static Map<String, Object> createGlAccountHistoryAccumulateForACustomTimePeriod(
			DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String organizationPartyId = (String) context
				.get("organizationPartyId");
		List<String> customTimePeriods = FastList.newInstance();
		if (customTimePeriodId != null && !customTimePeriodId.equals(""))
			customTimePeriods.add(customTimePeriodId);
		try {
			List<EntityCondition> conds = FastList.newInstance();

			if (customTimePeriods.size() == 0) {
				conds.add(EntityCondition.makeCondition("periodTypeId",
						EntityOperator.LIKE, "%FISCAL%"));
				List<GenericValue> lstTimePeriods = delegator.findList(
						"CustomTimePeriod",
						EntityCondition.makeCondition(conds), null, null, null,
						false);
				for (GenericValue ct : lstTimePeriods) {
					String id = ct.getString("customTimePeriodId");
					customTimePeriods.add(id);
				}
			}

			List<GenericValue> lstGlAccount = delegator.findList("GlAccount",
					null, null, null, null, false);

			for (String cusId : customTimePeriods) {
				for (GenericValue g : lstGlAccount) {
					String glAccountId = g.getString("glAccountId");
					
					GenericValue glAccHis = delegator.findOne(
							"GlAccountHistoryAccumulate", UtilMisc.toMap(
									"glAccountId", glAccountId,
									"organizationPartyId", organizationPartyId,
									"customTimePeriodId", cusId),
							false);
					
					
					if(glAccHis != null){
						Debug.log(module + "::createGlAccountHistoryAccumulateForACustomTimePeriod, period " + cusId + ", glAccount" + glAccountId
								+ ", exists --> CONTINUE");
						continue;
					}
					Debug.log(module + "::createGlAccountHistoryAccumulateForACustomTimePeriod, period " + cusId + ", glAccount" + glAccountId
							+ ", CREATE NEW");
					GenericValue gha = delegator
							.makeValue("GlAccountHistoryAccumulate");
					gha.put("glAccountId", glAccountId);
					gha.put("organizationPartyId", organizationPartyId);
					gha.put("customTimePeriodId", cusId);
					gha.put("postedDebits", BigDecimal.ZERO);
					gha.put("postedCredits", BigDecimal.ZERO);
					delegator.create(gha);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> accumulateAmountGlAccountFromAcctgTransEntry(
			DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			String glAccountId = (String) context.get("glAccountId");
			String organizationPartyId = (String) context
					.get("organizationPartyId");
			String debitCreditFlag = (String) context.get("debitCreditFlag");
			java.sql.Timestamp transactionDate = (java.sql.Timestamp) context
					.get("transactionDate");
			BigDecimal amount = (BigDecimal) context.get("amount");
//			Debug.log(module
//					+ "::accumulateAmountGlAccountFromAcctgTransEntry, glAccountId = "
//					+ glAccountId + ", organizationPartyId = "
//					+ organizationPartyId + ", debitCreditFlag = "
//					+ debitCreditFlag + ", transactionDate = "
//					+ transactionDate + ", amount = " + amount);

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.LESS_THAN_EQUAL_TO,
					new Date(transactionDate.getTime())));
			conds.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, new Date(
							transactionDate.getTime())));
			conds.add(EntityCondition.makeCondition("periodTypeId",
					EntityOperator.LIKE, "%FISCAL%"));

			List<GenericValue> customTimePeriods = delegator.findList(
					"CustomTimePeriod", EntityCondition.makeCondition(conds),
					null, null, null, false);
			for (GenericValue ct : customTimePeriods) {
				String customTimePeriodId = (String) ct
						.getString("customTimePeriodId");
				GenericValue glAccHis = delegator.findOne(
						"GlAccountHistoryAccumulate", UtilMisc.toMap(
								"glAccountId", glAccountId,
								"organizationPartyId", organizationPartyId,
								"customTimePeriodId", customTimePeriodId),
						false);
				if (glAccHis != null) {
					if (debitCreditFlag.equals("C")) {
						BigDecimal oldAmount = glAccHis
								.getBigDecimal("postedCredits");
						if (oldAmount == null)
							glAccHis.put("postedCredits", amount);
						else {
							oldAmount = oldAmount.add(amount);
							glAccHis.put("postedCredits", oldAmount);
						}
					} else {
						BigDecimal oldAmount = glAccHis
								.getBigDecimal("postedDebits");
						if (oldAmount == null)
							glAccHis.put("postedDebits", amount);
						else {
							oldAmount = oldAmount.add(amount);
							glAccHis.put("postedDebits", oldAmount);
						}

					}
					delegator.store(glAccHis);
				} else {
					glAccHis = delegator
							.makeValue("GlAccountHistoryAccumulate");
					glAccHis.put("glAccountId", glAccountId);
					glAccHis.put("organizationPartyId", organizationPartyId);
					glAccHis.put("customTimePeriodId", customTimePeriodId);
					if (debitCreditFlag.equals("C")) {
						glAccHis.put("postedCredits", amount);
					} else {
						glAccHis.put("postedDebits", amount);
					}
					delegator.create(glAccHis);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateAccumulateAmountGlAccountFromAcctgTransEntry(
			DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			/*
			 * String acctgTransId = (String)context.get("acctgTransId"); String
			 * acctgTransEntrySeqId =
			 * (String)context.get("acctgTransEntrySeqId");
			 * 
			 * GenericValue acctgTrans = delegator.findOne("AcctgTrans",
			 * UtilMisc.toMap("acctgTransId",acctgTransId), false);
			 * 
			 * GenericValue acctgTransEntry =
			 * delegator.findOne("AcctgTransEntry",
			 * UtilMisc.toMap("acctgTransId"
			 * ,acctgTransId,"acctgTransEntrySeqId",acctgTransEntrySeqId),
			 * false);
			 */
			// GenericValue userLogin = (GenericValue)context.get("userLogin");
			// String userLoginId = (String)userLogin.get("userLoginId");
			String glAccountId = (String) context.get("glAccountId");
			String organizationPartyId = (String) context
					.get("organizationPartyId");
			// String organizationPartyId =
			// PartyUtil.getRootOrganization(delegator, userLoginId);
			String debitCreditFlag = (String) context.get("debitCreditFlag");
			java.sql.Timestamp transactionDate = (java.sql.Timestamp) context
					.get("transactionDate");
			BigDecimal prevAmount = (BigDecimal) context.get("prevAmount");// old
																			// amount
			BigDecimal amount = (BigDecimal) context.get("amount");// new amount

			Debug.log(module
					+ "::updateAccumulateAmountGlAccountFromAcctgTransEntry, glAccountId = "
					+ glAccountId + ", organizationPartyId = "
					+ organizationPartyId + ", debitCreditFlag = "
					+ debitCreditFlag + ", transactionDate = "
					+ transactionDate + ", prevAmount = " + prevAmount
					+ ", amount = " + amount);

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.LESS_THAN_EQUAL_TO,
					new Date(transactionDate.getTime())));
			conds.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, new Date(
							transactionDate.getTime())));
			conds.add(EntityCondition.makeCondition("periodTypeId",
					EntityOperator.LIKE, "%FISCAL%"));

			List<GenericValue> customTimePeriods = delegator.findList(
					"CustomTimePeriod", EntityCondition.makeCondition(conds),
					null, null, null, false);
			for (GenericValue ct : customTimePeriods) {
				String customTimePeriodId = (String) ct
						.getString("customTimePeriodId");
				GenericValue glAccHis = delegator.findOne(
						"GlAccountHistoryAccumulate", UtilMisc.toMap(
								"glAccountId", glAccountId,
								"organizationPartyId", organizationPartyId,
								"customTimePeriodId", customTimePeriodId),
						false);
				if (glAccHis != null) {
					if (debitCreditFlag.equals("C")) {
						BigDecimal oldAmount = glAccHis
								.getBigDecimal("postedCredits");
						if (oldAmount == null)
							glAccHis.put("postedCredits", amount);
						else {
							oldAmount = oldAmount.add(amount);
							oldAmount = oldAmount.subtract(prevAmount);
							glAccHis.put("postedCredits", oldAmount);
						}
					} else {
						BigDecimal oldAmount = glAccHis
								.getBigDecimal("postedDebits");
						if (oldAmount == null)
							glAccHis.put("postedDebits", amount);
						else {
							oldAmount = oldAmount.add(amount);
							oldAmount = oldAmount.subtract(prevAmount);
							glAccHis.put("postedDebits", oldAmount);
						}

					}
					delegator.store(glAccHis);
				} else {
					/*
					 * glAccHis =
					 * delegator.makeValue("GlAccountHistoryAccumulate");
					 * glAccHis.put("glAccountId", glAccountId);
					 * glAccHis.put("organizationPartyId", organizationPartyId);
					 * glAccHis.put("customTimePeriodId", customTimePeriodId);
					 * if (debitCreditFlag.equals("C")) {
					 * glAccHis.put("postedCredits", amount); } else {
					 * glAccHis.put("postedDebits", amount); }
					 * delegator.create(glAccHis);
					 */
					Debug.logError(
							"OLBIUS: updateAccumulateAmountGlAccountFromAcctgTransEntry, entry NOT exist",
							module);
					return ServiceUtil
							.returnError("OLBIUS: updateAccumulateAmountGlAccountFromAcctgTransEntry, entry NOT exist");
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createAcctgTransHistoryAndEntry(
			DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String acctgTransId = (String) context.get("acctgTransId");
		List<Map<String, Object>> acctgTransEntriesChangeList = (List<Map<String, Object>>) context
				.get("acctgTransEntriesChangeList");
		String changeUserLoginId = (String) context.get("changeUserLoginId");
		Timestamp changeDate = (Timestamp) context.get("changeDate");
		if (changeDate == null) {
			changeDate = UtilDateTime.nowTimestamp();
		}
		if (changeUserLoginId == null) {
			changeUserLoginId = userLogin.getString("userLoginId");
		}
		GenericValue acctgTransHistory = delegator
				.makeValue("AcctgTransHistory");
		acctgTransHistory.setAllFields(context, false, null, null);
		acctgTransHistory.set("changeDate", changeDate);
		acctgTransHistory.set("changeUserLoginId", changeUserLoginId);
		try {
			delegator.create(acctgTransHistory);

			for (Map<String, Object> tempMap : acctgTransEntriesChangeList) {
				GenericValue acctgTransEntryHistory = delegator
						.makeValue("AcctgTransEntryHistory");
				acctgTransEntryHistory.setAllFields(tempMap, false, null, null);
				acctgTransEntryHistory.set("changeDate", changeDate);
				acctgTransEntryHistory.set("acctgTransId", acctgTransId);
				delegator.create(acctgTransEntryHistory);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}

	public static String INVOICE_TYPE_SALES_INVOICE = "SALES_INVOICE";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> closedAcctgTrans(DispatchContext dctx,
			Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String invoiceId = (String) context.get("invoiceId");
		String orderId = (String) context.get("orderId");
		try {
			List<GenericValue> listAcctgTrans = delegator.findList(
					"AcctgTrans", EntityCondition.makeCondition(EntityCondition
							.makeCondition("invoiceId", invoiceId),
							EntityCondition.makeCondition("acctgTransTypeId",
									INVOICE_TYPE_SALES_INVOICE)), null, null,
					null, false);
			if (listAcctgTrans == null)
				return ServiceUtil.returnSuccess();
			List<GenericValue> listOrderItemBilling = delegator.findList(
					"OrderItemBilling", EntityCondition.makeCondition(
							EntityCondition.makeCondition("invoiceId",
									invoiceId), EntityCondition.makeCondition(
									"orderId", orderId)), null, null, null,
					false);
			if (listOrderItemBilling == null)
				return ServiceUtil.returnSuccess();
			String acctgTransId = (String) EntityUtil.getFirst(listAcctgTrans)
					.get("acctgTransId");
			Map<String, Object> postTransCtx = FastMap.newInstance();
			postTransCtx.put("acctgTransId", acctgTransId);
			postTransCtx.put("userLogin", context.get("userLogin"));
			dctx.getDispatcher().runSync("postAcctgTrans", postTransCtx);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnSuccess();
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnSuccess();
		}

		return ServiceUtil.returnSuccess();
	}
}
