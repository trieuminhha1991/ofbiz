var addKPIObj = (function(){
	var init = function(){
		initSimpleInput();
		initDropDown();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAddNew"));
	};
	var initSimpleInput = function(){
		$("#keyPerfIndicatorNameAdd").jqxInput({width: '96%', height: 20});
		$("#descendantSelect").jqxCheckBox({ width: '90%', height: 25});
		$("#applAllParty").jqxCheckBox({ width: '90%', height: 25});
		$("#fromDateAdd").jqxDateTimeInput({width: '100%', height: 25});
		$("#thruDateAdd").jqxDateTimeInput({width: '95%', height: 25});
	};
	var initDropDown = function(){
		var config = {filterable: true, checkboxes: true, displayMember: 'description', valueMember: 'emplPositionTypeId', 
				width: '98%', height: 25, searchMode: 'contains'};
		createJqxDropDownListExt($("#emplPositionTypeApplAdd"), globalVar.emplPositionTypeArr, config);
	};
	var initJqxTree = function(){
		var rootPartyArr = globalVar.rootPartyArr;
		var jqxTreeEle = $("#partyTreeAdd");
		var url = "getListPartyRelByParent"; 
		var dataTreeGroup = new Array();
		var textKey = "partyName";
		var valueKey = "partyId";
		var parentKey = "partyIdFrom";
		var suffix = "tree";
		var suffixChild = "treeChild";
		for(var i = 0; i < rootPartyArr.length; i++){
			dataTreeGroup.push({
	   				id: rootPartyArr[i][valueKey] + "_" + suffix,
	   				parentid: "-1",
	   				text: rootPartyArr[i][textKey],
	   				value: rootPartyArr[i][valueKey]
	   		});
	   		dataTreeGroup.push({
	   				id: rootPartyArr[i][valueKey] +"_" + suffixChild,
	   				parentid: rootPartyArr[i][valueKey] + "_" + suffix,
	   				text: "Loading...",
	   				value: url
	   		});
		}
		var source =
	    {
	        datatype: "json",
	        datafields: [
	            { name: 'id' },
	            { name: 'parentid' },
	            { name: 'text' },
	            { name: 'value' }
	        ],
	        id: 'id',
	        localdata: dataTreeGroup
	    };	
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		dataAdapter.dataBind();
		var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeEle.jqxTree({source: records, width: '97%', theme: 'light', height: 150, hasThreeStates: true, checkboxes: true});
		createExpandEventJqxTree(jqxTreeEle, null, true, null, parentKey);
	};
	var initWindow = function(){
		var initContent = function(){
			initJqxTree();
		};
		createJqxWindow($("#AddNewKeyPerfIndicatorWindow"), 500, 470, initContent);
	};
	var initEvent = function(){
		$("#AddNewKeyPerfIndicatorWindow").on('open', function(event){
			initData();
		});
		$("#AddNewKeyPerfIndicatorWindow").on('close', function(event){
			resetData();
		});
		$('#descendantSelect').on('change', function (event) {
            var checked = event.args.checked;
            $('#partyTreeAdd').jqxTree({ hasThreeStates: checked });
        });
		$('#applAllParty').on('change', function(event){
			var checked = event.args.checked;
			if(checked){
				$("#partyApplArea").addClass("disabledArea");
				$('#descendantSelect').jqxCheckBox({checked: false});
			}else{
				$("#partyApplArea").removeClass("disabledArea");
				$('#descendantSelect').jqxCheckBox({checked: true});
			}
		});
		$("#emplPositionTypeApplAdd").on('checkChange', function (event){
			var args = event.args;
			var item = args.item;
			var value = item.value;
			var checked = item.checked;
			if(value == "_NA_"){
				var size = globalVar.emplPositionTypeArr.length;
				if(checked){
					$("#emplPositionTypeApplAdd").jqxDropDownList("close" );
					for(var i = 1; i < size; i++){
						$("#emplPositionTypeApplAdd").jqxDropDownList('uncheckIndex', i); 
						$("#emplPositionTypeApplAdd").jqxDropDownList('disableAt', i); 
					}
				}else{
					for(var i = 1; i < size; i++){
						$("#emplPositionTypeApplAdd").jqxDropDownList('enableAt', i); 
					}
				}
			}
		});
		$("#cancelAddNew").click(function(event){
			$("#AddNewKeyPerfIndicatorWindow").jqxWindow('close');
		});
		$("#saveAddNew").click(function(event){
			var valid = $("#AddNewKeyPerfIndicatorWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateKPIConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createKeyPerfIndicator();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	var createKeyPerfIndicator = function(){
		var data = getData();
		disableAll();
		$.ajax({
			type : 'POST',
			url : 'createKeyPerfIndicator',
			data : data,
			success : function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
					$("#AddNewKeyPerfIndicatorWindow").jqxWindow('close');
					$('#jqxgrid').jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();	
			}
		});
	};
	var initValidator = function(){
		$("#AddNewKeyPerfIndicatorWindow").jqxValidator({
			rules: [
				{input : '#keyPerfIndicatorNameAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#emplPositionTypeApplAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#fromDateAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#thruDateAdd', message : uiLabelMap.ThruDateMustGreaterThanFromDate, action : 'blur',
					rule : function(input, commit){
						var fromDate = $('#fromDateAdd').jqxDateTimeInput('val', 'date');
						var thruDate = $(input).jqxDateTimeInput('val', 'date');
						if(thruDate && thruDate < fromDate){
							return false;
						}
						return true;
					}
				},
				{input : '#partyTreeAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						var applAllParty = $("#applAllParty").jqxCheckBox('checked'); 
						if(!applAllParty){
							var checkedItems = $("#partyTreeAdd").jqxTree('getCheckedItems');
							if(checkedItems.length == 0){
								return false;
							}
						}
						return true;
					}
				},
				
			]
		});
	};
	var initData = function(){
		var date = new Date();
		date.setDate(1);
		$('#descendantSelect').jqxCheckBox({checked: false});
		$('#applAllParty').jqxCheckBox({checked: true});
		$("#fromDateAdd").val(date);
		$("#thruDateAdd").val(null);
	};
	var resetData = function(){
		$("#keyPerfIndicatorNameAdd").val("");
		$("#emplPositionTypeApplAdd").jqxDropDownList('clearSelection');
		$("#fromDateAdd").val(null);
		$("#thruDateAdd").val(null);
		$("#AddNewKeyPerfIndicatorWindow").jqxValidator('hide');
	};
	var getData = function(){
		var data = {};
		data.keyPerfIndicatorName = $("#keyPerfIndicatorNameAdd").val();
		var applAllParty = $("#applAllParty").jqxCheckBox('checked');
		if(applAllParty){
			data.isApplAllParty = "Y";
		}else{
			var partyIds = [];
			var partyCheckedItem = $("#partyTreeAdd").jqxTree('getCheckedItems');
			partyCheckedItem.forEach(function(item){
				partyIds.push(item.value);
			});
			data.partyIds = JSON.stringify(partyIds);
		}
		var isApplAllPosType = false;
		var positionTypeChecked = $("#emplPositionTypeApplAdd").jqxDropDownList('getCheckedItems');
		var positionTypeArr = [];
		for(var i = 0; i < positionTypeChecked.length; i++ ){
			var item = positionTypeChecked[i];
			if(item.value == "_NA_"){
				isApplAllPosType = true;
				break;
			}
			positionTypeArr.push(item.value);
		}
		if(isApplAllPosType){
			data.isApplAllPosType = "Y";
		}else{
			data.emplPositionTypeIds = JSON.stringify(positionTypeArr); 
		}
		data.fromDate = $("#fromDateAdd").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDateAdd").jqxDateTimeInput('val', 'date')
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
	var disableAll = function(){
		$("#loadingAddNew").show();
		$("#cancelAddNew").attr("disabled", "disabled");
		$("#saveAddNew").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAddNew").hide();
		$("#cancelAddNew").removeAttr("disabled");
		$("#saveAddNew").removeAttr("disabled");
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	addKPIObj.init();
});