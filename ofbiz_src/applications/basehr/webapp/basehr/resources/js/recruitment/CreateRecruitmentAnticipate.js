var createRecruitmentAnticipate = (function() {
	var _updateGrid = false;
	var _isWindowInit = false;
	var init = function() {
		initJqxDropDownList();
		initJqxTreeButton();
		initInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerAddNew"));
	};
	var initJqxDropDownList = function() {
		createJqxDropDownList([], $("#emplPositionTypeAddNew"),"emplPositionTypeId", "description", 25, '99%');
	};
	var initJqxTreeButton = function() {
		var config = { dropDownBtnWidth : 200, treeWidth : 200};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"),$("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
		$('#jqxTreeAddNew').on('select', function(event) {
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"),$("#dropDownButtonAddNew"));
			var partyId = item.value;
			var year = $("#yearAdd").val();
			updateEmplPositionType(partyId, year);
		});
	};
	var initJqxWindow = function() {
		createJqxWindow($("#addRecruitmentAnticipateWindow"), 660, 470);
	};
	var initInput = function() {
		for (var i = 1; i <= 12; i++) {
			$("#month" + i).jqxNumberInput({width : '98%', height : 25, inputMode : 'simple',decimalDigits : 0});
		}
		$("#yearAdd").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var initEvent = function() {
		$("#addRecruitmentAnticipateBtn").click(function(event) {
			openWindow();
		});
		$("#cancelCreate").click(function(event) {
			$("#addRecruitmentAnticipateWindow").jqxWindow('close');
		});
		$("#saveCreate").click(function(event) {
			var valid = $("#addRecruitmentAnticipateWindow").jqxValidator('validate');
			if (!valid) {
				return;
			}
			createRecruitmentAnticipate(true);
		});
		$("#saveAndContinue").click(function(event) {
			var valid = $("#addRecruitmentAnticipateWindow").jqxValidator('validate');
			if (!valid) {
				return;
			}
			createRecruitmentAnticipate(false);
		});
		$("#yearAdd").on('valueChanged', function(event) {
			var year = event.args.value;
			var selectedItem = $('#jqxTreeAddNew').jqxTree('getSelectedItem');
			if(selectedItem){
				var partyId = selectedItem.value;
				updateEmplPositionType(partyId, year);
			}
		});
		$("#addRecruitmentAnticipateWindow").on('open', function(event) {
			var date = new Date();
			if (globalVar.rootPartyArr.length > 0) {
				$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
			}
			$("#yearAdd").val(date.getFullYear());
			_updateGrid = false;
			_isWindowInit = true;
			updateEmplPositionType(globalVar.rootPartyArr[0].partyId, date.getFullYear());
		});
		$("#addRecruitmentAnticipateWindow").on('close',function(event) {
			_isWindowInit = false;
			clearDropDownContent($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			Grid.clearForm($(this));
			if (_updateGrid) {
				$("#jqxgrid").jqxGrid('updatebounddata');
			}
			for (var i = 1; i <= 12; i++) {
				$("#month" + i).jqxNumberInput('val', 0);
			}
		});
	};
	var updateEmplPositionType = function(partyId, year){
		if(_isWindowInit){
			$("#emplPositionTypeAddNew").jqxDropDownList({disabled : true});
			$.ajax({
				url : 'getListAllEmplPositionTypeOfParty',
				data : {partyId : partyId, year : year},
				type : 'POST',
				success : function(response) {
					if (response.responseMessage == "success") {
						updateSourceDropdownlist($("#emplPositionTypeAddNew"),response.listReturn);
					}
				},
				complete : function(jqXHR, textStatus) {
					$("#emplPositionTypeAddNew").jqxDropDownList({disabled : false});
				}
			});
		}
	};
	var createRecruitmentAnticipate = function(isCloseWindow) {
		var data = getData();
		disableAll();
		$("#loadingAddNew").show();
		$.ajax({
			url : 'createRecruitmentAnticipate',
			type : 'POST',
			data : data,
			success : function(response) {
				if (response.responseMessage == "success") {
					_updateGrid = true;
					if (isCloseWindow) {
						$("#addRecruitmentAnticipateWindow").jqxWindow('close');
					}
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
				} else {
					bootbox.dialog(response.errorMessage,
						[
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						} 
						]
					);
				}
			},
			complete : function(jqXHR, textStatus) {
				$("#loadingAddNew").hide();
				enableAll();
			}
		});
	};

	var getData = function() {
		var dataSubmit = {};
		dataSubmit.emplPositionTypeId = $("#emplPositionTypeAddNew").val();
		dataSubmit.year = $("#yearAdd").val();
		var item = $('#jqxTreeAddNew').jqxTree('getSelectedItem');
		dataSubmit.partyId = item.value;
		for (var i = 1; i <= 12; i++) {
			dataSubmit["month" + i] = $("#month" + i).val();
		}
		return dataSubmit;
	};

	var disableAll = function() {
		$("#emplPositionTypeAddNew").jqxDropDownList({disabled : true});
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled : true});
		$("#yearAdd").jqxNumberInput({disabled : true});
		for (var i = 1; i <= 12; i++) {
			$("#month" + i).jqxNumberInput({disabled : true});
		}
		$("#cancelCreate").attr("disabled", 'disabled');
		$("#saveCreate").attr("disabled", 'disabled');
		$("#saveAndContinue").attr("disabled", 'disabled');
	};
	var enableAll = function() {
		$("#emplPositionTypeAddNew").jqxDropDownList({disabled : false});
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled : false});
		$("#yearAdd").jqxNumberInput({disabled : false});
		for (var i = 1; i <= 12; i++) {
			$("#month" + i).jqxNumberInput({disabled : false});
		}
		$("#cancelCreate").removeAttr("disabled");
		$("#saveCreate").removeAttr("disabled");
		$("#saveAndContinue").removeAttr("disabled");
	};
	var initJqxValidator = function() {
		var rules = [ {
			input : '#emplPositionTypeAddNew',
			message : uiLabelMap.FieldRequired,
			action : 'blur',
			rule : function(input, commit) {
				if (!input.val()) {
					return false;
				}
				return true;
			}
		}, {
			input : '#yearAdd',
			message : uiLabelMap.FieldRequired,
			action : 'blur',
			rule : function(input, commit) {
				if (!input.val()) {
					return false;
				}
				return true;
			}
		} ];
		for (var i = 1; i <= 12; i++) {
			rules.push({
				input : '#month' + i,
				message : uiLabelMap.ValueMustBeGreateThanZero,
				action : 'blur',
				rule : function(input, commit) {
					if (input.val() < 0) {
						return false;
					}
					return true;
				}
			});
		}
		$("#addRecruitmentAnticipateWindow").jqxValidator({
			rules : rules
		});
	};
	var openWindow = function() {
		openJqxWindow($("#addRecruitmentAnticipateWindow"));
	};
	return {
		init : init,
		openWindow : openWindow
	}
}());
$(document).ready(function() {
	createRecruitmentAnticipate.init();
});