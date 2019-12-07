<script type="text/javascript" src="/webposSetting/images/js/purchase/ShowPurchasePlanInfo.js"></script>
<#include "script/ShowPurchasePlanInfoScript.ftl"/>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	showPurchasePlanObject.initRowDetail(index, parentElement, gridElement, datarecord);
}"/>
<#assign columngroups = "
				{ text: '${uiLabelMap.SettingSystemInfo}', align: 'center', name: 'SystemInformation' },
				{ text: '${uiLabelMap.SettingPurchaseOrder}', align: 'center', name: 'PurchaseOrder' }"/>

<#assign dataField = "[{ name: 'productId', type: 'string' },
					{ name: 'planId', type: 'string' },
					{ name: 'internalName', type: 'string' },
					{ name: 'qoh', type: 'number' },
					{ name: 'qoo', type: 'number' },
					{ name: 'qpdL', type: 'number' },
					{ name: 'qpdS', type: 'number' },
					{ name: 'lidL', type: 'number' },
					{ name: 'lidS', type: 'number' },
					{ name: 'lastSold', type: 'date', other: 'Timestamp' },
					{ name: 'lastReceived', type: 'date', other: 'Timestamp' },
					{ name: 'quantity', type: 'number' },
					{ name: 'totalLidL', type: 'number' },
					{ name: 'totalLidS', type: 'number' },
					{ name: 'unitCost', type: 'number' },
					{ name: 'grandTotal', type: 'number' },
					{ name: 'comments', type: 'string' },
					{ name: 'currencyUomId', type: 'string' }]"/>

<#assign columnlist = "
					{ text: '${uiLabelMap.SettingProductID}', datafield: 'productId', editable: false, cellsalign:'center',
						width: 100, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingProductName}', datafield: 'internalName',
						width: 200, columngroup: 'SystemInformation', editable: false
					},
					{ text: '${uiLabelMap.SettingSummaryQOH}', datafield: 'qoh', filtertype: 'number',editable: false, cellsalign:'right',
					  width: 70, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingQOO}', datafield: 'qoo',editable: false, cellsalign:'right', filtertype: 'number',
					  width: 70, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingQPDL}', datafield: 'qpdL', filtertype: 'number',editable: false, cellsalign:'right',
						width: 70, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingQPDS}', datafield: 'qpdS', filtertype: 'number',editable: false, cellsalign:'right',
						width: 70, columngroup: 'SystemInformation'
					},
					
					{ text: '${uiLabelMap.SettingLIDL}', datafield: 'lidL', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number',
						width: 70, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingLIDS}', datafield: 'lidS', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number',
						width: 70, columngroup: 'SystemInformation'
					},
					{ text: '${uiLabelMap.SettingLastsold}', datafield: 'lastSold', filtertype: 'range', cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right', columngroup: 'SystemInformation', editable: false
					},
					{ text: '${uiLabelMap.SettingLastReceived}', datafield: 'lastReceived',filtertype: 'range', cellsformat: 'dd/MM/yyyy',
						width: 150, cellsalign: 'right', columngroup: 'PurchaseOrder', editable: false
					},
					{ text: '${uiLabelMap.SettingTotalPOQuantity}', datafield: 'quantity',editable:false, cellsalign:'right', filtertype: 'number', filterable: false,
				  		width: 90, columngroup: 'PurchaseOrder', cellsformat: 'd'
					},
					{ text: '${uiLabelMap.SettingLIDL}', datafield: 'totalLidL', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number', filterable: false,
						width: 70, columngroup: 'PurchaseOrder'
					},
					{ text: '${uiLabelMap.SettingLIDS}', datafield: 'totalLidS', editable: false, cellsalign: 'right', cellsalign:'right', filtertype: 'number', filterable: false,
						width: 70, columngroup: 'PurchaseOrder'
					},
					{ text: '${uiLabelMap.SettingUnitCostPurchase}', datafield: 'unitCost', editable:true, cellsalign:'right', filtertype: 'number', filterable: false,
						width: 100, columngroup: 'PurchaseOrder', columntype: 'numberinput', cellclassname: commonObject.cellCanEdit,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxGridProductList').jqxGrid('getrowdata', row);
							if (data && data.unitCost) {
								return '<div style=\"text-align: right;\">' + commonObject.formatcurrency(data.unitCost, data.currencyUomId) + '</div>';
							} else {
								return '<div style=\"text-align: right;\">' + commonObject.formatcurrency(0, data.currencyUomId) + '</div>';
							}
						}, initeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({min: 0})
						}, validation : function (cell,value) {
							if ( value < 0) {
								return {result:false , message: '${uiLabelMap.SettingQuantityIsMustGreaterZero}'};
							}
							return true;
						}
					},
					{ text: '${uiLabelMap.SettingTotalCost}', datafield: 'grandTotal', editable:false, cellsalign:'right', filtertype: 'number', filterable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxGridProductList').jqxGrid('getrowdata', row);
							if (data && data.grandTotal){
								return '<div style=\"text-align: right;\">' + commonObject.formatcurrency(data.grandTotal, data.currencyUomId) + '</div>';
							} else {
								return '<div style=\"text-align: right;\">' + commonObject.formatcurrency(0, data.currencyUomId) + '</div>';
							}
						},
						width: 100,columngroup: 'PurchaseOrder',
					},
					{ text: '${uiLabelMap.SettingQuantityCheck}', datafield: 'qtyCheck', editable:false, filterable: false,
						width: 100, columngroup: 'PurchaseOrder', cellclassname: commonObject.cellCanEdit
					},
					{ text: '${uiLabelMap.SettingNotes}', datafield: 'comments', editable:true, filterable: false,
						width: 200, columngroup: 'PurchaseOrder', cellclassname: commonObject.cellCanEdit
					}"/>

<@jqGrid filtersimplemode="true" id="jqxGridProductList" dataField=dataField filterable="true" columnlist=columnlist
	clearfilteringbutton="true" showtoolbar="true" editable="true" editrefresh="true" editmode="click"
	columngrouplist=columngroups initrowdetails = "true" filtersimplemode = "true"
	customTitleProperties= "${uiLabelMap.SettingPurchaseHistory}: ${parameters.orderId}"
	initrowdetailsDetail= initrowdetailsDetail
	url="jqxGeneralServicer?sname=JQGetPlanPurchaseItemList&orderId=${parameters.orderId}"
	bindresize="true"/>