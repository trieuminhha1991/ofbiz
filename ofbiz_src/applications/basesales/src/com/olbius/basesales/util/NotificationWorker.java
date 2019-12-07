package com.olbius.basesales.util;

import com.olbius.basesales.party.PartyWorker;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationWorker {
	public static final String module = NotificationWorker.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static enum NotificationTypeEnum {
    	SO("[SO] "), PO("[PO] "), RO("[RO] "), CTKM("[CTKM] "), CTTLTB("[CTTL/CTTB] "), QTKM("[QTKM] "), REQDELORD("[DNGH] "), REQSALESTRANS("[YCDC] "), REQSALES("[YC] ");
    	
    	private String value;
    	private NotificationTypeEnum(String value) {
    		this.value = value;
    	}
    	public String getValue(){
    		return value;
    	}
    };
	
	public static void sendNotifyWhenCreateOrder(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(orderId)) return;
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (orderHeader == null) return;
		String statusId = orderHeader.getString("statusId");
		
		String header = "";
     	String state = "open";
     	String action = "viewOrder?" + "orderId=" + orderId;
     	String targetLink = "";
     	String ntfType = "MANY";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("ORDER_CREATED".equals(statusId)) {
			ntfType = "ONE";
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "APPROVE", userLogin);
			header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSApproveSalesOrder", locale) + " [" + orderId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} else if ("ORDER_APPROVED".equals(statusId)) {
			ntfType = "ONE";
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "RECEIVE_MSG_APPROVED", userLogin);
			header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSHaveNewSalesOrder", locale) + " [" + orderId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		}
	}
	
	public static void sendNotiChangeOrderStatus(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(orderId)) return;
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (orderHeader == null) return;
		String statusId = orderHeader.getString("statusId");
		
		String header = "";
		String state = "open";
		String action = "viewOrder?" + "orderId=" + orderId;
		String targetLink = "";
		String ntfType = "MANY";
		String sendToGroup = "N";
		String sendrecursive = "Y";
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("ORDER_CREATED".equals(statusId)) {
			ntfType = "ONE";
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "APPROVE", userLogin);
			header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSApproveSalesOrder", locale) + " [" + orderId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} else if ("ORDER_APPROVED".equals(statusId)) {
			ntfType = "ONE";
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "RECEIVE_MSG_APPROVED", userLogin);
			header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSHaveNewSalesOrder", locale) + " [" + orderId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		}
		
		// send notification to customer if customer has user login id
		String customerPartyId = null;
		GenericValue customerParty = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "PLACING_CUSTOMER"), null, false));
		if (customerParty != null) customerPartyId = customerParty.getString("partyId");
		if (customerPartyId != null) {
			GenericValue customerUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", customerPartyId), null, false));
			if (customerUserLogin != null) {
				ntfType = "ONE";
				sendToGroup = "Y";
				header = NotificationTypeEnum.PO.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPurchaseOrder", locale) + " [" + orderId + "]";
				NotificationUtil.sendNotify(dispatcher, locale, customerPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			}
		}
	}
	
	public static void sendNotifyWhenCreateOrderToOwner(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(orderId)) return;
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (orderHeader == null) return;
		String statusId = orderHeader.getString("statusId");
		
		String header = "";
     	String state = "open";
     	String action = "viewOrder?" + "orderId=" + orderId;
     	String targetLink = "";
     	String ntfType = "MANY";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		// send notification to owner if owner has user login id
		String ownerPartyId = null;
		GenericValue ownerParty = EntityUtil.getFirst(delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_FROM_VENDOR"), null, false));
		if (ownerParty != null) ownerPartyId = ownerParty.getString("partyId");
		if (ownerPartyId != null) {
			GenericValue ownerUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", ownerPartyId), null, false));
			if (ownerUserLogin != null) {
				if ("ORDER_CREATED".equals(statusId)) {
					ntfType = "ONE";
					sendToGroup = "Y";
					header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSApproveSalesOrder", locale) + " [" + orderId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, ownerPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				} else if ("ORDER_APPROVED".equals(statusId)) {
					ntfType = "ONE";
					sendToGroup = "Y";
					header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSHaveNewSalesOrder", locale) + " [" + orderId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, ownerPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
			}
		}
	}
	
	public static void sendNotifyWhenCreateReturnOrder(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, String returnId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(orderId) || UtilValidate.isEmpty(returnId)) return;
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (orderHeader == null) return;
		
		GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
		if (returnHeader == null) return;
		
		String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue orderOwnerRole = delegator.findOne("OrderRole", UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", organizationId, "roleTypeId", "BILL_FROM_VENDOR"), false);
		if (orderOwnerRole != null) {
			// owner is company
			
			String header = "";
	     	String state = "open";
	     	String action = "viewReturnOrder?returnId=" + returnId;
	     	String targetLink = "";
	     	String ntfType = "ONE";
	     	String sendToGroup = "N";
	     	String sendrecursive = "Y";
	     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	     	
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionReturnOrder(delegator, "RECEIVE_MSG_APPROVED", userLogin);
			StringBuilder headerMsg = new StringBuilder();
			headerMsg.append(NotificationTypeEnum.RO.getValue());
			headerMsg.append(UtilProperties.getMessage(resource, "BSHaveNewReturnOrder", locale));
			headerMsg.append(" [");
			headerMsg.append(returnId);
			headerMsg.append("] ");
			headerMsg.append(UtilProperties.getMessage(resource, "BSFromSalesOrder", locale));
			headerMsg.append(" [");
			headerMsg.append(orderId);
			headerMsg.append("]");
			header =  headerMsg.toString();
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		}
	}
    
	public static Map<String, Object> sendNotiWhenChangePromoStatus(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String productPromoId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
		if (productPromo == null) {
			String errMsg = UtilProperties.getMessage(resource_error, "BSNotFoundProductPromotionHasProductPromoIdIs", UtilMisc.toList(productPromoId), locale);
			return ServiceUtil.returnError(errMsg);
		}
		String statusId = productPromo.getString("statusId");
		
		String header = "";
     	String state = "open";
     	String action = "viewPromotion?productPromoId=" + productPromoId;
     	String targetLink = "";
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("PROMO_CREATED".equals(statusId)) {
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionPromo(delegator, "APPROVE", userLogin);
			header = UtilProperties.getMessage(resource, "BSApprovePromotion", locale) + " [" + productPromoId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} else if ("PROMO_ACCEPTED".equals(statusId)) {
			//List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionPromo(delegator, "RECEIVE_MSG_APPROVED", userLogin);
			//EntityCondition filterByDate = EntityUtil.getFilterByDateExpr();
			//(EntityCondition.makeCondition("fromDate", null), EntityOperator.OR, EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)
			
			EntityCondition filterByDate = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("productPromoId", productPromoId));
			listConds.add(filterByDate);
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStorePromoAppl", EntityCondition.makeCondition(listConds), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				String roleSeller = SalesUtil.getPropertyValue(delegator, "role.sell.in.store");
				
				// get seller of store
				listConds.clear();
				listConds.add(filterByDate);
				listConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				listConds.add(EntityCondition.makeCondition("roleTypeId", roleSeller));
				List<String> sellerIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
				if (UtilValidate.isNotEmpty(sellerIds)) {
					header = NotificationTypeEnum.CTKM.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPromotion", locale) + " [" + productPromoId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, sellerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
				
				// send notification to owner of product store if owner has user login id
				List<String> payToPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStore", EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds)), UtilMisc.toSet("payToPartyId"), null, null, false), "payToPartyId", true);
				if (UtilValidate.isNotEmpty(payToPartyIds)) {
					ntfType = "ONE";
					sendToGroup = "Y";
					header = NotificationTypeEnum.CTKM.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPromotionForYourCustomer", locale) + " [" + productPromoId + "]";
					NotificationUtil.sendNotify(dispatcher, locale, payToPartyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
			}
		} else if ("PROMO_CANCELLED".equals(statusId)) {
			String createdByUserLoginId = productPromo.getString("createdByUserLogin");
			GenericValue createdByUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", createdByUserLoginId), false);
			if (createdByUserLogin != null) {
				String createdByPartyId = createdByUserLogin.getString("partyId");
				if (UtilValidate.isNotEmpty(createdByPartyId)) {
					header = NotificationTypeEnum.CTKM.getValue() + UtilProperties.getMessage(resource, "BSPromotionWasCancelled", locale) + " [" + productPromoId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, createdByPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
			}
		}
		
		return successResult;
	}
	
	public static Map<String, Object> sendNotificationFinishPromotion(Delegator delegator, LocalDispatcher dispatcher, GenericValue productPromo, GenericValue userLogin, Locale locale) throws GenericServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		if (productPromo == null) {
			return successResult;
		}
		
     	List<String> roleTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "roleTypeId.receiveMsg.promo.maximum.limit");
     	if (UtilValidate.isNotEmpty(roleTypeIds)) {
     		List<String> partyIds = SalesPartyUtil.getPartiesByRoles(delegator, roleTypeIds, true, productPromo.getString("organizationPartyId"));
     		if (UtilValidate.isNotEmpty(partyIds)) {
     			String header = "";
     	     	String state = "open";
     	     	String action = "viewPromotion?productPromoId=" + productPromo.getString("productPromoId");
     	     	String targetLink = "";
     	     	String ntfType = "ONE";
     	     	String sendToGroup = "N";
     	     	String sendrecursive = "Y";
     	     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
     	     	
     	     	// create notification alert thru date
                header = NotificationTypeEnum.CTKM .getValue() + UtilProperties.getMessage (resource , "BSPromotionHasFinished" , locale ) + " [" + productPromo.getString("productPromoId") +"]" ;
     			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
     		}
     	}
		
		return successResult;
	}
	
	public static Map<String, Object> sendNotiWhenChangePromoExtStatus(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String productPromoId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		Map<String, Object> successResult = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "BSCreateSuccessful", locale));
		
		GenericValue productPromo = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", productPromoId), false);
		if (productPromo == null) {
			String errMsg = UtilProperties.getMessage(resource_error, "BSNotFoundProductPromotionHasProductPromoIdIs", UtilMisc.toList(productPromoId), locale);
			return ServiceUtil.returnError(errMsg);
		}
		String statusId = productPromo.getString("statusId");
		
		String header = "";
     	String state = "open";
     	String action = "viewPromotionExt?productPromoId=" + productPromoId;
     	String targetLink = "";
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("PROMO_CREATED".equals(statusId)) {
			List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionPromo(delegator, "APPROVE", userLogin);
			header = NotificationTypeEnum.CTTLTB.getValue() + UtilProperties.getMessage(resource, "BSApprovePromotion", locale) + " [" + productPromoId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} else if ("PROMO_ACCEPTED".equals(statusId)) {
			//List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionPromo(delegator, "RECEIVE_MSG_APPROVED", userLogin);
			
			EntityCondition filterByDate = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("productPromoId", productPromoId));
			listConds.add(filterByDate);
			List<String> productStoreIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStorePromoExtAppl", EntityCondition.makeCondition(listConds), UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				String roleSeller = SalesUtil.getPropertyValue(delegator, "role.sell.in.store");
				
				// get seller of store
				listConds.clear();
				listConds.add(filterByDate);
				listConds.add(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds));
				listConds.add(EntityCondition.makeCondition("roleTypeId", roleSeller));
				List<String> sellerIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listConds), UtilMisc.toSet("partyId"), null, null, false), "partyId", true);
				if (UtilValidate.isNotEmpty(sellerIds)) {
					header = NotificationTypeEnum.CTTLTB.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPromotionExt", locale) + " [" + productPromoId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, sellerIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
				
				// send notification to owner of product store if owner has user login id
				List<String> payToPartyIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductStore", EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds)), UtilMisc.toSet("payToPartyId"), null, null, false), "payToPartyId", true);
				if (UtilValidate.isNotEmpty(payToPartyIds)) {
					ntfType = "ONE";
					sendToGroup = "Y";
					header = NotificationTypeEnum.CTTLTB.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPromotionExtForYourCustomer", locale) + " [" + productPromoId + "]";
					NotificationUtil.sendNotify(dispatcher, locale, payToPartyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
					
					// get sup of distributor
					List<String> supIds = PartyWorker.getSupIdsByDistributor(delegator, payToPartyIds);
					if (UtilValidate.isNotEmpty(supIds)) {
						header = NotificationTypeEnum.CTTLTB.getValue() + UtilProperties.getMessage(resource, "BSHaveNewPromotionExt", locale) + " [" + productPromoId +"]";
						NotificationUtil.sendNotify(dispatcher, locale, supIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
					}
				}
			}
		} else if ("PROMO_CANCELLED".equals(statusId)) {
			String createdByUserLoginId = productPromo.getString("createdByUserLogin");
			GenericValue createdByUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", createdByUserLoginId), false);
			if (createdByUserLogin != null) {
				String createdByPartyId = createdByUserLogin.getString("partyId");
				if (UtilValidate.isNotEmpty(createdByPartyId)) {
					header = NotificationTypeEnum.CTTLTB.getValue() + UtilProperties.getMessage(resource, "BSPromotionExtWasCancelled", locale) + " [" + productPromoId +"]";
					NotificationUtil.sendNotify(dispatcher, locale, createdByPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
			}
		}

		return successResult;
	}
	
	public static void sendNotifyWhenCreatePromoSettlement(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String promoSettlementId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(promoSettlementId)) return;
		sendNotifyWhenCreatePromoSettlement(delegator, dispatcher, locale, promoSettlementId, null, userLogin);
	}
	public static void sendNotifyWhenCreatePromoSettlement(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue promoSettlement, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(promoSettlement)) return;
		sendNotifyWhenCreatePromoSettlement(delegator, dispatcher, locale, null, promoSettlement, userLogin);
	}
	private static void sendNotifyWhenCreatePromoSettlement(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String promoSettlementId, GenericValue promoSettlement, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(promoSettlementId) && UtilValidate.isEmpty(promoSettlement)) return;
		
		if (promoSettlement == null) promoSettlement = delegator.findOne("ProductPromoSettlement", UtilMisc.toMap("promoSettlementId", promoSettlementId), false);
		if (promoSettlement == null) return;
		
		promoSettlementId = promoSettlement.getString("promoSettlementId");
		String statusId = promoSettlement.getString("statusId");
		String productPromoId = promoSettlement.getString("productPromoId");
		String productPromoExtId = promoSettlement.getString("productPromoExtId");
		
		String header = "";
     	String state = "open";
     	String action = "viewPromoSettle?" + "promoSettlementId=" + promoSettlementId;
     	String targetLink = "";
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("PSETTLE_CREATED".equals(statusId)) {
			//List<String> partyIds = SalesPartyUtil.getPartiesHavePermissionByActionOrder(delegator, "APPROVE", userLogin);
			//header = NotificationTypeEnum.QTKM.getValue() + " " + UtilProperties.getMessage(resource, "BSApprovePromotionSettlement", locale) + " [" + promoSettlementId +"]";
			//NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		} else if ("PSETTLE_PROCESSING".equals(statusId)) {
			sendrecursive = "N";
			
			// send to Supervisor
			List<String> partyIds = new ArrayList<String>();
			List<String> productStoreIds = new ArrayList<String>();
			
			// get promotion
			if (UtilValidate.isNotEmpty(productPromoId)) {
				productStoreIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("ProductStorePromoAppl", 
								EntityCondition.makeCondition(EntityCondition.makeCondition("productPromoId", productPromoId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), 
								UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			} else if (UtilValidate.isNotEmpty(productPromoExtId)) {
				productStoreIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("ProductStorePromoExtAppl", 
								EntityCondition.makeCondition(EntityCondition.makeCondition("productPromoId", productPromoExtId), EntityOperator.AND, EntityUtil.getFilterByDateExpr()), 
								UtilMisc.toSet("productStoreId"), null, null, false), "productStoreId", true);
			}
			// get owner of productStore in promotion
			List<String> ownerProductStoreIds = null;
			if (UtilValidate.isNotEmpty(productStoreIds)) {
				ownerProductStoreIds = EntityUtil.getFieldListFromEntityList(
						delegator.findList("ProductStore", EntityCondition.makeCondition("productStoreId", EntityOperator.IN, productStoreIds), 
								UtilMisc.toSet("payToPartyId"), null, null, false), "payToPartyId", true);
			}
			// get sup of distributor
			if (UtilValidate.isNotEmpty(ownerProductStoreIds)) {
				partyIds = PartyWorker.getSupIdsByDistributor(delegator, ownerProductStoreIds);
			}
			
			header = NotificationTypeEnum.QTKM.getValue() + UtilProperties.getMessage(resource, "BSExecutePromotionSettlement", locale) + " [" + promoSettlementId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
		}
		/*
		 else if ("PSETTLE_APPROVED".equals(statusId)) {
			
		} else if ("PSETTLE_COMPLETED".equals(statusId)) {
			
		}
		*/
	}
	
	public static void sendNotifyWhenCreateReqDeliveryOrder(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String requirementId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(requirementId)) return;
		
		GenericValue orderHeader = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
		if (orderHeader == null) return;
		
		String header = "";
		String state = "open";
		String action = "viewReqDeliveryOrder?" + "requirementId=" + requirementId;
		String targetLink = "";
		String ntfType = "ONE";
		String sendToGroup = "N";
		String sendrecursive = "Y";
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<String> partyIds = SalesPartyUtil.getPartiesHavePermReqDeliveryOrder(delegator, "APPROVE", userLogin);
		header = NotificationTypeEnum.REQDELORD.getValue() + UtilProperties.getMessage(resource, "BSApproveReqDeliveryOrder", locale) + " [" + requirementId +"]";
		NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
	}
	
	public static void sendNotifyWhenCreateReqSalesTransfer(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String requirementId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(requirementId)) return;
		
		GenericValue orderHeader = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
		if (orderHeader == null) return;
		
		String header = "";
		String state = "open";
		String action = "viewReqSalesTransfer?" + "requirementId=" + requirementId;
		String targetLink = "";
		String ntfType = "ONE";
		String sendToGroup = "N";
		String sendrecursive = "Y";
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<String> partyIds = SalesPartyUtil.getPartiesHavePermReqDeliveryOrder(delegator, "APPROVE", userLogin);
		header = NotificationTypeEnum.REQSALESTRANS.getValue() + UtilProperties.getMessage(resource, "BSApproveReqSalesTransfer", locale) + " [" + requirementId +"]";
		NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
	}
	
	public static void sendNotifiWhenCreateRequirement(Delegator delegator, LocalDispatcher dispatcher, Locale locale, GenericValue requirement, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(requirement)) return;
		String statusId = requirement.getString("statusId");
		
		String header = "";
     	String state = "open";
     	String action = "viewReqSalesTransfer?" + "requirementId=" + requirement.getString("requirementId");
     	String targetLink = "";
     	String ntfType = "ONE";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
     	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		if ("REQ_CREATED".equals(statusId)) {
			// send notification to owner of facility
			if (UtilValidate.isEmpty(requirement.get("facilityId"))) return;
			sendToGroup = "Y";
			GenericValue ownerParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", requirement.get("facilityId")), false);
			if (ownerParty != null) {
				header = NotificationTypeEnum.REQSALES.getValue() + UtilProperties.getMessage(resource, "BSApproveRequirement", locale) + " [" + requirement.getString("requirementId") +"]";
				NotificationUtil.sendNotify(dispatcher, locale, ownerParty.getString("ownerPartyId"), header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			}
		} else if ("REQ_APPROVED".equals(statusId)) {
			// send notification to supervisor of distributor
			if (UtilValidate.isEmpty(requirement.get("facilityId"))) return;
			sendToGroup = "Y";
			GenericValue ownerParty = delegator.findOne("Facility", UtilMisc.toMap("facilityId", requirement.get("facilityId")), false);
			if (ownerParty != null) {
				String distributorId = ownerParty.getString("ownerPartyId");
				List<String> supIds = PartyWorker.getSupIdsByDistributor(delegator, distributorId);
				if (UtilValidate.isNotEmpty(supIds)) {
					header = NotificationTypeEnum.REQSALES.getValue() + UtilProperties.getMessage(resource, "BSProcessRequirement", locale) + " [" + requirement.getString("requirementId") +"]";
					NotificationUtil.sendNotify(dispatcher, locale, supIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
				}
			}
		}
	}
	
	public static void sendNotifySOUpdateAfterPOModified(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
		if (UtilValidate.isEmpty(orderId)) return;
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (orderHeader == null) return;
		// String statusId = orderHeader.getString("statusId");
		
		String userLoginIdCreatedBy = orderHeader.getString("createdBy");
		if (UtilValidate.isNotEmpty(userLoginIdCreatedBy)) {
			GenericValue userLoginCreatedBy = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginIdCreatedBy), false);
			String partyId = userLoginCreatedBy.getString("partyId");
			if (UtilValidate.isNotEmpty(partyId)) {
				String header = "";
				String state = "open";
				String action = "viewOrder?" + "orderId=" + orderId;
				String targetLink = "";
				String ntfType = "ONE";
				String sendToGroup = "N";
				String sendrecursive = "Y";
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
				header = UtilProperties.getMessage(resource, "BSHaveSalesOrderWasEditedFollowPO", locale) + " [" + orderId +"]";
				NotificationUtil.sendNotify(dispatcher, locale, partyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			}
		}
	}

    public static void sendNotifyWhenCreateTrip(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String tripId, GenericValue userLogin) throws GenericEntityException, GenericServiceException {
        if (UtilValidate.isEmpty(tripId)) return;

        GenericValue orderHeader = delegator.findOne("Trip", UtilMisc.toMap("tripId", tripId), false);
        if (orderHeader == null) return;

        String header = "";
        String state = "open";
        String action = "viewTrip?" + "tripId=" + tripId;
        String targetLink = "";
        String ntfType = "ONE";
        String sendToGroup = "N";
        String sendRecursive = "Y";
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        List<String> partyIds = SalesPartyUtil.getPartiesHavePermReqDeliveryOrder(delegator, "APPROVE", userLogin);
        header = NotificationTypeEnum.REQDELORD.getValue() + UtilProperties.getMessage(resource, "BSApproveReqDeliveryOrder", locale) + " [" + tripId +"]";
        NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendRecursive, nowTimestamp, userLogin);
    }
}
