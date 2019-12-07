<#assign businessMenus = Static["com.olbius.dms.util.SecurityUtil"].getBussinessRoles(userLogin.partyId, delegator)>
<#assign sidebar = Static["com.olbius.dms.util.CommonUtil"].getCookie(request, "sidebar")>
<div id="sidebar" class="sidebar <#if sidebar=='off'>menu-min</#if>">
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
<div id="hed" style="height:40px;">
	<div id="sidebar-collapse" style="height:100%;" class="sidebar-collapse">
		<i class="icon-double-angle-left" style="margin-top:6px"></i></div>
	</div>
	<#list businessMenus as bMenu>
		<#assign applicationMenuLocation = "component://dms/widget/DmsMenus.xml" />
		<#assign appModelMenu = Static["org.ofbiz.widget.menu.MenuFactory"].getMenuFromLocation(applicationMenuLocation,bMenu,delegator,dispatcher)>
		<#assign module = Static["org.ofbiz.base.util.UtilHttp"].getModule(request) />
		<#if appModelMenu.getModule().equals(module)>
		 	${screens.render("component://dms/widget/DmsMenuScreens.xml#${bMenu}")}
		 </#if>
	</#list>
<#else>
  <#include "applicationList.ftl"/>
</#if>
</div>