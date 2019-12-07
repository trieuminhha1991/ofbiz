<#assign externalKeyParam = "&amp;externalLoginKey=" + requestAttributes.externalLoginKey?if_exists>

<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)?exists><#assign partyGroup = requestAttributes.partyGroup></#if>
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<#if defaultOrganizationPartyGroupName?has_content>
  <#assign orgName = defaultOrganizationPartyGroupName?if_exists>
<#else>
  <#assign orgName = "">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>${layoutSettings.companyName}: <#if (page.titleProperty)?has_content>${uiLabelMap[page.titleProperty]}<#else>${(page.title)?if_exists}</#if></title>
    <#if layoutSettings.shortcutIcon?has_content>
      <#assign shortcutIcon = layoutSettings.shortcutIcon/>
    <#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
      <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
    </#if>
    <#if shortcutIcon?has_content>
      <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>?v=2" type="image/x-icon"/>
    </#if>
    <#if layoutSettings.javaScripts?has_content>
        <#--layoutSettings.javaScripts is a list of java scripts. -->
        <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
        <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
        <#list layoutSettings.javaScripts as javaScript>
            <#if javaScriptsSet.contains(javaScript)>
                <#assign nothing = javaScriptsSet.remove(javaScript)/>
                <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
            </#if>
        </#list>
    </#if>
    <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
        <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#list>
    </#if>
    <script src="/aceadmin/assets/js/perfect-scrollbar.jquery.js" type="text/javascript"></script>
    <script src="/aceadmin/assets/js/Underscore1.8.3.js" type="text/javascript"></script>
    <script src="/aceadmin/assets/js/watch.js" type="text/javascript"></script>
    <script src="/aceadmin/assets/js/jquery.cookie.js" type="text/javascript"></script>
    <script src="/aceadmin/assets/js/loading.js" type="text/javascript"></script>
    
    <script async defer src="https://maps.googleapis.com/maps/api/js?v=quarterly&key=AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs&libraries=geometry,places,drawing,visualization"></script>
    <script type="text/javascript" src="/salesmtlresources/js/util/color-slicer.js"></script>
    <script src="/salesmtlresources/js/google_map/oms.min.js"></script>
    
    <@jqOlbCoreLib onlyCore=true/>
    <link rel="stylesheet" href="/aceadmin/assets/css/jquery-ui-1.10.3.custom.min.css" type="text/css">
    <link rel="stylesheet" href="/aceadmin/assets/css/custom-actors.css" type="text/css">
    <link rel="stylesheet" href="/aceadmin/assets/css/perfect-scrollbar.min.css" type="text/css">
	<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.base.css" type="text/css" />
	<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.energyblue.css" type="text/css" />
    <link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.wigetolbius.css" type="text/css" />
    <link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.custom.css" type="text/css" />
    <link rel="stylesheet" href="/aceadmin/assets/css/spinkit/spinkit.min.css" type="text/css">
    <#if layoutSettings.VT_STYLESHEET?has_content>
        <#list layoutSettings.VT_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.styleSheets?has_content>
        <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
        <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
        <#list layoutSettings.rtlStyleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
        <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_EXTRA_HEAD?has_content>
        <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
            ${extraHead}
        </#list>
    </#if>
    <#if layoutSettings.WEB_ANALYTICS?has_content>
      <script language="JavaScript" type="text/javascript">
        <#list layoutSettings.WEB_ANALYTICS as webAnalyticsConfig>
          ${StringUtil.wrapString(webAnalyticsConfig.webAnalyticsCode?if_exists)}
        </#list>
      </script>
    </#if>
    <!-- add the jQuery script 
    <script type="text/javascript" src="/aceadmin/jqw/scripts/jquery-1.10.2.min.js"></script>-->
</head>
<#if layoutSettings.headerImageLinkUrl?exists>
  <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}">
<#else>
  <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}">
</#if>
<#assign organizationLogoLinkURL = "${layoutSettings.organizationLogoLinkUrl?if_exists}">

<#if layoutSettings.VT_HDR_IMAGE_URL?has_content>
<#assign logoGroup = layoutSettings.VT_HDR_IMAGE_URL.get(0)/>
</#if>

<#if person?has_content>
  <#assign userName = person.lastName?if_exists + " " + person.middleName?if_exists + " " + person.firstName?if_exists>
<#elseif partyGroup?has_content>
  <#assign userName = partyGroup.groupName?if_exists>
<#elseif userLogin?exists>
  <#assign userName = userLogin.userLoginId>
<#else>
  <#assign userName = "">
</#if>
<body<#if userLogin?has_content><#else> class="login-layout"</#if>>
<#if userLogin?has_content>
<style>
.navbar{
   position: fixed;
   top: 0;
   left: 0;
   width: 100%;
   z-index: 10000;
}
</style>
</#if>
		<#include "component://aceadmin/webapp/aceadmin/includes/preferMenu.ftl"/>
		<@loading hide="true" background="rgba(0, 0, 0, 0.5)"/>
  		<div class="navbar navbar-inverse">
		  <div class="navbar-inner">
		   <div id="nav" class="container-fluid">
		   		<div class="logo">
					<a href="<@ofbizUrl>main</@ofbizUrl>" class="brand" style="padding:6px 10px 0px">
						<small>
							<span class="logo-group-name">
								<img width="57" height="33" src="<@ofbizContentUrl>${StringUtil.wrapString(logoGroup?if_exists)}</@ofbizContentUrl>"/>
							</span>
							<span class="logo-text">
								<#if businessTitle?has_content>
									<b>${organizationName?if_exists} - ${uiLabelMap.get(businessTitle)}</b>
								</#if>
							</span>
						</small>
					</a>
				</div>
			  <#if userLogin?has_content>
			  <ul class="nav ace-nav pull-right">
					<li class="grey hide">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<i class="icon-tasks"></i>
							<#assign iTasks = 0>
							<#if layoutSettings.middleTopMessage1?has_content> <#assign iTasks = iTasks + 1> </#if>
							<#if layoutSettings.middleTopMessage2?has_content> <#assign iTasks = iTasks + 1> </#if>
							<#if layoutSettings.middleTopMessage3?has_content> <#assign iTasks = iTasks + 1> </#if>
							<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "&externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
							<#if (externalLoginKey)?exists><#assign externalKeyParam = "&externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
							<span class="badge">${iTasks}</span>
						</a>
						<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-closer">
							<li class="nav-header">
								<i class="icon-ok"></i> ${uiLabelMap.TasksToComplete}
							</li>
							
							<li <#if !layoutSettings.middleTopMessage1?has_content> style="display:none;" </#if>>
								<a href="${StringUtil.wrapString(layoutSettings.middleTopLink1!)}<#if externalKeyParam?has_content>${externalKeyParam}</#if>">
									<div class="clearfix">
										<span class="pull-left">${layoutSettings.middleTopMessage1?if_exists}</span>
									</div>
								</a>
							</li>
							
							<li <#if !layoutSettings.middleTopMessage2?has_content> style="display:none;" </#if>>
								<a href="${StringUtil.wrapString(layoutSettings.middleTopLink2!)}<#if externalKeyParam?has_content>${externalKeyParam}</#if>">
									<div class="clearfix">
										<span class="pull-left">${layoutSettings.middleTopMessage2?if_exists}</span>
									</div>
								</a>
							</li>
							
							<li <#if !layoutSettings.middleTopMessage3?has_content> style="display:none;" </#if>>
								<a href="${StringUtil.wrapString(layoutSettings.middleTopLink3!)}<#if externalKeyParam?has_content>${externalKeyParam}</#if>">
									<div class="clearfix">
										<span class="pull-left">${layoutSettings.middleTopMessage3?if_exists}</span>
									</div>
								</a>
							</li>
							
							<li>
								<a href="/hrolbius/control/MyTasks">
									${uiLabelMap.ThemeAllTasks}
									<i class="icon-arrow-right"></i>
								</a>
							</li>
						</ul>
					</li>

					<li class="purple" id="ntfarea">
						${screens.render("component://Notification/widget/notificationScreens.xml#NotificationListBar")}
					</li>
					<#include "component://Notification/webapp/notification/ftl/getNotInPeriod.ftl"/>

					<li class="green hide">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">
							<i class="icon-envelope-alt icon-animated-vertical icon-only"></i>
							<span class="badge badge-success">0</span>
						</a>
						<ul class="pull-right dropdown-navbar dropdown-menu dropdown-caret dropdown-closer">
							<li class="nav-header">
								<i class="icon-envelope"></i> 0 ${uiLabelMap.Message}
							</li>
							<li style="display:none;">
								<a href="#">
									<span class="msg-body">
										<span class="msg-title">
											<span class="blue">Bob:</span>
											Nullam quis risus eget urna mollis ornare ...
										</span>
										<span class="msg-time">
											<i class="icon-time"></i> <span>3:15 pm</span>
										</span>
									</span>
								</a>
							</li>
							
							<li style="display:none;">
								<a href="#">
									See all messages
									<i class="icon-arrow-right"></i>
								</a>
							</li>									
	
						</ul>
					</li>

					<li class="light-blue user-profile">
						<a class="user-menu dropdown-toggle" href="#" data-toggle="dropdown">
							<span id="user_info">
								<small>${uiLabelMap.CommonWelcome},</small> ${userName}
							</span>
							<i class="icon-caret-down"></i>
						</a>
						<ul id="user_menu" class="pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-closer">
						<!--	<li><a href="<@ofbizUrl>editperson</@ofbizUrl>?partyId=${partyId?if_exists}"><div class="row-fluid"><div class="span2"><i class="icon-cog"></i></div><div class="span8">${uiLabelMap.CommonSetting}</div></div></a></li> -->
							
							<#if security.hasEntityPermission("HR_PROFILE", "_VIEW", session)>
								<li><a href="<@ofbizUrl>ViewEmployeeProfile</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="icon-user"></i></div><div class="span8">${uiLabelMap.CommonProfile}</div></div></a></li>
							</#if>
							<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
								<li><a href="<@ofbizUrl>DistributorDetail</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="icon-user"></i></div><div class="span8">${uiLabelMap.CommonProfile}</div></div></a></li>
							</#if>
							<#if security.hasEntityPermission("MENU_MODULE", "_VIEW", session) && componentNameSc?exists>
								<#assign businessMenus = Static["com.olbius.basehr.util.SecurityUtil"].getBussinessRoles(userLogin.partyId, delegator)>
								<#if businessMenus?has_content>
								<li><a href="#"><div class="row-fluid"><div class="span2"><i class="fa-bars"></i></div><div class="span8">${uiLabelMap.CommonModule}</div></div></a>
									<ul class="obl_submenu">
										<#assign currentMenu = Static["org.ofbiz.base.util.UtilHttp"].getModule(request)>
										<#list businessMenus as bMenu>
											<#assign applicationMenuLocation = "component://${componentNameSc?if_exists}/widget/${componentResourceName?if_exists}Menus.xml" />
											<#if applicationMenuLocation?exists>
												<#assign appModelMenu = Static["org.ofbiz.widget.menu.MenuFactory"].getMenuFromLocation(applicationMenuLocation,bMenu,delegator,dispatcher)>
												<#if appModelMenu?exists>
												<li <#if currentMenu?if_exists == appModelMenu.getModule()>class="selected-menu"</#if>>
									                <a href="<@ofbizUrl>setSessionModule</@ofbizUrl>?newModule=${appModelMenu.getModule()}">
									                	<div>${appModelMenu.getModuleName()} &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; [${appModelMenu.getModule()}]</div>
								                	</a>
									            </li>
									            </#if>
								            </#if>
										</#list>
									</ul>
								</li>
								</#if>
							</#if>
							<#assign listSubsidiaries = Static["com.olbius.basehr.util.SecurityUtil"].getOrganization(userLogin.userLoginId, "${parameters.basePermission?if_exists}", delegator, false)!>
							<#if listSubsidiaries?has_content && (listSubsidiaries?size > 1 || !userLogin.lastOrg?has_content)>
							<li><a href="#"><div class="row-fluid"><div class="span2"><i class="icon-code-fork"></i></div><div class="span8">${uiLabelMap.CommonOrg}</div></div></a>
								<ul class="obl_submenu">
									<#list listSubsidiaries as subsidiary>
										<#assign groupName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, subsidiary, false)>
										<li <#if StringUtil.wrapString(organizationName?if_exists) == StringUtil.wrapString(groupName)>class="selected-menu"</#if>>
											<a href="<@ofbizUrl>setSessionOrg</@ofbizUrl>?newOrg=${subsidiary}">
							                	<div>${groupName?if_exists} &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; [${subsidiary}]</div>
						                	</a>
					                	</li>
									</#list>
								</ul>
							</li>
							</#if>
							<#assign availableLocales = Static["org.ofbiz.base.util.UtilMisc"].availableLocales()/>
							<#if availableLocales?has_content>
							<li><a href="<@ofbizUrl>ListLocales</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="icon-flag"></i></div><div class="span8">${uiLabelMap.CommonLanguageTitle}</div></div></a> <!--<@ofbizUrl>ListLocales</@ofbizUrl> -->
								<ul class="obl_submenu">
									<#assign altRow = true>
								    <#list availableLocales as availableLocale>
								        <#assign altRow = !altRow>
								        <#assign langAttr = availableLocale.toString()?replace("_", "-")>
								        <#assign langDir = "ltr">
								        <#if "ar.iw"?contains(langAttr?substring(0, 2))>
								            <#assign langDir = "rtl">
								        </#if>
							            <li <#if locale?if_exists == availableLocale>class="selected-menu"</#if>>
							                <a href="<@ofbizUrl>setSessionLocale</@ofbizUrl>?newLocale=${availableLocale.toString()}">
							                	<div>${availableLocale.getDisplayName(availableLocale)} &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; [${availableLocale.toString()}]</div>
						                	</a>
							            </li>
								    </#list>
								</ul>
							</li>
							</#if>
							<#if security.hasEntityPermission("DISTRIBUTOR", "_ADMIN", session)>
							<li><a href="<@ofbizUrl>ChangePassword</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="fa-cog"></i></div><div class="span8">${uiLabelMap.HRChangePassword}</div></div></a></li>
							</#if>
							<#-- hoanm add config websiteid start-->
							<#if security.hasEntityPermission("ECOMMERCE", "_ADMIN", session) || security.hasEntityPermission("PRODUCT_CONTENT", "_UPDATE", session)
								|| security.hasEntityPermission("PRODUCT_CONTENT", "_CREATE", session)>
							<li><a href="<@ofbizUrl>ListWebsites</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="fa-globe"></i></div><div class="span8">${uiLabelMap.Website}</div></div></a>
								<ul class="obl_submenu">
								<#assign currentWebsiteId = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].getCurrentWebSite(delegator, userLogin)/>
								<#assign availableWebsites = Static["com.olbius.baseecommerce.backend.ConfigWebSiteServices"].availableWebsites(delegator)/>
								<#list availableWebsites as website>
									<li <#if currentWebsiteId?exists><#if currentWebsiteId == website.webSiteId>class="selected-menu"</#if></#if>>
						                <a href="activeWebsite?webSiteId=${website.webSiteId}">
						                	<div>${website.siteName} &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp; [${website.webSiteId}]</div>
					                	</a>
						            </li>
								</#list>
								</ul>
							</li>
							</#if>
							<#-- hoanm add config websiteid end-->
							<li class="divider"></li>
							<li><a href="<@ofbizUrl>logout</@ofbizUrl>"><div class="row-fluid"><div class="span2"><i class="icon-off"></i></div><div class="span8">${uiLabelMap.CommonLogout}</div></div></a></li>
						</ul>
					</li>
			  </ul><!--/.ace-nav-->
			  <script type="text/javascript">
				var locale = "${locale.toString()}"
				var commonLabelMap = {
					confirmButton : "${uiLabelMap.FormFieldTitle_confirmButton}",
					UpdateError : "${uiLabelMap.wgupdateerror}",
					UpdateSuccess : "${uiLabelMap.wgupdatesuccess}",
				};
			  	(function(){
			  		jQuery.fn.onDivResize = function (trigger, millis) {
				        if (millis == null) millis = 100;
				        var o = $(this[0]);
				        if (o.length < 1) return o;
				
				        var lastWidth = null;
				        var lastHeight = null;
				        setInterval(function () {
				            if (lastWidth == null) lastWidth = o.width();
				            if (lastHeight == null) lastHeight = o.height();
				            var newWidth = o.width();
				            var newHeight = o.height();
				            if (lastWidth != newWidth || lastHeight != newHeight) {
				                $(this).trigger('onDivResize', { lastWidth: lastWidth, lastHeight: lastHeight, newWidth: newWidth, newHeight: newHeight});
				                if (typeof (trigger) == "function") trigger(lastWidth, lastHeight, newWidth, newHeight);
				                lastWidth = o.width();
				                lastHeight = o.height();
				            }
				        }, millis);
				
				        return o;
				    };
			  	}());
				function nav(){
					$('div#nav ul li').mouseover(function() {
						$(this).find('ul:first').show();
					});
					
					$('div#nav ul li').mouseleave(function() {
						$('div#nav ul li ul').hide();
					});
					
					$('div#nav ul li ul').mouseleave(function() {						
						$('div#nav ul li ul').hide();;
					});
				};
				var initSidebar = function (){
					var sb = $("#sidebar");
					var navi = $("#nav");
					var height = navi.height();
					sb.css('top', height + 'px');
					sb.perfectScrollbar({suppressScrollX: true});
					$("#sidebar-collapse").on('click', function(){
						var flag = $("#sidebar").hasClass('menu-min');
						$(window).resize();
						$(window).trigger('resize');
						if(flag){
							$.cookie('sidebar', 'off');	
						}else{
							$.cookie('sidebar', 'on');
						}
					});
					// $(window).bind('scroll', function(){
						// var scroll = $(window).scrollTop();
						// if(scroll){
							// if(scroll < height){
								// var sub = height - scroll;
								// sb.css('top', sub + "px");	
							// }else{
								// sb.css('top',"0px");	
							// }
						// }else{
							// sb.css('top',height + "px");
						// }
					// });
				};
				var hideSidebar = function(){
					$("#sidebar").addClass('menu-min');
					$.cookie('sidebar', 'off');
				};
				var showSidebar = function(){
					$("#sidebar").removeClass('menu-min');
					$.cookie('sidebar', 'on');
				};
				$(document).ready(function() {
					nav();
					initSidebar();
					BasicUtils.initNoSpace($('.no-space'));
					MenuHarmony.adjust();
				});
				$(document).ajaxError(function( event, request, settings ) {
					if (request.status == 403) {
						location.reload();
					}
				});
				var MenuHarmony = (function() {
					var adjust = function() {
						if (distance() > ($(window).height()/2)) {
							$("#sidebar").scrollTop($(window).height());
						}
					};
					var distance = function() {
						var sidebar = $("#sidebar").find("li[class='open active']").offset();
						var topDistance = 0;
						if (sidebar){
							topDistance = sidebar.top
						}
						return topDistance;
					};
					return {
						adjust: adjust
					};
				})();
			</script>
			  <style type="text/css">
			  	#user_menu ul,
				#user_menu ul li {
					margin:0;
					padding:0;
					list-style:none;
				}
				#user_menu ul li{
					float:left;
					display:block;
				}
				.obl_submenu {
					right:100%;
					position: absolute;
					width: 100%;
					background: #FFF;
					display: none;
					line-height: 26px;
					z-index: 1000;
					margin-top:-30px !important;
					border-radius: 0;
					box-shadow: 0 2px 4px rgba(0,0,0,0.2);
					border:1px solid #ccc;
					padding:5px 0 !important;
				}
				.obl_submenu:before{
					position: absolute;
					top: 4px;
					right: -7px;
					display: inline-block;
					border-bottom: 7px solid transparent;
					border-left: 7px solid #efefef;
					border-top: 7px solid transparent;
					border-left-color: rgba(0,0,0,0.2);
					content: '';
				}
				.obl_submenu:after{
					position: absolute;
					top: 5px;
					right: -6px;
					display: inline-block;
					border-bottom: 6px solid transparent;
					border-left: 6px solid #fff;
					border-top: 6px solid transparent;
					content: '';
				}
				.obl_submenu li{
					display:block !important;
					width:93% !important;
				}
				.obl_submenu a{
					display:block !important;
					width:92% !important;
					color: black !important;
				}
				.obl_submenu a:hover{
					text-decoration:none !important;
				}
				.obl_submenu a div{
					padding:4px;
				}
			  </style>
			  </#if>	
		   </div><!--/.container-fluid-->
		  </div><!--/.navbar-inner-->
		</div><!--/.navbar-->
  
  <div id="wait-spinner" style="display:none">
    <div id="wait-spinner-image"></div>
  </div>
  <div class="container-fluid main-container" id="main-container">
			<a class="menu-toggler" id="menu-toggler" href="#">
    <span class="menu-text"></span>
   </a><!-- menu toggler -->