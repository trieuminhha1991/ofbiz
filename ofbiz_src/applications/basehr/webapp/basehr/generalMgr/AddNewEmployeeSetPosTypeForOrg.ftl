<div id="SetPositionTypeForOrgWindow" class="hide">
	<div>${uiLabelMap.addEmplPosition}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class="asterisk">${uiLabelMap.HrCommonPosition}</label>
						</div>
						<div class='span8'>
							<div id="positionTypeDropDownBtn">
								<div style="border-color: transparent;" id="positionTypeGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="asterisk">${uiLabelMap.OrganizationMange}</label>
						</div>
						<div class='span8'>
							<div id="setPositionTypeDropDownButton">
								<div id="setPositionTypeJqxTree"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="asterisk">${uiLabelMap.EffectiveFromDate}</label>
						</div>
						<div class='span8'>
							<div id="setPositionTypeActualFromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class="">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class='span8'>
							<div id="setPositionTypeActualThruDate"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="setPositionTypeSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="setPositionTypeCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeSetPosTypeForOrg.js"></script>