$(function(){
    ListTripObj.init();
});
var ListTripObj = (function(){
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
    var getLocalization = function getLocalization() {
        var localizationobj = {};
        localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
        localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
        localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
        localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
        localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
        localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
        localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
        localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
        localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
        localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
        localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
        localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
        localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
        localizationobj.todaystring = uiLabelMap.wgtodaystring;
        localizationobj.clearstring = uiLabelMap.wgclearstring;
        return localizationobj;
    };

    var showDetailTransfer = function showDetailTransfer(value){
        href = "viewDetailTransfer?transferId="+value;
        window.location.href = href;
    }

    var prepareCreate = function prepareCreate(uri){
        href = uri;
        window.location.href = href;
    }

    return {
        init: init,
        showDetailTransfer: showDetailTransfer,
        getLocalization: getLocalization,
        prepareCreate: prepareCreate,
    };
}());