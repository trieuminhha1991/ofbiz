<div id="page-content" class="page-content clearfix">
<div id="screenlet_1" class="widget-box transparent no-bottom-border">
	<div class="widget-body">
		<div class="widget-body-inner" id="screenlet_1_col">
				<div class="row-fluid">
				<#if parameters.checkPropose?exists>
				<#if parameters.checkPropose="CHECK_PROPOSE">
				<input type="hidden" name="approvalCheck" value="CHECK_PROPOSE"/>
  					<table class = "table table-striped table-hover table-bordered dataTable" cellspacing="0">
  						<tr>
  							<td>${uiLabelMap.HROlbiustrainingProposalId}</td>
  							<td>${uiLabelMap.HrolbiusDepartment}</td>
  							<td>${uiLabelMap.HROlbiusTrainingContent}</td>
  							<td>${uiLabelMap.HROlbiusFromDate}</td>
  							<td>${uiLabelMap.HROlbiusThruDate}</td>
  							<td>${uiLabelMap.ListProposedEmpl}</td>
  							<td>${uiLabelMap.HROlbiusTrainingForm}</td>
  							<td>${uiLabelMap.HROlbiusTrainingType}</td>
  							<td>${uiLabelMap.HROlbiusTypePropose}</td>
							<td>${uiLabelMap.HROlbiusExpense}</td>  							
							<td>${uiLabelMap.HROlbiusTrainingStatus}</td>
							<td>Status</td>
  						</tr>
  				
  						<#list proposeList as pro>
  						
  					<form name="CheckProposeTraining_${pro_index}" class="basic-form form-horizontal" id="CheckProposeTraining" action="<@ofbizUrl>CheckProposeTraining</@ofbizUrl>" method="post">
    					<#if pro.statusId="CREATE_PRO_TRAINING">
    					<tr>
    						<input type="hidden" name="trainingProposalId" value="${pro.trainingProposalId}">
  							<td>${pro.trainingProposalId}</td>
  							<td>${pro.partyId}</td>
  							<td>${pro.trainingContentId}</td>
  							<td>${pro.fromDate}</td>
	  						<td>${pro.thruDate}</td>
	  					<#assign emplIdList = delegator.findList("EmplTrainingProposal",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("trainingProposalId",pro.trainingProposalId),null,null,null,false)>
	  						<td>
	  					<#list emplIdList as job>	
	  						- ${job.partyId} <br>
	  					</#list>
	  						</td>		
	  						<td>${pro.trainingFormId}</td>
	  						<td>${pro.trainingTypeId}</td>
	  						<td>${pro.typeProposeId}</td>
	  						
	  						<td>${pro.expense?if_exists}</td>
	  						<td>
	  							<select name="statusId">
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","CREATE_PRO_TRAINING"),null,null,null,false)>
	  								<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  								</#list>
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","REJECT_PRO_TRAINING"),null,null,null,false)>
	  								<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  								</#list>
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","AGRE_PRO_TRAINING"),null,null,null,false)>
	  							<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  							</#list>
	  							</select>
	  						</td>
	  						<td width="10%">
	  							<button class="btn btn-success btn-small"  type="submit">
											 <i class = "icon-ok" ></i>
	              							${uiLabelMap.CommonSubmit}
	          					</button>
	  						</td>
  						</tr>
  						</#if>
  						</form>
  						</#list>
  					
  					</table>
  					
  					</#if>	
  					</#if>
  					<#if parameters.approvalCheck?exists>
  					<#if parameters.approvalCheck="APPROVAL_PRO_TRAINING">
  						<table class = "table table-striped table-hover table-bordered dataTable" cellspacing="0">
  						<tr>
  							<td>${uiLabelMap.HROlbiustrainingProposalId}</td>
  							<td>${uiLabelMap.HrolbiusDepartment}</td>
  							<td>${uiLabelMap.HROlbiusTrainingContent}</td>
  							<td>${uiLabelMap.HROlbiusFromDate}</td>
  							<td>${uiLabelMap.HROlbiusThruDate}</td>
  							<td>${uiLabelMap.ListProposedEmpl}</td>
  							<td>${uiLabelMap.HROlbiusTrainingForm}</td>
  							<td>${uiLabelMap.HROlbiusTrainingType}</td>
  							<td>${uiLabelMap.HROlbiusTypePropose}</td>
							<td>${uiLabelMap.HROlbiusExpense}</td>  							
							<td>${uiLabelMap.HROlbiusTrainingStatus}</td>
							<td>Status</td>
  						</tr>
  						
  						
  						<#list proposeList as pro>
  						
  					<form name="CheckProposeTraining_${pro_index}" class="basic-form form-horizontal" id="CheckProposeTraining" action="<@ofbizUrl>CheckProposeTraining</@ofbizUrl>" method="post">
    					<#if pro.statusId="AGRE_PRO_TRAINING">
    					<tr>
    						<input type="hidden" name="trainingProposalId" value="${pro.trainingProposalId}">
  							<td>${pro.trainingProposalId}</td>
  							<td>${pro.partyId}</td>
  							<td>${pro.trainingContentId}</td>
  							<td>${pro.fromDate}</td>
	  						<td>${pro.thruDate}</td>
	  					<#assign emplIdList = delegator.findList("EmplTrainingProposal",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("trainingProposalId",pro.trainingProposalId),null,null,null,false)>
	  						<td>
	  					<#list emplIdList as job>	
	  						- ${job.partyId} <br>
	  					</#list>
	  						</td>		
	  						<td>${pro.trainingFormId}</td>
	  						<td>${pro.trainingTypeId}</td>
	  						<td>${pro.typeProposeId}</td>
	  						
	  						<td width="10%">
	  							<input type="text" name="expense" style="width:80px"/>
	  						</td>
	  						<td>
	  							<select name="statusId">
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","APT_APPROVED"),null,null,null,false)>
	  								<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  								</#list>
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","INPRO_PRO_TRAINING"),null,null,null,false)>
	  								<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  								</#list>
	  							<#assign listStatus = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId","APT_CANCELED"),null,null,null,false)>
	  							<#list listStatus as status>
	  								<option value="${status.statusId}">
		  									${status.description?if_exists}
		  							</option>
	  							</#list>
	  							</select>
	  						</td>
	  						<td width="10%">
	  							<button class="btn btn-success btn-small"  type="submit">
											 <i class = "icon-ok" ></i>
	              							${uiLabelMap.CommonSubmit}
	          					</button>
	  						</td>
  						</tr>
  						</#if>
  						</form>
  						</#list>
  					
  					</table>
  					</#if>
  					</#if>
  				</div>
			
		</div>
	</div>
	</div>
</div>
