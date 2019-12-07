$(document).ready(function() {
	ThemesSetting.init();
});
if (typeof (ThemesSetting) == "undefined") {
	var ThemesSetting = (function() {
		var initJqxElements = function() {
			$("#jqxNotification").jqxNotification({
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});
		};
		var handleEvents = function() {
			$(".shortcut-icon").click(function() {
				$("#txtShortcutIcon").click();
			});
			$("#txtShortcutIcon").change(function() {
				ThemesSetting.readURL(this, $("#shortcutIcon"));
			});
			$(".logo-group").click(function() {
				$("#txtLogoGroup").click();
			});
			$("#txtLogoGroup").change(function() {
				ThemesSetting.readURL(this, $("#logoGroup"));
			});

			$("#btnSave").click(function() {
				DataAccess.execute({
					url : "updateVisualThemeResources",
					data : {
						resources : ThemesSetting.getValue()
					}
				}, ThemesSetting.notify);
			});
		};
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
				$("#btnSave").removeClass("hidden");
				var reader = new FileReader();
				reader.onload = function(e) {
					img.attr("src", e.target.result);
				}
				reader.readAsDataURL(input.files[0]);
			}
		};
		var getValue = function() {
			var value = new Array();
			if ($("#txtShortcutIcon").prop("files")[0]) {
				var url = DataAccess.uploadFile($("#txtShortcutIcon").prop(
						"files")[0]);
				if (url) {
					value.push({
						visualThemeId : "ACEADMIN",
						resourceTypeEnumId : "VT_SHORTCUT_ICON",
						sequenceId : "01",
						resourceValue : url
					});
				}
			}
			if ($("#txtLogoGroup").prop("files")[0]) {
				var url = DataAccess.uploadFile($("#txtLogoGroup")
						.prop("files")[0]);
				if (url) {
					value.push({
						visualThemeId : "ACEADMIN",
						resourceTypeEnumId : "VT_HDR_IMAGE_URL",
						sequenceId : "01",
						resourceValue : url
					});
				}
			}
			return JSON.stringify(value);
		};
		var notify = function(res) {
			$("#jqxNotification").jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				$("#jqxNotification").jqxNotification({
					template : "error"
				});
				$("#notificationContent").text(multiLang.updateError);
				$("#jqxNotification").jqxNotification("open");
			} else {
				$("#jqxNotification").jqxNotification({
					template : "info"
				});
				$("#notificationContent").text(multiLang.updateSuccess);
				$("#jqxNotification").jqxNotification("open");
				setTimeout(function() {
					location.reload();
				}, 1000);
			}
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			readURL : readURL,
			getValue : getValue,
			notify : notify
		}
	})();
}