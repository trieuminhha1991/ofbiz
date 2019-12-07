<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/posresources/js/PaidOut.js"></script>
<style>
#amountPaidOut{
	font-weight: bold;
 	 color: #cc0000;
 	 font-size: 20px;
 	 text-align: right;
}
.labelPaidInOut{
	font-size: 15px;
	padding-top: 5px;
}
textarea[name=reasonPaidOut] {
    resize: none;
}
#commentPaidOut{
	width: 313px;
	height: 115px;
}
</style>
<form id="PaidOutForm" action="" name="PaidOutForm">
<div id="alterpopupWindowPaidOut" style="display:none;">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSManagerPaidOut}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
    			<div class= 'row-fluid margin-bottom10'>
    				<div class='span2 align-right asterisk labelPaidInOut'>
    					${uiLabelMap.BPOSReason}
    				</div>
    				<div class='span10'>
    					 <div tabindex="5" id='paidOutReason'>
        				 </div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span2 align-right asterisk labelPaidInOut'>
    					${uiLabelMap.BPOSAmount}
    				</div>
    				<div class='span10'>
    					<input type="text" tabindex="6" name="amountPaidOut" id="amountPaidOut" style="width:313px; height: 33px"> 
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span2 align-right labelPaidInOut'>
    					${uiLabelMap.BPOSComments}
    				</div>
    				<div class='span10'>
    					<textarea tabindex="7" id="commentPaidOut" name="commentPaidOut"></textarea>
    				</div>
    			</div>
    			
    		</div>
    	</div>
    	<div class="form-action">
			<div class='pull-right'>
				<button id="alterSave" tabindex="8" class='btn btn-primary form-action-button' onclick="processPaidOut()"><i class='fa icon-ok'></i> ${uiLabelMap.BPOSSave}</button>
				<button id="alterCancel" tabindex="9" class='btn btn-danger form-action-button' onclick="resetPaidOut()"><i class='fa icon-refresh'></i> ${uiLabelMap.BSResetEdit}</button>
			</div>
		</div>
    </div>
</div>
</form>
<script type="text/javascript">
	var paidOutInvoiceItemType = [
		<#if invoiceItemTypeList?exists>
			<#list invoiceItemTypeList as invoiceItemType >
			{
				invoiceItemTypeId: '${invoiceItemType.invoiceItemTypeId?if_exists}',
				description: "${invoiceItemType.description?if_exists}"
			},
			</#list>
		</#if>
	];
	var sourcePaidOutReason =
        {
            localdata: paidOutInvoiceItemType,
            datatype: "array"
        };
    var dataAdapterPaidOut = new $.jqx.dataAdapter(sourcePaidOutReason); 
	var BPOSRecordCostOtherSuccess = "${StringUtil.wrapString(uiLabelMap.BPOSRecordCostOtherSuccess)}";
	var BPOSAmountIsRequired = "${StringUtil.wrapString(uiLabelMap.BPOSAmountIsRequired)}";
	var BPOSReasonIsRequired = "${StringUtil.wrapString(uiLabelMap.BPOSReasonIsRequired)}";
	var BPOSAreYouSureWithThisAmountOut = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureWithThisAmountOut)}";
	$('body').keydown(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		//67 is code of C
		if (e.ctrlKey && code == 79 && POSPermission.has("POS_PAID_CTRL_O", "CREATE")) {
			if (flagPopup) {
				showPaidOut();
			}
			e.preventDefault();
			return false;
		}
	});	
	$('#reasonPaidOut').keydown(function(event) {  	
	    code = event.keyCode ? event.keyCode : event.which; 	
	    if(code == 13){
	 	   processPaidOut();
	    }
	});
	$('#amountPaidOut').keydown(function(event) {  	
	    code = event.keyCode ? event.keyCode : event.which; 	
	    if(code == 13){
	 	   processPaidOut();
	    }
	});
</script>