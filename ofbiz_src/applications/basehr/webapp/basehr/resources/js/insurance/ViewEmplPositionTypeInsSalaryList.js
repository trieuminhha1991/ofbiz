var viewEmplPosTypeInsSalaryObject = (function(){
	var init = function(){
			initJqxGridEvent();
			initJqxNumberInput();
			initJqxDateTimeInput();
			initJqxDropdownlist();
			initJqxValidator();
			initJqxWindow();
			initBtnEvent();
			initJqxNotification();
	};
	
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			$("#jqxDatimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
			$("#jqxDatimeInput").on('change', function (event){
				var selection = $("#jqxDatimeInput").jqxDateTimeInput('getRange');
				refreshGridData(selection.from, selection.to);
			});
			$("#jqxDatimeInput").jqxDateTimeInput('setRange', new Date(globalVar.startMonth), new Date(globalVar.endMonth));
		});
	};
	
	var initJqxValidator = function(){
		$("#popupAddRow").jqxValidator({
			rules:[
				{
					input: "#emplPositionTypeId",
					message: uiLabelMap.FieldRequired,
					rule: function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#insuranceSocialSalary",
			  		message: uiLabelMap.FieldRequired + " " + uiLabelMap.CommonAnd + " " + uiLabelMap.ValueNotLessThanZero,
			  		rule: function(input, commit){
						if(!input.val() || input.val() < 0){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#insuranceAllowancePosition",
			  		message: uiLabelMap.ValueNotLessThanZero,
			  		rule: function(input, commit){
						if(input.val() && input.val() < 0){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#insuranceAllowanceSeniority",
			  		message: uiLabelMap.ValueNotLessThanZero,
			  		rule: function(input, commit){
						if(input.val() && input.val() < 0){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#insuranceAllowanceSeniorityExces",
			  		message: uiLabelMap.ValueNotLessThanZero,
			  		rule: function(input, commit){
						if(input.val() && input.val() < 0){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#insuranceOtherAllowance",
			  		message: uiLabelMap.ValueNotLessThanZero,
			  		rule: function(input, commit){
						if(input.val() && input.val() < 0){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#fromDate",
			  		message: uiLabelMap.FieldRequired,
			  		rule: function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#thruDate",
			  		message: uiLabelMap.ThruDateMustGreaterThanFromDate,
			  		rule: function(input, commit){
						var thruDate = input.jqxDateTimeInput('val', 'date');		  			
						if(thruDate){
							var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
							if(thruDate.getTime() < fromDate.getTime()){
								return false;
							}
							return true;
						}
						return true;
					}
			  	},
			  	{
			  		input: "#periodTypeId",
			  		message: uiLabelMap.FieldRequired,
			  		rule: function(input, commit){
			  			if(!input.val()){
			  				return false;
			  			}
			  			return true;
			  		}
			  	}
			]
		});
	};
	
	var initJqxNumberInput = function(){
		$("#insuranceSocialSalary").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', decimalDigits: 0});
		$("#insuranceAllowancePosition").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', decimalDigits: 0});
		$("#insuranceOtherAllowance").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, theme: 'olbius', decimalDigits: 0});
		$("#insuranceAllowanceSeniority").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', digits: 3, symbolPosition: 'right', symbol: '%'});
		$("#insuranceAllowanceSeniorityExces").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', digits: 3, symbolPosition: 'right', symbol: '%'});
	};

	var initJqxDropdownlist = function(){
		createJqxDropDownList(emplPositionTypeArr, $("#emplPositionTypeId"), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(periodTypeArr, $("#periodTypeId"), "periodTypeId", "description", 25, '98%');
	};

	var initJqxDateTimeInput = function(){
		$("#fromDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
		$("#thruDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius', showFooter: true});
		$("#thruDate").val(null);
	};

	var initJqxNotification = function(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#notifyContainer"});
	};

	var initBtnEvent = function(){
		$("#btnCancel").click(function(event){
			$("#popupAddRow").jqxWindow('close');
		});
		
		$("#btnSave").click(function(event){
			var valid = $("#popupAddRow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var emplPositionTypeSelect = $("#emplPositionTypeId").jqxDropDownList('getSelectedItem');
			bootbox.dialog(uiLabelMap.ConfirmAddSettingInsuranceSalary +" " + emplPositionTypeSelect.label + "?",
				[
				{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-smalli icon-ok",
	    		    "callback": function() {
	    		    	createInsuranceSalary();
	    		    }
				}, 
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-smalli icon-remove",
	    		    "callback": function() {
	    		    }
	    		}
				]		
			);
		});
	};

	var createInsuranceSalary = function(){
		var data = {};
		data.emplPositionTypeId = $("#emplPositionTypeId").jqxDropDownList('getSelectedItem').value;
		data.insuranceSalary = $("#insuranceSocialSalary").val();
		data.allowancePosition = $("#insuranceAllowancePosition").val();
		data.allowanceSeniority = $("#insuranceAllowanceSeniority").val()/100;
		data.allowanceSeniorityExces = $("#insuranceAllowanceSeniorityExces").val()/100;
		data.allowanceOther = $("#insuranceOtherAllowance").val();
		data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
		data.periodTypeId = $("#periodTypeId").val();
		var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = $("#thruDate").jqxDateTimeInput('val', 'date').getTime();
		}
		
		$.ajax({
			url: 'createEmplPositionTypeInsuranceSalary',
			data: data,
			type: 'POST',
			success: function(response){
				$("#popupAddRow").jqxWindow('close');
				$("#jqxNtf").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			}
		});
	};

	var refreshGridData = function(fromDate, thruDate){
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = 'jqxGeneralServicer?sname=JQGetEmplPosTypeInsuranceSalary&hasrequest=Y&fromDate=' + fromDate.getTime() + '&thruDate=' + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', source);
	};
	
	var initJqxWindow = function(){
		$("#popupAddRow").jqxWindow({showCollapseButton: false,autoOpen: false,
			maxWidth: 500, height: 460, width: 500, isModal: true, theme:'olbius',
			initContent: function(){
			}	
		});
		$("#popupAddRow").on('open', function(event){
			$("#fromDate").val(new Date(globalVar.startMonth));
		});
		
		$("#popupAddRow").on('close', function(event){
			Grid.clearForm($(this));
			$("#popupAddRow").jqxValidator('hide');
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewEmplPosTypeInsSalaryObject.init();
});

