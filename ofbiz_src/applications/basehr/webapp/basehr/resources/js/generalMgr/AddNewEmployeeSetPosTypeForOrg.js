var settingPositionTypeForOrgObj = (function(){
	var _ancestorTreeArr = [];
	var _partyIdFrom = null;
	var init = function(){
		initJqxTree();
		initDropDownGrid();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initJqxTree = function(){
		var expandCompleteFunc = function(){
			if(_ancestorTreeArr.length > 0){
				$("#setPositionTypeJqxTree").jqxTree('expandItem', $("#" + _ancestorTreeArr.shift() + "_treeSetPosType")[0]);
			}else if(_partyIdFrom){
				$("#setPositionTypeJqxTree").jqxTree('selectItem', $("#" + _partyIdFrom + "_treeSetPosType")[0]);
			}
		};
		var config = {dropDownBtnWidth: '97%', treeWidth: 290, expandCompleteFunc: expandCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#setPositionTypeJqxTree"), $("#setPositionTypeDropDownButton"), globalVar.rootPartyArr, "treeSetPosType", "treeChildSetPosType", config);
		setJqxTreeDropDownSelectEvent($("#setPositionTypeJqxTree"), $("#setPositionTypeDropDownButton"));
	};
	var initDropDownGrid = function(){
		$("#positionTypeDropDownBtn").jqxDropDownButton({width: '97%', height: 25});
		var datafield = [{name: 'emplPositionTypeId', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'classTypeDesc', type: 'string'}];
		var columns = [{text: uiLabelMap.HREmplPositionTypeId, datafield: 'emplPositionTypeId', width: '25%'},
		               {text: uiLabelMap.CommonDescription, datafield: 'description', width: '40%'},
		               {text: uiLabelMap.CommonGroup, datafield: 'classTypeDesc'}];
		var config = {
	   		width: 600, 
	   		virtualmode: true,
	   		showfilterrow: true,
	   		sortable: true,
	        filterable: true,
	        editable: false,
	        url: '',    
   			showtoolbar: false,
        	source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columns, null, $("#positionTypeGrid"));
	};
	var initInput = function(){
		$("#setPositionTypeActualFromDate").jqxDateTimeInput({width: '97%', height: 25});
		$("#setPositionTypeActualThruDate").jqxDateTimeInput({width: '97%', height: 25, showFooter: true});
		$("#setPositionTypeActualThruDate").val(null);
	};
	var initWindow = function(){
		createJqxWindow($("#SetPositionTypeForOrgWindow"), 450, 270);
	};
	var initData = function(){
		updateGridUrl($("#positionTypeGrid"), 'jqxGeneralServicer?sname=JQGetListEmplPositionTypeAndClass');
		$("#setPositionTypeActualFromDate").val(new Date());
		var selectedItem = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getSelectedItem');
		if(selectedItem){
			Loading.show('loadingMacro');
			_partyIdFrom = selectedItem.value;
			$("#setPositionTypeJqxTree").jqxTree('collapseAll');
			$.ajax({
				url: 'getAncestorTreeOfPartyGroup',
				data: {partyId: _partyIdFrom},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						if(response.ancestorTree && response.ancestorTree.length > 0){
							_ancestorTreeArr = response.ancestorTree;
							$("#setPositionTypeJqxTree").jqxTree('expandItem', $("#" + _ancestorTreeArr.shift() + "_treeSetPosType")[0]);
						}
					}
				},
				complete: function(jqXHR, textStatus){
					if(_partyIdFrom){
						$("#setPositionTypeJqxTree").jqxTree('selectItem', $("#" + _partyIdFrom + "_treeSetPosType")[0]);
					}
					Loading.hide('loadingMacro');
				}
			});
		}
	};
	var initEvent = function(){
		$("#SetPositionTypeForOrgWindow").on('open', function(event){
			initData();
		});
		$("#SetPositionTypeForOrgWindow").on('close', function(event){
			$("#positionTypeGrid").jqxGrid('clearselection');
			updateGridUrl($("#positionTypeGrid"), '');
			$("#positionTypeGrid").jqxGrid('clearfilters');
			$("#setPositionTypeActualThruDate").val(null);
			$("#setPositionTypeJqxTree").jqxTree('selectItem', null);
			$("#setPositionTypeJqxTree").jqxTree('collapseAll');
			$("#setPositionTypeDropDownButton").jqxDropDownButton('setContent', '');
			$("#positionTypeDropDownBtn").jqxDropDownButton('setContent', '');
			_ancestorTreeArr = [];
			_partyIdFrom = null;
		});
		$("#positionTypeGrid").on('rowclick', function (event) {
	        var args = event.args;
	        var row = $("#positionTypeGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['description'] + '</div>';
	        $("#positionTypeDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
	        $("#positionTypeDropDownBtn").jqxDropDownButton('close');
	    });
		$("#setPositionTypeCancel").click(function(event){
			$("#SetPositionTypeForOrgWindow").jqxWindow('close');
		});
		$("#setPositionTypeSave").click(function(event){
			var valid = $("#SetPositionTypeForOrgWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreatePositionTypeForOrgConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						 createEmplPositionTypeForOrg();
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
        $("#SetPositionTypeForOrgWindow").on('close',function(event){
            $("#SetPositionTypeForOrgWindow").jqxValidator('hide');
        });
	};
	var createEmplPositionTypeForOrg = function(){
		Loading.show('loadingMacro');
		var data = {};
		var positionTypeIndex = $("#positionTypeGrid").jqxGrid('getselectedrowindex');
		var positionTypeData = $("#positionTypeGrid").jqxGrid('getrowdata', positionTypeIndex);
		data.emplPositionTypeId = positionTypeData.emplPositionTypeId;
		var selectPartyItem = $("#setPositionTypeJqxTree").jqxTree('getSelectedItem');
		data.partyId = selectPartyItem.value; 
		data.actualFromDate = $("#setPositionTypeActualFromDate").jqxDateTimeInput('val', 'date').getTime();
		var actualThruDate = $("#setPositionTypeActualThruDate").jqxDateTimeInput('val', 'date');
		if(actualThruDate){
			data.actualThruDate = actualThruDate.getTime(); 
		}
		$.ajax({
			url: 'createEmplPositionTypeForOrg',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'error'){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
					template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
				var selectedItem = $("#jqxTree" + globalVar.defaultSuffix).jqxTree('getSelectedItem');
				if(selectedItem){
					emplWorkingInfo.getEmplPositionTypeInOrg(selectedItem.value);
				}
				$("#SetPositionTypeForOrgWindow").jqxWindow('close');
			},
			complete: function(jqXHR, textStatus){
				Loading.hide('loadingMacro');
			}
		});
	};
	var updateGridUrl = function(grid, url){
		var source = grid.jqxGrid('source');
		source._source.url = url;
		grid.jqxGrid('source', source);
	};
	var initValidator = function(){
		$("#SetPositionTypeForOrgWindow").jqxValidator({
			rules: [
		    {input: "#setPositionTypeDropDownButton", message: uiLabelMap.FieldRequired,  action: 'blur',
			    rule: function (input, commit) {
					var items = $("#setPositionTypeJqxTree").jqxTree('getSelectedItem');
					if(!items){
						return false;
					}
					return true;
			   }
		    },	        
		    {input: '#positionTypeDropDownBtn', message: uiLabelMap.FieldRequired, action: 'blur',
				rule: function (input, commit) {
					var selectedIndex = $("#positionTypeGrid").jqxGrid('getselectedrowindex');
					if(selectedIndex < 0){
						return false;
					}
					return true;
				}
			},
			{input: '#setPositionTypeActualFromDate', message: uiLabelMap.FieldRequired, action: 'blur',
				rule: function (input, commit) {
					if(!input.val()){
						return false;
					}
					return true;
				}
			},
			{input: '#setPositionTypeActualThruDate', message: uiLabelMap.GTDateFieldRequired, action: 'blur',
				rule: function (input, commit) {
					var thruDate = input.jqxDateTimeInput('val', 'date');
					if(thruDate){
						var fromDate = $("#setPositionTypeActualFromDate").jqxDateTimeInput('val', 'date');
						if(thruDate <= fromDate){
							return false;
						}
					}
					return true;
				}
			},
		    ],
		 });
	};
	var openWindow = function(){
		openJqxWindow($("#SetPositionTypeForOrgWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	settingPositionTypeForOrgObj.init();
});