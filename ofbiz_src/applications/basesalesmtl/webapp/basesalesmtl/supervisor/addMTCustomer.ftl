<#-- TODO deleted -->

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true />
<script src="/salesmtlresources/js/supervisor/addMTCustomer.js?v=0.0.2"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<style>
	.row-fluid {
	    min-height: 40px;
	}
</style>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>

<#if parameters.partyId?exists>
<#assign textBtnLast = uiLabelMap.CommonUpdate/>
<#else>
<#assign textBtnLast = uiLabelMap.CommonCreate/>
</#if>

<div class="row-fluid">
<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
    <ul class="wizard-steps wizard-steps-square">
            <li data-target="#generalInfo" class="active">
                <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
            </li>
            <li data-target="#contactInfo">
                <span class="step">2. ${uiLabelMap.ContactInformation}</span>
            </li>
            <li data-target="#representativeInfo">
	        	<span class="step">3. ${uiLabelMap.BERepresentative}</span>
	        </li>
	</ul>
</div><!--#fuelux-wizard-->
<div class="step-content row-fluid position-relative" id="step-container">
	<div class="step-pane active" id="generalInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/supervisor/createCustomer/generalInfo.ftl"/>
		</div>
	</div>
	<div class="step-pane" id="contactInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/supervisor/createCustomer/contactInfo.ftl"/>
		</div>
	</div>
	<div class="step-pane" id="representativeInfo">
		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 5px">
			<#include "component://basesalesmtl/webapp/basesalesmtl/common/representative.ftl"/>
		</div>
	</div>
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

<#assign listPartyType = delegator.findByAnd("PartyType", {"parentTypeId": "PARTY_GROUP_CUSTOMER"}, null, false)!/>

<script>
	<#if parameters.partyId?exists>
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSEditMTCustomer)}");
	$($(".widget-header").children("h4")).html("${StringUtil.wrapString(uiLabelMap.BSEditMTCustomer)}");
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var listPartyType = [<#if listPartyType?exists><#list listPartyType as item>{
		partyTypeId: '${item.partyTypeId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapPartyType = {<#if listPartyType?exists><#list listPartyType as item>
	"${item.partyTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	multiLang = _.extend(multiLang, {
		salesmanId: "${StringUtil.wrapString(uiLabelMap.salesmanId)}",
		BsRouteId: "${StringUtil.wrapString(uiLabelMap.BsRouteId)}",
		BSRouteName: "${StringUtil.wrapString(uiLabelMap.BSRouteName)}",
		BEPasswordEqualsUserName: "${StringUtil.wrapString(uiLabelMap.BEPasswordEqualsUserName)}",
		BEPasswordShort: "${StringUtil.wrapString(uiLabelMap.BEPasswordShort)}",
		BSSupervisorId: "${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}",
		BSSupervisor: "${StringUtil.wrapString(uiLabelMap.BSSupervisor)}",
		});
</script>