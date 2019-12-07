<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
<#include "script/transferDeliveryCommonScript.ftl"/>

<#assign columnlist="
	{	text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	    	return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},			
	{ text: '${uiLabelMap.CommonDeliveryId}', dataField: 'deliveryId', width: 120, editable:false,
		cellsrenderer: function(row, column, value){
		 	return '<span><a href=\"javascript:TransferDlvObj.showDetailDelivery(&#39;'+value+'&#39;);\"> ' + value  + '</a></span>'
	 	}
	},
	{ text: '${uiLabelMap.TransferId}', dataField: 'transferId', width: 120, editable:false, 
		cellsrenderer: function(row, column, value){
			 return \"<span><a target='_blank' href='viewDetailTransfer?transferId=\" + value + \"'>\" + value + \"</a></span>\";
	 	}
	},
	{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:true, columntype: 'dropdownlist',
		cellsrenderer: function(row, column, value){
			for(var i = 0; i < statusData.length; i++){
				if(statusData[i].statusId == value){
					return '<span title=' + value + '>' + statusData[i].description + '</span>'
				}
			}
		},
		createfilterwidget: function (column, columnElement, widget) {
					 		if (statusData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										if (statusData.length > 0) {
											for(var i = 0; i < statusData.length; i++){
												if(statusData[i].statusId == value){
													return '<span>' + statusData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   			},
		filtertype: 'checkedlist'
	 },
	 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityName', width: 150, editable:false,
	 },
	 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityName', editable:false, width: 150,
	 },
	 { text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.EstimatedStartDate}', dataField: 'estimatedStartDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.EstimatedArrivalDate}', dataField: 'estimatedArrivalDate', width: 160, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false,
		 cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" ></span>';
			 } else {
				 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
			 }
		 }, 
	 },
	 "/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
		{ name: 'statusId', type: 'string' },
	 	{ name: 'originFacilityId', type: 'string' },
	 	{ name: 'destFacilityId', type: 'string' },
	 	{ name: 'transferId', type: 'string' },
	 	{ name: 'destFacilityName', type: 'string' },
	 	{ name: 'originFacilityName', type: 'string' },
	 	{ name: 'createDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
	 	{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
		{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
		{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
		{ name: 'defaultWeightUomId', type: 'string' },
]"/>
<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}"
		 updateUrl="jqxGeneralServicer?sname=updateTransferDelivery&jqaction=U" functionAfterAddRow="TransferDlvObj.afterAddDelivery()" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 />
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 />
</#if>		 	 
<#include 'transferDeliveryCommon.ftl'>
