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

public class GlAccountNrPaymentMethodJQServices {
	public static final String module = GlAccountNrPaymentMethodJQServices.class.getName();
	 @SuppressWarnings("unchecked")
		public static Map<String, Object> jqListGlAccountNrPaymentMethod(DispatchContext ctx, Map<String, ? extends Object> context) {
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
	    	try {
	    		if(UtilValidate.isNotEmpty(listAllConditions)){
	    			for(int i  = 0; i < listAllConditions.size();i++){
	    				String listCondStr = listAllConditions.get(i).toString().trim();
	    				String fieldCond = listCondStr.split(" ")[0];
	    				int index = listCondStr.indexOf("%");
	    				if(index != -1){
	    					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
	    					if(fieldCond.equals("accountCode")){
	    	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountName",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
	    	    				listAllConditions.remove(i);
	    	    				break;
	    					}else if(fieldCond.equals("accountCodeDef")){
	    						listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountNameDef",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
	    	    				listAllConditions.remove(i);
	    	    				break;
	    					}else continue;
	    				}
	    			}
	    		}
	    		
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("PaymentMethodTypeGlAccountDetail", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListGlAccountNrPaymentMethod service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 
	 
	 @SuppressWarnings("unchecked")
	    public static Map<String, Object> getPaymentMethodTypeNotDedault(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	String organizationPartyId =  (String)parameters.get("organizationPartyId")[0];
	    	EntityCondition tmpConditon = EntityCondition.makeCondition("organizationPartyId",EntityJoinOperator.NOT_EQUAL,organizationPartyId);
	    	
	    	Locale locale = (Locale) context.get("locale");
	    	List<Map<String,Object>> listRs = FastList.newInstance();
	    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
	    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
	    		int start = pagenum*pagesize;
	    		int end = start + pagesize;
	    		
	    		List<GenericValue> listPaymentMTOfOrganization = delegator.findList("PaymentMethodTypeGlAccountDetail",EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
	    		List<String> listPaymentMethodUsed = FastList.newInstance();
	    		if(UtilValidate.isNotEmpty(listPaymentMTOfOrganization)){
	    			for(GenericValue var  : listPaymentMTOfOrganization){
	    				listPaymentMethodUsed.add(var.getString("paymentMethodTypeId"));
	    			}
	    		}
	    		listIterator = delegator.find("PaymentMethodType",EntityCondition.makeCondition("paymentMethodTypeId",EntityJoinOperator.NOT_IN,UtilValidate.isNotEmpty(listPaymentMethodUsed) ? listPaymentMethodUsed : UtilMisc.toList("")), null, null, listSortFields, opts);
	    		
	    		if(pagesize > listIterator.getResultsTotalSize()){
	    			pagesize = 	listIterator.getResultsTotalSize();
	    		}
	    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
	    		if(UtilValidate.isNotEmpty(listGv)){
	    			for(GenericValue gv : listGv){
	    				Map<String,Object> mapGv = FastMap.newInstance();
	    				mapGv.put("paymentMethodTypeId", gv.getString("paymentMethodTypeId"));
	    				GenericValue glType = delegator.findOne("PaymentMethodType", false, UtilMisc.toMap("paymentMethodTypeId", gv.getString("paymentMethodTypeId")));
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
	
