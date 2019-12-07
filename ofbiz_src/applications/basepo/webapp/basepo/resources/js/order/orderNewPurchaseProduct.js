$(function() {
	OlbOrderProduct.init();
});
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;
var OlbOrderProduct = (function() {
	var listProductAdds = [];
	var productAddOLBG = null;
	var productOLBG = null;
    var localData = [];
    var productSearch= null;
	var init = function() {
		isFirst = true;
		if (typeof productOrderMap === 'undefined') {
			productOrderMap = [];
		}
		if (orderId != null){
 			initProductGrid(false, false, localData);
 		} else {
 			initProductGrid(true, true, null);
 		}
		initProductAddGrid();
		initInput();
		initSearch();
		initEvents();
	};
	
	var initSearch = function(){
		var configComboBoxSearchProduct = {
				datafields: [
					{ name: 'productId', type: 'string' },
					{ name: 'supplierProductId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productPlanId', type: 'string' },
					{ name: 'customTimePeriodId', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'quantityParent', type: 'number' },
					{ name: 'quantityPurchase', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'quantityUomIds', type: 'string' },
					{ name: 'quantityUomIdParent', type: 'string' },
					{ name: 'lastPrice', type: 'number' },
					{ name: 'totalValue', type: 'number' },
					{ name: 'planQuantity', type: 'number' },
					{ name: 'orderedQuantity', type: 'number' },
					{ name: 'description', type: 'string' },
					{ name: 'productWeight', type: 'number' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'weightUomIds', type: 'string' },
					{ name: 'requireAmount', type: 'string' },
					{ name: 'itemComment', type: 'string' },
					{ name: 'uomId', type: 'string' },
					{ name: 'currencyUomId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'purchaseDiscontinuationDate', type: 'date', other: 'timestamp'},
					{ name: 'minimumOrderQuantity', type: 'number' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'amountOnHandTotal', type: 'number' }
			    ],
			    type: "POST",
			    root: "productsList",
			    url: "findProductsPurchase",
			    placeHolder: uiLabelMap.BPSearchProductToAdd + " (F1)",
		        messageItemNotFound: uiLabelMap.BPProductNotFoundOrHasBeenExpiredSupplied,
		        displayMember: "productCode",
		        valueMember: "productCode",
		        formatDataFuncItem: function(data, searchString) {
		            data.productToSearch = searchString;
		            data.supplierPartyId = $("#supplierId").val();
		            if ($("#originFacilityId").length > 0){
		            	data.facilityId = $("#originFacilityId").jqxDropDownList('val');
		            }
		            data.currencyUomId = $("#currencyUomId").jqxDropDownList('val');
		            return data;
		        	
		        },
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
			productSearch = new OlbComboBoxSearchRemote($("#jqxProductSearch"), configComboBoxSearchProduct);
	}
	
	var initInput = function (){
		$("#menuProduct").jqxMenu({ width: 220, autoOpenPopup: false, mode: "popup", theme: theme });
		$("#addProductPopup").jqxWindow({
 		    maxWidth: 1500, minWidth: 500, width: 1000, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 470, maxHeight: 670, resizable: false, cancelButton: $("#addProductCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
 		});
	};
	
	var dataFieldProduct = [{ name: 'productId', type: 'string' },
	    					{ name: 'supplierProductId', type: 'string' },
	    					{ name: 'productCode', type: 'string' },
	    					{ name: 'productPlanId', type: 'string' },
	    					{ name: 'customTimePeriodId', type: 'string' },
	    					{ name: 'quantity', type: 'number' },
	    					{ name: 'quantityParent', type: 'number' },
	    					{ name: 'quantityPurchase', type: 'number' },
	    					{ name: 'quantityUomId', type: 'string' },
	    					{ name: 'quantityUomIds', type: 'array' },
	    					{ name: 'quantityUomIdParent', type: 'string' },
	    					{ name: 'lastPrice', type: 'number' },
	    					{ name: 'totalValue', type: 'number' },
	    					{ name: 'planQuantity', type: 'number' },
	    					{ name: 'orderedQuantity', type: 'number' },
	    					{ name: 'quantityOnHandTotal', type: 'number' },
	    					{ name: 'amountOnHandTotal', type: 'number' },
	    					{ name: 'availableToPromiseTotal', type: 'number' },
	    					{ name: 'description', type: 'string' },
	    					{ name: 'productWeight', type: 'number' },
	    					{ name: 'convertNumber', type: 'number' },
	    					{ name: 'weightUomId', type: 'string' },
	    					{ name: 'purchaseUomId', type: 'string' },
	    					{ name: 'weightUomIds', type: 'array' },
	    					{ name: 'requireAmount', type: 'string' },
	    					{ name: 'uomId', type: 'string' },
	    					{ name: 'currencyUomId', type: 'string' },
	    					{ name: 'itemComment', type: 'string' },
	    					{ name: 'partyId', type: 'string' },
	    					{ name: 'productName', type: 'string' },
	    					{ name: 'quantityReceived', type: 'number' },
	    					{ name: 'purchaseDiscontinuationDate', type: 'date', other: 'timestamp'},
	    					{ name: 'minimumOrderQuantity', type: 'number' }];
	
	var columnProducts = [
				{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				{ datafield: 'productId', width: 80, editable: false, pinned: true,hidden: true, cellclassname: cellclassname },
				{ text: uiLabelMap.BPOProductId, datafield: 'productCode', width: 100, editable: false, pinned: true, cellclassname: cellclassname },
				{ text: uiLabelMap.BPOProductName, datafield: 'productName', minwidth: 150, editable: false, pinned: true, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
 						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
 						var checkExp = false;
 						if (rowData['purchaseDiscontinuationDate'] != undefined && rowData['purchaseDiscontinuationDate'] != null) {
 							var now = new Date();
 							var ex = new Date(rowData['purchaseDiscontinuationDate']);
 							if (ex <= now){
 								return '<span>' + value + ' (' + uiLabelMap.BSDiscountinuePurchase + ' ' + jOlbUtil.dateTime.formatFullDate(ex) + ')</span>';
 					        }
 						}
 						return '<span>' + value +'</span>';
 					},
				},
				{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', width: 100, sortable: false, editable: false, filterable: false, cellsalign: 'right', cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') {
							value = data.amountOnHandTotal;
						} 
						var description = formatnumber(value);
						if (data.requireAmount && data.requireAmount == 'Y') {
							description = formatnumber(value) + ' (' + getUomDesc(data.weightUomId) +')';
						} else {
							description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
						}
						return '<span class=\"align-right\">' + description +'</span>';
					}, 
				},
				{ text: uiLabelMap.MOQ, datafield: 'minimumOrderQuantity', width: 100, sortable: false, editable: false, filterable: false, cellsalign: 'right', cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') {
							description = formatnumber(value) + ' (' + getUomDesc(data.weightUomId) +')';
						} else {
							description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
						}
						return '<span class=\"align-right\">' + description +'</span>';
					}, 
				},
				{text: uiLabelMap.BLPackingForm, sortable: false, dataField: 'convertNumber', editable: false, width: 90, columntype: 'number',  filterable:false, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						value = getConvertNumber(rowData);
						return '<span class=\"align-right\">' + formatnumber(value) +'</span>';
					}, 
				},
				
				{text: uiLabelMap.BSPurchaseUomId, sortable: false, dataField: 'uomId', width: 100, columntype: 'dropdownlist',  filterable:false, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (item.requireAmount && 'Y' == item.requireAmount) {
								if (item.weightUomId) {
									value = item.weightUomId;
								}
							} else {
								if (item.quantityUomId) {
									value = item.quantityUomId;
								}
							}
						} 
						if (value) {
							var desc = getUomDesc(value);
							return '<span style=\"text-align: right\">' + desc +'</span>';
						} 
						return value;
					}, 
					cellbeginedit: function (row, datafield, columntype) {
						var rowsdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (rowsdata){
							var item = productOrderMap[rowsdata.productId];
							if (item){
								if (item.quantityReceived && item.quantityReceived > 0) {
									return false;
								}
							}
							var requireAmount = rowsdata.requireAmount;
							if (requireAmount && 'Y' == requireAmount){
								return false;
							}
							
							if (rowsdata['purchaseDiscontinuationDate'] != undefined && rowsdata['purchaseDiscontinuationDate'] != null) {
								var now = new Date();
								var ex = new Date(rowsdata['purchaseDiscontinuationDate']);
								if (ex <= now){
						        	return false;
						        }
						    }
						    return true;
					    }
					    return true;
					},	
				 	initeditor: function (row, cellvalue, editor) {
						var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount){
							var weightUomData = new Array();
							var itemSelected = data['uomId'];
							var weightUomIdArray = data['weightUomIds'];
							for (var i = 0; i < weightUomIdArray.length; i++) {
								var obj = weightUomIdArray[i];
								var uomId = obj.uomId;
								var row = {};
								if (uomId === undefined || uomId === '' || uomId === null) {
									row['description'] = '' + uomId;
								} else {
									row['description'] = '' + getUomDesc(uomId);
								}
								row['uomId'] = '' + uomId;
								weightUomData[i] = row;
							}
					 		var sourceDataUomWeight = {
				                localdata: weightUomData,
				                datatype: 'array'
				            };
				            var dataAdapterWeight = new $.jqx.dataAdapter(sourceDataUomWeight);
				            editor.jqxDropDownList({source: dataAdapterWeight, displayMember: 'description', valueMember: 'uomId'});
				            if (!itemSelected){
				            	var item = productOrderMap[data.productId];
								if (item) {
									if (item.quantityUomId) {
										itemSelected = item.quantityUomId;
									}
								} 
				            }
				            if (itemSelected){
								editor.jqxDropDownList('selectItem', itemSelected);					            
				            }
						} else {
							var packingUomData = new Array();
							var itemSelected = data['uomId'];
							var packingUomIdArray = data['quantityUomIds'];
							for (var i = 0; i < packingUomIdArray.length; i++) {
								var obj = packingUomIdArray[i];
								var quantityUomId = obj.quantityUomId;
								var row = {};
								if (quantityUomId === undefined || quantityUomId === '' || quantityUomId === null) {
									row['description'] = '' + quantityUomId;
								} else {
									row['description'] = '' + getUomDesc(quantityUomId);
								}
								row['quantityUomId'] = '' + quantityUomId;
								packingUomData[i] = row;
							}
					 		var sourceDataPacking = {
				                localdata: packingUomData,
				                datatype: 'array'
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({source: dataAdapterPacking, displayMember: 'description', valueMember: 'quantityUomId'});
				        	var item = productOrderMap[data.productId];
							if (item) {
								if (item.quantityUomId) {
									itemSelected = item.quantityUomId;
								}
							} 
							if (itemSelected){
				            	editor.jqxDropDownList('selectItem', itemSelected);
				            }
				        }
				  	}
				},
				{ text: uiLabelMap.BLPurchaseQtySum, datafield: 'quantityPurchase', sortable: false,  width: '100', editable: true, filterable: false, sortable: false, cellclassname: cellclassname,
					rendered: function (element) {
				      	$(element).jqxTooltip({ position: 'mouse', content: uiLabelMap.ByPurchaseQuantityUom});
				  	},
					cellsalign: 'right', columntype: 'numberinput',
					cellsrenderer: function(row, column, value) {
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (item.quantityPurchase) {
								value = item.quantityPurchase;
							}
						} 
						var checkExp = false;
						if (rowData['purchaseDiscontinuationDate'] != undefined && rowData['purchaseDiscontinuationDate'] != null) {
							var now = new Date();
							var ex = new Date(rowData['purchaseDiscontinuationDate']);
							if (ex <= now){
					        	checkExp = true;
					        }
					    }
						if (value > 0) {
							if (checkExp && (orderId == null || orderId == undefined)){
								return '<span style=\"text-align: right\">'+uiLabelMap.BSDiscountinuePurchase+'</span>';	
							} else {
								return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
							}
						} else {
							if (checkExp && (orderId == null || orderId == undefined)){
								return '<span style=\"text-align: right\">'+ uiLabelMap.BSDiscountinuePurchase +'</span>';	
							} else {
								return '<span style=\"text-align: right\"></span>';
							}
						}
					}, initeditor: function (row, cellvalue, editor) {
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if ('Y' == rowData.requireAmount) {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						} else {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
						}
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (item.quantityPurchase) {
								var u = item.quantityPurchase;
								if ('vi' == locale) {
									u = u.toString().replace('.', ',');
								}
								editor.jqxNumberInput('val', u);
							}
						} 
					}, validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: uiLabelMap.BPOCheckGreaterThan };
						}
//						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', cell.row);
//						if (rowData.minimumOrderQuantity && rowData.minimumOrderQuantity > 0){
//							var convertNumber = getConvertNumber(rowData);
//							if ((convertNumber*value) < rowData.minimumOrderQuantity){
//								return { result: false, message: uiLabelMap.BPORestrictMOQ };
//							}
//						}
						return true;
					}, cellbeginedit: function (row, datafield, columntype) {
						if (orderId != null){
 							return true;
 						}
						var rowsdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						if (rowsdata){
							if (rowsdata['purchaseDiscontinuationDate'] != undefined && rowsdata['purchaseDiscontinuationDate'] != null) {
								var now = new Date();
								var ex = new Date(rowsdata['purchaseDiscontinuationDate']);
								if (ex <= now){
						        	return false;
						        }
						        return true;
						    }
						    return true;
						}
						return true;
				    }
				},
				{text: uiLabelMap.BLQuantityEATotal, datafield: 'quantity', width: 100, editable: false, sortable: false, filterable: false, cellclassname: cellclassname,
					rendered: function (element) {
				      	$(element).jqxTooltip({ position: 'mouse', content: uiLabelMap.ByBaseQuantityUom});
				  	},
					cellsalign: 'right', columntype: 'numberinput', sortable: false, hidden: false,
					cellsrenderer: function(row, column, value){
						var rowdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowdata.productId];
						if (item) {
							value = item.quantity;
							if (value === null || value === undefined || value === '') {
								var requireAmount = rowdata.requireAmount;
								if (requireAmount && 'Y' == requireAmount){
									var weightUomIdArray = rowdata['weightUomIds'];
									for (var i = 0; i < weightUomIdArray.length; i++) {
										var obj = weightUomIdArray[i];
										var uomId = obj.uomId;
										if (uomId == rowdata.uomId) {
											value = obj.convertNumber;
											break;
										}
									}
								} else {
									var qtyUomIdArray = rowdata['quantityUomIds'];
									for (var i = 0; i < qtyUomIdArray.length; i++) {
										var obj = qtyUomIdArray[i];
										var uomId = obj.quantityUomId;
										if (uomId == rowdata.uomId) {
											value = obj.convertNumber;
											break;
										}
									}
								}
							}
							return '<span id=\"'+id+'\" style=\"text-align: right\">' + formatnumber(value) +'</span>';
						} else {
							value = 0;
							return '<span id=\"'+id+'\" style=\"text-align: right\">' + formatnumber(value) +'</span>';
						}
					}
				},
				{ text: uiLabelMap.UnitPrice, datafield: 'lastPrice', sortable: false, width: 100, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', cellclassname: cellclassname,
					cellbeginedit: function (row, datafield, columntype) {
						var rowsdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowsdata.productId];
						if (item) {
							if (item.quantityReceived && item.quantityReceived > 0) {
								return false;
							}
						}
						if (rowsdata['purchaseDiscontinuationDate'] != undefined && rowsdata['purchaseDiscontinuationDate'] != null) {
							var now = new Date();
							var ex = new Date(rowsdata['purchaseDiscontinuationDate']);
							if (ex <= now){
					        	return false;
					        }
					    }
					},	
					cellsrenderer: function(row, column, value){
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (typeof item.lastPrice == 'number') {
								value = parseFloat(item.lastPrice);
							} else if (typeof item.lastPrice == 'string'){
								value = parseFloat(item.lastPrice.replace(',', '.'));
							}
						}
						if (value) {
							return '<span style=\"text-align: right\" title=\"'+formatnumber(value, null, 3)+'\">' + formatnumber(value, null, 3) +'</span>';
						}
					},
					initeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 3 });
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (item.lastPrice) {
								var price = item.lastPrice;
								if (locale == 'vi'){
									price = price.toString();
									price = price.replace(',','');
									price = price.replace('.',',');
								}
								editor.jqxNumberInput('val', price);
							}
						} 
					}
				},
				{ text: uiLabelMap.BPOTotal, datafield: 'totalValue', sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', sortable: false, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value){
						var rowsdata = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowsdata.productId];
						if (item != undefined){
							var lastPrice = 0;
							if (item.lastPrice){
							 lastPrice = parseFloat(item.lastPrice.toString().replace(',', '.'));
							}
							var quantity = item.quantityPurchase;
							value = lastPrice*parseFloat(quantity);
							if (value) {
								return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
							} else {
								return '<span style=\"text-align: right\"></span>';
							}
						}
						return '<span style=\"text-align: right\"></span>';
					},
				},
				{ text: uiLabelMap.Note, datafield: 'itemComment', sortable: false, width: 100, editable: true, filterable: false, cellsalign: 'left', sortable: false, cellclassname: cellclassname,
					cellsrenderer: function(row, column, value) {
						var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
						var item = productOrderMap[rowData.productId];
						if (item) {
							if (item.itemComment) {
								value = item.itemComment;
							}
						} 
						if (value) {
							return '<span>' + value +'</span>';
						} 
						return value;
					}, 
					initeditor: function (row, cellvalue, editor) {
						editor.jqxInput();
						if (!cellvalue) {
							var rowData = $('#jqxgridProduct').jqxGrid('getrowdata', row);
							var item = productOrderMap[rowData.productId];
							if (item) {
								if (item.itemComment) {
									cellvalue = item.itemComment;
								}
							} 
						}
						editor.jqxInput('val', unescapeHTML(cellvalue));
					},
				},
       ];
	
	var initProductGrid = function(useUrl, virtualmode, localdata){
		var configProductAdd = {
			datafields: dataFieldProduct,
			columns: columnProducts,
			width: '100%',
			height: 'auto',
			sortable: true,
			editable: true,
			filterable: true,
			pageable: true,
			showfilterrow: true,
			useUtilFunc: false,
			useUrl: useUrl,
			url: '',
			groupable: false,
			showgroupsheader: false,
			showaggregates: false,
			showstatusbar: false,
			virtualmode:true,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			showtoolbar:false,
			columnsresize: true,
			isSaveFormData: true,
			formData: "filterObjData",
			selectionmode: "singlecell",
			editmode: "click",
			bindresize: true,
			virtualmode: virtualmode,
			localdata: localdata,
			pagesize: 15,
		};
		productOLBG = new OlbGrid($("#jqxgridProduct"), null, configProductAdd, []);
	};
	
	var getDataField = function(){
		var datafield = [{ name: 'productId', type: 'string' },
							{ name: 'supplierProductId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productPlanId', type: 'string' },
							{ name: 'customTimePeriodId', type: 'string' },
							{ name: 'quantity', type: 'number' },
							{ name: 'quantityParent', type: 'number' },
							{ name: 'quantityPurchase', type: 'number' },
							{ name: 'quantityUomId', type: 'string' },
							{ name: 'quantityUomIds', type: 'string' },
							{ name: 'quantityUomIdParent', type: 'string' },
							{ name: 'lastPrice', type: 'number' },
							{ name: 'totalValue', type: 'number' },
							{ name: 'planQuantity', type: 'number' },
							{ name: 'orderedQuantity', type: 'number' },
							{ name: 'quantityOnHandTotal', type: 'number' },
							{ name: 'amountOnHandTotal', type: 'number' },
							{ name: 'availableToPromiseTotal', type: 'number' },
							{ name: 'description', type: 'string' },
							{ name: 'productWeight', type: 'number' },
							{ name: 'convertNumber', type: 'number' },
							{ name: 'weightUomId', type: 'string' },
							{ name: 'purchaseUomId', type: 'string' },
							{ name: 'weightUomIds', type: 'string' },
							{ name: 'requireAmount', type: 'string' },
							{ name: 'uomId', type: 'string' },
							{ name: 'currencyUomId', type: 'string' },
							{ name: 'itemComment', type: 'string' },
							{ name: 'partyId', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'purchaseDiscontinuationDate', type: 'date', other: 'timestamp'},
							{ name: 'minimumOrderQuantity', type: 'number' }];
		return datafield;
	};
	
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable:false,},
			{ text: uiLabelMap.QOH, datafield: 'quantityOnHandTotal', sortable: true, width: 100, editable: false, filterable: false, cellsalign: 'right', cellclassname: cellclassnameAdd,
				cellsrenderer: function(row, column, value) {
					var data = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') {
						value = data.amountOnHandTotal;
					} 
					var description = formatnumber(value);
					if (data.requireAmount && data.requireAmount == 'Y') {
						description = formatnumber(value) + ' (' + getUomDesc(data.weightUomId) +')';
					} else {
						description = formatnumber(value) + ' (' + getUomDesc(data.quantityUomId)+')';
					}
					return '<span class=\"align-right\">' + description +'</span>';
				}, 
			},
			{editable: false, text: uiLabelMap.BSPurchaseUomId, sortable: false, dataField: 'uomId', width: 100, columntype: 'dropdownlist',  filterable:false, cellclassname: cellclassnameAdd,
				cellsrenderer: function(row, column, value) {
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					
					if (!value) {
						if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
							if (rowData.weightUomId) {
								value = rowData.weightUomId;
							}
						} else {
							if (rowData.quantityUomId) {
								value = rowData.quantityUomId;
							}
						}
					} 
					if (value) {
						var desc = getUomDesc(value);
						return '<span style=\"text-align: right\">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.BLPurchaseQtySum, datafield: 'quantityPurchase', sortable: false,  width: '100', editable: true, filterable: false, sortable: false, cellclassname: cellclassnameAdd,
				rendered: function (element) {
                  	$(element).jqxTooltip({ position: 'mouse', content: uiLabelMap.ByPurchaseQuantityUom});
              	},
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					var checkExp = false;
					if (rowData['purchaseDiscontinuationDate'] != undefined && rowData['purchaseDiscontinuationDate'] != null) {
						var now = new Date();
						var ex = new Date(rowData['purchaseDiscontinuationDate']);
						if (ex <= now){
				        	checkExp = true;
				        }
				    }
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
				    if (listProductAdds.length > 0){
				    	$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.quantityPurchase;
			   					return false;
			   				}
			   			});
				    }
					if (value > 0) {
						if (checkExp){
							return '<span style=\"text-align: right\" title=\"'+ uiLabelMap.BSDiscountinuePurchase+'\">'+uiLabelMap.BSDiscountinuePurchase+'</span>';	
						} else {
							return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
						}
					} else {
						if (checkExp){
							return '<span style=\"text-align: right\" title=\"'+ uiLabelMap.BSDiscountinuePurchase + '\">' + uiLabelMap.BSDiscountinuePurchase +  '</span>'; 	
						} else {
							return '<span style=\"text-align: right\"></span>';
						}
					}
					return '<span style=\"text-align: right\"></span>';
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductAdds.length > 0){
					    	$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.quantityPurchase;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue) {
						var u = cellvalue.toString().replace('.', ',');
						editor.jqxNumberInput('val', u);
					}
				}, validation: function (cell, value) {
					if (value < 0) {
						return { result: false, message: uiLabelMap.BPOCheckGreaterThan };
					}
					return true;
				}, cellbeginedit: function (row, datafield, columntype) {
					var rowsdata = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (rowsdata){
						if (rowsdata['purchaseDiscontinuationDate'] != undefined && rowsdata['purchaseDiscontinuationDate'] != null) {
							var now = new Date();
							var ex = new Date(rowsdata['purchaseDiscontinuationDate']);
							if (ex <= now){
					        	return false;
					        }
					    }
					}
					return true;
			    }
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'lastPrice', sortable: false, width: 100, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', cellclassname: cellclassnameAdd,
				cellbeginedit: function (row, datafield, columntype) {
					var rowsdata = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					var item = productOrderMap[rowsdata.productId];
					if (item) {
						if (item.quantityReceived && item.quantityReceived > 0) {
							return false;
						}
					}
					if (rowsdata['purchaseDiscontinuationDate'] != undefined && rowsdata['purchaseDiscontinuationDate'] != null) {
						var now = new Date();
						var ex = new Date(rowsdata['purchaseDiscontinuationDate']);
						if (ex <= now){
				        	return false;
				        }
				    }
				},	
				cellsrenderer: function(row, column, value){
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (listProductAdds.length > 0){
				    	$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.lastPrice;
			   					return false;
			   				}
			   			});
				    }
					if (value) {
						if (typeof value == 'number') {
							value = parseFloat(value);
						} else if (typeof value == 'string'){
							value = parseFloat(value.replace(',', '.'));
						}
					}
					if (value) {
						return '<span style=\"text-align: right\" title=\"'+formatnumber(value, null, 3)+'\">' + formatnumber(value, null, 3) +'</span>';
					}
				},
				initeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 3 });
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (!cellvalue){
						if (listProductAdds.length > 0){
					    	$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.lastPrice;
				   					return false;
				   				}
				   			});
					    }
					}
					if (cellvalue) {
						var price = cellvalue;
						if (locale == 'vi'){
							price = price.toString();
							price = price.replace(',','');
							price = price.replace('.',',');
						}
						editor.jqxNumberInput('val', price);
					} 
				}
			},
			{ text: uiLabelMap.Note, datafield: 'itemComment', sortable: false, width: 150, editable: true, filterable: false, cellsalign: 'left', sortable: false, cellclassname: cellclassnameAdd,
				cellsrenderer: function(row, column, value) {
					var rowData = $('#jqxgridProductAdd').jqxGrid('getrowdata', row);
					if (value) {
						return '<span>' + value +'</span>';
					} 
					return value;
				}, 
				initeditor: function (row, cellvalue, editor) {
					editor.jqxInput();
					if (!cellvalue) {
						cellvalue = unescapeHTML(cellvalue);
					}
					editor.jqxInput('val', cellvalue);
				},
				validation: function (cell, value) {
					if (checkSpecialCharacters(value)){
						return { result: false, message: uiLabelMap.validContainSpecialCharacter };
					}
					return true;
				}
			},
        ];
		return columns;
	};
	
	var initProductAddGrid = function(){
		var configProductAdd = {
			datafields: getDataField(),
			columns: getColumns($("#jqxgridProductAdd")),
			width: '100%',
			height: 'auto',
			sortable: true,
			editable: true,
			filterable: true,
			pageable: true,
			showfilterrow: true,
			useUtilFunc: false,
			useUrl: true,
			url: '',
			groupable: false,
			showgroupsheader: false,
			showaggregates: false,
			showstatusbar: false,
			virtualmode:true,
			showdefaultloadelement:true,
			autoshowloadelement:true,
			showtoolbar:false,
			columnsresize: true,
			isSaveFormData: true,
			formData: "filterObjData",
			selectionmode: "checkbox",
			bindresize: true,
			pagesize: 10,
		};
		productAddOLBG = new OlbGrid($("#jqxgridProductAdd"), null, configProductAdd, []);
	};
	
	var addOrIncreaseQuantity = function(newData){
		var productId = newData.productId;
		var quantityUomId = newData.quantityUomId;
		if (typeof(productOrderMap[productId]) != "undefined") {
			data = newData;
		} else {
			// add row
			var quantityUomId = newData.quantityUomId;
 			var quantityUomIds = newData.quantityUomIds;
 			var convertNumber = 1;
 			if (newData.requireAmount && newData.requireAmount == 'Y'){
 				quantityUomId = newData.weightUomId;
 				var weightUomIds = newData.weightUomIds;
 				for (x in weightUomIds){
 					if (weightUomIds[x].uomId == quantityUomId){
 						convertNumber = weightUomIds[x].convertNumber;
 					}
 				}
 			} else {
 				for (x in quantityUomIds){
 					if (quantityUomIds[x].uomId == quantityUomId){
 						convertNumber = quantityUomIds[x].convertNumber;
 					}
 				}
 			}
			var rowData = {
				productId: productId,
				productCode: newData.productCode,
				productName: newData.productName,
				weightUomId: newData.weightUomId,
				quantityUomId: quantityUomId,
 				uomId: quantityUomId,
				quantity: 0,
				quantityPurchase: 0,
				weightUomIds: newData.weightUomIds,
				quantityUomIds: newData.quantityUomIds,
				lastPrice: newData.lastPrice,
				requireAmount: newData.requireAmount,
				currencyUomId: newData.currencyUomId,
				partyId: newData.partyId,
				convertNumber: convertNumber,
 				quantityOnHandTotal: newData.quantityOnHandTotal,
 				amountOnHandTotal: newData.amountOnHandTotal
			};
			data = rowData;
		}
		if (data){
			$.each(listOrderItemData, function(i){
				var olb = listOrderItemData[i];
				if (olb.productId == productId ){
					listOrderItemData.splice(i,1);
					return false;
				}
			});
			listOrderItemData.unshift(data);
			OlbGridUtil.updateSource($("#jqxgridProduct"), null, listOrderItemData, null);
			setTimeout(function(){
				$('#jqxgridProduct').jqxGrid('begincelledit', 0, "quantityPurchase");
				$('#jqxgridProduct').jqxGrid('clearselection');
			}, 200);
		}
		return true;
	};
	
	var initEvents = function() {
		
		$("#btnProductToAdd").click(function (event) {
			
			var facilityId = $("#originFacilityId").jqxDropDownList('val');
			var partyId = $("#supplierId").val();
			var currencyUomId = $("#currencyUomId").jqxDropDownList('val');
			
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId=" + partyId + "&currencyUomId=" + currencyUomId + "&facilityId=" +facilityId);
			
			$("#addProductPopup").jqxWindow('open');
			$("#jqxgridProductAdd").jqxGrid("updatebounddata");
		});
		
		
		$("#addProductSave").on("click", function (event) {
			var rowPosition = "first";
			if (listProductAdds.length > 0){
				for (var x in listProductAdds){
					var data = listProductAdds[x];
					var check = false;
					var productId = data.productId;
					var item = productOrderMap[productId];
					
					var uomIdArray = data['quantityUomIds'];
					var requireAmount = data.requireAmount;
					if (requireAmount && 'Y' == requireAmount){
						uomIdArray = data['weightUomIds'];
		    		}
					var uomId = data.uomId;
					var quantity = 0;
					for (var i = 0; i < uomIdArray.length; i++) {
						var obj = uomIdArray[i];
						if (requireAmount && 'Y' == requireAmount){
							var uomIdTmp = obj.uomId;
							var convert = obj.convertNumber;
							if (uomId == uomIdTmp){
								quantity = convert*data.quantityPurchase;
								break;
							}
						} else {
							var quantityUomIdTmp = obj.quantityUomId;
							var convert = obj.convertNumber;
							if (uomId == quantityUomIdTmp){
								quantity = convert*data.quantityPurchase;
								break;
							}
						}
					}
					
					if (item){
						var x = item.quantity;
						var y = item.quantityPurchase;
						if (typeof x === 'string') {
					    	x.replace(',', '.');
					    	x = parseFloat(x, 3, null);
					    }
						if (typeof y === 'string') {
							y.replace(',', '.');
							y = parseFloat(y, 3, null);
						}
						item.quantity = x + quantity;
						item.quantityPurchase = y + data.quantityPurchase;
						item.itemComment = data.itemComment;
						var curRows = $("#jqxgridProduct").jqxGrid('getrows');
						for (var z in curRows){
							var t = curRows[z];
							if (t.productId == productId){
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "quantityPurchase", item.quantityPurchase);
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "lastPrice", item.lastPrice);
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "itemComment", item.itemComment);
							}
						}
					} else {
						
						item = {};
						item.itemComment = data.itemComment;
						item.lastPrice = data.lastPrice;
						item.productId = data.productId;
						item.quantity = quantity;
						item.quantityPurchase = data.quantityPurchase;
						if (requireAmount && 'Y' == requireAmount){
							item.weightUomId = data.uomId;
						} else {
							item.quantityUomId = data.uomId;
						}
						productOrderMap[productId] = item;
						
						listOrderItemData.push(data);
					}
				}
			}
			OlbGridUtil.updateSource($("#jqxgridProduct"), null, listOrderItemData, false);
 			$("#jqxgridProduct").jqxGrid('updatebounddata');
			$("#addProductPopup").jqxWindow('close');
		});
		
		$("#addProductPopup").on("close", function (event) {
			listProductAdds = [];
		});
		
		$("#jqxgridProductAdd").on('rowselect', function (event) {
	    	var args = event.args;
	    	var data = args.row;
	    	if (data.quantityPurchase > 0){
	    		$.each(listProductAdds, function(i){
	   				var olb = listProductAdds[i];
	   				if (olb.productId == data.productId ){
	   					listProductAdds.splice(i,1);
	   					return false;
	   				}
	   			});
	    		listProductAdds.push(data);
	    	}
	    });
	    $("#jqxgridProductAdd").on('rowunselect', function (event) {
	    	var args = event.args;
	    	var data = args.row;
	    	if (data){
	    		$.each(listProductAdds, function(i){
	   				var olb = listProductAdds[i];
	   				if (olb.productId == data.productId ){
	   					listProductAdds.splice(i,1);
	   					return false;
	   				}
	   			});
	    	}
	    });
	    
	    $("#jqxgridProductAdd").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantityPurchase'){
					if (value != 0){
						var item = $.extend({}, rowData);
						item.quantityPurchase = value;
						$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductAdds.splice(i,1);
			   					return false;
			   				}
			   			});
						listProductAdds.push(item);
						$("#jqxgridProductAdd").jqxGrid('selectrow', rowBoundIndex);
					} else {
						$("#jqxgridProductAdd").jqxGrid('unselectrow', rowBoundIndex);
					}
				} else if(dataField == 'lastPrice'){
					if (rowData.quantityPurchase > 0){
						var item = $.extend({}, rowData);
						item.lastPrice = value;
						$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductAdds.splice(i,1);
			   					return false;
			   				}
			   			});
						listProductAdds.push(item);
					}
				}  else if (dataField == 'itemComment'){
					if (rowData.quantityPurchase > 0 && value != '' && value != null && value != undefined & value != 'null'){
						if (listProductAdds.length > 0){
							
							$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					listProductAdds[i].itemComment = value;
				   					return false;
				   				}
				   			});
						}
					}
				}
			}
		});
	    
	    $("#jqxgridProductAdd").on("bindingcomplete", function(event) {
	    	var args = event.args;
	    	var rows = $("#jqxgridProductAdd").jqxGrid('getrows');
	    	if (listProductAdds.length > 0){
	    		for (var x in rows){
	    			var check = false;
	    			for (var y in listProductAdds){
	    				if (rows[x].productId === listProductAdds[y].productId) {
	    					check = true;
	    					break;
	    				}
		    		}
	    			var index = $('#jqxgridProductAdd').jqxGrid('getrowboundindexbyid',rows[x].uid);
	    			if (check){
    					$("#jqxgridProductAdd").jqxGrid('selectrow', index);
    				} else {
						$("#jqxgridProductAdd").jqxGrid('unselectrow', index);
					}
	    		}
	    	}
			
	    });
	    
		$("#menuProduct").on("itemclick", function (event) {
			var index = _.last($("#jqxgridProduct").jqxGrid("selectedrowindexes"));
			var rowData = $("#jqxgridProduct").jqxGrid("getRowData", index);
			var id = rowData.uid;
			var uomIdArray = rowData['quantityUomIds'];
			var requireAmount = rowData.requireAmount;
			if (requireAmount && 'Y' == requireAmount){
				uomIdArray = rowData['weightUomIds'];
    		}
			var uom = rowData.uomId;
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.BPGetBasePrice) {
				var productId = rowData.productId;
				var item = productOrderMap[productId];
				var value = rowData.quantityPurchase;
				if (!value && item){
					value = item.quantityPurchase;
				}
		    	if (value !== undefined && value !== null && value != '' && value > 0){
		    		qty = value;
		    		if (item) {
		    			uom = item.quantityUomId;
		    			if (requireAmount && 'Y' == requireAmount){
		    				uom = item.weightUomId;
		    			}
		    			if (rowData.itemComment) {
	    					item.itemComment = rowData.itemComment;
	    				}
			    		if (typeof oldvalue != 'undefined' && oldvalue != value) {
			    			OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
			    		} else if (orderId != 'undefined' && value > 0) {
			    			OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
			    		}
		    		} else {
		    			OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
		    			item = productOrderMap[productId];
		    			if (item) {
		    				if (rowData.itemComment) {
		    					item.itemComment = rowData.itemComment;
		    				}
		    			}
		    		}
		    	}
			} else if (tmpStr == uiLabelMap.BPReset) {
				if (orderId != null && orderId != undefined) {
					for (var x in listOrderItemInit){
						if (listOrderItemInit[x].productId == rowData.productId){
							var tmp = $.extend({}, listOrderItemInit[x]);
							productOrderMap[rowData.productId] = tmp;
							$("#jqxgridProduct").jqxGrid('setcellvaluebyid', id, "quantityPurchase", listOrderItemInit[x].quantityPurchase);
							$("#jqxgridProduct").jqxGrid('setcellvaluebyid', id, "lastPrice", listOrderItemInit[x].lastPrice);
							$("#jqxgridProduct").jqxGrid('setcellvaluebyid', id, "itemComment", listOrderItemInit[x].itemComment);
						}
					}
				} else {
					if (productOrderMap[rowData.productId]) {
						delete productOrderMap[rowData.productId]; 
						$("#jqxgridProduct").jqxGrid('setcellvaluebyid', id, "quantityPurchase", 0);
						$("#jqxgridProduct").jqxGrid('setcellvaluebyid', id, "itemComment", null);
						OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, 1, id, true, uomIdArray, requireAmount);
					}
				}
			} 
		});
		
		var currentValue = new Object();
		var currentFacility = new Object();
		$('#facility').on('close', function (event) {
			if (facilitySelected != null && supplierSelected != null){
				var value = $("#currencyUomId").jqxDropDownList('val');
				if (value) {
					var partyId = supplierSelected.partyId;
					var listFacilityIds = [];
					facilityId = facilitySelected.facilityId;
					if (currentValue.partyId != partyId || currentValue.currencyUomId != value || currentFacility.facilityId != facilityId) {
						if (orderId != null && orderId != undefined) {
							if (typeof listProductIds != 'undefined' && listProductIds.length > 0){
								$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
									+ partyId + "&currencyUomId=" + value + "&orderId=" + orderId + "&facilityId=" +facilityId;
							} else {
								$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
									+ partyId + "&currencyUomId=" + value + "&facilityId=" +facilityId;
							}
						} else {
							if (facilityId){
								var url = null;
								if (productPlanId != null && customTimePeriodId != null){
									url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
										+ partyId + "&currencyUomId=" + value + "&facilityId=" +facilityId
										+ "&productPlanId=" + productPlanId + "&customTimePeriodId=" + customTimePeriodId;
								} else {
									url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
										+ partyId + "&currencyUomId=" + value + "&facilityId=" +facilityId;
								}
								$("#jqxgridProduct").jqxGrid("source")._source.url = url;
							} else {
								$("#jqxgridProduct").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
									+ partyId + "&currencyUomId=" + value;
							}
							$("#jqxgridProduct").jqxGrid("updatebounddata");
						}
					}
					currentValue.partyId = partyId;
					currentValue.currencyUomId = value;
					currentFacility.facilityId = facilityId;
				}
			} 
		});
		
		$("#jqxgridProduct").on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				var productId = rowData.productId;
				var item = productOrderMap[productId];
				var uomIdArray = rowData['quantityUomIds'];
				var requireAmount = rowData.requireAmount;
				if (requireAmount && 'Y' == requireAmount){
					uomIdArray = rowData['weightUomIds'];
	    		}
				if (dataField == "quantityPurchase") {
			    	if (value !== undefined && value !== null && value != '' && value > 0){
			    		qty = value;
			    		var uom = rowData.uomId;
			    		if (item) {
			    			uom = item.quantityUomId;
			    			if (requireAmount && 'Y' == requireAmount){
			    				uom = item.weightUomId;
			    			}
			    			if (rowData.itemComment) {
		    					item.itemComment = rowData.itemComment;
		    				}
				    		if (typeof oldvalue != 'undefined' && oldvalue != value) {
				    			OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
				    		} else if (orderId != 'undefined' && value > 0) {
				    			if (!oldvalue) {
				    				if (typeof item.quantityPurchase == 'number') {
				    					oldvalue = parseFloat(item.quantityPurchase);
									} else if (typeof item.quantityPurchase == 'string'){
										oldvalue = parseFloat(item.quantityPurchase.replace(',', '.'));
									}
				    			}
				    			if (oldvalue != value) {
				    				OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
				    			}
				    		}
			    		} else {
			    			OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, uom, value, rowData.uid, false, uomIdArray, requireAmount);
			    			item = productOrderMap[productId];
			    			if (item) {
			    				if (rowData.itemComment) {
			    					item.itemComment = rowData.itemComment;
			    				}
			    			}
			    		}
			    	}
			    	if (value == 0){
			    		if (item != undefined){
			    			if (typeof orderId != 'undefined' && orderId != null && orderId != ''){
			    				item["quantityPurchase"] = 0;
			    				item["quantity"] = 0;
			    				productOrderMap[productId] = item;
			    			} else {
			    				delete productOrderMap[productId];
			    			}
			    		}
			    	}
				}
				if (dataField == "lastPrice") {
					if (item) {
						item.lastPrice = value;
					} else {
						var itemMap = {};
						if (rowData.quantityPurchase && rowData.quantityPurchase > 0) {
							itemMap.lastPrice = value;
							productOrderMap[productId] = itemMap;
						}
					}
				}
				if (dataField == "itemComment") {
					if (item) {
						item.itemComment = value;
					} else {
						var itemMap = {};
						if (rowData.quantityPurchase && rowData.quantityPurchase > 0) {
							itemMap.itemComment = itemComment;
							productOrderMap[productId] = itemMap;
						}
					}
				}
				if (dataField == "uomId") {
					var qty = 1;
			    	if (rowData.quantityPurchase !== undefined && rowData.quantityPurchase !== null && rowData.quantityPurchase != '' && rowData.quantityPurchase > 0){
			    		qty = rowData.quantityPurchase;
			    		isFirst = false;
			    	} else {
			    		if (item && item.quantityPurchase > 0) {
			    			qty = item.quantityPurchase;
			    		}
			    		if (typeof orderId != 'undefined' && orderId != null && orderId != '') isFirst = false;
			    	}
			    	OlbOrderProduct.updateProductPrice(rowData.productId, rowData.partyId, rowData.currencyUomId, value, qty, rowData.uid, isFirst, uomIdArray, requireAmount);
				}
			}
		});
	};
	var updateProductPrice = function (productId, partyId, currencyUomId, uomId, quantity, rowId, isFirst, uomIdArray, requireAmount){
		$.ajax({
			url : "calculatePurchasePrice",
			type : "POST",
			async: false,
			data : {
				productId : productId,
				partyId : partyId,
				currencyUomId : currencyUomId,
				quantityUomId: uomId,
				quantity : quantity,
			},
			dataType : "json",
			success : function(data) {
			}
		}).done(function(data) {
			var lastPrice = data.price;
			var minimumOrderQuantity = data.minimumOrderQuantity;
			var item = productOrderMap[productId];
			var quantityConvert = 0;
			var convert = 0;
			for (var i = 0; i < uomIdArray.length; i++) {
				var obj = uomIdArray[i];
				if (requireAmount && 'Y' == requireAmount){
					var uomIdTmp = obj.uomId;
					convert = obj.convertNumber;
					if (uomId == uomIdTmp){
						quantityConvert = convert*quantity;
						break;
					}
				} else {
					var quantityUomIdTmp = obj.quantityUomId;
					convert = obj.convertNumber;
					if (uomId == quantityUomIdTmp){
						quantityConvert = convert*quantity;
						break;
					}
				}
			}
			if (item) {
				if (!isFirst){
					item.quantityPurchase = quantity;
					item.quantity = quantityConvert;
					if (minimumOrderQuantity/convert > quantity){
						item.quantity = 0;
						item.quantityPurchase = 0;
					}
				} else {
					item.quantity = 0;
					item.quantityPurchase = 0;
				}
				if (requireAmount && 'Y' == requireAmount){
					item.weightUomId = uomId;
				} else {
					item.quantityUomId = uomId;
				}
				item.lastPrice = lastPrice;
				item.productId = productId;
			} else {
				var itemMap = {};
				if (!isFirst){
					itemMap.quantityPurchase = quantity;
					itemMap.quantity = quantityConvert;
					if (minimumOrderQuantity/convert > quantity){
						itemMap.quantity = 0;
						itemMap.quantityPurchase = 0;
					}
				} else {
					itemMap.quantity = 0;
					itemMap.quantityPurchase = 0;
				}
				
				itemMap.productId = productId;
				itemMap.lastPrice = lastPrice;
				itemMap.supplierProductId = partyId;
				if (requireAmount && 'Y' == requireAmount){
					itemMap.weightUomId = uomId;
				} else {
					itemMap.quantityUomId = uomId;
				}
				productOrderMap[productId] = itemMap;
			}
			setTimeout(function(){
			$("#jqxgridProduct").jqxGrid('setcellvaluebyid', rowId, "lastPrice", lastPrice);
			$("#jqxgridProduct").jqxGrid('setcellvaluebyid', rowId, "minimumOrderQuantity", minimumOrderQuantity);
			if (minimumOrderQuantity/convert > quantity){
				setTimeout(function(){
					$("#jqxgridProduct").jqxGrid('setcellvaluebyid', rowId, "quantityPurchase", 0);
					jOlbUtil.alert.error(uiLabelMap.BPORestrictMOQ + " (MOQ=" + minimumOrderQuantity + ")");
					return false;
				}, 50);
			} else {
				if (!isFirst){
					setTimeout(function(){
					$("#jqxgridProduct").jqxGrid('setcellvaluebyid', rowId, "quantityPurchase", quantity);
					}, 50);
				}
			}
			}, 50);
		});
	};
	
	var cellclassnameAdd = function (row, column, value, data) {
		var data = $('#jqxgridProductAdd').jqxGrid('getrowdata',row);
		if (data['purchaseDiscontinuationDate'] != undefined && data['purchaseDiscontinuationDate'] != null) {
			var now = new Date();
			var ex = new Date(data['purchaseDiscontinuationDate']);
			if (ex <= now){
	        	return 'background-cancel';
	        }
	    } else {
	    	if (column == 'quantityPurchase' || column == 'itemComment' || column == 'lastPrice') {
				return 'background-prepare';
	    	} 
	    }
	}
	var checkSpecialCharacters = function(value) {
		if (OlbCore.isNotEmpty(value) && !(/^[a-zA-Z0-9 ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀẾỂưăạảấầẩẫậắằẳẵặẹẻẽềếểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/.test(value))) {
			return true;
		}
		return false;
	}
	
	this.focusSearch = function() {
		$("#jqxProductSearch").jqxComboBox('clearSelection');
		$("#jqxProductSearch").jqxComboBox('close');
		$("#jqxProductSearch").jqxComboBox('focus');
		return false;
	}
	
	var getConvertNumber = function (rowData){
		var value = 1;
		var item = productOrderMap[rowData.productId];
		var curUomId = rowData.uomId;
		if (item) {
			if (item.requireAmount && 'Y' == item.requireAmount) {
				if (item.weightUomId) {
					curUomId = item.weightUomId;
				}
			} else {
				if (item.quantityUomId) {
					curUomId = item.quantityUomId;
				}
			}
		} 
		var requireAmount = rowData.requireAmount;
		if (requireAmount && 'Y' == requireAmount){
			var weightUomIdArray = rowData['weightUomIds'];
			for (var i = 0; i < weightUomIdArray.length; i++) {
				var obj = weightUomIdArray[i];
				var uomId = obj.uomId;
				if (uomId == curUomId) {
					value = obj.convertNumber;
					break;
				}
			}
		} else {
			var qtyUomIdArray = rowData['quantityUomIds'];
			for (var i = 0; i < qtyUomIdArray.length; i++) {
				var obj = qtyUomIdArray[i];
				var uomId = obj.quantityUomId;
				if (uomId == curUomId) {
					value = obj.convertNumber;
					break;
				}
			}
		}
		return value;
	}
	return {
		init : init,
		updateProductPrice: updateProductPrice,
	}
}());