<div class="row-fluid">
    <div class="span6">
        <h4 class="smaller green" style="display:inline-block">
        ${uiLabelMap.GeneralInfo}
        </h4>
        <table width="100%" border="0" cellpadding="1"
               class="table table-striped table-hover table-bordered dataTable">

            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.FacilityFrom}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="facilityIdDT"></span>
                </td>
            </tr>
            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.Address}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="contactMechIdDT"></span>
                </td>
            </tr>
            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.TransportCost}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="shipCostDT"></span>
                </td>
            </tr>
            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.CostCustomerPaid}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="shipReturnCostDT"></span>
                </td>
            </tr>
        </table>
    </div><!--.span6-->
    <div class="span6">
        <h4 class="smaller green" style="display:inline-block">
            <!-- title -->
        </h4>
        <table width="100%" border="0" cellpadding="1"
               class="table table-striped table-hover table-bordered dataTable margin-top15">
            <tr>
                <td align="right" valign="top" width="30%">
                    <div><b>${uiLabelMap.BSDesiredDeliveryDate}</b></div>
                </td>
                <td valign="top" width="70%">
                    <span id="fromDateDT"></span> - <span id="thruDateDT"></span>
                </td>

            </tr>
            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.Driver}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="driverPartyIdDT"></span>
                </td>
            </tr>
            <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.Description}</b> </span>
                </td>
                <td valign="top" width="70%">
                    <span id="descriptionDT"></span>
                </td>
            </tr>

        </table>
    </div>
</div>
<div class="row-fluid">
    <div class="span12">
        <h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListOrder}</h4>
        <table id="tableOrder" width="100%" border="0" cellpadding="0"
               class="table table-striped table-bordered table-hover dataTable">
            <thead>
            <tr valign="bottom" style="height: 40px">
                <th width="3%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
                <th width="10%" class="align-center"><span><b>${uiLabelMap.OrderId}</b></span></th>
                <th width="10%" class="align-center"><span><b>${uiLabelMap.BSCustomerId}</b></span></th>
                <th width="10%" class="align-center"><span><b>${uiLabelMap.BLDeliveryClusterId}</b></span></th>
                <th width="25%" class="align-center"><span><b>${uiLabelMap.BSFullName}</b></span></th>
                <th width="35%" class="align-center"><span><b>${uiLabelMap.BSAddress}</b></span></th>
                <th width="7%" class="align-center"><span><b>${uiLabelMap.BPOTotal}</b></span></th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
</div>


<div id="jqxNotification">
    <div id="notificationContent"></div>
</div>