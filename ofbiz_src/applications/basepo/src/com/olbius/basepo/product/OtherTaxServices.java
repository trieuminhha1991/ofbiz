package com.olbius.basepo.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;

public class OtherTaxServices {
	public static final String MODULE = OtherTaxServices.class.getName();
	
	public static Map<String, Object> createOtherTax(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String taxType = (String) context.get("taxType");
		String description = (String) context.get("description");
		String productId = (String) context.get("productId");
		GenericValue enumration = null;
		try {
			enumration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", taxType) , false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp fromDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			String fromDateStr = (String)context.get("fromDate");
			if (UtilValidate.isNotEmpty(fromDateStr)) {
				fromDate = new Timestamp(new Long(fromDateStr));
			}
		}
		Timestamp thruDate = null;
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			String thruDateStr = (String)context.get("thruDate");
			if (UtilValidate.isNotEmpty(thruDateStr)) {
				thruDate = new Timestamp(new Long(thruDateStr));
			}
		}
		BigDecimal percent = BigDecimal.ZERO;
		if (context.containsKey("percent")){
			if (UtilValidate.isNotEmpty(context.get("percent"))) {
				percent = new BigDecimal((String)context.get("percent"));
			}
		}
		String percentTmp = ((String) context.get("percent")).replace(".", "");
		String taxId = null;
		String taxName = enumration.get("description") +" "+ percent+ "%";
		if (taxType.equals("TAX_IMP_CATEGORY")){
			taxId = "TAX_IMP_CATEGORY" + percentTmp;
		} else {
			taxId = "TAX_EXCISE_CATEGORY" + percentTmp;
		}
		
		// create product category
		GenericValue category = delegator.makeValue("ProductCategory");
		category.put("productCategoryId", taxId);
		category.put("productCategoryTypeId", taxType);
		category.put("categoryName", taxName);
		
		// create product category member
		GenericValue categoryMember = null;
		try {
			if (UtilValidate.isNotEmpty(delegator.findOne("ProductCategoryMember", UtilMisc.toMap("productCategoryId", taxId, "productId", productId, "fromDate", fromDate), false))){
				categoryMember = delegator.findOne("ProductCategoryMember", UtilMisc.toMap("productCategoryId", taxId, "productId", productId, "fromDate", fromDate), false);
			}
			else {
				categoryMember = delegator.makeValue("ProductCategoryMember");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		categoryMember.put("productCategoryId", taxId);
		categoryMember.put("productId", productId);
		categoryMember.put("fromDate", fromDate);
		categoryMember.put("thruDate", thruDate);
		
		// create tax authority
		GenericValue taxProduct = null;
		try {
			if (UtilValidate.isNotEmpty(delegator.findList("TaxAuthorityRateProduct", EntityCondition.makeCondition("productCategoryId", taxId), null, null, null, false))){
				taxProduct = delegator.findList("TaxAuthorityRateProduct", EntityCondition.makeCondition("productCategoryId", taxId), null, null, null, false).get(0);
			}
			else {
				taxProduct = delegator.makeValue("TaxAuthorityRateProduct");
				String rateType = null;
				if (taxType.equals("TAX_IMP_CATEGORY")){
					rateType = "IMPORT_TAX";
				} else {
					rateType = "EXCISE_TAX";
				}
				taxProduct.put("taxAuthorityRateSeqId", delegator.getNextSeqId("TaxAuthorityRateProduct"));
				taxProduct.put("taxAuthGeoId", SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id"));
				taxProduct.put("taxAuthPartyId", SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id"));
				taxProduct.put("taxAuthorityRateTypeId", rateType );
				taxProduct.put("productCategoryId", taxId);
				taxProduct.put("minItemPrice", BigDecimal.ZERO);
				taxProduct.put("minPurchase", BigDecimal.ZERO);
				taxProduct.put("taxShipping", "N");
				taxProduct.put("taxPercentage", percent);
				taxProduct.put("taxPromotions", "Y");
				taxProduct.put("fromDate", fromDate);
				taxProduct.put("thruDate", thruDate);
				taxProduct.put("description", description);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		
		
		try {
			delegator.createOrStore(category);
			delegator.createOrStore(categoryMember);
			delegator.createOrStore(taxProduct);
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
			return ServiceUtil.returnError(e1.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> jqGetListOtherTax(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	if (parameters.containsKey("productId")) {
			if (UtilValidate.isNotEmpty(parameters.get("productId")[0])) {
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", parameters.get("productId")[0])));
			}
		}
    	listAllConditions.add(EntityCondition.makeCondition(
                EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
                EntityOperator.OR,
                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())
           ));
    	try {    		
    		listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "ProductCategoryAndTax", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, null, opts);
    		
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	successResult.put("TotalRows", String.valueOf(listIterator.size()));
    	return successResult;
		
	}
	
	public static Map<String, Object> removeOtherTax(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String categoryId = (String) context.get("categoryId");
		String productId = (String) context.get("productId");
		Timestamp fromDate = null;
		if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
			String fromDateStr = (String)context.get("fromDate");
			if (UtilValidate.isNotEmpty(fromDateStr)) {
				fromDate = new Timestamp(new Long(fromDateStr));
			}
		}
		GenericValue categoryMember = null;
    	try {    		
    		categoryMember = delegator.findOne("ProductCategoryMember", UtilMisc.toMap("productCategoryId", categoryId, "productId", productId, "fromDate", fromDate), false);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling getListAssets service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	
    	if (UtilValidate.isNotEmpty(categoryMember)){
    		Timestamp thruDate = new Timestamp(System.currentTimeMillis());
    		categoryMember.put("thruDate", thruDate);
    		try {
				categoryMember.store();
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
    	}
    	return successResult;
		
	}

	
}