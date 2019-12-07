<script>
	$(document).ready(function() {
		OwnerContact.init();
	});
	if (typeof (OwnerContact) == "undefined") {
		var OwnerContact = (function() {

		<#if security.hasEntityPermission("CALLCAMPAIGN", "_ADMIN", session)>
			var partyIdFrom, ownerEmployee;
			var initJqxElements = function() {
				var source = {
						datatype: "json",
						datafields: [
							{ name: "partyId" },
							{ name: "partyDetail" }
						],
						url: "getCallCenterEmployee"
				};
				var dataAdapter = new $.jqx.dataAdapter(source);

				$("#OwnerEmployee").jqxDropDownList({ theme: theme, width: "95%", height: 25, source: dataAdapter,
					displayMember: "partyDetail", valueMember: "partyId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
				$("#OwnerEmployeeBiz").jqxDropDownList({ theme: theme, width: "95%", height: 25, source: dataAdapter,
					displayMember: "partyDetail", valueMember: "partyId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			};
			var handleEvents = function() {
				$("#OwnerEmployee").on("bindingComplete", function (event) {
					if (partyIdFrom) {
						$("#OwnerEmployee").jqxDropDownList("val", partyIdFrom);
					}
				});
				$("#OwnerEmployeeBiz").on("bindingComplete", function (event) {
					if (partyIdFrom) {
						$("#OwnerEmployeeBiz").jqxDropDownList("val", partyIdFrom);
						partyIdFrom = null;
					}
				});
				$("#OwnerEmployee").on("change", function (event){
				    var args = event.args;
				    if (args) {
					    var index = args.index;
					    var item = args.item;
					    var label = item.label;
					    var value = item.value;
					    if (!CreateMode) {
						if (value != ownerEmployee.partyIdFrom) {
							bootbox.confirm(multiLang.ConfirmReassignContact, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
									if (result) {
										assignContactToParty(value);
										ownerEmployee.partyIdFrom = value;
									} else {
										$("#OwnerEmployee").jqxDropDownList("val", ownerEmployee.partyIdFrom);
									}
								});
							}
					    }
					}
				});
				$("#OwnerEmployeeBiz").on("change", function (event){
					var args = event.args;
					if (args) {
						var index = args.index;
						var item = args.item;
						var label = item.label;
						var value = item.value;
						if (!CreateMode) {
							if (value != ownerEmployee.partyIdFrom) {
								bootbox.confirm(multiLang.ConfirmReassignContact, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
									if (result) {
										assignContactToParty(value);
										ownerEmployee.partyIdFrom = value;
									} else {
										$("#OwnerEmployeeBiz").jqxDropDownList("val", ownerEmployee.partyIdFrom);
									}
								});
							}
						}
					}
				});
			};
			var assignContactToParty = function(partyId) {
				var contacts = new Array();
				var party = CookieLayer.getCurrentParty();
				contacts.push({partyId: party.partyId, fromDate: ownerEmployee.fromDate});
				DataAccess.execute({
						url: "assignContactToParty",
						data: {
							partyId: partyId,
							fromDate: ownerEmployee.fromDate,
							marketingCampaignId: ownerEmployee.marketingCampaignId,
							contacts: JSON.stringify(contacts)}
						}, OwnerContact.notify);
			};
			var notify = function(res) {
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#saveCustomerInfo").notify(multiLang.updateError, { position: "left", className: "error" });
			}else {
				$("#saveCustomerInfo").notify(multiLang.updateSuccess, { position: "left", className: "success" });
				}
			};
			var setValue = function(data) {
				if (data.ownerEmployee) {
					ownerEmployee = data.ownerEmployee;
					partyIdFrom = data.ownerEmployee.partyIdFrom;
					if (partyIdFrom && partyIdFrom != "N_A") {
						$("#OwnerEmployee").jqxDropDownList({ disabled: false });
						$("#OwnerEmployeeBiz").jqxDropDownList({ disabled: false });
						$("#OwnerEmployee").jqxDropDownList("val", partyIdFrom);
						$("#OwnerEmployeeBiz").jqxDropDownList("val", partyIdFrom);
					}
				}
			};
			var getValue = function() {
				var value = new Object();
				value.ownerEmployee = $("#OwnerEmployee").jqxDropDownList("val");
				return value;
			};
			var clean = function() {
				$("#OwnerEmployee").jqxDropDownList("clearSelection");
				$("#OwnerEmployeeBiz").jqxDropDownList("clearSelection");
			};
			var disabled = function() {
				$("#OwnerEmployee").jqxDropDownList({ disabled: false });
				$("#OwnerEmployeeBiz").jqxDropDownList({ disabled: false });
			};
		<#else>
			var initJqxElements = function() {

			};
			var handleEvents = function() {

			};
			var setValue = function(data) {
				if (data.ownerEmployee) {
					$("#OwnerEmployeeBiz").html(data.ownerEmployee.partyIdFrom);
					$("#OwnerEmployee").html(data.ownerEmployee.partyIdFrom);
				}
			};
			var getValue = function() {
				var value = new Object();
				value.ownerEmployee = null;
				return value;
			};
			var clean = function() {
				$("#OwnerEmployee").html("___");
				$("#OwnerEmployeeBiz").html("");
			};
			var disabled = function() {
				
			};
			var notify = function() {

			};
		</#if>

			return {
				init: function() {
					initJqxElements();
					handleEvents();
				},
				setValue: setValue,
				getValue: getValue,
				clean: clean,
				notify: notify,
				disabled: disabled
			};
		})();
	}
</script>