<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/administrationresources/js/setting/visualThemesSetting.js"></script>

<style>
	.theme-setting [class*="span"] {
		text-align: center;
	}
	.theme-setting .pointer {
		cursor: pointer;
	}
	.theme-setting #logoGroup {
		background: #438eb9;
	}
</style>

<#if layoutSettings.shortcutIcon?has_content>
	<#assign shortcutIcon = layoutSettings.shortcutIcon/>
<#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
	<#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
</#if>

<#if layoutSettings.VT_HDR_IMAGE_URL?has_content>
	<#assign logoGroup = layoutSettings.VT_HDR_IMAGE_URL.get(0)/>
</#if>


<div class="theme-setting">
	<div class="row-fluid margin-top10">
		<div class="span3">
			<img class="shortcut-icon pointer" id="shortcutIcon" src="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon?if_exists)}</@ofbizContentUrl>"/>
			<input type="file" id="txtShortcutIcon" style="display: none;" accept=".ico"/>
		</div>
		<div class="span9">
			<img class="logo-group pointer" id="logoGroup" src="<@ofbizContentUrl>${StringUtil.wrapString(logoGroup?if_exists)}</@ofbizContentUrl>"/>
			<input type="file" id="txtLogoGroup" style="display: none;" accept=".png"/>
		</div>
	</div>

	<div class="row-fluid">
		<div class="span3">
			Shortcut-Icon&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o green shortcut-icon pointer" title="${uiLabelMap.CommonChange}"></i>
		</div>
		<div class="span9">
			Logo-Group&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o green logo-group pointer" title="${uiLabelMap.CommonChange}"></i>
		</div>
	</div>
</div>

<#include "component://securityolbius/webapp/ftl/marco/olbius.ftl"/>

<#if security.hasEntityPermission("THEMESSETTING", "_UPDATE", session)>
<div class="row-fluid">
	<div class="span12 margin-top10">
		<button id="btnSave" class="btn btn-primary form-action-button pull-right hidden" title="${uiLabelMap.CommonSave}"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
	</div>
</div>
</#if>

<div id="jqxNotification">
	<div id="notificationContent">
	</div>
</div>

<script>
	var shortcutIcon = "<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon?if_exists)}</@ofbizContentUrl>";
</script>