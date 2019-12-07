package com.olbius.basehr.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basehr.sqlQuery.ResultEmployeeList;


public class OrgComposite extends Organization{

	public List<GenericValue> getChildList() throws GenericEntityException {
		List<GenericValue> childAllList = FastList.newInstance();
		for(Organization org : childList){
			childAllList.add(org.getOrg());
			if(UtilValidate.isNotEmpty(org.getChildList())){
				childAllList.addAll(org.getChildList());
			}
		}
		return childAllList;
	}

	@Override
	public List<GenericValue> getEmployeeInOrg(Delegator delegator) throws GenericEntityException {
		/*List<GenericValue> employeeList = FastList.newInstance();
		String rootPartyId = org.getString("partyId"); 
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		employeeList = delegator.findList("EmploymentAndPersonOlbius", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		for(Organization org: childList){
			employeeList.addAll(org.getEmployeeInOrg(delegator));
		}*/
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
		List<GenericValue> allDept = this.getAllDepartmentList(delegator);
		List<String> allDeptId = null;
		if(UtilValidate.isNotEmpty(allDept)){
			allDeptId = EntityUtil.getFieldListFromEntityList(allDept, "partyId", true);
		}else{
			allDeptId = FastList.newInstance();
		}
		String rootPartyId = org.getString("partyId"); 
		allDeptId.add(rootPartyId);
		ModelEntity modelEtt = delegator.getModelEntity("PartyAndRelAndEmplPosTypGroup");
		List<GenericValue> employeeList = FastList.newInstance();
		ResultEmployeeList listEmplQuery = new ResultEmployeeList(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz")), 
				modelEtt, fromDate, thruDate, listAllCond, allDeptId, sortedList);
		List<Map<String, Object>> listEmplMap = listEmplQuery.getEmployeeList();
		for(Map<String, Object> tempMap: listEmplMap){
			GenericValue tempGv = delegator.makeValue("PartyAndRelAndEmplPosTypGroup");
			tempGv.putAll(tempMap);
			employeeList.add(tempGv);
		}
		return employeeList;
	}
	
	public List<GenericValue> getAllDepartmentList(Delegator delegator) throws GenericEntityException{
		List<GenericValue> allDepartmentList = FastList.newInstance();
		String rootPartyId = org.getString("partyId");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		List<GenericValue> departmentList = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("groupName"), null, false);
		allDepartmentList.addAll(departmentList);
		for(Organization tempOrg: childList){
			List<GenericValue> tempList = tempOrg.getAllDepartmentList(delegator);
			if(UtilValidate.isNotEmpty(tempList)){
				allDepartmentList.addAll(tempList);
			}
		}
		return allDepartmentList;
	}
	
	public List<GenericValue> getDirectChildList(Delegator delegator){
		List<GenericValue> adapterList=new ArrayList<GenericValue>();
		for(Organization item : childList){
			String orgTypeId = item.getOrgType();
			if(PropertiesUtil.GROUP_TYPE.equals(orgTypeId)){
				adapterList.add(item.getOrg());
			}
		}
		return adapterList;
	}
	
	public void add(Organization org){
		this.childList.add(org);
	}
	
	public void remove(Organization org){
		this.childList.remove(org);
	}


}
