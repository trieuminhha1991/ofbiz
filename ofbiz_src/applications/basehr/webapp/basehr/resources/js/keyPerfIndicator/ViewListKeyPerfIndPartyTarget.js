var viewListKPITargetObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "viewDetail"){
            	window.location.href = "ViewKeyPerfIndPartyTargetDetail?partyTargetId=" + dataRecord.partyTargetId;
            }
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	viewListKPITargetObj.init();
});