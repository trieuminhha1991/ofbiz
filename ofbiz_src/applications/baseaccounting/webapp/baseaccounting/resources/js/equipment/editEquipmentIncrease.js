var editEquipmentIncreaseObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initDropDownSupplier();
		initEquipmentGrid();
		initWindow();
		initEvent();
		initValidator();
		$("#postingBtn").hide();
		$("#unpostedBtn").hide();
	};
	var initInput = function(){
		$("#dateArising").jqxDateTimeInput({width: '97%', height: 25});
		$("#voucherNbr").jqxInput({width: '95%', height: 22});
		$("#address").jqxInput({width: '95%', height: 22});
		$("#receiver").jqxInput({width: '95%', height: 22});
		$("#comment").jqxInput({width: '95%', height: 22});
	};
	var initDropDownSupplier = function(){
		$("#supplierDropDown").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'}); 
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
			{name: 'addressDetail', type: 'string'},
      	];
      	var columnlist = [
               {text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
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
	
	var initEquipmentGrid = function(){
		var grid = $("#equipmentItemGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
		                 {name: 'equipmentName', type: 'string'},
		                 {name: 'quantityUom', type: 'string'},
		                 {name: 'allocationTimes', type: 'number'},
		                 {name: 'debitGlAccountId', type: 'string'},
		                 {name: 'costGlAccountId', type: 'string'},
		                 {name: 'currencyUomId', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'depAmount', type: 'number'},
		                 {name: 'unitPrice', type: 'number'},
		                 {name: 'totalPrice', type: 'number'},		                 
		                 ];
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '12%', editable: false},
		                  {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '20%', editable: false},
		                  {text: uiLabelMap.BACCEquipQuantityUom, datafield: 'quantityUom', width: '12%', editable: false},
		                  {text: uiLabelMap.BACCAllowTimes, datafield: 'allocationTimes', width: '14%', 
		                	  cellsrenderer: function(row, columns, value){
		                		  if(typeof(value) != 'undefined'){
		                			  return '<span style="text-align: right">'+ value + '</span>';
		                		  }
		                	  }
		                  },
		                  {text: uiLabelMap.BACCPrepaidExpensesAccount, datafield: 'debitGlAccountId', width: '12%'},
		                  {text: uiLabelMap.BACCInstrumentToolsAccount, datafield: 'costGlAccountId', width: '12%'},
		                  {text: uiLabelMap.BACCQuantity, datafield: 'quantity', width: '10%',
		                	   cellsrenderer: function(row, column, value){
									if(typeof(value) == 'number'){
										return '<span style="text-align: right">' + value + '</span>';	
									}
									return '<span>' + value + '</span>';
								} 
		                  },
		                  {text: uiLabelMap.BACCDepAmount, dataField: 'depAmount', width: '16%', cellsformat: 'f',
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = grid.jqxGrid('getrowdata',row);
		                		  return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                	  }
						 },		                  
		                  {text: uiLabelMap.BACCUnitPrice, dataField: 'unitPrice', width: '16%', 
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = grid.jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
						 {text: uiLabelMap.BACCTotal, dataField: 'totalPrice', width: '17%', 
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = grid.jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
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
				editable: true,
				editmode:'selectedcell',
				selectionmode: 'singlecell',
				localization: getLocalization(),
				pageable: true,
				source: {
					pagesize: 5,
					localdata: [],
					id: 'equipmentId'
				}
		};
		
	    Grid.initGrid(config, datafield, columns, null, grid);
				
		$("#equipmentItemGrid").on('cellvaluechanged', function (event) { 
            var column = event.args.datafield;
            var rowBoundIndex = event.args.rowindex;
            if (column == "allocationTimes") {
            	var _data = $('#equipmentItemGrid').jqxGrid('getrowdata', rowBoundIndex);
                _data.depAmount = (_data.totalPrice / _data.allocationTimes).toFixed(2);                
                $("#equipmentItemGrid").jqxGrid('setcellvalue', rowBoundIndex, 'depAmount', _data.depAmount);
                $("#equipmentItemGrid").jqxGrid('refreshdata');
            } 
        });	    
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewEquipIncreaseWindow"), 820, 530);
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
		
		$("#addNewEquipIncreaseWindow").on('open', function(e){
			if(_isEdit){
				$("#dateArising").val(_data.dateArising);
				$("#voucherNbr").val(_data.voucherNbr);
				$("#comment").val(_data.comment);
				$("#address").val(_data.address);
				$("#receiver").val(_data.receiver);
				if(_data.supplierId){
					var dropDownContent = '<div class="innerDropdownContent">' + _data.supplierName + ' [' + _data.supplierCode + ']</div>';
					$("#supplierDropDown").jqxDropDownButton('setContent', dropDownContent);
					$("#supplierDropDown").attr("data-value", _data.supplierId);
				}
				getEquipmentIncreaseItemList(_data.equipmentIncreaseId);
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
			var url = 'jqxGeneralServicer?sname=JQGetListEquipmentNotIncrease';
			if(typeof(_data.equipmentIncreaseId) != 'undefined'){
				url += '&equipmentIncreaseId=' + _data.equipmentIncreaseId;
			}
			source._source.url = url;
			$("#equipmentGrid").jqxGrid('source', source);
		});
		
		$("#addNewEquipIncreaseWindow").on('close', function(e){
			Grid.clearForm($("#addNewEquipIncreaseWindow form"));
			updateGridLocalData($("#equipmentItemGrid"), []);
			$("#supplierGrid").jqxGrid('clearselection');
			$('#supplierGrid').jqxGrid('clearfilters');
			$('#supplierGrid').jqxGrid('gotopage', 0);
			$("#supplierDropDown").jqxDropDownButton('setContent', "");
			$("#addNewEquipIncreaseWindow").jqxValidator('hide');
			_isEdit = false;
			_data = {};
			$("#postingBtn").hide();
			$("#unpostedBtn").hide();
			enableEdit();
		});
		$("#cancelAddEquipIncrease").click(function(e){
			$("#addNewEquipIncreaseWindow").jqxWindow('close');
		});
		$("#saveAddEquipIncrease").click(function(e){
			var valid = $("#addNewEquipIncreaseWindow").jqxValidator('validate');
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
				editEquipmentIncrease();
			}else{
				bootbox.dialog(uiLabelMap.CreateEquipmentIncreaseConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 editEquipmentIncrease();
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
							 updatePostedEquipmentIncrease(false);
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
			updatePostedEquipmentIncrease(true);
		});
	};
	
	var initValidator = function(){
		$("#addNewEquipIncreaseWindow").jqxValidator({
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
		if($("#address").val()){
			data.address = $("#address").val() 
		}
		if($("#receiver").val()){
			data.receiver = $("#receiver").val() 
		}
		if($("#supplierDropDown").attr('data-value')){
			data.supplierId = $("#supplierDropDown").attr('data-value');
		}
		var rows = $("#equipmentItemGrid").jqxGrid('getrows');
		var equipmentItemArr = [];
		rows.forEach(function(row){
			var tempData = {};
			tempData.equipmentId = row.equipmentId;
			tempData.allocationTimes = row.allocationTimes;
			tempData.depAmount = row.depAmount;
			if(row.debitGlAccountId){
				tempData.debitGlAccountId = row.debitGlAccountId;
			}
			if(row.costGlAccountId){
				tempData.costGlAccountId = row.costGlAccountId;
			}
			equipmentItemArr.push(tempData);
		});
		data.equipmentIncreaseItem = JSON.stringify(equipmentItemArr);
		return data;
	};
	
	var editEquipmentIncrease = function(){
		Loading.show('loadingMacro');
		var data = getData();
		var url = 'createEquipmentIncreaseAndItem';
		if(_isEdit){
			data.equipmentIncreaseId = _data.equipmentIncreaseId;
			url = 'updateEquipmentIncreaseAndItem';
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
				  $("#addNewEquipIncreaseWindow").jqxWindow('close');
				  $("#jqxgrid").jqxGrid('updatebounddata');
				  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var updatePostedEquipmentIncrease = function(isPosted){
		Loading.show('loadingMacro');
		var data = {equipmentIncreaseId: _data.equipmentIncreaseId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedEquipmentIncrease',
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
	
	var getEquipmentIncreaseItemList = function(equipmentIncreaseId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getEquipmentIncreaseItem',
			type: "POST",
			data: {equipmentIncreaseId: equipmentIncreaseId},
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
					  updateGridLocalData($("#equipmentItemGrid"), response.equipmentIncreaseItemList);
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
		$("#supplierDropDown").jqxDropDownButton({disabled: true});
		$("#voucherNbr").jqxInput({disabled: true});
		$("#address").jqxInput({disabled: true});
		$("#comment").jqxInput({disabled: true});
		$("#receiver").jqxInput({disabled: true});
		$("#toolbarequipmentItemGrid").hide();
		$("#saveAddEquipIncrease").attr("disabled", "disabled");
	};
	var enableEdit = function(){
		$("#dateArising").jqxDateTimeInput({readonly: false});
		$("#dateArising").jqxDateTimeInput({disabled: false});
		$("#supplierDropDown").jqxDropDownButton({disabled: false});
		$("#voucherNbr").jqxInput({disabled: false});
		$("#address").jqxInput({disabled: false});
		$("#comment").jqxInput({disabled: false});
		$("#receiver").jqxInput({disabled: false});
		$("#toolbarequipmentItemGrid").show();
		$("#saveAddEquipIncrease").removeAttr("disabled");
	};
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#addNewEquipIncreaseWindow"));
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());

/**=================================================================================================*/

var equipmentItemObj = (function(){
	var init = function(){
		initInput();
		initEquipmentGrid();
		initAccountDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$('#allocationTimes').jqxNumberInput({min: 0, width: '97%', spinButtons: true, decimalDigits: 0});
		$('#quantity').jqxNumberInput({min: 0, width: '97%', spinButtons: true, decimalDigits: 0, disabled: true});
		$('#unitPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#totalPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
	};
	var initAccountDropDown = function(){
		$("#debitAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		$("#costAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		
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
		Grid.initGrid(configGrid2, datafields, columns, null, $("#costAccGrid"));
	};
	var initEquipmentGrid = function(){
		$("#equipmentDropDown").jqxDropDownButton({width: '97%', height: 25});
		var grid = $("#equipmentGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
					     {name: 'equipmentName', type: 'string'},
					     {name: 'quantity', type: 'number'},
					     {name: 'unitPrice', type: 'number'},
					     {name: 'totalPrice', type: 'number'},
					     {name: 'quantityUom', type: 'number'},
					     {name: 'currencyUomId', type: 'number'},
					     {name: 'assetGlAccountId', type: 'string'},
					     {name: 'depGlAccountId', type: 'string'},
					     {name: 'accDepGlAccountId', type: 'string'},
					     {name: 'lossGlAccountId', type: 'string'},
					     {name: 'profitGlAccountId', type: 'string'}];
		
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '19%'},
						{text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '33%'},
						{text: uiLabelMap.BACCQuantity, datafield: 'quantity', width: '15%', filterType: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									return '<span style="text-align: right">' + value + '</span>';	
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: uiLabelMap.BACCUnitPrice, dataField: 'unitPrice', width: '23%', 
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = grid.jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
						 {text: uiLabelMap.BACCTotal, dataField: 'totalPrice', width: '24%', 
							 filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = grid.jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style="text-align: right">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
						 ];
		var config = {
				url: '',
				filterable: true,
				showtoolbar: false,
				width : 600,
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
		accutils.createJqxWindow($("#newEquipmentWindow"), 450, 390);
	};
	var initEvent = function(){
		$("#newEquipmentWindow").on('open', function(e){			
		});
		$("#newEquipmentWindow").on('close', function(e){
			resetData();
		});
		$("#debitAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#debitAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.glAccountId);
			$("#debitAccDropDown").jqxDropDownButton('close');
		});
		$("#costAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#costAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#costAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#costAccDropDown").attr("data-value", rowData.glAccountId);
			$("#costAccDropDown").jqxDropDownButton('close');
		});
		$("#equipmentGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#equipmentGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.equipmentName + ' [' + rowData.equipmentId + ']</div>';
			$("#equipmentDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#equipmentDropDown").attr("data-value", rowData.equipmentId);
			$("#equipmentDropDown").jqxDropDownButton('close');
			$('#quantity').val(rowData.quantity);
			$('#unitPrice').val(rowData.unitPrice);
			$('#totalPrice').val(rowData.totalPrice);			
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.accDepGlAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.accDepGlAccountId);
			
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.assetGlAccountId + '</div>';
			$("#costAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#costAccDropDown").attr("data-value", rowData.assetGlAccountId);						
		});
		$("#cancelAddEquipment").click(function(e){
			$("#newEquipmentWindow").jqxWindow('close');
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
	};
	
	var addEquipment = function(){
		var rowData = {};
		var equipmentRowSelected = $("#equipmentGrid").jqxGrid('getselectedrowindex');
		var equipmentData = $("#equipmentGrid").jqxGrid('getrowdata', equipmentRowSelected);
		rowData.equipmentId = equipmentData.equipmentId;
		rowData.equipmentName = equipmentData.equipmentName;
		rowData.quantity = equipmentData.quantity;
		rowData.unitPrice = equipmentData.unitPrice;
		rowData.totalPrice = equipmentData.totalPrice;
		rowData.quantityUom = equipmentData.quantityUom;
		rowData.debitGlAccountId = $("#debitAccDropDown").attr('data-value');
		rowData.costGlAccountId = $("#costAccDropDown").attr('data-value');
		rowData.costAccountId = $("#costAccDropDown").attr('data-value');
		rowData.allocationTimes = $("#allocationTimes").val();
		rowData.depAmount = (equipmentData.totalPrice / rowData.allocationTimes).toFixed(2);
		var checkRowExists = $("#equipmentItemGrid").jqxGrid('getrowboundindexbyid', rowData.equipmentId);
		if(checkRowExists > -1){
			$('#equipmentItemGrid').jqxGrid('updaterow', rowData.equipmentId, rowData);
		}else{
			$("#equipmentItemGrid").jqxGrid('addrow', null, rowData, 'first');
		}
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
				{ input: '#allocationTimes', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
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
	var resetData = function(){
		$("#equipmentGrid").jqxGrid('clearselection');
		$("#debitAccGrid").jqxGrid('clearselection');
		$("#costAccGrid").jqxGrid('clearselection');
		
		$('#equipmentGrid').jqxGrid('clearfilters');
		$('#debitAccGrid').jqxGrid('clearfilters');
		$('#costAccGrid').jqxGrid('clearfilters');
		
		$('#equipmentGrid').jqxGrid('gotopage', 0);
		$('#debitAccGrid').jqxGrid('gotopage', 0);
		$('#costAccGrid').jqxGrid('gotopage', 0);
		
		$("#equipmentDropDown").jqxDropDownButton('setContent', "");
		$("#debitAccDropDown").jqxDropDownButton('setContent', "");
		$("#costAccDropDown").jqxDropDownButton('setContent', "");
		
		$('#allocationTimes').val(0);
		$('#quantity').val(0);
		$('#unitPrice').val(0);
		$('#totalPrice').val(0);
		$("#newEquipmentWindow").jqxValidator('hide');
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	editEquipmentIncreaseObj.init();
	equipmentItemObj.init();
});