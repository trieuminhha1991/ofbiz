package com.olbius.basepos.formular;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FormularEvents {
	public static int scale = 2;
	public static String resourceError = "WebPosSettingErrorUiLabels";

	public static String getPeriodLength(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		try {
			Map<String, Long> getPeriodType = getPeriodType(delegator);
			Long shortPeriod = getPeriodType.get("shortPeriodLength");
			Long longPeriod = getPeriodType.get("longPeriodLength");
			request.setAttribute("shortPeriod", shortPeriod);
			request.setAttribute("longPeriod", longPeriod);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotGetPeriodInfo", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		return "success";
	}

	public static String updateTimePeriod(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String longPeriodTmp = request.getParameter("longPeriod");
		String shortPeriodTmp = request.getParameter("shortPeriod");
		try {
			FormularServices.updateProductSummaryService(delegator, "PROCESSING");
			boolean beganTransaction = TransactionUtil.begin(7200);
			if (UtilValidate.isNotEmpty(longPeriodTmp)) {
				Long longPeriod = Long.valueOf(longPeriodTmp);
				if (UtilValidate.isNotEmpty(longPeriod)) {
					updateTimePeriod(delegator, "LongPeriod", longPeriod);
				}
			}
			if (UtilValidate.isNotEmpty(shortPeriodTmp)) {
				Long shortPeriod = Long.valueOf(shortPeriodTmp);
				if (UtilValidate.isNotEmpty(shortPeriod)) {
					updateTimePeriod(delegator, "ShortPeriod", shortPeriod);
				}
			}
			GenericValue productSummaryService = null;
			productSummaryService = delegator.findOne("ProductSummaryServices",
					UtilMisc.toMap("service", "updateProductSummary"), false);
			String formular = null;
			if (UtilValidate.isNotEmpty(productSummaryService)) {
				formular = productSummaryService.getString("formular");
			}
			if (UtilValidate.isNotEmpty(formular)) {
				Map<String, Object> updateProductSummaryInfoMap = FastMap.newInstance();
				updateProductSummaryInfoMap.put("userLogin", userLogin);
				updateProductSummaryInfoMap.put("formular", formular);
				Map<String, Object> updateProductSummaryInfo = dispatcher.runSync("updateProductSummaryInfo",
						updateProductSummaryInfoMap);
				if (!ServiceUtil.isSuccess(updateProductSummaryInfo)) {
					String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
			TransactionUtil.commit(beganTransaction);
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} catch (GenericServiceException e) {
			e.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} finally {
			// update Product summary service
			try {
				FormularServices.updateProductSummaryService(delegator, "COMPLETED");
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
				String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
						locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		}
		try {
			GenericValue productSummaryService = delegator.findOne("ProductSummaryServices",
					UtilMisc.toMap("service", "updateProductSummary"), false);
			if (UtilValidate.isNotEmpty(productSummaryService)) {
				request.setAttribute("lastUpdate", productSummaryService.getTimestamp("lastUpdated"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	public static void updateTimePeriod(Delegator delegator, String periodName, Long periodLength)
			throws GenericEntityException {
		GenericValue periodEntity = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", periodName), false);
		if (UtilValidate.isNotEmpty(periodEntity)) {
			periodEntity.put("periodLength", periodLength);
			periodEntity.store();
		}
	}

	public static String updateProductSummary(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		String formular = request.getParameter("formular");
		List<GenericValue> productList = FastList.newInstance();
		try {
			productList = delegator.findList("ProductAndProductCategoryMember", null, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotGetProductInfo", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		List<GenericValue> facilityList = FastList.newInstance();
		// EntityCondition facilityCond =
		// EntityCondition.makeCondition("facilityTypeId",
		// EntityOperator.EQUALS, "RETAIL_STORE");
		EntityCondition facilityCond = null;
		try {
			facilityList = delegator.findList("Facility", facilityCond, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotGetFacilityInfo", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		if (UtilValidate.isNotEmpty(facilityList) && UtilValidate.isNotEmpty(productList)) {
			try {
				// update Product summary service
				FormularServices.updateProductSummaryService(delegator, "PROCESSING");
				boolean beganTransaction = TransactionUtil.begin(7200);
				Map<String, Long> getPeriodType = FastMap.newInstance();
				getPeriodType = getPeriodType(delegator);
				Long shortPeriodTmp = getPeriodType.get("shortPeriodLength");
				Long longPeriodTmp = getPeriodType.get("longPeriodLength");
				BigDecimal shortPeriod = new BigDecimal(shortPeriodTmp);
				BigDecimal longPeriod = new BigDecimal(longPeriodTmp);

				for (GenericValue product : productList) {
					String productId = product.getString("productId");
					String productName = product.getString("productName");
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
						updatePFSummaryMap.put("formular", formular);
						Map<String, Object> udpatePFSummary = FastMap.newInstance();
						udpatePFSummary = dispatcher.runSync("updateProductFacilityInfo", updatePFSummaryMap);
						if (!ServiceUtil.isSuccess(udpatePFSummary)) {
							String errorMessage = UtilProperties.getMessage(resourceError,
									"SettingCanNotUpdateProductInfo", UtilMisc.toMap("productId", productId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
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
					/* BigDecimal totalQuantity = qoh.add(qoo); */
					qpdL = qtyL.divide(longPeriod, scale, RoundingMode.HALF_UP);
					qpdS = qtyS.divide(shortPeriod, scale, RoundingMode.HALF_UP);
					lidL = FormularServices.getLastInDay(qoh, qoo, qpdL);
					lidS = FormularServices.getLastInDay(qoh, qoo, qpdS);
					if (UtilValidate.isNotEmpty(formular)) {
						try {
							status = calculateFormular(formular, lastSold, qoh, qoo, lidL, lidS, qpdL, qpdS, qtyL, qtyS,
									longPeriod, shortPeriod);
						} catch (ScriptException e) {
							e.printStackTrace();
							String errorMessage = UtilProperties.getMessage(resourceError,
									"SettingCanNotUpdateProductInfo", UtilMisc.toMap("productId", productId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					}

					GenericValue productSummary = null;
					productSummary = delegator.findOne("ProductSummary", UtilMisc.toMap("productId", productId), false);
					if (UtilValidate.isNotEmpty(productSummary)) {
						Map<String, Object> updateProductSummaryMap = FastMap.newInstance();
						updateProductSummaryMap.put("userLogin", userLogin);
						updateProductSummaryMap.put("productId", productId);
						updateProductSummaryMap.put("productName", productName);
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
							String errorMessage = UtilProperties.getMessage(resourceError,
									"SettingCanNotUpdateProductInfo", UtilMisc.toMap("productId", productId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					} else {
						Map<String, Object> createProductSummaryMap = FastMap.newInstance();
						createProductSummaryMap.put("userLogin", userLogin);
						createProductSummaryMap.put("productId", productId);
						createProductSummaryMap.put("productName", productName);
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
							String errorMessage = UtilProperties.getMessage(resourceError,
									"SettingCanNotUpdateProductInfo", UtilMisc.toMap("productId", productId), locale);
							request.setAttribute("_ERROR_MESSAGE_", errorMessage);
							return "error";
						}
					}
				}
				TransactionUtil.commit(beganTransaction);
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
				String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
						locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			} catch (GenericServiceException e) {
				e.printStackTrace();
				// String errorMessage =
				// UtilProperties.getMessage(resourceError,
				// "SettingCanNotUpdateProductInfo", UtilMisc.toMap("productId",
				// productId), locale);
				String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
						locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			} finally {
				// update Product summary service
				try {
					FormularServices.updateProductSummaryService(delegator, "COMPLETED");
				} catch (GenericEntityException e2) {
					e2.printStackTrace();
					String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
							locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			}
			try {
				GenericValue productSummaryService = delegator.findOne("ProductSummaryServices",
						UtilMisc.toMap("service", "updateProductSummary"), false);
				if (UtilValidate.isNotEmpty(productSummaryService)) {
					productSummaryService.put("formular", formular);
					productSummaryService.store();
					request.setAttribute("lastUpdate", productSummaryService.getTimestamp("lastUpdated"));
					request.setAttribute("formular", formular);
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return "success";
	}

	public static Map<String, Long> getPeriodType(Delegator delegator) throws GenericEntityException {
		Map<String, Long> result = FastMap.newInstance();
		Long shortPeriodLength = null;
		Long longPeriodLength = null;
		GenericValue shortPeriodEntity = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "ShortPeriod"),
				false);
		if (UtilValidate.isNotEmpty(shortPeriodEntity)) {
			shortPeriodLength = shortPeriodEntity.getLong("periodLength");
		}
		GenericValue longPeriodEntity = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", "LongPeriod"),
				false);
		if (UtilValidate.isNotEmpty(longPeriodEntity)) {
			longPeriodLength = longPeriodEntity.getLong("periodLength");
		}
		result.put("shortPeriodLength", shortPeriodLength);
		result.put("longPeriodLength", longPeriodLength);
		return result;
	}

	public static String getProductSummaryServiceInfo(HttpServletRequest request, HttpServletResponse response) {
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue productSummaryServices = null;
		String status = null;
		Timestamp lastUpdate = null;
		String formular = null;
		try {
			productSummaryServices = delegator.findOne("ProductSummaryServices",
					UtilMisc.toMap("service", FormularServices.nameService), false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		if (UtilValidate.isNotEmpty(productSummaryServices)) {
			status = productSummaryServices.getString("status");
			lastUpdate = productSummaryServices.getTimestamp("lastUpdated");
			formular = productSummaryServices.getString("formular");
		} else {
			productSummaryServices = delegator.makeValue("ProductSummaryServices");
			productSummaryServices.put("service", "updateProductSummary");
			productSummaryServices.put("status", "COMPLETED");
			try {
				productSummaryServices.create();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				String errorMessage = UtilProperties.getMessage(resourceError, "SettingCanNotSetupThisTimeFomular",
						locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
		}
		request.setAttribute("status", status);
		request.setAttribute("lastUpdate", lastUpdate);
		request.setAttribute("formular", formular);
		return "success";
	}

	public static String calculateFormular(String formular, Timestamp lastSold, BigDecimal qoh, BigDecimal qoo,
			BigDecimal lidL, BigDecimal lidS, BigDecimal qpdL, BigDecimal qpdS, BigDecimal longPeriod,
			BigDecimal shortPeriod) throws ScriptException {
		int countLastSold = 0;
		if (UtilValidate.isNotEmpty(lastSold)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			countLastSold = UtilDateTime.getIntervalInDays(lastSold, nowTimestamp);
		}
		String status = null;
		formular = formular.replace("IF", "");
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByName("javascript");
		engine.put("LASTSOLD", countLastSold);
		engine.put("QOH", qoh.intValue());
		engine.put("QOO", qoo.intValue());
		engine.put("LIDL", lidL.intValue());
		engine.put("LIDS", lidS.intValue());
		engine.put("QPDL", qpdL.floatValue());
		engine.put("QPDS", qpdS.floatValue());
		engine.put("LONGPERIOD", longPeriod.intValue());
		engine.put("SHORTPERIOD", shortPeriod.intValue());
		status = (String) engine.eval(formular);
		return status;
	}

	public static String calculateFormular(String formular, Timestamp lastSold, BigDecimal qoh, BigDecimal qoo,
			BigDecimal lidL, BigDecimal lidS, BigDecimal qpdL, BigDecimal qpdS, BigDecimal qtyL, BigDecimal qtyS,
			BigDecimal longPeriod, BigDecimal shortPeriod) throws ScriptException {
		int countLastSold = 0;
		if (UtilValidate.isNotEmpty(lastSold)) {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			countLastSold = UtilDateTime.getIntervalInDays(lastSold, nowTimestamp);
		}
		String status = null;
		formular = formular.replace("IF", "");
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine engine = sem.getEngineByName("javascript");
		engine.put("LASTSOLD", countLastSold);
		engine.put("QOH", qoh.intValue());
		engine.put("QOO", qoo.intValue());
		engine.put("QTYL", qtyL.intValue());
		engine.put("QTYS", qtyS.intValue());
		engine.put("LIDL", lidL.intValue());
		engine.put("LIDS", lidS.intValue());
		engine.put("QPDL", qpdL.floatValue());
		engine.put("QPDS", qpdS.floatValue());
		engine.put("LONGPERIOD", longPeriod.intValue());
		engine.put("SHORTPERIOD", shortPeriod.intValue());
		status = (String) engine.eval(formular);
		return status;
	}

	public static BigDecimal getProductAverageCost(Delegator delegator, String productId, String facilityId)
			throws GenericEntityException {
		BigDecimal unitCost = BigDecimal.ZERO;
		List<EntityCondition> productAverageCostCondList = FastList.newInstance();
		productAverageCostCondList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		productAverageCostCondList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		productAverageCostCondList.add(
				EntityCondition.makeCondition("productAverageCostTypeId", EntityOperator.EQUALS, "SIMPLE_AVG_COST"));
		productAverageCostCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null));
		EntityCondition productAverageCostCond = EntityCondition.makeCondition(productAverageCostCondList,
				EntityOperator.AND);
		List<String> orderBy = FastList.newInstance();
		orderBy.add("fromDate DESC"); // sort descending
		List<GenericValue> productAverageCostList = delegator.findList("ProductAverageCost", productAverageCostCond,
				null, orderBy, null, false);
		if (UtilValidate.isNotEmpty(productAverageCostList)) {
			GenericValue productAverageCost = productAverageCostList.get(0);
			if (UtilValidate.isNotEmpty(productAverageCost)) {
				unitCost = productAverageCost.getBigDecimal("averageCost");
			}
		}
		return unitCost;
	}
}
