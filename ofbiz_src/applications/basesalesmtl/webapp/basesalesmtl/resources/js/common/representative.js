/* TODO deleted */
if (typeof (Representative) == "undefined") {
	var Representative = (function() {
		var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId;
	    var rCountryCBB;
		var rStateProvinceGeoCBB;
		var rDistrictGeoCBB;
		var rWardGeoCBB;
		var initJqxElements = function() {
			$("#txtRBirthDate").jqxDateTimeInput({theme: "olbius", width: 218, height: 30 });
			$("#txtRBirthDate").jqxDateTimeInput('setDate', null);
			
			$("#txtRGender").jqxDropDownList({theme: 'olbius', width: 218, height: 30, selectedIndex: 1, source: listGender, displayMember: "label",
				valueMember: "value", autoDropDownHeight: true, placeHolder: multiLang.filterchoosestring });
			
			rWardGeoCBB = InternalUtilNew.initComboboxGeo("", "WARD", "txtRWard");
			rDistrictGeoCBB = InternalUtilNew.initComboboxGeo("", "DISTRICT", "txtRCounty");
			rStateProvinceGeoCBB = InternalUtilNew.initComboboxGeo("", "PROVINCE", "txtRProvince");
			rCountryCBB = InternalUtilNew.initComboboxGeo("", "COUNTRY", "txtRCountry");
		};
		var handleEvents = function() {
            rCountryCBB.selectListener(function(itemData, index){
                if(itemData){
        			rStateProvinceGeoCBB.clearAll();
        			rDistrictGeoCBB.clearAll();
        			rWardGeoCBB.clearAll();
                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'PROVINCE', rStateProvinceGeoCBB, "");
                 }
            });
            rStateProvinceGeoCBB.selectListener(function(itemData, index){
                if(itemData){
        	        rDistrictGeoCBB.clearAll();
        	        rWardGeoCBB.clearAll();
                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'DISTRICT', rDistrictGeoCBB, "");
                }
            });
            rDistrictGeoCBB.selectListener(function(itemData, index){
        	    rWardGeoCBB.clearAll();
                if(itemData){
                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'WARD', rWardGeoCBB, "");
                }
            });
		};
		var extendId = new Object();
		var getValue = function() {
			var value = {
					partyFullName: $("#txtRFullName").val(),
					gender: $("#txtRGender").jqxDropDownList("val"),
					birthDate: $("#txtRBirthDate").jqxDateTimeInput('getDate')?$("#txtRBirthDate").jqxDateTimeInput('getDate').getTime():null,
					countryGeoId: OlbCore.isNotEmpty(rCountryCBB.getValue())?rCountryCBB.getValue():"",
					stateProvinceGeoId: OlbCore.isNotEmpty(rStateProvinceGeoCBB.getValue())?rStateProvinceGeoCBB.getValue():"",
					districtGeoId: OlbCore.isNotEmpty(rDistrictGeoCBB.getValue())?rDistrictGeoCBB.getValue():"",
					wardGeoId: OlbCore.isNotEmpty(rWardGeoCBB.getValue())?rWardGeoCBB.getValue():"",
					address1: $("#tarRAddress").val(),
					contactNumber: $("#txtRPhoneNumber").val(),
					infoString: $("#txtREmailAddress").val()
			};
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			return {representative: JSON.stringify(value)};
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtRFullName").val(data.partyFullName.trim());
				$("#txtRGender").jqxDropDownList("val", data.gender);
				$("#txtRBirthDate").jqxDateTimeInput('setDate', data.birthDate?new Date(data.birthDate):null);
				$("#txtRCountry").jqxComboBox("val", data.countryGeoId);
				$("#txtRProvince").jqxComboBox("val", data.stateProvinceGeoId);
				$("#txtRCounty").jqxComboBox("val", data.districtGeoId);
				$("#txtRWard").jqxComboBox("val", data.wardGeoId);
				stateProvinceGeoId = data.stateProvinceGeoId;
				districtGeoId = data.districtGeoId;
				wardGeoId = data.wardGeoId;
				$("#tarRAddress").val(data.address1);
				$("#txtRPhoneNumber").val(data.contactNumber);
				$("#txtREmailAddress").val(data.infoString);
				extendId.contactNumberId = data.contactNumberId;
				extendId.infoStringId = data.infoStringId;
				extendId.addressId = data.addressId;
				extendId.partyId = data.partyId;
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue
		};
	})();
}