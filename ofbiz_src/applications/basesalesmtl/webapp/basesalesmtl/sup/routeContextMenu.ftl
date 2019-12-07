<div id="Context${id}" class="hide">
	<ul>
        <li action="detailnewtab"><i class="fa fa-folder-open-o"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
        <li action="detail"><i class="fa fa-folder-open-o"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
        <#if hasOlbPermission("MODULE", "PARTY_ROUTER_NEW", "")>
            <li action="edit">
                <i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.Edit)} ${StringUtil.wrapString(uiLabelMap.PartyBasicInformation)}
            </li>
            <li action="addCustomer">
                <i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSUpdateRouteMap)}
            </li>
            <li action="updateListCustomer">
                <i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSUpdateRoute)}
            </li>
            <li action="generateCustomerSequenceNum">
                <i class="fa fa-car"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSGenCustomerSequenceNum)}
            </li>
            <li action="generateSaleRouteScheduleDetail">
                <i class="fa fa-road"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSgenSaleRouteScheduleDetail)}
            </li>
        </#if>
	</ul>
</div>
<script>
    if (typeof (ContextMenuRoute) == "undefined") {
        var ContextMenuRoute = (function(){
            var initContext = function(){
                var ct = $('#Context${id}');
                ct.jqxMenu({ theme: 'olbius', width: 280, autoOpenPopup: false, mode: 'popup'});
                ct.on('itemclick', function (event) {
                    var args = event.args;
                    var itemId = $(args).attr('action');
                    var grid = $('#${id}');
                    var row = grid.jqxGrid('getSelectedRowindex');
                    switch (itemId) {
                        case "detailnewtab":
                            var rowData = grid.jqxGrid("getrowdata", row);
                            if (rowData) {
                                var url = 'RouteDetail?routeId=' + rowData.routeId;
                                var win = window.open(url, "_blank");
                                win.focus();
                            }
                            break;
                        case "detail":
                            var rowData = grid.jqxGrid("getrowdata", row);
                            if (rowData) {
                                var url = 'RouteDetail?routeId=' + rowData.routeId;
                                var win = window.open(url, "_self");
                                win.focus();
                            }
                            break;
                        case 'edit':
                            AddRouteObj.updatePopup(row);
                            break;
                        /*case 'editaddress':
                            RouteAddress.open(row);
                            break;*/
                        case 'addCustomer':
                            CustomerRoute.open(row);
                            break;
                        case 'updateListCustomer':
                            OlbCustomerRoute.open(row);
                            break;
                        case 'generateCustomerSequenceNum':
                            OlbCustomerSequenceNum.openPopup(row);
                            break;
                        case 'generateSaleRouteScheduleDetail':
                            OlbGenSaleRouteSchedule.openWindow(row);
                            break;
                        default:
                            break;
                    }
                });
            };
            return {
                initContext:initContext,
            }
        })();
    }

    $(function () {
        // ensure that ContextMenuRoute init one time.
        if (typeof (ContextMenuRoute) != "undefined") {
            if (flagPopupLoadContext) {
                ContextMenuRoute.initContext();
                flagPopupLoadContext = false;
                setTimeout(function(){ flagPopupLoadContext = true }, 300);
            }
        }
    });
</script>