$(document).ready(function() {
	EditFacility.init();
});
if (typeof (EditFacility) == "undefined") {
	var EditFacility = (function() {
		var validatorSHIP;
		var mainWindow;
		var initJqxElements = function() {
			mainWindow.jqxWindow({
	            width: 500, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelEdit"), modalOpacity: 0.7, theme: theme
	        });
			$("#txtFacilityId").jqxInput({width: "90%", height: "23px", theme: theme , disabled: true});
			$("#txtFacilityName").jqxInput({width: "90%", height: "23px", theme: theme, disabled: true});
			$("#txtFacilityRequireDate").jqxDropDownList({
			  	source: RequireDateData,
				width : '90%',
				height : 23,
				valueMember: 'value', displayMember: 'description',
				theme: Theme,
				dropDownHeight: 100,
			});
		};
		var handleEvents = function() {
			mainWindow.on("close", function (event) {
				mainWindow.jqxValidator("hide");
			});
			
			$("#alterSaveEdit").click(function () {
				if (mainWindow.jqxValidator("validate")) {
					bootbox.dialog(uiLabelMap.AreYouSureEdit, 
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
					    			url : 'updateFacilityRequireDate',
					    			type : "POST",
					    			data : EditFacility.getValue(),
					    			beforeSend: function(){
					    				
					    			},
					    			success : function(response) {
					    				$('#container').empty();
					                    $('#grid').jqxGrid('updatebounddata');
					    				$("#editPopupWindow").jqxWindow('close'); 
					    				$('#jqxNotification').jqxNotification({ template: 'success'});
						                $("#notificationContent").text(uiLabelMap.UpdateSuccess);
						                $("#jqxNotification").jqxNotification("open");
					    				
					    			},
					    			error: function(data){
					    				alert("Send request is error");
					    			},
					    			complete : function(jqXHR, textStatus) {
					    			}
					    		});
					            $("#grid").jqxGrid("clearSelection");
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
	   				{input: '#txtFacilityName', type: 'validInputNotNull'},
	   				{input: '#txtFacilityId', type: 'validInputNotNull'},
	   				{input: '#txtFacilityRequireDate', type: 'validInputNotNull'},
	   				];
	   		validatorSHIP = new OlbValidator($('#editPopupWindow'), mapRules, null, {position: 'right'});
		};
		
		
		var getValue = function() {
			var value = new Object();
			value.facilityEdittingId = facilityEdittingId;
			value.facilityId = $("#txtFacilityId").jqxInput('val');
			value.facilityName = $("#txtFacilityName").jqxInput('val');
			value.requireDate = $("#txtFacilityRequireDate").jqxDropDownList('val');
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