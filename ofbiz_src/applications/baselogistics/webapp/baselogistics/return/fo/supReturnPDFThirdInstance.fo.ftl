<#escape x as x?xml>
<#assign margin_top = "20pt">
<#assign labelInstance =uiLabelMap.ThirdInstance + ': '+ uiLabelMap.BLSupplierSave>
	<#assign listItemAll = []>
	<#if listItem?size &gt; 0>
		<#list listItem as item >
			<#assign listItemAll = listItemAll + [item]>
	 	</#list>
	</#if>
	<#assign quantityTotal = 0>
	<#if listItemAll?size &gt; 0>
		<#assign index = 1>
		<#assign length = listItemAll?size - 1>
		<#assign max = length/>
		<#assign max = 25/>
		<#assign div = length/25 />
		<#assign displayTotal = true>
		<#if length &gt; 26>
			<#assign loop = div?floor>
			<#assign displayTotal = false>
			<#list 0..loop as j>
				<#assign listItemTmp = []>
				<#if index &lt; max || index == max>
					<#list index..max as i>
						<#if listItemAll[i-1]?has_content>
							<#assign listItemTmp = listItemTmp + [listItemAll[i-1]]>
						</#if>
					</#list>
				<#else>
					<#assign listItemTmp = []/>
					<#assign displayTotal = true>
				</#if>
				<#if listItemTmp?size &gt; 0>
					<#include "supReturnDPFHeader.fo.ftl">
					<#include "supReturnPDFContent.fo.ftl"/>
					<#if max &lt; length>
						<fo:block page-break-after="always"></fo:block>
					</#if>
				<#else>
					<fo:block page-break-after="always"></fo:block>
					<#include "supReturnDPFHeader.fo.ftl">
					<#include "supReturnPDFContent.fo.ftl"/>
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
		<#else>
			<#assign listItemTmp = listItemAll>
			<#include "supReturnDPFHeader.fo.ftl">
			<#include "supReturnPDFContent.fo.ftl"/>
		</#if>	
	</#if>
</#escape>