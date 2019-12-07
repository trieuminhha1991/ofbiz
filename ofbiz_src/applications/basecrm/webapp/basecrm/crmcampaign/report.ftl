<div class="widget-box">
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4 class="smaller"><i class="fa fa-users"></i>&nbsp;${uiLabelMap.ContactInformation}</h4>
	</div>
	<div class="widget-body no-padding-top" id="memberUsingCollapse" style="height: auto">
		<div class="widget-main">
			<button id="UpdateContactCampaignReport" class="update-top-corner"><i class="fa fa-refresh">&nbsp;</i></button>
			<div class="report-context">
				<h3>${uiLabelMap.Total}</h3>
				<p class="contact-number" id="totalContact"></p>
			</div>
			<div class="row-fluid padding-bottom10">
				<div class="span6">
					<div id="AssignedContactChart"></div>
					<div class="row-fluid">
						<div class="span6 no-left-margin report-context">
							<h3>${uiLabelMap.Assigned}</h3>
							<p class="contact-number" id="assignedCon"></p>
						</div>
						<div class="span6 no-left-margin report-context">
							<h3>${uiLabelMap.NotAssigned}</h3>
							<p class="contact-number" id="notAssignedContact"></p>
						</div>
					</div>
				</div>
				<div class="span6">
					<div id="CompletedContactChart"></div>
					<div class="row-fluid">
						<div class="span6 no-left-margin report-context">
							<h3>${uiLabelMap.Completed}</h3>
							<p class="contact-number" id="completedContact"></p>
						</div>
						<div class="span6 no-left-margin report-context">
							<h3>${uiLabelMap.NotCompleted}</h3>
							<p class="contact-number" id="notCompletedContact"></p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="widget-box margin-top15">
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4 class="smaller"><i class="fa fa-navicon"></i>&nbsp;${uiLabelMap.AssignedAgent}</h4>
	</div>
	<div class="widget-body no-padding-top" id="memberUsingCollapse" style="height: auto">
		<div class="widget-main">
			<#assign dataField="[
						{ name: 'partyId', type: 'string' },
						{ name: 'partyCode', type: 'string' },
						{ name: 'partyFullName', type: 'string' },
						{ name: 'total', type: 'string' },
						{ name: 'completed', type: 'string' },
						{ name: 'uncompleted', type: 'string' }]"/>

			<#if campaign?exists && campaign.marketingCampaignId?exists>
			<#assign marketingCampaignId = campaign.marketingCampaignId/>
			<#else>
			<#assign marketingCampaignId = ""/>
			</#if>

			<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyId)}', datafield: 'partyCode', width: 150 },
								{ text: '${StringUtil.wrapString(uiLabelMap.PersonCommunicate)}', datafield: 'partyFullName', minWidth: 250 },
								{ text: '${StringUtil.wrapString(uiLabelMap.Total)}', datafield: 'total', width: 200, cellsalign: 'right' },
								{ text: '${StringUtil.wrapString(uiLabelMap.Completed)}', datafield: 'completed', width: 200, cellsalign: 'right' },
								{ text: '${StringUtil.wrapString(uiLabelMap.NotCompleted)}', datafield: 'uncompleted', width: 200, cellsalign: 'right' }"/>

			<@jqGrid id="ListEmployeeContact" dataField=dataField columnlist=columnlist isShowTitleProperty="false" refreshbutton="true"
				filterable="false" sortable="false" jqGridMinimumLibEnable="false" customLoadFunction="true"
				url="jqxGeneralServicer?sname=GetEmployeeContactReport&marketingCampaignId=${marketingCampaignId}"/>
		</div>
	</div>
</div>