<@jqGridMinimumLib />
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcombobox2.full.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxslider.js"></script>
<script>
	var isDeleted = <#if campaign?exists && campaign.statusId?exists && campaign.statusId="MKTG_CAMP_DELETED">true<#else>false</#if>;
	var uiLabelMap = {
		UpdateSuccessfully : "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully?default(''))}",
		UpdateError : "${StringUtil.wrapString(uiLabelMap.UpdateError?default(''))}",
		CommonRequired : "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}",
		FieldRequired : "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired?default(''))}",
		ConfirmCreateCampaign: "${StringUtil.wrapString(uiLabelMap.ConfirmCreateCampaign?default(''))}",
		ConfirmRemoveCampaign:"${StringUtil.wrapString(uiLabelMap.ConfirmRemoveCampaign?default(''))}",
		Cancel: "${uiLabelMap.wgcancel}",
		OK: "${uiLabelMap.wgok}",
		NoData: "${StringUtil.wrapString(uiLabelMap.NoData?default(''))}",
		MustCreateCampaignFirst: "${StringUtil.wrapString(uiLabelMap.MustCreateCampaignFirst?default(''))}",
		MustChooseEmployeeFirst: "${StringUtil.wrapString(uiLabelMap.MustChooseEmployeeFirst?default(''))}",
		MustSearchContactFirst: "${StringUtil.wrapString(uiLabelMap.MustSearchContactFirst?default(''))}",
		assignSuccess: "${StringUtil.wrapString(uiLabelMap.assignSuccess?default(''))}",
		assignError: "${StringUtil.wrapString(uiLabelMap.assignError?default(''))}",
		CampaignIdExist: "${StringUtil.wrapString(uiLabelMap.CampaignIdExist?default(''))}",
		today: "${StringUtil.wrapString(uiLabelMap.Today?default(''))}",
		clear: "${StringUtil.wrapString(uiLabelMap.ClearString?default(''))}",
		Total: "${StringUtil.wrapString(uiLabelMap.Total?default(''))}",
		Assigned: "${StringUtil.wrapString(uiLabelMap.Assigned?default(''))}",
		NotAssigned: "${StringUtil.wrapString(uiLabelMap.NotAssigned?default(''))}",
		Completed: "${StringUtil.wrapString(uiLabelMap.Completed?default(''))}",
		NotCompleted: "${StringUtil.wrapString(uiLabelMap.NotCompleted?default(''))}",
		NotifyClickActive: "${StringUtil.wrapString(uiLabelMap.NotifyClickActive?default(''))}",
		NotifyClickDone: "${StringUtil.wrapString(uiLabelMap.NotifyClickDone?default(''))}",
		ConfirmAssignData: "${StringUtil.wrapString(uiLabelMap.ConfirmAssignData?default(''))}",
		PersonCommunicate: "${StringUtil.wrapString(uiLabelMap.PersonCommunicate?default(''))}",
		quantity: "${StringUtil.wrapString(uiLabelMap.quantity?default(''))}",
		InvalidFieldValue : "${StringUtil.wrapString(uiLabelMap.InvalidFieldValue?default(''))}",
		unAssigne : "${StringUtil.wrapString(uiLabelMap.unAssigne?default(''))}"
	};
	var campaignId = "${parameters.id?if_exists}";
	var url = "createCallCampaignAndContact";
	if(campaignId){
		url = "updateCallCampaignAndContact";
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.UpdateCampaign)}");
	}
	var period = [{
		value: "WEEK",
		description: "${StringUtil.wrapString(uiLabelMap.WEEK?default(''))}"
	},{
		value: "MONTH",
		description: "${StringUtil.wrapString(uiLabelMap.MONTH?default(''))}"
	},{
		value: "QUARTER",
		description: "${StringUtil.wrapString(uiLabelMap.QUARTER?default(''))}"
	},{
		value: "YEAR",
		description: "${StringUtil.wrapString(uiLabelMap.YEAR?default(''))}"
	}];
	var isThruDate = false;
	<#if !campaign?exists || (campaign?exists && campaign.isActive == "N" && campaign.statusId = "MKTG_CAMP_PLANNED")>
		<#if isThruDate?exists && isThruDate>
			var isEnableCondition = false;
			isThruDate = true;
		<#else>
			var isEnableCondition = true;
		</#if>
	<#else>
		var isEnableCondition = false;
	</#if>
	$(document).ready(function() {
		$('#campaignName').jqxInput('val', "${StringUtil.wrapString((campaign.campaignName)?if_exists)}");
		$('#campaignSummary').jqxEditor('val', "${StringUtil.wrapString((campaign.campaignSummary)?if_exists)}");
	});
</script>
<div class="tabbable" style="padding-bottom: 30px;">
	<ul class="nav nav-tabs" id="AssignResourceTab">
		<li class="active">
			<a data-toggle="tab" href="#generalInfo">
				<i class="fa fa-home"></i>
				${uiLabelMap.generalInfo}
			</a>
		</li>
		<li class='<#if !parameters.id?exists>tab-disabled</#if>'>
			<a <#if parameters.id?exists>data-toggle="tab"</#if> href="#assignContact">
				<i class="fa fa-list"></i>
				${uiLabelMap.AssignContact}
			</a>
		</li>
		<li class='<#if !parameters.id?exists>tab-disabled</#if>'>
			<a <#if parameters.id?exists>data-toggle="tab"</#if>  href="#assignedContact">
				<i class="fa fa-list-alt"></i>
				${uiLabelMap.AssignedContact}
			</a>
		</li>
		<li class='<#if !parameters.id?exists>tab-disabled</#if>'>
			<a <#if parameters.id?exists>data-toggle="tab"</#if>  href="#report">
				<i class="fa fa-pie-chart"></i>
				${uiLabelMap.KReport}
			</a>
		</li>
		<!-- <li class='<#if !parameters.id?exists>tab-disabled</#if>'>
			<a <#if parameters.id?exists>data-toggle="tab"</#if>  href="#order">
				<i class="fa fa-shopping-cart"></i>
				${uiLabelMap.DAOrderList}
			</a>
		</li> -->
	</ul>
	<div class="tab-content">
		<div id="generalInfo" class="tab-pane in active">
			<#include "generalInfo.ftl"/>
		</div>

		<div id="assignContact" class="tab-pane">
			<#include "assignContact.ftl"/>
		</div>
		<div id="assignedContact" class="tab-pane">
			<#include "moveDataCustomer.ftl"/>
			<div class='row-fluid margin-bottom10'>
				<div class='span12'>
					<label class="pull-left" style="margin-top: 4px;">${uiLabelMap.BSEmployee}&nbsp;</label>
					<div id="ListEmployee" class="pull-left margin-left10"></div>
				</div>
			</div>
			<#include "listAssign.ftl" />
		</div>
		<div id="report" class="tab-pane">
			<#include "report.ftl" />
		</div>
		<!-- <div id="order" class="tab-pane">
		</div> -->
	</div>
</div>
