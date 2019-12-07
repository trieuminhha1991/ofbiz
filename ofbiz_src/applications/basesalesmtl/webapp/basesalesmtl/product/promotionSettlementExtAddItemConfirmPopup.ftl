<div id="alterpopupWindowPromoSettleAddItemConfirm" style="display:none">
	<div>${uiLabelMap.BSConfirm}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div id="orderItemConfirmGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_confirm_alterCancel" class='btn form-action-button'><i class="icon-arrow-left"></i> ${uiLabelMap.BSPrev}</button>
	   			<button id="wn_confirm_alterSave" class='btn btn-primary form-action-button'>${uiLabelMap.BSFinish} <i class="icon-arrow-right icon-on-right"></i></button>
	   		</div>
		</div>
	</div>
</div>
<#assign dataFieldConfirm = "[
       		{name: 'productPromoId', type: 'string'},
       		{name: 'productPromoRuleId', type: 'string'},
			{name: 'partyId', type: 'string'},
			{name: 'statusId', type: 'string'},
			{name: 'fromDate', type: 'long'},
			{name: 'fromDate2', type: 'date', other: 'Timestamp'},
       		{name: 'thruDate', type: 'date', other: 'Timestamp'},
       		{name: 'resultEnumId', type: 'string'},
       		{name: 'url', type: 'string'},
       		{name: 'comment', type: 'string'},
       		{name: 'isPay', type: 'string'},
    	]"/>
<#assign columnlistConfirm = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSIsPay)}', dataField: 'isPay', width: '6%', pinned: true, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPromoId)}', dataField: 'productPromoId', width: '14%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPromoRuleId)}', dataField: 'productPromoRuleId', width: '14%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSPartyId)}', dataField: 'partyId', width: '14%', editable:false},
			{ text: '${uiLabelMap.BSCreateDate}', dataField: 'fromDate2', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range', editable:false,
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{ text: '${uiLabelMap.BSCreateDate}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype:'range', editable:false,
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUrl)}', dataField: 'url', width: '20%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSComment)}', dataField: 'comment', editable:true},
		"/>

<script type="text/javascript">
	var orderItemsConfirmOLBG;
	var localDataOrderItemSelected = [];
	$(function(){
		OlbPromoSettleAddItemConfirm.init();
	});
	var OlbPromoSettleAddItemConfirm = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowPromoSettleAddItemConfirm"), {maxWidth: 1120, width: 1120, height: 600, cancelButton: $("#wn_confirm_alterCancel")});
		};
		var initElementComplex = function(){
			var configOrderItems = {
				datafields: ${dataFieldConfirm},
				columns: [${columnlistConfirm}],
				width: '100%',
				height: '490px',
				autoheight: false,
				sortable: true,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: false,
				virtualmode:true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:false,
				editable:true,
			};
			orderItemsConfirmOLBG = new OlbGrid($("#orderItemConfirmGrid"), localDataOrderItemSelected, configOrderItems, []);
		};
		var initEvent = function(){
			$("#wn_confirm_alterSave").on("click", function(){
				var listProd = getListProductAll();
				if (listProd.length > 0) {
					var dataMap = {
						promoSettlementId: "${promoSettlement.promoSettlementId}"
					};
					dataMap.listItems = JSON.stringify(listProd);
					
					$.ajax({
						type: 'POST',
						url: 'addItemToPromoSettle',
						data: dataMap,
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, "default", function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
					        	$("#jqxNotification").jqxNotification("open");
					        	$('body').trigger('addItemToPromoSettleComplete');
					        	closeWindowConfirm();
					        	$("#orderItemConfirmGrid").jqxGrid("updatebounddata");
							});
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					return false;
				}
			});
		};
		var getListProductAll = function(){
			var data = $("#orderItemConfirmGrid").jqxGrid("getboundrows");
			if (typeof(data) == 'undefined') {
				jOlbUtil.alert.info("Error check data");
			}
			
			var listProd = [];
			$.each(data, function (key, value){
				if (typeof(value) != 'undefined' 
						&& OlbCore.isNotEmpty(value.productPromoRuleId) 
						&& OlbCore.isNotEmpty(value.partyId)
						&& OlbCore.isNotEmpty(value.fromDate)) {
					var prodItem = {
						productPromoId: value.productPromoId,
						productPromoRuleId: value.productPromoRuleId,
						partyId: value.partyId,
						fromDate: typeof(value.fromDate) != 'undefined' ? value.fromDate : "",
						isPay: typeof(value.isPay) != 'undefined' ? value.isPay : "",
						comment: typeof(value.comment) != 'undefined' ? value.comment : "",
					};
					listProd.push(prodItem);
				}
			});
			return listProd;
		};
		var openWindowConfirm = function(){
			$("#alterpopupWindowPromoSettleAddItemConfirm").jqxWindow("open");
		};
		var closeWindowConfirm = function(){
			$("#alterpopupWindowPromoSettleAddItemConfirm").jqxWindow("close");
		};
		return {
			init: init,
			openWindowConfirm: openWindowConfirm
		};
	}());
</script>
