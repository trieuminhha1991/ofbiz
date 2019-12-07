var updateDataRelatedObj = (function(){
	var init = function(){
		initJqxCheckBox();
		initJqxDateTimeInput();
		initDropDown();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerUpdateDataRelated"));
	};
	var initJqxDateTimeInput = function(){
		$("#fromDateTimekeeping").jqxDateTimeInput({width: '97%', height: 25});
		$("#thruDateTimekeeping").jqxDateTimeInput({width: '97%', height: 25});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#updateDataRelatedWindow"), 400, 260);
	};
	var initJqxCheckBox = function(){
		$("#emplLeaveCheck").jqxCheckBox({width: '97%', height: 25, checked: true});
		$("#holidayCheck").jqxCheckBox({width: '97%', height: 25, checked: true});
	};
	var initDropDown = function(){
		
	};
	var initEvent = function(){
		$("#updateDataRelatedWindow").on('open', function(event){
			$("#emplLeaveCheck").jqxCheckBox({checked: true});
			$("#holidayCheck").jqxCheckBox({checked: true});
			$("#fromDateTimekeeping").val(globalVar.fromDate);
			$("#thruDateTimekeeping").val(globalVar.thruDate);
		});
		$("#cancelUpdateData").click(function(event){
			$("#updateDataRelatedWindow").jqxWindow('close');
		});
		$("#saveUpdateData").click(function(event){
			updateDataRelated();
		});
	};
	var getData = function(){
		var dataSubmit = {};
		var emplLeaveCheck = $("#emplLeaveCheck").jqxCheckBox('checked');
		if(emplLeaveCheck){
			dataSubmit.emplLeaveUpdate = "Y";
		}else{
			dataSubmit.emplLeaveUpdate = "N";
		}
		var holidayCheck = $("#holidayCheck").jqxCheckBox('checked');
		if(holidayCheck){
			dataSubmit.holidayUpdate = "Y";
		}else{
			dataSubmit.holidayUpdate = "N";
		}
		dataSubmit.fromDate = $("#fromDateTimekeeping").jqxDateTimeInput('val', 'date').getTime(); 
		dataSubmit.thruDate = $("#thruDateTimekeeping").jqxDateTimeInput('val', 'date').getTime(); 
		dataSubmit.timekeepingDetailId = globalVar.timekeepingDetailId;  
		return dataSubmit;
	};
	var updateDataRelated = function(){
		var data = getData();
		disableAll();
		$("#loadingUpdateDataRelated").show();
		$.ajax({
			url: 'updateTimekeepingDataRelated',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#updateDataRelatedWindow").jqxWindow('close');
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
				$("#loadingUpdateDataRelated").hide();
			}
		});
	};
	var disableAll = function(){
		$("#emplLeaveCheck").jqxCheckBox({disabled: true});
		$("#holidayCheck").jqxCheckBox({disabled: true});
		$("#fromDateTimekeeping").jqxDateTimeInput({disabled: true});
		$("#thruDateTimekeeping").jqxDateTimeInput({disabled: true});
		$("#cancelUpdateData").attr("disabled", "disabled");
		$("#saveUpdateData").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#emplLeaveCheck").jqxCheckBox({disabled: false});
		$("#holidayCheck").jqxCheckBox({disabled: false});
		$("#fromDateTimekeeping").jqxDateTimeInput({disabled: false});
		$("#thruDateTimekeeping").jqxDateTimeInput({disabled: false});
		$("#cancelUpdateData").removeAttr("disabled");
		$("#saveUpdateData").removeAttr("disabled");
	};
	var openWindow = function(){
		openJqxWindow($("#updateDataRelatedWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	updateDataRelatedObj.init();
});