package com.olbius.olap;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

public class SalesInvoiceOlap {
	public static final String module = SalesInvoiceOlap.class.getName();
	
	public static Map<String, Object> loadInvoice(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		Delegator delegator = ctx.getDelegator();
		
		String tenant = delegator.getDelegatorTenantId();
		
		if(tenant==null) {
			tenant = Constant.getTenantDefault();
		}
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		Map<String, Object> naturalKeyFields = new HashMap<String, Object>();
		
		Object dateId = null;
		Object currencyId = null;
		
		Object invoiceId = context.get("invoiceId");
		
		Timestamp timestamp = (Timestamp) context.get("createdStamp");
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
		Date date = new Date(calendar.getTimeInMillis());
		
		try {
			naturalKeyFields.put("dateValue", date);
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "DateDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			dateId = tmp.get("dimensionId");
			if(dateId == null) {
				tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
				dateId = tmp.get("dimensionId");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		try {
			naturalKeyFields.clear();
			naturalKeyFields.put("currencyId", context.get("currencyUomId"));
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "CurrencyDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			currencyId = tmp.get("dimensionId");
			/*if(currencyId == null) {
				tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
				dateId = tmp.get("dimensionId");
			}*/
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		if(dateId!=null&&currencyId!=null&&invoiceId!=null) {
			GenericValue invoice = null;
			try {
				invoice = delegator.findOne("SalesInvoiceFact", false, UtilMisc.toMap("invoiceId", invoiceId, "tenantId", tenant));
			} catch (GenericEntityException e) {
			}
			
			if(invoice == null) {
				invoice = delegator.makeValue("SalesInvoiceFact");
				invoice.set("tenantId", tenant);
				invoice.set("invoiceId", invoiceId);
				invoice.set("invoiceDateDimId", dateId);
				invoice.set("origCurrencyDimId", currencyId);
				invoice.set("total", new BigDecimal(0));
				
				try {
					invoice.create();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> loadInvoiceItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		Delegator delegator = ctx.getDelegator();
		
		String tenant = delegator.getDelegatorTenantId();
		
		if(tenant==null) {
			tenant = Constant.getTenantDefault();
		}
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		Map<String, Object> naturalKeyFields = new HashMap<String, Object>();
		
		Object dateId = null;
		Object currencyId = null;
		Object productId = null;
		
		Object invoiceId = context.get("invoiceId");
		
		Object invoiceSeq = context.get("invoiceItemSeqId");
		
		GenericValue invoice = null;
		
		try {
			invoice = delegator.findOne("SalesInvoiceFact", false, UtilMisc.toMap("invoiceId", invoiceId, "tenantId", tenant));
		} catch (GenericEntityException e) {
		}
		
		if(invoice == null) {
			return result;
		}
		
		Timestamp timestamp = (Timestamp) context.get("createdStamp");
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
		Date date = new Date(calendar.getTimeInMillis());
		
		try {
			naturalKeyFields.put("dateValue", date);
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "DateDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			dateId = tmp.get("dimensionId");
			if(dateId == null) {
				tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
				dateId = tmp.get("dimensionId");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		try {
			naturalKeyFields.clear();
			naturalKeyFields.put("productId", context.get("productId"));
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "ProductDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			productId = tmp.get("dimensionId");
			if(productId == null) {
				dispatcher.runSync("loadProductInProductDimension", UtilMisc.toMap("productId", context.get("productId"), "updateMode", "TYPE2", "userLogin" ,context.get("userLogin")));
				tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "ProductDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
				productId = tmp.get("dimensionId");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		if(context.get("uomId") == null) {
			currencyId = invoice.get("origCurrencyDimId");
		} else {
			try {
				naturalKeyFields.clear();
				naturalKeyFields.put("currencyId", context.get("uomId"));
				Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "CurrencyDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
				currencyId = tmp.get("dimensionId");
				/*if(currencyId == null) {
					tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
					dateId = tmp.get("dimensionId");
				}*/
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		
		
		if(dateId!=null&&currencyId!=null&&invoiceId!=null&&productId!=null&&invoiceSeq!=null) {
			
			GenericValue invoiceItem = null;
			
			
			try {
				invoiceItem = delegator.findOne("SalesInvoiceItemFact", false, UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceSeq, "tenantId", tenant));
			} catch (GenericEntityException e) {
			}
			
			if(invoiceItem == null) {
				invoiceItem = delegator.makeValue("SalesInvoiceItemFact");
				invoiceItem.set("tenantId", Constant.getTenantDefault());
				invoiceItem.set("invoiceId", invoiceId);
				invoiceItem.set("invoiceItemSeqId", invoiceSeq);
				invoiceItem.set("invoiceDateDimId", dateId);
				invoiceItem.set("productDimId", productId);
				invoiceItem.set("origCurrencyDimId", currencyId);
				invoiceItem.set("invoiceItemTypeId", context.get("invoiceItemTypeId"));
				
				BigDecimal decimal = (BigDecimal)context.get("quantity");
				if(decimal == null) {
					decimal = new BigDecimal(0);
				}
				
				invoiceItem.set("quantity", decimal);
				
				decimal = (BigDecimal)context.get("amount");
				if(decimal == null) {
					decimal = new BigDecimal(0);
				}
				
				invoiceItem.set("amount", decimal);
				
				try {
					invoiceItem.create();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				decimal = decimal.multiply(invoiceItem.getBigDecimal("quantity"));
				
				invoice.set("total", invoice.getBigDecimal("total").add(decimal));
				
				try {
					invoice.store();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
				
		}
		return result;
	}
}
