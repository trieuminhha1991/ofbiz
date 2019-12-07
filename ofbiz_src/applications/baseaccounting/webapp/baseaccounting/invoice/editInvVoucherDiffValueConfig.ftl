<div id="editInvVoucherDiffValueWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCDifferenceValueAllow)}</label>
				</div>
				<div class="span6">
					<div id="editInvVoucherDiffValue"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditInVoicherDiffValue">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditInVoicherDiffValue">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/invoice/editInvVoucherDiffValueConfig.js"></script>