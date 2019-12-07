$(document).ready(function() {
	if (agreementIdParam) {
		Agreement.letUpdate(agreementIdParam);
	}
});
if (typeof (Agreement) == "undefined") {
	var Agreement = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#agreementCode").text(data.agreementCode);
				$("#tarDescription").text(data.description);
				if (data.agreementDate) {
					$('#divAgreementDate').text(new Date(data.agreementDate).toTimeOlbius());
				}
				if (data.fromDate) {
					$('#divFromDate').text(new Date(data.fromDate).toTimeOlbius());
				}
				if (data.thruDate) {
					$('#divThruDate').text(new Date(data.thruDate).toTimeOlbius());
				}
				$("#statusId").text(mapStatusItem[data.statusId]);
				if (data.statusId == "AGREEMENT_CREATED" || data.statusId == "AGREEMENT_MODIFIED") {
					$("#accept-wrapper").removeClass("hide");
					Agreement.handleEvents();
				}
			}
		};
		var handleEvents = function() {
			$("#jqxNotificationNested").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			$("#btnAccept").click(function() {
				$("#btnAccept").attr("disabled", true);
				DataAccess.execute({
								url: "approveAgreement",
								data: {agreementId: agreementIdParam}
								}, notify);
			});
		};
		var letUpdate = function(agreementId) {
			var data = DataAccess.getData({
				url: "loadAgreementInfo",
				data: {agreementId: agreementId},
				source: "agreementInfo"});
			Agreement.setValue(data);
			PartyA.setValue(data);
			PartyB.setValue(data);
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
				$("#statusId").text(mapStatusItem["AGREEMENT_APPROVED"]);
			}
		};
		return {
			setValue: setValue,
			letUpdate: letUpdate,
			handleEvents: handleEvents,
			notify: notify
		};
	})();
}
if (typeof (PartyA) == "undefined") {
	var PartyA = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$('#divPartyTo').text(LocalUtility.getPartyName(data.partyIdTo));
				$('#divFindRepresentTo').text(LocalUtility.getPartyName(data.representativeId));
				$("#divARepresentedBy").text(LocalUtility.getPartyName(data.representativeId));
				if (data.partyIdTo) {
					var data = DataAccess.getData({
							url: "getPartyInformationAjax",
							data: {partyId: data.partyIdTo},
							source: "partyInfo"});
					PartyA.setDetailInfo(data);
				}
			}
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
		return {
			setValue: setValue,
			setDetailInfo: setDetailInfo
		}
	})();
}
if (typeof (PartyB) == "undefined") {
	var PartyB = (function() {
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (data.partyIdFrom) {
					$('#divPartyFrom').text(LocalUtility.getPartyName(data.partyIdFrom));
					switch (data.partyTypeFrom) {
					case "RETAIL_OUTLET":
						var data = DataAccess.getData({
							url: "loadAgentInfo",
							data: {partyId: data.partyIdFrom, detail: "Y"},
							source: "agentInfo"});
						PartyB.setDetailInfo(data);
						$("#distributor").slideUp(1);
						$("#agent").slideDown(100);
						break;
					case "PARTY_GROUP":
						var data = DataAccess.getData({
							url: "loadDistributorInfo",
							data: {partyId: data.partyIdFrom, detail: "Y"},
							source: "distributorInfo"});
						PartyB.setDetailInfo(data);
						$("#agent").slideUp(1);
						$("#distributor").slideDown(100);
						break;
					default:
						break;
					}
				}
			}
		};
		var setDetailInfo = function(data) {
			if (!_.isEmpty(data)) {
				$("#divBAddress").text(data.address);
				$("#divBTelecom").text(data.contactNumber);
				$("#divBFax").text(data.faxNumber);
				if (data.taxAuthInfos) {
					$("#divBTaxCode").text(data.taxAuthInfos);
				}
				if (data.representative) {
					$("#divBRepresentedBy").text(data.representative.partyFullName);
				}
			}
		};
		return {
			setValue: setValue,
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