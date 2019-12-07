package com.olbius.acc.utils;

import com.olbius.acc.utils.accounts.AccountUtils;
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
public class EndingCache extends OlbiusCache<Map<String, BigDecimal>> {

    public static final String MODULE = PostedCache.class.getName();
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";
    private OpeningCache openingCache;
    private PostedCache postedCache;

    EndingCache(Map<String, Long> config) {
        super(config);
    }
    public void setOpeningCache(OpeningCache openingCache) {
        this.openingCache = openingCache;
    }
    public void setPostedCache(PostedCache postedCache) {
        this.postedCache = postedCache;
    }
    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        String[] tmp = key.split(CacheUtils.symbol);
        if(tmp.length == 4) {
            return getEndingByParty(delegator, key);
        } else {
            return getEnding(delegator, key);
        }
    }

    private Map<String, BigDecimal> getEndingByParty(Delegator delegator, String key) throws GenericEntityException, NoSuchAlgorithmException {
        String[] tmp = key.split(CacheUtils.symbol);
        String partyId = tmp[0];
        String glAccountId = tmp[1];
        String customTimePeriodId = tmp[2];
        String organizationPartyId = tmp[3];
        Map<String, BigDecimal> balMap = FastMap.newInstance();
        GenericValue glAccHisParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("partyId", partyId, "glAccountId", glAccountId,
                "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId), false);
        if(!UtilValidate.isEmpty(glAccHisParty)) {
            //If has closed period
            balMap.put(DEBIT, glAccHisParty.getBigDecimal("endingDrBalance"));
            balMap.put(CREDIT, glAccHisParty.getBigDecimal("endingCrBalance"));
        } else {
            //Get opening Balance
            Map<String, BigDecimal> openingBalance = openingCache.get(delegator, key);
            //Calculate balance
            BigDecimal openingDebBal = openingBalance.get(DEBIT);
            BigDecimal openingCreBal = openingBalance.get(CREDIT);
            Map<String, BigDecimal> postedAmount = postedCache.get(delegator, key);
            switch (AccountUtils.getAccountType(glAccountId, delegator)) {
                case AccountUtils.CREDIT:
                    balMap.put(CREDIT, openingCreBal.add(postedAmount.get(CREDIT).subtract(postedAmount.get(DEBIT))));
                    balMap.put(DEBIT, BigDecimal.ZERO);
                    break;
                case AccountUtils.DEBIT:
                    balMap.put(DEBIT, openingDebBal.add(postedAmount.get(DEBIT).subtract(postedAmount.get(CREDIT))));
                    balMap.put(CREDIT, BigDecimal.ZERO);
                default:
                    break;
            }
        }
        return balMap;
    }

    private Map<String, BigDecimal> getEnding(Delegator delegator, String key) throws GenericEntityException {
        String[] tmp = key.split(CacheUtils.symbol);
        String glAccountId = tmp[0];
        String customTimePeriodId = tmp[1];
        String organizationPartyId = tmp[2];
        Map<String, BigDecimal> balMap = new HashMap<>();
        GenericValue glAccHis = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId), false);
        if(!UtilValidate.isEmpty(glAccHis)) {
            balMap.put(DEBIT, glAccHis.getBigDecimal("endingDrBalance"));
            balMap.put(CREDIT, glAccHis.getBigDecimal("endingCrBalance"));
        } else {
            Map<String, BigDecimal> openingBalance = openingCache.get(delegator, key);
            BigDecimal openingDebBal = openingBalance.get(DEBIT) != null ? openingBalance.get(DEBIT) : BigDecimal.ZERO;
            BigDecimal openingCreBal = openingBalance.get(CREDIT) != null ? openingBalance.get(CREDIT) : BigDecimal.ZERO;
            Map<String, BigDecimal> postedAmount = postedCache.get(delegator, key);
            switch (AccountUtils.getAccountType(glAccountId, delegator)) {
                case AccountUtils.CREDIT:
                    balMap.put(CREDIT, openingCreBal.add(postedAmount.get(CREDIT).subtract(postedAmount.get(DEBIT))));
                    balMap.put(DEBIT, BigDecimal.ZERO);
                    break;
                case AccountUtils.DEBIT:
                    balMap.put(DEBIT, openingDebBal.add(postedAmount.get(DEBIT).subtract(postedAmount.get(CREDIT))));
                    balMap.put(CREDIT, BigDecimal.ZERO);
                default:
                    break;
            }
        }
        return balMap;
    }
}
