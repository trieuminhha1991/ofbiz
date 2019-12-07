package com.olbius.acc.report;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class AccReportUtil {

	public static final String module = AccReportUtil.class.getName();

	// get all children of all levels
	public static List<String> getAllGlAccountChildren(String strGlAccountId,
			Delegator delegator) {
		List<String> listData = new ArrayList<String>();
		try {
			List<GenericValue> listTmp = delegator.findList("GlAccount",
					EntityCondition.makeCondition("parentGlAccountId",
							EntityOperator.EQUALS, strGlAccountId), UtilMisc
							.toSet("glAccountId"), null, null, false);
			if (listTmp != null) {
				for (GenericValue genericValue : listTmp) {
					listData.add((String) genericValue.get("glAccountId"));
					listData.addAll(getAllGlAccountChildren(
							(String) genericValue.get("glAccountId"), delegator));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return listData;
	}

	public static String getAccountId(String strAccountCode, Delegator delegator) {
		if (delegator == null) {

			Debug.log(module
					+ "::getAccountId Null delegator is not allowed in this method");

		}

		GenericValue glAccount = null;
		try {
			glAccount = EntityUtil
					.getFirst((delegator.findByAnd("GlAccount",
							UtilMisc.toMap("accountCode", strAccountCode),
							null, true)));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			Debug.logError(e, "Problem getting GlAccount",
					"Accounting Report Ultil");
		}

		if (glAccount == null) {
			
			throw new IllegalArgumentException("The passed AccountCode ["
					+ strAccountCode + "] does not match an existing GlAccount");
		}
		String glAccountId = glAccount.getString("glAccountId");

		return glAccountId;
	}

	public static Map<String, BigDecimal> evaluateAccCreditOrDeditGlAccount(
			String strGlAccountId, String strOrganizationPartyId,
			Delegator delegator, String customTimePeriodId, String strFlag) {
		// List mainAndExprs = new ArrayList();

		List<String> listGlAccount = getAllGlAccountChildren(strGlAccountId,
				delegator);
		listGlAccount.add(strGlAccountId);

		Map<String, BigDecimal> balMap = new HashMap<String, BigDecimal>();
		BigDecimal amount = BigDecimal.ZERO;

		BigDecimal credit_amount = BigDecimal.ZERO;
		BigDecimal debit_amount = BigDecimal.ZERO;
		try {
			for (String gId : listGlAccount) {
				List<EntityCondition> conds = FastList.newInstance();

				conds.add(EntityCondition.makeCondition("glAccountId",
						EntityOperator.EQUALS, gId));
				conds.add(EntityCondition.makeCondition("organizationPartyId",
						EntityOperator.EQUALS, strOrganizationPartyId));
				conds.add(EntityCondition.makeCondition("customTimePeriodId",
						EntityOperator.EQUALS, customTimePeriodId));

				List<GenericValue> glhc = delegator.findList(
						"GlAccountHistoryAccumulate",
						EntityCondition.makeCondition(conds), null, null, null,
						false);


				for (GenericValue e : glhc) {
					BigDecimal postedCredit = e.getBigDecimal("postedCredits");
					BigDecimal postedDebit = e.getBigDecimal("postedDebits");
					if(postedCredit != null)
						credit_amount = credit_amount.add(postedCredit);
					if(postedDebit != null)
						debit_amount = debit_amount.add(postedDebit);
					
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (strFlag.equals("C")) {
			amount = credit_amount.subtract(debit_amount);
		} else {
			amount = debit_amount.subtract(credit_amount);
		}
		balMap.put(strGlAccountId, amount);

		/*
		 * 
		 * mainAndExprs.add(EntityCondition.makeCondition("glAccountId",
		 * EntityOperator.IN, listGlAccount)); //
		 * mainAndExprs.add(EntityCondition.makeCondition("debitCreditFlag", //
		 * EntityOperator.EQUALS, strFlag));
		 * mainAndExprs.add(EntityCondition.makeCondition("isPosted",
		 * EntityOperator.EQUALS, "Y"));
		 * mainAndExprs.add(EntityCondition.makeCondition("acctgTransTypeId",
		 * EntityOperator.NOT_EQUAL, "PERIOD_CLOSING")); if (fromDate != null) {
		 * mainAndExprs.add(EntityCondition.makeCondition("transactionDate",
		 * EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)); }
		 * mainAndExprs.add(EntityCondition.makeCondition("transactionDate",
		 * EntityOperator.LESS_THAN_EQUAL_TO, thruDate)); List<GenericValue>
		 * listData = delegator .findList("AcctgTransGlAccountSums",
		 * EntityCondition .makeCondition(mainAndExprs, EntityOperator.AND),
		 * UtilMisc.toSet("glAccountId", "debitCreditFlag", "amount"), null,
		 * null, false); Map<String, BigDecimal> balMap = new HashMap<String,
		 * BigDecimal>(); if (listData != null) { for (GenericValue genericValue
		 * : listData) { // FIXME get currency and convert if
		 * (genericValue.get("amount") == null) { continue; } if
		 * ("C".equals(strFlag)) { String strGlId =
		 * genericValue.getString("glAccountId"); String strDebitCreditFlag =
		 * genericValue .getString("debitCreditFlag"); BigDecimal amount =
		 * UtilValidate.isNotEmpty(balMap .get(strGlId)) ? balMap.get(strGlId) :
		 * BigDecimal.ZERO; ; if ("C".equals(strDebitCreditFlag))
		 * balMap.put(strGlId, amount.add(genericValue
		 * .getBigDecimal("amount"))); else balMap.put(strGlId,
		 * amount.subtract((genericValue .getBigDecimal("amount")))); } else {
		 * String strGlId = genericValue.getString("glAccountId"); String
		 * strDebitCreditFlag = genericValue .getString("debitCreditFlag");
		 * BigDecimal amount = UtilValidate.isNotEmpty(balMap .get(strGlId)) ?
		 * balMap.get(strGlId) : BigDecimal.ZERO; ; balMap.put(strGlId,
		 * genericValue.getBigDecimal("amount")); if
		 * ("D".equals(strDebitCreditFlag)) balMap.put(strGlId,
		 * amount.add(genericValue .getBigDecimal("amount"))); else
		 * balMap.put(strGlId, amount.subtract((genericValue
		 * .getBigDecimal("amount")))); } } }
		 */
		return balMap;
	}

	public static Map<String, BigDecimal> evaluateValueGlAccount(
			String strFunction, String strPeriodId, Delegator delegator,
			String strOrganizationPartyId) throws Exception {
		strFunction = strFunction.trim();
		// get fromDate and thruDate
		GenericValue customTime = delegator.findOne("CustomTimePeriod",
				UtilMisc.toMap("customTimePeriodId", strPeriodId), false);
		Date fromDate = (Date) customTime.get("fromDate");
		Date thruDate = (Date) customTime.get("thruDate");
		String customTimePeriodId = (String) customTime
				.getString("customTimePeriodId");
		Timestamp tsThruDate = new Timestamp(thruDate.getTime() + 86400000 - 1);
		// 1. DuNo ok1
		// 2. DuCo
		// 3. PhatSinhDoiUng
		// 4. LuyKePhatSinhCo
		// 5. LuyKePhatSinhNo
		// 6. DuNoChiTiet
		// 7. DuCoChiTiet
		// 8. DuNoChiTietTheoTK
		// 9. DuCoChiTietTheoTK
		// 10. DuNoChiTietTheoTKDT
		// 11. DuCoChiTietTheoTKDT
		String strAccountCode = strFunction.trim().substring(
				strFunction.indexOf("(") + 1, strFunction.length() - 1);
		String strAccountId = "";
		if (!strFunction.startsWith("PhatSinhDoiUng(")
				&& !strFunction.startsWith("DuNo(")
				&& !strFunction.startsWith("DuCo(")
				&& !strFunction.startsWith("DuNoChiTietTheoTKDT(")
				&& !strFunction.startsWith("DuCoChiTietTheoTKDT("))
			strAccountId = getAccountId(strAccountCode, delegator);
		// 1. DuNo
		Debug.log(module + "::evaluateValueGlAccount, strFunction = "
				+ strFunction);
		if (strFunction.startsWith("DuNo(")) {
			return evaluateAccCreditOrDeditGlAccount(strAccountCode,
					strOrganizationPartyId, delegator, customTimePeriodId, "D");
		} else
		// 2. DuCo
		if (strFunction.startsWith("DuCo(")) {
			return evaluateAccCreditOrDeditGlAccount(strAccountCode,
					strOrganizationPartyId, delegator, customTimePeriodId, "C");
		}
		;
		return new HashMap<String, BigDecimal>();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
