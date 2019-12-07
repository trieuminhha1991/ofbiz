var partyDateTimekeepingDetailObj = (function(){
	var _partyId;
	var _timekeepingDetailId;
	var _dateTimekeeping;
	var _timekeepingDetailPartyData = {};
	var init = function(){
		initJqxNumberInput();
		initCheckBox();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerDateTimekeepingDetail"));
	};
	var initJqxNumberInput = function(){
		$("#dateTimekeepingActualWorkday").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 1, inputMode: 'simple', disabled: true});
		$("#dateTimekeepingLeavePaid").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 1, inputMode: 'simple', disabled: true});
		$("#dateTimekeepingOvertimeHours").jqxNumberInput({ width: '100%', height: '25px', spinButtons: true, decimalDigits: 1, inputMode: 'simple', disabled: true});
	};
	var initCheckBox = function(){
		$("#checkActualWorkday").jqxCheckBox({ width: '96%', height: 25});
		$("#checkLeavePaid").jqxCheckBox({ width: '96%', height: 25});
		$("#checkOvertimeHours").jqxCheckBox({ width: '96%', height: 25});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#partyDatekeepingDetailWindow"), 420, 340);
	};
	var getData = function(){
		var data = {};
		data.partyId = _partyId;
		data.timekeepingDetailId = _timekeepingDetailId;
		data.dateTimekeeping = _dateTimekeeping;
		var workdayActual = $("#dateTimekeepingActualWorkday").val();
		var overtimeHours = $("#dateTimekeepingOvertimeHours").val();
		var workdayLeavePaid = $("#dateTimekeepingLeavePaid").val();
		var checkActualWorkday = $("#checkActualWorkday").jqxCheckBox('checked');
		var checkLeavePaid = $("#checkLeavePaid").jqxCheckBox('checked');
		var checkOvertimeHours = $("#checkOvertimeHours").jqxCheckBox('checked');
		if(checkActualWorkday){
			data.workdayActual = workdayActual;
		}
		if(checkOvertimeHours){
			data.overtimeHours = overtimeHours;
		}
		if(checkLeavePaid){
			data.workdayLeavePaid = workdayLeavePaid;
		}
		return data;
	};
	var initEvent = function(){
		$("#partyDatekeepingDetailWindow").on('close', function(event){
			Grid.clearForm($(this));
			$("#partyDatekeepingName").html("___________");
			$("#partyDatekeepingId").html("___________");
			$("#dateTimekeepingDetailDate").html("");
			_partyId = null;
			_timekeepingDetailId = null;
			_dateTimekeeping = null;
			_timekeepingDetailPartyData = {};
			$("#checkActualWorkday").jqxCheckBox({checked: false, disabled: false});
			$("#checkLeavePaid").jqxCheckBox({checked: false, disabled: false});
			$("#checkOvertimeHours").jqxCheckBox({checked: false});
		});
		
		$("#cancelUpdateTimekeepingDetail").click(function(event){
			$("#partyDatekeepingDetailWindow").jqxWindow('close');
		});
		$("#saveUpdateTimekeepingDetail").click(function(event){
			var data = getData();
			$("#loadingDateTimekeepingDetail").show();
			disableAll();
			$.ajax({
				url: 'updateTimekeepingDetailParty',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
							template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
						$("#jqxgrid").jqxGrid('updatebounddata');
	    				$("#partyDatekeepingDetailWindow").jqxWindow('close');
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
					$("#loadingDateTimekeepingDetail").hide();
					enableAll();
				}
			});
		});
		$("#checkActualWorkday").on('change', function (event){
			var checked = event.args.checked;
			$("#dateTimekeepingActualWorkday").jqxNumberInput({disabled: !checked});
		});
		$("#checkLeavePaid").on('change', function (event){
			var checked = event.args.checked;
			$("#dateTimekeepingLeavePaid").jqxNumberInput({disabled: !checked});
		});
		$("#checkOvertimeHours").on('change', function (event){
			var checked = event.args.checked;
			$("#dateTimekeepingOvertimeHours").jqxNumberInput({disabled: !checked});
		});
	};
	var openWindow = function(){
		openJqxWindow($("#partyDatekeepingDetailWindow"));
	};
	var setData = function(data){
		$("#loadingDateTimekeepingDetail").show();
		disableAll();
		_partyId = data.partyId;
		_timekeepingDetailId = data.timekeepingDetailId;
		_dateTimekeeping = data.dateTimekeeping;
		var date = new Date(parseInt(_dateTimekeeping));
		var dateDes = getDate(date) + "/" + getMonth(date) + "/" + date.getFullYear();
		$("#partyDatekeepingDetailWindow").jqxWindow('setTitle', uiLabelMap.CommonDate + " " + dateDes);
		$("#partyDatekeepingName").html(data.fullName);
		$("#partyDatekeepingId").html(data.partyCode);
		$("#dateTimekeepingDetailDate").html(dateDes);
		var isDayLeave = false;
		$.ajax({
			url: 'getTimekeepingDetailPartyInDateTimekeeping',
			data: {partyId: _partyId, timekeepingDetailId: _timekeepingDetailId, dateTimekeeping: _dateTimekeeping},
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
				var data = response.data;
				isDayLeave = response.isDayLeave;
				if(data){
					_timekeepingDetailPartyData = data;
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingDateTimekeepingDetail").hide();
				enableAll();
				if(_timekeepingDetailPartyData.workdayActual && _timekeepingDetailPartyData.workdayActual > 0){
					$("#dateTimekeepingActualWorkday").jqxNumberInput('val', _timekeepingDetailPartyData.workdayActual);
					$("#checkActualWorkday").jqxCheckBox({checked: true});
				}else{
					$("#dateTimekeepingActualWorkday").jqxNumberInput('val', 0);
					$("#checkActualWorkday").jqxCheckBox({checked: false});
				}
				if(_timekeepingDetailPartyData.workdayLeavePaid && _timekeepingDetailPartyData.workdayLeavePaid > 0){
					$("#dateTimekeepingLeavePaid").jqxNumberInput('val', _timekeepingDetailPartyData.workdayLeavePaid);
					$("#checkLeavePaid").jqxCheckBox({checked: true});
				}else{
					$("#dateTimekeepingLeavePaid").jqxNumberInput('val', 0);
					$("#checkLeavePaid").jqxCheckBox({checked: false});
				}
				if(_timekeepingDetailPartyData.overtimeHours && _timekeepingDetailPartyData.overtimeHours > 0){
					$("#dateTimekeepingOvertimeHours").jqxNumberInput('val', _timekeepingDetailPartyData.overtimeHours);
					$("#checkOvertimeHours").jqxCheckBox({checked: true});
				}else{
					$("#dateTimekeepingOvertimeHours").jqxNumberInput('val', 0);
					$("#checkOvertimeHours").jqxCheckBox({checked: false});
				}
				if(isDayLeave){
					$("#checkActualWorkday").jqxCheckBox({disabled: true});
					$("#checkLeavePaid").jqxCheckBox({disabled: true});
				}else{
					$("#checkActualWorkday").jqxCheckBox({disabled: false});
					$("#checkLeavePaid").jqxCheckBox({disabled: false});
				}
			}
		});
	};
	var disableAll = function(){
		/*$("#dateTimekeepingActualWorkday").jqxNumberInput({disabled: true});
		$("#dateTimekeepingLeavePaid").jqxNumberInput({disabled: true});
		$("#dateTimekeepingOvertimeHours").jqxNumberInput({disabled: true});*/
		$("#checkActualWorkday").jqxCheckBox({disabled: true});
		$("#checkOvertimeHours").jqxCheckBox({disabled: true});
		$("#checkLeavePaid").jqxCheckBox({disabled: true});
		$("#cancelUpdateTimekeepingDetail").attr("disabled", "disabled");
		$("#saveUpdateTimekeepingDetail").attr("disabled", "disabled");
	};
	var enableAll = function(){
		/*$("#dateTimekeepingActualWorkday").jqxNumberInput({disabled: false});
		$("#dateTimekeepingLeavePaid").jqxNumberInput({disabled: false});
		$("#dateTimekeepingOvertimeHours").jqxNumberInput({disabled: false});*/
		$("#checkActualWorkday").jqxCheckBox({disabled: false});
		$("#checkOvertimeHours").jqxCheckBox({disabled: false});
		$("#checkLeavePaid").jqxCheckBox({disabled: false});
		$("#cancelUpdateTimekeepingDetail").removeAttr("disabled");
		$("#saveUpdateTimekeepingDetail").removeAttr("disabled");
	};
	return {
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());

$(document).ready(function () {
	partyDateTimekeepingDetailObj.init();
});