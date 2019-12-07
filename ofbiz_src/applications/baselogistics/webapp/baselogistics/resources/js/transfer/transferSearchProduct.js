var ProductSearch = (function() {
	var comboboxSearch = null;
	var gridDestination = null;
	var editField = null;
	var searchUrl = null;
	var placeHolder = null;
	var param = {};
	var paramInput = {};
	var listProducts = null;
	var productSelectedMap = {};
	var messageItemNotFound = null;
	var init = function(comboboxSearchInput, gridDestinationInput, editFieldInput, searchUrlInput, listProductsReturn, paramInput, productSelectedMapInput, placeHolderInput, messageItemNotFoundInput) {
		comboboxSearch = comboboxSearchInput;
		gridDestination = gridDestinationInput;
		editField = editFieldInput;
		searchUrl = searchUrlInput;
		placeHolder = placeHolderInput;
		param = $.extend({}, paramInput); 
		paramInput = $.extend({}, paramInput); 
		listProducts = listProductsReturn;
		productSelectedMap = $.extend([{}], productSelectedMapInput);
		messageItemNotFound = messageItemNotFoundInput;
		initSearch();
	};
	
	function formatDataFuncItem(data, searchString){
		data.productToSearch = searchString;
        if (param != null){
			for(var keys in param){
				if (param[keys] != null || param[keys] != "" || param[keys] != "null" || param[keys] != undefined){
					data[keys] = param[keys];
				}
			}
        }
        return data;
	}
	var initSearch = function(){
		var configComboBoxSearchProduct = {
				datafields: [
		            { name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'requireAmount', type: 'string' },
					{ name: 'amount', type: 'number' },
					{ name: 'comment', type: 'string' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'number' },
					{ name: 'weightUomId', type: 'number' },
					{ name: 'unitCost', type: 'number' },
					{ name: 'uomId', type: 'string' },
			    ],
			    type: "POST",
			    root: listProducts,
			    url: searchUrl,
			    height: 20,
			    placeHolder: uiLabelMap.BPSearchProductToAdd + " (F1)",
		        messageItemNotFound: messageItemNotFound,
		        displayMember: "productCode",
		        valueMember: "productId",
		        formatDataFuncItem: formatDataFuncItem,
		        rendererFuncItem: function (item) {
		        	if (item != null) {
		           		var productName = item.productName;
		            	if (productName && productName.length > 65){
		            		productName = productName.substring(0, 65);
		                	productName = productName + '...';
		            	}
		           		var productCode = item.productCode;
		            	if (productCode && productCode.length > 20){
		            		productCode = productCode.substring(0, 20);
		                	productCode = productCode + '...';
		            	}
		            	var tableItem = '<div class="span12" style="width: 500px; height: 35px">'
		            	   + '<div class="span2" style="margin-left: -30px; width: 150px;">' + '[' + productCode + ']' + '</div>'
		            	   + '<div class="span6" style="width: 250px; margin-left: 10px; white-space: normal">' + item.productName + '</div>';
		                return tableItem;
		             }
		        	return "";
				},
				handlerSelectedItem: addOrIncreaseQuantity,
			};
			productSearch = new OlbComboBoxSearchRemote(comboboxSearch, configComboBoxSearchProduct);
	}
	
	var addOrIncreaseQuantity = function(newData){
		var productId = newData.productId;
		var quantityUomId = newData.quantityUomId;
		var transferItemSeqId;
		if (typeof(productSelectedMap[productId]) != "undefined") {
			var rows = gridDestination.jqxGrid('getrows');
			if (rows) {
				for (var x in rows){
					if (rows[x].productId == productId) {
						var rowBoundIndex = gridDestination.jqxGrid('getrowboundindexbyid', rows[x].uid);
						if (rowBoundIndex > -1) {
							gridDestination.jqxGrid('begincelledit', rowBoundIndex, editField);
						}
						return false;
					}
				};
			}
		} else {
			// add row
			var rows = gridDestination.jqxGrid('getrows');
			if (rows) {
				for (var x in rows){
					if (rows[x].productId == productId) {
						var rowBoundIndex = gridDestination.jqxGrid('getrowboundindexbyid', rows[x].uid);
						if (rowBoundIndex > -1) {
							gridDestination.jqxGrid('begincelledit', rowBoundIndex, editField);
						}
						return false;
					}
				};
			}
			
			for(var x of productSelectedMap){
				if(x.productId == productId){
					if(x.statusId == "TRANS_ITEM_CANCELLED"){
						transferItemSeqId = x.transferItemSeqId;
					}
				}
			}
			
			var rowData = {
				productId: productId,
				transferItemSeqId: transferItemSeqId, 
				productCode: newData.productCode,
				productName: newData.productName,
				quantityUomId : newData.quantityUomId,
				weightUomId: newData.weightUomId,
				requireAmount: newData.requireAmount,
				quantityOnHandTotal : newData.quantityOnHandTotal,
				description : newData.description,
				amount: newData.amount,
				uomId : newData.uomId,
				comment : newData.description,
				unitCost : newData.unitCost,
			};
			var rowPosition = "first";
			gridDestination.jqxGrid('addRow', null, rowData, rowPosition);
			gridDestination.jqxGrid('begincelledit', 0, editField);
			gridDestination.trigger("endaddrow", [rowPosition]);
			return false;
		}
	};
	
	return {
		init : init,
	}
}());