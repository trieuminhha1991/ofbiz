var addEmpleaveReasonTypeObject = (function(){
	var init = function(){
		initJqxCheckBox();
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxInput();
		initBtnEvent();
		initJqxValidator();
		initJqxWindow();
	};
	
	var initBtnEvent = function(){
		$("#cancelCreate").click(function(event){
			$("#alterpopupWindow").jqxWindow('close');
		});
		$("#saveCreate").click(function(event){
			var valid = $("#alterpopupWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AddNewRowConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		createEmplLeaveReasonType();   	
	    		 		$("#alterpopupWindow").jqxWindow('close');
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		    "callback": function() {
	    		    	$("#alterpopupWindow").jqxWindow('close');
	    		    }
	    		}]		
			);
		});
	};
	
	var createEmplLeaveReasonType = function(){
		var dataSubmit = {};
		dataSubmit.description = $("#descriptionAddNew").val();
		dataSubmit.emplTimekeepingSignId = $("#emplTimekeepingSignIdAddNew").val();
		var isBenefitSocialIns = $("#socialInsuranceBenefits").jqxCheckBox('checked');
		var isBenefitSal = $("#benefitSal").jqxCheckBox('checked');
		if(isBenefitSocialIns){
			dataSubmit.isBenefitSocialIns = "Y";
		}
		if(isBenefitSal){
			dataSubmit.isBenefitSal = "Y";
		}
		var rateBenefit = $("#rateBenefit").val();
		if(rateBenefit > 0){
			dataSubmit.rateBenefit = rateBenefit/100;
		}
		 $("#jqxgrid").jqxGrid('addRow', null, dataSubmit, "first");
	};
	
	var initJqxValidator = function(){
		$("#alterpopupWindow").jqxValidator({
			rules: [
				{
					input: '#descriptionAddNew',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: 'required'
				},
				{
					input : '#descriptionAddNew',
					message : uiLabelMap.InvalidChar,
					action : 'blur',
					rule : function(input, commit){
		        		var val = input.val();
		        		if(val){
		        			if(validationNameWithoutHtml(val)){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
				},
				{
					input: '#emplTimekeepingSignIdAddNew',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	
	var initJqxInput = function(){
		$("#descriptionAddNew").jqxInput({width: '96%', height: 19});
	};
	
	var initJqxDropDownList = function(){
		var renderer = function (index, label, value) {
			var dataRecord = globalVar.emplTimekeepingSignArr[index];
			return dataRecord.sign + ' (' + dataRecord.description  + ')';
		};
		createJqxDropDownList(globalVar.emplTimekeepingSignArr, $("#emplTimekeepingSignIdAddNew"), "emplTimekeepingSignId", "sign", 25, "98%", renderer);
		$("#emplTimekeepingSignIdAddNew").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var data = globalVar.emplTimekeepingSignArr[index];
				if(typeof(data.rateBenefit) != 'undefined'){
					$("#rateBenefit").val(data.rateBenefit);
				}else{
					$("#rateBenefit").val(0);
				}
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#alterpopupWindow"), 470, 300);
		$("#alterpopupWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#alterpopupWindow").jqxValidator('hide');
		});
	};
	
	var initJqxNumberInput = function(){
		$("#rateBenefit").jqxNumberInput({ width: '98%', height: '25px', digits: 3, 
			symbolPosition: 'right', symbol: '%',  spinButtons: false, inputMode: 'simple' });
	};
	
	var initJqxCheckBox = function(){
		$("#socialInsuranceBenefits").jqxCheckBox({});
		$("#benefitSal").jqxCheckBox({});
	};
	return{
		init: init
	}
}());

var editEmpleaveReasonTypeObject = (function(){
	var emplLeaveReasonTypeId;
	var editRowId;
	var init = function(){
		initJqxCheckBox();
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxInput();
		initBtnEvent();
		initJqxValidator();
		initJqxWindow();
	};
	
	var initBtnEvent = function(){
		$("#cancelUpdate").click(function(event){
			$("#editEmplLeaveReasonWindow").jqxWindow('close');
		});
		$("#saveUpdate").click(function(event){
			var valid = $("#editEmplLeaveReasonWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			updateEmplLeaveReasonType();
			$("#editEmplLeaveReasonWindow").jqxWindow('close');
		});
	};
	
	var updateEmplLeaveReasonType = function(){
		var dataSubmit = {};
		dataSubmit.emplLeaveReasonTypeId = emplLeaveReasonTypeId;
		dataSubmit.description = $("#descriptionUpdate").val();
		dataSubmit.emplTimekeepingSignId = $("#emplTimekeepingSignIdUpdate").val();
		var isBenefitSocialIns = $("#socialInsuranceBenefitsUpdate").jqxCheckBox('checked');
		var isBenefitSal = $("#benefitSalUpdate").jqxCheckBox('checked');
		if(isBenefitSocialIns){
			dataSubmit.isBenefitSocialIns = "Y";
		}
		if(isBenefitSal){
			dataSubmit.isBenefitSal = "Y";
		}
		var rateBenefit = $("#rateBenefitUpdate").val();
		if(rateBenefit > 0){
			dataSubmit.rateBenefit = rateBenefit/100;
		}
		$("#jqxgrid").jqxGrid('updaterow', editRowId, dataSubmit);
	};
	
	var initJqxValidator = function(){
		$("#editEmplLeaveReasonWindow").jqxValidator({
			rules: [
				{
					input: '#descriptionUpdate',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: 'required'
				},
				{
					input : '#descriptionUpdate',
					message : uiLabelMap.InvalidChar,
					action : 'blur',
					rule : function(input, commit){
		        		var val = input.val();
		        		if(val){
		        			if(validationNameWithoutHtml(val)){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
				},
				{
					input: '#emplTimekeepingSignIdUpdate',
					message: uiLabelMap.FieldRequired,
					action: 'blur',
					rule: function (input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	
	var initJqxInput = function(){
		$("#descriptionUpdate").jqxInput({width: '96%', height: 19});
	};
	
	var initJqxDropDownList = function(){
		var renderer = function (index, label, value) {
			var dataRecord = globalVar.emplTimekeepingSignArr[index];
			return dataRecord.sign + ' (' + dataRecord.description  + ')';
		};
		createJqxDropDownList(globalVar.emplTimekeepingSignArr, $("#emplTimekeepingSignIdUpdate"), "emplTimekeepingSignId", "sign", 25, "98%", renderer);
		$("#emplTimekeepingSignIdUpdate").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var data = globalVar.emplTimekeepingSignArr[index];
				if(typeof(data.rateBenefit) != 'undefined'){
					$("#rateBenefitUpdate").val(data.rateBenefit);
				}else{
					$("#rateBenefitUpdate").val(0);
				}
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#editEmplLeaveReasonWindow"), 470, 300);
		$("#editEmplLeaveReasonWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#editEmplLeaveReasonWindow").jqxValidator('hide');
		});
	};
	
	var initJqxNumberInput = function(){
		$("#rateBenefitUpdate").jqxNumberInput({ width: '98%', height: '25px', digits: 3, 
			symbolPosition: 'right', symbol: '%',  spinButtons: false, inputMode: 'simple' });
	};
	
	var initJqxCheckBox = function(){
		$("#socialInsuranceBenefitsUpdate").jqxCheckBox({});
		$("#benefitSalUpdate").jqxCheckBox({});
	};
	
	var setData = function(data){
		editRowId = data.rowid;
		emplLeaveReasonTypeId = data.emplLeaveReasonTypeId;
		if(data.isBenefitSocialIns){
			$("#socialInsuranceBenefitsUpdate").jqxCheckBox({checked: true});
		}else{
			$("#socialInsuranceBenefitsUpdate").jqxCheckBox({checked: false});
		}
		if(data.isBenefitSal){
			$("#benefitSalUpdate").jqxCheckBox({checked: true});
		}else{
			$("#benefitSalUpdate").jqxCheckBox({checked: false});
		}
		$("#emplTimekeepingSignIdUpdate").val(data.emplTimekeepingSignId);
		if(typeof(data.rateBenefit) != 'undefined'){
			var tempRate = data.rateBenefit * 100;
			$("#rateBenefitUpdate").val(tempRate);
		}else{
			$("#rateBenefitUpdate").val(0);
		}
		//console.log(data);
		$("#descriptionUpdate").val(data.description);
	};
	
	var openEditWindow = function(){
		openJqxWindow($("#editEmplLeaveReasonWindow"));
	};
	
	return{
		init: init,
		setData: setData,
		openEditWindow: openEditWindow
	}
}());

$(document).ready(function () {
	addEmpleaveReasonTypeObject.init();
	editEmpleaveReasonTypeObject.init();
});