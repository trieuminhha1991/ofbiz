<@jqGridMinimumLib />
<@jqOlbCoreLib hasComboBoxSearchRemote=true/>
<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
<#include 'script/purchaseDeliveryCommonScript.ftl'>
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},		
				{ text: '${uiLabelMap.ReceiveNoteId}', pinned: true, dataField: 'deliveryId', width: 120, editable:false, 
					cellsrenderer: function(row, column, value){
					 	var data = $('#jqxgridDelivery').jqxGrid('getrowdata', row);
						return '<span><a href=\"javascript:PODlvObj.showDetailDelivery(&#39;'+value+'&#39;, &#39;'+data.orderId+'&#39;);\"> ' + value  + '</a></span>';
				 	}
				},
				{ text: '${uiLabelMap.OrderId}', pinned: true, dataField: 'orderId', width: 120, editable:false, 
				cellsrenderer: function(row, column, value){
					 return \"<span><a target='_blank' href='viewDetailPO?orderId=\" + value + \"'>\" + value + \"</a></span>\";
				 }
				},
				{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 145, editable:false, filtertype: 'checkedlist',
					cellsrenderer: function(row, column, value){
						var desc = value;
						if ('DLV_EXPORTED' == value){
							desc = '${StringUtil.wrapString(uiLabelMap.Shipping)}';
				        } else if ('DLV_DELIVERED' == value){
				        	desc = '${StringUtil.wrapString(uiLabelMap.Completed)}';
				        } else {
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									desc = statusData[i].description;
								}
							}
				        }
						return '<span title=' + desc + '>' + desc + '</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if ('DLV_EXPORTED' == value){
									return '<span>${StringUtil.wrapString(uiLabelMap.Shipping)}</span>';
						        } else if ('DLV_DELIVERED' == value){
						        	return '<span>${StringUtil.wrapString(uiLabelMap.Completed)}</span>';
						        } else {
						        	if (statusData.length > 0) {
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
									}
						        }
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			},
				 },
				 { text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				 },
				 { text: '${uiLabelMap.FormFieldTitle_invoiceId}', dataField: 'invoiceId', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
					 return \"<span><a target='_blank' href='ViewAPInvoice?invoiceId=\" + value + \"'>\" + value + \"</a></span>\";
					 },
				 },
				 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				 },
				{ text: '${uiLabelMap.EstimatedStartDelivery}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.EstimatedEndDelivery}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualStartDelivery}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualEndDelivery}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ReceiveToFacility}', dataField: 'destFacilityName', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						 return '<span title=\"' + value + '\">' + value + '</span>';
					 },
				 },
				 { text: '${uiLabelMap.FacilityAddress}', dataField: 'destAddress', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 }, 
				 },
				{ text: '${uiLabelMap.ReceiveBatchNumber}', hidden: true, dataField: 'no', width: 120, editable:false,
					cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 "/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'statusId', type: 'string' },
             	{ name: 'partyIdTo', type: 'string' },
             	{ name: 'destContactMechId', type: 'string' },
             	{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'orderId', type: 'string' },
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
				{ name: 'destAddress', type: 'string' },
				{ name: 'originAddress', type: 'string' },
				{ name: 'invoiceId', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' },
	 		 	]"/>
<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE") && hasRoles == true>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&deliveryTypeId=DELIVERY_PURCHASE" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 updateUrl="" editColumns="" functionAfterAddRow="PODlvObj.checkCreatedDone" customTitleProperties="ListReceiveNote" selectionmode="singlecell"
		 jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="DeliveryMenu" viewSize="20" customtoolbaraction="extendToolbar"
	 />
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&deliveryTypeId=DELIVERY_PURCHASE" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 updateUrl="" editColumns="" functionAfterAddRow="" customTitleProperties="ListReceiveNote" selectionmode="singlecell"
		 jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="DeliveryMenu" viewSize="20"  customtoolbaraction="extendToolbar"
	 />
</#if>
<#include 'purchaseDeliveryCommon.ftl'>