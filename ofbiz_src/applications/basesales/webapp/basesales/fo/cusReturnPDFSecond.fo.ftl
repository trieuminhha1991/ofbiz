<#escape x as x?xml>
    <#assign margin_top = "15pt">
    <#assign labelInstance =uiLabelMap.SecondInstance + ': '+ uiLabelMap.FacilitySave>
    <#assign listItemAll = []>
    <#if listItem?size &gt; 0>
        <#list listItem as item >
            <#assign listItemAll = listItemAll + [item]>
        </#list>
    </#if>
    <#assign quantityTotal = 0>
    <#if listItemAll?size &gt; 0>
        <#assign index = 0>
        <#assign length = listItemAll?size - 1>
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
                    <#if listItemAll[i]?has_content>
                        <#assign listItemTmp = listItemTmp + [listItemAll[i]]>
                    </#if>
                </#list>
                <#if listItemTmp?size &gt; 0>
                    <#include "cusReturnDPFHeader.fo.ftl">
                    <#include "cusReturnPDFContent.fo.ftl"/>
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
            <#assign listItemTmp = listItemAll>
            <#include "cusReturnDPFHeader.fo.ftl">
            <#include "cusReturnPDFContent.fo.ftl"/>
        </#if>
    </#if>
</#escape>