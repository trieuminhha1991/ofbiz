if (typeof (Bussinesses) == "undefined") {
	var Bussinesses = (function() {
		var initJqxElements = function() {
			$("#txtGenderBusinesses").jqxDropDownList({
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
			$("#txtBirthDateBusinesses").jqxDateTimeInput({
				theme : theme,
				width : width
			});
			$("#txtBirthDateBusinesses").jqxDateTimeInput("setDate", null);
			$("#txtProvideDateBusinesses").jqxDateTimeInput({
				theme : theme,
				width : width
			});
			$("#txtProvideDateBusinesses").jqxDateTimeInput("setDate", null);
			Processor.initComboboxGeo("VNM", "PROVINCE",
					"txtProvidePlaceBusinesses");
			/*$("#txtFacebookBussiness").jqxInput({
				width : width,
				theme : theme,
				height : 25
			});*/
			$("#partyDataSourceBussiness").jqxInput({
				width : width,
				theme : theme,
				height : 25
			});
		};
		var initValidator = function() {
			$("#businessesInfoEditable")
					.jqxValidator(
							{
								rules : [
										{
											input : "#txtPartyCodeBuz",
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
											input : "#txtPartyCodeBuz",
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
											input : "#txtCorporationName",
											message : multiLang.fieldRequired,
											action : "blur",
											rule : "required"
										},
										{
											input : "#txtStudent",
											message : multiLang.DmsQuantityNotValid,
											action : "blur, change",
											rule : function(input, value) {
												var val = input.val();
												if (val >= 0
														|| globalPartyTypeId == "BUSINESSES") {
													return true;
												}
												return false;
											}
										},
										{
											input : "#txtTeacher",
											message : multiLang.DmsQuantityNotValid,
											action : "blur, change",
											rule : function(input, value) {
												var val = input.val();
												if (val >= 0
														|| globalPartyTypeId == "BUSINESSES") {
													return true;
												}
												return false;
											}
										},
										{
											input : "#txtIdentificationBusinesses",
											message : multiLang.IdentityNotValid,
											action : "blur",
											rule : function(input, value) {
												var val = input.val();
												if (isNaN(val)) {
													return false;
												}
												return true;
											}
										},
										{
											input : "#txtSdtBusinesses",
											message : multiLang.PhoneNotValid,
											action : "blur",
											rule : function(input, value) {
												var val = input.val();
												if (isNaN(val)) {
													return false;
												}
												return true;
											}
										},
										{
											input : "#partyDataSourceBussiness",
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
											input : "#txtBirthDateBusinesses",
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
												if (currentTime > value) {
													return true;
												}
												return false;
											}
										},
										{
											input : "#txtProvideDateBusinesses",
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
												$("#txtBirthDateBusinesses")
														.jqxDateTimeInput(
																"getDate") ? birthDate = $(
														"#txtBirthDateBusinesses")
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
		var extendId = new Object();
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				extendId = {};
				extendId.partyId = data.partyId;
				extendId.emailAddressId = data.emailAddressId;
				extendId.facebookId = data.facebookId;
				extendId.phoneHomeId = data.phoneHomeId;
				extendId.phoneWorkId = data.phoneWorkId;
				extendId.phoneMobileId = data.phoneMobileId;
				extendId.representativeMemberId = data.representative.partyId;
				extendId.representativeMemberPhoneId = data.representative.infoContactMechParty.phoneMobileId;
				if (data.groupName) {
					$("#txtCorporationName").val(data.groupName.trim());
				}
				$("#txtWebsite").val(data.officeSiteName);
				$("#txtDescription").val(data.comments);
				//$("#txtFacebookBussiness").val(data.facebook);
				$("#txtFacebook").val(data.facebook);
				$("#partyDataSourceBussiness").val(data.dataSourceId);
				$("#txtIdentificationBusinesses").val(
						data.representative.idNumber);
				$("#txtGenderBusinesses").val(data.representative.gender);
				if (data.representative.birthDate) {
					$("#txtBirthDateBusinesses").jqxDateTimeInput("setDate",
							new Date(data.representative.birthDate));
				}
				var first = data.representative.firstName ? data.representative.firstName
						: "";
				var middle = data.representative.middleName ? data.representative.middleName
						: "";
				var last = data.representative.lastName ? data.representative.lastName
						: "";
				var full = last + " " + middle + " " + first;
				$("#txtFullNameBusinesses").val(full.trim());
				$("#txtProvidePlaceBusinesses").val(
						data.representative.idIssuePlace);
				$("#txtEmailBusinesses").val(data.representative.emailAddress);
				$("#txtSdtBusinesses").val(
						data.representative.infoContactMechParty.phoneMobile);
				var is = data.representative.idIssueDate ? new Date(
						data.representative.idIssueDate) : null;
				$("#txtProvideDateBusinesses").jqxDateTimeInput("setDate", is);
				$("#txtProvidePlaceBusinesses").jqxComboBox("val",
						data.representative.idIssuePlace);
				var pt = "<b>";
				if (CookieLayer.getCurrentParty().partyRole) {
					pt += " - (" + CookieLayer.getCurrentParty().partyRole
							+ ")";
				}
				pt += "</b>";
				$("#BPartyIdBussiness").html(pt);
				$("#txtPartyCodeBuz").val(data.partyCode);
			}
		};
		var getValue = function() {
			var birthDate = $("#txtBirthDateBusinesses").jqxDateTimeInput(
					"getDate") ? $("#txtBirthDateBusinesses").jqxDateTimeInput(
					"getDate").getTime() : null;
			var idIssueDate = $("#txtProvideDateBusinesses").jqxDateTimeInput(
					"getDate") ? $("#txtProvideDateBusinesses")
					.jqxDateTimeInput("getDate").getTime() : null;
			var idIssuePlace = $("#txtProvidePlaceBusinesses").jqxComboBox(
					"getSelectedItem") ? $("#txtProvidePlaceBusinesses")
					.jqxComboBox("getSelectedItem").value : null;
			var value = extendId;
			value.groupName = $("#txtCorporationName").val();
			value.officeSiteName = $("#txtWebsite").val();
			value.comments = $("#txtDescription").val();
			value.fullName = $("#txtFullNameBusinesses").val();
			value.sdt = $("#txtSdtBusinesses").val();
			value.gender = $("#txtGenderBusinesses").jqxDropDownList("val");
			value.idNumber = $("#txtIdentificationBusinesses").val();
			value.birthDate = birthDate;
			value.idIssueDate = idIssueDate;
			value.idIssuePlace = idIssuePlace;
			//value.facebook = $("#txtFacebookBussiness").val();
			value.facebook = $("#txtFacebook").val();
			value.dataSourceId = $("#partyDataSourceBussiness").val();
			value.partyCode = $("#txtPartyCodeBuz").val();
			return value;
		};
		var showLayer = function() {
			$("#businessesInfoEditable").removeClass("hide");
			$("#familyInfoEditable").addClass("hide");
			$("#UsingHistory").addClass("hide");
			$("#studentInfo").addClass("hide");
			$("#teacherInfo").addClass("hide");
		};
		var validate = function() {
			return $("#businessesInfoEditable").jqxValidator("validate");
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
			extendId : extendId
		};
	})();
}