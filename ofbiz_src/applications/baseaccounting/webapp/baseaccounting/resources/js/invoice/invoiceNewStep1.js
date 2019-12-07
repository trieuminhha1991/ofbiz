var THEME = 'olbius';
var OLBCommonInfo = function(){	
	
}
OLBCommonInfo.prototype.init = function() {
	OLBCommonInfo.prototype.initForm();
	OLBCommonInfo.prototype.initValidator();
	OLBCommonInfo.prototype.initEvent();
	
	if (globalVar.businessType == 'AR') {			
		accutils.setValueDropDownButtonOnly($("#organizationId"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
		$("#organizationId").jqxDropDownButton('disabled',true);
		$("#enumPartyTypeId").val("CUSTOMER_PTY_TYPE");
		accutils.setValueDropDownButtonOnly($("#glAccountTypeId"), globalVar.glAccountTypeIdDefaultAR, globalVar.glAccountTypeNameDefaultAR + ' [' + globalVar.glAccountTypeIdDefaultAR + ']');
	} else {
		accutils.setValueDropDownButtonOnly($("#customerId"), globalVar.userLogin_lastOrg, globalVar.groupName + ' [' + globalVar.userLogin_lastOrg + ']');
		$("#customerId").jqxDropDownButton('disabled',true);
		$("#enumPartyTypeId").val("SUPPLIER_PTY_TYPE");
		accutils.setValueDropDownButtonOnly($("#glAccountTypeId"), globalVar.glAccountTypeIdDefaultAP, globalVar.glAccountTypeNameDefaultAP + ' [' + globalVar.glAccountTypeIdDefaultAP + ']');
	}
};
OLBCommonInfo.prototype.initForm = function(){
    $("#conversionFactor").jqxNumberInput({width: '96%',height : 25,digits : 12,min  : 0,max : 99999999, decimalDigits : 2});
	$("#dueDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '96%', theme: THEME});
	$("#invoiceDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px' , width: '96%', theme: THEME});	
	$("#invoiceTypeId").jqxDropDownList({autoDropDownHeight : (invoiceTypeData.length > 10 ? false : true), 
			source: invoiceTypeData, filterable:(invoiceTypeData.length > 10 ? true : false), 
			placeHolder: uiLabelMap.filterchoosestring, theme: THEME, width: '96%',
			height: '25', valueMember: 'invoiceTypeId', displayMember: 'description'});
	$("#currencyUomId").jqxDropDownList({autoDropDownHeight : (uomData.length > 10 ? false : true), 
			source: uomData, filterable: (uomData.length > 10 ? true : false), 
			placeHolder: uiLabelMap.filterchoosestring, theme: THEME, 
			width: '96%', height: '25', valueMember: 'uomId', displayMember: 'description'});
	if (typeof(globalVar.defaultCurrencyUomId) != "undefined" && globalVar.defaultCurrencyUomId.length > 0) {
		$("#currencyUomId").val(globalVar.defaultCurrencyUomId);
	} else {
		$("#currencyUomId").jqxDropDownList({selectedIndex: 0});
	}
	var urlFrom = "";
	var urlTo = "";
	var customerWidth = '96%';
	var organizationWidth = '96%';
	if (globalVar.businessType == 'AR') {
		customerWidth = '87%'
	} else {
		organizationWidth = '87%';
	}
	$("#customerId").jqxDropDownButton({
		width: customerWidth, 
		height: 25,
		theme: 'olbius',
		dropDownHorizontalAlignment: 'right'
	});
	$("#organizationId").jqxDropDownButton({
		width: organizationWidth, 
		height: 25,
		theme: 'olbius',
		dropDownHorizontalAlignment: 'right'
	});
	var datafield = [
	                 {name: 'partyId', type: 'string'}, 
	                 {name: 'partyCode', type: 'string'}, 
	                 {name: 'fullName', type: 'string'}
	                 ];
	var columns = [
					{text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.BACCFullName, datafield: 'fullName'}
				];
	
	var config = {
	   		width: '100%', 
	   		virtualmode: true,
	   		showfilterrow: true,
	   		pageable: true,
	   		sortable: true,
	        filterable: true,
	        editable: false,
	        url: '', 
	        showtoolbar: false,
        	source: {
        		pagesize: 5,
        	}
   	};
   	Grid.initGrid(config, datafield, columns, null, $("#organizationGrid"));
   	Grid.initGrid(config, datafield, columns, null, $("#customerGrid"));
	
	accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '96%', height: 25});
	(function(){
		if(Date !== undefined && typeof Date.prototype._validate.init === "function"){
			Date.prototype._validate.init("invoiceDate","dueDate");
		}
	}());
	$('#dueDate').jqxDateTimeInput('setDate', new Date().addDays(7));
	
	var glAccountTypeWidth;
	if (globalVar.businessType == 'AR') {
		glAccountTypeWidth = organizationWidth;
	} else {
		glAccountTypeWidth = customerWidth;
	}
	
	$("#glAccountTypeId").jqxDropDownButton({
		width: glAccountTypeWidth, 
		height: 25,
		theme: 'olbius',
		dropDownHorizontalAlignment: 'left'
	});
	var datafieldAcc = [
	                 {name: 'glAccountTypeId', type: 'string'}, 
	                 {name: 'description', type: 'string'}, 
	                 {name: 'glAccountId', type: 'string'},
	                 {name: 'glAccountCode', type: 'string'}
	              ];
	var columnsAcc = [
					{text: uiLabelMap.BACCGlAccountTypeId, datafield: 'glAccountTypeId', width: '25%'},
					{text: uiLabelMap.BACCDescription, datafield: 'description'},
					{text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountCode', width: '20%'}
				];
	var configAcc = {
	   		width: '100%', 
	   		virtualmode: true,
	   		showfilterrow: true,
	   		pageable: true,
	   		sortable: true,
	        filterable: true,
	        editable: false,
	        url: 'JQGetListGlAccountTypeDefault', 
	        showtoolbar: false,
        	source: {
        		pagesize: 5,
        	}
   	};
   	Grid.initGrid(configAcc, datafieldAcc, columnsAcc, null, $("#glAccountTypeGrid"));
};

OLBCommonInfo.prototype.initEvent = function(){
	$("#invoiceTypeId").on('select', function(event){
		var args = event.args;
		var currencyUomId = $("#currencyUomId").val();
	    if (args) {
	    	var item = args.item;
	    	var value = item.value;
    		if (value === 'PURCHASE_INVOICE' || value === 'SALES_INVOICE' || value === 'COMMISSION_INVOICE') {
    			$("#glAccountTypeIdDiv").removeClass('hide');
    		} else {
    			$("#glAccountTypeIdDiv").addClass('hide');
    		}
    		if (value == 'IMPORT_INVOICE') {
                $("#conversionFactorDiv").removeClass('hide');
                $("#conversionFactor").val(0);
                $("#currencyUomId").val('USD');
            }
            else {
                $("#conversionFactorDiv").addClass('hide');
                $("#currencyUomId").val('VND');
            }
	    }
	});
	$("#enumPartyTypeId").on('select', function(event){
		var args = event.args;
	    if (args) {
	    	var item = args.item;
	    	var value = item.value;
	    	var grid;
	    	if (globalVar.businessType == "AP") {
	    		grid = $("#organizationGrid");
	    		$("#organizationId").val("");
	    	} else {
	    		grid = $("#customerGrid");
	    		$("#customerId").val("");
	    	}
	    	var source = grid.jqxGrid('source');
	    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
	    	grid.jqxGrid('source', source);
	    }
	});
	$("#organizationGrid").on('rowclick', function(event){
		var args = event.args;
        var row = $("#organizationGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
        $("#organizationId").jqxDropDownButton('setContent', dropDownContent);
        $("#organizationId").jqxDropDownButton('close');
        accutils.setAttrDataValue('organizationId', row.partyId);
	});
	$("#customerGrid").on('rowclick', function(event){
		var args = event.args;
		var row = $("#customerGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
        $("#customerId").jqxDropDownButton('setContent', dropDownContent);
        $("#customerId").jqxDropDownButton('close');
        accutils.setAttrDataValue('customerId', row.partyId);
	});
	$('#invoiceDate').on('valueChanged', function (event) {  
	    var jsDate = event.args.date; 
	    $('#dueDate').jqxDateTimeInput('setDate', jsDate.addDays(7));
	}); 
	$("#glAccountTypeGrid").on('rowclick', function(event){
		var args = event.args;
		var row = $("#glAccountTypeGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div class="innerDropdownContent">' + row['description'] + ' [' + row.glAccountTypeId + '] ' + '</div>';
        $("#glAccountTypeId").jqxDropDownButton('setContent', dropDownContent);
        $("#glAccountTypeId").jqxDropDownButton('close');
        accutils.setAttrDataValue('glAccountTypeId', row.glAccountTypeId);
	});
};

OLBCommonInfo.prototype.initValidator = function(){
	$('#initInvoiceEntry').jqxValidator({
        rules: [
                {input: '#conversionFactor', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change',
                    rule: function (input) {
                    if(globalVar.businessType == 'AR') return true;
                    var currencyUomId = $("#currencyUomId").val();
                    return !(input.val() <= 0 && 'VND' !== currencyUomId);
                    }
                },
       			{ input: '#invoiceTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
       				rule: function (input, commit) {
       					return accutils.validElement(input, commit, 'validInputNotNull');
    				}
    			},
    			{ input: '#organizationId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
    				rule: function (input, commit) {
    					return accutils.validElement(input, commit, 'validInputNotNull');
    				}
    			},
    			{ input: '#customerId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
    				rule: function (input, commit) {
    					return accutils.validElement(input, commit, 'validInputNotNull');
    				}
    			},
    			{ input: '#glAccountTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
    				rule: function (input, commit) {
    					return accutils.validElement(input, commit, 'validInputNotNull');
    				}
    			}
           ],
           position: 'bottom'
    });
}

OLBCommonInfo.prototype.validateForm = function(){
	var result = false;
	$('#initInvoiceEntry').on('validationError', function (event) {
		result = false;
	});
	$('#initInvoiceEntry').on('validationSuccess', function (event) {
		result = true;
	});
	$('#initInvoiceEntry').jqxValidator('validate');
	return result;
}

OLBCommonInfo.prototype.editor = {};
OLBCommonInfo.prototype.dataItt = [];

OLBCommonInfo.prototype.getFormData = function(){
	var data = {};
	var invoiceTypeId = $('#invoiceTypeId').val();
	data['invoiceTypeIdLabel'] = $('#invoiceTypeId').jqxDropDownList('getSelectedItem').label;
	data['organizationIdLabel'] = $('#organizationId').val();
	data['customerIdLabel'] = $('#customerId').val();
	data['invoiceTypeId'] = invoiceTypeId;
	data['description'] = $('#description').val();
	data['currencyUomId'] = $('#currencyUomId').val();
	data['dueDate'] = ($('#dueDate').jqxDateTimeInput('getDate')).getTime();
	data['invoiceDate'] = ($('#invoiceDate').jqxDateTimeInput('getDate')).getTime();
	data['organizationId'] = $('#organizationId').attr('data-value');
	data['customerId'] = $('#customerId').attr('data-value');
	if (invoiceTypeId === 'PURCHASE_INVOICE' || invoiceTypeId === 'SALES_INVOICE' || invoiceTypeId === 'COMMISSION_INVOICE') {
		data['glAccountTypeId'] = $('#glAccountTypeId').attr('data-value');
	}
	if(invoiceTypeId === 'IMPORT_INVOICE') {
        data['conversionFactor'] = $("#conversionFactor").val();
    }
	return data;
};


$(document).ready(function(){
	$.jqx.theme = 'olbius';
	$('#customerId').on('close',function(){
		var interval = setInterval(function(){
			$('#customerGrid').jqxGrid('clearSelection');
			clearInterval(interval);
		}, 10);
	});
	$('#glAccountTypeId').on('close',function(){
		var interval = setInterval(function(){
			$('#glAccountTypeGrid').jqxGrid('clearSelection');
			clearInterval(interval);
		}, 10);
	});
	$('#organizationId').on('close',function(){
		var interval = setInterval(function(){
			$('#organizationGrid').jqxGrid('clearSelection');
			clearInterval(interval);
		}, 10);
	});
});