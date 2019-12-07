<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	<#assign payGrTypes = delegator.findList("PaymentGroupType", null, null, null, null, false) />
	var payGrTypeData = [
		<#list payGrTypes as item>
			{
				<#assign description = StringUtil.wrapString(item.get("description", locale))/>
				'paymentGroupTypeId' : '${item.paymentGroupTypeId}',
				'description' : '${description}'
			},
		</#list>
	];
</script>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_paymentGroupId}', dataField: 'paymentGroupId', editable: false, width: 150,
					 	cellsrenderer: function(row, column, value){
					 		return '<span><a href=accApPaymentGroupOverview?paymentGroupId=' + value +'>' + value + '</a></span>'
					 	}
					},
                     { text: '${uiLabelMap.FormFieldTitle_paymentGroupTypeId}', dataField: 'paymentGroupTypeId', editable: false, 
                     	cellsrenderer: function(row, column, value){
                     		for(i = 0; i < payGrTypeData.length; i++){
                     			if(payGrTypeData[i].paymentGroupTypeId == value){
                     				return '<span title='+ value + '>' + payGrTypeData[i].description +'</span>'
                     			}
                     		}
                     	}
                     },
                     { text: '${uiLabelMap.AccountingPaymentGroupName}', dataField: 'paymentGroupName', editable: true}
                     "/>
<#assign dataField="[{ name: 'paymentGroupId', type: 'string' },
                 	{ name: 'paymentGroupTypeId', type: 'string' },
                 	{ name: 'paymentGroupName', type: 'string' }]"/>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQApListPaymentGroup" createUrl="jqxGeneralServicer?jqaction=C&sname=createPaymentGroup"
		 addColumns="paymentGroupTypeId;paymentGroupName" updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePaymentGroup" editColumns="paymentGroupId;paymentGroupName"
		/>

<#include "popup/popupAddPaymentGroup.ftl"/>