package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;

public class ProductWorker {
	public static final String module = ProductWorker.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static String RESOURCE_PROPERTIES = "basesales.properties";
    
    // scales and rounding modes for BigDecimal math
    public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
    public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static int salestaxCalcDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    
    public static BigDecimal calculatePriceBeforeTax(BigDecimal priceAfterTax, BigDecimal taxPercentage) {
		if (taxPercentage == null || priceAfterTax == null) return priceAfterTax;
		return priceAfterTax.multiply(PERCENT_SCALE).divide(taxPercentage.add(PERCENT_SCALE), salestaxCalcDecimals, salestaxRounding);
	}
	public static BigDecimal calculatePriceAfterTax(BigDecimal priceBeforeTax, BigDecimal taxPercentage) {
		if (taxPercentage == null || priceBeforeTax == null) return priceBeforeTax;
		return priceBeforeTax.multiply(taxPercentage.add(PERCENT_SCALE)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
	}
    
	public static List<String> getProductCatalogAll(Delegator delegator) {
		List<String> returnValue = new ArrayList<String>();
		try {
			List<GenericValue> prodCatalogs = delegator.findByAnd("ProdCatalog", null, null, false);
			if (prodCatalogs != null) {
				returnValue = EntityUtil.getFieldListFromEntityList(prodCatalogs, "prodCatalogId", true);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when getProductCatalogAll: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return returnValue;
	}
	
	public static List<String> getAllCategoryTree(Delegator delegator, String parentCategoryId) {
		return getAllCategoryTree(delegator, parentCategoryId, null);
	}
	public static List<String> getAllCategoryTree(Delegator delegator, String parentCategoryId, String productCategoryTypeId) {
		List<String> listCategories = FastList.newInstance();
		if (delegator == null) return listCategories;
		try {
			listCategories.add(parentCategoryId);
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("parentProductCategoryId", parentCategoryId));
			if (productCategoryTypeId != null) conds.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> preCatChilds = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conds), null, null, null, false);
			//List<GenericValue> catChilds = EntityUtil.getRelated("CurrentProductCategory", null, preCatChilds, false);
			if (preCatChilds != null) {
				for (GenericValue catChild : preCatChilds) {
					listCategories.addAll(getAllCategoryTree(delegator, catChild.getString("productCategoryId"), productCategoryTypeId));
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryTree service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}
	
	public static List<Map<String, Object>> getAllCategoryTreeMap(Delegator delegator, String categoryId, String parentCategoryId) {
		return getAllCategoryTreeMap(delegator, parentCategoryId, parentCategoryId, null);
	}
	public static List<Map<String, Object>> getAllCategoryTreeMap(Delegator delegator, String categoryId, String parentCategoryId, String productCategoryTypeId) {
		List<Map<String, Object>> listCategories = FastList.newInstance();
		if (delegator == null) return listCategories;
		try {
			GenericValue category = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId), false);
			if (category != null) {
				Map<String, Object> parentCategoryMap = category.getAllFields();
				parentCategoryMap.put("parentCategoryId", parentCategoryId);
				listCategories.add(parentCategoryMap);
				
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("parentProductCategoryId", categoryId));
				if (productCategoryTypeId != null) conds.add(EntityCondition.makeCondition("productCategoryTypeId", productCategoryTypeId));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> categoryChildIds = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conds), null, null, null, false);
				//List<GenericValue> catChilds = EntityUtil.getRelated("CurrentProductCategory", null, preCatChilds, false);
				if (categoryChildIds != null) {
					for (GenericValue catChild : categoryChildIds) {
						listCategories.addAll(getAllCategoryTreeMap(delegator, catChild.getString("productCategoryId"), categoryId, productCategoryTypeId));
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryTreeMap service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}
	
	public static List<Map<String, Object>> getAllCategoryTreeMapNoPa(Delegator delegator, String parentCategoryId) {
		List<Map<String, Object>> listCategories = FastList.newInstance();
		if (delegator == null || UtilValidate.isEmpty(parentCategoryId)) return listCategories;
		try {
			if (parentCategoryId != null) {
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("parentProductCategoryId", parentCategoryId));
				conds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> preCatChilds = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conds), null, null, null, false);
				//List<GenericValue> catChilds = EntityUtil.getRelated("CurrentProductCategory", null, preCatChilds, false);
				if (preCatChilds != null) {
					for (GenericValue catChild : preCatChilds) {
						Map<String, Object> categoryMap = catChild.getAllFields();
						categoryMap.put("parentCategoryId", parentCategoryId);
						listCategories.add(categoryMap);
						
						listCategories.addAll(getAllCategoryTreeMapNoPa(delegator, catChild.getString("productCategoryId")));
					}
					
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryTreeMapNoPa service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}
	
	public static List<String> getAllCategoryParentTree(Delegator delegator, String categoryId, Timestamp moment) {
		List<String> listCategories = FastList.newInstance();
		if (delegator == null) return listCategories;
		try {
			List<EntityCondition> mainConds = FastList.newInstance();
			mainConds.add(EntityCondition.makeCondition("productCategoryId", categoryId));
			if (moment != null) mainConds.add(EntityUtil.getFilterByDateExpr(moment));
			List<GenericValue> preCatParents = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(mainConds, EntityOperator.AND), null, UtilMisc.toList("sequenceNum"), null, false);
			List<GenericValue> catParents = EntityUtil.getRelated("ParentProductCategory", null, preCatParents, false);
			if (catParents != null) {
				for (GenericValue catParent : catParents) {
					listCategories.addAll(getAllCategoryParentTree(delegator, catParent.getString("productCategoryId"), moment));
				}
				listCategories.add(categoryId);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryParentTree service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}
	
	public static Set<String> getAllCategoryIdByProduct(Delegator delegator, String productId, Boolean checkParentProduct) throws GenericEntityException {
		return getAllCategoryIdByProduct(delegator, productId, checkParentProduct, null);
	}
	
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
	
	@Deprecated
	public static String convertCustomType(Delegator delegator, String partyId, String typeFrom, String typeTo, GenericValue userLogin, boolean checkSeller){
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		String returnValue = null;
		try {
			if ("CONTACT".equals(typeFrom) && "INDIVIDUAL".equals(typeTo)) {
				List<EntityCondition> listAllCondition = FastList.newInstance();
				String roleSeller = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.sell.in.store", delegator);
				if (checkSeller) {
					if (userLogin == null) return null;
					listAllCondition.add(EntityCondition.makeCondition("roleTypeId", roleSeller));
					listAllCondition.add(EntityCondition.makeCondition("partyId", userLogin.getString("partyId")));
				}
				String organization = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				listAllCondition.add(EntityCondition.makeCondition("payToPartyId", organization));
				listAllCondition.add(EntityCondition.makeCondition("salesMethodChannelEnumId", "BHKENH_BAN_LE"));
				List<GenericValue> listProductStoreRetail = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(listProductStoreRetail)) {
					return null;
				}
				String partyRelCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "party.rel.sales.rep.to.customer", delegator);
				String partyRelContactCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "party.rel.contact.customer", delegator);
				
				// Find and thru khach hang tiem nang
				String roleOrgToCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.org.to.customer", delegator);
				listAllCondition.clear();
				listAllCondition.add(EntityUtil.getFilterByDateExpr());
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				listAllCondition.add(EntityCondition.makeCondition("partyIdTo", organization));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.contact.customer", delegator)));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgToCustomer));
				listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelContactCustomer));
				List<GenericValue> listContactRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isNotEmpty(listContactRels)) {
					for (GenericValue contactRel : listContactRels) {
						contactRel.put("thruDate", nowTimestamp);
					}
					delegator.storeAll(listContactRels);
				}
				
				// Add khach hang ban le
				String roleIndividualCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.individual.customer", delegator);
				String roleCustomer = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "role.customer", delegator);
				GenericValue findPartyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleIndividualCustomer), false);
				if (findPartyRole == null) {
					delegator.create("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleIndividualCustomer));
				}
				/*listAllCondition.clear();
				listAllCondition.add(EntityUtil.getFilterByDateExpr());
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				listAllCondition.add(EntityCondition.makeCondition("partyIdTo", organization));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleCustomer));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgToCustomer));
				listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelCustomer));
				List<GenericValue> findListPartyRelCustomer = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(findListPartyRelCustomer)) {
					GenericValue partyRelCustomerNew = delegator.makeValue("PartyRelationship");
					partyRelCustomerNew.set("partyIdFrom", partyId);
					partyRelCustomerNew.set("partyIdTo", organization);
					partyRelCustomerNew.set("roleTypeIdFrom", roleCustomer);
					partyRelCustomerNew.set("roleTypeIdTo", roleOrgToCustomer);
					partyRelCustomerNew.set("fromDate", nowTimestamp);
					partyRelCustomerNew.set("partyRelationshipTypeId", partyRelCustomer);
					delegator.create(partyRelCustomerNew);
				}*/
				listAllCondition.clear();
				listAllCondition.add(EntityUtil.getFilterByDateExpr());
				listAllCondition.add(EntityCondition.makeCondition("partyIdFrom", partyId));
				listAllCondition.add(EntityCondition.makeCondition("partyIdTo", organization));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", roleIndividualCustomer));
				listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", roleOrgToCustomer));
				listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelCustomer));
				List<GenericValue> findListPartyRelIndividualCustomer = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(findListPartyRelIndividualCustomer)) {
					GenericValue partyRelCustomerNew = delegator.makeValue("PartyRelationship");
					partyRelCustomerNew.set("partyIdFrom", partyId);
					partyRelCustomerNew.set("partyIdTo", organization);
					partyRelCustomerNew.set("roleTypeIdFrom", roleIndividualCustomer);
					partyRelCustomerNew.set("roleTypeIdTo", roleOrgToCustomer);
					partyRelCustomerNew.set("fromDate", nowTimestamp);
					partyRelCustomerNew.set("partyRelationshipTypeId", partyRelCustomer);
					delegator.create(partyRelCustomerNew);
				}
				// find product store BAN_LE
				GenericValue productStoreRetail = EntityUtil.getFirst(listProductStoreRetail);
				if (UtilValidate.isNotEmpty(productStoreRetail)) {
					GenericValue productStoreRoleNew = delegator.makeValue("ProductStoreRole");
					productStoreRoleNew.set("productStoreId", productStoreRetail.get("productStoreId"));
					productStoreRoleNew.set("partyId", partyId);
					productStoreRoleNew.set("roleTypeId", roleCustomer);
					productStoreRoleNew.set("fromDate", nowTimestamp);
					delegator.create(productStoreRoleNew);
					
					returnValue = productStoreRetail.getString("productStoreId");
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error when convertCustomType: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return returnValue;
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
    
    /**
     * Get list children of product is rows of Product entity
     * @param productId
     * @param delegator
     * @param moment
     * @return
     */
    public static List<GenericValue> getChildrenProduct(String productId, Delegator delegator, Timestamp moment) {
		List<GenericValue> _childrenProduct = new ArrayList<GenericValue>();
		if (productId == null) {
			Debug.logWarning("Bad product id", module);
			return _childrenProduct;
		}
		
		try {
			if (moment == null) moment = UtilDateTime.nowTimestamp();
			EntityCondition condDateTime = EntityUtil.getFilterByDateExpr(moment, "fromDate", "thruDate");
			List<EntityCondition> condAll = new ArrayList<EntityCondition>();
			condAll.add(condDateTime);
			condAll.add(EntityCondition.makeCondition("productId", productId));
			condAll.add(EntityCondition.makeCondition("productAssocTypeId", "PRODUCT_VARIANT"));
			List<GenericValue> virtualProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
			//virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs);
			if (UtilValidate.isEmpty(virtualProductAssocs)) {
				//okay, not a variant, try a UNIQUE_ITEM
				condAll.clear();
				condAll.add(condDateTime);
				condAll.add(EntityCondition.makeCondition("productId", productId));
				condAll.add(EntityCondition.makeCondition("productAssocTypeId", "UNIQUE_ITEM"));
				virtualProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
				//virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs);
			}
			if (UtilValidate.isNotEmpty(virtualProductAssocs)) {
				//found one, set this first as the parent product
				for (GenericValue productAssoc : virtualProductAssocs) {
					_childrenProduct.add(productAssoc.getRelatedOne("AssocProduct", true));
				}
			}
		} catch (GenericEntityException e) {
			throw new RuntimeException("Entity Engine error getting Children Product (" + e.getMessage() + ")");
		}
		return _childrenProduct;
    }
	
    /**
     * Get list product children is rows of ProductAssoc entity
     * @param productId
     * @param delegator
     * @param moment
     * @return
     */
    public static List<GenericValue> getChildrenAssocProduct(String productId, Delegator delegator, Timestamp moment) {
		List<GenericValue> _childrenProduct = new ArrayList<GenericValue>();
		if (productId == null) {
			Debug.logWarning("Bad product id", module);
			return _childrenProduct;
		}
		
		try {
			if (moment == null) moment = UtilDateTime.nowTimestamp();
			EntityCondition condDateTime = EntityUtil.getFilterByDateExpr(moment, "fromDate", "thruDate");
			List<EntityCondition> condAll = new ArrayList<EntityCondition>();
			condAll.add(condDateTime);
			condAll.add(EntityCondition.makeCondition("productId", productId));
			condAll.add(EntityCondition.makeCondition("productAssocTypeId", "PRODUCT_VARIANT"));
			List<GenericValue> variantProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
			//variantProductAssocs = EntityUtil.filterByDate(variantProductAssocs);
			if (UtilValidate.isEmpty(variantProductAssocs)) {
				//okay, not a variant, try a UNIQUE_ITEM
				condAll.clear();
				condAll.add(condDateTime);
				condAll.add(EntityCondition.makeCondition("productId", productId));
				condAll.add(EntityCondition.makeCondition("productAssocTypeId", "UNIQUE_ITEM"));
				variantProductAssocs = delegator.findList("ProductAssoc", EntityCondition.makeCondition(condAll, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, true);
				//variantProductAssocs = EntityUtil.filterByDate(variantProductAssocs);
			}
			if (UtilValidate.isNotEmpty(variantProductAssocs)) {
				//found one, set this first as the parent product
				_childrenProduct = variantProductAssocs;
			}
		} catch (GenericEntityException e) {
			throw new RuntimeException("Entity Engine error getting Children Product (" + e.getMessage() + ")");
		}
		return _childrenProduct;
    }
    
    /*@Deprecated
	public static List<Map<String, Object>> getListProductByCatalogAndPeriod(Delegator delegator, 
			Locale locale, List<String> prodCatalogId, boolean activeIsNowTimestamp, 
			String customTimePeriodId, Timestamp introductionDateLimit, Timestamp releaseDateLimit) throws GenericEntityException{
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
    	
    	List<String> productCategoryIds = FastList.newInstance();
    	List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		if (prodCatalogIds != null) {
			for (String prodCatalogIdItem : prodCatalogIds) {
				List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
				if (listCategory != null) {
					for (GenericValue categoryItem : listCategory) {
						productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
					}
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(productCategoryIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
            mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            
            if (activeIsNowTimestamp) {
                mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            } else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
            	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
            	if (customTimePeriod != null) {
            		mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
            		mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
            	}
            }
            if (introductionDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
            }
            if (releaseDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
            }
            EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
            
            EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            List<GenericValue> listProduct = delegator.findList("ProductAndCategoryMember", mainCond, selectFields, null, opts, false);
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
            		GenericValue itemProduct = itemProd.getRelatedOne("Product", false);
            		if ("Y".equals(itemProduct.getString("isVirtual"))) {
            			String colorCode = null;
            			String attrName = EntityUtilProperties.getPropertyValue("basesales.properties", "productAttrName.displayColor", delegator);
            			if (UtilValidate.isNotEmpty(attrName)) {
            				List<GenericValue> productAttrs = itemProduct.getRelated("ProductAttribute", UtilMisc.toMap("attrName", attrName), null, true);
                			if (productAttrs != null && productAttrs.size() > 0) {
                				GenericValue productAttr = productAttrs.get(0);
                				if (productAttr != null) colorCode = productAttr.getString("attrValue");
                			}
            			}
            			Map<String, Object> itemProductProcessed = FastMap.newInstance();
            			itemProductProcessed = processGeneralProd(delegator, locale, itemProd, null, colorCode);
            			List<GenericValue> listVariantProductAssoc = SalesUtil.getVirtualVariantAssocs(itemProduct);
            			if (listVariantProductAssoc != null){
            				List<Map<String, Object>> listVariantTmp = FastList.newInstance();
            				for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
            					GenericValue itemVariantProduct = itemVariantProductAssoc.getRelatedOne("AssocProduct", false);
            					listVariantTmp.add(processGeneralProd(delegator, locale, itemVariantProduct, itemProduct.getString("productId"), colorCode));
            				}
            				itemProductProcessed.put("listProduct", listVariantTmp);
            			}
            			listIterator.add(itemProductProcessed);
            		} else {
            			listIterator.add(processGeneralProd(delegator, locale, itemProduct, null, null));
            		}
				}
            }
		}
		return listIterator;
	}*/
	
    /**
     * Current only used in get sales forecast content
     * @param parameters
     * @param delegator
     * @param locale
     * @param prodCatalogIds
     * @param activeIsNowTimestamp
     * @param customTimePeriodId
     * @param introductionDateLimit
     * @param releaseDateLimit
     * @return
     * @throws GenericEntityException
     */
	public static Map<String, Object> getPartListProductByCatalogAndPeriod(Map<String,String[]> parameters, List<String> listSortFields, 
			List<String> prodCatalogIds, boolean activeIsNowTimestamp, String customTimePeriodId, Timestamp introductionDateLimit, 
			Timestamp releaseDateLimit, Boolean hasVirtualProd, Delegator delegator, Locale locale) throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
			if (!hasVirtualProd) mainCondList.add(EntityCondition.makeCondition("isVirtual", "N"));
			
            //mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            
            if (activeIsNowTimestamp) {
                mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
            } else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
            	GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
            	if (customTimePeriod != null) {
            		mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
            		mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
            	}
            }
            if (introductionDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
            }
            if (releaseDateLimit != null) {
                mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
            }
            EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
            
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempDataDetail", mainCond, null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, result);
            
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
            		GenericValue itemProduct = delegator.findOne("Product", UtilMisc.toMap("productId", itemProd.getString("productId")), false);
        			listIterator.add(processGeneralProd(delegator, locale, itemProduct, null, null));
				}
            }
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	/*@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getListProductAndTaxByCatalogAndPeriod(Delegator delegator, 
			Locale locale, List<String> prodCatalogId, Timestamp introductionDateLimit, Timestamp releaseDateLimit, 
			List<EntityCondition> listAllConditions, List<String> listSortFields, Map<String,String[]> parameters, Map<String, Object> successResult) throws GenericEntityException, ParseException{
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		if (prodCatalogIds != null) {
			for (String prodCatalogIdItem : prodCatalogIds) {
				List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
				if (listCategory != null) {
					for (GenericValue categoryItem : listCategory) {
						productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
					}
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(productCategoryIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			
			List<EntityCondition> listCondThis = FastList.newInstance();
			listCondThis.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
			
			List<EntityCondition> listCondParent = FastList.newInstance();
			listCondParent.add(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.IN, prodCatalogIds));
			
			//if (activeIsNowTimestamp) {
			//	listCondThis.add(EntityUtil.getFilterByDateExpr());
			//	
				//listCondParent.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, null));
			//	listCondParent.add(EntityUtil.getFilterByDateExpr("parentFromDate", "parentThruDate"));
			//}
			
			mainCondList.add(EntityCondition.makeCondition(
					EntityCondition.makeCondition(listCondThis, EntityOperator.AND), EntityOperator.OR,
					EntityCondition.makeCondition(listCondParent)
				));
			
			//  customTimePeriodId
			//  condAnd1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
			//	condAnd1.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
			//
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			
			boolean sortFieldIsNull = false;
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields = UtilMisc.toList("productCode");
				sortFieldIsNull = true;
			}
			
			Map<String, Object> listConditionAfterProcess = SalesUtil.processSplitListAllCondition(delegator, listAllConditions, "ProductAndCategoryAndTaxRate");
			List<EntityCondition> listAllConditionOnIn = (List<EntityCondition>) listConditionAfterProcess.get("listAllConditionOnIn");
			List<Map<String, Object>> listMapConditionOutOf = (List<Map<String, Object>>) listConditionAfterProcess.get("listMapConditionOutOf");
			
			EntityCondition mainCond = EntityCondition.makeCondition(
					EntityCondition.makeCondition(mainCondList, EntityOperator.AND), EntityOperator.AND, 
					EntityCondition.makeCondition(listAllConditionOnIn, EntityOperator.AND)
				);
			
			List<GenericValue> listProduct = null;
			if (UtilValidate.isEmpty(listMapConditionOutOf) && sortFieldIsNull) {
				EntityFindOptions options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				EntityListIterator iterator = delegator.find("ProductAndCategoryAndTaxRate", mainCond, null, null, listSortFields, options);
				if (iterator != null) {
					listProduct = SalesUtil.processIterator(iterator, parameters, successResult);
				}
			} else {
				listProduct = delegator.findList("ProductAndCategoryAndTaxRate", mainCond, null, listSortFields, opts, false);
			}
			if (listProduct != null) {
				for (GenericValue itemProduct : listProduct) {
					listIterator.add(processGeneralProdAndTax(delegator, locale, itemProduct));
				}
			}
			
			if (UtilValidate.isNotEmpty(listMapConditionOutOf)) {
				listIterator = SalesUtil.filterMapFromMapCond(listIterator, listMapConditionOutOf);
			}
			if (UtilValidate.isNotEmpty(listSortFields)) {
				listIterator = SalesUtil.sortList(listIterator, listSortFields);
			}
		}
		return listIterator;
	}*/
	
	public static Map<String, Object> getListProductAndTaxByCatalogAndPeriod(Delegator delegator, 
			Locale locale, List<String> prodCatalogIds, boolean activeIsNowTimestamp, 
			String customTimePeriodId, Timestamp introductionDateLimit, Timestamp releaseDateLimit, 
			Map<String,String[]> parameters, List<String> listSortFields, List<EntityCondition> listAllConditions, LocalDispatcher dispatcher) throws GenericEntityException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("unitUomId");
            selectFields.add("unitPrice");
            selectFields.add("salesUomId");
            selectFields.add("salesPrice");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempDataMore", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, result);
            
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			itemMap.put("packingUomIds", getListQuantityUomIds(itemProd.getString("productId"), itemProd.getString("quantityUomId"), delegator, dispatcher));
        			listIterator.add(itemMap);
				}
            }
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	/*public static Map<String, Object> getListProductAndTaxByCatalogAndPeriod(Delegator delegator, 
			Locale locale, List<String> prodCatalogIds, boolean activeIsNowTimestamp, 
			String customTimePeriodId, Timestamp introductionDateLimit, Timestamp releaseDateLimit, 
			Map<String,String[]> parameters, List<String> listSortFields, List<EntityCondition> listAllConditions) throws GenericEntityException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("unitUomId");
            selectFields.add("unitPrice");
            selectFields.add("salesUomId");
            selectFields.add("salesPrice");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempDataMore", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, result);
            
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			itemMap.put("packingUomIds", getListQuantityUomIds(itemProd.getString("productId"), itemProd.getString("quantityUomId"), delegator));
        			listIterator.add(itemMap);
				}
            }
		}
		result.put("listIterator", listIterator);
		return result;
	}*/
	
	/*public static List<Map<String, Object>> getListProductAndTaxByCatalogAndPeriod(Delegator delegator, 
			Locale locale, List<String> prodCatalogId, boolean activeIsNowTimestamp, 
			String customTimePeriodId, Timestamp introductionDateLimit, Timestamp releaseDateLimit) throws GenericEntityException{
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		if (prodCatalogIds != null) {
			for (String prodCatalogIdItem : prodCatalogIds) {
				List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
				if (listCategory != null) {
					for (GenericValue categoryItem : listCategory) {
						productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
					}
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(productCategoryIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
			List<GenericValue> listProduct = delegator.findList("CategoryAndProductAndTaxRate", mainCond, selectFields, null, opts, false);
			if (listProduct != null) {
				for (GenericValue itemProduct : listProduct) {
					if ("Y".equals(itemProduct.getString("isVirtual"))) {
						Map<String, Object> parentProductMap = processGeneralProdAndTax(delegator, locale, itemProduct);
						listIterator.add(parentProductMap);
						
						List<GenericValue> listVariantProductAssoc = getChildrenAssocProduct(itemProduct.getString("productId"), delegator, nowTimestamp);
						if (listVariantProductAssoc != null){
							List<Map<String, Object>> listVariantTmp = FastList.newInstance();
							for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
								GenericValue itemVariantProduct = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", itemVariantProductAssoc.get("productIdTo")), null, false));
								if (UtilValidate.isNotEmpty(itemVariantProduct)) {
									listVariantTmp.add(processGeneralProdAndTax(delegator, locale, itemVariantProduct, itemProduct, true, (String) parentProductMap.get("colorCode")));
								} else {
									itemVariantProduct = EntityUtil.getFirst(delegator.findByAnd("Product", UtilMisc.toMap("productId", itemVariantProductAssoc.get("productIdTo")), null, false));
									Map<String, Object> itemMap = processGeneralProdAndTax(delegator, locale, itemVariantProduct, itemProduct, false, (String) parentProductMap.get("colorCode"));
									itemMap.put("taxPercentage", itemProduct.get("taxPercentage"));
									itemMap.put("taxAuthPartyId", itemProduct.get("taxAuthPartyId"));
									itemMap.put("taxAuthGeoId", itemProduct.get("taxAuthGeoId"));
									listVariantTmp.add(itemMap);
								}
							}
							listIterator.addAll(listVariantTmp);
						}
					} else {
						listIterator.add(processGeneralProdAndTax(delegator, locale, itemProduct));
					}
				}
			}
		}
		return listIterator;
	}*/
	
	private static Map<String, Object> processGeneralProd(Delegator delegator, Locale locale, GenericValue product, String parentProductId, String colorCode) throws GenericEntityException {
		return processGeneralProd(delegator, locale, product, parentProductId, colorCode, false, false);
	}
	
	public static Map<String, Object> processGeneralProd(Delegator delegator, Locale locale, GenericValue product, 
			String parentProductId, String colorCode, Boolean enableInternalNameSearch, Boolean enablePackingUomIds) throws GenericEntityException {
		return processGeneralProd(delegator, locale, product, parentProductId, null, colorCode, enableInternalNameSearch, enablePackingUomIds, false, false);
	}
	
	public static Map<String, Object> processGeneralProdAndTax(Delegator delegator, Locale locale, GenericValue product, GenericValue parentProduct, Boolean enableTax, String displayColor) throws GenericEntityException {
		if (product == null) return null;
		if (parentProduct == null) parentProduct = getParentProduct(product.getString("productId"), delegator, UtilDateTime.nowTimestamp());
		
		String colorCode = displayColor;
		if (colorCode == null) {
			String attrName = EntityUtilProperties.getPropertyValue(SalesUtil.RESOURCE_PROPERTIES, "productAttrName.displayColor", delegator);
			if (UtilValidate.isNotEmpty(attrName)) {
				GenericValue productAttr = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", product.get("productId"), "attrName", attrName), true);
				if (productAttr != null) {
					if (productAttr != null) colorCode = productAttr.getString("attrValue");
				}
			}
		}
		
		return processGeneralProd(delegator, locale, product, null, parentProduct, colorCode, false, true, enableTax, false);
	}
	
	public static Map<String, Object> processGeneralProdAndTax(Delegator delegator, Locale locale, GenericValue product) throws GenericEntityException {
		return processGeneralProdAndTax(delegator, locale, product, null, true, null);
	}
	
	public static Map<String, Object> processGeneralProdAndTax(Delegator delegator, Locale locale, GenericValue product, GenericValue parentProduct) throws GenericEntityException {
		return processGeneralProdAndTax(delegator, locale, product, parentProduct, true, null);
	}
	
	public static Map<String, Object> processGeneralProdAndTax(Delegator delegator, Locale locale, GenericValue product, String parentProductId, String colorCode, boolean getFullColumn) throws GenericEntityException {
		return processGeneralProd(delegator, locale, product, parentProductId, null, colorCode, false, false, true, getFullColumn);
	}
	
	public static Map<String, Object> processGeneralProd(Delegator delegator, Locale locale, GenericValue product, 
			String parentProductId, GenericValue parentProduct, String colorCode, 
			Boolean enableInternalNameSearch, Boolean enablePackingUomIds, Boolean enableTax, boolean getFullColumn) throws GenericEntityException {
		Map<String, Object> row = null;
		if (getFullColumn) {
			row = new HashMap<String, Object>(product);
		} else {
			row = new HashMap<String, Object>();
		}
		Map<String, Object> parentProductTmp = null;
		if (parentProduct == null && parentProductId != null) {
			parentProductTmp = getTaxCategoryInfo(delegator, parentProductId, null); //EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRateSimple", UtilMisc.toMap("productId", parentProductId), null, false));
		} else if (parentProduct != null) {
			parentProductTmp = getTaxCategoryInfo(delegator, parentProduct.getString("productId"), null); //parentProductTmp = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRateSimple", UtilMisc.toMap("productId", parentProduct.get("productId")), null, false));
		}
		if (parentProductTmp != null) {
			row.put("parentProductId", parentProductTmp.get("productId"));
			row.put("parentProductCode", parentProductTmp.get("productCode"));
		}
		row.put("productId", product.get("productId"));
		row.put("productName", product.get("productName"));
		row.put("internalName", product.get("internalName"));
		row.put("quantityUomId", product.getString("quantityUomId"));
		row.put("salesUomId", product.getString("salesUomId"));
		row.put("isVirtual", product.get("isVirtual"));
		row.put("isVariant", product.get("isVariant"));
		row.put("productCode", product.get("productCode"));
		row.put("requireAmount", product.get("requireAmount"));
		if (colorCode != null) row.put("colorCode", colorCode);
		
		// get field features "taste" and ... of product
		row.put("features", getFeatureProduct(delegator, product, locale));
		
		// get fields related to TAX
		if (enableTax) {
			if (UtilValidate.isNotEmpty(product.get("taxPercentage"))) {
				row.put("taxPercentage", product.get("taxPercentage"));
				row.put("taxAuthPartyId", product.get("taxAuthPartyId"));
				row.put("taxAuthGeoId", product.get("taxAuthGeoId"));
			} else if (parentProductTmp != null && parentProductTmp.containsKey("taxPercentage")) {
				row.put("taxPercentage", parentProductTmp.get("taxPercentage"));
				row.put("taxAuthPartyId", parentProductTmp.get("taxAuthPartyId"));
				row.put("taxAuthGeoId", parentProductTmp.get("taxAuthGeoId"));
			}
		}
		
		// get field internal name for search with format [code]internalName
		if (enableInternalNameSearch) {
			StringBuilder internalNameSearch = new StringBuilder();
			internalNameSearch.append("[");
			if (UtilValidate.isNotEmpty(product.get("productCode"))) {
				internalNameSearch.append(product.get("productCode"));
			} else {
				internalNameSearch.append(product.get("productId"));
			}
			internalNameSearch.append("]");
			internalNameSearch.append(" ");
			internalNameSearch.append(product.get("internalName"));
			row.put("internalNameSearch", internalNameSearch.toString());
		}
		
		// get field is list packing uom id of product // column: packingUomId
		if (enablePackingUomIds) {
			row.put("packingUomIds", getListQuantityUomIds(product.getString("productId"), product.getString("quantityUomId"), delegator));
		}
		
		return row;
	}
	
	public static List<Map<String, Object>> getListQuantityUomIds(String productId, String quantityUomId, Delegator delegator) throws GenericEntityException {
		return getListQuantityUomIds(productId, quantityUomId, delegator, null);
	}
	public static List<Map<String, Object>> getListQuantityUomIds(String productId, String quantityUomId, Delegator delegator, LocalDispatcher dispatcher) throws GenericEntityException{
		List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
		if (UtilValidate.isEmpty(quantityUomId)) {
			return listQuantityUomIdByProduct;
		}
		// get field is list packing uom id of product // column: packingUomId
		EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", quantityUomId));
		EntityCondition condsMain = EntityCondition.makeCondition(condsItem, EntityOperator.AND, EntityUtil.getFilterByDateExpr());
		EntityFindOptions optsItem = new EntityFindOptions();
		optsItem.setDistinct(true);
		List<GenericValue> listConfigPacking = FastList.newInstance();
		listConfigPacking.addAll(delegator.findList("ConfigPackingAndUom", condsMain, null, null, optsItem, false));
		Set<String> uomIds = FastSet.newInstance();
		BigDecimal quantityConvert = BigDecimal.ONE;
		for (GenericValue conPackItem : listConfigPacking) {
			String uomFromId = conPackItem.getString("uomFromId");
			if (uomIds.contains(uomFromId)) {
				continue;
			}
			uomIds.add(uomFromId);
			// Check by quantity uom
			quantityConvert = conPackItem.getBigDecimal("quantityConvert");
			if (quantityConvert == null) quantityConvert = BigDecimal.ONE;
			BigDecimal unitPrice = BigDecimal.ZERO;
			if (dispatcher != null) {
				try {
					Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", null, "quantityUomId", uomFromId);
					Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
		        	if (!ServiceUtil.isError(resultCalPrice)) {
		        		unitPrice = (BigDecimal) resultCalPrice.get("basePrice");
		        	}
				} catch (Exception e) {
					Debug.logWarning("Error when calculate price of uom: " + uomFromId, module);
				}
			}
        	
        	// add to result
			Map<String, Object> packingUomIdMap = FastMap.newInstance();
			packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
			packingUomIdMap.put("uomId", uomFromId);
			packingUomIdMap.put("quantityConvert", quantityConvert);
			packingUomIdMap.put("unitPriceConvert", unitPrice);
			listQuantityUomIdByProduct.add(packingUomIdMap);
		}
		if (!uomIds.contains(quantityUomId)) {
			GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
			if (quantityUom != null) {
				BigDecimal unitPrice = BigDecimal.ZERO;
				if (dispatcher != null) {
					try {
						Map<String, Object> calPriceCtx = UtilMisc.<String, Object>toMap("productId", productId, "productStoreId", null, "quantityUomId", quantityUomId);
						Map<String, Object> resultCalPrice = dispatcher.runSync("calculateProductPriceCustom", calPriceCtx);
			        	if (!ServiceUtil.isError(resultCalPrice)) {
			        		unitPrice = (BigDecimal) resultCalPrice.get("basePrice");
			        	}
					} catch (Exception e) {
						Debug.logWarning("Error when calculate price of uom: " + quantityUomId, module);
					}
				}
				
				Map<String, Object> packingUomIdMap = FastMap.newInstance();
				packingUomIdMap.put("description", quantityUom.getString("description"));
				packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
				packingUomIdMap.put("quantityConvert", BigDecimal.ONE);
				packingUomIdMap.put("unitPriceConvert", unitPrice);
				listQuantityUomIdByProduct.add(packingUomIdMap);
			}
		}
		return listQuantityUomIdByProduct;
	}
	
	public static String getFeatureProduct(Delegator delegator, GenericValue product, Locale locale) throws GenericEntityException{
		StringBuffer features = new StringBuffer();
		if ("Y".equals(product.getString("isVariant"))) {
			List<GenericValue> productFeaturesAppl = delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", product.get("productId"), "productFeatureApplTypeId", "STANDARD_FEATURE"), null, false);
			if (productFeaturesAppl != null) {
				Iterator<GenericValue> featureIterator = productFeaturesAppl.iterator();
				while (featureIterator.hasNext()) {
					GenericValue featureApplItem = featureIterator.next();
					GenericValue feature = featureApplItem.getRelatedOne("ProductFeature", true);
					features.append(feature.get("description", locale));
					if (featureIterator.hasNext()) {
						features.append(", ");
					}
				}
			}
		}
		return features.toString();
	}
	
	public static Map<String, Object> findProductPriceQuotation(Delegator delegator, LocalDispatcher dispatcher, Locale locale, 
			List<String> listSortFields, List<EntityCondition> listAllConditions, Map<String,String[]> parameters, 
			List<String> prodCatalogId, boolean activeIsNowTimestamp, String customTimePeriodId, Timestamp introductionDateLimit, 
			Timestamp releaseDateLimit, String productStoreId, String partyId, Boolean hasVirtualProd) throws GenericEntityException, GenericServiceException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		
		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("uomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("barcode");
            selectFields.add("currencyUomId");
            selectFields.add("salesDiscontinuationDate");
            selectFields.add("purchaseDiscontinuationDate");
            //selectFields.add("sequenceNum");
            if (UtilValidate.isEmpty(listSortFields)) {
            	listSortFields.add("productId");
            }
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            List<GenericValue> listProduct = EntityMiscUtil.processIteratorToList(parameters, result, delegator, "ProductAndCatalogTempAndUoms", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			
        			Map<String, Object> contextMapFind = UtilMisc.<String, Object>toMap(
        									"productId", itemProd.get("productId"),
				        					"productStoreId", productStoreId, "partyId", partyId, 
				        					"quantityUomId", itemProd.get("uomId"));
        			Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceGroup", contextMapFind);
        			if (ServiceUtil.isSuccess(resultValue)) {
        				BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
        				BigDecimal basePrice = (BigDecimal) resultValue.get("basePrice");
        				BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");
        				
        				itemMap.put("price", basePrice);
        				itemMap.put("unitListPrice", listPrice);
        				itemMap.put("priceVAT", calculatePriceAfterTax(basePrice, taxPercentage));
        				itemMap.put("unitListPriceVAT", calculatePriceAfterTax(listPrice, taxPercentage));
        			}
        			
        			listIterator.add(itemMap);
				}
            }
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String, Object> findProductPriceAndTax(Delegator delegator, LocalDispatcher dispatcher, Locale locale, 
			List<String> prodCatalogId, boolean activeIsNowTimestamp, String customTimePeriodId, Timestamp introductionDateLimit, 
			Timestamp releaseDateLimit, List<String> productStoreIds, String partyId, Boolean hasVirtualProd,
			Map<String,String[]> parameters, List<String> listSortFields, List<EntityCondition> listAllConditions) throws GenericEntityException, GenericServiceException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		String productStoreId = null;
		String taxAuthPartyId = null;
		String taxAuthGeoId = null;
		if (UtilValidate.isNotEmpty(productStoreIds)) {
			productStoreId = productStoreIds.get(0);
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (productStore != null) {
				taxAuthPartyId = productStore.getString("vatTaxAuthPartyId");
				taxAuthGeoId = productStore.getString("vatTaxAuthGeoId");
			}
		}
		
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		
		//List<String> productCategoryIds = FastList.newInstance();
		//if (prodCatalogIds != null) {
		//	for (String prodCatalogIdItem : prodCatalogIds) {
		//		List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
		//		if (listCategory != null) {
		//			for (GenericValue categoryItem : listCategory) {
		//				productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
		//			}
		//		}
		//	}
		//}
		
		if (UtilValidate.isNotEmpty(prodCatalogIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, prodCatalogIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			//if (taxAuthPartyId != null) mainCondList.add(EntityCondition.makeCondition("taxAuthPartyId", taxAuthPartyId));
			//if (taxAuthGeoId != null) mainCondList.add(EntityCondition.makeCondition("taxAuthGeoId", taxAuthGeoId));
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			listAllConditions.add(mainCond);
			
			//List<GenericValue> listProduct = delegator.findList("CategoryAndProductAndTaxRate", mainCond, selectFields, UtilMisc.toList("sequenceNum", "productCode"), opts, false);
			//if (listProduct != null) {
			//	Set<String> quantityUomIds = FastSet.newInstance();
			//	for (GenericValue itemProductTax : listProduct) {
			//		quantityUomIds.clear();
			//		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", itemProductTax.get("productId")), false);
			//		if (product.getString("quantityUomId") != null) quantityUomIds.add(product.getString("quantityUomId"));
			//		List<GenericValue> configPacking = delegator.findByAnd("ConfigPacking", UtilMisc.toMap("productId", product.get("productId")), null, false);
			//		if (UtilValidate.isNotEmpty(configPacking)) {
			//			for (GenericValue item : configPacking) {
			//				quantityUomIds.add(item.getString("uomFromId"));
			//				quantityUomIds.add(item.getString("uomToId"));
			//			}
			//		}
			//		if (UtilValidate.isNotEmpty(quantityUomIds)) {
			//			for (String quantityUomId : quantityUomIds) {
			//				Map<String, Object> item = findProductPriceAndTaxInner(itemProductTax, delegator, dispatcher, locale, productStoreId, taxAuthPartyId, nowTimestamp, hasVirtualProd, quantityUomId);
			//				if (item != null) {
			//					listIterator.add(item);
			//				}
			//			}
			//		} else {
			//			Map<String, Object> item = findProductPriceAndTaxInner(itemProductTax, delegator, dispatcher, locale, productStoreId, taxAuthPartyId, nowTimestamp, hasVirtualProd, null);
			//			if (item != null) {
			//				listIterator.add(item);
			//			}
			//		}
			//		
			//	}
			//}
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("uomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("barcode");
            //selectFields.add("sequenceNum");
            if (UtilValidate.isEmpty(listSortFields)) {
            	listSortFields.add("productId");
            }
            EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
            EntityListIterator iterator = delegator.find("ProductAndCatalogTempAndUoms", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
            List<GenericValue> listProduct = SalesUtil.processIterator(iterator, parameters, result);
            
            if (listProduct != null) {
            	for (GenericValue itemProd : listProduct) {
        			Map<String, Object> itemMap = itemProd.getAllFields();
        			
        			Map<String, Object> contextMapFind = UtilMisc.<String, Object>toMap("productId", itemProd.getString("productId"),
        					"productStoreId", productStoreId, "partyId", partyId, "quantityUomId", itemProd.getString("uomId"));
        			Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceGroup", contextMapFind);
        			if (ServiceUtil.isSuccess(resultValue)) {
        				BigDecimal price = (BigDecimal) resultValue.get("price");
        				BigDecimal listPrice = (BigDecimal) resultValue.get("listPrice");
        				itemMap.put("price", price);
        				itemMap.put("unitListPrice", listPrice);
        				
        				BigDecimal taxPercentage = itemProd.getBigDecimal("taxPercentage");
        				if (price != null && taxPercentage != null) {
    						BigDecimal priceVAT = price.multiply(taxPercentage.add(PERCENT_SCALE)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
    						itemMap.put("priceVAT", priceVAT);
        				}
        			}
        			
        			listIterator.add(itemMap);
				}
            }
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	/*public static List<Map<String, Object>> findProductPriceAndTax(Delegator delegator, LocalDispatcher dispatcher, Locale locale, List<String> prodCatalogId, boolean activeIsNowTimestamp, 
			String customTimePeriodId, Timestamp introductionDateLimit, Timestamp releaseDateLimit, List<String> productStoreIds, String partyId, Boolean hasVirtualProd) throws GenericEntityException, GenericServiceException{
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		
		String productStoreId = null;
		String taxAuthPartyId = null;
		String taxAuthGeoId = null;
		if (UtilValidate.isNotEmpty(productStoreIds)) {
			productStoreId = productStoreIds.get(0);
			
			GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
			if (productStore != null) {
				taxAuthPartyId = productStore.getString("vatTaxAuthPartyId");
				taxAuthGeoId = productStore.getString("vatTaxAuthGeoId");
			}
		}
		
		List<String> productCategoryIds = FastList.newInstance();
		List<String> prodCatalogIds = FastList.newInstance();
		if (UtilValidate.isNotEmpty(prodCatalogId)) {
			prodCatalogIds.addAll(prodCatalogId);
		}
		if (prodCatalogIds != null) {
			for (String prodCatalogIdItem : prodCatalogIds) {
				List<GenericValue> listCategory = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogIdItem, "PCCT_BROWSE_ROOT");
				if (listCategory != null) {
					for (GenericValue categoryItem : listCategory) {
						productCategoryIds.addAll(SalesUtil.getAllCategoryTree(delegator, categoryItem.getString("productCategoryId")));
					}
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(productCategoryIds)) {
			List<EntityCondition> mainCondList = FastList.newInstance();
			mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			
			if (activeIsNowTimestamp) {
				mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			} else if (UtilValidate.isNotEmpty(customTimePeriodId)) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				if (customTimePeriod != null) {
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(customTimePeriod.getDate("thruDate").getTime()))));
					mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime()))));
				}
			}
			if (introductionDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
			}
			if (releaseDateLimit != null) {
				mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
			}
			if (taxAuthPartyId != null) mainCondList.add(EntityCondition.makeCondition("taxAuthPartyId", taxAuthPartyId));
			if (taxAuthGeoId != null) mainCondList.add(EntityCondition.makeCondition("taxAuthGeoId", taxAuthGeoId));
			EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
			
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			Set<String> selectFields = FastSet.newInstance();
            selectFields.add("productId");
            selectFields.add("productCode");
            selectFields.add("internalName");
            selectFields.add("productName");
            selectFields.add("quantityUomId");
            selectFields.add("isVirtual");
            selectFields.add("isVariant");
            selectFields.add("taxPercentage");
            selectFields.add("taxAuthPartyId");
            selectFields.add("taxAuthGeoId");
            selectFields.add("sequenceNum");
			List<GenericValue> listProduct = delegator.findList("CategoryAndProductAndTaxRate", mainCond, selectFields, UtilMisc.toList("sequenceNum", "productCode"), opts, false);
			if (listProduct != null) {
				Set<String> quantityUomIds = FastSet.newInstance();
				for (GenericValue itemProductTax : listProduct) {
					quantityUomIds.clear();
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", itemProductTax.get("productId")), false);
					if (product.getString("quantityUomId") != null) quantityUomIds.add(product.getString("quantityUomId"));
					List<GenericValue> configPacking = delegator.findByAnd("ConfigPacking", UtilMisc.toMap("productId", product.get("productId")), null, false);
					if (UtilValidate.isNotEmpty(configPacking)) {
						for (GenericValue item : configPacking) {
							quantityUomIds.add(item.getString("uomFromId"));
							quantityUomIds.add(item.getString("uomToId"));
						}
					}
					if (UtilValidate.isNotEmpty(quantityUomIds)) {
						for (String quantityUomId : quantityUomIds) {
							Map<String, Object> item = findProductPriceAndTaxInner(itemProductTax, delegator, dispatcher, locale, productStoreId, taxAuthPartyId, nowTimestamp, hasVirtualProd, quantityUomId);
							if (item != null) {
								listIterator.add(item);
							}
						}
					} else {
						Map<String, Object> item = findProductPriceAndTaxInner(itemProductTax, delegator, dispatcher, locale, productStoreId, taxAuthPartyId, nowTimestamp, hasVirtualProd, null);
						if (item != null) {
							listIterator.add(item);
						}
					}
					
				}
			}
		}
		return listIterator;
	}*/
	
	/*private static Map<String, Object> findProductPriceAndTaxInner(GenericValue itemProductTax, Delegator delegator, LocalDispatcher dispatcher, Locale locale, 
			String productStoreId, String partyId, Timestamp nowTimestamp, Boolean hasVirtualProd, String quantityUomId) throws GenericEntityException, GenericServiceException {
		Map<String, Object> listIteratorItem = null;
		if ("Y".equals(itemProductTax.getString("isVirtual"))) {
			if (hasVirtualProd) {
				Map<String, Object> productPriceMap = getProductPriceMap(delegator, dispatcher, locale, null, null, itemProductTax.getString("productId"), quantityUomId, itemProductTax, productStoreId, partyId);
				if (productPriceMap != null) listIteratorItem = productPriceMap;
			}
			
			List<GenericValue> listVariantProductAssoc = getChildrenAssocProduct(itemProductTax.getString("productId"), delegator, nowTimestamp);
			if (listVariantProductAssoc != null){
				for (GenericValue itemVariantProductAssoc : listVariantProductAssoc){
					GenericValue itemVariantProductTax = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRate", UtilMisc.toMap("productId", itemVariantProductAssoc.get("productIdTo")), null, false));
					if (UtilValidate.isNotEmpty(itemVariantProductTax)) {
						Map<String, Object> productPriceMapVariant = getProductPriceMap(delegator, dispatcher, locale, null, itemProductTax, itemVariantProductTax.getString("productId"), quantityUomId, itemVariantProductTax, productStoreId, partyId);
						if (productPriceMapVariant != null) listIteratorItem = productPriceMapVariant;
					} else {
						Map<String, Object> productPriceMapVariant = getProductPriceMap(delegator, dispatcher, locale, null, itemProductTax, itemVariantProductAssoc.getString("productIdTo"), quantityUomId, null, productStoreId, partyId);
						if (productPriceMapVariant != null) listIteratorItem = productPriceMapVariant;
					}
				}
			}
		} else {
			Map<String, Object> productPriceMapVariant = getProductPriceMap(delegator, dispatcher, locale, null, null, itemProductTax.getString("productId"), quantityUomId, itemProductTax, productStoreId, partyId);
			if (productPriceMapVariant != null) listIteratorItem = productPriceMapVariant;
		}
		return listIteratorItem;
	}*/
	
	/*public static Map<String, Object> getProductPriceMap(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String parentProductId, GenericValue parentProduct, 
			String productId, String quantityUomId, GenericValue productTax, String productStoreId, String partyId) throws GenericEntityException, GenericServiceException{
		Map<String, Object> productPriceMap = FastMap.newInstance();
		
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		if (product == null) return productPriceMap;
		
		Map<String, Object> contextMapFind = UtilMisc.<String, Object>toMap(
				"product", product,
				"productStoreId", productStoreId,
				"partyId", partyId,
				"quantityUomId", quantityUomId
			);
		Map<String, Object> resultValue = dispatcher.runSync("calculateProductPriceGroup", contextMapFind);
		if (ServiceUtil.isSuccess(resultValue)) {
			BigDecimal price = (BigDecimal) resultValue.get("price");
			productPriceMap.put("price", price);
			
			if (parentProduct == null && UtilValidate.isNotEmpty(parentProductId)) parentProduct = delegator.findOne("Product", UtilMisc.toMap("productId", parentProductId), false);
			if (parentProduct == null) parentProduct = getParentProduct(product.getString("productId"), delegator, UtilDateTime.nowTimestamp());

			GenericValue parentProductTmp = null;
			if (parentProduct != null) {
				parentProductTmp = EntityUtil.getFirst(delegator.findByAnd("ProductAndTaxAuthorityRateSimple", UtilMisc.toMap("productId", parentProduct.get("productId")), null, false));
			}
			if (parentProductTmp != null) {
				productPriceMap.put("parentProductId", parentProductTmp.getString("productId"));
				productPriceMap.put("parentProductCode", parentProductTmp.getString("productCode"));
			}
			productPriceMap.put("productId", product.get("productId"));
			productPriceMap.put("productName", product.get("productName"));
			productPriceMap.put("internalName", product.get("internalName"));
			productPriceMap.put("quantityUomId", quantityUomId);
			productPriceMap.put("isVirtual", product.get("isVirtual"));
			productPriceMap.put("isVariant", product.get("isVariant"));
			productPriceMap.put("productCode", product.get("productCode"));
			
			// get field features "taste" and ... of product
			StringBuffer features = new StringBuffer();
			if ("Y".equals(product.getString("isVariant"))) {
				List<GenericValue> productFeaturesAppl = delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", product.get("productId"), "productFeatureApplTypeId", "STANDARD_FEATURE"), null, false);
				if (productFeaturesAppl != null) {
					Iterator<GenericValue> featureIterator = productFeaturesAppl.iterator();
					while (featureIterator.hasNext()) {
						GenericValue featureApplItem = featureIterator.next();
						GenericValue feature = featureApplItem.getRelatedOne("ProductFeature", true);
						features.append(feature.get("description", locale));
						if (featureIterator.hasNext()) {
							features.append(", ");
						}
					}
				}
			}
			productPriceMap.put("features", features.toString());
			
			// get fields related to TAX
			BigDecimal taxPercentage = null;
			if (productTax != null && UtilValidate.isNotEmpty(productTax.get("taxPercentage"))) {
				productPriceMap.put("taxPercentage", productTax.get("taxPercentage"));
				productPriceMap.put("taxAuthPartyId", productTax.get("taxAuthPartyId"));
				productPriceMap.put("taxAuthGeoId", productTax.get("taxAuthGeoId"));
				taxPercentage = productTax.getBigDecimal("taxPercentage");
			} else if (parentProductTmp != null && parentProduct.containsKey("taxPercentage")) {
				productPriceMap.put("taxPercentage", parentProductTmp.get("taxPercentage"));
				productPriceMap.put("taxAuthPartyId", parentProductTmp.get("taxAuthPartyId"));
				productPriceMap.put("taxAuthGeoId", parentProductTmp.get("taxAuthGeoId"));
				taxPercentage = parentProductTmp.getBigDecimal("taxPercentage");
			}
			if (taxPercentage != null) {
				BigDecimal priceVAT = price.multiply(taxPercentage.add(PERCENT_SCALE)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
				productPriceMap.put("priceVAT", priceVAT);
			}
			
			return productPriceMap;
		}
		
		return productPriceMap;
	}*/
	
	/*public static Map<String, Object> processProductInfoSimple(GenericValue product) {
		StringBuilder internalNameSearch = new StringBuilder();
		internalNameSearch.append("[");
		if (UtilValidate.isNotEmpty(product.get("productCode"))) {
			internalNameSearch.append(product.get("productCode"));
		} else {
			internalNameSearch.append(product.get("productId"));
		}
		internalNameSearch.append("]");
		internalNameSearch.append(" ");
		internalNameSearch.append(product.get("internalName"));
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("productId", product.get("productId"));
		row.put("productCode", product.get("productCode"));
		row.put("productName", product.get("productName"));
		row.put("internalName", product.get("internalName"));
		row.put("internalNameSearch", internalNameSearch.toString());
		row.put("quantityUomId", product.getString("quantityUomId"));
		row.put("isVirtual", product.get("isVirtual"));
		row.put("isVariant", product.get("isVariant"));
		
		return row;
	}*/
	
	public static List<GenericValue> getListProduct(Delegator delegator, List<String> productIds) {
		List<GenericValue> listProduct = new ArrayList<GenericValue>();
		
		try {
			listProduct = delegator.findList("Product", 
					EntityCondition.makeCondition(EntityCondition.makeCondition("isVirtual", "N"), 
							EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.IN, productIds)), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return listProduct;
	}
	
	public static BigDecimal getPriceProductUpc(GenericValue product, String upcId, Delegator delegator, Locale locale) throws GenericEntityException {
		BigDecimal priceBuy = ProductUtils.getPriceInUpcId(upcId, locale);
		if (priceBuy != null) {
			String currencyUomId = null;
			GenericValue upcGV = EntityUtil.getFirst(delegator.findByAnd("GoodIdentificationMeasure", UtilMisc.toMap("productId", product.get("productId"), "goodIdentificationTypeId", "UPCA", "idValue", upcId), null, false));
			if (upcGV != null) {
				currencyUomId = upcGV.getString("measureUomId");
			} else {
				GenericValue productPriceGV = EntityUtil.getFirst(delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", product.get("productId")), null, false));
				if (productPriceGV != null) {
					currencyUomId = productPriceGV.getString("currencyUomId");
				}
			}
			if (UtilValidate.isNotEmpty(currencyUomId)) {
				if ("VND".equals(currencyUomId)) {
					priceBuy = priceBuy.multiply(new BigDecimal(1000)); // default 90.8 -> 90800 VND
				}
			}
		}
		return priceBuy;
	}
	
	public static BigDecimal getAmountProductUpc(GenericValue product, String upcId, Delegator delegator, LocalDispatcher dispatcher, Locale locale) throws GenericServiceException, GenericEntityException {
		BigDecimal amount = null;
		
		BigDecimal weightBuy = ProductUtils.getWeightInUpcId(upcId, locale);
		if (weightBuy != null) {
			//Map<String, Object> priceCalcResult = dispatcher.runSync("calculateProductPriceCustom", 
			//		UtilMisc.toMap("product", product, "partyId", partyId, "productStoreId", productStoreId, "userLogin", userLogin, "locale", locale));
			//if (ServiceUtil.isError(priceCalcResult)) {
			//	return null;
			//}
			//BigDecimal pricePerUnit = (BigDecimal) priceCalcResult.get("price");
			
			GenericValue upcGV = EntityUtil.getFirst(delegator.findByAnd("GoodIdentificationMeasure", UtilMisc.toMap("productId", product.get("productId"), "goodIdentificationTypeId", "UPCA", "idValue", upcId), null, false));
			if (upcGV != null) {
				String amountUomTypeId = product.getString("amountUomTypeId");
				if ("WEIGHT_MEASURE".equals(amountUomTypeId)) {
					String weightUomIdBuy = upcGV.getString("measureUomId");
					String weightUomId = product.getString("weightUomId");
					BigDecimal productWeight = product.getBigDecimal("productWeight");
					if (weightUomIdBuy != null && !weightUomIdBuy.equals(weightUomId)) {
						BigDecimal weightConvert = null;
						Map<String, Object> resultValue = dispatcher.runSync("convertUom", UtilMisc.toMap("originalValue", weightBuy, "uomId", weightUomIdBuy, "uomIdTo", weightUomId));
						if (ServiceUtil.isSuccess(resultValue)) {
							weightConvert = (BigDecimal) resultValue.get("convertedValue");
						}
						if (weightConvert != null) {
							weightBuy = weightConvert;
						}
					}
					
					// multiply: productWeight -> pricePerUnit, weightBuy -> ? price
					//price = weightBuy.multiply(pricePerUnit).divide(productWeight, 2, RoundingMode.HALF_UP);
					amount = weightBuy.divide(productWeight, 2, RoundingMode.HALF_UP);
				}
			} else {
				BigDecimal productWeight = product.getBigDecimal("productWeight");
				if (productWeight != null) {
					amount = weightBuy.divide(productWeight, 2, RoundingMode.HALF_UP);
				}
			}
		}
		
		return amount;
	}
	
	/*public static BigDecimal getAmountProductEan(GenericValue product, String eanId, Delegator delegator, Locale locale) throws GenericServiceException, GenericEntityException {
		BigDecimal amount = null;
		
		// get DecimalsInWeight
		Integer decimalsInWeight = null;
		GenericValue decimalsInWeightGV = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "DecimalsInWeight"), false);
		if (decimalsInWeightGV != null) {
			String decimalsInWeightStr = decimalsInWeightGV.getString("systemValue");
			decimalsInWeight = Integer.parseInt(decimalsInWeightStr);
		}
		if (decimalsInWeight == null) decimalsInWeight = 0;
		BigDecimal weightBuy = ProductUtils.getWeightInEanId(eanId, locale, decimalsInWeight);
		if (weightBuy != null) {
			BigDecimal productWeight = product.getBigDecimal("productWeight");
			if (productWeight != null) {
				amount = weightBuy.divide(productWeight, decimalsInWeight, RoundingMode.HALF_UP);
			}
		}
		
		return amount;
	}*/
	
	/**
	 * Get amount (weight) of product by EAN code
	 * @param eanId
	 * @param delegator
	 * @param locale
	 * @return
	 * @throws GenericServiceException
	 * @throws GenericEntityException
	 */
	public static BigDecimal getAmountProductEan(String eanId, Delegator delegator, Locale locale) throws GenericEntityException {
		BigDecimal amount = null;
		
		// get DecimalsInWeight
		Integer decimalsInWeight = null;
		GenericValue decimalsInWeightGV = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", "DecimalsInWeight"), false);
		if (decimalsInWeightGV != null) {
			String decimalsInWeightStr = decimalsInWeightGV.getString("systemValue");
			decimalsInWeight = Integer.parseInt(decimalsInWeightStr);
		}
		if (decimalsInWeight == null) decimalsInWeight = 0;
		amount = ProductUtils.getWeightInEanId(eanId, locale, decimalsInWeight);
		/*BigDecimal weightBuy = ProductUtils.getWeightInEanId(eanId, locale, decimalsInWeight);
		if (weightBuy != null) {
			String pluCode = ProductUtils.getPluCodeInEanId(eanId);
			if (pluCode != null) {
				GenericValue pluCodeGV = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "PLU", "idValue", pluCode), null, false));
				if (pluCodeGV != null) {
					GenericValue product = pluCodeGV.getRelatedOne("Product", false);
					BigDecimal productWeight = product.getBigDecimal("productWeight");
					if (productWeight != null) {
						amount = weightBuy.divide(productWeight, decimalsInWeight, RoundingMode.HALF_UP);
					}
				}
			}
		}*/
		
		return amount;
	}
	
	public static BigDecimal calcPriceTaxDisplay(BigDecimal price, BigDecimal taxPercentage, String currencyUomId) throws GenericEntityException {
		BigDecimal priceTax = price;
		if (price != null && taxPercentage != null) {
			priceTax = taxPercentage.add(new BigDecimal(100)).divide(new BigDecimal(100)).multiply(price);
			int roundNum = 2;
			if ("VND".equals(currencyUomId)) roundNum = -2;
			priceTax = priceTax.setScale(roundNum, RoundingMode.HALF_UP);
		}
		return priceTax;
	}

	/**
	 * Get info of TAX category
	 * @param delegator
	 * @param productId
	 * @return <i>a Map<String, Object> with keys is:</i> <br/>- taxCategoryId: String <br/>- taxPercentage: BigDecimal
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> getTaxCategoryInfo(Delegator delegator, String productId, Timestamp moment) throws GenericEntityException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (moment == null) moment = UtilDateTime.nowTimestamp();
		
		String taxCategoryId = null;
		BigDecimal taxPercentage = null;
		List<EntityCondition> condsTax = FastList.newInstance();
		condsTax.add(EntityCondition.makeCondition("productId", productId));
		condsTax.add(EntityUtil.getFilterByDateExpr(moment));
		GenericValue taxCategoryGV = EntityUtil.getFirst(delegator.findList("ProductAndTaxAuthorityRateSimple", EntityCondition.makeCondition(condsTax), null, null, null, false));
		if (taxCategoryGV != null) {
			taxCategoryId = taxCategoryGV.getString("taxCategoryId");
			taxPercentage = taxCategoryGV.getBigDecimal("taxPercentage");
			
			resultMap.put("taxAuthPartyId", taxCategoryGV.get("taxAuthPartyId"));
			resultMap.put("taxAuthGeoId", taxCategoryGV.get("taxAuthGeoId"));
			resultMap.put("productId", taxCategoryGV.get("productId"));
			resultMap.put("productCode", taxCategoryGV.get("productCode"));
		}
		resultMap.put("taxCategoryId", taxCategoryId);
		resultMap.put("taxPercentage", taxPercentage);
		return resultMap;
	}
}
