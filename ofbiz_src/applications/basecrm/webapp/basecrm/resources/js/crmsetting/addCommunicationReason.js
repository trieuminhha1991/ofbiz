$(document).ready(function() {
	CommunicationReason.init();
});
if (typeof (CommunicationReason) == "undefined") {
	var CommunicationReason = (function() {
		var jqxwindow, mainGrid;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme : theme,
				width : 550,
				height : 270,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
			$("#ReasonType").jqxDropDownList({
				autoDropDownHeight : true,
				source : reasonTypeList,
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
						enumCode : $("#ReasonCode").val(),
						enumTypeId : $("#ReasonType").jqxDropDownList("val"),
						sequenceId : $("#SequenceId").val(),
						description : $("#Description").val()
					};
					mainGrid.jqxGrid("addRow", null, row, "first");
					jqxwindow.jqxWindow("close");
				}
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#ReasonCode").val("");
				$("#ReasonType").jqxDropDownList("clearSelection");
				$("#SequenceId").val("");
				$("#Description").val("");
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator(
					{
						rules : [
								{
									input : "#ReasonCode",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								},
								{
									input : "#ReasonCode",
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
									input : "#ReasonCode",
									message : multiLang.BSCodeAlreadyExists,
									action : "change",
									rule : function(input, commit) {
										var enumCode = input.val();
										if (enumCode) {
											var check = DataAccess.getData({
												url : "checkEnumerationCode",
												data : {
													enumCode : enumCode
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
									input : "#ReasonType",
									message : multiLang.fieldRequired,
									action : "change",
									rule : function(input, commit) {
										if (input.val()) {
											return true;
										}
										return false;
									}
								}, {
									input : "#SequenceId",
									message : multiLang.DmsQuantityNotValid,
									action : "keyup, blur",
									rule : function(input, commit) {
										if (input.val() > 0) {
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
				jqxwindow = $("#addCommunicationReason");
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}