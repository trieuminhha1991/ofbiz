var editPaymentTypeObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initParentTypeDropDown();
		initGlAccountTypeDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#editPaymentTypeId").jqxInput({width: '90%', height: 22});
		$("#editPaymentTypeDesc").jqxInput({width: '90%', height: 22});
		$("#isAppliedInvoice").jqxCheckBox({ width: 120, height: 25});
	};
	var initParentTypeDropDown = function(){
		$("#parentTypeDropDown").jqxDropDownButton({width: '92%', height: 25});
		var datafields = [{name: 'paymentTypeId', type: 'string'}, 
		                  {name: 'description', type: 'string'},
						  ];
		var columns = [
						{text: uiLabelMap.CommonId, datafield: 'paymentTypeId', width: '35%'},
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
		Grid.initGrid(config, datafields, columns, null, $("#parentTypeGrid"));
	};
	var initGlAccountTypeDropDown = function(){
		$("#glAccountTypeDropDown").jqxDropDownButton({width: '92%', height: 25});
		var datafields = [{name: 'glAccountTypeId', type: 'string'}, 
		                  {name: 'description', type: 'string'},
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountType, datafield: 'glAccountTypeId', width: '35%'},
						{text: uiLabelMap.CommonDescription, datafield: 'description'}
					];
		
		var config = {
				url: 'JQGetListGlAccountType',
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
		Grid.initGrid(config, datafields, columns, null, $("#glAccountTypeGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editPaymentTypeWindow"), 500, 300);
	};
	var initEvent = function(){
		$("#editPaymentTypeWindow").on('open', function(e){
			if(_isEdit){
				$("#editPaymentTypeWindow").jqxWindow('setTitle', uiLabelMap.CommonEdit);
				$("#editPaymentTypeId").jqxInput({disabled: true});
				$("#editPaymentTypeId").val(_data.paymentTypeId);
				$("#editPaymentTypeDesc").val(_data.description);
				if(_data.parentTypeId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.parentTypeId + ' - ' + _data.parentTypeDesc + '</div>';
					$("#parentTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#parentTypeDropDown").attr("data-value", _data.parentTypeId);
				}
				if(_data.glAccountTypeId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.glAccountTypeId + ' - ' + _data.glAccountTypeDesc + '</div>';
					$("#glAccountTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#glAccountTypeDropDown").attr("data-value", _data.glAccountTypeId);
				}
				if(_data.isAppliedInvoice){
					$("#isAppliedInvoice").jqxCheckBox({checked: true});
				}
			}else{
				$("#editPaymentTypeWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateNew);
				$("#editPaymentTypeId").jqxInput({disabled: false});
			}
			updateGridSource($("#parentTypeGrid"), "jqxGeneralServicer?sname=JQGetListPaymentType");
		});
		$("#editPaymentTypeWindow").on('close', function(e){
			Grid.clearForm($("#editPaymentTypeWindow"));
			updateGridSource($("#parentTypeGrid"), "");
			$("#parentTypeGrid").jqxGrid('clearselection');
			$("#parentTypeGrid").jqxGrid('gotopage', 0);
			$("#parentTypeGrid").jqxGrid('clearfilters');

			$("#glAccountTypeGrid").jqxGrid('clearselection');
			$("#glAccountTypeGrid").jqxGrid('gotopage', 0);
			$("#glAccountTypeGrid").jqxGrid('clearfilters');
			$("#isAppliedInvoice").jqxCheckBox({checked: false});
			_isEdit = false;
			_data = {};
		});
		$("#cancelEditPaymentType").click(function(e){
			$("#editPaymentTypeWindow").jqxWindow('close');
		});
		$("#saveEditPaymentType").click(function(e){
			var valid = $("#editPaymentTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_isEdit){
				editPaymentType();
			}else{
				bootbox.dialog(uiLabelMap.CreatePaymentTypeConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								editPaymentType();
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
		$("#clearParentType").click(function(e){
			$("#parentTypeDropDown").jqxDropDownButton('setContent', "");
			$("#parentTypeGrid").jqxGrid('clearselection');
		});
		$("#clearGlAccountType").click(function(e){
			$("#glAccountTypeDropDown").jqxDropDownButton('setContent', "");
			$("#glAccountTypeGrid").jqxGrid('clearselection');
		});
		$("#parentTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#parentTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.paymentTypeId + ' - ' + rowData.description + '</div>';
			$("#parentTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#parentTypeDropDown").attr("data-value", rowData.paymentTypeId);
			$("#parentTypeDropDown").jqxDropDownButton('close');
		});
		$("#glAccountTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#glAccountTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountTypeId + ' - ' + rowData.description + '</div>';
			$("#glAccountTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#glAccountTypeDropDown").attr("data-value", rowData.glAccountTypeId);
			$("#glAccountTypeDropDown").jqxDropDownButton('close');
		});
	};
	
	var initValidator = function(){
		$("#editPaymentTypeWindow").jqxValidator({
			rules: [
				{input: '#editPaymentTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
				    	   return true;
						}
						return false;
					}
				},   
				{input: '#editPaymentTypeDesc', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
		data.paymentTypeId = $("#editPaymentTypeId").val();
		data.description = $("#editPaymentTypeDesc").val();
		if($("#parentTypeDropDown").val()){
			data.parentTypeId = $("#parentTypeDropDown").attr('data-value'); 
		}
		if($("#glAccountTypeDropDown").val()){
			data.glAccountTypeId = $("#glAccountTypeDropDown").attr('data-value'); 
		}
		var checked = $("#isAppliedInvoice").jqxCheckBox('checked');
		if(checked){
			data.isAppliedInvoice = "Y";
		}else{
			data.isAppliedInvoice = "N";
		}
		return data;
	};
	
	var editPaymentType = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = "createPaymentType";
		if(_isEdit){
			data.paymentTypeId = _data.paymentTypeId;
			url = "updatePaymentType";
		}
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
					  $("#editPaymentTypeWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('updatebounddata');
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
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#editPaymentTypeWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editPaymentTypeObj.init();
});