package com.olbius.basepos.jqservices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class PlanPOJQServices {
	public static Map<String, Object> jqListPlanPOItem(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException, ParseException{
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<Map<String, Object>> listPlanPOItem = FastList.newInstance();
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;
		int totalRows = 0;
		String[] orderIds = parameters.get("orderId");
		String orderId = null;
		if(UtilValidate.isNotEmpty(orderIds)){
				orderId = orderIds[0];
		}
		if(UtilValidate.isNotEmpty(orderId)){
			GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if(UtilValidate.isNotEmpty(order)){
				String planPOId = order.getString("planPOId");
				EntityCondition mainCond = EntityCondition.makeCondition("planPOId", EntityOperator.EQUALS, planPOId);
				List<GenericValue> listItems = delegator.findList("PlanPurchaseOrderItem", mainCond, null, null, null, false);
				if(UtilValidate.isNotEmpty(listItems)){
					for (GenericValue item : listItems) {
						Map<String, Object> planPOItem = FastMap.newInstance();
						planPOItem.put("productId", item.getString("productId"));
						planPOItem.put("productName", item.getString("productName"));
					
						BigDecimal qpd = item.getBigDecimal("qpd") == null?BigDecimal.ZERO : item.getBigDecimal("qpd");
						BigDecimal qoh = item.getBigDecimal("qoh") == null?BigDecimal.ZERO : item.getBigDecimal("qoh");
						BigDecimal qoo = item.getBigDecimal("qoo") == null?BigDecimal.ZERO : item.getBigDecimal("qoo");
						BigDecimal qtyBox = item.getBigDecimal("qtyBox") == null?BigDecimal.ZERO : item.getBigDecimal("qtyBox");
						BigDecimal qtyPic = item.getBigDecimal("qtyPic") == null?BigDecimal.ZERO : item.getBigDecimal("qtyPic");
						BigDecimal pickStandard = item.getBigDecimal("pickStandard") == null?BigDecimal.ZERO : item.getBigDecimal("pickStandard");
						planPOItem.put("qpd", qpd);
						planPOItem.put("qoh", qoh);
						planPOItem.put("qoo", qoo);
						BigDecimal sysTotal = BigDecimal.ZERO;
						if(UtilValidate.isNotEmpty(qoh)){
							sysTotal = sysTotal.add(qoh);
						}
						if(UtilValidate.isNotEmpty(qoo)){
							sysTotal = sysTotal.add(qoo);
						}
						BigDecimal sysLid = BigDecimal.ZERO;
						BigDecimal totalLid = BigDecimal.ZERO;
						
						planPOItem.put("lastReceived", item.getTimestamp("lastReceived"));
						planPOItem.put("lastSold", item.getTimestamp("lastSold"));
						planPOItem.put("qtyBox", qtyBox);
						planPOItem.put("qtyPic", qtyPic);
						planPOItem.put("pickStandard", pickStandard);
						BigDecimal totalPO = BigDecimal.ZERO;
						if(UtilValidate.isNotEmpty(qtyPic)){
							totalPO = totalPO.add(qtyPic);
						}
						if(UtilValidate.isNotEmpty(qtyBox) && UtilValidate.isNotEmpty(pickStandard)){
							BigDecimal totalPic = qtyBox.multiply(pickStandard);
							totalPO = totalPO.add(totalPic);
						}
						
						planPOItem.put("totalPO", totalPO);
						if(UtilValidate.isNotEmpty(qpd) && (qpd.compareTo(BigDecimal.ZERO) != 0)){
							sysLid = sysTotal.divide(qpd,1, RoundingMode.HALF_UP);
							totalLid = totalPO.divide(qpd,1, RoundingMode.HALF_UP);
						}
						planPOItem.put("sysLid", sysLid);
						planPOItem.put("totalLid", totalLid);
						
						BigDecimal unitCost = item.getBigDecimal("unitCost") == null?BigDecimal.ZERO :item.getBigDecimal("unitCost") ;
						
						planPOItem.put("unitCost", unitCost);
						BigDecimal totalCost = totalPO.multiply(unitCost);
								
						planPOItem.put("totalItemCost", totalCost);
						
						Map<String, String> mapCond = FastMap.newInstance();
						mapCond.put("orderId", orderId);
						mapCond.put("productId", item.getString("productId"));
						List<GenericValue> listComment = delegator.findByAnd("OrderItem", mapCond, UtilMisc.toList("orderId"), false);
						String comment = "";
						if(UtilValidate.isNotEmpty(listComment)){
							comment = listComment.get(0).getString("comments");
						}
						planPOItem.put("comments", comment);
						
						listPlanPOItem.add(planPOItem);
					}
				}
			}
		}
		if(end > listPlanPOItem.size()){
			end = listPlanPOItem.size();
		}
		listPlanPOItem = listPlanPOItem.subList(start, end);
	    totalRows = listPlanPOItem.size(); 
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listPlanPOItem);
		return successResult;
	}	
	
	public static Map<String, Object> JQViewPlanPOItemDetail(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException, ParseException{
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<Map<String, Object>> listPlanPOItem = FastList.newInstance();
		String[] productLists = parameters.get("productId");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;
		int totalRows = 0;
		String[] orderIds = parameters.get("orderId");
		String productId = null;
		String orderId = null;
		if(UtilValidate.isNotEmpty(productLists)){
				productId = productLists[0];
		}
		if(UtilValidate.isNotEmpty(orderIds)){
			orderId = orderIds[0];
		}
		if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(orderId)){
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if(UtilValidate.isNotEmpty(orderHeader)){
				String planPOId = orderHeader.getString("planPOId");
				if(UtilValidate.isNotEmpty(planPOId)){
					Map<String, String> mapCond = FastMap.newInstance();
					mapCond.put("productId", productId);
					mapCond.put("planPOId", planPOId);
					List<GenericValue> listPlanPOItemFacility = delegator.findByAnd("PlanPurchaseOrderItemFacility", mapCond, UtilMisc.toList("facilityId"), false);
					if(UtilValidate.isNotEmpty(listPlanPOItemFacility)){
						for (GenericValue pOItemFacility : listPlanPOItemFacility) {
							Map<String, Object> mapRowDetail = FastMap.newInstance();
							BigDecimal totalQuantity = BigDecimal.ZERO;
							String facilityId = pOItemFacility.getString("facilityId");
							String facilityName = pOItemFacility.getString("facilityName");
							BigDecimal qpd = pOItemFacility.getBigDecimal("qpd") == null ? BigDecimal.ZERO : pOItemFacility.getBigDecimal("qpd");
							BigDecimal qoh = pOItemFacility.getBigDecimal("qoh") == null ? BigDecimal.ZERO : pOItemFacility.getBigDecimal("qoh");
							BigDecimal qpo = pOItemFacility.getBigDecimal("qpo") == null ? BigDecimal.ZERO : pOItemFacility.getBigDecimal("qpo");
							totalQuantity = totalQuantity.add(qoh);
							totalQuantity = totalQuantity.add(qpo);
							BigDecimal lid = BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(qpd) && (qpd.compareTo(BigDecimal.ZERO) != 0)){
								lid = totalQuantity.divide(qpd, 1, RoundingMode.HALF_UP);
							}
							mapRowDetail.put("productId", productId);
							mapRowDetail.put("facilityId", facilityId);
							mapRowDetail.put("facilityName", facilityName);
							mapRowDetail.put("qohDetail", qoh);
							mapRowDetail.put("qpdDetail", qpd);
							mapRowDetail.put("poQuantity", qpo);
							mapRowDetail.put("facilityLid", lid);
							listPlanPOItem.add(mapRowDetail);
						}
					}
				}
				
			}
		}
		if(end > listPlanPOItem.size()){
			end = listPlanPOItem.size();
		}
		listPlanPOItem = listPlanPOItem.subList(start, end);
	    totalRows = listPlanPOItem.size(); 
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listPlanPOItem);
		return successResult;
	}
}
