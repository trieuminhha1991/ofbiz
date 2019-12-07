<div id="createNewRecrRoundWindow" style="display: none">
	<div id="windowHeaderNewRecrRoundWindow">
		<span>
		   ${uiLabelMap.NewRecruitmentRound}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewRecrRound">
			<form name="createNewRecrRoundWindow" id="createNewRecrRoundWindow">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.roundName}:</label>
								<div class="controls">
									<input id="rrWorkEffortName">
								</div>
							</div>
							
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.estimatedStartDate}:</label>  
								<div class="controls">
									<div id="rrEstimatedStartDate"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.estimatedCompletionDate}:</label>
								<div class="controls">
									<div id="rrEstimatedCompletionDate"></div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label no-left-margin">${uiLabelMap.description}:</label>   
								<div class="controls">
									<input id="rrDescription">
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.location}:</label>   
								<div class="controls">
									<input id="rrLocation">
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label asterisk">${uiLabelMap.sequenceNum}:</label>   
								<div class="controls">
									<div id="rrSequenceNum"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.roundType}:</label>   
								<div class="controls">
									<div id="rrWorkEffortTypeId"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelRecrRound" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveRecrRound" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
