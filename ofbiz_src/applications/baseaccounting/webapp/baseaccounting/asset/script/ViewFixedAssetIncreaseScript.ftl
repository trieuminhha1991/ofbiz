<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
var globalVar = {};
var uiLabelMap = {};
globalVar.paymentMethodTypeEnumArr = [
	<#if paymentMethodTypeEnumList?has_content>
		<#list paymentMethodTypeEnumList as paymentMethodTypeEnum>
		{
			enumId: '${paymentMethodTypeEnum.enumId}',
			description: '${StringUtil.wrapString(paymentMethodTypeEnum.description)}'
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

<#assign orgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>
<#assign partyAcctgPreference = delegator.findOne("PartyAcctgPreference", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", orgId), false)!/>

<#if partyAcctgPreference?exists>
	globalVar.currencyUomId = "${partyAcctgPreference.baseCurrencyUomId}";
</#if>

<#assign finAccountList = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(finAccountListActive, Static["org.ofbiz.base.util.UtilMisc"].toMap("ownerPartyId", orgId))/>
globalVar.finAccountArr = [
	<#list finAccountList as finAccount>
		{
			finAccountId: '${finAccount.finAccountId}',
			finAccountName: '${finAccount.finAccountName?if_exists}',
			finAccountCode: '${finAccount.finAccountCode?if_exists}',
		},
	</#list>
];
globalVar.orgId = "${orgId}";
uiLabelMap.SequenceId = "${StringUtil.wrapString(uiLabelMap.SequenceId)}";
uiLabelMap.POSupplierId = "${StringUtil.wrapString(uiLabelMap.POSupplierId)}";
uiLabelMap.POSupplierName = "${StringUtil.wrapString(uiLabelMap.POSupplierName)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.BACCFixedAssetIdShort = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}";
uiLabelMap.BACCFixedAssetName = "${StringUtil.wrapString(uiLabelMap.BACCFixedAssetName)}";
uiLabelMap.DebitAccount = "${StringUtil.wrapString(uiLabelMap.DebitAccount)}";
uiLabelMap.CreditAccount = "${StringUtil.wrapString(uiLabelMap.CreditAccount)}";
uiLabelMap.BACCPurchaseCost = "${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}";
uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.FixedAssetPurchased = "${StringUtil.wrapString(uiLabelMap.FixedAssetPurchased)}";
uiLabelMap.OrganizationUsed = "${StringUtil.wrapString(uiLabelMap.OrganizationUsed)}";
uiLabelMap.BACCPurCostAcc = "${StringUtil.wrapString(uiLabelMap.BACCPurCostAcc)}";
uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.FixedAssetIsNotSelected = "${StringUtil.wrapString(uiLabelMap.FixedAssetIsNotSelected)}";
uiLabelMap.BACCCreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCreateNewConfirm)}";
uiLabelMap.BACCPostedFixAssetConfirm ="${StringUtil.wrapString(uiLabelMap.BACCPostedFixAssetConfirm)}";
</script>