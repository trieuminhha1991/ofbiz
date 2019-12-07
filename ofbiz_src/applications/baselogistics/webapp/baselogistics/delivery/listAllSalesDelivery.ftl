<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
<#include "script/allSalesDeliveryScript.ftl"/>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},"/>
				<#if hasOlbPermission('MODULE', 'DISTRIBUTOR', 'ADMIN')>
					<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DeliveryId}', pinned: true, dataField: 'deliveryId', width: 120,
						cellsrenderer: function(row, column, value) {
							var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
							return '<span><a href=\"javascript:SalesDlvObj.showDetailDeliveryDistributor(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
						}
					},"/>
				<#else>	
					<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DeliveryId}', pinned: true, dataField: 'deliveryId', width: 120,
						cellsrenderer: function(row, column, value) {
							var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
							return '<span><a href=\"javascript:SalesDlvObj.showDetailDelivery(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
						}
					},"/>
				</#if>
				<#assign columnlist = columnlist + "{ text: '${uiLabelMap.OrderId}', pinned: true, dataField: 'orderId', width: 145,
					cellsrenderer: function (row, column, value) {
						return \"<span><a target='_blank' href='viewOrder?orderId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},"/>
				<#if !hasOlbPermission('MODULE', 'DISTRIBUTOR', 'ADMIN')>
				<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 145, filtertype: 'checkedlist',
					cellsrenderer: function (row, column, value) {
						for (var i = 0; i < statusData.length; i++) {
							if (statusData[i].statusId == value) {
								return '<span title=' + value + '>' + statusData[i].description + '</span>'
							}
						}
					},
					createfilterwidget: function (column, columnElement, widget) {
						widget.jqxDropDownList({ source: statusData, displayMember: 'description', valueMember: 'statusId' });
					}
				},"/>
				<#else>
				<#assign columnlist = columnlist + "
					{ text: '${uiLabelMap.ReceiveBatchNumber}', dataField: 'shipmentDistributorId', width: 145,
						cellsrenderer: function (row, column, value) {
						},
					},
					{ text: '${uiLabelMap.Status}', dataField: 'disStatusId', width: 145, filterable: false,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
							if (data.statusId == 'DLV_CANCELLED'){
								return '<span title=' + value + '>${uiLabelMap.BLCancel}</span>'
							}
							else if (data.shipmentDistributorId) {
								return '<span title=' + value + '>${uiLabelMap.Received}</span>'
							} else {
								return '<span title=' + value + '>${uiLabelMap.BLNotYetReceive}</span>'
							}
						},
					},"/>
				</#if>
				<#assign columnlist = columnlist + "
				{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', dataField: 'invoiceId', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
					 return \"<span><a target='_blank' href='ViewARInvoice?invoiceId=\" + value + \"'>\" + value + \"</a></span>\";
					 },
				 },
				{ text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.BLComposeNoteId}', dataField: 'picklistBinId', width: 120, editable:false, 
					cellsrenderer: function(row, column, value){
						return '<span>' + value  + '</span>';
				 	}
				},
				{ text: '${uiLabelMap.CustomerAddress}', dataField: 'destAddress', minwidth: 400,
					cellsrenderer: function (row, column, value) {
						return '<span title=\"' + value + '\">' + value + '</span>'
					}
				},
				{ text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							return '<span style=\"text-align: right\"></span>';
						} else {
							return '<span style=\"text-align: right\">'+ SalesDlvObj.formatFullDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.DeliveryBatchNumber}', dataField: 'shipmentId', width: 120, hidden: true,
					cellsrenderer: function (row, column, value) {
						return '<span title=\"' + value + '\">' + value + '</span>'
					}
				},
				{ text: '${uiLabelMap.ExportFromFacility}', dataField: 'originFacilityName', minwidth: 150,
					cellsrenderer: function (row, column, value) {
						return '<span title=\"' + value + '\">' + value + '</span>';
					},
				},
				"/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'picklistBinId', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'partyIdTo', type: 'string' },
				{ name: 'destContactMechId', type: 'string' },
				{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'orderId', type: 'string' },
				{ name: 'invoiceId', type: 'string' },
				{ name: 'originProductStoreId', type: 'string' },
				{ name: 'originFacilityId', type: 'string' },
				{ name: 'destFacilityId', type: 'string' },
				{ name: 'destFacilityName', type: 'string' },
				{ name: 'originFacilityName', type: 'string' },
				{ name: 'createDate', type: 'date', other: 'Timestamp' },
				{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'totalAmount', type: 'number' },
				{ name: 'no', type: 'string' },
				{ name: 'shipmentId', type: 'string' },
				{ name: 'shipmentDistributorId', type: 'string' },
				{ name: 'destAddress', type: 'string' },
				{ name: 'originAddress', type: 'string' },
				{ name: 'purchaseOrderId', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' }]"/>
<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow"
	url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES&partyIdTo=${partyIdTo?if_exists}" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"  mouseRightMenu="true" contextMenuId="DeliveryMenu"
	functionAfterAddRow="SalesDlvObj.afterAddDelivery()" customTitleProperties="ListDelivery" _customMessErr="SalesDlvObj.customMess"
	jqGridMinimumLibEnable="true" selectionmode="singlecell" viewSize="20" customtoolbaraction="extendToolbar"/>

<#include "salesDeliveryFormCommon.ftl">
<#include "loadDeliveryItems.ftl">