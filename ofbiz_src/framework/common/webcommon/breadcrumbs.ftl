<#assign contextPath = request.getContextPath()>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>
<#assign displaySecondaryApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "secondary")>
<#if context.get("selectedMenuItem")?has_content>
<#assign selectedMenuItem = context.get("selectedMenuItem")>
</#if>
<div id="breadcrumbs" class="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>
	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<a href="/">${uiLabelMap.Home}</a>
		</li>
		<#-- Primary Applications -->
        <#list displayApps as display>
          <#assign thisApp = display.getContextRoot()>
          <#assign permission = true>
          <#assign selected = false>
          <#assign permissions = display.getBasePermission()>
          <#list permissions as perm>
            <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session))>
              <#-- User must have ALL permissions in the base-permission list -->
              <#assign permission = false>
            </#if>
          </#list>
          <#assign integerCount = 0 >
          <#if permission == true>
            <#if thisApp == contextPath || contextPath + "/" == thisApp>
            	<#assign integerCount = integerCount + 1 />
            	<#if integerCount == 1>
            		<span class="divider"><i class="icon-angle-right"></i></span>
            	</#if>
              	<li><a href="${contextPath}/control/main">${display.title}</a></li>
              	<#if selectedMenuItem?has_content>
              		<span class="divider"><i class="icon-angle-right"></i></span>
              		<li class="active">${StringUtil.wrapString(selectedMenuItem)}</li>
              	</#if>
              	<#break>
            </#if>
          </#if>
        </#list>

        <#-- Secondary Applications -->
        <#list displaySecondaryApps as display>
          <#assign thisApp = display.getContextRoot()>
          <#assign permission = true>
          <#assign selected = false>
          <#assign permissions = display.getBasePermission()>
          <#list permissions as perm>
            <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session))>
              <#-- User must have ALL permissions in the base-permission list -->
              <#assign permission = false>
            </#if>
          </#list>
          <#assign integerCount2 = 0 >
          <#if permission == true>
            <#if thisApp == contextPath || contextPath + "/" == thisApp>
            	<#assign integerCount2 = integerCount2 + 1 />
            	<#if integerCount2 == 1>
            		<span class="divider"><i class="icon-angle-right"></i></span>
            	</#if>
              	<li class="active"><a href="${contextPath}/control/main">${display.title}</a></li>
              	<#if selectedMenuItem?has_content>
              		<span class="divider"><i class="icon-angle-right"></i></span>
              		<li class="active">${StringUtil.wrapString(selectedMenuItem)}</li>
              	</#if>
              	<#break>
            </#if>
          </#if>
        </#list>
    </ul>
</div>
