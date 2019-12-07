<div id="addNewRecruitmentBoard" class="hide">
	<div>${uiLabelMap.AddRecruitmentBoard}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRFullName}</label>
				</div>
				<div class='span8'>
					<input type="text" id="partyIdBoard">
					<button id="searchPartyNewBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini btn-primary">
						<i class="icon-only icon-search open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button>
				</div>					
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRCommonJobTitle}</label>
				</div>
				<div class='span8'>
					<input type="text" id="jobTitle">
				</div>					
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.CommonRole}</label>
				</div>
				<div class='span8'>
					<input type="text" id="roleDescription">
				</div>					
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelBoard" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveBoard" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button id="saveAndContinueBoard" type="button" class="btn btn-success form-action-button pull-right" ><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="addNewRecruitmentRound" class="hide">
	<div>${uiLabelMap.AddNewRecruitmentRound}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RoundOrder}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="roundOrderNew"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.CommonType}</label>
									</div>
									<div class='span8'>
										<div id="roundTypeEnumNew"></div>
									</div>	
								</div>
							</div>
						</div>
					</div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RoundName}</label>
				</div>
				<div class='span8'>
					<input id="roundNameNew" type="text">
				</div>	
			</div>
			
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.RecruitmentInterviewerMarker}</label>
				</div>
				<div class='span8'>
					<div id="interviewMarkerNew"></div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<input id="commentRoundNew" type="text">
				</div>	
			</div>
			<div class="row-fluid">
				<div id="recruitmentRoundSubjectGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancelRound" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSaveRound" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button id="saveAndContinueRound" type="button" class="btn btn-success form-action-button pull-right" ><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<div id="recruitmentSubjectListWindow" class="hide">
	<div>${uiLabelMap.RecruitmentSubject}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerrecruitSubjectListGrid"></div>
			<div id="jqxNotificationrecruitSubjectListGrid">
		        <div id="notificationContentrecruitSubjectListGrid">
		        </div>
		    </div>
			<div id="recruitSubjectListGrid"></div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelSelectSubject" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveSelectSubject" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSelect}</button>
				</div>
			</div>
		</div>
	</div>	
</div>

<#include "RecruitCreateRecruitPlanBaseRequirement.ftl"/>
<#include "RecruitCreateRecruitPlanNotBaseRequirement.ftl"/>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitmentPlan.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitRound.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitSubject.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitmentPlanBoard.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitCost.js"></script>
<#--<!-- <script type="text/javascript" src="/hrresources/js/recruitment/RecruitPreliminarySelConds.js"></script> -->
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrg"));
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>