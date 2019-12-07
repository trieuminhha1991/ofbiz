<@jqGridMinimumLib />
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/salesresources/js/setting/createRetailStore.js?v=0.0.1"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script src="/crmresources/js/generalUtils.js"></script>

<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#step1" class="active">
				<span class="step">1. ${uiLabelMap.BSGeneralInformation}</span>
			</li>
			<li data-target="#step2">
				<span class="step">2. ${uiLabelMap.BSStepConfirm}</span>
			</li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container" style="padding-top:15px">
		<div class="step-pane active" id="step1">
			<#include "createRetailStore_Information.ftl"/>
		</div>

		<div class="step-pane" id="step2">
			<#include "createRetailStore_Confirm.ftl"/>
		</div>
	</div><!--.step-content-->

	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.BSFinish}">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>

<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>

<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)!/>
<#assign geoTypeList = delegator.findList("GeoType", null , null, orderBy, null, false)!/>
<#assign reserveOrderEnum = delegator.findByAnd("Enumeration", {"enumTypeId" : "INV_RES_ORDER"}, null, true)!/>
<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
<#assign salesMethodChannelData = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!/>
<#assign defaultSalesChannelList = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "ORDER_SALES_CHANNEL"), null, false)!/>
<script>
	const productStoreId = "${(parameters.productStoreId)?if_exists}";
    if (productStoreId) {
        $($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.SettingEditProductStore)}");
    }
	multiLang = _.extend(multiLang, {
		BSClickToChoose: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
		BSOrganizationId: "${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}",
		BSFullName: "${StringUtil.wrapString(uiLabelMap.BSFullName)}",
		BSCurrencyUomId: "${StringUtil.wrapString(uiLabelMap.BSCurrencyUomId)}",
		BSPartyId: "${StringUtil.wrapString(uiLabelMap.BSPartyId)}",
		BSTaxAuthGeoId: "${StringUtil.wrapString(uiLabelMap.BSTaxAuthGeoId)}",
		BSGroupName: "${StringUtil.wrapString(uiLabelMap.BSGroupName)}",
		BSGeoId: "${StringUtil.wrapString(uiLabelMap.BSGeoId)}",
		BSGeoTypeId: "${StringUtil.wrapString(uiLabelMap.BSGeoTypeId)}",
		BSGeoName: "${StringUtil.wrapString(uiLabelMap.BSGeoName)}",
		BSGeoCode: "${StringUtil.wrapString(uiLabelMap.BSGeoCode)}",
		BSGeoSecCode: "${StringUtil.wrapString(uiLabelMap.BSGeoSecCode)}",
		BSAbbreviation: "${StringUtil.wrapString(uiLabelMap.BSAbbreviation)}",
		BSWellKnownText: "${StringUtil.wrapString(uiLabelMap.BSWellKnownText)}",
		BSEmployeeId: "${StringUtil.wrapString(uiLabelMap.BSEmployeeId)}",
		BSOrganizationId: "${StringUtil.wrapString(uiLabelMap.BSOrganizationId)}",
		BSClickToChoose: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
		
	});
	var currentOrganizationPartyId = <#if currentOrganizationPartyId?exists>'${currentOrganizationPartyId}'</#if>;
	var currencyUomData = [<#if currencyUom?exists><#list currencyUom as uomItem>{
		uomId : "${uomItem.uomId}",
		descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
	},</#list></#if>];
	var geoTypeList = [<#if geoTypeList?exists><#list geoTypeList as geoTypeL>{
		geoTypeId: "${geoTypeL.geoTypeId}",
		description: "${StringUtil.wrapString(geoTypeL.get("description", locale))}"
	},</#list></#if>];
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
	var reserveOrderEnumData = [<#if reserveOrderEnum?exists><#list reserveOrderEnum as item>{
		enumId : "${item.enumId}", description : "${StringUtil.wrapString(item.get("description", locale))}",
	},</#list></#if>];
	var salesMethodChannelData = [
	<#if salesMethodChannelData?exists>
	    <#list salesMethodChannelData as enumerationL>
	    {   enumId: "${enumerationL.enumId}",
	    	description: "${StringUtil.wrapString(enumerationL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	var defaultSalesChannelData = [
	<#if defaultSalesChannelList?exists>
	    <#list defaultSalesChannelList as enumerationL>
	    {   enumId: "${enumerationL.enumId}",
	    	description: "${StringUtil.wrapString(enumerationL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
</script>