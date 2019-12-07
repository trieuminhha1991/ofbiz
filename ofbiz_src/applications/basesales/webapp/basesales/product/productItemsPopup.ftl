<#if !gridProductItemsId?exists || !gridProductItemsId?has_content><#assign gridProductItemsId = "jqxgridProductItems"/></#if>
<#if !displayQuantityReturnPromo?exists || !displayQuantityReturnPromo?has_content><#assign displayQuantityReturnPromo = false/><#else><#assign displayQuantityReturnPromo = true></#if>
<#if !orderNewSearchProductSplit?exists><#assign orderNewSearchProductSplit = false/></#if>
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
 			/*
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
 			*/
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

	<#function makeRowItemId productId quantityUomId idEAN>
	    <#if !productId?has_content><#assign productId = "_NA_"/></#if>
	    <#if !quantityUomId?has_content><#assign quantityUomId = "_NA_"/></#if>
	    <#if !idEAN?has_content><#assign idEAN = "_NA_"/></#if>
		<#assign returnValue = productId + "@" + quantityUomId + "@" + idEAN>
	    <#return returnValue>
	</#function>

    if (productOrderMap == undefined) var productOrderMap = {};
    <#if shoppingCart?exists>
		<#assign orderItems = Static["com.olbius.basesales.shoppingcart.ShoppingCartWorker"].getOrderItemsInfo(shoppingCart)!>
		<#if orderItems?exists>
			<#list orderItems as orderItem>
				<#if (orderItem.productId?exists) && ("PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId || !(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>
					<#assign rowItemId = makeRowItemId(orderItem.productId?default('_NA_'), orderItem.quantityUomId?default('_NA_'), orderItem.idEAN?default('_NA_'))/>
					if (typeof(productOrderMap['${rowItemId}']) != 'undefined') {
						var itemValue = productOrderMap['${rowItemId}'];
						<#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
						itemValue.quantityReturnPromo = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
						<#else>
						itemValue.quantity = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
						</#if>
						productOrderMap['${rowItemId}'] = itemValue;
					} else {
						productOrderMap['${rowItemId}'] = {
							productId : '${orderItem.productId?default("")}',
							quantityUomId : '${orderItem.quantityUomId?default("")}',
							idEAN : '${orderItem.idEAN?default("")}',
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
<#assign tmpReturnPromoPer = false/>
<#if hasOlbEntityPermission("SALESORDER", "RETURN_PRODPROMO") && displayQuantityReturnPromo><#assign tmpReturnPromoPer = true/></#if>
<#assign dataField = "[
				{name: 'productId', type: 'string'},
				{name: 'productCode', type: 'string'},
		   		{name: 'productName', type: 'string'},
		   		{name: 'internalName', type: 'string'},
		   		{name: 'quantityUomId', type: 'string'},
		   		{name: 'packingUomIds', type: 'array'},
		   		{name: 'isVirtual', type: 'string'},
		   		{name: 'isVariant', type: 'string'},
		   		{name: 'features', type: 'string'},
		   		{name: 'colorCode', type: 'string'},
		   		{name: 'quantity', type: 'number', formatter: 'integer'},
		   		{name: 'quantityReturnPromo', type: 'number', formatter: 'integer'},
		   		{name: 'productAvailable', type: 'string'},
		   		{name: 'requireAmount', type: 'string'},
		   		{name: 'idEAN', type: 'string'},
			]"/>
<#if !orderNewSearchProductSplit>
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 140, editable:false, cellClassName: cellClass${gridProductItemsId}},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 140, editable: false, cellClassName: cellClass${gridProductItemsId}},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', editable: false, filterable: false, sortable: false, width: 100, cellClassName: cellClass${gridProductItemsId}},
				"/>
	<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: 120, filterable: false, columntype: 'dropdownlist', cellClassName: cellClass${gridProductItemsId},
						cellsrenderer: function(row, column, value){
							var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					 		var resultVal = value;
					 		if (data != undefined && data != null) {
					 			var productId = data.productId;
					 			var idStr = OlbProdItemPopup.makeRowItemId(data.productId, data.quantityUomId, data.idEAN);
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
					 			var idStr = OlbProdItemPopup.makeRowItemId(data.productId, data.quantityUomId, data.idEAN);
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
	<#if tmpReturnPromoPer>
	<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSQuantityReturnPromo)}', dataField: 'quantityReturnPromo', width: 120, cellsalign: 'right', filterable:false, sortable: false,
				 		cellClassName: cellClass${gridProductItemsId}, columntype: 'numberinput', cellsformat: 'd',
				 		cellsrenderer: function(row, column, value){
					 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
					 		if (data != undefined && data != null) {
					 			var productId = data.productId;
					 			var idStr = OlbProdItemPopup.makeRowItemId(data.productId, data.quantityUomId, data.idEAN);
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
<#else>
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: 140, editable:false, cellClassName: cellClass${gridProductItemsId}},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minwidth: 140, editable: false, cellClassName: cellClass${gridProductItemsId},
						cellsrenderer: function(row, column, value){
							var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
							if (data && OlbCore.isNotEmpty(data.idEAN)) {
								value += ' (' + data.idEAN + ')';
							}
							return \"<span>\" + value + \"</span>\";
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', editable: false, filterable: false, sortable: false, width: 100, cellClassName: cellClass${gridProductItemsId}},
				"/>
	<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: 120, filterable: false, columntype: 'dropdownlist', cellClassName: cellClass${gridProductItemsId},
						cellsrenderer: function(row, column, value){
					 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
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
				 	},"/>
	<#if tmpReturnPromoPer>
	<#assign columnlist = columnlist + "{text: '${StringUtil.wrapString(uiLabelMap.BSQuantityReturnPromo)}', dataField: 'quantityReturnPromo', width: 120, cellsalign: 'right', filterable:false, sortable: false,
				 		cellClassName: cellClass${gridProductItemsId}, columntype: 'numberinput', cellsformat: 'd',
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
					},"/>
	</#if>
</#if>

<#if prodCatalogIdArr?exists>
	<#assign prodCatalogIdStr = ""/>
	<#list prodCatalogIdArr as prodCatalogIdItem>
		<#assign prodCatalogIdStr = prodCatalogIdStr + "&prodCatalogId=" + prodCatalogIdItem/>
	</#list>
</#if>
<#if !idExisted?exists><#assign idExisted="false"/></#if>
<#if !viewSize?exists><#assign viewSize="15"/></#if>
<#if !otherParamUrl?exists>
	<#assign otherParamUrl = ""/>
	<#if !orderNewSearchProductSplit><#assign otherParamUrl = otherParamUrl + "productStoreId=${defaultProductStoreId?if_exists}"/></#if>
	<#assign otherParamUrl = otherParamUrl + "&hasrequest=Y${prodCatalogIdStr?if_exists}"/>
</#if>
<#assign groupable = "${(!orderNewSearchProductSplit)?string}">
<#assign virtualmode = "${(!orderNewSearchProductSplit)?string}">
<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" width="100%" bindresize="false" groupable=groupable virtualmode=virtualmode
		url="jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&${otherParamUrl}"
	/>

<script type="text/javascript">
	var OlbProdItemPopup = (function(){

		var init = function(){
			initEvent();
		};
		var initEvent = function(){
			$("#${gridProductItemsId}").on("endaddrow", function(event, rowPosition){
	            var data = null;
	            var rowBoundIndex = null;
	            if (rowPosition == "last") {
	            	var rows = $("#${gridProductItemsId}").jqxGrid('getrows');
	            	rowBoundIndex = rows.length-1;
	        	} else if (rowPosition == "first") {
	        		rowBoundIndex = 0;
	            }
	            if (rowBoundIndex != null) {
	            	data = $("#${gridProductItemsId}").jqxGrid('getrowdata', rowBoundIndex);
	            } else {
	            	return false;
	            }
	            if (data && data.productId) {
		    		var productId = data.productId;
		    		var quantityUomId = data.quantityUomId;
			   		var quantity = data.quantity;
			   		var quantityReturnPromo = data.quantityReturnPromo;
			   		var idEAN = data.idEAN;
			   		if (isNaN(quantity)) {
			   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', 0);
			   		}
			   		<#if tmpReturnPromoPer>
			   		if (isNaN(quantityReturnPromo)) {
			   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantityReturnPromo', 0);
		   			}
			   		</#if>
			   		var itemId = makeRowItemId(productId, quantityUomId, idEAN);
			   		if (typeof(productOrderMap[itemId]) != 'undefined') {
			   			var itemValue = productOrderMap[itemId];
			   			itemValue.quantity = quantity;
			   			itemValue.quantityReturnPromo = quantityReturnPromo;
			   			productOrderMap[itemId] = itemValue;
			   		} else {
			   			var itemValue = {};
			   			itemValue.productId = productId;
			   			itemValue.quantityUomId = quantityUomId;
			   			itemValue.quantity = quantity;
			   			itemValue.quantityReturnPromo = quantityReturnPromo;
			   			itemValue.idEAN = idEAN;
			   			productOrderMap[itemId] = itemValue;
			   		}
	            }
			});
			$("#${gridProductItemsId}").on("cellendedit", function (event) {
		    	var args = event.args;
		    	if (args.datafield == "quantity") {
		    		var rowBoundIndex = args.rowindex;
			    	var data = $("#${gridProductItemsId}").jqxGrid("getrowdata", rowBoundIndex);
			    	if (data && data.productId) {
			    		var productId = data.productId;
			    		var quantityUomId = data.quantityUomId;
			    		var idEAN = data.idEAN;
				   		var oldValue = args.oldvalue;
				   		var newValue = args.value;
				   		if (isNaN(newValue)) {
				   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', oldValue);
				   			return false;
				   		}

				   		var itemId = makeRowItemId(productId, quantityUomId, idEAN);
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
			    		var idEAN = data.idEAN;
				   		var oldValue = args.oldvalue;
				   		var newValue = args.value;
				   		if (isNaN(newValue)) {
				   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantityReturnPromo', oldValue);
				   			return false;
				   		}

				   		var itemId = makeRowItemId(productId, quantityUomId, idEAN);
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
			    		var idEAN = data.idEAN;
				   		var oldValue = args.oldvalue;
				   		var newValue = args.value;
				   		if (isNaN(quantityStr)) {
				   			$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', oldValue);
				   			return false;
				   		}
				   		var quantity = parseInt(quantityStr);
				   		var quantityReturnPromo = null;
				   		if (!isNaN(data.quantityReturnPromo)) quantityReturnPromo = parseInt(data.quantityReturnPromo);

				   		var itemId = makeRowItemId(productId, oldValue, idEAN);
				   		if (typeof(productOrderMap[itemId]) != 'undefined') {
				   			var itemValue = productOrderMap[itemId];
				   			//itemValue.quantityUomId = newValue;
				   			itemValue.quantity = itemValue.quantity - quantity;
				   			if (quantityReturnPromo != null) itemValue.quantityReturnPromo = itemValue.quantityReturnPromo - quantityReturnPromo;
				   			productOrderMap[itemId] = itemValue;
				   		}
				   		var itemIdNew = makeRowItemId(productId, newValue, idEAN);
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
		};
		var makeRowItemId = function(productId, quantityUomId, idEAN) {
			if (OlbCore.isEmpty(productId)) productId = "_NA_";
			if (OlbCore.isEmpty(quantityUomId)) quantityUomId = "_NA_";
			if (OlbCore.isEmpty(idEAN)) idEAN = "_NA_";
			var itemId = "" + productId + "@" + quantityUomId + "@" + idEAN;
			return itemId;
		};
		var addOrIncreaseQuantity = function(newData){
			var productId = newData.productId;
			var quantityUomId = newData.quantityUomId;
			var idEAN = newData.idEAN;
			var quantity = newData.quantity;

			var rowItemId = makeRowItemId(productId, quantityUomId, idEAN);
			if (typeof(productOrderMap[rowItemId]) != "undefined") {
				// increase quantity
				var rows = $("#${gridProductItemsId}").jqxGrid('getrows');
				if (rows) {
					$.each(rows, function(key, rowi){
						if (rowItemId == makeRowItemId(rowi.productId, rowi.quantityUomId, rowi.idEAN)) {
							var quantityNew = parseInt(rowi.quantity) + 1;
							var rowBoundIndex = $('#${gridProductItemsId}').jqxGrid('getrowboundindexbyid', rowi.uid);
							if (rowBoundIndex > -1) {
								$('#${gridProductItemsId}').jqxGrid('setcellvalue', rowBoundIndex, 'quantity', quantityNew);
								var itemValue = productOrderMap[rowItemId];
					   			itemValue.quantity = quantityNew;
					   			productOrderMap[rowItemId] = itemValue;
							}
							return false;
						}
					});
				}
			} else {
				// add row
				var rowData = {
					productId: productId,
					productCode: newData.productCode,
					productName: newData.productName,
					quantityUomId: quantityUomId,
					quantity: newData.quantity,
					idEAN: idEAN,
					packingUomIds: newData.packingUomIds,
					requireAmount: newData.requireAmount,
				};
				var rowPosition = "first";
				$("#${gridProductItemsId}").jqxGrid('addRow', null, rowData, rowPosition);

				$("#${gridProductItemsId}").trigger("endaddrow", [rowPosition]);
			}
		};
		return {
			init: init,
			makeRowItemId: makeRowItemId,
			addOrIncreaseQuantity: addOrIncreaseQuantity,
		};
	}());
	$(function(){
		OlbProdItemPopup.init();
	});
</script>