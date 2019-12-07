$(document).ready(function() {
	AddPrimaryAddress.init();
});
if (typeof (AddPrimaryAddress) == "undefined") {
	var AddPrimaryAddress = (function() {
		var jqxwindow, countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({
				theme: "olbius", width: 850, height: 210, maxWidth: 1845, resizable: false, isModal: true, autoOpen: false,
				cancelButton: $("#cancelAddAddress"), modalOpacity: 0.7
			});
			InternalUtil.initComboboxGeo("", "WARD", "txtWard");
		    InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
		    InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
		    InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
		};
		var handleEvents = function() {
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
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#txtProvince").jqxComboBox("clearSelection");
				$("#txtCounty").jqxComboBox("clearSelection");
				$("#txtWard").jqxComboBox("clearSelection");
				$("#tarAddress").val("");
				$("#txtContactMechId").val("");
			});
			$("#saveAddAddress").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					var data = AddPrimaryAddress.getValue();
					if (data.contactMechId) {
						//	updatePartyPostalAddress
						DataAccess.execute({
							url: "updatePartyPostalAddressAjax",
							data: data
							}, PrimaryAddress.notify);
					} else {
						//	createPostalAddressAndPurpose
						DataAccess.execute({
							url: "createPostalAddressAjax",
							data: data
							}, PrimaryAddress.notify);
					}
					jqxwindow.jqxWindow("close");
				}
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator({
			    rules: [{ input: "#txtCountry", message: multiLang.fieldRequired, action: "change",
					    	rule: function (input, commit) {
								if (input.val()) {
									return true;
								}
								return false;
							}
			            },
			            { input: "#txtProvince", message: multiLang.fieldRequired, action: "change",
			            	rule: function (input, commit) {
			            		if (input.val()) {
			            			return true;
			            		}
			            		return false;
			            	}
			            }],
			           position: 'bottom'
			});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#addAddressTitle").text(multiLang.DmsEditAddress);
				$("#txtContactMechId").val(data.contactMechId);
				$("#txtCountry").jqxComboBox("val", data.countryGeoId);
				$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
				$("#txtCounty").jqxComboBox("val", data.districtGeoId);
				$("#txtWard").jqxComboBox("val", data.wardGeoId);
				stateProvinceGeoId = data.stateProvinceGeoId;
				districtGeoId = data.districtGeoId;
				wardGeoId = data.wardGeoId;
				$("#tarAddress").val(data.address1);
			} else {
				$("#addAddressTitle").text(multiLang.DmsAddAddress);
			}
		};
		var getValue = function() {
			var value = {
				countryGeoId: $("#txtCountry").jqxComboBox("val"),
				stateProvinceGeoId: $("#txtProvince").jqxComboBox("val"),
				districtGeoId: $("#txtCounty").jqxComboBox("val"),
				wardGeoId: $("#txtWard").jqxComboBox("val"),
				address1: $("#tarAddress").val(),
				contactMechId: $("#txtContactMechId").val(),
				partyId: $("#txtPartyId").val(),
				city: $("#txtProvince").jqxComboBox("val"),
				contactMechPurposeTypeId: "PRIMARY_LOCATION",
				postalCode: "70000"
			};
			return value;
		};
		var open = function(data) {
			setValue(data);
			var wtmp = window;
	    	var tmpwidth = jqxwindow.jqxWindow("width");
	    	jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	jqxwindow.jqxWindow("open");
		};
		var _delete = function(data) {
			if (!_.isEmpty(data)) {
				bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
					if (result) {
						DataAccess.execute({
							url: "deletePartyContactMechPurposeAjax",
							data: {partyId: data.partyId, contactMechId: data.contactMechId, contactMechPurposeTypeId: data.contactMechPurposeTypeId }
							}, PrimaryAddress.notify);
					}
				});
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowAddPrimaryAddress");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			setValue: setValue,
			getValue: getValue,
			open: open,
			_delete: _delete
		}
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
				width: 218, height: 30, dropDownHeight: 150});
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