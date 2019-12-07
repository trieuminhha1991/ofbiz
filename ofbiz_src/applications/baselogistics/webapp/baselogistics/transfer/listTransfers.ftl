<#include "script/listTransferScript.ftl"/>
<#assign columnlist="
			{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},	"/>
<#if security.hasPermission("ACCOUNTING_VIEW", userLogin)>
    <#assign columnlist = columnlist + "
				{ text: '${StringUtil.wrapString(uiLabelMap.TransferId)}', datafield: 'transferId', pinned: true, width:150,
					cellsrenderer: function(row, colum, value){
						var link = 'accViewDetailTransfer?transferId=' + value;
			        	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
					}
				},"/>
<#elseif hasOlbPermission("MODULE", "LOG_TRANSFER", "VIEW")>
    <#assign columnlist = columnlist + "
				{ text: '${StringUtil.wrapString(uiLabelMap.TransferId)}', datafield: 'transferId', pinned: true, width:150,
					cellsrenderer: function(row, colum, value){
						var menuItem = '${selectedMenuItem}';
						var subMenuItem = '${selectedSubMenuItem}';
						var link = 'viewDetailTransfer?transferId=' + value + '&selectedMenuItem='+ menuItem +'&selectedSubMenuItem='+subMenuItem;
				    	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
					}
				},"/>
</#if>
<#assign columnlist = columnlist + "
			{ text: '${uiLabelMap.TransferType}', dataField: 'transferTypeId',  width: 200, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					for (var i = 0; i < transferTypeData.length; i ++){
						if (value && value == transferTypeData[i].transferTypeId){
							return '<span>' + transferTypeData[i].description + '<span>';
						}
					}
					return '<span>' + value + '<span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(transferTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'transferTypeId', valueMember: 'transferTypeId',
						renderer: function(index, label, value){
				        	if (transferTypeData.length > 0) {
								for(var i = 0; i < transferTypeData.length; i++){
									if(transferTypeData[i].transferTypeId == value){
										return '<span>' + transferTypeData[i].description + '</span>';
									}
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					for (var i = 0; i < statusData.length; i ++){
						if (value && value == statusData[i].statusId){
							return '<span>' + statusData[i].description + '<span>';
						}
					}
					return '<span>' + value + '<span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
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
			{ text: '${uiLabelMap.OriginFacility}', dataField: 'originFacilityName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.DestFacility}', dataField: 'destFacilityName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.RemainingSubTotal}', dataField: 'grandTotal', filtertype: 'number', width: 150, editable:false, cellsformat: 'd', cellsalign: 'right',
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.TransferDate}', hidden: true, width: 150, dataField: 'transferDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}
			},
			{ text: '${uiLabelMap.ShipAfterDate}', width: 150, dataField: 'shipAfterDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}
			},
			{ text: '${uiLabelMap.ShipBeforeDate}', width: 150, dataField: 'shipBeforeDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}
			},
			{ hidden:true, text: '${uiLabelMap.NeedsReservesInventory}', dataField: 'needsReservesInventory', minwidth: 130, editable:false,
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 if (value == 'Y'){
							 return '<span >${uiLabelMap.LogYes}</span>';
						 } else if (value == 'N'){
							 return '<span >${uiLabelMap.LogNO}</span>';
						 }
						 return '<span >'+ value +'</span>';
					 }
				}
			},
			{ hidden:true, text: '${uiLabelMap.MaySplit}', dataField: 'maySplit', minwidth: 130, editable:false,
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 if (value == 'Y'){
							 return '<span>${uiLabelMap.LogYes}</span>';
						 } else if (value == 'N'){
							 return '<span>${uiLabelMap.LogNO}</span>';
						 }
						 return '<span>'+ value +'</span>';
					 }
				}
			},
			{ hidden:true, text: '${uiLabelMap.Priority}', dataField: 'priority', minwidth: 100, editable:false, filtertype: 'number',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ value +'</span>';
					 }
				}
			},
			{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.CreatedDate}', width: 150, dataField: 'createdDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				}, 
			},
			{ text: '${uiLabelMap.CreatedBy}', dataField: 'createdByUserLogin', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.LastModifyBy}', dataField: 'lastModifyByUserLogin', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
	"/>
<#assign dataField="[{ name: 'transferId', type: 'string' },
	{ name: 'transferTypeId', type: 'string'},
	{ name: 'statusId', type: 'string'},
	{ name: 'originFacilityId', type: 'string' },
	{ name: 'destFacilityId', type: 'string' },
	{ name: 'originFacilityName', type: 'string' },
	{ name: 'destFacilityName', type: 'string' },
	{ name: 'originContactMechId', type: 'string'},
	{ name: 'createdDate', type: 'date', other: 'Timestamp'},
	{ name: 'transferDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
	{ name: 'needsReservesInventory', type: 'string'},
	{ name: 'maySplit', type: 'string'},
	{ name: 'priority', type: 'number'},
	{ name: 'description', type: 'string'},
	{ name: 'createdByUserLogin', type: 'string'},
	{ name: 'lastModifyByUserLogin', type: 'string'},
	{ name: 'grandTotal', type: 'number'},
 	]"/>
<@jqGrid filtersimplemode="true" id="jqxgridTransfer" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false"
url="jqxGeneralServicer?sname=JQGetListTransfer&transferTypeId=${parameters.transferTypeId?if_exists}" customTitleProperties="ListTransfer"
jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="TransferMenu"
customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript:ListTransferObj.prepareCreateNewTransfer();"
/>
<div id='TransferMenu' style="display:none;">
    <ul>
        <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
        <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>