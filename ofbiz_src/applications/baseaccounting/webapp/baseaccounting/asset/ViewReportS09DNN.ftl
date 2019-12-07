<div id="S09DNNWindow" class="hide">
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
							<div id="monthQuarter" style="display: inline-block; float: left;"></div>
							<div id="year" style="float: left; margin-left: 5px !important"></div>	
				   		</div>
					</div>
					<div class="row-fluid">
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCFormat}</label>
						</div>
						<div class="span8">
							<div id="pdfFormat" style="display: inline-block; float: left; margin: 7px 0 0 -3px !important;"><span style="font-size: 14px">${StringUtil.wrapString(uiLabelMap.BACCPdf)}</span></div>
							<div id="excelFormat" style="display: inline-block; float: left; margin:7px 0 0 0 !important; "><span style="font-size: 14px">${StringUtil.wrapString(uiLabelMap.BACCExcel)}</span></div>
				   		</div>
					</div>	
					<div class="row-fluid">
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCFixedAssetTypeShort}</label>
						</div>
						<div class="span8">
							<div id="fixedAssetType"></div>
							<div id="checkAllType" style="margin: 5px 0 0 -3px !important">${StringUtil.wrapString(uiLabelMap.CommonSelectAll)}</div>
				   		</div>
					</div>	
					
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelFixedAssetReportS09">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveFixedAssetReportS09">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/fixedAsset/ViewReportS09DNN.js?v=0.0.2"></script>