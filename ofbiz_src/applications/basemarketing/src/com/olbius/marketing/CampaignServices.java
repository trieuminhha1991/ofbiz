package com.olbius.marketing;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;


public class CampaignServices {

	public static final String module = CampaignServices.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMarketingCampaigns(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("marketingCampaignId ASC");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("marketingTypeId", EntityOperator.NOT_EQUAL, "CALLCAMPAIGN"));
			EntityCondition tmpCond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
			EntityListIterator listIterator = delegator.find("MarketingCampaignDetail", tmpCond, null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String, Object> createFullMarketingCampaign(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			String isActive = (String) context.get("isActive");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			ModelService model = ctx.getModelService("createMarketingCampaign");
	        Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			String pr = (String) context.get("products");
			String ct = (String) context.get("costs");
			String pl = (String) context.get("places");
			String rl = (String) context.get("roles");
			String isDone = (String) context.get("isDone");
			if(isDone == "Y") {
				inputMap.put("statusId", "MKTG_CAMP_COMPLETED");
			}else{
				if(isActive == "Y") {
					inputMap.put("statusId", "MKTG_CAMP_INPROGRESS");
				}else{
					inputMap.put("statusId", "MKTG_CAMP_PLANNED");
				}
			}
			Map<String, Object> marketing = dispatcher.runSync("createMarketingCampaign", inputMap);
			String marketingCampaignId = (String) marketing.get("marketingCampaignId");
			result.put("marketingCampaignId", marketingCampaignId);
			List<String> mkpl = FastList.newInstance();
			if(!UtilValidate.isEmpty(pl)) {
				JSONArray places = JSONArray.fromObject(pl);
				for(int i = 0; i< places.size(); i++) {
					JSONObject o = places.getJSONObject(i);
					Map<String, Object> inp = FastMap.newInstance();
					processDataPlace(o, marketingCampaignId, userLogin, inp);
					Map<String, Object> out = dispatcher.runSync("createMarketingPlace", inp);
					String mrkplid = (String) out.get("marketingPlaceId");
					mkpl.add(mrkplid);
				}
			}
			if(!UtilValidate.isEmpty(pr)) {
				JSONArray products = JSONArray.fromObject(pr);
				for(int i = 0; i< products.size(); i++) {
					JSONObject o = products.getJSONObject(i);
					Map<String, Object> inp = FastMap.newInstance();
					if(!o.has("productId")) {
						continue;
					}
					processDataProduct(o, marketingCampaignId, userLogin, inp, mkpl, i);
					dispatcher.runSync("createMarketingProduct", inp);
				}
			}
			if(!UtilValidate.isEmpty(ct)) {
				JSONArray costs = JSONArray.fromObject(ct);
				for(int i = 0; i< costs.size(); i++) {
					JSONObject o = costs.getJSONObject(i);
					if(!o.has("marketingCostTypeId")) {
						continue;
					}
					Map<String, Object> inp = FastMap.newInstance();
					processDataCost(o, marketingCampaignId, userLogin, inp, mkpl, i);
					dispatcher.runSync("createMarketingCost", inp);
				}
			}
			if(!UtilValidate.isEmpty(rl)) {
				JSONArray roles = JSONArray.fromObject(rl);
				List<String> partiesList = FastList.newInstance();
				for(int i = 0; i< roles.size(); i++) {
					JSONObject o = roles.getJSONObject(i);
					Map<String, Object> inp = FastMap.newInstance();
					inp.put("userLogin", userLogin);
					inp.put("marketingCampaignId", marketingCampaignId);
					if(!o.has("partyId") || !o.has("roleTypeId")) {
						continue;
					}
					processDataRole(o, marketingCampaignId, userLogin, inp, mkpl, i);
					if (UtilValidate.isNotEmpty(inp.get("partyId"))) {
						partiesList.add((String) inp.get("partyId"));
					}
					dispatcher.runSync("createMarketingRole", inp);
				}
				if (UtilValidate.isNotEmpty(partiesList) && "Y".equals(isActive)) {
					dispatcher.runSync("createNotification", UtilMisc.toMap("header", UtilProperties.getMessage("CustomMarketingUiLabels", "ThereAreNewCampaignAreCreated", locale), "action", "ListCampaignMarketing",
							"partiesList", partiesList, "userLogin", userLogin));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}
	public static Map<String, Object> updateMarketingCampaignAndItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		try {
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String isActive = (String) context.get("isActive");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			ModelService model = ctx.getModelService("updateMarketingCampaign");
	        Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			String pr = (String) context.get("products");
			String ct = (String) context.get("costs");
			String pl = (String) context.get("places");
			String rl = (String) context.get("roles");
			String isDone = (String) context.get("isDone");
			if(isDone == "Y") {
				inputMap.put("statusId", "MKTG_CAMP_COMPLETED");
			}else{
				if(isActive == "Y") {
					inputMap.put("statusId", "MKTG_CAMP_INPROGRESS");
				}else{
					inputMap.put("statusId", "MKTG_CAMP_PLANNED");
				}
			}
			dispatcher.runSync("updateMarketingCampaign", inputMap);
			List<String> mkpl = FastList.newInstance();
			List<GenericValue> oldPlace = delegator.findList("MarketingPlace",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
					UtilMisc.toSet("marketingCampaignId", "marketingPlaceId"), UtilMisc.toList("marketingPlaceId ASC"), null, false);
			if(!UtilValidate.isEmpty(pl)) {
				updateListPlace(dispatcher,userLogin,pl, marketingCampaignId, oldPlace, mkpl);
			}else if(!UtilValidate.isEmpty(oldPlace)) {
				removeListItem(dispatcher, userLogin, oldPlace, "Place", marketingCampaignId);
			}
			List<GenericValue> oldPro = delegator.findList("MarketingProduct",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
					UtilMisc.toSet("marketingCampaignId", "marketingProductId"), UtilMisc.toList("marketingProductId ASC"), null, false);
			if(!UtilValidate.isEmpty(pr)) {
				updateListProduct(dispatcher, userLogin, pr, marketingCampaignId, oldPro, mkpl);
			}else if(!UtilValidate.isEmpty(oldPro)) {
				removeListItem(dispatcher, userLogin, oldPro, "Product", marketingCampaignId);
			}
			List<GenericValue> oldCt = delegator.findList("MarketingCost",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
					UtilMisc.toSet("marketingCampaignId", "marketingCostId"), UtilMisc.toList("marketingCostId ASC"), null, false);
			if(!UtilValidate.isEmpty(ct)) {
				updateListCost(dispatcher, userLogin, ct, marketingCampaignId, oldCt, mkpl);
			}else if(!UtilValidate.isEmpty(oldCt)) {
				removeListItem(dispatcher, userLogin, oldCt, "Cost", marketingCampaignId);
			}
			List<GenericValue> oldRole = delegator.findList("MarketingRole",
					EntityCondition.makeCondition("marketingCampaignId", marketingCampaignId),
					UtilMisc.toSet("marketingCampaignId", "marketingRoleId"), UtilMisc.toList("marketingRoleId ASC"), null, false);
			if(!UtilValidate.isEmpty(rl)) {
				updateListRole(dispatcher, userLogin, locale, rl, marketingCampaignId, oldRole, mkpl, isActive);
			}else {
				if(!UtilValidate.isEmpty(oldRole)) {
					removeListItem(dispatcher, userLogin, oldRole, "Role", marketingCampaignId);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}
	private static void updateListPlace(LocalDispatcher dispatcher, GenericValue userLogin, String pl, String marketingCampaignId, List<GenericValue> oldPlace, List<String> mkpl)
					throws GenericServiceException{
		JSONArray places = JSONArray.fromObject(pl);
		for(int i = 0; i< places.size(); i++) {
			JSONObject o = places.getJSONObject(i);
			Map<String, Object> inp = FastMap.newInstance();
			processDataPlace(o, marketingCampaignId, userLogin, inp);
			if(o.has("marketingPlaceId")) {
				//	upate if has
				String marketingPlaceId = o.getString("marketingPlaceId");
				inp.put("marketingPlaceId", marketingPlaceId);
				checkListHasItem(oldPlace, "marketingPlaceId", marketingPlaceId);
				dispatcher.runSync("updateMarketingPlace", inp);
				mkpl.add(marketingPlaceId);
			}else{
				//	insert if not has
				Map<String, Object> ou = dispatcher.runSync("createMarketingPlace", inp);
				String marketingPlaceId = (String) ou.get("marketingPlaceId");
				mkpl.add(marketingPlaceId);
			}
		}
		if(!UtilValidate.isEmpty(oldPlace)) {
			removeListItem(dispatcher, userLogin, oldPlace, "Place", marketingCampaignId);
		}
	}
	private static void processDataPlace(JSONObject o, String marketingCampaignId, GenericValue userLogin, Map<String, Object> inp) {
		inp.put("userLogin", userLogin);
		inp.put("marketingCampaignId", marketingCampaignId);
		if(o.has("organizationId")) {
			inp.put("organizationId", o.get("organizationId"));
		}
		if(o.has("geoId")) {
			inp.put("geoId", o.get("geoId"));
		}
		if(o.has("contactMechId")) {
			inp.put("contactMechId", o.get("contactMechId"));
		}
	}
	private static void updateListProduct(LocalDispatcher dispatcher, GenericValue userLogin, String pr, String marketingCampaignId, List<GenericValue> oldPro, List<String> mkpl)
			throws GenericServiceException {
		JSONArray products = JSONArray.fromObject(pr);
		for(int i = 0; i< products.size(); i++) {
			JSONObject o = products.getJSONObject(i);
			if(!o.has("productId")) {
				continue;
			}
			Map<String, Object> inp = FastMap.newInstance();
			processDataProduct(o, marketingCampaignId, userLogin, inp, mkpl, i);
			if(o.has("marketingProductId")) {
				String marketingProductId = o.getString("marketingProductId");
				checkListHasItem(oldPro, "marketingProductId", marketingProductId);
				inp.put("marketingProductId", marketingProductId);
				dispatcher.runSync("updateMarketingProduct", inp);
			}else{
				dispatcher.runSync("createMarketingProduct", inp);
			}
		}
		if(!UtilValidate.isEmpty(oldPro)) {
			removeListItem(dispatcher, userLogin, oldPro, "Product", marketingCampaignId);
		}
	}
	private static void processDataProduct(JSONObject o, String marketingCampaignId, GenericValue userLogin, Map<String, Object> inp, List<String> mkpl, int i) {
		inp.put("userLogin", userLogin);
		inp.put("marketingCampaignId", marketingCampaignId);
		if(o.has("productTypeId")) {
			inp.put("productTypeId", o.get("productTypeId"));
		}
		if(o.has("productId")) {
			inp.put("productId", o.get("productId"));
		}
		if(o.has("uomId")) {
			inp.put("uomId", o.get("uomId"));
		}
		if(o.has("quantity")) {
			String qua = o.getString("quantity");
			BigDecimal quantity;
			if(!UtilValidate.isEmpty(qua)) {
				quantity = new BigDecimal(o.getString("quantity"));
			}else{
				quantity = new BigDecimal(0);
			}
			inp.put("quantity", quantity);
		}
		if(!UtilValidate.isEmpty(mkpl)) {
			inp.put("marketingPlaceId", mkpl.get(i));
		}
	}
	private static void updateListCost(LocalDispatcher dispatcher, GenericValue userLogin, String ct, String marketingCampaignId, List<GenericValue> oldPro, List<String> mkpl)
			throws GenericServiceException {
		JSONArray costs = JSONArray.fromObject(ct);
		for(int i = 0; i< costs.size(); i++) {
			JSONObject o = costs.getJSONObject(i);
			if(!o.has("marketingCostTypeId")) {
				continue;
			}
			Map<String, Object> inp = FastMap.newInstance();
			processDataCost(o, marketingCampaignId, userLogin, inp, mkpl, i);
			if(o.has("marketingCostId")) {
				String marketingCostId = o.getString("marketingCostId");
				checkListHasItem(oldPro, "marketingCostId", marketingCostId);
				inp.put("marketingCostId", marketingCostId);
				dispatcher.runSync("updateMarketingCost", inp);
			}else{
				dispatcher.runSync("createMarketingCost", inp);
			}
		}
		if(!UtilValidate.isEmpty(oldPro)) {
			removeListItem(dispatcher, userLogin, oldPro, "Cost", marketingCampaignId);
		}
	}
	private static void processDataCost(JSONObject o, String marketingCampaignId, GenericValue userLogin, Map<String, Object> inp, List<String> mkpl, int i) {
		inp.put("userLogin", userLogin);
		inp.put("marketingCampaignId", marketingCampaignId);
		if(o.has("marketingCostTypeId")) {
			inp.put("marketingCostTypeId",  o.get("marketingCostTypeId"));
		}
		if(o.has("description")) {
			inp.put("description",  o.get("description"));
		}
		if(o.has("currencyUomId")) {
			inp.put("currencyUomId",  o.get("currencyUomId"));
		}
		if(o.has("quantityUomId")) {
			inp.put("quantityUomId",  o.get("quantityUomId"));
		}
		if(o.has("quantity")) {
			String qua = o.getString("quantity");
			BigDecimal quantity;
			if(!UtilValidate.isEmpty(qua)) {
				quantity = new BigDecimal(o.getString("quantity"));
			}else{
				quantity = new BigDecimal(0);
			}
			inp.put("quantity", quantity);
		}
		if(o.has("unitPrice")) {
			String up = o.getString("unitPrice");
			BigDecimal unitPrice;
			if(!UtilValidate.isEmpty(up)) {
				unitPrice = new BigDecimal(o.getString("unitPrice"));
			}else{
				unitPrice = new BigDecimal(0);
			}
			inp.put("unitPrice", unitPrice);
		}
	}
	private static void updateListRole(LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, String ct, String marketingCampaignId, List<GenericValue> oldPro, List<String> mkpl, String isActive)
				throws GenericServiceException {
		JSONArray inputs = JSONArray.fromObject(ct);
		List<String> partiesList = FastList.newInstance();
		for(int i = 0; i< inputs.size(); i++) {
			JSONObject o = inputs.getJSONObject(i);
			Map<String, Object> inp = FastMap.newInstance();
			inp.put("userLogin", userLogin);
			inp.put("marketingCampaignId", marketingCampaignId);
			if(!o.has("partyId") || !o.has("roleTypeId")) {
				continue;
			}
			processDataRole(o, marketingCampaignId, userLogin, inp, mkpl, i);
			if(o.has("marketingRoleId")) {
				//	upate if has
				String marketingRoleId = o.getString("marketingRoleId");
				inp.put("marketingRoleId", marketingRoleId);
				checkListHasItem(oldPro, "marketingRoleId", marketingRoleId);
				if (UtilValidate.isNotEmpty(inp.get("partyId"))) {
					partiesList.add((String) inp.get("partyId"));
				}
				dispatcher.runSync("updateMarketingRole", inp);
			}else{
				//	insert if not has
				if (UtilValidate.isNotEmpty(inp.get("partyId"))) {
					partiesList.add((String) inp.get("partyId"));
				}
				dispatcher.runSync("createMarketingRole", inp);
			}
		}
		if(!UtilValidate.isEmpty(oldPro)) {
			removeListItem(dispatcher, userLogin, oldPro, "Role", marketingCampaignId);
		}
		if (UtilValidate.isNotEmpty(partiesList) && "Y".equals(isActive)) {
			dispatcher.runSync("createNotification", UtilMisc.toMap("header", UtilProperties.getMessage("CustomMarketingUiLabels", "ThereAreNewCampaignAreUpdated", locale), "action", "ListCampaignMarketing",
					"partiesList", partiesList, "userLogin", userLogin));
		}
	}
	private static void processDataRole(JSONObject o, String marketingCampaignId, GenericValue userLogin, Map<String, Object> inp, List<String> mkpl, int i) {
		inp.put("userLogin", userLogin);
		inp.put("marketingCampaignId", marketingCampaignId);
		if(o.has("partyId")) {
			inp.put("partyId",  o.get("partyId"));
		}
		if(o.has("roleTypeId")) {
			inp.put("roleTypeId",  o.get("roleTypeId"));
		}
		if(!UtilValidate.isEmpty(mkpl)) {
			inp.put("marketingPlaceId", mkpl.get(i));
		}
	}
	private static Boolean checkListHasItem(List<GenericValue> input, String key, String value) {
		int index = 0;
		for(GenericValue i : input) {
			String cur = i.getString(key);
			if(cur.equals(value)) {
				input.remove(index);
				return true;
			}
			index++;
		}
		return false;
	}
	private static void removeListItem(LocalDispatcher dispatcher, GenericValue userLogin, List<GenericValue> input, String name, String marketingCampaignId)
			throws GenericServiceException {
		String service = "deleteMarketing" + name;
		String key = "marketing"+name+"Id";
		for(GenericValue e : input) {
			Map<String, Object> i = FastMap.newInstance();
			i.put("userLogin", userLogin);
			i.put("marketingCampaignId", marketingCampaignId);
			i.put(key, e.getString(key));
			dispatcher.runSync(service, i);
		}
	}
	public static Map<String, Object> createMarketingCampaign( DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			GenericValue inset = delegator.makeValidValue("MarketingCampaign", context);
			inset.set("createdByUserLogin", userLoginId);
			inset.set("lastModifiedByUserLogin", userLoginId);
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			if(UtilValidate.isEmpty(marketingCampaignId)) {
				marketingCampaignId = delegator.getNextSeqId("MarketingCampaign");
				inset.setString("marketingCampaignId", marketingCampaignId);
			}
			delegator.createOrStore(inset);
			result.put("marketingCampaignId", marketingCampaignId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreateCampaign", locale));
		}
		return result;
	}
	public static Map<String, Object> updateMarketingCampaign(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingCampaignId = (String) context.get("marketingCampaignId");
			String userLoginId = userLogin.getString("userLoginId");
			if(!UtilValidate.isEmpty(marketingCampaignId)) {
				GenericValue inset = delegator.findOne("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), false);
				inset.set("lastModifiedByUserLogin", userLoginId);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotUpdateCampaign", locale));
		}
		return result;
	}
	/*marketing product*/
	public static Map<String, Object> createMarketingProduct(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValidValue("MarketingProduct", context);
			String marketingProductId = (String) context.get("marketingProductId");
			if(UtilValidate.isEmpty(marketingProductId)) {
				marketingProductId = delegator.getNextSeqId("MarketingProduct");
				mkdelys.set("marketingProductId", marketingProductId);
			}
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingProduct(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		String marketingProductId = (String)context.get("marketingProductId");
		try {
			GenericValue mk = delegator.findOne("MarketingProduct", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingProductId", marketingProductId), false);
			if(mk == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> deleteMarketingProduct(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String marketingCampaignId = (String)context.get("marketingCampaignId");
			String marketingProductId = (String)context.get("marketingProductId");
			GenericValue marketingProduct = delegator.findOne("MarketingProduct", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingProductId", marketingProductId), false);
			if(UtilValidate.isEmpty(marketingProduct)) {
				return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "NotFoundMarketingProduct", locale));
			}
			marketingProduct.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("MarketingUILabels", "deleteSuccessfully", locale));
    }
	/*marketing cost*/
	public static Map<String, Object> createMarketingCost(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue mkdelys = delegator.makeValidValue("MarketingCost", context);
			String marketingCostId = (String) context.get("marketingCostId");
			if(UtilValidate.isEmpty(marketingCostId)) {
				marketingCostId = delegator.getNextSeqId("MarketingCost");
			}
			mkdelys.set("marketingCostId", marketingCostId);
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingCost(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String marketingCampaignId = (String)context.get("marketingCampaignId");
			String marketingCostId = (String)context.get("marketingCostId");
			GenericValue mk = delegator.findOne("MarketingCost", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
																		"marketingCostId", marketingCostId), false);
			if(mk == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> deleteMarketingCost(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
			try {
				String marketingCampaignId = (String)context.get("marketingCampaignId");
				String marketingCostId = (String)context.get("marketingCostId");
				GenericValue marketingCost = delegator.findOne("MarketingCost", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingCostId", marketingCostId), false);
				if(UtilValidate.isEmpty(marketingCost)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "NotFoundMarketingCost", locale));
				}
				marketingCost.remove();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("MarketingUILabels", "deleteSuccessfully", locale));
    }
	/*marketing place*/
	public static Map<String, Object> createMarketingPlace(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			GenericValue mkdelys = delegator.makeValidValue("MarketingPlace", context);
			String marketingPlaceId = (String) context.get("marketingPlaceId");
			if(UtilValidate.isEmpty(marketingPlaceId)) {
				marketingPlaceId = delegator.getNextSeqId("MarketingPlace");
			}
			mkdelys.set("marketingPlaceId", marketingPlaceId);
			delegator.create(mkdelys);
			result.put("marketingPlaceId", "marketingPlaceId");
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToCreateMK", locale));
		}
		return result;
	}

	public static Map<String, Object> updateMarketingPlace(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String marketingCampaignId = (String)context.get("marketingCampaignId");
			String marketingPlaceId = (String)context.get("marketingPlaceId");
			GenericValue mk = delegator.findOne("MarketingPlace", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
							"marketingPlaceId", marketingPlaceId), false);
			if(mk == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> deleteMarketingPlace(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
			try {
				String marketingCampaignId = (String)context.get("marketingCampaignId");
				String marketingPlaceId = (String)context.get("marketingPlaceId");
				GenericValue marketingPlace = delegator.findOne("MarketingPlace", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingPlaceId", marketingPlaceId), false);
				if(UtilValidate.isEmpty(marketingPlace)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "NotFoundMarketingPlace", locale));
				}
				marketingPlace.remove();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("MarketingUILabels", "deleteSuccessfully", locale));
    }
	/*marketing role*/
	public static Map<String, Object> createMarketingRole(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue mkdelys = delegator.makeValidValue("MarketingRole", context);
			String marketingPlaceId = (String) context.get("marketingRoleId");
			if(UtilValidate.isEmpty(marketingPlaceId)) {
				marketingPlaceId = delegator.getNextSeqId("MarketingRole");
			}
			mkdelys.set("marketingRoleId", marketingPlaceId);
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingRole(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			String marketingCampaignId = (String)context.get("marketingCampaignId");
			String marketingPlaceId = (String)context.get("marketingRoleId");
			GenericValue mk = delegator.findOne("MarketingRole", UtilMisc.toMap("marketingCampaignId", marketingCampaignId,
																		"marketingRoleId", marketingPlaceId), false);
			if(mk == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> deleteMarketingRole(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
			try {
				String marketingCampaignId = (String)context.get("marketingCampaignId");
				String marketingRoleId = (String)context.get("marketingRoleId");
				GenericValue marketingRole = delegator.findOne("MarketingRole", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingRoleId", marketingRoleId), false);
				if(UtilValidate.isEmpty(marketingRole)) {
					return ServiceUtil.returnError(UtilProperties.getMessage("MarketingUILabels", "NotFoundMarketingRole", locale));
				}
				marketingRole.remove();
			} catch (GenericEntityException e) {
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("MarketingUILabels", "deleteSuccessfully", locale));
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listEventsOfCompetitor(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			if (parameters.containsKey("partyId")) {
				if (parameters.get("partyId").length > 0) {
					listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.EQUALS, parameters.get("partyId")[0]));
				}
			}
			EntityListIterator listIterator = delegator.find("OpponentEvent", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
