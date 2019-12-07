<#if (requestAttributes.topLevelList)?exists>
    <#assign topLevelList = requestAttributes.topLevelList>
</#if>
<#assign rootEle=true>

<#macro fillTree rootCat>
  <#if (rootCat?has_content)>
	<ul<#if rootEle=true> id="custom_accordion"</#if>>
	<#assign rootEle=false>
    <#list rootCat?sort_by("productCategoryId") as root>
	<li>
		<a href="<@ofbizCatalogAltUrl productCategoryId=root.productCategoryId/>" ><#if root.categoryName?exists>${root.categoryName}<#elseif root.categoryDescription?exists>${root.categoryDescription}<#else>${root.productCategoryId}</#if></a>
		<#if root.child?has_content>
		<span class="down"></span>
                <@fillTree rootCat=root.child/>
            </#if>
	</li>
    </#list>
    </ul>
  </#if>
</#macro>
<div class="box">
    <div class="box-heading">${uiLabelMap.BigshopProductCategories}</div>
    <div class="box-content box-category">
	<#if (topLevelList?has_content)>
		<@fillTree rootCat=completedTree/>
		</#if>
    </div>
</div>