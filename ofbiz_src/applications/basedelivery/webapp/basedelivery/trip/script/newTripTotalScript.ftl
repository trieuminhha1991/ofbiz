<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>

<@jqOlbCoreLib hasGrid=true hasValidator=true/>
<script type="text/javascript">
    if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
    uiLabelMap.BDYouNotYetChooseRecord = "${uiLabelMap.BDYouNotYetChooseRecord }?";
    uiLabelMap.BSAreYouSureYouWantToCreate = "${uiLabelMap.BSAreYouSureYouWantToCreate}";
    uiLabelMap.BSYouNotYetChooseProduct = "${uiLabelMap.BSYouNotYetChooseProduct}!";
    uiLabelMap.BSExistProductHaveNotPriceIs = "${uiLabelMap.BSExistProductHaveNotPriceIs}";
    uiLabelMap.BDPartyId = "${uiLabelMap.BDPartyId}";
    uiLabelMap.BDGroupName = "${uiLabelMap.BDGroupName}";
    uiLabelMap.BDVehicleId = "${StringUtil.wrapString(uiLabelMap.BDVehicleId)}";
    uiLabelMap.BDLicensePlate = "${StringUtil.wrapString(uiLabelMap.BDLicensePlate)}";

</script>
<script type="text/javascript" src="/deliresources/js/trip/newTripTotal.js"></script>
