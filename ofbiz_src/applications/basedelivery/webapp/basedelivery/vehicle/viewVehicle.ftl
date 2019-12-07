<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<script src="/deliresources/js/vehicle/createVehicle.js?v=0.0.1"></script>
<script>

    var cellclassname = function (row, column, value, data) {
        if (column == 'quantityPicked') {
            return 'background-prepare';
        }
    }
</script>
<h4 class="smaller lighter blue"
    style="margin: 5px 0px 10px 10px !important;font-weight:500;line-height:20px;font-size:18px;">
${StringUtil.wrapString(uiLabelMap.CreateNew)} ${StringUtil.wrapString(uiLabelMap.BDVehicle)
?lower_case}
</h4>

<div id="stocking-form">
    <div class="form-window-content">
        <#--<div class="row-fluid">-->
            <#--<div class="span10">-->
                <#--<div class="row-fluid margin-top10">-->
                    <#--<div class="span4"><label class="text-right asterisk">${StringUtil.wrapString-->
                    <#--(uiLabelMap.BLDriverId)}</label></div>-->
                    <#--<div class="span8">-->
                        <#--<div id="txtPickingFacility">-->
                            <#--<div id="jqxgridPickingFacility"></div>-->
                        <#--</div>-->
                    <#--</div>-->
                <#--</div>-->
            <#--</div>-->
        <#--</div>-->

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right asterisk">${StringUtil.wrapString
                    (uiLabelMap.BDLoading)}</label></div>
                    <div class="span8">
                        <input id="loading" value=""/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right asterisk">${StringUtil.wrapString
                    (uiLabelMap.BDVolume)}</label></div>
                    <div class="span8">
                        <input id="volume" value=""/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right asterisk">${StringUtil.wrapString
                    (uiLabelMap.BDReqNo)}</label></div>
                    <div class="span8">
                        <input id="reqNo" value=""/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right asterisk">${StringUtil.wrapString
                    (uiLabelMap.BDLicensePlate)}</label></div>
                    <div class="span8">
                        <input id="licensePlate" value=""/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right asterisk">${StringUtil.wrapString
                    (uiLabelMap.BDVehicleType)}</label></div>
                    <div class="span8">
                        <input id="vehicleType" value=""/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4"><label class="text-right">${StringUtil.wrapString
                    (uiLabelMap.BDDescription)}</label></div>
                    <div class="span8">
                        <input id="description" value=""/>
                    </div>
                </div>
            </div>
        </div>
        <div class="row-fluid">
            <div class="span10">
                <div class="row-fluid margin-top10">
                    <div class="span4">
                    </div>
                    <div class="span8">
                        <button id="btnUpload" type="button"
                                class="margin-top10 btn btn-primary form-action-button">
                        ${StringUtil.wrapString(uiLabelMap.CommonSelect)}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row-fluid margin-top10">
    <div class="span12">
    <#if hasOlbPermission("MODULE", "LOG_PICKLIST", "UPDATE")>
        <button id="btnCommit" type="button"
                class="btn btn-primary form-action-button pull-right hidden">
            <i class="fa fa-check"></i>${uiLabelMap.POCommonOK}
        </button>
        <#if !parameters.picklistId?exists>
            <button id="btnDelete" type="button"
                    class="btn btn-danger form-action-button pull-right hidden">
                <i class="fa fa-remove"></i>${uiLabelMap.DmsDeleteAll}
            </button>
        </#if>
    </#if>
    </div>
</div>

<div id="jqxNotification">
    <div id="notificationContent"></div>
</div>

<script>
    multiLang = _.extend(multiLang, {
        SequenceId: "${StringUtil.wrapString(uiLabelMap.SequenceId)}",
    });
    const picklistId = "${(parameters.picklistId)?if_exists}";
    const orderIds = "${(parameters.orderIds)?if_exists}";
    $(document).ready(function () {
        CreatePicklist.init();
        if (picklistId) {
            $($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.CommonUpdate)}");
            CreatePicklist.Update = true;
            LoadByPicklist.load(picklistId);
        }
        if (orderIds) {
            LoadByOrderIds.load("${(facility.facilityId)?if_exists}", "${(facility.facilityName)?if_exists}", orderIds.split(","));
        }
    });
</script>