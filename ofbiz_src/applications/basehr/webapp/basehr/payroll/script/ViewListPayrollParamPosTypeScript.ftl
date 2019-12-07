<@jqGridMinimumLib/>					
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<script type="text/javascript">
if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
globalVar.monthStart = ${monthStart.getTime()};
globalVar.monthEnd = ${monthEnd.getTime()};

globalVar.useRoleTypeGroup = ${useRoleTypeGroup};

if(typeof(uiLabelMap) == "undefined"){
	uiLabelMap = {};	
}
uiLabelMap.HRCommonNotSetting = "${StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)}";
uiLabelMap.AmountValueGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.AmountValueGreaterThanZero)}";
uiLabelMap.CommonRequired = "${StringUtil.wrapString(uiLabelMap.CommonRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.EmplPositionTypeRateGeoApplUpdate = "${StringUtil.wrapString(uiLabelMap.EmplPositionTypeRateGeoApplUpdate)}";
uiLabelMap.HrCommonRates = "${StringUtil.wrapString(uiLabelMap.HrCommonRates)}";
uiLabelMap.HRCommonAmount = "${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}";
uiLabelMap.AddRowDataConfirm = "${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}";

var emplPosTypeArr = [
	<#if emplPosType?exists>
		<#list emplPosType as posType>
			{
				emplPositionTypeId:"${posType.emplPositionTypeId}",
				description: "${StringUtil.wrapString(posType.description)}" 
			},
		</#list>
	</#if>
];	

var dataEmplPosTypeAdapter = new $.jqx.dataAdapter(emplPosTypeArr, {autoBind: true});
var dataEmplPosTypeList = dataEmplPosTypeAdapter.records;

var periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: "${periodType.periodTypeId}",
				description: "${StringUtil.wrapString(periodType.description?if_exists)}"
			},
		</#list>
	</#if>	                     
];

var filterBoxAdapter = new $.jqx.dataAdapter(periodTypeArr, {autoBind: true});
var dataSourePeriodTypeList = filterBoxAdapter.records;

var roleTypeGroupArr = [
	<#if roleTypeGroupList?has_content>
		<#list roleTypeGroupList as roleTypeGroup>
			{
				roleTypeGroupId: "${roleTypeGroup.roleTypeGroupId}",
				description: "${roleTypeGroup.description}"
			},
			
		</#list>
	</#if>	
];


var parametersArr = [
	<#if payrollParametersList?has_content>
		<#list payrollParametersList as param>
		{
			code: "${param.code}",
			name: "${param.name}",
			type: "${param.type?if_exists}",
			periodTypeId: "${param.periodTypeId?if_exists}"
		},
		</#list>
	</#if>
];

var uomArray = [
	<#if uomList?exists>
		<#list uomList as uom>
			{
				uomId: "${uom.uomId}",
				description: "${uom.description?if_exists}"
			},
		</#list>		
	</#if>
];

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
globalVar.rootPartyArr = [
             			<#if rootOrgList?has_content>
        				<#list rootOrgList as rootOrgId>
        				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
        				{
        					partyId: "${rootOrgId}",
        					partyName: "${rootOrg.groupName}"
        				},
        				</#list>
        			</#if>
        		];
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

</script>