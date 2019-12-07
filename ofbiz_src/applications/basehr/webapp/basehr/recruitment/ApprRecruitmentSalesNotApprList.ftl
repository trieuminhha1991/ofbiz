<div id="recSalesEmplListNotApprWindow" class="hide">
	<div>${uiLabelMap.ListSalesmanNotApproval}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="recruitmentSalesApprGrid"></div>
			<div class="row-fluid no-left-margin">
				<div id="loadingApprRec" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerApprRec"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelApprRecSalesEmpl" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveApprRecSalesEmpl" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.HRApprove}</button>
				</div>
			</div>
		</div>
	</div>		
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/ApprRecruitmentSalesNotApprList.js"></script>