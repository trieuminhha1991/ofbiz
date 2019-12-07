Loading.show();
$(document).ready(function() {
	RetailStore.init();
	if (!_.isEmpty(productStoreId)) {
        RetailStore.UpdateMode = true;
        RetailStore.load(productStoreId);
    }
    Loading.hide();
});
if (typeof (RetailStore) == "undefined") {
	var RetailStore = (function() {
		var jqxNotification, creatable = true;
		var initJqxElements = function() {
			$("#fuelux-wizard").ace_wizard().on("change" , function(e, info) {
				if (info.step == 1 && (info.direction == "next")) {
					if (RetailStoreInfo.validate()) {
						RetailStoreConfirm.setValue(RetailStoreInfo.getDetail());
						return true;
					}
					return false;
				}
			}).on("finished", function(e) {
				if (creatable) {
					create();
				}
			});
			
			jqxNotification.jqxNotification({ opacity : 0.9, autoClose : true, template : "info" });
		};
		var create = function() {
			creatable = false;
			DataAccess.execute({
				//url: "quickCreateRetailStore",
				url: RetailStore.UpdateMode?"quickUpdateRetailStore":"quickCreateRetailStore",
				data: RetailStoreInfo.getValue()},
			RetailStore.notify);
		};
		var load = function(productStoreId) {
            DataAccess.execute({
                url: "loadRetailStoreDetail",
                data: { productStoreId: productStoreId }},
                RetailStoreInfo.setValue);
        };
		var notify = function(res) {
			jqxNotification.jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				creatable = true;
				jqxNotification.jqxNotification({ template : "error" });
				$("#notificationContent").text(multiLang.updateError);
				jqxNotification.jqxNotification("open");
			} else {
				jqxNotification.jqxNotification({ template : "info" });
				//$("#notificationContent").text(multiLang.addSuccess);
				$("#notificationContent").text(RetailStore.UpdateMode?multiLang.updateSuccess:multiLang.addSuccess);
				jqxNotification.jqxNotification("open");
				setTimeout(function() {
					location.href = "listProductStore";
				}, 1000);
			}
		};
		return {
			init: function() {
				jqxNotification = $("#jqxNotification");
				initJqxElements();
				RetailStoreInfo.init();
			},
			UpdateMode: false,
            load: load,
			notify: notify
		}
	})();
}