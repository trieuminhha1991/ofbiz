<style type="text/css">
	.checkbox-custom {
		opacity: 1 !important; position: initial !important;
		margin-bottom: 8px !important;
	}
</style>

<div class="row-fluid">
	<div class="span12">
		<#if orh?exists>
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class="row-fluid">
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSOrderId}:</label>
							</div>
							<div class="div-inline-block">
								<span><b><a href="<@ofbizUrl>viewOrder?orderId=${orderId}</@ofbizUrl>" target="_blank">${orderId}</a></b></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderOrderTotal}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderGrandTotal() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderAmountAlreadyCredited}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderReturnedCreditTotalBd() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.OrderAmountAlreadyRefunded}:</label>
							</div>
							<div class="div-inline-block">
								<span><@ofbizCurrency amount=orh.getOrderReturnedRefundTotalBd() isoCode=orh.getCurrency()/></span>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSOrderTotalSelected}:</label>
							</div>
							<div class="div-inline-block">
								<span id="valOrderTotalSelected"></span>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-fluid-->
			</div><!--.form-horizontal-->
		</#if>
	</div><!--.span12-->
</div><!--.row-fluid-->

<#if returnableItems?has_content>
	<div class="row-fluid">
		<div class="span12">
			<div id="jqxNewReturnItems"></div>
			<span class="tooltip checkbox-custom">*&nbsp;${uiLabelMap.BSPriceNotIncludeTax}</span>
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span12">
			<div id="jqxgridOrderAdjustment"></div>
		</div>
	</div>
	
	<div class="margin-top20 margin-bottom20 pull-right">
		<a href="javascript:void(0);" id="btnCreateReturnOrder" class="btn btn-small btn-primary">${uiLabelMap.OrderReturnSelectedItems}</a>
	</div>
	
	<div id="alterpopupWindowReturnAdjAdd" style="display:none">
		<div>${uiLabelMap.BSAddOtherAdjustment}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<input type="hidden" id="wn_man_returnItemTypeId" value="RET_MAN_ADJ">
		        <input type="hidden" id="wn_man_returnItemSeqId" value="_NA_">
		        <input type="hidden" id="wn_man_returnTypeId" value="RTN_REFUND">
				<div class="row-fluid">
					<div class="span12 form-window-content-custom">
						<div class='row-fluid'>
							<div class='span3'>
								<label class="required">${uiLabelMap.BSAmount}</label>
							</div>
							<div class='span9'>
								<div id="wn_man_amount"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span3'>
								<label class="required">${uiLabelMap.BSDescription}</label>
							</div>
							<div class='span9'>
								<textarea id="wn_man_description" rows="3" class="span12"></textarea>
					   		</div>
						</div>
					</div>
				</div>
			</div>
		   	<div class="form-action">
		   		<div class="pull-right form-window-content-custom">
		   			<button id="wn_man_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
					<button id="wn_man_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		   		</div>
			</div>
		</div>
	</div>
<#else>
	<div class="alert alert-info">${uiLabelMap.OrderReturnNoReturnableItems} #${orderId}</div>
</#if>

<#if returnableItems?has_content>
<script type="text/javascript">
	var itemsTabCellclass = function (row, columnfield, value) {
 		var data = $('#jqxNewReturnItems').jqxGrid('getrowdata', row);
        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
            return 'background-promo';
        }
    }
	
	<#assign returnReasonDefault = ""/>
	var returnReasonData = [
		<#if returnReasons?exists>
		<#list returnReasons as reason>
			<#if reason_index == 0><#assign returnReasonDefault = reason.returnReasonId/></#if>
			{	"returnReasonId": "${reason.returnReasonId}",
				"description": "${reason.get("description",locale)?default(reason.returnReasonId)}"
			},
		</#list>
		</#if>
	];
	var formatPrice = function(strPrice){
        var price = strPrice.replace(",", ".");
        return price;
    }
	var returnOrderItemData = [
		<#list returnableItems.keySet() as orderItem>
		<#if orderItem.getEntityName() != "OrderAdjustment">
			<#-- this is an order item -->
        	<#assign returnItemType = (returnItemTypeMap.get(returnableItems.get(orderItem).get("itemTypeKey")))?if_exists/>
        	<#assign orderHeader = orderItem.getRelatedOne("OrderHeader", false)>
        	<#assign currencyDefault = orderHeader.currencyUom?default("")/>
        	<#assign product = orderItem.getRelatedOne("Product", false)!/>
        	<#if product.productTypeId == "ASSET_USAGE_OUT_IN">
        		<#assign returnPrice = 0/>
          	<#else>
          		<#assign returnPrice = returnableItems.get(orderItem).get("returnablePrice")?default(0)/>
			</#if>
        	{	"returnItemTypeId": "${returnItemType}",	
        		"orderId": "${orderItem.orderId}",
        		"orderItemSeqId": "${orderItem.orderItemSeqId}",
        		"description": "${orderItem.itemDescription?if_exists}",
        		"productId": "${orderItem.productId?if_exists}",
        		"productCode": "${product.productCode?if_exists}",
        		"quantity": "${orderItem.quantity?if_exists}",
        		"returnableQuantity": "${returnableItems.get(orderItem).get("returnableQuantity")}",
        		"returnQuantity": "",
        		"unitPrice": formatPrice("${orderItem.unitPrice}"),
        		"returnPrice": formatPrice("${returnPrice}"),
        		"returnReasonId": "${returnReasonDefault}",
        		"returnTypeId": "RTN_REFUND",
        		"expectedItemStatus": "INV_RETURNED",
        		"currencyUomId": "${currencyDefault}",
        		"isPromo": "${orderItem.isPromo?default("N")}",
        	},
        </#if>
		</#list>
	];
	var orderAdjustmentData = [
		<#if orderHeaderAdjustments?exists>
		<#list orderHeaderAdjustments as itemAdj>
			<#assign returnAdjustmentType = returnItemTypeMap.get(itemAdj.get("orderAdjustmentTypeId"))/>
	        <#assign adjustmentType = itemAdj.getRelatedOne("OrderAdjustmentType", false)/>
	        <#assign description = itemAdj.description?default(adjustmentType.get("description",locale))/>
			{	"returnAdjustmentTypeId": "${returnAdjustmentType?if_exists}",
				"returnItemSeqId": "_NA_",
				"orderAdjustmentId": "${itemAdj.orderAdjustmentId}",
				"description": "${description?if_exists}",
				"comments": "${itemAdj.comments?if_exists}",
				"amount": "${itemAdj.amount?if_exists}",
				"returnTypeId": "RTN_REFUND",
			},
		</#list>
		</#if>
	];
	
	var OlbReturnOrderItem = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementAdvance();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.numberInput.create($("#wn_man_amount"), {width: '98%', spinButtons: false, digits: 8, decimalDigits: 3, allowNull: true});
			jOlbUtil.windowPopup.create($("#alterpopupWindowReturnAdjAdd"), {width: 540, height: 240, cancelButton: $("#wn_man_alterCancel")});
		};
		var initElementAdvance = function(){
			var datafields = [
				{ name: 'returnItemTypeId', type: 'string'},
				{ name: 'orderId', type: 'string'},
				{ name: 'orderItemSeqId', type: 'string'},
				{ name: 'description', type: 'string'},
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'quantity', type: 'string'},
				{ name: 'unitPrice', type: 'string'},
				{ name: 'returnableQuantity', type: 'number', formatter: 'integer'},
				{ name: 'returnQuantity', type: 'number', formatter: 'integer'},
				{ name: 'returnPrice', type: 'string'},
				{ name: 'returnReasonId', type: 'string'},
				{ name: 'returnTypeId', type: 'string'},
				{ name: 'expectedItemStatus', type: 'string'},
				{ name: 'currencyUomId', type: 'string'},
				{ name: 'isPromo', type: 'string'},
	       	];
	       	var columns = [
	       		{ text: "${uiLabelMap.BSProductId}", dataField: 'productCode', width: '12%', editable:false, cellclassname: itemsTabCellclass},
		 		{ text: "${uiLabelMap.CommonDescription}", dataField: 'description', editable:false, cellclassname: itemsTabCellclass},
			 	{ text: "${uiLabelMap.OrderUnitPrice}", dataField: 'unitPrice', width: '10%', editable:false, cellsalign: 'right', cellsformat: 'c', cellclassname: itemsTabCellclass, 
				 	cellsrenderer: function(row, column, value) {
				 		var returnValue = '<div class=\"innerGridCellContent align-right\">';
				 		var data = $('#jqxNewReturnItems').jqxGrid('getrowdata', row);
				 		if (typeof(data) != 'undefined') {
					 		returnValue += formatcurrency(value, data.currencyUomId);
				 		} else {
							returnValue += value;
						}
						returnValue += '</div>';
						return returnValue;
				 	}
			 	},
			 	{ text: "${uiLabelMap.OrderReturnPrice} * ", dataField: 'returnPrice', width: '10%', editable:false, cellsalign: 'right', cellsformat: 'c', cellclassname: itemsTabCellclass, 
				 	cellsrenderer: function(row, column, value) {
				 		var returnValue = '<div class=\"innerGridCellContent align-right\">';
				 		var data = $('#jqxNewReturnItems').jqxGrid('getrowdata', row);
				 		if (typeof(data) != 'undefined') {
					 		returnValue += formatcurrency(value, data.currencyUomId);
				 		} else {
							returnValue += value;
						}
						returnValue += '</div>';
						return returnValue;
				 	}
			 	},
			 	{ text: "${uiLabelMap.BSOrderedQty}", dataField: 'quantity', width: '10%', editable:false, cellsalign: 'right', cellsformat: 'd', cellclassname: itemsTabCellclass,
			 		cellsrenderer: function(row, column, value){
		   				return '<div class="innerGridCellContent align-right">' + formatnumber(value) + '</div>';
				 	}
				},
			 	{ text: "${uiLabelMap.BSReturnableQty}", dataField: 'returnableQuantity', width: '10%', editable: false, cellsalign: 'right', filterable:false, sortable: false, cellsformat: 'd', cellclassname: itemsTabCellclass,
			 		cellsrenderer: function(row, column, value){
		   				return '<div class="innerGridCellContent align-right">' + formatnumber(value) + '</div>';
				 	},
			 	},
			 	{ text: "${uiLabelMap.BSReturnQty}", dataField: 'returnQuantity', width: '12%', editable:true, cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd', cellclassname: itemsTabCellclass,
			 		cellsrenderer: function(row, column, value){
		   				return '<div class="innerGridCellContent align-right">' + formatnumber(value) + '</div>';
				 	},
					validation: function (cell, value) {
						var data = $('#jqxNewReturnItems').jqxGrid('getrowdata', cell.row);
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanOrEqualZero}'};
						} else if (data != undefined && data.quantity != undefined) {
							if (value > data.returnableQuantity) {
								return {result: false, message: '${uiLabelMap.BSQuantityMustBeLessThanOrEqualReturnableQuantity}'};
							}
						}
						return true;
					},
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					}
			 	},
			 	{ text: "${uiLabelMap.OrderReturnReason}", dataField: 'returnReasonId', width: '200', editable:true, columntype:'dropdownlist', filterable:false, sortable:false, cellclassname: itemsTabCellclass, 
		   		 	cellsrenderer: function(row, column, value){
						var data = $('#jqxNewReturnItems').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent\">';
			   			for (var i = 0 ; i < returnReasonData.length; i++){
							if (value == returnReasonData[i].returnReasonId){
								returnVal += returnReasonData[i].description + '</div>';
		   						return returnVal;
							}
						}
			   			returnVal += value + '</div>';
		   				return returnVal;
					},
		   		 	createeditor: function (row, cellvalue, editor) {
				 		var sourceDataPacking = {
			                localdata: returnReasonData,
			                datatype: "array"
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({source: dataAdapterPacking, displayMember: 'description', valueMember: 'returnReasonId', dropDownWidth: '400', selectedIndex: 0});
                  	}
			 	},
	       	];
			var configReturnItem = {
				dropDownHorizontalAlignment: 'right',
				datafields: datafields,
				columns: columns,
				useUrl: false,
				clearfilteringbutton: false,
				editable: true,
				sortable: true,
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				editmode: 'click',
				selectionmode: 'checkbox',
				width: '100%',
				bindresize: true,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSListProduct)}"
				},
				<#--
				rendertoolbar: function(toolbar){
					<@renderToolbar id="jqxNewReturnItems" isShowTitleProperty="true" 
						customTitleProperties="${uiLabelMap.BSListProduct}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
                },
				-->
			};
			new OlbGrid($("#jqxNewReturnItems"), returnOrderItemData, configReturnItem, []);
			
			var datafields = [
				{ name: 'returnAdjustmentTypeId', type: 'string'},
				{ name: 'returnItemTypeId', type: 'string'},
				{ name: 'returnItemSeqId', type: 'string'},
				{ name: 'orderAdjustmentId', type: 'string'},
				{ name: 'description', type: 'string'},
				{ name: 'comments', type: 'string'},
				{ name: 'amount', type: 'number'},
				{ name: 'returnTypeId', type: 'string'},
	       	];
	       	var columns = [
		 		{ text: "${uiLabelMap.BSDescription}", dataField: 'description', editable:false},
		 		{ text: "${uiLabelMap.BSComment}", dataField: 'comments', width: '30%', editable:false},
			 	{ text: "${uiLabelMap.BSAmount}", dataField: 'amount', width: '20%', editable:false, cellsalign: 'right', cellsformat: 'c', 
				 	cellsrenderer: function(row, column, value) {
				 		var returnValue = '<div class=\"innerGridCellContent align-right\">';
				 		var data = $('#jqxgridOrderAdjustment').jqxGrid('getrowdata', row);
				 		if (typeof(data) != 'undefined') {
					 		returnValue += formatcurrency(value, data.currencyUomId);
				 		} else {
							returnValue += value;
						}
						returnValue += '</div>';
						return returnValue;
				 	}
			 	},
	       	];
			var configOrderAdj = {
				dropDownHorizontalAlignment: 'right',
				datafields: datafields,
				columns: columns,
				useUrl: false,
				clearfilteringbutton: false,
				editable: true,
				sortable: true,
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				editmode: 'click',
				selectionmode: 'checkbox',
				width: '100%',
				bindresize: true,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
				rendertoolbar: function(toolbar){
					<#assign grid2Customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:void(0);@OlbReturnOrderItem.openWindowAddReturnAdj()">
					<@renderToolbar id="jqxgridOrderAdjustment" isShowTitleProperty="true" 
						customTitleProperties="${uiLabelMap.BSListAdjustment}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=grid2Customcontrol1 customcontrol2="" customcontrol3="" customtoolbaraction=""/>
                },
			};
			new OlbGrid($("#jqxgridOrderAdjustment"), orderAdjustmentData, configOrderAdj, []);
		};
		var initEvent = function(){
			$("#btnCreateReturnOrder").on("click", function(){
				$("#btnCreateReturnOrder").addClass("disabled");
				var listProd = [];
				var numProd = 0;
				
				// list product
				var rowIndexes = $('#jqxNewReturnItems').jqxGrid('getselectedrowindexes');
				if (rowIndexes.length > 0) {
					for (var i = 0; i < rowIndexes.length; i++) {
						$("#jqxNewReturnItems").jqxGrid('endcelledit', rowIndexes[i], "quantity", true, true);
					}
					
					for (var i = 0; i < rowIndexes.length; i++) {
						var dataItem = $('#jqxNewReturnItems').jqxGrid('getrowdata', rowIndexes[i]);
						if (OlbCore.isNotEmpty(dataItem.orderId) && OlbCore.isNotEmpty(dataItem.orderItemSeqId) && OlbCore.isNotEmpty(dataItem.returnQuantity)) {
							if (parseInt(dataItem.returnQuantity) > 0) {
								var prodItem = {
									returnItemTypeId: dataItem.returnItemTypeId,
									orderId: dataItem.orderId,
									orderItemSeqId: dataItem.orderItemSeqId,
									<#--description: dataItem.description,-->
									productId: dataItem.productId,
									returnQuantity: dataItem.returnQuantity,
									returnPrice: dataItem.returnPrice,
									returnTypeId: dataItem.returnTypeId,
									returnReasonId: dataItem.returnReasonId,
									expectedItemStatus: dataItem.expectedItemStatus,
								};
								listProd.push(prodItem);
								numProd++;
							}
						}
					}
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					$("#btnCreateReturnOrder").removeClass("disabled");
					return false;
				}
				
				// list return adjustment
				var rowIndexes2 = $('#jqxgridOrderAdjustment').jqxGrid('getselectedrowindexes');
				if (OlbCore.isNotEmpty(rowIndexes2)) {
					for (var i = 0; i < rowIndexes2.length; i++) {
						$("#jqxgridOrderAdjustment").jqxGrid('endcelledit', rowIndexes2[i], "quantity", true, true);
					}
					
					for (var i = 0; i < rowIndexes2.length; i++) {
						var dataItem = $('#jqxgridOrderAdjustment').jqxGrid('getrowdata', rowIndexes2[i]);
						if ((OlbCore.isNotEmpty(dataItem.orderAdjustmentId) || (dataItem.returnItemTypeId == "RET_MAN_ADJ")) && OlbCore.isNotEmpty(dataItem.amount)) {
							if (dataItem.amount != 0) {
								var prodItem = {
									returnAdjustmentTypeId: dataItem.returnAdjustmentTypeId,
									returnItemTypeId: dataItem.returnItemTypeId,
									returnItemSeqId: dataItem.returnItemSeqId,
									orderAdjustmentId: dataItem.orderAdjustmentId,
									description: dataItem.description,
									amount: dataItem.amount,
									returnTypeId: dataItem.returnTypeId,
								};
								listProd.push(prodItem);
							}
						}
					}
				}
				
				if (listProd.length > 0 && numProd > 0) {
					$("#btnCreateReturnOrder").removeClass("disabled");
					jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}?", function(){
						$("#btnCreateReturnOrder").addClass("disabled");
						var dataMap = {
							returnId: "${returnId?if_exists}",
							orderId: "${orderId?if_exists}",
							_useRowSubmit: "Y",
						};
						
						dataMap.listProduct = JSON.stringify(listProd);
						
						$.ajax({
							type: 'POST',
							url: 'createReturnItemsCustomerAjax',
							data: dataMap,
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
									        	$('#containerNewRO').empty();
									        	$('#jqxNotificationNewRO').jqxNotification({ template: 'error'});
									        	$("#jqxNotificationNewRO").html(errorMessage);
									        	$("#jqxNotificationNewRO").jqxNotification("open");
									        	
									        	$("#btnCreateReturnOrder").removeClass("disabled");
									        	return false;
											}, function(){
												$('#containerNewRO').empty();
							    	        	$('#jqxNotificationNewRO').jqxNotification({ template: 'info'});
							    	        	$("#jqxNotificationNewRO").html("${uiLabelMap.wgupdatesuccess}");
							    	        	$("#jqxNotificationNewRO").jqxNotification("open");
							    	        	
							    	        	if (OlbCore.isNotEmpty(data.returnId)) {
							    	        		window.location.href = 'viewReturnOrder?returnId=' + data.returnId;
							    	        	}
							    	        	return true;
											});
							},
							error: function(data){
								alert("Send request is error");
								$("#btnCreateReturnOrder").removeClass("disabled");
							},
							complete: function(data){
								$("#loader_page_common").hide();
							},
						});
						return true;
					});
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSQuantityMustBeGreaterThanZero}");
					$("#btnCreateReturnOrder").removeClass("disabled");
					return false;
				}
			});
			
			$('#jqxNewReturnItems').on("rowselect", function(event){
				calculateTotalSelected();
			});
			$("#jqxNewReturnItems").on("rowunselect", function(event){
				calculateTotalSelected();
			});
			$("#jqxNewReturnItems").on("cellendedit", function (event) {
				var args = event.args;
				var newValue = args.value;
		    	if (args.datafield == "returnQuantity") {
		    		var rowBoundIndex = args.rowindex;
		    		if (newValue > 0) {
		    			$('#jqxNewReturnItems').jqxGrid('selectrow', rowBoundIndex);
		    		} else {
		    			$('#jqxNewReturnItems').jqxGrid('unselectrow', rowBoundIndex);
		    		}
					calculateTotalSelected(rowBoundIndex, newValue);
				}
			});
			
			var calculateTotalSelected = function(rowBoundIndex, newValue) {
				var totalValue = 0;
				
				var rowIndexes = $('#jqxNewReturnItems').jqxGrid('getselectedrowindexes');
				if (rowIndexes.length > 0) {
					for (var i = 0; i < rowIndexes.length; i++) {
					var indexNumber = rowIndexes[i];
						var dataItem = $('#jqxNewReturnItems').jqxGrid('getrowdata', indexNumber);
						if (OlbCore.isNotEmpty(dataItem.orderId) && OlbCore.isNotEmpty(dataItem.orderItemSeqId)) {
							if (OlbCore.isNotEmpty(dataItem.returnQuantity) && (parseInt(dataItem.returnQuantity) > 0)) {
								var returnPrice = dataItem.returnPrice;
								var returnQuantity = dataItem.returnQuantity;
								if (dataItem.isPromo == "N") {
									totalValue += returnPrice * returnQuantity;
								} else {
									totalValue -= returnPrice * returnQuantity;
								}
							} else if (OlbCore.isNotEmpty(rowBoundIndex) && OlbCore.isNotEmpty(newValue) && rowBoundIndex > -1 && (parseInt(newValue) > 0)) {
								var returnPrice = dataItem.returnPrice;
								if (dataItem.isPromo == "N") {
									totalValue += returnPrice * newValue;
								} else {
									totalValue -= returnPrice * newValue;
								}
							}
						}
					}
				}
				var totalValueStr = formatcurrency(totalValue , "${orh.getCurrency()?if_exists}");
				$("#valOrderTotalSelected").text(totalValueStr);
			}
			
			$("#wn_man_alterSave").on("click", function(){
				if (!validatorVAL.validate()) {
					return false;
				};
				var newValue = {
					"returnItemTypeId": $("#wn_man_returnItemTypeId").val(),
					"returnItemSeqId": $("#wn_man_returnItemSeqId").val(),
					"returnTypeId": $("#wn_man_returnTypeId").val(),
					"amount": $("#wn_man_amount").val(),
					"description": $("#wn_man_description").val(),
				};
				$("#alterpopupWindowReturnAdjAdd").jqxWindow("close");
				$("#jqxgridOrderAdjustment").jqxGrid("addrow", null, newValue);
				clearWindowAddReturnAdj();
			});
		};
		var openWindowAddReturnAdj = function(){
			$("#alterpopupWindowReturnAdjAdd").jqxWindow("open");
		};
		var clearWindowAddReturnAdj = function(){
			$("#wn_man_amount").jqxNumberInput("val", 0);
			$("#wn_man_description").val("");
		};
		var initValidateForm = function(){
			var extendRules = [
				{input: '#wn_man_amount', message: "${uiLabelMap.BSQuantityMustBeGreaterThanZero}", action: 'keyup', 
					rule: function(input, commit){
						var value = $(input).val();
						if(value != 0){
							return true;
						}
						return false;
					}
				}
			];
			var mapRules = [
					{input: '#wn_man_amount', type: 'validInputNotNull'},
					{input: '#wn_man_description', type: 'validInputNotNull'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowReturnAdjAdd'), mapRules, extendRules, {position: 'bottom'});
		};
		return {
			init: init,
			openWindowAddReturnAdj: openWindowAddReturnAdj,
		};
	}());
	
	$(function(){
		OlbReturnOrderItem.init();
	});
</script>
</#if>
<#--
		<input type="checkbox" class="checkbox-custom" name="_rowSubmit_o_${rowCount}" value="Y" onclick="javascript:checkToggle(this, '${selectAllFormName}');highlightRow(this,'returnItemId_tableRow_${rowCount}');"/>
      	
		<input type="hidden" name="returnItemTypeId_o_${rowCount}" value="${returnItemType}"/>
    	<input type="hidden" name="orderId_o_${rowCount}" value="${orderItem.orderId}"/>
    	<input type="hidden" name="orderItemSeqId_o_${rowCount}" value="${orderItem.orderItemSeqId}"/>
    	<input type="hidden" name="description_o_${rowCount}" value="${orderItem.itemDescription?if_exists}"/>
      	<#if orderItem.productId?exists>
        	<input type="hidden" name="productId_o_${rowCount}" value="${orderItem.productId}"/>
      	</#if>
      	
      	<input type="text" class="width100px align-right" size="6" name="returnQuantity_o_${rowCount}" value="${returnableItems.get(orderItem).get("returnableQuantity")}"/>
      	
      	<#if orderItem.productId?exists>
          	<#assign product = orderItem.getRelatedOne("Product", false)/>
          	<#if product.productTypeId == "ASSET_USAGE_OUT_IN">
            	<input type="hidden" size="8" name="returnPrice_o_${rowCount}" class="width100px" value="0.00"/>
          	<#else>
          		<#assign unitPriceOrderItem = returnableItems.get(orderItem).get("returnablePrice")?default(0)/>
            	<input type="hidden" size="8" name="returnPrice_o_${rowCount}" class="width100px" value="${unitPriceOrderItem}"/>
			</#if>
    	</#if>
    	
    	<select name="returnReasonId_o_${rowCount}">
      		<#list returnReasons as reason>
      		<option value="${reason.returnReasonId}">${reason.get("description",locale)?default(reason.returnReasonId)}</option>
      		</#list>
		</select>
		<input type="hidden" name="returnTypeId_o_${rowCount}" value="RTN_REFUND"/>
		<input type="hidden" name="expectedItemStatus_o_${rowCount}" value="INV_RETURNED"/>
		
		<select name="returnTypeId_o_${rowCount}">
      		<#list returnTypes as type>
      			<option value="${type.returnTypeId}" <#if type.returnTypeId=="RTN_REFUND">selected="selected"</#if>>${type.get("description",locale)?default(type.returnTypeId)}</option>
      		</#list>
		</select>
		<select name="expectedItemStatus_o_${rowCount}">
      		<option value="INV_RETURNED">${uiLabelMap.OrderReturned}</option>
      		<option value="INV_RETURNED">---</option>
      		<#list itemStts as status>
        		<option value="${status.statusId}">${status.get("description",locale)}</option>
      		</#list>
		</select>
	-->