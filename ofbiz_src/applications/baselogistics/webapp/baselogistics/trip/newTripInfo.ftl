<#include 'script/tripInfoScript.ftl'/>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="initTrip" name="initTrip" method="post" action="<@ofbizUrl>showPackConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
	<h5 class="smaller green row header blue font-bold" style="">
		${uiLabelMap.GeneralInfo}
	</h5>
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class="row-fluid ">
			    		<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.LogShipper}</div></div>
				 	    <div class="span7"><div id="shipperPartyId" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
				 	    <div class="span7"><textarea id="description" class='text-popup' style="width: 600px; height: 60px"></textarea></div>
					</div>
				</div>
				<div class="span6 ">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.ShipCost}</div></div>
				 	    <div class="span7"><div id="tripCost" class="green-label"></div></div>
					</div>
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.CostCustomerPaid}</div></div>
				 	    <div class="span7"><div id="costCustomerPaid" class="green-label"></div></div>
					</div>
					<div class='row-fluid'>
				   		<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.StartShipDate}</div></div>
				 	    <div class="span7"><div class="green-label" id="estimatedTimeStart"></div></div>
					</div>
					<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.EndShipDate}</div></div>
				 	    <div class="span7"><div class="green-label" id="estimatedTimeEnd"></div></div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>
