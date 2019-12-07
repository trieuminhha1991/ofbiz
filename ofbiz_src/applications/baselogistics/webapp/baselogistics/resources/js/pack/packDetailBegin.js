$(function(){
	PackDetailBeginObj.init();
});
var PackDetailBeginObj = (function() {
	var init = function() {
		var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		/*$('.nav.nav-tabs li').on('click', function(){
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
	    });*/
	};
	
	function approvePack(packId){
		bootbox.dialog(uiLabelMap.AreYouSureApprove, 
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
			    		url: "changePackStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			packId: packId,
			    			statusId: "TRANSFER_APPROVED",
			    			setItemStatus: "Y",
			    			newItemStatus: "TRANS_ITEM_APPROVED",
			    		},
			    		success: function (res){
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	}
	
	var initValidateForm = function(){
		/*var mapNoteRules = [
            {input: '#note', type: 'validInputNotNull'},
		];
		noteValidate = new OlbValidator($('#notePack'), mapNoteRules, null, {position: 'right'});*/
	};
	
	var cancelPack = function (packId){
		bootbox.dialog(uiLabelMap.AreYouSureCancel, 
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
			    		url: "changePackStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			packId: packId,
			    			statusId: "PACK_CANCELLED"
			    		},
			    		success: function (res){
			    			location.reload();
			    		}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};
	
	var prepareRejectPack = function (){
		/*$("#notePack").jqxWindow("open");*/
	};
	
	var editPack = function(packId){
		window.location.href = "editPack?packId="+packId
	}
	
	return {
		init: init,
		approvePack: approvePack,
		cancelPack: cancelPack,
		prepareRejectPack: prepareRejectPack,
		editPack: editPack,
	}
}());