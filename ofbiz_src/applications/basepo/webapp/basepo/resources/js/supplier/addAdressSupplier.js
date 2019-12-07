$(function() {
	pageCommonShippingAdrNewPopup.init();
	$(".hideCTM").css("display", "none");
	$("#alterpopupWindowContactMechNew").jqxWindow({
		height : 370,
		width : 500
	});
	$("#headerAddress").text(addAddressForSupp);
});
var pageCommonShippingAdrNewPopup = (function() {
	var dataCTM = {};
	var county = "VNM-HN2-UH";
	var ward = "VNM-HN2-UH-VT";
	var stateProvinceGeoId = "VNM-HN2";
	var countryGeoId = "VNM";

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
					theme : "olbius",
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
		initCombobox($("#wn_countryGeoId"), countryGeoId, "getListCountryGeo");

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
						theme : "olbius",
						width : "99%",
						displayMember : "geoName",
						valueMember : "geoId",
						placeHolder : DAClickToChoose
					});
				}
			});
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
		$("#alterpopupWindowContactMechNew").on("open", function() {
			$("#wn_countryGeoId").jqxComboBox("focus");
		});
		$("#alterpopupWindowContactMechNew").on("close", function() {
			$("#emailAddress").jqxInput("focus");
		});

		$("#alterCancelCTM").on("click", function() {
			$("#wn_countryGeoId").jqxComboBox("selectItem", countryGeoId);
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
							$
									.ajax({
										url : url,
										type : "POST",
										data : {},
										dataType : "json",
										success : function(data) {
											var dataCombo = data.results;
											comboBoxObj
													.jqxComboBox({
														searchMode : "containsignorecase",
														source : dataCombo,
														theme : "olbius",
														width : "99%",
														displayMember : "geoName",
														valueMember : "geoId",
														placeHolder : DAClickToChoose
													});
											if (comboBoxObj.selector == "#wn_stateProvinceGeoId") {
												comboBoxObj.jqxComboBox(
														"selectItem",
														stateProvinceGeoId);
											} else if (comboBoxObj.selector == "#wn_countyGeoId") {
												comboBoxObj.jqxComboBox(
														"selectItem", county);
											} else if (comboBoxObj.selector == "#wn_wardGeoId") {
												comboBoxObj.jqxComboBox(
														"selectItem", ward);
											}
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
							else {
								setValue();
								var address1 = $("#wn_address1").val();

								var m_countryGeoIdText = $("#wn_countryGeoId")
										.jqxComboBox("getSelectedItem").label;
								var m_stateProvinceGeoIdText = $(
										"#wn_stateProvinceGeoId").jqxComboBox(
										"getSelectedItem").label;
								var m_countyGeoIdText = $("#wn_countyGeoId")
										.jqxComboBox("getSelectedItem").label;
								var m_wardGeoIdText = $("#wn_wardGeoId")
										.jqxComboBox("getSelectedItem").label;

								countryGeoId = $("#wn_countryGeoId")
										.jqxComboBox("getSelectedItem").value;
								stateProvinceGeoId = $("#wn_stateProvinceGeoId")
										.jqxComboBox("getSelectedItem").value;
								county = $("#wn_countyGeoId").jqxComboBox(
										"getSelectedItem").value;
								ward = $("#wn_wardGeoId").jqxComboBox(
										"getSelectedItem").value;
								$("#alterpopupWindowContactMechNew").jqxWindow(
										"close");
								$("#address").val(
										address1 + ", " + m_wardGeoIdText
												+ ", " + m_countyGeoIdText
												+ ", "
												+ m_stateProvinceGeoIdText
												+ ", " + m_countryGeoIdText);
							}
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
							}, ]
				});
	};
	var getValue = function() {
		return dataCTM;
	};

	var setValue = function() {
		var m_countryGeoId = $("#wn_countryGeoId").jqxComboBox(
				"getSelectedItem").value;
		var m_stateProvinceGeoId = $("#wn_stateProvinceGeoId").jqxComboBox(
				"getSelectedItem").value;
		var m_countyGeoId = $("#wn_countyGeoId").jqxComboBox("getSelectedItem").value;
		var m_wardGeoId = $("#wn_wardGeoId").jqxComboBox("getSelectedItem").value;
		var address1 = $("#wn_address1").val();
		var postalCode = $("#wn_postalCode").val();

		dataCTM.countryGeoId = m_countryGeoId;
		dataCTM.stateProvinceGeoId = m_stateProvinceGeoId;
		dataCTM.countyGeoId = m_countyGeoId;
		dataCTM.wardGeoId = m_wardGeoId;
		dataCTM.address1 = address1;
		dataCTM.postalCode = postalCode;
	}

	var setValueCountyWard = function(dataR) {
		county = dataR.districtGeoId;
		ward = dataR.wardGeoId;
		stateProvinceGeoId = dataR.stateProvinceGeoId;
		countryGeoId = dataR.countryGeoId;
	}
	var setAllValue = function() {
		county = "VNM-HN2-UH";
		ward = "VNM-HN2-UH-VT";
		stateProvinceGeoId = "VNM-HN2";
		countryGeoId = "VNM";
	}
	return {
		init : init,
		initWindow : initWindow,
		initOther : initOther,
		getValue : getValue,
		setValue : setValue,
		setValueCountyWard : setValueCountyWard,
		setAllValue : setAllValue
	};
}());
