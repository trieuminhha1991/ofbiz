$(document).ready(function() {
	ProductOfRival.init();
});
if (typeof (ProductOfRival) == "undefined") {
	var ProductOfRival = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 550,
				maxWidth : 2000,
				height : 210,
				maxHeight : 1000,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
			$("#PartyRivals").jqxDropDownList({
				autoDropDownHeight : true,
				source : partyRivals,
				displayMember : "groupName",
				valueMember : "partyId",
				width : 218,
				height : 30,
				placeHolder : multiLang.filterchoosestring
			});
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var row = {};
					row = {
						partyId : $("#PartyRivals").jqxDropDownList("val"),
						productName : $("#ProductName").val()
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#PartyRivals").jqxDropDownList("clearSelection");
				$("#ProductName").val("");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
				rules : [ {
					input : "#PartyRivals",
					message : multiLang.fieldRequired,
					action : "change",
					rule : function(input, commit) {
						if (input.val()) {
							return true;
						}
						return false;
					}
				}, {
					input : "#ProductName",
					message : multiLang.fieldRequired,
					action : "keyup, blur",
					rule : "required"
				} ],
				scroll : false
			});
		};
		return {
			init : function() {
				jqxwindow = $("#addProductOfRival");
				mainGrid = $("#jqxgrid");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}