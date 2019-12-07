Loading.show("loadingMacro");
var currentParty = {
	partyId : ""
};
watch(currentParty, "partyId", function() {
	if (currentParty.partyId) {
		ExtendScreen.showExtendInfo();
	} else {
		ExtendScreen.hideExtendInfo();
	}
});
$(document).ready(function() {
	Processor.init();
	RepresentativeMember.init();
	Bussinesses.init();
	School.init();
	ContactLayer.init();
	CreateContact.init();
	ExtendScreen.init();
	Consideration.init();
	Loading.hide("loadingMacro");
	ContactAddressLayer.init();
	var obj = CookieLayer.getCurrentParty();
	currentParty.partyId = obj.partyId;
	currentParty.partyRole = obj.partyRole;
	setTimeout(function() {
		Processor.renderUser();
		$(window).resize();
	}, 500);

});
var width = "95%";
if (typeof (Processor) == "undefined") {
	var Processor = (function() {
		var extendId = new Object();
		var initJqxElements = function() {
			if (partyIdParameter) {
				CookieLayer.setCurrentParty(partyIdParameter);
				Processor.renderUser();
			}
		};
		var handleEvents = function() {

		};
		var _data = function() {
			var party = CookieLayer.getCurrentParty();
			if (!_.isEmpty(party.partyId)) {
				var data = DataAccess.getData({
					url : "getUserInfoEditable",
					data : {
						partyId : party.partyId
					},
					source : "results"
				});
				if (typeof (OwnerContact) != "undefined") {
					OwnerContact.setValue(data);
				}
				if (!_.isEmpty(data)) {
					globalPartyTypeId = data.partyTypeId;
					if (data.listFamilyId) {
						globalFamilyId = data.listFamilyId;
						if (globalFamilyId.length == 1) {
							globalFamilyId = globalFamilyId[0];
						}
					} else {
						globalFamilyId = "";
					}
				} else {
					CreateContact.clearAllForm();
					RepresentativeMember.showlayer();
					globalpartytypeid = "person";
					createmode = true;
				}
				return data;
			} else {
				CreateContact.clearAllForm();
				RepresentativeMember.showLayer();
				globalPartyTypeId = "PERSON";
				CreateMode = true;
			}
		};
		var renderUser = function() {
			var data = _data();
			if (!_.isEmpty(data)) {
				ContactLayer.setValue(data);
				Liability.getData(data.partyId);
				switch (globalPartyTypeId) {
				case "PERSON":
					RepresentativeMember.showLayer();
					RepresentativeMember.setValue(data);
					break;
				case "BUSINESSES":
					Bussinesses.showLayer();
					Bussinesses.setValue(data);
					break;
				case "SCHOOL":
					School.showLayer();
					School.setValue(data);
					break;
				default:
					break;
				}
				ExtendScreen.refresh();
			}
		};
		var initComboboxGeo = function(geoId, geoTypeId, element) {
			var url = "";
			if (geoTypeId != "COUNTRY" && geoId) {
				url = "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId="
						+ geoId;
			} else if (geoTypeId == "COUNTRY") {
				url = "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
			}
			var source = {
				datatype : "json",
				datafields : [ {
					name : "geoId"
				}, {
					name : "geoName"
				} ],
				url : url,
				cache : true
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#" + element).jqxComboBox({
				source : dataAdapter,
				theme : "olbius",
				displayMember : "geoName",
				valueMember : "geoId",
				width : width,
				height : 25
			});
		};
		var initEventComboboxGeo = function(geoTypeId, element,
				elementAffected, elementParents, thisGeoTypeId) {
			$("#" + element).on("change", function(event) {
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
		var saveCustomer = function(ForceSave) {
			$("#savePrimaryAddress").click();
			if (ContactLayer.validate()) {
				if (!ForceSave) {
					var checkphoneHome = checkDuplicateNumberPhone(ContactLayer
							.getValue().phoneHome);
					var checkmobilePhone = checkDuplicateNumberPhone(ContactLayer
							.getValue().mobilePhone);
					var checkofficeHome = checkDuplicateNumberPhone(ContactLayer
							.getValue().officeHome);
					var dataDuplicate = new Array();
					var phoneDuplicate = "";
					var party = CookieLayer.getCurrentParty();
					if (checkphoneHome.partyId
							&& checkphoneHome.partyId != party.partyId) {
						dataDuplicate.push(checkphoneHome.partyInfo);
						phoneDuplicate += ContactLayer.getValue().phoneHome
								+ ", ";
					}
					if (checkmobilePhone.partyId
							&& checkmobilePhone.partyId != party.partyId) {
						dataDuplicate.push(checkmobilePhone.partyInfo);
						phoneDuplicate += ContactLayer.getValue().mobilePhone
								+ ", ";
					}
					if (checkofficeHome.partyId
							&& checkofficeHome.partyId != party.partyId) {
						dataDuplicate.push(checkofficeHome.partyInfo);
						phoneDuplicate += ContactLayer.getValue().officeHome
								+ ", ";
					}
					if (!_.isEmpty(dataDuplicate)) {
						dataDuplicate = _.uniq(dataDuplicate);
						ContactDuplicate.renderGrid(
								$("#jqxGridContactInformation"), dataDuplicate);
						phoneDuplicate = phoneDuplicate.substring(0,
								phoneDuplicate.length - 2);
						ContactDuplicate.setPhoneDuplicate(phoneDuplicate);
						ContactDuplicate.open();
						return;
					}
				}
				notTrigger = true;
				ContactAddressLayer.updateRow();
				var data = new Object();
				data.contactAddress = ContactAddressLayer.getValue();
				data = _.extend(data, ContactLayer.getValue(), OwnerContact
						.getValue());
				switch (globalPartyTypeId) {
				case "PERSON":
					if (RepresentativeMember.validate()) {
						data.children = Family.getValue();
						data = _.extend(data, RepresentativeMember.getValue());
						if (CreateMode) {
							DataAccess.execute({
								url : "createContactFamilyAjax",
								data : data
							}, Notify.createCustomer);
						} else {
							if (_.isEmpty(data.familyId)) {
								DataAccess.execute({
									url : "updateContactPersonalAjax",
									data : data
								}, Notify.createCustomer);
							} else {
								DataAccess.execute({
									url : "updateContactFamilyAjax",
									data : data
								}, Notify.createCustomer);
							}
						}
					}
					break;
				case "BUSINESSES":
					if (Bussinesses.validate()) {
						data = _.extend(data, Bussinesses.getValue());
						if (CreateMode) {
							DataAccess.execute({
								url : "createContactBusinessAjax",
								data : data
							}, Notify.createCustomer);
						} else {
							DataAccess.execute({
								url : "updateContactBusinessAjax",
								data : data
							}, Notify.createCustomer);
						}
					}
					break;
				case "SCHOOL":
					if (Bussinesses.validate()) {
						data = _.extend(data, School.getValue());
						if (CreateMode) {
							DataAccess.execute({
								url : "createContactSchoolAjax",
								data : data
							}, Notify.createCustomer);
						} else {
							DataAccess.execute({
								url : "updateContactSchoolAjax",
								data : data
							}, Notify.createCustomer);
						}
					}
					break;
				default:
					break;
				}
			}
		};
		var checkDuplicateNumberPhone = function(contactNumber) {
			if (!contactNumber) {
				return true;
			}
			return DataAccess.getData({
				url : "checkDuplicateNumberPhone",
				data : {
					contactNumber : contactNumber
				},
				source : "*"
			});
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			_data : _data,
			renderUser : renderUser,
			initComboboxGeo : initComboboxGeo,
			initEventComboboxGeo : initEventComboboxGeo,
			saveCustomer : saveCustomer,
		};
	})();
}
if (typeof (Notify) == "undefined") {
	var Notify = (function() {
		var createCustomer = function(res) {
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				$("#saveCustomerInfo").notify(multiLang.updateError, {
					position : "left",
					className : "error"
				});
				result = false;
			} else {
				if (CreateMode) {
					$("#saveCustomerInfo").notify(multiLang.addSuccess, {
						position : "left",
						className : "success"
					});
					globalFamilyId = res["familyId"];
					CookieLayer.setCurrentParty(res["partyId"], "CONTACT");
					CreateMode = false;
					setTimeout(function() {
						Processor.renderUser();
					}, 200);
				} else {
					$("#saveCustomerInfo").notify(multiLang.updateSuccess, {
						position : "left",
						className : "success"
					});
				}
			}
		};
		return {
			createCustomer : createCustomer
		};
	})();
}
if (typeof (ExtendScreen) == "undefined") {
	var ExtendScreen = (function() {
		var showExtendInfo = function() {
			$("#extraInfor").show();
		};
		var hideExtendInfo = function() {
			$("#extraInfor").hide();
		};
		var initCollapse = function() {
			var list = $(".collapse");
			for (var x = 0; x < list.length; x++) {
				(function(x) {
					var obj = $(list[x]);
					var header = obj.siblings(".widget-header");
					header.click(function() {
						var party = CookieLayer.getCurrentParty();
						if (party.partyId) {
							obj.collapse("toggle");
						} else {
							SearchEngine.focusSearch();
						}
					});
					obj.on("show.bs.collapse", function() {
						var id = obj.attr("id");
						$("a[href='#" + id + "'] .fa-chevron-left").addClass(
								"fa-chevron-down");
					});
					obj.on("hide.bs.collapse", function() {
						var id = obj.attr("id");
						$("a[href='#" + id + "'] .fa-chevron-left")
								.removeClass("fa-chevron-down");
					});
				})(x);
			}
		};
		var initCollapseData = function() {
			var reloadCollapseData = function(obj, init, callback) {
				obj.on("show.bs.collapse", function() {
					var x = obj.data("id");
					var party = CookieLayer.getCurrentParty();
					if ((!x && party.partyId) || (x && x != party.partyId)) {
						init();
						$(this).attr("data-id", party.partyId);
						setTimeout(function() {
							callback(party.partyId);
							// focus(obj.parent());
						}, 1000);
					} else {
						// obj.collapse("toggle")
						// focusSearch();
					}
				});
			};
			// setTimeout(function(){
			// var party = CookieLayer.getCurrentParty();
			// if(party.partyId){
			// $("#memberUsingCollapse").trigger("show.bs.collapse");
			// }
			// }, 100);
			// $("#memberUsingCollapse").trigger("show.bs.collapse");
			reloadCollapseData($("#memberUsingCollapse"), Family.init, Family.updateGridFamily);
			if (typeof initGridlistOrderCustomer != "undefined") reloadCollapseData($("#orderContainer"), initGridlistOrderCustomer, renderOrder);
			if (typeof initGridlistAgreementCustomer != "undefined") reloadCollapseData($("#agreementContainer"), initGridlistAgreementCustomer, renderAgreement);
			if (typeof initGridlistCustomerPayment != "undefined") reloadCollapseData($("#paymentContainer"), initGridlistCustomerPayment, renderPayment);
			if (typeof initGridlistCustomerInvoice != "undefined") reloadCollapseData($("#invoiceContainer"), initGridlistCustomerInvoice, renderInvoice);
		};
		var renderOrder = function(partyId) {
			var grid = $("#listOrderCustomer");
			var url = "jqxGeneralServicer?sname=JQListSalesOrder&partyId="
					+ partyId + "&ia=Y";
			changeGridUrl(grid, url);
		};
		var renderAgreement = function(partyId) {
			var grid = $("#listAgreementCustomer");
			var adapter = grid.jqxGrid("source");
			if (adapter) {
				adapter.url = "jqxGeneralServicer?sname=JQGetListAgreementsOfPartner&partyIdFrom="
						+ partyId;
				adapter._source.url = adapter.url;
				grid.jqxGrid("source", adapter);
			}
		};
		var renderPayment = function(partyId) {
			var grid = $("#listCustomerPayment");
			var url = "jqxGeneralServicer?sname=JQGetListPayment&partyId="
					+ partyId + "&organizationPartyId=" + "company";
			changeGridUrl(grid, url);
		};
		var renderInvoice = function(partyId) {
			var grid = $("#listCustomerInvoice");
			var url = "jqxGeneralServicer?sname=JQGetListInvoice&partyId="
					+ partyId + "&organizationPartyId=" + "company";
			changeGridUrl(grid, url);
		};
		var changeGridUrl = function(grid, url) {
			var adapter = grid.jqxGrid("source");
			if (adapter) {
				adapter.url = url;
				adapter._source.url = url;
				grid.jqxGrid("source", adapter);
			}
		};
		var refresh = function() {
			var partyId = CookieLayer.getCurrentParty().partyId;
			if (partyId) {
				if ($("#orderContainer").hasClass("in")) {
					renderOrder(partyId);
				}
				if ($("#agreementContainer").hasClass("in")) {
					renderAgreement(partyId);
				}
				if ($("#paymentContainer").hasClass("in")) {
					renderPayment(partyId);
				}
				if ($("#invoiceContainer").hasClass("in")) {
					renderInvoice(partyId);
				}
				// CustomerConsideration.load(partyId);
				PurchaseHistory.load(partyId);
			}
		};
		var initHiddenValue = function() {
			if (typeof document.hidden !== "undefined") {
				hidden = "hidden";
				visibilityChange = "visibilitychange";
			} else if (typeof document.mozHidden !== "undefined") {
				hidden = "mozHidden";
				visibilityChange = "mozvisibilitychange";
			} else if (typeof document.msHidden !== "undefined") {
				hidden = "msHidden";
				visibilityChange = "msvisibilitychange";
			} else if (typeof document.webkitHidden !== "undefined") {
				hidden = "webkitHidden";
				visibilityChange = "webkitvisibilitychange";
			}
		};
		var handleVisibilityChange = function(run) {
			if (document[hidden]) {
				// console.log("leave");
			} else {
				refresh();
			}
		};
		return {
			init : function() {
				initCollapse();
				initCollapseData();
				initHiddenValue();
				handleVisibilityChange();
				document.addEventListener(visibilityChange,
						handleVisibilityChange, false);
			},
			refresh : refresh,
			showExtendInfo : showExtendInfo,
			hideExtendInfo : hideExtendInfo,
			changeGridUrl : changeGridUrl
		};
	})();
}