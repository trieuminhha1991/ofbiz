import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.webapp.taglib.*;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.category.CategoryWorker;

import com.olbius.product.category.NewCategoryWorker;

categoryId = parameters.catId;
catalogId = parameters.catalogId

if(!categoryId) {
	categoryId = request.getAttribute("productCategoryId") ?: parameters.category_id;
}

if(catalogId){
	ArrayList<Map<String, Object>> categoryRollUp = new ArrayList<Map<String, Object> >();
	cate = delegator.findOne("ProdCatalog", UtilMisc.toMap("prodCatalogId", catalogId), true);
	if(cate){
		context.catalog = cate;
	}
}else{
	if(!categoryId){
		categoryId = "";
	}
	if (categoryId != "") {
		//breadcrums
		categoryMap = [productCategoryId : categoryId];
		categoryTrailMap = dispatcher.runSync("getCategoryTrail", categoryMap);
		trail = categoryTrailMap.get("trail");
		ArrayList<Map<String, Object> > categoryRollUp = new ArrayList<Map<String, Object> >();
		for (String cateId : trail) {
			cate = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", cateId), true);
			if(cate){
				parentId = cate.getString("primaryParentCategoryId");
				if(parentId){
					categoryRollUp.add(cate);
				}
			}
		}
		if(categoryRollUp){
			context.categoryRollUp = categoryRollUp;
		}
	}else{
		//get current category include this product
		categoryMember = ProductWorker.getCurrentProductCategories(product);
		List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
		for(GenericValue cateMem : categoryMember){
			no = CategoryWorker.categoryRollupCount(cateMem);
			//			System.out.println("current category : " + cateMem.getString("productCategoryId") + "number: " + no);/
			categoryRollUp = null;
			if(no == 0){
				no2 = NewCategoryWorker.categoryRollupCountChildren(cateMem);
				if(no2 != 0){
					categoryMap = [productCategoryId : cateMem.getString("productCategoryId")];
					categoryTrailMap = dispatcher.runSync("getCategoryTrail", categoryMap);
					trail = categoryTrailMap.get("trail");
					ArrayList<Map<String, Object> > categoryRollUp = new ArrayList<Map<String, Object> >();
					for (String cateId : trail) {
						cate = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", cateId), true);
						if(cate){
							parentId = cate.getString("primaryParentCategoryId");
							if(parentId){
								categoryRollUp.add(cate);
							}
						}
					}
					context.categoryRollUp = categoryRollUp;
					break;
				}
			}else{
				categoryMap = [productCategoryId : cateMem.getString("productCategoryId")];
				categoryTrailMap = dispatcher.runSync("getCategoryTrail", categoryMap);
				trail = categoryTrailMap.get("trail");
				tempMap = FastMap.newInstance();
				tempMap.put("no", trail.size());
				tempMap.put("category", cateMem);
				tempMap.put("trail", trail);
				NewCategoryWorker.insertionSort(tempMap, temp);
				if(temp.size() != 0){
					Map<String, Object> categoryTrailMap = (Map<String, Object>)temp.get(0);
					List<String> trail = categoryTrailMap.get("trail");
					ArrayList<Map<String, Object> > categoryRollUp = new ArrayList<Map<String, Object> >();
					for (String cateId : trail) {
						cate = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", cateId), true);
						if(cate){
							parentId = cate.getString("primaryParentCategoryId");
							if(parentId){
								categoryRollUp.add(cate);
							}
						}
					}
					context.categoryRollUp = categoryRollUp;
				}
			}
		}

	}
}
