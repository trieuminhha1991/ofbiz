<#include "script/purchaseShipmentDisScript.ftl">
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<div style=margin:4px;>' + (value + 1) + '</div>';
					}
				},
				
				{ text: '${uiLabelMap.ShipmentId}', pinned: true, dataField: 'shipmentId', width: 150,
					cellsrenderer: function(row, column, value) {
						var data = $('#jqxgridShipmentPurchDis').jqxGrid('getrowdata', row);
						return '<span><a href=\"javascript:smtObj.viewShipmentDetail(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
					}
				},
					
				{ text: '${uiLabelMap.OrderId}', pinned: true, dataField: 'primaryOrderId', width: 150,
					cellsrenderer: function(row, column, value) {
						var data = $('#jqxgridShipmentPurchDis').jqxGrid('getrowdata', row);
						return '<span><a href=\"javascript:smtObj.viewOrderDetail(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
					}
				},
				
				{ text: '${uiLabelMap.DeliveryDoc}', pinned: true, dataField: 'deliveryId', width: 150,
					cellsrenderer: function(row, column, value) {
						var data = $('#jqxgridShipmentPurchDis').jqxGrid('getrowdata', row);
						return '<span><a href=\"javascript:smtObj.viewDeliveryDetail(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>';
					}
				},
				{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, filtertype: 'checkedlist',
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
				},
				{ text: '${uiLabelMap.CreatedDate}', dataField: 'createdDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range',
					cellsrenderer: function (row, column, value) {
					}
				},
				{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 150,
					cellsrenderer: function (row, column, value) {
					}
				},
				"/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'shipmentId', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'primaryOrderId', type: 'string' },
				{ name: 'description', type: 'string' },
				{ name: 'createdDate', type: 'date', other: 'timestamp'}]"/>
<@jqGrid filtersimplemode="true" id="jqxgridShipmentPurchDis" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow"
	url="jqxGeneralServicer?sname=jqGetShipmentPurchDis" editmode="dblclick"  mouseRightMenu="true" contextMenuId="ShipmentMenu" customTitleProperties="ListPurchaseShipment"
	jqGridMinimumLibEnable="true" selectionmode="singlecell" customcontrol1="icon-plus@${uiLabelMap.AddNew}@javascript:smtObj.prepareCreatePurchDisShipment();"/>
	
 <div id="ShipmentMenu" style="display:none;">
	<ul>
		<li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
		<li><i class="fa fa-plus"></i>${StringUtil.wrapString(uiLabelMap.AddNew)}</li>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
