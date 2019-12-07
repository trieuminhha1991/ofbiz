<script type="text/javascript">
<#if payrollTableAndSum.totalOrgPaid?exists>
	globalVar.totalOrgPaid = ${payrollTableAndSum.totalOrgPaid};
<#else>
	globalVar.totalOrgPaid = 0;
</#if>
<#if payrollTableAndSum.totalAcutalReceipt?exists>
	globalVar.totalAcutalReceipt = ${payrollTableAndSum.totalAcutalReceipt};
<#else>
	globalVar.totalAcutalReceipt = 0;
</#if>
globalVar.payrollTableName = "${StringUtil.wrapString(payrollTableAndSum.payrollTableName)}";

</script>
<div id="payrollTableApprWindow" class="hide">
	<div>${uiLabelMap.HRApprove}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.PayrollTableName)}</label>
				</div>
				<div class="span8">
					<input type="text" id="payrollTableName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.TotalOrgPaidInsurance)}</label>
				</div>
				<div class="span8">
					<div id="totalOrgPaidAmount"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.TotalRealSalaryPaid)}</label>
				</div>
				<div class="span8">
					<div id="totalAcutalReceiptAmount"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRApprove)}</label>
				</div>
				<div class="span8">
					<div id="approvalDropDown"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.ReasonEnumId)}</label>
				</div>
				<div class="span8">
					<textarea id="changeReasonAppr"></textarea>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingApproval" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerApproval"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAppr">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAppr">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/payroll/ApprovalPayrolTable.js"></script>