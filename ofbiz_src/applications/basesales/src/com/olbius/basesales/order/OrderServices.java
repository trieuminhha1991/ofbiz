package com.olbius.basesales.order;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.imagemanagement.ImageManagementServices;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.order.OrderDuplicateServices;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.product.ProductStoreWorker;
import com.olbius.basesales.util.NotificationUtil;
import com.olbius.basesales.util.NotificationWorker;
import com.olbius.basesales.util.ProcessConditionUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesPartyUtil.EmplRoleEnum;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

public class OrderServices {
	public static final String module = OrderServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
    public static final int orderDecimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int orderRounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    
    private static int pricePODecimals = UtilNumber.getBigDecimalScale("supplierprice.decimals");
    private static int pricePORounding = UtilNumber.getBigDecimalRoundingMode("supplierprice.rounding");
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesOrderMap(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		boolean isRoleDistributor = false;
    		boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESORDER");
    		if (!hasPermission) {
    			hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PURCHORDER_VIEW");
    			isRoleDistributor = true;
    		}
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            //return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			String userLoginPartyId = userLogin.getString("partyId");
			
			// only get sales orders
			listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			
			// check status of order
			String statusId = SalesUtil.getParameter(parameters, "_statusId");
			if (UtilValidate.isNotEmpty(statusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
			}
			//listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null));
			
    		// check sales method channel enumeration of order
			String channelCode = SalesUtil.getParameter(parameters, "cn");
			if (UtilValidate.isNotEmpty(channelCode)) {
				String salesMethodChannelEnumId = null;
				if ("ts".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.telesales");
				} else if ("ps".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.pos");
				} else if ("ec".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.ecommerce");
				}
				if (salesMethodChannelEnumId != null) {
					listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", salesMethodChannelEnumId));
				}
			}
			
			// check customer of order
			String partyId = SalesUtil.getParameter(parameters, "partyId");
			if (!isRoleDistributor) {
				if (SalesUtil.propertyValueEqualsIgnoreCase("get.order.by.created.by", "true")) {
					// get order by employee created
					if (!SalesPartyUtil.isCallCenterManager(delegator, userLoginPartyId) && SalesPartyUtil.isCallCenter(delegator, userLoginPartyId)) {
						// user login is a CallCenter employee
						String ia = SalesUtil.getParameter(parameters, "ia"); // for case view list order from call in/out screens
		    			if (!"Y".equals(ia)) {
		    				listAllConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginPartyId));
		    			}
					}
				} else {
					if (!SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) 
							&& !SalesPartyUtil.isCallCenterManager(delegator, userLoginPartyId)
							&& !SalesPartyUtil.isCallCenter(delegator, userLoginPartyId)) {
						
						if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
							List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLoginPartyId, false), "productStoreId", true);
							listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
						} else if (SalesPartyUtil.isSalesEmployee(delegator, userLoginPartyId)) {
							List<String> distributorIds = PartyWorker.getDistributorInOrgByManager(delegator, userLoginPartyId);
							listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, distributorIds));
							if (!distributorIds.contains(partyId)) partyId = null;
						}
					}
				}
			} else {
				// customer is user login
				partyId = userLoginPartyId;
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
			}
			
			// check seller of order
			String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			boolean searchable = false;
			if (organizationId != null || UtilValidate.isNotEmpty(partyId)) {
				if (organizationId != null) listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
				searchable = true;
			}
			
			// process
			if (searchable) {
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-orderDate");
					//listSortFields.add("priority");
				}
				Set<String> listSelectFields = FastSet.newInstance();
				listSelectFields.add("orderDate");
				listSelectFields.add("orderId");
				listSelectFields.add("orderName");
				listSelectFields.add("estimatedDeliveryDate");
				listSelectFields.add("shipBeforeDate");
				listSelectFields.add("shipAfterDate");
				listSelectFields.add("customerId");
				listSelectFields.add("customerCode");
				listSelectFields.add("customerFullName");
				listSelectFields.add("fullContactNumber");
				listSelectFields.add("productStoreId");
				listSelectFields.add("grandTotal");
				listSelectFields.add("statusId");
				listSelectFields.add("currencyUom");
				listSelectFields.add("agreementId");
				listSelectFields.add("agreementCode");
				listSelectFields.add("priority");
				listSelectFields.add("isFavorDelivery");
				listSelectFields.add("createdBy");
				
				listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
				listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);
				
				String tableName = "OrderHeaderFullView";
				String productPromoId = SalesUtil.getParameter(parameters, "productPromoId");
				if (productPromoId != null) {
					listAllConditions.add(EntityCondition.makeCondition("productPromoId", productPromoId));
					tableName = "PromoOrderHeaderFullView";
				}
				
				/*EntityCondition mainCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
				Long totalRows = delegator.findCountByCondition(tableName, mainCond, null, null);
				successResult.put("TotalRows", totalRows.toString());
				
				String viewIndexStr = (String) parameters.get("pagenum")[0];
		    	String viewSizeStr = (String) parameters.get("pagesize")[0];
		    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
		    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
		    	int viewOffset = viewIndex * viewSize;
		    	
				opts.setLimit(viewSize);
				opts.setOffset(viewOffset);
				listIterator = delegator.findList(tableName, mainCond, listSelectFields, listSortFields, opts, false);*/
				listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, tableName, 
						EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSelectFields, listSortFields, opts);
			}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    // Retail outlet's order list
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesOrderExternal(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Security security = ctx.getSecurity();
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "DIS_SALESORDER_VIEW");
    		if (!hasPermission) {
    			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
    			//return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
    			return successResult;
    		}
    		String userLoginPartyId = userLogin.getString("partyId");
    		
    		String partyId = null;
    		String statusId = null;
    		if (parameters.containsKey("_statusId") && parameters.get("_statusId").length > 0) {
    			statusId = parameters.get("_statusId")[0];
    		}
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			partyId = parameters.get("partyId")[0];
    		}
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		if (UtilValidate.isNotEmpty(partyId)) {
    			listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
    		}
    		if (UtilValidate.isNotEmpty(statusId)) {
    			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    		}
    		if (UtilValidate.isEmpty(listSortFields)) {
    			listSortFields.add("-orderDate");
    			listSortFields.add("priority");
    		}
    		String organizationId = null;
    		boolean searchable = false;
    		
    		boolean isEmployee = SalesPartyUtil.isEmployee(delegator, userLoginPartyId);
    		if (isEmployee) {
    			if (SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
    				searchable = true;
    				listAllConditions.add(EntityCondition.makeCondition("sellerTypeId", EntityOperator.NOT_EQUAL, "LEGAL_ORGANIZATION"));
    			} else {
    				//List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, false), "productStoreId", true);
    				if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
    					List<String> distIds = PartyWorker.getDistributorByASM(delegator, userLoginPartyId);
    					if (UtilValidate.isNotEmpty(distIds)) {
    						searchable = true;
    						listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, distIds));
    					}
    				} else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
    					List<String> distIds = PartyWorker.getDistributorByRSM(delegator, userLoginPartyId);
    					if (UtilValidate.isNotEmpty(distIds)) {
    						searchable = true;
    						listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, distIds));
    					}
    				} else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
    					List<String> distIds = PartyWorker.getDistributorByCSM(delegator, userLoginPartyId);
    					if (UtilValidate.isNotEmpty(distIds)) {
    						searchable = true;
    						listAllConditions.add(EntityCondition.makeCondition("sellerId", EntityOperator.IN, distIds));
    					}
    				} else {
    					List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLoginPartyId, false), "productStoreId", false);
    					if (UtilValidate.isNotEmpty(productStoreIds)) {
        					if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
        						//List<String> retailOutletIds = PartyWorker.getCustomerIdsBySalesExecutive(delegator, userLoginPartyId);
        						List<String> retailOutletIds = PartyWorker.getCustomerIdsBySalesmanId(delegator, userLoginPartyId);
            					if (UtilValidate.isNotEmpty(retailOutletIds)) {
            						searchable = true;
            						listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
            						listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, retailOutletIds));
            					}
        					} else {
        						String deptId = null;
        						List<String> deptIds = PartyWorker.getOrgByManager(delegator, userLoginPartyId);
        						if (deptIds != null) {
        							deptId = deptIds.get(0);
        						}
        						if (deptId != null) {
        							//List<String> productStoreIdsTmp = getListProductStoreIdsByEmpl(delegator, userLoginPartyId);
        							List<String> customerIdsOfSup = getListCustomerOfSup(delegator, userLoginPartyId);
    	    						/*if (UtilValidate.isNotEmpty(productStoreIdsTmp)) {
    	    							searchable = true;
    		    						listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsTmp));
    	    						}*/
        							if (UtilValidate.isNotEmpty(customerIdsOfSup)) {
    	    							searchable = true;
    		    						listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerIdsOfSup));
    	    						}
        						}
        					}
        				} else {
        					List<String> productStoreIdsTmp = getListProductStoreIdsByEmpl(delegator, userLoginPartyId);
    						if (UtilValidate.isNotEmpty(productStoreIdsTmp)) {
    							searchable = true;
        						listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIdsTmp));
    						}
        				}
    				}
    			}
			} else {
				organizationId = userLoginPartyId;
			}
    		if (!searchable && (organizationId != null || UtilValidate.isNotEmpty(partyId))) {
    			if (organizationId != null) listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
    			searchable = true;
    		}
    		if (searchable) {
    			listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
    			listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);
    			
    			listIterator = delegator.find("OrderHeaderFullView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListSalesOrderExternal service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    private static List<String> getListProductStoreIdsByEmpl(Delegator delegator, String partyId) throws GenericEntityException {
    	List<String> productStoreIds = new ArrayList<String>();
    	List<String> distributorIds = PartyWorker.getDistributorInOrgByManager(delegator, partyId);
		if (UtilValidate.isNotEmpty(distributorIds)) {
			productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStore", EntityCondition.makeCondition("payToPartyId", EntityOperator.IN, distributorIds), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", false);
		}
		return productStoreIds;
    }
    private static List<String> getListCustomerOfSup(Delegator delegator, String partyId) throws GenericEntityException{
    	List<GenericValue> partyDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", partyId), null, null, null, false);
    	return EntityUtil.getFieldListFromEntityList(partyDistributors, "partyId", true);
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesOrderReturnable(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	// LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	// Locale locale = (Locale) context.get("locale");
    	Security security = ctx.getSecurity();
    	OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
    		boolean isRoleDistributor = false;
    		boolean isRoleEmployee = securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESORDER");
    		boolean isRoleSalesman = securityOlb.olbiusHasPermission(userLogin, "VIEW", "MODULE", "SALESMAN_RETURNORDER");
    		boolean hasPermission = isRoleEmployee;
    		boolean hasPermissionSalesman = isRoleSalesman;
    		if (!hasPermission && !hasPermissionSalesman) {
    			hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PURCHORDER_VIEW");
    			isRoleDistributor = true;
    		}
    		if (!hasPermission && !hasPermissionSalesman) {
    			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
    			//return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
    			return successResult;
    		}
    		if(hasPermission){
				String userLoginPartyId = userLogin.getString("partyId");
    		/*Map<String, Object> tmpResult = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
    		if (ServiceUtil.isError(tmpResult)) return tmpResult;
    		List<GenericValue> listStore = (List<GenericValue>) tmpResult.get("listProductStore");
    		List<String> productStoreIds = null;
    		if (UtilValidate.isNotEmpty(listStore)) {
    			productStoreIds = EntityUtil.getFieldListFromEntityList(listStore, "productStoreId", true);
    		}*/

				boolean isEmployee = SalesPartyUtil.isEmployee(delegator, userLoginPartyId);
				if (isEmployee) {
					if (SalesUtil.propertyValueEqualsIgnoreCase("get.order.by.created.by", "true")) {
						boolean isGetAll = false;
						if (parameters.containsKey("ia") && parameters.get("ia").length > 0) {
							String ia = parameters.get("ia")[0];
							if ("Y".equals(ia)) {
								isGetAll = true;
							}
						}
						if (!isGetAll) {
							boolean isGetByCreatedBy = false;
							if (SalesPartyUtil.isCallCenterManager(delegator, userLoginPartyId)) {
								isGetByCreatedBy = false;
							} else if (SalesPartyUtil.isCallCenter(delegator, userLoginPartyId)) {
								isGetByCreatedBy = true;
							}
							if (isGetByCreatedBy) {
								listAllConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginPartyId));
							}
						}
					} else {
						if (SalesPartyUtil.isSalesEmployee(delegator, userLoginPartyId)) {
							List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLoginPartyId, false), "productStoreId", true);
							listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
						}
					}
				}

				String partyId = null;
				String channelCode = null;
				if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
					partyId = parameters.get("partyId")[0];
				}
				if (parameters.containsKey("cn") && parameters.get("cn").length > 0) {
					channelCode = parameters.get("cn")[0];
				}
				if (isRoleDistributor) {
					partyId = userLoginPartyId;
				}
				if (UtilValidate.isNotEmpty(partyId)) {
					listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
				}

				listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				listAllConditions.add(EntityCondition.makeCondition("quantityRemain", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				if (UtilValidate.isNotEmpty(channelCode)) {
					String salesMethodChannelEnumId = null;
					if ("ts".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.telesales");
					} else if ("ps".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.pos");
					} else if ("ec".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.ecommerce");
					}
					if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
						listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", salesMethodChannelEnumId));
					}
				}
				//listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.NOT_EQUAL, "BHKENH_POS"));
    		/*if (UtilValidate.isNotEmpty(productStoreIds)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
		        listIterator = delegator.find("OrderHeaderFullView", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}*/
				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				boolean searchable = false;
				if (organizationId != null || UtilValidate.isNotEmpty(partyId)) {
					if (organizationId != null) listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
					searchable = true;
				}
				if (searchable) {
					if (UtilValidate.isEmpty(listSortFields)) {
						listSortFields.add("-orderDate");
						listSortFields.add("priority");
					}

					listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
					listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);

					listIterator = delegator.find("OrderHeaderReturnable", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
				}
			}
    		// list SalesOrderReturnable by Salesman
    		if(hasPermissionSalesman){
				String userLoginPartyId = userLogin.getString("partyId");

				boolean isEmployee = SalesPartyUtil.isEmployee(delegator, userLoginPartyId);
				if (isEmployee) {
					if (SalesUtil.propertyValueEqualsIgnoreCase("get.order.by.created.by", "true")) {
						boolean isGetAll = false;
						if (parameters.containsKey("ia") && parameters.get("ia").length > 0) {
							String ia = parameters.get("ia")[0];
							if ("Y".equals(ia)) {
								isGetAll = true;
							}
						}
						if (!isGetAll) {
							boolean isGetByCreatedBy = false;
							if (SalesPartyUtil.isCallCenterManager(delegator, userLoginPartyId)) {
								isGetByCreatedBy = false;
							} else if (SalesPartyUtil.isCallCenter(delegator, userLoginPartyId)) {
								isGetByCreatedBy = true;
							}
							if (isGetByCreatedBy) {
								listAllConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginPartyId));
							}
						}
					} else {
						if (SalesPartyUtil.isSalesEmployee(delegator, userLoginPartyId)) {
							List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreView(delegator, userLogin, userLoginPartyId, false), "productStoreId", true);
							listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
						}
					}
				}

				String partyId = null;
				String channelCode = null;
				if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
					partyId = parameters.get("partyId")[0];
				}
				if (parameters.containsKey("cn") && parameters.get("cn").length > 0) {
					channelCode = parameters.get("cn")[0];
				}
				List<String> customerIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("PartyCustomer", EntityCondition.makeCondition(UtilMisc.toMap("salesmanId", userLoginPartyId)), null, null, null, false), "partyId", false);
				listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, customerIds));

				listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				listAllConditions.add(EntityCondition.makeCondition("quantityRemain", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
				if (UtilValidate.isNotEmpty(channelCode)) {
					String salesMethodChannelEnumId = null;
					if ("ts".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.telesales");
					} else if ("ps".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.pos");
					} else if ("ec".equals(channelCode)) {
						salesMethodChannelEnumId = SalesUtil.getPropertyValue(delegator, "sales.method.channel.enum.id.ecommerce");
					}
					if (UtilValidate.isNotEmpty(salesMethodChannelEnumId)) {
						listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", salesMethodChannelEnumId));
					}
				}
				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				boolean searchable = false;
				if (organizationId != null || UtilValidate.isNotEmpty(partyId)) {
					//if (organizationId != null) listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
					searchable = true;
				}
				if (searchable) {
					if (UtilValidate.isEmpty(listSortFields)) {
						listSortFields.add("-orderDate");
						listSortFields.add("priority");
					}

					listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
					listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);

					listIterator = delegator.find("OrderHeaderReturnable", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
				}
			}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListSalesOrder service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
	// Retail outlet's order list
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListSalesOrderExternalReturnable(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	// LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	// Locale locale = (Locale) context.get("locale");
    	Security security = ctx.getSecurity();
    	OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission
    		boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PURCHORDER_VIEW");
    		if (!hasPermission) {
    			Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
    			//return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
    			return successResult;
    		}
    		
    		String userLoginPartyId = userLogin.getString("partyId");
    		boolean isEmployee = SalesPartyUtil.isEmployee(delegator, userLoginPartyId);
    		
    		String partyId = null;
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			partyId = parameters.get("partyId")[0];
    		}
    		if (UtilValidate.isNotEmpty(partyId)) {
    			listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
    		}
    		
    		listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
    		listAllConditions.add(EntityCondition.makeCondition("quantityRemain", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
    		
    		String organizationId = null;
    		boolean searchable = false;
			if (isEmployee) {
				List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(ProductStoreWorker.getListProductStoreSell(delegator, userLogin, userLoginPartyId, false), "productStoreId", true);
				if (UtilValidate.isNotEmpty(productStoreIds)) {
					List<String> retailOutletIds = PartyWorker.getCustomerIdsBySalesExecutive(delegator, userLoginPartyId);
					if (UtilValidate.isNotEmpty(retailOutletIds)) {
						searchable = true;
						listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
						listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.IN, retailOutletIds));
					}
				}
			} else {
				organizationId = userLoginPartyId;
				searchable = true;
			}
    		if (searchable) {
    			if (organizationId != null) listAllConditions.add(EntityCondition.makeCondition("sellerId", organizationId));
    			
    			if (UtilValidate.isEmpty(listSortFields)) {
        			listSortFields.add("-orderDate");
        			listSortFields.add("priority");
        		}
    			
    			listAllConditions = ProcessConditionUtil.processOrderCondition(listAllConditions);
    			listSortFields = ProcessConditionUtil.processOrderSort(listSortFields);
    			
    			listIterator = delegator.find("OrderHeaderReturnable", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListSalesOrderExternalReturnable service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesOrderSimple(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		// LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		//check permission for each order type
			if (!SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESORDER")) {
				Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
			}
    		
    		String partyId = null;
    		String statusId = null;
    		String agreementId = null;
    		if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0) {
    			statusId = parameters.get("statusId")[0];
			}
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}
			if (parameters.containsKey("agreementId") && parameters.get("agreementId").length > 0) {
				agreementId = parameters.get("agreementId")[0];
			}
			
			listAllConditions.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("customerId", EntityOperator.EQUALS, partyId));
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
			}
			if (UtilValidate.isNotEmpty(agreementId)) {
				listAllConditions.add(EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, agreementId));
			}
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-orderDate");
				listSortFields.add("priority");
			}
			listIterator = delegator.find("OrderHeaderAndOrderRoleFromTo", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrderSimple service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> jqGetProductItemsCompareOrderAndAgreement(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = new ArrayList<Map<String,Object>>();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	
        try {
        	String agreementId = null;
        	if (parameters.containsKey("agreementId") && parameters.get("agreementId").length > 0) {
				agreementId = parameters.get("agreementId")[0];
			}
        	Map<String, Object> productItemsAllMap = FastMap.newInstance();
        	
        	// get map list product in agreement
	        if (UtilValidate.isNotEmpty(agreementId)) {
	        	GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
	        	if (agreement != null) {
	        		List<GenericValue> listAgreementProductAppl = delegator.findList("AgreementProductAppl", EntityCondition.makeCondition("agreementId", agreementId), null, null, null, false);
	        		if (UtilValidate.isNotEmpty(listAgreementProductAppl)) {
	        			for (GenericValue x : listAgreementProductAppl) {
		        			GenericValue agreementItem = delegator.findOne("AgreementItem", UtilMisc.toMap("agreementId", agreementId, "agreementItemSeqId", x.getString("agreementItemSeqId")), false);
		        			if (UtilValidate.isEmpty(agreementItem)) 
		        				continue;
		        			String productId = x.getString("productId");
		        			/*productItemItemMap.put("agreementId", x.getString("agreementId"));
		        			productItemItemMap.put("agreementItemSeqId", x.getString("agreementItemSeqId"));
		        			productItemItemMap.put("productId", productId);
		        			productItemItemMap.put("quantity", x.getBigDecimal("quantity"));
		        			productItemItemMap.put("unitPrice", x.getBigDecimal("price"));
		        			productItemItemMap.put("note", agreementItem.getString("agreementText"));*/
		        			if (UtilValidate.isNotEmpty(productId)) {
		        				BigDecimal quantityAgreement = x.getBigDecimal("quantity");
				    			BigDecimal unitPrice = x.getBigDecimal("price");
				    			BigDecimal amountAgreement = BigDecimal.ZERO;
				    			if (quantityAgreement != null && unitPrice != null) {
				    				amountAgreement = quantityAgreement.multiply(unitPrice);
	        					}
		        				if (productItemsAllMap.containsKey(productId)) {
		        					Map<String, Object> productItemsMapPerProductId = (Map<String, Object>) productItemsAllMap.get(productId);
		        					if (productItemsMapPerProductId != null) {
		        						BigDecimal quantityAgreementExist = (BigDecimal) productItemsMapPerProductId.get("quantityAgreement");
			        					if (quantityAgreementExist != null)
			        						quantityAgreement = quantityAgreement.add(quantityAgreementExist);
			        					BigDecimal amountAgreementExist = (BigDecimal) productItemsMapPerProductId.get("amountAgreement");
			        					if (amountAgreementExist != null)
			        						amountAgreement = amountAgreement.add(amountAgreementExist);
		        					} else {
		        						productItemsMapPerProductId = FastMap.newInstance();
		        					}
		        					productItemsMapPerProductId.put("quantityAgreement", quantityAgreement);
		        					productItemsMapPerProductId.put("unitPrice", unitPrice);
		        					productItemsMapPerProductId.put("amountAgreement", amountAgreement);
		        				} else {
		        					Map<String, Object> productItemsMapPerProductId = FastMap.newInstance();
		        					productItemsMapPerProductId.put("productId", productId);
		        					productItemsMapPerProductId.put("quantityAgreement", quantityAgreement);
		        					productItemsMapPerProductId.put("unitPrice", unitPrice);
	        						productItemsMapPerProductId.put("amountAgreement", amountAgreement);
		        					productItemsAllMap.put(productId, productItemsMapPerProductId);
		        				}
		        			}
		        		}
	        		}
	        	}
	        }
	        
	        // get map list product in order
	        List<EntityCondition> listAllCondition = FastList.newInstance();
	        listAllCondition.add(EntityCondition.makeCondition("agreementId", agreementId));
	        listAllCondition.add(EntityCondition.makeCondition("statusId", "ORDER_COMPLETED"));
	        List<GenericValue> listOrderHeaderAndItem = delegator.findList("OrderHeaderAndItemsDetail", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
	        if (UtilValidate.isNotEmpty(listOrderHeaderAndItem)) {
	        	for (GenericValue x : listOrderHeaderAndItem) {
        			String productId = x.getString("productId");
        			/*productItemItemMap.put("orderId", x.getString("orderId"));
        			productItemItemMap.put("orderItemSeqId", x.getString("orderItemSeqId"));
        			productItemItemMap.put("productId", productId);
        			productItemItemMap.put("quantity", x.getBigDecimal("quantity"));
        			productItemItemMap.put("unitPrice", x.getBigDecimal("unitPrice"));*/
        			if (UtilValidate.isNotEmpty(productId)) {
        				BigDecimal quantityOrder = x.getBigDecimal("quantity");
		    			BigDecimal unitPrice = x.getBigDecimal("unitPrice");
		    			BigDecimal amountOrder = BigDecimal.ZERO;
		    			if (quantityOrder != null && unitPrice != null) {
		    				amountOrder = quantityOrder.multiply(unitPrice);
    					}
        				if (productItemsAllMap.containsKey(productId)) {
        					Map<String, Object> productItemsMapPerProductId = (Map<String, Object>) productItemsAllMap.get(productId);
        					if (productItemsMapPerProductId != null) {
        						BigDecimal quantityOrderExist = (BigDecimal) productItemsMapPerProductId.get("quantityOrder");
	        					if (quantityOrderExist != null)
	        						quantityOrder = quantityOrder.add(quantityOrderExist);
	        					BigDecimal amountOrderExist = (BigDecimal) productItemsMapPerProductId.get("amountAgreement");
	        					if (amountOrderExist != null)
	        						amountOrder = amountOrder.add(amountOrderExist);
        					} else {
        						productItemsMapPerProductId = FastMap.newInstance();
        					}
        					productItemsMapPerProductId.put("quantityOrder", quantityOrder);
        					productItemsMapPerProductId.put("unitPrice", unitPrice);
        					productItemsMapPerProductId.put("amountOrder", amountOrder);
        				} else {
        					Map<String, Object> productItemsMapPerProductId = FastMap.newInstance();
        					productItemsMapPerProductId.put("productId", productId);
        					productItemsMapPerProductId.put("quantityOrder", quantityOrder);
        					productItemsMapPerProductId.put("unitPrice", unitPrice);
        					productItemsMapPerProductId.put("amountOrder", amountOrder);
        					productItemsAllMap.put(productId, productItemsMapPerProductId);
        				}
        			}
	        	}
	        }
	        
	        Set<String> productIdsSet = productItemsAllMap.keySet();
	        if (UtilValidate.isNotEmpty(productIdsSet)) {
	        	List<String> productIds = UtilMisc.<String>toList(productIdsSet);
	        	listAllCondition.clear();
	        	listAllCondition.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	        	//listAllCondition.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", null), EntityOperator.OR, EntityCondition.makeCondition("isVariant", "N")));
	        	List<GenericValue> listProductTmp = delegator.findList("Product", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
	    		for (GenericValue productTmp : listProductTmp) {
	    			Map<String, Object> thisProductMap = (Map<String, Object>) productItemsAllMap.get(productTmp.getString("productId"));
	    			if ("Y".equals(productTmp.getString("isVariant"))) {
	    				Map<String, Object> productMap = FastMap.newInstance();
	    				List<GenericValue> listVirtualProductAssoc = SalesUtil.getVariantVirtualAssocs(productTmp);
	    				if (UtilValidate.isNotEmpty(listVirtualProductAssoc)){
	    					productMap.put("parentProductId", listVirtualProductAssoc.get(0).getString("productId"));
            			}
		    			productMap.put("productId", productTmp.getString("productId"));
		    			productMap.put("productName", productTmp.getString("productName"));
		    			productMap.put("isVirtual", productTmp.getString("isVirtual"));
		    			productMap.put("isVariant", productTmp.getString("isVariant"));
		    			productMap.put("quantityAgreement", thisProductMap.get("quantityAgreement"));
		    			productMap.put("quantityOrder", thisProductMap.get("quantityOrder"));
		    			productMap.put("unitPrice", thisProductMap.get("unitPrice"));
		    			productMap.put("amountAgreement", thisProductMap.get("amountAgreement"));
		    			productMap.put("amountOrder", thisProductMap.get("amountOrder"));
		    			listIterator.add(productMap);
	    			} else {
	    				Map<String, Object> productMap = FastMap.newInstance();
		    			productMap.put("productId", productTmp.getString("productId"));
		    			productMap.put("productCode", productTmp.getString("productCode"));
		    			productMap.put("productName", productTmp.getString("productName"));
		    			productMap.put("isVirtual", productTmp.getString("isVirtual"));
		    			productMap.put("isVariant", productTmp.getString("isVariant"));
		    			productMap.put("parentProductId", null);
		    			productMap.put("quantityAgreement", thisProductMap.get("quantityAgreement"));
		    			productMap.put("quantityOrder", thisProductMap.get("quantityOrder"));
		    			productMap.put("unitPrice", thisProductMap.get("unitPrice"));
		    			productMap.put("amountAgreement", thisProductMap.get("amountAgreement"));
		    			productMap.put("amountOrder", thisProductMap.get("amountOrder"));
		    			listIterator.add(productMap);
	    			}
	    		}
	        }
        } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetProductItemsCompareBetweenOrderAndAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
        if (UtilValidate.isEmpty(listSortFields)) {
        	listSortFields.add("-quantityAgreement");
        }
        listIterator = EntityMiscUtil.sortList(listIterator, listSortFields, true);
        successResult.put("listIterator", listIterator);
        return successResult;
    }
    
    public static Map<String, Object> updateApprovedOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");
        Map<String, String> overridePriceMap = UtilGenerics.checkMap(context.get("overridePriceMap"));
        Map<String, String> itemDescriptionMap = UtilGenerics.checkMap(context.get("itemDescriptionMap"));
        Map<String, String> itemPriceMap = UtilGenerics.checkMap(context.get("itemPriceMap"));
        Map<String, String> itemQtyMap = UtilGenerics.checkMap(context.get("itemQtyMap"));
        Map<String, String> itemAmountMap = UtilGenerics.checkMap(context.get("itemAmountMap"));
        Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
        Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));
        Map<String, String> itemAttributesMap = UtilGenerics.checkMap(context.get("itemAttributesMap"));
        Map<String, String> itemEstimatedShipDateMap = UtilGenerics.checkMap(context.get("itemShipDateMap"));
        Map<String, String> itemEstimatedDeliveryDateMap = UtilGenerics.checkMap(context.get("itemDeliveryDateMap"));
        
        // TODOCHANGE: add new
        Map<String, String> itemExpireDateMap = UtilGenerics.checkMap(context.get("itemExpireDateMap"));
        Map<String, String> itemAlternativeQtyMap = UtilGenerics.checkMap(context.get("itemAlternativeQtyMap"));
        Map<String, String> itemQuantityUomIdMap = UtilGenerics.checkMap(context.get("itemQuantityUomIdMap"));
        if (overridePriceMap == null) overridePriceMap = FastMap.newInstance();
        if (itemDescriptionMap == null) itemDescriptionMap = FastMap.newInstance();
        if (itemPriceMap == null) itemPriceMap = FastMap.newInstance();
        if (itemQtyMap == null) itemQtyMap = FastMap.newInstance();
        if (itemAmountMap == null) itemAmountMap = FastMap.newInstance();
        if (itemReasonMap == null) itemReasonMap = FastMap.newInstance();
        if (itemCommentMap == null) itemCommentMap = FastMap.newInstance();
        if (itemAttributesMap == null) itemAttributesMap = FastMap.newInstance();
        if (itemEstimatedShipDateMap == null) itemEstimatedShipDateMap = FastMap.newInstance();
        if (itemEstimatedDeliveryDateMap == null) itemEstimatedDeliveryDateMap = FastMap.newInstance();
        if (itemExpireDateMap == null) itemExpireDateMap = FastMap.newInstance();
        if (itemAlternativeQtyMap == null) itemAlternativeQtyMap = FastMap.newInstance();
        if (itemQuantityUomIdMap == null) itemQuantityUomIdMap = FastMap.newInstance();
        
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        boolean isRemoveOrderAdjustment = false; // TODOCHANGE new process order adjustment billing invoice
        
        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = OrderDuplicateServices.loadCartForUpdate(dispatcher, delegator, userLogin, orderId, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShoppingCartEmpty", locale));
        }

        // go through the item attributes map once to get a list of key names
        Set<String> attributeNames =FastSet.newInstance();
        Set<String> keys  = itemAttributesMap.keySet();
        for (String key : keys) {
            String[] attributeInfo = key.split(":");
            attributeNames.add(attributeInfo[0]);
        }

        // go through the item map and obtain the totals per item
        Map<String, BigDecimal> itemTotals = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> itemTotalsAlterQty = new HashMap<String, BigDecimal>(); // TODOCHANGE add new attribute
        for (String key : itemQtyMap.keySet()) {
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            String alternativeQuantityStr = itemAlternativeQtyMap.get(key); // TODOCHANGE add new attribute
            BigDecimal groupQtyAlterQty = BigDecimal.ZERO; // TODOCHANGE add new attribute
            try {
                groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
                groupQtyAlterQty = (BigDecimal) ObjectType.simpleTypeConvert(alternativeQuantityStr, "BigDecimal", null, locale);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (groupQty.compareTo(BigDecimal.ONE) < 0) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderItemQtyMustBePositive", locale));
            }

            String[] itemInfo = key.split(":");
            BigDecimal tally = itemTotals.get(itemInfo[0]);
            BigDecimal tallyAlterQty = itemTotalsAlterQty.get(itemInfo[0]);
            if (tally == null) {
                tally = groupQty;
            } else {
                tally = tally.add(groupQty);
            }
            itemTotals.put(itemInfo[0], tally);
            
            if (tallyAlterQty == null) {
           	 tallyAlterQty = groupQtyAlterQty;
            } else {
           	 tallyAlterQty = tallyAlterQty.add(groupQtyAlterQty);
            }
            itemTotalsAlterQty.put(itemInfo[0], tallyAlterQty);
        }

        // set the items amount/price
        for (String itemSeqId : itemTotals.keySet()) {
            ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);

            if (cartItem != null) {
                BigDecimal qty = itemTotals.get(itemSeqId);
                BigDecimal priceSave = cartItem.getBasePrice();
                BigDecimal alterQty = itemTotalsAlterQty.get(itemSeqId); // TODOCHANGE add new attribute

                // set quantity
                try {
                	if (UtilValidate.isNotEmpty(alterQty)) {
                		cartItem.setQuantity(alterQty, dispatcher, cart, false, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
					} else {
						cartItem.setQuantity(qty, dispatcher, cart, false, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
					}
	                // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
	                if ("PRODPROMO_ORDER_ITEM".equals(cartItem.getItemType())) {
	                	List<GenericValue> itemAdjustmentsTmp = cartItem.getAdjustments();
	                	if (UtilValidate.isNotEmpty(itemAdjustmentsTmp)) {
	                		for (GenericValue itemAdjustmentTmp : itemAdjustmentsTmp) {
	                			if ("OrderAdjustment".equals(itemAdjustmentTmp.getEntityName())) {
	                				cartItem.removeAdjustment(itemAdjustmentTmp);
	                			}
	                		}
	                	}
	                	
	                	BigDecimal discountAmount = cartItem.getQuantity().multiply(cartItem.getBasePrice()).negate();
	
	                    //doOrderItemPromoAction(null, newItem, discountAmount, "amount", delegator);
	            		//doOrderItemPromoAction(GenericValue productPromoAction, ShoppingCartItem cartItem, BigDecimal amount, String amountField, Delegator delegator)
	            		discountAmount = discountAmount.setScale(3, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
	            		String descAjust = UtilProperties.getMessage("OrderUiLabels", "ReturnPromotionProduct", locale);
	                    GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
	                            UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "amount", discountAmount,
	                                    "productPromoId", null, "productPromoRuleId", null, "productPromoActionSeqId", null,
	                                    "description", descAjust));
	                    cartItem.addAdjustment(orderAdjustment);
	
	                    // set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
	                    cartItem.setIsPromo(true);
	                    if (Debug.verboseOn()) Debug.logVerbose("item return promo adjustments: " + cartItem.getAdjustments(), module);
	                }
	            } catch (CartItemModifyException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
	            Debug.logInfo("Set item quantity: [" + itemSeqId + "] " + qty, module);
	
	            if (cartItem.getIsModifiedPrice()) // set price
	                cartItem.setBasePrice(priceSave);
	            
	            BigDecimal price = null;
	            if (overridePriceMap.containsKey(itemSeqId)) {
	                String priceStr = itemPriceMap.get(itemSeqId);
	                if (UtilValidate.isNotEmpty(priceStr)) {
	//                    BigDecimal price = null;
	                    try {
	                        price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
	                    } catch (GeneralException e) {
	                        Debug.logError(e, module);
	                        return ServiceUtil.returnError(e.getMessage());
	                    }
	                    price = price.setScale(orderDecimals, orderRounding);
	                    cartItem.setBasePrice(price);
	                    cartItem.setIsModifiedPrice(true);
	                    Debug.logInfo("Set item price: [" + itemSeqId + "] " + price, module);
	                }
	
	            }
	            // amount - thangnv
	            if (itemAmountMap.containsKey(itemSeqId)) {
	            	String selectedAmountStr = itemAmountMap.get(itemSeqId);
	            	if (UtilValidate.isNotEmpty(selectedAmountStr)) {
	            		BigDecimal selectedAmount = new BigDecimal(selectedAmountStr);
	            		if (UtilValidate.isNotEmpty(selectedAmount)) {
	            			cartItem.setSelectedAmount(selectedAmount);
						}
	            	}
	            }
	            
	            // item comment - thangnv
	            if (itemCommentMap.containsKey(itemSeqId)) {
	            	String itemComment = itemCommentMap.get(itemSeqId);
	    			cartItem.setItemComment(itemComment);
	            }
	
	            // Update the item description
	            if (itemDescriptionMap != null && itemDescriptionMap.containsKey(itemSeqId)) {
	                String description = itemDescriptionMap.get(itemSeqId);
	                if (UtilValidate.isNotEmpty(description)) {
	                    cartItem.setName(description);
	                    Debug.logInfo("Set item description: [" + itemSeqId + "] " + description, module);
	                } else {
	                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                            "OrderItemDescriptionCannotBeEmpty", locale));
	                }
	            }
	
	            // update the order item attributes
	            if (itemAttributesMap != null) {
	                String attrValue = null;
	                for (String attrName : attributeNames) {
	                    attrValue = itemAttributesMap.get(attrName + ":" + itemSeqId);
	                    if (UtilValidate.isNotEmpty(attrName)) {
	                        cartItem.setOrderItemAttribute(attrName, attrValue);
	                        Debug.logInfo("Set item attribute Name: [" + itemSeqId + "] " + attrName + " , Value:" + attrValue, module);
	                    }
	                }
	            }
	            
	            // TODOCHANGE add new process
	        	// Update the item expireDate
	            if (itemExpireDateMap != null && itemExpireDateMap.containsKey(itemSeqId)) {
	                String expireDateStr = itemExpireDateMap.get(itemSeqId);
	                if (UtilValidate.isNotEmpty(expireDateStr)) {
	               	 Timestamp expireDate = null;
	               	 try {
	               		 if (UtilValidate.isNotEmpty(expireDateStr)) {
	    	    	        	Long expireDateL = Long.parseLong(expireDateStr);
	    	    	        	expireDate = new Timestamp(expireDateL);
	    	    	        }
			            	//expireDate = (Timestamp) ObjectType.simpleTypeConvert(expireDateStr, "Timestamp", null, locale);
			        	 } catch (Exception e) {
	    		            Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
			        	 }
	           		 cartItem.setAttribute("expireDate", expireDate);
	                    Debug.logInfo("Set item expireDate: [" + itemSeqId + "] " + expireDate, module);
	                }
	            }
	            // Update the item alternativeQuantity
	            if (UtilValidate.isNotEmpty(alterQty)) {
	       		 	cartItem.setAttribute("alternativeQuantity", alterQty);
	            }
	            // Update the item quantityUomId
	            if (itemQuantityUomIdMap != null && itemQuantityUomIdMap.containsKey(itemSeqId)) {
	                String quantityUomId = itemQuantityUomIdMap.get(itemSeqId);
	                if (UtilValidate.isNotEmpty(quantityUomId)) {
	           		 cartItem.setAttribute("quantityUomId", quantityUomId);
	                    Debug.logInfo("Set item quantityUomId: [" + itemSeqId + "] " + quantityUomId, module);
	                    String orderTypeId = cart.getOrderType();
	                    if ("PURCHASE_ORDER".equals(orderTypeId)){
	                    	String productId = cartItem.getProductId();
	                    	try {
	                    		GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
	                    		String baseQuantityUomId = objProduct.getString("quantityUomId");
	                    		String requireAmount = objProduct.getString("requireAmount");
	                    		if (UtilValidate.isEmpty(requireAmount) || "N".equals(requireAmount)) {
									if (UtilValidate.isNotEmpty(baseQuantityUomId) && UtilValidate.isNotEmpty(cartItem.getBasePrice())) {
		                    			BigDecimal convert = ProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQuantityUomId);
		                    			BigDecimal alternativeUnitPrice = price;
		                    			cartItem.setAlternativeUnitPrice(alternativeUnitPrice);
		                    			cartItem.setBasePrice(price.divide(convert, pricePODecimals, pricePORounding));
									}
								} else {
									cartItem.setAlternativeUnitPrice(price);
								}
	                    	} catch (GenericEntityException e){
	                    		return ServiceUtil.returnError("OLBIUS: get Product error ! " + e.toString());
	                    	}
	                    }
                    }
                }

            } else {
                Debug.logInfo("Unable to locate shopping cart item for seqId #" + itemSeqId, module);
            }
        }
        // Create Estimated Delivery dates
        for (Map.Entry<String, String> entry : itemEstimatedDeliveryDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();

            // ignore internationalised variant of dates
            if (!itemSeqId.endsWith("_i18n")) {
                String estimatedDeliveryDate = entry.getValue();
                if (UtilValidate.isNotEmpty(estimatedDeliveryDate)) {
                    Timestamp deliveryDate = Timestamp.valueOf(estimatedDeliveryDate);
                    ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                    cartItem.setDesiredDeliveryDate(deliveryDate);
                }
            }
        }

        // Create Estimated ship dates
        for (Map.Entry<String, String> entry : itemEstimatedShipDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();

            // ignore internationalised variant of dates
            if (!itemSeqId.endsWith("_i18n")) {
                String estimatedShipDate = entry.getValue();
                if (UtilValidate.isNotEmpty(estimatedShipDate)) {
                    Timestamp shipDate = Timestamp.valueOf(estimatedShipDate);
                    ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                    cartItem.setEstimatedShipDate(shipDate);
                }
            }
        }

        // update the group amounts
        for (String key : itemQtyMap.keySet()) {
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            try {
                groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            String[] itemInfo = key.split(":");
            int groupIdx = -1;
            try {
                groupIdx = Integer.parseInt(itemInfo[1]);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            // set the group qty
            ShoppingCartItem cartItem = cart.findCartItem(itemInfo[0]);
            if (cartItem != null) {
                Debug.logInfo("Shipping info (before) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
                cart.setItemShipGroupQty(cartItem, groupQty, groupIdx - 1);
                Debug.logInfo("Set ship group qty: [" + itemInfo[0] + " / " + itemInfo[1] + " (" + (groupIdx-1) + ")] " + groupQty, module);
                Debug.logInfo("Shipping info (after) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
            }
        }

        // save all the updated information
        try {
        	// TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
            OrderDuplicateServices.saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, UtilMisc.<String, Object>toMap("itemReasonMap", itemReasonMap, "itemCommentMap", itemCommentMap), calcTax, false, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);

        // log an order note
        try {
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", UtilProperties.getMessage("OrderUiLabels", "OrderUpdatedOrder", locale), "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }
    
    public static Map<String, Object> updateOrderItemsLoadToCart(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");
        Map<String, String> overridePriceMap = UtilGenerics.checkMap(context.get("overridePriceMap"));
        Map<String, String> itemDescriptionMap = UtilGenerics.checkMap(context.get("itemDescriptionMap"));
        Map<String, String> itemPriceMap = UtilGenerics.checkMap(context.get("itemPriceMap"));
        Map<String, String> itemQtyMap = UtilGenerics.checkMap(context.get("itemQtyMap"));
        Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
        Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));
        Map<String, String> itemAttributesMap = UtilGenerics.checkMap(context.get("itemAttributesMap"));
        Map<String, String> itemEstimatedShipDateMap = UtilGenerics.checkMap(context.get("itemShipDateMap"));
        Map<String, String> itemEstimatedDeliveryDateMap = UtilGenerics.checkMap(context.get("itemDeliveryDateMap"));
        
        // TODOCHANGE: add new
        Set<GenericPK> actionPKSelectedList = UtilGenerics.checkSet(context.get("actionPKSelectedList"));
        Map<String, String> itemExpireDateMap = UtilGenerics.checkMap(context.get("itemExpireDateMap"));
        Map<String, String> itemAlternativeQtyMap = UtilGenerics.checkMap(context.get("itemAlternativeQtyMap"));
        Map<String, String> itemQuantityUomIdMap = UtilGenerics.checkMap(context.get("itemQuantityUomIdMap"));
        if (overridePriceMap == null) overridePriceMap = FastMap.newInstance();
        if (itemDescriptionMap == null) itemDescriptionMap = FastMap.newInstance();
        if (itemPriceMap == null) itemPriceMap = FastMap.newInstance();
        if (itemQtyMap == null) itemQtyMap = FastMap.newInstance();
        if (itemReasonMap == null) itemReasonMap = FastMap.newInstance();
        if (itemCommentMap == null) itemCommentMap = FastMap.newInstance();
        if (itemAttributesMap == null) itemAttributesMap = FastMap.newInstance();
        if (itemEstimatedShipDateMap == null) itemEstimatedShipDateMap = FastMap.newInstance();
        if (itemEstimatedDeliveryDateMap == null) itemEstimatedDeliveryDateMap = FastMap.newInstance();
        if (itemExpireDateMap == null) itemExpireDateMap = FastMap.newInstance();
        if (itemAlternativeQtyMap == null) itemAlternativeQtyMap = FastMap.newInstance();
        if (itemQuantityUomIdMap == null) itemQuantityUomIdMap = FastMap.newInstance();
        
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        // TODOCHANGE manual promotion
        if (actionPKSelectedList == null) {
    		actionPKSelectedList = new HashSet<GenericPK>();
			
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
			listConds.add(EntityCondition.makeCondition("orderId", orderId));
			listConds.add(EntityCondition.makeCondition("productPromoId", EntityOperator.NOT_EQUAL, null));
			listConds.add(EntityCondition.makeCondition("productPromoRuleId", EntityOperator.NOT_EQUAL, null));
			listConds.add(EntityCondition.makeCondition("productPromoActionSeqId", EntityOperator.NOT_EQUAL, null));
			try {
				List<GenericValue> listAdjustment = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, null, false);
				if (listAdjustment != null) {
	            	for (GenericValue itemAdj : listAdjustment) {
	            		GenericValue promoAction = delegator.findOne("ProductPromoAction", UtilMisc.toMap("productPromoId", itemAdj.get("productPromoId"), "productPromoRuleId", itemAdj.get("productPromoRuleId"), "productPromoActionSeqId", itemAdj.get("productPromoActionSeqId")), false);
	            		actionPKSelectedList.add(promoAction.getPrimaryKey());
	            	}
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			}
        }
        
        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
        	// TODOCHANGE new process order adjustment billing invoice
        	boolean isRemoveOrderAdjustment = false;
        	Map<String, Object> resultLoadCartForUpdate = dispatcher.runSync("loadCartForUpdateCustom", UtilMisc.toMap("orderId", orderId, "isRemoveOrderAdjustment", isRemoveOrderAdjustment, "userLogin", userLogin));
        	if (ServiceUtil.isError(resultLoadCartForUpdate)) {
        		return ServiceUtil.returnError((String) resultLoadCartForUpdate.get(ModelService.ERROR_MESSAGE));
        	}
            cart = (ShoppingCart) resultLoadCartForUpdate.get("shoppingCart");
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderShoppingCartEmpty", locale));
        }
        
        // TODOCHANGE remove the adjustments
        List<GenericValue> listAdjustment = cart.getAdjustments();
        if (UtilValidate.isNotEmpty(listAdjustment)) {
        	List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
            adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            List<EntityCondition> exprs = new LinkedList<EntityCondition>();
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "PROMOTION_ADJUSTMENT"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "SHIPPING_CHARGES"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "SALES_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "VAT_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.NOT_EQUAL, "VAT_PRICE_CORRECT"));
            adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.AND));
            EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
            
            List<GenericValue> listAdjustmentFilted = EntityUtil.filterByCondition(listAdjustment, cond);
            listAdjustment.clear();
            if (listAdjustmentFilted != null) listAdjustment.addAll(listAdjustmentFilted);
        }
        // end new

        // go through the item attributes map once to get a list of key names
        Set<String> attributeNames =FastSet.newInstance();
        Set<String> keys  = itemAttributesMap.keySet();
        for (String key : keys) {
            String[] attributeInfo = key.split(":");
            attributeNames.add(attributeInfo[0]);
        }

        // go through the item map and obtain the totals per item
        Map<String, BigDecimal> itemTotals = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> itemTotalsAlterQty = new HashMap<String, BigDecimal>(); // TODOCHANGE add new attribute
        for (String key : itemQtyMap.keySet()) {
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            String alternativeQuantityStr = itemAlternativeQtyMap.get(key); // TODOCHANGE add new attribute
            BigDecimal groupQtyAlterQty = BigDecimal.ZERO; // TODOCHANGE add new attribute
            try {
                groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
                groupQtyAlterQty = (BigDecimal) ObjectType.simpleTypeConvert(alternativeQuantityStr, "BigDecimal", null, locale);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (groupQty.compareTo(BigDecimal.ONE) < 0) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderItemQtyMustBePositive", locale));
            }

            String[] itemInfo = key.split(":");
            BigDecimal tally = itemTotals.get(itemInfo[0]);
            BigDecimal tallyAlterQty = itemTotalsAlterQty.get(itemInfo[0]);
            if (tally == null) {
                tally = groupQty;
            } else {
                tally = tally.add(groupQty);
            }
            itemTotals.put(itemInfo[0], tally);
            
            if (tallyAlterQty == null) {
           	 tallyAlterQty = groupQtyAlterQty;
            } else {
           	 tallyAlterQty = tallyAlterQty.add(groupQtyAlterQty);
            }
            itemTotalsAlterQty.put(itemInfo[0], tallyAlterQty);
        }

        // set the items amount/price
        for (String itemSeqId : itemTotals.keySet()) {
            ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);

            if (cartItem != null) {
                BigDecimal qty = itemTotals.get(itemSeqId);
                BigDecimal priceSave = cartItem.getBasePrice();
                BigDecimal alterQty = itemTotalsAlterQty.get(itemSeqId); // TODOCHANGE add new attribute

                // set quantity
                try {
                    cartItem.setQuantity(qty, dispatcher, cart, false, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
                    // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
                    if ("PRODPROMO_ORDER_ITEM".equals(cartItem.getItemType())) {
                    	List<GenericValue> itemAdjustmentsTmp = cartItem.getAdjustments();
                    	if (UtilValidate.isNotEmpty(itemAdjustmentsTmp)) {
                    		for (GenericValue itemAdjustmentTmp : itemAdjustmentsTmp) {
                    			if ("OrderAdjustment".equals(itemAdjustmentTmp.getEntityName())) {
                    				cartItem.removeAdjustment(itemAdjustmentTmp);
                    			}
                    		}
                    	}
                    	
                    	BigDecimal discountAmount = cartItem.getQuantity().multiply(cartItem.getBasePrice()).negate();

                        //doOrderItemPromoAction(null, newItem, discountAmount, "amount", delegator);
                		//doOrderItemPromoAction(GenericValue productPromoAction, ShoppingCartItem cartItem, BigDecimal amount, String amountField, Delegator delegator)
                		discountAmount = discountAmount.setScale(3, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
                		String descAjust = UtilProperties.getMessage("OrderUiLabels", "ReturnPromotionProduct", locale);
                        GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                                UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "amount", discountAmount,
                                        "productPromoId", null, "productPromoRuleId", null, "productPromoActionSeqId", null,
                                        "description", descAjust));
                        cartItem.addAdjustment(orderAdjustment);

                        // set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
                        cartItem.setIsPromo(true);
                        if (Debug.verboseOn()) Debug.logVerbose("item return promo adjustments: " + cartItem.getAdjustments(), module);
                    }
                } catch (CartItemModifyException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                Debug.logInfo("Set item quantity: [" + itemSeqId + "] " + qty, module);

                if (cartItem.getIsModifiedPrice()) // set price
                    cartItem.setBasePrice(priceSave);

                if (overridePriceMap.containsKey(itemSeqId)) {
                    String priceStr = itemPriceMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(priceStr)) {
                        BigDecimal price = null;
                        try {
                            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
                        } catch (GeneralException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                        price = price.setScale(orderDecimals, orderRounding);
                        cartItem.setBasePrice(price);
                        cartItem.setIsModifiedPrice(true);
                        Debug.logInfo("Set item price: [" + itemSeqId + "] " + price, module);
                    }

                }

                // Update the item description
                if (itemDescriptionMap != null && itemDescriptionMap.containsKey(itemSeqId)) {
                    String description = itemDescriptionMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(description)) {
                        cartItem.setName(description);
                        Debug.logInfo("Set item description: [" + itemSeqId + "] " + description, module);
                    } else {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "OrderItemDescriptionCannotBeEmpty", locale));
                    }
                }

                // update the order item attributes
                if (itemAttributesMap != null) {
                    String attrValue = null;
                    for (String attrName : attributeNames) {
                        attrValue = itemAttributesMap.get(attrName + ":" + itemSeqId);
                        if (UtilValidate.isNotEmpty(attrName)) {
                            cartItem.setOrderItemAttribute(attrName, attrValue);
                            Debug.logInfo("Set item attribute Name: [" + itemSeqId + "] " + attrName + " , Value:" + attrValue, module);
                        }
                    }
                }
                
                // TODOCHANGE add new process
            	// Update the item expireDate
                if (itemExpireDateMap != null && itemExpireDateMap.containsKey(itemSeqId)) {
                    String expireDateStr = itemExpireDateMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(expireDateStr)) {
                   	 Timestamp expireDate = null;
                   	 try {
                   		 if (UtilValidate.isNotEmpty(expireDateStr)) {
        	    	        	Long expireDateL = Long.parseLong(expireDateStr);
        	    	        	expireDate = new Timestamp(expireDateL);
        	    	        }
    		            	//expireDate = (Timestamp) ObjectType.simpleTypeConvert(expireDateStr, "Timestamp", null, locale);
    		        	 } catch (Exception e) {
        		            Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
    		        	 }
               		 cartItem.setAttribute("expireDate", expireDate);
                        Debug.logInfo("Set item expireDate: [" + itemSeqId + "] " + expireDate, module);
                    }
                }
                // Update the item alternativeQuantity
                if (UtilValidate.isNotEmpty(alterQty)) {
           		 	cartItem.setAttribute("alternativeQuantity", alterQty);
                }
                // Update the item quantityUomId
                if (itemQuantityUomIdMap != null && itemQuantityUomIdMap.containsKey(itemSeqId)) {
                    String quantityUomId = itemQuantityUomIdMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(quantityUomId)) {
               		 cartItem.setAttribute("quantityUomId", quantityUomId);
                        Debug.logInfo("Set item quantityUomId: [" + itemSeqId + "] " + quantityUomId, module);
                    }
                }

            } else {
                Debug.logInfo("Unable to locate shopping cart item for seqId #" + itemSeqId, module);
            }
        }
        // Create Estimated Delivery dates
        for (Map.Entry<String, String> entry : itemEstimatedDeliveryDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();

            // ignore internationalised variant of dates
            if (!itemSeqId.endsWith("_i18n")) {
                String estimatedDeliveryDate = entry.getValue();
                if (UtilValidate.isNotEmpty(estimatedDeliveryDate)) {
                    Timestamp deliveryDate = Timestamp.valueOf(estimatedDeliveryDate);
                    ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                    cartItem.setDesiredDeliveryDate(deliveryDate);
                }
            }
        }

        // Create Estimated ship dates
        for (Map.Entry<String, String> entry : itemEstimatedShipDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();

            // ignore internationalised variant of dates
            if (!itemSeqId.endsWith("_i18n")) {
                String estimatedShipDate = entry.getValue();
                if (UtilValidate.isNotEmpty(estimatedShipDate)) {
                    Timestamp shipDate = Timestamp.valueOf(estimatedShipDate);
                    ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                    cartItem.setEstimatedShipDate(shipDate);
                }
            }
        }

        // update the group amounts
        for (String key : itemQtyMap.keySet()) {
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            try {
                groupQty = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            String[] itemInfo = key.split(":");
            int groupIdx = -1;
            try {
                groupIdx = Integer.parseInt(itemInfo[1]);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            // set the group qty
            ShoppingCartItem cartItem = cart.findCartItem(itemInfo[0]);
            if (cartItem != null) {
                Debug.logInfo("Shipping info (before) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
                cart.setItemShipGroupQty(cartItem, groupQty, groupIdx - 1);
                Debug.logInfo("Set ship group qty: [" + itemInfo[0] + " / " + itemInfo[1] + " (" + (groupIdx-1) + ")] " + groupQty, module);
                Debug.logInfo("Shipping info (after) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
            }
        }

        // TODOCHANGE manual promotion
        if (actionPKSelectedList != null) cart.setAttribute("promoActionSelected", actionPKSelectedList);
        
        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);
        
        // calc the sales tax  
        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        try {
            coh.calcAndAddTax();
        } catch (GeneralException e) {
            Debug.logError(e, module);
        }
        
        // save all the updated information
        /*try {
            OrderDuplicateServices.saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, UtilMisc.<String, Object>toMap("itemReasonMap", itemReasonMap, "itemCommentMap", itemCommentMap), calcTax, false);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);

        // log an order note
        try {
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", UtilProperties.getMessage("OrderUiLabels", "OrderUpdatedOrder", locale), "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
         */
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }
    
    public static Map<String, Object> updateOrderItemsSaveToOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        HttpServletRequest request = (HttpServletRequest) context.get("request");
        
        ShoppingCart cart = com.olbius.basesales.shoppingcart.ShoppingCartEvents.getCartUpdateObject(request);
        
        if (cart == null) {
            Debug.logWarning("Cart is null", module);
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderShoppingCartEmpty", locale));
        }
		
		String orderId = cart.getOrderId();
		Boolean calcTax = Boolean.TRUE;
		Map<String, Object> itemReasonMap = FastMap.newInstance(); 
		Map<String, Object> itemCommentMap = FastMap.newInstance();
        
        // save all the updated information
        try {
        	// TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
        	boolean isRemoveOrderAdjustment = false;
            OrderDuplicateServices.saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, UtilMisc.<String, Object>toMap("itemReasonMap", itemReasonMap, "itemCommentMap", itemCommentMap), calcTax, false, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // run promotions to handle all changes in the cart
        //ProductPromoWorker.doPromotions(cart, dispatcher);

        // log an order note
        try {
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", UtilProperties.getMessage("OrderUiLabels", "OrderUpdatedOrder", locale), "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("orderId", orderId);
        return result;
    }
    
    public static Map<String, Object> createOrderNoteChangeStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        String changeReason = (String) context.get("changeReason");
        StringBuilder noteValue = new StringBuilder();
        noteValue.append(UtilProperties.getMessage(resource, "BSOrder", locale));
        try {
        	GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        	if (order == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
        	GenericValue orderStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
        	if (orderStatus != null) {
        		noteValue.append(" ");
        		noteValue.append(orderStatus.get("description", locale));
        		noteValue.append(".");
        	} else {
        		noteValue.append(" ");
        		noteValue.append(statusId);
        		noteValue.append(".");
        	}
        	GenericValue reason = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", changeReason), false);
        	if (reason != null) {
        		noteValue.append(" [");
        		noteValue.append(reason.getString("enumCode"));
        		noteValue.append("] ");
        		noteValue.append(reason.get("description", locale));
        	} else {
        		noteValue.append(" ");
        		noteValue.append(changeReason);
        	}
        	
        	// log an order note
    	    try {
    	        dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", noteValue.toString(), "internalNote", "Y", "userLogin", userLogin));
    	    } catch (GenericServiceException e) {
    	        Debug.logError(e, module);
    	    }
        } catch (Exception e) {
			String errMsg = "Fatal error calling createOrderNoteChangeStatus service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
        
	    return successResult;
    }
    
    public static Map<String, Object> createOrderNoteRequireFavorDelivery(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        String orderId = (String) context.get("orderId");
        try {
        	GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        	if (order == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
    	    dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", UtilProperties.getMessage(resource, "BSRequestSupplierDeliveryThisOrder", locale) + "!", "internalNote", "Y", "userLogin", userLogin));
        } catch (Exception e) {
			String errMsg = "Fatal error calling createOrderNoteRequireFavorDelivery service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
        
	    return successResult;
    }
    
    public static Map<String, Object> createNotifyOrderRequiredFavorDelivery(DispatchContext dctx, Map<String, ? extends Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String orderId = (String)context.get("orderId");
        String toOrderId = null;
        
        try {
			List<GenericValue> listOrderItemAssoc = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if(UtilValidate.isNotEmpty(listOrderItemAssoc)){
				GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
				toOrderId = orderItemAssoc.getString("toOrderId");
			}
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error when get purchase order id from SO id: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSGetPurchaseOrderIdIsError", locale) + "!");
		}
        
    	/* 
    	String orderId = (String) context.get("orderId");
        String status = (String) context.get("status");
    	// send to user create login
    	String header = "";
     	String state = "open";
     	String action = "viewOrder";
     	String targetLink = "orderId=" + orderId;
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
			List<String> partyIds = FastList.newInstance();
			partyIds.add(userLogin.getString("partyId"));
			header = UtilProperties.getMessage(resource_error, "BSCreatePORequireTHDeliveryIsFail", locale) + " [" + orderId +"]";
			try {
				CheckOutHelper.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			} catch (GenericServiceException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError("sent notify error!");
			}
		} */
        
    	// send notify to PO manager
		List<String> listPartyIds = new ArrayList<String>();
		List<String> listPartyGroups = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "RECEIVE_MSG_FAVOR_DELIVERY", userLogin);
		if (listPartyGroups != null && !listPartyGroups.isEmpty()){
			for (String group : listPartyGroups){
				try {
					List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", group, "partyIdTo", "DPO", "roleTypeIdFrom", "MANAGER")), null, null, null, false);
					listManagers = EntityUtil.filterByDate(listManagers);
					if (!listManagers.isEmpty()){
						for (GenericValue manager : listManagers){
							listPartyIds.add(manager.getString("partyIdFrom"));
						}
					}
				} catch (GenericEntityException e) {
					String errMsg = "Fatal error when get party relationship: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError("Get Party relationship error!");
				}
			}
		}
		if (!listPartyIds.isEmpty()){
	     	try {
	     		String header = UtilProperties.getMessage("BasePOUiLabels", "SOnotifyPO", locale);
		     	String state = "open";
		     	String action = "viewDetailPO";
		     	String targetLink = "orderId=" + toOrderId;
		     	String ntfType = "ONE";
		     	String sendToGroup = "N";
		     	String sendrecursive = "Y";
		     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		     	
				NotificationUtil.sendNotify(dispatcher, locale, listPartyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			} catch (GenericServiceException e) {
				String errMsg = "Fatal error when create notification: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError("Sent notify error!");
			}
		}
		return successResult;
    }
    
    public static Map<String, Object> sendNotifyWhenCreateOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("orderId");
    	try {
    		GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		if (order == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
    		
    		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		GenericValue orderOwnerRole = delegator.findOne("OrderRole", UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", organizationId, "roleTypeId", "BILL_FROM_VENDOR"), false);
    		if (orderOwnerRole != null) {
    			// TODOCHANGE send notify to partyIds
            	NotificationWorker.sendNotifyWhenCreateOrder(delegator, dispatcher, locale, orderId, userLogin);
    		} else {
    			// send notify to owner of this order
    			NotificationWorker.sendNotifyWhenCreateOrderToOwner(delegator, dispatcher, locale, orderId, userLogin);
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyWhenCreateOrder service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
    
    private static Map<String, Object> processDataEditOrderPre(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue userLogin, GenericValue orderHeader, String orderId, List<Map<String, Object>> listProduct) {
    	Map<String, String> itemQtyMap = FastMap.newInstance();
		Map<String, String> itemExpireDateMap = FastMap.newInstance();
        Map<String, String> itemAlternativeQtyMap = FastMap.newInstance();
        Map<String, String> itemQuantityUomIdMap = FastMap.newInstance();
        Map<String, String> itemPriceMap = FastMap.newInstance();
        Map<String, String> overridePriceMap = FastMap.newInstance();
        
        for (Map<String, Object> prodItem : listProduct) {
        	String orderItemSeqId = (String) prodItem.get("orderItemSeqId");
        	String shipGroupSeqId = (String) prodItem.get("shipGroupSeqId");
        	String productId = (String) prodItem.get("productId");
        	String quantityUomId = (String) prodItem.get("quantityUomId");
        	String quantityStr = (String) prodItem.get("quantity");
        	String expireDateStr = (String) prodItem.get("expireDateStr");
        	
    		BigDecimal quantity = BigDecimal.ZERO;
    		BigDecimal alternativeQuantity = null;
    		if (UtilValidate.isNotEmpty(orderItemSeqId) && UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(quantityStr)) {
	    		// Check quantityUomId with productQuotation
	    		BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
	    		GenericValue productItem = null;
	    		try {
	    			productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
	    			if (productItem == null) {
	    				continue;
	    			}
	            } catch (Exception e) {
	                Debug.logWarning(e, "Problems [product not exists] get productId = " + productId, module);
	            }
	    		if (productItem.getString("quantityUomId") != null) {
	    			if (!quantityUomId.equals(productItem.getString("quantityUomId"))) {
	    				try {
	    					Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productItem.getString("productId"), "uomFromId", quantityUomId, "uomToId", productItem.getString("quantityUomId"), "userLogin", userLogin));
	    					if (ServiceUtil.isSuccess(resultValue)) {
	    						quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
	    					}
	    				} catch (Exception e) {
	    		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
	    		        }
	    			} else {
	    				quantityUomIdToDefault = BigDecimal.ONE;
	    			}
	    		}
	    		if (quantityUomIdToDefault == null || quantityUomIdToDefault.compareTo(BigDecimal.ZERO) <= 0) quantityUomIdToDefault = BigDecimal.ONE;
    		
	    		try {
	                quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
	                //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
	                if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, orderHeader.getString("productStoreId"))){
	                    quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
	                } else {
	                    quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
	                }
	                alternativeQuantity = new BigDecimal(quantity.doubleValue());
	                quantity = quantity.multiply(quantityUomIdToDefault);
	            } catch (Exception e) {
	                Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	                //quantity = BigDecimal.ONE;
	            }
	    		
	    		String qtyKey = orderItemSeqId + ":" + shipGroupSeqId;
	    		String quantity1 = "";
	    		String quantity2 = "";
	    		try {
	    			quantity1 = (String) ObjectType.simpleTypeConvert(quantity, "String", null, locale);
	    			quantity2 = (String) ObjectType.simpleTypeConvert(alternativeQuantity, "String", null, locale);
	    		} catch (GeneralException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		itemQtyMap.put(qtyKey, quantity1);
	    		itemExpireDateMap.put(orderItemSeqId, expireDateStr);
	    		itemAlternativeQtyMap.put(qtyKey, quantity2);
	    		itemQuantityUomIdMap.put(orderItemSeqId, quantityUomId);
    		}
        }
		
		Map<String, Object> contextMap = new HashMap<String, Object>();
		contextMap.put("orderId", orderId);
		contextMap.put("itemQtyMap", itemQtyMap);
		contextMap.put("itemExpireDateMap", itemExpireDateMap);
		contextMap.put("itemAlternativeQtyMap", itemAlternativeQtyMap);
		contextMap.put("itemQuantityUomIdMap", itemQuantityUomIdMap);
		contextMap.put("itemPriceMap", itemPriceMap);
		contextMap.put("overridePriceMap", overridePriceMap);
		contextMap.put("userLogin", userLogin);
		contextMap.put("locale", locale);
		
		return contextMap;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addItemsToApprovedOrderAdvance(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("orderId");
    	successResult.put("orderId", orderId);
    	
    	// process data
    	List<Object> productListParam = (List<Object>) context.get("productList");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}
    	List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					boolean calcTax = true;
					/*if (prodItem.get("calcTax").equals("null")) {
						calcTax = prodItem.getBoolean("calcTax");
					}*/
					productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
					productItem.put("productId", prodItem.getString("productId"));
					productItem.put("prodCatalogId", prodItem.getString("prodCatalogId"));
					productItem.put("quantity", prodItem.getString("quantity"));
					productItem.put("amount", prodItem.getString("amount"));
					productItem.put("overridePrice", prodItem.getString("overridePrice"));
					productItem.put("reasonEnumId", prodItem.getString("reasonEnumId"));
					productItem.put("orderItemTypeId", prodItem.getString("orderItemTypeId"));
					productItem.put("changeComments", prodItem.getString("changeComments"));
					productItem.put("itemDesiredDeliveryDate", prodItem.getString("itemDesiredDeliveryDate"));
					//productItem.put("itemAttributesMap", prodItem.getString("itemAttributesMap"));
					productItem.put("calcTax", calcTax);
					
					productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
					productItem.put("expireDate", prodItem.getString("expireDate"));
					productItem.put("shipBeforeDate", prodItem.getString("shipBeforeDate"));
					productItem.put("shipAfterDate", prodItem.getString("shipAfterDate"));
					
					productItem.put("orderId", orderId);
					productItem.put("userLogin", userLogin);
					productItem.put("locale", locale);
					
					//update
					if (prodItem.containsKey("orderItemSeqId")) productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqId"));
					//productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
					//productItem.put("productId", prodItem.getString("productId"));
					//productItem.put("unitPriceStr", prodItem.getString("unitPrice"));
					//productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
					//productItem.put("quantityStr", prodItem.getString("quantity"));
					
					listProduct.add(productItem);
				}
			}
    	} else {
    		listProduct = (List<Map<String, Object>>) context.get("productList");
    	}
    	if (UtilValidate.isEmpty(listProduct)) 
    		return successResult;
    	try {
    		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		List<EntityCondition> listAllConditionOI = FastList.newInstance();
    		listAllConditionOI.add(EntityCondition.makeCondition("orderId", orderId));
    		List<EntityCondition> listOrCondition = FastList.newInstance();
    		listOrCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
    		//listOrCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
    		listAllConditionOI.add(EntityCondition.makeCondition(listOrCondition, EntityOperator.OR));
	    	List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(listAllConditionOI, EntityOperator.AND), null, null, null, false);
	    	
	    	// check order item is exists or not
	    	List<Map<String, Object>> listProductItemUpdate = new ArrayList<Map<String,Object>>();
	    	List<Map<String, Object>> listProductItemAdd = new ArrayList<Map<String,Object>>();
	    	List<EntityCondition> listAllCondition = FastList.newInstance();
	    	for (Map<String, Object> prodItem : listProduct) {
	    		listAllCondition.clear();
	    		listAllCondition.add(EntityCondition.makeCondition("productId", prodItem.get("productId")));
	    		listAllCondition.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isPromo", null), EntityOperator.OR, EntityCondition.makeCondition("isPromo", "N")));
	    		List<GenericValue> orderItemsExist = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition(listAllCondition));
	    		if (UtilValidate.isNotEmpty(orderItemsExist)) {
	    			GenericValue orderItemExist = orderItemsExist.get(0);
	    			prodItem.put("orderItemSeqId", orderItemExist.get("orderItemSeqId"));
	    			listProductItemUpdate.add(prodItem);
	    		} else {
	    			listProductItemAdd.add(prodItem);
	    		}
	    	}
	    	
	    	// update order items
	    	if (UtilValidate.isNotEmpty(orderHeader) && UtilValidate.isNotEmpty(listProductItemUpdate)) {
	    		Map<String, Object> contextMapUpdate = processDataEditOrderPre(delegator, dispatcher, locale, userLogin, orderHeader, orderId, listProductItemUpdate);
	    		Map<String, Object> resultValueUpdate = dispatcher.runSync("updateOrderItemsCustom", contextMapUpdate);
				if (ServiceUtil.isError(resultValueUpdate)) {
					return ServiceUtil.returnError((String) resultValueUpdate.get(ModelService.ERROR_MESSAGE));
				}
	    	}
	    	
    		if (UtilValidate.isNotEmpty(listProductItemAdd)) {
    			if ("SALES_ORDER".equals(orderHeader.getString("orderTypeId"))) {
    				for (Map<String, Object> prodItem : listProductItemAdd) {
    					Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemOlb", prodItem);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}
    				}
    			} else if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))){
    				for (Map<String, Object> prodItem : listProductItemAdd) {
    					//reason use custom service: append item of purchase order, statusId is created not approved
						Timestamp shipAfterDate = null;
						if(prodItem.get("shipAfterDate") != null){
							String shipAfterDateStr = (String)prodItem.get("shipAfterDate");
							if(!shipAfterDateStr.equals("")){
								shipAfterDate = new Timestamp(Long.parseLong(shipAfterDateStr));
							}
						}
						prodItem.put("shipAfterDate", shipAfterDate);
						Timestamp shipBeforeDate = null;
						if(prodItem.get("shipBeforeDate") != null){
							String shipBeforeDateStr = (String)prodItem.get("shipBeforeDate");
							if(!shipBeforeDateStr.equals("")){
								shipBeforeDate = new Timestamp(Long.parseLong(shipBeforeDateStr));
							}
						}
						prodItem.put("shipBeforeDate", shipBeforeDate);
						
						String quantityStr = (String)prodItem.get("quantity");
						BigDecimal quantityBig = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
						prodItem.put("quantity", quantityBig);
						
						String amountStr = (String)prodItem.get("amount");
						BigDecimal amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
						prodItem.put("amount", amount);
						
						Timestamp itemDesiredDeliveryDate = null;
						if(prodItem.get("itemDesiredDeliveryDate") != null && !prodItem.get("itemDesiredDeliveryDate").equals("")){
							String itemDesiredDeliveryDateStr = (String)prodItem.get("itemDesiredDeliveryDate");
							itemDesiredDeliveryDate = new Timestamp(Long.parseLong(itemDesiredDeliveryDateStr));
						}
						prodItem.put("itemDesiredDeliveryDate", itemDesiredDeliveryDate);
						
						Timestamp expireDate = null;
						if(prodItem.get("expireDate") != null && !prodItem.get("expireDate").equals("")){
							String expireDateStr = (String)prodItem.get("expireDate");
							expireDate = new Timestamp(Long.parseLong(expireDateStr));
						}
						prodItem.put("expireDate", expireDate);
						
						
						Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemCustomPO", prodItem);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}else{
							if(orderHeader.getString("statusId").equals("ORDER_APPROVED")){
								Map<String, Object> mapItems = FastMap.newInstance();
								mapItems.put("orderId", orderId);
								mapItems.put("orderItemSeqId", (String)resultValue.get("orderItemSeqId"));
								mapItems.put("statusId", "ITEM_APPROVED");
								mapItems.put("userLogin", userLogin);
								dispatcher.runSync("changeOrderItemStatus", mapItems);
							}
						}
    				}
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling addItemsToApprovedOrderAdvance service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> addItemsToApprovedOrderAdvanceLoadToCart(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("orderId");
    	successResult.put("orderId", orderId);
    	
    	GenericValue orderHeader = null;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		} catch (GenericEntityException e1) {
			Debug.logError(e1, "Fatal error when get order header: " + e1.toString(), module);
		}
		if (orderHeader == null) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "DANotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
		}
    	
    	// process data
    	List<Object> productListParam = (List<Object>) context.get("productList");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}
    	List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					boolean calcTax = true;
					/*if (prodItem.get("calcTax").equals("null")) {
						calcTax = prodItem.getBoolean("calcTax");
					}*/
					
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal quantityReturnPromo = BigDecimal.ZERO;
					
					try {
						if (UtilValidate.isNotEmpty(prodItem.getString("quantity"))) {
							quantity = (BigDecimal) ObjectType.simpleTypeConvert(prodItem.getString("quantity"), "BigDecimal", null, locale);
			                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
						}
						if (UtilValidate.isNotEmpty(prodItem.getString("quantityReturnPromo"))) {
							quantityReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(prodItem.getString("quantityReturnPromo"), "BigDecimal", null, locale);
			                quantityReturnPromo = quantityReturnPromo.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
						}
    		        } catch (Exception e) {
    		            Debug.logWarning(e, "Problems parsing quantity string: ", module);
    		        }
					
					if (BigDecimal.ZERO.compareTo(quantity) < 0) {
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
						productItem.put("productId", prodItem.getString("productId"));
						
						if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
							BigDecimal basePrice = new BigDecimal(-1);
							try {
								if(prodItem.containsKey("basePrice")){
									basePrice = (BigDecimal) ObjectType.simpleTypeConvert(prodItem.getString("basePrice"), "BigDecimal", null, locale);
								}
							} catch (GeneralException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
							if(basePrice.compareTo(new BigDecimal(-1)) != 0){
								productItem.put("basePrice", basePrice);
							}
						}
						
						productItem.put("prodCatalogId", prodItem.getString("prodCatalogId"));
						productItem.put("quantity", prodItem.getString("quantity"));
						productItem.put("amount", prodItem.getString("amount"));
						productItem.put("overridePrice", prodItem.getString("overridePrice"));
						productItem.put("reasonEnumId", prodItem.getString("reasonEnumId"));
						productItem.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
						productItem.put("changeComments", prodItem.getString("changeComments"));
						productItem.put("itemDesiredDeliveryDate", prodItem.getString("itemDesiredDeliveryDate"));
						//productItem.put("itemAttributesMap", prodItem.getString("itemAttributesMap"));
						productItem.put("calcTax", calcTax);
						
						productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						productItem.put("expireDate", prodItem.getString("expireDate"));
						productItem.put("shipBeforeDate", prodItem.getString("shipBeforeDate"));
						productItem.put("shipAfterDate", prodItem.getString("shipAfterDate"));
						
						productItem.put("orderId", orderId);
						productItem.put("userLogin", userLogin);
						productItem.put("locale", locale);
						
						//update
						if (prodItem.containsKey("orderItemSeqId")) productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqId"));
						//productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
						//productItem.put("productId", prodItem.getString("productId"));
						//productItem.put("unitPriceStr", prodItem.getString("unitPrice"));
						//productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						//productItem.put("quantityStr", prodItem.getString("quantity"));
						
						listProduct.add(productItem);
					}
					if (BigDecimal.ZERO.compareTo(quantityReturnPromo) < 0) {
						
						Map<String, Object> productItem = FastMap.newInstance();
						productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
						productItem.put("productId", prodItem.getString("productId"));
						
						if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
							BigDecimal basePrice = new BigDecimal(-1);
							try {
								if(prodItem.containsKey("basePrice")){
									basePrice = (BigDecimal) ObjectType.simpleTypeConvert(prodItem.getString("basePrice"), "BigDecimal", null, locale);
								}
							} catch (GeneralException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
							if(basePrice.compareTo(new BigDecimal(-1)) != 0){
								productItem.put("basePrice", basePrice);
							}
						}
						
						productItem.put("prodCatalogId", prodItem.getString("prodCatalogId"));
						productItem.put("quantity", prodItem.getString("quantityReturnPromo"));
						productItem.put("amount", prodItem.getString("amount"));
						productItem.put("overridePrice", prodItem.getString("overridePrice"));
						productItem.put("reasonEnumId", prodItem.getString("reasonEnumId"));
						productItem.put("orderItemTypeId", "PRODPROMO_ORDER_ITEM");
						productItem.put("changeComments", prodItem.getString("changeComments"));
						productItem.put("itemDesiredDeliveryDate", prodItem.getString("itemDesiredDeliveryDate"));
						//productItem.put("itemAttributesMap", prodItem.getString("itemAttributesMap"));
						productItem.put("calcTax", calcTax);
						
						productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						productItem.put("expireDate", prodItem.getString("expireDate"));
						productItem.put("shipBeforeDate", prodItem.getString("shipBeforeDate"));
						productItem.put("shipAfterDate", prodItem.getString("shipAfterDate"));
						
						productItem.put("orderId", orderId);
						productItem.put("userLogin", userLogin);
						productItem.put("locale", locale);
						
						//update
						if (prodItem.containsKey("orderItemSeqIdReturnPromo")) productItem.put("orderItemSeqId", prodItem.getString("orderItemSeqIdReturnPromo"));
						//productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
						//productItem.put("productId", prodItem.getString("productId"));
						//productItem.put("unitPriceStr", prodItem.getString("unitPrice"));
						//productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
						//productItem.put("quantityStr", prodItem.getString("quantity"));
						
						listProduct.add(productItem);
					}
				}
			}
    	} else {
    		listProduct = (List<Map<String, Object>>) context.get("productList");
    	}
    	if (UtilValidate.isEmpty(listProduct)) 
    		return successResult;
    	try {
    		List<EntityCondition> listAllConditionOI = FastList.newInstance();
    		listAllConditionOI.add(EntityCondition.makeCondition("orderId", orderId));
    		List<EntityCondition> listOrCondition = FastList.newInstance();
    		listOrCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
    		listOrCondition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
    		listAllConditionOI.add(EntityCondition.makeCondition(listOrCondition, EntityOperator.AND));
	    	List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(listAllConditionOI, EntityOperator.AND), null, null, null, false);
	    	
	    	// check order item is exists or not
	    	List<Map<String, Object>> listProductItemUpdate = new ArrayList<Map<String,Object>>();
	    	List<Map<String, Object>> listProductItemAdd = new ArrayList<Map<String,Object>>();
	    	List<EntityCondition> listAllCondition = FastList.newInstance();
	    	for (Map<String, Object> prodItem : listProduct) {
	    		listAllCondition.clear();
	    		listAllCondition.add(EntityCondition.makeCondition("productId", prodItem.get("productId")));
	    		listAllCondition.add(EntityCondition.makeCondition("quantityUomId", prodItem.get("quantityUomId")));
	    		listAllCondition.add(EntityCondition.makeCondition("orderItemTypeId", prodItem.get("orderItemTypeId")));
	    		List<EntityCondition> condsOr = FastList.newInstance();
	    		condsOr.add(EntityCondition.makeCondition("isPromo", null));
	    		condsOr.add(EntityCondition.makeCondition("isPromo", "N"));
	    		condsOr.add(EntityCondition.makeCondition("orderItemTypeId", "PRODPROMO_ORDER_ITEM"));
	    		listAllCondition.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));
	    		List<GenericValue> orderItemsExist = EntityUtil.filterByCondition(orderItems, EntityCondition.makeCondition(listAllCondition));
	    		if (UtilValidate.isNotEmpty(orderItemsExist)) {
	    			GenericValue orderItemExist = orderItemsExist.get(0);
	    			prodItem.put("orderItemSeqId", orderItemExist.get("orderItemSeqId"));
	    			listProductItemUpdate.add(prodItem);
	    		} else {
	    			if ("PRODPROMO_ORDER_ITEM".equals(prodItem.get("orderItemTypeId"))) {
	    				prodItem.put("orderItemTypeId", prodItem.get("orderItemTypeId"));
	    				listProductItemAdd.add(prodItem);
	    			} else {
	    				listProductItemAdd.add(prodItem);
	    			}
	    		}
	    	}
	    	
	    	ShoppingCart cart = null;
    		
    		// update order items
	    	if(orderHeader.getString("orderTypeId").equals("SALES_ORDER")){
	    		HttpServletRequest request = (HttpServletRequest) context.get("request");
	    		if (request == null) {
	    			Debug.logWarning("Request is null", module);
	    			return ServiceUtil.returnError("Request is null");
	    		}
		    	//if (UtilValidate.isNotEmpty(orderHeader) && UtilValidate.isNotEmpty(listProductItemUpdate)) {
		    		try {
			    		// old
			    		Map<String, Object> contextMapUpdate = processDataEditOrderPre(delegator, dispatcher, locale, userLogin, orderHeader, orderId, listProductItemUpdate);
			    		//Map<String, Object> resultValueUpdate = dispatcher.runSync("updateOrderItemsCustom", contextMapUpdate);
			    		Map<String, Object> resultValueUpdate = dispatcher.runSync("updateOrderItemsLoadToCart", contextMapUpdate);
						if (ServiceUtil.isError(resultValueUpdate)) {
							return ServiceUtil.returnError((String) resultValueUpdate.get(ModelService.ERROR_MESSAGE));
						}
						// end old
						cart = (ShoppingCart) resultValueUpdate.get("shoppingCart");
						com.olbius.basesales.shoppingcart.ShoppingCartEvents.saveCartUpdateObject(request, cart);
		    		} catch (Exception e) {
		    			Debug.logError(e, e.getMessage(), module);
		            	return ServiceUtil.returnError("Fatal occur when run service updateOrderItemsLoadToCart");
		    		}
		    	//}
	    	}
	    	
	    	if (UtilValidate.isNotEmpty(listProductItemAdd)) {
    			if ("SALES_ORDER".equals(orderHeader.getString("orderTypeId"))) {
    				if (cart != null) {
    					GenericValue orderItem = EntityUtil.getFirst(delegator.findList("OrderItem", 
    							EntityCondition.makeCondition(EntityCondition.makeCondition("orderId", orderId), 
    									EntityOperator.AND, EntityCondition.makeCondition("prodCatalogId", EntityOperator.NOT_EQUAL, null)), 
    							UtilMisc.toSet("prodCatalogId"), null, null, false));
    					Timestamp shipBeforeDate = null;
    					Timestamp shipAfterDate = null;
    					String productStoreId = cart.getProductStoreId();
    					String catalogId = null;
    					if (orderItem != null) catalogId = orderItem.getString("prodCatalogId");
    					ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
    					GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
    					for (Map<String, Object> productItem : listProductItemAdd) {
    						/* old
        					prodItem.put("locale", locale);
        					prodItem.put("userLogin", userLogin);
        					Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemOlb", prodItem);
    						if (ServiceUtil.isError(resultValue)) {
    							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
    						}
    						end old
    						*/
    						
    						// PRODUCT_ORDER_ITEM
    						String productId = (String) productItem.get("productId");
    		    			String quantityUomId = (String) productItem.get("quantityUomId");
    		    			String orderItemTypeId = (String) productItem.get("orderItemTypeId");
    		    			String quantityStr = null;
    		    			String quantityReturnPromoStr = null;
    		    			if ("PRODUCT_ORDER_ITEM".equals(orderItemTypeId)) {
    		    				quantityStr = (String) productItem.get("quantity");
    		    			} else if ("PRODPROMO_ORDER_ITEM".equals(orderItemTypeId)) {
    		    				quantityReturnPromoStr = (String) productItem.get("quantity");
    		    			}
    		    			if (quantityStr == null && quantityReturnPromoStr == null) {
    		    				continue;
    		    			}
    		    			
    		    			BigDecimal price = BigDecimal.ZERO;
    		    			BigDecimal quantity = BigDecimal.ZERO;
    		    			BigDecimal priceReturnPromo = BigDecimal.ZERO;
    		    			BigDecimal quantityReturnPromo = BigDecimal.ZERO;
    		    			
    		    			if (UtilValidate.isNotEmpty(productId) && (UtilValidate.isNotEmpty(quantityStr) || UtilValidate.isNotEmpty(quantityReturnPromoStr))) {
    		    				try {
    		    		            if (UtilValidate.isNotEmpty(quantityStr)) {
    		    		            	quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
        		    		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
        		    		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
        		    		                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		    		            } else {
        		    		                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		    		            }
    		    		            }
    		    		            if (UtilValidate.isNotEmpty(quantityReturnPromoStr)) {
    		    		            	quantityReturnPromo = (BigDecimal) ObjectType.simpleTypeConvert(quantityReturnPromoStr, "BigDecimal", null, locale);
        		    		            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
        		    		            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
        		    		                quantityReturnPromo = quantityReturnPromo.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		    		            } else {
        		    		                quantityReturnPromo = quantityReturnPromo.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
        		    		            }
    		    		            }
    		    		        } catch (Exception e) {
    		    		            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr + ", quantity return promo: " + quantityReturnPromoStr, module);
    		    		        }
    		    				
    		    				if (productStore != null) {
    		    					String addToCartRemoveIncompat = productStore.getString("addToCartRemoveIncompat");
    		    		            String addToCartReplaceUpsell = productStore.getString("addToCartReplaceUpsell");
    		    		            try {
    		    		                if ("Y".equals(addToCartRemoveIncompat)) {
    		    		                    List<GenericValue> productAssocs = null;
    		    		                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
    		    		                            EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), EntityOperator.OR, EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId)),
    		    		                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_INCOMPATABLE")), EntityOperator.AND);
    		    		                    productAssocs = delegator.findList("ProductAssoc", cond, null, null, null, false);
    		    		                    productAssocs = EntityUtil.filterByDate(productAssocs);
    		    		                    List<String> productList = FastList.newInstance();
    		    		                    for (GenericValue productAssoc : productAssocs) {
    		    		                        if (productId.equals(productAssoc.getString("productId"))) {
    		    		                            productList.add(productAssoc.getString("productIdTo"));
    		    		                            continue;
    		    		                        }
    		    		                        if (productId.equals(productAssoc.getString("productIdTo"))) {
    		    		                            productList.add(productAssoc.getString("productId"));
    		    		                            continue;
    		    		                        }
    		    		                    }
    		    		                    for (ShoppingCartItem sci : cart) {
    		    		                        if (productList.contains(sci.getProductId())) {
    		    		                            try {
    		    		                                cart.removeCartItem(sci, dispatcher);
    		    		                            } catch (CartItemModifyException e) {
    		    		                                Debug.logError(e.getMessage(), module);
    		    		                            }
    		    		                        }
    		    		                    }
    		    		                }
    		    		                if ("Y".equals(addToCartReplaceUpsell)) {
    		    		                    List<GenericValue> productList = null;
    		    		                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
    		    		                            EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId),
    		    		                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_UPGRADE")), EntityOperator.AND);
    		    		                    productList = delegator.findList("ProductAssoc", cond, UtilMisc.toSet("productId"), null, null, false);
    		    		                    if (productList != null) {
    		    		                        for (ShoppingCartItem sci : cart) {
    		    		                            if (productList.contains(sci.getProductId())) {
    		    		                                try {
    		    		                                    cart.removeCartItem(sci, dispatcher);
    		    		                                } catch (CartItemModifyException e) {
    		    		                                    Debug.logError(e.getMessage(), module);
    		    		                                }
    		    		                            }
    		    		                        }
    		    		                    }
    		    		                }
    		    		            } catch (GenericEntityException e) {
    		    		                Debug.logError(e.getMessage(), module);
    		    		            }
    		    				}
    		    				Map<String, Object> result = null;
    		    				if (BigDecimal.ZERO.compareTo(quantity) < 0) {
    		    					Map<String, Object> paramMap = new FastMap<String, Object>();
    		        				if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
    		        		        	paramMap.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
    		        		        	paramMap.put("useAsDefaultDesiredDeliveryDate", "true");
    		        		        } else {
    		        		        	paramMap.put("itemDesiredDeliveryDate", "");
    		        		        }
    		        				if (UtilValidate.isNotEmpty(quantityUomId)) {
    		        					paramMap.put("quantityUomId", quantityUomId);
    		        				}
    		        				
    		        				// Translate the parameters and add to the cart
    		        		        result = cartHelper.addToCart(catalogId, null, null, productId, null, 
    		        		        		null, null, price, null, quantity, null, null, null, null, null, 
    		        		        		shipBeforeDate, shipAfterDate, null, null, paramMap, null, Boolean.FALSE);
    		    				}
    		    		        if (BigDecimal.ZERO.compareTo(quantityReturnPromo) < 0) {
    		    		        	Map<String, Object> paramMap2 = new FastMap<String, Object>();
    		        				if (UtilValidate.isNotEmpty(cart.getDefaultItemDeliveryDate())) {
    		        					paramMap2.put("itemDesiredDeliveryDate", cart.getDefaultItemDeliveryDate());
    		        					paramMap2.put("useAsDefaultDesiredDeliveryDate", "true");
    		        		        } else {
    		        		        	paramMap2.put("itemDesiredDeliveryDate", "");
    		        		        }
    		        				if (UtilValidate.isNotEmpty(quantityUomId)) {
    		        					paramMap2.put("quantityUomId", quantityUomId);
    		        				}
    		        				String parentProductId2 = null;
    		        				// Translate the parameters and add to the cart
    		        		        result = cartHelper.addToCart(catalogId, null, null, productId, null,
    		        		        		"PRODPROMO_ORDER_ITEM", null, priceReturnPromo, null, quantityReturnPromo, null, null, null, null, null,
    		        		                shipBeforeDate, shipAfterDate, null, null, paramMap2, parentProductId2, Boolean.FALSE, Boolean.TRUE);
    		    		        }
    		    		        if (ServiceUtil.isError(result)) {
    		    		        	return ServiceUtil.returnError((String) result.get(ModelService.ERROR_MESSAGE));
    		    		        } else {
    		    		        	String orderStatusId = cart.getOrderStatusId();
    		    		        	if ("ORDER_APPROVED".equals(orderStatusId)) {
    		    		        		Integer itemId = (Integer) result.get("itemId");
    		            		        if (itemId != null) {
    		            		        	ShoppingCartItem item = cart.findCartItem(itemId);
    		            		        	if (item != null) {
    		            		        		item.setStatusId("ITEM_APPROVED");
    		            		        	}
    		            		        }
    								}
    		    		        }
    		    		        // calc the sales tax        
    		    		        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
		    		            try {
		    		                coh.calcAndAddTax();
		    		            } catch (GeneralException e) {
		    		                Debug.logError(e, module);
		    		                throw new GeneralException(e.getMessage());
		    		            }
    		    		        // TODOCHANGE usePriceWithTax promo condition
    		    		    	ProductPromoWorker.doPromotions(cart, dispatcher);
    		    		    	// end
    		    			}
        				}
    				}
    			} else if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))){
    				for (Map<String, Object> prodItem : listProductItemAdd) {
    					//reason use custom service: append item of purchase order, statusId is created not approved
						Timestamp shipAfterDate = null;
						if(prodItem.get("shipAfterDate") != null){
							String shipAfterDateStr = (String)prodItem.get("shipAfterDate");
							if(!shipAfterDateStr.equals("")){
								shipAfterDate = new Timestamp(Long.parseLong(shipAfterDateStr));
							}
						}
						prodItem.put("shipAfterDate", shipAfterDate);
						Timestamp shipBeforeDate = null;
						if(prodItem.get("shipBeforeDate") != null){
							String shipBeforeDateStr = (String)prodItem.get("shipBeforeDate");
							if(!shipBeforeDateStr.equals("")){
								shipBeforeDate = new Timestamp(Long.parseLong(shipBeforeDateStr));
							}
						}
						prodItem.put("shipBeforeDate", shipBeforeDate);
						
						String quantityStr = (String)prodItem.get("quantity");
						BigDecimal quantityBig = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
						prodItem.put("quantity", quantityBig);
						
						String amountStr = (String)prodItem.get("amount");
						BigDecimal amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
						prodItem.put("amount", amount);
						
						Timestamp itemDesiredDeliveryDate = null;
						if(prodItem.get("itemDesiredDeliveryDate") != null && !prodItem.get("itemDesiredDeliveryDate").equals("")){
							String itemDesiredDeliveryDateStr = (String)prodItem.get("itemDesiredDeliveryDate");
							itemDesiredDeliveryDate = new Timestamp(Long.parseLong(itemDesiredDeliveryDateStr));
						}
						prodItem.put("itemDesiredDeliveryDate", itemDesiredDeliveryDate);
						
						Timestamp expireDate = null;
						if(prodItem.get("expireDate") != null && !prodItem.get("expireDate").equals("")){
							String expireDateStr = (String)prodItem.get("expireDate");
							expireDate = new Timestamp(Long.parseLong(expireDateStr));
						}
						prodItem.put("expireDate", expireDate);
						
						
						Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemCustomPO", prodItem);
						if (ServiceUtil.isError(resultValue)) {
							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
						}else{
							if(orderHeader.getString("statusId").equals("ORDER_APPROVED")){
								Map<String, Object> mapItems = FastMap.newInstance();
								mapItems.put("orderId", orderId);
								mapItems.put("orderItemSeqId", (String)resultValue.get("orderItemSeqId"));
								mapItems.put("statusId", "ITEM_APPROVED");
								mapItems.put("userLogin", userLogin);
								dispatcher.runSync("changeOrderItemStatus", mapItems);
							}
						}
    				}
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling addItemsToApprovedOrderAdvance service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addItemsToApprovedOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("orderId");
    	successResult.put("orderId", orderId);
    	
    	List<Object> productListParam = (List<Object>) context.get("productList");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(productListParam) && productListParam.size() > 0){
    		if (productListParam.get(0) instanceof String) isJson = true;
    	}
    	List<Map<String, Object>> listProduct = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String productListStr = "[" + (String) productListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(productListStr)) {
				jsonArray = JSONArray.fromObject(productListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject prodItem = jsonArray.getJSONObject(i);
					Map<String, Object> productItem = FastMap.newInstance();
					boolean calcTax = true;
					/*if (prodItem.get("calcTax").equals("null")) {
						calcTax = prodItem.getBoolean("calcTax");
					}*/
					productItem.put("shipGroupSeqId", prodItem.getString("shipGroupSeqId"));
					productItem.put("productId", prodItem.getString("productId"));
					productItem.put("prodCatalogId", prodItem.getString("prodCatalogId"));
					productItem.put("quantity", prodItem.getString("quantity"));
					productItem.put("amount", prodItem.getString("amount"));
					productItem.put("overridePrice", prodItem.getString("overridePrice"));
					productItem.put("reasonEnumId", prodItem.getString("reasonEnumId"));
					productItem.put("orderItemTypeId", prodItem.getString("orderItemTypeId"));
					productItem.put("changeComments", prodItem.getString("changeComments"));
					productItem.put("itemDesiredDeliveryDate", prodItem.getString("itemDesiredDeliveryDate"));
					//productItem.put("itemAttributesMap", prodItem.getString("itemAttributesMap"));
					productItem.put("calcTax", calcTax);
					
					productItem.put("quantityUomId", prodItem.getString("quantityUomId"));
					productItem.put("expireDate", prodItem.getString("expireDate"));
					productItem.put("shipBeforeDate", prodItem.getString("shipBeforeDate"));
					productItem.put("shipAfterDate", prodItem.getString("shipAfterDate"));
					
					productItem.put("orderId", orderId);
					productItem.put("userLogin", userLogin);
					productItem.put("locale", locale);
					listProduct.add(productItem);
				}
			}
    	} else {
    		listProduct = (List<Map<String, Object>>) context.get("productList");
    	}
    	try {
        	 if (UtilValidate.isNotEmpty(listProduct)) {
        		 for (Map<String, Object> prodItem : listProduct) {
        			 Map<String, Object> resultValue = dispatcher.runSync("appendOrderItemOlb", prodItem);
        			 if (ServiceUtil.isError(resultValue)) {
        				 return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
        			 }
        		 }
        	 }
    	} catch (Exception e) {
			String errMsg = "Fatal error calling addItemsToApprovedOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
    public static Map<String, Object> addItemToApprovedOrderSales(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");
        try {
    		if (UtilValidate.isEmpty(orderId)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSOrderIdIsEmpty", locale));
    		}
    		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		if (UtilValidate.isEmpty(orderHeader)) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toMap("orderId", orderId), locale));
    		}
    		String expireDateStr = (String) context.get("expireDate");
    		Timestamp expireDate = null;
    		
    		if (UtilValidate.isNotEmpty(expireDateStr)) {
    			try {	
    				Long expireDateL = Long.parseLong(expireDateStr);
	   	        	expireDate = new Timestamp(expireDateL);
    			} catch (Exception e) {
    			 	Debug.logWarning(e, "Problems parsing expireDate string: " + expireDateStr, module);
        		}
			}
    		
    		String productId = (String) context.get("productId");
    		String quantityStr = (String) context.get("quantity");
    		String amountStr = (String) context.get("amount");
    		BigDecimal quantity = null;
    		BigDecimal amount = null;
    		try {
	            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
	            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
	            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, orderHeader.getString("productStoreId"))){
	                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
	            } else {
	                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
	            }
	            
	            amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
	        } catch (Exception e) {
	            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatQuantity", locale));
	        }
    		
    		String itemDesiredDeliveryDateStr = (String) context.get("itemDesiredDeliveryDate");
    		String shipAfterDateStr = (String) context.get("shipAfterDate");
    		String shipBeforeDateStr = (String) context.get("shipBeforeDate");
    		Timestamp itemDesiredDeliveryDate = null;
            Timestamp shipAfterDate = null;
            Timestamp shipBeforeDate = null;
            try {
    	        if (UtilValidate.isNotEmpty(itemDesiredDeliveryDateStr)) {
    	        	Long desiredDeliveryDateL = Long.parseLong(itemDesiredDeliveryDateStr);
    	        	itemDesiredDeliveryDate = new Timestamp(desiredDeliveryDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
    	        	Long shipAfterDateL = Long.parseLong(shipAfterDateStr);
    	        	shipAfterDate = new Timestamp(shipAfterDateL);
    	        }
    	        if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
    	        	Long shipBeforeDateL = Long.parseLong(shipBeforeDateStr);
    	        	shipBeforeDate = new Timestamp(shipBeforeDateL);
    	        }
            } catch (Exception e) {
            	Debug.logError(e, UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenFormatDateTime", locale));
            }
			 
    		Map<String, Object> contextMap = FastMap.newInstance();
    		contextMap.putAll(context);
    		contextMap.put("expireDate", expireDate);
    		contextMap.put("quantity", quantity);
    		contextMap.put("amount", amount);
    		contextMap.put("itemDesiredDeliveryDate", itemDesiredDeliveryDate);
    		contextMap.put("shipBeforeDate", shipBeforeDate);
    		contextMap.put("shipAfterDate", shipAfterDate);
    		
    		Map<String, Object> result = dispatcher.runSync("appendOrderItemCustom", contextMap);
    		result.remove("shoppingCart");  //remove extra parameter
    		return result;
        } catch (GenericServiceException e) {
        	Debug.logError (e, "Add item into order items: " + e.toString(), module);
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorAddItemToOrder",
	                 UtilMisc.toMap("reason", e.toString()), locale));
        } catch (GenericEntityException e) {
        	Debug.logError (e, "Get orderHeader: " + e.toString(), module);
        	return ServiceUtil.returnError("Error when get orderHeader info");
		}
    }
    
    public static Map<String, Object> addItemToApprovedOrderCustom(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        BigDecimal basePrice = (BigDecimal) context.get("basePrice");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal amount = (BigDecimal) context.get("amount");
        Timestamp itemDesiredDeliveryDate = (Timestamp) context.get("itemDesiredDeliveryDate");
        Timestamp shipBeforeDate = (Timestamp) context.get("shipBeforeDate");
        Timestamp shipAfterDate = (Timestamp) context.get("shipAfterDate");
        String overridePrice = (String) context.get("overridePrice");
        String reasonEnumId = (String) context.get("reasonEnumId");
        String orderItemTypeId = (String) context.get("reasonEnumId");
        String changeComments = (String) context.get("changeComments");
        String quantityUomId = (String) context.get("quantityUomId");
        String weightUomId = (String) context.get("weightUomId");
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        int shipGroupIdx = -1;
        try {
            shipGroupIdx = Integer.parseInt(shipGroupSeqId);
            shipGroupIdx--;
        } catch (NumberFormatException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (shipGroupIdx < 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShipGroupSeqIdInvalid", UtilMisc.toMap("shipGroupSeqId", shipGroupSeqId), locale));
        }
        if (quantity.compareTo(BigDecimal.ONE) < 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderItemQtyMustBePositive", locale));
        }

        boolean isRemoveOrderAdjustment = false; // TODOCHANGE new process order adjustment billing invoice
        
        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = OrderDuplicateServices.loadCartForUpdate(dispatcher, delegator, userLogin, orderId, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShoppingCartEmpty", locale));
        }

        // add in the new product
        try {
            if ("PURCHASE_ORDER".equals(cart.getOrderType())) {
                GenericValue supplierProduct = cart.getSupplierProduct(productId, quantity, dispatcher);
                ShoppingCartItem item = null;
                if (supplierProduct != null) {
                	Map<String, Object> attributes = FastMap.newInstance();
                	attributes.put("quantityUomId", quantityUomId);
                	attributes.put("weightUomId", weightUomId);
                	attributes.put("price", basePrice);
                    item = ShoppingCartItem.makePurchaseOrderItem(null, productId, null, quantity, null, attributes, prodCatalogId, null, orderItemTypeId, null, dispatcher, cart, supplierProduct, itemDesiredDeliveryDate, itemDesiredDeliveryDate, null);
                    cart.addItem(0, item);
                } else {
                    throw new CartItemModifyException("No supplier information found for product [" + productId + "] and quantity quantity [" + quantity + "], cannot add to cart.");
                }

                if (basePrice != null) {
                    item.setBasePrice(basePrice);
                    item.setIsModifiedPrice(true);
                }

                cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
            } else {
            	// TODOCHANGE updateOrderItems add new attribute: "itemDesiredDeliveryDate", "quantityUomId", "alternativeQuantity", "alternativeUnitPrice", "expireDate"
            	Map<String, Object> attributes = new HashMap<String, Object>();
            	Timestamp expireDate = (Timestamp) context.get("expireDate");
     			BigDecimal quantityDefault = new BigDecimal(quantity.doubleValue());
     			BigDecimal alternativeQuantity = null;
     			if (UtilValidate.isNotEmpty(quantityUomId) && UtilValidate.isNotEmpty(quantity)) {
    				BigDecimal quantityUomIdToDefault = BigDecimal.ONE;
					GenericValue productItem = null;
					try {
						productItem = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					} catch (GenericEntityException e1) {
						Debug.logError(e1, module);
					}
					if (productItem != null) {
						if (productItem.getString("quantityUomId") != null) {
							if (!quantityUomId.equals(productItem.getString("quantityUomId"))) {
								try {
	    							Map<String, Object> resultValue = dispatcher.runSync("getConvertPackingNumber", UtilMisc.toMap("productId", productItem.getString("productId"), "uomFromId", quantityUomId, "uomToId", productItem.getString("quantityUomId"), "userLogin", userLogin));
	        						if (ServiceUtil.isSuccess(resultValue)) {
	        							quantityUomIdToDefault = (BigDecimal) resultValue.get("convertNumber");
	        						}
								} catch (Exception e) {
		        		            Debug.logWarning(e, "Problems run service name = getConvertPackingNumber", module);
		        		        }
							} else {
								quantityUomIdToDefault = BigDecimal.ONE;
							}
						}
						quantityDefault = quantity.multiply(quantityUomIdToDefault);
						alternativeQuantity = quantity;
						attributes.put("alternativeQuantity", alternativeQuantity);
					}
					attributes.put("quantityUomId", quantityUomId);
					if (expireDate != null) attributes.put("expireDate", expireDate);
     			}
     			ShoppingCartItem item = null;
     			if ("PRODPROMO_ORDER_ITEM".equals(orderItemTypeId)) {
     				// TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
     				item = ShoppingCartItem.makeItem(null, productId, null, quantityDefault, null, null, null, null, null, null, null, attributes, prodCatalogId, null, orderItemTypeId, null, dispatcher, cart, null, null, null, Boolean.FALSE, Boolean.FALSE);
            		BigDecimal discountAmount = quantity.multiply(item.getBasePrice()).negate();

                    //doOrderItemPromoAction(null, newItem, discountAmount, "amount", delegator);
            		//doOrderItemPromoAction(GenericValue productPromoAction, ShoppingCartItem cartItem, BigDecimal amount, String amountField, Delegator delegator)
            		discountAmount = discountAmount.setScale(3, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            		String descAjust = UtilProperties.getMessage(resource, "ReturnPromotionProduct", locale);
                    GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment",
                            UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "amount", discountAmount,
                                    "productPromoId", null, "productPromoRuleId", null, "productPromoActionSeqId", null,
                                    "description", descAjust));
                    item.addAdjustment(orderAdjustment);

                    // set promo after create; note that to setQuantity we must clear this flag, setQuantity, then re-set the flag
                    item.setIsPromo(true);
                    if (Debug.verboseOn()) Debug.logVerbose("item return promo adjustments: " + item.getAdjustments(), module);
     			} else {
     				// Old code
     				item = ShoppingCartItem.makeItem(null, productId, null, quantityDefault, null, null, null, null, null, null, null, attributes, prodCatalogId, null, null, null, dispatcher, cart, null, null, null, Boolean.FALSE, Boolean.FALSE);
     			}
     			// TODOCHANGE change status id of order item if order approved
	        	if (item != null) {
	        		String orderStatusId = cart.getOrderStatusId();
	        		if ("ORDER_APPROVED".equals(orderStatusId)) item.setStatusId("ITEM_APPROVED");
				}
	        	// end new
     			
                // Old code: ShoppingCartItem item = ShoppingCartItem.makeItem(null, productId, null, quantity, null, null, null, null, null, null, null, null, prodCatalogId, null, null, null, dispatcher, cart, null, null, null, Boolean.FALSE, Boolean.FALSE);
                if (basePrice != null && overridePrice != null) {
                    item.setBasePrice(basePrice);
                    // special hack to make sure we re-calc the promos after a price change
                    item.setQuantity(quantity.add(BigDecimal.ONE), dispatcher, cart, false);
                    item.setQuantity(quantity, dispatcher, cart, false);
                    item.setBasePrice(basePrice);
                    item.setIsModifiedPrice(true);
                }

                // set the item in the selected ship group
                item.setDesiredDeliveryDate(itemDesiredDeliveryDate);
                item.setShipBeforeDate(shipBeforeDate);
                item.setShipAfterDate(shipAfterDate);
                cart.clearItemShipInfo(item);
                cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
            }
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map<String, Object> changeMap = UtilMisc.<String, Object>toMap("itemReasonMap", UtilMisc.<String, Object>toMap("reasonEnumId", reasonEnumId),
                                        "itemCommentMap", UtilMisc.<String, Object>toMap("changeComments", changeComments));
        
        // New method 25/11/2014, Find and add agreement into Shopping cart
        // Update method 19/02/2016
        // get applicable agreements for order entry
        String agreementId = null;
        if (!UtilValidate.isNotEmpty(agreementId)) {
        	// default select
        	agreementId = null;
        	try {
        		List<EntityCondition> agreementConds = new ArrayList<EntityCondition>();
        		agreementConds.add(EntityUtil.getFilterByDateExpr());
            	if ("SALES_ORDER".equals(cart.getOrderType())) {
            		// for a sales order, orderPartyId = billToCustomer (the customer)
    	            String customerPartyId = cart.getOrderPartyId();
    	            String companyPartyId = cart.getBillFromVendorPartyId();
    	            
    	            List<String> customerPartyIds = new ArrayList<String>();
    	        	if (UtilValidate.isNotEmpty(customerPartyId)) {
    	        		customerPartyIds.add(customerPartyId);
    	        	}
    	        	List<String> customerGroupIds = EntityUtil.getFieldListFromEntityList(
    	        					delegator.findByAnd("PartyRelationship", UtilMisc.<String, Object>toMap("partyIdTo", customerPartyId, "roleTypeIdFrom", "PARENT_MEMBER", "roleTypeIdTo", "CHILD_MEMBER", "partyRelationshipTypeId", "GROUP_ROLLUP"), null, false), 
	        						"partyIdFrom", true);
    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
    	        		//customerPartyIds.addAll(customerGroupIds);
    	        		
    	        		// check party type
    	        		for (String customerGroupId : customerGroupIds) {
    	        			GenericValue customerGroupRole = delegator.findOne("Party", UtilMisc.toMap("partyId", customerGroupId), false);
    	        			if (customerGroupRole != null && "CUSTOMER_GROUP".equals(customerGroupRole.getString("partyTypeId"))) {
    	        				customerPartyIds.add(customerGroupId);
    	        			}
    	        		}
    	        		// check party role
    	        		/*for (String customerGroupId : customerGroupIds) {
    	        			GenericValue customerGroupRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", customerGroupId, "roleTypeId", "CUSTOMER_GROUP"), false);
    	        			if (UtilValidate.isNotEmpty(customerGroupRole)) {
    	        				customerPartyIds.add(customerGroupId);
    	        			}
    	        		}*/
    	        	}
    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
    	        		// the agreement for a sales order is from the customer group to company
    	        		agreementConds.add(EntityCondition.makeCondition(
    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
    	    					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerPartyIds)), EntityOperator.AND));
    	        	} else {
    	        	    // the agreement for a sales order is from the customer to company
    	        		agreementConds.add(EntityCondition.makeCondition(
    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
    							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND));
    	        	}
    	        	/*agreementRoleCondition = EntityCondition.makeCondition(
    	        			UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerPartyId),
    	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER")), EntityOperator.AND);*/
            	}
            	List<GenericValue> agreements = delegator.findList("Agreement", EntityCondition.makeCondition(agreementConds, EntityOperator.AND), null, null, null, true);
    	        // List<GenericValue> agreementRoles = delegator.findList("AgreementRole", agreementRoleCondition, null, null, null, true);
            	
                if (agreements != null) {
                	for (GenericValue agreementItem : agreements) {
                		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
                			agreementId = agreementItem.getString("agreementId");
                			break;
                		}
                	}
                }
            } catch (GenericEntityException e) {
            	return ServiceUtil.returnError(e.getMessage());
            }
        }
        // set the agreement if specified otherwise set the currency
        if (UtilValidate.isNotEmpty(agreementId)) {
        	ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
            if (UtilValidate.isNotEmpty(cart.getCurrency())) {
                cartHelper.setCurrency(cart.getCurrency());
            }
            Map<String, Object> resultAgreement = cartHelper.selectAgreement(agreementId);
            if (ServiceUtil.isError(resultAgreement)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAgreement));
            }
        }
        // END TODOCHANGE

        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);
        // TODOCHANGE end change
        
        // save all the updated information
        try {
        	// TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
            OrderDuplicateServices.saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, false, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        // log an order note
        try {
        	StringBuffer addedItemToOrder = new StringBuffer();
        	addedItemToOrder.append(UtilProperties.getMessage(resource, "OrderAddedItemToOrder", locale));
        	addedItemToOrder.append(" ");
        	addedItemToOrder.append(productId);
        	addedItemToOrder.append(" (");
        	addedItemToOrder.append(quantity);
        	addedItemToOrder.append(")");
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", addedItemToOrder.toString(), "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> addItemToApprovedOrderCustomList(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");

        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }
        
        boolean isRemoveOrderAdjustment = false; // TODOCHANGE new process order adjustment billing invoice
        
        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = OrderDuplicateServices.loadCartForUpdate(dispatcher, delegator, userLogin, orderId, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShoppingCartEmpty", locale));
        }
        
        List<Map<String, Object>> productItems = (List<Map<String, Object>>) context.get("productList");
        if (UtilValidate.isEmpty(productItems)) {
        	Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("shoppingCart", cart);
            result.put("orderId", orderId);
            return result;
        }

        // add in the new product
        try {
        	GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
        	
        	for (Map<String, Object> productItem : productItems) {
        		String shipGroupSeqId = (String) productItem.get("shipGroupSeqId");
                String productId = (String) productItem.get("productId");
                String prodCatalogId = (String) productItem.get("prodCatalogId");
                BigDecimal basePrice = (BigDecimal) productItem.get("basePrice");
                BigDecimal quantity = (BigDecimal) productItem.get("quantity");
                BigDecimal amount = (BigDecimal) productItem.get("amount");
                Timestamp itemDesiredDeliveryDate = (Timestamp) productItem.get("itemDesiredDeliveryDate");
                //Timestamp shipBeforeDate = (Timestamp) productItem.get("shipBeforeDate");
                //Timestamp shipAfterDate = (Timestamp) productItem.get("shipAfterDate");
                //String overridePrice = (String) productItem.get("overridePrice");
                //String reasonEnumId = (String) productItem.get("reasonEnumId");
                String orderItemTypeId = (String) productItem.get("reasonEnumId");
                //String changeComments = (String) productItem.get("changeComments");
                String itemComment = (String) productItem.get("itemComment");
                String quantityUomId = (String) productItem.get("quantityUomId");
                String weightUomId = (String) productItem.get("weightUomId");

                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }
                
                int shipGroupIdx = -1;
                try {
                    shipGroupIdx = Integer.parseInt(shipGroupSeqId);
                    shipGroupIdx--;
                } catch (NumberFormatException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                if (shipGroupIdx < 0) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "OrderShipGroupSeqIdInvalid", UtilMisc.toMap("shipGroupSeqId", shipGroupSeqId), locale));
                }
                if (quantity.compareTo(BigDecimal.ONE) < 0) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderItemQtyMustBePositive", locale));
                }
                
                if ("PURCHASE_ORDER".equals(cart.getOrderType())) {
                    GenericValue supplierProduct = cart.getSupplierProduct(productId, quantity, dispatcher);
                    ShoppingCartItem item = null;
                    if (supplierProduct != null) {
                    	Map<String, Object> attributes = FastMap.newInstance();
                    	attributes.put("quantityUomId", quantityUomId);
                    	attributes.put("weightUomId", weightUomId);
                    	attributes.put("price", basePrice);
                    	attributes.put("itemComment", itemComment);
                        item = ShoppingCartItem.makePurchaseOrderItem(null, productId, amount, quantity, null, attributes, prodCatalogId, null, orderItemTypeId, null, dispatcher, cart, supplierProduct, itemDesiredDeliveryDate, itemDesiredDeliveryDate, null);
                        if ("ORDER_APPROVED".equals(orderHeader.getString("statusId"))) {
                    		item.setStatusId("ITEM_APPROVED");
                        }
                        cart.addItem(0, item);
                    } else {
                        throw new CartItemModifyException("No supplier information found for product [" + productId + "] and quantity quantity [" + quantity + "], cannot add to cart.");
                    }

                    if (basePrice != null) {
                        //item.setBasePrice(basePrice);
                        item.setIsModifiedPrice(true);
                    }

                    cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
                } else {
                	// COPY FROM addItemToApprovedOrderCustom method
                }
                
                // log an order note
                try {
                	StringBuffer addedItemToOrder = new StringBuffer();
                	addedItemToOrder.append(UtilProperties.getMessage(resource, "OrderAddedItemToOrder", locale));
                	addedItemToOrder.append(" ");
                	addedItemToOrder.append(productId);
                	addedItemToOrder.append(" (");
                	addedItemToOrder.append(quantity);
                	addedItemToOrder.append(")");
                    dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", addedItemToOrder.toString(), "internalNote", "Y", "userLogin", userLogin));
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                }
        	}
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericEntityException e1) {
        	 Debug.logError(e1, module);
             return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
		}

        Map<String, Object> changeMap = FastMap.newInstance(); 
        								//tilMisc.<String, Object>toMap("itemReasonMap", UtilMisc.<String, Object>toMap("reasonEnumId", reasonEnumId),
                                        //"itemCommentMap", UtilMisc.<String, Object>toMap("changeComments", changeComments));
        
        // New method 25/11/2014, Find and add agreement into Shopping cart
        // Update method 19/02/2016
        // get applicable agreements for order entry
        String agreementId = null;
        if (!UtilValidate.isNotEmpty(agreementId)) {
        	// default select
        	agreementId = null;
        	try {
        		List<EntityCondition> agreementConds = new ArrayList<EntityCondition>();
        		agreementConds.add(EntityUtil.getFilterByDateExpr());
            	if ("SALES_ORDER".equals(cart.getOrderType())) {
            		// for a sales order, orderPartyId = billToCustomer (the customer)
    	            String customerPartyId = cart.getOrderPartyId();
    	            String companyPartyId = cart.getBillFromVendorPartyId();
    	            
    	            List<String> customerPartyIds = new ArrayList<String>();
    	        	if (UtilValidate.isNotEmpty(customerPartyId)) {
    	        		customerPartyIds.add(customerPartyId);
    	        	}
    	        	List<String> customerGroupIds = EntityUtil.getFieldListFromEntityList(
    	        					delegator.findByAnd("PartyRelationship", UtilMisc.<String, Object>toMap("partyIdTo", customerPartyId, "roleTypeIdFrom", "PARENT_MEMBER", "roleTypeIdTo", "CHILD_MEMBER", "partyRelationshipTypeId", "GROUP_ROLLUP"), null, false), 
	        						"partyIdFrom", true);
    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
    	        		//customerPartyIds.addAll(customerGroupIds);
    	        		
    	        		// check party type
    	        		for (String customerGroupId : customerGroupIds) {
    	        			GenericValue customerGroupRole = delegator.findOne("Party", UtilMisc.toMap("partyId", customerGroupId), false);
    	        			if (customerGroupRole != null && "CUSTOMER_GROUP".equals(customerGroupRole.getString("partyTypeId"))) {
    	        				customerPartyIds.add(customerGroupId);
    	        			}
    	        		}
    	        		// check party role
    	        		/*for (String customerGroupId : customerGroupIds) {
    	        			GenericValue customerGroupRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", customerGroupId, "roleTypeId", "CUSTOMER_GROUP"), false);
    	        			if (UtilValidate.isNotEmpty(customerGroupRole)) {
    	        				customerPartyIds.add(customerGroupId);
    	        			}
    	        		}*/
    	        	}
    	        	if (UtilValidate.isNotEmpty(customerGroupIds)) {
    	        		// the agreement for a sales order is from the customer group to company
    	        		agreementConds.add(EntityCondition.makeCondition(
    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
    	    					EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, customerPartyIds)), EntityOperator.AND));
    	        	} else {
    	        	    // the agreement for a sales order is from the customer to company
    	        		agreementConds.add(EntityCondition.makeCondition(
    	        				UtilMisc.toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, companyPartyId),
    							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, customerPartyId)), EntityOperator.AND));
    	        	}
    	        	/*agreementRoleCondition = EntityCondition.makeCondition(
    	        			UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerPartyId),
    	                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER")), EntityOperator.AND);*/
            	}
            	List<GenericValue> agreements = delegator.findList("Agreement", EntityCondition.makeCondition(agreementConds, EntityOperator.AND), null, null, null, true);
    	        // List<GenericValue> agreementRoles = delegator.findList("AgreementRole", agreementRoleCondition, null, null, null, true);
            	
                if (agreements != null) {
                	for (GenericValue agreementItem : agreements) {
                		if (agreementItem.containsKey("isAuto") && "Y".equals(agreementItem.getString("isAuto"))) {
                			agreementId = agreementItem.getString("agreementId");
                			break;
                		}
                	}
                }
            } catch (GenericEntityException e) {
            	return ServiceUtil.returnError(e.getMessage());
            }
        }
        // set the agreement if specified otherwise set the currency
        if (UtilValidate.isNotEmpty(agreementId)) {
        	ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
            if (UtilValidate.isNotEmpty(cart.getCurrency())) {
                cartHelper.setCurrency(cart.getCurrency());
            }
            Map<String, Object> resultAgreement = cartHelper.selectAgreement(agreementId);
            if (ServiceUtil.isError(resultAgreement)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultAgreement));
            }
        }
        // END TODOCHANGE

        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);
        // TODOCHANGE end change
        
        // save all the updated information
        try {
        	// TODOCHANGE add new parameter isRemoveOrderAdjustment, process order adjustment billing invoice
            OrderDuplicateServices.saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, false, isRemoveOrderAdjustment);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }
    
    public static Map<String, Object> checkAndAddRelSalesExecutive(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        //Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        String customerId = (String) context.get("partyId");
        String salesExecutiveId = (String) context.get("salesExecutiveId");
        try {
        	// check has relationship?
        	String currencyOrigizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        	PartyWorker.checkAndAddRelSalesExecutive(delegator, currencyOrigizationId, salesExecutiveId, customerId);
        } catch (Exception e) {
			String errMsg = "Fatal error calling checkAndAddRelSalesExecutive service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
        
	    return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListReturnSalesOrder(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		//Locale locale = (Locale) context.get("locale");
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
		
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
    		boolean isDistributor = false;
    		boolean isEmpl = false;
    		boolean isSalesman = false;
    		if (securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "RETURN_ORDER_VIEW")) {
    			isEmpl = true;
    		}
    		if (securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "DIS_RETURNORDER")) {
    			isDistributor = true;
    		}
			if (securityOlb.olbiusHasPermission(userLogin, "VIEW", "ENTITY", "SALESMAN_RETURNORDER")) {
				isSalesman = true;
			}
    		//check permission for each order type
			if (!isEmpl && !isDistributor && !isSalesman) {
				//Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + userLogin.get("userLoginId") + " attempt to run manual payment transaction!", module);
				//successResult = ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSTransactionNotAuthorized", locale));
				successResult.put("listIterator", listIterator);
	            return successResult;
			}
			if (isEmpl) {
				/*Map<String, Object> tmpResult = dispatcher.runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", userLogin));
	    		if (ServiceUtil.isError(tmpResult)) return tmpResult;
	    		List<GenericValue> listStore = (List<GenericValue>) tmpResult.get("listProductStore");
	    		List<String> productStoreIds = null;
	    		if (UtilValidate.isNotEmpty(listStore)) {
	    			productStoreIds = EntityUtil.getFieldListFromEntityList(listStore, "productStoreId", true);
	    		}*/
				boolean isGetAll = false;
				if (parameters.containsKey("ia") && parameters.get("ia").length > 0) {
	    			String ia = parameters.get("ia")[0];
	    			if ("Y".equals(ia)) {
	    				isGetAll = true;
	    			}
				}
				if (!isGetAll) {
					boolean isGetByCreatedBy = false;
					if (SalesPartyUtil.hasRole(delegator, userLogin.getString("partyId"), EmplRoleEnum.CALLCENTER)) {
						isGetByCreatedBy = true;
						
						if (SalesPartyUtil.hasRole(delegator, userLogin.getString("partyId"), EmplRoleEnum.CALLCENTER_MANAGER)) {
							isGetByCreatedBy = false;
						}
					}
		    		if (isGetByCreatedBy) {
		    			listAllConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLogin.getString("partyId")));
		    		}
				}
				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				listAllConditions.add(EntityCondition.makeCondition("toPartyId", EntityOperator.EQUALS, organizationId));
			} else if(isSalesman && !isDistributor){
    			// if userlogin is salesman and isn't distributor, getList ReturnOrder createBy salesman  and partyIdTo Distributor
    			String userLoginPartyId = userLogin.getString("partyId");
				List<GenericValue> listSalesman = delegator.findList("PartySalesman", EntityCondition.makeCondition("partyId", userLoginPartyId), null, null, null, false);
				if (UtilValidate.isNotEmpty(listSalesman)) {
					List<String> distributorIds = EntityUtil.getFieldListFromEntityList(listSalesman, "distributorId", true);
					listAllConditions.add(EntityCondition.makeCondition("toPartyId", EntityOperator.IN, distributorIds));
				}
				listAllConditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginPartyId));

			}else{
				String userLoginPartyId = userLogin.getString("partyId");
				listAllConditions.add(EntityCondition.makeCondition("toPartyId", EntityOperator.EQUALS, userLoginPartyId));
			}
			
			String partyId = null;
    		String statusId = null;
    		if (parameters.containsKey("statusId") && parameters.get("statusId").length > 0) {
    			statusId = parameters.get("statusId")[0];
			}
			if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
				partyId = parameters.get("partyId")[0];
			}
			listAllConditions.add(EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS, "CUSTOMER_RETURN"));
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
			}
			//listAllConditions.add(EntityCondition.makeCondition("salesMethodChannelEnumId", EntityOperator.NOT_EQUAL, "BHKENH_POS"));
			/*if (UtilValidate.isNotEmpty(productStoreIds)) {
				listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
		        listIterator = delegator.find("OrderHeaderAndOrderRoleFromTo", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			}*/
			
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-entryDate");
			}
			listIterator = delegator.find("ReturnHeaderOrderDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListReturnSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListReturnPurchOrder(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	//Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	try {
			String userLoginPartyId = userLogin.getString("partyId");
			listAllConditions.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, userLoginPartyId));
    		
    		listAllConditions.add(EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS, "CUSTOMER_RETURN"));
    		//listIterator = delegator.find("ReturnHeader", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
    		listIterator = delegator.find("ReturnHeaderOrderDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListReturnPurchOrder service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    /** Service to cancel an order item quantity */
    public static Map<String, Object> cancelOrderItemLoadToCart(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        BigDecimal cancelQuantity = (BigDecimal) context.get("cancelQuantity");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
        Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));

        // debugging message info
        String itemMsgInfo = orderId + " / " + orderItemSeqId + " / " + shipGroupSeqId;

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();

        boolean hasPermission = OrderDuplicateServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }
        
        try {
        	// TODOCHANGE order adjustment promotion
            // check order was created invoice? true/false
            List<GenericValue> orderAdjustmentsOI = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), null, false);
            if (UtilValidate.isNotEmpty(orderAdjustmentsOI)) {
            	for (GenericValue ordAdjItem : orderAdjustmentsOI) {
    				List<GenericValue> orderAdjustmentBillings = delegator.findByAnd("OrderAdjustmentBilling", UtilMisc.toMap("orderAdjustmentId", ordAdjItem.get("orderAdjustmentId")), null, false);
    				if (UtilValidate.isEmpty(orderAdjustmentBillings)) {
    					// remove order adjustment attribute if exists
    					List<GenericValue> orderAdjustmentAttrs = delegator.findByAnd("OrderAdjustmentAttribute", UtilMisc.toMap("orderAdjustmentId", ordAdjItem.get("orderAdjustmentId")), null, false);
    					if (UtilValidate.isNotEmpty(orderAdjustmentAttrs)) {
    						delegator.removeAll(orderAdjustmentAttrs);
    					}
    					
    					// remove order adjustment of this order item
    					delegator.removeValue(ordAdjItem);
    				} else {
    					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderCannotCancelThisOrderItemBecaseExistedInvoice",locale));
    				}
    			}
            }
        } catch (Exception e) {
        	Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId);
        if (orderItemSeqId != null) {
            fields.put("orderItemSeqId", orderItemSeqId);
        }
        if (shipGroupSeqId != null) {
            fields.put("shipGroupSeqId", shipGroupSeqId);
        }

        List<GenericValue> orderItemShipGroupAssocs = null;
        try {
            orderItemShipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", fields, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotGetOrderItemAssocEntity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
        }

        if (orderItemShipGroupAssocs != null) {
            for (GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocs) {
                GenericValue orderItem = null;
                try {
                    orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem", false);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }

                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                }

                BigDecimal aisgaCancelQuantity =  orderItemShipGroupAssoc.getBigDecimal("cancelQuantity");
                if (aisgaCancelQuantity == null) {
                    aisgaCancelQuantity = BigDecimal.ZERO;
                }
                BigDecimal availableQuantity = orderItemShipGroupAssoc.getBigDecimal("quantity").subtract(aisgaCancelQuantity);

                BigDecimal itemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                if (itemCancelQuantity == null) {
                    itemCancelQuantity = BigDecimal.ZERO;
                }
                BigDecimal itemQuantity = orderItem.getBigDecimal("quantity").subtract(itemCancelQuantity);
                if (availableQuantity == null) availableQuantity = BigDecimal.ZERO;
                if (itemQuantity == null) itemQuantity = BigDecimal.ZERO;

                BigDecimal thisCancelQty = null;
                if (cancelQuantity != null) {
                    thisCancelQty = cancelQuantity;
                } else {
                    thisCancelQty = availableQuantity;
                }

                if (availableQuantity.compareTo(thisCancelQty) >= 0) {
                    if (availableQuantity.compareTo(BigDecimal.ZERO) == 0) {
                        continue;  //OrderItemShipGroupAssoc already cancelled
                    }
                    orderItem.set("cancelQuantity", itemCancelQuantity.add(thisCancelQty));
                    orderItemShipGroupAssoc.set("cancelQuantity", aisgaCancelQuantity.add(thisCancelQty));

                    try {
                        List<GenericValue> toStore = UtilMisc.toList(orderItem, orderItemShipGroupAssoc);
                        delegator.storeAll(toStore);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderUnableToSetCancelQuantity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                    }

                    //  create order item change record
                    if (!"Y".equals(orderItem.getString("isPromo"))) {
                        String reasonEnumId = null;
                        String changeComments = null;
                        if (UtilValidate.isNotEmpty(itemReasonMap)) {
                            reasonEnumId = itemReasonMap.get(orderItem.getString("orderItemSeqId"));
                        }
                        if (UtilValidate.isNotEmpty(itemCommentMap)) {
                            changeComments = itemCommentMap.get(orderItem.getString("orderItemSeqId"));
                        }

                        Map<String, Object> serviceCtx = FastMap.newInstance();
                        serviceCtx.put("orderId", orderItem.getString("orderId"));
                        serviceCtx.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                        serviceCtx.put("cancelQuantity", thisCancelQty);
                        serviceCtx.put("changeTypeEnumId", "ODR_ITM_CANCEL");
                        serviceCtx.put("reasonEnumId", reasonEnumId);
                        serviceCtx.put("changeComments", changeComments);
                        serviceCtx.put("userLogin", userLogin);
                        Map<String, Object> resp = null;
                        try {
                            resp = dispatcher.runSync("createOrderItemChange", serviceCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                        if (ServiceUtil.isError(resp)) {
                            return ServiceUtil.returnError((String)resp.get(ModelService.ERROR_MESSAGE));
                        }
                    }

                    if (!("Y".equals(orderItem.getString("isPromo")) && "PRODUCT_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId")))) {
                    // log an order note
                    try {
                        BigDecimal quantity = thisCancelQty.setScale(1, orderRounding);
                        String cancelledItemToOrder = UtilProperties.getMessage("OrderUiLabels", "OrderCancelledItemToOrder", locale);
                        dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", cancelledItemToOrder + " " + 
                                orderItem.getString("productId") + " (" + quantity + ")", "internalNote", "Y", "userLogin", userLogin));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                    }
                    }

                    if (thisCancelQty.compareTo(itemQuantity) >= 0) {
                        // all items are cancelled -- mark the item as cancelled
                        Map<String, Object> statusCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId"), "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderUnableToCancelOrderLine", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    } else {
                        // reverse the inventory reservation
                        Map<String, Object> invCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId"), "shipGroupSeqId",
                                shipGroupSeqId, "cancelQuantity", thisCancelQty, "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("cancelOrderItemInvResQty", invCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderUnableToUpdateInventoryReservations", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    }
                } else {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderInvalidCancelQuantityCannotCancel", UtilMisc.toMap("thisCancelQty",thisCancelQty), locale));
                }
            }
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
        }

        return ServiceUtil.returnSuccess();
    }
    
    // Auto create OrderAdjustments
    // TODOCHANGE convert from simple to java with the same name service
    @SuppressWarnings("unchecked")
	public static Map<String, Object> recreateOrderAdjustmentsLoadToCart(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");
        
        try {
        	GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            
        	//boolean isRemoveAdjustment = false; // TODOCHANGE order adjustment promotion
            try {
    	        /*// TODOCHANGE order adjustment promotion
    			// check order was created invoice? true/false
    	        List<String> orderAdjustmentIds = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId), null, false), "orderAdjustmentId", true);
    	        if (UtilValidate.isNotEmpty(orderAdjustmentIds)) {
    	        	List<GenericValue> orderAdjustmentBillings = delegator.findList("OrderAdjustmentBilling", EntityCondition.makeCondition("orderAdjustmentId", EntityOperator.IN, orderAdjustmentIds), null, null, null, false);
    	        	if (!UtilValidate.isNotEmpty(orderAdjustmentBillings)) {
    	        		isRemoveAdjustment = true;
    	        	}
    	        }
    	        // end new */
            	
    	        // all existing promo order items are cancelled
    	        List<GenericValue> orderItems = order.getRelated("OrderItem", null, null, false);
    	        if (orderItems != null) {
    	        	Map<String, Object> cancelOrderItemInMap = FastMap.newInstance();
    	        	for (GenericValue orderItem : orderItems) {
    	        		if ("Y".equals(orderItem.getString("isPromo")) 
    	        				&& !"ITEM_CANCELLED".equals(orderItem.getString("statusId"))
    	        				&& !"PRODPROMO_ORDER_ITEM".equals(orderItem.getString("PRODPROMO_ORDER_ITEM"))) // TODOCHANGE new orderItemType PRODPROMO_ORDER_ITEM
    	        		{
    	        			cancelOrderItemInMap.clear();
    	        			cancelOrderItemInMap = ServiceUtil.setServiceFields(dispatcher, "cancelOrderItemNoActions", (Map<String, Object>) context, userLogin, null, locale);
    	        			cancelOrderItemInMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
    	        			dispatcher.runSync("cancelOrderItemNoActions", cancelOrderItemInMap);
    	        		}
    	        	}
    	        }
            } catch (GenericEntityException | GeneralServiceException | GenericServiceException ex1) {
                Debug.logWarning(ex1, "Error invoking getRelated in isCatalogInventoryAvailable", module);
            }
            
            List<GenericValue> orderAdjustments = order.getRelated("OrderAdjustment", null, null, false);
            
            // Accumulate the total existing promotional adjustment
            BigDecimal existingOrderAdjustmentTotal = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(orderAdjustments)) {
            	for (GenericValue orderAdjustment : orderAdjustments) {
            		if (UtilValidate.isNotEmpty(orderAdjustment.get("productPromoId"))) {
            			existingOrderAdjustmentTotal = existingOrderAdjustmentTotal.add(orderAdjustment.getBigDecimal("amount"));
            		}
            	}
            }
            existingOrderAdjustmentTotal = existingOrderAdjustmentTotal.setScale(3);
            
            /*// Recalculate the promotions for the order
            Map<String, Object> loadCartFromOrderInMap = ServiceUtil.setServiceFields(dispatcher, "loadCartFromOrder", (Map<String, Object>) context, userLogin, null, locale);
            loadCartFromOrderInMap.put("skipInventoryChecks", true);
            loadCartFromOrderInMap.put("skipProductChecks", true);
            Map<String, Object> resultLoadCart = dispatcher.runSync("loadCartFromOrder", loadCartFromOrderInMap);
            
            ShoppingCart cart = null;
            if (ServiceUtil.isSuccess(resultLoadCart)) {
            	cart = (ShoppingCart) resultLoadCart.get("shoppingCart");
            }
            
            if (cart == null) {
            	return ServiceUtil.returnError("Shopping Cart is null");
            }*/
            
            ShoppingCart cart = null;
            try {
            	// TODOCHANGE new process order adjustment billing invoice
            	boolean isRemoveOrderAdjustment = false;
            	Map<String, Object> resultLoadCartForUpdate = dispatcher.runSync("loadCartForUpdateCustom", UtilMisc.toMap("orderId", orderId, "isRemoveOrderAdjustment", isRemoveOrderAdjustment, "userLogin", userLogin));
            	if (ServiceUtil.isError(resultLoadCartForUpdate)) {
            		return ServiceUtil.returnError((String) resultLoadCartForUpdate.get(ModelService.ERROR_MESSAGE));
            	}
                cart = (ShoppingCart) resultLoadCartForUpdate.get("shoppingCart");
            } catch (GeneralException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
            if (cart == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderShoppingCartEmpty", locale));
            }
            
            // TODOCHANGE new process order adjustment billing invoice
            /*// TODOCHANGE
            // remove the adjustments
            try {
                List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
                adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
                List<EntityCondition> exprs = new LinkedList<EntityCondition>();
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_TAX"));
                exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PRICE_CORRECT"));
                adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
                EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
                delegator.removeByCondition("OrderAdjustment", cond);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }*/
            List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
            adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            List<EntityCondition> exprs = new LinkedList<EntityCondition>();
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PRICE_CORRECT"));
            adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
            EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
            
            List<GenericValue> listOrderAdjExists = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(cond), null, null, null, false);
            // end new
            
            // run promotions to handle all changes in the cart
            ProductPromoWorker.doPromotions(cart, dispatcher);
            
            // calc the sales tax  
            CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
            try {
                coh.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            // end new
            
            // TODOCHANGE new process order adjustment billing invoice. Recreate Order adjustment for each cart item
            List<GenericValue> orderItemShipGroupInfo = cart.makeAllShipGroupInfos();
            List<GenericValue> toBeStored = new LinkedList<GenericValue>();
            // set the order item ship groups
            // List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
            if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
                for (GenericValue valueObj : orderItemShipGroupInfo) {
                    valueObj.set("orderId", orderId);
                    if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                        //TODOCHANGE check adjustment exists, update this adjustment
                    	List<GenericValue> ordAdjEquals = EntityUtil.filterByAnd(listOrderAdjExists, 
                    			UtilMisc.toMap("orderId", valueObj.get("orderId"), "orderItemSeqId", valueObj.get("orderItemSeqId"), 
                    				"orderAdjustmentTypeId", valueObj.get("orderAdjustmentTypeId"), "shipGroupSeqId", valueObj.get("shipGroupSeqId")));
                        if (UtilValidate.isNotEmpty(ordAdjEquals)) {
                        	// new code
                        	listOrderAdjExists.removeAll(ordAdjEquals);
                        	/*for (GenericValue item : ordAdjEquals) {
                        		item.setNonPKFields(valueObj);
                        	}
                        	toBeStored.addAll(ordAdjEquals);*/
                        	
                        	String orderAdjustmentId = ordAdjEquals.get(0).getString("orderAdjustmentId");
                        	valueObj.put("orderAdjustmentId", orderAdjustmentId);
                        	toBeStored.add(valueObj);
                        } else {
                        	// old code
                        	// shipping / tax adjustment(s)
                            if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                                valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                            }
                            valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                            valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                            valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                            
                        	toBeStored.add(valueObj);
                        }
                    }
                    // toBeStored.add(valueObj);
                }
            }
            if (UtilValidate.isNotEmpty(listOrderAdjExists)) delegator.removeAll(listOrderAdjExists);
            
            delegator.storeAll(toBeStored);
            // end new
            
            HttpServletRequest request = (HttpServletRequest) context.get("request");
    		if (request == null) {
    			Debug.logWarning("Request is null", module);
    			return ServiceUtil.returnError("Request is null");
    		}
			com.olbius.basesales.shoppingcart.ShoppingCartEvents.saveCartUpdateObject(request, cart);
            
            /*// TODOCHANGE default estimatedShipDate
            String defaultShipGroupSeqId = "";
            List<GenericValue> orderItemShipGroups = delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId), null, false);
            if (UtilValidate.isNotEmpty(orderItemShipGroups)) {
            	GenericValue firstShipGroupSeqId = EntityUtil.getFirst(orderItemShipGroups);
            	defaultShipGroupSeqId = firstShipGroupSeqId.getString("shipGroupSeqId");
            }
            // end new
            
            List<ShoppingCartItem> items = cart.items();
            for (ShoppingCartItem item : items) {
            	String orderItemSeqId = item.getOrderItemSeqId();
            	if (!UtilValidate.isNotEmpty(orderItemSeqId)) {
            		// this is a new (promo) item
            		// a new order item is created
            		GenericValue newOrderItem = delegator.makeValue("OrderItem");
            		newOrderItem.put("orderId", orderId);
            		newOrderItem.put("orderItemTypeId", item.getItemType());
            		newOrderItem.put("selectedAmount", item.getSelectedAmount());
            		newOrderItem.put("unitPrice", item.getBasePrice());
            		newOrderItem.put("unitListPrice", item.getListPrice());
            		newOrderItem.put("itemDescription", item.getName());
            		newOrderItem.put("statusId", item.getStatusId());
            		newOrderItem.put("productId", item.getProductId());
            		newOrderItem.put("quantity", item.getQuantity());
            		// TODOCHANGE default estimatedShipDate
            		newOrderItem.put("shipBeforeDate", item.getShipBeforeDate());
            		newOrderItem.put("shipAfterDate", item.getShipAfterDate());
            		newOrderItem.put("estimatedDeliveryDate", item.getDesiredDeliveryDate());
            		// add new attribute: "quantityUomId", "alternativeQuantity", "alternativeUnitPrice", "expireDate"
            		newOrderItem.put("quantityUomId", item.getAttribute("quantityUomId"));
            		newOrderItem.put("alternativeQuantity", item.getAlternativeQuantity());
            		newOrderItem.put("alternativeUnitPrice", item.getAlternativeUnitPrice());
            		newOrderItem.put("expireDate", item.getAttribute("expireDate"));
            		newOrderItem.put("changeByUserLoginId", userLogin.getString("userLoginId"));
            		// end new
            		newOrderItem.put("isModifiedPrice", "N");
            		newOrderItem.put("isPromo", "Y");
            		if (!UtilValidate.isNotEmpty(newOrderItem.get("statusId"))) newOrderItem.put("statusId", "ITEM_CREATED");
            		delegator.setNextSubSeqId(newOrderItem, "orderItemSeqId", 5, 1);
            		delegator.create(newOrderItem);
            		// and the orderItemSeqId is assigned to the shopping cart item
            		item.setOrderItemSeqId(newOrderItem.getString("orderItemSeqId"));
            		
            		if (isRemoveAdjustment) {
            			// TODOCHANGE order adjustment promotion
            			// create order adjustment attribute if exists ...
            			// create order adjustment of this order item
            			List<GenericValue> newOrderAdjustments = item.getAdjustments();
            			if (UtilValidate.isNotEmpty(newOrderAdjustments)) {
            				for (GenericValue ordAdj : newOrderAdjustments) {
            					String ordAdjSeqId = delegator.getNextSeqId("OrderAdjustment");
            					ordAdj.put("orderAdjustmentId", ordAdjSeqId);
            					ordAdj.put("orderId", newOrderItem.getString("orderId"));
            					ordAdj.put("orderItemSeqId", newOrderItem.getString("orderItemSeqId"));
            				}
            				delegator.storeAll(newOrderAdjustments);
            			}
            			
            			if (UtilValidate.isNotEmpty(defaultShipGroupSeqId)) {
            				GenericValue ordItemShipGroupAssoc = delegator.makeValue("OrderItemShipGroupAssoc");
            				ordItemShipGroupAssoc.put("shipGroupSeqId", defaultShipGroupSeqId);
            				ordItemShipGroupAssoc.put("orderId", newOrderItem.get("orderId"));
            				ordItemShipGroupAssoc.put("orderItemSeqId", newOrderItem.get("orderItemSeqId"));
            				ordItemShipGroupAssoc.put("quantity", newOrderItem.get("quantity"));
            				delegator.create(ordItemShipGroupAssoc);
            			}
            			// end new
            		}
            	}
            }
            
            List<GenericValue> adjustments = cart.makeAllAdjustments();
            // Accumulate the new promotion total from the recalculated promotion adjustments
            BigDecimal newOrderAdjustmentTotal = BigDecimal.ZERO;
            for (GenericValue adjustment : adjustments) {
            	if (UtilValidate.isNotEmpty(adjustment.get("productPromoId"))) {
            		newOrderAdjustmentTotal = newOrderAdjustmentTotal.add(adjustment.getBigDecimal("amount"));
            		newOrderAdjustmentTotal = newOrderAdjustmentTotal.setScale(3);
            	}
            }
            
            // Determine the difference between existing and new promotion adjustment totals, if any
            BigDecimal orderAdjustmentTotalDifference = newOrderAdjustmentTotal.subtract(existingOrderAdjustmentTotal);
            orderAdjustmentTotalDifference = orderAdjustmentTotalDifference.setScale(3);
            
            // TODOCHANGE add if condition
            if ("ORDER_CANCELLED".equals(order.getString("statusId"))) {
            	// If the total has changed, create an OrderAdjustment to reflect the fact
            	if (orderAdjustmentTotalDifference.compareTo(BigDecimal.ZERO) != 0) {
            		Map<String, Object> createOrderAdjContext = FastMap.newInstance();
            		createOrderAdjContext.put("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT");
            		createOrderAdjContext.put("orderId", orderId);
            		createOrderAdjContext.put("orderItemSeqId", "_NA_");
            		createOrderAdjContext.put("shipGroupSeqId", "_NA_");
            		createOrderAdjContext.put("description", "Adjustment due to order change");
            		createOrderAdjContext.put("amount", orderAdjustmentTotalDifference);
            		createOrderAdjContext.put("userLogin", userLogin);
            		createOrderAdjContext.put("locale", locale);
            		Map<String, Object> resultCreateOrderAdjustment = dispatcher.runSync("createOrderAdjustment", createOrderAdjContext);
            		if (ServiceUtil.isError(resultCreateOrderAdjustment)) {
            			return ServiceUtil.returnError((String) resultCreateOrderAdjustment.get(ModelService.ERROR_MESSAGE));
            		}
            	}
            }*/
        } catch (GenericEntityException e) {
        	String errMsg = "Fatal error entity calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
        }/* catch (GeneralServiceException e) {
        	String errMsg = "Fatal error service calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		} catch (GenericServiceException e) {
			String errMsg = "Fatal error service calling jqGetListSalesOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}*/

        return ServiceUtil.returnSuccess();
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> attachFilesPaymentOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        String orderId = (String) context.get("orderId");
        List<Map<String, Object>> listFiles = (List<Map<String, Object>>) context.get("listFiles");
        
        try {
        	GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
        	if (orderHeader == null) {
    			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
    		}
        	
        	if (UtilValidate.isEmpty(listFiles)) {
        		return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSListOfFileIsEmpty", locale));
        	}
        	
        	//GenericValue superUser = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        	//if (superUser == null) {
        	//	return ServiceUtil.returnError("You haven't permission to create Payment order!");
        	//}
        	
        	for (Map<String, Object> fileItem : listFiles) {
        		String _uploadedFile_fileName = (String) fileItem.get("_uploadedFile_fileName");
        		String _uploadedFile_contentType = (String) fileItem.get("_uploadedFile_contentType");
        		ByteBuffer uploadedFile = (ByteBuffer) fileItem.get("uploadedFile");
        		
        		String uploadFileNameStore = orderHeader.getString("orderId") + "_" + UtilDateTime.nowTimestamp().getTime();
        		
        		// create Content
        		String contentId = null;
        		Map<String, Object> contentCtx = FastMap.newInstance();
	            contentCtx.put("contentTypeId", "DOCUMENT");
	            contentCtx.put("userLogin", userLogin);
            	try {
            		Map<String, Object> contentResult = dispatcher.runSync("createContent", contentCtx);
	                if (ServiceUtil.isError(contentResult)) {
	                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(contentResult));
	                }
	                contentId = (String) contentResult.get("contentId");
	            } catch (GenericServiceException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
	            
            	// store file into JCR
            	String path = null;
	            contentCtx.clear();
	            contentCtx.put("userLogin", userLogin);
	            contentCtx.put("uploadedFile", uploadedFile);
	            contentCtx.put("_uploadedFile_fileName", uploadFileNameStore);
	            contentCtx.put("_uploadedFile_contentType", _uploadedFile_contentType);
	            contentCtx.put("public", "Y");
	            contentCtx.put("folder", "/order");
	            try {
	                Map<String, Object> fileResult = dispatcher.runSync("jackrabbitUploadFile", contentCtx);
	                if (ServiceUtil.isError(fileResult)) {
	                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(fileResult));
	                }
	                path = (String) fileResult.get("path");
	            } catch (GenericServiceException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
	            ImageManagementServices.createContentAndDataResource(dctx, userLogin, _uploadedFile_fileName, path, contentId, _uploadedFile_contentType);
	            
	            // attach content to order
	            String orderContentTypeId = "IMAGE_URL";
	            Map<String, Object> orderContentCtx = FastMap.newInstance();
	            orderContentCtx.put("orderId", orderId);
	            orderContentCtx.put("userLogin", userLogin);
	            orderContentCtx.put("contentId", contentId);
	            orderContentCtx.put("orderContentTypeId", orderContentTypeId);
	            orderContentCtx.put("fromDate", UtilDateTime.nowTimestamp());
	            try {
	                Map<String, Object> orderContentResult = dispatcher.runSync("createOrderContentPaymentOrder", orderContentCtx);
	                if (ServiceUtil.isError(orderContentResult)) {
	                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(orderContentResult));
	                }
	            } catch (GenericServiceException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
	            
	            Map<String, Object> contentApprovalCtx = FastMap.newInstance();
	            contentApprovalCtx.put("contentId", contentId);
	            contentApprovalCtx.put("userLogin", userLogin);
	            try {
	            	Map<String, Object> imageContentApprovalResult = dispatcher.runSync("createImageContentApproval", contentApprovalCtx);
	            	if (ServiceUtil.isError(imageContentApprovalResult)) {
	                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(imageContentApprovalResult));
	                }
	            } catch (GenericServiceException e) {
	                Debug.logError(e, module);
	                return ServiceUtil.returnError(e.getMessage());
	            }
        	}
        } catch (GenericEntityException e) {
        	String errMsg = "Fatal error entity calling attachFilesPaymentOrder service: " + e.toString();
			Debug.logError(e, errMsg, module);
        }
		
		result.put("orderId", orderId);
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOrderItemDetail(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	EntityListIterator listIterator = null;
		try {
			String orderId = SalesUtil.getParameter(parameters, "orderId");
			if (UtilValidate.isNotEmpty(orderId)) {
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (orderHeader != null) {
					listAllConditions.add(EntityCondition.makeCondition("orderId", orderId));
					if (!"ORDER_CANCELLED".equals(orderHeader.getString("statusId"))) {
						listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
					}
					
					if (UtilValidate.isEmpty(listSortFields)) {
						listSortFields.add("isPromo");
					}
					
					listIterator = delegator.find("OrderItemAndProduct", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderItemDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> updateOrderAdjustmentOlb(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        Security security = ctx.getSecurity();
        OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        if (!(securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "SALES_ORDER_EDIT"))) {
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouHavenotUpdatePermission", locale));
        }
		
		// process data
    	List<Object> orderAdjListParam = (List<Object>) context.get("listOrderAdj");
    	boolean isJson = false;
    	if (UtilValidate.isNotEmpty(orderAdjListParam) && orderAdjListParam.size() > 0){
    		if (orderAdjListParam.get(0) instanceof String) isJson = true;
    	}
		List<Map<String, Object>> orderAdjData = new ArrayList<Map<String,Object>>();
    	if (isJson){
			String orderAdjListStr = "[" + (String) orderAdjListParam.get(0) + "]";
			JSONArray jsonArray = new JSONArray();
			if (UtilValidate.isNotEmpty(orderAdjListStr)) {
				jsonArray = JSONArray.fromObject(orderAdjListStr);
			}
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject item = jsonArray.getJSONObject(i);
					Map<String, Object> itemMap = FastMap.newInstance();
					if (item.containsKey("orderAdjustmentId")) itemMap.put("orderAdjustmentId", item.getString("orderAdjustmentId"));
					if (item.containsKey("orderAdjustmentTypeId")) itemMap.put("orderAdjustmentTypeId", item.getString("orderAdjustmentTypeId"));
					if (item.containsKey("description")) itemMap.put("description", item.getString("description"));
					
					BigDecimal amount = BigDecimal.ZERO;
					if (item.containsKey("amount")) {
	    	        	String amountStr = item.getString("amount");
	    	        	if (UtilValidate.isNotEmpty(amountStr)) {
		                    try {
		                    	amount = (BigDecimal) ObjectType.simpleTypeConvert(amountStr, "BigDecimal", null, locale);
		                    } catch (Exception e) {
		                        Debug.logWarning(e, "Problems parsing quantity string: " + amountStr, module);
		                        amount = BigDecimal.ZERO;
		                    }
		                }
						itemMap.put("amount", amount);
					}
					if (amount != null && BigDecimal.ZERO.compareTo(amount) != 0) {
						orderAdjData.add(itemMap);
					}
				}
			}
    	} else {
    		orderAdjData = (List<Map<String, Object>>) context.get("listOrderAdj");
    	}
    	//if (UtilValidate.isEmpty(orderAdjData)) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSYouNotYetChooseRow", locale));
		
    	// Get parameter information general
        String orderId = (String) context.get("orderId");
        String comments = (String) context.get("comments");
        try {
        	String shipGroupSeqId = null;
        	GenericValue orderItemShipGroup = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId), null, false));
        	if (orderItemShipGroup != null) {
        		shipGroupSeqId = orderItemShipGroup.getString("shipGroupSeqId");
        	}
        	
        	List<EntityCondition> conds = getCondsOrderAdjustmentEditable(delegator, orderId);
        	List<GenericValue> orderAdjustmentDelete = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(conds), null, null, null, false);
        	for (Map<String, Object> itemMap : orderAdjData) {
        		String orderAdjustmentId = (String) itemMap.get("orderAdjustmentId");
        		BigDecimal amount = (BigDecimal) itemMap.get("amount");
        		String description = null;
        		if (!itemMap.get("description").equals("null")){
        			description = (String) itemMap.get("description");
        		}
        		List<GenericValue> orderAdj = EntityUtil.filterByCondition(orderAdjustmentDelete, EntityCondition.makeCondition("orderAdjustmentId", orderAdjustmentId));
        		if (UtilValidate.isNotEmpty(orderAdj)) {
        			// update order adjustment
        			for (GenericValue itemAdj : orderAdj) {
        				orderAdjustmentDelete.remove(orderAdj);
        				
        				Map<String, Object> updateCtx = ServiceUtil.setServiceFields(dispatcher, "updateOrderAdjustment", itemAdj, userLogin, null, locale);
        				updateCtx.put("amount", amount);
        				updateCtx.put("description", description);
        				updateCtx.put("comments", comments);
        				Map<String, Object> updateResult = dispatcher.runSync("updateOrderAdjustment", updateCtx);
        				if (ServiceUtil.isError(updateResult)) {
        					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(updateResult));
        				}
        			}
        		} else {
        			// create new order adjustment
        			String orderAdjustmentTypeId = (String) itemMap.get("orderAdjustmentTypeId");
        			Map<String, Object> createCtx = UtilMisc.toMap("orderId", orderId, "comments", comments, 
        					"orderAdjustmentTypeId", orderAdjustmentTypeId, "description", description, "amount", amount, "shipGroupSeqId", shipGroupSeqId, 
        					"userLogin", userLogin, "locale", locale);
        			Map<String, Object> createResult = dispatcher.runSync("createOrderAdjustment", createCtx);
        			if (ServiceUtil.isError(createResult)) {
    					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResult));
    				}
        		}
        	}
        	if (UtilValidate.isNotEmpty(orderAdjustmentDelete)) {
        		for (GenericValue item : orderAdjustmentDelete) {
        			try {
        				delegator.removeValue(item);
        			} catch (Exception e1) {
        				Debug.logWarning("Error when remove order adjustment id = " + item.getString("orderAdjustmentId"), module);
        			}
        		}
        	}
        } catch (Exception e) {
			String errMsg = "Fatal error calling updateOrderAdjustmentOlb service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
        
        return successResult;
    }
    
    private static List<EntityCondition> getCondsOrderAdjustmentEditable(Delegator delegator, String orderId){
    	List<String> orderAdjustmentTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "order.adjustment.changeable");
    	List<EntityCondition> conds = FastList.newInstance();
    	conds.add(EntityCondition.makeCondition("orderId", orderId));
    	conds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("orderItemSeqId", null), EntityOperator.OR, EntityCondition.makeCondition("orderItemSeqId", "_NA_")));
    	conds.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, orderAdjustmentTypeIds));
    	return conds;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListOrderAdjustmentOlb(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
    	EntityListIterator listIterator = null;
		try {
			String orderId = SalesUtil.getParameter(parameters, "orderId");
			if (UtilValidate.isNotEmpty(orderId)) {
				listAllConditions.addAll(getCondsOrderAdjustmentEditable(delegator, orderId));
				listIterator = delegator.find("OrderAdjustment", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListOrderAdjustmentOlb service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListOrderItemsPromoReturn(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listOrderItemsPromo = FastList.newInstance();
		List<Map<String, Object>> listOrderAdjustmentsPromo = FastList.newInstance();
		
		try {
			List<GenericValue> listOrderItems = (List<GenericValue>) context.get("listOrderItems");
			if (listOrderItems == null || listOrderItems.isEmpty()) {
				return successResult;
			}
			
			String orderId = null;
			GenericValue orderItemFirst = listOrderItems.get(0);
			if (orderItemFirst != null) orderId = orderItemFirst.getString("orderId");
			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			if (orderHeader == null) {
				return ServiceUtil.returnError("Khong tim thay don hang ban phu hop");
			}
			
			// get list product promotion rules from list order item
			List<GenericValue> listPromoRules = FastList.newInstance();
			Map<String, Object> promoRulesProdCtx = FastMap.newInstance();
			for (GenericValue orderItem : listOrderItems) {
				promoRulesProdCtx.clear();
				promoRulesProdCtx.put("productId", orderItem.getString("productId"));
				promoRulesProdCtx.put("productStoreId", orderHeader.getString("productStoreId"));
				promoRulesProdCtx.put("timePoint", orderHeader.getTimestamp("orderDate"));
				promoRulesProdCtx.put("userLogin", userLogin);
				Map<String, Object> promoRulesResult = dispatcher.runSync("getListPromosRuleByProduct", promoRulesProdCtx);
				if (ServiceUtil.isError(promoRulesResult)) {
					continue;
				}
				
				if (promoRulesResult.get("listPromoRules") != null) {
					listPromoRules.addAll((List<GenericValue>) promoRulesResult.get("listPromoRules"));
				}
			}
			if (listPromoRules.isEmpty()) {
				return successResult;
			}
			
			List<String> promoIds = EntityUtil.getFieldListFromEntityList(listPromoRules, "productPromoId", true);
			List<String> promoRuleIds = EntityUtil.getFieldListFromEntityList(listPromoRules, "productPromoRuleId", true);
			
			// get list order adjustments promotion
			List<GenericValue> orderAdjustmentsPromo = FastList.newInstance();
			List<GenericValue> orderAdjustmentsOrderItemPromo = FastList.newInstance();
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("orderId", orderId));
			listConds.add(EntityCondition.makeCondition("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
			listConds.add(EntityCondition.makeCondition("productPromoId", EntityOperator.IN, promoIds));
			List<GenericValue> orderAdjsPromo = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(listConds), null, null, null, false);
			if (UtilValidate.isNotEmpty(orderAdjsPromo)) {
				for (GenericValue orderAdj : orderAdjsPromo) {
					if (orderAdj.get("productPromoRuleId") != null && promoRuleIds.contains(orderAdj.getString("productPromoRuleId"))) {
						if ("_NA_".equals(orderAdj.getString("orderItemSeqId"))) {
							orderAdjustmentsPromo.add(orderAdj);
						} else if (orderAdj.get("orderItemSeqId") != null) {
							//orderAdjustmentsPromo.add(orderAdj);
							//orderItemSeqIdsPromo.add(orderAdj.getString("orderItemSeqId"));
							orderAdjustmentsOrderItemPromo.add(orderAdj);
						}
					}
					
				}
			}
			
			if (UtilValidate.isNotEmpty(orderAdjustmentsOrderItemPromo)) {
				Set<String> orderItemSeqIdsAdded = FastSet.newInstance();
				for (GenericValue orderAdj : orderAdjustmentsOrderItemPromo) {
					listConds.clear();
					listConds.add(EntityCondition.makeCondition("orderId", orderId));
					listConds.add(EntityCondition.makeCondition("isPromo", "Y"));
					//listConds.add(EntityCondition.makeCondition("statusId", "ITEM_COMPLETED"));
					listConds.add(EntityCondition.makeCondition("orderItemSeqId", orderAdj.get("orderItemSeqId")));
					GenericValue orderItemPromo = EntityUtil.getFirst(delegator.findList("OrderItem", EntityCondition.makeCondition(listConds), null, null, null, false));
					if (orderItemPromo != null) {
						if (!orderItemSeqIdsAdded.contains(orderItemPromo.get("orderItemSeqId"))
								&& "ITEM_COMPLETED".equals(orderItemPromo.getString("statusId"))) {
							GenericValue product = orderItemPromo.getRelatedOne("Product", false);
							Map<String, Object> orderItem = FastMap.newInstance();
							orderItem.put("productId", product.get("productId"));
							orderItem.put("productCode", product.get("productCode"));
							orderItem.put("productName", product.get("productName"));
							orderItem.put("quantity", orderItemPromo.get("alternativeQuantity"));
							orderItem.put("quantityUomId", orderItemPromo.get("quantityUomId"));
							orderItem.put("orderAdjustmentId", orderAdj.get("orderAdjustmentId"));
							listOrderItemsPromo.add(orderItem);
							
							orderItemSeqIdsAdded.add(orderItemPromo.getString("orderItemSeqId"));
						}
					} else {
						orderAdjustmentsPromo.add(orderAdj);
					}
				}
			}
			
			if (orderAdjustmentsPromo.size() > 0) {
				for (GenericValue orderAdj : orderAdjustmentsPromo) {
					Map<String, Object> orderItem = FastMap.newInstance();
					orderItem.put("orderAdjustmentId", orderAdj.get("orderAdjustmentId"));
					orderItem.put("amount", orderAdj.get("amount"));
					orderItem.put("description", orderAdj.get("description"));
					listOrderAdjustmentsPromo.add(orderItem);
				}
			}
	    } catch (Exception e) {
			String errMsg = "Fatal error calling getListOrderItemsPromoReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listOrderItemsPromo", listOrderItemsPromo);
		successResult.put("listOrderAdjustmentsPromo", listOrderAdjustmentsPromo);
		return successResult;
	}
	
	/*public static Map<String, Object> testListOrderItemsPromoReturn(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			String orderId = (String) context.get("orderId");
			String orderItemSeqId = (String) context.get("orderItemSeqId");
			
			GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
			if (orderItem == null) {
				return successResult;
			}
			
			Map<String, Object> promoRulesProdCtx = FastMap.newInstance();
			promoRulesProdCtx.put("listOrderItems", UtilMisc.toList(orderItem));
			promoRulesProdCtx.put("userLogin", userLogin);
			successResult = dispatcher.runSync("getListOrderItemsPromoReturn", promoRulesProdCtx);
		} catch (Exception e) {
			String errMsg = "Fatal error calling testListOrderItemsPromoReturn service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		return successResult;
	}*/
	
	// update Sales order when purchase order (born from dropShip) update
    public static Map<String, Object> checkAndUpdateSalesOrderWhenPurchOrderModified(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Locale locale = (Locale) context.get("locale");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	
    	String purchOrderId = (String) context.get("orderId");
    	String salesOrderId = null;
    	try {
    		GenericValue purchOrderGV = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", purchOrderId), false);
    		// update only when order is purchase order
    		if (!"PURCHASE_ORDER".equals(purchOrderGV.getString("orderTypeId"))) {
    			Debug.logWarning("No Sales order is updated, because this order is not purchase order", module);
    			successResult.put("orderId", purchOrderId);
    	    	successResult.put("salesOrderId", salesOrderId);
    	    	return successResult;
    		}
    		
    		// find sales order from purchase order
    		List<GenericValue> listOrderItemAssoc = delegator.findByAnd("OrderItemAssoc", UtilMisc.toMap("toOrderId", purchOrderId, "orderItemAssocTypeId", "DROP_SHIPMENT"), null, false);
    		GenericValue orderItemAssoc = EntityUtil.getFirst(listOrderItemAssoc);
    		if (UtilValidate.isEmpty(orderItemAssoc)) {
    			successResult.put("orderId", purchOrderId);
    	    	successResult.put("salesOrderId", salesOrderId);
    			return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSNotFindSalesOrderAny", locale));
    		}
    		
    		salesOrderId = orderItemAssoc.getString("orderId");
    		String salesShipGroupSeqId = orderItemAssoc.getString("shipGroupSeqId");
    		String purchShipGroupSeqId = orderItemAssoc.getString("toShipGroupSeqId");
    		
    		GenericValue salesOrderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", salesOrderId), false);
    		
    		// get list purchase order item
    		// EntityCondition condStatusOrderItem = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED");
    		List<GenericValue> purchOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", purchOrderId), null, null, null, false);
    		List<GenericValue> salesOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", salesOrderId), null, null, null, false);
    		
			GenericValue salesOrderItem = EntityUtil.getFirst(
    				EntityUtil.filterByCondition(salesOrderItems, 
    						EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("orderItemSeqId", orderItemAssoc.get("orderItemSeqId")), 
    										EntityCondition.makeCondition("prodCatalogId", EntityOperator.NOT_EQUAL, null), 
    										EntityCondition.makeCondition("isPromo", "N")))
    				));
			
			if (UtilValidate.isEmpty(salesOrderItem)) {
				Debug.logWarning("No Sales order is updated, because sales order item correspond is null", module);
    			successResult.put("orderId", purchOrderId);
    	    	successResult.put("salesOrderId", salesOrderId);
    	    	return successResult;
    		}
			
    		String prodCatalogId = salesOrderItem.getString("prodCatalogId");
    		Timestamp estimatedDeliveryDate = salesOrderItem.getTimestamp("estimatedDeliveryDate");
    		Timestamp shipBeforeDate = salesOrderItem.getTimestamp("shipBeforeDate");
    		Timestamp shipAfterDate = salesOrderItem.getTimestamp("shipAfterDate");
    		
    		// process
    		for (GenericValue purchOrderItem : purchOrderItems) {
    			BigDecimal purchQuantity = purchOrderItem.getBigDecimal("quantity");
    			BigDecimal purchQuantityNew = purchQuantity;
    			
    			// find in order item assoc
    			GenericValue salesOrderItemAssoc = EntityUtil.getFirst(EntityUtil.filterByAnd(listOrderItemAssoc, UtilMisc.toMap("toOrderItemSeqId", purchOrderItem.getString("orderItemSeqId"))));
    			
    			/*if (salesOrderItemAssoc != null) {
    				//TODOCHANGE add sample product by item in requirement assoc
                	//List<GenericValue> reqAssocItems = delegator.findByAnd("OrderRequirementAssoc", UtilMisc.toMap("orderId", salesOrderItemAssoc.get("orderId"), "orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId")), null, false);
                	List<GenericValue> reqAssocItems = delegator.findByAnd("OrderReqAssocReqSampleAppl", UtilMisc.toMap("orderId", salesOrderItemAssoc.get("orderId"), "orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId")), null, false);
                	if (UtilValidate.isNotEmpty(reqAssocItems)) {
                		for (GenericValue reqAssocItem : reqAssocItems) {
                			BigDecimal quantityItemNew = reqAssocItem.getBigDecimal("quantitySample");
                			if (quantityItemNew != null) purchQuantityNew = purchQuantityNew.subtract(quantityItemNew);
                		}
                	}
                	// end new
    			}*/
    			
    			if (!"ITEM_CANCELLED".equals(purchOrderItem.getString("statusId"))) {
    				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", purchOrderItem.get("productId")), false);
    				if (product != null) {
    					Map<String, Object> productItemCtx = FastMap.newInstance();
        				productItemCtx.put("shipGroupSeqId", salesShipGroupSeqId);
        				productItemCtx.put("productId", product.get("productId"));
        				productItemCtx.put("prodCatalogId", prodCatalogId);
        				//productItemCtx.put("quantity", purchOrderItem.get("quantity"));
        				productItemCtx.put("quantity", purchQuantityNew);
        				productItemCtx.put("amount", null);
        				productItemCtx.put("overridePrice", null);
        				productItemCtx.put("reasonEnumId", null);
        				productItemCtx.put("orderItemTypeId", "PRODUCT_ORDER_ITEM");
        				productItemCtx.put("changeComments", null);
        				productItemCtx.put("itemDesiredDeliveryDate", estimatedDeliveryDate);
        				//productItemCtx.put("itemAttributesMap", null);
        				productItemCtx.put("calcTax", true);
        				productItemCtx.put("quantityUomId", product.get("quantityUomId"));
        				productItemCtx.put("expireDate", null);
        				productItemCtx.put("shipBeforeDate", shipBeforeDate);
        				productItemCtx.put("shipAfterDate", shipAfterDate);
        				productItemCtx.put("orderId", salesOrderId);
        				productItemCtx.put("userLogin", userLogin);
        				productItemCtx.put("locale", locale);
        				
        				if (salesOrderItemAssoc != null) {
            				// CONDITION: SO not have promotion product any
            				// have a relationship between SO item and PO item
            				// update SO item
            				
        					productItemCtx.put("orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId"));
        					GenericValue salesOrderItemGV = EntityUtil.getFirst(EntityUtil.filterByAnd(salesOrderItems, UtilMisc.toMap("orderId", salesOrderId, "orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId"))));
        					if (salesOrderItemGV != null && !"ITEM_CANCELLED".equals(salesOrderItemGV.getString("statusId"))) {
        						BigDecimal salesQuantity = salesOrderItemGV.getBigDecimal("quantity");
        						if (purchQuantity != null && salesQuantity != null && purchQuantity.compareTo(salesQuantity) != 0) {
        							Map<String, Object> contextMapUpdate = OrderWorker.processDataEditOrderPreProcess(delegator, dispatcher, locale, userLogin, salesOrderHeader, salesOrderId, UtilMisc.toList(productItemCtx));
        				    		Map<String, Object> resultValueUpdate = dispatcher.runSync("updateOrderItemsNoActions", contextMapUpdate);
        							if (ServiceUtil.isError(resultValueUpdate)) {
        								return ServiceUtil.returnError((String) resultValueUpdate.get(ModelService.ERROR_MESSAGE));
        							}
        						}
        						if (salesOrderItemAssoc.getBigDecimal("quantity") != null && purchQuantity.compareTo(salesOrderItemAssoc.getBigDecimal("quantity")) != 0) {
        							salesOrderItemAssoc.put("quantity", purchQuantity);
                					delegator.store(salesOrderItemAssoc);
        						}
        					}
            			} else {
            				// not have relationship any
            				// add SO item
            				
            				List<String> salesOrderItemSeqIdExisted = EntityUtil.getFieldListFromEntityList(salesOrderItems, "orderItemSeqId", true);
            				
            				// get info of product
            				if (product != null) {
                				Map<String, Object> resultValue = dispatcher.runSync("appendOrderItem", productItemCtx);
        						if (ServiceUtil.isError(resultValue)) {
        							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
        						}
            				}
            				
            				List<GenericValue> salesOrderItemsNew = delegator.findList("OrderItem", EntityCondition.makeCondition(
            						EntityCondition.makeCondition("orderId", salesOrderId), EntityOperator.AND, 
            						EntityCondition.makeCondition("orderItemSeqId", EntityOperator.NOT_IN, salesOrderItemSeqIdExisted)), null, null, null, false);
            				if (UtilValidate.isNotEmpty(salesOrderItemsNew)) {
            					// have SO item new
            					// add a relationship between SO item and PO item
                				for (GenericValue salesOrderItemNew : salesOrderItemsNew) {
                					GenericValue orderItemAssocNew = delegator.makeValue("OrderItemAssoc");
            						orderItemAssocNew.put("orderId", salesOrderId);
            						orderItemAssocNew.put("orderItemSeqId", salesOrderItemNew.get("orderItemSeqId"));
            						orderItemAssocNew.put("shipGroupSeqId", salesShipGroupSeqId);
            						orderItemAssocNew.put("toOrderId", purchOrderId);
            						orderItemAssocNew.put("toOrderItemSeqId", purchOrderItem.get("orderItemSeqId"));
            						orderItemAssocNew.put("toShipGroupSeqId", purchShipGroupSeqId);
            						orderItemAssocNew.put("orderItemAssocTypeId", "DROP_SHIPMENT");
            						orderItemAssocNew.put("quantity", purchQuantity);
                					delegator.create(orderItemAssocNew);
                				}
            				}
            			}
    				}
    			} else {
    				if (salesOrderItemAssoc != null) {
    					GenericValue salesOrderItemGV = EntityUtil.getFirst(EntityUtil.filterByAnd(salesOrderItems, UtilMisc.toMap("orderId", salesOrderId, "orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId"))));
    					if (salesOrderItemGV != null && !"ITEM_CANCELLED".equals(salesOrderItemGV.getString("statusId"))) {
    						// cancel SO item correspond
        					Map<String, Object> productItemCtx = UtilMisc.<String, Object>toMap(
        							"orderId", salesOrderId, "orderItemSeqId", salesOrderItemAssoc.get("orderItemSeqId"),
        							"shipGroupSeqId", salesShipGroupSeqId, "cancelQuantity", purchOrderItem.get("quantity"),
        							"userLogin", userLogin, "locale", locale);
        					// "itemReasonMap", null, "itemCommentMap", null
        					Map<String, Object> resultValue = dispatcher.runSync("cancelOrderItem", productItemCtx);
    						if (ServiceUtil.isError(resultValue)) {
    							return ServiceUtil.returnError((String) resultValue.get(ModelService.ERROR_MESSAGE));
    						}
    					}
    				}
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling checkAndUpdateSalesOrderWhenPurchOrderModified service: " + e.toString();
			Debug.logError(e, errMsg, module);
    	}
    	
    	successResult.put("orderId", purchOrderId);
    	successResult.put("salesOrderId", salesOrderId);
    	return successResult;
    }
    
    public static Map<String, Object> sendNotifySOUpdateAfterPOModified(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	
    	String orderId = (String) context.get("salesOrderId");
    	try {
    		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
    		if (orderHeader == null) return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSNotFoundOrderHasOrderIdIs", UtilMisc.toList(orderId), locale));
    		
    		NotificationWorker.sendNotifySOUpdateAfterPOModified(delegator, dispatcher, locale, orderId, userLogin);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling sendNotifyOrderAfterUpdate service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	
    	return successResult;
    }
}
