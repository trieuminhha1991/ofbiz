package com.olbius.marketing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class TradeServices {
	public static final String module = TradeServices.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceCustom = "DelysMarketingUiLabels";

	public static Map<String, Object> createSamplingCampaign(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String costList = (String) context.get("costList");
		String productUom = (String) context.get("products");
		String places = (String) context.get("places");
		try {
			Map<String, Object> mk = FastMap.newInstance();
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			mk.put("fromDate", fromDate);
			mk.put("thruDate", thruDate);
			mk.put("userLogin", userLogin);
			mk.put("campaignName", (String) context.get("campaignName"));
			mk.put("campaignSummary", (String) context.get("campaignSummary"));
			mk.put("statusId", (String) context.get("statusId"));
			mk.put("estimatedCost", (String) context.get("estimatedCost"));
			mk.put("isActive", (String) context.get("isActive"));
			Map<String, Object> marketing = (Map<String, Object>) dispatcher
					.runSync("createMarketingCampaign", mk);
			String marketingCampaignId = (String) marketing
					.get("marketingCampaignId");
			String pe = (String) context.get("people");
			Long people = pe != null ? Long.parseLong(pe) : 0;
			Map<String, Object> mkInfo = FastMap.newInstance();
			mkInfo.put("userLogin", userLogin);
			mkInfo.put("marketingCampaignId", marketingCampaignId);
			mkInfo.put("marketingTypeId", "SAMPLING_ACT");
			mkInfo.put("people", people);
			// mkInfo.put("marketingPlace", marketingPlace);
			/* create marketing information detail */
			dispatcher.runSync("createMarketingInfo", mkInfo);
			/* create contact mech */
			if (places != null) {
				/*create marketing sampling place*/
				Map<String, Object> ctm = FastMap.newInstance();
				ctm.put("userLogin", userLogin);
				ctm.put("places", places);
				ctm.put("marketingCampaignId", marketingCampaignId);
				Map<String, Object> ctmOut = dispatcher.runSync(
						"createSamplingPlace", ctm);
				/* create marketing product */
				if (productUom != null) {
					/* create marketing sampling product */
					Map<String, Object> sampling = FastMap.newInstance();
					sampling.put("marketingCampaignId", marketingCampaignId);
					sampling.put("productUom", productUom);
					sampling.put("userLogin", userLogin);
					sampling.put("contactMech", ctmOut.get("results"));
					dispatcher.runSync("createMarketingSamplingProduct",
							sampling);
				}
			}
			/* create marketing cost list */
			if (costList != null) {
				Map<String, Object> cost = FastMap.newInstance();
				cost.put("userLogin", userLogin);
				cost.put("costList", costList);
				cost.put("marketingCampaignId", marketingCampaignId);
				dispatcher.runSync("createCostList", cost);
			}
			res.put("message", "success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
		}
		return res;
	}
	
	/* update trade marketing - sampling activation */
	public static Map<String, Object> updateSamplingCampaign(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> res = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String costList = (String) context.get("costList");
		String productUom = (String) context.get("products");
		String isActive = (String) context.get("isActive");
		String marketingCampaignId = (String) context
				.get("marketingCampaignId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> user = FastMap.newInstance();
			user.put("marketingCampaignId", marketingCampaignId);
			user.put("userLogin", userLogin);
			Map<String, Object> mk = FastMap.newInstance();
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			mk.put("fromDate", fromDate);
			mk.put("thruDate", thruDate);
			mk.put("userLogin", userLogin);
			mk.put("campaignName", (String) context.get("campaignName"));
			mk.put("campaignSummary", (String) context.get("campaignSummary"));
			mk.put("marketingCampaignId",
					(String) context.get("marketingCampaignId"));
			mk.put("statusId", (String) context.get("statusId"));
			mk.put("estimatedCost", (String) context.get("estimatedCost"));
			mk.put("isActive", isActive);
			Map<String, Object> marketing = (Map<String, Object>) dispatcher
					.runSync("createMarketingCampaign", mk);
			marketingCampaignId = (String) marketing.get("marketingCampaignId");
			Long people = Long.parseLong((String) context.get("people"));
			String marketingPlace = (String) context.get("marketingPlace");
			Map<String, Object> mkInfo = FastMap.newInstance();
			mkInfo.put("userLogin", userLogin);
			mkInfo.put("marketingCampaignId", marketingCampaignId);
			mkInfo.put("marketingTypeId", "SAMPLING_ACT");
			mkInfo.put("people", people);
			mkInfo.put("marketingPlace", marketingPlace);
			/* create marketing information detail */
			dispatcher.runSync("createMarketingInfo", mkInfo);
			dispatcher.runSync("removeCostList", user);
			/* create marketing cost list */
			if (costList != null) {
				Map<String, Object> cost = FastMap.newInstance();
				cost.put("userLogin", userLogin);
				cost.put("costList", costList);
				cost.put("marketingCampaignId", marketingCampaignId);
				dispatcher.runSync("createCostList", cost);
			}
			/* create marketing product */
			dispatcher.runSync("removeMarketingSamplingProduct", user);
			if (productUom != null) {
				Map<String, Object> product = FastMap.newInstance();
				product.put("marketingCampaignId", marketingCampaignId);
				/* create marketing sampling product */
				Map<String, Object> sampling = FastMap.newInstance();
				sampling.put("marketingCampaignId", marketingCampaignId);
				sampling.put("productUom", productUom);
				sampling.put("userLogin", userLogin);
				dispatcher.runSync("createMarketingSamplingProduct", sampling);
			}
			/* send requirement to logistic */
			if (isActive != null && !isActive.isEmpty()) {
				Map<String, Object> requirement = FastMap.newInstance();
				requirement.put("userLogin", userLogin);
				requirement.put("requestor", userLogin.get("partyId"));
				requirement.put("", "");
				dispatcher.runSync("updateRequirementLogistics", user);
			}
			res.put("message", "success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.put("message", "error");
			e.printStackTrace();
		}
		return res;
	}

	public static Map<String, Object> getListTradeMarketing(
			DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		String[] typetmp = parameters.get("type");
		if(typetmp != null){
			String type = typetmp[0];
			if (type.isEmpty()) {
				successResult.put("listIterator", listIterator);
				return successResult;
			}
			if (type.equals("res")) {
				List<EntityCondition> tmp = FastList.newInstance();
				tmp.add(EntityCondition.makeCondition("marketingTypeId",
						"RESEARCH_FREQ"));
				tmp.add(EntityCondition.makeCondition("marketingTypeId",
						"RESEARCH_UNED"));
				listAllConditions.add(EntityCondition.makeCondition(tmp,
						EntityOperator.OR));
			} else if (type.equals("spl")) {
				listAllConditions.add(EntityCondition.makeCondition(
						"marketingTypeId", "SAMPLING_ACT"));
			} else if (type.equals("gift")) {
				listAllConditions.add(EntityCondition.makeCondition(
						"marketingTypeId", "TRADE_PROMOS"));
			} else if (type.equals("disc")) {
				listAllConditions.add(EntityCondition.makeCondition(
						"marketingTypeId", "DISCOUNT"));
			} else if (type.equals("aff")) {
				listAllConditions.add(EntityCondition.makeCondition(
						"marketingTypeId", "AFFILIATE"));
			} else if (type.equals("dig")) {
				listAllConditions.add(EntityCondition.makeCondition(
						"marketingTypeId", "DIGITAL"));
			} else {
				successResult.put("listIterator", listIterator);
				return successResult;
			}
			List<String> listSortFields = (List<String>) context
					.get("listSortFields");
			listSortFields.add("marketingCampaignId ASC");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityCondition tmpCond = EntityCondition.makeCondition(
					listAllConditions, EntityOperator.AND);
			try {
				listIterator = delegator.find("MarketingCampaignDetail", tmpCond,
						null, null, listSortFields, opts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
}
