package com.olbius.acc.utils;

import com.olbius.entity.cache.OlbiusCache;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 11/15/18.
 */
public class PostedCache extends OlbiusCache<Map<String, BigDecimal>> {

    public static final String MODULE = PostedCache.class.getName();
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";

    PostedCache(Map<String, Long> config) {
        super(config);
    }
    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        String[] tmp = key.split(CacheUtils.symbol);
        if(tmp.length == 4) {
            return getPostedByParty(delegator, key);
        } else {
            return getPosted(delegator, key);
        }
    }

    private Map<String, BigDecimal> getPostedByParty(Delegator delegator, String key) throws GenericEntityException, NoSuchAlgorithmException {
        String[] tmp = key.split(CacheUtils.symbol);
        String partyId = tmp[0];
        String glAccountId = tmp[1];
        String customTimePeriodId = tmp[2];
        String organizationPartyId = tmp[3];
        Map<String, BigDecimal> balMap = FastMap.newInstance();
        BigDecimal debAmount = BigDecimal.ZERO;
        BigDecimal creAmount = BigDecimal.ZERO;
        GenericValue acctgCustomTimePartySumFact = null;
        try {
            GenericValue glAccHisParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("partyId", partyId, "glAccountId", glAccountId,
                    "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId), false);
            if(!UtilValidate.isEmpty(glAccHisParty)) {
                //If has closed period
                balMap.put(DEBIT, glAccHisParty.getBigDecimal("postedDebits"));
                balMap.put(CREDIT, glAccHisParty.getBigDecimal("postedCredits"));
            } else {
                //Get Account transaction in custom time period
                Map<String, Object> findAcctgCustomTimePeriodSum = new FastMap<String, Object>();
                findAcctgCustomTimePeriodSum.put("glAccountId", glAccountId);
                findAcctgCustomTimePeriodSum.put("partyId", partyId);
                findAcctgCustomTimePeriodSum.put("organizationPartyId", organizationPartyId);
                findAcctgCustomTimePeriodSum.put("customTimePeriodId", customTimePeriodId);
                acctgCustomTimePartySumFact = delegator.findOne("AcctgCustomTimePartySumFact", findAcctgCustomTimePeriodSum, false);

                //Calculate balance
                if  (acctgCustomTimePartySumFact != null) {
                    debAmount = debAmount.add(acctgCustomTimePartySumFact.getBigDecimal("drAmount"));
                    creAmount = creAmount.add(acctgCustomTimePartySumFact.getBigDecimal("crAmount"));
                }
                balMap.put(DEBIT, debAmount);
                balMap.put(CREDIT, creAmount);
            }
        } catch (GenericEntityException e) {
            ErrorUtils.processException(e, MODULE);
        }
        return balMap;
    }

    private Map<String, BigDecimal> getPosted(Delegator delegator, String key) throws GenericEntityException {
        String[] tmp = key.split(CacheUtils.symbol);
        String glAccountId = tmp[0];
        String customTimePeriodId = tmp[1];
        String organizationPartyId = tmp[2];
        Map<String, BigDecimal> balMap = new HashMap<>();
        BigDecimal debAmount = BigDecimal.ZERO;
        BigDecimal creAmount = BigDecimal.ZERO;
        //Get closed balance
        GenericValue glAccHis = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId), false);
        if(!UtilValidate.isEmpty(glAccHis)) {
            //If has closed period
            balMap.put(DEBIT, glAccHis.getBigDecimal("postedDebits"));
            balMap.put(CREDIT, glAccHis.getBigDecimal("postedCredits"));
        } else {
            //Get Account transaction in custom time period
            Map<String, Object> findAcctgCustomTimePeriodSum = new FastMap<String, Object>();
            findAcctgCustomTimePeriodSum.put("glAccountId", glAccountId);
            findAcctgCustomTimePeriodSum.put("organizationPartyId", organizationPartyId);
            findAcctgCustomTimePeriodSum.put("customTimePeriodId", customTimePeriodId);
            GenericValue acctgCustomTimePeriodSumFact = delegator.findOne("AcctgCustomTimePeriodSumFact", findAcctgCustomTimePeriodSum, false);

            //Calculate balance
            if  (acctgCustomTimePeriodSumFact != null) {
                debAmount = debAmount.add(acctgCustomTimePeriodSumFact.getBigDecimal("drAmount"));
                creAmount = creAmount.add(acctgCustomTimePeriodSumFact.getBigDecimal("crAmount"));
            }
            balMap.put(DEBIT, debAmount);
            balMap.put(CREDIT, creAmount);
        }

        return balMap;
    }
}
