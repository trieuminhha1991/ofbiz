<div id="confirmWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLConfirmTransportInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	        <div class="row-fluid">
	        	<div class="span5"><span class="asterisk">${uiLabelMap.TransportCost}</span></div>
	        	<div class="span7"><div id="shipCostConfirm"></div></div>
	        </div>
	        <div class="row-fluid">
	        	<div class="span5"><span class="asterisk">${uiLabelMap.StartShipDate}</span></div>
	        	<div class="span7"><div id="fromDateConfirm"></div></div>
	        </div>
	        <div class="row-fluid">
	        	<div class="span5"><span class="asterisk">${uiLabelMap.EndShipDate}</span></div>
	        	<div class="span7"><div id="thruDateConfirm"></div></div>
	        </div>
		</div>
		<div class="form-action popup-footer">
	        <button id="confirmCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        <button id="confirmSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>