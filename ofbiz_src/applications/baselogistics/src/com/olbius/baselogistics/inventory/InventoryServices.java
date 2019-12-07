package com.olbius.baselogistics.inventory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.acc.utils.ErrorUtils;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.LogisticsServices;
import com.olbius.baselogistics.util.InventoryUtil;
import com.olbius.baselogistics.util.JsonUtil;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.LogisticsProductUtil;
import com.olbius.baselogistics.util.LogisticsUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

public class InventoryServices {

	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsErrorUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInventoryItemDetail(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		List<GenericValue> listIterator = FastList.newInstance();
		String facilityId = null;
		if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		String debt = null;
		if (parameters.get("debt") != null && parameters.get("debt").length > 0) {
			debt = parameters.get("debt")[0];
		}
		List<String> listFacilityIds = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(facilityId)) {
			EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.LIKE, facilityId);
			listAllConditions.add(condFa);
		} else if ((parameters.get("listFacilities") != null && parameters.get("listFacilities").length > 0)){
			String fa = parameters.get("listFacilities")[0];
			if (UtilValidate.isNotEmpty(fa)) {
				List<Map<String, Object>> listMapFacility = FastList.newInstance();
				try {
					listMapFacility = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", fa);
				} catch (ParseException e) {
					e.printStackTrace();
					ServiceUtil.returnError("OLBIUS: JqxWidgetSevices convert Json error! " + e.toString());
				}
				
				if (!listMapFacility.isEmpty()){
					for (Map<String, Object> map : listMapFacility) {
						if (!listFacilityIds.contains((String)map.get("facilityId"))){
							listFacilityIds.add((String)map.get("facilityId"));
						}
					}
				}
			}
		} else {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			listFacilityIds = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin);
		}
		String ownerPartyId = null;
		if (parameters.get("ownerPartyId") != null && parameters.get("ownerPartyId").length > 0) {
			ownerPartyId = parameters.get("ownerPartyId")[0];
		}
		if (UtilValidate.isNotEmpty(ownerPartyId) && UtilValidate.isEmpty(listFacilityIds)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
		}
		if (listSortFields.isEmpty()) {
			listSortFields.add("productId");
		}
		EntityCondition quantityCondQOH1 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		listAllConditions.add(quantityCondQOH1);
		if (!listFacilityIds.isEmpty()){
			EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
			listAllConditions.add(condFa);
		}
		
		if (UtilValidate.isNotEmpty(debt) && "Y".equals(debt)) {
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("INV_DEBT_SUPPLIER", "INV_DEBT_CUSTOMER")));
		}
		
		listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "InventoryItemGroupByDateDetail", 
				EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts); 
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetPhysicalInventoryDetail(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String facilityId = null;
		if (parameters.get("facilityId") != null
				&& parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		if (facilityId != null && !"".equals(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId",
					EntityOperator.EQUALS, facilityId));
		} else {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Boolean isDis = SalesPartyUtil.isDistributor(delegator,
					userLogin.getString("partyId"));
			List<GenericValue> listFas = new ArrayList<GenericValue>();
			if (isDis) {
				listFas = delegator.findList("Facility", EntityCondition
						.makeCondition(UtilMisc.toMap("ownerPartyId",
								userLogin.getString("partyId"))), null, null,
						null, false);
			} else {
				String orgPartyId = MultiOrganizationUtil
						.getCurrentOrganization(delegator,
								userLogin.getString("userLoginId"));
				listFas = delegator.findList("Facility", EntityCondition
						.makeCondition(UtilMisc.toMap("ownerPartyId",
								orgPartyId)), null, null, null, false);
			}

			List<String> listFaIds = new ArrayList<String>();
			for (GenericValue item : listFas) {
				listFaIds.add(item.getString("facilityId"));
			}
			listAllConditions.add(EntityCondition.makeCondition("facilityId",
					EntityOperator.IN, listFaIds));
		}
		if (listSortFields.isEmpty()) {
			listSortFields.add("-physicalInventoryId");
		}
		List<GenericValue> listPhysicalInvs = new ArrayList<GenericValue>();
		try {
			listPhysicalInvs = delegator.findList("PhysicalInventoryDetail",
					EntityCondition.makeCondition(listAllConditions,
							EntityOperator.AND), null, listSortFields, opts,
					false);
		} catch (GenericEntityException e) {
			return ServiceUtil
					.returnError("OLBIUS: get list physical inventory error");
		}
		successResult.put("listIterator", listPhysicalInvs);
		return successResult;
	}

	public static Map<String, Object> getInventoryItemPhysicalDetail(
			DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listConds = new ArrayList<EntityCondition>();
		String facilityId = (String) context.get("facilityId");
		if (UtilValidate.isNotEmpty(facilityId)) {
			listConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		List<GenericValue> listInvs = new ArrayList<GenericValue>();
		List<String> orderBys = new ArrayList<String>();
		orderBys.add("-quantityOnHandTotal"); 
		try {
			listInvs = delegator.findList("InventoryItemGroupByExpireAndManufacturedDateAll", EntityCondition.makeCondition(listConds, EntityOperator.AND),
					null, orderBys, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get list inventory item to create physical inventory error");
		}
		successResult.put("listInventoryItems", listInvs);
		return successResult;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetInventoryItemPhysicalDetail(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String facilityId = null;
		if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		if (facilityId != null && !"".equals(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		String ownerPartyId = null;
		if (parameters.get("ownerPartyId") != null && parameters.get("ownerPartyId").length > 0) {
			ownerPartyId = parameters.get("ownerPartyId")[0];
		}
		if (ownerPartyId != null && !"".equals(ownerPartyId)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
		}
		if (listSortFields.isEmpty()) {
			listSortFields.add("productId");
		}
		List<GenericValue> listInvs = new ArrayList<GenericValue>();
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("InventoryItemGroupByExpireAndManufacturedDate", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get list inventory item to create physical inventory error");
		}
		listInvs = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		List<Map<String, Object>> listResults = new ArrayList<Map<String, Object>>();
		if (!listInvs.isEmpty()) {
			for (GenericValue inv : listInvs) {
				List<Map<String, Object>> listToResult = FastList.newInstance();
				String productId = inv.getString("productId");
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				List<String> listUoms = LogisticsProductUtil.getProductPackingUoms(delegator, productId);
				List<GenericValue> listReasons = delegator.findList("VarianceReason", null, null, null, null, false);
				for (GenericValue reason : listReasons) {
					Map<String, Object> mapTmp = FastMap.newInstance();
					mapTmp.put("productId", productId);
					mapTmp.put("productCode", product.getString("productCode"));
					mapTmp.put("productName", product.getString("productName"));
					mapTmp.put("quantityUomId", product.getString("quantityUomId"));
					mapTmp.put("weightUomId", product.getString("weightUomId"));
					mapTmp.put("requireAmount", product.getString("requireAmount"));
					mapTmp.put("expireDate", inv.getString("expireDate"));
					mapTmp.put("datetimeReceived", inv.getString("datetimeReceived"));
					mapTmp.put("datetimeManufactured", inv.getString("datetimeManufactured"));
					mapTmp.put("quantityOnHandTotal", inv.getString("quantityOnHandTotal"));
					mapTmp.put("amountOnHandTotal", inv.getString("quantityOnHandTotal"));
					mapTmp.put("availabelToPromiseTotal", inv.getString("availabelToPromiseTotal"));
					mapTmp.put("lotId", inv.getString("lotId"));
					mapTmp.put("varianceReasonId", reason.getString("varianceReasonId"));
					mapTmp.put("description", reason.getString("description"));
					mapTmp.put("packingUomIds", listUoms);
					mapTmp.put("statusId", inv.getString("statusId"));
					mapTmp.put("ownerPartyId", inv.getString("ownerPartyId"));
					mapTmp.put("facilityId", inv.getString("facilityId"));
					listToResult.add(mapTmp);
				}
				Map<String, Object> row = new HashMap<String, Object>();
				row.putAll(inv);
				
				row.put("initExpireDate", inv.get("expireDate"));
				row.put("initDatetimeManufactured", inv.get("datetimeManufactured"));
				row.put("initLotId", inv.get("lotId"));
				row.put("initStatusId", inv.get("statusId"));
				
				row.put("rowDetail", listToResult);
				listResults.add(row);
			}
		}
		successResult.put("listIterator", listResults);
		return successResult;
	}

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> getInventoryItemAndProduct(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listInventoryProduct = new ArrayList<Map<String, Object>>();

		try {
			Map<String, Object> mapReturn = dispatcher.runSync(
					"getInventoryItemDetail", context);
			List<GenericValue> listInventoryGroupByProduct = (List<GenericValue>) mapReturn
					.get("listIterator");
			if (!listInventoryGroupByProduct.isEmpty()) {
				for (GenericValue inv : listInventoryGroupByProduct) {
					Map<String, Object> item = FastMap.newInstance();
					item = inv.getAllFields();
					String productId = inv.getString("productId");
					if (UtilValidate.isNotEmpty(productId)) {
						// list product packing uom
						List<String> listQuantityUoms = LogisticsProductUtil
								.getProductPackingUoms(delegator, productId);
						item.put("listQuantityUoms", listQuantityUoms);
						// list inventory item by product
						List<String> listInvIds = new ArrayList<String>();
						List<GenericValue> listInventoryByProduct = delegator
								.findList(
										"InventoryItem",
										EntityCondition.makeCondition(UtilMisc.toMap(
												"productId",
												inv.get("productId"),
												"expireDate",
												inv.get("expireDate"),
												"datetimeManufactured",
												inv.get("datetimeManufactured"),
												"datetimeReceived",
												inv.get("datetimeReceived"),
												"lotId", inv.get("lotId"),
												"facilityId",
												inv.get("facilityId"),
												"statusId",
												inv.get("statusId"),
												"ownerPartyId",
												inv.get("ownerPartyId"))),
										null, null, null, false);
						if (!listInventoryByProduct.isEmpty()) {
							for (GenericValue invTmp : listInventoryByProduct) {
								if (UtilValidate.isNotEmpty(invTmp
										.getBigDecimal("quantityOnHandTotal"))
										&& invTmp.getBigDecimal(
												"quantityOnHandTotal")
												.compareTo(BigDecimal.ZERO) > 0) {
									listInvIds.add(invTmp
											.getString("inventoryItemId"));
								}
							}
						}
						item.put("listInventoryItemIds", listInvIds);
					}
					listInventoryProduct.add(item);
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		successResult.put("listIterator", listInventoryProduct);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInventory(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String facilityId = null;
		if (parameters.get("facilityId") != null && parameters.get("facilityId")[0].length() > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		List<String> listFacilityIds = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(facilityId)) {
			EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.LIKE, facilityId);
			listAllConditions.add(condFa);
		} else if ((parameters.get("listFacilities") != null && parameters.get("listFacilities")[0].length() > 0)){
			String fa = parameters.get("listFacilities")[0];
			if (UtilValidate.isNotEmpty(fa)) {
				List<Map<String, Object>> listMapFacility = FastList.newInstance();
				try {
					listMapFacility = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", fa);
				} catch (ParseException e) {
					e.printStackTrace();
					ServiceUtil.returnError("OLBIUS: JqxWidgetSevices convert Json error! " + e.toString());
				}
				
				if (!listMapFacility.isEmpty()){
					for (Map<String, Object> map : listMapFacility) {
						if (!listFacilityIds.contains((String)map.get("facilityId"))){
							listFacilityIds.add((String)map.get("facilityId"));
						}
					}
				}
			}
		} else {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Security security = ctx.getSecurity();
			
			String deposit = null;
			if (parameters.get("deposit") != null && parameters.get("deposit").length > 0) {
				deposit = parameters.get("deposit")[0];
			}
			if (UtilValidate.isNotEmpty(deposit) && "Y".equals(deposit)){
				listFacilityIds = LogisticsFacilityUtil.getFacilityDepositAllowedView(delegator, userLogin);
			} else {
				listFacilityIds = LogisticsFacilityUtil.getFacilityAllowedView(delegator, userLogin, security);
			}
		}
		String ownerPartyId = null;
		if (parameters.get("ownerPartyId") != null && parameters.get("ownerPartyId").length > 0) {
			ownerPartyId = parameters.get("ownerPartyId")[0];
		}
		if (UtilValidate.isNotEmpty(ownerPartyId) && UtilValidate.isEmpty(listFacilityIds)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
		}
		if (listSortFields.isEmpty()) {
			listSortFields = UtilMisc.toList("productId", "facilityName");
		}

		List<Map<String, Object>> listInventoryItems = FastList.newInstance();
		
		if (!listFacilityIds.isEmpty()){
			EntityCondition condFa = EntityCondition.makeCondition("facilityId", EntityOperator.IN, listFacilityIds);
			listAllConditions.add(condFa);
		} else {
			if (UtilValidate.isEmpty(facilityId)) {
				successResult.put("listIterator", listInventoryItems);
				return successResult;
			}
		}
		
		List<GenericValue> listInventoryItemTotal = new ArrayList<GenericValue>();
			listInventoryItemTotal = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "InventoryItemTotalDetailAndSupplier", 
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);

		if (UtilValidate.isNotEmpty(listInventoryItemTotal)) {
			for (GenericValue inventoryItemTotal : listInventoryItemTotal) {
				String productIdTotal = inventoryItemTotal.getString("productId");
				String facilityIdTotal = inventoryItemTotal.getString("facilityId");
				Map<String, Object> row = new HashMap<String, Object>();
				List<GenericValue> listRowDetails = FastList.newInstance();
				row.putAll(inventoryItemTotal);
				String primaryUPC =  null;
				List<Map<String, Object>> listUPCs = FastList.newInstance();
				List<GenericValue> listGoodIdentification = delegator.findList("GoodIdentification",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productIdTotal)), null, null, null, false);
				if (!listGoodIdentification.isEmpty()){
					for (GenericValue item : listGoodIdentification) {
						if (UtilValidate.isNotEmpty(item.get("iupprm"))) {
							if ((Long)item.get("iupprm") == 1){
								primaryUPC = listGoodIdentification.get(0).getString("idValue");
							}
						}
						Map<String, Object> map = FastMap.newInstance();
						map.put("idValue", item.get("idValue"));
						listUPCs.add(map);
					}
					row.put("primaryUPC", primaryUPC);
					row.put("listUPCs", listUPCs);
				}
				List<GenericValue> listInventoryItem = new ArrayList<GenericValue>();
				List<EntityCondition> listConds = new ArrayList<EntityCondition>();
				listConds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productIdTotal));
				listConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityIdTotal));
				listInventoryItem = delegator.findList("InventoryItemGroupByDate", EntityCondition.makeCondition(listConds), null, null, null, false);
				
				for (GenericValue inventoryItem : listInventoryItem) {
					listRowDetails.add(inventoryItem);
				}
				List<Map<String, Object>> listToResult = FastList.newInstance();
				for (GenericValue inv : listRowDetails) {
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					if (UtilValidate.isEmpty(qoh)) {
						continue;
						//return ServiceUtil.returnError("InventoryItem wrong! qoh not found!");
					}
					if (qoh.compareTo(BigDecimal.ZERO) > 0) {
						Map<String, Object> mapTmp = FastMap.newInstance();
//						mapTmp.put("inventoryItemId", inv.getString("inventoryItemId"));
						mapTmp.put("productId", inv.getString("productId"));
						mapTmp.put("productCode", inv.getString("productCode"));
						mapTmp.put("requireAmount", inv.getString("requireAmount"));
						mapTmp.put("amountUomTypeId", inv.getString("amountUomTypeId"));
//						mapTmp.put("weightUomId", inv.getString("weightUomId"));
						mapTmp.put("expireDate", inv.getTimestamp("expireDate"));
						mapTmp.put("datetimeManufactured", inv.getTimestamp("datetimeManufactured"));
						mapTmp.put("datetimeReceived", inv.getTimestamp("datetimeReceived"));
						mapTmp.put("facilityId", inv.getString("facilityId"));
						mapTmp.put("facilityCode", inv.getString("facilityCode"));
//						mapTmp.put("internalName", inv.getString("internalName"));
						mapTmp.put("productName", inv.getString("productName"));
						mapTmp.put("quantityOnHandTotal", inv.getBigDecimal("quantityOnHandTotal"));
						mapTmp.put("amountOnHandTotal", inv.getBigDecimal("amountOnHandTotal"));
						mapTmp.put("availableToPromiseTotal", inv.getBigDecimal("availableToPromiseTotal"));
						mapTmp.put("quantityUomId", inv.getString("quantityUomId"));
						mapTmp.put("statusId", inv.getString("statusId"));
						mapTmp.put("lotId", inv.getString("lotId"));
//						mapTmp.put("lastUpdatedStamp", inv.get("lastUpdatedStamp"));
						listToResult.add(mapTmp);
					}
				}
				row.put("rowDetail", listToResult);
				listInventoryItems.add(row);
			}
		}
		
		successResult.put("listIterator", listInventoryItems);
		return successResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getAvailableINV(DispatchContext ctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String orderId = (String) parameters.get("orderId")[0];
		// 1. get list of Facility of current Org (org of this order)

		List<GenericValue> orderRoleFroms = delegator.findList("OrderRole",
				EntityCondition.makeCondition(UtilMisc.toMap("orderId",
						orderId, "roleTypeId", "BILL_FROM_VENDOR")), null,
				null, null, false);
		String orgId = orderRoleFroms.get(0).getString("partyId");
		List<GenericValue> listFacility = null;
		GenericValue order = delegator.findOne("OrderHeader", false,
				UtilMisc.toMap("orderId", orderId));
		String productStoreId = order.getString("productStoreId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");
		try {
			listFacility = delegator.findList("Facility", EntityCondition
					.makeCondition(UtilMisc.toMap("ownerPartyId", orgId,
							"facilityTypeId", "WAREHOUSE")), null, null, null,
					false);
			List<GenericValue> listTmp = new ArrayList<GenericValue>();
			for (GenericValue fac : listFacility) {
				String storeId = fac.getString("productStoreId");
				if (storeId == null || !storeId.equals(productStoreId)) {
					List<GenericValue> listStoreFac = delegator.findList(
							"ProductStoreFacility", EntityCondition
									.makeCondition(UtilMisc.toMap("facilityId",
											fac.getString("facilityId"),
											"productStoreId", productStoreId)),
							null, null, null, false);
					listStoreFac = EntityUtil.filterByDate(listStoreFac);
					if (listStoreFac.isEmpty()) {
						listTmp.add(fac);
						continue;
					}
				}
				String facilityId = fac.getString("facilityId");
				List<GenericValue> listFacilityParty = delegator.findList(
						"FacilityParty", EntityCondition.makeCondition(UtilMisc
								.toMap("facilityId", facilityId, "partyId",
										partyId, "roleTypeId", UtilProperties
												.getPropertyValue(
														LOGISTICS_PROPERTIES,
														"roleType.manager"))),
						null, null, null, false);
				listFacilityParty = EntityUtil.filterByDate(listFacilityParty);
				if (listFacilityParty.isEmpty()) {
					listTmp.add(fac);
					continue;
				}
			}
			listFacility.removeAll(listTmp);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil
					.returnError("Service getAvailableFacilityToExport:"
							+ e.toString());
		}
		// 2. get list of orderItem
		List<GenericValue> listOrderItem = null;
		try {
			listOrderItem = LogisticsProductUtil.getOrderItemRemains(delegator,
					orderId, "WAREHOUSE");
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError("Service getAvailableINV:"
					+ e.toString());
		}
		List<GenericValue> listData = new ArrayList<GenericValue>();
		List<GenericValue> listTmpData = new ArrayList<GenericValue>();
		// FIXME check for amount of created delivery
		// 3. get list of INV by orderItem and facility
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		List<EntityCondition> listCond = (List<EntityCondition>) context
				.get("listAllConditions");
		if (listCond != null && !listCond.isEmpty()) {
			L0: for (int j = 0; j < listOrderItem.size(); j++) {
				try {
					listTmpData = delegator.findList("SumATPByProductAndEXP",
							EntityCondition.makeCondition(listCond,
									EntityJoinOperator.AND), null,
							listSortFields, null, false);
				} catch (GenericEntityException e) {
					Debug.log(e.getStackTrace().toString(), module);
					return ServiceUtil.returnError("Service getAvailableINV:"
							+ e.toString());
				}
				for (int k = 0; k < listTmpData.size(); k++) {
					if (listTmpData
							.get(k)
							.getBigDecimal("qoh")
							.compareTo(
									listOrderItem.get(j).getBigDecimal(
											"quantity")) >= 0) {
						continue L0;
					}
				}
				break;
			}
			if (listTmpData != null && !listTmpData.isEmpty()) {
				for (int k = 0; k < listTmpData.size(); k++) {
					for (int i = 0; i < listFacility.size(); i++) {
						if (listFacility
								.get(i)
								.getString("facilityId")
								.equals(listTmpData.get(k).getString(
										"facilityId"))) {
							listData.add(listFacility.get(i));
						}
					}
				}
			}
			// remove duplicate value
			HashSet hs = new HashSet();
			hs.addAll(listData);
			listData.clear();
			listData.addAll(hs);
		} else {
			L1: for (int i = 0; i < listFacility.size(); i++) {
				L2: for (int j = 0; j < listOrderItem.size(); j++) {
					listCond = new ArrayList<EntityCondition>();
					listCond.add(EntityCondition.makeCondition("productId",
							listOrderItem.get(j).getString("productId")));
					listCond.add(EntityCondition.makeCondition("facilityId",
							listFacility.get(i).getString("facilityId")));
					try {
						listTmpData = delegator.findList(
								"SumATPByProductAndEXP", EntityCondition
										.makeCondition(listCond,
												EntityJoinOperator.AND), null,
								listSortFields, null, false);
					} catch (GenericEntityException e) {
						Debug.log(e.getStackTrace().toString(), module);
						return ServiceUtil
								.returnError("Service getAvailableINV:"
										+ e.toString());
					}
					for (int k = 0; k < listTmpData.size(); k++) {
						if (listTmpData
								.get(k)
								.getBigDecimal("qoh")
								.compareTo(
										listOrderItem.get(j).getBigDecimal(
												"quantity")) >= 0) {
							continue L2;
						}
					}
					continue L1;
				}
				listData.add(listFacility.get(i));
			}
		}
		// 4. return data
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("listIterator", listData);
		return result;
	}

	public static Map<String, Object> checkInventoryAvailable(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String productId = (String) context.get("productId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		String productStoreId = (String) context.get("productStoreId");
		String viewATPForAll = (String) context.get("viewATPForAll");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orgPartyId = null;
		String partyId = userLogin.getString("partyId");
		List<GenericValue> listRelations = delegator.findList(
				"PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo",
						partyId, "roleTypeIdTo", "EMPLOYEE")), null, null,
				null, false);
		listRelations = EntityUtil.filterByDate(listRelations);
		if (!listRelations.isEmpty()) {
			orgPartyId = MultiOrganizationUtil.getCurrentOrganization(
					delegator, userLogin.getString("userLoginId"));
		} else {
			orgPartyId = partyId;
		}
		Map<String, Object> mapNonUom = LogisticsProductUtil
				.checkInventoryAvailable(delegator, productId, quantity,
						viewATPForAll, orgPartyId, productStoreId);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("isOK", (Boolean) mapNonUom.get("isOK"));
		mapReturn.put("availableToPromiseTotal",
				(BigDecimal) mapNonUom.get("availableToPromiseTotal"));
		return mapReturn;
	}

	public static Map<String, Object> checkInventoryAvailableWithUom(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String productId = (String) context.get("productId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		String productStoreId = (String) context.get("productStoreId");
		String uomId = (String) context.get("uomId");
		String viewATPForAll = (String) context.get("viewATPForAll");
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orgPartyId = null;
		String partyId = userLogin.getString("partyId");
		List<GenericValue> listRelations = delegator.findList(
				"PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo",
						partyId, "roleTypeIdTo", "EMPLOYEE")), null, null,
				null, false);
		listRelations = EntityUtil.filterByDate(listRelations);
		if (!listRelations.isEmpty()) {
			orgPartyId = MultiOrganizationUtil.getCurrentOrganization(
					delegator, userLogin.getString("userLoginId"));
		} else {
			orgPartyId = partyId;
		}

		Map<String, Object> mapUom = LogisticsProductUtil
				.checkInventoryAvailableWithUom(delegator, productId, quantity,
						uomId, viewATPForAll, orgPartyId, productStoreId);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("isOK", (Boolean) mapUom.get("isOK"));
		mapReturn.put("availableToPromiseTotal",
				(BigDecimal) mapUom.get("availableToPromiseTotal"));
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkInventoryAvailableList(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String productStoreId = (String) context.get("productStoreId");
		String viewATPForAll = (String) context.get("viewATPForAll");
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("uomId", item.getString("quantityUomId"));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context
					.get("listProducts");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orgPartyId = null;
		String partyId = userLogin.getString("partyId");
		List<GenericValue> listRelations = delegator.findList(
				"PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo",
						partyId, "roleTypeIdTo", "EMPLOYEE")), null, null,
				null, false);
		listRelations = EntityUtil.filterByDate(listRelations);
		if (!listRelations.isEmpty()) {
			orgPartyId = MultiOrganizationUtil.getCurrentOrganization(
					delegator, userLogin.getString("userLoginId"));
		} else {
			orgPartyId = partyId;
		}
		List<Map<String, Object>> listProductChecks = LogisticsProductUtil
				.checkInventoryAvailableList(delegator, listProducts,
						viewATPForAll, orgPartyId, productStoreId);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("listProductChecks", listProductChecks);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkInventoryAvailableFacility(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String) context.get("facilityId");
		String viewATPForAll = (String) context.get("viewATPForAll");
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String)
				isJson = true;
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("uomId", item.getString("quantityUomId"));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context
					.get("listProducts");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String orgPartyId = null;
		String partyId = userLogin.getString("partyId");
		List<GenericValue> listRelations = delegator.findList(
				"PartyRelationship",
				EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo",
						partyId, "roleTypeIdTo", "EMPLOYEE")), null, null,
				null, false);
		listRelations = EntityUtil.filterByDate(listRelations);
		if (!listRelations.isEmpty()) {
			orgPartyId = MultiOrganizationUtil.getCurrentOrganization(
					delegator, userLogin.getString("userLoginId"));
		} else {
			orgPartyId = partyId;
		}
		List<Map<String, Object>> listProductChecks = LogisticsProductUtil
				.checkInventoryAvailableFacility(delegator, listProducts,
						viewATPForAll, orgPartyId, facilityId);
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("listProductChecks", listProductChecks);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListWarehouseReport(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		/*
		 * List<GenericValue> listData =
		 * delegator.findList("DataOlapImportExportEntity",
		 * EntityCondition.makeCondition(UtilMisc.toMap("inventoryType",
		 * "RECEIVE")), null, null, null, false); List<GenericValue>
		 * listDataSoure = new ArrayList<GenericValue>(); for (GenericValue list
		 * : listData) { String inventoryItemDetailSeqId =
		 * list.getString("inventoryItemDetailSeqId"); String inventoryItemId =
		 * list.getString("inventoryItemId"); GenericValue inventoryItemDetail =
		 * delegator.findOne("InventoryItemDetail",
		 * UtilMisc.toMap("inventoryItemId", inventoryItemId,
		 * "inventoryItemDetailSeqId", inventoryItemDetailSeqId), false);
		 * if(inventoryItemDetail != null){ BigDecimal quantityOnHandDiff =
		 * inventoryItemDetail.getBigDecimal("quantityOnHandDiff"); String aaaa
		 * = quantityOnHandDiff.toString(); list.setString("quantityOnHandDiff",
		 * aaaa); list.put("quantityOnHandDiff", quantityOnHandDiff);
		 * listDataSoure.add(list); } }
		 */
		try {
			listAllConditions.add(EntityCondition.makeCondition(
					"inventoryType", EntityOperator.EQUALS, "RECEIVE"));
			EntityCondition cond = EntityCondition.makeCondition(
					listAllConditions, EntityOperator.AND);
			listIterator = delegator.find("DataOlapImportExportEntity", cond,
					null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getShipments service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> exportProductFromServiceFacility(
			DispatchContext ctx, Map<String, ?> context)
			throws GenericEntityException, GenericServiceException {
		List<Object> listItemTmp = (List<Object>) context.get("listProducts");
		String facilityId = (String) context.get("facilityId");
		Boolean isJson = false;
		if (!listItemTmp.isEmpty()) {
			if (listItemTmp.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listProductItems = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listItemTmp.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId",
							item.getString("quantityUomId"));
				}
				listProductItems.add(mapItems);
			}
		} else {
			listProductItems = (List<Map<String, String>>) context
					.get("listProducts");
		}

		Delegator delegator = ctx.getDelegator();
		if (!listProductItems.isEmpty()) {
			for (Map<String, String> item : listProductItems) {
				String productId = item.get("productId");
				BigDecimal quantity = new BigDecimal(item.get("quantity"));
				List<String> orderByQty = new ArrayList<String>();
				orderByQty.add("quantityOnHandTotal");
				List<GenericValue> listInvs = delegator.findList(
						"InventoryItem", EntityCondition.makeCondition(UtilMisc
								.toMap("productId", productId, "facilityId",
										facilityId)), null, orderByQty, null,
						false);
				if (!listInvs.isEmpty()) {
					GenericValue product = delegator.findOne("Product", false,
							UtilMisc.toMap("productId", item.get("productId")));
					BigDecimal convert = LogisticsProductUtil
							.getConvertPackingNumber(delegator, productId,
									item.get("quantityUomId"),
									product.getString("quantityUomId"));
					BigDecimal remainQty = convert.multiply(quantity);
					for (GenericValue inv : listInvs) {
						BigDecimal invQty = inv
								.getBigDecimal("quantityOnHandTotal");
						if (invQty.compareTo(remainQty) > 0) {
							GenericValue tmpInvDetail = delegator
									.makeValue("InventoryItemDetail");
							tmpInvDetail.set("inventoryItemId",
									inv.getString("inventoryItemId"));
							tmpInvDetail
									.set("inventoryItemDetailSeqId",
											delegator
													.getNextSeqId("InventoryItemDetail"));
							tmpInvDetail.set("effectiveDate",
									UtilDateTime.nowTimestamp());
							tmpInvDetail.set("quantityOnHandDiff",
									BigDecimal.ZERO);
							tmpInvDetail.set("availableToPromiseDiff", invQty
									.subtract(remainQty).negate());
							tmpInvDetail.set("accountingQuantityDiff",
									BigDecimal.ZERO);
							tmpInvDetail.create();

							GenericValue tmpInvDetail2 = delegator
									.makeValue("InventoryItemDetail");
							tmpInvDetail2.set("inventoryItemId",
									inv.getString("inventoryItemId"));
							tmpInvDetail2
									.set("inventoryItemDetailSeqId",
											delegator
													.getNextSeqId("InventoryItemDetail"));
							tmpInvDetail2.set("effectiveDate",
									UtilDateTime.nowTimestamp());
							tmpInvDetail2.set("quantityOnHandDiff", invQty
									.subtract(remainQty).negate());
							tmpInvDetail2.set("availableToPromiseDiff",
									BigDecimal.ZERO);
							tmpInvDetail2.set("accountingQuantityDiff",
									BigDecimal.ZERO);
							tmpInvDetail2.create();
						} else {
							GenericValue tmpInvDetail = delegator
									.makeValue("InventoryItemDetail");
							tmpInvDetail.set("inventoryItemId",
									inv.getString("inventoryItemId"));
							tmpInvDetail
									.set("inventoryItemDetailSeqId",
											delegator
													.getNextSeqId("InventoryItemDetail"));
							tmpInvDetail.set("effectiveDate",
									UtilDateTime.nowTimestamp());
							tmpInvDetail.set("quantityOnHandDiff",
									BigDecimal.ZERO);
							tmpInvDetail.set("availableToPromiseDiff",
									invQty.negate());
							tmpInvDetail.set("accountingQuantityDiff",
									BigDecimal.ZERO);
							tmpInvDetail.create();

							GenericValue tmpInvDetail2 = delegator
									.makeValue("InventoryItemDetail");
							tmpInvDetail2.set("inventoryItemId",
									inv.getString("inventoryItemId"));
							tmpInvDetail2
									.set("inventoryItemDetailSeqId",
											delegator
													.getNextSeqId("InventoryItemDetail"));
							tmpInvDetail2.set("effectiveDate",
									UtilDateTime.nowTimestamp());
							tmpInvDetail2.set("quantityOnHandDiff",
									invQty.negate());
							tmpInvDetail2.set("availableToPromiseDiff",
									BigDecimal.ZERO);
							tmpInvDetail2.set("accountingQuantityDiff",
									BigDecimal.ZERO);
							tmpInvDetail2.create();
							remainQty = remainQty.subtract(invQty);
						}
					}
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		return mapReturn;
	}

	public static Map<String, Object> getInventoryByProduct(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		String productId = (String) context.get("productId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(
				delegator, userLogin.getString("userLoginId"));
		String agentPartyId = (String) context.get("partyId");
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		if (agentPartyId != null) {
			List<GenericValue> listAgent = delegator.findList(
					"PartyRelationship", EntityCondition.makeCondition(UtilMisc
							.toMap("partyIdFrom", agentPartyId, "partyIdTo",
									company, "roleTypeIdFrom",
									"ACTIVATION_AGENT",
									"partyRelationshipTypeId",
									"ACTIVATION_AGENT")), null, null, null,
					false);
			if (!listAgent.isEmpty()) {
				EntityCondition agent = EntityCondition
						.makeCondition("ownerPartyId",
								EntityJoinOperator.EQUALS, agentPartyId);
				listAllConditions.add(agent);
			} else {
				successResult.put("listInventoryItems", listInventoryItems);
				return successResult;
			}
		} else {
			List<GenericValue> listAgent = delegator.findList(
					"PartyRelationship", EntityCondition.makeCondition(UtilMisc
							.toMap("partyIdTo", company, "roleTypeIdFrom",
									"ACTIVATION_AGENT",
									"partyRelationshipTypeId",
									"ACTIVATION_AGENT")), null, null, null,
					false);
			if (!listAgent.isEmpty()) {
				List<String> listAgentPartyIds = new ArrayList<String>();
				for (GenericValue item : listAgent) {
					listAgentPartyIds.add(item.getString("partyIdFrom"));
				}
				EntityCondition agent = EntityCondition.makeCondition(
						"ownerPartyId", EntityOperator.IN, listAgentPartyIds);
				listAllConditions.add(agent);
			}
		}
		EntityCondition productCond = EntityCondition.makeCondition(
				"productId", EntityJoinOperator.EQUALS, productId);
		listAllConditions.add(productCond);

		try {
			EntityListIterator listIterator = null;
			listIterator = delegator.find("InventoryItem", EntityCondition
					.makeCondition(listAllConditions, EntityJoinOperator.AND),
					null, null, null, null);
			listInventoryItems = listIterator.getCompleteList();
			listIterator.close();
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getFacilities service: "
					+ e.toString();
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listInventoryItems", listInventoryItems);
		return successResult;
	}

	public static Map<String, Object> getDetailQuantityInventory(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("originFacilityId");
		Timestamp expireDate = (Timestamp) context.get("expireDate");
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal amountOnHandTotal = BigDecimal.ZERO;
		BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			String ownerPartyId = facility.getString("ownerPartyId");
			if (UtilValidate.isNotEmpty(expireDate)) {
				List<GenericValue> listInventoryItem = delegator.findList("GroupProductInventory", EntityCondition.makeCondition(UtilMisc.toMap("facilityId",
										facilityId, "productId", productId, "expireDate", expireDate, "ownerPartyId", ownerPartyId)), null, null, null, false);
				if (!listInventoryItem.isEmpty()) {
					quantityOnHandTotal = listInventoryItem.get(0).getBigDecimal("QOH");
					availableToPromiseTotal = listInventoryItem.get(0).getBigDecimal("ATP");
					amountOnHandTotal = listInventoryItem.get(0).getBigDecimal("AOH");
				}
			} else {
				List<GenericValue> listInventoryItem = delegator.findList("InventoryItemGroupByProductAndFacility", EntityCondition.makeCondition(UtilMisc
								.toMap("facilityId", facilityId, "productId", productId, "ownerPartyId", ownerPartyId)), null, null, null, false);
				if (!listInventoryItem.isEmpty()) {
					for (GenericValue inv : listInventoryItem) {
						quantityOnHandTotal = quantityOnHandTotal.add(inv.getBigDecimal("quantityOnHandTotal"));
						if (UtilValidate.isNotEmpty(inv.getBigDecimal("amountOnHandTotal"))) {
							amountOnHandTotal = amountOnHandTotal.add(inv.getBigDecimal("amountOnHandTotal"));
						}
						availableToPromiseTotal = availableToPromiseTotal.add(inv.getBigDecimal("availableToPromiseTotal"));
					}
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("getDetailQuantityInventory error" + e.toString());
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("availableToPromiseTotal", availableToPromiseTotal);
		mapReturn.put("amountOnHandTotal", amountOnHandTotal);
		mapReturn.put("quantityOnHandTotal", quantityOnHandTotal);
		return mapReturn;
	}

	public static Map<String, Object> createPhysicalInventoryAndVariance(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String comments = (String) context.get("comments");
		String inventoryItemId = (String) context.get("inventoryItemId");
		String partyId = (String) context.get("partyId");
		String quantityOnHandVarStr = (String) context.get("quantityOnHandVar");
		BigDecimal quantityOnHandVar = new BigDecimal(quantityOnHandVarStr);
		String varianceReasonId = (String) context.get("varianceReasonId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> contextInput = UtilMisc.toMap("userLogin",
				userLogin, "partyId", partyId, "inventoryItemId",
				inventoryItemId, "comments", comments, "varianceReasonId",
				varianceReasonId, "quantityOnHandVar", quantityOnHandVar);
		try {
			dispatcher.runAsync("createPhysicalInventoryAndVariance",
					contextInput);
		} catch (ServiceAuthException e) {
			e.printStackTrace();
		} catch (ServiceValidationException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductTotal(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			List<EntityCondition> orConditionList = new ArrayList<EntityCondition>();
			List<EntityCondition> mainConditionList = new ArrayList<EntityCondition>();
			// do not include configurable products
			conditionList.add(EntityCondition.makeCondition("productTypeId",
					EntityOperator.NOT_EQUAL, "AGGREGATED"));
			conditionList.add(EntityCondition.makeCondition("productTypeId",
					EntityOperator.NOT_EQUAL, "AGGREGATED_SERVICE"));
			conditionList.add(EntityCondition.makeCondition("productTypeId",
					EntityOperator.NOT_EQUAL, "AGGR_DIGSERV"));
			EntityCondition conditions = EntityCondition.makeCondition(
					conditionList, EntityOperator.AND);
			// no virtual products: note that isVirtual could be null,
			// we consider those products to be non-virtual and hence addable to
			// the order in bulk
			orConditionList.add(EntityCondition.makeCondition("isVirtual",
					EntityOperator.EQUALS, "N"));
			orConditionList.add(EntityCondition.makeCondition("isVirtual",
					EntityOperator.EQUALS, null));
			EntityCondition orConditions = EntityCondition.makeCondition(
					orConditionList, EntityOperator.OR);
			mainConditionList.add(orConditions);
			mainConditionList.add(conditions);
			listAllConditions.addAll(mainConditionList);

			EntityCondition tmpConditon = EntityCondition.makeCondition(
					listAllConditions, EntityJoinOperator.AND);
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("productId");
			}

			List<GenericValue> listProdGeneric = delegator.findList("Product",
					tmpConditon, null, listSortFields, opts, false);
			if (UtilValidate.isNotEmpty(listProdGeneric)) {
				for (GenericValue itemProd : listProdGeneric) {
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("productId", itemProd.get("productId"));
					row.put("productName", itemProd.get("productName"));
					row.put("internalName", itemProd.get("internalName"));
					row.put("quantityUomId",
							itemProd.getString("quantityUomId"));

					// column: packingUomId
					EntityCondition condsItem = EntityCondition
							.makeCondition(UtilMisc.toMap("productId",
									itemProd.get("productId"), "uomToId",
									itemProd.get("quantityUomId")));
					EntityFindOptions optsItem = new EntityFindOptions();
					optsItem.setDistinct(true);
					List<GenericValue> listConfigPacking = FastList
							.newInstance();
					listConfigPacking.addAll(delegator.findList(
							"ConfigPackingAndUom", condsItem, null, null,
							optsItem, false));
					List<Map<String, Object>> listQuantityUomIdByProduct = new ArrayList<Map<String, Object>>();
					for (GenericValue conPackItem : listConfigPacking) {
						Map<String, Object> packingUomIdMap = FastMap
								.newInstance();
						packingUomIdMap.put("description",
								conPackItem.getString("descriptionFrom"));
						packingUomIdMap.put("uomId",
								conPackItem.getString("uomFromId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					GenericValue quantityUom = delegator.findOne(
							"Uom",
							UtilMisc.toMap("uomId",
									itemProd.get("quantityUomId")), false);
					if (quantityUom != null) {
						Map<String, Object> packingUomIdMap = FastMap
								.newInstance();
						packingUomIdMap.put("description",
								quantityUom.getString("description"));
						packingUomIdMap.put("uomId",
								quantityUom.getString("uomId"));
						listQuantityUomIdByProduct.add(packingUomIdMap);
					}
					row.put("packingUomIds", listQuantityUomIdByProduct);
					listIterator.add(row);
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductTotal service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveInventoryProductFromOther(
			DispatchContext ctx, Map<String, ?> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			String facilityId = (String) context.get("facilityId");
			String datetimeReceivedStr = (String) context
					.get("datetimeReceived");
			long datetimeReceivedLog = Long.parseLong(datetimeReceivedStr);
			Timestamp datetimeReceived = new Timestamp(datetimeReceivedLog);
			List<Object> listTmp = (List<Object>) context.get("listProducts");
			Boolean isJson = false;
			if (!listTmp.isEmpty()) {
				if (listTmp.get(0) instanceof String) {
					isJson = true;
				}
			}
			List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
			if (isJson) {
				String stringJson = "[" + (String) listTmp.get(0) + "]";
				JSONArray lists = JSONArray.fromObject(stringJson);
				for (int i = 0; i < lists.size(); i++) {
					HashMap<String, Object> mapItems = new HashMap<String, Object>();
					JSONObject item = lists.getJSONObject(i);
					if (item.containsKey("productId")) {
						mapItems.put("productId", item.getString("productId"));
					}
					if (item.containsKey("quantity")) {
						mapItems.put("quantity",
								new BigDecimal(item.getString("quantity")));
					}
					if (item.containsKey("unitCost")) {
						mapItems.put("unitCost",
								new BigDecimal(item.getString("unitCost")));
					}
					if (item.containsKey("quantityUomId")) {
						mapItems.put("quantityUomId",
								item.getString("quantityUomId"));
					}
					if (item.containsKey("inventoryItemTypeId")) {
						mapItems.put("inventoryItemTypeId",
								item.getString("inventoryItemTypeId"));
					}
					if (item.containsKey("datetimeManufactured")) {
						mapItems.put("datetimeManufactured", new Timestamp(
								(Long) item.get("datetimeManufactured")));
					}
					if (item.containsKey("expireDate")) {
						mapItems.put("expireDate",
								new Timestamp((Long) item.get("expireDate")));
					}
					listProducts.add(mapItems);
				}
			}
			Delegator delegator = ctx.getDelegator();
			GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
			for (Map<String, Object> item : listProducts) {
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("productId", item.get("productId"));
				GenericValue product = delegator.findOne("Product", false,
						UtilMisc.toMap("productId", item.get("productId")));
				String quantityUomBase = product.getString("quantityUomId");
				BigDecimal quantity = (BigDecimal) item.get("quantity");
				if (UtilValidate.isNotEmpty(quantityUomBase)
						&& UtilValidate.isNotEmpty(item.get("quantityUomId"))
						&& !((String) item.get("quantityUomId"))
								.equals(quantityUomBase)
						&& quantity.compareTo(BigDecimal.ZERO) > 0) {
					quantity = quantity.multiply(LogisticsProductUtil
							.getConvertPackingNumber(delegator,
									(String) item.get("productId"),
									(String) item.get("quantityUomId"),
									(String) quantityUomBase));
				}
				mapTmp.put("quantityAccepted", quantity);
				mapTmp.put("quantityRejected", BigDecimal.ZERO);
				mapTmp.put("unitCost", item.get("unitCost"));
				mapTmp.put("facilityId", facilityId);
				mapTmp.put("inventoryItemTypeId",
						item.get("inventoryItemTypeId"));
				mapTmp.put("userLogin", system);
				mapTmp.put("datetimeReceived", datetimeReceived);
				mapTmp.put("datetimeManufactured",
						item.get("datetimeManufactured"));
				mapTmp.put("expireDate", item.get("expireDate"));
				mapTmp.put("rejectionId", null);
				dispatcher.runSync("receiveInventoryProduct", mapTmp);
			}
			// update destinationFacilityId of Shipment
			GenericValue shipment = delegator.findOne("Shipment",
					UtilMisc.toMap("shipmentId", context.get("shipmentId")),
					false);
			if (UtilValidate.isNotEmpty(shipment)) {
				shipment.set("destinationFacilityId", facilityId);
				shipment.store();
			}
			// update destFacilityId of Delivery
			GenericValue delivery = delegator.findOne("Delivery",
					UtilMisc.toMap("deliveryId", context.get("deliveryId")),
					false);
			if (UtilValidate.isNotEmpty(delivery)) {
				delivery.set("destFacilityId", facilityId);
				delivery.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> updateInventoryItemInLocation(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException, GenericServiceException {
		String inventoryItemId = (String) context.get("inventoryItemId");
		String locationId = (String) context.get("locationId");
		BigDecimal diffQty = (BigDecimal) context.get("quantity");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listInvItemByLocs = delegator.findList(
				"InventoryItemLocation", EntityCondition.makeCondition(UtilMisc
						.toMap("inventoryItemId", inventoryItemId,
								"locationId", locationId)), null, null, null,
				false);
		if (!listInvItemByLocs.isEmpty()
				&& diffQty.compareTo(BigDecimal.ZERO) != 0) {
			GenericValue obj = listInvItemByLocs.get(0);
			BigDecimal oldQty = obj.getBigDecimal("quantity");
			BigDecimal newQty = oldQty.add(diffQty);
			obj.set("quantity", newQty);
			delegator.store(listInvItemByLocs.get(0));
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}

	public static Map<String, Object> createNewInventoryItemInLocation(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException, GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> mapTmp = FastMap.newInstance();
		String inventoryItemId = (String) context.get("inventoryItemId");
		String locationId = (String) context.get("locationId");
		GenericValue inventory = delegator.findOne("InventoryItem", false,
				UtilMisc.toMap("inventoryItemId", inventoryItemId));
		String productId = inventory.getString("productId");
		GenericValue product = delegator.findOne("Product", false,
				UtilMisc.toMap("productId", productId));
		String quantityUomBase = product.getString("quantityUomId");
		String uomId = (String) context.get("quantityUomId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		Locale locale = (Locale) context.get("locale");
		if (quantity.compareTo(BigDecimal.ZERO) == 0) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"QuantityMustBeGreateThanZero", locale));
		}
		if (UtilValidate.isNotEmpty(quantityUomBase)
				&& UtilValidate.isNotEmpty(uomId)
				&& !(uomId).equals(quantityUomBase)
				&& quantity.compareTo(BigDecimal.ZERO) > 0) {
			quantity = quantity.multiply(LogisticsProductUtil
					.getConvertPackingNumber(delegator, productId, uomId,
							quantityUomBase));
		}
		mapTmp.put("productId", productId);
		if (quantity.compareTo(BigDecimal.ZERO) < 0) {
			mapTmp.put("quantityAccepted", quantity.negate());
			if ("NON_SERIAL_INV_ITEM".equals(inventory
					.getString("inventoryItemTypeId"))) {
				mapTmp.put("statusId", "INV_NS_DEFECTIVE");
			} else if ("SERIALIZED_INV_ITEM".equals(inventory
					.getString("inventoryItemTypeId"))) {
				mapTmp.put("statusId", "INV_DEFECTIVE");
			}
		} else {
			mapTmp.put("quantityAccepted", quantity);
		}
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		mapTmp.put("quantityRejected", BigDecimal.ZERO);
		mapTmp.put("unitCost", inventory.getBigDecimal("unitCost"));
		mapTmp.put("facilityId", inventory.getString("facilityId"));

		mapTmp.put("inventoryItemTypeId",
				inventory.getString("inventoryItemTypeId"));
		mapTmp.put("userLogin", system);
		mapTmp.put("datetimeReceived", UtilDateTime.nowTimestamp());
		mapTmp.put("datetimeManufactured",
				inventory.getTimestamp("datetimeManufactured"));
		mapTmp.put("expireDate", inventory.getTimestamp("expireDate"));
		mapTmp.put("rejectionId", null);
		Map<String, Object> mapReturnTmp = dispatcher.runSync(
				"receiveInventoryProduct", mapTmp);
		String newInvId = (String) mapReturnTmp.get("inventoryItemId");
		if (locationId != null && newInvId != null) {
			mapTmp = FastMap.newInstance();
			mapTmp.put("inventoryItemId", newInvId);
			if (quantity.compareTo(BigDecimal.ZERO) < 0) {
				mapTmp.put("quantity", quantity.negate());
			} else {
				mapTmp.put("quantity", quantity);
			}
			mapTmp.put("expireDate", inventory.getTimestamp("expireDate"));
			mapTmp.put("locationId", locationId);
			mapTmp.put("productId", productId);
			mapTmp.put("uomId", uomId);
			mapTmp.put("userLogin", (GenericValue) context.get("userLogin"));
			dispatcher.runSync("addInventoryItemToLocation", mapTmp);
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}

	public static Map<String, Object> addInventoryItemToLocation(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException, GenericServiceException {
		String inventoryItemId = (String) context.get("inventoryItemId");
		String locationId = (String) context.get("locationId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		Timestamp expireDate = (Timestamp) context.get("expireDate");
		String productId = (String) context.get("productId");
		String uomId = (String) context.get("uomId");

		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listInvItemByLocs = delegator.findList(
				"InventoryItemLocation", EntityCondition.makeCondition(UtilMisc
						.toMap("inventoryItemId", inventoryItemId,
								"locationId", locationId)), null, null, null,
				false);
		if (!listInvItemByLocs.isEmpty()
				&& quantity.compareTo(BigDecimal.ZERO) > 0) {
			GenericValue obj = listInvItemByLocs.get(0);
			BigDecimal oldQty = obj.getBigDecimal("quantity");
			BigDecimal newQty = oldQty.add(quantity);
			obj.set("quantity", newQty);
			delegator.store(listInvItemByLocs.get(0));
		} else if (listInvItemByLocs.isEmpty()
				&& quantity.compareTo(BigDecimal.ZERO) > 0) {
			GenericValue obj = delegator.makeValue("InventoryItemLocation");
			obj.put("inventoryItemId", inventoryItemId);
			obj.put("locationId", locationId);
			obj.put("quantity", quantity);
			obj.put("expireDate", expireDate);
			obj.put("productId", productId);
			obj.put("uomId", uomId);
			delegator.create(obj);
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}

	// dunglv
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductInventory(
			DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		String facilityId = null;
		if (parameters.get("facilityId") != null
				&& parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		if (facilityId != null && !"".equals(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId",
					EntityOperator.EQUALS, facilityId));
		}
		String ownerPartyId = null;
		if (parameters.get("ownerPartyId") != null
				&& parameters.get("ownerPartyId").length > 0) {
			ownerPartyId = parameters.get("ownerPartyId")[0];
		}
		if (ownerPartyId != null && !"".equals(ownerPartyId)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId",
					EntityOperator.EQUALS, ownerPartyId));
		}
		if (parameters.get("type") != null
				&& parameters.get("type").length > 0) {
			String type = parameters.get("type")[0];
			if ("cart".equals(type)) {
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "FINISHED_GOOD"));
				conditions.add(EntityCondition.makeCondition("productName", EntityJoinOperator.LIKE, "%Thẻ%"));
				List<GenericValue> products = delegator.findList("Product",
						EntityCondition.makeCondition(conditions), null, null, null, true);
				listAllConditions.add(EntityCondition.makeCondition("productId",
						EntityOperator.IN, EntityUtil.getFieldListFromEntityList(products, "productId", true)));
				listAllConditions.add(EntityCondition.makeCondition("serialNumber",
						EntityOperator.NOT_EQUAL, null));
			}
		}

		EntityCondition qohCon = EntityCondition.makeCondition(
				"quantityOnHandTotal", EntityOperator.GREATER_THAN,
				BigDecimal.ZERO);

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(mapCondition));
		conditions.add(EntityCondition.makeCondition(listAllConditions,
				EntityJoinOperator.AND));

		try {
			listSortFields.add("productId");
			listIterator = delegator.find("InventoryItemMin", EntityCondition
					.makeCondition(conditions, EntityOperator.AND), qohCon,
					null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqListSupplier service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	// end dunglv

	public static Map<String, Object> reserveInventoryForOrderItem(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		String orderId = (String) context.get("orderId");
		String orderItemSeqId = (String) context.get("orderItemSeqId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		String quantityUomId = (String) context.get("quantityUomId");
		String facilityId = (String) context.get("facilityId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap(
				"orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
		GenericValue orderHeader = delegator.findOne("OrderHeader",
				UtilMisc.toMap("orderId", orderId), false);
		String productStoreId = orderHeader.getString("productStoreId");
		String productId = orderItem.getString("productId");
		GenericValue product = delegator.findOne("Product",
				UtilMisc.toMap("productId", productId), false);
		String baseQtyUomId = product.getString("quantityUomId");
		if (UtilValidate.isNotEmpty(quantityUomId)
				&& UtilValidate.isNotEmpty(baseQtyUomId)) {
			if (!quantityUomId.equals(baseQtyUomId)) {
				BigDecimal convertUom = LogisticsProductUtil
						.getConvertPackingNumber(delegator, productId,
								quantityUomId, baseQtyUomId);
				if (convertUom.compareTo(BigDecimal.ZERO) > 0) {
					quantity = quantity.multiply(convertUom);
				}
			}
		}
		String statusId = orderItem.getString("statusId");
		if (UtilValidate.isNotEmpty(statusId)
				&& !"ITEM_CANCELLED".equals(statusId)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("orderId", orderId);
			GenericValue userLogin = delegator.findOne("UserLogin",
					UtilMisc.toMap("userLoginId", "system"), false);
			map.put("userLogin", userLogin);
			map.put("orderItemSeqId", orderItemSeqId);
			map.put("productId", productId);
			map.put("quantity", quantity);

			List<GenericValue> gv2 = delegator.findList("OrderItemShipGroup",
					EntityCondition.makeCondition("orderId", orderId), null,
					null, null, false);
			for (int j = 0; j < gv2.size(); j++) {
				map.put("shipGroupSeqId", gv2.get(j)
						.getString("shipGroupSeqId"));
				if (UtilValidate.isNotEmpty(facilityId)) {
					map.put("facilityId", facilityId);
					map.put("requireInventory", UtilProperties
							.getPropertyValue(LOGISTICS_PROPERTIES,
									"default.requireInventory"));
					dispatcher
							.runSync("reserveProductInventoryByFacility", map);
				} else {
					map.remove("facilityId");
					map.put("productStoreId", productStoreId);
					dispatcher.runSync("reserveStoreInventory", map);
				}
			}
		} else {
			if ("ITEM_CANCELLED".equals(statusId)) {
				return ServiceUtil
						.returnError("OLBIUS: reserveInventoryFromOrderItem - Cannot create reserves inventory item from cancelled item");
			}
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> quickReserveInventoryForOrder(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		String orderId = (String) context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> orderItems = delegator.findList("OrderItem",
				EntityCondition.makeCondition(UtilMisc
						.toMap("orderId", orderId)), null, null, null, false);
		if (!orderItems.isEmpty()) {
			for (GenericValue item : orderItems) {
				Map<String, Object> mapReserves = FastMap.newInstance();
				mapReserves.put("orderId", orderId);
				mapReserves.put("orderItemSeqId",
						item.getString("orderItemSeqId"));
				if (UtilValidate.isNotEmpty(item.get("alternativeQuantity"))) {
					mapReserves.put("quantity",
							item.getBigDecimal("alternativeQuantity"));
					mapReserves.put("quantityUomId",
							item.getString("quantityUomId"));
				} else {
					mapReserves.put("quantity", item.getBigDecimal("quantity"));
				}
				mapReserves.put("userLogin", userLogin);
				dispatcher.runSync("reserveInventoryForOrderItem", mapReserves);
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("orderId", orderId);
		return mapReturn;
	}

	public static Map<String, Object> quickReserveInventoryForOrderAndFacility(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		String orderId = (String) context.get("orderId");
		String facilityId = (String) context.get("facilityId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<GenericValue> orderItems = delegator.findList("OrderItem",
				EntityCondition.makeCondition(UtilMisc
						.toMap("orderId", orderId)), null, null, null, false);
		if (!orderItems.isEmpty()) {
			for (GenericValue item : orderItems) {
				Map<String, Object> mapReserves = FastMap.newInstance();
				mapReserves.put("orderId", orderId);
				mapReserves.put("orderItemSeqId",
						item.getString("orderItemSeqId"));
				if (UtilValidate.isNotEmpty(item.get("alternativeQuantity"))) {
					mapReserves.put("quantity",
							item.getBigDecimal("alternativeQuantity"));
					mapReserves.put("quantityUomId",
							item.getString("quantityUomId"));
				} else {
					mapReserves.put("quantity", item.getBigDecimal("quantity"));
				}
				mapReserves.put("userLogin", userLogin);
				mapReserves.put("facilityId", facilityId);
				dispatcher.runSync("reserveInventoryForOrderItem", mapReserves);
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("orderId", orderId);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetInventoryItemVarianceDetail(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		String facilityId = null;
		if (parameters.get("facilityId") != null
				&& parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		if (facilityId != null && !"".equals(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId",
					EntityOperator.EQUALS, facilityId));
		}
		String partyId = null;
		if (parameters.get("partyId") != null
				&& parameters.get("partyId").length > 0) {
			partyId = parameters.get("partyId")[0];
		}
		if (partyId != null && !"".equals(partyId)) {
			listAllConditions.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, partyId));
		}
		String physicalInventoryId = null;
		if (parameters.get("physicalInventoryId") != null
				&& parameters.get("physicalInventoryId").length > 0) {
			physicalInventoryId = parameters.get("physicalInventoryId")[0];
		}
		if (physicalInventoryId != null && !"".equals(physicalInventoryId)) {
			listAllConditions.add(EntityCondition.makeCondition(
					"physicalInventoryId", EntityOperator.EQUALS,
					physicalInventoryId));
		}
		List<GenericValue> listInvs = new ArrayList<GenericValue>();
		try {
			listIterator = delegator.find("InventoryItemVarianceGroupByDetail",
					EntityCondition.makeCondition(listAllConditions,
							EntityOperator.AND), null, null, listSortFields,
					opts);
			listInvs = listIterator.getCompleteList();
			listIterator.close();
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetInventoryItemVarianceDetail service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listInvs);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPhysicalInventoryCount(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String physicalInventoryId = (String) context.get("physicalInventoryId");
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("statusId") && !"".equals(item.getString("statusId")) && !"null".equals(item.getString("statusId"))) {
					mapItems.put("statusId", item.getString("statusId"));
				} else {
					mapItems.put("statusId", null);
				}
				if (item.containsKey("expireDate") && !"".equals(item.getString("expireDate")) && !"null".equals(item.getString("expireDate"))) {
					mapItems.put("expireDate", item.getString("expireDate"));
				} else {
					mapItems.put("expireDate", null);
				}
				if (item.containsKey("datetimeManufactured") && !"".equals(item.getString("datetimeManufactured")) && !"null".equals(item.getString("datetimeManufactured"))) {
					mapItems.put("datetimeManufactured", item.getString("datetimeManufactured"));
				} else {
					mapItems.put("datetimeManufactured", null);
				}
				if (item.containsKey("ownerPartyId")) {
					mapItems.put("ownerPartyId", item.getString("ownerPartyId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("lotId") && !"".equals(item.getString("lotId")) && !"null".equals(item.getString("lotId"))) {
					mapItems.put("lotId", item.getString("lotId"));
				} else {
					mapItems.put("lotId", null);
				}
				if (item.containsKey("quantityOnHandTotal")) {
					mapItems.put("quantityOnHandTotal", item.getString("quantityOnHandTotal"));
				}
				if (item.containsKey("comments")) {
					mapItems.put("comments", item.getString("comments"));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context
					.get("listProducts");
		}
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
		Delegator delegator = ctx.getDelegator();
		for (Map<String, Object> item : listProducts) {
			String productId = (String)item.get("productId");
			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			Boolean reqAmount = false;
			if (UtilValidate.isNotEmpty((objProduct.get("requireAmount"))) && "Y".equals((objProduct.getString("requireAmount")))) {
				reqAmount = true;
			} 
			BigDecimal quantity = new BigDecimal((String) item.get("quantityOnHandTotal"));
			EntityCondition Cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition Cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, item.get("facilityId"));
			EntityCondition Cond3;
			if (item.get("statusId") != "null" && item.get("statusId") != null) {
				Cond3 = EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, item.get("statusId"));
			} else {
				Cond3 = EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, null);
			}
			EntityCondition Cond4;
			if (UtilValidate.isNotEmpty(item.get("expireDate"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("expireDate")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond5;
			if (UtilValidate.isNotEmpty(item.get("datetimeManufactured"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("datetimeManufactured")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond6 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, item.get("ownerPartyId"));
			EntityCondition Cond7 = EntityCondition.makeCondition("lotId", EntityOperator.EQUALS, item.get("lotId"));

			EntityCondition Cond8 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity);
			if (reqAmount){
				Cond8 = EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity);
			}
			
			List<EntityCondition> listConds = new ArrayList<>();
			listConds = new ArrayList<>();
			listConds.add(Cond7);
			listConds.add(Cond6);
			listConds.add(Cond5);
			listConds.add(Cond4);
			listConds.add(Cond3);
			listConds.add(Cond2);
			listConds.add(Cond1);
			listConds.add(Cond8);
			
			EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
			List<GenericValue> listInventory = delegator.findList(
					"InventoryItem", allConds, null, null, null, false);
			if (!listInventory.isEmpty()) {
				GenericValue inv = listInventory.get(0);
				String inventoryItemId = inv.getString("inventoryItemId");
				Map<String, Object> map = FastMap.newInstance();
				map.put("inventoryItemId", inventoryItemId);
				map.put("quantityOnHandTotal", quantity);
				if (reqAmount) {
					map.put("quantityOnHandTotal", BigDecimal.ONE);
					map.put("amountOnHandTotal", quantity);
				}
				map.put("physicalInventoryId", physicalInventoryId);
				map.put("userLogin", userLogin);
				listInventoryItems.add(map);
			} else {
				List<EntityCondition> listConds2 = new ArrayList<>();
				listConds2.add(Cond7);
				listConds2.add(Cond6);
				listConds2.add(Cond5);
				listConds2.add(Cond4);
				listConds2.add(Cond3);
				listConds2.add(Cond2);
				listConds2.add(Cond1);
				EntityCondition Cond9 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
				listConds2.add(Cond9);
				
				EntityCondition allConds2 = EntityCondition.makeCondition(listConds2, EntityOperator.AND);
				listInventory = delegator.findList("InventoryItem", allConds2, null, null, null, false);
				if (listInventory.isEmpty()) return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
				BigDecimal quantityRemain = quantity;
				for (GenericValue inv : listInventory) {
					if (quantityRemain.compareTo(BigDecimal.ZERO) > 0){
						BigDecimal quantityOnHandTmp = inv.getBigDecimal("quantityOnHandTotal");
						if (reqAmount) quantityOnHandTmp = inv.getBigDecimal("amountOnHandTotal");
						if (quantityOnHandTmp.compareTo(quantityRemain) <= 0){
							quantityRemain = quantityRemain.subtract(quantityOnHandTmp);
							Map<String, Object> map = FastMap.newInstance();
							map.put("inventoryItemId", inv.getString("inventoryItemId"));
							map.put("quantityOnHandTotal", quantityOnHandTmp);
							if (reqAmount) {
								map.put("quantityOnHandTotal", BigDecimal.ONE);
								map.put("amountOnHandTotal", quantityOnHandTmp);
							}
							map.put("physicalInventoryId", physicalInventoryId);
							map.put("userLogin", userLogin);
							listInventoryItems.add(map);
						} else {
							quantityRemain = BigDecimal.ZERO;
							Map<String, Object> map = FastMap.newInstance();
							map.put("inventoryItemId", inv.getString("inventoryItemId"));
							map.put("physicalInventoryId", physicalInventoryId);
							map.put("quantityOnHandTotal", quantityRemain);
							if (reqAmount) {
								map.put("quantityOnHandTotal", BigDecimal.ONE);
								map.put("amountOnHandTotal", quantityRemain);
							}
							map.put("userLogin", userLogin);
							listInventoryItems.add(map);
						}
					} else {
						break;
					}
				}
			}
		}
		if (!listInventoryItems.isEmpty()){
			for (Map<String, Object> count : listInventoryItems) {
				try {
					dispatcher.runSync("createInventoryItemCount", count);
				} catch (GenericServiceException e){
					return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemCount error!");
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("physicalInventoryId", physicalInventoryId);
		return result;
	}
	
	public static Map<String, Object> createInventoryItemCount(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue newInvCount = delegator.makeValue("InventoryItemCount");
		String physicalInventoryId = (String)context.get("physicalInventoryId");
		String inventoryItemId = (String)context.get("inventoryItemId");
		BigDecimal quantityOnHandTotal = (BigDecimal)context.get("quantityOnHandTotal");
		BigDecimal amountOnHandTotal = (BigDecimal)context.get("amountOnHandTotal");
		String comments = (String)context.get("comments");
		
		newInvCount.put("physicalInventoryId", physicalInventoryId);
		newInvCount.put("inventoryItemId", inventoryItemId);
		newInvCount.put("quantityOnHandTotal", quantityOnHandTotal);
		newInvCount.put("amountOnHandTotal", amountOnHandTotal);
		newInvCount.put("comments", comments);
		
		delegator.createOrStore(newInvCount);
		
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPhysicalInventoryAndMultiVariance(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String partyId = (String) context.get("partyId");
		String facilityId = (String) context.get("facilityId");
		String generalComments = (String) context.get("generalComments");
		Long physicalInventoryDateTmp = (Long) context
				.get("physicalInventoryDate");
		Timestamp physicalInventoryDate = null;
		if (UtilValidate.isNotEmpty(physicalInventoryDateTmp)) {
			physicalInventoryDate = new Timestamp(physicalInventoryDateTmp);
		}
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("statusId") && !"".equals(item.getString("statusId")) && !"null".equals(item.getString("statusId"))) {
					mapItems.put("statusId", item.getString("statusId"));
				} else {
					mapItems.put("statusId", null);
				}
				if (item.containsKey("expireDate") && !"".equals(item.getString("expireDate")) && !"null".equals(item.getString("expireDate"))) {
					mapItems.put("expireDate", item.getString("expireDate"));
				} else {
					mapItems.put("expireDate", null);
				}
				if (item.containsKey("datetimeManufactured") && !"".equals(item.getString("datetimeManufactured")) && !"null".equals(item.getString("datetimeManufactured"))) {
					mapItems.put("datetimeManufactured",
							item.getString("datetimeManufactured"));
				} else {
					mapItems.put("datetimeManufactured", null);
				}
				if (item.containsKey("ownerPartyId")) {
					mapItems.put("ownerPartyId", item.getString("ownerPartyId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("lotId") && !"".equals(item.getString("lotId")) && !"null".equals(item.getString("lotId"))) {
					mapItems.put("lotId", item.getString("lotId"));
				} else {
					mapItems.put("lotId", null);
				}
				if (item.containsKey("quantityOnHandVar")) {
					mapItems.put("quantityOnHandVar", item.getString("quantityOnHandVar"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("varianceReasonId")) {
					mapItems.put("varianceReasonId", item.getString("varianceReasonId"));
				}
				if (item.containsKey("comments")) {
					mapItems.put("comments", item.getString("comments"));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProducts");
		}

		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
		Delegator delegator = ctx.getDelegator();
		for (Map<String, Object> item : listProducts) {
			
			BigDecimal quantity = new BigDecimal((String) item.get("quantityOnHandVar"));
			String varianceReasonId = (String)item.get("varianceReasonId");
			GenericValue reason = delegator.findOne("VarianceReason", false, UtilMisc.toMap("varianceReasonId", varianceReasonId));
			if (UtilValidate.isNotEmpty(reason)) {
				if (UtilValidate.isNotEmpty(reason.getString("negativeNumber")) && "Y".equals(reason.getString("negativeNumber"))){
					if (quantity.compareTo(BigDecimal.ZERO) > 0) {
						quantity = quantity.negate();
						item.put("quantityOnHandVar", quantity);
					}
				} 
			}
			String productId = (String)item.get("productId");
			GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			String requireAmount = objProduct.getString("requireAmount");
			Boolean reqAmount = false;
			if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
				reqAmount = true;
			}
			EntityCondition Cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition Cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, item.get("facilityId"));
			EntityCondition Cond3;
			if (item.get("statusId") != "null" && item.get("statusId") != null) {
				Cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, item.get("statusId"));
			} else {
				Cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
			}

			EntityCondition Cond4;
			if (UtilValidate.isNotEmpty(item.get("expireDate"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("expireDate")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond5;
			if (UtilValidate.isNotEmpty(item.get("datetimeManufactured"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("datetimeManufactured")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond6 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, item.get("ownerPartyId"));
			EntityCondition Cond7 = EntityCondition.makeCondition("lotId", EntityOperator.EQUALS, item.get("lotId"));
			EntityCondition Cond8 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity.abs());
			if (reqAmount) {
				Cond8 = EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity.abs());
			}
			EntityCondition Cond9 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);

			List<EntityCondition> listConds = new ArrayList<>();
			if (quantity.compareTo(BigDecimal.ZERO) > 0) {
				listConds = new ArrayList<>();
				listConds.add(Cond7);
				listConds.add(Cond6);
				listConds.add(Cond5);
				listConds.add(Cond4);
				listConds.add(Cond3);
				listConds.add(Cond2);
				listConds.add(Cond1);
				EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
				List<GenericValue> listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
				if (!listInventory.isEmpty()) {
					GenericValue inv = listInventory.get(0);
					String inventoryItemId = inv.getString("inventoryItemId");
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("inventoryItemId", inventoryItemId);
					listInventoryItems.add(map);
				} else {
					return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
				}
			} else {
				listConds = new ArrayList<>();
				listConds.add(Cond8);
				listConds.add(Cond7);
				listConds.add(Cond6);
				listConds.add(Cond5);
				listConds.add(Cond4);
				listConds.add(Cond3);
				listConds.add(Cond2);
				listConds.add(Cond1);
				listConds.add(Cond9);
				EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
				List<GenericValue> listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
				if (!listInventory.isEmpty()) {
					GenericValue inv = listInventory.get(0);
					String inventoryItemId = inv.getString("inventoryItemId");
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("inventoryItemId", inventoryItemId);
					listInventoryItems.add(map);
				} else {
					listConds = new ArrayList<>();
					listConds.add(Cond7);
					listConds.add(Cond6);
					listConds.add(Cond5);
					listConds.add(Cond4);
					listConds.add(Cond3);
					listConds.add(Cond2);
					listConds.add(Cond1);
					listConds.add(Cond9);
					allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
					listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
					if (!listInventory.isEmpty()) {
						BigDecimal qohTotal = BigDecimal.ZERO;
						for (GenericValue inv : listInventory) {
							qohTotal = qohTotal.add(inv.getBigDecimal("quantityOnHandTotal"));
							if (reqAmount) {
								qohTotal = qohTotal.add(inv.getBigDecimal("amountOnHandTotal"));
							}
						}
						if (qohTotal.compareTo(quantity.abs()) >= 0) {
							BigDecimal remainQuantity = quantity.abs();
							for (GenericValue inv : listInventory) {
								BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
								String inventoryItemId = inv.getString("inventoryItemId");
								Map<String, Object> map = FastMap.newInstance();
								map.putAll(item);
								map.put("inventoryItemId", inventoryItemId);
								if (remainQuantity.compareTo(qoh) >= 0) {
									map.put("quantityOnHandVar", qoh.negate().toString());
									remainQuantity = remainQuantity.subtract(qoh);
								} else {
									map.put("quantityOnHandVar", remainQuantity.negate().toString());
									remainQuantity = BigDecimal.ZERO;
								}
								listInventoryItems.add(map);
								if (remainQuantity.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}
							}
						} else {
							return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH");
						}
					} else {
						return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
					}
				}
			}
		}

		GenericValue system = delegator.findOne("UserLogin", false,
				UtilMisc.toMap("userLoginId", "system"));
		String physicalInventoryId = null;
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Map<String, Object> mapCreatePhysicalInv = UtilMisc.toMap("userLogin", system, "partyId", partyId, "generalComments", generalComments, "physicalInventoryDate",
					physicalInventoryDate, "facilityId", facilityId);
			Map<String, Object> mapReturnPhys = dispatcher.runSync("createPhysicalInventory", mapCreatePhysicalInv);
			physicalInventoryId = (String) mapReturnPhys.get("physicalInventoryId");
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
		}
		if (UtilValidate.isNotEmpty(physicalInventoryId) && !listInventoryItems.isEmpty()) {
			for (Map<String, Object> item : listInventoryItems) {
				String inventoryItemId = (String) item.get("inventoryItemId");
				if (UtilValidate.isNotEmpty(inventoryItemId) && UtilValidate.isNotEmpty(item.get("quantityOnHandVar")) && UtilValidate.isNotEmpty(item.get("varianceReasonId"))) {
					GenericValue existed = delegator.findOne("InventoryItemVariance", false, UtilMisc.toMap("inventoryItemId", item.get("inventoryItemId"), "physicalInventoryId", physicalInventoryId));
					BigDecimal quantityOnHandVar = BigDecimal.ZERO;
					if (item.get("quantityOnHandVar") instanceof String) {
						quantityOnHandVar = new BigDecimal((String) item.get("quantityOnHandVar"));
					} else {
						quantityOnHandVar = (BigDecimal)item.get("quantityOnHandVar");
					}
					GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
					String productId = (String) inv.getString("productId");
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String requireAmount = product.getString("requireAmount");
					Boolean reqAmount = false;
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						reqAmount = true;
					}
					String baseQtyUomId = product.getString("quantityUomId");
					String baseWeightUomId = product.getString("weightUomId");
					if (reqAmount) {
						if (UtilValidate.isNotEmpty(item.get("weightUomId")) && UtilValidate.isNotEmpty(product.get("weightUomId"))) {
							String weightUomId = (String) item.get("weightUomId");
							if (!weightUomId.equals(baseWeightUomId)) {
								BigDecimal convert = LogisticsProductUtil.getConvertWeightNumber(delegator, productId, weightUomId, baseWeightUomId);
								if (UtilValidate.isNotEmpty(convert) && convert.compareTo(BigDecimal.ZERO) > 0) {
									quantityOnHandVar = quantityOnHandVar.multiply(convert);
								}
							}
						}
					} else {
						if (UtilValidate.isNotEmpty(item.get("quantityUomId")) && UtilValidate.isNotEmpty(product.get("quantityUomId"))) {
							String quantityUomId = (String) item.get("quantityUomId");
							if (!quantityUomId.equals(baseQtyUomId)) {
								BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQtyUomId);
								if (UtilValidate.isNotEmpty(convert) && convert.compareTo(BigDecimal.ZERO) > 0) {
									quantityOnHandVar = quantityOnHandVar.multiply(convert);
								}
							}
						}
					}
					BigDecimal availableToPromiseVar = quantityOnHandVar;
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					if (reqAmount) {
						qoh = inv.getBigDecimal("amountOnHandTotal");
					}
					if ((availableToPromiseVar.compareTo(BigDecimal.ZERO) > 0) || (qoh.compareTo(availableToPromiseVar.abs()) >= 0 && availableToPromiseVar.compareTo(BigDecimal.ZERO) < 0)) {
						if (UtilValidate.isNotEmpty(existed)) {
							try {
								Map<String, Object> mapNewInv = inv.getAllFields();
								mapNewInv.remove("inventoryItemId");
								mapNewInv.remove("quantityOnHandTotal");
								mapNewInv.remove("availableToPromiseTotal");
								mapNewInv.put("userLogin", system);
								String invNewId = null;

								Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
								invNewId = (String) mapTmp.get("inventoryItemId");

									Map<String, Object> mapDetail = FastMap.newInstance();
									mapDetail.put("userLogin", system);
									mapDetail.put("inventoryItemId", inventoryItemId);
									mapDetail.put("quantityOnHandDiff", quantityOnHandVar.abs().negate());
									mapDetail.put("availableToPromiseDiff", quantityOnHandVar.abs().negate());
									if (reqAmount){
										mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
										mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
										mapDetail.put("amountOnHandDiff", quantityOnHandVar.abs().negate());
									}
									dispatcher.runSync("createInventoryItemDetail", mapDetail);

								if (quantityOnHandVar.compareTo(BigDecimal.ZERO) != 0) {
									BigDecimal qohVar = quantityOnHandVar;
									BigDecimal atpVar = availableToPromiseVar;
									BigDecimal amountVar = BigDecimal.ZERO;
									if (reqAmount){
										qohVar = BigDecimal.ONE;
										if (quantityOnHandVar.compareTo(BigDecimal.ZERO) < 0) qohVar = qohVar.negate();
										atpVar = qohVar;
										amountVar = quantityOnHandVar;
									}
									try {
										Map<String, Object> mapCreateInvItemVar = UtilMisc
												.toMap("userLogin", system,
														"inventoryItemId", invNewId,
														"physicalInventoryId", physicalInventoryId,
														"varianceReasonId", item.get("varianceReasonId"),
														"quantityOnHandVar", qohVar,
														"amountOnHandVar", amountVar,
														"availableToPromiseVar", atpVar,
														"comments", item.get("comments"));
										dispatcher.runSync("createInventoryItemVariance", mapCreateInvItemVar);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
									}
								}
							} catch (GenericServiceException e) {
								return ServiceUtil
										.returnError("OLBIUS: createInventoryItem runsync service error!");
							}
						} else {
							if (quantityOnHandVar.compareTo(BigDecimal.ZERO) != 0) {
								BigDecimal qohVar = quantityOnHandVar;
								BigDecimal atpVar = availableToPromiseVar;
								BigDecimal amountVar = BigDecimal.ZERO;
								if (reqAmount){
									qohVar = BigDecimal.ONE;
									if (quantityOnHandVar.compareTo(BigDecimal.ZERO) < 0) qohVar = qohVar.negate();
									atpVar = qohVar;
									amountVar = quantityOnHandVar;
								}
								try {
									Map<String, Object> mapCreateInvItemVar = UtilMisc
											.toMap("userLogin", system,
													"inventoryItemId", item.get("inventoryItemId"),
													"physicalInventoryId", physicalInventoryId,
													"varianceReasonId", item.get("varianceReasonId"),
													"quantityOnHandVar", qohVar,
													"amountOnHandVar", amountVar,
													"availableToPromiseVar", atpVar,
													"comments", item.get("comments"));
									dispatcher.runSync("createInventoryItemVariance", mapCreateInvItemVar);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
								}
							}
						}
					} else {
						return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH");
					}
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("physicalInventoryId", physicalInventoryId);
		return mapReturn;
	}

	public static Map<String, Object> setDefaultInventoryItemLabel(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String inventoryItemId = (String) context.get("inventoryItemId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listInventoryLabels = delegator.findList(
				"InventoryItemLabelAppl", EntityCondition
						.makeCondition(UtilMisc.toMap("inventoryItemId",
								inventoryItemId)), null, null, null, false);
		if (listInventoryLabels.isEmpty()) {
			GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
			String productId = inv.getString("productId");
			List<GenericValue> listConfigLabels = delegator.findList("ConfigLabel", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
			listConfigLabels = EntityUtil.filterByDate(listConfigLabels);
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if (!listConfigLabels.isEmpty()){
				for (GenericValue item : listConfigLabels) {
					String inventoryItemLabelId = item.getString("inventoryItemLabelId");
					GenericValue label = delegator.findOne("InventoryItemLabel", false, UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelId));
					if (label.getString("inventoryItemLabelTypeId").equals("USING_PURPOSE")){
						GenericValue userLogin = (GenericValue) context.get("userLogin");
						if (UtilValidate.isNotEmpty(label)) {
							try {
								dispatcher.runSync("createInventoryItemLabelAppl",
												UtilMisc.toMap("userLogin", userLogin,
														"inventoryItemId", inventoryItemId,
														"inventoryItemLabelId", inventoryItemLabelId,
														"inventoryItemLabelTypeId", label.getString("inventoryItemLabelTypeId")));
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error");
							}
						}
					}
				}
			} else {
				String defaultLabelId = UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.inventoryItemLabelId");
				if (UtilValidate.isNotEmpty(defaultLabelId)) {
					GenericValue label = delegator.findOne("InventoryItemLabel",
							false,
							UtilMisc.toMap("inventoryItemLabelId", defaultLabelId));
					GenericValue userLogin = (GenericValue) context
							.get("userLogin");
					if (UtilValidate.isNotEmpty(label)) {
						try {
							dispatcher.runSync("createInventoryItemLabelAppl",
											UtilMisc.toMap("userLogin", userLogin,
													"inventoryItemId", inventoryItemId,
													"inventoryItemLabelId", defaultLabelId,
													"inventoryItemLabelTypeId", label.getString("inventoryItemLabelTypeId")));
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error");
						}
					}
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("inventoryItemId", inventoryItemId);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getConfigLabels(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("ConfigLabelGroupByProduct",
					EntityCondition.makeCondition(listAllConditions,
							EntityOperator.AND), null, null, listSortFields,
					opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getConfigLabels service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetInventoryItemLabelTypes(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("InventoryItemLabelType",
					EntityCondition.makeCondition(listAllConditions,
							EntityOperator.AND), null, null, listSortFields,
					opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetInventoryItemLabelTypes service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetInventoryItemLabels(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("InventoryItemLabel", EntityCondition
					.makeCondition(listAllConditions, EntityOperator.AND),
					null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetInventoryItemLabels service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> createConfigLabel(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productId = (String) context.get("productId");
		String inventoryItemLabelId = (String) context
				.get("inventoryItemLabelId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String description = (String) context.get("description");
		try {
			GenericValue newConfig = delegator.makeValue("ConfigLabel");
			newConfig.put("productId", productId);
			newConfig.put("inventoryItemLabelId", inventoryItemLabelId);
			newConfig.put("fromDate", fromDate);
			if (UtilValidate.isNotEmpty(thruDate)) {
				newConfig.put("thruDate", thruDate);
			}
			if (UtilValidate.isNotEmpty(description)) {
				newConfig.put("description", description);
			}
			delegator.createOrStore(newConfig);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createConfigLabel service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	public static Map<String, Object> deleteConfigLabel(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productId = (String) context.get("productId");
		String inventoryItemLabelId = (String) context
				.get("inventoryItemLabelId");
		Long tmp = (Long) context.get("fromDate");
		Timestamp fromDate = new Timestamp(tmp);
		try {
			GenericValue config = delegator.findOne("ConfigLabel", false,
					UtilMisc.toMap("productId", productId,
							"inventoryItemLabelId", inventoryItemLabelId,
							"fromDate", fromDate));
			config.put("thruDate", UtilDateTime.nowTimestamp());
			delegator.store(config);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling deleteConfigLabel service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	public static Map<String, Object> deleteConfigLabelByProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productId = (String) context.get("productId");
		try {
			List<GenericValue> list = delegator.findList("ConfigLabel",
					EntityCondition.makeCondition(UtilMisc.toMap("productId",
							productId)), null, null, null, false);
			EntityUtil.filterByDate(list);
			if (!list.isEmpty()) {
				for (GenericValue item : list) {
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling deleteConfigLabelByProduct service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createConfigLabels(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		List<Object> listProductIdTmps = (List<Object>) context
				.get("listProductIds");

		Boolean isJson = false;
		if (!listProductIdTmps.isEmpty()) {
			if (listProductIdTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listProductIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listProductIdTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItem.put("productId", item.getString("productId"));
				}
				listProductIds.add(mapItem);
			}
		} else {
			listProductIds = (List<Map<String, String>>) context
					.get("listProductIds");
		}

		List<Object> listInventoryItemLabelIdTmps = (List<Object>) context
				.get("listInventoryItemLabelIds");
		isJson = false;
		if (!listInventoryItemLabelIdTmps.isEmpty()) {
			if (listInventoryItemLabelIdTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIds.add(mapItem);
			}
		} else {
			listInventoryItemLabelIds = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIds");
		}
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if (!listInventoryItemLabelIds.isEmpty()
					&& !listProductIds.isEmpty()) {
				for (Map<String, String> mapProductId : listProductIds) {
					String productId = mapProductId.get("productId");
					for (Map<String, String> mapInventotyItemLabelId : listInventoryItemLabelIds) {
						String inventoryItemLabelId = mapInventotyItemLabelId
								.get("inventoryItemLabelId");
						List<GenericValue> list = delegator.findList(
								"ConfigLabel", EntityCondition
										.makeCondition(UtilMisc.toMap(
												"productId", productId,
												"inventoryItemLabelId",
												inventoryItemLabelId)), null,
								null, null, false);
						list = EntityUtil.filterByDate(list);
						if (list.isEmpty()) {
							try {
								dispatcher.runSync("createConfigLabel",
										UtilMisc.toMap("userLogin",
												(GenericValue) context
														.get("userLogin"),
												"productId", productId,
												"inventoryItemLabelId",
												inventoryItemLabelId,
												"fromDate", UtilDateTime
														.nowTimestamp()));
							} catch (GenericServiceException e) {
								return ServiceUtil
										.returnError("OLBIUS: runsync service error!");
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: Creat configlabel error "
					+ e.toString());
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateConfigLabelByProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		String productId = (String) context.get("productId");
		List<Object> listInventoryItemLabelIdTmps = (List<Object>) context
				.get("listInventoryItemLabelIds");
		Boolean isJson = false;
		if (!listInventoryItemLabelIdTmps.isEmpty()) {
			if (listInventoryItemLabelIdTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIds.add(mapItem);
			}
		} else {
			listInventoryItemLabelIds = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIds");
		}
		try {
			List<GenericValue> listOlds = delegator.findList("ConfigLabel",
					EntityCondition.makeCondition(UtilMisc.toMap("productId",
							productId)), null, null, null, false);
			listOlds = EntityUtil.filterByDate(listOlds);
			List<String> listIdToRemoves = new ArrayList<String>();
			for (GenericValue item1 : listOlds) {
				Boolean check = false;
				for (Map<String, String> item2 : listInventoryItemLabelIds) {
					String inventoryItemLabelId = item2
							.get("inventoryItemLabelId");
					if (inventoryItemLabelId.equals(item1
							.getString("inventoryItemLabelId"))) {
						check = false;
					} else {
						check = true;
					}
				}
				if (check) {
					listIdToRemoves
							.add(item1.getString("inventoryItemLabelId"));
				}
			}
			if (!listIdToRemoves.isEmpty()) {
				EntityCondition Cond1 = EntityCondition.makeCondition(
						"productId", EntityOperator.EQUALS, productId);
				EntityCondition Cond2 = EntityCondition.makeCondition(
						"inventoryItemLabelId", EntityOperator.IN,
						listIdToRemoves);
				List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
				List<GenericValue> listRemoves = delegator.findList(
						"ConfigLabel",
						EntityCondition.makeCondition(listConds), null, null,
						null, false);
				listRemoves = EntityUtil.filterByDate(listRemoves);
				for (GenericValue item : listRemoves) {
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if (!listInventoryItemLabelIds.isEmpty()
					&& UtilValidate.isNotEmpty(productId)) {
				for (Map<String, String> mapInventotyItemLabelId : listInventoryItemLabelIds) {
					String inventoryItemLabelId = mapInventotyItemLabelId
							.get("inventoryItemLabelId");
					List<GenericValue> list = delegator.findList("ConfigLabel",
							EntityCondition.makeCondition(UtilMisc.toMap(
									"productId", productId,
									"inventoryItemLabelId",
									inventoryItemLabelId)), null, null, null,
							false);
					list = EntityUtil.filterByDate(list);
					if (list.isEmpty()) {
						try {
							dispatcher.runSync("createConfigLabel", UtilMisc
									.toMap("userLogin", (GenericValue) context
											.get("userLogin"), "productId",
											productId, "inventoryItemLabelId",
											inventoryItemLabelId, "fromDate",
											UtilDateTime.nowTimestamp()));
						} catch (GenericServiceException e) {
							return ServiceUtil
									.returnError("OLBIUS: runsync service error!");
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: Creat configlabel error "
					+ e.toString());
		}
		return successResult;
	}

	public static Map<String, Object> deleteAllConfigLabelByProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productId = (String) context.get("productId");
		EntityCondition Cond1 = EntityCondition.makeCondition("productId",
				EntityOperator.EQUALS, productId);
		List<EntityCondition> listConds = UtilMisc.toList(Cond1);
		try {
			List<GenericValue> listRemoves = delegator.findList("ConfigLabel",
					EntityCondition.makeCondition(listConds), null, null, null,
					false);
			delegator.removeAll(listRemoves);
		} catch (GenericEntityException e) {
			ServiceUtil
					.returnError("OLBIUS: deleteAllConfigLabelByProduct error "
							+ e.toString());
		}
		return successResult;
	}

	public static Map<String, Object> getInventoryItemLabelByProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productId = (String) context.get("productId");
		EntityCondition Cond1 = EntityCondition.makeCondition("productId",
				EntityOperator.EQUALS, productId);
		List<EntityCondition> listConds = UtilMisc.toList(Cond1);
		List<String> listInventoryItemLabelIds = new ArrayList<String>();
		try {
			List<GenericValue> lists = delegator.findList("ConfigLabel", EntityCondition.makeCondition(listConds), null, null, null, false);
			if (!lists.isEmpty()) {
				for (GenericValue item : lists) {
					listInventoryItemLabelIds.add(item.getString("inventoryItemLabelId"));
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: getInventoryItemLabelByProduct error " + e.toString());
		}
		successResult.put("listInventoryItemLabelIds", listInventoryItemLabelIds);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProductRelationships(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		List<GenericValue> list = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			listIterator = delegator.find("ProductRelationshipDetail", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, listSortFields, opts);
			list = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getProductRelationships service: " + e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", list);
		return successResult;
	}

	public static Map<String, Object> createProductRelationship(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productIdFrom = (String) context.get("productIdFrom");
		String inventoryItemLabelIdFrom = (String) context
				.get("inventoryItemLabelIdFrom");

		String productIdTo = (String) context.get("productIdTo");
		String inventoryItemLabelIdTo = (String) context
				.get("inventoryItemLabelIdTo");

		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String description = (String) context.get("description");

		String productRelationshipTypeId = (String) context
				.get("productRelationshipTypeId");
		try {
			GenericValue newRelation = delegator
					.makeValue("ProductRelationship");
			newRelation.put("productIdFrom", productIdFrom);
			newRelation.put("inventoryItemLabelIdFrom",
					inventoryItemLabelIdFrom);
			newRelation.put("productIdTo", productIdTo);
			newRelation.put("inventoryItemLabelIdTo", inventoryItemLabelIdTo);
			newRelation.put("fromDate", fromDate);
			if (UtilValidate.isNotEmpty(thruDate)) {
				newRelation.put("thruDate", thruDate);
			}
			if (UtilValidate.isNotEmpty(description)) {
				newRelation.put("description", description);
			}
			if (UtilValidate.isNotEmpty(productRelationshipTypeId)) {
				newRelation.put("productRelationshipTypeId",
						productRelationshipTypeId);
			}

			delegator.createOrStore(newRelation);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createConfigLabel service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createProductRelationships(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		List<Object> listProductIdFromTmps = (List<Object>) context
				.get("listProductIdFroms");
		List<Object> listProductIdToTmps = (List<Object>) context
				.get("listProductIdTos");

		Boolean isJson = false;
		if (!listProductIdFromTmps.isEmpty()) {
			if (listProductIdFromTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listProductFromIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listProductIdFromTmps.get(0)
					+ "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItem.put("productId", item.getString("productId"));
				}
				listProductFromIds.add(mapItem);
			}
		} else {
			listProductFromIds = (List<Map<String, String>>) context
					.get("listProductIdFroms");
		}

		isJson = false;
		if (!listProductIdToTmps.isEmpty()) {
			if (listProductIdToTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listProductToIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listProductIdToTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItem.put("productId", item.getString("productId"));
				}
				listProductToIds.add(mapItem);
			}
		} else {
			listProductToIds = (List<Map<String, String>>) context
					.get("listProductIdTos");
		}

		List<Object> listInventoryItemLabelIdFromTmps = (List<Object>) context
				.get("listInventoryItemLabelIdFroms");
		isJson = false;
		if (!listInventoryItemLabelIdFromTmps.isEmpty()) {
			if (listInventoryItemLabelIdFromTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIdFroms = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdFromTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIdFroms.add(mapItem);
			}
		} else {
			listInventoryItemLabelIdFroms = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIdFroms");
		}

		List<Object> listInventoryItemLabelIdToTmps = (List<Object>) context
				.get("listInventoryItemLabelIdTos");
		isJson = false;
		if (!listInventoryItemLabelIdToTmps.isEmpty()) {
			if (listInventoryItemLabelIdToTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIdTos = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdToTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIdTos.add(mapItem);
			}
		} else {
			listInventoryItemLabelIdTos = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIdTos");
		}

		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			if (!listInventoryItemLabelIdFroms.isEmpty()
					&& !listProductFromIds.isEmpty()
					&& !listInventoryItemLabelIdTos.isEmpty()
					&& !listProductToIds.isEmpty()) {
				for (Map<String, String> mapProductIdFrom : listProductFromIds) {
					String productIdFrom = mapProductIdFrom.get("productId");
					for (Map<String, String> mapInventotyItemLabelIdFrom : listInventoryItemLabelIdFroms) {
						String inventoryItemLabelIdFrom = mapInventotyItemLabelIdFrom
								.get("inventoryItemLabelId");
						for (Map<String, String> mapProductIdTo : listProductToIds) {
							String productIdTo = mapProductIdTo
									.get("productId");
							for (Map<String, String> mapInventotyItemLabelIdTo : listInventoryItemLabelIdTos) {
								String inventoryItemLabelIdTo = mapInventotyItemLabelIdTo
										.get("inventoryItemLabelId");
								List<GenericValue> list = delegator
										.findList(
												"ProductRelationship",
												EntityCondition.makeCondition(UtilMisc
														.toMap("productIdFrom",
																productIdFrom,
																"inventoryItemLabelIdFrom",
																inventoryItemLabelIdFrom,
																"productIdTo",
																productIdTo,
																"inventoryItemLabelIdTo",
																inventoryItemLabelIdTo)),
												null, null, null, false);
								list = EntityUtil.filterByDate(list);
								if (list.isEmpty()) {
									try {
										dispatcher
												.runSync(
														"createProductRelationship",
														UtilMisc.toMap(
																"userLogin",
																(GenericValue) context
																		.get("userLogin"),
																"productIdFrom",
																productIdFrom,
																"inventoryItemLabelIdFrom",
																inventoryItemLabelIdFrom,
																"productIdTo",
																productIdTo,
																"inventoryItemLabelIdTo",
																inventoryItemLabelIdTo,
																"fromDate",
																UtilDateTime
																		.nowTimestamp()));
									} catch (GenericServiceException e) {
										return ServiceUtil
												.returnError("OLBIUS: runsync service createProductRelationship error!");
									}
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: Creat ProductRelationships error "
					+ e.toString());
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateProductRelationship(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();

		String productIdFrom = (String) context.get("productIdFrom");
		String productIdTo = (String) context.get("productIdTo");

		List<Object> listInventoryItemLabelIdFromTmps = (List<Object>) context
				.get("listInventoryItemLabelIdFroms");
		Boolean isJson = false;
		if (!listInventoryItemLabelIdFromTmps.isEmpty()) {
			if (listInventoryItemLabelIdFromTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIdFroms = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdFromTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIdFroms.add(mapItem);
			}
		} else {
			listInventoryItemLabelIdFroms = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIdFroms");
		}

		List<Object> listInventoryItemLabelIdToTmps = (List<Object>) context
				.get("listInventoryItemLabelIdTos");
		isJson = false;
		if (!listInventoryItemLabelIdToTmps.isEmpty()) {
			if (listInventoryItemLabelIdToTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIdTos = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdToTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIdTos.add(mapItem);
			}
		} else {
			listInventoryItemLabelIdTos = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIdTos");
		}

		try {
			List<GenericValue> listOlds = delegator.findList(
					"ProductRelationship",
					EntityCondition.makeCondition(UtilMisc.toMap(
							"productIdFrom", productIdFrom, "productIdTo",
							productIdTo)), null, null, null, false);
			listOlds = EntityUtil.filterByDate(listOlds);
			List<GenericValue> listIdToRemoves = new ArrayList<GenericValue>();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (GenericValue item : listOlds) {
				Boolean check = false;
				for (Map<String, String> label1 : listInventoryItemLabelIdFroms) {
					String inventoryItemLabelIdFrom = label1
							.get("inventoryItemLabelId");
					List<GenericValue> listConfigLabel = delegator.findList(
							"ConfigLabel", EntityCondition
									.makeCondition(UtilMisc.toMap("productId",
											productIdFrom,
											"inventoryItemLabelId",
											inventoryItemLabelIdFrom)), null,
							null, null, false);
					listConfigLabel = EntityUtil.filterByDate(listConfigLabel);
					if (listConfigLabel.isEmpty()) {
						try {
							dispatcher.runSync("createConfigLabel", UtilMisc
									.toMap("userLogin", (GenericValue) context
											.get("userLogin"), "productId",
											productIdFrom,
											"inventoryItemLabelId",
											inventoryItemLabelIdFrom,
											"fromDate", UtilDateTime
													.nowTimestamp()));
						} catch (GenericServiceException e) {
							return ServiceUtil
									.returnError("OLBIUS: runsync service createConfigLabel error!");
						}
					}
					for (Map<String, String> label2 : listInventoryItemLabelIdTos) {
						String inventoryItemLabelIdTo = label2
								.get("inventoryItemLabelId");
						List<GenericValue> listConfigLabel2 = delegator
								.findList("ConfigLabel", EntityCondition
										.makeCondition(UtilMisc.toMap(
												"productId", productIdTo,
												"inventoryItemLabelId",
												inventoryItemLabelIdTo)), null,
										null, null, false);
						listConfigLabel2 = EntityUtil
								.filterByDate(listConfigLabel2);
						if (listConfigLabel2.isEmpty()) {
							try {
								dispatcher.runSync("createConfigLabel",
										UtilMisc.toMap("userLogin",
												(GenericValue) context
														.get("userLogin"),
												"productId", productIdTo,
												"inventoryItemLabelId",
												inventoryItemLabelIdTo,
												"fromDate", UtilDateTime
														.nowTimestamp()));
							} catch (GenericServiceException e) {
								return ServiceUtil
										.returnError("OLBIUS: runsync service createConfigLabel error!");
							}
						}
						if (inventoryItemLabelIdFrom.equals(item
								.getString("inventoryItemLabelIdFrom"))
								&& inventoryItemLabelIdTo.equals(item
										.getString("inventoryItemLabelIdTo"))) {
							check = false;
						} else {
							check = true;
						}
					}
				}
				if (check) {
					listIdToRemoves.add(item);
				}
			}
			if (!listIdToRemoves.isEmpty()) {
				for (GenericValue item : listIdToRemoves) {
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
			if (!listInventoryItemLabelIdFroms.isEmpty()
					&& UtilValidate.isNotEmpty(productIdFrom)
					&& !listInventoryItemLabelIdTos.isEmpty()
					&& UtilValidate.isNotEmpty(productIdTo)) {
				for (Map<String, String> mapInventotyItemLabelIdFrom : listInventoryItemLabelIdFroms) {
					String inventoryItemLabelIdFrom = mapInventotyItemLabelIdFrom
							.get("inventoryItemLabelId");
					for (Map<String, String> mapInventotyItemLabelIdTo : listInventoryItemLabelIdTos) {
						String inventoryItemLabelIdTo = mapInventotyItemLabelIdTo
								.get("inventoryItemLabelId");
						List<GenericValue> list = delegator.findList(
								"ProductRelationship", EntityCondition
										.makeCondition(UtilMisc.toMap(
												"productIdFrom", productIdFrom,
												"inventoryItemLabelIdFrom",
												inventoryItemLabelIdFrom,
												"productIdTo", productIdTo,
												"inventoryItemLabelIdTo",
												inventoryItemLabelIdTo)), null,
								null, null, false);
						list = EntityUtil.filterByDate(list);
						if (list.isEmpty()) {
							try {
								dispatcher.runSync("createProductRelationship",
										UtilMisc.toMap("userLogin",
												(GenericValue) context
														.get("userLogin"),
												"productIdFrom", productIdFrom,
												"inventoryItemLabelIdFrom",
												inventoryItemLabelIdFrom,
												"productIdTo", productIdTo,
												"inventoryItemLabelIdTo",
												inventoryItemLabelIdTo,
												"fromDate", UtilDateTime
														.nowTimestamp()));
							} catch (GenericServiceException e) {
								return ServiceUtil
										.returnError("OLBIUS: runsync service createProductRelationship error!");
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: Update ProductRelationship error "
					+ e.toString());
		}
		return successResult;
	}

	public static Map<String, Object> deleteProductRelationship(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String productIdFrom = (String) context.get("productIdFrom");
		String inventoryItemLabelIdFrom = (String) context
				.get("inventoryItemLabelIdFrom");
		String productIdTo = (String) context.get("productIdTo");
		String inventoryItemLabelIdTo = (String) context
				.get("inventoryItemLabelIdTo");
		Long tmp = (Long) context.get("fromDate");
		Timestamp fromDate = new Timestamp(tmp);
		try {
			if (UtilValidate.isNotEmpty(fromDate)) {
				GenericValue relation = delegator.findOne(
						"ProductRelationship", false, UtilMisc.toMap(
								"productIdFrom", productIdFrom,
								"inventoryItemLabelIdFrom",
								inventoryItemLabelIdFrom, "productIdTo",
								productIdTo, "inventoryItemLabelIdTo",
								inventoryItemLabelIdTo, "fromDate", fromDate));
				relation.put("thruDate", UtilDateTime.nowTimestamp());
				delegator.store(relation);
			} else {
				List<GenericValue> list = delegator.findList(
						"ProductRelationship", EntityCondition
								.makeCondition(UtilMisc.toMap("productIdFrom",
										productIdFrom,
										"inventoryItemLabelIdFrom",
										inventoryItemLabelIdFrom,
										"productIdTo", productIdTo,
										"inventoryItemLabelIdTo",
										inventoryItemLabelIdTo)), null, null,
						null, false);
				for (GenericValue item : list) {
					item.put("thruDate", UtilDateTime.nowTimestamp());
					delegator.store(item);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling deleteProductRelationship service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	public static Map<String, Object> createInventoryItemLabelType(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String inventoryItemLabelTypeId = (String) context
				.get("inventoryItemLabelTypeId");
		String parentTypeId = (String) context.get("parentTypeId");
		String description = (String) context.get("description");

		try {
			GenericValue check = delegator.findOne("InventoryItemLabelType",
					false, UtilMisc.toMap("inventoryItemLabelTypeId",
							inventoryItemLabelTypeId));
			if (UtilValidate.isEmpty(check)) {
				GenericValue newType = delegator
						.makeValue("InventoryItemLabelType");
				newType.put("inventoryItemLabelTypeId",
						inventoryItemLabelTypeId);
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					newType.put("parentTypeId", parentTypeId);
				}
				if (UtilValidate.isNotEmpty(description)) {
					newType.put("description", description);
				}
				newType.put("hasTable", "N");
				delegator.createOrStore(newType);
			} else {
				ServiceUtil.returnError("OLBIUS: The type has existed");
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createConfigLabel service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		return successResult;
	}

	public static Map<String, Object> getListInventoryLabelTypes(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String inventoryItemLabelTypeId = (String) context
				.get("inventoryItemLabelTypeId");
		String parentTypeId = (String) context.get("parentTypeId");

		try {
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(inventoryItemLabelTypeId)) {
				EntityCondition Cond = EntityCondition.makeCondition(
						"inventoryItemLabelTypeId", EntityOperator.EQUALS,
						inventoryItemLabelTypeId);
				listConds.add(Cond);
			}
			if (UtilValidate.isNotEmpty(parentTypeId)) {
				EntityCondition Cond = EntityCondition.makeCondition(
						"inventoryItemLabelTypeId", EntityOperator.EQUALS,
						inventoryItemLabelTypeId);
				listConds.add(Cond);
			}
			List<GenericValue> list = new ArrayList<GenericValue>();
			if (!listConds.isEmpty()) {
				list = delegator.findList("InventoryItemLabelType",
						EntityCondition.makeCondition(listConds), null, null,
						null, false);
			} else {
				list = delegator.findList("InventoryItemLabelType", null, null,
						null, null, false);
			}
			successResult.put("listInventoryItemLabelTypes", list);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling createConfigLabel service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}

		return successResult;
	}

	public static Map<String, Object> getInventoryAndLabelDetail(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(
				delegator, userLogin.getString("userLoginId"));
		try {
			listTmps = delegator.findList("InventoryItemAndLabelDetail",
					EntityCondition.makeCondition(UtilMisc.toMap(
							"ownerPartyId", company)), null, null, null, false);
			if (!listTmps.isEmpty()) {
				for (GenericValue item : listTmps) {
					EntityCondition Cond1 = EntityCondition.makeCondition(
							"productId", EntityOperator.EQUALS,
							item.get("productId"));
					EntityCondition Cond2 = EntityCondition.makeCondition(
							"facilityId", EntityOperator.EQUALS,
							item.get("facilityId"));
					EntityCondition Cond3;
					if (item.get("statusId") != "null"
							&& item.get("statusId") != null) {
						Cond3 = EntityCondition.makeCondition("statusId",
								EntityOperator.EQUALS, item.get("statusId"));
					} else {
						Cond3 = EntityCondition.makeCondition("statusId",
								EntityOperator.EQUALS, null);
					}
					EntityCondition Cond4 = EntityCondition.makeCondition(
							"expireDate", EntityOperator.EQUALS,
							item.getTimestamp("expireDate"));
					EntityCondition Cond5 = EntityCondition.makeCondition(
							"datetimeManufactured", EntityOperator.EQUALS,
							item.getTimestamp("datetimeManufactured"));
					EntityCondition Cond6 = EntityCondition.makeCondition(
							"ownerPartyId", EntityOperator.EQUALS,
							item.get("ownerPartyId"));
					EntityCondition Cond7 = EntityCondition.makeCondition(
							"lotId", EntityOperator.EQUALS, item.get("lotId"));
					EntityCondition Cond8 = EntityCondition.makeCondition(
							"quantityOnHandTotal", EntityOperator.GREATER_THAN,
							BigDecimal.ZERO);
					List<EntityCondition> listConds = new ArrayList<EntityCondition>();
					listConds.add(Cond8);
					listConds.add(Cond7);
					listConds.add(Cond6);
					listConds.add(Cond5);
					listConds.add(Cond4);
					listConds.add(Cond3);
					listConds.add(Cond2);
					listConds.add(Cond1);
					EntityCondition allConds = EntityCondition.makeCondition(
							listConds, EntityOperator.AND);
					List<GenericValue> listInvs = delegator.findList(
							"InventoryItem", allConds, null, null, null, false);
					Map<String, Object> mapItem = item.getAllFields();
					mapItem.put("quantityOnHandTotal",
							item.getBigDecimal("quantityOnHandTotal"));
					mapItem.put("availabelToPromiseTotal",
							item.getBigDecimal("availabelToPromiseTotal"));
					mapItem.put("inventoryItemLabelDesc",
							item.getString("description"));

					String productId = (String) mapItem.get("productId");
					GenericValue product = delegator.findOne("Product", false,
							UtilMisc.toMap("productId", productId));
					List<String> listUomIds = LogisticsProductUtil
							.getProductPackingUoms(delegator, productId);
					List<Map<String, Object>> mapUoms = new ArrayList<Map<String, Object>>();
					for (String uomId : listUomIds) {
						BigDecimal convert = LogisticsProductUtil
								.getConvertPackingNumber(delegator, productId,
										uomId,
										product.getString("quantityUomId"));
						Map<String, Object> map = FastMap.newInstance();
						map.put("quantityUomId", uomId);
						map.put("convert", convert);
						mapUoms.add(map);
					}

					Boolean checkUsingPurpose = false;
					List<GenericValue> listAppls = delegator.findList(
							"InventoryItemLabelAppl", EntityCondition
									.makeCondition(UtilMisc.toMap(
											"inventoryItemId",
											listInvs.get(0).getString(
													"inventoryItemId"))), null,
							null, null, false);
					List<String> list = new ArrayList<String>();
					for (GenericValue invLb : listAppls) {
						GenericValue label = delegator
								.findOne(
										"InventoryItemLabel",
										false,
										UtilMisc.toMap(
												"inventoryItemLabelId",
												invLb.getString("inventoryItemLabelId")));
						list.add(invLb.getString("inventoryItemLabelId"));
						if ("USING_PURPOSE".equals(label
								.getString("inventoryItemLabelTypeId"))) {
							checkUsingPurpose = true;
						}
					}

					List<GenericValue> listInvLabels = new ArrayList<GenericValue>();
					if (!list.isEmpty()) {
						EntityCondition cond1 = EntityCondition.makeCondition(
								"inventoryItemLabelId", EntityOperator.NOT_IN,
								list);
						List<EntityCondition> listCondLabels = UtilMisc
								.toList(cond1);
						if (checkUsingPurpose) {
							EntityCondition cond2 = EntityCondition
									.makeCondition("inventoryItemLabelTypeId",
											EntityOperator.NOT_EQUAL,
											"USING_PURPOSE");
							listCondLabels.add(cond2);
						}
						EntityCondition allCondLabels = EntityCondition
								.makeCondition(listCondLabels,
										EntityOperator.AND);
						listInvLabels = delegator.findList(
								"InventoryItemLabel", allCondLabels, null,
								null, null, false);
					} else {
						listInvLabels = delegator.findList(
								"InventoryItemLabel", null, null, null, null,
								false);
					}

					List<Map<String, Object>> listLabels = new ArrayList<Map<String, Object>>();
					for (GenericValue label : listInvLabels) {
						Map<String, Object> map = label.getAllFields();
						map.putAll(mapItem);
						map.put("description", label.getString("description"));
						map.put("packingUomIds",
								JsonUtil.convertListMapToJSON(mapUoms));
						listLabels.add(map);
					}
					mapItem.put("rowDetail", listLabels);

					listInventoryItems.add(mapItem);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getInventoryAndLabelDetail service: "
					+ e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listInventoryItems);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetInventoryAndLabelDetail(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");

		List<GenericValue> listTmps = new ArrayList<GenericValue>();
		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company));
		if (listSortFields.isEmpty()) {
			listSortFields.add("facilityId");
		}
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			listIterator = delegator.find("InventoryItemAndLabelDetail", null, EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts);
			listTmps = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
			if (!listTmps.isEmpty()) {
				for (GenericValue item : listTmps) {
					Map<String, Object> mapItem = item.getAllFields();
					mapItem.put("quantityOnHandTotal", item.getBigDecimal("quantityOnHandTotal"));
					mapItem.put("availabelToPromiseTotal", item.getBigDecimal("availabelToPromiseTotal"));
					mapItem.put("amountOnHandTotal", item.getBigDecimal("amountOnHandTotal"));

					String productId = (String) mapItem.get("productId");
					
					List<GenericValue> listConfigLabels = delegator.findList("ConfigLabel", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
					listConfigLabels = EntityUtil.filterByDate(listConfigLabels);
					List<String> listLabelAllows = EntityUtil.getFieldListFromEntityList(listConfigLabels, "inventoryItemLabelId", true);
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					List<String> listUomIds = LogisticsProductUtil.getProductPackingUoms(delegator, productId);
					List<Map<String, Object>> mapUoms = new ArrayList<Map<String, Object>>();
					for (String uomId : listUomIds) {
						BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, uomId, product.getString("quantityUomId"));
						Map<String, Object> map = FastMap.newInstance();
						map.put("quantityUomId", uomId);
						map.put("convert", convert);
						mapUoms.add(map);
					}

					Boolean checkUsingPurpose = false;
					String[] list = new String[] {};
					if (UtilValidate.isNotEmpty(item.getString("inventoryItemLabelId"))) {
						list = item.getString("inventoryItemLabelId").split(", ");
					}
					List<String> stringList = new ArrayList<String>(Arrays.asList(list));

					for (String invLbId : stringList) {
						GenericValue label = delegator.findOne("InventoryItemLabel", false, UtilMisc.toMap("inventoryItemLabelId", invLbId));
						if ("USING_PURPOSE".equals(label.getString("inventoryItemLabelTypeId"))) {
							checkUsingPurpose = true;
						}
					}

					List<GenericValue> listInvLabels = new ArrayList<GenericValue>();
					if (!stringList.isEmpty()) {
						EntityCondition cond1 = EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.NOT_IN, stringList);
						List<EntityCondition> listCondLabels = UtilMisc.toList(cond1);
						if (checkUsingPurpose) {
							EntityCondition cond2 = EntityCondition.makeCondition("inventoryItemLabelTypeId", EntityOperator.NOT_EQUAL, "USING_PURPOSE");
							listCondLabels.add(cond2);
						}
						EntityCondition Cond3 = EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.IN, listLabelAllows);
						listCondLabels.add(Cond3);
						EntityCondition allCondLabels = EntityCondition.makeCondition(listCondLabels, EntityOperator.AND);
						listInvLabels = delegator.findList("InventoryItemLabel", allCondLabels, null, null, null, false);
					} else {
						listInvLabels = delegator.findList("InventoryItemLabel", EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.IN, listLabelAllows), null, null, null, false);
					}

					List<Map<String, Object>> listLabels = new ArrayList<Map<String, Object>>();
					for (GenericValue label : listInvLabels) {
						Map<String, Object> map = label.getAllFields();
						map.putAll(mapItem);
						map.put("description", label.getString("description"));
						map.put("inventoryItemLabelId", label.getString("inventoryItemLabelId"));
						map.put("packingUomIds", JsonUtil.convertListMapToJSON(mapUoms));
						listLabels.add(map);
					}
					mapItem.put("rowDetail", listLabels);

					listInventoryItems.add(mapItem);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getInventoryAndLabelDetail service: " + e.toString();
			ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listInventoryItems);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createInventoryItemLabelAppls(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String inventoryItemId = (String) context.get("inventoryItemId");
		List<Object> listInventoryItemLabelIdTmps = (List<Object>) context
				.get("listInventoryItemLabelIds");
		Boolean isJson = false;
		if (!listInventoryItemLabelIdTmps.isEmpty()) {
			if (listInventoryItemLabelIdTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemLabelIds = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemLabelIdTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemLabelId")) {
					mapItem.put("inventoryItemLabelId",
							item.getString("inventoryItemLabelId"));
				}
				listInventoryItemLabelIds.add(mapItem);
			}
		} else {
			listInventoryItemLabelIds = (List<Map<String, String>>) context
					.get("listInventoryItemLabelIds");
		}
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			if (!listInventoryItemLabelIds.isEmpty()) {
				for (Map<String, String> item : listInventoryItemLabelIds) {
					List<GenericValue> list = delegator.findList(
							"InventoryItemLabelAppl", EntityCondition
									.makeCondition(UtilMisc.toMap(
											"inventoryItemId", inventoryItemId,
											"inventoryItemLabelId",
											item.get("inventoryItemLabelId"))),
							null, null, null, false);
					if (list.isEmpty()) {
						GenericValue label = delegator.findOne(
								"InventoryItemLabel",
								false,
								UtilMisc.toMap("inventoryItemLabelId",
										item.get("inventoryItemLabelId")));
						try {
							dispatcher
									.runSync(
											"createInventoryItemLabelAppl",
											UtilMisc.toMap(
													"userLogin",
													(GenericValue) context
															.get("userLogin"),
													"inventoryItemId",
													inventoryItemId,
													"inventoryItemLabelId",
													item.get("inventoryItemLabelId"),
													"inventoryItemLabelTypeId",
													label.getString("inventoryItemLabelTypeId")));
						} catch (GenericServiceException e) {
							ServiceUtil
									.returnError("OLBIUS createInventoryItemLabelAppl error "
											+ e.toString());
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS" + e.toString());
		}
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInventoryLabelFromRequirement(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");

		EntityCondition quantityCondQOH1 = EntityCondition.makeCondition(
				"quantityOnHandTotal", EntityOperator.GREATER_THAN,
				BigDecimal.ZERO);
		listAllConditions.add(quantityCondQOH1);

		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String requirementId = null;
		if (parameters.get("requirementId") != null
				&& parameters.get("requirementId").length > 0) {
			requirementId = parameters.get("requirementId")[0];
		}
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		
		List<String> listInventoryItemIds = new ArrayList<String>();
		if (UtilValidate.isNotEmpty(requirementId)) {
			try {
				GenericValue requirement = delegator.findOne("Requirement",
						false, UtilMisc.toMap("requirementId", requirementId));
				if (UtilValidate.isNotEmpty(requirement)) {
					String facilityId = requirement.getString("facilityId");
					EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
					EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_CANCELLED");
					List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
					EntityCondition allConds = EntityCondition.makeCondition( listConds, EntityOperator.AND);
					List<GenericValue> listReqItems = delegator.findList("RequirementItem", allConds, null, null, null, false);
					if (!listReqItems.isEmpty()) {
						for (GenericValue item : listReqItems) {
							String productId = item.getString("productId");
							List<GenericValue> listInventoryItemTmps = new ArrayList<GenericValue>();
							List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
							
							List<String> listPrIds = new ArrayList<String>();
							List<String> listInvLabelIds = new ArrayList<String>();
							
							List<String> listPrToIds = new ArrayList<String>();
							List<String> listInvLabelToIds = new ArrayList<String>();
							
							List<GenericValue> listConfigLabels = delegator.findList("ProductRelationship", EntityCondition.makeCondition(UtilMisc
													.toMap("productIdFrom", productId)), null, null, null, false);
							listConfigLabels = EntityUtil.filterByDate(listConfigLabels);
							
							for (GenericValue label : listConfigLabels) {
								Boolean check = false;
								for (String id : listPrIds) {
									if (id.equals(label.getString("productIdFrom")))
										check = true;
									break;
								}
								if (!check) listPrIds.add(label.getString("productIdFrom"));
								
								check = false;
								for (String id : listPrToIds) {
									if (id.equals(label.getString("productIdTo")))
										check = true;
									break;
								}
								if (!check) listPrToIds.add(label.getString("productIdTo"));
							}
							
							if (!listConfigLabels.isEmpty()) {
								for (GenericValue config : listConfigLabels) {
									Boolean check = false;
									for (String labelId : listInvLabelIds) {
										if (config.getString("inventoryItemLabelIdFrom").equals(labelId))
											check = true;
										break;
									}
									if (!check) {
										listInvLabelIds.add(config.getString("inventoryItemLabelIdFrom"));
									}
									
									check = false;
									for (String labelId : listInvLabelToIds) {
										if (config.getString("inventoryItemLabelIdTo").equals(labelId))
											check = true;
										break;
									}
									if (!check) {
										listInvLabelToIds.add(config.getString("inventoryItemLabelIdTo"));
									}
								}
							}
							
							listAllConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listPrIds));
							listAllConds.add(EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.IN, listInvLabelIds));
							listAllConds.add(quantityCondQOH1);
							if (UtilValidate.isNotEmpty(facilityId)) {
								EntityCondition Cond3 = EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId);
								listAllConds.add(Cond3);
							}
							
							listInventoryItemTmps = delegator.findList("InventoryItemAndLabelAppl", EntityCondition.makeCondition(listAllConds), null, null, null, false);
							for (GenericValue temp : listInventoryItemTmps) {
								List<EntityCondition> listAllConds2 = new ArrayList<EntityCondition>();
								listAllConds2.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, temp.getString("inventoryItemId")));
								listAllConds2.add(EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.IN, listInvLabelToIds));
								listAllConds2.add(quantityCondQOH1);
								List<GenericValue> list = delegator.findList("InventoryItemAndLabelAppl", EntityCondition.makeCondition(listAllConds2), null, null, null, false);
								if (list.isEmpty()){
									listInventoryItemIds.add(temp.getString("inventoryItemId"));
								}
							}
						}
					}
					listAllConditions.add(EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, listInventoryItemIds));
					listIterator = delegator.find("InventoryItemAndLabelAppl", EntityCondition .makeCondition(listAllConditions,
											EntityOperator.AND), null, null, listSortFields, opts);
					listInventoryItems = listIterator.getCompleteList();
					listIterator.close();
					for (GenericValue item1 : listInventoryItems) {
						for (GenericValue item2 : listReqItems) {
							if (item1.getString("productId").equals(item2.getString("productId"))) {
								item1.put("quantityTmp",item2.getBigDecimal("quantity"));
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling getInventoryAndLabelDetail service: "
						+ e.toString();
				ServiceUtil.returnError(errMsg);
			}
		}
		successResult.put("listIterator", listInventoryItems);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInventoryLabelToRequirement(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");

		EntityCondition quantityCondQOH1 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);
		listAllConditions.add(quantityCondQOH1);

		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String requirementId = null;
		if (parameters.get("requirementId") != null && parameters.get("requirementId").length > 0) {
			requirementId = parameters.get("requirementId")[0];
		}
		List<GenericValue> listInventoryItems = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(requirementId)) {
			try {
				GenericValue requirement = delegator.findOne("Requirement",
						false, UtilMisc.toMap("requirementId", requirementId));
				if (UtilValidate.isNotEmpty(requirement)) {
					String facilityId = requirement.getString("facilityId");
					EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
					EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_CANCELLED");
					List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
					EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
					List<GenericValue> listReqItems = delegator.findList("RequirementItem", allConds, null, null, null, false);
					if (!listReqItems.isEmpty()) {
						List<String> listPrIds = new ArrayList<String>();
						List<String> listInvLabelIds = new ArrayList<String>();
						for (GenericValue item : listReqItems) {
							String productId = item.getString("productId");
							List<GenericValue> listConfigLabels = delegator
									.findList(
											"ProductRelationship",
											EntityCondition.makeCondition(UtilMisc
													.toMap("productIdFrom",
															productId)), null,
											null, null, false);
							for (GenericValue label : listConfigLabels) {
								Boolean check = false;
								for (String id : listPrIds) {
									if (id.equals(label
											.getString("productIdTo")))
										check = true;
									break;
								}
								if (!check)
									listPrIds.add(label
											.getString("productIdTo"));
							}
							listConfigLabels = EntityUtil
									.filterByDate(listConfigLabels);
							if (!listConfigLabels.isEmpty()) {
								for (GenericValue config : listConfigLabels) {
									Boolean check = false;
									for (String labelId : listInvLabelIds) {
										if (config.getString(
												"inventoryItemLabelIdTo")
												.equals(labelId))
											check = true;
										break;
									}
									if (!check) {
										listInvLabelIds
												.add(config
														.getString("inventoryItemLabelIdTo"));
									}
								}
							}
						}
						listAllConditions.add(EntityCondition.makeCondition(
								"productId", EntityOperator.IN, listPrIds));
						listAllConditions.add(EntityCondition.makeCondition(
								"inventoryItemLabelId", EntityOperator.IN,
								listInvLabelIds));
						if (UtilValidate.isNotEmpty(facilityId)) {
							EntityCondition Cond3 = EntityCondition
									.makeCondition("facilityId",
											EntityOperator.EQUALS, facilityId);
							listAllConditions.add(Cond3);
						}
						listIterator = delegator.find(
								"InventoryItemAndLabelAppl", EntityCondition
										.makeCondition(listAllConditions,
												EntityOperator.AND), null,
								null, listSortFields, opts);
						listInventoryItems = listIterator.getCompleteList();
						listIterator.close();
						for (GenericValue item1 : listInventoryItems) {
							for (GenericValue item2 : listReqItems) {
								List<GenericValue> list = delegator
										.findList(
												"ProductRelationship",
												EntityCondition.makeCondition(UtilMisc
														.toMap("productIdFrom",
																item2.getString("productId"),
																"productIdTo",
																item1.getString("productId"))),
												null, null, null, false);
								for (GenericValue item : list) {
									if (item.getString("productIdFrom").equals(
											item2.getString("productId"))) {
										item1.put("quantityTmp",
												item2.getBigDecimal("quantity"));
									}
								}
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling getInventoryAndLabelDetail service: "
						+ e.toString();
				ServiceUtil.returnError(errMsg);
			}
		}
		successResult.put("listIterator", listInventoryItems);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createInventoryItemLabelApplFromRequirements(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String requirementId = (String) context.get("requirementId");
		List<Object> listInventoryItemIdFromTmps = (List<Object>) context
				.get("listInventoryItemIdFroms");
		Boolean isJson = false;
		if (!listInventoryItemIdFromTmps.isEmpty()) {
			if (listInventoryItemIdFromTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemIds = new ArrayList<Map<String, String>>();
		List<Map<String, String>> listInventoryItemIdFroms = new ArrayList<Map<String, String>>();

		if (isJson) {
			String stringJson = "["
					+ (String) listInventoryItemIdFromTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemId")) {
					mapItem.put("inventoryItemId",
							item.getString("inventoryItemId"));
				}
				if (item.containsKey("productId")) {
					mapItem.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItem.put("quantity", item.getString("quantity"));
				}
				listInventoryItemIdFroms.add(mapItem);
			}
		} else {
			listInventoryItemIdFroms = (List<Map<String, String>>) context
					.get("listInventoryItemIdFroms");
		}

		List<Object> listInventoryItemIdToTmps = (List<Object>) context
				.get("listInventoryItemIdTos");
		isJson = false;
		if (!listInventoryItemIdToTmps.isEmpty()) {
			if (listInventoryItemIdToTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listInventoryItemIdTos = new ArrayList<Map<String, String>>();
		if (isJson) {
			String stringJson = "[" + (String) listInventoryItemIdToTmps.get(0)
					+ "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				HashMap<String, String> mapItem = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("inventoryItemId")) {
					mapItem.put("inventoryItemId",
							item.getString("inventoryItemId"));
				}
				if (item.containsKey("productId")) {
					mapItem.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItem.put("quantity", item.getString("quantity"));
				}
				listInventoryItemIdTos.add(mapItem);
			}
		} else {
			listInventoryItemIdTos = (List<Map<String, String>>) context
					.get("listInventoryItemIdTos");
		}

		listInventoryItemIds.addAll(listInventoryItemIdFroms);
		listInventoryItemIds.addAll(listInventoryItemIdTos);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue system = delegator.findOne("UserLogin", false,
					UtilMisc.toMap("userLoginId", "system"));
			GenericValue userLogin = (GenericValue) context.get("userLogin");

			Map<String, Object> mapShipment = UtilMisc.toMap("requirementId",
					requirementId, "shipmentTypeId", "EXPORT_COMBINE",
					"userLogin", system);
			String shipmentId = null;
			try {
				Map<String, Object> mapResultShipment = dispatcher.runSync(
						"createShipmentFromRequirement", mapShipment);
				shipmentId = (String) mapResultShipment.get("shipmentId");
			} catch (GenericServiceException e) {
				return ServiceUtil
						.returnError("OLBIUS: receiveInventoryProduct error "
								+ e.toString());
			}
			List<GenericValue> listReqItems = delegator.findList(
					"RequirementItem", EntityCondition.makeCondition(UtilMisc
							.toMap("requirementId", requirementId)), null,
					null, null, false);
			if (!listInventoryItemIdTos.isEmpty()) {
				for (Map<String, String> itemTo : listInventoryItemIdTos) {
					String productId = itemTo.get("productId");
					String inventoryItemId = itemTo.get("inventoryItemId");
					BigDecimal quantityExport = new BigDecimal(
							itemTo.get("quantity"));
					BigDecimal quantity = BigDecimal.ZERO;
					String reqItemSeqId = null;
					for (GenericValue item : listReqItems) {
						if (item.getString("productId").equals(productId)) {
							quantity = item.getBigDecimal("quantity");
							reqItemSeqId = item.getString("reqItemSeqId");
							break;
						}
					}
					Map<String, Object> mapShipmentItem = FastMap.newInstance();
					mapShipmentItem.put("shipmentId", shipmentId);
					mapShipmentItem.put("productId", productId);
					mapShipmentItem.put("quantity", quantityExport);
					mapShipmentItem.put("userLogin", system);
					String shipmentItemSeqId = null;
					try {
						Map<String, Object> mapShipmentItemResult = dispatcher
								.runSync("createShipmentItem", mapShipmentItem);
						shipmentItemSeqId = (String) mapShipmentItemResult
								.get("shipmentItemSeqId");
					} catch (GenericServiceException e) {
						return ServiceUtil
								.returnError("OLBIUS - Create shipment item error");
					}

					// Create mapping
					GenericValue requirementItemShipment = delegator
							.makeValue("RequirementShipment");
					requirementItemShipment.put("shipmentId", shipmentId);
					requirementItemShipment.put("shipmentItemSeqId",
							shipmentItemSeqId);
					requirementItemShipment.put("requirementId", requirementId);
					requirementItemShipment.put("reqItemSeqId", reqItemSeqId);
					requirementItemShipment.put("quantity", quantityExport);
					delegator.create(requirementItemShipment);

					// create issues
					Map<String, Object> mapIssuance = FastMap.newInstance();
					mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
					mapIssuance.put("shipmentId", shipmentId);
					mapIssuance.put("inventoryItemId", inventoryItemId);
					mapIssuance.put("quantity", quantity);
					mapIssuance.put("issuedByUserLoginId",
							userLogin.getString("userLoginId"));
					mapIssuance.put("userLogin", system);
					try {
						dispatcher.runSync("createItemIssuance", mapIssuance);
					} catch (GenericServiceException e) {
						return ServiceUtil
								.returnError("OLBIUS - Create item issuance error");
					}
				}
				
				List<Map<String, Object>> listNewInv = new ArrayList<Map<String, Object>>();
				for (GenericValue item : listReqItems) {
					for (Map<String, String> from : listInventoryItemIdFroms) {
						if (from.get("productId").equals(item.getString("productId"))) {
							String productIdFrom = from.get("productId");
							String inventoryItemIdFrom = from.get("inventoryItemId");
							BigDecimal quantityFrom = new BigDecimal(from.get("quantity"));
							String inventoryItemLabelIdFrom = null;
							String inventoryItemLabelIdTo = null;
							for (Map<String, String> to : listInventoryItemIdTos) {
								String productIdTo = to.get("productId");
								List<GenericValue> listRelateds = delegator.findList(
												"ProductRelationship",EntityCondition.makeCondition(UtilMisc
														.toMap("productIdFrom",productIdFrom,"productIdTo",productIdTo)),
												null, null, null, false);
								listRelateds = EntityUtil.filterByDate(listRelateds);
								if (!listRelateds.isEmpty()) {
									inventoryItemLabelIdFrom = listRelateds.get(0).getString("inventoryItemLabelIdFrom");
									inventoryItemLabelIdTo = listRelateds.get(0).getString("inventoryItemLabelIdTo");
								}
							}
							GenericValue labelObjFrom = delegator.findOne("InventoryItemLabel", false, UtilMisc
											.toMap("inventoryItemLabelId", inventoryItemLabelIdFrom));
							GenericValue labelObjTo = delegator.findOne(
									"InventoryItemLabel", false, UtilMisc.toMap("inventoryItemLabelId", inventoryItemLabelIdTo));
							GenericValue inventoryItem = delegator.findOne(
									"InventoryItem", false, UtilMisc.toMap("inventoryItemId",
											inventoryItemIdFrom));
							BigDecimal qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
							if (quantityFrom.compareTo(qoh) > 0)
								ServiceUtil.returnError("OLBIUS: Cannot assign label to quantity greated than quantity on hand total");
							if (quantityFrom.compareTo(qoh) < 0) {
								Map<String, Object> mapShipmentItem = FastMap.newInstance();
								mapShipmentItem.put("shipmentId", shipmentId);
								mapShipmentItem.put("productId", productIdFrom);
								mapShipmentItem.put("quantity", quantityFrom);
								mapShipmentItem.put("userLogin", system);
								String shipmentItemSeqId = null;
								try {
									Map<String, Object> mapShipmentItemResult = dispatcher
											.runSync("createShipmentItem", mapShipmentItem);
									shipmentItemSeqId = (String) mapShipmentItemResult
											.get("shipmentItemSeqId");
								} catch (GenericServiceException e) {
									return ServiceUtil
											.returnError("OLBIUS - Create shipment item error");
								}

								// Create mapping
								GenericValue requirementItemShipment = delegator
										.makeValue("RequirementShipment");
								requirementItemShipment.put("shipmentId", shipmentId);
								requirementItemShipment.put("shipmentItemSeqId",
										shipmentItemSeqId);
								requirementItemShipment.put("requirementId", requirementId);
								requirementItemShipment.put("reqItemSeqId", item.getString("reqItemSeqId"));
								requirementItemShipment.put("quantity", quantityFrom);
								delegator.create(requirementItemShipment);

								// create issues
								Map<String, Object> mapIssuance = FastMap.newInstance();
								mapIssuance.put("shipmentItemSeqId", shipmentItemSeqId);
								mapIssuance.put("shipmentId", shipmentId);
								mapIssuance.put("inventoryItemId", inventoryItemIdFrom);
								mapIssuance.put("quantity", quantityFrom);
								mapIssuance.put("issuedByUserLoginId",
										userLogin.getString("userLoginId"));
								mapIssuance.put("userLogin", system);
								try {
									dispatcher.runSync("createItemIssuance", mapIssuance);
								} catch (GenericServiceException e) {
									return ServiceUtil
											.returnError("OLBIUS - Create item issuance error");
								}
								
								GenericValue invBase = delegator.findOne(
										"InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemIdFrom));
								Map<String, Object> mapNewInv = invBase.getAllFields();
								mapNewInv.remove("inventoryItemId");
								mapNewInv.remove("quantityOnHandTotal");
								mapNewInv.remove("availableToPromiseTotal");
								mapNewInv.put("userLogin", system);
								mapNewInv.put("quantity", quantityFrom);
								mapNewInv.put("inventoryItemLabelIdFrom", inventoryItemLabelIdFrom);
								mapNewInv.put("inventoryItemLabelIdTo", inventoryItemLabelIdTo);
								mapNewInv.put("inventoryItemLabelTypeIdFrom", labelObjFrom.getString("inventoryItemLabelTypeId"));
								mapNewInv.put("inventoryItemLabelTypeIdTo", labelObjTo.getString("inventoryItemLabelTypeId"));
								listNewInv.add(mapNewInv);
								
							} else {
								try {
									dispatcher.runSync("createInventoryItemLabelAppl", UtilMisc.toMap(
															"userLogin", userLogin,
															"inventoryItemId", inventoryItemIdFrom,
															"inventoryItemLabelId", inventoryItemLabelIdTo,
															"inventoryItemLabelTypeId", labelObjTo.getString("inventoryItemLabelTypeId")));
								} catch (GenericServiceException e) {
									ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error! " + e.toString());
								}
							}
						}
					}
				}
				
				// update shipment to packed
				mapShipment = FastMap.newInstance();
				mapShipment.put("userLogin", system);
				mapShipment.put("shipmentId", shipmentId);
				mapShipment.put("statusId", "SHIPMENT_PACKED");
				try {
					dispatcher.runSync("updateShipment", mapShipment);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
				}
				// update shipment to shipped
				mapShipment = FastMap.newInstance();
				mapShipment.put("userLogin", system);
				mapShipment.put("shipmentId", shipmentId);
				mapShipment.put("statusId", "SHIPMENT_SHIPPED");
				try {
					dispatcher.runSync("updateShipment", mapShipment);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
				}
				
				for (Map<String, Object> mapNewInv : listNewInv){
					String invNewId = null;
					try {
						Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
						invNewId = (String) mapTmp.get("inventoryItemId");
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: createInventoryItem runsync service error!");
					}
					BigDecimal quantity = (BigDecimal)mapNewInv.get("quantity");
					String inventoryItemLabelIdFrom = (String)mapNewInv.get("inventoryItemLabelIdFrom");
					String inventoryItemLabelIdTo = (String)mapNewInv.get("inventoryItemLabelIdTo");
					String inventoryItemLabelTypeIdFrom = (String)mapNewInv.get("inventoryItemLabelIdFrom");
					String inventoryItemLabelTypeIdTo = (String)mapNewInv.get("inventoryItemLabelIdTo");
					
					if (UtilValidate.isNotEmpty(invNewId)) {
						Map<String, Object> mapDetail2 = FastMap.newInstance();
						mapDetail2.put("inventoryItemId", invNewId);
						mapDetail2.put("quantityOnHandDiff", quantity);
						mapDetail2.put("availableToPromiseDiff", quantity);
						mapDetail2.put("userLogin", system);
						try {
							dispatcher.runSync("createInventoryItemDetail", mapDetail2);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
						}
						try {
							dispatcher.runSync(
											"createInventoryItemLabelAppl",
											UtilMisc.toMap("userLogin", userLogin, "inventoryItemId", invNewId,
													"inventoryItemLabelId", inventoryItemLabelIdFrom, "inventoryItemLabelTypeId",
													inventoryItemLabelTypeIdFrom));
						} catch (GenericServiceException e) {
							ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error! " + e.toString());
						}
						try {
							dispatcher.runSync("createInventoryItemLabelAppl", UtilMisc.toMap(
													"userLogin", userLogin, "inventoryItemId", invNewId,
													"inventoryItemLabelId", inventoryItemLabelIdTo,
													"inventoryItemLabelTypeId", inventoryItemLabelTypeIdTo));
						} catch (GenericServiceException e) {
							ServiceUtil.returnError("OLBIUS: createInventoryItemLabelAppl error! " + e.toString());
						}
					}
				}
				
				for (GenericValue item : listReqItems) {
					BigDecimal actualExecutedQuantity = item.getBigDecimal("actualExecutedQuantity");
					BigDecimal quantityToExport = BigDecimal.ZERO;
					for (Map<String, String> actual : listInventoryItemIds) {
						if (actual.get("productId").equals(item.getString("productId"))) {
							quantityToExport = quantityToExport.add(new BigDecimal(actual.get("quantity")));
						}
					}
					if (quantityToExport.compareTo(BigDecimal.ZERO) > 0) {
						if (UtilValidate.isNotEmpty(actualExecutedQuantity)) {
							if (item.getBigDecimal("quantity").compareTo(actualExecutedQuantity.add(quantityToExport)) > 0) {
								item.put("actualExecutedQuantity", actualExecutedQuantity.add(quantityToExport));
								delegator.store(item);
							} else {
								item.put("actualExecutedQuantity", item.getBigDecimal("quantity"));
								delegator.store(item);
								try {
									dispatcher.runSync("changeRequirementItemStatus",
													UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin"),
															"requirementId", requirementId,
															"reqItemSeqId", item.getString("reqItemSeqId"),
															"statusId", "REQ_COMPLETED"));
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: runsync service changeRequirementItemStatus error!");
								}
							}
						} else {
							if (item.getBigDecimal("quantity").compareTo(quantityToExport) > 0) {
								item.put("actualExecutedQuantity", quantityToExport);
								delegator.store(item);
							} else {
								item.put("actualExecutedQuantity", item.getBigDecimal("quantity"));
								delegator.store(item);
								try {
									dispatcher.runSync("changeRequirementItemStatus",
													UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin"),
															"requirementId", requirementId,
															"reqItemSeqId", item.getString("reqItemSeqId"),
															"statusId", "REQ_COMPLETED"));
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: runsync service changeRequirementItemStatus error!");
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS");
		}
		successResult.put("requirementId", requirementId);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> assignLabelInventorys (
			DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Object> listProductTmps = (List<Object>) context
				.get("listProducts");
		Boolean isJson = false;
		if (!listProductTmps.isEmpty()) {
			if (listProductTmps.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, String>> listProducts = new ArrayList<Map<String, String>>();

		if (isJson) {
			String stringJson = "[" + (String) listProductTmps.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				JSONObject item = lists.getJSONObject(i);
				Map<String, String> mapItems = new HashMap<String, String>();
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("statusId")) {
					mapItems.put("statusId", item.getString("statusId"));
				}
				if (item.containsKey("expireDate")) {
					mapItems.put("expireDate", item.getString("expireDate"));
				}
				if (item.containsKey("datetimeManufactured")) {
					mapItems.put("datetimeManufactured",
							item.getString("datetimeManufactured"));
				}
				if (item.containsKey("ownerPartyId")) {
					mapItems.put("ownerPartyId", item.getString("ownerPartyId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("lotId")) {
					mapItems.put("lotId", item.getString("lotId"));
				}
				if (item.containsKey("inventoryItemLabelIds")) {
					mapItems.put("inventoryItemLabelIds",
							item.getString("inventoryItemLabelIds"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId",
							item.getString("quantityUomId"));
				}
				if (item.containsKey("listCurrentLabelIds")) {
					mapItems.put("listCurrentLabelIds",
							item.getString("listCurrentLabelIds"));
				}
				
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, String>>) context
					.get("listProducts");
		}
		GenericValue system = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		for (Map<String, String> item : listProducts) {
			BigDecimal quantity = new BigDecimal((String) item.get("quantity"));
			EntityCondition Cond1 = EntityCondition.makeCondition("productId",
					EntityOperator.EQUALS, item.get("productId"));
			EntityCondition Cond2 = EntityCondition.makeCondition("facilityId",
					EntityOperator.EQUALS, item.get("facilityId"));
			EntityCondition Cond3;
			if (item.get("statusId") != "null" && item.get("statusId") != null) {
				Cond3 = EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, item.get("statusId"));
			} else {
				Cond3 = EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, null);
			}
			EntityCondition Cond10 = null;
			List<String> listCurrentLabels = new ArrayList<String>();
			if (UtilValidate.isNotEmpty(item.get("listCurrentLabelIds")) && !"null".equals(item.get("listCurrentLabelIds"))){
				JSONArray lists = JSONArray.fromObject(item.get("listCurrentLabelIds"));
				for (int i=0; i< lists.size(); i++) {
					listCurrentLabels.add(lists.getString(i) );
				}
				List<EntityCondition> listConds = UtilMisc.toList(Cond1);
				listConds.add(EntityCondition.makeCondition("inventoryItemLabelId",EntityOperator.IN, listCurrentLabels));
				EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
				List<GenericValue> listInvLabelApplied = delegator.findList("InventoryItemAndLabelAppl", allConds, null, null, null, false);
				List<GenericValue> listFilter = new ArrayList<GenericValue>();
				List<GenericValue> listOtherLabelTypes = delegator.findList("InventoryItemLabel", EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.NOT_IN, listCurrentLabels), null, null, null, false);
				List<String> listLabelTypeIdOthers = EntityUtil.getFieldListFromEntityList(listOtherLabelTypes, "inventoryItemLabelId", true);
				for (GenericValue invAppl : listInvLabelApplied){
					EntityCondition Condt = EntityCondition.makeCondition("inventoryItemId",EntityOperator.EQUALS, invAppl.getString("inventoryItemId"));
					EntityCondition Condy = EntityCondition.makeCondition("inventoryItemLabelId", EntityOperator.IN, listLabelTypeIdOthers);
					List<EntityCondition> listOtherConds = UtilMisc.toList(Condt, Condy);
					EntityCondition condOthers = EntityCondition.makeCondition(listOtherConds, EntityOperator.AND);
					List<GenericValue> listCheck = delegator.findList("InventoryItemLabelAppl", EntityCondition.makeCondition(condOthers), null, null, null, false);
					if (!listCheck.isEmpty() && listCheck.size() > 1){
						listFilter.add(invAppl);
					}
				}
				if (!listFilter.isEmpty()){
					listInvLabelApplied.removeAll(listFilter);
				}
				if (!listInvLabelApplied.isEmpty()){
					List<String> listInvItemIds = EntityUtil.getFieldListFromEntityList(listInvLabelApplied, "inventoryItemId", true);
					Cond10 = EntityCondition.makeCondition("inventoryItemId", EntityOperator.IN, listInvItemIds);
				} else {
					return ServiceUtil.returnError("OLBIUS_NOT_FOUND_INVENTORY_WITH_LABEL_CHOOSED");
				}
			}
			
			JSONArray lists = JSONArray.fromObject(item.get("inventoryItemLabelIds"));
			for (int i=0; i< lists.size(); i++) {
				if (!listCurrentLabels.contains(lists.getString(i))){
					listCurrentLabels.add(lists.getString(i));
				}
			}
			
			EntityCondition Cond4;
			if (UtilValidate.isNotEmpty(item.get("expireDate"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("expireDate")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond5;
			if (UtilValidate.isNotEmpty(item.get("datetimeManufactured"))){
				Timestamp tmp = new Timestamp(new Long((String) item.get("datetimeManufactured")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				Cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				Cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
			}
			EntityCondition Cond6 = EntityCondition.makeCondition(
					"ownerPartyId", EntityOperator.EQUALS,
					item.get("ownerPartyId"));
			EntityCondition Cond7 = EntityCondition.makeCondition("lotId",
					EntityOperator.EQUALS, item.get("lotId"));
			EntityCondition Cond8 = EntityCondition.makeCondition(
					"quantityOnHandTotal",
					EntityOperator.GREATER_THAN_EQUAL_TO, quantity);
			EntityCondition Cond9 = EntityCondition.makeCondition(
					"quantityOnHandTotal", EntityOperator.GREATER_THAN,
					BigDecimal.ZERO);
			try {
				List<EntityCondition> listConds = new ArrayList<>();
				listConds = new ArrayList<>();
				listConds.add(Cond9);
				listConds.add(Cond8);
				listConds.add(Cond7);
				listConds.add(Cond6);
				listConds.add(Cond5);
				listConds.add(Cond4);
				listConds.add(Cond3);
				listConds.add(Cond2);
				listConds.add(Cond1);
				if (UtilValidate.isNotEmpty(Cond10)){
					listConds.add(Cond10);
				}
				EntityCondition allConds = EntityCondition.makeCondition(
						listConds, EntityOperator.AND);
				List<GenericValue> listInventory = delegator.findList(
						"InventoryItem", allConds, null, null, null, false);

				List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
				if (!listInventory.isEmpty()) {
					GenericValue inv = listInventory.get(0);
					String inventoryItemId = inv.getString("inventoryItemId");
					String invNewId = null;
					if (inv.getBigDecimal("quantityOnHandTotal").compareTo(quantity) == 0){
						invNewId = inventoryItemId;
					} else {
						Map<String, Object> mapDetail = FastMap.newInstance();
						mapDetail.put("userLogin", system);
						mapDetail.put("inventoryItemId", inventoryItemId);
						mapDetail.put("quantityOnHandDiff", quantity.abs().negate());
						mapDetail.put("availableToPromiseDiff", quantity.abs().negate());
						try {
							dispatcher.runSync("createInventoryItemDetail", mapDetail);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error!");
						}
						
						Map<String, Object> mapNewInv = inv.getAllFields();
						mapNewInv.remove("inventoryItemId");
						mapNewInv.remove("quantityOnHandTotal");
						mapNewInv.remove("availableToPromiseTotal");
						mapNewInv.put("userLogin", system);
						Map<String, Object> mapTmp;
						try {
							mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createInventoryItem error!");
						}
						invNewId = (String) mapTmp.get("inventoryItemId");
						
						mapDetail = FastMap.newInstance();
						mapDetail.put("userLogin", system);
						mapDetail.put("inventoryItemId", invNewId);
						mapDetail.put("quantityOnHandDiff", quantity.abs());
						mapDetail.put("availableToPromiseDiff", quantity.abs());
						try {
							dispatcher.runSync("createInventoryItemDetail", mapDetail);
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error!");
						}
					}
					
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("inventoryItemId", invNewId);
					map.put("quantity", quantity);
					listInventoryItems.add(map);
				} else {
					listConds = new ArrayList<>();
					listConds.add(Cond7);
					listConds.add(Cond6);
					listConds.add(Cond5);
					listConds.add(Cond4);
					listConds.add(Cond3);
					listConds.add(Cond2);
					listConds.add(Cond1);
					listConds.add(Cond9);
					if (UtilValidate.isNotEmpty(Cond10)){
						listConds.add(Cond10);
					}
					allConds = EntityCondition.makeCondition(listConds,
							EntityOperator.AND);
					listInventory = delegator.findList("InventoryItem",
							allConds, null, null, null, false);
					if (!listInventory.isEmpty()) {
						BigDecimal qohTotal = BigDecimal.ZERO;
						for (GenericValue inv : listInventory) {
							qohTotal = qohTotal.add(inv
									.getBigDecimal("quantityOnHandTotal"));
						}
						if (qohTotal.compareTo(quantity.abs()) >= 0) {
							BigDecimal remainQuantity = quantity.abs();
							for (GenericValue inv : listInventory) {
								BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
								String inventoryItemId = inv.getString("inventoryItemId");
								Map<String, Object> map = FastMap.newInstance();
								map.putAll(item);
								if (remainQuantity.compareTo(qoh) >= 0) {
									map.put("quantity", qoh.toString());
									remainQuantity = remainQuantity.subtract(qoh);
									map.put("inventoryItemId", inventoryItemId);
								} else {
									map.put("quantity", remainQuantity.toString());
									
									Map<String, Object> mapDetail = FastMap.newInstance();
									mapDetail.put("userLogin", system);
									mapDetail.put("inventoryItemId", inventoryItemId);
									mapDetail.put("quantityOnHandDiff", remainQuantity.abs().negate());
									mapDetail.put("availableToPromiseDiff", remainQuantity.abs().negate());
									try {
										dispatcher.runSync("createInventoryItemDetail", mapDetail);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error!");
									}
									
									Map<String, Object> mapNewInv = inv.getAllFields();
									mapNewInv.remove("inventoryItemId");
									mapNewInv.remove("quantityOnHandTotal");
									mapNewInv.remove("availableToPromiseTotal");
									mapNewInv.put("userLogin", system);
									String invNewId = null;
									Map<String, Object> mapTmp;
									try {
										mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: createInventoryItem error!");
									}
									invNewId = (String) mapTmp.get("inventoryItemId");
									
									mapDetail = FastMap.newInstance();
									mapDetail.put("userLogin", system);
									mapDetail.put("inventoryItemId", invNewId);
									mapDetail.put("quantityOnHandDiff", remainQuantity.abs());
									mapDetail.put("availableToPromiseDiff", remainQuantity.abs());
									try {
										dispatcher.runSync("createInventoryItemDetail", mapDetail);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error!");
									}
									remainQuantity = BigDecimal.ZERO;
									map.put("inventoryItemId", invNewId);
								}
								listInventoryItems.add(map);
								if (remainQuantity.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}
							}
						} else {
							return ServiceUtil
									.returnError("QUANTITY_NOT_ENOUGH");
						}
					} else {
						return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
					}
				}
				if (!listInventoryItems.isEmpty()) {
					for (Map<String, Object> inv : listInventoryItems) {
						try {
							List<Map<String, String>> listTmp = new ArrayList<Map<String, String>>();
							for (String invLabelId : listCurrentLabels) {
								Map<String, String> map = UtilMisc.toMap("inventoryItemLabelId", invLabelId);
								listTmp.add(map);
							}
							dispatcher.runSync("createInventoryItemLabelAppls",
									UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin"),
											"inventoryItemId", inv.get("inventoryItemId"),
											"listInventoryItemLabelIds", listTmp));
						} catch (GenericServiceException e) {
							return ServiceUtil
									.returnError("OLBIUS: runsync service createInventoryItemLabelAppls error!");
						}
					}
				}

			} catch (GenericEntityException e) {
				return ServiceUtil
						.returnError("OLBIUS: assignLabelInventorys error!");
			}
		}
		return successResult;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetInventoryForecast(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String facilityId = null;
		if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("facilityId")[0])) {
				facilityId = parameters.get("facilityId")[0];
			}
		}
		String fromDateTmp = null;
		if (parameters.get("fromDate") != null && parameters.get("fromDate").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("fromDate")[0])) {
				fromDateTmp = parameters.get("fromDate")[0];
			}
		}
		String thruDateTmp = null;
		if (parameters.get("thruDate") != null && parameters.get("thruDate").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("thruDate")[0])) {
				thruDateTmp = parameters.get("thruDate")[0];
			}
		}
		String checkExpiredDate = null;
		if (parameters.get("checkExpiredDate") != null && parameters.get("checkExpiredDate").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("checkExpiredDate")[0])) {
				checkExpiredDate = parameters.get("checkExpiredDate")[0];
			}
		}
		
		if (UtilValidate.isEmpty(fromDateTmp) && UtilValidate.isEmpty(thruDateTmp)) {
			return successResult;
		}
		
    	Long fromDateLong = new Long(fromDateTmp);
    	Long thruDateLong = new Long(thruDateTmp);
    	
    	Timestamp fromDate = new Timestamp(fromDateLong);
    	Timestamp thruDate = new Timestamp(thruDateLong);
    	
    	if (fromDate.equals(thruDate)){
    		Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(thruDate.getTime());
            cal.add(Calendar.SECOND, 1);
            thruDate = new Timestamp(cal.getTime().getTime());
    	}
    	
    	List<String> listProductIds = new ArrayList<String>();
    	List<String> listProductIdActives = new ArrayList<String>();
    	List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
    	List<Map<String, Object>> listProductActives = new ArrayList<Map<String, Object>>();
    	
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String org = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	
    	List<GenericValue> listProductTmps = new ArrayList<GenericValue>();
    	EntityListIterator listIterator = null;
    	
    	listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("isVirtual", "N")));
    	
    	try {
    		listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
			listProductTmps = LogisticsUtil.getIteratorPartialList(listIterator, parameters, successResult);
    		for (GenericValue item : listProductTmps) {
				listProductIds.add(item.getString("productId"));
				Map<String, Object> map = FastMap.newInstance();
				map.put("productId", item.getString("productId"));
				map.put("productCode", item.getString("productCode"));
				map.put("productName", item.getString("productName"));
				map.put("quantityUomId", item.getString("quantityUomId"));
				listProducts.add(map);
			}
    	} catch (GenericEntityException e){
    		e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: OLBIUS inventoryForecast error!");
    	}
    	
		EntityCondition CondInit1 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
		EntityCondition CondInit3 = EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds);
		
		EntityCondition CondInit21 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INV_AVAILABLE");
		EntityCondition CondInit22 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
		EntityCondition CondInit23 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INV_NS_RETURNED");
		
		EntityCondition CondInit2 = EntityCondition.makeCondition(UtilMisc.toList(CondInit21, CondInit22, CondInit23), EntityOperator.OR);
		List<EntityCondition> listInvInitConds = UtilMisc.toList(CondInit1, CondInit2, CondInit3);
		
		if (UtilValidate.isNotEmpty(facilityId)){
			EntityCondition CondInit4 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
			listInvInitConds.add(CondInit4);
		}
    	
    	String entity = "InventoryItemGroupByProductOwnerFacilityStatus";
    	EntityCondition allInvInitConds = EntityCondition.makeCondition(listInvInitConds, EntityOperator.AND);
    	List<GenericValue> listCurrentInvs = new ArrayList<GenericValue>();
    	try {
    		listCurrentInvs = delegator.findList(entity, EntityCondition.makeCondition(allInvInitConds), null, null, null, false);
    	} catch (GenericEntityException e){
    		e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: OLBIUS inventoryForecast error!");
    	}
    	
    	// Calculate order purchase
    	
    	EntityCondition PO_CondType = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER");
    	EntityCondition PO_CondStatus = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED");

    	EntityCondition PO_Avail_CondTimeExac1 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition PO_Avail_CondTimeExac2 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
    	EntityCondition PO_Avail_CondTimeExac3 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition PO_Avail_CondTimeExac = EntityCondition.makeCondition(UtilMisc.toList(PO_Avail_CondTimeExac1, PO_Avail_CondTimeExac2, PO_Avail_CondTimeExac3));
    	
    	EntityCondition PO_Avail_CondTimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition PO_Avail_CondTimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.GREATER_THAN, fromDate);
    	EntityCondition PO_Avail_CondTimeBefore3 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
    	EntityCondition PO_Avail_CondTimeBefore = EntityCondition.makeCondition(UtilMisc.toList(PO_Avail_CondTimeBefore1, PO_Avail_CondTimeBefore2, PO_Avail_CondTimeBefore3));
    	
    	EntityCondition PO_Avail_CondTimeAfter1 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition PO_Avail_CondTimeAfter2 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
    	EntityCondition PO_Avail_CondTimeAfter3 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition PO_Avail_CondTimeAfter = EntityCondition.makeCondition(UtilMisc.toList(PO_Avail_CondTimeAfter1, PO_Avail_CondTimeAfter2, PO_Avail_CondTimeAfter3));
    	
    	List<EntityCondition> listPOTimeAvailConds = UtilMisc.toList(PO_Avail_CondTimeExac1, PO_Avail_CondTimeExac, PO_Avail_CondTimeBefore, PO_Avail_CondTimeAfter);
    	EntityCondition allPOTimeAvailConds = EntityCondition.makeCondition(listPOTimeAvailConds, EntityOperator.OR);
    	
    	List<EntityCondition> listPOAvailConds = UtilMisc.toList(PO_CondType, PO_CondStatus, allPOTimeAvailConds);
    	listPOAvailConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
    	EntityCondition allPOAvailableConds = EntityCondition.makeCondition(listPOAvailConds, EntityOperator.AND);
    	
    	List<GenericValue> listAvailablePOs = new ArrayList<GenericValue>();
    	try {
    		listAvailablePOs = delegator.findList("OrderItemMini", EntityCondition.makeCondition(allPOAvailableConds), null, null, null, false);
    		for (GenericValue item : listAvailablePOs) {
    			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: get orderitemmini error!");
		}
    	
    	EntityCondition PO_WBe_Recive_CondTimeExac1 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition PO_WBe_Recive_CondTimeExac2 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, fromDate);
    	EntityCondition PO_WBe_Recive_CondTimeExac = EntityCondition.makeCondition(UtilMisc.toList(PO_WBe_Recive_CondTimeExac1, PO_WBe_Recive_CondTimeExac2));
    	
    	EntityCondition PO_WBe_Recive_CondTimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition PO_WBe_Recive_CondTimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
    	EntityCondition PO_WBe_Recive_CondTimeBefore = EntityCondition.makeCondition(UtilMisc.toList(PO_WBe_Recive_CondTimeBefore1, PO_WBe_Recive_CondTimeBefore2));
    	
    	List<EntityCondition> listPOTimeWBeReceiveConds = UtilMisc.toList(PO_WBe_Recive_CondTimeExac, PO_WBe_Recive_CondTimeBefore);
    	EntityCondition allPOTimeWBeReceiveConds = EntityCondition.makeCondition(listPOTimeWBeReceiveConds, EntityOperator.OR);
    	
    	List<EntityCondition> listPOWBeReceiveConds = UtilMisc.toList(PO_CondType, PO_CondStatus, allPOTimeWBeReceiveConds);
    	listPOWBeReceiveConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
    	EntityCondition allPOWBeReciveConds = EntityCondition.makeCondition(listPOWBeReceiveConds, EntityOperator.AND);
    	List<GenericValue> listWBeReceivePOs = new ArrayList<GenericValue>();
    	try {
    		listWBeReceivePOs = delegator.findList("OrderItemMini", EntityCondition.makeCondition(allPOWBeReciveConds), null, null, null, false);
    		for (GenericValue item : listWBeReceivePOs) {
    			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: get orderitemmini error!");
		}
    	
    	// Calculate order sales 
    	
    	EntityCondition SO_CondType = EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER");
    	EntityCondition SO_CondStatus = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED");

    	EntityCondition SO_Avail_CondTimeExac1 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition SO_Avail_CondTimeExac2 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
    	EntityCondition SO_Avail_CondTimeExac3 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition SO_Avail_CondTimeExac = EntityCondition.makeCondition(UtilMisc.toList(SO_Avail_CondTimeExac1, SO_Avail_CondTimeExac2, SO_Avail_CondTimeExac3));
    	
    	EntityCondition SO_Avail_CondTimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition SO_Avail_CondTimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.GREATER_THAN, fromDate);
    	EntityCondition SO_Avail_CondTimeBefore3 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
    	EntityCondition SO_Avail_CondTimeBefore = EntityCondition.makeCondition(UtilMisc.toList(SO_Avail_CondTimeBefore1, SO_Avail_CondTimeBefore2, SO_Avail_CondTimeBefore3));
    	
    	EntityCondition SO_Avail_CondTimeAfter1 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition SO_Avail_CondTimeAfter2 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
    	EntityCondition SO_Avail_CondTimeAfter3 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition SO_Avail_CondTimeAfter = EntityCondition.makeCondition(UtilMisc.toList(SO_Avail_CondTimeAfter1, SO_Avail_CondTimeAfter2, SO_Avail_CondTimeAfter3));
    	
    	List<EntityCondition> listSOTimeAvailConds = UtilMisc.toList(SO_Avail_CondTimeExac, SO_Avail_CondTimeBefore, SO_Avail_CondTimeAfter);
    	EntityCondition allSOTimeAvailConds = EntityCondition.makeCondition(listSOTimeAvailConds, EntityOperator.OR);
    	
    	List<EntityCondition> listSOAvailConds = UtilMisc.toList(SO_CondType, SO_CondStatus, allSOTimeAvailConds);
    	listSOAvailConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
    	
    	EntityCondition allSOAvailableConds = EntityCondition.makeCondition(listSOAvailConds, EntityOperator.AND);
    	
    	List<GenericValue> listAvailableSOs = new ArrayList<GenericValue>();
    	try {
    		listAvailableSOs = delegator.findList("OrderItemMini", EntityCondition.makeCondition(allSOAvailableConds), null, null, null, false);
    		for (GenericValue item : listAvailableSOs) {
    			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: get orderitemmini error!");
		}
    	
    	EntityCondition SO_WBe_Sold_CondTimeExac1 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition SO_WBe_Sold_CondTimeExac2 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, fromDate);
    	EntityCondition SO_WBe_Sold_CondTimeExac3 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition SO_WBe_Sold_CondTimeExac = EntityCondition.makeCondition(UtilMisc.toList(SO_WBe_Sold_CondTimeExac1, SO_WBe_Sold_CondTimeExac2, SO_WBe_Sold_CondTimeExac3));
    	
    	EntityCondition SO_WBe_Sold_CondTimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition SO_WBe_Sold_CondTimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
    	EntityCondition SO_WBe_Sold_CondTimeBefore = EntityCondition.makeCondition(UtilMisc.toList(SO_WBe_Sold_CondTimeBefore1, SO_WBe_Sold_CondTimeBefore2));
    	
    	List<EntityCondition> listSOTimeWBeSoldConds = UtilMisc.toList(SO_WBe_Sold_CondTimeExac, SO_WBe_Sold_CondTimeBefore);
    	EntityCondition allSOTimeWBeSoldConds = EntityCondition.makeCondition(listSOTimeWBeSoldConds, EntityOperator.OR);
    	
    	List<EntityCondition> listSOWBeSoldConds = UtilMisc.toList(SO_CondType, SO_CondStatus, allSOTimeWBeSoldConds);
    	listSOWBeSoldConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
    	EntityCondition allSOWBeSoldConds = EntityCondition.makeCondition(listSOWBeSoldConds, EntityOperator.AND);
    	List<GenericValue> listWBeSoldSOs = new ArrayList<GenericValue>();
    	try {
    		listWBeSoldSOs = delegator.findList("OrderItemMini", EntityCondition.makeCondition(allSOWBeSoldConds), null, null, null, false);
    		for (GenericValue item : listWBeSoldSOs) {
    			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: get orderitemmini error!");
		}
    	
    	// transfer
    	List<GenericValue> listTransferOriginInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferDestInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferItemReceiveInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferItemExportInPeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listTransferExptInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferItemReceiveExptInPeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listTransferExptBeforePeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferItemReceiveExptBeforePeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listTransferOriginBeforePeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferDestBeforePeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listTransferItemReceiveBeforePeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listTransferItemExportBeforePeriods = new ArrayList<GenericValue>();
    	
    	if (UtilValidate.isNotEmpty(facilityId)){
    		EntityCondition TRF_CondType = EntityCondition.makeCondition("transferTypeId", EntityOperator.EQUALS, "TRANS_INTERNAL");
        	EntityCondition TRF_CondStatus = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANSFER_APPROVED");
        	EntityCondition TRF_CondStatusExpt = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANSFER_EXPORTED");
        	EntityCondition TRF_CondFAOrigin = EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId);
    		EntityCondition TRF_CondFADest = EntityCondition.makeCondition("destFacilityId", EntityOperator.EQUALS, facilityId);
    		
        	// transfer in period
        	EntityCondition TRF_IN_PER_TimeExac1 = EntityCondition.makeCondition("transferDate", EntityOperator.NOT_EQUAL, null);
        	EntityCondition TRF_IN_PER_TimeExac2 = EntityCondition.makeCondition("transferDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
        	EntityCondition TRF_IN_PER_TimeExac3 = EntityCondition.makeCondition("transferDate", EntityOperator.LESS_THAN, thruDate);
        	EntityCondition TRF_IN_PER_TimeExac = EntityCondition.makeCondition(UtilMisc.toList(TRF_IN_PER_TimeExac1, TRF_IN_PER_TimeExac2, TRF_IN_PER_TimeExac3));
        	
        	EntityCondition TRF_IN_PER_TimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
        	EntityCondition TRF_IN_PER_TimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.GREATER_THAN, fromDate);
        	EntityCondition TRF_IN_PER_TimeBefore3 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
        	EntityCondition TRF_IN_PER_TimeBefore = EntityCondition.makeCondition(UtilMisc.toList(TRF_IN_PER_TimeBefore1, TRF_IN_PER_TimeBefore2, TRF_IN_PER_TimeBefore3));
        	
        	EntityCondition TRF_IN_PER_TimeAfter1 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.NOT_EQUAL, null);
        	EntityCondition TRF_IN_PER_TimeAfter2 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
        	EntityCondition TRF_IN_PER_TimeAfter3 = EntityCondition.makeCondition("shipAfterDate", EntityOperator.LESS_THAN, thruDate);
        	EntityCondition TRF_IN_PER_TimeAfter = EntityCondition.makeCondition(UtilMisc.toList(TRF_IN_PER_TimeAfter1, TRF_IN_PER_TimeAfter2, TRF_IN_PER_TimeAfter3));
        	
        	List<EntityCondition> listTransferInPeriodTimeConds = UtilMisc.toList(TRF_IN_PER_TimeExac, TRF_IN_PER_TimeBefore, TRF_IN_PER_TimeAfter);
        	EntityCondition allTransferInPeriodTimeConds = EntityCondition.makeCondition(listTransferInPeriodTimeConds, EntityOperator.OR);
        	
        	List<EntityCondition> listAllTransferOriginInPeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatus, TRF_CondFAOrigin, allTransferInPeriodTimeConds);
        	EntityCondition allTransferOriginInPeriodConds  = EntityCondition.makeCondition(listAllTransferOriginInPeriodConds, EntityOperator.AND);
        	
        	List<EntityCondition> listAllTransferDestInPeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatus, TRF_CondFADest, allTransferInPeriodTimeConds);
        	EntityCondition allTransferDestInPeriodConds  = EntityCondition.makeCondition(listAllTransferDestInPeriodConds, EntityOperator.AND);
        	
        	List<EntityCondition> listAllTransferExptInPeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatusExpt, TRF_CondFADest, allTransferInPeriodTimeConds);
        	EntityCondition allTransferExptInPeriodConds  = EntityCondition.makeCondition(listAllTransferExptInPeriodConds, EntityOperator.AND);
        	
        	try {
        		listTransferOriginInPeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferOriginInPeriodConds), null, null, null, false);
        		if (!listTransferOriginInPeriods.isEmpty()){
	        		List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferOriginInPeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_APPROVED");
	        		
	        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1, Cond2);
	        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
	        	
	        		listTransferItemExportInPeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemExportInPeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
        		
        		listTransferDestInPeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferDestInPeriodConds), null, null, null, false);
        		if (!listTransferDestInPeriods.isEmpty()){
	        		List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferDestInPeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_APPROVED");
	        		
	        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1, Cond2);
	        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
	        	
	        		listTransferItemReceiveInPeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemExportInPeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
        		
        		listTransferExptInPeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferExptInPeriodConds), null, null, null, false);
        		if (!listTransferExptInPeriods.isEmpty()){
        			List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferExptInPeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_EXPORTED");
	        		
	        		List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
	        		listConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
	        		
	        		listTransferItemReceiveExptInPeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemReceiveExptInPeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
        		
    		} catch (GenericEntityException e) {
    			e.printStackTrace();
    			return ServiceUtil.returnError("OLBIUS: get transfer error!");
    		}
        	
        	// transfer before period
        	
        	EntityCondition TRF_BF_PER_TimeExac1 = EntityCondition.makeCondition("transferDate", EntityOperator.NOT_EQUAL, null);
        	EntityCondition TRF_BF_PER_TimeExac2 = EntityCondition.makeCondition("transferDate", EntityOperator.LESS_THAN, fromDate);
        	EntityCondition TRF_BF_PER_TimeExac = EntityCondition.makeCondition(UtilMisc.toList(TRF_BF_PER_TimeExac1, TRF_BF_PER_TimeExac2));
        	
        	EntityCondition TRF_BF_PER_TimeBefore1 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.NOT_EQUAL, null);
        	EntityCondition TRF_BF_PER_TimeBefore2 = EntityCondition.makeCondition("shipBeforeDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
        	EntityCondition TRF_BF_PER_TimeBefore = EntityCondition.makeCondition(UtilMisc.toList(TRF_BF_PER_TimeBefore1, TRF_BF_PER_TimeBefore2));
        	
        	List<EntityCondition> listTransferBeforePeriodTimeConds = UtilMisc.toList(TRF_BF_PER_TimeExac, TRF_BF_PER_TimeBefore);
        	EntityCondition allTransferBeforePeriodTimeConds = EntityCondition.makeCondition(listTransferBeforePeriodTimeConds, EntityOperator.OR);
        	
        	List<EntityCondition> listAllTransferOriginBeforePeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatus, TRF_CondFAOrigin, allTransferBeforePeriodTimeConds);
        	EntityCondition allTransferOriginBeforePeriodConds  = EntityCondition.makeCondition(listAllTransferOriginBeforePeriodConds, EntityOperator.AND);
        	
        	List<EntityCondition> listAllTransferDestBeforePeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatus, TRF_CondFADest, allTransferBeforePeriodTimeConds);
        	EntityCondition allTransferDestBeforePeriodConds  = EntityCondition.makeCondition(listAllTransferDestBeforePeriodConds, EntityOperator.AND);
        	
        	List<EntityCondition> listAllTransferExptBeforePeriodConds = UtilMisc.toList(TRF_CondType, TRF_CondStatusExpt, TRF_CondFADest, allTransferBeforePeriodTimeConds);
        	EntityCondition allTransferExptBeforePeriodConds  = EntityCondition.makeCondition(listAllTransferExptBeforePeriodConds, EntityOperator.AND);
        	
        	try {
        		listTransferOriginBeforePeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferOriginBeforePeriodConds), null, null, null, false);
        		if (!listTransferOriginBeforePeriods.isEmpty()){
	        		List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferOriginBeforePeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_APPROVED");
	        		
	        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1, Cond2);
	        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
	        	
	        		listTransferItemExportBeforePeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemReceiveBeforePeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
        		
        		listTransferDestBeforePeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferDestBeforePeriodConds), null, null, null, false);
        		if (!listTransferDestBeforePeriods.isEmpty()){
	        		List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferDestBeforePeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_APPROVED");
	        		
	        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1, Cond2);
	        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
	        	
	        		listTransferItemReceiveBeforePeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemExportBeforePeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
        		
        		listTransferExptBeforePeriods = delegator.findList("TransferHeader", EntityCondition.makeCondition(allTransferExptBeforePeriodConds), null, null, null, false);
        		if (!listTransferExptBeforePeriods.isEmpty()){
        			List<String> listTransferIds = new ArrayList<String>();
	        		for (GenericValue item : listTransferExptBeforePeriods) {
						if (!listTransferIds.contains(item.getString("transferId"))) listTransferIds.add(item.getString("transferId"));
					}
	        		EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.IN, listTransferIds);
	        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "TRANS_ITEM_EXPORTED");
	        		EntityCondition Cond3 = EntityCondition.makeCondition("destFacilityId", EntityOperator.EQUALS, facilityId);
	        		
	        		List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond3);
	        		listConds.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
	        		EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
	        		
	        		listTransferItemReceiveExptBeforePeriods = delegator.findList("TransferItem", EntityCondition.makeCondition(allConds), null, null, null, false);
	        		
	        		for (GenericValue item : listTransferItemReceiveExptBeforePeriods) {
	        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
	    			}
        		}
    		} catch (GenericEntityException e) {
    			e.printStackTrace();
    			return ServiceUtil.returnError("OLBIUS: get transfer error!");
    		}
    	}
    	
    	// requirement
    	
    	List<GenericValue> listRequirementInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listRequirementOutPeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listRequirementInBeforePeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listRequirementOutBeforePeriods = new ArrayList<GenericValue>();
    	
    	EntityCondition REQ_EXP_CondType = EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "EXPORT_REQUIREMENT");
    	EntityCondition REQ_RC_CondType = EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "RECEIVE_REQUIREMENT");
    	
    	EntityCondition REQ_CondStatus = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED");
		EntityCondition REQ_CondFA = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
		
    	// transfer in period
    	EntityCondition REQ_IN_PER_TimeExac1 = EntityCondition.makeCondition("requirementStartDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition REQ_IN_PER_TimeExac2 = EntityCondition.makeCondition("requirementStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
    	EntityCondition REQ_IN_PER_TimeExac3 = EntityCondition.makeCondition("requirementStartDate", EntityOperator.LESS_THAN, thruDate);
    	EntityCondition REQ_IN_PER_TimeExac = EntityCondition.makeCondition(UtilMisc.toList(REQ_IN_PER_TimeExac1, REQ_IN_PER_TimeExac2, REQ_IN_PER_TimeExac3));
    	List<EntityCondition> listReqInPeriodConds = UtilMisc.toList(REQ_IN_PER_TimeExac, REQ_RC_CondType, REQ_CondStatus);
    	if (UtilValidate.isNotEmpty(facilityId)){
    		listReqInPeriodConds.add(REQ_CondFA);
    	}
    	EntityCondition allReqInPeriodConds = EntityCondition.makeCondition(listReqInPeriodConds, EntityOperator.AND);
    	
    	List<EntityCondition> listReqOutPeriodConds = UtilMisc.toList(REQ_IN_PER_TimeExac, REQ_EXP_CondType, REQ_CondStatus);
    	if (UtilValidate.isNotEmpty(facilityId)){
    		listReqOutPeriodConds.add(REQ_CondFA);
    	}
    	EntityCondition allReqOutPeriodConds = EntityCondition.makeCondition(listReqOutPeriodConds, EntityOperator.AND);
    	
    	EntityCondition REQ_BF_PER_TimeExac1 = EntityCondition.makeCondition("requirementStartDate", EntityOperator.NOT_EQUAL, null);
    	EntityCondition REQ_BF_PER_TimeExac2 = EntityCondition.makeCondition("requirementStartDate", EntityOperator.LESS_THAN, fromDate);
    	EntityCondition REQ_BF_PER_TimeExac = EntityCondition.makeCondition(UtilMisc.toList(REQ_BF_PER_TimeExac1, REQ_BF_PER_TimeExac2));
    	List<EntityCondition> listReqInBeforePeriodConds = UtilMisc.toList(REQ_BF_PER_TimeExac, REQ_RC_CondType, REQ_CondStatus);
    	if (UtilValidate.isNotEmpty(facilityId)){
    		listReqInBeforePeriodConds.add(REQ_CondFA);
    	}
    	EntityCondition allReqInBeforePeriodConds = EntityCondition.makeCondition(listReqInBeforePeriodConds, EntityOperator.AND);
    	
    	List<EntityCondition> listReqOutBeforePeriodConds = UtilMisc.toList(REQ_BF_PER_TimeExac, REQ_EXP_CondType, REQ_CondStatus);
    	if (UtilValidate.isNotEmpty(facilityId)){
    		listReqOutBeforePeriodConds.add(REQ_CondFA);
    	}
    	EntityCondition allReqOutBeforePeriodConds = EntityCondition.makeCondition(listReqOutBeforePeriodConds, EntityOperator.AND);
    	
    	List<GenericValue> listRequirementItemInPeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listRequirementItemOutPeriods = new ArrayList<GenericValue>();
    	
    	List<GenericValue> listRequirementItemInBeforePeriods = new ArrayList<GenericValue>();
    	List<GenericValue> listRequirementItemOutBeforePeriods = new ArrayList<GenericValue>();
    	try { 
	    	listRequirementInPeriods = delegator.findList("Requirement", EntityCondition.makeCondition(allReqInPeriodConds), null, null, null, false);
			if (!listRequirementInPeriods.isEmpty()){
				List<String> listReqIds = new ArrayList<String>();
	    		for (GenericValue item : listRequirementInPeriods) {
					if (!listReqIds.contains(item.getString("requirementId"))) listReqIds.add(item.getString("requirementId"));
				}
	    		EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.IN, listReqIds);
//        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED");
        		
        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1);
        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
        	
        		listRequirementItemInPeriods = delegator.findList("RequirementItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
        		
        		for (GenericValue item : listRequirementItemInPeriods) {
        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
    			}
			}
			
			listRequirementInBeforePeriods = delegator.findList("Requirement", EntityCondition.makeCondition(allReqInBeforePeriodConds), null, null, null, false);
			if (!listRequirementInBeforePeriods.isEmpty()){
				List<String> listReqIds = new ArrayList<String>();
	    		for (GenericValue item : listRequirementInBeforePeriods) {
					if (!listReqIds.contains(item.getString("requirementId"))) listReqIds.add(item.getString("requirementId"));
				}
	    		EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.IN, listReqIds);
//        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED");
        		
        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1);
        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
        	
        		listRequirementItemInBeforePeriods = delegator.findList("RequirementItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
        		
        		for (GenericValue item : listRequirementItemInBeforePeriods) {
        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
    			}
			}
			
			listRequirementOutPeriods = delegator.findList("Requirement", EntityCondition.makeCondition(allReqOutPeriodConds), null, null, null, false);
			if (!listRequirementOutPeriods.isEmpty()){
				List<String> listReqIds = new ArrayList<String>();
	    		for (GenericValue item : listRequirementOutPeriods) {
					if (!listReqIds.contains(item.getString("requirementId"))) listReqIds.add(item.getString("requirementId"));
				}
	    		EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.IN, listReqIds);
//        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED");
        		
        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1);
        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
        	
        		listRequirementItemOutPeriods = delegator.findList("RequirementItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
        		
        		for (GenericValue item : listRequirementItemOutPeriods) {
        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
    			}
			}
			
			listRequirementOutBeforePeriods = delegator.findList("Requirement", EntityCondition.makeCondition(allReqOutBeforePeriodConds), null, null, null, false);
			if (!listRequirementOutBeforePeriods.isEmpty()){
				List<String> listReqIds = new ArrayList<String>();
	    		for (GenericValue item : listRequirementOutBeforePeriods) {
					if (!listReqIds.contains(item.getString("requirementId"))) listReqIds.add(item.getString("requirementId"));
				}
	    		EntityCondition Cond1 = EntityCondition.makeCondition("requirementId", EntityOperator.IN, listReqIds);
//        		EntityCondition Cond2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED");
        		
        		List<EntityCondition> listConds1 = UtilMisc.toList(Cond1);
        		listConds1.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProductIds));
        		EntityCondition allConds1 = EntityCondition.makeCondition(listConds1, EntityOperator.AND);
        	
        		listRequirementItemOutBeforePeriods = delegator.findList("RequirementItem", EntityCondition.makeCondition(allConds1), null, null, null, false);
        		
        		for (GenericValue item : listRequirementItemOutBeforePeriods) {
        			if(!listProductIdActives.contains(item.getString("productId"))) listProductIdActives.add(item.getString("productId"));
    			}
			}
    	} catch (GenericEntityException e){
    		return ServiceUtil.returnError("OLBIUS: getRequirement error!");
    	}
    	List<Map<String, Object>> listProductZeros = new ArrayList<Map<String, Object>>();
    	for (Map<String, Object> product : listProducts) {
    		String productId = (String)product.get("productId");
    		BigDecimal openingQuantity = BigDecimal.ZERO;
    		// init by QOH
    		for (GenericValue inv : listCurrentInvs) {
				if (inv.getString("productId").equals(productId)){
					openingQuantity = openingQuantity.add((BigDecimal)inv.get("quantityOnHandTotal"));
				}
			}
    		product.put("openingQuantity", openingQuantity);
			product.put("endingQuantity", openingQuantity);
			
			if (listProductIdActives.contains(productId)){
				listProductActives.add(product);
			} else {
				if (openingQuantity.compareTo(BigDecimal.ZERO) <= 0){
					listProductZeros.add(product);
				}
			}
			
    	}
    	if (!listProductActives.isEmpty()){
			listProducts.removeAll(listProductActives);
		}
//    	if (!listProductZeros.isEmpty()){
//			listProducts.removeAll(listProductZeros);
//		}
    	try {
	    	for (Map<String, Object> product : listProductActives) {
	    		String productId = (String)product.get("productId");
	    		BigDecimal openingQuantity = (BigDecimal)product.get("openingQuantity");
	    		// adding with PO
	    		
	    		BigDecimal quantityHasReceivedPO1 = BigDecimal.ZERO;
	    		
	    		BigDecimal quantityAvaiPO = BigDecimal.ZERO;
	        	List<EntityCondition> listHasReceivedPOCondInvs1 = new ArrayList<EntityCondition>();
	    		for (GenericValue orderItem : listAvailablePOs) {
	    			if (productId.equals(orderItem.getString("productId"))){
	    				quantityAvaiPO = quantityAvaiPO.add(orderItem.getBigDecimal("quantity"));
	    				String orderId = orderItem.getString("orderId");
						String orderItemSeqId = orderItem.getString("orderItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
						EntityCondition Cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
						EntityCondition Cond4 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
						List<EntityCondition> listHasReceivedInvConds = UtilMisc.toList(Cond1, Cond2, Cond4);
						
						EntityCondition allHasReceivedInvConds = EntityCondition.makeCondition(listHasReceivedInvConds, EntityOperator.AND);
						listHasReceivedPOCondInvs1.add(allHasReceivedInvConds);
	    			}
				}
	    		List<GenericValue> listInvDetails = new ArrayList<GenericValue>();
	    		if (!listHasReceivedPOCondInvs1.isEmpty()){
	    			EntityCondition allConds = EntityCondition.makeCondition(listHasReceivedPOCondInvs1, EntityOperator.OR);
	    			listInvDetails = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allConds), null, null, null, false);
	    		}
	    		for (GenericValue item : listInvDetails) {
					quantityHasReceivedPO1.add(item.getBigDecimal("quantityOnHandDiff"));
				}
				quantityAvaiPO = quantityAvaiPO.subtract(quantityHasReceivedPO1);
	    		
	    		BigDecimal quantityWBeReceivePO = BigDecimal.ZERO;
		    	BigDecimal quantityHasReceivedPO2 = BigDecimal.ZERO;
		    	List<EntityCondition> listHasReceivedPOCondInvs2 = new ArrayList<EntityCondition>();
				
				for (GenericValue item : listWBeReceivePOs) {
					if (productId.equals(item.getString("productId"))){
						quantityWBeReceivePO = quantityWBeReceivePO.add(item.getBigDecimal("quantity"));
						String orderId = item.getString("orderId");
						String orderItemSeqId = item.getString("orderItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
						EntityCondition Cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
						List<EntityCondition> listInvHasReceivedConds2 = UtilMisc.toList(Cond1, Cond2);
						EntityCondition allInvHasReceivedConds2 = EntityCondition.makeCondition(listInvHasReceivedConds2, EntityOperator.AND);
						listHasReceivedPOCondInvs2.add(allInvHasReceivedConds2);
					}
				}
				List<GenericValue> listInvHasReceivedDetails2 = new ArrayList<GenericValue>();
				if (!listHasReceivedPOCondInvs2.isEmpty()){
					EntityCondition allInvHasReceivedConds2 = EntityCondition.makeCondition(listHasReceivedPOCondInvs2, EntityOperator.OR);
					listInvHasReceivedDetails2 = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasReceivedConds2), null, null, null, false);
				}
				for (GenericValue item : listInvHasReceivedDetails2) {
					quantityHasReceivedPO2.add(item.getBigDecimal("quantityOnHandDiff"));
				}
				quantityWBeReceivePO = quantityWBeReceivePO.subtract(quantityHasReceivedPO2);
				
				openingQuantity = openingQuantity.add(quantityWBeReceivePO);
				
	    		// subtract with SO
	    		BigDecimal quantityHasSoldSO1 = BigDecimal.ZERO;
	    		
	    		BigDecimal quatityAvaiSO = BigDecimal.ZERO;
	        	List<EntityCondition> listHasSoldSOCondInvs1 = new ArrayList<EntityCondition>();
	    		for (GenericValue orderItem : listAvailableSOs) {
	    			if (productId.equals(orderItem.getString("productId"))){
	    				quatityAvaiSO = quatityAvaiSO.add(orderItem.getBigDecimal("quantity"));
	    				String orderId = orderItem.getString("orderId");
						String orderItemSeqId = orderItem.getString("orderItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
						EntityCondition Cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
						EntityCondition Cond4 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
						List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond4);
						
						EntityCondition allInvHasSoldConds1 = EntityCondition.makeCondition(listConds, EntityOperator.AND);
						listHasSoldSOCondInvs1.add(allInvHasSoldConds1);
	    			}
				}
	    		List<GenericValue> listInvHasSoldDetails1 = new ArrayList<GenericValue>();
	    		if (!listHasSoldSOCondInvs1.isEmpty()){
		    		EntityCondition allInvHasSoldConds1 = EntityCondition.makeCondition(listHasSoldSOCondInvs1, EntityOperator.OR);
		    		listInvHasSoldDetails1 = delegator.findList("ExportInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasSoldConds1), null, null, null, false);
	    		}
				for (GenericValue item : listInvHasSoldDetails1) {
					quantityHasSoldSO1.add(item.getBigDecimal("quantityOnHandDiff").negate());
				}
				quatityAvaiSO = quatityAvaiSO.subtract(quantityHasSoldSO1);
				
				BigDecimal quantityWBeSoldSO = BigDecimal.ZERO;
		    	BigDecimal quantityHasSoldSO2 = BigDecimal.ZERO;
		    	List<EntityCondition> listHasSoldSOCondInvs2 = new ArrayList<EntityCondition>();
				
				for (GenericValue item : listWBeSoldSOs) {
					if (productId.equals(item.getString("productId"))){
						quantityWBeSoldSO = quantityWBeSoldSO.add(item.getBigDecimal("quantity"));
						String orderId = item.getString("orderId");
						String orderItemSeqId = item.getString("orderItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
						EntityCondition Cond2 = EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId);
						List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2);
						EntityCondition allInvHasSoldConds2 = EntityCondition.makeCondition(listConds, EntityOperator.AND);
						listHasSoldSOCondInvs2.add(allInvHasSoldConds2);
					}
				}
				List<GenericValue> listInvHasSoldDetails2 = new ArrayList<GenericValue>();
				if (!listHasSoldSOCondInvs2.isEmpty()){
					EntityCondition allInvHasSoldConds2 = EntityCondition.makeCondition(listHasSoldSOCondInvs2, EntityOperator.OR);
					listInvHasSoldDetails2 = delegator.findList("ExportInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasSoldConds2), null, null, null, false);
				}
				for (GenericValue item : listInvHasSoldDetails2) {
					quantityHasSoldSO2.add(item.getBigDecimal("quantityOnHandDiff").negate());
				}
				quantityWBeSoldSO = quantityWBeSoldSO.subtract(quantityHasSoldSO2);
				
				openingQuantity = openingQuantity.subtract(quantityWBeSoldSO);
				
				if ("Y".equals(checkExpiredDate)){
					// subtract with inventory expired
					BigDecimal qohWBeExpired = BigDecimal.ZERO;
					EntityCondition Cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
					EntityCondition Cond2 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
					EntityCondition Cond3 = EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("INV_AVAILABLE", null));
		        	List<EntityCondition> listInvExpConds = UtilMisc.toList(Cond1, Cond2, Cond3);
	        		EntityCondition Cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN, fromDate);
	        		listInvExpConds.add(Cond4);
		        	EntityCondition allInvExpConds = EntityCondition.makeCondition(listInvExpConds, EntityOperator.AND);
		        	List<GenericValue> listInvExps = delegator.findList("InventoryItemGroupByProductOwnerFacilityStatusExpireDate", EntityCondition.makeCondition(allInvExpConds), null, null, null, false);
		        	if (!listInvExps.isEmpty()){
		        		for (GenericValue item : listInvExps) {
		        			qohWBeExpired = qohWBeExpired.add(item.getBigDecimal("quantityOnHandTotal"));
						}
		        	}
		        	openingQuantity = openingQuantity.subtract(qohWBeExpired);
				}
				
				BigDecimal endingQuantity = openingQuantity.subtract(quatityAvaiSO);
				endingQuantity = endingQuantity.add(quantityAvaiPO);
				
				// transfer
				BigDecimal transferOutInPerQty = BigDecimal.ZERO;
				List<EntityCondition> listHasExportedTransferCondInvs1 = new ArrayList<EntityCondition>();
				if (!listTransferItemExportInPeriods.isEmpty()){
					for (GenericValue item : listTransferItemExportInPeriods) {
						transferOutInPerQty = transferOutInPerQty.add(item.getBigDecimal("quantity"));
						String transferId = item.getString("transferId");
						String transferItemSeqId = item.getString("transferItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
						EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
						EntityCondition Cond4 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
						List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond4);
						
						EntityCondition allInvHasExportConds1 = EntityCondition.makeCondition(listConds, EntityOperator.AND);
						listHasExportedTransferCondInvs1.add(allInvHasExportConds1);
	    			}
				}
	    		List<GenericValue> listInvHasExportedDetails1 = new ArrayList<GenericValue>();
	    		if (!listHasExportedTransferCondInvs1.isEmpty()){
		    		EntityCondition allInvHasExportedConds1 = EntityCondition.makeCondition(listHasExportedTransferCondInvs1, EntityOperator.OR);
		    		listInvHasExportedDetails1 = delegator.findList("ExportInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasExportedConds1), null, null, null, false);
	    		}
	    		BigDecimal quantityHasExported1 = BigDecimal.ZERO;
				for (GenericValue item : listInvHasExportedDetails1) {
					quantityHasExported1.add(item.getBigDecimal("quantityOnHandDiff").negate());
				}
				transferOutInPerQty = transferOutInPerQty.subtract(quantityHasExported1);
				
				BigDecimal transferOutBeforePerQty = BigDecimal.ZERO;
				List<EntityCondition> listHasExportedTransferCondInvs2 = new ArrayList<EntityCondition>();
				if (!listTransferItemExportBeforePeriods.isEmpty()){
					for (GenericValue item : listTransferItemExportBeforePeriods) {
						transferOutBeforePerQty = transferOutBeforePerQty.add(item.getBigDecimal("quantity"));
						String transferId = item.getString("transferId");
						String transferItemSeqId = item.getString("transferItemSeqId");
						EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
						EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
						EntityCondition Cond4 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, org);
						List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond4);
						
						EntityCondition allInvHasExportConds2 = EntityCondition.makeCondition(listConds, EntityOperator.AND);
						listHasExportedTransferCondInvs2.add(allInvHasExportConds2);
	    			}
				}
	    		List<GenericValue> listInvHasExportedDetails2 = new ArrayList<GenericValue>();
	    		if (!listHasExportedTransferCondInvs2.isEmpty()){
		    		EntityCondition allInvHasExportedConds2 = EntityCondition.makeCondition(listHasExportedTransferCondInvs2, EntityOperator.OR);
		    		listInvHasExportedDetails2 = delegator.findList("ExportInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasExportedConds2), null, null, null, false);
	    		}
	    		BigDecimal quantityHasExported2 = BigDecimal.ZERO;
				for (GenericValue item : listInvHasExportedDetails2) {
					quantityHasExported2.add(item.getBigDecimal("quantityOnHandDiff").negate());
				}
				transferOutBeforePerQty = transferOutBeforePerQty.subtract(quantityHasExported2);
				
				BigDecimal transferInInPerQty = BigDecimal.ZERO;
				BigDecimal quantityHasReceivedTransfer = BigDecimal.ZERO;
				List<EntityCondition> listHasReceivedTransferCondInvs1 = new ArrayList<EntityCondition>();
				if (!listTransferItemReceiveInPeriods.isEmpty()){
					for (GenericValue item : listTransferItemReceiveInPeriods) {
						if (productId.equals(item.getString("productId"))){
							transferInInPerQty = transferInInPerQty.add(item.getBigDecimal("quantity"));
							String transferId = item.getString("transferId");
							String transferItemSeqId = item.getString("transferItemSeqId");
							EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
							EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
							List<EntityCondition> listInvHasReceivedConds2 = UtilMisc.toList(Cond1, Cond2);
							EntityCondition allInvHasReceivedConds2 = EntityCondition.makeCondition(listInvHasReceivedConds2, EntityOperator.AND);
							listHasReceivedTransferCondInvs1.add(allInvHasReceivedConds2);
						}
					}
					List<GenericValue> listInvHasReceivedTransferDetails2 = new ArrayList<GenericValue>();
					if (!listHasReceivedTransferCondInvs1.isEmpty()){
						EntityCondition allInvHasReceivedTransferConds2 = EntityCondition.makeCondition(listHasReceivedTransferCondInvs1, EntityOperator.OR);
						listInvHasReceivedTransferDetails2 = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasReceivedTransferConds2), null, null, null, false);
					}
					for (GenericValue item : listInvHasReceivedTransferDetails2) {
						quantityHasReceivedTransfer.add(item.getBigDecimal("quantityOnHandDiff"));
					}
				}
				transferInInPerQty.subtract(quantityHasReceivedTransfer); 
						
				BigDecimal transferInBeforePerQty = BigDecimal.ZERO;
				BigDecimal quantityHasReceivedTransfer2 = BigDecimal.ZERO;
				List<EntityCondition> listHasReceivedTransferCondInvs2 = new ArrayList<EntityCondition>();
				if (!listTransferItemReceiveBeforePeriods.isEmpty()){
					for (GenericValue item : listTransferItemReceiveBeforePeriods) {
						if (productId.equals(item.getString("productId"))){
							transferInBeforePerQty = transferInBeforePerQty.add(item.getBigDecimal("quantity"));
							String transferId = item.getString("transferId");
							String transferItemSeqId = item.getString("transferItemSeqId");
							EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
							EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
							List<EntityCondition> listInvHasReceivedConds2 = UtilMisc.toList(Cond1, Cond2);
							EntityCondition allInvHasReceivedConds2 = EntityCondition.makeCondition(listInvHasReceivedConds2, EntityOperator.AND);
							listHasReceivedTransferCondInvs2.add(allInvHasReceivedConds2);
						}
					}
					List<GenericValue> listInvHasReceivedTransferDetails2 = new ArrayList<GenericValue>();
					if (!listHasReceivedTransferCondInvs2.isEmpty()){
						EntityCondition allInvHasReceivedTransferConds2 = EntityCondition.makeCondition(listHasReceivedTransferCondInvs2, EntityOperator.OR);
						listInvHasReceivedTransferDetails2 = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasReceivedTransferConds2), null, null, null, false);
					}
					for (GenericValue item : listInvHasReceivedTransferDetails2) {
						quantityHasReceivedTransfer2.add(item.getBigDecimal("quantityOnHandDiff"));
					}
				}
				transferInBeforePerQty.subtract(quantityHasReceivedTransfer2);
				
				BigDecimal transferInBeforeExptPerQty = BigDecimal.ZERO;
				BigDecimal quantityHasReceivedExptTransfer1 = BigDecimal.ZERO;
				List<EntityCondition> listHasReceivedExptTransferCondInvs1 = new ArrayList<EntityCondition>();
				if(!listTransferItemReceiveExptBeforePeriods.isEmpty()){
					for (GenericValue item : listTransferItemReceiveExptBeforePeriods) {
						if (productId.equals(item.getString("productId"))){
							transferInBeforeExptPerQty = transferInBeforeExptPerQty.add(item.getBigDecimal("quantity"));
							String transferId = item.getString("transferId");
							String transferItemSeqId = item.getString("transferItemSeqId");
							EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
							EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
							List<EntityCondition> listInvHasReceivedConds1 = UtilMisc.toList(Cond1, Cond2);
							EntityCondition allInvHasReceivedConds1 = EntityCondition.makeCondition(listInvHasReceivedConds1, EntityOperator.AND);
							listHasReceivedExptTransferCondInvs1.add(allInvHasReceivedConds1);
						}
					}
					List<GenericValue> listInvHasReceivedExptTransferDetails1 = new ArrayList<GenericValue>();
					if (!listHasReceivedExptTransferCondInvs1.isEmpty()){
						EntityCondition allInvHasReceivedTransferConds1 = EntityCondition.makeCondition(listHasReceivedExptTransferCondInvs1, EntityOperator.OR);
						listInvHasReceivedExptTransferDetails1 = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasReceivedTransferConds1), null, null, null, false);
					}
					for (GenericValue item : listInvHasReceivedExptTransferDetails1) {
						quantityHasReceivedExptTransfer1.add(item.getBigDecimal("quantityOnHandDiff"));
					}
				}
				transferInBeforeExptPerQty = transferInBeforeExptPerQty.subtract(quantityHasReceivedExptTransfer1);
				
				BigDecimal transferInInExptPerQty = BigDecimal.ZERO;
				BigDecimal quantityHasReceivedExptTransfer2 = BigDecimal.ZERO;
				List<EntityCondition> listHasReceivedExptTransferCondInvs2 = new ArrayList<EntityCondition>();
				if(!listTransferItemReceiveExptInPeriods.isEmpty()){
					for (GenericValue item : listTransferItemReceiveExptInPeriods) {
						if (productId.equals(item.getString("productId"))){
							transferInInExptPerQty = transferInInExptPerQty.add(item.getBigDecimal("quantity"));
							String transferId = item.getString("transferId");
							String transferItemSeqId = item.getString("transferItemSeqId");
							EntityCondition Cond1 = EntityCondition.makeCondition("transferId", EntityOperator.EQUALS, transferId);
							EntityCondition Cond2 = EntityCondition.makeCondition("transferItemSeqId", EntityOperator.EQUALS, transferItemSeqId);
							List<EntityCondition> listInvHasReceivedConds2 = UtilMisc.toList(Cond1, Cond2);
							EntityCondition allInvHasReceivedConds2 = EntityCondition.makeCondition(listInvHasReceivedConds2, EntityOperator.AND);
							listHasReceivedExptTransferCondInvs2.add(allInvHasReceivedConds2);
						}
					}
					List<GenericValue> listInvHasReceivedExptTransferDetails2 = new ArrayList<GenericValue>();
					if (!listHasReceivedExptTransferCondInvs2.isEmpty()){
						EntityCondition allInvHasReceivedTransferConds2 = EntityCondition.makeCondition(listHasReceivedExptTransferCondInvs2, EntityOperator.OR);
						listInvHasReceivedExptTransferDetails2 = delegator.findList("ReceiveInventoryItemDetailAndInventory", EntityCondition.makeCondition(allInvHasReceivedTransferConds2), null, null, null, false);
					}
					for (GenericValue item : listInvHasReceivedExptTransferDetails2) {
						quantityHasReceivedExptTransfer2.add(item.getBigDecimal("quantityOnHandDiff"));
					}
				}
				transferInInExptPerQty = transferInInExptPerQty.subtract(quantityHasReceivedExptTransfer2);
				
				openingQuantity = openingQuantity.add(transferInBeforeExptPerQty);
				openingQuantity = openingQuantity.add(transferInBeforePerQty);
				openingQuantity = openingQuantity.subtract(transferOutBeforePerQty);
				
				endingQuantity = endingQuantity.add(transferInInExptPerQty);
				endingQuantity = endingQuantity.subtract(transferOutInPerQty);
				endingQuantity = endingQuantity.add(transferInInPerQty);
				
				// requirement
				
				BigDecimal reqInPerQty = BigDecimal.ZERO;
				if(!listRequirementItemInPeriods.isEmpty()){
					for (GenericValue item : listRequirementItemInPeriods) {
						if (productId.equals(item.getString("productId"))){
							reqInPerQty = reqInPerQty.add(item.getBigDecimal("quantity"));
						}
					}
				}
				BigDecimal reqOutPerQty = BigDecimal.ZERO;
				if(!listRequirementItemOutPeriods.isEmpty()){
					for (GenericValue item : listRequirementItemOutPeriods) {
						if (productId.equals(item.getString("productId"))){
							reqOutPerQty = reqOutPerQty.add(item.getBigDecimal("quantity"));
						}
					}
				}
				
				BigDecimal reqInBeforePerQty = BigDecimal.ZERO;
				if(!listRequirementItemInBeforePeriods.isEmpty()){
					for (GenericValue item : listRequirementItemInBeforePeriods) {
						if (productId.equals(item.getString("productId"))){
							reqInBeforePerQty = reqInBeforePerQty.add(item.getBigDecimal("quantity"));
						}
					}
				}
				BigDecimal reqOutBeforePerQty = BigDecimal.ZERO;
				if(!listRequirementItemOutBeforePeriods.isEmpty()){
					for (GenericValue item : listRequirementItemOutBeforePeriods) {
						if (productId.equals(item.getString("productId"))){
							reqOutBeforePerQty = reqOutBeforePerQty.add(item.getBigDecimal("quantity"));
						}
					}
				}
				openingQuantity = openingQuantity.add(reqInBeforePerQty);
				openingQuantity = openingQuantity.subtract(reqOutBeforePerQty);
				endingQuantity = endingQuantity.add(reqInPerQty);
				endingQuantity = endingQuantity.subtract(reqOutPerQty);
				
				product.put("openingQuantity", openingQuantity);
				product.put("endingQuantity", endingQuantity);
				listProducts.add(product);
			}
    	} catch (GenericEntityException e){
    		e.printStackTrace();
			return ServiceUtil.returnError("OLBIUS: OLBIUS inventoryForecast error!");
    	}
    	
    	successResult.put("listIterator", listProducts);
    	return successResult;
	}
	
	public static Map <String, Object> getPhysicalInventoryItemCountAndVariances(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	String physicalInventoryId = (String)context.get("physicalInventoryId");
    	List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
    	
    	try {
			List<GenericValue> listInvCount = delegator.findList("InventoryItemCountGroupByExpireAndManufacturedDate", EntityCondition.makeCondition(UtilMisc.toMap("physicalInventoryId", physicalInventoryId)), null, null, null, false);
			for (GenericValue item : listInvCount) {
				Map<String, Object> map = FastMap.newInstance();
				List<Map<String, Object>> listDetailItems = FastList.newInstance();
				String productId = item.getString("productId");
				String facilityId = item.getString("facilityId");
				String ownerPartyId = item.getString("ownerPartyId");
				String lotId = item.getString("lotId");
				String statusId = item.getString("statusId");
				Timestamp expireDate = item.getTimestamp("expireDate");
				Timestamp datetimeManufactured = item.getTimestamp("datetimeManufactured");
				Map<String, Object> mapTmp = UtilMisc.toMap("productId", productId, "facilityId", facilityId, "ownerPartyId", ownerPartyId, "lotId", lotId, "statusId", statusId, "expireDate", expireDate, "datetimeManufactured",datetimeManufactured, "physicalInventoryId", physicalInventoryId);
				List<GenericValue> listVariances = delegator.findList("InventoryItemVarianceGroupByExpireAndManufacturedDate", EntityCondition.makeCondition(mapTmp), null, null, null, false);
				if (!listVariances.isEmpty()){
					for (GenericValue var : listVariances) {
						listDetailItems.add(var);
					}
				}
				map.putAll(item);
				map.put("rowDetail", listDetailItems);
				listInventoryItems.add(map);
			}
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("OLBIUS: getPhysicalInventoryItemCountAndVariances error!");
		}
    	
    	successResult.put("listInventoryItems", listInventoryItems);
    	return successResult;
	}
	public static Map <String, Object> updateExpireDateAndManufacturedDate(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
    	String inventoryItemId = (String)context.get("inventoryItemId");
    	GenericValue inventoryItem;
		try {
			inventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
			Timestamp exp = inventoryItem.getTimestamp("expireDate");
	    	Timestamp mnf = inventoryItem.getTimestamp("datetimeManufactured");
	    	
	    	if (UtilValidate.isNotEmpty(exp) && UtilValidate.isNotEmpty(mnf)){
	    		Date dateExp = new Date(exp.getTime());
		    	Date dateMnf = new Date(mnf.getTime());
		    	
		    	Calendar cal = Calendar.getInstance();      
				cal.setTime(dateExp);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpExpDate = cal.getTime();      
				Timestamp newExpDate = new Timestamp(tmpExpDate.getTime());
				
				cal.setTime(dateMnf);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpMnfDate = cal.getTime();      
				Timestamp newDateMnf = new Timestamp(tmpMnfDate.getTime());
				
				inventoryItem.put("expireDate", newExpDate);
				inventoryItem.put("datetimeManufactured", newDateMnf);
				
				delegator.store(inventoryItem);
	    	}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get InventoryItem error!");
		}
		
    	return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getProductPackingUoms (DispatchContext ctx, Map<String, ? extends Object> context){
		String productId = (String)context.get("productId");
		Delegator delegator = ctx.getDelegator();
		List<String> listUomIds;
		Map<String, Object> successResult = FastMap.newInstance();
		try {
			listUomIds = LogisticsProductUtil.getProductPackingUoms(delegator, productId);
			successResult.put("listUomIds", listUomIds);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: getProductPackingUoms error!");
		}
    	return successResult;
    	
	}
	
	public static Map<String, Object> issueInventoryItemByShipment (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<GenericValue> listItemIssuance = delegator.findList(
				"ItemIssuance", EntityCondition.makeCondition(UtilMisc
						.toMap("shipmentId", shipmentId)), null, null,
				null, false);
		GenericValue system = delegator.findOne("UserLogin", false,
				UtilMisc.toMap("userLoginId", "system"));
		for (GenericValue issue : listItemIssuance) {
			Map<String, Object> mapDetail = FastMap.newInstance();
			mapDetail.put("userLogin", system);
			mapDetail.put("shipmentId", shipmentId);
			mapDetail.put("shipmentItemSeqId",
					issue.getString("shipmentItemSeqId"));
			mapDetail.put("inventoryItemId",
					issue.getString("inventoryItemId"));
			mapDetail.put("itemIssuanceId",
					issue.getString("itemIssuanceId"));
			mapDetail.put("quantityOnHandDiff",
					new BigDecimal(issue.getString("quantity")).negate());
			mapDetail.put("availableToPromiseDiff", new BigDecimal(
					issue.getString("quantity")).negate());
			try {
				dispatcher.runSync("createInventoryItemDetail", mapDetail);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: createInventoryItemDetail error "+ e.toString());
			}
		}
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("shipmentId", shipmentId);
    	return successResult;
	}
	
	@SuppressWarnings({"unchecked" })
	public static Map<String, Object> logisticsUpdateInventoryItems (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
//		String physicalInventoryId = (String)context.get("physicalInventoryId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("statusId") && !"".equals(item.getString("statusId")) && !"null".equals(item.getString("statusId"))) {
					mapItems.put("statusId", item.getString("statusId"));
				} else {
					mapItems.put("statusId", null);
				}
				if (item.containsKey("oldStatusId") && !"".equals(item.getString("oldStatusId")) && !"null".equals(item.getString("oldStatusId"))) {
					mapItems.put("oldStatusId", item.getString("oldStatusId"));
				} else {
					mapItems.put("oldStatusId", null);
				}
				if (item.containsKey("ownerPartyId")) {
					mapItems.put("ownerPartyId", item.getString("ownerPartyId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", new BigDecimal(item.getString("quantity")));
				}
				if (item.containsKey("lotId") && !"".equals(item.getString("lotId")) && !"null".equals(item.getString("lotId"))) {
					mapItems.put("lotId", item.getString("lotId"));
				} else {
					mapItems.put("lotId", null);
				}
				if (item.containsKey("oldLotId") && !"".equals(item.getString("oldLotId")) && !"null".equals(item.getString("oldLotId"))) {
					mapItems.put("oldLotId", item.getString("oldLotId"));
				} else {
					mapItems.put("oldLotId", null);
				}
				if (item.containsKey("expireDate") && !"".equals(item.getString("expireDate")) && !"null".equals(item.getString("expireDate"))) {
					mapItems.put("expireDate", new Timestamp(new Long(item.getString("expireDate"))));
				} else {
					mapItems.put("expireDate", null);
				}
				if (item.containsKey("datetimeManufactured") && !"".equals(item.getString("datetimeManufactured")) && !"null".equals(item.getString("datetimeManufactured"))) {
					mapItems.put("datetimeManufactured", new Timestamp(new Long(item.getString("datetimeManufactured"))));
				} else {
					mapItems.put("datetimeManufactured", null);
				}
				if (item.containsKey("oldExpireDate") && !"".equals(item.getString("oldExpireDate")) && !"null".equals(item.getString("oldExpireDate"))) {
					mapItems.put("oldExpireDate", new Timestamp(new Long(item.getString("oldExpireDate"))));
				} else {
					mapItems.put("oldExpireDate", null);
				}
				if (item.containsKey("oldDatetimeManufactured") && !"".equals(item.getString("oldDatetimeManufactured")) && !"null".equals(item.getString("oldDatetimeManufactured"))) {
					mapItems.put("oldDatetimeManufactured", new Timestamp(new Long(item.getString("oldDatetimeManufactured"))));
				} else {
					mapItems.put("oldDatetimeManufactured", null);
				}
				
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProducts");
		}
		
		if (!listProducts.isEmpty()){
			for (Map<String, Object> item : listProducts) {
				BigDecimal quantity = (BigDecimal)item.get("quantity");
				if (quantity.compareTo(BigDecimal.ZERO) <=0 ) return ServiceUtil.returnSuccess();
				String productId = (String)item.get("productId");
				GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				Boolean reqAmount = false;
				if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount"))) {
					reqAmount = true;
				}
				Map<String, Object> mapFind = FastMap.newInstance();
				mapFind.put("expireDate", item.get("oldExpireDate"));
				mapFind.put("datetimeManufactured", item.get("oldDatetimeManufactured"));
				mapFind.put("lotId", item.get("oldLotId"));
				mapFind.put("statusId", item.get("oldStatusId"));
				mapFind.put("facilityId", item.get("facilityId"));
				mapFind.put("productId", productId);
				mapFind.put("ownerPartyId", item.get("ownerPartyId"));
				List<GenericValue> listInvs = InventoryUtil.getListInventoryItems(delegator, mapFind);
				if (listInvs.isEmpty()){
					return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH_TO_UPDATE");
				}
				List<GenericValue> listInvToUpdates = new ArrayList<GenericValue>();
				Boolean check = true;
				for (GenericValue inv : listInvs) {
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					if (reqAmount) {
						qoh = inv.getBigDecimal("amountOnHandTotal");
					}
					if (qoh.compareTo(quantity) >= 0){
						if (qoh.compareTo(quantity) == 0){
							listInvToUpdates.add(inv);
						} else {
							String newInvId = null;
							if (reqAmount) {
								newInvId = InventoryUtil.splitInventoryItemByAmount(delegator, inv.getString("inventoryItemId"), quantity);
							} else {
								newInvId = InventoryUtil.splitInventoryItemByQuantity(delegator, inv.getString("inventoryItemId"), quantity);
							}
							GenericValue newInv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", newInvId));
							listInvToUpdates.add(newInv);
						}
						check = false;
						break;
					}
				}
				GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
				if (check){
					BigDecimal remainQty = quantity;
					for (GenericValue inv : listInvs) {
						BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
						if (reqAmount) {
							qoh = inv.getBigDecimal("amountOnHandTotal");
						}
						if (qoh.compareTo(remainQty) <= 0){
							listInvToUpdates.add(inv);
							remainQty = remainQty.subtract(qoh);
						} else {
							Map<String, Object> mapDetail = FastMap.newInstance();
							mapDetail.put("inventoryItemId", inv.getString("inventoryItemId"));
							mapDetail.put("quantityOnHandDiff", remainQty.negate());
							mapDetail.put("availableToPromiseDiff", remainQty.negate());
							if (reqAmount) {
								mapDetail.put("amountOnHandDiff", remainQty.negate());
								mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
								mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
							}
							mapDetail.put("userLogin", system);
							try {
								dispatcher.runSync("createInventoryItemDetail", mapDetail);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
							}
							
							Map<String, Object> mapNewInv = inv.getAllFields();
							mapNewInv.remove("inventoryItemId");
							mapNewInv.remove("quantityOnHandTotal");
							mapNewInv.remove("amountOnHandTotal");
							mapNewInv.remove("availableToPromiseTotal");
							mapNewInv.put("userLogin", system);
							String invNewId = null;
							try {
								Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
								invNewId = (String) mapTmp.get("inventoryItemId");
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
							}
							
							Map<String, Object> mapDetail2 = FastMap.newInstance();
							mapDetail2.put("inventoryItemId", invNewId);
							mapDetail2.put("quantityOnHandDiff", remainQty);
							mapDetail2.put("availableToPromiseDiff", remainQty);
							if (reqAmount) {
								mapDetail2.put("quantityOnHandDiff", BigDecimal.ONE);
								mapDetail2.put("amountOnHandDiff", remainQty);
								mapDetail2.put("availableToPromiseDiff", BigDecimal.ONE);
							}
							mapDetail2.put("userLogin", system);
							try {
								dispatcher.runSync("createInventoryItemDetail", mapDetail2);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
							}
							listInvToUpdates.add(inv);
							remainQty = BigDecimal.ZERO;
						}
						if (remainQty.compareTo(BigDecimal.ZERO) == 0) break;
					}
				}
				
				if (!listInvToUpdates.isEmpty()){
					for (GenericValue invTmp : listInvToUpdates) {
						Map<String, Object> inv = invTmp.getAllFields();
						String lotId = null;
						if (UtilValidate.isNotEmpty(item.get("lotId"))){
							lotId = (String)item.get("lotId");
							GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
							if (UtilValidate.isEmpty(lot)){
								lot = delegator.makeValue("Lot");
								lot.put("lotId", lotId);
								lot.put("creationDate", UtilDateTime.nowTimestamp());
								delegator.create(lot); 
							}
						}
						inv.put("expireDate", item.get("expireDate"));
						inv.put("datetimeManufactured", item.get("datetimeManufactured"));
						inv.put("lotId", lotId);
						inv.put("statusId", item.get("statusId"));
						inv.put("userLogin", system);
						try {
							dispatcher.runSync("updateInventoryItem", inv);
						} catch (GenericServiceException e){
							return ServiceUtil.returnError("OLBIUS: runsync service updateInventoryItem error!");
						}
					}
				}
			}
		}
		
		Map<String, Object> successResult = FastMap.newInstance();
		return successResult;
	}
	
	public static Map<String, Object> logisticsCreatePhysicalInventory (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue system = delegator.findOne("UserLogin", false,
				UtilMisc.toMap("userLoginId", "system"));
		String physicalInventoryId = null;
		
		String partyId = (String) context.get("partyId");
		String facilityId = (String) context.get("facilityId");
		String generalComments = (String) context.get("generalComments");
		Long physicalInventoryDateTmp = (Long) context.get("physicalInventoryDate");
		Timestamp physicalInventoryDate = null;
		if (UtilValidate.isNotEmpty(physicalInventoryDateTmp)) {
			physicalInventoryDate = new Timestamp(physicalInventoryDateTmp);
		}
		
		try {
			Map<String, Object> mapCreatePhysicalInv = UtilMisc.toMap(
					"userLogin", system, "partyId", partyId, "generalComments",
					generalComments, "physicalInventoryDate",
					physicalInventoryDate, "facilityId", facilityId);
			Map<String, Object> mapReturnPhys = dispatcher.runSync("createPhysicalInventory", mapCreatePhysicalInv);
			physicalInventoryId = (String) mapReturnPhys.get("physicalInventoryId");
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
		}
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("physicalInventoryId", physicalInventoryId);
		return successResult;
	}
	
	public static Map<String, Object> uploadInventoryExcelDocument(DispatchContext ctx, Map<String, Object> context) throws IOException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		InputStream stream = null;
		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			dispatcher.runSync("deleteAllTempCustomer", UtilMisc.toMap("userLogin", context.get("userLogin")));
			ByteBuffer uploadedFile = (ByteBuffer) context.get("uploadedFile");
			stream = new ByteArrayInputStream(uploadedFile.array());
			Workbook workbook = getWorkbook(stream, (String)context.get("fileName"));
			Sheet firstSheet = workbook.getSheetAt(0);
	        Iterator<Row> iterator = firstSheet.iterator();
	        while (iterator.hasNext()) {
	        	Row nextRow = iterator.next();
	        	switch (nextRow.getRowNum()) {
				case 0:
					checkTitleLv1(nextRow);
					break;
				default:
					List<Map<String, Object>> listInvTmp = new ArrayList<Map<String, Object>>();
					listInvTmp = analyzeContent(nextRow, delegator);
					if (!listInvTmp.isEmpty()){
						for (Map<String, Object> map : listInvTmp) {
							if (map.containsKey("isProductId")){
								if (map.containsKey("isProductId")){
									if (!(Boolean)map.get("isProductId")){
										return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLProductNotExists", locale) + " " + map.get("productCode"));
									}
								}
								if (map.containsKey("isFacilityId")){
									if (!(Boolean)map.get("isFacilityId")){
										return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFacilityNotExists", locale) + " " + map.get("facilityCode"));
									}
								}
								if (map.containsKey("isDatetimeManufactured")){
									if (!(Boolean)map.get("isDatetimeManufactured")){
										return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFileFormatError", locale) + " " + map.get("datetimeManufacturedStr"));
									}
								}
								if (map.containsKey("isExpireDate")){
									if (!(Boolean)map.get("isExpireDate")){
										return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFileFormatError", locale) + " " + map.get("expireDateStr"));
									}
								}
							}
						}
					}
					listInventoryItems.addAll(listInvTmp);
					break;
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseCRMUiLabels", "WrongFormat", locale) + e.getMessage());
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		result.put("listInventoryItems", listInventoryItems);
		return result;
	}
	
	private static List<Map<String, Object>> analyzeContent(Row nextRow, Delegator delegator) throws GenericEntityException {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = FastMap.newInstance();
		BigDecimal quantity = BigDecimal.ZERO;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = getCellValue(cell);
			if (value instanceof Double){
				value = value.toString();
			}
			switch (cell.getColumnIndex()) {
			case 0:
				if (UtilValidate.isNotEmpty(value)) {
					List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("productCode", value)), null, null, null, false);
					if (products.isEmpty()){
						products = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("productId", value)), null, null, null, false);
					}
					if (!products.isEmpty()){
						map.put("productId", products.get(0).getString("productId"));
						map.put("quantityUomId", products.get(0).getString("quantityUomId"));
						map.put("productName", products.get(0).getString("productName"));
						map.put("productCode", value);
						map.put("isProductId", true);
					} else {
						map.put("isProductId", false);
						map.put("productId", value);
						map.put("productCode", value);
					}
				}
				break;
//			case 1:
//				if (UtilValidate.isNotEmpty(value)) {
//					map.put("productName", value);
//				}
//				break;
//			case 2:
//				if (UtilValidate.isNotEmpty(value)) {
//					List<GenericValue> uoms = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("description", value)), null, null, null, false);
//					if (!uoms.isEmpty()){
//						map.put("quantityUomId", uoms.get(0).getString("uomId"));
//					} else {
//						map.put("quantityUomId", value);
//					}
//				}
//				break;
			case 1:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("quantity", new BigDecimal(value.toString()));
					quantity = new BigDecimal(value.toString());
				}
				break;
			case 2:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("unitCost", new BigDecimal(value.toString()));
				}
				break;
			case 3:
				if (UtilValidate.isNotEmpty(value)) {
					try {
						DateFormat formatter;
						formatter = new SimpleDateFormat("dd/MM/yyyy");
						Date date = formatter.parse(value.toString());
						java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
						map.put("datetimeManufactured", timeStampDate);
					} catch (ParseException e) {
						map.put("datetimeManufacturedStr", value.toString());
						map.put("isDatetimeManufactured", false);
					}
				}
				break;
			case 4:
				if (UtilValidate.isNotEmpty(value)) {
					try {
						DateFormat formatter;
						formatter = new SimpleDateFormat("dd/MM/yyyy");
						Date date = formatter.parse(value.toString());
						java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
						map.put("expireDate", timeStampDate);
					} catch (ParseException e) {
						map.put("expireDateStr", value.toString());
						map.put("isExpireDate", false);
					}
				}
				break;
			case 5:
				if (UtilValidate.isNotEmpty(value)) {
					value = value.toString().toUpperCase();
					List<GenericValue> lots = delegator.findList("Lot", EntityCondition.makeCondition(UtilMisc.toMap("lotId", value)), null, null, null, false);
					if (!lots.isEmpty()){
						map.put("lotId", lots.get(0).getString("lotId"));
					} else {
						map.put("lotId", value);
					}
				}
				break;
			case 6:
				if (UtilValidate.isNotEmpty(value)) {
					GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", value));
					map.put("facilityId", value);
					if (UtilValidate.isNotEmpty(facility)){
						map.put("isFacilityId", true);
					} else {
						map.put("isFacilityId", false);
					}
				}
				if (UtilValidate.isNotEmpty(value)) {
					List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityCode", value)), null, null, null, false);
					if (facilities.isEmpty()){
						facilities = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", value)), null, null, null, false);
					}
					if (!facilities.isEmpty()){
						map.put("facilityId", facilities.get(0).getString("facilityId"));
						map.put("facilityCode", value);
						map.put("isFacilityId", true);
					} else {
						map.put("isFacilityId", false);
						map.put("facilityId", value);
						map.put("facilityCode", value);
					}
				}
				
				break;
			default:
				break;
			}
		}
		if (quantity.compareTo(BigDecimal.ZERO) > 0){
			listInventoryItems.add(map);
		}
		return listInventoryItems;
	}
	
	private static void checkTitleLv1(Row nextRow) throws Exception {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				if (!"Mã sản phẩm".equals(value)) {
					throw new Exception("Mã sản phẩm");
				}
				break;
//			case 1:
//				if (!"Tên sản phẩm".equals(value)) {
//					throw new Exception("Tên sản phẩm");
//				}
//				break;
//			case 2:
//				if (!"Đơn vị".equals(value)) {
//					throw new Exception("Đơn vị");
//				}
//				break;
			case 1:
				if (!"Số lượng".equals(value)) {
					throw new Exception("Số lượng");
				}
				break;
			case 2:
				if (!"Đơn giá".equals(value)) {
					throw new Exception("Đơn giá");
				}
				break;
			case 3:
				if (!"Ngày sản xuất (dd/MM/yyyy)".equals(value)) {
					throw new Exception("Ngày sản xuất");
				}
				break;
			case 4:
				if (!"Hạn sử dụng (dd/MM/yyyy)".equals(value)) {
					throw new Exception("Hạn sử dụng");
				}
				break;
			case 5:
				if (!"Lô sản xuất".equals(value)) {
					throw new Exception("Lô sản xuất");
				}
				break;
			case 6:
				if (!"Kho hàng".equals(value)) {
					throw new Exception("Kho hàng");
				}
				break;
			default:
				break;
			}
		}
	}
	
	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue().trim();
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().getTime();
			} else {
				return cell.getNumericCellValue();
			}
		}
		return null;
	}
	
	public static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}
		return workbook;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> receiveInventoryFromUnknownSources(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator(); 
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", new BigDecimal(item.getString("quantity")));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("expireDate")) {
					mapItems.put("expireDate", new Timestamp(new Long((String)item.getString("expireDate"))));
				}
				if (item.containsKey("datetimeManufactured")) {
					mapItems.put("datetimeManufactured", new Timestamp(new Long((String)item.getString("datetimeManufactured"))));
				}
				if (item.containsKey("lotId")) {
					mapItems.put("lotId", item.getString("lotId"));
				}
				if (item.containsKey("unitCost")) {
					mapItems.put("unitCost", new BigDecimal(item.getString("unitCost")));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProducts");
		}
		
		if (!listProducts.isEmpty()){
			List<String> listFacilityIds = new ArrayList<String>();
			for (Map<String, Object> item : listProducts) {
				if (!listFacilityIds.contains((String)item.get("facilityId"))){
					listFacilityIds.add((String)item.get("facilityId"));
				}
			}
			if (!listFacilityIds.isEmpty()){
				for (String facilityId : listFacilityIds) {
					GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
					LocalDispatcher dispatcher = ctx.getDispatcher();
					GenericValue userLogin = (GenericValue)context.get("userLogin");
					String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
					List<GenericValue> list = delegator.findList("PartyAcctgPreference", EntityCondition.makeCondition(UtilMisc.toMap("partyId", ownerPartyId)), null, null, null, false);
					String currencyUomId = "VND";
					if (!list.isEmpty()){
						currencyUomId = list.get(0).getString("baseCurrencyUomId");
					}
					
					Map<String, Object> mapCreateShipment = FastMap.newInstance();
					mapCreateShipment.put("facilityId", facilityId);
					List<GenericValue> listFacilityContactMechs = LogisticsFacilityUtil.getFacilityContactMechs(delegator, facilityId, "SHIPPING_LOCATION");
					if (!listFacilityContactMechs.isEmpty()){
						mapCreateShipment.put("contactMechId", listFacilityContactMechs.get(0).getString("contactMechId"));
					}
		        	mapCreateShipment.put("partyIdTo", ownerPartyId);
		        	mapCreateShipment.put("shipmentTypeId", "RECEIVE_UNKNOWN");
		        	mapCreateShipment.put("statusId", "PURCH_SHIP_CREATED");
		        	mapCreateShipment.put("estimatedShipDate", UtilDateTime.nowTimestamp());
		        	mapCreateShipment.put("estimatedArrivalDate", UtilDateTime.nowTimestamp());
		        	mapCreateShipment.put("estimatedShipCost", BigDecimal.ZERO);
		        	mapCreateShipment.put("currencyUomId", currencyUomId);
		        	mapCreateShipment.put("defaultWeightUomId", UtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "default.weight.uom"));
		        	mapCreateShipment.put("userLogin", system);
		        	Map<String, Object> mapShipment = FastMap.newInstance();
		        	String shipmentId = null;
		        	try {
		        		mapShipment = dispatcher.runSync("createShipment", mapCreateShipment);
		        		if (UtilValidate.isNotEmpty(mapShipment.get("shipmentId"))){
		        			shipmentId = (String)mapShipment.get("shipmentId");
		        		}
		    		} catch (GenericServiceException e) {
		    			return ServiceUtil.returnError("OLBIUS - Create shipment transfer error");
		    		}
		        	if (UtilValidate.isNotEmpty(shipmentId)){
						for (Map<String, Object> item : listProducts) {
							BigDecimal quantity = (BigDecimal)item.get("quantity");
							if (quantity.compareTo(BigDecimal.ZERO) > 0) {
								String productId = (String)item.get("productId");
								
								String quantityUomId = (String)item.get("quantityUomId");
								GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
								String baseQuantityUomId = product.getString("quantityUomId");
								BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQuantityUomId);
								BigDecimal quantityOnHandTotal = quantity;
								if (convert.compareTo(BigDecimal.ZERO) <= 0){
									quantityOnHandTotal = quantity;
								} else {
									quantityOnHandTotal = quantity.multiply(convert);
								}
								
								Map<String, Object> mapShipmentItem = FastMap.newInstance();
								mapShipmentItem.put("shipmentId", shipmentId);
								mapShipmentItem.put("productId", productId);
								mapShipmentItem.put("quantity", quantityOnHandTotal);
								mapShipmentItem.put("userLogin", system);
								String shipmentItemSeqId = null;
								try {
									Map<String, Object> mapItem = dispatcher.runSync("createShipmentItem", mapShipmentItem);
									shipmentItemSeqId = (String)mapItem.get("shipmentItemSeqId");
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS - Create shipment item error");
								}
								String lotId = null;
								if (UtilValidate.isNotEmpty(item.get("lotId"))){
									lotId = ((String)item.get("lotId")).toUpperCase();
									GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
									if (UtilValidate.isEmpty(lot)){
										lot = delegator.makeValue("Lot");
										lot.put("lotId", lotId);
										lot.put("creationDate", UtilDateTime.nowTimestamp());
										delegator.create(lot); 
									}
								}
								
								item.put("shipmentItemSeqId", shipmentItemSeqId);
								item.put("shipmentId", shipmentId);
								item.put("quantityAccepted", quantityOnHandTotal);
								item.put("quantityExcess", BigDecimal.ZERO);
								item.put("quantityRejected", BigDecimal.ZERO);
								item.put("quantityQualityAssurance", BigDecimal.ZERO);
								item.put("expireDate", item.get("expireDate"));
								item.put("ownerPartyId", ownerPartyId);
								item.put("datetimeReceived", UtilDateTime.nowTimestamp());
								item.put("datetimeManufactured", item.get("datetimeManufactured"));
								item.put("lotId", lotId);
								item.put("statusId", null);
								item.put("userLogin", system);
								item.put("facilityId", facilityId);
								item.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
								item.put("unitCost", item.get("unitCost"));
								item.put("purCost", BigDecimal.ZERO);
								item.put("shipmentId", shipmentId);
								try {
									dispatcher.runSync("receiveInventoryProduct", item);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError("OLBIUS: receiveInventoryProduct error " + e.toString());
								}
							}
						}
		        	}
		        	
					// update shipment to shipped
					mapShipment = FastMap.newInstance();
					mapShipment.put("userLogin", system);
					mapShipment.put("shipmentId", shipmentId);
					mapShipment.put("statusId", "PURCH_SHIP_SHIPPED");
					try {
						dispatcher.runSync("updateShipment", mapShipment);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
					}
					
					// update shipment to received
					mapShipment = FastMap.newInstance();
					mapShipment.put("userLogin", system);
					mapShipment.put("shipmentId", shipmentId);
					mapShipment.put("statusId", "PURCH_SHIP_RECEIVED");
					try {
						dispatcher.runSync("updateShipment", mapShipment);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: updateShipment error " + e.toString());
					}
				}
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		return result;
	}
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetVarianceReasons(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			listIterator = delegator.find("VarianceReason", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get list variance reason error");
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> updateVarianceReason(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String varianceReasonId = (String)context.get("varianceReasonId");
		String description = (String)context.get("description");
		String negativeNumber = (String)context.get("negativeNumber");
		GenericValue variance = null;
		if(UtilValidate.isNotEmpty(varianceReasonId)){
			variance = delegator.findOne("VarianceReason", false, UtilMisc.toMap("varianceReasonId", varianceReasonId));
			if (UtilValidate.isEmpty(variance)){
				variance = delegator.makeValue("VarianceReason");
				variance.set("varianceReasonId", varianceReasonId);
			}
		} else{
			variance = delegator.makeValue("VarianceReason");
			varianceReasonId = delegator.getNextSeqId("VarianceReason");
			variance.set("varianceReasonId", varianceReasonId);
		}
		variance.set("description", description);
		variance.set("negativeNumber", negativeNumber);
		delegator.createOrStore(variance);
		
		successResult.put("varianceReasonId", varianceReasonId);
		return successResult;
	}
	
	public static Map<String, Object> deleteVarianceReason(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String varianceReasonId = (String)context.get("varianceReasonId");
		try {
			GenericValue variance = delegator.findOne("VarianceReason", false, UtilMisc.toMap("varianceReasonId", varianceReasonId));
			try {
				if (UtilValidate.isNotEmpty(variance)) delegator.removeValue(variance);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS_VARIANCE_CONSTRAIN");
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("OLBIUS: get variance error!");
		}
		successResult.put("varianceReasonId", varianceReasonId);
		return successResult;
	}
	
	public static Map<String, Object> createPhysicalInventoryAll (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue system = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "system"));
		String physicalInventoryId = null;
		
		String partyId = (String) context.get("partyId");
		String facilityId = (String) context.get("facilityId");
		String generalComments = (String) context.get("generalComments");
		Map<String, Object> map = FastMap.newInstance();
		map.put("userLogin", system);
		map.put("partyId", partyId);
		map.put("facilityId", facilityId);
		map.put("generalComments", generalComments);
		map.put("physicalInventoryDate", context.get("physicalInventoryDate"));
		Map<String, Object> mapResult = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(context.get("listProductVariances"))){
			try {
				map.put("listProducts", context.get("listProductVariances"));
				mapResult = dispatcher.runSync("createPhysicalInventoryAndMultiVariance", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: " + "createPhysicalInventoryAndMultiVariance" + " error");
			}
		} else {
			try {
				mapResult = dispatcher.runSync("logisticsCreatePhysicalInventory", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError("OLBIUS: " + "logisticsCreatePhysicalInventory" + " error");
			}
		}
		physicalInventoryId = (String)mapResult.get("physicalInventoryId");
		if (UtilValidate.isNotEmpty(physicalInventoryId)){
			map = FastMap.newInstance();
			map.put("userLogin", system);
			if (UtilValidate.isNotEmpty(context.get("listProductCounts"))){
				try {
					map.put("listProducts", context.get("listProductCounts"));
					map.put("physicalInventoryId", physicalInventoryId);
					dispatcher.runSync("createPhysicalInventoryCount", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}
			if (UtilValidate.isNotEmpty(context.get("listProductUpdates"))){
				map.put("listProducts", context.get("listProductUpdates"));
				map.put("physicalInventoryId", physicalInventoryId);
				try {
					dispatcher.runSync("logisticsUpdateInventoryItems", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		} else {
			return ServiceUtil.returnError("OLBIUS: logisticsCreatePhysicalInventory error!");
		}
		
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("physicalInventoryId", physicalInventoryId);
		return successResult;
	}
	
	public static Map<String, Object> logisticsUpdateInventoryItem (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		String statusId = (String)context.get("statusId");
		String quantityTmp = (String)context.get("quantity");
		if (UtilValidate.isNotEmpty(statusId) && ("INV_DEBT_CUSTOMER".equals(statusId) || "INV_DEBT_SUPPLIER".equals(statusId)) && UtilValidate.isNotEmpty(quantityTmp)) {
			Delegator delegator = ctx.getDelegator();
			List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
			EntityCondition cond4;
			if (UtilValidate.isNotEmpty(context.get("expireDate"))) {
				Timestamp tmp = new Timestamp(new Long((String) context.get("expireDate")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
			}
			listAllConds.add(cond4);
			EntityCondition cond5;
			if (UtilValidate.isNotEmpty(context.get("datetimeManufactured"))) {
				Timestamp tmp = new Timestamp(new Long((String) context.get("datetimeManufactured")));
				Date date = new Date(tmp.getTime());                     
				Calendar cal = Calendar.getInstance();      
				cal.setTime(date);                          
				cal.set(Calendar.HOUR_OF_DAY, 0);           
				cal.set(Calendar.MINUTE, 0);                
				cal.set(Calendar.SECOND, 0);                
				cal.set(Calendar.MILLISECOND, 0);           
				Date tmpDate = cal.getTime();      
				Timestamp expfrom = new Timestamp(tmpDate.getTime());
				EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
				
				cal.set(Calendar.HOUR_OF_DAY, 23);           
				cal.set(Calendar.MINUTE, 59);                
				cal.set(Calendar.SECOND, 59);                
				cal.set(Calendar.MILLISECOND, 999);           
				Date tmpDate2 = cal.getTime();      
				Timestamp expTo = new Timestamp(tmpDate2.getTime());
				EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
				cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
			} else {
				cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
			}
			listAllConds.add(cond5);
			if (UtilValidate.isNotEmpty(context.get("lotId"))) {
				listAllConds.add(EntityCondition.makeCondition("lotId", EntityOperator.EQUALS, (String)context.get("lotId")));
			}
			if (UtilValidate.isNotEmpty(context.get("productId"))) {
				listAllConds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, (String)context.get("productId")));
			}
			if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
				listAllConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, (String)context.get("facilityId")));
			}
			listAllConds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, (String)context.get("statusId")));
			List<GenericValue> listInventoryItems = delegator.findList("InventoryItem",
					EntityCondition.makeCondition(listAllConds), null, null, null, false);
			if (!listInventoryItems.isEmpty()) {
				List<GenericValue> listInvUpdates = new ArrayList<GenericValue>();
				BigDecimal quantity = new BigDecimal(quantityTmp);
				GenericValue invEng = null;
				for (GenericValue inv : listInventoryItems) {
					String productId = inv.getString("productId");
					GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					Boolean reqAmount = false;
					if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount"))) {
						reqAmount = true;
						qoh = inv.getBigDecimal("amountOnHandTotal");
					}
					if (qoh.compareTo(quantity) == 0) {
						invEng = inv;
						listInvUpdates.add(invEng);
						break;
					} else if (qoh.compareTo(quantity) > 0){
						String newInvId = null;
						if (!reqAmount) {
							newInvId = InventoryUtil.splitInventoryItemByQuantity(delegator, inv.getString("inventoryItemId"), quantity);
						} else {
							newInvId = InventoryUtil.splitInventoryItemByAmount(delegator, inv.getString("inventoryItemId"), quantity);
						}
						GenericValue objInventory = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", newInvId));
						invEng = objInventory;
						listInvUpdates.add(invEng);
						break;
					}
				}
				
				if (UtilValidate.isEmpty(invEng)) {
					BigDecimal remainQty = quantity;
					for (GenericValue inv : listInventoryItems) {
						String productId = inv.getString("productId");
						GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
						Boolean reqAmount = false;
						if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount"))) {
							reqAmount = true;
							qoh = inv.getBigDecimal("amountOnHandTotal");
						}
						if (qoh.compareTo(remainQty) <= 0) {
							listInvUpdates.add(inv);
							remainQty = quantity.subtract(qoh);
						} else {
							String newInvId = null;
							if (!reqAmount) {
								newInvId = InventoryUtil.splitInventoryItemByQuantity(delegator, inv.getString("inventoryItemId"), quantity);
							} else {
								newInvId = InventoryUtil.splitInventoryItemByAmount(delegator, inv.getString("inventoryItemId"), quantity);
							}
							GenericValue objInventory = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", newInvId));
							listInvUpdates.add(objInventory);
						}
						if (remainQty.compareTo(BigDecimal.ZERO) <= 0) break;
					}
				}
				if (!listInvUpdates.isEmpty()) {
					for (GenericValue inv : listInvUpdates) {
						inv.put("statusId", null);
						delegator.store(inv);
						// export 
						if ("INV_DEBT_CUSTOMER".equals(statusId)) {
							GenericValue tmpInvDetailNew = delegator.makeValue("InventoryItemDetail");
							tmpInvDetailNew.set("inventoryItemId", inv.getString("inventoryItemId"));
							tmpInvDetailNew.set("inventoryItemDetailSeqId", delegator.getNextSeqId("InventoryItemDetail"));
							tmpInvDetailNew.set("effectiveDate", UtilDateTime.nowTimestamp());
							GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", inv.getString("productId")));
							if (UtilValidate.isNotEmpty(objProduct) && "Y".equals(objProduct.getString("requireAmount"))) {
								tmpInvDetailNew.set("quantityOnHandDiff", BigDecimal.ONE.negate());
								tmpInvDetailNew.set("availableToPromiseDiff", BigDecimal.ONE.negate());
								tmpInvDetailNew.set("accountingQuantityDiff", BigDecimal.ONE.negate());
								tmpInvDetailNew.set("amountOnHandDiff", inv.getBigDecimal("amountOnHandTotal").negate());
							} else {
								tmpInvDetailNew.set("quantityOnHandDiff", inv.getBigDecimal("quantityOnHandTotal").negate());
								tmpInvDetailNew.set("availableToPromiseDiff", inv.getBigDecimal("availableToPromiseTotal").negate());
								tmpInvDetailNew.set("accountingQuantityDiff", inv.getBigDecimal("accountingQuantityTotal").negate());
							}
							tmpInvDetailNew.create();
						}
					}
				}
			}
		}
		Map<String, Object> successResult = FastMap.newInstance();
		return successResult;
	}
	
	public static Map<String, Object> checkQuantityOnHandAvailableProduct (DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		String productId = (String)context.get("productId");
		String quantityTmp = (String)context.get("quantity");
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConds = new ArrayList<EntityCondition>();
		EntityCondition cond4;
		if (UtilValidate.isNotEmpty(context.get("expireDate"))) {
			Timestamp tmp = new Timestamp(new Long((String) context.get("expireDate")));
			Date date = new Date(tmp.getTime());                     
			Calendar cal = Calendar.getInstance();      
			cal.setTime(date);                          
			cal.set(Calendar.HOUR_OF_DAY, 0);           
			cal.set(Calendar.MINUTE, 0);                
			cal.set(Calendar.SECOND, 0);                
			cal.set(Calendar.MILLISECOND, 0);           
			Date tmpDate = cal.getTime();      
			Timestamp expfrom = new Timestamp(tmpDate.getTime());
			EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
			
			cal.set(Calendar.HOUR_OF_DAY, 23);           
			cal.set(Calendar.MINUTE, 59);                
			cal.set(Calendar.SECOND, 59);                
			cal.set(Calendar.MILLISECOND, 999);           
			Date tmpDate2 = cal.getTime();      
			Timestamp expTo = new Timestamp(tmpDate2.getTime());
			EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
			cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
		} else {
			cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
		}
		listAllConds.add(cond4);
		EntityCondition cond5;
		if (UtilValidate.isNotEmpty(context.get("datetimeManufactured"))) {
			Timestamp tmp = new Timestamp(new Long((String) context.get("datetimeManufactured")));
			Date date = new Date(tmp.getTime());                     
			Calendar cal = Calendar.getInstance();      
			cal.setTime(date);                          
			cal.set(Calendar.HOUR_OF_DAY, 0);           
			cal.set(Calendar.MINUTE, 0);                
			cal.set(Calendar.SECOND, 0);                
			cal.set(Calendar.MILLISECOND, 0);           
			Date tmpDate = cal.getTime();      
			Timestamp expfrom = new Timestamp(tmpDate.getTime());
			EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
			
			cal.set(Calendar.HOUR_OF_DAY, 23);           
			cal.set(Calendar.MINUTE, 59);                
			cal.set(Calendar.SECOND, 59);                
			cal.set(Calendar.MILLISECOND, 999);           
			Date tmpDate2 = cal.getTime();      
			Timestamp expTo = new Timestamp(tmpDate2.getTime());
			EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
			cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
		} else {
			cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
		}
		listAllConds.add(cond5);
		if (UtilValidate.isNotEmpty(context.get("lotId"))) {
			listAllConds.add(EntityCondition.makeCondition("lotId", EntityOperator.EQUALS, (String)context.get("lotId")));
		}
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			listAllConds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, (String)context.get("productId")));
		}
		if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
			listAllConds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, (String)context.get("facilityId")));
		}
		if (UtilValidate.isNotEmpty(context.get("statusId"))) {
			listAllConds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, (String)context.get("statusId")));
		} else {
			EntityCondition condOrStt1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
			EntityCondition condOrStt2 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toSet("INV_NS_DEFECTIVE", "INV_NS_ON_HOLD", "INV_DEBT_SUPPLIER", "INV_DEBT_CUSTOMER", "INV_DEACTIVATED", "INV_ON_HOLD", "INV_DEFECTIVE", "INV_ON_HOLD"));
			List<EntityCondition> listCondsStt = new ArrayList<EntityCondition>();
			listCondsStt.add(condOrStt1);
			listCondsStt.add(condOrStt2);
			EntityCondition sttConds = EntityCondition.makeCondition(listCondsStt, EntityOperator.OR);
			listAllConds.add(sttConds);
		}
		BigDecimal quantity = new BigDecimal(quantityTmp);
		listAllConds.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, (String)context.get("productId")));
		GenericValue objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		
		if (UtilValidate.isNotEmpty(objProduct.get("requireAmount")) && "Y".equals(objProduct.getString("requireAmount"))) {
			listAllConds.add(EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity));
		} else {
			listAllConds.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity));
		}
		List<GenericValue> listInventoryItems = delegator.findList("InventoryItemGroupByDateNotDateReciveDetail",
				EntityCondition.makeCondition(listAllConds), null, null, null, false);
		Map<String, Object> successResult = FastMap.newInstance();
		if (!listInventoryItems.isEmpty()) {
			successResult.put("available", "Y");
		} else {
			successResult.put("available", "N");
		}
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkQuantityOnHandAvailableProducts(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("quantity")) {
					mapItems.put("quantity", item.getString("quantity"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("weightUomId")) {
					mapItems.put("weightUomId", item.getString("weightUomId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
				if (item.containsKey("expireDate")) {
					mapItems.put("expireDate", new Timestamp(new Long((String)item.getString("expireDate"))));
				}
				if (item.containsKey("datetimeManufactured")) {
					mapItems.put("datetimeManufactured", new Timestamp(new Long((String)item.getString("datetimeManufactured"))));
				}
				if (item.containsKey("lotId")) {
					mapItems.put("lotId", item.getString("lotId"));
				}
				if (item.containsKey("unitCost")) {
					mapItems.put("unitCost", new BigDecimal(item.getString("unitCost")));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProducts");
		}
		List<Map<String, Object>> listProductsAvailable = new ArrayList<Map<String, Object>>();
		if (!listProducts.isEmpty()){
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			for (Map<String, Object> map : listProducts) {
				Map<String, Object> item = FastMap.newInstance();
				item.putAll(map);
				map.put("userLogin", userLogin);
				try {
					Map<String, Object> result = dispatcher.runSync("checkQuantityOnHandAvailableProduct", map);
					String available = (String)result.get("available");
					item.put("available", available);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: checkQuantityOnHandAvailable error! " + e.toString());
				}
				listProductsAvailable.add(item);
			}
		}
		Map<String, Object> successResult = FastMap.newInstance();
		successResult.put("listProductsAvailable", listProductsAvailable);
		return successResult;
	}
	
	//	Huyendt edit stocking inventory
	@SuppressWarnings("unchecked")
	public static Map<String, Object> createPhysicalInventoryAndMultiVarianceNew(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		String partyId = (String) context.get("partyId");
		String facilityId = (String) context.get("facilityId");
		String eventId = (String) context.get("eventId");
		String generalComments = (String) context.get("generalComments");		
		Long physicalInventoryDateTmp = (Long) context
				.get("physicalInventoryDate");
		Timestamp physicalInventoryDate = null;
		if (UtilValidate.isNotEmpty(physicalInventoryDateTmp)) {
			physicalInventoryDate = new Timestamp(physicalInventoryDateTmp);
		}
		List<Object> mapProducts = (List<Object>) context.get("listProducts");
		Boolean isJson = false;
		if (!mapProducts.isEmpty()) {
			if (mapProducts.get(0) instanceof String) {
				isJson = true;
			}
		}
		List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
		if (isJson) {
			String stringJson = "[" + (String) mapProducts.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("productId")) {
					mapItems.put("productId", item.getString("productId"));
				}
				if (item.containsKey("statusId") && !"".equals(item.getString("statusId")) && !"null".equals(item.getString("statusId"))) {
					mapItems.put("statusId", item.getString("statusId"));
				} else {
					mapItems.put("statusId", null);
				}
//				if (item.containsKey("expireDate") && !"".equals(item.getString("expireDate")) && !"null".equals(item.getString("expireDate"))) {
//					mapItems.put("expireDate", item.getString("expireDate"));
//				} else {
//					mapItems.put("expireDate", null);
//				}
//				if (item.containsKey("datetimeManufactured") && !"".equals(item.getString("datetimeManufactured")) && !"null".equals(item.getString("datetimeManufactured"))) {
//					mapItems.put("datetimeManufactured",
//							item.getString("datetimeManufactured"));
//				} else {
//					mapItems.put("datetimeManufactured", null);
//				}
				if (item.containsKey("ownerPartyId")) {
					mapItems.put("ownerPartyId", item.getString("ownerPartyId"));
				}
				if (item.containsKey("facilityId")) {
					mapItems.put("facilityId", item.getString("facilityId"));
				}
//				if (item.containsKey("lotId") && !"".equals(item.getString("lotId")) && !"null".equals(item.getString("lotId"))) {
//					mapItems.put("lotId", item.getString("lotId"));
//				} else {
//					mapItems.put("lotId", null);
//				}
				if (item.containsKey("quantityOnHandVar")) {
					mapItems.put("quantityOnHandVar", item.getString("quantityOnHandVar"));
				}
				if (item.containsKey("quantityUomId")) {
					mapItems.put("quantityUomId", item.getString("quantityUomId"));
				}
				if (item.containsKey("varianceReasonId")) {
					mapItems.put("varianceReasonId", item.getString("varianceReasonId"));
				}
				if (item.containsKey("comments")) {
					mapItems.put("comments", item.getString("comments"));
				}
				listProducts.add(mapItems);
			}
		} else {
			listProducts = (List<Map<String, Object>>) context.get("listProducts");
		}

		List<Map<String, Object>> listInventoryItems = new ArrayList<Map<String, Object>>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue system = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", "system"));
		for (Map<String, Object> item : listProducts) {
			
			BigDecimal quantity = new BigDecimal((String) item.get("quantityOnHandVar"));
			String varianceReasonId = (String)item.get("varianceReasonId");
			GenericValue reason = delegator.findOne("VarianceReason", false, UtilMisc.toMap("varianceReasonId", varianceReasonId));
			if (UtilValidate.isNotEmpty(reason)) {
				if (UtilValidate.isNotEmpty(reason.getString("negativeNumber")) && "Y".equals(reason.getString("negativeNumber"))){
					if (quantity.compareTo(BigDecimal.ZERO) > 0) {
						quantity = quantity.negate();
						item.put("quantityOnHandVar", quantity);
					}
				} 
			}
			String productId = (String)item.get("productId");
			Boolean reqAmount = false;
			if (ProductUtil.isWeightProduct(delegator, productId)) {
				reqAmount = true;
			}
			EntityCondition Cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition Cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, item.get("facilityId"));
			EntityCondition Cond3;
			if (item.get("statusId") != "null" && item.get("statusId") != null) {
				Cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, item.get("statusId"));
			} else {
				Cond3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
			}

//			EntityCondition Cond4;
//			if (UtilValidate.isNotEmpty(item.get("expireDate"))){
//				Timestamp tmp = new Timestamp(new Long((String) item.get("expireDate")));
//				Date date = new Date(tmp.getTime());                     
//				Calendar cal = Calendar.getInstance();      
//				cal.setTime(date);                          
//				cal.set(Calendar.HOUR_OF_DAY, 0);           
//				cal.set(Calendar.MINUTE, 0);                
//				cal.set(Calendar.SECOND, 0);                
//				cal.set(Calendar.MILLISECOND, 0);           
//				Date tmpDate = cal.getTime();      
//				Timestamp expfrom = new Timestamp(tmpDate.getTime());
//				EntityCondition Condf = EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
//				
//				cal.set(Calendar.HOUR_OF_DAY, 23);           
//				cal.set(Calendar.MINUTE, 59);                
//				cal.set(Calendar.SECOND, 59);                
//				cal.set(Calendar.MILLISECOND, 999);           
//				Date tmpDate2 = cal.getTime();      
//				Timestamp expTo = new Timestamp(tmpDate2.getTime());
//				EntityCondition Condt = EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
//				Cond4 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
//			} else {
//				Cond4 = EntityCondition.makeCondition("expireDate", EntityOperator.EQUALS, null);
//			}
//			EntityCondition Cond5;
//			if (UtilValidate.isNotEmpty(item.get("datetimeManufactured"))){
//				Timestamp tmp = new Timestamp(new Long((String) item.get("datetimeManufactured")));
//				Date date = new Date(tmp.getTime());                     
//				Calendar cal = Calendar.getInstance();      
//				cal.setTime(date);                          
//				cal.set(Calendar.HOUR_OF_DAY, 0);           
//				cal.set(Calendar.MINUTE, 0);                
//				cal.set(Calendar.SECOND, 0);                
//				cal.set(Calendar.MILLISECOND, 0);           
//				Date tmpDate = cal.getTime();      
//				Timestamp expfrom = new Timestamp(tmpDate.getTime());
//				EntityCondition Condf = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, expfrom);
//				
//				cal.set(Calendar.HOUR_OF_DAY, 23);           
//				cal.set(Calendar.MINUTE, 59);                
//				cal.set(Calendar.SECOND, 59);                
//				cal.set(Calendar.MILLISECOND, 999);           
//				Date tmpDate2 = cal.getTime();      
//				Timestamp expTo = new Timestamp(tmpDate2.getTime());
//				EntityCondition Condt = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.LESS_THAN_EQUAL_TO, expTo);
//				Cond5 = EntityCondition.makeCondition(UtilMisc.toList(Condf, Condt), EntityOperator.AND);
//			} else {
//				Cond5 = EntityCondition.makeCondition("datetimeManufactured", EntityOperator.EQUALS, null);
//			}
			EntityCondition Cond6 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, item.get("ownerPartyId"));
//			EntityCondition Cond7 = EntityCondition.makeCondition("lotId", EntityOperator.EQUALS, item.get("lotId"));
			EntityCondition Cond8 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity.abs());
			if (reqAmount) {
				Cond8 = EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.GREATER_THAN_EQUAL_TO, quantity.abs());
			}
			EntityCondition Cond9 = EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO);

			List<EntityCondition> listConds = new ArrayList<>();
			if (quantity.compareTo(BigDecimal.ZERO) > 0) {
				listConds = new ArrayList<>();
//				listConds.add(Cond7);
				listConds.add(Cond6);
//				listConds.add(Cond5);
//				listConds.add(Cond4);
				listConds.add(Cond3);
				listConds.add(Cond2);
				listConds.add(Cond1);
				EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
				List<GenericValue> listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
				if (!listInventory.isEmpty()) {
					GenericValue inv = listInventory.get(0);
					String inventoryItemId = inv.getString("inventoryItemId");
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("inventoryItemId", inventoryItemId);
					listInventoryItems.add(map);
				} else {
					// extend by VIETTB
					Map<String, Object> inventoryItem = FastMap.newInstance();
					Timestamp datetimeReceived = new Timestamp(new Date().getTime());
					inventoryItem.put("productId", productId);
					GenericValue party = null;
					if (SalesPartyUtil.isDistributor(delegator, partyId)){
						party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
						if (UtilValidate.isNotEmpty(party)) {
							inventoryItem.put("currencyUomId", party.get("preferredCurrencyUomId"));
						} else {
							inventoryItem.put("currencyUomId","VND");
						}
					} else {
						party = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
						if (UtilValidate.isNotEmpty(party)) {
							inventoryItem.put("currencyUomId", party.get("baseCurrencyUomId"));
						} else {
							inventoryItem.put("currencyUomId","VND");
						}
					}
									
					inventoryItem.put("unitCost", BigDecimal.ZERO);
					inventoryItem.put("purCost", BigDecimal.ZERO);					
					inventoryItem.put("datetimeReceived", datetimeReceived);
					inventoryItem.put("facilityId", facilityId);
					inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
					inventoryItem.put("ownerPartyId", partyId);					
					inventoryItem.put("comments", "STOCKEVENT");
					inventoryItem.put("userLogin", system);
					String inventoryItemId = null;
					try {
						Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", inventoryItem);
						inventoryItemId = (String) mapTmp.get("inventoryItemId");						
						Map<String, Object> map = FastMap.newInstance();
						map.putAll(item);
						map.put("inventoryItemId", inventoryItemId);
						listInventoryItems.add(map);						
						
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
					}								
					//return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
				}
			} else {
				listConds = new ArrayList<>();
				listConds.add(Cond8);
//				listConds.add(Cond7);
				listConds.add(Cond6);
//				listConds.add(Cond5);
//				listConds.add(Cond4);
				listConds.add(Cond3);
				listConds.add(Cond2);
				listConds.add(Cond1);
				listConds.add(Cond9);
				EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
				List<GenericValue> listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
				if (!listInventory.isEmpty()) {
					GenericValue inv = listInventory.get(0);
					String inventoryItemId = inv.getString("inventoryItemId");
					Map<String, Object> map = FastMap.newInstance();
					map.putAll(item);
					map.put("inventoryItemId", inventoryItemId);
					listInventoryItems.add(map);
				} else {
					listConds = new ArrayList<>();
//					listConds.add(Cond7);
					listConds.add(Cond6);
//					listConds.add(Cond5);
//					listConds.add(Cond4);
					listConds.add(Cond3);
					listConds.add(Cond2);
					listConds.add(Cond1);
					listConds.add(Cond9);
					allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);
					listInventory = delegator.findList("InventoryItem", allConds, null, null, null, false);
					if (!listInventory.isEmpty()) {
						BigDecimal qohTotal = BigDecimal.ZERO;
						for (GenericValue inv : listInventory) {
							qohTotal = qohTotal.add(inv.getBigDecimal("quantityOnHandTotal"));
							if (reqAmount) {
								qohTotal = qohTotal.add(inv.getBigDecimal("amountOnHandTotal"));
							}
						}
						if (qohTotal.compareTo(quantity.abs()) >= 0) {
							BigDecimal remainQuantity = quantity.abs();
							for (GenericValue inv : listInventory) {
								BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
								if (reqAmount) {
									qoh = inv.getBigDecimal("amountOnHandTotal");
								}
								String inventoryItemId = inv.getString("inventoryItemId");
								Map<String, Object> map = FastMap.newInstance();
								map.putAll(item);
								map.put("inventoryItemId", inventoryItemId);
								if (remainQuantity.compareTo(qoh) >= 0) {
									map.put("quantityOnHandVar", qoh.negate().toString());
									remainQuantity = remainQuantity.subtract(qoh);
								} else {
									map.put("quantityOnHandVar", remainQuantity.negate().toString());
									remainQuantity = BigDecimal.ZERO;
								}
								listInventoryItems.add(map);
								if (remainQuantity.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}
							}
						} else {
							BigDecimal remainQuantity = quantity.abs();
							for (GenericValue inv : listInventory) {
								BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
								if (reqAmount) {
									qoh = inv.getBigDecimal("amountOnHandTotal");
								}
								String inventoryItemId = inv.getString("inventoryItemId");
								Map<String, Object> map = FastMap.newInstance();
								map.putAll(item);
								map.put("inventoryItemId", inventoryItemId);
								if (remainQuantity.compareTo(qoh) >= 0) {
									map.put("quantityOnHandVar", qoh.negate().toString());
									remainQuantity = remainQuantity.subtract(qoh);
								} else {
									map.put("quantityOnHandVar", remainQuantity.negate().toString());
									remainQuantity = BigDecimal.ZERO;
								}
								listInventoryItems.add(map);
								if (remainQuantity.compareTo(BigDecimal.ZERO) == 0) {
									break;
								}
							}
							
							Map<String, Object> inventoryItem = FastMap.newInstance();
							Timestamp datetimeReceived = new Timestamp(new Date().getTime());
							inventoryItem.put("productId", productId);
							
							GenericValue party = null;
							if (SalesPartyUtil.isDistributor(delegator, partyId)){
								party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
								if (UtilValidate.isNotEmpty(party)) {
									inventoryItem.put("currencyUomId", party.get("preferredCurrencyUomId"));
								} else {
									inventoryItem.put("currencyUomId","VND");
								}
							} else {
								party = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
								if (UtilValidate.isNotEmpty(party)) {
									inventoryItem.put("currencyUomId", party.get("baseCurrencyUomId"));
								} else {
									inventoryItem.put("currencyUomId","VND");
								}
							}			
							inventoryItem.put("unitCost", BigDecimal.ZERO);
							inventoryItem.put("purCost", BigDecimal.ZERO);					
							inventoryItem.put("datetimeReceived", datetimeReceived);
							inventoryItem.put("facilityId", facilityId);
							inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
							inventoryItem.put("ownerPartyId", partyId);					
							inventoryItem.put("comments", "STOCKEVENT");
							inventoryItem.put("userLogin", system);
							String inventoryItemId = null;
							try {
								Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", inventoryItem);
								inventoryItemId = (String) mapTmp.get("inventoryItemId");																					
								
								Map<String, Object> map = FastMap.newInstance();
								map.putAll(item);
								map.put("quantityOnHandVar", remainQuantity.abs().negate().toString());
								map.put("inventoryItemId", inventoryItemId);
								listInventoryItems.add(map);																							
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
							}															
														
						}
					} else {
						Map<String, Object> inventoryItem = FastMap.newInstance();
						Timestamp datetimeReceived = new Timestamp(new Date().getTime());
						inventoryItem.put("productId", productId);

						GenericValue party = null;
						if (SalesPartyUtil.isDistributor(delegator, partyId)){
							party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
							if (UtilValidate.isNotEmpty(party)) {
								inventoryItem.put("currencyUomId", party.get("preferredCurrencyUomId"));
							} else {
								inventoryItem.put("currencyUomId","VND");
							}
						} else {
							party = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", partyId));
							if (UtilValidate.isNotEmpty(party)) {
								inventoryItem.put("currencyUomId", party.get("baseCurrencyUomId"));
							} else {
								inventoryItem.put("currencyUomId","VND");
							}
						}			
						inventoryItem.put("unitCost", BigDecimal.ZERO);
						inventoryItem.put("purCost", BigDecimal.ZERO);					
						inventoryItem.put("datetimeReceived", datetimeReceived);
						inventoryItem.put("facilityId", facilityId);
						inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						inventoryItem.put("ownerPartyId", partyId);					
						inventoryItem.put("comments", "STOCKEVENT");
						inventoryItem.put("userLogin", system);
						String inventoryItemId = null;
						try {
							Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", inventoryItem);
							inventoryItemId = (String) mapTmp.get("inventoryItemId");
																		
							Map<String, Object> map = FastMap.newInstance();
							map.putAll(item);
							map.put("quantityOnHandVar", quantity.abs().negate().toString());
							map.put("inventoryItemId", inventoryItemId);
							listInventoryItems.add(map);																							
						} catch (GenericServiceException e) {
							return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
						}																					
//						return ServiceUtil.returnError("PRODUCT_NOT_FOUND");
					}
				}
			}
		}

		String physicalInventoryId = null;
		try {
			Map<String, Object> mapCreatePhysicalInv = UtilMisc.toMap("userLogin", system, "partyId", partyId, "generalComments", generalComments, "physicalInventoryDate",
					physicalInventoryDate, "facilityId", facilityId, "eventId", eventId);
			Map<String, Object> mapReturnPhys = dispatcher.runSync("createPhysicalInventory", mapCreatePhysicalInv);
			physicalInventoryId = (String) mapReturnPhys.get("physicalInventoryId");
		} catch (GenericServiceException e) {
			return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
		}
		if (UtilValidate.isNotEmpty(physicalInventoryId) && !listInventoryItems.isEmpty()) {
			for (Map<String, Object> item : listInventoryItems) {
				String inventoryItemId = (String) item.get("inventoryItemId");
				if (UtilValidate.isNotEmpty(inventoryItemId) && UtilValidate.isNotEmpty(item.get("quantityOnHandVar")) && UtilValidate.isNotEmpty(item.get("varianceReasonId"))) {
					GenericValue existed = delegator.findOne("InventoryItemVariance", false, UtilMisc.toMap("inventoryItemId", item.get("inventoryItemId"), "physicalInventoryId", physicalInventoryId));
					BigDecimal quantityOnHandVar = BigDecimal.ZERO;
					if (item.get("quantityOnHandVar") instanceof String) {
						quantityOnHandVar = new BigDecimal((String) item.get("quantityOnHandVar"));
					} else {
						quantityOnHandVar = (BigDecimal)item.get("quantityOnHandVar");
					}
					GenericValue inv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
					String productId = (String) inv.getString("productId");
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String requireAmount = product.getString("requireAmount");
					Boolean reqAmount = false;
					if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
						reqAmount = true;
					}
					String baseQtyUomId = product.getString("quantityUomId");
					String baseWeightUomId = product.getString("weightUomId");
					if (reqAmount) {
						if (UtilValidate.isNotEmpty(item.get("weightUomId")) && UtilValidate.isNotEmpty(product.get("weightUomId"))) {
							String weightUomId = (String) item.get("weightUomId");
							if (!weightUomId.equals(baseWeightUomId)) {
								BigDecimal convert = LogisticsProductUtil.getConvertWeightNumber(delegator, productId, weightUomId, baseWeightUomId);
								if (UtilValidate.isNotEmpty(convert) && convert.compareTo(BigDecimal.ZERO) > 0) {
									quantityOnHandVar = quantityOnHandVar.multiply(convert);
								}
							}
						}
					} else {
						if (UtilValidate.isNotEmpty(item.get("quantityUomId")) && UtilValidate.isNotEmpty(product.get("quantityUomId"))) {
							String quantityUomId = (String) item.get("quantityUomId");
							if (!quantityUomId.equals(baseQtyUomId)) {
								BigDecimal convert = LogisticsProductUtil.getConvertPackingNumber(delegator, productId, quantityUomId, baseQtyUomId);
								if (UtilValidate.isNotEmpty(convert) && convert.compareTo(BigDecimal.ZERO) > 0) {
									quantityOnHandVar = quantityOnHandVar.multiply(convert);
								}
							}
						}
					}
					BigDecimal availableToPromiseVar = quantityOnHandVar;
					BigDecimal qoh = inv.getBigDecimal("quantityOnHandTotal");
					if (reqAmount) {
						qoh = inv.getBigDecimal("amountOnHandTotal");
					}
					if (qoh!= null) {
						if ((availableToPromiseVar.compareTo(BigDecimal.ZERO) > 0) || (qoh.compareTo(availableToPromiseVar.abs()) >= 0 && availableToPromiseVar.compareTo(BigDecimal.ZERO) < 0) || (qoh.compareTo(availableToPromiseVar) >= 0 && availableToPromiseVar.compareTo(BigDecimal.ZERO)< 0 && qoh.compareTo(BigDecimal.ZERO) < 0) ) {
							if (UtilValidate.isNotEmpty(existed)) {
								try {
									Map<String, Object> mapNewInv = inv.getAllFields();
									mapNewInv.remove("inventoryItemId");
									mapNewInv.remove("quantityOnHandTotal");
									mapNewInv.remove("availableToPromiseTotal");
									mapNewInv.put("userLogin", system);
									String invNewId = null;
	
									Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", mapNewInv);
									invNewId = (String) mapTmp.get("inventoryItemId");
	
										Map<String, Object> mapDetail = FastMap.newInstance();
										mapDetail.put("userLogin", system);
										mapDetail.put("inventoryItemId", inventoryItemId);
										mapDetail.put("quantityOnHandDiff", quantityOnHandVar.abs().negate());
										mapDetail.put("availableToPromiseDiff", quantityOnHandVar.abs().negate());
										if (reqAmount){
											mapDetail.put("quantityOnHandDiff", new BigDecimal(-1));
											mapDetail.put("availableToPromiseDiff", new BigDecimal(-1));
											mapDetail.put("amountOnHandDiff", quantityOnHandVar.abs().negate());
										}
										dispatcher.runSync("createInventoryItemDetail", mapDetail);
	
									if (quantityOnHandVar.compareTo(BigDecimal.ZERO) != 0) {
										BigDecimal qohVar = quantityOnHandVar;
										BigDecimal atpVar = availableToPromiseVar;
										BigDecimal amountVar = BigDecimal.ZERO;
										if (reqAmount){
											qohVar = BigDecimal.ONE;
											if (quantityOnHandVar.compareTo(BigDecimal.ZERO) < 0) qohVar = qohVar.negate();
											atpVar = qohVar;
											amountVar = quantityOnHandVar;
										}
										try {
											Map<String, Object> mapCreateInvItemVar = UtilMisc
													.toMap("userLogin", system,
															"inventoryItemId", invNewId,
															"physicalInventoryId", physicalInventoryId,
															"varianceReasonId", item.get("varianceReasonId"),
															"quantityOnHandVar", qohVar,
															"amountOnHandVar", amountVar,
															"availableToPromiseVar", atpVar,
															"comments", item.get("comments"));
											dispatcher.runSync("createInventoryItemVariance", mapCreateInvItemVar);
										} catch (GenericServiceException e) {
											return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
										}
									}
								} catch (GenericServiceException e) {
									return ServiceUtil
											.returnError("OLBIUS: createInventoryItem runsync service error!");
								}
							} else {
								if (quantityOnHandVar.compareTo(BigDecimal.ZERO) != 0) {
									BigDecimal qohVar = quantityOnHandVar;
									BigDecimal atpVar = availableToPromiseVar;
									BigDecimal amountVar = BigDecimal.ZERO;
									if (reqAmount){
										qohVar = BigDecimal.ONE;
										if (quantityOnHandVar.compareTo(BigDecimal.ZERO) < 0) qohVar = qohVar.negate();
										atpVar = qohVar;
										amountVar = quantityOnHandVar;
									}
									try {
										Map<String, Object> mapCreateInvItemVar = UtilMisc
												.toMap("userLogin", system,
														"inventoryItemId", item.get("inventoryItemId"),
														"physicalInventoryId", physicalInventoryId,
														"varianceReasonId", item.get("varianceReasonId"),
														"quantityOnHandVar", qohVar,
														"amountOnHandVar", amountVar,
														"availableToPromiseVar", atpVar,
														"comments", item.get("comments"));
										dispatcher.runSync("createInventoryItemVariance", mapCreateInvItemVar);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
									}
								}
							}
						} else {
							return ServiceUtil.returnError("QUANTITY_NOT_ENOUGH");
						}
					} else {
						if (quantityOnHandVar.compareTo(BigDecimal.ZERO) != 0) {
							BigDecimal qohVar = quantityOnHandVar;
							BigDecimal atpVar = availableToPromiseVar;
							BigDecimal amountVar = BigDecimal.ZERO;
							if (reqAmount){
								qohVar = BigDecimal.ONE;
								if (quantityOnHandVar.compareTo(BigDecimal.ZERO) < 0) qohVar = qohVar.negate();
								atpVar = qohVar;
								amountVar = quantityOnHandVar;
							}
							try {
								Map<String, Object> mapCreateInvItemVar = UtilMisc
										.toMap("userLogin", system,
												"inventoryItemId", item.get("inventoryItemId"),
												"physicalInventoryId", physicalInventoryId,
												"varianceReasonId", item.get("varianceReasonId"),
												"quantityOnHandVar", qohVar,
												"amountOnHandVar", amountVar,
												"availableToPromiseVar", atpVar,
												"comments", item.get("comments"));
								dispatcher.runSync("createInventoryItemVariance", mapCreateInvItemVar);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError("OLBIUS: create physicalInventory error!");
							}
						}
					}
					
				}
			}
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		mapReturn.put("physicalInventoryId", physicalInventoryId);
		mapReturn.put("eventId", eventId);
		return mapReturn;
	}
	
	
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> jqGetListInventoryGroupByDate(
			DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		EntityListIterator listIterator = null;
		String facilityId = null;
		if (parameters.get("facilityId") != null && parameters.get("facilityId").length > 0) {
			facilityId = parameters.get("facilityId")[0];
		}
		if (UtilValidate.isNotEmpty(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		String productId = null;
		if (parameters.get("productId") != null && parameters.get("productId").length > 0) {
			productId = parameters.get("productId")[0];
		}
		if (UtilValidate.isNotEmpty(productId)) {
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		}
		if (UtilValidate.isNotEmpty(company)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company));
		}
		if (listSortFields.isEmpty()) {
			listSortFields.add("-expireDate");
		}
		listIterator = EntityMiscUtil.processIterator(parameters, successResult, 
	   			 delegator, "InventoryItemGroupExpireManufacturedLot", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
	   			 null, null, listSortFields, opts);
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
    public static Map<String, Object> getListInventoryGroupByDate(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String facilityId = null;
		if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
			facilityId = (String)context.get("facilityId");
		}
		String productId = null;
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			productId = (String)context.get("productId");
		}
		if (UtilValidate.isNotEmpty(productId)) {
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		}
		if (UtilValidate.isNotEmpty(facilityId)) {
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		}
		if (UtilValidate.isNotEmpty(company)) {
			listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company));
		}
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null));
		conds.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INV_AVAILABLE"));
		EntityCondition cond = EntityCondition.makeCondition(conds, EntityOperator.OR);
		listAllConditions.add(cond);
		
		List<String> listSortFields = FastList.newInstance();
		listSortFields.add("expireDate");
		List<GenericValue> listInventoryItems = FastList.newInstance();
		try {
			listInventoryItems = delegator.findList("InventoryItemGroupExpireManufacturedLot", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
			return ServiceUtil.returnError("OLBIUS: findList InventoryItemGroupExpireManufacturedLot error!");
		}
		successResult.put("listInventory", listInventoryItems);
		return successResult;
	}
    
    public static Map<String, Object> updateProductFacilityHistory(DispatchContext ctx, Map<String, ? extends Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		String facilityId = null;
		if (UtilValidate.isNotEmpty(context.get("facilityId"))) {
			facilityId = (String)context.get("facilityId");
		}
		String productId = null;
		if (UtilValidate.isNotEmpty(context.get("productId"))) {
			productId = (String)context.get("productId");
		}
		
		Timestamp lastUpdated = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		
		GenericValue objProductFacility = null;
		try {
			objProductFacility = delegator.findOne("ProductFacility", false, UtilMisc.toMap("productId", productId, "facilityId", facilityId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductFacility: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isNotEmpty(objProductFacility)) {
			List<EntityCondition> conds = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
			EntityCondition cond2 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			EntityCondition cond3 = EntityCondition.makeCondition("lastUpdated", EntityOperator.EQUALS, lastUpdated);
			conds.add(cond1);
			conds.add(cond2);
			conds.add(cond3);
			List<GenericValue> list = FastList.newInstance();
			try {
				list = delegator.findList("ProductFacilityHistory", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductFacilityHistory: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			BigDecimal quantity = objProductFacility.getBigDecimal("lastInventoryCount");
			
			// cost
			conds = FastList.newInstance();
			EntityCondition cond4 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
			conds.add(cond1);
			conds.add(cond2);
			conds.add(cond4);
			List<GenericValue> listProductAverageCost = FastList.newInstance();
			try {
				listProductAverageCost = delegator.findList("ProductAverageCost", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductAverageCost: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			BigDecimal averageCost = BigDecimal.ZERO;
			if (!listProductAverageCost.isEmpty()) averageCost = listProductAverageCost.get(0).getBigDecimal("averageCost");
			if (!list.isEmpty()){
				GenericValue his = list.get(0);
				his.put("productId", productId);
				his.put("facilityId", facilityId);
				his.put("quantity", quantity);
				his.put("averageCost", averageCost);
				his.put("lastUpdated", lastUpdated);
				try {
					delegator.store(his);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: Store ProductFacilityHistory error!");
				}
			} else {
				GenericValue his = delegator.makeValue("ProductFacilityHistory");
				his.put("productId", productId);
				his.put("facilityId", facilityId);
				his.put("quantity", quantity);
				his.put("averageCost", averageCost);
				his.put("lastUpdated", lastUpdated);
				try {
					delegator.create(his);
				} catch (GenericEntityException e) {
					Debug.logError(e.toString(), module);
					return ServiceUtil.returnError("OLBIUS: Create ProductFacilityHistory error!");
				}
			}
		}
		
		successResult.put("productId", productId);
		successResult.put("facilityId", facilityId);
		return successResult;
	}

    public static Map<String, Object> balanceInventoryItemAndDetail(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException, GenericServiceException {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();

        String inventoryItemId = (String) context.get("inventoryItemId");
        String productId = (String) context.get("productId");
        String facilityId = (String) context.get("facilityId");
        String ownerPartyId = (String) context.get("ownerPartyId");
        if (UtilValidate.isNotEmpty(inventoryItemId)) {
            GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
            if (UtilValidate.isNotEmpty(inventoryItem)) {
                productId = inventoryItem.getString("productId");
                ownerPartyId = inventoryItem.getString("ownerPartyId");
                facilityId = inventoryItem.getString("facilityId");
            }
        }

        if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(facilityId) && UtilValidate.isNotEmpty(ownerPartyId)) {
            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
            String requireAmount = product.getString("requireAmount");

            List<EntityCondition> condsNegative = FastList.newInstance();
            condsNegative.add(EntityCondition.makeCondition("productId", productId));
            condsNegative.add(EntityCondition.makeCondition("ownerPartyId", ownerPartyId));
            condsNegative.add(EntityCondition.makeCondition("facilityId", facilityId));
            if ("Y".equals(requireAmount)) {
                condsNegative.add(EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.LESS_THAN, BigDecimal.ZERO));
            } else {
                condsNegative.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.LESS_THAN, BigDecimal.ZERO));
            }

            List<EntityCondition> condsPositive = FastList.newInstance();
            condsPositive.add(EntityCondition.makeCondition("productId", productId));
            condsPositive.add(EntityCondition.makeCondition("ownerPartyId", ownerPartyId));
            condsPositive.add(EntityCondition.makeCondition("facilityId", facilityId));
            if ("Y".equals(requireAmount)) {
                condsPositive.add(EntityCondition.makeCondition("amountOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            } else {
                condsPositive.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            }

            List<String> listSortFields = FastList.newInstance();
            if ("Y".equals(requireAmount)) {
                listSortFields.add("-amountOnHandTotal");
            } else {
                listSortFields.add("-quantityOnHandTotal");
            }

            List<GenericValue> inventoryItemNegativeList = delegator.findList("InventoryItem",
                    EntityCondition.makeCondition(condsNegative), null, UtilMisc.toList("-datetimeReceived"), null, false);
            List<GenericValue> inventoryItemPositiveList = delegator.findList("InventoryItem",
                    EntityCondition.makeCondition(condsPositive), null, listSortFields, null, false);
            Map<String, Object> resultService = null;
            Map<String, Object> inventoryItemDetailMap = FastMap.newInstance();
            if (UtilValidate.isNotEmpty(inventoryItemNegativeList) && UtilValidate.isNotEmpty(inventoryItemPositiveList)) {
                for (GenericValue itemNegative : inventoryItemNegativeList) {
                    GenericValue inventoryItemNegative = itemNegative;
                    String inventoryItemIdNegative = inventoryItemNegative.getString("inventoryItemId");
                    BigDecimal quantityOnHandTotalNegative = inventoryItemNegative.getBigDecimal("quantityOnHandTotal");
                    BigDecimal amountOnHandTotalNegative = inventoryItemNegative.getBigDecimal("amountOnHandTotal");
                    BigDecimal unitCostNegative = inventoryItemNegative.getBigDecimal("unitCost");
                    BigDecimal purCostNegative = inventoryItemNegative.getBigDecimal("purCost");

                    for (GenericValue itemPositive : inventoryItemPositiveList) {
                        if ("Y".equals(requireAmount)) {
                            if (amountOnHandTotalNegative.equals(BigDecimal.ZERO))
                                break;
                        } else {
                            if (quantityOnHandTotalNegative.equals(BigDecimal.ZERO))
                                break;
                        }
                        GenericValue inventoryItemPositive = itemPositive;
                        String inventoryItemIdPositive = inventoryItemPositive.getString("inventoryItemId");
                        BigDecimal quantityOnHandTotalPositive = inventoryItemPositive.getBigDecimal("quantityOnHandTotal");
                        BigDecimal amountOnHandTotalPositive = inventoryItemPositive.getBigDecimal("amountOnHandTotal");
                        BigDecimal unitCostPositive = inventoryItemPositive.getBigDecimal("unitCost");
                        BigDecimal purCostPositive = inventoryItemPositive.getBigDecimal("purCost");

                        String description = UtilProperties.getMessage(resource, "BLBalanceInventoryItem",
                                UtilMisc.toMap("inventoryItemId", inventoryItemIdNegative, "newInventoryItemId", inventoryItemIdPositive), locale);

                        BigDecimal amountOnHandDiff = null;
                        BigDecimal quantityOnHandDiff = null;
                        if ("Y".equals(requireAmount)) {
                            amountOnHandDiff = amountOnHandTotalNegative.negate().compareTo(amountOnHandTotalPositive) <= 0
                                    ? amountOnHandTotalNegative.negate() : amountOnHandTotalPositive;
                            amountOnHandTotalNegative = amountOnHandTotalNegative.add(amountOnHandDiff);
                        } else {
                            quantityOnHandDiff = quantityOnHandTotalNegative.negate().compareTo(quantityOnHandTotalPositive) <= 0
                                    ? quantityOnHandTotalNegative.negate() : quantityOnHandTotalPositive;
                            quantityOnHandTotalNegative = quantityOnHandTotalNegative.add(quantityOnHandDiff);
                        }

                        inventoryItemDetailMap.clear();
                        inventoryItemDetailMap.put("userLogin", userLogin);
                        inventoryItemDetailMap.put("inventoryItemId", inventoryItemIdNegative);
                        if ("Y".equals(requireAmount)) {
                            inventoryItemDetailMap.put("availableToPromiseDiff", BigDecimal.ONE);
                            inventoryItemDetailMap.put("quantityOnHandDiff", BigDecimal.ONE);
                            inventoryItemDetailMap.put("amountOnHandDiff", amountOnHandDiff);
                        } else {
                            inventoryItemDetailMap.put("availableToPromiseDiff", quantityOnHandDiff);
                            inventoryItemDetailMap.put("quantityOnHandDiff", quantityOnHandDiff);
                        }
                        inventoryItemDetailMap.put("unitCost", unitCostNegative);
                        inventoryItemDetailMap.put("purCost", purCostNegative);
                        inventoryItemDetailMap.put("description", description);
                        resultService = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailMap);
                        if(!ServiceUtil.isSuccess(resultService)){
                            return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                        }

                        inventoryItemDetailMap.clear();
                        inventoryItemDetailMap.put("userLogin", userLogin);
                        inventoryItemDetailMap.put("inventoryItemId", inventoryItemIdPositive);
                        if ("Y".equals(requireAmount)) {
                            inventoryItemDetailMap.put("availableToPromiseDiff", BigDecimal.ONE.negate());
                            inventoryItemDetailMap.put("quantityOnHandDiff", BigDecimal.ONE.negate());
                            inventoryItemDetailMap.put("amountOnHandDiff", amountOnHandDiff.negate());
                        } else {
                            inventoryItemDetailMap.put("availableToPromiseDiff", quantityOnHandDiff.negate());
                            inventoryItemDetailMap.put("quantityOnHandDiff", quantityOnHandDiff.negate());
                        }
                        inventoryItemDetailMap.put("unitCost", unitCostPositive);
                        inventoryItemDetailMap.put("purCost", purCostPositive);
                        inventoryItemDetailMap.put("description", description);
                        resultService = dispatcher.runSync("createInventoryItemDetail", inventoryItemDetailMap);
                        if(!ServiceUtil.isSuccess(resultService)){
                            return ServiceUtil.returnError(ErrorUtils.getErrorMessageFromService(resultService));
                        }
                    }
                }
            }
        }

        return successResult;
    }
}