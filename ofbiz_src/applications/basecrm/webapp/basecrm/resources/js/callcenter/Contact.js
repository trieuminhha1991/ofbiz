if (typeof (ContactLayer) == "undefined") {
	var ContactLayer = (function() {
		var txtCountryValue = "VNM", txtProvinceValue, txtCountyValue, txtWardValue;

		var initJqxElements = function() {
			$("#txtPrimaryPhone").jqxDropDownList({
				theme : "olbius",
				width : width,
				height : 25,
				source : listPhone,
				displayMember : "label",
				valueMember : "value",
				placeHolder : multiLang.filterchoosestring,
				autoDropDownHeight : true
			});
			$("#txtShippingPhone").jqxDropDownList({
				theme : "olbius",
				width : width,
				height : 25,
				source : listPhone,
				displayMember : "label",
				valueMember : "value",
				placeHolder : multiLang.filterchoosestring,
				autoDropDownHeight : true
			});
			Processor.initComboboxGeo("", "WARD", "txtWard2");
			Processor.initComboboxGeo("", "DISTRICT", "txtCounty2");
			Processor.initComboboxGeo("", "PROVINCE", "txtProvince2");
			Processor.initComboboxGeo("", "COUNTRY", "txtCountry2");
		};
		var handleEvents = function() {
			$("#txtCountry2").on("bindingComplete", function(event) {
				if (txtCountryValue) {
					$("#txtCountry2").jqxComboBox("val", txtCountryValue);
					txtCountryValue = "VNM";
				}
			});

			$("#txtProvince2").on("bindingComplete", function(event) {
				if (txtProvinceValue) {
					$("#txtProvince2").jqxComboBox("val", txtProvinceValue);
					if (!$("#txtProvince2").jqxComboBox("getSelectedItem")) {
						$("#txtProvince2").jqxComboBox("clearSelection");
						txtProvinceValue = null;
					}
				}
			});
			$("#txtCounty2").on("bindingComplete", function(event) {
				if (txtCountyValue) {
					$("#txtCounty2").jqxComboBox("val", txtCountyValue);
					if (!$("#txtCounty2").jqxComboBox("getSelectedItem")) {
						$("#txtCounty2").jqxComboBox("clearSelection");
						txtCountyValue = null;
					}
				}
			});
			$("#txtWard2").on("bindingComplete", function(event) {
				if (txtWardValue) {
					$("#txtWard2").jqxComboBox("val", txtWardValue);
					if (!$("#txtWard2").jqxComboBox("getSelectedItem")) {
						$("#txtWard2").jqxComboBox("clearSelection");
						txtWardValue = null;
					}
				}
			});
			$("#txtPrimaryPhone").on(
					"select",
					function(event) {
						var args = event.args;
						if (args) {
							var index = args.index;
							var item = args.item;
							var label = item.label;
							var value = item.value;
							var phoneChoice = "";
							var element = "";
							switch (value) {
							case "PHONE_MOBILE":
								phoneChoice = $("#txtMobilePhone").val();
								element = "txtMobilePhone";
								break;
							case "PHONE_WORK":
								phoneChoice = $("#txtOfficePhone").val();
								element = "txtOfficePhone";
								break;
							case "PHONE_HOME":
								phoneChoice = $("#txtHomePhone").val();
								element = "txtHomePhone";
								break;
							default:
								break;
							}
							if (!phoneChoice) {
								if (element == "txtMobilePhone") {
									$("#" + element).notify(
											multiLang.notEnterAPhoneNumber, {
												position : "left bottom",
												className : "error"
											});
								} else {
									$("#" + element).notify(
											multiLang.notEnterAPhoneNumber, {
												position : "right bottom",
												className : "error"
											});
								}
								$("#txtPrimaryPhone").jqxDropDownList(
										"clearSelection");
							}
						}
					});
			$("#txtShippingPhone").on(
					"select",
					function(event) {
						var args = event.args;
						if (args) {
							var index = args.index;
							var item = args.item;
							var label = item.label;
							var value = item.value;
							var phoneChoice = "";
							var phoneChoice = "";
							switch (value) {
							case "PHONE_MOBILE":
								phoneChoice = $("#txtMobilePhone").val();
								element = "txtMobilePhone";
								break;
							case "PHONE_WORK":
								phoneChoice = $("#txtOfficePhone").val();
								element = "txtOfficePhone";
								break;
							case "PHONE_HOME":
								phoneChoice = $("#txtHomePhone").val();
								element = "txtHomePhone";
								break;
							default:
								break;
							}
							if (!phoneChoice) {
								if (element == "txtMobilePhone") {
									$("#" + element).notify(
											multiLang.notEnterAPhoneNumber, {
												position : "left bottom",
												className : "error"
											});
								} else {
									$("#" + element).notify(
											multiLang.notEnterAPhoneNumber, {
												position : "right bottom",
												className : "error"
											});
								}
								$("#txtShippingPhone").jqxDropDownList(
										"clearSelection");
							}
						}
					});

			Processor.initEventComboboxGeo("PROVINCE", "txtCountry2",
					"txtProvince2", "", "COUNTRY");
			Processor.initEventComboboxGeo("DISTRICT", "txtProvince2",
					"txtCounty2", "txtCountry2", "PROVINCE");
			Processor.initEventComboboxGeo("WARD", "txtCounty2", "txtWard2",
					"txtProvince2", "DISTRICT");
			Processor.initEventComboboxGeo("", "txtWard2", null, "txtCounty2",
					"WARD");
		};
		var initValidator = function() {
			$("#generalInformation").jqxValidator({
				position : "left",
				rules : [ {
					input : "#txtHomePhone",
					message : multiLang.PhoneNotValid,
					action : "blur",
					rule : function(input, value) {
						var val = input.val();
						if (isNaN(val)) {
							return false;
						}
						return true;
					}
				}, {
					input : "#txtMobilePhone",
					message : multiLang.PhoneNotValid,
					action : "blur",
					rule : function(input, value) {
						var val = input.val();
						if (isNaN(val)) {
							return false;
						}
						return true;
					}
				}, {
					input : "#txtOfficePhone",
					message : multiLang.PhoneNotValid,
					action : "blur",
					rule : function(input, value) {
						var val = input.val();
						if (isNaN(val)) {
							return false;
						}
						return true;
					}
				}, {
					input : "#txtEmail",
					message : multiLang.EmailNotValid,
					action : "blur",
					rule : "email"
				}, {
					input : "#txtShippingPhone",
					message : multiLang.fieldRequired,
					action : "change",
					rule : function(input, value) {
						if (input.val()) {
							return true;
						}
						return false;
					}
				} ]
			});
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				ContactAddressLayer.setValue(data.partyId);
				$("#txtEmail").val(data.emailAddress);
				$("#txtMobilePhone").val(data.phoneMobile);
				$("#txtOfficePhone").val(data.phoneWork);
				$("#txtHomePhone").val(data.phoneHome);
				$("#txtPrimaryPhone").val(data.primaryPhoneUsing);
				$("#txtShippingPhone").val(data.shippingPhoneUsing);

				var dataAddress = data.listPrimaryLocation[0];
				if (dataAddress) {
					if (dataAddress.countryGeoId) {
						txtCountryValue = dataAddress.countryGeoId;
						$("#txtCountry2").jqxComboBox("val",
								dataAddress.countryGeoId);
					}
					txtCountyValue = null;
					txtWardValue = null;
					if (dataAddress.provinceGeoId) {
						txtProvinceValue = dataAddress.provinceGeoId;
						$("#txtProvince2").jqxComboBox("val",
								dataAddress.provinceGeoId);
					}
					if (dataAddress.districtGeoId) {
						txtCountyValue = dataAddress.districtGeoId;
						$("#txtCounty2").jqxComboBox("val",
								dataAddress.districtGeoId);
					}
					if (dataAddress.wardGeoId) {
						txtWardValue = dataAddress.wardGeoId;
						$("#txtWard2")
								.jqxComboBox("val", dataAddress.wardGeoId);
					}
					if (dataAddress.address) {
						$("#tarAddress12").val(dataAddress.address);
					}
					$("#postalCtm").val(dataAddress.contactMechId);
				}
			}
		};
		var getValue = function() {
			var value = new Object();
			value.phoneHome = $("#txtHomePhone").val();
			value.officeHome = $("#txtOfficePhone").val();
			value.mobilePhone = $("#txtMobilePhone").val();
			value.primaryPhone = $("#txtPrimaryPhone").val();
			value.shippingPhone = $("#txtShippingPhone").val();
			value.email = $("#txtEmail").val();
			value.postalCode = 70000;
			return value;
		};
		var validate = function() {
			return $("#generalInformation").jqxValidator("validate")
					&& $("#addressDetails").jqxValidator("validate");
		};
		return {
			init : function() {
				if (organizationId == "MB") {
					txtProvinceValue = "VNM-HN2";
				} else if (organizationId == "MN") {
					txtProvinceValue = "VNM-HCM";
				}
				initJqxElements();
				handleEvents();
				initValidator();
			},
			setValue : setValue,
			getValue : getValue,
			validate : validate
		};
	})();
}
var ContactAddressLayer = (function() {
	var initJqxElements = function() {
		var sourceAddress = {
			datatype : "json",
			datafields : [ {
				name : "contactMechId",
				type : "string"
			}, {
				name : "address1",
				type : "string"
			}, {
				name : "countryGeoId",
				type : "string"
			}, {
				name : "stateProvinceGeoId",
				type : "string"
			}, {
				name : "districtGeoId",
				type : "string"
			}, {
				name : "wardGeoId",
				type : "string"
			}, {
				name : "countryGeo",
				type : "string"
			}, {
				name : "stateProvinceGeo",
				type : "string"
			}, {
				name : "districtGeo",
				type : "string"
			}, {
				name : "wardGeo",
				type : "string"
			}, {
				name : "note",
				type : "string"
			}, {
				name : "postalCode",
				type : "string"
			} ],
			id : "contactMechId",
			url : "jqxGetAddressFamily?partyId="
		};
		var dataAdapterAddress = new $.jqx.dataAdapter(sourceAddress);
		$("#jqxgridAddress").jqxGrid({
			width : "100%",
			localization : getLocalization(),
			source : dataAdapterAddress,
			columnsresize : true,
			pageable : true,
			autoheight : false,
			height : 150,
			showdefaultloadelement : false,
			autoshowloadelement : false,
			autorowheight : true,
			columns : [ {
				text : multiLang.DmsSequenceId,
				datafield : "",
				sortable : false,
				filterable : false,
				editable : false,
				pinned : true,
				groupable : false,
				draggable : false,
				resizable : false,
				width : 40,
				cellsrenderer : function(row, column, value) {
					return "<div style=margin:4px;>" + (row + 1) + "</div>";
				}
			}, {
				text : multiLang.DmsAddress1,
				datafield : "address1",
				minwidth : 140
			}, {
				text : multiLang.DmsWard,
				datafield : "wardGeo",
				width : 100
			}, {
				text : multiLang.DmsCounty,
				datafield : "districtGeo",
				width : 100
			}, {
				text : multiLang.DmsProvince,
				datafield : "stateProvinceGeo",
				width : 100
			}, {
				text : multiLang.DmsCountry,
				datafield : "countryGeo",
				width : 100
			}]
			/*
			, {
				text : multiLang.DmsNote,
				datafield : "note",
				width : 100
			}
			*/
		});
		$("#contextMenuAddress").jqxMenu({
			theme : "olbius",
			width : 180,
			autoOpenPopup : false,
			mode : "popup"
		});
		$("#jqxwindowAddressEditor").jqxWindow({
			theme : "olbius",
			width : 800,
			height: 270,
			resizable : false,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#cancelEditAddress"),
			modalOpacity : 0.7
		});
		Processor.initComboboxGeo("", "WARD", "txtWard");
		Processor.initComboboxGeo("", "DISTRICT", "txtCounty");
		Processor.initComboboxGeo("", "PROVINCE", "txtProvince");
		Processor.initComboboxGeo("", "COUNTRY", "txtCountry");
	};
	var countryGeoId, stateProvinceGeoId, districtGeoId, wardGeoId;
	var handleEvents = function() {
		$("#jqxgridAddress").on("contextmenu", function() {
			return false;
		});
		$("#jqxgridAddress").on(
				"rowclick",
				function(event) {
					if (event.args.rightclick) {
						$("#jqxgridAddress").jqxGrid("clearSelection");
						$("#jqxgridAddress").jqxGrid("selectrow",
								event.args.rowindex);
						var scrollTop = $(window).scrollTop();
						var scrollLeft = $(window).scrollLeft();
						$("#contextMenuAddress").jqxMenu(
								"open",
								parseInt(event.args.originalEvent.clientX) + 5
										+ scrollLeft,
								parseInt(event.args.originalEvent.clientY) + 5
										+ scrollTop);
						return false;
					}
				});
		$("#contextMenuAddress").on("itemclick", function(event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			switch (itemId) {
			case "editAddress":
				editAddress();
				break;
			case "deleteAddress":
				deleteAddress();
				break;
			case "setAsPrimary":
				setPrimary();
				break;
			default:
				break;
			}
		});
		$("#contextMenuAddress").on(
				"shown",
				function() {
					var rowIndex = $("#jqxgridAddress").jqxGrid(
							"getSelectedRowindex");
					var rowData = $("#jqxgridAddress").jqxGrid("getrowdata",
							rowIndex);
					if (rowData.note == multiLang.DmsPrimaryAddress) {
						$("#contextMenuAddress").jqxMenu("disable",
								"deleteAddress", true);
						$("#contextMenuAddress").jqxMenu("disable",
								"setAsPrimary", true);
					} else {
						$("#contextMenuAddress").jqxMenu("disable",
								"deleteAddress", false);
						$("#contextMenuAddress").jqxMenu("disable",
								"setAsPrimary", false);
					}
				});
		$("body").on("click", function() {
			$("#contextMenuAddress").jqxMenu("close");
		});
		$("#addAddress").on("click", function(e) {
			if ($("#otherAddress").hasClass("in")) {
				e.stopPropagation();
			}
			openPopupAddAddress();
		});
		$("#jqxwindowAddressEditor").on("close", function(event) {
			$("#jqxwindowAddressEditor").jqxValidator("hide");
			$("#jqxwindowAddressEditor").attr("rowId", "");
			setTimeout(function() {
				$("#txtCounty").jqxComboBox("clearSelection");
				$("#txtWard").jqxComboBox("clearSelection");
				$("#tarAddress1").val("");
				$("#txtPostalCode").val("");
			}, 1000);
		});
		Processor.initEventComboboxGeo("PROVINCE", "txtCountry", "txtProvince",
				"", "COUNTRY");
		Processor.initEventComboboxGeo("DISTRICT", "txtProvince", "txtCounty",
				"txtCountry", "PROVINCE");
		Processor.initEventComboboxGeo("WARD", "txtCounty", "txtWard",
				"txtProvince", "DISTRICT");
		Processor
				.initEventComboboxGeo("", "txtWard", null, "txtCounty", "WARD");

		$("#txtCountry").on("bindingComplete", function(event) {
			if (countryGeoId) {
				$("#txtCountry").jqxComboBox("val", countryGeoId);
				countryGeoId = "VNM";
			}
		});
		$("#txtProvince").on("bindingComplete", function(event) {
			if (stateProvinceGeoId) {
				$("#txtProvince").jqxComboBox("val", stateProvinceGeoId);
				if (!$("#txtProvince").jqxComboBox("getSelectedItem")) {
					$("#txtProvince").jqxComboBox("clearSelection");
					stateProvinceGeoId = null;
				}
			}
		});
		$("#txtCounty").on("bindingComplete", function(event) {
			if (districtGeoId) {
				$("#txtCounty").jqxComboBox("val", districtGeoId);
				if (!$("#txtCounty").jqxComboBox("getSelectedItem")) {
					$("#txtCounty").jqxComboBox("clearSelection");
					districtGeoId = null;
				}
			}
		});
		$("#txtWard").on("bindingComplete", function(event) {
			if (wardGeoId) {
				$("#txtWard").jqxComboBox("val", wardGeoId);
				if (!$("#txtWard").jqxComboBox("getSelectedItem")) {
					$("#txtWard").jqxComboBox("clearSelection");
					wardGeoId = null;
				}
			}
		});

		$("#saveEditAddress").click(
				function() {
					if ($("#jqxwindowAddressEditor").jqxValidator("validate")) {
						var rowId = $("#jqxwindowAddressEditor").attr("rowId");
						if (rowId) {
							$("#jqxwindowAddressEditor").attr("rowId", "");
							$("#jqxgridAddress").jqxGrid("updaterow", rowId,
									getValuePopupAddress());
						} else {
							$("#jqxgridAddress").jqxGrid("addrow", null,
									getValuePopupAddress());
						}
						$("#jqxwindowAddressEditor").jqxWindow("close");
					}
				});
		$("#savePrimaryAddress").on("click", function(event) {
			if ($("#txtProvince2").jqxComboBox("val")) {
				if (!$("#addressDetails").jqxValidator("validate")) {
					return;
				}
			}
			if (notTrigger) {
				notTrigger = false;
				event.stopPropagation();
			} else if ($("#otherAddress").hasClass("in")) {
				event.stopPropagation();
			}
			ContactAddressLayer.updateRow();
		});
	};
	var setPrimary = function() {
		var rows = $("#jqxgridAddress").jqxGrid("getrows");
		for ( var x in rows) {
			$("#jqxgridAddress").jqxGrid("setcellvaluebyid", rows[x].uid,
					"note", null);
		}
		var rowIndex = $("#jqxgridAddress").jqxGrid("getSelectedRowindex");
		var rowData = $("#jqxgridAddress").jqxGrid("getrowdata", rowIndex);
		$("#jqxgridAddress").jqxGrid("setcellvaluebyid", rowData.uid, "note",
				multiLang.DmsPrimaryAddress);

	};
	var openPopupAddAddress = function() {
		var wtmp = window;
		var tmpwidth = $("#jqxwindowAddressEditor").jqxWindow("width");
		$("#jqxwindowAddressEditor").jqxWindow({
			position : {
				x : (wtmp.outerWidth - tmpwidth) / 2,
				y : pageYOffset + 120
			}
		});
		$("#jqxwindowAddressEditor").jqxWindow("open");
	};
	var getValuePopupAddress = function() {
		var value = new Object();
		value.address1 = $("#tarAddress1").val();

		if ($("#txtWard").jqxComboBox("getSelectedItem")) {
			value.wardGeoId = $("#txtWard").jqxComboBox("getSelectedItem").value;
			value.wardGeo = $("#txtWard").jqxComboBox("getSelectedItem").label;
		}
		if ($("#txtCounty").jqxComboBox("getSelectedItem")) {
			value.districtGeoId = $("#txtCounty")
					.jqxComboBox("getSelectedItem").value;
			value.districtGeo = $("#txtCounty").jqxComboBox("getSelectedItem").label;
		}
		if ($("#txtProvince").jqxComboBox("getSelectedItem")) {
			value.stateProvinceGeoId = $("#txtProvince").jqxComboBox(
					"getSelectedItem").value;
			value.stateProvinceGeo = $("#txtProvince").jqxComboBox(
					"getSelectedItem").label;
		}
		if ($("#txtCountry").jqxComboBox("getSelectedItem")) {
			value.countryGeoId = $("#txtCountry")
					.jqxComboBox("getSelectedItem").value;
			value.countryGeo = $("#txtCountry").jqxComboBox("getSelectedItem").label;
		}
		return value;
	};
	var setValuePopupAddress = function(data) {
		if (_.isEmpty(data)) {
			return;
		}
		$("#jqxwindowAddressEditor").attr("rowId", data.uid);
		$("#tarAddress1").val(data.address1);
		$("#txtCountry").jqxComboBox("val", data.countryGeoId);
		if (!data.countryGeoId) {
			$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
		} else {
			stateProvinceGeoId = data.stateProvinceGeoId;
		}
		if (!stateProvinceGeoId) {
			$("#txtCounty").jqxComboBox("val", data.districtGeoId);
		} else {
			districtGeoId = data.districtGeoId;
		}
		if (!districtGeoId) {
			$("#txtWard").jqxComboBox("val", data.wardGeoId);
		} else {
			wardGeoId = data.wardGeoId;
		}
	};
	var editAddress = function() {
		var rowIndex = $("#jqxgridAddress").jqxGrid("getSelectedRowindex");
		var rowData = $("#jqxgridAddress").jqxGrid("getrowdata", rowIndex);
		setValuePopupAddress(rowData);
		openPopupAddAddress();
	};
	var deleteAddress = function() {
		bootbox.confirm(multiLang.ConfirmDeleteAddress, multiLang.CommonCancel,
				multiLang.CommonSubmit, function(result) {
					if (result) {
						var rowIndex = $("#jqxgridAddress").jqxGrid(
								"getSelectedRowindex");
						var rowData = $("#jqxgridAddress").jqxGrid(
								"getrowdata", rowIndex);
						$("#jqxgridAddress").jqxGrid("deleterow", rowData.uid);
					}
				});
	};
	var initValidator = function() {
		$("#jqxwindowAddressEditor").jqxValidator({
			rules : [ {
				input : "#tarAddress1",
				message : multiLang.fieldRequired,
				action : "change, blur",
				rule : "required"
			}, {
				input : "#txtProvince",
				message : multiLang.fieldRequired,
				action : "change",
				rule : function(input, value) {
					if (input.val()) {
						return true;
					}
					return false;
				}
			} ],
			scroll: false
		});
		$("#addressDetails").jqxValidator({
			rules : [ {
				input : "#tarAddress12",
				message : multiLang.fieldRequired,
				action : "keyup, blur",
				rule : "required"
			}, {
				input : "#txtProvince2",
				message : multiLang.fieldRequired,
				action : "change",
				rule : function(input, value) {
					if (input.val()) {
						return true;
					}
					return false;
				}
			} ],
			position : "left"
		});
	};
	var initComboboxGeo = function(geoId, geoTypeId, element) {
		var source = {
			datatype : "json",
			datafields : [ {
				name : "geoId"
			}, {
				name : "geoName"
			} ],
			url : "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId="
					+ geoId
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#" + element).jqxComboBox({
			source : dataAdapter,
			theme : "olbius",
			displayMember : "geoName",
			valueMember : "geoId",
			width : 180,
			height : 25
		});
	};
	var initEventComboboxGeo = function(geoTypeId, element, elementAffected,
			elementParents, thisGeoTypeId) {
		$("#" + element).on("change", function(event) {
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
	var updateRow = function() {

		if ($("#txtProvince2").jqxComboBox("val")) {
			if (!$("#addressDetails").jqxValidator("validate")) {
				return;
			}
		}
		var value = new Object();
		value.note = multiLang.DmsPrimaryAddress;
		value.address1 = $("#tarAddress12").val();
		if (!value.address1) {
			return;
		}
		value.note = multiLang.DmsPrimaryAddress;
		if ($("#txtWard2").jqxComboBox("getSelectedItem")) {
			value.wardGeoId = $("#txtWard2").jqxComboBox("getSelectedItem").value;
			value.wardGeo = $("#txtWard2").jqxComboBox("getSelectedItem").label;
		}
		if ($("#txtCounty2").jqxComboBox("getSelectedItem")) {
			value.districtGeoId = $("#txtCounty2").jqxComboBox(
					"getSelectedItem").value;
			value.districtGeo = $("#txtCounty2").jqxComboBox("getSelectedItem").label;
		}
		if ($("#txtProvince2").jqxComboBox("getSelectedItem")) {
			value.stateProvinceGeoId = $("#txtProvince2").jqxComboBox(
					"getSelectedItem").value;
			value.stateProvinceGeo = $("#txtProvince2").jqxComboBox(
					"getSelectedItem").label;
		}
		if ($("#txtCountry2").jqxComboBox("getSelectedItem")) {
			value.countryGeoId = $("#txtCountry2").jqxComboBox(
					"getSelectedItem").value;
			value.countryGeo = $("#txtCountry2").jqxComboBox("getSelectedItem").label;
		}
		if (!$("#postalCtm").val()) {
			$("#postalCtm").val(0);
		}
		var val = $("#jqxgridAddress").jqxGrid("updaterow",
				$("#postalCtm").val(), value);
		if (!val) {
			$("#jqxgridAddress").jqxGrid("addrow", null, value);
		}
	};
	var getValue = function() {
		var data = $("#jqxgridAddress").jqxGrid("getboundrows");
		return JSON.stringify(data);
	};
	var setValue = function(partyId) {
		var adapter = $("#jqxgridAddress").jqxGrid("source");
		if (adapter) {
			adapter.url = "jqxGetAddressFamily?partyId=" + partyId;
			adapter._source.url = "jqxGetAddressFamily?partyId=" + partyId;
			$("#jqxgridAddress").jqxGrid("source", adapter);
		}
	};
	return {
		init : function() {
			countryGeoId = "VNM";
			if (organizationId == "MB") {
				stateProvinceGeoId = "VNM-HN2";
			} else if (organizationId == "MN") {
				stateProvinceGeoId = "VNM-HCM";
			}
			initJqxElements();
			handleEvents();
			initValidator();
		},
		getValue : getValue,
		setValue : setValue,
		setValuePopupAddress : setValuePopupAddress,
		updateRow : updateRow
	};
})();