var editGlAccountTypeObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initParentTypeDropDown();
		initGlAccountDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#editGlAccountTypeId").jqxInput({width: '90%', height: 22});
		$("#editGlAccountTypeDesc").jqxInput({width: '90%', height: 22});
	};
	var initParentTypeDropDown = function(){
		$("#parentTypeDropDown").jqxDropDownButton({width: '92%', height: 25});
		var datafields = [{name: 'glAccountTypeId', type: 'string'}, 
		                  {name: 'description', type: 'string'},
						  ];
		var columns = [
						{text: uiLabelMap.CommonId, datafield: 'glAccountTypeId', width: '35%'},
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
	var initGlAccountDropDown = function(){
		$("#glAccountDropDown").jqxDropDownButton({width: '92%', height: 25});
		var datafields = [{name: 'glAccountId', type: 'string'},
		                  {name: 'accountCode', type: 'string'},
		                  {name: 'accountName', type: 'string'},
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '35%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var config = {
				url: 'JqxGetListGlAccounts',
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
		Grid.initGrid(config, datafields, columns, null, $("#glAccountGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editGlAccountTypeWindow"), 500, 270);
	};
	var initEvent = function(){
		$("#editGlAccountTypeWindow").on('open', function(e){
			if(_isEdit){
				$("#editGlAccountTypeWindow").jqxWindow('setTitle', uiLabelMap.CommonEdit);
				$("#editGlAccountTypeId").jqxInput({disabled: true});
				$("#editGlAccountTypeId").val(_data.glAccountTypeId);
				$("#editGlAccountTypeDesc").val(_data.description);
				if(_data.parentTypeId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.parentTypeId + ' - ' + _data.parentTypeDesc + '</div>';
					$("#parentTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#parentTypeDropDown").attr("data-value", _data.parentTypeId);
				}
				if(_data.glAccountId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.glAccountCode + ' - ' + _data.glAccountName + '</div>';
					$("#glAccountDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#glAccountDropDown").attr("data-value", _data.glAccountId);
				}
				
			}else{
				$("#editGlAccountTypeWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateNew);
				$("#editGlAccountTypeId").jqxInput({disabled: false});
			}
			updateGridSource($("#parentTypeGrid"), "jqxGeneralServicer?sname=JQGetListGlAccountType");
		});
		$("#editGlAccountTypeWindow").on('close', function(e){
			Grid.clearForm($("#editGlAccountTypeWindow"));
			updateGridSource($("#parentTypeGrid"), "");
			$("#parentTypeGrid").jqxGrid('clearselection');
			$("#parentTypeGrid").jqxGrid('gotopage', 0);
			$("#parentTypeGrid").jqxGrid('clearfilters');

			$("#glAccountGrid").jqxGrid('clearselection');
			$("#glAccountGrid").jqxGrid('gotopage', 0);
			$("#glAccountGrid").jqxGrid('clearfilters');
			_isEdit = false;
			_data = {};
		});
		$("#cancelEditGlAccountType").click(function(e){
			$("#editGlAccountTypeWindow").jqxWindow('close');
		});
		$("#saveEditGlAccountType").click(function(e){
			var valid = $("#editGlAccountTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_isEdit){
				editGlAccountType();
			}else{
				bootbox.dialog(uiLabelMap.CreateGlAccountTypeConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								editGlAccountType();
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
		$("#clearGlAccount").click(function(e){
			$("#glAccountDropDown").jqxDropDownButton('setContent', "");
			$("#glAccountGrid").jqxGrid('clearselection');
		});
		$("#parentTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#parentTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountTypeId + ' - ' + rowData.description + '</div>';
			$("#parentTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#parentTypeDropDown").attr("data-value", rowData.glAccountTypeId);
			$("#parentTypeDropDown").jqxDropDownButton('close');
		});
		$("#glAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#glAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.accountCode + ' - ' + rowData.accountName + '</div>';
			$("#glAccountDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#glAccountDropDown").attr("data-value", rowData.glAccountId);
			$("#glAccountDropDown").jqxDropDownButton('close');
		});
	};
	
	var initValidator = function(){
		$("#editGlAccountTypeWindow").jqxValidator({
			rules: [
				{input: '#editGlAccountTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
				    	   return true;
						}
						return false;
					}
				},   
				{input: '#editGlAccountTypeDesc', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
		data.glAccountTypeId = $("#editGlAccountTypeId").val();
		data.description = $("#editGlAccountTypeDesc").val();
		if($("#parentTypeDropDown").val()){
			data.parentTypeId = $("#parentTypeDropDown").attr('data-value'); 
		}
		if($("#glAccountDropDown").val()){
			data.glAccountId = $("#glAccountDropDown").attr('data-value'); 
		}
		return data;
	};
	
	var editGlAccountType = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = "createGlAccountType";
		if(_isEdit){
			data.glAccountTypeId = _data.glAccountTypeId;
			url = "updateGlAccountType";
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
					  $("#editGlAccountTypeWindow").jqxWindow('close');
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
		accutils.openJqxWindow($("#editGlAccountTypeWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editGlAccountTypeObj.init();
});