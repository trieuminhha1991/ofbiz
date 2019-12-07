package com.olbius.basepo.plan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.plan.DailyProcessor;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PlanServices {
	public static final String module = PlanServices.class.getName();
	public static final int NUM_DATE_OF_WEEK = 7;
	public static final int NUM_DEFAULT_WEEK = 4;

	@SuppressWarnings("unchecked")
	public static Map<String, Object> storePlanAndStock(DispatchContext dpx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security security = dpx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE",
				"PLANPO_PLAN_EDIT");
		if (!hasPermission) {
			return ServiceUtil.returnError(
					UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotUpdatePermission", locale));
		}
		String productPlanId = (String) context.get("productPlanId");
		String dataPlans = (String) context.get("dataPlans");
		JSONArray arrPlans = JSONArray.fromObject(dataPlans);
		String dataEnds = (String) context.get("dataEnds");
		JSONArray arrEnds = JSONArray.fromObject(dataEnds);

		int size = arrPlans.size();
		for (int i = 0; i < size; i++) {
			JSONObject itemPlan = arrPlans.getJSONObject(i);
			JSONObject itemEnd = arrEnds.getJSONObject(i);
			Set<String> key = itemPlan.keySet();
			String productId = (String) itemPlan.get("productId");
			for (String k : key) {
				if (!k.equals("productId") && !k.equals("productCode") && !k.equals("productName") && !k.equals("uid")
						&& !k.equals("MOQ") && !k.equals("SalesCycle")) {
					String quantityPlan = "0";
					if (itemPlan.get(k) instanceof Integer) {
						quantityPlan = ((Integer) itemPlan.get(k)).toString();
					} else if (itemPlan.get(k) instanceof String) {
						quantityPlan = (String) itemPlan.get(k);
					}

					String quantityEnd = "0";
					if (itemEnd.get(k) instanceof Integer) {
						quantityEnd = ((Integer) itemEnd.get(k)).toString();
					} else if (itemEnd.get(k) instanceof String) {
						quantityEnd = (String) itemEnd.get(k);
					}

					Map<String, Object> map = FastMap.newInstance();
					map.put("productPlanId", productPlanId);
					map.put("customTimePeriodId", k);
					map.put("productId", productId);
					map.put("planQuantity", quantityPlan);
					map.put("inventoryForecast", quantityEnd);
					map.put("statusId", "PLANITEM_CREATED");
					map.put("userLogin", userLogin);

					try {
						dispatcher.runSync("updatePlanItemWeekPO", map);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError(e.getMessage());
					}
				}
			}
		}
		return result;
	}

	public static Map<String, Object> updatePlanItemWeek(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String productPlanId = (String) context.get("productPlanId");
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productId = (String) context.get("productId");
		String planQuantityStr = (String) context.get("planQuantity");
		BigDecimal planQuantity = new BigDecimal(Integer.parseInt(planQuantityStr));
		String statusId = (String) context.get("statusId");
		BigDecimal inventoryForecast = new BigDecimal(0);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if (context.get("inventoryForecast") != null) {
			String invForecastStr = (String) context.get("inventoryForecast");
			inventoryForecast = new BigDecimal(Integer.parseInt(invForecastStr));
		}

		try {
			GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader",
					UtilMisc.toMap("productPlanId", productPlanId), false);
			List<GenericValue> listProductPlanHeader = delegator
					.findList("ProductPlanHeader",
							EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "PO_PLAN",
									"customTimePeriodId", customTimePeriodId, "organizationPartyId",
									productPlanHeader.getString("organizationPartyId"))),
							null, null, null, true);

			if (UtilValidate.isEmpty(listProductPlanHeader)) {
				GenericValue productPlanHeaderNew = delegator.makeValue("ProductPlanHeader");
				String productPlanIdNew = delegator.getNextSeqId("ProductPlanHeader");
				productPlanHeaderNew.put("productPlanId", productPlanIdNew);
				productPlanHeaderNew.put("parentProductPlanId", productPlanId);
				productPlanHeaderNew.put("productPlanTypeId", "PO_PLAN");
				productPlanHeaderNew.put("customTimePeriodId", customTimePeriodId);
				productPlanHeaderNew.put("createByUserLoginId", userLogin.getString("userLoginId"));
				productPlanHeaderNew.put("organizationPartyId", productPlanHeader.getString("organizationPartyId"));
				productPlanHeaderNew.put("statusId", "PO_PLAN_CREATED");
				delegator.create(productPlanHeaderNew);

				GenericValue productPlanItem = delegator.makeValue("ProductPlanItem");
				productPlanItem.put("productPlanId", productPlanIdNew);
				productPlanItem.put("productId", productId);
				productPlanItem.put("customTimePeriodId", customTimePeriodId);
				productPlanItem.put("planQuantity", planQuantity);
				productPlanItem.put("inventoryForecast", inventoryForecast);
				productPlanItem.put("statusId", statusId);
				delegator.create(productPlanItem);
			} else {
				GenericValue productPlanHeaderGe = EntityUtil.getFirst(listProductPlanHeader);

				GenericValue productPlanItemNew = delegator.findOne("ProductPlanItem",
						UtilMisc.toMap("productPlanId", productPlanHeaderGe.getString("productPlanId"), "productId",
								productId, "customTimePeriodId", customTimePeriodId),
						false);
				if (productPlanItemNew != null) {
					productPlanItemNew.put("planQuantity", planQuantity);
					productPlanItemNew.put("inventoryForecast", inventoryForecast);
					productPlanItemNew.put("statusId", statusId);
					delegator.store(productPlanItemNew);
				} else {
					GenericValue productPlanItemNew2 = delegator.makeValue("ProductPlanItem");
					productPlanItemNew2.put("productPlanId", productPlanHeaderGe.getString("productPlanId"));
					productPlanItemNew2.put("productId", productId);
					productPlanItemNew2.put("customTimePeriodId", customTimePeriodId);
					productPlanItemNew2.put("planQuantity", planQuantity);
					productPlanItemNew2.put("inventoryForecast", inventoryForecast);
					productPlanItemNew2.put("statusId", statusId);
					delegator.create(productPlanItemNew2);
				}
			}

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JqxGetStockAndPlan(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();

		String productPlanId = (String) context.get("productPlanId");
		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		String checkedWeek = (String) context.get("checkedWeek");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		List<String> listFacility = FastList.newInstance();
		List<GenericValue> facilities = FastList.newInstance();
		try {
			facilities = delegator.findByAnd("Facility",
					UtilMisc.toMap("ownerPartyId", organizationPartyId, "facilityTypeId", "WAREHOUSE"), null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(facilities)) {
			for (GenericValue fa : facilities) {
				List<GenericValue> facilityParty = FastList.newInstance();
				try {
					facilityParty = delegator.findByAnd("FacilityParty",
							UtilMisc.toMap("facilityId", fa.getString("facilityId"), "roleTypeId", "FACILITY_ADMIN"),
							null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				if (UtilValidate.isEmpty(facilityParty)) {
					listFacility.add(fa.getString("facilityId"));
				}
			}
		}

		GenericValue productPlanHeader = null;
		try {
			productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId),
					false);
			Map<String, Object> mapReturn = getListWeekForPlan(delegator, fromMonth, toMonth, checkedWeek,
					organizationPartyId);
			List<Map<String, Object>> listHeader = (List<Map<String, Object>>) mapReturn.get("headerWeek");
			List<GenericValue> listTimeW = (List<GenericValue>) mapReturn.get("listTimeW");

			Map<String, Object> mapInv = FastMap.newInstance();
			Map<String, Object> mapInvEnd = FastMap.newInstance();
			Map<String, Object> mapChannelSales = FastMap.newInstance();

			// get inv and sales by week
			for (GenericValue week : listTimeW) {
				Date fromDateW = week.getDate("fromDate");
				Date thruDateW = week.getDate("thruDate");
				String customTimePeriodIdW = (String) week.getString("customTimePeriodId");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(fromDateW);
				calendar.add(Calendar.DATE, -1);
				calendar.setTime(thruDateW);
				calendar.add(Calendar.DATE, -1);

				Map<String, Object> reportInvMap = invByDateMap(fromDateW.getTime(), listFacility);
				Map<String, Object> reportInvEndMap = invByDateMap(thruDateW.getTime(), listFacility);
				Map<String, Object> reportSalesMap = channelSalesMap(fromDateW.getTime(), thruDateW.getTime(),
						organizationPartyId, "ORDER_COMPLETED");

				mapInv.put(customTimePeriodIdW, reportInvMap);
				mapInvEnd.put(customTimePeriodIdW, reportInvEndMap);
				mapChannelSales.put(customTimePeriodIdW, reportSalesMap);
			}

			List<Map<String, Object>> listReturnResult = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, null));
			conds.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
			List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(conds), null, null, null, false);
			for (GenericValue product : listProduct) {
				String productId = product.getString("productId");
				String productCode = product.getString("productCode");
				String productName = product.getString("internalName");

				BigDecimal openInvBefore = new BigDecimal(0);
				BigDecimal salesBefore = new BigDecimal(0);
				BigDecimal planBefore = new BigDecimal(0);

				Map<String, Object> sfcWeekMap = getListSfcByWeek(delegator, productId, organizationPartyId);
				Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, "Opening");
				Map<String, Object> mapSales = assignToMap(productId, productCode, productName, "Channel sales");
				Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, "Orders");
				Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, "Ending");
				Map<String, Object> dayCover = assignToMap(productId, productCode, productName, "Days cover duration");
				Map<String, Object> accuracyMap = assignToMap(productId, productCode, productName, "Forecast accuracy");

				for (GenericValue week : listTimeW) {
					// get open inv
					BigDecimal endInvBefore = openInvBefore.subtract(salesBefore).add(planBefore);
					BigDecimal remainHead = new BigDecimal(0);
					Date fromDateW = week.getDate("fromDate");
					Date thruDateW = week.getDate("thruDate");
					String customTimePeriodIdW = (String) week.getString("customTimePeriodId");

					// TODO:get open inv week olap
					Map<String, Object> reportMap = (Map<String, Object>) mapInv.get(customTimePeriodIdW);
					if (UtilValidate.isNotEmpty(reportMap) && reportMap.containsKey(productId)) {
						remainHead = (BigDecimal) reportMap.get(productId);
					}

					BigDecimal openInv = new BigDecimal(0);
					BigDecimal endInv = new BigDecimal(0);
					BigDecimal sales = new BigDecimal(0);
					BigDecimal plan = new BigDecimal(0);
					BigDecimal accuracy = new BigDecimal(0);

					// get SFC of this week
					BigDecimal sfcThisWeek = new BigDecimal(0);
					if (sfcWeekMap.containsKey(customTimePeriodIdW)) {
						sfcThisWeek = (BigDecimal) sfcWeekMap.get(customTimePeriodIdW);
					}
					// get SFC of 2 next week
					String nextTime1 = getNextCustomTimePeriodWeek(customTimePeriodIdW, delegator);
					BigDecimal saleFc1 = new BigDecimal(0);
					BigDecimal saleFc2 = new BigDecimal(0);
					if (nextTime1 != null && sfcWeekMap.containsKey(nextTime1)) {
						saleFc1 = (BigDecimal) sfcWeekMap.get(nextTime1);
					}
					String nextTime2 = getNextCustomTimePeriodWeek(nextTime1, delegator);
					if (nextTime2 != null && sfcWeekMap.containsKey(nextTime2)) {
						saleFc2 = (BigDecimal) sfcWeekMap.get(nextTime2);
					}

					if (fromDateW.compareTo(new Date()) <= 0) {
						openInv = remainHead;
						List<GenericValue> listProductPlanHeaderW = delegator.findList("ProductPlanHeader",
								EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "PO_PLAN",
										"customTimePeriodId", customTimePeriodIdW, "organizationPartyId",
										productPlanHeader.getString("organizationPartyId"))),
								null, null, null, true);
						if (UtilValidate.isNotEmpty(listProductPlanHeaderW)) {
							GenericValue productPlanW = EntityUtil.getFirst(listProductPlanHeaderW);
							GenericValue productPlanItem = delegator
									.findOne("ProductPlanItem",
											UtilMisc.toMap("productPlanId", productPlanW.getString("productPlanId"),
													"customTimePeriodId", customTimePeriodIdW, "productId", productId),
											true);
							if (productPlanItem != null) {
								if (productPlanItem.get("orderedQuantity") != null) {
									plan = (BigDecimal) productPlanItem.get("orderedQuantity");
								}
							}
						}
					} else {
						if (UtilValidate.isNotEmpty(reportMap)) {
							openInv = remainHead;
						} else {
							openInv = endInvBefore;
						}
						// sales = SFC this week
						sales = sfcThisWeek;
						// get number plan
						BigDecimal equalSalesAndStock = openInv.subtract(sfcThisWeek).subtract(saleFc1)
								.subtract(saleFc2);
						if (equalSalesAndStock.compareTo(new BigDecimal(1)) < 0) {
							BigDecimal moqProduct = new BigDecimal(1);
							BigDecimal plan1 = sfcThisWeek.add(saleFc1).add(saleFc2).subtract(openInv);
							BigDecimal plan2 = plan1.divide(moqProduct, 0, RoundingMode.UP);
							plan = moqProduct.multiply(plan2);
						}
					}

					if (thruDateW.compareTo(new Date()) <= 0) {
						Map<String, Object> reportThruMap = (Map<String, Object>) mapInvEnd.get(customTimePeriodIdW);
						if (UtilValidate.isNotEmpty(reportThruMap) && reportThruMap.containsKey(productId)) {
							endInv = (BigDecimal) reportThruMap.get(productId);
						}

						Map<String, Object> salesMapByWeek = (Map<String, Object>) mapChannelSales
								.get(customTimePeriodIdW);
						if (UtilValidate.isNotEmpty(salesMapByWeek) && salesMapByWeek.containsKey(productId)) {
							sales = (BigDecimal) salesMapByWeek.get(productId);
						}
					} else {
						sales = sfcThisWeek;
						BigDecimal equalSalesAndStock = openInv.subtract(sfcThisWeek).subtract(saleFc1)
								.subtract(saleFc2);
						if (equalSalesAndStock.compareTo(new BigDecimal(1)) < 0) {
							BigDecimal moqProduct = new BigDecimal(1);
							BigDecimal plan1 = sfcThisWeek.add(saleFc1).add(saleFc2).subtract(openInv);
							BigDecimal plan2 = plan1.divide(moqProduct, 0, RoundingMode.UP);
							plan = moqProduct.multiply(plan2);
						}
						endInv = openInv.subtract(sales).add(plan);
					}
					openInvBefore = openInv;
					salesBefore = sales;
					planBefore = plan;
					if (sfcThisWeek.compareTo(new BigDecimal(0)) > 0) {
						accuracy = sales.divide(sfcThisWeek, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
					}
					String customId = (String) week.get("customTimePeriodId");
					mapOpen.put(customId, openInv);
					mapSales.put(customId, sales);
					accuracyMap.put(customId, accuracy);
					mapOrder.put(customId, plan);
					mapEnding.put(customId, endInv);
					dayCover.put(customId, new BigDecimal(0));
				}
				listReturnResult.add(mapOpen);
				listReturnResult.add(mapSales);
				listReturnResult.add(accuracyMap);
				listReturnResult.add(mapOrder);
				listReturnResult.add(mapEnding);
				listReturnResult.add(dayCover);
			}

			result.put("listWeekHeader", listHeader);
			result.put("listIterator", listReturnResult);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		return result;
	}

	public static Map<String, Object> getListWeekForPlan(Delegator delegator, String fromMonth, String toMonth,
			String checkedWeek, String org) {
		Map<String, Object> mapReturn = FastMap.newInstance();

		Date current = new Date();
		Calendar calendar = Calendar.getInstance();
		int thisMonth = calendar.get(Calendar.MONTH);
		int thisDay = calendar.get(Calendar.DATE);
		calendar.setTime(current);
		calendar.set(Calendar.MONTH, thisMonth);
		calendar.set(Calendar.DATE, thisDay);
		java.sql.Date fromDateSql;
		java.sql.Date thruDateSql;
		if (fromMonth != null && toMonth != null && checkedWeek != null) {
			int fromM = Integer.parseInt(fromMonth);
			int thruM = Integer.parseInt(toMonth);
			Boolean checkedW = Boolean.parseBoolean(checkedWeek);

			if (checkedW) {
				calendar.add(Calendar.DATE, -NUM_DATE_OF_WEEK * fromM);
				fromDateSql = new java.sql.Date(calendar.getTimeInMillis());
				calendar.add(Calendar.DATE, NUM_DATE_OF_WEEK * fromM);
				calendar.add(Calendar.DATE, NUM_DATE_OF_WEEK * thruM);
				thruDateSql = new java.sql.Date(calendar.getTimeInMillis());
			} else {
				calendar.add(Calendar.MONTH, -fromM);
				fromDateSql = new java.sql.Date(calendar.getTimeInMillis());
				calendar.add(Calendar.MONTH, fromM);
				calendar.add(Calendar.MONTH, thruM);
				thruDateSql = new java.sql.Date(calendar.getTimeInMillis());
			}
		} else {
			calendar.add(Calendar.DATE, -NUM_DEFAULT_WEEK * NUM_DATE_OF_WEEK);
			fromDateSql = new java.sql.Date(calendar.getTimeInMillis());
			calendar.add(Calendar.DATE, 2 * NUM_DEFAULT_WEEK * NUM_DATE_OF_WEEK);
			thruDateSql = new java.sql.Date(calendar.getTimeInMillis());
		}

		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = FastList.newInstance();

		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,
				thruDateSql);
		EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				fromDateSql);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, org);
		EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_WEEK");
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		listAllConditions.add(cond4);

		try {
			List<String> orderBy = FastList.newInstance();
			orderBy.add("fromDate");
			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, orderBy, null, true);
			if (UtilValidate.isNotEmpty(listCustomTimePeriod)) {
				for (GenericValue time : listCustomTimePeriod) {
					Map<String, Object> map = FastMap.newInstance();
					map.put("customTimePeriodId", time.getString("customTimePeriodId"));
					map.put("periodName", time.getString("periodName"));
					listReturn.add(map);
				}
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		mapReturn.put("headerWeek", listReturn);
		mapReturn.put("listTimeW", listCustomTimePeriod);

		return mapReturn;
	}

	public static Map<String, Object> invByDateMap(long date, List<String> listFacility) {
		DailyProcessor dailyPro = new DailyProcessor();
		Map<String, Object> reportMap = dailyPro.getDataInventoryReport(date, date, listFacility);
		return reportMap;
	}

	public static Map<String, Object> channelSalesMap(long fromDate, long thruDate, String org, String orderStatusId) {
		DailyProcessor dailyPro = new DailyProcessor();
		Map<String, Object> salesMap = dailyPro.getDataSalesReport(fromDate, thruDate, org, orderStatusId);
		return salesMap;
	}

	public static Map<String, Object> assignToMap(String productId, String productCode, String productName,
			String SalesCycle) {
		Map<String, Object> map = FastMap.newInstance();
		map.put("productId", productId);
		map.put("productName", productName);
		map.put("productCode", productCode);
		map.put("SalesCycle", SalesCycle);
		return map;
	}

	public static String getNextCustomTimePeriodWeek(String customTimePeriodId, Delegator delegator) {
		String nextCustomTimePeriodId = null;
		GenericValue customTimePeriod = null;
		try {
			customTimePeriod = delegator.findOne("CustomTimePeriod",
					UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(customTimePeriod)) {
			List<GenericValue> nextCustomTime = FastList.newInstance();
			try {
				nextCustomTime = delegator.findByAnd("CustomTimePeriod",
						UtilMisc.toMap("fromDate", customTimePeriod.getDate("thruDate")), null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

			if (UtilValidate.isNotEmpty(nextCustomTime)) {
				nextCustomTimePeriodId = EntityUtil.getFirst(nextCustomTime).getString("customTimePeriodId");
			}
		}
		return nextCustomTimePeriodId;
	}

	public static Map<String, Object> getListSfcByWeek(Delegator delegator, String productId,
			String organizationPartyId) {
		Map<String, Object> sfcByWeekMap = FastMap.newInstance();
		List<GenericValue> salesForecast = FastList.newInstance();
		try {
			salesForecast = delegator
					.findByAnd(
							"SalesForecastAndPeriod", UtilMisc.toMap("periodTypeId", "COMMERCIAL_WEEK",
									"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId),
							null, false);
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}

		if (UtilValidate.isNotEmpty(salesForecast)) {
			for (GenericValue sfc : salesForecast) {
				List<GenericValue> listSfcDetail = FastList.newInstance();
				try {
					listSfcDetail = delegator.findList("SalesForecastDetail",
							EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId",
									sfc.getString("salesForecastId"), "productId", productId)),
							null, null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				BigDecimal sfcThisWeek = new BigDecimal(0);
				if (UtilValidate.isNotEmpty(listSfcDetail)) {
					GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
					sfcThisWeek = sfcDetail.getBigDecimal("quantity");
				}
				sfcByWeekMap.put(sfc.getString("customTimePeriodId"), sfcThisWeek);
			}
		}

		return sfcByWeekMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JqxGetSalesForecastByWeek(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		String checkedWeek = (String) context.get("checkedWeek");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		try {
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			Map<String, Object> mapReturn = getListWeekForPlan(delegator, fromMonth, toMonth, checkedWeek,
					organizationPartyId);
			List<Map<String, Object>> listHeader = (List<Map<String, Object>>) mapReturn.get("headerWeek");
			List<GenericValue> listTimeW = (List<GenericValue>) mapReturn.get("listTimeW");
			List<Map<String, Object>> listReturnResult = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, null));
			conds.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
			List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(conds), null, null, null, false);

			// break 1
			Map<String, Object> mapSalesFcWeek = FastMap.newInstance();
			for (GenericValue week : listTimeW) {
				String customTimePeriodId = week.getString("customTimePeriodId");
				Date fromDateW = week.getDate("fromDate");
				Date thruDateW = week.getDate("thruDate");
				Map<String, Object> mapWeek = getBreakDailySFC(delegator, listProduct, fromDateW, thruDateW,
						customTimePeriodId, userLoginId);
				mapSalesFcWeek.put(customTimePeriodId, mapWeek);
			}

			for (GenericValue product : listProduct) {
				Map<String, Object> map = FastMap.newInstance();
				String productId = product.getString("productId");
				String productCode = product.getString("productCode");
				String productName = product.getString("internalName");
				map.put("productId", productId);
				map.put("productCode", productCode);
				map.put("productName", productName);
				for (GenericValue week : listTimeW) {
					String customTimePeriodId = week.getString("customTimePeriodId");
					Map<String, Object> mapRe = (Map<String, Object>) mapSalesFcWeek.get(customTimePeriodId);
					BigDecimal quantity = (BigDecimal) mapRe.get(customTimePeriodId + "" + productId);
					map.put((String) week.get("customTimePeriodId"), quantity);
				}
				listReturnResult.add(map);
			}
			result.put("listWeekHeader", listHeader);
			result.put("listIterator", listReturnResult);
			if (UtilValidate.isNotEmpty(listTimeW)) {
				result.put("value", "success");
			} else {
				result.put("value", "error");
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getBreakDailySFC(Delegator delegator, List<GenericValue> listProduct,
			Date fromDate, Date thruDate, String customTimePeriodId, String userLoginId) {
		Map<String, Object> map = getBreakDailyTime(delegator, fromDate, thruDate, userLoginId);
		Map<String, Object> thisMonth = (Map<String, Object>) map.get("thisMonth");
		Map<String, Object> nextMonth = (Map<String, Object>) map.get("nextMonth");
		Map<String, Object> mapReturn = FastMap.newInstance();
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		for (GenericValue product : listProduct) {
			String productId = product.getString("productId");
			BigDecimal quantity = new BigDecimal(0);
			if (thruDate.compareTo(new Date()) >= 0) {
				if (thisMonth != null) {
					try {
						// FIXME : find SFC month by company String
						// MultiOrganizationUtil.getCurrentOrganization(delegator);
						List<GenericValue> listSFCThisMonth = delegator.findList("SalesForecast",
								EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId",
										(String) thisMonth.get("customTimePeriodId"), "organizationPartyId", company,
										"internalPartyId", company)),
								null, null, null, true);
						if (UtilValidate.isNotEmpty(listSFCThisMonth)) {
							GenericValue salesForecastThisMonth = EntityUtil.getFirst(listSFCThisMonth);
							String salesForecastIdThisMonth = salesForecastThisMonth.getString("salesForecastId");

							List<GenericValue> listSFCDetailThisMonth = delegator
									.findList("SalesForecastDetail",
											EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId",
													salesForecastIdThisMonth, "productId", productId)),
											null, null, null, true);
							if (UtilValidate.isNotEmpty(listSFCDetailThisMonth)) {
								GenericValue SFCDetailThisMonth = EntityUtil.getFirst(listSFCDetailThisMonth);
								BigDecimal quantitySFC = SFCDetailThisMonth.getBigDecimal("quantity");
								long day = (Long) thisMonth.get("day");
								int totalDay = (Integer) thisMonth.get("totalMonth");
								BigDecimal totalDayMonth = new BigDecimal(totalDay);
								BigDecimal dayW = new BigDecimal(day);
								BigDecimal dayBreak = quantitySFC.divide(totalDayMonth, RoundingMode.HALF_UP);
								BigDecimal weekBreak = dayBreak.multiply(dayW);
								quantity = quantity.add(weekBreak.setScale(0, RoundingMode.HALF_UP));
							}
						}
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}

				if (nextMonth != null) {
					try {
						// FIXME : find SFC month by company
						List<GenericValue> listSFCNextMonth = delegator.findList("SalesForecast",
								EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId",
										(String) nextMonth.get("customTimePeriodId"), "organizationPartyId", company,
										"internalPartyId", company)),
								null, null, null, true);
						if (UtilValidate.isNotEmpty(listSFCNextMonth)) {
							GenericValue salesForecastNextMonth = EntityUtil.getFirst(listSFCNextMonth);
							String salesForecastIdNextMonth = salesForecastNextMonth.getString("salesForecastId");

							List<GenericValue> listSFCDetailNextMonth = delegator
									.findList("SalesForecastDetail",
											EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId",
													salesForecastIdNextMonth, "productId", productId)),
											null, null, null, true);
							if (UtilValidate.isNotEmpty(listSFCDetailNextMonth)) {
								GenericValue SFCDetailNextMonth = EntityUtil.getFirst(listSFCDetailNextMonth);
								BigDecimal quantitySFCNext = SFCDetailNextMonth.getBigDecimal("quantity");
								long day = (Long) nextMonth.get("day");
								int totalDay = (Integer) nextMonth.get("totalMonth");
								BigDecimal totalDayMonth = new BigDecimal(totalDay);
								BigDecimal dayW = new BigDecimal(day);
								BigDecimal dayBreak = quantitySFCNext.divide(totalDayMonth, RoundingMode.HALF_UP);
								BigDecimal weekBreak = dayBreak.multiply(dayW);
								quantity = quantity.add(weekBreak.setScale(0, RoundingMode.HALF_UP));
							}
						}
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					List<GenericValue> listSFCThisMonth = delegator.findList("SalesForecast",
							EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId,
									"organizationPartyId", company, "internalPartyId", company)),
							null, null, null, true);
					if (UtilValidate.isNotEmpty(listSFCThisMonth)) {
						GenericValue salesForecastThisMonth = EntityUtil.getFirst(listSFCThisMonth);
						String salesForecastIdThisMonth = salesForecastThisMonth.getString("salesForecastId");

						List<GenericValue> listSFCDetailThisMonth = delegator
								.findList("SalesForecastDetail",
										EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId",
												salesForecastIdThisMonth, "productId", productId)),
										null, null, null, true);
						if (UtilValidate.isNotEmpty(listSFCDetailThisMonth)) {
							GenericValue SFCDetailThisMonth = EntityUtil.getFirst(listSFCDetailThisMonth);
							quantity = SFCDetailThisMonth.getBigDecimal("quantity");
						}
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
			mapReturn.put(customTimePeriodId + "" + productId, quantity);
		}
		return mapReturn;
	}

	public static Map<String, Object> getBreakDailyTime(Delegator delegator, Date fromDate, Date thruDate,
			String userLoginId) {
		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		Map<String, Object> mapReturn = FastMap.newInstance();
		Map<String, Object> mapThisM = FastMap.newInstance();
		Map<String, Object> mapNextM = FastMap.newInstance();

		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_MONTH");
		EntityCondition cond2 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
		EntityCondition cond3 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				fromDate);
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition cond4 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		listAllConditions.add(cond4);
		try {
			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, null, null, true);
			if (UtilValidate.isNotEmpty(listCustomTimePeriod)) {
				GenericValue customTimePeriod = EntityUtil.getFirst(listCustomTimePeriod);
				Date thruDateM = customTimePeriod.getDate("thruDate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(thruDateM);
				int totalMonthThis = calendar.get(Calendar.DATE);
				Long diff = thruDateM.getTime() - fromDate.getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
				mapThisM.put("customTimePeriodId", customTimePeriod.getString("customTimePeriodId"));
				mapThisM.put("totalMonth", totalMonthThis);
				if (diffDays >= 7) {
					mapThisM.put("day", new Long(7));
				} else {
					mapThisM.put("day", diffDays);
					List<EntityCondition> listAllConditionsThru = FastList.newInstance();
					EntityCondition condThru1 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,
							"SALES_MONTH");
					EntityCondition condThru2 = EntityCondition.makeCondition("fromDate",
							EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
					EntityCondition condThru3 = EntityCondition.makeCondition("thruDate",
							EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
					EntityCondition condThru4 = EntityCondition.makeCondition("organizationPartyId",
							EntityOperator.EQUALS, partyIdTo);
					listAllConditionsThru.add(condThru1);
					listAllConditionsThru.add(condThru2);
					listAllConditionsThru.add(condThru3);
					listAllConditionsThru.add(condThru4);
					List<GenericValue> listCustomTimePeriodThru = delegator.findList("CustomTimePeriod",
							EntityCondition.makeCondition(listAllConditionsThru), null, null, null, true);
					if (UtilValidate.isNotEmpty(listCustomTimePeriodThru)) {
						GenericValue customTimePeriodThru = EntityUtil.getFirst(listCustomTimePeriodThru);
						Date thruDateNext = customTimePeriodThru.getDate("thruDate");
						calendar.setTime(thruDateNext);
						int totalMonthNext = calendar.get(Calendar.DATE);
						mapNextM.put("customTimePeriodId", customTimePeriodThru.getString("customTimePeriodId"));
						mapNextM.put("day", 7 - diffDays);
						mapNextM.put("totalMonth", totalMonthNext);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		mapReturn.put("thisMonth", mapThisM);
		mapReturn.put("nextMonth", mapNextM);
		return mapReturn;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> storeSaleForecastByPO(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		LocalDispatcher dispatcher = dpx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = (String) context.get("organizationPartyId");
		String internalPartyId = (String) context.get("internalPartyId");
		String currencyUomId = (String) context.get("currencyUomId");
		String salesForecastId = (String) context.get("salesForecastId");
		String sfcItems = (String) context.get("sfcItems");
		Locale locale = (Locale) context.get("locale");
		JSONArray arrOrderItems = JSONArray.fromObject(sfcItems);

		int size = arrOrderItems.size();
		for (int i = 0; i < size; i++) {
			JSONObject item = arrOrderItems.getJSONObject(i);
			Set<String> key = item.keySet();
			String productId = (String) item.get("productId");
			for (String k : key) {
				if (!k.equals("productId") && !k.equals("productName") && !k.equals("uid")) {
					String salesForecastIdWeek = null;
					int quantityInt = 0;
					try {
						quantityInt = (Integer) ObjectType.simpleTypeConvert(item.get(k), "Integer", null, locale);
					} catch (GeneralException e1) {
						e1.printStackTrace();
					}
					BigDecimal quantity = new BigDecimal(quantityInt);
					try {
						List<GenericValue> listSfc = delegator.findList("SalesForecast",
								EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", k,
										"parentSalesForecastId", salesForecastId, "organizationPartyId",
										organizationPartyId, "internalPartyId", internalPartyId)),
								null, null, null, false);
						if (UtilValidate.isEmpty(listSfc)) {
							Map<String, Object> inputSalesForecast = UtilMisc.<String, Object> toMap(
									"parentSalesForecastId", salesForecastId, "customTimePeriodId", k,
									"organizationPartyId", organizationPartyId, "currencyUomId", currencyUomId,
									"userLogin", userLogin, "internalPartyId", internalPartyId);
							try {
								Map<String, Object> resultService = dispatcher.runSync("createSalesForecast",
										inputSalesForecast);
								salesForecastIdWeek = (String) resultService.get("salesForecastId");

								Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object> toMap(
										"userLogin", userLogin, "salesForecastId", salesForecastIdWeek, "productId",
										productId, "quantity", quantity);

								dispatcher.runSync("createSalesForecastDetail", inputSalesForecastDetail);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
						} else {
							// update SFC
							GenericValue sfc = EntityUtil.getFirst(listSfc);
							salesForecastIdWeek = sfc.getString("salesForecastId");
							List<GenericValue> listSfcDetail = delegator.findList(
									"SalesForecastDetail", EntityCondition.makeCondition(UtilMisc
											.toMap("salesForecastId", salesForecastIdWeek, "productId", productId)),
									null, null, null, false);
							if (!UtilValidate.isEmpty(listSfcDetail)) {
								GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
								String salesForecastDetailId = sfcDetail.getString("salesForecastDetailId");

								Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object> toMap(
										"userLogin", userLogin, "salesForecastId", salesForecastIdWeek,
										"salesForecastDetailId", salesForecastDetailId, "productId", productId,
										"quantity", quantity);
								try {
									dispatcher.runSync("updateSalesForecastDetail", inputSalesForecastDetail);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError(e.getMessage());
								}
							} else {
								Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object> toMap(
										"userLogin", userLogin, "salesForecastId", salesForecastIdWeek, "productId",
										productId, "quantity", quantity);
								try {
									dispatcher.runSync("createSalesForecastDetail", inputSalesForecastDetail);
								} catch (GenericServiceException e) {
									return ServiceUtil.returnError(e.getMessage());
								}
							}
						}
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError(e.getMessage());
					}
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JqxGetPlanOfYear(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String productPlanId = (String) context.get("productPlanId");
		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		String checkedWeek = (String) context.get("checkedWeek");
		String productPlanTypeId = "PO_PLAN";
		GenericValue productPlanHeader = null;
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId),
					true);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		Map<String, Object> mapReturn = getListWeekForPlan(delegator, fromMonth, toMonth, checkedWeek,
				organizationPartyId);
		List<Map<String, Object>> listTime = (List<Map<String, Object>>) mapReturn.get("headerWeek");
		List<Map<String, Object>> listReturnResult = FastList.newInstance();

		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, null));
			conds.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
			List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(conds), null, null, null, false);

			for (GenericValue product : listProduct) {
				Map<String, Object> map = FastMap.newInstance();
				String productId = product.getString("productId");
				String productName = product.getString("internalName");
				map.put("productId", productId);
				map.put("productName", productName);
				map.put("productCode", product.getString("productCode"));
				for (Map<String, Object> week : listTime) {
					List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader",
							EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId,
									"productPlanTypeId", productPlanTypeId, "customTimePeriodId",
									(String) week.get("customTimePeriodId"), "organizationPartyId",
									productPlanHeader.getString("organizationPartyId"))),
							null, null, null, true);
					BigDecimal quantity = new BigDecimal(0);
					if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
						GenericValue productPlanHeaderNew = EntityUtil.getFirst(listProductPlanHeader);
						GenericValue listProductPlanItem = delegator.findOne("ProductPlanItem",
								UtilMisc.toMap("productPlanId", productPlanHeaderNew.getString("productPlanId"),
										"productId", productId, "customTimePeriodId",
										(String) week.get("customTimePeriodId")),
								true);
						if (listProductPlanItem != null) {
							quantity = listProductPlanItem.getBigDecimal("planQuantity");
						}
					}
					map.put((String) week.get("customTimePeriodId"), quantity);
				}
				listReturnResult.add(map);
			}

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("listWeekHeader", listTime);
		result.put("listIterator", listReturnResult);
		return result;
	}
}
