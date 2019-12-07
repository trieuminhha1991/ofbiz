<#include "script/ViewEmplSalaryItemHistoryDetailScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/payroll/ViewEmplSalaryItemHisDetails.js"></script>
<div id="emplSalaryItemDetail" class="hide">
	<div>${uiLabelMap.EmplSalaryItemDetails}</div>
	<div class='form-window-container'>
		<div class='row-fluid margin-bottom10'>
			<div id='jqxTabEmplSalaryItemDetail'>
				<ul>
					<li style="margin-left: 20px;">${uiLabelMap.CommonIncome}</li>
				    <li>${uiLabelMap.CommonDeduction}</li>
				</ul>
				<div style="margin-top: 5px">
					<div id="emplSalaryItemIncomeGrid"></div>
				</div>
				<div style="margin-top: 5px">
					<div id="emplSalaryItemDeductionGrid"></div>
				</div>
			</div>	
		</div>
	</div>	
</div>

<div id="orgDetailPaidWindow" class="hide">
	<div>${uiLabelMap.OrgDetailPaid}</div>
	<div class='form-window-container'>
		<div class='row-fluid margin-bottom10'>
			<div id="dropdownBtnOrgPaid">
				 <div style="border: none;" id='jqxTreeOrgPaid'>
				 </div>
			</div>
		</div>
		<div class='row-fluid margin-bottom10'>
			<div id="orgDetailPaidGrid"></div>
		</div>
	</div>
</div>
<div id="paySalaryInvoiceItemWindow" class="hide">
	<div>${uiLabelMap.InvoiceItemForSalaryItem}</div>
	<div class='form-window-container'>
		<div id="containerinvoiceItemSalaryGrid"></div>
		<div id="jqxNotificationinvoiceItemSalaryGrid">
	        <div id="notificationContentinvoiceItemSalaryGrid">
	        </div>
	    </div>
		<div class='row-fluid margin-bottom10'>
			<div id="invoiceItemSalaryGrid"></div>
		</div>
		<div class='row-fluid'>
			<h6 style="margin: 0; padding: 0"><i>(${StringUtil.wrapString(uiLabelMap.ConfigInvoiceItemAt)} <a href="<@ofbizUrl>ViewPartyFormulaInvoiceItemType</@ofbizUrl>">${uiLabelMap.HRCommonHere}</a>)</i></h6>
		</div>
	</div>
</div>
<div id="paySalaryInvoiceItemContextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		
	</ul>
</div>

<div id="editPaySalaryInvoiceItemWindow" class="hide">
	<div>${uiLabelMap.EditPaySalaryInvoiceItem}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.EmployeeId}</label>
				</div>
				<div class="span7">
					<input type="text" id="partyId">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.EmployeeName}</label>
				</div>
				<div class="span7">
					<input type="text" id="partyName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.CommonDepartment}</label>
				</div>
				<div class="span7">
					<input type="text" id="organizationList">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.PayrollItemType}</label>
				</div>
				<div class="span7">
					<input type="text" id="salaryItem">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.AccountingInvoiceItemType}</label>
				</div>
				<div class="span7">
					<div id="editInvoiceItemTypeEdit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelEditSalaryIIT" type="button" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditSalaryIIT"><i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
