<#escape x as x?xml>
<#assign labelInstance = uiLabelMap.ThirdInstance+ ': '+ uiLabelMap.AccountingSave>
<#assign i = 0>
<fo:block font-size="9" font-family="Arial" margin-top="25px">	
	<#assign listItemAll = []>
	<#if listItem?size &gt; 0>
		<#list listItem as item >
			<#assign listItemAll = listItemAll + [item]>
	 	</#list>
	</#if>
	<#if listItemAll?size &gt; 0>
		<#assign index = 0>
		<#assign length = listItemAll?size - 1>
		<#assign max = length/>
		<#assign max = 19/>
		<#assign div = length/20 />
		<#assign displayTotal = true>
		<#if length &gt; 20>
			<#assign loop = div?floor>
			<#assign displayTotal = false>
			<#list 0..loop as j>
				<#assign listItemTmp = []>
				<#list index..max as i>
					<#if listItemAll[i]?has_content>
						<#assign listItemTmp = listItemTmp + [listItemAll[i]]>
					</#if>
				</#list>
				<#if listItemTmp?size &gt; 0>
					<#include "deliveryNoteHeader.fo.ftl">
					<#include "stockOutNoteContent.fo.ftl"/>
					<#if max &lt; length>
						<#assign displayTotal = true>
						<fo:block font-size="9" font-family="Arial" margin-bottom="120px">
						</fo:block>
					</#if>
				</#if>
				<#assign index = max + 1>
				<#assign tmp = max + 20/>
				<#if tmp &gt; length>
					<#assign max = length/>
				<#else>
					<#assign max = max + 20/>
				</#if>
			</#list>
		<#else>
			<#assign listItemTmp = listItemAll>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "stockOutNoteContent.fo.ftl"/>
		</#if>	
	</#if>
</fo:block>
</#escape>