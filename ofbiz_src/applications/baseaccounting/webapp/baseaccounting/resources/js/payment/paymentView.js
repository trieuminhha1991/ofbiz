var paymentViewObj = (function(){
	var init = function(){
		var action = "";
		if(globalVar.businessType == "AP"){
			action = 'setAPPaymentStatus';
		}else if(globalVar.businessType == "AR"){
			action = 'setARPaymentStatus';
		}
		$("form[action='" + action + "']").submit(function(event){
			event.preventDefault(); // Prevent the form from submitting via the browser
		    var form = $(this);
		    Loading.show('loadingMacro');
		    $.ajax({
		        type: form.attr('method'),
		        url: form.attr('action'),
		        data: form.serialize()
		      }).done(function(response) {
		    	  if(response.responseMessage == "error"){
		    		  Loading.hide('loadingMacro');
		    		  bootbox.dialog(response.errorMessage,
								[
								{
									"label": uiLabelMap.CommonClose,
									"class": "btn-danger btn-small icon-remove open-sans",
								}]		
						);
		    		  return;
		    	  }
		    	  location.reload(); 
		      }).fail(function(data) {
		    	  Loading.hide('loadingMacro');
		      });
		});
	};
	var setPaymentStatus = function(formName, warningMessage){
		bootbox.dialog(warningMessage,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						$("form[name='" + formName + "']").submit();
					}	
				},
				{
					"label" : uiLabelMap.CommonCancel,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}]		
		);
	};
	return{
		init: init,
		setPaymentStatus: setPaymentStatus
	}
}());
$(document).ready(function () {
	$.jqx.theme = 'olbius';
	paymentViewObj.init();
});