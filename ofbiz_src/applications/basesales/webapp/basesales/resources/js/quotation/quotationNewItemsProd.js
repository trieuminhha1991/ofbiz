$(function(){
	OlbAddProductItems.init();
});
var OlbAddProductItems = (function(){
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
	/*var processDataRowSelect = function(rowBoundIndex) {
		var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
    	if (data) {
    		var idStr = data.productId + "@" + data.quantityUomId;
    		if (typeof(productPricesMap[idStr]) != "undefined") {
    			var itemValue = productPricesMap[idStr];
    			itemValue.selected = true;
    			productPricesMap[idStr] = itemValue;
    		} else {
				var itemValue = {};
    			itemValue.productId = data.productId;
    			itemValue.productCode = data.productCode;
    			itemValue.quantityUomId = data.quantityUomId;
    			itemValue.listPrice = data.listPrice;
    			itemValue.listPriceVAT = data.listPriceVAT;
    			itemValue.selected = true;
    			itemValue.taxPercentage = data.taxPercentage;
    			itemValue.features = data.features;
    			itemValue.productName = data.productName;
    			productPricesMap[idStr] = itemValue;
    		}
    	}
	};*/
	//var selectableCheckBox = true;
	var initEvent = function(){
		productGRID.bindingCompleteListener(function(){
			var dataRows = $("#jqxgridProd").jqxGrid("getboundrows");
			if (dataRows) {
				for (var i = 0; i < dataRows.length; i++) {
					var dataRow = dataRows[i];
					var idStr = dataRow.productId + "@" + dataRow.quantityUomId;
					if (typeof(productPricesMap[idStr]) != "undefined") {
						// update dataRow
		    			var itemValue = productPricesMap[idStr];
		    			itemValue.quantityUomId = dataRow.quantityUomId;
		    			itemValue.listPrice = dataRow.listPrice;
		    			itemValue.listPriceVAT = dataRow.listPriceVAT;
		    			itemValue.selected = true;
		    			productPricesMap[idStr] = itemValue;
					} else {
						// add row
		    			var itemValue = {};
		    			itemValue.productId = dataRow.productId;
		    			itemValue.productCode = dataRow.productCode;
		    			itemValue.quantityUomId = dataRow.quantityUomId;
		    			itemValue.listPrice = dataRow.listPrice;
		    			itemValue.listPriceVAT = dataRow.listPriceVAT;
		    			itemValue.selected = true;
		    			itemValue.taxPercentage = dataRow.taxPercentage;
		    			itemValue.features = dataRow.features;
		    			itemValue.productName = dataRow.productName;
		    			itemValue.quantityConvert = dataRow.quantityConvert;
		    			itemValue.unitPrice = dataRow.unitPrice;
		    			itemValue.unitPriceBef = dataRow.unitPriceBef;
		    			itemValue.unitPriceVAT = dataRow.unitPriceVAT;
		    			itemValue.packingUomIds = dataRow.packingUomIds;
		    			productPricesMap[idStr] = itemValue;
					}
				}
			}
		}, true);
		/*
		$("#jqxgridProd").on("bindingcomplete", function (event) {
			var dataRow = $("#jqxgridProd").jqxGrid("getboundrows");
			if (typeof(dataRow) != 'undefined') {
				var icount = 0;
				selectableCheckBox = false;
				$.each(dataRow, function(key, value){
					if (value) {
						var isSelected = false;
						var idStr = value.productId + "@" + value.quantityUomId;
						if (typeof(productPricesMap[idStr]) != "undefined") {
							var itemValue = productPricesMap[idStr];
							if (itemValue.selected) {
								$('#jqxgridProd').jqxGrid('selectrow', icount);
								isSelected = true;
							}
						}
						if (OlbElementUtil.isNotEmpty(value.productId) && !isSelected) {
							$('#jqxgridProd').jqxGrid('unselectrow', icount);
						}
					}
					icount++;
				});
				selectableCheckBox = true;
			}
		});
		$('#jqxgridProd').on('rowselect', function (event) {
			if (selectableCheckBox) {
				var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    if (Object.prototype.toString.call(rowBoundIndex) === '[object Array]') {
			    	for (var i = 0; i < rowBoundIndex.length; i++) {
			    		processDataRowSelect(rowBoundIndex[i]);
			    	}
			    } else {
			    	processDataRowSelect(rowBoundIndex);
			    }
			}
		});
		$('#jqxgridProd').on('rowunselect', function (event) {
			if (selectableCheckBox) {
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	if (typeof(data) != 'undefined') {
		    		var quantityUomId = data.quantityUomId;
		    		var idStr = data.productId + "@" + quantityUomId;
		    		if (typeof(productPricesMap[idStr]) != "undefined") {
		    			var itemValue = productPricesMap[idStr];
		    			itemValue.selected = false;
		    			productPricesMap[idStr] = itemValue;
		    		}
		    	}
			}
		});
		*/
		$("#jqxgridProd").on("cellendedit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "listPrice") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	var oldValue = args.oldvalue;
		   		var newValue = args.value;
		    	if (data && data.productId) {
		    		var priceVAT = null;
		    		if (data.taxPercentage) {
				   		if (OlbCore.isNotEmpty(newValue)) {
				   			var taxPercentage = data.taxPercentage;
					   		var valueCal = newValue + newValue * taxPercentage / 100;
					   		valueCal = Math.round(valueCal * 100) / 100
					   		//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', newValue);
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
				    		priceVAT = valueCal;
				   		} else {
				   			var valueCal = "";
				   			//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', newValue);
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
				   		}
			    	} else {
			    		if (OlbCore.isNotEmpty(newValue)) {
			    			//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', newValue);
			    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', newValue);
				    		priceVAT = newValue;
			    		} else {
			    			var valueCal = "";
			    			//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
			    		}
			    	}
		    		
		    		// new, update item
		    		var idStr = data.productId + "@" + data.quantityUomId;
		    		var itemValue = null;
		    		if (typeof(productPricesMap[idStr]) == "undefined") {
		    			itemValue = {};
		    			itemValue.productId = data.productId;
		    			itemValue.productCode = data.productCode;
		    			itemValue.quantityUomId = data.quantityUomId;
		    		} else {
		    			itemValue = productPricesMap[idStr];
		    		}
		    		itemValue.listPrice = newValue;
		    		if (priceVAT != null) itemValue.listPriceVAT = priceVAT;
		    		itemValue.taxPercentage = data.taxPercentage;
    				itemValue.features = data.features;
    				itemValue.productName = data.productName;
			   		productPricesMap[idStr] = itemValue;
			   		//if (newValue > 0) {
			   		//	$('#jqxgridProd').jqxGrid('selectrow', rowBoundIndex);
			   		//} else {
			   		//	$('#jqxgridProd').jqxGrid('unselectrow', rowBoundIndex);
			   		//}
		    	}
	    	} else if (args.datafield == "listPriceVAT") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	var oldValue = args.oldvalue;
		   		var newValue = args.value;
		    	if (data && data.productId) {
		    		var priceBeforeVAT = null;
		    		if (data.taxPercentage) {
				   		if (OlbCore.isNotEmpty(newValue)) {
				   			var taxPercentage = data.taxPercentage;
					   		var valueCal = newValue * 100 / (100 + taxPercentage);
					   		valueCal = Math.round(valueCal * 100) / 100
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
				    		//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', newValue);
					   		priceBeforeVAT = valueCal;
				   		} else {
				   			var valueCal = "";
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
				    		//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', newValue);
				   		}
			    	} else {
			    		if (OlbCore.isNotEmpty(newValue)) {
			    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', newValue);
			    			//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', newValue);
			    			priceBeforeVAT = newValue;
			    		} else {
			    			var valueCal = "";
				    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', valueCal);
				    		//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', valueCal);
			    		}
			    	}
		    		
		    		// new, update item
		    		var idStr = data.productId + "@" + data.quantityUomId;
		    		var itemValue = null;
		    		if (typeof(productPricesMap[idStr]) == "undefined") {
		    			itemValue = {};
		    			itemValue.productId = data.productId;
		    			itemValue.productCode = data.productCode;
		    			itemValue.quantityUomId = data.quantityUomId;
		    		} else {
		    			itemValue = productPricesMap[idStr];
		    		}
		    		itemValue.listPriceVAT = newValue;
		    		if (priceBeforeVAT != null) itemValue.listPrice = priceBeforeVAT;
		    		itemValue.taxPercentage = data.taxPercentage;
    				itemValue.features = data.features;
    				itemValue.productName = data.productName;
			   		productPricesMap[idStr] = itemValue;
			   		//if (newValue > 0) {
			   		//	$('#jqxgridProd').jqxGrid('selectrow', rowBoundIndex);
			   		//} else {
			   		//	$('#jqxgridProd').jqxGrid('unselectrow', rowBoundIndex);
			   		//}
		    	}
	    	} else if (args.datafield == "quantityUomId") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.packingUomIds) {
		    		var oldValue = args.oldvalue;
			   		var newValue = args.value;
		    		var packingUomIdArray = data.packingUomIds;
		    		var quantityConvert = 1;
		    		var unitPriceConvert = data.unitPrice;
		    		for (var i = 0; i < packingUomIdArray.length; i++) {
						var packingUomIdItem = packingUomIdArray[i];
						if (packingUomIdItem.uomId == newValue) {
							if (typeof(packingUomIdItem.quantityConvert) != "undefined" && packingUomIdItem.quantityConvert != null) {
								quantityConvert = packingUomIdItem.quantityConvert;
							}
							unitPriceConvert = packingUomIdItem.unitPriceConvert;
							break;
						}
					}
	    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'quantityConvert', quantityConvert);
	    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceBef', unitPriceConvert);
		    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceVAT', unitPriceConvert);
	    			
		    		var idStr = data.productId + "@" + oldValue;
		    		if (typeof(productPricesMap[idStr]) != "undefined") {
		    			var itemValue = productPricesMap[idStr];
		    			itemValue.selected = false;
		    			productPricesMap[idStr] = itemValue;
		    			
		    			// new item
		    			var idStrNew = data.productId + "@" + newValue;
			    		var itemValueNew = $.extend({}, itemValue);
		    			itemValueNew.productId = data.productId;
		    			itemValueNew.productCode = data.productCode;
		    			itemValueNew.quantityUomId = newValue;
		    			itemValueNew.quantityConvert = quantityConvert;
		    			itemValueNew.unitPrice = unitPriceConvert;
			    		itemValueNew.taxPercentage = data.taxPercentage;
			    		itemValueNew.features = data.features;
			    		itemValueNew.productName = data.productName;
			    		itemValueNew.selected = true;
				   		productPricesMap[idStrNew] = itemValueNew;
		    		}
		    	}
	    	}
    	});
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
		    		var idStr = dataItem.productId + "@" + dataItem.quantityUomId;
		    		if (typeof(productPricesMap[idStr]) != "undefined") {
		    			var itemValue = productPricesMap[idStr];
		    			itemValue.selected = false;
		    			productPricesMap[idStr] = itemValue;
		    		}
					
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
			// new, update item
	   		var idStr = data.productId + "@" + data.quantityUomId;
    		if (typeof(productPricesMap[idStr]) != "undefined") {
    			// delete row
    			var isFound = false;
    			var allRowTmp = $("#jqxgridProd").jqxGrid("getboundrows");
    			if (typeof(allRowTmp) != 'undefined') {
    				for (var j = 0; j < allRowTmp.length; j++) {
    					var itemTmp = allRowTmp[j];
    					if (itemTmp != window && itemTmp.productId == data.productId 
    							&& itemTmp.quantityUomId == data.quantityUomId && itemTmp.uid != null) {
    						//$("#jqxgridProd").jqxGrid("deleterow", itemTmp.uid);
    						var rowBoundIndex = $('#jqxgridProd').jqxGrid('getrowboundindexbyid', itemTmp.uid);
    						// update
    						$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', data.quantityUomId);
    						$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'quantityConvert', data.quantityConvert);
    						//$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPrice', data.unitPrice);
    		    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceBef', data.unitPriceBef);
    			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'unitPriceVAT', data.unitPriceVAT);
    			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', data.listPrice);
    			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'listPriceVAT', data.listPriceVAT);
    			    		isFound = true;
    						break;
    					}
    				}
    			}
    			if (!isFound) {
    				// add row
        			//$("#jqxgridProd").jqxGrid('addRow', null, itemValue, "first");
    				addItemToGridPopup(idStr, data);
    			} else {
    				// update data
        			var itemValue = productPricesMap[idStr];
        			itemValue.quantityUomId = data.quantityUomId;
        			itemValue.listPrice = data.listPrice;
        			itemValue.listPriceVAT = data.listPriceVAT;
        			itemValue.selected = true;
        			productPricesMap[idStr] = itemValue;
    			}
    		} else {
    			addItemToGridPopup(idStr, data);
    		}
		}
	};
	var addItemToGridPopup = function(idStr, data){
		// add row
		var itemValue = {};
		itemValue.productId = data.productId;
		itemValue.productCode = data.productCode;
		itemValue.quantityUomId = data.quantityUomId;
		itemValue.listPrice = data.listPrice;
		itemValue.listPriceVAT = data.listPriceVAT;
		itemValue.selected = true;
		itemValue.taxPercentage = data.taxPercentage;
		itemValue.features = data.features;
		itemValue.productName = data.productName;
		itemValue.quantityConvert = data.quantityConvert;
		itemValue.unitPrice = data.unitPrice;
		itemValue.unitPriceBef = data.unitPriceBef;
		itemValue.unitPriceVAT = data.unitPriceVAT;
		itemValue.packingUomIds = data.packingUomIds;
		
		$("#jqxgridProd").jqxGrid('addRow', null, itemValue, "last");
		productPricesMap[idStr] = $.extend({}, itemValue);
	};
	return {
		init: init,
		removeItemFromGrid: removeItemFromGrid,
		addItemsToGridPopup: addItemsToGridPopup,
	};
}());