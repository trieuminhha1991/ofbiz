<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<@jqGridMinimumLib/>
<script type="text/javascript">
	var localeStr = "VI"; 
	<#assign localeStr = "VI" />
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign packStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PACK_STATUS"), null, null, null, false) />
    var statusData = new Array();
	<#list packStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		statusData.push(row);
	</#list>
    var row = {};
    row['statusId'] = "NULL";
    row['description'] = "${StringUtil.wrapString(uiLabelMap.BLUnAssignedTripOrder)}";
    statusData.push(row);
	var acc = false;
	<#if security.hasPermission("ACCOUNTING_VIEW",	 userLogin)>
		acc = true;
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/pack/listPacks.js"></script>