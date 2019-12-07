<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.HROlbiusNewPerfReviewItemType}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<form action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" id="${formId}" class="form-horizontal" method="post">
			<input type="hidden" name="hasTable" value="N">				
			<div class="row-fluid">



				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				
					<div class="controls">
						<input type="text" name="perfReviewItemTypeId" class="required">
					</div>
				</div>



				
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.CommonDescription}</label>
					<div class="controls">
						<input type="text" name="description">
					</div>
				</div>


									
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.EmplPositionTypeId}</label>
					<div class="controls">
						<select name="emplPositionTypeId">
							<#list emplPositionTypes as posType>
								<option value="${posType.emplPositionTypeId}">${posType.description}</option>
							</#list>
						</select>
					</div>
				</div>



				
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.jobWeight}</label>
					<div class="controls">
						<input type="text" name="weight">
					</div>
				</div>



				
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonFromDate}</label>
					<div class="controls">
						<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</div>
				</div>




				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					<div class="controls">
						<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="thruDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
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
	jQuery.validator.addMethod("greaterThan", function(value, element, params){
		//var fromDate = Date.parseExact(value,"yyyy-MM-dd");
		if (value){
			return Date.parseExact(value,"yyyy-MM-dd") >= Date.parseExact($(params).val(),"yyyy-MM-dd");
		} else{ 
			return true;
		}	
	}, 'Must be greater than');
	
	$('#${formId}').validate({
		ignore : [],
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
			perfReviewItemTypeId: {
				required: true,
			},
			weight: {
				required: true,
				number: true,
				min: 0,
				max: 100
			},
			fromDate: {
				required: true,
			},
			thruDate:{
				greaterThan: "#fromDate"
			}
		},

		messages: {
			perfReviewItemTypeId: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},
			weight: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
				number:"${uiLabelMap.RequiredValueIsNumber} ${uiLabelMap.CommonFrom} 0 ${uiLabelMap.CommonTo} 100",
				min: "${uiLabelMap.HrolbiusRequiredValue}",
				max: "${uiLabelMap.LessThanValue} 100"
			},
			description: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},
			thruDate:{
				greaterThan: "${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}"
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

		submitHandler: function (form) {
			form.submit();
		},
		invalidHandler: function (form) {
		}
		
	});
});
</script>