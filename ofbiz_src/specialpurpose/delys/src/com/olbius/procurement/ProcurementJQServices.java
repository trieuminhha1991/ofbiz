package com.olbius.procurement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;













import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


public class ProcurementJQServices {
	public static final String module = ProcurementJQServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    
    
	@SuppressWarnings("unchecked")
    public static Map<String, Object> jqGetListShoppingProposal(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("requirementTypeId", "PO_REQ");
		//check role for userLogin
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean checkRole = false;
		String partyId = userLogin.getString("partyId");
		if(UtilValidate.isNotEmpty(partyId)){
			EntityCondition makeCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			List<GenericValue> listPartyRoles = delegator.findList("PartyRole", makeCond, null, null, null, false);
			if(UtilValidate.isNotEmpty(listPartyRoles)){
				for (GenericValue partyRole : listPartyRoles) {
					String roleType = partyRole.getString("roleTypeId");
					if(roleType.equalsIgnoreCase("DELYS_PROCUREMENT") || roleType.equalsIgnoreCase("DELYS_CEO")){
						checkRole = true;
						break;
					}
				}
			}
		}
		if(!checkRole){
			mapCondition.put("createdByUserLogin", userLogin.getString("userLoginId"));
		}
		
		//end check role for userLogin
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("Requirement", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListShoppingProposal service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqGetListRequirementItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String[] requirementIds = parameters.get("requirementId");
    	String requirementId = null;
    	if(UtilValidate.isNotEmpty(requirementIds)){
    		requirementId = requirementIds[0];
    	}
		Map<String, String> mapCondition = new HashMap<String, String>();
		
		mapCondition.put("requirementId", requirementId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("RequirementItemAndProduct", tmpConditon, null, null, listSortFields, opts);
    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListRequirementItem service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqGetListProductForProcurementProposal(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String[] productCateogryIds = parameters.get("productCateogryId");
    	String productCateogryId = null;
    	if(UtilValidate.isNotEmpty(productCateogryIds)){
    		productCateogryId = productCateogryIds[0];
    	}
		Map<String, String> mapCondition = new HashMap<String, String>();
		
		mapCondition.put("primaryProductCategoryId", productCateogryId);
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("Product", tmpConditon, null, null, listSortFields, opts);
    		
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListProductForProcurementProposal service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> jqNoItem(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	
		Map<String, String> mapCondition = new HashMap<String, String>();
		
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static Map<String, Object> createDetailShoppingProposal(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String productListStr = (String) context.get("productListStr");
        String requirementId = (String) context.get("requirementId");
        JSONArray listProductStr = JSONArray.fromObject(productListStr);
        LocalDispatcher dispatcher = ctx.getDispatcher();
        if(UtilValidate.isNotEmpty(listProductStr)){
        	for (int i = 0; i < listProductStr.size(); i++) {
        		
				JSONObject product = listProductStr.getJSONObject(i);
				Map<String, Object> createRequirementItemMap = new HashMap<String, Object>();
				
				createRequirementItemMap.put("productId", product.getString("productId"));
				createRequirementItemMap.put("prodCategoryId", "PROCUREMENT_CATEGORY");
				String quantityStr = product.getString("quantity");
				if(UtilValidate.isNotEmpty(quantityStr)){
					BigDecimal quantity = new BigDecimal(quantityStr);
					createRequirementItemMap.put("quantity", quantity);
				}
				
				if(UtilValidate.isNotEmpty(product.getString("quantityUomId"))){
					createRequirementItemMap.put("quantityUomId", product.getString("quantityUomId"));
				}
				
				createRequirementItemMap.put("reason", product.getString("reason"));
				createRequirementItemMap.put("userLogin", userLogin);	
				String reqItemSeqId = String.format("%05d", i+1);
				createRequirementItemMap.put("reqItemSeqId", reqItemSeqId);	
				createRequirementItemMap.put("requirementId", requirementId);
				dispatcher.runSync("createItemProcurementProposal", createRequirementItemMap);
			}
        }
        return successResult;
	}
	
}
