<#assign gridProductItemsId2 = "jqxgridOrderItem2">
<#assign dataField2 = "[
				{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'exportedQuantity', type: 'string'},
				{ name: 'tripId', type: 'string'},
				{ name: 'totalWeight', type: 'string'},
			]"/>
<#assign columnlist2 = "
				{text: '${StringUtil.wrapString(uiLabelMap.BDProductId)}', dataField: 'productId', width: '16%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BDProductName)}', dataField: 'productName', width: '14%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BDExportedQuantity)}', dataField: 'exportedQuantity', width: '12%', cellsalign: 'right', cellsformat: 'd'},
				{text: '${StringUtil.wrapString(uiLabelMap.BDTripId)}', dataField: 'tripId', width: '12%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BDTotalWeight)} (kg)', dataField: 'totalWeight', cellsformat: 'd'},
			"/>
<@jqGrid id=gridProductItemsId2 idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist2 dataField=dataField2
viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="singleRow" groupable="false"
url="jqxGeneralServicer?sname=JQGetListItemInTrip&tripId=${trip.tripId}"
/>