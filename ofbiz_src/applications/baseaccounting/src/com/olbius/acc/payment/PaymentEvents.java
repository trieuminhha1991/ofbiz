package com.olbius.acc.payment;

import com.olbius.acc.invoice.entity.Invoice;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.AccountUtils;
import com.olbius.accounting.invoice.InvoiceWorker;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PaymentEvents {
	public static String getPaymentMethodOfPayment(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String paymentId = request.getParameter("paymentId");
		try {
			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
			if(payment == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "can't find payment");
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute("paymentMethodId", payment.get("paymentMethodId"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			return "error";
		}
		return "success";
	}
	
	public static String createPaymentApplicationOlbius(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String paymentId = request.getParameter("paymentId");
		String invoiceId = request.getParameter("invoiceId");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		NumberFormat currencyFormatter = NumberFormat.getNumberInstance(locale);
		try {
			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
			GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			if(payment == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find payment");
				return "error";
			}
			if(invoice == null){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, "Cannot find invoice");
				return "error";
			}
			String invoiceUomId = invoice.getString("currencyUomId");
			String paymentUomId = payment.getString("currencyUomId");
			if(!invoiceUomId.equals(paymentUomId)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "PaymentCurrencyUomNotEqualInvoiceCurrencyUom", locale));
				return "error";
			}
			String amountAppliedStr = request.getParameter("amountApplied");
			BigDecimal amountApplied = BigDecimal.ZERO;
			if(amountAppliedStr != null){
				amountApplied = new BigDecimal(amountAppliedStr);
			}
			BigDecimal paymentTotalAmount = payment.getBigDecimal("amount");
			BigDecimal paymentApplied = PaymentWorker.getPaymentApplied(payment);
			BigDecimal paymentNotApplied = paymentTotalAmount.subtract(paymentApplied);
			if(paymentNotApplied.compareTo(BigDecimal.ZERO) < 0){
				paymentNotApplied = BigDecimal.ZERO;
			}
			if(paymentNotApplied.compareTo(amountApplied) < 0){
				Map<String, Object> errorMap = UtilMisc.toMap("appliedInvoiceAmount", currencyFormatter.format(amountApplied), 
						"amountNotAppliedPayment", currencyFormatter.format(paymentNotApplied), "paymentUom", paymentUomId, "invoiceUom", invoiceUomId);
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "AmountAppliedInvGreaterThanAmountNotPmtApplied", errorMap, locale));
				return "error";
			}
			BigDecimal invoiceAmountNotApplied = InvoiceWorker.getInvoiceNotApplied(invoice);
			if (amountApplied.compareTo(invoiceAmountNotApplied) > 0) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCValueAppliedMustLessThanAmountNotApplied", locale)) ;
                return "error";
            }
			Map<String, Object> context = FastMap.newInstance();
			context.put("locale", locale);
			context.put("invoiceId", invoiceId);
			context.put("paymentId", paymentId);
			context.put("userLogin", userLogin);
			context.put("amountApplied", amountApplied);
			String serviceName = "createPaymentApplication";
			//check payment is applied for invoice
			List<GenericValue> paymentApplicationList = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("invoiceId", invoiceId, "paymentId", paymentId), null, false);
			if(UtilValidate.isNotEmpty(paymentApplicationList)){
				serviceName = "updatePaymentApplication";
			}
			Map<String, Object> resultService = dispatcher.runSync(serviceName, context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
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
	
	@SuppressWarnings("unchecked")
	public static String createPayment(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String issuedDateStr = request.getParameter("issuedDate");
		String effectiveDateStr = request.getParameter("effectiveDate");
		String issuedDateVoucherStr = request.getParameter("issuedDateVoucher");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String amountStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(amountStr);
		Timestamp effectiveDate = null;
		Timestamp issuedDateVoucher = null;
		if(issuedDateStr != null){
			paramMap.put("issuedDate", new Date(Long.parseLong(issuedDateStr)));
		}
		if(effectiveDateStr != null){
			effectiveDate =  new Timestamp(Long.parseLong(effectiveDateStr));
			paramMap.put("effectiveDate", effectiveDate);
		}
		if(issuedDateVoucherStr != null){
			issuedDateVoucher =  new Timestamp(Long.parseLong(issuedDateVoucherStr));
			paramMap.put("issuedDateVoucher", issuedDateVoucher);
		}
		try {
			String paymentTypeId = (String)paramMap.get("paymentTypeId");
			String paymentMethodId = (String)paramMap.get("paymentMethodId");
			String partyIdTo = (String)paramMap.get("partyIdTo");
			String paymentTaxInfoStr = (String)paramMap.get("paymentTaxInfo");
			String rootPaymentTypeId = AccountUtils.getRootPaymentTypeId(delegator, paymentTypeId);
			String paymentCode = AccountUtils.getPaymentCode(delegator, rootPaymentTypeId, paymentMethodId, effectiveDate);
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPayment", paramMap, userLogin, timeZone, locale);
			context.put("paymentCode", paymentCode);
			context.put("amount", amount);

			if(paymentTaxInfoStr != null){
				JSONObject paymentTaxInfoJson = JSONObject.fromObject(paymentTaxInfoStr);
				context.put("partyName", paymentTaxInfoJson.get("partyName"));
				context.put("taxCode", paymentTaxInfoJson.get("taxCode"));
				context.put("address", paymentTaxInfoJson.get("address"));
				context.put("countryGeoId", paymentTaxInfoJson.get("countryGeoId"));
				context.put("stateGeoId", paymentTaxInfoJson.get("stateGeoId"));
				context.put("phoneNbr", paymentTaxInfoJson.has("phoneNbr")? paymentTaxInfoJson.get("phoneNbr"):null);
				context.put("finAccountCode", paymentTaxInfoJson.get("finAccountCode"));
				context.put("finAccountName", paymentTaxInfoJson.get("finAccountName"));
			}
			
			if("FEE_TAX_BANK_PAYMENT".equals(paymentTypeId)){
				String taxAmountStr = (String)paramMap.get("taxAmount");
				String productId = (String)paramMap.get("productIdTaxCode");
				String partyIdFrom = (String)paramMap.get("partyIdFrom");
				
				List<GenericValue> contacts = null;
				contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, false);
				if(UtilValidate.isEmpty(contacts)) {
					contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "GENERAL_LOCATION"), null, false);
				}
				if(UtilValidate.isEmpty(contacts)) {
					contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION"), null, false);
				} 			
				if(UtilValidate.isEmpty(contacts)) {
					GenericValue partyFullName = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyIdFrom), false);
					String mess = UtilProperties.getMessage("BaseAccountingUiLabels", "PaymentTaxCannotCreateAddressEmpty", UtilMisc.toMap("fullName", partyFullName.get("fullName")), UtilHttp.getLocale(request.getSession()));
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, mess);
					return "error";
				}
				GenericValue contactMech = contacts.get(0);
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId")), false);
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				List<GenericValue> itemProductList = FastList.newInstance();
				List<BigDecimal> itemAmountList = FastList.newInstance();
				List<BigDecimal> itemPriceList = FastList.newInstance();
				List<BigDecimal> itemQuantityList = FastList.newInstance();
				List<BigDecimal> itemShippingList = FastList.newInstance();
				itemProductList.add(product);
				itemAmountList.add(amount);
				itemPriceList.add(amount);
				itemQuantityList.add(BigDecimal.ONE);
				itemShippingList.add(BigDecimal.ZERO);
				Map<String, Object> addtaxMap = FastMap.newInstance();
				addtaxMap.put("itemProductList", itemProductList);
				addtaxMap.put("itemAmountList", itemAmountList);
				addtaxMap.put("itemPriceList", itemPriceList);
				addtaxMap.put("itemQuantityList", itemQuantityList);
				addtaxMap.put("itemShippingList", itemShippingList);
				addtaxMap.put("shippingAddress", postalAddress);
				addtaxMap.put("userLogin", userLogin);
				if("DISBURSEMENT".equals(rootPaymentTypeId)){
					addtaxMap.put("billToPartyId", partyIdFrom);
				}else{
					addtaxMap.put("billToPartyId", partyIdTo);
				}
				addtaxMap.put("payToPartyId", partyIdTo);
				Map<String, Object> addtaxResult = dispatcher.runSync("calcTax", addtaxMap);
				if(!ServiceUtil.isSuccess(addtaxResult)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(addtaxResult));
					return "error";
				}
				context.put("productIdTaxCode", productId);
				List<List<GenericValue>> itemAdjustments = (List<List<GenericValue>>)addtaxResult.get("itemAdjustments");
				if(UtilValidate.isNotEmpty(itemAdjustments)){
					List<GenericValue> itemAdjustment = itemAdjustments.get(0);
					if(UtilValidate.isNotEmpty(itemAdjustment)){
						GenericValue item = itemAdjustment.get(0);
						BigDecimal taxAmount = new BigDecimal(taxAmountStr);
						context.put("taxAmount", taxAmount);
						context.put("taxAuthPartyId", item.get("taxAuthPartyId"));
						context.put("taxAuthGeoId", item.get("taxAuthGeoId"));
						context.put("taxAuthorityRateSeqId", item.get("taxAuthorityRateSeqId"));
					}
				}
			}
			Map<String, Object> resultService = dispatcher.runSync("createPayment", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
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
		}
		return "success";
	}
	
	@SuppressWarnings("unchecked")
	public static String updatePayment(HttpServletRequest request, HttpServletResponse response){
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
        String issuedDateStr = request.getParameter("issuedDate");
        String paidDateStr = request.getParameter("paidDate");
		String effectiveDateStr = request.getParameter("effectiveDate");
		String issuedDateVoucherStr = request.getParameter("issuedDateVoucher");
        String amountStr = request.getParameter("amount");
        String conversionFactorStr = request.getParameter("conversionFactor");
        BigDecimal amount = null;
        if(UtilValidate.isNotEmpty(amountStr)) {
            amount = new BigDecimal(amountStr);
        }
        BigDecimal conversionFactor = null;
        if(UtilValidate.isNotEmpty(conversionFactorStr)) {
            conversionFactor = new BigDecimal(conversionFactorStr);
        }
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String paymentTypeId = (String)paramMap.get("paymentTypeId");
		String paymentId = (String)paramMap.get("paymentId");
		Timestamp effectiveDate = null;
		Timestamp issuedDateVoucher = null;
		if(issuedDateStr != null){
			paramMap.put("issuedDate", new Date(Long.parseLong(issuedDateStr)));
		}
        if(paidDateStr != null){
            paramMap.put("paidDate", new Date(Long.parseLong(paidDateStr)));
        }
		if(effectiveDateStr != null){
			effectiveDate =  new Timestamp(Long.parseLong(effectiveDateStr));
			paramMap.put("effectiveDate", effectiveDate);
		}
		if(issuedDateVoucherStr != null){
			issuedDateVoucher =  new Timestamp(Long.parseLong(issuedDateVoucherStr));
			paramMap.put("issuedDateVoucher", issuedDateVoucher);
		}
		try {
			String rootPaymentTypeId = AccountUtils.getRootPaymentTypeId(delegator, paymentTypeId);
			GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
			if(amount == null) amount = payment.getBigDecimal("amount");
			BigDecimal oldAmount = payment.getBigDecimal("amount");
			String posTerminalStateId = payment.getString("posTerminalStateId");
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePayment", paramMap, userLogin, timeZone, locale);
            context.put("amount", amount);
            if(UtilValidate.isNotEmpty(conversionFactor))
                context.put("conversionFactor", conversionFactor);
			if(paramMap.get("organizationName") == null){
				context.put("organizationName", null);
			}
			if(paramMap.get("identifyCard") == null){
				context.put("identifyCard", null);
			}
			if(paramMap.get("issuedPlace") == null){
				context.put("issuedPlace", null);
			}
			if(paramMap.get("issuedDate") == null){
				context.put("issuedDate", null);
			}
            if(paramMap.get("paidDate") == null){
                context.put("paidDate", null);
            }
			if("FEE_TAX_BANK_PAYMENT".equals(paymentTypeId)){
				String taxAmountStr = (String)paramMap.get("taxAmount");
				String productId = (String)paramMap.get("productIdTaxCode");
				String partyIdFrom = (String)paramMap.get("partyIdFrom");
				String partyIdTo = (String)paramMap.get("partyIdTo");
				//GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paramMap.get("paymentId")), false);
				if(partyIdFrom == null){
					partyIdFrom = payment.getString("partyIdFrom");
				}
				if(partyIdTo == null){
					partyIdTo = payment.getString("partyIdTo");
				}
				List<GenericValue> contacts = null;
				contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, false);
				if(UtilValidate.isEmpty(contacts)) {
					contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "GENERAL_LOCATION"), null, false);
				}
				if(UtilValidate.isEmpty(contacts)) {
					contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION"), null, false);
				} 			
				if(UtilValidate.isEmpty(contacts)) {
					GenericValue partyFullName = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyIdFrom), false);
					String mess = UtilProperties.getMessage("BaseAccountingUiLabels", "PaymentTaxCannotCreateAddressEmpty", UtilMisc.toMap("fullName", partyFullName.get("fullName")), UtilHttp.getLocale(request.getSession()));
					request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					request.setAttribute(ModelService.ERROR_MESSAGE, mess);
					return "error";
				}
				GenericValue contactMech = contacts.get(0);
				GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId")), false);
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
				List<GenericValue> itemProductList = FastList.newInstance();
				List<BigDecimal> itemAmountList = FastList.newInstance();
				List<BigDecimal> itemPriceList = FastList.newInstance();
				List<BigDecimal> itemQuantityList = FastList.newInstance();
				List<BigDecimal> itemShippingList = FastList.newInstance();
				itemProductList.add(product);
				itemAmountList.add(amount);
				itemPriceList.add(amount);
				itemQuantityList.add(BigDecimal.ONE);
				itemShippingList.add(BigDecimal.ZERO);
				Map<String, Object> addtaxMap = FastMap.newInstance();
				addtaxMap.put("itemProductList", itemProductList);
				addtaxMap.put("itemAmountList", itemAmountList);
				addtaxMap.put("itemPriceList", itemPriceList);
				addtaxMap.put("itemQuantityList", itemQuantityList);
				addtaxMap.put("itemShippingList", itemShippingList);
				addtaxMap.put("shippingAddress", postalAddress);
				addtaxMap.put("userLogin", userLogin);
				if("DISBURSEMENT".equals(rootPaymentTypeId)){
					addtaxMap.put("billToPartyId", partyIdFrom);
				}else{
					addtaxMap.put("billToPartyId", partyIdTo);
				}
				addtaxMap.put("payToPartyId", partyIdTo);
				Map<String, Object> addtaxResult = dispatcher.runSync("calcTax", addtaxMap);
				if(!ServiceUtil.isSuccess(addtaxResult)){
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(addtaxResult));
					return "error";
				}
				context.put("productIdTaxCode", productId);
				List<List<GenericValue>> itemAdjustments = (List<List<GenericValue>>)addtaxResult.get("itemAdjustments");
				if(UtilValidate.isNotEmpty(itemAdjustments)){
					List<GenericValue> itemAdjustment = itemAdjustments.get(0);
					if(UtilValidate.isNotEmpty(itemAdjustment)){
						GenericValue item = itemAdjustment.get(0);
						BigDecimal taxAmount = new BigDecimal(taxAmountStr);
						context.put("taxAmount", taxAmount);
						context.put("taxAuthPartyId", item.get("taxAuthPartyId"));
						context.put("taxAuthGeoId", item.get("taxAuthGeoId"));
						context.put("taxAuthorityRateSeqId", item.get("taxAuthorityRateSeqId"));
					}
				}
			}else{
				context.put("taxAmount", null);
				context.put("taxAuthPartyId", null);
				context.put("taxAuthGeoId", null);
				context.put("taxAuthorityRateSeqId", null);
				context.put("productIdTaxCode", null);
			}
			TransactionUtil.begin();
			Map<String, Object> resultService = dispatcher.runSync("updatePayment", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				TransactionUtil.rollback();
				return "error";
			}
			if(posTerminalStateId != null && posTerminalStateId.length() > 0){
				BigDecimal diffAmount = amount.subtract(oldAmount);
				GenericValue posTerminalState = delegator.findOne("PosTerminalState", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
				BigDecimal actualReceivedAmount = posTerminalState.getBigDecimal("actualReceivedAmount");
				BigDecimal posTerminalStateDiffAmount = posTerminalState.getBigDecimal("differenceAmount");
				actualReceivedAmount = actualReceivedAmount.add(diffAmount);
				posTerminalStateDiffAmount = posTerminalStateDiffAmount.add(diffAmount);
				posTerminalState.set("actualReceivedAmount", actualReceivedAmount);
				posTerminalState.set("differenceAmount", posTerminalStateDiffAmount);
				posTerminalState.store();
				
				//run job update list_work_shift
				context.clear();
				context.put("userLogin", userLogin);
				dispatcher.runSync("ListWorkShiftJobSchedule", context);
			}
			TransactionUtil.commit();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
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

    @SuppressWarnings("unchecked")
    public static String createPaymentForVoucherInvoice(HttpServletRequest request, HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        String issuedDateStr = request.getParameter("issuedDate");
        String effectiveDateStr = request.getParameter("effectiveDate");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String amountStr = request.getParameter("amount");
        BigDecimal amount = new BigDecimal(amountStr);
        Timestamp effectiveDate = null;
        if(issuedDateStr != null){
            paramMap.put("issuedDate", new Date(Long.parseLong(issuedDateStr)));
        }
        if(effectiveDateStr != null){
            effectiveDate =  new Timestamp(Long.parseLong(effectiveDateStr));
            paramMap.put("effectiveDate", effectiveDate);
        }
        try {
            try {
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    String paymentTypeId = (String)paramMap.get("paymentTypeId");
                    String paymentMethodId = (String)paramMap.get("paymentMethodId");
                    String partyIdTo = (String)paramMap.get("partyIdTo");
                    String paymentTaxInfoStr = (String)paramMap.get("paymentTaxInfo");
                    String rootPaymentTypeId = AccountUtils.getRootPaymentTypeId(delegator, paymentTypeId);
                    String paymentCode = AccountUtils.getPaymentCode(delegator, rootPaymentTypeId, paymentMethodId, effectiveDate);
                    Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPayment", paramMap, userLogin, timeZone, locale);
                    context.put("paymentCode", paymentCode);
                    context.put("amount", amount);

                    if(paymentTaxInfoStr != null){
                        JSONObject paymentTaxInfoJson = JSONObject.fromObject(paymentTaxInfoStr);
                        context.put("partyName", paymentTaxInfoJson.get("partyName"));
                        context.put("taxCode", paymentTaxInfoJson.get("taxCode"));
                        context.put("address", paymentTaxInfoJson.get("address"));
                        context.put("countryGeoId", paymentTaxInfoJson.get("countryGeoId"));
                        context.put("stateGeoId", paymentTaxInfoJson.get("stateGeoId"));
                        context.put("phoneNbr", paymentTaxInfoJson.has("phoneNbr")? paymentTaxInfoJson.get("phoneNbr"):null);
                        context.put("finAccountCode", paymentTaxInfoJson.get("finAccountCode"));
                        context.put("finAccountName", paymentTaxInfoJson.get("finAccountName"));
                    }
                    if("FEE_TAX_BANK_PAYMENT".equals(paymentTypeId)){
                        String taxAmountStr = (String)paramMap.get("taxAmount");
                        String productId = (String)paramMap.get("productIdTaxCode");
                        String partyIdFrom = (String)paramMap.get("partyIdFrom");
                        List<GenericValue> contacts = null;
                        contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, false);
                        if(UtilValidate.isEmpty(contacts)) {
                            contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "GENERAL_LOCATION"), null, false);
                        }
                        if(UtilValidate.isEmpty(contacts)) {
                            contacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION"), null, false);
                        }
                        if(UtilValidate.isEmpty(contacts)) {
                            GenericValue partyFullName = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyIdFrom), false);
                            String mess = UtilProperties.getMessage("BaseAccountingUiLabels", "PaymentTaxCannotCreateAddressEmpty", UtilMisc.toMap("fullName", partyFullName.get("fullName")), UtilHttp.getLocale(request.getSession()));
                            request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                            request.setAttribute(ModelService.ERROR_MESSAGE, mess);
                            return "error";
                        }
                        GenericValue contactMech = contacts.get(0);
                        GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId")), false);
                        GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                        List<GenericValue> itemProductList = FastList.newInstance();
                        List<BigDecimal> itemAmountList = FastList.newInstance();
                        List<BigDecimal> itemPriceList = FastList.newInstance();
                        List<BigDecimal> itemQuantityList = FastList.newInstance();
                        List<BigDecimal> itemShippingList = FastList.newInstance();
                        itemProductList.add(product);
                        itemAmountList.add(amount);
                        itemPriceList.add(amount);
                        itemQuantityList.add(BigDecimal.ONE);
                        itemShippingList.add(BigDecimal.ZERO);
                        Map<String, Object> addtaxMap = FastMap.newInstance();
                        addtaxMap.put("itemProductList", itemProductList);
                        addtaxMap.put("itemAmountList", itemAmountList);
                        addtaxMap.put("itemPriceList", itemPriceList);
                        addtaxMap.put("itemQuantityList", itemQuantityList);
                        addtaxMap.put("itemShippingList", itemShippingList);
                        addtaxMap.put("shippingAddress", postalAddress);
                        addtaxMap.put("userLogin", userLogin);
                        if("DISBURSEMENT".equals(rootPaymentTypeId)){
                            addtaxMap.put("billToPartyId", partyIdFrom);
                        }else{
                            addtaxMap.put("billToPartyId", partyIdTo);
                        }
                        addtaxMap.put("payToPartyId", partyIdTo);
                        Map<String, Object> addtaxResult = dispatcher.runSync("calcTax", addtaxMap);
                        if(!ServiceUtil.isSuccess(addtaxResult)){
                            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                            request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(addtaxResult));
                            return "error";
                        }
                        context.put("productIdTaxCode", productId);
                        List<List<GenericValue>> itemAdjustments = (List<List<GenericValue>>)addtaxResult.get("itemAdjustments");
                        if(UtilValidate.isNotEmpty(itemAdjustments)){
                            List<GenericValue> itemAdjustment = itemAdjustments.get(0);
                            if(UtilValidate.isNotEmpty(itemAdjustment)){
                                GenericValue item = itemAdjustment.get(0);
                                BigDecimal taxAmount = new BigDecimal(taxAmountStr);
                                context.put("taxAmount", taxAmount);
                                context.put("taxAuthPartyId", item.get("taxAuthPartyId"));
                                context.put("taxAuthGeoId", item.get("taxAuthGeoId"));
                                context.put("taxAuthorityRateSeqId", item.get("taxAuthorityRateSeqId"));
                            }
                        }
                    }
                    TransactionUtil.begin();
                    Map<String, Object> resultService = dispatcher.runSync("createPayment", context);
                    if(!ServiceUtil.isSuccess(resultService)){
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                    String paymentId = (String)resultService.get("paymentId");
                    String invoiceIdParam = (String)paramMap.get("invoiceIds");
                    Map<String, Object> paymentAppliedMap = FastMap.newInstance();
                    paymentAppliedMap.put("userLogin", userLogin);
                    paymentAppliedMap.put("locale", locale);
                    paymentAppliedMap.put("timeZone", timeZone);
                    paymentAppliedMap.put("paymentId", paymentId);
                    JSONArray invoiceIdJsonArr = JSONArray.fromObject(invoiceIdParam);
                    for(int i = 0; i < invoiceIdJsonArr.size(); i++){
                        JSONObject item = (JSONObject) invoiceIdJsonArr.get(i);
                        String invoiceId = item.getString("invoiceId");
                        BigDecimal amountApplied = new BigDecimal(item.getString("amount"));
                        if (amountApplied.compareTo(BigDecimal.ZERO) > 0) {
                            paymentAppliedMap.put("invoiceId", invoiceId);
                            paymentAppliedMap.put("amountApplied", amountApplied);
                            resultService = dispatcher.runSync("createPaymentApplication", paymentAppliedMap);
                            if(!ServiceUtil.isSuccess(resultService)){
                                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                                TransactionUtil.rollback();
                                return "error";
                            }
                        }
                    }
                    TransactionUtil.commit();
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                    request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
                }
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
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }
	
	public static String createPaymentForBank(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String issuedDateStr = request.getParameter("issuedDate");
		String effectiveDateStr = request.getParameter("effectiveDate");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String amountStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(amountStr);
		Timestamp effectiveDate = null;
		if(issuedDateStr != null){
			paramMap.put("issuedDate", new Date(Long.parseLong(issuedDateStr)));
		}
		if(effectiveDateStr != null){
			effectiveDate =  new Timestamp(Long.parseLong(effectiveDateStr));
			paramMap.put("effectiveDate", effectiveDate);
		}
		try {
			try {
				if (amount.compareTo(BigDecimal.ZERO) > 0) {
					String paymentTypeId = (String) paramMap.get("paymentTypeId");
					String paymentMethodId = (String) paramMap.get("paymentMethodId");
					String rootPaymentTypeId = AccountUtils.getRootPaymentTypeId(delegator, paymentTypeId);
					String paymentCode = AccountUtils.getPaymentCode(delegator, rootPaymentTypeId, paymentMethodId, effectiveDate);
					Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPayment", paramMap, userLogin, timeZone, locale);
					context.put("paymentCode", paymentCode);
					context.put("amount", amount);

					TransactionUtil.begin();
					Map<String, Object> resultService = dispatcher.runSync("createPayment", context);
					if(!ServiceUtil.isSuccess(resultService)){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
						TransactionUtil.rollback();
						return "error";
					}
					String paymentId = (String) resultService.get("paymentId");
					String receiptIdParam = (String) paramMap.get("receiptIds");
					JSONArray receiptIdJsonArr = JSONArray.fromObject(receiptIdParam);
					for(int i = 0; i < receiptIdJsonArr.size(); i++){
						JSONObject item = (JSONObject) receiptIdJsonArr.get(i);
						String receiptId = item.getString("receiptId");
						BigDecimal amountApplied = new BigDecimal(item.getString("amount"));
						if (amountApplied.compareTo(BigDecimal.ZERO) > 0) {
							GenericValue paymentReceipt = delegator.makeValue("PaymentReceipt");
							paymentReceipt.set("paymentReceiptId", delegator.getNextSeqId("PaymentReceipt"));
							paymentReceipt.set("paymentId", paymentId);
							paymentReceipt.set("receiptId", receiptId);
							paymentReceipt.set("amountApplied", amountApplied);
							paymentReceipt.create();
						}
					}
					TransactionUtil.commit();
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
					request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
				}
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
	
	public static String createPaymentAndApplication(HttpServletRequest request, HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String issuedDateStr = request.getParameter("issuedDate");
		String effectiveDateStr = request.getParameter("effectiveDate");
		String amountStr = request.getParameter("amount");
		String conversionFactorStr = request.getParameter("conversionFactor");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        Timestamp effectiveDate = null;
		if(effectiveDateStr != null){
			effectiveDate =  new Timestamp(Long.parseLong(effectiveDateStr));
			paramMap.put("effectiveDate", effectiveDate);
		}
        if(conversionFactorStr != null){
            paramMap.put("conversionFactor", new BigDecimal(conversionFactorStr));
        }
		if(issuedDateStr != null){
			paramMap.put("issuedDate", new Date(Long.parseLong(issuedDateStr)));
		}
		try {
		    String invoiceId = (String) paramMap.get("invoiceId");
		    String paymentCurrencyUomId = (String) paramMap.get("currencyUomId");
		    GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
            BigDecimal notAppliedAmount = InvoiceWorker.getInvoiceNotApplied(invoice);
            if(amountStr != null){
                paramMap.put("amount", new BigDecimal(amountStr));
                if(notAppliedAmount.compareTo(new BigDecimal(amountStr)) < 0) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCValueAppliedMustLessThanAmountNotApplied", locale)) ;
                    return "error";
                }
            }
            if(!invoice.get("currencyUomId").equals(paymentCurrencyUomId)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("AccountingUiLabels", "AccountingCurrenciesOfInvoiceAndPaymentNotCompatible", locale)) ;
                return "error";
            }
			String paymentTypeId = (String)paramMap.get("paymentTypeId");
			String paymentMethodId = (String)paramMap.get("paymentMethodId");
			String rootPaymentTypeId = AccountUtils.getRootPaymentTypeId(delegator, paymentTypeId);
			String paymentCode = AccountUtils.getPaymentCode(delegator, rootPaymentTypeId, paymentMethodId, effectiveDate);
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPaymentAndApplication", paramMap, userLogin, timeZone, locale);
			context.put("paymentCode", paymentCode);
			Map<String, Object> resultService = dispatcher.runSync("createPaymentAndApplication", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}
//            if(!"VND".equals(paymentCurrencyUomId)) {
//                Map<String, Object> mapExchangedRateHistory = FastMap.newInstance();
//                mapExchangedRateHistory.put("conversionFactor", new BigDecimal(conversionFactorStr));
//                mapExchangedRateHistory.put("documentTypeId", "PAYMENT");
//                mapExchangedRateHistory.put("currencyUomId", paymentCurrencyUomId);
//                mapExchangedRateHistory.put("currencyUomIdTo", "VND"); //TODO fix me
//                mapExchangedRateHistory.put("documentId", resultService.get("paymentId"));
//                mapExchangedRateHistory.put("userLogin", userLogin);
//                dispatcher.runSync("createExchangedRateHistory", mapExchangedRateHistory);
//            }
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
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
		}
		return "success";
	}
	
	public static String setPaymentStatus(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		try {
			Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "setPaymentStatus", paramMap, userLogin, timeZone, locale);
			Map<String, Object> resultService = dispatcher.runSync("setPaymentStatus", context);
			if(!ServiceUtil.isSuccess(resultService)){
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
				request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
				return "error";
			}

			//check pos_terminal_state
			String statusId = (String) paramMap.get("statusId");
			if ("PMNT_CANCELLED".equals(statusId)) {
				String paymentId = (String) paramMap.get("paymentId");
				GenericValue payment = delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
				if (UtilValidate.isNotEmpty(payment)) {
					String posTerminalStateId = payment.getString("posTerminalStateId");
					BigDecimal amount = UtilValidate.isNotEmpty(payment.getBigDecimal("amount"))
							? payment.getBigDecimal("amount") : BigDecimal.ZERO;
					if (UtilValidate.isNotEmpty(posTerminalStateId)) {
						GenericValue posTerminalState = delegator.findOne("PosTerminalState", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
						if (UtilValidate.isNotEmpty(posTerminalState)) {
							BigDecimal differenceAmount = UtilValidate.isNotEmpty(posTerminalState.getBigDecimal("differenceAmount"))
									? posTerminalState.getBigDecimal("differenceAmount") : BigDecimal.ZERO;
							BigDecimal actualReceivedAmount = UtilValidate.isNotEmpty(posTerminalState.getBigDecimal("actualReceivedAmount"))
									? posTerminalState.getBigDecimal("actualReceivedAmount") : BigDecimal.ZERO;
							GenericValue listWorkShift = delegator.findOne("ListWorkShift", UtilMisc.toMap("posTerminalStateId", posTerminalStateId), false);
							BigDecimal amountCash = listWorkShift.getBigDecimal("amountCash");
							BigDecimal newDifferenceAmount = differenceAmount.subtract(amount);
							BigDecimal checkAmount = amountCash.add(newDifferenceAmount);
							if (checkAmount.compareTo(BigDecimal.ZERO) == 0) {
								posTerminalState.set("differenceAmount", null);
								posTerminalState.set("actualReceivedAmount", null);
							} else {
								posTerminalState.set("differenceAmount", newDifferenceAmount);
								posTerminalState.set("actualReceivedAmount", actualReceivedAmount.subtract(amount));
							}
							posTerminalState.store();
							
							context.clear();
							context.put("userLogin", userLogin);
							dispatcher.runSync("ListWorkShiftJobSchedule", context);
						}
					}
					
					//check order_receipt_note
					List<GenericValue> paymentReceipts = delegator.findList("PaymentReceipt", EntityCondition.makeCondition("paymentId", paymentId), null, null, null, false);
					if (UtilValidate.isNotEmpty(paymentReceipts)) {
						for (GenericValue item : paymentReceipts) {
							item.remove();
						}
					}
				}
			}
			
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			request.setAttribute(ModelService.SUCCESS_MESSAGE, resultService.get(ModelService.SUCCESS_MESSAGE));
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getCause().getLocalizedMessage());
		}
		return "success";
	}
	
	public static String getPaymentDetailInfo(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String paymentId = request.getParameter("paymentId");
		try {
			GenericValue payment = delegator.findOne("PaymentPartyDetail", UtilMisc.toMap("paymentId", paymentId), false);
			if(payment != null){
				Map<String, Object> data = payment.getAllFields();
				if(payment.get("productIdTaxCode") != null){
					List<GenericValue> productTaxList = delegator.findByAnd("ProductTaxAndCategoryTax", UtilMisc.toMap("productId", payment.get("productIdTaxCode")), null, false);
					if(UtilValidate.isNotEmpty(productTaxList)){
						GenericValue productTax = productTaxList.get(0);
						data.put("productCode", productTax.get("productCode"));
						data.put("productName", productTax.get("productName"));
						data.put("taxRate", productTax.get("taxPercentage"));
					}
				}
				if(payment.get("issuedDate") != null){
					data.put("issuedDate", payment.getDate("issuedDate").getTime());
				}
				data.put("effectiveDate", payment.getTimestamp("effectiveDate").getTime());
				data.put("fullNameTo", payment.get("groupNameTo") != null? payment.get("groupNameTo") : payment.get("fullNameTo"));
				data.put("fullNameFrom", payment.get("groupNameFrom") != null? payment.get("groupNameFrom") : payment.get("fullNameFrom"));
				request.setAttribute("data", data);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
