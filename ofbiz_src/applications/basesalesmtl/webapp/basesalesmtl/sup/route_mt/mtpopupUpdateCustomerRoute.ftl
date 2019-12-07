<div id="jqxwindowListCustToRoute" class='hide'>
    <div>${uiLabelMap.BSListCustomer}</div>
    <div style="overflow: hidden;">
        <div class='form-window-content' style="height: 380px;">
            <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BSRoute}:</div> <div class="customerInfo jqxwindowTitle" style="display: inline-block;"></div></div>
            <div class="pull-right margin-bottom10">
                <a href='javascript:void(0)' onclick='OlbAddCustRoute.open()'><i class='icon-plus open-sans'></i>${uiLabelMap.accAddNewRow}</a>
                &nbsp;&nbsp;&nbsp;<a href='javascript:void(0)' onclick='OlbCustomerRoute._delete()' class='red'><i class='icon-trash open-sans'></i>${uiLabelMap.DmsDelete}</a>
            </div>
            <div id="jqxgridViewListCust"></div>
        </div>
        <div class="form-action">
            <button id="cancelViewListCust" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>


<div id="jqxwindowAddCust" class='hide'>
    <div>${uiLabelMap.BSListCustomer}</div>
    <div style="overflow: hidden;">
        <div class='form-window-content' style="height: 380px;">
            <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BSAddMTCustomerToRoute}:</div> <div class="customerInfo jqxwindowTitle"></div></div>
            <div id="jqxgridAddCust"></div>
        </div>
        <div class="form-action">
            <button id="cancelAddCust" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
            <button id="saveListCust" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
        </div>
    </div>
</div>

<#--<div id="jqxNotificationCust">-->
    <#--<div id="notificationContentCust">-->
    <#--</div>-->
<#--</div>-->

<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BSAgentId = '${StringUtil.wrapString(uiLabelMap.BSAgentId)}';
    uiLabelMap.BSAgentName = '${StringUtil.wrapString(uiLabelMap.BSAgentName)}';
    uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
    uiLabelMap.BSCustomerName = '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}';
    uiLabelMap.BSDistributorId = '${StringUtil.wrapString(uiLabelMap.BSDistributorId)}';
    uiLabelMap.BSSalesmanCode = '${StringUtil.wrapString(uiLabelMap.BSSalesmanCode)}';
    uiLabelMap.BSSequenceIdCustomer = '${StringUtil.wrapString(uiLabelMap.BSSequenceIdCustomer)}';
    uiLabelMap.ValueMustBeGreateThanZero = '${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}';
    uiLabelMap.ConfirmDelete = "${StringUtil.wrapString(uiLabelMap.ConfirmDelete)}";
    uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
    uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
    uiLabelMap.BSNotCustomerSelected = "${StringUtil.wrapString(uiLabelMap.BSNotCustomerSelected)}";
    uiLabelMap.BSLocation = "${StringUtil.wrapString(uiLabelMap.BSLocation)}";
    uiLabelMap.BSUpdateLocation = "${StringUtil.wrapString(uiLabelMap.BSUpdateLocation)}";
    uiLabelMap.BSUpdateLocationCustomer = "${StringUtil.wrapString(uiLabelMap.BSUpdateLocationCustomer)}";
    uiLabelMap.BSAddress = "${StringUtil.wrapString(uiLabelMap.BSAddress)}";
    uiLabelMap.BSVietNam = "${StringUtil.wrapString(uiLabelMap.BSVietNam)}";

    uiLabelMap.addSuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
    uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.wgupdateerror)}";
    uiLabelMap.wgcreatesuccess = "${StringUtil.wrapString(uiLabelMap.wgcreatesuccess)}";
    uiLabelMap.wgdeletesuccess = "${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}";
</script>

<script type="text/javascript" src="/salesmtlresources/js/sup/updateCustomerRouteMT.js?v=1.0.1"></script>