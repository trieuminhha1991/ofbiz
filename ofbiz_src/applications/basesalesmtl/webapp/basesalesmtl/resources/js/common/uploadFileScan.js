$(document).ready(function() {
	Uploader.init();
	Viewer.init();
});
if (typeof (Uploader) == "undefined") {
	var Uploader = (function() {
		var listImage = [], agreementId;
		var initJqxElements = function() {
			$("#jqxwindowUploadFile").jqxWindow({ theme: 'olbius',
			    width: 480, maxWidth: 1845, height: 250, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"),
			    modalOpacity: 0.7
			});
			$("#jqxNotificationNestedUploader").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			$('#id-input-file-3').ace_file_input({
	    		style:'well',
	    		btn_choose:'Drop files here or click to choose',
	    		btn_change:null,
	    		no_icon:'icon-cloud-upload',
	    		droppable:true,
	    		onchange:null,
	    		thumbnail:'small',
	    		before_change:function(files, dropped) {
	    			listImage = [];
	    			for (var int = 0; int < files.length; int++) {
	    				var imageName = files[int].name;
	    				var hashName = imageName.split(".");
	    				var extended = hashName.pop().toLowerCase();
	    				if (extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
	    					listImage.push(files[int]);
	    				} else {
							return false;
						}
	    			}
	    			return true;
	    		},
	    		before_remove : function() {
	    			listImage = [];
	    			return true;
	    		}
			});
		};
		var handleEvents = function() {
			$("#btnUploadFile").click(function () {
				if (listImage.length == 0) {
					alert(ChooseImagesToUpload);
				}
				for ( var x in listImage) {
					Uploader.uploadFile(listImage[x]);
				}
				Uploader.notify({});
				$("#jqxwindowUploadFile").jqxWindow('close');
			});
			$("#jqxwindowUploadFile").on("close", function() {
				agreementId = null;
				$('.ace-file-input').find(".icon-remove").click();
			});
		};
		var uploadFile = function(file) {
			var result;
			var form_data= new FormData();
			form_data.append("agreementId", agreementId);
			form_data.append("uploadedFile", file);
			$.ajax({
				url: "uploadFileScanAgreement",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				async: false,
				success: function() {}
			});
			return result;
		};
		var open = function(Id) {
			if (Id) {
				agreementId = Id;
				var wtmp = window;
				var tmpwidth = $('#jqxwindowUploadFile').jqxWindow('width');
		        $("#jqxwindowUploadFile").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
		        $("#jqxwindowUploadFile").jqxWindow('open');
			}
		};
		var notify = function(res) {
			$(window).scrollTop(0);
			$('#jqxNotificationNestedUploader').jqxNotification('closeLast');
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationNestedUploader").jqxNotification({ template: 'error'});
				$("#notificationContentNestedUploader").text(multiLang.updateError);
				$("#jqxNotificationNestedUploader").jqxNotification("open");
			}else {
				$("#jqxNotificationNestedUploader").jqxNotification({ template: 'info'});
				$("#notificationContentNestedUploader").text(uploadSuccessfully);
				$("#jqxNotificationNestedUploader").jqxNotification("open");
			}
		};
		return {
			init: function() {
				initJqxElements();
				handleEvents();
			},
			open: open,
			uploadFile: uploadFile,
			notify: notify
		};
	})();
}
if (typeof (Viewer) == "undefined") {
	var Viewer = (function() {
		var initJqxElements = function() {
			$("#jqxwindowViewFile").jqxWindow({ theme: 'olbius',
			    width: 900, maxWidth: 1845, height: "98%", resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelViewer"), modalOpacity: 0.7
			});
		};
		var loadData = function(agreementId) {
			var files = DataAccess.getData({
				url: "loadFileScanAgreement",
				data: {agreementId: agreementId},
				source: "fileScanAgreement"});
			var viewerFile = $("#contentViewerFile");
			for ( var x in files) {
				var img = $("<img/>");
				img.attr("src", files[x]);
				viewerFile.append(img);
			}
		};
		var open = function(agreementId) {
			loadData(agreementId);
			var wtmp = window;
			var tmpwidth = $('#jqxwindowViewFile').jqxWindow('width');
	        $("#jqxwindowViewFile").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 20 }});
	        $("#jqxwindowViewFile").jqxWindow('open');
		};
		return {
			init: function() {
				initJqxElements();
			},
			open: open
		};
	})();
}