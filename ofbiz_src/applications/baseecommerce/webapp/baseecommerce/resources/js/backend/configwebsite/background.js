$(document).ready(function() {
	BackGround.init();
});
if (typeof (BackGround) == "undefined") {
	var BackGround = (function() {
		var load = function() {
			var data = DataAccess.getData({
						url: "loadBackGround",
						data: {},
						source: "webSiteBackGround"});
			if (data.HEADER_BACKGROUND) {
				$("#headerImage").attr('src', data.HEADER_BACKGROUND);
				$('#switchHeader').prop('checked', data.HEADER_BACKGROUND_S === "CTNT_PUBLISHED");
			}
			if (data.INFO_BACKGROUND) {
				$("#infoImage").attr('src', data.INFO_BACKGROUND);
				$('#switchInfo').prop('checked', data.INFO_BACKGROUND_S === "CTNT_PUBLISHED");
			}
			if (data.FOOTER_BACKGROUND) {
				$("#footerImage").attr('src', data.FOOTER_BACKGROUND);
				$('#switchFooter').prop('checked', data.FOOTER_BACKGROUND_S === "CTNT_PUBLISHED");
			}
		};
		var initJqxElements = function() {
			$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9,
				autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#headerImage").click(function() {
				$("#txtHeaderImage").click();
			});
			$("#txtHeaderImage").change(function(){
				Images.readURL(this, $('#headerImage'));
				$('#switchHeader').prop('checked', true);
			});

			$("#infoImage").click(function() {
				$("#txtInfoImage").click();
			});
			$("#txtInfoImage").change(function(){
				Images.readURL(this, $('#infoImage'));
				$('#switchInfo').prop('checked', true);
			});

			$("#footerImage").click(function() {
				$("#txtFooterImage").click();
			});
			$("#txtFooterImage").change(function() {
				Images.readURL(this, $('#footerImage'));
				$('#switchFooter').prop('checked', true);
			});

			$("#btnSaveBG").click(function() {
				var data = new Object();
				if ($('#txtHeaderImage').prop('files')[0]) {
					data.headerImage = DataAccess.uploadFile($('#txtHeaderImage').prop('files')[0]);
				}
				if ($('#txtInfoImage').prop('files')[0]) {
					data.infoImage = DataAccess.uploadFile($('#txtInfoImage').prop('files')[0]);
				}
				if ($('#txtFooterImage').prop('files')[0]) {
					data.footerImage = DataAccess.uploadFile($('#txtFooterImage').prop('files')[0]);
				}
				data.headerImage_s = $('#switchHeader').prop('checked')?"CTNT_PUBLISHED":"CTNT_DEACTIVATED";
				data.infoImage_s = $('#switchInfo').prop('checked')?"CTNT_PUBLISHED":"CTNT_DEACTIVATED";
				data.footerImage_s = $('#switchFooter').prop('checked')?"CTNT_PUBLISHED":"CTNT_DEACTIVATED";
				DataAccess.execute({
					url: "configBackGround",
					data: data},
					BackGround.notify);
			});
		};
		var notify = function(res) {
			$('body').scrollTop(0);
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
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
				load();
			},
			notify: notify
		};
	})();
}
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var readURL = function(input, img) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            img.attr('src', e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		return {
			readURL: readURL,
		};
	})();
}