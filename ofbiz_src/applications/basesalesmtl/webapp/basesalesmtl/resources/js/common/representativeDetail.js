/* TODO deleted */
if (typeof (Representative) == "undefined") {
	var Representative = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtRFullName").text(data.partyFullName);
				$("#txtRGender").text(data.gender?mapGender[data.gender]:"");
				$("#txtRBirthDate").text(data.birthDate?new Date(data.birthDate).toTimeOlbius():"");
				$("#txtRCountry").text(data.countryGeoId);
				$("#txtRProvince").text(data.stateProvinceGeoId);
				$("#txtRCounty").text(data.districtGeoId);
				$("#txtRWard").text(data.wardGeoId);
				$("#tarRAddress").text(data.address1);
				$("#txtRPhoneNumber").text(data.contactNumber);
				$("#txtREmailAddress").text(data.infoString);
			}
		};
		return {
			setValue: setValue
		};
	})();
}