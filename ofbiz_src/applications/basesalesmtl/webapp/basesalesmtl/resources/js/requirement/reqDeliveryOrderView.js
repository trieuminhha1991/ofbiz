$(function(){
	OlbReqDlvOrdInfo.init();
});
var OlbReqDlvOrdInfo = (function(){
	var init = function(){
		initElement();
		initEvent();
	};
	var initElement = function(){
	};
	var initEvent = function(){
	};
	var approveRequirementDelivery = function approveRequirementDelivery(requirementId){
		bootbox.dialog(uiLabelMap.AreYouSureAccept, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
					$.ajax({
						type: 'POST',
						url: 'changeRequirementStatus',
						async: false,
						data: {
							requirementId: requirementId,
							statusId: "REQ_APPROVED",
						},
						success: function(data){
						},
					}).done(function(data) {
						location.reload();
					});
		    	Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);
	};

	var cancelRequirementDelivery = function cancelRequirementDelivery(requirementId){
    		bootbox.dialog(uiLabelMap.AreYouSureAccept,
    		[{"label": uiLabelMap.CommonCancel,
    			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
    		    "callback": function() {bootbox.hideAll();}
    		},
    		{"label": uiLabelMap.OK,
    		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
    		    "callback": function() {
    		    	Loading.show('loadingMacro');
    		    	setTimeout(function(){
    					$.ajax({
    						type: 'POST',
    						url: 'changeRequirementStatus',
    						async: false,
    						data: {
    							requirementId: requirementId,
    							statusId: "REQ_CANCELLED",
    						},
    						success: function(data){
    						},
    					}).done(function(data) {
    						location.reload();
    					});
    		    	Loading.hide('loadingMacro');
    		    	}, 500);
    		    }
    		}]);
    	};

	return {
		init: init,
		approveRequirementDelivery: approveRequirementDelivery,
		cancelRequirementDelivery: cancelRequirementDelivery
	};
}());