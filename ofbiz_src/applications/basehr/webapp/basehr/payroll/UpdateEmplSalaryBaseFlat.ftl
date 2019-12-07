<div id="batchUpdateSalaryWindow" class="hide">
	<div>${uiLabelMap.BatchUpdateBaseSalary}</div>
	<div class="form-window-container">
     	<div class='form-window-content'>
     		<div class="row-fluid" style="border-bottom: 1px solid #CCC">
     			<div class="span12">
     				<div class="span6">
     					<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}</label>
							</div>
							<div class="span8">
								<div id="amountSalUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}</label>
							</div>
							<div class="span8">
								<div class="row-fluid hide periodTypeDaily">
									<div id="fromDateDailyUpdate"></div>
								</div>
								<div class="row-fluid hide periodTypeMonthly">
									<div style="display: inline-block; float: left; margin-right: 5px" id="monthFromMonthlyUpdate"></div>
									<div style="display: inline-block; float: left;" id="yearFromMonthlyUpdate"></div> 
								</div>
								<div class="row-fluid hide periodTypeYearly">
									<div id="yearFromYearlyUpdate"></div>
								</div>
							</div>
						</div>
     				</div>
     				<div class="span6">
     					<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}</label>
							</div>
							<div class="span8">
								<div id="periodTypeSalUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.PayrollThruDate)}</label>
							</div>
							<div class="span8">
								<div class="row-fluid hide periodTypeDaily">
									<div id="thruDateDailyUpdate"></div>
								</div>
								<div class="row-fluid hide periodTypeMonthly">
									<div style="display: inline-block; float: left; margin-right: 5px" id="monthThruMonthlyUpdate"></div>
									<div style="display: inline-block; float: left;" id="yearThruMonthlyUpdate"></div> 
								</div>
								<div class="row-fluid hide periodTypeYearly">
									<div id="yearThruYearlyUpdate"></div>
								</div>
							</div>
						</div>
     				</div>
     			</div>
     		</div>
     		<div class="row-fluid" style="margin-top: 10px">
     			<div id="listEmplBaseSalGrid"></div>
     		</div>
     		<div class="row-fluid no-left-margin">
				<div id="loadingUpdate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerUpdate"></div>
				</div>
			</div>
     	</div>
     	<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelUpdate"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueUpdate"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdate"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
     	</div>
     </div>	
</div>

<script type="text/javascript" src="/hrresources/js/payroll/UpdateEmplSalaryBaseFlat.js"></script>