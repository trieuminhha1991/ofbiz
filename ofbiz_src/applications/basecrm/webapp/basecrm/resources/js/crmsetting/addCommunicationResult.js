$(document).ready(function() {
	CommunicationResult.init();
});
if (typeof (CommunicationResult) == "undefined") {
	var CommunicationResult = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 550,
				height : 230,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
			$("#Status").jqxDropDownList({
				autoDropDownHeight : true,
				source : reasonType,
				displayMember : "description",
				valueMember : "enumTypeId",
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
						enumTypeId : $("#ReasonTypeId").val(),
						parentTypeId : $("#Status").jqxDropDownList("val"),
						description : $("#Description").val()
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#ReasonTypeId").val("");
				$("#Status").jqxDropDownList("clearSelection");
				$("#Description").val("");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator(
					{
						rules : [
								{
									input : "#ReasonTypeId",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								},
								{
									input : "#ReasonTypeId",
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
									input : "#ReasonTypeId",
									message : multiLang.BSCodeAlreadyExists,
									action : "change",
									rule : function(input, commit) {
										var enumTypeId = input.val();
										if (enumTypeId) {
											var check = DataAccess.getData({
												url : "checkEnumerationTypeId",
												data : {
													enumTypeId : enumTypeId
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
									input : "#Status",
									message : multiLang.fieldRequired,
									action : "change",
									rule : function(input, commit) {
										if (input.val()) {
											return true;
										}
										return false;
									}
								}, {
									input : "#Description",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								} ],
						scroll : false
					});
		};
		return {
			init : function() {
				mainGrid = $("#jqxgrid");
				jqxwindow = $("#addCommunicationResult");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}