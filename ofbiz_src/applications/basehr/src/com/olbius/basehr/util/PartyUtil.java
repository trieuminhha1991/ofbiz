package com.olbius.basehr.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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

import com.olbius.basehr.payroll.util.PayrollUtil;
import com.olbius.basehr.util.EntityConditionUtils;

public class PartyUtil {

	public static final String RESOURCE = "general";
	public static final String module = PartyUtil.class.getName();
	
	public static List<String> getListOrgManagedByParty(Delegator delegator, String userLoginId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		if(PartyUtil.isFullPermissionView(delegator, userLoginId)){
			List<String> retList = FastList.newInstance();
			retList.add(PartyUtil.getCurrentOrganization(delegator, userLoginId));
			return retList;
		}
		return getListOrgManagedByParty(delegator, userLoginId, fromDate, thruDate, true);
	}
	
	public static List<String> getListOrgManagedByParty(Delegator delegator, String userLoginId) throws GenericEntityException{
		return getListOrgManagedByParty(delegator, userLoginId, UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
	}
	
	/**
	 * 
	 * @param delegator
	 * @param userLoginId
	 * @param fromDate
	 * @param thruDate
	 * @param excludeChild remove element in returned list if this element is child of others in list
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<String> getListOrgManagedByParty(Delegator delegator, String userLoginId, Timestamp fromDate, Timestamp thruDate, boolean excludeChild) throws GenericEntityException{
		EntityCondition filterDateCon = EntityConditionUtils.makeDateConds(fromDate, thruDate);
		String rootOrgId = getCurrentOrganization(delegator, userLoginId);
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		String partyId = userLogin.getString("partyId");
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdFrom", partyId, "partyRelationshipTypeId", "MANAGER");
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, filterDateCon, EntityCondition.makeCondition(mapCondition));
		List<GenericValue> relationshipList = delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		if(UtilValidate.isEmpty(relationshipList)){
			return null;
		}
		List<String> tempList = EntityUtil.getFieldListFromEntityList(relationshipList, "partyIdTo", true);
		List<String> retList = tempList;
		for(String tempChild: tempList){
			if(excludeChild){
				for(String tempChild2: tempList){
					if(checkAncestorOfParty(delegator, tempChild, tempChild2, userLogin)){
						retList.remove(tempChild2);	
					}
				}
			}
			if(!checkAncestorOfParty(delegator, rootOrgId, tempChild, userLogin)){
				retList.remove(tempChild);	
			}
		}
		return retList;
	}
	
	/**
	 * 
	 * @param deptId
	 * @param delegator
	 * @param roleTypeId roleType of manager of deptId
	 * @param fromDate
	 * @param thruDate
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<String> getManagerbyOrg(String deptId, Delegator delegator, String roleTypeId, Timestamp fromDate, Timestamp thruDate, String userLoginId) throws GenericEntityException {
		String rootPartyId = getRootOrganization(delegator, userLoginId);
		EntityCondition dateCon = EntityConditionUtils.makeDateConds(fromDate, thruDate);
		Map<String, String> mapCondition = UtilMisc.toMap("partyIdTo", deptId, "partyRelationshipTypeId", "MANAGER");
		EntityCondition conditionList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(mapCondition), dateCon);
		if(roleTypeId != null){
			conditionList = EntityCondition.makeCondition(conditionList, EntityJoinOperator.AND, EntityCondition.makeCondition("roleTypeIdFrom", roleTypeId));
		}
		List<GenericValue> relationshipList = delegator.findList("PartyRelationship", conditionList, null, null, null, false);
		List<String> retList = EntityUtil.getFieldListFromEntityList(relationshipList, "partyIdFrom", true);
		if(UtilValidate.isNotEmpty(retList)){
			return retList;
		}
		if(deptId.equals(rootPartyId)){
			return null;
		}else{
			//if deptId have not manager, manager of parent's deptId is manager of deptId
			GenericValue parentOrg = getParentOrgOfDepartmentCurr(delegator, deptId);
			if(parentOrg == null){
				return null;
			}
			String parentOrgId = parentOrg.getString("partyIdFrom");
			if(parentOrgId == null){
				return null;
			}
			return getManagerbyOrg(parentOrgId, delegator, fromDate, thruDate, userLoginId);
		}	
	}
	
	public static List<String> getManagerbyOrg(String deptId, Delegator delegator, Timestamp fromDate, Timestamp thruDate, String userLoginId) throws GenericEntityException {
		return getManagerbyOrg(deptId, delegator, null, fromDate, thruDate, userLoginId);
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
	public static String getDeptNameById(String managerUserLoginId, Delegator delegator) throws Exception {
		List<String> internalOrgId = PartyUtil.getListOrgManagedByParty(delegator, managerUserLoginId);
		List<String> internalOrgName = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", internalOrgId, "partyId", "groupName");
		String retString = StringUtils.join(internalOrgName, ", ");
		return retString;
	}
	
	/**
	 * Get Employees in Organization which is configured in general.property
	 * @param delegator
	 * @return A employee list
	 */
	@Deprecated
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
	@Deprecated
	public static Organization buildOrg(Delegator delegator, String partyId) throws GenericEntityException{
		return buildOrg(delegator, partyId, true, true);
	}
	
	public static Organization buildOrg(Delegator delegator, String partyId, boolean buildChildren) throws GenericEntityException{
		return buildOrg(delegator, partyId, buildChildren, false);
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
	
	public static boolean isFullPermissionView(Delegator delegator, String userLoginId) throws GenericEntityException{
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		if(userLogin == null){
			return false;
		}
		String partyId = userLogin.getString("partyId");		
		for(String roleTypeId: PropertiesUtil.ROLE_TYPE_FULL_PERMS){
			if(SecurityUtil.hasRole(roleTypeId, partyId, delegator)){
				return true;
			}
		}
		return false;
	}
	public static boolean isFullPermissionAction(Delegator delegator, String userLoginId) throws GenericEntityException{
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		if(userLogin == null){
			return false;
		}
		String partyId = userLogin.getString("partyId");		
		for(String roleTypeId: PropertiesUtil.ROLE_TYPE_FULL_PERMS){
			if(SecurityUtil.hasRole(roleTypeId, partyId, delegator)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * get list manager of organization have role is roleTypeId
	 * @param delegator
	 * @param roleTypeId
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<String> getListMgrOfOrgByRoleType(Delegator delegator, String roleTypeId, String userLoginId) throws GenericEntityException{
		List<String> parties = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
		List<String> mgrList = FastList.newInstance();
		for(String orgId: parties){
			List<String> tempMgrId = getManagerbyOrg(orgId, delegator, UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp(), userLoginId);
			if(tempMgrId != null){
				mgrList.addAll(tempMgrId);
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
	
	/*public static GenericValue getDepartmentOfEmployee(Delegator delegator, String employeeId) throws GenericEntityException{
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
	}*/
	
	
	public static List<String> getOrgOfEmployee(Delegator delegator, String employeeId, Timestamp moment) throws GenericEntityException{
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
		List<String> retList = EntityUtil.getFieldListFromEntityList(departmentList, "partyIdFrom", true); 
		return retList;
	}
	
	public static List<String> getDepartmentOfEmployee(Delegator delegator, String employeeId, Timestamp moment) throws GenericEntityException{
		return getDepartmentOfEmployee(delegator, employeeId, moment, moment);
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
	
	public static List<String> getManagerOfEmpl(Delegator delegator, String employeeId, Timestamp fromDate, Timestamp thruDate, String userLoginId) throws GenericEntityException {
		List<String> listDept = getDepartmentOfEmployee(delegator, employeeId, fromDate, thruDate);
		String rootPartyId = getRootOrganization(delegator, userLoginId);
		Set<String> mgrSet = FastSet.newInstance();
		for(String deptId: listDept){
			if(checkAncestorOfParty(delegator, rootPartyId, deptId)){
				List<String> listMgrId = getManagerbyOrg(deptId, delegator, fromDate, thruDate, userLoginId);
				if((listMgrId != null && !listMgrId.contains(employeeId)) || deptId.equals(rootPartyId)){
					mgrSet.addAll(listMgrId);
				}else{
					GenericValue parentDept = getParentOrgOfDepartmentCurr(delegator, deptId);
					if(parentDept != null){
						String parentDeptId = parentDept.getString("partyIdFrom");
						List<String> tempList = getManagerbyOrg(parentDeptId, delegator, fromDate, thruDate, userLoginId);
						if(tempList != null){
							mgrSet.addAll(tempList);
						}
					}
				}
			}
		}
		List<String> retList = null;
		if(UtilValidate.isEmpty(mgrSet)){
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			for(String managerSeniorRole: PropertiesUtil.MANAGER_SENIOR_ROLE){
				List<String> tempList = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, managerSeniorRole, delegator);
				mgrSet.addAll(tempList);
			}
		}
		retList = UtilMisc.toList(mgrSet);
		return retList;
	}
	
	public static List<String> getManagerOfEmpl(Delegator delegator, String employeeId, Timestamp moment, String userLoginId) throws GenericEntityException {
		return getManagerOfEmpl(delegator, employeeId, moment, moment, userLoginId);
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
	
	public static Timestamp getThruDateEmplPosition(Delegator delegator, String emplPositionTypeId, String partyId, Timestamp fromDate) throws GenericEntityException{
		List<EntityCondition> condition = FastList.newInstance();
		condition.add(EntityCondition.makeCondition("employeePartyId", partyId));
		condition.add(EntityCondition.makeCondition("emplPositionTypeId", emplPositionTypeId));
		condition.add(EntityCondition.makeCondition("fromDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
		List<GenericValue> emplPositionFulfillmentList = delegator.findList("EmplPositionAndFulfillment", EntityCondition.makeCondition(condition), null, UtilMisc.toList("-thruDate"), null, false);
		if(UtilValidate.isNotEmpty(emplPositionFulfillmentList)){
			return emplPositionFulfillmentList.get(0).getTimestamp("thruDate");
		}
		return fromDate;
	}
	
	public static Map<String,String> getListEmplPositionInOrgOfManager(Delegator delegator,String managerUserLoginId) throws Exception{
		List<GenericValue> listEmpl  = getListEmployeeOfManager(delegator,managerUserLoginId);
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
		conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
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
	
	
	
	public static List<GenericValue> getListEmployeeOfManager(Delegator delegator, String managerUserLoginId) throws Exception{
		List<GenericValue> employeeList = FastList.newInstance();
		Organization org = null;
		
		try {
			List<String> orgList = getListOrgManagedByParty(delegator, managerUserLoginId);
			for(String orgId: orgList){
				org = buildOrg(delegator, orgId);
				List<GenericValue> tempEmployeeList = org.getEmployeeInOrg(delegator);
				employeeList.addAll(tempEmployeeList);
			}
			
		} catch (GenericEntityException e) {
			Debug.log("Get Employee Fail");
		}
		
		return employeeList;
	}
	
	public static List<String> getListEmplMgrByParty(Delegator delegator, String userLoginId, Date fromDate, Date thruDate) throws GenericEntityException{
		Timestamp fromDateTs = new Timestamp(fromDate.getTime());
		Timestamp thruDateTs = null;
		if(thruDate != null){
			thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
		}
		return getListEmplMgrByParty(delegator, userLoginId, fromDateTs, thruDateTs, false);
	}
	
	/**
	 * get list employee that managed by userLogin
	 * @param delegator
	 * @param userLoginId
	 * @param fromDate
	 * @param thruDate
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<String> getListEmplMgrByParty(Delegator delegator, String userLoginId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		return getListEmplMgrByParty(delegator, userLoginId, fromDate, thruDate, false);
	}
	
	public static List<String> getListEmplMgrByParty(Delegator delegator, String userLoginId, Timestamp fromDate, Timestamp thruDate, boolean includeMgr) throws GenericEntityException{
		List<String> retList = FastList.newInstance();
		boolean isFullPerms = isFullPermissionView(delegator, userLoginId);
		List<String> orgList = FastList.newInstance();
		if(isFullPerms){
			orgList.add(getCurrentOrganization(delegator, userLoginId));
		}else{
			orgList = PartyUtil.getListOrgManagedByParty(delegator, userLoginId, fromDate, thruDate);
		}
		if(orgList != null){
			for(String orgId: orgList){
				Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
				List<GenericValue> tempList = buildOrg.getEmplInOrgAtPeriod(delegator, fromDate, thruDate);
				for(GenericValue temp: tempList){
					String partyId = temp.getString("partyId");
					if(!retList.contains(partyId)){
						retList.add(partyId);
					}
				}
			}
		}
		if(!includeMgr && !isFullPerms){
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
			String partyId = userLogin.getString("partyId");
			retList.remove(partyId);
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
	
	public static List<String> getSalessupDeptListByMgr(Delegator delegator, String managerUserLoginId){
		List<String> retList = FastList.newInstance();
		try {
			List<String> orgPartyListId = PartyUtil.getListOrgManagedByParty(delegator, managerUserLoginId);
			for(String orgPartyId: orgPartyListId){
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

	public static GenericValue getHrmAdmin(Delegator delegator, String organizationId) throws GenericEntityException {
//		#FIXME get hrm admin 
		return delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "OLBMBHRADMIN"), false);
	}

	public static List<String> getOrgNextLevelOfEmpl(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate) throws Exception{
		List<String> listDept = getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
		Set<String> listOrgNext = FastSet.newInstance();
		for(String deptId: listDept){
			GenericValue parentDept = getParentOrgOfDepartmentCurr(delegator, deptId);
			if(parentDept != null){
				listOrgNext.add(parentDept.getString("partyIdFrom"));
			}
		}
		List<String> retList = UtilMisc.toList(listOrgNext);
		return retList;
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
		//conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
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
	public static List<GenericValue> getPartyTaxParty(Delegator delegator, String partyId){
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("taxAuthGeoId", "VNM"));
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
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
		String roleTypeGroupId = null;
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
	
	public static List<String> getRoleTypeGroupListInPeriod(Delegator delegator, List<String> orgIdList,
			Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		List<String> retList = FastList.newInstance();
		for(String orgId: orgIdList){
			String tempRoleTypeGroupId = getRoleTypeGroupInPeriod(delegator, orgId, fromDate, thruDate);
			if(!retList.contains(tempRoleTypeGroupId) && tempRoleTypeGroupId != null){
				retList.add(tempRoleTypeGroupId);
			}
		}
		return retList;
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
		conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
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
		conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
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
		List<String> listRoles = SecurityUtil.getCurrentRoles(partyId, delegator);
		if(UtilValidate.isEmpty(listRoles)){
			return null;
		}
		if(listRoles.contains("INTERNAL_ORGANIZATIO")){
			return "INTERNAL_ORGANIZATIO";
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
		String rootId = getCurrentOrganization(delegator, null); 
		conditions.add(EntityCondition.makeCondition("partyId", orgId));
		conditions.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		
		EntityCondition commonConds = EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS");
		commonConds = EntityCondition.makeCondition(commonConds, EntityOperator.AND,  EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
		
		List<GenericValue> orgAddrList = delegator.findList("PartyContactDetailByPurpose", 
				EntityCondition.makeCondition(EntityCondition.makeCondition(conditions), EntityOperator.AND, commonConds), 
				null, UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isNotEmpty(orgAddrList) || orgId.equals(rootId)){
			return orgAddrList;
		}
		GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, orgId);
		if(parentOrg != null){
			return getPostalAddressOfOrg(delegator, parentOrg.getString("partyIdFrom"), fromDate, thruDate);
		}else{
			return FastList.newInstance();
		}
	}
	
	public static List<GenericValue> getPostalAddressOfOrg(Delegator delegator,
			List<String> orgIdList, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		List<GenericValue> retList = FastList.newInstance();
		for(String orgId: orgIdList){
			List<GenericValue> tempList = getPostalAddressOfOrg(delegator, orgId, fromDate, thruDate);
			retList.addAll(tempList);
		}
		return retList;
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
	
	public static String getSubsidiaryOfPartyGroup(Delegator delegator, String partyGroupId) throws GenericEntityException{
		String orgId = getRootOrganization(delegator);
		List<String> listSubsidiary = SecurityUtil.getPartiesByRoles("SUBSIDIARY", delegator);
		if(UtilValidate.isEmpty(listSubsidiary)){
			return orgId;
		}
		if(listSubsidiary.contains(partyGroupId)){
			return partyGroupId;
		}
		String tempOrgId = partyGroupId;
		while(true){
			GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempOrgId);
			if(parentOrg != null){
				String parentOrgId = parentOrg.getString("partyIdFrom");
				if(parentOrgId.equals(tempOrgId)){
					Debug.logError("Error in tree Oragnization " + parentOrgId + " is parent of itself", PartyUtil.class.getName());
					break;
				}
				if(listSubsidiary.contains(parentOrgId)){
					return parentOrgId;
				}else{
					tempOrgId = parentOrgId;	
				}
			}else{
				break;
			}
		}
		return orgId;
	}

	public static String getDefaultOrgOfEmployee(Delegator delegator, String partyId) throws GenericEntityException {
		GenericValue partyAtt = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "DEFAULT_ORG"), false);
		if(partyAtt != null){
			return partyAtt.getString("attrValue");
		}
		return getCurrentOrganization(delegator, null);
	}
	
	public static String getCurrentOrganization(Delegator delegator, String userLoginId) throws GenericEntityException{
		if(userLoginId == null){
			return MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		String organizationId = MultiOrganizationUtil.getLastOrganization(delegator, userLoginId);
		if(organizationId == null || "_NA_".equals(organizationId)){
			organizationId = getRootOrganization(delegator);
		}
		return organizationId;
	}
	
	public static String getRootOrganization(Delegator delegator) throws GenericEntityException{
		String organizationId = null;
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityUtil.getFilterByDateExpr());
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
		List<GenericValue> listGroupRollup = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		List<String> partyIdFromList = EntityUtil.getFieldListFromEntityList(listGroupRollup, "partyIdFrom", true);
		List<String> partyIdToList = EntityUtil.getFieldListFromEntityList(listGroupRollup, "partyIdTo", true);
		partyIdFromList.removeAll(partyIdToList);
		if(UtilValidate.isNotEmpty(partyIdFromList)){
			organizationId = partyIdFromList.get(0);
		}
		if(organizationId == null){
			//root organization is null because of not having department, so get root organization base on employee
			conds.clear();
			conds.add(EntityUtil.getFilterByDateExpr());
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
			List<GenericValue> listEmpl = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
			if(UtilValidate.isNotEmpty(listEmpl)){
				partyIdFromList = EntityUtil.getFieldListFromEntityList(listEmpl, "partyIdFrom", true);
				organizationId = partyIdFromList.get(0);
			}
		}
		return organizationId;
	}
	
	//FIXME need get root partyId, not current org
	public static String getRootOrganization(Delegator delegator, String userLoginId){
		if(userLoginId == null){
			return MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		return MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
	}
	
	

	public static List<GenericValue> getListSubsidiaryOfParty(
			Delegator delegator, String partyIdFrom, String customTimePeriodId) throws GenericEntityException {
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		Timestamp fromDateTs, thruDateTs;
		if(customTimePeriod != null){
			Date fromDate = customTimePeriod.getDate("fromDate");
			Date thruDate = customTimePeriod.getDate("thruDate");
			fromDateTs = new Timestamp(fromDate.getTime());
			thruDateTs = UtilDateTime.getDayEnd(new Timestamp(thruDate.getTime()));
		}else{
			fromDateTs = UtilDateTime.nowTimestamp();
			thruDateTs = UtilDateTime.nowTimestamp();
		}
		return getListSubsidiaryOfParty(delegator, partyIdFrom, fromDateTs, thruDateTs);		
	}
	
	public static List<GenericValue> getListSubsidiaryOfParty(
			Delegator delegator, String partyIdFrom, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
		return getListSubsidiaryOfParty(delegator, partyIdFrom, fromDate, thruDate, false);
	}
	
	public static List<GenericValue> getListSubsidiaryOfParty(
			Delegator delegator, String partyIdFrom, Timestamp fromDate, Timestamp thruDate, boolean includePartyFrom) throws GenericEntityException {
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition partyConds = EntityCondition.makeCondition("partyIdFrom", partyIdFrom); 
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SUBSIDIARY"));
		conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate));
		EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition(conds), EntityJoinOperator.AND, partyConds);
		List<GenericValue> listSub = delegator.findList("PartyRelationshipAndDetail", condition, null, UtilMisc.toList("partyIdTo"), null, false);
		if(includePartyFrom && SecurityUtil.hasRole("SUBSIDIARY", partyIdFrom, delegator)){
			GenericValue partyFrom = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyIdFrom), false);
			listSub.add(partyFrom);
		}
		return listSub;
	}
	
	public static List<String> getAncestorOfParty(Delegator delegator, String partyId) throws GenericEntityException{
		String currOrgId = getCurrentOrganization(delegator, null);
		String tempOrgId = partyId;
		List<String> retList = FastList.newInstance();
		while(!currOrgId.equals(tempOrgId)){
			GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempOrgId);
			if(parentOrg != null){
				String parentOrgId = parentOrg.getString("partyIdFrom");
				retList.add(parentOrgId);
				tempOrgId = parentOrgId;
			}else{
				break;
			}
		}
		return retList;
	}
	
	@Deprecated
	public static boolean checkAncestorOfParty(Delegator delegator, String ancestorId, String partyId) throws GenericEntityException{
		String rootId = getRootOrganization(delegator);
		String tempPartyId = partyId;
		do{
			GenericValue parentOrg = getParentOrgOfDepartmentCurr(delegator, tempPartyId);
			if(parentOrg == null){
				break;
			}
			tempPartyId = parentOrg.getString("partyIdFrom");
			if(tempPartyId.equals(ancestorId)){
				return true;
			}
		}while(!rootId.equals(tempPartyId) && !tempPartyId.equals(partyId));
		return false;
	}
	
	public static boolean checkAncestorOfParty(Delegator delegator, String ancestorId, String partyId, GenericValue userLogin) throws GenericEntityException{
		String rootId = getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String tempPartyId = partyId;
		do{
			GenericValue parentOrg = getParentOrgOfDepartmentCurr(delegator, tempPartyId);
			if(parentOrg == null){
				break;
			}
			tempPartyId = parentOrg.getString("partyIdFrom");
			if(tempPartyId.equals(ancestorId)){
				return true;
			}
		}while(!rootId.equals(tempPartyId) && !tempPartyId.equals(partyId));
		return false;
	}
	
	/**
	 * filter partyIds in list partyGroupList that have ancestor is ancestorId 
	 * @param delegator
	 * @param ancestorId
	 * @param partyGroupList
	 * @throws GenericEntityException 
	 */
	public static List<String> filterListPartyByAncestor(Delegator delegator,
			String ancestorId, List<String> partyGroupList) throws GenericEntityException {
		List<String> retList = FastList.newInstance();
		for(String partyGroupId: partyGroupList){
			if(ancestorId.equals(partyGroupId) || checkAncestorOfParty(delegator, ancestorId, partyGroupId)){
				retList.add(partyGroupId);
			}
		}
		return retList;
	}
	
	/**
	 * check whether partyId of userLoginId have managed partyGroupId or not
	 * @param delegator
	 * @param userLoginId
	 * @param partyGroupId
	 * @param fromDate
	 * @param thruDate
	 * @return
	 * @throws GenericEntityException 
	 */
	public static boolean checkPartyManageOrg(Delegator delegator, String userLoginId, String partyGroupId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		List<String> orgMgrList = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"), fromDate, thruDate);
		if(PartyUtil.isFullPermissionView(delegator, userLoginId)){
			return true;
		}
		if(orgMgrList == null){
			return false;
		}
		if(orgMgrList.contains(partyGroupId)){
			return true;
		}
		for(String orgMgr: orgMgrList){
			if(PartyUtil.checkAncestorOfParty(delegator, orgMgr, partyGroupId)){
				return true;
			}
		}
		return false;
	}
	
	public static List<GenericValue> getListAllEmplPositionTypeOfParty(Delegator delegator, String partyId, 
			Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		return getListAllEmplPositionTypeOfParty(delegator, partyId, fromDate, thruDate, null, null);
	}
	
	public static List<GenericValue> getListAllEmplPositionTypeOfParty(Delegator delegator, String partyId, 
			Timestamp fromDate, Timestamp thruDate, Set<String> listSelectedField, List<String> orderBy) throws GenericEntityException{
		Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, true, false);
		List<GenericValue> listOrg = buildOrg.getAllDepartmentList(delegator);
		List<String> listOrgId = FastList.newInstance();
		if(listOrg != null){
			listOrgId = EntityUtil.getFieldListFromEntityList(listOrg, "partyId", true);
		}
		listOrgId.add(partyId);
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listOrgId));
		conds.add(EntityConditionUtils.makeDateConds(fromDate, thruDate, "actualFromDate", "actualThruDate"));
		List<GenericValue> emplPosList = delegator.findList("EmplPositionAndType", EntityCondition.makeCondition(conds), listSelectedField, orderBy, null, false);
		return emplPosList;
	}
	
	public static void updatePartyCode(Delegator delegator, String partyId, String partyCode) throws GenericEntityException {
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		if(party != null){
			if(partyCode != null && partyCode.trim().length() > 0){
				party.set("partyCode", partyCode);
				party.store();
			}
		}
	}
	public static GenericValue getUserLoginByParty(Delegator delegator, String partyId) throws GenericEntityException{
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		List<GenericValue> UserLoginList= delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", partyId), null, null, opts, false);
		GenericValue UserLogin = EntityUtil.getFirst(UserLoginList);
		return UserLogin;
	}
	public static List<GenericValue> getEmployeeInDepartment(Delegator delegator, GenericValue userLogin, Timestamp moment) throws GenericEntityException {
		return getEmployeeInDepartmentByRole(delegator, userLogin, null, moment);
	}
	public static List<GenericValue> getEmployeeInDepartmentByRole(Delegator delegator, GenericValue userLogin, String roleTypeId, Timestamp moment) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		List<String> departments = getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), moment);
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, departments));
		if (UtilValidate.isNotEmpty(roleTypeId)) {
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.EQUALS, roleTypeId));
		}
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		return delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
	}
	/**
	 * 
	 * @param delegator
	 * @param orgId
	 * @param roleTypeId: role of employee with orgId
	 * @param partyRelationshipTypeId
	 * @param moment
	 * @return
	 * @throws GenericEntityException 
	 */
	public static List<String> getEmployeeHasRoleInDepartment(Delegator delegator, String orgId, String roleTypeId, String partyRelationshipTypeId, Timestamp moment) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		if(roleTypeId == null){
			return null;
		}
		if(partyRelationshipTypeId != null){
			conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
		}
		EntityCondition cond1 = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdFrom", orgId), EntityJoinOperator.AND,
															EntityCondition.makeCondition("roleTypeIdTo", roleTypeId));
		EntityCondition cond2 = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", orgId), EntityJoinOperator.AND,
										EntityCondition.makeCondition("roleTypeIdFrom", roleTypeId));
		conditions.add(EntityCondition.makeCondition(cond1, EntityJoinOperator.OR, cond2));
		conditions.add(EntityUtil.getFilterByDateExpr(moment));
		List<GenericValue> listParty = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions), null, null, null, false);
		Set<String> retSet = FastSet.newInstance();
		for(GenericValue tempGv: listParty){
			String roleTypeIdTo = tempGv.getString("roleTypeIdTo");
			String partyIdFrom = tempGv.getString("partyIdFrom");
			String partyIdTo = tempGv.getString("partyIdTo");
			String roleTypeIdFrom = tempGv.getString("roleTypeIdFrom");
			if(roleTypeId.equals(roleTypeIdFrom) && !partyIdFrom.equals(orgId)){
				retSet.add(partyIdFrom);
			}else if(roleTypeId.equals(roleTypeIdTo) && !partyIdTo.equals(orgId)){
				retSet.add(partyIdTo);
			}
		}
		List<String> retList = UtilMisc.toList(retSet);
		return retList;
	}
	public static GenericValue getCurrentManagerOrg(Delegator delegator, String partyId, String roleTypeId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
		if (UtilValidate.isNotEmpty(roleTypeId)) {
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.EQUALS, roleTypeId));
		}
		conditions.add(EntityUtil.getFilterByDateExpr());
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "MANAGER")));
		List<GenericValue> list = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
		return EntityUtil.getFirst(list);
	}
	public static List<GenericValue> getEmployeeInDepartmentByRole(Delegator delegator, List<String> departments, String roleTypeId, Timestamp moment) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, departments));
		if (UtilValidate.isNotEmpty(roleTypeId)) {
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.EQUALS, roleTypeId));
		}
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
		return delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
	}
	public static List<GenericValue> getEmployeeInDepartmentByRole(Delegator delegator, String department, String roleTypeId, Timestamp moment) throws GenericEntityException {
		return getEmployeeInDepartmentByRole(delegator, UtilMisc.toList(department), roleTypeId, moment);
	}
	public static List<GenericValue> getEmplInOrgAtPeriod(Delegator delegator, String userLoginId) throws GenericEntityException{
		Timestamp now = UtilDateTime.nowTimestamp();
		List<GenericValue> emplList = FastList.newInstance();
		List<String> listOrg = getListOrgManagedByParty(delegator, userLoginId, now, null);
		for (String s : listOrg) {
			Organization buildOrg = buildOrg(delegator, s, true, false);
			emplList = buildOrg.getEmplInOrgAtPeriod(delegator, now, null);
		}
		
		return emplList;
	}
	public static String getPartyCode(Delegator delegator, String partyId) throws GenericEntityException{
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		return getPartyCode(party);
	}
	public static String getPartyCode(GenericValue party){
		if(party == null){
			return null;
		}
		String partyCode = party.getString("partyCode");
		if(partyCode == null){
			partyCode = party.getString("partyId");
		}
		return partyCode;
	}
	
	public static Boolean checkEmployeeInOrg(Delegator delegator, String partyGroupId, String employeeId, Timestamp fromDate, Timestamp thruDate,GenericValue userLogin) throws GenericEntityException {
		List<String> listDeptEmpl = getDepartmentOfEmployee(delegator, employeeId, fromDate, thruDate);
		if(UtilValidate.isEmpty(listDeptEmpl)){
			return false;
		}
		for(String deptId: listDeptEmpl){
			if(partyGroupId.equals(deptId)){
				return true;
			}
			if(PartyUtil.checkAncestorOfParty(delegator,partyGroupId,deptId,userLogin)){
			    return true;
            }
		}
		return false;
	}
	public static String getEmployeeAvatarUrl(Delegator delegator, String partyId) throws GenericEntityException{
		List<GenericValue> partyContentList = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId, "partyContentTypeId", "LGOIMGURL"), UtilMisc.toList("-fromDate"), false);
		if(UtilValidate.isNotEmpty(partyContentList)){
			GenericValue partyContent = partyContentList.get(0);
			String contentId = partyContent.getString("contentId");
			GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
			String dataResourceId = content.getString("dataResourceId");
			GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
			String objectInfo = dataResource.getString("objectInfo");
			return objectInfo;
		}
		return null;
	}
	public static Integer getTotalDependentPerson(Delegator delegator, String partyId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", partyId));
		conds.add(EntityCondition.makeCondition("isDependent", "Y"));
		conds.add(EntityCondition.makeCondition("statusId", "DEP_ACCEPT"));
		conds.add(EntityCondition.makeCondition("dependentStartDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, fromDate));
		conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dependentEndDate", null),
												EntityJoinOperator.OR,
												EntityCondition.makeCondition("dependentEndDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, thruDate)));
		List<GenericValue> personDependentList = delegator.findList("PersonFamilyBackground", EntityCondition.makeCondition(conds), null, null, null, false); 
		return personDependentList.size();
	}
	
	public static List<GenericValue> getPartyPostalAddressByPurpose(Delegator delegator, String partyId, String purpose){
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", purpose));
		conditions.add(EntityCondition.makeCondition("partyId", partyId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> postalAddressList = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechPurposes = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditions), null, null, null, false);
			listPartyContactMechPurposes  = EntityUtil.filterByDate(listPartyContactMechPurposes);
			for (GenericValue item : listPartyContactMechPurposes) {
				List<GenericValue> tmp = delegator.findList("PostalAddressFullNameDetail", EntityCondition.makeCondition("contactMechId", item.getString("contactMechId")), null, null, null, false);
				postalAddressList.addAll(tmp);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
		}
		return postalAddressList;
	}
	public static List<GenericValue> getListDirectSubOrgOfParty(Delegator delegator, List<String> listParentOrgId) throws GenericEntityException{
		List<GenericValue> retList = FastList.newInstance();
		if(listParentOrgId == null){
			return null;
		}
		for(String partyId: listParentOrgId){
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, false, false);
			List<GenericValue> tempList = buildOrg.getDirectChildList(delegator);
			if(tempList != null){
				retList.addAll(tempList);
			}
		}
		return retList;
	}
	
	public static List<GenericValue> getListDirectEmplOfParty(Delegator delegator, List<String> listOrgId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
		List<GenericValue> retList = FastList.newInstance();
		if(listOrgId == null){
			return null;
		}
		for(String partyId: listOrgId){
			Organization buildOrg = PartyUtil.buildOrg(delegator, partyId, false, false);
			List<GenericValue> tempList = buildOrg.getDirectEmployee(delegator, fromDate, thruDate);
			if(tempList != null){
				retList.addAll(tempList);
			}
		}
		return retList;
	}
	


	/**
	 * check is sales dept
	 */
	public static boolean isSalesDepartmentManager(Delegator delegator, String partyId, String organization){
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALES_DEPARTMENT"));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()){
			List<String> listDsa = FastList.newInstance();
			listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
			
			conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listDsa));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			conds.add(EntityUtil.getFilterByDateExpr());
			
			listRelationships = FastList.newInstance();
			try {
				listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
				Debug.logError(e, errMsg);
				return false;
			}
			if (!listRelationships.isEmpty()) return true;
		}
		return false;
	}
	
	/**
	 * check is CSM dept
	 */
	public static boolean isSalesCsmDepartmentManager(Delegator delegator, String partyId, String organization){
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALES_DEPARTMENT"));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()){
			List<String> listDsa = FastList.newInstance();
			listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
			if (!listDsa.isEmpty()){
				List<GenericValue> listRoleTypes = FastList.newInstance();
				try {
					listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "CSM_DEPT"), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				if (listRoleTypes.isEmpty()) return false;
				
				List<String> listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDsa));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityUtil.getFilterByDateExpr());
				
				listRelationships = FastList.newInstance();
				try {
					listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				if (!listRelationships.isEmpty()) {
					List<String> listCsm = FastList.newInstance();
					listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
					if (!listCsm.isEmpty()){
						conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listCsm));
						conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						
						if (!listRelationships.isEmpty()) return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * check is RSM dept
	 */
	public static boolean isSalesRsmDepartmentManager(Delegator delegator, String partyId, String organization){
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALES_DEPARTMENT"));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()){
			List<String> listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
			if (!listDsa.isEmpty()){
				List<GenericValue> listRoleTypes = FastList.newInstance();
				try {
					listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "CSM_DEPT"), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				if (listRoleTypes.isEmpty()) return false;
				
				List<String> listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDsa));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityUtil.getFilterByDateExpr());
				
				listRelationships = FastList.newInstance();
				try {
					listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				
				if (!listRelationships.isEmpty()) {
					List<String> listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
					if (!listCsm.isEmpty()){
						listRoleTypes = FastList.newInstance();
						try {
							listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (listRoleTypes.isEmpty()) return false;
						
						listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listCsm));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (!listRelationships.isEmpty()) {
							List<String> listRsm = FastList.newInstance();
							listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
							if (!listRsm.isEmpty()){
								conds = FastList.newInstance();
								conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listRsm));
								conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
								conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
								conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
								conds.add(EntityUtil.getFilterByDateExpr());
								
								listRelationships = FastList.newInstance();
								try {
									listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
									Debug.logError(e, errMsg);
									return false;
								}
								
								if (!listRelationships.isEmpty()) return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * check is ASM dept
	 */
	public static boolean isSalesAsmDepartmentManager(Delegator delegator, String partyId, String organization){
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALES_DEPARTMENT"));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()){
			List<String> listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
			if (!listDsa.isEmpty()){
				List<GenericValue> listRoleTypes = FastList.newInstance();
				try {
					listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "CSM_DEPT"), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				if (listRoleTypes.isEmpty()) return false;
				
				List<String> listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDsa));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityUtil.getFilterByDateExpr());
				
				listRelationships = FastList.newInstance();
				try {
					listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				
				if (!listRelationships.isEmpty()) {
					List<String> listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
					if (!listCsm.isEmpty()){
						listRoleTypes = FastList.newInstance();
						List<String> listRoleTypeRsms = FastList.newInstance();
						try {
							listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (listRoleTypes.isEmpty()) return false;
						
						listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						listRoleTypeRsms = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listCsm));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (!listRelationships.isEmpty()) {
							List<String> listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
							if (!listRsm.isEmpty()){
								listRoleTypes = FastList.newInstance();
								try {
									listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "ASM_DEPT"), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
									Debug.logError(e, errMsg);
									return false;
								}
								if (listRoleTypes.isEmpty()) return false;
								
								listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
								conds = FastList.newInstance();
								conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listRsm));
								conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
								conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoleTypeRsms));
								conds.add(EntityUtil.getFilterByDateExpr());
								
								listRelationships = FastList.newInstance();
								try {
									listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
									Debug.logError(e, errMsg);
									return false;
								}
								if (!listRelationships.isEmpty()) {
									List<String> listAsm = FastList.newInstance();
									listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
									if (!listAsm.isEmpty()){
										conds = FastList.newInstance();
										conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listAsm));
										conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
										conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
										conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
										conds.add(EntityUtil.getFilterByDateExpr());
										
										listRelationships = FastList.newInstance();
										try {
											listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
											Debug.logError(e, errMsg);
											return false;
										}
										
										if (!listRelationships.isEmpty()) return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * check is SUP dept
	 */
	public static boolean isSalesSupDepartmentManager(Delegator delegator, String partyId, String organization){
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "SALES_DEPARTMENT"));
		conds.add(EntityUtil.getFilterByDateExpr());
		
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()){
			List<String> listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
			if (!listDsa.isEmpty()){
				List<GenericValue> listRoleTypes = FastList.newInstance();
				try {
					listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "CSM_DEPT"), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				if (listRoleTypes.isEmpty()) return false;
				
				List<String> listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDsa));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityUtil.getFilterByDateExpr());
				
				listRelationships = FastList.newInstance();
				try {
					listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
					Debug.logError(e, errMsg);
					return false;
				}
				
				if (!listRelationships.isEmpty()) {
					List<String> listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
					if (!listCsm.isEmpty()){
						listRoleTypes = FastList.newInstance();
						List<String> listRoleTypeRsms = FastList.newInstance();
						try {
							listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (listRoleTypes.isEmpty()) return false;
						
						listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						listRoleTypeRsms = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listCsm));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return false;
						}
						if (!listRelationships.isEmpty()) {
							List<String> listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
							if (!listRsm.isEmpty()){
								listRoleTypes = FastList.newInstance();
								try {
									listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "ASM_DEPT"), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
									Debug.logError(e, errMsg);
									return false;
								}
								if (listRoleTypes.isEmpty()) return false;
								
								listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
								conds = FastList.newInstance();
								conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listRsm));
								conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
								conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoleTypeRsms));
								conds.add(EntityUtil.getFilterByDateExpr());
								
								listRelationships = FastList.newInstance();
								try {
									listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
									Debug.logError(e, errMsg);
									return false;
								}
								if (!listRelationships.isEmpty()) {
									List<String> listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
									if (!listAsm.isEmpty()){
										listRoleTypes = FastList.newInstance();
										try {
											listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "SALESSUP_DEPT"), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
											Debug.logError(e, errMsg);
											return false;
										}
										if (listRoleTypes.isEmpty()) return false;
										
										listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
										conds = FastList.newInstance();
										conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listAsm));
										conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
										conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
										conds.add(EntityUtil.getFilterByDateExpr());
										
										listRelationships = FastList.newInstance();
										try {
											listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
											Debug.logError(e, errMsg);
											return false;
										}
									}
									if (!listRelationships.isEmpty()) {
										List<String> listSup = FastList.newInstance();
										listSup = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
										if (!listSup.isEmpty()){
											conds = FastList.newInstance();
											conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listSup));
											conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
											conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
											conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
											conds.add(EntityUtil.getFilterByDateExpr());
											
											listRelationships = FastList.newInstance();
											try {
												listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
											} catch (GenericEntityException e) {
												String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
												Debug.logError(e, errMsg);
												return false;
											}
											
											if (!listRelationships.isEmpty()) return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	
	public static  List<String> getDistributorManages (Delegator delegator, String partyId, String organization){
		List<GenericValue> listDistributors = FastList.newInstance();
		List<String> listPartyIds = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		List<GenericValue> listRelationships = FastList.newInstance();

		List<GenericValue> listRoleTypes = FastList.newInstance();
		List<GenericValue> listRoleTypeIdTos = FastList.newInstance();
		if (isSalesDepartmentManager(delegator, partyId, organization)){
			// get CSM -> RSM -> ASM -> SUP -> DIS
			// get DAS department
			
			conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			conds.add(EntityUtil.getFilterByDateExpr());
			
			listRelationships = FastList.newInstance();
			try {
				listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
				Debug.logError(e, errMsg);
				return listPartyIds;
			}
			if (!listRelationships.isEmpty()) {
				// get CSM department

				List<String> listDsa = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
				if (!listDsa.isEmpty()){
					try {
						listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "CSM_DEPT"), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
						Debug.logError(e, errMsg);
						return listPartyIds;
					}
					if (listRoleTypes.isEmpty()) return listPartyIds;
					
					listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
					conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listDsa));
					conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
					conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
					conds.add(EntityUtil.getFilterByDateExpr());
					
					listRelationships = FastList.newInstance();
					try {
						listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
						Debug.logError(e, errMsg);
						return listPartyIds;
					}
					
					if (!listRelationships.isEmpty()) {
						// get RSM department
						List<String> listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
						if (!listCsm.isEmpty()){
							listRoleTypes = FastList.newInstance();
							List<String> listRoleTypeRsms = FastList.newInstance();
							try {
								listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
								Debug.logError(e, errMsg);
								return listPartyIds;
							}
							if (listRoleTypes.isEmpty()) return listPartyIds;
							
							listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
							listRoleTypeRsms = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
							conds = FastList.newInstance();
							conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listCsm));
							conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
							conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
							conds.add(EntityUtil.getFilterByDateExpr());
							
							listRelationships = FastList.newInstance();
							try {
								listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
								Debug.logError(e, errMsg);
								return listPartyIds;
							}
							if (!listRelationships.isEmpty()) {
								// get ASM department
								List<String> listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
								if (!listRsm.isEmpty()){
									listRoleTypes = FastList.newInstance();
									try {
										listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "ASM_DEPT"), null, null, null, false);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
										Debug.logError(e, errMsg);
										return listPartyIds;
									}
									if (listRoleTypes.isEmpty()) return listPartyIds;
									
									listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
									conds = FastList.newInstance();
									conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listRsm));
									conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
									conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoleTypeRsms));
									conds.add(EntityUtil.getFilterByDateExpr());
									
									listRelationships = FastList.newInstance();
									try {
										listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
										Debug.logError(e, errMsg);
										return listPartyIds;
									}
									if (!listRelationships.isEmpty()) {
										// get SUP department
										List<String> listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
										if (!listAsm.isEmpty()){
											listRoleTypes = FastList.newInstance();
											try {
												listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "SALESSUP_DEPT"), null, null, null, false);
											} catch (GenericEntityException e) {
												String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
												Debug.logError(e, errMsg);
												return listPartyIds;
											}
											if (listRoleTypes.isEmpty()) return listPartyIds;
											
											listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
											conds = FastList.newInstance();
											conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listAsm));
											conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
											conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
											conds.add(EntityUtil.getFilterByDateExpr());
											
											listRelationships = FastList.newInstance();
											try {
												listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
											} catch (GenericEntityException e) {
												String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
												Debug.logError(e, errMsg);
												return listPartyIds;
											}
										}
										if (!listRelationships.isEmpty()) {
											List<String> listSup = FastList.newInstance();
											listSup = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
											if (!listSup.isEmpty()){
												conds = FastList.newInstance();
												conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listSup));
												conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
												conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
												conds.add(EntityUtil.getFilterByDateExpr());
												
												listRelationships = FastList.newInstance();
												try {
													listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
												} catch (GenericEntityException e) {
													String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
													Debug.logError(e, errMsg);
													return listPartyIds;
												}
												
												if (!listRelationships.isEmpty()) {
													listPartyIds = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdFrom", true);
													try {
														listDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listPartyIds), null, null, null, false);
													} catch (GenericEntityException e) {
														String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
														Debug.logError(e, errMsg);
														return listPartyIds;
													}
													return listPartyIds;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (isSalesCsmDepartmentManager(delegator, partyId, organization)){
				// get RSM -> ASM -> SUP -> DIS
				// get CSM department
				conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
				conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
				conds.add(EntityUtil.getFilterByDateExpr());
				
				listRelationships = FastList.newInstance();
				try {
					listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
					Debug.logError(e, errMsg);
					return listPartyIds;
				}
					
				if (!listRelationships.isEmpty()) {
					// get RSM department
					List<String> listCsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
					if (!listCsm.isEmpty()){
						listRoleTypes = FastList.newInstance();
						List<String> listRoleTypeRsms = FastList.newInstance();
						try {
							listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
							Debug.logError(e, errMsg);
							return listPartyIds;
						}
						if (listRoleTypes.isEmpty()) return listPartyIds;
						
						listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						listRoleTypeRsms = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
						conds = FastList.newInstance();
						conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listCsm));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return listPartyIds;
						}
						if (!listRelationships.isEmpty()) {
							// get ASM department
							List<String> listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
							if (!listRsm.isEmpty()){
								listRoleTypes = FastList.newInstance();
								try {
									listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "ASM_DEPT"), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
									Debug.logError(e, errMsg);
									return listPartyIds;
								}
								if (listRoleTypes.isEmpty()) return listPartyIds;
								
								listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
								conds = FastList.newInstance();
								conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listRsm));
								conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
								conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoleTypeRsms));
								conds.add(EntityUtil.getFilterByDateExpr());
								
								listRelationships = FastList.newInstance();
								try {
									listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
									Debug.logError(e, errMsg);
									return listPartyIds;
								}
								if (!listRelationships.isEmpty()) {
									// get SUP department
									List<String> listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
									if (!listAsm.isEmpty()){
										listRoleTypes = FastList.newInstance();
										try {
											listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "SALESSUP_DEPT"), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
											Debug.logError(e, errMsg);
											return listPartyIds;
										}
										if (listRoleTypes.isEmpty()) return listPartyIds;
										
										listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
										conds = FastList.newInstance();
										conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listAsm));
										conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
										conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
										conds.add(EntityUtil.getFilterByDateExpr());
										
										listRelationships = FastList.newInstance();
										try {
											listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
											Debug.logError(e, errMsg);
											return listPartyIds;
										}
									}
									if (!listRelationships.isEmpty()) {
										List<String> listSup = FastList.newInstance();
										listSup = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
										if (!listSup.isEmpty()){
											conds = FastList.newInstance();
											conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listSup));
											conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
											conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
											conds.add(EntityUtil.getFilterByDateExpr());
											
											listRelationships = FastList.newInstance();
											try {
												listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
											} catch (GenericEntityException e) {
												String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
												Debug.logError(e, errMsg);
												return listPartyIds;
											}
											
											if (!listRelationships.isEmpty()) {
												listPartyIds = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdFrom", true);
												try {
													listDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listPartyIds), null, null, null, false);
												} catch (GenericEntityException e) {
													String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
													Debug.logError(e, errMsg);
													return listPartyIds;
												}
												return listPartyIds;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (isSalesRsmDepartmentManager(delegator, partyId, organization)){
					// get ASM -> SUP -> DIS
					// get RSM department
					conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
					conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
					conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
					conds.add(EntityUtil.getFilterByDateExpr());
					
					listRelationships = FastList.newInstance();
					try {
						listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
						Debug.logError(e, errMsg);
						return listPartyIds;
					}
						
					if (!listRelationships.isEmpty()) {
						if (!listRelationships.isEmpty()) {
							// get ASM department
							List<String> listRsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
							if (!listRsm.isEmpty()){
								listRoleTypes = FastList.newInstance();
								List<String> listRoleTypeRsms = FastList.newInstance();
								try {
									listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "ASM_DEPT"), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
									Debug.logError(e, errMsg);
									return listPartyIds;
								}
								if (listRoleTypes.isEmpty()) return listPartyIds;
								listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
								
								listRoleTypes = FastList.newInstance();
								try {
									listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "RSM_DEPT"), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
									Debug.logError(e, errMsg);
									return listPartyIds;
								}
								
								listRoleTypeRsms = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
								conds = FastList.newInstance();
								conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listRsm));
								conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
								conds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, listRoleTypeRsms));
								conds.add(EntityUtil.getFilterByDateExpr());
								
								listRelationships = FastList.newInstance();
								try {
									listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
									Debug.logError(e, errMsg);
									return listPartyIds;
								}
								if (!listRelationships.isEmpty()) {
									// get SUP department
									List<String> listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
									if (!listAsm.isEmpty()){
										listRoleTypes = FastList.newInstance();
										try {
											listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "SALESSUP_DEPT"), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
											Debug.logError(e, errMsg);
											return listPartyIds;
										}
										if (listRoleTypes.isEmpty()) return listPartyIds;
										
										listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
										conds = FastList.newInstance();
										conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listAsm));
										conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
										conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
										conds.add(EntityUtil.getFilterByDateExpr());
										
										listRelationships = FastList.newInstance();
										try {
											listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
											Debug.logError(e, errMsg);
											return listPartyIds;
										}
										if (!listRelationships.isEmpty()) {
											List<String> listSup = FastList.newInstance();
											listSup = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
											if (!listSup.isEmpty()){
												conds = FastList.newInstance();
												conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listSup));
												conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
												conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
												conds.add(EntityUtil.getFilterByDateExpr());
												
												listRelationships = FastList.newInstance();
												try {
													listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
												} catch (GenericEntityException e) {
													String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
													Debug.logError(e, errMsg);
													return listPartyIds;
												}
												
												if (!listRelationships.isEmpty()) {
													listPartyIds = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdFrom", true);
													try {
														listDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listPartyIds), null, null, null, false);
													} catch (GenericEntityException e) {
														String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
														Debug.logError(e, errMsg);
														return listPartyIds;
													}
													return listPartyIds;
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
					if (isSalesAsmDepartmentManager(delegator, partyId, organization)) {
						// get ASM -> SUP -> DIS
						// get ASM department
						conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
						conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
						conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
						conds.add(EntityUtil.getFilterByDateExpr());
						
						listRelationships = FastList.newInstance();
						try {
							listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
							Debug.logError(e, errMsg);
							return listPartyIds;
						}
							
						if (!listRelationships.isEmpty()) {
							if (!listRelationships.isEmpty()) {
								// get SUP department
								List<String> listAsm = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
								if (!listAsm.isEmpty()){
									listRoleTypes = FastList.newInstance();
									try {
										listRoleTypes = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", "SALESSUP_DEPT"), null, null, null, false);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when findList RoleType: " + e.toString();
										Debug.logError(e, errMsg);
										return listPartyIds;
									}
									if (listRoleTypes.isEmpty()) return listPartyIds;
									
									listRoleTypeIdTos = EntityUtil.getFieldListFromEntityList(listRoleTypes, "roleTypeId", true);
									conds = FastList.newInstance();
									conds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, listAsm));
									conds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, listRoleTypeIdTos));
									conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
									conds.add(EntityUtil.getFilterByDateExpr());
									
									listRelationships = FastList.newInstance();
									try {
										listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
									} catch (GenericEntityException e) {
										String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
										Debug.logError(e, errMsg);
										return listPartyIds;
									}
									if (!listRelationships.isEmpty()) {
										List<String> listSup = FastList.newInstance();
										listSup = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdTo", true);
										if (!listSup.isEmpty()){
											conds = FastList.newInstance();
											conds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, listSup));
											conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
											conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
											conds.add(EntityUtil.getFilterByDateExpr());
											
											listRelationships = FastList.newInstance();
											try {
												listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
											} catch (GenericEntityException e) {
												String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
												Debug.logError(e, errMsg);
												return listPartyIds;
											}
											
											if (!listRelationships.isEmpty()) {
												listPartyIds = EntityUtil.getFieldListFromEntityList(listRelationships, "partyIdFrom", true);
												try {
													listDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listPartyIds), null, null, null, false);
												} catch (GenericEntityException e) {
													String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
													Debug.logError(e, errMsg);
													return listPartyIds;
												}
												return listPartyIds;
											}
										}
									}
								}
							}
						}
					} else {
						if (isSalesSupDepartmentManager(delegator, partyId, organization)) {
							try {
								listDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", partyId), null, null, null, false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
								Debug.logError(e, errMsg);
								return listPartyIds;
							}
						}
					}
				}
			}
		}
		
		if (!listDistributors.isEmpty()){
			listPartyIds = EntityUtil.getFieldListFromEntityList(listDistributors, "partyId", true);
		}
		return listPartyIds;
	}
	
	public static boolean isDistributor (Delegator delegator, String partyId, String organization){
		List<EntityCondition> conds = FastList.newInstance();
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		conds.add(EntityCondition.makeCondition("partyIdTo", organization));
		conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "DISTRIBUTOR"));
		conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listRelationships = FastList.newInstance();
		try {
			listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyRelationship: " + e.toString();
			Debug.logError(e, errMsg);
			return false;
		}
		if (!listRelationships.isEmpty()) return true;
		return false;
	}
}

