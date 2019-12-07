<div class="row-fluid">
	<div class="widget-body">
		<#if partyPayrollTableList?has_content>
			<form action="<@ofbizUrl>approvePayroll</@ofbizUrl>" method="post">
				<input type="hidden" name="payrollTableId" value="${parameters.payrollTableId}">
				<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
				<#assign commonUrl = "ApprovalPayrollTable?${paramList}" + "&"/>
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
			    <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
			    	<thead>
			    		<tr>
			    			<th>${uiLabelMap.EmployeeName}</th>
			    			<th>${uiLabelMap.CommonIdCode}</th>
			    			<th>${uiLabelMap.HrolbiusAmountValue}</th>
			    			<th>${uiLabelMap.CommonFromDate}</th>
			    			<th>${uiLabelMap.CommonThruDate}</th>
			    			<th>${uiLabelMap.CommonStatus}</th>
			    		</tr>
			    	</thead>
			    	<tbody>
			    		<#list partyPayrollTableList as partyPayrollTable>
			    			<tr>
			    				<td>
			    				<#assign party = delegator.findOne("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyPayrollTable.partyId), false)>
			    				${party.firstName?if_exists} ${party.middleName?if_exists} ${party.lastName?if_exists}
			    				
			    				<input type="hidden" name="partyId_o_${partyPayrollTable_index}" value="${partyPayrollTable.partyId}">
			    				</td>
			    				<td>
			    					<#assign code = delegator.findOne("PayrollFormula", Static["org.ofbiz.base.util.UtilMisc"].toMap("code", partyPayrollTable.code), false)>
			    					${code.name}
			    					<input type="hidden" name="code_o_${partyPayrollTable_index}" value="${partyPayrollTable.code}">	
			    				</td>
			    				<td>
			    					${Static["java.lang.Float"].parseFloat(partyPayrollTable.value)}
			    				</td>
			    				<td>
			    					${partyPayrollTable.fromDate?string["dd/MM/yyyy"]}
			    					<input type="hidden" name="fromDate_o_${partyPayrollTable_index}" value="${partyPayrollTable.fromDate}">
			    				</td>
			    				<td>
			    					${partyPayrollTable.thruDate?string["dd/MM/yyyy"]}
			    					<input type="hidden" name="thruDate_o_${partyPayrollTable_index}" value="${partyPayrollTable.thruDate}">
			    				</td>
			    				<td>
			    					<select name="statusId_o_${partyPayrollTable_index}">
			    						<#list statusList as status>
			    							<option value="${status.statusId}">${status.description}</option>
			    						</#list>
			    					</select>
			    				</td>
			    			</tr>
			    		</#list>
			    	</tbody>
			    </table>
			    <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
			    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
			    <button type="submit" class="btn btn-small btn-primary" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
			</form>		    		
		<#else>
			<div>
	            <p class="alert alert-info">${uiLabelMap.PayrollTableHaveCreatedInvoice} ${uiLabelMap.CommonOr}</p>
	        </div>	
		</#if>
	</div>
</div>