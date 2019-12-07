<#include "component://widget/templates/jqwLocalization.ftl" />
<style>
.labelCreateOrder{
	font-size: 13px;
	padding-top: 5px;
}
#amount{
	height: 32px;
	width: 99%;
	font-size: 20px;
  	font-weight: bold;
  	color: #cc0000;
  	text-align: right;
}
</style>
<div id="alterpopupWindowCreateOrder" style="display:none;">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSPageTitleCreateOrderToLog}</div>
    <div>
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span5 align-right labelCreateOrder'>
						${uiLabelMap.BPOSPayCash}
					</div>
					<div class="span7">
						<input type="text" id="amount" name="amount" onclick="this.select()">
			   		</div>
			   	</div>
    			<div class= 'row-fluid margin-bottom10'>
	    			<div class='span5 align-right asterisk labelCreateOrder'>
						${uiLabelMap.BSShippingAddress}
					</div>
					<div class="span7">
						<div id="shippingContactMechId">
							<div id="shippingContactMechGrid"></div><#-- name="shipping_contact_mech_id" -->
						</div>
			   		</div>
			   	</div>
    			<div class= 'row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk labelCreateOrder'>
						${uiLabelMap.BSShippingMethod}
					</div>
					<div class="span7">
						<div id="shippingMethodTypeId"></div><#-- name="shipping_method" -->
			   		</div>
    			</div>
    			<div class= 'row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk labelCreateOrder'>
						${uiLabelMap.BSPaymentMethod}
					</div>
					<div class="span7">
						<div id="checkOutPaymentId"></div><#-- name="checkOutPaymentId" -->
			   		</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk labelCreateOrder'>
    					${uiLabelMap.BSDesiredDeliveryDate}
    				</div>
    				<div class='span7'>
    					<div id='deliveredDate'></div>
    				</div>
    			</div>
    		</div>
    	</div>
    	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='icon-remove'></i> ${uiLabelMap.BPOSCancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right' onclick="createOrderToLog()"><i class='icon-ok'></i> ${uiLabelMap.BPOSCreate}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<script type="text/javascript">
	var BPOSPleaseSelectCustomer = "${StringUtil.wrapString(uiLabelMap.BPOSPleaseSelectCustomer)}";
	var BPOSValidateRequired = "${StringUtil.wrapString(uiLabelMap.BPOSValidateRequired)}";
	var BPOSAreYouCertainlyCreated = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouCertainlyCreated)}";
	var BPOSNoAnyItemInCart = "${StringUtil.wrapString(uiLabelMap.BPOSNoAnyItemInCart)}";
	var BSContactMechId = "${StringUtil.wrapString(uiLabelMap.BSContactMechId)}";
	var BSReceiverName = "${StringUtil.wrapString(uiLabelMap.BSReceiverName)}";
	var BSOtherInfo = "${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}";
	var BSAddress = "${StringUtil.wrapString(uiLabelMap.BSAddress)}";
	var BSCity = "${StringUtil.wrapString(uiLabelMap.BSCity)}";
	var BSStateProvince = "${StringUtil.wrapString(uiLabelMap.BSStateProvince)}";
	var BSCountry = "${StringUtil.wrapString(uiLabelMap.BSCountry)}";
	var BSCounty = "${StringUtil.wrapString(uiLabelMap.BSCounty)}";
	var BPOSAmountBiggerTotalDue = "${StringUtil.wrapString(uiLabelMap.BPOSAmountBiggerTotalDue)}";
	
	<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) !>
	var defaultProductStoreId = '${productStoreId?if_exists}';
	
	$('#alterpopupWindowCreateOrder').on('open', function (event) { 
		flagPopup = false;
		$('#amount').focus();
		var defaultPartyId = $("#partyIdTmp").val();
		initDropDownListPaymentMethod(defaultPartyId, defaultProductStoreId);
		initDropDownListShippingMethod(defaultPartyId, defaultProductStoreId);
		initDropDownButtonShippingAddress(defaultPartyId, defaultProductStoreId);
		
		var totalDue = $("#totalDue").val();
		totalDue = parseFloat(totalDue);
		$("#amount").maskMoney('mask', totalDue);
		
		$('#alterpopupWindowCreateOrder').keydown(function(event) {  	
	    	code = event.keyCode ? event.keyCode : event.which; 	
	    	if (code == 8){
	    		event.preventDefault();
	    		return false;
	       	}
	    	if (code == 13){
	    		createOrderToLog();
	       	}
		});
		
		$('#shippingContactMechId').keydown(function(event) {  	
	    	code = event.keyCode ? event.keyCode : event.which; 	
	    	if (code == 38){
	    		var rowindex = $('#shippingContactMechGrid').jqxGrid('getselectedrowindex');
	    		if (rowindex < 0){
	    			$("#shippingContactMechGrid").jqxGrid('selectrow', 0);
	    		} else {
	    			if (rowindex != 0){
	    				$("#shippingContactMechGrid").jqxGrid('selectrow', rowindex - 1);
	    			}
	    		}
	    		
	    		event.preventDefault();
				return false;
	       	}
	    	if (code == 40){
	    		var rowData = $('#shippingContactMechGrid').jqxGrid('getrows');
	    		var rowindex = $('#shippingContactMechGrid').jqxGrid('getselectedrowindex');
	    		if (rowindex < 0){
	    			$("#shippingContactMechGrid").jqxGrid('selectrow', 0);
	    		} else {
	    			if (rowindex < (rowData.length - 1)){
	    				$("#shippingContactMechGrid").jqxGrid('selectrow', rowindex + 1);
	    			}
	    		}
	    		
	    		event.preventDefault();
				return false;
	       	}
		});
	});
	
	$('#alterpopupWindowCreateOrder').on('close', function (event) { 
		flagPopup = true;
		$('#shippingContactMechId').jqxDropDownButton('setContent', '');
		$('#alterpopupWindowCreateOrder').jqxValidator('hide');
	});
	
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//84 is code of S
		if (e.ctrlKey && code == 83 && POSPermission.has("POS_ORDER_CTRL_S", "CREATE")) {
			if (flagPopup){
				showPopupCreateOrder();				
			}
			e.preventDefault();
			return false;
		}
	});	
</script>