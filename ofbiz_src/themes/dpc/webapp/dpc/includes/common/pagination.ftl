<#if listSize?exists && (listSize > 0) && start?exists && (start > -1) >
<div class="wrap_page">
	<div class="pagination">
		<a href="<#if link?exists>${link}<#else>?</#if>&VIEW_INDEX=${first}"><b class="pre"></b><b class="pre"></b></a>
		<a href="<#if link?exists>${link}<#else>?</#if>&VIEW_INDEX=${prev}" class='navigate'><b class="pre"></b></a>
		<#list start..end as i>
			<a class="navigate <#if i == current>actpage</#if>" href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${i}">${(i + 1)}</a>
		</#list>
		<a href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${next}" class='navigate'><b class="next"></b></a>
		<a href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${last}"><b class="next"></b><b class="next"></b></a>
	</div>
</div>
</#if>