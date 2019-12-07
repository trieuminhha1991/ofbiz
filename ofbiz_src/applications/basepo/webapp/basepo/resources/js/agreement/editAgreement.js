$(function() {
	pageCommonEditAgreement.init();
});

var pageCommonEditAgreement = (function() {
	var form = $("#formAdd");
	var popup = $("#alterpopupWindow");
	var partyFromChange = "";
	var partyToChange = "";

	var init = function() {
		initElement();
		initEvent();
		initRules();
		if (agreementDate != "") {
			$("#agreementDateAdd").jqxDateTimeInput("setDate",
					new Date(agreementDate));
		}
		if (fromDate != "") {
			$("#fromDateAdd").jqxDateTimeInput("setDate", new Date(fromDate));
		}
		if (thruDate != "") {
			$("#thruDateAdd").jqxDateTimeInput("setDate", new Date(thruDate));
		}
	};

	var initElement = function() {
		$("#agreementDateAdd").jqxDateTimeInput({
			width : "100%",
			dropDownHorizontalAlignment : "right",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			allowNullDate : true,
			value : null
		});
		$("#fromDateAdd").jqxDateTimeInput({
			width : "100%",
			dropDownHorizontalAlignment : "right",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			allowNullDate : true,
			value : null
		});
		$("#thruDateAdd").jqxDateTimeInput({
			width : "100%",
			formatString : "dd-MM-yyyy : HH:mm:ss",
			dropDownHorizontalAlignment : "right",
			allowNullDate : true,
			value : null
		});
		$("#descriptionAdd").jqxInput({
			width : "100%",
			height : "25px",
			theme : theme
		});

		$("#jqxNotification").jqxNotification({
			opacity : 0.9,
			autoClose : true,
			template : "info"
		});
	};

	var initEvent = function() {
		$("#alterpopupWindow").on("close", function() {
			clearForm();
		});

		$("#save").click(function() {
			if (!saveAgreementAction()) {
				return;
			}
			popup.jqxWindow("close");
		});
	};

	var initRules = function() {
		form.jqxValidator({
			rules : [ {
				input : "#agreementDateAdd",
				message : "uiLabelMap.CommonRequired",
				action : "change",
				rule : function(input, commit) {
					var value = $("#agreementDateAdd").jqxDateTimeInput("val");
					if (!value)
						return false;
					return true;
				}
			}, {
				input : "#fromDateAdd",
				message : "uiLabelMap.CommonRequired",
				action : "change",
				rule : function(input, commit) {
					var value = $("#fromDateAdd").jqxDateTimeInput("val");
					if (!value)
						return false;
					return true;
				}
			} ]
		});
	};

	var saveAgreementAction = function() {
		if (!form.jqxValidator("validate")) {
			return false;
		}
		var agreementDateJS = "";
		if ($("#agreementDateAdd").jqxDateTimeInput("getDate")) {
			agreementDateJS = $("#agreementDateAdd")
					.jqxDateTimeInput("getDate").toSQLTimeStamp();
		}
		var fromDateJS = "";
		if ($("#fromDateAdd").jqxDateTimeInput("getDate")) {
			fromDateJS = $("#fromDateAdd").jqxDateTimeInput("getDate")
					.toSQLTimeStamp();
		}
		var thruDateJS = "";
		if ($("#thruDateAdd").jqxDateTimeInput("getDate")) {
			thruDateJS = $("#thruDateAdd").jqxDateTimeInput("getDate")
					.toSQLTimeStamp();
		}
		var row = {
			agreementId : agreementId,
			description : $("#descriptionAdd").val(),
			agreementDate : agreementDateJS,
			fromDate : fromDateJS,
			thruDate : thruDateJS,
			statusId : "AGREEMENT_MODIFIED"
		};

		DataAccess.execute({
			url : "updateAgreement",
			data : row
		}, pageCommonEditAgreement.notify);

		return true;
	};
	var notify = function(res) {
		$("#jqxNotification").jqxNotification("closeLast");
		if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
			$("#jqxNotification").jqxNotification({
				template : "error"
			});
			$("#notificationContent").text(multiLang.updateError);
			$("#jqxNotification").jqxNotification("open");
		} else {
			$("#jqxNotification").jqxNotification({
				template : "info"
			});
			$("#notificationContent").text(multiLang.updateSuccess);
			$("#jqxNotification").jqxNotification("open");
		}
	};
	return {
		init : init,
		notify : notify
	}
}());