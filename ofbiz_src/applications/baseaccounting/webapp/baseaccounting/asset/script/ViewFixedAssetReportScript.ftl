<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLableMap = {};
globalVar.monthQuarterArr = [
	{id: 'month0', description: '${StringUtil.wrapString(uiLabelMap.BACCJanuary)}', type: 'month', value: 0},                             
	{id: 'month1', description: '${StringUtil.wrapString(uiLabelMap.BACCFebruary)}', type: 'month', value: 1},                             
	{id: 'month2', description: '${StringUtil.wrapString(uiLabelMap.BACCMarch)}', type: 'month', value: 2},                             
	{id: 'month3', description: '${StringUtil.wrapString(uiLabelMap.BACCApril)}', type: 'month', value: 3},                             
	{id: 'month4', description: '${StringUtil.wrapString(uiLabelMap.BACCMay)}', type: 'month', value: 4},                             
	{id: 'month5', description: '${StringUtil.wrapString(uiLabelMap.BACCJune)}', type: 'month', value: 5},                             
	{id: 'month6', description: '${StringUtil.wrapString(uiLabelMap.BACCJuly)}', type: 'month', value: 6},                             
	{id: 'month7', description: '${StringUtil.wrapString(uiLabelMap.BACCAugust)}', type: 'month', value: 7},                             
	{id: 'month8', description: '${StringUtil.wrapString(uiLabelMap.BACCSeptember)}', type: 'month', value: 8},                             
	{id: 'month9', description: '${StringUtil.wrapString(uiLabelMap.BACCOctober)}', type: 'month', value: 9},                             
	{id: 'month10', description: '${StringUtil.wrapString(uiLabelMap.BACCNovember)}', type: 'month', value: 10},                             
	{id: 'month11', description: '${StringUtil.wrapString(uiLabelMap.BACCDecember)}', type: 'month', value: 11},                             
	{id: 'quarter1', description: '${StringUtil.wrapString(uiLabelMap.BACCFirstQuarter)}', type: 'quarter', value: 0},                             
	{id: 'quarter2', description: '${StringUtil.wrapString(uiLabelMap.BACCSecondQuarter)}', type: 'quarter', value: 1},                             
	{id: 'quarter3', description: '${StringUtil.wrapString(uiLabelMap.BACCThirdQuarter)}', type: 'quarter', value: 2},                             
	{id: 'quarter4', description: '${StringUtil.wrapString(uiLabelMap.BACCFourthQuarter)}', type: 'quarter', value: 3},                             
	{id: 'year', description: '${StringUtil.wrapString(uiLabelMap.BACCYear)}', type: 'year'},                             
];

globalVar.monthArr = [
	{description: '${StringUtil.wrapString(uiLabelMap.BACCJanuary)}', value: 0},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCFebruary)}', value: 1},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCMarch)}', value: 2},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCApril)}', value: 3},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCMay)}', value: 4},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCJune)}', value: 5},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCJuly)}', value: 6},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCAugust)}', value: 7},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCSeptember)}', value: 8},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCOctober)}', value: 9},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCNovember)}', value: 10},                             
	{description: '${StringUtil.wrapString(uiLabelMap.BACCDecember)}', value: 11},                             
];

globalVar.fixedAssetTypeArr = [
	<#if fixedAssetTypeList?has_content>
		<#list fixedAssetTypeList as fixedAssetType>
		{
			fixedAssetTypeId: '${fixedAssetType.fixedAssetTypeId}',
			description: '${StringUtil.wrapString(fixedAssetType.description)}'
		},
		</#list>
	</#if>
];
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BACCFixedAssetIdShort = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}";
uiLabelMap.BACCFixedAssetName = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetName)}";
uiLabelMap.BACCPleaseChooseAcc = "${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc)}";
</script>