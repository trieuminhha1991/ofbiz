<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.statusArr = [
	<#if statusList?exists>
		<#list statusList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description?if_exists)}'
		},
		</#list>
	</#if>
];
globalVar.geoArr = [
	<#if geoList?exists>
		<#list geoList as geo>
		{
			geoId: '${geo.geoId}',
			geoName: '${StringUtil.wrapString(geo.geoName?if_exists)}'
		},
		</#list>
	</#if>                    
];

if(!globalVar.hasOwnProperty("trainingFormTypeArr")){
	globalVar.trainingFormTypeArr = [
		<#if trainingFormTypeList?exists>
			<#list trainingFormTypeList as trainingFormType>
			{
				trainingFormTypeId: "${trainingFormType.trainingFormTypeId}",
				description: "${StringUtil.wrapString(trainingFormType.description?if_exists)}"
			},
			</#list>
		</#if>                                 
	];
}

if(!globalVar.hasOwnProperty("trainingPurposeTypeArr")){
	globalVar.trainingPurposeTypeArr = [
		<#if trainingPurposeTypeList?exists>
			<#list trainingPurposeTypeList as trainingPurposeType>
			{
				trainingPurposeTypeId: "${trainingPurposeType.trainingPurposeTypeId}",
				description: "${StringUtil.wrapString(trainingPurposeType.description?if_exists)}"
			},
			</#list>
		</#if>
	];
}

var cellClass = function (row, columnfield, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		var date = new Date();
		if (date >= new Date(data.registerThruDate)){
			return "background-cancel";
		} else if (date <= new Date(data.registerThruDate) && date >= new Date(data.registerFromDate)){
			return "background-prepare";
		}
	}
}

var uiLabelMap = {};
uiLabelMap.AllowCancelRegisterBefore = "${StringUtil.wrapString(uiLabelMap.AllowCancelRegisterBefore)}";
uiLabelMap.HRDayLowercase = "${StringUtil.wrapString(uiLabelMap.HRDayLowercase)}";
uiLabelMap.NotAllowCancelRegister = "${StringUtil.wrapString(uiLabelMap.NotAllowCancelRegister)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.RegisterOrCancel = "${StringUtil.wrapString(uiLabelMap.RegisterOrCancel)}";
uiLabelMap.HRCommonNotRegister = "${StringUtil.wrapString(uiLabelMap.HRCommonNotRegister)}";
uiLabelMap.PleaseSelectOption = "${StringUtil.wrapString(uiLabelMap.PleaseSelectOption)}";
uiLabelMap.RegisterTrainingConfirm = "${StringUtil.wrapString(uiLabelMap.RegisterTrainingConfirm)}";
uiLabelMap.UnRegisterTrainingConfirm = "${StringUtil.wrapString(uiLabelMap.UnRegisterTrainingConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.Registered = "${StringUtil.wrapString(uiLabelMap.Registered)}";
uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
uiLabelMap.SelectedUnRegisterToUpdate = "${StringUtil.wrapString(uiLabelMap.SelectedUnRegisterToUpdate)}";

</script>