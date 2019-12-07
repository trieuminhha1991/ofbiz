<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for PaymentGatewayConfigType data
	<#assign listPayGatewayConfigType = delegator.findList("PaymentGatewayConfigType", null, null, null, null, false)>
	var configTypeData = [
		<#list listPayGatewayConfigType as item>
			{
				<#assign description = StringUtil.wrapString(item.description) />
				paymentGatewayConfigTypeId : '${item.paymentGatewayConfigTypeId}',
				description : '${description}',
			},
		</#list>
	]
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'paymentGatewayConfigTypeId', type: 'string'},
					 { name: 'description', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.AccountingPaymentGatewayConfigTypeId}', datafield: 'paymentGatewayConfigTypeId', width: 350, editable: false,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < configTypeData.length; i++){
								if(configTypeData[i].paymentGatewayConfigTypeId == value){
									return '<span>' + configTypeData[i].description + '</span>';
								}
							}
							
							return '<span>' + value + '</span>';
						}
					 },
                     { text: '${uiLabelMap.AccountingPaymentGatewayConfigDescription}', datafield: 'description'}
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListPaymentGatewayConfigs" dataField=dataField columnlist=columnlist
		 />
<#--=================================/Init Grid======================================================-->
	