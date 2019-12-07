$(function(){
	ReceiveReturnObj.init();
});
var ReceiveReturnObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		if (facilityReturnData.length > 0) {
			$('#destinationFacilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 150, dropDownHeight: 150, selectedIndex: 0, source: facilityReturnData, theme: theme, displayMember: 'description', valueMember: 'facilityId' });
			if (destinationFacilityId) {
				$('#destinationFacilityId').jqxDropDownList("val", destinationFacilityId);
			}
		}
		$("#notifyUpdateSuccess").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: false, template: "success"
	    });
		
		getAndLoadReturnItem();
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$('#destinationFacilityId').on("change", function (event) {
			getAndLoadReturnItem();
		});
		$("#jqxgridProductReturn").on("pagechanged", function (event) {
		    // event arguments.
		    var args = event.args;
		    // page number.
		    var pagenum = args.pagenum;
		    // page size.
		    var pagesize = args.pagesize;
		    var cell = $('#jqxgridProductReturn').jqxGrid('getselectedcell');
		    $("#jqxgridProductReturn").jqxGrid('endcelledit', cell.rowindex, cell.datafield, true, true);
		}); 
		
		$("#receiveProduct").click(function () {
			var selectedIndexs = [];
			var allRows = $('#jqxgridProductReturn').jqxGrid('getrows');
	        for (var id = 0; id < allRows.length; id ++){
	        	if (allRows[id].productId){
	        		selectedIndexs.push(allRows[id].uid);
	        	}
	        }
	        for(var id = 0; id < selectedIndexs.length; id++){
	            if(checkGridReturnItemRequiredData(selectedIndexs[id]) == true){
	                return false;
	            }
	        }
			if (allRows.length == 0){
				bootbox.dialog(uiLabelMap.YouNotYetChooseProduct, [{
                    "label" : uiLabelMap.OK,
                    "class" : "btn btn-primary standard-bootbox-bt",
                    "icon" : "fa fa-check",
                    }]
                );
                return false;
			} else {
				listReturnItemSelected = [];
				var distincts = [];
				for (var h = 0; h < allRows.length; h ++){
					var data1 = allRows[h];
					var check = true;
					for (var k = 0; k < distincts.length; k ++){
						var data2 = distincts[k];
						var exp1 = null;
						if (data1.expireDate) {
							var x1 = new Date(data1.expireDate);
							exp1 = x1.getTime();
						}
						var exp2 = null;
						if (data2.expireDate) {
							var x2 = new Date(data2.expireDate);
							exp2 = x2.getTime();
						}
						var mnf1 = null;
						if (data1.expireDate) {
							var y1 = new Date(data1.datetimeManufactured);
							mnf1 = y1.getTime();
						}
						var mnf2 = null;
						if (data2.expireDate) {
							var y2 = new Date(data2.datetimeManufactured);
							mnf2 = y2.getTime();
						}
						
						if (data1.orderItemSeqId == data2.orderItemSeqId && exp1 == exp2 && mnf1 == mnf2 && data1.lotId == data2.lotId && data1.returnReasonId == data2.returnReasonId){ 
							check = false;
							break;
						}
					}
					if (check){
						distincts.push(data1);
					}
				}
				
				for (var i = 0; i < distincts.length; i ++){
					var data1 = distincts[i];
					if (data1.productId && data1.returnQuantity > 0){
						var listDuplicated = [];
						for (var j = 0; j < allRows.length; j ++){
							var data2 = allRows[j];
							if (data2.productId && data2.returnQuantity > 0){
								var exp1 = null;
								if (data1.expireDate) {
									var x1 = new Date(data1.expireDate);
									exp1 = x1.getTime();
								}
								var exp2 = null;
								if (data2.expireDate) {
									var x2 = new Date(data2.expireDate);
									exp2 = x2.getTime();
								}
								var mnf1 = null;
								if (data1.expireDate) {
									var y1 = new Date(data1.datetimeManufactured);
									mnf1 = y1.getTime();
								}
								var mnf2 = null;
								if (data2.expireDate) {
									var y2 = new Date(data2.datetimeManufactured);
									mnf2 = y2.getTime();
								}
								if (data1.orderItemSeqId == data2.orderItemSeqId && exp1 == exp2 && mnf1 == mnf2 && data1.lotId == data2.lotId && data1.returnReasonId == data2.returnReasonId){ 
									listDuplicated.push(data2);
								}
							}
						}
						if (listDuplicated.length == 1){
							listReturnItemSelected.push(listDuplicated[0]);
						} else {
							var map = null;
							for (var k = 0; k < listDuplicated.length; k ++){
								if (listDuplicated[k].returnItemSeqId) {
									map = listDuplicated[k];
									break;
								}
							}
							if (map === null){
								map = listDuplicated[0];
							}
							var total = 0;
							for (var k = 0; k < listDuplicated.length; k ++){
								total = total + listDuplicated[k].returnQuantity;
							}
							map["returnQuantity"] = total;
							listReturnItemSelected.push(map);
						}
					}
				}
				
				for (var e = 0; e < listReturnItemSelected.length; e ++){
					var obj1 = listReturnItemSelected[e];
					var orderItemSeqId1 = obj1.orderItemSeqId;
					if (!obj1.returnItemSeqId){
						for (var f = 0; f < listReturnItemData.length; f ++){
							var orderItemSeqId2 = listReturnItemData[f].orderItemSeqId;
							if (orderItemSeqId2 == orderItemSeqId1){
								listReturnItemSelected[e]["returnItemSeqId"] = listReturnItemData[f].returnItemSeqId;
								break;
							}
						}
					}
				}
				bootbox.dialog(uiLabelMap.AreYouSureYouWantToImport, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	var datetimeReceived = new Date();
		            	for (var i = 0; i < listReturnItemSelected.length; i ++){
		            		if (listReturnItemSelected[i].inventoryStatusId == "Good"){
		            			listReturnItemSelected[i]['inventoryStatusId'] = null;
		            		}
		            		if (listReturnItemSelected[i].inventoryStatusId === null || listReturnItemSelected[i].inventoryStatusId === undefined){
		            			listReturnItemSelected[i]['inventoryStatusId'] = null;
		            		}
		            		listReturnItemSelected[i]['datetimeReceived'] = datetimeReceived.getTime();
		            		
		            		if (listReturnItemSelected[i]['datetimeManufactured']){
		            			listReturnItemSelected[i]['datetimeManufactured'] = new Date(listReturnItemSelected[i]['datetimeManufactured']).getTime();
		            		} else {
		            			delete listReturnItemSelected[i]['datetimeManufactured']; 
		            		}
		            		
		            		if (listReturnItemSelected[i]['expireDate']){
		            			listReturnItemSelected[i]['expireDate'] = new Date(listReturnItemSelected[i]['expireDate']).getTime();
		            		} else {
		            			delete listReturnItemSelected[i]['expireDate']; 
		            		}
		            		if (listReturnItemSelected[i]['lotId']){
		            			listReturnItemSelected[i]['lotId'] = listReturnItemSelected[i]['lotId'].toUpperCase();
		            		} else {
		            			delete listReturnItemSelected[i]['lotId']; 
		            		}
		            		delete listReturnItemSelected[i]['productName']; 
		            	}
		            	listReturnItemSelected = JSON.stringify(listReturnItemSelected);
		            	Loading.show('loadingMacro');
		            	var urlReceiveReturn = "";
		            	if (fromSales) {
		            		urlReceiveReturn = "salesReceiveReturn";
						} else {
							urlReceiveReturn = "logisticsReceiveReturn";
						}
		            	var facilityId = $("#destinationFacilityId").val();
		            	if (!facilityId) {
		            		facilityId = destinationFacilityId;
						}
		            	setTimeout(function(){
			            	$.ajax({
				   				 type: "POST",
				   				 url: urlReceiveReturn,
				   				 data: {
				   					 returnId: returnId,
				   					 shipmentId: shipmentId,
				   					 facilityId: facilityId,
				   					 listReturnItems: listReturnItemSelected,
				   				 },
				   				 dataType: "json",
				   				 async: false,
				   				 success: function(data){
				   					$("#notifyUpdateSuccess").jqxNotification("open");
				   					if (fromSales) {
				   						window.location.href = "CustomerReturnDetailForSup?returnId="+returnId;
									} else {
										window.location.href = "getDetailCustomerReturn?returnId="+returnId;
									}
				   				 },
				   				 error: function(response){
				   					if (fromSales) {
				   						window.location.href = "CustomerReturnDetailForSup?returnId="+returnId;
									} else {
										window.location.href = "getDetailCustomerReturn?returnId="+returnId;
									}
				   				 }
			   		 		}).done(function(data) {
				      		});
			            	Loading.hide('loadingMacro');
		            	}, 500);
		            }
				}]);
			}
			
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
	
	var renderHtmlContainGrids = function renderHtmlContainGrids() {
		var htmlRenderTabs = "<div id='jqxTabsreturnItem' style='margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px' id='tabDynamic'>";
		var htmlRenderGrids = "";
        htmlRenderTabs += "<li value=\"acas\" style=\"margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;\">accs</li>";
        htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=\"123\" ></div></div>";
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsContain").html(htmlRenderTabs);
	}
	
	var loadReturnItem = function loadReturnItem(valueDataSoure){
		var sourceTmp = [];
		ReceiveReturnObj.renderHtmlContainGrids;
		var sourceProduct =
		    {
		        datafields:[{ name: 'orderId', type: 'string' },
							{ name: 'productId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'quantityUomId', type: 'string' },
							{ name: 'requiredQuantity', type: 'number' },
							{ name: 'returnQuantity', type: 'number' },
							{ name: 'inventoryStatusId', type: 'string' },
							{ name: 'returnId', type: 'string' },
							{ name: 'returnItemSeqId', type: 'string' },
							{ name: 'returnPrice', type: 'number' },
							{ name: 'alternativeQuantity', type: 'number' },
							{ name: 'returnReasonId', type: 'string' },
							{ name: 'statusId', type: 'string' },
							{ name: 'lotId', type: 'string' },
							{ name: 'selectedAmount', type: 'number' },
							{ name: 'requireAmount', type: 'string' },
							{ name: 'orderItemSeqId', type: 'string' },
							{ name: 'weightUomId', type: 'string' },
							{ name: 'returnTypeId', type: 'string' },
							{ name: 'expireDate', type: 'date', other: 'Timestamp' },
							{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
							{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
							{ name: 'expRequired', type: 'string' },
							{ name: 'mnfRequired', type: 'string' },
							{ name: 'lotRequired', type: 'string' },
							{ name: 'isPromo', type: 'string' },
							],
		        localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		    $("#jqxgridProductReturn").jqxGrid({
	        source: dataAdapterProduct,
	        filterable: false,
	        showfilterrow: false,
	        theme: 'olbius',
	        rowsheight: 26,
	        width: '100%',
	        enabletooltips: true,
	        autoheight: true,
	        pageable: true,
	        pagesize: 10,
	        editable: true,
	        columnsresize: true,
	        localization: getLocalization(),
		        columns: [	
						{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, 
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<span style=margin:4px;>' + (value + 1) + '</span>';
						    }
						},
						{dataField: 'orderItemSeqId', width: 120, hidden: true,
						},
						{dataField: 'requiredQuantity', width: 120, hidden: true,
						},
						{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: true, pinned: true, columntype: 'dropdownlist',
							createeditor: function (row, cellvalue, editor) {
								var productData = [];
								for (var i = 0; i < listReturnItemData.length; i ++){
									var check = false;
									for (var j = 0; j < productData.length; j ++){
										if (productData[j].productCode == listReturnItemData[i].productCode){
											check = true;
											break;
										}
									}
									if (check == false){
										var f = {
											productCode: listReturnItemData[i].productCode,
										};
										productData.push(f);
									}
								}
								var sourceDataProduct =
								{
					               localdata: productData,
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
								 var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								 if (data.productId){
									 return false;
								 }
								 return true;
							 },
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								 if (!data.productId){
									 return '<span>'+uiLabelMap.PleaseSelectTitle+'</span>';
								 }
								 return '<span>'+value+'</span>';
							 }
						},
						{ text: uiLabelMap.ProductName, dataField: 'productName', width: 185, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (!data.productId){
									return '<span style=\"text-align: right\">...</span>';
								}
							}
						},
						{ text: uiLabelMap.ManufactureDate, dataField: 'datetimeManufactured', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
							cellsrenderer: function (row, column, value){
								if (value){
									return '<span style=\"text-align: right;\" class=\"focus-color\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									if (data.productId){
										return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							},
							initeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy', showFooter: true});
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (data.datetimeManufactured){
									editor.jqxDateTimeInput('setDate', new Date(data.datetimeManufactured));
								} 
						 	},
						 	validation: function (cell, value) {
						 		var now = new Date();
						 		var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
						 		if (value){
						 			if (value > now) {
							            return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeNow};
							        }
							        if (data.expireDate){
							        	var exp = new Date(data.expireDate);
							        	if (exp < new Date(value)){
								        	return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeExpireDate};
								        }
							        }
						 		} else {
						 			if (data.mnfRequired == 'Y') {
						 				return { result: false, message: uiLabelMap.DmsFieldRequired};
						 			}
						 		}
						        
						        return true;
							 },
						},
						{ text: uiLabelMap.ExpireDate, dataField: 'expireDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
							cellsrenderer: function (row, column, value){
								if (value){
									return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									if (data.productId){
										return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							},
							initeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px', formatString: 'dd/MM/yyyy', showFooter: true});
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (data.expireDate){
									editor.jqxDateTimeInput('setDate', new Date(data.expireDate));
								} 
						 	},
						 	validation: function (cell, value) {
						 		var now = new Date();
						 		var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
						 		if (value){
						 			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
							        if (data.datetimeManufactured){
							        	var mft = new Date(data.datetimeManufactured);
								        if (mft > new Date(value)){
								        	return { result: false, message: uiLabelMap.ExpireDateMustBeBeforeManufactureDate};
								        }
							        }
						 		} else {
						 			if (data.expRequired == 'Y') {
						 				return { result: false, message: uiLabelMap.DmsFieldRequired};
						 			}
						 		}
						       
						        return true;
							 },
						},
						{ text: uiLabelMap.QuantityReturned, dataField: 'returnQuantity', columntype: 'numberinput', width: 150, editable: true,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (value){
									return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + formatnumber(value) + '</span>';
								} else {
									if (data.productId){
										value = 0;
										return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + formatnumber(value) + '</span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
						        editor.jqxNumberInput({ decimalDigits: 0});
						        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
						        if (data.returnItemSeqId){
						        	editor.jqxNumberInput('val', data.returnQuantity);
						        }
						    },
						    validation: function (cell, value) {
						        if (value < 0){
						        	return { result: false, message: uiLabelMap.ExportValueMustBeGreaterThanZero};
						        }
						        if (value === null || value === undefined || value === ''){
						        	return { result: false, message: uiLabelMap.DmsFieldRequired};
						        }
						        if (value){
						        	var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
						        	var reqQuantity = data.requiredQuantity;
						        	var allRows = $('#jqxgridProductReturn').jqxGrid('getrows');
						        	var createdQty = 0;
						        	for (var i = 0; i < allRows.length; i ++){
						        		var rowData = allRows[i];
						        		if (rowData.uid != data.uid && rowData.productId == data.productId && rowData.orderItemSeqId == data.orderItemSeqId && rowData.orderId == data.orderId){
						        			createdQty = createdQty + rowData.returnQuantity;
						        		}
						        	}
						        	createdQty = createdQty + value;
						        	if (createdQty > reqQuantity){
						        		return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber + ': ' + createdQty + ' > '+ reqQuantity};
						        	}
						        }
						        return true;
							 },
						},
						 { text: uiLabelMap.Batch, dataField: 'lotId', width: 130, editable: true,
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								 if(data.statusId == 'RETURN_ACCEPTED'){
								 	if (value === null || value === undefined || value === ''){
								 		if (data.productId){
								 			return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
								 		} else {
								 			return '<span style=\"text-align: right\">...</span>';
								 		}
								 	} else {
								 		return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\" title=' + value.toLocaleString(localeStr) + '>' + value.toLocaleString(localeStr) + '</span>'
								 	}
						 		} else {	
									 if (value){
										 return '<span title=' + value + ' style=\"text-align: right\">' + value + '</span>';
									 } else {
										 return '<span style=\"text-align: right\"></span>';
									 }
							 	}
							 },
							 validation: function (cell, value) {
		                    	 if (!value) {
		                    		 var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
		                    		 if (data.lotRequired == "Y") {
		                    			 return { result: false, message: uiLabelMap.DmsFieldRequired};
		                    		 }
		                    	 }
		                    	 if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
		                    		 return { result: false, message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter};
		                    	 }
		                    	 return true;
							 },
						},
						{ text: uiLabelMap.IsPromo, dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (value === null || value === undefined || value === ''){
									if (data.productCode){
										 return '<span style=\"text-align: right\" class=\"focus-color\"></span>';
									 } else {
										 return '<span style=\"text-align: right;\">...</span>';
									 }
								} else {
									if (value == 'Y'){
										return '<span style=\"text-align: left\" class=\"focus-color\">' + uiLabelMap.LogYes + '</span>';
									}
									if (value == 'N'){
										return '<span style=\"text-align: left\" class=\"focus-color\">' + uiLabelMap.LogNO + '</span>';
									}
								}
							},
							cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								var code = data.productCode; 
								var count = 0;
								for(var i = 0; i < listReturnItemData.length; i++){
									var item = listReturnItemData[i];
									if (item.productCode == code){
										count = count + 1;
									}
								}
								if((data.deliveryItemSeqId === null || data.deliveryItemSeqId === undefined) && count > 1){
									return true;
								} else{
                                    return false;
                                }
							}, 
							initeditor: function(row, value, editor){
						        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
						        var listPromoStatus = [];
						        var map = {};
						        map['isPromoId'] = 'Y';
						        map['isPromoValue'] = 'Y';
						        listPromoStatus.push(map);
						        map = {};
						        map['isPromoId'] = 'N';
						        map['isPromoValue'] = 'N';
						        listPromoStatus.push(map);
						        editor.off('change');
						        editor.jqxDropDownList({ placeHolder: uiLabelMap.PleaseSelectTitle, source: listPromoStatus, selectedIndex: 0, dropDownWidth: '120px', popupZIndex: 755, displayMember: 'isPromoId', valueMember: 'isPromoValue',
			        			 	renderer: function(index, label, value) {
						                var item = editor.jqxDropDownList('getItem', index);
						                if (item.originalItem.isPromoId == 'Y'){
											return '<span style=\"text-align: left\">' +uiLabelMap.LogYes +'</span>';
										}
										if (item.originalItem.isPromoId == 'N'){
											return '<span style=\"text-align: left\">' +uiLabelMap.LogNO+ '</span>';
										}
					            	}
						        });
						        if (data.isPromo == 'Y'){
						        	editor.jqxDropDownList('selectItem', 'Y');
						        } else {
						        	editor.jqxDropDownList('selectItem', 'N');
						        }
						        var uid = data.uid;
						        var prCode = data.productCode;
						        editor.on('change', function (event){
									var args = event.args;
						     	    if (args) {
					     	    		var item = args.item;
						     		    if (item){
						     		    	var isPromo = item.value;
						     		    	var objTmp = 0;
						     		    	for (var e = 0; e < listReturnItemData.length; e ++){
						     		    		if (listReturnItemData[e].productCode == prCode && listReturnItemData[e].isPromo == isPromo){
						     		    			objTmp = listReturnItemData[e];
						     		    			break;
						     		    		}
						     		    	}
						     		    	$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', uid, 'alternativeQuantity', objTmp.returnQuantity);
						     		    	$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', uid, 'requiredQuantity', objTmp.returnQuantity);
						     		    	$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', uid, 'returnQuantity', 0);
						     		    	$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', uid, 'orderItemSeqId', objTmp.orderItemSeqId);
						     		    } 
						     	    }
						        });
							},
						
					 	},
						{ text: uiLabelMap.UnitPrice, dataField: 'returnPrice', columntype: 'numberinput', width: 150, editable: false,
							cellsrenderer: function (row, column, value){
								if(value){
									return '<span style=\"text-align: right; \">' + formatcurrency(value, currencyUomId) + '<span>';
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									if (data.productId){
										return '<span style=\"text-align: right; height: 100%;\"></span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
						        editor.jqxNumberInput({ decimalDigits: 0});
						        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
						        if (data.returnPrice){
						        	editor.jqxNumberInput('val', data.returnPrice);
						        }
						    },
						},
						{ text: uiLabelMap.Weight, dataField: 'selectedAmount', columntype: 'numberinput', width: 150, editable: false,
							cellsrenderer: function (row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if(value){ 
									return '<span style=\"text-align: right; \">' + formatnumber(value) + ' ' + getUomDescription(data.weightUomId)+ '<span>';
								} 
							},
						},
						{ text: uiLabelMap.Reason, dataField: 'returnReasonId', width: 200, editable: false, columntype: 'dropdownlist',
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (value === undefined || value === null || '' === value){
									if (data.productId){
										var id = data.uid;
										$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', id, 'returnReasonId', listReturnReason[0].returnReasonId);
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									for (var i = 0; i < listReturnReason.length; i ++){
										if (listReturnReason[i].returnReasonId == value){
											return '<span style=\"text-align: left\">' + listReturnReason[i].description + '</span>';
										}
									}
								}
							},
							initeditor: function(row, value, editor){
								editor.jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, autoDropDownHeight: false, dropDownHeight: '300px', source: listReturnReason, selectedIndex: 0, dropDownWidth: '200px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
							},
							createeditor: function (row, column, editor) {
								editor.jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, autoDropDownHeight: false, dropDownHeight: '300px', source: listReturnReason, selectedIndex: 0, dropDownWidth: '200px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
							},
						},
						{ text: uiLabelMap.ProductStatus, dataField: 'inventoryStatusId', width: 150, editable: false, columntype: 'dropdownlist',
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (value === undefined || value === null || '' === value){
									if (data.productId){
										return '<span style=\"text-align: left\">'+uiLabelMap.InventoryGood+'</span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									for (var i = 0; i < invStatusData.length; i ++){
										if (invStatusData[i].inventoryStatusId == value){
											return '<span style=\"text-align: left\">' + invStatusData[i].description + '</span>';
										}
									}
								}
							},
							initeditor: function (row, column, editor) {
								editor.jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, autoDropDownHeight: false, dropDownHeight: '100px', source: invStatusData, selectedIndex: 0, dropDownWidth: '150px', popupZIndex: 755, displayMember: 'description', valueMember: 'inventoryStatusId',});
								editor.jqxDropDownList('val', 'Good');
							},
						},
						{ text: uiLabelMap.CommonStatus, dataField: 'statusId', editable:true, width: 150, hidden: true,
							cellsrenderer: function (row, column, value){
								if(value){
									if ('CUSTOMER_RETURN' == returnHeaderTypeId){
										for (var i = 0; i < statusSOData.length; i ++){
											if (value == statusSOData[i].statusId){
												return '<span>' + statusSOData[i].description + '<span>';
											}
										}
									} else {
										for (var i = 0; i < statusPOData.length; i ++){
											if (value == statusPOData[i].statusId){
												return '<span>' + statusPOData[i].description + '<span>';
											}
										}
									}
								} else {
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									if (data.productId){
										return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							}
						},
						{ text: uiLabelMap.ActualExportedQuantity, dataField: 'alternativeQuantity', width: 150, editable:false,
							cellsrenderer: function (row, column, value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (value){
									return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
								} else {
									if (data.productId){
										return '<span style=\"text-align: right\">' + formatnumber(data.requiredQuantity) + '</span>';
									} else {
										return '<span style=\"text-align: right; height: 100%;\">...</span>';
									}
								}
							},
						},
						{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value){
								return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
									renderer: function(index, label, value){
							        	if (quantityUomData.length > 0) {
											for(var i = 0; i < quantityUomData.length; i++){
												if(quantityUomData[i].uomId == value){
													return '<span>' + quantityUomData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
								}
						},
	          		 ]
		    });
	}
	
	var addNewRow = function addNewRow(){
		var firstRow = $('#jqxgridProductReturn').jqxGrid('getrowdata', 0);
		if (firstRow.productId){
			$('#jqxgridProductReturn').jqxGrid('clearselection');
			var datarow = generaterow();
	        $("#jqxgridProductReturn").jqxGrid('addrow', null, datarow, "first");
	        $("#jqxgridProductReturn").jqxGrid('unselectrow', 0);
	        $("#jqxgridProductReturn").jqxGrid('begincelledit', 0, "productCode");
		} else {
			$("#jqxgridProductReturn").jqxGrid('begincelledit', 0, "productCode");
		}
	}
	
	function generaterow(productCode){
		var row = {};
		if (productCode){
			for(var i = 0; i < listReturnItemData.length; i++){
				var returnItem = listReturnItemData[i];
				if (returnItem.productCode == productCode){
					row["productId"] = returnItem.productId;
					row["productName"] = returnItem.productName;
					row["productCode"] = returnItem.productCode;
					row["returnId"] = returnItem.returnId;
					row["requiredQuantity"] = returnItem.requiredQuantity;
					row["returnQuantity"] = 0;
					row["inventoryStatusId"] = returnItem.inventoryStatusId;
					row["returnPrice"] = returnItem.returnPrice;
					row["alternativeQuantity"] = returnItem.alternativeQuantity;
					row["returnReasonId"] = returnItem.returnReasonId;
					row["statusId"] = returnItem.statusId;
					row["lotId"] = returnItem.lotId;
					row["orderItemSeqId"] = returnItem.orderItemSeqId;
					row["orderId"] = returnItem.orderId;
					row["returnTypeId"] = returnItem.returnTypeId;
					row["datetimeManufactured"] = returnItem.datetimeManufactured;
					row["expireDate"] = returnItem.expireDate;
					row["datetimeReceived"] = returnItem.datetimeReceived;
					row["quantityUomId"] = returnItem.quantityUomId;
					row["expRequired"] = returnItem.expRequired;
					row["mnfRequired"] = returnItem.mnfRequired;
					row["lotRequired"] = returnItem.lotRequired;
					row["isPromo"] = returnItem.isPromo;
					
					break;
				}
			}
		} else {
			row["productId"] = "";
			row["productName"] = "";
			row["productCode"] = "";
			row["returnId"] = "";
			row["returnItemSeqId"] = "";
			row["requiredQuantity"] = "";
			row["returnQuantity"] = "";
			row["inventoryStatusId"] = "";
			row["returnPrice"] = "";
			row["alternativeQuantity"] = "";
			row["returnReasonId"] = "";
			row["statusId"] = "RETURN_ACCEPTED";
			row["lotId"] = "";
			row["orderItemSeqId"] = "";
			row["orderId"] = "";
			row["returnTypeId"] = "";
			row["datetimeManufactured"] = "";
			row["expireDate"] = "";
			row["datetimeReceived"] = "";
			row["quantityUomId"] = "";
			row["expRequired"] = "";
			row["mnfRequired"] = "";
			row["lotRequired"] = "";
			row["isPromo"] = "";
		}
		return row;
	}
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridProductReturn").jqxGrid('getrowid', 0);
        $("#jqxgridProductReturn").jqxGrid('updaterow', id, datarow);
	}
	
	function checkGridReturnItemRequiredData(rowindex){
	    var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', rowindex);
	    if (data.productId){
	    	if(data.statusId == 'RETURN_ACCEPTED'){
		        if(data.returnQuantity === null || data.returnQuantity === undefined){
		            bootbox.dialog(uiLabelMap.QuantityNotEntered + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                "callback": function() {
	                        	$("#jqxgridProductReturn").jqxGrid('begincelledit', rowindex, "returnQuantity");
		                	}
		                }]
		            );
		            return true;
		        }
		        if (data.returnQuantity > 0){
		        	if(!data.datetimeManufactured && data.mnfRequired == 'Y'){
			            bootbox.dialog(uiLabelMap.MissingManufactureDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgridProductReturn").jqxGrid('begincelledit', rowindex, "datetimeManufactured");
			                }
			                }]
			            );
			            return true;
			        }
		        	if(!data.expireDate && data.expRequired == 'Y'){
			            bootbox.dialog(uiLabelMap.MissingExpireDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgridProductReturn").jqxGrid('begincelledit', rowindex, "expireDate");
			                }
			                }]
			            );
			            return true;
			        }
			        if(!data.lotId && data.lotRequired == 'Y'){
			            bootbox.dialog(uiLabelMap.MissingBacth + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgridProductReturn").jqxGrid('begincelledit', rowindex, "lotId");
			                }
			                }]
			            );
			            return true;
			        }
		        }
		    }
	    }
	    return false;
	}
	
	function getAndLoadReturnItem(){
		var listReturnItems = [];
		var facilityIdSelected = $('#destinationFacilityId').jqxDropDownList("val");
		$.ajax({
				 type: "POST",
				 url: "getReturnItemDetail",
				 data: {
					 returnId: returnId,
					 facilityId: facilityIdSelected,
				 },
				 dataType: "json",
				 async: false,
				 success: function(data){
					 listReturnItems = data.listReturnItems;
					 listReturnItemData = listReturnItems;
					 loadReturnItem(listReturnItems);
					 $("#jqxgridProductReturn").jqxGrid('updatebounddata');
				 },
	 		}).done(function(data) {
 		});
		
	}
	
	return {
		init: init,
		getLocalization: getLocalization,
		formatFullDate: formatFullDate,
		getFormattedDate: getFormattedDate,
		renderHtmlContainGrids: renderHtmlContainGrids,
		loadReturnItem: loadReturnItem,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
	};
}());