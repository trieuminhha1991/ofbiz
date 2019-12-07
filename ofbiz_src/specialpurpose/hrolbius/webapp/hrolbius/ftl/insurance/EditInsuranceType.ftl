<div id="${dataToggleModalId}" class="modal hide fade modal-dialog" tabindex="-1">
	<div class="modal-header no-padding ">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.EditInsurance_Title}			
		</div>
	</div>
	<div class="modal-body no-padding">
		<div class="row-fluid">
			<form action="<@ofbizUrl>CreateInsuranceType</@ofbizUrl>" name="EditInsuranceType" id="EditInsuranceType" class="form-horizontal" method="post">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.InsuranceTypeCode}
					</label>			
					<div class="controls">
						<input type="text" name="insuranceTypeId" value="">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.CommonDescription}
					</label>			
					<div class="controls">
						<input type="text" name="description" value="">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeIsCompulsory}
					</label>			
					<div class="controls">
						<select name="isCompulsory">
							<option value="Y">${uiLabelMap.CommonYes}</option>
							<option value="N">${uiLabelMap.CommonNo}</option>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeEmployerRate} (%)
					</label>			
					<div class="controls">
						<input type="text" id="insuranceTypeEmployerRate" name="employerRate" value=""/>
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.InsuranceTypeEmployeeRate} (%)
					</label>			
					<div class="controls">
						<input type="text"  id="insuranceTypeEmployeeRate" name="employeeRate" value="" />
					</div>
				</div>
				
				<div class="control-group no-left-margin ">
					<label>
						&nbsp;
					</label>
					<div class="controls">
						<button type="submit" class="btn btn-small btn-primary icon-ok">
							${uiLabelMap.CommonSubmit}
						</button>
					</div>	
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function() {
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	
	$("#EditInsuranceType").validate({
  		
  		errorElement: 'div',
    	errorClass: "invalid color-red",
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
  		
		rules:{
			insuranceTypeId:{
				required: true,
			},
			employerRate:{
				number:true,
				min:0,
				max:100
			},
			employeeRate:{
				number:true,
				min:0,
				max:100
			}
			
			
		},
		messages:{
			insuranceTypeId:{
				required: "${uiLabelMap.CommonRequired}"
			},
			employerRate:{
				number:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueNumber)}",
				min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
				max:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue1)}"
			},
			employeeRate:{
				number:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueNumber)}",
				min:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue)}",
				max:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValue1)}"
			}
		}
		});
});
  		
function changeValuePercen(value,id){
	var localeScript = "${locale}";
	$("#"+id).val(value.toLocaleNumber(localeScript));
}
</script>