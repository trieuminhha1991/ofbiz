<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'paymentGatewayConfigTypeId', type: 'string'},
					 { name: 'description', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.AccountingPaymentGatewayConfigTypeId}', datafield: 'paymentGatewayConfigTypeId', width: 350, editable: false},
                     { text: '${uiLabelMap.AccountingPaymentGatewayConfigDescription}', datafield: 'description'}
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListPaymentGatewayConfigTypes" dataField=dataField columnlist=columnlist
		 />