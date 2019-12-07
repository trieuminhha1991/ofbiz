<#if !isShipFromFacilityConsign?exists || !isShipFromFacilityConsign>
<style type="text/css">
	.facility-consign-container {display:none}
</style>
</#if>
<div class='row-fluid'>
	<div class='span5'>
		<label>${uiLabelMap.BSShipFromFacilityConsign}</label>
	</div>
	<div class="span7">
		<div id="shipFromFacilityConsign" style="margin-left:0 !important;margin-top:8px"></div>
	</div>
</div>
<div class='row-fluid facility-consign-container'>
	<div class='span5'>
		<label class="required">${uiLabelMap.BSDistributorDelivery}</label>
	</div>
	<div class="span7">
		<div id="favorDistributorPartyId">
			<div id="distributorPartyGrid"></div>
		</div>
	</div>
</div>
<div class='row-fluid facility-consign-container' style="margin-bottom: 40px">
	<div class='span5'>
		<label class="required">${uiLabelMap.BSFacilityDelivery}</label>
	</div>
	<div class="span7">
		<div id="facilityConsignId">
			<div id="facilityConsignGrid"></div>
		</div>
	</div>
</div>
<#include 'script/orderNewFacilityConsignScript.ftl'/>
