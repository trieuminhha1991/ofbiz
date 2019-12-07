<#include "script/returnProductScript.ftl"/>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},		
					{ text: '${uiLabelMap.OrderReturnId}', pinned: true, dataField: 'returnId', width: 120, editable:false, 
						cellsrenderer: function(row, column, value){
							 return '<span><a href=\"javascript:ReturnObj.showDetailReturn(&#39;' + value + '&#39;)\"> ' + value  + '</a></span>'
						}
					},
					"/>
<#if parameters.returnHeaderTypeId?exists && parameters.returnHeaderTypeId == 'CUSTOMER_RETURN'>
	<#assign columnlist = columnlist + "
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for (var i = 0; i < cusStatusData.length; i ++){
								if (value && value == cusStatusData[i].statusId){
									return '<span>' + cusStatusData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(cusStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
						        	if (cusStatusData.length > 0) {
										for(var i = 0; i < cusStatusData.length; i++){
											if(cusStatusData[i].statusId == value){
												return '<span>' + cusStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			},
					},
					"/>
<#else>
	<#assign columnlist = columnlist + "				
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for (var i = 0; i < supStatusData.length; i ++){
								if (value && value == supStatusData[i].statusId){
									return '<span>' + supStatusData[i].description + '<span>';
								}
							}
							return '<span>' + value + '<span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(supStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
						        	if (supStatusData.length > 0) {
										for(var i = 0; i < supStatusData.length; i++){
											if(supStatusData[i].statusId == value){
												return '<span>' + supStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
							},
					},
					"/>
</#if>
	<#assign columnlist = columnlist + "
					{ text: '${uiLabelMap.ReturnFromPartyId}', dataField: 'fromPartyCode', width: 150, editable:false,
						cellsrenderer: function(row, column, value){
						}
					},
					{ text: '${uiLabelMap.ReturnFrom}', dataField: 'fromGroupName', width: 150, editable:false,
						cellsrenderer: function(row, column, value){
							if (!value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (data.fromPartyFullName && '  ' != data.fromPartyFullName){
									return '<span>' + data.fromPartyFullName + ' [ '+ value +' ]' +'<span>';
								}
							}
						}
					},
					{ text: '${uiLabelMap.ReturnToPartyId}', dataField: 'toPartyCode', width: 200, editable:false,
						cellsrenderer: function(row, column, value){
						}
					},
					{ text: '${uiLabelMap.ReturnTo}', dataField: 'toGroupName', minwidth: 200, editable:false,
						cellsrenderer: function(row, column, value){
							if (!value){
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								if (data.toPartyFullName && '  ' != data.toPartyFullName){
									return '<span>' + data.toPartyFullName + ' [ '+ value +' ]' + '<span>';
								}
							}
						}
					},
					"/>

	<#assign columnlist = columnlist + " 
					{ text: '${uiLabelMap.FacilityId}', dataField: 'facilityCode', width: 150, editable:false,
					},
					{ text: '${uiLabelMap.FacilityName}', dataField: 'facilityName', width: 150, editable:false,
					},
					"/>
	<#assign columnlist = columnlist + "
		{ text: '${uiLabelMap.OrderEntryDate}', width: 150, dataField: 'entryDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
			cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+ ReturnObj.formatFullDate(value)+'</span>';
				 }
			}, 
		},
	"/>
	<#assign dataField="[{ name: 'returnId', type: 'string' },
	{ name: 'returnHeaderTypeId', type: 'string'},
	{ name: 'statusId', type: 'string'},
	{ name: 'createdBy', type: 'string'},
	{ name: 'fromPartyId', type: 'string' },
	{ name: 'toPartyId', type: 'string'},
	{ name: 'fromPartyCode', type: 'string' },
	{ name: 'toPartyCode', type: 'string'},
	{ name: 'paymentMethodId', type: 'string'},
	{ name: 'findAccountId', type: 'string'},
	{ name: 'entryDate', type: 'date', other: 'Timestamp' },
	{ name: 'originContactMechId', type: 'string'},
 	{ name: 'destinationFacilityId', type: 'string' },
 	{ name: 'facilityCode', type: 'string' },
	{ name: 'needsInventoryReceive', type: 'string'},
	{ name: 'currencyUomId', type: 'string'},
	{ name: 'toPartyFullName', type: 'string'},
	{ name: 'toGroupName', type: 'string'},
	{ name: 'fromPartyFullName', type: 'string'},
	{ name: 'fromGroupName', type: 'string'},
	{ name: 'facilityName', type: 'string'},
	{ name: 'returnTypeDesc', type: 'string'},
	{ name: 'statusDesc', type: 'string'},
 	]"/>
	<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
		 url="jqxGeneralServicer?sname=JQGetListProductReturn&returnHeaderTypeId=${parameters.returnHeaderTypeId?if_exists}&statusId=${parameters.statusId?if_exists}"  editmode="dblclick"
		 customTitleProperties="LogListProductReturn" mouseRightMenu="true" contextMenuId="ReturnMenu"
		 jqGridMinimumLibEnable="true" bindresize="false" 
	 />
	
<div id='ReturnMenu' style="display:none;">
	<ul>
	    <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>