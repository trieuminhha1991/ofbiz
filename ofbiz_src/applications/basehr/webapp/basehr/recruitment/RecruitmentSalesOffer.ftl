<div id="recruitmentSalesOfferWindow" class="hide">
	<div>${uiLabelMap.RecruitmentOffer}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="recruitmentSalesOfferGrid"></div>
			<div class="row-fluid no-left-margin">
				<div id="loadingOfferRec" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerOfferRec"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelOfferRec" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveOfferRec" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSubmit}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentSalesOffer.js"></script>