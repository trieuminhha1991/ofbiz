package com.olbius.util;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

public abstract class Organization {
	protected GenericValue org;
	private String orgType;
	public List<Organization> childList = FastList.newInstance();
	public GenericValue getOrg() {
		return org;
	}
	
	public void setOrg(GenericValue org) {
		this.org = org;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public List<Organization> getChilListOrg(){
		return childList;
	}
	
	public List<GenericValue> getEmplInOrgAtPeriod(Delegator delegator, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<GenericValue> employeeList = FastList.newInstance();
		String rootPartyId = org.getString("partyId"); 
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		}
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
														EntityJoinOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
		employeeList = delegator.findList("EmploymentAndPersonOlbius", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		for(Organization org: childList){
			employeeList.addAll(org.getEmplInOrgAtPeriod(delegator, fromDate, thruDate));
		}
		return employeeList;
	}
	
	public abstract List<GenericValue> getEmployeeInOrg(Delegator delegator, EntityCondition conds) throws GenericEntityException;
	
	public abstract List<GenericValue> getEmployeeInOrg(Delegator delegator) throws GenericEntityException;
	public abstract List<GenericValue> getChildList() throws GenericEntityException;
	public abstract List<GenericValue> getDirectEmployee(Delegator delegator) throws GenericEntityException;
	public abstract List<GenericValue> getDirectChildList(Delegator delegator);
	public abstract List<GenericValue> getAllDepartmentList(Delegator delegator) throws GenericEntityException;
	
}
