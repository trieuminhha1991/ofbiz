<div id = "createNewEduWindow" style="display: none;">
	<div id="windowHeaderNewEducation">
		<span>
		   ${uiLabelMap.NewEducation}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewEducation" id="createNewEducation">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.HRCollegeName}:</label>
						<div class="controls">
							<div id="eduSchoolId"></div>
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonFromDate}:</label>  
						<div class="controls">
							<div id="eduFromDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonThruDate}:</label>
						<div class="controls">
							<div id="eduThruDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRSpecialization}:</label>  
						<div class="controls">
							<div  id="eduMajorId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HROlbiusTrainingType}:</label>   
						<div class="controls">
							<div id="eduStudyModeTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRCommonClassification}:</label>   
						<div class="controls">
							<div id="eduDegreeClassificationTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRCommonSystemEducation}:</label>   
						<div class="controls">
							<div id="eduSystemTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveEdu"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelEdu">${uiLabelMap.CommonCancel}&nbsp;<i class="icon-remove"></i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
