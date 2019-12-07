<#--===================================Prepare Data=====================================================-->
<#if businessType == 'AR'>
	<#assign listPaymentTypes = delegator.findByAnd("PaymentType", {"parentTypeId" : "RECEIPT"}, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentTypeId DESC"), false)>
	<#assign listPaymentMethodTypes = delegator.findByAnd("PaymentMethodType", null, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentMethodTypeId DESC"), false)>
	<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "PMNT_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
	<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
<#else>
	<#assign listPaymentTypes = delegator.findByAnd("PaymentType", {"parentTypeId" : "DISBURSEMENT"}, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentTypeId DESC"), false)>
	<#assign listPaymentMethodTypes = delegator.findByAnd("PaymentMethodType", null, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentMethodTypeId DESC"), false)>
	<#assign listStatusItems = delegator.findByAnd("StatusItem", {"statusTypeId" : "PMNT_STATUS"}, Static["org.ofbiz.base.util.UtilMisc"].toList("statusTypeId DESC"), false)>
	<#assign listUoms = delegator.findByAnd("Uom", {"uomTypeId" : "CURRENCY_MEASURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("uomId DESC"), false)>
</#if>

<#assign listPaymentMethods = delegator.findByAnd("PaymentMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId","${userLogin.lastOrg}") , Static["org.ofbiz.base.util.UtilMisc"].toList("paymentMethodId DESC"), false)>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	if(typeof (globalVar) == "undefined"){
		var globalVar = {};
	}
	if(typeof (uiLabelMap) == "undefined"){
		var uiLabelMap = {};
	}
	<#assign organizationParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", '${userLogin.lastOrg}'), false)>
	<#assign party_preference = delegator.findByAnd("PartyAcctgPreference", {"partyId" : "${userLogin.lastOrg}"},null,false) !>
	<#if party_preference?exists && party_preference?size != 0>
		globalVar.preferenceCurrencyUom = '${party_preference.get(0).baseCurrencyUomId?if_exists}';
	<#else>
		globalVar.preferenceCurrencyUom = 'VND';
	</#if>
	
	globalVar.groupName = '${StringUtil.wrapString(organizationParty.get("groupName", locale))}';
	globalVar.userLogin_lastOrg = '${userLogin.lastOrg}';
	
	globalVar.businessType = "${businessType}";
	globalVar.paymentId = "${parameters.paymentId?if_exists}";
	uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.BACCOK = "${StringUtil.wrapString(uiLabelMap.BACCOK)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
	uiLabelMap.accConfirms = "${StringUtil.wrapString(uiLabelMap.BACCConfirms)}";
	uiLabelMap.accThisPayment = "${StringUtil.wrapString(uiLabelMap.BACCThisPayment)}";
	uiLabelMap.BACCInvoiceFromParty = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceFromParty)}";
	uiLabelMap.BACCInvoiceToParty = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceToParty)}";
	uiLabelMap.BACCInvoiceId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}";
	uiLabelMap.BACCInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceDate)}";
	uiLabelMap.BACCAmountApplied = "${StringUtil.wrapString(uiLabelMap.BACCAmountApplied)}";
	uiLabelMap.BACCInvoiceTotal = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTotal)}";
	uiLabelMap.CreatePaymentApplicationConfirm = "${StringUtil.wrapString(uiLabelMap.CreatePaymentApplicationConfirm)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
	uiLabelMap.BACCFullName = "${StringUtil.wrapString(uiLabelMap.BACCFullName)}";
	uiLabelMap.BACCProductId = "${StringUtil.wrapString(uiLabelMap.BACCProductId)}";
	uiLabelMap.BACCProductName = "${StringUtil.wrapString(uiLabelMap.BACCProductName)}";
	uiLabelMap.CreatePaymentApplicationConfirm = "${StringUtil.wrapString(uiLabelMap.CreatePaymentApplicationConfirm)}";
	uiLabelMap.BACCCancelPaymentConfirm = "${StringUtil.wrapString(uiLabelMap.BACCCancelPaymentConfirm)}";
    uiLabelMap.BACCVoidPaymentConfirm = "${StringUtil.wrapString(uiLabelMap.BACCVoidPaymentConfirm)}";
    uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
	//Prepare Data
	var dataPaymentType = [
	   <#if listPaymentTypes?exists>
	       	<#list listPaymentTypes as type>
	       		{
	       			paymentTypeId : "${type.paymentTypeId}",
	       			description : "${StringUtil.wrapString(type.get('description',locale))}"
				},
	   		</#list>
		</#if>
	];
	<#if businessType == "AP">
		<#assign listPaymentTypes = delegator.findByAnd("PaymentType", {"parentTypeId" : "TAX_PAYMENT"}, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentTypeId DESC"), false)>
		 <#if listPaymentTypes?exists>
	    	<#list listPaymentTypes as type>
	    	dataPaymentType.push({
	    			paymentTypeId : "${type.paymentTypeId}",
	    			description : "${StringUtil.wrapString(type.get('description',locale))}"
				})
			</#list>
		</#if>
	</#if>
	
	var dataPaymentMethodType = [
	   <#if listPaymentMethodTypes?exists>
	       	<#list listPaymentMethodTypes as type>
	       		{
	       			paymentMethodTypeId : "${type.paymentMethodTypeId}",
	       			description : "${StringUtil.wrapString(type.get('description',locale))}"
				},
	   		</#list>
		</#if>
	];
	
	var paymentMethodData = [
	   <#if listPaymentMethods?exists>
	       	<#list listPaymentMethods as method>
	       		{
	       			paymentMethodId : "${method.paymentMethodId}",
	       			description : "${StringUtil.wrapString(method.get('description',locale))}"
				},
	   		</#list>
		</#if>
	];
	
	var dataStatusType = [
      <#if listStatusItems?exists>
      	<#list listStatusItems as type>
      		{
      			statusId : "${type.statusId}",
      			description : "${StringUtil.wrapString(type.get('description', locale))}",
  			},
  		</#list>
  	  </#if>
	];
	var invStatusArr = [
      <#if invNewStatusList?exists>
      	<#list invNewStatusList as status>
      		{
      			statusId : "${status.statusId}",
      			description : "${StringUtil.wrapString(status.get('description'))}",
  			},
  		</#list>
  	  </#if>
	];
	
	var uomData = [
      <#if listUoms?exists>
      	<#list listUoms as uom>
      		<#if uom?exists && uom.uomId == "USD" || uom.uomId == "EUR" || uom.uomId == "VND">
      		{
      			uomId : "${uom.uomId}",
      			description : "${StringUtil.wrapString(uom.get('description'))}",
      			abbreviation : "${StringUtil.wrapString(uom.get('abbreviation'))}",
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
	
</script>
<#--===================================/Prepare Data=====================================================-->