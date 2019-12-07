<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.AreYouSureCreate = "${uiLabelMap.AreYouSureCreate}";
    uiLabelMap.BSYouNotYetChooseCustomer = "${uiLabelMap.BSYouNotYetChooseCustomer}";
    uiLabelMap.HasErrorWhenProcess = "${uiLabelMap.HasErrorWhenProcess}";

</script>
<script type="text/javascript" >
    $(function(){
        OlbMTEditSalesmanObj.init();
    });
    var OlbMTEditSalesmanObj = (function() {
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
                    var resultValidate = !OlbMTEditSalesmanInfoObj.getValidator().validate();
                    if(resultValidate) return false;

                    var customers = OlbMTEditSalesmanCustomersObj.getCustomers();
                    if (customers.length <= 0){
                        jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseCustomer);
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
            var rowindex = $("#jqxGridSalesman").jqxGrid('getselectedrowindex');
            var rowData = $("#jqxGridSalesman").jqxGrid("getrowdata", rowindex);
            if (OlbCore.isNotEmpty(rowData)){
                $("#salesmanConfirm").text(rowData.fullName);
            } else {
                $("#salesmanConfirm").text("");
            }

            var rowindexSup = $("#jqxGridSalessup").jqxGrid('getselectedrowindex');
            var rowDataSup = $("#jqxGridSalessup").jqxGrid("getrowdata", rowindexSup);
            if (OlbCore.isNotEmpty(rowDataSup)){
                $("#salessupConfirm").text(rowDataSup.fullName);
            } else {
                $("#salessupConfirm").text("");
            }

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
                salesmanId: OlbMTEditSalesmanInfoObj.getSalesmanDDB().getValue(),
                salessupId: OlbMTEditSalesmanInfoObj.getSalessupDDB().getValue(),
                customerIds: customerIdStr,
            };
            $.ajax({
                type: 'POST',
                url: 'updateSalessupSalesmanMTCustomer',
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
                    viewChangeSalesmanMTCustomer();
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

        function viewChangeSalesmanMTCustomer(){
            window.location.href = 'MTCustomerChangeSalesman';
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
</script>