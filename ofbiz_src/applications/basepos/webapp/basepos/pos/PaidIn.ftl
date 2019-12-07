<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/posresources/js/PaidIn.js"></script>
<style>
#amountPaidIn{
	font-weight: bold;
 	color: #cc0000;
 	font-size: 20px;
 	text-align: right;
}
.labelPaidOutIn{
	font-size: 15px;
	padding-top: 5px;
}
textarea[name=reasonPaidIn] {
    resize: none;
}
#commentPaidIn{
	width: 313px;
	height: 115px;
}
</style>
<form id="PaidInForm" action="" name="PaidInForm">
<div id="alterpopupWindowPaidIn" style="display:none;">
	<div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSManagerPaidIn}</div>
    <div>
    	<div class='row-fluid form-window-content'>
    		<div class='span12'>
    			<div class= 'row-fluid margin-bottom10'>
    				<div class='span2 align-right asterisk labelPaidOutIn'>
    					${uiLabelMap.BPOSReason}
    				</div>
    				<div class='span10'>
    					 <div tabindex="5" id='paidInReason'>
        				 </div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span2 align-right asterisk labelPaidOutIn'>
    					${uiLabelMap.BPOSAmount}
    				</div>
    				<div class='span10'>
    					<input tabindex="6" type="text" name="amountPaidIn" id="amountPaidIn" style="width:313px; height: 33px"> 
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span2 align-right labelPaidOutIn'>
    					${uiLabelMap.BPOSComments}
    				</div>
    				<div class='span10'>
    					<textarea tabindex="7" id="commentPaidIn" name="commentPaidIn"></textarea>
    				</div>
    			</div>
    		</div>
    	</div>
    	<div class="form-action">
			<div class='pull-right'>
				<button id="alterSave" tabindex="8" class='btn btn-primary form-action-button' onclick="processPaidIn()"><i class='fa icon-ok'></i> ${uiLabelMap.BPOSSave}</button>
				<button id="alterCancel" tabindex="9" class='btn btn-danger form-action-button' onclick="resetPaidIn()"><i class='fa icon-refresh'></i> ${uiLabelMap.BSResetEdit}</button>
			</div>
		</div>
    </div>
</div>
</form>
<script type="text/javascript">
	var paidInInvoiceItemType = [
		<#if invoiceItemTypeList?exists>
			<#list invoiceItemTypeList as invoiceItemType >
			{
				invoiceItemTypeId: '${invoiceItemType.invoiceItemTypeId?if_exists}',
				description: "${invoiceItemType.description?if_exists}"
			},
			</#list>
		</#if>
	        ];
	var sourcePaidInReason =
        {
            localdata: paidInInvoiceItemType,
            datatype: "array"
        };
    var dataAdapterPaidIn = new $.jqx.dataAdapter(sourcePaidInReason);           
	var BPOSRecordIncomeOtherSuccess = "${StringUtil.wrapString(uiLabelMap.BPOSRecordIncomeOtherSuccess)}";
	var BPOSAmountIsRequired = "${StringUtil.wrapString(uiLabelMap.BPOSAmountIsRequired)}";
	var BPOSReasonIsRequired = "${StringUtil.wrapString(uiLabelMap.BPOSReasonIsRequired)}";
	var BPOSAreYouSureWithThisAmountIn = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureWithThisAmountIn)}";
	$(document).ready(function(){
		$('body').keydown(function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			//73 is code of I
			if (e.ctrlKey && code == 73 && POSPermission.has("POS_PAID_CTRL_I", "CREATE")) {
				if (flagPopup){
					showPaidIn();
				}
				e.preventDefault();
				return false;
			}
		});	
	});
	$('#reasonPaidIn').keydown(function(event) {  	
       code = event.keyCode ? event.keyCode : event.which; 	
       if(code == 13){
    	   processPaidIn();
       }
	});
	$('#amountPaidIn').keydown(function(event) {  	
	    code = event.keyCode ? event.keyCode : event.which; 	
	    if(code == 13){
	    	processPaidIn();
	    }
	});
</script>