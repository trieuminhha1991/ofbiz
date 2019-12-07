<div id="alterpopupWindowEdit" style="display : none;">
	<div>
		${uiLabelMap.CommonEdit}
	</div>
	<div style="overflow: hidden;">
		<form id="ProStoShipmentCostEditForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSShipmentCostEstimateId}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<input id="shipmentCostEstimateEdit" />
					</div>
				</div>
				
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSShipmentMethodTypeId}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="carrierShipmentEdit">
							<div id="carrierShipmentMethodGridEdit" ></div>
						</div>
					</div>
				</div>
			
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 align-right">${uiLabelMap.BSOrderFlatPrice}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="orderFlatPriceEdit"></div> 
					</div>
				</div>
					
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 align-right">${uiLabelMap.BSOrderPricePercent}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="orderPricePercentEdit"></div>
					</div>
				</div>
				
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 align-right">${uiLabelMap.BSOrderItemFlatPrice}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="orderItemFlatPriceEdit"></div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.BSCancel}
						</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.BSSave}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript" src="/salesresources/js/setting/productStoreUpdateShipmentCost.js"></script>