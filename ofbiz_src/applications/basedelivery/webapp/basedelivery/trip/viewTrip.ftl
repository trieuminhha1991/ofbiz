<script src="/crmresources/js/generalUtils.js"></script>
<#include "script/viewTripScript.ftl"/>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
    $(function () {
        var $validation = false;
        $('#fuelux-wizard').ace_wizard().on('change', function (e, info) {
            return false;
        }).on('finished', function (e) {
            return false;
        }).on('stepclick', function (e) {
            return false;
        });
    <#if hasStepCustomerConfirmOrder?exists && hasStepCustomerConfirmOrder>
        $("#step3").css("min-width", "16%", "important");
    </#if>
    });
</script>

<script type="text/javascript">
    var printTripDetail = function () {
        var url = 'tripDetail.pdf?tripId=${trip.tripId}';
        var win = window.open(url, '_blank');
        win.focus();
    };
</script>

<#assign vehicleName = Static["com.olbius.basedelivery.vehicle.VehicleHelper"].getVehicleName(delegator, trip.vehicleId?if_exists)?if_exists>
<#assign contractorName = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, trip.contractorId?if_exists, true, true)?if_exists>
<#assign driverName = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, trip.driverId?if_exists, true, true)?if_exists>

<div class="row-fluid">
    <div class="span12">
        <div class="widget-box transparent" id="recent-box">
            <div class="widget-header" style="border-bottom:none">
                <div style="width:100%; border-bottom: 1px solid #c5d0dc">
                    <div class="row-fluid">
                        <div class="span10">
                            <div class="tabbable">
                                <ul class="nav nav-tabs" id="recent-tab">
                                    <li class="active">
                                        <a data-toggle="tab"
                                           href="#orderoverview-tab">${uiLabelMap.BSOverview}</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    <#if currentStatusId != "TRIP_CANCELLED">
                        <div class="span2" style="height:34px; text-align:right">

                            <a href="javascript: void(0);printTripDetail()"
                               data-rel="tooltip" title="${uiLabelMap.BDPrintTripDetail}"
                               data-placement="bottom" class="button-action"><i
                                    class="fa fa-file-pdf-o"></i></a>

                            <#if currentStatusId != "TRIP_CONFIRMED">

                                <a href="javascript:void(0)" id="tripApprove"
                                   data-rel="tooltip" title="${uiLabelMap.BSApproveAccept}"
                                   data-placement="bottom" class="button-action"><i
                                        class="fa fa-check"></i></a>
                            </#if>
                            <a href="javascript:changeOrderStatus('CANCEL')" data-rel="tooltip"
                               title="${uiLabelMap.BSCancel}" data-placement="bottom"
                               class="button-action"><i class="fa fa-times-circle-o"></i></a>
                            <form name="OrderCancel" method="post"
                                  action="<@ofbizUrl>changeTripStatus</@ofbizUrl>"
                                  style="position:absolute;">
                                <input type="hidden" name="statusId" value="TRIP_CANCELLED"/>
                                <input type="hidden" name="tripId" value="${trip.tripId}"/>
                                <input type="hidden" name="vehicleId"
                                       value="${trip.vehicleId?if_exists}"/>
                                <input type="hidden" name="driverId"
                                       value="${trip.driverId?if_exists}"/>
                                <input type="hidden" name="changeReason" value=""/>
                            </form>
                        </div>
                    </#if>
                    </div>
                </div>
            </div>
            <div class="widget-body" style="margin-top: -12px !important">
                <div class="widget-main padding-4">
                    <div id="notification">
                    </div>
                    <div id="container" style="width: 100%;"></div>
                    <div class="tab-content overflow-visible" style="padding:8px 0;">
                        <div id="tripInfo">
                            <div style="position:relative"><!-- class="widget-body"-->
                                <h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
                                ${uiLabelMap.BDViewTrip}
                                </h3>
                                <div><!--class="widget-main"-->
                                    <div class="title-status" id="statusTitle">
                                    ${trip.statusId?if_exists}
                                    </div>

                                    <div class="row-fluid">
                                        <div class="form-horizontal form-window-content-custom label-text-left content-description"
                                             style="margin:10px">
                                            <div class="row-fluid">
                                                <div class="span6">
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BDTripId}:</label>
                                                        </div>
                                                        <div class="div-inline-block">
                                                            <span><i>${trip.tripId?if_exists}</i></span>
                                                        </div>
                                                    </div>
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BDTotalWeight}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
                                                            <span><i>${trip.totalWeight?if_exists}
                                                                kg</i></span>
                                                        </div>
                                                    </div>

                                                <#if currentStatusId == "TRIP_CANCELLED" || currentStatusId == "TRIP_CONFIRMED">
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BDVehicleId}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
                                                            <span><i>${trip.vehicleName?if_exists}</i></span>
                                                        </div>
                                                    </div>

                                                    <div class='row-fluid' style="display: none">
                                                        <div class='span2'>
                                                            <label>${uiLabelMap.BDVehicleId}</label>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="vehicleId">
                                                                <div id="vehicleGrid"></div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                <#else>
                                                    <div class='row-fluid'>
                                                        <div class='span2'>
                                                            <label>${uiLabelMap.BDVehicleId}</label>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="vehicleId">
                                                                <div id="vehicleGrid"></div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </#if>
                                                <#assign status = trip.statusId>
                                                </div>
                                                <div class="span6">
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BSRequiredByDate}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
									<span>
                                    <#if trip.tripStartDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(trip.tripStartDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
                                                        </div>
                                                    </div>
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BSRequirementStartDate}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
									<span>
                                    <#if trip.tripStartDate?exists>
											${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(trip.tripStartDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
										</#if>
									</span>
                                                        </div>
                                                    </div>
                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BSDescription}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
                                                            <span>${trip.description?if_exists}</span>
                                                        </div>
                                                    </div>

                                                    <div class="row-fluid">
                                                        <div class="div-inline-block">
                                                            <label>${uiLabelMap.BDContractorId}
                                                                :</label>
                                                        </div>
                                                        <div class="div-inline-block">
                                                            <span>${contractorName?if_exists}</span>
                                                        </div>
                                                    </div>

                                                </div><!--.span6-->
                                            </div>
                                        </div><!-- .form-horizontal -->
                                        <div class="row-fluid">
                                            <div class="span12">
                                            <#include "viewTripItems.ftl"/>
                                            </div>
                                        </div><!--.form-horizontal-->

                                    </div><!--.row-fluid-->
                                </div><!--.widget-main-->
                            </div><!--.widget-body-->
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div style="position:relative">
    <div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
        <div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div>
                <div class="jqx-grid-load"></div>
                <span>${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>