<div id="deleteDialog"></div>
<div id="routePopup" style="display : none;">
	<div>${uiLabelMap.DistributionRoute}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid margin-bottom10">
				<div id="gridSM"></div>
			</div>
			<hr>
			<div class="row-fluid margin-bottom10">
				<div id="listSMDistribution"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelRt" class='btn btn-danger form-action-button pull-right' onclick="cancelRoute()"><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
			<button id="submitRt" class="btn btn-primary form-action-button pull-right" onclick="submitRoute()"><i class="fa-check"></i>&nbsp;${uiLabelMap.DAConfirm}</button>
		</div>
	</div>
</div>
<script src="/salesmtlresources/js/sup/routeEmplAddNew.js"></script>