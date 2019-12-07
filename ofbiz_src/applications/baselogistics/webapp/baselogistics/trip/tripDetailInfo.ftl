<#include 'script/tripDetailScript.ftl'/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">
    <div style="position:relative">
        
        <div>
            <h2 class="smaller lighter blue font-bold " style="text-align: center;">
            ${uiLabelMap.Trip}
            </h2>
            <div class="row-fluid font-bold" id="detailPack">
                <div class="span12">
                    <div class='row-fluid' style="margin-bottom: -10px !important">
                        <div class="span6">
                            <div class="row-fluid">
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.TripId}</div>
                                </div>
                                <div class="span7">
                                    <div id="shippingTripId" class="green-label">${trip.shippingTripId?if_exists}</div>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.LogShipper}</div>
                                </div>
                                <div class="span7">
                                    <div id="shipperPartyId" class="green-label"></div>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.ShipCost}</div>
                                </div>
                                <div class="span7">
                                    <div id="tripCost" class="green-label">${trip.tripCost?if_exists}</div>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.CostCustomerPaid}</div>
                                </div>
                                <div class="span7">
                                    <div id="costCustomerPaid"
                                         class="green-label">${trip.costCustomerPaid?if_exists}</div>
                                </div>
                            </div>
                        </div>
                        <div class="span6">
                            <div class='row-fluid'>
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.StartShipDate}</div>
                                </div>
                                <div class="span7">
                                    <div class="green-label"
                                         id="estimatedTimeStart">${trip.startDateTime?if_exists}</div>
                                </div>
                            </div>
                            <div class='row-fluid'>
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.EndShipDate}</div>
                                </div>
                                <div class="span7">
                                    <div class="green-label"
                                         id="estimatedTimeEnd">${trip.finishedDateTime?if_exists}</div>
                                </div>
                                <div class="row-fluid">
                                    <div class="span5" style="text-align: right;"><div>${uiLabelMap.Status}</div></div>
                                    <div class="span7 green"><div id="tripStatus" class=""></div></div>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span5" style="text-align: right;">
                                    <div>${uiLabelMap.Description}</div>
                                </div>
                                <div class="span7">
                                    <div id="description"
                                         class="green-label">${StringUtil.wrapString(trip.description?if_exists)}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row-fluid ">
            <div class="span12">
                <h4 class="row header smaller lighter blue font-bold" style="">
                ${uiLabelMap.BSListOrder}
                </h4>
                <div style="position:relative" class="form-window-content-custom">
                    <div id="jqxgridPackSelected"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="row-fluid hide" id="noteContent">
    <div class="span12">
        <h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
        ${uiLabelMap.Notes}
        </h4>
        <div id="listNote"></div>
    </div>
</div>
<div id="notePack" class="hide popup-bound">
    <div>${uiLabelMap.ReasonRejected}</div>
    <div class='form-window-container'>
        <div class='form-window-content'>
            <div class="row-fluid margin-top10">
                <div class="span3" style="text-align: right">
                    <div class="asterisk">${uiLabelMap.Notes}</div>
                </div>
                <div class="span8">
                    <textarea id="note" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px"
                              class="span12"></textarea>
                </div>
            </div>
        </div>
        <div class="form-action popup-footer">
            <button id="noteCancel" class='btn btn-danger form-action-button pull-right'><i
                    class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="noteSave" class='btn btn-primary form-action-button pull-right'><i
                    class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
        </div>
    </div>
</div>

<div id="viewMap" style="z-index: 1000000000000000 !important" class="hide popup-bound">
    <div>${uiLabelMap.BLViewMap}</div>
    <div class='form-window-container'>
        <div id="map" style="height: 600px;overflow-y: hidden;">
        </div>
    </div>
</div>