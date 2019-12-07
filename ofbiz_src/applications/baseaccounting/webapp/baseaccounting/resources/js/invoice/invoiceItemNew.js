var invoiceItemNewObj = (function(){
	var init = function(){
		initIITDropDownGrid();
		initProductDropDownGrid();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#newInvItemQty").jqxNumberInput({width: '97%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		$('#newInvItemAmount').jqxNumberInput({digits: 12, max: 999999999999, width: '97%', decimalDigits: 2, spinButtons: true});
		$('#newInvItemTotal').jqxNumberInput({digits: 12, max: 999999999999, width: '97%', decimalDigits: 2, spinButtons: true, disabled: true});
	};
	var initIITDropDownGrid = function(){
		$("#newInvItemTypeDropDown").jqxDropDownButton({width: '97%', height: 25}); 
		var grid = $("#newInvItemTypeGrid");
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
		$("#newInvItemProdDropDown").jqxDropDownButton({width: '97%', height: 25}); 
		var grid = $("#newInvItemProdGrid");
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
		accutils.createJqxWindow($("#invoiceItemNewWindow"), 470, 310);
	};
	var initEvent = function(){
		$("#invoiceItemNewWindow").on('close', function(event){
			Grid.clearForm($("#invoiceItemNewWindow"));
			$("#newInvItemTypeGrid").jqxGrid('clearselection');
			$("#newInvItemTypeGrid").jqxGrid('clearfilters');
			$("#newInvItemTypeGrid").jqxGrid('gotopage', 0);
			updateSourceGrid($("#newInvItemTypeGrid"), '');
			$("#newInvItemProdGrid").jqxGrid('clearselection');
			$("#newInvItemProdGrid").jqxGrid('clearfilters');
			$("#newInvItemProdGrid").jqxGrid('gotopage', 0);
			updateSourceGrid($("#newInvItemProdGrid"), '');
		});
		
		$("#invoiceItemNewWindow").on('open', function(event){
			updateSourceGrid($("#newInvItemProdGrid"), 'jqxGeneralServicer?sname=JqxGetTaxProducts');
			updateSourceGrid($("#newInvItemTypeGrid"), 'jqxGeneralServicer?sname=JQGetInvoiceItemTypeList&invoiceTypeId=' + globalVar.invoiceTypeId);
		});
		$("#newInvItemTypeGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#newInvItemTypeGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.description + '</div>';
			$("#newInvItemTypeDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#newInvItemTypeDropDown").attr("data-value", rowData.invoiceItemTypeId);
			$("#newInvItemTypeDropDown").jqxDropDownButton('close');
		});
		$("#newInvItemProdGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#newInvItemProdGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.productName + ' [' + rowData.productCode + ']' + '</div>';
			$("#newInvItemProdDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#newInvItemProdDropDown").attr("data-value", rowData.productId);
			$("#newInvItemProdDropDown").jqxDropDownButton('close');
		});
		$("#newInvItemQty").on('valueChanged', function(event){
			var quantity = event.args.value;
			var amount = $("#newInvItemAmount").val();
			$('#newInvItemTotal').val(amount * quantity);
		});
		$("#newInvItemAmount").on('valueChanged', function(event){
			var amount = event.args.value;
			var quantity = $("#newInvItemQty").val();
			$('#newInvItemTotal').val(amount * quantity);
		});
		$("#cancelNewInvoiceItem").click(function(e){
			$("#invoiceItemNewWindow").jqxWindow('close');
		});
		$("#saveNewInvoiceItem").click(function(e){
			var valid = $("#invoiceItemNewWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createNewInvoiceItemType();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
			
		});
	};
	var createNewInvoiceItemType = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createInvoiceItemOlb',
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
					  $("#invoiceItemNewWindow").jqxWindow('close');
					  $("#viewInvoiceTotal").html(formatcurrency(response.totalAmount, globalVar.currencyUomId));
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	var updateSourceGrid = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	var initValidator = function(){
		$("#invoiceItemNewWindow").jqxValidator({
			rules: [
				{ input: '#newInvItemTypeDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val()){
							return true;
						}
						return false;
					}
				},   
				{ input: '#newInvItemQty', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val() > 0){
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
		data.invoiceId = globalVar.invoiceId;
		data.invoiceItemTypeId = $("#newInvItemTypeDropDown").attr('data-value');
		if($("#newInvItemProdDropDown").val()){
			data.productId = $("#newInvItemProdDropDown").attr('data-value');
		}
		data.quantity = $("#newInvItemQty").val();
		data.amount = $("#newInvItemAmount").val();
		return data;
	}
	return{
		init: init
	}
}());
$(document).on('ready', function(){
	invoiceItemNewObj.init();
});