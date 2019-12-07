package com.olbius.basepo.plan;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.plan.processor.Inventory;
import com.olbius.basepo.plan.processor.PurchaseOrder;
import com.olbius.basepo.plan.processor.SalesOrder;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

public class PlanServicesv2 {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductPlan(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("productPlanTypeId", "PO_PLAN", "organizationPartyId", organizationId)));
			EntityListIterator listIterator = delegator.find("ProductPlanHeaderAndParty",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductNonVirtual(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("isVirtual", EntityJoinOperator.NOT_EQUAL, "Y"));
			listSortFields.add("productCode");
			if (parameters.containsKey("productId")) {
				String productId = parameters.get("productId")[0];
				if (UtilValidate.isNotEmpty(productId)) {
					listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN,
							Arrays.asList(productId.split(","))));
				}
			}
			if (parameters.containsKey("displayOption")) {
				String displayOption = parameters.get("displayOption")[0];
				JSONObject displayObj = JSONObject.fromObject(displayOption);
				Object viewOn = displayObj.get("viewOn");
				if ("HasSalesForecast".equals(viewOn)) {
					GenericValue userLogin = (GenericValue) context.get("userLogin");
					String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
							userLogin.getString("userLoginId"));
					listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.IN,
							getProductHasForecastInPeriod(delegator, displayObj, organizationId)));
				}
			}
			EntityListIterator listIterator = delegator.find("Product",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static List<Object> getProductHasForecastInPeriod(Delegator delegator, JSONObject displayOption,
			Object organizationId) throws GenericEntityException {
		List<Object> products = FastList.newInstance();
		List<GenericValue> timePeriod = getTimePeriodSales(delegator, displayOption.getString("periodType"),
				displayOption.getInt("previous"), displayOption.getInt("next"));
		if (UtilValidate.isNotEmpty(timePeriod)) {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(
					EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationId));
			conditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN,
					EntityUtil.getFieldListFromEntityList(timePeriod, "customTimePeriodId", true)));
			List<GenericValue> salesForecasts = delegator.findList("SalesForecast",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(salesForecasts)) {
				List<Object> salesForecastIds = EntityUtil.getFieldListFromEntityList(salesForecasts, "salesForecastId",
						true);
				conditions.clear();
				conditions.add(
						EntityCondition.makeCondition("quantity", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
				conditions
						.add(EntityCondition.makeCondition("salesForecastId", EntityJoinOperator.IN, salesForecastIds));
				List<GenericValue> salesForecastDetails = delegator.findList("SalesForecastDetail",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				products = EntityUtil.getFieldListFromEntityList(salesForecastDetails, "productId", true);
			}
		}
		return products;
	}

	public static Map<String, Object> loadIndicesOfProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			Object productPlanId = context.get("productPlanId");
			Object productId = context.get("productId");
			String periodType = (String) context.get("periodType");
			int previous = (Integer) context.get("previous");
			int next = (Integer) context.get("next");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> compressedPackage = FastMap.newInstance();

			List<GenericValue> timePeriod = getTimePeriodPurchase(delegator, periodType, previous, next);

			compressedPackage.putAll(getGridConfigSetupPlan(timePeriod, locale));
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));

			compressedPackage.put("localdata",
					getGridDataSetupPlan(delegator, timePeriod, organizationId, productPlanId, productId, locale));
			result.put("compressedPackage", compressedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createOrStoreProductPlanItem(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object productPlanId = context.get("productPlanId");
			Object productId = context.get("productId");
			JSONObject purchaseForecast = JSONObject.fromObject(context.get("purchaseForecast"));
			String uid = purchaseForecast.getString("uid");
			if ("PurchaseForecast".equals(uid)) {
				Set<String> keys = purchaseForecast.keySet();
				for (String k : keys) {
					GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod",
							UtilMisc.toMap("customTimePeriodId", k), false);
					if (UtilValidate.isNotEmpty(customTimePeriod)) {
						GenericValue productPlanItem = delegator.makeValidValue("ProductPlanItem",
								UtilMisc.toMap("productPlanId", productPlanId, "customTimePeriodId", k, "productId",
										productId, "planQuantity", BigDecimal.valueOf(purchaseForecast.getDouble(k)),
										"statusId", "PLANITEM_CREATED"));
						delegator.createOrStore(productPlanItem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	private static List<Map<String, Object>> getGridDataSetupPlan(Delegator delegator, List<GenericValue> timePeriod,
			Object organizationId, Object productPlanId, Object product, Locale locale) throws GenericEntityException {
		List<Map<String, Object>> localdata = FastList.newInstance();
		SQLProcessor processor = new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap"));
		List<String> indices = UtilMisc.toList("OpeningInventoryQuantity", "QuantitySold", "SalesForecast",
				"QuantityOrdered", "EndingInventoryQuantity");
		indices.add("PurchaseForecast");
		indices.add("InventoryDesired");
		List<GenericValue> facilities = delegator.findList("Facility",
				EntityCondition.makeCondition("ownerPartyId", EntityJoinOperator.EQUALS, organizationId), null, null,
				null, true);
		List<Object> facilityIds = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);

		for (String indice : indices) {
			Map<String, Object> row = FastMap.newInstance();
			row.put("id", indice);
			row.put("indices", UtilProperties.getMessage("PlanUiLabels", indice, locale));
			for (GenericValue x : timePeriod) {
				Object value = null;
				switch (indice) {
				case "OpeningInventoryQuantity":
					Inventory inventory = new Inventory(processor, delegator, x.getDate("fromDate"), facilityIds,
							product);
					value = inventory.getInventoryTotal();
					break;
				case "QuantitySold":
					SalesOrder salesOrder = new SalesOrder(processor, delegator, x.getDate("fromDate"),
							x.getDate("thruDate"), product, organizationId);
					value = salesOrder.getQuantityTotal();
					break;
				case "SalesForecast":
					value = getSalesForecastOfProduct(delegator, organizationId, product, x.getDate("fromDate"),
							x.getDate("thruDate"));
					break;
				case "QuantityOrdered":
					PurchaseOrder purchaseOrder = new PurchaseOrder(processor, delegator, x.getDate("fromDate"),
							x.getDate("thruDate"), product, organizationId);
					value = purchaseOrder.getQuantityTotal();
					break;
				case "EndingInventoryQuantity":
					inventory = new Inventory(processor, delegator, x.getDate("thruDate"), facilityIds, product);
					value = inventory.getInventoryTotal();
					break;
				case "PurchaseForecast":
					if (x.getDate("thruDate").getTime() > System.currentTimeMillis()) {
						value = getProductPlanItem(delegator, productPlanId, x.get("customTimePeriodId"), product);
					}
					break;
				case "InventoryDesired":
					if (x.getDate("thruDate").getTime() > System.currentTimeMillis()) {
						BigDecimal salesForecast = getSalesForecastOfProduct(delegator, organizationId, product,
								x.getDate("fromDate"), x.getDate("thruDate"));
						BigDecimal purchaseForecast = getProductPlanItem(delegator, productPlanId,
								x.get("customTimePeriodId"), product);
						inventory = new Inventory(processor, delegator, x.getDate("fromDate"), facilityIds, product);
						Object inventoryTotal = inventory.getInventoryTotal();
						BigDecimal openingQuantity = BigDecimal.ZERO;
						if (UtilValidate.isNotEmpty(inventoryTotal)) {
							openingQuantity = (BigDecimal) inventoryTotal;
						}
						value = purchaseForecast.subtract(salesForecast).add(openingQuantity);
					}
					break;
				default:
					break;
				}

				row.put(x.getString("customTimePeriodId"), (value == null ? 0 : value));
			}
			localdata.add(row);
		}
		return localdata;
	}

	private static BigDecimal getProductPlanItem(Delegator delegator, Object productPlanId, Object customTimePeriodId,
			Object productId) throws GenericEntityException {
		BigDecimal planQuantity = BigDecimal.ZERO;
		GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId",
				productPlanId, "customTimePeriodId", customTimePeriodId, "productId", productId), false);
		if (UtilValidate.isNotEmpty(productPlanItem)) {
			planQuantity = productPlanItem.getBigDecimal("planQuantity");
		}
		return planQuantity;
	}

	private static BigDecimal getSalesForecastOfProduct(Delegator delegator, Object organizationId, Object productId,
			Date fromDate, Date thruDate) throws GenericEntityException {
		BigDecimal salesForecast = BigDecimal.ZERO;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("organizationPartyId", organizationId, "fromDate", fromDate, "thruDate", thruDate)));
		conditions.add(EntityCondition.makeCondition("periodTypeId", EntityJoinOperator.IN,
				UtilMisc.toList("SALES_WEEK", "SALES_MONTH", "SALES_QUARTER", "SALES_YEAR")));
		List<GenericValue> customTimePeriods = delegator.findList("CustomTimePeriod",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("periodNum"), null, false);
		if (UtilValidate.isNotEmpty(customTimePeriods)) {
			Object customTimePeriodId = EntityUtil.getFirst(customTimePeriods).get("customTimePeriodId");
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("organizationPartyId", organizationId, "customTimePeriodId", customTimePeriodId)));
			List<GenericValue> salesForecasts = delegator.findList("SalesForecast",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(salesForecasts)) {
				Object salesForecastId = EntityUtil.getFirst(salesForecasts).get("salesForecastId");
				conditions.clear();
				conditions.add(EntityCondition
						.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId, "productId", productId)));
				List<GenericValue> salesForecastDetails = delegator.findList("SalesForecastDetail",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : salesForecastDetails) {
					salesForecast = salesForecast.add(x.getBigDecimal("quantity"));
				}
			}
		}
		return salesForecast;
	}

	private static Map<String, Object> getGridConfigSetupPlan(List<GenericValue> timePeriod, Locale locale) {
		Map<String, Object> config = FastMap.newInstance();

		List<Map<String, Object>> datafields = FastList.newInstance();
		List<Map<String, Object>> columns = FastList.newInstance();

		Map<String, Object> datafieldIndices = UtilMisc.toMap("name", "indices", "type", "string");
		datafields.add(datafieldIndices);
		Map<String, Object> datafieldId = UtilMisc.toMap("name", "id", "type", "string");
		datafields.add(datafieldId);

		Map<String, Object> columnIndices = UtilMisc.toMap("text",
				UtilProperties.getMessage("PlanUiLabels", "POIndices", locale), "datafield", "indices", "pinned", true,
				"width", 200, "editable", false);
		columns.add(columnIndices);

		for (GenericValue x : timePeriod) {
			Map<String, Object> datafield = FastMap.newInstance();
			datafield.put("name", x.get("customTimePeriodId"));
			datafield.put("type", "number");
			datafields.add(datafield);

			Map<String, Object> column = FastMap.newInstance();
			column.put("text", x.get("periodName"));
			column.put("datafield", x.get("customTimePeriodId"));
			column.put("width", 150);
			column.put("columntype", "numberinput");
			column.put("editable", true);
			columns.add(column);
		}
		config.put("datafields", datafields);
		config.put("columns", columns);
		return config;
	}

	private static List<GenericValue> getTimePeriodPurchase(Delegator delegator, String periodType, int previous,
			int next) throws GenericEntityException {
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom.set(Calendar.MINUTE, 0);
		calendarFrom.set(Calendar.SECOND, 0);
		calendarFrom.set(Calendar.MILLISECOND, 0);

		Calendar calendarTo = Calendar.getInstance();
		calendarTo.set(Calendar.HOUR_OF_DAY, 0);
		calendarTo.set(Calendar.MINUTE, 0);
		calendarTo.set(Calendar.SECOND, 0);
		calendarTo.set(Calendar.MILLISECOND, 0);

		switch (periodType) {
		case "COMMERCIAL_WEEK":
			calendarFrom.set(Calendar.DAY_OF_WEEK, calendarFrom.getActualMinimum(Calendar.DAY_OF_WEEK));
			calendarTo.set(Calendar.DAY_OF_WEEK, calendarTo.getActualMinimum(Calendar.DAY_OF_WEEK));
			calendarFrom.set(Calendar.WEEK_OF_YEAR, calendarFrom.get(Calendar.WEEK_OF_YEAR) - previous);
			calendarTo.set(Calendar.WEEK_OF_YEAR, calendarTo.get(Calendar.WEEK_OF_YEAR) + next);
			break;
		case "COMMERCIAL_MONTH":
			calendarFrom.set(Calendar.DAY_OF_MONTH, calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendarTo.set(Calendar.DAY_OF_MONTH, calendarTo.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendarFrom.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH) - previous);
			calendarTo.set(Calendar.MONTH, calendarTo.get(Calendar.MONTH) + next);
			break;
		default:
			break;
		}
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				new Date(calendarFrom.getTimeInMillis())));
		conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,
				new Date(calendarTo.getTimeInMillis())));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", periodType)));
		return delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(conditions), null,
				UtilMisc.toList("fromDate"), null, true);
	}

	private static List<GenericValue> getTimePeriodSales(Delegator delegator, String periodType, int previous, int next)
			throws GenericEntityException {
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom.set(Calendar.MINUTE, 0);
		calendarFrom.set(Calendar.SECOND, 0);
		calendarFrom.set(Calendar.MILLISECOND, 0);

		Calendar calendarTo = Calendar.getInstance();
		calendarTo.set(Calendar.HOUR_OF_DAY, 0);
		calendarTo.set(Calendar.MINUTE, 0);
		calendarTo.set(Calendar.SECOND, 0);
		calendarTo.set(Calendar.MILLISECOND, 0);

		switch (periodType) {
		case "COMMERCIAL_WEEK":
			calendarFrom.set(Calendar.DAY_OF_WEEK, calendarFrom.getActualMinimum(Calendar.DAY_OF_WEEK));
			calendarTo.set(Calendar.DAY_OF_WEEK, calendarTo.getActualMinimum(Calendar.DAY_OF_WEEK));
			calendarFrom.set(Calendar.WEEK_OF_YEAR, calendarFrom.get(Calendar.WEEK_OF_YEAR) - previous);
			calendarTo.set(Calendar.WEEK_OF_YEAR, calendarTo.get(Calendar.WEEK_OF_YEAR) + next);
			periodType = "SALES_WEEK";
			break;
		case "COMMERCIAL_MONTH":
			calendarFrom.set(Calendar.DAY_OF_MONTH, calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendarTo.set(Calendar.DAY_OF_MONTH, calendarTo.getActualMinimum(Calendar.DAY_OF_MONTH));
			calendarFrom.set(Calendar.MONTH, calendarFrom.get(Calendar.MONTH) - previous);
			calendarTo.set(Calendar.MONTH, calendarTo.get(Calendar.MONTH) + next);
			periodType = "SALES_MONTH";
			break;
		default:
			break;
		}
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				new Date(calendarFrom.getTimeInMillis())));
		conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO,
				new Date(calendarTo.getTimeInMillis())));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", periodType)));
		return delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(conditions), null,
				UtilMisc.toList("fromDate"), null, true);
	}

	public static Map<String, Object> loadProductPlanItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			Object productPlanId = context.get("productPlanId");
			String periodType = (String) context.get("periodType");
			int previous = (Integer) context.get("previous");
			int next = (Integer) context.get("next");
			Map<String, Object> compressedPackage = FastMap.newInstance();

			List<GenericValue> timePeriod = getTimePeriodPurchase(delegator, periodType, previous, next);

			compressedPackage.putAll(getGridConfigProductPlan(timePeriod, locale));

			compressedPackage.put("localdata", getGridDataProductPlan(delegator, timePeriod, productPlanId));
			result.put("compressedPackage", compressedPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static Map<String, Object> getGridConfigProductPlan(List<GenericValue> timePeriod, Locale locale) {
		Map<String, Object> config = FastMap.newInstance();

		List<Map<String, Object>> datafields = FastList.newInstance();
		List<Map<String, Object>> columns = FastList.newInstance();

		Map<String, Object> datafieldproductId = UtilMisc.toMap("name", "productId", "type", "string");
		datafields.add(datafieldproductId);
		Map<String, Object> datafieldproductCode = UtilMisc.toMap("name", "productCode", "type", "string");
		datafields.add(datafieldproductCode);
		Map<String, Object> datafieldproductName = UtilMisc.toMap("name", "productName", "type", "string");
		datafields.add(datafieldproductName);

		Map<String, Object> columnproductCode = UtilMisc.toMap("text",
				UtilProperties.getMessage("BasePOUiLabels", "POProductId", locale), "datafield", "productCode",
				"pinned", true, "width", 200, "filtertype", "input");
		columns.add(columnproductCode);
		Map<String, Object> columnproductName = UtilMisc.toMap("text",
				UtilProperties.getMessage("BasePOUiLabels", "BPOProductName", locale), "datafield", "productName",
				"pinned", true, "width", 250, "filtertype", "input");
		columns.add(columnproductName);

		for (GenericValue x : timePeriod) {
			Map<String, Object> datafield = FastMap.newInstance();
			datafield.put("name", x.get("customTimePeriodId"));
			datafield.put("type", "number");
			datafields.add(datafield);

			Map<String, Object> column = FastMap.newInstance();
			column.put("text", x.get("periodName"));
			column.put("datafield", x.get("customTimePeriodId"));
			column.put("width", 150);
			column.put("columntype", "numberinput");
			column.put("filtertype", "number");
			columns.add(column);
		}
		config.put("datafields", datafields);
		config.put("columns", columns);
		return config;
	}

	private static List<Map<String, Object>> getGridDataProductPlan(Delegator delegator, List<GenericValue> timePeriod,
			Object productPlanId) throws GenericEntityException {
		List<Map<String, Object>> localdata = FastList.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productPlanId", EntityJoinOperator.EQUALS, productPlanId));
		conditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityJoinOperator.IN,
				EntityUtil.getFieldListFromEntityList(timePeriod, "customTimePeriodId", true)));
		List<GenericValue> productPlanItems = delegator.findList("ProductPlanItem",
				EntityCondition.makeCondition(conditions), null, null, null, true);
		List<Object> productIds = EntityUtil.getFieldListFromEntityList(productPlanItems, "productId", true);
		for (Object productId : productIds) {
			Map<String, Object> row = FastMap.newInstance();
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
			row.put("productId", product.get("productId"));
			row.put("productCode", product.get("productCode"));
			row.put("productName", product.get("productName"));
			for (GenericValue x : timePeriod) {
				GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId",
						productPlanId, "customTimePeriodId", x.get("customTimePeriodId"), "productId", productId),
						true);
				BigDecimal planQuantity = BigDecimal.ZERO;
				if (UtilValidate.isNotEmpty(productPlanItem)) {
					planQuantity = productPlanItem.getBigDecimal("planQuantity");
				}
				row.put(x.getString("customTimePeriodId"), planQuantity);
			}
			localdata.add(row);
		}
		return localdata;
	}

	public static Map<String, Object> createProductPlanHeader(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			GenericValue productPlanHeader = delegator.makeValidValue("ProductPlanHeader", context);
			productPlanHeader.set("productPlanId", delegator.getNextSeqId("ProductPlanHeader"));
			productPlanHeader.set("createByUserLoginId", userLogin.get("userLoginId"));
			productPlanHeader.set("statusId", "PO_PLAN_CREATED");
			productPlanHeader.set("organizationPartyId", organizationId);
			productPlanHeader.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateProductPlanHeader(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue productPlanHeader = delegator.makeValidValue("ProductPlanHeader", context);
			productPlanHeader.set("modifiedByUserLoginId", userLogin.get("userLoginId"));
			productPlanHeader.set("statusId", "PO_PLAN_CREATED");
			productPlanHeader.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
}
