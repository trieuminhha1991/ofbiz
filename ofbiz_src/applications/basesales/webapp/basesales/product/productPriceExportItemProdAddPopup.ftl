<#assign dataFieldAddPopup = "[
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'primaryProductCategoryId', type: 'string'},
       		{name: 'supplierCode', type: 'string'},
       		{name: 'idSKU', type: 'string'}, 
       		{name: 'unitPriceVAT', type: 'number', formatter: 'float'},
       		{name: 'unitListPriceVAT', type: 'number', formatter: 'float'},
       		{name: 'taxPercentage', type: 'number'},
       		{name: 'uomId', type: 'string'},
       		{name: 'iupprm', type: 'string'},
       		{name: 'numCopy', type: 'string'},
    	]"/>
<#assign columnlistAddPopup = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 100, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: 120, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', dataField: 'primaryProductCategoryId', width: 80, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAbbSupplierId)}', dataField: 'supplierCode', width: 80, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUPC)}', dataField: 'idSKU', width: 120, editable: false, 
				cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridPriceItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right';
			 		if (data && data.iupprm == 1) returnVal += ' red';
		   			returnVal += '\">' + value + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'uomId', width: 80, editable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSListPrice)}', dataField: 'unitListPriceVAT', width: 100, cellsalign: 'right',
				filterable:false, sortable:false, editable: false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridPriceItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\"';
			 		if (data && data.unitListPriceVAT != data.unitPriceVAT) returnVal += ' style=\"text-decoration: line-through;\"';
			 		returnVal += '>';
			 		if (data && value) {
		   				returnVal += formatCurrencyNew(value, data.currencyUomId);
		   			}
		   			returnVal += '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPrice)}', dataField: 'unitPriceVAT', width: 100, cellsalign: 'right',
				filterable:false, sortable:false, editable: false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridPriceItemsProdAdd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		if (data && value) {
		   				returnVal += formatCurrencyNew(value, data.currencyUomId);
		   			}
		   			returnVal += '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAbbNumberPrint)}', dataField: 'numCopy', width: 80, cellsalign: 'right', 
		 		filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd', cellClassName: 'background-prepare', 
		 		cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
		   			returnVal += formatnumber(value) + '</div>';
	   				return returnVal;
			 	},
				validation: function (cell, value) {
					if (value < 0) {
						return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
					}
					return true;
				},
				createeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({decimalDigits: 0, digits: 9});
				}
		 	},
		"/>

<div id="windowPriceItemsProdAdd" style="display:none">
	<div>${uiLabelMap.BSAddItems}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridPriceItemsProdAdd"></div>
	   				<span class="noteExportPriceProduct help-inline red" style="color: #657ba0; font-style: italic"><b>${uiLabelMap.BSNoteY}</b>: ${uiLabelMap.BSOnlyUPCIsRedWhichPrintable}</span>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_price_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAdd}</button>
	   			<button id="wn_price_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSAddAndContinue}</button>
				<button id="wn_price_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbPriceItemsProdAddPop.init();
	});
	var OlbPriceItemsProdAddPop = (function(){
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			jOlbUtil.windowPopup.create($("#windowPriceItemsProdAdd"), {maxWidth: 960, width: 960, height: 460, cancelButton: $("#wn_price_alterCancel")});
			
			var configGridProduct = {
				datafields: ${dataFieldAddPopup},
				columns: [${columnlistAddPopup}],
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
				url: 'jqxGeneralServicer?sname=JQGetListProdExportPrice',
			};
			productGRID = new OlbGrid($("#jqxgridPriceItemsProdAdd"), null, configGridProduct, null);
		};
		var initEvent = function(){
			if (OlbQuotNewItemsProdSearch) {
				productGRID.bindingCompleteListener(function(){
					OlbQuotNewItemsProdSearch.productToSearchFocus();
				}, true);
			}
			
			$("#wn_price_alterSave").on("click", function(){
				var listProdData = getListProduct();
				
				if (listProdData.length > 0) {
					addItemsToGrid(listProdData);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
			});
			$("#wn_price_alterSaveAndContinue").on("click", function(){
				var listProdData = getListProduct();
				
				if (listProdData.length > 0) {
					addItemsToGridAndContinue(listProdData);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
			});
		};
		var getListProduct = function() {
			var listProdData = [];
			
			var dataRow = $("#jqxgridPriceItemsProdAdd").jqxGrid("getboundrows");
			if (typeof(dataRow) == "undefined" || dataRow.length < 1) {
				return listProdData;
			}
			if (typeof(dataRow) != 'undefined') {
				for (var i = 0; i < dataRow.length; i++) {
					var dataItem = dataRow[i];
					if (dataItem != window 
							&& typeof(dataItem) != "undefined" 
							&& typeof(dataItem.productId) != "undefined"
							&& !isNaN(dataItem.numCopy)
							&& parseInt(dataItem.numCopy) > 0) {
						listProdData.push(dataItem);
					}
				}
			}
			return listProdData;
		};
		var addItemsToGrid = function(listData) {
			OlbExportProdPrice.addItemsToGridPopup(listData);
			
			closeWindow();
			productGRID.clearSelection();
		};
		var addItemsToGridAndContinue = function(listData) {
			OlbExportProdPrice.addItemsToGridPopup(listData);
			
			productGRID.clearSelection();
		};
		var openWindow = function(){
			$("#windowPriceItemsProdAdd").jqxWindow("open");
		};
		var closeWindow = function(){
			$("#windowPriceItemsProdAdd").jqxWindow("close");
			if (OlbQuotNewItemsProdSearch) {
				OlbQuotNewItemsProdSearch.productToSearchFocus();
			}
		};
		var getObj = function(){
			return {productGRID: productGRID}
		};
		return {
			init: init,
			openWindow: openWindow,
			getObj: getObj
		};
	}());	
</script>


