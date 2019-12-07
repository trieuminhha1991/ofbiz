<div id="selectFacilityWindow" class="hide popup-bound">
	<div id="faTitle">${uiLabelMap.SelectFacilityToExport}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class='span3 align-right'>
					<span class="asterisk">${uiLabelMap.Facility}</span>
				</div>
				<div class='span8'>
					<div id="facility">
						<div id="gridFacility"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top10">
				<div class='span3 align-right'>
					<span class="asterisk">${uiLabelMap.Address}</span>
				</div>
				<div class='span8'>
					<div class="green-label" id="contactMechId"></div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="faCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="faSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/logresources/js/requirement/reqRequirementFacility.js?v=1.1.1"></script>