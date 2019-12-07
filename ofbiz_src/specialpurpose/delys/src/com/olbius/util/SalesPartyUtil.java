package com.olbius.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.sales.SalesEmployeeEntity;

public class SalesPartyUtil {
	public static final String RESOURCE = "general";
	public static final String RESOURCE_DL = "delys.properties";
	public static String module = SalesPartyUtil.class.getName();
	public static final String RSN_ROLEGROUP_PRODSTORE = "role.type.group.product.store.group";
	public static final String RSN_PRTROLE_SALEADMIN = "party.role.sales.admin"; // DL = DELYS
	public static final String RSN_PRTROLE_SALEADMIN_DL = "party.role.sales.admin.delys"; // DL = DELYS
	public static final String RSN_PRTROLE_SALEADMIN_GT_DL = "party.role.sales.admin.gt.delys";
	public static final String RSN_PRTROLE_SALEADMIN_MT_DL = "party.role.sales.admin.mt.delys";
	public static final String RSN_PRTROLE_INTERNAL_ORG = "party.role.internal.org";
	public static final String RSN_PRTROLE_SALEADMIN_MANAGE = "party.role.sales.admin.manage";
	public static final String RSN_PRTRELTYPE_SALESEMP = "party.relationship.type.sales.employee";
	public static final String RSN_PRTRELTYPE_SALESEMM = "party.relationship.type.sales.employment";
	public static final String RSN_PRTRELTYPE_SALESMGR = "party.relationship.type.sales.manager";
	public static final String RSN_PRTRELTYPE_EMPLOYMENT = "party.relationship.type.employment";
	public static final String RSN_PRTRELTYPE_DEPARTMENT = "party.relationship.type.department";
	public static final String RSN_PRTRELTYPE_DISTRIBUTOR = "party.relationship.type.distributor";
	public static final String RSN_PRTRELTYPE_CUSTOMER = "party.relationship.type.customer";
	public static final String RSN_PRTRELTYPE_SALESROUTE = "party.relationship.type.sales.route";
	public static final String RSN_PRTRELTYPE_CUSTOMER_MT = "party.relationship.type.customer.mt";
	public static final String RSN_PRTROLE_CEO_DL = "party.role.ceo.delys";
	public static final String RSN_PRTROLE_NBD_DL = "party.role.nbd.delys";
	public static final String RSN_PRTROLE_CSM_DL = "party.role.csm.delys";
	public static final String RSN_PRTROLE_RSM_DL = "party.role.rsm.delys";
	public static final String RSN_PRTROLE_ASM_DL = "party.role.asm.delys";
	public static final String RSN_PRTROLE_SUP_DL = "party.role.sup.delys";
	public static final String RSN_PRTROLE_SUP_GT_DL = "party.role.sup.gt.delys";
	public static final String RSN_PRTROLE_SALESMAN_DL = "party.role.salesman.delys";
	public static final String RSN_PRTROLE_SALESMAN_GT_DL = "party.role.salesman.gt.delys";
	public static final String RSN_PRTROLE_PG = "party.role.pg";
	public static final String RSN_PRTROLE_EMPLOYEE = "party.role.employee";
	public static final String RSN_PRTROLE_DISTRIBUTOR = "party.role.distributor.delys";
	public static final String RSN_PRTROLE_ACCOUNTANT = "party.role.accountant.delys";
	public static final String RSN_PRTROLE_LOG_SPECIALIST = "party.role.log.specialist";
	public static final String RSN_PRTROLE_CUSTOMER_GT_DL = "party.role.customer.gt.delys";
	public static final String RSN_PRTROLE_CUSTOMER_DL = "party.role.customer.delys";
	public static final String RSN_PRTROLE_ROUTE_DL = "party.role.route.delys";
	public static final String RSN_PRTROLE_CUSTOMER_MT = "party.role.customer.mt";
	
	public static Set<String> getBusinessMenusCurrently(Delegator delegator, GenericValue userLogin){
		Set<String> businessMenus = new HashSet<String>();
		if (UtilValidate.isEmpty(userLogin.getString("partyId"))) return businessMenus;
		try {
			List<GenericValue> roleTypeList = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, true);
			if (roleTypeList != null) {
				for (GenericValue roleType : roleTypeList) {
					String roleTypeId = roleType.getString("roleTypeId");
					GenericValue roleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessMenu"), true);
					if (roleTypeAttr != null){
						businessMenus.add(roleTypeAttr.getString("attrValue"));
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e, module);
		}
		return businessMenus;
	}
	
	public static List<GenericValue> getListRoleTypeProductStoreRole(Delegator delegator) {
		String roleGroup = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_ROLEGROUP_PRODSTORE, delegator);
		if (UtilValidate.isEmpty(roleGroup)) return null;
		return getListRoleTypeByRoleTypeGroup(delegator, roleGroup);
	}
	
	public static List<GenericValue> getListRoleTypeByRoleTypeGroup(Delegator delegator, String roleTypeGroupId) {
		if (UtilValidate.isEmpty(roleTypeGroupId)) return null;
		List<GenericValue> result = new ArrayList<GenericValue>();
		List<String> listRoleTypeId = FastList.newInstance();
		try {
			List<GenericValue> listRoleTypeGroup = delegator.findByAnd("RoleTypeGroup", UtilMisc.toMap("roleTypeGroupTypeId", roleTypeGroupId), null, false);
			if (UtilValidate.isNotEmpty(listRoleTypeGroup)) {
				for (GenericValue roleTypeGroup : listRoleTypeGroup) {
					List<GenericValue> listRoleTypeGroupMember = EntityUtil.filterByDate(delegator.findByAnd("RoleTypeGroupMember", UtilMisc.toMap("roleTypeGroupId", roleTypeGroup.get("roleTypeGroupId")), null, false));
					if (UtilValidate.isNotEmpty(listRoleTypeGroupMember)) {
						List<String> listRtId = EntityUtil.getFieldListFromEntityList(listRoleTypeGroupMember, "roleTypeId", true);
						listRoleTypeId.addAll(listRtId);
					}
				}
			}
			if (UtilValidate.isNotEmpty(listRoleTypeId)) {
				List<EntityCondition> listCond = FastList.newInstance();
				listCond.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleTypeId));
				result = delegator.findList("RoleType", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getListRoleTypeByRoleTypeGroup method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return result;
	}
	
	public static boolean isDistributor(GenericValue userLogin, Delegator delegator) {
		if (UtilValidate.isEmpty(userLogin) || UtilValidate.isEmpty(userLogin.getString("partyId"))) return false;
		String roleIdDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		return SecurityUtil.hasRoleWithCurrentOrg(roleIdDistributor, userLogin.getString("partyId"), delegator);
	}
	public static boolean isDistributor(String partyId, Delegator delegator) {
		if (UtilValidate.isEmpty(partyId)) return false;
		String roleIdDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		return SecurityUtil.hasRoleWithCurrentOrg(roleIdDistributor, partyId, delegator);
	}
	/*String partyIdUserLogin = userLogin.getString("partyId");
	if (partyIdUserLogin != null && SecurityUtil.hasRoleWithCurrentOrg("DELYS_DISTRIBUTOR", partyIdUserLogin, delegator)) {
		List<GenericValue> listPartyRole = null;
		try {
			listPartyRole = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyIdUserLogin), null, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when check distributor role: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (UtilValidate.isNotEmpty(listPartyRole)) {
			List<String> roleTypeIds = EntityUtil.getFieldListFromEntityList(listPartyRole, "roleTypeId", true);
			if (roleTypeIds != null) {
				if (roleTypeIds.contains("DELYS_DISTRIBUTOR")) {
					// userLogin is distributor
					isDistributor = true;
				}
			}
		}
	}
	return isDistributor;*/
	
	// TODO
	public static boolean isLogStoreKeeper(GenericValue userLogin, Delegator delegator) throws Exception{
		if (UtilValidate.isEmpty(userLogin.get("partyId"))){
			return false;
		}
		GenericValue logistic = delegator.findOne("PartyRole", UtilMisc.toMap("roleTypeId", "LOG_STOREKEEPER", "partyId", userLogin.get("partyId")), false);
		if(UtilValidate.isNotEmpty(logistic)){
			return true;
		}
		return false;
	}
	
	// TODO
	public static boolean isLogSpecialist(GenericValue userLogin, Delegator delegator) throws Exception{
		if (UtilValidate.isEmpty(userLogin.get("partyId"))) {
			return false;
		}
		GenericValue logistic = delegator.findOne("PartyRole", UtilMisc.toMap("roleTypeId", "LOG_SPECIALIST", "partyId", userLogin.get("partyId")), false);
		if(UtilValidate.isNotEmpty(logistic)){
			return true;
		}
		return false;
	}
	
	public static boolean isAccEmployee(String roleTypeId, GenericValue userLogin, Delegator delegator) {
		boolean isSup = false;
		if (UtilValidate.isEmpty(userLogin)) return isSup;
		String partyIdUserLogin = userLogin.getString("partyId");
		if (partyIdUserLogin != null && SecurityUtil.hasRole("EMPLOYEE", partyIdUserLogin, delegator)) {
			List<EntityCondition> listConds = FastList.newInstance();
			List<String> rolesAccept = getListDescendantRoleInclude(roleTypeId, delegator);
			listConds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, rolesAccept));
			listConds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdUserLogin));
			try {
				List<GenericValue> listPartyRole = delegator.findList("PartyRole", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(listPartyRole)) {
					List<String> departments = getListDeptIdByEmployee(delegator, partyIdUserLogin);
					if (UtilValidate.isNotEmpty(departments)) {
						for (String department : departments) {
							GenericValue accRole = delegator.findOne("PartyRole", UtilMisc.<String, Object>toMap("partyId", department, "roleTypeId", "ACC_DEPARTMENT"), false);
							if (accRole != null && SecurityUtil.hasRole("ACC_DEPARTMENT", department, delegator)) {
								isSup = true;
								break;
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error when check accountant role: " + e.toString();
				Debug.logError(e, errMsg, module);
				return false;
			}
		}
		return isSup;
	}
	
	public static boolean isEmployeeCore(String roleTypeId, GenericValue userLogin, Delegator delegator) {
		boolean isEmp = false;
		if (UtilValidate.isEmpty(userLogin)) return isEmp;
		String partyIdUserLogin = userLogin.getString("partyId");
		if (partyIdUserLogin != null && SecurityUtil.hasRole("EMPLOYEE", partyIdUserLogin, delegator)) {
			List<String> departments = getListDeptIdByEmployee(delegator, partyIdUserLogin);
			if (UtilValidate.isNotEmpty(departments)) {
				List<String> rolesAccept = getListDescendantRoleInclude(roleTypeId, delegator);
				if (UtilValidate.isNotEmpty(rolesAccept)) {
					for (String department : departments) {
						for (String roleAccept : rolesAccept) {
							if (SecurityUtil.hasRole(roleAccept, department, delegator)) {
								isEmp = true;
								break;
							}
						}
						if (isEmp) break;
					}
				}
			}
		}
		return isEmp;
	}
	
	public static boolean isEmployeeCore(String roleTypeId, String partyId, Delegator delegator) {
		boolean isEmp = false;
		if (UtilValidate.isEmpty(partyId)) return isEmp;
		String partyIdUserLogin = partyId;
		if (partyIdUserLogin != null && SecurityUtil.hasRole("EMPLOYEE", partyIdUserLogin, delegator)) {
			List<String> departments = getListDeptIdByEmployee(delegator, partyIdUserLogin);
			if (UtilValidate.isNotEmpty(departments)) {
				List<String> rolesAccept = getListDescendantRoleInclude(roleTypeId, delegator);
				if (UtilValidate.isNotEmpty(rolesAccept)) {
					for (String department : departments) {
						for (String roleAccept : rolesAccept) {
							if (SecurityUtil.hasRole(roleAccept, department, delegator)) {
								isEmp = true;
								break;
							}
						}
						if (isEmp) break;
					}
				}
			}
		}
		return isEmp;
	}
	
	public static boolean isDeptCore(Delegator delegator, String partyId, String roleTypeId) {
		boolean isDept = false;
		if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeId)) return isDept;
		List<String> roleTypeIds = getListDescendantRoleInclude(roleTypeId, delegator);
		return isDeptCore(delegator, partyId, roleTypeIds);
	}
	public static boolean isDeptCore(Delegator delegator, String partyId, List<String> roleTypeIds) {
		boolean isDept = false;
		if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeIds)) return isDept;
		for (String roleId : roleTypeIds) {
			if (partyId != null && SecurityUtil.hasRole(roleId, partyId, delegator)) {
				return true;
			}
		}
		return isDept;
	}
	
	public static boolean isCeoEmployee(GenericValue userLogin, Delegator delegator) {
		if (UtilValidate.isEmpty(userLogin) || UtilValidate.isEmpty(userLogin.get("partyId"))) return false;
		String roleIdCeo = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CEO_DL, delegator);
		return SecurityUtil.hasRoleWithCurrentOrg(roleIdCeo, userLogin.getString("partyId"), delegator);
	}
	
	public static boolean isChiefAccoutantEmployee(GenericValue userLogin, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ACCOUNTANT, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isAccEmployee(roleId, userLogin, delegator);
	}
	
	public static boolean isSupervisorEmployee(GenericValue userLogin, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, userLogin, delegator);
	}
	
	public static boolean isSupervisorGTEmployee(GenericValue userLogin, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_GT_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, userLogin, delegator);
	}
	
	public static boolean isSupervisorMTEmployee(GenericValue userLogin, Delegator delegator) {
		return isEmployeeCore("DELYS_SALESSUP_MT", userLogin, delegator);
	}
	
	public static boolean isAsmEmployee(GenericValue userLogin, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, userLogin, delegator);
	}
	
	public static boolean isSalesAdminEmployee(GenericValue userLogin, Delegator delegator) {
		String employeeRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_DL, "", delegator);
		if (UtilValidate.isEmpty(employeeRole)) return false;
		return isEmployeeCore(employeeRole, userLogin, delegator);
	}
	public static boolean isSalesAdminGTEmployee(GenericValue userLogin, Delegator delegator) {
		String employeeRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_GT_DL, "", delegator);
		if (UtilValidate.isEmpty(employeeRole)) return false;
		return isEmployeeCore(employeeRole, userLogin, delegator);
	}
	public static boolean isSalesAdminMTEmployee(GenericValue userLogin, Delegator delegator) {
		String employeeRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_MT_DL, "", delegator);
		if (UtilValidate.isEmpty(employeeRole)) return false;
		return isEmployeeCore(employeeRole, userLogin, delegator);
	}
	
	public static boolean isSalesAdminManagerEmployee(GenericValue userLogin, Delegator delegator) {
		String roleIdSalesadminManager = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_MANAGE, delegator);
		if (UtilValidate.isEmpty(roleIdSalesadminManager)) return false;
		return isEmployeeCore(roleIdSalesadminManager, userLogin, delegator);
	}
	
	public static boolean isCsmEmployee(GenericValue userLogin, Delegator delegator) {
		String roleIdCsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdCsm)) return false;
		return isEmployeeCore(roleIdCsm, userLogin, delegator);
	}
	public static boolean isRsmEmployee(GenericValue userLogin, Delegator delegator) {
		String roleIdRsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdRsm)) return false;
		return isEmployeeCore(roleIdRsm, userLogin, delegator);
	}
	public static boolean isNbdEmployee(GenericValue userLogin, Delegator delegator) {
		String roleIdNbd = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_NBD_DL, delegator);
		if (UtilValidate.isEmail(roleIdNbd)) return false;
		return isEmployeeCore(roleIdNbd, userLogin, delegator);
	}
	
	// Check role by partyId
	
	public static boolean isSalesmanEmployee(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		boolean isSalesman = false;
		List<String> roleIds = getListDescendantRoleInclude(roleId, delegator);
		for (String item : roleIds) {
			isSalesman = SecurityUtil.hasRole(item, partyId, delegator);
			if (isSalesman) return isSalesman;
		}
		return isSalesman;
	}
	public static boolean isSupervisorEmployee(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, partyId, delegator);
	}
	public static boolean isSupervisorGTEmployee(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_GT_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, partyId, delegator);
	}
	public static boolean isSupervisorMTEmployee(String partyId, Delegator delegator) {
		return isEmployeeCore("DELYS_SALESSUP_MT", partyId, delegator);
	}
	public static boolean isAsmEmployee(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isEmployeeCore(roleId, partyId, delegator);
	}
	public static boolean isCsmEmployee(String partyId, Delegator delegator) {
		String roleIdCsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdCsm)) return false;
		return isEmployeeCore(roleIdCsm, partyId, delegator);
	}
	public static boolean isRsmEmployee(String partyId, Delegator delegator) {
		String roleIdRsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdRsm)) return false;
		return isEmployeeCore(roleIdRsm, partyId, delegator);
	}
	public static boolean isNbdEmployee(String partyId, Delegator delegator) {
		String roleIdNbd = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_NBD_DL, delegator);
		if (UtilValidate.isEmail(roleIdNbd)) return false;
		return isEmployeeCore(roleIdNbd, partyId, delegator);
	}
	
	public static boolean isSupervisorDept(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isDeptCore(delegator, partyId, roleId);
	}
	public static boolean isSupervisorGTDept(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_GT_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isDeptCore(delegator, partyId, roleId);
	}
	public static boolean isSupervisorMTDept(String partyId, Delegator delegator) {
		return isDeptCore(delegator, partyId, "DELYS_SALESSUP_MT");
	}
	public static boolean isAsmDept(String partyId, Delegator delegator) {
		String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, "", delegator);
		if (UtilValidate.isEmpty(roleId)) return false;
		return isDeptCore(delegator, partyId, roleId);
	}
	public static boolean isCsmDept(String partyId, Delegator delegator) {
		String roleIdCsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdCsm)) return false;
		return isDeptCore(delegator, partyId, roleIdCsm);
	}
	public static boolean isRsmDept(String partyId, Delegator delegator) {
		String roleIdRsm = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
		if (UtilValidate.isEmail(roleIdRsm)) return false;
		return isDeptCore(delegator, partyId, roleIdRsm);
	}
	public static boolean isNbdDept(String partyId, Delegator delegator) {
		String roleIdNbd = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_NBD_DL, delegator);
		if (UtilValidate.isEmail(roleIdNbd)) return false;
		return isDeptCore(delegator, partyId, roleIdNbd);
	}
	
	public static boolean hasContain(List<String> listChild, List<String> listParent) {
		boolean isContain = false;
		for (String child : listChild) {
			if (listParent.contains(child)) {
				isContain = true;
				break;
			}
		}
		return isContain;
	}
	
	public static EntityCondition makeConditionPartyIsActiving() {
		EntityCondition entityCond = null;
		
		List<EntityCondition> exprList = new ArrayList<EntityCondition>();
    	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
    	exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null));
    	List<EntityCondition> exprList2 = new ArrayList<EntityCondition>();
    	exprList2.add(EntityCondition.makeCondition(exprList, EntityOperator.AND));
    	exprList2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null));
    	entityCond = EntityCondition.makeCondition(exprList2, EntityOperator.OR);
    	
		return entityCond;
	}
	
	// ================================== STEPING ===================================================================================
	public static List<String> getListCompanyInProperties(Delegator delegator) {
		List<String> listCompany = FastList.newInstance();
		String companyId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		if (UtilValidate.isNotEmpty(companyId)) {
			listCompany.add(companyId);
		}
		return listCompany;
	}

	public static String getCompanyInProperties(Delegator delegator) {
		String companyId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "company", delegator);
		return companyId;
	}
	
	public static String getCurrencyInProperties(Delegator delegator) {
		String currencyId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
		return currencyId;
	}

	public static List<Map<String, Object>> getPaymentSalesPolicyPartyInProperties(Delegator delegator) {
		List<Map<String, Object>> listParty = FastList.newInstance();
		String partyIdsStr = EntityUtilProperties.getPropertyValue(RESOURCE_DL, "payment.sales.policy.party", "COMPANY", delegator);
		if (UtilValidate.isNotEmpty(partyIdsStr)) {
			String[] partyIds = partyIdsStr.split(";");
			if (UtilValidate.isNotEmpty(partyIds)) {
				for (String item : partyIds) {
					String[] itemStr = item.split("@");
					if (itemStr != null && itemStr.length > 0) {
						Map<String, Object> newItem = FastMap.newInstance();
						String paymentParty = itemStr.length > 0 ? itemStr[0] : "";
						String description = itemStr.length > 1 ? itemStr[1] : "";
						newItem.put("paymentParty", paymentParty);
						newItem.put("description", description);
						listParty.add(newItem);
					}
				}
			}
		}
		return listParty;
	}
	
	public static List<String> getListCustomTimePeriodSalesInProperties(Delegator delegator) {
		List<String> listPeriodType = FastList.newInstance();
		String periodTypeStr = EntityUtilProperties.getPropertyValue(RESOURCE_DL, "custom.time.period.sales", "", delegator);
		if (UtilValidate.isNotEmpty(periodTypeStr)) {
			String[] periodTypeIds = periodTypeStr.split(";");
			if (UtilValidate.isNotEmpty(periodTypeIds)) {
				listPeriodType = Arrays.asList(periodTypeIds);
			}
		}
		return listPeriodType;
	}

	/**
	 * TODO: unused deleted
	 * @param supervisorId
	 * @param listSortFields
	 * @param listAllConditions
	 * @param opts
	 * @param delegator
	 * @return
	 * @throws GenericEntityException
	 */
	public static EntityListIterator getListSalesmanActiveBySupervisor (String supervisorId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts, Delegator delegator) throws GenericEntityException {
		EntityListIterator listIterator = null;
    	EntityCondition condStatusPartyDisable = makeConditionPartyIsActiving();
//    	GenericValue userLogin = (GenericValue) delegator.find
    	if (UtilValidate.isNotEmpty(supervisorId)) {
    		Map<String, String> mapCondition2 = new HashMap<String, String>();
    		mapCondition2.put("partyIdFrom", supervisorId);
        	mapCondition2.put("partyRelationshipTypeId", "GROUP_ROLLUP");
        	mapCondition2.put("roleTypeIdTo", "EMPLOYEE");
        	EntityCondition tmpConditon2 = EntityCondition.makeCondition(mapCondition2);
        	List<EntityCondition> listConds2 = FastList.newInstance();
        	listConds2.add(condStatusPartyDisable);
        	listConds2.add(tmpConditon2);
        	listConds2.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, getListDescendantRoleInclude("DELYS_SALESMAN", delegator)));
        	listConds2.add(EntityUtil.getFilterByDateExpr());
        	if (UtilValidate.isNotEmpty(listAllConditions)) listConds2.addAll(listAllConditions);
        	
        	listIterator = delegator.find("PartyRoleNameDetailPartyRelTo", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, listSortFields, opts);
    	}
    	// <PartyRelationship partyIdFrom="SUP1_GT_HANOI" partyIdTo="salesman1" roleTypeIdFrom="INTERNAL_ORGANIZATIO" roleTypeIdTo="EMPLOYEE" 
    	// fromDate="2014-03-21 16:07:33.0" partyRelationshipTypeId="EMPLOYMENT"/>
    	return listIterator;
    }
	
	public static List<GenericValue> getListSalesmanPersonBySup(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		if (UtilValidate.isNotEmpty(partyId)) return null;
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		List<String> supIds = EntityUtil.getFieldListFromEntityList(getListManagerPersonByDept(delegator, partyId), "partyIdFrom", true);
		if (supIds != null) {
			for (String supId : supIds) {
				List<GenericValue> tmpList = getListSalesmanPersonBySupDept(delegator, supId, listSortFields, listAllConditions, opts);
				if (tmpList != null) returnValue.addAll(tmpList);
			}
		}
		return returnValue;
	}
	
	/**
	 * TODO need test important
	 * @param asmDepartmentIds
	 * @param listSortFields
	 * @param listAllConditions
	 * @param opts
	 * @param delegator
	 * @return
	 * @throws GenericEntityException
	 */
	public static EntityListIterator getListSupDepartmentActiveByAsmDepartment(List<String> asmDepartmentIds, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts, Delegator delegator) throws GenericEntityException {
		EntityListIterator listIterator = null;
    	EntityCondition condStatusPartyDisable = makeConditionPartyIsActiving();
    	
    	if (UtilValidate.isNotEmpty(asmDepartmentIds)) {
    		Map<String, String> mapCondition2 = new HashMap<String, String>();
        	mapCondition2.put("partyRelationshipTypeId", "GROUP_ROLLUP");
        	mapCondition2.put("roleTypeIdTo", "DELYS_SALESSUP_GT");
        	EntityCondition tmpConditon2 = EntityCondition.makeCondition(mapCondition2);
        	List<EntityCondition> listConds2 = FastList.newInstance();
        	listConds2.add(condStatusPartyDisable);
        	listConds2.add(tmpConditon2);
        	listConds2.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, asmDepartmentIds));
        	listConds2.add(EntityUtil.getFilterByDateExpr());
        	if (UtilValidate.isNotEmpty(listAllConditions)) listConds2.addAll(listAllConditions);
        	
        	listIterator = delegator.find("PartyRoleNameDetailPartyRelTo", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, listSortFields, opts);
    	}
    	// <PartyRelationship partyIdFrom="company" partyIdTo="NPP_TUANMINH" roleTypeIdFrom="INTERNAL_ORGANIZATIO" roleTypeIdTo="DELYS_DISTRIBUTOR" 
    	// fromDate="2014-03-21 16:07:33.0" partyRelationshipTypeId="GROUP_ROLLUP"/>
    	return listIterator;
    }
	
	
	// ================================== STEPING ===================================================================================
	/**
	 * TODO method have not check party is person yet
	 * @param delegator
	 * @return
	 * @throws Exception
	 */
	public static String getCeoPersonId(Delegator delegator) throws Exception{
		String ceoRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CEO_DL, delegator);
		List<String> listCeoId = SecurityUtil.getPartiesByRoles(ceoRoleId, delegator);
		if (listCeoId == null || listCeoId.isEmpty()) return null;
		return listCeoId.get(0);
	}
	/**
	 * TODO method have not check party is person yet
	 * @param delegator
	 * @return
	 * @throws Exception
	 */
	public static List<String> getListNbdPersonId(Delegator delegator) throws Exception{
		/*EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "DELYS_NBD");
		List<GenericValue> nbdList = delegator.findList("PartyPersonPartyRole", condition1, UtilMisc.toSet("partyId"), null, null, false);
		if(nbdList == null || nbdList.isEmpty()){
			return null;
		}
		return EntityUtil.getFieldListFromEntityList(nbdList, "partyId", true);*/
		String nbdRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_NBD_DL, delegator);
		return SecurityUtil.getPartiesByRoles(nbdRoleId, delegator, true);
	}
	public static List<String> getLogsSpecialist(Delegator delegator) throws Exception{
		/*List<String> listLogisticId = new ArrayList<String>();
		EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "LOG_SPECIALIST");
		List<String> listLogistic = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRole", condition1, UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		if(UtilValidate.isNotEmpty(listLogistic)){
			listLogisticId.addAll(listLogistic);
		}
		return listLogisticId;*/
		String logSpecialistRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_LOG_SPECIALIST, delegator);
		return SecurityUtil.getPartiesByRoles(logSpecialistRoleId, delegator);
	}
	public static List<String> getAccoutants(Delegator delegator) throws Exception{
		/*EntityCondition condition1 = EntityCondition.makeCondition("roleTypeId", "DELYS_ACCOUNTANTS");
		List<GenericValue> accList = delegator.findList("PartyRole", condition1, UtilMisc.toSet("partyId"), null, null, false);
		if(accList == null || accList.isEmpty()){
			return null;
		}
		return EntityUtil.getFieldListFromEntityList(accList, "partyId", true);*/
		String accRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ACCOUNTANT, delegator);
		return SecurityUtil.getPartiesByRoles(accRoleId, delegator);
	}
	
	// ================================== STANDARDIZE STEPING ===================================================================================
	/*
	 * TODO: unused
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getPartiesByRole(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String roleTypeId, boolean hasChild) {
		List<String> parties = new ArrayList<String>();
		List<String> listChildRoles = new ArrayList<String>();
		try {
			if (hasChild) {
				// get role type children of roleTypeId
				Map<String, Object> resultService = dispatcher.runSync("getChildRoleTypes", UtilMisc.toMap("roleTypeId", roleTypeId, "userLogin", userLogin));
				if (ServiceUtil.isSuccess(resultService)) {
					listChildRoles = (List<String>) resultService.get("childRoleTypeIdList");
				}
			} else {
				listChildRoles.add(roleTypeId);
			}
			for (String roleItem : listChildRoles) {
				EntityCondition condition = EntityCondition.makeCondition("roleTypeId", roleItem);
				List<GenericValue> listParty = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
				if (listParty != null) {
					List<String> listPartyId = EntityUtil.getFieldListFromEntityList(listParty, "partyId", true);
					for (String partyId : listPartyId) {
						if (!parties.contains(partyId)) {
							parties.add(partyId);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method getPartiesByRole GenericEntityException", module);
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error when running method getPartiesByRole GenericServiceException", module);
		}
		return parties;
	}
	public static List<String> getPartiesByRole(Delegator delegator, LocalDispatcher dispatcher, String roleTypeId, boolean hasDescendantRole) {
		List<String> parties = new ArrayList<String>();
		List<String> listChildRoles = new ArrayList<String>();
		if (hasDescendantRole) {
			listChildRoles = getListDescendantRoleInclude(roleTypeId, delegator);
		} else {
			listChildRoles.add(roleTypeId);
		}
		if (listChildRoles != null) {
			for (String roleIdItem : listChildRoles) {
				List<String> listPartyId = SecurityUtil.getPartiesByRoles(roleIdItem, delegator);
				if (listPartyId != null) {
					for (String partyId : listPartyId) {
						if (!parties.contains(partyId)) {
							parties.add(partyId);
						}
					}
				}
			}
		}
		return parties;
	}
	
	public static List<String> getListSAIdByProdStore(Delegator delegator, String productStoreId) {
		String salesAdminRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN, delegator);
		return getListPartyIdByProdStoreAndRole(delegator, productStoreId, salesAdminRoleId, true, true);
	}
	public static List<String> getListSAIdGTByProdStore(Delegator delegator, String productStoreId) {
		String salesAdminRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_GT_DL, delegator);
		return getListPartyIdByProdStoreAndRole(delegator, productStoreId, salesAdminRoleId, true, true);
	}
	public static List<String> getListSAIdMTByProdStore(Delegator delegator, String productStoreId) {
		String salesAdminRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_MT_DL, delegator);
		return getListPartyIdByProdStoreAndRole(delegator, productStoreId, salesAdminRoleId, true, true);
	}
	
	public static List<String> getListPartyIdByProdStoreAndRole(Delegator delegator, String productStoreId, String roleTypeId, boolean hasDescendantRole, boolean isActiving) {
		List<String> returnValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(productStoreId) || UtilValidate.isEmpty(roleTypeId)) return returnValue;
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			if (hasDescendantRole) {
				listConds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, getListDescendantRoleInclude(roleTypeId, delegator)));
			} else {
				listConds.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
			}
			if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
			returnValue = EntityUtil.getFieldListFromEntityList(
										delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listConds), null, null, null, false), 
										"partyId", true);
		} catch (GenericEntityException e) {
            Debug.logError(e, "Error when running method getListPartyIdByProdStoreAndRole", module);
        }
		return returnValue;
	}
	
	public static List<GenericValue> getListGVRoleMemberDescendantInGroup(String roleTypeGroupId, Delegator delegator) {
		List<GenericValue> returnValue = FastList.newInstance();
        try {
            List<GenericValue> roleTypeList = EntityUtil.filterByDate(delegator.findByAnd("RoleTypeGroupMember", UtilMisc.toMap("roleTypeGroupId", roleTypeGroupId), null, true));
            if (roleTypeList != null) {
            	for (GenericValue roleTypeMember : roleTypeList) {
                    String roleTypeId = roleTypeMember.getString("roleTypeId");
                    List<GenericValue> listTemp = getListGVDescendantRoleInclude(delegator, roleTypeId);
                    if (listTemp != null) {
                    	 returnValue.addAll(listTemp);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error when running method getListGVRoleMemberDescendantInGroup", module);
        }
        return returnValue;
    }
	/**
	 * Get list descendant role include this parameter roleTypeId
	 * @param roleTypeId The ancestor role type id
	 * @param delegator
	 * @return The list role type id contain this parameter roleTypeId and descendant of it
	 */
	public static List<String> getListDescendantRoleInclude(String roleTypeId, Delegator delegator) {
		List<String> listDescendantRole = new ArrayList<String>();
		try {
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);
			if (roleType != null) {
				listDescendantRole.add(roleType.getString("roleTypeId"));
				List<GenericValue> listChild = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", roleType.get("roleTypeId")), null, false);
				if (UtilValidate.isNotEmpty(listChild)) {
					for (GenericValue child : listChild) {
						List<String> resultList = getListDescendantRoleInclude(child.getString("roleTypeId"), delegator);
						if (UtilValidate.isNotEmpty(resultList)) listDescendantRole.addAll(resultList);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getListDescendantRoleInclude: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listDescendantRole;
	}
	public static List<GenericValue> getListGVDescendantRoleInclude(Delegator delegator, String roleTypeId) {
        List<GenericValue> returnValue = new ArrayList<GenericValue>();
		try {
			GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId), false);
        	if (roleType != null) {
        		returnValue.add(roleType);
        		List<GenericValue> listChild = delegator.findByAnd("RoleType", UtilMisc.toMap("parentTypeId", roleType.get("roleTypeId")), null, false);
    			if (UtilValidate.isNotEmpty(listChild)) {
    				for (GenericValue child : listChild) {
    					List<GenericValue> resultList = getListGVDescendantRoleInclude(delegator, child.getString("roleTypeId"));
    					if (UtilValidate.isNotEmpty(resultList)) returnValue.addAll(resultList);
    				}
    			}
        	}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getGVListDescendantRoleInclude: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return returnValue;
    }
	
	public static List<GenericValue> getListDescendantGeoIncludeDefaultCountry(Delegator delegator) {
		String countryGeoId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, "default.country.geo.id", "VNM", delegator);
		if (UtilValidate.isNotEmpty(countryGeoId)) {
			return getListDescendantGeoInclude(countryGeoId, delegator);
		}
		return null;
	}
	
	public static List<GenericValue> getListDescendantGeoInclude(String geoId, Delegator delegator) {
		List<GenericValue> listDescendantRole = new ArrayList<GenericValue>();
		Set<String> geoIdSet = FastSet.newInstance();
		try {
			if (geoId == null || geoIdSet.contains(geoId)) return null;
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			if (geo != null) {
				geoIdSet.add(geo.getString("geoId"));
				listDescendantRole.add(geo);
				
				List<GenericValue> listChild = delegator.findByAnd("GeoAssoc", UtilMisc.toMap("geoId", geo.get("geoId")), null, false);
				if (UtilValidate.isNotEmpty(listChild)) {
					for (GenericValue child : listChild) {
						List<GenericValue> resultList = getListDescendantGeoInclude(child.getString("geoIdTo"), delegator);
						if (UtilValidate.isNotEmpty(resultList)) listDescendantRole.addAll(resultList);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getAllSubGeo: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listDescendantRole;
	}
	
	public static List<GenericValue> getListAscendantPeriod(Delegator delegator, String customTimePeriodId) {
		if (UtilValidate.isEmpty(customTimePeriodId)) return null; 
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		EntityFindOptions posisFindOptions = new EntityFindOptions();
		posisFindOptions.setDistinct(true);
		List<GenericValue> listPeriod;
		try {
			listPeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId), null, null, posisFindOptions, false);
			if (listPeriod != null) {
				for (GenericValue period : listPeriod) {
					if (UtilValidate.isNotEmpty(period.getString("parentPeriodId"))) {
						List<GenericValue> tmpItems = getListAscendantPeriod(delegator, period.getString("parentPeriodId"));
						if (tmpItems != null) returnValue.addAll(tmpItems);
					} else {
						returnValue.add(period);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getListAscendantPeriod: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListAscendantPeriod(Delegator delegator, List<String> customTimePeriodIds) {
		if (UtilValidate.isEmpty(customTimePeriodIds)) return null; 
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		EntityFindOptions posisFindOptions = new EntityFindOptions();
		posisFindOptions.setDistinct(true);
		List<GenericValue> listPeriod;
		try {
			listPeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, customTimePeriodIds), null, null, posisFindOptions, false);
			if (listPeriod != null) {
				for (GenericValue period : listPeriod) {
					if (UtilValidate.isNotEmpty(period.getString("parentPeriodId"))) {
						List<GenericValue> tmpItems = getListAscendantPeriod(delegator, period.getString("parentPeriodId"));
						if (tmpItems != null) returnValue.addAll(tmpItems);
					} else {
						returnValue.add(period);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getListAscendantPeriod: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return returnValue;
	}
	
	// ================================== CONTINUE STEPING ===================================================================================
	/*@SuppressWarnings("unchecked")
	public static List<String> getListSupPersonBySA(Delegator delegator, String partyId) {
		List<String> returnValue = new ArrayList<String>();
		List<String> supDepts = getListSupDeptBySA(delegator, partyId);
		if (UtilValidate.isEmpty(supDepts)) return returnValue;
		supDepts = (List<String>) SetUtil.removeDuplicateElementInList(supDepts);
		String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		List<String> roleTypesSup = getListDescendantRoleInclude(supRoleId, delegator);
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, supDepts));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypesSup));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));
		
		try {
			returnValue = EntityUtil.getFieldListFromEntityList(
					EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false)), 
					"partyIdFrom", true);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListSupPersonBySA", module);
	    }
		return returnValue;
	}*/
	/*
	<PartyRelationship partyIdFrom="DE_0090" partyIdTo="SALES_ADMIN_GT" roleTypeIdFrom="MANAGER" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="MANAGER"/>
	<PartyRelationship partyIdFrom="DE_0090" partyIdTo="SALES_ADMIN_GT" roleTypeIdFrom="DELYS_SALESADMIN_GT" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="SALES_EMPLOYEE"/>
	<PartyRelationship partyIdFrom="SALESADMIN_MANAGER" partyIdTo="SALES_ADMIN_GT" roleTypeIdFrom="SALESADMIN_MANAGER" roleTypeIdTo="DELYS_SALESADMIN_GT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="DPM_SALES_ADMIN" partyIdTo="SALESADMIN_MANAGER" roleTypeIdFrom="DEPARTMENT" roleTypeIdTo="SALESADMIN_MANAGER" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>  
	<PartyRelationship partyIdFrom="NBD" partyIdTo="DPM_SALES_ADMIN" roleTypeIdFrom="DELYS_NBD" roleTypeIdTo="DELYS_ASSISTANT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>  
	<PartyRelationship partyIdFrom="DSA" partyIdTo="NBD" roleTypeIdFrom="SALES_DEPARTMENT" roleTypeIdTo="DELYS_NBD" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="BANGIAMDOC" partyIdTo="DSA" roleTypeIdFrom="DR_DEPARTMENT" roleTypeIdTo="SALES_DEPARTMENT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	
	<PartyRelationship partyIdFrom="DSA" partyIdTo="NBD" roleTypeIdFrom="SALES_DEPARTMENT" roleTypeIdTo="DELYS_NBD" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="NBD" partyIdTo="CSM_MT" roleTypeIdFrom="DELYS_NBD" roleTypeIdTo="DELYS_CSM_MT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="NBD" partyIdTo="DPM_SALES_ADMIN" roleTypeIdFrom="DELYS_NBD" roleTypeIdTo="DELYS_ASSISTANT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>  
	<PartyRelationship partyIdFrom="CSM_GT" partyIdTo="RSM_R1" roleTypeIdFrom="DELYS_CSM_GT" roleTypeIdTo="DELYS_RSM_GT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="RSM_R1" partyIdTo="ASM_R1A" roleTypeIdFrom="DELYS_RSM_GT" roleTypeIdTo="DELYS_ASM_GT" fromDate="" partyRelationshipTypeId="GROUP_ROLLUP"/>
	<PartyRelationship partyIdFrom="ASM_R2B" partyIdTo="SUP_GT10" roleTypeIdFrom="DELYS_ASM_GT" roleTypeIdTo="DELYS_SALESSUP_GT" partyRelationshipTypeId="GROUP_ROLLUP"/>
	
	<PartyRelationship partyIdFrom="DE_0114" partyIdTo="SUP_GT10" roleTypeIdFrom="DELYS_SALESSUP_GT" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="SALES_EMPLOYEE"/>
	<PartyRelationship partyIdFrom="salessup1" partyIdTo="SUP1_GT_HANOI" roleTypeIdFrom="MANAGER" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="MANAGER"/>
	<PartyRelationship partyIdFrom="DE_0305" partyIdTo="SUP_GT10" roleTypeIdFrom="DELYS_SALESMAN_GT" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="SALES_EMPLOYEE"/> 
	<PartyRelationship partyIdFrom="SUP_GT63" partyIdTo="NPP01A01" roleTypeIdFrom="DELYS_SALESSUP_GT" roleTypeIdTo="DELYS_DISTRIBUTOR" partyRelationshipTypeId="DISTRIBUTION"> </PartyRelationship>
	<PartyRelationship partyIdFrom="DE_0569" partyIdTo="SUP_GT63" roleTypeIdFrom="MANAGER" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="" partyRelationshipTypeId="MANAGER"/>
	<!-- Distributor - Customer -->
	<PartyRelationship partyIdFrom="NPP01A01" partyIdTo="R1B0100000" roleTypeIdFrom="DELYS_DISTRIBUTOR" roleTypeIdTo="DELYS_CUSTOMER_GT" fromDate="" partyRelationshipTypeId="CUSTOMER"/>
    <!-- Distributor - Salesman -->
	<PartyRelationship partyIdFrom="NPP01A01" partyIdTo="DE_0368" roleTypeIdFrom="DELYS_DISTRIBUTOR" roleTypeIdTo="DELYS_SALESMAN_GT" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="SALES_EMPLOYMENT"/>
	
    <PartyRelationship partyIdFrom="SUP1_GT_HANOI" partyIdTo="ROUTE1" roleTypeIdFrom="DELYS_SALESSUP_GT" roleTypeIdTo="DELYS_ROUTE" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="SALES_ROUTE"/>
	<PartyRelationship partyIdFrom="ROUTE1" partyIdTo="salesman1" roleTypeIdFrom="DELYS_ROUTE" roleTypeIdTo="DELYS_SALESMAN_GT" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="EMPLOYMENT"/>
    <PartyRelationship partyIdFrom="ROUTE1" partyIdTo="CUSTOMER1" roleTypeIdFrom="DELYS_ROUTE" roleTypeIdTo="DELYS_CUSTOMER_GT" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="SALES_ROUTE"/>
    
	String salesAdminManagerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_MANAGE, delegator);
	String asmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
	String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
	
	EntityCondition condStatusPartyDisable = makeConditionPartyIsActiving();
	
    <!-- Salesman - Route -->
    <PartyRelationship partyIdFrom="DE_0368" partyIdTo="ROUTE_10000" roleTypeIdFrom="DELYS_SALESMAN_GT" roleTypeIdTo="DELYS_ROUTE" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="SALES_ROUTE"/>
	<PartyRelationship partyIdFrom="ROUTE_10000" partyIdTo="R1B0100000" roleTypeIdFrom="DELYS_ROUTE" roleTypeIdTo="DELYS_CUSTOMER_GT" fromDate="2007-03-21 16:07:33.0" partyRelationshipTypeId="SALES_ROUTE"/>
	
	MT
	<PartyRelationship partyIdFrom="AA0001" partyIdTo="DE_0679" roleTypeIdFrom="CUSTOMER_MT" roleTypeIdTo="DELYS_SALESMAN_MT" fromDate="2015-07-11 00:00:00" partyRelationshipTypeId="SALES_EMPLOYMENT" />
    <PartyRelationship partyIdFrom="SUP_MT2" partyIdTo="AA0001" roleTypeIdFrom="DELYS_SALESSUP_MT" roleTypeIdTo="CUSTOMER_MT" fromDate="2015-07-11 00:00:00" partyRelationshipTypeId="CUSTOMER_REL" />
	
	*/
	
	public static EntityListIterator getIteratorDistributorByCustomer(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		if (UtilValidate.isEmpty(partyId)) return null;
		EntityListIterator returnValue = null;
		String roleIdDistributor = "DISTRIBUTOR";
		String roleIdCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String relIdCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(roleIdCustomer, delegator);
		
		returnValue = getCoreIteratorPersonFollowByParties(delegator, listAllConditions, listSortFields, opts, true, 
				null, partyId, roleIdDistributor, null, null, null, null, customerRoleIds, relIdCustomer, true);
		
		return returnValue;
	}
	
	public static String getSalesmanPersonIdByCustomer(Delegator delegator, String partyId) {
		String returnValue = null;
		String routeRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ROUTE_DL, delegator);
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String routeRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESROUTE, delegator);
		List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		List<String> listRouteIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, null, partyId, routeRoleId, null, null, null, null, customerRoleIds, routeRelId, true), 
				"partyIdFrom", true);
		if (UtilValidate.isNotEmpty(listRouteIds)) {
			List<String> listReturnValue = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, null, null, null, routeRoleId, null, listRouteIds, salesmanRoleIds, null, routeRelId, true), 
					"partyIdFrom", true);
			if (listReturnValue != null && !listReturnValue.isEmpty()) {
				returnValue = listReturnValue.get(0);
			}
		}
		return returnValue;
	}
	/**
	 * Get SM, PG = partyIdTo, role = roleTypeIdTo
	 * @param delegator
	 * @param partyId
	 * @return
	 */
	public static GenericValue getSalesmanOrPgPersonIdByCustomerMT(Delegator delegator, String partyId) {
		GenericValue returnValue = null;
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String pgRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_PG, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String salesRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMM, delegator);
		List<String> salesmanAndPgRoleIds = FastList.newInstance();
		List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
		List<String> pgRoleIds = getListDescendantRoleInclude(pgRoleId, delegator);
		if (salesmanRoleIds != null) salesmanAndPgRoleIds.addAll(salesmanRoleIds);
		if (pgRoleIds != null) salesmanAndPgRoleIds.addAll(pgRoleIds);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		List<GenericValue> listReturnValue = coreGetDeptByDept(delegator, partyId, null, null, null, null, null, customerRoleIds, salesmanAndPgRoleIds, salesRelId, true);
		if (listReturnValue != null && !listReturnValue.isEmpty()) {
			returnValue = listReturnValue.get(0);
		}
		return returnValue;
	}
	
	public static List<String> getListSalesmanPersonIdByCustomerMT(Delegator delegator, String partyId) {
		List<String> returnValue = null;
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String salesRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMM, delegator);
		List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		returnValue = EntityUtil.getFieldListFromEntityList(
				coreGetDeptByDept(delegator, partyId, null, null, null, null, null, customerRoleIds, salesmanRoleIds, salesRelId, true), 
				"partyIdTo", true);
		return returnValue;
	}
	
	/**
	 * sup id = partyIdFrom, role = roleTypeIdFrom
	 * @param delegator
	 * @param partyId
	 * @return
	 */
	public static GenericValue getSupPersonIdByCustomerMT(Delegator delegator, String partyId) {
		GenericValue returnValue = null;
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String customerRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER_MT, delegator);
		List<String> supRoleIds = getListDescendantRoleInclude(supRoleId, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		List<String> listSupDeptId = EntityUtil.getFieldListFromEntityList(
				coreGetDeptByDept(delegator, null, partyId, null, null, null, null, supRoleIds, customerRoleIds, customerRelId, true), 
				"partyIdFrom", true);
		if (UtilValidate.isNotEmpty(listSupDeptId)) {
			List<String> returnValueIds = EntityUtil.getFieldListFromEntityList(getListManagerPersonByDept(delegator, listSupDeptId), "partyId", true);
			if (returnValueIds != null && !returnValueIds.isEmpty()) {
				String interalOrg = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
				String salesRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
				List<GenericValue> listR2 = coreGetDeptByDept(delegator, null, null, null, interalOrg, returnValueIds, listSupDeptId, null, null, salesRelId, true);
				if (UtilValidate.isNotEmpty(listR2)) returnValue = listR2.get(0);
			}
		}
		return returnValue;
	}
	
	public static GenericValue getSalesmanPersonByCustomer(Delegator delegator, String partyId) {
		GenericValue returnValue = null;
		String routeRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ROUTE_DL, delegator);
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String routeRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESROUTE, delegator);
		List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		List<String> listRouteIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, null, partyId, routeRoleId, null, null, null, null, customerRoleIds, routeRelId, true), 
				"partyIdFrom", true);
		if (UtilValidate.isNotEmpty(listRouteIds)) {
			returnValue = EntityUtil.getFirst(getCorePersonFollowByParties(delegator, null, null, null, true, null, null, null, routeRoleId, null, listRouteIds, salesmanRoleIds, null, routeRelId, true));
		}
		return returnValue;
	}
	
	public static EntityListIterator getIteratorDistributorBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		List<String> listSupDeptId = getListDeptIdByManager(delegator, partyId);
		if (listSupDeptId != null) {
			String roleIdSup = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
			String roleIdDis = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
			String partyRelDis = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
			List<String> roleIdsSup = getListDescendantRoleInclude(roleIdSup, delegator);
			returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					null, null, null, roleIdDis, listSupDeptId, null, roleIdsSup, null, partyRelDis, true);
		}
		return returnValue;
	}
	
	public static List<String> getListDistributorIdBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		List<String> returnValue = null;
		List<String> listSupDeptId = getListDeptIdByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(listSupDeptId)) {
			String roleIdSup = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
			String roleIdDis = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
			String partyRelDis = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
			List<String> roleIdsSup = getListDescendantRoleInclude(roleIdSup, delegator);
			returnValue = EntityUtil.getFieldListFromEntityList(getCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					null, null, null, roleIdDis, listSupDeptId, null, roleIdsSup, null, partyRelDis, true), "partyId", true);
		}
		return returnValue;
	}
	
	public static List<String> getListCustomerMTIdBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		List<String> returnValue = null;
		List<String> listSupDeptId = getListDeptIdByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(listSupDeptId)) {
			String roleIdSup = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
			//String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_MT, delegator);
			String partyRel = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER_MT, delegator);
			List<String> roleIdsSup = getListDescendantRoleInclude(roleIdSup, delegator);
			returnValue = EntityUtil.getFieldListFromEntityList(getCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					null, null, null, null, listSupDeptId, null, roleIdsSup, null, partyRel, true), "partyId", true);
		}
		return returnValue;
	}
	public static EntityListIterator getIteratorCustomerDirectBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		List<String> listSupDeptId = getListDeptIdByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(listSupDeptId)) {
			String roleIdSup = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
			//String roleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_MT, delegator);
			String partyRel = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER_MT, delegator);
			List<String> roleIdsSup = getListDescendantRoleInclude(roleIdSup, delegator);
			if (UtilValidate.isNotEmpty(roleIdsSup)) returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
																null, null, null, null, listSupDeptId, null, roleIdsSup, null, partyRel, true);
		}
		return returnValue;
	}
	
	/**
	 * Lay ra danh sach nha phan phoi va tat ca cac khach hang truc tiep cua cong ty (MT, horeca, ...)
	 * @param delegator
	 * @param partyId
	 * @param listAllConditions
	 * @param listSortFields
	 * @param opts
	 * @return
	 */
	public static List<String> getListDistOrCustomerDirectIdBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		List<String> returnValue = new ArrayList<String>();
		List<String> tmp1 = getListDistributorIdBySup(delegator, partyId, listAllConditions, listSortFields, opts);
		List<String> tmp2 = getListCustomerMTIdBySup(delegator, partyId, listAllConditions, listSortFields, opts);
		if (tmp1 != null) returnValue.addAll(tmp1);
		if (tmp2 != null) returnValue.addAll(tmp2);
		return returnValue;
	}
	
	public static EntityListIterator getIteratorDistributorAll(Delegator delegator, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		String internalRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String distributorRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		String partyRelDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
		String companyId = getCompanyInProperties(delegator);
		if (UtilValidate.isNotEmpty(companyId)) {
			returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					companyId, null, internalRoleId, distributorRoleId, null, null, null, null, partyRelDistributor, true);
		}
		return returnValue;
	}
	
	public static EntityListIterator getIteratorCustomerBySalesman(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		String routeRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ROUTE_DL, delegator);
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
		String routeRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESROUTE, delegator);
		List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
		List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
		List<String> listRouteIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, partyId, null, null, routeRoleId, null, null, salesmanRoleIds, null, routeRelId, true), 
				"partyIdTo", true);
		if (UtilValidate.isNotEmpty(listRouteIds)) {
			returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					null, null, routeRoleId, null, listRouteIds, null, null, customerRoleIds, routeRelId, true);
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListCustomerBySup(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String partyRelDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String disRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		
		List<GenericValue> listSupDept = getListDeptByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(listSupDept)) {
			for (GenericValue supDept : listSupDept) {
				List<GenericValue> listDistributor = coreGetDeptByDept(delegator, supDept.getString("partyId"), null, null, disRoleTypeId, null, null, supRoleTypeIds, null, partyRelDistributor, true);
				if (listDistributor != null) {
					for (GenericValue disItem : listDistributor) {
						List<GenericValue> tmpList = getListCustomerByDis(delegator, disItem.getString("partyId"), listAllConditions, listSortFields, opts);
						if (tmpList != null) returnValue.addAll(tmpList);
					}
				}
			}
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListCustomerBySupDept(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String partyRelDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String disRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		
		List<GenericValue> listDistributor = coreGetDeptByDept(delegator, partyId, null, null, disRoleTypeId, null, null, supRoleTypeIds, null, partyRelDistributor, true);
		if (UtilValidate.isNotEmpty(listDistributor)) {
			for (GenericValue disItem : listDistributor) {
				List<GenericValue> tmpList = getListCustomerByDis(delegator, disItem.getString("partyIdTo"), listAllConditions, listSortFields, opts);
				if (tmpList != null) returnValue.addAll(tmpList);
			}
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListCustomerByDis(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_GT_DL, delegator);
		String disRoleTypeId = "DISTRIBUTOR";
		String partyRelCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER, delegator);
		return getCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, partyId, null, disRoleTypeId, customerRoleId, 
				null, null, null, null, partyRelCustomer, true);
	}

	public static List<GenericValue> getListCustomerByDis(Delegator delegator, String partyId) {
		String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_GT_DL, delegator);
		String disRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		String partyRelCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER, delegator);
		return coreGetDeptByDept(delegator, partyId, null, disRoleTypeId, customerRoleId, null, null, null, null, partyRelCustomer, true);
	}
	
	public static EntityListIterator getIteratorSalesmanPersonByCsm(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
    		List<GenericValue> listCsmDept = getListDeptByEmployee(delegator, partyId);
    		if (UtilValidate.isNotEmpty(listCsmDept)) {
    			List<String> csmDeptIds = EntityUtil.getFieldListFromEntityList(listCsmDept, "partyIdFrom", true);
    			
    			String csmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
    			String rsmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
    			String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
    			List<String> csmRoleTypeIds = getListDescendantRoleInclude(csmRoleTypeId, delegator);
    			List<String> rsmRoleTypeIds = getListDescendantRoleInclude(rsmRoleTypeId, delegator);
    			List<GenericValue> listRelCsmToRsm = coreGetDeptByDept(delegator, null, null, null, null, csmDeptIds, null, csmRoleTypeIds, rsmRoleTypeIds, partyRelDepartment, true);
    			if (UtilValidate.isNotEmpty(listRelCsmToRsm)) {
    				List<String> rsmDeptIds = EntityUtil.getFieldListFromEntityList(listRelCsmToRsm, "partyIdTo", true);
    				listIterator = getIteratorSalesmanPersonByRsmDept(delegator, partyId, listSortFields, listAllConditions, opts, rsmDeptIds);
    			}
    		}
    	}
    	return listIterator;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonByRsmDept(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts,
			List<String> rsmDeptIds) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(rsmDeptIds)) {
			String rsmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
			String asmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
			String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
			List<String> rsmRoleTypeIds = getListDescendantRoleInclude(rsmRoleTypeId, delegator);
			List<String> asmRoleTypeIds = getListDescendantRoleInclude(asmRoleTypeId, delegator);
			List<GenericValue> listRelRsmToAsm = coreGetDeptByDept(delegator, null, null, null, null, rsmDeptIds, null, rsmRoleTypeIds, asmRoleTypeIds, partyRelDepartment, true);
			if (UtilValidate.isNotEmpty(listRelRsmToAsm)) {
				List<String> asmDeptIds = EntityUtil.getFieldListFromEntityList(listRelRsmToAsm, "partyIdTo", true);
				listIterator = getIteratorSalesmanPersonByAsmDept(delegator, partyId, listSortFields, listAllConditions, opts, asmDeptIds);
			}
    	}
    	return listIterator;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonByRsm(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
    		List<GenericValue> listRsmDept = getListDeptByEmployee(delegator, partyId);
    		if (UtilValidate.isNotEmpty(listRsmDept)) {
    			List<String> rsmDeptIds = EntityUtil.getFieldListFromEntityList(listRsmDept, "partyIdFrom", true);
				listIterator = getIteratorSalesmanPersonByRsmDept(delegator, partyId, listSortFields, listAllConditions, opts, rsmDeptIds);
    		}
    	}
    	return listIterator;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonByAsmDept(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts,
			List<String> asmDeptIds) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(asmDeptIds)) {
			String asmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
			String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
			String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
			List<String> asmRoleTypeIds = getListDescendantRoleInclude(asmRoleTypeId, delegator);
			List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
			List<GenericValue> listRel = coreGetDeptByDept(delegator, null, null, null, null, asmDeptIds, null, asmRoleTypeIds, supRoleTypeIds, partyRelDepartment, true);
			if (UtilValidate.isNotEmpty(listRel)) {
				List<String> supDeptIds = EntityUtil.getFieldListFromEntityList(listRel, "partyIdTo", true);
				String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
    			List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
    			listIterator = getIteratorCoreEmpPersonFollowOneDept(delegator, listSortFields, opts, supDeptIds, salesmanRoleIds);
			}
    	}
    	return listIterator;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonByAsm(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
    		List<GenericValue> listAsmDept = getListDeptByEmployee(delegator, partyId);
    		if (UtilValidate.isNotEmpty(listAsmDept)) {
    			List<String> asmDeptIds = EntityUtil.getFieldListFromEntityList(listAsmDept, "partyIdFrom", true);
    			listIterator = getIteratorSalesmanPersonByAsmDept(delegator, partyId, listSortFields, listAllConditions, opts, asmDeptIds);
    		}
    	}
    	return listIterator;
    }
	
	public static List<String> getListSupDeptIdByAsmDept(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = null;
		String asmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<String> asmRoleTypeIds = getListDescendantRoleInclude(asmRoleTypeId, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		List<GenericValue> listRel = coreGetDeptByDept(delegator, partyId, null, null, null, null, null, asmRoleTypeIds, supRoleTypeIds, partyRelDepartment, true);
		if (UtilValidate.isNotEmpty(listRel)) {
			returnValue = EntityUtil.getFieldListFromEntityList(listRel, "partyIdTo", true);
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByAsmDept(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = null;
		String asmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<String> asmRoleTypeIds = getListDescendantRoleInclude(asmRoleTypeId, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		List<GenericValue> listRel = coreGetDeptByDept(delegator, partyId, null, null, null, null, null, asmRoleTypeIds, supRoleTypeIds, partyRelDepartment, true);
		if (UtilValidate.isNotEmpty(listRel)) {
			List<String> supDeptIds = EntityUtil.getFieldListFromEntityList(listRel, "partyIdTo", true);
			returnValue = EntityUtil.getFieldListFromEntityList(getListManagerPersonByDept(delegator, supDeptIds), "partyId", true);
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByAsm(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		List<String> asmDeptIds = getListDeptIdByManager(delegator, partyId);
		if (asmDeptIds != null) {
			for (String asmItem : asmDeptIds) {
				List<String> tmpSupPerson = getListSupPersonIdByAsmDept(delegator, asmItem);
				if (tmpSupPerson != null) returnValue.addAll(tmpSupPerson);
			}
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByRsmDept(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		String rsmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
		String asmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<String> rsmRoleTypeIds = getListDescendantRoleInclude(rsmRoleTypeId, delegator);
		List<String> asmRoleTypeIds = getListDescendantRoleInclude(asmRoleTypeId, delegator);
		List<String> asmDeptIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, partyId, null, null, null, null, null, rsmRoleTypeIds, asmRoleTypeIds, partyRelDepartment, true), 
				"partyIdTo", true);
		if (asmDeptIds != null) {
			for (String asmDeptId : asmDeptIds) {
				List<String> tmpSupIds = getListSupPersonIdByAsmDept(delegator, asmDeptId);
				if (tmpSupIds != null) returnValue.addAll(tmpSupIds);
			}
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByRsm(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		List<String> rsmDeptIds = getListDeptIdByManager(delegator, partyId);
		if (rsmDeptIds != null) {
			for (String rsmItem : rsmDeptIds) {
				List<String> tmpSupPerson = getListSupPersonIdByRsmDept(delegator, rsmItem);
				if (tmpSupPerson != null) returnValue.addAll(tmpSupPerson);
			}
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByCsmDept(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		String rsmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_RSM_DL, delegator);
		String csmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<String> rsmRoleTypeIds = getListDescendantRoleInclude(rsmRoleTypeId, delegator);
		List<String> csmRoleTypeIds = getListDescendantRoleInclude(csmRoleTypeId, delegator);
		List<String> rsmDeptIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, partyId, null, null, null, null, null, csmRoleTypeIds, rsmRoleTypeIds, partyRelDepartment, true), 
				"partyIdTo", true);
		if (rsmDeptIds != null) {
			for (String rsmDeptId : rsmDeptIds) {
				List<String> tmpSupIds = getListSupPersonIdByRsmDept(delegator, rsmDeptId);
				if (tmpSupIds != null) returnValue.addAll(tmpSupIds);
			}
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByCsm(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		List<String> csmDeptIds = getListDeptIdByManager(delegator, partyId);
		if (csmDeptIds != null) {
			for (String csmItem : csmDeptIds) {
				List<String> tmpSupPerson = getListSupPersonIdByCsmDept(delegator, csmItem);
				if (tmpSupPerson != null) returnValue.addAll(tmpSupPerson);
			}
		}
    	return returnValue;
    }

	public static List<String> getListSupPersonIdByNbdDept(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		String nbdRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_NBD_DL, delegator);
		String csmRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CSM_DL, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<String> nbdRoleTypeIds = getListDescendantRoleInclude(nbdRoleTypeId, delegator);
		List<String> csmRoleTypeIds = getListDescendantRoleInclude(csmRoleTypeId, delegator);
		List<String> csmDeptIds = EntityUtil.getFieldListFromEntityList(coreGetDeptByDept(delegator, partyId, null, null, null, null, null, nbdRoleTypeIds, csmRoleTypeIds, partyRelDepartment, true), 
				"partyIdTo", true);
		if (csmDeptIds != null) {
			for (String csmDeptId : csmDeptIds) {
				List<String> tmpSupIds = getListSupPersonIdByCsmDept(delegator, csmDeptId);
				if (tmpSupIds != null) returnValue.addAll(tmpSupIds);
			}
		}
    	return returnValue;
    }
	
	public static List<String> getListSupPersonIdByNbd(Delegator delegator, String partyId) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<String> returnValue = FastList.newInstance();
		List<String> nbdDeptIds = getListDeptIdByManager(delegator, partyId);
		if (nbdDeptIds != null) {
			for (String nbdItem : nbdDeptIds) {
				List<String> tmpSupPerson = getListSupPersonIdByNbdDept(delegator, nbdItem);
				if (tmpSupPerson != null) returnValue.addAll(tmpSupPerson);
			}
		}
    	return returnValue;
    }
	
	public static List<GenericValue> getListCustomerByAsmDept(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		List<String> supDeptIds = getListSupDeptIdByAsmDept(delegator, partyId);
		if (UtilValidate.isNotEmpty(supDeptIds)) {
			for (String supDeptId : supDeptIds) {
				List<GenericValue> tmpList = getListCustomerBySupDept(delegator, supDeptId, listAllConditions, listSortFields, opts);
				if (tmpList != null) returnValue.addAll(tmpList);
			}
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListCustomerByAsm(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		if (UtilValidate.isEmpty(partyId)) return null;
		List<GenericValue> returnValue = null;
		List<String> asmDeptIds = EntityUtil.getFieldListFromEntityList(getListDeptByManager(delegator, partyId), "partyId", true);
		if (UtilValidate.isNotEmpty(asmDeptIds)) {
			returnValue = getListCustomerByAsmDept(delegator, asmDeptIds.get(0), listSortFields, listAllConditions, opts);
		}
		return returnValue;
	}
	
	public static List<GenericValue> coreGetDeptByDept(Delegator delegator, String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, 
			List<String> partyIdFroms, List<String> partyIdTos, List<String> roleTypeIdFroms, List<String> roleTypeIdTos, String partyRelationshipTypeId, boolean isActiving) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		List<EntityCondition> listConds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(partyIdFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		if (UtilValidate.isNotEmpty(partyIdTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		if (UtilValidate.isNotEmpty(roleTypeIdFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if (UtilValidate.isNotEmpty(partyIdFroms)) listConds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIdFroms));
		if (UtilValidate.isNotEmpty(partyIdTos)) listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdTos));
		if (UtilValidate.isNotEmpty(roleTypeIdFroms)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdFroms));
		if (UtilValidate.isNotEmpty(roleTypeIdTos)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleTypeIdTos));
		if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
		if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			returnValue = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method coreGetDeptByDept", module);
	    }
		return returnValue;
	}
	
	public static EntityListIterator getIteratorSalesmanPersonBySup(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
    		List<GenericValue> listDept = getListDeptByEmployee(delegator, partyId);
    		if (UtilValidate.isNotEmpty(listDept)) {
    			String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
    			List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
    			List<String> deptIds = EntityUtil.getFieldListFromEntityList(listDept, "partyIdFrom", true);
    			listIterator = getIteratorCoreEmpPersonFollowOneDept(delegator, listSortFields, opts, deptIds, salesmanRoleIds);
    		}
    	}
    	return listIterator;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonBySupDept(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator listIterator = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
			String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
			List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
			List<String> deptIds = UtilMisc.<String>toList(partyId);
			listIterator = getIteratorCoreEmpPersonFollowOneDept(delegator, listSortFields, opts, deptIds, salesmanRoleIds);
    	}
    	return listIterator;
    }
	public static List<GenericValue> getListSalesmanPersonBySupDept(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		List<GenericValue> returnValue = null;
    	if (UtilValidate.isNotEmpty(partyId)) {
			String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
			List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
			List<String> deptIds = UtilMisc.<String>toList(partyId);
			returnValue = getCoreEmpPersonFollowOneDept(delegator, listSortFields, opts, deptIds, salesmanRoleIds);
    	}
    	return returnValue;
    }
	
	public static EntityListIterator getIteratorSalesmanPersonByDis(Delegator delegator, String partyId, List<String> listSortFields, List<EntityCondition> listAllConditions, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		if (UtilValidate.isNotEmpty(partyId)) {
			String disRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
			String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
			String salesEmploymentRelId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMM, delegator);
			List<String> salesmanRoleIds = getListDescendantRoleInclude(salesmanRoleId, delegator);
			returnValue = getCoreIteratorPersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					partyId, null, disRoleId, null, null, null, null, salesmanRoleIds, salesEmploymentRelId, true);
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListSupDeptBySalesman(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		List<String> roleTypes = getListDescendantRoleInclude(salesmanRoleId, delegator);
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypes));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));
		listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			returnValue = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListSupDeptBySalesman", module);
	    }
		return returnValue;
	}
	
	/**
	 * TODO need rewrite, get by SUP - NPP - CUSTOMER thread
	 * today get by SUP - NVBH - Customer
	 * @param delegator
	 * @param partyId
	 * @return
	 */
	public static List<GenericValue> getListSupPersonBySalesman(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		List<GenericValue> listSupDept = getListSupDeptBySalesman(delegator, partyId);
		if (UtilValidate.isNotEmpty(listSupDept)) {
			for (GenericValue supDept : listSupDept) {
				String partyIdTo = supDept.getString("partyIdTo");
				List<GenericValue> listSupManager = getListManagerPersonByDept(delegator, partyIdTo);
				if (listSupManager != null) returnValue.addAll(listSupManager);
			}
		}
		return returnValue;
	}
	
	public static String getSupPersonIdBySalesman(Delegator delegator, String partyId) {
		String returnValue = null;
		GenericValue supFirst = EntityUtil.getFirst(getListSupPersonBySalesman(delegator, partyId));
		if (supFirst != null) {
			returnValue = supFirst.getString("partyId");
		}
		return returnValue;
	}
	
	public static List<GenericValue> getListSupPersonByDistributor(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		if (UtilValidate.isEmpty(partyId)) return returnValue;
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		String disRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		List<GenericValue> listSupDept = coreGetDeptByDept(delegator, null, partyId, null, disRoleTypeId, null, null, supRoleTypeIds, null, partyRelDepartment, true);
		if (UtilValidate.isNotEmpty(listSupDept)) {
			for (GenericValue supDept : listSupDept) {
				String partyIdTo = supDept.getString("partyIdFrom");
				List<GenericValue> listSupManager = getListManagerPersonByDept(delegator, partyIdTo);
				if (listSupManager != null) returnValue.addAll(listSupManager);
			}
		}
		return returnValue;
	}
	
	public static List<String> getListSupPersonIdByDistributor(Delegator delegator, String partyId){
		return EntityUtil.getFieldListFromEntityList(getListSupPersonByDistributor(delegator, partyId), "partyId", true);
	}
	
	public static List<GenericValue> getListSupPersonByCustomerDirect(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		if (UtilValidate.isEmpty(partyId)) return returnValue;
		String supRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		//String roleIdCustMT = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_MT, delegator);
		String partyRelDepartment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER_MT, delegator);
		List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleTypeId, delegator);
		List<GenericValue> listSupDept = coreGetDeptByDept(delegator, null, partyId, null, null, null, null, supRoleTypeIds, null, partyRelDepartment, true);
		if (UtilValidate.isNotEmpty(listSupDept)) {
			for (GenericValue supDept : listSupDept) {
				String partyIdTo = supDept.getString("partyIdFrom");
				List<GenericValue> listSupManager = getListManagerPersonByDept(delegator, partyIdTo);
				if (listSupManager != null) returnValue.addAll(listSupManager);
			}
		}
		return returnValue;
	}
	
	public static List<String> getListSupPersonIdByCustomerDirect(Delegator delegator, String partyId){
		return EntityUtil.getFieldListFromEntityList(getListSupPersonByCustomerDirect(delegator, partyId), "partyId", true);
	}
	
	public static List<GenericValue> getListAsmPersonBySupDept(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String asmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		List<String> asmRoleTypes = getListDescendantRoleInclude(asmRoleId, delegator);
		List<String> supRoleTypes = getListDescendantRoleInclude(supRoleId, delegator);
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdTo", partyId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, asmRoleTypes));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, supRoleTypes));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> listAsmDept = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
			if (UtilValidate.isNotEmpty(listAsmDept)) {
				for (GenericValue asmDept : listAsmDept) {
					String partyIdFrom = asmDept.getString("partyIdFrom");
					List<GenericValue> listSupManager = getListManagerPersonByDept(delegator, partyIdFrom);
					if (listSupManager != null) returnValue.addAll(listSupManager);
				}
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListAsmPersonBySupDept", module);
	    }
		return returnValue;
	}
	
	public static List<String> getListAsmDeptBySA(Delegator delegator, String partyId) {
		List<String> returnValue = new ArrayList<String>();
		String asmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_ASM_DL, delegator);
		if (UtilValidate.isEmpty(asmRoleId)) return returnValue;
		try {
			returnValue = getListDeptXByRoleXAndSaId(delegator, partyId, asmRoleId);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListAsmDeptBySA", module);
	    }
		
		return returnValue;
	}
	public static List<String> getListSupDeptBySA(Delegator delegator, String partyId) {
		List<String> returnValue = new ArrayList<String>();
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		if (UtilValidate.isEmpty(supRoleId)) return returnValue;
		try {
			returnValue = getListDeptXByRoleXAndSaId(delegator, partyId, supRoleId);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListSupDeptBySA", module);
	    }
		
		return returnValue;
	}
	
	public static EntityListIterator getIteratorSupDeptBySA(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		if (UtilValidate.isEmpty(supRoleId)) return returnValue;
		try {
			List<String> supDeptIds = getListDeptXByRoleXAndSaId(delegator, partyId, supRoleId);
			if (supDeptIds != null) {
				List<EntityCondition> listCond = FastList.newInstance();
				if (listAllConditions != null) listCond.addAll(listAllConditions);
				listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, supDeptIds));
				returnValue = delegator.find("PartyFullNameDetail", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, listSortFields, opts);
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListSupDeptBySA", module);
	    }
		
		return returnValue;
	}
	
	public static EntityListIterator getIteratorSupPersonBySA(Delegator delegator, String partyId, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue= null;
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		returnValue = getIteratorCoreEmpPersonFollowSupDeptBySA(delegator, partyId, listSortFields, opts, supRoleId);
		return returnValue;
	}
	
	public static EntityListIterator getIteratorSalesmanPersonBySA(Delegator delegator, String partyId, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue= null;
		String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALESMAN_DL, delegator);
		returnValue = getIteratorCoreEmpPersonFollowSupDeptBySA(delegator, partyId, listSortFields, opts, salesmanRoleId);
		return returnValue;
	}
	
	@SuppressWarnings("unchecked")
	public static EntityListIterator getIteratorCoreEmpPersonFollowSupDeptBySA(Delegator delegator, String partyId, List<String> listSortFields, EntityFindOptions opts, String roleTypeId) {
		EntityListIterator returnValue= null;
		List<String> supDepts = getListSupDeptBySA(delegator, partyId);
		if (UtilValidate.isEmpty(supDepts)) return returnValue;
		supDepts = (List<String>) SetUtil.removeDuplicateElementInList(supDepts);
		List<String> roleTypes = getListDescendantRoleInclude(roleTypeId, delegator);
		
		returnValue = getIteratorCoreEmpPersonFollowOneDept(delegator, listSortFields, opts, supDepts, roleTypes);
		return returnValue;
	}
	
	/**
	 * Get list X's department by "X's roleTypeId" and "partyId of salesAdmin person"
	 * @param delegator
	 * @param partyId The salesAdminId
	 * @param roleTypeIdAccept X's roleTypeId
	 * @return The list Sales X's department Id
	 * @throws GenericEntityException 
	 */
	public static List<String> getListDeptXByRoleXAndSaId(Delegator delegator, String partyId, String roleTypeIdAccept) throws GenericEntityException {
		List<String> returnValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(partyId)) return returnValue;
		String salesAdminRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SALEADMIN_DL, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String partyRelTypeIdSalesMgr = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESMGR, delegator);
		EntityFindOptions findOpts = new EntityFindOptions();
		findOpts.setDistinct(true);
		if (UtilValidate.isEmpty(salesAdminRoleId) || UtilValidate.isEmpty(internalOrgRole)) {
			return returnValue;
		}
		List<String> partyRoleTypes = getListDescendantRoleInclude(salesAdminRoleId, delegator);
		List<String> partyRoleTypesAsm = getListDescendantRoleInclude(roleTypeIdAccept, delegator);
		/*List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, partyRoleTypes));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));*/
		
		// get list position sales administrator manage, ASM is normal case
		List<EntityCondition> listConds2 = FastList.newInstance();
		listConds2.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, partyRoleTypes));
		listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesMgr));
		List<GenericValue> listPstSalesAdminMgr = EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, findOpts, false));
		for (GenericValue pstSalesAdminMgr : listPstSalesAdminMgr) {
			String roleTypeIdTo = pstSalesAdminMgr.getString("roleTypeIdTo");
			String partyIdTo = pstSalesAdminMgr.getString("partyIdTo");
			List<String> listPartyRel2 = findPartyIdsAcceptCondition(delegator, partyRoleTypesAsm, roleTypeIdTo, partyIdTo, false);
			if (UtilValidate.isNotEmpty(listPartyRel2)) {
				returnValue.addAll(listPartyRel2);
			}
		}
		
		return returnValue;
	}
	
	// ====================================== Util Base ==============================================================================
	/**
	 * 
	 * @param delegator
	 * @param listAccept List roleTypeId accepted
	 * @param roleTypeId Root's roleTypeId
	 * @param partyId Root's partyId
	 * @param runOnlyUp Only action find roll up, find parent
	 * @return
	 * @throws GenericEntityException
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findPartyIdsAcceptCondition(Delegator delegator, List<String> listAccept, String roleTypeId, String partyId, boolean runOnlyUp) throws GenericEntityException {
		List<String> returnValue = new ArrayList<String>();
		if (listAccept.contains(roleTypeId)) {
			returnValue.add(partyId);
		} else {
			boolean isSearched = false;
			EntityFindOptions findOpts = new EntityFindOptions();
			findOpts.setDistinct(true);
			if (!isSearched && !runOnlyUp) {
				// search roll down
				List<EntityCondition> listConds3 = FastList.newInstance();
				listConds3.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				listConds3.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeId));
				listConds3.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
				List<GenericValue> listPstRollDown3 = EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds3, EntityOperator.AND), null, null, findOpts, false));
				if (listPstRollDown3 != null) {
					for (GenericValue pstRollDown : listPstRollDown3) {
						String roleTypeIdTo3 = pstRollDown.getString("roleTypeIdTo");
						String partyIdTo3 = pstRollDown.getString("partyIdTo");
						// ... if else
						List<String> listPartyRel3 = findPartyIdsAcceptCondition(delegator, listAccept, roleTypeIdTo3, partyIdTo3, false);
						if (UtilValidate.isNotEmpty(listPartyRel3)) {
							isSearched = true;
							returnValue.addAll(listPartyRel3);
						}
					}
				}
			}
			if (!isSearched) {
				// search roll up
				List<EntityCondition> listConds3 = FastList.newInstance();
				listConds3.add(EntityCondition.makeCondition("partyIdTo", partyId));
				listConds3.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeId));
				listConds3.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
				List<GenericValue> listPstRollDown3 = EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds3, EntityOperator.AND), null, null, findOpts, false));
				if (listPstRollDown3 != null) {
					for (GenericValue pstRollDown : listPstRollDown3) {
						String roleTypeIdFrom3 = pstRollDown.getString("roleTypeIdFrom");
						String partyIdFrom3 = pstRollDown.getString("partyIdFrom");
						// ... if else
						List<String> listPartyRel3 = findPartyIdsAcceptCondition(delegator, listAccept, roleTypeIdFrom3, partyIdFrom3, true);
						if (UtilValidate.isNotEmpty(listPartyRel3)) {
							isSearched = true;
							returnValue.addAll(listPartyRel3);
						}
					}
				}
			}
		}
		returnValue = (List<String>) SetUtil.removeDuplicateElementInList(returnValue);
		return returnValue;
	}
	
	/**
	 * Get list person is manager of department partyGroup
	 * @param delegator
	 * @param partyId Id of department
	 * @return List PartyFromAndPartyNameDetail Record. Return name "partyIdFrom"
	 */
	public static List<GenericValue> getListManagerPersonByDept(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdTo", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			List<GenericValue> returnValue2 = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
			if (returnValue2 != null) {
				returnValue.addAll(returnValue2);
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListManagerPersonByDept", module);
	    }
		return returnValue;
	}
	/**
	 * Get list person is manager of departments partyGroup
	 * @param delegator
	 * @param partyId Id of department
	 * @return List PartyFromAndPartyNameDetail Record. Return name "partyIdFrom"
	 */
	public static List<GenericValue> getListManagerPersonByDept(Delegator delegator, List<String> partyIds) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		if (partyIds == null) return returnValue;
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			for (String partyId : partyIds) {
				List<EntityCondition> listConds2 = FastList.newInstance();
				listConds2.add(EntityCondition.makeCondition("partyIdTo", partyId));
				listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
				listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
				listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
				List<GenericValue> returnValue2 = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
				if (returnValue2 != null) {
					returnValue.addAll(returnValue2);
				}
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListManagerPersonByDept", module);
	    }
		return returnValue;
	}
	
	/**
	 * Get list department of employee
	 * @param delegator
	 * @param partyId
	 * @return List PartyRelationship Record. Return name "partyIdFrom"
	 */
	public static List<GenericValue> getListDeptByEmployee(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String relTypeEmployment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_EMPLOYMENT, delegator);
    	String roleTypeEmployee = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_EMPLOYEE, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdTo", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeEmployee));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", relTypeEmployment));
			List<GenericValue> returnValue2 = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
			if (returnValue2 != null) {
				returnValue.addAll(returnValue2);
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListDeptByEmployee", module);
	    }
		return returnValue;
	}
	
	public static List<String> getListDeptIdByEmployee(Delegator delegator, String partyId) {
		List<String> returnValue = null;
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		String relTypeEmployment = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_EMPLOYMENT, delegator);
    	String roleTypeEmployee = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_EMPLOYEE, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdTo", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeEmployee));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", relTypeEmployment));
			returnValue = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false), 
					"partyIdFrom", true);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListDeptIdByEmployee", module);
	    }
		return returnValue;
	}
	
	public static List<GenericValue> getListDeptByManager(Delegator delegator, String partyId) {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			List<GenericValue> returnValue2 = delegator.findList("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false);
			if (returnValue2 != null) {
				returnValue.addAll(returnValue2);
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListDeptByManager", module);
	    }
		return returnValue;
	}
	
	/**
	 * Get iterator list dept by manager
	 * @param delegator
	 * @param partyId
	 * @param listAllConditions
	 * @param listSortFields
	 * @param opts
	 * @return List PartyRelationship Record. Return name "partyId"
	 */
	public static EntityListIterator getIteratorDeptByManager(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		if (UtilValidate.isEmpty(partyId)) return null;
		EntityListIterator returnValue = null;
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			if (listAllConditions != null) listConds2.addAll(listAllConditions);
			listConds2.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			returnValue = delegator.find("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getIteratorDeptByManager", module);
	    }
		return returnValue;
	}
	
	public static List<String> getListDeptIdByManager(Delegator delegator, String partyId) {
    	String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		try {
			List<EntityCondition> listConds2 = FastList.newInstance();
			listConds2.add(EntityCondition.makeCondition("partyIdFrom", partyId));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
			listConds2.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
			listConds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			return EntityUtil.getFieldListFromEntityList(delegator.findList("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listConds2, EntityOperator.AND), null, null, null, false), 
					"partyId", true);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getListDeptByManager", module);
	    }
		return null;
	}
	
	/**
	 * [Method core] Get iterator person is employee of department (SUP department, ASM department, RSM department ...)
	 * @param delegator
	 * @param listSortFields
	 * @param opts
	 * @param deptIds
	 * @param roleTypeIdsEmployee
	 * @return Iterator PartyFromAndPartyNameDetail record. Return name "partyIdFrom"
	 */
	public static EntityListIterator getIteratorCoreEmpPersonFollowOneDept(Delegator delegator, 
			List<String> listSortFields, EntityFindOptions opts, List<String> deptIds, List<String> roleTypeIdsEmployee) {
		EntityListIterator returnValue= null;
		if (UtilValidate.isEmpty(deptIds)) return returnValue;
		String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, deptIds));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsEmployee));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));
		listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			returnValue = delegator.find("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getIteratorCoreEmpPersonFollowOneDept", module);
	    }
		return returnValue;
	}
	public static List<GenericValue> getCoreEmpPersonFollowOneDept(Delegator delegator, 
			List<String> listSortFields, EntityFindOptions opts, List<String> deptIds, List<String> roleTypeIdsEmployee) {
		List<GenericValue> returnValue= null;
		if (UtilValidate.isEmpty(deptIds)) return returnValue;
		String partyRelTypeIdSalesEmp = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_SALESEMP, delegator);
		String internalOrgRole = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_INTERNAL_ORG, delegator);
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, deptIds));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsEmployee));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", internalOrgRole));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeIdSalesEmp));
		listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			returnValue = delegator.findList("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getIteratorCoreEmpPersonFollowOneDept", module);
	    }
		return returnValue;
	}
	public static EntityListIterator getCoreIteratorPersonFollowByParties(Delegator delegator, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts, boolean isGetPartyFrom, 
			String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, 
			List<String> partyIdsTo, List<String> partyIdsFrom, List<String> roleTypeIdsFrom, List<String> roleTypeIdsTo, String partyRelTypeId, boolean isActiving) {
		EntityListIterator returnValue= null;
		List<EntityCondition> listConds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listAllConditions)) listConds.addAll(listAllConditions);
		if (UtilValidate.isNotEmpty(partyIdFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		if (UtilValidate.isNotEmpty(partyIdTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		if (UtilValidate.isNotEmpty(roleTypeIdFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if (UtilValidate.isNotEmpty(partyIdsFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIdsFrom));
		if (UtilValidate.isNotEmpty(partyIdsTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdsTo));
		if (UtilValidate.isNotEmpty(roleTypeIdsFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdsTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleTypeIdsTo));
		if (UtilValidate.isNotEmpty(partyRelTypeId)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeId));
		if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			String entityName = "PartyFromAndPartyNameDetail";
			if (!isGetPartyFrom) entityName = "PartyToAndPartyNameDetail";
			returnValue = delegator.find(entityName, EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getCoreIteratorPersonFollowByParties", module);
	    }
		return returnValue;
	}
	public static List<GenericValue> getCorePersonFollowByParties(Delegator delegator, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts, boolean isGetPartyFrom, 
			String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, 
			List<String> partyIdsFrom, List<String> partyIdsTo, List<String> roleTypeIdsFrom, List<String> roleTypeIdsTo, String partyRelTypeId, boolean isActiving) {
		List<GenericValue> returnValue= null;
		List<EntityCondition> listConds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listAllConditions)) listConds.addAll(listAllConditions);
		if (UtilValidate.isNotEmpty(partyIdFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		if (UtilValidate.isNotEmpty(partyIdTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		if (UtilValidate.isNotEmpty(roleTypeIdFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if (UtilValidate.isNotEmpty(partyIdsFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIdsFrom));
		if (UtilValidate.isNotEmpty(partyIdsTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdsTo));
		if (UtilValidate.isNotEmpty(roleTypeIdsFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdsTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleTypeIdsTo));
		if (UtilValidate.isNotEmpty(partyRelTypeId)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeId));
		if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			String entityName = "PartyFromAndPartyNameDetail";
			if (!isGetPartyFrom) entityName = "PartyToAndPartyNameDetail";
			returnValue = delegator.findList(entityName, EntityCondition.makeCondition(listConds, EntityOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getCoreIteratorPersonFollowByParties", module);
	    }
		return returnValue;
	}
	public static List<GenericValue> getCorePersonFollowByParties(Delegator delegator, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts, boolean isGetPartyFrom, 
			String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, 
			List<String> partyIdsFrom, List<String> partyIdsTo, List<String> roleTypeIdsFrom, List<String> roleTypeIdsTo, String partyRelTypeId, List<String> partyRelTypeIds, boolean isActiving) {
		List<GenericValue> returnValue= null;
		List<EntityCondition> listConds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listAllConditions)) listConds.addAll(listAllConditions);
		if (UtilValidate.isNotEmpty(partyIdFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		if (UtilValidate.isNotEmpty(partyIdTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		if (UtilValidate.isNotEmpty(roleTypeIdFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if (UtilValidate.isNotEmpty(partyIdsFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIdsFrom));
		if (UtilValidate.isNotEmpty(partyIdsTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdsTo));
		if (UtilValidate.isNotEmpty(roleTypeIdsFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdsTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleTypeIdsTo));
		if (UtilValidate.isNotEmpty(partyRelTypeId)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeId));
		if (UtilValidate.isNotEmpty(partyRelTypeIds)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, partyRelTypeIds));
		if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			String entityName = "PartyFromAndPartyNameDetail";
			if (!isGetPartyFrom) entityName = "PartyToAndPartyNameDetail";
			returnValue = delegator.findList(entityName, EntityCondition.makeCondition(listConds, EntityOperator.AND), null, listSortFields, opts, false);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getCoreIteratorPersonFollowByParties", module);
	    }
		return returnValue;
	}
	public static EntityListIterator getIteratorCorePersonFollowByParties(Delegator delegator, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts, boolean isGetPartyFrom, 
			String partyIdFrom, String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo, 
			List<String> partyIdsFrom, List<String> partyIdsTo, List<String> roleTypeIdsFrom, List<String> roleTypeIdsTo, String partyRelTypeId, boolean isActiving) {
		EntityListIterator returnValue= null;
		List<EntityCondition> listConds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listAllConditions)) listConds.addAll(listAllConditions);
		if (UtilValidate.isNotEmpty(partyIdFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		if (UtilValidate.isNotEmpty(partyIdTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", partyIdTo));
		if (UtilValidate.isNotEmpty(roleTypeIdFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		if (UtilValidate.isNotEmpty(partyIdsFrom)) listConds.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIdsFrom));
		if (UtilValidate.isNotEmpty(partyIdsTo)) listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, partyIdsTo));
		if (UtilValidate.isNotEmpty(roleTypeIdsFrom)) listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleTypeIdsFrom));
		if (UtilValidate.isNotEmpty(roleTypeIdsTo)) listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleTypeIdsTo));
		if (UtilValidate.isNotEmpty(partyRelTypeId)) listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelTypeId));
		if (isActiving) listConds.add(EntityUtil.getFilterByDateExpr());
		try {
			String entityName = "PartyFromAndPartyNameDetail";
			if (!isGetPartyFrom) entityName = "PartyToAndPartyNameDetail";
			returnValue = delegator.find(entityName, EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getCoreIteratorPersonFollowByParties", module);
	    }
		return returnValue;
	}
	public static void buildDataEmployeeTree(Delegator delegator, String rootId, SalesEmployeeEntity employee, List<String> roleAllFind) {
		try {
			GenericValue root = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", rootId), false);
			if (root != null) {
				employee.setPartyId(root.getString("partyId"));
				String firstName = root.getString("firstName");
				String middleName = root.getString("middleName");
				String lastName = root.getString("lastName");
				String groupName = root.getString("groupName");
				String fullName = PartyHelper.formatPartyNameObject(root, true, true);
				employee.setFirstName(firstName);
				employee.setMiddleName(middleName);
				employee.setLastName(lastName);
				employee.setGroupName(groupName);
				employee.setFullName(fullName);
				employee.setBirthday(root.getDate("birthDate"));
				employee.setDescription(root.getString("description"));
				employee.setStatusId(root.getString("statusId"));
				
				// get data level 1
				employee.addChild(coreBuildEmployeeTree(delegator, root.getString("partyId"), roleAllFind));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error when running method buildDataEmployeeTree", module);
		}
	}
	
	public static void buildDataEmployeeTree(Delegator delegator, List<String> rootIds, SalesEmployeeEntity employee, List<String> roleAllFind) {
		if (rootIds != null && rootIds.size() > 1) {
			try {
				employee.setPartyId("ROOT");
				for (String rootId : rootIds) {
					GenericValue root = delegator.findOne("PartyNameView", UtilMisc.toMap("partyId", rootId), false);
					if (root != null) {
						SalesEmployeeEntity child = new SalesEmployeeEntity();
						child.setPartyId(root.getString("partyId"));
						String firstName = root.getString("firstName");
						String middleName = root.getString("middleName");
						String lastName = root.getString("lastName");
						String groupName = root.getString("groupName");
						String fullName = PartyHelper.formatPartyNameObject(root, true, true);
						child.setFirstName(firstName);
						child.setMiddleName(middleName);
						child.setLastName(lastName);
						child.setGroupName(groupName);
						child.setFullName(fullName);
						child.setBirthday(root.getDate("birthDate"));
						child.setDescription(root.getString("description"));
						child.setStatusId(root.getString("statusId"));
						
						// get data level 1
						child.addChild(coreBuildEmployeeTree(delegator, root.getString("partyId"), roleAllFind));
						employee.addChild(child);
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error when running method buildDataEmployeeTree", module);
			}
		} else if (rootIds != null && rootIds.size() == 1){
			buildDataEmployeeTree(delegator, rootIds.get(0), employee, roleAllFind);
		}
	}
	
	public static List<SalesEmployeeEntity> coreBuildEmployeeTree(Delegator delegator, String partyId, List<String> roleAllFind) throws GenericEntityException {
		List<SalesEmployeeEntity> returnValue = new ArrayList<SalesEmployeeEntity>();
		String relTypeDept = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DEPARTMENT, delegator);
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", partyId));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", relTypeDept));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, roleAllFind));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, roleAllFind));
		listConds.add(EntityUtil.getFilterByDateExpr());
		
		// Get children
		List<GenericValue> listChildGV = delegator.findList("PartyToAndPartyNameDetail", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, UtilMisc.toList("partyId"), null, false);
		for (GenericValue childGV : listChildGV) {
			SalesEmployeeEntity childItem = new SalesEmployeeEntity();
			childItem.setPartyId(childGV.getString("partyId"));
			String firstName2 = childGV.getString("firstName");
			String middleName2 = childGV.getString("middleName");
			String lastName2 = childGV.getString("lastName");
			String groupName2 = childGV.getString("groupName");
			String fullName2 = PartyHelper.formatPartyNameObject(childGV, true, true);
			childItem.setFirstName(firstName2);
			childItem.setMiddleName(middleName2);
			childItem.setLastName(lastName2);
			childItem.setGroupName(groupName2);
			childItem.setFullName(fullName2);
			childItem.setBirthday(childGV.getDate("birthDate"));
			childItem.setDescription(childGV.getString("description"));
			childItem.setStatusId(childGV.getString("statusId"));
			
			// Get manager
			List<GenericValue> listManager = getListManagerPersonByDept(delegator, childGV.getString("partyId"));
			if (listManager != null && listManager.size() > 0) {
				GenericValue manager = EntityUtil.getFirst(listManager);
				SalesEmployeeEntity managerItem = new SalesEmployeeEntity();
				managerItem.setPartyId(manager.getString("partyId"));
				String firstName3 = manager.getString("firstName");
				String middleName3 = manager.getString("middleName");
				String lastName3 = manager.getString("lastName");
				String groupName3 = manager.getString("groupName");
				String fullName3 = PartyHelper.formatPartyNameObject(manager, true, true);
				managerItem.setFirstName(firstName3);
				managerItem.setMiddleName(middleName3);
				managerItem.setLastName(lastName3);
				managerItem.setGroupName(groupName3);
				managerItem.setFullName(fullName3);
				managerItem.setBirthday(manager.getDate("birthDate"));
				managerItem.setDescription(manager.getString("description"));
				managerItem.setStatusId(manager.getString("statusId"));
				childItem.setManager(managerItem);
			}
			
			// Get children
			List<SalesEmployeeEntity> listChild2 = coreBuildEmployeeTree(delegator, childGV.getString("partyId"), roleAllFind);
			if (listChild2 != null) childItem.addChild(listChild2);
			returnValue.add(childItem);
		}
		
		// Get Salesman
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, "party.role.sup.delys", delegator);
    	List<String> supRoleIds = SalesPartyUtil.getListDescendantRoleInclude(supRoleId, delegator);
    	boolean isSupDept = false;
    	if (supRoleIds != null) {
    		for (String supId : supRoleIds) {
    			if (SecurityUtil.hasRole(supId, partyId, delegator)) {
					isSupDept = true;
				}
    		}
    	}
    	if (isSupDept) {
    		List<SalesEmployeeEntity> listSalesman = new ArrayList<SalesEmployeeEntity>();
    		List<GenericValue> listSalesmanGV = getListSalesmanPersonBySupDept(delegator, partyId, null, null, null);
    		if (listSalesmanGV != null) {
    			if (listSalesmanGV != null) {
    				for (GenericValue salesman : listSalesmanGV) {
    					SalesEmployeeEntity childItem = new SalesEmployeeEntity();
    					childItem.setPartyId(salesman.getString("partyId"));
    					String firstName2 = salesman.getString("firstName");
    					String middleName2 = salesman.getString("middleName");
    					String lastName2 = salesman.getString("lastName");
    					String groupName2 = salesman.getString("groupName");
    					String fullName2 = "NVBH - " + PartyHelper.formatPartyNameObject(salesman, true, true);
    					childItem.setFirstName(firstName2);
    					childItem.setMiddleName(middleName2);
    					childItem.setLastName(lastName2);
    					childItem.setGroupName(groupName2);
    					childItem.setFullName(fullName2);
    					childItem.setBirthday(salesman.getDate("birthDate"));
    					childItem.setDescription(salesman.getString("description"));
    					childItem.setStatusId(salesman.getString("statusId"));
    					listSalesman.add(childItem);
					}
    			}
    		}
    		returnValue.addAll(listSalesman);
    	}
		
		return returnValue;
	}
	
	public static List<Map<String, Object>> convertEmployeeEntityTreeToListMap(SalesEmployeeEntity employee, String parentId, int level) {
		List<Map<String, Object>> returnValue = new ArrayList<Map<String,Object>>();
		if (employee != null) {
			boolean isAdded = false;
			List<SalesEmployeeEntity> listChild = employee.getListChild();;
			if (UtilValidate.isNotEmpty(parentId)) {
				Map<String, Object> root = FastMap.newInstance();
				root.put("id", employee.getPartyId());
				root.put("parentId", parentId);
				root.put("groupName", employee.getGroupName());
				root.put("fullName", employee.getFullName());
				
				if (employee.getManager() != null) {
					SalesEmployeeEntity manager = employee.getManager();
					root.put("firstName", manager.getFirstName());
					root.put("middleName", manager.getMiddleName());
					root.put("lastName", manager.getLastName());
					root.put("statusId", manager.getStatusId());
					root.put("description", manager.getDescription());
					root.put("birthday", manager.getBirthday());
					String fullName2 = formatPartyName(manager.getFirstName(), manager.getMiddleName(), manager.getLastName(), null);
					if (employee.getFullName() != null && fullName2 != null) root.put("fullName", employee.getFullName().concat(" - " + fullName2));
				} else {
					root.put("firstName", employee.getFirstName());
					root.put("middleName", employee.getMiddleName());
					root.put("lastName", employee.getLastName());
					root.put("statusId", employee.getStatusId());
					root.put("description", employee.getDescription());
					root.put("birthday", employee.getBirthday());
				}
				root.put("level", level);
				List<String> listChildId = FastList.newInstance();
				if (listChild != null) {
					for (SalesEmployeeEntity child : listChild) {
						listChildId.add(child.getPartyId());
					}
				}
				root.put("records", listChildId);
				returnValue.add(root);
				isAdded = true;
			}
			
			if (listChild != null) {
				for (int i = 0; i < listChild.size(); i++) {
					if (isAdded && i == 0) level++;
					SalesEmployeeEntity child = listChild.get(i);
					List<Map<String, Object>> childMap = convertEmployeeEntityTreeToListMap(child, employee.getPartyId(), level);
					if (childMap != null) returnValue.addAll(childMap);
				}
			}
		}
		return returnValue;
	}
	
	public static String formatPartyName(String firstName, String middleName, String lastName, String groupName) {
		StringBuilder result = new StringBuilder();
		result.append(UtilFormatOut.ifNotEmpty(lastName, "", " "));
		result.append(UtilFormatOut.ifNotEmpty(middleName, "", " "));
		result.append(UtilFormatOut.ifNotEmpty(firstName, "", " "));
		result.append(UtilFormatOut.ifNotEmpty(groupName, "", " "));
		
		return result.toString();
	}
	
	public static EntityListIterator getIteratorCustomerBySA(Delegator delegator, String partyId, List<EntityCondition> listAllConditions, List<String> listSortFields, EntityFindOptions opts) {
		EntityListIterator returnValue = null;
		String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_SUP_DL, delegator);
		if (UtilValidate.isEmpty(supRoleId)) return returnValue;
		try {
			List<String> supDeptIds = getListDeptXByRoleXAndSaId(delegator, partyId, supRoleId);
			if (UtilValidate.isNotEmpty(supDeptIds)) {
				List<EntityCondition> listCond = FastList.newInstance();
				if (listAllConditions != null) listCond.addAll(listAllConditions);
				listCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, supDeptIds));
				// get list distributor by sup dept
				String partyRelDistributor = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_DISTRIBUTOR, delegator);
				String disRoleTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_DISTRIBUTOR, delegator);
				List<String> supRoleTypeIds = getListDescendantRoleInclude(supRoleId, delegator);
				List<String> distributorIds = EntityUtil.getFieldListFromEntityList(
						coreGetDeptByDept(delegator, null, null, null, disRoleTypeId, supDeptIds, null, supRoleTypeIds, null, partyRelDistributor, true), "partyIdTo", true);
				if (UtilValidate.isNotEmpty(distributorIds)) {
					String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
					String relIdCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER, delegator);
					List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
					returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
							null, null, "DISTRIBUTOR", null, distributorIds, null, null, customerRoleIds, relIdCustomer, true);
				}
			}
		} catch (GenericEntityException e) {
	        Debug.logError(e, "Error when running method getIteratorCustomerBySA", module);
	    }
		return returnValue;
	}
	
	public static EntityListIterator getListCustomerByDistribution(Delegator delegator,List<EntityCondition> listAllConditions,  List<String> listSortFields,EntityFindOptions opts,List<String> distributorIds){
		EntityListIterator returnValue = null;
		if (UtilValidate.isNotEmpty(distributorIds)) {
			String customerRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTROLE_CUSTOMER_DL, delegator);
			String relIdCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_DL, RSN_PRTRELTYPE_CUSTOMER, delegator);
			List<String> customerRoleIds = getListDescendantRoleInclude(customerRoleId, delegator);
			returnValue = getIteratorCorePersonFollowByParties(delegator, listAllConditions, listSortFields, opts, false, 
					null, null, "DISTRIBUTOR", null, distributorIds, null, null, customerRoleIds, relIdCustomer, true);
		}
		return returnValue;
	}
	
	
	/* Kenh MT
	<!-- Liên kết "khách hàng - Salesman, pg": -->
    <PartyRelationship partyIdFrom="AA0001" partyIdTo="DE_0679" roleTypeIdFrom="CUSTOMER_MT" roleTypeIdTo="DELYS_SALESMAN_MT" fromDate="2015-07-06 15:14:49" partyRelationshipTypeId="SALES_EMPLOYMENT" />
    <PartyRelationship partyIdFrom="SUP_MT2" partyIdTo="DE_0679" roleTypeIdFrom="INTERNAL_ORGANIZATIO" roleTypeIdTo="EMPLOYEE" fromDate="2015-03-23 00:00:00" thruDate="2015-05-21 00:00:00" partyRelationshipTypeId="EMPLOYMENT" lastUpdatedStamp="2015-06-17 17:20:28" lastUpdatedTxStamp="2015-06-17 17:20:28" createdStamp="2015-06-17 17:20:28" createdTxStamp="2015-06-17 17:20:28"> </PartyRelationship>
	<PartyRelationship partyIdFrom="DE_0104" partyIdTo="SUP_MT2" roleTypeIdFrom="MANAGER" roleTypeIdTo="INTERNAL_ORGANIZATIO" fromDate="2015-06-17 17:20:28" partyRelationshipTypeId="MANAGER" lastUpdatedStamp="2015-06-17 17:20:28" lastUpdatedTxStamp="2015-06-17 17:20:28" createdStamp="2015-06-17 17:20:28" createdTxStamp="2015-06-17 17:20:28"> </PartyRelationship>
	
	 */
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sortList(List<Map<String,Object>> listProductCaculateds, List<String> listSortFields){
		if (UtilValidate.isEmpty(listSortFields)) return listProductCaculateds;
		
		String sortField = listSortFields.toString();
    	if(sortField.contains("[")){
    		sortField = StringUtil.replaceString(sortField, "[", "");
    	}
    	if(sortField.contains("]")){
    		sortField = StringUtil.replaceString(sortField, "]", "");
    	}
    	
		CompareObj objCompare = new CompareObj();
		objCompare.setSortField(sortField);
		
		Collections.sort(listProductCaculateds, objCompare);
		return listProductCaculateds;
		
	}
	
	public static List<Map<String, Object>> filterMap(List<Map<String, Object>> listProductCaculateds, List<EntityCondition> listAllConditions) throws ParseException{
		if (UtilValidate.isEmpty(listAllConditions)) return listProductCaculateds;
		
		List<Map<String, Object>> returnResult = FastList.newInstance();
		
		// process list all condition
		List<Map<String, Object>> listAllCondMap = FastList.newInstance();
    	for (EntityCondition condition : listAllConditions){
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = new HashMap<String, Object>();
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				if(fieldName.contains("(")) fieldName = StringUtil.replaceString(fieldName, "(", "");
				if(fieldName.contains(")")) fieldName = StringUtil.replaceString(fieldName, ")", "");
				if(value.contains("(")) value = StringUtil.replaceString(value, "(", "");
				if(value.contains(")")) value = StringUtil.replaceString(value, ")", "");
				if(value.contains("'")) value = StringUtil.replaceString(value, "'", "");
				if(value.contains("%")) value = StringUtil.replaceString(value, "%", "");
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
				listAllCondMap.add(condMap);
			}
		}
    	if(UtilValidate.isNotEmpty(listAllCondMap)){
			for (Map<String, Object> productCal : listProductCaculateds) {
				boolean flagAdd = true;
				for (Map<String, Object> mapCond : listAllCondMap) {
					String fieldName = (String) mapCond.get("fieldName");
					String operator = (String) mapCond.get("operator");
					String value = (String) mapCond.get("value");
					
					if(operator.equals("LIKE")){
						String productCalValue = (String) productCal.get(fieldName);
						/*String realvalue = value.substring(2, value.length() -2);*/
						if(productCalValue == null || !productCalValue.contains(value)){
							flagAdd = false;
							break;
						}
					}
					if(operator.equals("NOT_LIKE")){
						String productCalValue = (String) productCal.get(fieldName);
						if(productCalValue == null || productCalValue.contains(value)){
							flagAdd = false;
							break;
						}
					}
					if(operator.equals("EQUAL")){
						String productCalValue = (String) productCal.get(fieldName);
						if(productCalValue == null || !productCalValue.equals(value)){
							flagAdd = false;
							break;
						}
					}
					if(operator.equals("NOT_EQUAL")){
						String productCalValue = (String) productCal.get(fieldName);
						if(productCalValue == null || productCalValue.equals(value)){
							flagAdd = false;
							break;
						}
					}
					if(operator.equals("=")){
						if(productCal.get(fieldName) instanceof BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter = new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) != 0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter = new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) != 0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
							
						}else {
							String productCalValue = (String) productCal.get(fieldName);
							if(productCalValue == null || !productCalValue.equals(value)){
								flagAdd = false;
								break;
							}
						}
					}
					if(operator.equals(">=")){
						if(productCal.get(fieldName) instanceof Timestamp){
							Timestamp productCalValue = (Timestamp) productCal.get(fieldName);
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
							value = value.replace("-", "/");
		    				value = value + " 00:00:00 am";
						    Date parsedDate = new java.sql.Date(dateFormat.parse((String)value).getTime());
						    Timestamp valueAlter = new java.sql.Timestamp(parsedDate.getTime());
							
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || !productCalValue.after(valueAlter)){
										flagAdd = false;
										break;
									}
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof  BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter =  new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) <0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof  Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter =  new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) <0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
							
						}
					}
					if(operator.equals("<=")){
						if(productCal.get(fieldName) instanceof Timestamp){
							Timestamp productCalValue = (Timestamp) productCal.get(fieldName);
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
							value = value.replace("-", "/");
							value = value + " 23:59:59 pm";
						    Date parsedDate = new java.sql.Date(dateFormat.parse((String)value).getTime());
						    Timestamp valueAlter = new java.sql.Timestamp(parsedDate.getTime());
							
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || !productCalValue.before(valueAlter)){
										flagAdd = false;
										break;
									}
								}
							}else{
								flagAdd = false;
								break;
							}
							
						}else if(productCal.get(fieldName) instanceof BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter =  new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) >0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter =  new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) >0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}
					}
					if(operator.equals(">")){
						if(productCal.get(fieldName) instanceof  BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter =  new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) <=0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof  Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter =  new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) <=0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
							
						}
					}
					if(operator.equals("<")){
					 if(productCal.get(fieldName) instanceof BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter =  new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) >=0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter =  new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) >=0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}	
							
						}
					}
					if(operator.equals("<>")){
						if(productCal.get(fieldName) instanceof BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							BigDecimal valueAlter =  new BigDecimal(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) ==0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}else if(productCal.get(fieldName) instanceof Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							Long valueAlter =  new Long(value);
							if(UtilValidate.isNotEmpty(productCalValue)){
								if(UtilValidate.isNotEmpty(valueAlter)){
									if(productCalValue == null || productCalValue.compareTo(valueAlter) ==0){
										flagAdd = false;
										break;
									}
								}else{
									flagAdd = false;
									break;
								}
							}else{
								flagAdd = false;
								break;
							}
						}
					}
					if(operator.equals("IS")){
						if(productCal.get(fieldName) instanceof BigDecimal){
							BigDecimal productCalValue = (BigDecimal) productCal.get(fieldName);
							if(value.equals("NULL")){
								if(productCalValue != null){
									flagAdd = false;
									break;
								}
							}else{
								if(productCalValue == null){
									flagAdd = false;
									break;
								}
							}
							
						}else if(productCal.get(fieldName) instanceof Long){
							Long productCalValue = (Long) productCal.get(fieldName);
							if(value.equals("NULL")){
								if(productCalValue != null){
									flagAdd = false;
									break;
								}
							}else{
								if(productCalValue == null){
									flagAdd = false;
									break;
								}
							}
							
							
						}
					}
				}
				if(flagAdd){
					returnResult.add(productCal);
				}
			}
		} else {
			returnResult.addAll(listProductCaculateds);
		}
		return returnResult;
 	}
	
	public static List<GenericValue> processIterator(EntityListIterator listIterator, Map<String, String[]> parameters, Map<String, Object> successResult) {
    	List<GenericValue> returnValue = null;
    	String viewIndexStr = (String) parameters.get("pagenum")[0];
    	String viewSizeStr = (String) parameters.get("pagesize")[0];
    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
    	try {
    		if (UtilValidate.isNotEmpty(listIterator)) {
    			if (viewSize != 0) {
    				if (viewIndex == 0) {
    					returnValue = listIterator.getPartialList(0, viewSize);
    				} else {
    					returnValue = listIterator.getPartialList(viewIndex * viewSize + 1, viewSize);
    				}
    			} else {
    				returnValue = listIterator.getCompleteList();
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling processIterator service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		if (listIterator != null) {
			try {
				int totalRows = listIterator.getResultsSizeAfterPartialList();
				successResult.put("TotalRows", String.valueOf(totalRows));
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error when get size of list iterator", module);
			} finally {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
		}
    	return returnValue;
    }
}
@SuppressWarnings("rawtypes")
class CompareObj implements Comparator {
	private String sortField;
	public String getSortField() {
		return sortField;
	}
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object test1, Object test2) {
		Map<String, Object> o1 = (Map<String, Object>) test1;
		Map<String, Object> o2 = (Map<String, Object>) test2;
		// TODO Auto-generated method stub
		String alterSortField = this.sortField;
		if(alterSortField.contains("-")){
			alterSortField = alterSortField.replace("-", "");
		}
		if(o1.get(alterSortField) instanceof String){
			if(sortField.contains("-")){
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((String)o1.get(alterSortField)).compareTo((String)o2.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return 1;
				} else if (o2.get(alterSortField) == null) {
					return -1;
				}
			}else{
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((String)o2.get(alterSortField)).compareTo((String)o1.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return -1;
				} else if (o2.get(alterSortField) == null) {
					return 1;
				}
			}
		}else if(o1.get(alterSortField) instanceof BigDecimal){
			if(sortField.contains("-")){
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((BigDecimal)o1.get(alterSortField)).compareTo((BigDecimal)o2.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return 1;
				} else if (o2.get(alterSortField) == null) {
					return -1;
				}
			}else{
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((BigDecimal)o2.get(alterSortField)).compareTo((BigDecimal)o1.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return -1;
				} else if (o2.get(alterSortField) == null) {
					return 1;
				}
			}
		}else{
			if(sortField.contains("-")){
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((Timestamp)o1.get(alterSortField)).compareTo((Timestamp)o2.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return 1;
				} else if (o2.get(alterSortField) == null) {
					return -1;
				}
			}else{
				if (o1.get(alterSortField) != null && o2.get(alterSortField) != null) {
					return ((Timestamp)o2.get(alterSortField)).compareTo((Timestamp)o1.get(alterSortField));
				} else if (o1.get(alterSortField) == null && o2.get(alterSortField) == null) {
					return 0;
				} else if (o1.get(alterSortField) == null) {
					return 1;
				} else if (o2.get(alterSortField) == null) {
					return -1;
				}
			}
		}
		return 0;
	}
}
