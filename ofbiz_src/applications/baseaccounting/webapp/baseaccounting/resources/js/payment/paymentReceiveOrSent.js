var paymentReceiveOrSentObj = (function(){
	var _paymentId;
	var _businessType;
	var init = function(){
		initDropDown();
		initWindow();
		initEvent();
	};
	var initDropDown = function(){
		accutils.createJqxDropDownList($("#quickPaymentMethodList"), paymentMethodData, {width: '98%', height: 25, valueMember: 'paymentMethodId', displayMember: 'description'});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#quickEditPaymMethodWindow"), 450, 140);
	};
	var setPaymentReceiveOrSent = function(paymentId, businessType, actionDesc){
		_paymentId = paymentId;
		_businessType = businessType;
		var warningMsg = '<div ><div class="row-fluid"><div class="span12" style="font-size : 13px;"><span style="color : #037c07;font-weight :bold;"><i class="fa-hand-o-right"></i>&nbsp;' 
									+ uiLabelMap.accConfirms +'</span> <span style="color:red;font-weight : bold;">' 
									+ actionDesc + '&nbsp; <span style="color : #037c07;font-weight :bold;">' 
									+ uiLabelMap.accThisPayment  +'</span></span></div></div></div>';
		bootbox.dialog(warningMsg,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						checkPaymentMethodExists(paymentId, businessType);
					}	
				},
				{
					"label" : uiLabelMap.CommonCancel,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}]		
		);
	};
	var checkPaymentMethodExists = function(paymentId, businessType){
		Loading.show('loadingMacro');
		$.ajax({
			url: 'getPaymentMethodOfPayment',
			type:'POST',
			data: {paymentId: paymentId},
			success: function(response){
				if(response.responseMessage == "success"){
					if(response.paymentMethodId){
						setPaymentStatus(paymentId, businessType);
					}else{
						Loading.hide('loadingMacro');
						accutils.openJqxWindow($("#quickEditPaymMethodWindow"));
					}
				}else{
					Loading.hide('loadingMacro');
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete:  function(jqXHR, textStatus){
				
			}
		});
	};
	var setPaymentStatus = function(paymentId, businessType){
		if(businessType == "AP"){
			$("form[name='PaymentSent']").submit();
		}else if(businessType == "AR"){
			$("form[name='PaymentReceive']").submit();
		}
		
	};
	var initEvent = function(){
		$("#quickEditPaymMethodWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#quickEditPaymMethodWindow").on('open', function(event){
			$("#quickPaymentMethodList").jqxDropDownList({selectedIndex: 0});
		});
		
		$("#cancelQuickEditPaymentMethod").click(function(event){
			$("#quickEditPaymMethodWindow").jqxWindow('close');
		});
		$("#saveQuickEditPaymentMethod").click(function(event){
			$("#saveQuickEditPaymentMethod").attr("disabled", "disabled");
			$("#cancelQuickEditPaymentMethod").attr("disabled", "disabled");
			Loading.show('loadingMacro');
			$.ajax({
				url: 'updatePaymentMethod',
				type:'POST',
				data: {paymentId: _paymentId, paymentMethodId: $("#quickPaymentMethodList").val()},
				success: function(response){
					if(response._ERROR_MESSAGE_LIST_ || response._ERROR_MESSAGE_){
						var errorMessage = typeof(response._ERROR_MESSAGE_LIST_) != "undefined"? response._ERROR_MESSAGE_LIST_[0]: response._ERROR_MESSAGE_;
						Loading.hide('loadingMacro');
						$("#saveQuickEditPaymentMethod").removeAttr("disabled");
						$("#cancelQuickEditPaymentMethod").removeAttr("disabled");
						bootbox.dialog(errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);
					}else{
						setPaymentStatus(_paymentId, _businessType);
					}
				},
				complete:  function(jqXHR, textStatus){
					
				}
			});
		});
	};
	return{
		init: init,
		setPaymentReceiveOrSent: setPaymentReceiveOrSent
	}
}());

$(document).ready(function () {
	$.jqx.theme = 'olbius';
	paymentReceiveOrSentObj.init();
});
