package com.olbius.acc.setting;

import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 12/20/17.
 */
public class InvoiceTypeSettingServices {
    public static Map<String, Object> createInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = new FastMap<String, Object>();
        Locale locale = (Locale)context.get("locale");
        String invoiceTypeCode = (String)context.get("invoiceTypeCode");
        String description = (String)context.get("description");
        String invoiceTypeId = "INV_TYPE_" + delegator.getNextSeqId("InvoiceType");
        Long count = delegator.findCountByCondition("InvoiceType", EntityCondition.makeCondition("invoiceTypeCode", invoiceTypeCode), null, null);
        if(count > 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceTypeCodeIsExists",
                    UtilMisc.toMap("invoiceTypeCode", invoiceTypeCode), locale));
        }
        GenericValue invoiceType = delegator.makeValue("InvoiceType",
                UtilMisc.toMap("invoiceTypeId", invoiceTypeId,
                        "invoiceTypeCode", invoiceTypeCode,
                        "description", description));
        delegator.create(invoiceType);
        return result;
    }


    public static Map<String, Object> updateInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = new FastMap<String, Object>();
        String invoiceTypeId = (String)context.get("invoiceTypeId");
        String invoiceTypeCode = (String) context.get("invoiceTypeCode");
        String description = (String) context.get("description");
        GenericValue invoiceType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", invoiceTypeId), false);
        invoiceType.set("invoiceTypeCode", invoiceTypeCode);
        invoiceType.set("description", description);
        delegator.store(invoiceType);
        result.put("value", "update");
        return result;
    }

    public static Map<String, Object> deleteInvoiceType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = new FastMap<String, Object>();
        Locale locale = (Locale) context.get("locacle");
        String invoiceTypeId = (String)context.get("invoiceTypeId");
        String errorMessage = validDeleteInvoiceType(delegator, invoiceTypeId, locale);
        if(UtilValidate.isNotEmpty(errorMessage)) {
            return ServiceUtil.returnError(errorMessage);
        }
        GenericValue invoiceType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", invoiceTypeId), false);
        delegator.removeValue(invoiceType);
        return result;
    }

    private static String validDeleteInvoiceType(Delegator delegator, String invoiceTypeId, Locale locale) throws GenericEntityException {
        Long count = delegator.findCountByCondition("InvoiceItemType", EntityCondition.makeCondition("invoiceTypeId", invoiceTypeId), null, null);
        if(count > 0) {
            return UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceTypeIdIsExists", locale);
        }
        count = delegator.findCountByCondition("EnumerationInvoiceType", EntityCondition.makeCondition("invoiceTypeId", invoiceTypeId), null, null);
        if(count > 0) {
            return UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceTypeIdIsExists", locale);
        }
        return null;
    }
}
