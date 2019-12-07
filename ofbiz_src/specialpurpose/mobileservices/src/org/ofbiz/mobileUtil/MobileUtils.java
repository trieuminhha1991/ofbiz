package org.ofbiz.mobileUtil;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.SecurityUtil;
import com.olbius.product.catalog.NewCatalogWorker;
import com.olbius.product.product.ProductUtils;

public class MobileUtils {
	public static final String resource = "BaseSalesErrorUiLabels";
	public static Map<String, Object> checkProductStoreActive(Delegator delegator , Locale locale, String productStoreId){
		if (UtilValidate.isNotEmpty(productStoreId)){
			try {
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				if (UtilValidate.isNotEmpty(productStore)){
					if (productStore.get("statusId").equals("PRODSTORE_ENABLED")){
						return ServiceUtil.returnSuccess();
					}
				} 
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorCheckProductStoreActive", locale));
			}
		}
		return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSProductStoreDeactive", locale));
	}
	
	public static Map<String, Object> checkCustomerActive(Delegator delegator , Locale locale, String partyId){
		if (UtilValidate.isNotEmpty(partyId)){
			try {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(party)){
					if (party.get("statusId").equals("PARTY_ENABLED")){
						return ServiceUtil.returnSuccess();
					}
				} 
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSErrorCheckCustomerActive", locale));
			}
		}
		return ServiceUtil.returnError(UtilProperties.getMessage(resource, "BSCustomerDeactive", locale));
	}
	public static List<String> getAllProductCategory(HttpServletRequest request, Delegator delegator, String productStoreId) throws GenericEntityException, GenericServiceException{
		List<String> res = FastList.newInstance();
		List<Map<String, Object>> categories = buildCategoryTree(request, delegator, productStoreId);
		recurseGetProductCategory(categories, res);
		return res;
	}
	public static List<Map<String, Object>> getAllProducts(HttpServletRequest request, String productStoreId) throws GenericEntityException{
		List<String> categoryList = null;
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			categoryList = MobileUtils.getAllProductCategory(request, delegator, productStoreId);
		} catch (GenericServiceException e1) {
			// TODO Auto-generated catch block
			Debug.log(e1.getMessage());
		}
		List<Map<String, Object>> resultList = FastList.newInstance();
		if(categoryList == null) {
			return resultList;
		}
		String currency = request.getParameter("currencyUomId");
		if (currency == null || currency.isEmpty()) {
			currency = UtilProperties.getPropertyValue("general",
					"currency.uom.id.default");
		}
		EntityCondition ec = EntityCondition.makeCondition(
				"productCategoryId", EntityOperator.IN, categoryList);
		EntityCondition cr = EntityCondition.makeCondition(
				"priceCurrencyUomId", EntityOperator.EQUALS, currency);
		List<EntityCondition> allConditions = FastList.newInstance();
		allConditions.add(ec);
		allConditions.add(cr);
		allConditions.add(EntityCondition.makeCondition(
				"priceProductPriceTypeId", EntityOperator.EQUALS,
				"LIST_PRICE"));
		EntityCondition queryConditionsList = EntityCondition
				.makeCondition(allConditions, EntityOperator.AND);
		EntityListIterator iterator = null;
		List<GenericValue> listProduct;
		try {
			LocalDispatcher dispatcher = (LocalDispatcher) request
					.getAttribute("dispatcher");
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap.put("inputParams", queryConditionsList);
			Map<String, Object> tmpMap2 = dispatcher.runSync("TempGetAllProduct", tmpMap);
			iterator = (EntityListIterator) tmpMap2.get("outputParams");
			listProduct = iterator.getCompleteList();
			for (GenericValue product : listProduct) {
				Map<String, Object> res = FastMap.newInstance();
				res.put("productId", product.get("productId"));
				res.put("productName", product.get("productProductName"));
				res.put("uom", product.get("priceCurrencyUomId"));
				res.put("unitPrice", product.get("pricePrice"));
				res.put("productCategoryId", product.get("productCategoryId"));
				resultList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(iterator!= null){
				iterator.close();
			}
		}
		return resultList;
	}
	public static List<Map<String, Object>> buildCategoryTree(HttpServletRequest request, Delegator delegator, String productStoreId) throws GenericEntityException{
		List<GenericValue> main = NewCatalogWorker.getStoreCatalogs(delegator, productStoreId);
		List<Map<String, Object>> cataCatList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(main)){
			String mainCatalog = main.get(0).getString("prodCatalogId");
			request.setAttribute("CURRENT_CATALOG_ID", mainCatalog);
			List<GenericValue> allCategory = ProductUtils.getCatalogTopCategories(request, mainCatalog);
			for(GenericValue e : allCategory){
				Map<String, Object> tmpCata = FastMap.newInstance();
				String curCategoryId = e.getString("productCategoryId");
				GenericValue category = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", curCategoryId), true);
				tmpCata.put("category", category);
				if(curCategoryId == null || curCategoryId.isEmpty()){
					continue;
				}
				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
				conditions.add(EntityCondition.makeCondition("parentProductCategoryId", curCategoryId));
				List<GenericValue> categoryList = delegator.findList("ProductCategoryRollupAndChild", EntityCondition.makeCondition(conditions), null, UtilMisc.toList("sequenceNum"), null, true);
				if (UtilValidate.isNotEmpty(categoryList)) {
					List<Map<String, Object>> completedTree = fillTree(categoryList, 1, "", delegator, request);
					tmpCata.put("parentProductCategoryId", curCategoryId);
					tmpCata.put("child", completedTree);
					cataCatList.add(tmpCata);
				}
			}
		}
		return cataCatList;
	}
	public static List<Map<String, Object>> fillTree(List<GenericValue> rootCat, int level,
			String parentCategoryId, Delegator delegator,
			HttpServletRequest request) {
		if (rootCat != null && level < 4) {
			List<Map<String, Object>> listTree = FastList.newInstance();
			for (GenericValue root : rootCat) {
				try {
					List<GenericValue> preCatChilds = delegator
							.findByAnd("ProductCategoryRollup", UtilMisc.toMap(
									"parentProductCategoryId",
									root.get("productCategoryId")), null, false);
					List<GenericValue> catChilds = EntityUtil
							.getRelated("CurrentProductCategory", null,
									preCatChilds, false);
					List<Map<String, Object>> childList = FastList.newInstance();
					if (catChilds != null) {
						if (level == 2)
							childList = fillTree(catChilds, level + 1,
									parentCategoryId.replaceAll("/", "") + '/'
											+ root.get("productCategoryId"),
									delegator, request);
						// replaceAll and '/' uses for fix bug in the breadcrum
						// for href of category
						else if (level == 1)
							childList = fillTree(
									catChilds,
									level + 1,
									parentCategoryId.replaceAll("/", "")
											+ root.get("productCategoryId"),
									delegator, request);
						else
							childList = fillTree(
									catChilds,
									level + 1,
									parentCategoryId + '/'
											+ root.get("productCategoryId"),
									delegator, request);
					}
					List<GenericValue> productsInCat = delegator
							.findByAnd(
									"ProductCategoryAndMember",
									UtilMisc.toMap("productCategoryId",
											root.get("productCategoryId")),
									null, false);
					if (productsInCat != null || childList != null) {
						Map<String, Object> rootMap = FastMap.newInstance();
						GenericValue category = delegator.findOne(
								"ProductCategory",
								UtilMisc.toMap("productCategoryId",
										root.get("productCategoryId")), false);
						rootMap.put("category", category);
						rootMap.put("productCategoryId",
								root.getString("productCategoryId"));
						rootMap.put("parentCategoryId", parentCategoryId);
						rootMap.put("child", childList);
						listTree.add(rootMap);
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return listTree;
		}
		return null;
	}
	public static void recurseGetProductCategory(List<Map<String, Object>> categories, List<String> res){
		for(Map<String, Object> category : categories){
			List<Map<String, Object>> child = (List<Map<String, Object>>) category.get("child");
			GenericValue e = (GenericValue) category.get("category");
			String productCategoryId = e.getString("productCategoryId");
			res.add(productCategoryId);
			if(child != null){
				recurseGetProductCategory(child, res);
			}
		}
	}
	
	public static String getAvatar(Delegator delegator, String partyId) throws GenericEntityException{
			String partyContent = null;
			List<GenericValue> partyContentList = delegator.findList(
					"PartyContent", EntityCondition.makeCondition("partyId",
							EntityOperator.EQUALS, partyId), null,
					UtilMisc.toList("fromDate DESC"), null, false);
			if(partyContentList.size() > 0){
				String contentId = (String) partyContentList.get(0).get("contentId");
				if (contentId != null) {

				}
			}
			
			return partyContent;
	}
	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(Map<K, V> map, int n) {
		Comparator<? super Entry<K, V>> comparator = new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e0, Entry<K, V> e1) {
				V v0 = e0.getValue();
				V v1 = e1.getValue();
				return v0.compareTo(v1);
			}
		};
		PriorityQueue<Entry<K, V>> highest = new PriorityQueue<Entry<K, V>>(n, comparator);
		for (Entry<K, V> entry : map.entrySet()) {
			highest.offer(entry);
			while (highest.size() > n) {
				highest.poll();
			}
		}

		List<Entry<K, V>> result = new ArrayList<Map.Entry<K, V>>();
		while (highest.size() > 0) {
			result.add(highest.poll());
		}
		return result;
	}
	public static List<String> getRoadToday(Delegator delegator, String partyId) throws GenericEntityException{
        return getRoadTodayWithPartyId(delegator,partyId);
	}
	public static List<String> getRoadTodayWithPartyId(Delegator delegator, String partyId) throws GenericEntityException{
		List<EntityCondition> listCond = FastList.newInstance();
		/*listCond.add(EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS,partyId));
		listCond.add(EntityUtil.getFilterByDateExpr("SRFromDate", "SRThruDate"));
		listCond.add(EntityUtil.getFilterByDateExpr("SCFromDate", "SCThruDate"));
		String today = getTodayString();
		listCond.add(EntityCondition.makeCondition("scheduleRoute", today));
		List<GenericValue> listRel = null;
		List<String> listStores = FastList.newInstance();
		try {
			listRel = delegator.findList("PartyRelationshipRouteDetail",
					EntityCondition.makeCondition(listCond,EntityOperator.AND), null, null, null, false);
			
			if(UtilValidate.isNotEmpty(listRel)){
				for(GenericValue e : listRel){
					if(!listStores.contains(e.getString("routeId"))){
						listStores.add(e.getString("routeId"));
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
			throw e;
		}*/

		List<String> routeIdsResult = FastList.newInstance();
        String today = getTodayString();
        listCond.add(EntityCondition.makeCondition("executorId",partyId));
        listCond.add(EntityCondition.makeCondition("statusId","ROUTE_ENABLED")); //need change to SupUtil.ROUTE_ENABLED
        List<String> routeIds = EntityUtil.getFieldListFromEntityList(delegator.findList("Route", EntityCondition.makeCondition(listCond),UtilMisc.toSet("routeId"),
                null, null,false),"routeId",true);

        listCond.clear();
        listCond.add(EntityCondition.makeCondition("scheduleRoute",today));
        listCond.add(EntityUtil.getFilterByDateExpr());
        listCond.add(EntityCondition.makeCondition("routeId",EntityOperator.IN,routeIds));
        routeIdsResult = EntityUtil.getFieldListFromEntityList(delegator.findList("SalesRouteSchedule", EntityCondition.makeCondition(listCond),UtilMisc.toSet("routeId"),
                null, null,false),"routeId",true);
		return routeIdsResult;
	}
    public static Date genTodayDate() {
        Date rs = new Date(System.currentTimeMillis());
        return rs;
    }
	public static String getTodayString(){
		Calendar cal = Calendar.getInstance();
		int num = cal.get(Calendar.DAY_OF_WEEK);
		String day = null;
		switch(num){
			case 1:
				day = "SUNDAY";
				break;
			case 2:
				day = "MONDAY";
				break;
			case 3:
				day = "TUESDAY";
				break;
			case 4:
				day = "WEDNESDAY";
				break;
			case 5:
				day = "THURSDAY";
				break;
			case 6:
				day = "FRIDAY";
				break;
			case 7:
				day = "SATURDAY";
				break;
		}
		return day;
	}
	public static String updateGeoPoint(Delegator delegator, String geoPointId, Double latitude, Double longitude)
			throws GenericEntityException{
		GenericValue geo = delegator.findOne("GeoPoint", UtilMisc.toMap("geoPointId",geoPointId), false);
		geo.set("latitude", latitude);
		geo.set("longitude", longitude);
		geo.store();
		return geoPointId;
	}
	public static String createGeoPoint(Delegator delegator, Double latitude, Double longitude)
			throws GenericEntityException{
		GenericValue geo = delegator.makeValue("GeoPoint");
		String geoPointId = delegator.getNextSeqId("GeoPoint");
		geo.set("geoPointId", geoPointId);
		geo.set("latitude", latitude);
		geo.set("longitude", longitude);
		geo.create();
		return geoPointId;
	}
	public static void createOrderGeoPoint(Delegator delegator, String orderId, String geoPointId) throws GenericEntityException{
		GenericValue GeoPointOrderSM  = delegator.makeValue("OrderAndGeoPoint");
		GeoPointOrderSM.set("geoPointId", geoPointId);
		GeoPointOrderSM.set("orderId", orderId);
		GeoPointOrderSM.create();
	}
	public static Map<String, Object> updatePostalAddress(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId,
										String countryGeoId, String stateProvinceGeoId, String city, String districtGeoId, String geoPointId, String address) throws GenericEntityException, GenericServiceException{
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",  "system"), false);
		Map<String, Object> fk = FastMap.newInstance();
		fk.put("userLogin", userLogin);
		fk.put("contactMechTypeId", "POSTAL_ADDRESS");
		fk.put("partyId", partyId);
		fk.put("contactMechId", contactMechId);
		fk.put("countryGeoId", countryGeoId);
		fk.put("stateProvinceGeoId", stateProvinceGeoId);
		fk.put("districtGeoId", districtGeoId);
		fk.put("geoPointId", geoPointId);
		fk.put("address1", address);
		fk.put("city", city);
		fk.put("postalCode", "100000");
		return dispatcher.runSync("updatePartyPostalAddress", fk);
	}
	public static String createPartyPostalAddress(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId,
			String countryGeoId, String stateProvinceGeoId, String city, String districtGeoId, String geoPointId, String address, String roleTypeId, boolean isThruDateOld) throws GenericEntityException, GenericServiceException{
		return createPartyPostalAddress(delegator, dispatcher, partyId, contactMechId, countryGeoId, stateProvinceGeoId, city, districtGeoId, geoPointId, address, roleTypeId, true, isThruDateOld);
	}
	public static String createPartyPostalAddress(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId,
			String countryGeoId, String stateProvinceGeoId, String city, String districtGeoId, String geoPointId, String address, boolean isThruDateOld) throws GenericEntityException, GenericServiceException{
		return createPartyPostalAddress(delegator, dispatcher, partyId, contactMechId, countryGeoId, stateProvinceGeoId, city, districtGeoId, geoPointId, address, "SHIP_TO_CUSTOMER", true, isThruDateOld);
	}
	public static String createPartyPostalAddress(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId,
			String countryGeoId, String stateProvinceGeoId, String city, String districtGeoId, String geoPointId, String address, String roleTypeId, boolean isShipping, boolean isThruDateOld) throws GenericEntityException, GenericServiceException{
		String tmp = createPostalAddress(delegator, dispatcher, partyId, contactMechId, address, countryGeoId, stateProvinceGeoId, city, districtGeoId, geoPointId, address);
		if(UtilValidate.isEmpty(contactMechId)){
			contactMechId = tmp;
		}
		createPartyContactMechPurpose(delegator, dispatcher, partyId, contactMechId, "PRIMARY_LOCATION", isThruDateOld);
		if(UtilValidate.isNotEmpty(roleTypeId)){
			createPartyContactMech(delegator, dispatcher, partyId, contactMechId, "POSTAL_ADDRESS", roleTypeId);
		}
		if(isShipping){
			createPartyContactMechPurpose(delegator, dispatcher, partyId, contactMechId, "SHIPPING_LOCATION", isThruDateOld);
		}
		return contactMechId;
	}
	public static String createPostalAddress(Delegator delegator, LocalDispatcher dispatcher,
			String partyId, String contactMechId, String toName, String country, String stateProvinceGeoId, String city, String district, String geoPointId, String address1)
					throws GenericEntityException, GenericServiceException{
		if(UtilValidate.isNotEmpty(address1)){
			address1 = address1.trim();
		}
		GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> party = FastMap.newInstance();
		party.put("userLogin", partyUserLogin);
		party.put("address1", address1);
		party.put("contactMechId", contactMechId);
		party.put("toName", toName);
		party.put("stateProvinceGeoId", stateProvinceGeoId);
		party.put("city", city);
		party.put("countryGeoId", country);
		party.put("districtGeoId", district);
		party.put("geoPointId", geoPointId);
		party.put("postalCode", "70000");
		Map<String, Object> res = dispatcher.runSync("createPostalAddress", party);
		return (String) res.get("contactMechId");
	}
	public static Map<String, Object> createPartyContactMech(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId, String contactMechTypeId, String roleTypeId) throws GenericServiceException, GenericEntityException{
		GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> fk = FastMap.newInstance();
		fk.put("partyId", partyId);
		fk.put("contactMechId", contactMechId);
		fk.put("roleTypeId", roleTypeId);
		fk.put("contactMechTypeId", contactMechTypeId);
		fk.put("userLogin", partyUserLogin);
		List<GenericValue> old = delegator.findList("PartyContactMech", EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyId", partyId),
					EntityCondition.makeCondition("roleTypeId", roleTypeId)
				)), null, null, null, false);
		for(GenericValue e : old){
			e.set("thruDate", UtilDateTime.nowTimestamp());
			e.store();
		}
		return dispatcher.runSync("createPartyContactMech", fk);
	}
	public static Map<String, Object> createPartyContactMechPurpose(Delegator delegator, LocalDispatcher dispatcher, String partyId, String contactMechId, String contactMechPurposeTypeId, boolean isThruDateOld) throws GenericServiceException, GenericEntityException{
		GenericValue partyUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
		Map<String, Object> fk = FastMap.newInstance();
		fk = FastMap.newInstance();
		fk.put("userLogin", partyUserLogin);
		fk.put("contactMechId", contactMechId);
		fk.put("partyId", partyId);
		fk.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
		if(isThruDateOld){
			List<GenericValue> old = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyId", partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", contactMechPurposeTypeId)
				)), null, null, null, false);
			for(GenericValue e : old){
				e.set("thruDate", UtilDateTime.nowTimestamp());
				e.store();
			}
		}
		return dispatcher.runSync("createPartyContactMechPurpose", fk);
	}
	public static String getGeoId(Delegator delegator, String geoName) throws GenericEntityException{
		List<GenericValue> list = delegator.findList("Geo", EntityCondition.makeCondition("geoName", geoName), null, UtilMisc.toList("geoId"), null, false);
		GenericValue e = null;
		if(UtilValidate.isNotEmpty(list)){
			e = list.get(0);
			return e.getString("geoId");
		}else {
			list = delegator.findList("Geo", EntityCondition.makeCondition("geoId", EntityOperator.LIKE, geoName + "%"), null, UtilMisc.toList("geoId"), null, false);
			if(UtilValidate.isNotEmpty(list)){
				e = list.get(0);
				return e.getString("geoId");
			}
		}
		return null;
	}
	public static String getGeoId(Delegator delegator, String geoName, String geoTypeId) throws GenericEntityException{
	    List<EntityCondition> conds = FastList.newInstance();
	    conds.add(EntityCondition.makeCondition("geoName", geoName));
	    conds.add(EntityCondition.makeCondition("geoTypeId", geoTypeId));
		List<GenericValue> list = delegator.findList("Geo", EntityCondition.makeCondition(conds), null, UtilMisc.toList("geoId"), null, false);
		GenericValue e = null;
		if(UtilValidate.isNotEmpty(list)){
			e = list.get(0);
			return e.getString("geoId");
		}else {
		    conds.clear();
		    conds.add(EntityCondition.makeCondition("geoName", EntityOperator.LIKE, geoName + "%"));
            conds.add(EntityCondition.makeCondition("geoTypeId", geoTypeId));
			list = delegator.findList("Geo", EntityCondition.makeCondition(conds), null, UtilMisc.toList("geoId"), null, false);
			if(UtilValidate.isNotEmpty(list)){
				e = list.get(0);
				return e.getString("geoId");
			}
		}
		return null;
	}
	/* return list object with specify fields of object */
	public static List<Map<String, Object>> getListObjectWithFields(
			List<GenericValue> input, String[] fields) {
		ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		String temp = "";
		for (int x = 0; x < input.size(); x++) {
			Map<String, Object> obj = FastMap.newInstance();
			GenericValue objtemp = input.get(x);
			for (int i = 0; i < fields.length; i++) {
				temp = fields[i];
				obj.put(fields[i], objtemp.get(temp));
			}
			res.add(obj);
		}
		return res;
	}
	/* get specify fields in an generic object */
	public static Map<String, Object> getFieldsObject(GenericValue input,
			String[] fields) {
		Map<String, Object> res = FastMap.newInstance();
		String temp = "";
		for (int i = 0; i < fields.length; i++) {
			temp = fields[i];
			res.put(fields[i], input.get(temp));
		}
		return res;
	}
	public static void sendNotifyManager(DispatchContext dpc, Map<String, Object> context, String msg, String target, String param){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String header = UtilProperties.getMessage("BaseSalesMtlUiLabels", msg, locale);
		Map<String, Object> in = FastMap.newInstance();
		in.put("userLogin", userLogin);
		in.put("partyId", userLogin.getString("partyId"));
		try{
			Map<String, Object> out = dispatcher.runSync("getSupManager", in);
			String partyIdTo = (String) out.get("partyIdTo");
			dispatcher.runSync("createNotification",
					UtilMisc.toMap("partyId", partyIdTo, "targetLink", param, "sendToSender", "Y",
							"action", target, "header", header, "ntfType", "ONE", "userLogin", userLogin));
		}catch(Exception e){
			Debug.log(e.getMessage());
		}
	}
	public static void sendMarketingManagerNotify(DispatchContext dpc, Map<String, Object> context, String msg, String target, String param){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String header = UtilProperties.getMessage("CustomMarketingUiLabels", msg, locale);
		Delegator delegator = dpc.getDelegator();
		try{
			List<String> employees = SecurityUtil.getPartiesByRoles("MARKETING_EMPLOYEE", delegator);
			for(String e : employees){
				dispatcher.runSync("createNotification", UtilMisc.toMap("partyId", e, "targetLink", param, "sendToSender", "Y",
								"action", target, "header", header, "ntfType", "ONE", "userLogin", userLogin));
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
		}
	}
}
