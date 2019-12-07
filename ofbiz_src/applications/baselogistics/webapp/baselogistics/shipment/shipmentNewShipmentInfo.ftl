<#include 'script/shipmentInfoScript.ftl'/>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="initShipment" name="initShipment" method="post" action="<@ofbizUrl>showShipmentConfirmPage</@ofbizUrl>">
<input name="listInvString" type="hidden"></input>
<input type="hidden" id="shipmentTypeId" name="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"></input>
	<div class="row-fluid">
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.OriginFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="originFacilityId" name="originFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.OriginAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="originContactMechId" name="originContactMechId"></div>
				   		</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.DestFacility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="destinationFacilityId" name="destinationFacilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.DestAddress}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="destinationContactMechId" name="destinationContactMechId"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.EstimatedShipDate}</span>
						</div>
						<div class="span7">
							<div id="estimatedShipDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.EstimatedArrivalDate}</span>
						</div>
						<div class="span7">
							<div id="estimatedArrivalDate">
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6">
			<#--		<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.ShipmentMethod}</span>
						</div>
						<div class="span7">
							<div  class="green-label" id="shipmentMethodTypeId" name="shipmentMethodTypeId"></div>
				   		</div>
					</div>
			-->
					<div class='row-fluid'>
						<div class='span5'>
							<span>${uiLabelMap.EstimatedShipCost}</span>
						</div>
						<div class="span7">	
							<div id="estimatedShipCost" class="green-label"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span>${uiLabelMap.CurrencyUomId}</span>
						</div>
						<div class="span7">
							<div class="span5">
								<div id="currencyUomId" class="green-label"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>