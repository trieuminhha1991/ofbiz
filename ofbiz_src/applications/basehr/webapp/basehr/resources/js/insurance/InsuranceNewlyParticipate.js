var adjParticipateObj = (function(){
	var _partyId = "";
	var _hospitalId = "";
	var _partyIdFrom = null;
	var _windowWidth = 870;
	var _ancestorTreeArr = [];
	var init = function(){
		initSimpleInput();
		initDateTime();
		initNumberInput();
		initWindow();
		initEvent();
		initValidator();
		create_spinner($("#spinnerAdjustParticipate"));
	};
	var initSimpleInput = function(){
		$("#employeeName").jqxInput({width: '85%', height: 20, disabled: true});
		$("#hospitalNameAdjParicipate").jqxInput({width: '85%', height: 20, disabled: true});
		$("#socialInsNbrAdjParticipate").jqxInput({width: '96%', height: 20});
		$("#healthInsuranceNbr").jqxInput({width: '96%', height: 20});
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceType" + insuranceType.insuranceTypeId).jqxCheckBox({ width: 60, height: 25, 
				checked: isCompulsory == "Y", disabled: isCompulsory == "Y"});
			$("#rate" + insuranceType.insuranceTypeId).jqxNumberInput({ width: 60, height: 25, digits: 3, 
				symbolPosition: 'right', symbol: '%', spinButtons: false, disabled: true, decimal: insuranceType.employeeRate});
		});
	};
	var initNumberInput = function(){
		$("#insuranceSalAdjPariticipate").jqxNumberInput({ width: '98%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#insuranceAllowancePos").jqxNumberInput({ width: '98%', height: '25px',
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#allowanceSeniorExces").jqxNumberInput({ width: '98%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#allowanceSenior").jqxNumberInput({ width: '98%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
		$("#allowanceOther").jqxNumberInput({ width: '98%', height: '25px', 
			spinButtons: true, decimalDigits: 0, digits: 12, max: 999999999999, min: 0});
	};
	var initDropDown = function(){
		var expandCompleteFunc = function(){
			if(_ancestorTreeArr.length > 0){
				$("#jqxTreeAdjParticipate").jqxTree('expandItem', $("#" + _ancestorTreeArr.shift() + "_treeParticipate")[0]);
			}else if(_partyIdFrom){
				$("#jqxTreeAdjParticipate").jqxTree('selectItem', $("#" + _partyIdFrom + "_treeParticipate")[0]);
			}
		};
		var span8Width = $("#adjustParticipateWindow .span8").width() * 0.98;
		var config = {dropDownBtnWidth: '98%', treeWidth: span8Width, expandCompleteFunc: expandCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAdjParticipate"), $("#dropDownAdjParticipate"), globalVar.rootPartyArr, "treeParticipate", "treeChildParticipate", config);
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionTypeAdjParticipate"), "emplPositionTypeId", "description", 25, "98%");
		createJqxDropDownList(globalVar.genderArr, $("#genderAdjParticipate"), "genderId", "description", 25, "100%");
		$("#genderAdjParticipate").jqxDropDownList({disabled: true});
	};
	var initDateTime = function(){
		var monthDataRequire = [];
		var monthDataNotRequire = [{month: -1, description: "-----------"}];
		for(var i = 0; i < 12; i++){
			monthDataRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
			monthDataNotRequire.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		$("#birthDateParticipate").jqxDateTimeInput({width: '95%', height: 25, disabled: true});
		createJqxDropDownList(monthDataRequire, $("#monthNewlyParticipateFrom"), "month", "description", 25, 90);
		$("#yearNewlyParticipateFrom").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});

		createJqxDropDownList(monthDataNotRequire, $("#monthNewlyParticipateThru"), "month", "description", 25, 90);
		$("#yearNewlyParticipateThru").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		
		createJqxDropDownList(monthDataNotRequire, $("#monthStartParticipate"), "month", "description", 25, 90);
		$("#yearStartParticipate").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		
		createJqxDropDownList(monthDataNotRequire, $("#monthFromHealthIns"), "month", "description", 25, 90);
		$("#yearFromHealthIns").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});

		createJqxDropDownList(monthDataNotRequire, $("#monthThruHealthIns"), "month", "description", 25, 90);
		$("#yearThruHealthIns").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initWindow = function(){
		var initContent = function(){
			initDropDown();
		};
		createJqxWindow($("#adjustParticipateWindow"), _windowWidth, 520, initContent);
	};
	var initEvent = function(){
		$("#adjustParticipateWindow").on('open', function(event){
			initData();
		});
		$("#adjustParticipateWindow").on('close', function(event){
			resetData();
		});
		$("#monthStartParticipate").on('select', function(event){
			var args = event.args;
		    if(args){
		    	var item = args.item;
		    	var month = item.value;
		    	if(month < 0){
		    		$("#yearStartParticipate").jqxNumberInput({disabled: true});
		    	}else{
		    		$("#yearStartParticipate").jqxNumberInput({disabled: false});
		    	}
		    }
		});
		$("#monthFromHealthIns").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearFromHealthIns").jqxNumberInput({disabled: true});
				}else{
					$("#yearFromHealthIns").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthThruHealthIns").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearThruHealthIns").jqxNumberInput({disabled: true});
				}else{
					$("#yearThruHealthIns").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#monthNewlyParticipateThru").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var month = item.value;
				if(month < 0){
					$("#yearNewlyParticipateThru").jqxNumberInput({disabled: true});
				}else{
					$("#yearNewlyParticipateThru").jqxNumberInput({disabled: false});
				}
			}
		});
		$("#chooseEmplBtn").click(function(event){
			emplListInOrgObj.openWindow();//emplListInOrgObj is defined in ViewListInsEmplAdjustParticipate.js
		});
		$("#chooseHospitalBtn").click(function(event){
			openJqxWindow($("#hospitalListWindow"));
		});
		$(document).bind('chooseEmplAdjParticipate', function(event){
			var opened = $("#adjustParticipateWindow").jqxWindow('isOpen');
			if(opened){
				var data = emplListInOrgObj.getData();
				if(data){
					_partyId = data.partyId;
					$("#employeeName").val(data.fullName);
					getEmplDeptPositionOther();
				}
			}
		});
		$("#jqxTreeAdjParticipate").on('select', function(event){
			setDropdownContent(event.args.element, $("#jqxTreeAdjParticipate"), $("#dropDownAdjParticipate"));
		});
		$("#hospitalListWindow").bind('chooseDataHospital', function(event){
			var isWindowOpened = $("#adjustParticipateWindow").jqxWindow('isOpen');
			if(isWindowOpened){
				var data = hospitalListObject.getSelectedHospitalData();
				if(data){
					$("#hospitalNameAdjParicipate").val(data.hospitalName);
					_hospitalId = data.hospitalId;
				}
			}
		});
		$("#cancelAdjParticipate").click(function(event){
			$("#adjustParticipateWindow").jqxWindow('close');
		});
		$("#saveContinueAdjParticipate").click(function(event){
			var valid = $("#adjustParticipateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateNewlyParticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						 createEmplParicipate(false);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
		$("#saveAdjParticipate").click(function(event){
			var valid = $("#adjustParticipateWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CreateNewlyParticipateConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						 createEmplParicipate(true);
					}
				},
				{
					"label": uiLabelMap.CommonCancel,
					"class": "btn-danger icon-remove btn-small open-sans",
				}]
			);
		});
	};
	var initValidator = function(){
		$("#adjustParticipateWindow").jqxValidator({
			rules: [
				{ input: '#chooseEmplBtn', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						if(!_partyId || _partyId.length == 0){
							return false;
						}
						return true;
					}
				},     
				{ input: '#insuranceSalAdjPariticipate', message: uiLabelMap.ValueMustBeGreateThanZero , action: 'none',
					rule : function(input, commit){
						if($(input).val() <= 0){
							return false;
						}
						return true;
					}
				},     
				{ input: '#yearNewlyParticipateThru', message: uiLabelMap.ParticipateThruGreateThanParticipateFrom, action: 'blur',
					rule : function(input, commit){
						var monthThru = $("#monthNewlyParticipateThru").val();
						var yearThru = $("#yearNewlyParticipateThru").val();
						if(monthThru > -1){
							var monthFrom = $("#monthNewlyParticipateFrom").val();
							var yearFrom = $("#yearNewlyParticipateFrom").val();
							var fromDate = new Date(yearFrom, monthFrom, 1);
							var thruDate = new Date(yearThru, monthThru, 1);
							if(fromDate > thruDate){
								return false;
							}
						}
						return true;
					}
				},     
				{ input: '#hospitalNameAdjParicipate', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						var healthInsuranceNbr = $("#healthInsuranceNbr").val();
						if(healthInsuranceNbr.length > 0){
							if(_hospitalId.length == 0){
								return false;
							}
						}
						return true;
					}
				},
				{ input: '#monthFromHealthIns', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						var healthInsuranceNbr = $("#healthInsuranceNbr").val();
						if(healthInsuranceNbr.length > 0){
							if($(input).val() < 0){
								return false;
							}
						}
						return true;
					}
				},
				{ input: '#monthThruHealthIns', message: uiLabelMap.FieldRequired, action: 'none',
					rule : function(input, commit){
						var healthInsuranceNbr = $("#healthInsuranceNbr").val();
						if(healthInsuranceNbr.length > 0){
							if($(input).val() < 0){
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
		var year = date.getFullYear();
		$("#birthDateParticipate").val(null);
		$("#monthStartParticipate").jqxDropDownList({selectedIndex: 0});
		$("#monthFromHealthIns").jqxDropDownList({selectedIndex: 0});
		$("#monthThruHealthIns").jqxDropDownList({selectedIndex: 0});
		$("#monthNewlyParticipateThru").jqxDropDownList({selectedIndex: 0});
		$("#monthNewlyParticipateFrom").val(date.getMonth());
		$("#yearStartParticipate").val(year);
		$("#yearFromHealthIns").val(year);
		$("#yearThruHealthIns").val(year);
		$("#yearNewlyParticipateFrom").val(year);
		$("#yearNewlyParticipateThru").val(year);
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			$("#insuranceType" + insuranceType.insuranceTypeId).jqxCheckBox({checked: isCompulsory == "Y"});
			$("#rate" + insuranceType.insuranceTypeId).jqxNumberInput({ decimal: insuranceType.employeeRate});
		});
	};
	var resetData = function(){
		_partyId = "";
		_partyIdFrom = null;
		_hospitalId = "";
		Grid.clearForm($("#adjustParticipateWindow"));
		clearDropDownContent($('#jqxTreeAdjParticipate'), $("#dropDownAdjParticipate"));
		$('#jqxTreeAdjParticipate').jqxTree('collapseAll');
		_ancestorTreeArr = [];
	};
	var getEmplDeptPositionOther = function(){
		disableAll();
		$("#jqxTreeAdjParticipate").jqxTree('collapseAll');
		$.ajax({
			url: 'getEmplDeptPositionOther',
			data: {partyId: _partyId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					if(response.emplPositionTypeId){
						$("#emplPositionTypeAdjParticipate").val(response.emplPositionTypeId);
					}else{
						$("#emplPositionTypeAdjParticipate").jqxDropDownList('clearSelection');
					}
					$("#birthDateParticipate").val(new Date(response.birthDate));
					$("#genderAdjParticipate").val(response.gender);
					_partyIdFrom = response.partyIdFrom;
					if(response.ancestorTree && response.ancestorTree.length > 0){
						_ancestorTreeArr = response.ancestorTree;
						$("#jqxTreeAdjParticipate").jqxTree('expandItem', $("#" + _ancestorTreeArr.shift() + "_treeParticipate")[0]);
					}
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
				if(_partyIdFrom){
					$("#jqxTreeAdjParticipate").jqxTree('selectItem', $("#" + _partyIdFrom + "_treeParticipate")[0]);
				}
			}
		});
	};
	var getData = function(){
		var data = {};
		data.partyId = _partyId;
		data.partyGroupId = _partyIdFrom;
		data.emplPositionTypeId = $("#emplPositionTypeAdjParticipate").val();
		data.insuranceSocialNbr = $("#socialInsNbrAdjParticipate").val();
		var month = $("#monthNewlyParticipateFrom").val();
		var year = $("#yearNewlyParticipateFrom").val();
		data.fromDate = (new Date(year, month, 1)).getTime();
		month = $("#monthNewlyParticipateThru").val();
		year = $("#monthNewlyParticipateYear").val();
		if(month > -1){
			data.thruDate = (new Date(year, month, 1)).getTime();
		}
		var insuranceTypeArr = [];
		globalVar.insuranceTypeArr.forEach(function(insuranceType){
			var isCompulsory = insuranceType.isCompulsory;
			if("Y" != isCompulsory){
				if($("#insuranceType" + insuranceType.insuranceTypeId).jqxCheckBox('checked')){
					insuranceTypeArr.push(insuranceType.insuranceTypeId);
				}
			}
		});
		data.insuranceTypeNotCompulsory = JSON.stringify(insuranceTypeArr);
		month = $("#monthStartParticipate").val();
		year = $("#yearStartParticipate").val();
		if(month > -1){
			data.dateParticipateIns = (new Date(year, month, 1)).getTime();
		}
		data.insHealthCard = $("#healthInsuranceNbr").val();
		data.hospitalId = _hospitalId;
		month = $("#monthFromHealthIns").val();
		year = $("#yearFromHealthIns").val();
		if(month > -1){
			data.insHealthFromDate = (new Date(year, month, 1)).getTime();
		}
		month = $("#monthThruHealthIns").val();
		year = $("#yearThruHealthIns").val();
		if(month > -1){
			data.insHealthThruDate = (new Date(year, month, 1)).getTime();
		}
		data.amount = $("#insuranceSalAdjPariticipate").val();
		data.allowanceSeniority = $("#allowanceSenior").val();
		data.allowanceSeniorityExces = $("#allowanceSeniorExces").val();
		data.allowancePosition = $("#insuranceAllowancePos").val();
		data.allowanceOther = $("#allowanceOther").val();
		return data;
	};
	var createEmplParicipate = function(isCloseWindow){
		var data = getData();
		disableAll();
		$.ajax({
			url: 'createInsEmplNewlyAdjustParticipate',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
					if(isCloseWindow){
						$("#adjustParticipateWindow").jqxWindow('close');
					}else{
						resetData();
						initData();
					}
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
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
	var disableAll = function(){
		$("#loadingAdjustParticipate").show();
		$("#cancelAdjParticipate").attr("disabled", "disabled");
		$("#saveContinueAdjParticipate").attr("disabled", "disabled");
		$("#saveAdjParticipate").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAdjustParticipate").hide();
		$("#cancelAdjParticipate").removeAttr("disabled");
		$("#saveContinueAdjParticipate").removeAttr("disabled");
		$("#saveAdjParticipate").removeAttr("disabled");
	};
	var openWindow = function(){
		openJqxWindow($("#adjustParticipateWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function () {
	adjParticipateObj.init();
});