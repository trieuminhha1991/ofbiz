<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownList=true hasValidator=true/>

<script type="text/javascript">
    <#assign idGridJQ = "jqxEditSO"/>
    <#assign addType = "popup"/>
    <#assign alternativeAddPopup="alterpopupWindow"/>
    function renderJqxTitle(){
    <#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
        <@renderJqxTitle titlePropertyTmp=titleProperty id=idGridJQ/>
        return jqxheader;
    <#elseif customTitleProperties?exists && customTitleProperties != "">
        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=idGridJQ/>
        return jqxheader;
    </#if>
        return "";
    }
    var dataCreateAddRowButton = null;
    var addType = "${addType}";
    <#if addType != "popup">
        <#if addinitvalue !="">
        dataCreateAddRowButton = {${primaryColumn}: '${addinitvalue}'}
        <#else>
        dataCreateAddRowButton = ${primaryColumn}
        </#if>
    </#if>

    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BDVehicleId = "${StringUtil.wrapString(uiLabelMap.BDVehicleId)}";
    uiLabelMap.BDLoading = "${StringUtil.wrapString(uiLabelMap.BDLoading)}";
    uiLabelMap.BDLicensePlate = "${StringUtil.wrapString(uiLabelMap.BDLicensePlate)}";
    uiLabelMap.BDVolume = "${StringUtil.wrapString(uiLabelMap.BDVolume)}";
    uiLabelMap.BDReqNo = "${StringUtil.wrapString(uiLabelMap.BDReqNo)}";
    uiLabelMap.BDVehicleType = "${StringUtil.wrapString(uiLabelMap.BDVehicleType)}";
    uiLabelMap.BDDescription = "${StringUtil.wrapString(uiLabelMap.BDDescription)}";
    uiLabelMap.BDLongitude= "${StringUtil.wrapString(uiLabelMap.BDLongitude)}";
    uiLabelMap.BDWidth = "${StringUtil.wrapString(uiLabelMap.BDWidth)}";
    uiLabelMap.BDHeight = "${StringUtil.wrapString(uiLabelMap.BDHeight)}";
    uiLabelMap.Ton= "${StringUtil.wrapString(uiLabelMap.Ton)}";
    uiLabelMap.M3= "${StringUtil.wrapString(uiLabelMap.M3)}";


    uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}!";
    uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
    uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen = "${StringUtil.wrapString(uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen)}";
    uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem)}";
    uiLabelMap.AreYouSureEdit = "${StringUtil.wrapString(uiLabelMap.AreYouSureEdit)}";

    uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}!";
    uiLabelMap.BSDeleteOrderItem = "${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}";

    var idGridJQ = "${idGridJQ}";
    var alternativeAddPopup = <#if alternativeAddPopup?exists>"${alternativeAddPopup}"<#else>""</#if>

    jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
</script>
<script type="text/javascript" src="/deliresources/js/vehicle/editVehicle.js"></script>