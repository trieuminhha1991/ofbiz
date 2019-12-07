<div id="jqxNotificationSchedule">
    <div id="notificationContentSchedule">
    </div>
</div>

<div id="popupWindowGenSchedule" class='hide'>
    <div>
    ${uiLabelMap.BSgenSaleRouteScheduleDetail}
    </div>
    <div class="form-window-container">
        <div class='form-window-content'>
            <div class='row-fluid'>
                <div class='row-fluid margin-bottom10'>
                    <div class='span5 text-algin-right'>
                        <span class="asterisk">${uiLabelMap.CommonFromDate}</span>
                    </div>
                    <div class="span7">
                        <div id="fromDateSchedule"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span5 text-algin-right'>
                        <span class="asterisk">${uiLabelMap.CommonThruDate}</span>
                    </div>
                    <div class="span7">
                        <div id="thruDateSchedule"></div>
                    </div>
                </div>
                <div class="form-action">
                    <button id="cancelGenSchedule" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="genSchedule" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.BSGenSchedule}</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.wgupdateerror)}";

    if (typeof (OlbGenSaleRouteSchedule) == "undefined") {
        var OlbGenSaleRouteSchedule = (function($) {
            var _routeId = null;
            var validatorVAL = null;
            var init = function () {
                initElement();
                initValidateForm();
                initEvent();
            };

            var initElement = function () {
                $("#popupWindowGenSchedule").jqxWindow({
                    width: 380,
                    height: 180,
                    resizable: true,
                    isModal: true,
                    autoOpen: false,
                    cancelButton: $("#cancelGenSchedule"),
                    modalOpacity: 0.7,
                    theme: theme
                });
                var now = new Date();
                now.setHours(0,0,0);
                jOlbUtil.dateTimeInput.create("#fromDateSchedule", {
                    width: '90%',
                    allowNullDate: false,
                    value: now,
                    showFooter: true,
                    formatString: 'dd/MM/yyyy'
                });
                var defaultThruDate = new Date();
                defaultThruDate.setHours(23,59,59);
                defaultThruDate.setDate(defaultThruDate.getDate() + 30); //30 ngay
                jOlbUtil.dateTimeInput.create("#thruDateSchedule", {
                    width: '90%',
                    allowNullDate: false,
                    value: defaultThruDate,
                    showFooter: true,
                    formatString: 'dd/MM/yyyy'
                });
            };

            var initEvent = function () {
                $('#genSchedule').click(function () {
                    if(!validatorVAL.validate()) return false;
                    OlbGenSaleRouteSchedule.update();
                });
                $('#popupWindowGenSchedule').on('close', function (event) {
                    validatorVAL.hide();
                });
            };
            var initValidateForm = function () {
                var extendRules = [
                ];
                var mapRules = [
                    {input: '#fromDateSchedule', type: 'validDateTimeInputNotNull'},
                    {input: '#fromDateSchedule', type: 'validDateCompareToday'},
                    {input: '#fromDateSchedule, #thruDateSchedule', type: 'validCompareTwoDate', paramId1 : "fromDateSchedule", paramId2 : "thruDateSchedule"},
                ];
                validatorVAL = new OlbValidator($('#popupWindowGenSchedule'), mapRules, extendRules, {position: 'right', scroll: true});
            };
            var openWindow = function (row) {
                var rowData = $('#ListRoute').jqxGrid('getrowdata', row);
                _routeId = rowData.routeId;
                $("#popupWindowGenSchedule").jqxWindow('open');
            };
            var closeWindow = function (row) {
                $("#popupWindowGenSchedule").jqxWindow('close');
            };

            var update = function() {
                var grid = $('#ListRoute');
                var dataMap = getData();
                $.ajax({
                    type: 'POST',
                    url: 'generateSaleRouteScheduleDetailDate',
                    data: dataMap,
                    beforeSend: function(){
                        openLoader();
                    },
                    success: function(data){
                        OlbGenSaleRouteSchedule.notify(data);
                    },
                    error: function(data){
                        alert("Send request is error");
                    },
                    complete: function(data){
                        closeLoader();
                    },
                });
                OlbGenSaleRouteSchedule.closeWindow();

            };
            var getData = function () {
                var data = {};
                data["routeId"] = _routeId;
                data["fromDate"] = $("#fromDateSchedule").jqxDateTimeInput('getDate').getTime();
                data["thruDate"] = $("#thruDateSchedule").jqxDateTimeInput('getDate').getTime();
                return data;
            };
            var notify = function(res) {
                $("#jqxNotificationSchedule").jqxNotification("closeLast");
                if(res["message"]){
                    $("#jqxNotificationSchedule").jqxNotification({ template: "error"});
                    $("#notificationContentSchedule").text(uiLabelMap.updateError);
                    $("#jqxNotificationSchedule").jqxNotification("open");
                }else {
                    $("#jqxNotificationSchedule").jqxNotification({ template: "info"});
                    $("#notificationContentSchedule").text(uiLabelMap.wgcreatesuccess);
                    $("#jqxNotificationSchedule").jqxNotification("open");
                }
            };
            return {
                init: init,
                update: update,
                getData: getData,
                openWindow: openWindow,
                closeWindow: closeWindow,
                notify: notify
            };
        })(jQuery);
    }

    $(function () {
        // ensure that OlbGenSaleRouteSchedule init one time.
        if (flagPopupLoadGenSchedule) {
            OlbGenSaleRouteSchedule.init();
            flagPopupLoadGenSchedule = false;
            setTimeout(function(){ flagPopupLoadGenSchedule = true }, 300);
        }
    });
</script>