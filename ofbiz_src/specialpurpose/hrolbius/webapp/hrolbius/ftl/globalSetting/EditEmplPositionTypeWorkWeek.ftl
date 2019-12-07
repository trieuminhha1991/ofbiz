
<form action="<@ofbizUrl>updateEmplPositionTypeWorkWeek</@ofbizUrl>" id="EmplPositionTypeWorkWeek" method="post" class="basic-form form-horizontal">
	<div class="row-fluid">
		<input type="hidden" name="emplPositionTypeId" value="${emplPositionType.emplPositionTypeId}">
		<div class="control-group no-left-margin">
			<label class="">
				<label class="asterisk">${uiLabelMap.EmplPositionTypeId}</label>  
			</label>
			<div class="controls">
						 &nbsp;
				${emplPositionType.description?if_exists}[${emplPositionType.emplPositionTypeId}]							
			</div>
		</div>
		<#assign filterByDateCond = Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()>
		<#assign condition1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("emplPositionTypeId", emplPositionType.emplPositionTypeId)>
		<#list dayOfWeek as day>
			<div class="control-group no-left-margin">
				<label>
						<label class="asterisk">${day.description}</label>
					<input type="hidden" name="dayOfWeek_o_${day_index}" value="${day.dayOfWeek}">  
				</label>
			
				<div class="controls">
					 
					<#assign condition2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("dayOfWeek", day.dayOfWeek)>
					<#assign conditions = [filterByDateCond, condition1, condition2]>
					<#assign workShiftList = delegator.findList("EmplPositionTypeWorkWeek", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions, Static["org.ofbiz.entity.condition.EntityOperator"].AND), null, null, null, false)>
					<#assign workShiftListStr = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(workShiftList, "workingShiftId", true)> 		 
					<select name="workShiftId_o_${day_index}" multiple="multiple" class="chzn-select" >
						<option>
						<#list workingShift as shift>
							<option value="${shift.workingShiftId}" <#if workShiftListStr?contains(shift.workingShiftId)>selected="selected"</#if>>${shift.description?if_exists}</option>
						</#list>
						
					</select>							
				</div>
			</div>
		</#list>
		<div class="control-group no-left-margin" style="margin-top: 20px;">
			<label class="">
				<label >&nbsp;</label>
			</label>
			<div class="controls">
				 &nbsp;
				<button type="submit" id="submit" class="btn btn-success btn-small">
					<i class="icon-ok" ></i>
					${uiLabelMap.CommonSubmit}
				</button>
			</div>
		</div>
	</div>
</form>
<script type="text/javascript">
	$(document).ready(function(){
		$('#EmplPositionTypeWorkWeek').validate({
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
				workShiftId_o_0: {
					required: true,
				},
				workShiftId_o_1: {
					required: true,
				},
				workShiftId_o_2: {
					required: true,
				},
				workShiftId_o_3: {
					required: true,
				},
				workShiftId_o_4: {
					required: true,
				},
				workShiftId_o_5: {
					required: true,
				},
				workShiftId_o_6: {
					required: true,
				},
			},

			messages: {
				workShiftId_o_0: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_1: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_2: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_3: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_4: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_5: {
					required: "<span style='color:red;'>Bắt buộc</span>",
				},
				workShiftId_o_6: {
					required: "<span style='color:red;'>Bắt buộc</span>",
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
	})
</script>

