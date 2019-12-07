var fixedAssetAllocateObj = (function(){
	var init = function(){
		initInput();
		initDropDownTree();
		initDropDownGrid();
		initWindow();
		initValidator();
		initEvent();
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: "97%", treeWidth: 280};
		globalObject.createJqxTreeDropDownBtn($("#wn_allocPartyTree"), $("#wn_allocPartyId"), globalVar.rootPartyArr, "treeAlloc", "treeChildAlloc", config);
		$("#wn_allocPartyTree").on('select', function(event){
			var item = $('#wn_allocPartyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#wn_allocPartyId").jqxDropDownButton('setContent', dropDownContent);
	        $("#wn_allocPartyId").jqxDropDownButton('close');
	        accutils.setAttrDataValue('wn_allocPartyId', item.value);
	        $("#wn_allocPartyId").attr("data-label", item.label);
		});
	};
	var initDropDownGrid = function(){
		$("#wn_allocGlAccountId").jqxDropDownButton({width: '97%', height: 25});
		var datafields = [{name: 'glAccountId', type: 'string'}, {name: 'accountName', type: 'string'}];
		var columns = [
						{text: uiLabelMap.BACCGlAccountId, datafield: 'glAccountId', width: '30%'},
						{text: uiLabelMap.BACCAccountName, datafield: 'accountName'}
					];
		
		var config = {
				url: 'JqxGetListGlAccountByClass&glAccountClassId=SGA_EXPENSE',
				filterable: true,
				showtoolbar : false,
				width : '100%',
				virtualmode: true,
				editable: false,
				localization: getLocalization(),
				pageable: true,
				source:{
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafields, columns, null, $("#wn_allocGlAccountGrid"));
	};
	var initValidator = function(){
		$('#formNewAllocParty').jqxValidator({
	        rules: [
	       			{ input: '#wn_allocPartyId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	       				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#allocRate', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val() <= 0){
	                    	   return false;
	                       }
	                       return true;
	    				}
	    			},
	    			{ input: '#allocRate', message: uiLabelMap.ValueMustBeLessOrEqualThanOneHundred, action: 'keyup, change', 
	    				rule: function (input, commit) {
	    					if(input.val() > 100){
	    						return false;
	    					}
	    					return true;
	    				}
	    			},
	    			{ input: '#wn_allocGlAccountId', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
	    				rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			}
               ]
	    });
	};
	var initInput = function(){
		$("#allocRate").jqxNumberInput({digits: 3, inputMode: 'simple', decimalDigits: 0, spinButtons: true, width: '97%', symbol: '%', symbolPosition: 'right'});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#newAllocParty"), 450, 220);
	};
	var initEvent = function(){
		$("#wn_allocGlAccountGrid").on('rowclick', function(event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var rowData = $("#wn_allocGlAccountGrid").jqxGrid('getrowdata', boundIndex);
			var dropDownContent = '<div class="innerDropdownContent">' + rowData.glAccountId + '</div>';
			$("#wn_allocGlAccountId").jqxDropDownButton('setContent', dropDownContent);
			$("#wn_allocGlAccountId").attr("data-value", rowData.glAccountId);
			$("#wn_allocGlAccountId").jqxDropDownButton('close');
		});
		$('#newAllocParty').on('close',function(){
			Grid.clearForm($('#newAllocParty'));
			$('#wn_allocGlAccountGrid').jqxGrid('clearSelection');
			$('#wn_allocPartyTree').jqxTree('selectItem', null);
			$("#wn_allocPartyId").attr("data-label", "");
		});
		$("#alterCancel2").click(function(event){
			$('#newAllocParty').jqxWindow('close');
		});
		$("#alterSave2").click(function(event){
			var valid = $("#formNewAllocParty").jqxValidator('validate');
			if(!valid){
				return;
			}
			var row = {};
			row['allocPartyId'] = $('#wn_allocPartyId').attr('data-value');
			row['allocPartyName'] = $('#wn_allocPartyId').attr('data-label');
			row['allocRate'] = $('#allocRate').val();
			row['allocGlAccountId'] = $('#wn_allocGlAccountId').attr('data-value');
			fixedAssetStep3.addRowData(row);
			$("#newAllocParty").jqxWindow('close');
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	fixedAssetAllocateObj.init()
});