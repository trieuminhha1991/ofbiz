$(document).ready(function() {
	AddUserGroup.init();
});
if (typeof (AddUserGroup) == "undefined") {
	var AddUserGroup = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: "olbius", width: 550, maxWidth: 2000, height: 210, maxHeight: 1000, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#btnCancelUserGroup"), modalOpacity: 0.7
			});
		};
		var handleEvents = function() {
			$("#btnSaveUserGroup").click(function(){
				if (jqxwindow.jqxValidator("validate")) {
					var row = {};
					row = {
						partyCode : $("#txtUserGroupId").val(),
						description : $("#txtDescription").val(),
						groupName : $("#txtDescription").val(),
						partyTypeId: "SECURITY_GROUP",
						statusId: "PARTY_ENABLED"
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("close",function(){
				jqxwindow.jqxValidator("hide");
				$("#txtUserGroupId").val("");
				$("#txtDescription").val("");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [
							{ input: "#txtUserGroupId", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
							{ input: "#txtUserGroupId", message: multiLang.ContainSpecialSymbol, action: "keyup, blur",
								rule: function (input, commit) {
									var value = input.val();
									if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
										return true;
									}
									return false;
								}
							},
							{ input: "#txtUserGroupId", message: multiLang.BSCodeAlreadyExists, action: "change",
								rule: function (input, commit) {
									var partyCode = input.val();
									if (partyCode) {
										var check = DataAccess.getData({
											url: "checkPartyCode",
											data: {partyCode: partyCode},
											source: "check"});
										if ("false" == check) {
											 return false;
										}
									}
									return true;
								}
							},
							{ input: "#txtDescription", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" } ]
			});
		};
		return {
			init: function() {
				jqxwindow = $("#addUserGroup");
				mainGrid = $("#jqxgridUserInModule");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}