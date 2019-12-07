package com.olbius.accounting.jqservices;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;


public class GLAccountTypeDefaultJQServices {
	public static final String module = GLAccountTypeDefaultJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getGLAccountTypeDedault(DispatchContext ctx, Map<String, ? extends Object> context) {
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
    	listAllConditions.add(tmpConditon);
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("GlAccountTypeDefaultAndDetail", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getGLAccountTypeDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getGLAccountTypeNotDedault(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String organizationPartyId =  (String)parameters.get("organizationPartyId")[0];
    	
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		
    		List<GenericValue> listPaymentOfOrganization = delegator.findList("GlAccountTypeDefaultAndDetail",EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
    		List<String> listPaymentUsed = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
    			for(GenericValue var  : listPaymentOfOrganization){
    				listPaymentUsed.add(var.getString("glAccountTypeId"));
    			}
    		}
    		listIterator = delegator.find("GlAccountType",EntityCondition.makeCondition("glAccountTypeId",EntityJoinOperator.NOT_IN,UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList("") ), null, null, listSortFields, opts);
    		if(pagesize > listIterator.getResultsTotalSize()){
    			pagesize = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("glAccountTypeId", gv.getString("glAccountTypeId"));
    				GenericValue glType = delegator.findOne("GlAccountType", false, UtilMisc.toMap("glAccountTypeId", gv.getString("glAccountTypeId")));
    				mapGv.put("description",(UtilValidate.isNotEmpty(glType) ? glType.get("description",locale) : ""));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = SalesPartyUtil.filterMap(listRs, listAllConditions);
    		listRs = SalesPartyUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(!listAllConditions.isEmpty()){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getGLAccountTypeNotDedault service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;
    	
    }
    
}
