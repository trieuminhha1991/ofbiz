<div id="editDeliveryNoteWindow" class="hide">
	<div>${uiLabelMap.DeliveryNoteInfo}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class="row-fluid">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryId}:</div>
								<div class="span7 green-label align-left">
									<span id="deliveryIdView"></span>
								</div>
							</div>
							<div class='row-fluid'>
								<div class="span5" style="text-align: right;">${uiLabelMap.BSOrderId}:</div>
								<div class="span7 green-label align-left">
									<span id="orderIdView"></span>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class="span5" style="text-align: right;">${uiLabelMap.BACCStatusId}:</div>
								<div class="span7 green-label align-left">
									<span id="statusIdView"></span>
								</div>
							</div>
						</div>
					</div><!-- ./span12 -->
				</div><!-- ./row-fluid -->
			</form>
			<div class="row-fluid">
				<div id="shipmentItemBillingGrid"></div>	
			</div>
		</div><!-- ./row-fluid -->
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="closeEditDeliveryNote">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>	
			<button type="button" class='btn btn-warning form-action-button pull-right' id="cancelDeliveryNote">
				<i class='fa fa-exclamation-triangle'></i>&nbsp;${uiLabelMap.CancelDeliveryNote}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/invoice/invoiceDeliveryNote.js"></script>