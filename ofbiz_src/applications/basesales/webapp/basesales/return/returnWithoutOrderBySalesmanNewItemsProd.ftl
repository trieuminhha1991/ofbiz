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
	var getUomDesc = function(uomId) {
		for (var i in uomData) {
			var obj = uomData[i];
			if (obj.uomId == uomId) return obj.description;
		}
		return uomId;
	}
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
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: 120, editable:false, filterable: false,
				cellsrenderer: function(row, column, value){
	   				return '<div>' + getUomDesc(value) + '</div>';
			 	},
			},
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
				editable: true, filterable:false, sortable:false, cellsformat: 'd', cellClassName: 'background-prepare', 
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



<#include "returnWithoutOrderBySalesmanNewProdSearch.ftl"/>
<#assign urlSName = ""/>	
<div id="jqxgridProd"></div>
<#include "returnWithoutOrderBySalesmanNewItemsAddPopup.ftl">

<script type="text/javascript">
	var dataFieldItemsProd = ${dataFieldItemsProd};
	var columnListItemsProd = [${columnListItemsProd}];
	var urlSName = "${urlSName}";
</script>
<script type="text/javascript">
	$(function(){
		OlbReturnAddProductItemsBySalesman.init();
	});
	var OlbReturnAddProductItemsBySalesman = (function(){
		var productGRID;
		
		var init = function(){
			initElementComplex();
			initEvent();
		};
		var initElementComplex = function(){
			var rendertoolbar = function (toolbar) {
				toolbar.html("");
				var container = $("<div id='toolbarcontainerGridProduct' class='widget-header' style='height:33px !important;'><div id='jqxProductSearch' class='pull-right' style='margin-left: -10px !important; margin-top: 4px; padding: 0px !important'></div></div>");
				toolbar.append(container);
				container.append('<div class="margin-top10">');
				container.append('<a href="javascript:OlbReturnNewItemsProdAddPopBySalesman.openWindow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus"></i></a>');
				container.append('<a href="javascript:OlbReturnAddProductItemsBySalesman.removeItemFromGrid()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="red fa fa-times"></i></a>');
				container.append('</div>');
			}
			// TH2: JQGetListProductSellAll
			var configGridProduct = {
				datafields: dataFieldItemsProd,
				columns: columnListItemsProd,
				width: '100%',
				height: 'auto',
				sortable: true,
				editable: true,
				filterable: true,
				pageable: true,
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: false,
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				virtualmode:false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:true,
				columnsresize: true,
				isSaveFormData: true,
				toolbarheight: 38,
				formData: "filterObjData",
				selectionmode: "singlerow",
				bindresize: true,
				pagesize: 10,
				rendertoolbar: rendertoolbar,
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

