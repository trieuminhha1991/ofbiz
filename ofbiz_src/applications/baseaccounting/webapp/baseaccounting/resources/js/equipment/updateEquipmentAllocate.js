var updateEquipmentAllocateObj = (function(){
	var _data = {};
	var _equipmentList = [];
	var init = function(){
		initInput();
		initGrid();
		initWindow();
		initEvent();
		initContextMenu();
		$("#postingBtn").hide();
		$("#unpostedBtn").hide();
	};
	var initInput = function(){
		$("#updateVoucherDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#updateVoucherNbr").jqxInput({width: '95%', height: 22});
	};
	var initGrid = function(){
		var grid = $("#updateEquipAllocItemPartyGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
		                 {name: 'equipmentName', type: 'string'},
		                 {name: 'allocatedAmount', type: 'number'},
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'productStoreId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'orgUseName', type: 'string'}, 
		                 {name: 'allocatedPercent', type: 'number'}, 
		                 {name: 'amount', type: 'number'}, 
		                 {name: 'costGlAccountId', type: 'string'}, 
		                 {name: 'creditGlAccountId', type: 'string'}, 
		                 {name: 'debitGlAccountId', type: 'string'}
		              ];
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '13%'},
		               {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '23%'},
		               {text: uiLabelMap.BACCTotalCost, dataField: 'allocatedAmount', width: '20%', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata', row);
				        		if(typeof(value) == 'number'){
				        			return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
				        		}
				        		return '<span>' + value + '</span>';
							}, aggregates: ['sum'],
							aggregatesrenderer: function (aggregates, column, element) {
								var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
								$.each(aggregates, function (key, value) {
									var color = 'red';
									renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>' + uiLabelMap.BACCTotal + ': ' + formatcurrency(value) + '</b></div>';
								});
								renderstring += "</div>";
								return renderstring;
							}
					    },
					    {text: uiLabelMap.BACCAllocPartyId, datafield: 'orgUseName', width: '20%'},
					    {text: uiLabelMap.BACCPercent + '(%)', datafield: 'allocatedPercent', width: '10%', columntype: 'numberinput',
					    	cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata', row);
				        		if(typeof(value) == 'number'){
				        			return '<span style="text-align: right">' + value + '%</span>';
				        		}
				        		return '<span>' + value + '</span>';
							}
					    },
					    {text: uiLabelMap.BACCAmount, dataField: 'amount', width: '22%', filtertype: 'number', columntype: 'numberinput',
					    	cellsrenderer: function(row, columns, value){
					    		var data = grid.jqxGrid('getrowdata', row);
					    		if(typeof(value) == 'number'){
					    			return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
					    		}
					    		return '<span>' + value + '</span>';
					    	}, aggregates: ['sum'],
							aggregatesrenderer: function (aggregates, column, element) {
								var renderstring = "<div class='jqx-widget-content jqx-widget-content-" + theme + "' style='float: left; width: 100%; height: 100%; '>";
								$.each(aggregates, function (key, value) {
									var color = 'red';
									renderstring += '<div style="color: ' + color + '; position: relative; margin-top: 10px; margin-right: 2px; text-align: right; overflow: hidden;"><b>' + uiLabelMap.BACCTotal + ': ' + formatcurrency(value) + '</b></div>';
								});
								renderstring += "</div>";
								return renderstring;
							}
					    },
					    {text: uiLabelMap.BACCCostGlAccount, datafield: 'costGlAccountId', width: '14%'},
					    {text: uiLabelMap.CreditAccount, datafield: 'creditGlAccountId', width: '14%'},
					    {text: uiLabelMap.DebitAccount, datafield: 'debitGlAccountId', width: '14%'}
		           ];
		
		var config = {
			showdefaultloadelement: false,
			autoshowloadelement: false,
			dropDownHorizontalAlignment: 'right',
			datafields: datafield,
			columns: columns,
			useUrl: false,
			pagesize: 5,
			width: '100%',
			bindresize: true,
			localization: getLocalization(),
			showtoolbar: true,
			virtualmode: false,
			showfilterrow: true,
			filterable: true,
			sortable: true,
			editable: false,
			pageable: true,
			showstatusbar: true,
			statusbarheight: 40,
			showaggregates: true,
			rendertoolbarconfig: {
				titleProperty: uiLabelMap.BACCAllocation,
				customcontrol1: customcontrol4,
			}
		};
		new OlbGrid(grid, null, config, []);
	    Grid.createContextMenu(grid, $("#menuUpdateEquipAllocItemParty"), false);
	};
	
	var openPopupAdd = function(){
		$("#newEquipAllocItemPtyWindow").jqxWindow("open");
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#updateEquipmentAllocateWindow"), 850, 550);
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("menuUpdateEquipAllocItemParty", 30, 160, {popupZIndex: 22000});
		$("#menuUpdateEquipAllocItemParty").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#updateEquipAllocItemPartyGrid").jqxGrid('getselectedrowindex');
			var data = $("#updateEquipAllocItemPartyGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				equipmentAllocItemPtyAndStoreObj.openWindow(data);
			}else if(action == "delete"){
				bootbox.dialog(uiLabelMap.BACCConfirmDelete,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 $("#updateEquipAllocItemPartyGrid").jqxGrid('deleterow', data.uid);
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
	};
	var initEvent = function(){
		$("#updateEquipmentAllocateWindow").on('close', function(event){
			_data = {};
			_equipmentList = [];
			Grid.clearForm($("#updateEquipmentAllocateWindow form"));
			updateGridLocalData($("#updateEquipAllocItemPartyGrid"), []);
			$("#updateEquipAllocItemPartyGrid").jqxGrid('clearselection');
		});
		$("#updateEquipmentAllocateWindow").on('open', function(event){
			$("#updateVoucherDate").val(_data.voucherDate);
			$("#updateVoucherNbr").val(_data.voucherNbr);
			$("#updateComment").val(_data.comment);
			getEquipmentAllocateAndItemAndParty(_data.equipmentAllocateId);
			var isPosted = _data.isPosted;
			if(isPosted){
				$("#postingBtn").hide();
				$("#unpostedBtn").show();
				disableEdit();
			}else{
				$("#postingBtn").show();
				$("#unpostedBtn").hide();
				enableEdit();
			}
		});
		$("#cancelUpdateEquipAllocate").click(function(e){
			$("#updateEquipmentAllocateWindow").jqxWindow('close');
		});
		$("#saveUpdateEquipAllocate").click(function(e){
			if(!validate()){
				return;
			}
			Loading.show('loadingMacro');	
			var data = getData();
			$.ajax({
				url: 'updateEquipmentAllocateAndItemParty',
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
					  $("#updateEquipmentAllocateWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		
		$("#unpostedBtn").click(function(e){
			bootbox.dialog(uiLabelMap.BACCUnpostedConfirm,
					[
					 {
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 updatePostedEquipmentAllocate(false);
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
			updatePostedEquipmentAllocate(true);
		});
	};
	var getData = function(){
		var data = {};
		data.equipmentAllocateId = _data.equipmentAllocateId;
		var voucherDate = $("#updateVoucherDate").jqxDateTimeInput('val', 'date');
		data.voucherDate = voucherDate.getTime();
		data.updateVoucherNbr = $("#updateVoucherNbr").val();
		if($("#updateComment").val()){
			data.comment = $("#updateComment").val(); 
		}
		data.equipmentAllocItemParty = JSON.stringify($("#updateEquipAllocItemPartyGrid").jqxGrid('getrows'));
		return data;
	};
	var getEquipmentAllocateAndItemAndParty = function(equipmentAllocateId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getEquipmentAllocateAndItemAndParty',
			type: "POST",
			data: {equipmentAllocateId: equipmentAllocateId},
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
					  updateGridLocalData($("#updateEquipAllocItemPartyGrid"), response.equipmentAllocItemStoreAndPartyList);
					  accutils.updateSourceDropdownlist($("#equipmentListItemPty"), response.equipmentAllocItemStoreAndPartyList);
					  _equipmentList = response.equipmentList;
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var validate = function(){
		var rows = $("#updateEquipAllocItemPartyGrid").jqxGrid('getrows');
		var equipmentPercentObj = {};
		_equipmentList.forEach(function(equipment){
			equipmentPercentObj[equipment.equipmentId] = 0;
		});
		rows.forEach(function(row){
			var percent = row.allocatedPercent;
			equipmentPercentObj[row.equipmentId] += percent;
		});
		for(var equipmentId in equipmentPercentObj){
			if(equipmentPercentObj.hasOwnProperty(equipmentId)){
				if(equipmentPercentObj[equipmentId] != 100){
					bootbox.dialog(uiLabelMap.BACCEquipment + ' "<b>' + equipmentId + '</b>": ' + uiLabelMap.BACCEquipmentAllocatePercentNotEqual100,
							[{
								 "label" : uiLabelMap.CommonClose,
								 "class" : "btn-danger btn-small icon-remove open-sans",
							 }
							 ]		
					);
					return false;
				}
			}
		}
		return true;
	};
	
	var updatePostedEquipmentAllocate = function(isPosted){
		Loading.show('loadingMacro');
		var data = {equipmentAllocateId: _data.equipmentAllocateId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedEquipmentAllocate',
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
	
	var disableEdit = function(){
		$("#updateVoucherDate").jqxDateTimeInput({readonly: true});
		$("#updateVoucherDate").jqxDateTimeInput({disabled: true});
		$("#updateVoucherNbr").jqxInput({disabled: true});
		$("#toolbarupdateEquipAllocItemPartyGrid").hide();
		$("#saveUpdateEquipAllocate").attr("disabled", "disabled");
		$("#menuUpdateEquipAllocItemParty").jqxMenu({disabled: true});
		$("#updateComment").attr("disabled", "disabled");
	};
	var enableEdit = function(){
		$("#updateVoucherDate").jqxDateTimeInput({readonly: false});
		$("#updateVoucherDate").jqxDateTimeInput({disabled: false});
		$("#updateVoucherNbr").jqxInput({disabled: false});
		$("#toolbarupdateEquipAllocItemPartyGrid").show();
		$("#saveUpdateEquipAllocate").removeAttr("disabled");
		$("#menuUpdateEquipAllocItemParty").jqxMenu({disabled: false});
		$("#updateComment").removeAttr("disabled");
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var openWindow = function(data){
		_data = data;
		accutils.openJqxWindow($("#updateEquipmentAllocateWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		openPopupAdd: openPopupAdd
	}
}());
$(document).ready(function(){
	updateEquipmentAllocateObj.init();
});