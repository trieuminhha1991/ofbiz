$(document).ready(function() {
	CommunicationSubject.init();
});
if (typeof (CommunicationSubject) == "undefined") {
	var CommunicationSubject = (function() {
		var enumId;
		var initJqxElements = function() {
			$("#addCommunicationSubject").jqxWindow({
				theme : theme,
				width : 550,
				height : 230,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#btnCancel"),
				modalOpacity : 0.7
			});
			$("#SequenceId").jqxNumberInput({
				theme : theme,
				width : 218,
				height : 30,
				inputMode : "simple",
				spinButtons : true,
				decimalDigits : 0,
				min : 0
			});
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if ($("#addCommunicationSubject").jqxValidator("validate")) {
					var row = {};
					row = {
						enumCode : $("#SubjectCode").val(),
						sequenceId : $("#SequenceId").val(),
						description : $("#Description").val(),
						enumTypeId : "COMM_SUBJECT"
					};
					$("#jqxgrid").jqxGrid("addRow", null, row, "first");
					$("#addCommunicationSubject").jqxWindow("close");
				}
			});
			$('#addCommunicationSubject').on('close', function() {
				$('#addCommunicationSubject').jqxValidator('hide');
				$('#SubjectCode').val("");
				$('#SequenceId').val("");
				$('#Description').val("");
			});
		};
		var initValidator = function() {
			$("#addCommunicationSubject").jqxValidator(
					{
						rules : [
								{
									input : "#SubjectCode",
									message : multiLang.fieldRequired,
									action : "keyup, blur",
									rule : "required"
								},
								{
									input : "#SubjectCode",
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
									input : "#SubjectCode",
									message : multiLang.BSCodeAlreadyExists,
									action : "change",
									rule : function(input, commit) {
										var enumCode = input.val();
										if (enumCode) {
											var check = DataAccess.getData({
												url : "checkEnumerationCode",
												data : {
													enumCode : enumCode,
													enumId : enumId
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
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}