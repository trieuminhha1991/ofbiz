$(function(){
	OlbSettingProductStoreRoleDel.init();
});
var OlbSettingProductStoreRoleDel = (function(){
	var initWindow = (function(){
		var windowDeleteMember =  $('#popupDeleteMember').jqxWindow({ width: 320, height : 120,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7 });
	});
	
	var eventChooseDel = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
	        if (action == "delete") {
	        	var wtmp = window;
	    	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
	    	   	var tmpwidth = $('#popupDeleteMember').jqxWindow('width');
	    	   	$('#popupDeleteMember').jqxWindow('open');
	        }
		});
	});
	
	var eventDelete = (function(){
		$('#alterSave3').click(function () {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		   	if (rowindex >= 0) {
			   	deletePSRole();
	           	$('#popupDeleteMember').jqxWindow('hide');
	           	$('#popupDeleteMember').jqxWindow('close');
		   	}
	    });
		
		function deletePSRole(){
			var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
			var success = successK;
			var aProductStoreRole = new Array();
				var data2 = $("#jqxgrid").jqxGrid('getrowdata', row);
				var map = {};
				map['partyId'] = data2.partyId;
				map['productStoreId'] = data2.productStoreId;
				map['roleTypeId'] = data2.roleTypeId;
				map['fromDate'] = data2.fromDate.getTime();
				if(!data2.thruDate){
					map['thruDate'] = data2.fromDate.getTime();
				}else{
					map['thruDate'] = data2.thruDate.getTime();
				}
				aProductStoreRole = map;
			if (aProductStoreRole.length <= 0){
				return false;
			} else {
				aProductStoreRole = JSON.stringify(aProductStoreRole);
				jQuery.ajax({
			        url: 'deleteProductStoreRole',
			        type: 'POST',
			        async: true,
			        data: {
			        		'aProductStoreRole': aProductStoreRole,
		        		},
			        success: function(res) {
			        	var message = '';
						var template = '';
						if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
							if(res._ERROR_MESSAGE_LIST_){
								message += res._ERROR_MESSAGE_LIST_;
							}
							if(res._ERROR_MESSAGE_){
								message += res._ERROR_MESSAGE_;
							}
							template = 'error';
						}else{
							message = success;
							template = 'success';
							$("#jqxgrid").jqxGrid('updatebounddata');
			        		$("#jqxgrid").jqxGrid('clearselection');
			        		window.location.reload();
						}
						updateGridMessage('jqxgrid', template ,message);
			        },
			        error: function(e){
			        	console.log(e);
			        }
			    });
			}
		}
	});
	
	var eventClosePopup = (function(){
		$('#popupDeleteMember').on('close',function(){
			$('#jqxgrid').jqxGrid('refresh');
		});
	});
	
	var init = (function(){
		initWindow();
		eventChooseDel();
		eventDelete();
		eventClosePopup();
	});
	
	return{
		init: init,
	}
}());