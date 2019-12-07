var viewTrainingDetailObj = (function(){
	var init = function(){
		initEvent();
		$("#jqxNotificationEditTraining").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerEditTraining"});
	};
	var initEvent = function(){
		$("#sendApprTraining").click(function(event){
			bootbox.dialog(uiLabelMap.SendApprRequestConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		sendApprRequest();   	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonCancel,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	var sendApprRequest = function(){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'sendApprRequestTraining',
			data: {trainingCourseId: globalVar.trainingCourseId},
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					location.reload();
				}else{
					Grid.renderMessage('EditTraining', response._ERROR_MESSAGE_, {autoClose : true,
						template : 'error', appendContainer: "#containerEditTraining", opacity : 0.9});
					Loading.hide('loadingMacro');
				}
			},
			complete: function(jqXHR, textStatus){
				
			}
		});
	};
	return{
		init: init
	}
}());

var viewApprHistoryObj = (function(){
	var init = function(){
		initWindow();
		initEvent();
		$('#listApprHistory').slimScroll({
			height: '250px',
			alwaysVisible : true
		});
	};
	var initWindow = function(){
		createJqxWindow($("#apprHisWindow"), 600, 400);
	};
	var initEvent = function(){
		$("#viewApprHistory").click(function(){
			openJqxWindow($("#apprHisWindow"));
		});
		$("#closeApprHistory").click(function(){
			$("#apprHisWindow").jqxWindow('close');
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	$.jqx.theme = 'olbius';
	$('[data-rel=popover]').popover({html:true});
	viewTrainingDetailObj.init();
	viewApprHistoryObj.init();
});