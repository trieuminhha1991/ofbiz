<@jqGridMinimumLib />
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var uiLabelMap = {};
var globalVar = {};
<#if !rootOrgList?exists>
	<#assign rootOrgList = [Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)]/>
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

globalVar.fixedAssetAssignRoleArr = [
	<#if fixedAssetAssignRoleList?exists>
		<#list fixedAssetAssignRoleList as roleType>
		{
			roleTypeId: '${StringUtil.wrapString(roleType.roleTypeId)}',
			description: '${StringUtil.wrapString(roleType.get("description", locale))}'
		},
		</#list>
	</#if>
];
globalVar.fixedAssetAssignStatusArr = [
	<#if fixedAssetAssignStatusList?exists>
		<#list fixedAssetAssignStatusList as status>
		{
			statusId: '${StringUtil.wrapString(status.statusId)}',
			description: '${StringUtil.wrapString(status.get("description", locale))}'
		},
		</#list>
	</#if>
];

<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "FIXEDASSET_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
globalVar.statusData = [
  <#if listStatusItems?exists>
  	<#list listStatusItems as item>
  		{
  			statusId : "${item.statusId}",
  			description : "${StringUtil.wrapString(item.get('description'))}",
			},
		</#list>
	  </#if>
];

<#assign listFixedAssetTypes = delegator.findList("FixedAssetType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].NOT_EQUAL, null), null, null, null, true) />
globalVar.fixedAssetTypeData = [
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
globalVar.uomData = [
	<#if listUoms?exists>
		<#list listUoms as uom>
			<#if uom.uomId == 'VND' || uom.uomId == 'USD' || uom.uomId == 'EUR'>
			{
  				uomId : "${uom.uomId}",
  				description : "${StringUtil.wrapString(uom.get('description'))}",
  				abbreviation : "${StringUtil.wrapString(uom.get('abbreviation'))}",
			},
			</#if>	
		</#list>
	</#if>
];

globalVar.periodData = [
    {periodId : "MONTH", description: "${uiLabelMap.BACCMonth}"},
    {periodId : "YEAR", description: "${uiLabelMap.BACCYear}"},
];

globalVar.orgUseTypeArr = [
		{type: 'productStore', description: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}'},                           
		{type: 'internalOrganization', description: '${StringUtil.wrapString(uiLabelMap.BACCInternalOrganization)}'},                           
];

globalVar.fixedAssetId = "${parameters.fixedAssetId}";
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.validStartDateMustLessThanOrEqualFinishDate = "${StringUtil.wrapString(uiLabelMap.validStartDateMustLessThanOrEqualFinishDate)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.BSMonthLowercase = "${StringUtil.wrapString(uiLabelMap.BSMonthLowercase)}";
uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
uiLabelMap.BACCAllocGlAccoutId = "${StringUtil.wrapString(uiLabelMap.BACCAllocGlAccoutId)}";
uiLabelMap.BACCMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.BACCMustNotByContainSpecialCharacter)}";
uiLabelMap.BACCSpecialCharacterCheck = "${StringUtil.wrapString(uiLabelMap.BACCSpecialCharacterCheck)}";
</script>