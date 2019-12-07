package com.olbius.salesmtl.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.util.CRMUtils;

import javolution.util.FastList;

public class FilterSupport {
	
	public static class ListDistributor {
		
		@SuppressWarnings("unchecked")
		public static List<EntityCondition> makeCondition(Map<String, ? extends Object> context, Delegator delegator) throws GenericEntityException {
			List<EntityCondition> conditions = FastList.newInstance();
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			for (EntityCondition e : listAllConditions) {
				String condition = e.toString();
				String[] conditionSplitted = condition.split(" ");
				if (conditionSplitted.length < 2) {
					continue;
				}
				String fieldName = conditionSplitted[0];
				fieldName = CRMUtils.cleanFieldName(fieldName);
				
				String operator = conditionSplitted[1];
				if ("LIKE".equals(operator)) {
					conditionSplitted = condition.split(" '%");
					if (conditionSplitted.length < 2) {
						continue;
					}
					String value = conditionSplitted[1].trim();
					value = CRMUtils.cleanValue(value).toUpperCase();
					if (UtilValidate.isEmpty(value)) {
						continue;
					}
					switch (fieldName) {
					case "supervisor":
						List<String> partyIds = searchSupervisor(delegator, value);
						conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, partyIds));
						break;
					default:
						conditions.add(e);
						break;
					}
				} else {
					conditions.add(e);
				}
			}
			return conditions;
		}
		private static List<String> searchSupervisor(Delegator delegator, String value) throws GenericEntityException {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityJoinOperator.EQUALS, "SALESSUP_DEPT_GT"));
			conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("roleTypeIdTo"), EntityJoinOperator.LIKE, "%" + value.replaceAll("\\s+", "%") + "%"));
			List<GenericValue> supervisors = delegator.findList("PartyToAndPartyNameDetail",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
			List<String> partyIds = EntityUtil.getFieldListFromEntityList(supervisors, "partyId", true);
			return partyIds;
		}
	}
	
}
