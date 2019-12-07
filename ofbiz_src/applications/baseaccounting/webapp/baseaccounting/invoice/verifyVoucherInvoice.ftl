<div class="hide" id="verifyVoucherInvWindow">
	<div>${uiLabelMap.BACCVoucherVerify}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div id="loadingVerifyVoucher"></div>
			<div class="row-fluid form-horizontal form-window-content-custom label-text-left content-description">
				<div class="span6">
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueNotTaxInActual)}</span>
						</div>
						<div class="span5">
							<div id="amountNotTaxActualEdit" class="green-label" style="text-align: left;"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right"><b>${StringUtil.wrapString(uiLabelMap.BACCDifference)}</b></span>
						</div>
						<div class="span5">
							<div id="diffAmountNotTaxEdit" class="green-label" style="text-align: left; "></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueTaxInActual)}</span>
						</div>
						<div class="span5">
							<div id="amountTaxActualEdit" class="green-label" style="text-align: left;"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right"><b>${StringUtil.wrapString(uiLabelMap.BACCDifference)}</b></span>
						</div>
						<div class="span5">
							<div id="diffAmountTaxEdit" class="green-label" style="text-align: left;"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="hr hr8 hr-double hr-dotted"></div>
			<div class="row-fluid">
				<ul class="nav nav-tabs padding-18">
					<li class="active">
						<a data-toggle="tab" href="#invoiceItemNotTaxContainer" aria-expanded="true">
							${uiLabelMap.InvoiceValueNotTaxInSystem}
						</a>
					</li>
					<li class="">
						<a data-toggle="tab" href="#invoiceItemTaxContainer" aria-expanded="false">
							${uiLabelMap.InvoiceValueTaxInSystem}
						</a>
					</li>
				</ul>
				<div class="tab-content overflow-visible" style="border: none !important">
					<div class="tab-pane active " id="invoiceItemNotTaxContainer">
						<div id="invoiceItemNotTaxGrid"></div>
					</div>
					<div class="tab-pane" id="invoiceItemTaxContainer">
						<div id="invoiceItemTaxGrid"></div>
					</div>
				</div>
			</div>
		</div>		
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelVerifyVoucher">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveVerifyVoucher">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			<button type="button" class='btn btn-yellow form-action-button pull-right' id="resetVerifyVoucher">
				<i class='fa fa-refresh'></i>&nbsp;${uiLabelMap.BACCCommonReset}</button>
            <#if !invoice.isVerified?exists || invoice.isVerified != "Y">
				<button type="button" class='btn btn-success form-action-button pull-right' id="verifyVerifyVoucher">
					<i class='fa fa-gavel'></i>&nbsp;${uiLabelMap.BACCVerify}</button>
			</#if>	
		</div>
	</div>
</div>

<script type="text/javascript" src="/accresources/js/invoice/verifyVoucherInvoice.js?v=20170419"></script>