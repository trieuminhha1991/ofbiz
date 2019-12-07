<div class="row-fluid">
	<div class="widget-body">
		<#if partyInsuranceReportList?has_content>
			<form action="<@ofbizUrl>createPartyInsurancePayment</@ofbizUrl>" method="post">
				<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
				<#assign commonUrl = "AddEmplInsurancePayment?" + "&"/>
				<#assign viewIndexFirst = 0/>
		   		<#assign viewIndexPrevious = viewIndex - 1/>
		    	<#assign viewIndexNext = viewIndex + 1/>
		    	<#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
			    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
			    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
			    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" 
			    		paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" 
			    		paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" 
			    		ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" 
			    		ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
				<input type="hidden" name="insuranceTypeId" value="${insurancePayment.insuranceTypeId}">
				<input type="hidden" name="insurancePaymentId" value="${parameters.insurancePaymentId}">	    		
				<input type="hidden" name="_useRowSubmit" value="Y">
				<table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
					<thead>
						<tr class="header-row">
							<td class="header-table">
								<input type="checkbox" id="select-all"><span class="lbl" style="margin-top: 0px !important"></span>
							</td>
							<td class="header-table">
								${uiLabelMap.EmployeeName}
							</td>
							<td class="header-table">
								${uiLabelMap.InsuranceReportName}
							</td>
							<td class="header-table">
								${uiLabelMap.InsuranceParticipateType}
							</td>
							<td class="header-table">
								${uiLabelMap.CommonFromDate}
							</td>
							<td class="header-table">
								${uiLabelMap.CommonThruDate}
							</td>
							<td class="header-table">
								${uiLabelMap.SuspendParticipateInsuranceReason}
							</td>
							<td class="header-table">
								${uiLabelMap.HRNotes}
							</td>
						</tr>
					</thead>			
					<tbody>				
						<#list partyInsuranceReportList as tempList>
							<tr>
								<td class="header-table">
									<input type="checkbox" name="_rowSubmit_o_${tempList_index}" value="Y">
									<span class="lbl" style="margin-top: 0px !important"></span>
								</td>
								<td>
									${tempList.partyId?if_exists}
									<input type="hidden" name="partyId_o_${tempList_index}" value="${tempList.partyId?if_exists}">
								</td>
								<td>
									<#assign participateInsuranceReport = delegator.findOne("ParticipateInsuranceReport", Static["org.ofbiz.base.util.UtilMisc"].toMap("reportId", tempList.reportId), false)>
									${participateInsuranceReport.reportName?if_exists}
									<input type="hidden" name="reportId_o_${tempList_index}" value="${tempList.reportId}">
								</td>
								<td>
									${tempList.insuranceParticipateTypeId?if_exists}
									<input type="hidden" name="insuranceParticipateTypeId_o_${tempList_index}" value="${tempList.insuranceParticipateTypeId?if_exists}">
								</td>
								<td>
									<#if tempList.fromDate?exists>
										${tempList.fromDate?string["dd-MM-yyyy"]}
									</#if>
									
								</td>
								<td>
									<#if tempList.thruDate?exists>
										${tempList.thruDate?string["dd-MM-yyyy"]}
									</#if>
									
								</td>
								<td>
									<#if tempList.suspendReasonId?exists>
										<#assign suspendReason = delegator.findOne("SuspendParticipateInsuranceReason", Static["org.ofbiz.base.util.UtilMisc"].toMap("suspendReasonId", tempList.suspendReasonId), false)>
										${suspendReason.description?if_exists}
									</#if>
								</td>
								<td>
									${tempList.comments?if_exists}
								</td>
							</tr>
						</#list> 
					</tbody>
				</table>
				<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
				<@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
				<button type="submit" class="btn btn-small btn-primary" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.CommonAdd}</button>		
			</form>
		<#else>
			<div>
	            <p class="alert alert-info">${uiLabelMap.NotPartySuitableWithInsurancePayment}</p>
	        </div>	
		</#if>
	</div>
</div>
<script type="text/javascript">
jQuery(function() {
	$('#select-all').click(function(event) {
	  if(this.checked) {
	      // Iterate each checkbox
	      $(':checkbox').each(function() {
	          this.checked = true;
	      });
	  }
	  else {
	    $(':checkbox').each(function() {
	          this.checked = false;
	      });
	  }
	});
});
</script>