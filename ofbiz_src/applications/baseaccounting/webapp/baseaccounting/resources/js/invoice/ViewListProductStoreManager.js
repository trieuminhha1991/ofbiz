var viewListStoreManagerObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 200);
		$("#contextMenu").on('itemclick', function (event){
			var args = event.args;
			var action = $(args).attr("action");
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(action == "viewListTerminal"){
				listPosTerminalObj.openWindow(rowData);
			}else if(action == "cashHanover"){
				createCashHandoverObj.openWindow(rowData);
			}
		});
	};
	return{
		init: init
	}
}());
var listPosTerminalObj = (function(){
	var init = function(){
		initGrid();
		initWindow();
		initEvent();
	};
	var initGrid = function(){
		var grid = $("#posTerminalListGrid");
		var datafield = [{name: 'posTerminalId', type: 'string'},
		                 {name: 'terminalName', type: 'string'},
		                 {name: 'isCashHandover', type: 'bool'},
		                 {name: 'cashHandoverAmount', type: 'number'},
		                 ];
		
		var columns = [{text: uiLabelMap.BPOSTerminalId, datafield: 'posTerminalId', width: '22%'},
		               {text: uiLabelMap.BPOSTerminalName, datafield: 'terminalName', width: '35%'},
		               {text: uiLabelMap.IsHandedOverMoney, datafield: 'isCashHandover', width: '21%', columntype: 'checkbox'},
		               {text: uiLabelMap.BACCAmount, datafield: 'cashHandoverAmount', columntype: 'numberinput',
		            	   cellsrenderer: function(row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
								}						
							}  
		               }
		               ];
		var config = {
				url: '',
				showtoolbar : false,
				//rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				sortable: true,
				source: {
					pagesize: 10,
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initEvent = function(){
		$("#closeListPosTerminal").click(function(){
			$("#ListPosTerminalWindow").jqxWindow('close');
		});
		$("#ListPosTerminalWindow").on('close', function(event){
			var source = $("#posTerminalListGrid").jqxGrid('source');
			source._source.url = "";
			$("#posTerminalListGrid").jqxGrid('source', source);
			$("#storeNameView").html("");
			$("#productStoreIdView").html("");
		});
	};
	
	var openWindow = function(data){
		updateGrid(data.productStoreId);
		$("#storeNameView").html(data.storeName);
		$("#productStoreIdView").html(data.productStoreId);
		accutils.openJqxWindow($("#ListPosTerminalWindow"));
	};
	var updateGrid = function(productStoreId){
		var source = $("#posTerminalListGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQGetListPosTerminalByProductStore&productStoreId=" + productStoreId;
		$("#posTerminalListGrid").jqxGrid('source', source);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#ListPosTerminalWindow"), 600, 470);
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

var createCashHandoverObj = (function(){
	var _amountHandover = 500000;
	var _productStoreId = null;
	var init = function(){
		initDropDownGrid();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initDropDownGrid = function(){
		var grid = $("#posTerNotCashHanoverGrid");
		$("#posTemrinalDropDown").jqxDropDownButton({width: '96%', height: 25,theme: 'olbius',});
		var datafield = [{name: 'posTerminalId', type: 'string'},
		                 {name: 'terminalName', type: 'string'},]
		
		var columns = [{text: uiLabelMap.BPOSTerminalId, datafield: 'posTerminalId', width: '30%'},
		                 {text: uiLabelMap.BPOSTerminalName, datafield: 'terminalName'}];
		var config = {
				url: '',
				showtoolbar : false,
				width : 500,
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				selectionmode: 'checkbox',
				sortable: true,
				source: {
					pagesize: 10,
					localdata: []
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initInput = function(){
		$("#cashHandoverTerminal").jqxNumberInput({width: '96%', max: 999999999999999999, digits: 18, decimalDigits:0, spinButtons: true, min: 0, value: _amountHandover});
		$("#toalCashHandoverTerminal").jqxNumberInput({ width: '96%', max: 999999999999999999, digits: 18, decimalDigits:0, spinButtons: true, min: 0, disabled: true});
		$("#dateCashHandover").jqxDateTimeInput({width: '96%', height: 25, formatString: 'dd/MM/yyyy HH:mm:ss'});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#cashHandoverTerminalWindow"), 450, 260);
	};
	var openWindow = function(data){
		_productStoreId = data.productStoreId;
		accutils.openJqxWindow($("#cashHandoverTerminalWindow"));
	};
	var initEvent = function(){
		$("#cashHandoverTerminalWindow").on('open', function(event){
			initOpen();
		});
		$("#cashHandoverTerminalWindow").on('close', function(event){
			updateGridLocalData([]);
			$("#posTemrinalDropDown").jqxDropDownButton('setContent', "");
			$("#cashHandoverTerminal").val(null);
			$("#toalCashHandoverTerminal").val(null);
			$("#dateCashHandover").val(null);
			$("#posTerNotCashHanoverGrid").jqxGrid('clearselection');
			$("#cashHandoverTerminalWindow").jqxValidator('hide');
		});
		$("#cancelCreateCashHandover").click(function(event){
			$("#cashHandoverTerminalWindow").jqxWindow('close');
		});
		$("#saveCreateCashHandover").click(function(event){
			var valid = $("#cashHandoverTerminalWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateCashHandoverTerminalConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createCashHandoverTerminal();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#posTerNotCashHanoverGrid").on('rowselect', function(event){
			setPOSTerminalDropDownContent();
		});
		$("#posTerNotCashHanoverGrid").on('rowunselect', function(event){
			setPOSTerminalDropDownContent();
		});
		var setPOSTerminalDropDownContent = function(){
			var selectedrows = $("#posTerNotCashHanoverGrid").jqxGrid('getselectedrowindexes');
			var dropDownContent = '<div class="innerDropdownContent">' + selectedrows.length + ' ' + uiLabelMap.BACCPOSTerminal + '</div>';
			$("#posTemrinalDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#toalCashHandoverTerminal").val(selectedrows.length * _amountHandover);
		};
	};
	var initOpen = function(){
		var date = new Date();
		$("#dateCashHandover").val(date);
		Loading.show('loadingMacro');
		$("#cashHandoverTerminal").val(_amountHandover);
		$.ajax({
			url: 'getPOSTerminalNotCashHandover',
			data: {productStoreId: _productStoreId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]);
					return;
				}
				updateGridLocalData(response.listReturn);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateGridLocalData = function(localdata){
		var source = $("#posTerNotCashHanoverGrid").jqxGrid('source');
		source._source.localdata = localdata;
		$("#posTerNotCashHanoverGrid").jqxGrid('source', source);
	};
	var initValidator = function(){
		$("#cashHandoverTerminalWindow").jqxValidator({
			rules: [
				{ input: '#cashHandoverTerminal', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close', 
						rule: function (input, commit) {
							if($(input).val() <= 0){
								return false;
							}
							return true;
					}
				},
				{ input: '#posTemrinalDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
					rule: function (input, commit) {
						var selectedrows = $("#posTerNotCashHanoverGrid").jqxGrid('getselectedrowindexes');
						if(selectedrows.length > 0){
							return true;
						}
						return false;
					}
				},
			]
		});
	};
	var getData = function(){
		var rowSelectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowSelectedIndex);
		var data = {};
		data.amount = $("#cashHandoverTerminal").val();
		data.partyId = rowData.partyId;
		data.productStoreId = rowData.productStoreId;
		data.dateReceived = $("#dateCashHandover").jqxDateTimeInput('val', 'date').getTime();
		var POSTerminalSelectedIndex = $("#posTerNotCashHanoverGrid").jqxGrid('getselectedrowindexes');
		var posTerminalSelectedId = [];
		POSTerminalSelectedIndex.forEach(function(rowindex){
			var tempRowData = $("#posTerNotCashHanoverGrid").jqxGrid('getrowdata', rowindex);
			posTerminalSelectedId.push(tempRowData.posTerminalId);
		});
		data.posTerminalIds = JSON.stringify(posTerminalSelectedId);
		return data;
	};
	var createCashHandoverTerminal = function(){
		var data = getData();
		Loading.show('loadingMacro');
		$.ajax({
			url: 'createCashHandoverTerminal',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								 "label" : uiLabelMap.CommonClose,
								 "class" : "btn-danger btn-small icon-remove open-sans",
							 }]);
							return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#cashHandoverTerminalWindow").jqxWindow('close');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	viewListStoreManagerObj.init();
	listPosTerminalObj.init();
	createCashHandoverObj.init();
});