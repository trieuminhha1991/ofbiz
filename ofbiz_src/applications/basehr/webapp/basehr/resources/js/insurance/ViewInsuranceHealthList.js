var insuraneHealthListObject = (function(){
	var init = function(){
		initJqxGridEvent();
		initContextMenu();
	};
	var initJqxGridEvent = function(){
		$("#jqxgrid").on('loadCustomControlAdvance', function(){
			$("#jqxDatimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
			$("#jqxDatimeInput").on('valueChanged', function (event){
				var selection = $("#jqxDatimeInput").jqxDateTimeInput('getRange');
				refreshGridData(selection.from, selection.to);
			});
			$("#jqxDatimeInput").jqxDateTimeInput('setRange', new Date(globalVar.startYear), new Date(globalVar.endYear));
		});
	};
	
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 160);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'edit'){
            	editPartyHealthInsInfoObj.openWindow(dataRecord);
            }
		});
	};
	
	var refreshGridData = function(fromDate, thruDate){
		var source = $("#jqxgrid").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQInsuranceHealthList&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime();
		$("#jqxgrid").jqxGrid('source', source);
	};
	
	return{
		init: init
	}
}());

var editPartyHealthInsInfoObj = (function(){
	var _partyHealthInsId = "";
	var _hospitalId = "";
	var init = function(){
		initInput();
		initDropDownDateTime();
		initWindow();
		initEvent();
		initValidator();
	};
	var initInput = function(){
		$("#editHealthInsPartyId").jqxInput({width: '96%', height: 20, disabled: true});
		$("#editHealthInsNbr").jqxInput({width: '96%', height: 20});
		$("#editHealthInsHospitalName").jqxInput({width: '84%', height: 20, disabled: true});
	};
	var initDropDownDateTime = function(){
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownListExt($("#editHealthInsMonthFrom"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0});
		$("#editHealthInsYearFrom").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
		createJqxDropDownListExt($("#editHealthInsMonthThru"), monthData, {valueMember: "month", displayMember: "description", width: 90, height: 25, selectedIndex: 0});
		$("#editHealthInsYearThru").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});

	};
	var initWindow = function(){
		createJqxWindow($("#EditPartyHealthInsWindow"), 450, 300);
	};
	var openWindow = function(data){
		fillData(data);
		openJqxWindow($("#EditPartyHealthInsWindow"));
	};
	var fillData = function(data){
		_partyHealthInsId = data.partyHealthInsId;
		_hospitalId = data.hospitalId;
		$("#editHealthInsPartyId").val(data.fullName);
		$("#editHealthInsNbr").val(data.insHealthCard);
		var fromDate = data.fromDate;
		var thruDate = data.thruDate;
		$("#editHealthInsMonthFrom").val(fromDate.getMonth());
		$("#editHealthInsYearFrom").val(fromDate.getFullYear());
		$("#editHealthInsMonthThru").val(thruDate.getMonth());
		$("#editHealthInsYearThru").val(thruDate.getFullYear());
		$("#editHealthInsHospitalName").val(data.hospitalName);
	};
	var initEvent = function(){
		$("#editHealthInsChooseHospitalBtn").click(function(){
			openJqxWindow($("#hospitalListWindow"));
		});
		$("#hospitalListWindow").bind('chooseDataHospital', function(event){
			var data = hospitalListObject.getSelectedHospitalData();
			if(data){
				$("#editHealthInsHospitalName").val(data.hospitalName);
				_hospitalId = data.hospitalId;
			}
		});
		$("#EditPartyHealthInsWindow").on('close', function(event){
			Grid.clearForm($("#EditPartyHealthInsWindow"));
		});
		$("#cancelEditHealthIns").click(function(event){
			$("#EditPartyHealthInsWindow").jqxValidator('hide');
			$("#EditPartyHealthInsWindow").jqxWindow('close');
			_partyHealthInsId = "";
			_hospitalId = "";
		});
		$("#saveEditHealthIns").click(function(event){
			var valid = $("#EditPartyHealthInsWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			Loading.show('loadingMacro');
			var data = getData();
			$.ajax({
				url: 'updatePartyInsuranceHealth',
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
					$("#EditPartyHealthInsWindow").jqxWindow('close');
					Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
					$("#jqxgrid").jqxGrid('updatebounddata');
				},
				complete: function(){
					Loading.hide('loadingMacro');
				}
			});
		});
	};
	
	var getData = function(){
		var data = {};
		data.partyHealthInsId = _partyHealthInsId;
		data.hospitalId = _hospitalId;
		data.insHealthCard = $("#editHealthInsNbr").val();
		var monthFrom = $("#editHealthInsMonthFrom").val();
		var yearFrom = $("#editHealthInsYearFrom").val();
		var monthTo = $("#editHealthInsMonthThru").val();
		var yearTo = $("#editHealthInsYearThru").val();
		var dateFrom = new Date(yearFrom, monthFrom, 1, 0, 0, 0, 0);
		var dateTo = new Date(yearTo, monthTo, 1, 0, 0, 0, 0);
		data.fromDate = dateFrom.getTime();
		data.thruDate = dateTo.getTime(); 
		return data;
	};
	
	var initValidator = function(){
		$("#EditPartyHealthInsWindow").jqxValidator({
			rules: [
				{ input: '#editHealthInsNbr', message: uiLabelMap.FieldRequired, action: 'keyup, change', 
					rule: function (input, commit) {
						if(!input.val()){
							return false
						}
						return true;
					}
				},
				{ input: '#editHealthInsYearThru', message: uiLabelMap.ThruDateMustBeAfterFromDate, action: 'keyup, change', 
					rule: function (input, commit) {
						var monthFrom = $("#editHealthInsMonthFrom").val();
						var yearFrom = $("#editHealthInsYearFrom").val();
						var monthTo = $("#editHealthInsMonthThru").val();
						var yearTo = $("#editHealthInsYearThru").val();
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
	return{
		init: init,
		openWindow: openWindow 
	}
}());

$(document).ready(function () {
	insuraneHealthListObject.init();
	editPartyHealthInsInfoObj.init();
});
