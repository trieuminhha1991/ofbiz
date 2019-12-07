var emplLeaveObject = (function(){
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
		create_spinner($("#spinner-ajax"));
	};
	
	var initJqxNumberInput = function(){
		var date = new Date(globalVar.nowTimestamp);
		$("#year").jqxNumberInput({ width: '65px', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$('#year').on('valueChanged', function (event)
		{
		    var value = event.args.value;
		    var tmpS = $("#jqxgrid").jqxGrid('source');
		    tmpS._source.url = "jqxGeneralServicer?sname=JQGetListEmplLeaveList&hasrequest=Y&year=" + value;
		    $("#jqxgrid").jqxGrid('source', tmpS);
		    updateEmplLeaveInfo(value);
		}); 
		$("#year").val(date.getFullYear());
	};
	
	var updateEmplLeaveInfo = function(year){
		$("#ajaxLoading").show();
		$.ajax({
			url: 'getEmplLeaveInfo',
			data: {year: year},
			type: 'POST', 
			success: function(response){
				var notSettingValue = "______";
				if(typeof(response.annualLeaveDayYear) != 'undefined'){
					$("#annualLeaveDayYear").text(response.annualLeaveDayYear)
				}else{
					$("#annualLeaveDayYear").text(notSettingValue);
				}
				if(typeof(response.annualLastYearTransferred) != 'undefined'){
					$("#annualLastYearTransferred").text(response.annualLastYearTransferred)
				}else{
					$("#annualLastYearTransferred").text(notSettingValue);
				}
				if(typeof(response.annualGrantedLeaveInYear) != 'undefined'){
					$("#annualGrantedLeaveInYear").text(response.annualGrantedLeaveInYear)
				}else{
					$("#annualGrantedLeaveInYear").text(notSettingValue);
				}
				if(typeof(response.annualLeft) != 'undefined'){
					$("#annualLeft").text(response.annualLeft)
				}else{
					$("#annualLeft").text(notSettingValue);
				}
				if(typeof(response.annualLeaveRemain) != 'undefined'){
					$("#annualLeaveRemain").text(response.annualLeaveRemain)
				}else{
					$("#annualLeaveRemain").text(notSettingValue);
				}
				if(typeof(response.unpaidLeave) != 'undefined'){
					$("#unpaidLeave").text(response.unpaidLeave)
				}else{
					$("#unpaidLeave").text(notSettingValue);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoading").hide();
			}
		});
	};
	var initBtnEvent = function(){
		$("#addNew").click(function(event){
			createEmplLeaveObject.openWindow();
		});
		$("#cancelAppl").click(function(event){
			var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			if(rowIndex < 0){
				return;
			}
			bootbox.dialog(uiLabelMap.CancelEmplLeaveApplConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							cancelEmplLeaveApp();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	
	var cancelEmplLeaveApp = function(){
		var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var rowData = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
		disableBtn();
		disabledJqxGrid($("#jqxgrid"));
		$.ajax({
			url: 'cancelEmplLeave',
			data: {emplLeaveId: rowData.emplLeaveId},
			type: 'POST', 
			success: function(response){
				$("#jqxNtf").jqxNotification('closeLast');
				if(response.responseMessage == "success"){
					$("#jqxNtfContent").text(response.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					$("#jqxNtfContent").text(response.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function(jqXHR, textStatus){
				enableJqxGrid($("#jqxgrid"));
				enableBtn();
			}
		});
	};
	
	var disableBtn = function(){
		$("#addNew").attr("disabled", "disabled");
		$("#cancelAppl").attr("disabled", "disabled");
	};
	var enableBtn = function(){
		$("#addNew").removeAttr("disabled");
		$("#cancelAppl").removeAttr("disabled");
	};
	
	
	return{
		init: init,
		disableBtn,
		enableBtn
	}
}());

$(document).ready(function(){
	emplLeaveObject.init();
});