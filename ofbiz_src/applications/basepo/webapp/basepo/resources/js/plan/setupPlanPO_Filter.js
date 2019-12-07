if (typeof (PlanFilter) == "undefined") {
	var PlanFilter = (function() {
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 800,
				height : 200,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelPlanFilter"),
				modalOpacity : 0.7
			});
			$("#txtViewOn").jqxDropDownList({
				theme : theme,
				source : displayOption,
				selectedIndex : 1,
				displayMember : "text",
				valueMember : "value",
				width : 200,
				height : 25,
				autoDropDownHeight : true
			});
			$("#txtPeriod").jqxDropDownList({
				theme : theme,
				source : period,
				selectedIndex : 1,
				displayMember : "text",
				valueMember : "value",
				width : 200,
				height : 25,
				autoDropDownHeight : true
			});

			$("#txtPrevious").jqxNumberInput({
				width : 200,
				height : 25,
				inputMode : "simple",
				decimalDigits : 0,
				min : 1,
				spinButtons : true
			});
			$("#txtNext").jqxNumberInput({
				width : 200,
				height : 25,
				inputMode : "simple",
				decimalDigits : 0,
				min : 1,
				spinButtons : true
			});

			$("#txtPrevious").jqxNumberInput("setDecimal", 2);
			$("#txtNext").jqxNumberInput("setDecimal", 5);
		};
		var handleEvents = function() {
			$("#savePlanFilter").click(function() {
				FilterAdapter.apply();
				jqxwindow.jqxWindow("close");
			});
		};
		var getValue = function() {
			var value = {
				viewOn : $("#txtViewOn").jqxDropDownList("val"),
				periodType : $("#txtPeriod").jqxDropDownList("val"),
				previous : $("#txtPrevious").jqxNumberInput("getDecimal"),
				next : $("#txtNext").jqxNumberInput("getDecimal") + 1
			};
			return value;
		};
		var open = function() {
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({
				position : {
					x : (wtmp.outerWidth - tmpwidth) / 2,
					y : pageYOffset + 70
				}
			});
			jqxwindow.jqxWindow("open");
		};
		return {
			init : function() {
				if (typeof (FilterAdapter.apply) != "function") {
					throw "FilterAdapter required";
				}
				jqxwindow = $("#jqxwindowPlanFilter");
				initJqxElements();
				handleEvents();
			},
			getValue : getValue,
			open : open
		};
	})();
}