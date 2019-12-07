import java.util.*;
import org.ofbiz.base.util.*;

String partyId = userLogin.getString("partyId");
List<GenericValue> roleTypeList = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId), null, false);
Set<String> businessMenus = new HashSet<String>();
Set<String> roleTypeIds = new HashSet<String>();
for (roleType in roleTypeList) {
	String roleTypeId = roleType.getString("roleTypeId");
	roleTypeIds.add(roleTypeId);
	GenericValue roleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessMenu"), false);
	if (roleTypeAttr != null){
		businessMenus.add(roleTypeAttr.getString("attrValue"));
	}
}
if (businessMenus) {
	String menuName = businessMenus.iterator().next();
	switch (menuName) {
	case "DELYS_SALESADMIN_GT":
		context.selectedMenuItem = "product";
		context.selectedSubMenuItem = "listProduct";
		break;
	case "LOG_ADMIN":
		context.selectedMenuItem = "facility";
		context.selectedSubMenuItem = "Product";
		break;
	
	case "IMPORT_ADMIN":
		context.selectedMenuItem = "product";
		context.selectedSubMenuItem = "ListProduct";
		break;
		
	default:
		context.selectedMenuItem = "facility";
		context.selectedSubMenuItem = "Product";
		break;
	}
}

if(UtilValidate.isNotEmpty(parameters.productId)){
	def productId = parameters.productId;
	def fromDate = Timestamp.valueOf(parameters.fromDate);
	def productCategoryId = parameters.productCategoryId;
	GenericValue thisProduct = delegator.findOne("ProductAndProductCategoryMember", UtilMisc.toMap("productId", productId, "fromDate", fromDate, "productCategoryId", productCategoryId), false);
	context.thisProduct = thisProduct;
}