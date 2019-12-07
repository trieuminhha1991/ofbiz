var editInvoiceItemTypeObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initItemTypeDropDown();
		initGlAccountDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#editTypeId").jqxInput({width: '90%', height: 22});
		$("#editTypeDescription").jqxInput({width: '90%', height: 22});
		accutils.createJqxDropDownList($("#invoiceTypeDropDown"), globalVar.invoiceTypeArr, {valueMember: 'invoiceTypeId', displayMember: 'description', width: '92%', height: 25});
	};
	var initItemTypeDropDown = function(){
		$("#itemTypeDropDown").jqxDropDownButton({width: '92%', height: 25});
		var datafields = [{name: 'invoiceItemTypeId', type: 'string'}, 
		                  {name: 'description', type: 'string'},
						  ];
		var columns = [
						{text: uiLabelMap.CommonId, datafield: 'invoiceItemTypeId', width: '35%'},
						{text: uiLabelMap.CommonDescription, datafield: 'description'}
					];
		
		var config = {
				url: '',
				filterable: true,
				showtoolbar : false,
				width : 550,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafields, columns, null, $("#itemTypeGrid"));
	};
	
	var initGlAccountDropDown = function(){
		$("#glAccountDropDown").jqxDropDownButton({width: '92%', height: 25});
		
		var datafields = [{name: 'glAccountId', type: 'string'}, 
		                  {name: 'accountCode', type: 'string'},
						  {name: 'accountName', type: 'string'}
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var config = {
				url: 'JqxGetListGlAccounts',
				filterable: true,
				showtoolbar : false,
				width : 500,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafields, columns, null, $("#glAccountGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editInvoiceItemTypeWindow"), 500, 310);
	};
	var initEvent = function(){
		$("#editInvoiceItemTypeWindow").on('open', function(e){
			if(_isEdit){
				$("#editInvoiceItemTypeWindow").jqxWindow('setTitle', uiLabelMap.CommonEdit);
				$("#editTypeId").jqxInput({disabled: true});
				$("#editTypeId").val(_data.invoiceItemTypeId);
				$("#editTypeDescription").val(_data.description);
				if(_data.parentTypeId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.parentTypeId + ' - ' + _data.parentTypeDesc + '</div>';
					$("#itemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#itemTypeDropDown").attr("data-value", _data.parentTypeId);
				}
				if(_data.defaultGlAccountId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.defaultGlAccountId + ' - ' + _data.accountName + '</div>';
					$("#glAccountDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#glAccountDropDown").attr("data-value", _data.defaultGlAccountId);
				}
				
				$("#invoiceTypeDropDown").val(_data.invoiceTypeId);
			}else{
				$("#editInvoiceItemTypeWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateNew);
				$("#editTypeId").jqxInput({disabled: false});
			}
			updateGridSource($("#itemTypeGrid"), "jqxGeneralServicer?sname=JQGetListInvoiceItemTypeGLA");
		});
		$("#editInvoiceItemTypeWindow").on('close', function(e){
			Grid.clearForm($("#editInvoiceItemTypeWindow"));
			updateGridSource($("#itemTypeGrid"), "");
			$("#itemTypeGrid").jqxGrid('clearselection');
			$("#itemTypeGrid").jqxGrid('gotopage', 0);
			$("#itemTypeGrid").jqxGrid('clearfilters');

			$("#glAccountGrid").jqxGrid('clearselection');
			$("#glAccountGrid").jqxGrid('gotopage', 0);
			$("#glAccountGrid").jqxGrid('clearfilters');
			
			_isEdit = false;
			_data = {};
		});
		$("#cancelEditInvoiceItemType").click(function(e){
			$("#editInvoiceItemTypeWindow").jqxWindow('close');
		});
		$("#saveEditInvoiceItemType").click(function(e){
			var valid = $("#editInvoiceItemTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_isEdit){
				editInvoiceItemType();
			}else{
				bootbox.dialog(uiLabelMap.CreateInvoiceItemConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 editInvoiceItemType();
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]	
				);
			}
		});
		$("#clearItemType").click(function(e){
			$("#itemTypeDropDown").jqxDropDownButton('setContent', "");
			$("#itemTypeGrid").jqxGrid('clearselection');
		});
		$("#clearInvoiceType").click(function(e){
			$("#invoiceTypeDropDown").jqxDropDownList('clearSelection');
		});
		$("#clearGlAccount").click(function(e){
			$("#glAccountDropDown").jqxDropDownButton('setContent', "");
			$("#glAccountGrid").jqxGrid('clearselection');
		});
		$("#itemTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#itemTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.invoiceItemTypeId + ' - ' + rowData.description + '</div>';
			$("#itemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#itemTypeDropDown").attr("data-value", rowData.invoiceItemTypeId);
			$("#itemTypeDropDown").jqxDropDownButton('close');
		});
		$("#glAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#glAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + ' - ' + rowData.accountName + '</div>';
			$("#glAccountDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#glAccountDropDown").attr("data-value", rowData.glAccountId);
			$("#glAccountDropDown").jqxDropDownButton('close');
		});
	};
	
	var initValidator = function(){
		$("#editInvoiceItemTypeWindow").jqxValidator({
			rules: [
				{input: '#editTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
				    	   return true;
						}
						return false;
					}
				},   
				{input: '#editTypeDescription', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},   
			]
		});
	};
	
	var getData = function(){
		var data = {};
		data.invoiceItemTypeId = $("#editTypeId").val();
		data.description = $("#editTypeDescription").val();
		if($("#itemTypeDropDown").val()){
			data.parentTypeId = $("#itemTypeDropDown").attr('data-value'); 
		}
		if($("#invoiceTypeDropDown").val()){
			data.invoiceTypeId = $("#invoiceTypeDropDown").val(); 
		}
		if($("#glAccountDropDown").val()){
			data.defaultGlAccountId = $("#glAccountDropDown").attr('data-value');
		}
		return data;
	}; 
	
	var editInvoiceItemType = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = 'createInvoiceItemType';
		if(_isEdit){
			url = 'updateInvoiceItemType';
			data.invoiceItemTypeId = _data.invoiceItemTypeId; 
		}
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(response) {
				  if(response._ERROR_MESSAGE_){
					  bootbox.dialog(response._ERROR_MESSAGE_,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
					  $("#editInvoiceItemTypeWindow").jqxWindow('close');
					  if(!_isEdit){
						  viewInvoiceItemTypeObj.setRowKeySelect(data.invoiceItemTypeId);
					  }
					  $("#invoiceItemTypeTree").jqxTreeGrid('updateBoundData');
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var updateGridSource = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	var openNewWindow = function(data){
		accutils.openJqxWindow($("#editInvoiceItemTypeWindow"));
	};
	var openWindow = function(data){
		if(typeof(data) != 'undefined'){
			_isEdit = true;
			_data = data;
		}
		accutils.openJqxWindow($("#editInvoiceItemTypeWindow"));
	};
	return{
		init: init,
		openNewWindow: openNewWindow,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editInvoiceItemTypeObj.init();
});