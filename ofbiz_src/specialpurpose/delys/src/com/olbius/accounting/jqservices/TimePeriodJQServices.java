package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class TimePeriodJQServices {
	public static final String module = TimePeriodJQServices.class.getName();
	
	@SuppressWarnings("unchecked")
    /** Method to get the period type.  These are identified in GroupPeriodType with enumTypeId "FISCAL_ACCOUNT". */
    public static List<String> getPeriodTypeIds(Delegator delegator) throws GenericEntityException {
        List<String> typeIds = FastList.newInstance();
        List<GenericValue> groupPeriodTypes = delegator.findByAnd("PeriodType", UtilMisc.toMap("groupPeriodTypeId", "FISCAL_ACCOUNT"), null, true);
        for (GenericValue groupPeriodType : groupPeriodTypes) {
            typeIds.add(groupPeriodType.getString("periodTypeId"));
        }
        return typeIds;
    }		
	
	@SuppressWarnings("unchecked")
    /** Method to get the period type.  These are identified in GroupPeriodType with enumTypeId "FISCAL_ACCOUNT". */
    public static List<String> getPartiesAcctgPreferenceIds(Delegator delegator) throws GenericEntityException {
        List<String> partyIds = FastList.newInstance();
        List<GenericValue> partyAcctgPreferenceList = delegator.findByAnd("PartyAcctgPreference", null, null, true);
        for (GenericValue partyAcctgPreference : partyAcctgPreferenceList) {
        	partyIds.add(partyAcctgPreference.getString("partyId"));
        }
        return partyIds;
    }	
	
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListPartiesAcctgPreference(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	EntityCondition partyList = EntityCondition.makeCondition("partyId", EntityOperator.IN, getPartiesAcctgPreferenceIds(delegator));
    	listAllConditions.add(partyList);
    	try {
    		listIterator = delegator.find("PartyNameView", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getListParty service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
		
	 @SuppressWarnings("unchecked")
		public static Map<String, Object> jqListCustomTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	EntityCondition periodTypeList = EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, getPeriodTypeIds(delegator));
	    	listAllConditions.add(periodTypeList);   	
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		EntityCondition tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("CustomTimePeriod", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListCustomTimePeriod service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 
	 @SuppressWarnings("unchecked")
		public static Map<String, Object> jqListCustomTimePeriodOrganization(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	EntityCondition periodTypeList = EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, getPeriodTypeIds(delegator));
	    	listAllConditions.add(tmpConditon);
	    	listAllConditions.add(periodTypeList);   	
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("CustomTimePeriod", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListCustomTimePeriod service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 
	 public static Map<String, Object> jqListOpenTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
	    	mapCondition.put("isClosed", "N");
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("CustomTimePeriod", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListOpenTimePeriod service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 public static Map<String, Object> jqListClosedTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
	    	mapCondition.put("isClosed", "Y");
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("CustomTimePeriod", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListClosedTimePeriod service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }

}
