
<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
	    <#assign commonUrl = "/delys/control/lookupBudgetPromotion?presentation=layer&" + paramList + "&"/>
	    <#assign viewIndexFirst = 0/>
	    <#assign viewIndexPrevious = viewIndex - 1/>
	    <#assign viewIndexNext = viewIndex + 1/>
	    <#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
	    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
	    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
	    <@nextPrev commonUrl=commonUrl ajaxEnabled=false 
	     			javaScriptEnabled=false 
	     			paginateStyle="dataTables_paginate paging_bootstrap pagination" 
	     			paginateFirstStyle="nav-first" 
	     			viewIndex=viewIndex 
	     			highIndex=highIndex 
	     			listSize=listSize 
	     			viewSize=viewSize ajaxFirstUrl="" firstUrl="" 
	     			paginateFirstLabel="" 
	     			paginatePreviousStyle="nav-previous" 
	     			ajaxPreviousUrl="" previousUrl=""
	     			paginatePreviousLabel="" pageLabel="" 
	     			ajaxSelectUrl="" selectUrl="" 
	     			ajaxSelectSizeUrl="" selectSizeUrl="" 
	     			commonDisplaying=commonDisplaying paginateNextStyle="nav-next" 
	     			ajaxNextUrl="" nextUrl="" paginateNextLabel="" 
	     			paginateLastStyle="nav-last" ajaxLastUrl="" 
	     			lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />

<table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
	<thead>
		<tr>
			<td>${uiLabelMap.BudgetId}</td>
			<td>${uiLabelMap.budgetTypeId}</td>
			<td>${uiLabelMap.comments}</td>
			<td>${uiLabelMap.amount}</td>
			<td>${uiLabelMap.productId} - ${uiLabelMap.quantity}</td>			
		</tr>
	</thead>
	<tbody>
		<#if listIt?has_content>
		<#list listIt as budget>
			<#assign budgetAmount = delegator.findByAnd("BudgetAndTotalAmount", Static["org.ofbiz.base.util.UtilMisc"].toMap("budgetId", budget.budgetId), null, false)>
			<#assign budgetProductQty = delegator.findByAnd("BudgetAndTotalProductQty", Static["org.ofbiz.base.util.UtilMisc"].toMap("budgetId", budget.budgetId), null, false)>
			<tr>
				<td>
					<a class="btn btn-mini btn-info" href="javascript:set_value('${budget.budgetId}')">${budget.budgetId}</a>
				</td>
				<td>${budget.budgetTypeId}</td>
				<td>${budget.comments?if_exists}</td>
				<td>
					<#if budgetAmount?has_content>
						${budgetAmount[0].amount?if_exists}	
					</#if>
					
				</td>
				<td>
					<#if budgetProductQty?has_content>
						<#list budgetProductQty as productQty>
							<#if productQty.productId?exists>
								${productQty.productId}: ${productQty.quantity?if_exists}<br/>
							</#if>
						</#list>
					</#if>
				</td>
			</tr>
		</#list>
		</#if>
	</tbody>
</table>					     			