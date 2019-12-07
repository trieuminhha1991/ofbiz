package com.olbius.acc.report.financialstm;

import com.olbius.acc.utils.CacheUtils;
import com.olbius.entity.cache.OlbiusCache;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 11/16/18.
 */
public class TargetFormulaCache extends OlbiusCache<BigDecimal> {

    String isClosed;
    String reportIdM;
    String reportIdT;

    public TargetFormulaCache(Map<String, Long> config) {
        super(config);
    }

    @Override
    public BigDecimal loadCache(Delegator delegator, String key) throws Exception {
        return calculateTargetCache(delegator, key);
    }

    public void setParameters(String isClosed, String reportIdM, String reportIdT) {
        this.isClosed = isClosed;
        this.reportIdM = reportIdM;
        this.reportIdT = reportIdT;
    }

    private BigDecimal calculateTargetCache(Delegator delegator, String key) throws Exception {
        String[] tmp = key.split(CacheUtils.symbol);
        String strTargetId = tmp[0];
        String reportId = tmp[1];
        String strPeriodId = tmp[2];
        String strOrganizationPartyId = tmp[3];
        BigDecimal returnValue = new BigDecimal(0);
        GenericValue reportTarget;
        if ("Y".equals(isClosed)) {
            reportTarget = delegator.findOne("AccReportTarget", UtilMisc.toMap("targetId", strTargetId, "reportId", reportIdM), false);
        } else {
            reportTarget = delegator.findOne("AccReportTarget", UtilMisc.toMap("targetId", strTargetId, "reportId", reportIdT), false);
        }
        if (UtilValidate.isEmpty(reportTarget)) {
            return returnValue;
        }
        List<EntityCondition> listConds = new ArrayList<EntityCondition>();
        listConds.add(EntityCondition.makeCondition("parentTargetId", EntityOperator.EQUALS, strTargetId));
        listConds.add(EntityCondition.makeCondition("reportId", EntityOperator.EQUALS, reportId));
        List<GenericValue> tmpChildrenList = delegator.findList("AccReportTarget",
                EntityCondition.makeCondition(listConds), null, null, null, false);
        if (tmpChildrenList == null || tmpChildrenList.isEmpty()) {
            String strTmp = (String) reportTarget.get("formula");
            if (strTmp == null || strTmp.isEmpty()) {
                returnValue = returnValue.add(new BigDecimal(0));
            } else {
                returnValue = returnValue.add(Fomular.buildAndCalculate((String) reportTarget.get("formula"), strPeriodId,
                        delegator, strOrganizationPartyId));
            }
        } else {
            BigDecimal childValue;
            for (GenericValue genericValue2 : tmpChildrenList) {
                String childRawKey = genericValue2.getString("targetId") + ";" + reportId + ";" + strPeriodId + ";" + strOrganizationPartyId;
                childValue =  this.get(delegator, childRawKey);
                if (genericValue2.getString("unionSign") != null
                        && genericValue2.getString("unionSign").equals("S")) {
                    returnValue = returnValue.subtract(childValue);
                } else {
                    returnValue = returnValue.add(childValue);
                }
            }
        }
        return returnValue;
    }
}
