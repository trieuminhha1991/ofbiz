<#escape x as x?xml>
<#assign margin_top = "10pt">
<fo:block font-size="9" font-family="Arial">	
	<#assign labelInstance = uiLabelMap.SecondInstance+ ': '+ uiLabelMap.FacilitySave>
	<#if listItem?size &gt; 0>
		<#assign index = 0>
		<#assign length = listItem?size - 1>
		<#assign max = length/>
		<#assign max = 24/>
		<#assign div = length/25 />
		<#if length &gt; 25>
			<#assign loop = div?floor>
			<#list 0..loop as j>
				<#assign listItemTmp = []>
				<#list index..max as i>
					<#if listItem[i]?has_content>
						<#assign listItemTmp = listItemTmp + [listItem[i]]>
					</#if>
				</#list>
				<#if listItemTmp?size &gt; 0>
					<#assign k = listItemTmp?size>
					<#if k &gt; 18>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteContent.fo.ftl"/>
						<fo:block page-break-after="always"></fo:block>
					<#elseif k &lt; 20>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteContent.fo.ftl"/>
					<#else>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteContent.fo.ftl"/>
					</#if>	
				</#if>
				<#assign index = max + 1>
				<#assign tmp = max + 25/>
				<#if tmp &gt; length>
					<#assign max = length/>
				<#else>
					<#assign max = max + 25/>
				</#if>
			</#list>
		<#elseif length &lt; 20>
			<#assign listItemTmp = listItem>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "receiptNoteContent.fo.ftl"/>
		<#else>
			<#assign listItemTmp = listItem>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "receiptNoteContent.fo.ftl"/>
			
		</#if>
	</#if>
</fo:block>
</#escape>