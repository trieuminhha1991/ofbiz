package com.olbius.basesales.party;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;

import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;

public class PartyWorker {
	public static final String module = PartyWorker.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static String RESOURCE_PROPERTIES = "basesales.properties";
    
    public static void checkAndAddRelSalesExecutive(Delegator delegator, String organizationId, String salesExecutiveId, String customerId) {
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
		String salesExecutiveTypeId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
		String orgRoleTypeId = SalesUtil.getPropertyValue(delegator, "role.org.to.customer");
		
		//List<String> customerTypeIds = SalesPartyUtil.getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
		List<String> customerTypeIds = new ArrayList<String>();
		List<String> roleCustomers = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.customers");
		if (UtilValidate.isNotEmpty(roleCustomers)) {
			for (String roleCustomer : roleCustomers) {
				List<String> tmp = SalesPartyUtil.getDescendantRoleTypeIds(roleCustomer, delegator);
				if (UtilValidate.isNotEmpty(tmp)) customerTypeIds.addAll(tmp);
			}
		}
    	try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", customerId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", salesExecutiveTypeId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, customerTypeIds));
			List<GenericValue> tmpRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
			if (tmpRel == null || tmpRel.size() < 1) {
				//String retailRoleTypeId = SalesUtil.getPropertyValue(delegator, "role.individual.customer");
				List<GenericValue> customerRels = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", 
						UtilMisc.toMap("partyIdFrom", customerId, "partyIdTo", organizationId, "roleTypeIdTo", orgRoleTypeId), null, false));
				if (customerRels != null) {
					List<String> customerRoleIds = EntityUtil.getFieldListFromEntityList(customerRels, "roleTypeIdFrom", true);
					if (customerRoleIds != null && customerRoleIds.size() > 0) {
						String customerRoleId = customerRoleIds.get(0);
						String contactRoleId = SalesUtil.getPropertyValue(delegator, "role.contact.customer");
						String retailRoleId = SalesUtil.getPropertyValue(delegator, "role.individual.customer");
						if (customerRoleId.equals(contactRoleId)) {
							customerRoleId = retailRoleId;
						}
						GenericValue partyRel = delegator.makeValue("PartyRelationship");
						partyRel.put("partyIdFrom", salesExecutiveId);
						partyRel.put("partyIdTo", customerId);
						partyRel.put("roleTypeIdFrom", salesExecutiveTypeId);
						partyRel.put("roleTypeIdTo", customerRoleId);
						partyRel.put("fromDate", nowTimestamp);
						partyRel.put("partyIdFrom", salesExecutiveId);
						partyRel.put("partyRelationshipTypeId", partyRelationshipTypeId);
						delegator.create(partyRel);
					}
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when checkAndAddRelSalesExecutive: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
	}
    
    public static List<String> getDistributorInOrgByManager(Delegator delegator, String partyId) throws GenericEntityException {
    	List<String> distributorIds = new ArrayList<String>();
    	String deptId = null;
		List<String> deptIds = PartyWorker.getOrgByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(deptIds)) {
			deptId = deptIds.get(0);
			distributorIds = EntityUtil.getFieldListFromEntityList(PartyWorker.getDistributorInOrg(delegator, deptId), "partyId", true);
		}
    	return distributorIds;
    }
    
    public static List<GenericValue> getDistributorInOrg(Delegator delegator, String deptId) throws GenericEntityException{
    	List<GenericValue> listDistributor = new ArrayList<GenericValue>();
    	
    	Organization buildOrg = PartyUtil.buildOrg(delegator, deptId, true, false);
		if (buildOrg != null) {
			Timestamp nowTimstamp = UtilDateTime.nowTimestamp();
			listDistributor = getEmplInOrgAtPeriod(delegator, nowTimstamp, null, buildOrg.getOrg(), buildOrg.getChilListOrg());
		}
    	
    	return listDistributor;
    }
    
    public static List<GenericValue> getEmplInOrgAtPeriod(Delegator delegator, Timestamp fromDate, Timestamp thruDate, GenericValue org, List<Organization> childList) throws GenericEntityException{
		List<GenericValue> employeeList = FastList.newInstance();
		String rootPartyId = org.getString("partyId"); 
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", rootPartyId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "DISTRIBUTOR"));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTION"));
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, thruDate));
		}
		conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
														EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)));
		//conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN, ));
		employeeList = delegator.findList("EmploymentAndPartyGroupOlbius", EntityCondition.makeCondition(conditions), null, null, null, false);
		for(Organization orgItem: childList){
			List<GenericValue> tempList = getEmplInOrgAtPeriod(delegator, fromDate, thruDate, orgItem.getOrg(), orgItem.getChilListOrg());
			List<String> partyIdExistsList = EntityUtil.getFieldListFromEntityList(employeeList, "partyId", true);
			tempList = EntityUtil.filterByCondition(tempList, EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN, partyIdExistsList));
			employeeList.addAll(tempList);
		}
		return employeeList;
	}
    
    public static List<String> getCustomerIdsBySalesExecutive(Delegator delegator, String salesExecutiveId) throws GenericEntityException {
    	List<String> customerIds = FastList.newInstance();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
		String roleSalesExecutiveId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
		String roleCustomerId = SalesUtil.getPropertyValue(delegator, "role.customer");
		
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", salesExecutiveId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleSalesExecutiveId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleCustomerId));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
		listConds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> salesToCustomerRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		if (UtilValidate.isNotEmpty(salesToCustomerRels)) {
			customerIds = EntityUtil.getFieldListFromEntityList(salesToCustomerRels, "partyIdTo", true);
		}
		
		return customerIds;
	}
    public static List<String> getCustomerIdsBySalesmanId(Delegator delegator, String salesmanId) throws GenericEntityException {
    	List<String> customerIds = FastList.newInstance();
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("salesmanId", salesmanId));
		listConds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
        List<GenericValue> partyCustomers = delegator.findList("PartyCustomer", EntityCondition.makeCondition(listConds), null, null, null, false);
		if (UtilValidate.isNotEmpty(partyCustomers)) {
			customerIds = EntityUtil.getFieldListFromEntityList(partyCustomers, "partyId", true);
		}
		return customerIds;
	}
	public static List<String> getDistributorIdsBySalesmanId(Delegator delegator, String partyId) throws GenericEntityException {
		List<String> distributorIds = FastList.newInstance();
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyId", partyId));
		listConds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
		List<GenericValue> partySalesman = delegator.findList("PartySalesman", EntityCondition.makeCondition(listConds), null, null, null, false);
		if (UtilValidate.isNotEmpty(partySalesman)) {
			distributorIds = EntityUtil.getFieldListFromEntityList(partySalesman, "distributorId", true);
		}
		return distributorIds;
	}
    
    public static List<String> getDistributorIdsBySalesExecutive(Delegator delegator, String salesExecutiveId) throws GenericEntityException {
    	List<String> distributorIds = FastList.newInstance();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.distributor.to.sales.executive");
    	String roleSalesExecutiveId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
    	String roleDistributorId = SalesUtil.getPropertyValue(delegator, "role.distributor");
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdTo", salesExecutiveId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleDistributorId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleSalesExecutiveId));
    	listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
    	listConds.add(EntityUtil.getFilterByDateExpr());
    	List<GenericValue> disToSalesRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyIdFrom"), null, null, false);
    	if (UtilValidate.isNotEmpty(disToSalesRels)) {
    		distributorIds = EntityUtil.getFieldListFromEntityList(disToSalesRels, "partyIdFrom", true);
    	}
    	
    	return distributorIds;
    }
    public static String getDistributorIdBySalesExecutive(Delegator delegator, String salesExecutiveId) throws GenericEntityException {
    	List<String> tmp = getDistributorIdsBySalesExecutive(delegator, salesExecutiveId);
    	return (tmp != null && tmp.size() > 0) ? tmp.get(0) : null;
    }
    public static List<String> getSalesExecutiveIdsByDistributor(Delegator delegator, String distributorId) throws GenericEntityException {
    	List<String> salesExeIds = FastList.newInstance();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.distributor.to.sales.executive");
    	String roleSalesExecutiveId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
    	String roleDistributorId = SalesUtil.getPropertyValue(delegator, "role.distributor");
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdFrom", distributorId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleDistributorId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleSalesExecutiveId));
    	listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
    	listConds.add(EntityUtil.getFilterByDateExpr());
    	List<GenericValue> disToSalesRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyIdTo"), null, null, false);
    	if (UtilValidate.isNotEmpty(disToSalesRels)) {
    		salesExeIds = EntityUtil.getFieldListFromEntityList(disToSalesRels, "partyIdTo", true);
    	}
    	
    	return salesExeIds;
    }
    public static List<String> getSalesExecutiveIdsByCustomer(Delegator delegator, String customerId) throws GenericEntityException {
    	List<String> salesExeIds = FastList.newInstance();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
    	String roleSalesExecutiveId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
    	String roleCustomerId = SalesUtil.getPropertyValue(delegator, "role.customer");
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdTo", customerId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleSalesExecutiveId));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleCustomerId));
    	listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
    	listConds.add(EntityUtil.getFilterByDateExpr());
    	List<GenericValue> disToSalesRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyIdFrom"), null, null, false);
    	if (UtilValidate.isNotEmpty(disToSalesRels)) {
    		salesExeIds = EntityUtil.getFieldListFromEntityList(disToSalesRels, "partyIdFrom", true);
    	}
    	
    	return salesExeIds;
    }
    public static List<String> getSalesmanIdsByAsm(Delegator delegator, String asmId) throws GenericEntityException {
    	List<String> salesmanIds = FastList.newInstance();
    	
    	List<String> asmDeptIds = getOrgByManager(delegator, asmId);
    	if (UtilValidate.isNotEmpty(asmDeptIds)) {
    		List<String> supDeptIds = getDeptOrgByParty(delegator, true, asmDeptIds, "INTERNAL_ORGANIZATIO", "SALESSUP_DEPT_GT", "GROUP_ROLLUP");
        	if (supDeptIds != null) {
    			// get list salesman ids
    			salesmanIds = getSalesmanIdsBySupDept(delegator, supDeptIds);
    		}
    	}
    	return salesmanIds;
    }
    public static List<String> getSalesmanIdsBySup(Delegator delegator, String supId) throws GenericEntityException{
    	List<String> salesmanIds = FastList.newInstance();
		
		// get list sup dept ids
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", supId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		List<String> supDeptIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		if (supDeptIds != null) {
			// get list salesman ids
			salesmanIds = getSalesmanIdsBySupDept(delegator, supDeptIds);
		}
		
		return salesmanIds;
    }
    public static List<String> getSalesmanIdsBySupDept(Delegator delegator, List<String> supDeptIds) throws GenericEntityException {
    	List<String> salesmanRoleTypeIds = SalesPartyUtil.getDescendantRoleTypeIds("SALESMAN_EMPL", delegator);
    	
    	List<String> salesmanIds = FastList.newInstance();
    	List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, supDeptIds));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, salesmanRoleTypeIds));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYEE"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		salesmanIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		return salesmanIds;
    }
    
    public static List<String> getDistributorIdsByAsm(Delegator delegator, String asmId) throws GenericEntityException {
    	List<String> salesmanIds = FastList.newInstance();
    	
    	List<String> asmDeptIds = getOrgByManager(delegator, asmId);
    	if (UtilValidate.isNotEmpty(asmDeptIds)) {
    		List<String> supDeptIds = getDeptOrgByParty(delegator, true, asmDeptIds, "INTERNAL_ORGANIZATIO", "SALESSUP_DEPT_GT", "GROUP_ROLLUP");
        	if (supDeptIds != null) {
    			// get list distributor ids
    			salesmanIds = getDistributorIdsBySupDept(delegator, supDeptIds);
    		}
    	}
    	return salesmanIds;
    }
    public static List<String> getDistributorIdsBySup(Delegator delegator, String supId) throws GenericEntityException {
    	List<String> distributorIds = FastList.newInstance();
    	//String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.representative");
		//String roleSalesExecutiveId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
		//String roleCustomerId = SalesUtil.getPropertyValue(delegator, "role.customer");
		
		// get list sup dept ids
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("partyIdFrom", supId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		List<String> supDeptIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		Debug.log(module + "::getDistributorIdsBySup, supId = " + supId + ", list.sz = " + supDeptIds.size());
		if (supDeptIds != null) {
			// get list distributor ids
			distributorIds = getDistributorIdsBySupDept(delegator, supDeptIds);
		}
		
		return distributorIds;
	}
    
    public static List<String> getDisOfRetailSalesAdminGT(Delegator delegator, String userLoginPartyId) throws GenericEntityException {
        //GenericValue productStoreIds = delegator.findOne("ProductStoreRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "SELLER"), false);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLoginPartyId, "roleTypeId", "SELLER")));
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> productStoreRoleSADs = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> listDistributor = FastList.newInstance();
		for (GenericValue ps : productStoreRoleSADs) {
			List<EntityCondition> conditions1 = FastList.newInstance();
			conditions1.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", ps.get("productStoreId"), "roleTypeId", "CUSTOMER")));
			//productStoreIds.add(ps.getString("productStoreId"));
			List<GenericValue> productStoreIdsOfCus = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conditions1), null, null, null, false);
			for(GenericValue pSC: productStoreIdsOfCus){
				listDistributor.add(pSC.getString("partyId"));
			}
		}
		return listDistributor;
	}
    
    public static List<String> getDistributorIdsBySupDept(Delegator delegator, List<String> supDeptIds) throws GenericEntityException {
    	List<String> distributorIds = FastList.newInstance();
    	List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, supDeptIds));
		distributorIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDistributor", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		return distributorIds;
    }
    
    public static List<String> getDistributorIdsBySupDept(Delegator delegator, String supDeptId) throws GenericEntityException {
    	List<String> distributorIds = FastList.newInstance();
    	List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("supervisorId", supDeptId));
		distributorIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDistributor", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		return distributorIds;
    }
    
    public static List<String> getCustomerIdsBySup(Delegator delegator, String supId) throws GenericEntityException {
    	List<String> customerIds = FastList.newInstance();
    	
		List<String> distributorIds = getDistributorIdsBySup(delegator, supId);
		if (UtilValidate.isNotEmpty(distributorIds)) {
				List<EntityCondition> listConds = FastList.newInstance();
				listConds.add(EntityCondition.makeCondition("distributorId",EntityOperator.IN, distributorIds));
				customerIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyCustomer", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		}
		return customerIds;
	}
    
    public static String getDistributorByCustomer(Delegator delegator, String customerId) throws GenericEntityException {
    	String distributorId;
    	GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", customerId), false);
    	distributorId = partyCustomer.getString("distributorId");
		return distributorId;
    }
    
    public static List<String> getSupIdsByDistributor(Delegator delegator, String distributorId) throws GenericEntityException {
    	List<String> supIds = FastList.newInstance();
    	GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", distributorId), false);
    	supIds.add(partyDistributor.getString("supervisorId"));
		return supIds;
	}
    
    public static List<String> getSupIdsByDistributor(Delegator delegator, List<String> distributorIds) throws GenericEntityException {
    	List<String> supIds;
    	EntityCondition condition = EntityCondition.makeCondition("partyId", EntityOperator.IN, distributorIds);
        supIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDistributor", condition, UtilMisc.toSet("supervisorId"), null, null, false), "supervisorId", true);
        return supIds;
    }
    
    public static List<String> getSupIdsByCustomer(Delegator delegator, String customerId) throws GenericEntityException {
    	List<String> supIds = FastList.newInstance();
    	
    	// get list distributor
    	String distributorId = getDistributorByCustomer(delegator, customerId);
    	
    	// get list sup id
    	if (UtilValidate.isNotEmpty(distributorId)) supIds = getSupIdsByDistributor(delegator, distributorId);
    	
    	return supIds;
    }
    
    public static List<String> getDeptOrgByParty(Delegator delegator, boolean isSearchByPartyIdFrom, String partyId, String roleTypeIdFrom, String roleTypeIdTo, String partyRelationshipTypeId) throws GenericEntityException {
    	List<String> deptOrgIds = FastList.newInstance();
    	if (UtilValidate.isEmpty(partyId)) return deptOrgIds;
    	String partySearchBy = "partyIdFrom";
    	String partyGetBy = "partyIdTo";
    	if (!isSearchByPartyIdFrom) {
    		partySearchBy = "partyIdTo";
    		partyGetBy = "partyIdFrom";
    	}
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition(partySearchBy, partyId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
		listConds.add(EntityUtil.getFilterByDateExpr());
		deptOrgIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet(partyGetBy), null, null, false), partyGetBy, true);
		return deptOrgIds;
    }
    public static List<String> getDeptOrgByParty(Delegator delegator, boolean isSearchByPartyIdFrom, List<String> partyIds, String roleTypeIdFrom, String roleTypeIdTo, String partyRelationshipTypeId) throws GenericEntityException {
    	List<String> deptOrgIds = FastList.newInstance();
    	if (UtilValidate.isEmpty(partyIds)) return deptOrgIds;
    	String partySearchBy = "partyIdFrom";
    	String partyGetBy = "partyIdTo";
    	if (!isSearchByPartyIdFrom) {
    		partySearchBy = "partyIdTo";
    		partyGetBy = "partyIdFrom";
    	}
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition(partySearchBy, EntityOperator.IN, partyIds));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
    	listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
    	listConds.add(EntityUtil.getFilterByDateExpr());
    	deptOrgIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet(partyGetBy), null, null, false), partyGetBy, true);
    	return deptOrgIds;
    }
    public static List<String> getOrgByManager(Delegator delegator, String managerId) throws GenericEntityException {
    	List<String> deptIds = FastList.newInstance();
    	if (UtilValidate.isEmpty(managerId)) return deptIds;
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdFrom", managerId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		deptIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		return deptIds;
    }
    public static List<String> getPartyMgrByDept(Delegator delegator, String deptId) throws GenericEntityException {
    	List<String> partyMrgIds = FastList.newInstance();
    	if (UtilValidate.isEmpty(deptId)) return partyMrgIds;
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdTo", deptId));
		listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
		listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
		listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
		listConds.add(EntityUtil.getFilterByDateExpr());
		partyMrgIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		return partyMrgIds;
    }
    public static List<String> getPartyMgrByDept(Delegator delegator, List<String> deptIds) throws GenericEntityException {
    	List<String> partyMrgIds = FastList.newInstance();
    	if (UtilValidate.isEmpty(deptIds)) return partyMrgIds;
    	
    	List<EntityCondition> listConds = FastList.newInstance();
    	listConds.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, deptIds));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "MANAGER"));
    	listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
    	listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
    	listConds.add(EntityUtil.getFilterByDateExpr());
    	partyMrgIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRelationship", EntityCondition.makeCondition(listConds, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
    	return partyMrgIds;
    }
    public static List<Map<String, Object>> getAllPartyPostalAddress(Delegator delegator, String partyId, String contactMechPurposeTypeId){
	List<Map<String, Object>> resList = FastList.newInstance();
	if(UtilValidate.isNotEmpty(partyId)){
			try {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), true);
				contactMechPurposeTypeId = UtilValidate.isEmpty(contactMechPurposeTypeId) ? "PRIMARY_LOCATION" : contactMechPurposeTypeId;
				List<GenericValue> contactMechs = (List<GenericValue>) ContactHelper.getContactMech(party, contactMechPurposeTypeId, "POSTAL_ADDRESS", false);
				for(GenericValue contactMech : contactMechs){
					Map<String, Object> cusMap = FastMap.newInstance();
					GenericValue postalAddress = contactMech.getRelatedOne(
							"PostalAddress", false);
					cusMap.put("address1", postalAddress.get("address1"));
					String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
					String districtGeoId = postalAddress.getString("districtGeoId");
					if(UtilValidate.isNotEmpty(stateProvinceGeoId)){
						cusMap.put("stateProvinceGeoId", stateProvinceGeoId);
						GenericValue city = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),  true);
						cusMap.put("city", city.getString("geoName"));
					}
					if(UtilValidate.isNotEmpty(districtGeoId)){
						cusMap.put("districtGeoId", districtGeoId);
						GenericValue district = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId),  true);
						cusMap.put("district", district.getString("geoName"));
					}
					resList.add(cusMap);
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			}
	}
	return resList;
    }
    public static List<GenericValue> getAllPartyPostalAddressValue(Delegator delegator, String partyId, String contactMechPurposeTypeId){
	List<GenericValue> addresses = FastList.newInstance();
	if(UtilValidate.isNotEmpty(partyId)){
				try {
					contactMechPurposeTypeId = UtilValidate.isEmpty(contactMechPurposeTypeId) ? "PRIMARY_LOCATION" : contactMechPurposeTypeId;
					addresses = delegator.findList("PartyAndPostalAddressDetail",
							EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId),
									EntityUtil.getFilterByDateExpr())),
							null, UtilMisc.toList("-contactMechId"), null, false);
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
		}
		return addresses;
    }
    public static List<GenericValue> getAllPartyPostalAddressValue(Delegator delegator, List<String> partyId, String contactMechPurposeTypeId){
	List<GenericValue> addresses = FastList.newInstance();
	if(UtilValidate.isNotEmpty(partyId)){
			try {
				contactMechPurposeTypeId = UtilValidate.isEmpty(contactMechPurposeTypeId) ? "PRIMARY_LOCATION" : contactMechPurposeTypeId;
				addresses = delegator.findList("PartyAndPostalAddressDetail",
						EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyId),
								EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId),
								EntityUtil.getFilterByDateExpr())),
						null, UtilMisc.toList("-contactMechId"), null, false);
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			}
		}
		return addresses;
    }
	public static List<String> getAllPartyPostalAddressField(Delegator delegator, String partyId, String contactMechPurposeTypeId, String fieldName, boolean distinct){
		List<String> fields = FastList.newInstance();
		if(UtilValidate.isNotEmpty(partyId)){
				try {
					contactMechPurposeTypeId = UtilValidate.isEmpty(contactMechPurposeTypeId) ? "PRIMARY_LOCATION" : contactMechPurposeTypeId;
					List<GenericValue> addresses = delegator.findList("PartyAndPostalAddressDetail",
							EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId),
									EntityUtil.getFilterByDateExpr())),
							null, UtilMisc.toList("-contactMechId"), null, false);
					fields = EntityUtil.getFieldListFromEntityList(addresses, fieldName, distinct);
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
		}
		return fields;
    }
	public static List<String> getAllPartyPostalAddressField(Delegator delegator, List<String> partyId, String contactMechPurposeTypeId, String fieldName, boolean distinct){
		List<String> fields = FastList.newInstance();
		if(UtilValidate.isNotEmpty(partyId)){
				try {
					contactMechPurposeTypeId = UtilValidate.isEmpty(contactMechPurposeTypeId) ? "PRIMARY_LOCATION" : contactMechPurposeTypeId;
					List<GenericValue> addresses = delegator.findList("PartyAndPostalAddressDetail",
							EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyId),
									EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId),
									EntityUtil.getFilterByDateExpr())),
							null, UtilMisc.toList("-contactMechId"), null, false);
					fields = EntityUtil.getFieldListFromEntityList(addresses, fieldName, distinct);
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
		}
		return fields;
    }
	
	public static String getFullNamePostalAddress(Delegator delegator, String contactMechId) throws GenericEntityException {
		String successReturn = null;
		GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId), false);
		if (postalAddress != null) {
			StringBuilder postalAddressName = new StringBuilder();
			postalAddressName.append(UtilFormatOut.ifNotEmpty(postalAddress.getString("address1"), "", ", "));
			
			GenericValue geoWard = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.get("wardGeoId")), false);
			if (geoWard != null && !"_NA_".equals(geoWard.getString("geoId"))) {
				postalAddressName.append(UtilFormatOut.ifNotEmpty(geoWard.getString("geoName"), "", ", "));
			}
			GenericValue geoDistrict = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.get("districtGeoId")), false);
			if (geoDistrict != null && !"_NA_".equals(geoDistrict.getString("geoId"))) {
				postalAddressName.append(UtilFormatOut.ifNotEmpty(geoDistrict.getString("geoName"), "", ", "));
			}
			GenericValue geoProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.get("stateProvinceGeoId")), false);
			if (geoProvince != null && !"_NA_".equals(geoProvince.getString("geoId"))) {
				postalAddressName.append(UtilFormatOut.ifNotEmpty(geoProvince.getString("geoName"), "", ", "));
			}
			GenericValue geoCountry = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.get("countryGeoId")), false);
			if (geoCountry != null && !"_NA_".equals(geoCountry.getString("geoId"))) {
				postalAddressName.append(UtilFormatOut.ifNotEmpty(geoCountry.getString("geoName"), "", ""));
			}
			successReturn = postalAddressName.toString();
		}
		return successReturn;
	}

    public static List<String> distributorOfSupervisorEnable(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
		listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLogin.get("partyId")));
		List<GenericValue> partyDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyDistributors, "partyId", true);
	}

    public static List<String> getCustOfSalesman(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
        List<EntityCondition> listAllConditions = FastList.newInstance();
        listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
        listAllConditions.add(EntityCondition.makeCondition("salesmanId", userLogin.get("partyId")));
        List<GenericValue> partyDistributors = delegator.findList("PartyCustomer", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        return EntityUtil.getFieldListFromEntityList(partyDistributors, "partyId", true);
    }
    
    
    /**
     * Get distributor by supervisor manager
     * @param delegator
     * @param supIds
     * @return
     */
	public static List<String> getDistributorBySupervisor(Delegator delegator, String supId) {
		List<String> resultValue = new ArrayList<String>();
		// get distributor by SUP.manager
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("supervisorId", supId));
			conditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyDistributor", EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getDistributor method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return resultValue;
	}
	
    /**
     * Get distributor by list of supervisor manager
     * @param delegator
     * @param supIds
     * @return
     */
	public static List<String> getDistributorBySupervisor(Delegator delegator, List<String> supIds) {
		List<String> resultValue = new ArrayList<String>();
		// get distributor by SUP.manager
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, supIds));
			conditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyDistributor", EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getDistributor method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return resultValue;
	}
	
	/**
	 * Get department of SUP from ASM.dept
	 * @param delegator
	 * @param partyASMDeptId
	 * @param partyASMDeptIds
	 * @return
	 */
	public static List<String> getSupDeptByASMDept(Delegator delegator, String partyASMDeptId, List<String> partyASMDeptIds) {
		List<String> resultValue = new ArrayList<String>();
		boolean isSingle = true;
		if (partyASMDeptId == null) isSingle = false;
		
		// get department of SUP from ASM.dept
		//<PartyRelationship partyIdFrom="ASM_GT_R1A" partyIdTo="SUP_GT_R1A_0001" 
		//roleTypeIdFrom="INTERNAL_ORGANIZATIO" roleTypeIdTo="SALESSUP_DEPT_GT" 
		//fromDate="2014-01-01 00:00:00.0" partyRelationshipTypeId="GROUP_ROLLUP"/> 
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			if (isSingle) {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyASMDeptId));
			} else {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyASMDeptIds));
			}
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", "SALESSUP_DEPT_GT"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getSupDeptByASMDept method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}
	
	/**
	 * Get department of ASM from RSM.dept
	 * @param delegator
	 * @param partyRSMDeptId
	 * @param partyRSMDeptIds
	 * @return
	 */
	public static List<String> getASMDeptByRSMDept(Delegator delegator, String partyRSMDeptId, List<String> partyRSMDeptIds) {
		List<String> resultValue = new ArrayList<String>();
		boolean isSingle = true;
		if (partyRSMDeptId == null) isSingle = false;
		
		// get department of ASM from RSM.dept
		//<PartyRelationship partyIdFrom="RSM_GT_R1" partyIdTo="ASM_GT_R1A" 
		//roleTypeIdFrom="RSM_DEPT_GT" roleTypeIdTo="ASM_DEPT_GT" 
		//fromDate="2014-01-01 00:00:00.0" partyRelationshipTypeId="GROUP_ROLLUP"/>
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			if (isSingle) {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyRSMDeptId));
			} else {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyRSMDeptIds));
			}
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", "RSM_DEPT_GT"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", "ASM_DEPT_GT"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getASMDeptByRSMDept method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}
	
	/**
	 * Get department of RSM from CSM.dept
	 * @param delegator
	 * @param partyCSMDeptId
	 * @param partyCSMDeptIds
	 * @return
	 */
	public static List<String> getRSMDeptByCSMDept(Delegator delegator, String partyCSMDeptId, List<String> partyCSMDeptIds) {
		List<String> resultValue = new ArrayList<String>();
		boolean isSingle = true;
		if (partyCSMDeptId == null) isSingle = false;
		
		// get department of RSM from CSM.dept
		//<PartyRelationship partyIdFrom="CSM_MB_GT" partyIdTo="RSM_GT_R1" 
		//roleTypeIdFrom="INTERNAL_ORGANIZATIO" roleTypeIdTo="RSM_DEPT_GT" 
		//fromDate="2014-01-01 00:00:00.0" partyRelationshipTypeId="GROUP_ROLLUP"/>
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			if (isSingle) {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyCSMDeptId));
			} else {
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyCSMDeptIds));
			}
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", "RSM_DEPT_GT"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyIdTo"), null, null, false), "partyIdTo", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getRSMDeptByCSMDept method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}
	
	/**
	 * Get distributor by salesman
	 * @param delegator
	 * @param partySMId
	 * @return
	 */
	public static List<String> getDistributorBySalesman(Delegator delegator, String partySMId) {
		List<String> resultValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(partySMId)) return resultValue;
		
		// get distributor by salesman
		try {
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartySalesman", EntityCondition.makeCondition("partyId", partySMId), UtilMisc.toSet("distributorId"), null, null, false), "distributorId", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getDistributorBySalesman method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}
	
	/**
	 * Get distributor by salesAdmin
	 * @param delegator
	 * @param partySMId
	 * @return
	 */
	public static List<String> getDistributorBySalesadmin(Delegator delegator, String partySADId) {
		List<String> resultValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(partySADId)) return resultValue;
		
		// get distributor by salesAdmin
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyId", partySADId));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeId", "SELLER"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(
					delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			if (UtilValidate.isEmpty(productStoreIds)) return resultValue;
			
			listAllCondition.clear();
			listAllCondition.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
			listAllCondition.add(EntityUtil.getFilterByDateExpr());
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getDistributorBySalesadmin method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}

	/**
	 * Get distributor by organization
	 * @param delegator
	 * @param orgId
	 * @return
	 */
	public static List<String> getDistributorByOrg(Delegator delegator, String orgId , Boolean allStatus) {
		List<String> resultValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(orgId)) return resultValue;
		if (allStatus == null) allStatus = false;
		
		// get department of ASM from RSM.dept
		//<PartyRelationship partyIdFrom="NPP01A01" partyIdTo="MB" 
		//roleTypeIdFrom="DISTRIBUTOR" roleTypeIdTo="INTERNAL_ORGANIZATIO" 
		//fromDate="2014-01-01 00:00:00.0" partyRelationshipTypeId="DISTRIBUTOR_REL"/>  
		try {
			List<EntityCondition> listAllCondition = FastList.newInstance();
			listAllCondition.add(EntityCondition.makeCondition("partyIdTo", orgId));
			listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", "DISTRIBUTOR_REL"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", "DISTRIBUTOR"));
			listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			if ( allStatus ){
				listAllCondition.add(EntityUtil.getFilterByDateExpr());
			}
			resultValue = EntityUtil.getFieldListFromEntityList(
					delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("partyIdFrom"), null, null, false), "partyIdFrom", true);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when run getDistributorByOrg method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return resultValue;
	}
	
	/**
	 * Get supervisor by ASM person
	 * @param delegator
	 * @param partyASMId
	 * @return
	 */
	public static List<String> getSupervisorByASM(Delegator delegator, String partyASMId) {
		List<String> resultValue = new ArrayList<String>();
		// get department ASM has this party is manager
		String deptId = SalesPartyUtil.getOrgIdManagedByParty(delegator, partyASMId);
		if (UtilValidate.isEmpty(deptId)) return resultValue;
		
		// get department of SUP from ASM.dept
		List<String> supDeptIds = getSupDeptByASMDept(delegator, deptId, null);
		if (UtilValidate.isEmpty(supDeptIds)) return resultValue;
		
		// get SUP.manager in SUP.dept
		for (String supDeptId : supDeptIds) {
			List<String> supIdItems = SalesPartyUtil.getManagerIdOfDept(delegator, supDeptId);
			if (supIdItems != null) resultValue.addAll(supIdItems);
		}
		
		return resultValue;
	}
	
	/**
	 * Get supervisor by RSM person
	 * @param delegator
	 * @param partyRSMId
	 * @return
	 */
	public static List<String> getSupervisorByRSM(Delegator delegator, String partyRSMId) {
		List<String> resultValue = new ArrayList<String>();
		// get department RSM has this party is manager
		String deptId = SalesPartyUtil.getOrgIdManagedByParty(delegator, partyRSMId);
		if (UtilValidate.isEmpty(deptId)) return resultValue;
		
		// get department of ASM from RSM.dept
		List<String> asmDeptIds = getASMDeptByRSMDept(delegator, deptId, null);
		if (UtilValidate.isEmpty(asmDeptIds)) return resultValue;
		
		// get department of SUP from ASM.dept
		List<String> supDeptIds = getSupDeptByASMDept(delegator, null, asmDeptIds);
		if (UtilValidate.isEmpty(supDeptIds)) return resultValue;
		
		// get SUP.manager in SUP.dept
		for (String supDeptId : supDeptIds) {
			List<String> supIdItems = SalesPartyUtil.getManagerIdOfDept(delegator, supDeptId);
			if (supIdItems != null) resultValue.addAll(supIdItems);
		}
		
		return resultValue;
	}
	
	/**
	 * Get supervisor by CSM person
	 * @param delegator
	 * @param partyCSMId
	 * @return
	 */
	public static List<String> getSupervisorByCSM(Delegator delegator, String partyCSMId) {
		List<String> resultValue = new ArrayList<String>();
		// get department CSM has this party is manager
		String deptId = SalesPartyUtil.getOrgIdManagedByParty(delegator, partyCSMId);
		if (UtilValidate.isEmpty(deptId)) return resultValue;
		
		// get department of RSM from CSM.dept
		List<String> rsmDeptIds = getRSMDeptByCSMDept(delegator, deptId, null);
		if (UtilValidate.isEmpty(rsmDeptIds)) return resultValue;
		
		// get department of ASM from RSM.dept
		List<String> asmDeptIds = getASMDeptByRSMDept(delegator, null, rsmDeptIds);
		if (UtilValidate.isEmpty(asmDeptIds)) return resultValue;
		
		// get department of SUP from ASM.dept
		List<String> supDeptIds = getSupDeptByASMDept(delegator, null, asmDeptIds);
		if (UtilValidate.isEmpty(supDeptIds)) return resultValue;
		
		// get SUP.manager in SUP.dept
		for (String supDeptId : supDeptIds) {
			List<String> supIdItems = SalesPartyUtil.getManagerIdOfDept(delegator, supDeptId);
			if (supIdItems != null) resultValue.addAll(supIdItems);
		}
		
		return resultValue;
	}

	/**
	 * Get distributor by ASM person
	 * @param delegator
	 * @param partyASMId
	 * @return
	 */
	public static List<String> getDistributorByASM(Delegator delegator, String partyASMId) {
		List<String> resultValue = new ArrayList<String>();
		
		// get SUP.manager by ASM.manager
		List<String> supIds = getSupervisorByASM(delegator, partyASMId);
		
		// get distributor by SUP.manager
		resultValue = getDistributorBySupervisor(delegator, supIds);
		
		return resultValue;
	}
	
	/**
	 * Get distributor by RSM person
	 * @param delegator
	 * @param partyRSMId
	 * @return
	 */
	public static List<String> getDistributorByRSM(Delegator delegator, String partyRSMId) {
		List<String> resultValue = new ArrayList<String>();
		
		// get SUP.manager by ASM.manager
		List<String> supIds = getSupervisorByRSM(delegator, partyRSMId);
		
		// get distributor by SUP.manager
		resultValue = getDistributorBySupervisor(delegator, supIds);
		
		return resultValue;
	}
	
	/**
	 * Get distributor by CSM person
	 * @param delegator
	 * @param partyRSMId
	 * @return
	 */
	public static List<String> getDistributorByCSM(Delegator delegator, String partyRSMId) {
		List<String> resultValue = new ArrayList<String>();
		
		// get SUP.manager by ASM.manager
		List<String> supIds = getSupervisorByCSM(delegator, partyRSMId);
		
		// get distributor by SUP.manager
		resultValue = getDistributorBySupervisor(delegator, supIds);
		
		return resultValue;
	}
	
}
