<#include 'script/deliveryEntryInfoScript.ftl'/>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="initDeliveryEntry" name="initDeliveryEntry" method="post" action="<@ofbizUrl>showShipmentConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
	<input id='defaultWeightUomId' value='WT_kg' type='hidden'></input>
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.FacilityFrom}</div></div>
					    <div class="span7"><div id="facilityIdAdd" style="color: #037C07;" class="green-label"></div></div>
					</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.Address}</div></div>
					    <div class="span7"><div id="contactMechId" class="green-label"></div></div>
					</div>
					<div class='row-fluid hide'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.Status}</div></div>
				 	    <div class="span7"><div id="statusDEId" style="color: #037C07;" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.BLTransportType}</div></div>
				 	    <div class="span7"><div id="shipmentTypeId" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.TransportCost}</div></div>
				 	    <div class="span7"><div id="shipCost" class="green-label"></div></div>
					</div>
					<div class='row-fluid'>
				   		<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.StartShipDate}</div></div>
				 	    <div class="span7"><div class="green-label" id="fromDateAdd"></div></div>
					</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.EndShipDate}</div></div>
				 	    <div class="span7"><div class="green-label" id="thruDateAdd"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.CarrierParty}</div></div>
				 	    <div class="span7"><div id="carrierPartyId" class="green-label"></div></div>
			   		</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div>${uiLabelMap.Vehicle}</div></div>
				 	    <div class="span7"><div id="fixedAssetId" class="green-label"></div></div>
					</div>
					<div class='row-fluid'>
						<div class="span5" style="text-align: right;"><div>${uiLabelMap.Driver}</div></div>
				 	    <div class="span7"><div id="driverPartyId" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
			    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.Deliverer}</div></div>
				 	    <div class="span7"><div id="delivererPartyId" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
				 	    <div class="span7"><textarea id="description" class='text-popup' style="width: 600px; height: 60px"></textarea></div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>