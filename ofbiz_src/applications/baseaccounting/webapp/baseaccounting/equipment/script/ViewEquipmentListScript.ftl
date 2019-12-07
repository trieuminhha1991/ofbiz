<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
<#if !rootOrgList?exists>
	<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
</#if>
var globalVar = {};
var uiLabelMap = {};

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

globalVar.currencyUomArr = [
	<#if listCurrencyUom?has_content>
		<#list listCurrencyUom as uom>
			<#if uom.uomId == "EUR" || uom.uomId == "VND" || uom.uomId == "USD">
			{
				uomId: "${uom.uomId}",
				abbreviation: '${StringUtil.wrapString(uom.abbreviation?if_exists)}',
				description: '${StringUtil.wrapString(uom.description?if_exists)}'
			},
			</#if>
		</#list>
	</#if>
];

globalVar.equipmentTypeArr = [
	<#if listEquipmentType?has_content>
		<#list listEquipmentType as equipmentType>
		{
			equipmentTypeId: '${equipmentType.equipmentTypeId}',
			description: '${StringUtil.wrapString(equipmentType.description)}'
		},
		</#list>
	</#if>
];
<#if defaultCurrencyUomId?exists>
globalVar.defaultCurrencyUomId = "${defaultCurrencyUomId}";
</#if>
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

<#assign listOrgPartner = Static["com.olbius.basehr.util.SecurityUtil"].getPartiesByRoles('PARTNER', delegator, false) />
globalVar.listOrgPartner =[
	<#if listOrgPartner?has_content>
		<#list listOrgPartner as partnerId>
			<#assign partner = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partnerId), false)/>
			{
				partyId : "${partnerId}",
				partyName : "${StringUtil.wrapString(partner.groupName)}"
			},
		</#list>
	</#if>
];
	
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CreateEquipmentConfirm = "${StringUtil.wrapString(uiLabelMap.CreateEquipmentConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
uiLabelMap.BACCCreateNew = "${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}";
uiLabelMap.BACCCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreateNewConfirm)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
uiLabelMap.BACCOrganization = "${StringUtil.wrapString(uiLabelMap.BACCOrganization)}";
uiLabelMap.EquipmentPartyIsEmpty = "${StringUtil.wrapString(uiLabelMap.EquipmentPartyIsEmpty)}";
uiLabelMap.EquipmentQtyMustBeEqualOrgUsedQty = "${StringUtil.wrapString(uiLabelMap.EquipmentQtyMustBeEqualOrgUsedQty)}";
uiLabelMap.CommonList = "${StringUtil.wrapString(uiLabelMap.CommonList)}";
uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
uiLabelMap.BACCFranchising = "${StringUtil.wrapString(uiLabelMap.BACCFranchising)}";
uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
uiLabelMap.BACCFromDate = "${StringUtil.wrapString(uiLabelMap.BACCFromDate)}";
uiLabelMap.BACCThruDate = "${StringUtil.wrapString(uiLabelMap.BACCThruDate)}";
uiLabelMap.BACCFromDateValidate = "${StringUtil.wrapString(uiLabelMap.BACCFromDateValidate)}";
uiLabelMap.BACCThruDateValidate = "${StringUtil.wrapString(uiLabelMap.BACCThruDateValidate)}";
</script>
<style>
	.not-active {
   		pointer-events: none;
   		cursor: default;
   		color: #848484;
	}
</style>