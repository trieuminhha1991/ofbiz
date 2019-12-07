$(function(){
	ExportReqObj.init();
});
var ExportReqObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		if ("REQ_APPROVED" != curStatusId){
			window.location.href = "viewRequirementDetail?requirementId="+requirementId;
		}
		if (requiredFacilityId != undefined && requiredFacilityId != null){
			$('#facilityIdExpt').val(requiredFacilityId);
			for (j in facilityData) {
				if (facilityData[j].facilityId == requiredFacilityId) $("#facilityReq").text(facilityData[j].facilityName);
			}
		} 
		var curFacilityId = $('#facilityIdExpt').val();
		var contactMechData = [];
		$('#contactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle,  selectedIndex: 0, width: 200, source: contactMechData, theme: theme, displayMember: 'description', valueMember: 'contactMechId',});
		update({
			facilityId: curFacilityId,
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
		
		var listRequirementItems = [];
		$.ajax({
				 type: "POST",
				 url: "getRequirementItems",
				 data: {
					 requirementId: requirementId,
				 },
				 dataType: "json",
				 async: false,
				 success: function(data){
					 listRequirementItems = [];
					 var tmp = data.listRequirementItems;
					 for (var i = 0; i < tmp.length; i ++){
						 var item = tmp[i];
						 if (item.expireDate){
							 item['expireDate'] = item.expireDate.time;
							 if (item.fromExpiredDate){
								 item['fromExpiredDate'] = item.fromExpiredDate.time;
							 }
							 if (item.toExpiredDate){
								 item['toExpiredDate'] = item.toExpiredDate.time;
							 }
							 listRequirementItems.push(item);
						 } else {
							 listRequirementItems.push(item);
						 }
					 }
				 },
	 		}).done(function(data) {
 		});
		listRequirementItemData = listRequirementItems;
		loadRequirementItem(listRequirementItems);
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$("#jqxgridRequirementItem").on("pagechanged", function (event) {
		    // event arguments.
		    var args = event.args;
		    // page number.
		    var pagenum = args.pagenum;
		    // page size.
		    var pagesize = args.pagesize;
		    var cell = $('#jqxgridRequirementItem').jqxGrid('getselectedcell');
		    $("#jqxgridRequirementItem").jqxGrid('endcelledit', cell.rowindex, cell.datafield, true, true);
		}); 
		
//		$("#facilityIdExpt").on('change', function(event){
//			curFacilityId = $('#facilityIdExpt').jqxDropDownList('val');
//			$('#jqxgridRequirementItem').jqxGrid('updatebounddata'); 
//		});
		 
		$("#exportProduct").click(function () {
			var listRequirementItemSelected = [];
			var selectedItems = [];
			var allRows =  $('#jqxgridRequirementItem').jqxGrid('getrows');
	        for (var id = 0; id < allRows.length; id ++){
	        	if(checkGridRequirementItemRequiredData(allRows[id].uid) == true){ 
	                return false;
	            }
	        	if (allRows[id].statusId == "REQ_APPROVED"){
	        		if (allRows[id].exportQuantity > 0 || allRows[id].exportQuantity === undefined) {
	        			selectedItems.push(allRows[id]);
	        		}
	        	}
	        }
	        
	        var invSelected = [];
	        for(var i = 0; i < selectedItems.length; i ++){
	        	var check = false;
	        	for (j = 0; j < invSelected.length; j ++){
	        		if (invSelected[j].inventoryItemId == selectedItems[i].inventoryItemId){
	        			check = true;
	        			break;
	        		}
	        	}
	        	if (check == false){
	        		var map = {
	        			inventoryItemId: selectedItems[i].inventoryItemId,	
	        		};
	        		invSelected.push(map);
	        	}
	        }
	        for (var i = 0; i < invSelected.length; i ++){
	        	var obj = null;
	        	var total = 0;
	        	for (var j = 0; j < selectedItems.length; j++){
	        		if (invSelected[i].inventoryItemId == selectedItems[j].inventoryItemId){
	        			obj = selectedItems[j];
	        			total = total + selectedItems[j].exportQuantity;
	        		}
	        	}
	        	obj["exportQuantity"] = total; 
	        	listRequirementItemSelected.push(obj);
	        }
			bootbox.dialog(uiLabelMap.AreYouSureYouWantToExport, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll();}
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	for (var i = 0; i < listRequirementItemSelected.length; i ++){
						listRequirementItemSelected[i]["productName"] = '';
	            	}
	            	listRequirementItemSelected = JSON.stringify(listRequirementItemSelected);
	            	Loading.show('loadingMacro');
	            	setTimeout(function(){
		            	$.ajax({
			   				 type: "POST",
			   				 url: "exportProductFromRequirement",
			   				 data: {
			   					 requirementId: requirementId,
			   					 contactMechId: $("#contactMechId").val(),
		            			 facilityId: $("#facilityIdExpt").val(),
			   					 listRequirementItems: listRequirementItemSelected,
			   				 },
			   				 dataType: "json",
			   				 async: false,
			   				 success: function(data){
			   				 },
			   				 error: function(response){
			   				 }
		   		 		}).done(function(data) {
		   		 			window.location.href = "viewRequirementDetail?requirementId="+requirementId;
			      		});
		            	Loading.hide('loadingMacro');
	            	}, 500);
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
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	 var entityMap = {
			    "&": "&amp;",
			    "<": "&lt;",
			    ">": "&gt;",
			    '"': '&quot;',
			    "'": '&#39;',
			    "/": '&#x2F;'
			 };
	 var renderHtmlContainGrids = function renderHtmlContainGrids() {
		 var htmlRenderTabs = "<div id='jqxTabsreturnItem' style='margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px' id='tabDynamic'>";
		 var htmlRenderGrids = "";
		 htmlRenderTabs += "<li value=\"acas\" style=\"margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;\">accs</li>";
		 htmlRenderGrids += "<div style='overflow: hidden;'><div style='border:none;' id=\"123\" ></div></div>";
		 htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		 $("#jqxTabsContain").html(htmlRenderTabs);
	 }
	 
	 var loadRequirementItem = function loadRequirementItem(valueDataSoure){
			var sourceTmp = [];
			ExportReqObj.renderHtmlContainGrids;
			var sourceProduct = {
		        datafields:[{ name: 'orderId', type: 'string' },
							{ name: 'productId', type: 'string' },
							{ name: 'productCode', type: 'string' },
							{ name: 'productName', type: 'string' },
							{ name: 'quantityUomId', type: 'string' },
							{ name: 'weightUomId', type: 'string' },
							{ name: 'uomId', type: 'string' },
							{ name: 'requireAmount', type: 'string' },
							{ name: 'quantity', type: 'number' },
							{ name: 'weight', type: 'number' },
							{ name: 'inventoryStatusId', type: 'string' },
							{ name: 'requirementId', type: 'string' },
							{ name: 'reqItemSeqId', type: 'string' },
							{ name: 'unitCost', type: 'number' },
							{ name: 'returnReasonId', type: 'string' },
							{ name: 'statusId', type: 'string' },
							{ name: 'lotId', type: 'string' },
							{ name: 'orderItemSeqId', type: 'string' },
							{ name: 'inventoryItemId', type: 'string' },
							{ name: 'returnTypeId', type: 'string' },
							{ name: 'baseWeightUomId', type: 'string' },
							{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
							{ name: 'expireDate', type: 'date', other: 'Timestamp' },
							{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
							{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
							{ name: 'actualExecutedQuantity', type: 'number' },
							{ name: 'exportQuantity', type: 'number' },
							{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
							{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
							],
							localdata: valueDataSoure,
					        datatype: "array",
					    };
					    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
					    var check = false;
					    $("#jqxgridRequirementItem").jqxGrid({
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
					        columns: [	{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
							    groupable: false, draggable: false, resizable: false,
							    datafield: '', columntype: 'number', width: 50,
							    cellsrenderer: function (row, column, value) {
							        return '<div style=margin:4px;>' + (value + 1) + '</div>';
							    }
							},		
							{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 150, editable: true, pinned: true, columntype: 'dropdownlist',
								createeditor: function (row, cellvalue, editor) {
									var sourceDataProduct =
									{
						               localdata: listRequirementItemData,
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
							     		    	ExportReqObj.updateRowData(item.value);
							     		    } 
							     	    }
							        });
								 },
								 cellbeginedit: function (row, datafield, columntype) {
									 var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									 if (data.productId){
										 return false;
									 }
									 return true;
								 },
								 cellsrenderer: function(row, column, value){
									 var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									 if (!data.productId){
										 return '<span>'+uiLabelMap.PleaseSelectTitle+'</span>';
									 }
									 return '<span>'+value+'</span>';
								 }
							},
							{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable:false,
								cellsrenderer: function(row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (!data.productId){
										return '<span style=\"text-align: right\">...</span>';
									}
								}
							},
							{ text: uiLabelMap.LogInventoryItem, dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 350, editable: true, sortable: false,
							    cellsrenderer: function(row, column, value){
							        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
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
								        					return '<span style=\"text-align: right\" class=\"focus-color\"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
								        				} else {
								        					return '<span style=\"text-align: right\" class=\"focus-color\"></span>';
								        				}
								        			}
								        		}
							        		} else {
							        			var exp = data.expiredDate;
								                return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(exp)) + '</span>';
							        		}
						        		} else {
						        			if (data.productCode){
							            		if (data.statusId == 'REQ_APPROVED'){
							            			var id = data.uid;
								            		var t = true;
								            		if (curFacilityId == null){
										        		curFacilityId = $('#facilityIdExpt').val();
										        	}
								            		var requireAmount = data.requireAmount;
										 			if (data.exportQuantity === '' || data.exportQuantity === null || data.exportQuantity === undefined) {
										 				for(i = 0; i < listInv.length; i++){
											 				var remainQty = data.quantity;
											 				var qoh = listInv[i].quantityOnHandTotal;
											 				var actualExecuted = data.actualExecutedQuantity;
											 				var reqQty = data.quantity;
											 				if (requireAmount && requireAmount == 'Y') {
											 					remainQty = data.weight;
											 					if (data.actualExecutedWeight && data.actualExecutedWeight > 0){
												 					remainQty = data.weight - data.actualExecutedWeight;
												 				}
											 					qoh = listInv[i].amountOnHandTotal;
											 					reqQty = data.weight;
											 					actualExecuted = data.actualExecutedWeight;
											 				} else {
											 					if (data.actualExecutedQuantity && data.actualExecutedQuantity > 0){
												 					remainQty = data.quantity - data.actualExecutedQuantity;
												 				}
											 				}
											 				
											 				if (data.productId == listInv[i].productId && qoh >= 0 && qoh >= remainQty && curFacilityId == listInv[i].facilityId){
												 				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
												 				if (actualExecuted){
												 					$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', reqQty - actualExecuted);
												 				} else {
												 					$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', reqQty);
												 				}
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
										        					return '<span style=\"text-align: right\" class=\"focus-color\"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
										        				} else {
										        					return '<span style=\"text-align: right\" class=\"focus-color\"></span>';
										        				}
												 			} else {
												 				check = false;
												 				if (i == listInv.length - 1){
													 				for (var k = 0; k < listInv.length; k ++){
													 					// select one of inventory has qoh > 0 
													 					var qohTemp = data.quantityOnHandTotal;
													 					if (requireAmount && requireAmount == 'Y') {
													 						var qohTemp = data.amountOnHandTotal;
													 					}
													 					if (data.productId == listInv[i].productId && qohTemp >= 0 && curFacilityId == listInv[k].facilityId){
													 						check = true;
													 						$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[k].inventoryItemId);
															 				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'exportQuantity', qohTemp);
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
													        					return '<span style=\"text-align: right\" class=\"focus-color\"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
													        				} else {
													        					return '<span style=\"text-align: right\" class=\"focus-color\"></span>';
													        				}
												        				}
													 				}
												 					return '<span style=\"text-align: left\" class=\"warning-color\"> ' +uiLabelMap.NotEnough+ '</span>';
												 				} else {
												 					continue;
												 				}
											 				}
										 				}
										 			} else {
										 				for(i = 0; i < listInv.length; i++){
											 				var qoh = listInv[i].quantityOnHandTotal;
											 				if (requireAmount && requireAmount == 'Y') {
											 					qoh = listInv[i].amountOnHandTotal;
											 				}
											 				if (data.productId == listInv[i].productId && qoh >= data.exportQuantity && curFacilityId == listInv[i].facilityId){
												 				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
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
										        					return '<span style=\"text-align: right\" class=\"focus-color\"> ' +uiLabelMap.ExpiredDateSum+ ': ' +exp+ ' - '+uiLabelMap.ManufacturedDateSum+': ' + mnf + ' - '+uiLabelMap.ReceivedDateSum+': ' + rcd + '</span>';
										        				} else {
										        					return '<span style=\"text-align: right\" class=\"focus-color\"></span>';
										        				}
											 				
											 				} 
										 				}
										 				check = false;
										 				return '<span style=\"text-align: left\" class=\"warning-color\"> ' +uiLabelMap.NotEnough+ '</span>';
										 			}
											 		if (check == false){
											 			var missingQty = 0;
											 			if (requireAmount && requireAmount == 'Y') {
											 				if (data.actualExecutedWeight){
												 				missingQty = data.weight - data.actualExecutedWeight;
												 			} else {
												 				missingQty = data.weight;
												 			}
											 			} else {
											 				if (data.actualExecutedQuantity){
												 				missingQty = data.quantity - data.actualExecutedQuantity;
												 			} else {
												 				missingQty = data.quantity;
												 			}
											 			}
											 			
											 			return '<span title=\"'+uiLabelMap.NotEnough+': ' +formatnumber(missingQty)+ '\" style=\"text-align: left\" class=\"warning-color\">'+uiLabelMap.NotEnough+'</span>';
											 		}
								            	} else {
								            		return '<span style=\"text-align: right;\" class=\"focus-color\"></span>';
								            	}
							            	} else {
												return '<span style=\"text-align: right;\" class=\"focus-color\">...</span>';
											}
							            }
							        	return '<span style=\"text-align: right\" class=\"focus-color\"><span>';
							        }
							    }, 
							    initeditor: function(row, value, editor){
								    var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
								    var requireAmount = data.requireAmount;
								    var uid = data.uid;
								    var invData = [];
								    var curInvId = null;
								    if (data.inventoryItemId != null && data.inventoryItemId != undefined){
								    	curInvId = data.inventoryItemId;
								    }
								    var invEnoughTmp = null;
							        for(i = 0; i < listInv.length; i++){
							        	if (curFacilityId == null){
							        		curFacilityId = $('#facilityIdExpt').val();
							        	}
							        	if (curFacilityId == listInv[i].facilityId){
							        		if(listInv[i].productId == data.productId){
							        			var qoh = listInv[i].quantityOnHandTotal;
							        			if (requireAmount && requireAmount == 'Y') {
							        				qoh = listInv[i].amountOnHandTotal;
							        			}
							        			
							        			if (qoh >= data.exporteQuantity){
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
								                
								                tmpValue.quantityCurrent = qoh;
								                
								                var qtyUom = getUomDescription(listInv[i].quantityUomId);
								                if (requireAmount && requireAmount == 'Y') {
								                	qtyUom = getUomDescription(listInv[i].weightUomId);
								                }
								                tmpValue.qtyUom = qtyUom;
								                invData.push(tmpValue);
								            }
							        	}
							        }
							        if (invData.length > 0){
							        	if (curInvId != null){
							        		var curInv = null;
							        		var curQuantity = 0;
							        		var allrowTmp = $('#jqxgridRequirementItem').jqxGrid('getrows');
							        		for(var j = 0; j < allrowTmp.length; j++){
							        			if (allrowTmp[j].inventoryItemId == curInvId){
							        				curQuantity = curQuantity + allrowTmp[j].exportQuantity;
							        			}
							        		}
							        		for(var i = 0; i < invData.length;i++){
							        			if(invData[i].inventoryItemId == curInvId){
							        				invData[i].quantityCurrent = invData[i].quantityCurrent - curQuantity;
							        			} else {
							        				var qtyOfOtherInv = 0;
							        				for(var j = 0; j < allrowTmp.length; j++){
							        					if (allrowTmp[j].inventoryItemId == invData[i].inventoryItemId){
							        						qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].exportQuantity;
							        					}
							        				}
							        				invData[i].quantityCurrent = invData[i].quantityCurrent - qtyOfOtherInv;
							        			}
							        		}
								        	editor.jqxDropDownList({selectedIndex: 0, placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, dropDownWidth: '650px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
									            renderer: function(index, label, value) {
									                var item = editor.jqxDropDownList('getItem', index);
									                var tmp = item.originalItem.quantityCurrent;
									                if (item.originalItem.quantityCurrent < 0){
									                	tmp = '- ' + formatnumber(Math.abs(item.originalItem.quantityCurrent));
									                } else {
									                	tmp = formatnumber(Math.abs(item.originalItem.quantityCurrent));
									                }
									                return '[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - [<span style=\"color:blue;\">'+uiLabelMap.ManufacturedDateSum+':</span>&nbsp;' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">'+uiLabelMap.ReceivedDateSum+':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + formatnumber(item.originalItem.quantityOnHandTotal) + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + tmp + ']&nbsp;</span>';
									            }
									        });
								        }
							        } else {
							        	editor.jqxDropDownList({selectedIndex: 0, placeHolder: uiLabelMap.PleaseSelectTitle, source: invData, dropDownWidth: '650px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
								            renderer: function(index, label, value) {
								                var item = editor.jqxDropDownList('getItem', index);
								                var tmp = item.originalItem.quantityCurrent;
								                if (item.originalItem.quantityCurrent < 0){
								                	tmp = '-' + formatnumber(Math.abs(item.originalItem.quantityCurrent));
								                } else {
								                	tmp = formatnumber(Math.abs(item.originalItem.quantityCurrent));
								                }
								                return '[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - [<span style=\"color:blue;\">'+uiLabelMap.ManufacturedDateSum+':</span>&nbsp;' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">'+uiLabelMap.ReceivedDateSum+':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + formatnumber(item.originalItem.quantityOnHandTotal) + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + tmp + 	']&nbsp;</span>';
								            }
								        });
						        		if (invEnoughTmp != null){
						        			editor.jqxDropDownList('selectItem', invEnoughTmp);
						        		}
							        }
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
									 if (!check){
										 return false;
									 }
									 return true;
								 },
							}, 	
							{ text: uiLabelMap.ExportQuantity, dataField: 'exportQuantity', columntype: 'numberinput', width: 130, editable: true,
								cellsrenderer: function(row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (value === undefined || value === null || value === ''){
										if (data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId === ''){
											if (data.productCode){
												value = 0;
												return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + formatnumber(value) + '</span>';
											} else {
												return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">...</span>';
											} 
										} else {
											for (var j = 0; j < listInv.length; j ++){
												if (data.inventoryItemId == listInv[j].inventoryItemId){
													var requireAmount = data.requireAmount;
													if (requireAmount && requireAmount == 'Y') {
														if (data.weight > listInv[j].amountOnHandTotal){
															value = listInv[j].amountOnHandTotal;
														} else {
															value = data.weight;
														}
													} else {
														if (data.quantity > listInv[j].quantityOnHandTotal){
															value = listInv[j].quantityOnHandTotal;
														} else {
															value = data.quantity;
														}
													}
													return '<span style=\"text-align: right; background-color: #e6ffb8; v\">' + formatnumber(value) + '</span>';
												}
											}
											value = 0;
											return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + formatnumber(value) + '</span>';
										}
									} else {
										return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%\">' + formatnumber(value) + '</span>';
									}
								},
								initeditor: function(row, value, editor){
							        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							        var requireAmount = data.requireAmount;
							        if(data.statusId != 'REQ_APPROVED'){
	                                    editor.jqxNumberInput({disabled: true});
	                                }else{
	                                	editor.jqxNumberInput({decimalDigits: 0, disabled: false});
	                                	if (requireAmount && requireAmount == 'Y') {
	                                		editor.jqxNumberInput({decimalDigits: 2, disabled: false});
	                                	}
	                                    if (value != null && value != undefined){
	                                    	editor.jqxNumberInput('val', value);
	                                    } else {
	                                    	if (data.productCode != null && data.productCode != undefined){
	                                    		if (check){
	                                    			if (requireAmount && requireAmount == 'Y') {
	                                    				if (data.actualExecutedWeight != null && data.actualExecutedWeight != undefined && data.actualExecutedWeight != ''){
			                                    			if (data.weight){
				                                            	editor.jqxNumberInput('val', data.weight - data.actualExecutedWeight);
				                                            }
			                                    		} else {
			                                    			if (data.weight){
				                                            	editor.jqxNumberInput('val', data.weight);
				                                            }
			                                    		}
	                                    			} else {
	                                    				if (data.actualExecutedQuantity != null && data.actualExecutedQuantity != undefined && data.actualExecutedQuantity != ''){
			                                    			if (data.quantity){
				                                            	editor.jqxNumberInput('val', data.quantity - data.actualExecutedQuantity);
				                                            }
			                                    		} else {
			                                    			if (data.quantity){
				                                            	editor.jqxNumberInput('val', data.quantity);
				                                            }
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
							        var dataTmp = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
							        var prCode = dataTmp.productCode;
							        var requireAmount = dataTmp.requireAmount;
							        var rows = $('#jqxgridRequirementItem').jqxGrid('getrows');
									
							        var listByPr = [];
							        for (var i = 0; i < rows.length; i ++){
							        	if (prCode == rows[i].productCode){
											 listByPr.push(rows[i]);
							        	} 
							        }
							        
							        var allDlvQty = 0;
							        for (var i = 0; i < listRequirementItemData.length; i ++){
							        	if (listRequirementItemData[i].productCode == prCode){
							        		if (requireAmount && requireAmount == 'Y') {
							        			if (listRequirementItemData[i].actualExecutedWeight){
								        			allDlvQty = allDlvQty + actualExecutedWeight[i].weight - listRequirementItemData[i].actualExecutedWeight								        		} else {
								        			allDlvQty = allDlvQty + listRequirementItemData[i].weight;
								        		}
							        		} else {
							        			if (listRequirementItemData[i].actualExecutedQuantity){
								        			allDlvQty = allDlvQty + listRequirementItemData[i].quantity - listRequirementItemData[i].actualExecutedQuantity;
								        		} else {
								        			allDlvQty = allDlvQty + listRequirementItemData[i].quantity;
								        		}
							        		}
							        	}
									}
							        var curQty = 0;
							        for (var i = 0; i < listByPr.length; i ++){
							        	if (listByPr[i].productCode == prCode){
							        		if (listByPr[i].exportQuantity != undefined){
							        			curQty = curQty + listByPr[i].exportQuantity;
							        		} else {
							        			if (requireAmount && requireAmount == 'Y') {
							        				curQty = curQty + listByPr[i].quantity;
							        			} else {
							        				curQty = curQty + listByPr[i].weight;
							        			}
							        		}
							        	}	
							        }
							        var totalCreated = 0;
							        if (dataTmp.exportQuantity != undefined){
							        	totalCreated = curQty + value - parseInt(dataTmp.exportQuantity);
							        } else {
							        	if (requireAmount && requireAmount == 'Y') {
								        	if (dataTmp.actualExecutedWeight){
								        		totalCreated = curQty + value - parseInt(dataTmp.weight - dataTmp.actualExecutedWeight);
								        	} else {
								        		totalCreated = curQty + value - parseInt(dataTmp.weight);
								        	}
							        	} else {
							        		if (dataTmp.actualExecutedQuantity){
								        		totalCreated = curQty + value - parseInt(dataTmp.quantity - dataTmp.actualExecutedQuantity);
								        	} else {
								        		totalCreated = curQty + value - parseInt(dataTmp.quantity);
								        	}
							        	}
							        }
							        if (totalCreated - allDlvQty > 0){
							        	return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber +': ' + totalCreated + ' > ' + allDlvQty};
							        }
							        return true;
								 },
							},
							{ text: uiLabelMap.RequiredNumber, dataField: 'quantity', columntype: 'numberinput', width: 150, editable: false,
								cellsrenderer: function(row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (data.productCode){
										var requireAmount = data.requireAmount;
										if (requireAmount && requireAmount == 'Y') {
											return '<span style=\"text-align: right;\">' + formatnumber(data.weight) + '</span>';
										} else {
											return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
										}
									} else {
										return '<span style=\"text-align: right;\">...</span>';
									}
								},
							},
							{ text: uiLabelMap.EXPRequired, dataField: 'expireDate', align: 'left', width: 200, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
								cellsrenderer: function(row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (value){
										var str = DatetimeUtilObj.getFormattedDate(new Date(value));
										if (data.fromExpiredDate) {
											str = DatetimeUtilObj.getFormattedDate(new Date(data.fromExpiredDate));
										} 
										if (data.toExpiredDate) {
											str = str + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(data.toExpiredDate));
										}
										return '<span style=\"text-align: right;\">'+str+'</span>';
									} else {
										if (data.productCode){
											return '<span style=\"text-align: right;\"></span>';
										} else {
											return '<span style=\"text-align: right;\">...</span>';
										}
									}
								}
							},
							{ text: uiLabelMap.Unit, dataField: 'quantityUomId', width: 150, editable:false,
								cellsrenderer: function (row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (value){
										var requireAmount = data.requireAmount;
										if (requireAmount && requireAmount == 'Y') {
											return '<span style=\"text-align: right;\">' + getUomDescription(data.weightUomId) + '</span>';
										} else {
											return '<span style=\"text-align: right;\">' + getUomDescription(value) + '</span>';
										}
									} else {
										return '<span style=\"text-align: right;\">...</span>';
									}
								},
							},
							{ text: uiLabelMap.UnitPrice, dataField: 'unitCost', columntype: 'numberinput', width: 150, editable: false,
								cellsrenderer: function (row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if(value){
										return '<span style=\"text-align: right;\">' + formatnumber(value) + '<span>';
									} else {
										if (data.productCode){
											return '<span style=\"text-align: right;\"></span>';
										} else {
											return '<span style=\"text-align: right;\">...</span>';
										}
									}
								},
								initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
							        editor.jqxNumberInput({ decimalDigits: 3});
							        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							        if (data.returnPrice){
							        	editor.jqxNumberInput('val', data.returnPrice);
							        }
							    },
							},
							{ text: uiLabelMap.QuantityExported, dataField: 'actualExecutedQuantity', width: 150, editable:false,
								cellsrenderer: function (row, column, value){
									var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
									if (value){
										return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
									} else {
										if (data.productCode){
											value = 0;
											return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
										} else {
											return '<span style=\"text-align: right;\">...</span>';
										}
									}
								},
							},
							]
					    });
				}
	 
	 var addNewRow = function addNewRow(){
		 var firstRow = $('#jqxgridRequirementItem').jqxGrid('getrowdata', 0);
		 if (firstRow.productId){
			 $('#jqxgridRequirementItem').jqxGrid('clearselection');
			 var datarow = generaterow();
			 $("#jqxgridRequirementItem").jqxGrid('addrow', null, datarow, "first");
			 $("#jqxgridRequirementItem").jqxGrid('unselectrow', 0);
			 $("#jqxgridRequirementItem").jqxGrid('begincelledit', 0, "productCode");
		 } else {
			$("#jqxgridRequirementItem").jqxGrid('begincelledit', 0, "productCode");
		 }
	 }
	 function generaterow(productCode){
		 var row = {};
		 if (productCode){
			 for(var i = 0; i < listRequirementItemData.length; i++){
				 var reqItem = listRequirementItemData[i];
				 if (reqItem.productCode == productCode){
					 row["orderId"] = reqItem.orderId;
					 row["productId"] = reqItem.productId;
					 row["productCode"] = reqItem.productCode;
					 row["productName"] = reqItem.productName;
					 row["quantityUomId"] = reqItem.quantityUomId;
					 row["quantity"] = reqItem.quantity;
					 row["inventoryStatusId"] = reqItem.inventoryStatusId;
					 row["requirementId"] = reqItem.requirementId;
					 row["reqItemSeqId"] = reqItem.reqItemSeqId;
					 row["unitCost"] = reqItem.unitCost;
					 row["returnReasonId"] = reqItem.returnReasonId;
					 row["statusId"] = reqItem.statusId;
					 row["lotId"] = reqItem.lotId;
					 row["orderItemSeqId"] = reqItem.orderItemSeqId;
					 row["inventoryItemId"] = reqItem.inventoryItemId;
					 row["returnTypeId"] = reqItem.returnTypeId;
					 row["expiredDate"] = reqItem.expiredDate;
					 row["expireDate"] = reqItem.expireDate;
					 row["datetimeManufactured"] = reqItem.datetimeManufactured;
					 row["datetimeReceived"] = reqItem.datetimeReceived;
					 row["actualExecutedQuantity"] = reqItem.actualExecutedQuantity;
					 row["actualExecutedWeight"] = reqItem.actualExecutedWeight;
					 row["baseWeightUomId"] = reqItem.baseWeightUomId;
					 row["weightUomId"] = reqItem.weightUomId;
					 row["weight"] = reqItem.weight;
					 row["requireAmount"] = reqItem.requireAmount;
					 row["exportQuantity"] = 0;
					 row["fromExpiredDate"] = reqItem.fromExpiredDate;
					 row["toExpiredDate"] = reqItem.toExpiredDate;
					 break; 
				 }
			 }
		 } else {
			 row["orderId"] = "";
			 row["productId"] = "";
			 row["productCode"] = "";
			 row["productName"] = "";
			 row["quantityUomId"] = "";
			 row["quantity"] = "";
			 row["inventoryStatusId"] = "";
			 row["requirementId"] = "";
			 row["reqItemSeqId"] = "";
			 row["unitCost"] = "";
			 row["returnReasonId"] = "";
			 row["statusId"] = "REQ_APPROVED";
			 row["lotId"] = "";
			 row["orderItemSeqId"] = "";
			 row["inventoryItemId"] = "";
			 row["expiredDate"] = "";
			 row["expireDate"] = "";
			 row["datetimeManufactured"] = "";
			 row["datetimeReceived"] = "";
			 row["actualExecutedQuantity"] = "";
			 row["exportQuantity"] = "";
			 row["fromExpiredDate"] = "";
			 row["toExpiredDate"] = "";
			 
			 row["actualExecutedWeight"] = "";
			 row["baseWeightUomId"] = "";
			 row["weightUomId"] = "";
			 row["weight"] = "";
			 row["requireAmount"] = "";
		 }
		 return row;
	}
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridRequirementItem").jqxGrid('getrowid', 0);
        $("#jqxgridRequirementItem").jqxGrid('updaterow', id, datarow);
	}
	 
	
	function checkGridRequirementItemRequiredData(rowindex){
	    var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', rowindex);
	    var requireAmount = data.requireAmount;
	    if(data.statusId == 'REQ_APPROVED'){
	    	if (listInv.length > 0 && data.inventoryItemId != null && data.inventoryItemId != undefined && data.inventoryItemId != ''){
	    		for(i = 0; i < listInv.length; i++){
	    	        if(listInv[i].inventoryItemId == data.inventoryItemId){
	    	        	var qoh = listInv[i].quantityOnHandTotal;
	    	        	if (requireAmount && requireAmount == 'Y') {
	    	        		qoh = listInv[i].amountOnHandTotal;
	    	        	} 
	    	            if (qoh < data.exportQuantity){
	    	            	bootbox.dialog(uiLabelMap.ExpiredDateSelectedNotEnoughSelectAnother, [{
	    		                "label" : uiLabelMap.OK,
	    		                "class" : "btn btn-primary standard-bootbox-bt",
	    		                "icon" : "fa fa-check",
	    		                "callback": function() {
	    		                    $("#jqxgridRequirementItem").jqxGrid('begincelledit', rowindex, "inventoryItemId");
	    		                }
	    		                }]
	    		            );
	    	            	return true;
	    	            }
	    	        }
	    	    }
	    	}
    		if(data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId == ''){
    			if (data.exportQuantity <= 0 || data.exportQuantity === undefined || data.exportQuantity === null || data.exportQuantity === ''){
    				jOlbUtil.alert.error(uiLabelMap.NotEnough);
    				return true;
    			}
	        } else if (data.exportQuantity === null || data.exportQuantity === undefined){
	            bootbox.dialog(uiLabelMap.PleaseEnterQuantityExported, [{
	                "label" : uiLabelMap.OK,
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                    $("#jqxgridRequirementItem").jqxGrid('begincelledit', rowindex, "exportQuantity");
	                }
	            }]
	            );
	            return true;
	        }
	    }
	    return false;
	}
	
	return {
		init: init,
		getLocalization: getLocalization,
		formatFullDate: formatFullDate,
		getFormattedDate: getFormattedDate,
		loadRequirementItem: loadRequirementItem,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
	};
}());