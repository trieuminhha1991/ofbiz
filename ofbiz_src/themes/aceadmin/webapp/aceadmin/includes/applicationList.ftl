<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#--<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>-->
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>
<#assign displaySecondaryApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "secondary")>
    <#if userLogin?has_content>
    <#--
    <div id="delys-logo">
		<div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
			<img src="/images/delys-logo.png" alt="">
		</div>

		<div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
			<img src="/images/delys-logo-mini.png" alt="">
		</div>
		
	</div>
    -->
    <div id="hed" style="height:40px;"><div id="sidebar-collapse" style="height:100%;" class="sidebar-collapse"><i class="icon-double-angle-left" style="margin-top:6px"></i></div></div>
        <ul class="nav nav-list">
        	<!-- Dashboard is here 
        	<li id="lidb" class="active">
        		<a href="/">
        			<i class="icon-dashboard"></i>
        			<span class="menu-text">${uiLabelMap.DashBoard}</span>
        			<script type="text/javascript">
						if(window.location.pathname.indexOf("/control/") == 0){
							$("#lidb").attr('class', 'active');
						}else{
							$("#lidb").removeAttr('class');
						}
					</script>
        		</a>
        	</li>-->
        </ul>
        <ul class="nav nav-list">
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
              <#if permission == true>
                <#if thisApp == contextPath || contextPath + "/" == thisApp>
                  <#assign selected = true>
                </#if>
                <#assign thisApp = StringUtil.wrapString(thisApp)>
                <#assign thisURL = thisApp>
                <#if thisApp != "/">
                  <#assign thisURL = thisURL + "/control/main">
                </#if>
                <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
                  <!-- do not display this component-->
                <#else>
                  <li <#if selected>class="active open"</#if>>
                    <#if selected>${setRequestAttribute("menuSelected", "true")}<#else>${setRequestAttribute("menuSelected", "false")}</#if>
                    ${setRequestAttribute("menuItemText",uiLabelMap[display.title])}
                    ${setRequestAttribute("subMenuItemUrl", thisApp+"/control/")}
                    ${setRequestAttribute("menuItemUrl", thisURL+externalKeyParam)}
                    ${setRequestAttribute("extKey", externalKeyParam)}
                  	${screens.render("component://${display.componentConfig.componentName}/widget/CommonScreens.xml#${display.name}")}
                  </li>
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
              <#if permission == true>
                <#if thisApp == contextPath || contextPath + "/" == thisApp>
                  <#assign selected = true>
                </#if>
                <#assign thisApp = StringUtil.wrapString(thisApp)>
                <#assign thisURL = thisApp>
                <#if thisApp != "/">
                  <#assign thisURL = thisURL + "/control/main">
                </#if>
                <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
                  <!-- do not display this component-->
                <#else>
                    <li<#if selected> class="active open"</#if>>
                    <#if selected>${setRequestAttribute("menuSelected", "true")}<#else>${setRequestAttribute("menuSelected", "false")}</#if>
                    ${setRequestAttribute("menuItemText",uiLabelMap[display.title])}
					${setRequestAttribute("subMenuItemUrl", thisApp+"/control/")}
                    ${setRequestAttribute("menuItemUrl", thisURL+externalKeyParam)}
                    ${setRequestAttribute("extKey", externalKeyParam)}
                  	${screens.render("component://${display.componentConfig.componentName}/widget/CommonScreens.xml#${display.name}")}
                  	</li>
                </#if>
              </#if>
            </#list>
        </ul>
    	<div id="sidebar-collapse-bottom"><i class="icon-double-angle-left"></i></div>
    </#if>