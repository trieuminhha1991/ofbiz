package com.olbius.marketing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.widget.form.ModelFormField.EntityOptions;

public class MarketingCampaign {
	public static final String module = MarketingCampaign.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceOrder = "OrderUiLabels";
	public static final String resourceMarketingDelys = "DelysMarketingUILabels";

	public static Map<String, Object> createMarketingCampaign(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String campaignName = (String) context.get("campaignName");
		String campaignSummary = (String) context.get("campaignSummary");
		String ec = (String) context.get("estimatedCost");
		BigDecimal estimatedCost = ec != null ? new BigDecimal(ec)
				: new BigDecimal(0);
		String bc = (String) context.get("budgetedCost");
		BigDecimal budgetedCost = bc != null ? new BigDecimal(bc)
				: new BigDecimal(0);
		String isActive = (String) context.get("isActive");
		try {
			GenericValue inset = delegator.makeValue("MarketingCampaign");
			String marketingCampaignId = (String) context
					.get("marketingCampaignId");
			marketingCampaignId = marketingCampaignId != null ? marketingCampaignId
					: delegator.getNextSeqId("MarketingCampaign");
			inset.set("marketingCampaignId", marketingCampaignId);
			inset.set("campaignName", campaignName);
			inset.set("campaignSummary", campaignSummary);
			inset.set("statusId", "MKTG_CAMP_PLANNED");
			inset.set("estimatedCost", estimatedCost);
			inset.set("budgetedCost", budgetedCost);
			inset.set("isActive", isActive);
			inset.set("fromDate", fromDate);
			inset.set("thruDate", thruDate);
			inset.set("createdByUserLogin", userLogin.get("userLoginId"));
			delegator.createOrStore(inset);
			res.put("marketingCampaignId", marketingCampaignId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		}
		return res;
	}
	public static Map<String, Object> updateMarketingCampaignHeader(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
		try {
			ModelService mk = dctx.getModelService("updateMarketingCampaign");
	        Map<String, Object> inputMK = mk.makeValid(context, ModelService.IN_PARAM);
	        ModelService mkinfo = dctx.getModelService("updateMarketingInfo");
	        Map<String, Object> inputMKinfo = mkinfo.makeValid(context, ModelService.IN_PARAM);
			dispatcher.runSync("updateMarketingCampaign", inputMK);
			dispatcher.runSync("updateMarketingInfo", inputMKinfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	/* update marketing campaign*/
	public static Map<String, Object> updateMarketingCampaign(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		try {
			GenericValue mk = delegator.findOne("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), false);
			if(mk == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	/* create marketing information */
	public static Map<String, Object> createMarketingInfo(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String marketingTypeId = (String) context.get("marketingTypeId");
		Long people = (Long) context.get("people");
		// String marketingPlace = (String) context.get("marketingPlace");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValue("MarketingInfo");
			mkdelys.set("people", people);
			mkdelys.set("marketingCampaignId", marketingCampaignId);
			mkdelys.set("marketingTypeId", marketingTypeId);
			// mkdelys.set("marketingPlace", marketingPlace);
			delegator.createOrStore(mkdelys);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.put("message", "error");
			return res;
		}
		res.put("message", "success");
		return res;
	}
	/* update marketing campaign info */
	public static Map<String, Object> updateMarketingInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		try {
			GenericValue mk = delegator.findOne("MarketingInfo", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), false);
			if(mk == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createMarketingPlace(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValidValue("MarketingPlace", context);
			String marketingPlaceId = (String) context.get("marketingPlaceId");
			if(marketingPlaceId == null || marketingPlaceId.isEmpty()){
				marketingPlaceId = delegator.getNextSeqId("MarketingPlace");
			}
			mkdelys.set("marketingPlaceId", marketingPlaceId);
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingPlace(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		String marketingPlaceId = (String)context.get("marketingPlaceId");
		try {
			GenericValue mk = delegator.findOne("MarketingPlace", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "marketingPlaceId", marketingPlaceId), false);
			if(mk == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createMarketingProductSimple(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValidValue("MarketingProduct", context);
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingProduct(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		String productId = (String)context.get("productId");
		try {
			GenericValue mk = delegator.findOne("MarketingPlace", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, "productId", productId), false);
			if(mk == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createMarketingCost(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValidValue("MarketingCostDetail", context);
			String marketingCostId = (String) context.get("marketingCostId");
			if(marketingCostId == null || marketingCostId.isEmpty()){
				marketingCostId = delegator.getNextSeqId("MarketingCostDetail");
			}
			mkdelys.set("marketingCostId", marketingCostId);
			delegator.create(mkdelys);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToCreateMK", locale));
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> updateMarketingCost(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String marketingCampaignId = (String)context.get("marketingCampaignId");
		String marketingCostId = (String)context.get("marketingCostId");
		String marketingCostTypeId = (String)context.get("marketingCostTypeId");
		try {
			GenericValue mk = delegator.findOne("MarketingCostDetail", UtilMisc.toMap("marketingCampaignId", marketingCampaignId, 
																		"marketingCostId", marketingCostId,
																		"marketingCostTypeId", marketingCostTypeId), false);
			if(mk == null){
				return ServiceUtil.returnError(UtilProperties.getMessage("DelysMarketingUILabels", "FailedToGetMK", locale));
			}
			mk.setNonPKFields(context);
			mk.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	/* create postal address with geo id of city & address */
	public static Map<String, Object> createPostalAddressGeo(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String address1 = (String) context.get("address1");
		String contactMechId = (String) context.get("contactMechId");
		String city = (String) context.get("stateProvinceGeoId");
		try {
			/* Marketing delys info */
			GenericValue mkdelys = delegator.makeValue("PostalAddress");
			mkdelys.set("contactMechId", contactMechId);
			mkdelys.set("address1", address1);
			mkdelys.set("stateProvinceGeoId", city);
			delegator.createOrStore(mkdelys);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.put("message", "error");
			return res;
		}
		res.put("message", "success");
		return res;
	}

	/* create list cost for marketing */
	public static Map<String, Object> createCostList(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String costList = (String) context.get("costList");
		if (costList != null) {
			JSONArray costs = JSONArray.fromObject(costList);
			try {
				for (int j = 0; j < costs.size(); j++) {
					JSONObject co = costs.getJSONObject(j);
					String id = co.getString("id");
					JSONArray listCost = co.getJSONArray("content");
					for (int k = 0; k < listCost.size(); k++) {
						JSONObject tmp = listCost.getJSONObject(k);
						GenericValue coe = delegator
								.makeValue("MarketingCostDetail");
						coe.set("marketingCostId",
								delegator.getNextSeqId("MarketingCostDetail"));
						coe.set("marketingCampaignId", marketingCampaignId);
						coe.set("marketingCostTypeId", id);
						coe.set("description", tmp.getString("description"));
						coe.set("unitPrice",
								new BigDecimal(tmp.getString("unitPrice")));
						coe.set("quantity",
								new BigDecimal(tmp.getString("quantity")));
						delegator.create(coe);
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				res.put("message", "error");
				e.printStackTrace();
				return res;
			}
		}
		res.put("message", "success");
		return res;
	}

	/* remove all cost list of a marketing campaign */
	public static Map<String, Object> removeCostList(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		try {
			Map<String, Object> cond = FastMap.newInstance();
			cond.put("marketingCampaignId", marketingCampaignId);
			delegator.removeByAnd("MarketingCostDetail", cond);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	/* create list product of a marketing campaign */
	public static Map<String, Object> createSamplingPlace(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String pl = (String) context.get("places");
		JSONArray places = JSONArray.fromObject(pl);
		List<Map<String, Object>> ctmSup = FastList.newInstance();
		try {
			for (int i = 0; i < places.size(); i++) {
				String prId = places.getString(i);
				if (!prId.isEmpty()) {
					JSONObject place = JSONObject.fromObject(prId);
					String sup = place.getString("sup");
					String address = place.getString("address");
					String geoId = place.getString("geoId");
					Map<String, Object> ctm = FastMap.newInstance();
					ctm.put("userLogin", userLogin);
					ctm.put("address1", address);
					ctm.put("stateProvinceGeoId", geoId);
					Map<String, Object> contact = dispatcher.runSync(
							"createContactMechGeo", ctm);
					Map<String, Object> tmp = FastMap.newInstance();
					String contactMechId = (String) contact
							.get("contactMechId");
					tmp.put("contactMechId", contactMechId);
					tmp.put("address", address);
					ctmSup.add(tmp);
					GenericValue mkPl = delegator.makeValue("MarketingPlace");
					String marketingPlaceId = delegator
							.getNextSeqId("MarketingPlace");
					mkPl.put("marketingPlaceId", marketingPlaceId);
					mkPl.put("marketingCampaignId", marketingCampaignId);
					mkPl.put("contactMechId", contactMechId);
					mkPl.put("partyId", sup);
					mkPl.put("geoId", geoId);
					mkPl.put("roleTypeId", "MKSL_SUP_SP");
					delegator.create(mkPl);
				}
			}
			res.put("results", ctmSup);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	/* create list product of a marketing campaign */
	public static Map<String, Object> createMarketingProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String productId = (String) context.get("productId");
		JSONArray products = JSONArray.fromObject(productId);
		try {
			for (int i = 0; i < products.size(); i++) {
				String prId = products.getString(i);
				if (!prId.isEmpty()) {
					GenericValue product = delegator
							.makeValue("MarketingProduct");
					product.set("marketingCampaignId", marketingCampaignId);
					product.set("productId", prId);
					delegator.create(product);
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	/* create Logistic requirement */
	public static Map<String, Object> createLogisticRequirement(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<String> listRequirementId = FastList.newInstance();
		try {
			String requirementTypeId = (String) context
					.get("requirementTypeId");
			Timestamp shipBeforeDate = (Timestamp) context.get("fromDate");
			String requirementId = "";
			String currentPlace = "";
			List<GenericValue> products = (List<GenericValue>) context
					.get("products");

			for (GenericValue product : products) {
				if (!requirementId.isEmpty()
						&& currentPlace == product.getString("contactMechId")) {
					BigDecimal quantity = new BigDecimal(
							product.getInteger("quantity"));
					String uom = product.getString("uomId");
					Map<String, Object> tmp = FastMap.newInstance();
					tmp.put("userLogin", userLogin);
					tmp.put("requirementId", requirementId);
					tmp.put("requirementTypeId", requirementTypeId);
					tmp.put("productId", product.getString("productId"));
					tmp.put("quantity", quantity);
					tmp.put("quantityUomId", uom);
					tmp.put("statusId", "REQ_ITEM_CREATED");
					tmp.put("shipBeforeDate", shipBeforeDate);
					dispatcher.runAsync("addProductToRequirement", tmp);
				} else if (requirementId.isEmpty()
						|| currentPlace != product.getString("contactMechId")) {
					currentPlace = product.getString("contactMechId");
					Map<String, Object> input = FastMap.newInstance();
					input.put("userLogin", userLogin);
					input.put("destContactMechId",
							(String) context.get("destContactMechId"));
					input.put("receiverParty",
							(String) context.get("receiverParty"));
					input.put("requestor", userLogin.getString("partyId"));
					input.put("requirementTypeId", requirementTypeId);
					Map<String, Object> outputReq = dispatcher.runSync(
							"updateRequirementLogistics", input);
					requirementId = (String) outputReq.get("requirementId");
					listRequirementId.add(requirementId);
				}
			}
			res.put("listRequirementId", listRequirementId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	// remove marketing product
	public static Map<String, Object> deleteMarketingCampaign(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			dispatcher.runSync("deleteMarketingCampaign", context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	// remove marketing product
	public static Map<String, Object> removeMarketingProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		try {
			Map<String, Object> cond = FastMap.newInstance();
			cond.put("marketingCampaignId", marketingCampaignId);
			delegator.removeByAnd("MarketingProduct", cond);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		res.put("message", "success");
		return res;
	}

	/* store sampling product into MarketingSamplingProduct table */
	public static Map<String, Object> createSamplingProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		String productUom = (String) context.get("productUom");
		List<Map<String, Object>> contactMech = (List<Map<String, Object>>) context
				.get("contactMech");
		JSONArray products = JSONArray.fromObject(productUom);
		try {
			for (int i = 0; i < products.size(); i++) {
				JSONObject product = products.getJSONObject(i);
				String productId = product.getString("id");
				String address = product.getString("geoId");
				JSONArray content = product.getJSONArray("content");
				String contactMechId = checkContactMech(contactMech, address);
				if (!contactMech.isEmpty()) {
					for (int j = 0; j < content.size(); j++) {
						JSONObject tmpPro = content.getJSONObject(j);
						if (tmpPro.has("uom") && tmpPro.has("quantity")) {
							String uom = tmpPro.getString("uom");
							String quan = tmpPro.getString("quantity");
							BigDecimal quantity = quan != null ? new BigDecimal(
									quan) : new BigDecimal(0);
							GenericValue tmp = delegator
									.makeValue("MarketingSamplingProduct");
							String productTypeId = tmpPro.getString("type");
							tmp.set("marketingCampaignId", marketingCampaignId);
							tmp.set("productId", productId);
							tmp.set("contactMechId", contactMechId);
							tmp.set("uomId", uom);
							tmp.set("quantity", quantity);
							tmp.set("productTypeId", productTypeId);
							delegator.create(tmp);
						}
					}
				}
			}
			res.put("message", "success");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
		}
		return res;
	}

	public static String checkContactMech(List<Map<String, Object>> input,
			String key) {
		for (Map<String, Object> obj : input) {
			String contactMechId = (String) obj.get("contactMechId");
			String address = (String) obj.get("address");
			String tmp1 = address.toLowerCase();
			String tmp2 = address.toLowerCase();
			if (tmp1.contains(tmp2) || tmp2.contains(tmp1)) {
				return contactMechId;
			}
		}
		return "";
	}

	/* remove all cost list of a marketing campaign */
	public static Map<String, Object> removeMarketingSamplingProduct(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		try {
			Map<String, Object> cond = FastMap.newInstance();
			cond.put("marketingCampaignId", marketingCampaignId);
			delegator.removeByAnd("MarketingSamplingProduct", cond);
			res.put("message", "success");
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
			return res;
		}
		return res;
	}

	// get marketing sampling campaign by id
	public static Map<String, Object> getMarketingSampling(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		try {
			Map<String, Object> mk = FastMap.newInstance();
			mk.put("marketingCampaignId", marketingCampaignId);
			List<GenericValue> marketingCampaign = delegator.findByAnd(
					"MarketingCampaignDetail", mk, null, false);
			if (!marketingCampaign.isEmpty()) {
				GenericValue tmp = marketingCampaign.get(0);
				Map<String, Object> marketing = FastMap.newInstance();
				marketing.put("info", tmp);
				List<GenericValue> products = delegator.findList(
						"MarketingSamplingProductDetail", EntityCondition
								.makeCondition("marketingCampaignId",
										marketingCampaignId), null, null, null,
						false);
				List<GenericValue> costs = delegator.findList(
						"MarketingCampaignCostDetail", EntityCondition
								.makeCondition("marketingCampaignId",
										marketingCampaignId), null, null, null,
						false);
				List<GenericValue> places = delegator.findList(
						"MarketingPlaceDetail", EntityCondition.makeCondition(
								"marketingCampaignId", marketingCampaignId),
						null, null, null, false);
				marketing.put("products", products);
				marketing.put("costs", costs);
				marketing.put("places", places);
				res.put("result", marketing);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	// get district by province
	public static Map<String, Object> getDistrictByProvince(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		String province = (String) context.get("geoId");
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("geoIdFrom",
					EntityOperator.EQUALS, province));
			cond.add(EntityCondition.makeCondition("geoTypeId",
					EntityOperator.EQUALS, "DISTRICT"));
			List<GenericValue> geo = delegator.findList("GeoAssocAndGeoTo",
					EntityCondition.makeCondition(cond, EntityOperator.AND),
					null, null, null, true);
			res.put("results", geo);
			res.put("message", "success");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("message", "error");
		}
		return res;
	}
	// get district by province
	public static Map<String, Object> getProvinceByCountry(
				DispatchContext ctx, Map<String, ? extends Object> context) {
			Map<String, Object> res = FastMap.newInstance();
			String province = (String) context.get("geoId");
			Delegator delegator = ctx.getDelegator();
			try {
				List<EntityCondition> cond = FastList.newInstance();
				cond.add(EntityCondition.makeCondition("geoIdFrom",
						EntityOperator.EQUALS, province));
				List<EntityCondition> type = FastList.newInstance();
				type.add(EntityCondition.makeCondition("geoTypeId",
						EntityOperator.EQUALS, "PROVINCE"));
				type.add(EntityCondition.makeCondition("geoTypeId",
						EntityOperator.EQUALS, "STATE"));
				cond.add(EntityCondition.makeCondition(type, EntityOperator.OR));
				List<GenericValue> geo = delegator.findList("GeoAssocAndGeoTo",
						EntityCondition.makeCondition(cond, EntityOperator.AND),
						null, null, null, true);
				res.put("results", geo);
				res.put("message", "success");
			} catch (Exception e) {
				e.printStackTrace();
				res.put("message", "error");
			}
			return res;
		}

	// get district by province
	public static Map<String, Object> getWardByDistrict(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		String province = (String) context.get("geoId");
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("geoIdFrom",
					EntityOperator.EQUALS, province));
			cond.add(EntityCondition.makeCondition("geoTypeId",
					EntityOperator.EQUALS, "WARD"));
			List<GenericValue> geo = delegator.findList("GeoAssocAndGeoTo",
					EntityCondition.makeCondition(cond, EntityOperator.AND),
					null, null, null, true);
			res.put("results", geo);
			res.put("message", "success");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("message", "error");
		}
		return res;
	}
	// get sup by county
	public static Map<String, Object> getSupByDistrict(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		String county = (String) context.get("geoId");
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("countyGeoId",
					EntityOperator.EQUALS, county));
			List<GenericValue> geo = delegator.findList("PartyContactMechGeo",
					EntityCondition.makeCondition(cond, EntityOperator.AND),
					null, null, null, true);
			res.put("results", geo);
			res.put("message", "success");
		} catch (Exception e) {
			e.printStackTrace();
			res.put("message", "error");
		}
		return res;
	}
	
	/*create Marketing promos */
	public static Map<String, Object> createMarketingPromos(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> res = FastMap.newInstance();
		String geo = (String) context.get("geoId");
		String productPromoId = (String) context.get("productPromoId");
		String promoName = (String) context.get("promoName");
		String productPromoTypeId = (String) context.get("productPromoTypeId");
		List<String> roleTypeIds = (List<String>) context.get("roleTypeIds");
		List<String> productStoreId = (List<String>) context.get("productStoreIds");
		String promoText = (String) context.get("promoText");
		String budgetId = (String) context.get("budgetId");
		String miniRevenueId = (String) context.get("miniRevenueId");
		String promoSalesTargets = (String) context.get("promoSalesTargets");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		Timestamp thruDate = (Timestamp) context.get("thruDate");
		String isActive = (String) context.get("isActive");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			Map<String, Object> promos = FastMap.newInstance();
			promos.put("productPromosId", productPromoId);
			promos.put("promoName", promoName);
			promos.put("productPromoTypeId", productPromoTypeId);
			promos.put("roleTypeIds", roleTypeIds);
			promos.put("productStoreIds", productStoreId);
			promos.put("promoText", promoText);
			promos.put("budgetId", budgetId);
			promos.put("miniRevenueId", miniRevenueId);
			promos.put("promoSalesTargets", promoSalesTargets);
			promos.put("fromDate", fromDate);
			promos.put("thruDate", thruDate);
			promos.put("userLogin", userLogin);
			Map<String, Object> promosOutput = dispatcher.runSync("createProductPromoDelys", promos);
			if(productPromoId == null || productPromoId.isEmpty()){
				productPromoId = (String) promosOutput.get("productPromoId");
			}
			Map<String, Object> mk = FastMap.newInstance();
			mk.put("isActive", isActive);
			mk.put("fromDate", fromDate);
			mk.put("thruDate", thruDate);
			mk.put("campaignName", promoName);
			mk.put("campaignSummary", promoText);	
			mk.put("userLogin", userLogin);
			Map<String, Object> mkOutut = dispatcher.runSync("createMarketingCampaign", mk);
			String marketingCampaignId = (String) mkOutut.get("marketingCampaignId");
			Map<String, Object> mkInfo = FastMap.newInstance();
			mkInfo.put("marketingCampaignId", marketingCampaignId);
			mkInfo.put("marketingTypeId", productPromoTypeId);
			mkInfo.put("geoId", geo);
			mkInfo.put("userLogin", userLogin);
			dispatcher.runSync("createMarketingInfo", mkInfo);
			Map<String, Object> mkPro = FastMap.newInstance();
			mkPro.put("marketingCampaignId", marketingCampaignId);
			mkPro.put("productPromosId", productPromoId);
			mkPro.put("userLogin", userLogin);
			dispatcher.runSync("createMarketingProductPromos", mkPro);
			String rules = (String) context.get("rules");
			if(rules != null && !rules.isEmpty()){
				Map<String, Object> cr = FastMap.newInstance();
				cr.put("rules", rules);
				cr.put("userLogin", userLogin);
				cr.put("productPromoId", productPromoId);
				dispatcher.runSync("createRule", cr);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	public static Map<String, Object> createMarketingProductPromos(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String productPromosId = (String) context.get("productPromosId");
		try {
			GenericValue inset = delegator.makeValue("MarketingPromos");
			inset.set("marketingCampaignId", marketingCampaignId);
			inset.set("productPromosId", productPromosId);
			delegator.createOrStore(inset);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		}
		return res;
	}
	public static Map<String, Object> createRule(DispatchContext ctx, Map<String, ? extends Object> context){
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productPromoId = (String) context.get("productPromoId");
		String rules = (String) context.get("rules");
		Map<String, Object> res = FastMap.newInstance();
		try{
			if(rules != null && !rules.isEmpty()){
				JSONArray rulesArr = JSONArray.fromObject(rules);
				for(int i = 0; i < rulesArr.size(); i++){
					JSONObject o = rulesArr.getJSONObject(i);
					String rname = o.getString("ruleName");
					String rcond = o.getString("cond");
					String raction = o.getString("action");
					Map<String, Object> rmap = FastMap.newInstance();
					rmap.put("userLogin", userLogin);
					rmap.put("productPromoId", productPromoId);
					rmap.put("ruleName", rname);
					Map<String, Object> rout = dispatcher.runSync("createProductPromoRuleDelys", rmap);
					String productPromoRuleId = (String) rout.get("productPromoRuleId");
					Map<String, Object> cin = FastMap.newInstance();
					cin.put("userLogin", userLogin);
					cin.put("condition", rcond);
					cin.put("productPromoId", productPromoId);
					cin.put("productPromoRuleId", productPromoRuleId);
					dispatcher.runSync("createRuleCondition", cin);
					Map<String, Object> ain = FastMap.newInstance();
					ain.put("userLogin", userLogin);
					ain.put("action", raction);
					ain.put("productPromoId", productPromoId);
					ain.put("productPromoRuleId", productPromoRuleId);
					dispatcher.runSync("createRuleAction", ain);

				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	public  static Map<String, Object> createRuleCondition(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		String conds = (String) context.get("condition");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			JSONObject condArr = JSONObject.fromObject(conds);
			List<String> prodList = FastList.newInstance();
			if(condArr.has("products") && !condArr.getString("products").equals("null")){
				JSONArray products = condArr.getJSONArray("products");
				for(int i = 0; i < products.size(); i++){
					prodList.add(products.getString(i));
				}
			}
			List<String> categories = FastList.newInstance();
			if(condArr.has("categories") && !condArr.getString("categories").equals("null")){
				JSONArray cates = condArr.getJSONArray("categories");
				for(int j = 0; j< cates.size(); j++){
					categories.add(cates.getString(j));
				}
			}
			String inputParamEnumId = condArr.getString("inputParamEnumId");
			String operatorEnumId = condArr.getString("operatorEnumId");
			String condValue = condArr.getString("condValue");
			Map<String, Object> input = FastMap.newInstance();
			input.put("productPromoId", productPromoId);
			input.put("productPromoRuleId", productPromoRuleId);
			input.put("inputParamEnumId", inputParamEnumId);
			input.put("operatorEnumId", operatorEnumId);
			input.put("condValue", condValue);
			input.put("productIdListCond", prodList);
			input.put("productCatIdListCond", categories);
			input.put("userLogin", userLogin);
			dispatcher.runSync("createProductPromoCondDelys", input);
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	public  static Map<String, Object> createRuleAction(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		String action = (String) context.get("action");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try{
			JSONObject condArr = JSONObject.fromObject(action);
			List<String> prodList = FastList.newInstance();
			if(condArr.has("products") && !condArr.getString("products").equals("null")){
				JSONArray products = condArr.getJSONArray("products");
				for(int i = 0; i < products.size(); i++){
					prodList.add(products.getString(i));
				}
			}
			List<String> categories = FastList.newInstance();
			if(condArr.has("categories") && !condArr.getString("categories").equals("null")){
				JSONArray cates = condArr.getJSONArray("categories");
				for(int j = 0; j< cates.size(); j++){
					categories.add(cates.getString(j));
				}
			}
			String productPromoActionEnumId = condArr.getString("productPromoActionEnumId");
			String quantity = condArr.getString("quantity");
			String amount = condArr.getString("amount");
			String productId = condArr.getString("productId");
			Map<String, Object> input = FastMap.newInstance();
			input.put("productPromoId", productPromoId);
			input.put("productPromoRuleId", productPromoRuleId);
			input.put("productPromoActionEnumId", productPromoActionEnumId);
			input.put("productIdListAction", prodList);
			input.put("productCatIdListAction", categories);
			input.put("quantity", quantity);
			input.put("amount", amount);
			input.put("productId", productId);
			input.put("userLogin", userLogin);
			dispatcher.runSync("createProductPromoActionDelys", input);
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	/*create Marketing promos */
	public static Map<String, Object> updateMarketingPromos(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> res = FastMap.newInstance();
		String geo = (String) context.get("place");
		String isActive = (String) context.get("isActive");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try{
//			dispatcher.runSync("","");
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
}
