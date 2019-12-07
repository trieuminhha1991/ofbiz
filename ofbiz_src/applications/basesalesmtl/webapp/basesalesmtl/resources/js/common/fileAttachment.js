$(document).ready(function() {
	if (entityName) {
		Uploader.init();
		Viewer.init();
	} else {
		throw "Missing entityName";
	}
});
if (typeof (Uploader) == "undefined") {
	var Uploader = (function() {
		var fileAttachment = [], fields, jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme: theme,
				width: 480, maxWidth: 1845, height: 250, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"),
				modalOpacity: 0.7
			});
			$("#jqxNotificationUploader").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			$("#id-input-file-3").ace_file_input({
				style:"well",
				btn_choose:"Drop files here or click to choose",
				btn_change:null,
				no_icon:"icon-cloud-upload",
				droppable:true,
				onchange:null,
				thumbnail:"small",
				before_change:function(files, dropped) {
					fileAttachment = files;
					return true;
				},
				before_remove : function() {
					fileAttachment = [];
					return true;
				}
			});
		};
		var handleEvents = function() {
			$("#btnUploadFile").click(function () {
				if (fileAttachment.length == 0) {
					alert(ChooseImagesToUpload);
					return;
				}
				jqxwindow.jqxWindow("close");
				if (Loading) {
					Loading.show();
				}
				setTimeout(function() {
					for ( var x in fileAttachment) {
						Uploader.uploadFile(fileAttachment[x]);
					}
					if (Loading) {
						Loading.hide();
					}
					Uploader.notify({});
				});
			});
		};
		var uploadFile = function(file) {
			if (typeof (file) == "object") {
				if (file.size > 0) {
					var form_data= new FormData();
					form_data.append("fields", JSON.stringify(fields));
					form_data.append("entityName", entityName);
					form_data.append("uploadedFile", file);
					$.ajax({
						url: "uploadFileAttachment",
						type: "POST",
						data: form_data,
						cache : false,
						contentType : false,
						processData : false,
						async: true
					});
				}
			}
		};
		var open = function(fieldsParam) {
			if (fieldsParam) {
				fields = fieldsParam;
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
			} else {
				throw "missing fieldsParam";
			}
		};
		var notify = function(res) {
			$(window).scrollTop(0);
			$("#jqxNotificationUploader").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationUploader").jqxNotification({ template: "error"});
				$("#notificationContentUploader").text(multiLang.updateError);
				$("#jqxNotificationUploader").jqxNotification("open");
			}else {
				$("#jqxNotificationUploader").jqxNotification({ template: "info"});
				$("#notificationContentUploader").text(uploadSuccessfully);
				$("#jqxNotificationUploader").jqxNotification("open");
				location.reload();
			}
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowUploadFile");
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
		var jqxwindow;
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme: theme,
				width: 900, maxWidth: 1845, height: "98%", resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelViewer"), modalOpacity: 0.7
			});
		};
		var loadData = function(fields) {
			if (fields) {
				var files = DataAccess.getData({
					url: "loadFileAttachment",
					data: {fields: JSON.stringify(fields), entityName: entityName},
					source: "fileAttachment"});
				//	for type of files are imagination
				var viewerFile = $("#contentViewerFile");
				viewerFile.html("");
				for ( var x in files) {
					var container = $("<div></div>");
					container.addClass("img-container");
					var img = $("<img/>");
					img.attr("src", files[x]);
					container.append(img);
					viewerFile.append(container);
				}
			} else {
				throw "missing fields";
			}
		};
		var open = function(fieldsParam) {
			loadData(fieldsParam);
			var wtmp = window;
			var tmpwidth = jqxwindow.jqxWindow("width");
			jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 20 }});
			jqxwindow.jqxWindow("open");
		};
		return {
			init: function() {
				jqxwindow = $("#jqxwindowViewFile");
				initJqxElements();
			},
			open: open
		};
	})();
}