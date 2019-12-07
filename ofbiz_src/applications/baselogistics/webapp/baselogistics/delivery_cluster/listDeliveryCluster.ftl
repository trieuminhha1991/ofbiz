<#include "script/listDeliveryClusterScript.ftl"/>
<#assign id="jqxgridDeliveryCluster"/>
<script type="text/javascript">
    var listStatusItem =
            [
                {
                    statusId: 'DELIVERY_CLUSTER_ENABLED',
                    description: '${StringUtil.wrapString(uiLabelMap.BLEnabled)}'
                },
                {
                    statusId: 'DELIVERY_CLUSTER_DISABLED',
                    description: '${StringUtil.wrapString(uiLabelMap.BLDisabled)}'
                },
            ];
    var mapStatusItem = {
        'DELIVERY_CLUSTER_ENABLED': '${StringUtil.wrapString(uiLabelMap.BLEnabled)}',
        'DELIVERY_CLUSTER_DISABLED': '${StringUtil.wrapString(uiLabelMap.BLDisabled)}'
    };
</script>

<#assign dataField="[
				{name: 'deliveryClusterId', type: 'string'},
				{name: 'deliveryClusterCode', type: 'string'},
				{name: 'deliveryClusterName', type: 'string'},
				{name: 'description', type: 'string'},
				{name: 'createdDate', type: 'date', other: 'Timestamp'},
				{name: 'createdByUserLoginId', type: 'string'},
				{name: 'executorId', type: 'string'},
				{name: 'executorCode', type: 'string'},
				{name: 'executorName', type: 'string'},
				{name: 'managerId', type: 'string'},
				{name: 'managerCode', type: 'string'},
				{name: 'managerName', type: 'string'},
				{name: 'statusId', type: 'string'}]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BLDeliveryClusterCode)}', datafield: 'deliveryClusterCode', width: '10%', editable: false, pinned: true,
                    cellsrenderer: function(row, column, value){
                        var data = $('#jqxgridDeliveryCluster').jqxGrid('getrowdata', row);
                        return '<span><a href=\"deliveryClusterDetail?deliveryClusterId=' + data.deliveryClusterId + '\">' + value + '</a></span>';
                    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BLDeliveryClusterName)}', datafield: 'deliveryClusterName', width: '10%'},

				{text: '${StringUtil.wrapString(uiLabelMap.BLShipperCodeShort)}', datafield: 'executorCode', width: '12%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BLShipperNameShort)}', datafield: 'executorName', width: '16%'},

				{text: '${StringUtil.wrapString(uiLabelMap.BLManagerCode)}', datafield: 'managerCode', width: '12%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BLManagerName)}', datafield: 'managerName', width: '16%'},

                {text: '${uiLabelMap.DACreateDate}', dataField: 'createdDate', cellsformat: 'dd/MM/yyyy - HH:mm:ss', width: '14%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: '12%', editable: true,
					cellsrenderer: function(row, colum, value){
						value?value=mapStatusItem[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapStatusItem[value];
			                }
    		        	});
					}
				}
			"/>
<#assign customcontrol1="fa fa-globe open-sans@${uiLabelMap.BLDeliveryClusterOnMap}@javascript: void(0)@OlbDeliveryClusterOnMap.open()"/>
<#assign customcontrol2="fa fa-plus open-sans@${uiLabelMap.AddNew}@javascript: void(0)@OlbListDeliveryClusterObj.openAddNewDeliveryCluster()"/>

<@jqGrid id="jqxgridDeliveryCluster" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
    showtoolbar="true" alternativeAddPopup="alterpopupWindowEdit" autorowheight="true" viewSize="10"
    url="jqxGeneralServicer?sname=JQGetListDeliveryCluster" contextMenuId="contextMenu" mouseRightMenu="true"
    deleterow="true" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteDeliveryCluster" deleteColumn="deliveryClusterId"
    updateUrl="jqxGeneralServicer?jqaction=U&sname=updateDeliveryCluster"
    editColumns="deliveryClusterId;deliveryClusterCode;deliveryClusterName;description;executorId"
    customcontrol1=customcontrol1 customcontrol2=customcontrol2
/>

<#include "clusterContextMenu.ftl"/>
<#include "deliveryClusterOnMap.ftl"/>
<#include "popup/deliveryClusterUpdatePopup.ftl"/>
<#include "popup/deliveryClusterCustomerPopup.ftl"/>
<#include "popup/deliveryClusterUpdateLocationPopup.ftl"/>
<#include "popup/deliveryClusterUpdateCustomerOnMap.ftl"/>