<div id="payrollTableInvoiceWindow" class="hide">
	<div>${uiLabelMap.PageTitleApprovalPayrollTable}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>		
			<div class="row-fluid">
				<div id="containerpayrollInvoiceGrid">
			    </div>
			    <div id="jqxNotificationpayrollInvoiceGrid">
			        <div id="notificationContentpayrollInvoiceGrid">
			        </div>
			    </div>
				<div id="payrollInvoiceGrid"></div>
			</div>
		</div>
		<div class="row-fluid">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelCreateInvoice">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateInvoice">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="partyCreatedInvoiceWindow" class="hide">
	<div>${uiLabelMap.PayrollInvoiceList}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div id="invoiceCreatedGrid"></div>
		</div>
	</div>
</div>
<div id="invoiceContextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>
<div id="partyReceiveInvoiceWindow" class="hide">
	<div>${uiLabelMap.InvoiceIsCreatedForOrg}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.EmployeeId}</label>
				</div>
				<div class="span8">
					<input type="text" id="partyIdNotCreatedInv"/>
					<button class="btn btn-mini" type="button" id="searchPartyIdNotCreatedInv">
						<i class="icon-only icon-search nav-search-icon bigger-110" style="vertical-align: baseline;"></i>
					</button>
				</div>
			</div>	
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.EmployeeName}</label>
				</div>
				<div class="span8">
					<input type="text" id="fullNameNotCreatedInv"/>
				</div>
			</div>	
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.RealSalaryPaid}</label>
				</div>
				<div class="span8">
					<div id="realSalaryNotCreatedInv"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.OrganizationReceiveInvoice}</label>
				</div>
				<div class="span8">
					<div id="partyGroupReceiveInv"></div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingPartyGroupReceiveInv" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerPartyGroupReceiveInv"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditPartyReceiveInv">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditPartyReceiveInv">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/payroll/PayrollTableInvoice.js"></script>