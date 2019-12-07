package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastSet;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import static com.olbius.basesales.product.ProductWorker.calculatePriceAfterTax;
import static org.ofbiz.mobileservices.ProductServices.getProductImage;

public class MobileSalesnetProduct {
    public static final String module = MobileSalesnetProduct.class.getName();
    public static final String resource = "MobileServicesUiLabels";
    public static final String resourceError = "MobileServicesErrorUiLabels";
        public static Map<String, Object> mGetProductByInfoSearch(DispatchContext dpc, Map<String, Object> context) {
            Map<String, Object> res = ServiceUtil.returnSuccess();
            Locale locale = (Locale) context.get("locale");
            LocalDispatcher dispatcher = dpc.getDispatcher();
            String pagesize = (String) context.get("pagesize");
            String[] ps = { pagesize };
            String pagenum = (String) context.get("pagenum");
            String[] pn = { pagenum };
            String partyId = (String) context.get("partyId");
            String[] pr = { partyId };
            String searchString = (String) context.get("searchString");
            String [] sS = { searchString };
            String service = "JQFindProductPriceQuotes";
            String[] sname = { service };
            // String method = "MOBILE_SALES_CHANNEL";
            // String[] mt = {method};
            String productStoreId = (String) context.get("productStoreId");
            String[] pst = { productStoreId };
            Map<String, String[]> params = FastMap.newInstance();
            params.put("pagesize", ps);
            params.put("pagenum", pn);
            params.put("sname", sname);
            params.put("partyId", pr);
            params.put("searchString", sS);
            // params.put("salesMethodChannelEnumId", mt);
            params.put("productStoreId", pst);
            List<EntityCondition> listAllConditions = FastList.newInstance();
            List<String> listSortFields = FastList.newInstance();
            EntityFindOptions opts = new EntityFindOptions();
            Map<String, Object> in = FastMap.newInstance();
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            in.put("userLogin", userLogin);
            in.put("parameters", params);
            in.put("locale", locale);
            in.put("listAllConditions", listAllConditions);
            in.put("listSortFields", listSortFields);
            in.put("opts", opts);
            Delegator delegator = dpc.getDelegator();
            try {
                List<Map<String, Object>> listProductsResult = FastList.newInstance();
                /*get inventory*/
                List<EntityCondition> conds = FastList.newInstance();
                conds.add(EntityCondition.makeCondition("productStoreId",productStoreId));
                conds.add(EntityUtil.getFilterByDateExpr());
                List<GenericValue> productStoreFacility = delegator.findList("ProductStoreFacility",EntityCondition.makeCondition(conds),
                        UtilMisc.toSet("facilityId"),null,null,false);
                if (UtilValidate.isEmpty(productStoreFacility)) {
                    return ServiceUtil.returnError("Facility not found");
                }
                String facilityId =  productStoreFacility.get(0).getString("facilityId");
                List<GenericValue> productFacilities = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId",facilityId),UtilMisc.toSet("productId","lastInventoryCount"),null,null,false);
                Map<String,BigDecimal> productFacilitiesMap = FastMap.newInstance();
                for (GenericValue gv : productFacilities) {
                    productFacilitiesMap.put(gv.getString("productId"),gv.getBigDecimal("lastInventoryCount"));
                }
                /*end get inventory*/
                List<GenericValue> uoms = delegator.findList("Uom",EntityCondition.makeCondition("uomTypeId","PRODUCT_PACKING"),UtilMisc.toSet("uomId","description"),null,null,false);
                Map<String,String> uomMap = FastMap.newInstance();
                for (GenericValue uom: uoms) {
                    uomMap.put((String)uom.get("uomId"), (String)uom.get("description"));
                }
                Map<String, Object> out = dispatcher.runSync("JQFindProductPriceQuotesByInfoSearch", in);
                List<Map<String, Object>> listProducts = (List<Map<String, Object>>) out.get("listIterator");
                if(UtilValidate.isEmpty(listProducts)){
                    String errMsg = UtilProperties.getMessage(resourceError, "ProductNotFound", locale);
                    Debug.log(errMsg, module);
                    return ServiceUtil.returnSuccess(errMsg);

                }
                for (Map<String, Object> product : listProducts) {
                    /*check inventory*/
                    String productId = (String) product.get("productId");
                    if (!productFacilitiesMap.containsKey(productId)) continue;
                    if (productFacilitiesMap.get(productId).compareTo(BigDecimal.ZERO) < 0) continue;
                    /*end check inventory*/

                    in = FastMap.newInstance();
                    in.put("userLogin", userLogin);
                    in.put("productId", product.get("productId"));
                    in.put("quantity", BigDecimal.ZERO);
                    in.put("productStoreId", productStoreId);
                    in.put("viewATPForAll", "Y");
                    String uomId = (String) product.get("uomId");
                    product.put("uomDescription", (String)uomMap.get(uomId));
                    Boolean isOK = false;
                    BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
                    Map<String, Object> image = getProductImage(delegator, (String) product.get("productId"));
                    product.putAll(image);
                    if (uomId != null && !"".equals(uomId)) {
                        in.put("uomId", uomId);
                        Map<String, Object> mapUom = dispatcher.runSync("checkInventoryAvailableWithUom", in);
                        isOK = (Boolean) mapUom.get("isOK");
                        availableToPromiseTotal = (BigDecimal) mapUom.get("availableToPromiseTotal");
                    } else {
                        Map<String, Object> mapNonUom = dispatcher.runSync("checkInventoryAvailable", in);
                        isOK = (Boolean) mapNonUom.get("isOK");
                        availableToPromiseTotal = (BigDecimal) mapNonUom.get("availableToPromiseTotal");
                    }
                    product.put("available", isOK);
                    product.put("availableToPromiseTotal", availableToPromiseTotal);
                    listProductsResult.add(product);
                }
                res.put("listProducts", listProductsResult);
            } catch (GenericServiceException e) {
                Debug.log(e.getMessage());
                return ServiceUtil.returnError(e.getMessage());
            } catch (GenericEntityException e) {
                Debug.log(e.getMessage());
                return ServiceUtil.returnError(e.getMessage());
            }

            return res;
    }

    /** Function "tim bao gia san pham voi infoSearch"
     * Modified from jqFindProductPriceQuotes
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> jqFindProductPriceQuotesByInfoSearch(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();

        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        //EntityFindOptions opts =(EntityFindOptions) context.get("opts");
        Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
        List<Map<String, Object>> listIterator = null;
        try {
            List<String> prodCatalogIds = FastList.newInstance();

            //String roleTypeIds = SalesUtil.getParameter(parameters, "roleTypeIds");
            //String salesMethodChannelEnumId = SalesUtil.getParameter(parameters, "salesMethodChannelEnumId");

            String partyId = SalesUtil.getParameter(parameters, "partyId");
            String searchString = SalesUtil.getParameter(parameters, "searchString");
            boolean hasVirtualProd = "Y".equals(SalesUtil.getParameter(parameters, "hasVirtualProd")) ? true : false;

            String productStoreId = SalesUtil.getParameter(parameters, "productStoreId");
            if (UtilValidate.isNotEmpty(productStoreId)) {
                List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", productStoreId), UtilMisc.toSet("prodCatalogId"), null, null, false), "prodCatalogId", true);
                if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
                    prodCatalogIds.addAll(productCatalogIdsTmp);
                }
            }

            if (UtilValidate.isNotEmpty(prodCatalogIds)) {
                Map<String, Object> productResult = findProductPriceQuotationByInfoSearch(delegator, dispatcher, locale, listSortFields, listAllConditions, parameters, prodCatalogIds,
                        true, null, null, null, productStoreId, partyId, searchString, hasVirtualProd);
                if (productResult != null) {
                    successResult.put("TotalRows", productResult.get("TotalRows"));
                    listIterator = (List<Map<String, Object>>) productResult.get("listIterator");
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling jqFindProductPriceQuotesByInfoSearch service: " + e.toString();
            Debug.logError(e, errMsg, module);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static Map<String, Object> findProductPriceQuotationByInfoSearch(Delegator delegator, LocalDispatcher dispatcher, Locale locale,
                                                                            List<String> listSortFields, List<EntityCondition> listAllConditions, Map<String,String[]> parameters,
                                                                            List<String> prodCatalogId, boolean activeIsNowTimestamp, String customTimePeriodId, Timestamp introductionDateLimit,
                                                                            Timestamp releaseDateLimit, String productStoreId, String partyId, String searchString, Boolean hasVirtualProd) throws GenericEntityException, GenericServiceException{
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();

        List<String> prodCatalogIds = FastList.newInstance();
        if (UtilValidate.isNotEmpty(prodCatalogId)) {
            prodCatalogIds.addAll(prodCatalogId);
        }

        if (UtilValidate.isNotEmpty(prodCatalogIds)) {
            List<EntityCondition> mainCondList = FastList.newInstance();
            mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

            if (UtilValidate.isNotEmpty(searchString)) {
                mainCondList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoSearch"), EntityOperator.LIKE, EntityFunction.UPPER("%" + searchString + "%")));
            }
            if (activeIsNowTimestamp) {
                mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            } else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
                GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
                if (customTimePeriod != null) {
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
                    mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
                }
            }
            if (introductionDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
            }
            if (releaseDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
            }
            EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
            listAllConditions.add(mainCond);

            Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("uomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("barcode");
            selectFields.add("currencyUomId");
            //selectFields.add("sequenceNum");
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("productId");
            }
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            List<GenericValue> listProduct = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "ProductAndCatalogTempAndUomsComplexPro", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);

            if (listProduct != null) {
                for (GenericValue itemProd : listProduct) {
                    Map<String, Object> itemMap = itemProd.getAllFields();

                    Map<String, Object> contextMapFind = UtilMisc.<String, Object>toMap(
                            "productId", itemProd.get("productId"),
                            "productStoreId", productStoreId, "partyId", partyId,
                            "quantityUomId", itemProd.get("uomId"));
                    Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceGroup", contextMapFind);
                    if (ServiceUtil.isSuccess(resultValue)) {
                        BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
                        BigDecimal basePrice = (BigDecimal) resultValue.get("basePrice");
                        BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");

                        itemMap.put("price", basePrice);
                        itemMap.put("unitListPrice", listPrice);
                        itemMap.put("priceVAT", calculatePriceAfterTax(basePrice, taxPercentage));
                        itemMap.put("unitListPriceVAT", calculatePriceAfterTax(listPrice, taxPercentage));
                    }

                    listIterator.add(itemMap);
                }
            }
        }
        result.put("listIterator", listIterator);
        return result;
    }
}