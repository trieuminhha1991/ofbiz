var createKpiRewardPunishmentObj = (function(){
	var _isUpdate = false;
	var _perfCriteriaPolicyId = "";
	var _rowIndex;
	var init = function(){
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxDateTimeInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
		create_spinner($("#spinnerAjax"));
	};
	var initJqxNumberInput = function(){
		$("#salaryRateEdit").jqxNumberInput({ width: '98%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#allowanceRateEdit").jqxNumberInput({ width: '98%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#bonusAmountEdit").jqxNumberInput({ width: '98%', height: '25px', digits: 12, decimalDigits: 0,  spinButtons: true, max: 999999999999});
		$("#punishmentAmountEdit").jqxNumberInput({ width: '98%', height: '25px', digits: 12, decimalDigits: 0,  spinButtons: true, max: 999999999999});
	};
	var initJqxDropDownList = function(){
		var datafield = [{name: 'perfCriteriaRateGradeId'}, {name: 'perfCriteriaRateGradeName'}, {name: 'fromRating'}, {name: 'toRating'}];
		createJqxDropDownListBinding($("#perfCriteriaRateGradeList"), datafield, "getPerfCriteriaRateGrade", 
				"listReturn", "perfCriteriaRateGradeId", "perfCriteriaRateGradeName", "100%", 25);
		
	};
	var initJqxValidator = function(){
		$("#alterpopupWindow").jqxValidator({
			rules: [
				{input : '#perfCriteriaRateGradeList', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
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
				{input : '#thruDateEdit', message : uiLabelMap.ValueMustBeGreateThanEffectiveDate, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return true;
						}
						var fromDate = $("#fromDateEdit").jqxDateTimeInput('val', 'date');
						var thruDate = input.jqxDateTimeInput('val', 'date');
						if(thruDate <= fromDate){
							return false;
						}
						return true;
					}
				},
	        ]
		});
	};
	var initJqxDateTimeInput = function(){
		$("#fromDateEdit").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateEdit").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
		$("#thruDateEdit").val(null);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#alterpopupWindow"), 450, 380);
		$("#alterpopupWindow").on('close', function(event){
			Grid.clearForm($(this));
			_isUpdate = false;
			_perfCriteriaPolicyId = "";
			_rowIndex = -1;
		});
		$("#alterpopupWindow").on('open', function(event){
			if(!_isUpdate){
				$("#salaryRateEdit").val(100);
				$("#allowanceRateEdit").val(100);
				$("#alterpopupWindow").jqxWindow('setTitle', uiLabelMap.CreateRewardPunishmentKPIPolicy);
			}else{
				$("#alterpopupWindow").jqxWindow('setTitle', uiLabelMap.EditRewardPunishmentKPIPolicy);
			}
		});
	};
	var initEvent = function(){
		$("#perfCriteriaRateGradeBtn").click(function(event){
			perfCriteriaRateGradeObj.openWindow(); //perfCriteriaRateGradeObj is defined in PerfCriteriaRateGradeList.js
		});
		$("#cancelKPIPolicy").click(function(event){
			$("#alterpopupWindow").jqxWindow('close');
		});
		$("#saveKPIPolicy").click(function(event){
			var valid = $("#alterpopupWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(!_isUpdate){
				bootbox.dialog(uiLabelMap.ConfirmCreatePerfCriteriaPolicy,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createPerfCriteriaPolicy();
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}else{
				if(_rowIndex > -1){
					var data = getData();
					data.perfCriteriaPolicyId = _perfCriteriaPolicyId;
					var rowid = $("#jqxgrid").jqxGrid('getrowid', _rowIndex);
					$("#jqxgrid").jqxGrid('updaterow', rowid, data);
					$("#alterpopupWindow").jqxWindow('close');
				}
			}
		});
		$("#jqxgrid").on('rowdoubleclick', function(event){
			_isUpdate = true;
			var args = event.args;
			var boundIndex = args.rowindex;
			_rowIndex = boundIndex;
			var rowData = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			setData(rowData);
			openJqxWindow($("#alterpopupWindow"));
		});
	};
	var setData = function(data){
		_perfCriteriaPolicyId = data.perfCriteriaPolicyId;
		$("#perfCriteriaRateGradeList").val(data.perfCriteriaRateGradeId);
		$("#salaryRateEdit").val(data.salaryRate);
		$("#allowanceRateEdit").val(data.allowanceRate);
		$("#bonusAmountEdit").val(data.bonusAmount);
		$("#punishmentAmountEdit").val(data.punishmentAmount);
		$("#fromDateEdit").val(data.fromDate);
		$("#thruDateEdit").val(data.thruDate);
	};
	var createPerfCriteriaPolicy = function(){
		$("#ajaxLoading").show();
		$("#cancelKPIPolicy").attr("disabled", "disabled");
		$("#saveKPIPolicy").attr("disabled", "disabled");
		var data = getData();
		$.ajax({
			url: 'createPerfCriteriaPolicy',
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
				$("#cancelKPIPolicy").removeAttr("disabled");
				$("#saveKPIPolicy").removeAttr("disabled");
			}
		});
	};
	
	var getData = function(){
		var data = {};
		data.perfCriteriaRateGradeId = $("#perfCriteriaRateGradeList").val();
		data.salaryRate = $("#salaryRateEdit").val();
		data.allowanceRate = $("#allowanceRateEdit").val();
		data.bonusAmount = $("#bonusAmountEdit").val();
		data.punishmentAmount = $("#punishmentAmountEdit").val();
		data.fromDate = $("#fromDateEdit").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDateEdit").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
	var updatePerfCriteriaRateGradeDropDown = function(){
		updateJqxDropDownListBinding($("#perfCriteriaRateGradeList"), "getPerfCriteriaRateGrade");
	};
	return{
		init: init,
		updatePerfCriteriaRateGradeDropDown: updatePerfCriteriaRateGradeDropDown
	}
}());
$(document).ready(function(){
	createKpiRewardPunishmentObj.init();
});