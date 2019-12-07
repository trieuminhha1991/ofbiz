import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.category.NewCategoryWorker;

import com.olbius.product.product.ProductUtils;

List fillTree(rootCat ,CatLvl, parentCategoryId) {
	
    if(rootCat) {
        rootCat.sort{ it.sequenceNum }
        def listTree = FastList.newInstance();
        for(root in rootCat) {
        	List<EntityCondition> conditions = FastList.newInstance();
    		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
    		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", root.productCategoryId));
    		
    		preCatChilds = delegator.findList("ProductCategoryRollup",
    				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, true);
    		
            catChilds = EntityUtil.getRelated("CurrentProductCategory", null , preCatChilds, true);
            def childList = FastList.newInstance();
            // CatLvl uses for identify the Category level for display different css class
            if(catChilds) {
                if(CatLvl==2)
                    childList = fillTree(catChilds,CatLvl+1, parentCategoryId.replaceAll("/", "")+'/'+root.productCategoryId);
                    // replaceAll and '/' uses for fix bug in the breadcrum for href of category
                else if(CatLvl==1)
                    childList = fillTree(catChilds,CatLvl+1, parentCategoryId.replaceAll("/", "")+root.productCategoryId);
                else
                    childList = fillTree(catChilds,CatLvl+1, parentCategoryId+'/'+root.productCategoryId);
            }
            productsInCat  = delegator.findByAnd("ProductCategoryAndMember", ["productCategoryId": root.productCategoryId], null, true);
            // Display the category if this category containing products or contain the category that's containing products
            if(productsInCat || childList) {
                def rootMap = FastMap.newInstance();
                category = delegator.findOne("ProductCategory", ["productCategoryId": root.productCategoryId], true);
                rootMap["categoryName"] = category.categoryName;
                rootMap["categoryDescription"] = category.description;
                rootMap["categoryImageUrl"] = category.categoryImageUrl;
                rootMap["productCategoryId"] = root.productCategoryId;
                rootMap["parentCategoryId"] = parentCategoryId;
                rootMap["child"] = childList;
                listTree.add(rootMap);
            }
        }
        return listTree;
    }
}
// get all catalogs
//catalogName:Catalog, description:null, headerLogo:null, prodCatalogId:MainCatalog, sequenceNum:1
main = NewCatalogWorker.getStoreCatalogs(request);
count = 1;
if(UtilValidate.isNotEmpty(main)){
	mainCatalog = main[0].prodCatalogId;
	request.setAttribute("CURRENT_CATALOG_ID", mainCatalog);
//	${setRequestAttribute("CURRENT_CATALOG_ID", proCat.category.productCategoryId)}
	allCategory = ProductUtils.getCatalogTopCategories(request, mainCatalog);
	
//	allCategory = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition([parentProductCategoryId : topCatalog]), null, null, null, false);
	def cataCatList = FastList.newInstance();
	for(GenericValue e : allCategory){
		def tmpCata = FastMap.newInstance();
		curCategoryId = e.getString("productCategoryId");
		category = delegator.findOne("ProductCategory", [productCategoryId : curCategoryId], true);
		tmpCata["category"] = category;
		if(curCategoryId == null || curCategoryId.isEmpty()){
			count++;
			continue;
		}
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition("parentProductCategoryId", curCategoryId));
		categoryList = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, true);
		if (categoryList) {
			
			catContentWrappers = FastMap.newInstance();
			completedTree = fillTree(categoryList,1,"");
			tmpCata["completedTree"] = completedTree;
			tmpCata.topProduct = NewCategoryWorker.getCategoryProductTop(delegator, curCategoryId, 4, 0);
			cataCatList.add(tmpCata);
		} else {
			tmpCata["completedTree"] = e;
			tmpCata.topProduct = NewCategoryWorker.getCategoryProductTop(delegator, curCategoryId, 4, 0);
			cataCatList.add(tmpCata);
		}
	}
	context.cataCatList = cataCatList;
}