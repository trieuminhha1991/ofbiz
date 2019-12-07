<@jqGridMinimumLib />

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<@jqOlbCoreLib hasComboBox=true hasValidator=true/>

<script type="text/javascript">	
	var formulaTypeId = null;
	<#if parameters.formulaTypeId?has_content>
		formulaTypeId = '${parameters.formulaTypeId?if_exists}';
	</#if>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	
	uiLabelMap.BLFormulaParameterId = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterId)}";
	uiLabelMap.BLParameterName = "${StringUtil.wrapString(uiLabelMap.BLParameterName)}";
	uiLabelMap.BLFormulaParameterType = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterType)}";
	uiLabelMap.BLFormulaParameterValue = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterValue)}";
	
	uiLabelMap.BSPayToParty = "${StringUtil.wrapString(uiLabelMap.BSPayToParty)}";
	uiLabelMap.BLStoreName = "${StringUtil.wrapString(uiLabelMap.BLStoreName)}";
	uiLabelMap.BLProductStoreId = "${StringUtil.wrapString(uiLabelMap.BLProductStoreId)}";
	uiLabelMap.BLApplyForAll = "${StringUtil.wrapString(uiLabelMap.BLApplyForAll)}";
	uiLabelMap.BLProductStore = "${StringUtil.wrapString(uiLabelMap.BLProductStore)}";
	uiLabelMap.Owner = "${StringUtil.wrapString(uiLabelMap.Owner)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	
	
	var listParameterCustomizeData = [];
	<#assign params = delegator.findList("FormulaParameter", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "PAR_DEACTIVATED"), null, null, null, false) />
	<#list params as item>
		<#if item.parameterTypeId != 'PARAM_SYSTEM'>
			var row = {};
			var code = "${StringUtil.wrapString(item.get('parameterCode', locale)?if_exists)}";
			row['parameterId'] = "${item.parameterId?if_exists}";
			row['parameterName'] = "${StringUtil.wrapString(item.get('parameterName', locale)?if_exists)}";
			row['parameterCode'] = code;
			row['parameterValue'] = "${StringUtil.wrapString(item.get('parameterValue', locale)?if_exists)}";
			row['parameterTypeId'] = "${StringUtil.wrapString(item.get('parameterTypeId', locale)?if_exists)}";
			row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
			row['defaultValue'] = "${StringUtil.wrapString(item.get('defaultValue', locale)?if_exists)}";
			listParameterCustomizeData.push(row);
		</#if>
	</#list>
	
	var paramStoreCellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridParameterProductStore').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			var thruDateTmp = new Date(data.thruDate);
 			var now = new Date();
 			if (now > thruDateTmp && data.thruDate != null) {
 				return "background-cancel";
 			} else {
 				return "background-prepare";
 			}
 		}
    }
	
</script>
<script type="text/javascript" src="/logresources/js/formula/parameterProductStore.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
