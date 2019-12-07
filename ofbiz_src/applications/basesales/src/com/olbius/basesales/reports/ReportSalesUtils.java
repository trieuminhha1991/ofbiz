package com.olbius.basesales.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.bi.olap.query.condition.Condition;

import javolution.util.FastMap;

public class ReportSalesUtils {
	private final static String module = ReportSalesUtils.class.getName(); 
	
	public static List<Map<String, Object>> getProductSalesOrder(Delegator delegator){
		List<Map<String, Object>> resultValue = new ArrayList<Map<String, Object>>();
		try {
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			List<GenericValue> prodOrder = delegator.findList("SalesOrderNewFact", null, UtilMisc.toSet("productDimId"), null, opts, false);
			if (UtilValidate.isNotEmpty(prodOrder)) {
				for (GenericValue prod : prodOrder) {
					GenericValue prodDimension = delegator.findOne("ProductDimension", UtilMisc.toMap("dimensionId", prod.get("productDimId")), false);
					if (prodDimension != null) {
						Map<String, Object> itemMap = FastMap.newInstance();
						itemMap.put("productId", prodDimension.get("productId"));
						itemMap.put("productCode", prodDimension.get("productCode"));
						itemMap.put("productName", prodDimension.get("productName"));
						resultValue.add(itemMap);
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError("Error when query ", module);
		}
		return resultValue;
	}
	
	public static Condition makeCondFindByCreatorSO(Delegator delegator, String userLoginPartyId) {
		if (delegator == null || userLoginPartyId == null) {
			return Condition.make("SOF.creator_dim_id = ''");
		}
		Long creatorDimId = null;
		if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isCallCenterManager(delegator, userLoginPartyId)) {
			return null;
		} else {
			try {
				GenericValue creatorDim = EntityUtil.getFirst(delegator.findByAnd("PartyDimension", UtilMisc.toMap("partyId", userLoginPartyId), null, false));
				if (creatorDim != null) {
					creatorDimId = creatorDim.getLong("dimensionId");
				}
			} catch (GenericEntityException e) {
				Debug.logWarning("Error when get dim of party", module);
			}
			if (creatorDimId == null) return Condition.make("SOF.creator_dim_id = ''");
			return Condition.makeEQ("SOF.creator_dim_id", creatorDimId);
		}
	}
	
	public static Condition makeCondFindByCreatorSO2(Delegator delegator, String userLoginPartyId) {
		Condition nullCond = Condition.make("SOF.creator_dim_id is null");
		if (delegator == null || userLoginPartyId == null) {
			return nullCond;
		}
		try {
			List<String> deptIdsManaged = PartyWorker.getOrgByManager(delegator, userLoginPartyId);
			if (UtilValidate.isNotEmpty(deptIdsManaged)) {
				// find all order of employee in depts
				/*List<Long> deptDimIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDimension", EntityCondition.makeCondition("partyId", EntityOperator.IN, deptIdsManaged), UtilMisc.toSet("dimensionId"), null, null, false), "dimensionId", true);
				if (UtilValidate.isNotEmpty(deptDimIds)) {
					StringBuilder sql = new StringBuilder();
					sql.append("SELECT PEOREL.dimension_id FROM party_empl_org_eee_relationship AS PEOREL ");
					sql.append("WHERE PEOREL.role_type_id = 'SALES_EMPLOYEE' AND SOF.creator_dim_id = PEOREL.dimension_id AND PEOREL.parent_dim_id ");
				    if (deptDimIds.size() == 1) {
				    	sql.append("= " + deptDimIds.get(0).toString());
					} else {
						sql.append("IN (");
						for (int i = 0; i < deptDimIds.size(); i++) {
							sql.append(deptDimIds.get(i).toString());
							if (i < deptDimIds.size() - 1) {
								sql.append(",");
							} else {
								sql.append(")");
							}
						}
					}
					Condition cond = Condition.make("EXISTS (" + sql.toString() + ")");
					return cond;
				}
				return nullCond;*/
				return null;
			} else {
				// find order of only employee
				Long creatorDimId = null;
				GenericValue creatorDim = EntityUtil.getFirst(delegator.findByAnd("PartyDimension", UtilMisc.toMap("partyId", userLoginPartyId), null, false));
				if (creatorDim != null) creatorDimId = creatorDim.getLong("dimensionId");
				if (creatorDimId == null) return nullCond;
				return Condition.makeEQ("SOF.creator_dim_id", creatorDimId);
			}
		} catch (GenericEntityException e) {
			Debug.logWarning("Error when get dim of party: " + e, module);
		}
		
		return nullCond;
	}
	
	public static Condition makeCondFindByProdStoreRole(Delegator delegator, String partyId, String asTableName) {
		return makeCondFindByProdStoreRole(delegator, partyId, asTableName, false);
	}
	
	public static Condition makeCondFindByProdStoreRoleAndDist(Delegator delegator, String partyId, String asTableName, boolean isViewPartner, List<String> distributorIds) throws GenericEntityException {
		Condition nullCond = Condition.make(asTableName + ".product_store_dim_id is null");
		if (UtilValidate.isEmpty(distributorIds)) return nullCond; 
		
		if (isViewPartner) {
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStore", EntityCondition.makeCondition("payToPartyId", EntityOperator.IN, distributorIds), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				List<String> productStoreDimIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreDimension", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), UtilMisc.toSet("dimensionId"), null, null, false), "dimensionId", true);
				if (UtilValidate.isNotEmpty(productStoreDimIds)) {
					String condStoreIds = StringUtil.join(productStoreDimIds, "', '");
					nullCond = Condition.make(asTableName + ".product_store_dim_id IN ('" + condStoreIds + "')");
				}
			}
		} else {
			List<String> distDimIds = EntityUtil.getFieldListFromEntityList(delegator.findList("PartyDimension", 
					EntityCondition.makeCondition("partyId", EntityOperator.IN, distributorIds), UtilMisc.toSet("dimensionId"), null, null, false), "dimensionId", true);
			if (UtilValidate.isNotEmpty(distDimIds)) {
				String condDistIds = StringUtil.join(distDimIds, "', '");
				nullCond = Condition.make(asTableName + ".customer_dim_id IN ('" + condDistIds + "')");
			}
		}
		
		return nullCond;
	}
	
	public static Condition makeCondFindByProdStoreRole(Delegator delegator, String partyId, String asTableName, boolean isViewPartner) {
		Condition nullCond = Condition.make(asTableName + ".product_store_dim_id is null");
		if (delegator == null || partyId == null) {
			return nullCond;
		}
		try {
			if (SalesPartyUtil.isSalesManager(delegator, partyId)) {
				return null;
			} else if (SalesPartyUtil.isSalesCSM(delegator, partyId)) {
				List<String> distributorIds = PartyWorker.getDistributorByCSM(delegator, partyId);
				nullCond = makeCondFindByProdStoreRoleAndDist(delegator, partyId, asTableName, isViewPartner, distributorIds);
			} else if (SalesPartyUtil.isSalesRSM(delegator, partyId)) {
				List<String> distributorIds = PartyWorker.getDistributorByRSM(delegator, partyId);
				nullCond = makeCondFindByProdStoreRoleAndDist(delegator, partyId, asTableName, isViewPartner, distributorIds);
			} else if (SalesPartyUtil.isSalesASM(delegator, partyId)) {
				List<String> distributorIds = PartyWorker.getDistributorByASM(delegator, partyId);
				nullCond = makeCondFindByProdStoreRoleAndDist(delegator, partyId, asTableName, isViewPartner, distributorIds);
			} else if (SalesPartyUtil.isSalessup(delegator, partyId)) {
				List<String> distributorIds = PartyWorker.getDistributorBySupervisor(delegator, partyId);
				nullCond = makeCondFindByProdStoreRoleAndDist(delegator, partyId, asTableName, isViewPartner, distributorIds);
			} else {
				// get list product store by role
				List<String> roleTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "role.view.sales.report");
				
				if (UtilValidate.isNotEmpty(roleTypeIds)) {
					List<EntityCondition> conds = new ArrayList<EntityCondition>();
					conds.add(EntityCondition.makeCondition("partyId", partyId));
					conds.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds));
					conds.add(EntityUtil.getFilterByDateExpr());
					List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conds), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
					if (UtilValidate.isNotEmpty(productStoreIds)) {
						List<String> productStoreDimIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreDimension", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), UtilMisc.toSet("dimensionId"), null, null, false), "dimensionId", true);
						if (UtilValidate.isNotEmpty(productStoreDimIds)) {
							String condStoreIds = StringUtil.join(productStoreDimIds, "', '");
							nullCond = Condition.make(asTableName + ".product_store_dim_id IN ('" + condStoreIds + "')");
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logWarning("Error when call function makeCondFindByProdStoreRole: " + e, module);
		}
		
		return nullCond;
	}
}
