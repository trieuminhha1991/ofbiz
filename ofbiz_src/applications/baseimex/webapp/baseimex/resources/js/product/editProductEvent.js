$(document).ready(function() {
	ObjEditEve.init();
});
var ObjEditEve = (function() {
	var gridProduct = $("#jqxGridProducts");  
	var gridProductAdd = $("#jqxGridProductAdds");  
	var popupAddProduct = $("#popupAddProduct");  
	var popupEdit = $("#popupEdit");
	var listProductSelected = [];
	var listProductAdds = [];
	
	var selectedData = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidateForm();
	};
	
	var initInput = function() { 
		$("#eventCode").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#eventName").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#description").jqxInput({width: 300, theme: theme}); 
		
		$("#executedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#completedDate").jqxDateTimeInput({width: 300, theme: theme}); 
		popupEdit.jqxWindow({
			width : 1200,
			height : 640,
			minWidth : 600,
			minHeight : 200,
			maxWidth : 1400,
			maxHeight : 700,
			resizable : true,
			cancelButton : $("#alterCancel"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
		
		popupAddProduct.jqxWindow({
			width : 1200,
			height : 470,
			minWidth : 600,
			minHeight : 200,
			maxWidth : 1400,
			maxHeight : 700,
			resizable : true,
			cancelButton : $("#addProductCancel"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});
		
	}
	
	var initElementComplex = function() {
		initGridProduct(gridProduct);
		initGridProductAdd(gridProductAdd);
	}
	
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
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', sortable: false,  width: 120, editable: false, filterable: false, sortable: false, cellclassname: productGridCellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
						if (rowData.weightUomId) {
							value = rowData.weightUomId;
						}
					} else { 
						if (rowData.quantityUomId) {
							value = rowData.quantityUomId;
						}
					}
					if (value) {
						var desc = getUomDesc(value);
						return '<span class="align-right">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.BLQuantityRegistered, datafield: 'registeredQuantity', sortable: false,  width: 150, editable: false, filterable: false, sortable: false,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.BLQuantityUse, datafield: 'quantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
				    if (typeof value === 'string') {
				    	value = value.replace(',', '.');
				    	value = parseFloat(value, 3, null);
				    }
				    if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.quantity;
			   					return false;
			   				}
			   			});
				    }
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.quantity;
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
					var rowData = grid.jqxGrid('getrowdata', cell.row);
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					return true;
				}, 
			},
        ];
		return columns; 
	};
	
	var getColumnAdds = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,},
			{ text: uiLabelMap.Unit, datafield: 'quantityUomId', sortable: false,  width: 120, editable: false, filterable: false, sortable: false, cellclassname: Cellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (rowData.requireAmount && 'Y' == rowData.requireAmount) {
						if (rowData.weightUomId) {
							value = rowData.weightUomId;
						}
					} else { 
						if (rowData.quantityUomId) {
							value = rowData.quantityUomId;
						}
					}
					if (value) {
						var desc = getUomDesc(value);
						return '<span class="align-right">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.BLQuantityRegistered, datafield: 'quantity', sortable: false,  width: 150, editable: false, filterable: false, sortable: false, cellclassname: Cellclass,
              	cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}
			},
			{ text: uiLabelMap.BLQuantityUse, datafield: 'createQuantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: Cellclass,
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (typeof value === 'string') {
						value = value.replace(',', '.');
						value = parseFloat(value, 3, null);
					}
					if (listProductAdds.length > 0){
						$.each(listProductAdds, function(i){
							var olb = listProductAdds[i];
							if (olb.productId == rowData.productId ){
								value = olb.quantity;
								return false;
							}
						});
					}
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
				}, initeditor: function (row, cellvalue, editor) {
					var rowData = grid.jqxGrid('getrowdata', row);
					
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
					var rowData = grid.jqxGrid('getrowdata', row);
					if (!cellvalue) {
						if (listProductAdds.length > 0){
							$.each(listProductAdds, function(i){
								var olb = listProductAdds[i];
								if (olb.productId == rowData.productId ){
									cellvalue = olb.quantity;
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
					var rowData = grid.jqxGrid('getrowdata', cell.row);
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					return true;
				}, 
			},
			];
		return columns; 
	};
	
	var getDataField = function(){
		var datafield = [
             	{ name: 'productId', type: 'string'},
             	{ name: 'eventItemSeqId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'registeredQuantity', type: 'number' },
				{ name: 'requireAmount', type: 'String'}]
		return datafield;
	};
	
	var rendertoolbarProduct = function (toolbar){
		toolbar.html("");
		var id = "ProductList";
		var me = this;
		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
     	var container = $('#toolbarButtonContainer' + id);
        var maincontainer = $("#toolbarcontainer" + id);
        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.CommonAdd + "@javascript:void(0)@ObjEditEve.openPopupAddProduct()";
        Grid.createCustomControlButton(gridProduct, container, customcontrol1);
	}; 
	
	var initGridProduct = function(grid){
		var config = {
				width: '100%', 
				showtoolbar: true,
		   		rendertoolbar: rendertoolbarProduct,
				virtualmode: false,
				showtoolbar: true,
				selectionmode: 'singlecell',
				editmode: 'click',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: true,
				rowsheight: 26,
				rowdetails: false,
				useUrl: false,
				source: {pagesize: 10}
		};
		Grid.initGrid(config, getDataField(), getColumns(grid), null, grid);
	};
	
	var initGridProductAdd = function(grid){
		var config = {
				width: '100%', 
		   		virtualmode: true,
		   		showtoolbar: false,
		   		selectionmode: 'singlerow',
		   		editmode: 'click',
		   		pageable: true,
		   		sortable: true,
		        filterable: true,	        
		        editable: true,
		        rowsheight: 26,
		        rowdetails: false,
		        useUrl: true,
		        url: '',                
		        source: {pagesize: 10}
	  	};
		Grid.initGrid(config, getDataField(), getColumnAdds(grid), null, grid);
	};
	
	var initEvents = function() {
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					if (value >= 0){
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						if (value >= 0){
							var item = $.extend({}, rowData);
							item.quantity = value;
							listProductSelected.push(item);
						}
					} 
				} 
			}
		});
		
		gridProductAdd.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'createQuantity'){
					if (value >= 0){
						$.each(listProductAdds, function(i){
							var olb = listProductAdds[i];
							if (olb.productId == rowData.productId ){
								listProductAdds.splice(i,1);
								return false;
							}
						});
						if (value > 0){
							var item = $.extend({}, rowData);
							item.quantity = value;
							item.registeredQuantity = rowData.quantity;
							listProductAdds.push(item);
						}
					} 
				} 
			}
		});
		
		$("#addProductSave").on('click', function (event) {
			if (listProductAdds.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			var listRemove = [];
			if (listProductAdds != undefined && listProductAdds.length > 0 && listProductSelected.length > 0){
				for (var i in listProductAdds){
					var data1 = listProductAdds[i];
					var check = false;
					for (var j in listProductSelected){
						var data2 = listProductSelected[j];
						if (data1.productId == data2.productId){
							check = false;
							listProductSelected[j].quantity = listProductSelected[j].quantity + data1.quantity;
							listRemove.push(data1);
						}
					}
				}
				if (listRemove.length > 0) {
					for (var x in listRemove){
						var t = listRemove[x];
						$.each(listProductAdds, function(i){
							if(listProductAdds[i].productId === t.productId) {
								listProductAdds.splice(i,1);
						        return false;
						    }
						});
					}
				}
				if (listProductAdds.length > 0){
					for (var i in listProductAdds){
						listProductSelected.push(listProductAdds[i]);
					}
				}
				updateProductGridData(listProductSelected);
				popupAddProduct.jqxWindow('close');
			}
		});
		
		
		$("#alterSave").on('click', function (event) {
			var resultValidate = !validatorVAL.validate();
			if(resultValidate) return false;
			if (listProductSelected.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			
			var listProductUpdates = [];
			var listProductCancels = [];
			var listProductNews = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					var map = {};
			   		map['productId'] = data.productId;
			   		map['quantity'] = data.quantity;
			   		map['uomId'] = data.uomId;
					if (data.quantity > 0){
						if (data.eventItemSeqId){
							map['eventItemSeqId'] = data.eventItemSeqId;
							listProductUpdates.push(map);
						} else {
							map['registeredQuantity'] = data.registeredQuantity;
							listProductNews.push(map);
						}
					} else {
						if (data.eventItemSeqId){
							map['eventItemSeqId'] = data.eventItemSeqId;
							listProductCancels.push(map);
						}
					}
				}
			}
			if (listProductSelected.length == listProductCancels.length){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			listProductUpdates = JSON.stringify(listProductUpdates);
			listProductCancels = JSON.stringify(listProductCancels);
			listProductNews = JSON.stringify(listProductNews);
			
			bootbox.dialog(uiLabelMap.AreYouSureUpdate, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				    "callback": function() {bootbox.hideAll();}
				}, 
				{"label": uiLabelMap.OK,
				    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				    "callback": function() {
				    	Loading.show('loadingMacro');
				    	setTimeout(function(){
							var data = {};
							if ($("#description").jqxInput('val')){
								data.description = $("#description").jqxInput('val');
							}
							if ($("#eventCode").jqxInput('val')){
								data.eventCode = $("#eventCode").jqxInput('val');
							}
							if ($("#eventName").jqxInput('val')){
								data.eventName = $("#eventName").jqxInput('val');
							}
							
							var x = $("#executedDate").jqxDateTimeInput('getDate');
							if (x){
								data.executedDate = x.getTime();
							}
								
							var y = $("#completedDate").jqxDateTimeInput('getDate');
							if (y){
								data.completedDate = y.getTime();
							}
								
							data.listProductUpdates = listProductUpdates;
							data.listProductCancels = listProductCancels;
							data.listProductNews = listProductNews;
							data.eventId = selectedData.eventId;
							
							$.ajax({
					    		url: "updateProductEvent",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
										jOlbUtil.alert.error(res._ERROR_MESSAGE_);
										Loading.hide("loadingMacro");
										return false;
									}
					    			$('#container').empty();
				                    popupEdit.jqxWindow('close');
				    				$('#jqxNotification').jqxNotification({ template: 'success'});
					                $("#notificationContent").text(uiLabelMap.UpdateSuccess);
					                $("#jqxNotification").jqxNotification("open");
					                location.reload();
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
	}
	
	var initValidateForm = function(){
		var extendRules = [
			       			{
			       				input: '#eventCode', 
			       			    message: uiLabelMap.WrongFormat + "0-9, a-z, A-Z, _, -", 
			       			    action: 'keyup, blur', 
			       			    position: 'right',
			       			    rule: function (input) {
			       				    if (input.length > 0 ){
			       				    	var patt = /[^0-9a-zA-Z\_\-]/gm;
				       			    	var result = input.val().match(patt);
			       				    	if (result) return false
			       				    	else return true;
			       				    }
			       				    return true;
			       			    }
			       			},
			       		];
		var mapRules = [
		   				{input: '#executedDate', type: 'validInputNotNull'},
		               ];
		validatorVAL = new OlbValidator($('#popupEdit'), mapRules, extendRules, {position: 'right'});
	};
	
    var openScreenAdd = function (){
    	location.href = "newQualityTestEvent";
    }
    
    var openPopupAddProduct = function (){
    	if (selectedData){
    		updateGridProductAdd(selectedData.packingListId);
        	popupAddProduct.jqxWindow('open');
    	}
    }
    
    var updateGridProductAdd = function (packingListId){
    	if (selectedData){
    		var url = "jqxGeneralServicer?sname=jqGetPackingListItems&packingListId=" + packingListId;
    		gridProductAdd.jqxGrid("source")._source.url = url;
    		gridProductAdd.jqxGrid("updatebounddata");
    	}
    }
	
    var openPopupEdit = function (rowData){
    	if (rowData){
    		selectedData = $.extend({}, rowData);
    		$("#executedDate").jqxDateTimeInput('setDate', new Date(rowData.executedDate));
    		$("#completedDate").jqxDateTimeInput('setDate', new Date(rowData.completedDate));
    		
    		$("#eventCode").jqxInput('val', rowData.eventCode); 
    		$("#eventName").jqxInput('val', rowData.eventName); 
    		$("#description").jqxInput('val', rowData.description); 
    		
    		listProductSelected = [];
    		
    		listProductSelected = getProductByEvent(rowData.eventId);
    		updateProductGridData(listProductSelected);
    		
        	popupEdit.jqxWindow('open');
    	}
    }
    
	var updateProductGridData = function (data) {
		data.sort(function(a, b){
			return b.quantity - a.quantity;
		})
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = data;
		gridProduct.jqxGrid("source", tmpS);
	}
	
	var getProductByEvent = function (eventId) {
		var listProduct = [];
		$.ajax({	
			 type: "POST",
			 url: "getProductByEvent",
			 data: {
				 eventId: eventId
			 },
			 dataType: "json",
			 async: false,
			 success: function(res){
				 if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
					 jOlbUtil.alert.error(res._ERROR_MESSAGE_);
					 Loading.hide("loadingMacro");
					 return false;
				 }
				 if (res.listProducts){
					 listProduct = res.listProducts;
				 }
			 },
			 error: function(response){
				 
			 }
 		}).done(function(data) {
	 			
 		});
		return listProduct;
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = gridProduct.jqxGrid('getrowdata',row);
		if (column == 'quantity') {
			return 'background-prepare';
		}
	}
	
	var Cellclass = function (row, column, value, data) {
		var data = gridProductAdd.jqxGrid('getrowdata',row);
		if (column == 'createQuantity') {
			return 'background-prepare';
		}
	}
	
	return {
		init : init,
		openPopupEdit: openPopupEdit,
		openPopupAddProduct: openPopupAddProduct,
	}
}());