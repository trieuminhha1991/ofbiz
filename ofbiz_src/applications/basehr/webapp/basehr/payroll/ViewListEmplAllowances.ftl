<#include "script/ViewListEmplAllowancesScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'emplPositionTypeDes', type: 'string'},
						{name: 'department', type: 'string'},
						{name: 'periodTypeId', type: 'string'},
					   "/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '13%', cellsalign: 'left', editable: false},
					    {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: '15%', cellsalign: 'left', editable: false},
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDes', width: '20%'},"/>	
<#assign totalWidth = 100-(13+15+20+20)/>
<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>
<div class="row-fluid">
	<ul class="nav nav-tabs" id="recent-tab">
		<#if allowanceParamPeriodList?has_content>
			<#list allowanceParamPeriodList as periodTypeId>
				<#assign periodType = delegator.findOne("PeriodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", periodTypeId), true)/>
				<li class="<#if periodTypeId_index == 0>active</#if>">
					<a data-toggle="tab" href="#allowance_${periodTypeId}" aria-expanded="true">
						${StringUtil.wrapString(uiLabelMap.HREmplAllowances)} ${StringUtil.wrapString(periodType.description)}
					</a>
				</li>
			</#list>
		</#if>
	</ul>
	<div class="tab-content overflow-visible" style="border: none !important; padding: 16px 0">
		<#if allowanceParamPeriodList?has_content>
			<#list allowanceParamPeriodList as periodTypeId>
				<#assign periodType = delegator.findOne("PeriodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", periodTypeId), true)/>
				<#assign tempAllowancesParamList = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(allowancesParamList, 
																		Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", periodTypeId))/> 
				<div id="allowance_${periodTypeId}" class="tab-pane <#if periodTypeId_index == 0>active</#if>">
					<#assign tempDatafield = datafield/>
					<#assign tempColumnlist = columnlist/>
					<#assign columnWidth = totalWidth / tempAllowancesParamList?size>
					<#if  (columnWidth < 12)>
						<#assign columnWidth = 12/>
					</#if>
					<#list tempAllowancesParamList as allowancesParam>
						<#assign tempDatafield = tempDatafield + "{name: 'allowances_${allowancesParam.code}', type: 'number'},"/>	
						<#assign tempColumnlist = tempColumnlist + "{text: '${StringUtil.wrapString(allowancesParam.name)}', datafield: 'allowances_${allowancesParam.code}', filterable: false,
																columntype: 'numberinput', width: '${columnWidth}%',
																cellsrenderer: function(row, column, value){
																	if(typeof(value) == 'number'){
																		return '<span style=\"text-align: right\">' + formatcurrency(value) + '<span>';
																	}
																}
															},"/> 
					</#list>
					<#assign tempDatafield = tempDatafield + "]"/>
					<#assign gridId = "grid${periodTypeId}"/>
					<#if periodTypeId == "DAILY">
						<#assign customcontrolAdvance = "<div id='date_${periodTypeId}' style='display: inline-block; margin-right: 5px'></div>"/>
					<#elseif periodTypeId == "YEARLY">
						<#assign customcontrolAdvance = "<div id='year_${periodTypeId}' style='display: inline-block; margin-right: 5px'></div>"/>
					<#else>
						<#assign customcontrolAdvance = "<div id='month_${periodTypeId}' style='display: inline-block; margin-right: 5px'></div><div id='year_${periodTypeId}' style='display: inline-block;'></div>"/>
					</#if>
					<@jqGrid url="" dataField=tempDatafield columnlist=tempColumnlist
						editable="false" showlist="true" sortable="true" id=gridId
						showtoolbar="true" deleterow="false" jqGridMinimumLibEnable="false"
						addrow="true" addType="popup" alternativeAddPopup="AddEmplAllowancesWindow"
						customControlAdvance=customcontrolAdvance mouseRightMenu="true" contextMenuId="contextMenu"
						/>
				</div>
			</#list>
		</#if>
	</div>
</div>
<div id="contextMenu" class="hide">
	<ul>
		<li action="viewDetail">
			<i class="fa fa-search-plus"></i>${uiLabelMap.CommonDetail}
        </li>
	</ul>
</div>
<div id="viewEmplAllowanceWindow" class="hide">
	<div>${uiLabelMap.AllowanceDetails}</div>
	<div class="form-window-container">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeId)}</label>
						</div>
						<div class="span8">
							<input type="text" id="partyCodeView">
							<!-- <button id="searchPartyCodeBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini">
								<i class="icon-only icon-search open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button> -->
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${StringUtil.wrapString(uiLabelMap.EmployeeName)}</label>
						</div>
						<div class="span8">
							<input type="text" id="fullNameView">
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<div id="containerviewEmplAllowanceGrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
			</div>
			<div id="jqxNotificationviewEmplAllowanceGrid">
			    <div id="notificationContentviewEmplAllowanceGrid">
			    </div>
			</div>
			<div id="viewEmplAllowanceGrid"></div>
		</div>
	</div>	
</div>
<#include "AddEmplAllowance.ftl"/>
<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmplAllowances.js"></script>		
		