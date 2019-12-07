package com.olbius.acc.invoice;

import com.olbius.acc.utils.EntityUtils;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.ExcelUtil;
import com.olbius.acc.utils.UtilServices;
import com.olbius.acc.utils.accounts.AccountUtils;
import com.olbius.accounting.invoice.InvoiceWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;
import com.olbius.services.JqxWidgetSevices;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class InvoiceEvents implements InvoiceType {

    public final static String module = InvoiceEvents.class.getName();
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

    @SuppressWarnings("unchecked")
    public static String createInvoice(HttpServletRequest request, HttpServletResponse response) {
        //Get delegator
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        //Get Dispatcher
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);

        //Get userLogin
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";

        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        Map<String, Object> createInvoiceRs = FastMap.newInstance();
        try {
            beganTransaction = TransactionUtil.begin();
            //Get parameters
            String invoiceTypeId = (String) parameters.get("invoiceTypeId");
            String description = (String) parameters.get("description");
            String organizationId = (String) parameters.get("organizationId");
            String customerId = (String) parameters.get("customerId");
            String glAccountTypeId = (String) parameters.get("glAccountTypeId");
            String currencyUomId = (String) parameters.get("currencyUomId");
            String conversionFactorStr = (String) parameters.get("conversionFactor");
            Timestamp dueDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String) parameters.get("dueDate"));
            Timestamp invoiceDate = (Timestamp) JqxWidgetSevices.convert("java.sql.Timestamp", (String) parameters.get("invoiceDate"));
            List<Map<String, String>> listItems = (List<Map<String, String>>) JqxWidgetSevices.convert("java.util.List", (String) parameters.get("listItems"));
            //Create Invoice
            String invoiceTaxInfoStr = (String) parameters.get("invoiceTaxInfo");
            Map<String, Object> createInvoiceCtx = FastMap.newInstance();
            createInvoiceCtx.put("invoiceTypeId", invoiceTypeId);
            createInvoiceCtx.put("partyIdFrom", organizationId);
            createInvoiceCtx.put("partyId", customerId);
            createInvoiceCtx.put("userLogin", userLogin);
            createInvoiceCtx.put("description", description);
            createInvoiceCtx.put("dueDate", dueDate);
            createInvoiceCtx.put("invoiceDate", invoiceDate);
            createInvoiceCtx.put("currencyUomId", currencyUomId);
            createInvoiceCtx.put("glAccountTypeId", glAccountTypeId);
            if(UtilValidate.isNotEmpty(conversionFactorStr))
                createInvoiceCtx.put("conversionFactor", new BigDecimal(conversionFactorStr));
            if (invoiceTaxInfoStr != null) {
                JSONObject invoiceTaxInfoJson = JSONObject.fromObject(invoiceTaxInfoStr);
                createInvoiceCtx.put("partyName", invoiceTaxInfoJson.get("partyName"));
                createInvoiceCtx.put("taxCode", invoiceTaxInfoJson.get("taxCode"));
                createInvoiceCtx.put("address", invoiceTaxInfoJson.get("address"));
                createInvoiceCtx.put("countryGeoId", invoiceTaxInfoJson.get("countryGeoId"));
                createInvoiceCtx.put("stateGeoId", invoiceTaxInfoJson.get("stateGeoId"));
                createInvoiceCtx.put("phoneNbr", invoiceTaxInfoJson.has("phoneNbr") ? invoiceTaxInfoJson.get("phoneNbr") : null);
            }
            createInvoiceRs = dispatcher.runSync("createInvoice", createInvoiceCtx);

            //Create InvoiceItem
            for (Map<String, String> item : listItems) {
                Map<String, Object> createInvoiceItemCtx = FastMap.newInstance();
                createInvoiceItemCtx.put("invoiceId", createInvoiceRs.get("invoiceId"));
                if (UtilValidate.isEmpty(item.get("invoiceItemSeqId")))
                    createInvoiceItemCtx.put("invoiceItemSeqId", createInvoiceRs.get("invoiceItemSeqId"));
                else createInvoiceItemCtx.put("invoiceItemSeqId", item.get("invoiceItemSeqId"));

                createInvoiceItemCtx.put("amount", BigDecimal.valueOf(Double.parseDouble(item.get("amount"))));
                createInvoiceItemCtx.put("invoiceItemTypeId", item.get("invoiceItemTypeId"));
                createInvoiceItemCtx.put("productId", item.get("productId"));
                createInvoiceItemCtx.put("quantity", item.get("quantity"));
                createInvoiceItemCtx.put("description", item.get("description"));
                createInvoiceItemCtx.put("overrideGlAccountId", item.get("overrideGlAccountId"));
                createInvoiceItemCtx.put("taxAuthPartyId", item.get("taxAuthPartyId"));
                createInvoiceItemCtx.put("taxAuthGeoId", item.get("taxAuthGeoId"));
                createInvoiceItemCtx.put("parentInvoiceItemSeqId", item.get("parentInvoiceItemSeqId"));
                createInvoiceItemCtx.put("taxAuthorityRateSeqId", item.get("taxAuthorityRateSeqId"));
                createInvoiceItemCtx.put("userLogin", userLogin);
                dispatcher.runSync("createInvoiceItem", createInvoiceItemCtx);
            }
        } catch (GenericTransactionException e) {
            Debug.log(e.getStackTrace().toString(), module);
            okay = false;
            mess = e.getMessage();
        } catch (Exception e) {
            Debug.log(e.getStackTrace().toString(), module);
            okay = false;
            mess = e.getMessage();
        } finally {
            if (!okay) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, mess);
                    return "error";
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            } else {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            }
        }
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        request.setAttribute("invoiceId", createInvoiceRs.get("invoiceId"));
        return "success";
    }

    public static String getTotalAmountUnPaidInTime(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        BigDecimal unpaidAmount = BigDecimal.ZERO;

        Delegator delegator = DelegatorFactory.getDelegator("default");
        List<String> INVOICE_TYPE = FastList.newInstance();
        //Get Dispatcher
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        String fromDueDateStr = (String) parameters.get("fromDueDate");
        String toDueDateStr = (String) parameters.get("toDueDate");
        String invoiceType = (String) parameters.get("invoiceType");

        UtilServices.getAllInvoiceType(delegator, INVOICE_TYPE, invoiceType);
        EntityCondition invoiceTypeCon = EntityCondition.makeCondition("invoiceTypeId", EntityJoinOperator.IN, INVOICE_TYPE);

        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

        List<EntityCondition> conds = FastList.newInstance();
        EntityCondition tempCond = EntityCondition.makeCondition(
                EntityCondition.makeCondition("dueDate", EntityOperator.BETWEEN, UtilMisc.toList(new Timestamp(Long.valueOf(fromDueDateStr)), new Timestamp(Long.valueOf(toDueDateStr)))),
                EntityOperator.OR,
                EntityCondition.makeCondition("dueDate", EntityOperator.EQUALS, null)
        );
        conds.add(tempCond);
        conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("INVOICE_PAID", "INVOICE_CANCELLED")));
        conds.add(invoiceTypeCon);
        if("PURCHASE_INVOICE".equals(invoiceType)) {
            EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, organizationPartyId);
            conds.add(organizationPartyCon);
        } else {
            EntityCondition organizationPartyCon = EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, organizationPartyId);
            conds.add(organizationPartyCon);
        }
        List<GenericValue> invoices = delegator.findList("InvoiceAndInvoiceItem", EntityCondition.makeCondition(conds), null, null, null, false);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        for(GenericValue invoice : invoices) {
            totalAmount = totalAmount.add(invoice.getBigDecimal("amount"));
            List<GenericValue> paymentAppls = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", invoice.get("invoiceId")), null, null, null, false);
            if(UtilValidate.isEmpty(paymentAppls)) continue;
            for(GenericValue paymentAppl : paymentAppls) {
                GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentAppl.get("paymentId")), false);
                if(payment.get("statusId").equals("PMNT_CONFIRMED")) paidAmount = paidAmount.add(paymentAppl.getBigDecimal("amountApplied"));
            }
        }
        unpaidAmount = totalAmount.subtract(paidAmount);
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        request.setAttribute("totalAmount", unpaidAmount);
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String updateInvoiceApplication(HttpServletRequest request, HttpServletResponse response) {
        //Get delegator
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        //Get Dispatcher
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);

        //Get userLogin
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        //Get parameters
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        try {
            beganTransaction = TransactionUtil.begin();
            //Get parameters
            List<Map<String, String>> listPayments = (List<Map<String, String>>) JqxWidgetSevices.convert("java.util.List", (String) parameters.get("listPayments"));
            String invoiceId = (String) parameters.get("invoiceId");
            //Create InvoiceItem
            for (Map<String, String> item : listPayments) {
                Map<String, Object> updatePayApplDefCtx = FastMap.newInstance();
                updatePayApplDefCtx.put("invoiceId", invoiceId);
                updatePayApplDefCtx.put("paymentId", item.get("paymentId"));
                updatePayApplDefCtx.put("amountApplied", BigDecimal.valueOf(Double.parseDouble(item.get("amountApplied"))));
                updatePayApplDefCtx.put("userLogin", userLogin);
                dispatcher.runSync("updatePaymentApplicationDef", updatePayApplDefCtx);
            }
        } catch (GenericTransactionException e) {
            Debug.log(e.getStackTrace().toString(), module);
            okay = false;
            mess = e.getMessage();
        } catch (Exception e) {
            Debug.log(e.getStackTrace().toString(), module);
            okay = false;
            mess = e.getMessage();
        } finally {
            if (!okay) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, mess);
                    return "error";
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            } else {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            }
        }
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String addTax(HttpServletRequest request, HttpServletResponse response) {
        //Get delegator
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        //Get Dispatcher
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);

        //Get userLogin
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        boolean beganTransaction = false;
        boolean okay = true;
        String mess = "";
        List<Map<String, String>> listItems = FastList.newInstance();
        try {
            //Get parameters
            Map<String, Object> parameters = UtilHttp.getParameterMap(request);
            listItems = (List<Map<String, String>>) JqxWidgetSevices.convert("java.util.List", (String) parameters.get("listItems"));
            String partyId = (String) parameters.get("partyId");
            String partyIdFrom = (String) parameters.get("partyIdFrom");
            String invoiceTypeId = (String) parameters.get("invoiceTypeId");
            String currencyUomId = "";
            String currentItemSeqId = "";

            //Declare variables
            List<GenericValue> contacts = null;
            Map<String, Object> addtaxMap = FastMap.newInstance();
            Map<String, Object> addtaxResult = FastMap.newInstance();
            BigDecimal totalAmount = new BigDecimal(0);
            BigDecimal total = new BigDecimal(0);
            List<BigDecimal> itemAmountList = FastList.newInstance();
            List<BigDecimal> itemPriceList = FastList.newInstance();
            List<BigDecimal> itemQuantityList = FastList.newInstance();
            List<BigDecimal> itemShippingList = FastList.newInstance();
            List<String> itemSeqList = FastList.newInstance();
            List<GenericValue> itemProductList = FastList.newInstance();
            BigDecimal orderShippingAmount = new BigDecimal(0);
            BigDecimal orderPromotionsAmount = new BigDecimal(0);
            String invoiceItemTypeId = null;
            contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, false);
            if (UtilValidate.isEmpty(contacts)) {
                contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "GENERAL_LOCATION"), null, false);
            }
            if (UtilValidate.isEmpty(contacts)) {
                contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION"), null, false);
            }
            if (UtilValidate.isEmpty(contacts)) {
                GenericValue partyFullName = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
                mess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCContactEmpty", UtilMisc.toMap("fullName", partyFullName.get("fullName")), UtilHttp.getLocale(request.getSession()));
                request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                request.setAttribute(ModelService.ERROR_MESSAGE, mess);
                return "error";
            }
            GenericValue contactMech = contacts.get(0);
            GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId")), false);
            if (SALES_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyId);
                invoiceItemTypeId = "ITM_SALES_TAX";
            } else if (PURC_RTN_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyId);
                invoiceItemTypeId = "SRT_SALES_TAX_ADJ";
            } else if (INTEREST_INVOICE.equals(invoiceTypeId) || PAY_SETTLEMENT_INV.equals(invoiceTypeId) || GIFTS_INVOICE.equals(invoiceTypeId) || CANCEL_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyId);
                invoiceItemTypeId = "ITM_SALES_TAX";
            } else if (PURCHASE_INVOICE.equals(invoiceTypeId) || IMPORT_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyIdFrom);
                invoiceItemTypeId = "PITM_SALES_TAX";
            } else if (CUST_RTN_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyIdFrom);
                invoiceItemTypeId = "CRT_SALES_TAX_ADJ";
            } else if (PAYROL_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyIdFrom);
                invoiceItemTypeId = "PAYROL_TAX_FEDERAL";
            } else if (COMMISSION_INVOICE.equals(invoiceTypeId) || SETTLEMENT_INVOICE.equals(invoiceTypeId)) {
                addtaxMap.put("billToPartyId", partyIdFrom);
                invoiceItemTypeId = "PITM_SALES_TAX";
            }
            addtaxMap.put("payToPartyId", partyIdFrom);
            for (Map<String, String> item : listItems) {
                if(UtilValidate.isNotEmpty(item.get("currencyUomId"))) {
                    currencyUomId = item.get("currencyUomId");
                }
                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item.get("productId")), false);
                BigDecimal amount = item.get("amount") != null ? new BigDecimal(item.get("amount")) : BigDecimal.ZERO;
                BigDecimal quantity = item.get("quantity") != null ? new BigDecimal(item.get("quantity")) : BigDecimal.ZERO;
                if (product != null) {
                    if ("ITM_PROMOTION_ADJ".equals(item.get("invoiceItemTypeId"))) {
                        total = amount.multiply(quantity);
                        totalAmount = total.subtract(amount);
                    } else {
                        totalAmount = amount.multiply(quantity);
                    }
                    itemProductList.add(product);
                    itemAmountList.add(totalAmount);
                    itemPriceList.add(amount);
                    itemQuantityList.add(quantity);
                    itemShippingList.add(new BigDecimal(0));
                    currentItemSeqId = item.get("invoiceItemSeqId");
                    itemSeqList.add(currentItemSeqId);
                }
            }
            addtaxMap.put("itemProductList", itemProductList);
            addtaxMap.put("itemAmountList", itemAmountList);
            addtaxMap.put("itemPriceList", itemPriceList);
            addtaxMap.put("itemQuantityList", itemQuantityList);
            addtaxMap.put("itemShippingList", itemShippingList);
            addtaxMap.put("orderShippingAmount", orderShippingAmount);
            addtaxMap.put("orderPromotionsAmount", orderPromotionsAmount);
            addtaxMap.put("shippingAddress", postalAddress);
            addtaxMap.put("userLogin", userLogin);
            if (!UtilValidate.isEmpty(itemProductList)) {
                addtaxResult = dispatcher.runSync("calcTax", addtaxMap);
            } else {
                throw new IllegalArgumentException("Cannot call calcTax service, when don't have productId");
            }

            List<List<GenericValue>> itemAdjustments = (List<List<GenericValue>>) addtaxResult.get("itemAdjustments");

            int index = 0;
            for (List<GenericValue> itemAdjustment : itemAdjustments) {
                for (GenericValue item : itemAdjustment) {
                    Map<String, String> itemAdj = FastMap.newInstance();
                    itemAdj.put("invoiceItemTypeId", invoiceItemTypeId);
                    itemAdj.put("productId", itemProductList.get(index).getString("productId"));
                    itemAdj.put("amount", item.getBigDecimal("amount").toString());
                    itemAdj.put("quantity", "" + 1);
                    itemAdj.put("parentInvoiceItemSeqId", itemSeqList.get(index));
                    currentItemSeqId = EntityUtils.getNextSeqId(currentItemSeqId);
                    itemAdj.put("invoiceItemSeqId", currentItemSeqId);
                    itemAdj.put("taxAuthPartyId", item.getString("taxAuthPartyId"));
                    itemAdj.put("taxAuthGeoId", item.getString("taxAuthGeoId"));
                    itemAdj.put("taxAuthorityRateSeqId", item.getString("taxAuthorityRateSeqId"));
                    itemAdj.put("description", item.getString("comments"));
                    itemAdj.put("currencyUomId", currencyUomId);
                    String invoiceItemTypeDesc = "";
                    GenericValue invoiceItemTypeG = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
                    if (invoiceItemTypeG != null)
                        invoiceItemTypeDesc = invoiceItemTypeG.getString("description");
                    itemAdj.put("invoiceItemTypeDesc", invoiceItemTypeDesc);
                    listItems.add(itemAdj);
                }
                index++;
            }
        } catch (ParseException | GenericEntityException | GenericServiceException e) {
            Debug.log(e.getStackTrace().toString(), module);
            okay = false;
            mess = e.getMessage();
        } finally {
            if (!okay) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Failure in processing agree to preliminary", null);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, mess);
                    return "error";
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            } else {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    request.setAttribute(ModelService.ERROR_MESSAGE, gte.getMessage());
                    return "error";
                }
            }
        }
        request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        request.setAttribute("listItems", listItems);
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String createVoucherInvoice(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        try {
            Map<String, Object> paramMap = AccountUtils.getParameterMapWithFileUploaded(request);
            String issuedDateStr = (String) paramMap.get("issuedDate");
            String voucherCreatedDateStr = (String) paramMap.get("voucherCreatedDate");
            Timestamp issuedDate = null, voucherCreatedDate = null;
            if (issuedDateStr != null) {
                issuedDate = new Timestamp(Long.parseLong(issuedDateStr));
            }
            if (voucherCreatedDateStr != null) {
                voucherCreatedDate = new Timestamp(Long.parseLong(voucherCreatedDateStr));
            }
            String amountStr = (String) paramMap.get("amount");
            String taxAmountStr = (String) paramMap.get("taxAmount");
            String totalAmountVoucherStr = (String) paramMap.get("totalAmountVoucher");
            paramMap.put("amount", new BigDecimal(amountStr));
            paramMap.put("taxAmount", new BigDecimal(taxAmountStr));
            paramMap.put("totalAmountVoucher", new BigDecimal(totalAmountVoucherStr));
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createVoucherInvoice", paramMap, userLogin, timeZone, locale);
            context.put("issuedDate", issuedDate);
            context.put("voucherCreatedDate", voucherCreatedDate);
            context.put("timeZone", timeZone);
            List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>) paramMap.get("listFileUploaded");
            if (UtilValidate.isNotEmpty(listFileUploaded)) {
                Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
                String _uploadedFile_contentType = (String) fileUploadedMap.get("_uploadedFile_contentType");
                ByteBuffer uploadedFile = (ByteBuffer) fileUploadedMap.get("uploadedFile");
                if (!CommonUtil.isImageFile(_uploadedFile_contentType)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "FileIsNotImage", locale));
                    return "error";
                }
                context.put("uploadedFile", uploadedFile);
                context.put("_uploadedFile_contentType", _uploadedFile_contentType);
                context.put("_uploadedFile_fileName", fileUploadedMap.get("_uploadedFile_fileName"));
            }
            Map<String, Object> resultService = dispatcher.runSync("createVoucherInvoice", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
        } catch (FileUploadException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String updateVoucherInvoice(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        try {
            Map<String, Object> paramMap = AccountUtils.getParameterMapWithFileUploaded(request);
            String issuedDateStr = (String) paramMap.get("issuedDate");
            String voucherCreatedDateStr = (String) paramMap.get("voucherCreatedDate");
            Timestamp issuedDate = null, voucherCreatedDate = null;
            if (issuedDateStr != null) {
                issuedDate = new Timestamp(Long.parseLong(issuedDateStr));
            }
            if (voucherCreatedDateStr != null) {
                voucherCreatedDate = new Timestamp(Long.parseLong(voucherCreatedDateStr));
            }
            String amountStr = (String) paramMap.get("amount");
            String taxAmountStr = (String) paramMap.get("taxAmount");
            String totalAmountVoucherStr = (String) paramMap.get("totalAmountVoucher");
            paramMap.put("amount", new BigDecimal(amountStr));
            paramMap.put("taxAmount", new BigDecimal(taxAmountStr));
            paramMap.put("totalAmountVoucher", new BigDecimal(totalAmountVoucherStr));
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateInvoiceVoucher", paramMap, userLogin, timeZone, locale);
            context.put("issuedDate", issuedDate);
            context.put("voucherCreatedDate", voucherCreatedDate);
            context.put("timeZone", timeZone);
            List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>) paramMap.get("listFileUploaded");
            if (UtilValidate.isNotEmpty(listFileUploaded)) {
                Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
                String _uploadedFile_contentType = (String) fileUploadedMap.get("_uploadedFile_contentType");
                ByteBuffer uploadedFile = (ByteBuffer) fileUploadedMap.get("uploadedFile");
                if (!CommonUtil.isImageFile(_uploadedFile_contentType)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "FileIsNotImage", locale));
                    return "error";
                }
                context.put("uploadedFile", uploadedFile);
                context.put("_uploadedFile_contentType", _uploadedFile_contentType);
                context.put("_uploadedFile_fileName", fileUploadedMap.get("_uploadedFile_fileName"));
            }
            Map<String, Object> resultService = dispatcher.runSync("updateInvoiceVoucher", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (FileUploadException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getInvoiceVoucherAmount(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId == null) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find invoice");
            return "error";
        }
        try {
            List<GenericValue> voucherInvoiceAndTotalList = delegator.findByAnd("VoucherInvoiceAndTotal", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            if (UtilValidate.isEmpty(voucherInvoiceAndTotalList)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, "Invoice is not created voucher");
                return "error";
            }
            request.setAttribute("amount", voucherInvoiceAndTotalList.get(0).get("amount"));
            request.setAttribute("taxAmount", voucherInvoiceAndTotalList.get(0).get("taxAmount"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateInvoiceItemBaseAcc(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Security security = (Security) request.getAttribute("security");
        OlbiusSecurity olbSecurity = SecurityUtil.getOlbiusSecurity(security);
        if (!olbSecurity.olbiusHasPermission(userLogin, "UPDATE", "MODULE", "ACC_PAYABLE_INV_EDIT")) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("PartyUiLabels", "PartyNoAccess", locale));
            return "error";
        }
        String invoiceId = request.getParameter("invoiceId");
        String invoiceItemsParam = request.getParameter("invoiceItems");
        if (invoiceItemsParam == null) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find invoice item to update");
            return "error";
        }
        JSONArray invoiceItemArr = JSONArray.fromObject(invoiceItemsParam);
        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("invoiceId", invoiceId);
        context.put("locale", locale);
        try {
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            TransactionUtil.begin();
            Map<String, Object> resultService = null;
            List<String> invoiceItemSeqIds = FastList.newInstance();
            List<GenericValue> acctgTransList = delegator.findList("AcctgTrans", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
            String rootInvoiceTypeId = AccountUtils.getRootInvoiceType(delegator, invoice.getString("invoiceTypeId"));
            List<String> invoiceItemTypeIds = InvoiceWorker.getTaxableInvoiceItemTypeIds(delegator);
            for (int i = 0; i < invoiceItemArr.size(); i++) {
                JSONObject invoiceItemObj = invoiceItemArr.getJSONObject(i);
                String invoiceItemSeqId = invoiceItemObj.getString("invoiceItemSeqId");
                GenericValue invoiceItem = delegator.findOne("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId), false);
                if ("PURCHASE_INVOICE".equals(rootInvoiceTypeId)) {
                    Boolean isInvoiceHaveBilling = AccountUtils.checkInvoiceHaveBilling(delegator, invoiceId);
                    if (isInvoiceHaveBilling) {
                        String invoiceItemTypeId = invoiceItem.getString("invoiceItemTypeId");
                        if (!invoiceItemTypeIds.contains(invoiceItemTypeId)) {
                            continue;
                        }
                    }
                }
                invoiceItemSeqIds.add(invoiceItemSeqId);
                BigDecimal amount = new BigDecimal(invoiceItemObj.getString("amount"));
                BigDecimal quantity = invoiceItemObj.get("quantity") != null ? new BigDecimal(invoiceItemObj.getString("quantity")) : (invoiceItem.get("quantity") != null ? invoiceItem.getBigDecimal("quantity") : BigDecimal.ONE);
                context.put("invoiceItemSeqId", invoiceItemSeqId);
                context.put("amount", amount);
                context.put("quantity", quantity);
                resultService = dispatcher.runSync("updateInvoiceItemOlbius", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    TransactionUtil.rollback();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    return "error";
                }
                //update acctgTransEntry
                BigDecimal acctgTransAmount = quantity != null ? amount.multiply(quantity) : amount;
                for (GenericValue acctgTrans : acctgTransList) {
                    String acctgTransId = acctgTrans.getString("acctgTransId");
                    List<GenericValue> acctgTransEntryList = delegator.findByAnd("AcctgTransEntry", UtilMisc.toMap("acctgTransId", acctgTransId, "invoiceItemSeqId", invoiceItemSeqId), null, false);
                    for (GenericValue acctgTransEntry : acctgTransEntryList) {
                        resultService = dispatcher.runSync("updateAcctgTransEntry", UtilMisc.toMap("userLogin", userLogin, "acctgTransId", acctgTransId, "acctgTransEntrySeqId", acctgTransEntry.get("acctgTransEntrySeqId"), "amount", acctgTransAmount, "origAmount", acctgTransAmount));
                        if (!ServiceUtil.isSuccess(resultService)) {
                            TransactionUtil.rollback();
                            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                            request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                            return "error";
                        }
                    }
                }
            }
            TransactionUtil.commit();
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateInvoiceItem(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String quantityStr = (String) paramMap.get("quantity");
        String amountStr = (String) paramMap.get("amount");
        String invoiceId = (String) paramMap.get("invoiceId");
        try {
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateInvoiceItem", paramMap, userLogin, timeZone, locale);
            context.put("quantity", new BigDecimal(quantityStr));
            context.put("amount", new BigDecimal(amountStr));
            Map<String, Object> resultService = dispatcher.runSync("updateInvoiceItem", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            BigDecimal totalAmount = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));

            //update payment application if purchase invoice has advance payment
            List<GenericValue> paymentApplications = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
            for(GenericValue paymentAppl : paymentApplications) {
                String paymentId = paymentAppl.getString("paymentId");
                GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
                if(UtilValidate.isNotEmpty(payment.get("paymentPreferenceId")) && UtilValidate.isNotEmpty(payment.get("paymentCode"))) {
                    BigDecimal amountPayment = payment.getBigDecimal("amount");
                    if(amountPayment.compareTo(totalAmount) >= 0) {
                        paymentAppl.set("amountApplied", totalAmount);
                        payment.set("paymentApplied", totalAmount);
                    }
                    else {
                        paymentAppl.set("amountApplied", amountPayment);
                        payment.set("paymentApplied", amountPayment);
                    }
                    paymentAppl.store();
                    payment.store();
                }
            }
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        }
        return "success";
    }

    public static String createInvoiceItem(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String quantityStr = (String) paramMap.get("quantity");
        String amountStr = (String) paramMap.get("amount");
        String invoiceId = (String) paramMap.get("invoiceId");
        String productId = (String) paramMap.get("productId");
        String invoiceItemTypeId = (String) paramMap.get("invoiceItemTypeId");
        try {
            List<String> invoiceItemTypeTaxList = InvoiceWorker.getTaxableInvoiceItemTypeIds(delegator);
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createInvoiceItem", paramMap, userLogin, timeZone, locale);
            BigDecimal amount = new BigDecimal(amountStr);
            context.put("quantity", new BigDecimal(quantityStr));
            context.put("amount", amount);
            if (invoiceItemTypeTaxList.contains(invoiceItemTypeId)) {
                List<GenericValue> taxCategoryList = delegator.findList("ProductTaxAndCategoryTax", EntityCondition.makeCondition("productId", productId), null, null, null, false);
                if (UtilValidate.isNotEmpty(taxCategoryList)) {
                    GenericValue taxCategory = taxCategoryList.get(0);
                    context.put("taxAuthPartyId", taxCategory.get("taxAuthPartyId"));
                    context.put("taxAuthGeoId", taxCategory.get("taxAuthGeoId"));
                    context.put("taxAuthorityRateSeqId", taxCategory.get("taxAuthorityRateSeqId"));
                }
            }
            Map<String, Object> resultService = dispatcher.runSync("createInvoiceItem", context);

            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            BigDecimal totalAmount = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));

            //update payment application if purchase invoice has advance payment
            List<GenericValue> paymentApplications = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
            for(GenericValue paymentAppl : paymentApplications) {
                String paymentId = paymentAppl.getString("paymentId");
                GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
                if(UtilValidate.isNotEmpty(payment.get("paymentPreferenceId")) && UtilValidate.isNotEmpty(payment.get("paymentCode"))) {
                    BigDecimal amountPayment = payment.getBigDecimal("amount");
                    if(amountPayment.compareTo(totalAmount) >= 0) {
                        paymentAppl.set("amountApplied", totalAmount);
                        payment.set("paymentApplied", totalAmount);
                    }
                    else {
                        paymentAppl.set("amountApplied", amountPayment);
                        payment.set("paymentApplied", amountPayment);
                    }
                    paymentAppl.store();
                    payment.store();
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String createPOInvoiceItemTypeAdj(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String amountStr = request.getParameter("amount");
        BigDecimal amount = amountStr != null ? new BigDecimal(amountStr) : BigDecimal.ZERO;
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String invoiceId = (String) paramMap.get("invoiceId");
        try {
            TransactionUtil.begin();
            try {
                GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "PINV_ADJIMPPO_ITEM"), false);
                if (invoiceItemType == null) {
                    TransactionUtil.rollback();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "CannotFindPOInvoiceItemTypeAdj", locale));
                    return "error";
                }
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createInvoiceItemOlbius", paramMap, userLogin, timeZone, locale);
                context.put("amount", amount);
                context.put("quantity", BigDecimal.ONE);
                context.put("invoiceItemTypeId", "PINV_ADJIMPPO_ITEM");
                context.put("description", invoiceItemType.get("description"));
                Map<String, Object> resultService = dispatcher.runSync("createInvoiceItemOlbius", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    TransactionUtil.rollback();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    return "error";
                }
                resultService = dispatcher.runSync("createAcctgTransForAdjPurchaseInvoice", UtilMisc.toMap("userLogin", userLogin, "timeZone", timeZone, "locale", locale, "invoiceId", invoiceId));
                if (!ServiceUtil.isSuccess(resultService)) {
                    TransactionUtil.rollback();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    return "error";
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getProductPrice(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String businessType = request.getParameter("businessType");
        String partyId = request.getParameter("partyId");
        String productId = request.getParameter("productId");
        String currencyUomId = request.getParameter("currencyUomId");
        Locale locale = UtilHttp.getLocale(request);
        BigDecimal productPrice = BigDecimal.ZERO;
        String productCurrencyUomId = null;
        if ("AP".equals(businessType)) {
            List<EntityCondition> conds = FastList.newInstance();
            conds.add(EntityCondition.makeCondition("partyId", partyId));
            conds.add(EntityCondition.makeCondition("productId", productId));
            conds.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
            try {
                List<GenericValue> supplierProductPrices = delegator.findList("SupplierProduct", EntityCondition.makeCondition(conds), null, UtilMisc.toList("-availableFromDate"), null, false);
                if (UtilValidate.isNotEmpty(supplierProductPrices)) {
                    GenericValue supplierProductPrice = supplierProductPrices.get(0);
                    productCurrencyUomId = supplierProductPrice.getString("currencyUomId");
                    productPrice = (BigDecimal) supplierProductPrice.get("lastPrice");
                } else {
                    GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                    GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyId), false);
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "CannotFindProductPriceOfSupplier", UtilMisc.toMap("productName", product.get("productName"), "fullName", party.get("fullName")), locale));
                    return "error";
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            }
        } else {
            try {
                Map<String, Object> resultService = dispatcher.runSync("calculateProductPriceCustom", UtilMisc.toMap("productId", productId));
                productPrice = (BigDecimal) resultService.get("price");
                productCurrencyUomId = (String) resultService.get("currencyUsed");
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            }
        }
        Map<String, Object> resultService;
        try {
            resultService = dispatcher.runSync("convertUom", UtilMisc.toMap("uomId", productCurrencyUomId, "uomIdTo", currencyUomId, "originalValue", productPrice, "defaultDecimalScale", Long.valueOf(2), "defaultRoundingMode", "HalfUp"));
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            productPrice = (BigDecimal) resultService.get("convertedValue");
            request.setAttribute("productPrice", productPrice);
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getInvoiceItemByOrderList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, String[]> params = request.getParameterMap();
        String strOrderDate = params.get("orderDate") != null ? params.get("orderDate")[0] : null;
        try {
            List<String> listOrderId = AccountUtils.getListSalesOrderNotVATInvoice(dispatcher, params, locale, timeZone, userLogin);
            if (UtilValidate.isEmpty(listOrderId)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, "You haven't choose any order");
                return "error";
            }

            Set<String> productStoreIds = FastSet.newInstance();
            for (String orderId : listOrderId) {
                GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
                if (UtilValidate.isNotEmpty(orderHeader)) {
                    String productStoreId = orderHeader.getString("productStoreId");
                    productStoreIds.add(productStoreId);
                }
            }
            if (UtilValidate.isNotEmpty(productStoreIds) && productStoreIds.size() > 1) {
                String errorMessage = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNumberOfStoreMustBeOne", locale);
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, errorMessage);
                return "error";
            }

            Map<String, Object> context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("timeZone", timeZone);
            context.put("locale", locale);
            context.put("listOrderId", listOrderId);
            context.put("returnDate", strOrderDate);
            Map<String, Object> resultService = dispatcher.runSync("getInvoiceItemListByOrderNotVAT", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute("listReturn", resultService.get("listReturn"));
            request.setAttribute("productStoreId", productStoreIds.iterator().next());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        }
        return "success";
    }

    public static String getOrderListSelectedInOrderNotVATInv(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, String[]> params = request.getParameterMap();
        try {
            List<String> listOrderId = AccountUtils.getListSalesOrderNotVATInvoice(dispatcher, params, locale, timeZone, userLogin);
            request.setAttribute("listReturn", listOrderId);
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String createInvoiceVAT(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String invoiceDateStr = request.getParameter("invoiceDate");
        Timestamp invoiceDate = new Timestamp(Long.parseLong(invoiceDateStr));
        String voucherForm = request.getParameter("voucherForm");
        String voucherNumber = request.getParameter("voucherNumber");
        String voucherSerial = request.getParameter("voucherSerial");
        String orderDateStr = request.getParameter("orderDate");
        String productStoreId = request.getParameter("productStoreId");
        try {
            String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Map<String, String[]> params = request.getParameterMap();
            List<String> listOrderId = AccountUtils.getListSalesOrderNotVATInvoice(dispatcher, params, locale, timeZone, userLogin);
            if (UtilValidate.isEmpty(listOrderId)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, "You haven't choose any order");
                return "error";
            }
            Map<String, Object> context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("timeZone", timeZone);
            context.put("locale", locale);
            context.put("listOrderId", listOrderId);
            context.put("returnDate", orderDateStr);
            Map<String, Object> resultService = dispatcher.runSync("getInvoiceItemListByOrderNotVAT", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            List<GenericValue> listInvoiceItem = (List<GenericValue>) resultService.get("listReturn");

            GenericValue partyAcctgPreference = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", orgId), false);
            context.clear();
            context.put("userLogin", userLogin);
            context.put("locale", locale);
            context.put("timeZone", timeZone);
            context.put("invoiceDate", invoiceDate);
            context.put("partyIdFrom", orgId);
            context.put("partyId", "_NA_");
            context.put("invoiceTypeId", "SALES_INVOICE_TOTAL");
            context.put("statusId", "INVOICE_IN_PROCESS");
            context.put("description", request.getParameter("description"));
            context.put("productStoreId", productStoreId);
            if (partyAcctgPreference != null) {
                context.put("currencyUomId", partyAcctgPreference.get("baseCurrencyUomId"));
            }
            TransactionUtil.begin();
            resultService = dispatcher.runSync("createInvoice", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            String invoiceId = (String) resultService.get("invoiceId");
            context.clear();
            context.put("userLogin", userLogin);
            context.put("locale", locale);
            context.put("timeZone", timeZone);
            context.put("invoiceId", invoiceId);
            Map<String, Object> invoiceItemMap = FastMap.newInstance();
            BigDecimal totalAmountVoucher = BigDecimal.ZERO;
            for (GenericValue invoiceItem : listInvoiceItem) {
                String taxAuthorityRateSeqId = invoiceItem.getString("taxAuthorityRateSeqId");
                String productId = invoiceItem.getString("productId");
                String productCategoryId = "";
                if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                    GenericValue taxAuthority = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", taxAuthorityRateSeqId), false);
                    if (UtilValidate.isNotEmpty(taxAuthority)) {
                        productCategoryId = taxAuthority.getString("productCategoryId");
                    }
                } else {
                    List<EntityCondition> conds = FastList.newInstance();
                    conds.add(EntityCondition.makeCondition("productId", productId));
                    conds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
                    conds.add(EntityUtil.getFilterByDateExpr());
                    List<GenericValue> prdCateMembers = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, UtilMisc.toList("sequenceNum"), null, false);
                    if (UtilValidate.isNotEmpty(prdCateMembers)) {
                        productCategoryId = prdCateMembers.get(0).getString("productCategoryId");
                    }
                }

                if (UtilValidate.isNotEmpty(productCategoryId) && !productCategoryId.equals("")) {
                    Map<String, Object> amountMap = FastMap.newInstance();
                    if (invoiceItemMap.containsKey(productCategoryId)) {
                        amountMap = (Map<String, Object>) invoiceItemMap.get(productCategoryId);
                        BigDecimal amount = (BigDecimal) amountMap.get("amount");
                        BigDecimal taxAmount = (BigDecimal) amountMap.get("taxAmount");
                        if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                            amountMap.put("taxAmount", taxAmount.add(invoiceItem.getBigDecimal("subTotalAmount")));
                        } else {
                            amountMap.put("amount", amount.add(invoiceItem.getBigDecimal("subTotalAmount")));
                        }
                    } else {
                        if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                            amountMap.put("amount", BigDecimal.ZERO);
                            amountMap.put("taxAmount", invoiceItem.getBigDecimal("subTotalAmount"));
                        } else {
                            amountMap.put("taxAmount", BigDecimal.ZERO);
                            amountMap.put("amount", invoiceItem.getBigDecimal("subTotalAmount"));
                        }
                    }
                    invoiceItemMap.put(productCategoryId, amountMap);
                    totalAmountVoucher = totalAmountVoucher.add(invoiceItem.getBigDecimal("subTotalAmount"));
                }

                context.put("productId", productId);
                context.put("quantity", invoiceItem.get("quantity") != null ? new BigDecimal(invoiceItem.getString("quantity")) : BigDecimal.ONE);
                context.put("amount", invoiceItem.get("amount"));
                context.put("description", invoiceItem.get("description"));
                context.put("taxAuthorityRateSeqId", taxAuthorityRateSeqId);
                context.put("taxAuthGeoId", invoiceItem.get("taxAuthGeoId"));
                context.put("taxAuthPartyId", invoiceItem.get("taxAuthPartyId"));
                context.put("invoiceItemTypeId", invoiceItem.get("invoiceItemTypeId"));
                resultService = dispatcher.runSync("createInvoiceItem", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
            }
            Map<String, Object> orderInvoiceNoteCtx = FastMap.newInstance();
            orderInvoiceNoteCtx.put("userLogin", userLogin);
            orderInvoiceNoteCtx.put("locale", locale);
            orderInvoiceNoteCtx.put("timeZone", timeZone);
            String orderInvoiceNoteId = delegator.getNextSeqId("OrderInvoiceNote");
            for (String orderId : listOrderId) {
                orderInvoiceNoteCtx.put("orderInvoiceNoteId", orderInvoiceNoteId);
                orderInvoiceNoteCtx.put("orderId", orderId);
                orderInvoiceNoteCtx.put("invoiceId", invoiceId);
                orderInvoiceNoteCtx.put("isCreatedInSale", Boolean.FALSE);
                orderInvoiceNoteCtx.put("isCreatedVatInv", Boolean.TRUE);
                resultService = dispatcher.runSync("createOrStoreOrderInvoiceNote", orderInvoiceNoteCtx);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
            }

            //change status invoice to INVOICE_READY
            Map<String, Object> invStatusMap = FastMap.newInstance();
            invStatusMap.put("userLogin", userLogin);
            invStatusMap.put("locale", locale);
            invStatusMap.put("timeZone", timeZone);
            invStatusMap.put("invoiceId", invoiceId);
            invStatusMap.put("statusId", "INVOICE_READY");
            resultService = dispatcher.runSync("setInvoiceStatus", invStatusMap);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                TransactionUtil.rollback();
                return "error";
            }

            //create voucher invoice
            if (UtilValidate.isNotEmpty(invoiceItemMap)) {
                for (String item : invoiceItemMap.keySet()) {
                    Map<String, Object> contextMap = FastMap.newInstance();
                    Map<String, Object> amountMap = (Map<String, Object>) invoiceItemMap.get(item);
                    BigDecimal amount = (BigDecimal) amountMap.get("amount");
                    BigDecimal taxAmount = (BigDecimal) amountMap.get("taxAmount");

                    contextMap.put("userLogin", userLogin);
                    contextMap.put("locale", locale);
                    contextMap.put("timeZone", timeZone);
                    contextMap.put("issuedDate", invoiceDate);
                    contextMap.put("voucherCreatedDate", invoiceDate);
                    contextMap.put("voucherForm", voucherForm);
                    contextMap.put("voucherSerial", voucherSerial);
                    contextMap.put("voucherNumber", voucherNumber);
                    contextMap.put("amount", amount);
                    contextMap.put("taxAmount", taxAmount);
                    contextMap.put("taxProductCategoryId", item);
                    contextMap.put("totalAmountVoucher", totalAmountVoucher);
                    contextMap.put("invoiceId", invoiceId);

                    resultService = dispatcher.runSync("createVoucherInvoice", contextMap);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                }
            }

            //verify invoice voucher
            /*invStatusMap.clear();
			invStatusMap.put("userLogin", userLogin);
			invStatusMap.put("locale", locale);
			invStatusMap.put("timeZone", timeZone);
			invStatusMap.put("invoiceId", invoiceId);
			resultService = dispatcher.runSync("verifyInvoiceVoucher", invStatusMap);
			if (!ServiceUtil.isSuccess(resultService)) {
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}*/

            TransactionUtil.commit();
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String quickCreateVoucherInvoice(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        try {
            Map<String, Object> paramMap = AccountUtils.getParameterMapWithFileUploaded(request);
            String invoiceId = (String) paramMap.get("invoiceId");
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            List<GenericValue> listVoucherInvoice = invoice.getRelated("VoucherInvoice", null, null, false);
            if (UtilValidate.isNotEmpty(listVoucherInvoice)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels.xml", "BACCCannotQuickCreateVoucherExisting", locale));
            }
            List<GenericValue> listInvoiceItem = invoice.getRelated("InvoiceItem", null, null, false);
            Map<String, Map<String, BigDecimal>> invoiceItemMap = FastMap.newInstance();
            List<GenericValue> listNoProduct = FastList.newInstance();
            BigDecimal totalAmountVoucher = BigDecimal.ZERO;
            for (GenericValue invoiceItem : listInvoiceItem) {
                String taxAuthorityRateSeqId = "";
                if (UtilValidate.isNotEmpty(invoiceItem.get("taxAuthorityRateSeqId"))) {
                    taxAuthorityRateSeqId = invoiceItem.getString("taxAuthorityRateSeqId");
                }
                if (UtilValidate.isEmpty(invoiceItem.get("productId")) && UtilValidate.isEmpty(taxAuthorityRateSeqId)) {
                    listNoProduct.add(invoiceItem);
                    continue;
                }
                String productId = invoiceItem.getString("productId");
                String productCategoryId = "";
                if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                    GenericValue taxAuthority = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", taxAuthorityRateSeqId), false);
                    if (UtilValidate.isNotEmpty(taxAuthority)) {
                        productCategoryId = taxAuthority.getString("productCategoryId");
                    }
                } else {
                    List<EntityCondition> conds = FastList.newInstance();
                    conds.add(EntityCondition.makeCondition("productId", productId));
                    conds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
                    conds.add(EntityUtil.getFilterByDateExpr());
                    List<GenericValue> prdCateMembers = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, UtilMisc.toList("sequenceNum"), null, false);
                    if (UtilValidate.isNotEmpty(prdCateMembers)) {
                        productCategoryId = prdCateMembers.get(0).getString("productCategoryId");
                    }
                }

                if (UtilValidate.isNotEmpty(productCategoryId)) {
                    Map<String, BigDecimal> amountMap = FastMap.newInstance();
                    if (invoiceItemMap.containsKey(productCategoryId)) {
                        amountMap = invoiceItemMap.get(productCategoryId);
                        BigDecimal amount = amountMap.get("amount");
                        BigDecimal taxAmount = amountMap.get("taxAmount");
                        if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                            amountMap.put("taxAmount", taxAmount.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity"))));
                        } else {
                            amountMap.put("amount", amount.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity"))));
                        }
                    } else {
                        if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                            amountMap.put("amount", BigDecimal.ZERO);
                            amountMap.put("taxAmount", invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                        } else {
                            amountMap.put("taxAmount", BigDecimal.ZERO);
                            amountMap.put("amount", invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                        }
                    } invoiceItemMap.put(productCategoryId, amountMap);
                    totalAmountVoucher = totalAmountVoucher.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                }
            }

            BigDecimal itemAmountNoProduct = BigDecimal.ZERO;
            for (GenericValue item : listNoProduct) {
                itemAmountNoProduct = item.getBigDecimal("amount").multiply(item.getBigDecimal("quantity"));
            }
            totalAmountVoucher = totalAmountVoucher.add(itemAmountNoProduct);
            String voucherForm = (String) paramMap.get("voucherForm");
            String voucherSerial = (String) paramMap.get("voucherSerial");
            String voucherNumber = (String) paramMap.get("voucherNumber");
            String description = (String) paramMap.get("description");
            Timestamp invoiceDate = invoice.getTimestamp("invoiceDate");
            Map<String, Object> contextMap = FastMap.newInstance();
            for (String productCategoryId : invoiceItemMap.keySet()) {
                Map<String, BigDecimal> voucher = invoiceItemMap.get(productCategoryId);
                BigDecimal itemAmount = voucher.get("amount");
                BigDecimal taxAmount = voucher.get("taxAmount");
                if(itemAmount.add(itemAmountNoProduct).compareTo(BigDecimal.ZERO) < 0) {
                    itemAmountNoProduct = itemAmount.add(itemAmountNoProduct);
                    itemAmount = BigDecimal.ZERO;
                } else {
                    itemAmount = itemAmount.add(itemAmountNoProduct);
                    itemAmountNoProduct = BigDecimal.ZERO;
                }
                itemAmountNoProduct = BigDecimal.ZERO;
                contextMap.put("userLogin", userLogin);
                contextMap.put("locale", locale);
                contextMap.put("timeZone", timeZone);
                contextMap.put("issuedDate", invoiceDate);
                contextMap.put("voucherCreatedDate", invoiceDate);
                contextMap.put("voucherForm", voucherForm);
                contextMap.put("voucherSerial", voucherSerial);
                contextMap.put("voucherNumber", voucherNumber);
                contextMap.put("amount", itemAmount);
                contextMap.put("taxAmount", taxAmount);
                contextMap.put("taxProductCategoryId", productCategoryId);
                contextMap.put("totalAmountVoucher", totalAmountVoucher);
                contextMap.put("description", description);
                contextMap.put("invoiceId", invoiceId);
                Map<String, Object> resultService = dispatcher.runSync("createVoucherInvoice", contextMap);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
        } catch (GenericEntityException | GenericServiceException | FileUploadException | IOException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } return "success";
    }

    public static String createListOrderInvoiceNote(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String orderIdParam = request.getParameter("orderIds");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createOrStoreOrderInvoiceNote", paramMap, userLogin, timeZone, locale);
                JSONArray orderIdArr = JSONArray.fromObject(orderIdParam);
                for (int i = 0; i < orderIdArr.size(); i++) {
                    String orderId = orderIdArr.getString(i);
                    context.put("orderId", orderId);
                    Map<String, Object> resultServices = dispatcher.runSync("createOrStoreOrderInvoiceNote", context);
                    if (!ServiceUtil.isSuccess(resultServices)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultServices));
                        TransactionUtil.rollback();
                        return "error";
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateOrderInvoiceNote(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        try {
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createOrStoreOrderInvoiceNote", paramMap, userLogin, timeZone, locale);
            try {
                Map<String, Object> resultServices = dispatcher.runSync("createOrStoreOrderInvoiceNote", context);
                if (!ServiceUtil.isSuccess(resultServices)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultServices));
                    return "error";
                }
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GenericServiceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            }
        } catch (GeneralServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }

        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String updateOrderIsCreatedVATInvoice(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String orderId = request.getParameter("orderId");
        String issuedDateStr = request.getParameter("issuedDate");
        Timestamp issuedDate = new Timestamp(Long.parseLong(issuedDateStr));
        String voucherForm = request.getParameter("voucherForm");
        String voucherNumber = request.getParameter("voucherNumber");
        String voucherSerial = request.getParameter("voucherSerial");
        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        context.put("orderId", orderId);
        context.put("isCreatedVatInv", Boolean.TRUE);
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> resultService = dispatcher.runSync("createOrStoreOrderInvoiceNote", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }

                GenericValue orderInvoiceNote = null;
                try {
                    orderInvoiceNote = delegator.findOne("OrderInvoiceNote", UtilMisc.toMap("orderId", orderId), false);
                } catch (GenericEntityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                }

                String invoiceId = null;
                if (UtilValidate.isNotEmpty(orderInvoiceNote)) {
                    GenericValue invoiceTaxInfo = delegator.makeValue("InvoiceTaxInfo");
                    invoiceTaxInfo.set("partyName", orderInvoiceNote.getString("customerName"));
                    invoiceTaxInfo.set("taxCode", orderInvoiceNote.getString("taxInfoId"));
                    invoiceTaxInfo.set("address", orderInvoiceNote.getString("address"));

                    List<GenericValue> invoice = null;
                    try {
                        invoice = delegator.findList("OrderInvoiceNoteAndDetail", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                    }

                    if (UtilValidate.isNotEmpty(invoice)) {
                        invoiceId = invoice.get(0).getString("invoiceId");
                        invoiceTaxInfo.set("invoiceId", invoiceId);
                    } else {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
                        TransactionUtil.rollback();
                    }

                    try {
                        invoiceTaxInfo.create();
                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                    }
                }

                List<GenericValue> listInvoiceItem = null;
                try {
                    listInvoiceItem = delegator.findList("InvoiceItem", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
                } catch (GenericEntityException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Map<String, Object> invoiceItemMap = FastMap.newInstance();
                BigDecimal totalAmountVoucher = BigDecimal.ZERO;
                for (GenericValue invoiceItem : listInvoiceItem) {
                    String taxAuthorityRateSeqId = invoiceItem.getString("taxAuthorityRateSeqId");
                    String productId = invoiceItem.getString("productId");
                    String productCategoryId = "";
                    if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                        GenericValue taxAuthority = null;
                        try {
                            taxAuthority = delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", taxAuthorityRateSeqId), false);
                        } catch (GenericEntityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (UtilValidate.isNotEmpty(taxAuthority)) {
                            productCategoryId = taxAuthority.getString("productCategoryId");
                        }
                    } else {
                        List<EntityCondition> conds = FastList.newInstance();
                        conds.add(EntityCondition.makeCondition("productId", productId));
                        conds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
                        conds.add(EntityUtil.getFilterByDateExpr());
                        List<GenericValue> prdCateMembers = null;
                        try {
                            prdCateMembers = delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, UtilMisc.toList("sequenceNum"), null, false);
                        } catch (GenericEntityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (UtilValidate.isNotEmpty(prdCateMembers)) {
                            productCategoryId = prdCateMembers.get(0).getString("productCategoryId");
                        }
                    }

                    if (UtilValidate.isNotEmpty(productCategoryId) && !productCategoryId.equals("")) {
                        Map<String, Object> amountMap = FastMap.newInstance();
                        if (invoiceItemMap.containsKey(productCategoryId)) {
                            amountMap = (Map<String, Object>) invoiceItemMap.get(productCategoryId);
                            BigDecimal amount = (BigDecimal) amountMap.get("amount");
                            BigDecimal taxAmount = (BigDecimal) amountMap.get("taxAmount");
                            if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                                amountMap.put("taxAmount", taxAmount.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity"))));
                            } else {
                                amountMap.put("amount", amount.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity"))));
                            }
                        } else {
                            if (UtilValidate.isNotEmpty(taxAuthorityRateSeqId)) {
                                amountMap.put("amount", BigDecimal.ZERO);
                                amountMap.put("taxAmount", invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                            } else {
                                amountMap.put("taxAmount", BigDecimal.ZERO);
                                amountMap.put("amount", invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                            }
                        }
                        invoiceItemMap.put(productCategoryId, amountMap);
                        totalAmountVoucher = totalAmountVoucher.add(invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")));
                    }
                }

                //create voucher invoice
                if (UtilValidate.isNotEmpty(invoiceItemMap)) {
                    for (String item : invoiceItemMap.keySet()) {
                        Map<String, Object> contextMap = FastMap.newInstance();
                        Map<String, Object> amountMap = (Map<String, Object>) invoiceItemMap.get(item);
                        BigDecimal amount = (BigDecimal) amountMap.get("amount");
                        BigDecimal taxAmount = (BigDecimal) amountMap.get("taxAmount");

                        contextMap.put("userLogin", userLogin);
                        contextMap.put("locale", locale);
                        contextMap.put("timeZone", timeZone);
                        contextMap.put("issuedDate", issuedDate);
                        contextMap.put("voucherCreatedDate", issuedDate);
                        contextMap.put("voucherForm", voucherForm);
                        contextMap.put("voucherSerial", voucherSerial);
                        contextMap.put("voucherNumber", voucherNumber);
                        contextMap.put("amount", amount);
                        contextMap.put("taxAmount", taxAmount);
                        contextMap.put("taxProductCategoryId", item);
                        contextMap.put("totalAmountVoucher", totalAmountVoucher);
                        contextMap.put("invoiceId", invoiceId);

                        resultService = dispatcher.runSync("createVoucherInvoice", contextMap);
                        if (!ServiceUtil.isSuccess(resultService)) {
                            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                            request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                            TransactionUtil.rollback();
                            return "error";
                        }
                    }
                }

                //verify invoice voucher
                GenericValue invoiceVerified = null;
                try {
                    invoiceVerified = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
                } catch (GenericEntityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (UtilValidate.isNotEmpty(invoiceVerified)) {
                    invoiceVerified.set("isVerified", "Y");
                    try {
                        invoiceVerified.store();
                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            }
        } catch (GenericTransactionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }

        return "success";
    }

    public static String createCashHandoverTerminal(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        String posTerminalIdParam = request.getParameter("posTerminalIds");
        JSONArray posTerminalIdJsonArr = JSONArray.fromObject(posTerminalIdParam);
        String dateReceivedStr = request.getParameter("dateReceived");
        String amountStr = request.getParameter("amount");
        List<String> posTerminalIdList = FastList.newInstance();
        for (int i = 0; i < posTerminalIdJsonArr.size(); i++) {
            String posTerminalId = posTerminalIdJsonArr.getString(i);
            posTerminalIdList.add(posTerminalId);
        }
        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        context.put("posTerminalIdList", posTerminalIdList);
        context.put("partyId", request.getParameter("partyId"));
        context.put("productStoreId", request.getParameter("productStoreId"));
        context.put("dateReceived", new Timestamp(Long.parseLong(dateReceivedStr)));
        context.put("amount", new BigDecimal(amountStr));
        try {
            Map<String, Object> resultService = dispatcher.runSync("createCashHandoverTerminal", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getInvoiceAndVoucherDiffAmount(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String invoiceId = request.getParameter("invoiceId");
        try {
            List<GenericValue> voucherTotalList = delegator.findByAnd("VoucherInvoiceAndTotal", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            if (UtilValidate.isNotEmpty(voucherTotalList)) {
                GenericValue voucherTotal = voucherTotalList.get(0);
                GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
                BigDecimal invoiceTaxTotal = InvoiceWorker.getInvoiceTaxTotal(invoice);
                BigDecimal invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice);
                BigDecimal taxAmount = voucherTotal.getBigDecimal("taxAmount");
                BigDecimal voucherAmount = voucherTotal.getBigDecimal("amount");
                request.setAttribute("taxAmount", taxAmount);
                request.setAttribute("amount", voucherAmount);
                request.setAttribute("diffInvoiceTaxAmount", invoiceTaxTotal.subtract(taxAmount));
                request.setAttribute("diffInvoiceAmount", invoiceNoTaxTotal.subtract(voucherAmount));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateInvoice(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        String invoiceDateStr = request.getParameter("invoiceDate");
        String paidDateStr = request.getParameter("paidDate");
        String dueDateStr = request.getParameter("dueDate");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        if (invoiceDateStr != null) {
            paramMap.put("invoiceDate", new Timestamp(Long.parseLong(invoiceDateStr)));
        }
        try {
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateInvoice", paramMap, userLogin, timeZone, locale);
            if (paidDateStr != null) {
                context.put("paidDate", new Timestamp(Long.parseLong(paidDateStr)));
            } else {
                context.put("paidDate", null);
            }
            if (dueDateStr != null) {
                context.put("dueDate", new Timestamp(Long.parseLong(dueDateStr)));
            } else {
                context.put("dueDate", null);
            }
            TransactionUtil.begin();
            Map<String, Object> resultService = dispatcher.runSync("updateInvoice", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            context = ServiceUtil.setServiceFields(dispatcher, "editInvoiceTaxInfo", paramMap, userLogin, timeZone, locale);
            if (paramMap.get("partyName") == null) {
                context.put("partyName", null);
            }
            if (paramMap.get("address") == null) {
                context.put("address", null);
            }
            if (paramMap.get("taxCode") == null) {
                context.put("taxCode", null);
            }
            if (paramMap.get("phoneNbr") == null) {
                context.put("phoneNbr", null);
            }
            resultService = dispatcher.runSync("editInvoiceTaxInfo", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                TransactionUtil.rollback();
                return "error";
            }
            TransactionUtil.commit();
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    @SuppressWarnings("unchecked")
    public static String exportExcelListEmployeeWorkShift(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        try {
            String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Map<String, String[]> params = request.getParameterMap();
            Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
            paramsExtend.put("pagesize", new String[]{"0"});
            paramsExtend.put("sname", new String[]{"JQGetListWorkShift"});
            paramsExtend.put("organizationPartyId", new String[]{orgId});
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("parameters", paramsExtend);
            context.put("userLogin", userLogin);
            context.put("timeZone", timeZone);
            context.put("locale", locale);
            Map<String, Object> resultService = dispatcher.runSync("jqxGridGeneralServicer", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                return "error";
            }
            List<GenericValue> listData = (List<GenericValue>) resultService.get("results");
            Workbook wb = new HSSFWorkbook();
            Map<String, CellStyle> styles = ExcelUtil.createStyles(wb);
            Sheet sheet = wb.createSheet("Sheet1");
            CellStyle csWrapText = wb.createCellStyle();
            csWrapText.setWrapText(true);
            sheet.setDisplayGridlines(true);
            sheet.setPrintGridlines(true);
            sheet.setFitToPage(true);
            sheet.setHorizontallyCenter(true);
            PrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setLandscape(true);
            sheet.setAutobreaks(true);
            printSetup.setFitHeight((short) 1);
            printSetup.setFitWidth((short) 1);

            sheet.setColumnWidth(0, 11 * 350);
            sheet.setColumnWidth(1, 15 * 350);
            sheet.setColumnWidth(2, 15 * 350);
            sheet.setColumnWidth(3, 15 * 350);
            sheet.setColumnWidth(4, 15 * 350);
            sheet.setColumnWidth(5, 15 * 350);
            sheet.setColumnWidth(6, 15 * 350);
            sheet.setColumnWidth(7, 17 * 350);
            sheet.setColumnWidth(8, 16 * 350);
            sheet.setColumnWidth(9, 15 * 350);
            sheet.setColumnWidth(10, 16 * 350);
            sheet.setColumnWidth(11, 16 * 350);
            sheet.setColumnWidth(12, 16 * 350);
            sheet.setColumnWidth(13, 16 * 350);
            sheet.setColumnWidth(14, 16 * 350);
            sheet.setColumnWidth(15, 16 * 350);

            int rownum = 1;
            //create title
            int totalColumnnOfTitle = 8;
            Row titleRow = sheet.createRow(rownum);
            titleRow.setHeight((short) 400);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTakeMoneyFromEmployee", locale));
            titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));

            rownum++;

            Row emptyRow = sheet.createRow(rownum);
            emptyRow.setHeight((short) 400);
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, totalColumnnOfTitle));
            Cell khoangCachCell2 = emptyRow.createCell(0);
            khoangCachCell2.setCellStyle(styles.get("cell_bold_centered_no_border_16"));

            rownum += 1;
            //create header
            Row headerRow = sheet.createRow(rownum);
            headerRow.setHeight((short) 500);
            String titleWorkShiftName = UtilProperties.getMessage("BasePosUiLabels", "BPOSWorkShiftId", locale);
            String titleStartTime = UtilProperties.getMessage("BasePosUiLabels", "BPOSStartTime", locale);
            String titleFinishTime = UtilProperties.getMessage("BasePosUiLabels", "BPOSFinishTime", locale);
            String titleFacility = UtilProperties.getMessage("BaseLogisticsSGCUiLabels", "SGCProductStore", locale);
            String titleTerminal = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPOSTerminal", locale);
            String titleStartTrans = UtilProperties.getMessage("BasePosUiLabels", "BPOSStartTrans", locale);
            String titleFinishTrans = UtilProperties.getMessage("BasePosUiLabels", "BPOSFinishTrans", locale);
            String titleEmployee = UtilProperties.getMessage("BasePosUiLabels", "BPOSEmployee", locale);
            String titleStartAmount = UtilProperties.getMessage("BasePosUiLabels", "BPOSStartAmount", locale);
            String titleFinishAmount = UtilProperties.getMessage("BasePosUiLabels", "BPOSFinishAmount", locale);
            String titleOtherIcone = UtilProperties.getMessage("BasePosUiLabels", "BPOSOtherIncome", locale);
            String titleOtherCost = UtilProperties.getMessage("BasePosUiLabels", "BPOSOtherCost", locale);
            String titlePayCash = UtilProperties.getMessage("BasePosUiLabels", "BPOSPayCash", locale);
            String titleCreditCard = UtilProperties.getMessage("BasePosUiLabels", "BPOSCreditCard", locale);
            String titleActualAmount = UtilProperties.getMessage("BasePosUiLabels", "BPOSAcutalAmount", locale);
            String titleDifference = UtilProperties.getMessage("BasePosUiLabels", "BPOSDifference", locale);

            String titleGrandTotalGroup = UtilProperties.getMessage("BasePosUiLabels", "BPOSGrandTotal", locale);

            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 3, 3));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 4, 4));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 5, 5));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 6, 6));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 7, 7));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 8, 8));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 9, 9));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 10, 10));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 11, 11));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 12, 12));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 15, 15));
            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, 16, 16));

            sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 13, 14));

            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue(titleWorkShiftName);
            headerCell1.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue(titleStartTime);
            headerCell2.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell3 = headerRow.createCell(2);
            headerCell3.setCellValue(titleFinishTime);
            headerCell3.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell4 = headerRow.createCell(3);
            headerCell4.setCellValue(titleFacility);
            headerCell4.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell41 = headerRow.createCell(4);
            headerCell41.setCellValue(titleTerminal);
            headerCell41.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));


            Cell headerCell5 = headerRow.createCell(5);
            headerCell5.setCellValue(titleStartTrans);
            headerCell5.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell6 = headerRow.createCell(6);
            headerCell6.setCellValue(titleFinishTrans);
            headerCell6.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell7 = headerRow.createCell(7);
            headerCell7.setCellValue(titleEmployee);
            headerCell7.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell71 = headerRow.createCell(8);
            headerCell71.setCellValue(titleEmployee);
            headerCell71.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell8 = headerRow.createCell(9);
            headerCell8.setCellValue(titleStartAmount);
            headerCell8.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell9 = headerRow.createCell(10);
            headerCell9.setCellValue(titleFinishAmount);
            headerCell9.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell10 = headerRow.createCell(11);
            headerCell10.setCellValue(titleOtherIcone);
            headerCell10.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell11 = headerRow.createCell(12);
            headerCell11.setCellValue(titleOtherCost);
            headerCell11.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCellGroup12And13 = headerRow.createCell(13);
            headerCellGroup12And13.setCellValue(titleGrandTotalGroup);
            headerCellGroup12And13.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell14 = headerRow.createCell(15);
            headerCell14.setCellValue(titleActualAmount);
            headerCell14.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell15 = headerRow.createCell(16);
            headerCell15.setCellValue(titleDifference);
            headerCell15.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            rownum++;
            Row headerRow2 = sheet.createRow(rownum);
            headerRow2.setHeight((short) 500);

            Cell headerCell12 = headerRow2.createCell(13);
            headerCell12.setCellValue(titlePayCash);
            headerCell12.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));

            Cell headerCell13 = headerRow2.createCell(14);
            headerCell13.setCellValue(titleCreditCard);
            headerCell13.setCellStyle(styles.get("cell_bold_centered_header_excel_no_border_left_11"));
            rownum++;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            for (GenericValue data : listData) {
                Row tempRow = sheet.createRow(rownum);
                tempRow.setHeight((short) 400);
                Cell tempCell1 = tempRow.createCell(0);
                tempCell1.setCellValue(data.getString("posTerminalStateId"));
                tempCell1.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell2 = tempRow.createCell(1);
                Timestamp openedDate = data.getTimestamp("openedDate");
                String openDateStr = "";
                if (openedDate != null) {
                    openDateStr = df.format(openedDate);
                }
                tempCell2.setCellValue(openDateStr);
                tempCell2.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell3 = tempRow.createCell(2);
                Timestamp closedDate = data.getTimestamp("closedDate");
                String closedDateStr = "";
                if (closedDate != null) {
                    closedDateStr = df.format(closedDate);
                }
                tempCell3.setCellValue(closedDateStr);
                tempCell3.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell4 = tempRow.createCell(3);
                tempCell4.setCellValue(data.getString("facilityName"));
                tempCell4.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell41 = tempRow.createCell(4);
                tempCell41.setCellValue(data.getString("terminalName"));
                tempCell41.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell5 = tempRow.createCell(5);
                tempCell5.setCellValue(data.getString("startingTxId"));
                tempCell5.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell6 = tempRow.createCell(6);
                tempCell6.setCellValue(data.getString("endingTxId"));
                tempCell6.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell7 = tempRow.createCell(7);
                tempCell7.setCellValue(data.getString("openedByUserLoginId"));
                tempCell7.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell71 = tempRow.createCell(8);
                tempCell71.setCellValue(data.getString("fullName"));
                tempCell71.setCellStyle(styles.get("cell_border_left_center"));

                Cell tempCell8 = tempRow.createCell(9);
                tempCell8.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal startingDrawerAmount = data.getBigDecimal("startingDrawerAmount");
                if (startingDrawerAmount != null) {
                    tempCell8.setCellValue(startingDrawerAmount.doubleValue());
                }

                Cell tempCell9 = tempRow.createCell(10);
                tempCell9.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal actualEndingCash = data.getBigDecimal("actualEndingCash");
                if (actualEndingCash != null) {
                    tempCell9.setCellValue(actualEndingCash.doubleValue());
                }

                Cell tempCell10 = tempRow.createCell(11);
                tempCell10.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal otherInCome = data.getBigDecimal("otherInCome");
                if (otherInCome != null) {
                    tempCell10.setCellValue(otherInCome.doubleValue());
                }

                Cell tempCell11 = tempRow.createCell(12);
                tempCell11.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal otherCost = data.getBigDecimal("otherCost");
                if (otherCost != null) {
                    tempCell11.setCellValue(otherCost.doubleValue());
                }

                Cell tempCell12 = tempRow.createCell(13);
                tempCell12.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal amountCash = data.getBigDecimal("amountCash");
                if (amountCash != null) {
                    tempCell12.setCellValue(amountCash.doubleValue());
                }

                Cell tempCell13 = tempRow.createCell(14);
                tempCell13.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal amountCard = data.getBigDecimal("amountCard");
                if (amountCard != null) {
                    tempCell13.setCellValue(amountCard.doubleValue());
                }

                Cell tempCell14 = tempRow.createCell(15);
                tempCell14.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal actualReceivedAmount = data.getBigDecimal("actualReceivedAmount");
                if (actualReceivedAmount != null) {
                    tempCell14.setCellValue(actualReceivedAmount.doubleValue());
                }

                Cell tempCell15 = tempRow.createCell(16);
                tempCell15.setCellStyle(styles.get("cell_normal_formatnumber_centered_border_full_10"));
                BigDecimal differenceAmount = data.getBigDecimal("differenceAmount");
                if (differenceAmount != null) {
                    tempCell15.setCellValue(differenceAmount.doubleValue());
                }
                rownum++;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                wb.write(baos);
                byte[] bytes = baos.toByteArray();
//				String fileName = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCTakeMoneyFromEmployee", locale);
                String fileName = "nhan-tien-tu-nhan-vien";
                response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
                response.setContentType("application/vnd.xls");
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return "error";
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return "error";
        }

        return "success";
    }

    public static String getInvoiceAppliedAndNotAppliedAmount(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String invoiceId = request.getParameter("invoiceId");
        try {
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            if (invoice != null) {
                BigDecimal appliedAmount = InvoiceWorker.getInvoiceApplied(invoice);
                BigDecimal notAppliedAmount = InvoiceWorker.getInvoiceNotApplied(invoice);
                request.setAttribute("appliedAmount", UtilFormatOut.formatCurrency(appliedAmount, invoice.getString("currencyUomId"), UtilHttp.getLocale(request), 2));
                request.setAttribute("notAppliedAmount", UtilFormatOut.formatCurrency(notAppliedAmount, invoice.getString("currencyUomId"), UtilHttp.getLocale(request), 2));
                request.setAttribute("notAppliedAmountNbr", notAppliedAmount);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "success";
    }

    public static String viewInvoice(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String invoiceId = request.getParameter("invoiceId");
        try {
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            if (invoice != null) {
                String invoiceTypeId = invoice.getString("invoiceTypeId");
                String parentInvoiceTypeId = AccountUtils.getRootInvoiceType(delegator, invoiceTypeId);
                if ("PURCHASE_INVOICE".equals(parentInvoiceTypeId)) {
                    return "APInvoice";
                } else if ("SALES_INVOICE".equals(parentInvoiceTypeId)) {
                    return "ARInvoice";
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    public static String getInvoiceInfoByVoucher(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String voucherIdParam = request.getParameter("voucherIds");
        JSONArray voucherIdJSONArr = JSONArray.fromObject(voucherIdParam);
        List<String> voucherIds = FastList.newInstance();
        for (int i = 0; i < voucherIdJSONArr.size(); i++) {
            voucherIds.add(voucherIdJSONArr.getString(i));
        }
        try {
            List<GenericValue> voucherInvoiceList = delegator.findList("VoucherInvoice", EntityCondition.makeCondition("voucherId", EntityJoinOperator.IN, voucherIds), null, null, null, false);
            List<String> invoiceIds = EntityUtil.getFieldListFromEntityList(voucherInvoiceList, "invoiceId", true);
            EntityCondition cond = EntityCondition.makeCondition("invoiceId", EntityJoinOperator.IN, invoiceIds);
            List<GenericValue> invoiceList = delegator.findList("InvoiceAndPaymentIdGroupBy", cond, null, UtilMisc.toList("-invoiceDate"), null, false);
            List<Map<String, Object>> retList = FastList.newInstance();
            List<Map<String, Object>> partyReceiveList = FastList.newInstance();
            List<Map<String, Object>> partyPaidList = FastList.newInstance();
            Map<String, String> partyFromMap = FastMap.newInstance();
            Map<String, String> partyMap = FastMap.newInstance();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (GenericValue invoice : invoiceList) {
                Map<String, Object> tempMap = invoice.getAllFields();
                String invoiceId = invoice.getString("invoiceId");

                List<GenericValue> voucherList = delegator.findList("VoucherInvoiceAndVoucher", EntityCondition.makeCondition("invoiceId", invoiceId), null, null, null, false);
                String voucherNumber = "";
                if (UtilValidate.isNotEmpty(voucherList)) {
                    for (GenericValue voucher : voucherList) {
                        voucherNumber += voucher.getString("voucherNumber");
                        if (voucherList.indexOf(voucher) != voucherList.size() - 1) {
                            voucherNumber += ", ";
                        }
                    }
                }
                tempMap.put("voucherNumber", voucherNumber);

                BigDecimal total = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
                tempMap.put("total", total);
                BigDecimal amountNotApplied = InvoiceWorker.getInvoiceNotApplied(delegator, invoiceId);
                BigDecimal amountApplied = total.subtract(amountNotApplied);
                tempMap.put("amountApplied", amountApplied);
                totalAmount = totalAmount.add(amountNotApplied);

                tempMap.put("invoiceDate", invoice.getTimestamp("invoiceDate").getTime());
                partyFromMap.put(invoice.getString("partyIdFrom"), invoice.get("groupNameFrom") != null ? invoice.getString("groupNameFrom") : invoice.getString("fullNameFrom"));
                partyMap.put(invoice.getString("partyId"), invoice.get("groupName") != null ? invoice.getString("groupName") : invoice.getString("fullName"));
                retList.add(tempMap);
            }
            for (Entry<String, String> entry : partyFromMap.entrySet()) {
                Map<String, Object> tempMap = FastMap.newInstance();
                tempMap.put("partyId", entry.getKey());
                tempMap.put("fullName", entry.getValue());
                partyReceiveList.add(tempMap);
            }
            for (Entry<String, String> entry : partyMap.entrySet()) {
                Map<String, Object> tempMap = FastMap.newInstance();
                tempMap.put("partyId", entry.getKey());
                tempMap.put("fullName", entry.getValue());
                partyPaidList.add(tempMap);
            }
            request.setAttribute("invoiceList", retList);
            request.setAttribute("partyReceiveList", partyReceiveList);
            request.setAttribute("partyPaidList", partyPaidList);
            request.setAttribute("totalAmount", totalAmount);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getInvoiceInfoByBank(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String orderIdParam = request.getParameter("orderIds");
        JSONArray orderIdJSONArr = JSONArray.fromObject(orderIdParam);
        List<String> orderIds = FastList.newInstance();
        for (int i = 0; i < orderIdJSONArr.size(); i++) {
            orderIds.add(orderIdJSONArr.getString(i));
        }
        try {
            EntityCondition cond = EntityCondition.makeCondition("orderId", EntityJoinOperator.IN, orderIds);
            List<GenericValue> invoiceList = delegator.findList("OrderReceiptNoteDetail", cond, null, null, null, false);
            List<Map<String, Object>> retList = FastList.newInstance();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (GenericValue invoice : invoiceList) {
                Map<String, Object> tempMap = invoice.getAllFields();
                BigDecimal total = invoice.getBigDecimal("amount");
                BigDecimal amountApplied = BigDecimal.ZERO;
                String receiptId = invoice.getString("receiptId");
                List<GenericValue> paymentReceipt = delegator.findList("PaymentReceipt", EntityCondition.makeCondition("receiptId", receiptId), null, null, null, false);
                if (UtilValidate.isNotEmpty(paymentReceipt)) {
                    for (GenericValue payment : paymentReceipt) {
                        amountApplied = amountApplied.add(payment.getBigDecimal("amountApplied"));
                    }
                }
                BigDecimal amountNotApplied = total.subtract(amountApplied);
                tempMap.put("total", total);
                tempMap.put("amountApplied", amountApplied);
                totalAmount = totalAmount.add(amountNotApplied);
                tempMap.put("orderDate", invoice.getTimestamp("orderDate").getTime());
                retList.add(tempMap);
            }
            request.setAttribute("invoiceList", retList);
            request.setAttribute("totalAmount", totalAmount);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getInvoiceDetailInfo(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String invoiceId = request.getParameter("invoiceId");
        try {
            GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            if (invoice == null) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find invoice to edit");
                return "error";
            }
            Map<String, Object> data = invoice.getFields(UtilMisc.toList("partyId", "partyIdFrom"));
            data.put("invoiceDate", invoice.getTimestamp("invoiceDate").getTime());
            data.put("conversionFactor", invoice.get("conversionFactor"));
            data.put("dueDate", invoice.get("dueDate") != null ? invoice.getTimestamp("dueDate").getTime() : null);
            data.put("paidDate", invoice.get("paidDate") != null ? invoice.getTimestamp("paidDate").getTime() : null);
            GenericValue partyFrom = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", invoice.get("partyIdFrom")), false);
            GenericValue party = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", invoice.get("partyId")), false);
            data.put("fullNameFrom", partyFrom.get("groupName") != null ? partyFrom.get("groupName") : partyFrom.get("fullName"));
            data.put("fullNameTo", party.get("groupName") != null ? party.get("groupName") : party.get("fullName"));
            GenericValue invoiceTaxInfo = delegator.findOne("InvoiceTaxInfo", UtilMisc.toMap("invoiceId", invoiceId), false);
            if (invoiceTaxInfo != null) {
                data.put("partyName", invoiceTaxInfo.get("partyName"));
                data.put("taxCode", invoiceTaxInfo.get("taxCode"));
                data.put("address", invoiceTaxInfo.get("address"));
                data.put("phoneNbr", invoiceTaxInfo.get("phoneNbr"));
            }
            request.setAttribute("data", data);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateVoucherInvoiceDiffValue(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        String systemId = EntityUtilProperties.getPropertyValue("baseaccounting.properties", "invoice.voucher.diff.value.system.id", null, delegator);
        Map<String, Object> context = FastMap.newInstance();
        String systemValue = request.getParameter("systemValue");
        context.put("systemConfigId", systemId);
        context.put("systemValue", systemValue);
        context.put("dataType", "number");
        context.put("userLogin", userLogin);
        try {
            Map<String, Object> resultService = dispatcher.runSync("createOrStoreVoucherInvoiceSystemConfig", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String isImportTrade(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, GenericServiceException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String invoiceId = request.getParameter("invoiceId");
        GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
        Map<String, Object> result = dispatcher.runSync("getPartyAccountingPreferences", UtilMisc.toMap("organizationPartyId", invoice.getString("partyId"), "userLogin", userLogin));
        GenericValue partyAccountingPreference = (GenericValue) result.get("partyAccountingPreference");
        if(UtilValidate.isEmpty(partyAccountingPreference)) {
            request.setAttribute("isImportTrade", false);
            return "success";
        }
        String baseCurrencyUomId = partyAccountingPreference.getString("baseCurrencyUomId");
        if(baseCurrencyUomId.equals(invoice.getString("currencyUomId"))) request.setAttribute("isImportTrade", false);
        else request.setAttribute("isImportTrade", true);
        request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
        return "success";
    }

    public static String getDeliveryByInvoiceId(HttpServletRequest request, HttpServletResponse response){
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String invoiceId = request.getParameter("invoiceId");
        Delegator delegator = (Delegator)request.getAttribute("delegator");
        try {
            List<GenericValue> shipmentItemBillingList = delegator.findByAnd("ShipmentItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
            if(UtilValidate.isNotEmpty(shipmentItemBillingList)){
                GenericValue shipmentItemBilling = shipmentItemBillingList.get(0);
                List<GenericValue> deliveryList = delegator.findByAnd("Delivery", UtilMisc.toMap("shipmentId", shipmentItemBilling.get("shipmentId")), null, false);
                if(UtilValidate.isNotEmpty(deliveryList)){
                    String deliveryId = deliveryList.get(0).getString("deliveryId");
                    Map<String, Object> context = FastMap.newInstance();
                    context.put("deliveryId", deliveryId);
                    context.put("userLogin", userLogin);
                    Map<String, Object> resultService = dispatcher.runSync("getDeliveryById", context);
                    if(!ServiceUtil.isSuccess(resultService)){
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        return "error";
                    }
                    request.setAttribute("deliveryId", resultService.get("deliveryId"));
                    request.setAttribute("orderId", resultService.get("orderId"));
                    if(resultService.get("statusId") != null){
                        GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", resultService.get("statusId")), false);
                        request.setAttribute("statusDesc", statusItem.get("description"));
                    }
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        }
        return "success";
    }

    public static String cancelDeliveryNoteByInvoice(HttpServletRequest request, HttpServletResponse response){
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String invoiceId = request.getParameter("invoiceId");
        Delegator delegator = (Delegator)request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        try {
            TransactionUtil.begin();
            try {
                GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
                if(invoice == null){
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, "Error: cannot find invoice with id: " + invoiceId);
                    return "error";
                }
                List<GenericValue> shipmentItemBillingList = delegator.findByAnd("ShipmentItemBilling", UtilMisc.toMap("invoiceId", invoiceId), null, false);
                List<String> shipmentIdList = EntityUtil.getFieldListFromEntityList(shipmentItemBillingList, "shipmentId", true);
                Map<String, Object> context = FastMap.newInstance();
                context.put("userLogin", userLogin);
                context.put("shipmentIds", shipmentIdList);
                Map<String, Object> resultService = dispatcher.runSync("cancelShipmentAndDeliveryByShipment", context);
                if(!ServiceUtil.isSuccess(resultService)){
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                resultService = dispatcher.runSync("setInvoiceStatus", UtilMisc.toMap("invoiceId", invoiceId, "statusId", "INVOICE_CANCELLED", "userLogin", userLogin));
                if(!ServiceUtil.isSuccess(resultService)){
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                resultService = dispatcher.runSync("updateOrderAndItemByInvoice", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
                if(!ServiceUtil.isSuccess(resultService)){
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "CancelDeliveryNoteSuccess", locale));
                TransactionUtil.commit();
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

}