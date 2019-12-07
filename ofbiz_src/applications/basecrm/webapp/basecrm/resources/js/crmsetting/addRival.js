$(document).ready(function() {
	AddRival.init();
});
if (typeof (AddRival) == "undefined") {
	var AddRival = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 550,
				height : 200,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var row = {};
					row = {
						partyCode : $("#PartyCode").val(),
						groupName : $("#GroupName").val()
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
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
			jqxwindow.jqxValidator(
					{
						rules : [
								{
									input : "#PartyCode",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								},
								{
									input : "#PartyCode",
									message : multiLang.ContainSpecialSymbol,
									action : "keyup, blur",
									rule : function(input, commit) {
										var value = input.val();
										if (!value.containSpecialChars()
												&& !hasWhiteSpace(value)) {
											return true;
										}
										return false;
									}
								}, {
									input : "#PartyCode",
									message : multiLang.BSCodeAlreadyExists,
									action : "change",
									rule : function(input, commit) {
										var partyCode = input.val();
										if (partyCode) {
											var check = DataAccess.getData({
												url : "checkPartyCode",
												data : {
													partyCode : partyCode
												},
												source : "check"
											});
											if ("false" == check) {
												return false;
											}
										}
										return true;
									}
								}, {
									input : "#GroupName",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								} ],
						scroll : false
					});
		};
		return {
			init : function() {
				jqxwindow = $("#addRival");
				mainGrid = $("#jqxgrid");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}