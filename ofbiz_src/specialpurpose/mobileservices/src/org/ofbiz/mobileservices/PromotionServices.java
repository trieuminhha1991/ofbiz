package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import com.olbius.basepo.product.ProductUtils;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.Mobile;
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
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.mobileUtil.MobileUtils;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;

import javax.rmi.CORBA.Util;

public class PromotionServices implements Mobile {

	public static final String module = PromotionServices.class.getName();
	public static Map<String, Object> getPromotionTypes(DispatchContext dcx, Map<String, Object> context){
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Delegator delegator = dcx.getDelegator();
		try {
			List<GenericValue> list = delegator.findList("ProductPromoExtType", null, null, UtilMisc.toList("productPromoTypeId"), null, false);
			results.put("results", list);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	public static Map<String, Object> getPromotionsByType(DispatchContext dcx, Map<String, Object> context){
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Delegator delegator = dcx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String productPromoTypeId = (String) context.get("productPromoTypeId");
		String productStores = (String) context.get("productStores");
		try {
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<EntityCondition> cond = FastList.newInstance();
			cond.add(EntityCondition.makeCondition("productPromoTypeId", productPromoTypeId));
			cond.add(EntityCondition.makeCondition("showToCustomer", "Y"));
			cond.add(EntityUtil.getFilterByDateExpr());
			cond.add(EntityCondition.makeCondition("organizationPartyId", organizationId));
			cond.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
			List<String> stores = FastList.newInstance();
			JSONArray arr = JSONArray.fromObject(productStores);
			int size = arr.size();
			for(int i = 0; i < size; i ++){
				stores.add(arr.getString(i));
			}
			if(UtilValidate.isNotEmpty(stores)){
				cond.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, stores));
			}
			List<GenericValue> list = delegator.findList("ProductPromoExtStoreAppl", EntityCondition.makeCondition(cond), null, UtilMisc.toList("productPromoTypeId"), null, false);
			results.put("results", list);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	public static Map<String, Object> getOrderPromotions(DispatchContext dcx, Map<String, Object> context) throws GenericServiceException{
		Map<String, Object> results = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = dcx.getDispatcher();
		String productStoreId = (String) context.get("productStoreId");
		String[] pd = {productStoreId};
		String pagesize = (String) context.get("pagesize");
		String[] ps = { pagesize };
		String pagenum = (String) context.get("pagenum");
		String[] pn = { pagenum };
		String[] ic = {"Y"};
		String[] cs = {"Y"};
		String[] is = {"Y"};
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> in = FastMap.newInstance();
			Map<String, String[]> params = FastMap.newInstance();
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions();
			in.put("userLogin", userLogin);
			in.put("parameters", params);
			in.put("locale", locale);
			in.put("listAllConditions", listAllConditions);
			in.put("listSortFields", listSortFields);
			in.put("opts", opts);
			params.put("pagesize", ps);
			params.put("pagenum", pn);
			params.put("productStoreId", pd);
			params.put("isCustomer", ic);
			params.put("checkActiveStr", cs);
			params.put("isSellerStr", is);
			in.put("parameters", params);
			Map<String, Object> out = (Map<String, Object>) dispatcher.runSync("JQListProductPromo", in);
			EntityListIterator list = (EntityListIterator) out.get("listIterator");
			if(list != null){
				List<GenericValue> promotions = list.getCompleteList();
				results.put("promotions", promotions);
				list.close();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	public static Map<String, Object> getPromotionDetail(DispatchContext dcx, Map<String, Object> context){
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Delegator delegator = dcx.getDelegator();
		String productPromoId = (String) context.get("productPromoId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			EntityCondition promo = EntityCondition.makeCondition("productPromoId", productPromoId);
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> list = delegator.findList("ProductPromoExt",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productPromoId", productPromoId),
							EntityCondition.makeCondition("organizationPartyId", organizationPartyId),
							EntityUtil.getFilterByDateExpr()
							)), null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isEmpty(list)){
				return ServiceUtil.returnError("Empty");
			}
			if(UtilValidate.isEmpty(list)){
				return ServiceUtil.returnError("Empty");
			}
			GenericValue info = EntityUtil.getFirst(list);
			List<GenericValue> rl = delegator.findList("ProductPromoExtRule", EntityCondition.makeCondition("productPromoId", productPromoId), null, UtilMisc.toList("productPromoRuleId"), null, false);
			List<Map<String, Object>> rules = FastList.newInstance();
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			Set<String> cpFields = UtilMisc.toSet("productCode", "productName", "inputParamEnumId", "operatorEnumId", "condValue", "otherValue");
			cpFields.addAll(UtilMisc.toSet("note", "productPromoId", "productPromoRuleId", "productPromoCondSeqId"));

			Set<String> cCFields = UtilMisc.toSet("productCategoryId", "categoryName", "inputParamEnumId", "operatorEnumId", "condValue", "otherValue");
			cCFields.addAll(UtilMisc.toSet("note", "productPromoId", "productPromoRuleId", "productPromoCondSeqId"));

			Set<String> apFields = UtilMisc.toSet("productCode", "productName", "productPromoActionEnumId");
			apFields.addAll(UtilMisc.toSet("quantity", "amount", "productPromoId", "productPromoRuleId", "productPromoActionSeqId"));

			Set<String> aCFields = UtilMisc.toSet("productCategoryId", "categoryName", "productPromoActionEnumId");
			aCFields.addAll(UtilMisc.toSet("quantity", "amount", "productPromoId", "productPromoRuleId", "productPromoActionSeqId"));

			List<String> cFilter = UtilMisc.toList("productPromoCondSeqId");
			List<String> aFilter = UtilMisc.toList("productPromoActionSeqId");

			for(GenericValue r : rl){
				EntityCondition cc = EntityCondition.makeCondition(
						UtilMisc.toList(promo, EntityCondition.makeCondition("productPromoRuleId", r.getString("productPromoRuleId"))));
				List<GenericValue> condition = delegator.findList("ProductPromoExtCond", cc, null, cFilter, options, false);
				List<GenericValue> action = delegator.findList("ProductPromoExtAction", cc, null, aFilter, options, false);
				List<GenericValue> pcond = delegator.findList("ProductPromoExtCondProduct", cc, cpFields, cFilter, options, false);
				List<GenericValue> paction = delegator.findList("ProductPromoExtActionProduct", cc, apFields, aFilter, options, false);
				List<GenericValue> ccond = delegator.findList("ProductPromoExtCondCategory", cc, cCFields, cFilter, options, false);
				List<GenericValue> caction = delegator.findList("ProductPromoExtActionCategory", cc, null, aFilter, options, false);
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(r);
				o.put("productCondition", pcond);
				o.put("categoryCondition", ccond);
				o.put("productAction", paction);
				o.put("categoryAction", caction);
				o.put("condition", condition);
				o.put("action", action);
				rules.add(o);
			}
			List<GenericValue> roles = delegator.findList("ProductPromoExtRoleTypeAppl", promo, null, UtilMisc.toList("roleTypeId"), options, false);
			List<GenericValue> stores = delegator.findList("ProductPromoExtStoreAppl", promo, null, UtilMisc.toList("productStoreId"), options, false);
			results.put("info", info);
			results.put("rules", rules);
			results.put("stores", stores);
			results.put("roles", roles);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}
	public static Map<String, Object> getOrderPromotion(DispatchContext dcx, Map<String, Object> context){
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Delegator delegator = dcx.getDelegator();
		String productPromoId = (String) context.get("productPromoId");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			EntityCondition promo = EntityCondition.makeCondition("productPromoId", productPromoId);
			String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			List<GenericValue> list = delegator.findList("ProductPromo",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("productPromoId", productPromoId),
							EntityCondition.makeCondition("organizationPartyId", organizationPartyId),
							EntityUtil.getFilterByDateExpr()
							)), null, UtilMisc.toList("fromDate"), null, false);
			if(UtilValidate.isEmpty(list)){
				return ServiceUtil.returnError("Empty");
			}
			if(UtilValidate.isEmpty(list)){
				return ServiceUtil.returnError("Empty");
			}
			GenericValue info = EntityUtil.getFirst(list);
			List<GenericValue> rl = delegator.findList("ProductPromoRule", EntityCondition.makeCondition("productPromoId", productPromoId), null, UtilMisc.toList("productPromoRuleId"), null, false);
			List<Map<String, Object>> rules = FastList.newInstance();
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			Set<String> cpFields = UtilMisc.toSet("productCode", "productName", "inputParamEnumId", "operatorEnumId", "condValue", "otherValue");
			cpFields.addAll(UtilMisc.toSet("productPromoId", "productPromoRuleId", "productPromoCondSeqId"));

			Set<String> cCFields = UtilMisc.toSet("productCategoryId", "categoryName", "inputParamEnumId", "operatorEnumId", "condValue", "otherValue");
			cCFields.addAll(UtilMisc.toSet("productPromoId", "productPromoRuleId", "productPromoCondSeqId"));

			Set<String> apFields = UtilMisc.toSet("productCode", "productName", "productPromoActionEnumId");
			apFields.addAll(UtilMisc.toSet("quantity", "amount", "productPromoId", "productPromoRuleId", "productPromoActionSeqId"));

			Set<String> aCFields = UtilMisc.toSet("productCategoryId", "categoryName", "productPromoActionEnumId");
			aCFields.addAll(UtilMisc.toSet("quantity", "amount", "productPromoId", "productPromoRuleId", "productPromoActionSeqId"));

			List<String> cFilter = UtilMisc.toList("productPromoCondSeqId");
			List<String> aFilter = UtilMisc.toList("productPromoActionSeqId");

			for(GenericValue r : rl){
				EntityCondition cc = EntityCondition.makeCondition(
						UtilMisc.toList(promo, EntityCondition.makeCondition("productPromoRuleId", r.getString("productPromoRuleId"))));
				List<GenericValue> condition = delegator.findList("ProductPromoCond", cc, null, cFilter, options, false);
				List<GenericValue> action = delegator.findList("ProductPromoAction", cc, null, aFilter, options, false);
				List<GenericValue> pcond = delegator.findList("ProductPromoCondProduct", cc, cpFields, cFilter, options, false);
				List<GenericValue> paction = delegator.findList("ProductPromoActionProduct", cc, apFields, aFilter, options, false);
				List<GenericValue> ccond = delegator.findList("ProductPromoCondCategory", cc, cCFields, cFilter, options, false);
				List<GenericValue> caction = delegator.findList("ProductPromoActionCategory", cc, null, aFilter, options, false);
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(r);
				o.put("productCondition", pcond);
				o.put("categoryCondition", ccond);
				o.put("productAction", paction);
				o.put("categoryAction", caction);
				o.put("condition", condition);
				o.put("action", action);
				rules.add(o);
			}
			List<GenericValue> roles = delegator.findList("ProductPromoRoleTypeAppl", promo, null, UtilMisc.toList("roleTypeId"), options, false);
			List<GenericValue> stores = delegator.findList("ProductPromoStoreAppl", promo, null, UtilMisc.toList("productStoreId"), options, false);
			results.put("info", info);
			results.put("rules", rules);
			results.put("stores", stores);
			results.put("roles", roles);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return results;
	}

	public static Map<String, Object> createRegistrationPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		try{
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
				context.put("fromDate", fromDate);
				GenericValue prmo = delegator.makeValidValue("ProductPromoExtRegister", context);
				prmo.set("statusId", "PROMO_REGISTRATION_CREATED");
				prmo.create();
			}else{
				GenericValue prmo = delegator.findOne("ProductPromoExtRegister", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "fromDate", fromDate, "partyId", partyId), false);
				if(UtilValidate.isEmpty(prmo)){
					prmo.setFields(context);
					prmo.set("statusId", "PROMO_REGISTRATION_CREATED");
					prmo.create();
				}
			}
			MobileUtils.sendNotifyManager(dcx, context, "BSNewAgentPromosNotify", "customerPromosRegExh", "");
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}
	public static void createRegistrationPromotion(Delegator delegator, String partyId, String productPromoId, String productPromoRuleId, String statusId, Timestamp fromDate){
		try{
			if(UtilValidate.isEmpty(fromDate)){
				fromDate = UtilDateTime.nowTimestamp();
			}
			GenericValue prmo = delegator.makeValue("ProductPromoExtRegister");
			prmo.set("partyId", partyId);
			prmo.set("productPromoId", productPromoId);
			prmo.set("productPromoRuleId", productPromoRuleId);
			prmo.set("fromDate", fromDate);
			prmo.set("statusId", statusId);
			prmo.create();
		}catch(Exception e){
			Debug.log(e.getMessage());
		}
	}
	public static Map<String, Object> acceptRegistrationPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		try{
			List<GenericValue> list = delegator.findList("ProductPromoExtRegister", EntityCondition.makeCondition(
					UtilMisc.toList(
						EntityCondition.makeCondition("partyId", partyId),
						EntityCondition.makeCondition("productPromoId", productPromoId),
						EntityCondition.makeCondition("productPromoRuleId", productPromoRuleId),
//						EntityCondition.makeCondition("statusId","PROMO_REGISTRATION_CREATED"),
						EntityUtil.getFilterByDateExpr()
					)), null, null, null, false);
			boolean flag = false;
			for(GenericValue e : list){
				if(e.getString("statusId").equals("PROMO_REGISTRATION_ACCEPTED")
						&& e.getString("productPromoId").equals("productPromoId")){
					flag = true;
					continue;
				}
				e.set("thruDate", UtilDateTime.nowTimestamp());
				e.store();
			}
			if(!flag) createRegistrationPromotion(delegator, partyId, productPromoId, productPromoRuleId, "PROMO_REGISTRATION_ACCEPTED", null);
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> changeStatusRegPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		String statusId = (String) context.get("statusId");
		try{
			List<GenericValue> list = delegator.findList("ProductPromoExtRegister", EntityCondition.makeCondition(
					UtilMisc.toList(
							EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("productPromoId", productPromoId),
							EntityCondition.makeCondition("productPromoRuleId", productPromoRuleId),
//						EntityCondition.makeCondition("statusId","PROMO_REGISTRATION_CREATED"),
							EntityUtil.getFilterByDateExpr()
							)), null, null, null, false);
			boolean flag = false;
			for(GenericValue e : list){
				if(e.getString("statusId").equals("PROMO_REGISTRATION_ACCEPTED")
						&& e.getString("productPromoId").equals("productPromoId")){
					flag = true;
					continue;
				}
				e.set("thruDate", UtilDateTime.nowTimestamp());
				e.store();
			}
			if(!flag) createRegistrationPromotion(delegator, partyId, productPromoId, productPromoRuleId, statusId, null);
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> updateRegistrationPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		String resultEnumId = (String) context.get("resultEnumId");
		String url = (String) context.get("url");
		Long fd = (Long) context.get("fromDate");
		try{
			Timestamp fromDate = new Timestamp(fd);
			GenericValue prmo = delegator.findOne("ProductPromoExtRegister", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "fromDate", fromDate, "partyId", partyId), false);
			if(UtilValidate.isNotEmpty(prmo)){
				if(UtilValidate.isNotEmpty(resultEnumId)){
					prmo.set("resultEnumId", resultEnumId);
					prmo.set("statusId", "PROMO_REGISTRATION_COMPLETED");
				}
				if(UtilValidate.isNotEmpty(url)){
					prmo.set("url", url);
				}
				prmo.store();
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		return res;
	}
	public static Map<String, Object> deleteRegistrationPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		try{
			GenericValue prmo = delegator.findOne("ProductPromoExtRegister", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "fromDate", fromDate, "partyId", partyId), false);
			if (prmo == null) {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSRecordIsNotFound", locale));
			}
			if ("PROMO_REGISTRATION_CREATED".equals(prmo.getString("statusId"))) {
				prmo.set("statusId", "PROMO_REGISTRATION_CANCELLED");
				prmo.set("thruDate", UtilDateTime.nowTimestamp());
				prmo.store();
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSJustOnlyDeleteRecordWithStatusIsCreated", locale));
			}
		} catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
		}
		return res;
	}
	public static Map<String, Object> getPromotionRegistered(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		try{
			List<GenericValue> list = delegator.findList("ProductPromoExtRegister", 
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"))), null, UtilMisc.toList("fromDate"), null, false);
			List<Map<String, Object>> resList = FastList.newInstance();
			for(GenericValue e : list){
				Map<String, Object> o = FastMap.newInstance();
				o.putAll(e);
				GenericValue promo = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", e.getString("productPromoId")), false);
				o.put("productPromoId", promo.getString("productPromoId"));
				o.put("promoName", promo.getString("promoName"));
				resList.add(o);
			}
			res.put("results", resList);
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}
	@Deprecated
	public static Map<String, Object> getCustomerRegistered(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		try{
			Map<String, Object> customerRoute = CustomerServices.getStoreByRoadNew(dcx, context);
			List<Map<String, Object>> customers = (List<Map<String, Object>>) customerRoute.get("customers");
			if(UtilValidate.isNotEmpty(customers)){
				List<String> parties = FastList.newInstance();
				for(Map<String, Object> o : customers){
					String partyId = (String) o.get("partyIdTo");
					if(UtilValidate.isNotEmpty(partyId)){
						parties.add(partyId);
					}
				}
				Set<String> fields = UtilMisc.toSet("partyId", "groupName");
				EntityFindOptions options = new EntityFindOptions();
				options.setDistinct(true);
				List<GenericValue> list = delegator.findList("ProductPromoExtRegisterParty",
						EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, parties),
								EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"),
								EntityUtil.getFilterByDateExpr())), fields, UtilMisc.toList("groupName"), options, false);
				res.put("results", list);
			}
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}

	public static Map<String, Object> createRegistrationEvaluation(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> res = ServiceUtil.returnSuccess();
        String url = (String) context.get("url");
        String productPromoId = (String) context.get("productPromoId");
        String productPromoRuleId = (String) context.get("productPromoRuleId");
        String partyId = (String) context.get("partyId");
        String resultEnumId = (String) context.get("resultEnumId");
		try{
			Timestamp entryDate = UtilDateTime.nowTimestamp();
			context.put("entryDate", entryDate);
			context.put("createdBy", userLogin.get("userLoginId"));
			GenericValue prmo = delegator.makeValidValue("ProductPromoExtRegistrationEval", context);
			prmo.create();
			MobileUtils.sendNotifyManager(dcx, context, "BSNewAgentGradedNotify", "customerPromosRegExh", "");

            List<EntityCondition> conditions = FastList.newInstance();
            conditions.add(EntityCondition.makeCondition("productPromoId", productPromoId));
            conditions.add(EntityCondition.makeCondition("productPromoRuleId", productPromoRuleId));
            conditions.add(EntityCondition.makeCondition("partyId", partyId));
            List<GenericValue> promoS = delegator.findList("ProductPromoExtRegister", EntityCondition.makeCondition(conditions), null, null, null, false);
            if(UtilValidate.isNotEmpty(promoS)){
                for(GenericValue promoItem: promoS){
                    if(UtilValidate.isNotEmpty(resultEnumId)){
                        promoItem.set("resultEnumId", resultEnumId);
                        promoItem.set("statusId", "PROMO_REGISTRATION_COMPLETED");
                    }
                    if(UtilValidate.isNotEmpty(url)){
                        promoItem.set("url", url);
                    }
                    promoItem.store();
                }
            }
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}

	public static Map<String, Object> mGetListPromotionExt(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listProductPromoExtApplStore = FastList.newInstance();
		Integer page = 0;

		if (UtilValidate.isNotEmpty(context.get("viewIndex"))) {
			page = Integer.parseInt((String) context.get("viewIndex"));
		}
		Integer size = 10;
		if (UtilValidate.isNotEmpty(context.get("viewSize"))) {
			size = Integer.parseInt((String) context.get("viewSize"));
		}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		EntityFindOptions opts = new EntityFindOptions();
		try {
			// check permission
			boolean showData = false;
			boolean isRoleEmployee = false;
			OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);
			boolean hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_VIEW");
			boolean hasUpdatePermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_UPDATE") || securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "PRODPROMOTION_EXT_APPROVE");
			if (hasPermission) {
				showData = true;
				isRoleEmployee = true;
			} else {
				hasPermission = securityOlb.olbiusHasPermission(userLogin, null, "MODULE", "DIS_PRODPROMOTION");
			}
			if (!hasPermission) {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_sale_error, "BSYouHavenotViewPermission",locale));
			}
			String ownerId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			String productStoreId = (String) context.get("productStoreId");

			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-createdDate");
			}

			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("organizationPartyId", ownerId)));
			listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", productStoreId)));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDateAppl", "thruDateAppl"));
			showData = true;
			if (!isRoleEmployee || !hasUpdatePermission) listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
			if (showData) {
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productPromoId");
				//listSelectFields.add("productPromoTypeId");
				listSelectFields.add("promoName");
				listSelectFields.add("createdDate");
				listSelectFields.add("fromDate");
				listSelectFields.add("thruDate");
				listSelectFields.add("statusId");
				listSelectFields.add("organizationPartyId");
				opts.setDistinct(true);
				listIterator = EntityMiscUtil.processIterator(parameters,successResult,delegator,"ProductPromoExtApplStore",EntityCondition.makeCondition(listAllConditions),null,UtilMisc.toSet(listSelectFields),listSortFields, opts);
				listProductPromoExtApplStore = listIterator.getCompleteList();
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductPromoExt service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listPromotionExt", listProductPromoExtApplStore);
		successResult.put("totalRows", Integer.parseInt((String)successResult.get("TotalRows")));
		successResult.remove("TotalRows");
		return successResult;
	}

	public static Map<String, Object> mGetPromotionExtInfoDetail(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> res = ServiceUtil.returnSuccess();
		Delegator delegator = dpc.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String productPromoId = (String) context.get("productPromoId");
		LocalDispatcher dispatcher = dpc.getDispatcher();
		Map<String, Object> resultsDataPromotionExtInfoMap = FastMap.newInstance();

		EntityFindOptions opts = new EntityFindOptions();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		List<Map<String, Object>> listProPromotionExtRule = FastList.newInstance();
		List<String> listSortFields = FastList.newInstance();
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityListIterator listIterator = null;
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			resultsDataPromotionExtInfoMap.put("productPromoId", productPromoId);
			GenericValue productPromo = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", productPromoId), false);
			if(UtilValidate.isNotEmpty(productPromo)){
				resultsDataPromotionExtInfoMap.put("promoName", productPromo.getString("promoName"));
				String currentStatusId = (String) productPromo.get("statusId");
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", currentStatusId), false);
				//String statusDes = (String) statusItem.get("description");
				String statusId = (String) statusItem.get("statusId");
				if(UtilValidate.isNotEmpty(statusItem)){
					if ("PROMO_ACCEPTED".equals(statusId)){
						String statusDesCV = (String) UtilProperties.getMessage(resource, "MbAccepted", locale);
						resultsDataPromotionExtInfoMap.put("statusDes", statusDesCV);
					}
				}
				resultsDataPromotionExtInfoMap.put("promoText", productPromo.get("promoText"));
				resultsDataPromotionExtInfoMap.put("fromDate", productPromo.get("fromDate"));
				resultsDataPromotionExtInfoMap.put("thruDate", productPromo.get("thruDate"));
			}
			listAllConditions.add(EntityCondition.makeCondition("productPromoId", productPromoId));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			String storeName = "";
			String productStoreId = "";
			List<GenericValue> productStorePromoAppl = delegator.findList("ProductStorePromoExtAppl", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, UtilMisc.toList("sequenceNum", "productPromoId"), null, false);
			if(UtilValidate.isNotEmpty(productStorePromoAppl)){
				for(GenericValue prodStorePromoAppl: productStorePromoAppl){
					Map<String, Object> tmpMapStoreName = FastMap.newInstance();
					GenericValue productStore = prodStorePromoAppl.getRelatedOne("ProductStore", true);
					if(UtilValidate.isNotEmpty(productStore.get("storeName"))){
						storeName += (String) productStore.get("storeName") + ", ";
					}else{
						storeName += (String) productStore.get("productStoreId") + ",";
					}
				}
				if(storeName.length()>1){
					storeName = storeName.substring(0,storeName.length() - 2);
				}
				resultsDataPromotionExtInfoMap.put("storeName", storeName);
			}
			List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoExtRule", UtilMisc.toMap("productPromoId", productPromoId), UtilMisc.toList("ruleName"), false);
			if (UtilValidate.isNotEmpty(productPromoRules)){
				for (GenericValue productPromoRule: productPromoRules ){
					Map<String, Object> resultsProPromoExtRuleDescMap = FastMap.newInstance();
					resultsProPromoExtRuleDescMap.put("ruleName", productPromoRule.get("ruleName"));
					resultsProPromoExtRuleDescMap.put("ruleText", productPromoRule.get("ruleText"));
					resultsProPromoExtRuleDescMap.put("productPromoRuleId", productPromoRule.get("productPromoRuleId"));
					listProPromotionExtRule.add(resultsProPromoExtRuleDescMap);
				}
			}
			res.put("resultsDataPromotionExtInfoMap", resultsDataPromotionExtInfoMap);
			res.put("listProPromotionExtRule", listProPromotionExtRule);
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}

		return res;
	}
	public static Map<String, Object> mcreateRegistrationPromotion(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		String partyId = (String) context.get("partyId");
		String productPromoId = (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		String fromDateStr = (String) context.get("fromDateStr");
		String thruDateStr = (String) context.get("thruDateStr");
		Timestamp fromDateParam = null;
		Timestamp thruDateParam = null;
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		try{
			if(UtilValidate.isEmpty(fromDateStr)){
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
				context.put("fromDate", nowTimestamp);
			}else {
				Long fromDateL = Long.parseLong(fromDateStr);
				fromDateParam = new Timestamp(fromDateL);
				context.put("fromDate", fromDateParam);
			}
			if(UtilValidate.isNotEmpty(thruDateStr)){
				Long thruDateL = Long.parseLong(thruDateStr);
				thruDateParam = new Timestamp(thruDateL);
				context.put("thruDate", thruDateParam);
			}
			if(UtilValidate.isNotEmpty(productPromoRuleId)){
				GenericValue checkProductPromoRuleId = delegator.findOne("ProductPromoExtRule", UtilMisc.toMap("productPromoId",productPromoId,"productPromoRuleId", productPromoRuleId),false);
				if(UtilValidate.isEmpty(checkProductPromoRuleId)){
					return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSproductPromoIdWrong", locale));
				}
			}
			listAllConditions.add(EntityCondition.makeCondition("productPromoId",EntityOperator.EQUALS, productPromoId));
			listAllConditions.add(EntityCondition.makeCondition("productPromoRuleId",EntityOperator.EQUALS, productPromoRuleId));
			listAllConditions.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
			List<GenericValue> prmo = delegator.findList("ProductPromoExtRegister",
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			if(UtilValidate.isNotEmpty(prmo)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "BSExistData", locale));
			}
			GenericValue prmoCreate = delegator.makeValidValue("ProductPromoExtRegister", context);
			prmoCreate.set("statusId", "PROMO_REGISTRATION_CREATED");
			prmoCreate.create();
			MobileUtils.sendNotifyManager(dcx, context, "BSNewAgentPromosNotify", "customerPromosRegExh", "");
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}
	//get list ProductPromoExtRegisterParty To mark
	public static Map<String, Object> mgetProductPromoExtRegisterMark(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		String partyId = (String) context.get("partyId");
		try{
			Set<String> fields = UtilMisc.toSet("productPromoId", "productPromoRuleId", "ruleName");
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			//listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"));
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PROMO_REGISTRATION_CREATED"));
			listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PROMO_REGISTRATION_CANCELLED"));
			listAllConditions.add(EntityCondition.makeCondition("productPromoTypeId", "PROMO_EXHIBITION"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> listProPromotionExtRegisterAccept = delegator.findList("ProductPromoExtRegisterPartyRule",
					EntityCondition.makeCondition(listAllConditions), fields, UtilMisc.toList("productPromoId"), options, false);
			res.put("listProPromotionExtRegisterAccept", listProPromotionExtRegisterAccept);

			listAllConditions.clear();
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId", "PROD_PROMOEXT_REG_EVAL"));
			List<GenericValue> listResultsEnumeration = delegator.findList("Enumeration",
					EntityCondition.makeCondition(listAllConditions), UtilMisc.toSet("enumId", "enumTypeId", "description"), null, options, false);
			res.put("listResultsEnumeration",listResultsEnumeration);
		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}

	//get list ProductPromoExtRegisterAccepted to Register
	public static Map<String, Object> mgetProductPromoExtAccepted(DispatchContext dcx, Map<String, Object> context){
		Delegator delegator = dcx.getDelegator();
		Map<String, Object> res = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = FastList.newInstance();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<String> distributorIds = FastList.newInstance();
		List<EntityCondition> listConds = FastList.newInstance();
		EntityListIterator listIterator = null;
		List<GenericValue> listProPromotionExtAccepted = FastList.newInstance();
		Integer page = 0;
		if (UtilValidate.isNotEmpty(context.get("viewIndex"))) {
			page = Integer.parseInt((String) context.get("viewIndex"));
		}
		Integer size = 10;
		if (UtilValidate.isNotEmpty(context.get("viewSize"))) {
			size = Integer.parseInt((String) context.get("viewSize"));
		}
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");

		try{
			String userLoginId = (String) userLogin.get("userLoginId");
			listConds.add(EntityCondition.makeCondition("partyId", userLoginId));
			listConds.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "PARTY_ENABLED"));
			List<GenericValue> partySalesman = delegator.findList("PartySalesman", EntityCondition.makeCondition(listConds), null, null, null, false);
			if (UtilValidate.isNotEmpty(partySalesman)) {
				distributorIds = EntityUtil.getFieldListFromEntityList(partySalesman, "distributorId", true);
			}
			Set<String> fields = UtilMisc.toSet("productPromoId", "productPromoRuleId", "ruleName");
			EntityFindOptions options = new EntityFindOptions();
			options.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, distributorIds));
			listAllConditions.add(EntityCondition.makeCondition("statusId", "PROMO_ACCEPTED"));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());

			listIterator = EntityMiscUtil.processIterator(parameters,res,delegator,"ProductPromoExtAndProductPromoExtRuleAndAppStore",EntityCondition.makeCondition(listAllConditions),null,fields,UtilMisc.toList("productPromoId"), options);
			listProPromotionExtAccepted = listIterator.getCompleteList();
			res.put("listProPromotionExtAccepted", listProPromotionExtAccepted);
			res.put("totalRows", Integer.parseInt((String)res.get("TotalRows")));
			res.remove("TotalRows");

		}catch(Exception e){
			Debug.log(e.getMessage());
			return ServiceUtil.returnSuccess();
		}
		return res;
	}

}
