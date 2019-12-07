    
<#assign dataField="[
			{name : 'paymentApplicationId',type: 'string'},
			{name : 'paymentId',type: 'string'},
			{name : 'taxAuthGeoId',type: 'string'},
			{name : 'amountApplied',type: 'number'}
			]
"/>    

<#assign columnlist="
		{text : '${uiLabelMap.FormFieldTitle_paymentApplicationId}',datafield : 'paymentApplicationId',hidden:true},
		{text : '${uiLabelMap.FormFieldTitle_paymentId}',datafield : 'paymentId',hidden:true},
		{text : '${uiLabelMap.FormFieldTitle_taxAuthGeoId}',datafield : 'taxAuthGeoId'},
		{text : '${uiLabelMap.FormFieldTitle_amountApplied}',datafield : 'amountApplied',cellsformat : 'd',filtertype: 'number'}
"/>

<@jqGrid dataField=dataField columnlist=columnlist id="editPaymentApplicationsTax" url="jqxGeneralServicer?sname=JQGetListApplicationsTax&paymentId=${parameters.paymentId?if_exists}"
	clearfilteringbutton="true"  deleterow="true" removeUrl="jqxGeneralServicer?sname=removePaymentApplication&jqaction=D"
	deleteColumn="paymentApplicationId;paymentId;taxAuthGeoId;amountApplied"	
	/>