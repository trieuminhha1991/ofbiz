<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>

<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign isFullPermissitonView = Static["com.olbius.basehr.util.PartyUtil"].isFullPermissionView(delegator, userLogin.userLoginId)/>
<#assign rootList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId, nowTimestamp, nowTimestamp)/>
<#if isFullPermissitonView>
	<#assign rootParty = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#if rootList?has_content>
		<#if (rootList?seq_index_of(rootParty) < 0)>
			<#assign rootList = [rootParty] + rootList />
		</#if>
	<#else>
		<#assign rootList = [rootParty]/>
	</#if>
</#if>
globalVar.rootPartyArr = [
	<#if rootList?has_content>
		<#list rootList as partyId>
		<#assign party = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId), false)/>
		{
			partyId: "${partyId}",
			partyName: "${party.groupName}"
		},
		</#list>
	</#if>
];
globalVar.periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
		{
			periodTypeId: '${periodType.periodTypeId}',
			description: '${StringUtil.wrapString(periodType.description)}',
			uomId: '${periodType.uomId}',
			periodLength: ${periodType.periodLength},
		},
		</#list>
	</#if>
];
globalVar.uomArr = [
	<#if uomList?has_content>
		<#list uomList as uom>
		{
			uomId: '${uom.uomId}',
			description: '${StringUtil.wrapString(uom.description)}',
			abbreviation: '${StringUtil.wrapString(uom.abbreviation)}',
		},
		</#list>
	</#if>
];
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ThruDateMustGreaterThanFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.SettingTarget = "${StringUtil.wrapString(uiLabelMap.SettingTarget)}";
uiLabelMap.HRTarget = "${StringUtil.wrapString(uiLabelMap.HRTarget)}";
uiLabelMap.HRCommonMeasure = "${StringUtil.wrapString(uiLabelMap.HRCommonMeasure)}";
uiLabelMap.KPIWeigth = "${StringUtil.wrapString(uiLabelMap.KPIWeigth)}";
uiLabelMap.KeyPerfIndicator = "${StringUtil.wrapString(uiLabelMap.KeyPerfIndicator)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.KeyPerfIndicatorIsNotSetting = "${StringUtil.wrapString(uiLabelMap.KeyPerfIndicatorIsNotSetting)}";
uiLabelMap.CreateKeyPerfIndPartyTargetConfirm = "${StringUtil.wrapString(uiLabelMap.CreateKeyPerfIndPartyTargetConfirm)}";
uiLabelMap.TargetsHaveNotSetting = "${StringUtil.wrapString(uiLabelMap.TargetsHaveNotSetting)}";
</script>