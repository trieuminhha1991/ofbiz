package com.olbius.basepos.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basepos.util.POUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PosUtil {
	public static List<GenericValue> doFilterGenericValue(List<GenericValue> listData, List<EntityCondition> listAllConditions) {
		List<GenericValue> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if(UtilValidate.isNotEmpty(listConditions)){
			for (GenericValue x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");
					
					Object fieldValue = x.get(fieldName);
					
					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator) || UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}
					
					if(operator.equalsIgnoreCase("LIKE")){
						if (!fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_LIKE")){
						if (fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("EQUAL")){
						if (!fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
						if (fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("RANGE")){
						String valueFrom = (String) m.get("valueFrom");
						String valueTo = (String) m.get("valueTo");
						if (UtilValidate.isEmpty(valueFrom.trim()) || UtilValidate.isEmpty(valueTo.trim())) {
							pass = false;
							break;
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
							Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
							if(fieldValueTs.before(valueFromTs)){
								pass = false;
							}
							if(fieldValueTs.after(valueToTs)){
								pass = false;
							}
						} if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueFromDate = Date.valueOf(valueFrom);
							Date valueToDate = Date.valueOf(valueTo);
							if (fieldValueDate.compareTo(valueFromDate) < 0) {
								pass = false;
							}
							if (fieldValueDate.compareTo(valueToDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) != 0){
								pass = false;
							}
						} else  if (fieldValue instanceof Integer) {
							Integer fieldValueInt = (Integer) fieldValue;
							Integer valueInt = Integer.valueOf(value);
							if (fieldValueInt != valueInt) {
								pass = false;
							}
						} else if (fieldValue instanceof String) {
							if (!fieldValue.toString().equals(value)) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) < 0){
								pass = false;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.before(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) < 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("<=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) > 0){
								pass = false;
								break;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.after(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) <= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) >= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<>")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) == 0){
								pass = false;
								break;
							}
						}
					}
				}
				if (pass) {
					listReturn.add(x);
				}
			}
		}else {
			return listData;
		}
		return listReturn;
	}
	
	public static List<Map<String, Object>> doFilter(List<Map<String, Object>> listData, List<EntityCondition> listAllConditions) {
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if(UtilValidate.isNotEmpty(listConditions)){
			for (Map<String, Object> x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");
					
					Object fieldValue = x.get(fieldName);
					
					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator) || UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}
					if(operator.equalsIgnoreCase("LIKE")){
						if (!fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_LIKE")){
						if (fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("EQUAL")){
						if (!fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
						if (fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("RANGE")){
						String valueFrom = (String) m.get("valueFrom");
						String valueTo = (String) m.get("valueTo");
						if (UtilValidate.isEmpty(valueFrom.trim()) || UtilValidate.isEmpty(valueTo.trim())) {
							pass = false;
							break;
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
							Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
							if(fieldValueTs.before(valueFromTs)){
								pass = false;
							}
							if(fieldValueTs.after(valueToTs)){
								pass = false;
							}
						} if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueFromDate = Date.valueOf(valueFrom);
							Date valueToDate = Date.valueOf(valueTo);
							if (fieldValueDate.compareTo(valueFromDate) < 0) {
								pass = false;
							}
							if (fieldValueDate.compareTo(valueToDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) != 0){
								pass = false;
							}
						} else  if (fieldValue instanceof Integer) {
							Integer fieldValueInt = (Integer) fieldValue;
							Integer valueInt = Integer.valueOf(value);
							if (fieldValueInt != valueInt) {
								pass = false;
							}
						} else if (fieldValue instanceof String) {
							if (!fieldValue.toString().equals(value)) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) < 0){
								pass = false;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.before(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) < 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("<=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) > 0){
								pass = false;
								break;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.after(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) <= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) >= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<>")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) == 0){
								pass = false;
								break;
							}
						}
					}
				}
				if (pass) {
					listReturn.add(x);
				}
			}
		}else {
			return listData;
		}
		return listReturn;
	}
	
	private static List<Map<String, Object>> makeListConditions(List<EntityCondition> listConditions) {
		List<Map<String, Object>> listMapConditions = FastList.newInstance();
		for (EntityCondition condition : listConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = FastMap.newInstance();
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							String valueFrom = (String) conditionSplit[2].trim();
							String valueTo = (String) conditionSplit[7].trim();
							valueFrom = cleanValue(valueFrom);
							valueTo = cleanValue(valueTo);
							
							condMap.put("valueFrom", valueFrom);
							condMap.put("valueTo", valueTo);
						}
					}
				}
				
				fieldName = cleanFieldName(fieldName);
				value = cleanValue(value);
				
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
				listMapConditions.add(condMap);
			}
		}
		return listMapConditions;
	}
	
	private static String cleanValue(String value) {
		if(value.contains("(")){
			value = value.replace("(", "");
		}
		if(value.contains(")")){
			value = value.replace(")", "");
		}
		if(value.contains("'")){
			value = value.replace("'", "");
		}
		if(value.contains("%")){
			value = value.replace("%", "");
		}
		return value;
	}
	
	private static String cleanFieldName(String fieldName) {
		if(fieldName.contains("(")){
			fieldName = fieldName.replace("(", "");
		}
		if(fieldName.contains(")")){
			fieldName = fieldName.replace(")", "");
		}
		return fieldName;
	}
	
	public static List<Map<String, Object>> sortListMap(List<Map<String, Object>> listProductCaculateds,
			String sortField) {
		POUtil poUtil = new POUtil();
		poUtil.setSortField(sortField);
		Collections.sort(listProductCaculateds, poUtil);
		return listProductCaculateds;
	}
	
	public static void recordAccounting(String invoiceTypeId, String partyIdFrom, String partyId, BigDecimal amount, String currencyUomId, String description,
				List<Map<String, Object>> invoiceItemList, String paymentType,GenericValue userLogin, LocalDispatcher dispatcher, String resource, Locale locale) throws GenericServiceException{
		Map<String, Object> createInvoiceMap = FastMap.newInstance();
    	createInvoiceMap.put("userLogin", userLogin);
    	createInvoiceMap.put("statusId", "INVOICE_IN_PROCESS");
    	createInvoiceMap.put("currencyUomId", currencyUomId);
    	createInvoiceMap.put("description",description );
		createInvoiceMap.put("invoiceTypeId", invoiceTypeId);
		createInvoiceMap.put("partyId",partyId);
    	createInvoiceMap.put("partyIdFrom",partyIdFrom);
    	
    	Map<String, Object> createInvoice = FastMap.newInstance();
    	createInvoice = dispatcher.runSync("createInvoice", createInvoiceMap);
    	// create invoice item
    	String invoiceId = (String)createInvoice.get("invoiceId");
    	for (Map<String, Object> invoiceItem : invoiceItemList) {
    		Map<String, Object> createInvoiceItemMap = FastMap.newInstance();
        	createInvoiceItemMap.put("userLogin", userLogin);
        	createInvoiceItemMap.put("invoiceId", invoiceId);
        	createInvoiceItemMap.put("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
        	createInvoiceItemMap.put("amount", invoiceItem.get("amount"));
        	createInvoiceItemMap.put("quantity", invoiceItem.get("quantity"));
        	createInvoiceItemMap.put("invoiceItemTypeId", invoiceItem.get("invoiceItemTypeId"));
        	createInvoiceItemMap.put("description", invoiceItem.get("description"));
    		dispatcher.runSync("createInvoiceItem", createInvoiceItemMap);
		}
    	
    	//create payment
    	String paymentDescription = UtilProperties.getMessage(resource, "WebPosPaymentDescription", locale);
    	Map<String, Object> createPaymentMap = FastMap.newInstance();
    	createPaymentMap.put("userLogin", userLogin);
    	createPaymentMap.put("paymentMethodTypeId", "CASH");
    	createPaymentMap.put("paymentTypeId", paymentType);
    	createPaymentMap.put("partyIdTo", partyIdFrom);
    	createPaymentMap.put("partyIdFrom", partyId);
    	if(invoiceTypeId.equals("SALES_INVOICE")){
    		createPaymentMap.put("statusId", "PMNT_RECEIVED");
    	}else{
    		createPaymentMap.put("statusId", "PMNT_SENT");
    	}
		
    	createPaymentMap.put("comments", paymentDescription);
    	createPaymentMap.put("amount", amount);
    	createPaymentMap.put("currencyUomId", currencyUomId);
    	Map<String, Object> createPayment = FastMap.newInstance();
		createPayment = dispatcher.runSync("createPayment", createPaymentMap);
		
    	
    	// create payment application
    	String paymentId = (String) createPayment.get("paymentId");
    	Map<String, Object> paymentApplicationMap = FastMap.newInstance();
    	paymentApplicationMap.put("userLogin", userLogin);
    	paymentApplicationMap.put("paymentId", paymentId);
    	paymentApplicationMap.put("invoiceId", invoiceId);
		dispatcher.runSync("createPaymentApplication", paymentApplicationMap);
		// update invoice status
		Map<String, Object> approvedInvoiceMapo = FastMap.newInstance();
		approvedInvoiceMapo.put("userLogin", userLogin);
		approvedInvoiceMapo.put("statusId", "INVOICE_READY");
		approvedInvoiceMapo.put("invoiceId", invoiceId);
		dispatcher.runSync("setInvoiceStatus", approvedInvoiceMapo);
		Map<String, Object> paidInvoiceMapo = FastMap.newInstance();
		paidInvoiceMapo.put("userLogin", userLogin);
		paidInvoiceMapo.put("statusId", "INVOICE_PAID");
		paidInvoiceMapo.put("invoiceId", invoiceId);
		dispatcher.runSync("setInvoiceStatus", paidInvoiceMapo);
	}
}
