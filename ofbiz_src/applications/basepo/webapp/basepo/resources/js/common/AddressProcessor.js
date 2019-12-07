if (typeof (AddressProcessor) == "undefined") {
	var AddressProcessor = (function() {
		var countryDDL, provinceDDL, countyDDL, wardDDL, validateVAL = null;
		
		var jqxwindow, clientage, txtCountryValue = "VNM", txtProvinceValue, txtCountyValue, txtWardValue;
		var editTag = $("<a href='javascript:void(0);' style='float: left' class='btn-small' title='" + multiLang.CommonUpdate + "'><i class='icon-edit open-sans'></i></a>");
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				width: 520, theme: theme, height: 360, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#alterCancelAddressProcessor"), modalOpacity: 0.7
			});
			
			jOlbUtil.input.create("#txtAddress1", {width: 218});
			jOlbUtil.input.create("#txtZipCode", {width: 218});
			
			countryDDL = AddressProcessor.initComboboxGeo("", "COUNTRY", "txtCountry");
			provinceDDL = AddressProcessor.initComboboxGeo("", "PROVINCE", "txtProvince");
			countyDDL = AddressProcessor.initComboboxGeo("", "DISTRICT", "txtCounty");
			wardDDL = AddressProcessor.initComboboxGeo("", "WARD", "txtWard");
			
			editTag.insertAfter(clientage);
		};
		var handleEvents = function() {
			jqxwindow.on("open", function () {
				if ($(clientage).data("contactMechId")) {
					$("#addressProcessorTitle").text(multiLang.DmsEditAddress);
				} else {
					$("#addressProcessorTitle").text(multiLang.DmsAddAddress);
				}
			});
			jqxwindow.on("close", function () {
				jqxwindow.jqxValidator("hide");
			});
			$("#alterSaveAddressProcessor").click(function () {
				if (validatorVAL.validate()) {
					AddressProcessor.setContactMechId();
					jqxwindow.jqxWindow("close");
				}
			});
			
			countryDDL.bindingCompleteListener(function(){
				if (txtCountryValue) {
					countryDDL.selectItem([txtCountryValue]);
					//txtCountryValue = "VNM";
				} else {
					//$("#txtProvince").jqxComboBox("clear");
					provinceDDL.clearAll();
				}
			});
			provinceDDL.bindingCompleteListener(function(){
				if (txtProvinceValue) {
					provinceDDL.selectItem([txtProvinceValue]);
					//if (!$("#txtProvince").jqxComboBox("getSelectedItem")) {
					//$("#txtProvince").jqxComboBox("clearSelection");
					//txtProvinceValue = null;
					//}
				} else {
					//$("#txtCounty").jqxComboBox("clear");
					countyDDL.clearAll();
				}
			});
			countyDDL.bindingCompleteListener(function(){
				if (txtCountyValue) {
					countyDDL.selectItem([txtCountyValue]);
					//if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
					//$("#txtCounty").jqxComboBox("clearSelection");
					//txtCountyValue = null;
					//}
				} else {
					//$("#txtWard").jqxComboBox("clear");
					wardDDL.clearAll();
				}
			});
			wardDDL.bindingCompleteListener(function(){
				if (txtWardValue) {
					wardDDL.selectItem([txtWardValue]);
					//if (!$("#txtWard").jqxComboBox("getSelectedItem")) {
					//$("#txtWard").jqxComboBox("clearSelection");
					//txtWardValue = null;
					//}
				}
			});
			
			AddressProcessor.initEventComboboxGeo("PROVINCE", "txtCountry", "txtProvince", "", "COUNTRY");
			AddressProcessor.initEventComboboxGeo("DISTRICT", "txtProvince", "txtCounty", "txtCountry", "PROVINCE");
			AddressProcessor.initEventComboboxGeo("WARD", "txtCounty", "txtWard", "txtProvince", "DISTRICT");
			AddressProcessor.initEventComboboxGeo("", "txtWard", null, "txtCounty", "WARD");
			
			editTag.click(function() {
				AddressProcessor.open($(clientage).data("contactMechId"));
			});
		};
		var initValidator = function() {
			var extendRules = [
						{ input: "#txtZipCode", message: uiLabelMap.BPPostalCodeIsNotValid, action:"keyup, blur",
						    rule: function (input, commit){
						        if (OlbCore.isEmpty(input.val())){
						            return true;
						        }
						        return checkRegex(input.val(), uiLabelMap.BPCheckPostalCode);
						    }
						}
                   ];
	   		var mapRules = [
	                	{input: '#txtAddress1', type: 'validInputNotNull'},
	                	{input: '#txtCountry', type: 'validObjectNotNull', objType: 'dropDownList'},
	                	{input: '#txtProvince', type: 'validObjectNotNull', objType: 'dropDownList'},
	               ];
	   		validatorVAL = new OlbValidator($(jqxwindow), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		var initComboboxGeo = function(geoId, geoTypeId, element) {
			var url = "";
			if(geoTypeId != "COUNTRY" && geoId){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			}else if(geoTypeId == "COUNTRY"){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
			}
			return new OlbDropDownList($("#" + element), null, {
						useUrl: true,
						url: url, 
						filterable: true, 
						theme: theme, 
						root: "listGeo",
						value: "geoName", 
						key: "geoId",
						width: 218, 
						height: 30,
						placeHolder: uiLabelMap.BSClickToChoose
					});
		};
		var initEventComboboxGeo = function (geoTypeId, element, elementAffected, elementParents, thisGeoTypeId) {
			$("#" + element).on("change", function (event) {
				var args = event.args;
				if (args) {
					var index = args.index;
					var item = args.item;
					if (item) {
						var label = item.label;
						var value = item.value;
						if (elementAffected && value) {
							initComboboxGeo(value, geoTypeId, elementAffected);
						}
					}
				}
			});
		};
		var setValue = function(data) {
			data = data.postalAddress;
			if (data) {
				if (data.countryGeoId) {
					txtCountryValue = data.countryGeoId;
					countryDDL.selectItem(data.countryGeoId);
				}
				txtCountryValue = null;
				txtProvinceValue = null;
				txtCountyValue = null;
				txtWardValue = null;
				if (data.stateProvinceGeoId) {
					txtProvinceValue = data.stateProvinceGeoId;
					provinceDDL.selectItem(data.stateProvinceGeoId);
				}
				if (data.districtGeoId) {
					txtCountyValue = data.districtGeoId;
					countyDDL.selectItem(data.districtGeoId);
				}
				if (data.wardGeoId) {
					txtWardValue = data.wardGeoId;
					ward.selectItem(data.wardGeoId);
				}
				if (data.address1) {
					$("#txtAddress1").val(data.address1);
				}
				$("#txtZipCode").val(data.postalCode);
			}
		};
		var getValue = function() {
			var value = new Object();
			value.contactMechId = $(clientage).data("contactMechId");
			value.countryGeoId = countryDDL.getValue();
			value.stateProvinceGeoId = provinceDDL.getValue();
			value.districtGeoId = countyDDL.getValue();
			value.wardGeoId = wardDDL.getValue();
			value.address1 = $("#txtAddress1").val();
			value.postalCode = $("#txtZipCode").val();
			return value;
		};
		var setContactMechId = function() {
			var address = "";
			if ($("#txtAddress1").val()) {
				address += $("#txtAddress1").val();
			}
			address += ", " + wardDDL.getLabel();
			address += ", " + countyDDL.getLabel();
			address += ", " + provinceDDL.getLabel();
			address += ", " + countryDDL.getLabel();
			$(clientage).val(address);
		};
		var clean = function() {
			countyDDL.clearAll();
			wardDDL.clearAll();
			$("#txtAddress1").val("");
			$("#txtZipCode").val("");
		};
		var loadPostalAddress = function(contactMechId) {
			DataAccess.execute({
			url: "loadPostalAddress",
			data: { contactMechId: contactMechId }
			}, AddressProcessor.setValue);
		};
		var open = function(contactMechId) {
			if (!contactMechId) {
				clean();
			}
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
			jqxwindow.jqxWindow("open");
		};
		return {
			init: function() {
				clientage = $("input[AddressProcessor]")[0];
				jqxwindow = $("#alterpopupWindowAddressProcessor");
				initJqxElements();
				handleEvents();
				initValidator();
				if (organizationId == "MB") {
					txtProvinceValue = "VNM-HN2";
				} else if (organizationId == "MN") {
					txtProvinceValue = "VNM-HCM";
				}
			},
			initComboboxGeo: initComboboxGeo,
			initEventComboboxGeo: initEventComboboxGeo,
			setValue: setValue,
			getValue: getValue,
			setContactMechId: setContactMechId,
			loadPostalAddress: loadPostalAddress,
			open: open
		};
	})();
}