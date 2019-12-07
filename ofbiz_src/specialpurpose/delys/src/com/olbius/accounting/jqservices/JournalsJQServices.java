package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class JournalsJQServices {
	public static final String module = JournalsJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListJournals(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String[] organizationPartyIds = parameters.get("organizationPartyId");
    	String organizationPartyId = null;
    	if(UtilValidate.isNotEmpty(organizationPartyIds)){
    		organizationPartyId = organizationPartyIds[0];
    	}
    	if(UtilValidate.isNotEmpty(organizationPartyId)){
    		Map<String, String> mapCondition = new HashMap<String, String>();
        	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
        	EntityCondition cond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);
        	listAllConditions.add(cond);
        	try {
        		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
        		listIterator = delegator.find("GlJournal", tmpConditon, null, null, listSortFields, opts);
    		} catch (Exception e) {
    			String errMsg = "Fatal error calling jqGetListJournals service: " + e.toString();
    			Debug.logError(e, errMsg, module);
    		}
        	successResult.put("listIterator", listIterator);
    	}
    	
    	return successResult;
    }
}
