$(function() {
	SupReturnProductObj.init();
});
var SupReturnProductObj = (function() {
	var productAddOLBG = null;
	var gridProduct = $("#jqxgridProduct"); 
	var gridProductAdd = $("#jqxgridProductAdd"); 
	var productSelected = null;
	var listProductAdd = [];
	var init = function() {
		if (listProductSelected === undefined) {
			listProductSelected = [];
		}
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#addProductPopup").jqxWindow({
 		    maxWidth: 1500, minWidth: 500, width: 1000, modalZIndex: 10000, zIndex:10000, minHeight: 200, height: 470, maxHeight: 670, resizable: false, cancelButton: $("#addProductCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
 		});
	};
	var initElementComplex = function() {
		initProductGrid();
		initProductGridAdd();
		
//		if (returnItemInitData){
//			var tmpS = gridProduct.jqxGrid("source");
//			tmpS._source.localdata = returnItemInitData;
//			gridProduct.jqxGrid("source", tmpS);
//			gridProduct.jqxGrid("updatebounddata");
//		}
	};
	var initEvents = function() {
		
		gridProduct.on("rowselect", function (event) {    
			var args = event.args;
			var rowData = args.row;
			if (rowData){
				productSelected = $.extend({}, rowData);
			}
		});
		
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				var productId = rowData.productId;
				var item = null;
				for (var c in listProductSelected){
					var b = listProductSelected[c];
					if (b.productId == productId){
						item = b;
						break;
					}
				}
				if (dataField == "quantity") {
			    	if (value !== undefined && value !== null && value != '' && value > 0){
			    		var price = rowData.returnPriceTmp;
			    		if (oldvalue != value && !rowData.returnPriceTmp) {
			    			price = updateReturnPrice(rowData.productId, rowData.uid);
			    		} 
			    		if (item){
			    			item.quantity = value;
			    			item.returnPriceTmp = price;
			    		} else {
			    			var s = $.extend({}, rowData);
			    			s.returnPriceTmp = price;
			    			s.quantity = value;
			    			s.returnReasonId = rowData.returnReasonId;
			    			listProductSelected.push(s);
			    		}
			    	}
			    	if (value == 0){
			    		if (item != undefined){
			    			for (var c in listProductSelected){
								var b = listProductSelected[c];
								if (b.productId == productId){
									listProductSelected.splice(c, 1);
									break;
								}
							}
			    		}
			    		gridProduct.jqxGrid('setcellvaluebyid', rowData.uid, "returnPriceTmp", null);
			    		gridProduct.jqxGrid('setcellvaluebyid', rowData.uid, "returnReasonId", null);
			    	}
				}
				if (dataField == "returnPriceTmp") {
					if (item){
						if (value !== undefined && value !== null && value != '' && value >= 0 && rowData.quantity > 0){
			    			item.returnPriceTmp = value;
				    	}
					}
				}
				if (dataField == "returnReasonId") {
					if (item){
						if (value !== undefined && value !== null && value != '' && rowData.quantity > 0){
			    			item.returnReasonId = value;
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
				var productId = rowData.productId;
				var item = null;
				for (var c in listProductAdd){
					var b = listProductAdd[c];
					if (b.productId == productId){
						item = b;
						break;
					}
				}
				if (dataField == "quantity") {
			    	if (value !== undefined && value !== null && value != '' && value > 0){
			    		var price = rowData.returnPrice;
			    		if (item){
			    			item.quantity = value;
			    			item.returnPrice = price;
			    		} else {
			    			var s = $.extend({}, rowData);
			    			s.returnPrice = price;
			    			s.quantity = value;
			    			s.returnReasonId = rowData.returnReasonId;
			    			listProductAdd.push(s);
			    		}
			    	}
			    	if (value == 0){
			    		if (item != undefined){
			    			for (var c in listProductAdd){
								var b = listProductAdd[c];
								if (b.productId == productId){
									listProductAdd.splice(c, 1);
									break;
								}
							}
			    		}
			    		gridProductAdd.jqxGrid('setcellvaluebyid', rowData.uid, "returnPrice", null);
			    		gridProductAdd.jqxGrid('setcellvaluebyid', rowData.uid, "returnReasonId", null);
			    	}
				}
				if (dataField == "returnPrice") {
					if (item){
						if (value !== undefined && value !== null && value != '' && value >= 0 && rowData.quantity > 0){
			    			item.returnPrice = value;
				    	}
					}
				}
				if (dataField == "returnReasonId") {
					if (item){
						if (value !== undefined && value !== null && value != '' && rowData.quantity > 0){
			    			item.returnReasonId = value;
				    	}
					}
				}
			}
		});
		
		$("#addProductSave").on("click", function (event) {
			var rowPosition = "first";
			if (listProductAdd.length > 0){
				for (var x in listProductAdd){
					var data = listProductAdd[x];
					var check = false;
					var productId = data.productId;
					var item = null;
					
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == data.productId ){
		   					item = olb;
		   					return false;
		   				}
		   			});
					
					if (item){
						var x = item.quantity;
						if (typeof x === 'string') {
					    	x.replace(',', '.');
					    	x = parseFloat(x, 3, null);
					    }
						item.quantity = x + data.quantity;
						item.returnReasonId = data.returnReasonId;
						item.returnPriceTmp = data.returnPrice;
						var curRows = gridProduct.jqxGrid('getrows');
						for (var z in curRows){
							var t = curRows[z];
							if (t.productId == productId){
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "quantity", item.quantity);
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "returnPriceTmp", item.returnPriceTmp);
								$("#jqxgridProduct").jqxGrid('setcellvaluebyid', t.uid, "returnReasonId", item.returnReasonId);
							}
						}
					} else {
						var x = $.extend({}, data);
						x.returnPriceTmp = x.returnPrice;
						listProductSelected.push(x);
					}
				}
			}
			OlbGridUtil.updateSource(gridProduct, null, listProductSelected, false);
 			gridProduct.jqxGrid('updatebounddata');
			$("#addProductPopup").jqxWindow('close');
		});
		
		$("#addProductPopup").on("close", function (event) {
			listProductAdd = [];
		});
	};
	var initValidateForm = function() {
		var extendRules = [];
		var mapRules = [];
	};
	
	var updateReturnPrice = function (productId, rowId){
		var price = 0;
		$.ajax({
			url : "getProductReturnPrice",
			type : "POST",
			async: false,
			data : {
				productId : productId,
				facilityId: facilityId,
				organizationPartyId: ownerPartyId,
			},
			dataType : "json",
			success : function(data) {
				price = data.returnPrice;
				gridProduct.jqxGrid('setcellvaluebyid', rowId, "returnPriceTmp", price);
			}
		}).done(function(data) {
		});
		return price;
	};
	
	var dataFieldAdd = [
	                	{name: 'productId', type: 'string'},
	        			{name: 'productCode', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'requireAmount', type: 'string'},
	               		{name: 'description', type: 'string'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'returnReasonId', type : 'string'}, 
	               		{name: 'returnPrice', type: 'number'},
	               		{name: 'quantity', type: 'number'},
	                    ];
	var columnAdd = [
				{text: uiLabelMap.ProductId, dataField: 'productCode', width: 140, editable:false},
				{text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 120, editable:false},
				{text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 120, editable:false, filterable: false,
					cellsrenderer : function(row, column, value) {
						var data = gridProductAdd.jqxGrid("getrowdata", row);
						var requireAmount = data.requireAmount;
						if (requireAmount && requireAmount == 'Y') {
							value = data.weightUomId;
						}
						if (value) {
							return '<span class="align-right">' + getUomDescription(value) + '</span>';
						}
					},
				},
				{ text : uiLabelMap.Quantity, datafield : "quantity", width : "120", editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", cellClassName: 'background-prepare',
					cellsrenderer : function(row, column, value) {
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
						if (value > 0) {
							return '<span class="align-right">' + formatnumber(value) +'</span>';
						} else {
							return '<span class="align-right"></span>';
						}
					},
					initeditor: function (row, cellvalue, editor) {
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if ('Y' == rowData.requireAmount) {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						} else {
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
						}
						if (!cellvalue) {
							if (listProductAdd.length > 0){
						    	$.each(listProductAdd, function(i){
					   				var olb = listProductAdd[i];
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
					},
					validation : function(cell, value) {
						var data = gridProduct.jqxGrid("getrowdata", cell.row);
						if (value < 0) {
							return { result : false, message : uiLabel.ValueMustBeGreaterThanZero };
						}
						return true;
					}
				},
				{ text : uiLabelMap.UnitPrice, datafield : "returnPrice",
					width : 100, editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", cellClassName: 'background-prepare', 
					cellsrenderer: function(row, column, value){
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.returnPrice;
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
							return '<span class="align-right">' + formatnumber(value, null, 3) +'</span>';
						}
					},
					initeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (!cellvalue){
							if (listProductAdd.length > 0){
						    	$.each(listProductAdd, function(i){
					   				var olb = listProductSelected[i];
					   				if (olb.productId == rowData.productId ){
					   					cellvalue = olb.returnPrice;
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
				{ text : uiLabelMap.Reason, datafield : "returnReasonId", width : 200, editable : true, filterable : false, columntype : "dropdownlist", cellclassname: productGridCellclass, 
					cellsrenderer : function(row, column, value) {
						var rowData = gridProductAdd.jqxGrid('getrowdata', row);
						if (listProductAdd.length > 0){
					    	$.each(listProductAdd, function(i){
				   				var olb = listProductAdd[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.returnReasonId;
				   					return false;
				   				}
				   			});
					    }
						if (value) {
							for (var i = 0; i < returnReasonData.length; i++) {
								if (value == returnReasonData[i].returnReasonId) {
									return '<span>' + returnReasonData[i].description + '</span>';
								}
							}
						} else {
							return '<span></span>';
						}
					},
					createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDropDownList({
							placeHolder : uiLabelMap.PleaseSelectTitle,
							source : returnReasonData,
							valueMember : "returnReasonId",
							displayMember : "description"
						});
					}
				}, 
             ];
	
	var initProductGridAdd = function() {
		var grid = gridProductAdd;
		var datafield = dataFieldAdd;
		var columns = columnAdd;
		var configProductAdd = {
				datafields: datafield,
				columns: columns,
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
				selectionmode: "singlerow",
				bindresize: true,
				pagesize: 10,
			};
			productAddOLBG = new OlbGrid(grid, null, configProductAdd, []);
	};

	var datafieldprs = [ { name : "orderId", type : "string" }, 
	                     { name : "productId", type : "string" }, 
	                     { name : "productCode", type : "string" }, 
	                     { name : "productName", type : "string" }, 
	                     { name : "quantity", type : "number" }, 
	                     { name : "amount", type : "number" }, 
	                     { name : "returnPriceTmp", type : "number" },
	                     { name : "returnPrice", type : "number" },
	                     { name : "orderItemSeqId", type : "string" }, 
	                     { name : "orderedQuantity", type : "number" }, 
	                     { name : "returnableQuantity", type : "number" }, 
	                     { name : "returnReasonId", type : "string" }, 
	                     { name : "quantityUomId", type : "string" }, 
	                     { name : "weightUomId", type : "string" }, 
	                     { name : "requireAmount", type : "string" }, 
                     ];
	var columnprs = [
			{ text : uiLabelMap.SequenceId, sortable : false, filterable : false, editable : false, pinned : true, groupable : false, draggable : false, resizable : false, datafield : "", columntype : "number", width : 40,
				cellsrenderer : function(row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},
			{ text : uiLabelMap.ProductId, datafield : "productId", width : 120, editable : false, hidden : true, pinned : true
			},
			{ text : uiLabelMap.ProductId, datafield : "productCode", width : 120, editable : false, pinned : true
			},
			{ text : uiLabelMap.ProductName, dataField : "productName", minwidth : 130, editable : false,
			},
			{ text : uiLabelMap.Unit, datafield : "quantityUomId", width : "120", editable : false, filterable : false, cellsalign : "right", columntype : "numberinput",
				cellsrenderer : function(row, column, value) {
					var data = gridProduct.jqxGrid("getrowdata", row);
					var requireAmount = data.requireAmount;
					if (requireAmount && requireAmount == 'Y') {
						value = data.weightUomId;
					}
					if (value) {
						return '<span class="align-right">' + getUomDescription(value) + '</span>';
					}
				},
			},
			{ text : uiLabelMap.Quantity, datafield : "quantity", width : "120", editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", cellclassname: productGridCellclass, 
				cellsrenderer : function(row, column, value) {
					var rowData = gridProduct.jqxGrid('getrowdata', row);
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
				},
				initeditor: function (row, cellvalue, editor) {
					var rowData = gridProduct.jqxGrid('getrowdata', row);
					if ('Y' == rowData.requireAmount) {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
					} else {
						editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 0, spinMode: 'simple', groupSeparator: '.', min:0 });
					}
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
				},
				validation : function(cell, value) {
					var data = gridProduct.jqxGrid("getrowdata", cell.row);
					if (value < 0) {
						return { result : false, message : uiLabel.ValueMustBeGreaterThanZero };
					}
					return true;
				}
			},
			{ text : uiLabelMap.UnitPrice, datafield : "returnPriceTmp",
				width : 100, editable : true, filterable : false, cellsalign : "right", columntype : "numberinput", cellclassname: productGridCellclass, 
				cellsrenderer : function(row, column, value) {
				},
				cellsrenderer: function(row, column, value){
					var rowData = gridProduct.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.returnPriceTmp;
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
						return '<span class="align-right">' + formatnumber(value, null, 3) +'</span>';
					}
				},
				initeditor: function (row, cellvalue, editor) {
					editor.jqxNumberInput({ inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0, decimalDigits: 2 });
					var rowData = gridProduct.jqxGrid('getrowdata', row);
					if (!cellvalue){
						if (listProductSelected.length > 0){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					cellvalue = olb.returnPriceTmp;
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
			{ text : uiLabelMap.Reason, datafield : "returnReasonId", width : 200, editable : true, filterable : false, columntype : "dropdownlist", cellclassname: productGridCellclass, 
				cellsrenderer : function(row, column, value) {
					if (value) {
						for (var i = 0; i < returnReasonData.length; i++) {
							if (value == returnReasonData[i].returnReasonId) {
								return '<span>' + returnReasonData[i].description + '</span>';
							}
						}
					} else {
						return '<span></span>';
					}
				},
				createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight) {
					editor.jqxDropDownList({
						placeHolder : uiLabelMap.PleaseSelectTitle,
						source : returnReasonData,
						valueMember : "returnReasonId",
						displayMember : "description"
					});
				}
			}, 
		];
	var initProductGrid = function() {
		var rendertoolbarProduct = function (toolbar){
			toolbar.html("");
	        var container = $("<div id='toolbarcontainerGridProduct' class='widget-header' style='height:33px !important;'><div id='jqxProductSearch' class='pull-right' style='margin-left: -10px !important; margin-top: 4px'></div></div>");
	        toolbar.append(container);
	        container.append('<div class="margin-top10">');
	        container.append('<a href="javascript:SupReturnProductObj.deleteRow()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="red fa fa-times"></i></a>');
	        container.append('<a href="javascript:SupReturnProductObj.addWithGrid()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-plus"></i></a>');
	        container.append('</div>');
	        
	        var paramInput = {
					supplierPartyId: partyId,
					currencyUomId: currencyUomId,
			};
			SearchProduct.init($("#jqxProductSearch"), $("#jqxgridProduct"), "quantity", "findProductsToReturn", "listProducts",  paramInput, listProductSelected, uiLabelMap.BPSearchProductToAdd, uiLabelMap.BPProductNotFound);
		}
		
		var grid = gridProduct;
		var config = {
			datafields : datafieldprs,
			columns : columnprs,
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
			rendertoolbar: rendertoolbarProduct,
			formData: "filterObjData",
			selectionmode: "singlerow",
			toolbarheight: 38,
			bindresize: true,
			pagesize: 10,
		};
		
		product = new OlbGrid(grid, returnItemInitData, config, [])
	};
	
	var deleteRow = function (){
		if (productSelected == null) {
			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseProduct);
			return false;
		}
		var rowindex = gridProduct.jqxGrid('getselectedrowindex');
		var data = gridProduct.jqxGrid('getrowdata', rowindex);
		if (data){
			if (data.quantity > 0){
				$.each(listProductSelected, function(m){
	   				var olb = listProductSelected[m];
	   				if (olb) {
	   					if (olb.productId == data.productId) {
		   					listProductSelected.splice(m, 1);
		   					return false;
		   				}
	   				}
	   			});
			}
			var rowid = data.uid;
			gridProduct.jqxGrid('deleterow', rowid);
		}
	}
	
	var addWithGrid = function (){
		if (facilitySelected){
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQGetListProductToReturnSupplier&facilityId=" + facilitySelected.facilityId);
		} else {
			productAddOLBG.updateSource("jqxGeneralServicer?sname=JQGetListProductToReturnSupplier");
		}
		$("#addProductPopup").jqxWindow('open');
		$("#jqxgridProductAdd").jqxGrid("updatebounddata");
	}
	
	var loadProduct = function loadProduct(valueDataSoure) {
		for (var i = 0; i < valueDataSoure.length; i++) {
			valueDataSoure[i]["unitPriceTmp"] = valueDataSoure[i]["unitPrice"];
		}
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = valueDataSoure;
		gridProduct.jqxGrid("source", tmpS);
	};
	
	function productGridCellclass (row, column, value, data) {
    	if (column == 'quantity' || column == 'returnPriceTmp' || column == 'returnReasonId') {
			return 'background-prepare';
    	}
	}
	
	return {
		init : init,
		loadProduct : loadProduct,
		deleteRow: deleteRow,
		addWithGrid: addWithGrid,
	}
}());