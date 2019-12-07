<form class="form-horizontal form-window-content-custom" id="initTransfer" name="initTransfer" method="post" action="<@ofbizUrl>showTransferConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
		<div class="span11">
			<div class='row-fluid'>
				<div class="span6">
					<div class='row-fluid'>
						<div class='span4'>
							<span >${uiLabelMap.TransferType}</span>
						</div>
						<div class="span8">
							<div class="green-label" style="text-align: left;" id="transferTypeId" name="transferTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<span >${uiLabelMap.OriginFacility}</span>
						</div>
						<div class="span8">
							<div class="green-label" style="text-align: left;" id="originFacility" name="originFacility"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<span >${uiLabelMap.OriginAddress}</span>
						</div>
						<div class="span8">
							<div class="green-label" style="text-align: left;" id="originContactMechId" name="originContactMechId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<span >${uiLabelMap.DestFacility}</span>
						</div>
						<div class="span8">
							<div class="green-label" style="text-align: left;" id="destFacility" name="destFacility"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<span >${uiLabelMap.DestAddress}</span>
						</div>
						<div class="span8">
							<div class="green-label" style="text-align: left;" id="destContactMechId" name="destContactMechId"></div>
				   		</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class="span5 margin-top10">
							<label class="required">${uiLabelMap.BLShippingDate}</label>
						</div>
						<div class="span7">
							<div id="shipAfterDate"></div>
							<div id="shipBeforeDate"  class="margin-top10"></div>
						</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.ShipmentMethod}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="shipmentMethodTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid hide'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.CarrierParty}</span>
						</div>
						<div class="span7">
							<div id="carrierPartyId" class="green-label"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span7 align-left">
							<textarea id="description" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>