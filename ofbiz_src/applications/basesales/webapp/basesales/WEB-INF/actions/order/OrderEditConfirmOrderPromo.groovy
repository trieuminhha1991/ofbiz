import java.util.Iterator;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilFormatOut;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.ProductPromoUseInfo;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilProperties;

ShoppingCart shoppingCart = session.getAttribute("shoppingCartUpdate");
context.shoppingCart = shoppingCart;
if (shoppingCart) {
	Iterator<ProductPromoUseInfo> productPromoUseInfoIter = shoppingCart.getProductPromoUseInfoIter();
	context.productPromoUseInfoIter = productPromoUseInfoIter;
	
	Set<String> productPromoIdsUse = new HashSet<String>();
	List<Map<String, Object>> productPromoUseInfos = FastList.newInstance();
	if (productPromoUseInfoIter) {
		while (productPromoUseInfoIter.hasNext()) {
			ProductPromoUseInfo productPromoUseInfo = productPromoUseInfoIter.next();
			
			if (productPromoIdsUse.contains(productPromoUseInfo.productPromoId)) {
				// gop cac promotion use lai
				for (Map<String, Object> productPromoUseInfoMapExisted : productPromoUseInfos) {
					if (productPromoUseInfoMapExisted.productPromoId == productPromoUseInfo.productPromoId) {
						if (productPromoUseInfo.totalDiscountAmount != 0) {
							totalDiscountAmountAll = productPromoUseInfo.totalDiscountAmount;
							if (productPromoUseInfoMapExisted.totalDiscountAmount != null) totalDiscountAmountAll = totalDiscountAmountAll + productPromoUseInfoMapExisted.totalDiscountAmount;
							productPromoUseInfoMapExisted.put("totalDiscountAmount", totalDiscountAmountAll);
						}
						if (productPromoUseInfo.quantityLeftInActions > 0) {
							quantityLeftInActionsAll = productPromoUseInfo.quantityLeftInActions;
							if (productPromoUseInfoMapExisted.quantityLeftInActions != null 
								&& (productPromoUseInfo.quantityLeftInActions > productPromoUseInfoMapExisted.quantityLeftInActions)){
								quantityLeftInActionsAll = productPromoUseInfoMapExisted.quantityLeftInActions;
							}
							productPromoUseInfoMapExisted.put("quantityLeftInActions", quantityLeftInActionsAll);
						}
					}
				}
				continue;
			} else {
				productPromoIdsUse.add(productPromoUseInfo.productPromoId);
			}
			
			Map<String, Object> productPromoUseInfoMap = FastMap.newInstance();
			
			if (productPromoUseInfo.productPromoId) productPromoUseInfoMap.put("productPromoId", productPromoUseInfo.productPromoId);
			if (productPromoUseInfo.productPromoCodeId) productPromoUseInfoMap.put("productPromoCodeId", productPromoUseInfo.productPromoCodeId);
			if (productPromoUseInfo.totalDiscountAmount != 0) productPromoUseInfoMap.put("totalDiscountAmount", productPromoUseInfo.totalDiscountAmount);
			if (productPromoUseInfo.quantityLeftInActions > 0) productPromoUseInfoMap.put("quantityLeftInActions", productPromoUseInfo.quantityLeftInActions);
			
			if (productPromoUseInfo.statusUsePerCondActual) {
				Map<String, Object> productPromoRuleMap = FastMap.newInstance();
				
				for (entry in productPromoUseInfo.statusUsePerCondActual.entrySet()){
					entityPK = entry.getKey();
					statusUse = entry.getValue();
					GenericValue productPromoAction = delegator.findOne(entityPK.getEntityName(), entityPK, false);
					Map<String, Object> productPromoRuleMapContent = FastMap.newInstance();
					Map<String, Object> productPromoActionMap = FastMap.newInstance();
					productPromoActionMap.put("entityPK", entityPK);
					productPromoActionMap.put("statusUse", statusUse);
					
					if (productPromoAction) {
						productPromoActionMap.put("productPromoId", productPromoAction.productPromoId);
						productPromoActionMap.put("productPromoRuleId", productPromoAction.productPromoRuleId);
						productPromoActionMap.put("productPromoActionSeqId", productPromoAction.productPromoActionSeqId);
						productPromoActionMap.put("productPromoActionEnumId", productPromoAction.productPromoActionEnumId);
						productPromoActionMap.put("orderAdjustmentTypeId", productPromoAction.orderAdjustmentTypeId);
						productPromoActionMap.put("serviceName", productPromoAction.serviceName);
						productPromoActionMap.put("quantity", productPromoAction.quantity);
						productPromoActionMap.put("amount", productPromoAction.amount);
						productPromoActionMap.put("productId", productPromoAction.productId);
						productPromoActionMap.put("partyId", productPromoAction.partyId);
						productPromoActionMap.put("useCartQuantity", productPromoAction.useCartQuantity);
						productPromoActionMap.put("operatorEnumId", productPromoAction.operatorEnumId);
						
						StringBuffer description = new StringBuffer();
						if (productPromoAction.productPromoActionEnumId) {
							GenericValue productPromoActionCurEnum = productPromoAction.getRelatedOne("ActionEnumeration", true);
							if (productPromoActionCurEnum) {
								description.append(productPromoActionCurEnum.get("description", locale));
							} else {
								description.append("[");
								description.append(productPromoAction.productPromoActionEnumId);
								description.append("]");
							}
						}
						description.append(". ");
						
						if (productPromoAction.quantity) {
							description.append(UtilProperties.getMessage("BaseSalesUiLabels", "BSQuantity", locale));
							description.append(": ");
							description.append(UtilFormatOut.formatQuantity(productPromoAction.quantity));
						}
						if (productPromoAction.amount) {
							description.append(UtilProperties.getMessage("BaseSalesUiLabels", "BSAmountOrPercent", locale));
							description.append(": ");
							description.append(UtilFormatOut.formatQuantity(productPromoAction.amount));
						}
						if (productPromoAction.productId) {
							description.append(UtilProperties.getMessage("BaseSalesUiLabels", "BSProductId", locale));
							description.append(": ");
							description.append(productPromoAction.productCode);
						}
						if (productPromoAction.partyId) {
							description.append(UtilProperties.getMessage("BaseSalesUiLabels", "BSPartyId", locale));
							description.append(": ");
							description.append(productPromoAction.partyId);
						}
						
						List<GenericValue> listProduct = delegator.findByAnd("ProductPromoProduct", entityPK, null, false);
						if (listProduct != null && listProduct.size() > 0) {
							description.append(" <br/> <div class='margin-left20 font-normal-span'>");
							for (int i = 0; i < listProduct.size(); i++) {
								GenericValue productItem = listProduct.get(i);
								GenericValue product = productItem.getRelatedOne("Product", true);
								if (product != null) {
									description.append("<div><i class='icon-caret-right blue'></i>");
									description.append(product.productCode);
									description.append(" - ");
									description.append(product.productName);
									description.append("</div>");
								}
							}
							description.append("</div>");
						}
						productPromoActionMap.put("description", description.toString());
						
						GenericValue productPromoRule = productPromoAction.getRelatedOne("ProductPromoRule", false);
						if (productPromoRule) {
							if (productPromoRuleMap.get(productPromoRule.productPromoRuleId) != null) {
								productPromoRuleMapContent = productPromoRuleMap.get(productPromoRule.productPromoRuleId);
								List<GenericValue> productPromoActions = productPromoRuleMapContent.get("productPromoActions");
								if (productPromoActions) {
									productPromoActions.add(productPromoActionMap);
								} else {
									productPromoRuleMapContent.put("productPromoActions", UtilMisc.toList(productPromoActionMap));
								}
							} else {
								productPromoRuleMapContent.put("productPromoRuleId", productPromoRule.productPromoRuleId);
								productPromoRuleMapContent.put("ruleName", productPromoRule.ruleName + " [" + productPromoRule.productPromoRuleId + "]");
								List<GenericValue> productPromoActions = productPromoRuleMapContent.get("productPromoActions");
								if (productPromoActions) {
									productPromoActions.add(productPromoActionMap);
								} else {
									productPromoRuleMapContent.put("productPromoActions", UtilMisc.toList(productPromoActionMap));
								}
								productPromoRuleMap.put(productPromoRule.productPromoRuleId, productPromoRuleMapContent);
							}
						}
					}
				}
				productPromoUseInfoMap.put("productPromoRules", productPromoRuleMap);
			}
			productPromoUseInfos.add(productPromoUseInfoMap);
		}
		context.productPromoUseInfos = productPromoUseInfos;
	}
}