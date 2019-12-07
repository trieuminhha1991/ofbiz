<@jqGridMinimumLib />

<script type="text/javascript">	
	var formulaParameterTypeId = null;
	<#if parameters.formulaParameterTypeId?has_content>
		formulaParameterTypeId = '${parameters.formulaParameterTypeId?if_exists}';
	</#if>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.BLFormulaParameterId = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterId)}";
	uiLabelMap.BLFormulaParameter = "${StringUtil.wrapString(uiLabelMap.BLFormula)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BSPayToParty = "${StringUtil.wrapString(uiLabelMap.BSPayToParty)}";
	uiLabelMap.BLStoreName = "${StringUtil.wrapString(uiLabelMap.BLStoreName)}";
	uiLabelMap.BLProductStoreId = "${StringUtil.wrapString(uiLabelMap.BLProductStoreId)}";
	uiLabelMap.BLApplyForAll = "${StringUtil.wrapString(uiLabelMap.BLApplyForAll)}";
	uiLabelMap.BLProductStore = "${StringUtil.wrapString(uiLabelMap.BLProductStore)}";
	uiLabelMap.Owner = "${StringUtil.wrapString(uiLabelMap.Owner)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.ParameterName = "${StringUtil.wrapString(uiLabelMap.ParameterName)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.BLParameterCodeExisted = "${StringUtil.wrapString(uiLabelMap.BLParameterCodeExisted)}";
	uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.BLCannotDelete = "${StringUtil.wrapString(uiLabelMap.BLCannotDelete)}";
	uiLabelMap.BLParameterInUsing = "${StringUtil.wrapString(uiLabelMap.BLParameterInUsing)}";
	uiLabelMap.BLCannotEditSystemParameter = "${StringUtil.wrapString(uiLabelMap.BLCannotEditSystemParameter)}";
	uiLabelMap.BLCannotDeleteSystemParameter = "${StringUtil.wrapString(uiLabelMap.BLCannotDeleteSystemParameter)}";
	uiLabelMap.BLParameterHasBeenDeactivated = "${StringUtil.wrapString(uiLabelMap.BLParameterHasBeenDeactivated)}";
	uiLabelMap.BLParameterHasBeenActivated = "${StringUtil.wrapString(uiLabelMap.BLParameterHasBeenActivated)}";
	uiLabelMap.BLDeactivated = "${StringUtil.wrapString(uiLabelMap.BLDeactivated)}";
	uiLabelMap.BLActivated = "${StringUtil.wrapString(uiLabelMap.BLActivated)}";
	var parameterTypeData = [];
	var allParameterTypeData = [];
	<#assign paramTypes = delegator.findList("FormulaParameterType", null, null, null, null, false) />
	<#list paramTypes as item>
		var row = {};
		row['parameterTypeId'] = "${item.parameterTypeId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		<#if item.parentTypeId?exists && "PARAM_CUSTOMIZABLE" == item.parentTypeId>
			parameterTypeData.push(row);
		</#if>
		allParameterTypeData.push(row);
	</#list>
	function getParameterDescription(parameterTypeId) {
		for (var i = 0; i < allParameterTypeData.length; i ++) {
			if (allParameterTypeData[i].parameterTypeId == parameterTypeId) {
				return allParameterTypeData[i].description;
			}
		}
		return parameterTypeId;
	}
	
	var cellClassName = function (row, columnfield, value) {
 		var data = $('#jqxgridFromulaParameter').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARAM_SYSTEM" == data.parameterTypeId) {
 				return "background-important-nd";
 			} else if ("PARAM_CUSTOMIZABLE" == data.statusId) {
 				return "background-prepare";
 			} else if ("PAR_DEACTIVATED" == data.statusId) {
 				return "background-cancel";
			}
 		}
    }
    
    var formulaParameterStatusData = [];
	<#assign statusFormulaParams = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "PARAMETER_STATUS")), null, null, null, false) />
	<#list statusFormulaParams as item>
		var row = {};
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		formulaParameterStatusData.push(row);
	</#list>
	function getFormulaParameterStatusDescription(statusId) {
		for (var i = 0; i < formulaParameterStatusData.length; i ++) {
			if (formulaParameterStatusData[i].statusId == statusId) {
				return formulaParameterStatusData[i].description;
			}
		}
		return parameterTypeId;
	}
</script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasComboBox=true hasValidator=true/>
<script type="text/javascript" src="/logresources/js/formula/listFormulaParmeters.js"></script>
