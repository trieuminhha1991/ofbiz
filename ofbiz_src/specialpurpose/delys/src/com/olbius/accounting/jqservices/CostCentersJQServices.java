package com.olbius.accounting.jqservices;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
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
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class CostCentersJQServices {
	public static final String module = CostCentersJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
	private static JSONArray costCenters;
    public static Map<String, Object> jqListGlAcctgAndAmountPercentage(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	/*EntityListIterator listIterator = null;*/
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<String> glAccountCategoriesReturn = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String organizationPartyId = null;
    	String[] organizationPartyIds = parameters.get("organizationPartyId");
    	listAllConditions.add(tmpConditon);
    	List<GenericValue> glAccountOrganizations = FastList.newInstance();
    	
    	if(UtilValidate.isNotEmpty(organizationPartyIds)){
    		 organizationPartyId = organizationPartyIds[0];
    	}
    	List<String> partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, "GROUP_ROLLUP");
    	partyIds.add(organizationPartyId);
    	if(UtilValidate.isNotEmpty(partyIds)){
    		EntityCondition glAccountOrgCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds);
    		glAccountOrganizations = delegator.findList("GlAccountOrganization", glAccountOrgCond, null, UtilMisc.toList("glAccountId"), null, false);
    		    	
    	}
    	EntityCondition glAccountCategoryCond = EntityCondition.makeCondition("glAccountCategoryTypeId", EntityOperator.EQUALS, "COST_CENTER");
    	
    	List<GenericValue> glAccountCategories = delegator.findList("GlAccountCategory", glAccountCategoryCond, null, UtilMisc.toList("glAccountCategoryId"), null, false);
    	if(UtilValidate.isNotEmpty(organizationPartyIds) && UtilValidate.isNotEmpty(glAccountCategories)){
    		for (GenericValue glAccountOrganization : glAccountOrganizations) {
				for (GenericValue glAccountCategory : glAccountCategories) {
					GenericValue organizationParty = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", glAccountOrganization.get("organizationPartyId")), false);
						
					List<EntityCondition> listAllCondReal = FastList.newInstance();
					listAllCondReal.addAll(listAllConditions);
					listAllCondReal.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountOrganization.getString("glAccountId")));
					listAllCondReal.add(EntityCondition.makeCondition("glAccountCategoryId", EntityOperator.EQUALS, glAccountCategory.getString("glAccountCategoryId")));
					listAllCondReal.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
					
					listSortFields.add("fromDate");
					List<GenericValue> glAccountCategoryMembers  = delegator.findList("GlAccountAndGlAccountCategoryMember", EntityCondition.makeCondition(listAllCondReal,EntityJoinOperator.AND), null, listSortFields, opts, false);
					
					if(UtilValidate.isNotEmpty(glAccountCategoryMembers) && glAccountCategoryMembers.size() > 0){
						GenericValue glAccountCategoryMember = glAccountCategoryMembers.get(0);
						Map<String, Object> glAcctgOrgAndCostCenterMap = new HashMap<String, Object>();
//						if(UtilValidate.isEmpty(glAcctgOrgAndCostCenterMap) && glAcctgOrgAndCostCenterMap.size() == 0){
							glAcctgOrgAndCostCenterMap.put(glAccountCategory.getString("glAccountCategoryId"), glAccountCategoryMember.getBigDecimal("amountPercentage"));
							glAcctgOrgAndCostCenterMap.put("glAccountId", glAccountCategoryMember.getString("glAccountId"));
							glAcctgOrgAndCostCenterMap.put("accountCode", glAccountCategoryMember.getString("accountCode"));
							glAcctgOrgAndCostCenterMap.put("accountName", glAccountCategoryMember.getString("accountName"));
							String organizationValue = organizationParty.getString("groupName") + "[" + glAccountOrganization.getString("organizationPartyId") +"]";
							glAcctgOrgAndCostCenterMap.put("organizationPartyId", glAccountOrganization.getString("organizationPartyId"));
							glAcctgOrgAndCostCenterMap.put("organizationPartyName", organizationValue);
//						}else{
//							glAcctgOrgAndCostCenterMap.put(glAccountCategory.getString("glAccountCategoryId"), glAccountCategoryMember.getBigDecimal("amountPercentage"));
							listIterator.add(glAcctgOrgAndCostCenterMap);
//						}
					}
				}
			}
    	}
    	/*for (GenericValue glAccountCategory : glAccountCategories) {
			String glAccoutCategoryName = glAccountCategory.getString("description");
			glAccountCategoriesReturn.add(glAccoutCategoryName);
		}*/
    	successResult.put("glAccountCategories", glAccountCategories);
    
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    public static Map<String,Object> JQgetListCostCenter(DispatchContext dpct,Map<String,Object> context){
    	Delegator delegator = (Delegator) dpct.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String organizationPartyId = (parameters.containsKey("organizationPartyId")) ? parameters.get("organizationPartyId")[0] : null;
    	Map<String,Object> resultList = ServiceUtil.returnSuccess();
    	List<String> partyIds = FastList.newInstance();
    	List<Map<String,Object>> listIterator = FastList.newInstance();
    	List<Map<String,Object>> listIteratorTmp = FastList.newInstance();
    	try {
			
			partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, "GROUP_ROLLUP");
			partyIds.add(organizationPartyId);
    		List<GenericValue> glAccountOrganizations = delegator.findList("GlAccountOrganization", EntityCondition.makeCondition("organizationPartyId",EntityJoinOperator.IN,partyIds), null, UtilMisc.toList("glAccountId"), opts, true);
    		if(UtilValidate.isNotEmpty(glAccountOrganizations)){
    			List<GenericValue> glAccountCategories = delegator.findList("GlAccountCategory", EntityCondition.makeCondition("glAccountCategoryTypeId","COST_CENTER"), null, UtilMisc.toList("glAccountCategoryId"), opts, true);
    			if(UtilValidate.isNotEmpty(glAccountCategories)){
    				for(GenericValue glAccountOrganization : glAccountOrganizations){
    					for(GenericValue glAccountCategory : glAccountCategories){
						Map<String,Object> glAcctgOrgAndCostCenterMap = FastMap.newInstance();
                         List<EntityCondition> listCond = FastList.newInstance();
                         listCond.add(EntityCondition.makeCondition("glAccountId",glAccountOrganization.getString("glAccountId")));
                         listCond.add(EntityCondition.makeCondition("glAccountCategoryId",glAccountCategory.getString("glAccountCategoryId")));
                         listCond.add(EntityCondition.makeCondition("thruDate",null));
                         listCond.add(EntityCondition.makeCondition(listAllConditions));
                         GenericValue organizationParty = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId", glAccountOrganization.getString("organizationPartyId")));
                         List<GenericValue> glAccountCategoryMembers = delegator.findList("GlAccountAndGlAccountCategoryMember", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null,null , false);
	    					if (UtilValidate.isNotEmpty(glAccountCategoryMembers)) {
								GenericValue glAccountCategoryMember = glAccountCategoryMembers.get(0);
	    						glAcctgOrgAndCostCenterMap.put(glAccountCategory.getString("glAccountCategoryId"), glAccountCategoryMember.getBigDecimal("amountPercentage"));
	    						glAcctgOrgAndCostCenterMap.put("organizationPartyId",glAccountOrganization.getString("organizationPartyId") );
	    						glAcctgOrgAndCostCenterMap.put("glAccountId", glAccountCategoryMember.getString("glAccountId"));
	    						glAcctgOrgAndCostCenterMap.put("accountCode", glAccountCategoryMember.getString("accountCode"));
	    						glAcctgOrgAndCostCenterMap.put("accountName", glAccountCategoryMember.getString("accountName"));
	    						String organizationValue = organizationParty.getString("groupName") + "[" + glAccountOrganization.getString("organizationPartyId") +"]";
	    						glAcctgOrgAndCostCenterMap.put("organizationPartyName",organizationValue);
	    						if(listIterator.isEmpty()){
	    							listIterator.add(glAcctgOrgAndCostCenterMap);
	    						}else{
	    							boolean flag = false;
	    							for(Map<String,Object> maptmp : listIterator){
	    								if(maptmp.get("glAccountId").equals(glAccountCategoryMember.getString("glAccountId"))){
	    									maptmp.put(glAccountCategory.getString("glAccountCategoryId"), glAccountCategoryMember.getBigDecimal("amountPercentage"));
	    									flag = true;
	    									break;
	    								}else continue;
	    							}
	    							if(!flag){
	    								listIterator.add(glAcctgOrgAndCostCenterMap);
	    							}
	    						}
							}	
    					}
    				}
    				resultList.put("glAccountCategories", glAccountCategories);
    			}
    			resultList.put("listIterator", listIterator);
    		}
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An error when get list cost center by " + e.getMessage());
		}
    	return resultList;
    }
    
    
    public static Map<String,Object> updateCostCenters(DispatchContext dpct,Map<String,Object> context) throws GenericEntityException{
    	Delegator delegator =  (Delegator) dpct.getDelegator();
		LocalDispatcher dispatcher = (LocalDispatcher) dpct.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String params = (String) context.get("costCenters");
    	costCenters = JSONArray.fromObject(params);
    	Locale locale = (Locale) context.get("locale");
    	Map<String,Object> result = FastMap.newInstance();
    	if(UtilValidate.isNotEmpty(costCenters)){
    		for (int i = 0; i < costCenters.size(); i++) {
				JSONObject costCenter = costCenters.getJSONObject(i);
				if(UtilValidate.isNotEmpty(costCenter)){
					Map<String, Object> costCenterMapService = new HashMap<String, Object>();
					if(UtilValidate.isNotEmpty(costCenter.getString("glAccountId")) && !costCenter.getString("glAccountId").equalsIgnoreCase("null")){
						costCenterMapService.put("glAccountId", costCenter.getString("glAccountId"));
					}else{
						costCenterMapService.put("glAccountId", null);
					}
					Map<String, Object> costCenterMap = new HashMap<String, Object>();
					List<GenericValue> glAccountCategories = delegator.findList("GlAccountCategory", EntityCondition.makeCondition("glAccountCategoryTypeId", "COST_CENTER"), null, UtilMisc.toList("glAccountCategoryId"), null, false);
			    	if( UtilValidate.isNotEmpty(glAccountCategories)){
			    		for (GenericValue glAccountCategory : glAccountCategories) {
							String glAccountCategoryId = glAccountCategory.getString("glAccountCategoryId");
							String percentAmount = costCenter.containsKey(glAccountCategoryId) ? costCenter.getString(glAccountCategoryId) : null ;
							costCenterMap.put(glAccountCategoryId, (percentAmount!= null) ? percentAmount  : "");
						}
			    	}
			    	costCenterMapService.put("userLogin", userLogin);
			    	costCenterMapService.put("amountPercentageMap", costCenterMap);
			    	try {
			    		result = dispatcher.runSync("createUpdateCostCenter", costCenterMapService);
			    		if(ServiceUtil.isSuccess(result)){
		    				return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysAccGlUiLabels", "updateCostCenterSuccess"	, locale));
			    		}else return ServiceUtil.returnError(UtilProperties.getMessage("DelysAccGlUiLabels", "updateCostCenterError", locale));
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
				
			}
    	}
    	
    	return result;
    }
    
}
