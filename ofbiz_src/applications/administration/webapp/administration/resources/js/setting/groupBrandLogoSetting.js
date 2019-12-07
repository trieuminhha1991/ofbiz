$(document).ready(function() {
	LogoSetting.init();
});
if (typeof (LogoSetting) == "undefined") {
	var LogoSetting = (function() {
		var initJqxElements = function() {
			$("#jqxNotification").jqxNotification({
				opacity : 0.9,
				autoClose : true,
				template : "info"
			});
		};
		var handleEvents = function() {
			$(".logo-group").click(function() {
				$("#txtLogoGroup").click();
			});
			$("#txtLogoGroup").change(function() {
				LogoSetting.readURL(this, $("#logoGroup"));
				$("#btnSave").removeClass("hidden");
			});

			$("#btnSave").click(
					function() {
						if ($("#txtLogoGroup").prop("files")[0]) {

							var form_data = new FormData();
							form_data.append("logoImg", $("#txtLogoGroup")
									.prop("files")[0]);
							$.ajax({
								url : "storeBrandLogo",
								type : "POST",
								data : form_data,
								cache : false,
								contentType : false,
								processData : false,
								async : false,
								success : function() {
								}
							}).done(function(res) {
								LogoSetting.notify(res);
							});
						}
					});
		};
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function(e) {
					img.attr("src", e.target.result);
				}
				reader.readAsDataURL(input.files[0]);
			}
		};
		var load = function() {
			var value = DataAccess.getData({
				url : "getBrandLogo",
				data : {
					partyId : null
				},
				source : "originalUrl"
			});
			return value;
		};
		var setValue = function() {
			$("#logoGroup").attr("src", LogoSetting.load());
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
				LogoSetting.setValue();
			},
			readURL : readURL,
			setValue : setValue,
			load : load,
			notify : notify
		}
	})();
}