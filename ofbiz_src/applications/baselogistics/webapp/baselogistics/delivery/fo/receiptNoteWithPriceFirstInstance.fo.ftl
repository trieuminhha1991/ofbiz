<#escape x as x?xml>
<#assign labelInstance = "">
<#if numInstance?exists && numInstance &gt; 1>	
	<#assign labelInstance = uiLabelMap.FirstInstance+ ': '+ uiLabelMap.OriginalSave>
</#if>
<#assign margin_top = "10pt">
<#assign i = 0>
<fo:block font-size="9" font-family="Arial">	
	<#if listItem?size &gt; 0>
		<#assign index = 0>
		<#assign length = listItem?size - 1>
		<#assign max = length/>
		<#assign max = 24/>
		<#assign div = length/25 />
		<#assign displayTotal = true>
		<#if length &gt; 25>
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
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteWithPriceContent.fo.ftl"/>
						<fo:block page-break-after="always"></fo:block>
						<#if max == length>
							<#assign listItemTmp = []>
							<#assign displayTotal = true>
							<#include "deliveryNoteHeader.fo.ftl">
							<#include "receiptNoteWithPriceContent.fo.ftl"/>
						</#if>
					<#elseif k &lt; 20>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteWithPriceContent.fo.ftl"/>
					<#else>
						<#assign displayTotal = false>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteWithPriceContent.fo.ftl"/>
						
						<fo:block page-break-after="always"></fo:block>
						
						<#assign listItemTmp = []>
						<#assign displayTotal = true>
						<#include "deliveryNoteHeader.fo.ftl">
						<#include "receiptNoteWithPriceContent.fo.ftl"/>
					</#if>	
				</#if>
				<#assign index = max + 1>
				<#assign tmp = max + 25/>
				<#if tmp &gt; length>
					<#assign max = length/>
					<#assign displayTotal = true>
				<#else>
					<#assign max = max + 25/>
				</#if>
			</#list>
		<#elseif length &lt; 20>
			<#assign listItemTmp = listItem>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "receiptNoteWithPriceContent.fo.ftl"/>
		<#else>
			<#assign displayTotal = false>
			<#assign listItemTmp = listItem>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "receiptNoteWithPriceContent.fo.ftl"/>
			
			<fo:block page-break-after="always"></fo:block>
			
			<#assign listItemTmp = []>
			<#assign displayTotal = true>
			<#include "deliveryNoteHeader.fo.ftl">
			<#include "receiptNoteWithPriceContent.fo.ftl"/>
		</#if>
	</#if>
</fo:block>
</#escape>