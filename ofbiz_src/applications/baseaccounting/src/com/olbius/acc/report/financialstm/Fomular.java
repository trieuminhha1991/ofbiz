package com.olbius.acc.report.financialstm;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.olbius.acc.utils.CacheUtils;
import com.olbius.entity.cache.OlbiusCache;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.CacheSingleton;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.AccountUtils;

public abstract class Fomular {

	public static final String MODULE = Fomular.class.getName();

	private static FormulaCache openingCache;
	private static FormulaCache endingCache;
	private static FormulaCache postedCache;
	private static OlbiusCache targetCache;

	Fomular() {
        openingCache = CacheSingleton.getFormulaCache(CacheUtils.FORMULA_OPENING_CACHE);
        postedCache = CacheSingleton.getFormulaCache(CacheUtils.FORMULA_POSTED_CACHE);
        endingCache = CacheSingleton.getFormulaCache(CacheUtils.FORMULA_ENDING_CACHE);
        targetCache = CacheSingleton.getCacheByName(CacheUtils.FORMULA_TARGET_CACHE);
	}

	// build and calculate
	public static BigDecimal buildAndCalculate(String strFormula, String strPeriodId, Delegator delegator,
			String strOrganizationPartyId) throws Exception {
		if (strFormula == null || "".equals(strFormula)) {
			return new BigDecimal(0);
		}
		BigDecimal bdResult = new BigDecimal(0);
		// calculate Plus sign
		String[] strPLus = strFormula.split("\\+");
		for (int i = 0; i < strPLus.length; i++) {
			// calculate Subtract sign
			BigDecimal tmpBd;
			if (strPLus[i].contains("-")) {
				String[] strSubtract = strPLus[i].split("-"); // FIXME can be
																// removed
				tmpBd = evaluateValue(strSubtract[0], strPeriodId, delegator, strOrganizationPartyId);
				if (!UtilValidate.isEmpty(tmpBd)) {
					bdResult = bdResult.add(tmpBd);
				}
				for (int j = 1; j < strSubtract.length; j++) {
					tmpBd = evaluateValue(strSubtract[j], strPeriodId, delegator, strOrganizationPartyId);
					if (!UtilValidate.isEmpty(tmpBd)) {
						bdResult = bdResult.subtract(tmpBd);
					}
				}
			} else {
				tmpBd = evaluateValue(strPLus[i], strPeriodId, delegator, strOrganizationPartyId);
				if (!UtilValidate.isEmpty(tmpBd)) {
					bdResult = bdResult.add(tmpBd);
				}
			}
		}
		return bdResult;
	}

	// calculate target value
	public static BigDecimal evalueTargetValue(Delegator delegator, String strTargetId, String reportId, String reportIdM, String reportIdT,
			String strPeriodId, String isClosed, String strOrganizationPartyId) throws Exception {
		String rawKey = strTargetId + ";" + reportId + ";" + strPeriodId + ";" + strOrganizationPartyId;
        ((TargetFormulaCache) targetCache).setParameters(isClosed, reportIdM, reportIdT);
		return ((TargetFormulaCache) targetCache).get(delegator, rawKey);
	}

	public static BigDecimal evaluateValue(String strFunction, String strPeriodId, Delegator delegator,
			String strOrganizationPartyId) throws Exception {
		strFunction = strFunction.trim();
		// get fromDate and thruDate
		GenericValue customTime = delegator.findOne("CustomTimePeriod",
				UtilMisc.toMap("customTimePeriodId", strPeriodId), false);
		Date fromDate = (Date) customTime.get("fromDate");
		Date thruDate = (Date) customTime.get("thruDate");
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
		String strAccountCode = strFunction.trim().substring(strFunction.indexOf("(") + 1, strFunction.length() - 1);
		// 1. DuNo
		if (strFunction.startsWith("DuNo(")) {
			return evaluateSurplusCreditAndDebit(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "D");
		} else
		// 2. DuCo
		if (strFunction.startsWith("DuCo(")) {
			return evaluateSurplusCreditAndDebit(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "C");
		} else
		// 3. PhatSinhDoiUng
		if (strFunction.startsWith("PhatSinhDoiUng(")) {
			String tmpCD = strFunction.trim().substring(strFunction.indexOf("(") + 1, strFunction.length() - 1);
			String[] arrCD = tmpCD.split(",");
			return evaluatePhatSinhDoiUng(arrCD[0].trim(), arrCD[1].trim(), strOrganizationPartyId, delegator,
					new Timestamp(fromDate.getTime()), tsThruDate);
		} else
		// 4. LuyKePhatSinhCo
		if (strFunction.startsWith("LuyKePhatSinhCo(")) {
			return evaluateAccCreditOrDedit(strAccountCode, strOrganizationPartyId, delegator,
					new Timestamp(fromDate.getTime()), tsThruDate, "C");
		} else
		// 5. LuyKePhatSinhNo
		if (strFunction.startsWith("LuyKePhatSinhNo(")) {
			return evaluateAccCreditOrDedit(strAccountCode, strOrganizationPartyId, delegator,
					new Timestamp(fromDate.getTime()), tsThruDate, "D");
		} else
		// 6. LuongTinhDuNo
		if (strFunction.startsWith("LuongTinhDuNo(")) {
			return evaluateBiSurplusCreditAndDebit(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "D");
		} else
		// 7. LuongTinhDuNo
		if (strFunction.startsWith("LuongTinhDuCo(")) {
			return evaluateBiSurplusCreditAndDebit(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "C");
		}
		// 8.DuNoPhatSinh
		if (strFunction.startsWith("DuNoPhatSinh(")) {
			return evaluateSurplusAccCreditAndDebit(strAccountCode, strOrganizationPartyId, delegator,
					new Timestamp(fromDate.getTime()), tsThruDate, "D");
		} else
		// 9. DuCoPhatSinh
		if (strFunction.startsWith("DuCoPhatSinh(")) {
			return evaluateSurplusAccCreditAndDebit(strAccountCode, strOrganizationPartyId, delegator,
					new Timestamp(fromDate.getTime()), tsThruDate, "C");
		} else
		// 10. TangGiamDauKy
		if (strFunction.startsWith("TangGiamDauKy(")) {
			return evaluateSurplusOpeningEnding(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "O");
		}
		else
		// 11. TangGiamCuoiKy
		if (strFunction.startsWith("TangGiamCuoiKy(")) {
			return evaluateSurplusOpeningEnding(strAccountCode, strPeriodId, strOrganizationPartyId, delegator, "E");
		}
		else
		// 12. SoDuDauKy
		if (strFunction.startsWith("SoDuDauKy(")) {
			return evaluateOpeningBalance(strAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		}
		else
		// 13. SoDuCuoiKy
		if (strFunction.startsWith("SoDuCuoiKy(")) {
			return evaluateEndingBalance(strAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		}			
		
		return new BigDecimal(0);
	}

	public static String getAccountId(String strAccountCode, Delegator delegator) {
		if (delegator == null) {
			throw new IllegalArgumentException("Null delegator is not allowed in this method");
		}

		GenericValue glAccount = null;
		try {
			glAccount = EntityUtil.getFirst(
					(delegator.findByAnd("GlAccount", UtilMisc.toMap("accountCode", strAccountCode), null, true)));
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problem getting GlAccount", "Accounting Report Ultil");
		}

		String glAccountId = null;
		if (UtilValidate.isNotEmpty(glAccount)) {
			glAccountId = glAccount.getString("glAccountId");
		}

		return glAccountId;
	}

	// get all children of all levels
	public static List<String> getAllGlAccountChildren(String strGlAccountId, Delegator delegator)
			throws GenericEntityException {
		List<String> listData = new ArrayList<String>();
		List<GenericValue> listTmp = delegator.findList("GlAccount",
				EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, strGlAccountId),
				UtilMisc.toSet("glAccountId"), null, null, false);
		if (listTmp != null) {
			for (GenericValue genericValue : listTmp) {
				listData.add((String) genericValue.get("glAccountId"));
				listData.addAll(getAllGlAccountChildren((String) genericValue.get("glAccountId"), delegator));
			}
		}
		return listData;
	}

	// get all children of all levels with accountCode
	public static List<String> getAllGlAccountCodeChildren(String strGlAccountId, Delegator delegator)
			throws GenericEntityException {
		List<String> listData = new ArrayList<String>();
		List<GenericValue> listTmp = delegator.findList("GlAccount",
				EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, strGlAccountId),
				UtilMisc.toSet("accountCode", "glAccountId"), null, null, false);
		if (listTmp != null) {
			for (GenericValue genericValue : listTmp) {
				listData.add((String) genericValue.get("accountCode"));
				listData.addAll(getAllGlAccountCodeChildren((String) genericValue.get("glAccountId"), delegator));
			}
		}
		return listData;
	}
	
	// get all children of all levels with gl_account_id
	public static List<String> getAllGlAccountIdChildren(String strGlAccountId, Delegator delegator)
			throws GenericEntityException {
		List<String> listData = new ArrayList<String>();
		List<GenericValue> listTmp = delegator.findList("GlAccount",
				EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, strGlAccountId),
				UtilMisc.toSet("accountCode", "glAccountId"), null, null, false);
		if (listTmp != null) {
			for (GenericValue genericValue : listTmp) {
				listData.add((String) genericValue.get("glAccountId"));
				listData.addAll(getAllGlAccountCodeChildren((String) genericValue.get("glAccountId"), delegator));
			}
		}
		return listData;
	}
	
	// evaluate DuNo and DuCo value
	private static BigDecimal evaluateSurplusCreditAndDebit(String strGlAccountCode, String strPeriodId,
			String strOrganizationPartyId, Delegator delegator, String strFlag) throws Exception {
		Map<String, BigDecimal> endingBalance = getEndingBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		BigDecimal surplus = BigDecimal.ZERO;
		if (strFlag.equals("C")) {
			surplus = endingBalance.get(BalanceWorker.CREDIT).subtract(endingBalance.get(BalanceWorker.DEBIT)); 
		} else {
			surplus = endingBalance.get(BalanceWorker.DEBIT).subtract(endingBalance.get(BalanceWorker.CREDIT));
		}
		
		return surplus;
	}	

	// evaluate SoDuDauKy value
	private static BigDecimal evaluateOpeningBalance(String strGlAccountCode, String strPeriodId,
			String strOrganizationPartyId, Delegator delegator) throws Exception {
		Map<String, BigDecimal> openingBalance = getOpeningBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		BigDecimal surplus = BigDecimal.ZERO;
		String glAccountId = getAccountId(strGlAccountCode, delegator);
		switch (AccountUtils.getAccountType(glAccountId, delegator)) {
		case AccountUtils.CREDIT:
			surplus = openingBalance.get(BalanceWorker.CREDIT).subtract(openingBalance.get(BalanceWorker.DEBIT));
			break;
		case AccountUtils.DEBIT:
			surplus = openingBalance.get(BalanceWorker.DEBIT).subtract(openingBalance.get(BalanceWorker.CREDIT));
		default:
			break;
		}				
		return surplus;
	}
	
	// evaluate SoDuCuoiKy value
	private static BigDecimal evaluateEndingBalance(String strGlAccountCode, String strPeriodId,
			String strOrganizationPartyId, Delegator delegator) throws Exception {
		Map<String, BigDecimal> endingBalance = getEndingBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		BigDecimal surplus = BigDecimal.ZERO;
		String glAccountId = getAccountId(strGlAccountCode, delegator);
		switch (AccountUtils.getAccountType(glAccountId, delegator)) {
		case AccountUtils.CREDIT:
			surplus = endingBalance.get(BalanceWorker.CREDIT).subtract(endingBalance.get(BalanceWorker.DEBIT));
			break;
		case AccountUtils.DEBIT:
			surplus = endingBalance.get(BalanceWorker.DEBIT).subtract(endingBalance.get(BalanceWorker.CREDIT));
		default:
			break;
		}				
		return surplus;		
	}	
	
	// evaluate LuongTinhDuNo and LuongTinhDuCo value
	private static BigDecimal evaluateBiSurplusCreditAndDebit(String strGlAccountCode, String strPeriodId, String strOrganizationPartyId, Delegator delegator, String strFlag) throws Exception{
		Map<String, BigDecimal> endingBalance = getEndingBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		BigDecimal surplus = BigDecimal.ZERO;
		if (strFlag.equals("C")) {
			surplus = endingBalance.get(BalanceWorker.CREDIT).subtract(endingBalance.get(BalanceWorker.DEBIT)); 
		} else {
			surplus = endingBalance.get(BalanceWorker.DEBIT).subtract(endingBalance.get(BalanceWorker.CREDIT));
		}
		if (surplus.compareTo(BigDecimal.ZERO) < 0) {
			surplus = BigDecimal.ZERO;
		}
		return surplus;
	}

	// evaluate PhatSinhDoiUng
	public static BigDecimal evaluatePhatSinhDoiUng(String glAccountCodeD, String glAccountCodeC,
			String strOrganizationPartyId, Delegator delegator, Timestamp fromDate, Timestamp thruDate)
			throws GenericEntityException {
		List<EntityCondition> mainAndExprs = new ArrayList<EntityCondition>();
		BigDecimal returnData = new BigDecimal(0);
		String glAccountIdD = getAccountId(glAccountCodeD, delegator);
		List<String> listChildGlAccD = getAllGlAccountCodeChildren(glAccountIdD, delegator);
		listChildGlAccD.add(glAccountCodeD);
		String glAccountIdC = getAccountId(glAccountCodeC, delegator);
		List<String> listChildGlAccC = getAllGlAccountCodeChildren(glAccountIdC, delegator);
		listChildGlAccC.add(glAccountCodeC);
		mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Date(fromDate.getTime())));
		mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, new Date(thruDate.getTime())));
		mainAndExprs.add(EntityCondition.makeCondition("glAccountCode", EntityJoinOperator.IN, listChildGlAccD));
		mainAndExprs.add(EntityCondition.makeCondition("recipGlAccountCode", EntityJoinOperator.IN, listChildGlAccC));
		mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", strOrganizationPartyId));
		List<GenericValue> listData = delegator.findList("AcctgDocumentListFactSum", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), 
				UtilMisc.toSet("crAmountTotal", "drAmountTotal"), null, null, false);
		for (GenericValue item : listData) {
			BigDecimal drAmountTotal = UtilValidate.isNotEmpty(item.getBigDecimal("drAmountTotal")) ? item.getBigDecimal("drAmountTotal") : BigDecimal.ZERO;
			BigDecimal crAmountTotal = UtilValidate.isNotEmpty(item.getBigDecimal("crAmountTotal")) ? item.getBigDecimal("crAmountTotal") : BigDecimal.ZERO;
			returnData = returnData.add(drAmountTotal).subtract(crAmountTotal);
		}
		return returnData.abs();
	}

	// evaluate DuNoPhatSinh and DuCoPhatSinh value
	public static BigDecimal evaluateSurplusAccCreditAndDebit(String glAccountCode, String strOrganizationPartyId,
			Delegator delegator, Timestamp fromDate, Timestamp thruDate, String strFlag) throws GenericEntityException {
		List<EntityCondition> mainAndExprs = new ArrayList<EntityCondition>();
		String glAccountId = getAccountId(glAccountCode, delegator);
		List<String> listChildGlAcc = getAllGlAccountCodeChildren(glAccountId, delegator);
		listChildGlAcc.add(glAccountCode);
		mainAndExprs.add(EntityCondition.makeCondition("glAccountCode", EntityOperator.IN, listChildGlAcc));
		mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, strOrganizationPartyId));
		if (fromDate != null) {
			mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Date(fromDate.getTime())));
		}
		mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, new Date(thruDate.getTime())));
		List<GenericValue> listTrans = delegator.findList("AcctgDocumentListFactSum3",
				EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), UtilMisc.toSet("crAmountTotal", "drAmountTotal"), null, null, false);
		if (UtilValidate.isNotEmpty(listTrans) && listTrans.size()> 0) {
			GenericValue genericValue = listTrans.get(0);
			BigDecimal returnData = new BigDecimal(0);
			if (strFlag.equals("C")) {
				returnData = returnData.add(UtilValidate.isNotEmpty(genericValue.getBigDecimal("crAmountTotal")) ? genericValue.getBigDecimal("crAmountTotal") : BigDecimal.ZERO);
				returnData = returnData.subtract(UtilValidate.isNotEmpty(genericValue.getBigDecimal("drAmountTotal")) ? genericValue.getBigDecimal("drAmountTotal") : BigDecimal.ZERO);
			} else {
				returnData = returnData.add(UtilValidate.isNotEmpty(genericValue.getBigDecimal("drAmountTotal")) ? genericValue.getBigDecimal("drAmountTotal") : BigDecimal.ZERO);
				returnData = returnData.subtract(UtilValidate.isNotEmpty(genericValue.getBigDecimal("crAmountTotal")) ? genericValue.getBigDecimal("crAmountTotal") : BigDecimal.ZERO);			
			}
			return returnData;
		}
		return new BigDecimal(0);
	}
	
	// evaluate LuyKePhatSinhCo and LuyKePhatSinhNo value
	public static BigDecimal evaluateAccCreditOrDedit(String glAccountCode, String strOrganizationPartyId,
			Delegator delegator, Timestamp fromDate, Timestamp thruDate, String strFlag) throws GenericEntityException {
		List<EntityCondition> mainAndExprs = new ArrayList<EntityCondition>();
		String glAccountId = getAccountId(glAccountCode, delegator);
		List<String> listChildGlAcc = getAllGlAccountCodeChildren(glAccountId, delegator);
		listChildGlAcc.add(glAccountCode);
		mainAndExprs.add(EntityCondition.makeCondition("glAccountCode", EntityOperator.IN, listChildGlAcc));
		mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, strOrganizationPartyId));
		if (fromDate != null) {
			mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Date(fromDate.getTime())));
		}
		mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, new Date(thruDate.getTime())));
		List<GenericValue> listTrans = delegator.findList("AcctgDocumentListFactSum2",
				EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), UtilMisc.toSet("crAmountTotal", "drAmountTotal"), null, null, false);
		for (GenericValue genericValue : listTrans) {
			BigDecimal returnData = new BigDecimal(0);
			if (strFlag.equals("C")) {
				if (genericValue.get("crAmountTotal") == null) {
					continue;
				}
				returnData = returnData.add(genericValue.getBigDecimal("crAmountTotal"));
			} else {
				if (genericValue.get("drAmountTotal") == null) {
					continue;
				}
				returnData = returnData.add(genericValue.getBigDecimal("drAmountTotal"));
			}
			return returnData.abs();
		}
		return new BigDecimal(0);
	}
	
	// evaluate TangGiamDauKy and TangGiamCuoiKy value
	private static BigDecimal evaluateSurplusOpeningEnding(String strGlAccountCode, String strPeriodId,
			String strOrganizationPartyId, Delegator delegator, String strFlag) throws Exception {
		Map<String, BigDecimal> endingBalance = getEndingBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		Map<String, BigDecimal> openingBalance = getOpeningBalance(strGlAccountCode, strPeriodId, strOrganizationPartyId, delegator);
		
		BigDecimal surplus = BigDecimal.ZERO;
		if (strFlag.equals("O")) {
			surplus = openingBalance.get(BalanceWorker.CREDIT).add(openingBalance.get(BalanceWorker.DEBIT)).subtract(endingBalance.get(BalanceWorker.CREDIT).add(endingBalance.get(BalanceWorker.DEBIT))); 
		} else {
			surplus = endingBalance.get(BalanceWorker.CREDIT).add(endingBalance.get(BalanceWorker.DEBIT)).subtract(openingBalance.get(BalanceWorker.CREDIT).add(openingBalance.get(BalanceWorker.DEBIT)));
		}
		
		return surplus;
	}	

	public static List<String> removeDuplicatedRecord(List<String> list) {
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(list);
		list.clear();
		list.addAll(hs);
		return list;
	}

	@SuppressWarnings("unchecked")
	static Map<String, BigDecimal> getOpeningBalance(String glAccountCode, String customTimePeriodId,
			String orgPartyId, Delegator delegator) throws GenericEntityException, NoSuchAlgorithmException {
		String rawKey = glAccountCode + ";" + customTimePeriodId + ";" + orgPartyId;
		return openingCache.get(delegator, rawKey);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, BigDecimal> getEndingBalance(String glAccountCode, String customTimePeriodId,
			String organizationPartyId, Delegator delegator) throws GenericEntityException, NoSuchAlgorithmException {
		String rawKey = glAccountCode + ";" + customTimePeriodId + ";" + organizationPartyId;
		return endingCache.get(delegator, rawKey);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, BigDecimal> getPostedAmount(String glAccountCode, String customTimePeriodId,
			String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException {
		String rawKey = glAccountCode + ";" + customTimePeriodId + ";" + organizationPartyId;
		return postedCache.get(delegator, rawKey);
	}

	public static String getPreviousPeriod(String customTimePeriodId, Delegator delegator) throws GenericEntityException {
		String previousPeriodId = "";
		try {
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			if(!UtilValidate.isEmpty(customTimePeriod)) {
				String periodTypeId = customTimePeriod.getString("periodTypeId");
				List<EntityCondition> condList = new ArrayList<EntityCondition>();
				condList.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
				condList.add(EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN, customTimePeriod.getDate("thruDate")));
				EntityCondition allCon = EntityCondition.makeCondition(condList);
				List<GenericValue> listPreviousPeriods = delegator.findList("CustomTimePeriod", allCon, null, UtilMisc.toList("-thruDate"), null, false);
				if(listPreviousPeriods.size() > 0) {
					GenericValue period = listPreviousPeriods.get(0);
					previousPeriodId = period.getString("customTimePeriodId");
				}
			}
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
		}
		return previousPeriodId;
	}
}