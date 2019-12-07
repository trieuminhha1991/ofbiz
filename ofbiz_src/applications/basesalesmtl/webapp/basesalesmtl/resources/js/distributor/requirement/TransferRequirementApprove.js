$(document).ready(function() {
	RequirementApprove.init();
});
if (typeof (RequirementApprove) == "undefined") {
	var RequirementApprove = (function() {
		var initJqxElements = function() {
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var setRequirementStatus = function(requirementId, statusId) {
			DataAccess.execute({
			url: "setRequirementStatus",
			data: {
				requirementId: requirementId,
				statusId: statusId}
			}, this.notify);
		};
		var processRequirement = function(action) {
			switch (action) {
			case "Destroyed":
				this.Destroyed.showOption();
				break;
			case "Transfer":
				this.Transfer.execute();
				break;
			default:
				break;
			}
		};
		var Transfer = (function() {
			var execute = function() {
				DataAccess.execute({
					url: "executeWithdrawalRequirement",
					data: Transfer.getValue()
					}, RequirementApprove.notify);
			};
			var getValue = function() {
				var value = {
					distributorId: $("#distributorId").val(),
					requirementId: $("#requirementId").text().trim(),
				};
				return value;
			};
			return {
				getValue: getValue,
				execute: execute
			}
		})();
		var Destroyed = (function() {
			var jqxwindow;
			var initJqxElements = function() {
				
			};
			var handleEvents = function() {
				
			};
			var initValidator = function() {
				
			};
			var getValue = function() {
				
			};
			var showOption = function() {
				
			};
			return {
				init: function() {
					initJqxElements();
					handleEvents();
					initValidator();
				},
				showOption: showOption,
				getValue: getValue
			}
		})();
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotification").jqxNotification({ template: "error"});
				$("#notificationContent").text(errormes);
				$("#jqxNotification").jqxNotification("open");
			}else {
				$("#jqxNotification").jqxNotification({ template: "info"});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
				location.reload();
			}
		};
		return {
			init: function() {
				initJqxElements();
				Destroyed.init();
			},
			setRequirementStatus: setRequirementStatus,
			processRequirement: processRequirement,
			Destroyed: Destroyed,
			Transfer: Transfer,
			notify: notify
		}
	})();
}