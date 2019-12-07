<div id="addNewVoucherWindowQuick" class="hide">
	<div>${uiLabelMap.BACCQuickCreate}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}</label>
				</div>
				<div class="span7">
					<input type="text" id="invoiceIdVoucherQuick" value="${parameters.invoiceId}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherFormQuick" >
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherSerialQuick">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherNumberQuick">
				</div>
			</div>
            <div class='row-fluid margin-bottom5'>
                <div class="span5 text-algin-right">
                    <label class="">${StringUtil.wrapString(uiLabelMap.BACCDescription)}</label>
                </div>
                <div class="span7">
                    <input type="text" id="descriptionQuick">
                </div>
            </div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddVoucherQuick">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddVoucherQuick">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/quickCreateVoucherInvoice.js?v=20170819"></script>