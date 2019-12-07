<div id="contextMenu" class="hide">
    <ul>
        <li id="mnitemViewdetailnewtab"><i
                class="fa fa-folder-open-o"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}
        </li>
        <li id="mnitemViewdetail"><i
                class="fa fa-folder-open-o"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <li id="mnitemRefesh"><i class="fa fa-refresh"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSRefresh)}
        </li>
        <li id="mnitemEditInfomation"><i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.Edit)} ${StringUtil.wrapString(uiLabelMap.PartyBasicInformation)}
        </li>
        <li id="mnitemUpdateListCustomerOnMap">
            <i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BLUpdateClusterCustomerOnMap)}
        </li>
        <li id="mnitemUpdateListCustomer">
            <i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BLUpdateClusterCustomer)}
        </li>
    </ul>
</div>
<script type="text/javascript">
    if (typeof (ContextMenuDeliveryCluster) == "undefined") {
        var ContextMenuDeliveryCluster = (function () {
            var initContext = function () {
                var grid = $('#jqxgridDeliveryCluster');
                var ct = $('#contextMenu');
                ct.jqxMenu({theme: 'olbius', width: 280, autoOpenPopup: false, mode: 'popup'});
                ct.on('itemclick', function (event) {
                    var args = event.args;
                    var itemId = $(args).attr('id');
                    var rowIndexSelected = grid.jqxGrid('getSelectedRowindex');
                    var rowData = grid.jqxGrid("getrowdata", rowIndexSelected);
                    switch (itemId) {
                        case "mnitemViewdetailnewtab":
                            if (rowData) {
                                var url = 'deliveryClusterDetail?deliveryClusterId=' + rowData.deliveryClusterId;
                                var win = window.open(url, "_blank");
                                win.focus();
                            }
                            break;
                        case "mnitemViewdetail":
                            if (rowData) {
                                var url = 'deliveryClusterDetail?deliveryClusterId=' + rowData.deliveryClusterId;
                                var win = window.open(url, "_self");
                                win.focus();
                            }
                            break;
                        case "mnitemUpdateListCustomer":
                            if (rowData) {
                                OlbDeliveryClusterCustomerObj.open(rowIndexSelected);
                            }
                            break;
                        case "mnitemEditInfomation":
                            if (rowData) {
                                OlbUpdateDeliveryClusterObj.updatePopup(rowIndexSelected);
                            }
                            break;
                        case "mnitemRefesh":
                            grid.jqxGrid('updatebounddata');
                            break;
                            break;
                        case "mnitemUpdateListCustomerOnMap":
                            OlbDeliveryClusterCustomerOnMapObj.open(rowIndexSelected);
                            break;
                            break;
                        default:
                            break;
                    }
                });
            };
            return {
                initContext: initContext,
            }
        })();
    }
    $(function () {
        if (typeof (ContextMenuDeliveryCluster) != "undefined") {
            ContextMenuDeliveryCluster.initContext();
        }
    });
</script>