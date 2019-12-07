import org.ofbiz.base.util.UtilMisc;

String productPlanId = parameters.productPlanId;
GenericValue productPlanYear = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
String yearName = productPlanYear.getString("productPlanName");
context.yearName = yearName;