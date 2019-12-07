package com.olbius.basepos.jqservices;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class SalesOrderHistory {
	public static final String module = SalesOrderHistory.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetSalesOrderHistory(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		
		String[] createdBys = parameters.get("createdBy");
		String createdBy = null;
		if (UtilValidate.isNotEmpty(createdBys)) {
			createdBy = createdBys[0];
		}
		// Get all sales employees with EXECUTIVE role
		List<EntityCondition> tmpList = new ArrayList<>();
		List<EntityCondition> tmpThruDateList = new ArrayList<>();
		HttpServletRequest request = (HttpServletRequest) context.get("request");
		Date tmpDate = new Date();
		EntityCondition eca = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
		EntityCondition ecb = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(tmpDate.getTime()));
		tmpThruDateList.add(eca);
		tmpThruDateList.add(ecb);
		
		EntityCondition ec1 = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, (String) request.getSession().getAttribute("productStoreId"));
		EntityCondition ec2 = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SELLER");
		EntityCondition ec3 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(tmpDate.getTime()));
		tmpList.add(ec1);
		tmpList.add(ec2);
		tmpList.add(ec3);
		tmpList.add(EntityCondition.makeCondition(tmpThruDateList, EntityJoinOperator.OR));
		List<GenericValue> sellerList = null;
		try {
			sellerList = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(tmpList, EntityJoinOperator.AND), UtilMisc.toSet("partyId"), null, null, false);
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling jqGetSalesOrderHistory service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
		}
		EntityCondition ccITI1;
		EntityCondition ccITI2;
		if(sellerList != null && !sellerList.isEmpty()){
			List<String> listSeller = new ArrayList<>();
			for (GenericValue seller : sellerList) {
				listSeller.add(seller.getString("partyId"));
			}
			listSeller.add(createdBy);
			ccITI1 = EntityCondition.makeCondition("returnCreatedBy", EntityOperator.IN, listSeller);
			ccITI2 = EntityCondition.makeCondition("createdBy", EntityOperator.IN, listSeller);
		}else{
			ccITI1 = EntityCondition.makeCondition("returnCreatedBy", EntityOperator.EQUALS, createdBy);
			ccITI2 = EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, createdBy);
		}
		EntityCondition creatCondition = EntityCondition.makeCondition(EntityJoinOperator.OR, ccITI1, ccITI2);
		listAllConditions.add(creatCondition);
		EntityCondition allCondition = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);

		listSortFields.add("-posTerminalLogId");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("-posTerminalLogId");
//			listSortFields.add("-returnDate");
//			listSortFields.add("-orderDate");
		}
		opts.setDistinct(true);
		// FIXME The list show all order, return from other product store
		try {
			opts.setLimit(20);
			Long totalRows = delegator.findCountByCondition("PosHistory", allCondition, null, null);
			successResult.put("TotalRows", totalRows.toString());
			listIterator = delegator.findList("PosHistory", allCondition, null, listSortFields, opts, false); // OrderHeaderAndRoleTypeAndReturn
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetSalesOrderHistory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListPromotion(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityCondition storeCondition = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,
				parameters.get("productStoreId")[0]);
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(storeCondition);
		listAllConditions.add(tmpConditon);
		listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listSortFields.add("-fromDate");
		try {
			listIterator = delegator.find("ProductStorePromoApplFilter",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListPromotion service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

}
