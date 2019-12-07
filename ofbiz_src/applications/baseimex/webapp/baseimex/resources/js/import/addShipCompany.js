$(document).ready(function() {
	AddShipCompany.init();
});
if (typeof (AddShipCompany) == "undefined") {
	var AddShipCompany = (function() {
		var validatorSHIP;
		var mainWindow;
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 500, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
	        });
			$("#txtCompanyId").jqxInput({width: "295", height: "23px", theme: theme});
			$("#txtShipCompanyName").jqxInput({width: "295", height: "23px", theme: theme});
			$("#txtDescription").jqxTextArea({width: "295", height: "46px", theme: theme});
		};
		var handleEvents = function() {
			mainWindow.on("close", function (event) {
				mainWindow.jqxValidator("hide");
			});
			
			$("#alterSave").click(function () {
				if (mainWindow.jqxValidator("validate")) {
					bootbox.dialog(uiLabelMap.AreYouSureCreate, 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": uiLabelMap.OK,
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
							Loading.show('loadingMacro');
					    	setTimeout(function(){
					    		Loading.show('loadingMacro');
					    		$.ajax({
					    			url : 'addShipCompany',
					    			type : "POST",
					    			data : AddShipCompany.getValue(),
					    			beforeSend: function(){
					    				
					    			},
					    			success : function(response) {
					    				$('#container').empty();
					                    $('#jqxgrid').jqxGrid('updatebounddata');
					    				$("#alterpopupWindow").jqxWindow('close'); 
					    				if (response._ERROR_MESSAGE_ == "COMPANYID_DUPLICATED") {
					    					alert(uiLabelMap.CompanyIdDuplicated);
					    				} else {
					    					$('#jqxNotification').jqxNotification({ template: 'success'});
						                    $("#notificationContent").text(uiLabelMap.CreateSuccess);
						                    $("#jqxNotification").jqxNotification("open");
					    				}
					    			},
					    			error: function(data){
					    				alert("Send request is error");
					    			},
					    			complete : function(jqXHR, textStatus) {
					    			}
					    		});
					    		resetData();
					            $("#jqxgrid").jqxGrid("clearSelection");
					            mainWindow.jqxWindow("close");
					            Loading.hide('loadingMacro');
					            
					    	}, 500);
			            }
			        }]);
				}
				
		    });
		};
		var initValidator = function() {
	   		var mapRules = [
	   				{input: '#txtShipCompanyName', type: 'validInputNotNull'},
	   				{input: '#txtCompanyId', type: 'validInputNotNull'},
	   				];
	   		validatorSHIP = new OlbValidator($('#alterpopupWindow'), mapRules, null, {position: 'right'});
		};
		var getValue = function() {
			var value = new Object();
			value.companyId = $("#txtCompanyId").jqxInput('val');
			value.companyName = $("#txtShipCompanyName").jqxInput('val');
			value.description = $("#txtDescription").jqxTextArea('val');
			return value;
		};
		var resetData = function() {
			$("#txtCompanyId").val("");
        	$("#txtShipCompanyName").val("");
        	$("#txtDescription").val("");
		}
		return {
			init: function() {
				mainWindow = $("#alterpopupWindow");
				initJqxElements();
				handleEvents()
				initValidator();
			},
			getValue: getValue,
		};
	})();
}