package com.olbius.salesmtl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.CrabEntity;
import com.olbius.administration.util.UniqueUtil;
import com.olbius.basehr.employee.helper.EmployeeHelper;
import com.olbius.basehr.employee.services.EmployeeLeaveServices;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsPartyUtil;
import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.util.CRMUtils;
import com.olbius.basesales.util.NotificationUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.crm.CallcenterServices;
import com.olbius.salesmtl.util.RouteUtils;
import com.olbius.salesmtl.util.SupUtil;
import com.olbius.security.util.SecurityUtil;

public class DistributorServices {
	public static final String module = DistributorServices.class.getName();
	public static final String resource = "widgetUiLabels";
	public static final String resourceError = "widgetErrorUiLabels";
	public static final String resource_error = "OrderUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerByDist(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			String partyId = null;
			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId")
					&& UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
			}
			if (partyId != null) {
				listIterator = SalesmtlUtil.getListCustomerByDis(delegator, partyId, listAllConditions, listSortFields,
						opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}*/

	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerBySupp(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			String partyId = null;
			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId")
					&& UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
			}
			if (partyId != null) {
				listIterator = SalesmtlUtil.getListCustomerBySup(delegator, partyId, listAllConditions, listSortFields,
						opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (GenericValue x : listIterator) {
			x.set("partyIdFrom", PartyHelper.getPartyName(delegator, x.getString("partyIdFrom"), true));
		}
		result.put("listIterator", listIterator);
		return result;
	}*/

	@SuppressWarnings("unchecked")
	public static Map<String, Object> ownerStoreInformation(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			String partyId = null;
			if (parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId"))) {
				partyId = parameters.get("partyId")[0];
			}
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("partyIdTo", partyId));
			listConds.add(EntityCondition.makeCondition("roleTypeIdFrom", "OWNER"));
			listConds.add(EntityCondition.makeCondition("roleTypeIdTo", "CUSTOMER_GT"));
			listConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
			List<GenericValue> listStoreName = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false);
			if (UtilValidate.isNotEmpty(listStoreName)) {
				GenericValue StoreName = EntityUtil.getFirst(listStoreName);
				String OwnerStoreId = StoreName.getString("partyIdFrom");
				List<EntityCondition> listConds1 = FastList.newInstance();
				listConds1.add(EntityCondition.makeCondition("partyId", OwnerStoreId));
				listConds1.add(EntityCondition.makeCondition("partyIdTo", partyId));
				listConds1.add(EntityCondition.makeCondition("partyRelationshipTypeId", "OWNER"));
				List<GenericValue> listIterator1 = delegator.findList("PartyFromAndPartyNameDetail",
						EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, listSortFields, opts,
						false);
				GenericValue Iterator1 = EntityUtil.getFirst(listIterator1);
				GenericValue Iterator2 = delegator.findOne("Person", UtilMisc.toMap("partyId", OwnerStoreId), false);
				Map<String, Object> tmpMap = FastMap.newInstance();
				tmpMap.put("partyId", OwnerStoreId);
				tmpMap.put("fullName", Iterator1.getString("fullName"));
				tmpMap.put("birthDate", Iterator2.getDate("birthDate"));
				tmpMap.put("fromDate", Iterator1.getTimestamp("fromDate"));
				listIterator.add(tmpMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCDARInvoiceByUserLoginDis(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		List<String> statusIds = new ArrayList<String>();
		statusIds.add("INVOICE_IN_PROCESS");
		statusIds.add("INVOICE_READY");
		if (UtilValidate.isNotEmpty(partyId)) {
			try {
				EntityCondition tmpConditon = EntityCondition.makeCondition(
						UtilMisc.<EntityCondition> toList(
								EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,
										"PURCHASE_INVOICE"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "company"),
						EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds),
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId)),
						EntityJoinOperator.AND);
				listAllConditions.add(tmpConditon);
				tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCDAPInvoiceByUserLoginDis(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		List<String> statusIds = new ArrayList<String>();
		statusIds.add("INVOICE_IN_PROCESS");
		statusIds.add("INVOICE_READY");
		if (UtilValidate.isNotEmpty(partyId)) {
			try {
				EntityCondition tmpConditon = EntityCondition
						.makeCondition(
								UtilMisc.<EntityCondition> toList(
										EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,
												"SALES_INVOICE"),
								EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "company"),
								EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIds),
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)),
						EntityJoinOperator.AND);
				listAllConditions.add(tmpConditon);
				tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("InvoiceAndType", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductByCategoryCatalogLM(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		// String catalogId = (String) parameters.get("catalogId")[0];
		String productStoreId = "";
		if (parameters.containsKey("productStoreId")) {
			productStoreId = (String) parameters.get("productStoreId")[0];
		}
		HttpServletRequest request = (HttpServletRequest) context.get("request");
		try {
			// if (UtilValidate.isNotEmpty(catalogId)) {
			Set<String> productIds = FastSet.newInstance();
			// GenericValue catalogObj = delegator.findOne("ProdCatalog",
			// UtilMisc.toMap("prodCatalogId", catalogId), false);
			// if (catalogObj != null) {
			// List<EntityCondition> conditionList = new
			// ArrayList<EntityCondition>();
			// List<EntityCondition> orConditionList = new
			// ArrayList<EntityCondition>();
			// List<EntityCondition> mainConditionList = new
			// ArrayList<EntityCondition>();
			// do not include configurable products
			// conditionList.add(EntityCondition.makeCondition("productTypeId",
			// EntityOperator.NOT_EQUAL, "AGGREGATED"));
			// conditionList.add(EntityCondition.makeCondition("productTypeId",
			// EntityOperator.NOT_EQUAL, "AGGREGATED_SERVICE"));
			// conditionList.add(EntityCondition.makeCondition("productTypeId",
			// EntityOperator.NOT_EQUAL, "AGGR_DIGSERV"));
			// conditionList.add(EntityCondition.makeCondition("prodCatalogId",
			// EntityOperator.EQUALS, catalogId));
			// EntityCondition conditions =
			// EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			// // no virtual products: note that isVirtual could be null,
			// // we consider those products to be non-virtual and hence addable
			// to the order in bulk
			// orConditionList.add(EntityCondition.makeCondition("isVirtual",
			// EntityOperator.EQUALS, "N"));
			// orConditionList.add(EntityCondition.makeCondition("isVirtual",
			// EntityOperator.EQUALS, null));
			// EntityCondition orConditions =
			// EntityCondition.makeCondition(orConditionList,
			// EntityOperator.OR);
			// mainConditionList.add(orConditions);
			// mainConditionList.add(conditions);
			// listAllConditions.addAll(mainConditionList);
			listAllConditions.add(EntityCondition.makeCondition("isVirtual", "N"));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("sequenceNumCategory");
				listSortFields.add("sequenceNumRollup");
				listSortFields.add("sequenceNumProduct");
			}
			opts.setDistinct(true);
			List<GenericValue> listProdGeneric = delegator.findList("Product", tmpConditon, null, null, opts, false);
			if (UtilValidate.isNotEmpty(listProdGeneric)) {
				boolean hasCart = false;
				GenericValue orderItemSelected = null;
				List<GenericValue> orderItems = new ArrayList<GenericValue>();
				if (request != null) {
					HttpSession session = request.getSession();
					ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
					if (cart != null) {
						orderItems = cart.makeOrderItems();
						hasCart = true;
					}
				}
				for (GenericValue itemProd : listProdGeneric) {
					String iProductId = itemProd.getString("productId");
					if (UtilValidate.isEmpty(iProductId) || productIds.contains(iProductId)) {
						continue;
					}
					if (hasCart) {
						for (GenericValue orderItem : orderItems) {
							if (orderItem.getString("productId") != null
									&& (UtilValidate.isEmpty(orderItem.getString("isPromo"))
											|| "N".equals(orderItem.getString("isPromo")))
									&& orderItem.getString("productId").equals(itemProd.getString("productId"))) {
								orderItemSelected = orderItem;
							}
						}
					}
					Map<String, Object> row = new HashMap<String, Object>();
					productIds.add(iProductId);
					row.put("productId", itemProd.get("productId"));
					row.put("productName", itemProd.get("productName"));
					row.put("productPackingUomId", itemProd.get("productPackingUomId"));
					row.put("quantityUomId", itemProd.getString("productPackingUomId"));

					// column: packingUomId
					EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId",
							itemProd.get("productId"), "uomToId", itemProd.get("quantityUomId")));
					EntityFindOptions optsItem = new EntityFindOptions();
					optsItem.setDistinct(true);
					List<GenericValue> listConfigPacking = FastList.newInstance();
					listConfigPacking
							.addAll(delegator.findList("ConfigPackingAndUom", condsItem, null, null, optsItem, false));
					List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
					for (GenericValue conPackItem : listConfigPacking) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
						packingUomIdMap.put("uomId", conPackItem.getString("uomFromId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					GenericValue quantityUom = delegator.findOne("Uom",
							UtilMisc.toMap("uomId", itemProd.get("quantityUomId")), false);
					if (quantityUom != null) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", quantityUom.getString("description"));
						packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					row.put("packingUomId", listQuantityUomIdByProduct);

					// column: expireDate
					List<Map<String, Object>> listExpireDateByProduct = new ArrayList<Map<String, Object>>();
					List<EntityCondition> exConds = FastList.newInstance();
					exConds.add(EntityCondition.makeCondition("productStoreId", productStoreId));
					exConds.add(EntityCondition.makeCondition("productId", itemProd.getString("productId")));
					boolean isAdded = false;
					/*
					 * EntityListIterator iterQuantityInventorySum =
					 * delegator.find("ProductStoreFacilityInventorySumAtpqoh",
					 * EntityCondition.makeCondition(exConds,
					 * EntityOperator.AND),
					 * EntityCondition.makeCondition("quantityOnHandTotal",
					 * EntityOperator.GREATER_THAN, BigDecimal.ZERO), null,
					 * UtilMisc.toList("expireDate"), null); if
					 * (iterQuantityInventorySum != null) { List<GenericValue>
					 * listQuantityInventorySum =
					 * iterQuantityInventorySum.getCompleteList(); if
					 * (listQuantityInventorySum != null) {
					 * listQuantityInventorySum =
					 * EntityUtil.filterByDate(listQuantityInventorySum); for
					 * (int i = 0; i < listQuantityInventorySum.size(); i++) {
					 * GenericValue quantityInventoryItem =
					 * listQuantityInventorySum.get(i); String tmpExpireDate =
					 * quantityInventoryItem.getString("expireDate"); if
					 * (hasCart && orderItemSelected != null &&
					 * orderItemSelected.getString("expireDate").equals(
					 * tmpExpireDate)) { row.put("expireDate", tmpExpireDate);
					 * row.put("qohTotal",
					 * quantityInventoryItem.get("quantityOnHandTotal"));
					 * row.put("atpTotal",
					 * quantityInventoryItem.get("availableToPromiseTotal")); }
					 * else { if (i == 0) { row.put("expireDate",
					 * tmpExpireDate); row.put("qohTotal",
					 * quantityInventoryItem.get("quantityOnHandTotal"));
					 * row.put("atpTotal",
					 * quantityInventoryItem.get("availableToPromiseTotal"));
					 * isAdded = true; } } Map<String, Object> mapItem =
					 * FastMap.newInstance(); mapItem.put("expireDate",
					 * tmpExpireDate); mapItem.put("qohTotal",
					 * quantityInventoryItem.get("quantityOnHandTotal"));
					 * mapItem.put("atpTotal",
					 * quantityInventoryItem.get("availableToPromiseTotal"));
					 * listExpireDateByProduct.add(mapItem); } }
					 * iterQuantityInventorySum.close(); }
					 */
					row.put("expireDateList", listExpireDateByProduct);
					if (!isAdded && orderItemSelected == null) {
						if (!isAdded)
							row.put("expireDate", "");
						row.put("qohTotal", "");
						row.put("atpTotal", "");
					}
					listIterator.add(row);
				}
			}
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerBySalesman(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		opts.setDistinct(true);
		try {
			String partyId = null;
			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId")
					&& UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
			}
			if (partyId != null) {
				listIterator = SalesmtlUtil.getIteratorCustomerBySalesman(delegator, partyId, listAllConditions,
						listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}*/

	public static Map<String, Object> listOrderByDis(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = null;
			if (UtilValidate.isNotEmpty(userLogin)) {
				partyId = userLogin.getString("partyId");
				List<GenericValue> listGenericOder = delegator.findByAnd("OrderRole",
						UtilMisc.toMap("partyId", partyId, "roleTypeId", "BILL_FROM_VENDOR"), null, false);
				for (GenericValue g : listGenericOder) {
					Map<String, Object> tmp = FastMap.newInstance();
					List<EntityCondition> listConds = FastList.newInstance();
					listConds.add(EntityCondition.makeCondition("orderId", g.getString("orderId")));
					listConds.add(EntityCondition.makeCondition("roleTypeId", "PLACING_CUSTOMER"));
					List<GenericValue> FindCustomerIds = delegator.findList("OrderRole",
							EntityCondition.makeCondition(listConds, EntityOperator.AND), null, null, opts, false);
					GenericValue FindCustomerId = EntityUtil.getFirst(FindCustomerIds);
					String CustomerId = FindCustomerId.getString("partyId");
					GenericValue Order = delegator.findOne("OrderHeader",
							UtilMisc.toMap("orderId", g.getString("orderId")), false);
					tmp.put("orderDate", Order.getTimestamp("orderDate"));
					tmp.put("orderId", Order.getString("orderId"));
					tmp.put("orderName", Order.getString("orderName"));
					tmp.put("orderTypeId", Order.getString("orderTypeId"));
					tmp.put("customerId", CustomerId);
					tmp.put("productStoreId", Order.getString("productStoreId"));
					tmp.put("grandTotal", Order.getBigDecimal("grandTotal"));
					tmp.put("statusId", Order.getString("statusId"));
					tmp.put("currencyUom", Order.getString("currencyUom"));
					listIterator.add(tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CRMUtils.listCompact(listIterator, context);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listReturnOrderToCompany(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(userLogin)) {
				String companyId = EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY",
						"company", delegator);
				listAllConditions.add(EntityCondition.makeCondition("toPartyId", companyId));
				if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
					listAllConditions.add(EntityCondition.makeCondition("fromPartyId", userLogin.getString("partyId")));
				} else if (SalesPartyUtil.isLogSpecialist(delegator, userLogin.getString("partyId"))) {
					listAllConditions.add(EntityCondition.makeCondition("statusId", "RETURN_ACCEPTED"));
				} else {
					listAllConditions.add(EntityCondition.makeCondition("createdBy", userLogin.get("userLoginId")));
				}
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("ReturnHeader", tmpConditon, null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listReturnOrderFromCustomer(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(userLogin.get("partyId"))) {
				listAllConditions.add(EntityCondition.makeCondition("toPartyId", userLogin.get("partyId")));
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("ReturnHeader", tmpConditon, null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	// TODOCHANGE delete
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createReturnRequirement(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String customerId = (String) context.get("customerId");
		String distributorId = (String) context.get("distributorId");
		String contactMechId = (String) context.get("contactMechId");
		String description = (String) context.get("description");
		Timestamp requirementStartDate = (Timestamp) context.get("requirementStartDate");
		Timestamp requiredByDate = (Timestamp) context.get("requiredByDate");
		String requirementTypeId = (String) context.get("requirementTypeId");
		String reason = (String) context.get("reason");

		boolean beganTx = TransactionUtil.begin(7200);
		String currencyUomId = (String) context.get("currencyUomId");
		if (UtilValidate.isNotEmpty(currencyUomId)) {
			currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default",
					"USD", delegator);
		}
		String requirementId = "";
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

			requirementId = delegator.getNextSeqId("Requirement");
			Map<String, Object> contextMap = UtilMisc.<String, Object> toMap("requirementId", requirementId,
					"requirementTypeId", requirementTypeId, "requirementStartDate", requirementStartDate,
					"requiredByDate", requiredByDate, "currencyUomId", currencyUomId, "description", description,
					"reason", reason, "contactMechId", contactMechId, "statusId", "RETURREQ_CREATED", "createdDate",
					nowTimestamp, "createdByUserLogin", userLogin.get("userLoginId"));

			GenericValue requirement = delegator.makeValue("Requirement", contextMap);
			delegator.create(requirement);

			Map<String, Object> contextMap2 = UtilMisc.<String, Object> toMap("requirementId",
					requirement.get("requirementId"), "statusId", requirement.get("statusId"), "statusDate",
					nowTimestamp, "statusUserLogin", userLogin.get("userLoginId"));
			delegator.create("RequirementStatus", contextMap2);

			// create roles in requirement
			// SALESMAN_GT
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			Map<String, Object> contextRoleMap = UtilMisc.<String, Object> toMap("requirementId", requirementId,
					"partyId", distributorId, "roleTypeId", "OWNER", "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner);
			contextRoleMap = UtilMisc.<String, Object> toMap("requirementId", requirementId, "partyId", customerId,
					"roleTypeId", "CUSTOMER_GT", "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner2 = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner2);
			contextRoleMap = UtilMisc.<String, Object> toMap("requirementId", requirementId, "partyId", distributorId,
					"roleTypeId", "DISTRIBUTOR", "fromDate", nowTimestamp);
			GenericValue requirementRoleOwner3 = delegator.makeValidValue("RequirementRole", contextRoleMap);
			toBeStored.add(requirementRoleOwner3);

			delegator.storeAll(toBeStored);

			// create requirement item
			List<Map<String, String>> listProducts = (List<Map<String, String>>) context.get("listProducts");
			for (Map<String, String> item : listProducts) {
				String productId = item.get("productId");
				String quantity = item.get("quantity");
				String quantityUomId = item.get("quantityUomId");
				String expDateTmp = item.get("expireDate");
				Timestamp expireDate = null;
				if (UtilValidate.isNotEmpty(expDateTmp) && !"null".equals(expDateTmp)) {
					expireDate = new Timestamp(Long.parseLong(expDateTmp, 10));
				}
				try {
					Map<String, Object> contextItemMap = UtilMisc.<String, Object> toMap("requirementId", requirementId,
							"requirementTypeId", requirementTypeId, "productId", productId, "quantity", quantity,
							"quantityUomId", quantityUomId, "currencyUomId", currencyUomId, "expireDate", expireDate,
							"statusId", "REQ_ITEM_CREATED", "userLogin", userLogin);
					dispatcher.runSync("addProductToRequirement", contextItemMap);
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		}
		TransactionUtil.commit(beganTx);
		result.put("requirementId", requirementId);
		return result;
	}

	// TODOCHANGE delete
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCustomerBySup(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");

		List<EntityCondition> exprList = new ArrayList<EntityCondition>();
		exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
		exprList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null));
		List<EntityCondition> exprList2 = new ArrayList<EntityCondition>();
		exprList2.add(EntityCondition.makeCondition(exprList, EntityOperator.AND));
		exprList2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
		EntityCondition condStatusPartyDisable = EntityCondition.makeCondition(exprList2, EntityOperator.OR);
		listAllConditions.add(condStatusPartyDisable);

		String partyId = null;
		opts.setDistinct(true);
		try {
			// get list distributor
			if (parameters.containsKey("supPartyId") && UtilValidate.isNotEmpty(parameters.get("supPartyId"))) {
				partyId = parameters.get("supPartyId")[0];
			}
			if (UtilValidate.isEmpty(partyId)) {
				partyId = userLogin.getString("partyId");
			}
			Map<String, String> mapCondition1 = new HashMap<String, String>();
			mapCondition1.put("partyIdFrom", partyId);
			mapCondition1.put("partyRelationshipTypeId", "MANAGER");
			mapCondition1.put("roleTypeId", "INTERNAL_ORGANIZATIO");
			mapCondition1.put("roleTypeIdTo", "INTERNAL_ORGANIZATIO");
			EntityCondition tmpConditon1 = EntityCondition.makeCondition(mapCondition1);
			List<EntityCondition> listConds1 = FastList.newInstance();
			listConds1.add(condStatusPartyDisable);
			listConds1.add(tmpConditon1);

			GenericValue listSupPosition = EntityUtil
					.getFirst(EntityUtil.filterByDate(delegator.findList("PartyRoleNameDetailPartyRelTo",
							EntityCondition.makeCondition(listConds1, EntityOperator.AND), null, null, opts, false)));
			if (listSupPosition != null) {
				Map<String, String> mapCondition2 = new HashMap<String, String>();
				mapCondition2.put("partyIdFrom", listSupPosition.getString("partyId"));
				mapCondition2.put("partyRelationshipTypeId", "DISTRIBUTION");
				mapCondition2.put("roleTypeId", "DISTRIBUTOR");
				mapCondition2.put("roleTypeIdTo", "DISTRIBUTOR");
				EntityCondition tmpConditon2 = EntityCondition.makeCondition(mapCondition2);
				List<EntityCondition> listConds2 = FastList.newInstance();
				listConds2.add(condStatusPartyDisable);
				listConds2.add(tmpConditon2);

				List<GenericValue> listDistributor = EntityUtil.filterByDate(delegator.findList(
						"PartyRoleNameDetailPartyRelTo", EntityCondition.makeCondition(listConds2, EntityOperator.AND),
						null, null, opts, false));
				if (UtilValidate.isNotEmpty(listDistributor)) {
					for (GenericValue distributor : listDistributor) {
						Map<String, String> mapCondition3 = new HashMap<String, String>();
						mapCondition3.put("partyIdFrom", distributor.getString("partyId"));
						mapCondition3.put("partyRelationshipTypeId", "CUSTOMER");
						mapCondition3.put("roleTypeId", "CUSTOMER_GT");
						mapCondition3.put("roleTypeIdTo", "CUSTOMER_GT");
						EntityCondition tmpConditon3 = EntityCondition.makeCondition(mapCondition3);
						List<EntityCondition> listConds3 = FastList.newInstance();
						listConds3.add(condStatusPartyDisable);
						listConds3.add(tmpConditon3);
						listConds3.addAll(listAllConditions);
						List<GenericValue> listCustomer = EntityUtil
								.filterByDate(delegator.findList("PartyRoleNameDetailPartyRelTo",
										EntityCondition.makeCondition(listConds3, EntityOperator.AND), null,
										listSortFields, opts, false));
						if (UtilValidate.isNotEmpty(listCustomer)) {
							listIterator.addAll(listCustomer);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> listDistributorByCustomer(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyId = null;
		String noUserlogin = null;
		opts.setDistinct(true);
		try {
			// get list distributor
			if (parameters.containsKey("customerId") && UtilValidate.isNotEmpty(parameters.get("customerId"))) {
				partyId = parameters.get("customerId")[0];
			}
			if (parameters.containsKey("noUserlogin") && UtilValidate.isNotEmpty(parameters.get("noUserlogin"))) {
				noUserlogin = parameters.get("noUserlogin")[0];
			}
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isEmpty(noUserlogin)) {
				partyId = userLogin.getString("partyId");
			}
			listIterator = SalesmtlUtil.getIteratorDistributorByCustomer(delegator, partyId, listAllConditions,
					listSortFields, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}*/

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyPostalAddresses(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyId = null;
		try {
			// get list distributor
			if (parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")))
				partyId = parameters.get("partyId")[0];
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
				listAllConditions.add(EntityCondition.makeCondition("contactMechTypeId", "POSTAL_ADDRESS"));
				List<EntityCondition> condNoContactMechPurpose = FastList.newInstance();
				condNoContactMechPurpose.addAll(listAllConditions);
				if (parameters.containsKey("contactMechPurposeTypeId")
						&& UtilValidate.isNotEmpty(parameters.get("contactMechPurposeTypeId"))) {
					String contactMechPurposeTypeId = parameters.get("contactMechPurposeTypeId")[0];
					listAllConditions
							.add(EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId));
				} else {
					List<EntityCondition> listCondOr = FastList.newInstance();
					listCondOr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "GENERAL_LOCATION"));
					listCondOr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "BILLING_LOCATION"));
					listCondOr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PAYMENT_LOCATION"));
					listCondOr.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
					listAllConditions.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
				}

				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
				List<GenericValue> listAddress = delegator.findList("PartyContactDetailByPurpose", tmpConditon, null,
						listSortFields, opts, false);
				listAddress = EntityUtil.filterByDate(listAddress, null, "purposeFromDate", "purposeThruDate", true);
				listAddress = EntityUtil.filterByDate(listAddress);
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (GenericValue address : listAddress) {
						Map<String, Object> newItem = FastMap.newInstance();
						newItem.put("contactMechId", address.get("contactMechId"));
						newItem.put("address1", address.get("address1"));
						newItem.put("address2", address.get("address2"));
						newItem.put("directions", address.get("directions"));
						newItem.put("city", address.get("city"));
						newItem.put("postalCode", address.get("postalCode"));
						newItem.put("stateProvinceGeoId", address.get("stateProvinceGeoId"));
						newItem.put("countyGeoId", address.get("countyGeoId"));
						newItem.put("countryGeoId", address.get("countryGeoId"));
						newItem.put("contactMechPurposeTypeId", address.get("contactMechPurposeTypeId"));
						listIterator.add(newItem);
					}
				} else {
					tmpConditon = EntityCondition.makeCondition(condNoContactMechPurpose);
					listAddress = delegator.findList("PartyAndContactMech", tmpConditon, null, listSortFields, opts,
							false);
					listAddress = EntityUtil.filterByDate(listAddress);
					for (GenericValue address : listAddress) {
						Map<String, Object> newItem = FastMap.newInstance();
						newItem.put("contactMechId", address.get("contactMechId"));
						newItem.put("address1", address.get("paAddress1"));
						newItem.put("address2", address.get("paAddress2"));
						newItem.put("directions", address.get("paDirections"));
						newItem.put("city", address.get("paCity"));
						newItem.put("postalCode", address.get("paPostalCode"));
						newItem.put("stateProvinceGeoId", address.get("paStateProvinceGeoId"));
						newItem.put("countyGeoId", address.get("paCountyGeoId"));
						newItem.put("countryGeoId", address.get("paCountryGeoId"));
						listIterator.add(newItem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSales(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			List<EntityCondition> listCondTypeOr = FastList.newInstance();
			listCondTypeOr.add(EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"));
			listAllConditions.add(EntityCondition.makeCondition(listCondTypeOr, EntityOperator.OR));
			EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
			List<GenericValue> listProduct = delegator.findList("Product", tmpConditon, null, listSortFields, opts,
					false);
			if (UtilValidate.isNotEmpty(listProduct)) {
				for (GenericValue itemProd : listProduct) {
					Map<String, Object> row = FastMap.newInstance();
					row.put("productId", itemProd.get("productId"));
					row.put("productName", itemProd.get("productName"));
					row.put("productPackingUomId", itemProd.get("productPackingUomId"));
					row.put("quantityUomId", itemProd.getString("productPackingUomId"));
					row.put("internalName", itemProd.getString("internalName"));
					// column: packingUomId
					EntityCondition condsItem = EntityCondition.makeCondition(UtilMisc.toMap("productId",
							itemProd.get("productId"), "uomToId", itemProd.get("quantityUomId")));
					EntityFindOptions optsItem = new EntityFindOptions();

					List<GenericValue> listConfigPacking = FastList.newInstance();
					listConfigPacking
							.addAll(delegator.findList("ConfigPackingAndUom", condsItem, null, null, optsItem, false));
					List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
					for (GenericValue conPackItem : listConfigPacking) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", conPackItem.getString("descriptionFrom"));
						packingUomIdMap.put("uomId", conPackItem.getString("uomFromId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					GenericValue quantityUom = delegator.findOne("Uom",
							UtilMisc.toMap("uomId", itemProd.get("quantityUomId")), false);
					if (quantityUom != null) {
						Map<String, Object> packingUomIdMap = FastMap.newInstance();
						packingUomIdMap.put("description", quantityUom.getString("description"));
						packingUomIdMap.put("uomId", quantityUom.getString("uomId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					row.put("packingUomId", listQuantityUomIdByProduct);
					listIterator.add(row);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			if (parameters.containsKey("requirementTypeId")
					&& UtilValidate.isNotEmpty(parameters.get("requirementTypeId"))) {
				String requirementTypeId = parameters.get("requirementTypeId")[0];
				if (UtilValidate.isNotEmpty(requirementTypeId)) {
					listAllConditions.add(EntityCondition.makeCondition("requirementTypeId", requirementTypeId));
				}
			}
			boolean isDistributor = false;
			if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
				isDistributor = true;
			}
			if (isDistributor) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", userLogin.get("partyId")));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "DISTRIBUTOR"));
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("RequirementAndRole", tmpConditon, null, null, listSortFields, opts);
			} else {
				EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions);
				listIterator = delegator.find("Requirement", tmpConditon, null, null, listSortFields, opts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductStoreAndDetail(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listIterator = new ArrayList<GenericValue>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setDistinct(true);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String partyId = null;
			if (UtilValidate.isNotEmpty(userLogin)) {
				partyId = userLogin.getString("partyId");
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
				listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "OWNER"));
				listIterator = delegator.findList("ProductStoreRoleDetail",
						EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields,
						opts, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}

	public static Map<String, Object> getOriginalQuantityOnHandTotalAjax(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String inventoryItemId = (String) context.get("inventoryItemId");
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		GenericValue inventoryItem = delegator.findOne("InventoryItem",
				UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
		if (UtilValidate.isNotEmpty(inventoryItem)) {
			quantityOnHandTotal = inventoryItem.getBigDecimal("quantityOnHandTotal");
		}
		result.put("quantityOnHandTotal", quantityOnHandTotal);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listInventoryItem(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String facilityId = null;
		if (UtilValidate.isNotEmpty(parameters.get("facilityId")) && parameters.get("facilityId").length > 0){
			facilityId = parameters.get("facilityId")[0];
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		
		String type = null;
		if (UtilValidate.isNotEmpty(parameters.get("type")) && parameters.get("type").length > 0){
			type = parameters.get("type")[0];
		}
		Boolean deposit = false;
		if (UtilValidate.isNotEmpty(type)) {
			if ("deposit".equals(type)){
				deposit = true;
			}
		}
		if (UtilValidate.isNotEmpty(facilityId)){
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		} else {
			if (UtilValidate.isNotEmpty(parameters.get("listFacilityIds")) && parameters.get("listFacilityIds").length > 0){
				List<String> listFacilityIds = new ArrayList<String>();
		    	if (parameters.get("listFacilityIds") != null && parameters.get("listFacilityIds").length > 0){
		    		JSONArray lists = JSONArray.fromObject(parameters.get("listFacilityIds")[0]);
					for (int i = 0; i < lists.size(); i++){
						JSONObject item = lists.getJSONObject(i);
						if (item.containsKey("facilityId")){
							if (item.getString("facilityId") != null && !"".equals(item.getString("facilityId"))){
								listFacilityIds.add( item.getString("facilityId"));
							}
						}
					}
		    	}
		    	EntityCondition Cond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
				listAllConditions.add(Cond);
			} else {
				List<GenericValue> listFacilities = new ArrayList<GenericValue>();
				if (UtilValidate.isNotEmpty(type)) {
					if (deposit){
						listFacilities = LogisticsFacilityUtil.listDepositFacilities(delegator, userLogin.getString("userLoginId"));
					} else {
						listFacilities = LogisticsPartyUtil.getFacilityByRoles(delegator, partyId, UtilMisc.toList("MANAGER", "OWNER"));
					}
				} else {
					listFacilities = LogisticsPartyUtil.getFacilityByRoles(delegator, partyId, UtilMisc.toList("MANAGER", "OWNER"));
				}
				List<String> listFacilityIds = new ArrayList<String>();
				for (GenericValue item : listFacilities) {
					listFacilityIds.add(item.getString("facilityId"));
				}
				EntityCondition Cond = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
				listAllConditions.add(Cond);
			}
		}
		
		if (deposit){
			String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			EntityCondition Cond = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company);
			listAllConditions.add(Cond);
		}
		List<GenericValue> listInventoryItem = delegator.findList("InventoryAndItemProduct",
				EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
		EntityCondition quantityCondQOH1 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
    	EntityCondition quantityCondATP1 = EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
    	List<EntityCondition> listCond1 = new ArrayList<EntityCondition>();
    	listCond1.add(quantityCondQOH1);
    	listCond1.add(quantityCondATP1);
    	EntityCondition quantityCond1 = EntityCondition.makeCondition(listCond1, EntityOperator.AND);
    	
    	EntityCondition quantityCondQOH2 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.EQUALS, BigDecimal.ZERO);
    	EntityCondition quantityCondATP2 = EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.NOT_EQUAL, BigDecimal.ZERO);
    	List<EntityCondition> listCond2 = new ArrayList<EntityCondition>();
    	listCond2.add(quantityCondQOH2);
    	listCond2.add(quantityCondATP2);
    	EntityCondition quantityCond2 = EntityCondition.makeCondition(listCond2, EntityOperator.AND);
    	
    	EntityCondition quantityCondQOH3 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
    	EntityCondition quantityCondATP3 = EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.LESS_THAN, BigDecimal.ZERO);
    	List<EntityCondition> listCond3 = new ArrayList<EntityCondition>();
    	listCond3.add(quantityCondQOH3);
    	listCond3.add(quantityCondATP3);
    	EntityCondition quantityCond3 = EntityCondition.makeCondition(listCond3, EntityOperator.AND);
    	
    	EntityCondition quantityCondQOH4 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
    	EntityCondition quantityCondATP4 = EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.EQUALS, BigDecimal.ZERO);
    	List<EntityCondition> listCond4 = new ArrayList<EntityCondition>();
    	listCond4.add(quantityCondQOH4);
    	listCond4.add(quantityCondATP4);
    	EntityCondition quantityCond4 = EntityCondition.makeCondition(listCond4, EntityOperator.AND);
    	
    	List<EntityCondition> listCondTotal = new ArrayList<EntityCondition>();
    	listCondTotal.add(quantityCond1);
    	listCondTotal.add(quantityCond2);
    	listCondTotal.add(quantityCond3);
    	listCondTotal.add(quantityCond4);
    	
    	EntityCondition quantityCond = EntityCondition.makeCondition(listCondTotal, EntityOperator.OR);
    	
    	listAllConditions.add(quantityCond);
		List<GenericValue> listInventoryItemTotal = delegator.findList("ProductNameByInventoryItemTotal",
				EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts,
				false);
		List<Map<String, Object>> listIterator = FastList.newInstance();
		for (GenericValue inventoryItemTotal : listInventoryItemTotal) {
			String productIdTotal = inventoryItemTotal.getString("productId");
			Map<String, Object> row = new HashMap<String, Object>();
			List<GenericValue> listRowDetails = FastList.newInstance();
			row.putAll(inventoryItemTotal);
			for (GenericValue inventoryItem : listInventoryItem) {
				String productId = inventoryItem.getString("productId");
				if (productIdTotal.equals(productId)) {
					listRowDetails.add(inventoryItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			for (GenericValue inv : listRowDetails) {
				List<GenericValue> listInventoryItemLabel = delegator.findList("InventoryItemAndLabel",
						EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inv.get("inventoryItemId"))),
						null, null, null, false);
				String statusId = null;
				if (!listInventoryItemLabel.isEmpty()) {
					statusId = "INV_LABELED";
				} else {
					statusId = "INV_NO_LABEL";
				}
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", inv.getString("productId")));
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("labelStatusId", statusId);
				mapTmp.put("inventoryItemId", inv.getString("inventoryItemId"));
				mapTmp.put("productId", inv.getString("productId"));
				mapTmp.put("productCode", product.getString("productCode"));
				mapTmp.put("expireDate", inv.getTimestamp("expireDate"));
				mapTmp.put("datetimeReceived", inv.getTimestamp("datetimeReceived"));
				mapTmp.put("facilityId", inv.getString("facilityId"));
				mapTmp.put("internalName", inv.getString("internalName"));
				mapTmp.put("quantityOnHandTotal", inv.getBigDecimal("quantityOnHandTotal"));
				mapTmp.put("availableToPromiseTotal", inv.getBigDecimal("availableToPromiseTotal"));
				mapTmp.put("quantityUomId", inv.getString("quantityUomId"));
				mapTmp.put("statusId", inv.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listShipments(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummyParty = null;
		try {
			List<Map<String, Object>> deliveries = FastList.newInstance();
			String TotalRows = "0";

			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;

			GenericValue userLogin = (GenericValue) context.get("userLogin");
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", userLogin.get("partyId"))));

			dummyParty = delegator.find("DeliveryDetail", EntityCondition.makeCondition(listAllConditions), null, null,
					listSortFields, opts);
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
			List<GenericValue> listDeliveries = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listDeliveries) {
				Map<String, Object> delivery = FastMap.newInstance();
				delivery.putAll(x);
				List<Map<String, Object>> deliveryItems = FastList.newInstance();
				String deliveryId = x.getString("deliveryId");
				List<GenericValue> litsDeliveryItem = delegator.findList("DeliveryItem",
						EntityCondition.makeCondition(
								UtilMisc.toMap("deliveryId", deliveryId, "statusId", "DELI_ITEM_EXPORTED")),
						null, null, null, false);
				for (GenericValue z : litsDeliveryItem) {
					Map<String, Object> deliveryItem = FastMap.newInstance();
					deliveryItem.put("quantity", z.get("actualExportedQuantity"));
					GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",
							z.get("fromOrderId"), "orderItemSeqId", z.get("fromOrderItemSeqId")), false);
					if (UtilValidate.isNotEmpty(orderItem)) {
						deliveryItem.put("unitListPrice", orderItem.get("unitListPrice"));
					}
					GenericValue inventoryItem = delegator.findOne("InventoryAndItemProduct",
							UtilMisc.toMap("inventoryItemId", z.get("inventoryItemId")), false);
					if (UtilValidate.isNotEmpty(inventoryItem)) {
						deliveryItem.put("productId", inventoryItem.get("productId"));
						deliveryItem.put("productName", inventoryItem.get("productName"));
						deliveryItem.put("quantityUomId", inventoryItem.get("quantityUomId"));
						deliveryItem.put("inventoryItemTypeId", inventoryItem.get("inventoryItemTypeId"));
						deliveryItem.put("currencyUomId", inventoryItem.get("currencyUomId"));
						deliveryItem.put("datetimeManufactured", inventoryItem.get("datetimeManufactured"));
						deliveryItem.put("expireDate", inventoryItem.get("expireDate"));
						deliveryItems.add(deliveryItem);
					}
				}
				delivery.put("rowDetail", deliveryItems);
				deliveries.add(delivery);
			}
			result.put("listIterator", deliveries);
			result.put("TotalRows", TotalRows);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dummyParty.close();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private static boolean hasPermission(String orderId, GenericValue userLogin, String action, Security security,
			Delegator delegator) {
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		String orderTypeId = orh.getOrderTypeId();
		String partyId = null;
		GenericValue orderParty = orh.getEndUserParty();
		if (UtilValidate.isEmpty(orderParty)) {
			orderParty = orh.getPlacingParty();
		}
		if (UtilValidate.isNotEmpty(orderParty)) {
			partyId = orderParty.getString("partyId");
		}
		boolean hasPermission = hasPermission(orderTypeId, partyId, userLogin, action, security);
		if (!hasPermission) {
			GenericValue placingCustomer = null;
			try {
				Map<String, Object> placingCustomerFields = UtilMisc.<String, Object> toMap("orderId", orderId,
						"partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
				placingCustomer = delegator.findOne("OrderRole", placingCustomerFields, false);
			} catch (GenericEntityException e) {
				Debug.logError("Could not select OrderRoles for order " + orderId + " due to " + e.getMessage(),
						module);
			}
			hasPermission = (placingCustomer != null);
		}
		return hasPermission;
	}

	private static boolean hasPermission(String orderTypeId, String partyId, GenericValue userLogin, String action,
			Security security) {
		boolean hasPermission = security.hasEntityPermission("ORDERMGR", "_" + action, userLogin);
		if (!hasPermission) {
			if (orderTypeId.equals("SALES_ORDER")) {
				if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, action, "MODULE", "DIS_SALESORDER")) {
					hasPermission = true;
				} else {
					// check sales agent/customer relationship
					List<GenericValue> repsCustomers = new LinkedList<GenericValue>();
					try {
						repsCustomers = EntityUtil
								.filterByDate(
										userLogin.getRelatedOne("Party", false)
												.getRelated("FromPartyRelationship", UtilMisc.toMap("roleTypeIdFrom",
														"AGENT", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId),
										null, false));
					} catch (GenericEntityException ex) {
						Debug.logError("Could not determine if " + partyId + " is a customer of user "
								+ userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
					}
					if ((repsCustomers != null) && (repsCustomers.size() > 0)
							&& (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
						hasPermission = true;
					}
					if (!hasPermission) {
						// check sales sales rep/customer relationship
						try {
							repsCustomers = EntityUtil
									.filterByDate(userLogin.getRelatedOne("Party", false)
											.getRelated("FromPartyRelationship", UtilMisc.toMap("roleTypeIdFrom",
													"SALES_REP", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId),
									null, false));
						} catch (GenericEntityException ex) {
							Debug.logError(
									"Could not determine if " + partyId + " is a customer of user "
											+ userLogin.getString("userLoginId") + " due to " + ex.getMessage(),
									module);
						}
						if ((repsCustomers != null) && (repsCustomers.size() > 0)
								&& (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
							hasPermission = true;
						}
					}
				}
			} else if ((orderTypeId.equals("PURCHASE_ORDER")
					&& (security.hasEntityPermission("ORDERMGR", "_PURCHASE_" + action, userLogin)))) {
				hasPermission = true;
			}
		}
		return hasPermission;
	}

	/**
	 * Service for checking to see if an order is fully completed or canceled
	 */
	public static Map<String, Object> checkItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orderId = (String) context.get("orderId");

		// check and make sure we have permission to change the order
		Security security = ctx.getSecurity();
		boolean hasPermission = DistributorServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
		if (!hasPermission) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
					"OrderYouDoNotHavePermissionToChangeThisOrdersStatus", locale));
		}
		// get the order header
		GenericValue orderHeader = null;
		try {
			orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Cannot get OrderHeader record", module);
		}
		if (orderHeader == null) {
			Debug.logError("OrderHeader came back as null", module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderCannotUpdateNullOrderHeader",
					UtilMisc.toMap("orderId", orderId), locale));
		}

		// get the order items
		List<GenericValue> orderItems = null;
		try {
			orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId), null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, "Cannot get OrderItem records", module);
			return ServiceUtil.returnError(
					UtilProperties.getMessage(resource_error, "OrderProblemGettingOrderItemRecords", locale));
		}

		String orderHeaderStatusId = orderHeader.getString("statusId");
		String orderTypeId = orderHeader.getString("orderTypeId");

		boolean allCanceled = true;
		boolean allComplete = true;
		boolean allApproved = true;
		if (orderItems != null) {
			for (GenericValue item : orderItems) {
				String statusId = item.getString("statusId");
				// Debug.logInfo("Item Status: " + statusId, module);
				if (!"ITEM_CANCELLED".equals(statusId)) {
					// Debug.logInfo("Not set to cancel", module);
					allCanceled = false;
					// Out of the box code
					if (!"ITEM_COMPLETED".equals(statusId)) {
						// Debug.logInfo("Not set to complete", module);
						allComplete = false;
						if (!"ITEM_APPROVED".equals(statusId)) {
							// Debug.logInfo("Not set to approve", module);
							allApproved = false;
							break;
						}
					}
				}
			}

			// find the next status to set to (if any)

			String newStatus = null;
			if (allCanceled) {
				if (!"PURCHASE_ORDER".equals(orderTypeId)) {
					newStatus = "ORDER_CANCELLED";
				}
			} else if (allComplete) {
				newStatus = "ORDER_COMPLETED";
			} else if (allApproved) {
				boolean changeToApprove = true;

				// NOTE DEJ20070805 I'm not sure why we would want to
				// auto-approve the header... adding at least this one exeption
				// so that we don't have to add processing, held, etc statuses
				// to the item status list
				// NOTE2 related to the above: appears this was a weird way to
				// set the order header status by setting all order item
				// statuses... changing that to be less weird and more direct
				// this is a bit of a pain: if the current statusId =
				// ProductStore.headerApprovedStatus and we don't have that
				// status in the history then we don't want to change it on
				// approving the items
				if (UtilValidate.isNotEmpty(orderHeader.getString("productStoreId"))) {
					try {
						GenericValue productStore = delegator.findOne("ProductStore",
								UtilMisc.toMap("productStoreId", orderHeader.getString("productStoreId")), false);
						if (productStore != null) {
							String headerApprovedStatus = productStore.getString("headerApprovedStatus");
							if (UtilValidate.isNotEmpty(headerApprovedStatus)) {
								if (headerApprovedStatus.equals(orderHeaderStatusId)) {
									Map<String, Object> orderStatusCheckMap = UtilMisc.<String, Object> toMap("orderId",
											orderId, "statusId", headerApprovedStatus, "orderItemSeqId", null);

									List<GenericValue> orderStatusList = delegator.findByAnd("OrderStatus",
											orderStatusCheckMap, null, false);
									// should be 1 in the history, but just in
									// case accept 0 too
									if (orderStatusList.size() <= 1) {
										changeToApprove = false;
									}
								}
							}
						}
					} catch (GenericEntityException e) {
						String errMsg = "Database error checking if we should change order header status to approved: "
								+ e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}

				if ("ORDER_SENT".equals(orderHeaderStatusId))
					changeToApprove = false;
				if ("ORDER_COMPLETED".equals(orderHeaderStatusId)) {
					if ("SALES_ORDER".equals(orderTypeId)) {
						changeToApprove = false;
					}
				}
				if ("ORDER_CANCELLED".equals(orderHeaderStatusId))
					changeToApprove = false;

				if (changeToApprove) {
					newStatus = "ORDER_APPROVED";
				}
			}

			// now set the new order status
			if (newStatus != null && !newStatus.equals(orderHeaderStatusId)) {
				Map<String, Object> serviceContext = UtilMisc.<String, Object> toMap("orderId", orderId, "statusId",
						newStatus, "userLogin", userLogin);
				Map<String, Object> newSttsResult = null;
				try {
					newSttsResult = dispatcher.runSync("changeOrderStatusWithoutAccTrans", serviceContext);
				} catch (GenericServiceException e) {
					Debug.logError(e, "Problem calling the changeOrderStatus service", module);
				}
				if (ServiceUtil.isError(newSttsResult)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
				}
			}
		} else {
			Debug.logWarning(UtilProperties.getMessage(resource_error, "OrderReceivedNullForOrderItemRecordsOrderId",
					UtilMisc.toMap("orderId", orderId), locale), module);
		}
		return ServiceUtil.returnSuccess();
	}

	/**
	 * perfect_not_delete_important
	 * 
	 * @throws GenericEntityException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listDistributors(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		List<Map<String, Object>> distributors = FastList.newInstance();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginPartyId = userLogin.getString("partyId");
			//String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<EntityCondition> conditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			opts.setDistinct(true);
			boolean isSearch = true;
			if (UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("partyCode");
			}
//			List<EntityCondition> conditions = FastList.newInstance();
			if (parameters.containsKey("sD")) {
				if ("N".equals(parameters.get("sD")[0])) {
					conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
				}
			}
			conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REJECTED"));
//			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//			conditions.add(EntityCondition
//					.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO",
//							"roleTypeIdFrom", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTOR_REL")));
            conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			
            /*if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)) {
			    List<String> distOfSupervisors = distributorOfSupervisor(delegator, userLogin);
                conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, distOfSupervisors));
			} else if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESMAN_MT", userLogin, false)) {
                List<String> distOfSalesmans = distributorOfSalesman(delegator, userLogin);
                conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, distOfSalesmans));
			} else if (MTLUtil.hasSecurityGroupPermission(delegator, "SALESADMIN_GT", userLogin, false) || MTLUtil.hasSecurityGroupPermission(delegator, "SALESADMIN_MT", userLogin, false)) {
                List<String> distOfSalesAdminGT = distOfSalesAdminGT(delegator, userLogin);
                conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, distOfSalesAdminGT));
			} else if (MTLUtil.hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, false)) {
                List<String> listEmplOfSalesManager = SupUtil.getManagerIdsOfChildDeptBySalesManager(delegator, userLogin);
                conditions.add(EntityCondition.makeCondition("supervisorId", EntityJoinOperator.IN, listEmplOfSalesManager));
            }*/
            
            if (SalesPartyUtil.isSalesman(delegator, userLoginPartyId)) {
            	List<String> listDistIds = PartyWorker.getDistributorBySalesman(delegator, userLoginPartyId);
            	if (UtilValidate.isEmpty(listDistIds)) {
            		isSearch = false;
            	} else if (listDistIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
            	}
            } else if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
            	conditions.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
            	List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
            	if (UtilValidate.isEmpty(listSupIds)) {
            		isSearch = false;
            	} else if (listSupIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
            	}
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
            	List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
            	if (UtilValidate.isEmpty(listSupIds)) {
            		isSearch = false;
            	} else if (listSupIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
            	}
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
            	List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
            	if (UtilValidate.isEmpty(listSupIds)) {
            		isSearch = false;
            	} else if (listSupIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
            	}
            } else if (SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
            	List<String> listDistIds = PartyWorker.getDistributorBySalesadmin(delegator, userLoginPartyId);
            	if (UtilValidate.isEmpty(listDistIds)) {
            		isSearch = false;
            	} else if (listDistIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
            	}
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)) {
            	String currentOrgId = SalesUtil.getCurrentOrganization(delegator, userLogin);
            	List<String> listDistIds = PartyWorker.getDistributorByOrg(delegator, currentOrgId , Boolean.TRUE);
            	if (UtilValidate.isEmpty(listDistIds)) {
            		isSearch = false;
            	} else if (listDistIds.size() == 1) {
            		conditions.add(EntityCondition.makeCondition("partyId", listDistIds.get(0)));
            	} else {
            		conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistIds));
            	}
            } else {
            	isSearch = false;
            }
			
			if (isSearch) {
				dummy = EntityMiscUtil.processIterator(parameters, result, delegator, "PartyDistributorInfos", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, listSortFields, opts);

				List<GenericValue> parties = FastList.newInstance();
				if(dummy != null) {
				    parties = dummy.getCompleteList();
	            }
				for (GenericValue x : parties) {
					Map<String, Object> party = FastMap.newInstance();
					party.put("partyId", x.get("partyId"));
					party.put("partyCode", x.get("partyCode"));
					party.put("statusId", x.get("statusId"));
					party.put("groupName", x.get("groupName"));
	                party.put("geoPointId", x.get("geoPointId"));
	                party.put("latitude", x.get("latitude"));
	                party.put("longitude", x.get("longitude"));
					party.put("preferredCurrencyUomId", x.get("preferredCurrencyUomId"));
					party.put("officeSiteName", x.get("officeSiteName"));
					party.put("contactNumber", x.get("contactNumber"));
					party.put("emailAddress", x.get("emailAddress"));
					party.put("address1", x.get("address1"));
//					party.putAll(getSupervisor(delegator, x.get("partyId")));
					party.put("supervisorId", x.get("supervisorId"));
					party.put("supervisor", x.get("supervisor"));
					distributors.add(party);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummy != null) {
				dummy.close();
			}
		}
		result.put("listIterator", distributors);
		return result;
	}
	public static List<String> distributorOfSupervisor(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
	    List<GenericValue> partyDistributors = delegator.findList("PartyDistributor", EntityCondition.makeCondition("supervisorId", userLogin.get("partyId")), null, null, null, false);
		return EntityUtil.getFieldListFromEntityList(partyDistributors, "partyId", true);
	}
	/*public static List<String> distributorOfSalesman(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
        GenericValue salesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", userLogin.get("partyId")), false);
        List<String> listDistributor = FastList.newInstance();
        if (UtilValidate.isNotEmpty(salesman)) {
            listDistributor.add(salesman.getString("distributorId"));
        }
		return listDistributor;
	}*/
	/*public static List<String> distOfSalesAdminGT(Delegator delegator, GenericValue userLogin) throws GenericEntityException {
        //GenericValue productStoreIds = delegator.findOne("ProductStoreRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "SELLER"), false);
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "SELLER")));
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		List<GenericValue> productStoreRoleSADs = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conditions), null, null, null, false);
		List<String> listDistributor = FastList.newInstance();
		for (GenericValue ps : productStoreRoleSADs) {
			List<EntityCondition> conditions1 = FastList.newInstance();
			conditions1.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", ps.get("productStoreId"), "roleTypeId", "CUSTOMER")));
			//productStoreIds.add(ps.getString("productStoreId"));
			List<GenericValue> productStoreIdsOfCus = delegator.findList("ProductStoreRole", EntityCondition.makeCondition(conditions1), null, null, null, false);
			for(GenericValue pSC: productStoreIdsOfCus){
				listDistributor.add(pSC.getString("partyId"));
			}
		}
		return listDistributor;
	}*/
	public static Map<String, Object> getSupervisor(Delegator delegator, Object distributorId) throws GenericEntityException, GenericServiceException {
		Map<String, Object> supervisor = FastMap.newInstance();
		GenericValue distributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", distributorId), false);
		if (UtilValidate.isNotEmpty(distributor)) {
			String supervisorId = distributor.getString("supervisorId");
			supervisor.put("supervisorId", supervisorId);
			supervisor.put("supervisor", PartyHelper.getPartyName(delegator, supervisorId, true, true));
		}
		return supervisor;
	}
	public static Map<String, Object> getSupervisorRepresentative(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Object partyIdTo) throws GenericEntityException, GenericServiceException {
		Map<String, Object> representative = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "MANAGER",
				"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "MANAGER")));
		List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
		if (UtilValidate.isNotEmpty(partyRelationships)) {
			String partyIdFrom = EntityUtil.getFirst(partyRelationships).getString("partyIdFrom");
			representative.put("representativeId", partyIdFrom);
			representative.put("representative", PartyHelper.getPartyName(delegator, partyIdFrom, true, true));
			representative.put("contactNumber", dispatcher.runSync("getPartyTelephone",
					UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", userLogin)).get("contactNumber"));
			representative.put("emailAddress", dispatcher.runSync("getPartyEmail",
					UtilMisc.toMap("partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "userLogin", userLogin)).get("emailAddress"));
		}
		return representative;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSalesexecutive(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", userLogin.get("partyId"), "roleTypeIdFrom", "DISTRIBUTOR",
						"roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_EMPLOYMENT")));
				List<GenericValue> dummy = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdTo"), null, null, false);
				listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, EntityUtil.getFieldListFromEntityList(dummy, "partyIdTo", true)));
			} else {
				List<Object> departmentsRollup = FastList.newInstance();
				List<String> departments = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
				for (String s : departments) {
					Organization buildOrg = PartyUtil.buildOrg(delegator, s, true, false);
					departmentsRollup.add(s);
					List<String> departmentsChild = EntityUtil.getFieldListFromEntityList(
							buildOrg.getAllDepartmentList(delegator)
							, "partyId", true);
					if (UtilValidate.isNotEmpty(departmentsChild)) {
						departmentsRollup.addAll(departmentsChild);
					}
				}
				listAllConditions.add(EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.IN, departmentsRollup));
			}
			listAllConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			listAllConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.EQUALS, "SALES_EXECUTIVE"));
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
			EntityListIterator listIterator = delegator.find("PartyFromAndPartyNameDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> listSalesman(DispatchContext dpct, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = (Delegator) dpct.getDelegator();
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        EntityFindOptions opt = (EntityFindOptions) context.get("opts");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginPartyId = userLogin.getString("partyId");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        try {
            String department = "";
            String supervisorId="";
            if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("department")
                    && UtilValidate.isNotEmpty(parameters.get("department")[0])) {
                department = parameters.get("department")[0];
            }

			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("supervisorId")
					&& UtilValidate.isNotEmpty(parameters.get("supervisorId")[0])) {
				supervisorId = parameters.get("supervisorId")[0];
				listAllConditions.add(EntityCondition.makeCondition("supervisorId", supervisorId));
			}

            List<String> parties = FastList.newInstance();
            if (UtilValidate.isEmpty(department)) {
                parties = EntityUtil.getFieldListFromEntityList(
                        PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
                        , "partyId", true);
            } else {
                parties = EntityUtil.getFieldListFromEntityList(
                        PartyUtil.getEmployeeInDepartmentByRole(delegator, department, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
                        , "partyId", true);
            }
            opt.setDistinct(true);
            boolean  isSearch = true;
            if (SalesPartyUtil.isSalessup(delegator, userLoginPartyId)) {
                listAllConditions.add(EntityCondition.makeCondition("supervisorId", userLoginPartyId));
            } else if (SalesPartyUtil.isSalesASM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByASM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesRSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByRSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesCSM(delegator, userLoginPartyId)) {
                List<String> listSupIds = PartyWorker.getSupervisorByCSM(delegator, userLoginPartyId);
                if (UtilValidate.isEmpty(listSupIds)) {
                    isSearch = false;
                } else if (listSupIds.size() == 1) {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", listSupIds.get(0)));
                } else {
                    listAllConditions.add(EntityCondition.makeCondition("supervisorId", EntityOperator.IN, listSupIds));
                }
            } else if (SalesPartyUtil.isSalesManager(delegator, userLoginPartyId) || SalesPartyUtil.isSalesAdminManager(delegator, userLoginPartyId)
                    ||SalesPartyUtil.isSalesAdmin(delegator, userLoginPartyId)) {
                //nothing
            } else {
                isSearch = false;
            }
            if (isSearch){
                listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"),
                        EntityCondition.makeCondition("statusId", null)), EntityOperator.OR));
                EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
                listIterator = EntityMiscUtil.processIterator(parameters,successResult,delegator,"PartySalesmanAndSupNameDetail",cond,null, null,listSortFields,opt);
            }
            successResult.put("listIterator", listIterator);
        } catch (Exception e) {
            Debug.log(e.getMessage());
            return ServiceUtil.returnError("Error get listSalesman");
        }
        return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListSalesmanSimple(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            List<EntityCondition> conditions = FastList.newInstance();
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
            List<GenericValue> listSalesman = delegator.findList("PartySalesman",
                    EntityCondition.makeCondition(conditions), null, listSortFields, opts, false);
            result.put("listSalesman", listSalesman);
        } catch (Exception e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "HasErrorWhenProcessing", (Locale)context.get("locale")));
        }
        return result;
    }
	
	public static Map<String, Object> checkUserLoginId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkUserLoginId(delegator, context.get("userLoginId"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}

	private static void createDistributorRole(Delegator delegator, String partyId, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		String logStorekeeper = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.storekeeper");
		List<String> roles = Arrays.asList("DISTRIBUTOR", "CUSTOMER", "MANAGER", "CHILD_MEMBER", 
				"BILL_TO_CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER", "BILL_FROM_VENDOR", 
				"OWNER", "SELLER", "SALES_EXECUTIVE", "STOCKING_COUNT", "STOCKING_SCAN", "STOCKING_CHECK", "STOCKING_INPUT", logStorekeeper);
		
		List<GenericValue> toBeStored = new LinkedList<GenericValue>();
		for (String r : roles) {
			//dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", r, "userLogin", userLogin));
			toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", r)));
		}
		delegator.storeAll(toBeStored);
	}
	private static void createDistributorRelationship(Delegator delegator, LocalDispatcher dispatcher, String partyId, Object organizationId, GenericValue userLogin) throws GenericServiceException {
		String roleDis = SalesUtil.getPropertyValue(delegator, "role.distributor");
		String roleOrg = SalesUtil.getPropertyValue(delegator, "role.org.to.customer");
		
		// relationship from distributor to organization
		dispatcher.runSync("createPartyRelationship",
				UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", roleDis,
						"roleTypeIdTo", roleOrg, "partyRelationshipTypeId", "DISTRIBUTOR_REL", "userLogin", userLogin));
		
		// relationship from distributor group to distributor
		String roleTypeIdParent = SalesUtil.getPropertyValue(delegator, "group.role.from.parent.member");
		String roleTypeIdChild = SalesUtil.getPropertyValue(delegator, "group.role.to.child.member");
		String relGroup = SalesUtil.getPropertyValue(delegator, "group.party.rel.member");
		dispatcher.runSync("createPartyRelationship",
				UtilMisc.toMap("partyIdFrom", "DISTRIBUTOR_GROUP", "partyIdTo", partyId, "roleTypeIdFrom",
						roleTypeIdParent, "roleTypeIdTo", roleTypeIdChild, "partyRelationshipTypeId", relGroup, "userLogin", userLogin));
        dispatcher.runSync("createPartyRelationship",
                UtilMisc.toMap("partyIdFrom", "DISTRIBUTOR_SERC", "partyIdTo", partyId, "roleTypeIdFrom",
                        "SECURITY_GROUP", "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "SECURITY_GROUP_REL", "userLogin", userLogin));
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = null;
		
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String partyCode = (String) context.get("partyCode");
			String partyFullName = (String) context.get("groupName"); 
			String facilityId = partyCode;
			String productStoreId = facilityId;
			
			try {
				UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
				UniqueUtil.checkUserLoginId(delegator, partyCode);
				
				GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", partyCode), false);
				if (facility != null) throw new Exception();
				
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", partyCode), false);
				if (productStore != null) throw new Exception();
			} catch (Exception e) {
				ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSUserIsAlreadyExists", locale));
			}
			
			// create party
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			partyId = "NPP" + organizationId + delegator.getNextSeqId("Party");
			if (UtilValidate.isEmpty(partyCode)) partyCode = partyId;
			
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("partyId", partyId);
			partyGroup.put("partyTypeId", "PARTY_GROUP");
			partyGroup.put("statusId", "PARTY_DISABLED");
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			Map<String, Object> resultCreateDis = dispatcher.runSync("createPartyGroup", partyGroup);
			if (ServiceUtil.isError(resultCreateDis)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateDis));
			}
			
			// store party code, because service "createPartyGroup" not store field "partyCode"
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", (partyCode!=null?partyCode:partyId)), EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			
			// create roles
			createDistributorRole(delegator, partyId, userLogin);
			
			// create relationships
			createDistributorRelationship(delegator, dispatcher, partyId, organizationId, userLogin);
			
			// create user login
			Map<String, Object> resultCreateUserLogin = dispatcher.runSync("createUserLogin", UtilMisc.toMap("userLoginId", partyCode, 
							"enabled", "Y", "currentPassword", context.get("currentPassword"), 
							"currentPasswordVerify", context.get("currentPasswordVerify"), "partyId", partyId, "userLogin", userLogin));
			if (ServiceUtil.isError(resultCreateUserLogin)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateUserLogin));
			}
			delegator.storeByCondition("UserLogin", UtilMisc.toMap("lastOrg", organizationId, "lastModule", "DISTRIBUTOR", "lastLocale", locale.toString()),
					EntityCondition.makeCondition("userLoginId", partyCode));
			
			// create user login group
			GenericValue userLoginSecurityGroup = delegator.makeValidValue("UserLoginSecurityGroup",
					UtilMisc.toMap("userLoginId", partyCode, "groupId", "DISTRIBUTOR_ADMIN", "fromDate", nowTimestamp, "organizationId", organizationId));
			userLoginSecurityGroup.create();
			
			// create postal address
			String address1 = (String) context.get("address1");
			String countryGeoId = (String) context.get("countryGeoId");
			String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
			String districtGeoId = (String) context.get("districtGeoId");
			String wardGeoId = (String) context.get("wardGeoId");
			String geoPointId = (String) context.get("geoPointId");
			String postalCode = (String) context.get("postalCode");
			String toName = (String) context.get("toName");
			String attnName = (String) context.get("attnName");
			String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", toName, attnName, 
					"PRIMARY_LOCATION", partyId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);
			
			if (postalAddressId != null) {
				// add others purpose into postal address
				dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, 
						"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
			}
			
			/*CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS", partyFullName, partyFullName, "SHIPPING_LOCATION", partyId, address1, countryGeoId,
					stateProvinceGeoId, districtGeoId, wardGeoId, stateProvinceGeoId, "70000", geoPointId, userLogin);*/
			
			// create telecom number 
			String contactNumber = (String) context.get("contactNumber");
			String telecomId = CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, partyFullName, partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, telecomId, "PHONE_SHIPPING", partyId, userLogin);
			
			// create email
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			
			// create tax info
			String taxAuthInfos = (String) context.get("taxAuthInfos");
			String defaultTaxAuthGeoId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.geo.id");
			String defaultTaxAuthPartyId = SalesUtil.getPropertyValue(delegator, "default.tax.auth.party.id");
			Map<String, Object> resultTaxInfo = dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", defaultTaxAuthGeoId, "taxAuthPartyId", defaultTaxAuthPartyId, "partyTaxId", taxAuthInfos, "userLogin", userLogin));
			if (ServiceUtil.isError(resultTaxInfo)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultTaxInfo));
			}
			
			// create party representative
			String representativeParams = (String) context.get("representative");
			Map<String, Object> resultCreateRep = createRepresentativeAdvance(delegator, dispatcher, partyId, representativeParams, userLogin, "DISTRIBUTOR", postalAddressId);
			//createRepresentative(delegator, dispatcher, partyId, representativeParams, "DISTRIBUTOR", userLogin);
			if (ServiceUtil.isError(resultCreateRep)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRep));
			}
			String representativeId = (String) resultCreateRep.get("representativeId");
			
			// create relationship with SUPor
			String supervisorId = (String) context.get("supervisorId");
            dispatcher.runSync("createPartyDistributor", UtilMisc.toMap("partyId", partyId,
                    "supervisorId", supervisorId, "userLogin", userLogin));
			if (UtilValidate.isNotEmpty(supervisorId)) {
				dispatcher.runSync("updateSupervisorDistributor", UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", supervisorId, "userLogin", userLogin));
			}
			
			// add party to product store
			List<String> productStoreIds = (List<String>) context.get("productStores[]");
			GenericValue productStoreOrg = null;
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				for (String prodStoreId : productStoreIds) {
					if (UtilValidate.isEmpty(productStoreOrg)) {
						productStoreOrg = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", prodStoreId), false);
					}
					dispatcher.runSync("createProductStoreRole", UtilMisc.toMap("partyId", partyId, "productStoreId", prodStoreId, "roleTypeId", "CUSTOMER", "userLogin", userLogin));
				}
			}
			
			// create facility of party
			String facilityParams = (String) context.get("facility");
			if (UtilValidate.isNotEmpty(facilityParams)) {
				//String facilityId = CrabEntity.noDuplicateId(delegator, "Facility", "facilityId", partyId);
				//String productStoreId = CrabEntity.noDuplicateId(delegator, "ProductStore", "productStoreId", facilityId);
				List<String> productCatalogs = (List<String>) context.get("productCatalogs[]");
				
				Map<String, Object> resultCreateProdStore = createDefaultStoreForDistributor(dispatcher, delegator, partyId, representativeId, facilityId, productStoreId, productCatalogs, productStoreOrg, userLogin);
				if (ServiceUtil.isError(resultCreateProdStore)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateProdStore));
				}
				
				Map<String, Object> resultCreateFacility = createDefaultFacilityForDistributor(dispatcher, delegator, locale, facilityParams, partyId, facilityId, productStoreId, userLogin, postalAddressId);
				if (ServiceUtil.isError(resultCreateFacility)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateFacility));
				}
			}
			
			// send notification
			String header = "";
	     	String state = "open";
	     	String action = "DistributorDetail?" + "partyId=" + partyId;
	     	String targetLink = "";
	     	String ntfType = "ONE";
	     	String sendToGroup = "N";
	     	String sendrecursive = "Y";
			header = UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSNewDistributorNotify", locale) + " [" + partyCode + "]";
			List<String> salesAdminManagerIds = SalesPartyUtil.getPartiesByRoles(delegator, UtilMisc.toList("SALESADMIN_MANAGER"), true, userLogin);
			NotificationUtil.sendNotify(dispatcher, locale, salesAdminManagerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		successResult.put("partyId", partyId);
		return successResult;
	}
	public static Map<String, Object> createRepresentativeAdvance(Delegator delegator, LocalDispatcher dispatcher, String partyId, 
			String representativeParams, GenericValue userLogin, String roleTypeIdTo, String primaryAddressContactMechId) throws GenericServiceException, GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		if (UtilValidate.isEmpty(representativeParams)) return successResult;
		
		String representativeId = null;
		
		// process parameters
		JSONObject representativeObj = JSONObject.fromObject(representativeParams);
		if (UtilValidate.isNotEmpty(representativeObj)) {
			Date birthDate = null;
			if (representativeObj.get("birthDate") instanceof Long) {
				Long birthDateL = representativeObj.getLong("birthDate");
				if (UtilValidate.isNotEmpty(birthDateL)) {
					birthDate = new Date(birthDateL);
				}
			}
			String partyFullName = "";
			if (representativeObj.containsKey("partyFullName") && representativeObj.get("partyFullName") != null) {
				partyFullName = representativeObj.getString("partyFullName");
			}
			String gender = representativeObj.containsKey("gender") ? (String) representativeObj.get("gender") : null;
			Map<String, Object> createRepCtx = FastMap.newInstance();
			createRepCtx.putAll(CallcenterServices.demarcatePersonName(partyFullName));
			createRepCtx.put("partyId", partyId + "WN");
			createRepCtx.put("statusId", "PARTY_ENABLED");
			createRepCtx.put("gender", gender);
			createRepCtx.put("birthDate", birthDate);
			createRepCtx.put("userLogin", userLogin);
			Map<String, Object> resultCreateRep = dispatcher.runSync("createPerson", createRepCtx);
			if (ServiceUtil.isError(resultCreateRep)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRep));
			}
			representativeId = (String) resultCreateRep.get("partyId");
			
			// create roles
			List<GenericValue> tobeStore = new LinkedList<GenericValue>();
			tobeStore.add(delegator.makeValue("PartyRole", "partyId", representativeId, "roleTypeId", "OWNER"));
			tobeStore.add(delegator.makeValue("PartyRole", "partyId", representativeId, "roleTypeId", "SELLER"));
			delegator.storeAll(tobeStore);
			
			Map<String, Object> resultCreateRel = dispatcher.runSync("createPartyRelationship", 
							UtilMisc.toMap("partyIdFrom", representativeId, "partyIdTo", partyId, "roleTypeIdFrom", "OWNER",
							"roleTypeIdTo", roleTypeIdTo, "partyRelationshipTypeId", "OWNER", "userLogin", userLogin));
			if (ServiceUtil.isError(resultCreateRel)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRel));
			}
			
			Boolean isUsePrimaryAddress = representativeObj.containsKey("isUsePrimaryAddress") ? (Boolean) representativeObj.get("isUsePrimaryAddress") : true;
			String repFullName = representativeObj.containsKey("partyFullName") ? (String) representativeObj.get("partyFullName") : null;
			if (isUsePrimaryAddress) {
				if (UtilValidate.isNotEmpty(primaryAddressContactMechId)) {
					Map<String, Object> partyContactMechCtx = UtilMisc.<String, Object>toMap(
							"partyId", representativeId, 
							"contactMechId", primaryAddressContactMechId, 
							"contactMechTypeId", "POSTAL_ADDRESS",
							"contactMechPurposeTypeId", "PRIMARY_LOCATION",
							"allowSolicitation", "Y", 
							"userLogin", userLogin);
					dispatcher.runSync("createPartyContactMech", partyContactMechCtx);
				}
			} else {
				// create postal address
				String address1 = representativeObj.containsKey("address1") ? (String) representativeObj.get("address1") : null;
				String countryGeoId = representativeObj.containsKey("countryGeoId") ? (String) representativeObj.get("countryGeoId") : null;
				String stateProvinceGeoId = representativeObj.containsKey("stateProvinceGeoId") ? (String) representativeObj.get("stateProvinceGeoId") : null;
				String districtGeoId = representativeObj.containsKey("districtGeoId") ? (String) representativeObj.get("districtGeoId") : null;
				String wardGeoId = representativeObj.containsKey("wardGeoId") ? (String) representativeObj.get("wardGeoId") : null;
				String geoPointId = representativeObj.containsKey("geoPointId") ? (String) representativeObj.get("geoPointId") : null;
				String postalCode = representativeObj.containsKey("postalCode") ? (String) representativeObj.get("postalCode") : null;
				String toName = representativeObj.containsKey("toName") ? (String) representativeObj.get("toName") : null;
				String attnName = representativeObj.containsKey("attnName") ? (String) representativeObj.get("attnName") : null;
				String postalAddressId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
						"POSTAL_ADDRESS", toName, attnName, 
						"PRIMARY_LOCATION", representativeId, address1, countryGeoId, stateProvinceGeoId, districtGeoId , wardGeoId, stateProvinceGeoId, postalCode, geoPointId, userLogin);
				
				if (UtilValidate.isNotEmpty(postalAddressId)) {
					// add others purpose into postal address
					dispatcher.runSync("createPartyContactMechPurpose", UtilMisc.toMap("partyId", representativeId, 
							"contactMechId", postalAddressId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "userLogin", userLogin));
				}
			}
			
			String contactNumber = representativeObj.containsKey("contactNumber") ? (String) representativeObj.get("contactNumber") : null;
			CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE", contactNumber, repFullName, representativeId, userLogin);

			String infoString = representativeObj.containsKey("infoString") ? (String) representativeObj.get("infoString") : null;
			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", infoString, "PRIMARY_EMAIL", representativeId, userLogin);
		}
		successResult.put("representativeId", representativeId);
		return successResult;
	}
	
	private static Map<String, Object> createDefaultStoreForDistributor(LocalDispatcher dispatcher, Delegator delegator, String partyId, String representativeId, 
			String facilityId, String productStoreId, List<String> productCatalogsAppl, GenericValue productStoreOrg, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		//	create product store has owner is distributor
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		//String salesMethodChannelEnumId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.method.channel.enum", delegator);
		//String defaultCurrencyUomId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.store.defaultCurrencyUomId", delegator);
		//String storeCreditAccountEnumId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.store.storeCreditAccountEnumId", delegator);
		
		String salesMethodChannelEnumId = null;
		String defaultCurrencyUomId = null;
		String storeCreditAccountEnumId = null;
		String vatTaxAuthGeoId = null;
		String vatTaxAuthPartyId = null;
		if (productStoreOrg != null) {
			salesMethodChannelEnumId = productStoreOrg.getString("salesMethodChannelEnumId");
			defaultCurrencyUomId = productStoreOrg.getString("defaultCurrencyUomId");
			storeCreditAccountEnumId = productStoreOrg.getString("storeCreditAccountEnumId");
			vatTaxAuthGeoId = productStoreOrg.getString("vatTaxAuthGeoId");
			vatTaxAuthPartyId = productStoreOrg.getString("vatTaxAuthPartyId");
		}
		
		Map<String, Object> resultCreateProdStore = dispatcher.runSync("createProductStoreOlb", UtilMisc.toMap(
						"productStoreId", productStoreId, "storeName", productStoreId, "salesMethodChannelEnumId",
						salesMethodChannelEnumId, "defaultCurrencyUomId", defaultCurrencyUomId, "storeCreditAccountEnumId", storeCreditAccountEnumId,
						"payToPartyId", partyId, "vatTaxAuthGeoId", vatTaxAuthGeoId,
						"vatTaxAuthPartyId", vatTaxAuthPartyId, "userLogin", userLogin));
		if (ServiceUtil.isError(resultCreateProdStore)) {
			return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateProdStore));
		}
		
		// apply catalog to product store
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if (UtilValidate.isNotEmpty(productCatalogsAppl)) {
			for (String catalogId : productCatalogsAppl) {
				dispatcher.runSync("createProductStoreCatalog", UtilMisc.toMap("prodCatalogId", catalogId, "fromDate", nowTimestamp,
								"productStoreId", productStoreId, "sequenceNum", Long.valueOf(productCatalogsAppl.indexOf(catalogId) + 1), "userLogin", userLogin));
			}
		}
		
		// create product store role
		List<GenericValue> tobeStored = new LinkedList<GenericValue>();
		//tobeStored.add(delegator.makeValue("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "OWNER", "productStoreId", productStoreId, "fromDate", nowTimestamp, "sequenceNum", 1)));
		tobeStored.add(delegator.makeValue("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "MANAGER", "productStoreId", productStoreId, "fromDate", nowTimestamp, "sequenceNum", Long.valueOf(1))));
		tobeStored.add(delegator.makeValue("ProductStoreRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SELLER", "productStoreId", productStoreId, "fromDate", nowTimestamp, "sequenceNum", Long.valueOf(2))));
		tobeStored.add(delegator.makeValue("ProductStoreRole", UtilMisc.toMap("partyId", representativeId, "roleTypeId", "SELLER", "productStoreId", productStoreId, "fromDate", nowTimestamp, "sequenceNum", Long.valueOf(3))));
		delegator.storeAll(tobeStored);
		
		// create product store payment setting
		String paymentMethodTypeId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.store.paymentMethodTypeId", delegator);
		String paymentServiceTypeEnumId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.store.paymentServiceTypeEnumId", delegator);
		dispatcher.runSync("createProductStorePaymentSetting", UtilMisc.toMap(
				"productStoreId", productStoreId, "paymentMethodTypeId", paymentMethodTypeId, "paymentServiceTypeEnumId", paymentServiceTypeEnumId, 
				"applyToAllProducts", "Y", "userLogin", userLogin));
		
		// create product store ship method
		String shipmentMethodTypeId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.sales.store.shipmentMethodTypeId", delegator);
		dispatcher.runSync("createProductStoreShipMeth", UtilMisc.toMap(
				"productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId, "partyId", "_NA_",
				"roleTypeId", "CARRIER", "allowUspsAddr", "N", "requireUspsAddr", "N", "includeNoChargeItems", "Y", "userLogin", userLogin));
		
		return successResult;
	}
	
	private static Map<String, Object> createDefaultFacilityForDistributor(LocalDispatcher dispatcher, Delegator delegator, Locale locale, 
			Object facilityParams, String partyId, String facilityId, String productStoreId, GenericValue userLogin, String primaryPostalAddressId) throws GenericServiceException, GenericEntityException {
		Map<String, Object> successResutl = ServiceUtil.returnSuccess();
		
		JSONObject facilityObj = JSONObject.fromObject(facilityParams);
		if (UtilValidate.isNotEmpty(facilityObj)) {
			String defaultInventoryItemTypeId = EntityUtilProperties.getPropertyValue("basesalesmtl.properties", "distributor.facility.defaultInventoryItemTypeId", delegator);
			
			Boolean isUsePrimaryAddress = (Boolean) facilityObj.get("isUsePrimaryAddress");
			String facilityName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFacility", locale) + " " + facilityId;
			String storeName = UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesChannel", locale) + " " + facilityId;
			String facilityConsignName = UtilProperties.getMessage("BaseSalesUiLabels", "BSFacilityConsignista", locale) + " " + facilityId;
			
			String postalAddressId = null;
			if (isUsePrimaryAddress) {
				postalAddressId = primaryPostalAddressId;
			} else {
				// create facility's postal address
				String address1 = (String) facilityObj.get("address1");
				String countryGeoId = (String) facilityObj.get("countryGeoId");
				String stateProvinceGeoId = (String) facilityObj.get("stateProvinceGeoId");
				String districtGeoId = (String) facilityObj.get("districtGeoId");
				String postalCode = (String) facilityObj.get("postalCode");
				String wardGeoId = (String) facilityObj.get("wardGeoId");
				String toName = (String) facilityObj.get("toName");
				String attnName = (String) facilityObj.get("attnName");
				Map<String, Object> resultCreatePostalAddress = dispatcher.runSync("createPostalAddress",
						UtilMisc.toMap("address1", address1, "city", stateProvinceGeoId, "countryGeoId", countryGeoId, "districtGeoId", districtGeoId,
								"postalCode", postalCode, "stateProvinceGeoId", stateProvinceGeoId, "wardGeoId", wardGeoId,
								"toName", toName, "attnName", attnName, "userLogin", userLogin));
				if (ServiceUtil.isError(resultCreatePostalAddress)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreatePostalAddress));
				}
				postalAddressId = (String) resultCreatePostalAddress.get("contactMechId");
			}
			
			//	create facility telecom number
			String telecomNumberId = null;
			String telecomNumber = (String) facilityObj.get("contactNumber");
			if (UtilValidate.isNotEmpty(telecomNumber)) {
				Map<String, Object> resultCreateTelecom = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap("askForName", facilityName,
						"contactNumber", telecomNumber, "userLogin", userLogin));
				if (ServiceUtil.isError(resultCreateTelecom)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateTelecom));
				}
				telecomNumberId = (String) resultCreateTelecom.get("contactMechId");
			}
			
			// map contact meches to facility
			Map<String, Object> resultCreateRel = dispatcher.runSync("createFacilityForPartyGroup", UtilMisc.toMap(
							"facilityId", facilityId, "facilityTypeId", "WAREHOUSE", "partyId", partyId, "primaryFacilityGroupId", "FACILITY_INTERNAL",
							"defaultInventoryItemTypeId", defaultInventoryItemTypeId, "facilityName", facilityName,
							"postalAddressId", postalAddressId, "telecomNumberId", telecomNumberId,
							"defaultDaysToShip", Long.valueOf("1"), "productStoreId", productStoreId, "userLogin", userLogin));
			if (ServiceUtil.isError(resultCreateRel)) {
				return ServiceUtil.returnError(ServiceUtil.getErrorMessage(resultCreateRel));
			}
			
			delegator.storeByCondition("ProductStore", UtilMisc.toMap("inventoryFacilityId", facilityId, "storeName", storeName), EntityCondition.makeCondition("productStoreId", productStoreId));
			
			// create product store facility
			dispatcher.runSync("createProductStoreFacility",
					UtilMisc.toMap("facilityId",  facilityId, "productStoreId", productStoreId,
							"fromDate", new Timestamp(System.currentTimeMillis()), "userLogin", userLogin));
			
			// create deposit facility (KHO KY GUI)
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			dispatcher.runSync("createFacilityForPartyGroup", UtilMisc.toMap(
							"facilityId", facilityId + "_KG", "facilityTypeId", "WAREHOUSE", "ownerPartyId", organizationId, "partyId", partyId, "primaryFacilityGroupId", "FACILITY_CONSIGN",
							"defaultInventoryItemTypeId", defaultInventoryItemTypeId, "facilityName", facilityConsignName,
							"postalAddressId", postalAddressId, "telecomNumberId", telecomNumberId,
							"defaultDaysToShip", Long.valueOf("1"), "productStoreId", productStoreId, "userLogin", userLogin));
			
		}
		
		return successResutl;
	}
	private static void updateDistributorRelationship(LocalDispatcher dispatcher, Delegator delegator, String partyId, Object organizationId,
			GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "DISTRIBUTOR",
						"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "DISTRIBUTOR_REL")));
		List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		boolean exists = false;
		for (GenericValue x : partyRelationships) {
			if (x.get("partyIdTo").equals(organizationId)) {
				exists = true;
				continue;
			}
			x.set("thruDate", new Timestamp(System.currentTimeMillis()));
			x.store();
		}
		if (!exists) {
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", partyId, "partyIdTo", organizationId, "roleTypeIdFrom", "DISTRIBUTOR",
							"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "DISTRIBUTOR_REL",
							"userLogin", userLogin));
		}
	}

	public static Map<String, Object> loadDistributorInfo(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, Object> distributorInfo = FastMap.newInstance();
			String partyId = (String) context.get("partyId");
			GenericValue partyGroup = delegator.findOne("PartyAndPartyGroupDetail", UtilMisc.toMap("partyId", partyId), false);
			distributorInfo.put("partyId", partyGroup.get("partyId"));
			distributorInfo.put("partyCode", partyGroup.get("partyCode"));
			distributorInfo.put("statusId", partyGroup.get("statusId"));
			distributorInfo.put("partyCode", partyGroup.get("partyCode"));
			distributorInfo.put("groupName", partyGroup.get("groupName"));
			distributorInfo.put("officeSiteName", partyGroup.get("officeSiteName"));
			distributorInfo.put("logoImageUrl", partyGroup.get("logoImageUrl"));
			distributorInfo.put("comments", partyGroup.get("comments"));
			distributorInfo.put("currencyUomId", partyGroup.get("preferredCurrencyUomId"));

			List<EntityCondition> conditions = FastList.newInstance();
			//	get taxAuthInfos
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM", "taxAuthPartyId", "VNM_TAX")));
			List<GenericValue> partyTaxAuthInfos = delegator.findList("PartyTaxAuthInfo",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyTaxId", "fromDate"), null, null, false);
			if (UtilValidate.isNotEmpty(partyTaxAuthInfos)) {
				GenericValue partyTaxAuthInfo = EntityUtil.getFirst(partyTaxAuthInfos);
				distributorInfo.put("taxAuthInfos", partyTaxAuthInfo.get("partyTaxId"));
				distributorInfo.put("taxAuthInfosfromDate", partyTaxAuthInfo.getTimestamp("fromDate").getTime());
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("fromDate"), null, false);
			for (GenericValue x : partyAndContactMechs) {
				switch (x.getString("contactMechTypeId")) {
				case "TELECOM_NUMBER":
					distributorInfo.put("contactNumber", x.get("tnContactNumber"));
					distributorInfo.put("contactNumberId", x.get("contactMechId"));
					break;
				case "EMAIL_ADDRESS":
					distributorInfo.put("infoString", x.get("infoString"));
					distributorInfo.put("infoStringId", x.get("contactMechId"));
					break;
				case "POSTAL_ADDRESS":
					distributorInfo.put("address1", x.get("paAddress1"));
					if ("Y".equals(context.get("detail"))) {
						distributorInfo.put("wardGeoId", getGeoName(delegator, x.get("paWardGeoId")));
						distributorInfo.put("districtGeoId", getGeoName(delegator, x.get("paDistrictGeoId")));
						distributorInfo.put("stateProvinceGeoId", getGeoName(delegator, x.get("paStateProvinceGeoId")));
						distributorInfo.put("countryGeoId", getGeoName(delegator, x.get("paCountryGeoId")));
						GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
						if (UtilValidate.isNotEmpty(postalAddress)) {
							String address = postalAddress.getString("address1") + ", " + 
									postalAddress.getString("wardGeoName") + ", " + 
									postalAddress.getString("districtGeoName") + ", " + 
									postalAddress.getString("stateProvinceGeoName") + ", " + 
									postalAddress.getString("countryGeoName");
							if (UtilValidate.isNotEmpty(address)) {
								address = address.replaceAll(", null, ", ", ");
								address = address.replaceAll(", null,", ", ");
							}
							distributorInfo.put("address", address);
						}
					} else {
						distributorInfo.put("wardGeoId", x.get("paWardGeoId"));
						distributorInfo.put("districtGeoId", x.get("paDistrictGeoId"));
						distributorInfo.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
						distributorInfo.put("countryGeoId", x.get("paCountryGeoId"));
						distributorInfo.put("addressId", x.get("contactMechId"));
					}
					break;
				default:
					break;
				}
			}
			//	get representative
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "DISTRIBUTOR",
					"roleTypeIdFrom", "OWNER", "partyRelationshipTypeId", "OWNER")));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
			if (UtilValidate.isNotEmpty(partyRelationships)) {
				String representativeId = (String) EntityUtil.getFirst(partyRelationships).get("partyIdFrom");
				distributorInfo.put("representative", getRepresentative(delegator, representativeId, context.get("detail")));
			}
			distributorInfo.putAll(getSupervisor(delegator, partyId));
			result.put("distributorInfo", distributorInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> updateDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			String partyId = (String) context.get("partyId");
			
			// update PartyDistributor
			GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyId), false);
			partyDistributor.set("partyCode", context.get("partyCode"));
			partyDistributor.set("fullName", context.get("groupName"));
			partyDistributor.set("supervisorId", context.get("supervisorId"));
			partyDistributor.store();
			Map<String, Object> partyGroup = CrabEntity.fastMaking(delegator, "PartyGroup", context);
			partyGroup.put("preferredCurrencyUomId", context.get("currencyUomId"));
			result = dispatcher.runSync("updatePartyGroup", partyGroup);
			
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", context.get("partyCode")),
					EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			updateDistributorRelationship(dispatcher, delegator, partyId, organizationId, userLogin);
			
			String contactMechId = CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String)context.get("contactNumberId"), "TELECOM_NUMBER",
					"PRIMARY_PHONE", context.get("contactNumber"), context.get("groupName"), partyId, userLogin);
			CallcenterServices.createPartyContactMechPurpose(dispatcher, delegator, contactMechId, "PHONE_SHIPPING", partyId, userLogin);
			
			CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)context.get("infoStringId"), "EMAIL_ADDRESS",
					context.get("infoString"), "PRIMARY_EMAIL", partyId, userLogin);
			if (UtilValidate.isEmpty(context.get("taxAuthInfosfromDate"))) {
				dispatcher.runSync("createPartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"), "userLogin", userLogin));
			} else {
				Long taxAuthInfosfromDate = (Long) context.get("taxAuthInfosfromDate");
				dispatcher.runSync("updatePartyTaxAuthInfo", UtilMisc.toMap("partyId", partyId, "taxAuthGeoId", "VNM",
						"taxAuthPartyId", "VNM_TAX", "partyTaxId", context.get("taxAuthInfos"),
						"fromDate", new Timestamp(taxAuthInfosfromDate), "userLogin", userLogin));
			}
			
			updateRepresentative(delegator, dispatcher, partyId, (String) context.get("representative"), "DISTRIBUTOR", userLogin);


			if (UtilValidate.isNotEmpty(context.get("supervisorId"))) {
				dispatcher.runSync("updateSupervisorDistributor",
						UtilMisc.toMap("partyIdTo", partyId, "partyIdFrom", context.get("supervisorId"), "userLogin", userLogin));
			}
			result.put("partyId", partyId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static String getGeoName(Delegator delegator, Object geoId)
			throws GenericEntityException {
		String geoName = "";
		if (UtilValidate.isNotEmpty(geoId)) {
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			if (UtilValidate.isNotEmpty(geo)) {
				geoName = geo.getString("geoName");
			}
		}
		return geoName;
	}
	
	public static Map<String, Object> getGeoPoint(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> geoPoints = delegator.findList("GeoPoint",
					EntityCondition.makeCondition("dataSourceId", EntityJoinOperator.EQUALS, "GEOPT_GOOGLE"),
					UtilMisc.toSet("geoPointId", "information"), null, null, false);
			result.put("geoPoints", geoPoints);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> updateFacilityBasic(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String facilityId = (String) context.get("facilityId");
		Map<String, Object> postalAddress = FastMap.newInstance();
		try {
			GenericValue facility = delegator.makeValidValue("Facility", context);
			facility.store();
			
			List<GenericValue> listContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
			if(UtilValidate.isEmpty(listContactMech)){
				GenericValue contactMech = delegator.makeValue("ContactMech");
				contactMech.put("contactMechId", delegator.getNextSeqId("ContactMech"));
				contactMech.put("contactMechTypeId", "POSTAL_ADDRESS");
				delegator.create(contactMech);
				
				GenericValue facContactMech = delegator.makeValue("FacilityContactMech");
				facContactMech.put("facilityId", facilityId);
				facContactMech.put("contactMechId", contactMech.get("contactMechId"));
				facContactMech.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMech);
				
				GenericValue facContactMechPurpose01 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose01.put("facilityId", facilityId);
				facContactMechPurpose01.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose01.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
				facContactMechPurpose01.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose01);
				
				GenericValue facContactMechPurpose02 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose02.put("facilityId", facilityId);
				facContactMechPurpose02.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose02.put("contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
				facContactMechPurpose02.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose02);
				
				GenericValue facContactMechPurpose03 = delegator.makeValue("FacilityContactMechPurpose");
				facContactMechPurpose03.put("facilityId", facilityId);
				facContactMechPurpose03.put("contactMechId", contactMech.get("contactMechId"));
				facContactMechPurpose03.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				facContactMechPurpose03.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(facContactMechPurpose03);
				
				postalAddress.put("contactMechId", contactMech.get("contactMechId"));
			}else{
				postalAddress.put("contactMechId", listContactMech.get(0).getString("contactMechId"));
			}
			postalAddress.put("address1", context.get("address1"));
			postalAddress.put("toName", context.get("facilityName"));
			postalAddress.put("countryGeoId", context.get("countryGeoId"));
			postalAddress.put("stateProvinceGeoId", context.get("provinceGeoId"));
			postalAddress.put("districtGeoId", context.get("districtGeoId"));
			postalAddress.put("wardGeoId", context.get("wardGeoId"));
			GenericValue savePostalAddress = delegator.makeValidValue("PostalAddress", postalAddress);
			delegator.createOrStore(savePostalAddress);
			
			if (UtilValidate.isNotEmpty(context.get("phoneNumberId"))) {
				GenericValue telecomNumber = delegator.makeValidValue("TelecomNumber",
						UtilMisc.toMap("contactMechId", context.get("phoneNumberId"), "contactNumber", context.get("phoneNumber")));
				telecomNumber.store();
			} else {
				dispatcher.runSync("createFacilityTelecomNumber",
						UtilMisc.toMap("facilityId", context.get("facilityId"), "contactNumber", context.get("phoneNumber"),
								"contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

    public static Map<String, Object> updateSupervisorDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            Object partyIdTo = context.get("partyIdTo"); //distributorId
            Object partyIdFrom = context.get("partyIdFrom"); //supervisorId
            String managerId = (String) partyIdFrom;
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "SALESSUP_DEPT", "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTION")));
            List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog", EntityCondition.makeCondition(conditions), null, null, null, false);
            boolean exists = false;
            List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, managerId, UtilDateTime.nowTimestamp());
            if(UtilValidate.isNotEmpty(departmentIds)) managerId = departmentIds.get(0);
            for (GenericValue x : partyRelationships) {
                if (x.get("partyIdFrom").equals(managerId)) {
                    exists = true;
                    continue;
                }
                x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                x.store();
            }
            if (!exists) {
                GenericValue supDeptRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "SALESSUP_DEPT"), false);
                if (supDeptRole == null) {
                    // add role dept to party
                    supDeptRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "SALESSUP_DEPT"));
                    delegator.create(supDeptRole);
                }
                LocalDispatcher dispatcher = ctx.getDispatcher();
                dispatcher.runSync("createPartyRelationDmsLog", UtilMisc.toMap("partyIdFrom", managerId, "partyIdTo", partyIdTo, "roleTypeIdFrom", "SALESSUP_DEPT", "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "DISTRIBUTION",
                        "logTypeId", "DISTRIBUTOR_SUPERVISOR","userLogin", context.get("userLogin")));

                //	thruDate all salesman
                conditions.clear();
                conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
                conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdTo, "roleTypeIdFrom", "DISTRIBUTOR", "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_EMPLOYMENT")));
                partyRelationships = delegator.findList("PartyRelationDmsLog", EntityCondition.makeCondition(conditions), null, null, null, false);
                for (GenericValue x : partyRelationships) {
                    x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                    x.store();
                }
                dispatcher.runSync("updatePartyDistributor", UtilMisc.toMap("partyId", partyIdTo, "supervisorId", partyIdFrom, "userLogin", context.get("userLogin")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError("error");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateDistributorProvideAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            List<String> oldDistributor = FastList.newInstance();
            Object partyIdTo = context.get("partyIdTo"); //distributorId
            Object partyIdFrom = context.get("partyIdFrom"); //customerId
            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "roleTypeIdFrom", "CUSTOMER",
                    "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "CUSTOMER_REL")));
            List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            boolean exists = false;
            for (GenericValue x : partyRelationships) {
                if (x.get("partyIdTo").equals(partyIdTo)) {
                    exists = true;
                    continue;
                }
                oldDistributor.add(x.getString("partyIdTo"));
                x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                x.store();
            }
            if (!exists) {
                dispatcher.runSync("createPartyRelationDmsLog",
                        UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", "CUSTOMER",
                                "roleTypeIdTo", "DISTRIBUTOR", "partyRelationshipTypeId", "CUSTOMER_REL",
                                "logTypeId", "CUSTOMER_DISTRIBUTOR", "userLogin", context.get("userLogin")));
                //	thruDate salesman
                conditions.clear();
                conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
                conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdFrom, "roleTypeIdFrom", "SALES_EXECUTIVE",
                        "roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL")));
                partyRelationships = delegator.findList("PartyRelationDmsLog",
                        EntityCondition.makeCondition(conditions), null, null, null, false);
                for (GenericValue x : partyRelationships) {
                    x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                    x.store();
                }

                GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdFrom), false);
                partyCustomer.set("distributorId", partyIdTo);
//                partyCustomer.set("salesmanId", null);
                partyCustomer.store();
            }
            //	createProductStoreRole
            List<String> productStores = (List<String>) context.get("productStores[]");
            if (UtilValidate.isNotEmpty(productStores)) {
                for (String s : productStores) {
                    conditions.clear();
                    conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
                    conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "CUSTOMER",
                            "productStoreId", s)));
                    List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole",
                            EntityCondition.makeCondition(conditions), null, null, null, false);
                    if (UtilValidate.isEmpty(productStoreRoles)) {
                        dispatcher.runSync("createProductStoreRole",
                                UtilMisc.toMap("partyId", partyIdFrom, "productStoreId", s, "roleTypeId", "CUSTOMER", "userLogin", context.get("userLogin")));
                    }
                }
            }
            //	thruDate store of oldDistributor
            conditions.clear();
            conditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.IN, oldDistributor));
            List<GenericValue> oldProductStores = delegator.findList("ProductStore",
                    EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
            List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(oldProductStores, "productStoreId", true);
            conditions.clear();
            conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdFrom, "roleTypeId", "CUSTOMER")));
            conditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, productStoreIds));
            List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            for (GenericValue x : productStoreRoles) {
                x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                x.store();
            }
            String customerId = (String)partyIdFrom;
            RouteUtils.unbindCustomerToRoute(delegator, customerId);
            RouteUtils.unbindSalesmanToCustomer(delegator, customerId);
            RouteUtils.removeRouteScheduleDetailDateByCustomer(delegator, customerId);
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError("error");
        }
        return result;
    }

	public static Map<String, Object> updateSalesmanProvideCustomerMT(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object partyIdTo = context.get("partyIdTo"); //customerId
			Object partyIdFrom = context.get("partyIdFrom"); //salesmanId
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "SALES_EXECUTIVE",
					"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL")));

			List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			boolean exists = false;
			List<String> listSalesmanAssigned = FastList.newInstance();
			for (GenericValue x : partyRelationships) {
				if (x.get("partyIdFrom").equals(partyIdFrom)) {
					exists = true;
					continue;
				}
				listSalesmanAssigned.add(x.getString("partyIdFrom"));
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
			GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdTo), false);
			partyCustomer.set("salesmanId", partyIdFrom);
			partyCustomer.store();
			if (!exists) {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				dispatcher.runSync("createPartyRelationDmsLog",
						UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", "SALES_EXECUTIVE",
								"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL", "logTypeId", "C","userLogin", context.get("userLogin")));
				//List<Object> listRoutes = FastList.newInstance();
//				for (String s : listSalesmanAssigned) {
//					listRoutes.addAll(EntityUtil.getFieldListFromEntityList(
//							delegator.findList("PartyRelationshipRouteDetail",
//									EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, s),
//									UtilMisc.toSet("routeId"), null, null, false)
//							, "routeId", true));
//				}
//				for (Object o : listRoutes) {
//					SupUtil.createAndUpdateRelationship(delegator, dispatcher, (String)o, (String)partyIdTo, "ROUTE", "CUSTOMER", "SALES_ROUTE", false, true);
//				}
			}
			//String customerId = (String)partyIdTo;
			//RouteUtils.unbindCustomerToRoute(delegator, customerId);
			//RouteUtils.removeRouteScheduleDetailDateByCustomer(delegator, customerId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> updateSalesmanProvideAgent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object partyIdTo = context.get("partyIdTo"); //customerId
			Object partyIdFrom = context.get("partyIdFrom"); //salesmanId
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "SALES_EXECUTIVE",
					"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL")));
			
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			boolean exists = false;
			List<String> listSalesmanAssigned = FastList.newInstance();
			for (GenericValue x : partyRelationships) {
				if (x.get("partyIdFrom").equals(partyIdFrom)) {
					exists = true;
					continue;
				}
				listSalesmanAssigned.add(x.getString("partyIdFrom"));
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
			GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyIdTo), false);
			partyCustomer.set("salesmanId", partyIdFrom);
			partyCustomer.store();
			if (!exists) {
				LocalDispatcher dispatcher = ctx.getDispatcher();
				dispatcher.runSync("createPartyRelationDmsLog",
						UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom", "SALES_EXECUTIVE",
								"roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "SALES_REP_REL", "logTypeId", "C","userLogin", context.get("userLogin")));
				List<Object> listRoutes = FastList.newInstance();
				for (String s : listSalesmanAssigned) {
					listRoutes.addAll(EntityUtil.getFieldListFromEntityList(
							delegator.findList("PartyRelationshipRouteDetail",
									EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, s),
									UtilMisc.toSet("routeId"), null, null, false)
							, "routeId", true));
				}
				for (Object o : listRoutes) {
					SupUtil.createAndUpdateRelationship(delegator, dispatcher, (String)o, (String)partyIdTo, "ROUTE", "CUSTOMER", "SALES_ROUTE", false, true);
				}
			}
			String customerId = (String)partyIdTo;
			RouteUtils.unbindCustomerToRoute(delegator, customerId);
			RouteUtils.removeRouteScheduleDetailDateByCustomer(delegator, customerId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSalesmanAssigned(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			Object partyIdFrom = userLogin.get("partyId");
			if (parameters.containsKey("partyIdFrom")) {
				partyIdFrom = parameters.get("partyIdFrom")[0];
			}
//			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
//			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "roleTypeIdFrom", "DISTRIBUTOR",
//					"roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_EMPLOYMENT")));
//			String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
//			Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
//    		List<String> parties = EntityUtil.getFieldListFromEntityList(buildOrg.getEmployeeInOrg(delegator), "partyId", true);
    		// conditions.add(EntityCondition.makeCondition("distributorId", partyIdFrom));
//    		conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));

            conditions.add(EntityCondition.makeCondition("distributorId", partyIdFrom));
			conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_ENABLED"));
			List<GenericValue> dummy = EntityMiscUtil.processIteratorToList(parameters, result, delegator,"PartySalesman",
					EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
			
			Debug.log(module + "::listSalesmanAssigned, partyIdFrom = " + partyIdFrom + ", list.sz = " + dummy.size());
			
			List<Map<String, Object>> listSalesman = FastList.newInstance();
			for (GenericValue x : dummy) {
				Map<String, Object> salesman = FastMap.newInstance();
				salesman.putAll(x);
                GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", x.get("partyId")), false);
                salesman.put("firstName", person.get("firstName"));
                salesman.put("middleName", person.get("middleName"));
                salesman.put("lastName", person.get("lastName"));
				salesman.put("contactNumber", dispatcher.runSync("getPartyTelephone",
						UtilMisc.toMap("partyId", x.get("partyId"), "contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", userLogin)).get("contactNumber"));
				List<String> managers = PartyUtil.getManagerOfEmpl(delegator, x.getString("partyId"), new Timestamp(System.currentTimeMillis()), userLogin.getString("userLoginId"));
				String manager = "";
				for (String s : managers) {
					manager += PartyHelper.getPartyName(delegator, s, true) + ", ";
				}
				if (manager.length() > 2) {
					manager = manager.substring(0, manager.length() - 2);
				}
				salesman.put("manager", manager);
				List<String> departments = PartyUtil.getDepartmentOfEmployee(delegator, x.getString("partyId"), new Timestamp(System.currentTimeMillis()));
				String department = "";
				for (String s : departments) {
					department += PartyHelper.getPartyName(delegator, s, true) + ", ";
				}
				if (department.length() > 2) {
					department = department.substring(0, department.length() - 2);
				}
				salesman.put("department", department);
				
				Debug.log(module + "::listSalesmanAssigned, GOT dummy x = " + x.get("partyId") + ", manager = " +
				manager + ", department = " + department);
				
				listSalesman.add(salesman);
			}
			result.put("listIterator", listSalesman);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListSalesmanByDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            List<EntityCondition> conditions = FastList.newInstance();
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
            String distributorCode = "";
            if (parameters.containsKey("distributorCode")) {
                distributorCode = parameters.get("distributorCode")[0];
            }
            GenericValue distributor = EntityUtil.getFirst(delegator.findList("PartyDistributor", EntityCondition.makeCondition("partyCode", distributorCode), null, listSortFields, opts, false));
            if(distributor == null) return result;
            String distributorId = distributor.getString("partyId");
            conditions.add(EntityCondition.makeCondition("distributorId", distributorId));
            List<GenericValue> dummy = delegator.findList("PartySalesman",
                    EntityCondition.makeCondition(conditions), null, null, null, false);
            List<Map<String, Object>> listSalesman = FastList.newInstance();
            for (GenericValue x : dummy) {
                Map<String, Object> salesman = FastMap.newInstance();
                salesman.putAll(x);
                salesman.put("contactNumber", dispatcher.runSync("getPartyTelephone",
                        UtilMisc.toMap("partyId", x.get("partyId"), "contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", userLogin)).get("contactNumber"));
//                List<String> managers = PartyUtil.getManagerOfEmpl(delegator, x.getString("partyId"), new Timestamp(System.currentTimeMillis()), userLogin.getString("userLoginId"));
//                String manager = "";
//                for (String s : managers) {
//                    manager += PartyHelper.getPartyName(delegator, s, true) + ", ";
//                }
//                if (manager.length() > 2) {
//                    manager = manager.substring(0, manager.length() - 2);
//                }
//                salesman.put("manager", manager);
                List<String> departments = PartyUtil.getDepartmentOfEmployee(delegator, x.getString("partyId"), new Timestamp(System.currentTimeMillis()));
                String department = "";
                for (String s : departments) {
                    department += PartyHelper.getPartyName(delegator, s, true) + ", ";
                }
                if (department.length() > 2) {
                    department = department.substring(0, department.length() - 2);
                }
                salesman.put("department", department);
                listSalesman.add(salesman);
            }
            result.put("listIterator", listSalesman);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public static List<String> getSalesmanOfDistributor(Delegator delegator, GenericValue userLogin, Object partyId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "DISTRIBUTOR",
				"roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_EMPLOYMENT")));
		String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
		List<String> parties = EntityUtil.getFieldListFromEntityList(buildOrg.getEmployeeInOrg(delegator), "partyId", true);
		conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
		List<GenericValue> dummy = delegator.findList("PartyToAndPartyNameDetail",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId"), null, null, false);
		return EntityUtil.getFieldListFromEntityList(dummy, "partyId", true);
	}
	public static Map<String, Object> getSalesmanAssigned(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
		try {
            List<GenericValue> dummy = FastList.newInstance();
            if(!"DISTRIBUTOR".equals(roleTypeIdFrom)) {
                GenericValue userLogin = (GenericValue) context.get("userLogin");
                List<EntityCondition> conditions = FastList.newInstance();
                conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
                conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", context.get("partyId"), "roleTypeIdFrom", context.get("roleTypeIdFrom"),
                        "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", context.get("partyRelationshipTypeId"))));
                String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
                List<String> parties = EntityUtil.getFieldListFromEntityList(buildOrg.getEmployeeInOrg(delegator), "partyId", true);
                conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
                dummy = delegator.findList("PartyToAndPartyNameDetail",
                        EntityCondition.makeCondition(conditions), null, null, null, false);
            }
            else {
                dummy = delegator.findList("PartySalesman", EntityCondition.makeCondition("distributorId", context.get("partyId")), null, null, null, false);
            }
			List<Map<String, Object>> listSalesman = FastList.newInstance();
			for (GenericValue x : dummy) {
				Map<String, Object> salesman = FastMap.newInstance();
				salesman.putAll(x);
                GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", x.get("partyId")), false);
                salesman.put("firstName", person.get("firstName"));
                salesman.put("middleName", person.get("middleName"));
                salesman.put("lastName", person.get("lastName"));
                List<String> departments = PartyUtil.getDepartmentOfEmployee(delegator, x.getString("partyId"), new Timestamp(System.currentTimeMillis()));
				String department = "";
				for (String s : departments) {
					department += PartyHelper.getPartyName(delegator, s, true) + ", ";
				}
				if (department.length() > 2) {
					department = department.substring(0, department.length() - 2);
				}
				salesman.put("department", department);
				listSalesman.add(salesman);
			}
			result.put("listSalesman", listSalesman);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> getSalesmanAvailable(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", context.get("partyId"), "roleTypeIdFrom", context.get("roleTypeIdFrom"),
					"roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", context.get("partyRelationshipTypeId"))));
			String companyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			Organization buildOrg = PartyUtil.buildOrg(delegator, companyId, true, false);
			List<String> parties = EntityUtil.getFieldListFromEntityList(buildOrg.getEmployeeInOrg(delegator), "partyId", true);
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
			List<GenericValue> dummy = delegator.findList("PartySalesman", EntityCondition.makeCondition("distributorId", EntityOperator.NOT_EQUAL,null), UtilMisc.toSet("partyId"), null, null, false);
			List<String> partiesNotIn = EntityUtil.getFieldListFromEntityList(dummy, "partyId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
            String supervisorId = (String) context.get("supervisorId");
			if (UtilValidate.isEmpty(supervisorId)) {
				parties = EntityUtil.getFieldListFromEntityList(
						PartyUtil.getEmployeeInDepartmentByRole(delegator, userLogin, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
						, "partyId", true);
			} else {
                String department = SalesPartyUtil.getOrgIdManagedByParty(delegator,supervisorId);
				parties = EntityUtil.getFieldListFromEntityList(
						PartyUtil.getEmployeeInDepartmentByRole(delegator, department, "SALES_EXECUTIVE", new Timestamp(System.currentTimeMillis()))
						, "partyId", true);
			}
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, parties));
			if (UtilValidate.isNotEmpty(partiesNotIn)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN, partiesNotIn));
			}
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("roleTypeIdFrom", "SALES_EXECUTIVE",
					"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyRelationshipTypeId", "EMPLOYEE")));
			List<GenericValue> listSalesman = delegator.findList("PartyFromAndPartyNameDetail",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : listSalesman) {
				x.set("partyIdTo", PartyHelper.getPartyName(delegator, x.getString("partyIdTo"), true, true));
			}
			result.put("listSalesman", listSalesman);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> assignSalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			Object partyIdFrom = context.get("partyIdFrom");
			Object partyIdTo = context.get("partyIdTo");
			//	createPartyRelationship
//			dispatcher.runSync("createPartyRelationship",
//					UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom",
//							context.get("roleTypeIdFrom"), "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId",
//							context.get("partyRelationshipTypeId"), "userLogin", context.get("userLogin")));
			
			if ("DISTRIBUTOR".equals(context.get("roleTypeIdFrom"))) {
                dispatcher.runSync("createPartyRelationDmsLog",
                        UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom",
                                context.get("roleTypeIdFrom"), "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId",
                                context.get("partyRelationshipTypeId"), "logTypeId", "SALESMAN_DISTRIBUTOR","userLogin", context.get("userLogin")));
                GenericValue partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", partyIdTo), false);
                partySalesman.set("distributorId", partyIdFrom);
                partySalesman.store();
				//	createProductStoreRole
				//	get productStoreId of distributor
				List<GenericValue> productStores = delegator.findList("ProductStore",
						EntityCondition.makeCondition("payToPartyId", EntityComparisonOperator.EQUALS, partyIdFrom),
						UtilMisc.toSet("productStoreId"), null, null, false);
				for (GenericValue x : productStores) {
					dispatcher.runSync("createProductStoreRole",
							UtilMisc.toMap("partyId", partyIdTo, "productStoreId", x.get("productStoreId"), "roleTypeId", "SELLER", "userLogin", context.get("userLogin")));
				}
			}
			else {
                dispatcher.runSync("createPartyRelationship",
                        UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "roleTypeIdFrom",
                                context.get("roleTypeIdFrom"), "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId",
                                context.get("partyRelationshipTypeId"), "userLogin", context.get("userLogin")));
            }
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	public static Map<String, Object> unassignSalesman(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object partyIdFrom = context.get("partyIdFrom"); //distributorId
			Object partyIdTo = context.get("partyIdTo"); //salesmanId
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo,
					"roleTypeIdFrom", context.get("roleTypeIdFrom"), "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", context.get("partyRelationshipTypeId"))));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
			if ("DISTRIBUTOR".equals(context.get("roleTypeIdFrom"))) {
			    //set thruDate for relation distributor-salesman
			    conditions.clear();
                conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
                conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo,
                        "roleTypeIdFrom", context.get("roleTypeIdFrom"), "roleTypeIdTo", "SALES_EXECUTIVE", "partyRelationshipTypeId", context.get("partyRelationshipTypeId"))));
                List<GenericValue> dmsLogs = delegator.findList("PartyRelationDmsLog",
                        EntityCondition.makeCondition(conditions), null, null, null, false);
                for (GenericValue x : dmsLogs) {
                    x.set("thruDate", new Timestamp(System.currentTimeMillis()));
                    x.store();
                }

                GenericValue partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", partyIdTo), false);
                partySalesman.set("distributorId", null);
                partySalesman.store();
				//	removeProductStoreRole
				//	get productStoreId of distributor
				List<GenericValue> productStores = delegator.findList("ProductStore",
						EntityCondition.makeCondition("payToPartyId", EntityComparisonOperator.EQUALS, partyIdFrom),
						UtilMisc.toSet("productStoreId"), null, null, false);
				for (GenericValue x : productStores) {
					conditions.clear();
					conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
					conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyIdTo,"roleTypeId", "SELLER", "productStoreId", x.get("productStoreId"))));
					List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole",
							EntityCondition.makeCondition(conditions), null, null, null, false);
					for (GenericValue z : productStoreRoles) {
						z.set("thruDate", new Timestamp(System.currentTimeMillis()));
						z.store();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFacilityByOwnerParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("type")) {
				GenericValue userLogin = (GenericValue)context.get("userLogin");
				String userLoginId = userLogin.getString("userLoginId");
				String partyOwnerId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId); 
				
				/* OLD CODE
				listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.EQUALS, ownerPartyId));
				String type = parameters.get("type")[0];
				switch (type) {
				case "deposit":
					listAllConditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.NOT_EQUAL, ownerPartyId));
					break;
				case "owner":
					listAllConditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.EQUALS, ownerPartyId));
					break;
				default:
					break;
				}
				EntityListIterator listIterator = delegator.find("FacilityAndProductStore",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				 */
				// NEW CODE
				String type = parameters.get("type")[0];
				switch (type) {
				case "deposit":
					List<String> listCondition = FastList.newInstance();
					listCondition.add("FACILITY_ADMIN");
					listCondition.add("MANAGER");
					listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.EQUALS, partyOwnerId));
					/*listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, ownerPartyId));*/
					listAllConditions.add(EntityCondition.makeCondition("facilityTypeId", EntityJoinOperator.NOT_EQUAL, "VIRTUAL_WAREHOUSE"));
					listAllConditions.add(EntityCondition.makeCondition("roleTypeId", EntityJoinOperator.IN, listCondition));
					listAllConditions.add(EntityUtil.getFilterByDateExpr());
					listAllConditions.add(EntityCondition.makeCondition("primaryFacilityGroupId", EntityJoinOperator.EQUALS, "FACILITY_CONSIGN"));
					break;
				case "owner":
					listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.EQUALS, userLogin.getString("partyId")));
					break;
				default:
					break;
				}
				Set<String> selectFields = FastSet.newInstance();
				selectFields.add("facilityId");
				selectFields.add("facilityCode");
				selectFields.add("facilityTypeId");
				selectFields.add("parentFacilityId");
				selectFields.add("ownerPartyId");
				selectFields.add("defaultInventoryItemTypeId");
				selectFields.add("facilityName");
				selectFields.add("primaryFacilityGroupId");
				selectFields.add("oldSquareFootage");
				selectFields.add("facilitySize");
				selectFields.add("facilitySizeUomId");
				selectFields.add("productStoreId");
				selectFields.add("defaultDaysToShip");
				selectFields.add("openedDate");
				selectFields.add("closedDate");
				selectFields.add("description");
				selectFields.add("defaultDimensionUomId");
				selectFields.add("defaultWeightUomId");
				selectFields.add("geoPointId");
				selectFields.add("fullName");
				opts.setDistinct(true);
				EntityListIterator listIterator = delegator.find("FacilityAndFacilityParty", EntityCondition.makeCondition(listAllConditions), null, selectFields, listSortFields, opts);
				
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling listFacilityByOwnerParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return result;
	}
	
	public static Map<String, Object> rejectDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId));
			List<GenericValue> agreements = delegator.findList("Agreement",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(agreements)) {
				throw new Exception();
			}
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId),
					EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.EQUALS, partyId)
					), EntityJoinOperator.OR));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "OWNER"));
			List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : partyRelationships) {
				x.set("thruDate", new Timestamp(System.currentTimeMillis()));
				x.store();
			}
		} catch (Exception e) {
			result = ServiceUtil.returnError("error");
		}
		return result;
	}
	
	public static Map<String, Object> salersOfDistributor(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String partyId = (String) context.get("partyId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, partyId));
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityJoinOperator.EQUALS, "CUSTOMER"));
			List<GenericValue> productStoreRoles = delegator.findList("ProductStoreRole",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("productStoreId"), null, null, false);
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(productStoreRoles, "productStoreId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("productStoreId", EntityJoinOperator.IN, productStoreIds));
			conditions.add(EntityCondition.makeCondition("roleTypeId", EntityJoinOperator.EQUALS, "SELLER"));
			productStoreRoles = delegator.findList("ProductStoreRoleDetail",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyId", "productStoreId", "storeName"), null, null, false);
			List<Map<String, Object>> salers = FastList.newInstance();
			for (GenericValue x : productStoreRoles) {
				Map<String, Object> saler = FastMap.newInstance();
				saler.putAll(x);
				saler.put("partyName", CrabEntity.getPartyName(delegator, x.get("partyId")));
				saler.put("partyCode", CrabEntity.getPartyCode(delegator, x.get("partyId")));
				saler.put("contactNumber", dispatcher.runSync("getPartyTelephone",
						UtilMisc.toMap("partyId", x.get("partyId"), "contactMechPurposeTypeId", "PRIMARY_PHONE", "userLogin", context.get("userLogin"))).get("contactNumber"));
				saler.put("emailAddress", dispatcher.runSync("getPartyEmail",
						UtilMisc.toMap("partyId", x.get("partyId"), "contactMechPurposeTypeId", "PRIMARY_EMAIL", "userLogin", context.get("userLogin"))).get("emailAddress"));
				salers.add(saler);
			}
			result.put("salers", salers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listRoutes(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
            List<String> listSortFields = (List<String>) context.get("listSortFields");
            EntityFindOptions opts = (EntityFindOptions) context.get("opts");
            Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
            if (parameters.containsKey("partyId")) {
                String partyId = parameters.get("partyId")[0];
                listAllConditions.add(EntityCondition.makeCondition("executorId", partyId));
            }
            Set<String> fieldsToSelect = null;
            if (parameters.containsKey("distinct")) {
                if ("Y".equals(parameters.get("distinct")[0])) {
                    fieldsToSelect = FastSet.newInstance();
                    fieldsToSelect.addAll(UtilMisc.toSet("routeId", "routeName", "routeCode", "description", "executorId", "statusId"));
                    opts.setDistinct(true);
                }
            }
            listAllConditions.add(EntityCondition.makeCondition("statusId", "ROUTE_ENABLED"));
            EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters, result, delegator, "Route",
                    EntityCondition.makeCondition(listAllConditions), null, fieldsToSelect, listSortFields, opts);
            result.put("listIterator", listIterator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStores(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			if (parameters.containsKey("partyId")) {
				String partyId = parameters.get("partyId")[0];
				conditions.add(EntityCondition.makeCondition("salesmanId",partyId));
			}
			conditions.add(EntityCondition.makeCondition("statusId","PARTY_ENABLED"));
			EntityListIterator listIterator = EntityMiscUtil.processIterator(parameters,result,delegator,"PartyCustomer",
                    EntityCondition.makeCondition(conditions),null,null,listSortFields,opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public static String createRepresentative(Delegator delegator, LocalDispatcher dispatcher, String partyId, String representative, String roleTypeIdTo, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		String representativeId = "";
		JSONObject representativeObj = JSONObject.fromObject(representative);
		if (UtilValidate.isNotEmpty(representativeObj)) {
			Date birthDate = null;
			if (representativeObj.get("birthDate") instanceof Long) {
				Long birthDateL = representativeObj.getLong("birthDate");
				if (UtilValidate.isNotEmpty(birthDateL)) {
					birthDate = new Date(birthDateL);
				}
			}
			Map<String, Object> mapCreateRepresentative = FastMap.newInstance();
			mapCreateRepresentative.putAll(CallcenterServices.demarcatePersonName(representativeObj.getString("partyFullName")));
			mapCreateRepresentative.put("partyId", partyId + "WN");
			mapCreateRepresentative.put("statusId", "PARTY_ENABLED");
			mapCreateRepresentative.put("gender", representativeObj.get("gender"));
			mapCreateRepresentative.put("birthDate", birthDate);
			mapCreateRepresentative.put("userLogin", userLogin);
			representativeId = (String) dispatcher.runSync("createPerson", mapCreateRepresentative).get("partyId");
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", representativeId, "roleTypeId", "OWNER", "userLogin", userLogin));
			dispatcher.runSync("createPartyRole",
					UtilMisc.toMap("partyId", representativeId, "roleTypeId", "SELLER", "userLogin", userLogin));
			dispatcher.runSync("createPartyRelationship",
					UtilMisc.toMap("partyIdFrom", representativeId, "partyIdTo", partyId, "roleTypeIdFrom", "OWNER",
							"roleTypeIdTo", roleTypeIdTo, "partyRelationshipTypeId", "OWNER", "userLogin", userLogin));
			
			String contactMechId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
					"POSTAL_ADDRESS", representativeObj.get("partyFullName"), representativeObj.get("partyFullName"),
					"SHIPPING_LOCATION", representativeId, representativeObj.get("address1"),
					representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
					representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"),
					representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
			CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS",
					representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "PRIMARY_LOCATION", representativeId,
					representativeObj.get("address1"), representativeObj.get("countryGeoId"),
					representativeObj.get("stateProvinceGeoId"), representativeObj.get("districtGeoId"),
					representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"), "70000", userLogin);

			CallcenterServices.createContactMechTelecomNumber(dispatcher, "TELECOM_NUMBER", "PRIMARY_PHONE",
					representativeObj.get("contactNumber"), representativeObj.get("partyFullName"), representativeId, userLogin);

			CallcenterServices.createContactMechEmail(dispatcher, "EMAIL_ADDRESS", representativeObj.get("infoString"),
					"PRIMARY_EMAIL", representativeId, userLogin);
		}
		return representativeId;
	}
	@SuppressWarnings("deprecation")
	public static void updateRepresentative(Delegator delegator, LocalDispatcher dispatcher, String partyId, String representative, String roleTypeIdTo, GenericValue userLogin) throws GenericServiceException, GenericEntityException {
		JSONObject representativeObj = JSONObject.fromObject(representative);
		if (UtilValidate.isNotEmpty(representativeObj)) {
			if (!representativeObj.containsKey("partyId")) {
				createRepresentative(delegator, dispatcher, partyId, representative, roleTypeIdTo, userLogin);
			} else {
				String representativeId = representativeObj.getString("partyId");
				if (UtilValidate.isEmpty(representativeId)) {
					createRepresentative(delegator, dispatcher, partyId, representative, roleTypeIdTo, userLogin);
				} else {
					Date birthDate = null;
					if (representativeObj.get("birthDate") instanceof Long) {
						Long birthDateL = representativeObj.getLong("birthDate");
						if (UtilValidate.isNotEmpty(birthDateL)) {
							birthDate = new Date(birthDateL);
						}
					}
					Map<String, Object> mapUpdateRepresentative = FastMap.newInstance();
					mapUpdateRepresentative.putAll(CallcenterServices.demarcatePersonName((String) representativeObj.get("partyFullName")));
					mapUpdateRepresentative.put("partyId", representativeId);
					mapUpdateRepresentative.put("gender", representativeObj.get("gender"));
					mapUpdateRepresentative.put("birthDate", birthDate);
					mapUpdateRepresentative.put("userLogin", userLogin);
					dispatcher.runSync("updatePerson", mapUpdateRepresentative);
					
					if (UtilValidate.isEmpty(representativeObj.get("addressId"))) {
						String contactMechId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
								"POSTAL_ADDRESS", representativeObj.get("partyFullName"), representativeObj.get("partyFullName"),
								"SHIPPING_LOCATION", representativeId, representativeObj.get("address1"),
								representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
								representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"),
								representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
						CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS",
								representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "PRIMARY_LOCATION", representativeId,
								representativeObj.get("address1"), representativeObj.get("countryGeoId"),
								representativeObj.get("stateProvinceGeoId"), representativeObj.get("districtGeoId"),
								representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
					} else {
						if(representativeObj.containsKey("isUsePrimaryAddressOld")) {
							Boolean isUsePrimaryAddress = representativeObj.containsKey("isUsePrimaryAddress") ? (Boolean) representativeObj.get("isUsePrimaryAddress") : true;
							Boolean isUsePrimaryAddressOld = representativeObj.containsKey("isUsePrimaryAddressOld") ? (Boolean) representativeObj.get("isUsePrimaryAddressOld") : true;
							List<EntityCondition> contactMechCond = FastList.newInstance();
							contactMechCond.add(EntityCondition.makeCondition("partyId", partyId));
							contactMechCond.add(EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_LOCATION"));
							contactMechCond.add(EntityUtil.getFilterByDateExpr());
							List<GenericValue> contactMech = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(contactMechCond, EntityOperator.AND), null, null, null, false);
							if(isUsePrimaryAddressOld==false && isUsePrimaryAddress==true) {
								Map<String,Object> mapDelete=FastMap.newInstance();
								mapDelete.put("userLogin", userLogin);
								mapDelete.put("partyId",representativeId);
								mapDelete.put("contactMechId",(String) representativeObj.get("addressId"));
								dispatcher.runSync("deletePartyContactMechAndPurpose", mapDelete);
								CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, (String) contactMech.get(0).get("contactMechId"), "POSTAL_ADDRESS",
										representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "PRIMARY_LOCATION", representativeId,
										representativeObj.get("address1"), representativeObj.get("countryGeoId"),
										representativeObj.get("stateProvinceGeoId"), representativeObj.get("districtGeoId"),
										representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
								CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, (String) (String) contactMech.get(0).get("contactMechId"),
										"POSTAL_ADDRESS", representativeObj.get("partyFullName"), representativeObj.get("partyFullName"),
										"SHIPPING_LOCATION", representativeId, representativeObj.get("address1"),
										representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
										representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"),
										representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
							} else if (isUsePrimaryAddressOld==true && isUsePrimaryAddress==false){
								Map<String,Object> mapDelete=FastMap.newInstance();
								mapDelete.put("userLogin", userLogin);
								mapDelete.put("partyId",representativeId);
								mapDelete.put("contactMechId",(String) representativeObj.get("addressId"));
								dispatcher.runSync("deletePartyContactMechAndPurpose", mapDelete);
								String contactMechId = CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, "",
										"POSTAL_ADDRESS", representativeObj.get("partyFullName"), representativeObj.get("partyFullName"),
										"SHIPPING_LOCATION", representativeId, representativeObj.get("address1"),
										representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
										representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"),
										representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
								CallcenterServices.createContactMechPostalAddress(dispatcher, delegator, contactMechId, "POSTAL_ADDRESS",
										representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "PRIMARY_LOCATION", representativeId,
										representativeObj.get("address1"), representativeObj.get("countryGeoId"),
										representativeObj.get("stateProvinceGeoId"), representativeObj.get("districtGeoId"),
										representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"), "70000", userLogin);
							} else{
								CallcenterServices.updateContactMechPostalAddress(dispatcher, delegator, (String) representativeObj.get("addressId"), "POSTAL_ADDRESS",
										representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "SHIPPING_LOCATION", representativeId,
										representativeObj.get("address1"), representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
										representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"),
										"70000", userLogin);
							}
						} else {
							CallcenterServices.updateContactMechPostalAddress(dispatcher, delegator, (String) representativeObj.get("addressId"), "POSTAL_ADDRESS",
									representativeObj.get("partyFullName"), representativeObj.get("partyFullName"), "SHIPPING_LOCATION", representativeId,
									representativeObj.get("address1"), representativeObj.get("countryGeoId"), representativeObj.get("stateProvinceGeoId"),
									representativeObj.get("districtGeoId"), representativeObj.get("wardGeoId"), representativeObj.get("stateProvinceGeoId"),
									"70000", userLogin);
						}
					}
					CallcenterServices.updateContactMechTelecomNumber(dispatcher, delegator, (String) representativeObj.get("contactNumberId"), "TELECOM_NUMBER",
							"PRIMARY_PHONE", representativeObj.get("contactNumber"), representativeObj.get("partyFullName"), representativeId, userLogin);
					CallcenterServices.updateContactMechEmail(dispatcher, delegator, (String)representativeObj.get("infoStringId"), "EMAIL_ADDRESS",
							representativeObj.get("infoString"), "PRIMARY_EMAIL", representativeId, userLogin);
				}
			}
		}
	}
	public static Map<String, Object> getRepresentative(Delegator delegator, String partyId, Object detail) throws GenericEntityException {
		Map<String, Object> representative = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(partyId)) {
			GenericValue person = delegator.findOne("PersonAndPartyGroup", UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isNotEmpty(person)) {
				representative.put("partyFullName", person.get("partyFullName"));
				representative.put("gender", person.get("gender"));
				representative.put("birthDate", person.get("birthDate"));
				representative.put("partyId", person.get("partyId"));
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
				List<GenericValue> partyAndContactMechs = delegator.findList("PartyAndContactMech",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : partyAndContactMechs) {
					switch (x.getString("contactMechTypeId")) {
					case "TELECOM_NUMBER":
						representative.put("contactNumber", x.get("tnContactNumber"));
						representative.put("contactNumberId", x.get("contactMechId"));
						break;
					case "EMAIL_ADDRESS":
						representative.put("infoString", x.get("infoString"));
						representative.put("infoStringId", x.get("contactMechId"));
						break;
					case "POSTAL_ADDRESS":
						representative.put("address1", x.get("paAddress1"));
						representative.put("contactMechId", x.get("contactMechId"));
						if ("Y".equals(detail)) {
							representative.put("wardGeoId", DistributorServices.getGeoName(delegator, x.get("paWardGeoId")));
							representative.put("districtGeoId", DistributorServices.getGeoName(delegator, x.get("paDistrictGeoId")));
							representative.put("stateProvinceGeoId", DistributorServices.getGeoName(delegator, x.get("paStateProvinceGeoId")));
							representative.put("countryGeoId", DistributorServices.getGeoName(delegator, x.get("paCountryGeoId")));
							GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", x.get("contactMechId")), false);
							if (UtilValidate.isNotEmpty(postalAddress)) {
								String address = postalAddress.getString("address1") + ", " + 
										postalAddress.getString("wardGeoName") + ", " + 
										postalAddress.getString("districtGeoName") + ", " + 
										postalAddress.getString("stateProvinceGeoName") + ", " + 
										postalAddress.getString("countryGeoName");
								if (UtilValidate.isNotEmpty(address)) {
									address = address.replaceAll(", null, ", ", ");
									address = address.replaceAll(", null,", ", ");
								}
								representative.put("address", address);
							}
						} else {
							representative.put("wardGeoId", x.get("paWardGeoId"));
							representative.put("districtGeoId", x.get("paDistrictGeoId"));
							representative.put("stateProvinceGeoId", x.get("paStateProvinceGeoId"));
							representative.put("countryGeoId", x.get("paCountryGeoId"));
							representative.put("addressId", x.get("contactMechId"));
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return representative;
	}
	
	public static Map<String, Object> loadProductStores(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			if ("N".equals(context.get("getAll"))) {
				String payToPartyId = (String) context.get("payToPartyId");
				if (UtilValidate.isNotEmpty(payToPartyId)) {
					conditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.EQUALS, payToPartyId));
				} else {
					GenericValue userLogin = (GenericValue) context.get("userLogin");
					String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					conditions.add(EntityCondition.makeCondition("payToPartyId", EntityJoinOperator.EQUALS, organizationId));
				}
			}
			List<GenericValue> productStores = delegator.findList("ProductStore",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			result.put("productStores", productStores);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> loadProdCatalogs(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			String prodCatalogId = (String) context.get("prodCatalogId");
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(prodCatalogId)) {
				conditions.add(EntityCondition.makeCondition("prodCatalogId", EntityJoinOperator.EQUALS, prodCatalogId));
			}
			List<GenericValue> prodCatalogs = delegator.findList("ProdCatalog",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			result.put("prodCatalogs", prodCatalogs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listSalesmanEmplLeave(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		List<Map<String, Object>> salesmanLeave = FastList.newInstance();
		String TotalRows = "0";
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			
			TimeZone timeZone = (TimeZone)context.get("timeZone");
			Locale locale = (Locale)context.get("locale");
			Calendar cal = Calendar.getInstance();
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
			Timestamp yearStart = UtilDateTime.getYearStart(timestamp);
			Timestamp yearEnd = UtilDateTime.getYearEnd(yearStart, timeZone, locale);
			
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			opts.setDistinct(true);
			List<EntityCondition> conditions = (List<EntityCondition>) context.get("listAllConditions");
			String condition = conditions.toString();
			if (!condition.contains("fromDate")) {
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Timestamp entryDateStartDay = new Timestamp(cal.getTimeInMillis());
				conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDateStartDay));
			}
			List<String> salesmanId = getSalesmanOfDistributor(delegator, userLogin, userLogin.get("partyId"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, salesmanId));
			dummy = delegator.find("EmplLeave", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
			TotalRows = String.valueOf(dummy.getResultsTotalSize());
			List<GenericValue> parties = dummy.getPartialList(start, pagesize);
			for (GenericValue x : parties) {
				Map<String, Object> party = FastMap.newInstance();
				String tempPartyId = x.getString("partyId");
				Float totalDayLeave = EmployeeHelper.getNbrDayLeave(delegator, x);
				party.put("partyId", tempPartyId);
				party.put("partyName", PartyUtil.getPersonName(delegator, tempPartyId));
				party.put("nbrDayLeave", totalDayLeave);
				party.put("statusId", x.get("statusId"));
				party.put("emplLeaveReasonTypeId", x.get("emplLeaveReasonTypeId"));
				party.put("emplLeaveId", x.get("emplLeaveId"));
				String workingShiftId = x.getString("workingShiftId");
				Timestamp fromDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, x.getTimestamp("fromDate"), 
						x.getString("fromDateLeaveTypeId"), EmployeeLeaveServices.START_LEAVE);
				Timestamp thruDateLeave = EmployeeHelper.getTimeEmplLeave(delegator, workingShiftId, x.getTimestamp("thruDate"), 
						x.getString("thruDateLeaveTypeId"), EmployeeLeaveServices.END_LEAVE);
				party.put("fromDate", fromDateLeave.getTime());
				party.put("thruDate", thruDateLeave.getTime());
				party.put("dateApplication", x.getTimestamp("dateApplication").getTime());
				List<GenericValue> emplPosTypeList = PartyUtil.getPositionTypeOfEmplInPeriod(delegator, tempPartyId, yearStart, yearEnd);
				List<String> emplPosTypeDes = EntityUtil.getFieldListFromEntityList(emplPosTypeList, "description", true);
				party.put("emplPositionType", StringUtils.join(emplPosTypeDes, ", "));
				List<String> deptList = PartyUtil.getDepartmentOfEmployee(delegator, tempPartyId, yearStart, yearEnd);
				List<String> deptNameList = CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", deptList, "partyId", "groupName");
				party.put("department", StringUtils.join(deptNameList, ", "));
				party.put("workingShiftId", x.get("workingShiftId"));
				party.put("fromDateLeaveTypeId", x.get("fromDateLeaveTypeId"));
				party.put("thruDateLeaveTypeId", x.get("thruDateLeaveTypeId"));
				party.put("description", x.get("description"));
				party.put("commentApproval", x.get("commentApproval"));
				salesmanLeave.add(party);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummy != null) {
				dummy.close();
			}
		}
		result.put("listIterator", salesmanLeave);
		result.put("TotalRows", TotalRows);
		return result;
	}

    @SuppressWarnings("unchecked")
	public static Map<String, Object> setDistributorStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("statusId");
        try {
            GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyId), false);
            GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
            String partyCode = (String) party.get("partyCode");
            GenericValue userLogin = EntityUtil.getFirst(delegator.findList("UserLogin", 
            		EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "lastModule", "DISTRIBUTOR")), null, null, null, false));
            GenericValue productStore = EntityUtil.getFirst(delegator.findList("ProductStore", 
            		EntityCondition.makeCondition(UtilMisc.toMap("payToPartyId", partyId)), null, null, null , false));
            
            if (statusId.equals("PARTY_ENABLED")){
            	partyDistributor.set("statusId", statusId);
            	userLogin.set("enabled", "Y");
            	productStore.set("statusId", "PRODSTORE_ENABLED");
            } else if (statusId.equals("PARTY_DISABLED")){
            	partyDistributor.set("statusId", statusId);
            	userLogin.set("enabled", "N");
            	productStore.set("statusId", "PRODSTORE_DISABLED");
            }
            
            partyDistributor.store();
            userLogin.store();
            productStore.store();
            ctx.getDispatcher().runSync("setPartyStatus", context);
        } catch (GenericEntityException e1) {
            Debug.logError(e1, e1.getMessage(), module);
            return ServiceUtil.returnError(e1.getMessage() );
        } catch (GenericServiceException e2) {
            Debug.logError(e2, e2.getMessage(), module);
            return ServiceUtil.returnError( e2.getMessage());
        }
        Map<String, Object> results = ServiceUtil.returnSuccess();
        return results;
    }

	public static Map<String, Object> listSalesmanSamples(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		opts.setLimit(10);
		opts.setMaxRows(10);
		try {
			List<GenericValue> listSalesman = delegator.findList("PartySalesmanInformationDetails", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
			if (listSalesman != null) {
				for (GenericValue itemSalesman: listSalesman) {
					Map<String, Object> itemMap = itemSalesman.getAllFields();
					List<GenericValue> payHistory = delegator.findList("PayHistory", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", itemSalesman.getString("partyId"),
							"partyIdFrom", itemSalesman.getString("partyIdFrom"), "roleTypeIdTo", "EMPLOYEE", "roleTypeIdFrom", "INTERNAL_ORGANIZATIO")),
							null, null, null, false);
						itemMap.put("dateJoinCompany", payHistory.size() != 0?payHistory.get(0).getString("fromDate"):null);
						itemMap.put("periodTypeId", payHistory.size() != 0?payHistory.get(0).getString("periodTypeName"):null);
						itemMap.put("amount", payHistory.size() != 0?payHistory.get(0).getString("amount"):null);
					List<GenericValue> userLogin = delegator.findList("UserLoginAndSecurityGroupSales", EntityCondition.makeCondition(UtilMisc.toMap("partyId", itemSalesman.getString("partyId"))),
							null, null, null, false);
						itemMap.put("userLoginId", userLogin.size() != 0?userLogin.get(0).getString("userLoginId"):null);
//						itemMap.put("descriptionGroup", userLogin.size() != 0?userLogin.get(0).getString("description"):null);
					listIterator.add(itemMap);
				}
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createDataSalesmanImport(DispatchContext ctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			String sizeSalesmans = parameters.get("sizeSalesmans")[0];
			int len = Integer.parseInt(sizeSalesmans);
			for (int i=0; i < len; i++){
				String sequence = parameters.get("salesmans["+i+"][sequence]")[0];
				String partyCode = parameters.get("salesmans["+i+"][partyCode]")[0];
				String lastName = parameters.get("salesmans["+i+"][lastName]")[0];
				String middleName = parameters.get("salesmans["+i+"][middleName]")[0];
				String firstName = parameters.get("salesmans["+i+"][firstName]")[0];
				String gender = parameters.get("salesmans["+i+"][gender]")[0];
				String birthday = parameters.get("salesmans["+i+"][birthday]")[0];
				String idNumber = parameters.get("salesmans["+i+"][idNumber]")[0];
				String religion = parameters.get("salesmans["+i+"][religion]")[0];
				String idIssueDate = parameters.get("salesmans["+i+"][idIssueDate]")[0];
				String idIssuePlace = parameters.get("salesmans["+i+"][idIssuePlace]")[0];
				String maritalStatusId = parameters.get("salesmans["+i+"][maritalStatusId]")[0];
				String ethnicOrigin = parameters.get("salesmans["+i+"][ethnicOrigin]")[0];
				String nationality = parameters.get("salesmans["+i+"][nationality]")[0];
				String nativeLand = parameters.get("salesmans["+i+"][nativeLand]")[0];
				String partyIdFrom = parameters.get("salesmans["+i+"][partyIdFrom]")[0];
				String emplPositionTypeId = parameters.get("salesmans["+i+"][emplPositionTypeId]")[0];
				String dateJoinCompany = parameters.get("salesmans["+i+"][dateJoinCompany]")[0];
				String amount = parameters.get("salesmans["+i+"][amount]")[0];
				String periodTypeId = parameters.get("salesmans["+i+"][periodTypeId]")[0];
				String userLoginId = parameters.get("salesmans["+i+"][userLoginId]")[0];
				String password = parameters.get("salesmans["+i+"][password]")[0];
				String isParticipateIns = parameters.get("salesmans["+i+"][isParticipateIns]")[0];
				String workingStatusId = parameters.get("salesmans["+i+"][workingStatusId]")[0];
				String permanentRes = parameters.get("salesmans["+i+"][permanentRes]")[0];
				String currRes = parameters.get("salesmans["+i+"][currRes]")[0];

				Map<String, Object> dataCtx = FastMap.newInstance();
				dataCtx.put("partyCode", partyCode);
				dataCtx.put("lastName", lastName);
				dataCtx.put("middleName", middleName);
				dataCtx.put("firstName", firstName);
				dataCtx.put("gender", gender);
				dataCtx.put("birthday", birthday);
				dataCtx.put("idNumber", idNumber);
				dataCtx.put("religion", religion);
				dataCtx.put("idIssueDate", idIssueDate);
				dataCtx.put("idIssuePlace", idIssuePlace);
				dataCtx.put("maritalStatusId", maritalStatusId);
				dataCtx.put("ethnicOrigin", ethnicOrigin);
				dataCtx.put("nationality", nationality);
				dataCtx.put("nativeLand", nativeLand);
				dataCtx.put("partyIdFrom", partyIdFrom);
				dataCtx.put("emplPositionTypeId", emplPositionTypeId);
				dataCtx.put("dateJoinCompany", dateJoinCompany);
				dataCtx.put("salaryBaseFlat", amount);
				dataCtx.put("periodTypeId", periodTypeId);
				dataCtx.put("userLoginId", userLoginId);
				dataCtx.put("password", password);
				dataCtx.put("isParticipateIns", isParticipateIns);
				dataCtx.put("workingStatusId", workingStatusId);
				dataCtx.put("permanentRes", permanentRes);
				dataCtx.put("currRes", currRes);
				dataCtx.put("userLogin", userLogin);
				Map<String, Object>	result = dispatcher.runSync("createNewSalesmanImport", dataCtx);
				dataCtx.put("sequence",sequence);
				if(result.containsKey("partyId")){
					dataCtx.put("statusImport","success");
					dataCtx.put("message", UtilProperties.getMessage("BaseSalesUiLabels", "BSCreateSuccessful", locale));
				}else{
					dataCtx.put("statusImport","error");
					dataCtx.put("message",result.get("message"));
				}
				listIterator.add(dataCtx);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

}
