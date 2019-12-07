$(function(){
	OlbSettingProductStoreCatalogDel.init();
});

var OlbSettingProductStoreCatalogDel = (function(){
	var initWindow = (function(){
		$('#popupDeleteMember').jqxWindow({ width: 320, height : 120,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7 });
	});
	
	var eventRightClickMenu = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action"); 
            if (action == 'delete') {
            	var wtmp = window;
        	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
        	   	var tmpwidth = $('#popupDeleteMember').jqxWindow('width');
        	   	$('#popupDeleteMember').jqxWindow('open');
            }
		});
	});
	
	var eventDeleteProductStoreCatalog = (function(){
		$('#alterSave3').click(function () {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		   	if (rowindex >= 0) {
			   	deletePSCatalog();
	           	$('#popupDeleteMember').jqxWindow('hide');
	           	$('#popupDeleteMember').jqxWindow('close');
		   	}
	    });
		
		function deletePSCatalog(){
			var deleteSuccess = "${StringUtil.wrapString(uiLabelMap.DADeleteSuccess)}";
			var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
			var success = deleteSuccess;
			var cMemberr = new Array();
				var data2 = $("#jqxgrid").jqxGrid('getrowdata', row);
				var map = {};
				map['prodCatalogId'] = data2.prodCatalogId;
				map['productStoreId'] = data2.productStoreId;
				map['sequenceNum'] = data2.sequenceNum;
				map['fromDate'] = data2.fromDate.getTime();
				if(!data2.thruDate){
					map['thruDate'] = data2.fromDate.getTime();
				}else{
					map['thruDate'] = data2.thruDate.getTime();
				}
				cMemberr = map;
			if (cMemberr.length <= 0){
				return false;
			} else {
				cMemberr = JSON.stringify(cMemberr);
				jQuery.ajax({
			        url: 'deleteProductStoreCatalogg',
			        type: 'POST',
			        async: true,
			        data: {
			        		'cMemberr': cMemberr,
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
	
	var init = (function(){
		initWindow();
		eventRightClickMenu();
		eventDeleteProductStoreCatalog();
	});
	
	return{
		init: init,
	}
}());