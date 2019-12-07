$(function () {
    OlbListDeliveryClusterObj.init();
});
var OlbListDeliveryClusterObj = (function () {
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
    };
    var initInputs = function () {
    };
    var initElementComplex = function () {
    };
    var initEvents = function () {
    };
    var openAddNewDeliveryCluster = function () {
        var url = 'createNewCluster';
        var win = window.open(url, "_blank");
        win.focus();
    };
    return {
        init: init,
        openAddNewDeliveryCluster: openAddNewDeliveryCluster,
    }
}());