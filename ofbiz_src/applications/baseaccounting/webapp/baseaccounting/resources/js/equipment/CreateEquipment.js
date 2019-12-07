var createEquipmentStep1Obj = (function(){
	var _thousandsseparator = ".";
	var _decimalseparator = ",";;
	var init = function(){
		initInput();
		initDropDown();
		initDropDownTree();
		initEvent();
		initData();
		initValidator();
	};
	var initInput = function(){
		if(globalVar.currencyUomId == "USD"){
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }else if(globalVar.currencyUomId == "EUR"){
	        _decimalseparator = ".";
	        _thousandsseparator = ",";
	    }
		$('#equipmentId').jqxInput({width: '94%', height: 25 });
		$('#equipmentName').jqxInput({width: '94%', height: 25});
		$('#allowTimes').jqxNumberInput({decimalDigits: 0, inputMode: 'simple', min: 0, digits: 12, width: '94%', spinButtons: true });
		$('#quantity').jqxNumberInput({decimalDigits: 0, inputMode: 'simple', min: 0, digits: 12, width: '94%', spinButtons: true });
		$('#unitPrice').jqxNumberInput({digits: 12, max: 99999999999, min: 0, decimalDigits: 2,
			decimalSeparator: _decimalseparator, groupSeparator: _thousandsseparator, width: '100%', spinButtons: true });
		$('#dateAcquired').jqxDateTimeInput({width: '94%', height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#currencyUomId"), globalVar.uomCurrencyArr, {valueMember: 'uomId', displayMember: 'abbreviation', width: '82%', height: 25});
		accutils.createJqxDropDownList($("#quantityUomId"), globalVar.uomProdPackArr, {valueMember: 'uomId', displayMember: 'description', width: '94%', height: 25});
		accutils.createJqxDropDownList($("#equipmentTypeId"), globalVar.equipmentTypeArr, {valueMember: 'equipmentTypeId', displayMember: 'description', width: '94%', height: 25});
		accutils.createJqxDropDownList($("#roleTypeId"), [], {valueMember: 'roleTypeId', displayMember: 'description', width: '94%', height: 25});
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: '94%', treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#wn_partyTree"), $("#wn_partyId"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	var initEvent = function(){
		$("#wn_partyTree").on('select', function(event){
			var item = $('#wn_partyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#wn_partyId").jqxDropDownButton('setContent', dropDownContent);
	        $("#wn_partyId").jqxDropDownButton('close');
	        accutils.setAttrDataValue('wn_partyId', item.value);
		});
	};
	var initData = function(){
		if(globalVar.rootPartyArr.length > 0){
			$("#wn_partyTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		$("#currencyUomId").on('select', function(event){
			var args = event.args;
			if (args) {
				var item = args.item;
				var currencyUomId = item.value;
				if(currencyUomId == "USD"){
					_decimalseparator = ",";
				    _thousandsseparator = ".";
				}else if(currencyUomId == "USD"){
			        _decimalseparator = ".";
			        _thousandsseparator = ",";
			    }else if(currencyUomId == "EUR"){
			        _decimalseparator = ".";
			        _thousandsseparator = ",";
			    }
				$('#unitPrice').jqxNumberInput({decimalSeparator: _decimalseparator, groupSeparator: _thousandsseparator});
			}
		});
		if(globalVar.defaultCurrencyUomId){
			$("#currencyUomId").val(globalVar.defaultCurrencyUomId);
		}
	};
	var initValidator = function(){
		$('#newEquipment').jqxValidator({
	        rules: [
	       			{ input: '#equipmentId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	       				rule: function (input, commit) {
	       					return accutils.validElement(input, commit, 'validInputNotNull');
	    				}
	    			},
	    			{ input: '#equipmentTypeId', message: uiLabelMap.FieldRequired, action: 'change, close', 
	    				rule: function (input, commit) {
	    					return accutils.validElement(input, commit, 'validInputNotNull');
	    				}
	    			},
	    			{ input: '#wn_partyId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#dateAcquired', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#quantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					if(input.val() <= 0){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},
	    			{ input: '#allowTimes', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	                       if(input.val() <= 0){
	                    	   return false;
	                       }
	                       return true;
	    				}
	    			},	    			
	    			{ input: '#currencyUomId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					return accutils.validElement(input, commit, 'validInputNotNull');
	    				}
	    			},
	    			{ input: '#quantityUomId', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					return accutils.validElement(input, commit, 'validInputNotNull');
	    				}
	    			},
	    			{ input: '#equipmentName', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					return accutils.validElement(input, commit, 'validInputNotNull');
	    				}
	    			},
	    			{ input: '#unitPrice', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					if(input.val() <= 0){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},	    	
               ],
               position: 'bottom'
	    });
	};
	var validate = function(){
		return $('#newEquipment').jqxValidator('validate');
	};
	var getData = function(){
		var data = {};
		data.equipmentId = $("#equipmentId").val();
		data.equipmentTypeId = $("#equipmentTypeId").val();
		data.equipmentName = $("#equipmentName").val();
		data.allowTimes = $("#allowTimes").val();
		data.quantity = $('#quantity').val();
		data.unitPrice = $('#unitPrice').val();
		data.currencyUomId = $('#currencyUomId').val();
		data.quantityUomId = $('#quantityUomId').val();
		if($('#roleTypeId').val()){
			data.roleTypeId = $('#roleTypeId').val();
		}
		data.dateAcquired = $("#dateAcquired").jqxDateTimeInput('val', 'date').getTime();
		data.partyId = $("#wn_partyId").attr('data-value');
		return data;
	};
	return{
		init: init,
		validate: validate,
		getData: getData
	}
}());

/**===========================================================================**/

var createEquipmentStep2Obj = (function(){
	var init = function(){
		initGrid();
		initInput();
		initDropDownTree();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initGrid = function(){
		var grid = $("#newAllocGrid");
		var datafield = [{name: 'allocPartyId', type: 'string'},
                         {name: 'allocPartyCode', type: 'string'},
                         {name: 'allocPartyName', type: 'string'},
                         {name: 'allocRate', type: 'number'},
                         {name: 'allocGlAccountId', type: 'string'},
                         {name: 'allocGlAccountName', type: 'string'},
                         ];
		var columns = [{text: uiLabelMap.BACCSeqId, ortable: false, filterable: false, editable: false,
					        resizable: false, datafield: '', columntype: 'number', width: '5%',
					        cellsrenderer: function (row, column, value) {
					            return "<div style='margin:4px;'>" + (value + 1) + "</div>";
					        }
						},
						{text: uiLabelMap.BACCAllocPartyCode, datafield: 'allocPartyCode', width: '16%'}, 
						{text: uiLabelMap.BACCAllocPartyName, filterable : false, datafield: 'allocPartyName', width: '35%',},
						{text: uiLabelMap.BACCAllocRate, filterable: false, datafield: 'allocRate', columntype: 'numberinput', 
							width: '15%', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								return "<span style='text-align: right;'>" + value + "%</span>";
							}
						},
						{text: uiLabelMap.BACCAllocGlAccoutId,  datafield: 'allocGlAccountName',}
		];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "newAllocGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + uiLabelMap.BACCAllocationSetting + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newAllocParty")});
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
					localdata: [],
					id: 'allocPartyId'
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initInput = function(){
		$("#allocRate").jqxNumberInput({digits: 3, inputMode: 'simple', decimalDigits: 0, spinButtons: true, width: '97%', symbolPosition: 'right', symbol: '%'});
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: '97%', treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#allocPartyTree"), $("#allocPartyDropDownBtn"), globalVar.rootPartyArr, "treeAlloc", "treeAllocChild", config);
	};
	var initDropDownGrid = function(){
		$("#allocGlAccountId").jqxDropDownButton({width: '97%', height: 25});
		var datafield =  [{name: 'glAccountId', type: 'string'}, 
		                  {name: 'accountName', type: 'string'}];
		var columns = [{text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountId', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}];
		var config = {
				url: '',
				showtoolbar : false,
				width : 550,
				autoheight: true,
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				sortable: true,
				filterable: true,
				pageable: true,
				source:{
					pagesize: 10,
				},
		};
		Grid.initGrid(config, datafield, columns, null, $("#allocGlAccountGrid"));
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newAllocParty"), 450, 250);
	};
	var initEvent = function(){
		$("#allocPartyTree").on('select', function(event){
			var item = $('#allocPartyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#allocPartyDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
	        $("#allocPartyDropDownBtn").jqxDropDownButton('close');
	        $("#allocPartyDropDownBtn").attr('label', item.label);
	        accutils.setAttrDataValue('allocPartyDropDownBtn', item.value);
	        for(var i = 0; i < globalVar.partyGlAccountData.length; i++){
	        	if(globalVar.partyGlAccountData[i].partyId == item.value){
	        		var label = globalVar.partyGlAccountData[i].accountName + ' [' + globalVar.partyGlAccountData[i].glAccountId + ']';
	        		accutils.setValueDropDownButtonOnly($("#allocGlAccountId"), globalVar.partyGlAccountData[i].glAccountId, label);
	        		$("#allocGlAccountId").attr('label', label);
	        		break;
	        	}
	        }
		});
		$("#allocGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#allocGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var label = data.accountName + ' [' + data.glAccountId + ']';
			var dropDownContent = '<div class="innerDropdownContent">' + label + '</div>';
	        $("#allocGlAccountId").jqxDropDownButton('setContent', dropDownContent);
	        $("#allocGlAccountId").jqxDropDownButton('close');
	        $("#allocGlAccountId").attr('label', label);
			accutils.setAttrDataValue('allocGlAccountId', data.glAccountId);
		});
		$("#newAllocParty").on('open', function(event){
			initOpen();
		});
		$("#newAllocParty").on('close', function(event){
			resetData();
		});
		$("#cancelAllocParty").click(function(event){
			$("#newAllocParty").jqxWindow('close');
		});
		$("#saveAndContinueAllocParty").click(function(event){
			var valid = $("#newAllocParty").jqxValidator('validate');
			if(!valid){
				return;
			}
			allocateParty();
			resetData();
			initOpen();
		});
		$("#saveAllocParty").click(function(event){
			var valid = $("#newAllocParty").jqxValidator('validate');
			if(!valid){
				return;
			}
			allocateParty();
			$("#newAllocParty").jqxWindow('close');
		});
	};
	
	var allocateParty = function(){
		var data = getNewRowGridData();
		var rowIndex = $("#newAllocGrid").jqxGrid('getrowboundindexbyid', data.allocPartyId);
		if(rowIndex > -1){
			$("#newAllocGrid").jqxGrid('updaterow', data.allocPartyId, data);
		}else{
			$("#newAllocGrid").jqxGrid('addrow', null, data, 'first');
		}
	};
	
	var getTotalAllocPercent = function(){
		var total = 0;
		var rows = $("#newAllocGrid").jqxGrid('getrows');
		rows.forEach(function(rowData){
			total += rowData.allocRate;
		});
		return total;
	};
	
	var initOpen = function(){
		var source = $("#allocGlAccountGrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JqxGetListGlAccounts";
		$("#allocGlAccountGrid").jqxGrid('source', source);
		var totalAllocRate = getTotalAllocPercent();
		var rate = 100 - totalAllocRate;
		if(rate > 0){
			$("#allocRate").val(rate);
		}else{
			$("#allocRate").val(0);
		}
	};
	
	var resetData = function(){
		$("#allocRate").val(0);
		$("#allocPartyTree").jqxTree('clearSelection');
		$("#allocGlAccountGrid").jqxGrid('clearselection');
		$("#allocGlAccountId").jqxDropDownButton('setContent', "");
		$("#allocPartyDropDownBtn").jqxDropDownButton('setContent', "");
		$("#allocGlAccountId").attr('label', "");
		$("#allocPartyDropDownBtn").attr('label', "");
		accutils.setAttrDataValue('allocPartyDropDownBtn', null);
		accutils.setAttrDataValue('allocGlAccountId', null);
	};
	
	var getNewRowGridData = function(){
		var data = {};
		data.allocPartyId = $("#allocPartyDropDownBtn").attr('data-value'); 
		data.allocPartyCode = $("#allocPartyDropDownBtn").attr('data-value');
		data.allocPartyName = $("#allocPartyDropDownBtn").attr('label');
		data.allocGlAccountId = $("#allocGlAccountId").attr('data-value');
		data.allocGlAccountName = $("#allocGlAccountId").attr('label');
		data.allocRate = $("#allocRate").val();
		return data;
	};
	var initValidator = function(){
		$("#newAllocParty").jqxValidator({
	        rules: [
	       			{ input: '#allocPartyDropDownBtn', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
	       				rule: function (input, commit) {
	       					if(!$(input).attr('data-value')){
	       						return false;
	       					}
	       					return true;
	    				}
	    			},
	    			{ input: '#allocGlAccountId', message: uiLabelMap.FieldRequired, action: 'change, close', 
	    				rule: function (input, commit) {
	    					if(!$(input).attr('data-value')){
	       						return false;
	       					}
	       					return true;
	    				}
	    			},
	    			{ input: '#allocRate', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					if(input.val() <= 0){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},	    	
	    			{ input: '#allocRate', message: uiLabelMap.ValueMustLessThanOneHundred, action: 'keyup, change, close', 
	    				rule: function (input, commit) {
	    					if(input.val() > 100){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},	    	
               ],
	    });
	};
	var getData = function(){
		var rows = $("#newAllocGrid").jqxGrid('getrows');
		return JSON.stringify(rows);
	};
	return {
		init: init,
		getTotalAllocPercent: getTotalAllocPercent,
		getData: getData
	}
}());

var createEquipmentObj = (function(){
	var init = function(){
		initWizard();
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
 				var valid = createEquipmentStep1Obj.validate();
 				if(!valid){
 					return false;
 				}
 			}
		}).on('finished', function(e) {
			var totalAllocRate = createEquipmentStep2Obj.getTotalAllocPercent();
			if(totalAllocRate != 100){
				bootbox.dialog(uiLabelMap.TotalAllocateForPartyMustEqualOneHundred,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);			
				return false;
			}
			bootbox.dialog(uiLabelMap.CreateEquipmentConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createEquipment();
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
	var createEquipment = function(){
		var data = createEquipmentStep1Obj.getData();
		data.listAllocs = createEquipmentStep2Obj.getData();
		Loading.show('loadingMacro');
		$.ajax({
			url: 'createEquipmentAndAlloc',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					Loading.hide('loadingMacro');
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				window.location.href = 'ListEquipments';
			}
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	$.jqx.theme = 'olbius';
	createEquipmentStep1Obj.init();
	createEquipmentStep2Obj.init();
	createEquipmentObj.init();
});