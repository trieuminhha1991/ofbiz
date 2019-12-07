package com.olbius.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.payroll.PayrollUtil;
import com.olbius.payroll.util.PayrollEntityConditionUtils;

public class PartyUtil {

	public static final String RESOURCE = "general";
	public static final String module = PartyUtil.class.getName();
	
	/**
	 * Get a organization by manger Id
	 * @param managerId
	 * @param delegator
	 * @return Id of Organization
	 * @throws GenericEntityException 
	 * @throws Exception
	 */
	public static String getOrgByManager(String managerId, Delegator delegator) throws GenericEntityException{
		//FIXME Don't have condition about status
		EntityCondition filterDateCon = EntityUtil.getFilterByDateExpr();
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdFrom", managerId, "partyRelationshipTypeId", "MANAGER");
		
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(mapCondition), filterDateCon);
		List<GenericValue> relationshipList = (List<GenericValue>)delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		if(UtilValidate.isEmpty(relationshipList)){
				return null;
		}
		GenericValue firstValue = EntityUtil.getFirst(relationshipList);
		return firstValue.getString("partyIdTo");
	}
	
	/**
	 * Get a organization by a manager
	 * @param userLogin
	 * @param delegator
	 * @return Id of Organization
	 * @throws Exception
	 */
	public static String getOrgByManager(GenericValue userLogin, Delegator delegator) throws Exception{
		//FIXME Don't have condition about status
		EntityCondition filterDateCon = EntityUtil.getFilterByDateExpr();
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdFrom", userLogin.getString("partyId"), "partyRelationshipTypeId", "MANAGER");
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, filterDateCon, EntityCondition.makeCondition(mapCondition));
		List<GenericValue> relationshipList = (List<GenericValue>)delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		if(UtilValidate.isEmpty(relationshipList)){
			return null;
		}
		GenericValue partyRelationship = EntityUtil.getFirst(relationshipList);
		return partyRelationship.getString("partyIdTo");
	}
	
	/**
	 * Get a manager by organization id
	 * @param deptId
	 * @param delegator
	 * @return Id of Manager
	 * @throws GenericEntityException 
	 * @throws Exception
	 */
	public static String getManagerbyOrg(String deptId, Delegator delegator) throws GenericEntityException {
		String rootPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		EntityCondition dateCon = EntityUtil.getFilterByDateExpr();
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdTo", deptId, "partyRelationshipTypeId", "MANAGER");
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(mapCondition), dateCon);
		List<GenericValue> relationshipList = delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		if(UtilValidate.isNotEmpty(relationshipList)){
			GenericValue firstValue = EntityUtil.getFirst(relationshipList);
			return firstValue.getString("partyIdFrom");
		}
		if(deptId.equals(rootPartyId)){
			return null;
		}else{
			//if deptId have not manager, manager of parent is manager of deptId
			GenericValue parentOrg = getParentOrgOfDepartmentCurr(delegator, deptId);
			String parentOrgId = parentOrg.getString("partyIdFrom");
			if(parentOrgId == null){
				return null;
			}
			return getManagerbyOrg(parentOrgId, delegator);
		}
	}
	
	/**
	 * Get a manager by a organization
	 * @param deptId
	 * @param delegator
	 * @return Id of Manager
	 * @throws Exception
	 */
	public static String getManagerbyOrg(GenericValue org, Delegator delegator) throws Exception{
		EntityCondition dateCon = EntityUtil.getFilterByDateExpr();
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdTo", org.getString("partyId"), "partyRelationshipTypeId", "MANAGER");
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(mapCondition), dateCon);
		List<GenericValue> relationshipList = (List<GenericValue>)delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		if(UtilValidate.isEmpty(relationshipList)){
			return null;
		}
		GenericValue firstValue = EntityUtil.getFirst(relationshipList);
		return firstValue.getString("partyIdFrom");
	}
	
	/**
	 * Get department name by id
	 * @param managerId
	 * @param delegator
	 * @return Name of department
	 * @throws Exception
	 */
	public static String getDeptNameById(String managerId, Delegator delegator) throws Exception {
		String internalOrgId = PartyUtil.getOrgByManager(managerId, delegator);
		GenericValue internalOrg = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId", internalOrgId),false);
		return internalOrg.getString("groupName");
	}
	
	/**
	 * Get Employees in Organization which is configured in general.property
	 * @param delegator
	 * @return A employee list
	 */
	public static List<GenericValue> getEmployeeInOrg(Delegator delegator) {
		//FIXME Need a specific company
		Properties generalProp = UtilProperties.getProperties(RESOURCE);
		String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
		List<GenericValue> employeeList = FastList.newInstance();
		Organization org = null;
		try {
			org = buildOrg(delegator, defaultOrganizationPartyId);
		} catch (GenericEntityException e1) {
			Debug.log("Build Organization Fail");
			return null;
		};
		try {
			employeeList = org.getEmployeeInOrg(delegator);
		} catch (GenericEntityException e) {
			Debug.log("Get Employee Fail");
			return null;
		}
		
		return employeeList;
	}
	
	/**
	 * Build a organization tree
	 * @param delegator
	 * @param partyId
	 * @return A Organization Object
	 * @throws GenericEntityException
	 */
	public static Organization buildOrg(Delegator delegator, String partyId) throws GenericEntityException{
		return buildOrg(delegator, partyId, true, true);
	}
	
	public static Organization buildOrg(Delegator delegator, String partyId, boolean buildChildren) throws GenericEntityException{
		return buildOrg(delegator, partyId, buildChildren, true);
	}
	
	/**
	 * 
	 * @param delegator
	 * @param partyId
	 * @param buildChildren: if true, build all organization is child of party
	 * @param includeEmployee: if true, add all employee to organization when build Organization
	 * @return
	 * @throws GenericEntityException
	 */
	public static Organization buildOrg(Delegator delegator, String partyId, boolean buildChildren, boolean includeEmployee) throws GenericEntityException{
		EntityCondition condition1 = EntityCondition.makeCondition("partyIdFrom", partyId);
		EntityCondition condition2 = EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP");
		EntityCondition condition4 = PayrollUtil.makeGTEcondition("thruDate", new Timestamp(new Date().getTime()));
		EntityCondition conditionList;
		if(includeEmployee){
			EntityCondition condition3 = EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT");
			EntityCondition condition23 = EntityCondition.makeCondition(EntityJoinOperator.OR, condition2, condition3);
			conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, condition1, condition23, condition4);
		}else{
			EntityCondition condition12 = EntityCondition.makeCondition(EntityJoinOperator.AND, condition1, condition2);
			conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, condition12, condition4);
		}
		
		//FIXME Fix partyIdTo is childId
		List<GenericValue> childList = delegator.findList("PartyRelationship", conditionList, UtilMisc.toSet("partyIdTo") , null, null, false);
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);		
		String orgTypeId = CommonUtil.getPartyTypeOfParty(delegator, party.getString("partyTypeId"));
		if(childList == null || childList.isEmpty()){
			//Get party for this organization
			OrgLeaf leaf = new OrgLeaf();
			//GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			leaf.setOrg(party);
			if(orgTypeId != null){
				leaf.setOrgType(orgTypeId);
			}
			return leaf;
		}else {
			OrgComposite composite = new OrgComposite();
			composite.setOrg(party);
			if(orgTypeId != null){
				composite.setOrgType(orgTypeId);
			}
			for(GenericValue child: childList){
				if(buildChildren){
					//test
					/*boolean checkLoopOrg = checkLoopOrg(delegator, partyId, child.getString("partyIdTo"));
					if(checkLoopOrg){
						return null;
					}*/
					//end test
					Organization childOrg = buildOrg(delegator, child.getString("partyIdTo"), buildChildren, includeEmployee);
					composite.add(childOrg);
				}else{
					OrgComposite childComposite = new OrgComposite();
					GenericValue childParty = delegator.findOne("Party", UtilMisc.toMap("partyId", child.getString("partyIdTo")), false);
					String tempOrgTypeId = CommonUtil.getPartyTypeOfParty(delegator, childParty.getString("partyTypeId"));
					childComposite.setOrg(childParty);
					if(tempOrgTypeId != null){
						childComposite.setOrgType(tempOrgTypeId);
					}
					composite.add(childComposite);
				}
			}
			return composite;
		}
	}
	
	/*private static boolean checkLoopOrg(Delegator delegator, String partyId, String partyIdChild) throws GenericEntityException {
		String rootPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		if(partyId == null){
			return true;
		}
		if(partyId.equals(rootPartyId)){
			return false;
		}
		GenericValue parentParty = getParentOrgOfDepartmentCurr(delegator, partyId);
		if(parentParty == null){
			System.out.println("parent of partyId null: " + partyId);
			//return true;
		}else{
			String partyIdParent = parentParty.getString("partyIdFrom");
			if(partyIdParent != null && partyIdParent.equals(partyIdChild)){
				System.out.println("partyId: " + partyId + " -- partyIdParent and Child: " + partyIdChild);
				return true;
			}else{
				return checkLoopOrg(delegator, partyIdParent, partyIdChild);
			}
		}
		return false;
	}*/

	/**
	 * Get CEO 
	 * @param delegator
	 * @return Id of chief of executive(CEO)
	 * @throws Exception
	 */
	public static String getCEO(Delegator delegator) {
		List<String> parties = SecurityUtil.getPartiesByRoles("PHOTONGGIAMDOC", delegator);
		if(UtilValidate.isEmpty(parties)) {
			return null;
		}else {
			return parties.get(0);
		}
	}
	
	public static List<String> getListMgrOfOrgByRoleType(Delegator delegator, String roleTypeId) throws GenericEntityException{
		List<String> parties = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
		List<String> mgrList = FastList.newInstance();
		for(String orgId: parties){
			String mgrId = getManagerbyOrg(orgId, delegator);
			if(mgrId != null){
				mgrList.add(mgrId);
			}
		}
		return mgrList;
	}
	
	public static boolean isCEO(Delegator delegator, GenericValue userLogin) throws Exception{
		String ceoId = getCEO(delegator);
		return userLogin.getString("partyId").equals(ceoId) ? true : false;
	}
	
	public static boolean isEmployee(Delegator delegator, GenericValue userLogin) throws Exception{
		return SecurityUtil.hasRole("EMPLOYEE", userLogin.getString("partyId"), delegator);
	}
	
	public static boolean isHeadOfDept(Delegator delegator, GenericValue userLogin) throws Exception{
		return SecurityUtil.hasRole("TRUONGBOPHAN", userLogin.getString("partyId"), delegator);
	}
	/**
	 * Get Department of employee
	 * @param delegator
	 * @param employeeId
	 * @return GenericValue: employee
	 * @throws GenericEntityException
	 */
	public static GenericValue getDepartmentOfEmployee(Delegator delegator, String employeeId) throws GenericEntityException{
		return getDepartmentOfEmployee(delegator, employeeId, UtilDateTime.nowTimestamp());
	}
	
	public static GenericValue getDepartmentOfEmployee(Delegator delegator, String employeeId, Timestamp moment) throws GenericEntityException{
		//find department of employee
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "EMPLOYMENT"));
		conditions.add(EntityUtil.getFilterByDateExpr(moment));
		List<GenericValue> departmentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isEmpty(departmentList)){
			return null;
		}
		return EntityUtil.getFirst(departmentList);
	}
	
	public static List<String> getDepartmentOfEmployee(Delegator delegator, String employeeId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"));
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeeId));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "EMPLOYMENT"));
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN, thruDate));
		}
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityJoinOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityJoinOperator.GREATER_THAN, fromDate)));
		List<GenericValue> departmentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false);
		List<String> departmentIdList = EntityUtil.getFieldListFromEntityList(departmentList, "partyIdFrom", true);
		return departmentIdList;
	}
	
	public static List<GenericValue> getOrgOfEmplInPeriod(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<GenericValue> retList = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		}
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
		retList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromDate"), null, false);
		return retList;
	}
	
	//TODO need add condition about dateTime
	public static GenericValue getParentOrgOfDepartmentCurr(Delegator delegator, String departmentId) throws GenericEntityException{
		//find department of employee
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, departmentId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> departmentList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isEmpty(departmentList)){
			return null;
		}
					
		return EntityUtil.getFirst(departmentList);
	}
	
	public static String getManagerOfEmpl(Delegator delegator, String employeeId) throws GenericEntityException {
		GenericValue dept = getDepartmentOfEmployee(delegator, employeeId);
		if(dept != null){
			String rootPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
			String mgrId = getManagerbyOrg(dept.getString("partyIdFrom"), delegator);
			if(!employeeId.equals(mgrId) || dept.equals(rootPartyId)){
				return mgrId;
			}else{
				GenericValue parentDept = getParentOrgOfDepartmentCurr(delegator, dept.getString("partyIdFrom"));
				if(parentDept != null){
					String parentDeptId = parentDept.getString("partyIdFrom");
					return getManagerbyOrg(parentDeptId, delegator);	
				}
			}
		}
		return null;
	}
	
	public static List<GenericValue> getCurrPositionTypeOfEmpl(Delegator delegator, String employeeId) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment 
		conditions.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employeeId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> positionList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		return positionList;
	}
	
	public static List<GenericValue> getCurrPositionTypeOfEmpl(Delegator delegator) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> positionList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		return positionList;
	}
	
	public static Map<String,String> getListEmplPositionInOrgOfManager(Delegator delegator,String managerId) throws Exception{
		List<GenericValue> listEmpl  = getListEmployeeOfManager(delegator,managerId);
		Map<String,String> listPosOfEmpl = FastMap.newInstance();
		for(GenericValue empl : listEmpl){
			if(!empl.getString("partyId").isEmpty()){
				List<GenericValue> listEmplFullFillment = getCurrPositionTypeOfEmpl(delegator,empl.getString("partyId"));
				for(GenericValue emplPosType : listEmplFullFillment){
					String emplPosTypeId = emplPosType.getString("emplPositionId");
					GenericValue tempPos = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPosTypeId), false);
					GenericValue tmpPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", tempPos.getString("emplPositionTypeId")), false);
					if(!listPosOfEmpl.containsKey(tmpPosType.getString("emplPositionTypeId"))){
						listPosOfEmpl.put(tmpPosType.getString("emplPositionTypeId"), tmpPosType.getString("description"));
					}else continue;
				}
			}
		}
		return listPosOfEmpl;
	}
	
	public static String getCurrPosTypeOfEmplOverview(Delegator delegator, String employeeId) throws GenericEntityException{
		List<GenericValue> emplPosType = getCurrPositionTypeOfEmpl(delegator, employeeId);
		List<String> emplPos = FastList.newInstance();
		for(GenericValue tempPos: emplPosType){
			GenericValue emplType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", tempPos.getString("emplPositionTypeId")), false);
			emplPos.add(emplType.getString("description"));
		}
		return StringUtils.join(emplPos, ", ");
	}
	
	public static List<GenericValue> getPositionTypeOfEmplAtTime(Delegator delegator, String employeeId, Timestamp moment) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr(moment));
		conditions.add(EntityCondition.makeCondition("employeePartyId", employeeId));
		List<GenericValue> emplPosFul = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions), null, null, null, false);
		return emplPosFul;
	}
	
	public static List<GenericValue> getPositionTypeOfEmplInPeriod(Delegator delegator, String employeeId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
		conditions.add(EntityCondition.makeCondition("employeePartyId", employeeId));
		List<GenericValue> emplPosFul = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-fromDate"), null, false);
		return emplPosFul;
	}
	
	public static List<GenericValue> getPositionOfEmplInDate(Delegator delegator, String employeeId, java.sql.Date date) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		Timestamp timestamp = new Timestamp(date.getTime());
		Timestamp fromDate = UtilDateTime.getDayStart(timestamp);
		Timestamp thruDate = UtilDateTime.getDayEnd(timestamp);
		conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
													EntityOperator.OR,
													EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)));
		conditions.add(EntityCondition.makeCondition("employeePartyId", employeeId));
		List<GenericValue> emplPosFul = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(conditions), null, null, null, false);
		return emplPosFul;
	}
	
	
	
	public static List<GenericValue> getListEmployeeOfManager(Delegator delegator, String managerId) throws Exception{
		List<GenericValue> employeeList = FastList.newInstance();
		Organization org = null;
		String orgId = getOrgByManager(managerId, delegator);
		
		try {
			org = buildOrg(delegator, orgId);
		} catch (GenericEntityException e1) {
			Debug.log("Build Organization Fail");
		}
		try {
			employeeList = org.getEmployeeInOrg(delegator);
		} catch (GenericEntityException e) {
			Debug.log("Get Employee Fail");
		}
		
		return employeeList;
	}
	
	//get list employee that managerId manage directly, not include managerId
	public static List<GenericValue> getListEmplDirectMgr(Delegator delegator, String managerId) throws GenericEntityException{
		return getListEmplDirectMgr(delegator, managerId, false);
	}
	//get list employee that manager manage directly
	public static List<GenericValue> getListEmplDirectMgr(Delegator delegator, String managerId, boolean includeMgr) throws GenericEntityException{
		List<GenericValue> retList = FastList.newInstance();
		String orgId = PartyUtil.getOrgByManager(managerId, delegator);
		Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, false);
		retList = buildOrg.getDirectEmployee(delegator);
		if(!includeMgr){
			retList = EntityUtil.filterByCondition(retList, EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, managerId));
		}
		return retList;
	}
	
	public static boolean isRetiredAge(Delegator delegator, String employeeId) throws GenericEntityException{
		GenericValue employee = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);		
		java.sql.Date birthDate = employee.getDate("birthDate");
		if(UtilValidate.isEmpty(birthDate)){
			return false;
		}
		Calendar dob = Calendar.getInstance();
		dob.setTime(birthDate);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
			  age--;  
		} else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
		    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
		  age--;  
		}
		if(PropertiesUtil.retiredAge > age){
			return false;
		}
		return true;
	}
	
	public static List<String> getSalessupDeptListByMgr(Delegator delegator, String managerId){
		List<String> retList = FastList.newInstance();
		try {
			String orgPartyId = PartyUtil.getOrgByManager(managerId, delegator);
			Organization organization = PartyUtil.buildOrg(delegator, orgPartyId);
			List<GenericValue> orgList = FastList.newInstance(); 
			orgList = organization.getChildList();
			orgList.add(organization.getOrg());
			for(GenericValue org: orgList){
				List<GenericValue> orgRolesGv = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", org.getString("partyId")), null, false);
				List<String> roles = EntityUtil.getFieldListFromEntityList(orgRolesGv, "roleTypeId", true);
				if(roles.contains("DELYS_SALESSUP_GT")){
					retList.add(org.getString("partyId"));
				}
			}
		} catch (Exception e){
			Debug.log(e.getStackTrace().toString(), module);
		}		
		return retList;
	}
	
	public static boolean isAdmin(String partyId, Delegator delegator){
		String adminId = getHrmAdmin(delegator);
		
		return partyId.equals(adminId) ? true : false;
	}
	
	public static boolean isAdmin(Delegator delegator, GenericValue userLogin){
		String adminId = getHrmAdmin(delegator);
		
		return userLogin.getString("partyId").equals(adminId) ? true : false;
	}
	
	public static String getHrmAdmin(Delegator delegator){
		List<String> listAdmin = SecurityUtil.getPartiesByRoles("HRMADMIN", delegator);
		if(!UtilValidate.isEmpty(listAdmin)) {
			return listAdmin.get(0);
		}
		return "";
	}		
	
	public static String getOrgNextLevelOfEmpl(Delegator delegator, String partyId) throws Exception{
		GenericValue dept = getDepartmentOfEmployee(delegator, partyId);
		GenericValue parentDept = getParentOrgOfDepartmentCurr(delegator, dept.getString("partyIdFrom"));
		return parentDept.getString("partyIdFrom");
	}
	
	public static List<String> getPartyByRootAndRole(Delegator delegator, String partyId, String role) throws GenericEntityException{
		Organization org = null;
		List<String> childListbyRole = FastList.newInstance();
		org = buildOrg(delegator, partyId);
		List<GenericValue> childList = org.getChildList();
		for(GenericValue item: childList){
			EntityCondition partyCon = EntityCondition.makeCondition("partyId", item.getString("partyId"));
			EntityCondition RoleCon = EntityCondition.makeCondition("roleTypeId", role);
			List<GenericValue> partyRoleList = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityJoinOperator.AND, partyCon, RoleCon), UtilMisc.toSet("partyId"), null, null, false);
			if(UtilValidate.isEmpty(partyRoleList)){
				continue;
			}else{
				childListbyRole.add(item.getString("partyId"));
				}
			}
		return childListbyRole;
	}
	
	public static List<String> getPeopleByRootAndRole(Delegator delegator, String partyId, String role) throws GenericEntityException{
		Organization org = null;
		List<String> childListbyRole = FastList.newInstance();
		org = buildOrg(delegator, partyId);
		List<GenericValue> childList = org.getChildList();
		for(GenericValue item: childList){
			EntityCondition partyCon = EntityCondition.makeCondition("partyId", item.getString("partyId"));
			EntityCondition RoleCon = EntityCondition.makeCondition("roleTypeId", role);
			List<GenericValue> partyRoleList = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityJoinOperator.AND, partyCon, RoleCon), UtilMisc.toSet("partyId"), null, null, false);
			if(UtilValidate.isEmpty(partyRoleList)){
				continue;
			}else{
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", item.getString("partyId")), false);
				if(UtilValidate.isEmpty(person)){
					continue;
				}
				childListbyRole.add(item.getString("partyId"));
				}
			}
		return childListbyRole;
	}
	
	public static List<String> getPartyByRootAndRoles(Delegator delegator, String partyId, List<String> roles) throws GenericEntityException{
		Organization org = null;
		List<String> childListbyRole = FastList.newInstance();
		org = buildOrg(delegator, partyId);
		List<GenericValue> childList = org.getChildList();
		for(GenericValue item: childList){
			EntityCondition partyCon = EntityCondition.makeCondition("partyId", item.getString("partyId"));
			//Map<String, Object> roleConMap = FastMap.newInstance();
			List<EntityCondition> roleConds = new ArrayList<EntityCondition>();
			for(String role : roles){
				//roleConMap.put("roleTypeId", role);
				roleConds.add(EntityCondition.makeCondition("roleTypeId", role));
			}
			//EntityCondition RoleCon = EntityCondition.makeCondition(roleConMap, EntityJoinOperator.OR);
			EntityCondition RoleCon = EntityCondition.makeCondition(roleConds, EntityJoinOperator.OR);
			List<GenericValue> partyRoleList = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityJoinOperator.AND, partyCon, RoleCon), UtilMisc.toSet("partyId"), null, null, false);
			if(UtilValidate.isEmpty(partyRoleList)){
				continue;
			}else{
				childListbyRole.add(item.getString("partyId"));
				}
			}
		return childListbyRole;
	}
	
	public static List<GenericValue> getAllManagerInOrg(Delegator delegator) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
		EntityFindOptions findOpts = new EntityFindOptions();
		findOpts.setDistinct(true);
		List<GenericValue> managerList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), UtilMisc.toList("partyIdFrom"), findOpts, false);
		List<GenericValue> retList = FastList.newInstance();
		for(GenericValue manager: managerList){
			String partyId = manager.getString("partyIdFrom");
			GenericValue party = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
			if(party != null){
				retList.add(party);
			}else{
				System.err.println("cannot find partyId: " + partyId);
			}
		}
		return retList;
	}
	
	public static String getPersonName(Delegator delegator, String partyId) throws GenericEntityException{
		
		GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
		if(person == null){
			return "";
		}
		StringBuffer partyName = new StringBuffer();
		if(person.getString("lastName") != null){
			partyName.append(person.getString("lastName"));
		}
		if(person.getString("middleName") != null){
			partyName.append(" ");
			partyName.append(person.getString("middleName"));
		}
		if(person.getString("firstName") != null){
			partyName.append(" ");
			partyName.append(person.getString("firstName"));
		}
		return partyName.toString();
	}

	public static List<GenericValue> getPostalAddressByPurpose(Delegator delegator, String purpose){
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment
		conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", purpose));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> postalAddressList = null;
		try {
			postalAddressList = delegator.findList("PartyPostalAddressPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return postalAddressList;
	}
	public static List<GenericValue> getTelecomNumberByPurpose(Delegator delegator, String purpose){
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment
		conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", purpose));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> postalAddressList = null;
		try {
			postalAddressList = delegator.findList("PartyTelecomNumberPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return postalAddressList;
	}
	public static List<GenericValue> getFaxNumberByPurpose(Delegator delegator, String purpose){
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment
		conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", purpose));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> postalAddressList = null;
		try {
			postalAddressList = delegator.findList("PartyFaxNumberPurpose", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return postalAddressList;
	}
	public static List<GenericValue> getPartyTax(Delegator delegator){
		List<EntityCondition> conditions = FastList.newInstance();
		//FIXME need change entity EmplPositionAndPositionType to EmplPositionAndFulfillment
		conditions.add(EntityCondition.makeCondition("taxAuthGeoId", "VNM"));
		conditions.add(EntityCondition.makeCondition("taxAuthPartyId", "VNM_TAX"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> partyTaxList = null;
		try {
			partyTaxList = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return partyTaxList;
	}
	public static List<GenericValue> getFinAccount(Delegator delegator){
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> finAccountList = null;
		try {
			finAccountList = delegator.findList("FinAccount", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return finAccountList;
	}

	public static String getRoleTypeGroupInPeriod(Delegator delegator, String orgId,
			Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		String roleTypeGroupId = "_NA_";
		List<EntityCondition> conditions = FastList.newInstance();
		List<EntityCondition> conditionDate = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", orgId));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		
		conditionDate.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
		if(thruDate != null){
			conditionDate.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null),
														EntityOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate)));
		}
		List<GenericValue> listPartyRelRole = delegator.findList("PartyRelationship", EntityCondition.makeCondition(EntityCondition.makeCondition(conditions),
																													EntityOperator.AND,
																													EntityCondition.makeCondition(conditionDate)), null, null, null, false);
		List<String> listRole = EntityUtil.getFieldListFromEntityList(listPartyRelRole, "roleTypeIdTo", true);
		if(UtilValidate.isNotEmpty(listRole)){
			List<GenericValue> roleTypeGroupMember = getRoleTypeGroupMemberOfListRole(delegator, listRole, EntityCondition.makeCondition(conditionDate));
			if(UtilValidate.isNotEmpty(roleTypeGroupMember)){
				return roleTypeGroupMember.get(0).getString("roleTypeGroupId");	
			}
		}
		return roleTypeGroupId;
	}
	
	public static List<GenericValue> getRoleTypeGroupMemberOfListRole(Delegator delegator, List<String> listRole, EntityCondition conditionDate) 
			throws GenericEntityException{
		EntityCondition roleTypeGroupMemberCond = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRole);
		EntityCondition roleTypeGroupConds = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeGroupTypeId", "SALES_ROLE"),
																			EntityJoinOperator.OR,
																			EntityCondition.makeCondition("roleTypeGroupId", "OFFICE_ROLE"));
		List<GenericValue> listRoleTypeGroup = delegator.findList("RoleTypeGroup", roleTypeGroupConds, null, null, null, false);
		if(UtilValidate.isNotEmpty(listRoleTypeGroup)){
			List<String> roleTypeGroupList = EntityUtil.getFieldListFromEntityList(listRoleTypeGroup, "roleTypeGroupId", true);
			roleTypeGroupMemberCond = EntityCondition.makeCondition(roleTypeGroupMemberCond, EntityJoinOperator.AND, 
																	EntityCondition.makeCondition("roleTypeGroupId", EntityJoinOperator.IN, roleTypeGroupList));
			List<GenericValue> roleTypeGroupMember = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(roleTypeGroupMemberCond, EntityOperator.AND, conditionDate), null, 
					UtilMisc.toList("-fromDate"), null, false);
			return roleTypeGroupMember;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListRoleTypeGroupInPeriod(Delegator delegator, String orgId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", orgId));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		conditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
		Map<String, Object> mapReturn = FastMap.newInstance();
		List<GenericValue> listPartyRelRole = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromDate"), null, false);
		for(GenericValue tempGv: listPartyRelRole){
			String roleTypeId = tempGv.getString("roleTypeIdTo");
			Timestamp fromDateGv = tempGv.getTimestamp("fromDate");
			Timestamp thruDateGv = tempGv.getTimestamp("thruDate");
			if(fromDate.after(fromDateGv)){
				fromDateGv = fromDate;
			}
			if(thruDateGv == null || (thruDate != null && thruDate.before(thruDateGv))){
				thruDateGv = thruDate;
			}
			List<Map<String, Object>> listRoleTypeGroup = getRoleTypeGroupOfRoleType(delegator, roleTypeId, fromDateGv, thruDateGv); 
			for(Map<String, Object> entry: listRoleTypeGroup){
				String roleTypeGroupId = (String)entry.get("roleTypeGroupId");
				List<Map<String, Timestamp>> listTimestamp = (List<Map<String, Timestamp>>)mapReturn.get(roleTypeGroupId);
				if(listTimestamp == null){
					listTimestamp = FastList.newInstance();
					mapReturn.put(roleTypeGroupId, listTimestamp);
				}
				Map<String, Timestamp> tempMap = FastMap.newInstance();
				tempMap.put("fromDate", (Timestamp)entry.get("fromDate"));
				tempMap.put("thruDate", (Timestamp)entry.get("thruDate"));
				listTimestamp.add(tempMap);
			}
		}
		for(Map.Entry<String, Object> entry: mapReturn.entrySet()){
			List<Map<String, Timestamp>> tempList = (List<Map<String, Timestamp>>)entry.getValue();
			tempList = CommonUtil.combineListContinuousTime(tempList, 3600*1000);
			entry.setValue(tempList);
		}
		return mapReturn;
	}
	
	public static List<Map<String, Object>> getRoleTypeGroupOfRoleType(Delegator delegator, String roleTypeId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		conditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
		List<GenericValue> roleTypeGroupMember = delegator.findList("RoleTypeGroupMember", EntityCondition.makeCondition(conditions), 
				null, UtilMisc.toList("fromDate"), null, false);
		for(GenericValue tempGv: roleTypeGroupMember){
			Map<String, Object> tempMap = FastMap.newInstance();
			listReturn.add(tempMap);
			Timestamp fromDateGv = tempGv.getTimestamp("fromDate");
			Timestamp thruDateGv = tempGv.getTimestamp("thruDate");
			if(fromDateGv.before(fromDate)){
				fromDateGv = fromDate;
			}
			if(thruDateGv == null || (thruDate != null && thruDate.before(thruDateGv))){
				thruDateGv = thruDate;
			}
			tempMap.put("roleTypeGroupId", tempGv.getString("roleTypeGroupId"));
			tempMap.put("fromDate", fromDateGv);
			tempMap.put("thruDate", thruDateGv);
		}
		return listReturn;
	}
	
	public static String getWorkEffortAttr(Delegator delegator, String partyId, String attrName) {
		List<GenericValue> ListWorkEffortAttr = null;
		try {
			ListWorkEffortAttr = delegator.findByAnd("EmplAppWorkEffortAttributeView", UtilMisc.toMap("applyingPartyId", partyId, "attrName", attrName), null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		GenericValue workEffortAttr = EntityUtil.getFirst(ListWorkEffortAttr);
		if(workEffortAttr == null) {
			return null;
		}else {
			return workEffortAttr.getString("attrValue");
		}
	}
	
	public static String getWorkEffortAttr(Delegator delegator, String workEffortId, String attrName, Boolean isByWorkEffortId) {
		List<GenericValue> ListWorkEffortAttr = null;
		try {
			if(isByWorkEffortId) {
				ListWorkEffortAttr = delegator.findByAnd("EmplAppWorkEffortAttributeView", UtilMisc.toMap("workEffortId", workEffortId, "attrName", attrName), null, false);
			}	
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		GenericValue workEffortAttr = EntityUtil.getFirst(ListWorkEffortAttr);
		if(workEffortAttr == null) {
			return null;
		}else {
			return workEffortAttr.getString("attrValue");
		}
	}
	/**
	 * Get Party Name
	 * @param delegator
	 * @param partyId
	 * @return GroupName with party group and full name with person
	 * @throws GenericEntityException 
	 */
	public static String getPartyName(Delegator delegator, String partyId) throws GenericEntityException {
		return PartyHelper.getPartyName(delegator, partyId, true, true);
	}
	
	public static Map<String, String> getHighestPartyEdu(Delegator delegator, String partyId) throws GenericEntityException{
		Map<String, String> retMap = FastMap.newInstance();
		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", partyId),
																	EntityJoinOperator.AND,
																  EntityCondition.makeCondition("thruDate", EntityJoinOperator.NOT_EQUAL, null));
		List<GenericValue> personEdu = delegator.findList("PersonEducation", condition, null, UtilMisc.toList("-thruDate"), null, false);
		if(UtilValidate.isNotEmpty(personEdu)){
			GenericValue emplHighestEdu = personEdu.get(0);
			String schoolId = emplHighestEdu.getString("schoolId");
			GenericValue schoolGv = delegator.findOne("EducationSchool", UtilMisc.toMap("schoolId", schoolId), false);
			retMap.put("schoolId", schoolGv.getString("schoolName"));
			String majorId = emplHighestEdu.getString("majorId");
			GenericValue majorGv = delegator.findOne("Major", UtilMisc.toMap("majorId", majorId), false);
			retMap.put("majorId", majorGv.getString("description"));
			String educationSystemTypeId = emplHighestEdu.getString("educationSystemTypeId");
			GenericValue educationSystemType = delegator.findOne("EducationSystemType", UtilMisc.toMap("educationSystemTypeId", educationSystemTypeId), false);
			retMap.put("educationSystemTypeId", educationSystemType.getString("description"));
		}
		return retMap;
	}

	public static String getPartyGroupRoleTypeDept(Delegator delegator,
			String partyId) throws GenericEntityException {
		// TODO Auto-generated method stub
		List<String> listRoles = SecurityUtil.getCurrentRoles(partyId, delegator);
		if(UtilValidate.isEmpty(listRoles)){
			return null;
		}
		List<GenericValue> listRoleDept = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", "DEPARTMENT"), null, false);
		if(listRoleDept != null){
			List<String> listRoleDeptList = EntityUtil.getFieldListFromEntityList(listRoleDept, "roleTypeId", true);
			for(String roleTypeId: listRoles){
				if(listRoleDeptList.contains(roleTypeId)){
					return roleTypeId;
				}
			}
		}
		return listRoles.get(0);
	}

	
	public static List<GenericValue> getPostalAddressOfOrg(Delegator delegator,
			String orgId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		String rootId = MultiOrganizationUtil.getCurrentOrganization(delegator); 
		conditions.add(EntityCondition.makeCondition("partyId", orgId));
		conditions.add(PayrollEntityConditionUtils.makeDateConds(fromDate, thruDate));
		
		EntityCondition commonConds = EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS");
		commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND,  EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
		
		List<GenericValue> orgAddrList = delegator.findList("PartyContactDetailByPurpose", 
				EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), EntityOperator.AND, commonConds), 
				null, UtilMisc.toList("fromDate"), null, false);
		if(UtilValidate.isNotEmpty(orgAddrList) || orgId.equals(rootId)){
			return orgAddrList;
		}
		GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, orgId);
		if(parentOrg != null){
			return getPostalAddressOfOrg(delegator, parentOrg.getString("partyIdFrom"), fromDate, thruDate);
		}else{
			System.out.println("orgId: " + orgId);
			return FastList.newInstance();
		}
	}
	/**
	 * Party, is belong to root ?
	 * @param partyId
	 * @param delegator
	 * @return
	 */
	public static boolean checkInOrg(Organization orgTree, String partyId, Delegator delegator) {
		List<GenericValue> childList = null;
		try {
			childList = orgTree.getChildList();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		for (GenericValue item : childList) {
			if(item.getString("partyId").equals(partyId)) {
				return true;
			}
		}
		return false;
	}
}
