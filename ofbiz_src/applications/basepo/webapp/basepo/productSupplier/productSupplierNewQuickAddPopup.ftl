<#assign dataFieldSuppItemsProdAdd = "
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'quantityUomId', type: 'string'},
       		{name: 'purchaseUomId', type: 'array'},
       		{name: 'purchasePrice', type: 'number', formatter: 'float'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'purchasePriceNew', type: 'number'},
       	"/>
<#assign columnsSuppItemsProdAdd = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 160, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 80, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUomId)}', dataField: 'quantityUomId', width: 100, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)} (${uiLabelMap.BSBeforeTax})', dataField: 'purchasePrice', width: 220, cellsalign: 'right', hidden: false,
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridSuppItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatcurrency(value, data.currencyUomId) + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSNewPrice)} (${uiLabelMap.BSBeforeTax})', dataField: 'purchasePriceNew', width: 220, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridSuppItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
		   			returnVal += formatcurrency(value, data.currencyUomId) + '</div>';
	   				return returnVal;
			 	},
			 	validation: function (cell, value) {
					if (value < 0) {
						return {result: false, message: '${uiLabelMap.POPriceMustBeGreaterThanZero}'};
					}
					return true;
				}
			},
		"/>

<div id="windowSuppItemsProdAdd" style="display:none">
	<div>${uiLabelMap.AddNewProductPrice}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class="row-fluid">
						<div class='span3'>
							<label style="text-align:left" class="required">${uiLabelMap.BSSupplier}:</label>
						</div>
						<div class='span9'>
							<div id="wn_suppItems_supplierId">
								<div id="wn_suppItems_supplierGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class="row-fluid">
						<div class='span3'>
							<label style="text-align:left" class="required">${uiLabelMap.BSFromDate}:</label>
						</div>
						<div class='span9'>
							<div id="wn_suppItems_fromDateId"></div>
				   		</div>
					</div>
					<div class="row-fluid">
						<div class='span3'>
							<label style="text-align:left">${uiLabelMap.BSThruDate}:</label>
						</div>
						<div class='span9'>
							<div id="wn_suppItems_thruDateId"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridSuppItemsProdAdd"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_suppItems_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSave}</button>
	   			<button id="wn_suppItems_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_suppItems_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbSuppNewItemsProdAddPop.init();
	});
	var OlbSuppNewItemsProdAddPop = (function(){
		var supplierDDB;
		var productGRID;
		var validatorVAL;
		
		var init = function(){
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElementComplex = function(){
			jOlbUtil.windowPopup.create($("#windowSuppItemsProdAdd"), {maxWidth: 1100, width: 1100, height: 520, cancelButton: $("#wn_suppItems_alterCancel")});
			jOlbUtil.dateTimeInput.create("#wn_suppItems_fromDateId", {width: '220', allowNullDate: true, value: null, showFooter: true});
			jOlbUtil.dateTimeInput.create("#wn_suppItems_thruDateId", {width: '220', allowNullDate: true, value: null, showFooter: true});
			
			var configSupplier = {
				useUrl: true,
				widthButton: "100%",
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: "partyId", type: "string"},
					{name: "partyCode", type: "string"},
					{name: "groupName", type: "string"}
				],
				columns: [ 
					{text: uiLabelMap.POSupplierId, datafield: "partyCode", width: "200"},
					{text: uiLabelMap.POSupplierName, datafield: "groupName"}
				],
				url: "jqGetListPartySupplier&subsidiary=Y",
				useUtilFunc: true,
				key: "partyId",
				keyCode: "partyCode",
				description: ["groupName"],
				autoCloseDropDown: true,
				filterable: true
			};
			supplierDDB = new OlbDropDownButton($("#wn_suppItems_supplierId"), $("#wn_suppItems_supplierGrid"), null, configSupplier, []);
			
			var configGridProduct = {
				datafields: [${dataFieldSuppItemsProdAdd}],
				columns: [${columnsSuppItemsProdAdd}],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				editable: true,
				pageable: true,
				pagesize: 10,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: '', //jqxGeneralServicer?sname=JQGetListProductAndPurchasePrice
				groupable: true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'checkbox',
				virtualmode: true,
			};<#--TH2: JQGetListProductSellAll-->
			productGRID = new OlbGrid($("#jqxgridSuppItemsProdAdd"), null, configGridProduct, []);
		};
		var initEvent = function(){
			supplierDDB.getGrid().rowSelectListener(function(itemData){
				if (itemData) productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductAndPurchasePrice&supplierId=" + itemData.partyId);
			});
			
			$("#wn_suppItems_alterSave").on("click", function(){
				addItemsToGrid(false);
			});
			$("#wn_suppItems_alterSaveAndContinue").on("click", function(){
				addItemsToGrid(true);
			});
			
			$("#wn_suppItems_alterCancel").on("click", function(){
				supplierDDB.clearAll();
				productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductAndPurchasePrice&supplierId=");
				$("#wn_suppItems_thruDateId").val(null);
			});
			
			$("#windowSuppItemsProdAdd").on("close", function(event) {
				supplierDDB.clearAll();
				productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductAndPurchasePrice&supplierId=");
				$("#wn_suppItems_thruDateId").val(null);
			});
			
			$("#jqxgridSuppItemsProdAdd").on("cellendedit", function (event) {
		    	var args = event.args;
		    	if (args.datafield == "purchasePriceNew") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxgridSuppItemsProdAdd").jqxGrid("getrowdata", rowBoundIndex);
			    	var oldValue = args.oldvalue;
			   		var newValue = args.value;
			    	if (data && data.productId) {
				   		if (newValue > 0) {
				   			$('#jqxgridSuppItemsProdAdd').jqxGrid('selectrow', rowBoundIndex);
				   		} else {
				   			$('#jqxgridSuppItemsProdAdd').jqxGrid('unselectrow', rowBoundIndex);
			   			}
			    	}
		    	}
	    	});
		};
		var addItemsToGrid = function(isContinue) {
			if (!validatorVAL.validate()) return false;
			
			var rowindexes = $("#jqxgridSuppItemsProdAdd").jqxGrid("getselectedrowindexes");
			if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				return false;
			}
			var listProdData = [];
			for (var i = 0; i < rowindexes.length; i++) {
				var dataItem = $("#jqxgridSuppItemsProdAdd").jqxGrid("getrowdata", rowindexes[i]);
				if (dataItem) {
					if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
						if ( typeof(dataItem.purchasePriceNew) != "undefined" && dataItem.purchasePriceNew > 0 ){
							listProdData.push(dataItem);
						} else {
							jOlbUtil.alert.error("${uiLabelMap.BSAllQuantityMustBeGreaterThanZero}");
							return false;
						}
					}
				}
			}
			
			if (listProdData.length > 0) {
				addItemsToGridAjax(listProdData, isContinue);
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
			}
		};
		var addItemsToGridAjax = function(listData, isContinue){
			var dataMap = {
				partyId: supplierDDB.getValue(),
			};
			var availableFromDateT = $('#wn_suppItems_fromDateId').jqxDateTimeInput('getDate');
			if (typeof(availableFromDateT) != 'undefined' && availableFromDateT != null) {
				dataMap['availableFromDate'] = availableFromDateT.getTime();
			}
			var availableThruDateT = $('#wn_suppItems_thruDateId').jqxDateTimeInput('getDate');
			if (typeof(availableThruDateT) != 'undefined' && availableThruDateT != null) {
				dataMap['availableThruDate'] = availableThruDateT.getTime();
			}
			
			var productItems = [];
			for (var i = 0; i < listData.length; i++) {
				var data = listData[i];
				if (OlbCore.isEmpty(data.productId)) {
					continue;
				}
				
				// new value
    			var itemValue = {};
    			itemValue.productId = data.productId;
    			itemValue.uomId = data.quantityUomId;
    			itemValue.purchasePrice = data.purchasePriceNew;
	    		
	    		productItems.push(itemValue);
			}
			dataMap.productItems = JSON.stringify(productItems);
			
			jOlbUtil.confirm.dialog(uiLabelMap.BSAreYouSureYouWantToCreate, 
				function(){
					$("#wn_suppItems_alterSave").addClass("disabled");
					$("#wn_suppItems_alterSaveAndContinue").addClass("disabled");
					$("#wn_suppItems_alterCancel").addClass("disabled");
					$.ajax({
						type: 'POST',
						url: 'addProductsToSupplier',
						data: dataMap,
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$("#wn_suppItems_alterSave").removeClass("disabled");
								$("#wn_suppItems_alterSaveAndContinue").removeClass("disabled");
								$("#wn_suppItems_alterCancel").removeClass("disabled");
								
								$("#container").empty();
								$("#jqxNotification").jqxNotification({ template: "info"});
								$("#jqxNotification").html(errorMessage);
								$("#jqxNotification").jqxNotification("open");
								return false;
							}, function(){
								var listEventMsg = data._EVENT_MESSAGE_LIST_;
								if (listEventMsg != null && listEventMsg.length > 0) {
									productIdsNotSuccess = data.productIdsNotSuccess;
									
									var responseMessage = listEventMsg[0];
									if (listEventMsg.length > 1) {
										responseMessage += "<ol>";
										for (var i = 1; i < listEventMsg.length; i++) {
							        		responseMessage += "<li>" + listEventMsg[i] + '&nbsp;&nbsp;&nbsp;<a href="viewProduct?productId=' + productIdsNotSuccess[i-1] + '" target="_blank">Xem san pham</a>' + "</li>";
							        	}
							        	responseMessage += "</ol>";
									}
						        	
									bootbox.dialog(responseMessage, [
						                {"label": "${uiLabelMap.CommonCancel}", "icon": 'fa fa-remove', "class": 'btn btn-danger form-action-button pull-right',
								            "callback": function() {bootbox.hideAll();}
								        }
								    ]);
								} else {
									$("#container").empty();
									$("#jqxNotification").jqxNotification({ template: "info"});
									$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
									$("#jqxNotification").jqxNotification("open");
									
									$("#wn_suppItems_alterSave").removeClass("disabled");
									$("#wn_suppItems_alterSaveAndContinue").removeClass("disabled");
									$("#wn_suppItems_alterCancel").removeClass("disabled");
									
									if (isContinue) {
										productGRID.updateBoundData();
										$("#jqxgridSupplierProduct").jqxGrid("updatebounddata");
									} else {
										closeWindow();
										$("#jqxgridSupplierProduct").jqxGrid("updatebounddata");
										
										supplierDDB.clearAll();
										productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductAndPurchasePrice&supplierId=");
										$("#wn_suppItems_thruDateId").val(null);
									}
								}
							});
						},
						error: function(data){
							alert("Send request is error");
							$("#wn_suppItems_alterSave").removeClass("disabled");
							$("#wn_suppItems_alterSaveAndContinue").removeClass("disabled");
							$("#wn_suppItems_alterCancel").removeClass("disabled");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				}
			);
		};
		var openWindow = function(){
			$("#windowSuppItemsProdAdd").jqxWindow("open");
			$("#wn_suppItems_fromDateId").jqxDateTimeInput("setDate", new Date((new Date()).getTime() + 1000));
		};
		var closeWindow = function(){
			$("#windowSuppItemsProdAdd").jqxWindow("close");
		};
		var initValidateForm = function(){
			var extendRules = [
					{input: '#wn_suppItems_fromDateId, #wn_suppItems_thruDateId', message: uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate, action: 'valueChanged', 
						rule: function(input, commit){
							return OlbValidatorUtil.validElement(input, commit, 'validCompareTwoDate', {paramId1 : "wn_suppItems_fromDateId", paramId2 : "wn_suppItems_thruDateId"});
						}
					}
	           ];
			var mapRules = [
					{input: '#wn_suppItems_supplierId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#wn_suppItems_fromDateId', type: 'validDateTimeInputNotNull'},
					{input: '#wn_suppItems_fromDateId', type: 'validDateTimeCompareToday', message: uiLabelMap.validRequiredValueGreatherOrEqualDateTimeToDay},
	            ];
			validatorVAL = new OlbValidator($('#windowSuppItemsProdAdd'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		return {
			init: init,
			openWindow: openWindow
		};
	}());	
</script>
