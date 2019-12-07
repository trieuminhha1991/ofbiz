//import org.ofbiz.base.util.UtilMisc;
//import org.ofbiz.entity.condition.EntityCondition;
//import org.ofbiz.entity.util.EntityUtil;
//
//String lotId = (String)parameters.lotId;
//String productPlanId = (String)parameters.productPlanId;
//fields = ["agreementId"] as Set;
//String agreementId = "";
//List<GenericValue> listProductPlanAndOrderToAgree = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "lotId", lotId)), fields, null, null, false);
//GenericValue productPlanAndOrder = EntityUtil.getFirst(listProductPlanAndOrderToAgree);
//if(productPlanAndOrder != null){
//	agreementId = (String)productPlanAndOrder.get("agreementId");
//	GenericValue agreementAttr = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
//	context.agreementName =(String)agreementAttr.get("attrValue");
//}
//System.out.println ("AA:" +agreementId);
//context.agreementId = agreementId;