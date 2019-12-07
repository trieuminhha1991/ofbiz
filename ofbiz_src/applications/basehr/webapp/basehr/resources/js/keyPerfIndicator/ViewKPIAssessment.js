var contextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenu", 30, 240);
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if("emplListInAssessment" == action){
            	emplListInKPIAssessment.setData(dataRecord);
            	if(globalVar.updatePermission){
            		addEmplToAssessmentKPIObj.setData(dataRecord);//addEmplToAssessmentKPIObj is defined in EditEmplInKPIAssessment.js
            	}
            	emplListInKPIAssessment.openWindow();//emplListInKPIAssessment is defined in ViewEmplInKPIAssessment.js
            }
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	contextMenuObj.init();
});