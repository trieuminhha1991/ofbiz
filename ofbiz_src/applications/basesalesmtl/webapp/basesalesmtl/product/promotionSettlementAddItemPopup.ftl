<div id="alterpopupWindowPromoSettleAddItem" style="display:none">
	<div>${uiLabelMap.BSAddItemIntoPromotionSettlement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div id="orderItemGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'>${uiLabelMap.BSNext} <i class="icon-arrow-right icon-on-right"></i></button>
	   		</div>
		</div>
	</div>
</div>
<#assign dataField = "[
       		{name: 'sellerId', type: 'string'},
       		{name: 'customerId', type: 'string'},
			{name: 'orderDate', type: 'date', other: 'Timestamp'},
			{name: 'orderId', type: 'string'},
			{name: 'orderItemSeqId', type: 'string'},
       		{name: 'productId', type: 'string'},
       		{name: 'productCode', type: 'string'},
       		{name: 'quantityActual', type: 'string'},
       		{name: 'comments', type: 'string'},
    	]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSDistributorId)}', dataField: 'sellerId', width: '14%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', dataField: 'customerId', width: '14%'},
			{ text: '${uiLabelMap.BSCreateDate}', dataField: 'orderDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', dataField: 'orderId', width: '14%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSOrderItemSeqId)}', dataField: 'orderItemSeqId', width: '14%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '14%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantityActual', width: '14%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSComment)}', dataField: 'comments', width: '20%'},
		"/>
<#assign contextMenuItemId = "ctxmnuoi">
<div id='contextMenuOrderItemGrid' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_pay"><i class="fa fa-check"></i>${StringUtil.wrapString(uiLabelMap.BSPay)}</li>
	    <li id="${contextMenuItemId}_notpay"><i class="fa fa-times"></i>${StringUtil.wrapString(uiLabelMap.BSNotPay)}</li>
	</ul>
</div>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true/>
<#include "promotionSettlementAddItemConfirmPopup.ftl"/>
<script type="text/javascript">
	var orderItemsOLBG;
	$(function(){
		OlbPromoSettleAddItem.init();
	});
	var OlbPromoSettleAddItem = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowPromoSettleAddItem"), {maxWidth: 1120, width: 1120, height: 600, cancelButton: $("#wn_alterCancel")});
		};
		var initElementComplex = function(){
			var configOrderItems = {
				datafields: ${dataField},
				columns: [${columnlist}],
				width: '100%',
				height: '500px',
				autoheight: false,
				sortable: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: true,
				useUrl: true,
				url: 'JQListOrderItemPromoNeedSettleByRole&promoSettlementId=${promoSettlement.promoSettlementId}',
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:false,
				selectionmode: 'multiplerows',
				contextMenu: "contextMenuOrderItemGrid",
				configcontextmenu: {selectrow: false}
			};
			orderItemsOLBG = new OlbGrid($("#orderItemGrid"), null, configOrderItems, []);
			
			$("#contextMenuOrderItemGrid").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', popupZIndex: 99999999});
		};
		var initEvent = function(){
			$("#wn_alterSave").on("click", function(){
				OlbPromoSettleAddItemConfirm.openWindowConfirm();
			});
			$("body").on("addItemToPromoSettleComplete", function(){
				closeWindow();
				$("#jqxPromoSettelementDetail").jqxGrid("updatebounddata");
			});
			$("#contextMenuOrderItemGrid").on('itemclick', function (event) {
				var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var gridObj = $("#orderItemGrid");
	        	var rowIndexes = gridObj.jqxGrid('getselectedrowindexes');
	        	if (rowIndexes == null || rowIndexes == undefined || rowIndexes.length <= 0) {
	        		jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}!");
	        		return false;
	        	}
	        	var rowscount = gridObj.jqxGrid('getdatainformation').rowscount;
	        	switch(tmpId) {
	        		case "${contextMenuItemId}_pay": {
	        			var deleteRowIds = [];
						for (var i = 0; i < rowIndexes.length; i++) {
							var itemData = gridObj.jqxGrid("getrowdata", i);
							var itemId = gridObj.jqxGrid('getrowid', i);
							if (itemData != null && OlbCore.isNotEmpty(itemData.orderId) && OlbCore.isNotEmpty(itemData.orderItemSeqId)) {
								var itemTmp = {
									sellerId: itemData.sellerId,
									customerId: itemData.customerId,
									orderDate: itemData.orderDate,
									orderId: itemData.orderId,
									orderItemSeqId: itemData.orderItemSeqId,
									productId: itemData.productId,
									productCode: itemData.productCode,
									quantityActual: itemData.quantityActual,
									quantityApprove: itemData.quantityActual,
									isPay: "Y",
								};
								localDataOrderItemSelected.push(itemTmp);
								deleteRowIds.push(itemId);
							}
						}
						gridObj.jqxGrid('clearselection');
						for (var i = 0; i < deleteRowIds.length; i++) {
							if (OlbCore.isNotEmpty(deleteRowIds[i])) {
								gridObj.jqxGrid('deleterow', deleteRowIds[i]);
							}
						}
						OlbPromoSettleAddItemConfirm.openWindowConfirm();
						orderItemsConfirmOLBG.updateSource(null, localDataOrderItemSelected);
						break;
					};
	        		case "${contextMenuItemId}_notpay": {
	        			var deleteRowIds = [];
	        			for (var i = 0; i < rowIndexes.length; i++) {
	        				var itemData = gridObj.jqxGrid("getrowdata", i);
	        				var itemId = gridObj.jqxGrid('getrowid', i);
							if (itemData != null && OlbCore.isNotEmpty(itemData.orderId) && OlbCore.isNotEmpty(itemData.orderItemSeqId)) {
								var itemTmp = {
									sellerId: itemData.sellerId,
									customerId: itemData.customerId,
									orderDate: itemData.orderDate,
									orderId: itemData.orderId,
									orderItemSeqId: itemData.orderItemSeqId,
									productId: itemData.productId,
									productCode: itemData.productCode,
									quantityActual: itemData.quantityActual,
									isPay: "N",
								};
								localDataOrderItemSelected.push(itemTmp);
								deleteRowIds.push(itemId);
							}
						}
						gridObj.jqxGrid('clearselection');
						for (var i = 0; i < deleteRowIds.length; i++) {
							if (OlbCore.isNotEmpty(deleteRowIds[i])) {
								gridObj.jqxGrid('deleterow', deleteRowIds[i]);
							}
						}
						OlbPromoSettleAddItemConfirm.openWindowConfirm();
						orderItemsConfirmOLBG.updateSource(null, localDataOrderItemSelected);
						break;
					};
	        		default: break;
	        	}
			});
		};
		var closeWindow = function(){
			$("#alterpopupWindowPromoSettleAddItem").jqxWindow("close");
		};
		return {
			init: init,
		};
	}());
</script>
<#--
url: 'jqxGeneralServicer?sname=JQListOrderItemPromoNeedSettleByRole',
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				statusbarheight: 30,
				virtualmode:false,
rendertoolbar: function(toolbar){
					<@renderToolbar id="orderItemGrid" isShowTitleProperty="false" customTitleProperties="" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="alterpopupWindow" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="false" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
				},
-->