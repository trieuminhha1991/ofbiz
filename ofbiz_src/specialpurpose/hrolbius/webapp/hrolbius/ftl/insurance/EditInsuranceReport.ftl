<div id="${dataToggleModalId}" class="modal hide fade modal-dialog" tabindex="-1">
	<div class="modal-header no-padding ">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.AddInsuranceReport}			
		</div>
	</div>
	<div class="modal-body no-padding">
		<div class="row-fluid">
			<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
			<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
			<form action="createInsuranceReport" class="basic-form form-horizontal" method="post" id="InsuranceReport">
		
				<div class="control-group no-left-margin ">
					<label class="">
						<label for="reportName" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.InsuranceReportName}</label>  
					</label>
					<div class="controls">
						<input type="text" name="reportName" id="reportName">
					</div>
				</div>
				<div class="control-group no-left-margin ">
					<label class="">
						<label for="reportDate" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.DateReport}</label>  
					</label>
					<div class="controls">
						<div class="row-fluid">
							<div class="span12">
								<div class="row-fluid">
									<div class="span2" style="margin: 0; padding: 0">
										<label style="display: inline;">${uiLabelMap.CommonMonth}</label>
									</div>
									<div class="span3" style="margin: 0">
										<input type="text" style="margin-bottom:0px;" name="month" class="input-mini" id="month" />
									</div>
									
									<div class="span2" style="margin: 0; padding: 0">
										<label style="display: inline;">${uiLabelMap.CommonYear}</label>
									</div>
									<div class="span3" style="margin: 0">
										<input type="text" style="margin-bottom:0px;"class="input-mini" id="year" name="year"/>
									</div>
								</div>		
							</div>
						</div>
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
	<#assign minYear = currentYear-10>
	<#assign maxYear = currentYear+10>
	$(function() {
		jQuery('#month').ace_spinner({value:${currentMonth},min:1,max:12,step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
		jQuery('#year').ace_spinner({value:${currentYear},min:${minYear},max:${maxYear},step:1, icon_up:'icon-caret-up', icon_down:'icon-caret-down'});
		
		jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
		jQuery("#${createNewLinkId}").attr("role", "button");
		jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
		
		$('#InsuranceReport').validate({
			errorElement: 'span',
			errorClass: 'help-inline',
			focusInvalid: false,
			rules: {
				reportName: {
					required: true,
					validateId: true
				}			
			},

			messages: {
				reportName: {
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