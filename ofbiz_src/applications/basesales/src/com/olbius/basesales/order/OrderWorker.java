package com.olbius.basesales.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class OrderWorker {
	public static final String module = OrderWorker.class.getName();
	public static final String resource = "SalesUiLabels";
    public static final String resource_error = "SalesErrorUiLabels";
    public static final String resource_order_origin = "OrderUiLabels";
	public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
    public static int salestaxCalcDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    
	public static BigDecimal getGrandTotalOrder(Delegator delegator, String agreementId) throws GenericEntityException {
		BigDecimal grandTotal = BigDecimal.ZERO;
		List<GenericValue> listOrderHeader = delegator.findList("OrderHeader", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		if (UtilValidate.isNotEmpty(listOrderHeader)) {
			for (GenericValue x : listOrderHeader) {
				grandTotal = grandTotal.add(x.getBigDecimal("grandTotal"));
			}
		}
		return grandTotal;
	}
	
	public static Map<String, Object> getInfoOrderListOrdered(Delegator delegator, String agreementId) throws GenericEntityException {
		Map<String, Object> resultValue = FastMap.newInstance();
		List<EntityCondition> listAllCondition = FastList.newInstance();
		listAllCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
		int countOrderOrdered = 0;
		int countOrderOrderedCompleted = 0;
		BigDecimal grandTotal = BigDecimal.ZERO;
		BigDecimal grandTotalCompleted = BigDecimal.ZERO;
		List<GenericValue> listOrderHeader = delegator.findList("OrderHeader", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		if (UtilValidate.isNotEmpty(listOrderHeader)) {
			countOrderOrdered = listOrderHeader.size();
			for (GenericValue x : listOrderHeader) {
				grandTotal = grandTotal.add(x.getBigDecimal("grandTotal"));
			}
			
			List<GenericValue> listOrderHeaderCompleted = EntityUtil.filterByAnd(listOrderHeader, UtilMisc.toMap("statusId", "ORDER_COMPLETED"));
			if (UtilValidate.isNotEmpty(listOrderHeaderCompleted)) {
				countOrderOrderedCompleted = listOrderHeaderCompleted.size();
				for (GenericValue x : listOrderHeaderCompleted) {
					grandTotalCompleted = grandTotalCompleted.add(x.getBigDecimal("grandTotal"));
				}
			}
		}
		resultValue.put("grandTotal", grandTotal);
		resultValue.put("countOrderOrdered", countOrderOrdered);
		resultValue.put("grandTotalCompleted", grandTotalCompleted);
		resultValue.put("countOrderOrderedCompleted", countOrderOrderedCompleted);
		return resultValue;
	}

	public static BigDecimal getTotalTaxOrderItemPromo(Delegator delegator, String orderId) throws GenericEntityException{
		if (delegator == null || orderId == null) return null; 
		BigDecimal totalAmount = null;
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		List<EntityCondition> listConds = FastList.newInstance();
		listConds.add(EntityCondition.makeCondition("orderId", orderId));
		listConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
		listConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
		List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
		totalAmount = getTotalTaxOrderItemPromo(delegator, orderItems, orderHeader.getTimestamp("orderDate"));
		return totalAmount;
	}
	
	public static BigDecimal getTotalTaxOrderItemPromo(Delegator delegator, List<GenericValue> orderItems, Timestamp moment) throws GenericEntityException{
		if (UtilValidate.isEmpty(orderItems)) return null;
		
		BigDecimal totalAmount = BigDecimal.ZERO;
		//String productStoreId = orderHeader.getString("productStoreId"); phuc vu cho viec nhieu hon 1 tax_geo_id
		List<EntityCondition> condsAll = FastList.newInstance();
		condsAll.add(EntityUtil.getFilterByDateExpr(moment, "taxFromDate", "taxThruDate"));
		//condsAll.add(EntityUtil.getFilterByDateExpr(moment, "cateFromDate", "cateThruDate"));
		for (GenericValue orderItem : orderItems) {
			if ("ITEM_REJECTED".equals(orderItem.getString("statusId")) || "ITEM_CANCELLED".equals(orderItem.getString("statusId"))) continue;
			
			if ("Y".equals(orderItem.getString("isPromo"))) {
				EntityCondition condsItem = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", orderItem.get("productId")), 
						EntityOperator.AND, EntityCondition.makeCondition(condsAll, EntityOperator.AND)
					);
				GenericValue productTax = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRate", condsItem, null, null, null, false));
				if (productTax != null && UtilValidate.isNotEmpty(productTax.get("taxPercentage"))) {
					if (UtilValidate.isNotEmpty(orderItem.get("alternativeQuantity")) && UtilValidate.isNotEmpty(orderItem.get("alternativeUnitPrice"))) {
						totalAmount = totalAmount.add(orderItem.getBigDecimal("alternativeQuantity").multiply(orderItem.getBigDecimal("alternativeUnitPrice")).multiply(productTax.getBigDecimal("taxPercentage")).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding));
					} else {
						totalAmount = totalAmount.add(orderItem.getBigDecimal("quantity").multiply(orderItem.getBigDecimal("unitPrice")).multiply(productTax.getBigDecimal("taxPercentage")).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding));
					}
				} else {
					GenericValue parentProduct = com.olbius.basesales.product.ProductWorker.getParentProduct(orderItem.getString("productId"), delegator, moment);
					if (parentProduct != null) {
						EntityCondition condsItem2 = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", parentProduct.get("productId")), 
								EntityOperator.AND, EntityCondition.makeCondition(condsAll, EntityOperator.AND)
							);
						GenericValue parentProductTax = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRate", condsItem2, null, null, null, false));
						if (parentProductTax != null && UtilValidate.isNotEmpty(parentProductTax.get("taxPercentage"))) {
							if (UtilValidate.isNotEmpty(orderItem.get("alternativeQuantity")) && UtilValidate.isNotEmpty(orderItem.get("alternativeUnitPrice"))) {
								totalAmount = totalAmount.add(orderItem.getBigDecimal("alternativeQuantity").multiply(orderItem.getBigDecimal("alternativeUnitPrice")).multiply(parentProductTax.getBigDecimal("taxPercentage")).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding));
							} else {
								totalAmount = totalAmount.add(orderItem.getBigDecimal("quantity").multiply(orderItem.getBigDecimal("unitPrice")).multiply(parentProductTax.getBigDecimal("taxPercentage")).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding));
							}
						}
					}
				}
			}
		}
		return totalAmount;
	}
	
	public static void updateOrderItemPrice(Delegator delegator, LocalDispatcher dispatcher, GenericValue orderHeader, GenericValue orderItem, GenericValue product, GenericValue parentProduct, 
				BigDecimal quantity, String quantityUomId, BigDecimal selectedAmount, List<GenericValue> orderItemPriceInfos) throws CartItemModifyException, GenericEntityException {
        // set basePrice using the calculateProductPrice service
        try {
            Map<String, Object> priceContext = FastMap.newInstance();

            GenericValue orderRoleCustomer = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderHeader.get("orderId"), "roleTypeId", "PLACING_CUSTOMER"), null, false));
            
            String partyId = orderRoleCustomer.getString("partyId");
            if (partyId != null) {
                priceContext.put("partyId", partyId);
            }
            // check alternative packaging
            String parentProductId = null;
            if (parentProduct != null) parentProductId = parentProduct.getString("productId");
            
            boolean isAlternativePacking = ProductWorker.isAlternativePacking(delegator, orderItem.getString("productId") , parentProductId);
            BigDecimal pieces = BigDecimal.ONE;
            if(isAlternativePacking && UtilValidate.isNotEmpty(parentProductId)){
                GenericValue originalProduct = parentProduct;
                if (originalProduct != null) pieces = new BigDecimal(originalProduct.getLong("piecesIncluded"));
                priceContext.put("product", originalProduct);
                parentProduct = null;
            }else{
                priceContext.put("product", product);
            }
            
            priceContext.put("quantity", quantity);
            if (selectedAmount == null) selectedAmount = BigDecimal.ZERO;
            priceContext.put("amount", selectedAmount);
            
            if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
                priceContext.put("currencyUomId", orderHeader.get("currencyUom"));
                Map<String, Object> priceResult = dispatcher.runSync("calculatePurchasePrice", priceContext);
                if (ServiceUtil.isError(priceResult)) {
                    throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
                }
                Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                if (!validPriceFound.booleanValue()) {
                    throw new CartItemModifyException("Could not find a valid price for the product with ID [" + product.getString("productId") + "] and supplier with ID [" + partyId + "], not adding to cart.");
                }
                
                if(isAlternativePacking){
                    orderItem.put("unitPrice", ((BigDecimal) priceResult.get("price")).divide(pieces, RoundingMode.HALF_UP));
                }else{
                	orderItem.put("unitPrice", ((BigDecimal) priceResult.get("price")));
                }
                
                orderItemPriceInfos = UtilGenerics.checkList(priceResult.get("orderItemPriceInfos"));
            } else {
            	/*if (product != null) {
                    String productStoreId = orderHeader.getString("productStoreId");
                    List<GenericValue> productSurvey = ProductStoreWorker.getProductSurveys(delegator, productStoreId, product.getString("productId"), "CART_ADD", parentProductId);
                    if (UtilValidate.isNotEmpty(productSurvey) && UtilValidate.isNotEmpty(attributes)) {
                        List<String> surveyResponses = UtilGenerics.checkList(attributes.get("surveyResponses"));
                        if (UtilValidate.isNotEmpty(surveyResponses)) {
                            for (String surveyResponseId : surveyResponses) {
                                // TODO: implement multiple survey per product
                                if (UtilValidate.isNotEmpty(surveyResponseId)) {
                                    priceContext.put("surveyResponseId", surveyResponseId);
                                    break;
                                }
                            }
                        }
                    }
                }*/
                priceContext.put("currencyUomIdTo", orderHeader.get("currencyUom"));
                priceContext.put("prodCatalogId", null);
                priceContext.put("webSiteId", null);
                priceContext.put("productStoreId", orderHeader.getString("productStoreId"));
                priceContext.put("agreementId", null);
                priceContext.put("productPricePurposeId", "PURCHASE");
                priceContext.put("checkIncludeVat", "Y");

                // check if a survey is associated with the item and add to the price calculation
                /*List<String> surveyResponses = UtilGenerics.checkList(getAttribute("surveyResponses"));
                if (UtilValidate.isNotEmpty(surveyResponses)) {
                    priceContext.put("surveyResponseId", surveyResponses.get(0));
                }*/
                /* TODOCHANGE add new attribute: "quantityUomId"
                 * condition: check if has quantityUomId then:
                 * value 0: alternativeUnitPrice = get product price by quantityUomId input
                 * value 1: unitPrice = get product price by quantityUomId default
                 * else: normal process */
                Map<String, Object> priceResult = null;
                Map<String, Object> priceResultAlternative = null;
                if (quantityUomId != null) {
                	Map<String, Object> priceContextAlternative = FastMap.newInstance();
                	priceContextAlternative.putAll(priceContext);
                	String quantityUomIdDefault = product.getString("quantityUomId");
                	if (quantityUomIdDefault != null) {
                		priceContext.put("quantityUomId", quantityUomIdDefault);
                    	priceContext.put("termUomId", quantityUomIdDefault);
                    	priceResult = dispatcher.runSync("calculateProductPriceCustom", priceContext);
                	} else {
                		priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                	}
                	priceContextAlternative.put("quantityUomId", quantityUomId);
                	priceContextAlternative.put("termUomId", quantityUomId);
                	priceResultAlternative = dispatcher.runSync("calculateProductPriceCustom", priceContextAlternative);
                	if (ServiceUtil.isError(priceResultAlternative)) {
                		//alternativeUnitPrice
                		throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResultAlternative));
                	}
                } else {
                	// Old code
                	priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                }
                
                if (ServiceUtil.isError(priceResult)) {
                    throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(priceResult));
                }

                Boolean validPriceFound = (Boolean) priceResult.get("validPriceFound");
                if (Boolean.FALSE.equals(validPriceFound)) {
                    throw new CartItemModifyException("Could not find a valid price for the product with ID [" + product.getString("productId") + "], not adding to cart.");
                }
                
                //set alternative product price
                if(isAlternativePacking){
                    int decimals = 2;
                    if (priceResult.get("listPrice") != null) {
                        orderItem.set("unitListPrice", ((BigDecimal) priceResult.get("listPrice")).divide(pieces, decimals, RoundingMode.HALF_UP));
                    }

                    if (priceResult.get("basePrice") != null) {
                        orderItem.set("unitPrice", ((BigDecimal) priceResult.get("basePrice")).divide(pieces, decimals, RoundingMode.HALF_UP));
                    }

                    if (priceResult.get("price") != null) {
                        //this.setDisplayPrice(((BigDecimal) priceResult.get("price")).divide(pieces, decimals, RoundingMode.HALF_UP));
                    }

                    if (priceResult.get("specialPromoPrice") != null) {
                        //this.setSpecialPromoPrice(((BigDecimal) priceResult.get("specialPromoPrice")).divide(pieces, decimals, RoundingMode.HALF_UP));
                    }
                } else {
                    if (priceResult.get("listPrice") != null) {
                        orderItem.set("unitListPrice", ((BigDecimal) priceResult.get("listPrice")));
                    }

                    if (priceResult.get("basePrice") != null) {
                        orderItem.set("unitPrice", ((BigDecimal) priceResult.get("basePrice")));
                    }

                    if (priceResult.get("price") != null) {
                       // this.setDisplayPrice(((BigDecimal) priceResult.get("price")));
                    }
                    
                    // this.setSpecialPromoPrice((BigDecimal) priceResult.get("specialPromoPrice"));
                    
                    // TODOCHANGE quantityUomId
                    if (priceResultAlternative != null && priceResultAlternative.get("basePrice") != null) {
                    	orderItem.set("alternativeUnitPrice", priceResultAlternative.get("basePrice"));
                    }
                    /*if (priceResultAlternative.get("price") != null) {
                    	this.setAttribute("alternativeUnitPrice", priceResultAlternative.get("price"));
                    } else if (priceResultAlternative.get("listPrice") != null) {
                    	this.setAttribute("alternativeUnitPrice", priceResultAlternative.get("listPrice"));
                    } else {
                    	this.setAttribute("alternativeUnitPrice", priceResultAlternative.get("specialPromoPrice"));
                    }*/
                }
                
                orderItemPriceInfos = UtilGenerics.checkList(priceResult.get("orderItemPriceInfos"));

                // If product is configurable, the price is taken from the configWrapper.
                /*if (configWrapper != null) {
                    // TODO: for configurable products need to do something to make them VAT aware... for now base and display prices are the same
                    this.setBasePrice(configWrapper.getTotalPrice());
                    // Check if price display with taxes
                    GenericValue productStore = ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator);
                    if (UtilValidate.isNotEmpty(productStore) && "Y".equals(productStore.get("showPricesWithVatTax"))) {
                        BigDecimal totalPrice = configWrapper.getTotalPrice();
                        // Get Taxes
                        Map<String, Object> totalPriceWithTaxMap = dispatcher.runSync("calcTaxForDisplay", UtilMisc.toMap("basePrice", totalPrice, "productId", this.productId, "productStoreId", cart.getProductStoreId()));
                        this.setDisplayPrice((BigDecimal) totalPriceWithTaxMap.get("priceWithTax"));
                    } else {
                        this.setDisplayPrice(configWrapper.getTotalPrice());
                    }
                }*/

                // no try to do a recurring price calculation; not all products have recurring prices so may be null
                Map<String, Object> recurringPriceContext = FastMap.newInstance();
                recurringPriceContext.putAll(priceContext);
                // TODOCHANGE remove quantityUomId in Map
                if (quantityUomId != null) {
                	recurringPriceContext.remove("quantityUomId");
                }
                /*Map<String, Object> recurringPriceResult = null;
                if (quantityUomId != null) {
                	recurringPriceResult = dispatcher.runSync("calculateProductPriceCustom", recurringPriceContext);
                } else {
                	recurringPriceResult = dispatcher.runSync("calculateProductPrice", recurringPriceContext);
                }*/
                
                /*recurringPriceContext.put("productPricePurposeId", "RECURRING_CHARGE");
                Map<String, Object> recurringPriceResult = dispatcher.runSync("calculateProductPrice", recurringPriceContext);
                if (ServiceUtil.isError(recurringPriceResult)) {
                    throw new CartItemModifyException("There was an error while calculating the price: " + ServiceUtil.getErrorMessage(recurringPriceResult));
                }*/

                // for the recurring price only set the values iff validPriceFound is true
                /*Boolean validRecurringPriceFound = (Boolean) recurringPriceResult.get("validPriceFound");
                if (Boolean.TRUE.equals(validRecurringPriceFound)) {
                    if (recurringPriceResult.get("basePrice") != null) {
                        this.setRecurringBasePrice((BigDecimal) recurringPriceResult.get("basePrice"));
                    }
                    if (recurringPriceResult.get("price") != null) {
                        this.setRecurringDisplayPrice((BigDecimal) recurringPriceResult.get("price"));
                    }
                }*/
            }
        } catch (GenericServiceException e) {
            throw new CartItemModifyException("There was an error while calculating the price", e);
        }
    }
	
	public static String updateStatusStartEdit(Delegator delegator, HttpServletRequest request, String orderId){
		try {
			GenericValue orderAttrIsEditing = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderId, "attrName", "isEditing"), false);
			if (orderAttrIsEditing != null) {
				orderAttrIsEditing.put("attrValue", "Y");
				delegator.store(orderAttrIsEditing);
			} else {
				orderAttrIsEditing = delegator.makeValue("OrderAttribute");
				orderAttrIsEditing.put("orderId", orderId);
				orderAttrIsEditing.put("attrName", "isEditing");
				orderAttrIsEditing.put("attrValue", "Y");
				delegator.create(orderAttrIsEditing);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when save status editing");
        	return "error";
		}
		return "success";
	}
	
	public static String updateStatusFinishEdit(Delegator delegator, HttpServletRequest request, String orderId){
		try {
			GenericValue orderAttrIsEditing = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderId, "attrName", "isEditing"), false);
			if (orderAttrIsEditing != null) {
				orderAttrIsEditing.put("attrValue", "N");
				delegator.store(orderAttrIsEditing);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", "Fatal occur when save status editing");
        	return "error";
		}
		return "success";
	}
	
	public static BigDecimal getTotalWeightProductOrderItem(Delegator delegator, LocalDispatcher dispatcher, String orderId, String orderItemSeqId) {
		BigDecimal sumWeight = BigDecimal.ZERO;
		try {
			GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
			sumWeight = getTotalWeightProductOrderItem(delegator, dispatcher, orderItem);
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage(), module);
        	return sumWeight;
		}
		return sumWeight;
	}
	public static BigDecimal getTotalWeightProductOrderItem(Delegator delegator, LocalDispatcher dispatcher, GenericValue orderItem) {
		return getTotalWeightProductOrderItem(delegator, dispatcher, orderItem, null, null);
	}
	public static BigDecimal getTotalWeightProductOrderItem(Delegator delegator, LocalDispatcher dispatcher, GenericValue orderItem, BigDecimal quantity, String productId) {
		BigDecimal sumWeight = BigDecimal.ZERO;
		if (orderItem == null && (UtilValidate.isEmpty(productId) || quantity == null)) return sumWeight;
		
		String weightUomIdTarget = "WT_kg";
		
		try {
			GenericValue product = null;
			BigDecimal weight = null;
			if (orderItem != null) {
				product = orderItem.getRelatedOne("Product", false);
				if (quantity == null) {
					quantity = orderItem.getBigDecimal("quantity");
					if (orderItem.getBigDecimal("cancelQuantity") != null) {
						quantity = quantity.subtract(orderItem.getBigDecimal("cancelQuantity"));
					}
				}
			} else {
				product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			}
			if (product != null) {
				weight = product.getBigDecimal("weight");
				if (weight != null) {
					String weightUomId = product.getString("weightUomId");
					if (weight != null && weightUomId != null && quantity != null) {
						weight = quantity.multiply(weight);
						Map<String, Object> resultConvertUom = dispatcher.runSync("convertUom", UtilMisc.<String, Object>toMap("originalValue", weight, "uomId", weightUomId, "uomIdTo", weightUomIdTarget));
						if (ServiceUtil.isSuccess(resultConvertUom)) {
							sumWeight = (BigDecimal) resultConvertUom.get("convertedValue");
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage(), module);
        	return sumWeight;
		} catch (GenericServiceException e) {
			Debug.logError(e, e.getMessage(), module);
	    	return sumWeight;
		}
		if (sumWeight == null) sumWeight = BigDecimal.ZERO;
		return sumWeight;
	}
	public static BigDecimal getTotalWeightProduct(Delegator delegator, LocalDispatcher dispatcher, String productId, BigDecimal quantity) {
		return getTotalWeightProductOrderItem(delegator, dispatcher, null, quantity, productId);
	}
	public static BigDecimal getTotalWeightProduct(Delegator delegator, LocalDispatcher dispatcher, String orderId) {
		BigDecimal sumWeight = BigDecimal.ZERO;
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.<String, Object>toMap("orderId", orderId), false);
			if (orderHeader != null) {
				List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", null, null, false);
				if (UtilValidate.isNotEmpty(orderItems)) {
					for (GenericValue orderItem : orderItems) {
						BigDecimal tmp = getTotalWeightProductOrderItem(delegator, dispatcher, orderItem);
						if (tmp != null) sumWeight = sumWeight.add(tmp);
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, e.getMessage(), module);
        	return sumWeight;
		}
		
		return sumWeight;
	}
	
	public static Map<String, Object> processDataEditOrderPreProcess(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue userLogin, GenericValue orderHeader, String orderId, List<Map<String, Object>> listProduct) {
    	Map<String, String> itemQtyMap = FastMap.newInstance();
		Map<String, String> itemExpireDateMap = FastMap.newInstance();
        Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
        Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
        Map<String, String> itemPriceMap = FastMap.newInstance();
        Map<String, String> overridePriceMap = FastMap.newInstance();
        
        for (Map<String, Object> prodItem : listProduct) {
        	String orderItemSeqId = (String) prodItem.get("orderItemSeqId");
        	String shipGroupSeqId = (String) prodItem.get("shipGroupSeqId");
        	String productId = (String) prodItem.get("productId");
        	String quantityUomId = (String) prodItem.get("quantityUomId");
        	BigDecimal quantity = (BigDecimal) prodItem.get("quantity");
        	String expireDateStr = (String) prodItem.get("expireDateStr");
        	
    		BigDecimal alternativeQuantity = null;
    		if (UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(productId) && quantity != null) {
	    		// Check quantityUomId with productQuotation
	    		BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
	    		GenericValue productItem = null;
	    		try {
	    			productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	    			if (productItem == null) {
	    				continue;
	    			}
	            } catch (Exception e) {
	                Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
	            }
	    		if (productItem.getString("quantityUomId") != null) {
	    			if (!quantityUomId.equals(productItem.getString("quantityUomId"))) {
	    				try {
	    					Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productItem.getString("productId"), "uomFromId", quantityUomId, "uomToId", productItem.getString("quantityUomId"), "userLogin", userLogin));
	    					if (ServiceUtil.isSuccess(resultValue)) {
	    						quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
	    					}
	    				} catch (Exception e) {
	    		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
	    		        }
	    			} else {
	    				quantityUomIdToDefault = BigDecimal.ONE;
	    			}
	    		}
    		
	    		try {
	                alternativeQuantity = new BigDecimal(quantity.doubleValue());
	                quantity = quantity.multiply(quantityUomIdToDefault);
	            } catch (Exception e) {
	                Debug.logWarning(e, "Problems parsing quantity string: " + quantity, module);
	                //quantity = BigDecimal.ONE;
	            }
	    		
	    		String qtyKey = orderItemSeqId + ":" + shipGroupSeqId;
	    		String quantity1 = "";
	    		String quantity2 = "";
	    		try {
	    			quantity1 = (String) ObjectType.simpleTypeConvert(quantity, "String", null, locale);
	    			quantity2 = (String) ObjectType.simpleTypeConvert(alternativeQuantity, "String", null, locale);
	    		} catch (GeneralException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		itemQtyMap.put(qtyKey, quantity1);
	    		itemExpireDateMap.put(orderItemSeqId, expireDateStr);
	    		itemAlternativeQtyMap.put(qtyKey, quantity2);
	    		itemQuantityUomIdMap.put(orderItemSeqId, quantityUomId);
    		}
        }
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("orderId", orderId);
		contextMap.put("itemQtyMap", itemQtyMap);
		contextMap.put("itemExpireDateMap", itemExpireDateMap);
		contextMap.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
		contextMap.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
		contextMap.put("itemPriceMap", itemPriceMap);
		contextMap.put("overridePriceMap", overridePriceMap);
		contextMap.put("userLogin", userLogin);
		contextMap.put("locale", locale);
		
		return contextMap;
    }
}
