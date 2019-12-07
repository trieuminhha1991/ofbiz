$(document).ready(function() {
	QADocumentation.init();
});
if (typeof (QADocumentation) == "undefined") {
	var QADocumentation = (function() {
		var mainWindow;
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 500, height: 330, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelDoc"), modalOpacity: 0.7, theme: theme
	        });
			$("#registerDate").jqxDateTimeInput({width: "300px", height: "25px", theme: theme});
			$("#registerDate").jqxDateTimeInput("clear");
			$("#sampleSentDate").jqxDateTimeInput({width: "300px", height: "25px", theme: theme});
			$("#registerNumber").jqxInput({width: "295px", height: "23px", theme: theme});
			$("#sampleSentDate").jqxDateTimeInput("clear");
		};
		var handleEvents = function() {
			$('#saveDoc').on('click', function() {
				if (mainWindow.jqxValidator("validate")) {
					jQuery.ajax({
				        url: 'updateDocumentCustomsAjax',
				        type: 'POST',
				        async: false,
				        data: QADocumentation.getValue(),
				        dataType: 'json',
				        success: function(res){
				        	mainWindow.jqxWindow('close');
				        }
					 });
				}
			});
			$("#saveDocAndDownload").on("click", function(){
				if (mainWindow.jqxValidator("validate")) {
					
					jQuery.ajax({
				        url: "updateDocumentCustomsAjax",
				        type: "POST",
				        async: false,
				        data: QADocumentation.getValue(),
				        dataType: "json",
				        success: function(res){
				        	mainWindow.jqxWindow("close");
				        }
					}).done(function() {
						switch ($('#documentCustomsTypeId').val()) {
						case "TESTED":
							window.location.href = "exportDocumentTested?containerId=" + $("#containerCustomsId").val();
							break;
						case "QUARANTINE":
							window.location.href = "exportDocumentQuarantine?containerId=" + $("#containerCustomsId").val();
							break;
						default:
							break;
						}
					});
				}
			});
		};
		var initValidator = function() {
			mainWindow.jqxValidator({
			    rules: [{ input: "#registerNumber", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
			            { input: "#registerDate", message: multiLang.fieldRequired, action: "valueChanged", 
			            	rule: function (input, commit) {
			            		var value = input.jqxDateTimeInput("getDate");
			            		if (value > 0) {
			            			return true;
								}
			            		return false;
			            	}
			            },
			            { input: "#sampleSentDate", message: multiLang.fieldRequired, action: "valueChanged", 
			            	rule: function (input, commit) {
			            		var value = input.jqxDateTimeInput("getDate");
			            		if (value > 0) {
			            			return true;
			            		}
			            		return false;
			            	}
			            }]
			});
		};
		var getValue = function() {
			var value = new Object();
			value.documentCustomsId = $("#documentCustomsId").val();
			value.containerId = $("#containerCustomsId").val();
			value.documentCustomsTypeId = $("#documentCustomsTypeId").val();
			value.registerNumber = $("#registerNumber").jqxInput('val');
			value.registerDate = $("#registerDate").jqxDateTimeInput("getDate").getTime();
			value.sampleSendDate = $("#sampleSentDate").jqxDateTimeInput("getDate").getTime();
			return value;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#headerDoc").text(data);
			}
		};
		var open = function (data) {
			QADocumentation.setValue(data);
			var wtmp = window;
			var tmpwidth = mainWindow.jqxWindow("width");
	        mainWindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        mainWindow.jqxWindow("open");
		};
		return {
			init: function() {
				mainWindow = $("#popupDocQA");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			setValue: setValue,
			open: open
		};
	})();
}