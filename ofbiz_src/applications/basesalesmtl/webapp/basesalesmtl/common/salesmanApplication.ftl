<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/salesmtlresources/js/common/salesmanApplication.js"></script>

<style>
.jqx-checkbox.jqx-checkbox-olbius {
    margin-left: 0px !important;
}
</style>

<div class="row-fluid margin-top10">
	<div class="span6">
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.BSRequiresGPS}</label></div>
			<div class="span7"><div id="txtRequiresGPS"></div></div>
		</div>
	</div>
	<div class="span6">
		<div class="row-fluid">
			<div class="span5"><label class="text-right">${uiLabelMap.BSTheMinimumDistance}</label></div>
			<div class="span7"><div id="txtTheMinimumDistance"></div></div>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("APPLICATIONSETTING", "_UPDATE", session)>
<div class="row-fluid">
	<div class="span12 margin-top10">
		<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
	</div>
</div>
</#if>

<div id="jqxNotification">
<div id="notificationContent"></div>
</div>