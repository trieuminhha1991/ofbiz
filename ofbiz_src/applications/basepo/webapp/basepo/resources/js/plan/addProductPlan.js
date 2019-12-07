$(document).ready(function() {
	AddProductPlan.init();
});
if (typeof (AddProductPlan) == "undefined") {
	var AddProductPlan = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 550,
				height : 210,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
			$("#txtYearPlan").jqxDropDownList({
				theme : theme,
				source : listPeriod,
				selectedIndex : 0,
				displayMember : "periodName",
				valueMember : "customTimePeriodId",
				width : 218,
				height : 30
			});
		};
		var handleEvents = function() {
			$("#btnSave").click(
					function() {
						if (jqxwindow.jqxValidator("validate")) {
							mainGrid.jqxGrid("addRow", null, AddProductPlan
									.getValue(), "first");
							jqxwindow.jqxWindow("close");
						}
					});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#PartyCode").val("");
				$("#GroupName").val("");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules : [ {
					input : "#txtYearPlan",
					message : multiLang.fieldRequired,
					action : "blur",
					rule : function(input, commit) {
						var value = input.val();
						if (value != "Please Choose:") {
							return true;
						}
						return false;
					}
				}, {
					input : "#txtProductPlanName",
					message : multiLang.fieldRequired,
					action : "keyup, blur",
					rule : "required"
				} ]
			});
		};
		var getValue = function() {
			var value = new Object();
			value.productPlanTypeId = "PO_PLAN";
			value.customTimePeriodId = $("#txtYearPlan").jqxDropDownList("val");
			value.productPlanName = $("#txtProductPlanName").val();
			return value;
		};
		return {
			init : function() {
				jqxwindow = $("#alterpopupWindow");
				mainGrid = $("#jqxgirdProductPlan");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue : getValue
		};
	})();
}