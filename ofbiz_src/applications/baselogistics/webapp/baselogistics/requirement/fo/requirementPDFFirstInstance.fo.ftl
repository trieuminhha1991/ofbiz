<#escape x as x?xml>
<#assign margin_top = "10pt">
<#assign labelInstance =uiLabelMap.FirstInstance + ': '+ uiLabelMap.OriginalSave>
	<#assign quantityTotal = 0>
	<#if listItems?size &gt; 0>
		<#assign index = 1>
		<#assign length = listItems?size>
		<#assign max = length/>
		<#assign max = 22/>
		<#assign div = length/23 />
		<#assign displayTotal = true>
		<#if length &gt; 23>
			<#assign loop = div?floor>
			<#assign displayTotal = false>
			<#list 0..loop as j>
				<#assign listItemTmp = []>
				<#list index..max as i>
					<#if listItems[i-1]?has_content>
						<#assign listItemTmp = listItemTmp + [listItems[i-1]]>
					</#if>
				</#list>
				<#if listItemTmp?size &gt; 0>
					<#include "requirementDPFHeader.fo.ftl">
					<#include "requirementPDFContent.fo.ftl"/>
					<#if max &lt; length>
						<fo:block page-break-after="always"></fo:block>
					</#if>
				</#if>
				<#assign index = max + 1>
				<#assign tmp = max + 23/>
				<#if tmp &gt; length>
					<#assign max = length/>
					<#assign displayTotal = true>
				<#else>
					<#assign max = max + 23/>
				</#if>
			</#list>
		<#else>
			<#assign listItemTmp = listItems>
			<#include "requirementDPFHeader.fo.ftl">
			<#include "requirementPDFContent.fo.ftl"/>
		</#if>	
	</#if>
</#escape>