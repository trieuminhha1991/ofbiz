<div class="row-fluid">
	<div class="span12">
		<div id="scoreExamWindow" style="display: none;">
			<div id="windowHeaderScoreExam">
	            <span>
	               ${uiLabelMap.scoreExam}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentScoreExam">
            	<div class="basic-form form-horizontal" style="margin-top: 10px">
        			<form name="scoreDetail" id="scoreDetail">
	        			<div class="row-fluid" >
		        			<div class="span12">
			        			<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.applFullname}:</label>  
									<div class="controls">
										<div id="examFullname"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.Gender}:</label>  
									<div class="controls">
										<div id="examGender"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.BirthDate}:</label>  
									<div class="controls">
										<div id="examBirthDate"></div>
									</div>
								</div>
			        			<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.Position}:</label>  
									<div class="controls">
										<div id="examEmplPositionType"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.Department}:</label>  
									<div class="controls">
										<div id="examPartyId"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.score}:</label>  
									<div class="controls">
										<input id="core"></input>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.resultId}:</label>  
									<div class="controls">
										<div id="examResultId" ></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.nextRound}:</label>  
									<div class="controls">
										<div id="examIsNextRound" style="margin-left: -3px !important;"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">&nbsp</label>
									<div class="controls">
										<button type="button" class="btn btn-mini btn-success" id="alterSaveScoreExam"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
										<button type="button" class="btn btn-mini btn-danger" id="alterCancelScoreExam"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>