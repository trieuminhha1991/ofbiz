<script type="text/javascript">
    var localData = [];
    <#if listTrip?exists && listTrip?size &gt; 0>
        <#list listTrip as item>
        var lineItem = {};
        lineItem.tripId = '${item.tripId?if_exists}';
        lineItem.scLogId = '${item.scLogId?if_exists}';
        lineItem.contractorId = '${item.contractorId?if_exists}';
        lineItem.vehicleId = '${item.vehicleId?if_exists}';
        lineItem.driverId = '${item.driverId?if_exists}';
        lineItem.statusId = "${item.statusId?if_exists}";
        lineItem.description = "${item.description?if_exists}";
        lineItem.totalWeight = "${item.totalWeight?if_exists}";

        localData.push(lineItem);
        </#list>
    </#if>

    var cellclass = function (row, columnfield, value) {
        var data = $('#jqxEditSO').jqxGrid('getrowdata', row);
        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
            return 'background-promo';
        }
    }
</script>

<div id="containerEditSO"
     style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationEditSO" style="margin-bottom:5px">
    <div id="notificationEditSO"></div>
</div>

<div class="row-fluid">
    <div id="jqxEditSO"></div>
</div>

<div class="row-fluid margin-between-block">
    <div class="pull-right form-window-content-custom">
        <button id="confirmDialog" class='btn btn-primary form-action-button'><i
                class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
        <button id="alterCancelEdit" class='btn btn-danger form-action-button'><i
                class='fa-remove'></i> ${uiLabelMap.BSExit}</button>
    </div>
</div>

<div style="position:relative">
    <div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
        <div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div>
                <div class="jqx-grid-load"></div>
                <span>${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>

<div id='contextMenu'>
    <ul>
        <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
        <li><i class="fa fa-remove"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}</li>
    </ul>
</div>
<div id="windowEditContactMech" style="display:none">
    <div>${uiLabelMap.BSConfirmOrder}</div>
    <div class='form-window-container'>
        <div class='form-window-content'>
            <div id="windowEditContactMechContainer"></div>
        </div>
        <div class="form-action">
            <div class="pull-right form-window-content-custom">
                <button id="we_alterSave" class='btn btn-primary form-action-button'><i
                        class='fa-check'></i> ${uiLabelMap.BSConfirmation}</button>
            </div>
        </div>
    </div>
</div>

<#include "script/editTripScript.ftl"/>
