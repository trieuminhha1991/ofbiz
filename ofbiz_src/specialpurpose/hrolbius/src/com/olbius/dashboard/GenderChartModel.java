package com.olbius.dashboard;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

public class GenderChartModel extends ChartModel{

	@Override
	public void buildModel(Delegator delegator) {
		double femaleNumber = getFemaleEmpl(delegator);
		double maleNumber =  getMaleEmpl(delegator);
		model.put("F", femaleNumber);
		model.put("M", maleNumber);
	}
	
	private int getFemaleEmpl(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("genderDimId", "NU"), EntityJoinOperator.AND);
		List<GenericValue> females = FastList.newInstance();
		try {
			females = delegator.findList("EmplPositionTypeFact",condition , null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		int count = 0;
		for(GenericValue item: females){
			count += item.getBigDecimal("employeeNumber").intValue();
		}
		return count;
	}
	
	private int getMaleEmpl(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("genderDimId", "NAM"), EntityJoinOperator.AND);
		List<GenericValue> females = FastList.newInstance();
		try {
			females = delegator.findList("EmplPositionTypeFact",condition , null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		int count = 0;
		for(GenericValue item: females){
			count += item.getBigDecimal("employeeNumber").intValue();
		}
		return count;
	}
}
