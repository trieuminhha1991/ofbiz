var fixedAssetNewStep1 = (function(){
	var inputTextWidth = '96%';
	var otherInputWidth = '98%';
	var init = function(){
		initInput();
		initDropDown();
		initDropDownTree();
		initDropDownGrid();
		initProductStoreDropDown();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#fixedAssetId").jqxInput({width: inputTextWidth, height: 22 });
		$("#fixedAssetName").jqxInput({width: inputTextWidth, height: 22});
		$("#warrantyPeriod").jqxInput({width: inputTextWidth, height: 22});
		$("#countryOrigin").jqxInput({width: inputTextWidth, height: 22});
		$("#warrantyCondition").jqxInput({width: inputTextWidth, height: 22});
		$("#serialNumber").jqxInput({width: inputTextWidth, height: 22});
		$("#yearMade").jqxNumberInput({ width: '95%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$("#quantity").jqxNumberInput({ width: '100%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
		$("#receiptNumber").jqxInput({width: inputTextWidth, height: 22});
		$("#receiptDate").jqxDateTimeInput({width: '98%', height: 25});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#statusId"), globalVar.statusData, {valueMember: 'statusId', displayMember: 'description', width: otherInputWidth, height: 25, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#currencyUomId"), globalVar.uomData, {valueMember: 'uomId', displayMember: 'abbreviation', width: otherInputWidth, height: 25});
		accutils.createJqxDropDownList($("#fixedAssetTypeId"), globalVar.fixedAssetTypeData, {valueMember: 'fixedAssetTypeId', displayMember: 'description', width: otherInputWidth, height: 25, filterable: true, placeHolder: uiLabelMap.filterchoosestring});
		accutils.createJqxDropDownList($("#orgUseTypeDropDown"), globalVar.orgUseTypeArr, {valueMember: 'type', displayMember: 'description', width: '97%', height: 25});
		accutils.createJqxDropDownList($("#partnerDropdown"),globalVar.listOrgPartner, {valueMember:'partyId',displayMember: 'partyName', width: '97%', height: 25} );
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: otherInputWidth, treeWidth: 300};
		globalObject.createJqxTreeDropDownBtn($("#partyTree"), $("#partyDropDown"), globalVar.rootPartyArr, "tree", "treeChild", config);
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
	var initDropDownGrid = function(){
		$("#supplierDropDown").jqxDropDownButton({width: otherInputWidth, height: 25}); 
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
      	];
      	var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
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
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#supplierGrid"));
	};
	
	var initEvent = function(){
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
		$('#fixedAssetTypeId').on('change', function (event) {
		 	var args = event.args;
		 	if (args) {
			    var item = args.item;
			    for(var i = 0; i < globalVar.assetTypeGlAccData.length; i++){
			    	if(globalVar.assetTypeGlAccData[i].fixedAssetTypeId == item.value && globalVar.assetTypeGlAccData[i].fixedAssetId == '_NA_'){
			    		accutils.setValueDropDownButtonOnly($("#costGlAccountId"), globalVar.assetTypeGlAccData[i].assetGlAccountId, globalVar.assetTypeGlAccData[i].assetGlAccountId);
			    		accutils.setValueDropDownButtonOnly($("#depGlAccountId"), globalVar.assetTypeGlAccData[i].accDepGlAccountId, globalVar.assetTypeGlAccData[i].accDepGlAccountId);
			    		accutils.setValueDropDownButtonOnly($("#allocGlAccountId"), globalVar.assetTypeGlAccData[i].depGlAccountId, globalVar.assetTypeGlAccData[i].depGlAccountId);
			    		break;
			    	}
			    }
		 	}
		});
		$("#autoGenerateIdBtn").click(function(e){
			Loading.show('loadingMacro');
			$.ajax({
				url: 'getFixedAssetIdAutoGenerate',
				success: function(response) {
					  if(response._ERROR_MESSAGE_){
						  bootbox.dialog(response._ERROR_MESSAGE_,
									[
									{
										"label" : uiLabelMap.CommonClose,
										"class" : "btn-danger btn-small icon-remove open-sans",
									}]		
							);		
					  }else{
						  $("#fixedAssetId").val(response.fixedAssetId);
					  }
				  },
				complete:  function(jqXHR, textStatus){
					Loading.hide('loadingMacro');	
				}
			});
		});
		$("#orgUseTypeDropDown").on('select', function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				if(value === "productStore"){
					$("#productStoreDropDown").show();
					$("#partyDropDown").hide();
					$("#partnerDropdown").hide();
				}else if(value === "internalOrganization"){
					$("#productStoreDropDown").hide();
					$("#partyDropDown").show();
					$("#partnerDropdown").hide();
				}else if(value == "partner"){
					$("#partnerDropdown").show();
					$("#productStoreDropDown").hide();
					$("#partyDropDown").hide();
				}else{
					$("#productStoreDropDown").hide();
					$("#partyDropDown").hide();
				}
			}
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
	};
	
	var windownOpenInit = function(){
		$("#quantity").val(1);
		$("#orgUseTypeDropDown").jqxDropDownList({selectedIndex: 0});
		if(globalVar.currencyUomId){
			$("#currencyUomId").val(globalVar.currencyUomId);
		}
		if(globalVar.rootPartyArr.length > 0){
			$("#partyTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		var date = new Date();
		$("#yearMade").val(date.getFullYear());
	};
	var initValidator = function(){
		$('#fixedAssetGeneralInfoForm').jqxValidator({
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
	    			{ input: '#fixedAssetId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
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
                    { input: '#statusId', message: uiLabelMap.FieldRequired, action: 'none',
                        rule: function (input, commit) {
                            if(!input.val()){
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
	var validate = function(){
		return $('#fixedAssetGeneralInfoForm').jqxValidator('validate');
	};
	var getData = function(){
		var submitedData = {};
		submitedData.fixedAssetId = $('#fixedAssetId').val();
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
		}else if(orgUseType == 'partner'){
			submitedData.partyId = $("#partnerDropdown").val();
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
	var resetData = function(){
		Grid.clearForm($('#fixedAssetGeneralInfoForm'));
		$('#fixedAssetGeneralInfoForm').jqxValidator('hide');
		$("#supplierGrid").jqxGrid('clearselection');
		$("#partyTree").jqxTree('selectItem', null);
		$("#orgUseTypeDropDown").jqxDropDownList('clearSelection');
		$("#partyDropDown").jqxDropDownButton('setContent', "");
		
		$("#productStoreGrid").jqxGrid('clearselection');
		$('#productStoreGrid').jqxGrid('clearfilters');
		$('#productStoreGrid').jqxGrid('gotopage', 0);
		$("#productStoreDropDown").jqxDropDownButton('setContent', "");
		$("#partnerDropdown").jqxDropDownList('clearSelection');
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
		init: init,
		validate: validate,
		resetData: resetData,
		windownOpenInit: windownOpenInit,
		getData: getData
	}
}());
$(document).ready(function(){
	fixedAssetNewStep1.init();
});