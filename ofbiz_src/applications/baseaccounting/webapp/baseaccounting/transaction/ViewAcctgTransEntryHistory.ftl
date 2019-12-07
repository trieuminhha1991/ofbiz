<div id="AcctgTransEntryHistoryWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BSHistory)}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="row-fluid form-horizontal form-window-content-custom label-text-left content-description">
					<div class='row-fluid'>
						<div class="span3 text-algin-right">
							<span style="float: right;">${uiLabelMap.BACCModifiedDate}</span>
						</div>								
						<div class="span9">
							<div id="acctgTransHistoryDropDown">
								<div id="acctgTransHistoryGrid"></div>	
							</div>
						</div>								
					</div>
				</div>
			</div>
			<div class="row-fluid" style="margin-top: 15px">
				<div id="acctgTransEntryHistoryGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="closeViewHistory" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/transaction/ViewAcctgTransEntryHistory.js"></script>