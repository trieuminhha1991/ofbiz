var RecruitmentPlanDetailObj = (function(){
    var _isUpdateRecruitCostItemType = false;
    var _recruitCostItemTypeSelected = "";
    var init = function(){
        initJqxGrid();
        initJqxInput();
        initJqxDropDownList();
        initJqxValidator();
        initEvent();
        initJqxWindow();
    };

    var initJqxInput = function(){

    };

    var initJqxDropDownList = function(){
    };

    var initJqxGrid = function(){

    };

    var initJqxWindow = function(){
        createJqxWindow($("#RecruitmentPlanDetail"), 550, 520);
    };

    var initEvent = function(){

    };

    var createRecruitCostItemType = function(){

    };

    var openWindow = function(){
        openJqxWindow($("#RecruitmentPlanDetail"));
    };

    var initJqxValidator = function(){

    };
    return{
        init: init,
        openWindow: openWindow
    }
}());
$(document).ready(function(){
    RecruitmentPlanDetailObj.init();
});
