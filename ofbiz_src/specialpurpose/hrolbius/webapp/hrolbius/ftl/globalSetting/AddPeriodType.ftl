<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.HrOlbiusAddPeriodType}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<form action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" id="${formId}" class="form-horizontal" method="post">
			<input type="hidden" name="uomId" value="TF_hr" id="AddPeriodType_uomId">
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				
					<div class="controls">
						<input type="text" name="periodTypeId" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonDescription}</label>
					<div class="controls">
						<input type="text" name="description" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.HrolbiusPeriodLength}</label>
					<div class="controls">
						<input type="text" name="periodLength" class="required">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">
						&nbsp;  
					</label>
					<div class="controls">
						<button class="btn btn-small btn-primary"  type="submit">
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</div>					
		</form>
	</div>
</div>
<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	$('#${formId}').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		focusInvalid: false,
		rules: {
			periodTypeId: {
				required: true,
			},
			periodLength: {
				required: true,
				min: 0,
				number: true
			},
			description: {
				required: true,
			}
			
		},

		messages: {
			periodTypeId: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},
			periodLength: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				min: "${uiLabelMap.HrolbiusRequiredValue}",
				number:"${uiLabelMap.HrolbiusRequiredValue}"
			},
			description: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},			
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

		submitHandler: function (form) {
			form.submit();
		},
		invalidHandler: function (form) {
		}
		
	});
});
</script>