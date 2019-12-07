<div id="${dataToggleModalId}" class="modal hide fade modal-dialog" tabindex="-1">
	<div class="modal-header no-padding ">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.AddInsurancePayment}			
		</div>
	</div>
	<div class="modal-body no-padding">
		<div class="row-fluid">
			<#assign currentYear = currentDateTime.get(Static["java.util.Calendar"].YEAR)>
			<#assign currentMonth = currentDateTime.get(Static["java.util.Calendar"].MONTH) + 1>
			<form action="createInsurancePayment" class="basic-form form-horizontal" method="post">
				<div class="control-group no-left-margin">
					<label class="">
						<label for="insurancePaymentName" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.InsurancePaymentName}</label>  
					</label>
					<div class="controls">
						<input type="text" name="insurancePaymentName" id="insurancePaymentName">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="">
						<label for="InsuranceTypeId" class="asterisk" id="InsuranceType_title">${uiLabelMap.InsuranceType}</label>
					</label>
					<div class="controls">
						<select name="insuranceTypeId" id="InsuranceTypeId">
							<#list insuranceTypeList as insuranceType>
								<option value="${insuranceType.insuranceTypeId}">${insuranceType.description?if_exists}</option>
							</#list>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin ">
					<label class="">
						<label for="reportDate" class="asterisk" id="InsuranceReportName_title">${uiLabelMap.DatePayment}</label>  
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
				<div class="control-group no-left-margin">
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
	});
</script>