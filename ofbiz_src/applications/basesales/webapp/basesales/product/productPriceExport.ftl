<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<div id="productPriceOption" class="row-fluid form-horizontal form-window-content-custom">
	<div class="span8">
		<div class="row-fluid">
			<div class="span3">
				<label>${uiLabelMap.BSFindBy}:</label>
			</div>
			<div class="span3">
				<div id="radFindAll" style="text-align: left"><span>None</span></div>
			</div>
			<div class="span3">
				<div id="radFindByCatalog" style="text-align: left"><span>Catalog</span></div>
			</div>
			<div class="span3">
				<div id="radFindByCategory" style="text-align: left"><span>Category</span></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span3">
				<label>${uiLabelMap.BSExport}:</label>
			</div>
			<div class="span3">
				<div id="radExportAll" style="text-align: left"><span>${uiLabelMap.BSExportAll}</span></div>
			</div>
			<div class="span3">
				<div id="radExportAny" style="text-align: left"><span>${uiLabelMap.BSExportAny}</span></div>
			</div>
			<div class="span3">
			</div>
		</div>
		<div class="row-fluid no-bottom-margin">
			<div class="span3">
				<label>${uiLabelMap.BSAdvance}:</label>
			</div>
			<div class="span9">
				<div class="row-fluid">
					<div class="span4">
						<div id="radIncludePriceRule" style="text-align: left; margin-left:0 !important"><span>${uiLabelMap.BSIncludePriceRule}</span></div>
					</div>
					<div class="span8">
						<div id="containerStore" class="no-bottom-margin">
							<div id="productStoreId" style="display:inline-block" class="no-bottom-margin"><div id="productStoreGrid"></div></div>
							<label class="required" style="display:inline-block; line-height:15px"></label>
						</div>
					</div>
				</div>
				<div class="row-fluid no-bottom-margin">
					<div class="span6">
						<div id="radPrintAlterUom" style="text-align: left; margin-left:0 !important"><span>${uiLabelMap.BSPrintAlternativeUom}</span></div>
					</div>
					<div class="span6">
						<div id="radPrintNormalUPC" style="text-align: left; margin-left:0 !important"><span>${uiLabelMap.BSPrintNormalUPC}</span></div>
					</div>
				</div>
			</div>
		</div>
	</div><!--.span6-->
	<div class="span4">
		<div class="row-fluid">
			<div class="span3 align-left">
				<label>${uiLabelMap.BSCatalog}</label>
			</div>
			<div class="span9">
				<div id="prodCatalogId"><div id="prodCatalogGrid"></div></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span3 align-left">
				<label>${uiLabelMap.BSCategory}</label>
			</div>
			<div class="span9">
				<div id="productCategoryId"><div id="productCategoryGrid"></div></div>
			</div>
		</div>
		<div class="row-fluid no-bottom-margin">
			<div class="span12"></div>
		</div>
		<div id="searchAllContainer" class="row-fluid no-bottom-margin" style="display:none">
			<div class="pull-left">
				<button type="button" id="btnFind" class="btn btn-small btn-primary width100px"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionFind}</button>
			</div>
		</div>
	</div><!--.span6-->
</div><!--.row-fluid-->

<hr class="small-margin"/>

<div class="row-fluid">
	<div class="span12">
		<div id="exportAllContainer" style="display:none">
			<div id="gridProduct"></div>
			<div style="font-size:16px; margin-top:10px;" class="pull-right">
				<span class="green"><b>${uiLabelMap.BSExportAll}</b> <i class="fa fa-angle-double-right" aria-hidden="true"></i> &nbsp;</span>
				<button type="button" id="btnExportPdfInForm" class="btn btn-small btn-primary"><i class="fa fa-file-pdf-o"></i>&nbsp;${uiLabelMap.BSExportPdfByForm}</button>
			</div>
		</div>
		<#--
		<div id="addItemContainer" style="margin-top:20px; margin-bottom:2px">
			<div style="width:300px; margin: 0 auto; height: 45px">
				<span id="iconAddProductToExport"><i class="fa-3x fa-long-arrow-down" style="line-height: 33px;color:#5e5e5e" aria-hidden="true"></i></span>
				<button id="btnAddProductToExport" class="btn btn-small btn-primary" style="margin-left:10px; vertical-align:top">${uiLabelMap.BSAdd}</button>
				<button id="btnSubProductToExport" class="btn btn-small btn-primary" style="margin-left:10px; vertical-align:top">${uiLabelMap.BSDelete}</button>
			</div>
		</div>
		-->
		<div id="exportAnyContainer" style="">
			<#include "productPriceExportItemProdSearch.ftl"/>
			<div id="gridExportAnyProduct"></div>
			<span class="noteExportPriceProduct help-inline red" style="color: #657ba0; font-style: italic"><b>${uiLabelMap.BSNoteY}</b>: ${uiLabelMap.BSOnlyUPCIsRedWhichPrintable}</span>
			<div style="font-size:16px; margin-top:10px" class="pull-right">
				<span class="green"><b>${uiLabelMap.BSExportAny}</b> <i class="fa fa-angle-double-right" aria-hidden="true"></i> &nbsp;</span>
				<button type="button" id="btnExportPdfInFormAny" class="btn btn-small btn-primary"><i class="fa fa-file-pdf-o"></i>&nbsp;${uiLabelMap.BSExportPdfByForm}</button>
			</div>
		</div>
	</div><!--.span12-->
</div><!--.row-fluid-->
<br/>


<#assign dataField = "[
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'primaryProductCategoryId', type: 'string'},
       		{name: 'supplierCode', type: 'string'},
       		{name: 'idSKU', type: 'string'}, 
       		{name: 'unitPrice', type: 'number', formatter: 'float'},
       		{name: 'unitListPrice', type: 'number', formatter: 'float'},
       		{name: 'taxPercentage', type: 'number'},
       		{name: 'uomId', type: 'string'},
       		{name: 'iupprm', type: 'string'},
       		{name: 'numCopy', type: 'string'},
    	]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 100},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: 120},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', dataField: 'primaryProductCategoryId', width: 120},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAbbSupplierId)}', dataField: 'supplierCode', width: 120},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUPC)}', dataField: 'idSKU', width: 120, 
				cellsrenderer: function(row, column, value){
			 		var data = $('#gridProduct').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right';
			 		if (data && data.iupprm == '1') returnVal += ' red';
		   			returnVal += '\">' + value + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'uomId', width: 80},
			{text: '${StringUtil.wrapString(uiLabelMap.BSListPrice)}', dataField: 'unitListPrice', width: 120, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#gridProduct').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\"';
			 		if (data.unitListPrice != data.unitPrice) returnVal += ' style=\"text-decoration: line-through;\"';
			 		returnVal += '>';
			 		if (data && value) {
			 			var newValue = value;
			 			if (data.taxPercentage) {
			 				newValue = ((100 + data.taxPercentage)/100) * value;
			 			}
		   				returnVal += formatCurrencyNew(newValue, data.currencyUomId);
		   			}
		   			returnVal += '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPrice)}', dataField: 'price', width: 120, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#gridProduct').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		if (data && data.unitPrice) {
			 			var newValue = data.unitPrice;
			 			if (data.taxPercentage) {
			 				newValue = ((100 + data.taxPercentage)/100) * data.unitPrice;
			 			}
		   				returnVal += formatCurrencyNew(newValue, data.currencyUomId);
		   			}
		   			returnVal += '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAbbNumberPrint)}', dataField: 'numCopy', width: 80, cellsalign: 'right', filterable:false, sortable: false, 
		 		columntype: 'numberinput', cellsformat: 'd',
		 		cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
		   			returnVal += 1 + '</div>';
	   				return returnVal;
			 	},
		 	},
		"/>
		
<#include "productPriceExportItemProdAddPopup.ftl"/>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	if (!productPricesMap) var productPricesMap = {};
	
	$(function(){
		OlbExportProdPrice.init();
	});
	var OlbExportProdPrice = (function(){
		var prodCatalogDDB;
		var productCategoryDDB;
		var productGRID;
		var exportAnyProductGRID;
		var productStoreDDB;
		var validatorVAL;
		var idGridExport = $("#gridExportAnyProduct");
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			$("#radExportAll").jqxRadioButton({groupName: 'exportType', theme: 'olbius', width: 250, height: 25});
			$("#radExportAny").jqxRadioButton({groupName: 'exportType', theme: 'olbius', width: 250, height: 25, checked: true});
			$("#radFindAll").jqxRadioButton({groupName: 'filterType', theme: 'olbius', width: 250, height: 25, checked: true});
			$("#radFindByCatalog").jqxRadioButton({groupName: 'filterType', theme: 'olbius', width: 250, height: 25});
			$("#radFindByCategory").jqxRadioButton({groupName: 'filterType', theme: 'olbius', width: 250, height: 25});
			
			jOlbUtil.checkBox.create($("#radIncludePriceRule"), {checked:true});
			jOlbUtil.checkBox.create($("#radPrintAlterUom"));
			jOlbUtil.checkBox.create($("#radPrintNormalUPC"));
		};
		var initElementComplex = function(){
			var configProductItems = {
				datafields: ${dataField},
				columns: [${columnlist}],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 10,
				pagesizeoptions: [5, 10, 15, 20, 25, 50, 100],
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProdExportPrice',
			};
			productGRID = new OlbGrid($("#gridProduct"), null, configProductItems, []);
			
			var configProductItems = {
				datafields: [
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
		    	],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 100, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: 120, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', dataField: 'primaryProductCategoryId', width: 120, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.BSAbbSupplierId)}', dataField: 'supplierCode', width: 120, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.BSUPC)}', dataField: 'idSKU', width: 120, editable: false,
						cellsrenderer: function(row, column, value){
					 		var data = $('#gridExportAnyProduct').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"innerGridCellContent align-right';
					 		if (data && data.iupprm == '1') returnVal += ' red';
				   			returnVal += '\">' + value + '</div>';
			   				return returnVal;
					 	}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'uomId', width: 80, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.BSListPrice)}', dataField: 'unitListPriceVAT', width: 120, cellsalign: 'right', editable: false,
						filterable:false, sortable:false, cellsformat: 'd2', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#gridExportAnyProduct').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"innerGridCellContent align-right\"';
					 		if (data.unitListPriceVAT != data.unitPriceVAT) returnVal += ' style=\"text-decoration: line-through;\"';
					 		returnVal += '>';
					 		if (data && value) {
				   				returnVal += formatCurrencyNew(value, data.currencyUomId);
				   			}
				   			returnVal += '</div>';
			   				return returnVal;
					 	}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPrice)}', dataField: 'unitPriceVAT', width: 120, cellsalign: 'right', editable: false,
						filterable:false, sortable:false, cellsformat: 'd2', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#gridExportAnyProduct').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					 		if (data && value) {
				   				returnVal += formatCurrencyNew(value, data.currencyUomId);
				   			}
				   			returnVal += '</div>';
			   				return returnVal;
					 	}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSAbbNumberPrint)}', dataField: 'numCopy', width: 80, cellsalign: 'right', 
						filterable:false, sortable: false, editable: true, columntype: 'numberinput', cellsformat: 'd', cellClassName: 'background-prepare', 
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
				],
				width: '100%',
				height: 'auto',
				editable: true,
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 10,
				pagesizeoptions: [5, 10, 15, 20, 25, 50, 100],
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: false,
				virtualmode: false,
				showtoolbar: true,
				rendertoolbarconfig: {
					<#assign customcontrol1 = "fa fa-plus@${uiLabelMap.BSAdd}@javascript: void(0);@OlbExportProdPrice.openWindow()">
					<#assign customcontrol2 = "fa fa-plus@${uiLabelMap.BSDelete}@javascript: void(0);@OlbExportProdPrice.deleteItemProductAny()">
					titleProperty: "",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					customcontrol2: "${StringUtil.wrapString(customcontrol2)}",
				},
			};
			exportAnyProductGRID = new OlbGrid($("#gridExportAnyProduct"), null, configProductItems, []);
			
			var configProdCatalog = {
				widthButton: '80%',
				width: '500px',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				datafields: [
					{name: 'prodCatalogId', type: 'string'},
					{name: 'catalogName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProdCatalogId)}', datafield: 'prodCatalogId', width: '40%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCatalogName)}', datafield: 'catalogName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQGetListProdCatalog',
				useUtilFunc: false,
				key: 'prodCatalogId',
				description: ['catalogName'],
				autoCloseDropDown: true,
				showClearButton: true,
				disabled: true
			};
			prodCatalogDDB = new OlbDropDownButton($("#prodCatalogId"), $("#prodCatalogGrid"), null, configProdCatalog, null);
			
			var configProductCategory = {
				widthButton: '80%',
				width: '500px',
				dropDownHorizontalAlignment: 'right',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				searchId: 'productCategoryId',
				datafields: [
					{name: 'productCategoryId', type: 'string'},
					{name: 'parentCategoryId', type: 'string'},
					{name: 'categoryName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}', datafield: 'productCategoryId', width: '40%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}', datafield: 'categoryName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0&productCategoryTypeId=CATALOG_CATEGORY',
				useUtilFunc: false,
				key: 'productCategoryId',
				description: ['categoryName'],
				parentKeyId: 'parentCategoryId',
				gridType: 'jqxTreeGrid',
				autoCloseDropDown: true,
				showClearButton: true,
				disabled: true
			};
			productCategoryDDB = new OlbDropDownButton($("#productCategoryId"), $("#productCategoryGrid"), null, configProductCategory, null);
			
			var configProductStore = {
				widthButton: '80%',
				width: '500px',
				dropDownHorizontalAlignment: 'left',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				searchId: 'productStoreId',
				datafields: [
					{name: 'productStoreId', type: 'string'},
					{name: 'storeName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelId)}', datafield: 'productStoreId', width: '25%'},
					{text: '${StringUtil.wrapString(uiLabelMap.BSPSSalesChannelName)}', datafield: 'storeName', width: '75%'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule',
				useUtilFunc: false,
				key: 'productStoreId',
				description: ['storeName'],
				autoCloseDropDown: true,
			};
			productStoreDDB = new OlbDropDownButton($("#productStoreId"), $("#productStoreGrid"), null, configProductStore, null);
		};
		var initEvent = function(){
			$("#radFindAll").on("checked", function(){
				prodCatalogDDB.getButtonObj().jqxDropDownButton("disabled", true);
				productCategoryDDB.getButtonObj().jqxDropDownButton("disabled", true);
				prodCatalogDDB.clearAll();
				productCategoryDDB.clearAll();
			});
			$("#radFindByCatalog").on("checked", function(){
				prodCatalogDDB.getButtonObj().jqxDropDownButton("disabled", false);
				productCategoryDDB.getButtonObj().jqxDropDownButton("disabled", true);
				productCategoryDDB.clearAll();
			});
			$("#radFindByCategory").on("checked", function(){
				prodCatalogDDB.getButtonObj().jqxDropDownButton("disabled", true);
				productCategoryDDB.getButtonObj().jqxDropDownButton("disabled", false);
				prodCatalogDDB.clearAll();
			});
			$("#radIncludePriceRule").on("checked", function(){
				$("#containerStore").show();
			});
			$("#radIncludePriceRule").on("unchecked", function(){
				productStoreDDB.getButtonObj().jqxDropDownButton('close');
				validatorVAL.hide();
				$("#containerStore").hide();
			});
			$("#radPrintNormalUPC").on("checked", function(){
				$(".noteExportPriceProduct").hide();
			});
			$("#radPrintNormalUPC").on("unchecked", function(){
				$(".noteExportPriceProduct").show();
			});
			
			$("#btnFind").click(function(){
				if (!validatorVAL.validate()) return false;
				
				var filterType = "";
				if ($("#radFindAll").val()) filterType = "ALLPROD";
				if ($("#radFindByCatalog").val()) filterType = "BYCATALOG";
				if ($("#radFindByCategory").val()) filterType = "BYCATEGORY";
				var prodCatalogId = prodCatalogDDB.getValue();
				var productCategoryId = productCategoryDDB.getValue();
				
				var otherParam = "filterType=" + filterType;
				if (prodCatalogId) otherParam += "&prodCatalogId=" + prodCatalogId;
				if (productCategoryId) otherParam += "&productCategoryId=" + productCategoryId;
				if ($("#radIncludePriceRule").val()) {
					otherParam += "&includePriceRule=Y";
					otherParam += "&productStoreId=" + productStoreDDB.getValue();
				}
				productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProdExportPrice&" + otherParam);
			});
			$("#btnExportPdfInForm").click(function(){
				if (!validatorVAL.validate()) return false;
				
				var filterType = "";
				if ($("#radFindAll").val()) filterType = "ALLPROD";
				if ($("#radFindByCatalog").val()) filterType = "BYCATALOG";
				if ($("#radFindByCategory").val()) filterType = "BYCATEGORY";
				
				if (filterType == "ALLPROD") {
					jOlbUtil.confirm.dialog("${uiLabelMap.BSWarningPrintAllProductPrice}", 
						function(){
							printProductPrices(filterType);
						}
					);
				} else {
					printProductPrices(filterType);
				}
			});
			$("#btnExportPdfInFormAny").click(function(){
				if (!validatorVAL.validate()) return false;
				
				if ($("#radExportAny").val()) {
					var listProd = [];
					var dataRow = $(idGridExport).jqxGrid("getboundrows");
		        	if (dataRow) {
						for (var i = 0; i < dataRow.length; i++) {
							var dataItem = dataRow[i];
							if (dataItem != window && dataItem != undefined) {
								if (dataItem.idSKU) {
									var itemSKUPrint = dataItem.idSKU;
									if (!isNaN(dataItem.numCopy)) itemSKUPrint += "@" + parseInt(dataItem.numCopy);
									listProd.push(itemSKUPrint);
								}
								//if (dataItem.productId) {
								//	listProd.push(dataItem.productId);
								//}
							}
						}
					}
					if (listProd.length <= 0) {
						jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
						return false;
					} else {
						<#--
						var otherParam = "";
						if ($("#radIncludePriceRule").val()) {
							otherParam += "&includePriceRule=Y";
							otherParam += "&productStoreId=" + productStoreDDB.getValue();
						}
						if ($("#radPrintAlterUom").val()) {
							otherParam += "&printAlterUom=Y";
						}
						for (var i = 0; i < listProd.length; i++) {
							if (listProd[i]) otherParam += "&productId=" + listProd[i];
						}
						var url = 'ProductPrice.pdf?' + otherParam;
						var win = window.open(url, '_blank');
						win.focus();
						-->
						
						var form = document.createElement("form");
					    form.setAttribute("method", "POST");
					    form.setAttribute("action", "ProductPrice.pdf");
					    form.setAttribute("target", "_blank");
					    
					    if ($("#radIncludePriceRule").val()) {
					    	var hiddenField0 = document.createElement("input");
					        hiddenField0.setAttribute("type", "hidden");
					        hiddenField0.setAttribute("name", "includePriceRule");
					        hiddenField0.setAttribute("value", "Y");
					        form.appendChild(hiddenField0);
					        
					        var hiddenField02 = document.createElement("input");
					        hiddenField02.setAttribute("type", "hidden");
					        hiddenField02.setAttribute("name", "productStoreId");
					        hiddenField02.setAttribute("value", productStoreDDB.getValue());
					        form.appendChild(hiddenField02);
						}
						
						if ($("#radPrintAlterUom").val()) {
							var hiddenField03 = document.createElement("input");
					        hiddenField03.setAttribute("type", "hidden");
					        hiddenField03.setAttribute("name", "printAlterUom");
					        hiddenField03.setAttribute("value", "Y");
					        form.appendChild(hiddenField03);
						}
						
						if ($("#radPrintNormalUPC").val()) {
							var hiddenField04 = document.createElement("input");
					        hiddenField04.setAttribute("type", "hidden");
					        hiddenField04.setAttribute("name", "printNormalUPC");
					        hiddenField04.setAttribute("value", "Y");
					        form.appendChild(hiddenField04);
						}
				        
				        for (var i = 0; i < listProd.length; i++) {
							if (listProd[i]) {
								var hiddenField = document.createElement("input");
					            hiddenField.setAttribute("type", "hidden");
					            hiddenField.setAttribute("name", "productId");
					            hiddenField.setAttribute("value", listProd[i]);
					            form.appendChild(hiddenField);
							}
						}
				        
					    document.body.appendChild(form);
					    form.submit();
					}
				}
			});
			
			$("#radExportAll").on("checked", function(){
				$("#exportAllContainer").show();
				$("#searchAllContainer").show();
				$("#addItemContainer").hide();
				$("#exportAnyContainer").hide();
			});
			$("#radExportAny").on("checked", function(){
				$("#exportAllContainer").hide();
				$("#searchAllContainer").hide();
				$("#addItemContainer").show();
				$("#exportAnyContainer").show();
			});
			<#--
			$("#btnAddProductToExport").on("click", function(){
				var idGridView = $("#gridProduct");
				var idGridExport = $("#gridExportAnyProduct");
				var rowindex = $(idGridView).jqxGrid('getselectedrowindex');
		        var rowData = $(idGridView).jqxGrid("getrowdata", rowindex);
		        if (typeof(rowData) == "undefined" || rowData == null) {
		        	jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
		        	return false;
		        }
		        
				var uid = $(idGridExport).data("uid");
				if (uid) {
					$(idGridExport).jqxGrid('updaterow', uid, rowData);
				} else {
					$(idGridExport).jqxGrid('addrow', null, rowData);
				}
			});
			$("#btnSubProductToExport").on("click", function(){
				var idGridExport = $("#gridExportAnyProduct");
				var rowindex = $(idGridExport).jqxGrid('getselectedrowindex');
				if (rowindex == null || rowindex < 0) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
				} else {
					var rowData = $(idGridExport).jqxGrid("getrowdata", rowindex);
					if (rowData) {
						$(idGridExport).jqxGrid('deleterow', rowData.uid);
					}
				}
			});
			-->
		};
		var printProductPrices = function(filterType){
			var prodCatalogId = prodCatalogDDB.getValue();
			var productCategoryId = productCategoryDDB.getValue();
			
			var otherParam = "filterType=" + filterType;
			if (prodCatalogId) otherParam += "&prodCatalogId=" + prodCatalogId;
			if (productCategoryId) otherParam += "&productCategoryId=" + productCategoryId;
			if ($("#radIncludePriceRule").val()) {
				otherParam += "&includePriceRule=Y";
				otherParam += "&productStoreId=" + productStoreDDB.getValue();
			}
			if ($("#radPrintAlterUom").val()) {
				otherParam += "&printAlterUom=Y";
			}
			if ($("#radPrintNormalUPC").val()) {
				otherParam += "&printNormalUPC=Y";
			}
			var url = 'ProductPrice.pdf?' + otherParam;
			var win = window.open(url, '_blank');
			win.focus();
			<#--
			var form = document.createElement("form");
		    form.setAttribute("method", "POST");
		    form.setAttribute("action", "ProductPrice.pdf");
		    form.setAttribute("target", "_blank");
		    
		    var hiddenField0 = document.createElement("input");
	        hiddenField0.setAttribute("type", "hidden");
	        hiddenField0.setAttribute("name", "filterType");
	        hiddenField0.setAttribute("value", filterType);
	        form.appendChild(hiddenField0);
	        
	        if (prodCatalogId) {
	        	var hiddenField1 = document.createElement("input");
		        hiddenField1.setAttribute("type", "hidden");
		        hiddenField1.setAttribute("name", "prodCatalogId");
		        hiddenField1.setAttribute("value", prodCatalogId);
		        form.appendChild(hiddenField1);
	        }
			if (productCategoryId) {
				var hiddenField2 = document.createElement("input");
		        hiddenField2.setAttribute("type", "hidden");
		        hiddenField2.setAttribute("name", "productCategoryId");
		        hiddenField2.setAttribute("value", productCategoryId);
		        form.appendChild(hiddenField2);
			}
	        
		    var prodSelectedList = new Array();
		    var prodSelectedRows = $('#jqxgridProdSelected').jqxGrid('getrows');
			for (var i = 0; i < prodSelectedRows.length; i++) {
				var itemSelected = prodSelectedRows[i];
				if (itemSelected.productId != undefined) {
					var hiddenField = document.createElement("input");
		            hiddenField.setAttribute("type", "hidden");
		            hiddenField.setAttribute("name", "productId");
		            hiddenField.setAttribute("value", itemSelected.productId);
		            form.appendChild(hiddenField);
				}
			}
			
	        
		    document.body.appendChild(form);
		    form.submit();-->
		};
		var addItemsToGridPopup = function(listData){
			var gridObj = $("#gridExportAnyProduct");
			for (var i = 0; i < listData.length; i++) {
				var data = listData[i];
				if (OlbCore.isEmpty(data.productId)) {
					continue;
				}
				
				// new, update item
		   		var idStr = data.idSKU;
	    		if (typeof(productPricesMap[idStr]) != "undefined") {
	    			var allRowTmp = gridObj.jqxGrid("getboundrows");
	    			if (typeof(allRowTmp) != 'undefined') {
	    				for (var j = 0; j < allRowTmp.length; j++) {
	    					var itemTmp = allRowTmp[j];
	    					if (itemTmp != window && itemTmp.idSKU == data.idSKU && itemTmp.productId == data.productId 
	    							&& itemTmp.quantityUomId == data.quantityUomId && itemTmp.uid != null) {
	    						var rowBoundIndex = gridObj.jqxGrid('getrowboundindexbyid', itemTmp.uid);
	    						
	    						// update
	    			    		gridObj.jqxGrid('setcellvalue', rowBoundIndex, 'numCopy', data.numCopy);
	    						break;
	    					}
	    				}
	    			}
					// add row
	    			//gridObj.jqxGrid('addRow', null, itemValue, "first");
	    			
	    			// update data
	    			var itemValue = productPricesMap[idStr];
	    			itemValue.numCopy = data.numCopy;
	    			productPricesMap[idStr] = itemValue;
	    		} else {
	    			// add row
	    			var itemValue = {};
	    			itemValue.productId = data.productId;
	    			itemValue.productCode = data.productCode;
	    			itemValue.uomId = data.uomId;
	    			itemValue.productName = data.productName;
	    			itemValue.currencyUomId = data.currencyUomId;
	    			itemValue.primaryProductCategoryId = data.primaryProductCategoryId;
	    			itemValue.supplierCode = data.supplierCode;
	    			itemValue.idSKU = data.idSKU;
	    			itemValue.unitPriceVAT = data.unitPriceVAT;
	    			itemValue.unitListPriceVAT = data.unitListPriceVAT;
	    			itemValue.taxPercentage = data.taxPercentage;
	    			itemValue.iupprm = data.iupprm;
	    			itemValue.numCopy = data.numCopy;
	    			
	    			gridObj.jqxGrid('addRow', null, itemValue, "last");
	    			
	    			productPricesMap[idStr] = itemValue;
	    		}
			}
		};
		var deleteItemProductAny = function(){
			var rowindex = $(idGridExport).jqxGrid('getselectedrowindex');
			if (rowindex == null || rowindex < 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
			} else {
				var rowData = $(idGridExport).jqxGrid("getrowdata", rowindex);
				if (rowData) {
					var idStr = rowData.idSKU;
					$(idGridExport).jqxGrid('deleterow', rowData.uid);
					delete productPricesMap[idStr];
				}
			}
		};
		var initValidateForm = function(){
			var extendRules = [
					{input: '#productStoreId', message: '${StringUtil.wrapString(uiLabelMap.validFieldRequire)}', action: 'close', 
						rule: function(input, commit){
							if ($("#radIncludePriceRule").val()) {
								return OlbValidatorUtil.validElement(input, commit, 'validObjectNotNull', {objType: 'dropDownButton'});
							}
							return true;
						}
					},
	           ];
			var mapRules = [];
			validatorVAL = new OlbValidator($('#productPriceOption'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var getObj = function(){
			return {
				productStoreDDB: productStoreDDB
			}
		};
		var openWindow = function(){
			if (!validatorVAL.validate()) return false;
			
			OlbPriceItemsProdAddPop.openWindow();
			var filterType = "";
			if ($("#radFindAll").val()) filterType = "ALLPROD";
			if ($("#radFindByCatalog").val()) filterType = "BYCATALOG";
			if ($("#radFindByCategory").val()) filterType = "BYCATEGORY";
			var prodCatalogId = prodCatalogDDB.getValue();
			var productCategoryId = productCategoryDDB.getValue();
			
			var otherParam = "filterType=" + filterType;
			if (prodCatalogId) otherParam += "&prodCatalogId=" + prodCatalogId;
			if (productCategoryId) otherParam += "&productCategoryId=" + productCategoryId;
			if ($("#radIncludePriceRule").val()) {
				otherParam += "&includePriceRule=Y";
				otherParam += "&productStoreId=" + productStoreDDB.getValue();
			}
			OlbPriceItemsProdAddPop.getObj().productGRID.updateSource("jqxGeneralServicer?sname=JQGetListProdExportPrice&" + otherParam);
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var isCheckPrimaryUPC = function(){
			return !$("#radPrintNormalUPC").val();
		};
		return {
			init: init,
			getObj: getObj,
			getValidator: getValidator,
			openWindow: openWindow,
			addItemsToGridPopup: addItemsToGridPopup,
			deleteItemProductAny: deleteItemProductAny,
			isCheckPrimaryUPC: isCheckPrimaryUPC
		}
	}());
	
	var formatCurrencyNew = function(num, uom){
	    if(num == null){
	        return "";
	    }
	    
	    decimalseparator = ",";
	    thousandsseparator = ".";
	    if (typeof(uom) == "undefined" || uom == null) {
	        uom = "${defaultOrganizationPartyCurrencyUomId?if_exists}";
	    }
	    if (uom == "VND") {
	    	num = Math.round(num / 100) * 100;
	    }
	    if (uom == "USD") {
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    } else if(uom == "EUR") {
	        decimalseparator = ".";
	        thousandsseparator = ",";
	    }
	    
	    var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
	    if(str.indexOf(".") > 0) {
	        parts = str.split(".");
	        str = parts[0];
	    }
	    str = str.split("").reverse();
	    var c;
	    for(var j = 0, len = str.length; j < len; j++) {
	        if(str[j] != ",") {
	        	if(str[j] == '-'){
	        		if(output && output.length > 1){
	        			if(output[output.length - 1] == '.'){
	        				output.splice(output.length - 1,1);
	        			}
	            		c = true;
	            		break;
	        		}
	        	} 
	            output.push(str[j]);
	            if(i%3 == 0 && j < (len - 1)) {
	            	output.push(thousandsseparator);
	            }
	            i++;
	        }
	    }
	    if(c) output.push("-");
	    formatted = output.reverse().join("");
	    
	    if (uom == "VND") {
	    	return (formatted ? formatted : "0") + ((parts) ? parts[1].substr(0, 2) : "");
	    } else {
	    	var decimalfraction = "";
	    	
			//decimalfraction = decimalseparator + parts[1].substr(0, 2);
			var dectmp = (parts) ? parts[1].substr(0, 2) : "";
			if (dectmp.length == 1) {
				dectmp += "0";
			} else if (dectmp.length == 0) {
				dectmp += "00";
			}
			decimalfraction = decimalseparator + dectmp;
			
		    //var returnValue = (formatted ? formatted : "0") + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "");
			var returnValue = (formatted ? formatted : "0") + decimalfraction;
		    return returnValue;
	    }
	};
	
</script>