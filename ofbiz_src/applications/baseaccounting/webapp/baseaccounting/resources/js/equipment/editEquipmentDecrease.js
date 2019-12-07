editEquipmentDecreaseObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initEquipmentGrid();
		initWindow();
		initContextMenu();
		initEvent();
		initValidator();
		$("#postingBtn").hide();
		$("#unpostedBtn").hide();
	};
	var initInput = function(){
		$("#dateArising").jqxDateTimeInput({width: '97%', height: 25});
		$("#voucherNbr").jqxInput({width: '95%', height: 22});
	};
	var initEquipmentGrid = function(){
		var grid = $("#equipmentItemGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
		                 {name: 'equipmentName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'quantityDecrease', type: 'number'},
		                 {name: 'debitGlAccountId', type: 'string'},
		                 {name: 'lossGlAccountId', type: 'string'},
		                 {name: 'unitPrice', type: 'number'},
		                 {name: 'totalPrice', type: 'number'},
		                 {name: 'allocatedValue', type: 'number'},
		                 {name: 'remainValue', type: 'number'},
		                 {name: 'decreaseReason', type: 'string'},
		                 {name: 'currencyUomId', type: 'string'},
		                 ];
		
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '12%'},
		               {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '20%'},
		               {text: uiLabelMap.BACCQuantityInUse, datafield: 'quantity', width: '17%', columntype: 'numberinput',
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + value + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCQuantityDecreased, datafield: 'quantityDecrease', width: '17%', columntype: 'numberinput',
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span style="text-align: right">' + value + '</span>';
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCPrepaidExpensesAccount, datafield: 'debitGlAccountId', width: '12%'},
		               {text: uiLabelMap.BACCEquipmentDecrementAccount, datafield: 'lossGlAccountId', width: '12%'},
		               {text: uiLabelMap.BACCUnitPrice, dataField: 'unitPrice', width: '16%', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
							}
					    },
						{text: uiLabelMap.BACCTotal, dataField: 'totalPrice', width: '17%', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(typeof(value) == 'number'){
			                		return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		                	}
						},
						{text: uiLabelMap.BACCAllocatedValue, dataField: 'allocatedValue', width: '17%', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(typeof(value) == 'number'){
			                		return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		                	}
						},
						{text: uiLabelMap.BACCRemainingValue, dataField: 'remainValue', width: '17%', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata',row);
		                		if(typeof(value) == 'number'){
			                		return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		                	}
						},
						{text: uiLabelMap.BACCDecrementReasonTypeId, datafield: 'decreaseReason', width: '20%'},
		               ];
		
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "equipmentItemGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newEquipmentWindow")});
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
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
				source: {
					pagesize: 5,
					localdata: [],
					id: 'equipmentId'
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	    //Grid.createContextMenu(grid, $("#contextMenuEquipmentItem"), false);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewEquipDecreaseWindow"), 820, 500);
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenuEquipmentItem", 30, 160, {popupZIndex: 22000});
		$("#contextMenuEquipmentItem").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#equipmentItemGrid").jqxGrid('getselectedrowindex');
			var data = $("#equipmentItemGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				$("#equipmentDropDown").jqxDropDownButton({disabled: true});
				var dropDownContent = '<div class="innerDropdownContent">' + data.equipmentName + ' [' + data.equipmentId + ']</div>';
				$("#equipmentDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#equipmentDropDown").attr("data-value", data.equipmentId);
				$("#equipmentDropDown").attr("data-label", data.equipmentName);
				$("#quantity").val(data.quantity);
				$("#quantityDecrease").val(data.quantityDecrease);
				$("#unitPrice").val(data.unitPrice);
				$("#totalPrice").val(data.totalPrice);
				$("#decreaseReason").val(data.decreaseReason);
				var dropDownContentDebit = '<div class="innerDropdownContent">' + data.debitGlAccountId + '</div>';
				$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContentDebit);
				var dropDownContentDecrement = '<div class="innerDropdownContent">' + data.lossGlAccountId + '</div>';
				$("#decrementAccDropDown").jqxDropDownButton('setContent', dropDownContentDecrement);
				$('#allocatedValue').val(data.allocatedValue);
				$('#remainingValue').val(data.remainValue);
				accutils.openJqxWindow($("#newEquipmentWindow")); 
			}
		});
	};
	var initEvent = function(){
		$("#addNewEquipDecreaseWindow").on('open', function(e){
			if(_isEdit){
				$("#dateArising").val(_data.dateArising);
				$("#voucherNbr").val(_data.voucherNbr);
				$("#comment").val(_data.comment);
				getEquipmentDecreaseItemList(_data.equipmentDecreaseId);
				var isPosted = _data.isPosted;
				if(isPosted){
					$("#postingBtn").hide();
					$("#unpostedBtn").hide();
					disableEdit();
				}else{
					$("#postingBtn").show();
					$("#unpostedBtn").hide();
					enableEdit();
				}
			}else{
				$("#dateArising").val(new Date());
			}
			var source = $("#equipmentGrid").jqxGrid('source');
			var url = 'jqxGeneralServicer?sname=JQGetListEquipmentInUse';
			if(typeof(_data.equipmentDecreaseId) != 'undefined'){
				url += '&equipmentDecreaseId=' + _data.equipmentDecreaseId;
			}
			source._source.url = url;
			$("#equipmentGrid").jqxGrid('source', source);
		});
		
		$("#addNewEquipDecreaseWindow").on('close', function(e){
			Grid.clearForm($("#addNewEquipDecreaseWindow form"));
			updateGridLocalData($("#equipmentItemGrid"), []);
			$("#addNewEquipDecreaseWindow").jqxValidator('hide');
			_isEdit = false;
			_data = {};
			enableEdit();
		});
		$("#cancelAddEquipDecrease").click(function(e){
			$("#addNewEquipDecreaseWindow").jqxWindow('close');
		});
		
		$("#saveAddEquipDecrease").click(function(e){
			var valid = $("#addNewEquipDecreaseWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var equipmentItemData = $("#equipmentItemGrid").jqxGrid('getrows');
			if(equipmentItemData.length < 1){
				bootbox.dialog(uiLabelMap.EquipmentIsNotSelected,
						[{
							 "label" : uiLabelMap.CommonClose,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
				return;
			}
			if(_isEdit){
				editEquipmentDecrease();
			}else{
				bootbox.dialog(uiLabelMap.CreateEquipmentDecreaseConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 editEquipmentDecrease();
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
		
		$("#unpostedBtn").click(function(e){
			bootbox.dialog(uiLabelMap.BACCUnpostedConfirm,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 updatePostedEquipmentDecrease(false);
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#postingBtn").click(function(e){
			updatePostedEquipmentDecrease(true);
		});
	};
	var initValidator = function(){
		$("#addNewEquipDecreaseWindow").jqxValidator({
			rules: [
				{ input: '#dateArising', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				}
			]
		});
	};
	
	var getData = function(){
		var data = {};
		var dateArising = $("#dateArising").jqxDateTimeInput('val', 'date');
		data.dateArising = dateArising.getTime();
		data.voucherNbr = $("#voucherNbr").val();
		if($("#comment").val()){
			data.comment = $("#comment").val() 
		}
		var rows = $("#equipmentItemGrid").jqxGrid('getrows');
		var equipmentItemArr = [];
		rows.forEach(function(row){
			var tempData = {};
			tempData.equipmentId = row.equipmentId;
			tempData.quantityDecrease = row.quantityDecrease;
			tempData.quantityInUse = row.quantity;
			tempData.lossGlAccountId = row.lossGlAccountId;
			tempData.remainValue = row.remainValue;
			if(row.decreaseReason){
				tempData.decreaseReason = row.decreaseReason;
			}
			equipmentItemArr.push(tempData);
		});
		data.equipmentDecreaseItem = JSON.stringify(equipmentItemArr);
		return data;
	};
	
	var editEquipmentDecrease = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = 'createEquipmentDecreaseAndItem';
		if(_isEdit){
			data.equipmentDecreaseId = _data.equipmentDecreaseId;
			url = 'updateEquipmentDecreaseAndItem';
		}
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					  );	
					  return;
				  }
				  $("#addNewEquipDecreaseWindow").jqxWindow('close');
				  $("#jqxgrid").jqxGrid('updatebounddata');
				  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var updatePostedEquipmentDecrease = function(isPosted){
		Loading.show('loadingMacro');
		var data = {equipmentDecreaseId: _data.equipmentDecreaseId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedEquipmentDecrease',
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
					return
				}
				$("#jqxgrid").jqxGrid('updatebounddata');
				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
				if(isPosted){
					disableEdit();
					$("#postingBtn").hide();
					$("#unpostedBtn").hide();
				}else{
					enableEdit();
					$("#postingBtn").show();
					$("#unpostedBtn").hide();
				}
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var getEquipmentDecreaseItemList = function(equipmentDecreaseId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getEquipmentDecreaseItem',
			type: "POST",
			data: {equipmentDecreaseId: equipmentDecreaseId},
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
					  updateGridLocalData($("#equipmentItemGrid"), response.equipmentDecreaseItemList);
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var disableEdit = function(){
		$("#dateArising").jqxDateTimeInput({readonly: true});
		$("#dateArising").jqxDateTimeInput({disabled: true});
		$("#voucherNbr").jqxInput({disabled: true});
		$("#toolbarequipmentItemGrid").hide();
		$("#saveAddEquipDecrease").attr("disabled", "disabled");
		$("#contextMenuEquipmentItem").jqxMenu({disabled: true});
		$("#comment").attr("disabled", "disabled");
	};
	var enableEdit = function(){
		$("#dateArising").jqxDateTimeInput({readonly: false});
		$("#dateArising").jqxDateTimeInput({disabled: false});
		$("#voucherNbr").jqxInput({disabled: false});
		$("#toolbarequipmentItemGrid").show();
		$("#saveAddEquipDecrease").removeAttr("disabled");
		$("#contextMenuEquipmentItem").jqxMenu({disabled: false});
		$("#comment").removeAttr("disabled");
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var getEquipmentDecreaseId = function(){
		return _data.equipmentDecreaseId;
	};
	var openWindow = function(data){
		_data = data;
		_isEdit = true;
		accutils.openJqxWindow($("#addNewEquipDecreaseWindow"));
	};
	return{
		init: init,
		getEquipmentDecreaseId: getEquipmentDecreaseId,
		openWindow: openWindow
	}
}());

/**=================================================================================================*/
var equipmentItemObj = (function(){
	var _allocatedValue = 0;
	var init = function(){
		initInput();
		initAccountDropDown();
		initEquipmentGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$('#quantityDecrease').jqxNumberInput({min: 0, width: '97%', spinButtons: true, decimalDigits: 0});
		$('#quantity').jqxNumberInput({min: 0, width: '97%', spinButtons: true, decimalDigits: 0, disabled: true});
		$('#unitPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#totalPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#allocatedValue').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#remainingValue').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true});
		$("#decreaseReason").jqxInput({width: '95%', height: 22})
	};
	var initAccountDropDown = function(){
		$("#debitAccDropDown").jqxDropDownButton({width: '97%', height: 25, disabled: true});
		$("#decrementAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		
		var datafields = [{name: 'glAccountId', type: 'string'}, 
		                  {name: 'accountCode', type: 'string'},
						  {name: 'accountName', type: 'string'}
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var configGrid1 = {
				url: 'JqxGetListGlAccounts',
				filterable: true,
				showtoolbar : false,
				width : 450,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		var configGrid2 = {
				url: 'JqxGetListGlAccounts',
				filterable: true,
				showtoolbar : false,
				width : 450,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(configGrid1, datafields, columns, null, $("#debitAccGrid"));
		Grid.initGrid(configGrid2, datafields, columns, null, $("#decrementAccGrid"));
	};
	var initEquipmentGrid = function(){
		$("#equipmentDropDown").jqxDropDownButton({width: '97%', height: 25});
		var grid = $("#equipmentGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
					     {name: 'equipmentName', type: 'string'},
					     {name: 'unitPrice', type: 'number'},
					     {name: 'quantityInUse', type: 'number'},]
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '30%'},
						{text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName'},
						];
		var config = {
				url: '',
				filterable: true,
				showtoolbar: false,
				width : 500,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipmentWindow"), 450, 500);
	};
	
	var initEvent = function(){
		$("#cancelAddEquipment").click(function(e){
			$("#newEquipmentWindow").jqxWindow('close');
		});
		$("#newEquipmentWindow").on('close', function(e){
			resetData();
		});
		$("#newEquipmentWindow").on('open', function(e){
			var dropDownContent = '<div class="innerDropdownContent">' + '811' + '</div>';
			$("#decrementAccDropDown").jqxDropDownButton('setContent', dropDownContent);
		});
		
		$("#decrementAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#decrementAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#decrementAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#decrementAccDropDown").attr("data-value", rowData.glAccountId);
			$("#decrementAccDropDown").jqxDropDownButton('close');
		});
		
		$("#equipmentGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#equipmentGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.equipmentName + ' [' + rowData.equipmentId + ']</div>';
			$("#equipmentDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#equipmentDropDown").attr("data-value", rowData.equipmentId);
			$("#equipmentDropDown").attr("data-label", rowData.equipmentName);
			$("#equipmentDropDown").jqxDropDownButton('close');
			//$('#totalPrice').val(rowData.totalPrice);
		});
		
		$("#equipmentGrid").on('rowselect', function(event){
			var args = event.args;
			var rowData = args.row;
			var equipmentDecreaseId = editEquipmentDecreaseObj.getEquipmentDecreaseId();
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getEquipmentQuantityInUse',
				type: "POST",
				data: {equipmentDecreaseId: equipmentDecreaseId, equipmentId: rowData.equipmentId},
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						  );	
						  return;
					  }
					  var quantity = response.quantityInUse;
					  $('#quantity').val(quantity);
					  $('#unitPrice').val(rowData.unitPrice);
					  $('#quantityDecrease').val(quantity);
					  var dropDownContent = '<div class="innerDropdownContent">' + response.debitGlAccountId + '</div>';
					  $("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
					  var allocatedValue = response.allocatedValue;
					  _allocatedValue = response.allocatedValue;
					  var total = $("#totalPrice").val();
					  $('#allocatedValue').val(allocatedValue);
					  $('#remainingValue').val(total - allocatedValue);
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#saveAddEquipment").click(function(e){
			var valid = $("#newEquipmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipment();
			$("#newEquipmentWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipment").click(function(e){
			var valid = $("#newEquipmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipment();
			resetData();
		});
		$('#quantityDecrease').on('valueChanged', function (event){
			var value = event.args.value;
			var unitPrice = $('#unitPrice').val();
            var quantity = $('#quantity').val();
            var total = value * unitPrice;
            $('#totalPrice').val(total);
            var allocatedValue = _allocatedValue * value / quantity;
			$('#allocatedValue').val(allocatedValue);
			$('#remainingValue').val(quantity * unitPrice - total - allocatedValue);
		});
	};
	
	var initValidator = function(){
		$("#newEquipmentWindow").jqxValidator({
			rules: [
				{ input: '#equipmentDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
				{ input: '#quantityDecrease', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
					rule: function (input, commit) {
						if(input.val() > 0){
							return true;
						}
						return false;
					}
				},
				{ input: '#quantityDecrease', message: uiLabelMap.ValueMustBeLessThanQuantitInUse, action: 'keyup, change', 
					rule: function (input, commit) {
						var quantityInUse = $("#quantity").val();
						if(input.val() > quantityInUse){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var resetData = function(){
		$("#equipmentGrid").jqxGrid('clearselection');
		$('#equipmentGrid').jqxGrid('clearfilters');
		$('#equipmentGrid').jqxGrid('gotopage', 0);
		$("#equipmentDropDown").jqxDropDownButton('setContent', "");
		
		$('#quantityDecrease').val(0);
		$('#quantity').val(0);
		$('#unitPrice').val(0);
		$('#totalPrice').val(0);
		$('#decreaseReason').val("");
		$("#debitAccDropDown").jqxDropDownButton('setContent', "");
		$("#decrementAccDropDown").jqxDropDownButton('setContent', "");
		$('#allocatedValue').val(0);
		$('#remainingValue').val(0);
		_allocatedValue = 0;
		$("#newEquipmentWindow").jqxValidator('hide');
		$("#equipmentDropDown").jqxDropDownButton({disabled: false});
	};
	var addEquipment = function(){
		var rowData = {};
		rowData.equipmentId = $("#equipmentDropDown").attr('data-value');
		rowData.equipmentName = $("#equipmentDropDown").attr('data-label');
		rowData.quantity = $("#quantity").val();
		rowData.unitPrice = $("#unitPrice").val();
		rowData.totalPrice = $("#totalPrice").val();
		rowData.quantityDecrease = $("#quantityDecrease").val();
		rowData.debitGlAccountId = $("#debitAccDropDown").val();
		rowData.lossGlAccountId = $("#decrementAccDropDown").val();
		rowData.allocatedValue = $("#allocatedValue").val();
		rowData.remainValue = $("#remainingValue").val();
		if($("#decreaseReason").val()){
			rowData.decreaseReason = $("#decreaseReason").val();
		}
		var checkRowExists = $("#equipmentItemGrid").jqxGrid('getrowboundindexbyid', rowData.equipmentId);
		if(checkRowExists > -1){
			$('#equipmentItemGrid').jqxGrid('updaterow', rowData.equipmentId, rowData);
		}else{
			$("#equipmentItemGrid").jqxGrid('addrow', null, rowData, 'first');
		}
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	editEquipmentDecreaseObj.init();
	equipmentItemObj.init();
});