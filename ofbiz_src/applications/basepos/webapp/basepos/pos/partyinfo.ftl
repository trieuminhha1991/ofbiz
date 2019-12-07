<div id="posPartyInfo" class="customer-info">
	<input type="hidden" id="partyIdTmp" name="partyIdTmp" value=""/>
	<div class='row-fluid'>
		<div class='span4' style="font-family: 'Roboto';">
			${uiLabelMap.BPOSId}:
		</div>
		<div class='span8' id="partyId">
		</div>
   	</div>
	<div class='row-fluid'>
		<div class='span4' style="font-family: 'Roboto';">
			${uiLabelMap.BPOSFullName}:
		</div>
		<div class='span8' id="partyName">
		</div>
   	</div>
	<div class='row-fluid'>
		<div class='span4' style="font-family: 'Roboto';">
			${uiLabelMap.BPOSAddress}:
		</div>
		<div class='span8' id="partyAddress">
		</div>
   	</div>
	<div class='row-fluid'>
		<div class='span4' style="font-family: 'Roboto';">
			${uiLabelMap.BPOSMobile}:
		</div>
		<div class='span8' id="partyMobile">
		</div>
   	</div>
   	<div class='row-fluid'>
		<div class='span4' style="font-family: 'Roboto';">
			${uiLabelMap.BPOSCustomerType}:
		</div>
		<div class='span8' id="partyLoyalty">
		</div>
   	</div>
</div>
<script type="text/javascript">
	$(document).ready(function (){ 
		updateParty();
	});
</script>	