<#assign gridProductItemsId2 = "jqxgridOrderItem2">
<#assign dataField2 = "[
				{ name: 'productId', type: 'string'},
				{ name: 'productCode', type: 'string'},
				{ name: 'quantity', type: 'string'},
				{ name: 'primaryProductCategoryId', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'totalWeight', type: 'string'},
			]"/>
<#assign columnlist2 = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductCategoryId)}', dataField: 'primaryProductCategoryId', width: '16%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '14%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', width: '12%', cellsalign: 'right', cellsformat: 'd'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSWeight)} (kg)', dataField: 'totalWeight', width: '12%', cellsalign: 'right', cellsformat: 'd'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName'},
			"/>
<@jqGrid id=gridProductItemsId2 idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist2 dataField=dataField2 
		viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="singleRow" groupable="false" 
		url="jqxGeneralServicer?sname=JQGetListOrderItemReqDelivery&requirementId=${requirement.requirementId}" 
	/>