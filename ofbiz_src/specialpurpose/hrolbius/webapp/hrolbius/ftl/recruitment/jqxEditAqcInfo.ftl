<div id = "createNewAcquaintanceWindow" style="display: none;">
	<div id="windowHeaderNewAcquaintanceMember">
		<span>
		   ${uiLabelMap.NewAcquaintance}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewAcquaintance" id="createNewAcquaintance">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.LastName}:</label>
						<div class="controls">
							<input id="aqcLastName">
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MiddleName}:</label>  
						<div class="controls">
							<input id="aqcMiddleName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FirstName}:</label>
						<div class="controls">
							<input id="aqcFirstName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRRelationship}:</label>  
						<div class="controls">
							<div  id="aqcRelationshipTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.BirthDate}:</label>   
						<div class="controls">
							<div id="aqcBirthDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HROccupation}:</label>   
						<div class="controls">
							<input id="aqcOccupation">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRPlaceWork}:</label>   
						<div class="controls">
							<input id="aqcPlaceWork">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.PhoneNumber}:</label>   
						<div class="controls">
							<input id="aqcPhoneNumber">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.knowFor}:</label>   
						<div class="controls">
							<input id="aqcKnowFor">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveAqc"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelAqc">${uiLabelMap.CommonCancel}&nbsp;<i class="icon-remove"></i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
