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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;

public class CreditCardTypeGlAccountJQServices {
	public static final String module = CreditCardTypeGlAccountJQServices.class.getName();
	 @SuppressWarnings("unchecked")
		public static Map<String, Object> jqListCreditCardTypeGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) {
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
	    		
	    		DynamicViewEntity view = new DynamicViewEntity();
	    		view.setEntityName("CreditCardAndEnumeration");
	    		view.addMemberEntity("CC", "CreditCardTypeGlAccount");
	    		view.addMemberEntity("EN", "Enumeration");
	    		view.addMemberEntity("GL", "GlAccount");
	    		view.addAlias("CC", "cardType");
	    		view.addAlias("CC", "organizationPartyId");
	    		view.addAlias("CC", "glAccountId");
	    		view.addAlias("EN", "enumCode");
	    		view.addAlias("EN", "enumId");
	    		view.addAlias("GL", "glAccountId");
	    		view.addAlias("GL", "accountName");
	    		view.addAlias("GL", "accountCode");
	    		view.addViewLink("EN","CC",Boolean.FALSE,UtilMisc.toList(new ModelKeyMap("enumId", "cardType")));
	    		view.addViewLink("CC","GL",Boolean.FALSE,UtilMisc.toList(new ModelKeyMap("glAccountId", "glAccountId")));
	    		try {
	    			view.makeModelViewEntity(delegator);
				} catch (Exception e) {
					Debug.log("Fatal Error when create View Entity in service jqListCreditCardTypeGlAccount",module,e);
					return ServiceUtil.returnError("Fatal Error when create View Entity in service jqListCreditCardTypeGlAccount"  +e.toString());
				}
	    			if(UtilValidate.isNotEmpty(listAllConditions)){
	        			for(int i  = 0; i < listAllConditions.size();i++){
	        				String listCondStr = listAllConditions.get(i).toString().trim();
	        				String fieldCond = listCondStr.split(" ")[0];
	        				int index = listCondStr.indexOf("%");
	        				if(index != -1){
	        					String tmpValue = listCondStr.substring(index + 1, listCondStr.length() - 1);
	        					if(fieldCond.equals("cardType")){
	        	    				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("enumCode",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
	        	    				listAllConditions.remove(i);
	        	    				break;
	        					}else if(fieldCond.equals("glAccountId")){
	        						listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("accountName",EntityJoinOperator.LIKE,"%" + tmpValue + "%"),listAllConditions.get(i)),EntityJoinOperator.OR));
	        	    				listAllConditions.remove(i);
	        	    				break;
	        					}else continue;
	        				}
	        			}
	        		}	
	        		
	    		listIterator = delegator.findListIteratorByCondition(view,EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
//	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
//	    		listIterator = delegator.find("CreditCardTypeGlAccount", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListCreditCardTypeGlAccount service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
	 

	 @SuppressWarnings("unchecked")
	    public static Map<String, Object> jqgetListCreditCardTypeNotGlAccount(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
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
	    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
	    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
	    		int start = pagenum*pagesize;
	    		int end = start + pagesize;
	    		
	    		List<GenericValue> listPaymentOfOrganization = delegator.findList("CreditCardTypeGlAccount",EntityCondition.makeCondition("organizationPartyId",organizationPartyId),null,null,null,false);
	    		List<String> listPaymentUsed = FastList.newInstance();
	    		if(UtilValidate.isNotEmpty(listPaymentOfOrganization)){
	    			for(GenericValue var  : listPaymentOfOrganization){
	    				listPaymentUsed.add(var.getString("cardType"));
	    			}
	    		}
	    		
	    		List<EntityCondition> listConds = FastList.newInstance();
	    		listConds.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CREDIT_CARD_TYPE"));
	    		listConds.add(EntityCondition.makeCondition("enumId",EntityJoinOperator.NOT_IN,UtilValidate.isNotEmpty(listPaymentUsed) ? listPaymentUsed : UtilMisc.toList("")));
	    		EntityCondition cardTypeCond = EntityCondition.makeCondition(listConds, EntityOperator.AND);
	    		
	    		listIterator = delegator.find("Enumeration",cardTypeCond, null, null, listSortFields, opts);
	    		
	    		if(pagesize > listIterator.getResultsTotalSize()){
	    			pagesize = 	listIterator.getResultsTotalSize();
	    		}
	    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
	    		if(UtilValidate.isNotEmpty(listGv)){
	    			for(GenericValue gv : listGv){
	    				Map<String,Object> mapGv = FastMap.newInstance();
	    				mapGv.put("cardType", gv.getString("enumId"));
	    				GenericValue glType = delegator.findOne("Enumeration", false, UtilMisc.toMap("enumId", gv.getString("enumId")));
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
				String errMsg = "Fatal error calling jqgetListCreditCardTypeNotGlAccount service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	return resultSuccess;
	    	
	    }
	 
}
