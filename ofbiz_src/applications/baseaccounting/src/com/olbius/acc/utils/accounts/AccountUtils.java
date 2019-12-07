package com.olbius.acc.utils.accounts;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.utils.ErrorUtils;

public class AccountUtils {
    
	public static final String DEBIT = "DEBIT";
	public static final String CREDIT = "CREDIT";
	
	public static final String module = AccountUtils.class.getName();
	
	
	public static String getAccountType(String glAccountId, Delegator delegator) {
		String accType = "";
		try {
			GenericValue glAcc = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
			String glAccountClassId = glAcc.getString("glAccountClassId");
			accType = getAccountTypeByClass(glAccountClassId, delegator);
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, module);
		}
		return accType;
	}
	
	private static String getAccountTypeByClass(String glAccountClassId, Delegator delegator) {
		String accType = "";
		try {
			GenericValue accountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", glAccountClassId), false);
			if(UtilValidate.isEmpty(accountClass.getString("parentClassId"))) {
				accType = glAccountClassId;
			}else {
				accType = getAccountTypeByClass(accountClass.getString("parentClassId"), delegator);
			}
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, module);
		}
		return accType;
	}
	
	public static String getDefaultCurrencyUom(Delegator delegator, String partyId){
		String defaultCurrency = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD", delegator);
		return defaultCurrency;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParameterMapWithFileUploaded(HttpServletRequest request) throws FileUploadException, IOException {
		Map<String, Object> retMap = FastMap.newInstance();
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		List<Map<String, Object>> listFileUploaded = FastList.newInstance();
		for (FileItem item : items) {
            if (item.isFormField()) {
                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                String fieldName = item.getFieldName();
                String fieldValue = IOUtils.toString(item.getInputStream(), "UTF-8");
                retMap.put(fieldName, fieldValue);
            } else {
                // Process form file field (input type="file").
            	Map<String, Object> tempMap = FastMap.newInstance();
                String fileName = FilenameUtils.getName(item.getName());
                InputStream fileContent = item.getInputStream();
                ByteBuffer bf = ByteBuffer.wrap(IOUtils.toByteArray(fileContent));
                tempMap.put("uploadedFile", bf);
                tempMap.put("_uploadedFile_fileName", fileName);
                tempMap.put("_uploadedFile_contentType", item.getContentType());
                listFileUploaded.add(tempMap);
            }
        }
		retMap.put("listFileUploaded", listFileUploaded);
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getListSalesOrderNotVATInvoice(LocalDispatcher dispatcher, Map<String, String[]> params, Locale locale, TimeZone timeZone, GenericValue userLogin) throws GenericServiceException{
		List<String> listOrderId = FastList.newInstance();
		Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
		paramsExtend.put("pagesize", new String[]{"0"});
		paramsExtend.put("sname", new String[]{"JQGetListOrderExternalNotVAT"});
		Map<String,Object> context = new HashMap<String,Object>();
		String selectedAll = params.get("selectedAll") != null? params.get("selectedAll")[0] : null;
		String orderIdNotSelectedStr = params.get("orderIdNotSelected") != null? params.get("orderIdNotSelected")[0] : null;
		String orderIdSelectedStr = params.get("orderIdSelected") != null? params.get("orderIdSelected")[0] : null;
		if("Y".equals(selectedAll)){
			context.put("parameters", paramsExtend);
			context.put("userLogin", userLogin);
			context.put("timeZone", timeZone);
			context.put("locale", locale);
			Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
			if(!ServiceUtil.isSuccess(resultService)){
				return null;
			}
			List<GenericValue> listOrderGv = (List<GenericValue>)resultService.get("results");
			if(orderIdNotSelectedStr != null){
				JSONArray orderIdNotSelectedJsonArr = JSONArray.fromObject(orderIdNotSelectedStr);
				List<String> orderIdNotSelectedList = FastList.newInstance();
				for(int i = 0; i < orderIdNotSelectedJsonArr.size(); i++){
					orderIdNotSelectedList.add(orderIdNotSelectedJsonArr.getString(i));
				}
				listOrderGv = EntityUtil.filterByCondition(listOrderGv, EntityCondition.makeCondition("orderId", EntityJoinOperator.NOT_IN, orderIdNotSelectedList));
			}
			listOrderId = EntityUtil.getFieldListFromEntityList(listOrderGv, "orderId", true);
		}else if(orderIdSelectedStr != null){
			JSONArray orderIdSelectedJsonArr = JSONArray.fromObject(orderIdSelectedStr);
			for(int i = 0; i < orderIdSelectedJsonArr.size(); i++){
				listOrderId.add(orderIdSelectedJsonArr.getString(i));
			}
		}
		return listOrderId;
	}
	
	public static String getRootInvoiceType(Delegator delegator, String invoiceTypeId) throws GenericEntityException{
		String parentTypeId = invoiceTypeId;
		while(!("PURCHASE_INVOICE".equals(parentTypeId) || "SALES_INVOICE".equals(parentTypeId) || parentTypeId == null)){
			GenericValue parentType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", parentTypeId), false);
			parentTypeId = parentType.getString("parentTypeId");
		}
		return parentTypeId;
	}
	
	public static String getRootPaymentTypeId(Delegator delegator, String paymentTypeId) throws GenericEntityException{
		String parentTypeId = paymentTypeId;
		GenericValue parentType = null;
		while(true){
			parentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", parentTypeId), false);
			if(parentType == null || parentType.getString("parentTypeId") == null){
				break;
			}
			parentTypeId = parentType.getString("parentTypeId");
		}
		return parentTypeId;
	}
	
	public static String getPaymentCode(Delegator delegator, String paymentTypeId, String paymentMethodId,Timestamp effectiveDate) throws GenericEntityException{
		if(paymentMethodId == null || paymentTypeId == null){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		if(effectiveDate != null){
			cal.setTime(effectiveDate);
		}
		int year = cal.get(Calendar.YEAR);
		Long nextSeqLong = 0l;
		List<GenericValue> paymentCodeSeqValueList = delegator.findByAnd("PaymentCodeSeqValue", 
				UtilMisc.toMap("paymentTypeId", paymentTypeId, "paymentMethodId", paymentMethodId, "year", year), null, false);
		GenericValue paymentCodeSeqValue = EntityUtil.getFirst(paymentCodeSeqValueList);
		if(paymentCodeSeqValue == null){
			String sequenceId = delegator.getNextSeqId("PaymentCodeSeqValue");
			paymentCodeSeqValue = delegator.makeValue("PaymentCodeSeqValue");
			paymentCodeSeqValue.set("sequenceId", sequenceId);
			paymentCodeSeqValue.set("paymentTypeId", paymentTypeId);
			paymentCodeSeqValue.set("paymentMethodId", paymentMethodId);
			paymentCodeSeqValue.set("year", year);
			paymentCodeSeqValue.set("sequenceValue", 0l);
		}
		nextSeqLong = paymentCodeSeqValue.getLong("sequenceValue") + 1;
		String nextSeqId = String.valueOf(year) + UtilFormatOut.formatPaddedNumber(nextSeqLong, 6);
		paymentCodeSeqValue.set("sequenceValue", nextSeqLong);
		delegator.createOrStore(paymentCodeSeqValue);
		return nextSeqId;
	}
	
	public static List<GenericValue> getProductCategoryByType(Delegator delegator, String productCategoryTypeId, boolean includeBrowseRoot) throws GenericEntityException{
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
		if(!includeBrowseRoot){
			List<GenericValue> prodCatalogCatList = delegator.findList("ProdCatalogCategory", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("prodCatalogCategoryTypeId", "PCCT_BROWSE_ROOT"),
												EntityJoinOperator.AND,
												EntityUtil.getFilterByDateExpr()), null, null, null, false);
			if(UtilValidate.isNotEmpty(prodCatalogCatList)){
				List<String> categoryBrowseRootIds = EntityUtil.getFieldListFromEntityList(prodCatalogCatList, "productCategoryId", true);
				conds.add(EntityCondition.makeCondition("productCategoryId", EntityJoinOperator.NOT_IN, categoryBrowseRootIds));
			}
		}
		List<GenericValue> productCategoryList = delegator.findList("ProductCategory", EntityCondition.makeCondition(conds), null, UtilMisc.toList("categoryName"), null, false);
		return productCategoryList;
	}
	/*public static String getNextPaymentSeqId(Delegator delegator) throws GenericEntityException{
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		Long nextSeqLong = 0l;
		GenericValue sequenceValueBaseYear = delegator.findOne("SequenceValueBaseYear", UtilMisc.toMap("seqName", "Payment", "year", year), false);
		if(sequenceValueBaseYear == null){
			//sequenceId for this year is not create, so reset sequenceId in SequenceValueItem
			GenericValue sequenceValueItem = delegator.makeValue("SequenceValueItem");
			sequenceValueItem.put("seqName", "Payment");
			sequenceValueItem.put("seqId", 1L);
			delegator.createOrStore(sequenceValueItem);
			sequenceValueBaseYear = delegator.makeValue("SequenceValueBaseYear");
			sequenceValueBaseYear.put("seqName", "Payment");
			sequenceValueBaseYear.put("year", year);
			delegator.create(sequenceValueBaseYear);
			nextSeqLong = 1L;
		}else{
			nextSeqLong = delegator.getNextSeqIdLong("Payment");
		}
		String nextSeqId = String.valueOf(year) + UtilFormatOut.formatPaddedNumber(nextSeqLong, 6);
		return nextSeqId;
	}*/

	public static boolean checkInvoiceHaveBilling(Delegator delegator, String invoiceId) throws GenericEntityException {
		List<GenericValue> orderItemBilling = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
		return UtilValidate.isNotEmpty(orderItemBilling);
	}
	
	public static String replaceSpecialCharToHtml(String str){
		if(str == null){
			return str;
		}
		String retStr = str.replaceAll("&", "&amp;").replaceAll(">", "&gt").replaceAll("<", "&lt;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;");
		return retStr;
	}
	
	public static boolean isInvoiceTransPosted(Delegator delegator, String invoiceId) throws GenericEntityException{
		GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
		if(invoice == null){
			return false;
		}
		String statusId = invoice.getString("statusId");
		if("INVOICE_APPROVED".equals(statusId) || "INVOICE_SENT".equals(statusId) || "INVOICE_RECEIVED".equals(statusId) 
				|| "INVOICE_READY".equals(statusId) || "INVOICE_PAID".equals(statusId)){
			return true;
		}
		return false;
	}

	public static BigDecimal calculateUnitPriceInvoiceItem(Delegator delegator, BigDecimal amount, String productId, String invQuantityUomId, String baseQuantityUomId) throws GenericEntityException {
		if(invQuantityUomId == null || baseQuantityUomId == null || invQuantityUomId.equals(baseQuantityUomId)){
			return amount;
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityUtil.getFilterByDateExpr());
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityCondition.makeCondition("uomFromId", invQuantityUomId));
		conds.add(EntityCondition.makeCondition("uomToId", baseQuantityUomId));
		List<GenericValue> configPackingList = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
		if(UtilValidate.isEmpty(configPackingList)){
			return amount;
		}
		GenericValue configPacking = configPackingList.get(0);
		BigDecimal quantityConvert = configPacking.getBigDecimal("quantityConvert");
		BigDecimal baseAmount = amount.divide(quantityConvert, 2, RoundingMode.HALF_UP);
		BigDecimal retVal = baseAmount.multiply(quantityConvert).setScale(2, RoundingMode.HALF_UP);
		return retVal;
	}
}
