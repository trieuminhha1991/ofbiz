package com.olbius.acc.invoice;

import com.google.common.collect.Maps;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.AccountUtils;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.util.EntityMiscUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;

public class InvoiceServices {
	public static Map<String, Object> updateReturnOrderAfterInvoicePaid(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String invoiceId = (String)context.get("invoiceId");
		try {
			List<GenericValue> listReturnOrder = delegator.findByAnd("ReturnItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
			EntityCondition conds = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.NOT_EQUAL, invoiceId);
			if(UtilValidate.isNotEmpty(listReturnOrder)){
				List<String> listReturnOrderId = EntityUtil.getFieldListFromEntityList(listReturnOrder, "returnId", true);
				for(String returnId: listReturnOrderId){
					boolean isAllInvoiceOfReturnOrdPaid = true;
					List<GenericValue> listReturnOrderItem = delegator.findList("ReturnItemBilling", EntityCondition.makeCondition(conds, EntityJoinOperator.AND,
																															EntityCondition.makeCondition("returnId", returnId)), null, null, null, false);
					if(UtilValidate.isNotEmpty(listReturnOrderItem)){
						List<String> otherInvoiceIdList = EntityUtil.getFieldListFromEntityList(listReturnOrderItem, "invoiceId", true);
						for(String otherInvoiceId: otherInvoiceIdList){
							GenericValue otherInvoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", otherInvoiceId), false);
							if(!"INVOICE_PAID".equals(otherInvoice.get("statusId"))){
								isAllInvoiceOfReturnOrdPaid = false;
								break;
							}
						}
					}
					if(isAllInvoiceOfReturnOrdPaid){
						Map<String, Object> ctxMap = FastMap.newInstance();
						ctxMap.put("userLogin", userLogin);
						ctxMap.put("returnId", returnId);
						ctxMap.put("statusId", "SUP_RETURN_COMPLETED");
						Map<String, Object> resultService = dispatcher.runSync("updateReturnHeader", ctxMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createVoucherInvoice(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "accounting";
		
		try {
			String invoiceId = (String)context.get("invoiceId");
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if("Y".equals(invoice.get("isVerified"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotCrateVoucherWhenInvoiceInVerified", locale));
			}
			if("INVOICE_IN_PROCESS".equals(invoice.get("statusId"))){
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", invoice.get("statusId")), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotCrateVoucherWhenInvoiceInProcess",
						UtilMisc.toMap("status", status.get("description")), locale));
			}
			GenericValue voucher = delegator.makeValue("Voucher");
			String voucherId = delegator.getNextSeqId("Voucher");
			voucher.setNonPKFields(context);
			voucher.set("voucherId", voucherId);
			if(documentFile != null){
				Map<String, Object> uploadedFileCtx = FastMap.newInstance();
				uploadedFileCtx.put("uploadedFile", documentFile);
				uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
				uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
				uploadedFileCtx.put("public", "Y");
				uploadedFileCtx.put("folder", folder);
				uploadedFileCtx.put("userLogin", userLogin);
				Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
				GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
				String path = (String)resultService.get("path");
				Map<String, Object> dataResourceCtx = FastMap.newInstance();
				dataResourceCtx.put("objectInfo", path);
				dataResourceCtx.put("dataResourceName", uploadFileNameStr);
				dataResourceCtx.put("userLogin", systemUserLogin);
				dataResourceCtx.put("dataResourceTypeId", "IMAGE_OBJECT");
				dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
				dataResourceCtx.put("isPublic", "Y");
				resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
				}
				String dataResourceId = (String) resultService.get("dataResourceId");
				voucher.set("dataResourceId", dataResourceId);
			}
			delegator.create(voucher);
			GenericValue voucherInvoice = delegator.makeValue("VoucherInvoice");
			voucherInvoice.put("voucherId", voucherId);
			voucherInvoice.put("invoiceId", invoiceId);
			delegator.create(voucherInvoice);
			successResult.put("voucherId", voucherId);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> getInvoiceItemExcludeTax(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String invoiceId = (String)context.get("invoiceId");
		List<EntityCondition> conds = FastList.newInstance();
		try {
			List<String> taxableInvoiceItemTypeIds = InvoiceWorker.getTaxableInvoiceItemTypeIds(delegator);
			conds.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.NOT_IN, taxableInvoiceItemTypeIds));
			List<GenericValue> invoiceItemList = delegator.findList("InvoiceItemAndTypeAndProduct", EntityCondition.makeCondition(conds), null, UtilMisc.toList("invoiceItemSeqId"), null, false);
			successResult.put("listReturn", invoiceItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	public static Map<String, Object> getInvoiceItemTax(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String invoiceId = (String)context.get("invoiceId");
		List<EntityCondition> conds = FastList.newInstance();
		try {
			List<String> taxableInvoiceItemTypeIds = InvoiceWorker.getTaxableInvoiceItemTypeIds(delegator);
			conds.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.IN, taxableInvoiceItemTypeIds));
			List<GenericValue> invoiceItemList = delegator.findList("InvoiceItemAndTypeAndProduct", EntityCondition.makeCondition(conds), null, UtilMisc.toList("invoiceItemSeqId"), null, false);
			successResult.put("listReturn", invoiceItemList);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> updateInvoiceItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String invoiceId = (String)context.get("invoiceId");
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if("Y".equals(invoice.get("isVerified"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotUpdateWhenInvoiceInVerified", locale));
			}
			GenericValue invoiceItem = delegator.findOne("InvoiceItem", UtilMisc.toMap("invoiceId", context.get("invoiceId"), "invoiceItemSeqId", context.get("invoiceItemSeqId")), false);
			invoiceItem.setNonPKFields(context);
			invoiceItem.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> updateTransactionDateForInvoice(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		try {
			String invoiceId = (String)context.get("invoiceId");
			List<GenericValue> invoiceVouchers = delegator.findByAnd("VoucherInvoiceAndVoucher", UtilMisc.toMap("invoiceId", invoiceId),   UtilMisc.toList("-voucherCreatedDate"), false);
			if (invoiceVouchers != null && invoiceVouchers.size() > 0) {
				GenericValue invoiceVouche = invoiceVouchers.get(0);
				
				List<GenericValue> acctgTransList = delegator.findByAnd("AcctgTrans", UtilMisc.toMap("invoiceId", invoiceId), null, false);
				
				for (GenericValue acctgTrans: acctgTransList) {
					acctgTrans.set("transactionDate", invoiceVouche.getTimestamp("voucherCreatedDate"));;
					acctgTrans.store();
				}
			}			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}	
	
	public static Map<String, Object> createInvoiceItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String invoiceId = (String)context.get("invoiceId");
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if("Y".equals(invoice.get("isVerified"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotCreateWhenInvoiceInVerified", locale));
			}
			GenericValue invoiceItem = delegator.makeValue("InvoiceItem");
			invoiceItem.setAllFields(context, true, null, null);
			delegator.setNextSubSeqId(invoiceItem, "invoiceItemSeqId", 5, 1);
			invoiceItem.create();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> removeInvoiceVoucher(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String voucherId = (String)context.get("voucherId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String,Object> resultService = null;
		try {
			List<GenericValue> invoiceVouchers = delegator.findByAnd("VoucherInvoice", UtilMisc.toMap("voucherId", voucherId), null, false);
			String invoiceId = invoiceVouchers.get(0).getString("invoiceId");
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if("Y".equals(invoice.get("isVerified"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotUpdateWhenInvoiceInVerified", locale));
			}
			for(GenericValue invoiceVoucher: invoiceVouchers){
				invoiceVoucher.remove();
			}
			GenericValue voucher = delegator.findOne("Voucher", UtilMisc.toMap("voucherId", voucherId), false);
			voucher.remove();
			resultService = dispatcher.runSync("updateInvoiceNewStatus", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
		
	public static Map<String, Object> verifyInvoiceVoucher(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String invoiceId = (String)context.get("invoiceId");
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String acctgTransIdList = "";
		try {
			// Step 1: kiem tra xem hoa don do do da duoc so khop day du gia tri chua?
			List<GenericValue> voucherAndInvTotals = delegator.findByAnd("VoucherAndInvTotal", UtilMisc.toMap("invoiceId", invoiceId), null, false);
			if(UtilValidate.isEmpty(voucherAndInvTotals)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceHasNoVoucher", locale));
			}			
			for (GenericValue voucherAndInvTotal : voucherAndInvTotals) {
				List<GenericValue> voucherTotalAndInvs = delegator.findByAnd("VoucherTotalAndInv", UtilMisc.toMap("voucherForm", voucherAndInvTotal.getString("voucherForm"), "voucherSerial", voucherAndInvTotal.getString("voucherSerial"),  "voucherNumber", voucherAndInvTotal.getString("voucherNumber"), "partyIdFrom", voucherAndInvTotal.getString("partyIdFrom") , "partyId", voucherAndInvTotal.getString("partyId"), "issuedDate", voucherAndInvTotal.get("issuedDate")), null, false);
				if(UtilValidate.isEmpty(voucherTotalAndInvs)){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceHasNoVoucher", locale));
				} else if (voucherTotalAndInvs.size() > 1) { 
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPleaseCheckVoucher", locale) + voucherAndInvTotal.getString("voucherForm") + " " + voucherAndInvTotal.getString("voucherSerial") + " " + voucherAndInvTotal.getString("voucherNumber") + UtilProperties.getMessage("BaseAccountingUiLabels", "BACCHaveCreatedDateOrIssueDate", locale));
				} else {
					GenericValue voucherTotalAndInv = voucherTotalAndInvs.get(0);
					BigDecimal totalAmountVoucher = voucherTotalAndInv.getBigDecimal("totalAmountVoucher") != null? voucherTotalAndInv.getBigDecimal("totalAmountVoucher") : BigDecimal.ZERO;
					BigDecimal amount = voucherTotalAndInv.getBigDecimal("amount");
					BigDecimal taxAmount = voucherTotalAndInv.getBigDecimal("taxAmount");
					BigDecimal totalAmount = amount.add(taxAmount);
					if (totalAmountVoucher.compareTo(totalAmount) != 0)
					{
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCVoucherNotFullfitValue", locale) + voucherAndInvTotal.getString("voucherForm") + " " + voucherAndInvTotal.getString("voucherSerial") + " " + voucherAndInvTotal.getString("voucherNumber") + " " + totalAmountVoucher.toString() + " <> " + totalAmount.toString());
					}
				}
				
				List<GenericValue> voucherAndInvoices = delegator.findByAnd("VoucherAndInvTotal", UtilMisc.toMap("voucherForm", voucherAndInvTotal.getString("voucherForm"), "voucherSerial", voucherAndInvTotal.getString("voucherSerial"),  "voucherNumber", voucherAndInvTotal.getString("voucherNumber"), "partyIdFrom", voucherAndInvTotal.getString("partyIdFrom") , "partyId", voucherAndInvTotal.getString("partyId"), "issuedDate", voucherAndInvTotal.get("issuedDate")), null, false);
				for (GenericValue voucherAndInvoice: voucherAndInvoices)
				{
					String strInvoiceId = voucherAndInvoice.getString("invoiceId");
					List<GenericValue> voucherInvoiceAndTotals = delegator.findByAnd("VoucherInvoiceAndTotal", UtilMisc.toMap("invoiceId", strInvoiceId), null, false);
					if(UtilValidate.isEmpty(voucherInvoiceAndTotals)){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceHasNoVoucher", locale));
					}
					GenericValue voucherInvoiceAndTotal = voucherInvoiceAndTotals.get(0);
					GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", strInvoiceId), false);
					if(UtilValidate.isNotEmpty(invoice.get("isVerify")) && invoice.getBoolean("isVerify")){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotVerifyWhenStatusNotReady", locale));
					}
					BigDecimal amount = voucherInvoiceAndTotal.getBigDecimal("amount");
					BigDecimal taxAmount = voucherInvoiceAndTotal.getBigDecimal("taxAmount");
					
					BigDecimal invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice);
					BigDecimal invoiceTaxTotal = InvoiceWorker.getInvoiceTaxTotal(invoice);
					BigDecimal diffTotal = invoiceNoTaxTotal.subtract(amount).setScale(2, RoundingMode.HALF_UP);
					BigDecimal diffTax = invoiceTaxTotal.subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);
					if(diffTax.compareTo(BigDecimal.ZERO) != 0){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceAmountTaxActualAndSystemDiff", locale) + ": " + strInvoiceId);
					}
					Map<String, Object> resultService = dispatcher.runSync("getInvoiceVoucherDiffValueConfig", UtilMisc.toMap("invoiceId", strInvoiceId, "userLogin", context.get("userLogin")));
					String systemValueStr = (String)resultService.get("systemValue");
					BigDecimal systemValue = new BigDecimal(systemValueStr);
					if(diffTotal.abs().compareTo(systemValue) > 0){
						return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "InvoiceAmountActualAndSystemDiffNotExceed", locale) + " " + systemValue.toString());
					}
					
					String rootInvoiceTypeId = AccountUtils.getRootInvoiceType(delegator, invoice.getString("invoiceTypeId"));
					if("PURCHASE_INVOICE".equals(rootInvoiceTypeId) && diffTotal.compareTo(BigDecimal.ZERO) != 0){
						Boolean isInvoiceHaveBilling = AccountUtils.checkInvoiceHaveBilling(delegator, strInvoiceId);
						if(isInvoiceHaveBilling){
							GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "PINV_ADJIMPPO_ITEM"), false);
							if(invoiceItemType == null){
								return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotFindPOInvoiceItemTypeAdj", locale));
							}
							Map<String, Object> ctxMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "invoiceId", strInvoiceId);
							ctxMap.put("amount", diffTotal.negate());
							ctxMap.put("quantity", BigDecimal.ONE);
							ctxMap.put("invoiceItemTypeId", "PINV_ADJIMPPO_ITEM");
							ctxMap.put("description", invoiceItemType.get("description"));
							resultService = dispatcher.runSync("createInvoiceItemOlbius", ctxMap);
							if(!ServiceUtil.isSuccess(resultService)){
								return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
							}
							resultService = dispatcher.runSync("createAcctgTransForAdjPurchaseInvoice", UtilMisc.toMap("userLogin", userLogin, "timeZone", timeZone, "locale", locale, "invoiceId", strInvoiceId));
							if(!ServiceUtil.isSuccess(resultService)){
								return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
							}
						}
					}
					invoice.set("isVerified", "Y");
					invoice.set("verifiedDate", UtilDateTime.nowTimestamp());
					invoice.store();
					
					Map<String, Object> ctxUpdateStatusMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "invoiceId", strInvoiceId);
					resultService = dispatcher.runSync("updateInvoiceNewStatus", ctxUpdateStatusMap);
					
					// update transaction date
					List<GenericValue> acctgTransList = delegator.findByAnd("AcctgTrans", UtilMisc.toMap("invoiceId", strInvoiceId), null, false);
					for (GenericValue acctgTrans: acctgTransList) {
					    GenericValue voucher = delegator.findOne("Voucher", UtilMisc.toMap("voucherId", voucherAndInvoice.get("voucherId")), false);
                        if(!"SALES_INVOICE".equals(rootInvoiceTypeId))
                            acctgTrans.set("transactionDate", voucher.get("voucherCreatedDate"));
                        else acctgTrans.set("transactionDate", invoice.get("invoiceDate"));
                        acctgTrans.store();
                        if (!"Y".equals(acctgTrans.getString("isPosted")) && !acctgTransIdList.contains(";" + acctgTrans.getString("acctgTransId")+ ";")) {
							Map<String, Object> ctxTransMap = UtilMisc.toMap("userLogin", context.get("userLogin"), "acctgTransId", acctgTrans.getString("acctgTransId"));
							resultService = dispatcher.runSync("postAcctgTrans", ctxTransMap);
							acctgTransIdList = acctgTransIdList + ";" + acctgTrans.getString("acctgTransId") + ";";
						}
					}
				}
			}			
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getGetListOrderExternalNotVATJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>)context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions)context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String orderDateStr = parameters.get("orderDate") != null? parameters.get("orderDate")[0] : null;
    	EntityListIterator listIterator = null;
    	if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields.add("-orderDate");
    	}
    	try {
    		Timestamp timestamp = UtilDateTime.nowTimestamp();
    		if(orderDateStr != null){
    			timestamp = new Timestamp(Long.parseLong(orderDateStr));
    		}
    		Timestamp startDate = UtilDateTime.getDayStart(timestamp);
    		Timestamp endDate = UtilDateTime.getDayEnd(timestamp);
    		EntityCondition dateCond = EntityCondition.makeCondition(EntityCondition.makeCondition("orderDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate),
    																	EntityJoinOperator.AND,
    																	EntityCondition.makeCondition("orderDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, endDate));
    		listAllConditions.add(dateCond);
    		List<GenericValue> listOrderInvoiceNoteList = delegator.findList("OrderInvoiceNoteAndOrder", dateCond, null, null, null, false);
    		List<String> orderIdVATList = EntityUtil.getFieldListFromEntityList(listOrderInvoiceNoteList, "orderId", true);
    		if(UtilValidate.isNotEmpty(orderIdVATList)){
    			listAllConditions.add(EntityCondition.makeCondition("orderId", EntityJoinOperator.NOT_IN, orderIdVATList));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"));
			listIterator = EntityMiscUtil.processIterator(parameters, successResult, delegator, "OrderHeaderFullView", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInvoiceItemListByOrderNotVAT(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<String> listOrderId = (List<String>)context.get("listOrderId");
		List<GenericValue> listReturn = FastList.newInstance();
		try {
			EntityCondition orderIdConds = EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, listOrderId);
			List<GenericValue> orderItemBillingList = delegator.findList("OrderItemBilling", orderIdConds, null, null, null, false);
			List<String> invoiceListId = EntityUtil.getFieldListFromEntityList(orderItemBillingList, "invoiceId", true);
			List<GenericValue> productStoreList = delegator.findList("OrderHeader", orderIdConds, null, null, null, false);
			List<String> productStoreListId = EntityUtil.getFieldListFromEntityList(productStoreList, "productStoreId", true);
			String returnDateStr = (String) context.get("returnDate");			
			List<EntityCondition> listReturnCondition = FastList.newInstance();
			Timestamp timestamp = UtilDateTime.nowTimestamp();
    		if(returnDateStr != null){
    			timestamp = new Timestamp(Long.parseLong(returnDateStr));
    		}
    		Timestamp startDate = UtilDateTime.getDayStart(timestamp);
    		Timestamp endDate = UtilDateTime.getDayEnd(timestamp);
    		EntityCondition dateCond = EntityCondition.makeCondition(EntityCondition.makeCondition("entryDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, startDate),
    																	EntityJoinOperator.AND,
    																	EntityCondition.makeCondition("entryDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, endDate));
    		listReturnCondition.add(dateCond);
    		listReturnCondition.add(EntityCondition.makeCondition("destinationFacilityId", EntityJoinOperator.IN, productStoreListId));
			List<GenericValue> returnItemBillingList = delegator.findList("ReturnHeaderItemAndBilling", EntityCondition.makeCondition(listReturnCondition), null, null, null, false);
			List<String> returnInvoiceListId = EntityUtil.getFieldListFromEntityList(returnItemBillingList, "invoiceId", true);
			
			//ModelEntity modelEntity = delegator.getModelEntity("InvoiceItemAndProductGroupBy");
			ModelEntity modelEntity = delegator.getModelEntity("InvoiceItemGroupBy");
			Set<String> selectedField = UtilMisc.toSet(modelEntity.getAllFieldNames());
			selectedField.remove("invoiceId");
			selectedField.remove("invoiceItemSeqId");
			EntityCondition invoiceCond = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.IN, invoiceListId);
			//List<GenericValue> invoianhceItemProductList = delegator.findList("InvoiceItemAndProductGroupBy", invoiceCond, selectedField, UtilMisc.toList("description"), null, false);
			List<GenericValue> invoiceItemProductList = delegator.findList("InvoiceItemGroupBy", invoiceCond, selectedField, UtilMisc.toList("description"), null, false);
			selectedField.remove("productCode");
			selectedField.remove("productId");
			selectedField.add("taxAuthPartyId");
			selectedField.add("taxAuthGeoId");
			selectedField.add("taxAuthorityRateSeqId");
			List<GenericValue> invoiceItemSaletaxList = delegator.findList("InvoiceItemAndSaleTaxGroupBy", invoiceCond, selectedField, null, null, false);
			selectedField.remove("taxAuthPartyId");
			selectedField.remove("taxAuthGeoId");
			selectedField.remove("taxAuthorityRateSeqId");
			List<GenericValue> invoiceItemDiscountList = delegator.findList("InvoiceItemAndDiscountGroupBy", invoiceCond, selectedField, null, null, false);
			listReturn.addAll(invoiceItemProductList);
			listReturn.addAll(invoiceItemSaletaxList);
			listReturn.addAll(invoiceItemDiscountList);
			if(UtilValidate.isNotEmpty(returnInvoiceListId)){
				EntityCondition returnInvoiceCond = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.IN, returnInvoiceListId);
				selectedField = UtilMisc.toSet(modelEntity.getAllFieldNames());
				selectedField.remove("invoiceId");
				selectedField.remove("invoiceItemSeqId");
				List<GenericValue> invoiceItemProductReturnList = delegator.findList("InvoiceItemReturnGroupBy", returnInvoiceCond, selectedField, UtilMisc.toList("description"), null, false);

				selectedField.remove("productCode");
				selectedField.remove("productId");
				selectedField.add("taxAuthPartyId");
				selectedField.add("taxAuthGeoId");
				selectedField.add("taxAuthorityRateSeqId");
				List<GenericValue> invoiceItemSaleTaxReturnList = delegator.findList("InvoiceItemAndSaleTaxRtnGroupBy", returnInvoiceCond, selectedField, UtilMisc.toList("description"), null, false);
				
				selectedField.remove("taxAuthPartyId");
				selectedField.remove("taxAuthGeoId");
				selectedField.remove("taxAuthorityRateSeqId");
				List<GenericValue> invoiceItemDiscountReturnList = delegator.findList("InvoiceItemAndDiscountRtnGroupBy", returnInvoiceCond, selectedField, UtilMisc.toList("description"), null, false);
				listReturn.addAll(invoiceItemDiscountReturnList);
				listReturn.addAll(invoiceItemProductReturnList);
				listReturn.addAll(invoiceItemSaleTaxReturnList);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listReturn", listReturn);
		return successResult;
	}
	
	public static Map<String, Object> getPOSTerminalNotCashHandover(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> listReturn = FastList.newInstance();
		String productStoreId = (String)context.get("productStoreId");
		try {
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(productStore != null){
				String inventoryFacilityId = productStore.getString("inventoryFacilityId");
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("facilityId", inventoryFacilityId));
				conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isCashHandover", null),
						EntityJoinOperator.OR,
						EntityCondition.makeCondition("isCashHandover", Boolean.FALSE)));
				listReturn = delegator.findList("PosTerminal", EntityCondition.makeCondition(conds), null, UtilMisc.toList("terminalName"), null, false);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listReturn", listReturn);
		return successResult; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createCashHandoverTerminal(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Locale locale = (Locale)context.get("locale");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Delegator delegator = dctx.getDelegator();
		List<String> posTerminalIdList = (List<String>)context.get("posTerminalIdList");
		String partyId = (String)context.get("partyId");
		Timestamp dateReceived = (Timestamp)context.get("dateReceived");
		String productStoreId = (String)context.get("productStoreId");
		BigDecimal amount = (BigDecimal)context.get("amount");
		BigDecimal totalAmount = amount.multiply(new BigDecimal(posTerminalIdList.size()));
		try {
			List<EntityCondition> productStoreRoleConds = FastList.newInstance();
			productStoreRoleConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			productStoreRoleConds.add(EntityCondition.makeCondition("partyId", partyId));
			productStoreRoleConds.add(EntityCondition.makeCondition("roleTypeId", "MANAGER"));
			productStoreRoleConds.add(EntityUtil.getFilterByDateExpr(dateReceived));
			List<GenericValue> productStoreRole = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(productStoreRoleConds), null, null, null, false);
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if(UtilValidate.isEmpty(productStoreRole)){
				GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "PartyIsNotManagerProductStore",
						UtilMisc.toMap("partyName", party.get("fullName"), "storeName", productStore.get("storeName"),
								"date", DateUtil.getDateMonthYearDesc(dateReceived)), locale));
			}
			
			String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", orgId), false);
			Map<String, Object> paymentContext = FastMap.newInstance();
			paymentContext.put("userLogin", context.get("userLogin"));
			paymentContext.put("partyIdFrom", partyId);
			paymentContext.put("partyIdTo", orgId);
			paymentContext.put("paymentTypeId", "ADVANCES_PAYMENT");
			paymentContext.put("paymentMethodId", "CASH_VNM");
			paymentContext.put("effectiveDate", dateReceived);
			paymentContext.put("amount", totalAmount);
			paymentContext.put("statusId", "PMNT_NOT_PAID");
			if(partyAcctgPreference != null){
				paymentContext.put("currencyUomId", partyAcctgPreference.get("baseCurrencyUomId"));
			}
			Map<String, Object> resultService = dispatcher.runSync("createPayment", paymentContext);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			List<EntityCondition> posTerminalConds = FastList.newInstance();
			posTerminalConds.add(EntityCondition.makeCondition("posTerminalId", EntityJoinOperator.IN, posTerminalIdList));
			posTerminalConds.add(EntityCondition.makeCondition("facilityId", productStore.get("inventoryFacilityId")));
			List<GenericValue> posTerminalList = delegator.findList("PosTerminal", EntityCondition.makeCondition(posTerminalConds), null, null, null, false);
			for(GenericValue posTerminal: posTerminalList){
				posTerminal.set("isCashHandover", Boolean.TRUE);
				posTerminal.set("cashHandoverAmount", amount);
				posTerminal.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		return successResult;
	}
	
	public static Map<String, Object> updateInvoiceVoucher(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		ByteBuffer documentFile = (ByteBuffer) context.get("uploadedFile");
		String uploadFileNameStr = (String) context.get("_uploadedFile_fileName");
		String _uploadedFile_contentType = (String)context.get("_uploadedFile_contentType");
		String folder = "accounting";
		try {
			String invoiceId = (String) context.get("invoiceId");
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if("Y".equals(invoice.get("isVerified"))){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotUpdateVoucherWhenInvoiceInVerified", locale));
			}
			
			GenericValue voucher = delegator.findOne("Voucher", UtilMisc.toMap("voucherId", (String) context.get("voucherId")), false);
			if (UtilValidate.isNotEmpty(voucher)) {
				voucher.setNonPKFields(context);
				
				if(documentFile != null){
					Map<String, Object> uploadedFileCtx = FastMap.newInstance();
					uploadedFileCtx.put("uploadedFile", documentFile);
					uploadedFileCtx.put("_uploadedFile_fileName", uploadFileNameStr);
					uploadedFileCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
					uploadedFileCtx.put("public", "Y");
					uploadedFileCtx.put("folder", folder);
					uploadedFileCtx.put("userLogin", userLogin);
					Map<String, Object> resultService = dispatcher.runSync("jackrabbitUploadFile", uploadedFileCtx);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
					}
					GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
					String path = (String)resultService.get("path");
					Map<String, Object> dataResourceCtx = FastMap.newInstance();
					dataResourceCtx.put("objectInfo", path);
					dataResourceCtx.put("dataResourceName", uploadFileNameStr);
					dataResourceCtx.put("userLogin", systemUserLogin);
					dataResourceCtx.put("dataResourceTypeId", "IMAGE_OBJECT");
					dataResourceCtx.put("mimeTypeId", _uploadedFile_contentType);
					dataResourceCtx.put("isPublic", "Y");
					resultService = dispatcher.runSync("createDataResource", dataResourceCtx);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
					String dataResourceId = (String) resultService.get("dataResourceId");
					voucher.set("dataResourceId", dataResourceId);
				}
				
				voucher.store();
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
	
	public static Map<String, Object> updateInvoiceNewStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String invoiceId = (String)context.get("invoiceId");
		String newStatusId = null;
		try {
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if(invoice == null){
				return ServiceUtil.returnError("cannot find invoice");
			}
			newStatusId = invoice.getString("newStatusId");
			if("INV_PAID_NEW".equals(newStatusId) || "INV_CANCELLED_NEW".equals(newStatusId)){
				return ServiceUtil.returnSuccess();
			}
			String isVerified = invoice.getString("isVerified");
			if("Y".equals(isVerified)){
				newStatusId = "INV_APPR_NEW";
			}else{
				List<GenericValue> voucherInvoiceList = delegator.findList("VoucherInvoice", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
				if(voucherInvoiceList.size() == 0){
					newStatusId = "INV_NOT_APPR_NEW";
				}else{
					newStatusId = "INV_IN_PROCESS_NEW";
				}
			}
			invoice.set("newStatusId", newStatusId);
			invoice.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} 
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> setInvoiceNewStatus(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String invoiceId = (String)context.get("invoiceId");
		String newStatusId = (String)context.get("newStatusId");
		try {
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if(invoice == null){
				return ServiceUtil.returnError("cannot find invoice");
			}
			invoice.set("newStatusId", newStatusId);
			invoice.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createInvoiceTaxInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue invoiceTaxInfo = delegator.makeValue("InvoiceTaxInfo");
		String invoiceId = (String)context.get("invoiceId"); 
		String partyName = (String)context.get("partyName"); 
		String taxCode = (String)context.get("taxCode"); 
		String address = (String)context.get("address"); 
		String countryGeoId = (String)context.get("countryGeoId"); 
		String stateGeoId = (String)context.get("stateGeoId"); 
		try {
			if(partyName != null && taxCode != null && address != null && countryGeoId != null && stateGeoId != null){
				invoiceTaxInfo.setAllFields(context, false, null, null);
			}else{
				GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
				String invoiceTypeId = invoice.getString("invoiceTypeId");
				String rootInvoiceTypeId = AccountUtils.getRootInvoiceType(delegator, invoiceTypeId);
				String partyId = null;
				invoiceTaxInfo.put("invoiceId", invoiceId);
				if("PURCHASE_INVOICE".equals(rootInvoiceTypeId)){
					partyId = invoice.getString("partyIdFrom");
				}else if("SALES_INVOICE".equals(rootInvoiceTypeId)){
					partyId = invoice.getString("partyId");
				}
				GenericValue partyNameGv = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
				if(partyNameGv != null){
					invoiceTaxInfo.put("partyName", partyNameGv.getString("fullName").trim());
				}
				Map<String, Object> resultService = null;
				resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION"));
				if(resultService.get("contactMechId") != null){
					GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", resultService.get("contactMechId")), false);
					String address1 = (String)resultService.get("address1");
					stateGeoId = (String)resultService.get("stateProvinceGeoId");
					countryGeoId = (String)resultService.get("countryGeoId");
					String districtGeoId = postalAddress.getString("districtGeoId");
					String wardGeoId = postalAddress.getString("wardGeoId");
					address = address1;
					if(wardGeoId != null){
						GenericValue wardGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", wardGeoId), false);
						address += ", " + wardGeo.getString("geoName");
					}
					if(districtGeoId != null){
						GenericValue districtGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
						address += ", " + districtGeo.getString("geoName");
					}
					invoiceTaxInfo.put("address", address);
					invoiceTaxInfo.put("countryGeoId", countryGeoId);
					invoiceTaxInfo.put("stateGeoId", stateGeoId);
				}
				resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"));
				if(resultService.get("contactMechId") != null){
					String contactNumber = (String)resultService.get("contactNumber");
					invoiceTaxInfo.put("phoneNbr", contactNumber);
				}
				Timestamp invoiceDate = invoice.getTimestamp("invoiceDate");
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("partyId", partyId));
				conds.add(EntityUtil.getFilterByDateExpr(invoiceDate));
				List<GenericValue> partyTaxAuthInfoList = delegator.findList("PartyTaxAuthInfo", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
				if(UtilValidate.isNotEmpty(partyTaxAuthInfoList)){
					GenericValue partyTaxAuthInfo = partyTaxAuthInfoList.get(0);
					invoiceTaxInfo.put("taxCode", partyTaxAuthInfo.get("partyTaxId"));
				}
			}
			delegator.create(invoiceTaxInfo);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> editInvoiceTaxInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue invoiceTaxInfo = delegator.makeValue("InvoiceTaxInfo");
		invoiceTaxInfo.setAllFields(context, true, null, null);
		try {
			delegator.createOrStore(invoiceTaxInfo);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}

    public static Map<String, Object> getInvoiceSpecification(DispatchContext dctx, Map<String, Object> context){
        Map<String, Object> retMap = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String invoiceId = (String)context.get("invoiceId");
        try {
            List<GenericValue> orderInvoiceNotes = delegator.findByAnd("OrderInvoiceNote", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            List<String> listInvoices = FastList.newInstance();
            if(UtilValidate.isNotEmpty(orderInvoiceNotes)){
                List<String> listOrderId = EntityUtil.getFieldListFromEntityList(orderInvoiceNotes, "orderId", true);
                List<GenericValue> orderItemBilling = delegator.findList("OrderItemBilling", EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, listOrderId), null, null, null, false);
                listInvoices = EntityUtil.getFieldListFromEntityList(orderItemBilling, "invoiceId", false);
            }else{
                listInvoices.add(invoiceId);
            }
            EntityCondition cond = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.IN, listInvoices);
            ModelEntity productTaxSumModel = delegator.getModelEntity("InvoiceItemProductWithTaxView");
            ModelEntity taxSumModel = delegator.getModelEntity("InvoiceItemAndTaxSum");
            ModelEntity productTax0SumModel = delegator.getModelEntity("InvoiceItemAndVATCategorySum");
            List<String> productTaxSumSelected = productTaxSumModel.getAllFieldNames();
            List<String> taxSumSelected = taxSumModel.getAllFieldNames();
            List<String> productTax0SumSelected = productTax0SumModel.getAllFieldNames();
            productTaxSumSelected.remove("invoiceId");
            taxSumSelected.remove("invoiceId");
            productTax0SumSelected.remove("invoiceId");
            List<GenericValue> productTaxSumList = delegator.findList("InvoiceItemProductWithTaxView", cond, UtilMisc.toSet(productTaxSumSelected), UtilMisc.toList("productName"), null, false);
            List<GenericValue> taxSumList = delegator.findList("InvoiceItemAndTaxSum", cond, UtilMisc.toSet(taxSumSelected), UtilMisc.toList("taxPercentage"), null, false);
            List<GenericValue> productTax0SumList = delegator.findList("InvoiceItemAndVATCategorySum", cond, UtilMisc.toSet(productTax0SumSelected), UtilMisc.toList("productName"), null, false);
            List<Map<String, Object>> discountList = FastList.newInstance();
            List<String> parentInvoiceItemSeqIds = EntityUtil.getFieldListFromEntityList(delegator.findList("InvoiceItem", EntityCondition.makeCondition(
                    UtilMisc.toList(EntityCondition.makeCondition("invoiceId", invoiceId),
                            EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.NOT_EQUAL, null),
                            EntityCondition.makeCondition("taxAuthorityRateSeqId", EntityOperator.NOT_EQUAL, null)
                            )), null, null, null, false), "parentInvoiceItemSeqId", true);
            List<EntityCondition> conds = FastList.newInstance();
            conds.addAll(
                    UtilMisc.toList(EntityCondition.makeCondition("invoiceId", invoiceId),
                            EntityCondition.makeCondition("taxAuthorityRateSeqId", EntityOperator.EQUALS, null),
                            EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, null),
                            EntityCondition.makeCondition("amount", EntityOperator.GREATER_THAN, BigDecimal.ZERO)));
            if(UtilValidate.isNotEmpty(parentInvoiceItemSeqIds)){
                conds.add(EntityCondition.makeCondition("invoiceItemSeqId", EntityOperator.NOT_IN, parentInvoiceItemSeqIds));
            }
            if(UtilValidate.isNotEmpty(productTax0SumList)) {
                List<String> productIds = EntityUtil.getFieldListFromEntityList(productTax0SumList, "productId", false);
                conds.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, productIds));
            }
            List<GenericValue> itemMissingTax = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conds), null, null, null, false);
            List<GenericValue> discount = delegator.findByAnd("InvoiceItemAndDiscountGroupBy", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            conds.clear();
            conds.add(cond);
            conds.add(EntityCondition.makeCondition("taxAuthorityRateSeqId", EntityOperator.NOT_EQUAL, null));
            conds.add(EntityCondition.makeCondition("parentInvoiceItemSeqId", EntityOperator.EQUALS, null));
            List<GenericValue> taxNotIncludeItems = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conds), null, null, null, false);
            Map<String, Map<String, Object>> taxSumMap = FastMap.newInstance();
            for(GenericValue tax : taxNotIncludeItems) {
                GenericValue taxAuthority = EntityUtil.getFirst(delegator.findList("TaxAuthorityRateProduct",
                        EntityCondition.makeCondition("taxAuthorityRateSeqId", tax.get("taxAuthorityRateSeqId")), null, null, null, false));
                GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", tax.get("invoiceId")), false);
                Map<String, Object> taxMap = FastMap.newInstance();
                taxMap.put("invoiceId", tax.get("invoiceId"));
                taxMap.put("taxAuthorityRateSeqId", tax.get("taxAuthorityRateSeqId"));
                taxMap.put("taxPercentage", taxAuthority.get("taxPercentage"));
                taxMap.put("currencyUomId", invoice.get("currencyUomId"));
                taxMap.put("totalTaxAmount", tax.getBigDecimal("amount").multiply(tax.getBigDecimal("quantity")));
                taxMap.put("amountNotTax", BigDecimal.ZERO);
                taxMap.put("amountTaxInc", tax.getBigDecimal("amount").multiply(tax.getBigDecimal("quantity")));
                String authorityRateSeqId = (String) taxMap.get("taxAuthorityRateSeqId");
                if(taxSumMap.get(authorityRateSeqId) == null) {
                    taxSumMap.put(authorityRateSeqId, taxMap);
                } else {
                    Map<String, Object> existed = taxSumMap.get(authorityRateSeqId);
                    BigDecimal totalTaxAmount = (BigDecimal) taxMap.get("totalTaxAmount");
                    BigDecimal amountTaxInc = (BigDecimal) taxMap.get("amountTaxInc");
                    existed.put("totalTaxAmount", ((BigDecimal) existed.get("totalTaxAmount")).add(totalTaxAmount));
                    existed.put("amountTaxInc", ((BigDecimal) existed.get("amountTaxInc")).add(amountTaxInc));
                    taxSumMap.put(authorityRateSeqId, existed);
                }
            }
            for(GenericValue tax : taxSumList) {
                Map<String, Object> taxMap = UtilMisc.toMap(tax);
                String authorityRateSeqId = (String) taxMap.get("taxAuthorityRateSeqId");
                if(taxSumMap.get(authorityRateSeqId) == null) {
                    taxSumMap.put(authorityRateSeqId, taxMap);
                }
                else {
                    Map<String, Object> existed = taxSumMap.get(authorityRateSeqId);
                    BigDecimal totalTaxAmount = (BigDecimal) taxMap.get("totalTaxAmount");
                    BigDecimal amountTaxInc = (BigDecimal) taxMap.get("amountTaxInc");
                    BigDecimal amountNotTax = (BigDecimal) taxMap.get("amountNotTax");
                    existed.put("totalTaxAmount", ((BigDecimal) existed.get("totalTaxAmount")).add(totalTaxAmount));
                    existed.put("amountTaxInc", ((BigDecimal) existed.get("amountTaxInc")).add(amountTaxInc));
                    existed.put("amountNotTax", ((BigDecimal) existed.get("amountNotTax")).add(amountNotTax));
                    taxSumMap.put(authorityRateSeqId, existed);
                }
            }            
            for(GenericValue x : discount) {
                Map<String, Object> item = FastMap.newInstance();
                item.put("quantity", x.get("quantity"));
                item.put("amount", x.get("amount"));
                item.put("invoiceId", x.get("invoiceId"));
                item.put("productName", x.getString("description"));
                item.put("productCode", "");
                item.put("taxPercentage", BigDecimal.ZERO);
                item.put("amountNotTax", x.get("subTotalAmount"));
                item.put("amountTaxInc", x.get("subTotalAmount"));
                item.put("currencyUomId", x.get("currencyUomId"));
                discountList.add(item);
                
            }
            /*Get total mount not tax*/
            BigDecimal invoiceTotalAmount = BigDecimal.ZERO;
            List<Map<String, Object>> productTaxSumListFinal = FastList.newInstance();
            for (GenericValue x : productTaxSumList) {
                Map<String, Object> item = FastMap.newInstance();
                item.put("invoiceId", x.get("invoiceId"));
                item.put("productCode", x.get("productCode"));
                item.put("productName", x.get("productName"));
                item.put("quantityUomId", x.get("quantityUomId"));
                item.put("quantity", x.get("quantity"));
                item.put("amount", x.get("amount"));
                item.put("currencyUomId", x.get("currencyUomId"));
                item.put("taxPercentage", x.get("taxPercentage"));
                item.put("taxDescription", x.get("taxDescription"));
                item.put("totalTaxAmount", x.get("totalTaxAmount"));
                item.put("taxAuthorityRateSeqId", x.get("taxAuthorityRateSeqId"));
                item.put("amountNotTax", x.get("amountNotTax"));
                item.put("amountTaxInc", x.get("amountTaxInc"));
                productTaxSumListFinal.add(item);
                invoiceTotalAmount = invoiceTotalAmount.add(x.getBigDecimal("amountNotTax"));
            }
            List<Map<String, Object>> productTax0SumListFinal = FastList.newInstance();
            for(GenericValue temp : productTax0SumList){
            	Map<String, Object> item = FastMap.newInstance();
            	item.put("quantity", temp.get("quantity"));
            	item.put("invoiceId", temp.get("invoiceId"));
            	item.put("productCode", temp.get("productCode"));
            	item.put("productName", temp.get("productName"));
            	item.put("amount", temp.get("amount"));
            	item.put("quantityUomId", temp.get("quantityUomId"));
            	item.put("currencyUomId", temp.get("currencyUomId"));
            	item.put("uomAbbreviation", temp.get("uomAbbreviation"));
            	item.put("invoiceId", temp.get("invoiceId"));            	
            	productTax0SumListFinal.add(temp);
                invoiceTotalAmount = invoiceTotalAmount.add(temp.getBigDecimal("amountNotTax"));
            }
            	               
            List <GenericValue> discountOnProds = delegator.findList("InvoiceItemAndProdDiscount", EntityCondition.makeCondition("invoiceId", invoiceId) , null, null, null, false);           
            for (GenericValue x : itemMissingTax) {
            	Map<String, Object> item = FastMap.newInstance();
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", x.get("productId")), false);
                GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", x.get("invoiceId")), false);
                item.put("invoiceId", x.get("invoiceId"));
                if(UtilValidate.isNotEmpty(product)) {
                    item.put("productCode", product.get("productCode"));
                    item.put("productName", product.get("productName"));
                }
                item.put("invoiceItemSeqId", x.get("invoiceItemSeqId"));
                item.put("productId", x.get("productId"));
                item.put("quantityUomId", x.get("quantityUomId"));
                item.put("quantity", x.get("quantity"));
                item.put("amount", x.get("amount"));
                item.put("currencyUomId", invoice.get("currencyUomId"));
                item.put("amountNotTax", x.getBigDecimal("amount").multiply(x.getBigDecimal("quantity")));
                item.put("amountTaxInc", x.getBigDecimal("amount").multiply(x.getBigDecimal("quantity")));
                
                /*Check if this product is promotional*/                
                GenericValue filterResult = EntityUtil.getFirst(EntityUtil.filterByCondition(discountOnProds, EntityCondition.makeCondition(
            		UtilMisc.toList(
  		                      EntityCondition.makeCondition("invoiceId", x.get("invoiceId")),
  		                      EntityCondition.makeCondition("productId", x.get("productId")),
  		                      EntityCondition.makeCondition("parentInvoiceItemSeqId", x.get("invoiceItemSeqId"))
            				))));
                if(UtilValidate.isNotEmpty(filterResult)){
                	BigDecimal totalAmountDiscountInc = filterResult.getBigDecimal("totalAmountDiscountInc");
                	BigDecimal amtNotTax = (BigDecimal) item.get("amountNotTax");
            		item.put("amountNotTax", amtNotTax.add(totalAmountDiscountInc));
                    item.put("amountTaxInc", amtNotTax.add(totalAmountDiscountInc));
                    item.put("isPromotionalProduct", "true");
                }
            	productTax0SumListFinal.add(item);
            	invoiceTotalAmount = invoiceTotalAmount.add(x.getBigDecimal("amount").multiply(x.getBigDecimal("quantity")));	           
            }
            
            /* Get discount on total amount invoice*/
            conds.clear();
            conds.add(cond);
            conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "ITM_DISCOUNT_ADJ"));
            conds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, null));
            List<GenericValue> discountTotalOnInvList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conds), null, null, null, false);
            BigDecimal  discountTotalOnInv = BigDecimal.ZERO; 
            for(GenericValue discountItem : discountTotalOnInvList){
            	discountTotalOnInv = discountTotalOnInv.add(discountItem.getBigDecimal("amount").multiply(discountItem.getBigDecimal("quantity")));
            }
            
            BigDecimal discountDivideTotalAmount = discountTotalOnInv.divide(invoiceTotalAmount, 6 , RoundingMode.HALF_UP);
            Iterator<Map.Entry<String, Map<String,Object>>> itr = taxSumMap.entrySet().iterator();
            while(itr.hasNext())
            {
                 Map.Entry<String, Map<String,Object>> entry = itr.next(); 
                 Map<String,Object> tempMap = entry.getValue();
                 BigDecimal amountNotTaxFinal = (BigDecimal)tempMap.get("amountNotTax");
                 BigDecimal amountTaxIncFinal = (BigDecimal)tempMap.get("amountTaxInc");
                 BigDecimal addAmount = amountNotTaxFinal.multiply(discountDivideTotalAmount);
                 tempMap.put("amountNotTax", amountNotTaxFinal.add(addAmount));
                 tempMap.put("amountTaxInc", amountTaxIncFinal.add(addAmount));
                 taxSumMap.put(entry.getKey(), tempMap);
            }
            
            List<Map<String, Object>> productTax0SumListFinalFinal = FastList.newInstance();
            for( Map<String, Object> tempProdTax0 : productTax0SumListFinal){
            	if(invoiceTotalAmount != BigDecimal.ZERO){
                	BigDecimal amountNotTaxFinal = (BigDecimal)tempProdTax0.get("amountNotTax");
                	BigDecimal amountTaxIncFinal = (BigDecimal)tempProdTax0.get("amountTaxInc");
                	BigDecimal addAmount = amountNotTaxFinal.multiply(discountDivideTotalAmount);
                	tempProdTax0.put("amountNotTax", amountNotTaxFinal.add(addAmount));
                	tempProdTax0.put("amountTaxInc", amountTaxIncFinal.add(addAmount));
                	productTax0SumListFinalFinal.add(tempProdTax0);	
            	}
            }
            /*Get discount on tax product*/
            for( GenericValue disOnProd: discountOnProds){
            	String prodId =  disOnProd.getString("productId");
            	String parentInvoiceItemSeqId = disOnProd.getString("parentInvoiceItemSeqId");
            	GenericValue taxAuthor= EntityUtil.getFirst(delegator.findList("InvoiceItemTaxView", EntityCondition.makeCondition(
            		UtilMisc.toList(
  		                      EntityCondition.makeCondition("invoiceId", invoiceId),
  		                      EntityCondition.makeCondition("productId", prodId),
  		                      EntityCondition.makeCondition("parentInvoiceItemSeqId", parentInvoiceItemSeqId)
            				)), null, null, null, false));
            	if(UtilValidate.isNotEmpty(taxAuthor)){
                	String taxAuthorityRateSeqId = taxAuthor.getString("taxAuthorityRateSeqId");
                	if(taxSumMap.get(taxAuthorityRateSeqId) != null) {
                		Map<String, Object> existed = taxSumMap.get(taxAuthorityRateSeqId);
                		BigDecimal totalAmountDiscountInc = disOnProd.getBigDecimal("totalAmountDiscountInc");
                		existed.put("amountNotTax", ((BigDecimal) existed.get("amountNotTax")).add(totalAmountDiscountInc));
                		existed.put("amountTaxInc", ((BigDecimal) existed.get("amountTaxInc")).add(totalAmountDiscountInc));
                		taxSumMap.put(taxAuthorityRateSeqId, existed);		          
                	}
            	}
            }
            List<Map<String, Object>> taxSumListFinal = new ArrayList<>(taxSumMap.values());
            retMap.put("listProduct", productTaxSumListFinal);
            retMap.put("listDiscount", discountList);
            retMap.put("listProductTax0", productTax0SumListFinalFinal);
            retMap.put("listSalesTax", taxSumListFinal);            
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        return retMap;
    }
	
	public static Map<String, Object> createOrStoreVoucherInvoiceSystemConfig(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String systemConfigId = (String)context.get("systemConfigId");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("systemConfigId", systemConfigId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> voucherInvoiceSystemConfigList = delegator.findList("VoucherInvoiceSystemConfig", EntityCondition.makeCondition(conds), null, null, null, false);
			for(GenericValue voucherInvoiceSystemConfig: voucherInvoiceSystemConfigList){
				voucherInvoiceSystemConfig.set("thruDate", UtilDateTime.nowTimestamp());
				voucherInvoiceSystemConfig.store();
			}
			GenericValue newEntity = delegator.makeValue("VoucherInvoiceSystemConfig");
			newEntity.setNonPKFields(context);
			newEntity.set("systemConfigId", systemConfigId);
			newEntity.set("fromDate", UtilDateTime.nowTimestamp());
			delegator.create(newEntity);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getInvoiceVoucherDiffValueConfig(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String invoiceId = (String)context.get("invoiceId");
		try {
			String systemConfigId = EntityUtilProperties.getPropertyValue("baseaccounting.properties", "invoice.voucher.diff.value.system.id", null, delegator);
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("systemConfigId", systemConfigId));
			retMap.put("systemValue", String.valueOf(0));
			if(invoice != null){
				List<GenericValue> voucherInvoiceSystemConfigList = null;
				String isVerified = invoice.getString("isVerified");
				Timestamp verifiedDate = invoice.getTimestamp("verifiedDate");
				if("Y".equals(isVerified)){
					if(verifiedDate != null){
						cond.add(EntityUtil.getFilterByDateExpr(verifiedDate));
						voucherInvoiceSystemConfigList = delegator.findList("VoucherInvoiceSystemConfig", EntityCondition.makeCondition(cond), null, UtilMisc.toList("-fromDate"), null, false);
						
					}
				}else{
					cond.add(EntityUtil.getFilterByDateExpr());
					voucherInvoiceSystemConfigList = delegator.findList("VoucherInvoiceSystemConfig", EntityCondition.makeCondition(cond), null, UtilMisc.toList("-fromDate"), null, false);
				}
				if(UtilValidate.isNotEmpty(voucherInvoiceSystemConfigList)){
					GenericValue voucherInvoiceSystemConfig = voucherInvoiceSystemConfigList.get(0);
					retMap.put("systemValue", voucherInvoiceSystemConfig.getString("systemValue"));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> getDeliveryItemDetailByInvoice(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		String invoiceId = (String)context.get("invoiceId");
		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			List<GenericValue> orderShipmentReceiptList = delegator.findByAnd("OrderShipmentReceipt", UtilMisc.toMap("invoiceId", invoiceId), null, false);
			List<String> shipmentIds = EntityUtil.getFieldListFromEntityList(orderShipmentReceiptList, "shipmentId", true);
			if(UtilValidate.isNotEmpty(shipmentIds)){
				conds.add(EntityCondition.makeCondition("shipmentId", EntityJoinOperator.IN, shipmentIds));
				List<GenericValue> deliveryItemList = delegator.findList("DeliveryItemAndChange", EntityCondition.makeCondition(conds), null, null, null, false);
				retMap.put("deliveryItemList", deliveryItemList);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> modifyDeliveryItem(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String deliveryItemChangeParam = (String)context.get("deliveryItemChange");
		String invoiceId = (String)context.get("invoiceId");
		JSONArray deliveryItemChangeArr = JSONArray.fromObject(deliveryItemChangeParam);
		try {
			String currentOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			for(int i = 0; i < deliveryItemChangeArr.size(); i++){
				JSONObject deliveryItemChangeJson = deliveryItemChangeArr.getJSONObject(i);
				String deliveryId = deliveryItemChangeJson.getString("deliveryId");
				String deliveryItemSeqId = deliveryItemChangeJson.getString("deliveryItemSeqId");
				String invoiceItemSeqId = deliveryItemChangeJson.getString("invoiceItemSeqId");
				String shipmentReceiptId = deliveryItemChangeJson.getString("shipmentReceiptId");
				String orderId = deliveryItemChangeJson.getString("orderId");
				String orderItemSeqId = deliveryItemChangeJson.getString("orderItemSeqId");
				String invQuantityStr = deliveryItemChangeJson.has("invQuantity")? deliveryItemChangeJson.getString("invQuantity") : null;
				String editQuantityStr = deliveryItemChangeJson.has("editQuantity")? deliveryItemChangeJson.getString("editQuantity") : null;
				String amountEditStr = deliveryItemChangeJson.has("amountEdit")? deliveryItemChangeJson.getString("amountEdit") : null;
				String amountStr = deliveryItemChangeJson.has("amount")? deliveryItemChangeJson.getString("amount") : null;
				BigDecimal quantity = invQuantityStr != null? new BigDecimal(invQuantityStr) : BigDecimal.ONE.negate();
				BigDecimal editQuantity = editQuantityStr != null? new BigDecimal(editQuantityStr) : BigDecimal.ONE.negate();
				BigDecimal amount = amountStr != null? new BigDecimal(amountStr) : BigDecimal.ONE.negate();
				BigDecimal amountEdit = amountEditStr != null? new BigDecimal(amountEditStr) : BigDecimal.ONE.negate();
				List<GenericValue> deliveryItemChangeList = delegator.findByAnd("DeliveryItemChangeAndII", UtilMisc.toMap("deliveryId", deliveryId, "deliveryItemSeqId", deliveryItemSeqId,
						"shipmentReceiptId", shipmentReceiptId), UtilMisc.toList("-createdDate"), false);
				GenericValue deliveryItemChange = EntityUtil.getFirst(deliveryItemChangeList);
				if(deliveryItemChange != null){
					quantity = deliveryItemChange.getBigDecimal("quantity");
					amount = deliveryItemChange.getBigDecimal("amount");
				}
				if((editQuantity.compareTo(BigDecimal.ONE.negate()) > 0 && editQuantity.compareTo(quantity) != 0) 
						|| amountEdit.compareTo(amount) != 0){
					GenericValue orderItemBilling = delegator.findOne("OrderItemBilling", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId,
							"invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId), false);
					if(UtilValidate.isNotEmpty(orderItemBilling)){
						if (deliveryItemChange != null) {
							invoiceItemSeqId = deliveryItemChange.getString("invoiceItemSeqId");
						}
						GenericValue invoiceItem = delegator.findOne("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId), false);
						GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderItemBilling.get("orderId"), "orderItemSeqId", orderItemBilling.get("orderItemSeqId")), false);
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", invoiceItem.get("productId")), false);
						String invQuantityUomId = orderItem.getString("quantityUomId");
						String deliveryQuantityUomId = product.getString("quantityUomId");
						Map<String, Object> deliveryItemInvoiceItemMap = FastMap.newInstance();
						deliveryItemInvoiceItemMap.put("userLogin", userLogin);
						deliveryItemInvoiceItemMap.put("deliveryId", deliveryId);
						deliveryItemInvoiceItemMap.put("deliveryItemSeqId", deliveryItemSeqId);
						deliveryItemInvoiceItemMap.put("shipmentReceiptId", shipmentReceiptId);
						deliveryItemInvoiceItemMap.put("invoiceId", invoiceId);
						/**=============== tao invoice_item voi so luong nhap moi hoac gia' nhap moi ================*/
						if(editQuantity.compareTo(BigDecimal.ZERO) < 0){
							editQuantity = invoiceItem.getBigDecimal("quantity");
						}
						if(quantity.compareTo(BigDecimal.ZERO) < 0){
							quantity = invoiceItem.getBigDecimal("quantity");
						}
						if(amountEdit.compareTo(BigDecimal.ZERO) < 0){
							amountEdit = invoiceItem.getBigDecimal("amount");
						}
						
						BigDecimal quantityBase = quantity, amountEditBase = amountEdit, editQuantityBase = editQuantity;
						if(!invQuantityUomId.equals(deliveryQuantityUomId)){
							List<EntityCondition> conds = FastList.newInstance();
							conds.add(EntityCondition.makeCondition("productId", product.get("productId")));
							conds.add(EntityCondition.makeCondition("uomFromId", invQuantityUomId));
							conds.add(EntityCondition.makeCondition("uomToId", deliveryQuantityUomId));
							conds.add(EntityUtil.getFilterByDateExpr());
							List<GenericValue> configPackingList = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-fromDate"), null, false);
							if(UtilValidate.isNotEmpty(configPackingList)){
								GenericValue configPacking = configPackingList.get(0);
								BigDecimal quantityConvert = configPacking.getBigDecimal("quantityConvert");
								quantityBase = quantity.multiply(quantityConvert);
								editQuantityBase = editQuantity.multiply(quantityConvert);
								amountEditBase = amountEdit.divide(quantityConvert, 2, RoundingMode.HALF_UP);
							}
						}
						if(amountEdit.compareTo(invoiceItem.getBigDecimal("amount")) != 0){
							amountEdit = AccountUtils.calculateUnitPriceInvoiceItem(delegator, amountEdit, invoiceItem.getString("productId"), invQuantityUomId, deliveryQuantityUomId);
						}
						Map<String, Object> invoiceItemMap = invoiceItem.getAllFields();
						invoiceItemMap.put("userLogin", userLogin);
						invoiceItemMap.remove("invoiceItemSeqId");
						invoiceItemMap.put("quantity", editQuantity);
						invoiceItemMap.put("amount", amountEdit);
						Map<String, Object> resultService = dispatcher.runSync("createInvoiceItem", invoiceItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						String newInvoiceItemSeqId  = (String) resultService.get("invoiceItemSeqId");
						/**link invoice_item voi delivery_item **/
						deliveryItemInvoiceItemMap.put("invoiceItemSeqId", resultService.get("invoiceItemSeqId"));
						deliveryItemInvoiceItemMap.put("modifiedUserLoginId", userLogin.getString("userLoginId"));
						deliveryItemInvoiceItemMap.put("createdDate", UtilDateTime.nowTimestamp());
						resultService = dispatcher.runSync("createDeliveryItemChange", deliveryItemInvoiceItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						
						/**============ tao invoice_item nguoc lai voi invoice_item da nhap, de tong bang 0, coi nhu xuat bu tru ==========*/
						invoiceItemMap.remove("invoiceItemSeqId");
						invoiceItemMap.put("quantity", quantity.negate());
						invoiceItemMap.put("amount", invoiceItem.get("amount"));
						invoiceItemMap.put("invoiceItemTypeId", "INVOICE_ADJ");
						resultService = dispatcher.runSync("createInvoiceItem", invoiceItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						
						/**============ update lai invoice_item_type_id cua invoice_item cu ==========*/
						resultService = dispatcher.runSync("updateInvoiceItem", UtilMisc.toMap("invoiceId", invoiceId,
																				"invoiceItemSeqId", invoiceItemSeqId, 
																				"userLogin", userLogin, 
																				"invoiceItemTypeId", "INVOICE_ADJ"));
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						
						/**============== thuc hien thao tac xuat/nhap kho ===========================**/
						GenericValue delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", deliveryId), false);
						GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
						Map<String, Object> productAvgCostResult = dispatcher.runSync("getProductAverageCostBaseOlbius", UtilMisc.toMap("facilityId", delivery.get("destFacilityId"),
																																		"productId", invoiceItem.get("productId"),
																																		"ownerPartyId", currentOrgId,
																																		"userLogin", userLogin));
						Map<String, Object> editInventoryItemMap = FastMap.newInstance();
						editInventoryItemMap.put("userLogin", userLogin);
						editInventoryItemMap.put("productId", invoiceItem.get("productId"));
						editInventoryItemMap.put("facilityId", delivery.get("destFacilityId"));
						editInventoryItemMap.put("unitCost", productAvgCostResult.get("unitCost"));
						editInventoryItemMap.put("purCost", productAvgCostResult.get("purCost"));
						editInventoryItemMap.put("ownerPartyId", currentOrgId);
						editInventoryItemMap.put("orderId", orderId);
						editInventoryItemMap.put("orderItemSeqId", orderItemSeqId);
						editInventoryItemMap.put("quantity", quantityBase);
						editInventoryItemMap.put("currencyUomId", invoice.get("currencyUomId"));
						
						BigDecimal exportCost = (BigDecimal) invoiceItem.get("amount");
						/**============== xuat kho ================**/
						resultService = dispatcher.runSync("exportProductFromFacility", editInventoryItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						/**============== tao but toan xuat kho============**/
						String reciprocalSeqId = UtilFormatOut.formatPaddedNumber(1, 5);
						GenericValue productType = delegator.findOne("ProductType", UtilMisc.toMap("productTypeId", product.get("productTypeId")), false);
						Map<String, Object> createAcctgTransAndEntriesMap = FastMap.newInstance();
						List<GenericValue> acctgTransEntriesExp = FastList.newInstance();
						GenericValue creditEntryExp = delegator.makeValue("AcctgTransEntry");
						creditEntryExp.put("debitCreditFlag", "C");
						creditEntryExp.put("organizationPartyId", currentOrgId);
						creditEntryExp.put("glAccountTypeId", productType.get("glAccountTypeId"));
						creditEntryExp.put("productId", invoiceItem.get("productId"));
						creditEntryExp.put("partyId", invoice.get("partyIdFrom"));
						creditEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
                        creditEntryExp.put("origAmount", exportCost.multiply(quantityBase));
                        creditEntryExp.put("amount", invoice.getBigDecimal("conversionFactor").multiply(exportCost).multiply(quantityBase));
						creditEntryExp.put("origCurrencyUomId", invoice.get("currencyUomId"));
						creditEntryExp.put("reciprocalSeqId", reciprocalSeqId);
						creditEntryExp.put("invoiceItemSeqId", invoiceItemSeqId);
						acctgTransEntriesExp.add(creditEntryExp);
						
						GenericValue debitEntryExp = delegator.makeValue("AcctgTransEntry");
						debitEntryExp.put("debitCreditFlag", "D");
						debitEntryExp.put("organizationPartyId", currentOrgId);
						debitEntryExp.put("glAccountTypeId", "UNINVOICED_SHIP_RCPT");
						debitEntryExp.put("productId", invoiceItem.get("productId"));
						debitEntryExp.put("partyId", invoice.get("partyIdFrom"));
						debitEntryExp.put("roleTypeId", "BILL_FROM_VENDOR");
						debitEntryExp.put("origAmount", exportCost.multiply(quantityBase));
                        debitEntryExp.put("amount", invoice.getBigDecimal("conversionFactor").multiply(exportCost).multiply(quantityBase));
                        debitEntryExp.put("origCurrencyUomId", invoice.get("currencyUomId"));
						debitEntryExp.put("reciprocalSeqId", reciprocalSeqId);
						debitEntryExp.put("invoiceItemSeqId", invoiceItemSeqId);
						acctgTransEntriesExp.add(debitEntryExp);
						
						createAcctgTransAndEntriesMap.put("userLogin", userLogin);
						createAcctgTransAndEntriesMap.put("glFiscalTypeId", "ACTUAL");
						createAcctgTransAndEntriesMap.put("acctgTransTypeId", "EXP_ADJ_INVOICE");
						createAcctgTransAndEntriesMap.put("partyId", invoice.get("partyIdFrom"));
						createAcctgTransAndEntriesMap.put("invoiceId", invoiceId);
						createAcctgTransAndEntriesMap.put("acctgTransEntries", acctgTransEntriesExp);
						resultService = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						/**============= nhap kho =================**/
						editInventoryItemMap.put("quantity", editQuantityBase);
						editInventoryItemMap.put("unitCost", amountEditBase);
						editInventoryItemMap.put("purCost", BigDecimal.ZERO);
						resultService = dispatcher.runSync("importProductToFacility", editInventoryItemMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
						/**========== tao but toan nhap kho =================**/
						List<GenericValue> acctgTransEntriesImp = FastList.newInstance();
						GenericValue creditEntryImp = delegator.makeValue("AcctgTransEntry");
						creditEntryImp.put("debitCreditFlag", "C");
						creditEntryImp.put("organizationPartyId", currentOrgId);
						creditEntryImp.put("glAccountTypeId", "UNINVOICED_SHIP_RCPT");
						creditEntryImp.put("productId", invoiceItem.get("productId"));
						creditEntryImp.put("partyId", invoice.get("partyIdFrom"));
						creditEntryImp.put("roleTypeId", "BILL_FROM_VENDOR");
						creditEntryImp.put("origAmount", amountEdit.multiply(editQuantity));
                        creditEntryImp.put("amount", invoice.getBigDecimal("conversionFactor").multiply(amountEdit).multiply(editQuantity));
                        creditEntryImp.put("origCurrencyUomId", invoice.get("currencyUomId"));
						creditEntryImp.put("reciprocalSeqId", reciprocalSeqId);
						creditEntryImp.put("invoiceItemSeqId", newInvoiceItemSeqId);
						acctgTransEntriesImp.add(creditEntryImp);
						
						GenericValue debitEntryImp = delegator.makeValue("AcctgTransEntry");
						debitEntryImp.put("debitCreditFlag", "D");
						debitEntryImp.put("organizationPartyId", currentOrgId);
						debitEntryImp.put("glAccountTypeId", productType.get("glAccountTypeId"));
						debitEntryImp.put("productId", invoiceItem.get("productId"));
						debitEntryImp.put("partyId", invoice.get("partyIdFrom"));
						debitEntryImp.put("roleTypeId", "BILL_FROM_VENDOR");
						debitEntryImp.put("origAmount", amountEdit.multiply(editQuantity));
                        debitEntryImp.put("amount", invoice.getBigDecimal("conversionFactor").multiply(amountEdit).multiply(editQuantity));
						debitEntryImp.put("origCurrencyUomId", invoice.get("currencyUomId"));
						debitEntryImp.put("reciprocalSeqId", reciprocalSeqId);
						debitEntryImp.put("invoiceItemSeqId", newInvoiceItemSeqId);
						acctgTransEntriesImp.add(debitEntryImp);
						createAcctgTransAndEntriesMap.put("acctgTransEntries", acctgTransEntriesImp);
						createAcctgTransAndEntriesMap.put("acctgTransTypeId", "IMP_ADJ_INVOICE");
						resultService = dispatcher.runSync("createAcctgTransAndEntries", createAcctgTransAndEntriesMap);
						if(!ServiceUtil.isSuccess(resultService)){
							return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
	}
	
	public static Map<String, Object> createDeliveryItemChange(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		GenericValue deliveryItemChange = delegator.makeValue("DeliveryItemChange");
		deliveryItemChange.setAllFields(context, true, null, null);
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		try {
			String deliveryItemChangeId = delegator.getNextSeqId("DeliveryItemChange");
			deliveryItemChange.put("deliveryItemChangeId", deliveryItemChangeId);
			delegator.create(deliveryItemChange);
			retMap.put("deliveryItemChangeId", deliveryItemChangeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> exportProductFromFacility(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String productId = (String)context.get("productId");
		String facilityId = (String)context.get("facilityId");
		String ownerPartyId = (String)context.get("ownerPartyId");
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		BigDecimal unitCost = (BigDecimal)context.get("unitCost");
		BigDecimal purCost = (BigDecimal)context.get("purCost");
		BigDecimal totalExportQty = (BigDecimal)context.get("quantity");
		String currencyUomId = (String)context.get("currencyUomId");
		
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String requireAmount = null;
		if (UtilValidate.isNotEmpty(product)) {
			requireAmount = product.getString("requireAmount");
		}
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityCondition.makeCondition("facilityId", facilityId));
		conds.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
		try {
			List<GenericValue> inventoryItemList = delegator.findList("InventoryItem", EntityCondition.makeCondition(conds), null, UtilMisc.toList("datetimeReceived"), null, false);
			Map<String, Object> resultService = null;
			Map<String, Object> inventoryItemDetailMap = FastMap.newInstance();
			inventoryItemDetailMap.put("orderId", orderId);
			inventoryItemDetailMap.put("orderItemSeqId", orderItemSeqId);
			inventoryItemDetailMap.put("userLogin", userLogin);
			if(UtilValidate.isNotEmpty(inventoryItemList)){
				for(GenericValue inventoryItem: inventoryItemList){
					BigDecimal exportQty =  inventoryItem.getBigDecimal("quantityOnHandTotal");
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						exportQty = inventoryItem.getBigDecimal("amountOnHandTotal");
					}
					if(exportQty.compareTo(totalExportQty) > 0){
						exportQty = totalExportQty;
					}
					totalExportQty = totalExportQty.subtract(exportQty);
					inventoryItemDetailMap.put("inventoryItemId", inventoryItem.get("inventoryItemId"));
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						inventoryItemDetailMap.put("availableToPromiseDiff", BigDecimal.ONE.negate());
						inventoryItemDetailMap.put("quantityOnHandDiff", BigDecimal.ONE.negate());
						inventoryItemDetailMap.put("amountOnHandDiff", exportQty.negate());
					} else {
						inventoryItemDetailMap.put("availableToPromiseDiff", exportQty.negate());
						inventoryItemDetailMap.put("quantityOnHandDiff", exportQty.negate());
					}
					resultService = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
					}
					if(totalExportQty.compareTo(BigDecimal.ZERO) <= 0){
						break;
					}
				}
			}
			if(totalExportQty.compareTo(BigDecimal.ZERO) > 0){
				Map<String, Object> inventoryItemMap = FastMap.newInstance();
				inventoryItemMap.put("userLogin", userLogin);
				inventoryItemMap.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				inventoryItemMap.put("productId", productId);
				inventoryItemMap.put("facilityId", facilityId);
				inventoryItemMap.put("purCost", purCost);
				inventoryItemMap.put("unitCost", unitCost);
				inventoryItemMap.put("ownerPartyId", ownerPartyId);
				inventoryItemMap.put("currencyUomId", currencyUomId);
				resultService = dispatcher.runSync("createInventoryItem", inventoryItemMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
				inventoryItemDetailMap.put("inventoryItemId", resultService.get("inventoryItemId"));
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					inventoryItemDetailMap.put("availableToPromiseDiff", BigDecimal.ONE.negate());
					inventoryItemDetailMap.put("quantityOnHandDiff", BigDecimal.ONE.negate());
					inventoryItemDetailMap.put("amountOnHandDiff", totalExportQty.negate());
				} else {
					inventoryItemDetailMap.put("availableToPromiseDiff", totalExportQty.negate());
					inventoryItemDetailMap.put("quantityOnHandDiff", totalExportQty.negate());
				}
				resultService = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailMap);
				if(!ServiceUtil.isSuccess(resultService)){
					return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> importProductToFacility(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String productId = (String)context.get("productId");
		String facilityId = (String)context.get("facilityId");
		String ownerPartyId = (String)context.get("ownerPartyId");
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		String currencyUomId = (String)context.get("currencyUomId");
		BigDecimal unitCost = (BigDecimal)context.get("unitCost");
		BigDecimal purCost = (BigDecimal)context.get("purCost");
		BigDecimal quantity = (BigDecimal)context.get("quantity");
		String oldInventoryItemId = "";
		if(UtilValidate.isNotEmpty(context.get("oldInventoryItemId"))) {
            oldInventoryItemId = (String) context.get("oldInventoryItemId");
        }
		
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String requireAmount = null;
		if (UtilValidate.isNotEmpty(product)) {
			requireAmount = product.getString("requireAmount");
		}
		
		Map<String, Object> inventoryItemMap = FastMap.newInstance();
		inventoryItemMap.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
		inventoryItemMap.put("userLogin", userLogin);
		inventoryItemMap.put("productId", productId);
		inventoryItemMap.put("facilityId", facilityId);
		inventoryItemMap.put("purCost", purCost);
		inventoryItemMap.put("unitCost", unitCost);
		inventoryItemMap.put("ownerPartyId", ownerPartyId);
		inventoryItemMap.put("currencyUomId", currencyUomId);
		if(UtilValidate.isNotEmpty(oldInventoryItemId)) {
		    GenericValue oldInventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", oldInventoryItemId), false);
		    inventoryItemMap.put("datetimeManufactured", oldInventoryItem.get("datetimeManufactured"));
		    inventoryItemMap.put("expireDate", oldInventoryItem.get("expireDate"));
		    inventoryItemMap.put("lotId", oldInventoryItem.get("lotId"));
        }
		try {
			Map<String, Object> resultService = dispatcher.runSync("createInventoryItem", inventoryItemMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			String inventoryItemId  = (String) resultService.get("inventoryItemId");
			Map<String, Object> inventoryItemDetailMap = FastMap.newInstance();
			inventoryItemDetailMap.put("orderId", orderId);
			inventoryItemDetailMap.put("orderItemSeqId", orderItemSeqId);
			inventoryItemDetailMap.put("userLogin", userLogin);
			inventoryItemDetailMap.put("inventoryItemId", resultService.get("inventoryItemId"));
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				inventoryItemDetailMap.put("availableToPromiseDiff", BigDecimal.ONE);
				inventoryItemDetailMap.put("quantityOnHandDiff", BigDecimal.ONE);
				inventoryItemDetailMap.put("accountingQuantityDiff", BigDecimal.ZERO);
				inventoryItemDetailMap.put("amountOnHandDiff", quantity);
			} else {
				inventoryItemDetailMap.put("availableToPromiseDiff", quantity);
				inventoryItemDetailMap.put("quantityOnHandDiff", quantity);
				inventoryItemDetailMap.put("accountingQuantityDiff", quantity);
			}
			
			resultService = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}
			
			Map<String, Object> productAverageCostMap = FastMap.newInstance();
			productAverageCostMap.put("facilityId", facilityId);
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				productAverageCostMap.put("quantityAccepted", BigDecimal.ONE); 
				productAverageCostMap.put("amountAccepted", quantity);
			} else {
				productAverageCostMap.put("quantityAccepted", quantity); 
			}
			productAverageCostMap.put("productId", productId);
			productAverageCostMap.put("inventoryItemId", inventoryItemId);
			productAverageCostMap.put("userLogin", userLogin);
			
			resultService = dispatcher.runSync("updateProductAverageCostOnReceiveInventory", productAverageCostMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
			}			
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> cancelShipmentAndDeliveryByShipment(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        List<String> shipmentIds = (List<String>)context.get("shipmentIds");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        EntityCondition shipmentConds = EntityCondition.makeCondition("shipmentId", EntityJoinOperator.IN, shipmentIds);
        try {
            String currentOrgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            List<GenericValue> delveryList = delegator.findList("Delivery", shipmentConds, null, null, null, false);
            List<String> deliveryIds = EntityUtil.getFieldListFromEntityList(delveryList, "deliveryId", true);
            EntityCondition deliveryConds = EntityCondition.makeCondition("deliveryId", EntityJoinOperator.IN, deliveryIds);
            delegator.storeByCondition("Delivery", UtilMisc.toMap("statusId", "DLV_CANCELLED"), shipmentConds);
            delegator.storeByCondition("DeliveryItem", UtilMisc.toMap("statusId", "DELI_ITEM_CANCELLED"), deliveryConds);
            Map<String, Object> resultService = null;

            /** ====== Huy phieu soan tuong ung ==== **/
            List<String> pickBinIds = EntityUtil.getFieldListFromEntityList(delveryList, "picklistBinId", true);
            EntityCondition pickBinConds = EntityCondition.makeCondition("picklistBinId", EntityJoinOperator.IN, pickBinIds);
            delegator.storeByCondition("PicklistBin", UtilMisc.toMap("binStatusId", "PICKBIN_CANCELLED"), pickBinConds);
            delegator.storeByCondition("PicklistItem", UtilMisc.toMap("itemStatusId", "PICKITEM_CANCELLED"), pickBinConds);

            /**====== nhap lai san pham vao kho ==========**/
            for(String shipmentId: shipmentIds){
                GenericValue shipment = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentId), false);
                String facilityId = shipment.getString("originFacilityId");
                if(facilityId == null){
                    shipment.getString("destinationFacilityId");
                }
                List<GenericValue> shipmentItemList = delegator.findList("InvoiceShipmentItemAndAcctgTrans", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
                for(GenericValue shipmentItem: shipmentItemList){
                    BigDecimal quantity = shipmentItem.getBigDecimal("quantity");
                    BigDecimal unitPrice = shipmentItem.getBigDecimal("amount").divide(shipmentItem.getBigDecimal("quantity"));
                    Map<String, Object> productAvgCostResult = dispatcher.runSync("getProductAverageCostBaseOlbius", UtilMisc.toMap("facilityId", facilityId,
                            "productId", shipmentItem.get("productId"),
                            "ownerPartyId", currentOrgId,
                            "userLogin", userLogin));
                    List<GenericValue> listOrderShipment = FastList.newInstance();
                    try {
                        EntityCondition cond = EntityCondition.makeCondition(EntityCondition.makeCondition("shipmentId", shipmentId), EntityOperator.AND, EntityCondition.makeCondition("shipmentItemSeqId", shipmentItem.get("shipmentItemSeqId")));
                        listOrderShipment = delegator.findList("OrderShipment", cond, null, null, null, false);
                    } catch (GenericEntityException e) {
                        return ServiceUtil.returnError("OLBIUS: findList OrderShipment error!");
                    }
                    Map<String, Object> editInventoryItemMap = FastMap.newInstance();
                    if (!listOrderShipment.isEmpty()){
                        editInventoryItemMap.put("orderId", listOrderShipment.get(0).getString("orderId"));
                        editInventoryItemMap.put("orderItemSeqId", listOrderShipment.get(0).get("orderItemSeqId"));
                    }

                    editInventoryItemMap.put("userLogin", userLogin);
                    editInventoryItemMap.put("productId", shipmentItem.get("productId"));
                    editInventoryItemMap.put("facilityId", facilityId);
                    editInventoryItemMap.put("unitCost", unitPrice);
                    editInventoryItemMap.put("purCost", BigDecimal.ZERO);
                    editInventoryItemMap.put("ownerPartyId", currentOrgId);
                    editInventoryItemMap.put("quantity", quantity);
                    editInventoryItemMap.put("oldInventoryItemId", shipmentItem.get("inventoryItemId"));
                    resultService = dispatcher.runSync("importProductToFacility", editInventoryItemMap);
                    if(!ServiceUtil.isSuccess(resultService)){
                        return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                    }
                }
                /**====== Dao lai but toan xuat kho ==========**/
                List<GenericValue> acctgTransList = delegator.findList("AcctgTrans", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
                for(GenericValue acctgTrans: acctgTransList){
                    Map<String, Object> copyAcctgTransAndEntries = FastMap.newInstance();
                    copyAcctgTransAndEntries.put("fromAcctgTransId", acctgTrans.getString("acctgTransId"));
                    copyAcctgTransAndEntries.put("revert", "Y");
                    copyAcctgTransAndEntries.put("userLogin", userLogin);
                    resultService = dispatcher.runSync("copyAcctgTransAndEntries", copyAcctgTransAndEntries);
                    if(!ServiceUtil.isSuccess(resultService)){
                        return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                    }
                    /**==== Posted cho Giao dich ===*/
                    Map<String, Object> postTransCtx = FastMap.newInstance();
                    postTransCtx.put("acctgTransId", resultService.get("acctgTransId"));
                    postTransCtx.put("userLogin", context.get("userLogin"));
                    resultService = dispatcher.runSync("postAcctgTrans", postTransCtx);
                    if(!ServiceUtil.isSuccess(resultService)){
                        return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                    }
                }
            }
            /**=========== huy shipment ==============**/
            delegator.storeByCondition("Shipment", UtilMisc.toMap("statusId", "SHIPMENT_CANCELLED"), shipmentConds);

            /**=========== Reserve Inventory cho order ==============**/
            for(String deliveryId: deliveryIds){
                List<GenericValue> deliveryItemList = delegator.findList("DeliveryItem", EntityCondition.makeCondition("deliveryId", deliveryId), null, null, null, false);
                for (GenericValue deliveryItem: deliveryItemList) {
                    Map<String, Object> reserveInventoryForOrderItems = FastMap.newInstance();
                    reserveInventoryForOrderItems.put("orderId", deliveryItem.getString("fromOrderId"));
                    reserveInventoryForOrderItems.put("orderItemSeqId", deliveryItem.getString("fromOrderItemSeqId"));
                    reserveInventoryForOrderItems.put("quantity", deliveryItem.getBigDecimal("actualExportedQuantity"));
                    GenericValue userLoginSystem = delegator.findOne("UserLogin",
                            UtilMisc.toMap("userLoginId", "system"), false);
                    reserveInventoryForOrderItems.put("userLogin", userLoginSystem);
                    resultService = dispatcher.runSync("reserveInventoryForOrderItem", reserveInventoryForOrderItems);
                    if(!ServiceUtil.isSuccess(resultService)){
                        return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                    }
                }
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> updateOrderAndItemByInvoice(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        String invoiceId = (String)context.get("invoiceId");
        try {
            List<GenericValue> orderItemBillingList = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            List<String> orderIdList = EntityUtil.getFieldListFromEntityList(orderItemBillingList, "orderId", true);
            EntityCondition orderConds = EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIdList);
            delegator.storeByCondition("OrderHeader", UtilMisc.toMap("statusId", "ORDER_APPROVED"), orderConds);
            for(GenericValue orderItemBilling: orderItemBillingList){
                GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderItemBilling.get("orderId"), "orderItemSeqId", orderItemBilling.get("orderItemSeqId")), false);
                orderItem.set("statusId", "ITEM_APPROVED");
                orderItem.store();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        return ServiceUtil.returnSuccess();
    }
}