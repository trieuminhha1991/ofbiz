package com.olbius.basepos.formular;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

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
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FormularServices {
	public static int scale = 2;
	public static String module = FormularServices.class.getName();
	public static String nameService = "updateProductSummary";
	public static String resourceError = "WebPosSettingErrorUiLabels";

	public static void updateProductSummaryService(Delegator delegator, String status) throws GenericEntityException {
		GenericValue productSummaryService = null;
		productSummaryService = delegator.findOne("ProductSummaryServices", UtilMisc.toMap("service", nameService),
				false);
		if (UtilValidate.isNotEmpty(productSummaryService)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			productSummaryService.put("status", status);
			if (status.equals("COMPLETED")) {
				productSummaryService.put("lastUpdated", nowTimestamp);
			}
			productSummaryService.store();
		} else {
			productSummaryService = delegator.makeValue("ProductSummaryServices");
			productSummaryService.put("service", nameService);
			productSummaryService.put("status", "PROCESSING");
			productSummaryService.create();
		}
	};

	// converting updateProductSummary event in formularEvent to service
	public static Map<String, Object> updateProductSummary(DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException, ScriptException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String formular = (String) context.get("formular");
		List<GenericValue> productList = FastList.newInstance();
		productList = delegator.findList("ProductAndProductCategoryMember", null, null, null, null, false);
		List<GenericValue> facilityList = FastList.newInstance();
		EntityCondition facilityCond = EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,
				"RETAIL_STORE");
		facilityList = delegator.findList("Facility", facilityCond, null, null, null, false);
		if (UtilValidate.isNotEmpty(facilityList) && UtilValidate.isNotEmpty(productList)) {
			Map<String, Long> getPeriodType = FastMap.newInstance();
			getPeriodType = FormularEvents.getPeriodType(delegator);
			Long shortPeriodTmp = getPeriodType.get("shortPeriodLength");
			Long longPeriodTmp = getPeriodType.get("longPeriodLength");
			BigDecimal shortPeriod = new BigDecimal(shortPeriodTmp);
			BigDecimal longPeriod = new BigDecimal(longPeriodTmp);

			for (GenericValue product : productList) {
				String productId = product.getString("productId");
				String internalName = product.getString("internalName");
				String quantityUomId = product.getString("quantityUomId");
				BigDecimal qoh = BigDecimal.ZERO;
				BigDecimal qoo = BigDecimal.ZERO;
				BigDecimal qtyL = BigDecimal.ZERO;
				BigDecimal qtyS = BigDecimal.ZERO;
				BigDecimal qpdL = BigDecimal.ZERO;
				BigDecimal qpdS = BigDecimal.ZERO;
				BigDecimal lidL = BigDecimal.ZERO;
				BigDecimal lidS = BigDecimal.ZERO;
				Timestamp lastSold = null;
				Timestamp lastReceived = null;
				BigDecimal unitCost = BigDecimal.ZERO;
				String status = null;
				int count = 0;
				for (GenericValue facility : facilityList) {
					count++;
					String facilityId = facility.getString("facilityId");
					// update product facility summary
					Map<String, Object> updatePFSummaryMap = FastMap.newInstance();
					updatePFSummaryMap.put("userLogin", userLogin);
					updatePFSummaryMap.put("productId", productId);
					updatePFSummaryMap.put("facilityId", facilityId);
					updatePFSummaryMap.put("longPeriod", longPeriod);
					updatePFSummaryMap.put("shortPeriod", shortPeriod);
					if (UtilValidate.isNotEmpty(formular)) {
						updatePFSummaryMap.put("formular", formular);
					}

					Map<String, Object> udpatePFSummary = FastMap.newInstance();
					udpatePFSummary = dispatcher.runSync("updateProductFacilityInfo", updatePFSummaryMap);
					if (!ServiceUtil.isSuccess(udpatePFSummary)) {
						return updatePFSummaryMap;
					}
					// update product summary
					Map<String, String> productFacilitySummaryMap = FastMap.newInstance();
					productFacilitySummaryMap.put("productId", productId);
					productFacilitySummaryMap.put("facilityId", facilityId);
					GenericValue productFacilitySummary = null;
					productFacilitySummary = delegator.findOne("ProductFacilitySummary", productFacilitySummaryMap,
							false);
					if (UtilValidate.isNotEmpty(productFacilitySummary)) {
						BigDecimal qohTmp = productFacilitySummary.getBigDecimal("qoh");
						BigDecimal qooTmp = productFacilitySummary.getBigDecimal("qoo");
						BigDecimal qtyLTmp = productFacilitySummary.getBigDecimal("qtyL");
						BigDecimal qtySTmp = productFacilitySummary.getBigDecimal("qtyS");
						Timestamp lastSoldTmp = productFacilitySummary.getTimestamp("lastSold");
						Timestamp lastReceivedTmp = productFacilitySummary.getTimestamp("lastReceived");
						if (UtilValidate.isNotEmpty(qohTmp)) {
							qoh = qoh.add(qohTmp);
						}
						if (UtilValidate.isNotEmpty(qooTmp)) {
							qoo = qoo.add(qooTmp);
						}
						if (UtilValidate.isNotEmpty(qtyLTmp)) {
							qtyL = qtyL.add(qtyLTmp);
						}
						if (UtilValidate.isNotEmpty(qtySTmp)) {
							qtyS = qtyS.add(qtySTmp);
						}
						if (UtilValidate.isNotEmpty(lastSoldTmp)) {
							if (UtilValidate.isNotEmpty(lastSold)) {
								if (lastSold.before(lastSoldTmp)) {
									lastSold = lastSoldTmp;
								}
							} else {
								lastSold = lastSoldTmp;
							}
						}
						if (UtilValidate.isNotEmpty(lastReceivedTmp)) {
							if (UtilValidate.isNotEmpty(lastReceived)) {
								if (lastReceived.before(lastReceivedTmp)) {
									lastReceived = lastReceivedTmp;
								}
							} else {
								lastReceived = lastReceivedTmp;
							}
						}
						BigDecimal unitCostTmp = productFacilitySummary.getBigDecimal("unitCost");
						if (UtilValidate.isNotEmpty(unitCostTmp)) {
							unitCost = unitCost.add(unitCostTmp);
						}
					}
				}
				BigDecimal countTmp = BigDecimal.valueOf(count);
				unitCost = unitCost.divide(countTmp, scale, RoundingMode.HALF_UP);
				qpdL = qtyL.divide(longPeriod, scale, RoundingMode.HALF_UP);
				qpdS = qtyS.divide(shortPeriod, scale, RoundingMode.HALF_UP);
				lidL = FormularServices.getLastInDay(qoh, qoo, qpdL);
				lidS = FormularServices.getLastInDay(qoh, qoo, qpdS);
				/*
				 * if(UtilValidate.isEmpty(formular)){ GenericValue
				 * productSummaryService =
				 * delegator.findOne("ProductSummaryServices",
				 * UtilMisc.toMap("service", "updateProductSummary"), false); }
				 */
				if (UtilValidate.isNotEmpty(formular)) {
					status = FormularEvents.calculateFormular(formular, lastSold, qoh, qoo, lidL, lidS, qpdL, qpdS,
							qtyL, qtyS, longPeriod, shortPeriod);
				}

				GenericValue productSummary = null;
				productSummary = delegator.findOne("ProductSummary", UtilMisc.toMap("productId", productId), false);
				if (UtilValidate.isNotEmpty(productSummary)) {
					Map<String, Object> updateProductSummaryMap = FastMap.newInstance();
					updateProductSummaryMap.put("userLogin", userLogin);
					updateProductSummaryMap.put("productId", productId);
					updateProductSummaryMap.put("internalName", internalName);
					updateProductSummaryMap.put("quantityUomId", quantityUomId);
					updateProductSummaryMap.put("qpdL", qpdL);
					updateProductSummaryMap.put("qpdS", qpdS);
					updateProductSummaryMap.put("qtyL", qtyL);
					updateProductSummaryMap.put("qtyS", qtyS);
					updateProductSummaryMap.put("lidL", lidL);
					updateProductSummaryMap.put("lidS", lidS);
					updateProductSummaryMap.put("qoh", qoh);
					updateProductSummaryMap.put("qoo", qoo);
					updateProductSummaryMap.put("lastSold", lastSold);
					updateProductSummaryMap.put("lastReceived", lastReceived);
					updateProductSummaryMap.put("unitCost", unitCost);
					updateProductSummaryMap.put("status", status);

					Map<String, Object> updateProductSummary = FastMap.newInstance();
					updateProductSummary = dispatcher.runSync("updateProductSummary", updateProductSummaryMap);
					if (!ServiceUtil.isSuccess(updateProductSummary)) {
						return updateProductSummary;
					}
				} else {
					Map<String, Object> createProductSummaryMap = FastMap.newInstance();
					createProductSummaryMap.put("userLogin", userLogin);
					createProductSummaryMap.put("productId", productId);
					createProductSummaryMap.put("internalName", internalName);
					createProductSummaryMap.put("quantityUomId", quantityUomId);
					createProductSummaryMap.put("qpdL", qpdL);
					createProductSummaryMap.put("qpdS", qpdS);
					createProductSummaryMap.put("qtyL", qtyL);
					createProductSummaryMap.put("qtyS", qtyS);
					createProductSummaryMap.put("lidL", lidL);
					createProductSummaryMap.put("lidS", lidS);
					createProductSummaryMap.put("qoh", qoh);
					createProductSummaryMap.put("qoo", qoo);
					createProductSummaryMap.put("lastSold", lastSold);
					createProductSummaryMap.put("lastReceived", lastReceived);
					createProductSummaryMap.put("unitCost", unitCost);
					createProductSummaryMap.put("status", status);
					Map<String, Object> createProductSummary = FastMap.newInstance();
					createProductSummary = dispatcher.runSync("createProductSummary", createProductSummaryMap);
					if (!ServiceUtil.isSuccess(createProductSummary)) {
						return createProductSummary;
					}
				}
			}
		}
		return result;
	}

	public static Map<String, BigDecimal> getPeriodType(Delegator delegator) throws GenericEntityException {
		Map<String, BigDecimal> result = FastMap.newInstance();
		BigDecimal shortPeriodLength = null;
		BigDecimal longPeriodLength = null;

		GenericValue shortPeriodEntity = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "ShortPeriod"),
				false);
		if (UtilValidate.isNotEmpty(shortPeriodEntity)) {
			shortPeriodLength = shortPeriodEntity.getBigDecimal("periodLength");
		}
		GenericValue longPeriodEntity = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "LongPeriod"),
				false);
		if (UtilValidate.isNotEmpty(longPeriodEntity)) {
			longPeriodLength = longPeriodEntity.getBigDecimal("periodLength");
		}
		result.put("shortPeriodLength", shortPeriodLength);
		result.put("longPeriodLength", longPeriodLength);
		return result;
	}

	public static Map<String, Object> updateProductFacilityInfo(DispatchContext dctx,
			Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException, ScriptException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("facilityId");
		String internalName = null;
		String quantityUomId = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		BigDecimal longPeriod = (BigDecimal) context.get("longPeriod");
		BigDecimal shortPeriod = (BigDecimal) context.get("shortPeriod");
		BigDecimal qoh = BigDecimal.ZERO;
		BigDecimal qoo = BigDecimal.ZERO;
		BigDecimal qtyL = BigDecimal.ZERO;
		BigDecimal qtyS = BigDecimal.ZERO;
		BigDecimal qpdL = BigDecimal.ZERO;
		BigDecimal qpdS = BigDecimal.ZERO;
		BigDecimal lidL = BigDecimal.ZERO;
		BigDecimal lidS = BigDecimal.ZERO;
		BigDecimal unitCost = BigDecimal.ZERO;
		String status = null;

		String formular = (String) context.get("formular");
		Timestamp lastSold = null;
		Timestamp lastReceived = null;
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		if (UtilValidate.isNotEmpty(product)) {
			internalName = product.getString("internalName");
			quantityUomId = product.getString("quantityUomId");
		}
		// unit cost
		unitCost = FormularEvents.getProductAverageCost(delegator, productId, facilityId);
		// sale order
		Map<String, Object> saleOrderInfoL = getSaleOrderInfo(delegator, productId, facilityId, longPeriod);
		if (UtilValidate.isNotEmpty(saleOrderInfoL)) {
			lastSold = (Timestamp) saleOrderInfoL.get("lastSold");
			qtyL = (BigDecimal) saleOrderInfoL.get("totalSale");
			qpdL = (BigDecimal) saleOrderInfoL.get("qpd");
		}

		Map<String, Object> saleOrderInfoS = getSaleOrderInfo(delegator, productId, facilityId, shortPeriod);
		if (UtilValidate.isNotEmpty(saleOrderInfoS)) {
			qtyS = (BigDecimal) saleOrderInfoS.get("totalSale");
			qpdS = (BigDecimal) saleOrderInfoS.get("qpd");
		}
		// purchase order
		Map<String, Object> purchaseOrderInfo = getPurchaseOrderInfo(delegator, productId, facilityId);
		if (UtilValidate.isNotEmpty(purchaseOrderInfo)) {
			qoo = (BigDecimal) purchaseOrderInfo.get("qoo");
		}
		// receiving
		Map<String, Object> receivingInfo = getReceivingInfo(delegator, productId, facilityId);
		if (UtilValidate.isNotEmpty(receivingInfo)) {
			lastReceived = (Timestamp) receivingInfo.get("lastReceived");
		}

		qoh = getQuantityOnHand(delegator, productId, facilityId);

		lidL = getLastInDay(qoh, qoo, qpdL);
		lidS = getLastInDay(qoh, qoo, qpdS);
		// calculate formular
		if (UtilValidate.isNotEmpty(formular)) {
			status = FormularEvents.calculateFormular(formular, lastSold, qoh, qoo, lidL, lidS, qpdL, qpdS, qtyL, qtyS,
					longPeriod, shortPeriod);
		}

		Map<String, String> productFacilitySummaryMap = FastMap.newInstance();
		productFacilitySummaryMap.put("productId", productId);
		productFacilitySummaryMap.put("facilityId", facilityId);
		GenericValue productFacilitySummary = delegator.findOne("ProductFacilitySummary", productFacilitySummaryMap,
				false);
		if (UtilValidate.isNotEmpty(productFacilitySummary)) {
			Map<String, Object> updatePFMap = FastMap.newInstance();
			updatePFMap.put("userLogin", userLogin);
			updatePFMap.put("productId", productId);
			updatePFMap.put("internalName", internalName);
			updatePFMap.put("quantityUomId", quantityUomId);
			updatePFMap.put("facilityId", facilityId);
			updatePFMap.put("qoh", qoh);
			updatePFMap.put("qoo", qoo);
			updatePFMap.put("lastSold", lastSold);
			updatePFMap.put("lastReceived", lastReceived);
			updatePFMap.put("qtyL", qtyL);
			updatePFMap.put("qtyS", qtyS);
			updatePFMap.put("qpdL", qpdL);
			updatePFMap.put("qpdS", qpdS);
			updatePFMap.put("lidL", lidL);
			updatePFMap.put("lidS", lidS);
			updatePFMap.put("unitCost", unitCost);
			updatePFMap.put("status", status);
			Map<String, Object> updatePF = dispatcher.runSync("updateProductFacilitySummary", updatePFMap);
			return updatePF;
		} else {
			Map<String, Object> createPFMap = FastMap.newInstance();
			createPFMap.put("userLogin", userLogin);
			createPFMap.put("productId", productId);
			createPFMap.put("internalName", internalName);
			createPFMap.put("quantityUomId", quantityUomId);
			createPFMap.put("facilityId", facilityId);
			createPFMap.put("qoh", qoh);
			createPFMap.put("qoo", qoo);
			createPFMap.put("lastSold", lastSold);
			createPFMap.put("lastReceived", lastReceived);
			createPFMap.put("qtyL", qtyL);
			createPFMap.put("qtyS", qtyS);
			createPFMap.put("qpdL", qpdL);
			createPFMap.put("qpdS", qpdS);
			createPFMap.put("lidL", lidL);
			createPFMap.put("lidS", lidS);
			createPFMap.put("unitCost", unitCost);
			createPFMap.put("status", status);
			Map<String, Object> createPF = dispatcher.runSync("createProductFacilitySummary", createPFMap);
			return createPF;
		}
	}

	public static BigDecimal getLastInDay(BigDecimal qoh, BigDecimal qoo, BigDecimal qpd) {
		BigDecimal lastInDay = BigDecimal.ZERO;
		if (UtilValidate.isNotEmpty(qpd) && qpd.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal total = qoh.add(qoo);
			lastInDay = (total).divide(qpd, scale, RoundingMode.HALF_UP);
		}
		return lastInDay;
	}

	public static BigDecimal getQuantityOnHand(Delegator delegator, String productId, String facilityId)
			throws GenericEntityException {
		BigDecimal qoh = BigDecimal.ZERO;
		Map<String, String> conditions = FastMap.newInstance();
		conditions.put("productId", productId);
		conditions.put("facilityId", facilityId);
		GenericValue productFaciltiy = delegator.findOne("ProductFacility", conditions, false);
		if (UtilValidate.isNotEmpty(productFaciltiy)) {
			BigDecimal lastInventoryCount = productFaciltiy.getBigDecimal("lastInventoryCount");
			if (UtilValidate.isNotEmpty(lastInventoryCount)) {
				qoh = lastInventoryCount;
			}
		}
		return qoh;
	}

	public static Map<String, Object> getSaleOrderInfo(Delegator delegator, String productId, String facilityId,
			BigDecimal periodLength) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();

		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		int periodLengthTmp = periodLength.intValue();
		Timestamp startTime = UtilDateTime.getDayStart(nowTimestamp, periodLengthTmp * (-1));
		Timestamp lastSold = null;
		// sale
		BigDecimal totalSale = BigDecimal.ZERO;
		BigDecimal totalReturn = BigDecimal.ZERO;
		BigDecimal qpd = null;
		List<GenericValue> saleList = FastList.newInstance();
		List<EntityCondition> saleConditionList = FastList.newInstance();
		saleConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		saleConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		saleConditionList
				.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, startTime));
		saleConditionList
				.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
		EntityCondition saleCond = EntityCondition.makeCondition(saleConditionList);
		saleList = delegator.findList("SaleOrderItem", saleCond, null, null, null, false);
		if (UtilValidate.isNotEmpty(saleList)) {
			for (GenericValue saleOrder : saleList) {
				BigDecimal quantity = saleOrder.getBigDecimal("quantity");
				Timestamp orderDate = saleOrder.getTimestamp("orderDate");
				if (UtilValidate.isNotEmpty(lastSold)) {
					if (orderDate.after(lastSold)) {
						lastSold = orderDate;
					}
				} else {
					lastSold = orderDate;
				}
				if (UtilValidate.isNotEmpty(quantity)) {
					totalSale = totalSale.add(quantity);
				}
			}
		}
		// return
		List<GenericValue> returnList = FastList.newInstance();
		List<EntityCondition> returnConditionList = FastList.newInstance();
		returnConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		returnConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		returnConditionList
				.add(EntityCondition.makeCondition("returnDate", EntityOperator.GREATER_THAN_EQUAL_TO, startTime));
		returnConditionList
				.add(EntityCondition.makeCondition("returnDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
		EntityCondition returnCond = EntityCondition.makeCondition(returnConditionList);
		returnList = delegator.findList("ReturnHeaderAndShipmentReceipt", returnCond, null, null, null, false);
		if (UtilValidate.isNotEmpty(returnList)) {
			for (GenericValue returnOrder : returnList) {
				BigDecimal quantity = returnOrder.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(quantity)) {
					totalReturn = totalReturn.add(quantity);
				}
			}
		}

		BigDecimal quantity = totalSale.subtract(totalReturn);
		qpd = quantity.divide(periodLength, scale, RoundingMode.HALF_UP);
		result.put("lastSold", lastSold);
		result.put("totalSale", quantity);
		result.put("qpd", qpd);
		return result;

	}

	public static Map<String, Object> getPurchaseOrderInfo(Delegator delegator, String productId, String facilityId)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		List<GenericValue> purchaseOrderList = FastList.newInstance();
		BigDecimal quantityOnOrder = BigDecimal.ZERO;
		Map<String, String> mapConditions = FastMap.newInstance();
		mapConditions.put("productId", productId);
		mapConditions.put("facilityId", facilityId);
		EntityCondition condition = EntityCondition.makeCondition(mapConditions);
		purchaseOrderList = delegator.findList("PurchaseOrderItem", condition, null, null, null,
				false);
		if (UtilValidate.isNotEmpty(purchaseOrderList)) {
			for (GenericValue purchaseOrder : purchaseOrderList) {
				BigDecimal quantity = purchaseOrder.getBigDecimal("quantity");
				if (UtilValidate.isNotEmpty(quantity)) {
					quantityOnOrder = quantityOnOrder.add(quantity);
				}
			}
		}
		result.put("qoo", quantityOnOrder);
		return result;
	}

	public static Map<String, Object> getReceivingInfo(Delegator delegator, String productId, String facilityId)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
//		List<GenericValue> recevingList = FastList.newInstance();
//		EntityCondition condition = EntityCondition.makeCondition("returnId", EntityOperator.NOT_EQUAL, null);
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
		conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));
		List<GenericValue> inventoryItems = delegator.findList("InventoryItem",
				EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-datetimeReceived"), null, false);
		Timestamp lastReceived = null;
		if (UtilValidate.isNotEmpty(inventoryItems)) {
			lastReceived = EntityUtil.getFirst(inventoryItems).getTimestamp("datetimeReceived");
		}
//		List<String> orderBy = FastList.newInstance();
//		orderBy.add("-datetimeReceived");
//		recevingList = delegator.findList("ShipmentReceipt", condition, null, orderBy, null, false);
//		if (UtilValidate.isNotEmpty(recevingList)) {
//			GenericValue shipmentReceipt = recevingList.get(0);
//			if (UtilValidate.isNotEmpty(shipmentReceipt)) {
//				lastReceived = shipmentReceipt.getTimestamp("datetimeReceived");
//			}
//		}
		result.put("lastReceived", lastReceived);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductSummary(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId");
		}
		listAllConditions.add(EntityCondition.makeCondition(
				EntityCondition.makeCondition("purchaseDiscontinuationDate", EntityOperator.EQUALS, null),
				EntityOperator.OR, EntityCondition.makeCondition("purchaseDiscontinuationDate",
						EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis()))));
		listAllConditions.add(EntityCondition.makeCondition("isVirtual", EntityJoinOperator.EQUALS, "N"));
		listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "FINISHED_GOOD"));
		try {
			listIterator = delegator.find("ProductSummaryAndProduct",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductSummary service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jQGetListProductFacilitySummary(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String[] productIds = parameters.get("productId");
		String productId = null;
		if (UtilValidate.isNotEmpty(productIds)) {
			productId = productIds[0];
		}
		if (UtilValidate.isNotEmpty(productId)) {
			EntityCondition productCond = EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
			listAllConditions.add(productCond);
		}
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields.add("productId");
		}
		try {
			listIterator = delegator.find("ProductFacilitySummaryAndFacility",
					EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,
					listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProductFacilitySummary service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
}
