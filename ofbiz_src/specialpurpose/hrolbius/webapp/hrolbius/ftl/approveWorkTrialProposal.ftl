<div id="page-content" class="page-content clearfix">
<div id="screenlet_1" class="widget-box transparent no-bottom-border">
	<div class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
		<h4>
			${uiLabelMap.PageTitleApproveWorkTrialProposal}
		</h4>
		<br class="clear"/>
	</div>
	<div class="widget-body">
		<div class="widget-body-inner" id="screenlet_1_col">
			<form name="ApproveWorkTrialProposal" onsubmit="javascript:submitFormDisableSubmits(this)" class="basic-form form-horizontal" id="ApproveWorkTrialProposal" action="<@ofbizUrl>approveWorkTrialProposal</@ofbizUrl>" method="post">
    			<div class="row-fluid">
  					<input type="hidden" name="workTrialProposalId" value="${parameters.workTrialProposalId}" />
  					<input type="hidden" name="ntfId" value="${parameters.ntfId}" />
  					<input type="hidden" name="workingPartyId" value="${workTrial.workingPartyId?if_exists}" />
  					<input type="hidden" name="emplPositionTypeId" value="${workTrial.emplPositionTypeId?if_exists}" />
  					<input type="hidden" name="salary" value="${workTrial.salary?if_exists}" />
  					<input type="hidden" name="allowance" value="${workTrial.allowance?if_exists}" />
  					<input type="hidden" name="trialSalaryRate" value="${workTrial.trialSalaryRate?if_exists}" />
  					<input type="hidden" name="fromDate" value="${workTrial.fromDate?if_exists}" />
  					<input type="hidden" name="trialTime" value="${workTrial.trialTime?if_exists}" />
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.DeptName}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							  <#assign org = delegator.findOne("PartyGroup", {"partyId" : workTrial.workingPartyId?if_exists}, true)>
 								${org.groupName}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Position}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							 <#assign emplPositionType = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : workTrial.emplPositionTypeId?if_exists}, true)>
  							${emplPositionType.description?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Salary}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${workTrial.salary?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Allowance}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${workTrial.allowance?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.TrailSalaryRate}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${workTrial.trialSalaryRate?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.FromDate}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${workTrial.fromDate?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.trialTime}</label>  
						</label>
  						<div class="controls">
  						 	&nbsp;
  							${workTrial.trialTime?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Description}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${workTrial.description?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Applicant}</label>  
						</label>
  						<div class="controls">
  							<#list trialEmplList as empl>
  								 &nbsp; ${empl.firstName?if_exists} ${empl.middleName?if_exists} ${empl.lastName?if_exists}<br/>
  							</#list>
  							<select multiple="multiple" style="display: none;" name="applicantList" id="applicantList">
  								<#list trialEmplList as empl>
  									<option value=${empl.partyId} selected/>
  								</#list>
  							</select>
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Status}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							<select name="statusId" >
							<#list approvalStatusList as status>
								<option value="${status.statusId?if_exists}">
									${status.description?if_exists}
								</option>
							</#list>
							</select>
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >&nbsp;</label>
						</label>
  						<div class="controls">
  							 &nbsp;
  							<button name="submit" type="submit" class="btn btn-success btn-small">
								<i class="icon-ok" ></i>
								${uiLabelMap.CommonSubmit}
							</button>
 						</div>
  					</div>
				</div>
			</form>
		</div>
	</div>
	</div>
</div>
