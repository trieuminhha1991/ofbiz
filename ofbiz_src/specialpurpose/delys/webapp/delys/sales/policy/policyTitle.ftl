<#if salesPolicyId?has_content || (salesPolicyId?exists && salesPolicyId?has_content)>
	<i class="icon-angle-right"></i>
</#if>
<#if salesPolicyId?has_content>
	<#if salesPolicy.policyName?length &gt; 25 >
		<span class="display-inline-block hover-name" title="${salesPolicy.policyName}">${salesPolicy.policyName?substring(0, 25)}...</span>
	<#else>
		${salesPolicy.policyName}
	</#if>
</#if>
<#if salesPolicyId?exists && salesPolicyId?has_content>
	[${uiLabelMap.CommonId}:${salesPolicyId}]
</#if>