$(function(){
	orderCommon.init();
});
var orderCommon = (function(){
	var init = function() {
		initEvent();
	};
	var initEvent = function(){
		// Active tab by parameter
	    /*var query = window.location.search.substring(1);
	    var vars = query.split("&");
	    for (var i=0;i<vars.length;i++) {
	        var pair = vars[i].split("=");
	        if(pair[0] == 'activeTab'){
	            // remove current active tab
	            var tmpStr = $("li.active a[data-toggle='tab']").attr("href");
	            $(tmpStr).removeClass('active');
	            $("li.active a[data-toggle='tab']").parent().removeAttr('class');
	            // Active
	            $('#' + pair[1]).addClass('active');
	            $("a[href='#" + pair[1] + "']").parent().addClass("active");
	            break;
	        }
	    }*/
	    $('.nav.nav-tabs li').on('click', function(){
	    	// clear parameter
	    	var thisHref = location.href;
	    	var queryParam = thisHref.split("?");
	    	var newHref = "";
	    	if (queryParam != null && queryParam != undefined) {
	    		newHref = queryParam[0] + "?";
	    	}
	    	var isAdded = false;
	    	if (queryParam.length > 1) {
	    		var varsParam = queryParam[1].split("&");
			    for (var i = 0; i < varsParam.length; i++) {
			        var pairParam = varsParam[i].split("=");
			        if(pairParam[0] != 'activeTab'){
			        	if (isAdded) newHref += "&";
			        	newHref += varsParam[i];
			        	isAdded = true;
			        }
			    }
	    	}
	    	var tabObj = $(this).find("a[data-toggle=tab]");
	    	if (tabObj != null && tabObj != undefined) {
	    		var tabHref = tabObj.attr("href");
	    		if (tabHref.indexOf("#") == 0) {
	    			var tabId = tabHref.substring(1);
	    			window.history.pushState({}, "", newHref + '&activeTab=' + tabId);
	    		}
	    	}
	    });
	};
	var markOrderViewed = function(){
	    jQuery.ajax({
	        url: 'markOrderViewed',
	        type: "POST",
	        data: jQuery('#orderViewed').serialize(),
	        success: function(data) {
	        	var callback = function(){
	        		$('#checkViewed').attr("checked", false);
		            jQuery("#isViewed").fadeOut('fast');
		            jQuery("#viewed").fadeIn('fast');
	        	};
	        	processResult(data, callback);
	        }
	    });
	};
	var markOrderUnViewed = function(){
		jQuery.ajax({
	        url: 'markOrderUnViewed',
	        type: "POST",
	        data: jQuery('#orderUnViewed').serialize(),
	        success: function(data) {
	        	var callback = function(){
	        		$('#checkViewed').attr("checked", false);
		            jQuery("#viewed").fadeOut('fast');
		            jQuery("#isViewed").fadeIn('fast');
	        	};
	        	processResult(data, callback);
	        }
	    });
	};
	var openEditPriority = function(){
		$("#setOrderReservationPriority").css("display", "block");
		$("#spanOrderReservationPriority").css("display", "none");
	}
	var cancelEditPriority = function(){
		$("#setOrderReservationPriority").css("display", "none");
		$("#spanOrderReservationPriority").css("display", "block");
	}
	var saveEditPriority = function(priorityData){
		$("#btnSavePriority").addClass("disabled");
		$("#btnCancelSavePriority").addClass("disabled");
		var data = $("#setOrderReservationPriority").serialize();
		var url = "setOrderReservationPriorityAjax";
		$.ajax({
            type: "POST", 
            url: url,
            data: data,
            beforeSend: function () {
				$("#info_loader").show();
			}, 
            success: function (data) {
            	var callback = function (){
            		if (data.priority != null) {
    	        		var priority = data.priority;
    	        		var priorityDisplay = "";
    	        		if (typeof(priorityData) != "undefined") {
    	        			for (var i = 0; i < priorityData.length; i++) {
    	        				var priorityItem = priorityData[i];
    	        				if (priority == priorityItem.enumId) {
    	        					priorityDisplay = priorityItem.description;
    	        				}
    	        			}
    	        		}
    	        		$("#spanOrderReservationPriority > span").html(priorityDisplay);
    	        	}
    	        	cancelEditPriority();
            	}
            	processResult(data, callback);
            },
            error: function () {},
            complete: function() {
		        $("#info_loader").hide();
		        $("#btnSavePriority").removeClass("disabled");
		        $("#btnCancelSavePriority").removeClass("disabled");
		    }
        });
	};
	var processResult = function(data, callback){
		if (data.thisRequestUri == "json") {
    		var errorMessage = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>";
    		var isError = false;
	        if (data._ERROR_MESSAGE_LIST_ != null) {
	        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
	        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
	        		isError = true;
	        	}
	        }
	        if (data._ERROR_MESSAGE_ != null) {
	        	errorMessage = data._ERROR_MESSAGE_;
	        	isError = true;
	        }
	        errorMessage += "</span>";
	        if (isError) {
				bootbox.dialog(errorMessage, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
					}]
				);
	        } else {
	        	callback();
	        }
    	}
	};
	return {
		init: init,
		markOrderViewed: markOrderViewed,
		markOrderUnViewed: markOrderUnViewed,
		openEditPriority: openEditPriority,
		cancelEditPriority: cancelEditPriority,
		saveEditPriority: saveEditPriority
	}
}());
