<#include "script/ViewManagerListEmplKPIScript.ftl"/>
<div class="row-fluid">
	<ul class="nav nav-tabs padding-18" id="frequency_tab" >
		<li id="daily_li" class="active">
			<a data-toggle="tab" href="#daily_tab">${uiLabelMap.CommonDaily}</a>
		</li>
		<li id="weekly_li">
			<a data-toggle="tab" href="#weekly_tab">${uiLabelMap.CommonWeekly}</a>
		</li>
		<li id="monthly_li">
			<a data-toggle="tab" href="#monthly_tab">${uiLabelMap.CommonMonthly}</a>
		</li>
		<li id="quarterly_li">
			<a data-toggle="tab" href="#quarterly_tab">${uiLabelMap.Quarterly}</a>
		</li>
		<li id="yearly_li">
			<a data-toggle="tab" href="#yearly_tab">${uiLabelMap.CommonYearly}</a>
		</li>
		<li class="pull-right">
			<button id="removeFilter" class="grid-action-button icon-filter open-sans " style="float: right;font-size : 14px; width :auto;margin-top:9px">${uiLabelMap.HRCommonRemoveFilter}</button>
			<button id="viewListEmplKPI" class="grid-action-button fa-eye open-sans" style="float: right; font-size: 14px">${uiLabelMap.ViewListKPIAssignForEmpl}</button>
			<#if security.hasEntityPermission("HR_KPIPERF", "_ADMIN", session)>
				<button id="assignEmplKPIByPos" class="grid-action-button fa-cogs" style="float: right; font-size: 14px">${uiLabelMap.AssignKPIForEmplByPosition}</button>
			</#if>
		</li>
	</ul>
	<div class="tab-content overflow-visible" style="border: none !important">
		<#include "ViewManagerListEmplKPIDaily.ftl">
	</div>
</div>
<#assign listEmplKPIWindow = "listEmplKPIWindow"/>
<#include "ViewListEmplKPI.ftl"/>
<#if security.hasEntityPermission("HR_KPIPERF", "_ADMIN", session)>
	<#assign setKPIByPosWindow = "setKPIByPosWindow"/>
	<#include "KPISettingByPosition.ftl"/>
</#if>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewManagerListEmplKPI.js"></script>
