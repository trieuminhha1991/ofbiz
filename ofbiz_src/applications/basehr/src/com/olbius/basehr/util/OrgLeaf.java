package com.olbius.basehr.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelEntity;
import com.olbius.basehr.sqlQuery.ResultEmployeeList;


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
		return getEmployeeInOrg(delegator, null, null, null, null);
	}
	
	@Override
	public List<GenericValue> getEmployeeInOrg(Delegator delegator, Timestamp fromDate, Timestamp thruDate, List<String> sortedList, List<Map<String, Object>> listAllCond) throws GenericEntityException {
		if(UtilValidate.isEmpty(sortedList)){
			if (sortedList == null) {
				sortedList = FastList.newInstance();
			}
			sortedList.add("firstName");
		}
		List<GenericValue> employeeList = FastList.newInstance();
		List<String> partyIdFromList = FastList.newInstance();
		String rootPartyId = org.getString("partyId");
		partyIdFromList.add(rootPartyId);
		ModelEntity modelEtt = delegator.getModelEntity("PartyAndRelAndEmplPosTypGroup");
		ResultEmployeeList listEmplQuery = new ResultEmployeeList(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), 
				modelEtt, fromDate, thruDate, listAllCond, partyIdFromList, sortedList);
		List<Map<String, Object>> listEmplMap = listEmplQuery.getEmployeeList();
		for(Map<String, Object> tempMap: listEmplMap){
			GenericValue tempGv = delegator.makeValue("PartyAndRelAndEmplPosTypGroup");
			tempGv.putAll(tempMap);
			employeeList.add(tempGv);
		}
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
