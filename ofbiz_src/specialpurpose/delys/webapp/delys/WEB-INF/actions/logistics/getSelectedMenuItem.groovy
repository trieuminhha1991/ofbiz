import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", parameters.requirementId));
if (requirement != null){
	requirementTypeId = (String)requirement.get("requirementTypeId");
} else {
	requirementTypeId = parameters.requirementTypeId;
}

if ("SALES_REQ".equals(parameters.requirementTypeId) || "SALES_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "SalesRequirement";
}
if ("INTERNAL_SALES_REQ".equals(parameters.requirementTypeId) || "INTERNAL_SALES_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "InternalSalesRequirement";
}
if ("GIFT_REQ".equals(parameters.requirementTypeId) || "GIFT_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "GiftRequirement";
}
if ("CHANGE_DATE_REQ".equals(parameters.requirementTypeId) || "CHANGE_DATE_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "ChangeDateRequirement";
}
if ("MARKETING_REQ".equals(parameters.requirementTypeId) || "MARKETING_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "MarketingRequirement";
}
if ("TRANSFER_REQ".equals(parameters.requirementTypeId) || "TRANSFER_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "TransferRequirement";
}
if ("RECEIVE_PRODUCT_REQ".equals(parameters.requirementTypeId) || "RECEIVE_PRODUCT_REQ".equals(requirementTypeId)){
	context.selectedSubMenuItem = "ReceiveByProduct";
}
context.requirementTypeId = parameters.requirementTypeId;