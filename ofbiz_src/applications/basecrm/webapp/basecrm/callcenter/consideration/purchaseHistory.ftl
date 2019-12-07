<script src="/crmresources/js/callcenter/purchaseHistory.js"></script>

<#assign dataField="[{ name: 'productId', type: 'string' },
					 { name: 'productCode', type: 'string' },
					 { name: 'productName', type: 'string' },
					 { name: '_count', type: 'number' },
					 { name: 'fromDate', type: 'date', other: 'Timestamp' },
					 { name: 'thruDate', type: 'date', other: 'Timestamp' }]"/>
<#assign columnlist = "{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
							cellsrenderer: function (row, column, value) {
								return '<div style=margin:4px;>' + (row + 1) + '</div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productCode', width: 150 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName' },
						{ text: '${StringUtil.wrapString(uiLabelMap.CRMFirstPurchase)}', datafield: 'fromDate', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
						{ text: '${StringUtil.wrapString(uiLabelMap.CRMRecentPurchase)}', datafield: 'thruDate', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
						{ text: '${StringUtil.wrapString(uiLabelMap.CRMTotalPurchases)}', datafield: '_count', filtertype: 'number', width: 120,
							cellsrenderer: function(row, colum, value){
								return '<span class=\"text-right\">' + value.toLocaleString(locale) + '</span>';
							}
						}"/>

<@jqGrid url="" dataField=dataField columnlist=columnlist filterable="true" customLoadFunction="true" viewSize="5"
	clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" 
	addrow="false" id="jqxPurchaseHistory" isShowTitleProperty="false"/>