<script type="text/javascript">
	globalVar.isEditable = ${isEditable};
</script>

<div id="payrollPartyAmountDetailWindow" class="hide">
	<div>${uiLabelMap.EmplSalaryItemDetails}</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
    		<div class="row-fluid">
    			<div class="span12">
    				<div class="span6">
			    		<div class='row-fluid margin-bottom10'>
			    			<div class='span4 text-algin-right'>
			    				<label>${uiLabelMap.EmployeeId}</label>
			    			</div>
			    			<div class="span8">
								<input type="text" id="partyIdDetail"/>
								<button class="btn btn-mini" type="button" id="searchBtn">
									<i class="icon-only icon-search nav-search-icon bigger-110" style="vertical-align: baseline;"></i>
								</button>
							</div>	
			    		</div>
			    		<div class='row-fluid margin-bottom10'>
			    			<div class='span4 text-algin-right'>
			    				<label>${uiLabelMap.SalaryBaseFlat}</label>
			    			</div>
			    			<div class="span8">
								<div id="baseSalaryAmountDetail"></div>
							</div>	
			    		</div>
    				</div>
    				<div class="span6">
    					<div class='row-fluid margin-bottom10'>
			    			<div class='span4 text-algin-right'>
			    				<label>${uiLabelMap.EmployeeName}</label>
			    			</div>
			    			<div class="span8">
								<input type="text" id="fullNameDetail"/>
							</div>	
			    		</div>
			    		<div class='row-fluid margin-bottom10'>
			    			<div class='span4 text-algin-right'>
			    				<label>${uiLabelMap.InsuranceSalaryShort}</label>
			    			</div>
			    			<div class="span8">
								<div id="insSalaryAmountDetail"></div>
							</div>	
			    		</div>
    				</div>
    			</div>
    		</div>
    		<div class="row-fluid">
    			<div id="jqxTabDetail">
    				<ul>
    					 <li style="margin-left: 10px;">
    					 	${uiLabelMap.CommonIncome}
    					 </li>
    					 <li>
    					 	${uiLabelMap.CommonDeduction}
    					 </li>
    				</ul>
    				<div style="overflow: hidden;">
    					<div id="containergridIncome" style="background-color: transparent; overflow: auto;">
					    </div>
					    <div id="jqxNotificationgridIncome">
					        <div id="notificationContentgridIncome">
					        </div>
					    </div>
    					<div id="gridIncome"></div>
    				</div>
    				<div style="overflow: hidden;">	
    					<div id="containergridDeduction" style="background-color: transparent; overflow: auto;">
					    </div>
					    <div id="jqxNotificationgridDeduction">
					        <div id="notificationContentgridDeduction">
					        </div>
					    </div>
    					<div id="gridDeduction"></div>
    				</div>
    			</div>
    		</div>
    		<div class="row-fluid no-left-margin">
				<div id="loadingPartySalDetail" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerPartySalDetail"></div>
				</div>
			</div>
    	</div>
    	<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    	</div>
    </div>	
</div>

<#if isEditable == "true">
	<div id="addEmplIncomeWindow" class="hide">
		<div>${uiLabelMap.AddPayrollIncome}</div>
		<div class='form-window-container'>
	    	<div class='form-window-content'>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label class="asterisk">${uiLabelMap.HRIncome}</label>
	    			</div>
	    			<div class="span8">
						<div id="incomeFormulaNew"></div>
					</div>	
	    		</div>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label>${uiLabelMap.HRCommonAmount}</label>
	    			</div>
	    			<div class="span8">
						<div id="incomeAmountNew"></div>
					</div>	
	    		</div>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label>${uiLabelMap.TaxableType}</label>
	    			</div>
	    			<div class="span8">
						<input type="text" id="taxableTypeNew">
					</div>	
	    		</div>
	    		<div class="row-fluid no-left-margin">
					<div id="loadingIncomeCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerIncomeCreateNew"></div>
					</div>
				</div>
	    	</div>
	    	<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddIncome">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				<button id="saveAndContinueIncome" class='btn btn-success form-action-button pull-right'>
					<i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddIncome">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
	    </div>	
	</div>
	<div id="addEmplDeductionWindow" class="hide">
		<div>${uiLabelMap.AddPayrollDeduction}</div>
		<div class='form-window-container'>
	    	<div class='form-window-content'>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label class="asterisk">${uiLabelMap.HRDeduction}</label>
	    			</div>
	    			<div class="span8">
						<div id="deductionFormulaNew"></div>
					</div>	
	    		</div>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label>${uiLabelMap.HRCommonAmount}</label>
	    			</div>
	    			<div class="span8">
						<div id="deductionAmountNew"></div>
					</div>	
	    		</div>
	    		<div class='row-fluid margin-bottom10'>
	    			<div class='span4 text-algin-right'>
	    				<label></label>
	    			</div>
	    			<div class="span8">
						<div id='isExemptedTaxNew' style='margin-left: 0px !important'>
	               			<span style="font-size: 14px">${uiLabelMap.IsExemptedTax}</span>
	           			</div>
					</div>	
	    		</div>
	    		<div class="row-fluid no-left-margin">
					<div id="loadingDeductionCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerDeductionCreateNew"></div>
					</div>
				</div>
	    	</div>
	    	<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddDeduction">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				<button id="saveAndContinueDeduction" class='btn btn-success form-action-button pull-right'>
					<i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>	
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddDeduction">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
	    </div>	
	</div>
	<div id='contextMenuIncome' style="z-index: 20000 !important"  class="hide">
		<ul>
			<li action="delete">
				<i class="icon-trash"></i>${uiLabelMap.DmsDelete}
	        </li>
		</ul>
	</div>
	<div id='contextMenuDeduction' style="z-index: 20000 !important"  class="hide">
		<ul>
			<li action="delete">
				<i class="icon-trash"></i>${uiLabelMap.DmsDelete}
	        </li>
		</ul>
	</div>
	<script type="text/javascript" src="/hrresources/js/payroll/AddPartyPayrollAmount.js"></script>
</#if>

<script type="text/javascript" src="/hrresources/js/payroll/ViewPayrollTablePartyDetail.js"></script>