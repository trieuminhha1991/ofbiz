<#include 'script/updateShippingTripStatusScript.ftl'/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">
	<h4 class="row header smaller lighter blue font-bold" style="margin: 5px 0px 10px 0px !important;line-height:20px;font-size:18px;">
		${uiLabelMap.UpdateShippingTripStatus}
	</h4>
	<div style="position:relative">
    <div class="row-fluid font-bold">
  		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
  			<div class="span12">
  				<h5 class="smaller blue font-bold header" style=" line-height:20px;">
  					${uiLabelMap.GeneralInfo}
  				</h5>
  				<div class='row-fluid' style="margin-bottom: -10px !important">
  					<div class="span6">
              <div class="row-fluid">
  				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.TripId}</div></div>
  					 	    <div class="span7"><div id="shippingTripId" class="green-label"></div></div>
  						</div>
  						<div class="row-fluid">
  				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.LogShipper}</div></div>
  					 	    <div class="span7"><div id="shipperPartyId" class="green-label"></div></div>
  						</div>
  						<div class="row-fluid">
  							<div class="span5" style="text-align: right;"><div>${uiLabelMap.ShipCost}</div></div>
  					 	    <div class="span7"><div id="tripCost" class="green-label"></div></div>
  						</div>
              <div class="row-fluid">
  							<div class="span5" style="text-align: right;"><div>${uiLabelMap.CostCustomerPaid}</div></div>
  					 	    <div class="span7"><div id="costCustomerPaid" class="green-label"></div></div>
  						</div>
  					</div>
  					<div class="span6">
  						<div class='row-fluid'>
  							<div class="span5" style="text-align: right;"><div>${uiLabelMap.StartShipDate}</div></div>
  							<div class="span7"><div class="green-label" id="estimatedTimeStart"></div></div>
  					</div>
  					<div class='row-fluid'>
  						<div class="span5" style="text-align: right;"><div>${uiLabelMap.EndShipDate}</div></div>
  							<div class="span7"><div class="green-label" id="estimatedTimeEnd"></div></div>
  					</div>
						<div class="row-fluid">
								<div class="span5" style="text-align: right;"><div>${uiLabelMap.Status}</div></div>
								<div class="span7"><div id="tripStatus" class="green-label"></div></div>
						</div>
            <div class="row-fluid">
                <div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
                <div class="span7"><div id="description" class="green-label"></div></div>
            </div>
  					</div>
  				</div>
  			</div>
  		</div><!-- .form-horizontal -->
  	</div><!--.row-fluid-->
  	<div class="row-fluid margin-top10">
  		<div class="span12">
  			<h5 class="smaller blue font-bold header" style="">
  			${uiLabelMap.UpdateOrderStatus}
			</h5>
  			<div id="jqxgridPackSelected" style="width: 100%"></div>
  		</div>
  	</div>
		<div class="row-fluid wizard-actions margin-top10">
			<button class="btn btn-small btn-next btn-success" id="btnConfirm">
				<i class="icon-arrow-right icon-on-right"></i>
				${uiLabelMap.Save}
			</button>
		</div>
	</div>
</div>


<div id="confirmWindow" style="z-index: 10000000000000 !important" class="hide popup-bound">
	<div>${uiLabelMap.Confirm}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<h4 class="smaller blue header font-bold" style="display:inline-block">
						${uiLabelMap.ConfirmOrderStatusInfo}
					</h4>
					<div id="jqxgridConfirmPackStatus"></div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="confirmCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
			<button id="confirmSave" class='btn btn-primary form-action-button pull-right'>
				<i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/logresources/js/trip/updateShippingTripStatus.js"></script>