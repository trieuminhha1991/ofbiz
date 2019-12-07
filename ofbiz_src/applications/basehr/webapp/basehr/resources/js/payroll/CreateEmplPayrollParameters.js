var createEmplPayrollParamObject = (function(){
	var init = function(){
			initJqxDateTime();
			initBtnEvent();
			initJqxDropDownList();
			initJqxValidator();
			initJqxWindow();
			initJqxGridSearchEmpl();
	};
	
	var initJqxDateTime = function(){
			$("#fromDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		    $("#thruDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		    restrictFomDateThruDate($("#fromDateParamNew"), $("#thruDateParamNew"));
		    $("#configPayrollParamFromDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		    $("#configPayrollParamThruDate").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		    $("#thruDateParamNew").val(null);
		    restrictFomDateThruDate($("#configPayrollParamFromDate"), $("#configPayrollParamThruDate"));
	};
	
	var initBtnEvent = function(){
		$("#addNew").click(function(event){
			openJqxWindow(jQuery("#popupWindowPayrollEmplParams"));
		});
		$("#searchEmpl").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
		});
		$("#btnCancel").click(function(event){
			$("#popupWindowPayrollEmplParams").jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			var valid = $("#popupWindowPayrollEmplParams").jqxValidator('validate');
			if(!valid){
				return false;
			}
			$("#btnSave").attr("disabled", "disabled");
			bootbox.dialog(uiLabelMap.CreateEmplPayrollParametersWarning,
					[{
		    		    "label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		    	createEmplPayrollParameters();
		    		    }
		    		},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		    "callback": function() {
		    		    	$("#btnSave").removeAttr("disabled");
		    		    }
		    		}
					
					]
			);
		});
		$("#settingPyrllPosTypeConfig").click(function(event){
			openJqxWindow($("#windowConfigParamPosType"));
		});
		$("#btnSaveConfigParam").click(function(event){
			var valid = $("#windowConfigParamPosType").jqxValidator('validate');
			if(!valid){
				return;
			}
			var dataSubmit = {};
			dataSubmit.fromDate = $("#configPayrollParamFromDate").jqxDateTimeInput('val', 'date').getTime();
			var thruDate = $("#configPayrollParamThruDate").jqxDateTimeInput('val', 'date');
			if(thruDate){
				dataSubmit.thruDate = thruDate.getTime();
			}
			dataSubmit.overrideDataWay = $("#configPyrllParamSettingDropdown").val();
			var item = $("#jqxTree").jqxTree('getSelectedItem');
			dataSubmit.partyId = item.value;
			$("#jqxgrid").jqxGrid({disabled: true});
			$("#jqxgrid").jqxGrid('showloadelement');
			$("#windowConfigParamPosType").jqxWindow('close');
			$.ajax({
				url: 'settingEmplPayrollParamByConfig',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					$("#jqxNtf").jqxNotification('closeLast');
					if(response.responseMessage == "success"){
						$('#jqxgrid').jqxGrid('updatebounddata');
						$("#jqxNtfContent").text(response.successMessage);
						$("#jqxNtf").jqxNotification({template: 'info'});
						$("#jqxNtf").jqxNotification("open");
					}else{
						$("#jqxNtfContent").text(response.errorMessage);
						$("#jqxNtf").jqxNotification({template: 'error'});
						$("#jqxNtf").jqxNotification("open");
					}
				},
				complete: function(jqXHR, textStatus){
					$("#jqxgrid").jqxGrid({disabled: false});
					$("#jqxgrid").jqxGrid('hideloadelement');
				}
			});
		});
		$("#btnCancelConfigParam").click(function(event){
			$("#windowConfigParamPosType").jqxWindow('close');
		});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(codeArr, $('#parameterCodeNew'), "code", "description", 25, "98%");
		var configPayrollParamSetting = [
		                 		    	{value: 'getValueHighest', description: uiLabelMap.PayrollParamPositionHighest},                             
		                 		    	{value: 'getValueLowest', description: uiLabelMap.PayrollParamPositionLowest},                             		                 		    	                          
		                 		];
		createJqxDropDownList(configPayrollParamSetting, $('#configPyrllParamSettingDropdown'), "value", "description", 25, "98%");
		$('#parameterCodeNew').on('select', function(event){
			 var args = event.args;
			 if(args){
				 var index = args.index;
				 var datarecord = codeArr[index];
				 var periodTypeId = datarecord.periodTypeId;
				 for(var i = 0; i < periodTypeArr.length; i++){
					 if(periodTypeId == periodTypeArr[i].periodTypeId){
						 $("#periodTypeParamNew").html(periodTypeArr[i].description);
						 break;
					 }
				 }
				 if(datarecord.type == 'CONSTPERCENT'){
		    		$("#parameterValueNew").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    	}else{
		    		$("#parameterValueNew").jqxNumberInput({decimalDigits:0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    	}
			 }
		});
	};
	
	var initJqxValidator = function(){
		$("#popupWindowPayrollEmplParams").jqxValidator({
			rules:[
				{input: '#searchEmpl', message: uiLabelMap.CommonRequired, action: 'blur',
					rule: function (input, commit){
						var value = $("#partyIdPayrollParam").val();
						if(!value || !value.value){
							return false
						}
						return true;
					}	
				},        
				{input: '#parameterCodeNew', message: uiLabelMap.CommonRequired, action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false
						}
						return true;
					}	
				},
				{
					input : '#parameterValueNew', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input,commit){
						if(input.val() < 0){
							return false;
						}
						return true;
					}
				}
			]
		});
		$("#windowConfigParamPosType").jqxValidator({
			rules:[
				{input: '#configPayrollParamFromDate', message: uiLabelMap.CommonRequired, action: 'blur',
					rule: function (input, commit){
						var value = $("#configPayrollParamFromDate").val();
						if(!value){
							return false
						}
						return true;
					}	
				}     	
			]
		});
	};
	
	var createEmplPayrollParameters = function(){
		$('#jqxgrid').jqxGrid({disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		var thruDate = $("#thruDateParamNew").jqxDateTimeInput('getDate');
		var dataSubmit = {};
		dataSubmit["partyId"] = $("#partyIdPayrollParam").jqxInput('val').value;
		dataSubmit["code"] = $('#parameterCodeNew').val();
		dataSubmit["value"] = $("#parameterValueNew").val();
		dataSubmit["fromDate"] = $("#fromDateParamNew").jqxDateTimeInput('getDate').getTime();
		if(thruDate){
			dataSubmit["thruDate"] = thruDate.getTime();
		}
		$.ajax({
			url: 'createEmplPayrollParameters',
			data: dataSubmit,
			type: 'POST',
			success: function(data){
				$("#jqxNtf").jqxNotification('closeLast');
				if(data.responseMessage == "success"){
					$('#jqxgrid').jqxGrid('updatebounddata');
					$("#jqxNtfContent").text(data.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
				}else{
					$("#jqxNtfContent").text(data.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$("#btnSave").removeAttr("disabled");
				$('#jqxgrid').jqxGrid('hideloadelement');
				$('#jqxgrid').jqxGrid({disabled: false});
				$("#popupWindowPayrollEmplParams").jqxWindow('close');
				//clearDataInWindow();
			}
		});
	};
	
	var initJqxRadioButton = function(){
		$("#getValueHighest").jqxRadioButton({checked: true, theme: 'olbius'});
		$("#getValueLowest").jqxRadioButton({checked: false, theme: 'olbius'});
		$("#getValueTotal").jqxRadioButton({ checked: false, theme: 'olbius'});
	};
	
	var initJqxSplitter = function (){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	
	var initJqxWindow = function(){
		$("#popupWindowPayrollEmplParams").jqxWindow({showCollapseButton: false,autoOpen: false,
			maxWidth: 520, minWidth: 520, height: 350, width: 520, isModal: true, theme:theme,
			initContent: function(){
				initJqxInput();
				$("#parameterValueNew").jqxNumberInput({decimalDigits: 0, width: '98%', height: '25px', spinButtons: false, digits: 9, max: 999999999, theme: 'olbius'});
			}	
		});
		$("#popupWindowPayrollEmplParams").on('close', function(event){
			clearDataInWindow();
			$("#popupWindowPayrollEmplParams").jqxValidator('hide');
		});
		$("#popupWindowPayrollEmplParams").on('open', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#fromDateParamNew").val(selection.from);
		});
		
		$('#popupWindowEmplList').jqxWindow({
		    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 500, height: 500, width: "80%", isModal: true, 
		    theme:'olbius', collapsed:false,
		    initContent: function () {  
		    	initJqxSplitter();
		    }
		});
		$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != "undefined"){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
			$("#popupWindowPayrollEmplParams").jqxValidator('hide');
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
		$("#windowConfigParamPosType").jqxWindow({
			showCollapseButton: true, autoOpen: false, height: 240, width: 420, isModal: true, 
		    theme:'olbius', collapsed:false,
		    initContent: function () {  		    	
		    }
		});
		$("#windowConfigParamPosType").on('close', function(event){
			Grid.clearForm($(this));
			$(this).jqxValidator('hide');
		});
		$("#windowConfigParamPosType").on('open', function(event){
			$("#configPyrllParamSettingDropdown").jqxDropDownList('selectItem', 'getValueHighest');
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			$("#configPayrollParamFromDate").val(selection.from);
			$("#configPayrollParamThruDate").val(null);
		});
	};
	
	var initJqxInput = function(){
		$("#partyIdPayrollParam").jqxInput({ placeHolder: uiLabelMap.EnterEmployeeId,
			height: 23, width: '86%', theme: 'olbius', valueMember: 'partyId', displayMember:'partyName', disabled: true});
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, url: "", selectionmode: 'singlerow', width: "100%", height: 440});
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    $('#popupWindowEmplList').jqxWindow('close');
		    $("#partyIdPayrollParam").jqxInput('val', {value: data.partyId, label: data.partyName});  	
		});
	};
	
	var clearDataInWindow = function(){
		var nowDate = new Date(globalVar.nowTimestamp);
		$("#periodTypeParamNew").html(uiLabelMap.HRCommonNotSetting);
		$("#partyIdPayrollParam").jqxInput('val', {label: ' ', value: ''});
		$("#parameterCodeNew").jqxDropDownList('clearSelection');
		$("#parameterValueNew").val(0);
		//$("#fromDateParamNew").val(nowDate);
		$("#thruDateParamNew").val(null);
	};
	
	return{
		init: init
	};
}());

function jqxTreeEmplListSelect(event){
	refreshBeforeReloadGrid($('#EmplListInOrg'));
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=" + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}

$(document).ready(function () {
	createEmplPayrollParamObject.init();
});