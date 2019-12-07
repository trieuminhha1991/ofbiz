package org.ofbiz.mobileservices;

import java.sql.Timestamp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

public class SynchronizeServices {
	public static final String module = SynchronizeServices.class.getName();
	public static final String resource_error = "SynchronizeServicesErrorUiLabels";

	public static Map<String, Object> getPromotions(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		Timestamp cur = UtilDateTime.nowTimestamp();
		List<EntityCondition> allConditions = FastList.newInstance();
		allConditions.add(EntityCondition.makeCondition("productPromoTypeId",
				EntityOperator.EQUALS, "PROMOTION"));
		allConditions.add(EntityCondition.makeCondition("productPromoStatusId",
				EntityOperator.EQUALS, "PROMO_ACCEPTED"));
		allConditions.add(EntityCondition.makeCondition("showToCustomer",
				EntityOperator.EQUALS, "Y"));
		allConditions.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "DELYS_CUSTOMER_GT"));
		EntityCondition now = EntityUtil.getFilterByDateExpr(cur);
		allConditions.add(now);
		EntityCondition queryConditionsList = EntityCondition.makeCondition(
				allConditions, EntityOperator.AND);
		Set<String> fields = FastSet.newInstance();
		fields.add("productPromoId");
		fields.add("productId");
		fields.add("productPromoRuleId");
		fields.add("orderAdjustmentTypeId");
		fields.add("productPromoActionEnumId");
		fields.add("productPromoCondSeqId");
		fields.add("productPromoActionSeqId");
		fields.add("inputParamEnumId");
		fields.add("productPromoApplEnumId");
		fields.add("operatorEnumId");
		fields.add("otherValue");
		fields.add("quantity");
		fields.add("amount");
		fields.add("partyId");
		fields.add("fromDate");
		fields.add("thrudate");
		fields.add("condValue");
		fields.add("ruleName");
		try {
			List<GenericValue> promotions = delegator.findList("PromosEvents",
					queryConditionsList, null, UtilMisc.toList("fromDate"),
					options, true);
			res.put("promotions", promotions);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
