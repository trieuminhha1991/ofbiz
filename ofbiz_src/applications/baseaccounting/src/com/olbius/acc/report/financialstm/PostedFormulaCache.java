package com.olbius.acc.report.financialstm;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.ErrorUtils;
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
public class PostedFormulaCache extends FormulaCache {

    private final static String MODULE = PostedFormulaCache.class.getName();
    public PostedFormulaCache(Map<String, Long> config) {
        super(config);
    }

    @Override
    public Map<String, BigDecimal> loadCache(Delegator delegator, String key) throws Exception {
        return calculatePostedFormula(delegator, key);
    }

    private Map<String, BigDecimal> calculatePostedFormula(Delegator delegator, String key) throws GenericEntityException {
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
        BigDecimal debAmount = BigDecimal.ZERO;
        BigDecimal creAmount = BigDecimal.ZERO;
        List<GenericValue> acctgCustomTimePeriodSumFactList = null;
        try {
            List<String> listChildGlAcc = Fomular.getAllGlAccountIdChildren(glAccountId, delegator);
            listChildGlAcc.add(glAccountId);
            //Get Account transaction in custom time period
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
            conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
            conds.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listChildGlAcc));
            acctgCustomTimePeriodSumFactList = delegator.findList("AcctgCustomTimePeriodSumFact", EntityCondition.makeCondition(conds), null, null, null, false);
            if  (UtilValidate.isNotEmpty(acctgCustomTimePeriodSumFactList)) {
                for (GenericValue item : acctgCustomTimePeriodSumFactList) {
                    debAmount = debAmount.add(item.getBigDecimal("drAmount"));
                    creAmount = creAmount.add(item.getBigDecimal("crAmount"));
                }
            }
            balMap.put(BalanceWorker.DEBIT, debAmount);
            balMap.put(BalanceWorker.CREDIT, creAmount);
        } catch (GenericEntityException e) {
            ErrorUtils.processException(e, MODULE);
        }
        return balMap;
    }
}
