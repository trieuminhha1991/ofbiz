package com.olbius.salesmtl.report;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.bi.olap.query.condition.Condition;

public class DepartmentDetector {
	public static Condition prevent(Delegator delegator, GenericValue userLogin, Locale locale) throws Exception {
		Condition condition = new Condition();
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		List<GenericValue> parties = delegator.findList("PartyDimension",
				EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, organizationId), UtilMisc.toSet("dimensionId"), null, null, false);
		if (UtilValidate.isNotEmpty(parties)) {
			Long dimensionId = (Long) EntityUtil.getFirst(parties).get("dimensionId");
			condition.and("organ_dep", "=", dimensionId);
		}
		String field = "";
		if (SalesPartyUtil.isSalessup(delegator, userLogin.getString("partyId"))) {
			field = "sup_dep";
		} else if (SalesPartyUtil.isSalesASM(delegator, userLogin.getString("partyId"))) {
			field = "asm_dep";
		} else if (SalesPartyUtil.isSalesRSM(delegator, userLogin.getString("partyId"))) {
			field = "rsm_dep";
		} else if (SalesPartyUtil.isSalesCSM(delegator, userLogin.getString("partyId"))) {
			field = "csm_dep";
		} else if (SalesPartyUtil.isSalesAdmin(delegator, userLogin.getString("partyId")) || SalesPartyUtil.isSalesManager(delegator, userLogin.getString("partyId"))) {
			field = "";
		} else {
			throw new Exception(UtilProperties.getMessage("BaseAccountingUiLabels",
					"BACCYouHavenotViewPermission", locale));
		}
		if (UtilValidate.isNotEmpty(field)) {
			List<String> departments = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
			parties = delegator.findList("PartyDimension",
					EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, departments), UtilMisc.toSet("dimensionId"), null, null, false);
			if (UtilValidate.isNotEmpty(parties)) {
				List<Object> dimensionIds = EntityUtil.getFieldListFromEntityList(parties, "dimensionId", true);
				condition.andIn(field, dimensionIds);
			}
		}
		return condition;
	}
}
