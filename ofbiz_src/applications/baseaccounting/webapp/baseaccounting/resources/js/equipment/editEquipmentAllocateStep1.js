/**================================= step 1 =======================================**/
var editEquipmentAllocStep1 = (function(){
	var _equipmentStoreAndParty = {};
	var init = function(){
		initInput();
		initGrid();
		initValidator();
	};
	var initInput = function(){
		$("#voucherDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#voucherNbr").jqxInput({width: '95%', height: 22});
	};
	var initGrid = function(){
		var grid = $("#equipmentAllocItemGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
		                 {name: 'equipmentName', type: 'string'},
		                 {name: 'allocatedAmount', type: 'number'},
		                 {name: 'allocationAmountUsing', type: 'number'},
		                 {name: 'depGlAccountId', type: 'string'},
		                 {name: 'debitGlAccountId', type: 'string'},
		                 {name: 'costGlAccountId', type: 'string'},
		                 {name: 'dateArising', type: 'date'}
		              ];
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '15%'},
		               {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '30%'},
		               {text: uiLabelMap.DateArising, datafield: 'dateArising', width: '14%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
		               {text: uiLabelMap.BACCTotalAllocatedAmount, dataField: 'allocatedAmount', width: '20%', filtertype: 'number', columntype: 'numberinput',
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
					    {text: uiLabelMap.BACCAllocationAmountOfUsingEquipment, dataField: 'allocationAmountUsing', width: '21%', filtertype: 'number', columntype: 'numberinput',
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
					    }
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
				titleProperty: uiLabelMap.BACCEquipment,
				customcontrol1: customcontrol1,
				customcontrol2: customcontrol2
			}
		};
	    new OlbGrid(grid, null, config, []);
	};
	
	var removeItemFromGrid = function(){
		var rowindexes = $("#equipmentAllocItemGrid").jqxGrid("getselectedrowindexes");
		if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
			jOlbUtil.alert.error(uiLabelMap.BACCYouNotYetChooseEquipment);
			return false;
		}
		for (var i = 0; i < rowindexes.length; i++) {
			var dataItem = $("#equipmentAllocItemGrid").jqxGrid("getrowdata", rowindexes[i]);
			if (dataItem) {
				$("#equipmentAllocItemGrid").jqxGrid('deleterow', dataItem.uid);
			}
		}
	};
	
	var openPopupAdd = function(){
		$("#newEquipAllocItemWindow").jqxWindow("open");
	};
	
	var initValidator = function(){
		$("#step1").jqxValidator({
			rules: [
				{ input: '#voucherDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
	
	var validate = function(){
		var valid = $("#step1").jqxValidator('validate');
		if(!valid){
			return false;
		}
		var rows = $("#equipmentAllocItemGrid").jqxGrid('getrows');
		if(rows.length > 0){
			return true;
		}
		bootbox.dialog(uiLabelMap.EquipmentIsNotSelected,
				[{
					 "label" : uiLabelMap.CommonClose,
					 "class" : "btn-danger btn-small icon-remove open-sans",
				 }
				 ]		
		);
		return false;
	};
	
	var initWindowOpenEvent = function(){
		var month = $("#allocateMonth").val();
		var year = $("#allocatedYear").val();
		var date = new Date(year, parseInt(month), 1);
		var source = $("#equipmentGrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetListEquipmentAbilityToAllocate&date=' + date.getTime() + '&month=' + month + '&year=' + year;
		$("#equipmentGrid").jqxGrid('source', source);
		
		getListEquipmentAllocItemGrid(date.getTime(), month, year);
	};
	
	var getListEquipmentAllocItemGrid = function(date, month, year) {
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getListEquipmentAllocItemGrid',
			type: "POST",
			data: {
				date: date,
				month: month,
				year: year
			},
			success: function(response) {
				updateGridLocalData($("#equipmentAllocItemGrid"), response.listReturn);
				var mapReturn = response.mapReturn;
				var equipmentStoreAndParty = editEquipmentAllocStep1.getEquipmentStoreAndParty();
				for (var key in mapReturn) {
					if (mapReturn.hasOwnProperty(key)) {
						equipmentStoreAndParty[key] = mapReturn[key];
					}
				}
			},
			complete: function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var getEquipmentStoreAndParty = function(){
		return _equipmentStoreAndParty;
	};
	
	var resetData = function(){
		_equipmentStoreAndParty = {};
		Grid.clearForm($("#step1 form"));
		updateGridLocalData($("#equipmentAllocItemGrid"), []);
		$("#step1").jqxValidator('hide');
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		source._source.id = 'equipmentId';
		var deleteFuncCustom = function(rowid, commit){
			delete _equipmentStoreAndParty[rowid];
		};
		source._source.deleteFuncCustom = deleteFuncCustom;
		grid.jqxGrid('source', source);
	};
	var getData = function(){
		var data = {};
		var rows = $("#equipmentAllocItemGrid").jqxGrid('getrows');
		var voucherDate = $("#voucherDate").jqxDateTimeInput('val', 'date');
		data.voucherDate = voucherDate.getTime();
		data.voucherNbr = $("#voucherNbr").val();
		if($("#comment").val()){
			data.comment = $("#comment").val(); 
		}
		var item = [];
		rows.forEach(function(row){
			var tempRow = {};
			tempRow.equipmentId = row.equipmentId;
			tempRow.allocatedAmount = row.allocatedAmount;
			tempRow.allocationAmountUsing = row.allocationAmountUsing;
			item.push(tempRow);
		});
		data.equipmentAllocItem = JSON.stringify(item);
		return data;
	};
	return{
		init: init,
		initWindowOpenEvent: initWindowOpenEvent,
		validate: validate,
		resetData: resetData,
		getEquipmentStoreAndParty: getEquipmentStoreAndParty,
		getData: getData,
		openPopupAdd: openPopupAdd,
		removeItemFromGrid: removeItemFromGrid
	}
}());

/**--------------------------------------------------------------------------------------**/

var equipmentAllocationItemObj = (function(){
	var init = function(){
		initDropDownGrid();
		initInput();
		initWindow();
		initValidator();
		initEvent();
	};
	var initDropDownGrid = function(){
		$("#equipmentDropDown").jqxDropDownButton({width: '97%', height: 25});
		var grid = $("#equipmentGrid");
		var datafield =  [
				{name: 'equipmentId', type: 'string'},
				{name: 'equipmentName', type: 'string'},
				{name: 'allocatedAmount', type: 'number'},
                {name: 'depGlAccountId', type: 'string'},
                {name: 'debitGlAccountId', type: 'string'},
                {name: 'costGlAccountId', type: 'string'}				
    	];
		var columns = [{text: uiLabelMap.BACCEquipmentId, datafield: 'equipmentId', width: '20%'},
		               {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentName', width: '50%'},
		               {text: uiLabelMap.BACCTotalAllocatedAmount, dataField: 'allocatedAmount', filtertype: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, columns, value){
								var data = grid.jqxGrid('getrowdata', row);
		                		if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + formatcurrency(value) + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
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
		        url: '',                
		        source: {pagesize: 5}
	      	};
	      	Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initInput = function(){
		$('#allocatedAmountItem').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#allocationAmountUsingItem').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipAllocItemWindow"), 400, 220);
	};
	var initValidator = function(){
		$("#newEquipAllocItemWindow").jqxValidator({
			rules: [
				{ input: '#equipmentDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val()){
				    	   return true;
				       }
				       return false;
					}
				},
				{ input: '#allocationAmountUsingItem', message: uiLabelMap.ValueMustBeLessThanEqualTotalAmount, action: 'keyup, change', 
					rule: function (input, commit) {
						var totalAllocatedAmount = $("#allocatedAmountItem").val();
						if(input.val() > totalAllocatedAmount){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var initEvent = function(){
		$("#equipmentGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#equipmentGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.equipmentName + ' [' + rowData.equipmentId + ']</div>';
			$("#equipmentDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#equipmentDropDown").attr("data-value", rowData.equipmentId);
			$("#equipmentDropDown").attr("data-label", rowData.equipmentName);
			$("#equipmentDropDown").jqxDropDownButton('close');
			$('#allocatedAmountItem').val(rowData.allocatedAmount);
			$('#allocationAmountUsingItem').val(rowData.allocatedAmount);
		});
		$("#newEquipAllocItemWindow").on('close', function(e){
			resetData();
		});
		$("#cancelAddEquipAllocateItem").click(function(e){
			$("#newEquipAllocItemWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipAllocateItem").click(function(e){
			var valid = $("#newEquipAllocItemWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipment(false);
		});
		$("#saveAddEquipAllocateItem").click(function(e){
			var valid = $("#newEquipAllocItemWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipment(true);
		});
	};
	var addEquipment = function(isCloseWindow){
		Loading.show('loadingMacro');
		var rowData = {};
		var item = $("#equipmentGrid").jqxGrid('getSelectedRowIndex');
		var data = $('#equipmentGrid').jqxGrid('getRowData', item);
		rowData.equipmentId = $("#equipmentDropDown").attr('data-value');
		rowData.equipmentName = $("#equipmentDropDown").attr('data-label');
		rowData.allocatedAmount = $("#allocationAmountUsingItem").val();
		rowData.allocationAmountUsing = $("#allocationAmountUsingItem").val();
		rowData.costGlAccountId = data.costGlAccountId; 
		rowData.creditGlAccountId = data.debitGlAccountId;
		rowData.debitGlAccountId = data.depGlAccountId; 
		
		var checkRowExists = $("#equipmentAllocItemGrid").jqxGrid('getrowboundindexbyid', rowData.equipmentId);
		if(checkRowExists > -1){
			$('#equipmentAllocItemGrid').jqxGrid('updaterow', rowData.equipmentId, rowData);
		}else{
			$("#equipmentAllocItemGrid").jqxGrid('addrow', null, rowData, 'first');
		}
		$.ajax({
			url: 'getEquipmentPartyAndStoreAndPostedInfo',
			type: "POST",
			data: {equipmentId: rowData.equipmentId},
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
				  var equipmentStoreAndParty = editEquipmentAllocStep1.getEquipmentStoreAndParty();
				  equipmentStoreAndParty[rowData.equipmentId] = response.equipmentPartyAndStoreList;
				  if(isCloseWindow){
					  $("#newEquipAllocItemWindow").jqxWindow('close');
				  }else{
					  resetData();
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	var resetData = function(){
		$("#equipmentGrid").jqxGrid('clearselection');
		$('#equipmentGrid').jqxGrid('gotopage', 0);
		$("#equipmentDropDown").jqxDropDownButton('setContent', "");
		$('#allocatedAmountItem').val(0);
		$('#allocationAmountUsingItem').val(0);
	};
	return{
		init: init
	}
}());