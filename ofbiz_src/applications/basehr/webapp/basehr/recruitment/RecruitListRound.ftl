<div class="hide" id="recruitmentRoundWindow">
	<div>${uiLabelMap.RecruitmentRoundList}</div>
	<div class='form-window-container'>
		<div class="row-fluid" style="position: relative;">
			<div id="roundMainSplitter" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="listBoxRoundRec"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	               		<div id="appendNtfrecruitRoundCandidateGrid">
							<div id="ntfRecruitRoundCandidateGrid">
								<span id="ntfTextRecruitRoundCandidateGrid"></span>
							</div>
						</div>
	                   <div id="recruitRoundCandidateGrid"></div>
	               </div>
	        	</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingListCandidateRound" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerListCandidateRound"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	<div id="recruitStoreInterviewWindow" class="hide">
		<div>${uiLabelMap.RecruitmentStoreInterview}</div>
		<div class='form-window-container'>
			<div class='form-window-content' style="position: relative;">
				<div id="recruitStoreInterviewPartyInfoPanel">
					<div class="row-fluid">
						<div class="span12" style="margin-top: 15px">
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.RecruitmentCandidateId}</label>
									</div>  
									<div class="span8">
										<input type="text" id="candidateIdStoreInterview" />
							   		</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HRFullName}</label>
									</div>
									<div class="span8">
										<input type="text" id="fullNameStoreInterview" />
									</div>
								</div>		
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyGender}</label>
									</div>
									<div class="span8">
										<input type="text" id="genderStoreInterview" />
									</div>
								</div>		
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyBirthDate}</label>
									</div>
									<div class="span8">
										<input type="text" id="birthDateStoreInterview" />
									</div>
								</div>		
							</div>
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.DegreeTraining}</label>
									</div>
									<div class="span8">
										<input type="text" id="trainingStoreInterview" />
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HRSpecialization}</label>
									</div>
									<div class="span8">
										<input type="text" id="majorIdStoreInterview" />
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HRCommonClassification}</label>
									</div>
									<div class="span8">
										<input type="text" id="classificationStoreInterview" />
									</div>
								</div>	
							</div>
						</div>
					</div>
				</div>
				<div style="margin-top: 10px" id="recruitStoreInterviewResultsPanel">
					<div class="row-fluid" >
						<div class="span12" style="padding: 15px 0px 0px">
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.RecruitmentDateInterview}</label>
									</div>
									<div class="span8">
										<div id="dateInterviewStore"></div>
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label></label>
									</div>
									<div class="span8">
										<div id="moveNextRound" style="margin-left: -5px !important; margin-top: 5px"><label>${uiLabelMap.MoveCandidateToNextRound}</label></div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.RecruitmentInterviewRound}</label>
									</div>
									<div class="span8">
										<input id="recRoundStoreInterview" type="text">
									</div>
								</div>	
								
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span12" style="padding: 0 18px 0">
							<!-- <div id="recruitRoundSubjPartyGrid"></div> -->
							<div id="recruitCandidateExaminerGrid"></div>
						</div>
					</div>
					<div class="row-fluid" style="margin-top: 15px">
						<div class="span12" style="padding: 0 18px 0">
							<div class='row-fluid margin-bottom10'>
								<div class='span2'>
									<label class="">${uiLabelMap.HRCommonComment}</label>
								</div>
								<div class="span10">
									<textarea id="commentStoreInterview"></textarea>
								</div>
							</div>	
						</div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingUpdateCandidateResult" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerUpdateCandidateResult"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button id="cancelStoreInterview" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button id="saveStoreInterview" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="contextMenuRoundRec" style="z-index: 20000 !important" class="hide">
		<ul>
	         <li id="candidateInterviewScheduling"><i class="fa-calendar"></i>${uiLabelMap.CandidateInterviewScheduling}</li>
	         <!-- <li id="examResultBatchUpdate"><i class="fa-pencil-square-o"></i>${uiLabelMap.ExamResultUpdate}</li> -->
	     </ul>
	</div>
	
	<div id="contextMenuRoundRecParty" style="z-index: 20000 !important" class="hide">
		<ul>
	         <li action="storeInterviewRec" id="storeInterviewRec"><i class="fa-file-image-o"></i>${uiLabelMap.RecruitmentStoreInterview}</li>
	         <li action="agreePassRecruitment" id="agreePassRecruitment"><i class="fa-thumbs-o-up"></i>${uiLabelMap.AgreeToPassRecruitment}</li>
	         <li action="receiveCandidate" id="receiveCandidate"><i class="fa-user"></i>${uiLabelMap.ReceiveCandidate}</li>
	     </ul>
	</div>

	<div id="contextMenuExamierEval" style="z-index: 20000 !important" class="hide">
		<ul>
	         <li action="viewDetail" id="viewDetailEvalCandidate"><i class="fa fa-search"></i>${uiLabelMap.ViewDetails}</li>
	     </ul>
	</div>
	
	<#include "recruitmentCalendarSchedule.ftl">
	<#include "recruitmentReceiveCandidate.ftl"/>
	<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentStoreInterviewOfCandidate.js"></script>
	<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentListRoundContextMenu.js"></script>
	${setContextField("editable", "false")}
	<#include "RecruitEvalutedCandidateTestRound.ftl"/>
	<#include "RecruitEvalutedCandidateINTVWRound.ftl"/>
</#if>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitListRound.js"></script>