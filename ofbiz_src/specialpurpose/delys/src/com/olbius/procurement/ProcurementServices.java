package com.olbius.procurement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.PartyUtil;

public class ProcurementServices {
	public static final String module = ProcurementServices.class.getName();
	public static final String resource = "DelysProcurementLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    public static Map<String, Object> getDepartmentFromUserLogin(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> successReturn = ServiceUtil.returnSuccess();
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = ctx.getDelegator();
    	String userLoginId = (String) context.get("createdByUserLogin");
    	GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
    	String groupName = "";
    	if(UtilValidate.isNotEmpty(userLogin)){
    		String partyId = userLogin.getString("partyId");
    		if(UtilValidate.isNotEmpty(partyId)){
    			try {
    				groupName = PartyUtil.getDeptNameById(partyId, delegator);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}else{
    			return ServiceUtil.returnError(UtilProperties.getMessage("DelysProcurementLabels", "UserLoginDoNotlinkParty", UtilMisc.toMap("userLoginId", userLoginId), locale));
    		}
    	}
    	successReturn.put("departmentName", groupName); 
    	return successReturn;
    }
    public static Map<String, Object> updateProcurementProposal(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String productListStr = (String) context.get("productListStr");
        String requirementId = (String) context.get("requirementId");
        BigDecimal estimatedBudget = (BigDecimal) context.get("estimatedBudget");
        String currencyUomId = (String) context.get("currencyUomId");
        String description = (String) context.get("description");
        Timestamp requirementStartDate = (Timestamp) context.get("requirementStartDate");
        Timestamp requiredByDate = (Timestamp) context.get("requiredByDate");
        Security security = (Security) ctx.getSecurity();
        String partyId = "";
        boolean hasPermisson = security.hasEntityPermission("PROCUREMENT", "_UPDATE", userLogin);
        if(!hasPermisson){
        	return ServiceUtil.returnError(UtilProperties.getMessage(resource, "NotEnoughPermissionToEditThisProposal", locale));
        	
        }
        GenericValue procurementProposalSelected = null;
        List<String> errorMessageList = FastList.newInstance();
        try {
			procurementProposalSelected = delegator.findOne("Requirement",UtilMisc.toMap("requirementId", requirementId), false);
			String createdByUserLoginId = procurementProposalSelected.getString("createdByUserLogin");
			GenericValue createdByUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", createdByUserLoginId), false);
			partyId = createdByUserLogin.getString("partyId");
			
			if(UtilValidate.isEmpty(procurementProposalSelected)){
				errorMessageList.add(UtilProperties.getMessage(resource, "ProcurmentProposalNotFound", UtilMisc.toMap("requirementId", requirementId), locale));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(UtilValidate.isEmpty(estimatedBudget)){
        	errorMessageList.add(UtilProperties.getMessage(resource, "EstimatedBudgetIsRequired", locale));
        }
        if(UtilValidate.isEmpty(currencyUomId)){
        	errorMessageList.add(UtilProperties.getMessage(resource, "CurrencyBudgetIsRequired", locale));
        }
        if(UtilValidate.isEmpty(description)){
        	errorMessageList.add(UtilProperties.getMessage(resource, "DescriptionIsRequired", locale));
        }
        if(errorMessageList.size() > 0){
        	return ServiceUtil.returnError(errorMessageList);
        }
        
        procurementProposalSelected.set("estimatedBudget", estimatedBudget);
        procurementProposalSelected.set("currencyUomId", currencyUomId);
        procurementProposalSelected.set("description", description.trim());
        procurementProposalSelected.set("requirementStartDate", requirementStartDate);
        procurementProposalSelected.set("requiredByDate", requiredByDate);
        //after update requirement then if it haven't error we will continue 
        try {
			delegator.store(procurementProposalSelected);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.logError("Update Procurment proposal failed ", module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,"ProcurementProposalUpdateFailedPleaseNotifyCustomerService", locale));
			
		}
        List<GenericValue> listRequirementItem = FastList.newInstance();
        EntityCondition itemCond = EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, requirementId);
        try {
			listRequirementItem = delegator.findList("RequirementItem", itemCond, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        JSONArray listProductStr = JSONArray.fromObject(productListStr);
        List<GenericValue> listNoItemRemoved = FastList.newInstance();
        List<GenericValue> listItemWillRemove = FastList.newInstance();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        if(UtilValidate.isNotEmpty(listProductStr)){
        	//if requirement which don't have any requirement item, we will add new requirementItem
        	if(UtilValidate.isNotEmpty(listRequirementItem)){
        		
        			for (int i = 0; i < listProductStr.size(); i++) {
        				boolean addNewItem = true;
        				
        				JSONObject product = listProductStr.getJSONObject(i);
        				for (GenericValue requirementItem : listRequirementItem) {
        					
        					if(product.getString("productId").equalsIgnoreCase(requirementItem.getString("productId"))){
        						listNoItemRemoved.add(requirementItem);
        						Map<String, Object> updateRequirementItemMap = new HashMap<String, Object>();
        						updateRequirementItemMap.put("requirementId", requirementId);
        						updateRequirementItemMap.put("reqItemSeqId", requirementItem.getString("reqItemSeqId"));
        						String quantityStr = product.getString("quantity");
        	    				if(UtilValidate.isNotEmpty(quantityStr)){
        	    					BigDecimal quantity = new BigDecimal(quantityStr);
        	    					updateRequirementItemMap.put("quantity", quantity);
        	    					if(UtilValidate.isNotEmpty(product.getString("quantityUomId"))){
        	    						updateRequirementItemMap.put("quantityUomId", product.getString("quantityUomId"));
        	        				}
        	    					updateRequirementItemMap.put("reason", product.getString("reason"));
        	        				updateRequirementItemMap.put("userLogin", userLogin);
        	    				}
        	    			 addNewItem = false;
        	    			 dispatcher.runSync("updateItemProcurementProposal", updateRequirementItemMap);
        	    			  
        					}
						}
        				if(addNewItem){
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
        			//remove requirement item
        			boolean removeSlectedItem = true;
        			for (GenericValue requirementItem : listRequirementItem) {
						for (GenericValue notRemoved : listNoItemRemoved) {
							if(requirementItem.getString("productId").equalsIgnoreCase(notRemoved.getString("productId"))){
								removeSlectedItem = false;
								break;
							}
						}
						if(removeSlectedItem){
							listItemWillRemove.add(requirementItem);
						}
					}
        			for (GenericValue item : listItemWillRemove) {
						Map<String, Object> removeRequirementItem = new HashMap<String, Object>();
						removeRequirementItem.put("requirementId", item.getString("requirementId"));
						removeRequirementItem.put("reqItemSeqId", item.getString("reqItemSeqId"));
						removeRequirementItem.put("userLogin", userLogin);
						dispatcher.runSync("removeItemProcurementProposal", removeRequirementItem);
					}
        	}else{
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
        	
        }
        //send notification for person who created this procurement proposal
        Map<String, Object> createNotification = new HashMap<String, Object>();
		createNotification.put("userLogin", userLogin);
		createNotification.put("state", "open");
		Date nowTime = new Date();
		Timestamp nowTimeStamp = new Timestamp(nowTime.getTime());
		String[] parameterForUiLabel = new String[2];
		parameterForUiLabel[0] = requirementId;
		parameterForUiLabel[1] = userLogin.getString("userLoginId");
		String header = UtilProperties.getMessage(resource, "ProcurementProposalEditNotification", parameterForUiLabel, locale);
		createNotification.put("targetLink", requirementId);
		createNotification.put("action", "viewProcurementProposal?requirementId=" + requirementId);
		createNotification.put("partyId", partyId);
		createNotification.put("dateTime", nowTimeStamp);
		createNotification.put("header", header);
		createNotification.put("ntfType", "one");
		dispatcher.runSync("createNotification", createNotification);
        return successResult;
	} 	
}
