$(document).ready(function() {
	ObjAddQuota.init();
});
var ObjAddQuota = (function() {
	listProductSelected = [];
	var gridProduct = $("#jqxGridProducts");
	var gridSupplier = $("#jqxGridSupplier");
	var validatorVAL = null;
	var partySelected = null;
	var currencySelected = null;
	addWindow = $("#popupWindowQuota");
	var init = function() { 
		initInput();
		initElementComplex(); 
		initEvents();
		initValidate();
	};
	
	var initInput = function() { 
		$("#supplier").jqxDropDownButton({width: 300, theme: theme}); 
		$("#quotaCode").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#quotaName").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#description").jqxInput({width: 300, theme: theme}); 
		$('#supplier').jqxDropDownButton('setContent', '<div class="dropdown-button">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$('#currencyUomId').jqxDropDownList({ source: [], selectedIndex: 0, width: 300,theme: theme, valueMember: 'uomId', displayMember: 'description', placeHolder : uiLabelMap.PleaseSelectTitle})
		
		addWindow.jqxWindow({
			width : 1200,
			height : 680,
			minWidth : 600,
			minHeight : 200,
			maxWidth : 1400,
			maxHeight : 800,
			resizable : true,
			cancelButton : $("#alterCancelQuota"),
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
		initgridSupplier(gridSupplier);
	}
	
	var initgridSupplier = function(grid){
		var datafield =  [
		                  { name: 'partyId', type: 'string'},
		                  { name: 'partyCode', type: 'string'},
		                  { name: 'groupName', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.POSupplierId, datafield: 'partyCode', align: 'left', width: 200, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<div style="cursor: pointer;">' + value + '</div>';
		                		  }
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.POSupplierName, datafield: 'groupName', align: 'left', minwidth: 150, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style="cursor: pointer;">' + value + '</div>';
		                	  }
		                  },
		                  ];
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
//				url: 'jqGetListPartySupplier',                
				url: '',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
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
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 150, editable: false, },
			{ text: uiLabelMap.Unit, dataField: 'quantityUomId', minwidth: 100, filterable: false, editable: false,
				cellsrenderer: function(row, column, value) {
					var rowData = grid.jqxGrid('getrowdata', row);
					if (rowData.requireAmount == 'Y' && rowData.amountUomTypeId == 'WEIGHT_MEASURE') value = rowData.weightUomId;
					return '<span>' + getUomDesc(value) +'</span>';
			    }
			},
			{ text: uiLabelMap.BIECurrentQuota, datafield: 'quantityQuota', sortable: false, editable: false,  width: 150, filterable: false, sortable: false,
				cellsrenderer: function(row, column, value) {
					if (value > 0) {
						return '<span class="align-right">' + formatnumber(value) +'</span>';
					} else {
						return '<span class="align-right">0</span>';
					}
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
			{ text: uiLabelMap.FromDate + ' *', dataField: 'fromDate', width: 150, filterable: false, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
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
			{ text: uiLabelMap.ThruDate + ' *', dataField: 'thruDate', width: 150, filterable: false, editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
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
			{ name: 'productCode', type: 'string'},
			{ name: 'productName', type: 'string' },
			{ name: 'uomId', type: 'string' },
			{ name: 'weightUomId', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'amountUomTypeId', type: 'string' },
			{ name: 'quantity', type: 'number' },
			{ name: 'quantityQuota', type: 'number' },
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			{ name: 'thruDate', type: 'date', other: 'Timestamp'},
		 	]
		
		var config = {
			width: '100%', 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		editmode: 'rowclick',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: true,
	        rowsheight: 26,
	        rowdetails: false,
	        useUrl: true,
	        url: "JQGetPOListProducts&getQuota=Y",                
	        source: {pagesize: 15}
	  	};
	  	Grid.initGrid(config, datafield, columns, null, grid);
	}
	
	
	var initEvents = function() {
		gridSupplier.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridSupplier.jqxGrid('getrowdata', rowBoundIndex);
	        partySelected = rowData;
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.partyCode + ' - ' + rowData.groupName +'</div>';
	        $('#supplier').jqxDropDownButton('setContent', dropDownContent);
	        gridSupplier.jqxGrid('selectrow', rowBoundIndex);
	        $("#supplier").jqxDropDownButton('close');
	        
	        updateCurrencyUom(partySelected.partyId);
	    });
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'quantity'){
					if (value > 0){
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
	    
		addWindow.on('close', function (event) {
			 gridProduct.jqxGrid('clearSelection');
			 $('#supplier').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
			 validatorVAL.hide();
			 $('#quotaCode').jqxInput('clear');
			 $('#quotaName').jqxInput('clear');
			 $('#description').jqxInput('clear');
			 listProductSelected = [];
			 partySelected = null;
			 currencySelected = null;
		 });
		
		$("#alterSaveQuota").on('click', function (event) {
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
					var map = {};
			   		map['productId'] = data.productId;
			   		map['quotaQuantity'] = data.quantity;
			   		map['uomId'] = data.uomId;
			   		var fromDate = new Date(data.fromDate);
			   		var thruDate = new Date(data.thruDate);
			   		map['fromDate'] = fromDate.getTime();
			   		map['thruDate'] = thruDate.getTime();
			        listProducts.push(map);
				}
			}
			listProducts = JSON.stringify(listProducts);
			
			bootbox.dialog(uiLabelMap.AreYouSureCreate, 
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
							if ($("#quotaCode").jqxInput('val')){
								data.quotaCode = $("#quotaCode").jqxInput('val');
							}
							if ($("#quotaName").jqxInput('val')){
								data.quotaName = $("#quotaName").jqxInput('val');
							}
							if (currencySelected != null){
								data.currencyUomId = currencySelected.currencyUomId;
							}
							if (partySelected != null){
								data.supplierPartyId = partySelected.partyId;
							}
							data.listProducts = listProducts;
							$.ajax({
					    		url: "createQuotaHeader",
					    		type: "POST",
					    		async: false,
					    		data: data,
					    		success: function (res){
					    			if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
										jOlbUtil.alert.error(res._ERROR_MESSAGE_);
										Loading.hide("loadingMacro");
										return false;
									}
					    			addWindow.jqxWindow('close');
					    			if (res.quotaId != undefined){
					    				window.location.href = "getDetailQuotaHeaders?quotaId="+res.quotaId
					    			}
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
		
		$("#currencyUomId").on('change', function (event) {
			currencySelected = event.args.item.originalItem;
			if (partySelected && currencySelected) {
				updateProductGrid(partySelected.partyId, currencySelected.currencyUomId);
			}
		});
	}
	
	var initValidate = function() {
		var extendRules = [
//			{
//				input: '#supplier', 
//			    message: uiLabelMap.FieldRequired, 
//			    action: 'blur', 
//			    position: 'right',
//			    rule: function (input) {
//			    	if (partySelected == null){
//			    		return false;
//			    	}
//				   	return true;
//			    }
//			},
		];
   		var mapRules = [
   			{input: '#quotaCode', type: 'validInputNotNull', action: 'valueChanged'},
        ];
   		validatorVAL = new OlbValidator(addWindow, mapRules, extendRules, {position: 'right'});
	}
	
	var openPopupAdd = function() {
		addWindow.jqxWindow('open');
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = gridProduct.jqxGrid('getrowdata',row);
    	if (column == 'quantity' || column == 'fromDate' || column == 'thruDate') {
    		return 'background-prepare';
    	}
	}
	
	var updateCurrencyUom = function(partyId) {
		$.ajax({
			url : "getSupplierCurrencyUom",
			type : "POST",
			data : {
				partyId : partyId,
			},
			dataType : "json",
			success : function(data) {
				
			}
		}).done(function(data) {
			var listCurrencyUoms = data.listCurrencyUoms;
			var currencyCombo = [];
			if (listCurrencyUoms != undefined && listCurrencyUoms.length > 0) {
				for (var i = 0; i < listCurrencyUoms.length; i ++) {
					var x = {};
					x.currencyUomId = listCurrencyUoms[i].uomId;
					x.description = listCurrencyUoms[i].abbreviation;
					currencyCombo.push(x);
				}
			}
			if (currencyCombo.length > 0){
				$("#currencyUomId").jqxDropDownList({ source : currencyCombo, disabled : false });
				$("#currencyUomId").jqxDropDownList('selectIndex', 0);
				currencySelected = currencyCombo[0]; 
				if (partySelected && currencySelected) {
					updateProductGrid(partySelected.partyId, currencySelected.currencyUomId);
				}
			}
		});
	};
	
	function updateProductGrid(supplierId, currencyUomId){
		if (supplierId && currencyUomId) {
			listProductSelected = [];
			var tmpS = gridProduct.jqxGrid("source");
			tmpS._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&getQuota=Y&supplierId="
				+ supplierId + "&currencyUomId=" + currencyUomId;
			gridProduct.jqxGrid("updatebounddata");
		}
	}
	
	return {
		init : init,
		openPopupAdd: openPopupAdd,
	}
}());