$(document).ready(function() {
	AddOtherTax.init();
});
if (typeof (AddOtherTax) == "undefined") {
	var AddOtherTax = (function() {
		var validatorVal;
		var mainWindow;
		var init = function(){
			mainWindow = $("#alterpopupWindow");
			initJqxElements();
			handleEvents();
			initValidator();
		}
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 600, height: 325, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
	        });
			$('#taxType').jqxDropDownList({ 
				source: taxTypes,  
				width: 300,
				valueMember: 'enumId', 
				displayMember: 'description', 
				placeHolder : uiLabelMap.BPPleaseSelect})
			$("#taxPercent").jqxNumberInput({ inputMode: 'simple', decimalDigits: 2, spinMode: 'simple', groupSeparator: '.', min:0, width: 300}); 
			$("#fromDate").jqxDateTimeInput({width: 300, theme: theme}); 
			$("#thruDate").jqxDateTimeInput({width: 300, theme: theme}); 
			$("#fromDate").jqxDateTimeInput('clear');
			$("#thruDate").jqxDateTimeInput('clear');
			$("#description").jqxTextArea({width: 300, height: 48, theme: theme}); 
		};
		var handleEvents = function() {
			mainWindow.on("close", function (event) {
				resetData();
			});
			$("#alterSave").click(function () {
				if(!validatorVAL.validate()){return;}
				var thruDate = null;
				if ($("#thruDate").jqxDateTimeInput('getDate') != null){
					thruDate = $("#thruDate").jqxDateTimeInput('getDate').getTime();
				}
				var data = {
						taxType: $("#taxType").jqxDropDownList('val'),
						percent: $("#taxPercent").jqxNumberInput('val'),
						fromDate: $("#fromDate").jqxDateTimeInput('getDate').getTime(),
						thruDate: thruDate,
						description: $("#description").jqxTextArea('val'),
						productId: productId
					}; 
		    	var url = "createOtherTax";
		    	Loading.show("loadingMacro");
		    	$.ajax({	
					 type: "POST",
					 url: url,
					 data: data,
					 dataType: "json",
					 async: false,
					 success: function(res){
						 if (res._ERROR_MESSAGE_ != undefined && res._ERROR_MESSAGE_ != null) {
							 jOlbUtil.alert.error(res._ERROR_MESSAGE_);
							 Loading.hide("loadingMacro");
							 return false;
						 }
						 else {
							 Loading.hide("loadingMacro");
							 $('#jqxNotification').jqxNotification({ template: 'success'});
							 $("#notificationContent").text(uiLabelMap.CreateSuccess);
							 $("#jqxNotification").jqxNotification("open");
							 $('#jqxGridOtherTax').jqxGrid('updatebounddata');
						 	}
						 mainWindow.jqxWindow('close');
					 },
					 error: function(response){
					 }
			 		}).done(function(data) {
		  		});	
		    });
		};
		var initValidator = function() {
			var extendRules = [
			      				{input: '#fromDate', message: uiLabelMap.CannotBeforeNow, action: 'valueChanged', 
			      					rule: function(input, commit){
			      						var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
			      		     		   	var nowDate = new Date();
			      		     			if (typeof(fromDate) != 'undefined' && fromDate != null){
			      				 		    if (fromDate < nowDate ) {
			      				 		    	return false;
			      				 		    } 
			      					   	}
			      		     			return true;
			      					}
			      				},
			      				{input: '#thruDate', message: uiLabelMap.CannotBeforeNow, action: 'valueChanged', 
			      					rule: function(input, commit){
			      						var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
			      		     		   	var nowDate = new Date();
			      		     			if (typeof(thruDate) != 'undefined' && thruDate != null){
			      				 		    if (thruDate < nowDate ) {
			      				 		    	return false;
			      				 		    } 
			      					   	}
			      		     			return true;
			      					}
			      				},
			      				{input: '#fromDate', message: uiLabelMap.CanNotAfterThruDate, action: 'valueChanged', 
			      					rule: function(input, commit){
			      						var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
			      						var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
			      					   	if (typeof(fromDate) != 'undefined' && fromDate != null && typeof(thruDate) != 'undefined' && thruDate != null ) {
			      				 		    if (fromDate > thruDate) {
			      				 		    	return false;
			      				 		    }
			      				 		    return true;
			      					   	}
			      					   	return true;
			      					}
			      				},
			      				{input: '#thruDate', message: uiLabelMap.CanNotBeforeFromDate, action: 'valueChanged', 
			      					rule: function(input, commit){
			      						var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
			      						var thruDate = $('#thruDate').jqxDateTimeInput('getDate');
			      						if (typeof(fromDate) != 'undefined' && fromDate != null && typeof(thruDate) != 'undefined' && thruDate != null ) {
			      				 		    if (fromDate > thruDate) {
			      				 		    	return false;
			      				 		    }
			      					   	}
			      						return true;
			      					}
			      				},
			      				{input: '#taxPercent', message: uiLabelMap.ValueMustBeGreaterThanZero, action: 'valueChanged', 
			      					rule: function(input, commit){
			      						if ($('#taxPercent').val() == '' || $('#taxPercent').val() == undefined || $('#taxPercent').val() < 0 ){
			      							return false;
			      						}
			      						return true;
			      					}
			      				},
			                 ];
			      		var mapRules = [
			      				{input: '#fromDate', type: 'validInputNotNull'},
			      				{input: '#taxPercent', type: 'validInputNotNull'},
			      				{input: '#taxType', type: 'validObjectNotNull', objType: 'dropDownList'}
			                  ];
			      		validatorVAL = new OlbValidator($('#alterpopupWindow'), mapRules, extendRules, {position: 'topcenter'});
		};
		var resetData = function() {
			$("#taxType").jqxDropDownList('setContent', null);
        	$("#taxPercent").val(0);
        	$("#fromDate").jqxDateTimeInput('clear');
			$("#thruDate").jqxDateTimeInput('clear');
			$("#description").jqxTextArea('val', "");
		}
		return {
			init: init ,
		};
	})();
}