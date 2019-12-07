<div id="AddNewKeyPerfIndicatorWindow" class="hide">
	<div>${uiLabelMap.AddNewKeyPerfIndicator}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.KeyPerfIndicatorName}</label>
				</div>
				<div class='span8'>
					<input type="text" id="keyPerfIndicatorNameAdd">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.PartyApply}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div id="applAllParty" style="margin-left: 0px !important; padding-top: 5px">
							<span style="font-size: 14px">${uiLabelMap.ApplyForAllDepartment}</span>
						</div>
					</div>
					<div class="row-fluid" id="partyApplArea">
						<div class="row-fluid">
							<div id="partyTreeAdd"></div>
						</div>
						<div class="row-fluid">
							<div id="descendantSelect" style="margin-left: 0px !important; padding-top: 5px">
								<span>${uiLabelMap.IncludeDescendantParty}</span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.PositionTypeAppl}</label>
				</div>
				<div class='span8'>
					<div id="emplPositionTypeApplAdd"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.EffectiveFromDate}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span5">
							<div id="fromDateAdd"></div>
						</div>
						<div class="span7">
							<div class='row-fluid'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.CommonThruDate}</label>
								</div>
								<div class='span8'>
									<div id="thruDateAdd"></div>
								</div>
							</div>
						</div>		
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddNew"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddNew" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddNew" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/AddKeyPerfIndicator.js"></script>