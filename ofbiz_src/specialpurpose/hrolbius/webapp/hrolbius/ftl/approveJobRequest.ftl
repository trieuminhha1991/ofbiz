<div id="page-content" class="page-content clearfix">
<div id="screenlet_1" class="widget-box transparent no-bottom-border">
	<div class="widget-header widget-header-blue widget-header-flat wi1dget-header-large">
		<h4>
			${uiLabelMap.PageTitleApproveJobRequest}
		</h4>
		<br class="clear"/>
	</div>
	<div class="widget-body">
		<div class="widget-body-inner" id="screenlet_1_col">
			<form name="EditJobRequest" onsubmit="javascript:submitFormDisableSubmits(this)" class="basic-form form-horizontal" id="EditJobRequest" action="<@ofbizUrl>approveJobRequest</@ofbizUrl>" method="post">
    			<div class="row-fluid">
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.DeptName}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${deptName?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Position}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${emplPositionType.description?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.FromDate}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${jobRequest.fromDate?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.ThruDate}</label>  
						</label>
  						<div class="controls">
  						 	&nbsp;
  							${jobRequest.thruDate?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.ResourceNumber}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${jobRequest.resourceNumber?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.RecruitmentType}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${recruitmentType.description?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.RecruitmentForm}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${recruitmentForm.description?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.OtherRequirement}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${jobRequest.otherRequirement?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.JobDescription}</label>  
						</label>
  						<div class="controls">
  							 &nbsp;
  							${jobRequest.jobDescription?if_exists}
 						</div>
  					</div>
  					
  					<div class="control-group no-left-margin">
  						<label class="">
							<label >${uiLabelMap.Criteria}</label>  
						</label>
  						<div class="controls">
  							<#list criteriaList as criteria> 
								&nbsp;${criteria?if_exists}<br/>
							</#list>
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
  					<input type="hidden" name = "ntfId" value="${ntfId?if_exists}"/>
  					<input type="hidden" name = "jobRequestId" value="${jobRequestId?if_exists}"/>
  					<input type="hidden" name = "targetLink" value="${targetLink?if_exists}"/>
  					<input type="hidden" name = "header" value="${header?if_exists}"/>
  					<input type="hidden" name = "notiToId1" value="${notiToId1?if_exists}"/>
					<input type="hidden" name = "notiToId2" value="${notiToId2?if_exists}"/>
				</div>
			</form>
		</div>
	</div>
	</div>
</div>
