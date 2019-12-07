package com.olbius.basepo.productSupplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepo.utils.POUtil;
import com.olbius.security.util.SecurityUtil;

public class ProductSupplierServices {
	public static final String module = ProductSupplierServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSupplierProductConfig(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				if (UtilValidate.isNotEmpty(partyId)) {
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
			}
			listAllConditions.add(EntityCondition.makeCondition("supplierPrefOrderId", EntityOperator.EQUALS, "10_MAIN_SUPPL"));
			List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
			listCondOr.add(EntityCondition.makeCondition("availableThruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
			listCondOr.add(EntityCondition.makeCondition("availableThruDate", EntityOperator.EQUALS, null));
			listAllConditions.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
			
			listSortFields.add("productId");
			EntityListIterator listIterator = delegator.find("SuppProAndProdConfAndParty", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			List<GenericValue> list = new ArrayList<GenericValue>();
			list = POUtil.getIteratorPartialList(listIterator, parameters, successResult);
			successResult.put("listIterator", list);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getSupplierProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		return successResult;
	}

	public static Map<String, Object> loadProduct(DispatchContext dpx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		List<GenericValue> listProduct = delegator.findList("Product",
				EntityCondition.makeCondition(UtilMisc.toMap("isVirtual", "N")), null, null, null, false);
		result.put("listProduct", listProduct);
		return result;
	}

	public static Map<String, Object> loadCurrencyUomIdBySupplier(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String) context.get("partyId");
		List<GenericValue> listProductCurrencyUomId = new ArrayList<GenericValue>();
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		listProductCurrencyUomId.add(party);
		result.put("listProductCurrencyUomId", listProductCurrencyUomId);
		return result;
	}

	public static Map<String, Object> addNewSupplierForProductId(DispatchContext dpx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Security security = dpx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SUP_PRODUCT_NEW");
		if (!hasPermission) {
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}

		String partyId = (String) context.get("partyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String comments = (String) context.get("comments");
		String availableFromDateStr = (String) context.get("availableFromDate");
		String availableThruDateStr = (String) context.get("availableThruDate");
		String productId = (String) context.get("productId");
		String canDropShip = (String) context.get("canDropShip");
		String supplierProductId = (String) context.get("supplierProductId");
		
		String lastPriceStr = (String) context.get("lastPrice");
		BigDecimal lastPrice = UtilValidate.isNotEmpty(lastPriceStr) ? new BigDecimal(lastPriceStr) : null;
		
		String quantityUomId = (String) context.get("quantityUomId");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
		if (UtilValidate.isEmpty(quantityUomId) && product != null) {
			quantityUomId = product.getString("quantityUomId");
		}
		
		String shippingPriceStr = (String) context.get("shippingPrice");
		BigDecimal shippingPrice = null;
		if (shippingPriceStr != null) {
			shippingPrice = new BigDecimal(shippingPriceStr);
		}
		
		String minimumOrderQuantityStr = (String) context.get("minimumOrderQuantity");
		BigDecimal minimumOrderQuantity = UtilValidate.isNotEmpty(minimumOrderQuantityStr) ? new BigDecimal(minimumOrderQuantityStr) : null;
		
		Timestamp availableFromDate = null;
		if (UtilValidate.isNotEmpty(availableFromDateStr)) {
			Long availableFromDateLog = Long.parseLong(availableFromDateStr);
			availableFromDate = new Timestamp(availableFromDateLog);
		}
		Timestamp availableThruDate = null;
		if (UtilValidate.isNotEmpty(availableThruDateStr)) {
			Long availableThruDateLog = Long.parseLong(availableThruDateStr);
			availableThruDate = new Timestamp(availableThruDateLog);
		}
		
		String supplierPrefOrderId = (String) context.get("supplierPrefOrderId");
		if (UtilValidate.isEmpty(supplierPrefOrderId)) {
			supplierPrefOrderId = "10_MAIN_SUPPL";
		}

		GenericValue supplierProduct = delegator.findOne("SupplierProduct", 
				UtilMisc.toMap("partyId", partyId, "productId", productId, "currencyUomId", currencyUomId,
						"availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity), false);
		if (supplierProduct == null) {
			List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", 
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "productId", productId, 
							"quantityUomId", quantityUomId, "currencyUomId", currencyUomId)), null, null, null, false);
			if (UtilValidate.isNotEmpty(listSupplierProduct)) {
				Date dateCurrent = new Date();
				Timestamp currentTime = new Timestamp(dateCurrent.getTime());
				for (GenericValue sp : listSupplierProduct) {
					Timestamp availableThruDateOld = sp.getTimestamp("availableThruDate");
					if (availableThruDateOld != null) {
						if (availableThruDateOld.compareTo(currentTime) > 0) {
							sp.put("availableThruDate", currentTime);
							delegator.store(sp);
						}
					} else {
						sp.put("availableThruDate", currentTime);
						delegator.store(sp);
					}
				}
			}
			
			// create new record
			Map<String, Object> contextInput = UtilMisc.toMap("partyId", partyId, "productId", productId, 
					"supplierPrefOrderId", supplierPrefOrderId, "quantityUomId", quantityUomId, "canDropShip", canDropShip, 
					"supplierProductId", supplierProductId, "currencyUomId", currencyUomId,
					"availableFromDate", availableFromDate, "availableThruDate", availableThruDate, 
					"minimumOrderQuantity", minimumOrderQuantity, "lastPrice", lastPrice, "shippingPrice", shippingPrice, 
					"comments", comments, "userLogin", userLogin, "locale", locale);
			try {
				Map<String, Object> createResult = dispatcher.runSync("createSupplierProduct", contextInput);
				if (ServiceUtil.isError(createResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, e.getMessage(), module);
				return ServiceUtil.returnError(e.getStackTrace().toString());
			}
			
			result.put("value", "success");
		} else {
			result.put("value", "exits");
		}

		return result;
	}
	
	public static Map<String, Object> addNewSupplierProduct(DispatchContext dpx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Security security = dpx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SUP_PRODUCT_NEW");
		if (!hasPermission) {
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
		}

		String partyId = (String) context.get("partyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String comments = (String) context.get("comments");
		String availableFromDateStr = (String) context.get("availableFromDate");
		String availableThruDateStr = (String) context.get("availableThruDate");
		String productId = (String) context.get("productId");
		String canDropShip = (String) context.get("canDropShip");
		String supplierProductId = (String) context.get("supplierProductId");
		
		String lastPriceStr = (String) context.get("lastPrice");
		BigDecimal lastPrice = UtilValidate.isNotEmpty(lastPriceStr) ? new BigDecimal(lastPriceStr) : null;
		
		String quantityUomId = (String) context.get("quantityUomId");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
		if (UtilValidate.isEmpty(quantityUomId) && product != null) {
			quantityUomId = product.getString("quantityUomId");
		}
		
		String shippingPriceStr = (String) context.get("shippingPrice");
		BigDecimal shippingPrice = null;
		if (shippingPriceStr != null) {
			shippingPrice = new BigDecimal(shippingPriceStr);
		}
		
		String minimumOrderQuantityStr = (String) context.get("minimumOrderQuantity");
		BigDecimal minimumOrderQuantity = UtilValidate.isNotEmpty(minimumOrderQuantityStr) ? new BigDecimal(minimumOrderQuantityStr) : null;
		
		Timestamp availableFromDate = null;
		if (UtilValidate.isNotEmpty(availableFromDateStr)) {
			Long availableFromDateLog = Long.parseLong(availableFromDateStr);
			availableFromDate = new Timestamp(availableFromDateLog);
		}
		Timestamp availableThruDate = null;
		if (UtilValidate.isNotEmpty(availableThruDateStr)) {
			Long availableThruDateLog = Long.parseLong(availableThruDateStr);
			availableThruDate = new Timestamp(availableThruDateLog);
		}
		
		String supplierPrefOrderId = (String) context.get("supplierPrefOrderId");
		if (UtilValidate.isEmpty(supplierPrefOrderId)) {
			supplierPrefOrderId = "10_MAIN_SUPPL";
		}

		GenericValue supplierProduct = delegator.findOne("SupplierProduct", 
						UtilMisc.toMap("partyId", partyId, "productId", productId, "currencyUomId", currencyUomId, 
								"availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity), false);
		if (supplierProduct == null) {
			// create new record
			Map<String, Object> contextInput = UtilMisc.toMap("partyId", partyId, "productId", productId, 
					"supplierPrefOrderId", supplierPrefOrderId, "quantityUomId", quantityUomId, "canDropShip", canDropShip, 
					"supplierProductId", supplierProductId, "currencyUomId", currencyUomId,
					"availableFromDate", availableFromDate, "availableThruDate", availableThruDate, 
					"minimumOrderQuantity", minimumOrderQuantity, "lastPrice", lastPrice, "shippingPrice", shippingPrice, 
					"comments", comments, "userLogin", userLogin, "locale", locale);
			try {
				Map<String, Object> createResult = dispatcher.runSync("createSupplierProduct", contextInput);
				if (ServiceUtil.isError(createResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, e.getMessage(), module);
				return ServiceUtil.returnError(e.getStackTrace().toString());
			}
			
			result.put("value", "success");
		} else {
			// update record
			BigDecimal lastPriceOld = supplierProduct.getBigDecimal("lastPrice");
			if (lastPriceOld != null && lastPriceOld.compareTo(lastPrice) != 0) {
				supplierProduct.set("lastPrice", lastPrice);
				delegator.store(supplierProduct);
			}
			
			result.put("value", "success");
		}

		return result;
	}
	
	/*
	 * TODOCHANGE change to replace service addNewSupplierForProductId
	 */
	/*public static Map<String, Object> createSupplierProductOlb(DispatchContext dpx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Security security = dpx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		try {
			boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "SUP_PRODUCT_NEW");
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale));
			}
			
			String productId = (String) context.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSRecordHasIdIsNotFound", locale));
			}
			String quantityUomId = (String) context.get("quantityUomId");
			if (UtilValidate.isEmpty(quantityUomId)) {
				quantityUomId = product.getString("quantityUomId");
			}
			
			String partyId = (String) context.get("partyId");
			String currencyUomId = (String) context.get("currencyUomId");
			String comments = (String) context.get("comments");
			String canDropShip = (String) context.get("canDropShip");
			
			String supplierProductId = (String) context.get("supplierProductId");
			if (UtilValidate.isEmpty(supplierProductId)) {
				supplierProductId = productId;
			}
			
			BigDecimal lastPrice = null;
			String lastPriceStr = (String) context.get("lastPrice");
			if (UtilValidate.isNotEmpty(lastPriceStr)) {
				lastPrice = new BigDecimal(lastPriceStr);
			}
			
			BigDecimal shippingPrice = null;
			String shippingPriceStr = (String) context.get("shippingPrice");
			if (UtilValidate.isNotEmpty(shippingPriceStr)) {
				shippingPrice = new BigDecimal(shippingPriceStr);
			}
			
			BigDecimal minimumOrderQuantity = null;
			String minimumOrderQuantityStr = (String) context.get("minimumOrderQuantity");
			if (UtilValidate.isNotEmpty(minimumOrderQuantityStr)) {
				minimumOrderQuantity = new BigDecimal(minimumOrderQuantityStr);
			}
			
			Timestamp availableFromDate = null;
			String availableFromDateStr = (String) context.get("availableFromDate");
			if (UtilValidate.isNotEmpty(availableFromDateStr)) {
				Long availableFromDateLog = Long.parseLong(availableFromDateStr);
				if (availableFromDateLog != null) availableFromDate = new Timestamp(availableFromDateLog);
			}
			
			Timestamp availableThruDate = null;
			String availableThruDateStr = (String) context.get("availableThruDate");
			if (UtilValidate.isNotEmpty(availableThruDateStr)) {
				Long availableThruDateLog = Long.parseLong(availableThruDateStr);
				if (availableThruDateLog != null) availableThruDate = new Timestamp(availableThruDateLog);
			}
			
			String supplierPrefOrderId = (String) context.get("supplierPrefOrderId");
			if (UtilValidate.isEmpty(supplierPrefOrderId)) supplierPrefOrderId = "10_MAIN_SUPPL";
			
			GenericValue supplierProduct = delegator.findOne("SupplierProduct", UtilMisc.toMap(
					"partyId", partyId, "productId", productId, "currencyUomId", currencyUomId,
					"availableFromDate", availableFromDate, "minimumOrderQuantity", minimumOrderQuantity), false);
			if (supplierProduct == null) {
				// don't know business of this case
				List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", 
								EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId,
										"productId", productId, "currencyUomId", currencyUomId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(listSupplierProduct)) {
					Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
					for (GenericValue item : listSupplierProduct) {
						Timestamp itemAvailableThruDate = item.getTimestamp("availableThruDate");
						if (itemAvailableThruDate != null) {
							if (itemAvailableThruDate.compareTo(nowTimestamp) > 0) {
								item.put("availableThruDate", nowTimestamp);
								delegator.store(item);
							}
						} else {
							item.put("availableThruDate", nowTimestamp);
							delegator.store(item);
						}
					}
				}
				
				Map<String, Object> contextInput = UtilMisc.toMap("userLogin", userLogin, "locale", locale,
						"supplierPrefOrderId", supplierPrefOrderId, "quantityUomId", quantityUomId, 
						"partyId", partyId, "productId", productId, "supplierProductId", supplierProductId, 
						"availableFromDate", availableFromDate, "availableThruDate", availableThruDate, 
						"minimumOrderQuantity", minimumOrderQuantity, "currencyUomId", currencyUomId, 
						"lastPrice", lastPrice, "shippingPrice", shippingPrice, 
						"canDropShip", canDropShip, "comments", comments);
				Map<String, Object> supplierProdCreateResult = dispatcher.runSync("createSupplierProduct", contextInput);
				if (ServiceUtil.isError(supplierProdCreateResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(supplierProdCreateResult));
				}
				
				result.put("value", "success");
			} else {
				result.put("value", "exits");
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling createSupplierProductOlb service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		
		return result;
	}*/
	public static Map<String, Object> updateJqxSupplierProductConfig(DispatchContext ctx, Map<String, Object> context) {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"SUP_PRODUCT_EDIT");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}
		Map<String, Object> result = FastMap.newInstance();
		try {
			String productId = (String) context.get("productId");
			String partyId = (String) context.get("partyId");
			String currencyUomId = (String) context.get("currencyUomId");
			String minimumOrderQuantityString = (String) context.get("minimumOrderQuantity");
			BigDecimal minimumOrderQuantity = new BigDecimal(minimumOrderQuantityString);
			java.sql.Timestamp availableFromDate = (Timestamp) context.get("availableFromDate");
			java.sql.Timestamp availableThruDate = (Timestamp) context.get("availableThruDate");

			// Get data to be updated
			String supplierProductId = (String) context.get("supplierProductId");
			String lastPriceString = (String) context.get("lastPrice");
			BigDecimal lastPrice = new BigDecimal(lastPriceString);

			Map<String, Object> contextTmpSupplierProduct = new HashMap<String, Object>();
			contextTmpSupplierProduct.put("productId", productId);
			contextTmpSupplierProduct.put("partyId", partyId);
			contextTmpSupplierProduct.put("currencyUomId", currencyUomId);
			contextTmpSupplierProduct.put("minimumOrderQuantity", minimumOrderQuantity);
			contextTmpSupplierProduct.put("availableFromDate", availableFromDate);
			
			GenericValue supplierProduct = delegator.findOne("SupplierProduct", contextTmpSupplierProduct, false);
			
			if (UtilValidate.isNotEmpty(supplierProduct)) {
				contextTmpSupplierProduct.put("availableThruDate", UtilDateTime.nowTimestamp());
				contextTmpSupplierProduct.put("userLogin", userLogin);
				dispatcher.runSync("updateSupplierProduct", contextTmpSupplierProduct);
				
				contextTmpSupplierProduct.clear();
				contextTmpSupplierProduct.putAll(supplierProduct);
				contextTmpSupplierProduct.put("userLogin", userLogin);
				contextTmpSupplierProduct.put("productId", productId);
				contextTmpSupplierProduct.put("partyId", partyId);
				contextTmpSupplierProduct.put("currencyUomId", currencyUomId);
				contextTmpSupplierProduct.put("minimumOrderQuantity", minimumOrderQuantity);
				contextTmpSupplierProduct.put("availableFromDate", UtilDateTime.nowTimestamp());
				contextTmpSupplierProduct.put("availableThruDate", availableThruDate);
				contextTmpSupplierProduct.put("supplierProductId", supplierProductId);
				contextTmpSupplierProduct.put("lastPrice", lastPrice);
				
				dispatcher.runSync("createSupplierProduct", ctx.getModelService("createSupplierProduct").makeValid(contextTmpSupplierProduct, ModelService.IN_PARAM));
			}
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSupplierTarget(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("SupplierAndProductAndTarget",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListSupplierTarget service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listIterator);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListSupplierFinAccountJQ(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyId = parameters.get("partyId") != null? parameters.get("partyId")[0] : null; 
		listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", partyId));
		if(UtilValidate.isEmpty(listSortFields)){
			listSortFields.add("statusId");
		}
		try {
			listIterator = delegator.find("FinAccountAndGeo", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		result.put("listIterator", listIterator);
		return result;
	}
}
