<div id="createDeductionFormulaWindow" class="hide">
	<div>${uiLabelMap.AddNewIncomeDeduction}</div>
	<div class="form-window-container">
		<div class="form-window-content" style="position: relative;">
			<form id="createNewFormulaForm">
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">${uiLabelMap.formulaCode}</label>
					</div>
					<div class="span8">
						<input type="text" id="newFormulaCode">
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">${uiLabelMap.formulaName}</label>
					</div>
					<div class="span8">
						<input type="text" id="newFormulaName">
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonCharacteristic}</label>
					</div>
					<div class="span8">
						<div id="newPayrollCharacId"></div>
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.HRCommonQuota}</label>
					</div>
					<div class="span8">
						<input type="text" id="maxValue">
						<button id="maxValueFormulaBtn" type="button" class="btn btn-success btn-mini"><i class="icon-only fa-calculator"></i></button>								
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">${uiLabelMap.AmountOrFormula}</label>
					</div>
					<div class="span8">
						<input type="text" id="functionValue">
						<button id="valueFormulaBtn" type="button" class="btn btn-success btn-mini"><i class="icon-only fa-calculator"></i></button>								
					</div>
				</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.IsExempted}</label>
					</div>
					<div class="span8">
						<div style="margin-left: 16px; margin-top: 4px">
							<div id="newExempted"></div>
						</div>
					</div>
				</div>	
			</form>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjax"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCreateFormula" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateFormula">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/payroll/CreateNewFormulaDeduction.js"></script>