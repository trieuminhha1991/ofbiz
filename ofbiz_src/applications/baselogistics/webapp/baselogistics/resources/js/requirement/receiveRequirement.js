$(function(){
	ReceiveReqObj.init();
});
var ReceiveReqObj = (function(){
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
		var contactMechData = [];
		$('#contactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle,  selectedIndex: 0, width: 200, source: contactMechData, theme: theme, displayMember: 'description', valueMember: 'contactMechId',});
		getDetailRequirement(requirementId);
		update({
			facilityId: $("#facilityId").val(),
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
		for (var i = 0; i < listRequirementItemData.length; i ++){
			var data = listRequirementItemData[i];
			var requireAmount = data.requireAmount;
			var quantityTmp = 0;
			if (requireAmount && requireAmount == 'Y') {
				if (data.actualExecutedWeight){
					quantityTmp = data.weight - data.actualExecutedWeight;
				} else {
					quantityTmp = data.weight;
				}
			} else {
				if (data.actualExecutedQuantity){
					quantityTmp = data.quantity - data.actualExecutedQuantity;
				} else {
					quantityTmp = data.quantity;
				}
			}
			listRequirementItemData[i]["receiveQuantity"] = quantityTmp;
		}
		loadRequirementItem(listRequirementItems);
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		if (requirementTypeId == "RECEIVE_REQUIREMENT"){
			$('#facilityId').on('change', function(event){
				update({
					facilityId: $("#facilityId").val(),
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
			});
		} else if (requirementTypeId == "EXPORT_REQUIREMENT"){
			$('#facilityId').on('change', function(event){
				update({
					facilityId: $("#facilityId").val(),
					contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
			});
		}
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
		
		$("#receiveProduct").click(function () {
			var selectedItems = [];
			var allRows = $('#jqxgridRequirementItem').jqxGrid('getrows');
	        for (var id = 0; id < allRows.length; id ++){
	        	if (allRows[id].productId && allRows[id].receiveQuantity > 0){
	        		var data = allRows[id];
	        		var rowindex = $('#jqxgridRequirementItem').jqxGrid('getrowboundindexbyid', data.uid);
	        		if(!data.datetimeManufactured && data.mnfRequired == "Y"){
			            bootbox.dialog(uiLabelMap.MissingManufactureDate + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgridRequirementItem").jqxGrid('begincelledit', rowindex, "datetimeManufactured");
			                }
			                }]
			            );
			            return false;
			        }
			        if(!data.actualExpireDate && data.expRequired == "Y"){
		            bootbox.dialog(uiLabelMap.TheExpiredDateFieldNotYetBeEntered + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                "callback": function() {
		                    $("#jqxgridRequirementItem").jqxGrid('begincelledit', rowindex, "actualExpireDate");
		                }
		                }]
		            );
		            return false;
			        }
			        if(!data.lotId  && data.lotRequired == "Y"){
			            bootbox.dialog(uiLabelMap.MissingBacth + " " + uiLabelMap.For.toLowerCase() + " " + uiLabelMap.Product.toLowerCase() + ": " + data.productCode, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                "callback": function() {
			                    $("#jqxgridRequirementItem").jqxGrid('begincelledit', rowindex, "lotId");
			                }
			                }]
			            );
			            return false;
			        }
	        		selectedItems.push(allRows[id]);
	        	}
	        }
	        listRequirementItemSelected = [];
			var distincts = [];
			for (var h = 0; h < selectedItems.length; h ++){
				var data1 = allRows[h];
				var check = true;
				for (var k = 0; k < distincts.length; k ++){
					var data2 = distincts[k];
					var exp1 = null;
					if (data1.actualExpireDate){
						var x1 = new Date(data1.actualExpireDate);
						exp1 = x1.getTime();
					}
					var exp2 = null;
					if (data2.actualExpireDate){
						var x2 = new Date(data2.actualExpireDate);
						exp2 = x2.getTime();
					}
					var mnf1 = null;
					if (data1.datetimeManufactured){
						var y1 = new Date(data1.datetimeManufactured);
						mnf1 = y1.getTime();
					}
					var mnf2 = null;
					if (data2.datetimeManufactured){
						var y2 = new Date(data2.datetimeManufactured);
						mnf2 = y2.getTime();
					}
					if (data1.productId == data2.productId && (exp1 == exp2) && (mnf1 == mnf2) && (data1.lotId == data2.lotId)){ 
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
				var listDuplicated = [];
				for (var j = 0; j < selectedItems.length; j ++){
					var data2 = selectedItems[j];
					var exp1 = null;
					if (data1.actualExpireDate){
						var x1 = new Date(data1.actualExpireDate);
						exp1 = x1.getTime();
					}
					var exp2 = null;
					if (data2.actualExpireDate){
						var x2 = new Date(data2.actualExpireDate);
						exp2 = x2.getTime();
					}
					var mnf1 = null;
					if (data1.datetimeManufactured){
						var y1 = new Date(data1.datetimeManufactured);
						mnf1 = y1.getTime();
					}
					var mnf2 = null;
					if (data2.datetimeManufactured){
						var y2 = new Date(data2.datetimeManufactured);
						mnf2 = y2.getTime();
					}
					
					if (data1.productId == data2.productId && (exp1 == exp2) && (mnf1 == mnf2) && (data1.lotId == data2.lotId)){ 
						listDuplicated.push(data2);
					}
				}
				if (listDuplicated.length == 1){
					listRequirementItemSelected.push(listDuplicated[0]);
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
						total = total + listDuplicated[k].receiveQuantity;
					}
					map["receiveQuantity"] = total;
					listRequirementItemSelected.push(map);
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
	            	var now = new Date();
	            	var datetimeReceived = now.getTime();
	            	for (var i = 0; i < listRequirementItemSelected.length; i ++){
	            		listRequirementItemSelected[i]['productName'] = '';
	            		listRequirementItemSelected[i]['datetimeReceived'] = datetimeReceived;
	            		if (listRequirementItemSelected[i]['datetimeManufactured']){
	            			listRequirementItemSelected[i]['datetimeManufactured'] = new Date(listRequirementItemSelected[i]['datetimeManufactured']).getTime();
	            		} else {
	            			delete listRequirementItemSelected[i]['datetimeManufactured']; 
	            		}
	            		if (listRequirementItemSelected[i]['actualExpireDate']){
	            			listRequirementItemSelected[i]['expireDate'] = new Date(listRequirementItemSelected[i]['actualExpireDate']).getTime();
	            		} else {
	            			delete listRequirementItemSelected[i]['expireDate']; 
	            		}
	            		var requireAmount = listRequirementItemSelected[i].requireAmount;
	            		if (requireAmount && requireAmount == 'Y') {
	            			if (listRequirementItemSelected[i]['receiveQuantity'] > 0) {
	            				listRequirementItemSelected[i]['actualExecutedWeight'] = listRequirementItemSelected[i]['receiveQuantity'];
		            			listRequirementItemSelected[i]['actualExecutedQuantity'] = 1;
	            			} else {
	            				listRequirementItemSelected[i]['actualExecutedWeight'] = listRequirementItemSelected[i]['receiveQuantity'];
		            			listRequirementItemSelected[i]['actualExecutedQuantity'] = 0;
	            			}
	            		} else {
	            			listRequirementItemSelected[i]['actualExecutedQuantity'] = listRequirementItemSelected[i]['receiveQuantity'];
	            		}
	            		
	            		if (listRequirementItemSelected[i]['lotId']){
	            			listRequirementItemSelected[i]['lotId'] = listRequirementItemSelected[i]['lotId'].toUpperCase();
	            		} else {
	            			delete listRequirementItemSelected[i]['lotId']; 
	            		}
	            	}
	            	listRequirementItemSelected = JSON.stringify(listRequirementItemSelected);
	            	var url = "receiveProductFromRequirement";
	            	Loading.show('loadingMacro');
	            	setTimeout(function(){
		            	$.ajax({	
			   				 type: "POST",
			   				 url: url,
			   				 data: {
			   					 requirementId: requirementId,
			   					 facilityId: $("#facilityId").val(),
			   					 contactMechId: $("#contactMechId").val(),
			   					 listRequirementItems: listRequirementItemSelected,
			   				 },
			   				 dataType: "json",
			   				 async: false,
			   				 success: function(data){
			   					window.location.href = "viewRequirementDetail?requirementId="+requirementId;
			   				 },
			   				 error: function(response){
			   					window.location.href = "viewRequirementDetail?requirementId="+requirementId;
			   				 }
		   		 		}).done(function(data) {
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
	var getDetailRequirement = function(requirementId){
		var budgetEstimated = requirement.estimatedBudget;
		if (requirement.facilityId != null && requirement.facilityId != undefined && requirement.facilityId != ''){
			for (var i=0; i< facilityData.length; i ++){
				if (facilityData[i].facilityId == requirement.facilityId){
					if ($("#facilityId").length > 0){
						$("#facilityId").hide();
						$("#facilityToName").show();
						$("#facilityId").val(requirement.facilityId);
						$("#facilityToName").text(facilityData[i].facilityName);
					}
				}
			}
		} else {
			if ($("#facilityId").length > 0){
				$("#facilityToName").hide();
				$("#facilityId").show();
				$('#facilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle,  selectedIndex: 0, width: 200, source: facilityData, theme: theme, displayMember: 'facilityName', valueMember: 'facilityId',});
			}
		}
		$("#requiredByDate").text(requirement.requiredByDate);
		$("#requirementStartDate").text(requirement.requirementStartDate);
		$("#estimatedBudget").text(budgetEstimated.toLocaleString(localeStr) + " (" + requirement.currencyUomId + ")");
		$("#requirementStartDate").text(DatetimeUtilObj.formatFullDate(new Date(requirement.requirementStartDate)));
		$("#requiredByDate").text(DatetimeUtilObj.formatFullDate(new Date(requirement.requiredByDate)));
	};
	
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
    
    var loadRequirementItem = function loadRequirementItem(valueDataSoure){
		var sourceTmp = [];
		var sourceProduct = {
	        datafields:[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'requirementId', type: 'string'},
				{ name: 'reqItemSeqId', type: 'string'},
				{ name: 'productName', type: 'string' },
				{ name: 'internalName', type: 'string' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'statusId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'currencyUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'weight', type: 'number' },
				{ name: 'receiveQuantity', type: 'number' },
				{ name: 'actualExecutedQuantity', type: 'number' },
				{ name: 'actualExecutedWeight', type: 'number' },
				{ name: 'unitCost', type: 'number' },
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
				{ name: 'fromExpiredDate', type: 'date', other: 'Timestamp'},
				{ name: 'toExpiredDate', type: 'date', other: 'Timestamp'},
				{ name: 'expRequired', type: 'String'},
				{ name: 'mnfRequired', type: 'String'},
				{ name: 'lotRequired', type: 'String'},
				{ name: 'requireAmount', type: 'String'},
				],
				localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
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
		        columns: [
					{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: uiLabelMap.ProductId, dataField: 'productCode', width: 120, editable: true, pinned: true, columntype: 'dropdownlist',
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
					     		    	ReceiveReqObj.updateRowData(item.value);
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
					{ text: uiLabelMap.ProductName, datafield: 'productName', align: 'left', minwidth: 200, editable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							if (!data.productId){
								return '<span style=\"text-align: right\">...</span>';
							}
						}	
					},
					{ text: uiLabelMap.ManufactureDate, dataField: 'datetimeManufactured', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false, filterable: false, filterable: false,
						cellsrenderer: function (row, column, value){
							if (value){
								return '<span style=\"text-align: right;\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
							} else {
								var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
								if (data.productId){
									return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
						initeditor: function (row, column, editor) {
							editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy', showFooter: true});
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							if (data.datetimeManufactured){
								editor.jqxDateTimeInput('setDate', new Date(data.datetimeManufactured));
							} 
					 	},
					 	validation: function (cell, value) {
					 		var now = new Date();
					 		if (value) {
						        if (value > now) {
						            return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeNow};
						        }
						        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
						        if (data.expireDate){
						        	var exp = new Date(data.expireDate);
						        	if (exp < new Date(value)){
							        	return { result: false, message: uiLabelMap.ManufactureDateMustBeBeforeExpireDate};
							        }
						        }
					 		} else {
					        	var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
					        	if (data.mnfRequired == "Y") {
					        		return { result: false, message: uiLabelMap.DmsFieldRequired};
					        	}
					        }
					        return true;
						 },
					},
					{ text: uiLabelMap.ExpireDate, dataField: 'actualExpireDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false, filterable: false, filterable: false,
						cellsrenderer: function (row, column, value){
							if (value){
								return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
							} else {
								var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
								if (data.productId){
									return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
						initeditor: function (row, column, editor) {
							editor.jqxDateTimeInput({ height: '25px', formatString: 'dd/MM/yyyy', showFooter: true});
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							if (data.expireDate){
								editor.jqxDateTimeInput('setDate', new Date(data.expireDate));
							} 
					 	},
					 	validation: function (cell, value) {
					 		if (value) {
					 			var now = new Date();
						        if (value && value < now) {
						            return { result: false, message: uiLabelMap.ExpireDateMustBeAfterNow};
						        }
						        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
						        if (data.datetimeManufactured){
						        	var mft = new Date(data.datetimeManufactured);
							        if (mft > new Date(value)){
							        	return { result: false, message: uiLabelMap.ExpireDateMustBeBeforeManufactureDate};
							        }
						        }
					        } else {
					        	var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
					        	if (data.expRequired == "Y") {
					        		return { result: false, message: uiLabelMap.DmsFieldRequired};
					        	}
					        }
					        return true;
					 	},
					},
					{ text: uiLabelMap.ReceivedQuantity, dataField: 'receiveQuantity', columntype: 'numberinput', width: 120, editable: true, filterable: false, filterable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
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
					        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
					    },
					    validation: function (cell, value) {
					    	var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
					        if (value < 0){
					        	return { result: false, message: uiLabelMap.QuantityMustBeGreateThanZero};
					        }
					        if ("RECEIVE_DISAGGR" != reasonEnumId) {
					        	var totalQuantityEntered = 0;
						        var productCode = data.productCode;
						        var requireAmount = data.requireAmount;
						        var rows = $('#jqxgridRequirementItem').jqxGrid('getrows');
					        	for (var i = 0; i < rows.length; i ++){
					        		if (data.reqItemSeqId == rows[i].reqItemSeqId && rows[i].uid != data.uid){
					        			totalQuantityEntered = totalQuantityEntered + rows[i].receiveQuantity;
					        		}
						        }
					        	var totalQuantityNeeded = 0;
						        for (var i = 0; i < listRequirementItemData.length; i ++){
						        	if (data.reqItemSeqId == listRequirementItemData[i].reqItemSeqId){
						        		if (requireAmount && requireAmount == 'Y') {
						        			if (listRequirementItemData[i].actualExecutedWeight){
							        			totalQuantityNeeded = totalQuantityNeeded + listRequirementItemData[i].weight - listRequirementItemData[i].actualExecutedWeight;
							        		} else {
							        			totalQuantityNeeded = totalQuantityNeeded + listRequirementItemData[i].weight;
							        		}
						        		} else {
						        			if (listRequirementItemData[i].actualExecutedQuantity){
							        			totalQuantityNeeded = totalQuantityNeeded + listRequirementItemData[i].quantity - listRequirementItemData[i].actualExecutedQuantity;
							        		} else {
							        			totalQuantityNeeded = totalQuantityNeeded + listRequirementItemData[i].quantity;
							        		}
						        		}
						        	}
						        }
						        totalQuantityEntered = totalQuantityEntered + value;
						        if (totalQuantityEntered > totalQuantityNeeded){
						        	return { result: false, message: uiLabelMap.CannotGreaterRequiredNumber +': ' + totalQuantityEntered + ' > ' + totalQuantityNeeded};
						        } else if (totalQuantityEntered == 0 && totalQuantityNeeded > 0){
						        	return { result: false, message: uiLabelMap.TotalEnteredQuantityMustBeGreatedZero};
						        }
					        }
					        return true;
					    },
					},
					{ text: uiLabelMap.Batch, dataField: 'lotId', width: 100, editable: true, filterable: false, filterable: false,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							 if(data.statusId == 'REQ_APPROVED'){
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
	                    		 var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', cell.row);
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
					{ text: uiLabelMap.Unit, datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', editable: false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								value = data.weightUomId;
							}
							if (value){
								return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
							} else {
								if (data.productId){
									return '<span style=\"text-align: right;\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
					},
					{ text: uiLabelMap.UnitPrice, dataField: 'unitCost', columntype: 'numberinput', width: 120, editable: false, filterable: false, filterable: false,
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span style=\"text-align: right;\">' + formatcurrency(value) + '<span>';
							} else {
								var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
								if (data.productId){
									return '<span style=\"text-align: right; height: 100%;\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
					        if (data.returnPrice){
					        	editor.jqxNumberInput('val', data.returnPrice);
					        }
					    },
					},
					{ text: uiLabelMap.RequiredNumber, datafield: 'quantity', cellsalign: 'right', align: 'left', width: 120, editable: false, filterable: true,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							if (requireAmount && requireAmount == 'Y') {
								value = data.weight;
							}
							if (value){
								return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
							} else {
								if (!data.productId){
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
					},
					{ text: uiLabelMap.ReceivedNumber, dataField: 'actualExecutedQuantity', columntype: 'numberinput', width: 120, editable: false, filterable: false, filterable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							if (value){
								return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							} else {
								if (data.productId){
									value = 0;
									return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						},
					},
					{ text: uiLabelMap.EXPRequired, dataField: 'expireDate', align: 'left', width: 200, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							if (value != null && value != undefined && value != ''){
								var str = DatetimeUtilObj.getFormattedDate(new Date(value));
								if (data.fromExpiredDate) {
									str = DatetimeUtilObj.getFormattedDate(new Date(data.fromExpiredDate));
								} 
								if (data.toExpiredDate) {
									str = str + ' - ' + DatetimeUtilObj.getFormattedDate(new Date(data.toExpiredDate));
								}
								return '<span style=\"text-align: right;\">'+str+'</span>';
							} else {
								if (data.productId){
									return '<span style=\"text-align: right;\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\">...</span>';
								}
							}
						}
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
					 row["productId"] = reqItem.productId;
					 row["productCode"] = reqItem.productCode;
					 row["requirementId"] = reqItem.requirementId;
					 row["reqItemSeqId"] = reqItem.reqItemSeqId;
					 row["productName"] = reqItem.productName;
					 row["expireDate"] = reqItem.expireDate;
					 row["statusId"] = reqItem.statusId;
					 row["quantityUomId"] = reqItem.quantityUomId;
					 row["weightUomId"] = reqItem.weightUomId;
					 row["uomId"] = reqItem.uomId;
					 row["quantity"] = reqItem.quantity;
					 row["weight"] = reqItem.weight;
					 row["actualExecutedQuantity"] = reqItem.actualExecutedQuantity;
					 row["actualExecutedWeight"] = reqItem.actualExecutedWeight;
					 row["unitCost"] = reqItem.unitCost;
					 row["datetimeManufactured"] = reqItem.datetimeManufactured;
					 row["actualExpireDate"] = reqItem.actualExpireDate;
					 row["fromExpiredDate"] = reqItem.fromExpiredDate;
					 row["requireAmount"] = reqItem.requireAmount;
					 row["toExpiredDate"] = reqItem.toExpiredDate;
					 row["receiveQuantity"] = 0;
					 row["expRequired"] = reqItem.expRequired;
					 row["mnfRequired"] = reqItem.mnfRequired;
					 row["lotRequired"] = reqItem.lotRequired;
					 break; 
				 }
			 }
		 } else {
			 row["productId"] = "";
			 row["productCode"] = "";
			 row["requirementId"] = "";
			 row["reqItemSeqId"] = "";
			 row["productName"] = "";
			 row["expireDate"] = "";
			 row["statusId"] = "REQ_APPROVED";
			 row["quantityUomId"] = "";
			 row["quantity"] = "";
			 row["weightUomId"] = "";
			 row["weight"] = "";
			 row["uomId"] = "";
			 row["requireAmount"] = "";
			 row["actualExecutedQuantity"] = "";
			 row["actualExecutedWeight"] = "";
			 row["unitCost"] = "";
			 row["datetimeManufactured"] = "";
			 row["actualExpireDate"] = "";
			 row["fromExpiredDate"] = "";
			 row["toExpiredDate"] = "";
			 row["receiveQuantity"] = "";
			 row["expRequired"] = "";
			row["mnfRequired"] = "";
			row["lotRequired"] = "";
		 }
		 return row;
	}
	
	var updateRowData = function updateRowData(productCode){
		var datarow = generaterow(productCode);
		var id = $("#jqxgridRequirementItem").jqxGrid('getrowid', 0);
       $("#jqxgridRequirementItem").jqxGrid('updaterow', id, datarow);
	}
	
	function checkGridRequirementItemRequiredData(data){
		var allRows = $('#jqxgridRequirementItem').jqxGrid('getrows');
	    rowindex = data.uid;
 	    if (data.productId){
 	    	if(data.statusId == 'REQ_APPROVED'){
 		        if (data.receiveQuantity > 0){
 			        
 		        }
 		    }
 	    }
	    return false;
	}
	return {
		init: init,
		getLocalization: getLocalization,
		addNewRow: addNewRow,
		updateRowData: updateRowData,
		loadRequirementItem: loadRequirementItem,
	};
}());