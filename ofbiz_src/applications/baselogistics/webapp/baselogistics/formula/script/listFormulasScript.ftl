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
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.BLFormulaId = "${StringUtil.wrapString(uiLabelMap.BLFormulaId)}";
	uiLabelMap.BLFormula = "${StringUtil.wrapString(uiLabelMap.BLFormula)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BLApplyForAll = "${StringUtil.wrapString(uiLabelMap.BLApplyForAll)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.BLProductStore)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.BLFormulaName = "${StringUtil.wrapString(uiLabelMap.ParameterName)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.BLFormulaCodeExisted = "${StringUtil.wrapString(uiLabelMap.BLFormulaCodeExisted)}";
	uiLabelMap.HasErrorWhenProcess = "${StringUtil.wrapString(uiLabelMap.HasErrorWhenProcess)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.BLCannotDelete = "${StringUtil.wrapString(uiLabelMap.BLCannotDelete)}";
	uiLabelMap.BLFormulaInUsing = "${StringUtil.wrapString(uiLabelMap.BLFormulaInUsing)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.BLFormulaParameterId = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterId)}";
	uiLabelMap.BLParameterName = "${StringUtil.wrapString(uiLabelMap.BLParameterName)}";
	uiLabelMap.BLFormulaParameterType = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterType)}";
	uiLabelMap.BLFormulaParameterValue = "${StringUtil.wrapString(uiLabelMap.BLFormulaParameterValue)}";
	uiLabelMap.BLDefaultValue = "${StringUtil.wrapString(uiLabelMap.BLDefaultValue)}";
	uiLabelMap.BLFormulaStructureWrong = "${StringUtil.wrapString(uiLabelMap.BLFormulaStructureWrong)}";
	uiLabelMap.BLFormulaContainParameterNotExisted = "${StringUtil.wrapString(uiLabelMap.BLFormulaContainParameterNotExisted)}";
	uiLabelMap.BLFormulaCharLastWrong = "${StringUtil.wrapString(uiLabelMap.BLFormulaCharLastWrong)}";
	uiLabelMap.BLFormulaCharFirstWrong = "${StringUtil.wrapString(uiLabelMap.BLFormulaCharFirstWrong)}";
	uiLabelMap.BLFormulaMissingOpenParenthesis = "${StringUtil.wrapString(uiLabelMap.BLFormulaMissingOpenParenthesis)}";
	uiLabelMap.BLFormulaMissingCloseParenthesis = "${StringUtil.wrapString(uiLabelMap.BLFormulaMissingCloseParenthesis)}";
	uiLabelMap.BLFormulaPossitionOperatorWrong = "${StringUtil.wrapString(uiLabelMap.BLFormulaPossitionOperatorWrong)}";
	uiLabelMap.BLFormulaNeedToHaveParamOrNumber = "${StringUtil.wrapString(uiLabelMap.BLFormulaNeedToHaveParamOrNumber)}";
	uiLabelMap.BLFormulaNotAllowUnderscore = "${StringUtil.wrapString(uiLabelMap.BLFormulaNotAllowUnderscore)}";
	uiLabelMap.BLFormulaNeedOperationBetweenParam = "${StringUtil.wrapString(uiLabelMap.BLFormulaNeedOperationBetweenParam)}";
	uiLabelMap.BLDeactivated = "${StringUtil.wrapString(uiLabelMap.BLDeactivated)}";
	uiLabelMap.BLFormulaHasBeenDeactivated = "${StringUtil.wrapString(uiLabelMap.BLFormulaHasBeenDeactivated)}";
	uiLabelMap.BLActivated = "${StringUtil.wrapString(uiLabelMap.BLActivated)}";
	uiLabelMap.BLFormulaHasBeenActivated = "${StringUtil.wrapString(uiLabelMap.BLFormulaHasBeenActivated)}";
	uiLabelMap.BLUpdateWhenCalculating = "${StringUtil.wrapString(uiLabelMap.BLUpdateWhenCalculating)}";
	
	var formulaStatusData = [];
	<#assign statusFormulas = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "FORMULA_STATUS")), null, null, null, false) />
	<#list statusFormulas as item>
		var row = {};
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		formulaStatusData.push(row);
	</#list>
	function getFormulaStatusDescription(statusId) {
		for (var i = 0; i < formulaStatusData.length; i ++) {
			if (formulaStatusData[i].statusId == statusId) {
				return formulaStatusData[i].description;
			}
		}
		return parameterTypeId;
	}
	
	var formulaCellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridFromula').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("FML_DEACTIVATED" == data.statusId) {
 				return "background-cancel";
 			} else if ("FML_ACTIVATED" == data.statusId) {
 				return "background-prepare";
 			} else {
 				return "background-prepare";
 			}
 		}
    }
    
    var formulaTypeData = [];
	<#assign formulaTypes = delegator.findList("FormulaType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, null), null, null, null, false) />
	<#list formulaTypes as item>
		var row = {};
		row['formulaTypeId'] = "${item.formulaTypeId?if_exists}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		formulaTypeData.push(row);
	</#list>
    
    var formulaTypeId = "${parameters.formulaTypeId?if_exists}";
    if (formulaTypeId != null && formulaTypeId != undefined && formulaTypeId != "") {
    	var check = false;
    	for (var i = 0; i < formulaTypeData.length; i ++ ) {
    		if (formulaTypeData[i].formulaTypeId == formulaTypeId) {
    			check = true; break;
    		}
    	}
    	if (check == false) {
    		alert("${StringUtil.wrapString(uiLabelMap.BLFormulaTypeNotExisted)}");
    	}
    }
    
    var listParameterData = [];
    var listParameterDataCode = [];
	<#assign params = delegator.findList("FormulaParameter", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "PAR_DEACTIVATED"), null, null, null, false) />
	<#list params as item>
		var row = {};
		var code = "${StringUtil.wrapString(item.get('parameterCode', locale)?if_exists)}";
		row['parameterId'] = "${item.parameterId?if_exists}";
		row['parameterName'] = "${StringUtil.wrapString(item.get('parameterName', locale)?if_exists)}";
		row['parameterCode'] = code;
		row['parameterValue'] = "${StringUtil.wrapString(item.get('parameterValue', locale)?if_exists)}";
		row['parameterTypeId'] = "${StringUtil.wrapString(item.get('parameterTypeId', locale)?if_exists)}";
		row['description'] = "${StringUtil.wrapString(item.get('description', locale)?if_exists)}";
		row['defaultValue'] = "${StringUtil.wrapString(item.get('defaultValue', locale)?if_exists)}";
		listParameterData.push(row);
		listParameterDataCode.push(code);
	</#list>
	
</script>
<script type="text/javascript" src="/logresources/js/formula/listFormulas.js"></script>
<script type="text/javascript" src="/logresources/js/util/suggest.js"></script>
