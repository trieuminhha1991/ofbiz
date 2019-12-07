import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductContentWrapper;

priceRuleConditions = EntityCondition.makeCondition([inputParamEnumId : "PRIP_PRODUCT_ID", productPriceActionTypeId : "PRICE_FLAT"], EntityOperator.AND);
priceRuleFindOptions = new EntityFindOptions();
priceRuleFindOptions.setDistinct(true);
productPriceRules = delegator.findList("ProductQuotationAndPriceRCA", priceRuleConditions, null, null, priceRuleFindOptions, false);
