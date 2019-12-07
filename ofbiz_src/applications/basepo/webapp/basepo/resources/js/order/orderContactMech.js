$(function() {
	// pageCommonShippingAdrNewPopup.initWindow();
	pageCommonShippingAdrNewPopup.init();
});
var pageCommonShippingAdrNewPopup = (function() {
	var init = function() {
		initWindow();
		initOther();
	};
	var initOther = function() {
		initElement();
		initElementComplex();
		initEvent();
		initValidateForm();
	};
	var initWindow = function() {
		$("#alterpopupWindowContactMechNew").jqxWindow({
			width : 550,
			height : 450,
			resizable : true,
			isModal : true,
			autoOpen : false,
			cancelButton : $("#alterCancelCTM"),
			modalOpacity : 0.7,
			theme : theme
		});
	};
	var initElement = function() {
		$("#wn_toName").jqxInput({
			height : 25,
			theme : theme,
			maxLength : 100
		});
		$("#wn_attnName").jqxInput({
			height : 25,
			theme : theme,
			maxLength : 100
		});
		$("#wn_address1").jqxInput({
			height : 25,
			theme : theme,
			maxLength : 255
		});
		$("#wn_postalCode").jqxInput({
			height : 25,
			theme : theme,
			maxLength : 60
		});

		var tmpWidth = "100%";
		$("#containerContactMech").width(tmpWidth);
		$("#jqxNotificationContactMech").jqxNotification({
			icon : {
				width : 25,
				height : 25,
				url : "/aceadmin/assets/images/info.jpg"
			},
			width : tmpWidth,
			appendContainer : "#containerContactMech",
			opacity : 1,
			autoClose : true,
			template : "success"
		});
	};

	var initCombobox = function(id, idChoose, url) {
		$.ajax({
			url : url,
			type : "POST",
			data : {},
			dataType : "json",
			success : function(data) {
				var dataCombo = data.records;
				id.jqxComboBox({
					searchMode : "containsignorecase",
					autoComplete : false,
					source : dataCombo,
					theme : theme,
					width : "99%",
					displayMember : "geoName",
					valueMember : "geoId",
					placeHolder : DAClickToChoose
				});
				if (idChoose != null)
					id.jqxComboBox("selectItem", idChoose);
			}
		});
	}

	var initElementComplex = function() {
		// khoi tao combobox country
		initCombobox($("#wn_countryGeoId"), "VNM", "getListCountryGeo");

		var initDropDownGeoState = function(id, sname) {
			var url = "jqxGeneralServicer?sname=" + sname;
			$.ajax({
				url : url,
				type : "POST",
				data : {},
				dataType : "json",
				success : function(data) {
					var dataCombo = data.results;
					id.jqxComboBox({
						searchMode : "containsignorecase",
						autoComplete : false,
						source : dataCombo,
						theme : theme,
						width : "99%",
						displayMember : "geoName",
						valueMember : "geoId",
						placeHolder : DAClickToChoose
					});
				}
			});
			// new OlbComboBox(elementObj, null, configGeo, []);
		};
		initDropDownGeoState($("#wn_stateProvinceGeoId"),
				"JQGetAssociatedStateListGeo&geoId=VNM&pagesize=0&pagenum=1");
		initDropDownGeoState($("#wn_countyGeoId"),
				"JQGetAssociatedStateOtherListGeo");
		initDropDownGeoState($("#wn_wardGeoId"),
				"JQGetAssociatedStateOtherListGeo");

		var localYN = [ {
			id : "Y",
			description : "uiLabelMap.DAYes"
		}, {
			id : "N",
			description : "uiLabelMap.DANo"
		} ];
		var configYN = {
			width : "99%",
			placeHolder : DAClickToChoose,
			key : "id",
			value : "description",
			autoDropDownHeight : true,
			selectedIndex : 0,
			displayDetail : false,
			dropDownHorizontalAlignment : "right",
		};
	};
	var initEvent = function() {
		$("#wn_countryGeoId").on("change", function(event) {
			getAssociatedState($("#wn_stateProvinceGeoId"), event);
		});
		$("#wn_stateProvinceGeoId").on("change", function(event) {
			getAssociatedState($("#wn_countyGeoId"), event);
		});
		$("#wn_countyGeoId").on("change", function(event) {
			getAssociatedState($("#wn_wardGeoId"), event);
		});

		$("#addNewShippingAddress").on("click", function() {
			$("#alterpopupWindowContactMechNew").jqxWindow("open");
		});

		var getAssociatedState = function(comboBoxObj, event) {
			var args = event.args;
			if (args) {
				var item = args.item;
				if (item) {
					var geoId = item.value;
					if (geoId) {
						var tmpSource = $(comboBoxObj).jqxComboBox("source");
						if (typeof (tmpSource) != "undefined") {
							url = "jqxGeneralServicer?sname=JQGetAssociatedStateOtherListGeo&geoId="
									+ geoId + "&pagesize=0&pagenum=1";
							// $(comboBoxObj).jqxComboBox("clearSelection");
							// $(comboBoxObj).jqxComboBox("source", tmpSource);
							$.ajax({
								url : url,
								type : "POST",
								data : {},
								dataType : "json",
								success : function(data) {
									var dataCombo = data.results;
									comboBoxObj.jqxComboBox({
										searchMode : "containsignorecase",
										source : dataCombo,
										theme : theme,
										width : "99%",
										displayMember : "geoName",
										valueMember : "geoId",
										placeHolder : DAClickToChoose
									});
									comboBoxObj.jqxComboBox("selectIndex", 0);
								}
							});

						}
					}
				}
			}
		};

		$("#alterSaveCTM")
				.on(
						"click",
						function() {
							if (!$("#alterpopupWindowContactMechNew")
									.jqxValidator("validate"))
								return false;

							bootbox
									.dialog(
											""
													+ "uiLabelMapDAAreYouSureYouWantCreate"
													+ "?",
											[
													{
														"label" : "wgcancel",
														"icon" : "fa fa-remove",
														"class" : "btn  btn-danger form-action-button pull-right",
														"callback" : function() {
															bootbox.hideAll();
														}
													},
													{
														"label" : "wgok",
														"icon" : "fa-check",
														"class" : "btn btn-primary form-action-button pull-right",
														"callback" : function() {
															var m_countryGeoId = $(
																	"#wn_countryGeoId")
																	.jqxComboBox(
																			"getSelectedItem").value;
															var m_stateProvinceGeoId = $(
																	"#wn_stateProvinceGeoId")
																	.jqxComboBox(
																			"getSelectedItem").value;
															var m_countyGeoId = $(
																	"#wn_countyGeoId")
																	.jqxComboBox(
																			"getSelectedItem").value;
															var m_wardGeoId = $(
																	"#wn_wardGeoId")
																	.jqxComboBox(
																			"getSelectedItem").value;
															var itemSupp = $(
																	"#supplierId")
																	.jqxDropDownList(
																			"getSelectedItem");
															var data = {
																partyId : itemSupp.value,
																contactMechTypeId : typeof ($("#wn_contactMechTypeId")
																		.val()) != "undefined" ? $(
																		"#wn_contactMechTypeId")
																		.val()
																		: null,
																contactMechPurposeTypeId : typeof ($("#wn_contactMechPurposeTypeId")
																		.val()) != "undefined" ? $(
																		"#wn_contactMechPurposeTypeId")
																		.val()
																		: null,
																toName : typeof ($("#wn_toName")
																		.val()) != "undefined" ? $(
																		"#wn_toName")
																		.val()
																		: null,
																attnName : typeof ($("#wn_attnName")
																		.val()) != "undefined" ? $(
																		"#wn_attnName")
																		.val()
																		: null,
																countryGeoId : typeof (m_countryGeoId) != "undefined" ? m_countryGeoId
																		: null,
																stateProvinceGeoId : typeof (m_stateProvinceGeoId) != "undefined" ? m_stateProvinceGeoId
																		: null,
																countyGeoId : typeof (m_countyGeoId) != "undefined" ? m_countyGeoId
																		: null,
																wardGeoId : typeof (m_wardGeoId) != "undefined" ? m_wardGeoId
																		: null,
																address1 : typeof ($("#wn_address1")
																		.val()) != "undefined" ? $(
																		"#wn_address1")
																		.val()
																		: null,
																postalCode : typeof ($("#wn_postalCode")
																		.val()) != "undefined" ? $(
																		"#wn_postalCode")
																		.val()
																		: null,
															};
															$
																	.ajax({
																		type : "POST",
																		url : "createPostalAddressShippingForParty",
																		data : data,
																		beforeSend : function() {
																			$(
																					"#loader_page_common_load")
																					.show();
																		},
																		success : function(
																				data) {
																			var itemFacility = $(
																					"#originFacilityId")
																					.jqxDropDownList(
																							"getSelectedItem");
																			loadButtonGrid(itemFacility.value);
																			// var
																			// resultValue
																			// =
																			// processResultCreateAddress(data);
																			// if
																			// (resultValue
																			// &&
																			// typeof(data.contactMechId)
																			// !=
																			// "undefined")
																			// {
																			// $("body").trigger("createContactmechComplete",
																			// [data.contactMechId]);
																			// }
																		},
																		error : function() {
																			alert("Send to server is false!");
																		},
																		complete : function() {
																			$(
																					"#loader_page_common_load")
																					.hide();
																			$(
																					"#alterpopupWindowContactMechNew")
																					.jqxWindow(
																							"close");
																		}
																	});
														}
													} ]);
						});

		var processResultCreateAddress = function(data) {
			if (data.thisRequestUri == "json") {
				var errorMessage = "";
				if (data._ERROR_MESSAGE_LIST_ != null) {
					for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						errorMessage += "<p><b>uiLabelMapDAErrorUper</b>: "
								+ data._ERROR_MESSAGE_LIST_[i] + "</p>";
					}
				}
				if (data._ERROR_MESSAGE_ != null) {
					errorMessage += "<p><b>uiLabelMapDAErrorUper</b>: "
							+ data._ERROR_MESSAGE_ + "</p>";
				}
				if (errorMessage != "") {
					$("#containerContactMech").empty();
					$("#jqxNotificationContactMech").jqxNotification({
						template : "error"
					});
					$("#jqxNotificationContactMech").html(errorMessage);
					$("#jqxNotificationContactMech").jqxNotification("open");
					return false;
				} else {
					$("#container").empty();
					$("#jqxNotification").jqxNotification({
						template : "info"
					});
					$("#jqxNotification").html("uiLabelMap.wgupdatesuccess");
					$("#jqxNotification").jqxNotification("open");
					return true;
				}
			} else {
				return true;
			}
		};
	};
	var initValidateForm = function() {
		$("#alterpopupWindowContactMechNew").jqxValidator(
				{
					position : "bottom",
					rules : [
							{
								input : "#wn_countryGeoId",
								message : ValueIsNotEmpty,
								action : "change,valueChanged, blur",
								rule : function(input, commit) {
									var item = $("#wn_countryGeoId")
											.jqxComboBox("getSelectedItem")
									if (item != null) {
										return true;
									} else
										return false;
									return true;
								}
							},
							{
								input : "#wn_stateProvinceGeoId",
								message : ValueIsNotEmpty,
								action : "change,valueChanged, blur",
								rule : function(input, commit) {
									var item = $("#wn_stateProvinceGeoId")
											.jqxComboBox("getSelectedItem")
									if (item != null) {
										return true;
									} else
										return false;
									return true;
								}
							},
							{
								input : "#wn_countyGeoId",
								message : ValueIsNotEmpty,
								action : "change,valueChanged, blur",
								rule : function(input, commit) {
									var item = $("#wn_countyGeoId")
											.jqxComboBox("getSelectedItem")
									if (item != null) {
										return true;
									} else
										return false;
									return true;
								}
							},
							{
								input : "#wn_wardGeoId",
								message : ValueIsNotEmpty,
								action : "change,valueChanged, blur",
								rule : function(input, commit) {
									var item = $("#wn_wardGeoId").jqxComboBox(
											"getSelectedItem")
									if (item != null) {
										return true;
									} else
										return false;
									return true;
								}
							},
							{
								input : "#wn_address1",
								message : ValueIsNotEmpty,
								action : "blur,valueChanged",
								rule : function(input, commit) {
									var wn_countryGeoId = $("#wn_address1")
											.val();
									if (wn_countryGeoId == ""
											|| wn_countryGeoId == null) {
										return false;
									} else {
										return true;
									}
									return true;
								}
							} ],
					scroll : false
				});
	};
	return {
		init : init,
		initWindow : initWindow,
		initOther : initOther
	};
}());
