if (typeof (School) == "undefined") {
	var School = (function() {
		var initJqxElements = function() {

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
				$("#txtStudent").val(data.student);
				$("#txtTeacher").val(data.teacher);
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
			value.numberTeacher = $("#txtTeacher").val();
			value.numberStudent = $("#txtStudent").val();
			value.partyCode = $("#txtPartyCodeBuz").val();
			return value;
		};
		var showLayer = function() {
			$("#businessesInfoEditable").removeClass("hide");
			$("#studentInfo").removeClass("hide");
			$("#teacherInfo").removeClass("hide");
			$("#familyInfoEditable").addClass("hide");
			$("#UsingHistory").addClass("hide");
		};
		return {
			init : function() {
				initJqxElements();
			},
			setValue : setValue,
			getValue : getValue,
			showLayer : showLayer,
			extendId : extendId
		};
	})();
}