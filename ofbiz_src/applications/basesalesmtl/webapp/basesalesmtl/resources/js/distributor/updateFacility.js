$(document).ready(function() {
	if (facilityIdParam) {
		Facility.init();
		Facility.setValue(addressData);
	}
});
if (typeof (Facility) == "undefined") {
	var Facility = (function() {
		var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId, geoPointId;
		var initJqxElements = function() {
			var source = { datatype: "json",
					datafields: [{ name: "geoPointId" },
					             { name: "information" }],
					             url: "getGeoPoint" };
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#geoPointId").jqxDropDownList({ source: dataAdapter, theme: "olbius", displayMember: "information", valueMember: "geoPointId",
				width: 218, height: 30, placeHolder: multiLang.filterchoosestring});
			
			InternalUtil.initComboboxGeo("", "WARD", "txtWard");
		    InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
		    InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
		    InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
		    
		    $("#jqxNotificationNestedSlide").jqxNotification({ width: "100%", appendContainer: "#containerSlide", opacity: 0.9, autoClose: true, template: "info" });
		};
		var handlerEvent = function() {
			InternalUtil.initEventComboboxGeo("PROVINCE", "txtCountry", "txtProvince", "", "COUNTRY");
			InternalUtil.initEventComboboxGeo("DISTRICT", "txtProvince", "txtCounty", "txtCountry", "PROVINCE");
			InternalUtil.initEventComboboxGeo("WARD", "txtCounty", "txtWard", "txtProvince", "DISTRICT");
			InternalUtil.initEventComboboxGeo("", "txtWard", null, "txtCounty", "WARD");
			$("#txtCountry").on("bindingComplete", function (event) {
	    		if (countryGeoId) {
	    			$("#txtCountry").jqxComboBox("val", countryGeoId);
	    			countryGeoId = "VNM";
	    		}
	    	});
			$("#txtProvince").on("bindingComplete", function (event) {
				if (stateProvinceGeoId) {
					$("#txtProvince").jqxComboBox("val", stateProvinceGeoId);
					if (!$("#txtProvince").jqxComboBox("getSelectedItem")) {
						$("#txtProvince").jqxComboBox("clearSelection");
						stateProvinceGeoId = null;
					}
				}
			});
			$("#txtCounty").on("bindingComplete", function (event) {
				if (districtGeoId) {
					$("#txtCounty").jqxComboBox("val", districtGeoId);
					if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
						$("#txtCounty").jqxComboBox("clearSelection");
						districtGeoId = null;
					}
				}
			});
			$("#txtWard").on("bindingComplete", function (event) {
				if (wardGeoId) {
					$("#txtWard").jqxComboBox("val", wardGeoId);
					if (!$("#txtWard").jqxComboBox("getSelectedItem")) {
						$("#txtWard").jqxComboBox("clearSelection");
						wardGeoId = null;
					}
				}
			});
			$("#geoPointId").on("bindingComplete", function (event) {
				if (geoPointId) {
					$("#geoPointId").jqxDropDownList("val", geoPointId);
				}
			});
			$("#btnSave").click(function() {
				if ($("#updateFacility").jqxValidator("validate")) {
					$("#btnSave").attr("disabled", true);
					DataAccess.execute({ url: "updateFacilityBasic", data: Facility.getValue() }, Facility.notify);
				}
			});
		};
		var initValidator = function() {
			$("#updateFacility").jqxValidator({
			    rules: [
			            { input: "#facilityName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
			            { input: "#tarAddress", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
						{ input: "#txtCounty", message: multiLang.fieldRequired, action: "keyup, blur",
							rule: function (input, commit) {
								if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
									return true;
								}
								if (input.val()) {
									return true;
								}
								return false;
							}
						}],
						position: 'bottom'
			});
		};
		var getValue = function() {
			var value = new Object();
			value.contactMechId = addressData.contactMechId;
			value.facilityId = facilityIdParam;
			value.facilityName = $("#facilityName").val();
			value.geoPointId = $("#geoPointId").jqxDropDownList('val');
			value.description = $("#description").val();
			
			value.countryGeoId = $("#txtCountry").jqxComboBox('val');
			value.stateProvinceGeoId = $("#txtProvince").jqxComboBox('val');
			value.districtGeoId = $("#txtCounty").jqxComboBox('val');
			value.wardGeoId = $("#txtWard").jqxComboBox('val');
			value.address1 = $("#tarAddress").val();
			value.phoneNumber = $("#txtPhoneNumber").val();
			value.phoneNumberId = $("#txtPhoneNumberId").val();
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtCountry").jqxComboBox("val", data.countryGeoId);
				$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
				$("#txtCounty").jqxComboBox("val", data.districtGeoId);
				$("#txtWard").jqxComboBox("val", data.wardGeoId);
				$("#geoPointId").jqxDropDownList("val", data.geoPointId);
				stateProvinceGeoId = data.stateProvinceGeoId;
				districtGeoId = data.districtGeoId;
				wardGeoId = data.wardGeoId;
				geoPointId = data.geoPointId;
			}
		};
		var notify = function(res) {
			$(window).scrollTop(0);
			$('#jqxNotificationNestedSlide').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'error'});
				$("#notificationContentNestedSlide").text(multiLang.updateError);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedSlide").jqxNotification({ template: 'info'});
				$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				setTimeout(function() {
					location.href = "findFacilityDis";
				}, 2000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handlerEvent();
				initValidator();
			},
			getValue: getValue,
			setValue: setValue,
			notify: notify
		};
	})();
}
if (typeof (InternalUtil) == "undefined") {
	var InternalUtil = (function() {
		var initComboboxGeo = function(geoId, geoTypeId, element) {
			var url = "";
			if(geoTypeId != "COUNTRY" && geoId){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			}else if(geoTypeId == "COUNTRY"){
				url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
			}
			var source = { datatype: "json",
					datafields: [{ name: "geoId" },
					             { name: "geoName" }],
					             url: url,
					             cache: true };
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#" + element).jqxComboBox({ source: dataAdapter, theme: "olbius", displayMember: "geoName", valueMember: "geoId",
				width: 218, height: 30});
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
					    if (elementAffected) {
					    	initComboboxGeo(value, geoTypeId, elementAffected);
						}
					}
				}
			});
		};
		return {
			initComboboxGeo: initComboboxGeo,
			initEventComboboxGeo: initEventComboboxGeo
		};
	})();
}