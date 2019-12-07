<div id="invoiceItemNewWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCInvoiceItem}</label>
					</div>
					<div class="span7">
						<div id="newInvItemTypeDropDown">
							<div id="newInvItemTypeGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCProduct}</label>
					</div>
					<div class="span7">
						<div id="newInvItemProdDropDown">
							<div id="newInvItemProdGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCQuantity}</label>
					</div>
					<div class="span7">
						<div id="newInvItemQty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCUnitPrice}</label>
					</div>
					<div class="span7">
						<div id="newInvItemAmount"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTotal}</label>
					</div>
					<div class="span7">
						<div id="newInvItemTotal"></div>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelNewInvoiceItem">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveNewInvoiceItem">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<script type="text/javascript" src="/accresources/js/invoice/invoiceItemNew.js"></script>