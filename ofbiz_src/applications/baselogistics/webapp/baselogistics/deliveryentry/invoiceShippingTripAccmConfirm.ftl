<style type="text/css">
	#ship-info-container {
		height: 155px;
		overflow: hidden;
		-webkit-transition: height 1s ease-in-out;
		transition: height 1s ease-in-out;
		padding-bottom:3px;
	}
	#view-more-note-ship-info {
		margin-top:-23px;
	}
</style>
<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.BACCGeneralInfo}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable" id="generalInvTable">
	        <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.BACCInvoiceType}</b> </span>
                </td>
                <td valign="top" width="70%" id="invoiceTypeLabel">
                </td>
            </tr>
	        <tr>
                <td align="right" valign="top" width="25%">
                    <span><b>${uiLabelMap.BACCOrganization}</b> </span>
                </td>
                <td valign="top" width="70%" id="organizationLabel"></td>
            </tr>
            <tr>
	            <td align="right" valign="top" width="25%">
	                <span><b>${uiLabelMap.BACCCustomer}</b> </span>
	            </td>
	            <td valign="top" width="70%" id="customerLabel">
	            </td>
	        </tr>
        </table>
	</div><!--.span12-->
</div>
<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.BACCListInvoiceItems}</h4>
    	<table width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable" id="invoice-item-table">
  			<thead>
  				<tr valign="bottom">
  					<th width="8%" class="align-center""><span><b>${uiLabelMap.DeliveryEntryCode}</b></span></th>
  					<th width="18%" class="align-center"><span><b>${uiLabelMap.BLDstartDateTime}</b></span></th>
  					<th width="28%" class="align-center"><span><b>${uiLabelMap.BLDfinishedDateTime}</b></span></th>
		            <th width="12%" align="right" class="align-center"><span><b>${uiLabelMap.BSQuantity}</b></span></th>
		            <th width="13%" align="right" class="align-center"><span><b>${uiLabelMap.BLDTripCost}</b></span></th>
		            <th width="8%" align="right" class="align-center"><span><b>${uiLabelMap.BSCurrencyUomId}</b></span></th>
		            <th width="13%" align="right" class="align-center"><span><b>${uiLabelMap.BSItemTotal}</b></span></th>
	          	</tr>
  			</thead>
  			<tbody>
  				<tr>
		            <td align="right" colspan="6" class="align-right"><div><b>${uiLabelMap.BACCTotal}</b></div></td>
		            <td align="right" nowrap="nowrap" class="align-right"><div id="invoice-total"></div></td>
	          	</tr>
       		</tbody>
    	</table>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		$("#view-more-note-ship-info").on("click", function() {
			if ($("#ship-info-container").hasClass("active")) {
				$("#view-more-note-ship-info i").removeClass("fa-compress");
				$("#view-more-note-ship-info i").addClass("fa-expand");
				$("#ship-info-container").removeClass("active");
				$("#ship-info-container").height('155');
			} else {
				$("#view-more-note-ship-info i").removeClass("fa-expand");
				$("#view-more-note-ship-info i").addClass("fa-compress");
				$("#ship-info-container").addClass("active");
				$("#ship-info-container").height($("#ship-info-container table").height());
			}
		});
	});
</script>

<script type="text/javascript" src="/logresources/js/deliveryentry/invoiceShippingTripAccmConfirm.js"></script>