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
	<#assign returnHeaderTypeId = parameters.returnHeaderTypeId?if_exists/>
	var returnHeaderTypeId = '${returnHeaderTypeId?if_exists}';
	
	<#assign cusReturnStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_RETURN_STTS"), null, null, null, false) />
	var cusStatusData = new Array();
	<#list cusReturnStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		cusStatusData.push(row);
	</#list>
	
	<#assign supReturnStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PORDER_RETURN_STTS"), null, null, null, false) />
	var supStatusData = new Array();
	<#list supReturnStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		supStatusData.push(row);
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
</script>
<script type="text/javascript" src="/logresources/js/return/returnProduct.js"></script>