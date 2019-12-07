<#ftl encoding='UTF-8'>
<#if context.get("selectedBizMenuItemTitle")?has_content>
<#assign selectedBizMenuItemTitle = context.get("selectedBizMenuItemTitle")>
</#if>
<#if context.get("selectedBizSubMenuItemTitle")?has_content>
<#assign selectedBizSubMenuItemTitle = context.get("selectedBizSubMenuItemTitle")>
</#if>
<div id="breadcrumbs" class="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>
	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
		</li>
		<#if selectedBizMenuItemTitle?has_content>
		<li>
			${selectedBizMenuItemTitle}
		</li>
		</#if>
		<#if selectedBizSubMenuItemTitle?has_content>
		<li>
			<span class="divider"><i class="icon-angle-right"></i></span>
			${selectedBizSubMenuItemTitle}
		</li>
		</#if>
    </ul>
</div>
