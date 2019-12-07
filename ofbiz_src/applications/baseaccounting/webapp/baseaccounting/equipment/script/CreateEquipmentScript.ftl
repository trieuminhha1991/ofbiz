<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
</#if>
<#assign listPartyGlAcc = delegator.findByAnd("FAPartyGlAccountView", null, null, false)>
var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());
var globalVar = {};
var uiLabelMap = {};
<#if defaultCurrencyUomId?exists>
globalVar.defaultCurrencyUomId = "${defaultCurrencyUomId}";
</#if>

globalVar.partyGlAccountData = [
	<#if listPartyGlAcc?exists>
  		<#list listPartyGlAcc as item>
  		{
  			partyId : "${item.partyId}",
  			glAccountId : "${item.glAccountId}",
  			accountName : "${StringUtil.wrapString(item.get('accountName'))}",
		},
		</#list>
	</#if>
];

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
globalVar.equipmentTypeArr = [
	<#if equipmentTypeList?exists>
		<#list equipmentTypeList as equipmentType>
		{
			equipmentTypeId : "${equipmentType.equipmentTypeId}",
  			description : '${StringUtil.wrapString(equipmentType.get("description", locale))}',
		},
		</#list>
	</#if>
];
globalVar.uomCurrencyArr = [
	<#if uomCurrencyList?exists>
		<#list uomCurrencyList as uom>
		{
			uomId : "${uom.uomId}",
   			description : '${StringUtil.wrapString(uom.get("description"))}',
   			abbreviation : '${StringUtil.wrapString(uom.get("abbreviation"))}',
		},
		</#list>
	</#if>
];
globalVar.uomProdPackArr = [
	<#if uomProdPackList?exists>
		<#list uomProdPackList as uom>
		{
			uomId : "${uom.uomId}",
   			description : '${StringUtil.wrapString(uom.get("description"))}',
		},
		</#list>
	</#if>
];

uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.BACCAllocationSetting = "${StringUtil.wrapString(uiLabelMap.BACCAllocationSetting)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.BACCSeqId = "${StringUtil.wrapString(uiLabelMap.BACCSeqId)}";
uiLabelMap.BACCAllocPartyCode = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyCode)}";
uiLabelMap.BACCAllocPartyName = "${StringUtil.wrapString(uiLabelMap.BACCAllocPartyName)}";
uiLabelMap.BACCAllocRate = "${StringUtil.wrapString(uiLabelMap.BACCAllocRate)}";
uiLabelMap.BACCAllocGlAccoutId = "${StringUtil.wrapString(uiLabelMap.BACCAllocGlAccoutId)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.ValueMustLessThanOneHundred = "${StringUtil.wrapString(uiLabelMap.ValueMustLessThanOneHundred)}";
uiLabelMap.TotalAllocateForPartyMustEqualOneHundred = "${StringUtil.wrapString(uiLabelMap.TotalAllocateForPartyMustEqualOneHundred)}";
uiLabelMap.CreateEquipmentConfirm = "${StringUtil.wrapString(uiLabelMap.CreateEquipmentConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
</script>