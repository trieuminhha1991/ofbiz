package com.olbius.acc.report.balancetrial;

import com.olbius.acc.utils.*;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.acc.utils.accounts.AccountUtils;

import com.olbius.entity.cache.OlbiusCache;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.accounting.trial.Balance;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.*;

public class BalanceWorker {

    private static OlbiusCache postedCache, endingCache, openingCache;

    public static final String MODULE = BalanceWorker.class.getName();
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";

    @SuppressWarnings("unchecked")
    public static Map<String, BigDecimal> getOpeningBalance(String glAccountId, String customTimePeriodId,String orgPartyId ,Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        initCache();
        String rawKey = glAccountId + ";" + customTimePeriodId + ";" + orgPartyId;
        return ((OpeningCache) openingCache).get(delegator, rawKey);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, BigDecimal> getEndingBalance(String glAccountId, String customTimePeriodId,String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        initCache();
        String rawKey = glAccountId + ";" + customTimePeriodId + ";" + organizationPartyId;
        return ((EndingCache) endingCache).get(delegator, rawKey);
    }

    // Using Olap bay VietTB
    public static Map<String, BigDecimal> getPostedAmount(String glAccountId, String customTimePeriodId,String organizationPartyId, Delegator delegator) throws Exception {
        //Get opening Balance
        initCache();
        String rawKey = glAccountId + ";" + customTimePeriodId + ";" + organizationPartyId;
        return ((PostedCache) postedCache).loadCache(delegator, rawKey);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, BigDecimal> getOpeningBalanceByParty(String glAccountId, String partyId, String customTimePeriodId, String orgPartyId ,Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        initCache();
        String rawKey = partyId + ";" + glAccountId + ";" + customTimePeriodId + ";" + orgPartyId;
        return ((OpeningCache) openingCache).get(delegator, rawKey);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, BigDecimal> getEndingBalanceByParty(String glAccountId, String partyId, String customTimePeriodId,String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        initCache();
        String rawKey = partyId + ";" + glAccountId + ";" + customTimePeriodId + ";" + organizationPartyId;
        return ((EndingCache) endingCache).get(delegator, rawKey);
    }

    // Using Olap bay VietTB
    @SuppressWarnings("unchecked")
    public static Map<String, BigDecimal> getPostedAmountByParty(String glAccountId, String partyId, String customTimePeriodId, String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        initCache();
        String rawKey = partyId + ";" + glAccountId + ";" + customTimePeriodId + ";" + organizationPartyId;
        return ((PostedCache) postedCache).get(delegator, rawKey);
    }

    public static Map<String, BigDecimal> getOpeningFromTo(String glAccountId, Date fromDate, Date thruDate, String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException{
        Map<String, BigDecimal> balMap = new HashMap<String, BigDecimal>();
        String customTimePeriodId = "";
        GenericValue period = null;
        BigDecimal postedCredit = BigDecimal.ZERO;
        BigDecimal postedDebit = BigDecimal.ZERO;

        List<EntityCondition> condList = new ArrayList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("groupPeriodTypeId", EntityComparisonOperator.EQUALS, "FISCAL_ACCOUNT"));
        condList.add(EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN, fromDate));
        condList.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.EQUALS, organizationPartyId));
        EntityCondition allCon = EntityCondition.makeCondition(condList);
        List<GenericValue> listCustomTimePeriods = delegator.findList("CustomTimePeriodAndType", allCon, null, UtilMisc.toList("-thruDate", "periodTypeId"), null, false);
        if(listCustomTimePeriods.size() > 0) {
            period = listCustomTimePeriods.get(0);
            customTimePeriodId = period.getString("customTimePeriodId");
        }

        Map<String, BigDecimal> openingBal = BalanceWorker.getEndingBalance(glAccountId, customTimePeriodId, organizationPartyId, delegator);
        BigDecimal balDrAmount = openingBal.get(BalanceWorker.DEBIT);
        BigDecimal balCrAmount = openingBal.get(BalanceWorker.CREDIT);
        Set<String> fieldSelect = FastSet.newInstance();
        fieldSelect.add("glAccountId");
        fieldSelect.add("organizationPartyId");
        fieldSelect.add("drAmount");
        fieldSelect.add("crAmount");

        Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
        List<Object> listAccountId = FastList.newInstance();
        List<Account> listAccount = acc.getListChild();
        for(Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountId.add(accEntity.getGlAccountId());
        }
        listAccountId.add(glAccountId);

        List<EntityCondition> listCondition = new ArrayList<EntityCondition>();
        if (period != null)
            listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN, period.getDate("thruDate")));
        else listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN, fromDate));

        listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.LESS_THAN, fromDate));
        listCondition.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.EQUALS, organizationPartyId));
        listCondition.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, listAccountId));
        EntityCondition allCondition = EntityCondition.makeCondition(listCondition);
        List<GenericValue> listAcctgTransSumFact = delegator.findList("AcctgTransSumFact", allCondition, fieldSelect, null, null, false);
        if (listAcctgTransSumFact.size() > 0) {
            GenericValue acctgTransSumFact = listAcctgTransSumFact.get(0);
            postedDebit = acctgTransSumFact.getBigDecimal("drAmount");
            postedCredit = acctgTransSumFact.getBigDecimal("crAmount");
        }

        switch (AccountUtils.getAccountType(glAccountId, delegator)) {
            case AccountUtils.CREDIT:
                balMap.put(CREDIT, balCrAmount.add(postedCredit.subtract(postedDebit)));
                balMap.put(DEBIT, BigDecimal.ZERO);
                break;
            case AccountUtils.DEBIT:
                balMap.put(DEBIT, balDrAmount.add(postedDebit.subtract(postedCredit)));
                balMap.put(CREDIT, BigDecimal.ZERO);
            default:
                break;
        }
        return balMap;
    }

    public static Map<String, BigDecimal> getOpeningByPartyFromTo(String glAccountId, String partyId, Date fromDate, Date thruDate,
                                                                   String organizationPartyId, Delegator delegator) throws NoSuchAlgorithmException, GenericEntityException {
        Map<String, BigDecimal> balMap = new HashMap<String, BigDecimal>();
        String customTimePeriodId = "";
        GenericValue period = null;
        BigDecimal postedCredit = BigDecimal.ZERO;
        BigDecimal postedDebit = BigDecimal.ZERO;

        List<EntityCondition> condList = new ArrayList<>();
        condList.add(EntityCondition.makeCondition("groupPeriodTypeId", EntityComparisonOperator.EQUALS, "FISCAL_ACCOUNT"));
        condList.add(EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN, fromDate));
        condList.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.EQUALS, organizationPartyId));
        EntityCondition allCon = EntityCondition.makeCondition(condList);
        List<GenericValue> listCustomTimePeriods = delegator.findList("CustomTimePeriodAndType", allCon, null, UtilMisc.toList("-thruDate", "periodTypeId"), null, false);
        if(listCustomTimePeriods.size() > 0) {
            period = listCustomTimePeriods.get(0);
            customTimePeriodId = period.getString("customTimePeriodId");
        }

        Map<String, BigDecimal> openingBal = BalanceWorker.getEndingBalanceByParty(glAccountId, partyId, customTimePeriodId, organizationPartyId, delegator);
        BigDecimal balDrAmount = openingBal.get(BalanceWorker.DEBIT);
        BigDecimal balCrAmount = openingBal.get(BalanceWorker.CREDIT);
        Set<String> fieldSelect = FastSet.newInstance();
        fieldSelect.add("glAccountId");
        fieldSelect.add("organizationPartyId");
        fieldSelect.add("drAmount");
        fieldSelect.add("crAmount");

        Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
        List<Object> listAccountId = FastList.newInstance();
        List<Account> listAccount = acc.getListChild();
        for(Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountId.add(accEntity.getGlAccountId());
        }
        listAccountId.add(glAccountId);

        List<EntityCondition> listCondition = new ArrayList<EntityCondition>();
        if (period != null)
            listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN, period.getDate("thruDate")));
        else listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN, fromDate));

        listCondition.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.LESS_THAN, fromDate));
        listCondition.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
        listCondition.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, listAccountId));
        listCondition.add(EntityCondition.makeCondition("partyId", partyId));
        EntityCondition allCondition = EntityCondition.makeCondition(listCondition);
        List<GenericValue> listAcctgTransSumFact = delegator.findList("AcctgTransPartySumFact", allCondition, fieldSelect, null, null, false);
        if (listAcctgTransSumFact.size() > 0) {
            GenericValue acctgTransSumFact = listAcctgTransSumFact.get(0);
            postedDebit = acctgTransSumFact.getBigDecimal("drAmount");
            postedCredit = acctgTransSumFact.getBigDecimal("crAmount");
        }

        switch (AccountUtils.getAccountType(glAccountId, delegator)) {
            case AccountUtils.CREDIT:
                balMap.put(CREDIT, balCrAmount.add(postedCredit.subtract(postedDebit)));
                balMap.put(DEBIT, BigDecimal.ZERO);
                break;
            case AccountUtils.DEBIT:
                balMap.put(DEBIT, balDrAmount.add(postedDebit.subtract(postedCredit)));
                balMap.put(CREDIT, BigDecimal.ZERO);
            default:
                break;
        }

        return balMap;
    }

    public static List<String> getListChildGlAccount(Delegator delegator, String glAccountId) {
        Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
        List<String> listAccountId = FastList.newInstance();
        List<Account> listAccount = acc.getListChild();
        for(Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountId.add(accEntity.getGlAccountId());
        }
        listAccountId.add(glAccountId);
        return listAccountId;
    }

    public static String getPreviousPeriod(String customTimePeriodId, Delegator delegator) {
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

    static void initCache() throws GenericEntityException {
        openingCache = CacheSingleton.getCacheByName(CacheUtils.BAL_OPENING_CACHE);
        endingCache = CacheSingleton.getCacheByName(CacheUtils.BAL_ENDING_CACHE);
        postedCache = CacheSingleton.getCacheByName(CacheUtils.BAL_POSTED_CACHE);
    }

    static void clearCache(Delegator delegator) {
        openingCache.clean(delegator);
        endingCache.clean(delegator);
        postedCache.clean(delegator);
    }
}