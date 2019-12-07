<!-- review -->
<div id="excelExportWindow" class="hide">
	<div>${uiLabelMap.ExportExcel}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.HRReportTemplate}</label>
				</div>
				<div class="span8">
					<label>${uiLabelMap.InsuranceD02TS}</label>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.PeriodDeclaration}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="monthCustomTimeExcel"></div>
							</div>
							<div class="span6">
								<div id="yearCustomTimeExcel"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.TimesDeclaration}</label>
				</div>
				<div class="span8">
					<div id="sequenceNumInsDecl"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingExcel" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerExcel"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelExcelExport" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveExcelExport">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<script type="text/javascript" src="/hrresources/js/insurance/ExportD02TSExcel.js"></script>