<#if hasViewProposalPermission?exists && hasViewProposalPermission>
	<script type="text/javascript">
		var skillTypeList = new Array();
		<#list skillTypeList as skillType>
			var row ={};
			row["skillTypeId"] = "${skillType.skillTypeId}";
			row["description"] = "${StringUtil.wrapString(skillType.description)}";
			skillTypeList[${skillType_index}] = row;
		</#list>
		
		var statusSkillList = new Array();
		<#list statusList as status>
			var row ={};
			row["statusId"] = "${status.statusId}";
			row["description"] = "${status.description}";
			statusSkillList[${status_index}] = row;
		</#list>
		
		var sourceStatusType = {
		        localdata: statusSkillList,
		        datatype: "array"
		    };		
		var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusType, {autoBind: true});
	    var dataSoureList = filterBoxAdapter.records;
		
		<#assign datafield="[{name:'trainingCourseId', type:'string'},
		                     {name: 'skillTypeId', type:'string'},
		                     {name: 'requiredLevelStatusId', type: 'string'}]"/>
		<#assign columnlist ="{text: '${uiLabelMap.TrainingCourseId}', datafield: 'trainingCourseId' ,filtertype: 'input', editable: false, cellsalign: 'left', hidden: true},
							  {text: '${uiLabelMap.SkillTypeName}', datafield: 'skillTypeId', columntype: 'custom', filtertype: 'checkedlist', cellsalign: 'left',width: '45%',
									cellsrenderer: function (row, column, value){
										for(var i = 0; i < skillTypeList.length; i++){
											if(skillTypeList[i].skillTypeId == value){
												return '<div style=\"\">' + skillTypeList[i].description + '</div>';		
											}
										}
									},
									createfilterwidget: function(column, columnElement, widget){
										var sourceSkillType = {
									        localdata: skillTypeList,
									        datatype: \"array\"
									    };		
										var filterBoxAdapter = new $.jqx.dataAdapter(sourceSkillType, {autoBind: true});
									    var dataSoureList = filterBoxAdapter.records;
									    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
									    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'skillTypeId', valueMember : 'skillTypeId', height: '25px',
									    	autoDropDownHeight: false, searchMode: 'containsignorecase', incrementalSearch: true, filterable:true,
											renderer: function (index, label, value) {
												for(i=0; i < skillTypeList.length; i++){
													if(skillTypeList[i].skillTypeId == value){
														return skillTypeList[i].description;
													}
												}
											    return value;
											}
										});
									    
									}	
								},
								{text: '${uiLabelMap.RequrimentLevelSkillTrainingCourse}', datafield: 'requiredLevelStatusId', columntype: 'custom', filtertype: 'checkedlist', cellsalign: 'left',
									cellsrenderer: function (row, column, value){
										for(var i = 0; i < statusSkillList.length; i++){
											if(statusSkillList[i].statusId == value){
												return '<div style=\"\">' + statusSkillList[i].description + '</div>';		
											}
										}
									},	
									createfilterwidget: function(column, columnElement, widget){
										
									    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
									    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'statusId', valueMember : 'statusId', height: '25px', 
											renderer: function (index, label, value) {
												for(i=0; i < statusSkillList.length; i++){
													if(statusSkillList[i].statusId == value){
														return statusSkillList[i].description;
													}
												}
											    return value;
											}
										});
									    
									}	
								}
								">
	</script>
	<div class="row-fluid">
		<div class="span12">
			<div class="tabbable">
				<ul class="nav nav-tabs padding-18">
					<li class="active">
						<a data-toggle="tab" href="#trainingCourseInfo">
							<i class="green bigger-120"></i>
							${uiLabelMap.TrainingCourseInfomation}
						</a>
					</li>
					<li>
						<a data-toggle="tab" href="#trainingCourseSkillType">
							<i class="orange  bigger-120"></i>
							${uiLabelMap.TrainingCourseSkillType}
						</a>
					</li>
					<li>
						<a data-toggle="tab" href="#trainingCourseTrainees">
							<i class="blue bigger-120"></i>
							${uiLabelMap.TrainingCourseTrainees}
						</a>
					</li>
				</ul>
				<div class="tab-content no-border">
					<div id="trainingCourseInfo" class="tab-pane in active">
						<#include "trainingCourseInfo.ftl">
					</div>
					<div id="trainingCourseSkillType" class="tab-pane">
						<@jqGrid filtersimplemode="true"  dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
							 filterable="true" deleterow="false" editable="false" addrow="false" width="'100%'" bindresize="false"
							 url="jqxGeneralServicer?trainingCourseId=${trainingCourse.trainingCourseId}&hasrequest=Y&sname=JQListTrainingCourseSkillType" id="jqxGridSkillType" removeUrl="" deleteColumn=""
							 updateUrl="" 
							 editColumns=""
						/>
					</div>
					<div id="trainingCourseTrainees" class="tab-pane">
						<div class="form-horizontal">
							<div class="control-group">
								<label class="control-label">${uiLabelMap.EmplPositionTypeTrained}</label>
								<div class="controls">
									<#if trainingCourseEmplPosType?has_content>
										<#list trainingCourseEmplPosTypes as trainingEmplPosType>
											<#assign emplPosType = delegator.findOne("EmplPositionType", Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionTypeId", trainingEmplPosType.emplPositionTypeId), false)>
											${emplPosType.description?if_exists}
											<#if trainingEmplPosType_has_next>
												&nbsp;,
											</#if>
										</#list> 
									<#else>
										${uiLabelMap.HRCommonNotSetting}
									</#if>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.PartyGroupTrained}</label>
								<div class="controls">
									<#if trainingCourseTrainee?has_content>
										<#list trainingCourseTrainee as trainee>
											<#assign party = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", trainee.partyId), false)>
											${party.groupName?if_exists}
											<#if trainee_has_next>
												&nbsp;,
											</#if>
										</#list>
									<#else>
										${uiLabelMap.HRCommonNotSetting}
									</#if>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>		
	<#if allPartyApproval?has_content>
		<hr/>
		<div class="row-fluid">
			<div class="span12 mgt20 boder-all-profile">	
				<span class="text-header">${uiLabelMap.DetailsApproveProposal}</span>				
				<div class="form-horizontal">
					<#list allPartyApproval as tempApproval>
						<#if tempApproval_index%2 == 0 >
							<#if tempApproval_index &gt; 0>
								<hr/>
							</#if>							
							<div class="row-fluid">
								<div class="span12">
						</#if>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.HRApprover}
								</label>
								<div class="controls">
									<#assign partyApprover = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", tempApproval.partyId), false)>
									${partyApprover.lastName?if_exists} ${partyApprover.middleName?if_exists} ${partyApprover.firstName?if_exists}
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.HREmployeePosition}</label>
								<div class="controls">
									${Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, tempApproval.partyId)}
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.CommonStatus}
								</label>
								<div class="controls">
									<#assign currStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", tempApproval.approvalStatusId), false)>
									${currStatus.description?if_exists}
								</div>
							</div>
							<div class="control-group no-left-margin">	
								<label class="control-label">${uiLabelMap.ApprovalDate}</label>
								<div class="controls">
									${tempApproval.approvalDate?string["dd-MM-yyyy"]}
								</div>
							</div>
							<#if tempApproval.comment?exists>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.HRNotes}</label>
									<div class="controls">
										${tempApproval.comment}
									</div>
								</div>
							</#if>
						</div>	
						<#if tempApproval_index%2 == 1 || !tempApproval_has_next>
								</div>
							</div>
						</#if>
					</#list>
				</div>	
			</div>	
		</div>
	</#if>
	<#if hasUpdateProposalPermission?exists && hasUpdateProposalPermission && trainingCourse.statusId == "TRAINING_PLANNED_PPS">
		<hr/>
		<div class="row-fluid">
			<form class="form-horizontal" action="<@ofbizUrl>updateTrainingCourseProposal</@ofbizUrl>" method="post">
				<input type="hidden" name="emplProposalId" value="${emplProposal.emplProposalId}">
				<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}"> 
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HRApprove}</label>
					<div class="controls">
						<select name="approvalStatusId" id="approvalStatusId">
							<#list approvalStatusList as status>
								<option value="${status.statusId}">${status.description}</option> 
							</#list>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HRNotes}</label>
					<div class="controls">
						<input name="comment" type="text">
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-primary btn-small" type="submit">
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</form>
		</div>
	</#if>
<#else>
	<div class="alert alert-info">
		<h4>${uiLabelMap.NotHavePermissionView}</h4>
	</div>	
</#if>