$(document).ready(function() {
	AddBillOfLading.init();
});
if (typeof (AddBillOfLading) == "undefined") {
	var AddBillOfLading = (function() {
		var validatorBILL;
		var mainWindow;
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 500, height: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
	        });
			$("#txtpartyIdFrom").jqxComboBox({ 
				source: source_partyShippingLine, displayMember: "name", valueMember: "value", theme: theme,
				width: "300", height: "25px", searchMode: "containsignorecase", autoOpen: false, autoComplete: false
			});
			$("#txtdepartureDate").jqxDateTimeInput({width: "300", height: "25px", theme: theme});
			$("#txtarrivalDate").jqxDateTimeInput({width: "300", height: "25px", theme: theme});
			$("#txtBillNumber").jqxInput({width: "295", height: "23px", theme: theme});
			$("#txtdepartureDate").jqxDateTimeInput("clear");
			$("#txtarrivalDate").jqxDateTimeInput("clear");
		};
		var handleEvents = function() {
			mainWindow.on("close", function (event) {
				mainWindow.jqxValidator("hide");
			});
			mainWindow.on("open", function (event) {
				$("#txtBillNumber").jqxInput('val');
				$("#txtpartyIdFrom").val("");
				$("#txtarrivalDate").jqxDateTimeInput("clear");
			});
			
			$("#alterSave").click(function () {
				if (mainWindow.jqxValidator("validate")) {
					bootbox.dialog(uiLabelMap.AreYouSureCreate, 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": uiLabelMap.OK,
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
							Loading.show('loadingMacro');
					    	setTimeout(function(){
					    	   	$("#jqxgrid").jqxGrid("addRow", null, AddBillOfLading.getValue(), "first");
					            $("#jqxgrid").jqxGrid("clearSelection");
					            mainWindow.jqxWindow("close");
				        	Loading.hide('loadingMacro');
					    	}, 500);
			            }
			        }]);
				}
		    });
		};
		var initValidator = function() {
	   		var mapRules = [
	   				{input: '#txtBillNumber', type: 'validInputNotNull'},
	   				{input: '#txtpartyIdFrom', type: 'validInputNotNull'},
	   				{input: '#txtdepartureDate', type: 'validInputNotNull'},
	   				{input: '#txtdepartureDate', type: 'validDateTimeCompareToday'},
	   				{input: '#txtarrivalDate', type: 'validInputNotNull'},
	   				{input: '#txtarrivalDate', type: 'validDateTimeCompareToday'},
	   				{input: '#txtdepartureDate, #txtarrivalDate', type: 'validCompareTwoDate', paramId1 : "txtdepartureDate", paramId2 : "txtarrivalDate"},
	               ];
	   		validatorBILL = new OlbValidator($('#alterpopupWindow'), mapRules, null, {position: 'right'});
		};
		var getValue = function() {
			var value = new Object();
			value.billNumber = $("#txtBillNumber").jqxInput('val');
			value.partyIdFrom = $("#txtpartyIdFrom").jqxComboBox("getSelectedItem").value;
			value.departureDate = $("#txtdepartureDate").jqxDateTimeInput("getDate");
			value.arrivalDate = $("#txtarrivalDate").jqxDateTimeInput("getDate");
			return value;
		};
		return {
			init: function() {
				mainWindow = $("#alterpopupWindow");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
		};
	})();
}