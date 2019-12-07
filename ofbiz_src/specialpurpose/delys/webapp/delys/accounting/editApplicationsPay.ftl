    
<#assign dataFieldap="[
			{name : 'paymentApplicationId',type: 'string'},
			{name : 'paymentId',type: 'string'},
			{name : 'toPaymentId',type: 'string'},
			{name : 'amountApplied', type: 'number'}
			]
"/>    

<#assign columnlistap="
		{text : '${uiLabelMap.FormFieldTitle_paymentApplicationId}',datafield : 'paymentApplicationId',hidden:true},
		{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',hidden:true},
		{text : '${uiLabelMap.FormFieldTitle_toPaymentId}',datafield : 'toPaymentId'},
		{text : '${uiLabelMap.FormFieldTitle_amountApplied}',datafield : 'amountApplied',filtertype : 'number'}
"/>
<@jqGrid id="editPaymentPays" dataField=dataFieldap columnlist=columnlistap url="jqxGeneralServicer?sname=JQGetListApplicationsEdit&paymentId=${parameters.paymentId?if_exists}"
	clearfilteringbutton="true"  deleterow="true" removeUrl="jqxGeneralServicer?jqaction=D&sname=removePaymentApplication"
	deleteColumn="paymentApplicationId;paymentId;toPaymentId;amountApplied"
	/>