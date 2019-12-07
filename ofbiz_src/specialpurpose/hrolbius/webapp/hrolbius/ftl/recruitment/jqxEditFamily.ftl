<div id = "createNewFamilyWindow" style="display: none">
	<div id="windowHeaderNewFamilyMember">
		<span>
		   ${uiLabelMap.NewFamilyMember}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewFamily" id="createNewFamily">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.LastName}:</label>
						<div class="controls">
							<input id="fmLastName">
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MiddleName}:</label>  
						<div class="controls">
							<input id="fmMiddleName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FirstName}:</label>
						<div class="controls">
							<input id="fmFirstName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRRelationship}:</label>  
						<div class="controls">
							<div  id="fmRelationshipTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.BirthDate}:</label>   
						<div class="controls">
							<div id="fmBirthDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HROccupation}:</label>   
						<div class="controls">
							<input id="fmOccupation">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRPlaceWork}:</label>   
						<div class="controls">
							<input id="fmPlaceWork">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.PhoneNumber}:</label>   
						<div class="controls">
							<input id="fmPhoneNumber">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.EmergencyContact}:</label>   
						<div class="controls">
							<div id="fmEmergencyContact" style="margin-left: -3px !important;"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveFamily"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelFamily">${uiLabelMap.CommonCancel}&nbsp;<i class="icon-remove"></i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
