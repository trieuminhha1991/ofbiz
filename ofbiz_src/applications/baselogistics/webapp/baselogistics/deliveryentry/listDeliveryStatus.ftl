<#include "script/listDeliveryStatusScript.ftl"/>
<#assign columnlist="
			{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},	"/>

<#assign columnlist = columnlist + "
            { text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', pinned: true, width:150,
					cellsrenderer: function(row, colum, value){
						var menuItem = '${selectedMenuItem}';
						var subMenuItem = '${selectedSubMenuItem}';
						var link = 'viewOrder?orderId=' + value + '&selectedMenuItem='+ menuItem +'&selectedSubMenuItem='+subMenuItem;
				    	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
					}
				},
			{ text: '${StringUtil.wrapString(uiLabelMap.BLDeliveryCluster)}', datafield: 'deliveryClusterCode', width:150,
				},
			{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 200, editable:false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
				    var data = grid.jqxGrid('getrowdata', row);
					for (var i = 0; i < statusData.length; i ++){
						if (value && value == statusData[i].statusId){
							return '<span>' + statusData[i].description + '<span>';
						}
					}

					return '<span>' + '${StringUtil.wrapString(uiLabelMap.BLUnAssignedTripOrder)}' + '<span>';
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

			{ text: '${uiLabelMap.Customer}', dataField: 'partyName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.Address}', dataField: 'postalAddressName', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			,
			{ text: '${uiLabelMap.CreatedBy}', dataField: 'createdBy', minwidth: 200, editable:false,
				cellsrenderer: function(row, column, value){
				}
			},
			{ text: '${uiLabelMap.Amount}', dataField: 'totalGrandAmount', minwidth: 200, editable:false, cellsalign: 'right',filtertype: 'number',
				cellsrenderer: function(row, column, value){
				    return '<span style=\"text-align: right\">'+formatnumber(value) +'</span>';
				}
			},
			{ text: '${uiLabelMap.ShipAfterDate}', width: 150, dataField: 'shipAfterDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 
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

			{ text: '${uiLabelMap.CreatedDate}', width: 150, dataField: 'entryDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
				cellsrenderer: function(row, column, value){
					 if (!value){
						 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
					 } else {
						 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
					 }
				},
			}
	"/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
	{ name: 'statusId', type: 'string'},
	{ name: 'deliveryClusterCode', type: 'string'},
	{ name: 'orderStatusId', type: 'string'},
	{ name: 'originContactMechId', type: 'string'},
	{ name: 'entryDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
	{ name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
	{ name: 'description', type: 'string'},
	{ name: 'createdBy', type: 'string'},
	{ name: 'lastModifyByUserLogin', type: 'string'},
	{ name: 'partyName', type: 'string'},
	{ name: 'postalAddressName', type: 'string'},
	{ name: 'totalGrandAmount', type: 'number'},
 	]"/>
<@jqGrid filtersimplemode="true" id="jqxgridPack" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false"
url="jqxGeneralServicer?sname=JQGetListOrderWithPackDelivery" customTitleProperties="BLListDeliveryStatus"
jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="PackMenu"
/>
<div id='PackMenu' style="display:none;">
    <ul>
        <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
        <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>