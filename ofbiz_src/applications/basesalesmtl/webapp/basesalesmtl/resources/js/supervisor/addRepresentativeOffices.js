Loading.show();
var UpdateMode = false;
$(document).ready(function() {
	if (partyIdPram) {
		UpdateMode = true;
	}
	AddMTRepresentativeOffice.init();
	Validator.init();
	if (partyIdPram) {
		AddMTRepresentativeOffice.letUpdate(partyIdPram);
		$("#account-info").addClass('hide');
	}
	setTimeout(function() {
		Loading.hide();
	}, 2000);
});

if (typeof (Validator) == "undefined") {
	var Validator = (function() {
		var init = function() {
			if (UpdateMode) {
				$("#generalInfo").jqxValidator({
				    rules: [{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
						rule: function (input, commit) {
							if (!UpdateMode) {
								return true;
							}
							if (input.val()) {
								return true;
							}
							return false;
						}
	          		},
					{ input: '#partyCode', message: multiLang.DmsPartyCodeAlreadyExists, action: 'change',
						rule: function (input, commit) {
							var check = DataAccess.getData({
									url: "checkPartyCode",
									data: {partyId: partyIdPram, partyCode: input.val()},
									source: "check"});
							if ("false" == check) {
								return false;
							}
							return true;
						}
					},
					{ input: '#partyCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
		            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
		            ],
		           position: 'bottom'
				});
			} else {
				$("#generalInfo").jqxValidator({
				    rules: [{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
						rule: function (input, commit) {
							if (!UpdateMode) {
								return true;
							}
							if (input.val()) {
								return true;
							}
							return false;
						}
	          		},
					{ input: '#partyCode', message: multiLang.DmsPartyCodeAlreadyExists, action: 'change',
						rule: function (input, commit) {
							var check = DataAccess.getData({
									url: "checkPartyCode",
									data: {partyId: partyIdPram, partyCode: input.val()},
									source: "check"});
							if ("false" == check) {
								return false;
							}
							return true;
						}
					},
					{ input: '#partyCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
		            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
		            ],
		            position: 'bottom'
				});
				$("#contactInfo").jqxValidator({
				    rules: [{ input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: "keyup, blur",
						    	rule: function (input, commit) {
									if (input.val()) {
										return true;
									}
									return false;
								}
				            },
				            { input: "#txtCountry", message: multiLang.fieldRequired, action: "change",
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
				            },
				            { input: "#tarAddress", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" }],
				           position: 'bottom'
				});
			}
		};
		var hide = function() {
			if (UpdateMode) {
				$("#generalInfo").jqxValidator("hide");
			} else {
				$("#generalInfo").jqxValidator("hide");
				$("#contactInfo").jqxValidator("hide");
			}
		};
		var validate = (function() {
			var generalInfo = function() {
				return $("#generalInfo").jqxValidator("validate");
			};
			var contactInfo = function() {
				if (UpdateMode) {
					return true;
				} else {
					return $("#contactInfo").jqxValidator("validate");
				}
			};
			return {
				generalInfo: generalInfo,
				contactInfo: contactInfo
			}
		})();
		return {
			init: init,
			hide: hide,
			validate: validate
		}
	})();
}

if (typeof (AddMTRepresentativeOffice) == "undefined") {
	var AddMTRepresentativeOffice = (function() {
		var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId, productStoreIds = new Array();
		var initJqxElements = function() {

			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		        if (info.step == 1 && (info.direction == "next")) {
		        	return Validator.validate.generalInfo();
		        }
		        if(info.direction == "previous"){
		        	Validator.hide();
		        }
		    }).on('finished', function(e) {
		    	if (!Validator.validate.contactInfo()){
		    		return false;
		    	}
		    	if (UpdateMode) {
					mesConfirm = multiLang.UpdateConfirm;
				} else {
					mesConfirm = multiLang.CreateNewConfirm;
				}
		    	bootbox.dialog(mesConfirm,
					[{
						"label" : multiLang.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok",
		    		    "callback": function() {
		    		    	Loading.show();
							var url = UpdateMode?"updateMTRepresentativeOffice":"createMTRepresentativeOffice";
							var logoImageUrl;
							if ($('#logoImageUrl').prop('files')[0]) {
								logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
							}
							var data = AddMTRepresentativeOffice.getValue();
							data.logoImageUrl = logoImageUrl;
							DataAccess.execute({ url: url, data: data }, AddMTRepresentativeOffice.notify);
		    		    }
					},
					{
		    		    "label" : multiLang.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove",
		    		}]
				);
		    });

			$("#currencyUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listCurrencyUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#currencyUomId").jqxDropDownList("val", "VND");
			if (!UpdateMode) {
				InternalUtil.initComboboxGeo("", "WARD", "txtWard");
			    InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
			    InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
			    InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
			}
		    $("#jqxNotificationNestedSlide").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#logoImage").click(function() {
				$("#logoImageUrl").click();
			});
			$("#logoImageUrl").change(function(){
				Images.readURL(this, $("#logoImage"));
			});
			if (!UpdateMode) {
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
			}
		};
		var getValue = function() {
			if (UpdateMode) {
				var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
				};
			} else {
				var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						countryGeoId: $("#txtCountry").jqxComboBox("val"),
						stateProvinceGeoId: $("#txtProvince").jqxComboBox("val"),
						districtGeoId: $("#txtCounty").jqxComboBox("val"),
						wardGeoId: $("#txtWard").jqxComboBox("val"),
						address1: $("#tarAddress").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val()
				};
			}
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			return value;
		};
		var extendId = new Object();
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#partyCode").val(data.partyCode);
				$("#groupName").val(data.groupName);
				$("#groupName").val(data.groupNameLocal);
				$("#officeSiteName").val(data.officeSiteName);
				$("#comments").val(data.comments);
				$("#currencyUomId").jqxDropDownList("val", data.currencyUomId);
				$("#taxAuthInfos").val(data.taxAuthInfos);

				if (!UpdateMode) {
					$("#txtCountry").jqxComboBox("val", data.countryGeoId);
					$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
					$("#txtCounty").jqxComboBox("val", data.districtGeoId);
					$("#txtWard").jqxComboBox("val", data.wardGeoId);
					stateProvinceGeoId = data.stateProvinceGeoId;
					districtGeoId = data.districtGeoId;
					wardGeoId = data.wardGeoId;
					$("#tarAddress").val(data.address1);
				}
				$("#txtPhoneNumber").val(data.contactNumber);
				$("#txtEmailAddress").val(data.infoString);
				extendId.contactNumberId = data.contactNumberId;
				extendId.infoStringId = data.infoStringId;
				extendId.addressId = data.addressId;
				extendId.taxAuthInfosfromDate = data.taxAuthInfosfromDate;
				extendId.partyId = data.partyId;
				if (data.logoImageUrl) {
					$("#logoImage").attr("src", data.logoImageUrl);
				}
			}
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadMTRepresentativeOfficeInfo",
				data: {partyId: partyId},
				source: "MTCustomerInfo"});
			AddMTRepresentativeOffice.setValue(data);
		};
		var notify = function(res) {
			Loading.hide();
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
					if (res) {
						if (res['partyId']) {
							location.href = "MTRepresentativeOfficeDetail?partyId=" + res['partyId'];
						}
					}
				}, 2000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			letUpdate: letUpdate,
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
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            img.attr("src", e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		return {
			readURL: readURL,
		};
	})();
}