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
public class OpeningFormulaCache extends FormulaCache {

    private FormulaCache endingCache;
    public OpeningFormulaCache(Map<String, Long> config) {
        super(config);
    }

    public void setEndingCache(FormulaCache endingCache) {
        this.endingCache = endingCache;
    }
    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        return calculateOpeningFormula(delegator, key);
    }

    private Map<String, BigDecimal> calculateOpeningFormula(Delegator delegator, String key) throws GenericEntityException {
        Map<String, BigDecimal> balMap = new HashMap<>();
        String[] tmp = key.split(symbol);

        String glAccountCode = tmp[0];
        String customTimePeriodId = tmp[1];
        String orgPartyId = tmp[2];
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
        listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", orgPartyId, "customTimePeriodId", customTimePeriodId)));
        List<GenericValue> glAccHisList = delegator.findList("GlAccountHistory", EntityCondition.makeCondition(listCond), null, null, null, false);
        if (!UtilValidate.isEmpty(glAccHisList)) {
            BigDecimal openingDrBalance = BigDecimal.ZERO;
            BigDecimal openingCrBalance = BigDecimal.ZERO;

            for (GenericValue item : glAccHisList) {
                if (item.getBigDecimal("openingDrBalance") == null) {
                    openingDrBalance = openingDrBalance.add(BigDecimal.ZERO);
                } else {
                    openingDrBalance = openingDrBalance.add(item.getBigDecimal("openingDrBalance"));
                }
                if (item.getBigDecimal("openingCrBalance") == null) {
                    openingCrBalance = openingCrBalance.add(BigDecimal.ZERO);
                } else {
                    openingCrBalance = openingCrBalance.add(item.getBigDecimal("openingCrBalance"));
                }
            }
            balMap.put(BalanceWorker.DEBIT, openingDrBalance);
            balMap.put(BalanceWorker.CREDIT, openingCrBalance);
        } else {
            String previousPeriodId = Fomular.getPreviousPeriod(customTimePeriodId, delegator);
            if (UtilValidate.isEmpty(previousPeriodId)) {
                List<EntityCondition> conds = FastList.newInstance();
                conds.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listChildGlAcc));
                conds.add(EntityCondition.makeCondition("organizationPartyId", orgPartyId));
                conds.add(EntityCondition.makeCondition(
                        EntityCondition.makeCondition("openingDrBalance", EntityOperator.GREATER_THAN, BigDecimal.ZERO),
                        EntityOperator.OR,
                        EntityCondition.makeCondition("openingCrBalance", EntityOperator.GREATER_THAN, BigDecimal.ZERO))
                );
                List<GenericValue> balList = delegator.findList("GlAccountBalance", EntityCondition.makeCondition(conds),
                        null, null, null, false);
                BigDecimal debit = BigDecimal.ZERO;
                BigDecimal credit = BigDecimal.ZERO;
                for(GenericValue bal: balList) {
                    debit = debit.add(bal.get("openingDrBalance") != null ? bal.getBigDecimal("openingDrBalance") : BigDecimal.ZERO);
                    credit = credit.add(bal.get("openingCrBalance") != null ? bal.getBigDecimal("openingCrBalance") : BigDecimal.ZERO);
                }
                balMap.put(BalanceWorker.DEBIT, debit);
                balMap.put(BalanceWorker.CREDIT, credit);
            } else {
                String endKey = glAccountCode + ";" + previousPeriodId + ";" + orgPartyId;
                balMap =  endingCache.get(delegator, endKey);
            }
        }
        return balMap;
    }
}
