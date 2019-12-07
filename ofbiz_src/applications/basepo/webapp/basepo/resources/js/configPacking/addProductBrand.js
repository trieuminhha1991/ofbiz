$(document).ready(function() {
	AddBrand.init();
});
if (typeof (AddBrand) == "undefined") {
	var AddBrand = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAddBrand").jqxWindow({
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
		};

		var handleEvents = function() {
			$("#btnSave").click(function() {
				if ($("#jqxwindowAddBrand").jqxValidator("validate")) {
					var row = {};
					row = {
						partyCode : $("#PartyCode").val(),
						groupName : $("#GroupName").val(),
						partyTypeId : "BRANDGROUP",
						statusId : "PARTY_DISABLED"
					};
					$("#jqxgrid").jqxGrid("addRow", null, row, "first");
					$("#jqxwindowAddBrand").jqxWindow("close");
				}
			});
			$("#jqxwindowAddBrand").on("close", function() {
				$("#jqxwindowAddBrand").jqxValidator("hide");
				$("#PartyCode").val("");
				$("#GroupName").val("");
			});
		};

		var initValidator = function() {
			$("#jqxwindowAddBrand").jqxValidator(
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
				initJqxElements();
				handleEvents();
				initValidator();
			}
		};
	})();
}