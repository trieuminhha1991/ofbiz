if (typeof (RepresentativeMember) == "undefined") {
	var RepresentativeMember = (function() {
		var initJqxElements = function() {
			$("#txtFulName").jqxInput({
				width : "calc(" + width + " - 6px)",
				theme : theme,
				height : 25
			});
			$("#txtEmail").jqxInput({
				width : "calc(" + width + " - 12px)",
				theme : theme,
				height : 25
			});
			$("#txtHomePhone").jqxInput({
				width : "calc(" + width + " - 12px)",
				theme : theme
			});
			$("#txtMobilePhone").jqxInput({
				width : "calc(" + width + " - 12px)",
				theme : theme
			});
			$("#txtOfficePhone").jqxInput({
				width : "calc(" + width + " - 12px)",
				theme : theme
			});
			$("#txtBirthDate").jqxDateTimeInput({
				theme : theme,
				width : width
			});
			$("#txtBirthDate").jqxDateTimeInput("setDate", null);
			$("#identityDate").jqxDateTimeInput({
				theme : theme,
				width : width
			});
			$("#identityDate").jqxDateTimeInput("setDate", null);
			$("#identification").jqxInput({
				width : "calc(" + width + " - 12px)",
				theme : theme,
				height : 25
			});
			$("#txtFacebook").jqxInput({
				width : width,
				theme : theme,
				height : 25
			});
			$("#partyDataSource").jqxInput({
				width : width,
				theme : theme,
				height : 25
			});
			$(".no-space").keyup(function() {
				var val = $(this).val();
				val = val.replace(/[^\w-]/gi, "");
				var res = "";
				for (var x = 0; x < val.length; x++) {
					res += val[x].toUpperCase();
				}
				$(this).val(res);
			});
			Processor.initComboboxGeo("VNM", "PROVINCE", "providePlace");
			$("#txtGender").jqxDropDownList({
				theme : theme,
				width : width,
				height : 25,
				selectedIndex : 1,
				source : listGender,
				displayMember : "label",
				valueMember : "value",
				placeHolder : label.gender,
				autoDropDownHeight : true,
				placeHolder : multiLang.filterchoosestring
			});
		};
		var initValidator = function() {
			$("#familyInfoEditable")
					.jqxValidator(
							{
								rules : [
										{
											input : "#txtPartyCode",
											message : multiLang.fieldRequired,
											action : "blur",
											rule : function(input, commit) {
												if (CreateMode) {
													return true;
												}
												if (input.val()) {
													return true;
												}
												return false;
											}
										},
										{
											input : "#txtPartyCode",
											message : multiLang.DmsPartyCodeAlreadyExists,
											action : "change",
											rule : function(input, commit) {
												var check = DataAccess
														.getData({
															url : "checkPartyCode",
															data : {
																partyId : CookieLayer
																		.getCurrentParty().partyId,
																partyCode : input
																		.val()
															},
															source : "check"
														});
												if ("false" == check) {
													return false;
												}
												return true;
											}
										},
										{
											input : "#txtFulName",
											message : multiLang.fieldRequired,
											action : "blur",
											rule : "required"
										},
										{
											input : "#partyDataSource",
											message : multiLang.SmallerThan20,
											action : "blur",
											rule : function(input, value) {
												var val = input.val();
												if (val.length > 20) {
													return false;
												}
												return true;
											}
										},
										{
											input : "#txtBirthDate",
											message : multiLang.dateNotValid,
											action : "valueChanged",
											rule : function(input, commit) {
												var currentTime = new Date()
														.getTime();
												var value = 0;
												input
														.jqxDateTimeInput("getDate") ? value = input
														.jqxDateTimeInput(
																"getDate")
														.getTime()
														: value;
												if (value == 0) {
													return true;
												}
												if (currentTime > value) {
													return true;
												}
												return false;
											}
										},
										{
											input : "#identityDate",
											message : multiLang.dateNotValid,
											action : "valueChanged",
											rule : function(input, commit) {
												var currentTime = new Date()
														.getTime();
												var value = 0;
												input
														.jqxDateTimeInput("getDate") ? value = input
														.jqxDateTimeInput(
																"getDate")
														.getTime()
														: value;
												if (value == 0) {
													return true;
												}
												var birthDate = 0;
												$("#txtBirthDate")
														.jqxDateTimeInput(
																"getDate") ? birthDate = $(
														"#txtBirthDate")
														.jqxDateTimeInput(
																"getDate")
														.getTime()
														: birthDate;
												if ((birthDate != 0 && birthDate > value)
														|| currentTime < value) {
													return false;
												}
												return true;
											}
										} ]
							});
		};
		var bindDataToDropdown = function(partyId) {
			var data = DataAccess.getData({
				url : "getListMemberInFamilyDropdown",
				data : {
					partyId : partyId
				},
				source : "listMemberInFamilyDropdown"
			});
			$("#ClaimPartyId").jqxDropDownList({
				source : data
			});
			$("#ClaimPartyId").jqxDropDownList("val", partyId);
		};
		var extendId = new Object();
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				extendId = {};
				extendId.partyId = data.partyId;
				extendId.facebookId = data.facebookId;
				extendId.emailAddressId = data.emailAddressId;
				extendId.phoneHomeId = data.phoneHomeId;
				extendId.phoneWorkId = data.phoneWorkId;
				extendId.phoneMobileId = data.phoneMobileId;
				Family.setValue(data.partyId);
				bindDataToDropdown(data.partyId);
				$("#txtIdentification").val(data.idNumber);
				$("#txtGender").val(data.gender);
				if (data.birthDate) {
					$("#txtBirthDate").jqxDateTimeInput("setDate",
							new Date(data.birthDate));
				}
				$("#partyDataSource").val(data.dataSourceId);
				$("#txtFacebook").val(data.facebook);
				var first = data.firstName ? data.firstName : "";
				var middle = data.middleName ? data.middleName : "";
				var last = data.lastName ? data.lastName : "";
				$("#txtFulName")
						.val((last + " " + middle + " " + first).trim());
				$("#identification").val(data.idNumber);
				if (data.idIssueDate) {
					$("#identityDate").jqxDateTimeInput("setDate",
							new Date(data.idIssueDate));
				}
				if (data.idIssuePlace) {
					$("#providePlace").jqxComboBox("val", data.idIssuePlace);
				}
				var pt = "<b>";
				if (CookieLayer.getCurrentParty().partyRole) {
					pt += " - (" + CookieLayer.getCurrentParty().partyRole
							+ ")";
				}
				pt += "</b>";
				$("#BPartyId").html(pt);
				$("#txtPartyCode").val(data.partyCode);
			}
		};
		var getValue = function() {
			var value = extendId;
			value.birthDate = $("#txtBirthDate").jqxDateTimeInput("getDate") ? $(
					"#txtBirthDate").jqxDateTimeInput("getDate").getTime()
					: null;
			value.idIssueDate = $("#identityDate").jqxDateTimeInput("getDate") ? $(
					"#identityDate").jqxDateTimeInput("getDate").getTime()
					: null;
			value.idIssuePlace = $("#providePlace").jqxComboBox(
					"getSelectedItem") ? $("#providePlace").jqxComboBox(
					"getSelectedItem").value : null;
			value.idNumber = $("#identification").val();
			value.fullName = $("#txtFulName").val();
			value.gender = $("#txtGender").jqxDropDownList("val");
			value.familyId = globalFamilyId;
			value.facebook = $("#txtFacebook").val();
			value.dataSourceId = $("#partyDataSource").val();
			value.partyCode = $("#txtPartyCode").val();
			return value;
		};
		var showLayer = function() {
			$("#familyInfoEditable").removeClass("hide");
			$("#UsingHistory").removeClass("hide");
			$("#businessesInfoEditable").addClass("hide");
			$("#studentInfo").addClass("hide");
			$("#teacherInfo").addClass("hide");
		};
		var validate = function() {
			return $("#familyInfoEditable").jqxValidator("validate");
		};
		return {
			init : function() {
				initJqxElements();
				initValidator();
			},
			setValue : setValue,
			getValue : getValue,
			showLayer : showLayer,
			validate : validate,
			extendId : extendId,
			bindDataToDropdown : bindDataToDropdown
		};
	})();
}