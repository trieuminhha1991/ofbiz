var kpiAssessmentObj = (function(){
	var init = function(){
		initJqxTreeButton();
		initJqxDateTimeInput();
		initJqxDropDownList();
		initInput();
		initJqxValidator();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerAjax"));
	};
	var initInput = function(){
		$("#perfCriteriaAssessmentName").jqxInput({width: '96%', height: 20});
	};
	var initJqxEditor = function(){
		$("#commentEdit").jqxEditor({ 
    		width: '98%',
            theme: 'olbiuseditor',
            tools: '',
            height: 120,
        });
	};
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 302, treeWidth: 302};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeEdit"), $("#dropDownButtonEdit"), globalVar.rootPartyArr, "tree", "treeChild", config);
		$('#jqxTreeEdit').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeEdit').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeEdit"), $("#dropDownButtonEdit"));
		});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownListBinding($("#periodTypeIdEdit"), [{name: 'periodTypeId'}, {name: 'description'}, {name: "uomId"}, {name: "periodLength"}], 
				'getKPIAssessmentPeriod', "listReturn", "periodTypeId", "description", '100%', 25);
		$("#periodTypeIdEdit").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var originalItem = item.originalItem;
				var periodLength = originalItem.periodLength;
				var uomId = originalItem.uomId;
				var fromDate = $("#fromDateEdit").jqxDateTimeInput('val', 'date');
				setThruDateValue(fromDate, periodLength, uomId);
			}
		});
	};
	var updatePeriodType = function(){
		var source = $("#periodTypeIdEdit").jqxDropDownList('source');
		source._source.url = "getKPIAssessmentPeriod";
		 $("#periodTypeIdEdit").jqxDropDownList('source', source);
	};
	var setThruDateValue = function(fromDate, periodLength, uomId){
		if(fromDate && periodLength && uomId){
			var year = fromDate.getFullYear();
			var month = fromDate.getMonth();
			var day = fromDate.getDate();
			if(uomId == "TF_yr"){
				year += periodLength;
			}else if(uomId == "TF_mon"){
				month += periodLength;
			}else if(uomId == "TF_wk"){
				day += periodLength * 7;
			}else if(uomId == "TF_day"){
				day += periodLength;
			}
			var date = new Date(year, month, day - 1);
			$("#thruDateEdit").val(date);
		}
	};
	var initJqxDateTimeInput = function(){
		$("#fromDateEdit").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateEdit").jqxDateTimeInput({width: '98%', height: 25});
		$("#fromDateEdit").on('valueChanged', function (event){
			var periodTypeSelected = $("#periodTypeIdEdit").jqxDropDownList('getSelectedItem');
			if(periodTypeSelected){
				var originalItem = periodTypeSelected.originalItem;
				var fromDate = event.args.date;
				setThruDateValue(fromDate, originalItem.periodLength, originalItem.uomId);
			}
		});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		};
		createJqxWindow($("#alterpopupWindow"), 490, 440, initContent);
		$("#alterpopupWindow").on('open', function(event){
			var nowDate = new Date();
			var fromDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), 1);
			$("#fromDateEdit").val(fromDate);
			$("#thruDateEdit").val(null);
		});
		$("#alterpopupWindow").on('close', function(event){
			Grid.clearForm($(this));
			clearDropDownContent($("#jqxTreeEdit"), $("#dropDownButtonEdit"));
		});
	};
	var initJqxValidator = function(){
		$("#alterpopupWindow").jqxValidator({
			rules: [
				{input : '#perfCriteriaAssessmentName', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#periodTypeIdEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#dropDownButtonEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						var item = $("#jqxTreeEdit").jqxTree('getSelectedItem');
						if(!item){
							return false;
						}
						return true;
					}
				},  
				{input : '#fromDateEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#thruDateEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},  
				{input : '#thruDateEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						var fromDate = $("#fromDateEdit").jqxDateTimeInput('val', 'date'); 
						var thruDate = input.jqxDateTimeInput('val', 'date'); 
						if(fromDate && thruDate){
							if(fromDate >= thruDate){
								return false;
							}
						}
						return true;
					}
				},  
			]
		});
	};
	var getData = function(){
		var data = {};
		var partySelected = $("#jqxTreeEdit").jqxTree('getSelectedItem');
		data.perfCriteriaAssessmentName = $("#perfCriteriaAssessmentName").val();
		data.partyId = partySelected.value;
		data.periodTypeId = $("#periodTypeIdEdit").val();
		data.fromDate = $("#fromDateEdit").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#thruDateEdit").jqxDateTimeInput('val', 'date').getTime();
		data.comment = $("#commentEdit").val();
		return data;
	};
	var initEvent = function(){
		$("#cancelAssessment").click(function(event){
			$("#alterpopupWindow").jqxWindow('close');
		});
		$("#saveAssessment").click(function(event){
			var valid = $("#alterpopupWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreatePerfCriteriaAssessment,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPerfCriteriaAssessment();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#addAssessmentPeriod").click(function(event){
			kpiAssessmentPeriodObj.openWindow();
		});
	};
	var createPerfCriteriaAssessment = function(){
		var data = getData();
		$("#ajaxLoading").show();
		$("#cancelAssessment").attr("disabled", "disabled");
		$("#saveAssessment").attr("disabled", "disabled");
		$.ajax({
			url: 'createPerfCriteriaAssessment',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#alterpopupWindow").jqxWindow('close');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			error: function(){
				
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoading").hide();
				$("#cancelAssessment").removeAttr("disabled");
				$("#saveAssessment").removeAttr("disabled");
			}
		});
	};
	return{
		init: init,
		updatePeriodType: updatePeriodType
	}
}());

var kpiAssessmentPeriodObj = (function(){
	var init = function(){
		$("#periodLength").jqxNumberInput({width: "98%", height: 25,  spinButtons: true, decimalDigits: 0, inputMode: 'simple'});
		createJqxDropDownList(globalVar.kpiAssessmentPeriodArr, $("#uomIdAssessPeriod"), "uomId", "abbreviation", 25, '95%');
		createJqxWindow($("#kpiAssessmentPeriodWindow"), 350, 180);
		$("#kPIAssessmentPeriodName").jqxInput({width: '96%', height: 20});
		create_spinner($("#spinnerKPIAssessPeriod"));
		initEvent();
		initJqxValidator();
	};
	var initEvent = function(){
		$("#kpiAssessmentPeriodWindow").on('open', function(event){
			$("#uomIdAssessPeriod").jqxDropDownList({selectedIndex: 0});
		});
		$("#kpiAssessmentPeriodWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#cancelKPIAssessPeriod").click(function(event){
			$("#kpiAssessmentPeriodWindow").jqxWindow('close');
		});
		$("#saveKPIAssessPeriod").click(function(event){
			var valid = $("#kpiAssessmentPeriodWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			disableAll();
			$("#loadingKPIAssessPeriod").show();
			var data = getData();
			$.ajax({
				url: 'createKPIAssessPeriod',
				data: data,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						kpiAssessmentObj.updatePeriodType();
						$("#kpiAssessmentPeriodWindow").jqxWindow('close');
					}else{
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]
						);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingKPIAssessPeriod").hide();
					enableAll();
				}
			});
		});
	};
	var getData = function(){
		var data = {};
		data.description = $("#kPIAssessmentPeriodName").val();
		data.periodLength = $("#periodLength").val();
		data.uomId = $("#uomIdAssessPeriod").val();
		return data;
	};
	var disableAll = function(){
		$("#saveKPIAssessPeriod").attr("disabled", "disabled");
		$("#cancelKPIAssessPeriod").attr("disabled", "disabled");
		$("#kPIAssessmentPeriodName").jqxInput({disabled: true});
		$("#periodLength").jqxNumberInput({disabled: true});
		$("#uomIdAssessPeriod").jqxDropDownList({disabled: true});
	};
	var enableAll = function(){
		$("#saveKPIAssessPeriod").removeAttr("disabled");
		$("#cancelKPIAssessPeriod").removeAttr("disabled");
		$("#kPIAssessmentPeriodName").jqxInput({disabled: false});
		$("#periodLength").jqxNumberInput({disabled: false});
		$("#uomIdAssessPeriod").jqxDropDownList({disabled: false});
	};
	var initJqxValidator = function(){
		$("#kpiAssessmentPeriodWindow").jqxValidator({
			rules: [
				{input : '#kPIAssessmentPeriodName', message: uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
				{input : '#kPIAssessmentPeriodName', message: uiLabelMap.HRContainSpecialSymbol, action : 'blur',
					rule : function(input, commit){
						if(input.val() && validationNameWithoutHtml(input.val())){
							return false;
						}
						return true;
					}
				},     
				{input : '#periodLength', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() <= 0){
							return false;
						}
						return true;
					}
				},     
				{input : '#uomIdAssessPeriod', message: uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},     
			]
		});
	};
	var openWindow = function(){
		openJqxWindow($("#kpiAssessmentPeriodWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	kpiAssessmentObj.init();
	kpiAssessmentPeriodObj.init();
});