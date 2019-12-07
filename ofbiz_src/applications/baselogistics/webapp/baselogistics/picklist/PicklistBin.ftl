<#include "script/PicklistBinScript.ftl"/>
<#assign dataField = "[
		{ name: 'picklistId', type: 'string'},
		{ name: 'picklistBinId', type: 'string'},
		{ name: 'binStatusId', type: 'string'},
		{ name: 'primaryOrderId', type: 'string'},
		{ name: 'partyPickId', type: 'string'},
		{ name: 'partyPickCode', type: 'string'},
		{ name: 'partyPickName', type: 'string'},
		{ name: 'partyCheckId', type: 'string'},
		{ name: 'partyCheckCode', type: 'string'},
		{ name: 'partyCheckName', type: 'string'},
		{ name: 'picklistDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'},
		{ name: 'createdDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'},
		{ name: 'approvedDate', type: 'date', other:'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'}
]"/>

<#assign columnlist = "[
		{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, 
			groupable: false, draggable: false, resizable: false, width: 50,
			cellsrenderer: function (row, column, value) {
				return '<div style=margin:4px;>' + (row + 1) + '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoPhieuSoan)}', datafield: 'picklistBinId', width: 120,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='PicklistDetail?picklistBinId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLDmsSoDotSoan)}', dataField: 'picklistId', width: 100},
		{ text: '${StringUtil.wrapString(uiLabelMap.BSOrderId)}', datafield: 'primaryOrderId', width: 120,
			cellsrenderer: function(row, colum, value) {
				return \"<span><a href='viewOrder?orderId=\" + value + \"' target='_blank'>\" + value + \"</a></span>\";
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'binStatusId', width: 120, filtertype: 'checkedlist',
			cellsrenderer: function(row, column, value){
				value = value?mapPickbinStatus[value]:value;
				return '<span title=' + value +'>' + value + '</span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				widget.jqxDropDownList({ source: pickbinStatusData, displayMember: 'description', valueMember: 'statusId' });
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.CreatedDate)}', datafield: 'createdDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm', filtertype: 'range'},
		{ text: '${StringUtil.wrapString(uiLabelMap.BLApprovedDate)}', datafield: 'approvedDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm', filtertype: 'range'},
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsNhanVienSoan)}', datafield: 'partyPickName'},
		{ text: '${StringUtil.wrapString(uiLabelMap.DmsNhanVienKiem)}', datafield: 'partyCheckName'}
]"/>

<@jqGrid id="jqxgridPicklistBin" clearfilteringbutton="true" alternativeAddPopup="addRival" columnlist=columnlist dataField=dataField 
	contextMenuId="contextMenuPicklistBin" mouseRightMenu="true" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"
	url="jqxGeneralServicer?sname=JQGetListPicklistBin" customtoolbaraction="" selectionmode="singlerow" sourceId="picklistBinId"/>

<div id="contextMenuPicklistBin" style="display:none;">
	<ul>
		<li id="binViewDetail"><i class="fa fa-folder-open-o"></i>${uiLabelMap.BSViewDetail}</li>
		<#if hasOlbPermission("MODULE", "LOG_PICKLIST", "UPDATE")>
		
		<li id="binEmployee"><i class="fa fa-user"></i>${uiLabelMap.Employee}
		</li>
		<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
			<li id="mnuCreateDelivery"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.BLCreateSalesDelivery}</li>
		</#if>
		<li id="binApprove"><i class="fa fa-check"></i>${StringUtil.wrapString(uiLabelMap.BACCApprove)}</li>
		<li id="binCancel"><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li>
		</#if>
	</ul>
</div>

<#-- 
<script type="text/javascript" src="/logresources/js/picklist/picklistBinSearchByProduct.js?v=0.0.1"></script>
<script type="text/javascript">
	var extendToolbar = function(container){
		var str = "<div id='jqxProductSearch' class='pull-right margin-top5 margin-left10'><div id='jqxProductSearchGrid' style='margin-top: 4px;'></div></div>";
		container.append(str);
		
		OlbPicklistBinSearchByProduct.init();
	};
</script>
 -->
<#include "changeEmployeePopup.ftl"/>