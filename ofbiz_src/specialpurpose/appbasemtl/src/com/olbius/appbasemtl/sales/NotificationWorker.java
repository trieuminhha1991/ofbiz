package com.olbius.appbasemtl.sales;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

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

import com.olbius.basesales.util.NotificationUtil;
import com.olbius.basesales.util.NotificationWorker.NotificationTypeEnum;

import javolution.util.FastList;

import com.olbius.basesales.util.SalesPartyUtil;

public class NotificationWorker {
	public static final String module = NotificationWorker.class.getName();
    public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    
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
			
			List<EntityCondition> listCond = FastList.newInstance();
			listCond.add(EntityUtil.getFilterByDateExpr());
			listCond.add(EntityCondition.makeCondition("productStoreId", orderHeader.getString("productStoreId")));
			listCond.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", "SELLER"), EntityOperator.OR, EntityCondition.makeCondition("roleTypeId", "MANAGER")));
			List<String> partyIds = EntityUtil.getFieldListFromEntityList(
					delegator.findList("ProductStoreRole", EntityCondition.makeCondition(listCond), null, null, null, false), "partyId", true);
			
			header = NotificationTypeEnum.SO.getValue() + UtilProperties.getMessage(resource, "BSApproveSalesOrder", locale) + " [" + orderId +"]";
			NotificationUtil.sendNotify(dispatcher, locale, partyIds, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, nowTimestamp, userLogin);
			
			sendNotiChangeOrderStatusToCustomer(delegator, dispatcher, locale, orderId, nowTimestamp, userLogin);
		} else if ("ORDER_SADAPPROVED".equals(statusId)) {
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
	
	public static void sendNotiChangeOrderStatusToCustomer(Delegator delegator, LocalDispatcher dispatcher, Locale locale, String orderId, Timestamp orderDate, GenericValue userLogin) throws GenericEntityException, GenericServiceException{
		String header = "";
     	String state = "open";
     	String action = "viewOrder?" + "orderId=" + orderId;
     	String targetLink = "";
     	String ntfType = "MANY";
     	String sendToGroup = "N";
     	String sendrecursive = "Y";
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
				NotificationUtil.sendNotify(dispatcher, locale, customerPartyId, header, state, action, targetLink, ntfType, sendToGroup, sendrecursive, orderDate, userLogin);
			}
		}
	}
}
