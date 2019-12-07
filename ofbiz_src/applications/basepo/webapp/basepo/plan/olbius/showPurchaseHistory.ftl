<#include "script/showPurchaseHistoryScript.ftl"/>
<#include "component://basepos/webapp/basepos/common/showNotification.ftl"/>
<#include "component://basepos/webapp/basepos/facility/script/ShowFacilityListScript.ftl"/>
<#assign dataField = "[{ name: 'orderId', type: 'string' },
					{ name: 'orderDate', type: 'date', other:'Timestamp' },
					{ name: 'statusId', type: 'string' },
					{ name: 'grandTotal', type: 'number'},
					{ name: 'originFacilityId', type: 'string' },
					{ name: 'available', type: 'bool' },
					{ name: 'createdBy', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'currencyUom', type: 'string' }]"/>

<#assign columnlist = "
					{ text: '${uiLabelMap.SettingOrderId}', datafield: 'orderId', editable:false, width: 90, pinned: true,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridPurchaseHistory').jqxGrid('getrowdata', row);
							if (data && data.orderId){
								if(data.orderId === showPurchaseHistoryGlobalObject.orderId && showPurchaseHistoryGlobalObject.flagSelectRow === true){
									showPurchaseHistoryGlobalObject.flagSelectRow = false;
									$('#jqxgridPurchaseHistory').jqxGrid('selectrow', row);	
								}							
								return '<div><a  href=' + 'ShowPurchaseOrder?orderId=' + data.orderId + '>' +  data.orderId + '</a>' + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.SettingOrderDate}', datafield: 'orderDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
					{ text: '${uiLabelMap.SettingOrderStatus}', datafield: 'statusId', filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridPurchaseHistory').jqxGrid('getrowdata', row);
							for (i = 0; i < orderStatusData.length; i++) {
								if(data.statusId == orderStatusData[i].statusId) {
									return '<span style=\"margin-left: 10px;\">' + orderStatusData[i].description + '</span>';
								}
							}
						}, createfilterwidget: function (column, columnElement, widget) {
							var sourceOs =
							{
								localdata: orderStatusData,
								datatype: \"array\"
							};
							var filterBoxAdapterOs = new $.jqx.dataAdapter(sourceOs, {
								autoBind: true
							});
							var uniqueRecordsOs = filterBoxAdapterOs.records;
							widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecordsOs, displayMember: 'description', valueMember: 'statusId', autoDropDownHeight:false, dropDownHeight:200,
								renderer: function (index, label, value) {
									for(i = 0; i < orderStatusData.length; i++){
										if(orderStatusData[i].statusId==value){
											return '<span>' + orderStatusData[i].description + '</span>'
										}
									}
									return value;
								}
							});
						}
					},
					{ text: '${uiLabelMap.SettingGrandTotal}', datafield: 'grandTotal', cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridPurchaseHistory').jqxGrid('getrowdata', row);
							if (data && data.grandTotal){
								return '<div style=\"text-align: right;\">' + formatcurrency(data.grandTotal, data.currencyUom) + '</div>';
							} else {
								return '<div style=\"text-align: right;\">' + formatcurrency(0, data.currencyUom) + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.SettingOrderCreatedBy}', datafield: 'createdBy', width: 250 },
					{ text: '${uiLabelMap.SettingStoreName}', datafield: 'originFacilityId', width: 150,filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridPurchaseHistory').jqxGrid('getrowdata', row);
							
							return '<span style=\"margin-left: 10px;\">' + showFacilityListGlobalObject.getDescriptionFacility(data.originFacilityId) + '</span>';
						}, createfilterwidget: function (column, columnElement, widget) {
							var source =
							{
								localdata: showFacilityListGlobalObject.facilityList,
								datatype: \"array\"
							};
							var filterBoxAdapter = new $.jqx.dataAdapter(source, {
								autoBind: true
							});
							var uniqueRecords = filterBoxAdapter.records;
							widget.jqxDropDownList({selectedIndex: 0,  source: uniqueRecords, displayMember: 'facilityName', valueMember: 'facilityId', autoDropDownHeight:false, dropDownHeight:200,
								renderer: function (index, label, value) {
									return '<span>' + showFacilityListGlobalObject.getDescriptionFacility(value) + '</span>';
								}
							});
						}
					}"/>	
<@jqGrid filtersimplemode="true" dataField=dataField filterable="true" columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" editable="false" bindresize="true" id="jqxgridPurchaseHistory"
	url="jqxGeneralServicer?sname=JQPurchaseOrderHistory"/>

<script type="text/javascript" src="/poresources/js/pos/ShowPurchaseHistory.js"></script>