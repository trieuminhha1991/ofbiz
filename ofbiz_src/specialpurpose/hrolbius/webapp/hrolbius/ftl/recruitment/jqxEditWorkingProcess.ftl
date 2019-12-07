<div id="createNewWorkingProcessWindow" style="display: none;">
	<div id="windowHeaderNewWorkingProcess">
		<span>
		   ${uiLabelMap.NewWorkingProcess}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewWorkingProcess" id="createNewWorkingProcess">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CompanyName}:</label>
						<div class="controls">
							<input id="wpCompanyName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.EmplPositionTypeId}:</label>
						<div class="controls">
							<input id="wpEmplPositionTypeId">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.JobDescription}:</label>  
						<div class="controls">
							<input id="wpJobDescription"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRSalary}:</label>   
						<div class="controls">
							<input id="wpPayroll"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.TerminationReason}:</label>   
						<div class="controls">
							<input id="wpTerminationReasonId">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRRewardAndDisciplining}:</label>   
						<div class="controls">
							<input id="wpRewardDiscrip">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonFromDate}:</label>   
						<div class="controls">
							<div id="wpFromDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonThruDate}:</label>   
						<div class="controls">
							<div id="wpThruDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-success" id="alterSaveWP"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelWP">
								${uiLabelMap.CommonCancel}&nbsp;<i class="icon-remove"></i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>