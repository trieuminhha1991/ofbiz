<#include 'purchaseDeliveryCommonScript.ftl'>
<script type="text/javascript">
	<#if parameters.orderId?has_content>
		if (typeof inOrderDetail != 'undefined') {
			inOrderDetail = true;
		}
		if (typeof glOrderId != 'undefined') {
			glOrderId = '${parameters.orderId?if_exists}';
		}
	</#if>
	var orderId = '${parameters.orderId?if_exists}';
	
	<#assign orderHeader = delegator.findOne("OrderHeader", false, {"orderId", parameters.orderId?if_exists})>
	var currencyUom = '${orderHeader.currencyUom?if_exists}';
	var currentOrderStatus = '${orderHeader.statusId}';
	<#assign orderItemShipGroup = delegator.findList("OrderItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId?if_exists)), null, null, null, false) />

	var reqFacToReceive = null;
	<#if orderItemShipGroup?has_content>
		reqFacToReceive = '${orderItemShipGroup[0].get("facilityId")?if_exists}';
		<#assign defaultFacilityId = orderItemShipGroup[0].facilityId?if_exists/>
	</#if>
	
	<#if defaultFacilityId?has_content>
		<#assign hasRoles = Static["com.olbius.baselogistics.util.LogisticsFacilityUtil"].checkRoleWithFacility(delegator, defaultFacilityId, userLogin.partyId, "MANAGER")/>
		<#assign facilityTmp = delegator.findOne("Facility", false, {"facilityId", defaultFacilityId?if_exists})>
		facilitySelected = {};
		facilitySelected.facilityId = "${facilityTmp.facilityId?if_exists}";
		facilitySelected.facilityCode = "${facilityTmp.facilityCode?if_exists}";
		facilitySelected.facilityName = "${facilityTmp.facilityName?if_exists}";
	<#else>
		<#assign hasRoles = true>
	</#if>
		
	<#if orderHeader.statusId == "ORDER_CANCELLED">
		<#assign orderItems = delegator.findList("OrderItemAndProductDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId, "statusId", "ITEM_CANCELLED")), null, null, null, false) />
	<#else>
		<#assign orderItems = delegator.findList("OrderItemAndProductDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId, "isPromo", "N", "statusId", "ITEM_APPROVED")), null, null, null, false) />
	</#if>
	
	<#assign orderShipGroups = delegator.findList("OrderItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId)), null, null, null, false) />
	<#assign shipdate = ''/>
	<#if orderShipGroups?has_content>
		<#assign shipdate = orderShipGroups[0].shipByDate?if_exists>
	</#if>

	var orderItemData = new Array();
	<#list orderItems as item>
	
	var row = {};
	row['orderId'] = "${item.orderId}";
	row['orderItemSeqId'] = "${item.orderItemSeqId}";
	<#if (item.estimatedDeliveryDate?has_content)>
		row['estimatedDeliveryDate'] = "${item.estimatedDeliveryDate.getTime()}";
	<#else>
		<#if shipdate?has_content>
			row['estimatedDeliveryDate'] = '${shipdate.getTime()}';
		<#else>
			row['estimatedDeliveryDate'] = null;
		</#if>
	</#if>
	
	row['quantity'] = "${item.quantity?if_exists}";
	row['quantityUomId'] = "${item.quantityUomId?if_exists}";
	row['baseQuantityUomId'] = "${item.baseQuantityUomId?if_exists}";
	row['productWeight'] = "${item.productWeight?if_exists}";
	row['weight'] = "${item.weight?if_exists}";
	row['weightUomId'] = "${item.weightUomId?if_exists}";
	row['expireDate'] = "${item.expireDate?if_exists}";
	
	orderItemData.push(row);
	</#list>
	<#assign suppliers = delegator.findList("OrderRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId?if_exists, "roleTypeId", "SUPPLIER_AGENT")), null, null, null, false)>
	<#assign partyFromId = ''/>
	<#if suppliers?has_content>
		<#assign partyFromId = suppliers[0].partyId>
	</#if>
	var partyFromIdGl = '${partyFromId}';
	<#assign partyFrom = delegator.findOne("PartyNameView", false, {"partyId", partyFromId?if_exists})>
	
	var estimatedDeliveryDate = null;
	var shipBeforeDate = null;
	<#if orderItems?has_content && orderItems?length &gt; 0>
		<#assign firstItem = orderItems.get(0)>
		<#if !firstItem.estimatedDeliveryDate?has_content>
			<#if firstItem.shipAfterDate?has_content>
				var tmp = "${firstItem.shipAfterDate.getTime()}";
				estimatedDeliveryDate = new Date(parseInt(tmp));
			<#else>
				estimatedDeliveryDate = new Date();
			</#if>
		<#else>
			var tmp = "${firstItem.estimatedDeliveryDate.getTime()}";
			estimatedDeliveryDate = new Date(parseInt(tmp));
		</#if>
		<#if firstItem.shipBeforeDate?has_content>
			var tmp = "${firstItem.shipBeforeDate.getTime()}";
			shipBeforeDate = new Date(parseInt(tmp));
		</#if>
	<#else>
		<#assign orderCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", parameters.orderId)>
		<#assign statusCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "ITEM_CANCELLED")>
		<#assign promoCond = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("isPromo", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "Y")>
		<#assign listConds = Static["org.ofbiz.base.util.UtilMisc"].toList(orderCond, statusCond, promoCond)>
		
		<#assign listOrderItemTmp = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listConds, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND), null, null, null, false)>
		<#list listOrderItemTmp as oi>
			<#if oi.estimatedDeliveryDate?has_content?has_content>
				<#assign firstItem = oi>
				<#break>
			</#if>
		</#list>
		<#if firstItem?has_content && !firstItem.estimatedDeliveryDate?has_content>
			<#if firstItem.shipAfterDate?has_content>
				var tmp = "${firstItem.shipAfterDate.getTime()}";
				estimatedDeliveryDate = new Date(parseInt(tmp));
			<#else>
				estimatedDeliveryDate = new Date();
			</#if>
		<#else>
			<#if firstItem?has_content>
				var tmp = "${firstItem.estimatedDeliveryDate.getTime()}";
				estimatedDeliveryDate = new Date(parseInt(tmp));
			<#else>
				estimatedDeliveryDate = new Date();
			</#if>
		</#if>
		<#if firstItem.shipBeforeDate?has_content>
			var tmp = "${firstItem.shipBeforeDate.getTime()}";
			shipBeforeDate = new Date(parseInt(tmp));
		</#if>
	</#if>

	<#assign createdDone = Static["com.olbius.baselogistics.util.LogisticsProductUtil"].checkPurchaseOrderReceipt(delegator, parameters.orderId?if_exists)/>

	<#assign dlvItems = delegator.findList("DeliveryItemView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("fromOrderId", parameters.orderId?if_exists)), null, null, null, false) />
	var dlvItemData = new Array();
	var createdDone = false;
	<#if createdDone>
		createdDone = true;
		<#if dlvItems?has_content>
			<#list dlvItems as item>
				var row = {};
				
				row['deliveryId'] = "${item.deliveryId?if_exists}";
				row['deliveryItemSeqId'] = "${item.deliveryItemSeqId?if_exists}";
				row['fromOrderItemSeqId'] = "${item.fromOrderItemSeqId?if_exists}";
				row['fromOrderId'] = "${item.fromOrderId?if_exists}";
				row['productId'] = "${item.productId?if_exists}";
				row['productName'] = "${item.productName?if_exists}";
				row['quantityUomId'] = "${item.quantityUomId?if_exists}";
				row['comment'] = "${item.comment?if_exists}";
				row['actualExportedQuantity'] = "${item.actualExportedQuantity?if_exists}";
				<#if item.actualDeliveredQuantity?has_content && item.actualDeliveredQuantity != 0>
					row['actualDeliveredQuantity'] = "${item.actualDeliveredQuantity?if_exists}";
				<#else>
					row['actualDeliveredQuantity'] = 0;
				</#if>
				row['statusId'] = "${item.statusId?if_exists}";
				row['quantity'] = "${item.quantity?if_exists}";
				row['inventoryItemId'] = "${item.inventoryItemId?if_exists}";
				row['actualExpireDate'] = "${item.actualExpireDate?if_exists}";
				row['expireDate'] = "${item.expireDate?if_exists}";
				row['deliveryStatusId'] = "${item.deliveryStatusId?if_exists}";
				row['weight'] = "${item.weight?if_exists}";
				row['productWeight'] = "${item.productWeight?if_exists}";
				row['weightUomId'] = "${item.weightUomId?if_exists}";
				row['defaultWeightUomId'] = "${item.defaultWeightUomId?if_exists}";
				
				dlvItemData.push(row);
			</#list>
		</#if>
	</#if>
	
	
	var checkStorekeeper = false;
	var destContactData = new Array();
    
    <#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.storekeeper"), "partyId", userLogin.partyId)), null, null, null, false)/>
	var listFacilityManage = [];
	<#list storeKeeper as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	<#assign admin = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("baselogistics.properties", "role.manager.admin"), "partyId", userLogin.partyId)), null, null, null, false)/>
	<#list admin as item>
	listFacilityManage.push('${item.facilityId}');
	</#list>
</script>