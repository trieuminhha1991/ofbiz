package com.olbius.product.product;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

public class ProductWorker {
	public static final String module = ProductWorker.class.getName();
	
	public static Set<String> getAllCategoryIdByProduct(Delegator delegator, String productId, Boolean checkParentProduct, String productCategoryTypeId) throws GenericEntityException {
		Set<String> productCategoryIds = FastSet.newInstance();
		if (delegator == null || UtilValidate.isEmpty(productId)) return productCategoryIds;
		
		List<EntityCondition> conds = new ArrayList<EntityCondition>();
		conds.add(EntityCondition.makeCondition("productId", productId));
		conds.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
		conds.add(EntityUtil.getFilterByDateExpr());
		List<String> categoryIds1 = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", false);
		if (UtilValidate.isNotEmpty(categoryIds1)) {
			for (String categoryId : categoryIds1) {
				List<String> tmp = getAllCategoryParentTree(delegator, categoryId, null);
				if (UtilValidate.isNotEmpty(tmp)) {
					productCategoryIds.addAll(tmp);
				}
			}
		}
		
		if (checkParentProduct) {
			GenericValue parentProduct = getParentProduct(productId, delegator, null);
			if (parentProduct != null) {
				conds.clear();
				conds.add(EntityCondition.makeCondition("productId", parentProduct.getString("productId")));
				conds.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<String> categoryIds2 = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryMemberDetail", EntityCondition.makeCondition(conds), null, null, null, false), "productCategoryId", false);
				if (UtilValidate.isNotEmpty(categoryIds2)) {
					for (String categoryId : categoryIds2) {
						List<String> tmp = getAllCategoryParentTree(delegator, categoryId, null);
						if (UtilValidate.isNotEmpty(tmp)) {
							productCategoryIds.addAll(tmp);
						}
					}
				}
			}
		}
		
		return productCategoryIds;
	}
	
	public static List<String> getAllCategoryParentTree(Delegator delegator, String categoryId, Timestamp moment) {
		List<String> listCategories = FastList.newInstance();
		if (delegator == null) return listCategories;
		try {
			List<EntityCondition> mainConds = FastList.newInstance();
			mainConds.add(EntityCondition.makeCondition("productCategoryId", categoryId));
			mainConds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
			if (moment != null) mainConds.add(EntityUtil.getFilterByDateExpr(moment));
			List<GenericValue> preCatParents = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(mainConds, EntityOperator.AND), null, UtilMisc.toList("sequenceNum"), null, false);
			//List<GenericValue> catParents = EntityUtil.getRelated("ParentProductCategory", null, preCatParents, false);
			if (preCatParents != null) {
				for (GenericValue catParent : preCatParents) {
					listCategories.addAll(getAllCategoryParentTree(delegator, catParent.getString("parentProductCategoryId"), moment));
				}
				listCategories.add(categoryId);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryParentTree service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}
	
	//get parent product
    public static GenericValue getParentProduct(String productId, Delegator delegator, Timestamp moment) {
    	if (moment == null) {
    		return org.ofbiz.product.product.ProductWorker.getParentProduct(productId, delegator);
    	} else {
    		GenericValue _parentProduct = null;
            if (productId == null) {
                Debug.logWarning("Bad product id", module);
            }

            try {
            	EntityCondition condDateTime = EntityUtil.getFilterByDateExpr(moment, "fromDate", "thruDate");
            	List<EntityCondition> condAll = new ArrayList<EntityCondition>();
            	condAll.add(condDateTime);
            	condAll.add(EntityCondition.makeCondition("productIdTo", productId));
            	condAll.add(EntityCondition.makeCondition("productAssocTypeId", "PRODUCT_VARIANT"));
                List<GenericValue> virtualProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
                virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs);
                if (UtilValidate.isEmpty(virtualProductAssocs)) {
                    //okay, not a variant, try a UNIQUE_ITEM
                	condAll.clear();
                	condAll.add(condDateTime);
                	condAll.add(EntityCondition.makeCondition("productIdTo", productId));
                	condAll.add(EntityCondition.makeCondition("productAssocTypeId", "UNIQUE_ITEM"));
                    virtualProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
                    virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs);
                }
                if (UtilValidate.isNotEmpty(virtualProductAssocs)) {
                    //found one, set this first as the parent product
                    GenericValue productAssoc = EntityUtil.getFirst(virtualProductAssocs);
                    _parentProduct = productAssoc.getRelatedOne("MainProduct", true);
                }
            } catch (GenericEntityException e) {
                throw new RuntimeException("Entity Engine error getting Parent Product (" + e.getMessage() + ")");
            }
            return _parentProduct;
    	}
    }
    
	public static boolean isWeightProduct(GenericValue product) {
		if (product == null) return false;
		if ("FINISHED_GOOD".equals(product.getString("productTypeId")) 
				&& "WEIGHT_MEASURE".equals(product.getString("amountUomTypeId")) 
				&& UtilValidate.isNotEmpty(product.getString("weightUomId"))) {
			return true;
		}
		return false;
	}
	
	public static boolean isWeightProduct(Delegator delegator, String productId) throws GenericEntityException {
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		return isWeightProduct(product);
	}
}
