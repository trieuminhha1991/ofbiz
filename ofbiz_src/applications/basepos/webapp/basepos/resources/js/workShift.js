$(document).ready(function(){
	$("#actualCash").maskMoney({precision:2,thousands: '.', decimal: ','});
	$('#actualCash').keydown(function(event) {
		code = event.keyCode ? event.keyCode : event.which;
		//13 is code of enter key
		if(code == 13){
			processTakeMoneyFromEmployee();
		}
	});
	
	$('#takeMoneyFromEmployee').jqxWindow({
		width: 400, height: 150, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
	});
	
	$('#takeMoneyFromEmployee').on('open', function (event) {
		 $("#actualCash").focus();
	});
});

var posTerminalStateId;
function takeMoneyFromEmployee(row){
	var data = $('#jqxgridListWorkShift').jqxGrid('getrowdata', row);
	posTerminalStateId = data.posTerminalStateId;
	if(data.closedDate){
		if(data.actualReceivedAmount){
			bootbox.dialog(BPOSWorkShiftIsReceivedMoney, [{
                "label" : BPOSOK,
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else {
			$("#actualCash").maskMoney('mask', 0.0);
			$('#takeMoneyFromEmployee').jqxWindow('open');
		}
	} else {
		bootbox.dialog(BPOSWorkShiftNotFinish, [{
            "label" : BPOSOK,
            "class" : "btn btn-primary standard-bootbox-bt",
            "icon" : "fa fa-check",
            }]
        );
	}
}

function cancelTakeMoneyFromEmployee(){
	$("#actualCash").maskMoney('mask', 0.0);
}

function processTakeMoneyFromEmployee(){
	bootbox.hideAll();
	$('#takeMoneyFromEmployee').jqxWindow('close');
	bootbox.dialog(BPOSAreYouSureTakeMoneyFromEmployee, [{
        "label"   : BPOSCancel,
        "icon"    : 'fa fa-remove',
        "class"   : 'btn  btn-danger form-action-button pull-right',
        "callback": function() {
        	$("#actualCash").maskMoney('mask', 0.0);
			$('#takeMoneyFromEmployee').jqxWindow('open');
        }
    }, {
        "label"   : BPOSOK,
        "icon"    : 'fa-check',
        "class"   : 'btn btn-primary form-action-button pull-right',
        "callback": function() {
        	var actualCash = $("#actualCash").maskMoney('unmasked')[0];
			actualCash = parseFloat(actualCash);
			actualCash = Math.round(actualCash);
			if(actualCash && actualCash >0){
				var param = "actualReceivedAmount=" + actualCash  + "&posTerminalStateId=" + posTerminalStateId;
				$.ajax({url: 'takeMoneyFromEmployee',
					data: param,
				    type: 'post',
				    async: false,
				    success: function(data) {
				    	getResultOfTakeMoneyFromEmployee(data);
				    },
				    error: function(data) {
				    	getResultOfTakeMoneyFromEmployee(data);
				    }
				});
			} else {
				 bootbox.hideAll();
				 $('#takeMoneyFromEmployee').jqxWindow('close');
				 bootbox.dialog(BPOSAmountIsNotValid, [{
			            "label" : BPOSOK,
			            "class" : "btn btn-primary standard-bootbox-bt",
			            "icon" : "fa fa-check",
			            }]
			     );
			 }
       	}
    }]);
}

function getResultOfTakeMoneyFromEmployee(data){
	var serverError = getServerError(data);
    if (serverError != "") {
    	$('#takeMoneyFromEmployee').jqxWindow('close');
    	bootbox.dialog(serverError, [{
            "label" : BPOSOK,
            "class" : "btn btn-primary standard-bootbox-bt",
            "icon" : "fa fa-check",
            }]
        );
    } else {
    	$('#jqxNotification').jqxNotification('closeLast');
    	$("#jqxNotification").jqxNotification({ template: 'info'});
        $("#notificationContent").text(BPOSTakeMoneySuccess);
        $('#jqxNotification').jqxNotification('open');
    	$('#jqxgridListWorkShift').jqxGrid('updatebounddata');
    }
}

//Check server side error
function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
        serverErrorHash = data._ERROR_MESSAGE_LIST_;
        $.each(serverErrorHash, function(i, error) {
          if (error != undefined) {
              if (error.message != undefined) {
                  serverError += error.message;
              } else {
                  serverError += error;
              }
            }
        });
    }
    if (data._ERROR_MESSAGE_ != undefined) {
        serverError = data._ERROR_MESSAGE_;
    }
    return serverError;
}