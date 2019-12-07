<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script>
	var globalVar = {};
	var uiLabelMap = {};
	
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
	
	<#assign currentOrgId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	
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
    <#assign listFixedAssetTypeGlAccs = delegator.findList("FixedAssetTypeGlAccount",
    Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, [currentOrgId, 'company']), null, null, null, false) />

	globalVar.assetTypeGlAccData = [
		<#if listFixedAssetTypeGlAccs?exists>
			<#list listFixedAssetTypeGlAccs as item>
			{
				assetGlAccountId : "${item.assetGlAccountId}",
				depGlAccountId : "${item.depGlAccountId}",
				accDepGlAccountId : "${item.accDepGlAccountId}",
				fixedAssetTypeId : "${item.fixedAssetTypeId}",
				fixedAssetId : "${item.fixedAssetId}",
			},
			</#list>
		</#if>
	];

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
	
	globalVar.locale = '${locale}';
	globalVar.orgUseTypeArr = [
    		<#--{type: 'productStore', description: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}'},                           -->
    		{type: 'internalOrganization', description: '${StringUtil.wrapString(uiLabelMap.BACCInternalOrganization)}'}, 
    		<#--{type: 'partner', description: '${StringUtil.wrapString(uiLabelMap.BACCFranchising)}' }                          -->
    ];

	uiLabelMap.CreateFixedAssetConfirm = "${StringUtil.wrapString(uiLabelMap.CreateFixedAssetConfirm)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
	uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
	uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
	uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
	uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
	uiLabelMap.FixedAssetAccompanyName = "${StringUtil.wrapString(uiLabelMap.FixedAssetAccompanyName)}";
	uiLabelMap.BSCalculateUomId = "${StringUtil.wrapString(uiLabelMap.BSCalculateUomId)}";
	uiLabelMap.BACCQuantity = "${StringUtil.wrapString(uiLabelMap.BACCQuantity)}";
	uiLabelMap.BSValue = "${StringUtil.wrapString(uiLabelMap.BSValue)}";
	uiLabelMap.FixedAssetAccompanyList = "${StringUtil.wrapString(uiLabelMap.FixedAssetAccompanyList)}";
	uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
	uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
	uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
	uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
	uiLabelMap.BACCDateFixedAssetFieldRequired = "${StringUtil.wrapString(uiLabelMap.BACCDateFixedAssetFieldRequired)}";
	uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
	uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
	uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
	uiLabelMap.BACCAllocGlAccoutId = "${StringUtil.wrapString(uiLabelMap.BACCAllocGlAccoutId)}";
    uiLabelMap.BACCMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.BACCMustNotByContainSpecialCharacter)}";
    uiLabelMap.BACCSpecialCharacterCheck = "${StringUtil.wrapString(uiLabelMap.BACCSpecialCharacterCheck)}";

</script>