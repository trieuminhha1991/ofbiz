package com.olbius.baselogistics.requirement;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.olbius.product.util.ProductUtil;

import javolution.util.FastList;

public class RequirementWorker {
	private static String module = RequirementWorker.class.getName();
	public static BigDecimal calculateReuqirementGrandTotal(Delegator delegator, String requirementId){
		BigDecimal grandTotal = BigDecimal.ZERO;
		
		GenericValue objRequirement = null;
		try {
			objRequirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Requirement: " + e.toString();
			Debug.logError(e, errMsg, module);
			return BigDecimal.ZERO;
		}
		if (UtilValidate.isNotEmpty(objRequirement)) {
			String statusId = objRequirement.getString("statusId");
			EntityCondition cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
			EntityCondition cond2 = EntityCondition.makeCondition("quantity", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(cond1);
			conds.add(cond2);
			List<GenericValue> listItems = FastList.newInstance();
			if (!"REQ_CANCELLED".equals(statusId) && !"REQ_REJECTED".equals(statusId)){
				EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_CANCELLED");
				EntityCondition cond4 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_REJECTED");
				conds.add(cond3);
				conds.add(cond4);
			}
			try {
				listItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList RequirementItem: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			if (!listItems.isEmpty()){
				for (GenericValue item : listItems) {
					String productId = item.getString("productId");
					if (ProductUtil.isWeightProduct(delegator, productId)){
						if (UtilValidate.isNotEmpty(item.getBigDecimal("weight")) && UtilValidate.isNotEmpty(item.getBigDecimal("unitCost"))) {
							BigDecimal itemTotal = item.getBigDecimal("weight").multiply(item.getBigDecimal("unitCost"));
							grandTotal = grandTotal.add(itemTotal);
						}
					} else {
						if (UtilValidate.isNotEmpty(item.get("unitCost"))) {
							BigDecimal itemTotal = item.getBigDecimal("quantity").multiply(item.getBigDecimal("unitCost"));
							grandTotal = grandTotal.add(itemTotal);
						}
					}
				}
			}
		}
		return grandTotal;
	}
}
