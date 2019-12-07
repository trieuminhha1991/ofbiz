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

public class QualificationChartModel extends ChartModel{

	@Override
	public void buildModel(Delegator delegator) {
		Integer uniDegree = getUniDegree(delegator);
		Integer masterDegree = getMasterDegree(delegator);
		Integer collegeDegree = getcollegeDegree(delegator);
		Integer intermediateDegree = getIntermediateDegree(delegator);
		Integer secondaryDegree = getSecondaryDegree(delegator);
		Integer baseDegree = getBaseDegree(delegator);
		
		model.put("uniDegree", uniDegree.doubleValue());
		model.put("masterDegree", masterDegree.doubleValue());
		model.put("collegeDegree", collegeDegree.doubleValue());
		model.put("intermediateDegree", intermediateDegree.doubleValue());
		model.put("secondaryDegree", secondaryDegree.doubleValue());
		model.put("baseDegree", baseDegree.doubleValue());
	}
	
	private int getUniDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDDH"), EntityJoinOperator.AND);
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

	private int getMasterDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDTS"), EntityJoinOperator.AND);
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
	
	private int getcollegeDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDCD"), EntityJoinOperator.AND);
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
	
	private int getIntermediateDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDTC"), EntityJoinOperator.AND);
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
	
	private int getSecondaryDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDPT"), EntityJoinOperator.AND);
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
	
	private int getBaseDegree(Delegator delegator){
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("qualificationDimId", "TDCS"), EntityJoinOperator.AND);
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
