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
</script>
<#assign dataFieldItemsProd = "[
			{name: 'productId', type: 'string'},
			{name: 'productCode', type: 'string'},
       		{name: 'productName', type: 'string'},
       		{name: 'description', type: 'string'},
       		{name: 'quantityUomId', type: 'string'},
       		{name: 'currencyUomId', type: 'string'},
       		{name: 'returnPrice', type: 'number'},
       		{name: 'returnQuantity', type: 'number'},
    	]"/>
<#assign columnListItemsProd = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 140, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 120, editable:false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: 120, editable:false, filterable: false},
			{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', width: 160, editable:true, filterable: false, cellClassName: 'background-prepare'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSReturnPrice)} (${uiLabelMap.BSBeforeVAT})', dataField: 'returnPrice', width: 160, cellsalign: 'right',
				editable:true, filterable:false, sortable:false, cellsformat: 'd2', cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
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
			{text: '${StringUtil.wrapString(uiLabelMap.BSReturnQty)}', dataField: 'returnQuantity', width: 160, cellsalign: 'right',
				editable: true, filterable:false, sortable:false, cellClassName: 'background-prepare', 
			 	cellsrenderer: function(row, column, value){
			 		var newValue = value;
			 		if (!value) {
			 			newValue = 0;
			 		} 
	 				var returnVal = '<div class=\"innerGridCellContent align-right\">';
	   				returnVal += formatnumber(newValue) + '</div>';
	   				return returnVal;
			 	}
			},
		"/>

<#include "returnSupplierWithoutOrderNewProdSearch.ftl"/>

<#assign urlSName = ""/>	
<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@OlbReturnNewItemsProdAddPop.openWindow()">
<#assign customcontrol2 = "icon-minus open-sans@${uiLabelMap.BSDelete}@javascript: void(0);@OlbReturnAddProductItems.removeItemFromGrid()">
<div id="jqxgridProd"></div>

<#include "returnSupplierWithoutOrderNewItemsAddPopup.ftl">

<script type="text/javascript">
	var dataFieldItemsProd = ${dataFieldItemsProd};
	var columnListItemsProd = [${columnListItemsProd}];
	var customcontrol1 = "${StringUtil.wrapString(customcontrol1)}";
	var customcontrol2 = "${StringUtil.wrapString(customcontrol2)}";
	var urlSName = "${urlSName}";
</script>
<script type="text/javascript">
	$(function(){
		OlbReturnAddProductItems.init();
	});
	var OlbReturnAddProductItems = (function(){
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			// TH2: JQGetListProductSellAll
			var configGridProduct = {
				datafields: dataFieldItemsProd,
				columns: columnListItemsProd,
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				editable: true,
				editmode: 'click',
				pageable: true,
				pagesize: 15,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: urlSName,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				//selectionmode:'multiplerows',
				virtualmode: false,
				showtoolbar: true,
				rendertoolbarconfig: {
					titleProperty: "",
					customcontrol1: customcontrol1,
					customcontrol2: customcontrol2
				},
			};
			
			productGRID = new OlbGrid($("#jqxgridProd"), null, configGridProduct, []);
		};
		var initEvent = function(){
			
		};
		var removeItemFromGrid = function(){
			var rowindexes = $("#jqxgridProd").jqxGrid("getselectedrowindexes");
			if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
				jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
				return false;
			}
			for (var i = 0; i < rowindexes.length; i++) {
				var dataItem = $("#jqxgridProd").jqxGrid("getrowdata", rowindexes[i]);
				if (dataItem) {
					if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
						$("#jqxgridProd").jqxGrid('deleterow', dataItem.uid);
					}
				}
			}
		};
		var addItemsToGridPopup = function(listData){
			for (var i = 0; i < listData.length; i++) {
				var data = listData[i];
				if (OlbCore.isEmpty(data.productId)) {
					continue;
				}
				
				var flagAdd = true;
				
    			// update row
    			var allRowTmp = $("#jqxgridProd").jqxGrid("getboundrows");
    			if (typeof(allRowTmp) != 'undefined') {
    				for (var j = 0; j < allRowTmp.length; j++) {
    					var itemTmp = allRowTmp[j];
    					if (itemTmp != window && itemTmp.productId == data.productId 
    							&& itemTmp.quantityUomId == data.quantityUomId && itemTmp.uid != null) {
    						//$("#jqxgridProd").jqxGrid("deleterow", itemTmp.uid);
    						var rowBoundIndex = $('#jqxgridProd').jqxGrid('getrowboundindexbyid', itemTmp.uid);
    						
    						// update
    						$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'description', data.description);
    						$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'returnPrice', data.returnPrice);
    						$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'returnQuantity', data.returnQuantity);
    			    		
    			    		flagAdd = false;
    						break;
    					}
    				}
    			}
    			
    			if (flagAdd) {
    				// add row
    				var itemValue = {};
	    			itemValue.productId = data.productId;
	    			itemValue.productCode = data.productCode;
	    			itemValue.quantityUomId = data.quantityUomId;
	    			itemValue.productName = data.productName;
	    			itemValue.description = data.description;
	    			itemValue.returnPrice = data.returnPrice;
	    			itemValue.returnQuantity = data.returnQuantity;
    				
    				$("#jqxgridProd").jqxGrid('addRow', null, itemValue, "first");
    			}
			}
		};
		var getListProductAll = function(){
			var dataRow = $("#jqxgridProd").jqxGrid("getboundrows");
			if (typeof(dataRow) == 'undefined') {
				jOlbUtil.alert.info("Error check data");
			}
			
			var listProd = [];
			var icount = 0;
			for (var i = 0; i < dataRow.length; i++) {
				var dataItem = dataRow[i];
				if (dataItem != window && dataItem != undefined) {
					if ((typeof(dataItem.returnQuantity) != 'undefined' && parseInt(dataItem.returnQuantity) > 0)) {
						var prodItem = {
							productId: dataItem.productId,
							quantityUomId: dataItem.quantityUomId,
							description: dataItem.description,
							returnPrice: dataItem.returnPrice,
							returnQuantity: dataItem.returnQuantity
						};
						listProd.push(prodItem);
						icount++;
					}
				}
			}
			return listProd;
		};
		return {
			init: init,
			removeItemFromGrid: removeItemFromGrid,
			addItemsToGridPopup: addItemsToGridPopup,
			getListProductAll: getListProductAll,
		};
	}());
</script>

