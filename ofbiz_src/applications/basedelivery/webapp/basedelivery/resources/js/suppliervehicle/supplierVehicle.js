$(function(){
    ListVehicleObj.init();
});
var ListVehicleObj = (function(){
    var init = function(){
        initInputs();
        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var initInputs = function (){
        if ($("#TransferMenu").length > 0){
            $("#TransferMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: theme});
        }
    };
    var initElementComplex = function (){
    };
    var initEvents = function (){
        $("#TransferMenu").on('itemclick', function (event) {
            var data = $('#jqxgridTransfer').jqxGrid('getRowData', $("#jqxgridTransfer").jqxGrid('selectedrowindexes'));
            var tmpStr = $.trim($(args).text());
            if(tmpStr == uiLabelMap.ViewDetailInNewPage){
                if (acc == true) {
                    window.open("accViewDetailTransfer?transferId=" + data.transferId, '_blank');
                } else {
                    window.open("viewDetailTransfer?transferId=" + data.transferId, '_blank');
                }
            } else if(tmpStr == uiLabelMap.BSViewDetail){
                if (acc == true) {
                    window.location.href = "accViewDetailTransfer?transferId=" + data.transferId;
                } else {
                    window.location.href = "viewDetailTransfer?transferId=" + data.transferId;
                }
            } else if (tmpStr == uiLabelMap.BSRefresh){
                $('#jqxgridTransfer').jqxGrid('updatebounddata');
            }
        });
    };
    var initValidateForm = function (){
    };

    var prepareCreate = function prepareCreate(uri){
        href = uri;
        window.location.href = href;
    };

    return {
        init: init,
        prepareCreate: prepareCreate,
    };
}());