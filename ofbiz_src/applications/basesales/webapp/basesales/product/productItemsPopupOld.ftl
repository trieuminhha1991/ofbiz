<#if !gridProductItemsId?exists || !gridProductItemsId?has_content><#assign gridProductItemsId = "jqxgridProductItems"/></#if>
<#if !displayQuantityReturnPromo?exists || !displayQuantityReturnPromo?has_content><#assign displayQuantityReturnPromo = false/><#else><#assign displayQuantityReturnPromo = true></#if>
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
	var parentProductIds = {};
	var cellClass${gridProductItemsId} = function (row, columnfield, value) {
 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			if (typeof(data.parentProductId) != 'undefined' && typeof(data.colorCode) != 'undefined') {
 				var parentProductId = data.parentProductId;
 				if (parentProductId != null && !(/^\s*$/.test(parentProductId))) {
 					if (typeof(parentProductIds[parentProductId]) == 'undefined') {
 						var newColor = '' + data.colorCode;//''+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
 						var className = newColor.replace("#", "");
 						parentProductIds[parentProductId] = "background-" + className;
 						$("<style type='text/css'> .background-" + className + "{background-color:" + newColor + " !important} </style>").appendTo("head");
 						returnValue += "background-" + className;
 					} else {
	 					returnValue += parentProductIds[parentProductId];
	 				}
 				}
 			}
 			if (typeof(data.productAvailable) != 'undefined') {
 				if (data.productAvailable == 'true') {
 					returnValue += " row-cell-success";
 				} else if (data.productAvailable == 'false') {
 					returnValue += " row-cell-error";
 				}
 			}
 			//if (typeof(data.isVirtual) != 'undefined' && "Y" == data.isVirtual) {
	        //}
	        return returnValue;
 		}
    }
    var cellbeginedit = function (row, datafield, columntype, value) {
    	var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
    	if (typeof(data) != 'undefined') {
    		if (typeof(data.isVirtual) != 'undefined' && "Y" == data.isVirtual) return false;
    	}
    }
    if (productOrderMap == undefined) var productOrderMap = {};
    <#if shoppingCart?exists>
		<#assign orderItems = shoppingCart.makeOrderItems()>
		<#if orderItems?exists>
			<#list orderItems as orderItem>
				<#if (orderItem.productId?exists) && ("PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId || !(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>
				if (typeof(productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}']) != 'undefined') {
					var itemValue = productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'];
					<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
					itemValue.quantityReturnPromo = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
					<#else>
					itemValue.quantity = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
					</#if>
					productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = itemValue;
				} else {
					productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = {
						productId : '${orderItem.productId?default("")}',
						quantityUomId : '${orderItem.quantityUomId?default("")}',
						<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
						quantityReturnPromo : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
						<#else>
						quantity : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
						</#if>
					};
				}
				</#if>
			</#list>
		</#if>
    </#if>
</script>
<#--
,
cellsrenderer: function(row, column, value){
	var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		if ('Y' == data.isVariant) {
			return '<span title=' + value +'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + value + '</span>';
		}
	}
	return '<span title=' + value +'>' + value + '</span>';
}
cellbeginedit: cellbeginedit, 
-->
<#assign dataField = "[
				{name: 'productId', type: 'string'},
				{name: 'productCode', type: 'string'},
				{name: 'parentProductId', type: 'string'},
				{name: 'parentProductCode', type: 'string'},
		   		{name: 'productName', type: 'string'},
		   		{name: 'internalName', type: 'string'},
		   		{name: 'quantityUomId', type: 'string'},
		   		{name: 'packingUomIds', type: 'array'},
		   		{name: 'isVirtual', type: 'string'},
		   		{name: 'isVariant', type: 'string'},
		   		{name: 'parentProductId', type: 'string'},
		   		{name: 'features', type: 'string'},
		   		{name: 'colorCode', type: 'string'},
		   		{name: 'quantity', type: 'number', formatter: 'integer'},
		   		{name: 'quantityReturnPromo', type: 'number', formatter: 'integer'},
		   		{name: 'productAvailable', type: 'string'}
			]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 140, editable:false, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSParentProduct)}', dataField: 'parentProductId', editable: false, width: 140, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', editable: false, width: 100, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 140, editable: false, cellClassName: cellClass${gridProductItemsId}},
			"/>
<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: 120, filterable: false, columntype: 'dropdownlist', cellClassName: cellClass${gridProductItemsId},
					cellsrenderer: function(row, column, value){
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		var resultVal = value;
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productOrderMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.quantityUomId)) {
				   				data.productUomId = itemMapValue.quantityUomId;
				   				resultVal = itemMapValue.quantityUomId;
				   			}
			   			}
			   			for (var i = 0 ; i < uomData.length; i++){
							if (resultVal == uomData[i].uomId){
								returnVal += uomData[i].description + '</div>';
		   						return returnVal;
							}
						}
			   			returnVal += value + '</div>';
		   				return returnVal;
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var packingUomData = new Array();
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
						
						var itemSelected = data['quantityUomId'];
						var packingUomIdArray = data['packingUomIds'];
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
				 		var sourceDataPacking = {
			                localdata: packingUomData,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', width: 140, cellsalign: 'right', filterable:false, sortable: false, 
			 		cellClassName: cellClass${gridProductItemsId}, columntype: 'numberinput', cellsformat: 'd',
			 		cellsrenderer: function(row, column, value){
				 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productOrderMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.quantity)) {
				   				data.quantity = itemMapValue.quantity;
				   				returnVal += formatnumber(itemMapValue.quantity) + '</div>';
				   				return returnVal;
				   			}
			   			}
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
			 	},"/>
<#if hasOlbEntityPermission("SALESORDER", "RETURN_PRODPROMO") && displayQuantityReturnPromo>
<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSQuantityReturnPromo)}', dataField: 'quantityReturnPromo', width: 120, cellsalign: 'right', filterable:false, sortable: false, 
			 		cellClassName: cellClass${gridProductItemsId}, columntype: 'numberinput', cellsformat: 'd',
			 		cellsrenderer: function(row, column, value){
				 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		if (data != undefined && data != null) {
				 			var productId = data.productId;
				 			var idStr = data.productId + '@' + data.quantityUomId;
				 			var itemMapValue = productOrderMap[idStr];
							if (typeof(itemMapValue) != 'undefined' && OlbCore.isNotEmpty(itemMapValue.quantityReturnPromo)) {
				   				data.quantityReturnPromo = itemMapValue.quantityReturnPromo;
				   				returnVal += formatnumber(itemMapValue.quantityReturnPromo) + '</div>';
				   				return returnVal;
				   			}
			   			}
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
				},"/>
</#if>

<#if prodCatalogIdArr?exists>
	<#assign prodCatalogIdStr = ""/>
	<#list prodCatalogIdArr as prodCatalogIdItem>
		<#assign prodCatalogIdStr = prodCatalogIdStr + "&prodCatalogId=" + prodCatalogIdItem/>
	</#list>
</#if>
<#if !idExisted?exists><#assign idExisted="false"/></#if>
<#if !viewSize?exists><#assign viewSize="15"/></#if>
<#if !otherParamUrl?exists><#assign otherParamUrl = "productStoreId=${defaultProductStoreId?if_exists}&hasrequest=Y${prodCatalogIdStr?if_exists}"/></#if>

<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
		viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" groupable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&${otherParamUrl}" 
	/>
	
<script type="text/javascript">
	$(function(){
		$("#${gridProductItemsId}").on("cellendedit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "quantity") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#${gridProductItemsId}").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.productId) {
		    		var productId = data.productId;
		    		var quantityUomId = data.quantityUomId;
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (isNaN(newValue)) {
			   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', oldValue);
			   			return false;
			   		}
			   		
			   		var itemId = "" + productId + "@" + quantityUomId;
			   		if (typeof(productOrderMap[itemId]) != 'undefined') {
			   			var itemValue = productOrderMap[itemId];
			   			itemValue.quantity = newValue;
			   			productOrderMap[itemId] = itemValue;
			   		} else {
			   			var itemValue = {};
			   			itemValue.productId = productId;
			   			itemValue.quantityUomId = quantityUomId;
			   			itemValue.quantity = newValue;
			   			itemValue.quantityReturnPromo = data.quantityReturnPromo;
			   			productOrderMap[itemId] = itemValue;
			   		}
		    	}
	    	} else if (args.datafield == "quantityReturnPromo") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#${gridProductItemsId}").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.productId) {
		    		var productId = data.productId;
		    		var quantityUomId = data.quantityUomId;
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (isNaN(newValue)) {
			   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantityReturnPromo', oldValue);
			   			return false;
			   		}
			   		
			   		var itemId = "" + productId + "@" + quantityUomId;
			   		if (typeof(productOrderMap[itemId]) != 'undefined') {
			   			var itemValue = productOrderMap[itemId];
			   			itemValue.quantityReturnPromo = newValue;
			   			productOrderMap[itemId] = itemValue;
			   		} else {
			   			var itemValue = {};
			   			itemValue.productId = productId;
			   			itemValue.quantityUomId = quantityUomId;
			   			itemValue.quantity = data.quantity;
			   			itemValue.quantityReturnPromo = newValue;
			   			productOrderMap[itemId] = itemValue;
			   		}
		    	}
	    	} else if (args.datafield == "quantityUomId") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#${gridProductItemsId}").jqxGrid("getrowdata", rowBoundIndex);
		    	if (OlbCore.isNotEmpty(data) && OlbCore.isNotEmpty(data.productId) && OlbCore.isNotEmpty(data.quantity)) {
		    		var productId = data.productId;
		    		var quantityStr = data.quantity;
			   		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (isNaN(quantityStr)) {
			   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', oldValue);
			   			return false;
			   		}
			   		var quantity = parseInt(quantityStr);
			   		var quantityReturnPromo = null;
			   		if (!isNaN(data.quantityReturnPromo)) quantityReturnPromo = parseInt(data.quantityReturnPromo);
			   		
			   		var itemId = "" + productId + "@" + oldValue;
			   		if (typeof(productOrderMap[itemId]) != 'undefined') {
			   			var itemValue = productOrderMap[itemId];
			   			//itemValue.quantityUomId = newValue;
			   			itemValue.quantity = itemValue.quantity - quantity;
			   			if (quantityReturnPromo != null) itemValue.quantityReturnPromo = itemValue.quantityReturnPromo - quantityReturnPromo;
			   			productOrderMap[itemId] = itemValue;
			   		}
			   		var itemIdNew = "" + productId + "@" + newValue;
			   		if (typeof(productOrderMap[itemIdNew]) != 'undefined') {
			   			var itemValue = productOrderMap[itemIdNew];
			   			//itemValue.quantityUomId = newValue;
			   			itemValue.quantity = quantity;
			   			itemValue.quantityReturnPromo = quantityReturnPromo;
			   			productOrderMap[itemIdNew] = itemValue;
			   		} else {
			   			var itemValue = {};
			   			itemValue.productId = productId;
			   			itemValue.quantityUomId = newValue;
			   			itemValue.quantity = quantity;
			   			itemValue.quantityReturnPromo = quantityReturnPromo;
			   			productOrderMap[itemIdNew] = itemValue;
			   		}
		    	}
	    	}
    	});
	});
</script>
<#--
,
cellsrenderer: function(row, column, value){
	var cellContent = '<div class=\"innerGridCellContent align-right\">';
	if (value == undefined || value == null || value == '') {
		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
 		if (data != undefined && data != null && data.productId != null) {
			var productId = data.productId;
			var quantity = productQuantitiesMap['' + productId];
			if (quantity != undefined && quantity != null && quantity != \"\") {
				data.quantity = quantity;
				cellContent += quantity;
			}
		}
	}
	cellContent += value + '</div>';
	return cellContent;
}
-->
<#--
<div id="jqxgridSO"></div>
<script type="text/javascript">
	$(function(){
		pageCommonThird.init();
	});
	var pageCommonThird = (function(){
		var init = function(){
			initElementAdvance();
		};
		var initElementAdvance = function(){
			var datafields = [
				{name: 'productId', type: 'string'},
				{name: 'parentProductId', type: 'string'},
		   		{name: 'productName', type: 'string'},
		   		{name: 'internalName', type: 'string'},
		   		{name: 'quantityUomId', type: 'string'},
		   		{name: 'packingUomIds', type: 'string'},
		   		{name: 'isVirtual', type: 'string'},
		   		{name: 'isVariant', type: 'string'},
		   		{name: 'parentProductId', type: 'string'},
		   		{name: 'features', type: 'string'},
		   		{name: 'quantity', type: 'number', formatter: 'integer'},
			];
			var columns = [
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productId', width: '13%', editable:false, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSParentProduct)}', dataField: 'parentProductId', editable: false, width: '13%', cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', editable: false, width: '10%', cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSInternalName)}', dataField: 'internalName', editable: false, width: '15%', cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable: false, cellClassName: cellClass${gridProductItemsId}},
				{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: '12%', columntype: 'dropdownlist', cellClassName: cellClass${gridProductItemsId},
					cellsrenderer: function(row, column, value){
						for (var i = 0 ; i < uomData.length; i++){
							if (value == uomData[i].uomId){
								var content = '<div class=\"innerGridCellContent\" title=\"' + uomData[i].description + '\">' + uomData[i].description + '</div>';
								return content;
							}
						}
						return '<div class=\"innerGridCellContent\" title=\"' + value + '\">' + value + '</div>';
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var packingUomData = new Array();
						var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
						
						var itemSelected = data['quantityUomId'];
						var packingUomIdArray = data['packingUomIds'];
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
				 		var sourceDataPacking = {
			                localdata: packingUomData,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', cellsalign: 'right', filterable:false, sortable: false, cellClassName: cellClass${gridProductItemsId}}
			];
			var configProductList = {
				showdefaultloadelement: false,
				autoshowloadelement: false,
				dropDownHorizontalAlignment: 'right',
				datafields: datafields,
				columns: columns,
				useUrl: true,
				root: 'results',
				url: 'JQGetListProductByCatalogAndStore&productStoreId=${defaultProductStoreId?if_exists}&hasrequest=Y',
				useUtilFunc: true,
				clearfilteringbutton: false,
				editable: true,
				alternativeAddPopup: 'alterpopupWindow',
				pagesize: 15,
				showtoolbar: false,
				editmode: 'click',
				selectionmode: 'multiplecellsadvanced',
				width: '100%',
				bindresize: false,
				groupable: true,
			};
			new OlbGrid($("#${gridProductItemsId}"), null, configProductList, []);
		};
		return {
			init: init,
		};
	}());
</script>
-->