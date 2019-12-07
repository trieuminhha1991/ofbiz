package com.olbius.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.policy.SalesCommissionAdjustmentEntity;
import com.olbius.policy.SalesCommissionEntity;
import com.olbius.policy.SalesPolicyWorker;
import com.olbius.util.SalesPartyUtil;

public class SalesStatementServices {
	public static final String module = SalesStatementServices.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> updateListSalesCommissionDataStatus(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
	    Locale locale = (Locale)context.get("locale");
	    Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DAUpdatesuccess", locale));
	    String statusId = (String) context.get("statusId");
	    List<String> listItems = (List<String>) context.get("listItems[]");
	    List<String> errorMessageList = FastList.newInstance();
	    if (UtilValidate.isEmpty(statusId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DAStatusIdCannotEmpty",locale));
	    }
	    if (!"SALES_COMM_ACCEPTED".equals(statusId) && !"SALES_COMM_CANCELED".equals(statusId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DAStatusIdInvalid",locale));
	    }
	    if (UtilValidate.isNotEmpty(errorMessageList)) {
	    	return ServiceUtil.returnError(errorMessageList);
	    }
	    if (listItems != null) {
	    	try {
	    		List<GenericValue> salesCommissionDatas = delegator.findList("SalesCommissionData", EntityCondition.makeCondition("salesCommissionId", EntityOperator.IN, listItems), null, null, null, false);
	    		if (salesCommissionDatas != null) {
	    			for (GenericValue commissionDataItem : salesCommissionDatas) {
	    				commissionDataItem.put("statusId", statusId);
	    			}
	    			delegator.storeAll(salesCommissionDatas);
	    		}
	    	} catch (GenericEntityException e) {
				Debug.logError(e, "Error when run service storeSalesStatementJQ", module);
			}
	    }
	    return successResult;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String,Object> storeSalesStatementJQ(DispatchContext ctx, Map<String, Object> context){
		Delegator delegator = ctx.getDelegator();
	    Locale locale = (Locale)context.get("locale");
	    Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    GenericValue userLogin = (GenericValue) context.get("userLogin");
	    String salesStatementTypeId = (String) context.get("salesStatementTypeId");
	    String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String listItemsStr = (String) context.get("listItems");
	    String listParentRootStr = (String) context.get("listParentRoot");
	    List<Map<String, Object>> listItems = null;
	    List<String> listParentRoot = null;
	    
	    List<String> errorMessageList = FastList.newInstance();
	    if (UtilValidate.isEmpty(salesStatementTypeId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DASalesStatementTypeCannotEmpty",locale));
	    }
	    if (UtilValidate.isEmpty(customTimePeriodId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DACustomTimePeriodCannotEmpty",locale));
	    }
	    if (UtilValidate.isNotEmpty(errorMessageList)) {
	    	return ServiceUtil.returnError(errorMessageList);
	    }
	    try {
	    	listParentRootStr = listParentRootStr.replaceAll("\"", "");
	    	listItems = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listItemsStr);
	    	listParentRoot = (List<String>) JqxWidgetSevices.convert("java.util.List", listParentRootStr);
        } catch (ParseException e1) {
            return ServiceUtil.returnError(e1.toString());
        }
	    if (listItems != null) {
	    	try {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
				List<String> productIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("Product", null, null, false), "productId", true);
				List<String> categoryIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("ProductCategory", null, null, false), "productCategoryId", true);
				/*List<GenericValue> products = delegator.findByAnd("Product", null, null, false);
				List<GenericValue> categories = delegator.findByAnd("ProductCategory", null, null, false);*/
				if (customTimePeriod != null) {
					Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
					Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
					
					if (UtilValidate.isNotEmpty(listParentRoot)) {
						for (String parentRoot : listParentRoot) {
							coreStoreSalesStatementWithMapLevel(delegator, listItems, parentRoot, null, salesStatementTypeId, customTimePeriodId, fromDate, thruDate, productIds, categoryIds, userLogin, locale);
						}
					} else {
						coreStoreSalesStatementWithMapLevel(delegator, listItems, null, null, salesStatementTypeId, customTimePeriodId, fromDate, thruDate, productIds, categoryIds, userLogin, locale);
					}
					/*for (Map<String, Object> item : listItems) {
						String partyId = (String) item.get("partyId");
						if (UtilValidate.isNotEmpty(partyId)) {
							GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
							if ("PARTY_GROUP".equals(party.getString("partyTypeId"))) {
								List<GenericValue> partyManagerIds = SalesPartyUtil.getListManagerPersonByDept(delegator, partyId);
								if (UtilValidate.isNotEmpty(partyManagerIds)) {
									String partyManagerId = partyManagerIds.get(0).getString("partyId");
									coreStoreSalesStatement(delegator, userLogin, salesStatementTypeId, partyManagerId, customTimePeriodId, fromDate, thruDate, item, productIds, categoryIds, locale);
								}
							} else if ("PERSON".equals(party.getString("partyTypeId"))) {
								coreStoreSalesStatement(delegator, userLogin, salesStatementTypeId, partyId, customTimePeriodId, fromDate, thruDate, item, productIds, categoryIds, locale);
							}
						}
			    	}*/
				}
	    	} catch (GenericEntityException e) {
				Debug.logError(e, "Error when run service storeSalesStatementJQ", module);
			}
	    }
    	return successResult;
	}
	
	public static void coreStoreSalesStatementWithMapLevel(Delegator delegator, List<Map<String, Object>> listItem, String parentId, String parentSalesId, 
			String salesStatementTypeId, String customTimePeriodId, Timestamp fromDate, Timestamp thruDate, 
			List<String> productIds, List<String> categoryIds, GenericValue userLogin, Locale locale) throws GenericEntityException {
		List<Map<String, Object>> listItemRun = pushMapsFromList(listItem, "parentId", parentId);
		if (UtilValidate.isNotEmpty(listItemRun)) {
			for (Map<String, Object> item : listItemRun) {
				String partyId = (String) item.get("partyId");
				if (UtilValidate.isNotEmpty(partyId)) {
					String salesId = "";
					GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(party)) {
						// Create for both PARTY_GROUP and PERSON
						salesId = coreStoreSalesStatement(delegator, userLogin, parentSalesId, salesStatementTypeId, partyId, customTimePeriodId, fromDate, thruDate, item, productIds, categoryIds, locale);
					}
					/*if ("PARTY_GROUP".equals(party.getString("partyTypeId"))) {
						// Create for manager person
						List<GenericValue> partyManagerIds = SalesPartyUtil.getListManagerPersonByDept(delegator, partyId);
						if (UtilValidate.isNotEmpty(partyManagerIds)) {
							String partyManagerId = partyManagerIds.get(0).getString("partyId");
							salesId = coreStoreSalesStatement(delegator, userLogin, parentSalesId, salesStatementTypeId, partyManagerId, customTimePeriodId, fromDate, thruDate, item, productIds, categoryIds, locale);
						}
					} else if ("PERSON".equals(party.getString("partyTypeId"))) {
						salesId = coreStoreSalesStatement(delegator, userLogin, parentSalesId, salesStatementTypeId, partyId, customTimePeriodId, fromDate, thruDate, item, productIds, categoryIds, locale);
					}*/
					if (UtilValidate.isNotEmpty(salesId)) {
						coreStoreSalesStatementWithMapLevel(delegator, listItem, partyId, salesId, salesStatementTypeId, customTimePeriodId, fromDate, thruDate, productIds, categoryIds, userLogin, locale);
					}
				}
	    	}
		}
	}
	
	public static List<Map<String, Object>> pushMapsFromList(List<Map<String, Object>> listItem, String parentColumnName, String parentId) {
		List<Map<String, Object>> returnValue = FastList.newInstance();
		if (UtilValidate.isEmpty(listItem) || UtilValidate.isEmpty(parentColumnName)) return returnValue;
		for (Map<String, Object> mapItem : listItem) {
			if (mapItem.containsKey(parentColumnName)) {
				String value = (String) mapItem.get(parentColumnName);
				if (UtilValidate.isNotEmpty(value) && value.equals(parentId)) {
					returnValue.add(mapItem);
				}
			}
		}
		listItem.removeAll(returnValue);
		return returnValue;
	}
	
	public static String coreStoreSalesStatement(Delegator delegator, GenericValue userLogin, String parentSalesId, 
			String salesStatementTypeId, String partyId, String customTimePeriodId, Timestamp fromDate, Timestamp thruDate, 
			Map<String, Object> item, List<String> productIds, List<String> categoryIds, Locale locale) throws GenericEntityException {
		GenericValue salesStatementHeader = delegator.makeValue("SalesStatementHeader");
		salesStatementHeader.put("salesId", delegator.getNextSeqId("SalesStatementHeader"));
		salesStatementHeader.put("parentSalesId", parentSalesId);
		salesStatementHeader.put("salesTypeId", salesStatementTypeId);
		salesStatementHeader.put("statusId", "SALES_SM_CREATED");
		salesStatementHeader.put("organizationPartyId", SalesPartyUtil.getCompanyInProperties(delegator));
		salesStatementHeader.put("internalPartyId", partyId);
		salesStatementHeader.put("customTimePeriodId", customTimePeriodId);
		salesStatementHeader.put("fromDate", fromDate);
		salesStatementHeader.put("thruDate", thruDate);
		salesStatementHeader.put("createdBy", userLogin.get("userLoginId"));
		salesStatementHeader.put("modifiedBy", userLogin.get("userLoginId"));
		delegator.create(salesStatementHeader);
		String salesId = salesStatementHeader.getString("salesId");
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		Iterator<Map.Entry<String, Object>> iterator = item.entrySet().iterator();
		int itemSeqId = 1;
		while (iterator.hasNext()) {
			Map.Entry<String, Object> iItem = iterator.next();
			String key = iItem.getKey();
			/*EntityCondition iCond = EntityCondition.makeCondition("productId", key);
			EntityCondition iCond2 = EntityCondition.makeCondition("categoryId", key);
			GenericValue product = EntityUtil.getFirst(EntityUtil.filterByCondition(products, iCond));
			GenericValue category = EntityUtil.getFirst(EntityUtil.filterByCondition(categories, iCond2));*/
			//if (UtilValidate.isNotEmpty(product)) {
			if (productIds.contains(key)) {
				BigDecimal quantity = null;
				try {
		            quantity = (BigDecimal) ObjectType.simpleTypeConvert(iItem.getValue(), "BigDecimal", null, locale);
		        } catch (Exception e) {
		            Debug.logWarning(e, "Problems parsing quantity string: " + iItem.getValue(), module);
		        }
				if (quantity != null) {
					String salesItemSeqId = UtilFormatOut.formatPaddedNumber(itemSeqId, 5);
					GenericValue statementItem = delegator.makeValue("SalesStatementItem");
					statementItem.put("salesId", salesId);
					statementItem.put("salesItemSeqId", salesItemSeqId);
					statementItem.put("productId", iItem.getKey());
					statementItem.put("quantity", quantity);
					//statementItem.put("quantityUomId", product.getString("productPackingUomId"));
					statementItem.put("changeByUserLoginId", userLogin.get("userLoginId"));
					tobeStored.add(statementItem);
					itemSeqId++;
				}
			} else if (categoryIds.contains(key)) {
				//} else if (UtilValidate.isNotEmpty(category)) {
				BigDecimal quantity = null;
				try {
		            quantity = (BigDecimal) ObjectType.simpleTypeConvert(iItem.getValue(), "BigDecimal", null, locale);
		        } catch (Exception e) {
		            Debug.logWarning(e, "Problems parsing quantity string: " + iItem.getValue(), module);
		        }
				if (quantity != null) {
					String salesItemSeqId = UtilFormatOut.formatPaddedNumber(itemSeqId, 5);
					GenericValue statementItem = delegator.makeValue("SalesStatementItem");
					statementItem.put("salesId", salesId);
					statementItem.put("salesItemSeqId", salesItemSeqId);
					statementItem.put("productCategoryId", iItem.getKey());
					statementItem.put("quantity", iItem.getValue());
					statementItem.put("changeByUserLoginId", userLogin.get("userLoginId"));
					tobeStored.add(statementItem);
					itemSeqId++;
				}
			}
		}
		delegator.storeAll(tobeStored);
		return salesId;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesStatement(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("salesTypeId") && parameters.get("salesTypeId").length > 0) {
				String salesTypeId = parameters.get("salesTypeId")[0];
				if (UtilValidate.isNotEmpty(salesTypeId)) listAllConditions.add(EntityCondition.makeCondition("salesTypeId", salesTypeId));
			}
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("SalesStatementHeader", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesStatementGroup(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	opts.setDistinct(true);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("salesTypeId") && parameters.get("salesTypeId").length > 0) {
				String salesTypeId = parameters.get("salesTypeId")[0];
				if (UtilValidate.isNotEmpty(salesTypeId)) listAllConditions.add(EntityCondition.makeCondition("salesTypeId", salesTypeId));
			}
    		if (parameters.containsKey("customTimePeriodId") && parameters.get("customTimePeriodId").length > 0) {
				String customTimePeriodId = parameters.get("customTimePeriodId")[0];
				if (UtilValidate.isNotEmpty(customTimePeriodId)) listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
			}
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-fromDate");
    		}
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			listIterator = delegator.find("SalesStatementAndTimePeriod", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesStatement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> approveTargetSalesStatementGroup(DispatchContext ctx, Map<String, Object> context) {
		return changeStatusTargetSalesStatementGroup(ctx, context, "SALES_SM_ACCEPTED");
	}
	
	public static Map<String, Object> cancelTargetSalesStatementGroup(DispatchContext ctx, Map<String, Object> context) {
		return changeStatusTargetSalesStatementGroup(ctx, context, "SALES_SM_CANCELLED");
	}
	
	public static Map<String, Object> changeStatusTargetSalesStatementGroup(DispatchContext ctx, Map<String, Object> context, String statusId) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String salesTypeId = (String) context.get("salesTypeId");
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	List<String> errorMessageList = FastList.newInstance();
	    if (UtilValidate.isEmpty(salesTypeId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DASalesStatementTypeCannotEmpty",locale));
	    }
	    if (UtilValidate.isEmpty(customTimePeriodId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DACustomTimePeriodCannotEmpty",locale));
	    }
	    if (UtilValidate.isNotEmpty(errorMessageList)) {
	    	return ServiceUtil.returnError(errorMessageList);
	    }
	    List<EntityCondition> listAllConditions = FastList.newInstance();
	    EntityFindOptions opts = new EntityFindOptions();
	    opts.setDistinct(true);
	    try {
	    	listAllConditions.add(EntityCondition.makeCondition("salesTypeId", salesTypeId));
	    	listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
	    	listAllConditions.add(EntityCondition.makeCondition("statusId", "SALES_SM_CREATED"));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			List<GenericValue> listStatement = delegator.findList("SalesStatementHeader", tmpConditon, null, null, opts, false);
			if (listStatement != null) {
				for (GenericValue statementItem : listStatement) {
					statementItem.put("statusId", statusId);
				}
				delegator.storeAll(listStatement);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling approveTargetSalesStatementGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	
	/*
	public static Map<String, Object> runSalesStatementReport(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	String salesId = (String) context.get("salesId");
    	Locale locale = (Locale) context.get("locale");
    	
    	result.put("salesId", salesId);
    	GenericValue salesStatement = null;
    	try {
			salesStatement = delegator.findOne("SalesStatementHeader", UtilMisc.toMap("salesId", salesId), false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
		}
    	
    	if (salesStatement != null) {
    		List<GenericValue> listSalesStatementItem = null;
    		try {
				listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
			}
    		if (listSalesStatementItem != null) {
        		for (GenericValue ssItem : listSalesStatementItem) {
        			BigDecimal quantityActual = BigDecimal.ZERO;
        			BigDecimal amountActual = BigDecimal.ZERO;
        			try {
	        			if (ssItem.getString("productId") != null && !"".equals(ssItem.getString("productId"))) {
							// sales statement item is product. Get list orderItem buy productId, orderDate and isPromo == N
	        				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
	        				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, ssItem.getString("productId")));
	        				conditionList.add(EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "N"));
	        				conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	        				conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "ITEM_COMPLETED"));
	        				conditionList.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, salesStatement.getString("internalPartyId")));
	        				conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(salesStatement.getTimestamp("fromDate"), salesStatement.getTimestamp("thruDate"))));
							List<GenericValue> listOrderItem = delegator.findList("OrderHeaderAndItemsDetail", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
							if (listOrderItem != null) {
								for (GenericValue oItem : listOrderItem) {
									if (oItem.getString("quantity") != null && oItem.getString("unitPrice") != null) {
										quantityActual = quantityActual.add(oItem.getBigDecimal("quantity"));
										amountActual = amountActual.add(oItem.getBigDecimal("quantity").multiply(oItem.getBigDecimal("unitPrice")));
									}
								}
							}
						} else if (ssItem.getString("productCategoryId") != null && !"".equals(ssItem.getString("productCategoryId"))) {
							EntityFindOptions opts = new EntityFindOptions();
					        opts.setDistinct(true);
							List<EntityCondition> conditionListParent = new ArrayList<EntityCondition>();
							conditionListParent.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, ssItem.getString("productCategoryId")));
	        				conditionListParent.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, ssItem.getTimestamp("fromDate")));
	        				List<EntityCondition> conditionOr = new ArrayList<EntityCondition>();
	        				conditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	        				conditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, ssItem.getTimestamp("fromDate")));
	        				conditionListParent.add(EntityCondition.makeCondition(conditionOr, EntityOperator.OR));
							List<GenericValue> listProductId = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionListParent, EntityOperator.AND), UtilMisc.toSet("productId"), null, opts, false);
							for (GenericValue pItem : listProductId) {
								List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		        				conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, pItem.getString("productId")));
		        				conditionList.add(EntityCondition.makeCondition("isPromo", EntityOperator.EQUALS, "N"));
		        				conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
		        				conditionList.add(EntityCondition.makeCondition("itemStatusId", EntityOperator.EQUALS, "ITEM_COMPLETED"));
		        				conditionList.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, salesStatement.getString("internalPartyId")));
		        				conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(salesStatement.getTimestamp("fromDate"), salesStatement.getTimestamp("thruDate"))));
								List<GenericValue> listOrderItem = delegator.findList("OrderHeaderAndItemsDetail", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
								if (listOrderItem != null) {
									for (GenericValue oItem : listOrderItem) {
										if (oItem.getString("quantity") != null && oItem.getString("unitPrice") != null) {
											quantityActual.add(oItem.getBigDecimal("quantity"));
											amountActual.add(oItem.getBigDecimal("quantity").multiply(oItem.getBigDecimal("unitPrice")));
										}
									}
								}
							}
						}
        				ssItem.set("quantityActual", quantityActual);
        				ssItem.set("amountActual", amountActual);
        			} catch (GenericEntityException e) {
        				Debug.logError(e, module);
        				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
        			}
				}
    		}
    		if (listSalesStatementItem != null) {
    			try {
					delegator.storeAll(listSalesStatementItem);
					salesStatement.set("statusId", "SALES_SM_COMPLETED");
					delegator.store(salesStatement);
				} catch (GenericEntityException e) {
					Debug.logError(e, module);
    				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
				}
    		}
    	}
    	result = ServiceUtil.returnSuccess();
    	result.put("salesId", salesId);
    	return result;
    }
	 */
	
	public static Map<String, Object> runSalesStatementReportGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String salesTypeId = (String) context.get("salesTypeId");
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	//String statusId = (String) context.get("statusId");
    	String statusId = "SALES_SM_ACCEPTED";
    	successResult.put("salesTypeId", salesTypeId);
    	successResult.put("customTimePeriodId", customTimePeriodId);
    	successResult.put("statusId", statusId);
    	List<String> errorMessageList = FastList.newInstance();
	    if (UtilValidate.isEmpty(salesTypeId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DASalesStatementTypeCannotEmpty",locale));
	    }
	    if (UtilValidate.isEmpty(customTimePeriodId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DACustomTimePeriodCannotEmpty",locale));
	    }
	    if (UtilValidate.isNotEmpty(errorMessageList)) {
	    	return ServiceUtil.returnError(errorMessageList);
	    }
	    List<EntityCondition> listAllConditions = FastList.newInstance();
	    EntityFindOptions opts = new EntityFindOptions();
	    opts.setDistinct(true);
	    try {
	    	listAllConditions.add(EntityCondition.makeCondition("salesTypeId", salesTypeId));
	    	listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
	    	listAllConditions.add(EntityCondition.makeCondition("statusId", statusId));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
			List<GenericValue> listStatement = delegator.findList("SalesStatementHeader", tmpConditon, null, null, opts, false);
			if (listStatement != null) {
				for (GenericValue statementItem : listStatement) {
					Map<String, Object> resultValue = dispatcher.runSync("runSalesStatementReport", UtilMisc.toMap("salesId", statementItem.get("salesId"), "userLogin", userLogin, "locale", locale));
					if (ServiceUtil.isError(resultValue)) return resultValue;
				}
				
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling runSalesStatementReportGroup service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	
	public static Map<String, Object> runSalesStatementReport(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String salesId = (String) context.get("salesId");
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	result.put("salesId", salesId);
    	GenericValue salesStatement = null;
    	try {
			salesStatement = delegator.findOne("SalesStatementHeader", UtilMisc.toMap("salesId", salesId), false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
		}
    	if (salesStatement != null) {
    		// NVBH/PG, SUP, ASM, RSM, CSM, NBD
    		String partyId = salesStatement.getString("internalPartyId");
			try {
				String customTimePeriodId = salesStatement.getString("customTimePeriodId");
	    		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
	    		Timestamp fromDate = new Timestamp(customTimePeriod.getDate("fromDate").getTime());
	    		Timestamp thruDate = new Timestamp(customTimePeriod.getDate("thruDate").getTime());
	    		List<EntityCondition> listAllCondition = new ArrayList<EntityCondition>();
	    		listAllCondition.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				//listAllCondition.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, salesStatement.getString("internalPartyId")));
				listAllCondition.add(EntityCondition.makeCondition("orderDate", EntityOperator.BETWEEN, UtilMisc.toList(fromDate, thruDate)));
				
				if ("SALES_IN".equals(salesStatement.getString("salesTypeId"))) {
					// calculate for NPP, ASM, ...
	    			
				} else if ("SALES_OUT".equals(salesStatement.getString("salesTypeId"))) {
					// calculate for NVBH/PG, SUP, ASM, ...
					GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(party)) {
						if ("PARTY_GROUP".equals(party.getString("partyTypeId"))) {
							List<String> supIds = null;
							if (SalesPartyUtil.isSupervisorDept(partyId, delegator)) {
								supIds = EntityUtil.getFieldListFromEntityList(SalesPartyUtil.getListManagerPersonByDept(delegator, partyId), "partyIdFrom", true);
							} else if (SalesPartyUtil.isAsmDept(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByAsmDept(delegator, partyId);
							} else if (SalesPartyUtil.isRsmDept(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByRsmDept(delegator, partyId);
							} else if (SalesPartyUtil.isCsmDept(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByCsmDept(delegator, partyId);
							} else if (SalesPartyUtil.isNbdDept(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByNbdDept(delegator, partyId);
							}
							if (supIds != null) {
								List<String> orderIds = getListOrderIdBySupPersonIds(delegator, supIds, listAllCondition);
								if (UtilValidate.isNotEmpty(orderIds)) {
									Map<String, Object> resultValue = calsulateStatementItem(delegator, dispatcher, locale, userLogin, salesStatement, orderIds);
									if (resultValue != null) {
										return resultValue;
									}
								}
							}
						} else if ("PERSON".equals(party.getString("partyTypeId"))) {
							List<String> supIds = null;
							if (SalesPartyUtil.isSalesmanEmployee(partyId, delegator)) {
								String salesmanRoleId = EntityUtilProperties.getPropertyValue(SalesPartyUtil.RESOURCE_DL, SalesPartyUtil.RSN_PRTROLE_SALESMAN_DL, delegator);
								List<String> salesmanRoleIds = SalesPartyUtil.getListDescendantRoleInclude(salesmanRoleId, delegator);
								listAllCondition.add(EntityCondition.makeCondition("partyId", partyId));
								listAllCondition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, salesmanRoleIds));
								listAllCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_COMPLETED")));
								List<String> orderIds = EntityUtil.getFieldListFromEntityList(
										delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false), 
										"orderId", true);
								Map<String, Object> resultValue = calsulateStatementItem(delegator, dispatcher, locale, userLogin, salesStatement, orderIds);
								if (resultValue != null) {
									return resultValue;
								}
							} else if (SalesPartyUtil.isSupervisorEmployee(partyId, delegator)) {
								String supRoleId = EntityUtilProperties.getPropertyValue(SalesPartyUtil.RESOURCE_DL, SalesPartyUtil.RSN_PRTROLE_SUP_DL, delegator);
								List<String> supRoleIds = SalesPartyUtil.getListDescendantRoleInclude(supRoleId, delegator);
								listAllCondition.add(EntityCondition.makeCondition("partyId", partyId));
								listAllCondition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, supRoleIds));
								listAllCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_COMPLETED")));
								List<String> orderIds = EntityUtil.getFieldListFromEntityList(
										delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false), 
										"orderId", true);
								Map<String, Object> resultValue = calsulateStatementItem(delegator, dispatcher, locale, userLogin, salesStatement, orderIds);
								if (resultValue != null) {
									return resultValue;
								}
							} else if (SalesPartyUtil.isAsmEmployee(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByAsm(delegator, partyId);
							} else if (SalesPartyUtil.isRsmEmployee(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByRsm(delegator, partyId);
							} else if (SalesPartyUtil.isCsmEmployee(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByCsm(delegator, partyId);
							} else if (SalesPartyUtil.isNbdEmployee(partyId, delegator)) {
								supIds = SalesPartyUtil.getListSupPersonIdByNbd(delegator, partyId);
							}
							if (supIds != null) {
								List<String> orderIds = getListOrderIdBySupPersonIds(delegator, supIds, listAllCondition);
								if (UtilValidate.isNotEmpty(orderIds)) {
									Map<String, Object> resultValue = calsulateStatementItem(delegator, dispatcher, locale, userLogin, salesStatement, orderIds);
									if (resultValue != null) {
										return resultValue;
									}
								}
							}
						}
					}
					
					
					// TODO run with other permission
					salesStatement.set("statusId", "SALES_SM_COMPLETED");
					delegator.store(salesStatement);
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToRun", new Object[] {e.getMessage()}, locale));
			}
    	}
    	result = ServiceUtil.returnSuccess();
    	result.put("salesId", salesId);
    	return result;
    }
	
	public static List<String> getListOrderIdBySupPersonIds(Delegator delegator, List<String> supPersonIds, List<EntityCondition> listAllCondition) throws GenericEntityException {
		if (supPersonIds == null) return null;
		List<String> orderIds = FastList.newInstance();
		String supRoleId = EntityUtilProperties.getPropertyValue(SalesPartyUtil.RESOURCE_DL, SalesPartyUtil.RSN_PRTROLE_SUP_DL, delegator);
		List<String> supRoleIds = SalesPartyUtil.getListDescendantRoleInclude(supRoleId, delegator);
		listAllCondition.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, supRoleIds));
		listAllCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("ORDER_APPROVED", "ORDER_COMPLETED")));
		
		for (String supId : supPersonIds) {
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.addAll(listAllCondition);
			listCond.add(EntityCondition.makeCondition("partyId", supId));
			List<String> tmpOrderIds = EntityUtil.getFieldListFromEntityList(
					delegator.findList("OrderHeaderAndRoles", EntityCondition.makeCondition(listCond, EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false), 
					"orderId", true);
			if (tmpOrderIds != null) orderIds.addAll(tmpOrderIds);
		}
		return orderIds;
	}
	
	public static Map<String, Object> calsulateStatementItem(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue userLogin, 
			GenericValue salesStatement, List<String> orderIds) {
		Map<String, Object> resultValue = null;
		List<GenericValue> listSalesStatementItem = null;
		List<EntityCondition> listAllCondition = FastList.newInstance();
		try {
			listSalesStatementItem = delegator.findByAnd("SalesStatementItem", UtilMisc.toMap("salesId", salesStatement.getString("salesId")), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
		}
		listAllCondition.add(EntityCondition.makeCondition("isPromo", "N"));
		listAllCondition.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
		listAllCondition.add(EntityCondition.makeCondition("statusId", "ITEM_COMPLETED"));
		if (listSalesStatementItem != null) {
    		for (GenericValue ssItem : listSalesStatementItem) {
    			BigDecimal quantityActual = BigDecimal.ZERO;
    			BigDecimal amountActual = BigDecimal.ZERO;
    			try {
        			if (ssItem.getString("productId") != null && !"".equals(ssItem.getString("productId"))) {
						// sales statement item is product. Get list orderItem buy productId, orderDate and isPromo == N
        				List<EntityCondition> listCond = FastList.newInstance();
        				listCond.addAll(listAllCondition);
        				listCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, ssItem.getString("productId")));
						List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
						if (listOrderItem != null) {
							Map<String, Object> returnValue = calculateQuantityAndAmountInOrderItems(delegator, dispatcher, userLogin, quantityActual, amountActual, listOrderItem);
							quantityActual = (BigDecimal) returnValue.get("quantityActual");
							amountActual = (BigDecimal) returnValue.get("amountActual");
						}
					} else if (ssItem.getString("productCategoryId") != null && !"".equals(ssItem.getString("productCategoryId"))) {
						EntityFindOptions opts = new EntityFindOptions();
				        opts.setDistinct(true);
						List<EntityCondition> conditionListParent = new ArrayList<EntityCondition>();
						conditionListParent.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, ssItem.getString("productCategoryId")));
        				conditionListParent.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, ssItem.getTimestamp("fromDate")));
        				List<EntityCondition> conditionOr = new ArrayList<EntityCondition>();
        				conditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
        				conditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, ssItem.getTimestamp("fromDate")));
        				conditionListParent.add(EntityCondition.makeCondition(conditionOr, EntityOperator.OR));
						List<GenericValue> listProductId = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(conditionListParent, EntityOperator.AND), UtilMisc.toSet("productId"), null, opts, false);
						for (GenericValue pItem : listProductId) {
							List<EntityCondition> listCond = FastList.newInstance();
	        				listCond.addAll(listAllCondition);
	        				listCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, pItem.getString("productId")));
							List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
							if (listOrderItem != null) {
								Map<String, Object> returnValue = calculateQuantityAndAmountInOrderItems(delegator, dispatcher, userLogin, quantityActual, amountActual, listOrderItem);
								quantityActual = (BigDecimal) returnValue.get("quantityActual");
								amountActual = (BigDecimal) returnValue.get("amountActual");
							}
						}
					}
    				ssItem.set("quantityActual", quantityActual);
    				ssItem.set("amountActual", amountActual);
    			} catch (GenericEntityException e) {
    				Debug.logError(e, module);
    				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
    			} catch (GenericServiceException e) {
    				Debug.logError(e, module);
    				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToConvertQuantity", new Object[] {e.getMessage()}, locale));
				}
			}
    		try {
				delegator.storeAll(listSalesStatementItem);
				/*salesStatement.set("statusId", "SALES_SM_COMPLETED");
				delegator.store(salesStatement);*/
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAFailedToGetSalesStatement", new Object[] {e.getMessage()}, locale));
			}
		}
		return resultValue;
	}
    
    public static Map<String, Object> calculateQuantityAndAmountInOrderItems(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, BigDecimal quantityActual, BigDecimal amountActual, List<GenericValue> listOrderItem) throws GenericEntityException, GenericServiceException{
    	Map<String, Object> returnValue = FastMap.newInstance();
    	for (GenericValue oItem : listOrderItem) {
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", oItem.get("productId")), true);
            if (UtilValidate.isNotEmpty(oItem.getBigDecimal("alternativeUnitPrice")) && UtilValidate.isNotEmpty(oItem.getBigDecimal("alternativeQuantity"))) {
            	BigDecimal quantityConvert = BigDecimal.ONE;
            	if (product != null) {
            		String quantityUomIdDefault = product.getString("productPackingUomId");
            		String quantityUomIdOrder = oItem.getString("quantityUomId");
            		if (UtilValidate.isNotEmpty(quantityUomIdOrder) && UtilValidate.isNotEmpty(quantityUomIdDefault)) {
            			Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", oItem.get("productId"), "uomFromId", quantityUomIdOrder, "uomToId", quantityUomIdDefault, "userLogin", userLogin));
						if (ServiceUtil.isSuccess(resultValue)) {
							quantityConvert = (BigDecimal) resultValue.get("convertNumber");
							if (quantityConvert.compareTo(BigDecimal.ZERO) == 0) {
								quantityConvert = BigDecimal.ONE;
							}
						}
            		}
            	}
            	quantityActual = quantityActual.add(oItem.getBigDecimal("alternativeQuantity").divide(quantityConvert, 2, RoundingMode.HALF_UP));
				amountActual = amountActual.add(oItem.getBigDecimal("alternativeQuantity").multiply(oItem.getBigDecimal("alternativeUnitPrice")));
            } else if (oItem.getString("quantity") != null && oItem.getString("unitPrice") != null){
            	BigDecimal quantityConvert = BigDecimal.ONE;
            	if (product != null) {
            		String quantityUomIdDefault = product.getString("quantityUomId");
            		String quantityUomIdOrder = oItem.getString("quantityUomId");
            		if (UtilValidate.isNotEmpty(quantityUomIdOrder) && UtilValidate.isNotEmpty(quantityUomIdDefault)) {
            			Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", oItem.get("productId"), "uomFromId", quantityUomIdOrder, "uomToId", quantityUomIdDefault, "userLogin", userLogin));
						if (ServiceUtil.isSuccess(resultValue)) {
							quantityConvert = (BigDecimal) resultValue.get("convertNumber");
							if (quantityConvert.compareTo(BigDecimal.ZERO) == 0) {
								quantityConvert = BigDecimal.ONE;
							}
						}
            		}
            	}
            	quantityActual = quantityActual.add(oItem.getBigDecimal("quantity").divide(quantityConvert, 2, RoundingMode.HALF_UP));
				amountActual = amountActual.add(oItem.getBigDecimal("quantity").multiply(oItem.getBigDecimal("unitPrice")));
            }
		}
    	returnValue.put("quantityActual", quantityActual);
    	returnValue.put("amountActual", amountActual);
    	return returnValue;
    }
    
    /*
     * @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListDistributorByCustomer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	//List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	
    	String partyId = null;
    	String noUserlogin = null;
    	opts.setDistinct(true);
    	try {
    		// get list distributor
    		listIterator = SalesPartyUtil.getIteratorDistributorByCustomer(delegator, partyId, listAllConditions, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListDistributorByCustomer service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
     */
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesCommissionData(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0) {
				String statusId = parameters.get("statusId")[0];
				if (UtilValidate.isNotEmpty(statusId)) listAllConditions.add(EntityCondition.makeCondition("statusId", statusId));
			}
			listIterator = delegator.find("SalesCommissionData", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListSalesCommissionData service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesCommissionLogRun(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		
		try {
			listIterator = delegator.find("SalesCommissionLogRun", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListSalesCommissionLogRun service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    
    public static Map<String, Object> storeCommissionCalculate(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "DAUpdatesuccess", locale));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String roleTypeId = (String) context.get("roleTypeId");
		String fromDateStr = (String) context.get("fromDate");
		String thruDateStr = (String) context.get("thruDate");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		
		List<String> errorMessageList = FastList.newInstance();
		if (UtilValidate.isEmpty(roleTypeId)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DARoleTypeCannotEmpty",locale));
	    }
		if (UtilValidate.isEmpty(fromDateStr)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DAFromDateCannotEmpty",locale));
	    }
		if (UtilValidate.isEmpty(thruDateStr)) {
	    	errorMessageList.add(UtilProperties.getMessage(resource_error, "DAThruDateCannotEmpty",locale));
	    }
		try {
        	Long fromDateL = Long.parseLong(fromDateStr);
        	fromDate = new Timestamp(fromDateL);
        	Long thruDateL = Long.parseLong(thruDateStr);
        	thruDate = new Timestamp(thruDateL);
        } catch (Exception e) {
        	errorMessageList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        	Debug.logWarning(e, "Problems parsing date string: " + fromDateStr + " and " + thruDateStr, module);
        }
	    if (UtilValidate.isNotEmpty(errorMessageList)) {
	    	return ServiceUtil.returnError(errorMessageList);
	    }
		
	    List<SalesCommissionEntity> listSalesCommission = SalesPolicyWorker.getSalesCommissionList2(delegator, locale, roleTypeId, null, fromDate, thruDate);
	    List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		if (listSalesCommission != null) {
			// Sales Commission Log Run
			GenericValue salesCommissionLogRun = delegator.makeValue("SalesCommissionLogRun");
			String commissionLogId = delegator.getNextSeqId("SalesCommissionLogRun");
			salesCommissionLogRun.put("commissionLogId", commissionLogId);
			salesCommissionLogRun.put("roleTypeId", roleTypeId);
			salesCommissionLogRun.put("fromDate", fromDate);
			salesCommissionLogRun.put("thruDate", thruDate);
			salesCommissionLogRun.put("createdDate", UtilDateTime.nowTimestamp());
			salesCommissionLogRun.put("createdBy", userLogin.get("userLoginId"));
			salesCommissionLogRun.put("countRowAction", new BigDecimal(listSalesCommission.size()));
			tobeStored.add(salesCommissionLogRun);
			
			// Sales Commission Data
			for (SalesCommissionEntity commissionData : listSalesCommission) {
				GenericValue salesCommissionData = delegator.makeValidValue("SalesCommissionData");
				String salesCommissionId = delegator.getNextSeqId("SalesCommissionData");
				salesCommissionData.put("salesCommissionId", salesCommissionId);
				salesCommissionData.put("partyId", commissionData.getPartyId());
				salesCommissionData.put("salesStatementId", commissionData.getSalesStatementId());
				salesCommissionData.put("amount", commissionData.getAmount());
				salesCommissionData.put("fromDate", commissionData.getFromDate());
				salesCommissionData.put("thruDate", commissionData.getThruDate());
				salesCommissionData.put("hasQuantity", commissionData.isHasQuantity());
				salesCommissionData.put("statusId", "SALES_COMM_CREATED");
				tobeStored.add(salesCommissionData);
				
				// Sales Commission Log Run Detail
				GenericValue salesCommissionLogRunDetail = delegator.makeValue("SalesCommissionLogRunDetail");
				salesCommissionLogRunDetail.put("commissionLogId", commissionLogId);
				salesCommissionLogRunDetail.put("salesCommissionId", salesCommissionId);
				tobeStored.add(salesCommissionLogRunDetail);
				
				// Sales Commission Adjustment
				List<SalesCommissionAdjustmentEntity> listAdjustment = commissionData.getListSalesCommissionsAdj();
				if (listAdjustment != null) {
					for (SalesCommissionAdjustmentEntity adjustment : listAdjustment) {
						GenericValue salesCommissionAdjustment = delegator.makeValue("SalesCommissionAdjustment");
						salesCommissionAdjustment.put("salesCommissionId", salesCommissionId);
						salesCommissionAdjustment.put("salesPolicyId", adjustment.getSalesPolicyId());
						salesCommissionAdjustment.put("salesPolicyRuleId", adjustment.getSalesPolicyRuleId());
						salesCommissionAdjustment.put("salesPolicyActionSeqId", adjustment.getSalesPolicyActionSeqId());
						salesCommissionAdjustment.put("amount", adjustment.getAmount());
						salesCommissionAdjustment.put("quantity", adjustment.getQuantity());
						salesCommissionAdjustment.put("productId", adjustment.getProductId());
						salesCommissionAdjustment.put("productCategoryId", adjustment.getCategoryId());
						salesCommissionAdjustment.put("description", adjustment.getDescription());
						tobeStored.add(salesCommissionAdjustment);
					}
				}
			}
			
			if (tobeStored != null) {
				try {
					delegator.storeAll(tobeStored);
				} catch (GenericEntityException e) {
					Debug.logError(e, "Problem with order storage or reservations", module);
		            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DAErrorCouldNotCreateSalesCommissionDataWriteError",locale) + e.getMessage() + ").");
				}
			}
		}
	    
		return successResult;
	}
}
