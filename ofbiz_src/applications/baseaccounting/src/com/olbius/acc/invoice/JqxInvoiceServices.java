package com.olbius.acc.invoice;

import com.olbius.acc.invoice.entity.Product;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.UtilServices;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
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

import java.sql.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class JqxInvoiceServices {
	
	public static final String MODULE = JqxInvoiceServices.class.getName();
	
	public static Map<String, Object> getListInvoices(DispatchContext ctx, Map<String, Object> context) {
    	long begin = System.currentTimeMillis();
 		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String invoiceType = ((String[])parameters.get("invoiceType"))[0];
    	String status = parameters.containsKey("status") ? parameters.get("status")[0] : null;
    	String paymentId = parameters.containsKey("paymentId") ? parameters.get("paymentId")[0] : null;
        String fromDueDateStr = parameters.containsKey("fromDueDate") ? parameters.get("fromDueDate")[0] : null;
        String toDueDateStr = parameters.containsKey("toDueDate") ? parameters.get("toDueDate")[0] : null;
		InvoiceList iList = InvoiceListFactory.getInvoiceList(invoiceType);
//    	Map<String, Object> mapInv = FastMap.newInstance();
    	Map<String, Object> result = FastMap.newInstance();
		try {
			if(status != null) context.put("status", status);
			if(paymentId != null) context.put("paymentId", paymentId);
			if(UtilValidate.isNotEmpty(fromDueDateStr)) {
			    context.put("fromDueDate", new Timestamp(Long.valueOf(fromDueDateStr)));
			    context.put("toDueDate", new Timestamp(Long.valueOf(toDueDateStr)));
            }
			result = iList.getListInvoice(ctx, context);
//			result = ServiceUtil.returnSuccess();
//			result.put("listIterator", mapInv.get("listInvoices"));
//			result.put("TotalRows", mapInv.get("size"));
		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);
			result = ServiceUtil.returnError(e.getMessage());
		}
		long end = System.currentTimeMillis();
		System.out.println("Time " + (end - begin));
    	return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInvoiceItemJQ(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("invoiceItemTypeId");
    			listSortFields.add("invoiceItemSeqId");
    		}
    		String invoiceId = parameters.get("invoiceId") != null? parameters.get("invoiceId")[0] : null;
    		mapCondition.put("invoiceId", invoiceId);
    		EntityCondition invoiceCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(invoiceCond);
    		listIterator = delegator.find("InvoiceItemAndProduct", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListInvoiceItems service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInvoiceProducts(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<Product> listResult = new ArrayList<Product>();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	listSortFields.add("invoiceItemTypeId");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	try {
    		String invoiceId = parameters.get("invoiceId")[0];
    		mapCondition.put("invoiceId", invoiceId);
    		EntityCondition invoiceCond = EntityCondition.makeCondition(mapCondition);
    		listAllConditions.add(invoiceCond);
    		listIterator = delegator.find("InvoiceItem", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    		Set<String> prodSet = new HashSet<String>();
    		List<GenericValue> listInvoiceItem = listIterator.getCompleteList();
    		for(GenericValue item: listInvoiceItem) {
    			prodSet.add(item.getString("productId"));
    		}
    		
    		GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
    		for(String item: prodSet) {
    			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item), false);
    			List<GenericValue> orderItemBilling = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
    			String orderId = EntityUtil.getFirst(orderItemBilling).getString("orderId");
    			List<GenericValue> orderProductList =  delegator.findByAnd("InventoryOrderItem", UtilMisc.toMap("orderId", orderId, "productId", item), null, false);
    			GenericValue orderProduct = EntityUtil.getFirst(orderProductList);
    			Product prod = new Product();
    			prod.setProductId(product.getString("productId"));
    			prod.setProductName(product.getString("productName"));
    			prod.setCost(orderProduct.getBigDecimal("unitCost").add(orderProduct.getBigDecimal("purCost")));
    			prod.setCurrencyUomId(invoice.getString("currencyUomId"));
    			listResult.add(prod);
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListInvoiceItems service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listResult);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListNotAppliedPayments(DispatchContext dpct, Map<String, Object> context) {
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		String invoiceId = (String) parameters.get("invoiceId")[0];
		try {
			Map<String,Object> mapTmp = FastMap.newInstance();
			mapTmp.put("invoiceId",invoiceId);
			mapTmp.put("userLogin", (GenericValue) context.get("userLogin"));
			Map<String,Object> resultRunSync = dpct.getDispatcher().runSync("getListNotAppliedPayments", mapTmp);
			result.put("listIterator", resultRunSync.get("payments"));
		} catch (Exception e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInvoiceApplications(DispatchContext ctx, Map<String, Object> context) {
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	Delegator delegator = ctx.getDelegator();
		String invoiceId = ((String[])parameters.get("invoiceId"))[0];
    	Map<String,Object> result = FastMap.newInstance();
    	try {
			Map<String,Object> mapTmp = FastMap.newInstance();
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			mapTmp.put("invoice", invoice);
			mapTmp.put("userLogin", (GenericValue) context.get("userLogin"));
			Map<String,Object> resultRunSync = ctx.getDispatcher().runSync("getListApplications", mapTmp);
			result.put("listIterator", resultRunSync.get("invoiceApplications"));
		} catch (Exception e) {
			String erMsg = "Fatal error when call servicer getListInvoiceApplications cause : " + e.getMessage();
			Debug.log(e,erMsg,MODULE);
		}
		return result;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListVoucherInvoiceJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	String invoiceId = parameters.get("invoiceId") != null? parameters.get("invoiceId")[0] : null;
    	if(invoiceId != null){
    		listAllConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
    	}
    	try {
			listIterator = delegator.find("VoucherInvoiceAndResource", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListVoucherJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-issuedDate");
				listSortFields.add("-voucherId");
			}
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "VoucherAndInvDetail",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInvoiceItemTypeListJQ(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String invoiceTypeId = parameters.get("invoiceTypeId") != null? parameters.get("invoiceTypeId")[0] : null;
		try {
			listAllConditions.add(EntityCondition.makeCondition("invoiceTypeId", invoiceTypeId));
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("description");
			}
			listIterator = delegator.find("InvoiceItemType", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAllListInvoiceJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityListIterator listIterator = null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-invoiceDate");
    		}
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "InvoicePartyDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListOrderInvoiceNoteJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityListIterator listIterator = null;
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-orderDate");
    		}
    		listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isCreatedInSale", Boolean.TRUE),
    				EntityJoinOperator.OR,
    				EntityCondition.makeCondition("isCreatedInSale", null)));
    		listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "OrderInvoiceNoteAndDetail",
    				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductStoreManagerJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("firstName");
			}
			listIterator = delegator.find("ProductStoreAndManagerRole", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListPosTerminalByProductStoreJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productStoreId = parameters.get("productStoreId") != null? parameters.get("productStoreId")[0] : null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("terminalName");
			}
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(productStore != null){
				String inventoryFacilityId = productStore.getString("inventoryFacilityId");
				listAllConditions.add(EntityCondition.makeCondition("facilityId", inventoryFacilityId));
				listIterator = delegator.find("PosTerminal", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getShipmentItemBillingByInvoiceJQ(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String invoiceId = parameters.containsKey("invoiceId") ? parameters.get("invoiceId")[0] : null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        listAllConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
        listAllConditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
        if(UtilValidate.isEmpty(listSortFields)){
            listSortFields.add("productCode");
        }
        try {
            EntityListIterator listIterator = delegator.find("ShipmentItemBillingAndII", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            successResult.put("listIterator", listIterator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        return successResult;
    }

    public static Map<String, Object> jqxGetListInvoiceByDueDate(DispatchContext ctx, Map<String, Object> context) {
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        String invoiceType = parameters.get("invoiceType")[0];
        String fromDueDateStr = parameters.containsKey("fromDueDate") ? parameters.get("fromDueDate")[0] : null;
        String toDueDateStr = parameters.containsKey("toDueDate") ? parameters.get("toDueDate")[0] : null;
        String isNullDueDateStr = parameters.containsKey("isNullDueDate") ? parameters.get("isNullDueDate")[0] : null;
        Boolean isNullDueDate = false;
        if("Y".equals(isNullDueDateStr))  isNullDueDate = true;
        InvoiceList iList = InvoiceListFactory.getInvoiceList(invoiceType);
        String status = "INVOICE_PAID,INVOICE_CANCELLED,";
        try {
            context.put("status", status);
            context.put("fromDueDate", new Timestamp(Long.valueOf(fromDueDateStr)));
            context.put("toDueDate", new Timestamp(Long.valueOf(toDueDateStr)));
            context.put("isNullDueDate", isNullDueDate);
            return iList.getListInvoice(ctx, context);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public static Map<String, Object> jqxGetListDiscountAndPromotion(DispatchContext ctx, Map<String, Object> context) {
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        try {
            EntityListIterator iterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "InvoiceDiscountAndPromoView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
            successResult.put("listIterator", iterator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return successResult;
    }

    public static Map<String, Object> jqxGetDetailDiscountForParty(DispatchContext ctx, Map<String, Object> context) {
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            //item type discount and promo
            List<String> invoiceItemType = new ArrayList<>(Arrays.asList("CRT_DISCOUNT_ADJ", "INV_CASHDISC_ADJ", "ITM_DISCOUNT_ADJ", "ITM_PROMOTION_ADJ", "ITM_PROMPAY_ADJ", "PAYROL_DD_FROM_GROSS", "PINV_DISPAY_ITEM", "PITM_CASHDISC_ADJ", "PITM_DISCOUNT_ADJ", "PITM_PROMOTION_ADJ", "SRT_DISCOUNT_ADJ", "SRT_PROMOTION_ADJ"));
            String partyId = ExportExcelUtil.getParameter(parameters, "partyId");
            String productPromoId = ExportExcelUtil.getParameter(parameters, "productPromoId");
            String newStatusId = ExportExcelUtil.getParameter(parameters, "newStatusId");
            String isFullData = ExportExcelUtil.getParameter(parameters, "isFullData");
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyId", partyId));
            if("null".equals(productPromoId)) {
                conds.add(EntityCondition.makeCondition("productPromoId", null));
            } else conds.add(EntityCondition.makeCondition("productPromoId", productPromoId));
            conds.add(EntityCondition.makeCondition("newStatusId", newStatusId));
            conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, invoiceItemType));
            conds.addAll(listAllConditions);
            if("Y".equals(isFullData)) {
                EntityListIterator iterator = delegator.find("InvoiceItemDiscountAndPromoView", EntityCondition.makeCondition(conds), null, null, listSortFields, opts);
                successResult.put("listIterator", iterator);
            } else {
                EntityListIterator iterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "InvoiceItemDiscountAndPromoView", EntityCondition.makeCondition(conds), null, null, listSortFields, opts);
                successResult.put("listIterator", iterator);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return successResult;
    }
}
