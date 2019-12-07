<div id="alterpopupWindow" style="display: none;">
    <div>${uiLabelMap.BPOAddSupplierTargets}</div>
    <div style="overflow: hidden;">
        <div id="formAdd" class="form-horizontal">
            <div class="row-fluid">
                <div class="span6">
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span4 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.ProductSupplier}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="supplierId"></div>
                        </div>
                    </div>
                </div>
                <div class="span6">
                    <div class="row-fluid" style="margin-top:10px;">
                        <div class="span2 div-inline-block">
                            <label class="asterisk" style="text-align:right; margin-top: 3px;"> ${uiLabelMap.BDVehicle}</label>
                        </div>
                        <div class="span8 div-inline-block">
                            <div id="vehicleId">
                                <div id="vehicleGrid">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <hr style="margin: 5px !important;"/>
            <div class="control-group no-left-margin" style="float:right">
                <button class="btn btn-danger form-action-button pull-right" id="alterCancel"><i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonCancel}</button>
                <button class="btn btn-primary form-action-button pull-right" id="alterSave"><i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
            </div>
        </div>
    </div>
</div>

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
<script type="text/javascript" src="/deliresources/js/suppliervehicle/addSupplierVehicle.js"></script>
