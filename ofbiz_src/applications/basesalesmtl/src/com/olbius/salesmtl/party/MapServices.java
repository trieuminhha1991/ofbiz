package com.olbius.salesmtl.party;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.salesmtl.util.SupUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.product.ProductStoreWorker;
import com.olbius.basesales.util.SalesPartyUtil;
public class MapServices {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCustomerBoundary(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException, GenericEntityException{
		Delegator delegator = dpc.getDelegator();
		LocalDispatcher dispatcher = dpc.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyId = userLogin.getString("partyId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		StringBuffer tmp = new StringBuffer();
		tmp.append(userLoginId).append("-").append("CUSTOMER");;
		if(UtilValidate.isNotEmpty(stateProvinceGeoId)){
			tmp.append("-").append(stateProvinceGeoId);
			StringBuilder tmp2 = new StringBuilder();
			String key = tmp2.append(userLoginId).append("-").append("ORG").toString();
			List<String> gr = (List<String>) MapProcess.getCache(key);
			if(gr != null){
				context.put("sups", gr);
			}else{
				Map<String, Object> input = FastMap.newInstance();
				gr = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
				if(UtilValidate.isNotEmpty(gr)){
					input.put("userLogin", userLogin);
					input.put("partyId", gr.get(0));
					Map<String, Object> out = dispatcher.runSync("getOrganizationUnit", input);
					List<Map<String, Object>> listReturn = (List<Map<String, Object>>) out.get("listReturn");
					List<String> org = FastList.newInstance();
					if(UtilValidate.isNotEmpty(listReturn)){
						for(Map<String, Object> o : listReturn){
							String cur = (String) o.get("partyId");
							if(!org.contains(cur)){
								org.add(cur);
							}
						}
					}
					MapProcess.setCache(key, org);
					context.put("sups", org);
				}
			}
		}
		EntityCondition conditions = SupUtil.getMapCustomerCondition(dpc, context);
		Map<String, Object> boundary = MapProcess.getBoundary(delegator, tmp.toString(), conditions);
		return boundary;
	}
	public static Map<String, Object> getContactBoundary(DispatchContext dpc, Map<String, Object> context) throws GenericServiceException{
		Delegator delegator = dpc.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
		StringBuffer tmp = new StringBuffer();
		tmp.append(userLoginId).append("-").append("CONTACT");
		if(UtilValidate.isNotEmpty(stateProvinceGeoId)){
			tmp.append("-").append(stateProvinceGeoId);
		}
		EntityCondition conditions = SupUtil.getMapContactCondition(dpc, context);
		Map<String, Object> boundary = MapProcess.getBoundary(delegator, tmp.toString(), conditions);
		return boundary;
	}

	public static Map<String, Object> loadAgentHistoryDetail(DispatchContext dpc, Map<String, Object> context){
		Map<String, Object> ret = ServiceUtil.returnSuccess();
		Map<String, Object> res = FastMap.newInstance();
		String partyId = (String) context.get("partyId");
		Delegator delegator = dpc.getDelegator();
		LocalDispatcher dispatcher = dpc.getDispatcher();
		EntityFindOptions opts = new EntityFindOptions();
		Locale locale = (Locale) context.get("locale");
		opts.setMaxRows(1);
		opts.setLimit(1);
		try {
			try {
				Map<String, Object> out = dispatcher.runSync("loadAgentInfo", context);
				res.putAll(out);
			} catch (GenericServiceException e) {
				Debug.log(e.getMessage());
			}
			List<GenericValue> his = delegator.findList("RouteHistory", EntityCondition.makeCondition("partyIdTo", partyId), null, UtilMisc.toList("-fromDate"), opts, false);
			GenericValue last = EntityUtil.getFirst(his);
			if(last != null){
				Timestamp fromDate = last.getTimestamp("fromDate");
				res.put("fromDate", fromDate);
			}
			List<GenericValue> or = delegator.findList("OrderHeaderAndOrderRoleFromTo",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("customerId", partyId),
							EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"))), null, UtilMisc.toList("-orderDate"), opts, false);
			GenericValue lastOr = EntityUtil.getFirst(or);
			if(lastOr != null){
				Timestamp orderDate = lastOr.getTimestamp("orderDate");
				res.put("orderDate", orderDate);
			}
			List<String> sms = SalesPartyUtil.getSalesExecutiveIdsOrderByCustomer(delegator, partyId);
			if(UtilValidate.isNotEmpty(sms)){
				String partyIdFrom = sms.get(0);
				GenericValue sm = delegator.findOne("PartyFullNameDetail", UtilMisc.toMap("partyId", partyIdFrom), false);
				res.put("salesman", sm.getString("fullName"));
			}
			List<String> stores = ProductStoreWorker.getProductStoreIdContainCustomer(delegator, partyId);
			if(UtilValidate.isNotEmpty(stores)){
				List<Map<String, Object>> pstres = FastList.newInstance();
				for(String s : stores){
					GenericValue ps = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", s), false);
					if(ps != null){
						Map<String, Object> o = UtilMisc.toMap("productStoreId", s , "storeName", ps.getString("storeName"));
						pstres.add(o);
					}
				}
				res.put("stores", pstres);
			}
			List<GenericValue> proRegis = delegator.findList("ProductPromoExtRegisterRuleDetail",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyId", partyId),
							EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_ACCEPTED"),
									EntityCondition.makeCondition("statusId", "PROMO_REGISTRATION_CREATED")), EntityOperator.OR),
							EntityUtil.getFilterByDateExpr())), UtilMisc.toSet("productPromoId", "ruleText", "ruleName", "statusId", "promoName", "resultEnumId"), UtilMisc.toList("-fromDate"), opts, false);
			if(UtilValidate.isNotEmpty(proRegis)){
				List<Map<String, Object>> ppe = FastList.newInstance();
				GenericValue s;
				for(GenericValue p : proRegis){
					Map<String, Object> o = FastMap.newInstance();
					o.putAll(p);
					s = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", p.getString("statusId")), true);
					String statusDescription = (String) s.get("description", locale);
					o.put("statusDescription", statusDescription);
					String resultEnumId = p.getString("resultEnumId");
					if(UtilValidate.isNotEmpty(resultEnumId)){
						s = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", resultEnumId), true);
						String resultEnumDescription = (String) s.get("description", locale);
						o.put("resultEnumDescription", resultEnumDescription);
					}
					ppe.add(o);
				}
				res.put("promotions", ppe);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		ret.put("result", res);
		return ret;
	}
}
