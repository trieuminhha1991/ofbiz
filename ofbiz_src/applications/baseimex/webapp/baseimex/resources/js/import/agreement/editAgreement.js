$(document).ready(function() {
	ObjEditAgreement.init();
});
var ObjEditAgreement = (function() {
	var gridProduct = $("#listProduct");   
	var popupEdit = $("#popupEdit");
	var gridProductAdd = $("#jqxGridProductAdds");  
	var popupAddProduct = $("#popupAddProduct"); 
	var uomCurrency = null;
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
		$("#agreementCode").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#agreementName").jqxInput({width: 295, height: 23, theme: theme}); 
		$("#agreementDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#fromDate").jqxDateTimeInput({width: 300, theme: theme}); 
		$("#thruDate").jqxDateTimeInput({width: 300, theme: theme});
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
			height : 350,
			minWidth : 600,
			minHeight : 200,
			maxWidth : 1400,
			maxHeight : 700,
			resizable : true,
			cancelButton : $("#alterCancelAdd"),
			keyboardNavigation : true,
			keyboardCloseKey : 15,
			isModal : true,
			autoOpen : false,
			modalOpacity : 0.7,
			theme : theme
		});	
	}
	
	var initElementComplex = function() {
		initGridProduct(gridProduct, rendertoolbarProduct);
		initGridProductAdd(gridProductAdd, rendertoolbarProductAdd);	
		
	}
	
	var getProductByAgreement = function (agreementId) {
		$.ajax({	
			 type: "POST",
			 url: "getProductByAgreement",
			 data: {
				 agreementId: agreementId
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
					 listProductSelected = res.listProducts;
					 updateProductGridData(listProductSelected);
					 popupEdit.jqxWindow('open');
				 }
			 },
			 error: function(response){
				 
			 }
 		}).done(function(data) {
	 			
 		});
	}
	function loadAjaxProductAdd(partyIdToFirst, uomCurency){
		if (partyIdToFirst && uomCurency) {
			var tmpS = gridProductAdd.jqxGrid("source");
			tmpS._source.url = "jqxGeneralServicer?sname=JQListProductBySupplier&getQuota=Y&supplierId="
				+ partyIdToFirst + "&currencyUomId=" + uomCurency;
			gridProductAdd.jqxGrid("updatebounddata");
			
		}
	}
	
	
	var rendertoolbarProduct = function (toolbar){
		toolbar.html("");
		var id = "ProductList";
		var me = this;
		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
		var container = $('#toolbarButtonContainer' + id);
        var maincontainer = $("#toolbarcontainer" + id);
        var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.CommonAdd + "@javascript:void(0)@ObjEditAgreement.openPopupAddProduct()";
        Grid.createCustomControlButton(gridProduct, container, customcontrol1);
	}; 
	var rendertoolbarProductAdd = function (toolbar){
		toolbar.html("");
		var id = "ProductList";
		var me = this;
		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.Product + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
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
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
						var item = $.extend({}, rowData);
						item.quantity = value;
						item.valueTotal = item.quantity * item.lastPrice;
						listProductSelected.push(item);
					
				} 
				if (dataField == 'lastPrice' && rowData.quantity > 0){
					$.each(listProductSelected, function(i){
		   				var olb = listProductSelected[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductSelected.splice(i,1);
		   					return false;
		   				}
		   			});
				
					var item = $.extend({}, rowData);
					item.lastPrice = value;
					item.valueTotal = item.quantity * item.lastPrice;
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
				if (dataField == 'quantity'){
					$.each(listProductAdds, function(i){
		   				var olb = listProductAdds[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductAdds.splice(i,1);
		   					return false;
		   				}
		   			});
					if (value > 0 && value >= rowData.minimumOrderQuantity){
						var item = $.extend({}, rowData);
						item.quantity = value;
						item.valueTotal = item.quantity * item.lastPrice;
						listProductAdds.push(item);
					} 
				}
				if (dataField == 'lastPrice' && rowData.quantity > 0){
					$.each(listProductAdds, function(i){
		   				var olb = listProductAdds[i];
		   				if (olb.productId == rowData.productId ){
		   					listProductAdds.splice(i,1);
		   					return false;
		   				}
		   			});
				
					var item = $.extend({}, rowData);
					item.lastPrice = value;
					item.valueTotal = item.quantity * item.lastPrice;
					listProductAdds.push(item);
				} 
			}	 
		});
		
		$("#addProductSave").on('click', function (event) {
			if (listProductAdds.length <= 0){
				jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
				return false;
			}
			var listRemove = [];
			if (listProductAdds != undefined && listProductAdds.length > 0 ){
				if (listProductSelected.length > 0){
					for (var i in listProductAdds){
						var data1 = listProductAdds[i];
						var check = false;
						for (var j in listProductSelected){
							var data2 = listProductSelected[j];
							if (data1.productId == data2.productId){
								check = false;
								listProductSelected[j].quantity = listProductAdds[i].quantity;
								listProductSelected[j].lastPrice = listProductAdds[i].lastPrice;
								listRemove.push(data1);
							}
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
			
			var listProductUpdates = [];
			var listProductCancels = [];
			var listProductNews = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					var map = {};
					map['lastPrice'] = data['lastPrice'];
					map['quantity'] = data['quantity'];
					map['productId'] = data['productId'];
					map['currencyUomId'] = data['currencyUomId'];
					
					console.log(map);
					if (data.quantity >0){
						if (data.agreementItemSeqId){
							map['agreementItemSeqId'] = data.agreementItemSeqId;
							listProductUpdates.push(map);
						} else {
							listProductNews.push(map);
						}
					} else {
						if (data.agreementItemSeqId){
							map['agreementItemSeqId'] = data.agreementItemSeqId;
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
							if ($("#agreementName").jqxInput('val')){
								data.agreementName = $("#agreementName").jqxInput('val');
							}
							if ($("#agreementCode").jqxInput('val')){
								data.agreementCode = $("#agreementCode").jqxInput('val');
							}
							
							var x = $("#agreementDate").jqxDateTimeInput('getDate');
							if (x){
								data.agreementDate = x.getTime();
							}
								
							var y = $("#fromDate").jqxDateTimeInput('getDate');
							if (y){
								data.fromDate = y.getTime();
							}
							var z = $("#thruDate").jqxDateTimeInput('getDate');
							if (z){
								data.thruDate = z.getTime();
							}
							data.listProductUpdates = listProductUpdates;
							data.listProductCancels = listProductCancels;
							data.listProductNews = listProductNews;
							data.agreementId = agreement.agreementId;
							$.ajax({
					    		url: "editPurchaseAgreement",
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
	var updateProductGridData = function (data) {
		for ( i in data){
			if ( data[i].quantity && data[i].lastPrice){
				data[i].valueTotal = data[i].quantity * data[i].lastPrice;
			}
		}
		var tmpS = gridProduct.jqxGrid("source");
		tmpS._source.localdata = data;
		gridProduct.jqxGrid("source", tmpS);
	}
	var getValidator = function(){
    	return validatorVAL;
    };
	var initValidateForm = function(){
		var extendRules = [
			       			{
			       				input: '#agreementCode', 
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
			       			{
			       				input: '#thruDate', 
			       			    message: uiLabelMap.ThruDateMustGreaterThanFromDate, 
			       			    action: 'valueChanged', 
			       			    position: 'right',
			       			    rule: function (input) {
			 			         	if($('#fromDate').jqxDateTimeInput('getDate') != null && $('#thruDate').jqxDateTimeInput('getDate') != null){
			 				        var fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime(); 
			 				        var thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
			 				        	if(fromDate < thruDate){
			 				        		return true; 
			 				        	}else{
			 				        		return false; 
			 				        	}
			       			    	}
			 					},
			       			},
			       			{
			       				input: '#fromDate', 
			       			    message: uiLabelMap.ThruDateMustGreaterThanFromDate, 
			       			    action: 'valueChanged', 
			       			    position: 'right',
			       			    rule: function (input) {
			 			         	if($('#fromDate').jqxDateTimeInput('getDate') != null && $('#thruDate').jqxDateTimeInput('getDate') != null){
			 				        var fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime(); 
			 				        var thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
			 				        	if(fromDate < thruDate){
			 				        		return true; 
			 				        	}else{
			 				        		return false; 
			 				        	}
			 			         		   
			 			         	}    
			 					},
			       			},
			       			{
			       				input: '#agreementDate', 
			       			    message: uiLabelMap.AgreementDateMustBeforeFromDate, 
			       			    action: 'valueChanged', 
			       			    position: 'right',
			       			    rule: function (input) {
			 			         	if($('#fromDate').jqxDateTimeInput('getDate') != null && $('#agreementDate').jqxDateTimeInput('getDate') != null){
			 				        var fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime(); 
			 				        var agreementDate = $('#agreementDate').jqxDateTimeInput('getDate').getTime();
			 				        	if(fromDate > agreementDate){
			 				        		return true; 
			 				        	}else{
			 				        		return false; 
			 				        	}
			 			         		   
			 			         	}    
			 					},
			       			},
			       		];
		
	   		var mapRules = [
	   				{input: '#fromDate', type: 'validInputNotNull'},
	   				{input: '#thruDate', type: 'validInputNotNull'},
	   				{input: '#agreementName', type: 'validInputNotNull'},
	   				{input: '#agreementCode', type: 'validInputNotNull'},
	   				{input: '#agreementDate', type: 'validInputNotNull'},
	               ];
	   		validatorVAL = new OlbValidator($('#popupEdit'), mapRules, extendRules, {position: 'right'});
	};
	
    var openPopupEdit = function (){
    	$("#agreementDate").jqxDateTimeInput('setDate', new Date(agreement.agreementDate));
    	$("#fromDate").jqxDateTimeInput('setDate', new Date(agreement.fromDate));
    	$("#thruDate").jqxDateTimeInput('setDate', new Date(agreement.thruDate));
    	$("#agreementCode").jqxInput('val', agreement.agreementCode); 
    	$("#agreementName").jqxInput('val', agreementAtt.attrValue); 
    	listProductSelected = [];
    	getProductByAgreement(agreement.agreementId);
    	popupEdit.jqxWindow('open');
    }
	var initGridProduct = function(grid , toolbar){
		var url = "";
		var datafield =  [
			                  {name: 'productId', type: 'string'},
			                  {name: 'productCode', type: 'string'},
			                  {name: 'productName', type: 'string'},
			                  {name: 'quantityUomId', type: 'string'},
			                  {name: 'weightUomId', type: 'string'},
			                  {name: 'requireAmount', type: 'string'},
			                  {name: 'amountUomTypeId', type: 'string'},
			                  {name: 'unit', type: 'string'},
			                  {name: 'quantity', type: 'number'},
			                  {name: 'orderedQuantity', type: 'number'},
			                  {name: 'quantityQuota', type: 'number'},
			                  {name: 'minimumOrderQuantity', type: 'number'},
			                  {name: 'planQuantity', type: 'number'},
			                  {name: 'lastPrice', type: 'number'},
			                  {name: 'valueTotal', type: 'string'},
			                  {name: 'currencyUomId', type: 'string'},
			                  {name: 'agreementItemSeqId', type: 'string'},
			                  ];
		var columnlist = [
	              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},	
	          	{datafield: 'productId', width: 100, editable: false, hidden: true, cellclassname: cellclassname},
	          	{text: uiLabelMap.ProductId, pinned: true, datafield: 'productCode', width: '10%', editable: false, cellclassname: cellclassname,
	          		cellsrenderer: function (row, column, value) {
				        return "<div style='margin:4px;'>" + value + "</div>";
				    },
	          	},
	          	{text: uiLabelMap.ProductName, pinned: true, datafield: 'productName', editable: false,  cellclassname: cellclassname, minwidth: 100,
	          		cellsrenderer: function (row, column, value) {
				        return "<div style='margin:4px;'>" + value + "</div>";
				    },
	          	},
	          	{text: uiLabelMap.Unit, datafield: 'quantityUomId', editable: false,  cellclassname: cellclassname, width: '8%',
	          		cellsrenderer: function (row, column, value) {
	          			var rowsdata = gridProduct.jqxGrid('getrowdata', row);
	          			if (rowsdata) {
	          				if (rowsdata.requirementAmount && rowsdata.requirementAmount == 'Y' && rowsdata.amountUomTypeId && rowsdata.amountUomTypeId == 'WEIGHT_MEASURE'){
	          					value = rowsdata.weightUomId;
	          				}
	          			}
	          			return "<div style='margin:4px;'>" + getUomDesc(value) + "</div>";
	          		},
	          	},
	      		{text: uiLabelMap.PlanQuantity, hidden: checkPlan(), datafield: 'planQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right',width: '12%',
	          		cellsrenderer: function(row, column, value){
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.orderedQuantity, hidden: checkPlan(), datafield: 'orderedQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '12%',
	          		cellsrenderer: function(row, column, value){
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.BIEQuota, datafield: 'quantityQuota', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
	          		cellsrenderer: function(row, column, value){
	          			if (value){
	          				return '<span class="align-right">' + formatnumber(value) +'</span>';
	          			}
	          		},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.MOQ, datafield: 'minimumOrderQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
	          		cellsrenderer: function(row, column, value){
	          			if (value){
	          				return '<span class="align-right">' + formatnumber(value) +'</span>';
	          			}
	          		},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.OrderQuantityEdit, datafield: 'quantity', editable: true, filterable: false, align: 'left', cellsalign: 'right',columntype: 'numberinput',width: '10%', cellclassname: cellclassname,
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			
	          			if (listProductSelected.length > 0 && rowData != undefined ){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
	  					if (value > 0){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					} else {
	  						return '<span class="align-right"></span>';
	  					} 
	  				},
	  				initeditor: function (row, cellvalue, editor) {
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
					},
					validation: function (cell, value) {
						var rowData = grid.jqxGrid('getrowdata', cell.row);
						if (value < 0) {
							return { result: false, message: uiLabelMap.DAQuantityMustBeGreaterThanZero };
						}
						if (value > 0 && value < rowData.minimumOrderQuantity) {
							return { result: false, message: uiLabelMap.DmsRestrictQuantityPO };
						}
						return true;
					},
	          	},
	          	{text: uiLabelMap.unitPrice, datafield: 'lastPrice', editable: true, filterable: false, columntype: 'numberinput', cellsalign: 'right',width: '10%', cellclassname: cellclassname,
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			if (listProductSelected.length > 0 && rowData != undefined){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.lastPrice;
				   					return false;
				   				}
				   			});
					    }
	          			if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},
	  				initeditor: function (row, cellvalue, editor) {
						var rowData = grid.jqxGrid('getrowdata', row);
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						var rowData = grid.jqxGrid('getrowdata', row);
						if (!cellvalue) {
							if (listProductSelected.length > 0 && rowData != undefined){
						    	$.each(listProductSelected, function(i){
					   				var olb = listProductSelected[i];
					   				if (olb.productId == rowData.productId ){
					   					cellvalue = olb.lastPrice;
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
	          	},
	          	{text: uiLabelMap.DAItemTotal, datafield: 'valueTotal', editable: false, filterable: false, width: '12%',  cellclassname: cellclassname, cellsalign: 'right',
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			if (rowData != undefined){
	          				if (rowData.quantity && rowData.lastPrice ) {
	          					value = rowData.quantity * rowData.lastPrice;
	          				}
	          			}
	          			if (listProductSelected.length > 0 && rowData != undefined){
					    	$.each(listProductSelected, function(i){
				   				var olb = listProductSelected[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.valueTotal;
				   					return false;
				   				}
				   			});
					    }
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},
	          	}
	          ];
			var virtualMode = true;
//			if (customTimePeriodId && productPlanId){
//				virtualMode = false;
//			}
			var config = {
					rendertoolbar: toolbar,
					datafields: datafield,
					columns: columnlist,
					width: '100%',
					height: 'auto',
					sortable: true,
					editable: true,
					filterable: true,
					pageable: true,
					showfilterrow: true,
					useUtilFunc: false,
					useUrl: false,
					url: '',
					groupable: false,
					showgroupsheader: false,
					showaggregates: false,
					showstatusbar: false,
					virtualmode:virtualMode,
					showdefaultloadelement:true,
					autoshowloadelement:true,
					showtoolbar:true,
					columnsresize: true,
					isSaveFormData: true,
					formData: "filterObjData",
					selectionmode: "singlerow",
					bindresize: true,
					pagesize: 10,
					editmode: 'click',
				};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initGridProductAdd = function(grid , toolbar){
		var url = "";
		var datafield =  [
			                  {name: 'productId', type: 'string'},
			                  {name: 'productCode', type: 'string'},
			                  {name: 'productName', type: 'string'},
			                  {name: 'quantityUomId', type: 'string'},
			                  {name: 'weightUomId', type: 'string'},
			                  {name: 'requireAmount', type: 'string'},
			                  {name: 'amountUomTypeId', type: 'string'},
			                  {name: 'unit', type: 'string'},
			                  {name: 'quantity', type: 'number'},
			                  {name: 'orderedQuantity', type: 'number'},
			                  {name: 'quantityQuota', type: 'number'},
			                  {name: 'minimumOrderQuantity', type: 'number'},
			                  {name: 'planQuantity', type: 'number'},
			                  {name: 'lastPrice', type: 'number'},
			                  {name: 'valueTotal', type: 'string'},
			                  {name: 'currencyUomId', type: 'string'},
			                  ];
		var columnlist = [
	              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},	
	          	{datafield: 'productId', width: 100, editable: false, hidden: true, cellclassname: cellclassname},
	          	{text: uiLabelMap.ProductId, pinned: true, datafield: 'productCode', width: '10%', editable: false, cellclassname: cellclassname,
	          		cellsrenderer: function (row, column, value) {
				        return "<div style='margin:4px;'>" + value + "</div>";
				    },
	          	},
	          	{text: uiLabelMap.ProductName, pinned: true, datafield: 'productName', editable: false,  cellclassname: cellclassname, minwidth: 100,
	          		cellsrenderer: function (row, column, value) {
				        return "<div style='margin:4px;'>" + value + "</div>";
				    },
	          	},
	          	{text: uiLabelMap.Unit, datafield: 'quantityUomId', editable: false,  cellclassname: cellclassname, width: '8%',
	          		cellsrenderer: function (row, column, value) {
	          			var rowsdata = gridProduct.jqxGrid('getrowdata', row);
	          			if (rowsdata) {
	          				if (rowsdata.requirementAmount && rowsdata.requirementAmount == 'Y' && rowsdata.amountUomTypeId && rowsdata.amountUomTypeId == 'WEIGHT_MEASURE'){
	          					value = rowsdata.weightUomId;
	          				}
	          			}
	          			return "<div style='margin:4px;'>" + getUomDesc(value) + "</div>";
	          		},
	          	},
	      		{text: uiLabelMap.PlanQuantity, hidden: checkPlan(), datafield: 'planQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right',width: '12%',
	          		cellsrenderer: function(row, column, value){
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.orderedQuantity, hidden: checkPlan(), datafield: 'orderedQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '12%',
	          		cellsrenderer: function(row, column, value){
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.BIEQuota, datafield: 'quantityQuota', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
	          		cellsrenderer: function(row, column, value){
	          			if (value){
	          				return '<span class="align-right">' + formatnumber(value) +'</span>';
	          			}
	          		},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.MOQ, datafield: 'minimumOrderQuantity', editable: false, filterable: false, align: 'left', cellsalign: 'right', width: '10%',
	          		cellsrenderer: function(row, column, value){
	          			if (value){
	          				return '<span class="align-right">' + formatnumber(value) +'</span>';
	          			}
	          		},  cellclassname: cellclassname
	          	},
	          	{text: uiLabelMap.OrderQuantityEdit, datafield: 'quantity', editable: true, filterable: false, align: 'left', cellsalign: 'right',columntype: 'numberinput',width: '10%', cellclassname: cellclassname,
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			if (listProductAdds.length > 0){
					    	$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.quantity;
				   					return false;
				   				}
				   			});
					    }
	  					if (value > 0){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					} else {
	  						return '<span class="align-right"></span>';
	  					} 
	  				},
	  				initeditor: function (row, cellvalue, editor) {
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
					},
					validation: function (cell, value) {
						var rowData = grid.jqxGrid('getrowdata', cell.row);
						if (value < 0) {
							return { result: false, message: uiLabelMap.DAQuantityMustBeGreaterThanZero };
						}
						if (value > 0 && value < rowData.minimumOrderQuantity) {
							return { result: false, message: uiLabelMap.DmsRestrictQuantityPO };
						}
						return true;
					},
	          	},
	          	{text: uiLabelMap.unitPrice, datafield: 'lastPrice', editable: true, filterable: false, columntype: 'numberinput', cellsalign: 'right',width: '10%', cellclassname: cellclassname,
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			if (listProductAdds.length > 0){
					    	$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.lastPrice;
				   					return false;
				   				}
				   			});
					    }
	          			if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},
	  				initeditor: function (row, cellvalue, editor) {
						var rowData = grid.jqxGrid('getrowdata', row);
							editor.jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0 });
						var rowData = grid.jqxGrid('getrowdata', row);
						if (!cellvalue) {
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
							var u = cellvalue.toString().replace('.', ',');
							editor.jqxNumberInput('val', u);
						}
					},
	          	},
	          	{text: uiLabelMap.DAItemTotal, datafield: 'valueTotal', editable: false, filterable: false, width: '12%',  cellclassname: cellclassname, cellsalign: 'right',
	          		cellsrenderer: function(row, column, value){
	          			var rowData = grid.jqxGrid('getrowdata', row);
	          			if (rowData.quantity && rowData.lastPrice) {
	          				value = rowData.quantity * rowData.lastPrice;
	          			}
	          			if (listProductAdds.length > 0){
					    	$.each(listProductAdds, function(i){
				   				var olb = listProductAdds[i];
				   				if (olb.productId == rowData.productId ){
				   					value = olb.valueTotal;
				   					return false;
				   				}
				   			});
					    }
	  					if (value){
	  						return '<span class="align-right">' + formatnumber(value) +'</span>';
	  					}
	  				},
	          	}
	          ];
			var virtualMode = true;
//			if (customTimePeriodId && productPlanId){
//				virtualMode = false;
//			}
			var config = {
					rendertoolbar: toolbar,
					datafields: datafield,
					columns: columnlist,
					width: '100%',
					height: 'auto',
					sortable: true,
					editable: true,
					filterable: true,
					pageable: true,
					showfilterrow: true,
					useUtilFunc: false,
					useUrl: false,
					url: '',
					groupable: false,
					showgroupsheader: false,
					showaggregates: false,
					showstatusbar: false,
					virtualmode:virtualMode,
					showdefaultloadelement:true,
					autoshowloadelement:true,
					showtoolbar:true,
					columnsresize: true,
					isSaveFormData: true,
					formData: "filterObjData",
					selectionmode: "singlerow",
					bindresize: true,
					pagesize: 10,
					editmode: 'click',
				};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
    var cellclassname = function (row, column, value) {
 	  	if (column == 'quantity' || column == 'lastPrice') {
    		return 'background-prepare';
    	}
	};
	var checkPlan = function (){
//		if (customTimePeriodId && productPlanId) return false;
		return true;
	}
	var openPopupAddProduct = function (){
		var tmpS = gridProduct.jqxGrid("source");
		uomCurrency = tmpS._source.records[0].currencyUomId;
		loadAjaxProductAdd(agreement.partyIdTo, uomCurrency);
//    	resetPopupAdd();
        popupAddProduct.jqxWindow('open');
    	
    }
	
	return {
		init : init,
		openPopupEdit: openPopupEdit,
		openPopupAddProduct: openPopupAddProduct,
		getValidator : getValidator,
	}
}());