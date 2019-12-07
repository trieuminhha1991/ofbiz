<div id="evaluatedINTVWRoundWindow" class="hide">
	<div>${uiLabelMap.RatingINTVWResultCandidate}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div id="containerrecruitINTWRoundGrid" style="background-color: transparent; overflow: auto;">
			    </div>
			    <div id="jqxNotificationrecruitINTWRoundGrid">
			        <div id="notificationContentrecruitINTWRoundGrid">
			        </div>
			    </div>
				<div id="recruitINTWRoundGrid"></div>
			</div>
			<hr/>
			<div class="row-fluid">
				<div class='row-fluid margin-bottom10'>
					<div class='span2 align-right'>
						<label class="">${uiLabelMap.HRCommonComment}</label>
					</div>
					<div class='span10'>
						<textarea id="evaluatedINTVWRoundComment"></textarea>
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
									<div id="intvwRoundCANDPassed"><span style="font-size: 14px">${uiLabelMap.HRCommonPass}</span></div>
								</div>
								<div class="span5">
									<div id="intvwRoundCANDNotPassed">
										<span style="font-size: 14px">${uiLabelMap.HRCommonFail}</span>
									</div>
								</div>
							</div>
						</div>
					</div>					
				</div>	
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingEvaluatedINTVWRound" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerEvaluatedINTVWRound"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<#if editable?exists && editable == "true">
						<button id="cancelEvalutedINTWCAND" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button id="saveEvalutedINTWCAND" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					<#else>
						<button id="cancelEvalutedINTWCAND" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
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
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitEvalutedCandidateINTVWRound.js"></script>