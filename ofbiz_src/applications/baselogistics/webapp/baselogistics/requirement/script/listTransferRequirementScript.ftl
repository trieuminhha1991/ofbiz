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
	<#assign transferTypeId = parameters.transferTypeId?if_exists/>
	var transferTypeId = '${transferTypeId?if_exists}';
	<#assign requirement = delegator.findOne("Requirement", {"requirementId" : parameters.requirementId?if_exists}, false)!>
	<#assign transferStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "TRANSFER_STATUS"), null, null, null, false) />
	var transferStatusData = new Array();
	<#list transferStatus as item>
		var row = {};
		row['statusId'] = "${item.statusId}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale))}";
		transferStatusData.push(row);
	</#list>
	
	<#assign transferTypes = delegator.findList("TransferType", null, null, null, null, false) />
	var transferTypeData = new Array();
	<#list transferTypes as item>
		<#assign listChilds = delegator.findList("TransferType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", item.transferTypeId?if_exists), null, null, null, false) />
		<#if !(listChilds[0]?has_content && !item.parentTypeId?has_content)>
			var row = {};
			row['transferTypeId'] = "${item.transferTypeId?if_exists}";
			row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
			transferTypeData.push(row);
		</#if>
	</#list>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
</script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/transfer/listTransfer.js"></script>