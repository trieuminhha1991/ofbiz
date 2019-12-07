<#include 'script/newDeliveryClusterInfoScript.ftl'/>
<script type="text/javascript">

</script>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="initDeliveryCluster" name="initDeliveryEntry" method="post" action="<@ofbizUrl>showShipmentConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
	<input id='defaultWeightUomId' value='WT_kg' type='hidden'></input>
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
                    <div class="row-fluid margin-bottom10">
                        <div class="span5" style="text-align: right">
                            <div class="asterisk"> ${uiLabelMap.BLDeliveryClusterCode} </div>
                        </div>
                        <div class="span7">
                            <input id="deliveryClusterCode"></input>
                        </div>
                    </div>
                    <div class="row-fluid margin-bottom10">
                        <div class="span5" style="text-align: right">
                            <div class="asterisk"> ${uiLabelMap.BLDeliveryClusterName} </div>
                        </div>
                        <div class="span7">
                            <input id="deliveryClusterName"></input>
                        </div>
                    </div>
				</div>
				<div class="span6">
                    <div class='row-fluid'>
                        <div class='span5'>
                            <span class="asterisk">${uiLabelMap.BLShipperName}</span>
                        </div>
                        <div class="span7">
                            <div id="shipper">
                                <div id="jqxGridShipper"></div>
                            </div>
                        </div>
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