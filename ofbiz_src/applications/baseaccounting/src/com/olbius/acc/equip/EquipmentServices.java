package com.olbius.acc.equip;

import com.olbius.acc.equip.entity.EquipAllocCost;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class EquipmentServices {
	
	public static final String MODULE = EquipmentServices.class.getName();
	private static final int  RECIPROCAL_ITEM_SEQ_ID = 5;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getListEquipmentJQ(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	//listSortFields.add("equipmentName");
    	EntityFindOptions opts = (EntityFindOptions) context.get("opts");
    	try {
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("equipmentName");
    		}
    		listIterator = delegator.find("EquipmentAndTotalPrice", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		}catch (Exception e) {
			String errMsg = "Fatal error calling JqxGetListEquipments service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	
	public static Map<String, Object> createEquipment(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		String equipmentId = (String) context.get("equipmentId");
		GenericValue equipment = delegator.makeValue("Equipment");
		equipment.setNonPKFields(context);
		if(equipmentId == null) {
			equipmentId = delegator.getNextSeqId("Equipment");
		}
		equipment.set("equipmentId", equipmentId);
		try {
			equipment.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("equipmentId", equipmentId);
		return result;
	}
	
	public static Map<String, Object> updateEquipment(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String equipmentId = (String)context.get("equipmentId");
		try {
			Map<String, Object> updateFieldMap = new HashMap<String, Object>(context);
			boolean isPosted = EquipmentUtils.isEquipmentPosted(delegator, equipmentId);
			if(isPosted){
				updateFieldMap.remove("currencyUomId");
				updateFieldMap.remove("quantityUom");
				updateFieldMap.remove("quantity");
				updateFieldMap.remove("unitPrice");
			}
			GenericValue equipment = delegator.makeValue("Equipment");
			equipment.setAllFields(updateFieldMap, false, null, null);
			delegator.store(equipment);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object> createOrStoreEquipmentParty(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("equipmentId", (String) context.get("equipmentId")));
		conds.add(EntityCondition.makeCondition("partyId", (String) context.get("partyId")));
		List<EntityCondition> dateConds = FastList.newInstance();
		dateConds.add(EntityCondition.makeCondition("thruDate", null));
		dateConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())));
		conds.add(EntityCondition.makeCondition(dateConds, EntityJoinOperator.OR));
		
		List<GenericValue> listEquipParties = delegator.findList("EquipmentParty", EntityCondition.makeCondition(conds), null, null, null, false);
		if (UtilValidate.isNotEmpty(listEquipParties)) {
			for (GenericValue item : listEquipParties) {
				item.set("thruDate", new Timestamp(System.currentTimeMillis()));
				item.store();
			}
		}
		
		GenericValue equipmentParty = delegator.makeValue("EquipmentParty");
		equipmentParty.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createOrStoreEquipmentProductStore(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("equipmentId", (String) context.get("equipmentId")));
		conds.add(EntityCondition.makeCondition("productStoreId", (String) context.get("productStoreId")));
		List<EntityCondition> dateConds = FastList.newInstance();
		dateConds.add(EntityCondition.makeCondition("thruDate", null));
		dateConds.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())));
		conds.add(EntityCondition.makeCondition(dateConds, EntityJoinOperator.OR));
		
		List<GenericValue> listEquipStores = delegator.findList("EquipmentProductStore", EntityCondition.makeCondition(conds), null, null, null, false);
		if (UtilValidate.isNotEmpty(listEquipStores)) {
			for (GenericValue item : listEquipStores) {
				item.set("thruDate", new Timestamp(System.currentTimeMillis()));
				item.store();
			}
		}
		
		GenericValue equipmentProductStore = delegator.makeValue("EquipmentProductStore");
		equipmentProductStore.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentProductStore);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createEquipmentIncrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentIncrease = delegator.makeValue("EquipmentIncrease");
		equipmentIncrease.setNonPKFields(context);
		String equipmentIncreaseId = delegator.getNextSeqId("EquipmentIncrease");
		equipmentIncrease.set("equipmentIncreaseId", equipmentIncreaseId);
		equipmentIncrease.set("isPosted", Boolean.FALSE);
		try {
			delegator.create(equipmentIncrease);
			retMap.put("equipmentIncreaseId", equipmentIncreaseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}

	public static Map<String, Object> updateEquipmentIncrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String equipmentIncreaseId = (String)context.get("equipmentIncreaseId");
		try {
			GenericValue equipmentIncrease = delegator.findOne("EquipmentIncrease", UtilMisc.toMap("equipmentIncreaseId", equipmentIncreaseId), false);
			if(equipmentIncrease == null){
				return ServiceUtil.returnError("cannot find Equipment Increase width id: " + equipmentIncreaseId);
			}
//			Boolean isPosted = equipmentIncrease.getBoolean("isPosted");
//			if(isPosted != null && isPosted){
//				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotUpdateWhenPosted", locale));
//			}
			equipmentIncrease.setNonPKFields(context, true);
			delegator.store(equipmentIncrease);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteEquipmnentIncrease(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String equipmentIncreaseId = (String)context.get("equipmentIncreaseId");
		try {
			GenericValue equipmentIncrease = delegator.findOne("EquipmentIncrease", UtilMisc.toMap("equipmentIncreaseId", equipmentIncreaseId), false);
			if(equipmentIncrease == null){
				return ServiceUtil.returnError("Cannot find equipment increase width id: " + equipmentIncreaseId + " to delete");
			}
			Boolean isPosted = equipmentIncrease.getBoolean("isPosted");
			if(isPosted != null && isPosted){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotDeleteWhenPosted", locale));
			}
			List<GenericValue> equipmentIncreaseItemList = delegator.findByAnd("EquipmentIncreaseItem", UtilMisc.toMap("equipmentIncreaseId", equipmentIncreaseId), null, false);
			List<String> equipmentIds = EntityUtil.getFieldListFromEntityList(equipmentIncreaseItemList, "equipmentId", true);
			List<GenericValue> equipmentAllocItemList = delegator.findList("EquipmentAllocItem", EntityCondition.makeCondition("equipmentId", EntityJoinOperator.IN, equipmentIds), null, null, null, false);
			if(UtilValidate.isNotEmpty(equipmentAllocItemList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "CannotDeleteBecauseEquipmentIsAllocated", locale));
			}
			
			delegator.removeByCondition("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentIncreaseId", equipmentIncreaseId));
			equipmentIncrease.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgdeletesuccess", locale));
	}
	
	public static Map<String, Object> updatePostedEquipmentIncrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String equipmentIncreaseId = (String)context.get("equipmentIncreaseId");
		String isPosted = (String)context.get("isPosted");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId"));
		String message = null;
		try {
			GenericValue equipmentIncrease = delegator.findOne("EquipmentIncrease", UtilMisc.toMap("equipmentIncreaseId", equipmentIncreaseId), false);
			if("Y".equals(isPosted)){
				equipmentIncrease.set("isPosted", Boolean.TRUE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				
				if(UtilValidate.isNotEmpty(equipmentIncrease.getString("acctgTransId"))){
					 return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BaccEquipmentPostedError", locale));
				}
				Map<String, Object> createAcctgTrans = FastMap.newInstance();
				createAcctgTrans.put("description", equipmentIncrease.getString("comment"));
				createAcctgTrans.put("transactionDate", equipmentIncrease.getTimestamp("dateArising"));
				createAcctgTrans.put("acctgTransTypeId", "EQUIP_INCREMENT");
				createAcctgTrans.put("partyId", organizationPartyId);
				createAcctgTrans.put("userLogin", context.get("userLogin"));
				createAcctgTrans.put("glFiscalTypeId", "ACTUAL");

				int reciprocalSeqId = 1;
				String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				
				 List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentIncreaseId", equipmentIncreaseId ), null, null, null, false);
				 List<GenericValue> acctgTransEntries = FastList.newInstance();
				 
				 for(GenericValue item: equipmentIncreaseItemList){
					 GenericValue acctgTransEntryC = delegator.makeValue("AcctgTransEntry");
					 GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId",item.getString("equipmentId")), false);
					 
					 BigDecimal unitPrice =equipment.getBigDecimal("unitPrice");
					 BigDecimal quanity = equipment.getBigDecimal("quantity");
					 String currencyUomId = equipment.getString("currencyUomId");
					 
					 BigDecimal amount = unitPrice.multiply(quanity); 
					 acctgTransEntryC.put("organizationPartyId", organizationPartyId);
					 acctgTransEntryC.put("amount", amount);
					 acctgTransEntryC.put("currencyUomId", currencyUomId);
					 acctgTransEntryC.put("origCurrencyUomId", currencyUomId);
					 acctgTransEntryC.put("partyId", organizationPartyId);
					 acctgTransEntryC.put("reciprocalSeqId", reciprocalItemSeqId);
					 
					 acctgTransEntryC.put("debitCreditFlag", "C");
					 acctgTransEntryC.put("glAccountId", item.getString("costGlAccountId"));
					 acctgTransEntries.add(acctgTransEntryC);
					 					
					 GenericValue acctgTransEntryD = (GenericValue) acctgTransEntryC.clone();
					 acctgTransEntryD.put("debitCreditFlag", "D");
					 acctgTransEntryD.put("glAccountId", item.getString("debitGlAccountId"));
					 acctgTransEntries.add(acctgTransEntryD);
					 reciprocalSeqId+=1;
					 reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				 }
				 createAcctgTrans.put("acctgTransEntries", acctgTransEntries);
				 LocalDispatcher localDispatcher = dctx.getDispatcher();
				 Map<String, Object> createAccTransResult= localDispatcher.runSync("createAcctgTransAndEntries", createAcctgTrans);
				
				 if(ServiceUtil.isSuccess(createAccTransResult)){
					 String acctgTransId = (String)createAccTransResult.get("acctgTransId");
					 equipmentIncrease.put("acctgTransId", acctgTransId);
				 }else{
					 return ServiceUtil.returnError("error");
				 }
			}else{
				equipmentIncrease.set("isPosted", Boolean.FALSE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCUnpostedSuccess", locale);
			}
			equipmentIncrease.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}catch(GenericServiceException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(message);
	}
	
	public static Map<String, Object> createOrStoreEquipmentIncreaseItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentIncreaseItem = delegator.makeValue("EquipmentIncreaseItem");
		equipmentIncreaseItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentIncreaseItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createEquipmentDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentDecrease = delegator.makeValue("EquipmentDecrease");
		equipmentDecrease.setNonPKFields(context);
		String equipmentDecreaseId = delegator.getNextSeqId("EquipmentDecrease");
		equipmentDecrease.set("equipmentDecreaseId", equipmentDecreaseId);
		equipmentDecrease.set("isPosted", Boolean.FALSE);
		try {
			delegator.create(equipmentDecrease);
			retMap.put("equipmentDecreaseId", equipmentDecreaseId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEquipmentDecrease(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String equipmentDecreaseId = (String)context.get("equipmentDecreaseId");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue equipmentDecrease = delegator.findOne("EquipmentDecrease", UtilMisc.toMap("equipmentDecreaseId", equipmentDecreaseId), false);
			if(equipmentDecrease == null){
				return ServiceUtil.returnError("cannot find Equipment Decrease width id: " + equipmentDecreaseId);
			}
			Boolean isPosted = equipmentDecrease.getBoolean("isPosted");
			if(isPosted != null && isPosted){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotUpdateWhenPosted", locale));
			}
			equipmentDecrease.setNonPKFields(context, true);
			delegator.store(equipmentDecrease);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> deleteEquipmnentDecrease(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String equipmentDecreaseId = (String)context.get("equipmentDecreaseId");
		try {
			GenericValue equipmentDecrease = delegator.findOne("EquipmentDecrease", UtilMisc.toMap("equipmentDecreaseId", equipmentDecreaseId), false);
			if(equipmentDecrease == null){
				return ServiceUtil.returnError("Cannot find equipment Decrease width id: " + equipmentDecreaseId + " to delete");
			}
			Boolean isPosted = equipmentDecrease.getBoolean("isPosted");
			if(isPosted != null && isPosted){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotDeleteWhenPosted", locale));
			}
			delegator.removeByCondition("EquipmentDecreaseItem", EntityCondition.makeCondition("equipmentDecreaseId", equipmentDecreaseId));
			equipmentDecrease.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgdeletesuccess", locale));
	}
	
	public static Map<String, Object> updatePostedEquipmentDecrease(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		LocalDispatcher localDispatcher = dctx.getDispatcher();
		String equipmentDecreaseId = (String)context.get("equipmentDecreaseId");
		String isPosted = (String)context.get("isPosted");
		String message = null;
		
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			GenericValue equipmentDecrease = delegator.findOne("EquipmentDecrease", UtilMisc.toMap("equipmentDecreaseId", equipmentDecreaseId), false);
			if("Y".equals(isPosted)){
				if(UtilValidate.isNotEmpty(equipmentDecrease.getString("acctgTransId"))){
					 return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BaccEquipmentPostedError", locale));
				}
				equipmentDecrease.set("isPosted", Boolean.TRUE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				
				Map<String, Object> createAccTrans = FastMap.newInstance();
				createAccTrans.put("description", equipmentDecrease.get("decreaseReason"));
				createAccTrans.put("transactionDate", equipmentDecrease.getTimestamp("dateArising"));
				createAccTrans.put("acctgTransTypeId", "EQUIP_DECREMENT");
				createAccTrans.put("organizationPartyId", organizationPartyId);
				createAccTrans.put("userLogin", userLogin);
				createAccTrans.put("glFiscalTypeId", "ACTUAL");
				
				Map<String, Object> createTransRs = localDispatcher.runSync("createAcctgTrans", createAccTrans);
				String acctgTransId = null;
				if (ServiceUtil.isSuccess(createTransRs)) {
					 acctgTransId = (String) createTransRs.get("acctgTransId");
				} else {
					return ServiceUtil.returnError("error");
				}
				
				List<GenericValue> equipmentDecreaseItemList = delegator.findList("EquipmentDecreaseItem", EntityCondition.makeCondition("equipmentDecreaseId", equipmentDecreaseId ), null, null, null, false);
				Map<String, Object> createTransEntryCtx = FastMap.newInstance();
				int reciprocalSeqId = 1;
				for (GenericValue item : equipmentDecreaseItemList) {
					String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
					
					List<GenericValue> equipIncreaseList = delegator.findByAnd("EquipmentIncreaseItem", UtilMisc.toMap("equipmentId", item.getString("equipmentId")), null, false);
					GenericValue equipIncrease = equipIncreaseList.get(0);
					
					GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId", item.getString("equipmentId")), false);
					String partyId = organizationPartyId;
					if (UtilValidate.isNotEmpty(equipment) && UtilValidate.isNotEmpty(equipment.getString("partyId"))) {
						partyId = equipment.getString("partyId");
					}
		            
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("remainValue"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "D");
					createTransEntryCtx.put("glAccountId", item.getString("lossGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					
					createTransEntryCtx.clear();
					createTransEntryCtx.put("amount", item.getBigDecimal("remainValue"));
					createTransEntryCtx.put("acctgTransId", acctgTransId);
					createTransEntryCtx.put("debitCreditFlag", "C");
					createTransEntryCtx.put("glAccountId", equipIncrease.getString("debitGlAccountId"));
					createTransEntryCtx.put("organizationPartyId", organizationPartyId);
					createTransEntryCtx.put("partyId", partyId);
					createTransEntryCtx.put("reciprocalSeqId", reciprocalItemSeqId);
					createTransEntryCtx.put("userLogin", userLogin);
					localDispatcher.runSync("createAcctgTransEntry", createTransEntryCtx);
					reciprocalSeqId +=1;
				}
				//update FixedAssetDecrease
				equipmentDecrease.put("acctgTransId", acctgTransId);
				equipmentDecrease.store();
				
				//Post Trans
				Map<String, Object> postTransCtx = FastMap.newInstance();
				postTransCtx.put("acctgTransId", acctgTransId);
				postTransCtx.put("userLogin", userLogin);
				localDispatcher.runSync("postAcctgTrans", postTransCtx);
			} else {
				equipmentDecrease.set("isPosted", Boolean.FALSE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCUnpostedSuccess", locale);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}catch (GenericServiceException e){
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(message);
	}
	
	public static Map<String, Object> createOrStoreEquipmentDecreaseItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		String equipmentId = (String)context.get("equipmentId");
		String equipmentDecreaseId = (String)context.get("equipmentDecreaseId");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		Integer quantityDecreased = (Integer)context.get("quantityDecrease");
		Integer quantityInUse = (Integer)context.get("quantityInUse");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId", equipmentId), false);
			GenericValue equipmentDecreaseItem = delegator.findOne("EquipmentDecreaseItem", UtilMisc.toMap("equipmentId", equipmentId, "equipmentDecreaseId", equipmentDecreaseId), false);
			List<GenericValue> equipmentDecreaseItemTotalList = delegator.findList("EquipmentDecreaseItem",
					EntityCondition.makeCondition(EntityCondition.makeCondition("equipmentDecreaseId", EntityJoinOperator.NOT_EQUAL, equipmentDecreaseId),
							EntityJoinOperator.AND,
							EntityCondition.makeCondition("equipmentId", equipmentId)), null, null, null, false);
			Integer quantityDecreaseCurrent = 0;
			BigDecimal quantityTotalBig = equipment.getBigDecimal("quantity");
			Integer quantityTotal = quantityTotalBig.intValue();
			for(GenericValue equipmentDecreaseItemTotal: equipmentDecreaseItemTotalList){
				quantityDecreaseCurrent += equipmentDecreaseItemTotal.getInteger("quantityDecrease");
			}
			if((quantityDecreaseCurrent + quantityDecreased) > quantityTotal){
				Integer tempQuantityInUse = quantityTotal - quantityDecreaseCurrent;
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentDecreaseQtyGreaterQtyInUse",
						UtilMisc.toMap("equipmentId", equipmentId, "quantityInUse", tempQuantityInUse), locale));
			}
			if(equipmentDecreaseItem != null){
				if(quantityInUse == null){
					quantityInUse = equipmentDecreaseItem.getInteger("quantityInUse");
				}
			}else{
				equipmentDecreaseItem = delegator.makeValue("EquipmentDecreaseItem");
			}
			if(quantityInUse != null && quantityDecreased > quantityInUse){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEquipmentDecreaseQtyMustLessThanQtyInUse", locale));
			}
			equipmentDecreaseItem.setAllFields(context, false, null, null);
			delegator.createOrStore(equipmentDecreaseItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createEquipmentAllocate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentAllocate = delegator.makeValue("EquipmentAllocate");
		equipmentAllocate.setNonPKFields(context);
		String equipmentAllocateId = delegator.getNextSeqId("EquipmentAllocate");
		equipmentAllocate.set("equipmentAllocateId", equipmentAllocateId);
		equipmentAllocate.set("isPosted", Boolean.FALSE);
		try {
			delegator.create(equipmentAllocate);
			retMap.put("equipmentAllocateId", equipmentAllocateId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updateEquipmentAllocate(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String equipmentAllocateId = (String)context.get("equipmentAllocateId");
		Locale locale = (Locale)context.get("locale");
		try {
			GenericValue equipmentAllocate = delegator.findOne("EquipmentAllocate", UtilMisc.toMap("equipmentAllocateId", equipmentAllocateId), false);
			if(equipmentAllocate == null){
				return ServiceUtil.returnError("cannot find Equipment allocate width id: " + equipmentAllocateId);
			}
			Boolean isPosted = equipmentAllocate.getBoolean("isPosted");
			if(isPosted != null && isPosted){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotUpdateWhenPosted", locale));
			}
			equipmentAllocate.setNonPKFields(context, true);
			delegator.store(equipmentAllocate);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> updatePostedEquipmentAllocate(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String equipmentAllocateId = (String) context.get("equipmentAllocateId");
		String isPosted = (String) context.get("isPosted");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue) context.get("userLogin")).getString("userLoginId"));
		String message = null;
		try {
			GenericValue equipmentAllocate = delegator.findOne("EquipmentAllocate", UtilMisc.toMap("equipmentAllocateId", equipmentAllocateId), false);
			if("Y".equals(isPosted)) {
				TransactionUtil.begin();
				equipmentAllocate.set("isPosted", Boolean.TRUE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				
				Map<String, Object> createAccTrans = FastMap.newInstance();
				createAccTrans.put("description", equipmentAllocate.getString("comment"));
				createAccTrans.put("transactionDate", equipmentAllocate.getTimestamp("voucherDate"));
				createAccTrans.put("acctgTransTypeId", "ALLOCATION");
				createAccTrans.put("partyId", organizationPartyId);
				createAccTrans.put("userLogin", context.get("userLogin"));
				createAccTrans.put("glFiscalTypeId", "ACTUAL");

				int reciprocalSeqId = 1;
				String reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				
				List<GenericValue> listItemAllocateStore = delegator.findList("EquipmentAllocItemStoreAndDetail", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId), null, null, null, false);
				List<GenericValue> listItemAllocateParty = delegator.findList("EquipmentAllocItemPartyAndDetail", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId), null, null, null, false);
				List<GenericValue> acctgTransEntries = FastList.newInstance();
				
				for (GenericValue itemStore : listItemAllocateStore) {
					String productStoreId = itemStore.getString("productStoreId");
					String equipmentIdSt = itemStore.getString("equipmentId");
					GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId",equipmentIdSt) , false);
					
					String currencyUomId = equipment.getString("currencyUomId");
					String creditGlAccount = itemStore.getString("creditGlAccountId");
					String debitGlAccount = itemStore.getString("debitGlAccountId");
					BigDecimal amount = itemStore.getBigDecimal("amount");
					
					GenericValue acctgTransEntryC = delegator.makeValue("AcctgTransEntry");
					acctgTransEntryC.put("amount", amount);
					acctgTransEntryC.put("organizationPartyId", organizationPartyId);
					acctgTransEntryC.put("partyId", organizationPartyId);
					acctgTransEntryC.put("productStoreId", productStoreId);
					acctgTransEntryC.put("origCurrencyUomId", currencyUomId);
					acctgTransEntryC.put("currencyUomId", currencyUomId);
					acctgTransEntryC.put("reciprocalSeqId", reciprocalItemSeqId);
										
					acctgTransEntryC.put("debitCreditFlag", "C");
					acctgTransEntryC.put("glAccountId", creditGlAccount);
					acctgTransEntries.add(acctgTransEntryC);
					
					GenericValue acctgTransEntryD = (GenericValue) acctgTransEntryC.clone();
					acctgTransEntryD.put("debitCreditFlag", "D");
					acctgTransEntryD.put("glAccountId", debitGlAccount);					
					acctgTransEntries.add(acctgTransEntryD);
					reciprocalSeqId += 1;
					reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				}
				
				for (GenericValue itemParty : listItemAllocateParty) {
					String partyId = itemParty.getString("partyId");
					String equipmentIdPt = itemParty.getString("equipmentId");
					GenericValue equipment = delegator.findOne("Equipment", UtilMisc.toMap("equipmentId",equipmentIdPt) , false);
					
					String currencyUomId = equipment.getString("currencyUomId");
					String creditGlAccount = itemParty.getString("creditGlAccountId");
					String debitGlAccount = itemParty.getString("debitGlAccountId");
					BigDecimal amount = itemParty.getBigDecimal("amount");
					
					GenericValue acctgTransEntryC = delegator.makeValue("AcctgTransEntry");
					
					acctgTransEntryC.put("amount", amount);
					acctgTransEntryC.put("organizationPartyId", organizationPartyId);
					acctgTransEntryC.put("partyId", partyId);
					acctgTransEntryC.put("origCurrencyUomId", currencyUomId);					
					acctgTransEntryC.put("currencyUomId", currencyUomId);	
					acctgTransEntryC.put("reciprocalSeqId", reciprocalItemSeqId);
					
					acctgTransEntryC.put("debitCreditFlag", "C");
					acctgTransEntryC.put("glAccountId", creditGlAccount);
					acctgTransEntries.add(acctgTransEntryC);
					
					GenericValue acctgTransEntryD = (GenericValue) acctgTransEntryC.clone();
					acctgTransEntryD.put("debitCreditFlag", "D");
					acctgTransEntryD.put("glAccountId", debitGlAccount);
					
					acctgTransEntries.add(acctgTransEntryD);
					
					reciprocalSeqId += 1;
					reciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalSeqId, RECIPROCAL_ITEM_SEQ_ID);
				}
				createAccTrans.put("acctgTransEntries", acctgTransEntries);
				LocalDispatcher localDispatcher = dctx.getDispatcher();
				Map<String, Object> createAccTransResult= localDispatcher.runSync("createAcctgTransAndEntries", createAccTrans);
				
				if(ServiceUtil.isSuccess(createAccTransResult)) {
					 String acctgTransId = (String)createAccTransResult.get("acctgTransId");
					 equipmentAllocate.put("acctgTransId", acctgTransId);
					 equipmentAllocate.store();
					 message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				} else {
					TransactionUtil.rollback();
					return ServiceUtil.returnError("error");
				}
				
			} else {
				return ServiceUtil.returnError("error");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(message);
	}
	
	public static Map<String, Object> deleteEquipmentAllocate(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		String equipmentAllocateId = (String)context.get("equipmentAllocateId");
		try {
			GenericValue equipmentAllocate = delegator.findOne("EquipmentAllocate", UtilMisc.toMap("equipmentAllocateId", equipmentAllocateId), false);
			if(equipmentAllocate == null){
				return ServiceUtil.returnError("cannot find Equipment allocate width id: " + equipmentAllocateId);
			}
			Boolean isPosted = equipmentAllocate.getBoolean("isPosted");
			if(isPosted != null && isPosted){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "BACCCannotDeleteWhenPosted", locale));
			}
			delegator.removeByCondition("EquipmentAllocItemParty", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId));
			delegator.removeByCondition("EquipmentAllocItemStore", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId));
			delegator.removeByCondition("EquipmentAllocItem", EntityCondition.makeCondition("equipmentAllocateId", equipmentAllocateId));
			equipmentAllocate.remove();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgdeletesuccess", locale));
	}
	
	public static Map<String, Object> createOrStoreEquipmentAllocItem(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentAllocItem = delegator.makeValue("EquipmentAllocItem");
		equipmentAllocItem.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentAllocItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createOrStoreEquipmentAllocItemParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentAllocItemParty = delegator.makeValue("EquipmentAllocItemParty");
		equipmentAllocItemParty.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentAllocItemParty);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	public static Map<String, Object> createOrStoreEquipmentAllocItemStore(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		GenericValue equipmentAllocItemStore = delegator.makeValue("EquipmentAllocItemStore");
		equipmentAllocItemStore.setAllFields(context, false, null, null);
		try {
			delegator.createOrStore(equipmentAllocItemStore);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	/**=========================================================**/
	
	public Map<String, Object> createEquipmentAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		String equipAllocationId = (String) context.get("equipAllocationId");
		String equipmentId = (String) context.get("equipmentId");
		BigDecimal alloTimes = (BigDecimal) context.get("alloTimes");
		Timestamp preAllocationDate = (Timestamp) context.get("preAllocationDate");
		BigDecimal monthlyAllocAmount = (BigDecimal) context.get("monthlyAllocAmount");
		BigDecimal amount = (BigDecimal) context.get("amount");
		BigDecimal allocatedAmount = BigDecimal.ZERO;
		BigDecimal remainingValue = amount;
		
		GenericValue equipmentAlloc = delegator.makeValue("EquipmentAllocation");
		equipmentAlloc.set("equipAllocationId", equipAllocationId);
		equipmentAlloc.set("equipmentId", equipmentId);
		equipmentAlloc.set("alloTimes", alloTimes);
		equipmentAlloc.set("preAllocationDate", preAllocationDate);
		equipmentAlloc.set("amount", amount);
		equipmentAlloc.set("allocatedAmount", allocatedAmount);
		equipmentAlloc.set("remainingValue", remainingValue);
		equipmentAlloc.set("monthlyAllocAmount", monthlyAllocAmount);
		if(equipAllocationId == null) {
			equipAllocationId = delegator.getNextSeqId("EquipmentAllocation");
		}
		equipmentAlloc.set("equipAllocationId", equipAllocationId);
		try {
			equipmentAlloc.create();
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("equipAllocationId", equipAllocationId);
		return result;
	}
	
	public Map<String, Object> getEquipCostAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		List<EquipAllocCost> alloCosts = new ArrayList<EquipAllocCost>();
		try {
			List<GenericValue> listEquips = delegator.findList("Equipment", null, null, UtilMisc.toList("partyId ASC"), null, false);
			for(GenericValue equip : listEquips) {
				List<GenericValue> equipPartyAllocs = delegator.findByAnd("EquipmentPartyAlloc", UtilMisc.toMap("equipmentId", equip.getString("equipmentId")), null, false);
				for(GenericValue alloc : equipPartyAllocs) {
					EquipAllocCost allocCost = new EquipAllocCost();
					List<GenericValue> listEquipAllocs = delegator.findByAnd("EquipmentAllocation", UtilMisc.toMap("equipmentId", equip.getString("equipmentId")), null, false);
					List<GenericValue> listEquipPeriods = delegator.findByAnd("EquipmentCustomTimePeriod", UtilMisc.toMap("equipmentId", equip.getString("equipmentId")), null, false);
					GenericValue equipAlloc = listEquipAllocs.get(0);
					BigDecimal allocAmount = BigDecimal.ZERO;
					if(listEquipPeriods.size() > 0) {
						GenericValue equipPeriod = listEquipPeriods.get(0);
						allocAmount = equipPeriod.getBigDecimal("amount");
					}
					BigDecimal amount = equipAlloc.getBigDecimal("amount").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal remainingValue = equipAlloc.getBigDecimal("remainingValue").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal monthlyAllocAmount = equipAlloc.getBigDecimal("monthlyAllocAmount").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					BigDecimal allocatedAmount = equipAlloc.getBigDecimal("allocatedAmount").multiply(alloc.getBigDecimal("allocRate")).divide(BigDecimal.valueOf(100d));
					
					allocCost.setEquipmentId(equip.getString("equipmentId"));
					allocCost.setEquipmentName(equip.getString("equipmentName"));
					allocCost.setAllocDate(equip.getTimestamp("dateAcquired"));
					allocCost.setAmount(amount);
					allocCost.setRemainingValue(remainingValue);
					allocCost.setMonthNumber(equipAlloc.getBigDecimal("alloTimes").longValue());
					allocCost.setMonthlyAllocAmount(monthlyAllocAmount);
					allocCost.setAllowAmount(allocAmount);
					allocCost.setAccumulatedAllocAmount(allocatedAmount);
					allocCost.setUomId(equip.getString("currencyUomId"));
					allocCost.setPartyId(alloc.getString("allocPartyId"));
					if(equipAlloc.getTimestamp("preAllocationDate").after(new Timestamp(Calendar.getInstance().getTimeInMillis()))) {
						allocCost.setPreAccumulatedAllocAmount(allocatedAmount.subtract(allocAmount));
					}else {
						allocCost.setPreAccumulatedAllocAmount(allocatedAmount);
					}
					alloCosts.add(allocCost);
				}
			}
			result = ServiceUtil.returnSuccess();
			result.put("listCostAlloc", alloCosts);
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public Map<String, Object> createEquipmentPartyAlloc(DispatchContext dispatcher, Map<String, Object> context){
		Delegator delegator = dispatcher.getDelegator();
		String equipmentId = (String)context.get("equipmentId");
		String allocPartyId = (String)context.get("allocPartyId");
		BigDecimal allocRate = (BigDecimal)context.get("allocRate");
		String allocGlAccountId = (String)context.get("allocGlAccountId");
		
		GenericValue equipPartyAlloc = delegator.makeValue("EquipmentPartyAlloc");
		delegator.setNextSubSeqId(equipPartyAlloc, "seqId", 5, 1);
		equipPartyAlloc.put("equipmentId", equipmentId);
		//equipPartyAlloc.put("seqId", seqId);
		equipPartyAlloc.put("allocPartyId", allocPartyId);
		equipPartyAlloc.put("allocRate", allocRate);
		equipPartyAlloc.put("allocGlAccountId", allocGlAccountId);
		try {
			equipPartyAlloc.create();
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("equipmentId", equipmentId);
		return result;
	}
	/**================================================================================**/
	
	public static Map<String, Object> createEquipmentType(DispatchContext dctx, Map<String, Object> context){
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = ServiceUtil.returnSuccess(UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
		String equipmentTypeId = (String)context.get("equipmentTypeId");
		String description = (String)context.get("description");
		try {
			if(equipmentTypeId != null){
				equipmentTypeId = equipmentTypeId.trim();
				GenericValue equimentType = delegator.findOne("EquipmentType", UtilMisc.toMap("equipmentTypeId", equipmentTypeId), false);
				if(equimentType != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentTypeIdIsExistsed", UtilMisc.toMap("equipmentTypeId", equipmentTypeId), locale));
				}
			}else{
				equipmentTypeId = delegator.getNextSeqId("EquipmentType");
			}
			description = description.trim();
			List<GenericValue> equimentTypeList = delegator.findByAnd("EquipmentType", UtilMisc.toMap("description", description), null, false);
			if(UtilValidate.isNotEmpty(equimentTypeList)){
				return ServiceUtil.returnError(UtilProperties.getMessage("BaseAccountingUiLabels", "EquipmentTypeIdDescExistsed", UtilMisc.toMap("description", description), locale));
			}
			GenericValue equimentType = delegator.makeValue("EquipmentType");
			equimentType.put("equipmentTypeId", equipmentTypeId);
			equimentType.put("description", description);
			delegator.create(equimentType);
			retMap.put("equipmentTypeId", equipmentTypeId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentIncreaseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String isPosted = parameters.get("isPosted") != null? parameters.get("isPosted")[0] : null;
    	EntityListIterator listIterator = null;
    	try {
    		if("Y".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.TRUE));
    		}else if("N".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.FALSE));
    		}
    		if(UtilValidate.isEmpty(listSortFields)){
    			listSortFields.add("-dateArising");
    		}
			listIterator = delegator.find("EquipmentIncreaseAndItemTotal", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentDecreaseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String isPosted = parameters.get("isPosted") != null? parameters.get("isPosted")[0] : null;
		EntityListIterator listIterator = null;
		try {
			if("Y".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.TRUE));
    		}else if("N".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.FALSE));
    		}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-dateArising");
			}
			listIterator = delegator.find("EquipmentDecreaseAndItemTotal", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentAllocateJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String isPosted = parameters.get("isPosted") != null? parameters.get("isPosted")[0] : null;
		EntityListIterator listIterator = null;
		try {
			if("Y".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.TRUE));
    		}else if("N".equals(isPosted)){
    			listAllConditions.add(EntityCondition.makeCondition("isPosted", Boolean.FALSE));
    		}
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("-voucherDate");
			}
			listIterator = delegator.find("EquipmentAllocateAndTotalAmount", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentNotIncreaseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String equipmentIncreaseId = parameters.get("equipmentIncreaseId") != null? parameters.get("equipmentIncreaseId")[0] : null; 
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("equipmentName");
			}
			List<GenericValue> equipmentIncreaseItemList = delegator.findList("EquipmentIncreaseItem", EntityCondition.makeCondition("equipmentIncreaseId", EntityJoinOperator.NOT_EQUAL, equipmentIncreaseId), null, null, null, false);
			if(UtilValidate.isNotEmpty(equipmentIncreaseItemList)){
				List<String> equipmentIds = EntityUtil.getFieldListFromEntityList(equipmentIncreaseItemList, "equipmentId", true);
				listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds));
			}
			listIterator = delegator.find("EquipmentTypeAndTotalPrice", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentAbilityToAllocateJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String dateStr = parameters.get("date") != null? parameters.get("date")[0] : null;	
		String month = parameters.get("month") != null? parameters.get("month")[0] : null;
		String year = parameters.get("year") != null? parameters.get("year")[0] : null;
		EntityListIterator listIterator = null;
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("equipmentName");
			}
			
			Timestamp date = new Timestamp(System.currentTimeMillis());
			if(dateStr != null){
				date = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(dateStr)));
			}
			
			List<GenericValue> equipmentUsedList = delegator.findList("EquipmentDecreaseItemSumByDate",
					EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, date),
					UtilMisc.toSet("equipmentId", "quantityDecrease", "quantity"), null, null, false);
			if (UtilValidate.isNotEmpty(equipmentUsedList)) {
				Set<String> equipmentIds = FastSet.newInstance();
				for (GenericValue item : equipmentUsedList) {
					if ((new BigDecimal(item.getInteger("quantityDecrease"))).compareTo(item.getBigDecimal("quantity")) >= 0) {
						equipmentIds.add(item.getString("equipmentId"));
					}
				}
				listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds));
			}
			
			List<GenericValue> equipmentAllocateItemList = delegator.findList("EquipmentAllocateAndItem",
					EntityCondition.makeCondition(EntityCondition.makeCondition("month", Integer.valueOf(month)),
							EntityJoinOperator.AND, EntityCondition.makeCondition("year", Integer.valueOf(year))), null, null, null, false);
			if(UtilValidate.isNotEmpty(equipmentAllocateItemList)){
				List<String> equipmentIds = EntityUtil.getFieldListFromEntityList(equipmentAllocateItemList, "equipmentId", true);
				listAllConditions.add(EntityCondition.makeCondition("equipmentId", EntityJoinOperator.NOT_IN, equipmentIds));
			}			
			
			listAllConditions.add(EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, date));
			listIterator = delegator.find("EquipmentAndTypeAndAbilityAllocate", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListEquipmentInUseJQ(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>)context.get("listAllConditions");
		List<String> listSortFields = (List<String>)context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions)context.get("opts");
		EntityListIterator listIterator = null;
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String equipmentDecreaseId = parameters.get("equipmentDecreaseId") != null? parameters.get("equipmentDecreaseId")[0] : null; 
		try {
			if(UtilValidate.isEmpty(listSortFields)){
				listSortFields.add("equipmentName");
			}
			listAllConditions.add(EntityCondition.makeCondition("quantityInUse", EntityJoinOperator.GREATER_THAN, BigDecimal.ZERO));
			EntityCondition conds = EntityCondition.makeCondition(listAllConditions);
			if(equipmentDecreaseId != null){
				List<GenericValue> equipmentDecreaseItemList = delegator.findByAnd("EquipmentDecreaseItem", UtilMisc.toMap("equipmentDecreaseId", equipmentDecreaseId), null, false);
				List<String> equipmentIds = EntityUtil.getFieldListFromEntityList(equipmentDecreaseItemList, "equipmentId", false);
				conds = EntityCondition.makeCondition(conds, EntityJoinOperator.OR, EntityCondition.makeCondition("equipmentId", EntityJoinOperator.IN, equipmentIds));
			}
			listIterator = delegator.find("EquipmentAndDecrTotalAndRemain", conds, null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> getEquipmentOverviewReportData(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = new Timestamp(Long.parseLong(thruDateStr));
		fromDate = UtilDateTime.getDayStart(fromDate);
		thruDate = UtilDateTime.getDayEnd(thruDate, timeZone, locale);
		List<Map<String, Object>> listData = FastList.newInstance();
		try {
			List<GenericValue> equipmentIncreaseAndItemList = delegator.findList("EquipmentIncreaseAndItem", EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate), null, UtilMisc.toList("equipmentId"), null, false);
			List<GenericValue> equipmentDecreaseAndItemList = delegator.findList("EquipmentDecreaseAndItem", EntityCondition.makeCondition("dateArising", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate), null, UtilMisc.toList("-dateArising"), null, false);
			List<GenericValue> equipmentAllocateAndItemList = delegator.findList("EquipmentAllocateAndItem", EntityCondition.makeCondition("voucherDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate), null, UtilMisc.toList("-voucherDate"), null, false);
			BigDecimal sumTotalPrice = BigDecimal.ZERO;
			Integer sumQuantityDecrease = 0;
			BigDecimal sumAllocatedAmount = BigDecimal.ZERO;
			BigDecimal sumDecreaseTotal = BigDecimal.ZERO;
			BigDecimal sumQuantity = BigDecimal.ZERO;
			BigDecimal sumAmountRemain = BigDecimal.ZERO;
			Integer sumQuantityRemain = 0;
			Integer sumAllocationTimeRemain = 0;
			Integer sumAllocatedCount = 0;
			Integer sumAllocationTimes = 0;
			for(GenericValue equipmentIncreaseAndItem: equipmentIncreaseAndItemList){
				Map<String, Object> tempMap = equipmentIncreaseAndItem.getAllFields();
				String equipmentId = equipmentIncreaseAndItem.getString("equipmentId");
				EntityCondition equipmentCond = EntityCondition.makeCondition("equipmentId", equipmentId);
				List<GenericValue> tempEquipmentDecreaseList = EntityUtil.filterByCondition(equipmentDecreaseAndItemList, equipmentCond);
				List<GenericValue> tempEquipmentAllocateList = EntityUtil.filterByCondition(equipmentAllocateAndItemList, equipmentCond);
				BigDecimal unitPrice = equipmentIncreaseAndItem.getBigDecimal("unitPrice");
				BigDecimal totalPrice = equipmentIncreaseAndItem.getBigDecimal("totalPrice");
				Integer allocationTimes =  equipmentIncreaseAndItem.getInteger("allocationTimes");
				BigDecimal quantity = equipmentIncreaseAndItem.getBigDecimal("quantity");
				Integer quantityDecrease = 0;
				BigDecimal allocatedAmount = BigDecimal.ZERO;
				for(GenericValue equipmentDecrease: tempEquipmentDecreaseList){
					quantityDecrease += equipmentDecrease.getInteger("quantityDecrease");
				}
				for(GenericValue equipmentAllocate: tempEquipmentAllocateList){
					allocatedAmount = allocatedAmount.add(equipmentAllocate.getBigDecimal("allocatedAmount"));
				}
				
				BigDecimal decreaseTotal = unitPrice.multiply(new BigDecimal(quantityDecrease));
				BigDecimal amountRemain = totalPrice.subtract(allocatedAmount).subtract(decreaseTotal);
				sumTotalPrice = sumTotalPrice.add(totalPrice);
				sumQuantityDecrease += quantityDecrease;
				sumAllocatedAmount = sumAllocatedAmount.add(allocatedAmount);
				sumDecreaseTotal = sumDecreaseTotal.add(decreaseTotal);
				sumQuantity = sumQuantity.add(quantity);
				sumQuantityRemain +=  quantity.intValue() - quantityDecrease;
				sumAllocationTimeRemain += allocationTimes - tempEquipmentAllocateList.size();
				sumAllocatedCount += tempEquipmentAllocateList.size();
				sumAllocationTimes += allocationTimes;
				sumAmountRemain = sumAmountRemain.add(amountRemain);
				tempMap.put("quantityDecrease", quantityDecrease);
				tempMap.put("decreaseTotal", decreaseTotal);
				tempMap.put("allocatedAmount", allocatedAmount);
				tempMap.put("allocatedCount", tempEquipmentAllocateList.size());
				tempMap.put("allocationTimeRemain", allocationTimes - tempEquipmentAllocateList.size());
				tempMap.put("quantityRemain", quantity.intValue() - quantityDecrease);
				tempMap.put("amountRemain", amountRemain);
				listData.add(tempMap);
			}
			retMap.put("sumTotalPrice", sumTotalPrice);
			retMap.put("sumQuantityDecrease", sumQuantityDecrease);
			retMap.put("sumAllocatedAmount", sumAllocatedAmount);
			retMap.put("sumDecreaseTotal", sumDecreaseTotal);
			retMap.put("sumQuantity", sumQuantity);
			retMap.put("sumQuantityRemain", sumQuantityRemain);
			retMap.put("sumAllocationTimeRemain", sumAllocationTimeRemain);
			retMap.put("sumAllocatedCount", sumAllocatedCount);
			retMap.put("sumAllocationTimes", sumAllocationTimes);
			retMap.put("sumAmountRemain", sumAmountRemain);
			retMap.put("listData", listData);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}
}
