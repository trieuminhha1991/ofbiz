<div id="partyFixedAssetAssignmentWindow" class="hide">
	<div>${uiLabelMap.BACCNewAssignedParty}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="asterisk">${uiLabelMap.BACCOrganizationParty}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyDropDownBtn">
								<div id="assignPartyTree"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.PartyRole}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyRoleType"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BACCAllocatedDate}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyAllocatedDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.UsingFromDate}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyFromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyThruDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.CommonStatus}</label>
						</div>
						<div class='span7'>
							<div id="assignPartyStatus"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="saveAssignParty" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="cancelAssignParty" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetPartyAssignment.js"></script>