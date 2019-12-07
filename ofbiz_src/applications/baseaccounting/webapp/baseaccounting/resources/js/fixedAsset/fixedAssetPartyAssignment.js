var fixedAssetPartyAssignObj = (function(){
	var init = function(){
		initDropDownTree();
		initDropDown();
		initInput();
		initEvent();
		initWindow();
		initValidator();
	};
	var initDropDownTree = function(){
		var config = {dropDownBtnWidth: "97%", treeWidth: 280};
		globalObject.createJqxTreeDropDownBtn($("#assignPartyTree"), $("#assignPartyDropDownBtn"), globalVar.rootPartyArr, "treeAlloc", "treeChildAlloc", config);
		$("#assignPartyTree").on('select', function(event){
			var item = $('#assignPartyTree').jqxTree('getItem', event.args.element);
			var dropDownContent = '<div class="innerDropdownContent">' + item.label + '</div>';
	        $("#assignPartyDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
	        $("#assignPartyDropDownBtn").jqxDropDownButton('close');
	        accutils.setAttrDataValue('assignPartyDropDownBtn', item.value);
	        $("#assignPartyDropDownBtn").attr("data-label", item.label);
		});
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#assignPartyRoleType"), globalVar.fixedAssetAssignRoleArr, {placeHolder: uiLabelMap.filterchoosestring, width: '97%', 
			height: '25px', valueMember: 'roleTypeId', displayMember: 'description'});
		accutils.createJqxDropDownList($("#assignPartyStatus"), globalVar.fixedAssetAssignStatusArr, {placeHolder: uiLabelMap.filterchoosestring, width: '97%', 
			height: '25px', valueMember: 'statusId', displayMember: 'description'});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#partyFixedAssetAssignmentWindow"), 450, 340);
	};
	var initInput = function(){
		$("#assignPartyAllocatedDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#assignPartyFromDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#assignPartyThruDate").jqxDateTimeInput({width: '97%', height: 25, showFooter: true});
	};
	var initEvent = function(){
		$("#partyFixedAssetAssignmentWindow").on('close', function(event){
			Grid.clearForm($("#partyFixedAssetAssignmentWindow"));
			$("#assignPartyTree").jqxTree('selectItem', null);
		});
		$("#partyFixedAssetAssignmentWindow").on('open', function(event){
			var date = new Date();
			$("#assignPartyAllocatedDate").val(date);
			$("#assignPartyFromDate").val(date);
			$("#assignPartyThruDate").val(null);
		});
		$("#cancelAssignParty").click(function(){
			$("#partyFixedAssetAssignmentWindow").jqxWindow('close');
		});
		$("#saveAssignParty").click(function(){
			var valid = $("#partyFixedAssetAssignmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			createPartyFixedAssetAssignment();
		});
	};
	var getData = function(){
		var data = {};
		var selectedParty = $("#assignPartyTree").jqxTree('getSelectedItem');
		data.partyId = selectedParty.value;
		data.roleTypeId = $("#assignPartyRoleType").val();
		data.allocatedDate = $("#assignPartyAllocatedDate").jqxDateTimeInput('val', 'date').getTime();
		data.fromDate = $("#assignPartyFromDate").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#assignPartyThruDate").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		data.fixedAssetId = globalVar.fixedAssetId;
		return data;
	};
	var createPartyFixedAssetAssignment = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createPartyFixedAssetAssignmentOlb',
			type: 'POST',
			data: data,
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				Grid.renderMessage('gridFixedAssetAssignment', response.successMessage, {template : 'success', appendContainer : '#containergridFixedAssetAssignment'});
				$("#gridFixedAssetAssignment").jqxGrid('updatebounddata');
				$("#partyFixedAssetAssignmentWindow").jqxWindow('close');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var initValidator = function(){
		$("#partyFixedAssetAssignmentWindow").jqxValidator({
			rules: [
				{ input: '#assignPartyDropDownBtn', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var value = $("#assignPartyTree").jqxTree('getSelectedItem');
						if(!value){
							return false;
						}
						return true;
					}
				},
				{ input: '#assignPartyRoleType', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
				       if(input.val().length <= 0){
				    	   return false;
				       }
				       return true;
					}
				},     
				{ input: '#assignPartyAllocatedDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
				{ input: '#assignPartyFromDate', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
				{ input: '#assignPartyStatus', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
				{ input: '#assignPartyThruDate', message: uiLabelMap.validStartDateMustLessThanOrEqualFinishDate, action: 'keyup, change', 
					rule: function (input, commit) {
						var thruDate = $(input).jqxDateTimeInput('val', 'date');
						if(thruDate){
							var fromDate = $("#assignPartyFromDate").jqxDateTimeInput('val', 'date');
							if(thruDate < fromDate){
								return false;
							}
						}
						return true;
					}
				},     
			]
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	fixedAssetPartyAssignObj.init();
});