<#assign dataField = "[
			{name: 'loyaltyPointId', type: 'string'}, 
			{name: 'loyaltyPointDetailSeqId', type: 'string'}, 
			{name: 'effectiveDate', type: 'date', other: 'Timestamp'}, 
			{name: 'pointDiff', type: 'number'}, 
			{name: 'orderId', type: 'string'}, 
			{name: 'returnId', type: 'string'}, 
		]"/>
		
<#assign columnlist = "
			{text: '${uiLabelMap.BSSeqId}', dataField: 'loyaltyPointDetailSeqId', width: 200}, 
          	{text: '${uiLabelMap.BSPoint}', dataField: 'pointDiff', filtertype: 'number'}, 
			{text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', 
				cellsrenderer: function(row, colum, value) {
                	return \"<span><a href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
                }
			}, 
			{text: '${uiLabelMap.BSReturnOrder}', dataField: 'returnId'}, 
			{text: '${uiLabelMap.BsEffectiveDate}', dataField: 'effectiveDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', width: 250}, 
		"/>
		
<@jqGrid id="jqxLoyaltyCustomer" url="jqxGeneralServicer?sname=JQListLoyaltyCustomerDetail&loyaltyPointId=${parameters.loyaltyPointId?if_exists}" columnlist=columnlist dataField=dataField
		editable="false" viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" clearfilteringbutton = "true"/>		