    <script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
    <script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<#include "script/listShippingTripScript.ftl"/>

<div id="jqxNotificationSuccess" style="display: none;">
    <div>
    ${uiLabelMap.UpdateSuccessfully}.
    </div>
</div>
<div id="jqxGridListShippingTrip" style="width: 100%"></div>

<#assign dataField="[{ name: 'shippingTripId', type: 'string' },
					 { name: 'startDateTime', type: 'date', other: 'Timestamp'},
					 { name: 'shipperId', type: 'string'},
					 { name: 'shipperName', type: 'string'},
					 { name: 'finishedDateTime', type: 'date', other: 'Timestamp'},
					 { name: 'tripCost', type: 'number'},
					 { name: 'costCustomerPaid', type: 'number'},
					 { name: 'statusId', type: 'string'}
					 ]"/>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,  cellClassName: ShippingTripObj.cellClass,
				        groupable: false, draggable: false, resizable: false,
				        datafield: '', columntype: 'number', width: 50,
				        cellsrenderer: function (row, column, value) {
				            return '<span style=margin:4px;>' + (value + 1) + '</span>';
				        }
					},					
					{ text: '${uiLabelMap.DeliveryEntryCode}', datafield: 'shippingTripId', width: '10%', editable: false, pinned: true,cellClassName: ShippingTripObj.cellClass,
						cellsrenderer: function(row, column, value){
							//var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span><a href=\"shippingTripDetail?shippingTripId=' + value +'\">' + value + '</a></span>';
						}
				 	},
				 	{ text: '${uiLabelMap.BLShipperCode}', datafield: 'shipperId', width: '17%',minwidth: 200, editable: false,cellClassName: ShippingTripObj.cellClass,
				 	},
				 	{ text: '${uiLabelMap.BLShipperName}', datafield: 'shipperName', width: '16%', editable: false,cellClassName: ShippingTripObj.cellClass,
				 	},
				 	{ text: '${uiLabelMap.BLDstartDateTime}', datafield: 'startDateTime', cellsalign: 'right', width: '15%', cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range',cellClassName: ShippingTripObj.cellClass,
                    },
				 	{ text: '${uiLabelMap.BLDfinishedDateTime}', datafield: 'finishedDateTime', cellsalign: 'right', width: '15%', cellsformat:'dd/MM/yyyy', editable: false, filtertype:'range', cellClassName: ShippingTripObj.cellClass,},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: '17%', editable:false, filtertype: 'checkedlist', cellClassName: ShippingTripObj.cellClass,
                        cellsrenderer: function(row, column, value){
                            var data = grid.jqxGrid('getrowdata', row);
                            for (var i = 0; i < statusAllStatusDE.length; i ++){
                                if (value && value == statusAllStatusDE[i].statusId){
                                    return '<span>' + statusAllStatusDE[i].description + '<span>';
                                }
                            }

                            return '<span>' + '${StringUtil.wrapString(uiLabelMap.BLUnAssignedTripOrder)}' + '<span>';
                        },
                        createfilterwidget: function (column, columnElement, widget) {
                            var filterDataAdapter = new $.jqx.dataAdapter(statusAllStatusDE, {
                                autoBind: true
                            });
                            var records = filterDataAdapter.records;
                            widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
                                renderer: function(index, label, value){
                                    if (statusAllStatusDE.length > 0) {
                                        for(var i = 0; i < statusAllStatusDE.length; i++){
                                            if(statusAllStatusDE[i].statusId == value){
                                                return '<span>' + statusAllStatusDE[i].description + '</span>';
                                            }
                                        }
                                    }
                                    return value;
                                }
                            });
                            widget.jqxDropDownList('checkAll');
                        }
			        },
				 	{ text: '${uiLabelMap.BLDTripCost}', datafield: 'tripCost', width: '16%', editable: false, cellsalign: 'right',cellClassName: ShippingTripObj.cellClass,
                        cellsrenderer: function(row, column, value){
                            return '<span style=\"text-align: right\">'+formatnumber(value) +'</span>';
                        }
				    },
				 	{ text: '${uiLabelMap.BLDCostCustomerPaid}', datafield: 'costCustomerPaid', width: '17%', editable: false, cellsalign: 'right',cellClassName: ShippingTripObj.cellClass,
                        cellsrenderer: function(row, column, value){
                            return '<span style=\"text-align: right\">'+formatnumber(value) +'</span>';
                        }
				    },

 				"/>
 <div id="containerNotify" style="width: 100%; overflow: auto;">
 </div>
<@jqGrid id="jqxGridListShippingTrip"  filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" editable="false"
url="jqxGeneralServicer?sname=JQGetListShippingTrip" contextMenuId="ShippingTripMenu" jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="PackMenu"
customcontrol1="icon-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0);@ShippingTripObj.createShippingTrip()"
/>
<div id='ShippingTripMenu' style="display:none;">
    <ul>
        <li><i class="fa fa-folder-open-o"></i>${uiLabelMap.ViewDetailInNewPage}</li>
        <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <li><i class="fa fa-trash red"></i>${uiLabelMap.CommonCancel}</li>
        <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>
