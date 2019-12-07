$(function(){
    OlbDeliveryClusterObj.init();
});
var OlbDeliveryClusterObj = (function() {
    var listShipmentItemFinish = new Array();
    var init = function() {
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function() {
    };
    var initElementComplex = function() {
    };
    var initEvents = function() {
        $('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
            if(info.step == 1 && (info.direction == "next")) {
                // check form valid
                $('#containerNotify').empty();
                var resultValidate = !OlbDeliveryClusterInfoObj.getValidator().validate();
                if(resultValidate) return false;

                var customers = OlbCustomerFilterObj.getCustomers();
                if (customers.length <= 0){
                    jOlbUtil.alert.error(uiLabelMap.BLYouNotYetChooseCustomer);
                    return false;
                }
                showConfirmPage(customers);
            }
        }).on('finished', function(e) {
            jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function() {
                Loading.show('loadingMacro');
                setTimeout(function(){
                    finishCreateDeliveryCluster();
                    Loading.hide('loadingMacro');
                }, 500);
            });
        }).on('stepclick', function(e){
            //prevent clicking on steps
        });
    };
    function showConfirmPage(listData){
        $("#deliveryClusterCodeConfirm").text($("#deliveryClusterCode").val());
        $("#deliveryClusterNameConfirm").text($("#deliveryClusterName").val());
        var rowindex = $("#jqxGridShipper").jqxGrid('getselectedrowindex');
        var rowData = $("#jqxGridShipper").jqxGrid("getrowdata", rowindex);
        if (OlbCore.isNotEmpty(rowData)){
            $("#shipperConfirm").text(rowData.fullName);
        } else {
            $("#shipperConfirm").text("");
        }
        $("#descriptionConfirm").text($("#description").val());
        var tmpSource = $("#jqxgridCustomerSelected").jqxGrid('source');
        if(typeof(tmpSource) != 'undefined'){
            tmpSource._source.localdata = listData;
            $("#jqxgridCustomerSelected").jqxGrid('source', tmpSource);
        }
    }

    function finishCreateDeliveryCluster(){
        var customerRows = $("#jqxgridCustomerSelected").jqxGrid("getrows");
        var customerIds = [];
        var customerIdStr = "";
        $.each(customerRows, function (i, v) {
            if (OlbCore.isNotEmpty(v.partyId)){
                customerIds.push(v.partyId); //partyId is customerId
            }
        });
        customerIdStr = customerIds.join(",");
        var dataMap = {};
        dataMap = {
            deliveryClusterCode: $("#deliveryClusterCode").val(),
            deliveryClusterName: $("#deliveryClusterName").val(),
            description: $("#description").val(),
            shipperId: OlbDeliveryClusterInfoObj.getShipperDDB().getValue(),
            customerIds: customerIdStr,
        };
        $.ajax({
            type: 'POST',
            url: 'createNewDeliveryCluster',
            async: false,
            data: dataMap,
            beforeSend: function(){
                $("#btnPrevWizard").addClass("disabled");
                $("#btnNextWizard").addClass("disabled");
                $("#loader_page_common").show();
            },
            success: function(data){
                if (data._ERROR_MESSAGE_) {
                    jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
                    return;
                }
                viewDeliveryClusterDetail(data.deliveryClusterId);
            },
            error: function(data){
                alert("Send request is error");
            },
            complete: function(data){
                $("#loader_page_common").hide();
                $("#btnPrevWizard").removeClass("disabled");
                $("#btnNextWizard").removeClass("disabled");
            },
        });
    }

    /*this feature in the future*/
    function viewDeliveryClusterDetail(deliveryClusterId){
        window.location.href = 'deliveryClusterDetail?deliveryClusterId=' + deliveryClusterId;
    }
    var initValidateForm = function(){
    };
    var reloadPages = function (){
        window.location.reload();
    };
    return {
        init: init,
        reloadPages: reloadPages,
    }
}());