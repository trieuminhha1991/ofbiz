$(function(){
	ReceiveReturnObj.init();
});
var ReceiveReturnObj = (function(){
	var btnClick = false;
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		
	};
	var initInputs = function (){
		
		$("#entryDate").text(formatFullDate(new Date(entryDate)));
		$('#destinationFacilityId').jqxDropDownList({disabled: true, placeHolder: uiLabelMap.PleaseSelectTitle, width: 150, dropDownHeight: 150, selectedIndex: 0, source: facilityReturnData, theme: 'olbius', displayMember: 'description', valueMember: 'facilityId',});
		$('#destinationFacilityId').jqxDropDownList('val', facilityEnoughId);
		var curFacilityId = $('#destinationFacilityId').jqxDropDownList('val');
		$("#notifyUpdateSuccess").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: false, template: "success"
	    });
		initReturnItem();
		var listReturnItems = [];
		$.ajax({
				 type: "POST",
				 url: "getReturnItems",
				 data: {
					 returnId: returnId,
				 },
				 dataType: "json",
				 async: false,
				 success: function(data){
					 listReturnItems = data.listReturnItems;
				 },
	 		}).done(function(data) {
 		});
		listReturnItemData = listReturnItems;
		listReturnItems = prepareData(listReturnItems, listInv);
		loadReturnItem(listReturnItems);
	};
	
	var prepareData = function(listReturnItems, listInv){
		if (listInv.length > 0) {
			var arrReturnItem = listReturnItems.slice();
			listReturnItems = [];
			var listInvTmp1 = [];
			var listInvTmp2 = [];
			for (var d in listInv) {
			    if(listInv[d].facilityId === facilityEnoughId) {
                    if (listInv[d].expireDate === null || listInv[d].expireDate === undefined || listInv[d].expireDate === "" || listInv[d].expireDate === "null") {
                        listInvTmp1.push(listInv[d]);
                    } else {
                        listInvTmp2.push(listInv[d]);
                    }
                }
			}

			listInvTmp2.sort(function (a,b) {
				return a.expireDate.time - b.expireDate.time;
			});

			listInv = [];
			for (var q in listInvTmp2) {
				listInv.push(listInvTmp2[q]);
			}
			for (var p in listInvTmp1) {
				listInv.push(listInvTmp1[p]);
			}
			for (var m in arrReturnItem) {
				var item = arrReturnItem[m];
				var remainQty = 0;
				var requireAmount = item.requireAmount;
				if (requireAmount && 'Y' == requireAmount){
					arrReturnItem[m]["exportQuantity"] = arrReturnItem[m].returnAmount;
					remainQty = arrReturnItem[m].returnAmount;
				} else {
					arrReturnItem[m]["exportQuantity"] = arrReturnItem[m].returnQuantity;
					remainQty = arrReturnItem[m].returnQuantity;
				}
				var expQuantity = item.exportQuantity;
				var productId = arrReturnItem[m].productId
				var fst = true;
				
				var check = false;
				for (var i in listInv) {
					if (productId == listInv[i].productId) {
						var qoh = listInv[i].quantityOnHandTotal;
						if (requireAmount && 'Y' == requireAmount){
							qoh = listInv[i].amountOnHandTotal;
							if (typeof(qoh) == 'number'){
								if (locale == 'vi') qoh = qoh.toString().replace(',', '.');
							} else {
								if (locale == 'vi') qoh = qoh.replace(',', '.');
							}
						}
						qoh = parseFloat(qoh);
						if (qoh > 0) {
							check = true;
						}
						if (qoh <= remainQty) {
							if (fst == true) {
								item["inventoryItemId"] = listInv[i].inventoryItemId;
								item["exportQuantity"] = qoh;
								listReturnItems.push(item);
								fst = false;
							} else {
								var newItem = $.extend({}, item);
								delete newItem["deliveryItemSeqId"];
								newItem["inventoryItemId"] = listInv[i].inventoryItemId;
								newItem["exportQuantity"] = qoh;
								listReturnItems.push(newItem);
							}
							remainQty = remainQty - qoh;
						} else {
							if (fst == true) {
								item["inventoryItemId"] = listInv[i].inventoryItemId;
								item["exportQuantity"] = remainQty;
								listReturnItems.push(item);
								fst = false;
							} else {
								var newItem = $.extend({}, item);
								delete newItem["deliveryItemSeqId"];
								newItem["inventoryItemId"] = listInv[i].inventoryItemId;
								newItem["exportQuantity"] = remainQty;
								listReturnItems.push(newItem);
							}
							remainQty = 0;
						}
						if (remainQty <= 0) break;
					}
				}
				if (check == false) {
					listReturnItems.push(item);
				}
			}
		} else {
			for (var x in listReturnItems){
				listReturnItems[x].exportQuantity = 0;
				listReturnItems[x].inventoryItemId = null;
			}
		}
		return listReturnItems;
	} 
	
	var loadReturnItem = function (data) {
		var tmpS = $("#jqxgridReturnItem").jqxGrid('source');
	    tmpS._source.localdata = data;
	    $("#jqxgridReturnItem").jqxGrid('source', tmpS);
	};
	
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$("#jqxgridReturnItem").on("pagechanged", function (event) {
		    // event arguments.
		    var args = event.args;
		    // page number.
		    var pagenum = args.pagenum;
		    // page size.
		    var pagesize = args.pagesize;
		    var cell = $('#jqxgridReturnItem').jqxGrid('getselectedcell');
		    $("#jqxgridReturnItem").jqxGrid('endcelledit', cell.rowindex, cell.datafield, true, true);
		}); 
		
		$("#destinationFacilityId").on('change', function(event){
			curFacilityId = $('#destinationFacilityId').jqxDropDownList('val');
			listInv = []; 
			for (var x in listInvTmp) {
				if (listInvTmp[x].facilityId == curFacilityId) {
					listInv.push(listInvTmp[x]);
				}
			}
			var listReturnItemTmp = prepareData (listReturnItemData, listInv);
			loadReturnItem(listReturnItemTmp);
		});
		 
		$("#exportProduct").click(function () {
			var selectedItems = [];
			var allRows =  $('#jqxgridReturnItem').jqxGrid('getrows');
	        for (var id = 0; id < allRows.length; id ++){
	        	if(checkGridReturnItemRequiredData(allRows[id].uid) == true){
	                return false;
	            }
	        	if (allRows[id].statusId == "SUP_RETURN_ACCEPTED"){
	        		if (allRows[id].exportQuantity > 0) {
	        			selectedItems.push(allRows[id]);
	        		}
	        	}
	        }
	        var invSelected = [];
	        for(var i = 0; i < selectedItems.length; i ++){
	        	var check = false;
	        	for (j = 0; j < invSelected.length; j ++){
	        		if (invSelected[j].inventoryItemId == selectedItems[i].inventoryItemId && invSelected[j].orderId == selectedItems[i].orderId){
	        			check = true;
	        			break;
	        		}
	        	}
	        	if (check == false){
	        		var map = {
	        			inventoryItemId: selectedItems[i].inventoryItemId,	
	        			orderId: selectedItems[i].orderId,	
	        		};
	        		invSelected.push(map);
	        	}
	        }
	        var listReturnItemSelected = [];
	        for (var i = 0; i < invSelected.length; i ++){
	        	var obj = null;
	        	var total = 0;
	        	for (var j = 0; j < selectedItems.length; j++){
	        		if (invSelected[i].inventoryItemId == selectedItems[j].inventoryItemId && invSelected[i].orderId == selectedItems[j].orderId){
	        			obj = selectedItems[j];
	        			total = total + selectedItems[j].exportQuantity;
	        		}
	        	}
	        	obj["exportQuantity"] = total; 
	        	listReturnItemSelected.push(obj);
	        }
	        var descError = null;
	        var checkQty = true;
			for (b in listReturnItemData) {
				var init = listReturnItemData[b];
				var initQuantity = init.returnQuantity;
				var expTotalQty = 0;
				for (c in listReturnItemSelected) {
					var _item = listReturnItemSelected[c];
					if (init.productCode == _item.productCode) {
						expTotalQty = expTotalQty + _item.exportQuantity;
					}
				}
				if (initQuantity > expTotalQty) {
					checkQty = false;
					if (descError != null) {
						descError = descError + uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(initQuantity) +";</br>";
					} else {
						descError = uiLabelMap.ProductId + ": " + init.productCode + " - " + uiLabelMap.Quantity + ": " + formatnumber(expTotalQty) + " < " + formatnumber(initQuantity) +";</br>";
					}
				}
			}
			if (checkQty == false) {
				descError = uiLabelMap.BLExportQuantityLessThanRequiredQuantity + "</br>" + descError;
			}
			var confirmMess = uiLabelMap.AreYouSureYouWantToExport;
			if (descError != null) {
				confirmMess = descError + uiLabelMap.AreYouSureYouWantToExport;
			}
			bootbox.dialog(confirmMess, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll(); btnClick = false;}
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	if (!btnClick){
	            		for (var i = 0; i < listReturnItemSelected.length; i ++){
		            		delete listReturnItemSelected[i]['productName'];
		            		delete listReturnItemSelected[i]['description'];
		            		listReturnItemSelected[i]['returnQuantity'] = listReturnItemSelected[i]['exportQuantity'];
		            	}
		            	listReturnItemSelected = JSON.stringify(listReturnItemSelected);
		            	Loading.show('loadingMacro');
		            	setTimeout(function(){
			            	$.ajax({
				   				 type: "POST",
				   				 url: "logisticsExportReturn",
				   				 data: {
				   					 returnId: returnId,
				   					 listReturnItems: listReturnItemSelected,
				   				 },
				   				 dataType: "json",
				   				 async: false,
				   				 success: function(data){
				   				 },
				   				 error: function(response){
				   				 }
			   		 		}).done(function(data) {
			   		 			$("#notifyUpdateSuccess").jqxNotification("open");
			   		 			window.location.href = "getDetailVendorReturn?returnId="+returnId+"&activeTab=general-tab";
				      		});
			            	Loading.hide('loadingMacro');
		            	}, 500);
	            		btnClick = true;
	            	}
	            }
			}]);
			
		});
	};
	var initValidateForm = function (){
	};
	var getLocalization = function getLocalization() {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	
	getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	function addZero(i) {
	    if (i < 10) {
	        i = "0" + i;
	    }
	    return i;
	}
	
	var rendertoolbar = function (toolbar){
		toolbar.html("");
		var id = "jqxgridFacilityRole";
		var me = this;
		var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.ListProduct + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'>" +
				"" + "<a style='float:right;font-size:14px;margin-right: 5px' id='addRow' href='javascript:ReceiveReturnObj.addNewRow()' data-rel='tooltip' title='" + uiLabelMap.AddRow + "' data-placement='bottom'><i class='icon-plus-sign open-sans'></i></a>"
				+ "</div></div>");
		toolbar.append(jqxheader);
     	var container = $('#toolbarButtonContainer' + id);
        var maincontainer = $("#toolbarcontainer" + id);
	}; 
		
	 var initReturnItem = function initReturnItem(valueDataSoure){
        var datafields = [{ name: 'orderId', type: 'string' },
	                	{ name: 'productId', type: 'string' },
	                	{ name: 'productCode', type: 'string' },
	                	{ name: 'productName', type: 'string' },
	                	{ name: 'requireAmount', type: 'string' },
	                	{ name: 'quantityUomId', type: 'string' },
	                	{ name: 'weightUomId', type: 'string' },
	                	{ name: 'returnQuantity', type: 'number' },
	                	{ name: 'returnAmount', type: 'number' },
	                	{ name: 'receivedQuantity', type: 'number' },
	                	{ name: 'receivedAmount', type: 'number' },
	                	{ name: 'exportQuantity', type: 'number' },
	                	{ name: 'inventoryStatusId', type: 'string' },
	                	{ name: 'returnId', type: 'string' },
	                	{ name: 'returnItemSeqId', type: 'string' },
	                	{ name: 'returnPrice', type: 'number' },
	                	{ name: 'returnReasonId', type: 'string' },
	                	{ name: 'statusId', type: 'string' },
	                	{ name: 'lotId', type: 'string' },
	                	{ name: 'orderItemSeqId', type: 'string' },
	                	{ name: 'inventoryItemId', type: 'string' },
	                	{ name: 'returnTypeId', type: 'string' },
	                	{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
	                	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
	                	{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
	                	{ name: 'actualDeliveredQuantity', type: 'number' },
	                	{ name: 'actualDeliveredAmount', type: 'number' },
	                	];
		   var columnlists= [
				{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},		
				{ text: uiLabelMap.OrderId, pinned: true, dataField: 'orderId', hidden: true, width: 100, editable:true, columntype: 'dropdownlist',
					createeditor: function (row, cellvalue, editor) {
						var listOrderIds = [];
						for (var i = 0; i < listReturnItemData.length; i ++){
							var check = false;
							var orderId = listReturnItemData[i].orderId;
							$.each(listOrderIds, function(i){
				   				var olb = listOrderIds[i];
				   				if (olb.orderId == orderId){
				   					check = true;
				   				}
				   			});
							if (!check) {
								var ord = {
									orderId: orderId,
								};
								listOrderIds.push(ord);
							}
						}
						var sourceDataOrder =
						{
			               localdata: listOrderIds,
			               datatype: 'array'
						};
						var dataAdapterOrder = new $.jqx.dataAdapter(sourceDataOrder);
						editor.off('change');
						editor.jqxDropDownList({source: dataAdapterOrder, autoDropDownHeight: true, displayMember: 'orderId', valueMember: 'orderId', placeHolder: uiLabelMap.PleaseSelectTitle,
						});
						editor.on('change', function (event){
							var args = event.args;
				     	    if (args) {
			     	    		var item = args.item;
				     		    if (item){
				     		    	ReceiveReturnObj.updateRowDataByOrder(item.value);
				     		    } 
				     	    }
				        });
					 },
					 cellbeginedit: function (row, datafield, columntype) {
						 var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						 if (data.productId && data.orderId && data.exportQuantity > 0){
							 return false;
						 }
						 return true;
					 },
					 cellsrenderer: function(row, column, value){
						 var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						 if (!data.orderId){
							 return '<span>'+uiLabelMap.PleaseSelectTitle+'</span>';
						 }
						 return '<span>'+value+'</span>';
					 }
				},
				{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 130, editable: true, pinned: true, columntype: 'dropdownlist',
					createeditor: function (row, cellvalue, editor) {
						
						var listProductIds = [];
						for (var i = 0; i < listReturnItemData.length; i ++){
							var check = false;
							var productId = listReturnItemData[i].productId;
							var productCode = listReturnItemData[i].productCode;
							$.each(listProductIds, function(i){
				   				var olb = listProductIds[i];
				   				if (olb.productId == productId){
				   					check = true;
				   				}
				   			});
							if (!check) {
								var prd = {
									productId: productId,
									productCode: productCode,
								};
								listProductIds.push(prd);
							}
						}
						
						var sourceDataProduct =
						{
			               localdata: listProductIds,
			               datatype: 'array'
						};
						var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
						editor.off('change');
						editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: uiLabelMap.PleaseSelectTitle,
							renderer: function(index, label, value) {
				                var item = editor.jqxDropDownList('getItem', index);
				                return '<span>'+item.originalItem.productCode+'</span>';
				            }
						});
						editor.on('change', function (event){
							var args = event.args;
				     	    if (args) {
			     	    		var item = args.item;
				     		    if (item){
				     		    	ReceiveReturnObj.updateRowData(item.value);
				     		    } 
				     	    }
				        });
					 },
					 cellbeginedit: function (row, datafield, columntype) {
						 var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						 if (data.productId && data.orderId && data.exportQuantity > 0){
							 return false;
						 }
						 return true;
					 },
					 cellsrenderer: function(row, column, value){
						 var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						 if (!data.productId){
							 return '<span>'+uiLabelMap.PleaseSelectTitle+'</span>';
						 }
						 return '<span>'+value+'</span>';
					 }
				},
				{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable:false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						if (!data.productId){
							return '<span class="align-right">...</span>';
						}
					}
				},
				{ text: uiLabelMap.LogInventoryItem, dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 350, editable: true, sortable: false,
					cellsrenderer: function(row, column, value){
				        var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
				        var requireAmount = data.requireAmount;
				        var check = false;
				        if(data != null && data != undefined){
				        	if(value != null && value != '' && value != undefined){
				        		if (data.expiredDate === null || data.expiredDate === undefined){
				        			for (var i = 0; i < listInv.length; i ++){
					        			if (listInv[i].inventoryItemId == data.inventoryItemId){
					        				var exp;
					        				var mnf;
					        				var rcd;
					        				if (listInv[i].expireDate != null && listInv[i].expireDate != undefined && listInv[i].expireDate != '') {
					        					exp = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].expireDate));
					        				} else {
					        					exp = uiLabelMap.ProductMissExpiredDate;
					        				}
					        				if (listInv[i].datetimeManufactured != null && listInv[i].datetimeManufactured != undefined && listInv[i].datetimeManufactured != '') {
					        					mnf = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeManufactured));
					        				} else {
					        					mnf = uiLabelMap.ProductMissDatetimeManufactured;
					        				}
					        				if (listInv[i].datetimeReceived != null && listInv[i].datetimeReceived != undefined && listInv[i].datetimeReceived != '') {
					        					rcd = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeReceived));
					        				} else {
					        					rcd = uiLabelMap.ProductMissDatetimeReceived;
					        				}
					        				if (exp && mnf && rcd){
					        					return '<span class="cell-right-focus"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
					        				} else {
					        					return '<span class="cell-right-focus"></span>';
					        				}
					        			}
					        		}
				        		} else {
				        			var exp = data.expiredDate;
					                return '<span class="cell-right-focus">' + DatetimeUtilObj.getFormattedDate(new Date(exp)) + '</span>';
				        		}
			        		} else {
			        			if (data.productCode){
				            		if (data.statusId == 'SUP_RETURN_ACCEPTED'){
				            			var id = data.uid;
					            		var t = true;
					            		if (curFacilityId == null){
							        		curFacilityId = $('#destinationFacilityId').jqxDropDownList('val');
							        	}
								 		for(i = 0; i < listInv.length; i++){
								 			if (data.exportQuantity === '' || data.exportQuantity === null || data.exportQuantity === undefined) {
								 				var remainQty = data.returnQuantity;
								 				if (data.receivedQuantity && data.receivedQuantity > 0){
								 					remainQty = data.returnQuantity - data.receivedQuantity;
								 				}
								 				if (requireAmount && requireAmount == 'Y') {
								 					remainQty = data.returnAmount;
									 				if (data.receivedAmount && data.receivedAmount > 0){
									 					remainQty = data.returnAmount - data.receivedAmount;
									 				}
								 				}
								 				var qoh = listInv[i].quantityOnHandTotal;
								 				if (requireAmount && requireAmount == 'Y') {
								 					qoh = listInv[i].amountOnHandTotal;
								 				}
								 				var receivedQuantity = data.receivedQuantity;
								 				if (requireAmount && requireAmount == 'Y') {
								 					receivedQuantity = data.receivedAmount;
								 				}
								 				var returnQuantity = data.returnQuantity;
								 				if (requireAmount && requireAmount == 'Y') {
								 					returnQuantity = data.returnAmount;
								 				}
								 				if (data.productId == listInv[i].productId && qoh >= 0 && qoh >= remainQty && curFacilityId == listInv[i].facilityId && curFacilityId == listInv[i].facilityId){
									 				$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
									 				if (receivedQuantity){
									 					$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', returnQuantity - receivedQuantity);
									 				} else {
									 					$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', returnQuantity);
									 				}
									 				var exp;
							        				var mnf;
							        				var rcd;
							        				if (listInv[i].expireDate != null && listInv[i].expireDate != undefined && listInv[i].expireDate != '') {
							        					exp = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].expireDate));
							        				} else {
							        					exp = uiLabelMap.ProductMissExpiredDate;
							        				}
							        				if (listInv[i].datetimeManufactured != null && listInv[i].datetimeManufactured != undefined && listInv[i].datetimeManufactured != '') {
							        					mnf = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeManufactured));
							        				} else {
							        					mnf = uiLabelMap.ProductMissDatetimeManufactured;
							        				}
							        				if (listInv[i].datetimeReceived != null && listInv[i].datetimeReceived != undefined && listInv[i].datetimeReceived != '') {
							        					rcd = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeReceived));
							        				} else {
							        					rcd = uiLabelMap.ProductMissDatetimeReceived;
							        				}
									 				check = true;
									 				return '<span class="cell-right-focus"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
									 			} else {
									 				check = false;
									 				if (i == listInv.length - 1){
										 				for (var k = 0; k < listInv.length; k ++){
										 					var qohTmp = listInv[k].quantityOnHandTotal;
											 				if (requireAmount && requireAmount == 'Y') {
											 					qohTmp = listInv[k].amountOnHandTotal;
											 				}
										 					if (data.productId == listInv[k].productId && qohTmp >= 0 && qohTmp >= 0 && curFacilityId == listInv[k].facilityId){
										 						$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[k].inventoryItemId);
												 				$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', listInv[k].quantityOnHandTotal);
												 				check = true;
												 				var exp;
										        				var mnf;
										        				var rcd;
										        				if (listInv[k].expireDate != null && listInv[k].expireDate != undefined && listInv[k].expireDate != '') {
										        					exp = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].expireDate));
										        				} else {
										        					exp = uiLabelMap.ProductMissExpiredDate;
										        				}
										        				if (listInv[k].datetimeManufactured != null && listInv[k].datetimeManufactured != undefined && listInv[k].datetimeManufactured != '') {
										        					mnf = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].datetimeManufactured));
										        				} else {
										        					mnf = uiLabelMap.ProductMissDatetimeManufactured;
										        				}
										        				if (listInv[k].datetimeReceived != null && listInv[k].datetimeReceived != undefined && listInv[k].datetimeReceived != '') {
										        					rcd = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].datetimeReceived));
										        				} else {
										        					rcd = uiLabelMap.ProductMissDatetimeReceived;
										        				}
										        				if (exp && mnf && rcd){
										        					return '<span class="cell-right-focus"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
										        				} else {
										        					return '<span class="cell-right-focus"></span>';
										        				}
										 					}
										 				}
									 					return '<span style=\"text-align: left\" class=\"warning-color\"> ' +uiLabelMap.NotEnough+ '</span>';
									 				} else {
									 					continue;
									 				}
								 				}
								 			} else {
								 				var qohTmp = listInv[i].quantityOnHandTotal;
								 				if (requireAmount && requireAmount == 'Y') {
								 					qohTmp = listInv[i].amountOnHandTotal;
								 				}
								 				if (data.productId == listInv[i].productId && qohTmp >= data.exportQuantity && curFacilityId == listInv[i].facilityId){
									 				$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
									 				check = true;
									 				var exp;
							        				var mnf;
							        				var rcd;
							        				if (listInv[i].expireDate != null && listInv[i].expireDate != undefined && listInv[i].expireDate != '') {
							        					exp = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].expireDate));
							        				} else {
							        					exp = uiLabelMap.ProductMissExpiredDate;
							        				}
							        				if (listInv[i].datetimeManufactured != null && listInv[i].datetimeManufactured != undefined && listInv[i].datetimeManufactured != '') {
							        					mnf = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeManufactured));
							        				} else {
							        					mnf = uiLabelMap.ProductMissDatetimeManufactured;
							        				}
							        				if (listInv[i].datetimeReceived != null && listInv[i].datetimeReceived != undefined && listInv[i].datetimeReceived != '') {
							        					rcd = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[i].datetimeReceived));
							        				} else {
							        					rcd = uiLabelMap.ProductMissDatetimeReceived;
							        				}
							        				if (exp && mnf && rcd){
							        					return '<span class="cell-right-focus"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
							        				} else {
							        					return '<span class="cell-right-focus"></span>';
							        				}
						        				} else {
						        					if (i == listInv.length - 1){
						        						check = false;
										 				for (var k = 0; k < listInv.length; k ++){
										 					var qohTmp = listInv[k].quantityOnHandTotal;
											 				if (requireAmount && requireAmount == 'Y') {
											 					qohTmp = listInv[k].amountOnHandTotal;
											 				}
										 					if (data.productId == listInv[k].productId && qohTmp >= 0 && listInv[k].quantityOnHandTotal >= 0 && curFacilityId == listInv[k].facilityId){
										 						check = true;
										 						$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[0].inventoryItemId);
												 				$('#jqxgridReturnItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', listInv[0].quantityOnHandTotal);
												 				var exp;
										        				var mnf;
										        				var rcd;
										        				if (listInv[k].expireDate != null && listInv[k].expireDate != undefined && listInv[k].expireDate != '') {
										        					exp = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].expireDate));
										        				} else {
										        					exp = uiLabelMap.ProductMissExpiredDate;
										        				}
										        				if (listInv[k].datetimeManufactured != null && listInv[k].datetimeManufactured != undefined && listInv[k].datetimeManufactured != '') {
										        					mnf = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].datetimeManufactured));
										        				} else {
										        					mnf = uiLabelMap.ProductMissDatetimeManufactured;
										        				}
										        				if (listInv[k].datetimeReceived != null && listInv[k].datetimeReceived != undefined && listInv[k].datetimeReceived != '') {
										        					rcd = $.datepicker.formatDate('dd/mm/yy', new Date(listInv[k].datetimeReceived));
										        				} else {
										        					rcd = uiLabelMap.ProductMissDatetimeReceived;
										        				}
										        				if (exp && mnf && rcd){
										        					return '<span class="cell-right-focus"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
										        				} else {
										        					return '<span class="cell-right-focus"></span>';
										        				}
									        				}
										 				}
										 				return '<span style=\"text-align: left\" class=\"warning-color\"> ' +uiLabelMap.NotEnough+ '</span>';
					        						} else {
					        							continue;
					        						}
								 				}
								 			}
								 		}
								 		if (check == false){
								 			return '<span title=\"'+uiLabelMap.NotEnoughDetail+': ' +data.returnQuantity+ '\" style=\"text-align: left\" class=\"warning-color\">'+uiLabelMap.NotEnough+'</span>';
								 		}
					            	} else {
					            		return '<span class="align-right" class=\"focus-color\"></span>';
					            	}
				            	} else {
									return '<span class="align-right" class=\"focus-color\">...</span>';
								}
				            }
				        	return '<span class="cell-right-focus"><span>';
				        }
				    }, 
				    initeditor: function(row, value, editor){
					    var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
					    editor.off('change');
					    var requireAmount = data.requireAmount;
					    var reqAmount = false; 
					    if (requireAmount && requireAmount == 'Y') reqAmount = true; 
					    var uid = data.uid;
					    var invData = [];
					    var curInvId = null;
					    if (data.inventoryItemId != null && data.inventoryItemId != undefined){
					    	curInvId = data.inventoryItemId;
					    }
					    var invEnoughTmp = null;
				        for(i = 0; i < listInv.length; i++){
				        	if (curFacilityId == null){
				        		curFacilityId = $('#destinationFacilityId').jqxDropDownList('val');
				        	}
				        	if (curFacilityId == listInv[i].facilityId){
				        		if(listInv[i].productId == data.productId){
				        			var qoh = listInv[i].quantityOnHandTotal;
				        			if (reqAmount) qoh = listInv[i].amountOnHandTotal;
				        			if (reqAmount >= data.exporteQuantity){
					            		invEnoughTmp = listInv[i].inventoryItemId;
					            	}
					                var tmpDate ;
					                var tmpValue = new Object();
					                if(listInv[i].expireDate != null && listInv[i].expireDate != undefined && listInv[i].expireDate != ''){
					                	var tmp = listInv[i].expireDate;
					                    tmpValue.expireDate =  $.datepicker.formatDate('dd/mm/yy', new Date(tmp));
					                } else{
					                    tmpValue.expireDate = uiLabelMap.ProductMissExpiredDate;
					                }
					                if(listInv[i].datetimeReceived != null && listInv[i].datetimeReceived != undefined && listInv[i].datetimeReceived != ''){
					                	var tmp = listInv[i].datetimeReceived;
					                    tmpValue.receivedDate =  $.datepicker.formatDate('dd/mm/yy', new Date(tmp));
					                } else{
					                    tmpValue.receivedDate = uiLabelMap.ProductMissDatetimeReceived;
					                }
					                if(listInv[i].datetimeManufactured != null && listInv[i].datetimeManufactured != undefined && listInv[i].datetimeManufactured != ''){
					                	var tmp = listInv[i].datetimeManufactured;
					                    tmpValue.datetimeManufactured =  $.datepicker.formatDate('dd/mm/yy', new Date(tmp));
					                } else{
					                    tmpValue.datetimeManufactured = uiLabelMap.ProductMissDatetimeManufactured;
					                }
					                tmpValue.inventoryItemId = listInv[i].inventoryItemId;
					                tmpValue.productId = listInv[i].productId;
					                tmpValue.quantityOnHandTotal = qoh;
					                tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
					                if (locale == 'vi') qoh = qoh.toString().replace(',', '.');
					                tmpValue.quantityCurrent = parseFloat(qoh);
					                var qtyUom = getUomDescription(listInv[i].quantityUomId);
					                tmpValue.qtyUom = qtyUom;
					                invData.push(tmpValue);
					            }
				        	}
				        }
		        		var curInv = null;
		        		var curQuantity = 0;
		        		var allrowTmp = $('#jqxgridReturnItem').jqxGrid('getrows');
		        		for(var j = 0; j < allrowTmp.length; j++){
		        			if (allrowTmp[j].inventoryItemId == curInvId){
		        				curQuantity = curQuantity + allrowTmp[j].exportQuantity;
		        			}
		        		}
		        		for(var i = 0; i < invData.length;i++){
		        			if(invData[i].inventoryItemId == curInvId){
		        				invData[i].quantityCurrent = parseFloat(invData[i].quantityCurrent) - parseFloat(curQuantity);
		        			} else {
		        				var qtyOfOtherInv = 0;
		        				for(var j = 0; j < allrowTmp.length; j++){
		        					if (allrowTmp[j].inventoryItemId == invData[i].inventoryItemId){
		        						qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].exportQuantity;
		        					}
		        				}
		        				invData[i].quantityCurrent = parseFloat(invData[i].quantityCurrent) - parseFloat(qtyOfOtherInv);
		        			}
		        		}
			        	editor.jqxDropDownList({selectedIndex: 0, placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, dropDownWidth: '650px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
				            renderer: function(index, label, value) {
				                var item = editor.jqxDropDownList('getItem', index);
				                var tmp = item.originalItem.quantityCurrent;
								if (locale == 'vi') tmp = tmp.toString().replace(',', '.');
								tmp = parseFloat(tmp);
				                if (item.originalItem.quantityCurrent < 0){
				                	tmp = '- ' + formatnumber(Math.abs(item.originalItem.quantityCurrent));
				                } else {
				                	tmp = formatnumber(Math.abs(item.originalItem.quantityCurrent));
				                }
								if (curInvId == item.originalItem.inventoryItemId) {
									indexSelected = item.index;
									editor.jqxDropDownList('selectIndex', indexSelected);
								}
								var qoh = item.originalItem.quantityOnHandTotal;
								if (locale == 'vi') qoh = qoh.replace(',', '.');
								return '[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - [<span style=\"color:blue;\">'+uiLabelMap.ManufacturedDateSum+':</span>&nbsp;' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">'+uiLabelMap.ReceivedDateSum+':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + formatnumber(qoh) + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + tmp + ']</span>';
				            }
				        });
				    },
				    validation: function (cell, value) {
				    	if (listInv.length < 1){
				    		return { result: false, message: uiLabelMap.FacilityNotEnoughProduct};
					    }
				        if (value == null || value == undefined || value == '') {
				            return { result: false, message: uiLabelMap.DmsFieldRequired};
				        }
				        return true;
				    },
				    cellbeginedit: function (row, datafield, columntype) {
						 if (listInv.length <= 0){
							 return false;
						 }
						 return true;
					 },
				}, 	
				{ text: uiLabelMap.QuantityReturned, dataField: 'exportQuantity', columntype: 'numberinput', width: 130, editable: true,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
					    var reqAmount = false; 
					    if (requireAmount && requireAmount == 'Y') reqAmount = true; 
						if (value === undefined || value === null || value === ''){
							if (data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId === ''){
								if (data.productCode){
									value = 0;
									return '<span class="cell-right-focus">' + formatnumber(value) + '</span>';
								} else {
									return '<span class="cell-right-focus">...</span>';
								}
							} else {
								for (var j = 0; j < listInv.length; j ++){
									if (data.inventoryItemId == listInv[j].inventoryItemId){
										var qoh = listInv[j].quantityOnHandTotal;
										if (reqAmount) qoh = listInv[j].amountOnHandTotal;
										if (data.returnQuantity > qoh){
											value = qoh;
										} else {
											value = data.returnQuantity;
										}
										return '<span class="cell-right-focus">' + formatnumber(value) +'</span>';
									}
								}
								value = 0;
								return '<span class="cell-right-focus">' + formatnumber(value) +'</span>';
							}
						} else {
							return '<span class="cell-right-focus">' + formatnumber(value) +'</span>';
						}
					},
					initeditor: function(row, value, editor){
				        var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
				        var requireAmount = data.requireAmount;
					    var reqAmount = false; 
					    if (requireAmount && requireAmount == 'Y') reqAmount = true; 
				        if(data.statusId != 'SUP_RETURN_ACCEPTED'){
                            if (reqAmount) {
                        		editor.jqxNumberInput({decimalDigits: 2, disabled: false});
                        	} else {
                        		editor.jqxNumberInput({disabled: true});
                        	}
                        } else{
                        	editor.jqxNumberInput({disabled: false});
                        	if (reqAmount) {
                        		editor.jqxNumberInput({decimalDigits: 2, disabled: false});
                        	} 
                            if (value != null && value != undefined){
                            	editor.jqxNumberInput('val', value);
                            } else {
                            	if (data.productCode != null && data.productCode != undefined){
                            		if (check){
                            			var receivedQuantity = data.receivedQuantity;
                            			var returnQuantity = data.returnQuantity;
                            			if (reqAmount){
                            				receivedQuantity = data.receivedAmount;
                            				returnQuantity = data.returnAmount;
                            			}
                                		if (receivedQuantity!= null && receivedQuantity != undefined && receivedQuantity != ''){
                                			if (receivedQuantity){
                                            	editor.jqxNumberInput('val', returnQuantity - receivedQuantity);
                                            }
                                		} else {
                                			if (returnQuantity){
                                            	editor.jqxNumberInput('val', returnQuantity);
                                            }
                                		}
                            		} else {
                                    	editor.jqxNumberInput('val', 0);
                            		}
                            	} else {
                            		editor.jqxNumberInput('val', 0);
                            	}
                            }
                        }
				    },
				    validation: function (cell, value) {
				        if (value < 0){
				        	return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
				        }
				        var dataTmp = $('#jqxgridReturnItem').jqxGrid('getrowdata', cell.row);
				        var requireAmount = dataTmp.requireAmount;
					    var reqAmount = false; 
					    if (requireAmount && requireAmount == 'Y')  {
					    	reqAmount = true; 
					    }
				        var prCode = dataTmp.productCode;
				        var orderId = dataTmp.orderId;
				        var rows = $('#jqxgridReturnItem').jqxGrid('getrows');
						
				        var listByPr = [];
				        for (var i = 0; i < rows.length; i ++){
				        	if (prCode == rows[i].productCode && orderId == rows[i].orderId){
								 listByPr.push(rows[i]);
				        	} 
				        }
				        
				        var allDlvQty = 0;
				        for (var i = 0; i < listReturnItemData.length; i ++){
				        	if (listReturnItemData[i].productCode == prCode && listReturnItemData[i].orderId == orderId){
				        		var receivedQuantity = listReturnItemData[i].receivedQuantity;
				        		var returnQuantity = listReturnItemData[i].returnQuantity;
				        		if (reqAmount) {
				        			receivedQuantity = listReturnItemData[i].receivedAmount;
				        			returnQuantity = listReturnItemData[i].returnAmount;
				        		}
				        		if (receivedQuantity){
				        			allDlvQty = allDlvQty + returnQuantity - receivedQuantity;
				        		} else {
				        			allDlvQty = allDlvQty + returnQuantity;
				        		}
				        	}
						}
				        var curQty = 0;
				        for (var i = 0; i < listByPr.length; i ++){
				        	if (listByPr[i].productCode == prCode){
				        		if (listByPr[i].exportQuantity != undefined){
				        			curQty = curQty + listByPr[i].exportQuantity;
				        		} else {
				        			if (reqAmount) {
				        				curQty = curQty + listByPr[i].returnAmount;
				        			} else {
				        				curQty = curQty + listByPr[i].returnQuantity;
				        			}
				        		}
				        	}	
				        }
				        var totalCreated = 0;
				        if (dataTmp.exportQuantity != undefined){
				        	totalCreated = curQty + value - parseFloat(dataTmp.exportQuantity);
				        } else {
				        	if (reqAmount) {
				        		if (dataTmp.receivedAmount){
					        		totalCreated = curQty + value - parseInt(dataTmp.returnAmount - dataTmp.receivedAmount);
					        	} else {
					        		totalCreated = curQty + value - parseInt(dataTmp.returnAmount);
					        	}
				        	} else {
				        		if (dataTmp.receivedQuantity){
					        		totalCreated = curQty + value - parseInt(dataTmp.returnQuantity - dataTmp.receivedQuantity);
					        	} else {
					        		totalCreated = curQty + value - parseInt(dataTmp.returnQuantity);
					        	}
				        	}
				        }
				        if (totalCreated - allDlvQty > 0){
				        	return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber +': ' + totalCreated + ' > ' + allDlvQty};
				        }
				        return true;
					 },
				},
				{ text: uiLabelMap.RequiredNumber, dataField: 'returnQuantity', columntype: 'numberinput', width: 130, editable: false,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (data.productId){
							if (requireAmount && requireAmount == 'Y') {
								value = data.returnAmount;
							}
							return '<span class="align-right">' + formatnumber(value) +'</span>';
						}
						return '<span class="align-right">...</span>';
					},
				},
				{ text: uiLabelMap.Unit, dataField: 'quantityUomId', columntype: 'numberinput', width: 100, editable: false,
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (data.productId){
							if (requireAmount && requireAmount == 'Y') {
								value = data.weightUomId;
							}
							var descriptionUom = getUomDescription(value);
							return '<span class="align-right">' + descriptionUom +'</span>';
						}
						return '<span class="align-right">...</span>';
					},
				},
				{ text: uiLabelMap.UnitPrice, dataField: 'returnPrice', columntype: 'numberinput', width: 150, editable: false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						if (!data.productId){
							return '<span class="align-right">...</span>';
						}
						if(value){
							return '<span class="align-right">' + formatcurrency(value) + '<span>';
						} else {
							return '<span class="align-right"><span>';
						}
					},
					initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
				        editor.jqxNumberInput({ decimalDigits: 0});
				        var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
				        if (data.returnPrice){
				        	editor.jqxNumberInput('val', data.returnPrice);
				        }
				    },
				},
				{ text: uiLabelMap.ExportedQuantity, dataField: 'receivedQuantity', width: 150, editable:false,
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (data.productId){
							if (requireAmount && requireAmount == 'Y') {
								value = data.receivedAmount;
							}
							if (value){
								return '<span class="align-right">' + formatnumber(value) +'</span>';
							} else {
								value = 0;
								return '<span class="align-right">' + formatnumber(value) +'</span>';
							}
						} else {
							return '<span class="align-right">...</span>';
						}
					},
				},
			];
		   	var config = {
					width: '100%', 
			   		virtualmode: true,
			   		showtoolbar: true,
			   		rendertoolbar: rendertoolbar,
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
		  	Grid.initGrid(config, datafields, columnlists, null, $("#jqxgridReturnItem"));
		}
	 
	 var addNewRow = function addNewRow(){
		 var firstRow = $('#jqxgridReturnItem').jqxGrid('getrowdata', 0);
		 if (firstRow.productId){
			 $('#jqxgridReturnItem').jqxGrid('clearselection');
			 var datarow = generaterow();
			 $("#jqxgridReturnItem").jqxGrid('addrow', null, datarow, "first");
			 $("#jqxgridReturnItem").jqxGrid('unselectrow', 0);
			 $("#jqxgridReturnItem").jqxGrid('begincelledit', 0, "productCode");
		 } else {
			$("#jqxgridReturnItem").jqxGrid('begincelledit', 0, "productCode");
		 }
	 }
	 function generaterow(productCode){
		 var row = {};
		 if (productCode){
			 for(var i = 0; i < listReturnItemData.length; i++){
				 var returnItem = listReturnItemData[i];
				 if (returnItem.productCode == productCode){
					 row["orderId"] = returnItem.orderId;
					 row["productId"] = returnItem.productId;
					 row["productCode"] = returnItem.productCode;
					 row["description"] = returnItem.description;
					 row["requireAmount"] = returnItem.requireAmount;
					 row["quantityUomId"] = returnItem.quantityUomId;
					 row["weightUomId"] = returnItem.weightUomId;
					 row["returnQuantity"] = returnItem.returnQuantity;
					 row["returnAmount"] = returnItem.returnAmount;
					 row["exportQuantity"] = 0;
					 row["inventoryStatusId"] = returnItem.inventoryStatusId;
					 row["returnId"] = returnItem.returnId;
					 row["returnItemSeqId"] = returnItem.returnItemSeqId;
					 row["returnPrice"] = returnItem.returnPrice;
					 row["returnReasonId"] = returnItem.returnReasonId;
					 row["statusId"] = returnItem.statusId;
					 row["lotId"] = returnItem.lotId;
					 row["orderItemSeqId"] = returnItem.orderItemSeqId;
					 row["inventoryItemId"] = returnItem.inventoryItemId;
					 row["returnTypeId"] = returnItem.returnTypeId;
					 row["expiredDate"] = returnItem.expiredDate,
					 row["datetimeManufactured"] = returnItem.datetimeManufactured,
					 row["datetimeReceived"] = returnItem.datetimeReceived,
					 row["actualDeliveredQuantity"] = returnItem.actualDeliveredQuantity;
					 row["actualDeliveredAmount"] = returnItem.actualDeliveredAmount;
					 break; 
				 }
			 }
		 } else {
			 row["orderId"] = "";
			 row["productId"] = "";
			 row["productCode"] = "";
			 row["description"] = "";
			 row["requireAmount"] = "";
			 row["quantityUomId"] = "";
			 row["weightUomId"] = "";
			 row["returnQuantity"] = "";
			 row["returnAmount"] = "";
			 row["exportQuantity"] = "";
			 row["inventoryStatusId"] = "";
			 row["returnId"] = "";
			 row["returnItemSeqId"] = "";
			 row["returnPrice"] = "";
			 row["returnReasonId"] = "";
			 row["statusId"] = "";
			 row["lotId"] = "";
			 row["orderItemSeqId"] = "";
			 row["inventoryItemId"] = "";
			 row["returnTypeId"] = "";
			 row["expiredDate"] = "";
			 row["datetimeManufactured"] = "";
			 row["datetimeReceived"] = "";
			 row["actualDeliveredQuantity"] = "";
			 row["actualDeliveredAmount"] = "";
		 }
		 return row;
	};
	
	var updateRowDataByOrder = function (orderId){
		var first = $("#jqxgridReturnItem").jqxGrid('getrowdata', 0);
		if (first.productId){
			var check = false;
			for (var i = 0; i < listReturnItemData.length; i ++){
				if (listReturnItemData[i].productId == first.productId && listReturnItemData[i].orderId ==  orderId){
					check = true;
				}
			}
			if (!check){
				var prTmp = null;
				for (var i = 0; i < listReturnItemData.length; i ++){
					if (listReturnItemData[i].orderId ==  orderId){
						prTmp = listReturnItemData[i].productCode;
						break;
					}
				}
				updateRowData(prTmp)
			}
		}
	};
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridReturnItem").jqxGrid('getrowid', 0);
        $("#jqxgridReturnItem").jqxGrid('updaterow', id, datarow);
	}
	
	function checkGridReturnItemRequiredData(rowindex){
	    var data = $('#jqxgridReturnItem').jqxGrid('getrowdata', rowindex);
	    if (data){
	    	var requireAmount = false;
		    if (data.requireAmount && data.requireAmount == 'Y') requireAmount = true;
		    if(data.statusId == 'SUP_RETURN_ACCEPTED'){
		    	if (listInv.length > 0 && data.inventoryItemId != null && data.inventoryItemId != undefined && data.inventoryItemId != ''){
		    		for(i = 0; i < listInv.length; i++){
		    			var qoh = listInv[i].quantityOnHandTotal;
		    			if (requireAmount) qoh = listInv[i].amountOnHandTotal;
		    	        if(listInv[i].inventoryItemId == data.inventoryItemId){
		    	            if (qoh < data.exportQuantity){
		    	            	bootbox.dialog(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother, [{
		    		                "label" : uiLabelMap.OK,
		    		                "class" : "btn btn-primary standard-bootbox-bt",
		    		                "icon" : "fa fa-check",
		    		                "callback": function() {
		    		                    $("#jqxgridReturnItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
		    		                }
		    		                }]
		    		            );
		    	            	return true;
		    	            }
		    	        }
		    	    }
		    	}
	    		if(data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId == ''){
	    			if (data.exportQuantity > 0){
	    				bootbox.dialog(uiLabelMap.NotEnoughDetail, [{
	    	                "label" : uiLabelMap.OK,
	    	                "class" : "btn btn-primary standard-bootbox-bt",
	    	                "icon" : "fa fa-check",
	    	                "callback": function() {
	    	                    $("#jqxgridReturnItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	    	                }
	    	                }]
	    	            );
	    				return true;
	    			} 
	    			if (data.exportQuantity == 0){
	    				bootbox.dialog(uiLabelMap.QuantityNotEntered, [{
	    	                "label" : uiLabelMap.OK,
	    	                "class" : "btn btn-primary standard-bootbox-bt",
	    	                "icon" : "fa fa-check",
	    	                "callback": function() {
	    	                    $("#jqxgridReturnItem").jqxGrid('begincelledit', rowindex, "exportQuantity");
	    	                }
	    	                }]
	    	            );
	    				return true;
	    			} 
		        } else if (data.exportQuantity === null || data.exportQuantity === undefined){
		            bootbox.dialog(uiLabelMap.QuantityNotEntered, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                "callback": function() {
		                    $("#jqxgridReturnItem").jqxGrid('begincelledit', rowindex, "exportQuantity");
		                }
		            }]
		            );
		            return true;
		        }
		    }
	    }
	    return false;
	}
	return {
		init: init,
		getLocalization: getLocalization,
		formatFullDate: formatFullDate,
		getFormattedDate: getFormattedDate,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
		updateRowDataByOrder: updateRowDataByOrder,
	};
}());