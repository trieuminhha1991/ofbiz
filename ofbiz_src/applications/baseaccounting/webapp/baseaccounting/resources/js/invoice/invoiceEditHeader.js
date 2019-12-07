var editInvoiceObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#updateInvoiceDate").jqxDateTimeInput({width: '96%', height: 25, formatString: "dd/MM/yyyy HH:mm:ss"});
		$("#updateInvoicePaidDate").jqxDateTimeInput({width: '96%', height: 25, showFooter: true, formatString: "dd/MM/yyyy HH:mm:ss"});
		$("#updateInvoiceDueDate").jqxDateTimeInput({width: '96%', height: 25, showFooter: true, formatString: "dd/MM/yyyy HH:mm:ss"});
		$("#updateInvoicePartyName").jqxInput({width: '94%', height: 22});
		$("#updateInvoiceTaxCode").jqxInput({width: '94%', height: 22});
		$("#updateInvoiceAddress").jqxInput({width: '90%', height: 22});
		$("#updateInvoicePhoneNbr").jqxInput({width: '90%', height: 22});
		$("#updateInvoicePartyTo").jqxInput({width: '90%', height: 22, disabled: true});
		$("#updateInvoicePartyFrom").jqxInput({width: '90%', height: 22, disabled: true});
		if($("#conversionFactorEdit").length)
            $('#conversionFactorEdit').jqxNumberInput({digits: 12, max: 999999999999, width: '92%', decimalDigits: 2, spinButtons: true});
        $("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 1, autoClose: true, template: "success" });
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#updateEnumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '92%', height: 25});
	};
	var initDropDownGrid = function(){
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
		if(globalVar.businessType == "AR"){
			$("#updateInvCustomerId").jqxDropDownButton({
				width: '92%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#updateInvCusGrid"));
		}else{
			$("#updateInvOrgId").jqxDropDownButton({
				width: '92%', 
				height: 25,
				theme: 'olbius',
				dropDownHorizontalAlignment: 'right'
			});
			Grid.initGrid(config, datafield, columns, null, $("#updateInvOrgGrid"));
		}
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editInvoiceWindow"), 800, 370);
	};
	var initEvent = function(){
		$("#editInvoiceBtn").click(function(e){
			accutils.openJqxWindow($("#editInvoiceWindow"));
		});
		$("#cancelUpdateInvoice").click(function(e){
			$("#editInvoiceWindow").jqxWindow('close');
		});
		$("#saveUpdateInvoice").click(function(e){
			var valid = $("#editInvoiceWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');	
			var data = getData();
			$.ajax({
				url: 'updateInvoice',
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
						  refreshInvoiceData();
						  $("#editInvoiceWindow").jqxWindow('close');
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		
		$("#editInvoiceWindow").on('open', function(e){
			Loading.show('loadingMacro');
			$.ajax({	
				url: 'getInvoiceDetailInfo',
				type: "POST",
				data: {invoiceId: globalVar.invoiceId},
				dataType: 'json',
				success: function(response) {
					if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
								  [
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);	
						  return;
					}
					var data = response.data;
					$("#updateInvoiceDate").val(new Date(data.invoiceDate));
					if(data.paidDate){
						$("#updateInvoicePaidDate").val(new Date(data.paidDate));
					}else{
						$("#updateInvoicePaidDate").val(null);
					}
					if(data.dueDate){
						$("#updateInvoiceDueDate").val(new Date(data.dueDate));
					}else{
						$("#updateInvoiceDueDate").val(null);
					}
					$("#updateInvoicePartyFrom").val(data.fullNameFrom);
                    $("#updateInvoicePartyTo").val(data.fullNameTo);
                    $("#conversionFactorEdit").val(data.conversionFactor);
					$("#updateInvoicePartyName").val(data.partyName);
					$("#updateInvoiceTaxCode").val(data.taxCode);
					$("#updateInvoiceAddress").val(data.address);
					$("#updateInvoicePhoneNbr").val(data.phoneNbr);
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		
		$("#editInvoiceWindow").on('close', function(e){
			Grid.clearForm($("#editInvoiceWindow"));
			$("#editInvoiceWindow").jqxValidator('hide');
			if(globalVar.businessType == "AR"){
				$("#updatePartyToContainerOld").show();
				$("#updatePartyToContainerNew").hide();
				$("#updateInvCustomerId").jqxDropDownButton("setContent", "");
				$("#updateInvCusGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
			}else{
				$("#updatePartyFromContainerOld").show();
				$("#updatePartyFromContainerNew").hide();
				$("#updateInvOrgId").jqxDropDownButton("setContent", "");
				$("#updateInvOrgGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
			}
		});
		
		$("#updateEnumPartyTypeId").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	var grid;
		    	if(globalVar.businessType == "AP"){
		    		grid = $("#updateInvOrgGrid");
		    		$("#updateInvOrgId").val("");
		    	}else{
		    		grid = $("#updateInvCusGrid");
		    		$("#updateInvCustomerId").val("");
		    	}
		    	var source = grid.jqxGrid('source');
		    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
		    	grid.jqxGrid('source', source);
		    }
		});
		
		if(globalVar.businessType == "AR"){
			$("#partyToEditBtn").click(function(e){
				$("#updatePartyToContainerOld").hide();
				$("#updatePartyToContainerNew").show();
				$("#updateEnumPartyTypeId").val("CUSTOMER_PTY_TYPE");
			});
			$("#cancelEditPartyToBtn").click(function(e){
				$("#updatePartyToContainerOld").show();
				$("#updatePartyToContainerNew").hide();
				$("#updateInvCustomerId").jqxDropDownButton("setContent", "");
				$("#updateInvCusGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
				$("#editInvoiceWindow").jqxValidator('hide');
			});
			$("#updateInvCusGrid").on('rowclick', function(event){
				var args = event.args;
				var row = $("#updateInvCusGrid").jqxGrid('getrowdata', args.rowindex);
				var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
		        $("#updateInvCustomerId").jqxDropDownButton('setContent', dropDownContent);
		        $("#updateInvCustomerId").jqxDropDownButton('close');
		        accutils.setAttrDataValue('updateInvCustomerId', row.partyId);
			});
		}else{
			$("#partyFromEditBtn").click(function(e){
				$("#updatePartyFromContainerOld").hide();
				$("#updatePartyFromContainerNew").show();
				$("#updateEnumPartyTypeId").val("SUPPLIER_PTY_TYPE");
			});
			$("#cancelEditPartyFromBtn").click(function(e){
				$("#updatePartyFromContainerOld").show();
				$("#updatePartyFromContainerNew").hide();
				$("#updateInvOrgId").jqxDropDownButton("setContent", "");
				$("#updateInvOrgGrid").jqxGrid('clearselection');
				$("#updateEnumPartyTypeId").jqxDropDownList('clearSelection');
				$("#editInvoiceWindow").jqxValidator('hide');
			});
			$("#updateInvOrgGrid").on('rowclick', function(event){
				var args = event.args;
		        var row = $("#updateInvOrgGrid").jqxGrid('getrowdata', args.rowindex);
		        var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
		        $("#updateInvOrgId").jqxDropDownButton('setContent', dropDownContent);
		        $("#updateInvOrgId").jqxDropDownButton('close');
		        accutils.setAttrDataValue('updateInvOrgId', row.partyId);
			});
		}
	};
	
	var refreshInvoiceData = function(){
		if($("#updateEnumPartyTypeId").val()){
			if(globalVar.businessType == "AR"){
				var partyToName = $("#updateInvCustomerId").val();
				$("#viewPartyToName").html(partyToName);
			}else{
				var partyFromName = $("#updateInvOrgId").val();
				$("#viewPartyFromName").html(partyFromName);
			}
		}
		$("#viewPartyName").html($("#updateInvoicePartyName").val());
		$("#viewTaxCode").html($("#updateInvoiceTaxCode").val());
		$("#viewTaxInfoAddr").html($("#updateInvoiceAddress").val());
		$("#viewPhoneNbr").html($("#updateInvoicePhoneNbr").val());
        $("#viewConversionFactor").html(formatcurrency($("#conversionFactorEdit").val(), 'VND'));
		var invoiceDate = $("#updateInvoiceDate").jqxDateTimeInput('val', 'date');
		var invoiceDateStr = getDateTimeDescription(invoiceDate);
		$("#viewInvoiceDate").html(invoiceDateStr);

		var paidDate = $("#updateInvoicePaidDate").jqxDateTimeInput('val', 'date');
		if(paidDate){
			var paidDateStr = getDateTimeDescription(paidDate);
			$("#viewPaidDate").html(paidDateStr);
		}else{
			$("#viewPaidDate").html("");
		}
		
		var dueDate = $("#updateInvoiceDueDate").jqxDateTimeInput('val', 'date');
		if(dueDate){
			var dueDateStr = getDateTimeDescription(dueDate);
			$("#viewDueDate").html(dueDateStr);
		}else{
			$("#viewDueDate").html("");
		}
	};
	
	var getData = function(){
		var data = {};
		data.invoiceId = globalVar.invoiceId;
		data.invoiceDate = $("#updateInvoiceDate").jqxDateTimeInput('val', 'date').getTime();
		var paidDate = $("#updateInvoicePaidDate").jqxDateTimeInput('val', 'date');
		var dueDate = $("#updateInvoiceDueDate").jqxDateTimeInput('val', 'date');
		if(paidDate){
			data.paidDate = paidDate.getTime();
		}
		if(dueDate){
			data.dueDate = dueDate.getTime();
		}
		var enumPartyTypeId = $("#updateEnumPartyTypeId").val();
		if(enumPartyTypeId && enumPartyTypeId.length > 0){
			if(globalVar.businessType == "AR"){
				data.partyId = $("#updateInvCustomerId").attr('data-value');
			}else{
				data.partyIdFrom = $("#updateInvOrgId").attr('data-value');
			}
		}
		if($("#updateInvoicePartyName").val()){
			data.partyName = $("#updateInvoicePartyName").val();
		}
		if($("#updateInvoiceAddress").val()){
			data.address = $("#updateInvoiceAddress").val();
		}
		if($("#updateInvoiceTaxCode").val()){
			data.taxCode = $("#updateInvoiceTaxCode").val();
		}
		if($("#updateInvoicePhoneNbr").val()){
			data.phoneNbr = $("#updateInvoicePhoneNbr").val();
		}
		if($("#conversionFactorEdit").length)
    		data.conversionFactor = ('' + $("#conversionFactorEdit").val()).replace(".", ",");
		return data;
	};
	var initValidator = function(){
		var rules = [];
		if(globalVar.businessType == "AR"){
			rules.push(
					{ input: '#updateInvCustomerId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
							if($("#updateEnumPartyTypeId").val() && !input.val()){
								return false;
							}
							return true;
						}
					}
			);
		}else{
			rules.push(
					{ input: '#updateInvOrgId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input) {
							return !($("#updateEnumPartyTypeId").val() && !input.val());
						}
					},
                {input: '#conversionFactorEdit', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'blur, keyup, change',
                    rule: function (input) {
                        return !(input.val() <= 0 && globalVar.currencyUomId !== 'VND');
                    }
                }
			);
		}
		$("#editInvoiceWindow").jqxValidator({
			rules: rules
		});
	};
	var getDateTimeDescription = function(date){
		var str = date.getDate() > 9? date.getDate() : ("0" + date.getDate());
		str += "/";
		str += date.getMonth() >= 9? (date.getMonth() + 1) : ("0" + (date.getMonth() + 1));
		str += "/";
		str += date.getFullYear() + " - ";
		str += date.getHours() > 9? date.getHours() : ("0" + date.getHours());
		str += ":";
		str += date.getMinutes() > 9? date.getMinutes() : ("0" + date.getMinutes());
		str += ":";
		str += date.getSeconds() > 9? date.getSeconds() : ("0" + date.getSeconds());
		return str;
	};
	return{
		init: init
	}
}());
$(document).on('ready', function(){
	editInvoiceObj.init();
});