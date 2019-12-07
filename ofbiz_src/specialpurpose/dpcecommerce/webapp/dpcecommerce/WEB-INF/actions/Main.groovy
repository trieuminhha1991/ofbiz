import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.catalog.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.base.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;

import org.ofbiz.entity.util.EntityUtil;

productCategoryId = parameters.productCategoryId;
catalogId = CatalogWorker.getCurrentCatalogId(request);
promoCat = CatalogWorker.getProdCatalogCategories(request, catalogId, "PCCT_BEST_SELL");
List<String, Object> best = FastList.newInstance();
EntityListIterator total = null;
for(GenericValue e : promoCat){
	Map<String, Object> o = FastMap.newInstance();
	GenericValue c = delegator.findOne("ProductCategory", [productCategoryId : e.getString("productCategoryId")], false);
	total = delegator.find("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toList(
														EntityCondition.makeCondition("productCategoryId", e.getString("productCategoryId")),
														EntityUtil.getFilterByDateExpr()
													)), null, null, null, null);
	size = total.getResultsTotalSize();
	total.close();
	o.put("productCategoryId", c.getString("productCategoryId"));
	o.put("categoryName", c.getString("categoryName"));
	o.put("icon", c.getString("icon"));
	o.put("total", String.valueOf(size));
	best.add(o);
}
context.bestsellingcat = best;
if(UtilValidate.isEmpty(productCategoryId)){
	if(UtilValidate.isNotEmpty(promoCat)){
		productCategoryId = promoCat[0].productCategoryId;
		request.setAttribute("productCategoryId", productCategoryId);
	}
}else{
	flag = false;
	if(UtilValidate.isNotEmpty(promoCat)){
		for(GenericValue e : promoCat){
			if(productCategoryId.equals(e.getString("productCategoryId"))){
				productCategoryId = e.getString("productCategoryId");
				flag = true;
			}
		}
		if(!flag){
			productCategoryId = promoCat[0].productCategoryId;
		}
		request.setAttribute("productCategoryId", productCategoryId);
	}
}