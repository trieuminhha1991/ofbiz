<div id="invoiceItemEditWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCInvoiceItem}</label>
					</div>
					<div class="span7">
						<div id="updateInvItemTypeDropDown">
							<div id="updateInvItemTypeGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCProduct}</label>
					</div>
					<div class="span7">
						<div id="updateInvItemProdDropDown">
							<div id="updateInvItemProdGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCQuantity}</label>
					</div>
					<div class="span7">
						<div id="updateInvItemQty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCUnitPrice}</label>
					</div>
					<div class="span7">
						<div id="updateInvItemAmount"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTotal}</label>
					</div>
					<div class="span7">
						<div id="updateInvItemTotal"></div>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelUpdateInvoiceItem">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdateInvoiceItem">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<script type="text/javascript" src="/accresources/js/invoice/invoiceItemEdit.js"></script>