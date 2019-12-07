<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.combobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<@jqOlbCoreLib hasValidator=true/>
<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<#assign tripId = trip.tripId>
<#assign driverId = trip.driverId?if_exists>
<script type="text/javascript">
    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BDYouNotYetChooseRecord = "${uiLabelMap.BDYouNotYetChooseRecord }?";
    uiLabelMap.BSAreYouSureYouWantToApprove = "${uiLabelMap.BSAreYouSureYouWantToApprove}";
    uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}!";
    uiLabelMap.BSExistProductHaveNotPriceIs = "${uiLabelMap.BSExistProductHaveNotPriceIs}";
    uiLabelMap.BDPartyId = "${uiLabelMap.BDPartyId}";
    uiLabelMap.BDGroupName = "${uiLabelMap.BDGroupName}";
    uiLabelMap.CommonSubmit = "${uiLabelMap.CommonSubmit}";
    uiLabelMap.BDVehicleId = "${StringUtil.wrapString(uiLabelMap.BDVehicleId)}";
    uiLabelMap.BDLicensePlate = "${StringUtil.wrapString(uiLabelMap.BDLicensePlate)}";
    uiLabelMap.fieldRequired = "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}";
    uiLabelMap.AreYouSureAccept = "${uiLabelMap.AreYouSureAccept}";
    uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
    uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
</script>

<script type="text/javascript">
    var reasonCancel = [
        {	enumId: "",
            descriptionSearch: "${StringUtil.wrapString(uiLabelMap.OtherReason)}"
        },
    <#if orderCancelReason?exists>
        <#list orderCancelReason as reasonItem>
            {	enumId: "${reasonItem.enumId}",
                descriptionSearch: "[${reasonItem.enumCode?if_exists}] ${StringUtil.wrapString(reasonItem.get("description", locale))}"
            },
        </#list>
    </#if>
    ];
    var tripId = "${StringUtil.wrapString(tripId)}";
    var driverId = "";
    <#if trip.driverId?exists>
    driverId ="${StringUtil.wrapString(trip.driverId)}";
    </#if>

    var supplierId = "${StringUtil.wrapString(trip.contractorId?if_exists)}";
    var vehicleId ="${StringUtil.wrapString(trip.vehicleId?if_exists)}";
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.OtherReason = "${StringUtil.wrapString(uiLabelMap.OtherReason)}";
    uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";

    function changeApproveOrderStatus(orderId, workEffortId, partyId, roleTypeId, fromDate, ntfId ){
        bootbox.dialog(uiLabelMap.AreYouSureApprove,
                [{"label": uiLabelMap.Cancel,
                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                    "callback": function() {bootbox.hideAll();}
                },
                    {"label": uiLabelMap.OK,
                        "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                        "callback": function() {
                            Loading.show('loadingMacro');
                            setTimeout(function(){
                                jQuery.ajax({
                                    url: "changeOrderStatus",
                                    type: "POST",
                                    async: false,
                                    data: {
                                        orderId: orderId,
                                        statusId: "ORDER_APPROVED",
                                        newStatusId : "ORDER_APPROVED",
                                        setItemStatus : "Y",
                                        workEffortId : workEffortId,
                                        partyId : partyId,
                                        roleTypeId : roleTypeId,
                                        fromDate : fromDate,
                                        ntfId : ntfId
                                    },
                                    success: function(res) {
                                        window.location.href = "viewOrder?orderId="+orderId+"&activeTab=orderoverview-tab";
                                        $("#notifyId").jqxNotification("open");
                                        if ($("#jqxgrid")){
                                            $("#approveOrderId").show();
                                        }
                                    }
                                });
                                Loading.hide('loadingMacro');
                            }, 500);
                        }
                    }]);
    }
</script>
<div id="confirmOrderChangeStatus" style="display:none">
    <div>${uiLabelMap.BSCancelOrder}</div>
    <div class='form-window-container'>
        <div class='form-window-content'>
            <div id="containerOrderChangeStatus" style="background-color: transparent; overflow: auto;"></div>
            <div id="jqxNotificationOrderChangeStatus" style="margin-bottom:5px">
                <div id="notificationOrderChangeStatus">
                </div>
            </div>
            <div class="row-fluid">
                <div class="span12 form-window-content-custom">
                    <div class='row-fluid'>
                        <div class='span3'>
                            <label for="wcos_changeReason" class="required">${uiLabelMap.BSReasonCancel}</label>
                        </div>
                        <div class='span9'>
                            <div id="wcos_changeReason"></div>
                        </div>
                    </div>
                    <div class='row-fluid'>
                        <div class='span3'>
                            <label for="wcos_changeDescription" class="required">${uiLabelMap.OtherReason}</label>
                        </div>
                        <div class='span9'>
                            <textarea id="wcos_changeDescription" style="width: 95%"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class="pull-right form-window-content-custom">
                <button id="alterConfirmSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                <button id="alterConfirmCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript"
        src="/deliresources/js/trip/viewTrip.js"></script>