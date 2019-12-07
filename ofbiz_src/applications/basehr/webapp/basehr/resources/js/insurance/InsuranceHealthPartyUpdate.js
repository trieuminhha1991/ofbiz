var insuranceHealthUpdateObj = (function(){
	var _partyId = "";
	var _hospitalId = "";
	var init = function(){
		initInput();
		initDropDown();
		initDropDownDateTime();
		initWindow();
		initValidator();
		initEvent();
	};
	var initInput = function(){
		$("#insHealthUpdatePartyCode").jqxInput({width: '96%', height: 20, disabled: true});
		$("#insHealthUpdateFullName").jqxInput({width: '96%', height: 20, disabled: true});
		$("#insHealthUpdateSocialInsNbr").jqxInput({width: '96%', height: 20});
		$("#insHealthUpdateBirthDate").jqxDateTimeInput({width: '95%', height: 25, showFooter: true});
		$("#insHealthUpdateHospitalNameOld").jqxInput({width: '96%', height: 20, disabled: true});
		$("#insHealthUpdateHealthInsNbrOld").jqxInput({width: '96%', height: 20, disabled: true});
		$("#insHealthUpdateHospitalNameNew").jqxInput({width: '82%', height: 20, disabled: true});
		$("#insHealthUpdateHealthInsNbrNew").jqxInput({width: '96%', height: 20});
	};
	var initDropDown = function(){
		createJqxDropDownListExt($("#insHealthUpdateGender"), globalVar.genderArr, {valueMember: "genderId", displayMember: "description", height: 25, width: "100%"});
	};
	var initDropDownDateTime = function(){
		var monthData = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownListExt($("#insHealthUpdateMonthFromOld"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0, disabled: true});
		$("#insHealthUpdateYearFromOld").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
		createJqxDropDownListExt($("#insHealthUpdateMonthThruOld"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0, disabled: true});
		$("#insHealthUpdateYearThruOld").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});

		createJqxDropDownListExt($("#insHealthUpdateMonthFromNew"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0});
		$("#insHealthUpdateYearFromNew").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
		createJqxDropDownListExt($("#insHealthUpdateMonthThruNew"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0});
		$("#insHealthUpdateYearThruNew").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0, disabled: true});
	};
	var initWindow = function(){
		createJqxWindow($("#insuranceHealthUpdateWindow"), 800, 400);
	};
	var initEvent = function(){
		$("#insuranceHealthUpdateWindow").on('open', function(event){
			initOpen();
		});
		$("#insuranceHealthUpdateWindow").on('close', function(event){
			resetData();
		});
		$("#cancelInsHealthUpdate").click(function(){
			$("#insuranceHealthUpdateWindow").jqxWindow('close');
		});
		$("#insHealthUpdateChooseHospitalBtn").click(function(){
			openJqxWindow($("#hospitalListWindow"));
		});
		$("#hospitalListWindow").bind('chooseDataHospital', function(event){
			var isWindowOpened = $("#insuranceHealthUpdateWindow").jqxWindow('isOpen');
			if(isWindowOpened){
				var data = hospitalListObject.getSelectedHospitalData();
				if(data){
					$("#insHealthUpdateHospitalNameNew").val(data.hospitalName);
					_hospitalId = data.hospitalId;
				}
			}
		});
		$("#saveInsHealthUpdate").click(function(){
			var valid = $("#insuranceHealthUpdateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.UpdatePartyHealthInsuranceConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createPartyInsuranceHealth();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		});
		$("#insHealthUpdateMonthFromNew").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#insHealthUpdateYearFromNew").jqxNumberInput({disabled: true});
				}else{
					$("#insHealthUpdateYearFromNew").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#insHealthUpdateMonthThruNew").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#insHealthUpdateYearThruNew").jqxNumberInput({disabled: true});
				}else{
					$("#insHealthUpdateYearThruNew").jqxNumberInput({disabled: false});
				}
			}
		});
	};
	var createPartyInsuranceHealth = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'createPartyInsuranceHealth',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);	
					return;
				}
				$("#insuranceHealthUpdateWindow").jqxWindow('close');
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#jqxgrid").jqxGrid('updatebounddata');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var initOpen = function(){
		Loading.show('loadingMacro');
		var date = new Date();
		$("#insHealthUpdateYearFromNew").val(date.getFullYear());
		$("#insHealthUpdateYearThruNew").val(date.getFullYear());
		$.ajax({
			url: 'getPartyInsuranceHealthLastest',
			data: {partyId: _partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);	
					return;
				}
				var result = response.result? response.result: {}; 
				fillData(result);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var getData = function(){
		var data = {};
		data.partyId = _partyId;
		data.hospitalId = _hospitalId;
		data.insHealthCard = $("#insHealthUpdateHealthInsNbrNew").val();
		if($("#insHealthUpdateSocialInsNbr").val()){
			data.insuranceSocialNbr = $("#insHealthUpdateSocialInsNbr").val();
		}
		if($("#insHealthUpdateGender").val()){
			data.gender = $("#insHealthUpdateGender").val();
		}
		if($("#insHealthUpdateBirthDate").val()){
			data.birthDate = $("#insHealthUpdateBirthDate").jqxDateTimeInput('val', 'date').getTime();
		}
		var monthFrom = $("#insHealthUpdateMonthFromNew").val();
		var yearFrom = $("#insHealthUpdateYearFromNew").val();
		var monthTo = $("#insHealthUpdateMonthThruNew").val();
		var yearTo = $("#insHealthUpdateYearThruNew").val();
		var dateFrom = new Date(yearFrom, monthFrom, 1, 0, 0, 0, 0);
		var dateTo = new Date(yearTo, monthTo, 1, 0, 0, 0, 0);
		data.fromDate = dateFrom.getTime();
		data.thruDate = dateTo.getTime(); 
		return data;
	};
	var fillData = function(data){
		if(data.insHealthCard){
			$("#insHealthUpdateHealthInsNbrOld").val(data.insHealthCard);
		}else{
			$("#insHealthUpdateHealthInsNbrOld").val(uiLabelMap.NotSetting);
		}
		if(data.hospitalName){
			$("#insHealthUpdateHospitalNameOld").val(data.hospitalName);
		}else{
			$("#insHealthUpdateHospitalNameOld").val(uiLabelMap.NotSetting);
		}
		if(data.fromDate){
			var date = new Date(data.fromDate);
			$("#insHealthUpdateMonthFromOld").val(date.getMonth());
			$("#insHealthUpdateYearFromOld").val(date.getFullYear())
		}else{
			$("#insHealthUpdateMonthFromOld").jqxDropDownList({selectedIndex: 0});
			$("#insHealthUpdateYearFromOld").val(null)
		}
		if(data.thruDate){
			var date = new Date(data.thruDate);
			$("#insHealthUpdateMonthThruOld").val(date.getMonth());
			$("#insHealthUpdateYearThruOld").val(date.getFullYear())
		}else{
			$("#insHealthUpdateMonthThruOld").jqxDropDownList({selectedIndex: 0});
			$("#insHealthUpdateYearThruOld").val(null);
		}
	};
	var initValidator = function(){
		$("#insuranceHealthUpdateWindow").jqxValidator({
			rules: [
				{ input: '#insHealthUpdateHealthInsNbrNew', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#insHealthUpdateHospitalNameNew', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#insHealthUpdateYearFromNew', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var month = $("#insHealthUpdateMonthFromNew").val();
						if(month < 0){
							return false;
						}
						return true;
					}
				},
				{ input: '#insHealthUpdateYearFromNew', message: uiLabelMap.InsuranceHealthFromDateNewMustGreaterThanThruDateOld, action: 'keyup, change', 
					rule: function (input, commit) {
						var monthFromNew = $("#insHealthUpdateMonthFromNew").val();
						var yearFromNew = $("#insHealthUpdateYearFromNew").val();
						var monthToOld = $("#insHealthUpdateMonthThruOld").val();
						if(monthToOld > -1){
							var yearToOld = $("#insHealthUpdateYearThruOld").val();
							var dateFromNew = new Date(yearFromNew, monthFromNew, 1, 0, 0, 0, 0);
							var dateToOld = new Date(yearToOld, monthToOld, 1, 0, 0, 0, 0);
							if(dateFromNew <= dateToOld){
								return false;
							}
						}
						return true;
					}
				},
				{ input: '#insHealthUpdateYearThruNew', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						var month = $("#insHealthUpdateMonthThruNew").val();
						if(month < 0){
							return false;
						}
						return true;
					}
				},
				{ input: '#insHealthUpdateYearThruNew', message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'keyup, change', 
					rule: function (input, commit) {
						var monthFrom = $("#insHealthUpdateMonthFromNew").val();
						var yearFrom = $("#insHealthUpdateYearFromNew").val();
						var monthTo = $("#insHealthUpdateMonthThruNew").val();
						var yearTo = $("#insHealthUpdateYearThruNew").val();
						var dateFrom = new Date(yearFrom, monthFrom, 0, 0, 0, 0, 0);
						var dateTo = new Date(yearTo, monthTo, 0, 0, 0, 0, 0);
						if(dateTo <= dateFrom){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var resetData = function(){
		Grid.clearForm($("#insuranceHealthUpdateWindow"));
		$("#insHealthUpdateGender").jqxDropDownList('clearSelection');
		$("#insHealthUpdateMonthFromOld").jqxDropDownList({selectedIndex: 0});
		$("#insHealthUpdateMonthThruOld").jqxDropDownList({selectedIndex: 0});
		$("#insHealthUpdateMonthFromNew").jqxDropDownList({selectedIndex: 0});
		$("#insHealthUpdateMonthThruNew").jqxDropDownList({selectedIndex: 0});
		$("#insuranceHealthUpdateWindow").jqxValidator('hide');
		_partyId = "";
		_hospitalId = "";
	};
	var openWindow = function(data){
		_partyId = data.partyId;
		$("#insHealthUpdatePartyCode").val(data.partyCode);
		$("#insHealthUpdateSocialInsNbr").val(data.insuranceSocialNbr);
		$("#insHealthUpdateFullName").val(data.fullName);
		$("#insHealthUpdateGender").val(data.gender);
		$("#insHealthUpdateBirthDate").val(data.birthDate);
		openJqxWindow($("#insuranceHealthUpdateWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	insuranceHealthUpdateObj.init();
});