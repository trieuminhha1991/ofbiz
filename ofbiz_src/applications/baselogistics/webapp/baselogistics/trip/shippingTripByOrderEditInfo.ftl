<#include 'script/shippingTripByOrderEditInfoScript.ftl' />
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="editShippingTrip" name="editShippingTrip" method="post" action="">
	<div class="row-fluid">
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.FacilityFrom}</div></div>
					    <div class="span7"><div id="facilityId" style="color: #037C07;" class="green-label"></div></div>
					</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.Address}</div></div>
					    <div class="span7"><div id="contactMechId" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.TransportCost}</div></div>
				 	    <div class="span7"><div id="shipCost" class="green-label"></div></div>
					</div>
          <div class="row-fluid">
              <div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.CostCustomerPaid}</div></div>
              <div class="span7"><div id="shipReturnCost" class="green-label"></div></div>
          </div>
				</div>
				<div class="span6">
          <div class='row-fluid'>
          		<div class="span5" style="text-align: right;"><div>${uiLabelMap.Driver}</div></div>
          		<div class="span7" ><div id="driverPartyId" class="green-label"></div></div>
          </div>
          <div class='row-fluid'>
            	<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.StartShipDate}</div></div>
            	<div class="span7"><div class="green-label" id="fromDate"></div></div>
          </div>
          <div class='row-fluid'>
              <div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.EndShipDate}</div></div>
            	<div class="span7"><div class="green-label" id="thruDate"></div></div>
          </div>
					<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
				 	    <div class="span7" style="text-align: left;"><textarea id="description"></textarea></div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>