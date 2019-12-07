package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
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
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class ProductQuotationServices {
	public static final String module = ProductQuotationServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotation(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
    	try {
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-createDate");
    		}
    		boolean isViewAllStatus = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_NEW") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_APPROVE");
    		if (!isViewAllStatus) {
    			listAllConditions.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
    		}
    		listAllConditions.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "SALES_QUOTATION"));
	    	listIterator = delegator.find("ProductQuotationDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductQuotation service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
		successResult.put("listIterator", listIterator);
    	return successResult;
	}
    
    /*Create an Product Quotation*/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> createProductQuotation(DispatchContext ctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
        
        Security security = ctx.getSecurity();
        boolean hasPermissionTypeSales = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_NEW");
        boolean hasPermissionTypePurchase = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATIONPO_NEW");
        if (!hasPermissionTypeSales && !hasPermissionTypePurchase) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermission",locale));
        }
        String productQuotationModuleTypeId = (String) context.get("productQuotationModuleTypeId");
        if (!(hasPermissionTypeSales && "SALES_QUOTATION".equals(productQuotationModuleTypeId)) 
        		&& !(hasPermissionTypePurchase && "PURCHASE_QUOTATION".equals(productQuotationModuleTypeId))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotCreatePermissionQuotationWithType", UtilMisc.toList("productQuotationModuleTypeId", productQuotationModuleTypeId), locale));
        }
        
        String productQuotationId = (String) context.get("productQuotationId");
        String productQuotationTypeId = (String) context.get("productQuotationTypeId");
        String quotationName = (String) context.get("quotationName");
        String description = (String) context.get("description");
        String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
        String currencyUomId = (String) context.get("currencyUomId");
        String fromDateStr = (String) context.get("fromDate");
        String thruDateStr = (String) context.get("thruDate");
        String partyId = (String) context.get("partyId");
        //List<String> roleTypeIdsParam = (List<String>) context.get("roleTypeIds");
        List<String> productStoreIds = (List<String>) context.get("productStoreIds[]");
        List<String> productStoreIds2 = (List<String>) context.get("productStoreIds");
        List<String> productStoreGroupIds = (List<String>) context.get("productStoreGroupIds[]");
        String isSelectAllProductStore = (String) context.get("isSelectAllProductStore");
        //List<String> partyIds = (List<String>) context.get("partyIds");
        //List<Object> productListParam = (List<Object>) context.get("productList");
        String productListStr = (String) context.get("productList");
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }
        
        List<String> errorMessageList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(productQuotationId)) {
        	try {
	        	GenericValue productQuotationIdFind = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
	        	if (productQuotationIdFind != null) {
	        		errorMessageList.add(UtilProperties.getMessage(resource_error, "BSProductQuotationWasExistedWithIdIs", locale));
	        	}
        	} catch (Exception e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenQueryData", locale));
            }
        }
        if (UtilValidate.isEmpty(quotationName)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSQuotaionNameMustNotBeEmpty", locale));
        }
        /*if (UtilValidate.isEmpty(salesMethodChannelEnumId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSSalesChannelMustNotBeEmpty", locale));
        }*/
        if (UtilValidate.isEmpty(currencyUomId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSCurrencyUomIdMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(fromDate)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSFromDateMustNotBeEmpty", locale));
        }
        
        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        /*boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}*/
    	List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	List<Map<String, Object>> listCategory = new ArrayList<Map<String,Object>>();
    	/*if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";*/
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				if ("PROD_CAT_PRICE_FOD".equals(productQuotationTypeId)) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject prodItem = jsonArray.getJSONObject(i);
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("productCategoryId", prodItem.getString("productCategoryId"));
						productItem.put("amount", prodItem.getString("amount"));
						listCategory.add(productItem);
					}
				} else {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject prodItem = jsonArray.getJSONObject(i);
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("productId", prodItem.getString("productId"));
						productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						productItem.put("listPrice", prodItem.getString("listPrice"));
						if (prodItem.containsKey("listPriceVAT")) productItem.put("listPriceVAT", prodItem.getString("listPriceVAT"));
						if (prodItem.containsKey("taxPercentage")) productItem.put("taxPercentage", prodItem.getString("taxPercentage"));
						listProduct.add(productItem);
					}
				}
			}
    	/*} else {
    		listProduct = (List<Map<String, Object>>) context.get("productList");
    	}*/
		if (UtilValidate.isEmpty(listProduct) && UtilValidate.isEmpty(listCategory)) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
		}
		
        
        // begin create Quotation
        List<String> alertMessageList = FastList.newInstance();
        if (UtilValidate.isEmpty(productQuotationId)) {
        	productQuotationId = delegator.getNextSeqId("ProductQuotation");
        }
        
        GenericValue productQuotation = delegator.makeValue("ProductQuotation");
        //productQuotation.setNonPKFields(context);
        productQuotation.set("productQuotationId", productQuotationId);
        productQuotation.set("productQuotationTypeId", productQuotationTypeId);
        productQuotation.set("productQuotationModuleTypeId", productQuotationModuleTypeId);
        productQuotation.set("quotationName", quotationName);
        productQuotation.set("currencyUomId", currencyUomId);
        productQuotation.set("createDate", UtilDateTime.nowTimestamp());
        productQuotation.set("createdByUserLogin", userLogin.get("userLoginId"));
        productQuotation.set("fromDate", fromDate);
        productQuotation.set("thruDate", thruDate);
        productQuotation.set("description", description);
        productQuotation.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
        productQuotation.set("statusId", "QUOTATION_CREATED");
        // first try to create the ProductQuotation; if this does not fail, continue.
        try {
            delegator.create(productQuotation);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create ProductQuotation entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationCreationFailedPleaseNotifyCustomerService",locale));
        }
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        GenericValue productQuotationStatus = delegator.makeValue("ProductQuotationStatus");
        String quotationStatusId = delegator.getNextSeqId("ProductQuotationStatus");
        productQuotationStatus.put("quotationStatusId", quotationStatusId);
        productQuotationStatus.put("statusId", "QUOTATION_CREATED");
        productQuotationStatus.put("productQuotationId", productQuotationId);
        productQuotationStatus.put("statusDatetime", nowTimestamp);
        productQuotationStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
        // first try to create the ProductQuotation; if this does not fail, continue.
        try {
            delegator.create(productQuotationStatus);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create ProductQuotationStatus entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationStatusCreationFailedPleaseNotifyCustomerService",locale));
        }
        
        // create product quotation - product store apply
        List<GenericValue> storeApplTobeStored = new LinkedList<GenericValue>();
        if ("Y".equals(isSelectAllProductStore)) {
        	try {
        		List<GenericValue> listProductStore = null;
        		
	        	List<EntityCondition> listAllConditions = FastList.newInstance();
				List<String> listSortFields = FastList.newInstance();
				EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("pagesize", new String[]{"0"});
				
	        	Map<String, Object> contextCtx = FastMap.newInstance();
	        	contextCtx.put("listAllConditions", listAllConditions);
	        	contextCtx.put("listSortFields", listSortFields);
	        	contextCtx.put("opts", opts);
	        	contextCtx.put("parameters", parameters);
	        	contextCtx.put("userLogin", userLogin);
	        	contextCtx.put("locale", locale);
				
				Map<String, Object> resultMap = dispatcher.runSync("JQGetListProductStorePriceRule", contextCtx);
				Object resultList = resultMap.get("listIterator");
				//String totalRows = (String) resultMap.get("TotalRows");
				if (resultList != null) {
					if (resultList instanceof EntityListIterator) {
						EntityListIterator tmpList = (EntityListIterator) resultList;
						listProductStore = tmpList.getCompleteList();
					}
				}
				if (UtilValidate.isNotEmpty(listProductStore)) {
					Long sequenceNum = new Long(1);
					for (GenericValue productStore : listProductStore) {
						GenericValue storeAppl = delegator.makeValue("ProductQuotationStoreAppl");
	            		storeAppl.put("productQuotationId", productQuotationId);
	            		storeAppl.put("productStoreId", productStore.get("productStoreId"));
	            		storeAppl.put("fromDate", fromDate);
	            		storeAppl.put("sequenceNum", sequenceNum);
	            		storeApplTobeStored.add(storeAppl);
	            		sequenceNum++;
					}
				}
        	} catch (GenericServiceException | GenericEntityException e) {
        		Debug.logWarning(e, "Error when get list product store", module);
			}
        } else {
        	if (UtilValidate.isNotEmpty(productStoreIds)) {
            	Long sequenceNum = new Long(1);
            	for (String productStoreId : productStoreIds) {
            		GenericValue storeAppl = delegator.makeValue("ProductQuotationStoreAppl");
            		storeAppl.put("productQuotationId", productQuotationId);
            		storeAppl.put("productStoreId", productStoreId);
            		storeAppl.put("fromDate", fromDate);
            		storeAppl.put("sequenceNum", sequenceNum);
            		storeApplTobeStored.add(storeAppl);
            		sequenceNum++;
            	}
            } else if (UtilValidate.isNotEmpty(productStoreIds2)) {
            	Long sequenceNum = new Long(1);
            	for (String productStoreId : productStoreIds2) {
            		GenericValue storeAppl = delegator.makeValue("ProductQuotationStoreAppl");
            		storeAppl.put("productQuotationId", productQuotationId);
            		storeAppl.put("productStoreId", productStoreId);
            		storeAppl.put("fromDate", fromDate);
            		storeAppl.put("sequenceNum", sequenceNum);
            		storeApplTobeStored.add(storeAppl);
            		sequenceNum++;
            	}
            }
        }
        if (UtilValidate.isNotEmpty(storeApplTobeStored)) {
        	try {
        		delegator.storeAll(storeApplTobeStored);
        	} catch (GenericEntityException e) {
                Debug.logError(e, "Cannot create ProductQuotationStoreAppl entity; problems with insert", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
            }
        }
        
        // create product quotation - product store group apply
        List<GenericValue> storeGroupApplTobeStored = new LinkedList<GenericValue>();
    	if (UtilValidate.isNotEmpty(productStoreGroupIds)) {
    		Long sequenceNum = new Long(1);
    		for (String productStoreGroupId : productStoreGroupIds) {
    			GenericValue storeAppl = delegator.makeValue("ProductQuotationStoreGroupAppl");
    			storeAppl.put("productQuotationId", productQuotationId);
    			storeAppl.put("productStoreGroupId", productStoreGroupId);
    			storeAppl.put("fromDate", fromDate);
    			storeAppl.put("sequenceNum", sequenceNum);
    			storeGroupApplTobeStored.add(storeAppl);
    			sequenceNum++;
    		}
    	}
        if (UtilValidate.isNotEmpty(storeGroupApplTobeStored)) {
        	try {
        		delegator.storeAll(storeGroupApplTobeStored);
        	} catch (GenericEntityException e) {
        		Debug.logError(e, "Cannot create ProductQuotationStoreGroupAppl entity; problems with insert", module);
        		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
        	}
        }
        
        /*boolean isJsonRole = false;
    	if (UtilValidate.isNotEmpty(roleTypeIdsParam) && roleTypeIdsParam.size() > 0){
    		if (roleTypeIdsParam.get(0) instanceof String) isJsonRole = true;
    	}
    	List<String> roleTypeIds = new ArrayList<String>();
    	if (isJsonRole){
			String roleTypeIdsStr = "[" + (String) roleTypeIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(roleTypeIdsStr)) {
				jsonArray = JSONArray.fromObject(roleTypeIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					roleTypeIds.add(jsonArray.getString(i));
				}
			}
    	} else {
    		roleTypeIds = (List<String>) context.get("roleTypeIds");
    	}
        if (UtilValidate.isNotEmpty(roleTypeIds)) {
        	try {
            	List<GenericValue> tobeStore = new LinkedList<GenericValue>();
    	        for (String roleTypeId : roleTypeIds) {
    	        	GenericValue quotationRoleTypeAppl = delegator.makeValue("ProductQuotationRoleTypeAppl", 
    	        			UtilMisc.<String, Object>toMap("productQuotationId", productQuotationId, "roleTypeId", roleTypeId));
    	        	tobeStore.add(quotationRoleTypeAppl);
    	        }
    	        delegator.storeAll(tobeStore);
            } catch (GenericEntityException e) {
            	Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSPartyApplyCreationFailedPleaseNotifyCustomerService",locale));
            }
        }*/
		
    	if (UtilValidate.isNotEmpty(listProduct)) {
    		for (Map<String, Object> prodItem : listProduct) {
    			String productId = (String) prodItem.get("productId");
    			String quantityUomId = (String) prodItem.get("quantityUomId");
    			String listPriceStr = (String) prodItem.get("listPrice");
    			//String listPriceVATStr = (String) prodItem.get("listPriceVAT");
    			//String taxPercentage = (String) prodItem.get("taxPercentageStr");
    			//TODO
    			try {
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					if (product == null || UtilValidate.isEmpty(listPriceStr)) {
						if (product == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSProductHasIdIsNotExists", UtilMisc.toList(productId), locale));
						}
						if (UtilValidate.isEmpty(listPriceStr)) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSListPriceOfProductHasIdIsMustNotBeEmpty", UtilMisc.toList(productId), locale));
		    			}
						if (alertMessageList.size() > 0) {
							successResult.put(ModelService.ERROR_MESSAGE_LIST, alertMessageList);
						}
		        	} else {
    					// get list product pricing rules of quotation
    					/* check product is exists in quotation running
    					List<GenericValue> priceRulesOfProduct = delegator.findByAnd("ProductQuotationAndPriceRCA", 
    							UtilMisc.<String, Object>toMap("productQuotationId", productQuotationId, "productId", productId, 
    									"inputParamEnumId", "PRIP_PRODUCT_ID", "productPriceActionTypeId", "PRICE_FLAT", "quantityUomId", quantityUomId), null, false);
    					if (UtilValidate.isNotEmpty(priceRulesOfProduct)) {
    						return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductParamIsAlreadyExistsInQuotationParam", UtilMisc.toMap("productId", productId, "productQuotationId", priceRulesOfProduct.get(0).get("productQuotationId")), locale));
    					}*/
    					
    					/* parameters: productQuotationId productId inputCategory priceToDist priceToMarket priceToConsumer
    					 * productQuotationSelected: productQuotationId *, quotationName, description, currencyUomId, fromDate Timestamp */
    					Map<String, Object> resultValue = createQuotationRuleAdvance(delegator, productQuotationId, description, quotationName, productId, quantityUomId, listPriceStr, partyId);
    					if (resultValue != null) {
    						return resultValue;
    					}
		        	}
    			} catch (Exception e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
    		}
    	} else if (UtilValidate.isNotEmpty(listCategory)) {
    		for (Map<String, Object> cateItem : listCategory) {
    			String productCategoryId = (String) cateItem.get("productCategoryId");
    			String amountStr = (String) cateItem.get("amount");
    			BigDecimal amount = new BigDecimal(amountStr);
    			//TODO
    			try {
					GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
					if (productCategory == null || UtilValidate.isEmpty(amountStr)) {
						if (productCategory == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSProductCategoryHasIdIsNotExists", UtilMisc.toList(productCategoryId), locale));
						}
						if (UtilValidate.isEmpty(amountStr)) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSListPriceOfProductCategoryHasIdIsMustNotBeEmpty", UtilMisc.toList(productCategoryId), locale));
		    			}
						if (alertMessageList.size() > 0) {
							successResult.put(ModelService.ERROR_MESSAGE_LIST, alertMessageList);
						}
		        	} else {
    					Map<String, Object> resultValue = createQuotationRuleAdvanceCategory(delegator, productQuotationId, description, quotationName, productCategoryId, amount, partyId);
    					if (ServiceUtil.isError(resultValue)) {
    						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultValue));
    					}
		        	}
    			} catch (Exception e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
    		}
    	}
    	
        successResult.put("productQuotationId", productQuotationId);
        return successResult;
    }
    
    private static Map<String, Object> createQuotationRuleAdvanceCategory(Delegator delegator, String productQuotationId, String description, 
    		String quotationName, String productCategoryId, BigDecimal amount, String partyIdApply){
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	try {
    		// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
			String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
			GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
					"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
			delegator.create(newEntityProductPriceRule);
			
			/* Create product price condition: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
				cond1: product */
			GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
			newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PROD_CAT_ID");
            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
            newEntityProductPriceCond.put("condValue", productCategoryId);
            delegator.create(newEntityProductPriceCond);
            
            if (UtilValidate.isNotEmpty(partyIdApply)) {
            	GenericValue partyApply = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdApply), false);
            	if (partyApply != null) {
            		String inputParamEnumId = "PRIP_PARTY_GRP_MEM";
            		List<String> partyTypePersonIds = SalesPartyUtil.getDescendantPartyTypeIds("PERSON", delegator);
                	if (UtilValidate.isNotEmpty(partyTypePersonIds) && partyTypePersonIds.contains(partyApply.getString("partyTypeId"))) {
                		inputParamEnumId = "PRIP_PARTY_ID";
                	}
            		GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
    	            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
    	            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
    	            newEntityProductPriceCond3.put("inputParamEnumId", inputParamEnumId);
    	            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
    	            newEntityProductPriceCond3.put("condValue", partyIdApply);
    	            delegator.create(newEntityProductPriceCond3);
            	}
            }
            
            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
            if (UtilValidate.isNotEmpty(amount)) {
            	// priceToDist
				GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
				newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
				delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
				newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FOD");
				newEntityProductPriceAction.put("amount", amount);
	            delegator.create(newEntityProductPriceAction);
            }
    	} catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    	return successResult;
    }
    
    private static Map<String, Object> createQuotationRuleAdvance(Delegator delegator, String productQuotationId, String description, 
    		String quotationName, String productId, String quantityUomId, String listPriceStr, 
    		String partyIdApply) {
    	try {
	    	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    		// Create product price rule: productPriceRuleId, ruleName, description, isSale, fromDate, thruDate, productQuotationId
			String productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
			GenericValue newEntityProductPriceRule = delegator.makeValue("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
					"ruleName", quotationName, "description", description, "productQuotationId", productQuotationId, "isSale", "N"));
			delegator.create(newEntityProductPriceRule);
			
			/* Create product price cond: productPriceRuleId *, productPriceCondSeqId *, inputParamEnumId, operatorEnumId, condValue
				cond1: product 
				cond2: quantityUomId - PRIP_QUANTITY_UOMID 
				cond3: partyId list*/
			GenericValue newEntityProductPriceCond = delegator.makeValue("ProductPriceCond");
			newEntityProductPriceCond.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
            delegator.setNextSubSeqId(newEntityProductPriceCond, "productPriceCondSeqId", 2, 1);
            newEntityProductPriceCond.put("inputParamEnumId", "PRIP_PRODUCT_ID");
            newEntityProductPriceCond.put("operatorEnumId", "PRC_EQ");
            newEntityProductPriceCond.put("condValue", productId);
            delegator.create(newEntityProductPriceCond);
            
        	if (UtilValidate.isEmpty(quantityUomId)) {
            	if (UtilValidate.isNotEmpty(product.getString("quantityUomId"))) {
            		quantityUomId = product.getString("quantityUomId");
            	}
            }
            if (UtilValidate.isNotEmpty(quantityUomId)) {
            	GenericValue newEntityProductPriceCond2 = delegator.makeValue("ProductPriceCond");
	            newEntityProductPriceCond2.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
	            delegator.setNextSubSeqId(newEntityProductPriceCond2, "productPriceCondSeqId", 2, 1);
	            newEntityProductPriceCond2.put("inputParamEnumId", "PRIP_QUANTITY_UOMID");
	            newEntityProductPriceCond2.put("operatorEnumId", "PRC_EQ");
	            newEntityProductPriceCond2.put("condValue", quantityUomId);
	            delegator.create(newEntityProductPriceCond2);
            }
            
            /*if (UtilValidate.isNotEmpty(partyIdsApply)) {
            	for (String partyId : partyIdsApply) {
            		GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
		            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
		            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
		            newEntityProductPriceCond3.put("inputParamEnumId", "PRIP_PARTY_ID");
		            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
		            newEntityProductPriceCond3.put("condValue", partyId);
		            delegator.create(newEntityProductPriceCond3);
            	}
            }*/
            if (UtilValidate.isNotEmpty(partyIdApply)) {
            	GenericValue partyApply = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdApply), false);
            	if (partyApply != null) {
            		String inputParamEnumId = "PRIP_PARTY_GRP_MEM";
            		List<String> partyTypePersonIds = SalesPartyUtil.getDescendantPartyTypeIds("PERSON", delegator);
                	if (UtilValidate.isNotEmpty(partyTypePersonIds) && partyTypePersonIds.contains(partyApply.getString("partyTypeId"))) {
                		inputParamEnumId = "PRIP_PARTY_ID";
                	}
            		GenericValue newEntityProductPriceCond3 = delegator.makeValue("ProductPriceCond");
    	            newEntityProductPriceCond3.put("productPriceRuleId", newEntityProductPriceRule.get("productPriceRuleId"));
    	            delegator.setNextSubSeqId(newEntityProductPriceCond3, "productPriceCondSeqId", 2, 1);
    	            newEntityProductPriceCond3.put("inputParamEnumId", inputParamEnumId);
    	            newEntityProductPriceCond3.put("operatorEnumId", "PRC_EQ");
    	            newEntityProductPriceCond3.put("condValue", partyIdApply);
    	            delegator.create(newEntityProductPriceCond3);
            	}
            }
            
            // Create product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
            if (UtilValidate.isNotEmpty(listPriceStr)) {
            	// priceToDist
        		BigDecimal priceToDist = new BigDecimal(listPriceStr);
				GenericValue newEntityProductPriceAction = delegator.makeValue("ProductPriceAction");
				newEntityProductPriceAction.put("productPriceRuleId", productPriceRuleId);
				delegator.setNextSubSeqId(newEntityProductPriceAction, "productPriceActionSeqId", 2, 1);
				newEntityProductPriceAction.put("productPriceActionTypeId", "PRICE_FLAT");
				newEntityProductPriceAction.put("amount", priceToDist);
	            delegator.create(newEntityProductPriceAction);
            }
    	} catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
		return null;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateProductQuotation(DispatchContext ctx, Map<String, Object> context) {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        Security security = ctx.getSecurity();
        boolean hasPermissionTypeSales = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_EDIT");
        boolean hasPermissionTypePurchase = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATIONPO_EDIT");
        if (!hasPermissionTypeSales && !hasPermissionTypePurchase) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
        }
        
        String productQuotationId = (String) context.get("productQuotationId");
        String quotationName = (String) context.get("quotationName");
        String description = (String) context.get("description");
        String salesMethodChannelEnumId = (String) context.get("salesMethodChannelEnumId");
        String currencyUomId = (String) context.get("currencyUomId");
        String fromDateStr = (String) context.get("fromDate");
        String thruDateStr = (String) context.get("thruDate");
        String partyId = (String) context.get("partyId");
        //List<String> roleTypeIdsParam = (List<String>) context.get("roleTypeIds");
        List<String> productStoreIds = (List<String>) context.get("productStoreIds[]");
        List<String> productStoreGroupIds = (List<String>) context.get("productStoreGroupIds[]");
        //List<String> partyIds = (List<String>) context.get("partyIds");
        String productListStr = (String) context.get("productList");
        
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
        }
        
        List<String> errorMessageList = FastList.newInstance();
        GenericValue quotationSelected = null;
        if (UtilValidate.isNotEmpty(productQuotationId)) {
        	try {
        		quotationSelected = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
	        	if (quotationSelected == null) {
	        		errorMessageList.add(UtilProperties.getMessage(resource_error, "BSNotFoundProductQuotationHasProductQuotationIdIs", UtilMisc.toList(productQuotationId), locale));
	        	}
        	} catch (Exception e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenQueryData", locale));
            }
        } else {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotaionIdMustNotBeEmpty", locale));
        }
        String productQuotationModuleTypeId = quotationSelected.getString("productQuotationModuleTypeId");
        if (!(hasPermissionTypeSales && "SALES_QUOTATION".equals(productQuotationModuleTypeId)) 
        		&& !(hasPermissionTypePurchase && "PURCHASE_QUOTATION".equals(productQuotationModuleTypeId))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermissionQuotationWithType", UtilMisc.toList("productQuotationModuleTypeId", productQuotationModuleTypeId), locale));
        }
        
        if (UtilValidate.isEmpty(quotationName)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSQuotaionNameMustNotBeEmpty", locale));
        }
        /*if (UtilValidate.isEmpty(salesMethodChannelEnumId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSSalesChannelMustNotBeEmpty", locale));
        }*/
        if (UtilValidate.isEmpty(currencyUomId)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSCurrencyUomIdMustNotBeEmpty", locale));
        }
        if (UtilValidate.isEmpty(fromDate)) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "BSFromDateMustNotBeEmpty", locale));
        }
        
        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }
        
        // begin update
        List<String> alertMessageList = FastList.newInstance();
        quotationSelected.set("quotationName", quotationName);
        quotationSelected.set("description", description);
        quotationSelected.set("salesMethodChannelEnumId", salesMethodChannelEnumId);
        quotationSelected.set("currencyUomId", currencyUomId);
        quotationSelected.set("fromDate", fromDate);
        quotationSelected.set("thruDate", thruDate);
        
        // first try to update the ProductQuotation; if this does not fail, continue.
        try {
            delegator.store(quotationSelected);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot update ProductQuotation entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationUpdateFailedPleaseNotifyCustomerService",locale));
        }
        if ("QUOTATION_ACCEPTED".equals(quotationSelected.getString("statusId"))) {
        	try {
				Map<String, Object> changeStatusResult = dispatcher.runSync("changeQuotationStatus", UtilMisc.toMap(
										"productQuotationId", productQuotationId, "statusId", "QUOTATION_MODIFIED", "userLogin", userLogin));
				if (ServiceUtil.isError(changeStatusResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(changeStatusResult));
				}
			} catch (GenericServiceException e) {
				Debug.logError(e, "Error when change status ProductQuotation to MODIFIED", module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenChangeStatus",locale));
			}
        }
        
        String productQuotationTypeId = quotationSelected.getString("productQuotationTypeId");
        
        List<EntityCondition> conds = FastList.newInstance();
        try {
        	/*if (UtilValidate.isNotEmpty(productStoreIds)) {
        		List<GenericValue> storeApplTobeStored = new LinkedList<GenericValue>();
        		boolean notExists = false;
        		
        		List<EntityCondition> listCond = new ArrayList<EntityCondition>();
        		listCond.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
        		List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
        		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
        		listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
        		listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
        		
        		List<GenericValue> storesApplRemove = delegator.findList("ProductQuotationStoreAppl", 
        				EntityCondition.makeCondition(EntityCondition.makeCondition(listCond), 
        						EntityOperator.AND, EntityCondition.makeCondition("productStoreId", EntityOperator.NOT_IN, productStoreIds)), null, null, null, false);
        		if (UtilValidate.isNotEmpty(storesApplRemove)) {
					for (GenericValue storeAppl : storesApplRemove) {
						storeAppl.set("thruDate", nowTimestamp);
					}
					delegator.storeAll(storesApplRemove);
        		}
        		List<GenericValue> storesApplExists = delegator.findList("ProductQuotationStoreAppl", EntityCondition.makeCondition(listCond), UtilMisc.toSet("productStoreId"), null, null, false);
				List<String> storeIdApplsExists = EntityUtil.getFieldListFromEntityList(storesApplExists, "productStoreId", true);
				if (UtilValidate.isEmpty(storeIdApplsExists)) {
					// add all
					notExists = true;
				}
				for (String productStoreId : productStoreIds) {
					if (notExists || !storeIdApplsExists.contains(productStoreId)) {
						GenericValue storeAppl = delegator.makeValue("ProductQuotationStoreAppl");
						storeAppl.put("productQuotationId", productQuotationId);
						storeAppl.put("productStoreId", productStoreId);
						storeAppl.put("fromDate", fromDate);
						storeApplTobeStored.add(storeAppl);
					} else {
						GenericValue productStoreAppl = EntityUtil.getFirst(EntityUtil.filterByAnd(storesApplExists, UtilMisc.toMap("productStoreId", productStoreId)));
						if (productStoreAppl != null) {
							productStoreAppl.set("fromDate", fromDate);
							storeApplTobeStored.add(productStoreAppl);
						}
					}
				}
				if (UtilValidate.isNotEmpty(storeApplTobeStored)) {
					delegator.storeAll(storeApplTobeStored);
				}
        	} else {
        		List<EntityCondition> condsDelete = new ArrayList<EntityCondition>();
				condsDelete.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
				condsDelete.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> storeApplsDelete = delegator.findList("ProductQuotationStoreAppl", EntityCondition.makeCondition(condsDelete), null, null, null, false);
				if (UtilValidate.isNotEmpty(storeApplsDelete)) {
					for (GenericValue storeAppl : storeApplsDelete) {
						storeAppl.set("thruDate", nowTimestamp);
					}
					delegator.storeAll(storeApplsDelete);
				}
        	}*/
        	List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	// update list product quotation store appl
        	conds.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
        	conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp), 
        			EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
        	List<GenericValue> listProductQuotationStoreAppl = delegator.findList("ProductQuotationStoreAppl", EntityCondition.makeCondition(conds), null, null, null, false);
            if (listProductQuotationStoreAppl != null) {
            	List<String> productStoreIdAppls = EntityUtil.getFieldListFromEntityList(listProductQuotationStoreAppl, "productStoreId", true);
            	for (GenericValue productQuotationStoreAppl : listProductQuotationStoreAppl) {
            		if (productStoreIds != null && productStoreIds.contains(productQuotationStoreAppl.getString("productStoreId"))) {
            			if (fromDate.equals(productQuotationStoreAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productStorePromoAppl record + create new record with fromDate
            				productQuotationStoreAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(productQuotationStoreAppl);
            				GenericValue newProductQuotationStoreAppl = delegator.makeValue("ProductQuotationStoreAppl");
            				newProductQuotationStoreAppl.put("productStoreId", productQuotationStoreAppl.get("productStoreId"));
            				newProductQuotationStoreAppl.put("productQuotationId", productQuotationStoreAppl.get("productQuotationId"));
            				newProductQuotationStoreAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductQuotationStoreAppl);
            			}
            		} else {
            			// this record was deleted
            			productQuotationStoreAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(productQuotationStoreAppl);
            		}
            	}
            	if (productStoreIds != null) {
            		for (String productStoreId : productStoreIds) {
                		if (productStoreIdAppls.contains(productStoreId)) {
                			// no action
                		} else {
                			// create new
                			GenericValue newProductQuotationStoreAppl = delegator.makeValue("ProductQuotationStoreAppl");
            				newProductQuotationStoreAppl.put("productStoreId", productStoreId);
            				newProductQuotationStoreAppl.put("productQuotationId", productQuotationId);
            				newProductQuotationStoreAppl.put("fromDate", fromDate);
            				newProductQuotationStoreAppl.put("thruDate", thruDate);
            				tobeStored.add(newProductQuotationStoreAppl);
                		}
                	}
            	}
            }
            delegator.storeAll(tobeStored);
        } catch (GenericEntityException e) {
			Debug.logError(e, "Cannot update ProductQuotationStoreAppl entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
		}
        
        try {
        	List<GenericValue> tobeStoredStoreGroup = new LinkedList<GenericValue>();
        	// update list product quotation store group appl
        	conds.clear();
        	conds.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
        	conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp), 
        			EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
        	List<GenericValue> listQuotationStoreGroupAppl = delegator.findList("ProductQuotationStoreGroupAppl", EntityCondition.makeCondition(conds), null, null, null, false);
        	if (listQuotationStoreGroupAppl != null) {
        		List<String> productStoreGroupIdAppls = EntityUtil.getFieldListFromEntityList(listQuotationStoreGroupAppl, "productStoreGroupId", true);
        		for (GenericValue quotationStoreGroupAppl : listQuotationStoreGroupAppl) {
        			if (productStoreGroupIds != null && productStoreGroupIds.contains(quotationStoreGroupAppl.getString("productStoreGroupId"))) {
        				if (fromDate.equals(quotationStoreGroupAppl.getTimestamp("fromDate"))) {
        					// no action
        				} else {
        					// thruDate this productStorePromoAppl record + create new record with fromDate
        					quotationStoreGroupAppl.put("thruDate", nowTimestamp);
        					tobeStoredStoreGroup.add(quotationStoreGroupAppl);
        					GenericValue newQuotationStoreGroupAppl = delegator.makeValue("ProductQuotationStoreGroupAppl");
        					newQuotationStoreGroupAppl.put("productStoreGroupId", quotationStoreGroupAppl.get("productStoreGroupId"));
        					newQuotationStoreGroupAppl.put("productQuotationId", quotationStoreGroupAppl.get("productQuotationId"));
        					newQuotationStoreGroupAppl.put("fromDate", fromDate);
        					tobeStoredStoreGroup.add(newQuotationStoreGroupAppl);
        				}
        			} else {
        				// this record was deleted
        				quotationStoreGroupAppl.put("thruDate", nowTimestamp);
        				tobeStoredStoreGroup.add(quotationStoreGroupAppl);
        			}
        		}
        		if (productStoreGroupIds != null) {
        			for (String productStoreGroupId : productStoreGroupIds) {
        				if (productStoreGroupIdAppls.contains(productStoreGroupId)) {
        					// no action
        				} else {
        					// create new
        					GenericValue newProductQuotationStoreAppl = delegator.makeValue("ProductQuotationStoreGroupAppl");
        					newProductQuotationStoreAppl.put("productStoreGroupId", productStoreGroupId);
        					newProductQuotationStoreAppl.put("productQuotationId", productQuotationId);
        					newProductQuotationStoreAppl.put("fromDate", fromDate);
        					newProductQuotationStoreAppl.put("thruDate", thruDate);
        					tobeStoredStoreGroup.add(newProductQuotationStoreAppl);
        				}
        			}
        		}
        	}
        	delegator.storeAll(tobeStoredStoreGroup);
        } catch (GenericEntityException e) {
        	Debug.logError(e, "Cannot update ProductQuotationStoreGroupAppl entity; problems with insert", module);
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing",locale));
        }
        
        /*boolean isJsonRole = false;
    	if (UtilValidate.isNotEmpty(roleTypeIdsParam) && roleTypeIdsParam.size() > 0){
    		if (roleTypeIdsParam.get(0) instanceof String) isJsonRole = true;
    	}
    	List<String> roleTypeIds = new ArrayList<String>();
    	if (isJsonRole){
			String roleTypeIdsStr = "[" + (String) roleTypeIdsParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(roleTypeIdsStr)) {
				jsonArray = JSONArray.fromObject(roleTypeIdsStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					roleTypeIds.add(jsonArray.getString(i));
				}
			}
    	} else {
    		roleTypeIds = (List<String>) context.get("roleTypeIds");
    	}*/
        
    	//boolean isJson = false;
    	//if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    	//	if (productListParam.get(0) instanceof String) isJson = true;
    	//}
    	List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	List<Map<String, Object>> listCategory = new ArrayList<Map<String,Object>>();
    	//if (isJson){
			//String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				if ("PROD_CAT_PRICE_FOD".equals(productQuotationTypeId)) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject prodItem = jsonArray.getJSONObject(i);
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("productCategoryId", prodItem.getString("productCategoryId"));
						productItem.put("amount", prodItem.getString("amount"));
						listCategory.add(productItem);
					}
				} else {
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject prodItem = jsonArray.getJSONObject(i);
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("productId", prodItem.getString("productId"));
						productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						productItem.put("listPrice", prodItem.getString("listPrice"));
						if (prodItem.containsKey("listPriceVAT")) productItem.put("listPriceVAT", prodItem.getString("listPriceVAT"));
						if (prodItem.containsKey("taxPercentage")) productItem.put("taxPercentage", prodItem.getString("taxPercentage"));
						listProduct.add(productItem);
					}
				}
			}
    	//} else {
    	//	listProduct = (List<Map<String, Object>>) context.get("listProducts");
    	//}
    	if (UtilValidate.isNotEmpty(listProduct)) {
    		List<String> listQuotationRuleIdUpdate = new ArrayList<String>();
			List<String> listQuotationRuleIdAll = null;
			try {
    			listQuotationRuleIdAll = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productQuotationId", productQuotationId), null, false), "productPriceRuleId", true);
    		} catch (GenericEntityException e) {
    			Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationUpdateFailedPleaseNotifyCustomerService",locale));
            }
			// TODO
    		for (Map<String, Object> prodItem : listProduct) {
    			String productId = (String) prodItem.get("productId");
    			String quantityUomId = (String) prodItem.get("quantityUomId");
    			String listPriceStr = (String) prodItem.get("listPrice");
    			//String listPriceVATStr = (String) prodItem.get("listPriceVAT");
    			//String taxPercentage = (String) prodItem.get("taxPercentageStr");
    			
    			try {
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					if (product == null || UtilValidate.isEmpty(listPriceStr)) {
						if (product == null) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSProductNotExists",locale));
						}
						if (UtilValidate.isEmpty(listPriceStr)) {
							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSPriceToDistOfProductXIsEmptyDontCreate", UtilMisc.<String, Object> toMap("productId", productId), locale));
		    			}
		        	} else {
						// get list product pricing rules of quotation
						/* List<GenericValue> priceRulesOfProduct = delegator.findByAnd("ProductQuotationAndPriceRCA", UtilMisc.toMap("productQuotationId", productQuotationId, "productId", productId, "inputParamEnumId", "PRIP_PRODUCT_ID", "productPriceActionTypeId", "PRICE_FLAT"), null, false);*/
						List<EntityCondition> listCond = new ArrayList<EntityCondition>();
						listCond.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
						listCond.add(EntityCondition.makeCondition("productId", productId));
						listCond.add(EntityCondition.makeCondition("quantityUomId", quantityUomId));
						listCond.add(EntityCondition.makeCondition("inputParamEnumId", "PRIP_PRODUCT_ID"));
						EntityFindOptions findOpts = new EntityFindOptions();
						findOpts.setDistinct(true);
		        		List<GenericValue> priceRulesOfProduct = delegator.findList("ProductQuotationAndPriceRCADetail", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, findOpts, false);
						if (priceRulesOfProduct == null || priceRulesOfProduct.isEmpty()) {
							// return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSProductQuotationRuleForThisProductNotFound",locale));
							// INSERT: create new rule (conditions/actions)
							Map<String, Object> resultValue = createQuotationRuleAdvance(delegator, productQuotationId, description, quotationName, productId, quantityUomId, listPriceStr, partyId);
	    					if (resultValue != null) {
	    						return resultValue;
	    					}
						} else {
							// UPDATE: product price action: productPriceRuleId *, productPriceActionSeqId *, productPriceActionTypeId, amount, rateCode
				            
							//List<String> listProductPriceRuleId = EntityUtil.getFieldListFromEntityList(priceRulesOfProduct, "productPriceRuleId", true);
							for (GenericValue productPriceRuleRCA : priceRulesOfProduct) {
								String productPriceRuleId = productPriceRuleRCA.getString("productPriceRuleId");
								String productPriceActionSeqId = "";
								if ("PRICE_FLAT".equals(productPriceRuleRCA.getString("productPriceActionTypeId"))) {
									//  priceToDist 
						            if (UtilValidate.isNotEmpty(listPriceStr)) {
						            	productPriceActionSeqId = productPriceRuleRCA.getString("productPriceActionSeqId");
					            		BigDecimal priceToDist = new BigDecimal(listPriceStr);
					            		GenericValue productPriceActionFirst = delegator.findOne("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productPriceActionSeqId", productPriceActionSeqId), false);
					            		if (productPriceActionFirst != null) {
					            			productPriceActionFirst.set("amount", priceToDist);
					            			delegator.store(productPriceActionFirst);
					            		}
						            }
								}
								listQuotationRuleIdUpdate.add(productPriceRuleId);
							}
						}
		        	}
    			} catch (Exception e) {
           		 	Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
    			}
    		}
    		
    		// DELETE: check and delete product quotation rule
    		if (listQuotationRuleIdAll != null) {
    			try {
	    			for (String productPriceRuleId : listQuotationRuleIdAll) {
	    				if (!listQuotationRuleIdUpdate.contains(productPriceRuleId)) {
	    					/*boolean hasPermissionDelete = security.hasEntityPermission("DELYS_QUOTATION", "_DELETE", userLogin);;
	    			        if (!hasPermissionDelete) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationDeletePermissionError",locale));*/
	    			        
	    			        // find all condition of a quotation rule
	    			        List<GenericValue> listProductPriceCond = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
	    			        if (listProductPriceCond != null) delegator.removeAll(listProductPriceCond);
	    			        
	    			        // find all action of a quotation rule
	    			        List<GenericValue> listProductPriceAct = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
	    			        if (listProductPriceAct != null) delegator.removeAll(listProductPriceAct);
	    			        
	    			        GenericValue productPriceRuleSelected = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), false);
	    			        if (productPriceRuleSelected != null) delegator.removeValue(productPriceRuleSelected);
	    				}
	    			}
    			} catch (Exception e) {
    				Debug.logError(e, "Cannot delete ProductPriceRule entity; problems with delete", module);
    	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSQuotationRuleDeleteFailedPleaseNotifyCustomerService",locale));
     			}
    		}
    	} else if (UtilValidate.isNotEmpty(listCategory)) {
    		try {
    			List<GenericValue> listPriceRulesCondActionDeleted = delegator.findByAnd("ProductQuotationRulesCateFOL", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
        		for (Map<String, Object> cateItem : listCategory) {
        			String productCategoryId = (String) cateItem.get("productCategoryId");
        			String amountStr = (String) cateItem.get("amount");
        			BigDecimal amount = new BigDecimal(amountStr);
        			
        			List<GenericValue> categoryRules = EntityUtil.filterByCondition(listPriceRulesCondActionDeleted, EntityCondition.makeCondition("productCategoryId", productCategoryId));
        			if (UtilValidate.isNotEmpty(categoryRules)) {
        				// update
        				for (GenericValue item : categoryRules) {
        					listPriceRulesCondActionDeleted.remove(item);
        					GenericValue productPriceCondition = delegator.findOne("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", item.get("productPriceRuleId"), "productPriceActionSeqId", item.get("productPriceActionSeqId")), false);
        					if (productPriceCondition != null) {
        						productPriceCondition.set("amount", amount);
        						delegator.store(productPriceCondition);
        					}
        				}
        			} else {
        				// create
        				GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId), false);
    					if (productCategory == null || UtilValidate.isEmpty(amountStr)) {
    						if (productCategory == null) {
    							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSProductCategoryHasIdIsNotExists", UtilMisc.toList(productCategoryId), locale));
    						}
    						if (UtilValidate.isEmpty(amountStr)) {
    							alertMessageList.add(UtilProperties.getMessage(resource_error, "BSListPriceOfProductCategoryHasIdIsMustNotBeEmpty", UtilMisc.toList(productCategoryId), locale));
    		    			}
    						if (alertMessageList.size() > 0) {
    							successResult.put(ModelService.ERROR_MESSAGE_LIST, alertMessageList);
    						}
    		        	} else {
        					Map<String, Object> resultValue = createQuotationRuleAdvanceCategory(delegator, productQuotationId, description, quotationName, productCategoryId, amount, partyId);
        					if (ServiceUtil.isError(resultValue)) {
        						return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultValue));
        					}
    		        	}
        			}
        		}
        		
        		if (UtilValidate.isNotEmpty(listPriceRulesCondActionDeleted)) {
        			for (GenericValue item : listPriceRulesCondActionDeleted) {
        				String productPriceRuleId = item.getString("productPriceRuleId");
        				
    			        // delete all condition
    			        List<GenericValue> listProductPriceCond = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
    			        if (listProductPriceCond != null) delegator.removeAll(listProductPriceCond);
    			        
    			        // delete all action
    			        List<GenericValue> listProductPriceAct = delegator.findByAnd("ProductPriceAction", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), null, false);
    			        if (listProductPriceAct != null) delegator.removeAll(listProductPriceAct);
    			        
    			        // delete rule
    			        GenericValue productPriceRuleSelected = delegator.findOne("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId), false);
    			        if (productPriceRuleSelected != null) delegator.removeValue(productPriceRuleSelected);
	    			}
        		}
    		} catch (Exception e) {
       		 	Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
			}
    	}
    	alertMessageList.add(UtilProperties.getMessage(resource, "BSUpdateSuccessful", locale));
    	if (alertMessageList.size() > 0) {
			successResult.put(ModelService.SUCCESS_MESSAGE_LIST, alertMessageList);
		}
        successResult.put("productQuotationId", productQuotationId);
        return successResult;
    }
	

    /** Service for changing the status on an product quotation */
	public static Map<String, Object> setQuotationStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productQuotationId = (String) context.get("productQuotationId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        Security security = ctx.getSecurity();
        boolean hasPermissionTypeSales = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATION_APPROVE");
        boolean hasPermissionTypePurchase = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "PRODQUOTATIONPO_APPROVE");
        if (!hasPermissionTypeSales && !hasPermissionTypePurchase) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission",locale));
        }
        
        GenericValue productQuotation = null;
        try {
        	productQuotation = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
        	if (productQuotation == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundProductQuotationHasProductQuotationIdIs", UtilMisc.toList(productQuotationId), locale));
			}
        	String productQuotationModuleTypeId = productQuotation.getString("productQuotationModuleTypeId");
            if (!(hasPermissionTypeSales && "SALES_QUOTATION".equals(productQuotationModuleTypeId)) 
            		&& !(hasPermissionTypePurchase && "PURCHASE_QUOTATION".equals(productQuotationModuleTypeId))) {
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermissionQuotationWithType", UtilMisc.toList("productQuotationModuleTypeId", productQuotationModuleTypeId), locale));
            }

            // first save off the old status
        	successResult.put("oldStatusId", productQuotation.get("statusId"));

        	if (productQuotation.getString("statusId").equals(statusId)) {
        		return successResult;
        	}
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", productQuotation.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findOne("StatusValidChange", statusFields, true);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "BSErrorCouldNotChangeQuotationStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorCouldNotChangeQuotationStatus",locale) + e.getMessage() + ").");
            }

            // update the current status
            productQuotation.set("statusId", statusId);

            // now create a status change
            GenericValue quotationStatus = delegator.makeValue("ProductQuotationStatus");
            quotationStatus.put("quotationStatusId", delegator.getNextSeqId("ProductQuotationStatus"));
            quotationStatus.put("statusId", statusId);
            quotationStatus.put("productQuotationId", productQuotationId);
            quotationStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            quotationStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
            quotationStatus.put("changeReason", changeReason);

            productQuotation.store();
            quotationStatus.create();
            
            if (statusId != null && statusId.equals("QUOTATION_ACCEPTED")) {
            	List<GenericValue> listPriceRule = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("productQuotationId", productQuotationId), null, false);
                if (listPriceRule != null && listPriceRule.size() > 0) {
                	for (GenericValue priceRuleItem : listPriceRule) {
    					if (productQuotation.get("fromDate") != null) {
    						priceRuleItem.set("fromDate", productQuotation.get("fromDate"));
    					}
    					if (productQuotation.get("thruDate") != null) {
    						priceRuleItem.set("thruDate", productQuotation.get("thruDate"));
    					}
    					priceRuleItem.store();
    				}
                }
			}
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "BSErrorCouldNotChangeQuotationStatus",locale) + e.getMessage() + ").");
        }
    	
        successResult.put("productQuotationId", productQuotationId);
        successResult.put("quotationStatusId", statusId);
        // Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
        return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAndTaxInQuotation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		try {
			String productQuotationId = SalesUtil.getParameter(parameters, "productQuotationId");
			GenericValue productQuotation = delegator.findOne("ProductQuotation", UtilMisc.toMap("productQuotationId", productQuotationId), false);
			if (productQuotation != null) {
				listAllConditions.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("productPriceRuleId");
				}
				List<GenericValue> listProducts = delegator.findList("ProductQuotationRulesAndTax", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
				if (UtilValidate.isNotEmpty(listProducts)) {
					for (GenericValue productItem : listProducts) {
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productItem.get("productId")), false);
						if (product == null) continue;
						
						Map<String, Object> mapItem = productItem.getAllFields();
						String productId = productItem.getString("productId");
						String quantityUomId = productItem.getString("quantityUomId");
						
						List<Map<String, Object>> packingUomIds = ProductWorker.getListQuantityUomIds(productId, product.getString("quantityUomId"), delegator, dispatcher);
						mapItem.put("packingUomIds", packingUomIds);
						
						if (UtilValidate.isNotEmpty(packingUomIds)) {
							for (Map<String, Object> uomItem : packingUomIds) {
								if (quantityUomId != null && quantityUomId.equals((String) uomItem.get("uomId"))) {
									mapItem.put("quantityConvert", uomItem.get("quantityConvert"));
									mapItem.put("unitPrice", uomItem.get("unitPriceConvert"));
								}
							}
						}
						listIterator.add(mapItem);
					}
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAndTaxInQuotation service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	/*
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductAndTaxInQuotation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		try {
			if (opts == null) opts = new EntityFindOptions();
			opts.setDistinct(true);
			
			// get all product in stores
			List<String> prodCatalogIds = new ArrayList<String>();
			List<String> productStoreIds = new ArrayList<String>();
			Map<String, GenericValue> mapProdSelected = FastMap.newInstance();
			
			if (parameters.containsKey("productStoreIds") && parameters.get("productStoreIds").length > 0) {
				String[] productStoreIdsStr = parameters.get("productStoreIds");
				List<String> productStoreIdsTmp = Arrays.asList(productStoreIdsStr);
				if (UtilValidate.isNotEmpty(productStoreIdsTmp)) {
					productStoreIds.addAll(productStoreIdsTmp);
				}
			}
			
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				String productQuotationId = SalesUtil.getParameter(parameters, "productQuotationId");
				if (UtilValidate.isNotEmpty(productQuotationId)) {
					List<String> productStoreIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductQuotationStoreAppl", UtilMisc.toMap("productQuotationId", productQuotationId), null, false), "productStoreId", true);
					if (UtilValidate.isNotEmpty(productStoreIdsTmp)) {
						boolean isGetItemInQuota = false;
						for (String productStoreId : productStoreIdsTmp) {
							if (productStoreIds.contains(productStoreId)) {
								isGetItemInQuota = true;
								break;
							}
						}
						if (isGetItemInQuota) {
							// get all product in quotation
							EntityCondition condMain = EntityCondition.makeCondition(EntityCondition.makeCondition(listAllConditions), EntityOperator.AND, EntityCondition.makeCondition("productQuotationId", productQuotationId));
							List<GenericValue> listTmp = delegator.findList("ProductQuotationRulesAndTax", condMain, null, listSortFields, opts, false);
							if (listTmp != null) {
								for (GenericValue itemProd : listTmp) {
									//listIterator.add(ProductWorker.processGeneralProdAndTax(delegator, locale, itemProd, null, null, true));
									String key = itemProd.getString("productId") + "@" + itemProd.getString("quantityUomId");
									mapProdSelected.put(key, itemProd);
								}
							}
						}
					}
				}
				
				List<String> productCatalogIdsTmp = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), null, null, null, false), "prodCatalogId", true);
				if (UtilValidate.isNotEmpty(productCatalogIdsTmp)) {
					prodCatalogIds.addAll(productCatalogIdsTmp);
				}
			}
			
			if (UtilValidate.isNotEmpty(prodCatalogIds)) {
				Map<String, Object> productResult = ProductWorker.getListProductAndTaxByCatalogAndPeriod(delegator, locale, prodCatalogIds, true, null, null, null, parameters, listSortFields, listAllConditions);
				if (productResult != null) {
					successResult.put("TotalRows", productResult.get("TotalRows"));
					listIterator = (List<Map<String, Object>>) productResult.get("listIterator");
				}
				
				if (UtilValidate.isNotEmpty(listIterator)) {
					if (UtilValidate.isNotEmpty(mapProdSelected)) {
						for (Map<String, Object> item : listIterator) {
							if (UtilValidate.isNotEmpty(item.get("packingUomIds"))) {
								List<Map<String, Object>> packingUomIds = (List<Map<String, Object>>) item.get("packingUomIds");
								for (Map<String, Object> itemUom : packingUomIds) {
									String key = item.get("productId") + "@" + itemUom.get("uomId");
									if (mapProdSelected.containsKey(key)) {
										GenericValue itemSelected = mapProdSelected.get(key);
										item.put("productQuotationId", itemSelected.get("productQuotationId"));
										item.put("currencyUomId", itemSelected.get("currencyUomId"));
										item.put("quantityUomId", itemSelected.get("quantityUomId"));
										item.put("listPrice", itemSelected.get("listPrice"));
										item.put("taxPercentage", itemSelected.get("taxPercentage"));
										item.put("taxAuthPartyId", itemSelected.get("taxAuthPartyId"));
										item.put("taxAuthGeoId", itemSelected.get("taxAuthGeoId"));
										item.put("listPriceVAT", itemSelected.get("listPriceVAT"));
										//productId, productCode, productName, isVirtual, isVariant, internalName
									}
								}
							} else {
								String key = item.get("productId") + "@";
								if (mapProdSelected.containsKey(key)) {
									GenericValue itemSelected = mapProdSelected.get(key);
									item.put("productQuotationId", itemSelected.get("productQuotationId"));
									item.put("currencyUomId", itemSelected.get("currencyUomId"));
									item.put("quantityUomId", itemSelected.get("quantityUomId"));
									item.put("listPrice", itemSelected.get("listPrice"));
									item.put("taxPercentage", itemSelected.get("taxPercentage"));
									item.put("taxAuthPartyId", itemSelected.get("taxAuthPartyId"));
									item.put("taxAuthGeoId", itemSelected.get("taxAuthGeoId"));
									item.put("listPriceVAT", itemSelected.get("listPriceVAT"));
									//productId, productCode, productName, isVirtual, isVariant, internalName
								}
							}
						}
					}
					
					//listIterator = EntityMiscUtil.filterMap(listIterator, listAllConditions);
					//listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductAndTaxInQuotation service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}*/
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotatonRules(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	if (parameters.containsKey("productQuotationId")) {
    		String productQuotationId = (String) parameters.get("productQuotationId")[0];
    		if (UtilValidate.isNotEmpty(productQuotationId)) {
    			listAllConditions.add(EntityCondition.makeCondition("productQuotationId", EntityOperator.EQUALS, productQuotationId));
    			/*listAllConditions.add(EntityCondition.makeCondition("inputParamEnumId", EntityOperator.EQUALS, "PRIP_PRODUCT_ID"));
    			listAllConditions.add(EntityCondition.makeCondition("productPriceActionTypeId", EntityOperator.EQUALS, "PRICE_FLAT"));*/
    			try {
    	    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    	    		if (UtilValidate.isEmpty(listSortFields)) {
    	    			listSortFields.add("productCode");
    	    		}
    				listIterator = delegator.find("ProductQuotationRulesAndTax", tmpConditon, null, null, listSortFields, opts);
    			} catch (Exception e) {
    				String errMsg = "Fatal error calling jqGetListProductQuotatonRules service: " + e.toString();
    				Debug.logError(e, errMsg, module);
    			}
    		}
    	}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductQuotatonRulesByProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		try {
			opts.setDistinct(true);
			String productId = SalesUtil.getParameter(parameters, "productId");
			if (UtilValidate.isNotEmpty(productId)) {
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				listAllConditions.add(EntityCondition.makeCondition("statusId", "QUOTATION_ACCEPTED"));
				//listAllConditions.add(EntityUtil.getFilterByDateExpr());
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-fromDate");
				}
				listAllConditions.add(EntityCondition.makeCondition("productQuotationModuleTypeId", "SALES_QUOTATION"));
				listIterator = delegator.find("ProductQuotationAndPriceRCADetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductQuotatonRulesByProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListQuotationCategory(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		//Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		try {
			String productQuotationId = SalesUtil.getParameter(parameters, "productQuotationId");
			if (UtilValidate.isNotEmpty(productQuotationId)) {
				listAllConditions.add(EntityCondition.makeCondition("productQuotationId", productQuotationId));
				listIterator = delegator.find("ProductQuotationRulesCateFOL", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListQuotationCategory service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
}
