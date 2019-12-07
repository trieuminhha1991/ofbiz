package com.olbius.acc.report.financialstm;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 11/16/18.
 */
public class EndingFormulaCache extends FormulaCache {

    private FormulaCache openingCache;
    private FormulaCache postedCache;

    public EndingFormulaCache(Map<String, Long> config) {
        super(config);
    }

    public void setOpeningCache(FormulaCache openingCache) {
        this.openingCache = openingCache;
    }
    public void setPostedCache(FormulaCache postedCache) {
        this.postedCache = postedCache;
    }
    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        return calculateEndingCache(delegator, key);
    }

    private Map<String, BigDecimal> calculateEndingCache(Delegator delegator, String key) throws GenericEntityException {
        Map<String, BigDecimal> balMap = new HashMap<>();
        String[] tmp = key.split(symbol);

        String glAccountCode = tmp[0];
        String customTimePeriodId = tmp[1];
        String organizationPartyId = tmp[2];
        String glAccountId = Fomular.getAccountId(glAccountCode, delegator);
        if (glAccountId == null) {
            balMap.put(BalanceWorker.DEBIT, BigDecimal.ZERO);
            balMap.put(BalanceWorker.CREDIT, BigDecimal.ZERO);
            return balMap;
        }
        List<String> listChildGlAcc = Fomular.getAllGlAccountIdChildren(glAccountId, delegator);
        listChildGlAcc.add(glAccountId);
        List<EntityCondition> listCond = new ArrayList<EntityCondition>();
        listCond.add(EntityCondition.makeCondition("glAccountId", EntityJoinOperator.IN, listChildGlAcc));
        listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", organizationPartyId, "customTimePeriodId", customTimePeriodId)));
        List<GenericValue> glAccHisList = delegator.findList("GlAccountHistory", EntityCondition.makeCondition(listCond), null, null, null, false);
        if (!UtilValidate.isEmpty(glAccHisList)) {
            BigDecimal endingDrBalance = BigDecimal.ZERO;
            BigDecimal endingCrBalance = BigDecimal.ZERO;
            for (GenericValue item : glAccHisList) {
                if (item.getBigDecimal("endingDrBalance") == null) {
                    endingDrBalance = endingDrBalance.add(BigDecimal.ZERO);
                } else {
                    endingDrBalance = endingDrBalance.add(item.getBigDecimal("endingDrBalance"));
                }
                if (item.getBigDecimal("endingCrBalance") == null) {
                    endingCrBalance = endingCrBalance.add(BigDecimal.ZERO);
                } else {
                    endingCrBalance = endingCrBalance.add(item.getBigDecimal("endingCrBalance"));
                }
            }
            balMap.put(BalanceWorker.DEBIT, endingDrBalance);
            balMap.put(BalanceWorker.CREDIT, endingCrBalance);
        } else {
            Map<String, BigDecimal> openingBalance = openingCache.get(delegator, key);
            BigDecimal openingDebBal = openingBalance.get(BalanceWorker.DEBIT);
            BigDecimal openingCreBal = openingBalance.get(BalanceWorker.CREDIT);
            Map<String, BigDecimal> postedAmount = postedCache.get(delegator, key);
            if (!UtilValidate.isEmpty(postedAmount)) {
                balMap.put(BalanceWorker.CREDIT, (openingCreBal.add(postedAmount.get(BalanceWorker.CREDIT))));
                balMap.put(BalanceWorker.DEBIT, (openingDebBal.add(postedAmount.get(BalanceWorker.DEBIT))));
            }
        }
        return balMap;
    }
}
