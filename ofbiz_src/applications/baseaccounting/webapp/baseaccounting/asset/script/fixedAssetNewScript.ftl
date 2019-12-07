<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#assign listFixedAssetTypes = delegator.findList("FixedAssetType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].NOT_EQUAL, null), null, null, null, true) />
var fixedAssetTypeData = [
  	<#if listFixedAssetTypes?exists>
	  	<#list listFixedAssetTypes as fixedAssetType>
	  		{
	  			fixedAssetTypeId : "${fixedAssetType.fixedAssetTypeId}",
	  			description : "${StringUtil.wrapString(fixedAssetType.get('description'))}",
				},
		</#list>
	</#if>
];

<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
var uomData = [
	<#if listUoms?exists>
		<#list listUoms as uom>
			<#if uom.uomId == 'VND' || uom.uomId == 'USD' || uom.uomId == 'EUR'>
			{
  				uomId : "${uom.uomId}",
  				description : "${StringUtil.wrapString(uom.get('description'))}",
			},
			</#if>	
		</#list>
	</#if>
];

var periodData = [
  {periodId : "MONTH", description: "${uiLabelMap.BACCMonth}"},
  {periodId : "YEAR", description: "${uiLabelMap.BACCYear}"},
]

<#assign currentOrgId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<#assign listFixedAssetTypeGlAccs = delegator.findByAnd("FixedAssetTypeGlAccount", {'organizationPartyId': currentOrgId}, null, false)>
var assetTypeGlAccData = [
	<#if listFixedAssetTypeGlAccs?exists>
		<#list listFixedAssetTypeGlAccs as item>
		{
			assetGlAccountId : "${item.assetGlAccountId}",
			depGlAccountId : "${item.depGlAccountId}",
			fixedAssetTypeId : "${item.fixedAssetTypeId}",
			fixedAssetId : "${item.fixedAssetId}",
		},
		</#list>
	</#if>
];

var uiLabelMap = {};
var globalVar = {};
<#assign orgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
<#assign partyAcctgPreference = delegator.findOne("PartyAcctgPreference", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orgId), false)!/>
<#if !rootOrgList?exists>
<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
</#if>

<#if partyAcctgPreference?exists>
globalVar.currencyUomId = "${partyAcctgPreference.baseCurrencyUomId}";
</#if>

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
globalVar.rootPartyArr = [
<#if rootOrgList?has_content>
	<#list rootOrgList as rootOrgId>
	<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
	{
		partyId: "${rootOrgId}",
		partyName: "${StringUtil.wrapString(rootOrg.groupName)}"
	},
	</#list>
</#if>
];
globalVar.locale = '${locale}';
uiLabelMap.BACCRateIsWrong = "${StringUtil.wrapString(uiLabelMap.BACCRateIsWrong)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.BACCRateIsWrong = "${StringUtil.wrapString(uiLabelMap.BACCRateIsWrong)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.BACCDateFixedAssetFieldRequired = "${StringUtil.wrapString(uiLabelMap.BACCDateFixedAssetFieldRequired)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BACCAllocationSetting = "${StringUtil.wrapString(uiLabelMap.BACCAllocationSetting)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.BACCSeqId = "${StringUtil.wrapString(uiLabelMap.BACCSeqId)}";
uiLabelMap.BACCAllocPartyId = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyId)}";
uiLabelMap.BACCAllocPartyName = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyName)}";
uiLabelMap.BACCAllocRate = "${StringUtil.wrapString(uiLabelMap.BACCAllocRate)}";
uiLabelMap.BACCAllocGlAccoutId = "${StringUtil.wrapString(uiLabelMap.BACCAllocGlAccoutId)}";
uiLabelMap.BACCAllocPartyCode = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyCode)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.ValueMustBeLessOrEqualThanOneHundred = "${StringUtil.wrapString(uiLabelMap.ValueMustBeLessOrEqualThanOneHundred)}";
uiLabelMap.TotalAllocateForPartyMustEqualOneHundred = "${StringUtil.wrapString(uiLabelMap.TotalAllocateForPartyMustEqualOneHundred)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.FixedAssetIsNotAllcated = "${StringUtil.wrapString(uiLabelMap.FixedAssetIsNotAllcated)}";
uiLabelMap.CreateFixedAssetConfirm = "${StringUtil.wrapString(uiLabelMap.CreateFixedAssetConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
</script>