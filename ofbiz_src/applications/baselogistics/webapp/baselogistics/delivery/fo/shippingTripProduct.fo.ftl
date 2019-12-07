<#escape x as x?xml>
<#assign labelInstance = uiLabelMap.ThirdInstance+ ': '+ uiLabelMap.CustomerSave>
<#assign i = 0>
<fo:block font-size="9" font-family="Arial">	
	<#if listItem?size &gt; 0>
		<#assign index = 0>
		<#assign length = listItem?size - 1>
		<#assign max = length/>
		<#assign max = 18/>
		<#assign div = length/19 />
		<#assign displayTotal = true>
		<#if length &gt; 19>
			<#assign loop = div?floor>
			<#assign displayTotal = false>
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
						<#include "productNoteHeader.fo.ftl">
						<#include "productNoteContent.fo.ftl"/>
						<fo:block page-break-after="always"></fo:block>
						<#if max == length>
							<#assign listItemTmp = []>
							<#assign displayTotal = true>
							<#include "productNoteHeader.fo.ftl">
							<#include "productNoteContent.fo.ftl"/>
						</#if>
					<#elseif k &lt; 10>
						<#include "productNoteHeader.fo.ftl">
						<#include "productNoteContent.fo.ftl"/>
					<#else>
						<#assign displayTotal = false>
						<#include "productNoteHeader.fo.ftl">
						<#include "productNoteContent.fo.ftl"/>
						
						<fo:block page-break-after="always"></fo:block>
						
						<#assign listItemTmp = []>
						<#assign displayTotal = true>
						<#include "productNoteHeader.fo.ftl">
						<#include "productNoteContent.fo.ftl"/>
					</#if>	
				</#if>
				<#assign index = max + 1>
				<#assign tmp = max + 19/>
				<#if tmp &gt; length>
					<#assign max = length/>
					<#assign displayTotal = true>
				<#else>
					<#assign max = max + 19/>
				</#if>
			</#list>
		<#elseif length &lt; 10>
			<#assign displayTotal = false>
			<#assign listItemTmp = listItem>
			<#include "productNoteHeader.fo.ftl">
			<#include "productNoteContent.fo.ftl"/>
		<#else>
			<#assign displayTotal = false>
			<#assign listItemTmp = listItem>
			<#include "productNoteHeader.fo.ftl">
			<#include "productNoteContent.fo.ftl"/>
			
			<fo:block page-break-after="always"></fo:block>
			
			<#assign listItemTmp = []>
			<#assign displayTotal = false>
			<#include "productNoteHeader.fo.ftl">
			<#include "productNoteContent.fo.ftl"/>
		</#if>	
	</#if>
</fo:block>
</#escape>