package com.olbius.basepos.events;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.product.store.ProductStoreWorker;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepos.session.WebPosSession;
import com.olbius.basepos.util.VNCharacterUtils;
import com.olbius.basesales.product.ProductUtils;
import com.olbius.basesales.product.ProductWorker;

import javolution.util.FastList;
import javolution.util.FastMap;

public class WebPosSearch {
	public static final String module = WebPosSearch.class.getName();
	public static String resource_error = "BasePosErrorUiLabels";

	public static String findProducts(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		//LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");
		ShoppingCart cart = webposSession.getCart();

		String productStoreId = cart.getProductStoreId();
		GenericValue productStore = null;
		try {
			productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
		} catch (GenericEntityException e2) {
			Debug.logError(e2.getMessage(), module);
		}

		if (UtilValidate.isEmpty(productStore)) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSWebPosSessionNotFound", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}

		String showPricesWithVatTax = productStore.getString("showPricesWithVatTax");

		boolean isIdEan = false;
		List<GenericValue> productList = FastList.newInstance();
		List<Map<String, Object>> productWithTaxList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(webposSession) && UtilValidate.isNotEmpty(cart)) {
			List<GenericValue> productTax = FastList.newInstance();
			String productToSearch = request.getParameter("productToSearch");
			String facilityId = cart.getFacilityId();
			if (UtilValidate.isNotEmpty(productToSearch)) {
				List<EntityCondition> orConds = FastList.newInstance();
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() + "%"));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch + "%")));
				
				String idPLU = null;
				BigDecimal amount = null;
            	if (productToSearch.length() == 13) {
            		idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
            		if (idPLU != null) { // EAN13 code
            			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
            			isIdEan = true;
            		}
            	}/* else if (productToSearch.length() == 12) {
            		// BigDecimal modifyPrice = null; TODO allow UPCA code with price embed
            		idPLU = ProductUtils.getPluCodeInUpcId(productToSearch);
            		if (idPLU != null) {
            			// UPC-A code
            			orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
            			isIdUpc = true;
            		}
            	}*/
				
				EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
				List<EntityCondition> mainConds = FastList.newInstance();
				mainConds.add(orCond);

				mainConds.add(EntityCondition.makeCondition(
						EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),
						EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate",
								EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis()))));

				mainConds.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD"));
				mainConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
				mainConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				if ("Y".equals(productStore.get("requireInventory"))) {
					mainConds.add(EntityCondition.makeCondition("qoh", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				}
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				EntityCondition mainCond = EntityCondition.makeCondition(mainConds);
				try {
					productList = delegator.findList("ProductAndPriceAndGoodIdentificationAndFacilityAndProStore", mainCond,
										new HashSet<String>(Arrays.asList("qoh", "currencyUomId", "termUomId", "price", "productId",
												"productName", "productCode", "requireAmount")), UtilMisc.toList("productCode"), findOptions, false);
				} catch (GenericEntityException e) {
					Debug.logError(e.getMessage(), module);
				}
				if (showPricesWithVatTax.equals("Y")) {
					if (UtilValidate.isNotEmpty(productList)) {
						List<String> tmpListProductId = FastList.newInstance();
						for (GenericValue pro : productList) {
							tmpListProductId.add(pro.getString("productId"));
						}
						EntityCondition tmpEC = EntityCondition.makeCondition("productId", EntityOperator.IN, tmpListProductId);
						try {
							productTax = delegator.findList("ProductAndTaxAuthorityRate", tmpEC, null, null, null, true);
						} catch (GenericEntityException e1) {
							Debug.logError(e1.getMessage(), module);
						}

						Map<String, Object> productTaxMap = FastMap.newInstance();
						if (UtilValidate.isNotEmpty(productTax)) {
							for (GenericValue product : productTax) {
								productTaxMap.put(product.getString("productId"), product.getBigDecimal("taxPercentage"));
							}
						}
						if (isIdEan) {
							try {
								amount = ProductWorker.getAmountProductEan(productToSearch, delegator, locale);
							} catch (GenericEntityException e) {
								Debug.logError(e.getMessage(), module);
							}
							
							/*try {
								Map<String, Object> infoAndAmountResult = dispatcher.runSync("getProductIdAndAmountByUPCA", UtilMisc.toMap("idUPCA", productToSearch));
								if (ServiceUtil.isSuccess(infoAndAmountResult)) {
			            			// modifyPrice = (BigDecimal) infoAndAmountResult.get("price");
			            			amount = (BigDecimal) infoAndAmountResult.get("amount");
			            		}
							} catch (GenericServiceException e) {
								Debug.logWarning("Error: don't get info and amount of product by UPCA code", module);
							}*/
						}
						for (GenericValue pro : productList) {
							Map<String, Object> productMap = pro.getAllFields();
							String productId = pro.getString("productId");
							BigDecimal price = pro.getBigDecimal("price");
							BigDecimal taxPercent = BigDecimal.ZERO;
							if (UtilValidate.isNotEmpty(productTaxMap) && productTaxMap.containsKey(productId)) {
								taxPercent = (BigDecimal) productTaxMap.get(productId);
							}
							BigDecimal tax = price.multiply(taxPercent).divide(new BigDecimal(100));
							BigDecimal priceWithTax = price.add(tax);
							productMap.put("price", priceWithTax);
							if (isIdEan) {
								productMap.put("amount", amount);
								//productMap.put("idUPCA", productToSearch);
								productMap.put("idEAN", productToSearch);
							}
							productWithTaxList.add(productMap);
						}
					}
				} else if (isIdEan) {
					if (UtilValidate.isNotEmpty(productList)) {
						/*try {
							Map<String, Object> infoAndAmountResult = dispatcher.runSync("getProductIdAndAmountByUPCA", UtilMisc.toMap("idUPCA", productToSearch));
							if (ServiceUtil.isSuccess(infoAndAmountResult)) {
		            			// modifyPrice = (BigDecimal) infoAndAmountResult.get("price");
		            			amount = (BigDecimal) infoAndAmountResult.get("amount");
		            		}
						} catch (GenericServiceException e) {
							Debug.logWarning("Error: don't get info and amount of product by UPCA code", module);
						}*/
						
						try {
							amount = ProductWorker.getAmountProductEan(productToSearch, delegator, locale);
						} catch (GenericEntityException e) {
							Debug.logError(e.getMessage(), module);
						}
						
						for (GenericValue pro : productList) {
							Map<String, Object> productMap = pro.getAllFields();
							productMap.put("amount", amount);
							//productMap.put("idUPCA", productToSearch);
							productMap.put("idEAN", productToSearch);
							productWithTaxList.add(productMap);
						}
					}
				}
			}
		} else {
			Debug.log(UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale));
		}

		if (showPricesWithVatTax.equals("Y") || isIdEan) {
			request.setAttribute("productsList", productWithTaxList);
		} else {
			request.setAttribute("productsList", productList);
		}
		return "success";
	}

	public static String findParties(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = request.getLocale();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyToSearch = request.getParameter("partyToSearch");
		String productStoreId = ProductStoreWorker.getProductStoreId(request);
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		List<GenericValue> partyList = FastList.newInstance();
		if (UtilValidate.isNotEmpty(userLogin)) {
			List<String> orderBy = FastList.newInstance();
			List<EntityCondition> mainContions = FastList.newInstance();
			List<EntityCondition> orConditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyToSearch)) {
				// Convert to Uppercase
				partyToSearch = partyToSearch.toUpperCase().trim();
				// Remove duplicate space in string
				partyToSearch = partyToSearch.replaceAll("\\s+", " ");
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastMiddle"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleFirst"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastMiddleFirst"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("phoneMobile"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("phoneWork"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
				orConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("phoneHome"),
						EntityOperator.LIKE, EntityFunction.UPPER("%" + partyToSearch + "%")));
			}
			EntityCondition orCondition = EntityCondition.makeCondition(orConditions, EntityOperator.OR);
			mainContions.add(orCondition);
			mainContions.add(EntityCondition.makeCondition("productStoreId", productStoreId));
			mainContions.add(EntityCondition.makeCondition("payToPartyId", organizationId));
			mainContions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "SMCHANNEL_POS"));
			EntityCondition mainCondition = EntityCondition.makeCondition(mainContions);
			orderBy.add("partyId");
			orderBy.add("firstName");
			orderBy.add("lastMiddleFirst");
			orderBy.add("lastMiddle");
			try {
				partyList = delegator.findList("CustomerAndStore", mainCondition, null, orderBy, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		} else {
			Debug.log(UtilProperties.getMessage(resource_error, "BPOSNotLoggedIn", locale));
		}
		request.setAttribute("partiesList", partyList);
		return "success";
	}

}