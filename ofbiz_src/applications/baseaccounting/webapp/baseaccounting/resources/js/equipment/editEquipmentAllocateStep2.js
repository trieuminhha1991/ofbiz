/**================================= step 2 ==========================================**/
var editEquipmentAllocStep2 = (function(){
	var init = function(){
		initGrid();
		initDropDown();
		initEvent();
		initContextMenu();
	};
	var initGrid = function(){
		var grid = $("#equipAllocItemPartyGrid");
		var datafield = [{name: 'equipmentId', type: 'string'},
		                 {name: 'equipmentName', type: 'string'},
		                 {name: 'allocatedAmount', type: 'number'},
		                 {name: 'partyId', type: 'string'}, 
		                 {name: 'productStoreId', type: 'string'}, 
		                 {name: 'partyCode', type: 'string'}, 
		                 {name: 'orgUseName', type: 'string'},
		                 {name: 'name', type: 'string'},
		                 {name: 'allocatedPercent', type: 'number'}, 
		                 {name: 'amount', type: 'number'}, 
		                 {name: 'costGlAccountId', type: 'string'},
		                 {name: 'creditGlAccountId', type: 'string'},
		                 {name: 'debitGlAccountId', type: 'string'}
		              ];
		var columns = [
		               {text: uiLabelMap.BACCEquimentName, datafield: 'equipmentId', width: '23%',
		            	   cellsrenderer: function(row, columns, value){
		            		   var rowData = grid.jqxGrid('getrowdata', row);
		            		   if(rowData){
		            			   return '<span>' + rowData.equipmentName + ' [' + value+ ']</span>';
		            		   }
		            	   }
		               },
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
					    {text: uiLabelMap.BACCInstrumentToolsAccount, datafield: 'costGlAccountId', width: '14%'},
					    {text: uiLabelMap.BACCPrepaidExpensesAccount, datafield: 'creditGlAccountId', width: '14%'},
					    {text: uiLabelMap.BACCCostGlAccount, datafield: 'debitGlAccountId', width: '14%'}
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
				customcontrol1: customcontrol3,
			}
		};
		
		new OlbGrid(grid, null, config, []);
	    Grid.createContextMenu(grid, $("#contextMenuEquipAllocItemParty"), false);
	};
	
	var openPopupAdd = function(){
		$("#newEquipAllocItemPtyWindow").jqxWindow("open");
	};
	
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#equipmentList"), [], {valueMember: 'equipmentId', displayMember: 'equipmentName', width: '80%', height: 25, placeHolder: uiLabelMap.CommonSelect + ' ' + uiLabelMap.BACCEquipment});
	};
	var initEvent = function(){
		$("#equipmentList").on('select', function(event){
			var args = event.args;
			if (args) {
				var value = args.item.value;
				//apply filter
				$("#equipAllocItemPartyGrid").jqxGrid('clearfilters');
				if(value != '_NA_'){
					var filtertype = 'stringfilter';
					var filter_or_operator = 1;
					var filtervalue = value;
					var filtercondition = 'equal';
					var filtergroup = new $.jqx.filter();
					var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
					filtergroup.addfilter(filter_or_operator, filter);
					$("#equipAllocItemPartyGrid").jqxGrid('addfilter', 'equipmentId', filtergroup);
					$("#equipAllocItemPartyGrid").jqxGrid('applyfilters');
				}
			}
		});
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenuEquipAllocItemParty", 30, 160, {popupZIndex: 22000});
		$("#contextMenuEquipAllocItemParty").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#equipAllocItemPartyGrid").jqxGrid('getselectedrowindex');
			var data = $("#equipAllocItemPartyGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				equipmentAllocItemPtyAndStoreObj.openWindow(data);
			}else if(action == 'delete'){
				bootbox.dialog(uiLabelMap.BACCConfirmDelete,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 $("#equipAllocItemPartyGrid").jqxGrid('deleterow', data.uid);
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
	var prepareData = function(){
		var equipmentRows = $("#equipmentAllocItemGrid").jqxGrid('getrows');
		accutils.updateSourceDropdownlist($("#equipmentList"), [{equipmentId: '_NA_', equipmentName: uiLabelMap.CommonAll}].concat(equipmentRows));
		var data = [];
		var equipmentPartyObj = editEquipmentAllocStep1.getEquipmentStoreAndParty();
		for(var i = 0; i < equipmentRows.length; i++){
			var row = equipmentRows[i];
			var equipmentPartyAndStoreArr = equipmentPartyObj[row.equipmentId];
			var totalPercent = 0;
			for(var j = 0; j < equipmentPartyAndStoreArr.length; j++){
				var tempRow = {};
				tempRow.allocatedAmount = row.allocatedAmount;
				tempRow.equipmentId = row.equipmentId;
				tempRow.equipmentName = row.equipmentName;
				var equipmentStoreAndParty = equipmentPartyAndStoreArr[j];
				if(typeof(equipmentStoreAndParty.partyId) != 'undefined'){
					tempRow.partyId = equipmentStoreAndParty.partyId;
					tempRow.partyCode = equipmentStoreAndParty.partyCode;
					tempRow.orgUseName = equipmentStoreAndParty.groupName;
				}else if(typeof(equipmentStoreAndParty.productStoreId) != 'undefined'){
					tempRow.productStoreId = equipmentStoreAndParty.productStoreId;
					tempRow.orgUseName = equipmentStoreAndParty.storeName;
				}
				var quantity = equipmentStoreAndParty.quantity;
				var totalQuantity = equipmentStoreAndParty.totalQuantity;
				if(j < equipmentPartyAndStoreArr.length - 1){
					var percent = (quantity / totalQuantity * 100).toFixed(2);
					totalPercent += parseFloat(percent);
				}else{
					var percent = 100 - totalPercent;
				}
				tempRow.allocatedPercent = percent;
				tempRow.amount = row.allocationAmountUsing * (percent/100);
				tempRow.costGlAccountId = row.costGlAccountId;
				tempRow.creditGlAccountId = row.debitGlAccountId;
				tempRow.debitGlAccountId = row.depGlAccountId;
				data.push(tempRow);
			}
		}
		updateGridLocalData($("#equipAllocItemPartyGrid"), data);
		accutils.updateSourceDropdownlist($("#equipmentListItemPty"), data);
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	var resetData = function(){
		updateGridLocalData($("#equipAllocItemPartyGrid"), []);
		accutils.updateSourceDropdownlist($("#equipmentList"), []);
		accutils.updateSourceDropdownlist($("#equipmentListItemPty"), []);
	};
	var validate = function(){
		var rows = $("#equipAllocItemPartyGrid").jqxGrid('getrows');
		var equipmentRows = $("#equipmentAllocItemGrid").jqxGrid('getrows');
		var equipmentPercentObj = {};
		equipmentRows.forEach(function(row){
			equipmentPercentObj[row.equipmentId] = 0;
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
					$("#equipmentList").val(equipmentId);
					return false;
				}
			}
		}
		return true;
	};
	
	var getData = function(){
		var data = [];
		var rows = $("#equipAllocItemPartyGrid").jqxGrid('getrows');
		rows.forEach(function(row){
			var tempRow = {};
			tempRow.equipmentId = row.equipmentId;
			if(typeof(row.partyId) != 'undefined'){
				tempRow.partyId = row.partyId;
			}else if(typeof(row.productStoreId) != 'undefined'){
				tempRow.productStoreId = row.productStoreId;
			}
			tempRow.allocatedPercent = row.allocatedPercent;
			tempRow.costGlAccountId = row.costGlAccountId;
			tempRow.creditGlAccountId = row.creditGlAccountId;
			tempRow.debitGlAccountId = row.debitGlAccountId;
			data.push(tempRow);
		});
		return data;
	};

	return{
		init: init,
		prepareData: prepareData,
		resetData: resetData,
		validate: validate,
		getData: getData,
		openPopupAdd: openPopupAdd
	}
}());

/**-------------------------------------------------------------------------------------------------------**/
var equipmentAllocItemPtyAndStoreObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initTreeDropDown();
		initProductStoreDropDown();
		initGridDropDown();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initTreeDropDown = function(){
		var config = {dropDownBtnWidth: '97%', treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#partyTree"), $("#partyDropDown"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	var initProductStoreDropDown = function(){
		$("#productStoreDropDown").jqxDropDownButton({width: '97%', height: 25});
		var grid = $("#productStoreGrid");
		var datafield =  [
				{name: 'productStoreId', type: 'string'},
				{name: 'storeName', type: 'string'},
    	];
		var columns = [{text: uiLabelMap.BSProductStoreId, datafield: 'productStoreId', width: '30%'},
		               {text: uiLabelMap.BSStoreName, datafield: 'storeName'},
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
	        url: 'JQGetListProductStoreByOrg',                
	        source: {pagesize: 5}
      	};
      	Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initGridDropDown = function(){
		$("#costAccDropDown").jqxDropDownButton({width: '97%', height: 25});
		
		var datafields = [{name: 'glAccountId', type: 'string'}, 
		                  {name: 'accountCode', type: 'string'},
						  {name: 'accountName', type: 'string'}
						  ];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var config = {
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
		Grid.initGrid(config, datafields, columns, null, $("#costAccGrid"));
		
		$("#debitAccDropDown").jqxDropDownButton({width: '97%', height: 25});
			
			var datafields = [{name: 'glAccountId', type: 'string'}, 
			                  {name: 'accountCode', type: 'string'},
							  {name: 'accountName', type: 'string'}
							  ];
			var columns = [
							{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
							{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
						];
			
			var config = {
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
		Grid.initGrid(config, datafields, columns, null, $("#debitAccGrid"));
		
		
		$("#creditAccDropDown").jqxDropDownButton({width: '97%', height: 25});
			
			var datafields = [{name: 'glAccountId', type: 'string'}, 
			                  {name: 'accountCode', type: 'string'},
							  {name: 'accountName', type: 'string'}
							  ];
			var columns = [
							{text: uiLabelMap.BACCGlAccountId, datafield: 'accountCode', width: '30%'},
							{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
						];
			
			var config = {
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
		Grid.initGrid(config, datafields, columns, null, $("#creditAccGrid"));
	};
	var initInput = function(){
		$('#allocatedAmountItemPty').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
		$('#amountItemPty').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true});
		$("#allocatedPercentItemPty").jqxNumberInput({max: 100, min: 0, width: '97%', spinButtons: true, digits: 3, symbolPosition: 'right', symbol: '%'});
		accutils.createJqxDropDownList($("#equipmentListItemPty"), [], {valueMember: 'equipmentId', displayMember: 'equipmentName', width: '97%', height: 25, placeHolder: uiLabelMap.BACCPleaseChooseAcc});
		accutils.createJqxDropDownList($("#orgUseTypeDropDown"), globalVar.orgUseTypeArr, {valueMember: 'type', displayMember: 'description', width: '97%', height: 25});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipAllocItemPtyWindow"), 450, 470);
	};
	var initEvent = function(){
		$("#orgUseTypeDropDown").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if(value === "productStore"){
					$("#productStoreDropDown").show();
					$("#partyDropDown").hide();
				}else if(value === "internalOrganization"){
					$("#productStoreDropDown").hide();
					$("#partyDropDown").show();
				}else{
					$("#productStoreDropDown").hide();
					$("#partyDropDown").hide();
				}
			}
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
		
		$("#debitAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#debitAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#debitAccDropDown").attr("data-value", rowData.glAccountId);
			$("#debitAccDropDown").jqxDropDownButton('close');
		});
		
		$("#creditAccGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#creditAccGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#creditAccDropDown").attr("data-value", rowData.glAccountId);
			$("#creditAccDropDown").jqxDropDownButton('close');
		});
		$("#partyTree").on('select', function(event){
			var item = $('#partyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
	        $("#partyDropDown").jqxDropDownButton('close');
	        accutils.setAttrDataValue('partyDropDown', item.value);
	        $("#partyDropDown").attr('data-label', item.label);
		});
		$("#productStoreGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#productStoreGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.storeName + ' [' + rowData.productStoreId + ']</div>';
			$("#productStoreDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#productStoreDropDown").attr("data-value", rowData.productStoreId);
			$("#productStoreDropDown").attr("data-label", rowData.storeName);
			$("#productStoreDropDown").jqxDropDownButton('close');
		});
		$("#newEquipAllocItemPtyWindow").on('open', function(e){
			var glAccountId = "";
			var creditGlAccountId = "";
			var debitGlAccountId = "";
			if(_isEdit){
				$("#saveAndContinueAddEquipAllocateItemPty").hide();
				glAccountId = _data.costGlAccountId;
				creditGlAccountId = _data.creditGlAccountId;
				debitGlAccountId = _data.debitGlAccountId;
				var dropDownContentParty = '<div class="innerDropdownContent">' + _data.orgUseName + '</div>';
		        if(typeof(_data.partyId) != 'undefined'){
		        	$("#partyDropDown").attr('data-value', _data.partyId);
		        	$("#partyDropDown").attr('data-label', _data.orgUseName);
		        	$("#partyDropDown").jqxDropDownButton('setContent', dropDownContentParty);
		        	$("#orgUseTypeDropDown").val("internalOrganization");
		        }else if(typeof(_data.productStoreId) != 'undefined'){
		        	$("#productStoreDropDown").attr('data-value', _data.productStoreId);
		        	$("#productStoreDropDown").attr('data-label', _data.orgUseName);
		        	$("#productStoreDropDown").jqxDropDownButton('setContent', dropDownContentParty);
		        	$("#orgUseTypeDropDown").val("productStore");
		        }
		        $("#allocatedAmountItemPty").val(_data.allocatedAmount);
		        $("#allocatedPercentItemPty").val(_data.allocatedPercent);
		        $("#amountItemPty").val(_data.amount);
		        $("#equipmentListItemPty").val(_data.equipmentId);
		        $("#equipmentListItemPty").jqxDropDownList({disabled: true});
				$("#partyDropDown").jqxDropDownButton({disabled: true});
				$("#productStoreDropDown").jqxDropDownButton({disabled: true});
				$("#orgUseTypeDropDown").jqxDropDownList({disabled: true});
			}else{
				$("#saveAndContinueAddEquipAllocateItemPty").show();
				$("#equipmentListItemPty").jqxDropDownList({disabled: false});
				$("#orgUseTypeDropDown").jqxDropDownList({selectedIndex: 0});
				$("#partyDropDown").jqxDropDownButton({disabled: false});
				$("#productStoreDropDown").jqxDropDownButton({disabled: false});
				$("#orgUseTypeDropDown").jqxDropDownList({disabled: false});
			}
			var dropDownContent = '<div class="innerDropdownContent">' + glAccountId + '</div>';
			$("#costAccDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#costAccDropDown").attr("data-value", glAccountId);
			$("#costAccDropDown").jqxDropDownButton({disabled: true});
			
			var dropDownCredit = '<div class="innerDropdownContent">' + creditGlAccountId + '</div>';
			$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownCredit);
			$("#creditAccDropDown").attr("data-value", creditGlAccountId);
			$("#creditAccDropDown").jqxDropDownButton({disabled: true});
			
			var dropDownDebit = '<div class="innerDropdownContent">' + debitGlAccountId + '</div>';
			$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownDebit);
			$("#debitAccDropDown").attr("data-value", debitGlAccountId);
			
			
		});
		$("#newEquipAllocItemPtyWindow").on('close', function(e){
			resetData();
		});
		$("#equipmentListItemPty").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var source = $("#equipmentListItemPty").jqxDropDownList('source');
				var localdata = source._source.localdata;
				
				$("#allocatedAmountItemPty").val(localdata[index].allocatedAmount);

				var dropDownContent = '<div class="innerDropdownContent">' + localdata[index].costGlAccountId + '</div>';
				$("#costAccDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#costAccDropDown").attr("data-value", localdata[index].costGlAccountId);
				
				var dropDownContent = '<div class="innerDropdownContent">' + localdata[index].creditGlAccountId + '</div>';
				$("#creditAccDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#creditAccDropDown").attr("data-value", localdata[index].creditGlAccountId);
				
				var dropDownContent = '<div class="innerDropdownContent">' + localdata[index].debitGlAccountId + '</div>';
				$("#debitAccDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#debitAccDropDown").attr("data-value", localdata[index].debitGlAccountId);
			}
		});
		$('#allocatedPercentItemPty').on('valueChanged', function (event){
		    var value = event.args.value;
		    var totalAmount = $('#allocatedAmountItemPty').val();
		    var amount = value/100 * totalAmount;
		    $('#amountItemPty').val(amount.toFixed(2));
		}); 
		$('#amountItemPty').on('valueChanged', function (event){
			var value = event.args.value;
			var totalAmount = $('#allocatedAmountItemPty').val();
			var percent = value/totalAmount * 100;
			$('#allocatedPercentItemPty').val(percent.toFixed(2));
		}); 
		$("#cancelAddEquipAllocateItemPty").click(function(e){
			$("#newEquipAllocItemPtyWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipAllocateItemPty").click(function(e){
			var valid = $("#newEquipAllocItemPtyWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipmentAllocItemParty(false);
		});
		$("#saveAddEquipAllocateItemPty").click(function(e){
			var valid = $("#newEquipAllocItemPtyWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipmentAllocItemParty(true);
		});
	};
	var initValidator = function(){
		$("#newEquipAllocItemPtyWindow").jqxValidator({
			rules: [
				{ input: '#equipmentListItemPty', message: uiLabelMap.FieldRequired, action: 'none', 
					rule: function (input, commit) {
						if($(input).val()){
				    	   return true;
						}
						return false;
					}
				},
				{ input: '#partyDropDown', message: uiLabelMap.FieldRequired, action: 'none', 
					rule: function (input, commit) {
						var orgUseType = $("#orgUseTypeDropDown").val(); 
						if(orgUseType === "internalOrganization" && !input.val()){
				    	   return false;
						}
						return true;
					}
				},
				{ input: '#productStoreDropDown', message: uiLabelMap.FieldRequired, action: 'none', 
					rule: function (input, commit) {
						var orgUseType = $("#orgUseTypeDropDown").val(); 
						if(orgUseType === "productStore" && !input.val()){
							return false;
						}
						return true;
					}
				},
				{ input: '#amountItemPty', message: uiLabelMap.ValueMustBeLessThanEqualTotalAmount, action: 'keyup, change', 
					rule: function (input, commit) {
						var totalAmount = $("#allocatedAmountItemPty").val();
						if(input.val() > totalAmount){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	
	var addEquipmentAllocItemParty = function(isCloseWindow){
		var rowData = {};
		var equipmentSelected = $("#equipmentListItemPty").jqxDropDownList('getSelectedItem'); 
		rowData.equipmentId = equipmentSelected.value;
		rowData.equipmentName = equipmentSelected.label;
		var orgUseType = $("#orgUseTypeDropDown").val(); 
		if(orgUseType == "internalOrganization"){
			rowData.partyId = $("#partyDropDown").attr('data-value');
			rowData.orgUseName = $("#partyDropDown").attr('data-label');
		}else if(orgUseType == "productStore"){
			rowData.productStoreId = $("#productStoreDropDown").attr('data-value');
			rowData.orgUseName = $("#productStoreDropDown").attr('data-label');
		}
		rowData.allocatedAmount = $("#allocatedAmountItemPty").val();
		rowData.allocatedPercent = $("#allocatedPercentItemPty").val();
		rowData.amount = $("#amountItemPty").val();
		rowData.costGlAccountId = $("#costAccDropDown").attr('data-value');
		rowData.creditGlAccountId = $("#creditAccDropDown").attr('data-value');
		rowData.debitGlAccountId = $("#debitAccDropDown").attr('data-value');
		if($("#editEquipAllocateWindow").jqxWindow('isOpen')){
			var grid = $("#equipAllocItemPartyGrid");
		}else if($("#updateEquipmentAllocateWindow").jqxWindow('isOpen')){
			var grid = $("#updateEquipAllocItemPartyGrid");
		}
		if(_isEdit){
			var boundIndex = grid.jqxGrid('getselectedrowindex');
			var rowid = grid.jqxGrid('getrowid', boundIndex);
			grid.jqxGrid('updaterow', rowid, rowData);
			$("#newEquipAllocItemPtyWindow").jqxWindow('close');
		}else{
			var rows = grid.jqxGrid('getrows');
			var rowid = -1;
			for(var i = 0; i < rows.length; i++){
				var row = rows[i];
				if(typeof(rowData.partyId) != 'undefined'){
					if(row.equipmentId == rowData.equipmentId && row.partyId == rowData.partyId){
						rowid = row.uid;
						break;
					}
				}else if(typeof(rowData.productStoreId) != 'undefined'){
					if(row.equipmentId == rowData.equipmentId && row.productStoreId == rowData.productStoreId){
						rowid = row.uid;
						break;
					}
				}
			}
			if(rowid > -1){
				bootbox.dialog(uiLabelMap.EquipmentIsAllocatedForParty_updateConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 grid.jqxGrid('updaterow', rowid, rowData);
								 if(isCloseWindow){
									 $("#newEquipAllocItemPtyWindow").jqxWindow('close');
								 }else{
									 resetData();
								 }
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}else{
				grid.jqxGrid('addrow', null, rowData, 'first');
				if(isCloseWindow){
					$("#newEquipAllocItemPtyWindow").jqxWindow('close');
				}else{
					resetData();
				}
			}
		}
	};
	
	var resetData = function(){
		Grid.clearForm($("#newEquipAllocItemPtyWindow"));
		$("#orgUseTypeDropDown").jqxDropDownList('clearSelection');
		$("#orgUseTypeDropDown").jqxDropDownList({disabled: false});
		$("#partyTree").jqxTree('selectItem', null);
		$('#partyTree').jqxTree('collapseAll');
		$("#partyDropDown").jqxDropDownButton('setContent', "");

		$("#costAccGrid").jqxGrid('clearselection');
		$('#costAccGrid').jqxGrid('clearfilters');
		$('#costAccGrid').jqxGrid('gotopage', 0);
		$("#costAccDropDown").jqxDropDownButton('setContent', "");
		
		$("#creditAccGrid").jqxGrid('clearselection');
		$('#creditAccGrid').jqxGrid('clearfilters');
		$('#creditAccGrid').jqxGrid('gotopage', 0);
		$("#creditAccDropDown").jqxDropDownButton('setContent', "");
		
		$("#debitAccGrid").jqxGrid('clearselection');
		$('#debitAccGrid').jqxGrid('clearfilters');
		$('#debitAccGrid').jqxGrid('gotopage', 0);
		$("#debitAccDropDown").jqxDropDownButton('setContent', "");
		
		
		$("#productStoreGrid").jqxGrid('clearselection');
		$('#productStoreGrid').jqxGrid('clearfilters');
		$('#productStoreGrid').jqxGrid('gotopage', 0);
		$("#productStoreDropDown").jqxDropDownButton('setContent', "");
		_data = {};
		_isEdit = false;
		$("#newEquipAllocItemPtyWindow").jqxValidator('hide');
	};
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#newEquipAllocItemPtyWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
	}
}());