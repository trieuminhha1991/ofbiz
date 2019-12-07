<div id="page-content" class="page-content clearfix">
<div id="screenlet_1" class="widget-box transparent no-bottom-border">
	<div class="widget-body">
		<div class="widget-body-inner" id="screenlet_1_col">
				<div class="row-fluid">
					<table class = "table table-striped table-hover table-bordered dataTable" cellspacing="0">
					<tr>
						<td>${uiLabelMap.ListProposedEmpl}</td>
						<td>${uiLabelMap.HRolbiusEmployeeName}</td>
						<td>Cap nhat</td>
					</tr>
					<#assign listEmpl=delegator.findList("EmplTrainingProposal",null,null,null,null,false)>
					<#list listEmpl as empl>
					<form name="EditVotesTrainingPersonal_${tcl_index}" class="basic-form form-horizontal" id="EditVotesTrainingPersonal" action="<@ofbizUrl>EditVotesTrainingPersonal</@ofbizUrl>" method="post"> 
						<input type="hidden" name="trainingProposalId" value="${tcl.trainingProposalId}"/>
						<tr>
							<td >
							<i class="btn btn-info btn-mini">
							<input type="hidden" name="partyId" value="${empl.partyId}"/>
								${empl.partyId?if_exists}				
							</i>
							</td>
							<td>
								<#assign name = delegator.findOne("Person",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",empl.partyId),false)>	
								<#if name?exists>
								${name.get("lastName")?if_exists}	 ${name.get("middleName")?if_exists}  ${name.get("firstName")?if_exists}
								</#if>			
							</td>
							<td>
								<button class="btn btn-success btn-small">
									<i class=" icon-ok" ></i>
									${uiLabelMap.CommonUpdate}
								</button>
							
							</td>
						</tr>
					</form>
					</#list>
				</table>
  				
  				</div>
			
		</div>
	</div>
	</div>
</div>
