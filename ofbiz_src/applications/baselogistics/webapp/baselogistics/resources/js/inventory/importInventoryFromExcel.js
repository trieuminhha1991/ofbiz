$(function(){
	InvImportExcelObj.init();
});
var InvImportExcelObj = (function(){
	var checkData = true;
	
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		initInventoryGrid();
	};
	var initInputs = function(){
		Uploader.init();
	};
	
	var initElementComplex = function(){
		
	};
	
	var initValidateForm = function(){
	};
	var initEvents = function(){
		
	};
	
	var Uploader = (function() {
		var initElements = function() {
			$("#id-input-file-1").ace_file_input({
				no_file: uiLabelMap.NoFile + " ...",
				btn_choose: uiLabelMap.CommonChooseFile,
				btn_change: uiLabelMap.CommonChange,
				droppable:false,
				onchange: null,
				thumbnail:true,
				before_change:function(file, dropped) {
					if ($("#id-input-file-1").val()) {
						if ($("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").removeClass("disabled");
						}
					} else {
						if (!$("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").addClass("disabled");
						}
					}
					return true;
				}
			});
			$("#jqxNotificationNestedSlide").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#btnUpload").click(function() {
				var name = $("#id-input-file-1").val();
				if (name === undefined || name === null || name === ''){
					jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseFile);
					return false;
				}
				var end1 = name.substring(name.length - 4, name.length);
				var end2 = name.substring(name.length - 5, name.length);
				if (end1 != '.xls' && end2 != '.xlsx'){
					jOlbUtil.alert.error(uiLabelMap.YouNeedUploadFileWithType + ' .xlsx ' + uiLabelMap.or + ' .xlsx');
					return false;
				}
				if ($("#id-input-file-1").val()) {
					Loading.show();
					setTimeout(function() {
						var form_data= new FormData();
						form_data.append("uploadedFile", $("#id-input-file-1")[0].files[0]);
						form_data.append("fileName", $("#id-input-file-1").val());
						$.ajax({
							url: "uploadInventoryExcelDocument",
							type: "POST",
							data: form_data,
							cache : false,
							contentType : false,
							processData : false,
							success: function() {}
						}).done(function(res) {
							Uploader.notify(res);
							var tmp = res.listInventoryItems;
							var tmpS = $("#jqxgridInventoryItemFromExcel").jqxGrid('source');
						    tmpS._source.localdata = tmp;
						    $("#jqxgridInventoryItemFromExcel").jqxGrid('source', tmpS);
						});
					}, 300);
				}
			});
			$("#btnImport").click(function() {
				uploadAndReceiveInventory();
			});
		};
		var notify = function(res) {
			Loading.hide();
			$("#jqxNotificationNestedSlide").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "error"});
				$("#notificationContentNestedSlide").text(errormes);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			} else {
				var message = "";
				res["_MESSAGE_"]?message=res["_MESSAGE_"]:message= uiLabelMap.uploadSuccessfully;
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "info"});
				$("#notificationContentNestedSlide").text(message);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				$("#btnCommit").removeClass("hidden");
			}
		};
		return {
			init: function() {
				initElements();
				handleEvents();
			},
			notify: notify,
		};
	})();
	
	var uploadAndReceiveInventory = function() {
		if (checkData == false){
			jOlbUtil.alert.error(uiLabelMap.FormatWrong + ' ' + uiLabelMap.or + ' ' + uiLabelMap.DataNotExisted.toLowerCase());
			return false;
		}
		
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
			[{"label": uiLabelMap.CommonCancel, 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll();}
	        }, 
	        {"label": uiLabelMap.OK,
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	Loading.show('loadingMacro');
					setTimeout(function() {
						var allRows = $("#jqxgridInventoryItemFromExcel").jqxGrid('getrows');
						for (var i = 0; i < allRows.length; i ++){
							if (allRows[i].expireDate != undefined && allRows[i].expireDate != null){
								var exp = (allRows[i].expireDate).getTime();
								allRows[i]['expireDate'] = exp;
							}
							if (allRows[i].datetimeManufactured != undefined && allRows[i].datetimeManufactured != null){
								var mnf = (allRows[i].datetimeManufactured).getTime();
								allRows[i]['datetimeManufactured'] = mnf;
							}
						}
						
						var listProducts = JSON.stringify(allRows);
						$.ajax({
							url: "receiveInventoryFromUnknownSources",
							type: "POST",
							data: {
								listProducts: listProducts,
							},
							async: false,
							success: function() {
							}
						}).done(function(res) {
							bootbox.dialog(uiLabelMap.LogReceiveProuductSuccess, 
							[{"label": uiLabelMap.OK,
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	bootbox.hideAll();
					            	window.location.replace("prepareImportInventoryFromExcel");
					            }
					        }]);
						});
						Loading.hide('loadingMacro');
					}, 500);
	            }
	        }
        ]);
	};
	
	var getDataFields = function(){
		var datafieldTmp = [{ name: 'productId', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'facilityId', type: 'string' },
						{ name: 'facilityCode', type: 'string' },
						{ name: 'lotId', type: 'string' },
						{ name: 'productName', type: 'string' },
						{ name: 'isProductId', type: 'string' },
						{ name: 'isFacilityId', type: 'string' },
						{ name: 'quantityUomId', type: 'string' },
						{ name: 'quantity', type: 'number' },
						{ name: 'unitCost', type: 'number' },
						{ name: 'datetimeManufacturedStr', type: 'string' },
						{ name: 'expireDateStr', type: 'string' },
						{ name: 'isExpireDate', type: 'string' },
						{ name: 'isDatetimeManufactured', type: 'string' },
						{ name: 'datetimeManufactured', type: 'date', other: 'timestamp' },
						{ name: 'expireDate', type: 'date', other: 'timestamp' },
						];
		return datafieldTmp;
	};
	
	var getColumns = function(grid){
		var columns = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},		
			{ text: uiLabelMap.ProductId + ' *', dataField: 'productCode', width: 150, editable: false, pinned: true,
				cellsrenderer: function(row, column, value){
					var data =  $("#jqxgridInventoryItemFromExcel").jqxGrid('getrowdata', row);
					if (data.isProductId === 'false'){
						checkData = false;
						return '<div style=\"float:left;\"><span style=\"display:inline;\">' + value + '</span><span style=\"display:inline;\" class=\"red\"> (' + uiLabelMap.NotExisted +')</span></div>';
					}
				},
			},
			{ text: uiLabelMap.ProductName, dataField: 'productName', minwidth: 200, editable:false,},
			{ text: uiLabelMap.Unit + ' *', dataField: 'quantityUomId', width: 120, editable:false, filterable:true,filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					var data =  $("#jqxgridInventoryItemFromExcel").jqxGrid('getrowdata', row);
					var descriptionUom = value;
					var check = false;
					for(var i = 0; i < quantityUomData.length; i++){
						if(value == quantityUomData[i].quantityUomId){
							descriptionUom = quantityUomData[i].description;
							check = true;
					 	}
					}
					if (check){
						return '<span>' + descriptionUom + '</span>';
					} else {
						checkData = false;
						return '<div style=\"float:left;\"><span style=\"display:inline;\">' + descriptionUom + '</span><span style=\"display:inline;\" class=\"red\"> (' + uiLabelMap.NotExisted +')</span></div>';
					}
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(quantityUomData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'quantityUomId', valueMember: 'quantityUomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
						renderer: function(index, label, value){
				        	if (quantityUomData.length > 0) {
								for(var i = 0; i < quantityUomData.length; i++){
									if(quantityUomData[i].quantityUomId == value){
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
			{ text: uiLabelMap.Quantity + ' *', dataField: 'quantity',width: 120, editable:false, filtertype: 'number',
				cellsrenderer: function(row, column, value){
					return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
				},
			},
			{ text: uiLabelMap.UnitPrice + ' *', dataField: 'unitCost',width: 120, editable:false, filtertype: 'number',
				cellsrenderer: function(row, column, value){
					return '<span style=\"text-align: right\">' + formatcurrency(value, currencyUomId) + '</span>';
				},
			},
			{ text: uiLabelMap.DatetimeManufactured, dataField: 'datetimeManufactured', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function(row, colum, value){
					var data =  $("#jqxgridInventoryItemFromExcel").jqxGrid('getrowdata', row);
					if(value === null || value === undefined || value === ''){
						if (data.isDatetimeManufactured === 'false'){
							checkData = false;
							return '<div style=\"float:left;\"><span style=\"display:inline;\">' + data.datetimeManufacturedStr + '</span><span style=\"display:inline;\" class=\"red\"> (' + uiLabelMap.FormatWrong +': dd/mm/YYY)</span></div>';
						}
					}
				}
			},
			{ text: uiLabelMap.ExpireDate, dataField: 'expireDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						var data =  $("#jqxgridInventoryItemFromExcel").jqxGrid('getrowdata', row);
						if (data.isExpireDate === 'false'){
							checkData = false;
							return '<div style=\"float:left;\"><span style=\"display:inline;\">' + data.expireDateStr + '</span><span style=\"display:inline;\" class=\"red\"> (' + uiLabelMap.FormatWrong +': dd/mm/YYY)</span></div>';
						}
					}
				}
			},
			{ text: uiLabelMap.Batch, datafield: 'lotId', align: 'left', width: 120,
				cellsrenderer: function(row, colum, value){
					if(value === null || value === undefined || value === ''){
						return '<span></span>';
					}
				}
			},
			{ text: uiLabelMap.Facility + ' *', datafield: 'facilityCode', align: 'left', width: 150,
				cellsrenderer: function(row, column, value){
					var data =  $("#jqxgridInventoryItemFromExcel").jqxGrid('getrowdata', row);
					if (data.isFacilityId === 'false'){
						checkData = false;
						return '<div style=\"float:left;\"><span style=\"display:inline;\">' + value + '</span><span style=\"display:inline;\" class=\"red\"> (' + uiLabelMap.NotExisted +')</span></div>';
					}
				},
			},
        ];
		return columns;
	};
	
	var initInventoryGrid = function(){
		var grid = $("#jqxgridInventoryItemFromExcel");
		var datafieldTmps = getDataFields();
		var columnTmps = getColumns(grid);
		var config = {
		   		width: '100%', 
		   		virtualmode: false,
		   		filterable: false,
		   		showtoolbar: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: true,
		        filterable: true,	        
		        editable: false,
		        rowsheight: 26,
		        url: '',                
		        source: {pagesize: 10}
		};
		Grid.initGrid(config, datafieldTmps, columnTmps, null, grid);
	};
	
	var getLocalization = function () {
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
	return {
		init: init,
	}
}());