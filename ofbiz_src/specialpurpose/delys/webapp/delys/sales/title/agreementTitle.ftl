<#--
<div id="page-content" class="clearfix padding-0-imp">
	<div class="page-header position-relative">
		<h1>
			<#if labelTitleProperty?has_content>${uiLabelMap[labelTitleProperty]}</#if> 
			${uiLabelMap.CommonFor} <small><i class="icon-double-angle-right"></i>
			<#if product?has_content>
				<#if product.internalName?has_content> ${product.internalName}</#if>
			</#if> 
			[${uiLabelMap.CommonId}:<#if productId?has_content> ${productId}</#if>] </small>
		</h1>
	</div>
</div>
<#if labelTitleProperty?has_content>${uiLabelMap[labelTitleProperty]}</#if>
-->

<#if agreement?exists && agreement?has_content>
	<#if linkPreviousUiLabel?exists>
		<i class="icon-angle-right"></i> <#if linkPreviousUrl?exists><a href="<@ofbizUrl>${linkPreviousUrl}</@ofbizUrl>" title="${uiLabelMap[linkPreviousUiLabel]}">${uiLabelMap[linkPreviousUiLabel]}</a><#else>${uiLabelMap[linkPreviousUiLabel]}</#if> 
	</#if>
	<i class="icon-angle-right"></i> ${uiLabelMap.DAId}: <a href="<@ofbizUrl>EditAgreementTerms?agreementId=${agreement.agreementId?if_exists}</@ofbizUrl>" title="<#if agreement.agreementId?has_content>${agreement.agreementId?if_exists}</#if>">${agreement.agreementId?if_exists}</a>
</#if>

<#--
<#if orderHeader?has_content>
	<h4 class="lighter smaller" style="margin-top: 10px;"> 
		${externalOrder?if_exists} &nbsp;<a href="<@ofbizUrl>orderpr.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank" data-rel="tooltip" title="${uiLabelMap.DAExportToPDF}" data-placement="bottom"><i class="fa-file-pdf-o"></i></a>
		<#if security.hasPermission("ORDERMGR_UPDATE", session)>
			/ <a href="<@ofbizUrl>editOrderItemsSales?${paramString}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAEditOrderItems}" data-placement="bottom"><i class="icon-edit open-sans"></i></a>
		</#if>
	</h4>
</#if>
-->