<div id="evaluatedCANDTestRoundWindow" class="hide">
	<div>${uiLabelMap.RatingTestResultCandidate}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div id="containerrecruitTestRoundSubjGrid" style="background-color: transparent; overflow: auto;">
			    </div>
			    <div id="jqxNotificationrecruitTestRoundSubjGrid">
			        <div id="notificationContentrecruitTestRoundSubjGrid">
			        </div>
			    </div>
				<div id="recruitTestRoundSubjGrid"></div>
			</div>
			<hr/>
			<div class="row-fluid">
				<div class='row-fluid margin-bottom10'>
					<div class='span2 align-right'>
						<label class="">${uiLabelMap.HRCommonComment}</label>
					</div>
					<div class='span10'>
						<textarea id="evaluatedtestRoundComment"></textarea>
					</div>					
				</div>	
				<div class='row-fluid margin-bottom10' style="margin-top: 15px">
					<div class='span2 align-right'>
						<label class="">${uiLabelMap.HRCommonConclusion}</label>
					</div>
					<div class='span10'>
						<div class="row-fluid">
							<div class="span12">
								<div class="span5">
									<div id="testRoundCANDPassed"><span style="font-size: 14px">${uiLabelMap.HRCommonPass}</span></div>
								</div>
								<div class="span5">
									<div id="testRoundCANDNotPassed">
										<span style="font-size: 14px">${uiLabelMap.HRCommonFail}</span>
									</div>
								</div>
							</div>
						</div>
					</div>					
				</div>	
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingEvaluatedTestRound" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerEvaluatedTestRound"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<#if editable?exists && editable == "true">
						<button id="cancelEvalutedTestCAND" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button id="saveEvalutedTestCAND" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					<#else>
						<button id="cancelEvalutedTestCAND" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					</#if>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
if(typeof(globalVar) == "undefined"){
	globalVar = {};	
}
<#if editable?exists && editable == "true">
	globalVar.editEvaluatedCandidate = true;
<#else>
	globalVar.editEvaluatedCandidate = false;
</#if>
</script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitEvalutedCandidateTestRound.js"></script>