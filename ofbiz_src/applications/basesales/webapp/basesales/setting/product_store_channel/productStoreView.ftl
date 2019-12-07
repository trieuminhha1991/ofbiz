<#assign isDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, productStore.payToPartyId?default(""))!/>

<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>

<style>
	.line-height30{
		line-height: 30px!important;
	}
	.profile-info-a {
		width: 25%!important;
	}
	.profile-info-b {
	    margin-left: 27%!important;
    	min-height: 20px;
	}
	.profile-info-row{
		font-size: 14px!important;
	}
	.container-buttons button {
	    color: #438eb9 !important;
	    border-color: #FFF !important;
	    margin-top: 4px;
	    font-size: 14px!important;
	    background: none!important;
	}
	.container-buttons button:hover {
	    background:none!important;
		color:#005580!important;
		font-size:14px!important;
		cursor: pointer;
	}
</style>
<script type="text/javascript">
	<#assign facility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId",productStore.inventoryFacilityId), false)!>
	<#assign personAndPartyGroupSimple = delegator.findOne("PersonAndPartyGroupSimple", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",productStore.payToPartyId), false)!>
	<#assign salesMethodChannelData = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "SALES_METHOD_CHANNEL"), null, false)!/>
	<#assign defaultSalesChannelList = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "ORDER_SALES_CHANNEL"), null, false)!/>
	<#assign partyTypeList = delegator.findList("PartyType", null , null, orderBy, null, false)!/>
	<#assign geoTypeList = delegator.findList("GeoType", null , null, orderBy, null, false)!/>
	<#assign currencyUomId = Static['com.olbius.basesales.util.SalesUtil'].getCurrentCurrencyUom(delegator)!/>
	<#assign storeCreditAccountEnumList = delegator.findByAnd("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "STR_CRDT_ACT"), null, false)>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
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
	
	var partyTypeList = [
	<#if partyTypeList?exists>
	    <#list partyTypeList as partyTypeL>
	    {	partyTypeId: "${partyTypeL.partyTypeId}",
	    	description: "${StringUtil.wrapString(partyTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var geoTypeList = [
	<#if geoTypeList?exists>
	    <#list geoTypeList as geoTypeL>
	    {	geoTypeId: "${geoTypeL.geoTypeId}",
	    	description: "${StringUtil.wrapString(geoTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var uomList = [
	<#if uomList?exists>
	    <#list uomList as uomL>
	    {	uomId : "${uomL.uomId}",
	    	abbreviation : "${StringUtil.wrapString(uomL.abbreviation)}",
	    	description: "${StringUtil.wrapString(uomL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	var storeCreditAccountEnumList = [
	<#if storeCreditAccountEnumList?exists>
	    <#list storeCreditAccountEnumList as storeCreditAccountEnumL>
	    {	enumId : "${storeCreditAccountEnumL.enumId}",
	    	description: "${StringUtil.wrapString(storeCreditAccountEnumL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	<#assign currentOrganizationPartyId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
	<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
	<#assign currencyUom = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, null, true)!/>
	<#assign reserveOrderEnum = delegator.findByAnd("Enumeration", {"enumTypeId" : "INV_RES_ORDER"}, null, true)!/>
	var currencyUomData = [
	<#if currencyUom?exists>
		<#list currencyUom as uomItem>
		{	uomId : "${uomItem.uomId}",
			descriptionSearch : "${StringUtil.wrapString(uomItem.get("description", locale))} [${uomItem.abbreviation}]",
		},
		</#list>
	</#if>
	];
	var reserveOrderEnumData = [
	<#if reserveOrderEnum?exists>
		<#list reserveOrderEnum as item>
		{	enumId : "${item.enumId}",
			description : "${StringUtil.wrapString(item.get("description", locale))}",
		},
		</#list>
	</#if>
	];
	
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
</script>

<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<div class="widget-box transparent">
	<div class="widget-header widget-header-small">
		<h4 class="widget-title blue smaller">
			${uiLabelMap.BSGeneralInformation}</span>
		</h4>
		<div class="pull-right container-buttons">
			<button id="editProductStore" style="margin-left:20px;" role="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" aria-disabled="false"><i class="icon-pencil open-sans"></i><span>${uiLabelMap.BSEdit}</span></button>
		</div>
	</div>
	<div class="widget-body">
		<div class="">
			<div id="profile-feed-1" class="profile-feed ace-scroll" style="position: relative;">
				<div class="scroll-content" style="max-height: 200px;">
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a"><#if isDistributor?exists && isDistributor>${uiLabelMap.BSPSSettingSaleId}<#else>${uiLabelMap.BSPSChannelId}</#if></div>
							<div class="profile-info-value profile-info-b"><span>${productStoreId?if_exists}</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a"><#if isDistributor?exists && isDistributor>${uiLabelMap.BSPSSettingSaleName}<#else>${uiLabelMap.BSPSChannelName}</#if></div>
							<div class="profile-info-value profile-info-b"><span>${productStore.storeName?if_exists}</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSStatusId}</div>
							<div class="profile-info-value profile-info-b">
								<#if productStore.statusId?exists>
									<#assign productStoreStatus = delegator.findOne("StatusItem", {"statusId": productStore.statusId}, false)!/>
									<span style="<#if "PRODSTORE_DISABLED" == productStore.statusId>color: #555;<#else>color: #037c07;</#if>font-weight: bold;">${productStoreStatus.get("description", locale)?if_exists}</span>
								</#if>
							</div>
						</div>
					</div>
					<#--
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSTitle}</div>
							<div class="profile-info-value profile-info-b"><span>${productStore.title?if_exists}</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSSubtitle}</div>
							<div class="profile-info-value profile-info-b"><span>${(productStore.subtitle)?if_exists}</span></div>
						</div>
					</div>
					-->
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSPayToParty}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if productStore.payToPartyId?exists>
									<#assign payToParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", productStore.payToPartyId), false)!>
									<#assign personAndPartyGroupSimple = delegator.findOne("PersonAndPartyGroupSimple", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", productStore.payToPartyId), false)!>
									${payToParty?if_exists.groupName?if_exists}
								</#if> (${(personAndPartyGroupSimple.partyCode)?if_exists})
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSPrimaryFacilityId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.inventoryFacilityId)?exists>
									<#assign conditionList = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityId", productStore.inventoryFacilityId)]/>
									<#assign conditionList = conditionList + [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productStoreId", productStore.productStoreId)]/>
									<#assign conditionList = conditionList + [Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()]/>
									<#assign productStoreFacilityDetails = delegator.findList("ProductStoreFacilityDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditionList), null, null, null, false)!/>
									<#if (productStoreFacilityDetails[0]?has_content)>
										<#assign facility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", productStoreFacilityDetails[0].facilityId), false)!>
										${StringUtil.wrapString(facility.get("facilityName", locale))} - 
										<#if facility.facilityCode?exists>(${facility.facilityCode})</#if>
									</#if>
								</#if>
							</span></div>
						</div>
					</div>
					<#--
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSCheckInventoryItem}</div>
							<div class="profile-info-value profile-info-b"><span><#if productStore.checkInventory?exists><#if productStore.checkInventory == "Y">${uiLabelMap.BSYes}<#else>${uiLabelMap.BSNo}</#if></#if></span></div>
						</div>
					</div>
					-->
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSRequireInventory}</div>
							<div class="profile-info-value profile-info-b"><span><#if productStore.requireInventory?exists><#if productStore.requireInventory == "Y">${uiLabelMap.BSYes}<#else>${uiLabelMap.BSNo}</#if></#if></span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSReserveOrderEnum}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if productStore.reserveOrderEnumId?exists>
									<#assign reserverOrderEnum = delegator.findOne("Enumeration", {"enumId": productStore.reserveOrderEnumId}, false)!>
									${StringUtil.wrapString(reserverOrderEnum.get("description", locale))}
								</#if>
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSDefaultCurrencyUomId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.defaultCurrencyUomId)?exists>
									<#assign currencyUomGV = delegator.findOne("Uom", Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId", productStore.defaultCurrencyUomId), false)!>
									${StringUtil.wrapString(currencyUomGV.get("description", locale))}
								</#if> (${productStore.defaultCurrencyUomId?if_exists})
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSVatTaxAuthGeoId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.defaultCurrencyUomId)?exists>
									<#assign vatTaxGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", productStore.vatTaxAuthGeoId), false)!>
									<#if (vatTaxGeo.get("geoName", locale))?exists>
										${StringUtil.wrapString(vatTaxGeo.get("geoName", locale))}
									</#if>
								</#if> (${productStore.vatTaxAuthGeoId?if_exists})
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSVatTaxAuthPartyId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.vatTaxAuthPartyId)?exists>
									<#assign vatTaxParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", productStore.vatTaxAuthPartyId), false)!>
									${StringUtil.wrapString(vatTaxParty.get("groupName", locale))}
								</#if> (${(productStore.vatTaxAuthPartyId)?if_exists})
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSShowPricesWithVatTax}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if productStore.showPricesWithVatTax?exists>
									${productStore.showPricesWithVatTax}
								</#if>
							</span></div>
						</div>
					</div>
					<#--
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSStoreCreditAccountEnumId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.storeCreditAccountEnumId)?exists>
									<#assign channel = delegator.findOne("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", productStore.storeCreditAccountEnumId), false)!>
									${StringUtil.wrapString(channel.get("description", locale))} 
								</#if>	
							</span></div>
						</div>
					</div>
					-->
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSSalesChannelType}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.salesMethodChannelEnumId)?exists>
									<#assign channel = delegator.findOne("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", productStore.salesMethodChannelEnumId), false)>
									${StringUtil.wrapString(channel.get("description", locale))}
								</#if>
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSSalesChannelEnumId}</div>
							<div class="profile-info-value profile-info-b"><span>
								<#if (productStore.defaultSalesChannelEnumId)?exists>
									<#assign channel = delegator.findOne("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumId", productStore.defaultSalesChannelEnumId), false)>
									${StringUtil.wrapString(channel.get("description", locale))}
								</#if>
							</span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSShowPricesWithVatTax}</div>
							<div class="profile-info-value profile-info-b"><span><#if productStore.showPricesWithVatTax?exists><#if productStore.showPricesWithVatTax == "Y">${uiLabelMap.BSYes}<#else>${uiLabelMap.BSNo}</#if></#if></span></div>
						</div>
					</div>
					<div class="profile-user-info profile-user-info-striped">
						<div class="profile-info-row">
							<div class="profile-info-name profile-info-a">${uiLabelMap.BSIncludeCustomerOtherSalesChannel}</div>
							<div class="profile-info-value profile-info-b"><span><#if productStore.includeOtherCustomer?exists><#if productStore.includeOtherCustomer == "Y">${uiLabelMap.BSYes}<#else>${uiLabelMap.BSNo}</#if><#else>${uiLabelMap.BSNo}</#if></span></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
<#assign facilityTypeDesc = delegator.findList("FacilityType", null, null, null, null, false)!/>
    var facilityTypeDescData = [
    <#if facilityTypeDesc?exists>
        <#list facilityTypeDesc as facilityType>
            {	facilityTypeId: '${facilityType.facilityTypeId}',
                description: '${StringUtil.wrapString(facilityType.description?if_exists)}',
			   	facilityName: '${StringUtil.wrapString(facilityType.facilityName?if_exists)}',
			    groupName: '${StringUtil.wrapString(facilityType.groupName?if_exists)}',
            },
        </#list>
    </#if>
    ];
</script>

<#include "productStoreEditPopup.ftl" />

<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbProdStoreView.init();
	});
	var OlbProdStoreView = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initEvent = function(){
			$("#editProductStore").on("click", function(){
				OlbProductStoreEdit.openWindow({
					productStoreId: "${StringUtil.wrapString(productStore.productStoreId?if_exists)}",
					storeName: "${StringUtil.wrapString(productStore.storeName?if_exists)}",
					payToPartyId: "${StringUtil.wrapString(productStore.payToPartyId?if_exists)}",
					defaultCurrencyUomId: "${StringUtil.wrapString(productStore.defaultCurrencyUomId?if_exists)}",
					defaultSalesChannelEnumId: "${StringUtil.wrapString(productStore.defaultSalesChannelEnumId?if_exists)}",
					vatTaxAuthPartyId: "${StringUtil.wrapString(productStore.vatTaxAuthPartyId?if_exists)}",
					vatTaxAuthGeoId: "${StringUtil.wrapString(productStore.vatTaxAuthGeoId?if_exists)}",
					salesMethodChannelEnumId: "${StringUtil.wrapString(productStore.salesMethodChannelEnumId?if_exists)}",
					inventoryFacilityId: "${StringUtil.wrapString(productStore.inventoryFacilityId?if_exists)}",
					reserveOrderEnumId: "${StringUtil.wrapString(productStore.reserveOrderEnumId?if_exists)}",
					showPricesWithVatTax: "${StringUtil.wrapString(productStore.showPricesWithVatTax?if_exists)}",
					includeOtherCustomer: "${StringUtil.wrapString(productStore.includeOtherCustomer?if_exists)}",
					requireInventory: "${StringUtil.wrapString(productStore.requireInventory?if_exists)}"
				});
			});
		};
		return {
			init: init
		};
	}());
</script>
