package com.olbius.basepo.party;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
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

import com.olbius.common.util.EntityMiscUtil;
import com.olbius.services.JqxWidgetSevices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PartyServices {
	public static final String module = PartyServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPartyAndGroupSupplier(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	try {
			/*List<EntityCondition> condsOr = FastList.newInstance();
			condsOr.add(EntityCondition.makeCondition("roleTypeId", "SUPPLIER"));
			condsOr.add(EntityCondition.makeCondition("roleTypeId", "SUPPLIER_AGENT"));
			listAllConditions.add(EntityCondition.makeCondition(condsOr, EntityOperator.OR));*/
    		listAllConditions.add(EntityCondition.makeCondition("roleTypeId", "SUPPLIER"));
			
			listIterator = delegator.find("PartyAndRoleFullNameSimple", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetListPartyAndGroupSupplier service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPartyCurrencyConfig(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<GenericValue> listOrders = FastList.newInstance();
		try {
			listSortFields.add("fromDate DESC");
			listOrders = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "PartyCurrencyDetail", 
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);

		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetPartyCurrencyConfig service: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		successResult.put("listIterator", listOrders);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
    public static Map<String,Object> createMultiPartyCurrency(DispatchContext ctx, Map<String, Object> context){
	    List<Map<String, Object>> listParties = null;
	    List<Map<String, Object>> listCurrencyUoms = null;
	    String strListPty = (String)context.get("listParties");
	    String strListRole = (String)context.get("listCurrencyUoms");
	    String fromDateStr = null;
	    String thruDateStr = null;
	    if (UtilValidate.isNotEmpty(context.get("thruDate"))) {
	    	thruDateStr = (String)context.get("thruDate");
		}
	    if (UtilValidate.isNotEmpty(context.get("fromDate"))) {
	    	fromDateStr = (String)context.get("fromDate");
	    } else {
	    	Long tmp = UtilDateTime.nowTimestamp().getTime();
	    	fromDateStr = tmp.toString();
	    }
	    
    	try {
			listParties = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strListPty);
			listCurrencyUoms = (List<Map<String, Object>>) JqxWidgetSevices.convert("java.util.List", strListRole);
		} catch (ParseException e) {
			return ServiceUtil.returnError("OLBIUS: createMultiPartyCurrency error when JqxWidgetSevices.convert ! " + e.toString());
		}
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		for (Map<String, Object> pt : listParties) {
			String partyId = (String)pt.get("partyId");
			for (Map<String, Object> rl : listCurrencyUoms) {
				String currencyUomId = (String)rl.get("currencyUomId");
				Map<String, Object> map = FastMap.newInstance();
				map.put("partyId", partyId);
				map.put("currencyUomId", currencyUomId);
				map.put("fromDate", fromDateStr);
				map.put("thruDate", thruDateStr);
				map.put("userLogin", userLogin);
				try {
					dispatcher.runSync("createPartyCurrency", map);
				} catch (GenericServiceException e) {
					return ServiceUtil.returnError("OLBIUS: createPartyCurrency error! " + e.toString());
				}
			}
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	
	public static Map<String,Object> createPartyCurrency(DispatchContext ctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String currencyUomId = (String)context.get("currencyUomId");
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
			Delegator delegator = ctx.getDelegator();
			GenericValue map = delegator.makeValue("PartyCurrency");
			map.put("partyId", partyId);
			map.put("currencyUomId", currencyUomId);
			map.put("fromDate", fromDateStp);
			map.put("thruDate", thruDateStp);
			try {
				delegator.createOrStore(map);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: createPartyCurrency error! " + e.toString());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String,Object> updatePartyCurrency(DispatchContext ctx, Map<String, Object> context){
		String partyId = (String)context.get("partyId");
		String currencyUomId = (String)context.get("currencyUomId");
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
		EntityCondition cond2 = EntityCondition.makeCondition("currencyUomId", EntityOperator.EQUALS, currencyUomId);
		conds.add(cond1);
		conds.add(cond2);
		if (UtilValidate.isNotEmpty(fromDateStp)) {
			EntityCondition cond3 = EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDateStp);
			conds.add(cond3);
			try {
				obj = delegator.findOne("PartyCurrency", false, conds);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findOne PartyCurrency: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
		} else {
			List<GenericValue> listPartyCurrency = FastList.newInstance();
			conds.add(EntityUtil.getFilterByDateExpr());
			try {
				listPartyCurrency = delegator.findList("PartyCurrency", EntityCondition.makeCondition(conds), null, null, null, false);
			} catch (GenericEntityException e) {
				String errMsg = "OLBIUS: Fatal error when findList PartyCurrency: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			if (!listPartyCurrency.isEmpty()) obj = listPartyCurrency.get(0);
		}
		if (UtilValidate.isNotEmpty(obj)) {
			obj.put("thruDate", thruDateStp);
			try {
				delegator.store(obj);
			} catch (GenericEntityException e) {
				return ServiceUtil.returnError("OLBIUS: updatePartyCurrency error! " + e.toString());
			}
		}
		
		return ServiceUtil.returnSuccess();
	}
}
