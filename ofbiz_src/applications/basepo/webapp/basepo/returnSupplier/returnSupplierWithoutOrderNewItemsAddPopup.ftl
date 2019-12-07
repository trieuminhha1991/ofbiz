<#assign dataFieldQuotItemsProdAdd = "
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'internalName', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'quantityUomId', type: 'string'},
       		{name: 'returnPrice', type: 'number', formatter: 'float'},
       	"/>
<#assign columnsQuotItemsProdAdd = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 120, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 120, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: 120, editable:false, filterable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', width: 140, editable: true, filterable: false, cellClassName: 'background-prepare'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSReturnPrice)} (T)', dataField: 'returnPrice', width: 140, cellsalign: 'right',
				editable: true, filterable:false, sortable:false, cellsformat: 'c2', cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
		   			returnVal += formatcurrency(value, currencyUomId, true) + '</div>';
	   				return returnVal;
			 	},
			 	validation: function (cell, value) {
					if (value < 0) {
						return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
					}
					return true;
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSReturnQty)}', dataField: 'returnQuantity', width: 140, cellsalign: 'right',
				editable: true, filterable:false, sortable:false, cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
	 				var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatnumber(value) + '</div>';
	   				return returnVal;
			 	}
			},
		"/>

<div id="windowReturnItemsProdAdd" style="display:none">
	<div>${uiLabelMap.BSAddItems}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridReturnItemsProdAdd"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_returnItems_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAdd}</button>
	   			<button id="wn_returnItems_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_returnItems_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbReturnNewItemsProdAddPop.init();
	});
	var OlbReturnNewItemsProdAddPop = (function(){
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			jOlbUtil.windowPopup.create($("#windowReturnItemsProdAdd"), {maxWidth: 1100, width: 1100, height: 480, cancelButton: $("#wn_returnItems_alterCancel")});
			
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
				url: '', //jqxGeneralServicer?sname=JQGetListProductToReturnSupplier
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'checkbox',
				virtualmode: true,
			};
			productGRID = new OlbGrid($("#jqxgridReturnItemsProdAdd"), null, configGridProduct, []);
		};
		var initEvent = function(){
			if (OlbReturnNewItemsProdSearch) {
				productGRID.bindingCompleteListener(function(){
					OlbReturnNewItemsProdSearch.productToSearchFocus();
				}, true);
			}
			
			$("#wn_returnItems_alterSave").on("click", function(){
				var rowindexes = $("#jqxgridReturnItemsProdAdd").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					return false;
				}
				var listProdData = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#jqxgridReturnItemsProdAdd").jqxGrid("getrowdata", rowindexes[i]);
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
			$("#wn_returnItems_alterSaveAndContinue").on("click", function(){
				var rowindexes = $("#jqxgridReturnItemsProdAdd").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
					return false;
				}
				var listProdData = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#jqxgridReturnItemsProdAdd").jqxGrid("getrowdata", rowindexes[i]);
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
			
			$("#jqxgridReturnItemsProdAdd").on("cellendedit", function (event) {
		    	var args = event.args;
		    	if (args.datafield == "returnQuantity") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#jqxgridReturnItemsProdAdd").jqxGrid("getrowdata", rowBoundIndex);
			    	var oldValue = args.oldvalue;
			   		var newValue = args.value;
			    	if (data && data.productId) {
			    		// new, update item
				   		if (newValue > 0) {
				   			$('#jqxgridReturnItemsProdAdd').jqxGrid('selectrow', rowBoundIndex);
				   		} else {
				   			$('#jqxgridReturnItemsProdAdd').jqxGrid('unselectrow', rowBoundIndex);
			   			}
			    	}
		    	}
		    });
		};
		var addItemsToGrid = function(listData) {
			OlbReturnAddProductItems.addItemsToGridPopup(listData);
			
			closeWindow();
			productGRID.clearSelection();
		};
		var addItemsToGridAndContinue = function(listData) {
			OlbReturnAddProductItems.addItemsToGridPopup(listData);
			
			productGRID.clearSelection();
		};
		var openWindow = function(){
			$("#windowReturnItemsProdAdd").jqxWindow("open");
			//reloadListProduct();
		};
		var closeWindow = function(){
			$("#windowReturnItemsProdAdd").jqxWindow("close");
		};
		var reloadListProduct = function(){
			var facilityId = OlbReturnWithoutOrderInfo.getObj().destinationFacilityDDB.getValue();
			productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProductToReturnSupplier&facilityId=" + facilityId);
		};
		return {
			init: init,
			openWindow: openWindow,
			reloadListProduct: reloadListProduct,
		};
	}());	
</script>

<#--
{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)}', dataField: 'unitPriceBef', width: '12%', cellsalign: 'right',
						editable: false, filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridReturnItemsProdAdd').jqxGrid('getrowdata', row);
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