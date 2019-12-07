<@jqGridMinimumLib />
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.emplTimekeepingSignArr = [
	<#if emplTimekeepingSignList?exists>
		<#list emplTimekeepingSignList as emplTimekeepingSign>
		{
			emplTimekeepingSignId: "${emplTimekeepingSign.emplTimekeepingSignId}",
			sign: "${emplTimekeepingSign.sign}",
			description: "${emplTimekeepingSign.description}",
			<#if emplTimekeepingSign.rateBenefit?exists>
			<#assign rateBenefit = emplTimekeepingSign.rateBenefit * 100 /> 
			rateBenefit: ${rateBenefit}
			</#if>
		},
		</#list>
	</#if>
];

<#if hasOlbPermission("MODULE", "HR_CONFIG", "UPDATE")>
globalVar.editable = "true";
<#else>
globalVar.editable = "false";
</#if>

var datalocal = [
        {
        	value : 'Y',
        	description : '${StringUtil.wrapString(uiLabelMap.CommonYes)}',
        },
        {
	    	value : 'null',
	    	description : '${StringUtil.wrapString(uiLabelMap.CommonNo)}',
        }
];
var uiLabelMap = {};
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.AddNewRowConfirm = "${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.IllegalCharacters = "${StringUtil.wrapString(uiLabelMap.IllegalCharacters)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.HRContainSpecialSymbol = "${StringUtil.wrapString(uiLabelMap.HRContainSpecialSymbol)}";
</script>
