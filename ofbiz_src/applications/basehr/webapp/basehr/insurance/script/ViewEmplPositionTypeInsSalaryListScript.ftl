<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>

<script type="text/javascript">
var periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: '${periodType.periodTypeId}',
				description: '${StringUtil.wrapString(periodType.description?if_exists)}'
			},
		</#list>
	</#if>
];
var emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
			{
				emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
				description: '${StringUtil.wrapString(emplPositionType.description?if_exists)} [${emplPositionType.emplPositionTypeId}]'
			},
		</#list>
	</#if>
];
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign endMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(nowTimestamp, timeZone, locale)/>
var globalVar = {
		nowTimestamp: ${nowTimestamp.getTime()},
		startMonth: ${startMonth.getTime()},
		endMonth: ${endMonth.getTime()}
};

var uiLabelMap = {};
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
uiLabelMap.CommonAnd = "${StringUtil.wrapString(uiLabelMap.CommonAnd?default(''))}";
uiLabelMap.ValueNotLessThanZero = "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero?default(''))}";
uiLabelMap.ThruDateMustGreaterThanFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate?default(''))}";
uiLabelMap.ConfirmAddSettingInsuranceSalary = "${StringUtil.wrapString(uiLabelMap.ConfirmAddSettingInsuranceSalary?default(''))}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit?default(''))}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose?default(''))}";
</script>