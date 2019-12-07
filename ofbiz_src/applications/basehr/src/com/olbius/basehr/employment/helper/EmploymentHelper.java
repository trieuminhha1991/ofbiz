package com.olbius.basehr.employment.helper;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.olbius.basehr.util.EntityConditionUtils;

public class EmploymentHelper {
	public static List<String> getPositionNotFulfillInPeriod(Delegator delegator, String partyId, String emplPositionTypeId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		List<String> retList = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate, "actualFromDate", "actualThruDate"));
		EntityCondition positionNotFulfilConds = EntityCondition.makeCondition(EntityCondition.makeCondition("employeePartyId", null),
				EntityJoinOperator.OR,
				EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null),
												EntityJoinOperator.AND,
												EntityCondition.makeCondition("thruDate", EntityJoinOperator.LESS_THAN, fromDate)));
		List<GenericValue> positionMayNotFulfillment = delegator.findList("AllEmplPositionAndFulfillment", 
				EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityJoinOperator.AND, positionNotFulfilConds), null, null, null, false);
		
		if(UtilValidate.isNotEmpty(positionMayNotFulfillment)){
			EntityCondition dateFulfillmentConds = EntityConditionUtils.makeDateConds(fromDate, thruDate);
			for(GenericValue tempEmplPositionFulGv: positionMayNotFulfillment){
				String emplPositionId = tempEmplPositionFulGv.getString("emplPositionId");
				List<GenericValue> tempEmplPositionFul = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition(dateFulfillmentConds, EntityJoinOperator.AND,
						EntityCondition.makeCondition("emplPositionId", emplPositionId)), null, null, null, false);
				if(UtilValidate.isEmpty(tempEmplPositionFul)){
					retList.add(emplPositionId);
				}
			}
		}
		return retList;
	}
}
