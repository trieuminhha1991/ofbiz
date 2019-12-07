var fixedAssetIncreaseNewStep1 = (function(){
	var _inputTextWidth = '96%';
	var _otherInputWidth = '98%';
	var init = function(){
		initInput();
		initDropDown();
		initSupplierDropDownGrid();
		initEmployeeDropDownGrid();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#address").jqxInput({width: _inputTextWidth, height: 22});
		$("#contactPerson").jqxInput({width: _inputTextWidth, height: 22});
		$("#description").jqxInput({width: _inputTextWidth, height: 22});
		$("#moneyReceiver").jqxInput({width: _inputTextWidth, height: 22});
		$("#accountReceiver").jqxInput({width: _inputTextWidth, height: 22});
		$("#paymentVoucherNbr").jqxInput({width: _inputTextWidth, height: 22});
		$("#dateArising").jqxDateTimeInput({width: _otherInputWidth, height: 25});
		$("#dueDate").jqxDateTimeInput({width: _otherInputWidth, height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#currencyUomId"), globalVar.uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: _otherInputWidth, height: 25});
		accutils.createJqxDropDownList($("#paymentMethodTypeEnumId"), globalVar.paymentMethodTypeEnumArr, {valueMember: 'enumId', displayMember: 'description', width: _otherInputWidth, height: 25});
		accutils.createJqxDropDownList($("#accountPayer"), globalVar.finAccountArr,
				{
				valueMember: 'finAccountId', 
				displayMember: 'finAccountCode', 
				width: _otherInputWidth, 
				height: 25,
				dropDownWidth: 400,
				renderer: function (index, label, value) {
		        	var item = globalVar.finAccountArr[index];
		        	if (item != null) {
		        		var finAccountName = item.finAccountName;
		            	if (finAccountName && finAccountName.length > 65){
		            		finAccountName = finAccountName.substring(0, 65);
		            		finAccountName = finAccountName + '...';
		            	}
		        		var tableItem = '<div class="row-fluid"><div class="span12" style="margin-left: 0; width: 380px; height: 30px">'
			            	   + '<div class="span3" style="margin-left: 0px">' + item.finAccountCode + '</div>'
			            	   + '<div class="span7" style="margin-left: 25px">' + finAccountName + '</div>'
			            	   + '</div></div>';
			            return tableItem;
		        	}
		        	return "";
		        },
				}
		);
	};
	var initSupplierDropDownGrid = function(){
		$("#supplierDropDown").jqxDropDownButton({width: _otherInputWidth, height: 25}); 
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
			{name: 'addressDetail', type: 'string'},
      	];
      	var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = $("#jqxgridSupplier").jqxGrid('getrowdata', row);
							value = data.partyId;
						}
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '75%',
					cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#supplierGrid"));
	};
	
	var initEmployeeDropDownGrid = function(){
		$("#employeeDropDown").jqxDropDownButton({width: _otherInputWidth, height: 25, dropDownHorizontalAlignment: 'right'}); 
		var grid = $("#employeeGrid");
		var datafield =  [
      		{name: 'partyId', type: 'string'},
      		{name: 'partyCode', type: 'string'},
      		{name: 'firstName', type: 'string'},
      		{name: 'fullName', type: 'string'},
      		{name: 'emplPositionType', type: 'string'},
      		{name: 'department', type: 'string'},
      	];
      	var columnlist = [
          {text: uiLabelMap.EmployeeId, datafield: 'partyCode' , editable: false, cellsalign: 'left', width: '18%', filterable: true},
      	  {text: uiLabelMap.EmployeeName, datafield: 'firstName', editable: false, cellsalign: 'left', width: '35%', filterable: true,
          	  cellsrenderer: function(row, column, value){
      				var rowData = grid.jqxGrid('getrowdata', row);
      				if(rowData){
      					return '<span>' + rowData.fullName + '</span>'; 
      				}
      			}
      	  },
      	  {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: '25%', filterable: true},
      	  {text: uiLabelMap.CommonDepartment, datafield: 'department', editable: false, cellsalign: 'left', filterable: true},
      	];
      	
      	var config = {
      		width: 580, 
      		virtualmode: true,
      		showfilterrow: false,
      		showtoolbar: false,
      		selectionmode: 'singlerow',
      		pageable: true,
      		sortable: false,
	        filterable: true,
	        editable: false,
	        url: 'JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + globalVar.orgId,
	        source: {
	        	pagesize: 10
	        }
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initEvent = function(){
		$("#supplierGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#supplierGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.groupName + ' [' + rowData.partyCode + ']</div>';
			$("#supplierDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#supplierDropDown").attr("data-value", rowData.partyId);
			$("#supplierDropDown").jqxDropDownButton('close');
		});
		$("#employeeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#employeeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.fullName + ' [' + rowData.partyCode + ']</div>';
			$("#employeeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#employeeDropDown").attr("data-value", rowData.partyId);
			$("#employeeDropDown").jqxDropDownButton('close');
		});
		
		$("#paymentMethodTypeEnumId").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				if(value == 'PMT_NOT_PAID'){
					$("#paymentVoucherNbr").jqxInput({disabled: true});
					$("#moneyReceiver").jqxInput({disabled: true});
					$("#accountReceiver").jqxInput({disabled: true});
					$("#accountPayer").jqxDropDownList({disabled: true});
				}else if(value == 'PMT_CASH_PAID'){
					$("#paymentVoucherNbr").jqxInput({disabled: false});
					$("#moneyReceiver").jqxInput({disabled: false});
					$("#accountReceiver").jqxInput({disabled: false});
					$("#accountPayer").jqxDropDownList({disabled: true});
				}else if(value == 'PMT_ACCOUNT_TRANSFER'){
					$("#paymentVoucherNbr").jqxInput({disabled: false});
					$("#moneyReceiver").jqxInput({disabled: true});
					$("#accountReceiver").jqxInput({disabled: false});
					$("#accountPayer").jqxDropDownList({disabled: false});
				}
			}
		});
	};
	
	var initValidator = function(){
		$("#fixedAssetIncreaseStep1").jqxValidator({
			rules: [
				{ input: '#dateArising', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
			]
		});
	};
	
	var validate = function(){
		return $("#fixedAssetIncreaseStep1").jqxValidator('validate');
	};
	
	var windownOpenInit = function(isEdit, data){
		if(isEdit){
			$("#dateArising").val(data.dateArising);
			$("#paymentMethodTypeEnumId").val(data.paymentMethodTypeEnumId);
			$("#dueDate").val(data.dueDate);
			$("#currencyUomId").val(data.currencyUomId);
			$("#paymentVoucherNbr").val(data.paymentVoucherNbr);
			$("#address").val(data.address);
			$("#contactPerson").val(data.contactPerson);
			$("#description").val(data.description);
			$("#paymentMethodTypeEnumId").val(data.paymentMethodTypeEnumId);
			$("#moneyReceiver").val(data.moneyReceiver);
			$("#accountPayer").val(data.accountPayer);
			$("#accountReceiver").val(data.accountReceiver);
			if(data.supplierId){
				$("#supplierDropDown").attr("data-value", data.supplierId);
				var dropDownContent = '<div class="innerDropdownContent">' + data.supplierName + ' [' + data.supplierCode + ']</div>';
				$("#supplierDropDown").jqxDropDownButton('setContent', dropDownContent);
			}
			if(data.employeeBuyerId){
				$("#employeeDropDown").attr('data-value', data.employeeBuyerId);
				var dropDownContent = '<div class="innerDropdownContent">' + data.emplName + ' [' + data.emplCode + ']</div>';
				$("#employeeDropDown").jqxDropDownButton('setContent', dropDownContent);
			}
		}else{
			if(globalVar.currencyUomId){
				$("#currencyUomId").val(globalVar.currencyUomId);
			}
			var date = new Date();
			$("#dateArising").val(date);
			$("#dueDate").val(null);
			$("#paymentMethodTypeEnumId").jqxDropDownList({selectedIndex: 0});
		}
	};
	var resetData = function(){
		Grid.clearForm($("#fixedAssetIncreaseStep1"))
		$("#employeeGrid").jqxGrid('clearselection');
		$("#supplierGrid").jqxGrid('clearselection');
	};
	var getData = function(){
		var data = {};
		var supplierId = $("#supplierDropDown").attr("data-value");
		if(supplierId && supplierId.length > 0){
			data.supplierId = supplierId;
		}
		if($("#address").val()){
			data.address = $("#address").val(); 
		}
		if($("#contactPerson").val()){
			data.contactPerson = $("#contactPerson").val();
		}
		if($("#description").val()){
			data.description = $("#description").val();
		}
		var employeeBuyerId = $("#employeeDropDown").attr('data-value');
		if(employeeBuyerId){
			data.employeeBuyerId = employeeBuyerId;
		}
		data.paymentMethodTypeEnumId = $("#paymentMethodTypeEnumId").val();
		if($("#moneyReceiver").val()){
			data.moneyReceiver = $("#moneyReceiver").val(); 
		}
		if($("#accountPayer").val()){
			data.accountPayer = $("#accountPayer").val(); 
		}
		if($("#accountReceiver").val()){
			data.accountReceiver = $("#accountReceiver").val();
		}
		var dateArising = $("#dateArising").jqxDateTimeInput('val', 'date');
		data.dateArising = dateArising.getTime();
		var dueDate = $("#dueDate").jqxDateTimeInput('val', 'date');
		if(dueDate){
			data.dueDate = dueDate.getTime();
		}
		data.paymentVoucherNbr = $("#paymentVoucherNbr").val();
		data.currencyUomId = $("#currencyUomId").val();
		data.isPosted = true;
		return data;
	};
	
	return{
		init: init,
		windownOpenInit: windownOpenInit,
		resetData: resetData,
		validate: validate,
		getData: getData
	}
}());
$(document).ready(function(){
	fixedAssetIncreaseNewStep1.init();
});