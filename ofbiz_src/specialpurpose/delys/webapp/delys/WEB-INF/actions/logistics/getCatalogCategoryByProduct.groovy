import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;

userLogin = session.getAttribute("userLogin");
partyId = userLogin.get('partyId');
context.partyId = partyId;

List<GenericValue> listCatalogs = new ArrayList<GenericValue>();
List<GenericValue> listCategories = new ArrayList<GenericValue>();
List<GenericValue> listCatalogTmp = new ArrayList<GenericValue>();
List<GenericValue> listCategoryTmp = new ArrayList<GenericValue>();

listCategoryTmp = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
if (!listCategoryTmp.isEmpty()){
	for (GenericValue member : listCategoryTmp){
		GenericValue category = delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId", (String)member.get("productCategoryId")));
		if (category != null){
			if (!listCategories.contains(category)){
				listCategories.add(category);
			}
		}
	}
}
if (!listCategories.isEmpty()){
	for (GenericValue category : listCategories){
		listCatalogTmp = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", (String)category.get("productCategoryId"))), null, null, null, false);
		if (!listCatalogTmp.isEmpty()){
			for (GenericValue cat : listCatalogTmp){
				GenericValue catalog = delegator.findOne("ProdCatalog", false, UtilMisc.toMap("prodCatalogId", (String)cat.get("prodCatalogId")));
				if (catalog != null) {
					if (!listCatalogs.contains(catalog)){
						listCatalogs.add(catalog);
					}
				}
			}
		}
	}
}
context.listCatalogs = listCatalogs;
context.listCategories = listCategories;