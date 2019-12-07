<#if parameters.id?exists>
	<#assign current=parameters.id/>
<#elseif categories?has_content>
	<#assign current=categories[0].contentTypeId/>
<#else>
	<#assign current=""/>
</#if>
<ul class="navinews">
	<li>
		<a class="<#if  type?exists  && type == 'newest'>actnavi</#if>" href="newcontent"><h1>${uiLabelMap.NewestContentTitle}</h1></a>
	</li>
	<#list categories?if_exists as category>
		<li>
			<a class="<#if type?exists && type == 'category' && category.contentTypeId == current>actnavi</#if>" href="categorycontent?id=${category.contentTypeId}">
				${category.description}<i class="allicon-new"></i>
			</a>
		</li>
	</#list>
</ul>