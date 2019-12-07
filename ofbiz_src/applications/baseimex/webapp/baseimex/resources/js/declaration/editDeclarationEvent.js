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
	var validatorVAL;
	var selectedData = null;
	var init = function() { 
		initInput();
		initElementComplex(); 
		initValidateForm();
		initEvents();
		
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
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, cellclassname: Cellclass,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,  cellclassname: Cellclass,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,  cellclassname: Cellclass, },
			{ text: uiLabelMap.FromDate, dataField: 'fromDate' , width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: Cellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.fromDate;
			   					return false;
			   				}
			   			});
				    }
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
			 		var data = grid.jqxGrid('getrowdata', cell.row);
			 		if (value) {
			 			if (data.thruDate){
			 				var exp = new Date(data.thruDate);
			 				if (exp < new Date(value)){
			 					return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
			 				}
			 			}
			 		} 
				    return true;
				 },
			},
			{ text: uiLabelMap.ThruDate, dataField: 'thruDate' ,  width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: Cellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductSelected.length > 0){
				    	$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.thruDate;
			   					return false;
			   				}
			   			});
				    }
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
			 		var data = grid.jqxGrid('getrowdata', cell.row);
			 		if (value){
			 			if (data.fromDate){
					        var exp = new Date(data.fromDate);
					        if (exp > new Date(value)){
						        return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
						    } 
					    } 
			 		}  
			 		return true;
				 },
			},
        ];
		return columns; 
	};
	
	var getColumnAdds = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, cellclassname: productGridCellclass,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},		
			{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: false, pinned: true,  cellclassname: productGridCellclass,},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 100, editable:false,  cellclassname: productGridCellclass,},
			{ text: uiLabelMap.FromDate, dataField: 'fromDate' , width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductAdds.length > 0){
				    	$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.fromDate;
			   					return false;
			   				}
			   			});
				    }
					if (value){
						return '<span class="align-right">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
					} else {
						return '<span class="align-right"></span>';
					}
				},
				initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = grid.jqxGrid('getrowdata', row);
		            editor.jqxDateTimeInput({disabled: false});
			 	},
			 	validation: function (cell, value) {
				    var data = grid.jqxGrid('getrowdata', cell.row);
				    if (data.thruDate){
				        var exp = new Date(data.thruDate);
				        if (exp < new Date(value)){
					        return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
					    }
				    }
			        return true;
				 },
			},
			{ text: uiLabelMap.ThruDate, dataField: 'thruDate' ,  width: '25%', editable: true, columntype: 'datetimeinput', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: productGridCellclass,
				cellsrenderer: function (row, column, value){
					var rowData = grid.jqxGrid('getrowdata', row);
					if (listProductAdds.length > 0){
				    	$.each(listProductAdds, function(i){
			   				var olb = listProductAdds[i];
			   				if (olb.productId == rowData.productId ){
			   					value = olb.thruDate;
			   					return false;
			   				}
			   			});
				    }
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
				    var data = grid.jqxGrid('getrowdata', cell.row);
				    if (data.fromDate){
				    	var exp = new Date(data.fromDate);
				    	if (exp > new Date(value)){
					        return { result: false, message: uiLabelMap.FromDateMustBeBeforeThruDate};
					    }
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
				{ name: 'requireAmount', type: 'String'},
				{ name: 'fromDate', type: 'date', other: 'Timestamp' },
				{ name: 'thruDate', type: 'date', other: 'Timestamp'}
				]
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
				url: 'JQGetPOListProducts',                
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
				if (dataField == 'fromDate'){
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
				if (dataField == 'thruDate'){
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
		});
		
		gridProductAdd.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData){
				if (dataField == 'fromDate'){
					$.each(listProductAdds, function(i){
		   				var olb = listProductAdds[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductAdds.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value != null ){
						var item = $.extend({}, rowData);
						item.fromDate = value;
						listProductAdds.push(item);
					} 
				}
				if (dataField == 'thruDate'){
					$.each(listProductAdds, function(i){
		   				var olb = listProductAdds[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductAdds.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value != null ){
						var item = $.extend({}, rowData);
						item.thruDate = value;
						listProductAdds.push(item);
					} 
				} 
			}	 
		});
		
		$("#addProductSave").on('click', function (event) {
			if (listProductAdds.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			
			for (var i in listProductAdds){
				if (listProductAdds[i].fromDate == null){
					jOlbUtil.alert.error(uiLabelMap.MissingFromDate);
					return false;
				}
				if (listProductAdds[i].thruDate == null){
					jOlbUtil.alert.error(uiLabelMap.MissingThruDate);
					return false
				}	
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
							listProductSelected[j].fromDate = listProductAdds[i].fromDate;
							listProductSelected[j].thruDate = listProductAdds[i].thruDate;
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
				listProductAdds = [];
				updateProductGridData(listProductSelected);
				popupAddProduct.jqxWindow('close');
			}
		});
		
		
		$("#alterSave").on('click', function (event) {
			var resultValidate = false;
			var resultValidate = !validatorVAL.validate();
			if(resultValidate) return false;
			if (listProductSelected.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			for (var i in listProductSelected){
				if (listProductSelected[i].fromDate == null && listProductSelected[i].thruDate != null){
					jOlbUtil.alert.error(uiLabelMap.MissingFromDate);
					return false;
				}
				if (listProductSelected[i].thruDate == null && listProductSelected[i].fromDate != null){
					jOlbUtil.alert.error(uiLabelMap.MissingThruDate);
					return false
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
			   		if (data.fromDate == null && data.thruDate == null){
			   			if (data.eventItemSeqId){
							map['eventItemSeqId'] = data.eventItemSeqId;
							listProductCancels.push(map);
			   			}
			   		}
			   		else if (typeof data.fromDate != 'number'){
			   			map['fromDate'] = data.fromDate.getTime();
				   		map['thruDate'] = data.thruDate.getTime();
			   		}
			   		else {
			   			map['fromDate'] = data.fromDate;
				   		map['thruDate'] = data.thruDate;
			   		}
			   		
					if (data.fromDate ){
						if (data.eventItemSeqId){
							map['eventItemSeqId'] = data.eventItemSeqId;
							listProductUpdates.push(map);
						} else {
							listProductNews.push(map);
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
					                location.reload()
					    		}
					    	});
						Loading.hide('loadingMacro');
				    	}, 500);
				    }
				}]);
		 });
	}
	var getValidator = function(){
    	return validatorVAL;
    };
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
    		resetPopupAdd();
        	popupAddProduct.jqxWindow('open');
    	}
    }
	
    var openPopupEdit = function (rowData){
    	if (rowData){
    		resetData();
    		selectedData = $.extend({}, rowData);
    		$("#executedDate").jqxDateTimeInput('setDate', new Date(rowData.executedDate));
    		if (rowData.completedDate != null){
    			
    			$("#completedDate").jqxDateTimeInput('setDate', new Date(rowData.completedDate));
    		}
    		
    		$("#eventCode").jqxInput('val', rowData.eventCode); 
    		$("#eventName").jqxInput('val', rowData.eventName); 
    		$("#description").jqxInput('val', rowData.description); 
    		
    		listProductSelected = [];
    		getProductByEvent(rowData.eventId);
        	
    	}
    }
    
    var resetData = function(){
		$("#executedDate").jqxDateTimeInput('setDate', null)
		$("#completedDate").jqxDateTimeInput('setDate', null);
		$("#eventCode").jqxInput('val', null); 
		$("#eventName").jqxInput('val', null); 
		$("#description").jqxInput('val', null);
    }
    var resetPopupAdd = function(){
    	gridProductAdd.jqxGrid('gotopage', 0);
    	gridProductAdd.jqxGrid('clearselection');
    	gridProductAdd.jqxGrid('updatebounddata');
    }
    
    
	var updateProductGridData = function (data) {
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
					 listProductSelected = res.listProducts;
					 updateProductGridData(listProductSelected);
					 popupEdit.jqxWindow('open');
				 }
			 },
			 error: function(response){
				 
			 }
 		}).done(function(data) {
	 			
 		});
		return listProduct;
	}
	
	var productGridCellclass = function (row, column, value, data) {
		var data = gridProductAdd.jqxGrid('getrowdata',row);
    	if (data.fromDate !=null || data.thruDate != null) {
    		return 'background-prepare';
    	}
	}
	
	var Cellclass = function (row, column, value, data) {
		var data = gridProduct.jqxGrid('getrowdata',row);
    	if (data.fromDate !=null || data.thruDate != null) {
    		return 'background-prepare';
    	}
	}
	
	return {
		init : init,
		openPopupEdit: openPopupEdit,
		openPopupAddProduct: openPopupAddProduct,
		getValidator : getValidator,
	}
}());