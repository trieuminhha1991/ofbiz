var fixedAssetEditGeneralInfoObj = (function(){
	var _inputTextWidth = '96%';
	var _otherInputWidth = '98%';
	var init = function(){
		initInput();
		initDropDown();
		initDropDownTree();
		initDropDownGrid();
		initProductStoreDropDown();
		initWindow();
		initEvent();
		initValidator();
		$("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 1, autoClose: true, template: "success" });
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#editFixedAssetInfoWindow"), 800, 480);
	};
	var initInput = function(){
		$("#fixedAssetId").jqxInput({width: _inputTextWidth, height: 22, disabled: true});
		$("#fixedAssetName").jqxInput({width: _inputTextWidth, height: 22});
		$("#warrantyPeriod").jqxInput({width: _inputTextWidth, height: 22});
		$("#countryOrigin").jqxInput({width: _inputTextWidth, height: 22});
		$("#warrantyCondition").jqxInput({width: _inputTextWidth, height: 22});
		$("#serialNumber").jqxInput({width: _inputTextWidth, height: 22});
		$("#yearMade").jqxNumberInput({ width: '95%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$("#quantity").jqxNumberInput({ width: '100%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
		$("#receiptNumber").jqxInput({width: _inputTextWidth, height: 22});
		$("#receiptDate").jqxDateTimeInput({width: '98%', height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#statusId"), globalVar.statusData, {valueMember: 'statusId', displayMember: 'description', width: _otherInputWidth, height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#currencyUomId"), globalVar.uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: _otherInputWidth, height: 25});
		accutils.createJqxDropDownList($("#fixedAssetTypeId"), globalVar.fixedAssetTypeData, {valueMember: 'fixedAssetTypeId', displayMember: 'description', width: _otherInputWidth, height: 25, filterable: true, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#orgUseTypeDropDown"), globalVar.orgUseTypeArr, {valueMember: 'type', displayMember: 'description', width: '97%', height: 25});
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: _otherInputWidth, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#partyTree"), $("#partyDropDown"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	var initDropDownGrid = function(){
		$("#supplierDropDown").jqxDropDownButton({width: _otherInputWidth, height: 25}); 
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
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
						if (!value) {
							var data = $("#jqxgridSupplier").jqxGrid('getrowdata', row);
							value = data.partyId;
						}
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
	        source: {pagesize: 5}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#supplierGrid"));
	};
	var initProductStoreDropDown = function(){
		$("#productStoreDropDown").jqxDropDownButton({width: '97%', height: 25, dropDownHorizontalAlignment: 'right'});
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
	var initEvent = function(){
		$("#editFixedAssetInfoBtn").click(function(e){
			accutils.openJqxWindow($("#editFixedAssetInfoWindow"));
		});
		$("#partyTree").on('select', function(event){
			var item = $('#partyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
	        $("#partyDropDown").jqxDropDownButton('close');
	        accutils.setAttrDataValue('partyDropDown', item.value);
		});
		$("#supplierGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#supplierGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.groupName + ' [' + rowData.partyCode + ']</div>';
			$("#supplierDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#supplierDropDown").attr("data-value", rowData.partyId);
			$("#supplierDropDown").jqxDropDownButton('close');
		});
		$("#editFixedAssetInfoWindow").on('open', function(e){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getFixedAssetGeneralInfo',
				type: "POST",
				data: {fixedAssetId: globalVar.fixedAssetId},
				success: function(response) {
					  if(response.responseMessage == "error"){
						  bootbox.dialog(response.errorMessage,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);
						  return;
					  }
					  setFixedAssetData(response.fixedAsset);
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#editFixedAssetInfoWindow").on('close', function(e){
			Grid.clearForm($('#editFixedAssetInfoWindow'));
			$('#editFixedAssetInfoWindow').jqxValidator('hide');
			$("#supplierGrid").jqxGrid('clearselection');
			$("#partyTree").jqxTree('selectItem', null)
		});
		$("#cancelEditFixedAssetInfo").click(function(e){
			$("#editFixedAssetInfoWindow").jqxWindow('close');
		});
		$("#saveEditFixedAssetInfo").click(function(e){
			var valid = $('#editFixedAssetInfoWindow').jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'updateFixedAsset',
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
						  return;
					  }
					  Grid.renderMessage('jqxgrid', uiLabelMap.wgupdatesuccess, {template : 'success', appendContainer : '#containerjqxgrid'});
					  updateFixedAssetView(data);
					  $("#editFixedAssetInfoWindow").jqxWindow('close');
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
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
	};
	var initValidator = function(){
		$('#editFixedAssetInfoWindow').jqxValidator({
			position: 'bottom',
	        rules: [
	       			{ input: '#fixedAssetTypeId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	       				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#fixedAssetName', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					if(input.val()){
	    						return true;
	    					}
	    					return false;
	    				}
	    			},
	    			{ input: '#currencyUomId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
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
	    			{ input: '#quantity', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					if(input.val() <= 0){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},
                    { input: '#serialNumber', message: uiLabelMap.BACCMustNotByContainSpecialCharacter, action: 'keyup, change',
                        rule: function (input, commit) {
                           return checkSpecialCharacters(input);
                     }
                    },
                    { input: '#warrantyPeriod', message: uiLabelMap.BACCMustNotByContainSpecialCharacter, action: 'keyup, change',
                        rule: function (input, commit) {
                            return checkSpecialCharacters(input);
                        }
                    },
                    { input: '#countryOrigin', message: uiLabelMap.BACCMustNotByContainSpecialCharacter, action: 'keyup, change',
                        rule: function (input, commit) {
                            return checkSpecialCharacters(input);
                        }
                    },
                    { input: '#warrantyCondition', message: uiLabelMap.BACCMustNotByContainSpecialCharacter, action: 'keyup, change',
                        rule: function (input, commit) {
                            return checkSpecialCharacters(input);
                        }
                    },
                    { input: '#description', message: uiLabelMap.BACCMustNotByContainSpecialCharacter, action: 'keyup, change',
                        rule: function (input, commit) {
                            return checkSpecialCharacters(input);
                        }
                    },
                    { input: '#receiptNumber', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }
	                       return false;
	    				}
	    			},
	    			{ input: '#receiptDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
	var setFixedAssetData = function(data){
		$("#fixedAssetId").val(data.fixedAssetId);
		$("#fixedAssetName").val(data.fixedAssetName);
		$("#currencyUomId").val(data.uomId);
		$("#fixedAssetTypeId").val(data.fixedAssetTypeId);
		$("#quantity").val(data.quantity);
		$("#yearMade").val(data.yearMade);
		var dropDownContent = '<div class="innerDropdownContent">' + data.fullName + '</div>';
		if(data.partyId){
			$("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
			accutils.setAttrDataValue('partyDropDown', data.partyId);
			$("#orgUseTypeDropDown").val("internalOrganization");
		}else if(data.productStoreId){
			$("#productStoreDropDown").jqxDropDownButton('setContent', dropDownContent);
			accutils.setAttrDataValue('productStoreDropDown', data.productStoreId);
			$("#orgUseTypeDropDown").val("productStore");
		}
        if(data.serialNumber){
        	$("#serialNumber").val(data.serialNumber);
        }
        if(data.countryOrigin){
        	$("#countryOrigin").val(data.countryOrigin);
        }
        if(data.warrantyPeriod){
        	$("#warrantyPeriod").val(data.warrantyPeriod);
        }
        if(data.warrantyCondition){
        	$("#warrantyCondition").val(data.warrantyCondition);
        }
        if(data.statusId){
        	$("#statusId").val(data.statusId);
        }
        if(data.description){
        	$("#description").val(data.description);
        }
        if(data.supplierId){
        	dropDownContent = '<div class="innerDropdownContent">' + data.supplierName + ' [' + data.supplierId + ']</div>';
			$("#supplierDropDown").jqxDropDownButton('setContent', dropDownContent);
			$("#supplierDropDown").attr("data-value", data.supplierId);
        }
        if (data.receiptNumber) {
        	$("#receiptNumber").val(data.receiptNumber);
        }
        if (data.receiptDate) {
        	$("#receiptDate").val(new Date(data.receiptDate));
        }
	};
	
	var getData = function(){
		var submitedData = {};
		submitedData.fixedAssetId = globalVar.fixedAssetId;
		submitedData.fixedAssetName = $('#fixedAssetName').val();
		submitedData.fixedAssetTypeId = $('#fixedAssetTypeId').val();
		submitedData.uomId = $("#currencyUomId").val();
		submitedData.quantity = $("#quantity").val();
		submitedData.yearMade = $("#yearMade").val();
		var orgUseType = $("#orgUseTypeDropDown").val(); 
		if(orgUseType == "internalOrganization"){
			submitedData.partyId = $("#partyDropDown").attr('data-value');
		}else if(orgUseType == "productStore"){
			submitedData.productStoreId = $("#productStoreDropDown").attr('data-value');
		}
		
		var supplierId = $("#supplierDropDown").attr("data-value");
		if(supplierId){
			submitedData.supplierId = supplierId;
		}
		if($("#serialNumber").val()){
			submitedData.serialNumber = $("#serialNumber").val();
		}
		if($("#warrantyPeriod").val()){
			submitedData.warrantyPeriod = $("#warrantyPeriod").val();
		}
		if($("#supplierDropDown").attr("data-value")){
			submitedData.supplierId = $("#supplierDropDown").attr("data-value");
		}
		if($("#statusId").val()){
			submitedData.statusId = $("#statusId").val(); 
		}
		if($("#warrantyCondition").val()){
			submitedData.warrantyCondition = $("#warrantyCondition").val(); 
		}
		if($("#description").val()){
			submitedData.description = $("#description").val(); 
		}
		if($("#countryOrigin").val()){
			submitedData.countryOrigin = $("#countryOrigin").val(); 
		}
		submitedData.receiptNumber = $("#receiptNumber").val();
		submitedData.receiptDate = $("#receiptDate").jqxDateTimeInput('val', 'date').getTime();
		
		return submitedData;
	};
	var updateFixedAssetView = function(data){
		$("#fixedAssetNameView").html(data.fixedAssetName);
		var fixedAssetType = $('#fixedAssetTypeId').jqxDropDownList('getSelectedItem');
		$("#fixedAssetTypeView").html(fixedAssetType.label);
		$("#fixedAssetYearMadeView").html(data.yearMade);
		var orgUseType = $("#orgUseTypeDropDown").val(); 
		if(orgUseType == "internalOrganization"){
			$("#fixedAssetPartyView").html($("#partyDropDown").val());
		}else if(orgUseType == "productStore"){
			$("#fixedAssetPartyView").html($("#productStoreDropDown").val());
		}
		
		$("#fixedAssetQtyView").html(data.quantity);
		if(data.supplierId){
			$("#fixedAssetSupplierView").html($("#supplierDropDown").val());
		}else{
			$("#fixedAssetSupplierView").html("_________________");
		}
		if(data.serialNumber){
			$("#fixedAssetSerialView").html(data.serialNumber);
		}else{
			$("#fixedAssetSerialView").html("_________________");
		}
		if(data.statusId){
			var status = $('#statusId').jqxDropDownList('getSelectedItem');
			$("#fixedAssetStatusView").html(status.label);
		}else{
			$("#fixedAssetStatusView").html("_________________");
		}
		if(data.warrantyPeriod){
			$("#fixedAssetWarrantyPeriodView").html(data.warrantyPeriod);
		}else{
			$("#fixedAssetWarrantyPeriodView").html("_________________");
		}
		if(data.warrantyCondition){
			$("#fixedAssetConditionView").html(data.warrantyCondition);
		}else{
			$("#fixedAssetConditionView").html("_________________");
		}
		if(data.countryOrigin){
			$("#fixedAssetCountryOriginView").html(data.countryOrigin);
		}else{
			$("#fixedAssetCountryOriginView").html("_________________");
		}
		if(data.description){
			$("#fixedAssetDescriptionView").html(data.description);
		}else{
			$("#fixedAssetDescriptionView").html("_________________");
		}
		
		if(data.receiptNumber){
			$("#fixedAssetReceiptNumberView").html(data.receiptNumber);
		}else{
			$("#fixedAssetReceiptNumberView").html("_________________");
		}
		
		if(data.receiptDate){
			$("#fixedAssetReceiptDateView").html(getDateDesc(new Date(data.receiptDate)));
		}else{
			$("#fixedAssetReceiptDateView").html("_________________");
		}
	};
	var getDateDesc = function(date){
		var str = "";
		str += (date.getDate() > 9? date.getDate() : ("0" + date.getDate()));
		str += "/" + (date.getMonth() >= 9? (date.getMonth() + 1) : ("0" + (date.getMonth() + 1)));
		str += "/" + date.getFullYear();
		return str;
	};
    var checkSpecialCharacters = function(input) {
        var value = input.val();
        if (OlbCore.isNotEmpty(value)) {
            var regexCheck = new RegExp(uiLabelMap.BACCSpecialCharacterCheck);
            if(regexCheck.test(value)){
                return true;
            }
            return false;
        }
        return true;
    };
	return{
		init: init
	};
}());
$(document).ready(function(){
	fixedAssetEditGeneralInfoObj.init();
});
