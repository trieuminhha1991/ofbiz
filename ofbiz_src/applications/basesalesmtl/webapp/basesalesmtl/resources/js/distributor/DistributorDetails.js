$(document).ready(function() {
	if (partyIdPram) {
		AddDistributor.letUpdate(partyIdPram);
		Grid.removeContextMenuHoverStyle($("#shippingContactMechGrid"));
	}
});

if (typeof (AddDistributor) == "undefined") {
	var AddDistributor = (function() {
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
				$("#txtPhoneNumber").text(data.contactNumber);
				$("#txtEmailAddress").text(data.infoString);
				Representative.setValue(data.representative);
				if (data.logoImageUrl) {
					$("#logoImage").attr("src", data.logoImageUrl);
				}
				$("#statusId").text(mapStatusItem[data.statusId]);
				if (data.statusId === "PARTY_DISABLED") {
					$("#accept-wrapper").removeClass("hide");
					AddDistributor.handleEvents();
				}
			}
		};
		var handleEvents = function() {
			$("#jqxNotificationNested").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			$("#btnAccept").click(function() {
				DataAccess.execute({
								url: "setPartyStatus",
								data: {
									partyId: partyIdPram,
									statusId: "PARTY_ENABLED"}
								}, notify);
			});
		};
		var letUpdate = function(partyId) {
			/*var data = DataAccess.getData({
				url: "loadDistributorInfo",
				data: {partyId: partyId, detail: "Y"},
				source: "distributorInfo"});*/
			var data = {};
			AddDistributor.setValue(data);
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
				DataAccess.execute({
					url: "createNotification",
					data: {partyId: "OLBLOGM", header: BSNewDistributorNotify, action: "DistributorDetail",
							targetLink: "partyId=" + partyIdPram, ntfType: "ONE", sendToSender: "Y"}
					});
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