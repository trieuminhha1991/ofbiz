/* TODO deleted */
Loading.show();
var UpdateMode = false;
$(document).ready(function() {
	if (partyIdPram) {
		UpdateMode = true;
	}
	AddDistributor.init();
	Validator.init();
	if (partyIdPram) {
		AddDistributor.letUpdate(partyIdPram);
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
									if (!UpdateMode) {
										var userLoginId = input.val();
										if (userLoginId) {
											var check = DataAccess.getData({
												url: "checkUserLoginId",
												data: {userLoginId: userLoginId},
												source: "check"});
											if ("false" == check) {
												 return false;
											}
										}
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
							{ input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
								rule: function (input, commit) {
									var value = (input.jqxDropDownButton("val")+"").trim();
									if (value) {
										return true;
									}
									return false;
								}
							},
				            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
				            { input: "#txtPhoneNumber", message: multiLang.fieldRequired, action: "keyup, blur",
						    	rule: function (input, commit) {
									if (input.val()) {
										return true;
									}
									return false;
								}
				            }],
				           position: 'bottom'
				});
				$("#representativeInfo").jqxValidator({
				    rules: [{ input: '#txtRBirthDate', message: multiLang.dateNotValid, action: 'valueChanged', 
					           	rule: function (input, commit) {
					           		var currentTime = new Date().getTime();
					           		var value = 0;
					           		input.jqxDateTimeInput('getDate')?value=input.jqxDateTimeInput('getDate').getTime():value;
					           		if (value == 0) {
										return true;
									}
					           		if (currentTime > value) {
					           			return true;
					           		}
					           		return false;
					           	}
					        }],
				           position: 'bottom'
				});
			} else {
				$("#generalInfo").jqxValidator({
				    rules: [{ input: "#partyCode", message: multiLang.fieldRequired, action: 'blur',
								rule: function (input, commit) {
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
									if (!UpdateMode) {
										var userLoginId = input.val();
										if (userLoginId) {
											var check = DataAccess.getData({
												url: "checkUserLoginId",
												data: {userLoginId: userLoginId},
												source: "check"});
											if ("false" == check) {
												 return false;
											}
										}
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
							{ input: "#divSupervisor", message: multiLang.fieldRequired, action: "close",
								rule: function (input, commit) {
									var value = (input.jqxDropDownButton("val")+"").trim();
									if (value) {
										return true;
									}
									return false;
								}
							},
				            { input: "#groupName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
					        { input: '#txtProductStore', message: multiLang.fieldRequired, action: 'change', 
					        	rule: function (input, commit) {
					        		var values = input.jqxComboBox('getSelectedItems');
					        		if (_.isEmpty(values)) {
					        			return false;
					        		}
					        		return true;
					        	}
					        },
					        { input: '#txtCatalog', message: multiLang.fieldRequired, action: 'change', 
					        	rule: function (input, commit) {
					        		var values = input.jqxComboBox('getSelectedItems');
					        		if (_.isEmpty(values)) {
					        			return false;
					        		}
					        		return true;
					        	}
					        }],
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
				$("#facilityInfo").jqxValidator({
				    rules: [{ input: "#txtFacilityName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
				            { input: "#txtFCountry", message: multiLang.fieldRequired, action: "change",
						    	rule: function (input, commit) {
									if (input.val()) {
										return true;
									}
									return false;
								}
				            },
				            { input: "#txtFProvince", message: multiLang.fieldRequired, action: "change",
				            	rule: function (input, commit) {
				            		if (input.val()) {
				            			return true;
				            		}
				            		return false;
				            	}
				            },
				            { input: "#tarFAddress", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" }],
				           position: 'bottom'
				});
				$("#accountInfo").jqxValidator({
				    rules: [{ input: "#password", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
							{ input : '#password', message: multiLang.BEPasswordShort, action: 'change',
								rule: function(input, label){
									if (UpdateMode) {
										return true;
									}
									if(input.val().length > 5){
										return true;
									}
									return false;
								}
							},
							{ input: "#passwordVerify", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
							{ input: "#passwordVerify", message: multiLang.BEPasswordNotMatch, action: "change",
								rule: function (input, commit) {
									if (input.val() == $("#password").val()) {
										return true;
									}
									return false;
								}
							}],
				           position: 'bottom'
				});
				$("#representativeInfo").jqxValidator({
				    rules: [{ input: '#txtRBirthDate', message: multiLang.dateNotValid, action: 'valueChanged', 
					           	rule: function (input, commit) {
					           		var currentTime = new Date().getTime();
					           		var value = 0;
					           		input.jqxDateTimeInput('getDate')?value=input.jqxDateTimeInput('getDate').getTime():value;
					           		if (value == 0) {
										return true;
									}
					           		if (currentTime > value) {
					           			return true;
					           		}
					           		return false;
					           	}
					        }],
				           position: 'bottom'
				});
			}
		};
		var hide = function() {
			if (UpdateMode) {
				$("#generalInfo").jqxValidator("hide");
				$("#representativeInfo").jqxValidator("hide");
			} else {
				$("#generalInfo").jqxValidator("hide");
				$("#contactInfo").jqxValidator("hide");
				$("#facilityInfo").jqxValidator("hide");
				$("#representativeInfo").jqxValidator("hide");
				$("#accountInfo").jqxValidator("hide");
			}
		};
		var validate = (function() {
			var generalInfo = function() {
				return $("#generalInfo").jqxValidator("validate");
			};
			var representativeInfo = function() {
				return $("#representativeInfo").jqxValidator("validate");
			};
			var contactInfo = function() {
				if (UpdateMode) {
					return true;
				} else {
					return $("#contactInfo").jqxValidator("validate");
				}
			};
			var facilityInfo = function() {
				if (UpdateMode) {
					return true;
				} else {
					return $("#facilityInfo").jqxValidator("validate");
				}
			};
			var accountInfo = function() {
				if (UpdateMode) {
					return true;
				} else {
					return $("#accountInfo").jqxValidator("validate");
				}
			};
			return {
				generalInfo: generalInfo,
				contactInfo: contactInfo,
				facilityInfo: facilityInfo,
				representativeInfo: representativeInfo,
				accountInfo: accountInfo
			}
		})();
		return {
			init: init,
			hide: hide,
			validate: validate
		}
	})();
}
if (typeof (AddDistributor) == "undefined") {
	var AddDistributor = (function() {
		var countryGeoId="VNM";
		var initJqxElements = function() {
			
			$(document).ready(function() {
				$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
			        if (info.step == 1 && (info.direction == "next")) {
			        	return Validator.validate.generalInfo();
			        } else if (info.step == 2 && (info.direction == "next")) {
			        	return Validator.validate.contactInfo();
			        } else if(info.step == 3 && (info.direction == "next")) {
			        	return Validator.validate.facilityInfo();
					} else if(info.step == 4 && (info.direction == "next")) {
						return Validator.validate.representativeInfo();
					}
			        if (info.direction == "previous") {
			        	Validator.hide();
			        }
			    }).on('finished', function(e) {
			    	if (!Validator.validate.accountInfo()){
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
								var url = UpdateMode?"updateDistributor":"createDistributor";
								var logoImageUrl;
								if ($('#logoImageUrl').prop('files')[0]) {
									logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
								}
								var data = AddDistributor.getValue();
								data.logoImageUrl = logoImageUrl;
								DataAccess.execute({ url: url, data: data }, AddDistributor.notify);
			    		    }
						},
						{
			    		    "label" : multiLang.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove",
			    		}]
					);
			    });
			});
			
			$("#currencyUomId").jqxDropDownList({ theme: "olbius", width: 218, height: 30, source: listCurrencyUom, displayMember: "description", valueMember: "uomId", placeHolder: multiLang.filterchoosestring});
			$("#currencyUomId").jqxDropDownList("val", "VND");
			if (!UpdateMode) {
				InternalUtil.initComboboxGeo("", "WARD", "txtWard");
			    InternalUtil.initComboboxGeo("", "DISTRICT", "txtCounty");
			    InternalUtil.initComboboxGeo("", "PROVINCE", "txtProvince");
			    InternalUtil.initComboboxGeo("", "COUNTRY", "txtCountry");
			    var source = { datatype: "json",
						datafields: [{ name: "productStoreId" },
						             { name: "storeName" }],
						             url: "loadProductStores?getAll=N"};
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txtProductStore").jqxComboBox({ theme: 'olbius', source: dataAdapter, width: 218, height: 30, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true});
				
				var source = { datatype: "json",
						datafields: [{ name: "prodCatalogId" },
						             { name: "catalogName" }],
						             url: "loadProdCatalogs"};
				var dataAdapter = new $.jqx.dataAdapter(source);
				$("#txtCatalog").jqxComboBox({ theme: 'olbius', source: dataAdapter, width: 218, height: 30, displayMember: "catalogName", valueMember: "prodCatalogId", multiSelect: true, dropDownHeight: 150});
			}
		    var initSupervisorDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{text: multiLang.BSSupervisorId, datafield: "partyCode", width: 150},
				               {text: multiLang.BSSupervisor, datafield: "groupName"}];
				GridUtils.initDropDownButton({
					url: "JQGetListGTSupervisorDepartment", autorowheight: true, filterable: true, showfilterrow: true,
					width: width ? width : 600, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridSupervisor").jqxGrid("clearfilters");
								return true;
							}
						}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var str = row.groupName + "[" + row.partyCode + "]";
					return str;
				});
			};
			initSupervisorDrDGrid($("#divSupervisor"),$("#jqxgridSupervisor"), 600);
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
				$("#partyCode").on('keyup', function() {
					$("#txtUserLoginID").val($("#partyCode").val());
				});
			}
			$("#copyAddress").click(function() {
				AddFacility.setValue(AddDistributor.getValue());
			});
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
						supervisorId: (Grid.getDropDownValue($("#divSupervisor"))+"").trim(),
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
						infoString: $("#txtEmailAddress").val(),
						currentPassword: $("#password").val(),
						currentPasswordVerify: $("#passwordVerify").val(),
						supervisorId: (Grid.getDropDownValue($("#divSupervisor"))+"").trim(),
						productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore")),
						productCatalogs: LocalUtil.getValueSelectedJqxComboBox($("#txtCatalog"))
				};
			}
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			if (UpdateMode) {
				value = _.extend(value, Representative.getValue());
			} else {
				value = _.extend(value, Representative.getValue(), AddFacility.getValue());
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
				Representative.setValue(data.representative);
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
				Grid.setDropDownValue($("#divSupervisor"), data.supervisorId, data.supervisor);
			}
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadDistributorInfo",
				data: {partyId: partyId},
				source: "distributorInfo"});
			AddDistributor.setValue(data);
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
				if (UpdateMode) {
					$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				} else {
					$("#notificationContentNestedSlide").text(multiLang.addSuccess);
				}
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				setTimeout(function() {
					if (res) {
						if (res['partyId']) {
							location.href = "DistributorDetail?partyId=" + res['partyId'];
						}
					}
				}, 2000);
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				Representative.init();
				AddFacility.init();
			},
			getValue: getValue,
			setValue: setValue,
			letUpdate: letUpdate,
			notify: notify
		};
	})();
}

if (typeof (AddFacility) == "undefined") {
	var AddFacility = (function() {
		var countryGeoId="VNM", stateProvinceGeoId, districtGeoId, wardGeoId;
		var initJqxElements = function() {
			if (!UpdateMode) {
				InternalUtil.initComboboxGeo("", "WARD", "txtFWard");
			    InternalUtil.initComboboxGeo("", "DISTRICT", "txtFCounty");
			    InternalUtil.initComboboxGeo("", "PROVINCE", "txtFProvince");
			    InternalUtil.initComboboxGeo("", "COUNTRY", "txtFCountry");
			}
		};
		var handleEvents = function() {
			if (!UpdateMode) {
				InternalUtil.initEventComboboxGeo("PROVINCE", "txtFCountry", "txtFProvince", "", "COUNTRY");
				InternalUtil.initEventComboboxGeo("DISTRICT", "txtFProvince", "txtFCounty", "txtFCountry", "PROVINCE");
				InternalUtil.initEventComboboxGeo("WARD", "txtFCounty", "txtFWard", "txtFProvince", "DISTRICT");
				InternalUtil.initEventComboboxGeo("", "txtFWard", null, "txtFCounty", "WARD");
				$("#txtFCountry").on("bindingComplete", function (event) {
		    		if (countryGeoId) {
		    			$("#txtFCountry").jqxComboBox("val", countryGeoId);
		    			countryGeoId = "VNM";
		    		}
		    	});
				$("#txtFProvince").on("bindingComplete", function (event) {
					if (stateProvinceGeoId) {
						$("#txtFProvince").jqxComboBox("val", stateProvinceGeoId);
						if (!$("#txtFProvince").jqxComboBox("getSelectedItem")) {
							$("#txtFProvince").jqxComboBox("clearSelection");
							stateProvinceGeoId = null;
						}
					}
				});
				$("#txtFCounty").on("bindingComplete", function (event) {
					if (districtGeoId) {
						$("#txtFCounty").jqxComboBox("val", districtGeoId);
						if (!$("#txtFCounty").jqxComboBox("getSelectedItem")) {
							$("#txtFCounty").jqxComboBox("clearSelection");
							districtGeoId = null;
						}
					}
				});
				$("#txtFWard").on("bindingComplete", function (event) {
					if (wardGeoId) {
						$("#txtFWard").jqxComboBox("val", wardGeoId);
						if (!$("#txtFWard").jqxComboBox("getSelectedItem")) {
							$("#txtFWard").jqxComboBox("clearSelection");
							wardGeoId = null;
						}
					}
				});
			}
		};
		var getValue = function() {
			var value = {
					countryGeoId: $("#txtFCountry").jqxComboBox("val"),
					stateProvinceGeoId: $("#txtFProvince").jqxComboBox("val"),
					districtGeoId: $("#txtFCounty").jqxComboBox("val"),
					wardGeoId: $("#txtFWard").jqxComboBox("val"),
					address1: $("#tarFAddress").val(),
					contactNumber: $("#txtFPhoneNumber").val(),
					facilityName: $("#txtFacilityName").val()
			};
			return {facility: JSON.stringify(value)};
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#txtFCountry").jqxComboBox("val", data.countryGeoId);
				$("#txtFProvince").jqxComboBox("val", data.stateProvinceGeoId);
				$("#txtFCounty").jqxComboBox("val", data.districtGeoId);
				$("#txtFWard").jqxComboBox("val", data.wardGeoId);
				stateProvinceGeoId = data.stateProvinceGeoId;
				districtGeoId = data.districtGeoId;
				wardGeoId = data.wardGeoId;
				$("#tarFAddress").val(data.address1);
				$("#txtFPhoneNumber").val(data.contactNumber);
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