package com.olbius.acc.report.financialstm;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.ErrorUtils;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FinancialReportBuilder extends FinancialStmBuilder {

	public static final String module = FinancialReportBuilder.class.getName();

	@Override
	public List<FinancialStm> buildFinStm(Map<String, Object> parameters, Delegator delegator) {
		// Global Variables
		List<FinancialStm> listFinStms = new ArrayList<FinancialStm>();

		try {
			// Get current period
			String customTimePeriodId = (String) parameters.get("customTimePeriodId");
			GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
			String isClosed = customTimePeriod.getString("isClosed");
			String previousCustTimePeriodId = BalanceWorker.getPreviousPeriod(customTimePeriodId, delegator);
			String isClosedPrev = "";
			if (UtilValidate.isEmpty(previousCustTimePeriodId)) {
				previousCustTimePeriodId = customTimePeriodId;
				isClosedPrev = isClosed;
			} else {
				customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", previousCustTimePeriodId), false);
				isClosedPrev = customTimePeriod.getString("isClosed");
			}
			// Get report
			String organizationPartyId = (String) parameters.get("organizationPartyId");
			String reportTypeId = (String) parameters.get("reportTypeId");
			String flag = (String) parameters.get("flag");
			
			//Debug.log(module + "::buildFinStm, organizationId = " + organizationPartyId + ", reportTypeId = " + reportTypeId
			//		+ ", flag = " + flag); 
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
			listConds.add(EntityCondition.makeCondition("reportTypeId", reportTypeId));
			listConds.add(EntityCondition.makeCondition("flag", flag));
			List<GenericValue> listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
			String reportId = EntityUtil.getFirst(listReport).getString("reportId");
			
			listConds.clear();
			listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
			listConds.add(EntityCondition.makeCondition("reportTypeId", reportTypeId));
			listConds.add(EntityCondition.makeCondition("flag", "M"));
			listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
			String reportIdM = null;
			GenericValue reportM = EntityUtil.getFirst(listReport); 
			if(reportM != null)
				reportIdM = reportM.getString("reportId");
			
			listConds.clear();
			listConds.add(EntityCondition.makeCondition("partyId", organizationPartyId));
			listConds.add(EntityCondition.makeCondition("reportTypeId", reportTypeId));
			listConds.add(EntityCondition.makeCondition("flag", "T"));
			listReport = delegator.findList("AccReport", EntityCondition.makeCondition(listConds), null, null, null, false);
			String reportIdT = null;
			GenericValue reportT = EntityUtil.getFirst(listReport);
			if(reportT != null) reportIdT = reportT.getString("reportId");
			
			if (!UtilValidate.isEmpty(reportId)) {
				// Get report target
				listConds.clear();
				listConds.add(EntityCondition.makeCondition("reportId", reportId));
				listConds.add(EntityCondition.makeCondition("parentTargetId", null));
				List<GenericValue> listTarget = delegator.findList("AccReportTarget", EntityCondition.makeCondition(listConds, EntityOperator.AND), null,
						UtilMisc.toList("orderIndex"), null, false);

				// Set Financial Statement Tree
				for (GenericValue item : listTarget) {
					IncomeStm income = new IncomeStm();
					income.setCode(item.getString("code"));
					income.setTargetId(item.getString("targetId"));
					income.setName(item.getString("name"));
					income.setDemonstration(item.getString("demonstration"));
					income.setDisplaySign(item.getString("displaySign"));
                    income.setOrderIndex(BigDecimal.valueOf((long) item.get("orderIndex")));
					//income.setValue1(evalueTargetValue(delegator, item.getString("targetId"), reportId, reportIdM, reportIdT, customTimePeriodId, isClosed, organizationPartyId));
					//income.setValue2(evalueTargetValue(delegator, item.getString("targetId"), reportId, reportIdM, reportIdT, previousCustTimePeriodId, isClosedPrev, organizationPartyId));
					BigDecimal e1 = evalueTargetValue(delegator, item.getString("targetId"), reportId, reportIdM, reportIdT, customTimePeriodId, isClosed, organizationPartyId);
					BigDecimal e2 = evalueTargetValue(delegator, item.getString("targetId"), reportId, reportIdM, reportIdT, previousCustTimePeriodId, isClosedPrev, organizationPartyId);
					Debug.log(module + "::buildFinStm, name = " + item.getString("name") + ", targetId = " + item.getString("targetId") + 
							",e1 = " + e1 + ", e2 = " + e2);
					income.setValue1(e1);
					income.setValue2(e2);
					
					income.addAllChild(getChildrenTree(item.getString("targetId"), reportId, reportIdM, reportIdT, customTimePeriodId, isClosed,
							previousCustTimePeriodId, isClosedPrev, delegator, organizationPartyId));
					listFinStms.add(income);
				}
			}
		} catch (Exception e) {
			ErrorUtils.processException(e, module);
		}
		return listFinStms;
	}

	private static List<FinancialStm> getChildrenTree(String strParentId, String reportId, String reportIdM, String reportIdT, String strPeriodId1, String isClosed,
                                                      String strPeriodId2, String isClosedPrev, Delegator delegator, String strOrganizationPartyId) throws Exception {
		List<EntityCondition> listConds = new ArrayList<EntityCondition>();
		listConds.add(EntityCondition.makeCondition("reportId", reportId));
		listConds.add(EntityCondition.makeCondition("parentTargetId", EntityOperator.EQUALS, strParentId));
		List<GenericValue> tmpList = delegator.findList("AccReportTarget", EntityCondition.makeCondition(listConds),
				null, UtilMisc.toList("orderIndex"), null, false);
		if (tmpList == null || tmpList.isEmpty()) {
			return null;
		} else {
			List<FinancialStm> listReturn = new ArrayList<FinancialStm>();
			for (GenericValue elm : tmpList) {
				IncomeStm child = new IncomeStm();
				child.setCode(elm.getString("code"));
				child.setTargetId(elm.getString("targetId"));
				child.setName(elm.getString("name"));
				child.setDemonstration(elm.getString("demonstration"));
				child.setDisplaySign(elm.getString("displaySign"));
                child.setOrderIndex(BigDecimal.valueOf((long) elm.get("orderIndex")));
				child.setValue1(evalueTargetValue(delegator, elm.getString("targetId"), reportId, reportIdM, reportIdT, strPeriodId1, isClosed, strOrganizationPartyId));
				child.setValue2(evalueTargetValue(delegator, elm.getString("targetId"), reportId, reportIdM, reportIdT, strPeriodId2, isClosedPrev, strOrganizationPartyId));
				List<FinancialStm> listChildren = getChildrenTree((String) elm.get("targetId"), reportId, reportIdM, reportIdT, strPeriodId1, isClosed,
						strPeriodId2, isClosedPrev, delegator, strOrganizationPartyId);
				// check for leaf node
				child.addAllChild(listChildren);
				listReturn.add(child);
			}
			return listReturn;
		}
	}
}