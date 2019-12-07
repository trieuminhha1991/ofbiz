<script>
	var listUom = [];
	<#if uoms?has_content>
	<#list uoms as uom>
	listUom.push({ uomId : '${uom.uomId}', description: '${uom.description}'});
	</#list>
	</#if>
</script>
<#if marketingDetail?exists>
<#assign info = marketingDetail.info/>
<#assign productSampling = marketingDetail.products/>
<#assign costs = marketingDetail.costs/>
<#assign places = marketingDetail.places/>
</#if>
<script>
	var info = {
	<#if info?exists>
	marketingCampaignId : '<#if info.marketingCampaignId?exists>${info.marketingCampaignId}</#if>',
	campaignName : '<#if info.campaignName?exists>${info.campaignName}</#if>',
	campaignSummary : '<#if info.campaignSummary?exists>${info.campaignSummary}</#if>',
	marketingPlace : '<#if info.address1?exists>${info.address1}</#if>',
	city : '<#if info?exists && info.geoName?exists>${info.geoName}</#if>',
	fromDate: '<#if info.fromDate?exists>${info.fromDate}</#if>',
	thruDate: '<#if info.thruDate?exists>${info.thruDate}</#if>',
	people:'<#if info.people?exists>${info.people}</#if>',
	isActive: '<#if info.isActive?exists>${info.isActive}</#if>'
	</#if>
	};
	var prdS = [
	<#if productSampling?exists>
	<#list productSampling as product>
	{
	productName: "<#if product.productName?exists>${product.productName}</#if>",
	productId: "<#if product.productId?exists>${product.productId}</#if>",
	quantity: "<#if product.quantity?exists>${product.quantity}</#if>",
	uomId: "<#if product.uomId?exists>${product.uomId}</#if>",
	productTypeId:  "<#if product.productTypeId?exists>${product.productTypeId}</#if>",
	contactMechId: "<#if product.contactMechId?exists>${product.contactMechId}</#if>",
	address1: "<#if product.address1?exists>${product.address1}</#if>"
	},
	</#list>
	</#if>
	];
	var places = [
	<#if places?exists>
	<#list places as place>
	{
	marketingPlaceId: "<#if place.marketingPlaceId?exists>${place.marketingPlaceId}</#if>",
	geoId: "<#if place.geoId?exists>${place.geoId}</#if>",
	districtGeoId: "<#if place.districtGeoId?exists>${place.districtGeoId}</#if>",
	geoName: "<#if place.geoName?exists>${place.geoName}</#if>",
	partyId: "<#if place.partyId?exists>${place.partyId}</#if>",
	roleTypeId: "<#if place.roleTypeId?exists>${place.roleTypeId}</#if>",
	contactMechId: "<#if place.contactMechId?exists>${place.contactMechId}</#if>",
	address1: "<#if place.address1?exists>${place.address1}</#if>"
	},
	</#list>
	</#if>
	];
	var costs = [
	<#if costs?exists>
	<#list costs as cost>
	{
	marketingCostTypeId: "<#if cost.marketingCostTypeId?exists>${cost.marketingCostTypeId}</#if>",
	description: "<#if cost.description?exists>${cost.description}</#if>",
	name: "<#if cost.name?exists>${cost.name}</#if>",
	unitPrice:  "<#if cost.unitPrice?exists>${cost.unitPrice}</#if>",
	quantity:  "<#if cost.quantity?exists>${cost.quantity}</#if>"
	},
	</#list>
	</#if>
	];
</script>