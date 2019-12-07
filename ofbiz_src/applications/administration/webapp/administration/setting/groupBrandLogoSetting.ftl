<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/administrationresources/js/setting/groupBrandLogoSetting.js"></script>

<style>
	.theme-setting [class*="span"] {
		text-align: center;
	}
	.theme-setting .pointer {
		cursor: pointer;
	}
</style>

<div class="theme-setting">
	<div class="row-fluid margin-top10">
		<div class="span9">
			<img class="logo-group pointer" id="logoGroup" src="/salesmtlresources/logo/LOGO_demo.png"/>
			<input type="file" id="txtLogoGroup" style="display: none;" accept=".png"/>
		</div>
	</div>

	<div class="row-fluid margin-top10">
		<div class="span9">
			Logo-Group&nbsp;&nbsp;&nbsp;<i class="fa fa-pencil-square-o green logo-group pointer" title="${uiLabelMap.CommonChange}"></i>
		</div>
	</div>
</div>



<#if security.hasEntityPermission("GROUPBRAND", "_UPDATE", session)>
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