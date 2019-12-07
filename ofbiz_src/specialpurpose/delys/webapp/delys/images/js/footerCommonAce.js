$(function() {
	$('[data-rel=tooltip]').tooltip();
	
	$(".chzn-select").css('width','220px').chosen({allow_single_deselect:true , no_results_text: "No such state!"})
	.on('change', function(){
		$(this).closest('form').validate().element($(this));
	});
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		if(info.step == 1) {
			if(!$('#createQuotation').valid()) {
				return false;
			} else {
				document.getElementById("createQuotation").submit();
			}
		}
	}).on('finished', function(e) {
		document.getElementById("createQuotation").submit();
	});
	
	//documentation : http://docs.jquery.com/Plugins/Validation/validate
	$.mask.definitions['~']='[+-]';
	$('#phone').mask('(999) 999-9999');

	jQuery.validator.addMethod("phone", function (value, element) {
		return this.optional(element) || /^\(\d{3}\) \d{3}\-\d{4}( x\d{1,6})?$/.test(value);
	}, "Enter a valid phone number.");
	
	$('#createQuotation').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			currencyUomId: {
				required: true
			},
			partyRoleTypesApply: {
				required: true
			},
			quotationName: {
				required: true
			}
		},

		messages: {
			currencyUomId: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			},
			partyRoleTypesApply: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			},
			quotationName: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		errorPlacement: function (error, element) {
			if(element.is(':checkbox') || element.is(':radio')) {
				var controls = element.closest('.controls');
				if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
				else error.insertAfter(element.nextAll('.lbl').eq(0));
			} 
			else if(element.is('.chzn-select')) {
				error.insertAfter(element.nextAll('[class*="chzn-container"]').eq(0));
			}
			else error.insertAfter(element);
		},
		submitHandler: function (form) {
			if(!$('#createQuotation').valid()) return false;
		},
		invalidHandler: function (form) {
		}
	});
	})
