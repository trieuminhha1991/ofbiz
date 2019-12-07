$(document).ready(function() {
	EditShipCompany.init();
});
if (typeof (EditShipCompany) == "undefined") {
	var EditShipCompany = (function() {
		var validatorSHIP;
		var mainWindow;
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 500, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme: theme
	        });
			$("#txtCompanyIdEdit").jqxInput({width: "295", height: "23px", theme: theme});
			$("#txtShipCompanyNameEdit").jqxInput({width: "295", height: "23px", theme: theme});
			$("#txtDescriptionEdit").jqxTextArea({width: "295", height: "46px", theme: theme});
		};
		var handleEvents = function() {
			mainWindow.on("close", function (event) {
				mainWindow.jqxValidator("hide");
			});
			
			$("#alterSaveEdit").click(function () {
				if (mainWindow.jqxValidator("validate")) {
					bootbox.dialog(uiLabelMap.AreYouSureEditShipCompany, 
						[{"label": uiLabelMap.CommonCancel, 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        }, 
				        {"label": uiLabelMap.OK,
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {
							
					    	setTimeout(function(){
					    		Loading.show('loadingMacro');
					    		$.ajax({
					    			url : 'updateShipCompany',
					    			type : "POST",
					    			data : EditShipCompany.getValue(),
					    			beforeSend: function(){
					    				
					    			},
					    			success : function(response) {
					    				$('#container').empty();
					                    $('#jqxgrid').jqxGrid('updatebounddata');
					    				$("#editPopupWindow").jqxWindow('close'); 
					    				if (response._ERROR_MESSAGE_ == "COMPANYID_DUPLICATED") {
					    					alert(uiLabelMap.CompanyIdDuplicated);
					    				} else {
					    					$('#jqxNotification').jqxNotification({ template: 'success'});
						                    $("#notificationContent").text(uiLabelMap.UpdateSuccess);
						                    $("#jqxNotification").jqxNotification("open");
					    				}
					    			},
					    			error: function(data){
					    				alert("Send request is error");
					    			},
					    			complete : function(jqXHR, textStatus) {
					    			}
					    		});
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
	   				{input: '#txtShipCompanyNameEdit', type: 'validInputNotNull'},
	   				{input: '#txtCompanyIdEdit', type: 'validInputNotNull'},
	   				];
	   		validatorSHIP = new OlbValidator($('#editPopupWindow'), mapRules, null, {position: 'right'});
		};
		
		
		var getValue = function() {
			var value = new Object();
			value.companyEdittingId = companyEdittingId;
			value.companyId = $("#txtCompanyIdEdit").jqxInput('val');
			value.companyName = $("#txtShipCompanyNameEdit").jqxInput('val');
			value.description = $("#txtDescriptionEdit").jqxTextArea('val');

			return value;
		};
		return {
			init: function() {
				mainWindow = $("#editPopupWindow");
				initJqxElements();
				handleEvents()
				initValidator();
			},
			getValue: getValue,
		};
	})();
}