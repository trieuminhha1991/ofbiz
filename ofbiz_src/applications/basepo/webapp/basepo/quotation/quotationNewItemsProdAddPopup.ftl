<#assign dataFieldQuotItemsProdAdd = "
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'parentProductId', type: 'string'},
       		{name: 'parentProductCode', type: 'string'},
       		{name: 'features', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'quantityUomId', type: 'string'},
       		{name: 'packingUomIds', type: 'array'},
       		{name: 'taxPercentage', type: 'number'}, 
       		{name: 'listPrice', type: 'number', formatter: 'float'},
       		{name: 'listPriceVAT', type: 'number', formatter: 'float'},
       		{name: 'colorCode', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'lastPrice', type: 'number'},
       		{name: 'unitPriceBef', type: 'number'},
       		{name: 'unitPriceVAT', type: 'number'},
       	"/>
<#assign columnsQuotItemsProdAdd = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 120, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 80, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSTax)}', dataField: 'taxPercentage', width: 70, editable:false, filterable: false, cellsalign: 'right', cellsformat: 'p'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: 70, 
				editable:true, filterable: false, columntype: 'dropdownlist', cellClassName: 'background-prepare', 
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-center\">';
		   			for (var i = 0 ; i < uomData.length; i++){
						if (value == uomData[i].uomId){
							returnVal += uomData[i].description + '</div>';
	   						return returnVal;
						}
					}
		   			returnVal += value + '</div>';
	   				return returnVal;
				},
			 	initeditor: function (row, cellvalue, editor) {
			 		var packingUomData = new Array();
					var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
					
					var itemSelected = data['quantityUomId'];
					var packingUomIdArray = data['packingUomIds'];
					/*
					for (var i = 0; i < packingUomIdArray.length; i++) {
						var packingUomIdItem = packingUomIdArray[i];
						var row = {};
						if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
							row['description'] = '' + packingUomIdItem.uomId;
						} else {
							row['description'] = '' + packingUomIdItem.description;
						}
						row['uomId'] = '' + packingUomIdItem.uomId;
						packingUomData[i] = row;
					}
					*/
			 		var sourceDataPacking = {
		                localdata: packingUomIdArray,
		                datatype: 'array'
		            };
		            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
		            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
		            editor.jqxDropDownList('selectItem', itemSelected);
		      	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSQuantityConvert)}', dataField: 'quantityConvert', width: 70, cellsalign: 'right',
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
			 		var newValue = value;
			 		if (data && !value) {
			 			newValue = 1;
			 			data.quantityConvert = newValue;
			 		} 
	 				var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatnumber(newValue) + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)} (T)', dataField: 'unitPriceBef', width: 120, cellsalign: 'right', hidden: false,
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
			 		var newValue = value;
			 		if (data && !value) {
			 			newValue = data.lastPrice;
			 		} 
			 		var currencyUomId = $('#currencyUomId').val();
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatcurrency(newValue, currencyUomId) + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)} (S)', dataField: 'unitPriceVAT', width: 120, cellsalign: 'right',
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
			 		var newValue = value;
			 		if (data && !value) {
			 			newValue = data.lastPrice;
			 		}
			 		var currencyUomId = $('#currencyUomId').val();
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var calcValue = newValue + (newValue * data.taxPercentage)/100;
	   				returnVal += formatcurrency(Math.round(calcValue * 100) / 100, currencyUomId) + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSNewPrice)} (T)', dataField: 'listPrice', width: 120, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var currencyUomId = $('#currencyUomId').val();
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		if (OlbCore.isEmpty(value)) {
				 		var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productPricesMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.listPrice)) {
				   				data.listPrice = itemMapValue.listPrice;
				   				returnVal += formatcurrency(itemMapValue.listPrice, currencyUomId) + '</div>';
				   				return returnVal;
				   			}
			   			}
		   			}
		   			returnVal += formatcurrency(value, currencyUomId) + '</div>';
	   				return returnVal;
			 	},
			 	validation: function (cell, value) {
					if (value < 0) {
						return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
					}
					return true;
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSNewPrice)} (S)', dataField: 'listPriceVAT', width: 120, cellsalign: 'right', editable: true,
				filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
				cellsrenderer: function(row, column, value){
					var currencyUomId = $('#currencyUomId').val();
					var returnVal = '<div class=\"innerGridCellContent align-right\">';
					if (OlbCore.isEmpty(value)) {
						var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productPricesMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.listPriceVAT)) {
								data.listPriceVAT = itemMapValue.listPriceVAT;
				   				returnVal += formatcurrency(Math.round(itemMapValue.listPriceVAT * 100) / 100, currencyUomId) + '</div>';
				   				return returnVal;
							}
			   			}
					} else {
			 			value = Math.round(value * 100) / 100;
			 		}
					returnVal += formatcurrency(value, currencyUomId) + '</div>';
					return returnVal;
			 	},
			}
		"/>

<div id="windowQuotItemsProdAdd" style="display:none">
	<div>${uiLabelMap.BSAddItems}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridQuotItemsProdAdd"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_quotItems_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAdd}</button>
	   			<button id="wn_quotItems_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_quotItems_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbQuotNewItemsProdAddPop.init();
	});
	var OlbQuotNewItemsProdAddPop = (function(){
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			jOlbUtil.windowPopup.create($("#windowQuotItemsProdAdd"), {maxWidth: 1100, width: 1100, height: 520, cancelButton: $("#wn_quotItems_alterCancel")});
			
			var configGridProduct = {
				datafields: [${dataFieldQuotItemsProdAdd}],
				columns: [${columnsQuotItemsProdAdd}],
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
				url: '',
				groupable: true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'checkbox',
				virtualmode: true,
			};<#--TH2: JQGetListProductSellAll-->
			productGRID = new OlbGrid($("#jqxgridQuotItemsProdAdd"), null, configGridProduct, []);
		};
		var initEvent = function(){
			if (OlbQuotNewItemsProdSearch) {
				productGRID.bindingCompleteListener(function(){
					OlbQuotNewItemsProdSearch.productToSearchFocus();
				}, true);
			}
			
			$("#wn_quotItems_alterSave").on("click", function(){
				var rowindexes = $("#jqxgridQuotItemsProdAdd").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					return false;
				}
				var listProdData = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#jqxgridQuotItemsProdAdd").jqxGrid("getrowdata", rowindexes[i]);
					if (dataItem) {
						if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
							listProdData.push(dataItem);
						}
					}
				}
				
				if (listProdData.length > 0) {
					addItemsToGrid(listProdData);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
			});
			$("#wn_quotItems_alterSaveAndContinue").on("click", function(){
				var rowindexes = $("#jqxgridQuotItemsProdAdd").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					return false;
				}
				var listProdData = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#jqxgridQuotItemsProdAdd").jqxGrid("getrowdata", rowindexes[i]);
					if (dataItem) {
						if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
							listProdData.push(dataItem);
						}
					}
				}
				
				if (listProdData.length > 0) {
					addItemsToGridAndContinue(listProdData);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
			});
			
			$("#jqxgridQuotItemsProdAdd").on("cellendedit", function (event) {
		    	var args = event.args;
		    	if (args.datafield == "listPrice") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxgridQuotItemsProdAdd").jqxGrid("getrowdata", rowBoundIndex);
			    	var oldValue = args.oldvalue;
			   		var newValue = args.value;
			    	if (data && data.productId) {
			    		var priceVAT = null;
			    		if (data.taxPercentage) {
					   		if (OlbCore.isNotEmpty(newValue)) {
					   			var taxPercentage = data.taxPercentage;
						   		var valueCal = newValue + newValue * taxPercentage / 100;
						   		valueCal = Math.round(valueCal * 100) / 100
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
					    		priceVAT = valueCal;
					   		} else {
					   			var valueCal = "";
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
					   		}
				    	} else {
				    		if (OlbCore.isNotEmpty(newValue)) {
				    			$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', newValue);
					    		priceVAT = newValue;
				    		} else {
				    			var valueCal = "";
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
				    		}
				    	}
			    		
			    		// new, update item
				   		if (newValue > 0) {
				   			$('#jqxgridQuotItemsProdAdd').jqxGrid('selectrow', rowBoundIndex);
				   		} else {
				   			$('#jqxgridQuotItemsProdAdd').jqxGrid('unselectrow', rowBoundIndex);
			   			}
			    	}
		    	} else if (args.datafield == "listPriceVAT") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxgridQuotItemsProdAdd").jqxGrid("getrowdata", rowBoundIndex);
			    	var oldValue = args.oldvalue;
			   		var newValue = args.value;
			    	if (data && data.productId) {
			    		var priceBeforeVAT = null;
			    		if (data.taxPercentage) {
					   		if (OlbCore.isNotEmpty(newValue)) {
					   			var taxPercentage = data.taxPercentage;
						   		var valueCal = newValue * 100 / (100 + taxPercentage);
						   		valueCal = Math.round(valueCal * 100) / 100
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
						   		priceBeforeVAT = valueCal;
					   		} else {
					   			var valueCal = "";
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
					   		}
				    	} else {
				    		if (OlbCore.isNotEmpty(newValue)) {
				    			$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', newValue);
				    			priceBeforeVAT = newValue;
				    		} else {
				    			var valueCal = "";
					    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
				    		}
				    	}
			    		
			    		// new, update item
			    		if (newValue > 0) {
				   			$('#jqxgridQuotItemsProdAdd').jqxGrid('selectrow', rowBoundIndex);
				   		} else {
				   			$('#jqxgridQuotItemsProdAdd').jqxGrid('unselectrow', rowBoundIndex);
				   		}
			    	}
		    	} else if (args.datafield == "quantityUomId") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxgridQuotItemsProdAdd").jqxGrid("getrowdata", rowBoundIndex);
			    	if (data && data.packingUomIds) {
			    		var oldValue = args.oldvalue;
				   		var newValue = args.value;
			    		var packingUomIdArray = data.packingUomIds;
			    		var quantityConvert = 1;
			    		var unitPriceConvert = data.unitPrice;
			    		for (var i = 0; i < packingUomIdArray.length; i++) {
							var packingUomIdItem = packingUomIdArray[i];
							if (packingUomIdItem.uomId == newValue) {
								if (typeof(packingUomIdItem.quantityConvert) != "undefined" && packingUomIdItem.quantityConvert != null) {
									quantityConvert = packingUomIdItem.quantityConvert;
								}
								unitPriceConvert = packingUomIdItem.unitPriceConvert;
								break;
							}
						}
		    			$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'quantityConvert', quantityConvert);
		    			$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceBef', unitPriceConvert);
			    		$('#jqxgridQuotItemsProdAdd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceVAT', unitPriceConvert);
			    	}
		    	}
	    	});
		};
		var addItemsToGrid = function(listData) {
			OlbAddProductItems.addItemsToGridPopup(listData);
			
			closeWindow();
			productGRID.clearSelection();
		};
		var addItemsToGridAndContinue = function(listData) {
			OlbAddProductItems.addItemsToGridPopup(listData);
			
			productGRID.clearSelection();
		};
		var openWindow = function(){
			$("#windowQuotItemsProdAdd").jqxWindow("open");
		};
		var closeWindow = function(){
			$("#windowQuotItemsProdAdd").jqxWindow("close");
		};
		return {
			init: init,
			openWindow: openWindow
		};
	}());	
</script>

<#--
{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)}', dataField: 'unitPriceBef', width: '12%', cellsalign: 'right',
						editable: false, filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridQuotItemsProdAdd').jqxGrid('getrowdata', row);
					 		var newValue = value;
					 		if (data && !value) {
					 			newValue = data.unitPrice
					 		} 
			 				var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   				returnVal += formatcurrency(newValue, currencyUomId) + '</div>';
			   				return returnVal;
					 	}
					},
-->