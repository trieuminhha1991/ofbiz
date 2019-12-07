<div id="S11DNNWindow" class="hide">
	<div>${uiLabelMap.BACCReportParameters}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class="row-fluid">
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.ReportingPeriod}</label>
					</div>
					<div class="span8">
						<div id="monthQuarterS11" style="display: inline-block; float: left;"></div>
						<div id="yearS11" style="float: left; margin-left: 5px !important"></div>	
			   		</div>
				</div>
				<div class="row-fluid">
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.BACCFixedAsset}</label>
					</div>
					<div class="span8">
						<div id="fixedAssetDropDown">
							<div id="fixedAssetGrid"></div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelFixedAssetReportS11">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveFixedAssetReportS11">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/fixedAsset/ViewReportS11DNN.js?v=0.0.1"></script>