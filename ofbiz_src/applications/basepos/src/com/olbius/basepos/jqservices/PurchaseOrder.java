package com.olbius.basepos.jqservices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepos.util.PosUtil;
import com.olbius.security.util.SecurityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PurchaseOrder {
	public static final String module = PurchaseOrder.class.getName();
	public static final String resource_error = "BasePosErrorUiLabels";

	public static Map<String, Object> jQCalculatePODetail(DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericEntityException, ParseException {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;
		int totalRows = 0;
		String[] productIds = parameters.get("productId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String productId = null;
		if (productIds != null) {
			productId = productIds[0];
		}
		if (UtilValidate.isNotEmpty(productId)) {
			List<GenericValue> listFacilities = delegator.findList("Facility", null, null, null, null, false);
			if (UtilValidate.isNotEmpty(listFacilities)) {
				for (GenericValue facility : listFacilities) {
					Map<String, Object> facilityDetail = new HashMap<String, Object>();
					facilityDetail.put("productId", productId);
					String facilityId = facility.getString("facilityId");
					facilityDetail.put("facilityName", facility.getString("facilityName"));
					facilityDetail.put("facilityId", facilityId);
					BigDecimal qpd = BigDecimal.ZERO;
					BigDecimal qoo = BigDecimal.ZERO;
					BigDecimal qoh = BigDecimal.ZERO;
					BigDecimal totalQuantity = BigDecimal.ZERO;
					GenericValue productFacility = delegator.findOne("ProductFacility",
							UtilMisc.toMap("productId", productId, "facilityId", facilityId), false);
					if (UtilValidate.isNotEmpty(productFacility)) {
						if (UtilValidate.isNotEmpty(productFacility.getBigDecimal("qpd"))) {
							qpd = productFacility.getBigDecimal("qpd");
						}
						if (UtilValidate.isNotEmpty(productFacility.getBigDecimal("qoo"))) {
							qoo = productFacility.getBigDecimal("qoo");
							totalQuantity = totalQuantity.add(qoo);
						}
						if (UtilValidate.isNotEmpty(productFacility.getBigDecimal("lastInventoryCount"))) {
							qoh = productFacility.getBigDecimal("lastInventoryCount");
							totalQuantity = totalQuantity.add(qoh);
						}
					}
					facilityDetail.put("qpdDetail", qpd);
					facilityDetail.put("qohDetail", qoh);
					facilityDetail.put("qooDetail", qoo);
					if (UtilValidate.isNotEmpty(qoh) && UtilValidate.isNotEmpty(qpd)
							&& qpd.compareTo(BigDecimal.ZERO) != 0) {
						BigDecimal facilityLid = totalQuantity.divide(qpd, 1, RoundingMode.HALF_UP);
						if (UtilValidate.isNotEmpty(facilityLid)) {
							facilityDetail.put("facilityLid", facilityLid);
						}
					}
					listReturn.add(facilityDetail);
				}
			}
		}

		if (end > listReturn.size()) {
			end = listReturn.size();
		}
		totalRows = listReturn.size();
		successResult.put("TotalRows", String.valueOf(totalRows));
		successResult.put("listIterator", listReturn);
		return successResult;
	}
	
	public static Map<String, Object> getProductAndProductFacility(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<EntityCondition> listHavingConditions = FastList.newInstance();
		List<EntityCondition> listOtherConditions = FastList.newInstance();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		int start = pagesize * pageNum;
		int end = start + pagesize;
		int totalRows = 0;
		String[] supplierIds = parameters.get("supplierId");
		Set<String> listProducts = FastSet.newInstance();
		String supplierId = null;
		if (supplierIds != null) {
			supplierId = supplierIds[0];
		}
		if (UtilValidate.isNotEmpty(listAllConditions)){
			for (int i = 0; i < listAllConditions.size(); i++){
				String cond = listAllConditions.get(i).toString();
				if (cond.contains("qoh") || cond.contains("qoo") || (cond.contains("lastSold") || (cond.contains("lastReceived")))){
					listHavingConditions.add(listAllConditions.get(i));
					listAllConditions.remove(i);
					i--;
				}
				if (cond.contains("qpd") || (cond.contains("sysLid"))){
					listOtherConditions.add(listAllConditions.get(i));
					listAllConditions.remove(i);
					i--;
				}
			}
		}
		if(UtilValidate.isEmpty(listSortFields)){
    		listSortFields = FastList.newInstance();
    		listSortFields.add("productId");
    	}
		if (UtilValidate.isNotEmpty(supplierId) && (supplierId != null)) {
			String supplierIdLists = supplierId.trim();
			String[] supplierIdList = supplierIdLists.split(",");
			for (String sup : supplierIdList) {
				List<EntityCondition> supplierConditions = FastList.newInstance();
				supplierConditions.add(EntityCondition.makeCondition("availableThruDate", EntityOperator.EQUALS, null));
				supplierConditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, sup));
				EntityCondition supplierCondition = EntityCondition.makeCondition(supplierConditions, EntityOperator.AND);
				List<GenericValue> productsOfSupplier = delegator.findList("SupplierProduct", supplierCondition, null, null,
						null, false);
				for (GenericValue proOfSup : productsOfSupplier) {
					listProducts.add(proOfSup.getString("productId"));
				}
			}
		}
		if(UtilValidate.isNotEmpty(listProducts)){
			listAllConditions.add(EntityCondition.makeCondition("productId", EntityOperator.IN, listProducts));
		}
		EntityCondition productTypeCond = EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD");
		listAllConditions.add(productTypeCond);
		EntityListIterator productAndFacilityIterator = null;
		try {
			productAndFacilityIterator = (EntityListIterator) delegator.find("ProductAndProductFacility", 
					EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), 
					EntityCondition.makeCondition(listHavingConditions, EntityOperator.AND), null, listSortFields, opts);
			
			List<GenericValue> productAndFacilityList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productAndFacilityIterator)){
				productAndFacilityList = productAndFacilityIterator.getCompleteList();
			}
			
			//listFacility 
			List<GenericValue> facilityList = FastList.newInstance();
			facilityList = delegator.findList("Facility",null , null, null, null, false);
			//calculatedProductList
			List<Map<String, Object>> calculatedProductList = FastList.newInstance();
			if(UtilValidate.isNotEmpty(facilityList)){
				for (GenericValue productFacility : productAndFacilityList) {
					BigDecimal unitCost = BigDecimal.ZERO;
					BigDecimal qpd = BigDecimal.ZERO;				int count = 1;
					int countQPD = 1;
					Map<String, Object> productFacilityTmp = FastMap.newInstance();
					productFacilityTmp.put("productId", productFacility.getString("productId"));
					productFacilityTmp.put("productName", productFacility.getString("internalName"));
					productFacilityTmp.put("productTypeId", productFacility.getString("productTypeId"));
					productFacilityTmp.put("lastSold", productFacility.getTimestamp("lastSold"));
					productFacilityTmp.put("lastReceived", productFacility.getTimestamp("lastReceived"));
					productFacilityTmp.put("pickStandard", productFacility.getBigDecimal("pickStandard"));
					BigDecimal qoo = productFacility.getBigDecimal("qoo") != null ? productFacility.getBigDecimal("qoo") : BigDecimal.ZERO;
					BigDecimal qoh = productFacility.getBigDecimal("qoh") != null ? productFacility.getBigDecimal("qoh") : BigDecimal.ZERO;
					productFacilityTmp.put("qoo", qoo);
					productFacilityTmp.put("qoh", qoh);
					for (GenericValue facility : facilityList) {
						GenericValue productFacilityEntity = delegator.findOne("ProductFacility", UtilMisc.toMap("productId", productFacility.getString("productId"), "facilityId", facility.getString("facilityId")), false);
						if(UtilValidate.isNotEmpty(productFacilityEntity)){
							BigDecimal unitCostTmp = productFacilityEntity.getBigDecimal("unitCost");
							if(UtilValidate.isNotEmpty(unitCostTmp) && unitCostTmp.compareTo(BigDecimal.ZERO) ==1){
								unitCost = unitCost.add(unitCostTmp);
								count = count + 1;
							}
							BigDecimal qpdTmp = productFacilityEntity.getBigDecimal("qpd");
							if(UtilValidate.isNotEmpty(qpdTmp)){
								qpd = qpd.add(qpdTmp);
								countQPD = countQPD + 1;
							}
							if(count >0){
								BigDecimal tmpCount = BigDecimal.valueOf(count);
								unitCost = unitCost.divide(tmpCount, 0, RoundingMode.HALF_UP);
							}
							if(countQPD >0){
								BigDecimal totalFacility = new BigDecimal(countQPD);
								qpd = qpd.divide(totalFacility, 1, RoundingMode.HALF_UP);
							}
						}
					}
					BigDecimal sys_total = qoh.add(qoo);
					BigDecimal lid = null;
					if(UtilValidate.isNotEmpty(qpd) && qpd.compareTo(BigDecimal.ZERO) != 0){
						lid = sys_total.divide(qpd,1, RoundingMode.HALF_UP);
					}
					productFacilityTmp.put("qpd", qpd);
					productFacilityTmp.put("sysLid", lid);
					productFacilityTmp.put("totalLid", lid);
					productFacilityTmp.put("unitCost", unitCost);
					calculatedProductList.add(productFacilityTmp);
				}
			}
			calculatedProductList = PosUtil.doFilter(calculatedProductList, listOtherConditions);
			String sortFiled = listSortFields.toString();
			if (sortFiled.contains("[")) {
				sortFiled = sortFiled.replace("[", "");
			}
			if (sortFiled.contains("]")) {
				sortFiled = sortFiled.replace("]", "");
			}
			if (UtilValidate.isNotEmpty(sortFiled)) {
				calculatedProductList = PosUtil.sortListMap(calculatedProductList, sortFiled);
			}

			if (end > productAndFacilityList.size()) {
				end = productAndFacilityList.size();
			}
			totalRows = calculatedProductList.size();
			if (totalRows > end){
				calculatedProductList = calculatedProductList.subList(start, end);
			}
			successResult.put("TotalRows", String.valueOf(totalRows));
			successResult.put("listIterator", calculatedProductList);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error: " + e.toString();
			Debug.logError(e, errMsg, module);
		} finally {
			try {
				productAndFacilityIterator.close();
			} catch (GenericDataSourceException e) {
				e.printStackTrace();
			}
		}
		
		return successResult;
	}

	public static String createPurchaseOrder(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Security security = (Security) request.getAttribute("security");
		boolean hasPermission = SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, null, "MODULE", "CALPO_NEW");
		if (!hasPermission){
			String errMsg = UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSYouHavenotCreatePermission", locale);
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
		}
		// set default values for cart
		String supplierPartyId = request.getParameter("supplierPartyId");
		String hasMutilPO = (String) request.getParameter("hasMtilPO");
		String productIds = (String) request.getParameter("productIds");
		String grandTotal = (String) request.getParameter("grandTotal");
		String mainFacility = (String) request.getParameter("mainFacility");
		Timestamp shipByDate = null;
		if(request.getParameter("shipByDate") != null){
			String shipByDateStr = (String) request.getParameter("shipByDate");
			Long shipByDateLong = Long.parseLong(shipByDateStr);
			shipByDate = new Timestamp(shipByDateLong);
		}
		
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator);
		String currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default",
				"VND", delegator);
		boolean beganTx;
		try {
			beganTx = TransactionUtil.begin();
		} catch (GenericTransactionException e1) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateTransaction", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		Map<String, Object> createPlanPOMap = FastMap.newInstance();
		BigDecimal grandTotalPO = new BigDecimal(grandTotal);
		if (UtilValidate.isNotEmpty(grandTotalPO)) {
			createPlanPOMap.put("grandTotal", grandTotalPO);
		}

		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		createPlanPOMap.put("createdDate", nowTimestamp);
		createPlanPOMap.put("createdBy", userLogin.getString("userLoginId"));
		createPlanPOMap.put("supplierId", supplierPartyId);
		if (UtilValidate.isNotEmpty(mainFacility)) {
			createPlanPOMap.put("facilityId", mainFacility);
		}
		createPlanPOMap.put("currencyUom", currencyUomId);
		createPlanPOMap.put("userLogin", userLogin);
		String planPOId = "";
		Map<String, Object> returnPlanPO = FastMap.newInstance();
		try {
			returnPlanPO = dispatcher.runSync("createPlanPO", createPlanPOMap);
			planPOId = (String) returnPlanPO.get("planPOId");
		} catch (GenericServiceException e) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCannotCreatePlanPO", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		List<Map<String, Object>> listProductsSelectedCaculatedPO = FastList.newInstance();
		JSONArray productListJson = JSONArray.fromObject(productIds);
		if (productListJson != null) {
			for (int index = 0; index < productListJson.size(); index++) {
				JSONObject productJson = productListJson.getJSONObject(index);
				if (UtilValidate.isNotEmpty(productJson)) {
					Map<String, Object> createPlanPOItemMap = FastMap.newInstance();
					Map<String, Object> productSelectedPO = FastMap.newInstance();
					createPlanPOItemMap.put("planPOId", planPOId);
					String planPOItemSeqId = String.format("%05d", index + 1);
					createPlanPOItemMap.put("planPOItemSeqId", planPOItemSeqId);
					String productId = (String) productJson.get("productId");
					GenericValue product = null;
					try {
						product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					} catch (GenericEntityException e1) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSNotFoundProduct",
								UtilMisc.toMap("productId", productId), locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					String quantityUomId = product.getString("quantityUomId");
					String purchaseUomId = product.getString("purchaseUomId");
					createPlanPOItemMap.put("productId", productId);
					createPlanPOItemMap.put("productName", productJson.get("productName"));
					String qpdParm = (String) productJson.getString("qpd");
					String qohParm = (String) productJson.getString("qoh");
					String qooParm = (String) productJson.getString("qoo");
					String pickStandardParam = (String) productJson.getString("pickStandard");
					String qtyBoxParam = (String) productJson.getString("qtyBox");
					String qtyPicParam = (String) productJson.getString("qtyPic");
					String unitCostParam = (String) productJson.getString("unitCost");
					BigDecimal qpd = new BigDecimal(qpdParm);
					BigDecimal qoh = new BigDecimal(qohParm);
					BigDecimal qoo = new BigDecimal(qooParm);
					BigDecimal pickStandard = BigDecimal.ZERO;
					if (!pickStandardParam.isEmpty()){
						pickStandard = new BigDecimal(pickStandardParam);
					}
					BigDecimal qtyBox = new BigDecimal(qtyBoxParam);
					BigDecimal qtyPic = new BigDecimal(qtyPicParam);
					BigDecimal unitCost = new BigDecimal(unitCostParam);
					createPlanPOItemMap.put("qpd", qpd);
					createPlanPOItemMap.put("qoh", qoh);
					createPlanPOItemMap.put("qoo", qoo);
					if (productJson.has("lastSold")) {
						Timestamp lastSold = UtilDateTime.getTimestamp(productJson.getString("lastSold"));
						createPlanPOItemMap.put("lastSold", lastSold);
					}
					if (productJson.has("lastReceived")) {
						Timestamp lastReceived = UtilDateTime.getTimestamp(productJson.getString("lastReceived"));
						createPlanPOItemMap.put("lastReceived", lastReceived);
					}
					// get information for purchase_order
					createPlanPOItemMap.put("pickStandard", pickStandard);
					createPlanPOItemMap.put("qtyBox", qtyBox);
					createPlanPOItemMap.put("qtyPic", qtyPic);
					createPlanPOItemMap.put("unitCost", unitCost);
					if (UtilValidate.isNotEmpty(purchaseUomId)) {
						createPlanPOItemMap.put("purchaseUomId", purchaseUomId);
					}
					if (UtilValidate.isNotEmpty(quantityUomId)) {
						createPlanPOItemMap.put("quantityUomId", quantityUomId);
					}
					createPlanPOItemMap.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createPlanPOItem", createPlanPOItemMap);
					} catch (GenericServiceException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCannotCreatePlanPOItem", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					// set
					productSelectedPO.put("productId", productId);
					productSelectedPO.put("unitCost", productJson.get("unitCost").toString());
					productSelectedPO.put("totalPO", productJson.get("totalPO").toString());
					productSelectedPO.put("comments", productJson.get("comments"));
					listProductsSelectedCaculatedPO.add(productSelectedPO);

					Map<String, Object> createPlanPOItemFacility = FastMap.newInstance();
					createPlanPOItemFacility.put("productId", productJson.get("productId"));
					createPlanPOItemFacility.put("planPOId", planPOId);
					createPlanPOItemFacility.put("userLogin", userLogin);
					List<Map<String, Object>> rowDetails = (List<Map<String, Object>>) productJson.get("rowDetail");
					if (UtilValidate.isNotEmpty(rowDetails)) {
						for (Map<String, Object> rowDetail : rowDetails) {
							String facilityId = (String) rowDetail.get("facilityId");
							String facilityName = (String) rowDetail.get("facilityName");
							String qpdDetailTmp = rowDetail.get("qpdDetail").toString();
							String qohDetailTmp = rowDetail.get("qohDetail").toString();
							String poQuantityTmp = rowDetail.get("poQuantity").toString();
							BigDecimal qpdDetail = new BigDecimal(qpdDetailTmp);
							BigDecimal qohDetail = new BigDecimal(qohDetailTmp);
							BigDecimal poQuantity = new BigDecimal(poQuantityTmp);
							createPlanPOItemFacility.put("facilityId", facilityId);
							createPlanPOItemFacility.put("facilityName", facilityName);
							createPlanPOItemFacility.put("qpd", qpdDetail);
							createPlanPOItemFacility.put("qoh", qohDetail);
							createPlanPOItemFacility.put("qpo", poQuantity);

							try {
								dispatcher.runSync("createPlanPOItemFacility", createPlanPOItemFacility);
							} catch (GenericServiceException e) {
								// TODO Auto-generated catch block
								String errorMessage = UtilProperties.getMessage(resource_error,
										"BPOSCannotCreatePlanPOItemFacility", locale);
								request.setAttribute("_ERROR_MESSAGE_", errorMessage);
								return "error";
							}
						}
					}
				}
			}
		}
		// end create plan po item
		List<GenericValue> listFacilitys = FastList.newInstance();
		try {
			listFacilitys = delegator.findList("Facility", null, null, null, null, false);
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSGetFacilityIsErrored", locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		}
		List<String> listFacilityIds = new ArrayList<String>();
		for (GenericValue facility : listFacilitys) {
			String facilityId = facility.getString("facilityId");
			listFacilityIds.add(facilityId);
		}
		Map<String, Object> listFacilityProducts = (Map<String, Object>) prepareCaculatedPO(productListJson,
				listFacilityIds);
		if (UtilValidate.isNotEmpty(hasMutilPO)) {
			if (hasMutilPO.equals("true")) {
				boolean flag = true;
				for (String facilityId : listFacilityIds) {
					List<Map<String, Object>> listProducts = (List<Map<String, Object>>) listFacilityProducts.get(facilityId);
					String createOrder = posCreatedPO(request, response, facilityId, supplierPartyId, company,
							currencyUomId, planPOId, listProducts, shipByDate);
					if (UtilValidate.isNotEmpty(createOrder) && createOrder.equals("purchase_order")) {
						ShoppingCartEvents.destroyCart(request, response);
					} else {
						flag = false;
						break;
					}
				}
				if (flag) {
					destroyCaculatedPO(request, response);
					try {
						TransactionUtil.commit(beganTx);
					} catch (GenericTransactionException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCommitTransaction",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					return "success";
				} else {
					if (UtilValidate.isEmpty(request.getAttribute("_ERROR_MESSAGE_"))){
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					}
					return "error";
				}
			} else {
				String createOrder = posCreatedPO(request, response, mainFacility, supplierPartyId, company,
						currencyUomId, planPOId, listProductsSelectedCaculatedPO, shipByDate);
				if (UtilValidate.isNotEmpty(createOrder) && createOrder.equals("purchase_order")) {
					ShoppingCartEvents.destroyCart(request, response);
					destroyCaculatedPO(request, response);
					try {
						TransactionUtil.commit(beganTx);
					} catch (GenericTransactionException e) {
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCommitTransaction",
								locale);
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
						return "error";
					}
					return "success";
				} else {
					if (UtilValidate.isEmpty(request.getAttribute("_ERROR_MESSAGE_"))){
						String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale); 
						request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					}
					return "error";
				}
			}
		}

		return "success";
	}

	public static Map<String, Object> prepareCaculatedPO(JSONArray listProductCaculatedPO, List<String> facilityIds) {
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(facilityIds)) {
			for (String facilityId : facilityIds) {
				List<Map<String, Object>> valueReturn = FastList.newInstance();
				for (int index = 0; index < listProductCaculatedPO.size(); index++) {
					JSONObject productJson = listProductCaculatedPO.getJSONObject(index);
					List<Map<String, Object>> rowDetails = (List<Map<String, Object>>) productJson.get("rowDetail");
					for (Map<String, Object> rowDetail : rowDetails) {
						String facilityIdRow = (String) rowDetail.get("facilityId");
						if (facilityId.equals(facilityIdRow)) {
							String productId = (String) rowDetail.get("productId");
							Map<String, Object> productPO = FastMap.newInstance();
							productPO.put("productId", productId);
							productPO.put("totalPO", rowDetail.get("poQuantity").toString());
							productPO.put("unitCost", productJson.get("unitCost").toString());
							productPO.put("facilityId", facilityIdRow);
							valueReturn.add(productPO);
						}

					}
				}
				mapReturn.put(facilityId, valueReturn);
			}
		}
		return mapReturn;
	}

	public static String posCreatedPO(HttpServletRequest request, HttpServletResponse response, String facilityId,
			String supplierPartyId, String company, String currencyUomId, String planPOId,
			List<Map<String, Object>> listProductsSelectedCaculatedPO) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Security security = (Security) request.getAttribute("security");
		String orderId = delegator.getNextSeqId("OrderHeader");
		cart.setOrderType("PURCHASE_ORDER");
		cart.setAttribute("originOrderId", "PURCHASE_ORDER");
		cart.setSupplierPartyId(0, supplierPartyId);
		cart.setOrderPartyId(supplierPartyId);
		cart.setChannelType("POS_SALES_CHANNEL");
		cart.setBillToCustomerPartyId(company);
		cart.setShipmentMethodTypeId(0, "STANDARD");
		cart.setCarrierPartyId(0, "_NA_");
		cart.setMaySplit(0, Boolean.FALSE);
		cart.setIsGift(0, Boolean.FALSE);
		cart.setAttribute("addpty", "Y");
		cart.setAttribute("supplierPartyId", supplierPartyId);
		cart.setBillFromVendorPartyId(supplierPartyId);
		cart.setFacilityId(facilityId);
		
		// get productStoreId according to facilityId
		List<GenericValue> productFacility = FastList.newInstance();
		EntityCondition mainCondition = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
		try {
			productFacility = delegator.findList("ProductStoreFacility", mainCondition, null, null, null, false);
			if (UtilValidate.isNotEmpty(productFacility)) {
				GenericValue productStore = productFacility.get(0);
				if (UtilValidate.isNotEmpty(productStore)) {
					String productStoreId = productStore.getString("productStoreId");
					cart.setProductStoreId(productStoreId);
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cart.setOrderId(orderId);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> contextMap = FastMap.newInstance();
		int size = listProductsSelectedCaculatedPO.size();
		ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
		String result = "success";
		String productErrorId = "";
		for (int i = 0; i < size; i++) {
			request.setAttribute("add_product_id", listProductsSelectedCaculatedPO.get(i).get("productId"));
			String unitCost = (String) listProductsSelectedCaculatedPO.get(i).get("unitCost");
			String totalPO = (String) listProductsSelectedCaculatedPO.get(i).get("totalPO");
			String comments = (String) listProductsSelectedCaculatedPO.get(i).get("comments");
			if (UtilValidate.isNotEmpty(unitCost)) {
				request.setAttribute("price", unitCost);
			} else {
				request.setAttribute("price", "0");
			}
			if (UtilValidate.isNotEmpty(totalPO)) {
				request.setAttribute("quantity", totalPO);
			} else {
				request.setAttribute("quantity", "0");
			}

			request.setAttribute("itemComment", comments);

			result = ShoppingCartEvents.addToCart(request, response);
			
			if (result.equals("error")){
				productErrorId = (String) listProductsSelectedCaculatedPO.get(i).get("productId");
				break;
			}
			
			contextMap.put("update_"+(size-i-1), totalPO);
			contextMap.put("price_"+(size-i-1), unitCost);
			contextMap.put("itemType_"+(size-i-1), "PRODUCT_ORDER_ITEM");
		}
		
		if (result.equals("error")){
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSSupplierProductNotFound", 
					UtilMisc.toMap("productId", productErrorId, "supplierId", supplierPartyId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			contextMap.put("finalizeReqAdditionalParty", false);
			contextMap.put("finalizeReqOptions", false);
			contextMap.put("removeSelected", false);
			contextMap.put("finalizeReqPayInfo", false);
			cartHelper.modifyCart(security, userLogin, contextMap, false, null, locale);

			// set shipping
			List<Map<String, Object>> facilityContactMechValueMaps = ContactMechWorker
					.getFacilityContactMechValueMaps(delegator, facilityId, false, null);
			if (UtilValidate.isNotEmpty(facilityContactMechValueMaps)) {
				Map<String, Object> facilityContactMechValueMap = facilityContactMechValueMaps.get(0);
				GenericValue postalAddress = (GenericValue) facilityContactMechValueMap.get("postalAddress");
				if (UtilValidate.isNotEmpty(postalAddress)) {
					String contactMechId = postalAddress.getString("contactMechId");
					if (UtilValidate.isNotEmpty(contactMechId)) {
						cart.setShippingContactMechId(0, contactMechId);
					}
				}
			}

			cartHelper.setCurrency(currencyUomId);
			String createOrder = CheckOutEvents.createOrderForPO(request, response);
			// set planPOId for the order is just created
			try {
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (UtilValidate.isNotEmpty(orderHeader)) {
					orderHeader.set("planPOId", planPOId);
					orderHeader.store();
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block

				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			return createOrder;
		}
		
	}
	
	public static String posCreatedPO(HttpServletRequest request, HttpServletResponse response, String facilityId,
			String supplierPartyId, String company, String currencyUomId, String planPOId,
			List<Map<String, Object>> listProductsSelectedCaculatedPO, Timestamp shipByDate) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String contactMechId = null;
		try {
			List<GenericValue> listCTM = delegator.findList("FacilityCTMAndCTM", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechTypeId", "POSTAL_ADDRESS")), null, null, null, false);
			if(UtilValidate.isNotEmpty(listCTM)){
				contactMechId = listCTM.get(0).getString("contactMechId");
			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Security security = (Security) request.getAttribute("security");
		String orderId = delegator.getNextSeqId("OrderHeader");
		cart.setOrderType("PURCHASE_ORDER");
		cart.setAttribute("originOrderId", "PURCHASE_ORDER");
		cart.setSupplierPartyId(0, supplierPartyId);
		cart.setOrderPartyId(supplierPartyId);
		cart.setChannelType("UNKNWN_SALES_CHANNEL");
		cart.setBillToCustomerPartyId(company);
		cart.setShipmentMethodTypeId(0, "NO_SHIPPING");
		cart.setCarrierPartyId(0, "_NA_");
		cart.setMaySplit(0, Boolean.FALSE);
		cart.setIsGift(0, Boolean.FALSE);
		cart.setAttribute("addpty", "Y");
		cart.setAttribute("supplierPartyId", supplierPartyId);
		cart.setBillFromVendorPartyId(supplierPartyId);
		cart.setFacilityId(facilityId);
		cart.setShipBeforeDate(shipByDate);
		cart.setAllShippingContactMechId(contactMechId);
		
		// get productStoreId according to facilityId
		List<GenericValue> productFacility = FastList.newInstance();
		EntityCondition mainCondition = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
		try {
			productFacility = delegator.findList("ProductStoreFacility", mainCondition, null, null, null, false);
			if (UtilValidate.isNotEmpty(productFacility)) {
				GenericValue productStore = productFacility.get(0);
				if (UtilValidate.isNotEmpty(productStore)) {
					String productStoreId = productStore.getString("productStoreId");
					cart.setProductStoreId(productStoreId);
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cart.setOrderId(orderId);
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> contextMap = FastMap.newInstance();
		int size = listProductsSelectedCaculatedPO.size();
		ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
		String result = "success";
		String productErrorId = "";
		for (int i = 0; i < size; i++) {
			request.setAttribute("add_product_id", listProductsSelectedCaculatedPO.get(i).get("productId"));
			String unitCost = (String) listProductsSelectedCaculatedPO.get(i).get("unitCost");
			String totalPO = (String) listProductsSelectedCaculatedPO.get(i).get("totalPO");
			String comments = (String) listProductsSelectedCaculatedPO.get(i).get("comments");
			if (UtilValidate.isNotEmpty(unitCost)) {
				request.setAttribute("price", unitCost);
			} else {
				request.setAttribute("price", "0");
			}
			if (UtilValidate.isNotEmpty(totalPO)) {
				request.setAttribute("quantity", totalPO);
			} else {
				request.setAttribute("quantity", "0");
			}

			request.setAttribute("itemComment", comments);

			result = ShoppingCartEvents.addToCart(request, response);
			
			if (result.equals("error")){
				productErrorId = (String) listProductsSelectedCaculatedPO.get(i).get("productId");
				break;
			}
			
			contextMap.put("update_"+(size-i-1), totalPO);
			contextMap.put("price_"+(size-i-1), unitCost);
			contextMap.put("itemType_"+(size-i-1), "PRODUCT_ORDER_ITEM");
		}
		
		if (result.equals("error")){
			String errorMessage = UtilProperties.getMessage(resource_error, "BPOSSupplierProductNotFound", 
					UtilMisc.toMap("productId", productErrorId, "supplierId", supplierPartyId), locale);
			request.setAttribute("_ERROR_MESSAGE_", errorMessage);
			return "error";
		} else {
			contextMap.put("finalizeReqAdditionalParty", false);
			contextMap.put("finalizeReqOptions", false);
			contextMap.put("removeSelected", false);
			contextMap.put("finalizeReqPayInfo", false);
			cartHelper.modifyCart(security, userLogin, contextMap, false, null, locale);

			// set shipping
			List<Map<String, Object>> facilityContactMechValueMaps = ContactMechWorker
					.getFacilityContactMechValueMaps(delegator, facilityId, false, null);
			if (UtilValidate.isNotEmpty(facilityContactMechValueMaps)) {
				Map<String, Object> facilityContactMechValueMap = facilityContactMechValueMaps.get(0);
				GenericValue postalAddress = (GenericValue) facilityContactMechValueMap.get("postalAddress");
				if (UtilValidate.isNotEmpty(postalAddress)) {
					if (UtilValidate.isNotEmpty(contactMechId)) {
						cart.setShippingContactMechId(0, contactMechId);
					}
				}
			}
			
			cartHelper.setCurrency(currencyUomId);
			String createOrder = CheckOutEvents.createOrderForPO(request, response);
			// set planPOId for the order is just created
			try {
				GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				if (UtilValidate.isNotEmpty(orderHeader)) {
					orderHeader.set("planPOId", planPOId);
					orderHeader.store();
				} else {
					String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
					request.setAttribute("_ERROR_MESSAGE_", errorMessage);
					return "error";
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				String errorMessage = UtilProperties.getMessage(resource_error, "BPOSCanNotCreateOrder", locale);
				request.setAttribute("_ERROR_MESSAGE_", errorMessage);
				return "error";
			}
			return createOrder;
		}
	}

	public static String destroyCaculatedPO(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		List<Map<String, Object>> listProductPO = (List<Map<String, Object>>) session.getAttribute("listProductPO");
		if (UtilValidate.isNotEmpty(listProductPO)) {
			for (Map<String, Object> productPO : listProductPO) {
				List<Map<String, Object>> rowDetails = (List<Map<String, Object>>) productPO.get("rowDetail");
				if (UtilValidate.isNotEmpty(rowDetails)) {
					for (Map<String, Object> rowDetail : rowDetails) {
						String productId = (String) rowDetail.get("productId");
						String facilityId = (String) rowDetail.get("facilityId");
						Map<String, Object> sessionProductFacility = (Map<String, Object>) session.getAttribute(productId + facilityId + "producDetailPO");
						if (UtilValidate.isNotEmpty(sessionProductFacility)) {
							session.removeAttribute(productId + facilityId + "producDetailPO");
						}
					}
				}
				String sessionProduct = (String) productPO.get("productId") + "productCaculated";
				if (UtilValidate.isNotEmpty(sessionProduct)) {
					session.removeAttribute(sessionProduct);
				}
			}
		}
		session.removeAttribute("listProductPO");
		return "success";
	}
}
