<script type="text/javascript">
    var productOrderMap = {};
    var localData = [];
    var orderItem = {};
    var listQuantityUomIdByProduct = [];
	<#if orderItemSGList?exists && orderItemSGList?size &gt; 0>
		<#assign prodCatalogId = orderItemSGList[0].prodCatalogId?default("")/>
		<#assign defaultItemDeliveryDate = orderItemSGList[0].estimatedDeliveryDate?default("")/>
		<#assign defaultShipBeforeDate = orderItemSGList[0].shipBeforeDate?default("")/>
		<#assign defaultShipAfterDate = orderItemSGList[0].shipAfterDate?default("")/>
		<#list orderItemSGList as orderItem>
			<#if (orderItem.productId?exists) && ("PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId || (!(orderItem.isPromo?exists) || orderItem.isPromo?string == "N"))>
				orderItem = {};
				orderItem.orderItemTypeId = '${orderItem.orderItemTypeId}';
				orderItem.orderId = '${orderItem.orderId}';
				orderItem.orderItemSeqId = '${orderItem.orderItemSeqId}';
				orderItem.shipGroupSeqId = '${orderItem.shipGroupSeqId}';
				orderItem.productId = '${orderItem.productId}';
				orderItem.productCode = '${orderItem.productCode?if_exists}';
				orderItem.productName = '${orderItem.itemDescription?if_exists}';
				orderItem.quantity = '${orderItem.quantity}';
				orderItem.isPromo = '${orderItem.isPromo}';
				orderItem.unitPrice = '${orderItem.unitPrice}';
				<#if orderItem.alternativeQuantity?exists>
					orderItem.quantity = '${orderItem.alternativeQuantity}';
				</#if>
				<#if orderItem.quantityUomId?exists>
					orderItem.quantityUomId = '${orderItem.quantityUomId}';
				</#if>
				<#if orderItem.alternativeUnitPrice?exists>
					orderItem.unitPrice = '${orderItem.alternativeUnitPrice}';
				</#if>
				<#assign condsItem = Static['org.ofbiz.entity.condition.EntityCondition'].makeCondition({"productId": orderItem.get("productId"), "uomToId": orderItem.get("quantityUomId")})/>
				<#assign listConfigPacking = delegator.findList("ConfigPackingAndUom", condsItem, null, null, null, false)!/>
				listQuantityUomIdByProduct = [];
				<#if listConfigPacking?exists>
					<#list listConfigPacking as conPackItem>
						listQuantityUomIdByProduct.push({"description" : '${StringUtil.wrapString(conPackItem.getString("descriptionFrom"))}', "uomId" : '${StringUtil.wrapString(conPackItem.getString("uomFromId"))}'});
					</#list>
				</#if>
				<#assign quantityUom = delegator.findOne("Uom", {"uomId" : orderItem.get("quantityUomId")}, false)!/>
				<#if quantityUom?exists>
					listQuantityUomIdByProduct.push({"description" : '${StringUtil.wrapString(quantityUom.getString("description"))}', "uomId" : '${StringUtil.wrapString(quantityUom.getString("uomId"))}'});
				</#if>
				orderItem.packingUomIds = listQuantityUomIdByProduct;
				localData.push(orderItem);
				
				if (typeof(productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}']) != 'undefined') {
					var itemValue = productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'];
					<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
					itemValue.quantityReturnPromo = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
					<#else>
					itemValue.quantity = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
					</#if>
					productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = itemValue;
				} else {
					productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = {
						productId : '${orderItem.productId?default("")}',
						quantityUomId : '${orderItem.quantityUomId?default("")}',
						<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
						quantityReturnPromo : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
						<#else>
						quantity : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
						</#if>
					};
				}
			</#if>
		</#list>
	</#if>
	
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
		{	uomId: '${uomItem.uomId}',
			description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var cellclass = function (row, columnfield, value) {
 		var data = $('#jqxEditSO').jqxGrid('getrowdata', row);
        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
            return 'background-promo';
        }
    }
</script>

<div id="containerEditSO" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationEditSO" style="margin-bottom:5px">
    <div id="notificationEditSO"></div>
</div>

<div class="row-fluid">
	<div id="jqxEditSO"></div>
</div>

<div class="row-fluid margin-between-block">
	<div class="pull-right form-window-content-custom">
		<button id="alterSaveEdit" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		<button id="alterCancelEdit" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BSExit}</button>
	</div>
</div>

<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <li><i class="fa fa-remove"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}</li>
	</ul>
</div>

<#--${screens.render("component://basesales/widget/OrderScreens.xml#OrderEditPromoChangeItem")}-->
<#include "orderEditAdjustment.ftl"/>
<#include "orderEditAddItemsPopup.ftl"/>
<div id="windowEditContactMech" style="display:none">
	<div>${uiLabelMap.BSConfirmOrder}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="windowEditContactMechContainer"></div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
				<button id="we_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSConfirmation}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "script/orderEditSalesOrderScript.ftl"/>
