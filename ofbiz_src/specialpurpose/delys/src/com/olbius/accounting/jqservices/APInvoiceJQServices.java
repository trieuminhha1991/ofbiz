package com.olbius.accounting.jqservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.text.NumberFormat;
import com.olbius.util.SalesPartyUtil;

public class APInvoiceJQServices {
	public static final String module = APInvoiceJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    private static int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    private static int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    private static final int INVOICE_ITEM_SEQUENCE_ID_DIGITS = 5;
    
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPInvoice2(DispatchContext ctx, Map<String, Object> context) {
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, Object> inputMap = new HashMap<String, Object>();
    	String strSort = "";
    	if(listSortFields.size() > 0){
    		strSort = listSortFields.get(0);
    		for(int i = 1; i < listSortFields.size(); i++){
    			strSort += "," + listSortFields.get(i);
    		}
    	}
    	if("".equals(strSort)){
    		strSort = "-invoiceDate";
    	}
    	Map<String, String> parametersStandard = convertMap(parameters);
    	parametersStandard.put("parentTypeId","PURCHASE_INVOICE");
    	inputMap.put("entityName","InvoiceAndType");
    	inputMap.put("inputFields",parametersStandard);
    	inputMap.put("orderBy",strSort);
    	inputMap.put("viewIndex",Integer.valueOf(parametersStandard.get("pagenum")));
    	inputMap.put("viewSize",Integer.valueOf(parametersStandard.get("pagesize")));
    	inputMap.put("noConditionFind","Y");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> tmpHM = new HashMap<String, Object>();
    	try {
    		tmpHM = dispatcher.runSync("performFind", inputMap);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", tmpHM.get("listIt"));
    	return successResult;
    }
//	@SuppressWarnings("unchecked")
//    public static Map<String, Object> jqGetListAPInvoice(DispatchContext ctx, Map<String, Object> context) {
//    	Delegator delegator = ctx.getDelegator();
//    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
//    	EntityListIterator listIterator = null;
//		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
//    	List<String> listSortFields = (List<String>) context.get("listSortFields");
//    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
//    	try {
//    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
//    		listIterator = delegator.find("InvoiceAndTypeAndPartyNameView", tmpConditon, null, null, listSortFields, opts);
//		} catch (Exception e) {
//			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
//			Debug.logError(e, errMsg, module);
//		}
//    	successResult.put("listIterator", listIterator);
//    	return successResult;
//    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPDueInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
    	// statusId condition
    	List<EntityCondition> listTmp = new ArrayList<EntityCondition>();
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_SENT"));
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_APPROVED"));
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));
    	listAllConditions.add(EntityCondition.makeCondition(listTmp,EntityJoinOperator.OR));
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	// Due condition
    	java.util.Date today = new java.util.Date();
    	listAllConditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Timestamp(today.getTime())));
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPDueInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPDueSoonInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
    	// statusId condition
    	List<EntityCondition> listTmp = new ArrayList<EntityCondition>();
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_SENT"));
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_APPROVED"));
    	listTmp.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));
    	listAllConditions.add(EntityCondition.makeCondition(listTmp,EntityJoinOperator.OR));
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	// Due condition
    	java.util.Date today = new java.util.Date();
    	listTmp = new ArrayList<EntityCondition>();
    	listTmp.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Timestamp(today.getTime())));
    	listTmp.add(EntityCondition.makeCondition("dueDate", null));
    	listAllConditions.add(EntityCondition.makeCondition(listTmp,EntityJoinOperator.OR));
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPDueSoonInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> findInvoiceTaxItem(Delegator delegator, List<GenericValue> listInvoiceTaxItem, String parentInvoiceItemSeqId)
	{
		GenericValue invoiceTaxItem = null;
		Map <String, Object> result = new HashMap<String, Object>();
		
		GenericValue taxAuthorityRateProduct = null;
		for (int i = 0; i < listInvoiceTaxItem.size(); i++)
		{									
			invoiceTaxItem = listInvoiceTaxItem.get(i);
			try {
				taxAuthorityRateProduct = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId",invoiceTaxItem.getString("taxAuthorityRateSeqId")), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (invoiceTaxItem.getString("parentInvoiceItemSeqId").equals(parentInvoiceItemSeqId) && taxAuthorityRateProduct.getString("taxAuthorityRateTypeId").equals("VAT_TAX"))
			{				
				result.put("taxPercentage",(BigDecimal) taxAuthorityRateProduct.getBigDecimal("taxPercentage"));
				result.put("productCategoryId",taxAuthorityRateProduct.getString("productCategoryId"));
				result.put("position",i);
				return result;
			}
		}
		
		result.put("taxPercentage", BigDecimal.ZERO);
		result.put("productCategoryId", "");
		result.put("position", -1);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> updateTaxAPInvoiceNotProcess(DispatchContext ctx, Map<String, Object> context) {
        List <Map<String, Object>> invoiceList = (List <Map<String, Object>>) context.get("listInvoiceId");
        //String taxStatus = (String) context.get("taxStatus");

        // get the cc object
        Delegator delegator = ctx.getDelegator();
        for (int iList = 0; iList < invoiceList.size(); iList++)
        {
        	GenericValue invoice;
        	Map<String, Object> invoiceIn = invoiceList.get(iList);
	        String invoiceId = (String)invoiceIn.get("invoiceId");
	        try {
	         	invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	      
	        //if (taxStatus.equals("true"))        
	        invoice.set("taxStatus", "1");
	        //else invoice.set("taxStatus", "0");
	        	      
	        try {
	            delegator.store(invoice);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
        
			//GenericValue invoiceInfo = null;
			int invoiceSeqIdNum = 0;
			String invoiceSeqId = "";
	        try{
	        	//invoiceInfo = delegator.findOne("InvoiceAndType", UtilMisc.toMap("invoiceId", invoiceId), false);
	        	String invoiceTemplate = "01GTKT3/001";
				String invoiceCode = "DL13P";
				//String invoiceId = invoice.getString("invoiceId");
				BigDecimal invoiceTotal = BigDecimal.ZERO ;
				List<GenericValue> listInvoicePaymentType = null;
				List<GenericValue> listInvoiceItem= null;
				GenericValue invoicePaymentType;
				//Map<String, Object> input = UtilMisc.toMap("invoice", invoice);
				//LocalDispatcher dispatcher = ctx.getDispatcher();
				invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice);
				//Map<String, Object> getInvoiceTotal = dispatcher.runSync("JQGetInvoiceTotal", input);	
				//invoiceTotal = (BigDecimal) getInvoiceTotal.get("invoiceTotal");
				BigDecimal quotaTotal = new BigDecimal("20000000");
				int cashPayment = 0;				
				listInvoicePaymentType = delegator.findByAnd("InvoiceAndApplAndPayment", UtilMisc.toMap("invoiceId", invoiceId), null, false);
				for (int j=0; j < listInvoicePaymentType.size(); j++)
				{
					invoicePaymentType = listInvoicePaymentType.get(j);
					String PaymentMethodType =  invoicePaymentType.getString("pmPaymentMethodTypeId");
					if (PaymentMethodType.equals("CASH"))
					{
						cashPayment = 1;
						break;
					}
				}
				// Note : process - hoa don tai san oto co gia >  1 ty 6
				// Neu Hoa don co gia tri >= 20M va tra bang tien mat thi ghi nhan vao loai 2
				if (cashPayment==1 && invoiceTotal.compareTo(quotaTotal) > 0)
				{
					 EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
			                    EntityCondition.makeCondition("invoiceId", invoiceId),
			                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, getTaxableInvoiceItemTypeIds(delegator))),
			                    EntityOperator.AND);					
					listInvoiceItem = delegator.findList("InvoiceItem", condition, null, null, null, false); 
							
					GenericValue invoiceItem = null;
					List<GenericValue> listInvoiceTaxItem = null;
					
					EntityConditionList<EntityExpr> mcondition = EntityCondition.makeCondition(UtilMisc.toList(
		                    EntityCondition.makeCondition("invoiceId", invoiceId),
		                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator))),
		                    EntityOperator.AND);					
				
					listInvoiceTaxItem = delegator.findList("InvoiceItem", mcondition, null, null, null, false);
					
					// Tham so cho cac truong hop khong co productId
					BigDecimal totalVat = BigDecimal.ZERO;
					BigDecimal totalNoVat = BigDecimal.ZERO;
					Boolean arrTaxProcess[] = new Boolean[listInvoiceTaxItem.size()];
					for (int iTax = 0; iTax < arrTaxProcess.length; iTax++)
						arrTaxProcess[iTax] = false;
					
					for (int iInvi = 0; iInvi < listInvoiceItem.size(); iInvi++)
					{					
						invoiceItem = listInvoiceItem.get(iInvi);					
						if (invoiceItem.getString("productId") != null)
						{
							
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);			            
							GenericValue invoiceTaxItem = null;
							GenericValue taxAuthorityRateProduct = null;
							BigDecimal amountTaxItem = BigDecimal.ZERO;
							BigDecimal taxPercentage ;
							String productCategoryId = "";
							Map <String, Object> iResult = findInvoiceTaxItem(delegator, listInvoiceTaxItem,invoiceItem.getString("invoiceItemSeqId"));						
							int iFind = (Integer) iResult.get("position");
							taxPercentage = (BigDecimal) iResult.get("taxPercentage");
							productCategoryId = (String) iResult.get("productCategoryId");
							if (iFind != -1)
							{
								arrTaxProcess[iFind] = true;
								invoiceSeqIdNum +=1;
				                invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);							
								
								GenericValue vatInvoiceInputTax = delegator.makeValue("VatInvoiceInputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								vatInvoiceInputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceInputTax.set("invoiceCode", invoiceCode);
								vatInvoiceInputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceInputTax.set("partyId", invoice.getString("partyIdFrom"));
								vatInvoiceInputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceInputTax.set("totalNoTax", amount);
								vatInvoiceInputTax.set("taxPercentage", taxPercentage.setScale(taxDecimals, taxRounding));
								
								invoiceTaxItem = listInvoiceTaxItem.get(iFind);
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceInputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyIdFrom")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceInputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceInputTax.set("partyTaxId", "");
								vatInvoiceInputTax.set("conditionType", "2.Hàng hóa không đủ điều kiện khấu trừ:");
						        try {
						        	vatInvoiceInputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceInputTax: ", module);
						        }
	
							}
						}
						else 
						{
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);
				            totalNoVat.add(amount);
						}
					} 
					for (int intTax = 0; intTax < listInvoiceTaxItem.size(); intTax++ )
						if (arrTaxProcess[intTax] == false)
						{
							GenericValue invoiceTaxItem = listInvoiceTaxItem.get(intTax);
							GenericValue taxAuthorityRateProduct = null;
							try {
								 taxAuthorityRateProduct = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId",invoiceTaxItem.getString("taxAuthorityRateSeqId")), false);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if ( taxAuthorityRateProduct.getString("taxAuthorityRateTypeId").equals("VAT_TAX"))
							{				
								invoiceSeqIdNum +=1;
				                invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);							
								GenericValue vatInvoiceInputTax = delegator.makeValue("VatInvoiceInputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								
								vatInvoiceInputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceInputTax.set("invoiceCode", invoiceCode);
								vatInvoiceInputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceInputTax.set("partyId", invoice.getString("partyIdFrom"));
								vatInvoiceInputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceInputTax.set("totalNoTax", totalNoVat);
								vatInvoiceInputTax.set("taxPercentage", taxAuthorityRateProduct.getBigDecimal("taxPercentage").setScale(taxDecimals, taxRounding));
								
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceInputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyIdFrom")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceInputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceInputTax.set("partyTaxId", "");
								vatInvoiceInputTax.set("conditionType", "2.Hàng hóa không đủ điều kiện khấu trừ:");
						        try {
						        	vatInvoiceInputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceInputTax: ", module);
						        }
	
							}
						}
				}
				else
				{
					 EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
			                    EntityCondition.makeCondition("invoiceId", invoiceId),
			                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, getTaxableInvoiceItemTypeIds(delegator))),
			                    EntityOperator.AND);					
					listInvoiceItem = delegator.findList("InvoiceItem", condition, null, null, null, false); 
							
					GenericValue invoiceItem = null;
					List<GenericValue> listInvoiceTaxItem = null;
					
					EntityConditionList<EntityExpr> mcondition = EntityCondition.makeCondition(UtilMisc.toList(
		                    EntityCondition.makeCondition("invoiceId", invoiceId),
		                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator))),
		                    EntityOperator.AND);					
				
					listInvoiceTaxItem = delegator.findList("InvoiceItem", mcondition, null, null, null, false);
					
					// Tham so cho cac truong hop khong co productId
					BigDecimal totalVat = BigDecimal.ZERO;
					BigDecimal totalNoVat = BigDecimal.ZERO;
					Boolean arrTaxProcess[] = new Boolean[listInvoiceTaxItem.size()];
					for (int iTax = 0; iTax < arrTaxProcess.length; iTax++)
						arrTaxProcess[iTax] = false;
					
					for (int iInvi = 0; iInvi < listInvoiceItem.size(); iInvi++)
					{					
						invoiceItem = listInvoiceItem.get(iInvi);					
						if (invoiceItem.getString("productId") != null)
						{
							
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);			            
							GenericValue invoiceTaxItem = null;
							GenericValue taxAuthorityRateProduct = null;
							BigDecimal amountTaxItem = BigDecimal.ZERO;
							BigDecimal taxPercentage ;
							String productCategoryId = "";
							Map <String, Object> iResult = findInvoiceTaxItem(delegator, listInvoiceTaxItem,invoiceItem.getString("invoiceItemSeqId"));
							int iFind = (Integer) iResult.get("position");
							taxPercentage = (BigDecimal) iResult.get("taxPercentage");
							productCategoryId = (String) iResult.get("productCategoryId");												
							if (iFind != -1)
							{
								arrTaxProcess[iFind] = true;
								invoiceSeqIdNum +=1;
								invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
								GenericValue vatInvoiceInputTax = delegator.makeValue("VatInvoiceInputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								vatInvoiceInputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceInputTax.set("invoiceCode", invoiceCode);
								vatInvoiceInputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceInputTax.set("partyId", invoice.getString("partyIdFrom"));
								vatInvoiceInputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceInputTax.set("totalNoTax", amount);
								if (productCategoryId.equals("TAX_VAT_5") || productCategoryId.equals("TAX_VAT_10") || productCategoryId.equals("TAX_VAT_0"))
								vatInvoiceInputTax.set("taxPercentage", taxPercentage.setScale(taxDecimals, taxRounding));
								
								invoiceTaxItem = listInvoiceTaxItem.get(iFind);
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceInputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyIdFrom")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceInputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceInputTax.set("partyTaxId", "");							
								if (productCategoryId.equals("TAX_VAT_5") || productCategoryId.equals("TAX_VAT_10") || productCategoryId.equals("TAX_VAT_0"))
								vatInvoiceInputTax.set("conditionType", "1.Hàng hóa, dịch vụ dùng riêng cho SXKD chịu thuế GTGT và sử dụng cho các hoạt động cung cấp hàng hóa, dịch vụ không kê khai, nộp thuế GTGT đủ điều kiện khấu trừ thuế:");
								else if (productCategoryId.equals("TAX_VAT_KPTH"))
									vatInvoiceInputTax.set("conditionType", "5.Hàng hóa, dịch vụ không phải tổng hợp trên tờ khai 01/GTGT:");
								else if (productCategoryId.equals("TAX_VAT_KCT"))
									vatInvoiceInputTax.set("conditionType", "3.Hàng hóa, dịch vụ dùng chung cho SXKD chịu thuế và không chịu thuế đủ điều kiện khấu trừ thuế:");
						        try {
						        	vatInvoiceInputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceInputTax: ", module);
						        }
	
							}
						}
						else 
						{
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);
				            totalNoVat.add(amount);
						}
					} 
					for (int intTax = 0; intTax < listInvoiceTaxItem.size(); intTax++ )
						if (arrTaxProcess[intTax] == false)
						{
							GenericValue invoiceTaxItem = listInvoiceTaxItem.get(intTax);
							GenericValue taxAuthorityRateProduct = null;
							try {
								 taxAuthorityRateProduct = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId",invoiceTaxItem.getString("taxAuthorityRateSeqId")), false);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if ( taxAuthorityRateProduct.getString("taxAuthorityRateTypeId").equals("VAT_TAX"))
							{				
								invoiceSeqIdNum +=1;
								invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
								GenericValue vatInvoiceInputTax = delegator.makeValue("VatInvoiceInputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								
								vatInvoiceInputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceInputTax.set("invoiceCode", invoiceCode);
								vatInvoiceInputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceInputTax.set("partyId", invoice.getString("partyIdFrom"));
								vatInvoiceInputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceInputTax.set("totalNoTax", totalNoVat);
								String productCategoryId = "";
								productCategoryId = taxAuthorityRateProduct.getString("productCategoryId");
								if (productCategoryId.equals("TAX_VAT_5") || productCategoryId.equals("TAX_VAT_10") || productCategoryId.equals("TAX_VAT_0"))							
								vatInvoiceInputTax.set("taxPercentage", taxAuthorityRateProduct.getBigDecimal("taxPercentage").setScale(taxDecimals, taxRounding));
								
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceInputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyIdFrom")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceInputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceInputTax.set("partyTaxId", "");
								
								if (productCategoryId.equals("TAX_VAT_5") || productCategoryId.equals("TAX_VAT_10") || productCategoryId.equals("TAX_VAT_0"))
								vatInvoiceInputTax.set("conditionType", "1.Hàng hóa, dịch vụ dùng riêng cho SXKD chịu thuế GTGT và sử dụng cho các hoạt động cung cấp hàng hóa, dịch vụ không kê khai, nộp thuế GTGT đủ điều kiện khấu trừ thuế:");
								else if (productCategoryId.equals("TAX_VAT_KPTH"))
									vatInvoiceInputTax.set("conditionType", "5.Hàng hóa, dịch vụ không phải tổng hợp trên tờ khai 01/GTGT:");
								else if (productCategoryId.equals("TAX_VAT_KCT"))
									vatInvoiceInputTax.set("conditionType", "3.Hàng hóa, dịch vụ dùng chung cho SXKD chịu thuế và không chịu thuế đủ điều kiện khấu trừ thuế:");
						        try {
						        	vatInvoiceInputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceInputTax: ", module);
						        }
	
							}
						}				
				}
		        } catch (GenericEntityException e) 
		        {
		            Debug.logError(e, module);
		            return ServiceUtil.returnError(e.getMessage());
		        }
        }
    	return ServiceUtil.returnSuccess();
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> updateTaxARInvoiceNotProcess(DispatchContext ctx, Map<String, Object> context) {
        List <Map<String, Object>> invoiceList = (List <Map<String, Object>>) context.get("listInvoiceId");        

        // get the cc object
        Delegator delegator = ctx.getDelegator();
        for (int iList = 0; iList < invoiceList.size(); iList++)
        {
        	GenericValue invoice;
        	Map<String, Object> invoiceOut = invoiceList.get(iList);
        	String invoiceId = (String)invoiceOut.get("invoiceId");
	        try {
	         	invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	           
	        invoice.set("taxStatus", "1");
	        	      
	        try {
	            delegator.store(invoice);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        
			int invoiceSeqIdNum = 0;
			String invoiceSeqId = "";
	        try{
	        	//invoiceInfo = delegator.findOne("InvoiceAndType", UtilMisc.toMap("invoiceId", invoiceId), false);
	        	String invoiceTemplate = "01GTKT3/001";
				String invoiceCode = "DL13P";
				invoiceId = invoice.getString("invoiceId");
				BigDecimal invoiceTotal = BigDecimal.ZERO ;
				List<GenericValue> listInvoicePaymentType = null;
				List<GenericValue> listInvoiceItem= null;
				GenericValue invoicePaymentType;
				//Map<String, Object> input = UtilMisc.toMap("invoice", invoice);
				//LocalDispatcher dispatcher = ctx.getDispatcher();
					 EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
			                    EntityCondition.makeCondition("invoiceId", invoiceId),
			                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN, getTaxableInvoiceItemTypeIds(delegator))),
			                    EntityOperator.AND);					
					listInvoiceItem = delegator.findList("InvoiceItem", condition, null, null, null, false); 
							
					GenericValue invoiceItem = null;
					List<GenericValue> listInvoiceTaxItem = null;
					
					EntityConditionList<EntityExpr> mcondition = EntityCondition.makeCondition(UtilMisc.toList(
		                    EntityCondition.makeCondition("invoiceId", invoiceId),
		                    EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.IN, getTaxableInvoiceItemTypeIds(delegator))),
		                    EntityOperator.AND);					
				
					listInvoiceTaxItem = delegator.findList("InvoiceItem", mcondition, null, null, null, false);
					
					// Tham so cho cac truong hop khong co productId
					BigDecimal totalVat = BigDecimal.ZERO;
					BigDecimal totalNoVat = BigDecimal.ZERO;
					Boolean arrTaxProcess[] = new Boolean[listInvoiceTaxItem.size()];
					for (int iTax = 0; iTax < arrTaxProcess.length; iTax++)
						arrTaxProcess[iTax] = false;
					
					for (int iInvi = 0; iInvi < listInvoiceItem.size(); iInvi++)
					{					
						invoiceItem = listInvoiceItem.get(iInvi);					
						if (invoiceItem.getString("productId") != null)
						{
							
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);			            
							GenericValue invoiceTaxItem = null;
							GenericValue taxAuthorityRateProduct = null;
							BigDecimal amountTaxItem = BigDecimal.ZERO;
							BigDecimal taxPercentage ;
							String productCategoryId = "";
							Map <String, Object> iResult = findInvoiceTaxItem(delegator, listInvoiceTaxItem,invoiceItem.getString("invoiceItemSeqId"));
							int iFind = (Integer) iResult.get("position");						
							productCategoryId = (String) iResult.get("productCategoryId");												
							if (iFind != -1)
							{
								arrTaxProcess[iFind] = true;
								invoiceSeqIdNum +=1;
								invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
								GenericValue vatInvoiceOutputTax = delegator.makeValue("VatInvoiceOutputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								vatInvoiceOutputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceOutputTax.set("invoiceCode", invoiceCode);
								vatInvoiceOutputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceOutputTax.set("partyId", invoice.getString("partyId"));
								vatInvoiceOutputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceOutputTax.set("totalNoTax", amount);
								
								invoiceTaxItem = listInvoiceTaxItem.get(iFind);
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceOutputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyId")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceOutputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceOutputTax.set("partyTaxId", "");							
								if (productCategoryId.equals("TAX_VAT_KCT"))
									vatInvoiceOutputTax.set("conditionType", "1.Hàng hóa, dịch vụ không chịu thuế giá trị gia tăng (GTGT):");
								else if (productCategoryId.equals("TAX_VAT_KPTH"))
									vatInvoiceOutputTax.set("conditionType", "5.Hàng hóa, dịch vụ không phải tổng hợp trên tờ khai 01/GTGT:");
								else if (productCategoryId.equals("TAX_VAT_0"))
									vatInvoiceOutputTax.set("conditionType", "2.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 0%:");
								else if (productCategoryId.equals("TAX_VAT_5"))
									vatInvoiceOutputTax.set("conditionType", "3.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 5%:");
								else if (productCategoryId.equals("TAX_VAT_10"))
									vatInvoiceOutputTax.set("conditionType", "4.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 10%:");
								
						        try {
						        	vatInvoiceOutputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceOutputTax: ", module);
						        }
	
							}
						}
						else 
						{
				            BigDecimal amount = invoiceItem.getBigDecimal("amount");
				            if (amount == null) {
				                amount = BigDecimal.ZERO;;
				            }
				            BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				            if (quantity == null) {
				                quantity = BigDecimal.ONE;
				            }
				            amount = amount.multiply(quantity);
				            amount = amount.setScale(taxDecimals, taxRounding);
				            totalNoVat.add(amount);
						}
					} 
					for (int intTax = 0; intTax < listInvoiceTaxItem.size(); intTax++ )
						if (arrTaxProcess[intTax] == false)
						{
							GenericValue invoiceTaxItem = listInvoiceTaxItem.get(intTax);
							GenericValue taxAuthorityRateProduct = null;
							try {
								 taxAuthorityRateProduct = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId",invoiceTaxItem.getString("taxAuthorityRateSeqId")), false);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if ( taxAuthorityRateProduct.getString("taxAuthorityRateTypeId").equals("VAT_TAX"))
							{				
								invoiceSeqIdNum +=1;
								invoiceSeqId = UtilFormatOut.formatPaddedNumber(invoiceSeqIdNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
								GenericValue vatInvoiceOutputTax = delegator.makeValue("VatInvoiceOutputTax",
					                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceSeqId", invoiceSeqId));
								
								vatInvoiceOutputTax.set("invoiceTemplate", invoiceTemplate);
								vatInvoiceOutputTax.set("invoiceCode", invoiceCode);
								vatInvoiceOutputTax.set("invoiceDate", invoice.getTimestamp("invoiceDate"));
								vatInvoiceOutputTax.set("partyId", invoice.getString("partyId"));
								vatInvoiceOutputTax.set("productId", invoiceItem.getString("productId"));
								vatInvoiceOutputTax.set("totalNoTax", totalNoVat);
								String productCategoryId = "";
								productCategoryId = taxAuthorityRateProduct.getString("productCategoryId");
								
					            BigDecimal amountTax = invoiceTaxItem.getBigDecimal("amount");
					            if (amountTax == null) {
					            	amountTax = BigDecimal.ZERO;;
					            }
					            BigDecimal quantityTax = invoiceTaxItem.getBigDecimal("quantity");
					            if (quantityTax == null) {
					            	quantityTax = BigDecimal.ONE;
					            }
					            amountTax = amountTax.multiply(quantityTax);
					            amountTax = amountTax.setScale(taxDecimals, taxRounding);
					            vatInvoiceOutputTax.set("totalTax", amountTax);
								// Get Party Tax Id
					            List<GenericValue> partyTaxAuthInfo = null;	
								EntityConditionList<EntityExpr> taxInfoCondition = EntityCondition.makeCondition(
										UtilMisc.toList(
						                    EntityCondition.makeCondition("partyId", invoice.getString("partyId")),
						                    EntityCondition.makeCondition("taxAuthGeoId", invoiceTaxItem.getString("taxAuthGeoId")),
						                    EntityCondition.makeCondition("taxAuthPartyId", invoiceTaxItem.getString("taxAuthPartyId"))),
						                    EntityOperator.AND);																
								partyTaxAuthInfo =  delegator.findList("PartyTaxAuthInfo", taxInfoCondition, null, null, null, false);
								GenericValue getPartyTaxAuth = null;
								if (partyTaxAuthInfo.size() > 0)
								{
									getPartyTaxAuth = partyTaxAuthInfo.get(0);
									vatInvoiceOutputTax.set("partyTaxId", getPartyTaxAuth.getString("partyTaxId"));
								}
								else
									vatInvoiceOutputTax.set("partyTaxId", "");
								
								if (productCategoryId.equals("TAX_VAT_KCT"))
									vatInvoiceOutputTax.set("conditionType", "1.Hàng hóa, dịch vụ không chịu thuế giá trị gia tăng (GTGT):");
								else if (productCategoryId.equals("TAX_VAT_KPTH"))
									vatInvoiceOutputTax.set("conditionType", "5.Hàng hóa, dịch vụ không phải tổng hợp trên tờ khai 01/GTGT:");
								else if (productCategoryId.equals("TAX_VAT_0"))
									vatInvoiceOutputTax.set("conditionType", "2.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 0%:");
								else if (productCategoryId.equals("TAX_VAT_5"))
									vatInvoiceOutputTax.set("conditionType", "3.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 5%:");
								else if (productCategoryId.equals("TAX_VAT_10"))
									vatInvoiceOutputTax.set("conditionType", "4.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 10%:");
						        try {
						        	vatInvoiceOutputTax.create();
						        } catch (Exception e) {
						            Debug.logInfo(e, "Exception thrown while setting vatInvoiceOutputTax: ", module);
						        }
	
							}				
						}
	        } catch (GenericEntityException e) 
	        {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
        }
    	return ServiceUtil.returnSuccess();
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqListVatTaxTemplate(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
		List<GenericValue> listVatTemplate = null;
    	try {
    		//tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("VatDeclarationTemplate", null, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqListVatTaxTemplate service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    	List<GenericValue> vatInvoiceOutputTax = null;
		try {
			 
			vatInvoiceOutputTax = delegator.findList("VatInvoiceOutputTax", tmpConditon, null, null, null, false); 
					//delegator.find("VatInvoiceOutputTax", tmpConditon, null, null, listSortFields, opts);
		}
		catch (Exception e) {
				String errMsg = "Fatal error calling jqListVatTaxTemplate service: " + e.toString();
				Debug.logError(e, errMsg, module);
		}
		
    	List<GenericValue> vatInvoiceInputTax = null;
		try {
			 
			vatInvoiceInputTax = delegator.findList("VatInvoiceInputTax", tmpConditon, null, null, null, false); 
					//delegator.find("VatInvoiceOutputTax", tmpConditon, null, null, listSortFields, opts);
		}
		catch (Exception e) {
				String errMsg = "Fatal error calling jqListVatTaxTemplate service: " + e.toString();
				Debug.logError(e, errMsg, module);
		}
		
		try {
			if (vatInvoiceOutputTax.size() > 0 || vatInvoiceOutputTax.size() > 0)
			{
				BigDecimal vat22 = BigDecimal.ZERO;
				BigDecimal vat23 = BigDecimal.ZERO;
				BigDecimal vat24 = BigDecimal.ZERO;
				for (int iVatIn = 0 ; iVatIn < vatInvoiceInputTax.size() ; iVatIn ++)
				{
					GenericValue invoiceInputTax = vatInvoiceInputTax.get(iVatIn);
					vat23 = vat23.add(invoiceInputTax.getBigDecimal("totalNoTax"));
					vat24 = vat24.add(invoiceInputTax.getBigDecimal("totalTax"));
				}	
				
				BigDecimal vat25 = vat24;
				BigDecimal vat26 = BigDecimal.ZERO;
				BigDecimal vat27 = BigDecimal.ZERO;
				BigDecimal vat28 = BigDecimal.ZERO;
				BigDecimal vat29 = BigDecimal.ZERO;
				BigDecimal vat30 = BigDecimal.ZERO;
				BigDecimal vat31 = BigDecimal.ZERO;
				BigDecimal vat32 = BigDecimal.ZERO;
				BigDecimal vat33 = BigDecimal.ZERO;
				BigDecimal vat34 = BigDecimal.ZERO;
				BigDecimal vat35 = BigDecimal.ZERO;
				BigDecimal vat36 = BigDecimal.ZERO;
				
				for (int iVatOut = 0 ; iVatOut < vatInvoiceOutputTax.size() ; iVatOut ++)
				{
					GenericValue invoiceOutputTax = vatInvoiceOutputTax.get(iVatOut);
					if (invoiceOutputTax.getString("conditionType").equals("1.Hàng hóa, dịch vụ không chịu thuế giá trị gia tăng (GTGT):"))
					{
						vat26 = vat26.add(invoiceOutputTax.getBigDecimal("totalNoTax"));
					}
					else if (invoiceOutputTax.getString("conditionType").equals("2.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 0%:"))
					{
						vat29 = vat29.add(invoiceOutputTax.getBigDecimal("totalNoTax"));
					}
					else if (invoiceOutputTax.getString("conditionType").equals("3.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 5%:"))
					{
						vat30 = vat30.add(invoiceOutputTax.getBigDecimal("totalNoTax"));
						vat31 = vat31.add(invoiceOutputTax.getBigDecimal("totalTax"));
					}
					else if (invoiceOutputTax.getString("conditionType").equals("4.Hàng hóa, dịch vụ chịu thuế suất thuế GTGT 10%:"))
					{
						vat32 = vat32.add(invoiceOutputTax.getBigDecimal("totalNoTax"));
						vat33 = vat33.add(invoiceOutputTax.getBigDecimal("totalTax"));
					}	
				}
				
				vat27 = vat27.add(vat29); vat27 = vat27.add(vat30); vat27 = vat27.add(vat32);
				vat28 = vat28.add(vat31); vat28 = vat28.add(vat33);
				vat34 = vat34.add(vat26); vat34 = vat34.add(vat27);
				vat35 = vat28;
				vat36 = vat35.subtract(vat25);
				BigDecimal vat37 = BigDecimal.ZERO;
				BigDecimal vat38 = BigDecimal.ZERO;
				BigDecimal vat39 = BigDecimal.ZERO;
				BigDecimal vat40 = BigDecimal.ZERO;
				BigDecimal vat40a = BigDecimal.ZERO;
				BigDecimal vat40b = BigDecimal.ZERO;
				BigDecimal vat41 = BigDecimal.ZERO;
				BigDecimal vat42 = BigDecimal.ZERO;
				BigDecimal vat43 = BigDecimal.ZERO;
				
				vat40a = vat36.subtract(vat22); vat40a=vat40a.add(vat37); vat40a=vat40a.subtract(vat38); vat40a=vat40a.subtract(vat39);
				if (vat40a.compareTo(BigDecimal.ZERO) < 0)
				{
					vat41 = vat40a.abs();
					vat40a = BigDecimal.ZERO;
				}
				vat43 = vat41.subtract(vat42);
				
				listVatTemplate = listIterator.getCompleteList();
				GenericValue vatTemplate = null;
				for (int iList = 0; iList < listVatTemplate.size(); iList++)
				{
					vatTemplate = listVatTemplate.get(iList);
					if (iList == 1)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat22);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 4)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat23);
						vatTemplate.set("totalVatTax", vat24);
						listVatTemplate.set(iList, vatTemplate);
					}	
					if (iList == 5)
					{
						vatTemplate = listVatTemplate.get(iList);						
						vatTemplate.set("totalVatTax", vat25);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 7)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat26);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 8)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat27);
						vatTemplate.set("totalVatTax", vat28);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 9)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat29);						
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 10)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat30);
						vatTemplate.set("totalVatTax", vat31);
						listVatTemplate.set(iList, vatTemplate);
					}	
					if (iList == 11)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat32);
						vatTemplate.set("totalVatTax", vat33);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 12)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalNoVatTax", vat34);
						vatTemplate.set("totalVatTax", vat35);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 13)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat36);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 15)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat37);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 16)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat38);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 17)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat39);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 19)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat40a);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 20)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat40b);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 21)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat40);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 22)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat41);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 23)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat42);
						listVatTemplate.set(iList, vatTemplate);
					}
					if (iList == 24)
					{
						vatTemplate = listVatTemplate.get(iList);
						vatTemplate.set("totalVatTax", vat43);
						listVatTemplate.set(iList, vatTemplate);
					}					
				}				
			}
			else 
			{
				listVatTemplate = listIterator.getCompleteList();
				GenericValue vatTemplate = null;
				vatTemplate = listVatTemplate.get(0);
				vatTemplate.set("totalNoVatTax", BigDecimal.ONE);
				listVatTemplate.set(0, vatTemplate);
			}
		} catch (Exception e)
		{
			String errMsg = "Fatal error calling jqListVatTaxTemplate service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	successResult.put("listIterator", listVatTemplate);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxAPInvoiceNotProcess(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");

    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
    	// statusId condition
    	List<EntityCondition> listTmp = new ArrayList<EntityCondition>();
    	listTmp.add(EntityCondition.makeCondition("taxStatus", "0"));
    	listTmp.add(EntityCondition.makeCondition("taxStatus", null));
    	listAllConditions.add(EntityCondition.makeCondition(listTmp,EntityJoinOperator.OR));
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	

	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxARInvoiceNotProcess(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "SALES_INVOICE");
    	// statusId condition
    	List<EntityCondition> listTmp = new ArrayList<EntityCondition>();
    	listTmp.add(EntityCondition.makeCondition("taxStatus", "0"));
    	listTmp.add(EntityCondition.makeCondition("taxStatus", null));
    	listAllConditions.add(EntityCondition.makeCondition(listTmp,EntityJoinOperator.OR));
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);    	
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxAPInvoiceProcessed(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
    	mapCondition.put("taxStatus", "1");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> updateTaxARInvoiceProcessed(DispatchContext ctx, Map<String, Object> context) {
		List <Map<String, Object>> invoiceList = (List <Map<String, Object>>) context.get("listInvoiceId");
		List<String> invoiceIds = new ArrayList<String>();
		
		for (int iList = 0; iList < invoiceList.size(); iList++ )
		{						
			invoiceIds.add((String)invoiceList.get(iList).get("invoiceId")); 
		}
		Delegator delegator = ctx.getDelegator();
		EntityExpr expr = new EntityExpr();
	        expr.init("invoiceId", EntityOperator.IN, invoiceIds);
	    try{
	    	delegator.storeByCondition("Invoice", UtilMisc.toMap("taxStatus", "0"), expr);
	    }  catch (GenericEntityException e) {
	    	Debug.logError(e, module);
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    
	    try {
     	List<GenericValue> listVatInvoiceInputTax = delegator.findList("VatInvoiceOutputTax", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null, false);
			for (GenericValue item : listVatInvoiceInputTax){
				item.remove();
			}
			} catch (GenericEntityException e){
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(e.getMessage());
			}			
     
    	return ServiceUtil.returnSuccess();
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> updateTaxAPInvoiceProcessed(DispatchContext ctx, Map<String, Object> context) {
		List <Map<String, Object>> invoiceList = (List <Map<String, Object>>) context.get("listInvoiceId");
		List<String> invoiceIds = new ArrayList<String>();
		
		for (int iList = 0; iList < invoiceList.size(); iList++ )
		{						
			invoiceIds.add((String)invoiceList.get(iList).get("invoiceId")); 
		}
	        
        // get the cc object
       Delegator delegator = ctx.getDelegator();
//        GenericValue invoice;
//        try {
//         	invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
//        } catch (GenericEntityException e) {
//            Debug.logError(e, module);
//            return ServiceUtil.returnError(e.getMessage());
//        }
//            
//        invoice.set("taxStatus", "0");
//        	      
//        try {
//            delegator.store(invoice);
//        } catch (GenericEntityException e) {
//            Debug.logError(e, module);
//            return ServiceUtil.returnError(e.getMessage());
//        }

		 EntityExpr expr = new EntityExpr();
	        expr.init("invoiceId", EntityOperator.IN, invoiceIds);
	    try{
	    	delegator.storeByCondition("Invoice", UtilMisc.toMap("taxStatus", "0"), expr);
	    }  catch (GenericEntityException e) {
	    	Debug.logError(e, module);
	    	return ServiceUtil.returnError(e.getMessage());
	    }
        try {
        	List<GenericValue> listVatInvoiceInputTax = delegator.findList("VatInvoiceInputTax", EntityCondition.makeCondition("invoiceId", EntityOperator.IN, invoiceIds), null, null, null, false);
			for (GenericValue item : listVatInvoiceInputTax){
				item.remove();
			}
			} catch (GenericEntityException e){
				// TODO Auto-generated catch block
				Debug.log(e.getMessage(), module);
				return ServiceUtil.returnError(e.getMessage());
			}			
        
    	return ServiceUtil.returnSuccess();
    }	
	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxARInvoiceProcessed(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	mapCondition.put("parentTypeId", "SALES_INVOICE");
    	mapCondition.put("taxStatus", "1");
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }		
	
	@SuppressWarnings("unchecked")
    /** Method to get the list party_Id where party role type is RoleTypeId Variable .  */
    public static List<String> getListPartyIdWithRoleType(Delegator delegator, String mstrRoleTypeId) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        List<GenericValue> listPartyIdWithRoleTypes = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", mstrRoleTypeId), null, true);
        for (GenericValue listPartyIdWithRoleType : listPartyIdWithRoleTypes) {
            typeIds.add(listPartyIdWithRoleType.getString("partyId"));
        }
        return typeIds;
    }	
	
	@SuppressWarnings("unchecked")
    /** Method to get the list party_Id where party role type is RoleTypeId List Variable .  */
    public static List<String> getListPartyIdWithRoleTypeList(Delegator delegator, List<String> listRoleTypeId) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        EntityCondition tmpConditon = EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, listRoleTypeId);    				
        List<GenericValue> listPartyIdWithRoleTypesList = delegator.findList("PartyRole", tmpConditon, null, null, null, true); 
        for (GenericValue listPartyIdWithRoleType : listPartyIdWithRoleTypesList) {
            typeIds.add(listPartyIdWithRoleType.getString("partyId"));
        }
        return typeIds;
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getPartyDistributor(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");

    	try {
    		EntityCondition tmpConditon = EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_DISTRIBUTOR"));    		
    		listAllConditions.add(tmpConditon);        		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);    		
    		
    		listIterator = delegator.find("PartyNameView", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getPartyDistributor service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getPartyCustomer(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");

    	try {
    		EntityCondition tmpConditon = EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_CUSTOMER"));
    		listAllConditions.add(tmpConditon);        		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);    	
    		
    		listIterator = delegator.find("PartyNameView", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getPartyDistributor service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
		
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getPartyCustomerAndDistributor(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<String> listRoleTypeId = new ArrayList<String>();
    	listRoleTypeId.add("DELYS_CUSTOMER");
    	listRoleTypeId.add("DELYS_DISTRIBUTOR");      
    	try {
    		EntityCondition tmpConditon = EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleTypeList(delegator, listRoleTypeId));
    		listAllConditions.add(tmpConditon);        		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);    	
    		
    		listIterator = delegator.find("PartyNameView", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getPartyDistributor service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }		
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getPartySupplier(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");

    	try {
    		EntityCondition tmpConditon = EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_SUPPLIER"));
    		listAllConditions.add(tmpConditon);        		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);    	
    		
    		listIterator = delegator.find("PartyNameView", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getPartyDistributor service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }		
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCDAPInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_DISTRIBUTOR");
//    	mapCondition.put("partyId", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");        
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"),
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_DISTRIBUTOR") )
                    ), EntityJoinOperator.AND);	
	
    		listAllConditions.add(tmpConditon);    
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCDARInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "SALES_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_DISTRIBUTOR");
//    	mapCondition.put("partyIdFrom", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");      	
    	try {    		
    	    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_DISTRIBUTOR") )
                    ), EntityJoinOperator.AND);	
	
    		listAllConditions.add(tmpConditon);
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCUSCDAPInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_CUSTOMER");
//    	mapCondition.put("partyId", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
//    	try {
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");        
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"),
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_CUSTOMER") )
                    ), EntityJoinOperator.AND);	
    		
    		listAllConditions.add(tmpConditon);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListCUSCDARInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "SALES_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_CUSTOMER");
//    	mapCondition.put("partyIdFrom", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");      	
    	try {    		
    	    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_CUSTOMER") )
                    ), EntityJoinOperator.AND);	    	    	    
    	    
    	    listAllConditions.add(tmpConditon);
    	    
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSUPCDAPInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "PURCHASE_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_SUPPLIER");
//    	mapCondition.put("partyId", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
//    	try {
    	
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");        
    	try {
    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PURCHASE_INVOICE"),
                            EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_SUPPLIER") )
                    ), EntityJoinOperator.AND);	    	
    		listAllConditions.add(tmpConditon);
    		
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSUPCDARInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	mapCondition.put("parentTypeId", "SALES_INVOICE");
//    	mapCondition.put("roleTypeId", "DELYS_SUPPLIER");
//    	mapCondition.put("partyIdFrom", "company");
//    	mapCondition.put("statusId", "INVOICE_IN_PROCESS");
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
//    	try {
    	List<String> statusIds = new ArrayList<String>();
    	statusIds.add("INVOICE_IN_PROCESS");
    	statusIds.add("INVOICE_READY");      	
    	try {    		
    	    		EntityCondition tmpConditon = 
                    EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                            EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "SALES_INVOICE"),
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "company"),
                            EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds), 
                            EntityCondition.makeCondition("partyId", EntityOperator.IN, getListPartyIdWithRoleType(delegator, "DELYS_SUPPLIER") )
                    ), EntityJoinOperator.AND);	    	
    	    
   	    	listAllConditions.add(tmpConditon);
   	    	
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListCDARInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListBillingAccoun(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("BillingAccount", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListBillingAccoun service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPInvItemAndOrdItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition tmpCond = EntityCondition.makeCondition("invoiceId",(parameters.get("invoiceId"))[0]);
    	listAllConditions.add(tmpCond);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("InvItemAndOrdItem", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvItemAndOrdItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
//	@SuppressWarnings("unchecked")
//    public static Map<String, Object> jqGetListAPInvoiceItems(DispatchContext ctx, Map<String, Object> context) {
//    	Delegator delegator = ctx.getDelegator();
//    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
//    	EntityListIterator listIterator = null;
//		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
//    	List<String> listSortFields = (List<String>) context.get("listSortFields");
//    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(tmpConditon);
//    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
//    	EntityCondition tmpCond = EntityCondition.makeCondition("parentTypeId", "PURCHASE_INVOICE");
//    	listAllConditions.add(tmpCond);
//
//    	try {
//    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
//    		listIterator = delegator.find("InvItemAndType", tmpConditon, null, null, listSortFields, opts);
//		} catch (Exception e) {
//			String errMsg = "Fatal error calling jqGetListAPInvoiceItems service: " + e.toString();
//			Debug.logError(e, errMsg, module);
//		}
//    	successResult.put("listIterator", listIterator);
//    	return successResult;
//    }	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPAcctgTransAndEntries(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition tmpCond = EntityCondition.makeCondition("invoiceId",(parameters.get("invoiceId"))[0]);
    	listAllConditions.add(tmpCond);
    	List<Map<String, Object>> target = FastList.newInstance();
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("AcctgTransAndEntries", tmpConditon, null, null, listSortFields, opts);
    		ModelEntity me = delegator.getModelEntity("AcctgTransAndEntries");
    		List<String> key = (List<String>) me.getAllFieldNames();
    		List<GenericValue> tmp = listIterator.getCompleteList();
    		listIterator.close();
    		Map<String, Object> tmpCal = FastMap.newInstance();
    		StringBuilder curKey = new StringBuilder();
    		for(GenericValue e : tmp){
    			Map<String, Object> o = FastMap.newInstance();
    			curKey = new StringBuilder();
    			curKey = curKey.append(e.getString("accountCode"))
    						   .append("|")
    						   .append(e.getString("debitCreditFlag"))
    						   .append("|")
    						   .append(e.getString("currencyUomId"))
    						   .append("|")
    						   .append( e.getString("origCurrencyUomId"));
    			if(tmpCal.containsKey(curKey.toString())){
    				o = (Map<String, Object>) tmpCal.get(curKey.toString());
    				BigDecimal currentAmount = BigDecimal.ZERO;
    				if(!UtilValidate.isEmpty(o.get("amount"))){
    					currentAmount = (BigDecimal) o.get("amount");
    				}
    				BigDecimal currentOrigAmount = BigDecimal.ZERO;
    				if(!UtilValidate.isEmpty(o.get("origAmount"))){
    					currentOrigAmount = (BigDecimal) o.get("origAmount");
    				}
    				BigDecimal newAmount = e.getBigDecimal("amount");
    				BigDecimal newOrigAmount = e.getBigDecimal("origAmount");
    				newAmount = newAmount.add(currentAmount);
    				newOrigAmount = newOrigAmount.add(currentOrigAmount);
    				o.put("amount", newAmount);
    				o.put("origAmount", newOrigAmount);
    				tmpCal.put(curKey.toString(), o);
    			}else{
    				for(String field : key){
        				o.put(field, e.get(field));
        			}
    				tmpCal.put(curKey.toString(), o);
    			}
    		}
    		
    		for (Entry<String, Object> entry : tmpCal.entrySet()){
    			target.add((Map<String, Object>) entry.getValue());
    		}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPAcctgTransAndEntries service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", target);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListAPInvoiceRole(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityCondition tmpCond = EntityCondition.makeCondition("invoiceId",(parameters.get("invoiceId"))[0]);
    	listAllConditions.add(tmpCond);
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Locale locale = (Locale) context.get("locale");
    	try {
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		
    		listIterator = delegator.find("InvoiceRoleAndPartyNameView",null, null, null, listSortFields, opts);
    		
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				GenericValue glType = delegator.findOne("RoleType", false, UtilMisc.toMap("roleTypeId", gv.getString("roleTypeId")));
    				mapGv.put("desRole",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				mapGv.put("roleTypeId", gv.getString("roleTypeId"));
    				mapGv.put("percentage", gv.getBigDecimal("percentage"));
    				mapGv.put("partyId", gv.getString("partyId"));
    				mapGv.put("groupName", gv.getString("groupName"));
    				mapGv.put("fullName", gv.getString("fullName"));
    				mapGv.put("datetimePerformed", gv.getString("datetimePerformed"));
    				listRs.add(mapGv);
    			}
    		}
    		
    		listRs = SalesPartyUtil.filterMap(listRs, listAllConditions);
    		listRs = SalesPartyUtil.sortList(listRs, listSortFields);
    		successResult.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			successResult.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			successResult.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    		
    		
    		
    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoiceRole service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
    public static Map<String, Object> createCommissionInvoicesJQ(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> tmpHM = new HashMap<String, Object>();
		String[] strInvoiceIds = ((String) context.get("invoiceIds")).split(",");
		String[] strPartyIds = ((String) context.get("partyIds")).split(",");
		tmpHM.put("invoiceIds", Arrays.asList(strInvoiceIds));
		tmpHM.put("partyIds", Arrays.asList(strPartyIds));
		tmpHM.put("userLogin", context.get("userLogin"));
    	try {
    		LocalDispatcher dispatcher = ctx.getDispatcher();
    		tmpHM = dispatcher.runSync("createCommissionInvoices", tmpHM); 
		} catch (Exception e) {
			String errMsg = "Fatal error calling createCommissionInvoicesJQ service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	
	@SuppressWarnings("rawtypes")
	private static Map<String, String> convertMap(Map<String, String[]> mapArray){
    	Map<String, String> returnValue = new HashMap<String, String>();
    	Iterator it = mapArray.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if(pairs.getValue() instanceof String[]){
            	returnValue.put(pairs.getKey().toString(), ((String[])pairs.getValue())[0]);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    	return returnValue;
    }
	
	@SuppressWarnings("unchecked")
    /** Method to get the taxable invoice item types as a List of invoiceItemTypeIds.  These are identified in Enumeration with enumTypeId TAXABLE_INV_ITM_TY. */
    public static List<String> getTaxableInvoiceItemTypeIds(Delegator delegator) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        List<GenericValue> invoiceItemTaxTypes = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "TAXABLE_INV_ITM_TY"), null, true);
        for (GenericValue invoiceItemTaxType : invoiceItemTaxTypes) {
            typeIds.add(invoiceItemTaxType.getString("enumId"));
        }
        return typeIds;
    }	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxAPInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	EntityCondition tmpConditon ;    	
    	List<GenericValue> listTaxApInvoice = null;
    	Map <String, Object> taxApInvoice = new   HashMap<String, Object>();    	
    	try 
    	{
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("VatInvoiceInputTax", tmpConditon, null, null, listSortFields, opts);   		    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListTaxARInvoice(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	EntityCondition tmpConditon ;    	
    	List<GenericValue> listTaxApInvoice = null;
    	Map <String, Object> taxApInvoice = new   HashMap<String, Object>();    	
    	try 
    	{
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("VatInvoiceOutputTax", tmpConditon, null, null, listSortFields, opts);   		    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListAPInvoice service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }	
	
	
	public static Map<String, Object> jqGetListApplicationList(DispatchContext ctx, Map<String, Object> context) { 
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String,Object>> invoiceApplications = FastList.newInstance();
		String invoiceId = (String) parameters.get("invoiceId")[0];
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> result  = FastMap.newInstance();
    	try {
			if(UtilValidate.isNotEmpty(invoiceId)){
				GenericValue invoice = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId", invoiceId),false);
				if(UtilValidate.isEmpty(invoice)){
						return ServiceUtil.returnError("Invoice not exists!Required invoice this service");
				}else{
					// retrieve related applications with null itemnumber
					List<GenericValue> invoiceAppls =  invoice.getRelated("PaymentApplication", UtilMisc.toMap("invoiceItemSeqId", null) , null, false);
					if(UtilValidate.isNotEmpty(invoiceAppls)){
						for(GenericValue invoiceAppl : invoiceAppls){
							Map<String,Object> mapTmp = FastMap.newInstance();
							mapTmp.put("invoiceId", invoiceAppl.getString("invoiceId"));
							mapTmp.put("invoiceItemSeqId", invoiceAppl.getString("invoiceItemSeqId"));
							mapTmp.put("total", InvoiceWorker.getInvoiceTotal(invoice));
							mapTmp.put("paymentApplicationId", invoiceAppl.getString("paymentApplicationId"));
							mapTmp.put("paymentId", invoiceAppl.getString("paymentId"));
							mapTmp.put("billingAccountId", invoiceAppl.getString("billingAccountId"));
							mapTmp.put("taxAuthGeoId", invoiceAppl.getString("taxAuthGeoId"));
							mapTmp.put("amountToApply", invoiceAppl.getString("amountApplied"));
							mapTmp.put("amountApplied", invoiceAppl.getString("amountApplied"));
							invoiceApplications.add(mapTmp);
						}
					}
					
					// retrieve related applications with an existing itemnumber
					List<GenericValue> invoiceItem =  invoice.getRelated("InvoiceItem", null, null, false);
					if(UtilValidate.isNotEmpty(invoiceItem)){
						for(GenericValue item : invoiceItem){
							BigDecimal itemTotal = null;
						    if (item.getString("amount") != null) {
						          if (item.getString("quantity").isEmpty()) {
						              itemTotal = item.getBigDecimal("amount");
						          } else {
						              itemTotal = item.getBigDecimal("amount").multiply(item.getBigDecimal("quantity"));
						          }
						    }
						    
						    // get relation payment applications for every item(can be more than 1 per item number)
						    List<GenericValue> listRelatePaymentApp = FastList.newInstance();
						    listRelatePaymentApp = item.getRelated("PaymentApplication", null, null, false);
						    	if(UtilValidate.isNotEmpty(listRelatePaymentApp)){
						    		for(GenericValue payment : listRelatePaymentApp){
						    			Map<String,Object> tmp = FastMap.newInstance();
						    			tmp.putAll(item);
						    			tmp.put("total",  NumberFormat.getInstance(locale).format(itemTotal));
						    			tmp.put("paymentApplicationId", payment.getString("paymentApplicationId"));
						    			tmp.put("paymentId", payment.getString("paymentId"));
						    			tmp.put("toPaymentId", payment.getString("toPaymentId"));
						    			tmp.put("amountApplied", payment.getString("amountApplied"));
						    			tmp.put("amountToApply", payment.getString("amountApplied"));
						    			tmp.put("billingAccountId", payment.getString("billingAccountId"));
						    			tmp.put("taxAuthGeoId", payment.getString("taxAuthGeoId"));
								        invoiceApplications.add(tmp);
						    		}
						    		
						    	}
						}
					}
				}
			}
		result.put("listIterator", invoiceApplications);
		result.put("TotalRows", String.valueOf(invoiceApplications.size()));
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListApplicationList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result; 
	 } 
	
	public static Map<String,Object> jqGetListInvoice(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		
		try {
			int pagesize = (parameters.get("pagesize")[0] != null)?Integer.parseInt(parameters.get("pagesize")[0]) : null;
			int pagenum = (parameters.get("pagenum")[0] != null)?Integer.parseInt(parameters.get("pagenum")[0]):null;
			
			if(UtilValidate.isNotEmpty(pagesize) && UtilValidate.isNotEmpty(pagenum)){
				int startIndex = pagesize*pagenum;
				int endIndex = startIndex + pagesize;
				listIterator = delegator.find("Invoice", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				List<GenericValue> listInvoicePartialList = listIterator.getPartialList(startIndex, endIndex);
				result.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
				result.put("listIterator", listInvoicePartialList);
			}else {
				List<GenericValue> listInvoice = delegator.findList("Invoice",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND),null,listSortFields,opts,false);
				result.put("listIterator", listInvoice);
				result.put("TotalRows", String.valueOf(listInvoice.size()));
			}
			listIterator.close();
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListInvoice cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
	}
	
	public static Map<String,Object> jqGetListInvoiceStatus(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String invoiceId = (String) parameters.get("invoiceId")[0];
		try {
			int pagesize = (parameters.get("pagesize")[0] != null)?Integer.parseInt(parameters.get("pagesize")[0]) : null;
			int pagenum = (parameters.get("pagenum")[0] != null)?Integer.parseInt(parameters.get("pagenum")[0]):null;
			listSortFields.add("statusDate");
			if(UtilValidate.isNotEmpty(pagesize) && UtilValidate.isNotEmpty(pagenum)){
				int startIndex = pagesize*pagenum;
				int endIndex = startIndex + pagesize;
				listAllConditions.add(EntityCondition.makeCondition("invoiceId",invoiceId));
				listIterator = delegator.find("InvoiceStatus", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				List<GenericValue> listStt = listIterator.getCompleteList();
				List<Map<String,Object>> listMap = FastList.newInstance();
				if(UtilValidate.isNotEmpty(listStt)){
					for(GenericValue stt: listStt){
						Map<String,Object> mapTmp = FastMap.newInstance();
						mapTmp.put("invoiceId", stt.getString("invoiceId"));
						mapTmp.put("statusId", stt.getString("statusId"));
						mapTmp.put("statusDate", stt.getString("statusDate"));
						GenericValue status = delegator.findOne("StatusItem", false, UtilMisc.toMap("statusId", stt.getString("statusId")));
						mapTmp.put("description", status.get("description", locale));
						listMap.add(mapTmp);
						}
				}
				result.put("listIterator", listMap);
				result.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
				listIterator.close();
			}
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListInvoiceStatus cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
	}
	
	
	public static Map<String,Object> jqGetListInvoiceTerms(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String invoiceId = (String) parameters.get("invoiceId")[0];
		try {
			int pagesize = (parameters.get("pagesize")[0] != null)?Integer.parseInt(parameters.get("pagesize")[0]) : null;
			int pagenum = (parameters.get("pagenum")[0] != null)?Integer.parseInt(parameters.get("pagenum")[0]):null;
			
			if(UtilValidate.isNotEmpty(pagesize) && UtilValidate.isNotEmpty(pagenum)){
				int startIndex = pagesize*pagenum;
				int endIndex = startIndex + pagesize;
				listAllConditions.add(EntityCondition.makeCondition("invoiceId",invoiceId));
				listIterator = delegator.find("InvoiceTerm", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListInvoiceTerms cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
	}
	
	public static Map<String,Object> jqGetListPaymentInfo(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		Map<String,Object> result = FastMap.newInstance();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String,Object> mapTmp = FastMap.newInstance();
		try {
			String invoiceId = (String) parameters.get("invoiceId")[0];
			try {
				 mapTmp = dpct.getDispatcher().runSync("getInvoicePaymentInfoList", UtilMisc.toMap("invoiceId", invoiceId,"userLogin",(GenericValue) context.get("userLogin")));
			} catch (Exception e) {
				String erMsg = "error when call service getInvoicePaymentInfoList cause : " +e.getMessage();
				Debug.log(e,erMsg,module);
			}
			if(ServiceUtil.isSuccess(mapTmp)){
				@SuppressWarnings("unchecked")
				List<Map<String,Object>> listIf = (List<Map<String,Object>>) mapTmp.get("invoicePaymentInfoList");
				result.put("TotalRows", String.valueOf(listIf.size()));
				result.put("listIterator", listIf);
			}
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListPaymentInfo cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		
		return result;
	}
	
	public static Map<String,Object> jqGetListInvoicesApplication(DispatchContext dpct,Map<String,Object> context){
		Delegator delegator = dpct.getDelegator();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String invoiceId = (String) parameters.get("invoiceId")[0];
		try {
				GenericValue invoice = delegator.findOne("Invoice", false, UtilMisc.toMap("invoiceId", invoiceId));
				Map<String,Object> mapTmp= dpct.getDispatcher().runSync("getListApplications", UtilMisc.toMap("invoice",invoice));
				List<Map<String,Object>> listRs = (List<Map<String,Object>>) mapTmp.get("invoiceApplications");
 				result.put("listIterator", listRs);
 				result.put("TotalRows", String.valueOf(listRs.size()));
		} catch (Exception e) {
			String erMsg = "error when call service jqGetListInvoicesApplication cause : " + e.getMessage();
			Debug.log(e,erMsg,module);
		}
		return result;
	}
	
}
