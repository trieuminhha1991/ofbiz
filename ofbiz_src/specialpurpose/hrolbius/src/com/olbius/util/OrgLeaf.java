package com.olbius.util;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class OrgLeaf extends Organization{

	@Override
	public List<GenericValue> getEmployeeInOrg(Delegator delegator) throws GenericEntityException {
		/*List<GenericValue> employeeList = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		String rootPartyId = org.getString("partyId");
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		employeeList = delegator.findList("EmploymentAndPersonOlbius", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);*/
		return getEmployeeInOrg(delegator, null);
	}
	
	@Override
	public List<GenericValue> getEmployeeInOrg(Delegator delegator, EntityCondition conds) throws GenericEntityException {
		List<GenericValue> employeeList = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		String rootPartyId = org.getString("partyId");
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		if(conds != null){
			conditions.add(conds);
		}
		employeeList = delegator.findList("EmploymentAndPersonOlbius", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		return employeeList;
	}
	
	public List<GenericValue> getDirectEmployee(Delegator delegator) throws GenericEntityException{
		List<GenericValue> employeeList = FastList.newInstance();
		String rootPartyId = org.getString("partyId"); 
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		employeeList = delegator.findList("EmploymentAndPersonOlbius", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		return employeeList;
	}
	
	public List<GenericValue> getDirectChildList(Delegator delegator){
		return null;
	}
	
	@Override
	public List<GenericValue> getChildList() throws GenericEntityException {
		//Leaf haven't child
		return null;
	}

	@Override
	public List<GenericValue> getAllDepartmentList(Delegator delegator)
			throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public List<GenericValue> getEmployeeInOrg(Delegator delegator,
			int viewIndex, int viewSize) throws GenericEntityException {
		// TODO Auto-generated method stub
		return null;
	}*/
}
