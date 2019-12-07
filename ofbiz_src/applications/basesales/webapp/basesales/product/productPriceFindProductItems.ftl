<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
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
       		{name: 'salesDiscontinuationDate', type: 'date', other: 'Timestamp' },
		    {name: 'purchaseDiscontinuationDate', type: 'date', other: 'Timestamp'}
    	]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSBarcode)}', dataField: 'barcode', width: '14%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '14%', editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable:false},
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
			{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesDiscontinuationDate)}', dataField: 'salesDiscontinuationDate' , width: '12%', editable: false, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', 
		   			cellsrenderer: function (row, column, value){
		   				if (value){
		   					return '<span class=\"align-right\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
		   				} else {
		   					return '<span class=\"align-right\"></span>';
		   				}
		   			},
		   				
		   	},
		   	{ text: '${StringUtil.wrapString(uiLabelMap.BSPurchaseDiscontinuationDate)}', dataField: 'purchaseDiscontinuationDate' ,  width: '12%', editable: false, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', 
		   			cellsrenderer: function (row, column, value){	
		   				if (value){
		   					return '<span class=\"align-right\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
		   				} else {
		   					return '<span class=\"align-right\"></span>';
		   				}
		   			},
		   	},
		"/>

<#assign urlSName = "JQFindProductPriceQuotes"/>
<div id="jqxgridProd"></div>

<script type="text/javascript">
	var productItemsOLBG;
	var urlSNameFindProdPrice = "${urlSName}";
	
	$(function(){
		OlbAddProductItems.init();
	});
	var OlbAddProductItems = (function(){
		var init = function(){
			initComplexElement();
		};
		var initComplexElement = function(){
			<#assign customcontrol1 = "fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript: void(0);@OlbAddProductItems.exportExcel()">
			<#assign customcontrol2 = "fa fa-file-pdf-o@${uiLabelMap.BSExportPdf}@javascript: void(0);@OlbAddProductItems.exportPdf()">
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
				url: 'jqxGeneralServicer?sname=${urlSName}',
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
				<#--	customcontrol2: "${StringUtil.wrapString(customcontrol2)}", -->
				},
				bindresize: true,
			};
			productItemsOLBG = new OlbGrid($("#jqxgridProd"), null, configProductItems, []);
		};
		var exportExcel = function(){
			var isExistData = productItemsOLBG.isExistData();
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
				return false;
			}
			
			//$("#jqxgridProd").jqxGrid('exportdata', 'xls', fileName);
			
			var productStoreId = OlbProdPriceFind.getObj().productStoreDDL.getValue();
			var partyId = OlbProdPriceFind.getObj().partyDDB.getValue();
			
			var otherParam = "";
			//if (OlbCore.isNotEmpty(salesMethodChannelEnumId)) otherParam += "&salesMethodChannelEnumId=" + salesMethodChannelEnumId;
			if (OlbCore.isNotEmpty(partyId)) otherParam += "&partyId=" + partyId;
			if (OlbCore.isNotEmpty(productStoreId)) otherParam += "&productStoreId=" + productStoreId;
			
			window.location.href = "exportGoodQuotaExcel?" + otherParam;
		};
		var exportPdf = function(){
			var isExistData = productItemsOLBG.isExistData();
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
				return false;
			}
			
			var productStoreId = OlbProdPriceFind.getObj().productStoreDDL.getValue();
			var partyId = OlbProdPriceFind.getObj().partyDDB.getValue();
			
			var otherParam = "productStoreId=" + productStoreId;
			if (partyId) otherParam += "&partyId=" + partyId;
			var url = 'findQuotation.pdf?' + otherParam;
			var win = window.open(url, '_blank');
			win.focus();
			<#--
			var form = document.createElement("form");
		    form.setAttribute("method", "POST");
		    form.setAttribute("action", "findQuotation.pdf");
		    form.setAttribute("target", "_blank");
		    
		    if (OlbCore.isNotEmpty(productStoreId)) {
		    	var hiddenField0 = document.createElement("input");
		        hiddenField0.setAttribute("type", "hidden");
		        hiddenField0.setAttribute("name", "productStoreId");
		        hiddenField0.setAttribute("value", productStoreId);
		        form.appendChild(hiddenField0);
		    }
	        if (OlbCore.isNotEmpty(partyId)) {
	        	var hiddenField1 = document.createElement("input");
		        hiddenField1.setAttribute("type", "hidden");
		        hiddenField1.setAttribute("name", "partyId");
		        hiddenField1.setAttribute("value", partyId);
		        form.appendChild(hiddenField1);
	        }
	        
		    document.body.appendChild(form);
		    form.submit(); -->
		    return true;
		};
		return {
			init: init,
			exportExcel: exportExcel,
			exportPdf: exportPdf,
		};
	}());
</script>



