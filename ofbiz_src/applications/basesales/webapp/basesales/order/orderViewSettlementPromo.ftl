<#if security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_VIEW", session) && hasPromoSettlement>
<div id="ordersettlecommit-tab" class="tab-pane<#if activeTab?exists && activeTab == "ordersettlecommit-tab"> active</#if>">
	<#assign dataField = "[
				{name: 'orderSettlementCommitId', type: 'string'}, 
				{name: 'promoSettlementResultId', type: 'string'}, 
				{name: 'quantityAccept', type: 'string'}, 
				{name: 'orderId', type: 'string'}, 
				{name: 'orderItemSeqId', type: 'string'}, 
				{name: 'productId', type: 'string'}, 
				{name: 'quantity', type: 'string'}, 
			]"/>
	<#assign columnlist = "
				{text: '${uiLabelMap.BSResultId}', dataField: 'orderSettlementCommitId', width: '12%'},
				{text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', width: '12%'},
				{text: '${uiLabelMap.BSOrderItemSeqId}', dataField: 'orderItemSeqId', width: '12%'},
				{text: '${uiLabelMap.BSPromoSettlementResultId}', dataField: 'promoSettlementResultId', width: '12%'},
				{text: '${uiLabelMap.BSProductId}', dataField: 'productId'},
				{text: '${uiLabelMap.BSQuantityAccept}', dataField: 'quantityAccept', width: '12%'},
				{text: '${uiLabelMap.BSPaidQty}', dataField: 'quantity', width: '12%'},
	  		"/>
	<#assign tmpDelete = "false"/>
	<#assign customcontrol1 = ""/>
	<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED">
		<#if security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_DELETE", session)>
			<#assign tmpDelete = "true"/>
		</#if>
		<#if security.hasEntityPermission("PROMOSETTLEMENT_ORDER", "_CREATE", session)>
			<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddItem}@javascript: void(0);@OlbOrderSettlement.addItem();">
		</#if>
	</#if>
	<@jqGrid id="jqxGridOrderSettelement" customTitleProperties="${uiLabelMap.BSAggregateResult}" 
			url="jqxGeneralServicer?sname=JQListOrderSettlementCommitment&orderId=${orderHeader.orderId}" columnlist=columnlist dataField=dataField 
			viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
			deleterow=tmpDelete deleteColumn="orderSettlementCommitId" removeUrl="jqxGeneralServicer?sname=removeItemOrderSettlementCommitment&jqaction=C" 
			customcontrol1=customcontrol1 customcontrol2="" mouseRightMenu="false" contextMenuId="contextMenu" jqGridMinimumLibEnable="true"/>
</div>

<div id="alterpopupWindowOrderSettleAddItem" style="display:none">
	<div>${uiLabelMap.BSCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<input type="hidden" id="wn_productId" value=""/>
				<div class="span11 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSOrderItemSeqId}</label>
						</div>
						<div class='span7'>
							<div id="wn_orderItemSeqId">
								<div id="wn_orderItemGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPromoSettlementResultId}</label>
						</div>
						<div class='span7'>
							<div id="wn_promoSettlementResultId">
								<div id="wn_promoSettlementResultGrid"></div>
							</div>
				   		</div>
					</div>
				</div><!--.span12-->
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	var orderItemDDB;
	var promoSettlementResultDDB;
	$(function(){
		OlbOrderSettlement.init();
	});
	var OlbOrderSettlement = (function(){
		var validatorVAL;
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowOrderSettleAddItem"), {width: 600, height: 200, cancelButton: $("#wn_alterCancel")});
		};
		var initElementComplex = function(){
			var configCustomer = {
				widthButton: '100%',
				root: 'results',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{name: 'orderId', type: 'string'}, 
					{name: 'orderItemSeqId', type: 'string'}, 
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
					{name: 'itemDescription', type: 'string'},
					{name: 'quantity', type: 'string'}
				],
				columns: [
					{text: '${uiLabelMap.BSSTT}', dataField: '', width: '10%', columntype: 'number', 
	                	cellsrenderer: function (row, column, value) {
	               			return '<div class="innerGridCellContent">' + (value + 1) + '</div>';
	                	}
					},
					{text: "${uiLabelMap.BSProductId}", datafield: 'productCode', width: '25%'},
					{text: "${uiLabelMap.BSDescription}", datafield: 'itemDescription'},
					{text: "${uiLabelMap.BSQuantity}", datafield: 'quantity', width: '25%', cellsalign: 'right', cellsformat: 'd'},
				],
				useUrl: true,
				url: 'JQListOrderItem&orderId=${orderHeader.orderId}',
				useUtilFunc: true,
				
				key: 'orderItemSeqId',
				description: ['productCode'],
				autoCloseDropDown: true,
				filterable: false
			};
			orderItemDDB = new OlbDropDownButton($("#wn_orderItemSeqId"), $("#wn_orderItemGrid"), null, configCustomer, []);
			
			var configPromoSettlementResult = {
				widthButton: '100%',
				root: 'results',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{name: 'promoSettlementResultId', type: 'string'}, 
					{name: 'partyId', type: 'string'}, 
					{name: 'productId', type: 'string'}, 
					{name: 'productCode', type: 'string'}, 
					{name: 'quantityAccept', type: 'string'},
				],
				columns: [
					{text: "${uiLabelMap.BSProductId}", datafield: 'productCode', width: '25%'},
					{text: "${uiLabelMap.BSQuantityAccept}", datafield: 'quantityAccept', width: '25%', cellsalign: 'right', cellsformat: 'd'},
					{text: "${uiLabelMap.BSPartyId}", datafield: 'partyId', width: '25%'},
					{text: "${uiLabelMap.BSPromoSettlementResultId}", datafield: 'promoSettlementResultId', width: '25%'},
				],
				useUrl: true,
				url: 'JQListPromoSettlementResultForReturn&partyId=${displayParty.partyId}',
				useUtilFunc: true,
				
				key: 'promoSettlementResultId',
				description: ['promoSettlementResultId', 'partyId', 'productCode'],
				autoCloseDropDown: true,
				filterable: true
			};
			promoSettlementResultDDB = new OlbDropDownButton($("#wn_promoSettlementResultId"), $("#wn_promoSettlementResultGrid"), null, configPromoSettlementResult, []);
		};
		var initValidateForm = function(){
			var mapRules = [
		            {input: '#wn_orderItemSeqId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#wn_promoSettlementResultId', type: 'validObjectNotNull', objType: 'dropDownButton'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowOrderSettleAddItem'), mapRules, []);
		};
		var initEvent = function(){
			$("#wn_orderItemGrid").on('rowselect', function (event) {
				var args = event.args;
				if (args) {
					var rowData = $("#wn_orderItemGrid").jqxGrid('getrowdata', args.rowindex);
					if (rowData) {
						$("#wn_productId").val(rowData.productId);
						promoSettlementResultDDB.updateSource("jqxGeneralServicer?sname=JQListPromoSettlementResultForReturn&partyId=${displayParty.partyId}&productId=" + rowData.productId);
					};
				}
		    });
		    $("#wn_alterSave").on("click", function(){
		    	if (!validatorVAL.validate()) return false;
		    	$.ajax({
					type: 'POST',
					url: 'addItemOrderSettlementCommitment',
					data: {
						orderId: "${orderHeader.orderId}",
						orderItemSeqId: orderItemDDB.getValue(),
						promoSettlementResultId: promoSettlementResultDDB.getValue()
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, 
							function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	$("#jqxGridOrderSettelement").jqxGrid("updatebounddata");
					        	$("#alterpopupWindowOrderSettleAddItem").jqxWindow("close");
							}
						);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
		    });
		};
		var addItem = function(){
			$("#alterpopupWindowOrderSettleAddItem").jqxWindow("open");
		};
		return {
			init: init,
			addItem: addItem
		}
	}());
</script>
</#if>