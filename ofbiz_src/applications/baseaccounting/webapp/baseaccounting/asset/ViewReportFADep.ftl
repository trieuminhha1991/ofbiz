<div id="faDepReportWindow" class="hide">
	<div>${uiLabelMap.BACCReportParameters}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class="row-fluid">
						<div class='span4'>
							<label class=''>${uiLabelMap.ReportingPeriod}</label>
						</div>
						<div class="span8">
							<div id="monthFADep" style="display: inline-block; float: left;"></div>
							<div id="yearFADep" style="float: left; margin-left: 5px !important"></div>	
				   		</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelFADepReport">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveFADepReport">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/fixedAsset/ViewReportFADep.js?v=0.0.1"></script>