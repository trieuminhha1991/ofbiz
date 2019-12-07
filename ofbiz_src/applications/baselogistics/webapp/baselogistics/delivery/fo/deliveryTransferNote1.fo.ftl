<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">	
	<#assign labelInstance = uiLabelMap.FirstInstance+ ': '+ uiLabelMap.OriginalSave>
	<#if listItems?size &gt; 0>
		<#assign index = 0>
		<#assign length = listItems?size - 1>
		<#assign max = length/>
		<#assign max = 19/>
		<#assign div = length/20 />
		<#if length &gt; 20>
			<#assign loop = div?floor>
			<#list 0..loop as j>
				<#assign listItemTmp = []>
				<#list index..max as i>
					<#if listItems[i]?has_content>
						<#assign listItemTmp = listItemTmp + [listItems[i]]>
					</#if>
				</#list>
				<#if listItemTmp?size &gt; 0>
					<#include "deliveryNoteHeader.fo.ftl">
					<#include "deliveryTransferContent.fo.ftl"/>
					<#if max &lt; length>
						<fo:block font-size="9" font-family="Arial" margin-bottom="120px">
						</fo:block>
					</#if>
				</#if>
				<#assign index = max+1>
				<#assign tmp = max + 20/>
				<#if tmp &gt; length>
					<#assign max = length/>
				<#else>
					<#assign max = max + 20/>
				</#if>
			</#list>
		<#else>
			<#assign listItemTmp = listItems>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "deliveryTransferContent.fo.ftl"/>
		</#if>	
	</#if>
</fo:block>
</#escape>