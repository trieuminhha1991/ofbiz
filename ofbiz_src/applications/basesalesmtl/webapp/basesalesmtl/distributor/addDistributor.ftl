<#-- TODO deleted -->
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/salesmtlresources/js/distributor/addDistributor.js?v=1.0.0"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<style>
	.row-fluid {
	    min-height: 40px;
	}
	#copyAddress {
		position: absolute;
	    top: 4px;
	    background: white;
    	cursor: pointer;
	}
</style>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>

<div class="row-fluid">
<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
    <ul class="wizard-steps wizard-steps-square">
            <li data-target="#generalInfo" class="active">
                <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
            </li>
            <li data-target="#contactInfo">
                <span class="step">2. ${uiLabelMap.ContactInformation}</span>
            </li>
            <#if parameters.partyId?exists>
            <#assign textBtnLast = uiLabelMap.CommonUpdate/>
            <li data-target="#representativeInfo">
	        	<span class="step">3. ${uiLabelMap.BERepresentative}</span>
	        </li>
	        <#else>
            <#assign textBtnLast = uiLabelMap.CommonCreate/>
            <li data-target="#facilityInfo">
	            <span class="step">3. ${uiLabelMap.FacilityInformation}</span>
	        </li>
	        <li data-target="#representativeInfo">
	        	<span class="step">4. ${uiLabelMap.BERepresentative}</span>
	        </li>
	        <li data-target="#accountInfo">
	            <span class="step">5. ${uiLabelMap.BSAccountInfo}</span>
	        </li>
            </#if>
	</ul>
</div><!--#fuelux-wizard-->
<div class="step-content row-fluid position-relative" id="step-container">
	<div class="step-pane active" id="generalInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/create/generalInfo.ftl"/>
		</div>
	</div>
	<div class="step-pane" id="contactInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/create/contactInfo.ftl"/>
		</div>
	</div>
	<#if parameters.partyId?exists>
	<div class="step-pane" id="representativeInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 5px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/common/representative.ftl"/>
		</div>
	</div>
	<#else>
	<div class="step-pane" id="facilityInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 5px">
			<a id="copyAddress" class="action-bar-header"><i class="fa fa-clipboard"></i>&nbsp;${uiLabelMap.BSCopyFromContactInformation}</a>
			<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/create/facilityInfo.ftl"/>
		</div>
	</div>
	<div class="step-pane" id="representativeInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 5px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/common/representative.ftl"/>
		</div>
	</div>
	<div class="step-pane" id="accountInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/create/accountInfo.ftl"/>
		</div>
	</div>
	</#if>
</div>
<div class="wizard-actions">
	<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(textBtnLast)}" id="btnNext">
		${uiLabelMap.CommonNext}
		<i class="icon-arrow-right icon-on-right"></i>
	</button>
	<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
		<i class="icon-arrow-left"></i>
		${uiLabelMap.CommonPrevious}
	</button>
</div>
</div>
<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<script>
	<#if parameters.partyId?exists>
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSEditDistributor)}");
	$($(".widget-header").children("h4")).html("${StringUtil.wrapString(uiLabelMap.BSEditDistributor)}");
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];
	multiLang = _.extend(multiLang, {
		BEPasswordEqualsUserName: "${StringUtil.wrapString(uiLabelMap.BEPasswordEqualsUserName)}",
		BEPasswordShort: "${StringUtil.wrapString(uiLabelMap.BEPasswordShort)}",
		BSSupervisorId: "${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}",
		BSSupervisor: "${StringUtil.wrapString(uiLabelMap.BSSupervisor)}",
		});
</script>