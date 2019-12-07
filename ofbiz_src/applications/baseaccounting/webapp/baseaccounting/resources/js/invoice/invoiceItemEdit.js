var invoiceItemEditObj = (function(){
	var _invoiceItemSeqId = "";
	var init = function(){
		initIITDropDownGrid();
		initProductDropDownGrid();
		initInput();
		initWindow();
		initContextMenu();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#updateInvItemQty").jqxNumberInput({width: '97%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$('#updateInvItemAmount').jqxNumberInput({digits: 12, max: 999999999999, width: '97%', decimalDigits: 2, spinButtons: true});
		$('#updateInvItemTotal').jqxNumberInput({digits: 12, max: 999999999999, width: '97%', decimalDigits: 2, spinButtons: true, disabled: true});
	};
	var initIITDropDownGrid = function(){
		$("#updateInvItemTypeDropDown").jqxDropDownButton({width: '97%', height: 25}); 
		var grid = $("#updateInvItemTypeGrid");
		var datafield = [{name: 'invoiceItemTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'defaultGlAccountId', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.CommonId, datafield: 'invoiceItemTypeId', width: '30%', filterable: true},
		               {text: uiLabelMap.CommonDescription, datafield: 'description', width: '50%', filterable: true},
		               {text: uiLabelMap.BACCGlAccountId, datafield: 'defaultGlAccountId', width: '20%', filterable: true},
		               ];
		var config = {
	      		width: 600, 
	      		virtualmode: true,
	      		showfilterrow: false,
	      		showtoolbar: false,
	      		selectionmode: 'singlerow',
	      		pageable: true,
	      		sortable: false,
		        filterable: true,
		        editable: false,
		        url: '',
		        source: {
		        	pagesize: 5
		        }
	      	};
	      	Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initProductDropDownGrid = function(){
		$("#updateInvItemProdDropDown").jqxDropDownButton({width: '97%', height: 25}); 
		var grid = $("#updateInvItemProdGrid");
		var datafield = [{name: 'productId', type: 'string'},
		                 {name: 'productCode', type: 'string'},
		                 {name: 'productName', type: 'string'}
		                 ];
		var columns = [{text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '30%', filterable: true},
		               {text: uiLabelMap.BACCProductName, datafield: 'productName', width: '70%', filterable: true},
		               ];
		var config = {
				width: 500, 
				virtualmode: true,
				showfilterrow: false,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: false,
				filterable: true,
				editable: false,
				url: '',
				source: {
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#invoiceItemEditWindow"), 470, 310);
	};
	var initEvent = function(){
		$("#invoiceItemEditWindow").on('close', function(event){
			Grid.clearForm($("#invoiceItemEditWindow"));
			$("#updateInvItemTypeGrid").jqxGrid('clearselection');
			$("#updateInvItemTypeGrid").jqxGrid('clearfilters');
			$("#updateInvItemTypeGrid").jqxGrid('gotopage', 0);
			updateSourceGrid($("#updateInvItemTypeGrid"), '');
			$("#updateInvItemProdGrid").jqxGrid('clearselection');
			$("#updateInvItemProdGrid").jqxGrid('clearfilters');
			$("#updateInvItemProdGrid").jqxGrid('gotopage', 0);
			updateSourceGrid($("#updateInvItemProdGrid"), '');
			_invoiceItemSeqId = "";
		});
		
		$("#invoiceItemEditWindow").on('open', function(event){
			updateSourceGrid($("#updateInvItemProdGrid"), 'jqxGeneralServicer?sname=JqxGetProducts');
			updateSourceGrid($("#updateInvItemTypeGrid"), 'jqxGeneralServicer?sname=JQGetInvoiceItemTypeList&invoiceTypeId=' + globalVar.invoiceTypeId);
		});
		$("#updateInvItemTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#updateInvItemTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.description + '</div>';
			$("#updateInvItemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#updateInvItemTypeDropDown").attr("data-value", rowData.invoiceItemTypeId);
			$("#updateInvItemTypeDropDown").jqxDropDownButton('close');
		});
		$("#updateInvItemProdGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#updateInvItemProdGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.productName + ' [' + rowData.productCode + ']' + '</div>';
			$("#updateInvItemProdDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#updateInvItemProdDropDown").attr("data-value", rowData.productId);
			$("#updateInvItemProdDropDown").jqxDropDownButton('close');
		});
		$("#updateInvItemQty").on('valueChanged', function(event){
			var quantity = event.args.value;
			var amount = $("#updateInvItemAmount").val();
			$('#updateInvItemTotal').val(amount * quantity);
		});
		$("#updateInvItemAmount").on('valueChanged', function(event){
			var amount = event.args.value;
			var quantity = $("#updateInvItemQty").val();
			$('#updateInvItemTotal').val(amount * quantity);
		});
		$("#cancelUpdateInvoiceItem").click(function(e){
			$("#invoiceItemEditWindow").jqxWindow('close');
		});
		$("#saveUpdateInvoiceItem").click(function(e){
			var valid = $("#invoiceItemEditWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'updateInvoiceItem',
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
						  $("#jqxgridInvItem").jqxGrid('updatebounddata');
						  $("#invoiceItemEditWindow").jqxWindow('close');
						  $("#viewInvoiceTotal").html(formatcurrency(response.totalAmount, globalVar.currencyUomId));
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
	};
	var updateSourceGrid = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 160);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgridInvItem").jqxGrid('getselectedrowindex');
			var data = $("#jqxgridInvItem").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				_invoiceItemSeqId = data.invoiceItemSeqId;
				$("#updateInvItemQty").val(data.quantity);
				$("#updateInvItemAmount").val(data.amount);
				$("#updateInvItemTotal").val(data.total);
				var dropDownContent = '<div class="innerDropdownContent">' + data.invoiceItemTypeDesc + '</div>';
				$("#updateInvItemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#updateInvItemTypeDropDown").attr("data-value", data.invoiceItemTypeId);
				if(data.productId){
					dropDownContent = '<div class="innerDropdownContent">' + data.productName + ' [' + data.productCode + ']' + '</div>';
					$("#updateInvItemProdDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#updateInvItemProdDropDown").attr("data-value", data.productId);
				}
				accutils.openJqxWindow($("#invoiceItemEditWindow"));
			}
		});
		$("#contextMenu").on('shown', function(){
			var rowindex = $("#jqxgridInvItem").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridInvItem").jqxGrid('getrowdata', rowindex);
			if(globalVar.invoiceTypeId == 'PURCHASE_INVOICE' && globalVar.isInvoiceHaveBilling && !dataRecord.taxAuthorityRateSeqId){
				$(this).jqxMenu('disable', "editInvoiceItem", true);
			}else{
				$(this).jqxMenu('disable', "editInvoiceItem", false);
			}
		});
	};
	var initValidator = function(){
		$("#invoiceItemEditWindow").jqxValidator({
			rules: [
				{ input: '#updateInvItemTypeDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
	var getData = function(){
		var data = {};
		data.invoiceItemSeqId = _invoiceItemSeqId;
		data.invoiceId = globalVar.invoiceId;
		data.invoiceItemTypeId = $("#updateInvItemTypeDropDown").attr('data-value');
		if($("#updateInvItemProdDropDown").val()){
			data.productId = $("#updateInvItemProdDropDown").attr('data-value');
		}
		data.quantity = $("#updateInvItemQty").val();
		data.amount = $("#updateInvItemAmount").val();
		return data;
	}
	return{
		init: init
	}
}());
$(document).on('ready', function(){
	invoiceItemEditObj.init();
});