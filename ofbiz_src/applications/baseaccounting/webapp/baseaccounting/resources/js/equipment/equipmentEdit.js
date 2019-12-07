var editEquipmentObj = (function(){
	var _isEdit = false;
	var _data = {}; 
	var init = function(){
		initInput();
		initDropDown();
		initPartyGrid();
		initStoreGrid();
		initContextMenu();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#equipmentId").jqxInput({width: '98%', height: 22});
		$("#equipmentName").jqxInput({width: '95%', height: 22});
		$("#quantityUom").jqxInput({width: '95%', height: 22});
		$('#quantity').jqxNumberInput({min: 0, width: '97%', spinButtons: true, decimalDigits: 0});
		$('#unitPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '70%', spinButtons: true});
		$('#totalPrice').jqxNumberInput({digits: 12, max: 999999999999, min: 0, width: '97%', spinButtons: true, disabled: true});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#equipmentTypeId"), globalVar.equipmentTypeArr, {valueMember: 'equipmentTypeId', displayMember: 'description', width: '97%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#currencyUomId"), globalVar.currencyUomArr, {valueMember: 'uomId', displayMember: 'abbreviation', width: '25%', height: 25});
	};
	
	var initPartyGrid = function(){
		var grid = $("#equipmentPartyGrid");
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'fromDate', type: 'date', other: 'Timestamp'},
		         		 {name: 'thruDate', type: 'date', other: 'Timestamp'}
		                 ];
		
		var columns = [
		               {text: uiLabelMap.BACCOrganization, datafield: 'groupName', width: 400},
		               {text: uiLabelMap.BACCQuantity, datafield: 'quantity', columntype: 'numberinput', width: 80,
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + value + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCFromDate, datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false},
		       		   {text: uiLabelMap.BACCThruDate, datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false},
		               ];
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "equipmentPartyGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.CommonList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newEquipmentPartyWindow")});
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
					id: 'partyId'
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	    Grid.createContextMenu(grid, $("#contextMenuEquipmentParty"), false);
	};
	
	var initStoreGrid = function(){
		var grid = $("#equipmentStoreGrid");
		var datafield = [{name: 'productStoreId', type: 'string'},
		                 {name: 'storeName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'fromDate', type: 'date', other: 'Timestamp'},
		         		 {name: 'thruDate', type: 'date', other: 'Timestamp'}
		                 ];
		var columns = [
		               {text: uiLabelMap.BSProductStoreId, datafield: 'productStoreId', width: 100},
		               {text: uiLabelMap.BSStoreName, datafield: 'storeName', width: 300},
		               {text: uiLabelMap.BACCQuantity, datafield: 'quantity', columntype: 'numberinput', width: 80,
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + value + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCFromDate, datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false},
		       		   {text: uiLabelMap.BACCThruDate, datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false},
		               ];
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "equipmentStoreGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.CommonList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newEquipmentStoreWindow")});
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
					id: 'productStoreId'
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	    Grid.createContextMenu(grid, $("#contextMenuEquipmentStore"), false);
	};
	
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewEquipmentWindow"), 800, 590);
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenuEquipmentParty", 30, 130, {popupZIndex: 22000});
		$("#contextMenuEquipmentParty").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#equipmentPartyGrid").jqxGrid('getselectedrowindex');
			var data = $("#equipmentPartyGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "delete"){
				$("#equipmentPartyGrid").jqxGrid('deleterow', data.uid);
			}else{
				equipmentPartyObj.openWindow(data);
			}
		});
		
		accutils.createJqxMenu("contextMenuEquipmentStore", 30, 130, {popupZIndex: 22000});
		$("#contextMenuEquipmentStore").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#equipmentStoreGrid").jqxGrid('getselectedrowindex');
			var data = $("#equipmentStoreGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "delete"){
				$("#equipmentStoreGrid").jqxGrid('deleterow', data.uid);
			}else if(action == "edit"){
				equipmentStoreObj.openWindow(data);
			}
		});
	};
	var initEvent = function(){
		$("#addNewEquipmentWindow").on('open', function(e){
			if(_isEdit){
				$("#saveAndContinueAddEquipment").hide();
				$("#autoGenerateIdBtn").addClass("not-active");
				$("#addNewEquipmentWindow").jqxWindow('setTitle', uiLabelMap.CommonEdit);
				$("#equipmentId").jqxInput({disabled: true});
				getEquipmentParty(_data.equipmentId);
			} else {
				$("#autoGenerateIdBtn").removeClass("not-active");
				if(typeof(globalVar.defaultCurrencyUomId) != "undefined"){
					$("#currencyUomId").val(globalVar.defaultCurrencyUomId);
				}
				$("#saveAndContinueAddEquipment").show();
				$("#addNewEquipmentWindow").jqxWindow('setTitle', uiLabelMap.BACCCreateNew);
				$("#equipmentId").jqxInput({disabled: false});
			}
		});
		$("#addNewEquipmentWindow").on('close', function(e){
			resetData();
		});
		$('#quantity').on('valueChanged', function(event){
			var quantity = event.args.value;
			var unitPrice = $('#unitPrice').val();
			$('#totalPrice').val(quantity * unitPrice);
		});
		$('#unitPrice').on('valueChanged', function(event){
			var unitPrice = event.args.value;
			var quantity = $('#quantity').val();
			$('#totalPrice').val(quantity * unitPrice);
		});
		$("#cancelAddEquipment").click(function(e){
			$("#addNewEquipmentWindow").jqxWindow('close');
		});
		$("#saveAddEquipment").click(function(e){
			var valid = $("#addNewEquipmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var equipmentPartyQty = equipmentPartyObj.getEquipmentQuantity();
			var equipmentStoreQty = equipmentStoreObj.getEquipmentQuantity();
			var equipmentPartnerQty = equipmentPartnerObj.getEquipmentQuantity();
			var quantity = $("#quantity").val();
			if(equipmentPartyQty + equipmentStoreQty + equipmentPartnerQty == 0){
				bootbox.dialog(uiLabelMap.EquipmentPartyIsEmpty,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
				return;
			}
			if(equipmentPartyQty + equipmentStoreQty + equipmentPartnerQty != parseInt(quantity)){
				bootbox.dialog(uiLabelMap.EquipmentQtyMustBeEqualOrgUsedQty,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
				return;
			}
			if(!_isEdit){
				bootbox.dialog(uiLabelMap.CreateEquipmentConfirm,
					[{
						 "label" : uiLabelMap.CommonSubmit,
						 "class" : "btn-primary btn-small icon-ok open-sans",
						 "callback": function() {
							 editEquipment(true);
						 }
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
			} else {
				editEquipment(true);
			}
		});
		$("#saveAndContinueAddEquipment").click(function(e){
			var valid = $("#addNewEquipmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var equipmentPartyQty = equipmentPartyObj.getEquipmentQuantity();
			var equipmentStoreQty = equipmentStoreObj.getEquipmentQuantity();
			var equipmentPartnerQty = equipmentPartnerObj.getEquipmentQuantity();
			var quantity = $("#quantity").val();
			if(equipmentPartyQty + equipmentStoreQty + equipmentPartnerQty == 0){
				bootbox.dialog(uiLabelMap.EquipmentPartyIsEmpty,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
				return;
			}
			if(equipmentPartyQty + equipmentStoreQty + equipmentPartnerQty != parseInt(quantity)){
				bootbox.dialog(uiLabelMap.EquipmentQtyMustBeEqualOrgUsedQty,
					[{
						 "label" : uiLabelMap.CommonClose,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
				return;
			}
			bootbox.dialog(uiLabelMap.CreateEquipmentConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						editEquipment(false);
					}
				},
				 {
					 "label" : uiLabelMap.CommonCancel,
					 "class" : "btn-danger btn-small icon-remove open-sans",
				}]		
			);
		});
		$("#addEquipmentTypeBtn").click(function(e){
			createEquipmentTypeObj.openWindow();// createEquipmentTypeObj is defined in equipmentEdit.js
		});
		$("#equipmentStoreLi").click(function(e){
			$("#equipmentStoreGrid").jqxGrid('refresh');
		});
		$("#equipmentPartyLi").click(function(e){
			$("#equipmentPartyGrid").jqxGrid('refresh');
		});
		$("#autoGenerateIdBtn").click(function(e){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getEquipmentIdAutoGenerate',
				success: function(response) {
					  if(response._ERROR_MESSAGE_){
						  bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						  );		
					  } else {
						  $("#equipmentId").val(response.equipmentId);
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
	};
	var getData = function(){
		var data = {};
		data.equipmentId = $("#equipmentId").val();
		data.equipmentName = $("#equipmentName").val();
		data.quantity = $("#quantity").val();
		data.unitPrice = $("#unitPrice").val();
		data.equipmentTypeId = $("#equipmentTypeId").val();
		data.currencyUomId = $("#currencyUomId").val();
		data.description = $("#description").val();
		if($("#quantityUom").val()){
			data.quantityUom = $("#quantityUom").val(); 
		}
		var rows = $("#equipmentPartyGrid").jqxGrid('getrows');
		var rowsStore = $("#equipmentStoreGrid").jqxGrid('getrows');
		var rowsPartner = $("#equipmentFranchisingGrid").jqxGrid('getrows');
		var equipmentPartyArr = [];
		var equipmentStoreArr = [];
		var equipmentPartneryArr = [];
		
		rowsPartner.forEach(function(row){
			var tempData = {};
			tempData.partyId = row.partyId;
			tempData.quantity = row.quantity;
			tempData.fromDate = row.fromDate.getTime();
			if (row.thruDate) {
				tempData.thruDate = row.thruDate.getTime();
			}
			equipmentPartneryArr.push(tempData);
		});
		
		rows.forEach(function(row){
			var tempData = {};
			tempData.partyId = row.partyId;
			tempData.quantity = row.quantity;
			tempData.fromDate = row.fromDate.getTime();
			if (row.thruDate) {
				tempData.thruDate = row.thruDate.getTime();
			}
			equipmentPartyArr.push(tempData);
		});
		rowsStore.forEach(function(row){
			var tempData = {};
			tempData.productStoreId = row.productStoreId;
			tempData.quantity = row.quantity;
			tempData.fromDate = row.fromDate.getTime();
			if (row.thruDate) {
				tempData.thruDate = row.thruDate.getTime();
			}
			equipmentStoreArr.push(tempData);
		});
		if(equipmentPartyArr.length > 0){
			data.equipmentPartyItem = JSON.stringify(equipmentPartyArr);
		}
		if(equipmentStoreArr.length > 0){
			data.equipmentStoreItem = JSON.stringify(equipmentStoreArr);
		}
		
		if(equipmentPartneryArr.length > 0){
			data.equipmentPartnerItem = JSON.stringify(equipmentPartneryArr);
		}
		return data;
	};
	var editEquipment = function(isCloseWindow){
		var data = getData();
		Loading.show('loadingMacro');
		var url = '';
		if (_isEdit) {
			url = 'updateEquipment';
			data.equipmentId = _data.equipmentId;
		} else {
			url = 'createEquipment';
		}
		$.ajax({
			url: url,
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
				$("#jqxgrid").jqxGrid('updatebounddata');
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				if (isCloseWindow) {
					$("#addNewEquipmentWindow").jqxWindow('close');
				} else {
					resetData();
				}
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var resetData = function(){
		Grid.clearForm($("#addNewEquipmentWindow form"));
		updateGridLocalData($("#equipmentPartyGrid"), []);
		updateGridLocalData($("#equipmentStoreGrid"), []);
		updateGridLocalData($("#equipmentFranchisingGrid"), []);
		$("#equipmentFranchisingGrid").jqxGrid('clearselection');
		$("#equipmentPartyGrid").jqxGrid('clearselection');
		$("#equipmentStoreGrid").jqxGrid('clearselection');
		$("#equipmentPartyLi").removeClass("active");
		$("#equipmentPartyTab").removeClass("active");
		$("#equipmentFranchising").removeClass("active");
		$("#equipmentFranchisingTab").removeClass("active");
		$("#equipmentStoreLi").addClass("active");
		$("#equipmentStoreTab").addClass("active");
		_isEdit = false;
		_data = {};
		enableEdit();
		$("#addNewEquipmentWindow").jqxValidator('hide');
	};
	
	var getEquipmentParty = function(equipmentId){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getEquipmentPartyAndStoreAndPostedInfo',
			type: "POST",
			data: {equipmentId: equipmentId},
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					  );		
				  } else {
					  if (response.isPosted) {
						  disableEdit();
					  } else {
						  enableEdit();
					  }
					  updateGridLocalData($("#equipmentPartyGrid"), response.equipmentPartyList);
					  updateGridLocalData($("#equipmentStoreGrid"), response.equipmentProductStoreList);
					  updateGridLocalData($("#equipmentFranchisingGrid"), response.equipmentPartnerList);
				  }
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var initValidator = function(){
		$("#addNewEquipmentWindow").jqxValidator({
			rules: [
				{ input: '#equipmentId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
						rule: function (input, commit) {
							if($(input).val()){
								return true;
							}
							return false;
					}
				},
				{ input: '#equipmentName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#equipmentTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#currencyUomId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				},
				{ input: '#quantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
    				rule: function (input, commit) {
    					if(input.val() <= 0){
    						return false;
    					}
    					return true;
    				}
    			},
			]
		});
	};
	
	var updateGridLocalData = function(grid, localdata){
		var source = grid.jqxGrid('source');
		source._source.localdata = localdata;
		grid.jqxGrid('source', source);
	};
	
	var disableEdit = function(){
		$("#quantity").jqxNumberInput({disabled: true});
		$("#unitPrice").jqxNumberInput({disabled: true});
		$("#currencyUomId").jqxDropDownList({disabled: true});
		$("#quantityUom").jqxInput({disabled: true});
		/*$("#toolbarequipmentPartyGrid").hide();
		$("#toolbarequipmentStoreGrid").hide();
		$("#contextMenuEquipmentParty").jqxMenu({disabled: true});
		$("#contextMenuEquipmentStore").jqxMenu({disabled: true});
		$("#equipmentEditNoteContainer").show();*/
	};
	var enableEdit = function(){
		$("#quantity").jqxNumberInput({disabled: false});
		$("#unitPrice").jqxNumberInput({disabled: false});
		$("#currencyUomId").jqxDropDownList({disabled: false});
		$("#quantityUom").jqxInput({disabled: false});
		/*$("#toolbarequipmentPartyGrid").show();
		$("#toolbarequipmentStoreGrid").show();
		$("#contextMenuEquipmentParty").jqxMenu({disabled: false});
		$("#contextMenuEquipmentStore").jqxMenu({disabled: false});
		$("#equipmentEditNoteContainer").hide();*/
	};
	
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		$("#equipmentId").val(data.equipmentId);
		$("#equipmentName").val(data.equipmentName);
		$("#equipmentTypeId").val(data.equipmentTypeId);
		$("#description").val(data.description);
		$("#quantity").val(data.quantity);
		$("#unitPrice").val(data.unitPrice);
		$("#currencyUomId").val(data.currencyUomId);
		$("#totalPrice").val(data.totalPrice);
		$("#quantityUom").val(data.quantityUom);
		accutils.openJqxWindow($("#addNewEquipmentWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

/**========================================================================================**/
var equipmentPartyObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initTreeDropDown();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$('#quantityUsed').jqxNumberInput({min: 0, width: '95%', spinButtons: true, decimalDigits: 0});
		$("#fromDateParty").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDateParty").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDateParty").jqxDateTimeInput("setDate", null);
	};
	var initTreeDropDown = function(){
		var config = {dropDownBtnWidth: '95%', treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#partyTree"), $("#partyDropDown"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipmentPartyWindow"), 450, 260);
	};
	var resetData = function(){
		Grid.clearForm($("#newEquipmentPartyWindow"));
		$("#partyTree").jqxTree('selectItem', null);
		$('#partyTree').jqxTree('collapseAll');
		_isEdit = false;
		_data = {};
	};
	var initEvent = function(){
		$("#newEquipmentPartyWindow").on('open', function(e){
			if(_isEdit){
				$("#partyDropDown").attr("data-value", _data.partyId);
				$("#partyDropDown").attr("data-label", _data.groupName);
				$("#partyDropDown").jqxDropDownButton({disabled: true});
				var dropDownContent = '<div class="innerDropdownContent">' + _data.groupName + '</div>';
				$("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#quantityUsed").val(_data.quantity);
				$("#fromDateParty").val(_data.fromDate);
				$("#thruDateParty").val(_data.thruDate);
				$("#saveAndContinueAddEquipParty").hide();
			} else {
				$("#partyDropDown").jqxDropDownButton({disabled: false});
				$("#saveAndContinueAddEquipParty").show();
				$("#fromDateParty").jqxDateTimeInput("setDate", new Date());
			}
		});
		$("#newEquipmentPartyWindow").on('close', function(e){
			resetData();
		});
		$("#cancelAddEquipParty").click(function(e){
			$("#newEquipmentPartyWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipParty").click(function(e){
			var valid = $("#newEquipmentPartyWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntParty();
			resetData();
			$("#fromDateParty").jqxDateTimeInput("setDate", new Date());
		});
		$("#saveAddEquipParty").click(function(e){
			var valid = $("#newEquipmentPartyWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntParty();
			$("#newEquipmentPartyWindow").jqxWindow('close');
		});
		$("#partyTree").on('select', function(event){
			var item = $('#partyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
	        $("#partyDropDown").jqxDropDownButton('close');
	        accutils.setAttrDataValue('partyDropDown', item.value);
	        $("#partyDropDown").attr('data-label', item.label)
		});
	};
	var initValidator = function(){
		$("#newEquipmentPartyWindow").jqxValidator({
			rules: [
				{ input: '#partyDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#quantityUsed', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
    				rule: function (input, commit) {
    					if(input.val() <= 0){
    						return false;
    					}
    					return true;
    				}
    			},
    			{ input: '#fromDateParty', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#thruDateParty', message: uiLabelMap.BACCThruDateValidate, action: 'keyup, change', 
					rule: function (input, commit) {
						var fromDate = $('#fromDateParty').jqxDateTimeInput('getDate'); 
						var thruDate = $('#thruDateParty').jqxDateTimeInput('getDate'); 
						if (thruDate) {
							if(fromDate.getTime() > thruDate.getTime()){
								return false;
							}
							return true
						}
						return true;
					}
				}
			]
		});
	};
	var addEquipemntParty = function(){
		var row = {};
		row.partyId = $("#partyDropDown").attr('data-value');
		row.groupName = $("#partyDropDown").attr('data-label');
		row.quantity = $("#quantityUsed").val();
		row.fromDate = $('#fromDateParty').jqxDateTimeInput('getDate'); 
		row.thruDate = $('#thruDateParty').jqxDateTimeInput('getDate');
		var checkRowExists = $("#equipmentPartyGrid").jqxGrid('getrowboundindexbyid', row.partyId);
		if(checkRowExists > -1){
			$('#equipmentPartyGrid').jqxGrid('updaterow', row.partyId, row);
		} else {
			$("#equipmentPartyGrid").jqxGrid('addrow', null, row, 'first');
		}
	};
	var getEquipmentQuantity = function(){
		var rows = $('#equipmentPartyGrid').jqxGrid('getrows');
		var quantity = 0;
		rows.forEach(function(row){
			quantity += row.quantity;
		});
		return quantity;
	};
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#newEquipmentPartyWindow"));
	};
	return{
		init: init,
		getEquipmentQuantity: getEquipmentQuantity,
		openWindow: openWindow
	}
}());

/**========================================================================================**/
var equipmentStoreObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initInput();
		initDropDownGrid();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$('#quantityStoreUsed').jqxNumberInput({min: 0, width: '95%', spinButtons: true, decimalDigits: 0});
		$("#fromDate").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDate").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDate").jqxDateTimeInput("setDate", null);
	};
	var initDropDownGrid = function(){
		$("#productStoreDropDown").jqxDropDownButton({width: '95%', height: 25});
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
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipmentStoreWindow"), 450, 260);
	};
	var initEvent = function(){
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
		$("#newEquipmentStoreWindow").on('close', function(e){
			resetData();
		});
		$("#newEquipmentStoreWindow").on('open', function(e){
			if(_isEdit){
				$("#productStoreDropDown").attr("data-value", _data.productStoreId);
				$("#productStoreDropDown").attr("data-label", _data.storeName);
				$("#productStoreDropDown").jqxDropDownButton({disabled: true});
				var dropDownContent = '<div class="innerDropdownContent">' + _data.storeName + ' [' + _data.productStoreId + ']</div>';
				$("#productStoreDropDown").jqxDropDownButton('setContent', dropDownContent);
				$("#quantityStoreUsed").val(_data.quantity);
				$("#fromDate").val(_data.fromDate);
				$("#thruDate").val(_data.thruDate);
				$("#saveAndContinueAddEquipStore").hide();
			} else {
				$("#productStoreDropDown").jqxDropDownButton({disabled: false});
				$("#saveAndContinueAddEquipStore").show();
				$("#fromDate").jqxDateTimeInput("setDate", new Date());
			}
		});
		$("#cancelAddEquipStore").click(function(e){
			$("#newEquipmentStoreWindow").jqxWindow('close');
		});
		$("#saveAndContinueAddEquipStore").click(function(e){
			var valid = $("#newEquipmentStoreWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntStore();
			resetData();
			$("#fromDate").jqxDateTimeInput("setDate", new Date());
		});
		$("#saveAddEquipStore").click(function(e){
			var valid = $("#newEquipmentStoreWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntStore();
			$("#newEquipmentStoreWindow").jqxWindow('close');
		});
	};
	var initValidator = function(){
		$("#newEquipmentStoreWindow").jqxValidator({
			rules: [
				{ input: '#productStoreDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#quantityStoreUsed', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
    				rule: function (input, commit) {
    					if(input.val() <= 0){
    						return false;
    					}
    					return true;
    				}
    			},
    			{ input: '#fromDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#thruDate', message: uiLabelMap.BACCThruDateValidate, action: 'keyup, change', 
					rule: function (input, commit) {
						var fromDate = $('#fromDate').jqxDateTimeInput('getDate'); 
						var thruDate = $('#thruDate').jqxDateTimeInput('getDate'); 
						if (thruDate) {
							if(fromDate.getTime() > thruDate.getTime()){
								return false;
							}
							return true
						}
						return true;
					}
				}
			]
		});
	};
	var resetData = function(){
		Grid.clearForm($("#newEquipmentStoreWindow"));
		$("#productStoreGrid").jqxGrid('clearselection');
		$("#productStoreGrid").jqxGrid('gotopage', 0);
		_isEdit = false;
		_data = {};
	};
	var addEquipemntStore = function(){
		var row = {};
		row.productStoreId = $("#productStoreDropDown").attr('data-value');
		row.storeName = $("#productStoreDropDown").attr('data-label');
		row.quantity = $("#quantityStoreUsed").val();
		row.fromDate = $('#fromDate').jqxDateTimeInput('getDate'); 
		row.thruDate = $('#thruDate').jqxDateTimeInput('getDate'); 
		var checkRowExists = $("#equipmentStoreGrid").jqxGrid('getrowboundindexbyid', row.productStoreId);
		if(checkRowExists > -1){
			$('#equipmentStoreGrid').jqxGrid('updaterow', row.productStoreId, row);
		} else {
			$("#equipmentStoreGrid").jqxGrid('addrow', null, row, 'first');
		}
	};
	var getEquipmentQuantity = function(){
		var rows = $('#equipmentStoreGrid').jqxGrid('getrows');
		var quantity = 0;
		rows.forEach(function(row){
			quantity += row.quantity;
		});
		return quantity;
	};
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#newEquipmentStoreWindow"));
	};
	return{
		init: init,
		getEquipmentQuantity: getEquipmentQuantity,
		openWindow: openWindow
	}
}());

/*======================================================================================================*/

var equipmentPartnerObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function() {
		initPartnerGrid();
		initWindow();
		initPartnerContextMenu();
		initInput();
		initEvent();
		initValidator();
	};
	
	var initPartnerGrid = function(){
		var grid = $("#equipmentFranchisingGrid");
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'groupName', type: 'string'},
		                 {name: 'quantity', type: 'number'},
		                 {name: 'fromDate', type: 'date', other: 'Timestamp'},
		         		 {name: 'thruDate', type: 'date', other: 'Timestamp'}
		                 ];
		
		var columns = [
		               {text: uiLabelMap.BACCFranchising, datafield: 'groupName', width: 400},
		               {text: uiLabelMap.BACCQuantity, datafield: 'quantity', columntype: 'numberinput', width: 80,
		            	   cellsrenderer: function(row, columns, value){
		            		   if(typeof(value) == 'number'){
		                			return '<span style="text-align: right">' + value + '</span>';
		                		}
		                		return '<span>' + value + '</span>';
		            	   }
		               },
		               {text: uiLabelMap.BACCFromDate, datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false},
		       		   {text: uiLabelMap.BACCThruDate, datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm:ss', editable: false}
		               ];
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "equipmentFranchisingGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.CommonList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#newEquipmentPartnerWindow")});
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
					id: 'partyId'
				}
		};
	    Grid.initGrid(config, datafield, columns, null, grid);
	    Grid.createContextMenu(grid, $("#contextMenuPartner"), false);
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newEquipmentPartnerWindow"), 450, 260);
	}
	var initInput = function(){
		accutils.createJqxDropDownList($("#partnerDropDown"), globalVar.listOrgPartner, {valueMember: 'partyId', displayMember: 'partyName', width: '95%', height: 25, placeHolder: uiLabelMap.filterchoosestring});
		$('#partnerUsed').jqxNumberInput({min: 0, width: '95%', spinButtons: true, decimalDigits: 0});
		$("#fromDatePartner").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDatePartner").jqxDateTimeInput({ theme: theme, width: '95%', formatString: 'dd/MM/yyyy HH:mm:ss', height: 25 });
		$("#thruDatePartner").jqxDateTimeInput("setDate", null);
	};
	
	var initPartnerContextMenu = function(){
		accutils.createJqxMenu("contextMenuPartner", 30, 130, {popupZIndex: 22000});
		$("#contextMenuPartner").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#equipmentFranchisingGrid").jqxGrid('getselectedrowindex');
			var data = $("#equipmentFranchisingGrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "delete"){
				$("#equipmentFranchisingGrid").jqxGrid('deleterow', data.uid);
			} else if(action == "edit"){
				equipmentPartnerObj.openWindow(data);
			}
		});
	};
	
	var initEvent = function(){
		$("#newEquipmentPartnerWindow").on('open', function(event){
			var source = $("#partnerDropDown").jqxDropDownList('source');
			if(_isEdit){
				var local = [
					{
						partyId: _data.partyId,
						partyName: _data.groupName
					}
				];
				var source = $("#partnerDropDown").jqxDropDownList('source');
				source._source.localdata = local;
				$("#partnerDropDown").val(_data.partyId);
				$("#partnerDropDown").jqxDropDownList({disabled: true});
				$("#partnerUsed").val(_data.quantity);
				$("#fromDatePartner").val(_data.fromDate);
				$("#thruDatePartner").val(_data.thruDate);
				$("#saveAndContinueAddEquipPartner").hide();
			} else {
				$("#partnerDropDown").jqxDropDownList({disabled: false});
				$("#saveAndContinueAddEquipPartner").show();
				$("#fromDatePartner").jqxDateTimeInput("setDate", new Date());
			}
			
		});
		
		$("#newEquipmentPartnerWindow").on('close', function(event){
			resetData();
		});
		
		$("#cancelAddEquipPartner").click(function(e){
			$("#newEquipmentPartnerWindow").jqxWindow('close');
		});
		
		$("#saveAndContinueAddEquipPartner").click(function(e){
			var valid = $("#newEquipmentPartnerWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntPartner();
			resetData();
			$("#fromDatePartner").jqxDateTimeInput("setDate", new Date());
		});
		$("#saveAddEquipPartner").click(function(e){
			var valid = $("#newEquipmentPartnerWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addEquipemntPartner();
			$("#newEquipmentPartnerWindow").jqxWindow('close');
		});
		
	};
	
	var initValidator = function(){
		$("#newEquipmentPartnerWindow").jqxValidator({
			rules: [
				{ input: '#partnerDropDown', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#partnerUsed', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
    				rule: function (input, commit) {
    					if(input.val() <= 0){
    						return false;
    					}
    					return true;
    				}
    			},
    			{ input: '#fromDatePartner', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if($(input).val()){
							return true;
						}
						return false;
					}
				}, 
				{ input: '#thruDatePartner', message: uiLabelMap.BACCThruDateValidate, action: 'keyup, change', 
					rule: function (input, commit) {
						var fromDate = $('#fromDatePartner').jqxDateTimeInput('getDate'); 
						var thruDate = $('#thruDatePartner').jqxDateTimeInput('getDate'); 
						if (thruDate) {
							if(fromDate.getTime() > thruDate.getTime()){
								return false;
							}
							return true
						}
						return true;
					}
				}
			]
		});
	};
	
	var addEquipemntPartner = function(){
		var row = {};
		var selectIndex = $("#partnerDropDown").jqxDropDownList('selectedIndex');
		var item = $("#partnerDropDown").jqxDropDownList('getItem', selectIndex); 
		row.partyId = item.value;
		row.groupName = item.label;
		row.quantity = $("#partnerUsed").val();
		row.fromDate = $('#fromDatePartner').jqxDateTimeInput('getDate'); 
		row.thruDate = $('#thruDatePartner').jqxDateTimeInput('getDate');
		var checkRowExists = $("#equipmentFranchisingGrid").jqxGrid('getrowboundindexbyid', row.partyId);
		if(checkRowExists > -1){
			$('#equipmentFranchisingGrid').jqxGrid('updaterow', row.partyId, row);
		} else {
			$("#equipmentFranchisingGrid").jqxGrid('addrow', null, row, 'first');
		}
	};
	
	var openWindow = function(data){
		_isEdit = true;
		_data = data;
		accutils.openJqxWindow($("#newEquipmentPartnerWindow"));
	};
	
	var resetData = function(){
		Grid.clearForm($("#newEquipmentPartnerWindow"));
		_isEdit = false;
		_data = {};
	};
	
	var getEquipmentQuantity = function(){
		var rows = $('#equipmentFranchisingGrid').jqxGrid('getrows');
		var quantity = 0;
		rows.forEach(function(row){
			quantity += row.quantity;
		});
		return quantity;
	};
	
	return {
		init : init,
		openWindow : openWindow,
		getEquipmentQuantity: getEquipmentQuantity
	}
}());

$(document).ready(function(){
	editEquipmentObj.init();
	equipmentPartyObj.init();
	equipmentStoreObj.init();
	equipmentPartnerObj.init();
});