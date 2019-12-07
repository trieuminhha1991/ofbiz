package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.basepo.product.ProductUtils;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.Mobile;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.rmi.CORBA.Util;

public class ProductServices implements Mobile {

	public static final String module = ProductServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";

	public static Map<String, Object> getPriceTax(DispatchContext dpc, Map<String, Object> context) {
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Locale locale = (Locale) context.get("locale");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		String pagesize = (String) context.get("pagesize");
		String[] ps = { pagesize };
		String pagenum = (String) context.get("pagenum");
		String[] pn = { pagenum };
		String partyId = (String) context.get("partyId");
		String[] pr = { partyId };
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
			Map<String, Object> out = dispatcher.runSync("JQFindProductPriceQuotes", in);
			List<Map<String, Object>> listProducts = (List<Map<String, Object>>) out.get("listIterator");
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
	public static Map<String, Object> getProductImage(Delegator delegator, String productId){
		Map<String, Object> res = FastMap.newInstance();
		try {
			GenericValue prod = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
			String large = prod.getString("largeImageUrl");
			String small = prod.getString("smallImageUrl");
			res.put("image", large);
			res.put("image_small", small);
		} catch (GenericEntityException e) {
			Debug.logWarning("Cannot get product", module);
		}
		return res;
	}
	public static Map<String, Object> getProductDetail(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		String productId = (String) context.get("productId");
		String productStoreId = (String) context.get("productStoreId");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Map<String, Object> results = FastMap.newInstance();
		try {
			GenericValue e = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			results.put("productId", e.getString("productId"));
			results.put("productName", e.getString("productName"));
			results.put("productCode", e.getString("productCode"));
			results.put("description", e.getString("description"));
			results.put("image", e.getString("largeImageUrl"));
			results.put("image_small", e.getString("smallImageUrl"));
			Map<String, Object> promos = dispatcher.runSync("getListPromosRuleByProduct", context);
//			Map<String, Object> input = FastMap.newInstance();
//			input.put("userLogin", context.get("userLogin"));
//			input.put("product", e);
//			input.put("productStoreId", productStoreId);
//			Map<String, Object> out = dispatcher.runSync("calculateProductPriceCustom", input);
//			results.put("listPrice", out.get("listPrice"));
//			results.put("defaultPrice", out.get("defaultPrice"));
//			results.put("price", out.get("price"));
			results.putAll(promos);
			res.put("results", results);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}

		return res;
	}

	public static Map<String, Object> mGetProductInfoDetail(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String productId = (String) context.get("productId");
		String productStoreId = (String) context.get("productStoreId");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Map<String, Object> resultsDataProInfoMap = FastMap.newInstance();
		Map<String, Object> results = FastMap.newInstance();
		EntityFindOptions opts = new EntityFindOptions();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		List<GenericValue> listProQuotation = null;
		try {
			Map<String, Object> resultProductInfo = dispatcher.runSync("loadProductInfo", UtilMisc.toMap("productId", productId));
			if (ServiceUtil.isSuccess(resultProductInfo)) {
				Map<String, Object> dataProdInfo = (Map<String, Object>) resultProductInfo.get("product");
				if (UtilValidate.isNotEmpty(dataProdInfo)) {
					String productCode = (String) dataProdInfo.get("productCode");
					String productName = (String) dataProdInfo.get("productName");
					String primaryProductCategoryId = (String) dataProdInfo.get("primaryProductCategoryId");
					String productCategoryTaxId = (String) dataProdInfo.get("productCategoryTaxId");
					String quantityUomId = (String) dataProdInfo.get("quantityUomId");
					GenericValue primaryProductCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", primaryProductCategoryId), false);
					if(UtilValidate.isNotEmpty(primaryProductCategory)){
						String categoryName = (String) primaryProductCategory.get("categoryName");
						resultsDataProInfoMap.put("categoryName", categoryName);//ten danh muc chinh
					}
					GenericValue productCategoryTax = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryTaxId), false);
					if(UtilValidate.isNotEmpty(productCategoryTax)){
						String categoryTaxName = (String) productCategoryTax.get("categoryName");
						resultsDataProInfoMap.put("categoryTaxName", categoryTaxName);//ten danh muc thue
					}
					GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
					if(UtilValidate.isNotEmpty(quantityUom)){
						String quantityUomDesc = (String) quantityUom.get("description");
						resultsDataProInfoMap.put("quantityUomDesc", quantityUomDesc);//don vi co ban
					}
					resultsDataProInfoMap.put("productCode", productCode);//ma san pham
					resultsDataProInfoMap.put("productName", productName);//ten san pham
					GenericValue productDefaultPrice = (GenericValue) dataProdInfo.get("productDefaultPrice");
					GenericValue productListPrice = (GenericValue) dataProdInfo.get("productListPrice");
					Map<String, Object> taxCategoryGV = ProductWorker.getTaxCategoryInfo(delegator, productId, null);
					BigDecimal taxPercentage = (BigDecimal) taxCategoryGV.get("taxPercentage");

					BigDecimal productDefaultPriceValue = null;
					if (productDefaultPrice != null && productDefaultPrice.get("price") != null) {
						productDefaultPriceValue = productDefaultPrice.getBigDecimal("price");
						if (taxPercentage != null) {
							productDefaultPriceValue = ProductUtils.calculatePriceAfterTax(productDefaultPriceValue, taxPercentage);
						}
					}
					resultsDataProInfoMap.put("productDefaultPriceValue", productDefaultPriceValue);//gia mac dinh

					BigDecimal productListPriceValue = null;
					if (productListPrice != null && productListPrice.get("price") != null) {
						productListPriceValue = productListPrice.getBigDecimal("price");
						if (taxPercentage != null) {
							productListPriceValue = ProductUtils.calculatePriceAfterTax(productListPriceValue, taxPercentage);
						}
					}
					resultsDataProInfoMap.put("productListPriceValue", productListPriceValue);// gia niem yet
				}
			}
			GenericValue prod = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			Map<String, Object> productVirtualTypeEnumsMap = FastMap.newInstance();
			productVirtualTypeEnumsMap.put("PROD_FINISH", UtilProperties.getMessage(resource, "BSProductNormal", locale ));
			productVirtualTypeEnumsMap.put("PROD_VIRTUAL", UtilProperties.getMessage(resource, "BSProductVirtualD", locale));
			productVirtualTypeEnumsMap.put("PROD_VARIANT", UtilProperties.getMessage(resource, "BSProductVariant", locale));
			productVirtualTypeEnumsMap.put("PROD_CONFIG", UtilProperties.getMessage(resource, "BSProductConfiguration", locale));

			if(UtilValidate.isNotEmpty(prod)){
				if(prod.get("productTypeId").equals("AGGREGATED")){
					resultsDataProInfoMap.put("productTypeId", productVirtualTypeEnumsMap.get("PROD_CONFIG"));
				}else {
					if(("N".equals(prod.get("isVirtual")))&& ("N".equals(prod.get("isVariant"))) ){
						resultsDataProInfoMap.put("productTypeId", productVirtualTypeEnumsMap.get("PROD_FINISH"));
					}else if (("Y".equals(prod.get("isVirtual")))&& ("N".equals(prod.get("isVariant")))){
						resultsDataProInfoMap.put("productTypeId", productVirtualTypeEnumsMap.get("PROD_VIRTUAL"));
					}else if (("N".equals(prod.get("isVirtual")))&& ("Y".equals(prod.get("isVariant")))){
						resultsDataProInfoMap.put("productTypeId", productVirtualTypeEnumsMap.get("PROD_VARIANT"));
					}
				}
			}
			List<Map<String, Object>> listPromos = new ArrayList<Map<String, Object>>();
			Map<String, Object> resultGetList = dispatcher.runSync("getListPromosRuleByProduct",
					UtilMisc.toMap("productId", productId, "isGetAll", "N", "isGetFutureActive", "Y",
							"productStoreId", productStoreId, "userLogin", userLogin, "locale", locale));
			if (ServiceUtil.isSuccess(resultGetList)) {
				List<GenericValue> listPromoRules = (List<GenericValue>) resultGetList.get("listPromoRules");
				if (UtilValidate.isNotEmpty(listPromoRules)) {
					List<String> productPromoIds = EntityUtil.getFieldListFromEntityList(listPromoRules, "productPromoId", true);
					List<GenericValue> listProductPromo = delegator.findList("ProductPromo", EntityCondition.makeCondition("productPromoId", EntityOperator.IN, productPromoIds), null, null, null, false);

					for (GenericValue promo : listProductPromo) {
						//Map<String, Object> productPromoMap = promo.getAllFields();
						Map<String, Object> productPromoMap = FastMap.newInstance();
						productPromoMap.put("productPromoId", promo.get("productPromoId"));
						productPromoMap.put("promoName", promo.get("promoName"));
						productPromoMap.put("createdDate", promo.get("createdDate"));
						productPromoMap.put("fromDate", promo.get("fromDate"));
						productPromoMap.put("thruDate", promo.get("thruDate"));
						String ruleTextStr = "";
						List<GenericValue> promoRules = EntityUtil.filterByCondition(listPromoRules, EntityCondition.makeCondition("productPromoId", promo.get("productPromoId")));
						if (UtilValidate.isNotEmpty(promoRules)) {
							List<String> ruleTexts = EntityUtil.getFieldListFromEntityList(promoRules, "ruleText", false);
							if (UtilValidate.isNotEmpty(ruleTexts)) ruleTextStr = StringUtils.join(ruleTexts, "<br/>");
						}
						productPromoMap.put("ruleTexts", ruleTextStr);
						listPromos.add(productPromoMap);
					}
				}
			}
			//getListProductQuotation
			List<Map<String, Object>> listProductQuotation = new ArrayList<Map<String, Object>>();
			opts.setDistinct(true);
			if (UtilValidate.isNotEmpty(productId)) {
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				listAllConditions.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
				//listAllConditions.add(EntityUtil.getFilterByDateExpr());
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}
				listAllConditions.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "SALES_QUOTATION"));
				listProQuotation = delegator.findList("ProductQuotationProductStoreApplPriceRCADetail",EntityCondition.makeCondition(listAllConditions),
						null,null,null,false);
			}
			if(UtilValidate.isNotEmpty(listProQuotation)){
				for (GenericValue prodQuo : listProQuotation) {
					Map<String, Object> productQuotationMap = FastMap.newInstance();
					productQuotationMap.put("productQuotationId", prodQuo.get("productQuotationId"));
					productQuotationMap.put("quotationName", prodQuo.get("quotationName"));
					productQuotationMap.put("storeName", prodQuo.get("storeName"));
					//productQuotationMap.put("listPriceVAT", prodQuo.get("listPriceVAT"));
					productQuotationMap.put("listPrice", prodQuo.get("listPrice"));
					productQuotationMap.put("currencyUomId", prodQuo.get("currencyUomId"));
					GenericValue quantityUomQuotation = delegator.findOne("Uom", UtilMisc.toMap("uomId", prodQuo.get("quantityUomId")), false);
					if(UtilValidate.isNotEmpty(quantityUomQuotation)) {
						String quantityUomDescQuotation = (String) quantityUomQuotation.get("description");
						productQuotationMap.put("quantityUomDesc", quantityUomDescQuotation);
					}
					productQuotationMap.put("fromDate", prodQuo.get("fromDate"));
					productQuotationMap.put("thruDate", prodQuo.get("thruDate"));
					listProductQuotation.add(productQuotationMap);
				}
			}


			res.put("resultsDataProInfoMap", resultsDataProInfoMap);
			res.put("listProductPromotion", listPromos);
			res.put("listProductQuotation", listProductQuotation);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}

		return res;
	}
}
