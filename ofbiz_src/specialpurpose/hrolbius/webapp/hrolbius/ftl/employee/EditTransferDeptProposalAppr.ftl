<div class="row-fluid">
	<#if hasViewProposalPermission>
		<div class="row-fluid">
			<div class="span12 mgt20 boder-all-profile">
				<span class="text-header">${uiLabelMap.DetailsProposal}</span>
				<div class="form-horizontal">
					<div class="span6">
						<div class="control-group no-left-margin ">
							<label>
								<label>${uiLabelMap.ProposedEmplProposal}</label>
							</label>
							<div class="controls">
								<#if proposedRoleTypeList?has_content>
									<#assign proposedId = proposedRoleTypeList[0].partyId>
									<#assign proposed = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", proposedId), false)>
									${proposed.lastName?if_exists} ${proposed.middleName?if_exists} ${proposed.firstName?if_exists}
								<#else>
									&nbsp;	
								</#if>								
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmployeePosition}
							</label>
							<div class="controls">
								<#if proposed?exists>
									<#assign proposedPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, proposed.partyId)>
									<#if proposedPosition?has_content>
										${proposedPosition}
									<#else>
										${uiLabelMap.NotBelongAnyPosition}
									</#if>
								<#else>
									&nbsp;	 
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.EmployeeCurrentDept}
							</label>
							<div class="controls">
								<#if proposed?exists>
									<#assign currDeptProposed = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, proposed.partyId)>
									<#if currDeptProposed?exists>
										<#assign deptNameProposed = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, currDeptProposed.getString("partyIdFrom"), false)>
										${deptNameProposed}
									<#else>
										${uiLabelMap.NotBelongAnyDepartment}	
									</#if>
								<#else>
									&nbsp;	
								</#if>
							</div>
						</div>
						<#if proposed?exists>
							<#if proposed.partyId != emplProposal.partyId>
								<div class="control-group no-left-margin ">
									<label class="control-label">
										${uiLabelMap.HREmplProposer}
									</label>
									<div class="controls">
										<#assign proposer = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", emplProposal.partyId), false)>
										${proposer.firstName?if_exists} ${proposer.middleName?if_exists} ${proposer.lastName?if_exists}
									</div>
								</div>
								<div class="control-group no-left-margin ">
									<label class="control-label">
										${uiLabelMap.HREmployeePosition}
									</label>
									<div class="controls">
										<#assign emplProposerPosition = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator, emplProposal.partyId)>
										${emplProposerPosition}
									</div>
								</div>
							</#if>
						</#if>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplApprovalCurrStatus}
							</label>
							<div class="controls">
								<#assign jobTransferProposalStatus = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplProposal.statusId), false)>
								${jobTransferProposalStatus.description}
							</div>
						</div>
						
					</div>
					<div class="span6">	
						<div class="control-group no-left-margin ">
							<label class="control-label">
								${uiLabelMap.HREmplDepartmentTo}
							</label>
							<div class="controls">
								<#assign deptToName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, jobTransferProposal.getString("internalOrgUnitToId"), false)>
								${deptToName}
							</div>
						</div>
						<div class="control-group no-left-margin ">
							<label class="control-label">
								${uiLabelMap.HREmployeePositionTo} 
							</label>
							<div class="controls">
								<#assign emplPositionTypeTo = delegator.findOne("EmplPositionType", Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionTypeId", jobTransferProposal.emplPositionTypeToId), false)>
								${emplPositionTypeTo.description?if_exists}
							</div>
						</div>
						<#if jobTransferProposal.dateLeave?exists>
							<div class="control-group no-left-margin ">
								<label class="control-label">
									${uiLabelMap.HREmplDateLeave}
								</label>
								<div class="controls">
									${jobTransferProposal.dateLeave?string["dd-MM-yyyy"]}
								</div>
							</div>
						</#if>
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HREmplDateMoveTo}
							</label>
							<div class="controls">
								${jobTransferProposal.dateMoveTo?string["dd-MM-yyyy"]}
							</div>
						</div>
						<#if jobTransferProposal.reason?exists>
							<div class="control-group no-left-margin">
								<label class="control-label">
									${uiLabelMap.EmployeeTransferReason}
								</label>
								<div class="controls">
									${jobTransferProposal.reason?if_exists}&nbsp;
								</div>
							</div>
						</#if>
					</div>
				</div>
			</div>
		</div>
		<#if allPartyApproval?has_content>
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
		
		<#--<!-- <#assign partyApproverList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(jobTransferApprovalRoleTypeList, "partyId", true)> -->
		<#--<!-- <#if partyApproverList.contains(userLogin.partyId) && !partyJobTransferApproval?has_content> -->		
		<#if hasUpdateProposalPermission?exists && hasUpdateProposalPermission && emplProposal.statusId != "PPSL_ACCEPTED" && emplProposal.statusId != "PPSL_REJECTED">
			<hr/>
			<form method="post" action="<@ofbizUrl>updateJobTransferProposal</@ofbizUrl>" class="basic-form form-horizontal">
				<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
				<input type="hidden" value="${emplProposal.emplProposalId}" name="emplProposalId">
				<div class="row-fluid">
					<div class="span12">
						<div class="control-group no-left-margin">	
							<label class="control-label">
								${uiLabelMap.HRApprove}
							</label>
							<div class="controls">
								<select name="approvalStatusId">
									<#list statusApprovalList as status>
										<option value="${status.statusId}">${status.description}</option>
									</#list>
								</select>
							</div>
						</div>	
						<div class="control-group no-left-margin">
							<label class="control-label">
								${uiLabelMap.HRNotes}
							</label>
							<div class="controls">
								<input type="text" name="comment">
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<button class="btn btn-small btn-primary icon-ok">
									${uiLabelMap.CommonSubmit}
								</button>
							</div>
						</div>
					</div>
				</div>
			</form>	
		</#if>
	<#else>
		<div class="alert alert-info">
			<h4>${uiLabelMap.NotHavePermissionView}</h4>
		</div>
	</#if>
</div>
