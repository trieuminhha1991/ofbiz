<#if listSize?exists && (listSize > 0) && start?exists && (start > 0) >
<div class="wrap_page">
	<div class="pagination">
		<a href="<#if link?exists>${link}<#else>?</#if>&VIEW_INDEX=${first}">
			<div  class='navigate'>
				<i class='fa fa-backward'></i>
			</div>
		</a>
		<a href="<#if link?exists>${link}<#else>?</#if>&VIEW_INDEX=${prev}">
			<div  class='navigate'>
				<i class='fa fa-chevron-left'></i>
			</div>
		</a>
		<#list start..end as i>
			<a class="<#if i == current>actpage</#if>" href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${i}">
				<div  class='navigate'>
					${(i + 1)}
				</div>
			</a>
		</#list>
		<a href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${next}">
			<div  class='navigate'>
				<i class='fa fa-chevron-right'></i>
			</div>
		</a>
		<a href="<#if link?exists>${link}&<#else>?</#if>VIEW_INDEX=${last}">
			<div  class='navigate'>
				<i class='fa fa-forward'></i>
			</div>
		</a>
	</div>
</div>
</#if>