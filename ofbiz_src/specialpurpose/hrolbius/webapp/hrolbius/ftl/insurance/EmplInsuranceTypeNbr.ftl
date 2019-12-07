<#assign partyId = parameters.partyId?if_exists>
<#--<!-- <#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
    <div class="dataTables_paginate paging_bootstrap pagination row-fluid dataTables_paginate paging_bootstrap pagination">
      <#if (viewIndexMax?int > 0)>

            <#if (viewIndex?int > 0)>
                <a href="<@ofbizUrl>ManagerInsuranceNbr/~partyId=${partyId?if_exists}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a>
                <#--<a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int - 1}~${sortOrder}');" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex?int < listSize?int>
              | <a href="<@ofbizUrl>ManagerInsuranceNbr/~partyId=${partyId?if_exists}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
             	<#--| <a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int + 1}~${sortOrder}');" class="buttontext">${uiLabelMap.CommonNext}</a>
            </#if>
 		</#if>
 	</div>
</#macro> -->
<#assign filterByDateCond = Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()>
<div class="row-fluid">
	<div class="widget-body">
		<#if partyList?exists>
			<#if partyList?has_content>
				<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
		<#assign commonUrl = "ManagerInsuranceNbr?" + "&"/>
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
						<tr class="header-row">
							<td class="header-table">${uiLabelMap.PartyName}</td>
							<#list insuranceTypeList as tempInsurance>
								<td>${uiLabelMap.CommonInsuranceNumber} ${uiLabelMap.CommonOf} ${tempInsurance.description}</td>
							</#list>
							<td>${uiLabelMap.CommonEdit}</td>
						</tr>
					</thead>
					<tbody>
						<#list partyList as party>
							<tr>
								<td><a href="<@ofbizUrl>EmployeeProfile?partyId=${party.partyId}</@ofbizUrl>">${party.firstName} ${party.middleName?if_exists} ${party.lastName} [${party.partyId}]</a></td>
								<#assign condition1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", party.partyId)>
								<#list insuranceTypeList as tempInsurance>
									<#assign condition2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("insuranceTypeId", tempInsurance.insuranceTypeId)>
									<#assign conditions = [filterByDateCond, condition1, condition2]>
									<#assign partyInsuranceList = delegator.findList("PartyInsurance", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions, Static["org.ofbiz.entity.condition.EntityOperator"].AND), null, Static["org.ofbiz.base.util.UtilMisc"].toList("-fromDate"), null, false)>								 		 
									<td>
										<#if partyInsuranceList?has_content>
											<#assign partyInsurance = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyInsuranceList)>
											${partyInsurance.insuranceNumber?if_exists}
										</#if>
									</td>
								</#list>
								<td style="text-align: center;">
									<a href="<@ofbizUrl></@ofbizUrl>" class="btn btn-mini btn-primary">
										<i class="icon-edit"></i>
									</a>
								</td>
							</tr>
						</#list> 
					</tbody>
				</table>
				<#-- Pagination -->
			<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
		    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
			<#else>
				<div>
		            <p class="alert alert-info">${uiLabelMap.PartyNoPartiesFound}</p>
		        </div>	
			</#if>
		<#else>
			<div>
	            <p class="alert alert-info">${uiLabelMap.PartyNoPartiesFound}</p>
	        </div>		
		</#if>
	</div>		
</div>