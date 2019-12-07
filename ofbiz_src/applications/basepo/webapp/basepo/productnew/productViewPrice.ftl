

<div id="prodprice-tab" class="tab-pane<#if activeTab?exists && activeTab == "prodprice-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<#if hasOlbPermission("MODULE", "PRODPRICE_VIEW", "")>
				<#include "productViewPricePrimary.ftl"/>
			<#--
			<#else>
				<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
				<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
				<script>
					var currencyUoms = [<#if currencyUoms?exists><#list currencyUoms as item>{
						uomId: '${item.uomId?if_exists}',
						description: "${StringUtil.wrapString(item.get("description", locale))}"
					},</#list></#if>];
					var mapCurrencyUom = {<#if currencyUoms?exists><#list currencyUoms as item>
					"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
					</#list></#if>};
					var quantityUoms = [<#if quantityUoms?exists><#list quantityUoms as item>{
				    	uomId: '${item.uomId?if_exists}',
				    	description: "${StringUtil.wrapString(item.get("description", locale))}"
				    },</#list></#if>];
					var mapQuantityUom = {<#if quantityUoms?exists><#list quantityUoms as item>
						"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
					</#list></#if>};
				</script>
			-->
			</#if>
		</div>
	</div>
	
	<br/>
	<#include "productViewPriceRule.ftl"/>
	
	<#if hasOlbPermission("MODULE", "PRODQUOTATION_VIEW", "")>
	<div class="row-fluid margin-top10">
		<div class="span12">
			<span class="tooltip checkbox-custom" style="opacity:1; margin-top:5px">${uiLabelMap.BSFindProductPriceIn} <a href="<@ofbizUrl>findProductPrice</@ofbizUrl>" target="_blank">${uiLabelMap.BSHere}</a></span>
		</div>
	</div>
	</#if>
</div>