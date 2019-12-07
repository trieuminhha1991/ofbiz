<div class='row-fluid'>
	<div class='span5'>
		<label>${uiLabelMap.BSRequestSupplierDelivery}</label>
	</div>
	<div class="span7">
		<div id="requestFavorDelivery" style="margin-left:0 !important;margin-top:8px"></div>
	</div>
</div>
<div class='row-fluid favor-delivery-container'>
	<div class='span5'>
		<label class="required">${uiLabelMap.BSSupplierDelivery}</label>
	</div>
	<div class="span7">
		<div id="favorSupplierPartyId">
			<div id="supplierPartyGrid"></div>
		</div>
	</div>
</div>
<div class='row-fluid favor-delivery-container' style="margin-bottom: 40px">
	<div class='span5'>
		<label class="required">${uiLabelMap.BSFacilityDelivery}</label>
	</div>
	<div class="span7">
		<div id="shipGroupFacilityId">
			<div id="facilityGrid"></div>
		</div>
	</div>
</div>
<#include 'script/orderNewDropShipScript.ftl'/>
