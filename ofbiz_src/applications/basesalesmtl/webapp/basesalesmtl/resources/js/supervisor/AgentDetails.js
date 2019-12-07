// TODO deleted
$(document).ready(function() {
	AdditionalContact.init();
	if (partyIdPram) {
		AddAgent.letUpdate(partyIdPram);
		Grid.removeContextMenuHoverStyle($("#shippingContactMechGrid"));
	}
});

if (typeof (AddAgent) == "undefined") {
	var AddAgent = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#groupName").text(data.groupName);
				$("#groupName").text(data.groupNameLocal);
				if (data.officeSiteName) {
					$("#officeSiteName").text(data.officeSiteName);
				}
				if (data.comments) {
					$("#comments").text(data.comments);
				}
				if (data.currencyUomId) {
					$("#currencyUomId").text(mapCurrencyUom[data.currencyUomId]);
				}
				if (data.taxAuthInfos) {
					$("#taxAuthInfos").text(data.taxAuthInfos);
				}
				if (data.contactNumber) {
					$("#txtPhoneNumber").text(data.contactNumber);
				}
				$("#txtEmailAddress").text(data.infoString);
				Representative.setValue(data.representative);
				if (data.logoImageUrl) {
					$("#logoImage").attr("src", data.logoImageUrl);
				}
				$("#statusId").text(mapStatusItem[data.statusId]);
				if (data.statusId === "PARTY_DISABLED") {
					$("#accept-wrapper").removeClass("hide");
					AddAgent.handleEvents();
				}
			}
		};
		var handleEvents = function() {
			$("#jqxNotificationNested").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			$("#btnAccept").click(function() {
				$("#btnAccept").attr("disabled", true);
				DataAccess.execute({
								url: "setPartyStatus",
								data: {
									partyId: partyIdPram,
									statusId: "PARTY_ENABLED"}
								}, notify);
			});
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadAgentInfo",
				data: {partyId: partyId, detail: "Y"},
				source: "agentInfo"});
			AddAgent.setValue(data);
			if (data.contacts) {
				AdditionalContact.initGrid(data.contacts);
			}
		};
		var notify = function(res) {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
		      	$("#notificationContentNested").text(errormes);
		      	$("#jqxNotificationNested").jqxNotification("open");
			}else {
				$("#jqxNotificationNested").jqxNotification({ template: 'info'});
		      	$("#notificationContentNested").text(multiLang.updateSuccess);
		      	$("#jqxNotificationNested").jqxNotification("open");
		      	$("#accept-wrapper").addClass("hide");
				$("#statusId").text(mapStatusItem["PARTY_ENABLED"]);
			}
		};
		return {
			handleEvents: handleEvents,
			setValue: setValue,
			letUpdate: letUpdate,
			notify: notify
		};
	})();
}