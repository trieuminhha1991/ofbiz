var viewTrainingListObj = (function(){
	var init = function(){
		initContextMenu();
	};
	var initContextMenu = function(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement; 
		$("#contextMenu").jqxMenu({ width: 250, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        var action = $(args).attr("action");
	        if(action == "trainingCourseDetailPlan"){
	        	if (dataRecord.statusId == 'TRAINING_SUMMARY' || dataRecord.statusId == 'TRAINING_COMPLETED') {
	        		location.href = "ViewTrainingSummary?trainingCourseId=" + dataRecord.trainingCourseId;
	        	} else {
	        		location.href = "ViewTrainingDetail?trainingCourseId=" + dataRecord.trainingCourseId;
	        	}
	        }else if(action == "viewListEmplRegister"){
	        	location.href = "ViewListEmplRegisterTraining?trainingCourseId=" + dataRecord.trainingCourseId;
	        }else if(action == "skillTraining"){
	        	
	        }
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	viewTrainingListObj.init();
});