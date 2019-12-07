<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script src="/administrationresources/js/security/Module.js"></script>

<div id="treeGridModule"></div>

<div id="jqxNotification">
<div id="notificationContent"></div>
</div>

<script>
multiLang = _.extend(multiLang, {
	ADPermissionDefault: "${StringUtil.wrapString(uiLabelMap.ADPermissionDefault)}",
	ADApplication: "${StringUtil.wrapString(uiLabelMap.ADApplication)}",
	ADApplicationId: "${StringUtil.wrapString(uiLabelMap.ADApplicationId)}",
	ADApplicationName: "${StringUtil.wrapString(uiLabelMap.ADApplicationName)}",
	userLoginId: "${StringUtil.wrapString(uiLabelMap.userLoginId)}",
	EmployeeName: "${StringUtil.wrapString(uiLabelMap.EmployeeName)}",
	CommonEmployee: "${StringUtil.wrapString(uiLabelMap.CommonEmployee)}",
	ADOverridePermission: "${StringUtil.wrapString(uiLabelMap.ADOverridePermission)}",
	ADUserGroupId: "${StringUtil.wrapString(uiLabelMap.ADUserGroupId)}",
	BSDescription: "${StringUtil.wrapString(uiLabelMap.BSDescription)}",
	ADUser: "${StringUtil.wrapString(uiLabelMap.ADUser)}",
	ADUserGroup: "${StringUtil.wrapString(uiLabelMap.ADUserGroup)}",
	});
var moduleId = "${parameters.moduleId?if_exists}";
</script>