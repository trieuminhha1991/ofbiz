<div id="addInsuranceReportWindow" class="hide">
	<div>${uiLabelMap.AddInsuranceParticipateReport}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonTime}</label>
				</div>
				<div class="span8">
					<div style="display: inline-block; margin-right: 5px" id="monthReport"></div>						
					<div style="display: inline-block;" id="yearReport" ></div> 	
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.InsuranceReportName}</label>
				</div>
				<div class="span8">
					<input type="text" id="reportName">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddReport" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddReport"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAddReport" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddReport">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/insurance/AddInsuranceParticipateReport.js"></script>