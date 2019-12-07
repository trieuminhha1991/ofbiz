<#assign businessMenus = Static["com.olbius.basehr.util.SecurityUtil"].getBussinessRoles(userLogin.partyId, delegator)!>
<#if businessMenus?has_content>
<#--<div id="delys-logo">
	<div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
		<img src="/images/delys-logo.png" alt="">
	</div>

	<div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
		<img src="/images/delys-logo-mini.png" alt="">
	</div>
</div>
-->
<div id="hed" style="height:40px;"><div id="sidebar-collapse" style="height:100%;" class="sidebar-collapse"><i class="icon-double-angle-left" style="margin-top:6px"></i></div></div>
	<#list businessMenus as bMenu>
		<#if componentNameSc?exists && componentResourceName?exists>
			<#assign applicationMenuLocation = "component://${componentNameSc?if_exists}/widget/${componentResourceName?if_exists}Menus.xml" />
			<#assign appModelMenu = Static["org.ofbiz.widget.menu.MenuFactory"].getMenuFromLocation(applicationMenuLocation,bMenu,delegator,dispatcher)!/>
			<#if appModelMenu?exists>
				<#assign module = Static["org.ofbiz.base.util.UtilHttp"].getModule(request) />
				<#if appModelMenu.getModule().equals(module)>
				 	${screens.render("component://${componentNameSc?if_exists}/widget/${componentResourceName?if_exists}MenuScreens.xml#${bMenu}")}
				</#if>
			</#if>
		</#if>
	</#list>
<#else>
  <#include "applicationList.ftl"/>
</#if>