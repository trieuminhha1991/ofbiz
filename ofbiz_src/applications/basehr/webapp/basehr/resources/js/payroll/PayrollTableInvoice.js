var payrollTableInvoiceObj = (function(){
	var _partyIdUnselectedList = [];
	var init = function(){
		//initGrid();
		initJqxWindow();
		initEvent();
	};
	
	var initGrid = function(){
		var datafield = [{name: 'isSelect', type: 'bool'},
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'firstName', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'actualReceipt', type: 'number'},
		                 {name: 'partyGroupId', type: 'string'},
		                 {name: 'payrollTableId', type: 'string'},
		                 {name: 'groupName', type: 'string'}
		                 ];
		var columns = [{text: '', datafield: 'isSelect', columntype: 'checkbox', width: '2%', filterable: false, editable: true},
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '19%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'firstName', width: '25%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = $('#payrollInvoiceGrid').jqxGrid('getrowdata', row);
								if(rowData){
									return '<span>' + rowData.fullName + '</span>';
								}
							}
		               },
		               {text: uiLabelMap.RealSalaryPaid, datafield: 'actualReceipt', width: '19%', columntype: 'numberinput', filtertype: 'number', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
								}
							},
		               },
		               {text: uiLabelMap.InvoiceIsCreatedForOrg, datafield: 'partyGroupId', width: '35%', columntype: 'dropdownlist', filterable: false, 
		            	   editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = $('#payrollInvoiceGrid').jqxGrid('getrowdata', row);
								if(value){
									return '<span>' + rowData.groupName + '</span>';
								}
							},
		               }
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "payrollInvoiceGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeHaveNotCreateInvoice + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var grid = $('#payrollInvoiceGrid');
		globalVar.formData = {};
		var config = {
				url: '',
				showtoolbar : true,
				width : '100%',
				autoheight: true,
				virtualmode: true,
				editable: true,
				rendertoolbar: rendertoolbar,
				localization: getLocalization(),
				sortable: true,
				filterable: true,
				pageable: true,
				isSaveFormData: true,
				formData: globalVar.formData,
				source:{
					pagesize: 10,
				},
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		Grid.createContextMenu(grid, $("#invoiceContextMenu"), false);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#payrollTableInvoiceWindow"), 800, 490, initGrid);
	};
	var initEvent = function(){
		$("#payrollTableInvoiceWindow").on('close', function(event){
			refreshBeforeReloadGrid($('#payrollInvoiceGrid'));
			$('#payrollInvoiceGrid').jqxGrid('clearfilters');
			_partyIdUnselectedList = [];
		});
		$("#payrollTableInvoiceWindow").on('open', function(event){
			var source = $('#payrollInvoiceGrid').jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JQGetPayrollTableRecordPartyNotInvoice&payrollTableId=' + globalVar.payrollTableId;
			$('#payrollInvoiceGrid').jqxGrid('source', source);
		});
		$("#payrollInvoiceGrid").on('bindingcomplete', function(event){
			var paginginformation = $(this).jqxGrid('getpaginginformation');
			var pagenum = paginginformation.pagenum;
			var pagesize = paginginformation.pagesize;
			var start = pagenum * pagesize;
			var end = start + pagesize;
			for(var rowIndex = start; rowIndex < end; rowIndex++){
				var rowData = $(this).jqxGrid('getrowdata', rowIndex);
				if(rowData){
					var partyId = rowData.partyId;
					if(_partyIdUnselectedList.indexOf(partyId) < 0){
						$(this).jqxGrid('setcellvalue', rowIndex, "isSelect", true);
					}
				}
			}
		});
		$("#payrollInvoiceGrid").on('cellvaluechanged', function (event){
			var datafield = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var isSelected = event.args.newvalue;
			var rowData = $("#payrollInvoiceGrid").jqxGrid('getrowdata', rowBoundIndex); 
			var partyId = rowData.partyId;
			var indexOfParty = _partyIdUnselectedList.indexOf(partyId); 
			if(isSelected){
				if(indexOfParty > -1){
					_partyIdUnselectedList.splice(indexOfParty, 1);
				}
			}else{
				if(indexOfParty < 0){
					_partyIdUnselectedList.push(partyId);
				}
			}
		});
		$("#payrollInvoiceGrid").on('filter', function(event){
			_partyIdUnselectedList = [];
			$('#payrollInvoiceGrid').jqxGrid('clearselection');
		});
		$("#cancelCreateInvoice").click(function(event){
			$("#payrollTableInvoiceWindow").jqxWindow('close');
		});
		$("#saveCreateInvoice").click(function(event){
			bootbox.dialog(uiLabelMap.CreateInvoiceConfirm,
				[
					{
					    "label" : uiLabelMap.CommonSubmit,
					    "class" : "icon-ok btn btn-small btn-primary open-sans",
					    "callback": function() {
					    	createPayrollInvoice();	
					    }
					},
					{
						  "label" : uiLabelMap.CommonCancel,
			    		   "class" : "btn-danger icon-remove btn-small open-sans",
					}
				]		
			);
		});
		
	};
	var createPayrollInvoice = function(){
		$("#payrollInvoiceGrid").jqxGrid('showloadelement');
		$("#cancelCreateInvoice").attr("disabled", 'disabled');
		$("#saveCreateInvoice").attr("disabled", 'disabled');
		var data = {};
		if(globalVar.formData && globalVar.formData.data){
			data = globalVar.formData.data;
		}
		data.payrollTableId = globalVar.payrollTableId;
		if(_partyIdUnselectedList.length > 0){
			data.partyIdUnselected = JSON.stringify(_partyIdUnselectedList);
		}
		$.ajax({
			url: 'createPayrollTablePartyInvoice',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					if(response.statusIdDesc){
						$("#statusIdDesc").text(response.statusIdDesc);
					}
					$("#payrollTableInvoiceWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#payrollInvoiceGrid").jqxGrid('hideloadelement');
				$("#cancelCreateInvoice").removeAttr("disabled");
				$("#saveCreateInvoice").removeAttr("disabled");
			}
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#payrollTableInvoiceWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		
	}
}());

var contextPartyGroupReceiveInv = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initJqxWindow();
		initContextMenu();
		initEvent();
		create_spinner($("#spinnerPartyGroupReceiveInv"));
		initJqxValidator();
		$("#jqxNotificationpayrollInvoiceGrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerpayrollInvoiceGrid"});
	};
	var initInput = function(){
		$("#partyIdNotCreatedInv").jqxInput({width: '84%', height: 20, placeHolder: uiLabelMap.CommonSearch + "..."});
		$("#fullNameNotCreatedInv").jqxInput({width: '96%', height: 20, disabled: true});
		$("#realSalaryNotCreatedInv").jqxNumberInput({width: '98%', height: '25px', spinButtons: true, readOnly: true, disabled: true, decimalDigits: 0, max: 999999999, digits: 9})
	};
	var initDropDown = function(){
		createJqxDropDownList([], $("#partyGroupReceiveInv"), 'partyId', 'groupName', 25, '98%');
	};
	var initContextMenu = function(){
		createJqxMenu("invoiceContextMenu", 30, 130, {popupZIndex: 22000});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#partyReceiveInvoiceWindow"), 450, 270);
	};
	var initEvent = function(){
		$("#invoiceContextMenu").on('itemclick', function (event){
			var args = event.args;
			var rowindex = $("#payrollInvoiceGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#payrollInvoiceGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "edit"){
            	openJqxWindow($("#partyReceiveInvoiceWindow"));
            	$("#partyIdNotCreatedInv").val(dataRecord.partyCode);
            	loadData(dataRecord.partyCode);
            }
		});
		$("#partyReceiveInvoiceWindow").on('close', function(event){
			Grid.clearForm($(this));
			updateSourceDropdownlist($("#partyGroupReceiveInv"), []);
			$("#jqxNotificationpayrollInvoiceGrid").jqxNotification('closeAll');
		});
		$("#searchPartyIdNotCreatedInv").click(function(event){
			var partyCode = $("#partyIdNotCreatedInv").val();
			loadData(partyCode);
		});
		$("#partyIdNotCreatedInv").on('keypress', function(event){
			if(event.which == 13 || event.keyCode == 13){
				var partyCode = $("#partyIdNotCreatedInv").val();
				loadData(partyCode);
			}
		});
		$("#cancelEditPartyReceiveInv").click(function(event){
			$("#partyReceiveInvoiceWindow").jqxWindow('close');
		});
		$("#saveEditPartyReceiveInv").click(function(event){
			var valid = $("#partyReceiveInvoiceWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			$("#loadingPartyGroupReceiveInv").show();
	    	$("#cancelEditPartyReceiveInv").attr("disabled", 'disabled');
	    	$("#saveEditPartyReceiveInv").attr("disabled", 'disabled');
	    	$.ajax({
	    		url: 'updatePayrollTableRecordParty',
	    		data: {partyCode: $("#partyIdNotCreatedInv").val(), payrollTableId: globalVar.payrollTableId, partyGroupId: $("#partyGroupReceiveInv").val()},
	    		type: 'POST',
	    		success: function(response){
	    			if(response._EVENT_MESSAGE_){
	    				Grid.renderMessage('payrollInvoiceGrid', response._EVENT_MESSAGE_, {autoClose: true,
							template : 'info', appendContainer: "#containerpayrollInvoiceGrid", opacity : 0.9});
	    				$("#partyReceiveInvoiceWindow").jqxWindow('close');
	    				$("#payrollInvoiceGrid").jqxGrid('updatebounddata');
					}else{
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}
	    		},
	    		complete: function(jqXHR, textStatus){
	    			$("#loadingPartyGroupReceiveInv").hide();
	    	    	$("#cancelEditPartyReceiveInv").removeAttr("disabled");
	    	    	$("#saveEditPartyReceiveInv").removeAttr("disabled");
				}
	    	});
		});
		
	};
	var loadData = function(partyCode){
		$("#loadingPartyGroupReceiveInv").show();
    	$("#cancelEditPartyReceiveInv").attr("disabled", 'disabled');
    	$("#saveEditPartyReceiveInv").attr("disabled", 'disabled');
    	$.ajax({
    		url: 'getListDepartmentInvoiceMapped',
    		data: {partyCode: partyCode, payrollTableId: globalVar.payrollTableId},
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == "success"){
    				$("#fullNameNotCreatedInv").val(response.fullName);
    		    	$("#realSalaryNotCreatedInv").val(response.actualReceipt);
    		    	var partyGroupData = response.partyGroupData;
    		    	updateSourceDropdownlist($("#partyGroupReceiveInv"), partyGroupData);
    		    	$("#partyGroupReceiveInv").val(response.partyGroupId);
				}else{
					updateSourceDropdownlist($("#partyGroupReceiveInv"), []);
					$("#realSalaryNotCreatedInv").val(0);
					$("#fullNameNotCreatedInv").val("");
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
    		},
    		complete: function(jqXHR, textStatus){
    			$("#loadingPartyGroupReceiveInv").hide();
    	    	$("#cancelEditPartyReceiveInv").removeAttr("disabled");
    	    	$("#saveEditPartyReceiveInv").removeAttr("disabled");
			}
    	});
	};
	var initJqxValidator = function(){
		$("#partyReceiveInvoiceWindow").jqxValidator({
			rules: [{ 
				input: '#partyGroupReceiveInv', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				rule: function (input, commit) {
                	if(!input.val()){
                		return false;
                	}
                	return true;
                }
			}
			]
		});
	};
	return{
		init: init
	}
}());

var partyCreatedInvoiceObj = (function(){
	var init = function(){
		initJqxWindow();
		initEvent();
	};
	var initGrid = function(){
		var datafield = [
		                 {name: 'partyId', type: 'string'},
		                 {name: 'partyCode', type: 'string'},
		                 {name: 'invoiceId', type: 'string'},
		                 {name: 'firstName', type: 'string'},
		                 {name: 'fullName', type: 'string'},
		                 {name: 'actualReceipt', type: 'number'},
		                 {name: 'partyGroupId', type: 'string'},
		                 {name: 'groupName', type: 'string'}
		                 ];
		var columns = [
		               {text: uiLabelMap.EmployeeId, datafield: 'partyCode', width: '17%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'firstName', width: '20%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = $('#invoiceCreatedGrid').jqxGrid('getrowdata', row);
								if(rowData){
									return '<span>' + rowData.fullName + '</span>';
								}
							}
		               },
		               {text: uiLabelMap.HRInvoiceId, datafield: 'invoiceId', width: '14%',
		            	   
		               },
		               {text: uiLabelMap.RealSalaryPaid, datafield: 'actualReceipt', width: '19%', columntype: 'numberinput', filtertype: 'number', editable: false,
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
								}
							},
		               },
		               {text: uiLabelMap.InvoiceIsCreatedForOrg, datafield: 'partyGroupId', width: '30%', columntype: 'dropdownlist', filterable: false, 
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var rowData = $('#invoiceCreatedGrid').jqxGrid('getrowdata', row);
								if(value){
									return '<span>' + rowData.groupName + '</span>';
								}
							}
		               }
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "invoiceCreatedGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.PayrollInvoiceListEmployee + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var grid = $('#invoiceCreatedGrid');
		globalVar.formData = {};
		var config = {
				url: '',
				showtoolbar : true,
				width : '100%',
				autoheight: true,
				virtualmode: true,
				editable: false,
				rendertoolbar: rendertoolbar,
				localization: getLocalization(),
				sortable: true,
				filterable: true,
				pageable: true,
				source:{
					pagesize: 10,
				},
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#partyCreatedInvoiceWindow"), 830, 450, initGrid);
	};
	var openWindow = function(){
		openJqxWindow($("#partyCreatedInvoiceWindow"));
	};
	var initEvent = function(){
		$("#partyCreatedInvoiceWindow").on('open', function(event){
			var source = $('#invoiceCreatedGrid').jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JQGetPayrollTableRecordPartyInvoice&payrollTableId=' + globalVar.payrollTableId;
			$('#invoiceCreatedGrid').jqxGrid('source', source);
		});
		$("#partyCreatedInvoiceWindow").on('close', function(event){
			refreshBeforeReloadGrid($('#invoiceCreatedGrid'));
		});
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function() {
	payrollTableInvoiceObj.init();
	partyCreatedInvoiceObj.init();
	contextPartyGroupReceiveInv.init();
});