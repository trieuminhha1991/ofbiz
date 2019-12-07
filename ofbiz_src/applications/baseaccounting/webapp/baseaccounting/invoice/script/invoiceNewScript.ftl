<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxloader.js"></script>
<script type="text/javascript" src="/accresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
var globalVar = {};
globalVar.businessType = '${businessType}';
<#assign organizationParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", '${userLogin.lastOrg}'), false)>
globalVar.groupName = '${StringUtil.wrapString(organizationParty.get("groupName", locale))}';
globalVar.userLogin_lastOrg = '${userLogin.lastOrg}';
globalVar.defaultCurrencyUomId = '${defaultCurrencyUomId?if_exists}';
var uiLabelMap = {};
uiLabelMap.BACCCustomerId = '${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}';
uiLabelMap.BACCFullName = '${StringUtil.wrapString(uiLabelMap.BACCFullName)}';
uiLabelMap.BACCOrganizationId = '${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}';
uiLabelMap.FieldRequired = '${StringUtil.wrapString(uiLabelMap.FieldRequired)}';
uiLabelMap.BACCInvoiceItemList = '${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemList)}';
uiLabelMap.BACCAddNewRow = '${StringUtil.wrapString(uiLabelMap.BACCAddNewRow)}';
uiLabelMap.BACCDelRow = '${StringUtil.wrapString(uiLabelMap.BACCDelRow)}';
uiLabelMap.BACCInvoiceItemSeqId = '${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemSeqId)}';
uiLabelMap.BACCInvoiceItemType = '${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemType)}';
uiLabelMap.BACCProduct = '${StringUtil.wrapString(uiLabelMap.BACCProduct)}';
uiLabelMap.BACCInvoiceItemType = '${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemType)}';
uiLabelMap.BACCQuantity = '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}';
uiLabelMap.BACCUnitPrice = '${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}';
uiLabelMap.BACCDescription = '${StringUtil.wrapString(uiLabelMap.BACCDescription)}';
uiLabelMap.filterchoosestring = '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}';
uiLabelMap.BACCProductId = '${StringUtil.wrapString(uiLabelMap.BACCProductId)}';
uiLabelMap.BACCProductName = '${StringUtil.wrapString(uiLabelMap.BACCProductName)}';
uiLabelMap.BACCNeedEnterInvItems = '${StringUtil.wrapString(uiLabelMap.BACCNeedEnterInvItems)}';
uiLabelMap.wgcancel = '${StringUtil.wrapString(uiLabelMap.wgcancel)}';
uiLabelMap.wgok = '${StringUtil.wrapString(uiLabelMap.wgok)}';
uiLabelMap.wgdelete = '${StringUtil.wrapString(uiLabelMap.wgdelete)}';
uiLabelMap.CannotDeleteRow = '${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}';
uiLabelMap.wgdeleteconfirm = '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}';
uiLabelMap.CommonAddNew = '${StringUtil.wrapString(uiLabelMap.CommonAddNew)}';
uiLabelMap.HaveChooseInvoiceTypeBeforeAddItem = '${StringUtil.wrapString(uiLabelMap.HaveChooseInvoiceTypeBeforeAddItem)}';
uiLabelMap.CommonClose = '${StringUtil.wrapString(uiLabelMap.CommonClose)}';
uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}';
uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)}';
uiLabelMap.BACCCreateInvoiceConfirm = '${StringUtil.wrapString(uiLabelMap.BACCCreateInvoiceConfirm)}';
uiLabelMap.InvoiceItemTypeOfInvoiceType = '${StringUtil.wrapString(uiLabelMap.InvoiceItemTypeOfInvoiceType)}';
uiLabelMap.CommonNotSet = '${StringUtil.wrapString(uiLabelMap.CommonNotSet)}';
uiLabelMap.PhoneNumberMustInOto9 = '${StringUtil.wrapString(uiLabelMap.PhoneNumberMustInOto9)}';
uiLabelMap.PhoneNumberMustBeContain10or11character = '${StringUtil.wrapString(uiLabelMap.PhoneNumberMustBeContain10or11character)}';
uiLabelMap.AdditionalInformation = '${StringUtil.wrapString(uiLabelMap.AdditionalInformation)}';
uiLabelMap.SellerName = '${StringUtil.wrapString(uiLabelMap.SellerName)}';
uiLabelMap.BuyerName = '${StringUtil.wrapString(uiLabelMap.BuyerName)}';
uiLabelMap.BACCTaxCode = '${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}';
uiLabelMap.BSAbbPhone = '${StringUtil.wrapString(uiLabelMap.BSAbbPhone)}';
uiLabelMap.CommonAddress1 = '${StringUtil.wrapString(uiLabelMap.CommonAddress1)}';
uiLabelMap.CommonId = '${StringUtil.wrapString(uiLabelMap.CommonId)}';
uiLabelMap.CommonDescription = '${StringUtil.wrapString(uiLabelMap.CommonDescription)}';
uiLabelMap.BACCGlAccountId = '${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}';
uiLabelMap.ValueMustBeGreateThanZero = '${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}';
uiLabelMap.BACCGlAccountTypeId = '${StringUtil.wrapString(uiLabelMap.BACCGlAccountTypeId)}';
var invoiceTypeData  = [
	<#if listInvoiceTypes?exists>
		<#list listInvoiceTypes as type>
			{
			'invoiceTypeId' : '${type.invoiceTypeId?if_exists}',
			'description' :'${StringUtil.wrapString(type.get("description",locale)?default(""))}' 			
			},
		</#list>
	</#if>
];		

var uomData = [
     <#assign listUoms = delegator.findByAnd("Uom", {'uomTypeId', 'CURRENCY_MEASURE'},null,false) !>
     <#if listUoms?exists>
     	<#list listUoms as uom>
     	<#if uom?exists && uom.uomId == "USD" || uom.uomId == "EUR" || uom.uomId == "VND">
     		{
     			uomId : "${uom.uomId}",
     			description : "${StringUtil.wrapString(uom.get('description'))}",
 			},
 		</#if>	
 		</#list>
 	  </#if>
];
globalVar.enumPartyTypeArr = [
	<#if enumPartyTypeList?exists>
		<#list enumPartyTypeList as enumPartyType>
		{
			enumId: "${enumPartyType.enumId}",
			description: '${StringUtil.wrapString(enumPartyType.description)}'
		},
		</#list>
	</#if>
];
globalVar.quantityUomList = [
	<#if uomList?has_content>
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.description)}" 
		},
		</#list>
	</#if>
];
globalVar.countryGeoArr = [
	<#if countryGeoList?has_content>
		<#list countryGeoList as geo>
		{
			geoId: '${geo.geoId}',
			geoName: "${StringUtil.wrapString(geo.geoName)}"
		},	
		</#list>
	</#if>
];
<#if defaultCountryGeoId?exists>
	globalVar.defaultCountryGeoId = "${defaultCountryGeoId}";
</#if>

<#assign glAccountTypeDefaultAP = delegator.findOne("GlAccountType", Static["org.ofbiz.base.util.UtilMisc"].toMap("glAccountTypeId", 'ACCOUNTS_PAYABLE'), false)>
globalVar.glAccountTypeNameDefaultAP = '${StringUtil.wrapString(glAccountTypeDefaultAP.get("description", locale))}';
globalVar.glAccountTypeIdDefaultAP = 'ACCOUNTS_PAYABLE';
<#assign glAccountTypeDefaultAR = delegator.findOne("GlAccountType", Static["org.ofbiz.base.util.UtilMisc"].toMap("glAccountTypeId", 'ACCOUNTS_RECEIVABLE'), false)>
globalVar.glAccountTypeNameDefaultAR = '${StringUtil.wrapString(glAccountTypeDefaultAR.get("description", locale))}';
globalVar.glAccountTypeIdDefaultAR = 'ACCOUNTS_RECEIVABLE';
</script>