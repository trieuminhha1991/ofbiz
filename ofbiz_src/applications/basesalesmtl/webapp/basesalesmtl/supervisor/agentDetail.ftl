<#-- TODO deleted -->
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/salesmtlresources/js/supervisor/AgentDetails.js"></script>
<style>
	.row-fluid {
	    min-height: 30px;
	}
	.text-header {
		color: black !important;
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
</style>

<div class="form-horizontal form-window-content-custom label-text-left content-description" style="position:relative;">
	
	<div class="row-fluid">
		<div id="statusId" class="title-status"></div>
		<div class="row-fluid">
			<div class="span6" style="padding-left: 15px;">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSAgentName}:</label>
					</div>
					<div class="div-inline-block">
						<span id="groupName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
					</div>
					<div class="div-inline-block">
						<span id="officeSiteName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.PartyTaxAuthInfos}:</label>
					</div>
					<div class="div-inline-block">
						<span id="taxAuthInfos"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSCurrencyUomId}:</label>
					</div>
					<div class="div-inline-block">
						<span id="currencyUomId"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span id="comments"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.EmailAddress}:</label>
					</div>
					<div class="div-inline-block">
						<span id="txtEmailAddress"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.PhoneNumber}:</label>
					</div>
					<div class="div-inline-block">
						<span id="txtPhoneNumber"></span>
					</div>
				</div>
			</div>
			<div class="span6" style="padding-left: 50px;">
				<div class="row-fluid">
					<div class="span8">
						<div class="logo-company">
							<img src="/salesmtlresources/logo/LOGO_demo.png" id="logoImage"/>
						</div>
					</div>
					<div class="span2"></div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="margin-top10">
	<#assign showtoolbar = "false"/>
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/listAddress.ftl"/>
	</div>
	<div class="margin-top10">
	<#assign updatableAdditionalContact="N">
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/additionalContact.ftl"/>
	</div>
	
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/representativeDetail.ftl"/>
	
	<#if security.hasEntityPermission("AGENT", "_APPROVE", session)>
		<div class="row-fluid hide" id="accept-wrapper">
			<div class="span12 margin-top10">
				<button id="btnAccept" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.DAAccept}</button>
			</div>
		</div>
	</#if>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign listUserLogin = delegator.findList("UserLogin", null, Static["org.ofbiz.base.util.UtilMisc"].toSet("userLoginId"), null, null, false) />
<#assign listSubsidiaries = Static["com.olbius.basehr.util.SecurityUtil"].getPartiesByRoles("SUBSIDIARY", delegator)>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
	<#if parameters.partyId?exists>
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSViewAgent)}");
	$('[data-rel=tooltip]').tooltip();
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var mapSubsidiaries = {<#if listSubsidiaries?exists><#list listSubsidiaries as subsidiary>
		<#assign groupName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, subsidiary, false)>
			"${subsidiary?if_exists}": "${StringUtil.wrapString(groupName?if_exists)}",
		</#list></#if>};
	var mapCurrencyUom = {<#if listCurrencyUom?exists><#list listCurrencyUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var BSNewAgentNotify = "${StringUtil.wrapString(uiLabelMap.BSNewAgentNotify)}";
</script>