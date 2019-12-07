package com.olbius.importServices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.DelysServices;

public class ImportAccServices {
	
	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;
	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}
	public static final String module = DelysServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	
	
	public static Map<String, Object> JQGetBillForAcc(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	SimpleDateFormat yearMonthDayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    	Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String party = (String)param.get("party")[0];

    	List<GenericValue> listGe = new ArrayList<GenericValue>();
    	try {
    		listGe = delegator.findList("BillOfLading", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		List<GenericValue> listBL = EntityUtil.filterByDate(listGe);
    		if(!UtilValidate.isEmpty(listBL)){
    			for(GenericValue x : listBL){
    				Map<String, Object> row = new HashMap<String, Object>();
    				String billId = (String)x.get("billId");
    				row.put("billId", billId);
    				row.put("billNumber", (String)x.get("billNumber"));
    				Timestamp deparDate = (Timestamp)x.get("departureDate");
    				if(deparDate != null){
						String departureDate = yearMonthDayFormat2.format(new Date(deparDate.getTime()));
						row.put("departureDate", departureDate);
					}
    				Timestamp arrivalDateStamp = (Timestamp)x.get("arrivalDate");
					if(arrivalDateStamp != null){
						String arrivalDate = yearMonthDayFormat2.format(new Date(arrivalDateStamp.getTime()));
						row.put("arrivalDate", arrivalDate);
					}
    				
    				List<GenericValue> listDetailCost = delegator.findList("CostAccBaseAndType", EntityCondition.makeCondition(UtilMisc.toMap("costAccountingTypeId", "COST_BILLOFLA", "departmentId", party)), null, null, null, false);
    				List<GenericValue> listDetail = EntityUtil.filterByDate(listDetailCost);
    				List<Map<String, String>> rowDetail = new ArrayList<Map<String,String>>();
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
    						Map<String, String> childDetail = new HashMap<String, String>(); 
//    						childDetail.ad
    						String costAccBaseId = (String)detail.get("costAccBaseId");
    						childDetail.put("costAccBaseId", costAccBaseId);
    						childDetail.put("invoiceItemTypeId", (String)detail.get("invoiceItemTypeId"));
    						childDetail.put("description", (String)detail.get("description"));
    						childDetail.put("billId", billId);
    						String costBillAccId = "";
    						BigDecimal costPriceTemporary = new BigDecimal(0);
    						BigDecimal costPriceActual = new BigDecimal(0); 
    						List<GenericValue> listCostBillAcc = delegator.findList("CostBillAccounting", EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", costAccBaseId, "billOfLadingId", billId)), null, null, null, false);
    						if(!UtilValidate.isEmpty(listCostBillAcc)){
    							GenericValue costBillAcc = EntityUtil.getFirst(listCostBillAcc);
    							costBillAccId = (String)costBillAcc.get("costBillAccountingId");
    							costPriceTemporary = (BigDecimal)costBillAcc.get("costPriceTemporary");
    							costPriceActual = (BigDecimal)costBillAcc.get("costPriceActual");
    						}
    						childDetail.put("costBillAccountingId", costBillAccId);
    						childDetail.put("costPriceTemporary", Integer.toString(costPriceTemporary.intValue()));
    						childDetail.put("costPriceActual", Integer.toString(costPriceActual.intValue()));
    						rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> updateCostBillAcc(DispatchContext ctx, Map<String, ? extends Object> context){
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
   	 	String billId = (String)context.get("billId");
   	 	String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
   	 	String costAccBaseId = (String)context.get("costAccBaseId");
   	 	GenericValue userLogin = (GenericValue)context.get("userLogin");
   	 	GenericValue costBaseId = null;
   	 	GenericValue costBaseOrderId = null;
   	 	
   	 	try {
			List<GenericValue> listCostBaseId = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", costAccBaseId, "invoiceItemTypeId", invoiceItemTypeId, "costAccountingTypeId", "COST_BILLOFLA")), null, null, null, false);
			costBaseId = EntityUtil.getFirst(listCostBaseId);
			
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e2.getMessage());
		}
   	 	List<GenericValue> listOrderCostAccBase = new ArrayList<GenericValue>();
   	 	if(costBaseId != null){
	   	 	try {
				listOrderCostAccBase = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("departmentId", (String)costBaseId.get("departmentId"), "invoiceItemTypeId", invoiceItemTypeId, "costAccountingTypeId", "COST_ORDER")), null, null, null, false);
				costBaseOrderId = EntityUtil.getFirst((EntityUtil.filterByDate(listOrderCostAccBase)));
				
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e1.getMessage());
			}
   	 	}
   	 	String costBillAccountingId = (String)context.get("costBillAccountingId");
   	 	String costPriceTemporary = (String)context.get("costPriceTemporary");
   	 	String costPriceActual = (String)context.get("costPriceActual");
   	 	BigDecimal costTemp = new BigDecimal(Integer.parseInt(costPriceTemporary));
   	 	BigDecimal costAc = new BigDecimal(Integer.parseInt(costPriceActual));
   	 	
   	 	if("".equals(costBillAccountingId) || costBillAccountingId == null){
   	 		costBillAccountingId = delegator.getNextSeqId("CostBillAccounting");
   	 		GenericValue costBillAcc = delegator.makeValue("CostBillAccounting");
   	 		costBillAcc.put("costBillAccountingId", costBillAccountingId);
   	 		costBillAcc.put("costAccBaseId", costAccBaseId);
   	 		costBillAcc.put("billOfLadingId", billId);
   	 		costBillAcc.put("costPriceTemporary", costTemp);
   	 		costBillAcc.put("costPriceActual", costAc);
   	 		try {
				delegator.create(costBillAcc);
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("billId", billId);
				mapContext.put("costAccBaseId", (String)costBaseOrderId.get("costAccBaseId"));
				mapContext.put("applicationBaseId", (String)costBaseOrderId.get("applicationBaseId"));
				mapContext.put("costPriceTemporary", costTemp);
				mapContext.put("userLogin",userLogin);
				try {
					dispatcher.runSync("allocationCostFromBLToOrder", mapContext);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					return ServiceUtil.returnError(e.getMessage());
				}
				
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
   	 		
   	 	}else{
   	 		GenericValue costBillAcc;
			try {
				costBillAcc = delegator.findOne("CostBillAccounting", UtilMisc.toMap("costBillAccountingId", costBillAccountingId), false);
		 		costBillAcc.put("costPriceTemporary", costTemp);
		 		costBillAcc.put("costPriceActual", costAc);
		 		delegator.store(costBillAcc);
		 		
		 		Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("billId", billId);
				mapContext.put("costAccBaseId", (String)costBaseOrderId.get("costAccBaseId"));
				mapContext.put("applicationBaseId", (String)costBaseOrderId.get("applicationBaseId"));
				mapContext.put("costPriceTemporary", costTemp);
				mapContext.put("userLogin",userLogin);
				try {
					dispatcher.runSync("allocationCostFromBLToOrder", mapContext);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					return ServiceUtil.returnError(e.getMessage());
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
	 		
   	 		
   	 	}
   	 	result.put("costBillAccountingId", costBillAccountingId);
    	return result;
    }
    
    public static Map<String, Object> allocationCostFromBLToOrder(DispatchContext ctx, Map<String, ? extends Object> context){
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
   	 	String billId = (String)context.get("billId");
   	 	String costAccBaseId = (String)context.get("costAccBaseId");
   	 	String applicationBaseId = (String)context.get("applicationBaseId");
   	 	BigDecimal costPriceTemporary = (BigDecimal)context.get("costPriceTemporary");
   	 	GenericValue userLogin = (GenericValue)context.get("userLogin");
   	 	try {
			List<GenericValue> listOrderByBL = delegator.findList("OrderAndContainer", EntityCondition.makeCondition(UtilMisc.toMap("billId", billId)), null, null, null, false);
			if(!UtilValidate.isEmpty(listOrderByBL)){
// tinh theo tieu thuc phan bo trung binh				
				if(applicationBaseId.equals("COST_AVG")){
					int sizeListOrder = listOrderByBL.size();
					for(GenericValue order : listOrderByBL){
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", new BigDecimal(1));
						mapContext.put("perOrder", new BigDecimal(sizeListOrder));
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String)order.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							return ServiceUtil.returnError(e.getMessage());
						}
					}
				}
//tinh theo tieu thuc phan bo so luong san pham trong order				
				if(applicationBaseId.equals("COST_QUANTITY")){
					BigDecimal totalQuantity = new BigDecimal(0);
					List<Map<String, Object>> listOrder = new ArrayList<Map<String,Object>>(); 
					for(GenericValue order1 : listOrderByBL){
						Map<String, Object> mapOrder = new FastMap<String, Object>();
						List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)order1.get("orderId"))), null, null, null, false);
						BigDecimal totalOneOrderBig = new BigDecimal(0);
						for(GenericValue orderItem : listOrderItem){
							BigDecimal quantityItem = (BigDecimal)orderItem.get("quantity");
							totalOneOrderBig = quantityItem.add(totalOneOrderBig);
						}
						mapOrder.put("orderId", (String)order1.get("orderId"));
						mapOrder.put("quantity", totalOneOrderBig);
						listOrder.add(mapOrder);
						totalQuantity = totalQuantity.add(totalOneOrderBig);
						
					}
					for(Map<String, Object> map : listOrder){
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal)map.get("quantity"));
						mapContext.put("perOrder", totalQuantity);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String)map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							return ServiceUtil.returnError(e.getMessage());
						}
					}
					
				}
//tinh theo tieu thuc phan bo khoi luong 1 order				
				if(applicationBaseId.equals("COST_WEIGHT")){
					BigDecimal totalWeight = new BigDecimal(0);
					List<Map<String, Object>> listOrder = new ArrayList<Map<String,Object>>(); 
					for(GenericValue order1 : listOrderByBL){
						Map<String, Object> mapOrder = new FastMap<String, Object>();
						List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)order1.get("orderId"))), null, null, null, false);
						BigDecimal totalOneOrderBig = new BigDecimal(0);
						for(GenericValue orderItem : listOrderItem){
							BigDecimal quantityItem = (BigDecimal)orderItem.get("quantity");
							GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)orderItem.get("productId")), false);
							BigDecimal weight = new BigDecimal(0);
							if(weight != null){
								weight = (BigDecimal)product.get("weight");
							}
							BigDecimal weightOrderItem = weight.multiply(quantityItem);
							totalOneOrderBig = weightOrderItem.add(totalOneOrderBig);
						}
						mapOrder.put("orderId", (String)order1.get("orderId"));
						mapOrder.put("quantity", totalOneOrderBig);
						listOrder.add(mapOrder);
						totalWeight = totalWeight.add(totalOneOrderBig);
						
					}
					for(Map<String, Object> map : listOrder){
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal)map.get("quantity"));
						mapContext.put("perOrder", totalWeight);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String)map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							return ServiceUtil.returnError(e.getMessage());
						}
					}
					
				}
//tinh theo tieu thuc phan bo gia tri order				
				if(applicationBaseId.equals("COST_VALUE")){
					BigDecimal totalValue = new BigDecimal(0);
					GenericValue billOfLading = delegator.findOne("BillOfLading", UtilMisc.toMap("billId", billId), false);
					Timestamp arrivalDate = new Timestamp(System.currentTimeMillis());
					if((Timestamp)billOfLading.get("arrivalDate") != null){
						arrivalDate = (Timestamp)billOfLading.get("arrivalDate");
					}
					List<Map<String, Object>> listOrder = new ArrayList<Map<String,Object>>(); 
					for(GenericValue order1 : listOrderByBL){
						Map<String, Object> mapOrder = new FastMap<String, Object>();
						
						GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", (String)order1.get("orderId")), false);
						String currencyUom = (String)orderHeader.get("currencyUom");
						BigDecimal grandTotal = (BigDecimal)orderHeader.get("grandTotal");
						List<GenericValue> uomConvert = delegator.findList("UomConversionDated", EntityCondition.makeCondition(UtilMisc.toMap("uomId", currencyUom, "uomIdTo", "VND")), null, null, null, false);
						GenericValue valueVND = EntityUtil.getFirst(EntityUtil.filterByDate(uomConvert, arrivalDate));
						Double valueConvert = (Double)valueVND.get("conversionFactor");
						BigDecimal valueConvertBig = BigDecimal.valueOf(valueConvert);
						BigDecimal valueOneOrder = valueConvertBig.multiply(grandTotal);
						totalValue = totalValue.add(valueOneOrder);
						mapOrder.put("orderId", (String)order1.get("orderId"));
						mapOrder.put("quantity", valueOneOrder);
						listOrder.add(mapOrder);
					}
					for(Map<String, Object> map : listOrder){
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("oneOrder", (BigDecimal)map.get("quantity"));
						mapContext.put("perOrder", totalValue);
						mapContext.put("totalCost", costPriceTemporary);
						mapContext.put("orderId", (String)map.get("orderId"));
						mapContext.put("costAccBaseId", costAccBaseId);
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateCostOrder", mapContext);
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							return ServiceUtil.returnError(e.getMessage());
						}
					}
					
				}
				
			}
			
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e.getMessage());
		}
   	 	return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> JQGetCostForOrder(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String orderId = (String)param.get("orderId")[0];
    	String departmentId = (String)param.get("departmentId")[0];
    	SimpleDateFormat yearMonthDayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    	EntityCondition cond = EntityCondition.makeCondition("costAccountingTypeId", EntityOperator.EQUALS, "COST_ORDER");
    	EntityCondition cond2 = EntityCondition.makeCondition("departmentId", EntityOperator.EQUALS, departmentId);
    	EntityCondition finalCond = EntityCondition.makeCondition(cond, EntityOperator.AND, cond2);
    	
    	if(("").equals(orderId)){
    		successResult.put("listIterator", listIterator);
    		return successResult;
    	}
    	listAllConditions.add(finalCond);
    	List<GenericValue> listGe = new ArrayList<GenericValue>();
    	try {
    		listGe = delegator.findList("CostAccBaseAndType", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		List<GenericValue> listCostAccBase = EntityUtil.filterByDate(listGe);
    		if(!UtilValidate.isEmpty(listCostAccBase)){
    			for(GenericValue x : listCostAccBase){
    				Map<String, Object> row = new HashMap<String, Object>();
    				row.put("costAccBaseId", (String)x.get("costAccBaseId"));
    				row.put("invoiceItemTypeId", (String)x.get("invoiceItemTypeId"));
    				row.put("description", (String)x.get("description"));
    				List<GenericValue> listCostAcc = delegator.findList("CostAccounting", EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", (String)x.get("costAccBaseId"), "orderId", orderId)), null, null, null, false);
    				String costAccountingId = "";
    				BigDecimal costPriceTemporary = new BigDecimal(0);
    				if(!UtilValidate.isEmpty(listCostAcc)){
    				GenericValue costAcc = EntityUtil.getFirst(listCostAcc);
    				costAccountingId = (String)costAcc.get("costAccountingId");
    				costPriceTemporary = (BigDecimal)costAcc.get("costPriceTemporary");
    				}
    				row.put("costAccountingId", costAccountingId);
    				row.put("costPriceTemporary", costPriceTemporary);
    				List<GenericValue> listDetailCost = delegator.findList("CostChildInvBaseAndType", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", (String)x.get("invoiceItemTypeId"))), null, null, null, false);
    				List<GenericValue> listDetail = EntityUtil.filterByDate(listDetailCost);
    				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
    						Map<String, Object> childDetail = new HashMap<String, Object>(); 
//    						childDetail.ad
    						childDetail.put("childInvItemTypeId", (String)detail.get("childInvItemTypeId"));
    						childDetail.put("costBase", (BigDecimal)detail.get("costBase"));
    						childDetail.put("costDescription", (String)detail.get("costDescription"));
    						childDetail.put("invoiceItemTypeId", (String)detail.get("invoiceItemTypeId"));
    						childDetail.put("description", (String)detail.get("description"));
    						rowDetail.add(childDetail);
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String, Object> updateCostAccounting(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = new FastMap<String, Object>();
    	String costAccountingId = "";
    	String costAccBaseId = (String)context.get("costAccBaseId");
    	String orderId = (String)context.get("orderId");
    	try {
			List<GenericValue> listCostAccId = delegator.findList("CostAccounting", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "costAccBaseId", costAccBaseId)), null, null, null, false);
			if(!UtilValidate.isEmpty(listCostAccId)){
				GenericValue costAccId = EntityUtil.getFirst(listCostAccId);
				costAccountingId = (String)costAccId.get("costAccountingId");
			}
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
    	
    	int costPriceTemporaryInt = Integer.parseInt((String)context.get("costPriceTemporary"));
    	BigDecimal costPriceTemporary = new BigDecimal(costPriceTemporaryInt);
    	
    	int costPriceActualInt = 0;
    	if((String)context.get("costPriceActual") != null && !("").equals((String)context.get("costPriceActual"))){
    		costPriceActualInt = Integer.parseInt((String)context.get("costPriceActual"));
    	}
    	BigDecimal costPriceActual = new BigDecimal(costPriceActualInt);
    	
    	if(costAccountingId == null || ("").equals(costAccountingId)){
    		GenericValue costAccounting = delegator.makeValue("CostAccounting");
    		costAccountingId = delegator.getNextSeqId("CostAccounting");
    		costAccounting.put("costAccountingId", costAccountingId);
    		costAccounting.put("costAccBaseId", costAccBaseId);
    		costAccounting.put("orderId", orderId);
    		costAccounting.put("costPriceTemporary", costPriceTemporary);
    		costAccounting.put("costPriceActual", costPriceActual);
    		try {
				delegator.create(costAccounting);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
    	}else{
    		try {
				GenericValue costAccounting = delegator.findOne("CostAccounting", UtilMisc.toMap("costAccountingId", costAccountingId), false);
				costAccounting.put("costPriceTemporary", costPriceTemporary);
				costAccounting.put("costPriceActual", costPriceActual);
				delegator.store(costAccounting);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
    		
    	}
    	successResult.put("costAccountingId", costAccountingId);
    	
    	return successResult;
    }
    
    public static Map<String, Object> ajaxUpdateCostOrder(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderCost =(String)context.get("orderCost");
		String orderId = (String)context.get("orderId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		JSONArray jsonArr = new JSONArray().fromObject(orderCost);
		
		for(int i = 0; i < jsonArr.size(); i++){
			
			JSONObject jsonObj = jsonArr.getJSONObject(i);
//create Lot
			String costAccountingId = (String)jsonObj.get("costAccountingId");
			String costAccBaseId = (String)jsonObj.get("costAccBaseId");
			String costPriceTemporary = ((Integer)jsonObj.getInt("costPriceTemporary")).toString();
			String costPriceActual = "";
			Map<String,Object> contextTmp = new HashMap<String, Object>();
			contextTmp.put("orderId", orderId);
			contextTmp.put("costAccountingId", costAccountingId);
			contextTmp.put("costAccBaseId", costAccBaseId);
			contextTmp.put("costPriceTemporary", costPriceTemporary);
			contextTmp.put("costPriceActual", costPriceActual);
			contextTmp.put("userLogin", userLogin);
			
			try {
				dispatcher.runSync("updateCostAccounting", contextTmp);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		return ServiceUtil.returnSuccess();
		
	}
}
