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
	
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.BLFormulaId = "${StringUtil.wrapString(uiLabelMap.BLFormulaId)}";
	uiLabelMap.BLFormula = "${StringUtil.wrapString(uiLabelMap.BLFormula)}";
	uiLabelMap.BLFormulaName = "${StringUtil.wrapString(uiLabelMap.BLFormulaName)}";
	
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.Product = "${StringUtil.wrapString(uiLabelMap.Product)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.Category = "${StringUtil.wrapString(uiLabelMap.Category)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	
	var prFormulaCellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridFromularProduct').jqxGrid('getrowdata', row);
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
<script type="text/javascript" src="/logresources/js/formula/productFormula.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
