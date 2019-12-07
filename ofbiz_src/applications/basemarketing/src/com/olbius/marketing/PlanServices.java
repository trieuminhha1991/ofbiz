package com.olbius.marketing;

import java.sql.Date;
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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class PlanServices {
	public static final String module = PlanServices.class.getName();
	public static final String resourceMarketing = "MarketingUiLabels";
	public static final String resourceMarketingCustom = "CustomMarketingUiLabels";

	public static Map<String, Object> getAllPlanMarketing(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try{
			String code = (String) context.get("code");
			String name = (String) context.get("name");
			String marketingPlanId = (String) context.get("marketingPlanId");
			List<EntityCondition> conditions = FastList.newInstance();
			if(!UtilValidate.isEmpty(code)){
				String c = "%" + code + "%";
				conditions.add(EntityCondition.makeCondition("code", EntityOperator.LIKE, c));
			}
			if(!UtilValidate.isEmpty(name)){
				String na = "%" + name + "%";
				conditions.add(EntityCondition.makeCondition("name", EntityOperator.LIKE, na));
			}
			if(!UtilValidate.isEmpty(marketingPlanId)){
				String mk = "%" + marketingPlanId + "%";
				conditions.add(EntityCondition.makeCondition("marketingPlanId", EntityOperator.LIKE, mk));
			}
			EntityFindOptions options = new EntityFindOptions();
			options.setLimit(10);
			List<GenericValue> results = delegator.findList("MarketingPlan", EntityCondition.makeCondition(conditions, EntityOperator.OR), null, null, null, false);
			result.put("results", results);
		}catch(Exception e){
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotGetPlan", locale));
		}
		return result;
	}
	public static Map<String, Object> createMarketingPlanAndItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			ModelService model = ctx.getModelService("createMarketingPlan");
	        Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			Map<String, Object> marketing = dispatcher.runSync("createMarketingPlan", inputMap);
			String marketingPlanId = (String) marketing.get("marketingPlanId");
			String code = (String) context.get("code");
			String contents = (String) context.get("contents");
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("fromDate");
			if(!UtilValidate.isEmpty(contents)){
				JSONArray listContent = JSONArray.fromObject(contents);
				for(int i = 0; i < listContent.size(); i++){
					JSONObject o = listContent.getJSONObject(i);
					if(!o.has("name") || !o.has("description")){
						continue;
					}
					String name;
					if(UtilValidate.isEmpty(code)){
						 name = "Plan " + marketingPlanId + o.getString("name");
					}else{
						name = "Plan " + code + o.getString("name");
					}
					String description = o.getString("description");
					String type = o.getString("type");
					Map<String, Object> input = FastMap.newInstance();
					input.put("userLogin", userLogin);
					input.put("contentName", name);
					input.put("description", description);
					Map<String, Object> out = dispatcher.runSync("createContentMarketing", input);
					String contentId = (String) out.get("contentId");
					input = FastMap.newInstance();
					input.put("userLogin", userLogin);
					input.put("marketingPlanId", marketingPlanId);
					input.put("marketingContentTypeId", type);
					input.put("contentId", contentId);
					input.put("fromDate", fromDate);
					input.put("thruDate", thruDate);
					dispatcher.runSync("createMarketingPlanContent", input);
				}
			}

			result.put("marketingPlanId", marketingPlanId);

		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotCreatePlan", locale));
		}
		return result;
	}
	public static Map<String, Object> updateMarketingPlanAndItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			ModelService model = ctx.getModelService("updateMarketingPlan");
	        Map<String, Object> inputMap = model.makeValid(context, ModelService.IN_PARAM);
			dispatcher.runSync("updateMarketingPlan", inputMap);
			String marketingPlanId = (String) context.get("marketingPlanId");
			String code = (String) context.get("code");
			String contents = (String) context.get("contents");
			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("fromDate");
			if(!UtilValidate.isEmpty(contents)){
				JSONArray listContent = JSONArray.fromObject(contents);
				for(int i = 0; i < listContent.size(); i++){
					JSONObject o = listContent.getJSONObject(i);
					if(!o.has("name") || !o.has("description")){
						continue;
					}
					String name;
					if(UtilValidate.isEmpty(code)){
						 name = "Plan " + marketingPlanId + o.getString("name");
					}else{
						name = "Plan " + code + o.getString("name");
					}

					String description = o.getString("description");
					String type = o.getString("type");
					Map<String, Object> input = FastMap.newInstance();
					input.put("userLogin", userLogin);
					input.put("contentName", name);
					input.put("description", description);
					String contentId;
					if(o.has("id") && !UtilValidate.isEmpty(o.getString("id"))){
						contentId = o.getString("id");
						input.put("contentId", contentId);
						dispatcher.runSync("updateContentMarketing", input);
						input = FastMap.newInstance();
						input.put("userLogin", userLogin);
						input.put("marketingPlanId", marketingPlanId);
						input.put("marketingContentTypeId", type);
						input.put("contentId", contentId);
						input.put("fromDate", fromDate);
						input.put("thruDate", thruDate);
						dispatcher.runSync("updateMarketingPlanContent", input);
					}else{
						Map<String, Object> out = dispatcher.runSync("createContentMarketing", input);
						contentId = (String) out.get("contentId");
						input = FastMap.newInstance();
						input.put("userLogin", userLogin);
						input.put("marketingPlanId", marketingPlanId);
						input.put("marketingContentTypeId", type);
						input.put("contentId", contentId);
						input.put("fromDate", fromDate);
						input.put("thruDate", thruDate);
						dispatcher.runSync("createMarketingPlanContent", input);
					}
				}
			}
			result.put("marketingPlanId", marketingPlanId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("CustomMarketingUiLabels", "CannotCreatePlan", locale));
		}
		return result;
	}
	/*marketing plan*/
	public static Map<String, Object> createMarketingPlan(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			GenericValue inset = delegator.makeValidValue("MarketingPlan", context);
			inset.set("createdByUserLogin", userLoginId);
			inset.set("lastModifiedByUserLogin", userLoginId);
			String marketingPlanId = (String) context.get("marketingPlanId");
			if(UtilValidate.isEmpty(marketingPlanId)){
				marketingPlanId = delegator.getNextSeqId("MarketingPlan");
				inset.setString("marketingPlanId", marketingPlanId);
			}
			delegator.createOrStore(inset);
			result.put("marketingPlanId", marketingPlanId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreatePlan", locale));
		}
		return result;
	}
	public static Map<String, Object> updateMarketingPlan(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String marketingPlanId = (String) context.get("marketingPlanId");
			String userLoginId = userLogin.getString("userLoginId");
			if(!UtilValidate.isEmpty(marketingPlanId)){
				GenericValue inset = delegator.findOne("MarketingPlan", UtilMisc.toMap("marketingPlanId", marketingPlanId), false);
				inset.set("lastModifiedByUserLogin", userLoginId);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotUpdatePlan", locale));
		}
		return result;
	}
	/*marketing plan content*/

	public static Map<String, Object> createMarketingPlanContent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue inset = delegator.makeValidValue("MarketingPlanContent", context);
			delegator.createOrStore(inset);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreatePlan", locale));
		}
		return result;
	}
	public static Map<String, Object> updateMarketingPlanContent(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			String marketingPlanId = (String) context.get("marketingPlanId");
			String contentId = (String) context.get("contentId");
			if(!UtilValidate.isEmpty(marketingPlanId)){
				GenericValue inset = delegator.findOne("MarketingPlanContent",
						UtilMisc.toMap("contentId", contentId, "marketingPlanId", marketingPlanId), false);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotUpdatePlan", locale));
		}
		return result;
	}
	/* content */
	/*marketing plan*/
	public static Map<String, Object> createContentMarketing(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String userLoginId = userLogin.getString("userLoginId");
			GenericValue inset = delegator.makeValidValue("Content", context);
			inset.set("createdByUserLogin", userLoginId);
			inset.set("lastModifiedByUserLogin", userLoginId);
			String contentId = delegator.getNextSeqId("Content");
			inset.setString("contentId", contentId);
			delegator.createOrStore(inset);
			result.put("contentId", contentId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotCreatePlan", locale));
		}
		return result;
	}
	public static Map<String, Object> updateContentMarketing(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String contentId = (String) context.get("contentId");
			String userLoginId = userLogin.getString("userLoginId");
			if(!UtilValidate.isEmpty(contentId)){
				GenericValue inset = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
				inset.set("lastModifiedByUserLogin", userLoginId);
				inset.setNonPKFields(context);
				inset.store();
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(UtilProperties.getMessage("MarketingUiLabels", "CannotUpdatePlan", locale));
		}
		return result;
	}
    public static Map<String, Object> getAutoMarketingPlanId(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String,Object> successReturn = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String result = "";
        try {
            result = "MKT" + delegator.getNextSeqId("MarketingPlan");
            successReturn.put("result",result);
        }catch (Exception e){
            return ServiceUtil.returnError(UtilProperties.getMessage("BaseMarketingUiLabels", "MKAutoIdError", locale));
        }

        return successReturn;
    }

}
