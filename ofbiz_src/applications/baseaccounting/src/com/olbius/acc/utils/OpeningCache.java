package com.olbius.acc.utils;

import com.olbius.acc.report.balancetrial.BalanceWorker;
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
public class OpeningCache extends OlbiusCache<Map<String, BigDecimal>> {

    public static final String MODULE = OpeningCache.class.getName();
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";

    private EndingCache endingCache;
    void setEndingCache(EndingCache endingCache) {
        this.endingCache = endingCache;
    }

    OpeningCache(Map<String, Long> config) {
        super(config);
    }

    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        String[] tmp = key.split(CacheUtils.symbol);
        if(tmp.length == 4) {
            return getOpeningByParty(delegator, key);
        } else {
            return getOpening(delegator, key);
        }
    }

     private Map<String, BigDecimal> getOpeningByParty(Delegator delegator, String key) throws GenericEntityException, NoSuchAlgorithmException {
        String[] tmp = key.split(CacheUtils.symbol);
        String partyId = tmp[0];
        String glAccountId = tmp[1];
        String customTimePeriodId = tmp[2];
        String orgPartyId = tmp[3];
        Map<String, BigDecimal> balMap = FastMap.newInstance();
        GenericValue glAccHisParty = delegator.findOne("GlAccountHistoryParty", UtilMisc.toMap("partyId", partyId, "glAccountId", glAccountId,
                "organizationPartyId", orgPartyId, "customTimePeriodId", customTimePeriodId), false);
        if(!UtilValidate.isEmpty(glAccHisParty)) {
            //If has closed period
            balMap.put(DEBIT, glAccHisParty.getBigDecimal("openingDrBalance"));
            balMap.put(CREDIT, glAccHisParty.getBigDecimal("openingCrBalance"));
        } else {
            //If hasn't closed period
            String previousPeriodId = BalanceWorker.getPreviousPeriod(customTimePeriodId, delegator);
            if(UtilValidate.isEmpty(previousPeriodId)) {
                GenericValue bal = delegator.findOne("GlAccountBalanceParty", UtilMisc.toMap("glAccountId",  glAccountId,
                        "organizationPartyId", orgPartyId, "partyId", partyId), false);
                if(bal != null) {
                    balMap.put(DEBIT, bal.getBigDecimal("openingDrBalance"));
                    balMap.put(CREDIT, bal.getBigDecimal("openingCrBalance"));
                } else {
                    balMap.put(DEBIT, BigDecimal.ZERO);
                    balMap.put(CREDIT, BigDecimal.ZERO);
                }
            } else {
                String endingKey = partyId + ";" + glAccountId + ";" + previousPeriodId + ";" + orgPartyId;
                balMap = endingCache.get(delegator, endingKey);
            }
        }
        return balMap;
    }

    private Map<String, BigDecimal> getOpening(Delegator delegator, String key) throws GenericEntityException {
        String[] tmp = key.split(CacheUtils.symbol);
        String glAccountId = tmp[0];
        String customTimePeriodId = tmp[1];
        String orgPartyId = tmp[2];
        Map<String, BigDecimal> balMap = new HashMap<String, BigDecimal>();
        //Get closed balance
        GenericValue glAccHis = delegator.findOne("GlAccountHistory", UtilMisc.toMap("glAccountId", glAccountId, "organizationPartyId", orgPartyId, "customTimePeriodId", customTimePeriodId), false);
        if(!UtilValidate.isEmpty(glAccHis)) {
            //If has closed period
            BigDecimal openingDrBalance = glAccHis.getBigDecimal("openingDrBalance") == null ? BigDecimal.ZERO : glAccHis.getBigDecimal("openingDrBalance");
            BigDecimal openingCrBalance = glAccHis.getBigDecimal("openingCrBalance") == null ? BigDecimal.ZERO : glAccHis.getBigDecimal("openingCrBalance");
            balMap.put(DEBIT, openingDrBalance);
            balMap.put(CREDIT, openingCrBalance);
        } else {
            //If hasn't closed period
            String previousPeriodId = BalanceWorker.getPreviousPeriod(customTimePeriodId, delegator);
            if(UtilValidate.isEmpty(previousPeriodId)) {
                GenericValue bal = delegator.findOne("GlAccountBalance", UtilMisc.toMap("glAccountId",  glAccountId, "organizationPartyId", orgPartyId), false);
                if(bal != null) {
                    BigDecimal openingDrBalance = bal.getBigDecimal("openingDrBalance") == null ? BigDecimal.ZERO : bal.getBigDecimal("openingDrBalance");
                    BigDecimal openingCrBalance = bal.getBigDecimal("openingCrBalance") == null ? BigDecimal.ZERO : bal.getBigDecimal("openingCrBalance");
                    balMap.put(DEBIT, openingDrBalance);
                    balMap.put(CREDIT, openingCrBalance);
                } else {
                    balMap.put(DEBIT, BigDecimal.ZERO);
                    balMap.put(CREDIT, BigDecimal.ZERO);
                }
            } else {
                String endingKey = glAccountId + ";" + previousPeriodId + ";" + orgPartyId;
                balMap = endingCache.get(delegator, endingKey);
            }
        }
        return balMap;
    }
}
