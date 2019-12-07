<script type="text/javascript">
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
		{	uomId: '${uomItem.uomId}',
			description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var currencyUomId = $('#currencyUomId').val();
	var parentProductIds = {};
	<#--var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			if (typeof(data.parentProductId) != 'undefined' && data.parentProductId != null && typeof(data.colorCode) != 'undefined') {
 				var parentProductId = data.parentProductId;
 				if (parentProductId != null && !(/^\s*$/.test(parentProductId))) {
 					if (typeof(parentProductIds[parentProductId]) == 'undefined') {
 						var newColor = '' + data.colorCode;
 						var className = newColor.replace("#", "");
 						parentProductIds[parentProductId] = "background-" + className;
 						$("<style type='text/css'> .background-" + className + "{background-color:" + newColor + " !important} </style>").appendTo("head");
 						returnValue += "background-" + className;
 					} else {
	 					returnValue += parentProductIds[parentProductId];
	 				}
 				}
 			} else if (typeof(data.colorCode) != 'undefined') {
 				//$("<style type='text/css'> .background-4B89AA {background-color: #4B89AA !important} </style>").appendTo("head");
 				returnValue += "font-bold";
 			}
	        return returnValue;
 		}
    }-->
    if (!productPricesMap) var productPricesMap = {};
    <#--
    <#if quotationItems?exists>
		<#list quotationItems as quotationItem>
			<#assign keyItem = "${quotationItem.productId?default('_NA_')}@${quotationItem.quantityUomId?default('_NA_')}">
			if (typeof(productPricesMap['${keyItem}']) != 'undefined') {
				var itemValue = productPricesMap['${keyItem}'];
    			itemValue.productId = "${quotationItem.productId?default("")}";
    			itemValue.productCode = "${quotationItem.productCode?default("")}";
    			itemValue.quantityUomId = "${quotationItem.quantityUomId?default("")}";
    			itemValue.listPrice = "${quotationItem.listPrice?default("")}";
    			itemValue.listPriceVAT = "${quotationItem.listPriceVAT?default("")}";
    			itemValue.selected = true;
    			itemValue.taxPercentage = "${quotationItem.taxPercentage?default("")}";
    			itemValue.features = "${quotationItem.features?default("")}";
    			itemValue.productName = "${quotationItem.productName?default("")}";
    			productPricesMap['${keyItem}'] = itemValue;
			} else {
				productPricesMap['${keyItem}'] = {
					productId : "${quotationItem.productId?default("")}",
	    			productCode : "${quotationItem.productCode?default("")}",
	    			quantityUomId : "${quotationItem.quantityUomId?default("")}",
	    			listPrice : "${quotationItem.listPrice?default("")}",
	    			listPriceVAT : "${quotationItem.listPriceVAT?default("")}",
	    			selected : true,
	    			taxPercentage : "${quotationItem.taxPercentage?default("")}",
	    			features : "${quotationItem.features?default("")}",
	    			productName : "${quotationItem.productName?default("")}"
				};
			}
		</#list>
    </#if>
    -->
</script>
<#if !copyMode?exists><#assign copyMode = false/></#if>
<#assign dataFieldItemsProd = "[
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'parentProductId', type: 'string'},
       		{name: 'parentProductCode', type: 'string'},
       		{name: 'features', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'quantityConvert', type: 'string'},
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
    	]"/>
<#assign columnListItemsProd = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 120, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 80, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSTax)}', dataField: 'taxPercentage', width: 70, editable:false, filterable: false, cellsalign: 'right', cellsformat: 'p'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: 70, editable:true, filterable: false, cellClassName: 'background-prepare', columntype: 'dropdownlist', 
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
			 	initeditor: function (row, cellvalue, editor) {
			 		var packingUomData = new Array();
					var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
					
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
			 		var newValue = value;
			 		if (!value) {
			 			newValue = 1;
			 		} 
	 				var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatnumber(newValue) + '</div>';
	   				return returnVal;
			 	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)} (T)', dataField: 'unitPriceBef', width: 140, cellsalign: 'right',
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
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
			{text: '${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)} (S)', dataField: 'unitPriceVAT', width: 140, cellsalign: 'right',
				editable: false, filterable:false, sortable:false, cellsformat: 'd2', 
			 	cellsrenderer: function(row, column, value){
			 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
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
			{text: '${StringUtil.wrapString(uiLabelMap.BSNewPrice)} (T)', dataField: 'listPrice', width: 140, cellsalign: 'right',
				filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var currencyUomId = $('#currencyUomId').val();
			 		if (OlbCore.isEmpty(value)) {
			 			var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
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
			{text: '${StringUtil.wrapString(uiLabelMap.BSNewPrice)} (S)', dataField: 'listPriceVAT', width: 140, cellsalign: 'right', editable: true,
				filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
				cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			 		var currencyUomId = $('#currencyUomId').val();
			 		if (OlbCore.isEmpty(value)) {
			 			var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
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
			},
		"/>
<#--
<#assign columngrouplist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSPriceToCustomer)}', align: 'center', name: 'PriceToCustomerColGroup'}
		"/>
<#assign currentCatalogId = Static["com.olbius.basesales.util.SalesUtil"].getProductCatalogDefault(delegator)!/>
urlSName = "JQListProductAndTaxByCatalog&channelEnumId&showAll=Y&prodCatalogId=${currentCatalogId?if_exists}"

{text: '${StringUtil.wrapString(uiLabelMap.BSParentProduct)}', dataField: 'parentProductCode', width: '10%', editable:false, cellClassName: cellClass},
			{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', width: '8%', editable:false, cellClassName: cellClass},

{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', width: '10%', editable:false, cellClassName: cellClass},
			

${setContextField("dataFieldProductItems", dataField)}
${setContextField("columnlistProductItems", columnlist)}
${setContextField("columnlistProductItemsConfirm", columnlistConfirm)}
${setContextField("columngrouplistProductItems", columngrouplist)}
-->

<#include "quotationNewItemsProdSearch.ftl"/>
<#assign urlSName = ""/>	
<#if productQuotation?exists && productQuotation.productQuotationId?exists>
	<#assign urlSName = "jqxGeneralServicer?sname=JQListProductAndTaxInQuotation&productQuotationId=${productQuotation.productQuotationId}&pagesize=0"/>
	<#--
	<#if productStoreAppls?has_content>
		<#list productStoreAppls as productStore>
			<#assign urlSName = urlSName + "&productStoreIds=" + productStore.productStoreId/>
		</#list>
	</#if>
	<#else><#assign urlSName = "JQListProductAndTaxByCatalog"/>-->
</#if>
<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@OlbQuotNewItemsProdAddPop.openWindow()">
<#assign customcontrol2 = "icon-minus open-sans@${uiLabelMap.BSDelete}@javascript: void(0);@OlbAddProductItems.removeItemFromGrid()">
<div id="jqxgridProd"></div>
<#--
<@jqGrid id="jqxgridProd" url="jqxGeneralServicer?sname=${urlSName}" columnlist=columnlist dataField=dataField 
		isShowTitleProperty="false" editable="true" viewSize="15" showtoolbar="true" editmode="dblclick" selectionmode="multiplerows"  
		customcontrol1=customcontrol1 customcontrol2=customcontrol2/>
-->

<#include "quotationNewItemsProdAddPopup.ftl">
<script type="text/javascript">
	var dataFieldItemsProd = ${dataFieldItemsProd};
	var columnListItemsProd = [${columnListItemsProd}];
	var customcontrol1 = "${StringUtil.wrapString(customcontrol1)}";
	var customcontrol2 = "${StringUtil.wrapString(customcontrol2)}";
	var urlSName = "${urlSName}";
</script>
<script type="text/javascript" src="/poresources/js/quotation/quotationNewItemsProd.js?v=0.0.1"></script>

