var addEmplToAssessmentKPIObj = (function(){
	var _perfCriteriaAssessmentId = "";
	var _partySelected = {};
	var init = function(){
		initJqxGridSearchEmpl();
		initJqxWindow();
		initEvent();
	};
	
	var initJqxGridSearchEmpl = function(){
		createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, url: 'JQGetEmplListInOrg&hasrequest=Y', selectionmode: "multiplerows"});
	};
	var initJqxSplitter =  function(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxSplitter();
		};
		createJqxWindow($("#popupWindowEmplList"), 900, 560, initContent);
		$('#popupWindowEmplList').on('open', function(event){
			if(typeof(globalVar.expandTreeId) != 'undefined'){
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#" + globalVar.expandTreeId + "_jqxTreeEmplList")[0]);
			}
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
			_partySelected = {};
		});
	};
	var setData = function(data){
		_perfCriteriaAssessmentId = data.perfCriteriaAssessmentId;
	};
	var initEvent = function(){
		$("#cancelChooseEmpl").click(function(event){
			$("#popupWindowEmplList").jqxWindow('close');
		});
		$("#saveChooseEmpl").click(function(event){
			var partyIdArr = Object.keys(_partySelected);
			$("#popupWindowEmplList").jqxWindow('close');
			$("#emplListInKPIAssessGrid").jqxGrid({disabled: true});
			$("#emplListInKPIAssessGrid").jqxGrid('showloadelement');
			if(partyIdArr.length > 0){
				$.ajax({
					url: 'addPerfCriteriaAssessmentParty',
					data: {perfCriteriaAssessmentId: _perfCriteriaAssessmentId, partyIds: JSON.stringify(partyIdArr)},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							Grid.renderMessage('emplListInKPIAssessGrid', response.successMessage, {autoClose: true,
								template : 'info', appendContainer: "#containeremplListInKPIAssessGrid", opacity : 0.9});
							$("#emplListInKPIAssessGrid").jqxGrid('updatebounddata');
						}else{
							Grid.renderMessage('emplListInKPIAssessGrid', response.errorMessage, {autoClose: true,
								template : 'error', appendContainer: "#containeremplListInKPIAssessGrid", opacity : 0.9});
						}
					},
					error: function(){
						
					},
					complete: function(jqXHR, textStatus){
						$("#emplListInKPIAssessGrid").jqxGrid({disabled: false});
						$("#emplListInKPIAssessGrid").jqxGrid('hideloadelement');
					}
				});
			}
		});
		$("#EmplListInOrg").on('rowselect', function (event){
			var args = event.args;
			var rowData = args.row;
			_partySelected[rowData.partyId] = rowData.partyId;
		});
		$("#EmplListInOrg").on('rowunselect', function (event){
			var args = event.args;
			var rowData = args.row;
			delete _partySelected[rowData.partyId];
		});
	}; 
	return{
		init: init,
		setData: setData
	}
}());

var editEmplKPIAssessmentObj = (function(){
	var _monthCustomTimePeriod = null;
	var _perfCriteriaAssessmentId = null;
	var _partyId = null;
	var init = function(){
		initJqxNumberInput();
		initJqxDropDownList();
		initJqxRadioButton();
		initJqxInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerPartyAssessment"));
	};
	var initJqxDropDownList = function(){
		//createJqxDropDownList(globalVar.perfCriteriaRateGradeArr, $("#perfCriRateGradeIdAssessment"), "perfCriteriaRateGradeId", "perfCriteriaRateGradeName", 25, '98%');
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimeAssessment"), "customTimePeriodId", "periodName", 25, '97%');
		createJqxDropDownList([], $("#monthCustomTimeAssessment"), "customTimePeriodId", "periodName", 25, '95%');
	};
	var initJqxNumberInput = function(){
		$("#salaryRateAssessment").jqxNumberInput({ width: '98%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#allowanceRateAssessment").jqxNumberInput({ width: '98%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#bonusAmountAssessment").jqxNumberInput({ width: '98%', height: '25px', digits: 12, decimalDigits: 0,  spinButtons: true, max: 999999999999});
		$("#punishmentAmountAssessment").jqxNumberInput({ width: '98%', height: '25px', digits: 12, decimalDigits: 0,  spinButtons: true, max: 999999999999});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#editEmplKPIAssessmentWindow"), 470, 460);
		$("#editEmplKPIAssessmentWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#partyIdAssessment").html("");
			_monthCustomTimePeriod = null;
			_partyId = null;
			_perfCriteriaAssessmentId = null;
		});
	};
	var initEvent = function(){
		$("#yearCustomTimeAssessment").on("select", function(event){
			var args = event.args;
			if(args){
				 var value = args.item.value;
				 $.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							updateSourceDropdownlist($("#monthCustomTimeAssessment"), listCustomTimePeriod);
							if(_monthCustomTimePeriod){
								$("#monthCustomTimeAssessment").val(_monthCustomTimePeriod);
							}
						}
					},
					complete: function(jqXHR, textStatus){
						//$("#monthCustomTimeAssessment").jqxDropDownList({disabled: false});
					}
				});
			}
		});
		$("#cancelPartyAssessment").click(function(event){
			$("#editEmplKPIAssessmentWindow").jqxWindow('close');
		});
		$("#savePartyAssessment").click(function(event){
			var valid = $("#editEmplKPIAssessmentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			data.partyId = _partyId;
			data.perfCriteriaAssessmentId = _perfCriteriaAssessmentId;
			disabledAll();
			$("#loadingPartyAssessment").show();
			$.ajax({
				url: 'updatePerfCriteriaAssessmentParty',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('emplListInKPIAssessGrid', response.successMessage, {autoClose: true,
							template : 'info', appendContainer: "#containeremplListInKPIAssessGrid", opacity : 0.9});
						$("#emplListInKPIAssessGrid").jqxGrid('updatebounddata');
						$("#editEmplKPIAssessmentWindow").jqxWindow('close');
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
					$("#loadingPartyAssessment").hide();
				}
			});
		});
	};
	var getData = function(){
		var data = {};
		//data.perfCriteriaRateGradeId = $("#perfCriRateGradeIdAssessment").val();
		data.salaryRate = $("#salaryRateAssessment").val();
		data.allowanceRate = $("#allowanceRateAssessment").val();
		data.bonusAmount = $("#bonusAmountAssessment").val();
		data.punishmentAmount = $("#punishmentAmountAssessment").val();
		data.customTimePeriodId = $("#monthCustomTimeAssessment").val();
		if($("#acceptPartyAssessment").jqxRadioButton('checked')){
			data.statusId = "KAS_ACCEPTED";
		}else if($("#rejectPartyAssessment").jqxRadioButton('checked')){
			data.statusId = "KAS_REJECTED";
		}
		return data;
	};
	var initJqxInput = function(){
		$("#partyAssessmentStatus").jqxInput({width: '96%', height: 20, disabled: true});
	};
	var initJqxRadioButton = function(){
		$("#acceptPartyAssessment").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectPartyAssessment").jqxRadioButton({ width: '95%', height: 25, checked: false});
	};
	var setData = function(data){
		_perfCriteriaAssessmentId = data.perfCriteriaAssessmentId;
		_partyId = data.partyId;
		//$("#perfCriRateGradeIdAssessment").val(data.perfCriteriaRateGradeId);
		$("#partyIdAssessment").html(data.fullName);
		if(data && data.salaryRate){
			$("#salaryRateAssessment").val(data.salaryRate);
		}else{
			//$("#salaryRateAssessment").val(100);
		}
		if(data && data.allowanceRate){
			$("#allowanceRateAssessment").val(data.allowanceRate);
		}else{
			//$("#allowanceRateAssessment").val(100);
		}
		$("#bonusAmountAssessment").val(data.bonusAmount);
		$("#punishmentAmountAssessment").val(data.punishmentAmount);
		_monthCustomTimePeriod = data.customTimePeriodId;
		if(data.hasOwnProperty("yearCustomTimePeriodId")){
			$("#yearCustomTimeAssessment").val(data.yearCustomTimePeriodId);
		}else{
			$("#yearCustomTimeAssessment").val(globalVar.selectYearCustomTimePeriodId);
		}
		if(data.hasOwnProperty("statusId")){
			var statusDes = "";
			for(var i = 0; i < globalVar.statusArr.length; i++){
				if(data.statusId == globalVar.statusArr[i].statusId){
					statusDes = globalVar.statusArr[i].description;
					break;
				}
			}
			$("#partyAssessmentStatus").val(statusDes);
			if(data.statusId == "KAS_ACCEPTED"){
				$("#acceptPartyAssessment").jqxRadioButton({checked: true});
			}else if(data.statusId == "KAS_REJECTED"){
				$("#rejectPartyAssessment").jqxRadioButton({checked: true});
			}
		}
	};
	var initJqxValidator = function(){
		$("#editEmplKPIAssessmentWindow").jqxValidator({
			rules: [
				/*{input : '#perfCriRateGradeIdAssessment', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},*/
				{input : '#salaryRateAssessment', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#allowanceRateAssessment', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() < 0){
							return false;
						}
						return true;
					}
				},
				{input : '#bonusAmountAssessment', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() < 0){
							return false;
						}
						return true;
					}
				},
				{input : '#punishmentAmountAssessment', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() < 0){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var disabledAll = function(){
		$("#cancelPartyAssessment").attr("disabled", "disabled");
		$("#savePartyAssessment").attr("disabled", "disabled");
		$("#rejectPartyAssessment").jqxRadioButton({disabled: true});
		$("#acceptPartyAssessment").jqxRadioButton({disabled: true});
		$("#yearCustomTimeAssessment").jqxDropDownList({disabled: true});
		$("#monthCustomTimeAssessment").jqxDropDownList({disabled: true});
		//$("#perfCriRateGradeIdAssessment").jqxDropDownList({disabled: true});
		$("#punishmentAmountAssessment").jqxNumberInput({disabled: true});
		$("#bonusAmountAssessment").jqxNumberInput({disabled: true});
		$("#allowanceRateAssessment").jqxNumberInput({disabled: true});
		$("#salaryRateAssessment").jqxNumberInput({disabled: true});
	};
	var enableAll = function(){
		$("#cancelPartyAssessment").removeAttr("disabled");
		$("#savePartyAssessment").removeAttr("disabled");
		$("#rejectPartyAssessment").jqxRadioButton({disabled: false});
		$("#acceptPartyAssessment").jqxRadioButton({disabled: false});
		$("#yearCustomTimeAssessment").jqxDropDownList({disabled: false});
		$("#monthCustomTimeAssessment").jqxDropDownList({disabled: false});
		//$("#perfCriRateGradeIdAssessment").jqxDropDownList({disabled: false});
		$("#punishmentAmountAssessment").jqxNumberInput({disabled: false});
		$("#bonusAmountAssessment").jqxNumberInput({disabled: false});
		$("#allowanceRateAssessment").jqxNumberInput({disabled: false});
		$("#salaryRateAssessment").jqxNumberInput({disabled: false});
	};
	var openWindow = function(){
		openJqxWindow($("#editEmplKPIAssessmentWindow"));
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}()); 

$(document).ready(function(){
	editEmplKPIAssessmentObj.init();
	addEmplToAssessmentKPIObj.init();
});