import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.*;
import com.olbius.product.catalog.NewCatalogWorker;

List fillTree(rootCat ,CatLvl, parentCategoryId) {
    if(rootCat) {
        rootCat.sort{ it.productCategoryId }
        def listTree = FastList.newInstance();
        for(root in rootCat) {
            preCatChilds = delegator.findByAnd("ProductCategoryRollup", ["parentProductCategoryId": root.productCategoryId], null, true);
            catChilds = EntityUtil.getRelated("CurrentProductCategory",null,preCatChilds,true);
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
catalogAll = NewCatalogWorker.getStoreCatalogs(request);
context.catalogAll = catalogAll;
def cataCatList = FastList.newInstance();
count = 1;
for(ctl in catalogAll){
	def tmpCata = FastMap.newInstance();
	tmpCata["catalog"] = ctl;
	tmpList = CategoryWorker.getRelatedCategories(request, "topLevelList" + count, NewCatalogWorker.getCatalogTopCategoryId(request, ctl.prodCatalogId), true);
	// get browse_root
	curCategoryId = NewCatalogWorker.getCatalogTopCategoryId(request,ctl.prodCatalogId);
	if(curCategoryId == null || curCategoryId.isEmpty()){
		count++;
		continue;
	}
	request.setAttribute("curCategoryId", curCategoryId);
	CategoryWorker.setTrail(request, curCategoryId);

	categoryList = request.getAttribute("topLevelList" + count++);
	if (categoryList) {
	    catContentWrappers = FastMap.newInstance();
	    CategoryWorker.getCategoryContentWrappers(catContentWrappers, categoryList, request);
	    context.catContentWrappers = catContentWrappers;
	    completedTree = fillTree(categoryList,1,"");
	    tmpCata["completedTree"] = completedTree;
	}
	cataCatList.add(tmpCata);
}
context.cataCatList = cataCatList;
System.out.println("xxxxx" + cataCatList);