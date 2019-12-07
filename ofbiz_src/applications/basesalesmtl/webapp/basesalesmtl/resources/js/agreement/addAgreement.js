var url = "createAgreementDistributor";
var UpdateMode = false;
$(document).ready(function() {
	Agreement.init();
	if (agreementIdParam) {
		UpdateMode = true;
		Agreement.letUpdate(agreementIdParam);
		url = "updateAgreementDistributor";
	} else {
		PartyA.setValue({partyIdTo: defaultDataMap.currencyOrganizationId});
	}
});
if (typeof (Agreement) == "undefined") {
	var Agreement = (function() {
		var validateDone = false;
		var initJqxElements = function() {
			$("#agreementCode").jqxInput({ theme: "olbius", width: '98%', height: 25 });
			$("#divAgreementDate").jqxDateTimeInput({ theme: "olbius", width: 200, height: 25 });
			$("#divFromDate").jqxDateTimeInput({ theme: "olbius", width: 200, height: 25 });
			$("#divThruDate").jqxDateTimeInput({ theme: "olbius", width: 200, height: 25, min: new Date((new Date().getTime() - 86400000)) });
			$("#divThruDate").jqxDateTimeInput("setDate", null);
			$("[data-rel=tooltip]").tooltip();
			$("#jqxNotificationNestedSlide").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (!validateDone) {
					if ($("#agreementInfo").jqxValidator("validate")) {
						Loading.show();
						validateDone = true;
						$("#btnSave").attr("disabled", true);
						var data = _.extend(Agreement.getValue(), PartyA.getValue(), PartyB.getValue());
						DataAccess.execute({
						url: url,
						data: data}, Agreement.notify);
					}
				}
			});
			$("#divFromDate").on("valueChanged", function (event) {
				var jsDate = event.args.date;
				$("#divThruDate ").jqxDateTimeInput("setMinDate", jsDate);
			});
		};
		var initValidator = function () {
			$("#agreementInfo").jqxValidator({
			    rules: [{ input: "#agreementCode", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
			            { input: "#agreementCode", message: multiLang.BSAgreementCodeAlreadyExists, action: "change",
							rule: function (input, commit) {
								var agreementCode = input.val();
								if (agreementCode) {
									var check = DataAccess.getData({
										url: "checkAgreementCode",
										data: {agreementCode: agreementCode, agreementId: agreementIdParam},
										source: "check"});
									if ("false" == check) {
										 return false;
									}
								}
								return true;
							}
						},
						{ input: '#agreementCode', message: multiLang.containSpecialSymbol, action: 'keyup, blur',
							rule: function (input, commit) {
								var value = input.val();
								if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divPartyFrom", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input).toString();
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divPartyTo", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input).toString();
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divFindRepresentTo", message: multiLang.fieldRequired, action: "close", 
							rule: function (input, commit) {
								var value = Grid.getDropDownValue(input).toString();
								if (value.trim()) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divFromDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divThruDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						},
						{ input: "#divAgreementDate", message: multiLang.fieldRequired, action: "change",
							rule: function (input, commit) {
								var value = input.val();
								if (value) {
									return true;
								}
								return false;
							}
						}]
			});
		};
		var getValue = function() {
			var value = new Object();
			value.agreementCode = $("#agreementCode").val();
			value.description = $("#tarDescription").val();
			value.agreementDate = $("#divAgreementDate").jqxDateTimeInput("getDate").getTime();
			value.fromDate = $("#divFromDate").jqxDateTimeInput("getDate").getTime();
			value.thruDate = $("#divThruDate").jqxDateTimeInput("getDate").getTime();
			value = _.extend(value, extendId);
			return value;
		};
		var extendId = new Object();
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#agreementCode").val(data.agreementCode);
				$("#tarDescription").val(data.description);
				if (data.agreementDate) {
					$("#divAgreementDate").jqxDateTimeInput("setDate", new Date(data.agreementDate.time));
				}
				if (data.fromDate) {
					$("#divFromDate").jqxDateTimeInput("setDate", new Date(data.fromDate.time));
				}
				if (data.thruDate) {
					$("#divThruDate").jqxDateTimeInput("setDate", new Date(data.thruDate.time));
				}
				extendId.agreementId = data.agreementId;
			}
		};
		var notify = function(res) {
			Loading.hide();
			$(window).scrollTop(0);
			$("#jqxNotificationNestedSlide").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				validateDone = false;
				$("#btnSave").attr("disabled", false);
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "error"});
				$("#notificationContentNestedSlide").text(multiLang.updateError);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "info"});
				if (UpdateMode) {
					$("#notificationContentNestedSlide").text(multiLang.updateSuccess);
				} else {
					$("#notificationContentNestedSlide").text(multiLang.addSuccess);
				}
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				if (res['agreementId']) {
					setTimeout(function() {
						 switch ($("#divParnerType").jqxDropDownList("val")) {
							case "Agents":
								location.href = "AgreementDetail?sub=AgreementWithAgent&agreementId=" + res['agreementId'];
								break;
							case "Distributor":
								location.href = "AgreementDetail?sub=AgreementWithDistributor&agreementId=" + res['agreementId'];
								break;
							default:
								break;
							}
					}, 2000);
				}
			}
		};
		var letUpdate = function(agreementId) {
			var data = DataAccess.getData({
				url: "loadAgreementInfo",
				data: {agreementId: agreementId},
				source: "agreementInfo"});
			Agreement.setValue(data);
			PartyA.setValue(data);
			PartyB.setValue(data);
			PartyA.disableElements();
			PartyB.disableElements();
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				initValidator();
				PartyA.init();
				PartyB.init();
			},
			getValue: getValue,
			setValue: setValue,
			letUpdate: letUpdate,
			notify: notify
		};
	})();
}
if (typeof (PartyA) == "undefined") {
	var PartyA = (function() {
		var initJqxElements = function() {
			var initPartyToDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "groupName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "partyCode", width: 200},
				               {text: multiLang.DmsGroupName, datafield: "groupName"}];
				GridUtils.initDropDownButton({url: "JQListOrganizationPartyS", filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridPartyTo").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}, clearOnClose: 'Y'
				}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var str = row.groupName + " [" + row.partyId + "]";
					return str;
				});
			};
			initPartyToDrDGrid($("#divPartyTo"),$("#jqxgridPartyTo"), 600);
			
			var initRepresentToDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "firstName", type: "string" },
				                  { name: "middleName", type: "string" },
				                  { name: "lastName", type: "string" }];
				var columns = [{text: multiLang.DmsPartyId, datafield: "partyCode", width: 150},
				               {text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150},
				               {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150},
				               {text: multiLang.DmsPartyFirstName, datafield: "firstName"}];
				GridUtils.initDropDownButton({url: "JQGetListSalesRepresentative", filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "partyId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridFindRepresentTo").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}
				, dropdown: {dropDownHorizontalAlignment: "left"}, clearOnClose: 'Y'}, datafields, columns, null, grid, dropdown, "partyId", function(row){
					var first = row.firstName ? row.firstName : "";
					var mid = row.middleName ? row.middleName : "";
					var last = row.lastName ? row.lastName : "";
					var str = last + " " + mid + " " + first + " [" + row.partyCode + "]";
					return str;
				});
			};
			initRepresentToDrDGrid($("#divFindRepresentTo"),$("#jqxgridFindRepresentTo"), 600);
		};
		var handleEvents = function() {
			$("#divPartyTo").on("close", function (event) {
				var partyId = Grid.getDropDownValue($("#divPartyTo")).toString();
				if (partyId.trim()) {
					var data = DataAccess.getData({
							url: "getPartyInformationAjax",
							data: {partyId: partyId},
							source: "partyInfo"});
					PartyA.setDetailInfo(data);
				}
			});
			$("#divFindRepresentTo").on("close", function (event) {
				var partyId = Grid.getDropDownValue($("#divFindRepresentTo")).toString();
				if (partyId.trim()) {
					$("#divARepresentedBy").text(LocalUtility.getPartyName(partyId));
				}
			});
		};
		var setDetailInfo = function(data) {
			if (!_.isEmpty(data)) {
				if (data.listAddress) {
					var listAddress = data.listAddress;
					for ( var x in listAddress) {
						if (listAddress[x]["contactMechPurposeType"] == "PRIMARY_LOCATION") {
							$("#divAAddress").text(listAddress[x].address1);
						}
					}
				}
				$("#divATelecom").text(data.contactNumber);
				$("#divAFax").text(data.faxNumber);
				$("#divATaxCode").text(data.taxAuthInfos);
			}
		};
		var getValue = function() {
			var value = new Object();
			value.partyIdTo = Grid.getDropDownValue($("#divPartyTo"));
			value.representativeId = Grid.getDropDownValue($("#divFindRepresentTo"));
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				Grid.setDropDownValue($("#divPartyTo"), data.partyIdTo, LocalUtility.getPartyName(data.partyIdTo));
				Grid.setDropDownValue($("#divFindRepresentTo"), data.representativeId, LocalUtility.getPartyName(data.representativeId));
				$("#divPartyTo").trigger("close");
				$("#divFindRepresentTo").trigger("close");
			}
		};
		var disableElements = function () {
			$("#divPartyTo").jqxDropDownButton({ disabled: true });
			$("#divFindRepresentTo").jqxDropDownButton({ disabled: true });
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			disableElements: disableElements,
			setDetailInfo: setDetailInfo
		}
	})();
}
if (typeof (PartyB) == "undefined") {
	var PartyB = (function() {
		var initJqxElements = function() {
			$("#divParnerType").jqxDropDownList({ source: parnerTypes, theme: "olbius", displayMember: "value", valueMember: "key",
				selectedIndex: 0, autoDropDownHeight: true, placeHolder: multiLang.filterchoosestring});
			
			var initPartyFromDrDGrid = function(dropdown, grid, width){
				var datafields = [{ name: "partyId", type: "string" },
				                  { name: "partyCode", type: "string" },
				                  { name: "fullName", type: "string" },
				                  { name: "groupName", type: "string" },
				                  { name: "partyTypeId", type: "string" }];
		        var columns = [{text: multiLang.DmsPartnerId, datafield: "partyCode", width: 200},
		                       {text: multiLang.DmsPartnerName, datafield: "fullName"}];
		    	GridUtils.initDropDownButton({url: "JQGetListAgents&sD=N", filterable: true, showfilterrow: true,
		    		width: width ? width : 600, source: {id: "partyId", pagesize: 5},
		    			handlekeyboardnavigation: function (event) {
			                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			                if (key == 70 && event.ctrlKey) {
			                	$("#jqxgridPartyFrom").jqxGrid("clearfilters");
			                	return true;
			                }
					 	}, dropdown: {dropDownHorizontalAlignment: "left"}, clearOnClose: 'Y'}, datafields, columns, null, grid, dropdown, "partyId", function(row){
							var str = "";
							if (row) str += row.groupName + " [" + row.partyCode + "]";
							return str;
						});
			};
			initPartyFromDrDGrid($("#divPartyFrom"),$("#jqxgridPartyFrom"), 600);
		};
		var handleEvents = function() {
			$("#divParnerType").on("change", function (event){
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    cleanPartyFromInfo();
				    Grid.cleanDropDownValue($("#divPartyFrom"));
				    switch (value) {
					case "Agents":
						reloadPartyFromDrDGrid("jqxGeneralServicer?sname=JQGetListAgents&sD=N");
						$("#distributor").slideUp(1);
						$("#agent").slideDown(100);
						break;
					case "Distributor":
						reloadPartyFromDrDGrid("jqxGeneralServicer?sname=JQGetListDistributor&sD=N");
						$("#agent").slideUp(1);
						$("#distributor").slideDown(100);
						break;
					default:
						break;
					}
				}
			});
			$("#divPartyFrom").on("close", function (event) {
				var partyId = Grid.getDropDownValue($("#divPartyFrom")).toString();
				if (partyId.trim()) {
					switch ($("#divParnerType").jqxDropDownList("val")) {
					case "Agents":
						var data = DataAccess.getData({
							url: "loadAgentInfo",
							data: {partyId: partyId, detail: "Y"},
							source: "agentInfo"});
						PartyB.setDetailInfo(data);
						break;
					case "Distributor":
						var data = DataAccess.getData({
							url: "loadDistributorInfo",
							data: {partyId: partyId, detail: "Y"},
							source: "distributorInfo"});
						PartyB.setDetailInfo(data);
						break;
					default:
						break;
					}
				}
			});
			/*$("#divPartyFrom").on("open", function (event) {
				$('#jqxgridPartyFrom').jqxGrid('updatebounddata');
			});*/
		};
		var getValue = function() {
			var value = new Object();
			value.partyIdFrom = Grid.getDropDownValue($("#divPartyFrom"));
			return value;
		};
		var setDetailInfo = function(data) {
			if (!_.isEmpty(data)) {
				$("#divBAddress").text(data.address);
				$("#divBTelecom").text(data.contactNumber);
				$("#divBFax").text(data.faxNumber);
				$("#divBTaxCode").text(data.taxAuthInfos);
				if (data.representative) {
					$("#divBRepresentedBy").text(data.representative.partyFullName);
				}
			}
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				switch (data.partyTypeFrom) {
				case "RETAIL_OUTLET":
					$("#divParnerType").jqxDropDownList("val", "Agents");
					break;
				case "PARTY_GROUP":
					$("#divParnerType").jqxDropDownList("val", "Distributor");
					break;
				default:
					break;
				}
				Grid.setDropDownValue($("#divPartyFrom"), data.partyIdFrom, LocalUtility.getPartyName(data.partyIdFrom));
				$("#divPartyFrom").trigger("close");
			}
		};
		var disableElements = function () {
			$("#divPartyFrom").jqxDropDownButton({ disabled: true });
			$("#divParnerType").jqxDropDownList({ disabled: true });
		};
		var reloadPartyFromDrDGrid = function (newUrl) {
			$("#jqxgridPartyFrom").jqxGrid("clear");
			var currentSource = $("#jqxgridPartyFrom").jqxGrid("source");
			if (currentSource) {
				currentSource.url = newUrl;
				currentSource._source.url = newUrl;
                $("#jqxgridPartyFrom").jqxGrid("clearfilters");
			}
		};
		var cleanPartyFromInfo = function () {
			$("#divBAddress").text("");
			$("#divBTelecom").text("");
			$("#divBFax").text("");
			$("#divBTaxCode").text("");
			$("#divBRepresentedBy").text("");
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue,
			disableElements: disableElements,
			setDetailInfo: setDetailInfo
		}
	})();
}
if (typeof (LocalUtility) == "undefined") {
	var LocalUtility = (function () {
		var getPartyName = function (partyId) {
			if (partyId) {
				return DataAccess.getData({
					url: "getPartyName",
					data: {partyId: partyId},
					source: "partyName"});
			}
		};
		return {
			getPartyName: getPartyName
		};
	})();
}
