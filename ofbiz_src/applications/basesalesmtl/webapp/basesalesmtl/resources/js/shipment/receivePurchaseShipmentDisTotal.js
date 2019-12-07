$(function(){
	smtReceiveObj.init();
});
var smtReceiveObj = (function() {
	var btnClick = false;
	var dropdownDlv = $("#txtGridDelivery");
	var gridDlv = $("#jqxgridDelivery");
	var dropdownFacility = $("#txtGridFacility");
	var gridFacility = $("#jqxgridFacility");
	var validatorVAL = null;
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function() {
		$("#splitterProduct").hide();
		$("#jqxGridProduct").hide();
	};
	
	var initElementComplex = function() {
		initSaleDeliveryGrid(dropdownDlv, gridDlv, 600);
		initFacilityGrid(dropdownFacility, gridFacility, 600);
		
		if (deliveryId != null) {
			updateProductGridData(deliveryId);
			Grid.setDropDownValue(dropdownDlv, deliveryId, deliveryId);
			dropdownDlv.trigger("close");
			dropdownDlv.jqxDropDownButton('disabled', true);
			dropdownFacility.jqxDropDownButton('disabled', true);
		}
	};
	
	var initSaleDeliveryGrid = function (dropdown, grid, width) {
		var datafields =
			[
				{ name: "orderId", type: "string" },
				{ name: "deliveryId", type: "string" },
				{ name: "createDate", type: "date", other: "Timestamp"},
			];
		var columns =
			[
				{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: uiLabelMap.BSOrderId, datafield: "orderId", width: 130 },
				{ text: uiLabelMap.DeliveryDoc, datafield: "deliveryId", width: 130 },
				{ text: uiLabelMap.CreatedDate, dataField: "createDate", minwidth: 150, cellsformat:'dd/MM/yyyy', filtertype:"range",
				}
			];
			GridUtils.initDropDownButton({url: "jqGetListDeliverySalesDisReceive", autorowheight: true, filterable: true, showfilterrow: true,
				width: 600,  source: {pagesize: 5}, selectionmode: "singlerow", closeOnSelect: "Y", disabled: true,
					handlekeyboardnavigation: function (event) {
						var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
						if (key == 70 && event.ctrlKey) {
							soGrid.jqxGrid("clearfilters");
							return true;
						}
				 	}, clearOnClose: false, dropdown: {width: 300}
			 	}, 
			 	datafields, 
			 	columns, null, grid, dropdown, "deliveryId", null);
	};
	
	var initFacilityGrid = function (dropdown, grid, width) {
		var datafields =  [
		{name: 'facilityId', type: 'string'},
		{name: 'facilityCode', type: 'string'},
		{name: 'facilityName', type: 'string'},
		{name: 'requireDate', type: 'string'},
    	];
    	var columns = [
			{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
				cellsrenderer: function (row, column, value) {
					if (!value) {
						var data = grid.jqxGrid('getrowdata', row);
						value = data.facilityId;
					}
			        return '<div style="cursor:pointer;">' + (value) + '</div>';
			    }
			},
			{text: uiLabelMap.FacilityName, datafield: 'facilityName', width: '80%',
				cellsrenderer: function (row, column, value) {
			        return '<div style="cursor:pointer;">' + (value) + '</div>';
			    }
			},
    	];
		GridUtils.initDropDownButton({url: "jqGetFacilities", autorowheight: true, filterable: true, showfilterrow: true,
			width: 600, source: {pagesize: 5}, selectionmode: "singlerow", closeOnSelect: "N",
			handlekeyboardnavigation: function (event) {
				var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
				if (key == 70 && event.ctrlKey) {
					soGrid.jqxGrid("clearfilters");
					return true;
				}
			}, clearOnClose: false, dropdown: {width: 300}
		}, 
		datafields, 
		columns, null, grid, dropdown, "facilityCode", null);
	};
	
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				var resultValidate = !validatorVAL.validate();
				if(resultValidate) {
					return false;
					}
				checkRequireDate();
				listProductSelected = [];
				updateProductGridData(deliveryId);
			} else if(info.step == 2 && (info.direction == "next")) {
				// check form valid
				$('#containerNotify').empty();
				if (listProductSelected.length <= 0){
					jOlbUtil.alert.error(uiLabelMap.DAYouNotYetChooseProduct);
					return false;
				}
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureYouWantToImport, function() {
				if (!btnClick){
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishReceiveDelivery();
		            	Loading.hide('loadingMacro');
	            	}, 500);
	            	btnClick = true;
				} 
            }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
            	btnClick = false;
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
		
		dropdownDlv.on('close', function (event) {
			deliveryId = Grid.getDropDownValue(dropdownDlv).toString();
		});
		
		dropdownFacility.on('close', function (event) {
			facilityId = Grid.getDropDownValue(dropdownFacility).toString();
		});
		
		gridFacility.on('rowselect', function (event) {
		    var args = event.args;
		    var rowData = args.row;
		    requireDate = rowData.requireDate;
		});
		
		gridDlv.on('bindingcomplete', function (event) {
			if (deliveryId != null){
				var rows = gridDlv.jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.deliveryId == deliveryId){
							var index = gridDlv.jqxGrid('getrowboundindexbyid', data1.uid);
							gridDlv.jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		gridFacility.on('bindingcomplete', function (event) {
			if (facilityId == null){
				var rows = gridFacility.jqxGrid('getrows');
				if (rows.length > 0){
					var data1 = rows[0];
					facilityId = data1.facilityId;
					requireDate = data1.requireDate;
					facilityCode = data1.facilityCode;
					var index = gridFacility.jqxGrid('getrowboundindexbyid', data1.uid);
					gridFacility.jqxGrid('selectrow', index);
					Grid.setDropDownValue(dropdownFacility, facilityId, facilityCode);
					dropdownFacility.trigger("close");
				}
			}
		});
		
		gridDlv.on('rowdoubleclick', function (event) { 
			dropdownDlv.jqxDropDownButton('close');
		});
		
		gridFacility.on('rowdoubleclick', function (event) { 
			dropdownFacility.jqxDropDownButton('close');
		});
		
		$("#btnUpload").on('click', function(){
			deliveryId = Grid.getDropDownValue(dropdownDlv).toString();
			facilityId = Grid.getDropDownValue(dropdownFacility).toString();
			updateProductGridData(deliveryId);
		});
		
	};
	
	var checkRequireDate = function (){
		if (requireDate == 'Y'){
			$("#splitterProduct").show();
			$("#jqxGridProduct").hide();
		} else {
			$("#splitterProduct").hide();
			$("#jqxGridProduct").show();
		}
	}
	
	var updateProductGridData = function (deliveryId){
		var listProductTmps = getDeliveryItemData(deliveryId);
		if (listProductTmps.length > 0){
			var listOrSeqs = [];
			for (var i in listProductTmps){
				var seq = listProductTmps[i].fromOrderItemSeqId;
				if (listOrSeqs.indexOf(seq) >= 0) continue;
				listOrSeqs.push(seq);
			}
			if (listOrSeqs.length > 0){
				for (var i in listOrSeqs){
					var seq = listOrSeqs[i]
					var item = {};
					var total = 0;
					for (var j in listProductTmps){
						var x = listProductTmps[j];
						if (listProductTmps[j].fromOrderItemSeqId == seq){
							item = $.extend({}, x);
							item.orderId = listProductTmps[j].fromOrderId;
							item.orderItemSeqId = listProductTmps[j].fromOrderItemSeqId;
							total = total + listProductTmps[j].actualExportedQuantity;
						}
					}
					item.quantity = total;
					item.actualExportedQuantity = total;
					listProductSelected.push(item);
				}
				listProductSelected.sort(function(a, b) { 
				    return b.actualExportedQuantity - a.actualExportedQuantity;
				})
				
				PurDlvProductDate.updateGridProductLocalData(listProductSelected);
				PurDlvProduct.updateProductLocalData(listProductSelected);
			}
		}
	}
	
	var getDeliveryItemData = function(deliveryId){
		var listItems = [];
		var url = "getDeliveryItemByDeliveryId";
    	$.ajax({	
			 type: "POST",
			 url: url,
			 data: {
				 deliveryId: deliveryId,
			 },
			 dataType: "json",
			 async: false,
			 success: function(data){
				 if (data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null) {
					jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcessing + ": " +data._ERROR_MESSAGE_);
					Loading.hide("loadingMacro");
					return false;
				} else {
					if (data.listDeliveryItems){
						listItems = data.listDeliveryItems;
					}
				}
			 },
			 error: function(response){
			 }
	 		}).done(function(data) {
  		});
		return listItems;
	}
	
	function showConfirmPage(){
		if ($("#tableProduct").length > 0){
			var totalValue = 0;
			$('#tableProduct tbody').empty();
			var tableRef = document.getElementById('tableProduct').getElementsByTagName('tbody')[0];
			listProductSelected.sort(function(a, b) { 
			    return b.quantity - a.quantity;
			})
			for (var i in listProductSelected){
				var data = listProductSelected[i];
				var newRow = tableRef.insertRow(tableRef.rows.length);
				if (data.isPromo == 'Y'){
					newRow.className = 'background-promo';
				}
				var newCell0 = newRow.insertCell(0);
				var u = parseInt(i) + 1;
				var newText = document.createTextNode(u);
				newCell0.appendChild(newText);
				
				var newCell1 = newRow.insertCell(1);
				newText = document.createTextNode(data.productCode);
				newCell1.appendChild(newText);
				
				var newCell2 = newRow.insertCell(2);
				newText = document.createTextNode(data.productName);
				newCell2.appendChild(newText);
				
				var newCell3 = newRow.insertCell(3);
				newText = document.createTextNode(data.isPromo);
				newCell3.appendChild(newText);
				
				var newCell4 = newRow.insertCell(4);
				if (data.description){
					newText = document.createTextNode(data.description);
				} else {
					newText = document.createTextNode("");
				}
				newCell4.appendChild(newText);
				
				var newCell5 = newRow.insertCell(5);
				newCell5.className = 'align-right';
				if (data.requireAmount && data.requireAmount == 'Y' && data.amountUomTypeId == 'WEIGHT_MEASURE') {
					newText = document.createTextNode(getUomDesc(data.weightUomId));
				} else {
					newText = document.createTextNode(getUomDesc(data.quantityUomId));
				}
				newCell5.appendChild(newText);
				
				var newCell6 = newRow.insertCell(6);
				newText = document.createTextNode(formatnumber(data.actualExportedQuantity));
				newCell6.appendChild(newText);
				
				var newCell7 = newRow.insertCell(7);
				newCell7.className = 'align-right';
				newText = document.createTextNode(formatnumber(data.quantity));
				newCell7.appendChild(newText);
				
				var newCell8 = newRow.insertCell(8);
				newCell8.className = 'align-right';
				var u = data.unitPrice;
				if (locale && locale === 'vi' && typeof(u) === 'string'){
					u = data.unitPrice.toString().replace('.', '');
					u = u.replace(',', '.');
				}
				if (data.isPromo == 'Y'){
					var h = document.createElement("strike")
					newText = document.createTextNode(formatnumber(u));
					h.appendChild(newText);
					newCell8.appendChild(h);
				} else {
					newText = document.createTextNode(formatnumber(u));
					newCell8.appendChild(newText);
				}
				
				var newCell9 = newRow.insertCell(9);
				newCell9.className = 'align-right';
				if (data.unitPrice >=0 && data.quantity >= 0 && (!data.isPromo || data.isPromo == 'N')){
					var u = data.unitPrice;
					if (locale && locale === 'vi' && typeof(u) === 'string'){
						u = data.unitPrice.toString().replace('.', '');
						u = u.replace(',', '.');
					}
					var lastPrice = parseFloat(u.toString());
					var v = data.quantity;
					if (locale && locale === 'vi' && typeof(v) === 'string'){
						v = v.toString().replace('.', '');
						v = v.replace(',', '.');
					}
					var quantity = parseFloat(v.toString());
					
					value = lastPrice*parseFloat(quantity);
					totalValue = totalValue + value;
					if (value >= 0) {
						newText = document.createTextNode(formatnumber(value));
					} 
					newCell9.appendChild(newText);
				} else {
					newText = document.createTextNode(0);
					newCell9.appendChild(newText);
				}
			}
			if (totalValue >= 0){
				var newRowTotal = tableRef.insertRow(tableRef.rows.length);
				var newCellTotal0 = newRowTotal.insertCell(0);
				newCellTotal0.colSpan = 9;
				newCellTotal0.className = 'align-right';
				newCellTotal0.style.fontWeight="bold";
				newCellTotal0.style.background="#f2f2f2";
				var str = uiLabelMap.OrderItemsSubTotal.toUpperCase();
				var newTextTotal = document.createTextNode(str);
				newCellTotal0.appendChild(newTextTotal);
				
				var newCellTotal9 = newRowTotal.insertCell(1);
				newCellTotal9.className = 'align-right';
				newCellTotal9.style.background="#f2f2f2";
				var newTextTotal = document.createTextNode(formatnumber(totalValue));
				newCellTotal9.appendChild(newTextTotal);
			}
		}
	}
	
	function finishReceiveDelivery(){
		if (facilityId && deliveryId && listProductSelected.length > 0){
			
			var listProducts = [];
			var listProductAttributes = [];
			if (listProductSelected != undefined && listProductSelected.length > 0){
				for (var i = 0; i < listProductSelected.length; i ++){
					var data = listProductSelected[i];
					if (data.quantity > 0){
						var map = {};
						var items = listProductMap[data.orderItemSeqId];
						if (items){
							if (items.length > 0){
								for (var x in items){
									var myObj = items[x];
									for(var keys in myObj){
										if (myObj[keys] === null || myObj[keys] === "" || myObj[keys] === "null" || myObj[keys] === undefined){
											delete myObj[keys]
										}
									}
									listProductAttributes.push(myObj);
								}
							}
						}
				   		map['productId'] = data.productId;
				   		map['quantity'] = data.quantity;
				   		map['deliveryId'] = data.deliveryId;
				   		map['deliveryItemSeqId'] = data.deliveryItemSeqId;
				   		map['orderItemSeqId'] = data.orderItemSeqId;
				        listProducts.push(map);
					}
				}
			}
			listProducts = JSON.stringify(listProducts);
			listProductAttributes = JSON.stringify(listProductAttributes);
			
	    	var url = "receiveProductFromPurchaseShipmentDis";
	    	$.ajax({	
				 type: "POST",
				 url: url,
				 data: {
					 deliveryId: deliveryId,
					 facilityId: facilityId,
					 listDeliveryItems: listProducts,
					 listProductAttributes: listProductAttributes,
				 },
				 dataType: "json",
				 async: false,
				 success: function(data){
					window.location.href = "listPurchaseShipmentDis";
				 },
				 error: function(response){
					window.location.href = "listPurchaseShipmentDis";
				 }
		 		}).done(function(data) {
	  		});
		}
	}
	
	var initValidateForm = function(){
		var extendRules = [
			{input: '#txtGridDelivery', message: uiLabelMap.FieldRequired, action: 'change', position: 'right',
				rule: function(input, commit){
					if (deliveryId == null){
						return false;
					}
					return true;
				}
			},
			{input: '#txtGridFacility', message: uiLabelMap.FieldRequired, action: 'change', position: 'right',
				rule: function(input, commit){
					if (facilityId == null){
						return false;
					}
					return true;
				}
			},
             ];
  		var mapRules = [
              ];
  		validatorVAL = new OlbValidator($('#pickDeliveryForm'), mapRules, extendRules, {position: 'right'});
	};
	
	return {
		init: init,
	}
}());