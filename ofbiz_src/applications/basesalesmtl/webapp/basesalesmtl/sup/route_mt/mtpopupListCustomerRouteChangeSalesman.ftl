<div id="jqxwindowListCustChangSalemanMT" class='hide'>
    <div>${uiLabelMap.BSListCustomerNotInSaleman}</div>
    <div style="overflow: hidden;">
        <div class='form-window-content' style="height: 380px;">
            <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BSListCustomerMT}:</div> <div class="customerInfo jqxwindowTitle" style="display: inline-block;"></div></div>

            <div id="jqxgridViewListRouteCustChangeSalesman"></div>
        </div>
        <div class="form-action">
            <button id="cancelViewListCustChangeSalesman" class='btn btn-danger form-action-button pull-right' title='${uiLabelMap.CommonClose}'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
            <button id="disagreeAndContinueBT" class='btn btn-info form-action-button pull-right' title='${uiLabelMap.BSUpdateSalemanRoute}'></i> ${uiLabelMap.BSDisagreeAndContinueBT}</button>
            <button id="okAndContinueBT" class='btn btn-success  form-action-button pull-right' title ='${uiLabelMap.BSUpdateSaleManRouteAndPartyMT}'> ${uiLabelMap.BSOkAndContinueBT}</button>
        </div>
    </div>
</div>

<div id="jqxNotificationCust">
    <div id="notificationContentCust">
    </div>
</div>

<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BSAgentId = '${StringUtil.wrapString(uiLabelMap.BSAgentId)}';
    uiLabelMap.BSAgentName = '${StringUtil.wrapString(uiLabelMap.BSAgentName)}';
    uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
    uiLabelMap.BSCustomerName = '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}';
    uiLabelMap.BSDistributorId = '${StringUtil.wrapString(uiLabelMap.BSDistributorId)}';
    uiLabelMap.BSSalesmanCode = '${StringUtil.wrapString(uiLabelMap.BSSalesmanCode)}';
    uiLabelMap.BSSalesmanName = '${StringUtil.wrapString(uiLabelMap.BSSalesmanName)}';
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

<script type="text/javascript" src="/salesmtlresources/js/sup/listCustomerRouteChangeSalemanMT.js?v=1.0.1"></script>