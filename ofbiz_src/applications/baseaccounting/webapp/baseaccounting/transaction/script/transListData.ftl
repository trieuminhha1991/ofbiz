<script>
	var isPostedData = [
	    {isPosted : 'Y',description : '${StringUtil.wrapString(uiLabelMap.BACCPostted)}'},
	    {isPosted : 'N',description : '${StringUtil.wrapString(uiLabelMap.BACCNotPostted)}'}
	];
	
	<#assign glAccountClasses = delegator.findList("GlAccountClass", null, null, null, null, true)/>
	var glAccountClasses = [
		<#list glAccountClasses as type>
			{
				<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
				glAccountClassId : "${type.glAccountClassId}",
				description: "${description}"	
			},
		</#list>
	];
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, true) />
	var statusItemsData =  [
	    <#list statusItems as item>
	    	{
	    		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
	    		'statusId' : "${item.statusId?if_exists}",
	    		'description' : "${description?if_exists}"
			},
		</#list>
	];
	
	<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", null, null, null, null, true) />
	var glAccountOACsData =  [
	      <#list glAccountOACs as item>
	      	{
	      		<#assign description = StringUtil.wrapString(item.get("accountName", locale)?if_exists + " [" + item.glAccountId?if_exists +"]")>
	      		'glAccountId' : "${item.glAccountId?if_exists}",
	      		'description' : "${description?if_exists}"
			},
		  </#list>
	];
	
	<#assign glAccountTypes = delegator.findList("GlAccountType", null, null, null, null, true)/>
	var glAccountTypes = [
		<#list glAccountTypes as type>
			{
				<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
				glAccountTypeId : "${type.glAccountTypeId}",
				description: "${description}"	
			},
		</#list>
	];
	
	//Prepare Data
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, Static["org.ofbiz.base.util.UtilMisc"].toList("description"), null, true) />
	var acctgTransTypesData =  [
        <#list acctgTransTypes as item>
        	{
        		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
        		'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",
        		'description' : "${description}"
			},
		</#list>
	];
	
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, true) />
	var glFiscalTypesData =  [
	      <#list glFiscalTypes as item>
	      	{
	      		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
	      		'glFiscalTypeId' : "${item.glFiscalTypeId?if_exists}",
	      		'description' : "${description}"	
	      	},
	  	  </#list>
  	];
	
	<#assign glJournalList = delegator.findByAnd("GlJournal", null, Static["org.ofbiz.base.util.UtilMisc"].toList("glJournalName"), true) />
	var glJournalArr =  [
	      <#list glJournalList as glJournal>
	      	{
	      		<#assign glJournalName = StringUtil.wrapString(glJournal.get("glJournalName", locale)?if_exists)>
	      		'glJournalId' : "${glJournal.glJournalId}",
	      		'glJournalName' : "${glJournalName}"	
	      	},
	  	  </#list>
  	];
	
	var dataItt  = [
	  	<#assign listType = delegator.findByAnd("InvoiceItemType",null,null,false) !>
	  	<#if listType?exists>
	  		<#list listType as type>
	  			{
	 				'invoiceItemTypeId' : '${type.invoiceItemTypeId?if_exists}',
	 				'description' :"${StringUtil.wrapString(type.get("description",locale)?default(""))}" 			
	  			},
	  		</#list>
	  	</#if>
  	];
	
	var globalVar = {};
	var uiLabelMap = {};
	globalVar.invoiceTypeArr = [
		<#if invoiceTypeList?exists>
			<#list invoiceTypeList as invoiceType>
			{
				invoiceTypeId: "${invoiceType.invoiceTypeId}",
				description: '${StringUtil.wrapString(invoiceType.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	globalVar.invoiceStatusTypeArr = [
		<#if invoiceStatusList?exists>
			<#list invoiceStatusList as status>
			{
				statusId: "${status.statusId}",
				description: '${StringUtil.wrapString(status.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	globalVar.paymentTypeArr = [
		<#if paymentTypeList?exists>
			<#list paymentTypeList as payment>
			{
				paymentTypeId: "${payment.paymentTypeId}",
				description: '${StringUtil.wrapString(payment.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	globalVar.paymentStatusTypeArr = [
		<#if paymentStatusList?exists>
			<#list paymentStatusList as status>
			{
				statusId: "${status.statusId}",
				description: '${StringUtil.wrapString(status.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	globalVar.shipmentTypeArr = [
		<#if shipmentTypeList?exists>
			<#list shipmentTypeList as shipmentType>
			{
				shipmentTypeId: "${shipmentType.shipmentTypeId}",
				description: '${StringUtil.wrapString(shipmentType.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	globalVar.enumPartyTypeArr = [
		<#if enumPartyTypeList?exists>
			<#list enumPartyTypeList as enumParty>
			{
				enumId: "${enumParty.enumId}",
				description: '${StringUtil.wrapString(enumParty.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	globalVar.glAccountTypeArr = [
		<#if glAccountTypeList?exists>
			<#list glAccountTypeList as glAccountType>
			{
				glAccountTypeId: "${glAccountType.glAccountTypeId}",
				description: '${StringUtil.wrapString(glAccountType.get("description", locale))}'
			},
			</#list>
		</#if>
	];
	
	uiLabelMap.BACCInvoiceId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}";
	uiLabelMap.BACCInvoiceTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}";
	uiLabelMap.BACCInvoiceFromParty = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceFromParty)}";
	uiLabelMap.BACCInvoiceToParty = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceToParty)}";
	uiLabelMap.BACCInvoiceDate = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceDate)}";
	uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
	uiLabelMap.BACCPaymentId = "${StringUtil.wrapString(uiLabelMap.BACCPaymentId)}";
	uiLabelMap.BACCPaymentFromParty = "${StringUtil.wrapString(uiLabelMap.BACCPaymentFromParty)}";
	uiLabelMap.BACCPaymentToParty = "${StringUtil.wrapString(uiLabelMap.BACCPaymentToParty)}";
	uiLabelMap.BACCPaymentTypeId = "${StringUtil.wrapString(uiLabelMap.BACCPaymentTypeId)}";
	uiLabelMap.BACCStatusId = "${StringUtil.wrapString(uiLabelMap.BACCStatusId)}";
	uiLabelMap.BACCEffectiveDate = "${StringUtil.wrapString(uiLabelMap.BACCEffectiveDate)}";
	uiLabelMap.ShipmentId = "${StringUtil.wrapString(uiLabelMap.ShipmentId)}";
	uiLabelMap.ShipmentType = "${StringUtil.wrapString(uiLabelMap.ShipmentType)}";
	uiLabelMap.OrderOrderId = "${StringUtil.wrapString(uiLabelMap.OrderOrderId)}";
	uiLabelMap.BSReturnOrder = "${StringUtil.wrapString(uiLabelMap.BSReturnOrder)}";
	uiLabelMap.EstimatedShipDate = "${StringUtil.wrapString(uiLabelMap.EstimatedShipDate)}";
	uiLabelMap.EstimatedArrivalDate = "${StringUtil.wrapString(uiLabelMap.EstimatedArrivalDate)}";
	uiLabelMap.BACCOrganizationId = "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}";
	uiLabelMap.BACCFullName = "${StringUtil.wrapString(uiLabelMap.BACCFullName)}";
	uiLabelMap.AccountingComments = "${StringUtil.wrapString(uiLabelMap.AccountingComments)}";
	uiLabelMap.BACCDebitAccount = "${StringUtil.wrapString(uiLabelMap.BACCDebitAccount)}";
	uiLabelMap.BACCCreditAccount = "${StringUtil.wrapString(uiLabelMap.BACCCreditAccount)}";
	uiLabelMap.BACCAmount = "${StringUtil.wrapString(uiLabelMap.BACCAmount)}";
	uiLabelMap.DAParty = "${StringUtil.wrapString(uiLabelMap.DAParty)}";
	uiLabelMap.BACCProduct = "${StringUtil.wrapString(uiLabelMap.BACCProduct)}";
	uiLabelMap.GlReconciliationId = "${StringUtil.wrapString(uiLabelMap.GlReconciliationId)}";
	uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
	uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
	uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
	uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
	uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
	uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
	uiLabelMap.BACCTransactionDetail = "${StringUtil.wrapString(uiLabelMap.BACCTransactionDetail)}";
	uiLabelMap.BACCProductId = "${StringUtil.wrapString(uiLabelMap.BACCProductId)}";
	uiLabelMap.BACCProductName = "${StringUtil.wrapString(uiLabelMap.BACCProductName)}";
	uiLabelMap.BACCAccountCode = "${StringUtil.wrapString(uiLabelMap.BACCAccountCode)}";
	uiLabelMap.BACCAccountName = "${StringUtil.wrapString(uiLabelMap.BACCAccountName)}";
	uiLabelMap.BACCGlAccountTypeId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountTypeId)}";
	uiLabelMap.MustEnterFiveNumber = "${StringUtil.wrapString(uiLabelMap.MustEnterFiveNumber)}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
	uiLabelMap.CreateAcctgTransConfirm = "${StringUtil.wrapString(uiLabelMap.CreateAcctgTransConfirm)}";
	uiLabelMap.AcctgTransEntriesIsEmpty = "${StringUtil.wrapString(uiLabelMap.AcctgTransEntriesIsEmpty)}";
	uiLabelMap.CreditAndDebitIsNotEquals = "${StringUtil.wrapString(uiLabelMap.CreditAndDebitIsNotEquals)}";
	uiLabelMap.IsNotValid = "${StringUtil.wrapString(uiLabelMap.IsNotValid)}";
	uiLabelMap.GlReconciliationId = "${StringUtil.wrapString(uiLabelMap.GlReconciliationId)}";
	uiLabelMap.BACCModifiedDate = "${StringUtil.wrapString(uiLabelMap.BACCModifiedDate)}";
	uiLabelMap.BACCUserModified = "${StringUtil.wrapString(uiLabelMap.BACCUserModified)}";
	uiLabelMap.BACCContentChange = "${StringUtil.wrapString(uiLabelMap.BACCContentChange)}";
	uiLabelMap.BACCAcctgTransEntrySeqId = "${StringUtil.wrapString(uiLabelMap.BACCAcctgTransEntrySeqId)}";
	uiLabelMap.BACCOldValue = "${StringUtil.wrapString(uiLabelMap.BACCOldValue)}";
	uiLabelMap.BACCNewValue = "${StringUtil.wrapString(uiLabelMap.BACCNewValue)}";
	uiLabelMap.BACCGlAccountId = "${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}";
	uiLabelMap.BACCAreYouSureCancelThisAcctgTrans = "${StringUtil.wrapString(uiLabelMap.BACCAreYouSureCancelThisAcctgTrans)}";
	uiLabelMap.BACCAreYouSurePostedThisAcctgTrans = "${StringUtil.wrapString(uiLabelMap.BACCAreYouSurePostedThisAcctgTrans)}";
</script>
