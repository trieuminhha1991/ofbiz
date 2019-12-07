<div id="jqxNotificationSequenceNum">
    <div id="notificationContentSequenceNum">
    </div>
</div>

<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.wgupdateerror)}";

    if (typeof (OlbCustomerSequenceNum) == "undefined") {
        var OlbCustomerSequenceNum = (function($) {
            var _routeId = null;
            var openPopup = function (row) {
                var rowData = $('#ListRoute').jqxGrid('getrowdata', row);
                _routeId = rowData.routeId;

                bootbox.dialog("${uiLabelMap.BSToDoGenCustomerSequenceNum}"+" " +_routeId +"?",
                    [{"label": '${uiLabelMap.wgcancel}',
                        "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                        "callback": function() {bootbox.hideAll();}
                    },
                    {"label": '${uiLabelMap.wgok}',
                        "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                        "callback": function() {
                            OlbCustomerSequenceNum.update();
                        }
                    }]
                );
            };

            var update = function() {
                if (OlbCore.isNotEmpty(_routeId)) {
                    $.ajax({
                        type: 'POST',
                        url: 'generateCustomerSequenceOfRoute',
                        data: {routeId: _routeId},
                        beforeSend: function(){
                            openLoader();
                        },
                        success: function(data){
                            OlbCustomerSequenceNum.notify(data);
                        },
                        error: function(data){
                            alert("Send request is error");
                        },
                        complete: function(data){
                            closeLoader();
                        },
                    });
                }
            };
            var notify = function(res) {
                $("#jqxNotificationSequenceNum").jqxNotification("closeLast");
                if(res["message"]){
                    $("#jqxNotificationSequenceNum").jqxNotification({ template: "error"});
                    $("#notificationContentSequenceNum").text(uiLabelMap.updateError);
                    $("#jqxNotificationSequenceNum").jqxNotification("open");
                }else {
                    $("#jqxNotificationSequenceNum").jqxNotification({ template: "info"});
                    $("#notificationContentSequenceNum").text(uiLabelMap.wgcreatesuccess);
                    $("#jqxNotificationSequenceNum").jqxNotification("open");
                }
            };
            return {
                openPopup: openPopup,
                update: update,
                notify: notify
            };
        })(jQuery);
    }
</script>