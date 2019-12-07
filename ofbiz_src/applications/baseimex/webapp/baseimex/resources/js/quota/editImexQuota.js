$(document).ready(function() {
	ObjEditQuota.init();
});
var ObjEditQuota = (function() {
	listProductSelected = [];
	var listProductAdds = [];
	var gridProduct = $("#jqxGridEditProducts");
	var gridProductAdd = $("#jqxGridProductAdds");  
	
	var validatorVAL = null;
	var selectedData = null;
	var popupAddProduct = $("#popupAddProduct");
	editWindow = $("#popupWindowEdit");
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidate();
	};
	
	var initInput = function() { 
		$("#quotaCodeEdit").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#quotaNameEdit").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#descriptionEdit").jqxInput({width: 300, theme: theme}); 
		
		editWindow.jqxWindow({
			width : 1200,
			height : 740,
			minWidth : 600,
			minHeight : 200,
			maxWidth : 1400,
			maxHeight : 800,
			resizable : true,
			cancelButton : $("#editCancel"),
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
	
	var initGridProductAdd = function(grid){
		var datafield = [
             	{ name: 'productId', type: 'string'},
             	{ name: 'eventItemSeqId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'requireAmount', type: 'String'},
				{ name: 'amountUomTypeId', type: 'String'},
				]
		
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 130, editable: false,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable: false,},
			{ text: uiLabelMap.Unit, dataField: 'uomId', minwidth: 100, editable: false, filterable: false,
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					value = rowData.quantityUomId;
					if (rowData.requireAmount == 'Y' && rowData.amountUomTypeId == 'WEIGHT_MEASURE'){
						value = rowData.weightUomId;
					}
					return '<span>' + getUomDesc(value) +'</span>';
			    }
			},
			{ text: uiLabelMap.BIENewQuota + ' *', datafield: 'quantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
					if (rowData.quantity < 0){
						editor.jqxNumberInput({disabled: true});
					} else {
						editor.jqxNumberInput({disabled: false});
					}
					
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
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.FromDate + ' *', dataField: 'fromDate', filterable: false, width: 150, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					$.each(listProductAdds, function(i){
						var olb = listProductAdds[i];
						if (olb.productId == rowData.productId ){
							value = olb.fromDate;
							return false;
						}
					});
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		if (value) {
				        var data = grid.jqxGrid('getrowdata', cell.row);
				        if (data.thruDate){
				        	var exp = new Date(data.thruDate);
				        	if (exp < new Date(value)){
					        	return { result: false, message: uiLabelMap.BIETimeRangeNotTrue};
					        }
				        }
			        } 
			        return true;
				 },
			},
			{ text: uiLabelMap.ThruDate + ' *', dataField: 'thruDate', filterable: false, width: 150, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					$.each(listProductAdds, function(i){
						var olb = listProductAdds[i];
						if (olb.productId == rowData.productId ){
							value = olb.thruDate;
							return false;
						}
					});
				    
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		if (value) {
				        var data = grid.jqxGrid('getrowdata', cell.row);
				        if (data.fromDate){
				        	var mnf = new Date(data.fromDate);
				        	if (mnf > new Date(value)){
					        	return { result: false, message: uiLabelMap.BIETimeRangeNotTrue};
					        }
				        }
			        } 
			        return true;
				 },
			},
        ];
		
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
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initGridProduct = function (grid) {
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 130, editable: false,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable: false,},
			{ text: uiLabelMap.Unit, dataField: 'uomId', minwidth: 100, filterable: false, editable: false, 
				cellsrenderer: function(row, column, value) {
					return '<span>' + getUomDesc(value) +'</span>';
			    }
			},
			{ text: uiLabelMap.BIENewQuota + ' *', datafield: 'quantity', sortable: false,  width: 150, editable: true, filterable: false, sortable: false, cellclassname: productGridCellclass,
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
					if (rowData.quantity < 0){
						editor.jqxNumberInput({disabled: true});
					} else {
						editor.jqxNumberInput({disabled: false});
					}
					
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
					if (value < 0) {
						return { result: false, message: uiLabelMap.ValueMustBeGreaterThanZero };
					}
					return true;
				}, 
			},
			{ text: uiLabelMap.FromDate + ' *', dataField: 'fromDate', filterable: false, width: 150, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = gridProduct.jqxGrid('getrowdata', row);
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.productId == rowData.productId ){
							value = olb.fromDate;
							return false;
						}
					});
				    
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = gridProduct.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		if (value) {
				        var data = gridProduct.jqxGrid('getrowdata', cell.row);
				        if (data.thruDate){
				        	var exp = new Date(data.thruDate);
				        	if (exp < new Date(value)){
					        	return { result: false, message: uiLabelMap.BIETimeRangeNotTrue};
					        }
				        }
			        } 
			        return true;
				 },
			},
			{ text: uiLabelMap.ThruDate + ' *', dataField: 'thruDate', filterable: false, width: 150, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = gridProduct.jqxGrid('getrowdata', row);
					$.each(listProductSelected, function(i){
						var olb = listProductSelected[i];
						if (olb.productId == rowData.productId ){
							value = olb.thruDate;
							return false;
						}
					});
				    
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = gridProduct.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		if (value) {
				        var data = gridProduct.jqxGrid('getrowdata', cell.row);
				        if (data.fromDate){
				        	var mnf = new Date(data.fromDate);
				        	if (mnf > new Date(value)){
					        	return { result: false, message: uiLabelMap.BIETimeRangeNotTrue};
					        }
				        }
			        } 
			        return true;
				 },
			},
        ];
		
		var datafield = [
			{ name: 'productId', type: 'string'},
			{ name: 'quotaId', type: 'string'},
			{ name: 'quotaItemSeqId', type: 'string'},
			{ name: 'productCode', type: 'string'},
			{ name: 'productName', type: 'string' },
			{ name: 'uomId', type: 'string' },
			{ name: 'quantity', type: 'number' },
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			{ name: 'thruDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var rendertoolbarProduct = function (toolbar){
			toolbar.html("");
			var id = "ProductList";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.CommonAdd + "@javascript:void(0)@ObjEditQuota.openPopupAddProduct()";
	        Grid.createCustomControlButton(gridProduct, container, customcontrol1);
		}; 
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbarProduct,
	   		selectionmode: 'singlerow',
	   		editmode: 'rowclick',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: "",                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	
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
						var item = $.extend({}, rowData);
						item.quantity = value;
						listProductSelected.push(item);
					} 
				} 
				if (dataField == 'uomId'){
					if (rowData.quantity > 0){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.uomId = value;
						listProductSelected.push(item);
					}
				} 
				if (dataField == 'thruDate'){
					if (rowData.quantity > 0){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.thruDate = value;
						listProductSelected.push(item);
					}
				} 
				if (dataField == 'fromDate'){
					if (rowData.quantity > 0){
						$.each(listProductSelected, function(i){
							var olb = listProductSelected[i];
							if (olb.productId == rowData.productId ){
								listProductSelected.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.fromDate = value;
						listProductSelected.push(item);
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
				if (dataField == 'quantity'){
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
							item.uomId = rowData.quantityUomId;
							if (rowData.requireAmount == 'Y' && rowData.amountUomTypeId == 'WEIGHT_MEASURE'){
								item.uomId = rowData.weightUomId;
							}
							listProductAdds.push(item);
						}
					} 
				} 
				if (dataField == 'uomId'){
					if (rowData.quantity > 0){
						$.each(listProductAdds, function(i){
							var olb = listProductAdds[i];
							if (olb.productId == rowData.productId ){
								listProductAdds.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.uomId = value;
						listProductAdds.push(item);
					}
				} 
				if (dataField == 'thruDate'){
					if (rowData.quantity > 0){
						$.each(listProductAdds, function(i){
							var olb = listProductAdds[i];
							if (olb.productId == rowData.productId ){
								listProductAdds.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.thruDate = value;
						listProductAdds.push(item);
					}
				} 
				if (dataField == 'fromDate'){
					if (rowData.quantity > 0){
						$.each(listProductAdds, function(i){
							var olb = listProductAdds[i];
							if (olb.productId == rowData.productId ){
								listProductAdds.splice(i,1);
								return false;
							}
						});
						var item = $.extend({}, rowData);
						item.fromDate = value;
						listProductAdds.push(item);
					}
				} 
			}
		});
	    
		editWindow.on('close', function (event) {
			 gridProduct.jqxGrid('clearSelection');
			 gridProductAdd.jqxGrid('clearSelection');
			 validatorVAL.hide();
			 $('#quotaCodeEdit').jqxInput('clear');
			 $('#quotaNameEdit').jqxInput('clear');
			 $('#descriptionEdit').jqxInput('clear');
			 listProductSelected = [];
			 selectedData = null;
		 });
		
		editWindow.on('open', function (event) {
		});
		
		$("#editSave").on('click', function (event) {
			var resultValidate = validatorVAL.validate();
			if(!resultValidate) return false;
			if (listProductSelected.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			
			var listProducts = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					if (!data.fromDate || !data.thruDate) {
						jOlbUtil.alert.error(uiLabelMap.BIENeedEnterFromThruDate);
						return false;
					}
				}
			}
			
			var listProductUpdates = [];
			var listProductCancels = [];
			var listProductNews = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					var map = {};
			   		map['productId'] = data.productId;
			   		map['quotaQuantity'] = data.quantity;
			   		map['uomId'] = data.uomId;
			   		var fromDate = new Date(data.fromDate);
			   		var thruDate = new Date(data.thruDate);
			   		map['fromDate'] = fromDate.getTime();
			   		map['thruDate'] = thruDate.getTime();
			   		
					if (data.quantity > 0){
						if (data.quotaItemSeqId){
							map['quotaItemSeqId'] = data.quotaItemSeqId;
							listProductUpdates.push(map);
						} else {
							listProductNews.push(map);
						}
					} else {
						if (data.quotaItemSeqId){
							map['quotaItemSeqId'] = data.quotaItemSeqId;
							listProductCancels.push(map);
						}
					}
				}
			}
			listProductUpdates = JSON.stringify(listProductUpdates);
			listProductCancels = JSON.stringify(listProductCancels);
			listProductNews = JSON.stringify(listProductNews);
			
			bootbox.dialog(uiLabelMap.AreYouSureSave, 
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
							if ($("#descriptionEdit").jqxInput('val')){
								data.description = $("#descriptionEdit").jqxInput('val');
							}
							if ($("#quotaCodeEdit").jqxInput('val')){
								data.quotaCode = $("#quotaCodeEdit").jqxInput('val');
							}
							if ($("#quotaNameEdit").jqxInput('val')){
								data.quotaName = $("#quotaNameEdit").jqxInput('val');
							}
							data.listProductUpdates = listProductUpdates;
							data.listProductCancels = listProductCancels;
							data.listProductNews = listProductNews;
							data.quotaId = selectedData.quotaId;
							$.ajax({
					    		url: "updateQuotaHeader",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
										jOlbUtil.alert.error(res._ERROR_MESSAGE_);
										Loading.hide("loadingMacro");
										return false;
									}
					    			editWindow.jqxWindow('close');
					    			if (typeof(ObjQuotas) != 'undefined'){
					    				ObjQuotas.updateGridData();
					    			}
					    			if (res.quotaId){
					    				window.location.reload();
					    			}
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
		
		$("#addProductSave").on('click', function (event) {
			if (listProductAdds.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			for (var i in listProductAdds){
				var data = listProductAdds[i];
				if (!data.fromDate || !data.thruDate) {
					jOlbUtil.alert.error(uiLabelMap.BIENeedEnterFromThruDate);
					return false;
				}
			}
			var listRemove = [];
			if (listProductAdds != undefined && listProductAdds.length > 0 && listProductSelected.length >= 0){
				for (var i in listProductAdds){
					var data1 = listProductAdds[i];
					var check = false;
					for (var j in listProductSelected){
						var data2 = listProductSelected[j];
						if (data1.productId == data2.productId){
							check = false;
							listProductSelected[j].quantity = listProductSelected[j].quantity + data1.quantity;
							listProductSelected[j].fromDate = data1.fromDate;
							listProductSelected[j].thruDate = data1.thruDate;
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
		
	}
	
	var initValidate = function() {
		var extendRules = [];
   		var mapRules = [
        ];
   		validatorVAL = new OlbValidator(editWindow, mapRules, extendRules, {position: 'right'});
	}
	
	var openPopupEdit = function(data) {
		if (data != undefined){
			$('#supplierDT').text(data.partyCode + " - " + data.fullName);
			$('#currencyUomDT').text(data.currencyUomId);
			$('#quotaCodeEdit').jqxInput('val', data.quotaCode);
			$('#quotaNameEdit').jqxInput('val', data.quotaName);
			$('#descriptionEdit').jqxInput('val', data.description);
			selectedData = $.extend({}, data);
			
			listProductSelected = [];
    		
			listProductSelected = getProductByQuotaItems(data.quotaId);
			for (x in listProductSelected){
				var t = listProductSelected[x];
				t.quantity = t.quotaQuantity;
			}
    		
    		updateProductGridData(listProductSelected);
		}
		editWindow.jqxWindow('open');
	}
	
	var getProductByQuotaItems = function (quotaId) {
		var listProduct = [];
		$.ajax({	
			 type: "POST",
			 url: "getProductQuota",
			 data: {
				 quotaId: quotaId
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
    	if (column == 'quantity' || column == 'fromDate' || column == 'thruDate') {
    		return 'background-prepare';
    	}
	}
	
	function updateProductGridAdd(supplierId, currencyUomId){
//		if (supplierId && currencyUomId) {
			listProductAdds = [];
			var tmpS = gridProductAdd.jqxGrid("source");
//			tmpS._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&getQuota=Y&hasrequest=Y&supplierId="
//				+ supplierId + "&currencyUomId=" + currencyUomId;
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetPOListProducts&getQuota=Y";
			gridProductAdd.jqxGrid("updatebounddata");
//		}
	}
	
	var openPopupAddProduct = function (){
    	if (selectedData){
    		updateProductGridAdd(selectedData.supplierPartyId, selectedData.currencyUomId);
        	popupAddProduct.jqxWindow('open');
    	}
    }
	
	var updateProductGridData = function (data) {
		data.sort(function(a, b){
			return b.quantity - a.quantity;
		})
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = data;
		gridProduct.jqxGrid("source", tmpS);
		gridProduct.jqxGrid("updatebounddata");
	}
	
	return {
		init : init,
		openPopupEdit: openPopupEdit,
		openPopupAddProduct: openPopupAddProduct,
	}

}());