$(document).ready(function() {
	openTerminalObject.init();
});

var openTerminalObject = (function(){
	var initJqxWindow = function (){
		$("#openTerminalJqxWindow").jqxWindow({theme: 'olbius',width: 450, height: 130, resizable: false, draggable: false, isModal: true, autoOpen: false,showCloseButton: false,
		modalOpacity: 0.7, showCloseButton: false, keyboardCloseKey: '',
		initContent: function (){
			initInputMaskMoney();
			initRuleInputMaskMoney();
			bindEventInputMaskMoney();
			initJqxButton();
			bindEventJqxButton();
		}});
		$('#openTerminalJqxWindow').jqxWindow('open');
	};
	var initJqxButton = function (){
		$("#cancelButton").jqxButton();
		$("#saveButton").jqxButton();
	};
	var bindEventJqxButton = function(){
		$("#cancelButton").on('click', function () {
			resetOpenterminal();
		});
		$("#saveButton").on('click', function () {
			checkAmountOpenTerminal();
		});
	};
	var bindEventJqxWindow = function (){
		 $('#openTerminalJqxWindow').on('open', function (event) {
	            $("#startingDrawerAmount").focus();
	     });
	};
	var initInputMaskMoney = function(){
		$("#startingDrawerAmount").maskMoney({precision:2,thousands: '.', decimal: ','});
		$("#startingDrawerAmount").maskMoney('mask', 0.0);
	};
	var initRuleInputMaskMoney = function (){
		$("#startingDrawerAmount").keypress(function (e) {
		    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57) && e.which != 13 && e.which != 16) {
		   	 	return false;
		    }else{
		    	$("#startingDrawerAmount").maskMoney('unmasked')[0];
		    }
		});
	};
	var bindEventInputMaskMoney = function (){
		$('#startingDrawerAmount').bind('keypress', function(event) {
	        code = event.keyCode ? event.keyCode : event.which;
	        if (code.toString() == 13) {
	        	checkAmountOpenTerminal();
	            flagPopupOpenTerminal = 1;
	            return ;
	        }
	        if (code.toString() == 27) {
	        	resetOpenterminal();
	            flagPopupOpenTerminal = 1;
	            return;
	        }
	    });
	};
	var  resetOpenterminal = function(){
		$("#startingDrawerAmount").maskMoney('mask', 0.0);
	};
	var checkAmountOpenTerminal = function(){
		var amount = $('#startingDrawerAmount').maskMoney('unmasked')[0];
		if (amount < 0){
			$('#openTerminalJqxWindow').jqxWindow('close');
			bootbox.dialog(BPOSEnterAmount,
				[{
					"label" : CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok",
	    		    "callback": function() {
	    		    	$('#openTerminalJqxWindow').jqxWindow('open');
	    		    }	
				}]		
			);
		} else {
			 openTerminalConfirm();
		}
	};
	var openTerminalConfirm = function (){
		var param = 'startingDrawerAmount=' + $("#startingDrawerAmount").maskMoney('unmasked')[0];
	    $('#openTerminalJqxWindow').jqxWindow('close');
	    bootbox.dialog(BPOSAreYouSure,
				[{
					"label" : CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok",
	    		    "callback": function() {
	    		    	$.ajax({url: 'OpenTerminal',
	    	                data: param,
	    	                type: 'post',
	    	                async: false,
	    	                success: function(data) {
	    	                    getResultOfOpenTerminal(data);
	    	                },
	    	                error: function(data) {
	    	                    getResultOfOpenTerminal(data);
	    	                }
	    	            });
	    		    }	
				},
				{
	    		    "label" : CommonCancel,
	    		    "class" : "btn-danger btn-small icon-remove",
	    		    "callback": function() {
	    		    	bootbox.hideAll();
	    	    		$('#openTerminalJqxWindow').jqxWindow('open');
	    	    		$("#startingDrawerAmount").maskMoney('mask', 0);
	    		    }
	    		}]		
		);
	};
	var init = function(){
		initJqxWindow();
		bindEventJqxWindow();
	};
	return {
		init: init
	};
}());

function getResultOfOpenTerminal(data){
	var serverError = getServerError(data);
    if (serverError != "") {
    	bootbox.hideAll();
		$('#openTerminalJqxWindow').jqxWindow('close');
			bootbox.dialog(serverError,
				[{
					"label" : CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok",
	    		    "callback": function() {
	    		    	window.location="main";
	    		    }	
				}]		
			);
    } else {
		$('#openTerminalJqxWindow').jqxWindow('close');
		$("#startingDrawerAmount").maskMoney('unmasked')[0];
		window.location="main";
    }
}

function getServerError(data){
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