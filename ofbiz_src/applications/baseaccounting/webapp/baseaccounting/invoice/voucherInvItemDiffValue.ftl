
<#assign invoiceItemTypeDiffValue = delegator.findOne("InvoiceItemType", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId", "PINV_ADJIMPPO_ITEM"), false)!/>
<div id="addIIDiffValueWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom5'>
				<div class="span5 text-algin-right">
					<label>${StringUtil.wrapString(uiLabelMap.BACCInvoiceItem)}</label>
				</div>
				<div class="span7">
					<label style="color: #037c07; font-weight: bold;">
						<#if invoiceItemTypeDiffValue?exists && invoiceItemTypeDiffValue.description?exists>
							${StringUtil.wrapString(invoiceItemTypeDiffValue.description)}
						<#else>
							___________________
						</#if>
					</label>
				</div>
			</div>
			<div class='row-fluid'>
				<div class="span5 text-algin-right">
					<label>${StringUtil.wrapString(uiLabelMap.BACCAmount)}</label>
				</div>
				<div class="span7">
					<div id="amountIITDiffValue"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelNewInvoiceItemDiffValue">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewInvoiceItemDiffValue">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/invoice/voucherInvItemDiffValue.js"></script>