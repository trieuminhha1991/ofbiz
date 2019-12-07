$(function(){
	TripDetailBeginObj.init();
});
var TripDetailBeginObj = (function() {
    var btnClick = false;
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
	}
	var approveShippingTrip = function(shippingTripId){
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
			    		url: "changeShippingTripStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			shippingTripId: shippingTripId,
			    			statusId: "TRIP_CONFIRMED"
			    		},
			    		success: function (data){
			    			location.reload();
			    		},
							error: function (data) {
								alert("Error");
							}
			    	});
				Loading.hide('loadingMacro');
            	}, 500);
		    }
		}]);
	};

    var quickApproveAndExportShippingTrip = function(shippingTripId){
        bootbox.dialog(uiLabelMap.AreYouSureApproveAndExport,
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
                                url: "quickApproveAndExportShippingTrip",
                                type: "POST",
                                async: false,
                                data: {
                                    shippingTripId: shippingTripId
                                },
                                success: function (data){
                                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                        $('#container').empty();
                                        $('#jqxNotification').jqxNotification({ template: 'error'});
                                        //$("#jqxNotification").html(uiLabelMap.BLDErrorExportShippingTrip);
                                        $("#jqxNotification").html(errorMessage);
                                        $("#jqxNotification").jqxNotification("open");
                                        return false;
                                    }, function(){
                                        location.reload();
                                    });

                                },
                                error: function (data) {
                                    alert("Error");
                                }
                            });
                            Loading.hide('loadingMacro');
                        }, 500);
                    }
                }]);
    }

	var initValidateForm = function(){
	};
	
	var cancelShippingTrip = function (shippingTripId){
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
			    		url: "changeShippingTripStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			shippingTripId: shippingTripId,
			    			statusId: "TRIP_CANCELLED"
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
	
	var prepareRejectTrip = function (){
	};
	
	var editShippingTrip = function(shippingTripId){
		window.location.href = "prepareEditShippingTrip?shippingTripId="+shippingTripId;
	};
	var updateShippingTripStatus = function(shippingTripId){
		window.location.href = "prepareUpdateShippingTripStatus?shippingTripId="+shippingTripId;
	};
    var prepareUpdateExportedDeliveryInTrip = function(shippingTripId){
        var mess = uiLabelMap.AreYouSureExport;
        jOlbUtil.confirm.dialog(mess, function () {
            if (!btnClick) {
                Loading.show('loadingMacro');
                setTimeout(function () {
                    $.ajax({
                        url: "exportDeliveryInTrip",
                        type: "POST",
                        async: false,
                        data: {
                            shippingTripId: shippingTripId
                        },
                        success: function (data) {
                            location.reload();
                        },
                        error: function (data) {
                            alert("Send request is error");
                        }
                    });

                }, 500);
                btnClick = true;
            }
        }, uiLabelMap.CommonCancel, uiLabelMap.OK, function () {
            btnClick = false;
        });
    };
    var exportPDFDeliveryDocument = function(shippingTripId){
        window.location.href = "shippingTripDeliveryAndExport.pdf?shippingTripId="+shippingTripId;
    };
    var exportPDFStockOutDocument = function(shippingTripId){
        window.location.href = "exportPDFStockOutDocument?shippingTripId="+shippingTripId;
    };
	
	return {
		init: init,
		approveShippingTrip: approveShippingTrip,
		cancelShippingTrip: cancelShippingTrip,
        quickApproveAndExportShippingTrip:quickApproveAndExportShippingTrip,
		prepareRejectTrip: prepareRejectTrip,
        updateShippingTripStatus:updateShippingTripStatus,
		editShippingTrip: editShippingTrip,
        prepareUpdateExportedDeliveryInTrip : prepareUpdateExportedDeliveryInTrip,
        exportPDFDeliveryDocument : exportPDFDeliveryDocument,
        exportPDFStockOutDocument : exportPDFStockOutDocument,
	}
}());