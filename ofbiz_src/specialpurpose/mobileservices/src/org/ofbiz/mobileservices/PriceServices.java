package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.uom.UomWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

public class PriceServices {
    public static final String module = PriceServices.class.getName();
    public static final String resource = "MobilemcsUiLabels";
    public static final String resourceError = "MobilemcsErrorUiLabels";
    public static final String remote_server = "RemoteServer.properties";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> mGetListPriceChange(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Integer page = 0;

        if (UtilValidate.isNotEmpty(context.get("viewIndex"))) {
            page = Integer.parseInt((String) context.get("viewIndex"));
        }
        Integer size = 10;
        if (UtilValidate.isNotEmpty(context.get("viewSize"))) {
            size = Integer.parseInt((String) context.get("viewSize"));
        }
        LocalDispatcher dispatcher = ctx.getDispatcher();

        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<EntityCondition> listAllConditions = FastList.newInstance();
        List<String> listSortFields = FastList.newInstance();
        List<GenericValue> listProduct = FastList.newInstance();
        List<Map<String, Object>> listProPriceChanges = FastList.newInstance();
        EntityFindOptions opts = new EntityFindOptions();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        int totalRows = 0;
        String productStoreId = (String) context.get("productStoreId");
        Map<String,String[]> parameters = FastMap.newInstance();
        parameters.put("pagenum",new String[]{String.valueOf(page)});
        parameters.put("pagesize",new String[]{String.valueOf(size)});

        EntityListIterator listIterator = null;

        try {
            /*String salesmanId = userLogin.getString("partyId");
            GenericValue productStoreDis = null;
            GenericValue partySalesman = null;
            String distributorId = null;
            partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId",salesmanId ), false);
            if(UtilValidate.isNotEmpty(partySalesman)){
                distributorId = partySalesman.getString("distributorId");
            }
            productStoreDis = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", distributorId), false);
            if(UtilValidate.isNotEmpty(productStoreDis)){
                productStoreId = productStoreDis.getString("productStoreId");
            }*/
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

            Timestamp fromDate = null;
            Timestamp thruDate = null;

            Timestamp fromDateParam = null;
            Timestamp thruDateParam = null;
            String fromDateStr = (String) context.get("fromDateStr");
            if (UtilValidate.isNotEmpty(fromDateStr)) {
                Long fromDateL = Long.parseLong(fromDateStr);
                fromDateParam = new Timestamp(fromDateL);
            }
            String thruDateStr = (String) context.get("thruDateStr");
            if (UtilValidate.isNotEmpty(thruDateStr)) {
                Long thruDateL = Long.parseLong(thruDateStr);
                thruDateParam = new Timestamp(thruDateL);
            }

            // fromDate: set to time 00:00:00.000
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fromDateParam);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            fromDate = new Timestamp(calendar.getTimeInMillis());

            // thruDate: set to time 23:59:59.999
            if (UtilValidate.isNotEmpty(thruDateParam)) {
                calendar = Calendar.getInstance();
                calendar.setTime(thruDateParam);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                thruDate = new Timestamp(calendar.getTimeInMillis());
            } else {
                calendar = Calendar.getInstance();
                calendar.setTime(fromDateParam);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                thruDate = new Timestamp(calendar.getTimeInMillis());
            }

            listAllConditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.BETWEEN,
                    UtilMisc.toList(fromDate, thruDate)));

            if (UtilValidate.isEmpty(productStoreId)) {
                String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                GenericValue productStore = EntityUtil.getFirst(delegator.findByAnd("ProductStore",
                        UtilMisc.toMap("payToPartyId", organizationId), null, false));
                if (productStore != null)
                    productStoreId = productStore.getString("productStoreId");
            }

            Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("uomId");
            //selectFields.add("isVirtual");
			//selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
			//selectFields.add("taxAuthGeoId");
            selectFields.add("barcode");
            selectFields.add("currencyUomId");
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("productId");
            }
            opts.setDistinct(true);
            opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
            try {
                listIterator = EntityMiscUtil.processIterator(parameters,successResult,delegator,"ProductSalesPriceLogAndUoms",EntityCondition.makeCondition(listAllConditions),null,UtilMisc.toSet(selectFields),listSortFields, opts);
                listProduct = listIterator.getCompleteList();
            } catch (Exception e) {
                Debug.logError(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "ProcessingError", locale));
            } finally {
                try {
                    listIterator.close();
                } catch (GenericEntityException e) {
                    Debug.logError(e.getMessage(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "ProcessingError", locale));
                }
            }
            if (listProduct != null) {
                for (GenericValue itemProd : listProduct) {
                    Map<String, Object> itemMap = itemProd.getAllFields();
                    Map<String, Object> contextMapFind = UtilMisc.<String, Object> toMap("productId",
                            itemProd.get("productId"), "productStoreId", productStoreId, "partyId", null,
                            "quantityUomId", itemProd.get("uomId"));
                    Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceCustom", contextMapFind);
                    if (ServiceUtil.isSuccess(resultValue)) {
                        BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
                        BigDecimal basePrice = (BigDecimal) resultValue.get("basePrice");
                        BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");

						//itemMap.put("price", basePrice);
						//itemMap.put("unitListPrice", listPrice);
                        itemMap.put("priceVAT", ProductWorker.calculatePriceAfterTax(basePrice, taxPercentage));
                        itemMap.put("unitListPriceVAT", ProductWorker.calculatePriceAfterTax(listPrice, taxPercentage));
                    }

                    listProPriceChanges.add(itemMap);
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListPriceChange service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listProPriceChanges", listProPriceChanges);
        successResult.put("productStoreId", productStoreId);
        successResult.put("totalRows", Integer.parseInt((String)successResult.get("TotalRows")));
        successResult.remove("TotalRows");
        return successResult;
    }
}
