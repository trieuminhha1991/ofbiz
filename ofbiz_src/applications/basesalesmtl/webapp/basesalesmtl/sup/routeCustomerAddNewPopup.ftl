<div id="messageNotification" style="display : none"></div>
<div id="messageNotificationError" style="display : none"></div>
<div id="deleteDialogCus"></div>
<div id="routePopupCus" style="display : none;" class="hide">
	<div>${uiLabelMap.DistributionRoute}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid margin-bottom10">
				<div id="gridCustomer"></div>
			</div>
			<hr>
			<div class="row-fluid margin-bottom10">
				<div id="listCustomerDistribution"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelRtCus" class='btn btn-danger form-action-button pull-right' onclick="cancelRouteCus()"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
			<button style="display;" id="submitRtCus" class="btn btn-primary form-action-button pull-right" onclick="submitRouteCus()"><i class="fa-check"></i>&nbsp;${uiLabelMap.DAConfirm}</button>
		</div>
	</div>
</div>
<@jqOlbCoreLib hasGrid=true/>
<script src="/salesmtlresources/js/sup/routeCustomerAddNew.js"></script>