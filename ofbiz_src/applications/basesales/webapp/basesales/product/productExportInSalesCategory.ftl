
<div class="row-fluid">
	<div class="span12">
		<form class="form-horizontal form-window-content-custom" method="post" action="<@ofbizUrl>findProductPriceAction</@ofbizUrl>">
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSSalesChannel}</label>
						</div>
						<div class="span7">
							<div id="productStoreId"></div>
				   		</div>
					</div>
				</div><!-- .span6 -->
				<div class="span6">
					<div class="row-fluid">
				   		<div class="pull-left">
							<button type="button" id="btnFindProductPrice" class="btn btn-small btn-primary"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionFind}</button>
				   		</div>
					</div>
				</div><!-- .span6 -->
			</div><!-- .row-fluid -->
		</form>
	</div>
</div>

<#assign dataField = "[
			{name: 'idSKU', type: 'string'},
			{name: 'iupprm', type: 'string'},
       		{name: 'productId', type: 'string'},
       		{name: 'productCode', type: 'string'},
       		{name: 'primaryProductCategoryId', type: 'string'},
       		{name: 'productName', type: 'string'}, 
       		{name: 'quantityUomId', type: 'string'},
       		{name: 'purchaseUomId', type: 'string'},
       		{name: 'salesUomId', type: 'string'},
       		{name: 'taxCategoryId', type: 'string'},
       		{name: 'supplierId', type: 'string'},
       		{name: 'partyCode', type: 'string'},
       		{name: 'purchasePriceVAT', type: 'number', formatter: 'float'},
       		{name: 'salesPriceVAT', type: 'number', formatter: 'float'},
       		{name: 'productQuotationId', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'salesDiscontinuationDate', type: 'date', other: 'Timestamp'},
       		{name: 'purchaseDiscontinuationDate', type: 'date', other: 'Timestamp'},
       		{name: 'quantityConvert', type: 'number', formatter: 'integer'},
    	]"/>
<#assign columnlist = "
			{text: 'UPC', dataField: 'idSKU', width: 130},
			{text: 'IUPPRM', dataField: 'iupprm', width: 60},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 100},
			{text: '${StringUtil.wrapString(uiLabelMap.BSNHCategoryId)}', dataField: 'primaryProductCategoryId', width: 70},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: 160},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUnitUom)}', dataField: 'quantityUomId', width: 90},
			{text: '${StringUtil.wrapString(uiLabelMap.BSQtyConvert)}', dataField: 'quantityConvert', width: 60, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd', 
			 	cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
		 			returnVal += formatnumber(value, '${locale}');
			 		returnVal += '</div>';
			 		return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSTaxProductCategory)}', dataField: 'taxCategoryId', width: 120, filterable:false, sortable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BPOSupplierId)}', dataField: 'partyCode', width: 80},
			{text: '${StringUtil.wrapString(uiLabelMap.BSPurchasePrice)}', dataField: 'purchasePriceVAT', width: 110, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
			 		if (data) {
			 			returnVal += formatcurrency(value, data.currencyUomId);
			 		}
			 		returnVal += '</div>';
			 		return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPrice)}', dataField: 'salesPriceVAT', width: 110, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
	   				var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
			 		if (data) {
			 			returnVal += formatcurrency(value, data.currencyUomId);
			 		}
			 		returnVal += '</div>';
			 		return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSQuotationId)}', dataField: 'productQuotationId', width: 120, filterable:false, sortable:false},
			{text: '${uiLabelMap.BSPurchaseDiscontinuationDate}', dataField: 'purchaseDiscontinuationDate', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${uiLabelMap.BSSalesDiscontinuationDate}', dataField: 'salesDiscontinuationDate', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range',
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
		"/>
<div class="row-fluid">
	<div class="span12">
		<div id="jqxgridProd"></div>
	</div>
</div>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.combobox.js"></script>
<script type="text/javascript">
	$(function(){
		OlbProductExportInSCate.init();
	});
	var OlbProductExportInSCate = (function(){
		var productStoreDDL;
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			var configProductStore = {
				width: '100%',
				dropDownHeight: '200px',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProductStorePriceRule&pagesize=0',
				key: 'productStoreId',
				value: 'storeName',
				autoDropDownHeight: false,
				selectedIndex: 0,
			}
			productStoreDDL = new OlbComboBox($("#productStoreId"), null, configProductStore, null);
			
			<#assign customcontrol1 = "fa fa-file-excel-o@@javascript: void(0);@OlbProductExportInSCate.exportExcel()">
			var configProductItems = {
				datafields: ${dataField},
				columns: [${columnlist}],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 15,
				//pagesizeoptions: [5, 10, 15, 20, 25, 50, 100],
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: '', // jqxGeneralServicer?sname=JQExportProductInSalesCategory
				showtoolbar:true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
				},
				bindresize: true,
			};
			productGRID = new OlbGrid($("#jqxgridProd"), null, configProductItems, []);
		};
		var initEvent = function(){
			$("#btnFindProductPrice").on("click", function(){
				var productStoreId = productStoreDDL.getValue();
				
				var otherParam = "";
				if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
				
				productGRID.updateSource("jqxGeneralServicer?sname=JQExportProductInSalesCategory" + otherParam);
			});
		};
		var exportExcel = function(){
			var isExistData = productGRID.isExistData();
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
				return false;
			}
			
			var productStoreId = productStoreDDL.getValue();
			
			var otherParam = "";
			if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
			
			window.location.href = "exportProductInSalesCategoryExcel?" + otherParam;
		};
		return {
			init: init,
			exportExcel: exportExcel
		};
	}());
</script>
