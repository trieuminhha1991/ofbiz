if (typeof (OlbWizardUploadSalesStatement) == "undefined") {
    var OlbWizardUploadSalesStatement = (function(){
        var init = function(){
            $('#wizardAddNew').ace_wizard().on('change' , function(e, info){
                if(info.step == 1 && (info.direction == "next")) {
                    if(!addSalesStatementSheetDetailObj.validate()){
                        return false;
                    }
                    setTimeout(function(){
                        addSalesStatementSheetDetailObj.prepareSourceFileData();
                    }, 5);
                }else if(info.step == 2 && (info.direction == "next")){
                    setTimeout(function(){
                        addSalesStatementSheetDetailObj.prepareJoinColumnData();
                    }, 5);
                }
            }).on('finished', function(e) {
                bootbox.dialog(uiLabelMap.ConfirmCreateFileSalesStatementSheetDetail,
                    [{
                        "label" : uiLabelMap.CommonSubmit,
                        "class" : "btn-primary btn-small icon-ok open-sans",
                        "callback": function() {
                            createSalesStatementDetail();
                        }
                    },
                        {
                            "label" : uiLabelMap.CommonClose,
                            "class" : "btn-danger btn-small icon-remove open-sans",
                        }]
                );
            }).on('stepclick', function(e, info){

            });
            initJqxWindow();
            create_spinner($("#spinnerCreateNew"));
        };
        var createSalesStatementDetail = function(){
            var tabActiveCurrent = $("#tabActiveDefault").val();
            var jqxGridId = "jqxSalesStatement";
            var dataRow = $("#" + jqxGridId).jqxGrid("getboundrows");
            var salesStatementId = OlbCRUDSalesStatement.getSalesStatementId();
            var dataList = [];
            if (typeof(dataRow) != 'undefined') {
                dataRow.forEach(function (dataItem) {
                    if (dataItem != window) {
                        var itemMap = dataItem;
                        itemMap.internalName = "";
                        itemMap.features = "";
                        dataList.push(itemMap);
                    }
                });
            }

            var fileData = addSalesStatementSheetDetailObj.getData();
            //var dataSubmit = $.extend({}, generalData, fileData);
            var dataSubmit = $.extend({}, fileData);
            var formData = new FormData();

            $("#upLoadFileForm").find('input[type=file]').each(function(){
                var field_name = $(this).attr('name');
                //for fields with "multiple" file support
                //field name should be something like `myfile[]`
                var files = $(this).data('ace_input_files');
                if(files && files.length > 0) {
                    for(var f = 0; f < files.length; f++) {
                        formData.append(field_name, files[f]);
                    }
                }
            });
            //var salesStatementId = $("#salesStatementId_" + tabActiveCurrent).val();
            for(var key in dataSubmit){
                formData.append(key, dataSubmit[key]);
            }
            formData.append("salesStatementId", salesStatementId);
            $("#btnPrev").attr("disabled", "disabled");
            $("#btnNext").attr("disabled", "disabled");
            $("#loadingCreateNew").show();
            $.ajax({
                url: 'createSalesStatementDetailByExcel',
                data: formData,
                type: 'POST',
                cache : false,
                contentType : false,
                processData : false,
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, "default", function(){
                            $('#container').empty();
                            $('#jqxNotification').jqxNotification({ template: 'info'});
                            $("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
                            $("#jqxNotification").jqxNotification("open");

                            window.location.href = 'viewSalesStatementDetail?salesStatementId=' + salesStatementId;
                        }
                    );
                },
                error: function(data){
                    alert("Send request is error");
                },
            });
        };
        var resetStep = function(){
            $('#wizardAddNew').wizard('previous');
            $('#wizardAddNew').wizard('previous');
        };
        var initJqxWindow = function(){
            $("#UploadFileSalesStatementEdited").on('close', function(event){
                resetStep();
                addSalesStatementSheetDetailObj.resetData();
                addSalesStatementSheetDetailObj.hideValidate();
            });
        };
        return{
            init: init
        }
    }());
}

$(document).ready(function(){
    OlbWizardUploadSalesStatement.init();
    addSalesStatementSheetDetailObj.init();//addSalesStatementSheetDetailObj is defined in AddNewSalesStatementDetailExcelFile.js
});