package com.olbius.acc.setting;

import com.olbius.basesales.contact.ContactMechWorker;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.service.annotations.Service;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class GlobalSettingServices {
	
	public static final String module = GlobalSettingServices.class.getName();
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListInvoiceItemTypeGLA(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("description");
    		}
    		listIterator = delegator.find("InvoiceItemTypeAndGlAccountDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListInvoiceItemTypeGLA service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getListBankConversion(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    	    List<GenericValue> listIterator = EntityMiscUtil.processIteratorToList(parameters, successResult, delegator, "BankConversion", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
    		successResult.put("listIterator", listIterator);
    	} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListInvoiceItemTypeGLA service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetPrimaryAddressByParty(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	try {
    		if (parameters.containsKey("partyId") && parameters.get("partyId").length > 0) {
    			String partyId = parameters.get("partyId")[0];
    			if (UtilValidate.isNotEmpty(partyId)) {
    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "contactMechTypeId", "POSTAL_ADDRESS")));
    				listAllConditions.add(EntityUtil.getFilterByDateExpr());
    				listAllConditions.add(EntityUtil.getFilterByDateExpr("purposeFromDate", "purposeThruDate"));
    				Set<String> selectFields = new HashSet<String>();
    				selectFields.add("contactMechId");
    				selectFields.add("toName");
    				selectFields.add("attnName");
    				selectFields.add("address1");
    				selectFields.add("address2");
    				selectFields.add("city");
    				selectFields.add("stateProvinceGeoId");
    				selectFields.add("postalCode");
    				selectFields.add("countryGeoId");
    				selectFields.add("districtGeoId");
    				selectFields.add("wardGeoId");
    				selectFields.add("fromDate");
    				opts.setDistinct(true);
    				
    				Map<String, Object> listConditionAfterProcess = SalesUtil.processSplitListAllCondition(delegator, listAllConditions, "PartyContactDetailByPurpose");
    				List<EntityCondition> listAllConditionOnIn = (List<EntityCondition>) listConditionAfterProcess.get("listAllConditionOnIn");
    				List<Map<String, Object>> listMapConditionOutOf = (List<Map<String, Object>>) listConditionAfterProcess.get("listMapConditionOutOf");
    				
    				if (UtilValidate.isEmpty(listSortFields)) {
    					listSortFields.add("-fromDate");
    				}
    				
    				List<GenericValue> listShippingContactMech = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(listAllConditionOnIn, EntityOperator.AND), selectFields, listSortFields, opts, false);
    				if (listShippingContactMech != null) {
						for (GenericValue shippingAddress : listShippingContactMech) {
							Map<String, Object> itemMap = FastMap.newInstance();
							itemMap.put("contactMechId", shippingAddress.get("contactMechId"));
							itemMap.put("toName", shippingAddress.get("toName"));
							itemMap.put("attnName", shippingAddress.get("attnName"));
							itemMap.put("address1", shippingAddress.get("address1"));
							itemMap.put("address2", shippingAddress.get("address2"));
							itemMap.put("city", shippingAddress.get("city"));
							itemMap.put("stateProvinceGeoId", shippingAddress.get("stateProvinceGeoId"));
							itemMap.put("postalCode", shippingAddress.get("postalCode"));
							
							String countryGeoId = shippingAddress.getString("countryGeoId");
							itemMap.put("countryGeoId", countryGeoId);
							itemMap.put("countryGeoName", countryGeoId);
							
							String stateProvinceGeoId = shippingAddress.getString("stateProvinceGeoId");
							itemMap.put("stateProvinceGeoId", stateProvinceGeoId);
							itemMap.put("stateProvinceGeoName", stateProvinceGeoId);
							
							String districtGeoId = shippingAddress.getString("districtGeoId");
							itemMap.put("districtGeoId", districtGeoId);
							itemMap.put("districtGeoName", districtGeoId);
							
							String wardGeoId = shippingAddress.getString("wardGeoId");
							itemMap.put("wardGeoId", wardGeoId);
							itemMap.put("wardGeoName", wardGeoId);
							
							itemMap.put("countryGeoName", ContactMechWorker.getGeoName(delegator, countryGeoId));
							itemMap.put("stateProvinceGeoName", ContactMechWorker.getGeoName(delegator, stateProvinceGeoId));
							itemMap.put("districtGeoName", ContactMechWorker.getGeoName(delegator, districtGeoId));
							itemMap.put("wardGeoName", ContactMechWorker.getGeoName(delegator, wardGeoId));
							listIterator.add(itemMap);
						}
						
						if (UtilValidate.isNotEmpty(listMapConditionOutOf)) {
							listIterator = EntityMiscUtil.filterMapFromMapCond(listIterator, listMapConditionOutOf);
						}
						if (UtilValidate.isNotEmpty(listSortFields)) {
							listIterator = EntityMiscUtil.sortList(listIterator, listSortFields);
						}
					}
    			}
    		}
    	} catch (Exception e) {
    		String errMsg = "Fatal error calling jqGetPrimaryAddressByParty service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListConversions(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		Locale locale = (Locale) context.get("locale");
		List<Map<String,Object>> listRs = FastList.newInstance();
		Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
		try {
			List<GenericValue> listGv = EntityMiscUtil.processIteratorToList(parameters, resultSuccess, delegator, "UomConversionDatedView", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("uomTypeId","CURRENCY_MEASURE"), EntityCondition.makeCondition("uomTypeToId","CURRENCY_MEASURE")), EntityJoinOperator.AND), null, null, listSortFields, opts);
			if(UtilValidate.isNotEmpty(listGv)){
				for(GenericValue gv : listGv){
					Map<String,Object> mapGv = FastMap.newInstance();
					mapGv.put("uomId", gv.getString("uomId"));
					mapGv.put("uomIdTo", gv.getString("uomIdTo"));
					mapGv.put("purposeEnumId", gv.getString("purposeEnumId"));
					GenericValue cost = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", gv.getString("uomId")));
					mapGv.put("uomIdDes",(UtilValidate.isNotEmpty(cost) ? cost.get("description",locale) : ""));
					GenericValue off = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", gv.getString("uomIdTo")));
					mapGv.put("uomIdToDes",(UtilValidate.isNotEmpty(off) ? off.get("description",locale) : "") );
					mapGv.put("fromDate", gv.getTimestamp("fromDate"));
					mapGv.put("thruDate", gv.getTimestamp("thruDate"));
					mapGv.put("conversionFactor", gv.getDouble("conversionFactor"));
					mapGv.put("purchaseExchangeRate", gv.getDouble("purchaseExchangeRate"));
                    mapGv.put("sellingExchangeRate", gv.getDouble("sellingExchangeRate"));
    				GenericValue bank = delegator.findOne("BankConversion", UtilMisc.toMap("bankId", gv.get("bankId")), false);
					if(bank != null){
                        mapGv.put("bankId", bank.get("bankId"));
                        mapGv.put("bankName", bank.get("bankName"));
					}
					listRs.add(mapGv);
				}
			}
			listRs = EntityMiscUtil.filterMap(listRs, listAllConditions);
			listRs = EntityMiscUtil.sortList(listRs, listSortFields);
			resultSuccess.put("listIterator", listRs);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListConversions service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return resultSuccess;
	}

	public static Map<String, Object> updateFXConversion(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
        String uomId = (String) context.get("uomId");
        String uomIdTo = (String) context.get("uomIdTo");
        String fromDateStr = (String) context.get("fromDate");
        String thruDateStr = (String) context.get("thruDate");
        Timestamp thruDate = null;
        if(UtilValidate.isNotEmpty(thruDateStr)) {
            thruDate = new Timestamp(Long.valueOf(thruDateStr));
        }
        Timestamp fromDate = new Timestamp(Long.valueOf(fromDateStr));
        try {
            GenericValue fxConversion = delegator.findOne("UomConversionDated", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo, "fromDate", fromDate), false);
            fxConversion.set("bankId", context.get("bankId"));
            fxConversion.set("conversionFactor", context.get("conversionFactor"));
            fxConversion.set("purchaseExchangeRate", context.get("purchaseExchangeRate"));
            fxConversion.set("sellingExchangeRate", context.get("sellingExchangeRate"));
            fxConversion.set("purposeEnumId", context.get("purposeEnumId"));
            fxConversion.set("thruDate", thruDate);
            fxConversion.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Cannot Update fxConversion");
        }
        return resultSuccess;
    }

    public static Map<String, Object> closeFXConversion(DispatchContext ctx, Map<String, Object> context) {
        String uomId = (String) context.get("uomId");
        String uomIdTo = (String) context.get("uomIdTo");
        String fromDateStr = (String) context.get("fromDate");
        Timestamp fromDate = new Timestamp(Long.valueOf(fromDateStr));
        Delegator delegator = ctx.getDelegator();
        Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
        try {
            GenericValue fxConversion = delegator.findOne("UomConversionDated", UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo, "fromDate", fromDate), false);
            fxConversion.set("thruDate", UtilDateTime.nowTimestamp());
            fxConversion.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("cannot close FX Conversion");
        }
        return resultSuccess;
    }
}
