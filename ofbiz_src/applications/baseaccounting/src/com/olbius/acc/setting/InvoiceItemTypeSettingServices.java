package com.olbius.acc.setting;

import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
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
public class InvoiceItemTypeSettingServices {
    public static Map<String, Object> createInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = new FastMap<String, Object>();
        String invoiceItemTypeCode = (String)context.get("invoiceItemTypeCode");
        String invoiceTypeId = (String) context.get("invoiceTypeId");
        String description = (String)context.get("description");
        String invoiceItemTypeId = "INV_ITEM_TYPE_" + delegator.getNextSeqId("InvoiceItemType");
        Long count = delegator.findCountByCondition("InvoiceItemType", EntityCondition.makeCondition("invoiceItemTypeCode", invoiceItemTypeCode), null, null);
        if(count > 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCInvoiceItemTypeCodeIsExists",
                    UtilMisc.toMap("invoiceItemTypeCode", invoiceItemTypeCode), locale));
        }
        GenericValue invoiceType = delegator.makeValue("InvoiceItemType",
                UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId,
                        "invoiceItemTypeCode", invoiceItemTypeCode,
                        "invoiceTypeId", invoiceTypeId,
                        "description", description));
        delegator.create(invoiceType);
        result.put("value", "create");
        return result;
    }


    public static Map<String, Object> updateInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = new FastMap<String, Object>();

        String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
        String invoiceTypeId = (String)context.get("invoiceTypeId");
        String description = (String) context.get("description");
        String invoiceItemTypeCode = (String) context.get("invoiceItemTypeCode");
        GenericValue invoiceType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
        invoiceType.set("invoiceTypeId", invoiceTypeId);
        invoiceType.set("description", description);
        invoiceType.set("invoiceItemTypeCode", invoiceItemTypeCode);
        delegator.store(invoiceType);
        result.put("value", "update");
        return result;
    }

    public static Map<String, Object> deleteInvoiceItemType(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> result = new FastMap<String, Object>();
        String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
        GenericValue invoiceType = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId), false);
        delegator.removeValue(invoiceType);
        result.put("value", "delete");
        return result;
    }
}
