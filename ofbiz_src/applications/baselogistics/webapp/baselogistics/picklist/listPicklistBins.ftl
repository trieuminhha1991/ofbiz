<#include "script/listPicklistBinsScript.ftl"/>
<div>
<#assign datafileds="[{ name: 'picklistId', type: 'string'},
					{ name: 'picklistBinId', type: 'string'},
					{ name: 'primaryOrderId', type: 'string'},
					{ name: 'binStatusId', type: 'string'},
			   ]"/>
<#assign columnlist="
		{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoPhieuSoan)}', datafield: 'picklistBinId', width: 250,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='PicklistDetail?picklistBinId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'binStatusId', width: 200, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				if (value) {
					return '<span>' + getStatusDescription (value)+ '</span>';				
				}
			},
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({ source: picklistBinStatusData, displayMember: 'description', valueMember: 'statusId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'primaryOrderId', minwidth: 250,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='viewOrder?orderId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		}
	"/>
<div id="notifyContainer" >
	<div id="notifyContainer"></div>
</div>

<div id='contextMenuPicklistBin' style="display:none;">
	<ul>
		<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
			<li id="mnuCreateDelivery"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.BLCreateSalesDelivery}</li>
		</#if>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
	<@jqGrid filtersimplemode="true" id="jqxgridPicklistBin" addType="popup" dataField=datafileds columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" editable="false" addrefresh="true"  alternativeAddPopup="" customTitleProperties="BLListPickingList"
		url="jqxGeneralServicer?sname=jQGetListPicklistBins" mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenuPicklistBin" showlist="true"/>
</div>