<script type="text/javascript">
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)!/>
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
		{	uomId: '${uomItem.uomId}',
			description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
</script>
<#assign dataField = "[
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'uomId', type: 'string'},
       		{name: 'taxPercentage', type: 'number'}, 
       		{name: 'price', type: 'number', formatter: 'float'},
       		{name: 'unitListPrice', type: 'number', formatter: 'float'},
       		{name: 'priceVAT', type: 'number', formatter: 'float'},
       		{name: 'unitListPriceVAT', type: 'number', formatter: 'float'},
       		{name: 'colorCode', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'barcode', type: 'string'},
    	]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSSTT}', datafield: '', width: 50, sortable: false, filterable: false, editable: false,
				cellsrenderer: function (row, column, value) {
					return '<div class=\"innerGridCellContent\">' + (row + 1) + '</div>';
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '12%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSBarcode)}', dataField: 'barcode', width: '20%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSTax)}', dataField: 'taxPercentage', editable:false, filterable: false, width: '7%', cellsalign: 'right', cellsformat: 'p'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'uomId', width: '7%', editable:true, filtertype: 'checkedlist',  
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
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
				createfilterwidget: function (column, columnElement, widget) {
			 		if (uomData.length > 0) {
			 			var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId',
							renderer: function(index, label, value){
								if (uomData.length > 0) {
									for(var i = 0; i < uomData.length; i++){
										if(uomData[i].uomId == value){
											return '<span>' + uomData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
			 		}
	   			}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSListPrice)}', dataField: 'unitListPriceVAT', width: '12%', cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
			 		var returnVal = '<div class=\"innerGridCellContent align-right\"';
			 		if (data) {
			 			if (data.unitListPrice != data.price) returnVal += ' style=\"text-decoration: line-through;\"';
			 			returnVal += '>';
			 			returnVal += OlbCore.formatCurrencyRoundByUom(value, data.currencyUomId);
			 		} else {
			 			returnVal += '>';
			 		}
			 		returnVal += '</div>';
			 		return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSSalesPrice)}', dataField: 'priceVAT', width: '12%', cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
	   				var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
			 		if (data) {
			 			returnVal += OlbCore.formatCurrencyRoundByUom(value, data.currencyUomId);
			 		}
			 		returnVal += '</div>';
			 		return returnVal;
			 	}
			},
		"/>

<div id="jqxgridProd"></div>

<script type="text/javascript">
	var productItemsOLBG;
	
	$(function(){
		OlbAddProductItems.init();
	});
	var OlbAddProductItems = (function(){
		var init = function(){
			initComplexElement();
		};
		var initComplexElement = function(){
			<#assign customcontrol1 = "fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript: void(0);@OlbAddProductItems.exportExcel()">
			var configProductItems = {
				datafields: ${dataField},
				columns: [${columnlist}],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProductSalesPriceChange',
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				virtualmode:true,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:true,
				columnsresize: true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
				},
				bindresize: true,
			};
			productItemsOLBG = new OlbGrid($("#jqxgridProd"), null, configProductItems, []);
		};
		var exportExcel = function(){
			var isExistData = productItemsOLBG.isExistData();
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSDataIsEmpty}");
				return false;
			}
			
			//$("#jqxgridProd").jqxGrid('exportdata', 'xls', fileName);
			
			var changeDateTypeId = OlbProdPriceFind.getObj().changeDateTypeDDL.getValue();
			var productStoreId = OlbProdPriceFind.getObj().productStoreDDB.getValue();
			var fromDate;
			var thruDate;
			if (typeof($('#fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#fromDate').jqxDateTimeInput('getDate') != null) {
				fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
			}
			if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
				thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
			}
			
			var otherParam = "";
			if (OlbCore.isNotEmpty(changeDateTypeId)) otherParam += "&changeDateTypeId=" + changeDateTypeId;
			if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
			if (OlbCore.isNotEmpty(fromDate)) otherParam += "&fromDate=" + fromDate;
			if (OlbCore.isNotEmpty(thruDate)) otherParam += "&thruDate=" + thruDate;
			
			window.location.href = "exportProdSalesPriceChangeExcel?" + otherParam;
		};
		return {
			init: init,
			exportExcel: exportExcel,
		};
	}());
</script>
