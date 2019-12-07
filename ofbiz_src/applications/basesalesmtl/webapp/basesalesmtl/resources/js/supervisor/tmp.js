
if (typeof (AddAgent) == "undefined") {
	var AddAgent = (function() {
		var getValue = function() {
			if (UpdateMode) {
				/*var value = {
						partyCode: $("#partyCode").val(),
						groupName: $("#groupName").val(),
						groupNameLocal: $("#groupName").val(),
						officeSiteName: $("#officeSiteName").val(),
						comments: $("#comments").val(),
						currencyUomId: $("#currencyUomId").jqxDropDownList("val"),
						taxAuthInfos: $("#taxAuthInfos").val(),
						contactNumber: $("#txtPhoneNumber").val(),
						infoString: $("#txtEmailAddress").val(),
						distributorId: Grid.getDropDownValue($("#divDistributor")).trim(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim()
				};*/
			} else {
				/*var value = {
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
						distributorId: Grid.getDropDownValue($("#divDistributor")).trim(),
						salesmanId: Grid.getDropDownValue($("#divSalesman")).trim(),
						//productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore")),
						routeId: Grid.getDropDownValue($("#divRoute")).trim()
				};*/
			}
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			value = _.extend(value, Representative.getValue(), AdditionalContact.getValue());
			return value;
		};
		var extendId = new Object();
		var setValue = function(data) {
			/*if (!_.isEmpty(data)) {
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
				Grid.setDropDownValue($("#divDistributor"), data.distributorId, data.distributor);
				Grid.setDropDownValue($("#divSalesman"), data.salesmanId, data.salesman);
			}*/
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
							location.href = "AgentDetail?partyId=" + res['partyId'];
						}
					}
				}, 2000);
			}
		};
		return {
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
