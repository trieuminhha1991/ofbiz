$(function(){
	ctxMenuShipCompany.init();
});

var ctxMenuShipCompany = (function(){
	var init = (function(){
		initElement();
		initEvent();
	});
	var initElement = function(){
		$("#contextMenu").jqxMenu({ width: 300, height: 120, autoOpenPopup: false, mode: 'popup'});
	};
	var initEvent = function(){
		$("#contextMenu").unbind('itemclick').on('itemclick', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        checkAction(dataRecord, event);
			
		});
		
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgrid").jqxGrid('getSelectedRowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getRowData', rowindex);
	        if ( dataRecord.statusId == 'PARTY_DISABLED') {
	        	$("#contextMenu").jqxMenu('disable', 'contextMenu_remove', true);
	        	/*$("#contextMenu").jqxMenu('disable', 'contextMenu_edit', true);*/
	        }
	        else {
	        	$("#contextMenu").jqxMenu('disable', 'contextMenu_remove', false);
	        	/*$("#contextMenu").jqxMenu('disable', 'contextMenu_edit', false);*/
	        }
		});
		
		
		function checkAction(dataRecord, event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);	  
	        if($(args).attr("action") == 'addShipCompany'){
	        	openJqxWindow($("#alterpopupWindow"));
	        }else if($(args).attr("action") == 'editShipCompany'){
	        	companyEdittingId = dataRecord.partyCode;
	        	$("#txtCompanyIdEdit").val(dataRecord.partyCode);
	        	$("#txtShipCompanyNameEdit").val(dataRecord.groupName);
	        	if (dataRecord.description != null) {
	        		$("#txtDescriptionEdit").val(dataRecord.description);
	        	} 
	        	else $("#txtDescriptionEdit").val("");
	        	openJqxWindow($("#editPopupWindow"));
			}else if($(args).attr("action") == 'removeShipCompany'){
				removeShipCompany(dataRecord.partyCode);
			}else if($(args).attr("action") == 'refresh'){
				$('#jqxgrid').jqxGrid('updatebounddata');
			}
		};
		
		function openJqxWindow(jqxWindowDiv){
			var wtmp = window;
			var tmpwidth = jqxWindowDiv.jqxWindow('width');
			jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			jqxWindowDiv.jqxWindow('open');
		};
		
		
		var removeShipCompany = function(partyCode) {
			bootbox.dialog(uiLabelMap.AreYouSureRemoveShipCompany, 
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
				    			url : 'removeShipCompany',
				    			type : "POST",
				    			data : {
				    				companyId: partyCode,
				    			},
				    			beforeSend: function(){
				    				
				    			},
				    			success : function(data) {
				    				$('#container').empty();
				                    $('#jqxNotification').jqxNotification({ template: 'success'});
				                    $("#notificationContent").text(uiLabelMap.RemoveSuccess);
				                    $("#jqxNotification").jqxNotification("open");
				                    $('#jqxgrid').jqxGrid('updatebounddata');
				    			},
				    			error: function(data){
				    				alert("Send request is error");
				    			},
				    			complete : function(jqXHR, textStatus) {
				    			}
				    		});
				            Loading.hide('loadingMacro');
				    	}, 500);
		            }
		        }]);
		};
	};
	
	return {
		init: init,
	}
}());