package com.olbius.basesales.product;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.config.ProductConfigWrapperException;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductConfig2Worker {
	public static final String module = ProductConfig2Worker.class.getName();
	public static final String resource = "ProductUiLabels";
    public static final String SEPARATOR = "::";    // cache key separator
    
    private static final UtilCache<String, ProductConfigWrapper> productConfigCache = UtilCache.createUtilCache("product.config", true);     // use soft reference to free up memory if needed
	
	public static ProductConfigWrapper getProductConfigWrapper(String productId, String currencyUomId, Delegator delegator, LocalDispatcher dispatcher, Locale locale, String catalogId, String webSiteId, String currentCatalogId, String productStoreId, GenericValue autoUserLogin, String quantityUomId) {
        ProductConfigWrapper configWrapper = null;
        try {
            /* caching: there is one cache created, "product.config"  Each product's config wrapper is cached with a key of
             * productId::catalogId::webSiteId::currencyUomId, or whatever the SEPARATOR is defined above to be.
             */
            String cacheKey = productId + SEPARATOR + productStoreId + SEPARATOR + catalogId + SEPARATOR + webSiteId + SEPARATOR + currencyUomId;
            configWrapper = productConfigCache.get(cacheKey);
            if (configWrapper == null) {
                configWrapper = new ProductConfigWrapper(delegator, dispatcher, productId, productStoreId, catalogId, webSiteId, currencyUomId, locale, autoUserLogin, quantityUomId);
                configWrapper = productConfigCache.putIfAbsentAndGet(cacheKey, new ProductConfigWrapper(configWrapper));
            } else {
                configWrapper = new ProductConfigWrapper(configWrapper);
            }
        } catch (ProductConfigWrapperException we) {
            configWrapper = null;
        } catch (Exception e) {
            Debug.logWarning(e.getMessage(), module);
        }
        return configWrapper;
    }
	
	public static Map<String, Object> fillProductConfigWrapper(ProductConfigWrapper configWrapper, Delegator delegator, Map<String, Object> parameters, Locale locale) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		if (parameters == null) parameters = FastMap.newInstance();
        int numOfQuestions = configWrapper.getQuestions().size();
        for (int k = 0; k < numOfQuestions; k++) {
            String[] opts = (String[]) parameters.get(Integer.toString(k));
            if (opts == null) {

                //  check for standard item comments
                ProductConfigWrapper.ConfigItem question = configWrapper.getQuestions().get(k);
                if (question.isStandard()) {
                    int i = 0;
                    while (i <= (question.getOptions().size() -1)) {
                        String comments = (String) parameters.get("comments_" + k + "_" + i);
                        if (UtilValidate.isNotEmpty(comments)) {
                            try {
                                configWrapper.setSelected(k, i, comments);
                            } catch (Exception e) {
                                Debug.logWarning(e.getMessage(), module);
                            }
                        }
                        i++;
                    }
                }
                continue;
            }
            for (String opt: opts) {
                int cnt = -1;
                try {
                    cnt = Integer.parseInt(opt);
                    String comments = null;
                    ProductConfigWrapper.ConfigItem question = configWrapper.getQuestions().get(k);
                    if (question.isSingleChoice()) {
                        comments = (String) parameters.get("comments_" + k + "_" + "0");
                    } else {
                        comments = (String) parameters.get("comments_" + k + "_" + cnt);
                    }

                    configWrapper.setSelected(k, cnt, comments);
                    ProductConfigWrapper.ConfigOption option = configWrapper.getItemOtion(k, cnt);

                    //  set selected variant products
                    if (UtilValidate.isNotEmpty(option) && (option.hasVirtualComponent())) {
                        List<GenericValue> components = option.getComponents();
                        int variantIndex = 0;
                        for (int i = 0; i < components.size(); i++) {
                            GenericValue component = components.get(i);
                            if (option.isVirtualComponent(component)) {
                                String productParamName = "add_product_id" + k + "_" + cnt + "_" + variantIndex;
                                String selectedProductId = (String) parameters.get(productParamName);
                                if (UtilValidate.isEmpty(selectedProductId)) {
                                    Debug.logWarning("ERROR: Request param [" + productParamName + "] not found!", module);
                                } else {

                                    //  handle also feature tree virtual variant methods
                                    if (ProductWorker.isVirtual(delegator, selectedProductId)) {
                                        if ("VV_FEATURETREE".equals(ProductWorker.getProductVirtualVariantMethod(delegator, selectedProductId))) {
                                            // get the selected features
                                            List<String> selectedFeatures = FastList.newInstance();
                                            Enumeration<String> paramNames = UtilGenerics.cast(parameters.keySet());
                                            while (paramNames.hasMoreElements()) {
                                                String paramName = paramNames.nextElement();
                                                if (paramName.startsWith("FT" + k + "_" + cnt + "_" + variantIndex)) {
                                                    selectedFeatures.add(((String[]) parameters.get(paramName))[0]);
                                                }
                                            }

                                            // check if features are selected
                                            if (UtilValidate.isEmpty(selectedFeatures)) {
                                                Debug.logWarning("ERROR: No features selected for productId [" + selectedProductId+ "]", module);
                                            }

                                            String variantProductId = ProductWorker.getVariantFromFeatureTree(selectedProductId, selectedFeatures, delegator);
                                            if (UtilValidate.isNotEmpty(variantProductId)) {
                                                selectedProductId = variantProductId;
                                            } else {
                                                Debug.logWarning("ERROR: Variant product not found!", module);
                                                return ServiceUtil.returnError(UtilProperties.getMessage("OrderErrorUiLabels", "cart.addToCart.incompatibilityVariantFeature", locale));
                                           }
                                        }
                                    }
                                    configWrapper.setSelected(k, cnt, i, selectedProductId);
                                }
                                variantIndex ++;
                            }
                        }
                    }
                } catch (Exception e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
        }
        return successResult;
    }
}
