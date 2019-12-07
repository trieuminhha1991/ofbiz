var createNewJobPositionObject = (function(){
	var init = function(){
		//initContextMenu();
		initEvent();
		initGrid();
		initInput();
		initJqxValidator();
		initJqxTree();
	    initJqxTreeEvent();
	    initJqxWindow();
	    create_spinner($("#spinnerAjax"));
	};
	
	var initInput = function(){
		$("#quantityPosition").jqxNumberInput({width: '97%', height: 25, spinButtons: true, decimalDigits: 0, min: 0});
		$("#actualFromDate, #actualThruDate").jqxDateTimeInput({width: '97%', height: 25, theme: 'olbius'});
		$("#actualThruDate").jqxDateTimeInput({showFooter: true});
	};
	
	var initJqxTree = function(){
		var config = {dropDownBtnWidth: 268, treeWidth: 268};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
	};
	
	var initJqxTreeEvent = function(){
		setJqxTreeDropDownSelectEvent($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addNewPositionWindow"), 500, 310);
		$("#addNewPositionWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#positionTypeGrid").jqxGrid('clearselection');
			$("#jqxTreeAddNew").jqxTree('selectItem', null);
		});
		$("#addNewPositionWindow").on('open', function(event){
			if(globalVar.rootPartyArr.length > 0){
				$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
			}
			$("#actualThruDate").val(null);
			$("#quantityPosition").val(1);
		});
	};
	
	var initEvent = function(){
		$("#alterCancel").click(function(event){
			$("#addNewPositionWindow").jqxWindow('close');
		});
		
		$("#createEmplPosition").click(function(event){
			openJqxWindow($("#addNewPositionWindow"));
		});
		
		$("#alterCancel").click(function(event){
			$("#addNewPositionWindow").jqxWindow('close');
		});
		
		$("#alterSave").click(function(event){
			var valid = $("#addNewPositionWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			checkHRPlanningForPosition();
		});
		$("#positionTypeGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#positionTypeGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['description'] + '</div>';
	        $("#positionTypeDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
	        $("#positionTypeDropDownBtn").jqxDropDownButton('close');
	    });
	};
	var checkHRPlanningForPosition = function(){
		var dataSubmit = getData();
		disabledBtn();
		$("#ajaxLoading").show();
		$.ajax({
			url: 'checkHRPlanningForPositionInOrg',
			data: dataSubmit,
			type: 'POST', 
			success: function(response){
				if(response.responseMessage == 'success'){
					var warningMessage = "";
					if(response.warningMessage){
						warningMessage = response.warningMessage; 
					}else{
						warningMessage = uiLabelMap.AddNewRowConfirm;
					}
					bootbox.dialog(warningMessage,
						[
							{
							    "label" : uiLabelMap.CommonSubmit,
							    "class" : "icon-ok btn btn-small btn-primary",
							    "callback": function() {
							    	createNewEmplPosition();	
							    }
							},
							{
								  "label" : uiLabelMap.CommonCancel,
					    		   "class" : "btn-danger icon-remove btn-small",
					    		   "callback": function() {
					    		   
					    		   }
							}
						]		
					);
				}else{
					bootbox.dialog(response.errorMessagae,
							[
								{
									  "label" : uiLabelMap.CommonClose,
						    		   "class" : "btn-danger icon-remove btn-small",
						    		   "callback": function() {
						    		   
						    		   }
								}
							]		
						);	
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				console.log(jqXHR, textStatus, errorThrown);
			},
			complete: function(jqXHR, textStatus){
				enabledBtn();
				$("#ajaxLoading").hide();
			}
		});
	};
	
	var createNewEmplPosition = function (){
		dataSubmit = getData(); 
		$('#jqxgrid').jqxGrid({ disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		$("#addNewPositionWindow").jqxWindow('close');
		$.ajax({
			url: 'createEmplPosition',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				$("#jqxNotify").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$("#ntfContent").html(response.successMessage);
					$("#jqxNotify").jqxNotification({template: 'info'});
    				$("#jqxNotify").jqxNotification("open");
    				$('#jqxgrid').jqxGrid('updatebounddata');
				}else{
					$("#ntfContent").html(response.errorMessage);
					$("#jqxNotify").jqxNotification({template: 'error'});
    				$("#jqxNotify").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$('#jqxgrid').jqxGrid({ disabled: false});
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});
	};
	var getData = function(){
		var selectedIndex = $("#positionTypeGrid").jqxGrid('getselectedrowindex');
		var rowData = $("#positionTypeGrid").jqxGrid('getrowdata', selectedIndex);
		var data = {};
		data.emplPositionTypeId = rowData.emplPositionTypeId;
		data.actualFromDate = $("#actualFromDate").jqxDateTimeInput('val', 'date').getTime();
		var actualThruDate = $("#actualThruDate").jqxDateTimeInput('val', 'date');
		data.quantity = $("#quantityPosition").val();
		if(actualThruDate){
			data.actualThruDate = actualThruDate.getTime(); 
		}
		var selectItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
		data.partyId = selectItem.value; 
		return data;
	};
	var initGrid = function(){
		//createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionTypeId"), "emplPositionTypeId", "description", 25, '97%')
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
	        url: 'JQGetListEmplPositionTypeAndClass',    
   			showtoolbar: false,
        	source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columns, null, $("#positionTypeGrid"));
	};
	var initJqxValidator = function(){
		$("#addNewPositionWindow").jqxValidator({
			rules: [
		    {
				input: "#dropDownButtonAddNew", message: uiLabelMap.FieldRequired, 
				action: 'blur',
			    rule: function (input, commit) {
					var items = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
					if(!items){
						return false;
					}
					return true;
			   }
		    },	        
		    {
				input: '#positionTypeDropDownBtn',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: function (input, commit) {
					var selectedIndex = $("#positionTypeGrid").jqxGrid('getselectedrowindex');
					if(selectedIndex < 0){
						return false;
					}
					return true;
				}
			},
			{
				input: '#quantityPosition',
				message: uiLabelMap.ValueMustBeGreateThanZero,
				action: 'blur',
				rule: function (input, commit) {
					var value = input.val();
					if(value <= 0){
						return false;
					}
					return true;
				}
			},
			{
				input: '#actualFromDate',
				message: uiLabelMap.FieldRequired,
				action: 'blur',
				rule: function (input, commit) {
					if(!input.val()){
						return false;
					}
					return true;
				}
			},
			{
				input: '#actualThruDate',
				message: uiLabelMap.ValueMustBeGreateThanEffectiveDate,
				action: 'blur',
				rule: function (input, commit) {
					var thruDate = input.jqxDateTimeInput('val', 'date');
					if(thruDate){
						var fromDate = $("#actualFromDate").jqxDateTimeInput('val', 'date');
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
	var disabledBtn = function(){
		$("#alterSave").attr("disabled", "disabled");
		$("#alterCancel").attr("disabled", "disabled");
	};
	var enabledBtn = function(){
		$("#alterSave").removeAttr("disabled");
		$("#alterCancel").removeAttr("disabled");
	};
	var validate = function(){
		return $("#addNewPositionWindow").jqxValidator('validate');
	};
	
	return{
		init: init,
		validate: validate
	}
}());

$(document).ready(function () {
	createNewJobPositionObject.init();
});