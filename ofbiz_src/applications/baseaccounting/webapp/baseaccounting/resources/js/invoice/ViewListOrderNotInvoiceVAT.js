var viewOrderNotVATObj = (function(){
	var _isCheckedAll = false;
	var _unSelectedArr = [];
	var _selectedArr = [];
	
	var init = function(){
		initJqxGridEvent();
		initContextMenu();
		initEvent();
		initGrid();
		initInput();
		initWindow();
		initValidator();
	};
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			var date = new Date();
			$("#orderDateSearch").jqxDateTimeInput({width: 150, height: 25, value: date, dropDownHorizontalAlignment: 'right'});
			updateGridByDate(date);
			$("#orderDateSearch").on('change', function (event){
				var date = event.args.date;
				updateGridByDate(date);
			});
		});
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 300);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var action = $(args).attr("action");
			if(action == "createVATInvoice"){
				accutils.openJqxWindow($("#createInvoiceVATWindow"));
			}else if(action == "createOrderInvoiceNote"){
				orderInvoiceNotObj.openWindow();
			}
		});
	};
	
	var updateGridByDate = function(date){
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetListOrderExternalNotVAT&orderDate=' + date.getTime();
		$("#jqxgrid").jqxGrid('source', source);
	};
	var initEvent = function(){
		$("#jqxgrid").on('checkedAll', function(e, flag){
			_isCheckedAll = flag;
			processSelectedRow('clear');
		});
		$("#jqxgrid").on('rowunselect', function(event){
			var rowindex = event.args.rowindex;
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
			if(rowData){
				processSelectedRow('unselect', rowData.orderId);
			}
		});
		$("#jqxgrid").on('rowselect', function(event){
			var rowindex = event.args.rowindex;
			//in case of checkedAll, typeof rowindex is Array
			if(typeof(rowindex) == "number"){
				var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
				if(rowData){
					processSelectedRow('select', rowData.orderId);
				}
			}
		});
		$("#createInvoiceVATWindow").on('open', function(){
			initOpen();
		});
		$("#createInvoiceVATWindow").on('close', function(){
			resetData();
		});
		$("#cancelCreateInvoiceVAT").click(function(){
			$("#createInvoiceVATWindow").jqxWindow('close');
		});
		$("#saveCreateInvoiceVAT").click(function(){
			var valid = $("#createInvoiceVATWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateInvoiceVATForOrderConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createInvoiceVAT();
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
	
	var getData = function(){
		var data = globalVar.formData.data != "undefined"? globalVar.formData.data : {};
		data.invoiceDate = $("#invoiceDate").jqxDateTimeInput('val', 'date').getTime();
		var description = $("#description").val();
		if(description.length > 0){
			data.description = description;
		}
		data.orderDate = $("#orderDateSearch").jqxDateTimeInput('val', 'date').getTime();
		data.voucherForm = $("#voucherForm").val();
		data.voucherNumber = $("#voucherNumber").val();
		data.voucherSerial = $("#voucherSerial").val();
		data.productStoreId = $("#productStoreIdHidden").val();
		return data;
	};
	
	var createInvoiceVAT = function(){
		var data = getData(); 
		Loading.show('loadingMacro');
		$.ajax({
			url: 'createInvoiceVAT',
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
				$("#createInvoiceVATWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#jqxgrid").jqxGrid('clearselection');
				resetOrderSelected();
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var processSelectedRow = function(type, orderId){
		if(type === 'clear'){
			_selectedArr = [];
			_unSelectedArr = [];
			return;
		}
		var indexInSelectedArr =  _selectedArr.indexOf(orderId);
		var indexInUnSelectedArr = _unSelectedArr.indexOf(orderId);
		if(type === 'unselect'){
			if(indexInSelectedArr > -1){
				_selectedArr.splice(indexInSelectedArr, 1);
			}
			if(_isCheckedAll && indexInUnSelectedArr < 0){
				_unSelectedArr.push(orderId);
			}
			return;
		}
		if(type === 'select'){
			if(indexInUnSelectedArr > -1){
				_unSelectedArr.splice(indexInUnSelectedArr, 1);
			}
			if(!_isCheckedAll && indexInSelectedArr < 0){
				_selectedArr.push(orderId);
			}
			return;
		}
	};
	var initGrid = function(){
		var grid = $("#invoiceItemProductGrid");
		var datafield = [{name: 'invoiceItemTypeDesc', type: 'string'},
		                 {name: 'productId', type: 'string'},
		                 {name: 'productCode', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'currencyUomId', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'amount', type: 'number'},
		                 {name: 'subTotalAmount', type: 'number'},
		                 {name: 'taxAuthorityRateSeqId', type: 'string'},
		                 {name: 'taxAuthGeoId', type: 'string'},
		                 {name: 'taxAuthPartyId', type: 'string'},
		                 ];
		var columns = [{text: uiLabelMap.BACCInvoiceItemType, datafield: 'invoiceItemTypeDesc', width: '29%'},
		               {text: uiLabelMap.BACCProductId, datafield: 'productCode', width: '14%'},
		               {text: uiLabelMap.BACCDescription, datafield: 'description', width: '24%'},
		               {text: uiLabelMap.BSQty, datafield: 'quantity', width: '5%', columntype: 'numberinput'},
		               {text: uiLabelMap.BACCUnitPrice, datafield: 'amount', width: '13%', columntype: 'numberinput',
		            	   cellsrenderer: function(row, colum, value){
						  		if(typeof(value) == 'number'){
						  			var data = grid.jqxGrid('getrowdata', row);
						  			return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</value>';
						  		}
						  	}
		               },
		               {text: uiLabelMap.BACCTotal, datafield: 'subTotalAmount', width: '15%', columntype: 'numberinput',
		            	   cellsrenderer: function(row, colum, value){
		            		   if(typeof(value) == 'number'){
		            			   var data = grid.jqxGrid('getrowdata', row);
		            			   return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</value>';
		            		   }
		            	   },
		            	   aggregates: ['sum'],
		            	   aggregatesrenderer: function (aggregates) {
        		              var renderstring = "";
        		              $.each(aggregates, function (key, value) {
        		                  renderstring += '<div style="font-size: 14px; padding: 8px; text-align: right"><b>' + formatcurrency(value) + '</b></div>';
        		              });
        		              return renderstring;
        		          }
		               }
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "invoiceItemProductGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.BACCInvoiceItem + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
		};
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				sortable: true,
				showaggregates: true,
				showstatusbar: true,
				statusbarheight: 35,
				source: {
					pagesize: 10,
					localdata: [],
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initInput = function(){
		$("#invoiceDate").jqxDateTimeInput({width: '97%', height: 25, formatString: "dd/MM/yyyy HH:mm:ss"});
		$("#description").jqxInput({width: '95%', height: 20});
		
		var voucherF = new Array("01GTKT", "02GTTT", "06HDXK", "07KPTQ", "03XKNB","04HGDL", "01BLP", "02BLP2", "HĐ-BACHHOA", "HDBACHHOA", "TNDN", "PLHD", "THUEMB", "C1-02/NS");
		$("#voucherForm").jqxInput({width: '95%', height: 20, source: voucherF, theme:'energyblue'});
		var voucherS = new Array("AB/17P", "SC/17P", "AA/16P", "AB/16P", "BK/01","AA/17E", "TT/16P", "ND/16P", "AB/18P", "AB/19P", "SC/18P", "SC/19P", "AA/17P", "AA/18P", "AA/19P");
		$("#voucherSerial").jqxInput({width: '95%', height: 20, source: voucherS, theme:'energyblue'});
		
		$("#voucherNumber").jqxFormattedInput({width: '95%', height: 20, value: ''});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#createInvoiceVATWindow"), 900, 570);
	};
	var initValidator = function(){
		$("#createInvoiceVATWindow").jqxValidator({
			rules: [
				{ input: '#invoiceDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				}, 
				{ input: '#voucherForm', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				}, 
				{ input: '#voucherNumber', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				}, 
				{ input: '#voucherSerial', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				}
			]
		});
	};
	var initOpen = function(){
		Loading.show('loadingMacro');
		var date = new Date();
		$("#invoiceDate").val(date);
		var data = globalVar.formData.data != "undefined"? globalVar.formData.data : {};
		data.orderDate = $("#orderDateSearch").jqxDateTimeInput('val', 'date').getTime();
		delete data.orderIdNotSelected;
		delete data.orderIdSelected;
		delete data.selectedAll;
		if(_isCheckedAll){
			data.selectedAll = "Y";
			if(_unSelectedArr.length > 0){
				data.orderIdNotSelected = JSON.stringify(_unSelectedArr);
			}
		}else{
			data.selectedAll = "N";
			data.orderIdSelected = JSON.stringify(_selectedArr);
		}
		$.ajax({
			url: 'getInvoiceItemByOrderList',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					$("#createInvoiceVATWindow").jqxWindow('close');
					bootbox.dialog(response.errorMessage,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]);
					return;
				}
				updateGridLocalData($("#invoiceItemProductGrid"), response.listReturn);
				$("#productStoreIdHidden").val(response.productStoreId);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	
	var resetData = function(){
		updateGridLocalData($("#invoiceItemProductGrid"), []);
		$("#description").val("");
		$("#voucherForm").val("");
		$("#voucherNumber").val("");
		$("#voucherSerial").val("");
		$("#invoiceDate").val(null);
		$("#productStoreIdHidden").val("");
		$("#createInvoiceVATWindow").jqxValidator('hide');
	};
	var resetOrderSelected = function(){
		_isCheckedAll = false;
		_unSelectedArr = [];
		_selectedArr = [];
	};
	var getOrderSelectedData = function(){
		var data = globalVar.formData.data != "undefined"? globalVar.formData.data : {};
		data.orderDate = $("#orderDateSearch").jqxDateTimeInput('val', 'date').getTime();
		if(_isCheckedAll){
			data.selectedAll = "Y";
			if(_unSelectedArr.length > 0){
				data.orderIdNotSelected = JSON.stringify(_unSelectedArr);
			}
		}else{
			data.selectedAll = "N";
			data.orderIdSelected = JSON.stringify(_selectedArr);
		}
		return data;
	};
	var get = function(){
		return {_unSelectedArr: _unSelectedArr,
		_isCheckedAll: _isCheckedAll,
		_selectedArr: _selectedArr}
	};
	return{
		init: init,
		resetOrderSelected: resetOrderSelected,
		getOrderSelectedData: getOrderSelectedData,
		get: get
	}
}());

var orderInvoiceNotObj = (function(){
	var _orderIdArr = [];
	var init = function(){
		initInput();
		initDropDown();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#customerNameVAT").jqxInput({width: '93%', height: 22});
		$("#companyNameVAT").jqxInput({width: '93%', height: 22});
		$("#taxInfoIdVAT").jqxInput({width: '93%', height: 22});
		$("#bankIdVAT").jqxInput({width: '93%', height: 22});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#enumPaymentMethodVAT"), globalVar.enumPaymentMethodArr, {valueMember: 'enumId', displayMember: 'description', width: '95%', height: 25});
	};
	var initDropDownGrid = function(){
		$("#orderListVATDropDown").jqxDropDownButton({width: '95%', height: 25});
		var grid = $("#orderListGrid");
		var datafield = [{name: 'orderId', type: 'string'}];
		var columns = [{text: uiLabelMap.BSOrderId, datafield: 'orderId', width: '100%'}];
		var config = {
				url: '',
				showtoolbar : false,
				width : 270,
				virtualmode: false,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				pagermode: "simple",
				source: {
					pagesize: 10,
					localdata: [],
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
		grid.jqxGrid({pagermode: "simple"});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#orderInvoiceNoteInfoWindow"), 470, 440);
	};
	var initEvent = function(){
		$("#enumPaymentMethodVAT").on('select', function(event){
			var args = event.args;
			if(args) {
				var index = args.index;
				var item = args.item;
				var value = item.value;
				if(value == "POSNoteTienGuiNganHang"){
					$("#bankIdVAT").jqxInput({disabled: false});
				}else if(value == "POSNoteTienMat"){
					$("#bankIdVAT").jqxInput({disabled: true});
				}
			}
		});
		
		$("#orderInvoiceNoteInfoWindow").on('open', function(event){
			$("#enumPaymentMethodVAT").jqxDropDownList({selectedIndex: 0});
			var data = viewOrderNotVATObj.getOrderSelectedData();
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getOrderListSelectedInOrderNotVATInv',
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
					_orderIdArr = response.listReturn;
					var localdata = [];
					_orderIdArr.forEach(function(orderId){
						localdata.push({orderId: orderId});
					});
					updateGridLocalData($("#orderListGrid"), localdata);
					var dropDownContent = '<div class="innerDropdownContent">' + _orderIdArr.length + ' ' + uiLabelMap.BACCOrderIsSelected + '</div>';
					$("#orderListVATDropDown").jqxDropDownButton('setContent', dropDownContent);
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
		
		$("#orderInvoiceNoteInfoWindow").on('close', function(event){
			Grid.clearForm($("#orderInvoiceNoteInfoWindow"));
			$("#orderListGrid").jqxGrid('clearselection');
			updateGridLocalData($("#orderListGrid"), []);
			_orderIdArr = [];
		});
		$("#cancelCreateOrderInvoiceNote").click(function(e){
			$("#orderInvoiceNoteInfoWindow").jqxWindow('close');
		});
		$("#saveCreateOrderInvoiceNote").click(function(e){
			var valid = $("#orderInvoiceNoteInfoWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateOrderInvoiceNoteConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createOrderInvoiceNote();
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
	var initValidator = function(){
		$("#orderInvoiceNoteInfoWindow").jqxValidator({
			rules: [
				{ input: '#customerNameVAT', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#companyNameVAT', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
				{ input: '#addressVAR', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!$(input).val()){
							return false;
						}
						return true;
					}
				},      
			]
		});
	};
	var getData = function(){
		var data = {};
		data.customerName = $("#customerNameVAT").val();
		data.companyName = $("#companyNameVAT").val();
		if($("#taxInfoIdVAT").val()){
			data.taxInfoId = $("#taxInfoIdVAT").val();
		}
		data.address = $("#addressVAR").val();
		data.paymentMethod = $("#enumPaymentMethodVAT").val();
		if($("#bankIdVAT").val()){
			data.bankId = $("#bankIdVAT").val();
		}
		data.orderIds = JSON.stringify(_orderIdArr);
		return data;
	};
	var createOrderInvoiceNote = function(){
		Loading.show('loadingMacro');
		var data = getData(); 
		$.ajax({
			url: 'createListOrderInvoiceNote',
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
				$("#orderInvoiceNoteInfoWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
				$("#jqxgrid").jqxGrid('clearselection');
				viewOrderNotVATObj.resetOrderSelected();
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var openWindow = function(){
		accutils.openJqxWindow($("#orderInvoiceNoteInfoWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	viewOrderNotVATObj.init();
	orderInvoiceNotObj.init();
});