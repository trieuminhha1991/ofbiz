package com.olbius.basesales.util;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;

import com.olbius.basehr.util.SecurityUtil;

public class SalesPartyUtil {
	public static final String module = SalesPartyUtil.class.getName();
	public static String RESOURCE_PROPERTIES = "basesales.properties";
	public enum EmplRoleEnum {
		CALLCENTER("role.employee.callcenter"), 
		CALLCENTER_MANAGER("role.manager.callcenter"), 
		EMPLOYEE("role.employee"), 
		SALES_EMPLOYEE("role.sales.employee"), 
		SALESADMIN_MANAGER("role.sales.admin.manager"), 
		SALESADMIN("role.sales.admin"), 
		CSM_EMPL("role.salescsm.empl"), 
		RSM_EMPL("role.salesrsm.empl"), 
		ASM_EMPL("role.salesasm.empl"), 
		SALESSUP_EMPL("role.salessup.empl"), 
		SALESMAN_EMPL("role.salesman.empl"), 
		DISTRIBUTOR("role.distributor"), 
		CSM_DEPT("role.salescsm.dept"),
		RSM_DEPT("role.salesrsm.dept"),
		ASM_DEPT("role.salesasm.dept"),
		SUP_DEPT("role.salessup.dept");
		
		private String value;
		private EmplRoleEnum(String value) {
			this.value = value;
		}
		public String getValue(){
			return value;
		}
	};
	
	public static List<String> getDescendantRoleTypeIds(String roleTypeId, Delegator delegator) {
		List<String> listRoleTypeIds = new ArrayList<String>();
		if (UtilValidate.isEmpty(roleTypeId)) return listRoleTypeIds;
		try {
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);
			if (roleType != null) {
				listRoleTypeIds.add(roleType.getString("roleTypeId"));
				List<GenericValue> listChild = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", roleType.get("roleTypeId")), null, false);
				if (UtilValidate.isNotEmpty(listChild)) {
					for (GenericValue child : listChild) {
						List<String> resultList = getDescendantRoleTypeIds(child.getString("roleTypeId"), delegator);
						if (UtilValidate.isNotEmpty(resultList)) listRoleTypeIds.addAll(resultList);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getDescendantRoleTypeIds: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listRoleTypeIds;
	}
	
	public static List<String> getDescendantPartyTypeIds(String partyTypeId, Delegator delegator) {
		List<String> listPartyTypeIds = new ArrayList<String>();
		if (UtilValidate.isEmpty(partyTypeId)) return listPartyTypeIds;
		try {
			GenericValue partyType = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId), false);
			if (partyType != null) {
				listPartyTypeIds.add(partyType.getString("partyTypeId"));
				List<GenericValue> listChild = delegator.findByAnd("PartyType", UtilMisc.toMap("parentTypeId", partyType.get("partyTypeId")), null, false);
				if (UtilValidate.isNotEmpty(listChild)) {
					for (GenericValue child : listChild) {
						List<String> resultList = getDescendantRoleTypeIds(child.getString("partyTypeId"), delegator);
						if (UtilValidate.isNotEmpty(resultList)) listPartyTypeIds.addAll(resultList);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getDescendantPartyTypeIds: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listPartyTypeIds;
	}
	
	
	@Deprecated
	public static List<String> getPartiesByRoles(Delegator delegator, List<String> roleTypeIds, boolean isPerson){
		List<String> listPartyId = new ArrayList<String>();
		if (roleTypeIds == null) return listPartyId;
		for (String roleTypeId : roleTypeIds) {
			List<String> tmp = SecurityUtil.getPartiesByRoles(roleTypeId, delegator, isPerson);
			if (tmp != null) listPartyId.addAll(tmp);
		}
		return listPartyId;
	}
	
	public static List<String> getPartiesByRoles(Delegator delegator, List<String> roleTypeIds, boolean isPerson, GenericValue userLogin){
		List<String> listPartyId = new ArrayList<String>();
		if (roleTypeIds == null) return listPartyId;
		for (String roleTypeId : roleTypeIds) {
			List<String> tmp = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin, roleTypeId, delegator);
			if (tmp != null) listPartyId.addAll(tmp);
		}
		return listPartyId;
	}
	public static List<String> getPartiesByRoles(Delegator delegator, List<String> roleTypeIds, boolean isPerson, String organizationPartyId){
		List<String> listPartyId = new ArrayList<String>();
		if (roleTypeIds == null) return listPartyId;
		for (String roleTypeId : roleTypeIds) {
			List<String> tmp = SecurityUtil.getPartiesByRolesWithOrg(organizationPartyId, roleTypeId, delegator);
			if (tmp != null) listPartyId.addAll(tmp);
		}
		return listPartyId;
	}
	
	/*@Deprecated
	public static List<String> getPartiesHavePermissionByActionOrder(Delegator delegator, String action){
		return getPartiesHavePermissionByActionOrder(delegator, action, null);
	}*/
	
	public static List<String> getPartiesHavePermissionByActionOrder(Delegator delegator, String action, GenericValue userLogin){
		List<String> listRoleTypeId = null;
		if ("APPROVE".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.approve.order", delegator));
		} else if ("RECEIVE_MSG_APPROVED".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.receiveMsg.order.approved", delegator));
		} else if ("RECEIVE_MSG_FAVOR_DELIVERY".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.receiveMsg.order.favor.delivery", delegator)); 
		}
		return getPartiesByRoles(delegator, listRoleTypeId, true, userLogin);
	}
	
	public static List<String> getPartiesHavePermissionByActionReturnOrder(Delegator delegator, String action, GenericValue userLogin){
		List<String> listRoleTypeId = null;
		if ("RECEIVE_MSG_APPROVED".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.receiveMsg.returnorder.approved", delegator));
		}
		return getPartiesByRoles(delegator, listRoleTypeId, true, userLogin);
	}
	
	public static List<String> getPartiesHavePermissionByActionPromo(Delegator delegator, String action, GenericValue userLogin){
		List<String> listRoleTypeId = null;
		if ("APPROVE".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.approve.promo", delegator));
		} else if ("RECEIVE_MSG_APPROVED".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.receiveMsg.promo.approved", delegator));
		}
		return getPartiesByRoles(delegator, listRoleTypeId, true, userLogin);
	}
	
	public static List<String> getPartiesHavePermReqDeliveryOrder(Delegator delegator, String action, GenericValue userLogin){
		List<String> listRoleTypeId = null;
		if ("APPROVE".equals(action)) {
			listRoleTypeId = SalesUtil.processKeyProperty(EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "roleTypeId.approve.req.delivery.order", delegator));
		}
		return getPartiesByRoles(delegator, listRoleTypeId, true, userLogin);
	}
	
	/*public static List<String> getSalesExecutiveIdsByCustomer(Delegator delegator, String customerId) {
		List<String> listSalesExecutiveId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
		String salesExecutiveTypeId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
		List<String> customerTypeIds = getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(salesExecutiveTypeId) || UtilValidate.isEmpty(customerTypeIds)) 
			return listSalesExecutiveId;
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", customerId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", salesExecutiveTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, customerTypeIds));
			listSalesExecutiveId = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), "partyIdFrom", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getSalesExecutiveIdsByCustomer method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return listSalesExecutiveId;
	}*/
	
	public static List<String> getSalesExecutiveIdsOrderByCustomer(Delegator delegator, String customerId) {
		List<String> listSalesExecutiveId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
		List<String> salesExecutiveTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.sales.executive.order");
		
		//List<String> customerTypeIds = getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
		List<String> customerTypeIds = new ArrayList<String>();
		List<String> roleCustomers = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.customers");
		if (UtilValidate.isNotEmpty(roleCustomers)) {
			for (String roleCustomer : roleCustomers) {
				List<String> tmp = SalesPartyUtil.getDescendantRoleTypeIds(roleCustomer, delegator);
				if (UtilValidate.isNotEmpty(tmp)) customerTypeIds.addAll(tmp);
			}
		}
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(salesExecutiveTypeIds) || UtilValidate.isEmpty(customerTypeIds)) 
			return listSalesExecutiveId;
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", customerId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, salesExecutiveTypeIds));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, customerTypeIds));
			listSalesExecutiveId = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false), "partyIdFrom", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getSalesExecutiveIdsByCustomer method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return listSalesExecutiveId;
	}
	
	public static List<String> getSalesExecutiveIdsByOrganization(Delegator delegator, String organizationId) {
		List<String> listSalesExecutiveId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.employee");
		String salesExecutiveTypeId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
		List<String> customerTypeIds = getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(salesExecutiveTypeId) || UtilValidate.isEmpty(customerTypeIds)) 
			return listSalesExecutiveId;
		
		listSalesExecutiveId = SecurityUtil.getPartiesByRoles(salesExecutiveTypeId, delegator);
		
		return listSalesExecutiveId;
	}
	
	public static List<String> getSalesExecutiveIdsOrderByOrganization(Delegator delegator, GenericValue userLogin) {
		List<String> listSalesExecutiveId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.employee");
		List<String> salesExecutiveTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.sales.executive.order");
		List<String> customerTypeIds = getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(salesExecutiveTypeIds) || UtilValidate.isEmpty(customerTypeIds)) 
			return listSalesExecutiveId;
		if (UtilValidate.isNotEmpty(salesExecutiveTypeIds)) {
			List<String> partyIdsTmp = SalesPartyUtil.getPartiesByRoles(delegator, salesExecutiveTypeIds, true, userLogin);
			if (UtilValidate.isNotEmpty(partyIdsTmp)) {
				listSalesExecutiveId.addAll(partyIdsTmp);
			}
		}
		/* List<String> partyIdsTmp = FastList.newInstance();
		for (String roleItem : salesExecutiveTypeIds) {
			partyIdsTmp.clear();
			partyIdsTmp = SecurityUtil.getPartiesByRoles(roleItem, delegator);
			if (UtilValidate.isNotEmpty(partyIdsTmp)) {
				listSalesExecutiveId.addAll(partyIdsTmp);
			}
		}
		*/
		return listSalesExecutiveId;
	}
	
	public static boolean hasRole(Delegator delegator, String partyId, String roleTypeId) {
		boolean returnValue = false;
		if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeId)) return returnValue; 
		if (SecurityUtil.hasRole(roleTypeId, partyId, delegator)) {
			returnValue = true;
		}
		return returnValue;
	}
	public static boolean hasRole(Delegator delegator, String partyId, List<String> roleTypeIds) {
		boolean returnValue = false;
		if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeIds)) return returnValue; 
		for (String roleTypeId : roleTypeIds) {
			if (SecurityUtil.hasRole(roleTypeId, partyId, delegator)) {
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}
	public static boolean hasRole(Delegator delegator, String partyId, EmplRoleEnum roleTypeIdEnum){
		if (delegator == null || UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeIdEnum)) return false;
		String roleTypeId = null;
		roleTypeId = SalesUtil.getPropertyValue(delegator, roleTypeIdEnum.getValue());
		if (roleTypeId != null) {
			return SecurityUtil.hasRole(roleTypeId, partyId, delegator);
		}
		return false;
	}
	public static boolean hasParentRole(Delegator delegator, String partyId, EmplRoleEnum parentRoleTypeIdEnum){
		if (delegator == null || UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(parentRoleTypeIdEnum)) return false;
		List<String> roleTypeIds = new ArrayList<String>();
		String parentRoleTypeId = SalesUtil.getPropertyValue(delegator, parentRoleTypeIdEnum.getValue());
		if (UtilValidate.isNotEmpty(parentRoleTypeId)) {
			List<String> tmp = getDescendantRoleTypeIds(parentRoleTypeId, delegator);
			if (UtilValidate.isNotEmpty(tmp)) roleTypeIds.addAll(tmp);
		}
		if (roleTypeIds.size() > 0) {
			return hasRole(delegator, partyId, roleTypeIds);
		}
		return false;
	}
	
	public static String getOrgIdManagedByParty(Delegator delegator, String partyId) {
		List<String> tmp = getOrgIdsManagedByParty(delegator, partyId);
		if (UtilValidate.isNotEmpty(tmp)) {
			return tmp.get(0);
		}
		return null;
	}
	
	public static List<String> getOrgIdsManagedByParty(Delegator delegator, String partyId) {
		List<String> listOrgId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.manager");
		String roleManagerId = SalesUtil.getPropertyValue(delegator, "role.manager");
		String roleOrgId = SalesUtil.getPropertyValue(delegator, "role.org.to.employee");
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(roleManagerId) || UtilValidate.isEmpty(roleOrgId)) 
			return listOrgId;
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleManagerId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgId));
			listOrgId = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND),
							UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getListOrgManagedByParty method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return listOrgId;
	}
	public static List<String> getManagerIdOfDept(Delegator delegator, String deptId) {
		List<String> listOrgId = new ArrayList<String>();
		String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.manager");
		String roleManagerId = SalesUtil.getPropertyValue(delegator, "role.manager");
		String roleOrgId = SalesUtil.getPropertyValue(delegator, "role.org.to.employee");
		if (UtilValidate.isEmpty(partyRelationshipTypeId) || UtilValidate.isEmpty(roleManagerId) || UtilValidate.isEmpty(roleOrgId)) 
			return listOrgId;
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", deptId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleManagerId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgId));
			listOrgId = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), 
							UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		
			//Debug.log(module + "::getManagerIdOfDept, departmentId = " + deptId + ", list.sz = " + listOrgId.size());
			
			//for(String id: listOrgId){
			//	Debug.log(module + "::getManagerIdOfDept, departmentId = " + deptId + ", GOT " + id);	
			//}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getListOrgManagedByParty method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return listOrgId;
	}
	
	public static boolean isManagerOfOrg(Delegator delegator, String partyId, String roleDeptId) {
		List<String> deptIdsManaged = getOrgIdsManagedByParty(delegator, partyId);
		if (UtilValidate.isNotEmpty(deptIdsManaged)) {
			for (String deptIdManaged : deptIdsManaged) {
				if (hasRole(delegator, deptIdManaged, roleDeptId)) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isSalesManager(Delegator delegator, String partyId) {
		String roleDeptSalesId = SalesUtil.getPropertyValue(delegator, "role.department.id.sales");
		return isManagerOfOrg(delegator, partyId, roleDeptSalesId);
	}
	public static boolean isCallCenterManager(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.CALLCENTER_MANAGER);
	}
	public static boolean isCallCenter(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.CALLCENTER);
	}
	public static boolean isDistributor(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.DISTRIBUTOR);
	}
	public static boolean isEmployee(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.EMPLOYEE);
	}
	public static boolean isSalesAdmin(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.SALESADMIN);
	}
	public static boolean isSalesAdminManager(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.SALESADMIN_MANAGER);
	}
	public static boolean isSalesEmployee(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.SALES_EMPLOYEE);
	}
	public static boolean isSalesman(Delegator delegator, String partyId){
		List<String> roleTypeIds = SalesPartyUtil.getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, EmplRoleEnum.SALESMAN_EMPL.getValue()), delegator);
		return hasRole(delegator, partyId, roleTypeIds);
	}
	public static boolean isSalessup(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.SALESSUP_EMPL);
	}
	public static boolean isSalesCSM(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.CSM_EMPL);
	}
	public static boolean isSalesRSM(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.RSM_EMPL);
	}
	public static boolean isSalesASM(Delegator delegator, String partyId){
		return hasRole(delegator, partyId, EmplRoleEnum.ASM_EMPL);
	}
	public static boolean isLogSpecialist(Delegator delegator, String partyId){
		String roleIdEmpLogSpecialist = SalesUtil.getPropertyValue("role.log.specialist.empl");
		return SalesPartyUtil.hasRole(delegator, partyId, roleIdEmpLogSpecialist);
	}
	public static boolean isSalesCSMDept(Delegator delegator, String deptId) {
		return hasParentRole(delegator, deptId, EmplRoleEnum.CSM_DEPT);
	}
	public static boolean isSalesRSMDept(Delegator delegator, String deptId) {
		return hasParentRole(delegator, deptId, EmplRoleEnum.RSM_DEPT);
	}
	public static boolean isSalesASMDept(Delegator delegator, String deptId) {
		return hasParentRole(delegator, deptId, EmplRoleEnum.ASM_DEPT);
	}
	public static boolean isSalesSUPDept(Delegator delegator, String deptId) {
		return hasParentRole(delegator, deptId, EmplRoleEnum.SUP_DEPT);
	}
	
	public static List<String> getDistributorBySupervisor(Delegator delegator, String partySupervisorId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("supervisorId", partySupervisorId));
		conditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
		List<String> listDistributorPartyIds = EntityUtil.getFieldListFromEntityList(
				delegator.findList("PartyDistributor", EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		return listDistributorPartyIds;
	}
}
