
<div class="row-fluid margin-top20">
	<div class="span12">
		<div id="jqxgridOrderAdjustment"></div>
	</div>
</div>

<div class="row-fluid margin-between-block">
	<div class="pull-right form-window-content-custom">
		<button id="wn_oeadj_alterSaveEdit" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		<button id="wn_oeadj_alterCancelEdit" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BSExit}</button>
	</div>
</div>

<div id="alterpopupWindowOrderAdjAdd" style="display:none">
	<div>${uiLabelMap.BSAddOtherAdjustment}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAdjustmentType}</label>
						</div>
						<div class='span7'>
							<div id="wn_oeadj_orderAdjustmentTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<textarea id="wn_oeadj_description" rows="3" class="span12"></textarea>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSAmount}</label>
						</div>
						<div class='span7'>
							<div id="wn_oeadj_amount"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_oeadj_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_oeadj_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#--{ name: 'shipGroupSeqId', type: 'string'},-->
<script type="text/javascript">
	$(function(){
		OlbOrderEditAdjustment.init();
	});
	var OlbOrderEditAdjustment = (function(){
		var orderAdjustmentTypeDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.numberInput.create($("#wn_oeadj_amount"), {width: '98%', spinButtons: false, digits: 8, decimalDigits: 3, allowNull: true});
			jOlbUtil.windowPopup.create($("#alterpopupWindowOrderAdjAdd"), {width: 540, height: 280, cancelButton: $("#wn_oeadj_alterCancel")});
		};
		var initElementComplex = function(){
			<#assign orderAdjustmentTypeIds = Static["com.olbius.basesales.util.SalesUtil"].getPropertyProcessedMultiKey(delegator, "order.adjustment.changeable")/>
			<#assign listOrderAdjustmentType = delegator.findList("OrderAdjustmentType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderAdjustmentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, orderAdjustmentTypeIds), null, null, null, false)!/>
			var orderAdjustmentTypeData = [
			<#if listOrderAdjustmentType?exists>
				<#list listOrderAdjustmentType as item>
				{	"orderAdjustmentTypeId" : "${item.orderAdjustmentTypeId}", 
					"description" : "${StringUtil.wrapString(item.get("description", locale))}"},
				</#list>
			</#if>
			];
			var configAdjustmentType = {
				width: '99%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				useUrl: false,
				key: 'orderAdjustmentTypeId',
				value: 'description',
				autoDropDownHeight: true
			}
			orderAdjustmentTypeDDL = new OlbDropDownList($("#wn_oeadj_orderAdjustmentTypeId"), orderAdjustmentTypeData, configAdjustmentType, []);
			
			var configOrderAdj = {
				dropDownHorizontalAlignment: 'right',
				datafields: [
					{ name: 'orderAdjustmentId', type: 'string'},
					{ name: 'orderAdjustmentTypeId', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'amount', type: 'number'},
		       	],
				columns: [
			 		{ text: "${uiLabelMap.BSAdjustmentType}", dataField: 'orderAdjustmentTypeId', width: 300, editable:false,
			 			cellsrenderer: function(row, column, value){
							if (orderAdjustmentTypeData.length > 0) {
								for(var i = 0 ; i < orderAdjustmentTypeData.length; i++){
	    							if (value == orderAdjustmentTypeData[i].orderAdjustmentTypeId){
	    								return '<span title =\"' + orderAdjustmentTypeData[i].description +'\">' + orderAdjustmentTypeData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
			 		},
			 		{ text: "${uiLabelMap.BSAmount}", dataField: 'amount', width: 300, editable:true, cellsalign: 'right', cellsformat: 'c', columntype: 'numberinput', 
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
					 	},
					 	createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({decimalDigits: 0, digits: 9});
						}
				 	},
			 		{ text: "${uiLabelMap.BSDescription}", dataField: 'description', minWidth: 200, editable:false},
		       	],
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQListOrderAdjustmentOlb&orderId=${orderHeader.orderId?if_exists}',
				clearfilteringbutton: false,
				editable: true,
				sortable: true,
				pageable: true,
				pagesize: 15,
				showtoolbar: false,
				editmode: 'click',
				width: '100%',
				bindresize: true,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
				rendertoolbar: function(toolbar){
					<#assign grid2Customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAddNew}@javascript:void(0);@OlbOrderEditAdjustment.openWindowAddOrderAdj()">
					<#assign grid2Customcontrol2 = "icon-trash open-sans@${uiLabelMap.BSDelete}@javascript:void(0);@OlbOrderEditAdjustment.removeOrderAdjustment()">
					<@renderToolbar id="jqxgridOrderAdjustment" isShowTitleProperty="true" 
						customTitleProperties="${uiLabelMap.BSListAdjustment}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=grid2Customcontrol1 customcontrol2=grid2Customcontrol2 customcontrol3="" customtoolbaraction=""/>
                },
			};
			new OlbGrid($("#jqxgridOrderAdjustment"), null, configOrderAdj, []);
		};
		var initEvent = function(){
			$("#wn_oeadj_alterSave").on("click", function(){
				if (!validatorVAL.validate()) {
					return false;
				};
				var newValue = {
					"orderAdjustmentTypeId": orderAdjustmentTypeDDL.getValue(),
					"amount": $("#wn_oeadj_amount").val(),
					"description": $("#wn_oeadj_description").val(),
				};
				$("#alterpopupWindowOrderAdjAdd").jqxWindow("close");
				$("#jqxgridOrderAdjustment").jqxGrid("addrow", null, newValue);
				clearWindowAddOrderAdj();
			});
			$('#wn_oeadj_alterCancelEdit').on('click', function(){
				window.open('viewOrder?orderId=${orderHeader.orderId?if_exists}', '_self');
			});
			$('#wn_oeadj_alterSaveEdit').on('click', function(){
				var rowIndex = $('#jqxgridOrderAdjustment').jqxGrid('getselectedrowindex');
				if (OlbCore.isNotEmpty(rowIndex)) {
					$("#jqxgridOrderAdjustment").jqxGrid('endcelledit', rowIndex, "quantity", true, true);
				}
				var dataMap = {
					orderId: "${orderHeader.orderId?if_exists}",
					comments: "${uiLabelMap.BSAddedManually}"
				};
				var listOrderAdj = getListItemAdj();
				//if (listOrderAdj.length > 0) {
					dataMap.listOrderAdj = JSON.stringify(listOrderAdj);
					
					$.ajax({
						type: 'POST',
						url: 'updateOrderAdjustmentOlb',
						data: dataMap,
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, "default", function(){
								$('#container').empty();
			    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
			    	        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			    	        	$("#jqxNotification").jqxNotification("open");
			    	        	window.location.href = 'viewOrder?orderId=' + orderId;
			    	        	return true;
							});
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				//} else {
				//	jOlbUtil.alert.error("${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseRow)}");
				//	return false;
				//}
			});
		};
		var getListItemAdj = function(){
			// list order adjustment
			var data = $("#jqxgridOrderAdjustment").jqxGrid("getboundrows");
			if (typeof(data) == 'undefined') {
				jOlbUtil.alert.info("Error check data");
			}
			
			var listProd = [];
			for (var i = 0; i < data.length; i++) {
				var dataItem = data[i];
				if (dataItem != window) {
					if (typeof(dataItem) != 'undefined' && typeof(dataItem.orderAdjustmentTypeId) != 'undefined' && typeof(dataItem.amount) != 'undefined') {
						var prodItem = {
							orderAdjustmentTypeId: dataItem.orderAdjustmentTypeId,
							amount: dataItem.amount,
							description: dataItem.description
						};
						listProd.push(prodItem);
					}
				}
			}
			return listProd;
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#wn_oeadj_orderAdjustmentTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
					{input: '#wn_oeadj_amount', type: 'validInputNotNull'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindowOrderAdjAdd'), mapRules, extendRules, {position: 'bottom'});
		};
		var removeOrderAdjustment = function(){
			var rowIndex = $("#jqxgridOrderAdjustment").jqxGrid('getselectedrowindex');
			if (rowIndex == null || rowIndex < 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
			} else {
				var rowData = $("#jqxgridOrderAdjustment").jqxGrid('getrowdata', rowIndex);
				if (rowData) {
					$("#jqxgridOrderAdjustment").jqxGrid('deleterow', rowData.uid);
				}
			}
		};
		var openWindowAddOrderAdj = function(){
			$("#alterpopupWindowOrderAdjAdd").jqxWindow("open");
		};
		var clearWindowAddOrderAdj = function(){
			orderAdjustmentTypeDDL.clearAll();
			$("#wn_oeadj_amount").jqxNumberInput("val", 0);
			$("#wn_oeadj_description").val("");
		};
		return {
			init: init,
			openWindowAddOrderAdj: openWindowAddOrderAdj,
			removeOrderAdjustment: removeOrderAdjustment
		}
	}());
</script>