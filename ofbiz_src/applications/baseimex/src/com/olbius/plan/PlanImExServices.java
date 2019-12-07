package com.olbius.plan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.services.JqxWidgetSevices;

public class PlanImExServices {
	public static final String module = PlanImExServices.class.getName();
	public static final String resource = "BaseImExUiLabels";
	public static final String IMEX_PROPERTIES = "imex.properties";
	
	public static Map<String, Object> createProductPlan(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String supplierPartyId = (String) context.get("supplierPartyId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = (String) userLogin.get("userLoginId");
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanTypeId = (String) context.get("productPlanTypeId");
		context.put("organizationPartyId", company);
		context.put("createByUserLoginId", userLoginId);
		String currencyUomId = null;
		if (UtilValidate.isNotEmpty(context.get("currencyUomId"))) {
			currencyUomId = (String) context.get("currencyUomId");
		}
		
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId);
		EntityCondition cond2 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, company);
		EntityCondition cond3 = EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("IMPORT_PLAN_CANCELLED", "IMPORT_PLAN_COMPLETED"));
		EntityCondition cond4 = EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, productPlanTypeId);
		EntityCondition cond5 = EntityCondition.makeCondition("supplierPartyId", EntityOperator.EQUALS, supplierPartyId);
		conds.add(cond1);
		conds.add(cond2);
		conds.add(cond3);
		conds.add(cond4);
		conds.add(cond5);
		if (UtilValidate.isNotEmpty(currencyUomId)) {
			EntityCondition cond6 = EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId);
			conds.add(cond6);
		}
		
		Locale locale = (Locale) context.get("locale");
		try {
			List<GenericValue> listProductPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(conds), null, null, null, false);
			if (UtilValidate.isNotEmpty(listProductPlan)) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEPeriodHasBeenCreated", locale));
			}

		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		GenericValue productPlanHeader = delegator.makeValue("ProductPlanHeader");
		String productPlanId = delegator.getNextSeqId("ProductPlanHeader");
		productPlanHeader.set("productPlanId", productPlanId);
		if (UtilValidate.isEmpty(context.get("productPlanCode"))) {
			context.put("productPlanCode", productPlanId);
		}
		productPlanHeader.setNonPKFields(context);
		try {
			delegator.createOrStore(productPlanHeader);
			String prodId = productPlanHeader.getString("productPlanId");
			result.put("productPlanId", prodId);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListProductPlan(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition cond1 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_YEAR");
		EntityCondition cond2 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,
				organizationPartyId);
		EntityCondition cond3 = EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, "IMPORT_PLAN");
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);

		try {
			EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			listIterator = delegator.find("ProductPlanAndCustomTime", cond, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPlan service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	public static Map<String, Object> JqxGetPlanOfYear(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String productPlanId = (String) context.get("productPlanId");
		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		GenericValue productPlanHeader = null;
		Date currentDate = new Date();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String supplierPartyId = null;
		try {
			productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), true);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		if (UtilValidate.isNotEmpty(productPlanHeader)) {
			String customTimePeriodId = productPlanHeader.getString("customTimePeriodId");
			supplierPartyId = productPlanHeader.getString("supplierPartyId");
			GenericValue customTimePeriod;
			try {
				customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), true);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			if (UtilValidate.isNotEmpty(customTimePeriod)) {
				currentDate = customTimePeriod.getDate("fromDate");
			}
		}
		String periodTypeId = "COMMERCIAL_MONTH";
		if (UtilValidate.isNotEmpty(supplierPartyId)) {
			List<GenericValue> listPartyPeriodType = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", supplierPartyId));
			conds.add(EntityUtil.getFilterByDateExpr());
			try {
				listPartyPeriodType = delegator.findList("PartyPeriodType", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyPeriodType: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listPartyPeriodType.isEmpty()){
				periodTypeId = listPartyPeriodType.get(0).getString("periodTypeId");
			}
		}
		List<Map<String, Object>> listTime = FastList.newInstance();
		if ("COMMERCIAL_WEEK".equals(periodTypeId)){
			if (fromMonth != null && toMonth != null) {
				listTime = (List<Map<String, Object>>) getListWeek(delegator, fromMonth, toMonth, currentDate, userLoginId);
			} else {
				listTime = (List<Map<String, Object>>) getListWeek(delegator, null, null, currentDate, userLoginId);
			}
		} else if ("COMMERCIAL_MONTH".equals(periodTypeId)){
			if (fromMonth != null && toMonth != null) {
				listTime = (List<Map<String, Object>>) getListMonth(delegator, fromMonth, toMonth, currentDate, userLoginId);
			} else {
				listTime = (List<Map<String, Object>>) getListMonth(delegator, null, null, currentDate, userLoginId);
			}
		} else if ("COMMERCIAL_QUARTER".equals(periodTypeId)){
			// quarter
		} else if ("COMMERCIAL_YEAR".equals(periodTypeId)){
			// year
			
		}

		List<Map<String, Object>> listReturnResult = FastList.newInstance();

		try {
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierPartyId));
			conds.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, productPlanHeader.getString("currencyUomId")));
			List<GenericValue> listProduct = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(conds), null, null, null, false);

			conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("parentProductPlanId", EntityOperator.EQUALS, productPlanId));
			conds.add(EntityCondition.makeCondition("productPlanTypeId", EntityOperator.EQUALS, "IMPORT_PLAN"));
			conds.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, productPlanHeader.getString("organizationPartyId")));
			conds.add(EntityCondition.makeCondition("supplierPartyId", EntityOperator.EQUALS, supplierPartyId));
			
			for(GenericValue product : listProduct){
				Map<String, Object> map = FastMap.newInstance();
				String productId = product.getString("productId");
				String productName = product.getString("productName");
				BigDecimal minimumOrderQuantity = product.getBigDecimal("minimumOrderQuantity");
				map.put("productId", productId);
				map.put("productName", productName);
				//map.put("productName", productName);
				map.put("productCode", product.getString("productCode"));
				map.put("MOQ", minimumOrderQuantity);
				for(Map<String, Object> period : listTime){
					List<EntityCondition> cond2s = FastList.newInstance();
					cond2s.addAll(conds);
					cond2s.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, (String)period.get("customTimePeriodId")));
					List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(cond2s), null, null, null, false);
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal quantityRemain = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(listProductPlanHeader)){
							GenericValue productPlanHeaderNew = EntityUtil.getFirst(listProductPlanHeader);
							GenericValue planItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanHeaderNew.getString("productPlanId"), "productId", productId, "customTimePeriodId", (String)period.get("customTimePeriodId")), false);
							if (UtilValidate.isNotEmpty(planItem)){
								quantity = planItem.getBigDecimal("planQuantity");
								quantityRemain = quantity;
								if (UtilValidate.isNotEmpty(planItem.get("orderedQuantity")) && UtilValidate.isNotEmpty(quantity)) {
									quantityRemain = quantity.subtract(planItem.getBigDecimal("orderedQuantity"));
								}
							}
					}
					map.put((String)period.get("customTimePeriodId"), quantityRemain);
					map.put((String)period.get("customTimePeriodId")+"_plan", quantity);
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

	public static List<Map<String, Object>> getListWeek(Delegator delegator, String fromMonth, String toMonth,
			Date currentDate, String userLoginId) {
		List<GenericValue> listTime = FastList.newInstance();
		if (fromMonth != null && toMonth != null) {
			listTime = getLastAndNextMonth(delegator, fromMonth, toMonth, currentDate, userLoginId);
		} else {
			listTime = getLastAndNextMonth(delegator, currentDate, userLoginId);
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		listTime = EntityUtil.filterByAnd(listTime, UtilMisc.toMap("periodTypeId", "COMMERCIAL_WEEK"));
		for (GenericValue time : listTime) {
			Map<String, Object> map = FastMap.newInstance();
			map.put("customTimePeriodId", time.getString("customTimePeriodId"));
			map.put("periodName", time.getString("periodName"));
			listReturn.add(map);
		}
		return listReturn;
	}

	public static List<GenericValue> getLastAndNextMonth(Delegator delegator, Date currentDate, String userLoginId) {
		Date curDate = new Date();
		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		// lay thang hien tai
//		int month = calendar.get(Calendar.MONTH);
		// set thang cua 1 nam bat ky = thang hien tai
		calendar.setTime(currentDate);
		calendar.set(Calendar.MONTH, 1);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		java.sql.Date lastMonth = new java.sql.Date(calendar.getTimeInMillis());
		calendar.add(Calendar.MONTH, 11);
		java.sql.Date nextMonth = new java.sql.Date(calendar.getTimeInMillis());

		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nextMonth);
		EntityCondition cond2 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				lastMonth);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,
				organizationPartyId);
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		try {
			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return listCustomTimePeriod;
	}

	public static List<GenericValue> getLastAndNextMonth(Delegator delegator, String fromMonth, String toMonth,
			Date currentDate, String userLoginId) {
		int fromM = Integer.parseInt(fromMonth);
		int toM = Integer.parseInt(toMonth);
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.MONTH, fromM - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		java.sql.Date lastMonth = new java.sql.Date(calendar.getTimeInMillis());
		calendar.set(Calendar.MONTH, toM);
		java.sql.Date nextMonth = new java.sql.Date(calendar.getTimeInMillis());

		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nextMonth);
		EntityCondition cond2 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				lastMonth);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,
				organizationPartyId);
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		try {
			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return listCustomTimePeriod;
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

		GenericValue productPlanHeader = null;
		try {
			productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		if (UtilValidate.isEmpty(productPlanHeader)) {
			return ServiceUtil.returnError("PlanHeader Not found!");
		}
		String customTimePeriodIdSFC = productPlanHeader.getString("customTimePeriodId");
		String supplierPartyId = productPlanHeader.getString("supplierPartyId");
		String periodTypeId = "COMMERCIAL_MONTH";
		try {
			String tmp = PeriodServices.getCurrentPartyPeriodType(delegator, supplierPartyId);
			if (tmp != null) periodTypeId  = tmp;
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.toString());
		}
		GenericValue customTimePeriodIdSFCGe = null;
		try {	
			customTimePeriodIdSFCGe = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodIdSFC), true);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		Date fromDateY = customTimePeriodIdSFCGe.getDate("fromDate");
		Date thruDateY = customTimePeriodIdSFCGe.getDate("thruDate");
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (fromMonth != null && toMonth != null && checkedWeek != null) {
			mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, fromMonth, toMonth, checkedWeek, userLoginId, periodTypeId);
		} else {
			mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, userLoginId, periodTypeId);
		}

		List<Map<String, Object>> listMapPeriods = (List<Map<String, Object>>) mapReturn.get("listMapPeriods");
		List<GenericValue> listPeriods = (List<GenericValue>) mapReturn.get("listPeriods");
		List<String> listAllPeriods = (List<String>) mapReturn.get("listAllPeriods");

		Map<String, List<Map<String, Object>>> actualSalesMap = new HashMap<String, List<Map<String, Object>>>();
			for (GenericValue period : listPeriods) {
				Date fromDateD = period.getDate("fromDate");
				Date thruDateD = period.getDate("thruDate");
				Timestamp fromDate = new Timestamp(fromDateD.getTime());
				Timestamp thruDate = new Timestamp(thruDateD.getTime());
				String periodId = (String) period.getString("customTimePeriodId");
				
				// lay salesActual
				LocalDispatcher dispatcher = dpx.getDispatcher();
				try {
					Map<String, Object> mapData = dispatcher.runSync("calculateActualExportInventory",
							UtilMisc.toMap("userLogin", userLogin, "fromDate", new Timestamp(fromDate.getTime()),
									"thruDate", new Timestamp(thruDate.getTime())));
					List<Map<String, Object>> listData = (List<Map<String, Object>>) mapData.get("data");
					actualSalesMap.put(periodId, listData);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}

			List<Map<String, Object>> listReturnResult = FastList.newInstance();
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
			conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, null));
			conds.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
			List<GenericValue> listProduct = FastList.newInstance();
			try {
				listProduct = delegator.findList("Product", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			for (GenericValue product : listProduct) {
				String productId = product.getString("productId");
				String productCode = product.getString("productCode");
				String productName = product.getString("productName");
				BigDecimal tonDauTruoc = new BigDecimal(0);
				BigDecimal salesTruoc = new BigDecimal(0);
				BigDecimal planTruoc = new BigDecimal(0);
				Locale locale = (Locale) context.get("locale");
				BigDecimal moq = getMoq(delegator, productId, supplierPartyId);
						
				List<BigDecimal> listSfcWeek = getListSfcByWeek(delegator, listAllPeriods, productId, userLoginId);
				Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIEOpenInventory", locale), "OpenInventory");
				Map<String, Object> mapSales = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIESalesForcast", locale), "SalesForcast");
				Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIEOrderQuantity", locale), "OrderQuantity");
				Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIEEndInventory", locale), "EndInventory");
				Map<String, Object> dayCover = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIEDayCoverDuration", locale), "DayCoverDuration");
				Map<String, Object> accuracyMap = assignToMap(productId, productCode, productName, moq, UtilProperties.getMessage(resource, "BIEForcastAccuracy", locale), "ForcastAccuracy");
				
				for (GenericValue period : listPeriods) {
					String periodId = period.getString("customTimePeriodId");
					// tinh Opening
					BigDecimal cuoiTruoc = tonDauTruoc.subtract(salesTruoc).add(planTruoc);
					int offSet = listAllPeriods.indexOf(periodId);
					
					BigDecimal remainHead = new BigDecimal(0);
					Date fromDateW = period.getDate("fromDate");
					Date thruDateW = period.getDate("thruDate");
					Timestamp fromDate = new Timestamp(fromDateW.getTime());
					Timestamp thruDate = new Timestamp(thruDateW.getTime());
					
					remainHead = listInvByDate(delegator, productId, fromDate, organizationPartyId);
					BigDecimal tonDau = remainHead;
					BigDecimal tonCuoi = BigDecimal.ZERO;
					
					BigDecimal sales = new BigDecimal(0);
					BigDecimal plan = new BigDecimal(0);
					BigDecimal accuracy = new BigDecimal(0);
					// lay SFC cua chu ky nay
					BigDecimal saleFc1 = new BigDecimal(0);
					BigDecimal saleFc2 = new BigDecimal(0);
					BigDecimal sfcThisWeek = listSfcWeek.get(offSet);
					
					if (offSet < listAllPeriods.size() - 2) {
						// lay sales FC 2 chu ky tiep theo
						String next1Week = listAllPeriods.get(offSet + 1);
						if (next1Week != null) {
							saleFc1 = listSfcWeek.get(offSet + 1);
						}
						String next2Week = listAllPeriods.get(offSet + 2);
						if (next2Week != null) {
							saleFc2 = listSfcWeek.get(offSet + 2);
						}
					}
					
					// lay so thuc te thuc hien cua plan
					List<GenericValue> listProductPlanHeaderW = FastList.newInstance();
					try {
						listProductPlanHeaderW = delegator.findList("ProductPlanHeader",
							EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "IMPORT_PLAN",
							"customTimePeriodId", periodId, "organizationPartyId", productPlanHeader.getString("organizationPartyId"))),
							null, null, null, true);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError(e.getMessage());
					}
					GenericValue productPlanW = null;
					GenericValue productPlanItem = null;
					if (UtilValidate.isNotEmpty(listProductPlanHeaderW)) {
						productPlanW = EntityUtil.getFirst(listProductPlanHeaderW);
						try {
							productPlanItem = delegator.findOne("ProductPlanItem",
											UtilMisc.toMap("productPlanId", productPlanW.getString("productPlanId"), "customTimePeriodId", periodId, "productId", productId), true);
						} catch (GenericEntityException e) {
							return ServiceUtil.returnError(e.getMessage());
						}
					}
					// so sanh dau chu ky voi ngay hien tai
					if (fromDateW.compareTo(new Date()) <= 0) {
						tonDau = remainHead;
						if (productPlanItem != null) {
							if (productPlanItem.get("orderedQuantity") != null) {
								plan = (BigDecimal) productPlanItem.get("orderedQuantity");
							}
						}
					} else {
						if (cuoiTruoc.compareTo(BigDecimal.ZERO) > 0) {
							tonDau = cuoiTruoc;
						}
						// lay so sales = SFC cua tuan
						sales = sfcThisWeek;
						// lay so plan theo cong thuc
						BigDecimal equalSalesAndStock = tonDau.subtract(sfcThisWeek).subtract(saleFc1)
								.subtract(saleFc2);
						if (equalSalesAndStock.compareTo(new BigDecimal(1)) < 0) {
							BigDecimal moqProduct = new BigDecimal(1); // TODO lay MOQ trong supplier_product
							
							BigDecimal plan1 = sfcThisWeek.add(saleFc1).add(saleFc2).subtract(tonDau);
							BigDecimal plan2 = plan1.divide(moqProduct, 0, RoundingMode.UP);
							if (productPlanItem != null) {
								if (productPlanItem.get("planQuantity") != null) {
									plan = (BigDecimal) productPlanItem.get("planQuantity");
								}
							} else {
								plan = moqProduct.multiply(plan2);
							}
						}
					}

					// so sanh cuoi chu ky voi ngay hientai de tinh ending
					if (thruDateW.compareTo(new Date()) <= 0) {
						// tinh ton kho thuc te
						tonCuoi = listInvByDate(delegator, productId, thruDate, organizationPartyId);
						// lay so sale = actual sales, hien chua co actual sales
						// => =0
						List<Map<String, Object>> listActualOneWeek = actualSalesMap.get(periodId);
						for (Map<String, Object> map : listActualOneWeek) {
							if (map.get("productId").equals(productId)) {
								sales = (BigDecimal) map.get("quantity");
								break;
							}
						}
					} else {
						sales = sfcThisWeek;
						BigDecimal equalSalesAndStock = tonDau.subtract(sfcThisWeek).subtract(saleFc1)
								.subtract(saleFc2);
						if (equalSalesAndStock.compareTo(new BigDecimal(1)) < 0) {
							BigDecimal moqProduct = new BigDecimal(1);
							BigDecimal plan1 = sfcThisWeek.add(saleFc1).add(saleFc2).subtract(tonDau);
							BigDecimal plan2 = plan1.divide(moqProduct, 0, RoundingMode.UP);
							if (productPlanItem != null) {
								if (productPlanItem.get("planQuantity") != null) {
									plan = (BigDecimal) productPlanItem.get("planQuantity");
								}
							} else {
								plan = moqProduct.multiply(plan2);
							}
						}
						tonCuoi = tonDau.subtract(sales).add(plan);
					}
					// gan lai cac so cua tuan truoc
					tonDauTruoc = tonDau;
					salesTruoc = sales;
					planTruoc = plan;
					if (sfcThisWeek.compareTo(new BigDecimal(0)) > 0) {
						accuracy = sales.divide(sfcThisWeek, 0, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
					}
					String customId = (String) period.get("customTimePeriodId");
					mapOpen.put(customId, tonDau);
					mapSales.put(customId, sales);
					accuracyMap.put(customId, accuracy);
					mapOrder.put(customId, plan);
					mapEnding.put(customId, tonCuoi);
					dayCover.put(customId, new BigDecimal(0));
				}
				listReturnResult.add(mapOpen);
				listReturnResult.add(mapSales);
				listReturnResult.add(accuracyMap);
				listReturnResult.add(mapOrder);
				listReturnResult.add(mapEnding);
				listReturnResult.add(dayCover);
			}

			result.put("listWeekHeader", listMapPeriods);
			result.put("listIterator", listReturnResult);
			result.put("periodTypeId", periodTypeId);
		
		return result;
	}

	public static List<String> getListCustomTimeSFCYear(Delegator delegator, Date fromDate, Date thruDate,
			String userLoginId, String periodTypeId) {
		List<String> listCustom = FastList.newInstance();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
		EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
		EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId);
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		listAllConditions.add(cond4);
		List<String> orderBy = FastList.newInstance();
		orderBy.add("fromDate");

		try {
			List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, orderBy, null, false);
			listCustom = EntityUtil.getFieldListFromEntityList(listCustomTimePeriod, "customTimePeriodId", true);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			return listCustom;
		}
		return listCustom;
	}

	public static Map<String, Object> getListWeekByYearSFCCustom(Delegator delegator, Date fromDate, Date thruDate,
			String fromMonth, String toMonth, String checkedWeek, String userLoginId, String periodTypeId) {
		int fromM = Integer.parseInt(fromMonth);
		int thruM = Integer.parseInt(toMonth);
		Boolean checkedW = Boolean.parseBoolean(checkedWeek);

		Date current = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(current);
		int thisMonth = calendar.get(Calendar.MONTH);
		int thisDay = calendar.get(Calendar.DATE);
		calendar.setTime(fromDate);
		calendar.set(Calendar.MONTH, thisMonth);
		calendar.set(Calendar.DATE, thisDay);
		calendar.add(Calendar.MONTH, -fromM);
		java.sql.Date fromDatSql = new java.sql.Date(calendar.getTimeInMillis());
		calendar.add(Calendar.MONTH, fromM);
		calendar.add(Calendar.MONTH, thruM);
		java.sql.Date thruDatSql = new java.sql.Date(calendar.getTimeInMillis());
		calendar.add(Calendar.MONTH, -thruM);
		if (checkedW) {
			calendar.setTime(fromDate);
			calendar.set(Calendar.MONTH, thisMonth);
			calendar.set(Calendar.DATE, thisDay);
			calendar.add(Calendar.DATE, -(7 * (fromM)));
			fromDatSql = new java.sql.Date(calendar.getTimeInMillis());
			calendar.add(Calendar.DATE, (7 * fromM));
			calendar.add(Calendar.DATE, (7 * (thruM)));
			thruDatSql = new java.sql.Date(calendar.getTimeInMillis());
		}

		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		Map<String, Object> mapReturn = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,
				thruDatSql);
		EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				fromDatSql);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
		EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId);
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

		calendar.setTime(fromDate);
		calendar.add(Calendar.YEAR, -1);
		Date fromDateLast = new java.sql.Date(calendar.getTimeInMillis());
		calendar.setTime(thruDate);
		calendar.add(Calendar.MONTH, thruM);
		Date thruDateLast = new java.sql.Date(calendar.getTimeInMillis());
		List<String> listCustomTime = getListCustomTimeSFCYear(delegator, fromDateLast, thruDateLast, userLoginId, periodTypeId);
		mapReturn.put("listMapPeriods", listReturn);
		mapReturn.put("listPeriods", listCustomTimePeriod);
		mapReturn.put("listAllPeriods", listCustomTime);
		return mapReturn;
	}

	public static Map<String, Object> getListWeekByYearSFCCustom(Delegator delegator, Date fromDate, Date thruDate,
			String userLoginId, String periodTypeId) {
		Date current = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(current);
		if ("COMMERCIAL_MONTH".equals(periodTypeId)){
			calendar.setTime(fromDate);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DATE, 1);
		} else {
			int thisMonth = calendar.get(Calendar.MONTH);
			int thisDay = calendar.get(Calendar.DATE);
			calendar.setTime(fromDate);
			calendar.set(Calendar.MONTH, thisMonth);
			calendar.set(Calendar.DATE, thisDay);
			calendar.add(Calendar.MONTH, -1);
		}
		
		java.sql.Date fromDatSql = new java.sql.Date(calendar.getTimeInMillis());
		if ("COMMERCIAL_MONTH".equals(periodTypeId)){
			calendar.add(Calendar.MONTH, 11);
		} else {
			calendar.add(Calendar.MONTH, 6);
		}
		java.sql.Date thruDatSql = new java.sql.Date(calendar.getTimeInMillis());

		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		Map<String, Object> mapReturn = FastMap.newInstance();
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,
				thruDatSql);
		EntityCondition cond2 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
				fromDatSql);
		EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
		EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId);
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

		calendar.setTime(fromDate);
		Date fromDateLast = new java.sql.Date(calendar.getTimeInMillis());
		calendar.setTime(thruDate);
		Date thruDateLast = new java.sql.Date(calendar.getTimeInMillis());
		List<String> listCustomTime = getListCustomTimeSFCYear(delegator, fromDateLast, thruDateLast, userLoginId, periodTypeId);

		mapReturn.put("listMapPeriods", listReturn);
		mapReturn.put("listPeriods", listCustomTimePeriod);
		mapReturn.put("listAllPeriods", listCustomTime);
		return mapReturn;
	}

	public static BigDecimal getMoqByProduct(List<GenericValue> listConfigMoq, String productId) {
		BigDecimal moq = new BigDecimal(1);
		if (UtilValidate.isNotEmpty(listConfigMoq)) {
			for (GenericValue moqGe : listConfigMoq) {
				if (moqGe.getString("productId").equals(productId)) {
					moq = moqGe.getBigDecimal("quantity");
					break;
				}
			}
		}
		return moq;
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

	public static GenericValue getListWeekByDate(Delegator delegator, Date date, String userLoginId) {
		List<GenericValue> listCustomTimePeriod = FastList.newInstance();
		GenericValue returnGe = null;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date);
		EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date);
		EntityCondition cond3 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_WEEK");
		EntityCondition cond4 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,
				organizationPartyId);
		listAllConditions.add(cond1);
		listAllConditions.add(cond2);
		listAllConditions.add(cond3);
		listAllConditions.add(cond4);
		try {

			listCustomTimePeriod = delegator.findList("CustomTimePeriod",
					EntityCondition.makeCondition(listAllConditions), null, null, null, true);
			if (UtilValidate.isNotEmpty(listCustomTimePeriod)) {
				returnGe = EntityUtil.getFirst(listCustomTimePeriod);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return returnGe;
	}

	public static BigDecimal getSfcByWeek(Delegator delegator, String customTimePeriodIdW, String productId,
			String userLoginId) {
		BigDecimal sfcThisWeek = new BigDecimal(0);
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<GenericValue> sfcWeekPO = FastList.newInstance();
		try {
			sfcWeekPO = delegator.findList("SalesForecast",
					EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodIdW,
							"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)),
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		if (UtilValidate.isNotEmpty(sfcWeekPO)) {
			GenericValue sfcWeek = EntityUtil.getFirst(sfcWeekPO);
			String sfcIdWeek = sfcWeek.getString("salesForecastId");
			List<GenericValue> listSfcDetail = FastList.newInstance();
			try {
				listSfcDetail = delegator.findList("SalesForecastDetail",
						EntityCondition
								.makeCondition(UtilMisc.toMap("salesForecastId", sfcIdWeek, "productId", productId)),
						null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			}
			if (UtilValidate.isNotEmpty(listSfcDetail)) {
				GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
				sfcThisWeek = sfcDetail.getBigDecimal("quantity");
			}
		}
		return sfcThisWeek;
	}

	public static List<BigDecimal> getListSfcByWeek(Delegator delegator, List<String> listCustomTime, String productId,
			String userLoginId) {
		List<BigDecimal> listSfcByPeriods = FastList.newInstance();
		for (String customTimePeriodId : listCustomTime) {
			BigDecimal sfcQuantity = new BigDecimal(0);
			String organizationPartyId = null;
			GenericValue objCustomTimePeriod = null;
			try {
				objCustomTimePeriod = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne CustomTimePeriod: " + e.toString();
				Debug.logError(e, errMsg, module);
				return listSfcByPeriods;
			}
			if (UtilValidate.isEmpty(objCustomTimePeriod)) return listSfcByPeriods;
			
			String periodTypeId = objCustomTimePeriod.getString("periodTypeId");
			Date fromDate = objCustomTimePeriod.getDate("fromDate");
			Date thruDate = objCustomTimePeriod.getDate("thruDate");
			organizationPartyId = objCustomTimePeriod.getString("organizationPartyId");
			
			String salesPeriodTypeId = null;
			switch (periodTypeId) {
				case "COMMERCIAL_WEEK":
					salesPeriodTypeId = "SALES_WEEK";
					break;
				case "COMMERCIAL_MONTH":
					salesPeriodTypeId = "SALES_MONTH";
					break;
				case "COMMERCIAL_QUARTER":
					salesPeriodTypeId = "SALES_QUARTER";
					break;
				case "COMMERCIAL_YEAR":
					salesPeriodTypeId = "SALES_YEAR";
					break;
				default:
					break;
				}
			List<EntityCondition> cond2s = FastList.newInstance();
			cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			cond2s.add(EntityCondition.makeCondition("periodTypeId", salesPeriodTypeId));
			cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
			cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
			List<GenericValue> sfcPeriods = FastList.newInstance();
			try {
				sfcPeriods = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
				Debug.logError(e, errMsg, module);
				return listSfcByPeriods;
			}
			
			if (!sfcPeriods.isEmpty()){
				GenericValue sfcPeriod = EntityUtil.getFirst(sfcPeriods);
				String periodId = sfcPeriod.getString("customTimePeriodId");
				
				List<GenericValue> sfcs = FastList.newInstance();
				try {
					sfcs = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", periodId,
									"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
				if (!sfcs.isEmpty()){
					GenericValue sfc = EntityUtil.getFirst(sfcs);
					String sfcId = sfc.getString("salesForecastId");
					List<GenericValue> listSfcDetail = FastList.newInstance();
					try {
						listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
					} catch (GenericEntityException e) {
						Debug.log(e.getMessage());
						return listSfcByPeriods;
					}
					if (UtilValidate.isNotEmpty(listSfcDetail)) {
						GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
						sfcQuantity = sfcDetail.getBigDecimal("quantity");
					}
				}
				listSfcByPeriods.add(sfcQuantity);
				
			} else {
				List<GenericValue> listCustomTimeSales = FastList.newInstance();
				switch (periodTypeId) {
				case "COMMERCIAL_WEEK":
					cond2s = FastList.newInstance();
					cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
					cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_MONTH"));
					cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
					cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
					try {
						listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
						Debug.logError(e, errMsg, module);
						return listSfcByPeriods;
					}
					if (!listCustomTimeSales.isEmpty()){
						// tim theo month chia trung binh cho 4 week
						List<GenericValue> forcasts = FastList.newInstance();
						try {
							forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
											"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
						} catch (GenericEntityException e) {
							Debug.log(e.getMessage());
						}
						if (!forcasts.isEmpty()) {
							GenericValue sfc = EntityUtil.getFirst(forcasts);
							String sfcId = sfc.getString("salesForecastId");
							List<GenericValue> listSfcDetail = FastList.newInstance();
							try {
								listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.log(e.getMessage());
								return listSfcByPeriods;
							}
							if (UtilValidate.isNotEmpty(listSfcDetail)) {
								GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
								sfcQuantity = sfcDetail.getBigDecimal("quantity");
								if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
									sfcQuantity = sfcQuantity.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
								}
							}
						}
						listSfcByPeriods.add(sfcQuantity);
					} else {
						// tim theo quy chia trung binh cho 12 tuan
						cond2s = FastList.newInstance();
						cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
						cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_QUARTER"));
						cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
						cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
						try {
							listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
							Debug.logError(e, errMsg, module);
							return listSfcByPeriods;
						}
						if (!listCustomTimeSales.isEmpty()){
							List<GenericValue> forcasts = FastList.newInstance();
							try {
								forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
												"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.log(e.getMessage());
							}
							if (!forcasts.isEmpty()) {
								GenericValue sfc = EntityUtil.getFirst(forcasts);
								String sfcId = sfc.getString("salesForecastId");
								List<GenericValue> listSfcDetail = FastList.newInstance();
								try {
									listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
								} catch (GenericEntityException e) {
									Debug.log(e.getMessage());
									return listSfcByPeriods;
								}
								if (UtilValidate.isNotEmpty(listSfcDetail)) {
									GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
									sfcQuantity = sfcDetail.getBigDecimal("quantity");
									if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
										sfcQuantity = sfcQuantity.divide(new BigDecimal(12), 0, RoundingMode.HALF_UP);
									}
								}
							}
							listSfcByPeriods.add(sfcQuantity);
						} else {
							// tim theo thang chia trung binh cho 48 tuan
							cond2s = FastList.newInstance();
							cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
							cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_YEAR"));
							cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
							cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
							try {
								listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
								Debug.logError(e, errMsg, module);
								return listSfcByPeriods;
							}
							if (!listCustomTimeSales.isEmpty()){
								List<GenericValue> forcasts = FastList.newInstance();
								try {
									forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
													"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
								} catch (GenericEntityException e) {
									Debug.log(e.getMessage());
								}
								if (!forcasts.isEmpty()) {
									GenericValue sfc = EntityUtil.getFirst(forcasts);
									String sfcId = sfc.getString("salesForecastId");
									List<GenericValue> listSfcDetail = FastList.newInstance();
									try {
										listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
									} catch (GenericEntityException e) {
										Debug.log(e.getMessage());
										return listSfcByPeriods;
									}
									if (UtilValidate.isNotEmpty(listSfcDetail)) {
										GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
										sfcQuantity = sfcDetail.getBigDecimal("quantity");
										if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
											sfcQuantity = sfcQuantity.divide(new BigDecimal(48), 0, RoundingMode.HALF_UP);
										}
									}
								}
								listSfcByPeriods.add(sfcQuantity);
							}
						}
					} 
					break;
				case "COMMERCIAL_MONTH":
					// tim theo quarter chia trung binh cho 3 month
					cond2s = FastList.newInstance();
					cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
					cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_QUARTER"));
					cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
					cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
					try {
						listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
						Debug.logError(e, errMsg, module);
						return listSfcByPeriods;
					}
					if (!listCustomTimeSales.isEmpty()){
						List<GenericValue> forcasts = FastList.newInstance();
						try {
							forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
											"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
						} catch (GenericEntityException e) {
							Debug.log(e.getMessage());
						}
						if (!forcasts.isEmpty()) {
							GenericValue sfc = EntityUtil.getFirst(forcasts);
							String sfcId = sfc.getString("salesForecastId");
							List<GenericValue> listSfcDetail = FastList.newInstance();
							try {
								listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.log(e.getMessage());
								return listSfcByPeriods;
							}
							if (UtilValidate.isNotEmpty(listSfcDetail)) {
								GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
								sfcQuantity = sfcDetail.getBigDecimal("quantity");
								if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
									sfcQuantity = sfcQuantity.divide(new BigDecimal(3), 0, RoundingMode.HALF_UP);
								}
							}
						}
						listSfcByPeriods.add(sfcQuantity);
					} else {
						// tim theo year chia trung binh cho 12 month
						cond2s = FastList.newInstance();
						cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
						cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_YEAR"));
						cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
						cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
						try {
							listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
							Debug.logError(e, errMsg, module);
							return listSfcByPeriods;
						}
						if (!listCustomTimeSales.isEmpty()){
							List<GenericValue> forcasts = FastList.newInstance();
							try {
								forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
												"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.log(e.getMessage());
							}
							if (!forcasts.isEmpty()) {
								GenericValue sfc = EntityUtil.getFirst(forcasts);
								String sfcId = sfc.getString("salesForecastId");
								List<GenericValue> listSfcDetail = FastList.newInstance();
								try {
									listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
								} catch (GenericEntityException e) {
									Debug.log(e.getMessage());
									return listSfcByPeriods;
								}
								if (UtilValidate.isNotEmpty(listSfcDetail)) {
									GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
									sfcQuantity = sfcDetail.getBigDecimal("quantity");
									if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
										sfcQuantity = sfcQuantity.divide(new BigDecimal(12), 0, RoundingMode.HALF_UP);
									}
								}
							}
							listSfcByPeriods.add(sfcQuantity);
						}
					}
					break;
				case "COMMERCIAL_QUARTER":
					// tim theo year chia trung binh cho 4 quarter
					cond2s = FastList.newInstance();
					cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
					cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_YEAR"));
					cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
					cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
					try {
						listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
						Debug.logError(e, errMsg, module);
						return listSfcByPeriods;
					}
					if (!listCustomTimeSales.isEmpty()){
						List<GenericValue> forcasts = FastList.newInstance();
						try {
							forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
											"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
						} catch (GenericEntityException e) {
							Debug.log(e.getMessage());
						}
						if (!forcasts.isEmpty()) {
							GenericValue sfc = EntityUtil.getFirst(forcasts);
							String sfcId = sfc.getString("salesForecastId");
							List<GenericValue> listSfcDetail = FastList.newInstance();
							try {
								listSfcDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", sfcId, "productId", productId)), null, null, null, false);
							} catch (GenericEntityException e) {
								Debug.log(e.getMessage());
								return listSfcByPeriods;
							}
							if (UtilValidate.isNotEmpty(listSfcDetail)) {
								GenericValue sfcDetail = EntityUtil.getFirst(listSfcDetail);
								sfcQuantity = sfcDetail.getBigDecimal("quantity");
								if (sfcQuantity.compareTo(BigDecimal.ZERO) >= 1){
									sfcQuantity = sfcQuantity.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
								}
							}
						}
						listSfcByPeriods.add(sfcQuantity);
					}
					break;
				default:
					break;
				}
			}
		}

		return listSfcByPeriods;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> JqxGetSalesForecastByWeek(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String salesForecastId = (String) context.get("salesForecastId");
		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		String periodTypeId = (String) context.get("periodTypeId");
		String checkedWeek = (String) context.get("checkedWeek");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		GenericValue salesForecast = null;
		try {
			salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId),
					true);
			String customTimePeriodIdSFC = salesForecast.getString("customTimePeriodId");
			GenericValue customTimePeriodIdSFCGe = delegator.findOne("CustomTimePeriod",
					UtilMisc.toMap("customTimePeriodId", customTimePeriodIdSFC), true);
			Date fromDateY = customTimePeriodIdSFCGe.getDate("fromDate");
			Date thruDateY = customTimePeriodIdSFCGe.getDate("thruDate");
			Map<String, Object> mapReturn = FastMap.newInstance();
			if (fromMonth != null && toMonth != null && checkedWeek != null) {
				mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, fromMonth, toMonth, checkedWeek,
						userLoginId, periodTypeId);
			} else {
				mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, userLoginId, periodTypeId);
			}

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
				String productName = product.getString("productName");
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
							// chua co salesFC cua tuan nao... => tao moi SFC va
							// SFC detail
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
	public static Map<String, Object> storePlanAndStock(DispatchContext dpx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dpx.getDispatcher();
		Map<String, Object> result = FastMap.newInstance();
		String productPlanId = (String) context.get("productPlanId");

		String dataPlans = (String) context.get("dataPlans");
		JSONArray arrPlans = JSONArray.fromObject(dataPlans);
		String dataEnds = (String) context.get("dataEnds");
		JSONArray arrEnds = JSONArray.fromObject(dataEnds);
		GenericValue userLogin = (GenericValue) context.get("userLogin");

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
						dispatcher.runSync("updatePlanItemWeek", map);
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
		BigDecimal planQuantity = new BigDecimal(planQuantityStr);
		String statusId = (String) context.get("statusId");
		BigDecimal inventoryForecast = new BigDecimal(0);
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		if (context.get("inventoryForecast") != null) {
			String invForecastStr = (String) context.get("inventoryForecast");
			inventoryForecast = new BigDecimal(invForecastStr);
		}

		try {
			GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader",
					UtilMisc.toMap("productPlanId", productPlanId), false);
			List<EntityCondition> conds = FastList.newInstance();
			
			conds.add(EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "IMPORT_PLAN",
									"customTimePeriodId", customTimePeriodId, "organizationPartyId",
									productPlanHeader.getString("organizationPartyId"))));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("IMPORT_PLAN_CREATED", "IMPORT_PLAN_APPROVED")));
			conds.add(EntityCondition.makeCondition("parentProductPlanId", EntityOperator.EQUALS, productPlanId));
			List<GenericValue> listProductPlanHeader = delegator
					.findList("ProductPlanHeader",
							EntityCondition.makeCondition(conds),
							null, null, null, true);

			if (UtilValidate.isEmpty(listProductPlanHeader)) {
				GenericValue productPlanHeaderNew = delegator.makeValue("ProductPlanHeader");
				String productPlanIdNew = delegator.getNextSeqId("ProductPlanHeader");
				productPlanHeaderNew.put("productPlanId", productPlanIdNew);
				productPlanHeaderNew.put("parentProductPlanId", productPlanId);
				productPlanHeaderNew.put("productPlanTypeId", "IMPORT_PLAN");
				productPlanHeaderNew.put("customTimePeriodId", customTimePeriodId);
				productPlanHeaderNew.put("createByUserLoginId", userLogin.getString("userLoginId"));
				productPlanHeaderNew.put("organizationPartyId", productPlanHeader.getString("organizationPartyId"));
				productPlanHeaderNew.put("supplierPartyId", productPlanHeader.getString("supplierPartyId"));
				productPlanHeaderNew.put("statusId", "IMPORT_PLAN_CREATED");
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

	public static Map<String, Object> loadProductPlanItemByCustomTimePeriodId(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		String productPlanId = (String) context.get("productPlanId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String supplierPartyId = (String) context.get("supplierPartyId");
		String uomCurency = (String) context.get("uomCurency");
		List<GenericValue> listProductPlanItem = FastList.newInstance();
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		List<GenericValue> listProductPlanHeader = FastList.newInstance();
		try {
			listProductPlanHeader = delegator.findList("ProductPlanHeader",
					EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId,
							"customTimePeriodId", customTimePeriodId, "organizationPartyId", company)),
					null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
			GenericValue productPlanHeader = EntityUtil.getFirst(listProductPlanHeader);
			String productPlanIdChild = productPlanHeader.getString("productPlanId");
			try {
				listProductPlanItem = delegator.findList("ProductPlanItem",
						EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanIdChild)), null, null,
						null, false);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}

		List<Map<String, Object>> listProPlan = FastList.newInstance();
		for (GenericValue productPlanItem : listProductPlanItem) {
			String productId = productPlanItem.getString("productId");
			BigDecimal quantity = productPlanItem.getBigDecimal("planQuantity");
			BigDecimal orderedQuantity = BigDecimal.ZERO;
			if (productPlanItem.get("orderedQuantity") != null) {
				orderedQuantity = productPlanItem.getBigDecimal("orderedQuantity");
			}
			if (quantity.compareTo(BigDecimal.ZERO) > 0) {
				quantity = quantity.subtract(orderedQuantity);
				try {
					BigDecimal lastPrice = new BigDecimal(0);
					List<GenericValue> listSupplierProduct = delegator
							.findList(
									"SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId",
											productId, "partyId", supplierPartyId, "currencyUomId", uomCurency)),
									null, null, null, false);
					List<EntityCondition> cond = FastList.newInstance();
					cond.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
					listSupplierProduct = EntityUtil.filterByAnd(listSupplierProduct, cond);
					BigDecimal minimumOrderQuantity = BigDecimal.ZERO;
					if (UtilValidate.isNotEmpty(listSupplierProduct)) {
						GenericValue supplierProduct = EntityUtil.getFirst(listSupplierProduct);
						lastPrice = supplierProduct.getBigDecimal("lastPrice");
						minimumOrderQuantity = supplierProduct.getBigDecimal("minimumOrderQuantity");
					}

					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					String productCode = product.getString("productCode");
					String productPlanIdChild2 = productPlanItem.getString("productPlanId");
					String customTimePeriodIdChild = productPlanItem.getString("customTimePeriodId");
					String productName = product.getString("productName");
					String unit = product.getString("quantityUomId");
					Map<String, Object> map = FastMap.newInstance();
					map.put("productId", productId);
					map.put("productName", productName);
					map.put("productCode", productCode);
					map.put("quantityUomId", product.getString("quantityUomId"));
					map.put("weightUomId", product.getString("weightUomId"));
					map.put("requireAmount", product.getString("requireAmount"));
					map.put("amountUomTypeId", product.getString("amountUomTypeId"));
					map.put("productPlanId", productPlanIdChild2);
					map.put("orderedQuantity", orderedQuantity);
					map.put("customTimePeriodId", customTimePeriodIdChild);
					map.put("quantity", new BigDecimal(0));
					map.put("planQuantity", quantity);
					map.put("lastPrice", lastPrice);
					map.put("unit", unit);
					map.put("minimumOrderQuantity", minimumOrderQuantity);

					listProPlan.add(map);

				} catch (GenericEntityException e) {
					return ServiceUtil.returnError(e.getMessage());
				}
			}
		}

		result.put("listProductPlanItem", listProPlan);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyPeriodTypes(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listOrders = FastList.newInstance();
		try {
			listSortFields.add("fromDate DESC");
			listOrders = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartyPeriodTypeDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPartyPeriodTypes service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listOrders);
		return successResult;
	}
	
	public static Map<String,Object> createPartyPeriod(DispatchContext ctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String periodTypeId = (String)context.get("periodTypeId");
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		EntityCondition cond2 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId);
		conds.add(cond1);
		conds.add(cond2);
		conds.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listPartyPeriod = FastList.newInstance();
		Locale locale = (Locale) context.get("locale");
		try {
			listPartyPeriod = delegator.findList("PartyPeriodType", EntityCondition.makeCondition(conds), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList PartyPeriodType: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listPartyPeriod.isEmpty()) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BIEPeriodHasBeenConfiged", locale));
		}
			
		String fromDate = (String)context.get("fromDate");
		String thruDate = null;
		Timestamp thruDateStp = null;
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			thruDate = (String)context.get("thruDate");
			thruDateStp = new Timestamp(new Long(thruDate));
		}
		Timestamp fromDateStp = null;
		if (UtilValidate.isNotEmpty(fromDate)) {
			fromDateStp = new Timestamp(new Long(fromDate));
		}
		if (UtilValidate.isNotEmpty(fromDateStp)) {
			GenericValue map = delegator.makeValue("PartyPeriodType");
			map.put("partyId", partyId);
			map.put("periodTypeId", periodTypeId);
			map.put("fromDate", fromDateStp);
			map.put("thruDate", thruDateStp);
			try {
				delegator.createOrStore(map);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: createPartyPeriod error! " + e.toString());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String,Object> updatePartyPeriod(DispatchContext ctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String periodTypeId = (String)context.get("periodTypeId");
		String fromDate = (String)context.get("fromDate");
		String thruDate = null;
		Timestamp thruDateStp = null;
		if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
			thruDate = (String)context.get("thruDate");
			thruDateStp = new Timestamp(new Long(thruDate));
		}
		Timestamp fromDateStp = null;
		if (UtilValidate.isNotEmpty(fromDate)) {
			fromDateStp = new Timestamp(new Long(fromDate));
		}
		
		Delegator delegator = ctx.getDelegator();
		GenericValue obj = null;
		List<EntityCondition> conds = FastList.newInstance();
		EntityCondition cond1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
		EntityCondition cond2 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId);
		conds.add(cond1);
		conds.add(cond2);
		if (UtilValidate.isNotEmpty(fromDateStp)) {
			EntityCondition cond3 = EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDateStp);
			conds.add(cond3);
			try {
				obj = delegator.findOne("PartyPeriodType", false, conds);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne PartyPeriodType: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} else {
			List<GenericValue> listPartyPeriod = FastList.newInstance();
			conds.add(EntityUtil.getFilterByDateExpr());
			try {
				listPartyPeriod = delegator.findList("PartyPeriodType", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyPeriodType: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listPartyPeriod.isEmpty()) obj = listPartyPeriod.get(0);
		}
		if (UtilValidate.isNotEmpty(obj)) {
			obj.put("thruDate", thruDateStp);
			try {
				delegator.store(obj);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: updatePartyPeriod error! " + e.toString());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
	
//	 public static Map<String, Object> JqxGetStockAndPlanByMonth(DispatchContext dpx, Map<String, Object> context){
//			Delegator delegator = dpx.getDelegator();
//			Map<String, Object> result = FastMap.newInstance();
//			String productPlanId = (String)context.get("productPlanId");
//			LocalDispatcher dispatcher = dpx.getDispatcher();
//			String fromMonth = (String)context.get("fromMonth");
//			String toMonth = (String)context.get("toMonth");
//			String checkedWeek = (String)context.get("checkedWeek");
//			GenericValue userLogin = (GenericValue)context.get("userLogin");
//			String userLoginId = userLogin.getString("userLoginId");
//			
//			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
//			
//			GenericValue productPlanHeader = null;
//			try {
//				productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
//				String customTimePeriodIdSFC = productPlanHeader.getString("customTimePeriodId");
//				GenericValue customTimePeriodIdSFCGe = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodIdSFC), true);
//				Date fromDateY = customTimePeriodIdSFCGe.getDate("fromDate");
//				Date thruDateY = customTimePeriodIdSFCGe.getDate("thruDate");
//				Map<String, Object> mapReturn = FastMap.newInstance();
//				if(fromMonth != null && toMonth != null && checkedWeek != null){
//					mapReturn = getListMonthByYearSFCCustom(delegator, fromDateY, thruDateY, fromMonth, toMonth, checkedWeek, userLoginId);
//				}else{
//					mapReturn = getListMonthByYearSFCCustom(delegator, fromDateY, thruDateY, userLoginId);
//				}
//				
//				List<Map<String, Object>> listHeader = (List<Map<String, Object>>)mapReturn.get("headerWeek");
//				List<GenericValue> listTimeW = (List<GenericValue>)mapReturn.get("listTimeW");
//				List<String> listCustomTime = (List<String>)mapReturn.get("listCustomTime");
//				
//				Map<String, Object> mapInv = FastMap.newInstance();
//				Map<String, Object> mapInvEnd = FastMap.newInstance();
//				Map<String, Object> mapSfc = FastMap.newInstance();
//				
//				List<List<List<Object>>> listActualSales = FastList.newInstance();
//				//lap week lay ton kho theo tuan
//				for(GenericValue week : listTimeW){
//					Date fromDateW = week.getDate("fromDate");
//					Date thruDateW = week.getDate("thruDate");
//					String customTimePeriodIdW = week.getString("customTimePeriodId");
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(fromDateW);
//					calendar.add(Calendar.DATE, -1);
//					java.sql.Date prevDate =  new java.sql.Date(calendar.getTimeInMillis());
//					String prevDateStr = String.valueOf(prevDate.getTime());
//					String facilityId = "";
//					List<Map<String, Object>> listReportByWeek = listInvByDate(prevDateStr, prevDateStr, facilityId);
//					
//					calendar.setTime(thruDateW);
////					calendar.add(Calendar.DATE, -1);
//					Date prevThruDate = new java.sql.Date(calendar.getTimeInMillis());
//					String prevThruDateStr = String.valueOf(prevThruDate.getTime());
//					List<Map<String, Object>> listReportByEndWeek = listInvByDate(prevThruDateStr, prevThruDateStr, facilityId);
//					
//					mapInv.put(customTimePeriodIdW, listReportByWeek);
//					mapInvEnd.put(customTimePeriodIdW, listReportByEndWeek);
//					
//					List<GenericValue> listSfcMonth = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDateW, "periodTypeId", "SALES_MONTH")), null, null, null, false);
//					String customTimeSfcId = "";
//					if(UtilValidate.isNotEmpty(listSfcMonth)){
//						GenericValue sfcMonth = EntityUtil.getFirst(listSfcMonth);
//						customTimeSfcId = sfcMonth.getString("customTimePeriodId");
//					}
//					mapSfc.put(customTimePeriodIdW, customTimeSfcId);
//				}
//				
//				for(String customtime : listCustomTime){
//					GenericValue custom = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customtime), true);
//					List<List<Object>> listData = FastList.newInstance();
//					if(custom != null){
//						Date fromDateW = custom.getDate("fromDate");
//						Date thruDateW = custom.getDate("thruDate");
//						try {
//							Map<String, Object> mapData = dispatcher.runSync("evaluateSalesGridProductByRegion", UtilMisc.toMap("userLogin", userLogin, "fromDate", new Timestamp(fromDateW.getTime()), "thruDate",new Timestamp(thruDateW.getTime()), "region", organizationPartyId, "orderStatus", "ORDER_COMPLETED"));
//							listData = (List<List<Object>>) mapData.get("data");
//						} catch (GenericServiceException e) {
//							return ServiceUtil.returnError(e.getMessage());
//						}
//					}
//					listActualSales.add(listData);
//				}
//				
//				List<Map<String, Object>> listReturnResult = FastList.newInstance();
//				List<String> orderBy = FastList.newInstance();
//				orderBy.add("productCode");
//				List<EntityCondition> conds = FastList.newInstance();
//				conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
//				conds.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.NOT_EQUAL, null));
//				conds.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "N"));
//				List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(conds), null, null, null, false);
//				
//				for(GenericValue product : listProduct){
//					
//					String productId = product.getString("productId");
//					String productCode = product.getString("productCode");
//					String productName = product.getString("productName");
//					BigDecimal tonDauTruoc = new BigDecimal(0);
//					BigDecimal salesTruoc = new BigDecimal(0);
//					BigDecimal planTruoc = new BigDecimal(0);
//					
//					//get MOQ product
//					List<GenericValue> listConfigMoq = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", "PP_PALLET", "uomToId", product.getString("quantityUomId"))), null, null, null, false);
//					listConfigMoq = EntityUtil.filterByDate(listConfigMoq);
//					BigDecimal moqProduct = new BigDecimal(1);
//					if(UtilValidate.isNotEmpty(listConfigMoq)){
//						GenericValue config = EntityUtil.getFirst(listConfigMoq);
//						moqProduct = config.getBigDecimal("quantityConvert");
//					}
//					
////					getSfcByWeek(delegator, listCustomTime, productId, userLoginId);
//					List<BigDecimal> listSfcWeek = getListSfcByWeek(delegator, listCustomTime, productId, userLoginId);
//					
//					Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, moqProduct, "Opening");
//					Map<String, Object> mapSales = assignToMap(productId, productCode, productName, moqProduct, "Channel sales");
//					Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, moqProduct, "Orders(Pallet)");
//					Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, moqProduct, "Ending");
//					Map<String, Object> dayCover = assignToMap(productId, productCode, productName, moqProduct, "Days cover duration");
//					Map<String, Object> accuracyMap = assignToMap(productId, productCode, productName, moqProduct, "Forecast accuracy");
//					
//					for(GenericValue week : listTimeW){
//						//tinh Opening
//						BigDecimal cuoiTruoc = tonDauTruoc.subtract(salesTruoc).add(planTruoc);
//						
//						BigDecimal remainHead = new BigDecimal(0);
//						Date fromDateW = week.getDate("fromDate");
//						Date thruDateW = week.getDate("thruDate");
//						String customTimePeriodIdW = week.getString("customTimePeriodId");
//						String customTimeSfcId = (String)mapSfc.get(customTimePeriodIdW);
//						
//						if(customTimeSfcId.equals("")) return ServiceUtil.returnError("Not have customTimePeriodId of SFC like as customTimePeriod of plan");
//						int offSet = listCustomTime.indexOf(customTimeSfcId);
//						
//						Calendar calendar = Calendar.getInstance();
////						calendar.setTime(fromDateW);
////						calendar.add(Calendar.DATE, -2);
////						java.sql.Date prevDate =  new java.sql.Date(calendar.getTimeInMillis());
//						//TODO:lay ton dau ky cua tuan olap 
//						GenericValue productPlanItemW = null;
//						List<Map<String, Object>> listReport = (List<Map<String, Object>>)mapInv.get(customTimePeriodIdW);
//						if(UtilValidate.isNotEmpty(listReport)) {
//							for(Map<String, Object> report: listReport){
//								String productIdRe = (String)report.get("productId");
//								if(productId.equals(productIdRe)){
//									BigDecimal atp = (BigDecimal)report.get("avaliableToPromiseTotal");
//									remainHead = atp.add(remainHead);
//									break;
//								}
//							}
//						}else{
////							GenericValue lastWeek = getListWeekByDate(delegator, prevDate, userLoginId);
////							String customTimePeriodIdLast = listCustomTime.get(offSet-1);
////							if(customTimePeriodIdLast!=null){
////								List<GenericValue> listProductPlanHeaderW = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "PO_PLAN", "customTimePeriodId", customTimePeriodIdLast, "organizationPartyId", productPlanHeader.getString("organizationPartyId"))), null, null, null, true);
////								if(UtilValidate.isNotEmpty(listProductPlanHeaderW)){
////									GenericValue productPlanW = EntityUtil.getFirst(listProductPlanHeaderW);
////									productPlanItemW = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanW.getString("productPlanId"), "customTimePeriodId", customTimePeriodIdLast, "productId", productId), true);
////									if(productPlanItemW != null){
////										if(productPlanItemW.get("inventoryForecast") != null){
////											remainHead = productPlanItemW.getBigDecimal("inventoryForecast");
////										}
////									}
////								}
////							}
//						}
//						
//						BigDecimal tonDau = new BigDecimal(0);
//						BigDecimal tonCuoi = new BigDecimal(0);
//						BigDecimal sales = new BigDecimal(0);
//						BigDecimal plan = new BigDecimal(0);
//						BigDecimal accuracy = new BigDecimal(0);
//						//lay SFC cua thang nay
//						BigDecimal sfcThisWeek = new BigDecimal(0);
//						if(offSet < listSfcWeek.size() && offSet >= 0){
//							sfcThisWeek = listSfcWeek.get(offSet);
//						}
//						//lay sales FC 1 thang toi
//						String next1Week = null;
//						if((offSet+1)< listCustomTime.size() && (offSet+1)>= 0){
//							next1Week = listCustomTime.get(offSet+1);
//						}
//						BigDecimal saleFc1 = new BigDecimal(0);
////						BigDecimal saleFc2 = new BigDecimal(0);
//						if(next1Week != null){
//							saleFc1 = listSfcWeek.get(offSet+1);
//						}
//						// so sanh dau tuan voi ngay hien tai
//						if(fromDateW.compareTo(new Date()) <=0){
//							tonDau = remainHead;
//							//lay so thuc te thuc hien cua plan
//							List<GenericValue> listProductPlanHeaderW = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "PO_PLAN", "customTimePeriodId", customTimePeriodIdW, "organizationPartyId", productPlanHeader.getString("organizationPartyId"))), null, null, null, true);
//							if(UtilValidate.isNotEmpty(listProductPlanHeaderW)){
//								GenericValue productPlanW = EntityUtil.getFirst(listProductPlanHeaderW);
//								GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanW.getString("productPlanId"), "customTimePeriodId", customTimePeriodIdW, "productId", productId), true);
//								if(productPlanItem != null){
//									if(productPlanItem.get("orderedQuantity") != null){
//										plan = ((BigDecimal)productPlanItem.get("orderedQuantity")).divide(moqProduct, 0,RoundingMode.UP);
//									}
//								}
//							}
//						}else{
////							if(UtilValidate.isNotEmpty(listReport) || productPlanItemW != null){//lay ton du bao
//							if(UtilValidate.isNotEmpty(listReport)){
//								tonDau = remainHead;
//							}else{
//								tonDau = cuoiTruoc;
//							}
//							// lay so sales = SFC cua tuan
//							sales = sfcThisWeek;
//							//lay so plan theo cong thuc
//							
////							BigDecimal tonDuBaoCuoiThang = saleFc1.multiply(new BigDecimal(7)).divide(new BigDecimal(30));
//							BigDecimal aa = saleFc1.multiply(new BigDecimal(7));
//							BigDecimal tonDuBaoCuoiThang = aa.divide(new BigDecimal(30), 0, RoundingMode.UP);
////							BigDecimal equalSalesAndStock = tonDau.subtract(sfcThisWeek).subtract(saleFc1).subtract(saleFc2);
//							BigDecimal importSales = sfcThisWeek.add(tonDuBaoCuoiThang).subtract(tonDau);
//							if(importSales.compareTo(new BigDecimal(1)) > 0){
//								
//								BigDecimal plan2 = importSales.divide(moqProduct, 0,RoundingMode.UP);
////								plan = moqProduct.multiply(plan2);
//								plan = plan2;
//							}
//						}
//						
//						// so sanh cuoi tuan voi ngay hientai de tinh ending
//						if(thruDateW.compareTo(new Date()) <=0){
//							// tinh ton kho thuc te
//							List<Map<String, Object>> listReportThru = (List<Map<String, Object>>)mapInvEnd.get(customTimePeriodIdW);
//							if(UtilValidate.isNotEmpty(listReportThru)) {
//								for(Map<String, Object> report : listReportThru){
//									String productIdRe = (String)report.get("productId");
//									if(productId.equals(productIdRe)){
//										BigDecimal atp = (BigDecimal)report.get("avaliableToPromiseTotal");
//										tonCuoi = atp.add(tonCuoi);
//										break;
//									}
//								}
//							}
//							// lay so sale = actual sales, hien chua co actual sales => =0
//							List<List<Object>> listActualOneWeek = listActualSales.get(offSet);
//							for(Object map : listActualOneWeek){
//								Map<String, Object> tmpMap = (Map<String, Object>)map;
//								if(tmpMap.get("productId").equals(productId)){
//									sales = (BigDecimal) tmpMap.get("Quantity");
//									break;
//								}
//							}
//							
//							
//						}else{
//							sales = sfcThisWeek;
//							
////							BigDecimal tonDuBaoCuoiThang = saleFc1.multiply(new BigDecimal(7)).divide(new BigDecimal(30));
//							BigDecimal aa = saleFc1.multiply(new BigDecimal(7));
//							BigDecimal tonDuBaoCuoiThang = aa.divide(new BigDecimal(30), 0, RoundingMode.UP);
//							BigDecimal importSales = sfcThisWeek.add(tonDuBaoCuoiThang).subtract(tonDau);
//							if(importSales.compareTo(new BigDecimal(1)) > 0){
//								
//								BigDecimal plan2 = importSales.divide(moqProduct, 0,RoundingMode.UP);
////								plan = moqProduct.multiply(plan2);
//								plan = plan2;
//							}
//							
//							tonCuoi = tonDau.subtract(sales).add(moqProduct.multiply(plan));
//						}
//	//gan lai cac so cua tuan truoc
//						tonDauTruoc = tonDau;
//						salesTruoc = sales;
////						planTruoc = plan;
//						planTruoc = moqProduct.multiply(plan);
//						if(sfcThisWeek.compareTo(new  BigDecimal(0)) > 0){
//							accuracy = sales.divide(sfcThisWeek, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
//						}
////						String customId = (String)week.get("customTimePeriodId");
//						mapOpen.put(customTimePeriodIdW, tonDau);
//						mapSales.put(customTimePeriodIdW, sales);
//						accuracyMap.put(customTimePeriodIdW, accuracy);
//						mapOrder.put(customTimePeriodIdW, plan);
//						mapEnding.put(customTimePeriodIdW, tonCuoi);
//						dayCover.put(customTimePeriodIdW, new BigDecimal(0));
//					}
//					listReturnResult.add(mapOpen);
//					listReturnResult.add(mapSales);
//					listReturnResult.add(accuracyMap);
//					listReturnResult.add(mapOrder);
//					listReturnResult.add(mapEnding);
//					listReturnResult.add(dayCover);
//				}
//				
//				result.put("listWeekHeader", listHeader);
//				result.put("listIterator", listReturnResult);
//			} catch (GenericEntityException e1) {
//				return ServiceUtil.returnError(e1.getMessage());
//			}
//			return result;
//		}
	    
//	    public static Map<String, Object> JqxGetStockAndPlanByMonthSaved(DispatchContext dpx, Map<String, Object> context){
//			Delegator delegator = dpx.getDelegator();
//			Map<String, Object> result = FastMap.newInstance();
//			String productPlanId = (String)context.get("productPlanId");
//			LocalDispatcher dispatcher = dpx.getDispatcher();
//			String fromMonth = (String)context.get("fromMonth");
//			String toMonth = (String)context.get("toMonth");
//			String checkedWeek = (String)context.get("checkedWeek");
//			GenericValue userLogin = (GenericValue)context.get("userLogin");
//			String userLoginId = userLogin.getString("userLoginId");
//			
//			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
//			
//			GenericValue productPlanHeader = null;
//			try {
//				productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
//				String customTimePeriodIdSFC = productPlanHeader.getString("customTimePeriodId");
//				GenericValue customTimePeriodIdSFCGe = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodIdSFC), true);
//				Date fromDateY = customTimePeriodIdSFCGe.getDate("fromDate");
//				Date thruDateY = customTimePeriodIdSFCGe.getDate("thruDate");
//				Map<String, Object> mapReturn = FastMap.newInstance();
//				if(fromMonth != null && toMonth != null && checkedWeek != null){
//					mapReturn = getListMonthByYearSFCCustom(delegator, fromDateY, thruDateY, fromMonth, toMonth, checkedWeek, userLoginId);
//				}else{
//					mapReturn = getListMonthByYearSFCCustom(delegator, fromDateY, thruDateY, userLoginId);
//				}
//				
//				List<Map<String, Object>> listHeader = (List<Map<String, Object>>)mapReturn.get("headerWeek");
//				List<GenericValue> listTimeW = (List<GenericValue>)mapReturn.get("listTimeW");
//				List<String> listCustomTime = (List<String>)mapReturn.get("listCustomTime");
//				
//				Map<String, Object> mapInv = FastMap.newInstance();
//				Map<String, Object> mapInvEnd = FastMap.newInstance();
//				Map<String, Object> mapSfc = FastMap.newInstance();
//				
//				List<List<List<Object>>> listActualSales = FastList.newInstance();
//				//lap week lay ton kho theo tuan
//				for(GenericValue week : listTimeW){
//					Date fromDateW = week.getDate("fromDate");
//					Date thruDateW = week.getDate("thruDate");
//					String customTimePeriodIdW = week.getString("customTimePeriodId");
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(fromDateW);
//					calendar.add(Calendar.DATE, -1);
//					java.sql.Date prevDate =  new java.sql.Date(calendar.getTimeInMillis());
//					String prevDateStr = String.valueOf(prevDate.getTime());
//					String facilityId = "";
//					List<Map<String, Object>> listReportByWeek = listInvByDate(prevDateStr, prevDateStr, facilityId);
//					
//					calendar.setTime(thruDateW);
////					calendar.add(Calendar.DATE, -1);
//					Date prevThruDate = new java.sql.Date(calendar.getTimeInMillis());
//					String prevThruDateStr = String.valueOf(prevThruDate.getTime());
//					List<Map<String, Object>> listReportByEndWeek = listInvByDate(prevThruDateStr, prevThruDateStr, facilityId);
//					
//					mapInv.put(customTimePeriodIdW, listReportByWeek);
//					mapInvEnd.put(customTimePeriodIdW, listReportByEndWeek);
//					
//					List<GenericValue> listSfcMonth = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDateW, "periodTypeId", "SALES_MONTH")), null, null, null, false);
//					String customTimeSfcId = "";
//					if(UtilValidate.isNotEmpty(listSfcMonth)){
//						GenericValue sfcMonth = EntityUtil.getFirst(listSfcMonth);
//						customTimeSfcId = sfcMonth.getString("customTimePeriodId");
//					}
//					mapSfc.put(customTimePeriodIdW, customTimeSfcId);
//				}
//				
//				for(String customtime : listCustomTime){
//					GenericValue custom = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customtime), true);
//					List<List<Object>> listData = FastList.newInstance();
//					if(custom != null){
//						Date fromDateW = custom.getDate("fromDate");
//						Date thruDateW = custom.getDate("thruDate");
//						try {
//							Map<String, Object> mapData = dispatcher.runSync("evaluateSalesGridProductByRegion", UtilMisc.toMap("userLogin", userLogin, "fromDate", new Timestamp(fromDateW.getTime()), "thruDate",new Timestamp(thruDateW.getTime()), "region", organizationPartyId, "orderStatus", "ORDER_COMPLETED"));
//							listData = (List<List<Object>>) mapData.get("data");
//						} catch (GenericServiceException e) {
//							return ServiceUtil.returnError(e.getMessage());
//						}
//					}
//					listActualSales.add(listData);
//				}
//				
//				List<Map<String, Object>> listReturnResult = FastList.newInstance();
//				List<String> orderBy = FastList.newInstance();
//				orderBy.add("productCode");
//				List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("isVirtual", "N")), null, orderBy, null, false);
////				List<GenericValue> listConfigMoq = delegator.findList("ConfigPacking", null, null, null, null, false);
////				listConfigMoq = EntityUtil.filterByDate(listConfigMoq);
//				for(GenericValue product : listProduct){
//					
//					String productId = product.getString("productId");
//					String productCode = product.getString("productCode");
//					String productName = product.getString("productName");
//					BigDecimal tonDauTruoc = new BigDecimal(0);
//					BigDecimal salesTruoc = new BigDecimal(0);
//					BigDecimal planTruoc = new BigDecimal(0);
//					
//					//get MOQ product
//					List<GenericValue> listConfigMoq = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", "PP_PALLET", "uomToId", product.getString("quantityUomId"))), null, null, null, false);
//					listConfigMoq = EntityUtil.filterByDate(listConfigMoq);
//					BigDecimal moqProduct = new BigDecimal(1);
//					if(UtilValidate.isNotEmpty(listConfigMoq)){
//						GenericValue config = EntityUtil.getFirst(listConfigMoq);
//						moqProduct = config.getBigDecimal("quantityConvert");
//					}
//					
////					getSfcByWeek(delegator, listCustomTime, productId, userLoginId);
//					List<BigDecimal> listSfcWeek = getListSfcByWeek(delegator, listCustomTime, productId, userLoginId);
//					Locale locale = (Locale) context.get("locale");
//					Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIEOpenInventory", locale));
//					Map<String, Object> mapSales = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIESalesForcast", locale));
//					Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIEOrderQuantity", locale));
//					Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIEEndInventory", locale));
//					Map<String, Object> dayCover = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIEDayCoverDuration", locale));
//					Map<String, Object> accuracyMap = assignToMap(productId, productCode, productName, moqProduct, UtilProperties.getMessage(resource, "BIEForcastAccuracy", locale));
//					
//					for(GenericValue week : listTimeW){
//						//tinh Opening
//						BigDecimal cuoiTruoc = tonDauTruoc.subtract(salesTruoc).add(planTruoc);
//						
//						BigDecimal remainHead = new BigDecimal(0);
//						Date fromDateW = week.getDate("fromDate");
//						Date thruDateW = week.getDate("thruDate");
//						String customTimePeriodIdW = week.getString("customTimePeriodId");
//						String customTimeSfcId = (String)mapSfc.get(customTimePeriodIdW);
//						
//						if(customTimeSfcId.equals("")) return ServiceUtil.returnError("Not have customTimePeriodId of SFC like as customTimePeriod of plan");
//						int offSet = listCustomTime.indexOf(customTimeSfcId);
//						
//						Calendar calendar = Calendar.getInstance();
////						calendar.setTime(fromDateW);
////						calendar.add(Calendar.DATE, -2);
////						java.sql.Date prevDate =  new java.sql.Date(calendar.getTimeInMillis());
//						//TODO:lay ton dau ky cua tuan olap 
//						GenericValue productPlanItemW = null;
//						List<Map<String, Object>> listReport = (List<Map<String, Object>>)mapInv.get(customTimePeriodIdW);
//						if(UtilValidate.isNotEmpty(listReport)) {
//							for(Map<String, Object> report: listReport){
//								String productIdRe = (String)report.get("productId");
//								if(productId.equals(productIdRe)){
//									BigDecimal atp = (BigDecimal)report.get("avaliableToPromiseTotal");
//									remainHead = atp.add(remainHead);
//									break;
//								}
//							}
//						}else{
//						}
//						
//						BigDecimal tonDau = new BigDecimal(0);
//						BigDecimal tonCuoi = new BigDecimal(0);
//						BigDecimal sales = new BigDecimal(0);
//						BigDecimal plan = new BigDecimal(0);
//						BigDecimal accuracy = new BigDecimal(0);
//						//lay SFC cua thang nay
//						BigDecimal sfcThisWeek = new BigDecimal(0);
//						if(offSet < listSfcWeek.size() && offSet >= 0){
//							sfcThisWeek = listSfcWeek.get(offSet);
//						}
//						//lay sales FC 1 thang toi
//						String next1Week = null;
//						if((offSet+1)< listCustomTime.size() && (offSet+1)>= 0){
//							next1Week = listCustomTime.get(offSet+1);
//						}
//						BigDecimal saleFc1 = new BigDecimal(0);
////						BigDecimal saleFc2 = new BigDecimal(0);
//						if(next1Week != null){
//							saleFc1 = listSfcWeek.get(offSet+1);
//						}
//						
//						//lay so thuc te thuc hien cua plan
//						List<GenericValue> listProductPlanHeaderW = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("productPlanTypeId", "PO_PLAN", "customTimePeriodId", customTimePeriodIdW, "organizationPartyId", productPlanHeader.getString("organizationPartyId"))), null, null, null, true);
//						if(UtilValidate.isNotEmpty(listProductPlanHeaderW)){
//							GenericValue productPlanW = EntityUtil.getFirst(listProductPlanHeaderW);
//							GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanW.getString("productPlanId"), "customTimePeriodId", customTimePeriodIdW, "productId", productId), true);
//							if(productPlanItem != null){
//								if(productPlanItem.get("orderedQuantity") != null){
//									plan = ((BigDecimal)productPlanItem.get("orderedQuantity")).divide(moqProduct, 0,RoundingMode.UP);
//								}
//							}
//						}
//						
//						// so sanh dau tuan voi ngay hien tai
//						if(fromDateW.compareTo(new Date()) <=0){
//							tonDau = remainHead;
//						}else{
////							if(UtilValidate.isNotEmpty(listReport) || productPlanItemW != null){//lay ton du bao
//							if(UtilValidate.isNotEmpty(listReport)){
//								tonDau = remainHead;
//							}else{
//								tonDau = cuoiTruoc;
//							}
//							// lay so sales = SFC cua tuan
//							sales = sfcThisWeek;
//						}
//						
//						// so sanh cuoi tuan voi ngay hientai de tinh ending
//						if(thruDateW.compareTo(new Date()) <=0){
//							// tinh ton kho thuc te
//							List<Map<String, Object>> listReportThru = (List<Map<String, Object>>)mapInvEnd.get(customTimePeriodIdW);
//							if(UtilValidate.isNotEmpty(listReportThru)) {
//								for(Map<String, Object> report : listReportThru){
//									String productIdRe = (String)report.get("productId");
//									if(productId.equals(productIdRe)){
//										BigDecimal atp = (BigDecimal)report.get("avaliableToPromiseTotal");
//										tonCuoi = atp.add(tonCuoi);
//										break;
//									}
//								}
//							}
//							// lay so sale = actual sales, hien chua co actual sales => =0
//							List<List<Object>> listActualOneWeek = listActualSales.get(offSet);
//							for(Object map : listActualOneWeek){
//								Map<String, Object> tmpMap = (Map<String, Object>)map;
//								if(tmpMap.get("productId").equals(productId)){
//									sales = (BigDecimal) tmpMap.get("Quantity");
//									break;
//								}
//							}
//							
//							
//						}else{
//							sales = sfcThisWeek;
//							
//							tonCuoi = tonDau.subtract(sales).add(moqProduct.multiply(plan));
//						}
//	//gan lai cac so cua tuan truoc
//						tonDauTruoc = tonDau;
//						salesTruoc = sales;
////						planTruoc = plan;
//						planTruoc = moqProduct.multiply(plan);
//						if(sfcThisWeek.compareTo(new  BigDecimal(0)) > 0){
//							accuracy = sales.divide(sfcThisWeek, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
//						}
////						String customId = (String)week.get("customTimePeriodId");
//						mapOpen.put(customTimePeriodIdW, tonDau);
//						mapSales.put(customTimePeriodIdW, sales);
//						accuracyMap.put(customTimePeriodIdW, accuracy);
//						mapOrder.put(customTimePeriodIdW, plan);
//						mapEnding.put(customTimePeriodIdW, tonCuoi);
//						dayCover.put(customTimePeriodIdW, new BigDecimal(0));
//					}
//					listReturnResult.add(mapOpen);
//					listReturnResult.add(mapSales);
//					listReturnResult.add(accuracyMap);
//					listReturnResult.add(mapOrder);
//					listReturnResult.add(mapEnding);
//					listReturnResult.add(dayCover);
//				}
//				
//				result.put("listWeekHeader", listHeader);
//				result.put("listIterator", listReturnResult);
//			} catch (GenericEntityException e1) {
//				return ServiceUtil.returnError(e1.getMessage());
//			}
//			return result;
//		}
	    
	    public static Map<String, Object> getListMonthByYearSFCCustom(Delegator delegator, Date fromDate, Date thruDate, String fromMonth, String toMonth, String checkedWeek, String userLoginId){
			int fromM = Integer.parseInt(fromMonth);
			int thruM = Integer.parseInt(toMonth);
			
			Date current = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(current);
			int thisMonth = calendar.get(Calendar.MONTH);
			int thisDay = calendar.get(Calendar.DATE);
			calendar.setTime(fromDate);
			calendar.set(Calendar.MONTH, thisMonth);
			calendar.set(Calendar.DATE, thisDay);
			calendar.add(Calendar.MONTH, -fromM);
			java.sql.Date fromDatSql = new java.sql.Date(calendar.getTimeInMillis());
//			calendar.set(Calendar.MONTH, thisMonth);
			calendar.add(Calendar.MONTH, fromM);
			calendar.add(Calendar.MONTH, thruM);
			java.sql.Date thruDatSql = new java.sql.Date(calendar.getTimeInMillis());
//			calendar.add(Calendar.MONTH, -thruM);
			
			List<GenericValue> listCustomTimePeriod = FastList.newInstance();
			Map<String, Object> mapReturn = FastMap.newInstance();
			List<Map<String, Object>> listReturn = FastList.newInstance();
			List<EntityCondition> listAllConditions = FastList.newInstance();
			String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDatSql);
			EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDatSql);
			EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
			EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_MONTH");
			listAllConditions.add(cond1);
			listAllConditions.add(cond2);
			listAllConditions.add(cond3);
			listAllConditions.add(cond4);
			try {
				List<String> orderBy = FastList.newInstance();
				orderBy.add("fromDate");
				listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions), null, orderBy, null, true);
				if(UtilValidate.isNotEmpty(listCustomTimePeriod)){
					for(GenericValue time : listCustomTimePeriod){
						Map<String, Object> map = FastMap.newInstance();
						map.put("customTimePeriodId", time.getString("customTimePeriodId"));
						map.put("periodName", time.getString("periodName"));
						listReturn.add(map);
					}
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			
			calendar.setTime(fromDate);
			calendar.add(Calendar.YEAR, -1);
			Date fromDateLast = new java.sql.Date(calendar.getTimeInMillis());
			calendar.setTime(thruDate);
			calendar.add(Calendar.MONTH, thruM);
			Date thruDateLast = new java.sql.Date(calendar.getTimeInMillis());
			List<String> listCustomTime = getListCustomTimeSFCMonth(delegator, fromDateLast, thruDateLast, userLoginId);
			
			mapReturn.put("headerWeek", listReturn);
			mapReturn.put("listTimeW", listCustomTimePeriod);
			mapReturn.put("listCustomTime", listCustomTime);
			return mapReturn;
		}
	    
	    public static List<String> getListCustomTimeSFCMonth(Delegator delegator, Date fromDate, Date thruDate, String userLoginId){
	    	List<String> listCustom = FastList.newInstance();
	    	List<EntityCondition> listAllConditions = FastList.newInstance();
	    	String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
			EntityCondition cond2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
			EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
			EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "SALES_MONTH");
			listAllConditions.add(cond1);
			listAllConditions.add(cond2);
			listAllConditions.add(cond3);
			listAllConditions.add(cond4);
			List<String> orderBy = FastList.newInstance();
			orderBy.add("fromDate");
			
			try {
				
				List<GenericValue> listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions), null, orderBy, null, false);
				listCustom = EntityUtil.getFieldListFromEntityList(listCustomTimePeriod, "customTimePeriodId", true);
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			}
			return listCustom;
	    }
	    
	    public static Map<String, Object> getListMonthByYearSFCCustom(Delegator delegator, Date fromDate, Date thruDate, String userLoginId){
			Date current = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(current);
			int thisMonth = calendar.get(Calendar.MONTH);
			int thisDay = calendar.get(Calendar.DATE);
			calendar.setTime(fromDate);
			calendar.set(Calendar.MONTH, thisMonth);
			calendar.set(Calendar.DATE, thisDay);
			calendar.add(Calendar.MONTH, -2);
			java.sql.Date fromDatSql = new java.sql.Date(calendar.getTimeInMillis());
			
			calendar.add(Calendar.MONTH, 6);
			java.sql.Date thruDatSql = new java.sql.Date(calendar.getTimeInMillis());
			
			List<GenericValue> listCustomTimePeriod = FastList.newInstance();
			Map<String, Object> mapReturn = FastMap.newInstance();
			List<Map<String, Object>> listReturn = FastList.newInstance();
			List<EntityCondition> listAllConditions = FastList.newInstance();
			String partyIdTo = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
			EntityCondition cond1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDatSql);
			EntityCondition cond2 = EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDatSql);
			EntityCondition cond3 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdTo);
			EntityCondition cond4 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_MONTH");
			listAllConditions.add(cond1);
			listAllConditions.add(cond2);
			listAllConditions.add(cond3);
			listAllConditions.add(cond4);
			try {
				List<String> orderBy = FastList.newInstance();
				orderBy.add("fromDate");
				listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listAllConditions), null, orderBy, null, true);
				if(UtilValidate.isNotEmpty(listCustomTimePeriod)){
					for(GenericValue time : listCustomTimePeriod){
						Map<String, Object> map = FastMap.newInstance();
						map.put("customTimePeriodId", time.getString("customTimePeriodId"));
						map.put("periodName", time.getString("periodName"));
						listReturn.add(map);
					}
				}
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
			
			
			calendar.setTime(fromDate);
			calendar.add(Calendar.YEAR, -1);
			Date fromDateLast = new java.sql.Date(calendar.getTimeInMillis());
			calendar.setTime(thruDate);
			calendar.add(Calendar.MONTH, 3);
			Date thruDateLast = new java.sql.Date(calendar.getTimeInMillis());
			List<String> listCustomTime = getListCustomTimeSFCMonth(delegator, fromDateLast, thruDateLast, userLoginId);
			
			mapReturn.put("headerWeek", listReturn);
			mapReturn.put("listTimeW", listCustomTimePeriod);
			mapReturn.put("listCustomTime", listCustomTime);
			return mapReturn;
		}
	    public static BigDecimal listInvByDate(Delegator delegator, String productId, Timestamp lastUpdated, String ownerPartyId){
	    	BigDecimal quantity = BigDecimal.ZERO;
	    	List<EntityCondition> conds = FastList.newInstance();
	    	if (UtilValidate.isNotEmpty(ownerPartyId)) {
	    		conds.add(EntityCondition.makeCondition("ownerPartyId", ownerPartyId));
			}
	    	conds.add(EntityCondition.makeCondition("productId", productId));
	    	conds.add(EntityCondition.makeCondition("lastUpdated", EntityOperator.LESS_THAN_EQUAL_TO, lastUpdated));
			try {
				List<GenericValue> listInvTmps = delegator.findList("ProductFacilityHistoryInternalSumProduct", EntityCondition.makeCondition(conds), null, null, null, false);
				if (!listInvTmps.isEmpty()){
					quantity = listInvTmps.get(0).getBigDecimal("quantity");
				}
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductFacilityHistoryInternalSumProduct: " + e.toString();
				Debug.logError(e, errMsg, module);
				return quantity;
			}
			return quantity;
		}
	    
	    public static Map<String, Object> assignToMap(String productId, String productCode, String productName, BigDecimal moq, String SalesCycle, String paramKey){
			Map<String, Object> map = FastMap.newInstance();
			map.put("productId", productId);
			map.put("productName", productName);
			map.put("productCode", productCode);
			map.put("MOQ", moq);
			map.put("SalesCycle", SalesCycle);
			map.put("paramKey", paramKey);
			return map;
		}
	    
    public static List<Map<String, Object>> getListMonth(Delegator delegator, String fromMonth, String toMonth, Date currentDate, String userLoginId){
		List<GenericValue> listTime = FastList.newInstance();
		if(fromMonth != null && toMonth != null){
			listTime = getLastAndNextMonth(delegator, fromMonth, toMonth, currentDate, userLoginId);
		}else{
			listTime = getLastAndNextMonth(delegator, currentDate, userLoginId);
		}
		List<Map<String, Object>> listReturn = FastList.newInstance();
		listTime = EntityUtil.filterByAnd(listTime, UtilMisc.toMap("periodTypeId", "COMMERCIAL_MONTH"));
		for(GenericValue time : listTime){
			Map<String, Object> map = FastMap.newInstance();
			map.put("customTimePeriodId", time.getString("customTimePeriodId"));
			map.put("periodName", time.getString("periodName"));
			listReturn.add(map);
		}
		return listReturn;
	}
    
    public static BigDecimal getMoq(Delegator delegator, String productId, String supplierPartyId){
    	BigDecimal quantity = BigDecimal.ONE;
    	List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", supplierPartyId));
    	conds.add(EntityCondition.makeCondition("productId", productId));
    	List<String> orderBys = FastList.newInstance();
    	orderBys.add("minimumOrderQuantity");
		try {
			List<GenericValue> listInvTmps = delegator.findList("SupplierProduct", EntityCondition.makeCondition(conds), null, null, null, false);
			if (!listInvTmps.isEmpty()){
				quantity = listInvTmps.get(0).getBigDecimal("minimumOrderQuantity");
			}
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList SupplierProduct: " + e.toString();
			Debug.logError(e, errMsg, module);
			return quantity;
		}
		return quantity;
	}
    
    public static Map<String,Object> changeProductPlanStatus(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> success = ServiceUtil.returnSuccess();
		String productPlanId = (String)context.get("productPlanId");
		String statusId = (String)context.get("statusId");
		Delegator delegator = ctx.getDelegator();
		GenericValue objPlan = null;
		try {
			objPlan = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductPlanHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objPlan)) {
			String errMsg = "OLBIUS: Fatal error when changeProductPlanStatus - ProductPlanHeader not found!: " + productPlanId;
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		objPlan.put("statusId", statusId);
		try {
			delegator.store(objPlan);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when store ProductPlanHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String itemStatusId = null;
		
		String productPlanTypeId = objPlan.getString("productPlanTypeId");
		if ("IMPORT_PLAN".equals(productPlanTypeId)){
			switch (statusId) {
			case "IMPORT_PLAN_APPROVED":
				itemStatusId = "PLANITEM_APPROVED";
				break;
			case "IMPORT_PLAN_COMPLETED":
				itemStatusId = "PLANITEM_COMPLETED";
				break;
			case "IMPORT_PLAN_CANCELLED":
				itemStatusId = "PLANITEM_CANCELLED";
				break;
			default:
				break;
			}
		}
		if (UtilValidate.isNotEmpty(itemStatusId)) {
			
			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("parentProductPlanId", productPlanId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, statusId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IMPORT_PLAN_CANCELLED"));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IMPORT_PLAN_COMPLETED"));
			List<GenericValue> listProductPlanHeader = FastList.newInstance();
			try {
				listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(conds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductPlanHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listProductPlanHeader.isEmpty()){
				LocalDispatcher dispatcher = ctx.getDispatcher();
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String, Object> map = FastMap.newInstance();
				map.put("userLogin", userLogin);
				for (GenericValue item : listProductPlanHeader) {
					map.put("productPlanId", item.getString("productPlanId"));
					map.put("statusId", statusId);
					try {
						Map<String, Object> rs = dispatcher.runSync("changeProductPlanStatus", map);
						if (ServiceUtil.isError(rs)) {
							return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
						}
					} catch (GenericServiceException e) {
						String errMsg = "OLBIUS: Fatal error when run service changeProductPlanStatus: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
			}
			
			conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("productPlanId", EntityOperator.EQUALS, productPlanId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, itemStatusId));
			conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PLANITEM_CANCELLED"));
			
			List<GenericValue> listPlanItems = FastList.newInstance();
			try {
				listPlanItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductPlanItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listPlanItems.isEmpty()){
				for (GenericValue item : listPlanItems) {
					item.put("statusId", itemStatusId);
					try {
						delegator.store(item);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when store ProductPlanItem: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
				}
			}
		}
		success.put("productPlanId", productPlanId);
		success.put("statusId", statusId);
		return success;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getDataPlanByPeriod(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();

		String fromMonth = (String) context.get("fromMonth");
		String toMonth = (String) context.get("toMonth");
		String checkedWeek = (String) context.get("checkedWeek");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		String customTimePeriodIdSFC = (String) context.get("customTimePeriodId");
		String supplierPartyId = (String) context.get("supplierPartyId");
		String currencyUomId = (String) context.get("currencyUomId");

		String periodTypeId = "COMMERCIAL_MONTH";
		try {
			String tmp = PeriodServices.getCurrentPartyPeriodType(delegator, supplierPartyId);
			if (tmp != null)
				periodTypeId = tmp;
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.toString());
		}
		GenericValue customTimePeriodIdSFCGe = null;
		try {
			customTimePeriodIdSFCGe = delegator.findOne("CustomTimePeriod",
					UtilMisc.toMap("customTimePeriodId", customTimePeriodIdSFC), true);
		} catch (GenericEntityException e1) {
			return ServiceUtil.returnError(e1.getMessage());
		}
		Date fromDateY = customTimePeriodIdSFCGe.getDate("fromDate");
		Date thruDateY = customTimePeriodIdSFCGe.getDate("thruDate");
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (fromMonth != null && toMonth != null && checkedWeek != null) {
			mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, fromMonth, toMonth, checkedWeek,
					userLoginId, periodTypeId);
		} else {
			mapReturn = getListWeekByYearSFCCustom(delegator, fromDateY, thruDateY, userLoginId, periodTypeId);
		}
		
		String salesForecastId = getSalesForecastByPeriod(delegator, fromDateY, thruDateY, periodTypeId, organizationPartyId);
		result.put("salesForecastId", salesForecastId);
		List<Map<String, Object>> listMapPeriods = (List<Map<String, Object>>) mapReturn.get("listMapPeriods");
		List<GenericValue> listPeriods = (List<GenericValue>) mapReturn.get("listPeriods");
		List<String> listAllPeriods = (List<String>) mapReturn.get("listAllPeriods");

		Map<String, List<Map<String, Object>>> actualSalesMap = new HashMap<String, List<Map<String, Object>>>();
		for (GenericValue period : listPeriods) {
			Date fromDateD = period.getDate("fromDate");
			Date thruDateD = period.getDate("thruDate");
			Timestamp fromDate = new Timestamp(fromDateD.getTime());
			Timestamp thruDate = new Timestamp(thruDateD.getTime());
			String periodId = (String) period.getString("customTimePeriodId");

			// lay salesActual
			LocalDispatcher dispatcher = dpx.getDispatcher();
			try {
				Map<String, Object> mapData = dispatcher.runSync("calculateActualExportInventory",
						UtilMisc.toMap("userLogin", userLogin, "fromDate", new Timestamp(fromDate.getTime()),
								"thruDate", new Timestamp(thruDate.getTime())));
				List<Map<String, Object>> listData = (List<Map<String, Object>>) mapData.get("data");
				actualSalesMap.put(periodId, listData);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}

		List<Map<String, Object>> listReturnResult = FastList.newInstance();
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierPartyId));
		conds.add(EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId));
		List<GenericValue> listProduct = FastList.newInstance();
		try {
			listProduct = delegator.findList("SupplierProductGroupAndProduct", EntityCondition.makeCondition(conds),
					null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}

		List<GenericValue> listUom = FastList.newInstance();
		try {
			listUom = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", "SHIPMENT_PACKING"), null,
					null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList Uom: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		List<String> listUomIds = FastList.newInstance();
		if (!listUom.isEmpty()) {
			listUomIds = EntityUtil.getFieldListFromEntityList(listUom, "uomId", true);
		}

		String pallet = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.pallet");
		String container = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.container");
		GenericValue objUomConversion = null;
		try {
			objUomConversion = delegator.findOne("UomConversion", false,
					UtilMisc.toMap("uomId", container, "uomIdTo", pallet));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne UomConversion: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		BigDecimal capacity = BigDecimal.ONE;
		if (UtilValidate.isNotEmpty(objUomConversion)) {
			capacity = objUomConversion.getBigDecimal("conversionFactor");
		}

		List<String> customTimePeriodIds = EntityUtil.getFieldListFromEntityList(listPeriods, "customTimePeriodId",
				true);

		for (GenericValue product : listProduct) {
			String productId = product.getString("productId");

			GenericValue objProduct = null;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			String purchaseUomId = objProduct.getString("purchaseUomId");
			String quantityUomId = objProduct.getString("quantityUomId");
			if (UtilValidate.isEmpty(purchaseUomId)) {
				purchaseUomId = objProduct.getString("quantityUomId");
			}
			BigDecimal convert = BigDecimal.ONE;
			if (UtilValidate.isNotEmpty(quantityUomId) && UtilValidate.isNotEmpty(purchaseUomId)) {
				convert = ProductUtil.getConvertPackingNumber(delegator, productId, purchaseUomId, quantityUomId);
			}

			BigDecimal pack = BigDecimal.ONE;
			if (!listUomIds.isEmpty()) {
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("uomFromId", EntityOperator.EQUALS, pallet));
				conds.add(EntityCondition.makeCondition("uomToId", EntityOperator.EQUALS, purchaseUomId));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> listProductPacking = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("quantityConvert");
				try {
					listProductPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null,
							null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList ConfigPacking: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listProductPacking.isEmpty()) {
					pack = listProductPacking.get(0).getBigDecimal("quantityConvert");
				}
			}
			String productCode = product.getString("productCode");
			String productName = product.getString("productName");
			BigDecimal tonDauTruoc = new BigDecimal(0);
			BigDecimal salesTruoc = new BigDecimal(0);
			BigDecimal planTruoc = new BigDecimal(0);
			Locale locale = (Locale) context.get("locale");
			BigDecimal moq = getMoq(delegator, productId, supplierPartyId);

			List<BigDecimal> listSfcWeek = getListSfcByWeek(delegator, listAllPeriods, productId, userLoginId);
			Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEOpenInventory", locale), "OpenInventory");
			Map<String, Object> mapSales = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIESalesForcast", locale), "SalesForcast");
			Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEOrderQuantity", locale), "OrderQuantity");
			Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEEndInventory", locale), "EndInventory");

			for (GenericValue period : listPeriods) {
				String periodId = period.getString("customTimePeriodId");
				// tinh Opening
				BigDecimal cuoiTruoc = tonDauTruoc.subtract(salesTruoc).add(planTruoc);
				int offSet = listAllPeriods.indexOf(periodId);

				BigDecimal remainHead = new BigDecimal(0);
				Date fromDateW = period.getDate("fromDate");
				Timestamp fromDate = new Timestamp(fromDateW.getTime());

				remainHead = listInvByDate(delegator, productId, fromDate, organizationPartyId);
				BigDecimal tonDau = remainHead;
				BigDecimal tonCuoi = BigDecimal.ZERO;

				BigDecimal sales = new BigDecimal(0);
				BigDecimal plan = new BigDecimal(0);
				// lay SFC cua chu ky nay
				BigDecimal saleFc1 = new BigDecimal(0);
				BigDecimal sfcThisWeek = listSfcWeek.get(offSet);

				if (offSet < listAllPeriods.size() - 1) {
					// lay sales FC chu ky tiep theo
					String next1Week = listAllPeriods.get(offSet + 1);
					if (next1Week != null) {
						saleFc1 = listSfcWeek.get(offSet + 1);
					}
				}

				if (cuoiTruoc.compareTo(BigDecimal.ZERO) > 0) {
					tonDau = cuoiTruoc;
				}
				// lay so sales = SFC cua tuan
				sales = sfcThisWeek;

				// lay so plan theo cong thuc
				plan = sfcThisWeek.add(saleFc1.multiply(new BigDecimal(1.5))).subtract(tonDau);

				if (pack.compareTo(BigDecimal.ONE) > 0) {
					// lam tron xuong
					plan = plan.divide(convert, 0, RoundingMode.FLOOR);
					plan = convert.multiply(pack.multiply(plan.divide(pack, 0, RoundingMode.FLOOR)));
				}
				if (plan.compareTo(BigDecimal.ZERO) < 0) plan = BigDecimal.ZERO;
				BigDecimal unit = convert.multiply(pack);
				tonCuoi = tonDau.subtract(sales).add(plan);
				if (tonCuoi.compareTo(saleFc1) < 0) {
					// ton an toan < 100% salesfc thang sau
					BigDecimal lech = saleFc1.subtract(tonCuoi);
					if (lech.compareTo(unit) > 0) {
						BigDecimal tang = unit;
						while (lech.compareTo(tang) > 0) {
							tang = tang.add(unit);
						}
						plan = plan.add(tang);
					} else {
						plan = plan.add(unit);
					}
				}
				// bien do dao dong cho phep dieu chinh
				int up = 0;
				int down = 0;
				BigDecimal tmp = tonCuoi;
				while (tmp.compareTo(saleFc1.multiply(new BigDecimal(1.5))) < 0) {
					tmp = tmp.add(unit);
					up++;
				}
				BigDecimal tmp2 = tonCuoi;
				while (tmp2.compareTo(saleFc1) > 0) {
					tmp2 = tmp2.subtract(unit);
					down++;
				}

				// gan lai cac so cua tuan truoc
				tonDauTruoc = tonDau;
				salesTruoc = sales;
				planTruoc = plan;
				String customId = (String) period.get("customTimePeriodId");
				mapOpen.put(customId, tonDau.divide(convert, 0, RoundingMode.HALF_UP));
				mapOpen.put("uomId", purchaseUomId);
				mapOpen.put("convert", convert);
				mapOpen.put("pack", pack);
				mapOpen.put("type", "begin");

				mapSales.put(customId, sales.divide(convert, 0, RoundingMode.HALF_UP));
				mapSales.put("uomId", purchaseUomId);
				mapSales.put("convert", convert);
				mapSales.put("pack", pack);
				mapSales.put("type", "forecast");

				mapOrder.put(customId, plan.divide(convert, 0, RoundingMode.HALF_UP));
				mapOrder.put("uomId", purchaseUomId);
				mapOrder.put("convert", convert);
				mapOrder.put("pack", pack);
				mapOrder.put(customId+"_up", up);
				mapOrder.put(customId+"_down", down);
				mapOrder.put("type", "plan");

				mapEnding.put(customId, tonCuoi.divide(convert, 0, RoundingMode.HALF_UP));
				mapEnding.put("uomId", purchaseUomId);
				mapEnding.put("convert", convert);
				mapEnding.put("pack", pack);
				mapEnding.put("type", "end");
			}
			listReturnResult.add(mapOpen);
			listReturnResult.add(mapSales);
			listReturnResult.add(mapOrder);
			listReturnResult.add(mapEnding);
		}
		
		List<Map<String, Object>> listPlans = FastList.newInstance();
		for (Map<String, Object> map : listReturnResult) {
			if (map.containsKey("type") && "plan".equals(map.get("type"))) {
				listPlans.add(map);
			}
		}
		
		for (String periodId : customTimePeriodIds) {
			BigDecimal totalPallet = BigDecimal.ZERO;
			List<String> productIds = FastList.newInstance();
			for (Map<String, Object> map : listPlans) {
				BigDecimal plan = (BigDecimal) map.get(periodId);
				BigDecimal pack = (BigDecimal) map.get("pack");
				if (UtilValidate.isNotEmpty(plan) && UtilValidate.isNotEmpty(pack)) {
					BigDecimal palletNum = plan.divide(pack, 0, RoundingMode.FLOOR);
					totalPallet = totalPallet.add(palletNum);
				}
			}
			BigDecimal chanCont = capacity.multiply(totalPallet.divide(capacity, 0, RoundingMode.FLOOR));
			if (totalPallet.compareTo(chanCont) == 0)
				continue;
			BigDecimal needDown = BigDecimal.ZERO;
			BigDecimal needUp = BigDecimal.ZERO;
			if (chanCont.compareTo(BigDecimal.ZERO) <= 0) {
				needUp = capacity.subtract(chanCont);
			} else {
				needDown = totalPallet.subtract(chanCont);
			}
			if (needUp.compareTo(BigDecimal.ZERO) > 0) {
				int count = 0;
				while (needUp.compareTo(BigDecimal.ZERO) > 0) {
					for (Map<String, Object> map : listPlans) {
						if (needUp.compareTo(BigDecimal.ZERO) <= 0)
							break;
						BigDecimal plan = (BigDecimal) map.get(periodId);
						BigDecimal pack = (BigDecimal) map.get("pack");
						int up = (int) map.get(periodId+"_up");
						if (up >= 1) {
							plan = plan.add(pack);
							up--;
							needUp = needUp.subtract(BigDecimal.ONE);
						} else {
							if (map.containsKey("productId")
									&& UtilValidate.isNotEmpty(map.containsKey("productId"))) {
								if (!productIds.contains(map.get("productId"))) {
									productIds.add((String) map.get("productId"));
									count++;
								}
							}
						}
						map.put(periodId, plan);
						map.put(periodId+"_up", up);
					}
					if (count >= listPlans.size())
						needUp = BigDecimal.ZERO;
				}
			}
			if (needDown.compareTo(BigDecimal.ZERO) > 0) {
				int count = 0;
				while (needDown.compareTo(BigDecimal.ZERO) > 0) {
					for (Map<String, Object> map : listPlans) {
						if (needDown.compareTo(BigDecimal.ZERO) <= 0)
							break;
						BigDecimal plan = (BigDecimal) map.get(periodId);
						BigDecimal pack = (BigDecimal) map.get("pack");
						Integer down = (Integer) map.get(periodId+"_down");
						if (down >= 1) {
							plan = plan.subtract(pack);
							down--;
							needDown = needDown.subtract(BigDecimal.ONE);
						} else {
							if (map.containsKey("productId")
									&& UtilValidate.isNotEmpty(map.containsKey("productId"))) {
								if (!productIds.contains(map.get("productId"))) {
									productIds.add((String) map.get("productId"));
									count++;
								}
							}
						}
						map.put(periodId, plan);
						map.put(periodId+"_down", down);
					}
					if (count >= listPlans.size())
						needDown = BigDecimal.ZERO;
				}
			}
			for (Map<String, Object> map1 : listPlans) {
				for (Map<String, Object> map2 : listReturnResult) {
					if (map2.containsKey(periodId) && map2.containsKey("type") && "plan".equals(map2.get("type")) && map1.get("productId").equals(map2.get("productId"))) {
						map2.put(periodId, map1.get(periodId));
						map2.put(periodId+"_up", map1.get(periodId+"_up"));
						map2.put(periodId+"_down", map1.get(periodId+"_down"));
					}
				}
			}
		}

		result.put("listPeriods", listMapPeriods);
		result.put("listProducts", listReturnResult);
		result.put("periodTypeId", periodTypeId);
		return result;
	}
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> createImportPlan(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String listProducts = null;
		String productPlanId = null;
		if (UtilValidate.isNotEmpty(context.get("listProducts"))) {
			listProducts = (String) context.get("listProducts");
		}
		if (UtilValidate.isNotEmpty(listProducts)) {
			context.remove("listProducts");
			try {
				Map<String, Object> rs = dispatcher.runSync("createProductPlan", context);
				if (ServiceUtil.isError(rs)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
				}
				if (rs.containsKey("productPlanId")){
					productPlanId = (String)rs.get("productPlanId");
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run service createProductPlan: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(productPlanId)) {
				List<Map<String, Object>> products = null;
				try {
					products = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listProducts);
				} catch (ParseException e) {
					return ServiceUtil.returnError("OLBIUS: createProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
				};
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Delegator delegator = ctx.getDelegator();
				for (Map<String, Object> product : products) {
					if (UtilValidate.isNotEmpty(product.get("planQuantity")) && UtilValidate.isNotEmpty(product.get("productId")) 
							&& UtilValidate.isNotEmpty(product.get("inventoryForecast")) && UtilValidate.isNotEmpty(product.get("customTimePeriodId"))) {
						String productId = null;
						if (UtilValidate.isNotEmpty(product.get("productId"))) {
							productId = (String)product.get("productId");
						}
						GenericValue objProduct = null;
						try {
							objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (UtilValidate.isEmpty(objProduct)) continue;
						String uomId = null;
						if (UtilValidate.isNotEmpty(product.get("uomId"))) {
							uomId = (String)product.get("uomId");
						}
						BigDecimal convert = BigDecimal.ONE;
						if (UtilValidate.isNotEmpty(uomId)) {
							String quantityUomId = objProduct.getString("quantityUomId");
							if (ProductUtil.isWeightProduct(delegator, productId)) quantityUomId = objProduct.getString("weightUomId");
							convert = ProductUtil.getConvertPackingNumber(delegator, productId, uomId, quantityUomId);
						}
						
						String quantityStr = (String)product.get("planQuantity");
						
						BigDecimal qty = new BigDecimal(quantityStr);
						qty = qty.multiply(convert);
						String inventoryForecastStr = (String)product.get("inventoryForecast");
						
						BigDecimal qtyInv = new BigDecimal(inventoryForecastStr);
						qtyInv = qtyInv.multiply(convert);
						
						String customTimePeriodId = (String)product.get("customTimePeriodId");
						if (UtilValidate.isNotEmpty(quantityStr) && UtilValidate.isNotEmpty(productId)
								&& UtilValidate.isNotEmpty(inventoryForecastStr) && UtilValidate.isNotEmpty(customTimePeriodId)) {
							
							Map<String, Object> map = FastMap.newInstance();
							map.put("productPlanId", productPlanId);
							map.put("customTimePeriodId", customTimePeriodId);
							map.put("productId", productId);
							map.put("planQuantity", qty.toString());
							map.put("inventoryForecast", qtyInv.toString());
							map.put("statusId", "PLANITEM_CREATED");
							map.put("userLogin", userLogin);

							try {
								dispatcher.runSync("updatePlanItemWeek", map);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
						}
					}
				}
			}
		}
		result.put("productPlanId", productPlanId);
		return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetCustomTimePeriodImportPlan(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityCondition cond1 = EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "COMMERCIAL_YEAR");
		listAllConditions.add(cond1);
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listPeriods = FastList.newInstance();
		
		String supplierPartyId = null;
		if (parameters.containsKey("supplierPartyId") && parameters.get("supplierPartyId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("supplierPartyId"))) {
				supplierPartyId = parameters.get("supplierPartyId")[0];
			}
		}
		String currencyUomId = null;
		if (parameters.containsKey("currencyUomId") && parameters.get("currencyUomId").length > 0) {
			if (UtilValidate.isNotEmpty(parameters.get("currencyUomId"))) {
				currencyUomId = parameters.get("currencyUomId")[0];
			}
		}
		
		EntityCondition cond2 = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS,
				organizationPartyId);
		listAllConditions.add(cond2);
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("supplierPartyId", supplierPartyId));
		conds.add(EntityCondition.makeCondition("currencyUomId", currencyUomId));
		conds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
		conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IMPORT_PLAN_CANCELLED"));
		
		List<GenericValue> listProductPlanHeader = FastList.newInstance();
		try {
			listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ProductPlanHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listProductPlanHeader.isEmpty()){
			List<String> customTimePeriodIds = EntityUtil.getFieldListFromEntityList(listProductPlanHeader, "customTimePeriodId", true);
			listAllConditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.NOT_IN, customTimePeriodIds));
		}
		
		try {
			if (listSortFields.isEmpty()){
				listSortFields.add("periodName");
			}
			listPeriods = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "CustomTimePeriod", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetCustomTimePeriodImportPlan service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listPeriods);
		return successResult;
	}
    
   	public static Map<String, Object> updateProductPlanByOrder(DispatchContext ctx, Map<String, Object> context) {
   		Map<String, Object> result = FastMap.newInstance();
   		Delegator delegator = ctx.getDelegator();
   		String orderId = null;
		if (UtilValidate.isNotEmpty(context.get("orderId"))) {
			orderId = (String) context.get("orderId");
		}
		List<GenericValue> listProductPlanAndOrder = FastList.newInstance();
		try {
			listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition("orderId", orderId), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ProductPlanAndOrder: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listProductPlanAndOrder.isEmpty()){
			// fix 1 order 1 plan - 1 plan n order
			String productPlanId = listProductPlanAndOrder.get(0).getString("productPlanId");
			List<GenericValue> listProductPlanAndOrderByPlan = FastList.newInstance();
			try {
				listProductPlanAndOrderByPlan = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition("productPlanId", productPlanId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductPlanAndOrder: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			List<String> orderIds = EntityUtil.getFieldListFromEntityList(listProductPlanAndOrderByPlan, "orderId", true);
			List<GenericValue> listOrderItem = FastList.newInstance();
			try {
				listOrderItem = delegator.findList("OrderItemGroupProduct", EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList OrderItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			List<GenericValue> listProductPlanItem = FastList.newInstance();
			try {
				listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition("productPlanId", productPlanId), null, null, null,
						false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList ProductPlanItem: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			for (GenericValue item : listProductPlanItem) {
				String productId = item.getString("productId");
				BigDecimal orderedQuantity = BigDecimal.ZERO;
				for (GenericValue oi : listOrderItem) {
					if (productId.equals(oi.getString("productId")) && !"ITEM_CANCELLED".equals(oi.getString("statusId"))){
						orderedQuantity = orderedQuantity.add(oi.getBigDecimal("quantity"));
						if (UtilValidate.isNotEmpty(oi.get("cancelQuantity"))) {
							orderedQuantity = orderedQuantity.subtract(oi.getBigDecimal("quantity"));
						}
					}
				}
				item.put("orderedQuantity", orderedQuantity);
				try {
					delegator.store(item);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when store ProductPlanItem: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
			}
		}
		return result;
	}
   	
   	public static Map<String, Object> updateProductPlanByShipment(DispatchContext ctx, Map<String, Object> context) {
   		Map<String, Object> result = FastMap.newInstance();
   		Delegator delegator = ctx.getDelegator();
   		String shipmentId = null;
   		if (UtilValidate.isNotEmpty(context.get("shipmentId"))) {
   			shipmentId = (String) context.get("shipmentId");
   		}
   		GenericValue objShipment = null;
		try {
			objShipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne Shipment: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String orderId = null;
		if (UtilValidate.isNotEmpty(objShipment)) {
			orderId = objShipment.getString("primaryOrderId");
		}
		if (UtilValidate.isEmpty(orderId)) {
			return result;
		}
   		List<GenericValue> listProductPlanAndOrder = FastList.newInstance();
   		try {
   			listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition("orderId", orderId), null, null, null,
   					false);
   		} catch (GenericEntityException e) {
   			String errMsg = "OLBIUS: Fatal error when findList ProductPlanAndOrder: " + e.toString();
   			Debug.logError(e, errMsg, module);
   			return ServiceUtil.returnError(errMsg);
   		}
   		if (!listProductPlanAndOrder.isEmpty()){
   			// fix 1 order 1 plan - 1 plan n order
   			String productPlanId = listProductPlanAndOrder.get(0).getString("productPlanId");
   			List<GenericValue> listShipmentItem = FastList.newInstance();
   			try {
   				listShipmentItem = delegator.findList("ShipmentItem", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null,
   						false);
   			} catch (GenericEntityException e) {
   				String errMsg = "OLBIUS: Fatal error when findList ShipmentItem: " + e.toString();
   				Debug.logError(e, errMsg, module);
   				return ServiceUtil.returnError(errMsg);
   			}
   			List<GenericValue> listProductPlanItem = FastList.newInstance();
   			try {
   				listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition("productPlanId", productPlanId), null, null, null,
   						false);
   			} catch (GenericEntityException e) {
   				String errMsg = "OLBIUS: Fatal error when findList ProductPlanItem: " + e.toString();
   				Debug.logError(e, errMsg, module);
   				return ServiceUtil.returnError(errMsg);
   			}
   			for (GenericValue item : listProductPlanItem) {
   				String productId = item.getString("productId");
   				BigDecimal recentPlanQuantity = BigDecimal.ZERO;
   				for (GenericValue oi : listShipmentItem) {
   					if (productId.equals(oi.getString("productId"))){
   						if (ProductUtil.isWeightProduct(delegator, productId)){
   							if (UtilValidate.isNotEmpty(oi.get("weight"))) {
   								recentPlanQuantity = recentPlanQuantity.add(oi.getBigDecimal("weight"));
							}
   						} else {
   							recentPlanQuantity = recentPlanQuantity.add(oi.getBigDecimal("quantity"));
   						}
   					}
   				}
   				item.put("recentPlanQuantity", recentPlanQuantity);
   				try {
   					delegator.store(item);
   				} catch (GenericEntityException e) {
   					String errMsg = "OLBIUS: Fatal error when store ProductPlanItem: " + e.toString();
   					Debug.logError(e, errMsg, module);
   					return ServiceUtil.returnError(errMsg);
   				}
   			}
   		}
   		return result;
   	}
   	
   	public static String getSalesForecastByPeriod(Delegator delegator, Date fromDate, Date thruDate, String periodTypeId, String organizationPartyId){
   		String salesForecastId = null;
   		List<EntityCondition> cond2s = FastList.newInstance();
   		cond2s = FastList.newInstance();
		cond2s.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
		cond2s.add(EntityCondition.makeCondition("periodTypeId", "SALES_YEAR"));
		cond2s.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
		cond2s.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate));
		List<GenericValue> listCustomTimeSales = FastList.newInstance();
		try {
			listCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(cond2s), null, null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
			Debug.logError(e, errMsg, module);
			return salesForecastId;
		}
		if (!listCustomTimeSales.isEmpty()){
			List<GenericValue> forcasts = FastList.newInstance();
			try {
				forcasts = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", listCustomTimeSales.get(0).getString("customTimePeriodId"),
								"organizationPartyId", organizationPartyId, "internalPartyId", organizationPartyId)), null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.log(e.getMessage());
			}
			if (!forcasts.isEmpty()) {
				GenericValue sfc = EntityUtil.getFirst(forcasts);
				salesForecastId = sfc.getString("salesForecastId");
			}
		}
   		
   		return salesForecastId;
   	}
   	
	public static Map<String, Object> getDataImportPlan(DispatchContext dpx, Map<String, Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		String productPlanId = null;
		if (UtilValidate.isNotEmpty(context.get("productPlanId"))) {
			productPlanId = (String) context.get("productPlanId");
		}
			
		GenericValue objProductPlanHeader = null;
		try {
			objProductPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne ProductPlanHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objProductPlanHeader)) {
			String errMsg = "OLBIUS: Fatal error when getDataImportPlan: ProductPlanHeader not found!";
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String customTimePeriodId = objProductPlanHeader.getString("customTimePeriodId");
		GenericValue objCustomTimePeriod = null;
		try {
			objCustomTimePeriod = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne CustomTimePeriod: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String supplierPartyId = objCustomTimePeriod.getString("supplierPartyId");
		
		List<GenericValue> listPeriods = FastList.newInstance();
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("parentProductPlanId", productPlanId));
		conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "IMPORT_PLAN_CANCELLED"));
		List<GenericValue> listProductPlanHeader = FastList.newInstance();
		try {
			listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList ProductPlanHeader: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		
		List<String> listAllPeriods = EntityUtil.getFieldListFromEntityList(listProductPlanHeader, "customTimePeriodId", true);
		try {
			listPeriods = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, listAllPeriods), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList CustomTimePeriod: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		List<Map<String, Object>> listReturnResult = FastList.newInstance();
		conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, listAllPeriods));
		conds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PLANITEM_CANCELLED"));
		List<GenericValue> listProduct = FastList.newInstance();
		try {
			listProduct = delegator.findList("ProductPlanItemAndProduct", EntityCondition.makeCondition(conds),
					null, null, null, false);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(e.getMessage());
		}
		List<String> productIdTmps = EntityUtil.getFieldListFromEntityList(listProduct, "productId", true);
		List<GenericValue> listProducts = FastList.newInstance();
		try {
			listProducts = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIdTmps), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList Product: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}

		List<GenericValue> listUom = FastList.newInstance();
		try {
			listUom = delegator.findList("Uom", EntityCondition.makeCondition("uomTypeId", "SHIPMENT_PACKING"), null,
					null, null, false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList Uom: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		List<String> listUomIds = FastList.newInstance();
		if (!listUom.isEmpty()) {
			listUomIds = EntityUtil.getFieldListFromEntityList(listUom, "uomId", true);
		}

		String pallet = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.pallet");
		String container = UtilProperties.getPropertyValue(IMEX_PROPERTIES, "imex.import.packing.uom.container");
		GenericValue objUomConversion = null;
		try {
			objUomConversion = delegator.findOne("UomConversion", false,
					UtilMisc.toMap("uomId", container, "uomIdTo", pallet));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne UomConversion: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		BigDecimal capacity = BigDecimal.ONE;
		if (UtilValidate.isNotEmpty(objUomConversion)) {
			capacity = objUomConversion.getBigDecimal("conversionFactor");
		}

		for (GenericValue product : listProducts) {
			String productId = product.getString("productId");

			GenericValue objProduct = null;
			try {
				objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			String purchaseUomId = objProduct.getString("purchaseUomId");
			String quantityUomId = objProduct.getString("quantityUomId");
			if (UtilValidate.isEmpty(purchaseUomId)) {
				purchaseUomId = objProduct.getString("quantityUomId");
			}
			BigDecimal convert = BigDecimal.ONE;
			if (UtilValidate.isNotEmpty(quantityUomId) && UtilValidate.isNotEmpty(purchaseUomId)) {
				convert = ProductUtil.getConvertPackingNumber(delegator, productId, purchaseUomId, quantityUomId);
			}

			BigDecimal pack = BigDecimal.ONE;
			if (!listUomIds.isEmpty()) {
				conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("uomFromId", EntityOperator.EQUALS, pallet));
				conds.add(EntityCondition.makeCondition("uomToId", EntityOperator.EQUALS, purchaseUomId));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> listProductPacking = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("quantityConvert");
				try {
					listProductPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(conds), null,
							null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList ConfigPacking: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listProductPacking.isEmpty()) {
					pack = listProductPacking.get(0).getBigDecimal("quantityConvert");
				}
			}
			String productCode = product.getString("productCode");
			String productName = product.getString("productName");
			BigDecimal tonDauTruoc = new BigDecimal(0);
			BigDecimal salesTruoc = new BigDecimal(0);
			BigDecimal planTruoc = new BigDecimal(0);
			Locale locale = (Locale) context.get("locale");
			BigDecimal moq = getMoq(delegator, productId, supplierPartyId);

			List<BigDecimal> listSfcWeek = getListSfcByWeek(delegator, listAllPeriods, productId, userLoginId);
			Map<String, Object> mapOpen = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEOpenInventory", locale), "OpenInventory");
			Map<String, Object> mapSales = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIESalesForcast", locale), "SalesForcast");
			Map<String, Object> mapOrder = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEOrderQuantity", locale), "OrderQuantity");
			Map<String, Object> mapEnding = assignToMap(productId, productCode, productName, moq,
					UtilProperties.getMessage(resource, "BIEEndInventory", locale), "EndInventory");

			for (GenericValue period : listPeriods) {
				String periodId = period.getString("customTimePeriodId");
				// tinh Opening
				BigDecimal cuoiTruoc = tonDauTruoc.subtract(salesTruoc).add(planTruoc);
				int offSet = listAllPeriods.indexOf(periodId);

				BigDecimal remainHead = new BigDecimal(0);
				Date fromDateW = period.getDate("fromDate");
				Timestamp fromDate = new Timestamp(fromDateW.getTime());

				remainHead = listInvByDate(delegator, productId, fromDate, organizationPartyId);
				BigDecimal tonDau = remainHead;
				BigDecimal tonCuoi = BigDecimal.ZERO;

				BigDecimal sales = new BigDecimal(0);
				BigDecimal plan = new BigDecimal(0);
				// lay SFC cua chu ky nay
				BigDecimal saleFc1 = new BigDecimal(0);
				BigDecimal sfcThisWeek = listSfcWeek.get(offSet);

				if (offSet < listAllPeriods.size() - 1) {
					// lay sales FC chu ky tiep theo
					String next1Week = listAllPeriods.get(offSet + 1);
					if (next1Week != null) {
						saleFc1 = listSfcWeek.get(offSet + 1);
					}
				}

				if (cuoiTruoc.compareTo(BigDecimal.ZERO) > 0) {
					tonDau = cuoiTruoc;
				}
				// lay so sales = SFC cua tuan
				sales = sfcThisWeek;

				// lay so plan theo cong thuc
				plan = sfcThisWeek.add(saleFc1.multiply(new BigDecimal(1.5))).subtract(tonDau);

				if (pack.compareTo(BigDecimal.ONE) > 0) {
					// lam tron xuong
					plan = plan.divide(convert, 0, RoundingMode.FLOOR);
					plan = convert.multiply(pack.multiply(plan.divide(pack, 0, RoundingMode.FLOOR)));
				}
				if (plan.compareTo(BigDecimal.ZERO) < 0) plan = BigDecimal.ZERO;
				BigDecimal unit = convert.multiply(pack);
				tonCuoi = tonDau.subtract(sales).add(plan);
				if (tonCuoi.compareTo(saleFc1) < 0) {
					// ton an toan < 100% salesfc thang sau
					BigDecimal lech = saleFc1.subtract(tonCuoi);
					if (lech.compareTo(unit) > 0) {
						BigDecimal tang = unit;
						while (lech.compareTo(tang) > 0) {
							tang = tang.add(unit);
						}
						plan = plan.add(tang);
					} else {
						plan = plan.add(unit);
					}
				}
				// bien do dao dong cho phep dieu chinh
				int up = 0;
				int down = 0;
				BigDecimal tmp = tonCuoi;
				while (tmp.compareTo(saleFc1.multiply(new BigDecimal(1.5))) < 0) {
					tmp = tmp.add(unit);
					up++;
				}
				BigDecimal tmp2 = tonCuoi;
				while (tmp2.compareTo(saleFc1) > 0) {
					tmp2 = tmp2.subtract(unit);
					down++;
				}

				// gan lai cac so cua tuan truoc
				tonDauTruoc = tonDau;
				salesTruoc = sales;
				planTruoc = plan;
				String customId = (String) period.get("customTimePeriodId");
				mapOpen.put(customId, tonDau.divide(convert, 0, RoundingMode.HALF_UP));
				mapOpen.put("uomId", purchaseUomId);
				mapOpen.put("convert", convert);
				mapOpen.put("pack", pack);
				mapOpen.put("type", "begin");

				mapSales.put(customId, sales.divide(convert, 0, RoundingMode.HALF_UP));
				mapSales.put("uomId", purchaseUomId);
				mapSales.put("convert", convert);
				mapSales.put("pack", pack);
				mapSales.put("type", "forecast");

				mapOrder.put(customId, plan.divide(convert, 0, RoundingMode.HALF_UP));
				mapOrder.put("uomId", purchaseUomId);
				mapOrder.put("convert", convert);
				mapOrder.put("pack", pack);
				mapOrder.put(customId+"_up", up);
				mapOrder.put(customId+"_down", down);
				mapOrder.put("type", "plan");

				mapEnding.put(customId, tonCuoi.divide(convert, 0, RoundingMode.HALF_UP));
				mapEnding.put("uomId", purchaseUomId);
				mapEnding.put("convert", convert);
				mapEnding.put("pack", pack);
				mapEnding.put("type", "end");
			}
			listReturnResult.add(mapOpen);
			listReturnResult.add(mapSales);
			listReturnResult.add(mapOrder);
			listReturnResult.add(mapEnding);
		}
		
		List<Map<String, Object>> listPlans = FastList.newInstance();
		for (Map<String, Object> map : listReturnResult) {
			if (map.containsKey("type") && "plan".equals(map.get("type"))) {
				listPlans.add(map);
			}
		}
		
		for (String periodId : listAllPeriods) {
			BigDecimal totalPallet = BigDecimal.ZERO;
			List<String> productIds = FastList.newInstance();
			for (Map<String, Object> map : listPlans) {
				BigDecimal plan = (BigDecimal) map.get(periodId);
				BigDecimal pack = (BigDecimal) map.get("pack");
				if (UtilValidate.isNotEmpty(plan) && UtilValidate.isNotEmpty(pack)) {
					BigDecimal palletNum = plan.divide(pack, 0, RoundingMode.FLOOR);
					totalPallet = totalPallet.add(palletNum);
				}
			}
			BigDecimal chanCont = capacity.multiply(totalPallet.divide(capacity, 0, RoundingMode.FLOOR));
			if (totalPallet.compareTo(chanCont) == 0)
				continue;
			BigDecimal needDown = BigDecimal.ZERO;
			BigDecimal needUp = BigDecimal.ZERO;
			if (chanCont.compareTo(BigDecimal.ZERO) <= 0) {
				needUp = capacity.subtract(chanCont);
			} else {
				needDown = totalPallet.subtract(chanCont);
			}
			if (needUp.compareTo(BigDecimal.ZERO) > 0) {
				int count = 0;
				while (needUp.compareTo(BigDecimal.ZERO) > 0) {
					for (Map<String, Object> map : listPlans) {
						if (needUp.compareTo(BigDecimal.ZERO) <= 0)
							break;
						BigDecimal plan = (BigDecimal) map.get(periodId);
						BigDecimal pack = (BigDecimal) map.get("pack");
						int up = (int) map.get(periodId+"_up");
						if (up >= 1) {
							plan = plan.add(pack);
							up--;
							needUp = needUp.subtract(BigDecimal.ONE);
						} else {
							if (map.containsKey("productId")
									&& UtilValidate.isNotEmpty(map.containsKey("productId"))) {
								if (!productIds.contains(map.get("productId"))) {
									productIds.add((String) map.get("productId"));
									count++;
								}
							}
						}
						map.put(periodId, plan);
						map.put(periodId+"_up", up);
					}
					if (count >= listPlans.size())
						needUp = BigDecimal.ZERO;
				}
			}
			if (needDown.compareTo(BigDecimal.ZERO) > 0) {
				int count = 0;
				while (needDown.compareTo(BigDecimal.ZERO) > 0) {
					for (Map<String, Object> map : listPlans) {
						if (needDown.compareTo(BigDecimal.ZERO) <= 0)
							break;
						BigDecimal plan = (BigDecimal) map.get(periodId);
						BigDecimal pack = (BigDecimal) map.get("pack");
						Integer down = (Integer) map.get(periodId+"_down");
						if (down >= 1) {
							plan = plan.subtract(pack);
							down--;
							needDown = needDown.subtract(BigDecimal.ONE);
						} else {
							if (map.containsKey("productId")
									&& UtilValidate.isNotEmpty(map.containsKey("productId"))) {
								if (!productIds.contains(map.get("productId"))) {
									productIds.add((String) map.get("productId"));
									count++;
								}
							}
						}
						map.put(periodId, plan);
						map.put(periodId+"_down", down);
					}
					if (count >= listPlans.size())
						needDown = BigDecimal.ZERO;
				}
			}
			for (Map<String, Object> map1 : listPlans) {
				for (Map<String, Object> map2 : listReturnResult) {
					if (map2.containsKey(periodId) && map2.containsKey("type") && "plan".equals(map2.get("type")) && map1.get("productId").equals(map2.get("productId"))) {
						map2.put(periodId, map1.get(periodId));
						map2.put(periodId+"_up", map1.get(periodId+"_up"));
						map2.put(periodId+"_down", map1.get(periodId+"_down"));
					}
				}
			}
		}

		result.put("listPeriods", listPeriods);
		result.put("listProducts", listReturnResult);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateImportPlan(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String listProducts = null;
		
		String productPlanId = null;
		if (UtilValidate.isNotEmpty(context.get("productPlanId"))) {
			productPlanId = (String) context.get("productPlanId");
		}
		
		if (UtilValidate.isNotEmpty(context.get("listProducts"))) {
			listProducts = (String) context.get("listProducts");
		}
		if (UtilValidate.isNotEmpty(listProducts)) {
			Delegator delegator = ctx.getDelegator();
			GenericValue objProductPlanHeader = null;
			try {
				objProductPlanHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne ProductPlanHeader: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (UtilValidate.isNotEmpty(objProductPlanHeader)) {
				List<Map<String, Object>> products = null;
				try {
					products = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", listProducts);
				} catch (ParseException e) {
					return ServiceUtil.returnError("OLBIUS: createProductEvent error when JqxWidgetSevices.convert ! " + e.toString());
				};
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				for (Map<String, Object> product : products) {
					if (UtilValidate.isNotEmpty(product.get("planQuantity")) && UtilValidate.isNotEmpty(product.get("productId")) 
							&& UtilValidate.isNotEmpty(product.get("inventoryForecast")) && UtilValidate.isNotEmpty(product.get("customTimePeriodId"))) {
						String productId = null;
						if (UtilValidate.isNotEmpty(product.get("productId"))) {
							productId = (String)product.get("productId");
						}
						GenericValue objProduct = null;
						try {
							objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (UtilValidate.isEmpty(objProduct)) continue;
						String uomId = null;
						if (UtilValidate.isNotEmpty(product.get("uomId"))) {
							uomId = (String)product.get("uomId");
						}
						BigDecimal convert = BigDecimal.ONE;
						if (UtilValidate.isNotEmpty(uomId)) {
							String quantityUomId = objProduct.getString("quantityUomId");
							if (ProductUtil.isWeightProduct(delegator, productId)) quantityUomId = objProduct.getString("weightUomId");
							convert = ProductUtil.getConvertPackingNumber(delegator, productId, uomId, quantityUomId);
						}
						
						String quantityStr = (String)product.get("planQuantity");
						
						BigDecimal qty = new BigDecimal(quantityStr);
						qty = qty.multiply(convert);
						String inventoryForecastStr = (String)product.get("inventoryForecast");
						
						BigDecimal qtyInv = new BigDecimal(inventoryForecastStr);
						qtyInv = qtyInv.multiply(convert);
						
						String customTimePeriodId = (String)product.get("customTimePeriodId");
						if (UtilValidate.isNotEmpty(quantityStr) && UtilValidate.isNotEmpty(productId)
								&& UtilValidate.isNotEmpty(inventoryForecastStr) && UtilValidate.isNotEmpty(customTimePeriodId)) {
							
							Map<String, Object> map = FastMap.newInstance();
							map.put("productPlanId", productPlanId);
							map.put("customTimePeriodId", customTimePeriodId);
							map.put("productId", productId);
							map.put("planQuantity", qty.toString());
							map.put("inventoryForecast", qtyInv.toString());
							map.put("userLogin", userLogin);

							try {
								dispatcher.runSync("updatePlanItemWeek", map);
							} catch (GenericServiceException e) {
								return ServiceUtil.returnError(e.getMessage());
							}
						}
					}
				}
			}
		}
		result.put("productPlanId", productPlanId);
		return result;
    }
}
