$(function(){
	ctxMenuFacilityExpiryDate.init();
});

var ctxMenuFacilityExpiryDate = (function(){
	var init = (function(){
		initElement();
		initEvent();
	});
	var initElement = function(){
		$("#contextMenu").jqxMenu({ width: 250, height: 60, autoOpenPopup: false, mode: 'popup'});
	};
	var initEvent = function(){
		$("#contextMenu").unbind('itemclick').on('itemclick', function (event) {
			var rowindex = $("#grid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#grid").jqxGrid('getrowdata', rowindex);
	        checkAction(dataRecord, event);
			
		});
		
		function checkAction(dataRecord, event){
			var args = event.args;
			var rowindex = $("#grid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#grid").jqxGrid('getrowdata', rowindex);	  
	        	
	        if($(args).attr("action") == 'edit'){
	        	facilityEdittingId = dataRecord.facilityId;
	        	$("#txtFacilityId").val(dataRecord.facilityId);
	        	$("#txtFacilityName").val(dataRecord.facilityName);
	        	$("#txtFacilityRequireDate").jqxDropDownList('selectItem', dataRecord.requireDate );
	        	openJqxWindow($("#editPopupWindow"));
			}else if($(args).attr("action") == 'refresh'){
				$('#grid').jqxGrid('updatebounddata');
			}
		};
		
		function openJqxWindow(jqxWindowDiv){
			var wtmp = window;
			var tmpwidth = jqxWindowDiv.jqxWindow('width');
			jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			jqxWindowDiv.jqxWindow('open');
		};
	}	
	
	return {
		init: init,
	}
}());