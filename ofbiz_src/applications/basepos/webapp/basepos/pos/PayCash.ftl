<style type="text/css">
.jqx-widget-content{
	font-family: 'Roboto';
}
#amountCash, #paybackCash, #amountCreditCard{
	height: 40px;
	width: 100%;
	font-size: 25px;
  	font-weight: bold;
  	color: #cc0000;
  	text-align: right;
}
.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
</style>
<input type="hidden" id="BPOSPayCash" value="${uiLabelMap.BPOSPayCash}"/>
<input type="hidden" id="BPOSPayCashGiveChange" value="${uiLabelMap.BPOSPayCashGiveChange}"/>
<input type="hidden" id="BPOSTransactionTotalDue" value="${uiLabelMap.BPOSTransactionTotalDue}"/>
<input type="hidden" id="BPOSPayCashTotal" value="${uiLabelMap.BPOSPayCashTotal}"/>
<input type="hidden" id="BPOSPageTitlePayment" value="${uiLabelMap.BPOSPageTitlePayment}"/>
<input type="hidden" id="BPOSContinuePrint" value="${uiLabelMap.BPOSContinuePrint}" />
<div class="errorPosMessage"><span id="payCashFormServerError"></span></div>
<span id="cashTotalDue"></span>
<div id="alterpopupWindowPayCash" style="display: none;">
    <div style="background-color: #438EB9; border-color: #0077BC;"><b>${uiLabelMap.BPOSPageTitlePayment}</b></div>
    <div style="overflow: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span12'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCash}
				</div>
				<div class='span8'>
					<input type="text" id="amountCash" name="amountCash" onclick="this.select()">
					<input type="hidden" id="amountCashInput" />
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSCreditCard}
				</div>
				<div class='span8'>
					<input type="text" id="amountCreditCard" name="amountCreditCard" onclick="this.select()">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCashGiveChange}
				</div>
				<div class='span8'>
					<input type="text" id="paybackCash" name="paybackCash" disabled="disabled">
					<input type="hidden" id="paybackCashInput" />
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request) !>
					<#if productStoreId?exists && productStoreId?has_content>
						<#assign config = delegator.findOne("ConfigPrintOrderAndStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", productStoreId), false) !>
					</#if>
					<#if config?exists && config?has_content>
						<#if config.isPrintBeforePayment == "N">
							<button id="continuePrint" type="button" class='btn btn-mini btn-primary form-action-button pull-right'  onclick="payCashConfirm('Y')"><i class='icon-ok'></i> ${uiLabelMap.BPOSContinuePrint}</button>
						</#if>
					</#if>
					<button id="continueNotPrint" type="button" class='btn btn-mini btn-primary form-action-button pull-right'  onclick="payCashConfirm('N')"><i class='icon-ok'></i> ${uiLabelMap.BPOSPageTitlePayment}</button>
				</div>
			</div>
		</div>
        </div>
    </div>
</div>
<script>
var BPOSNotEnoughPay = "${StringUtil.wrapString(uiLabelMap.BPOSNotEnoughPay)}";
var BPOSAmountCreditCardBiggerTotalDue = "${StringUtil.wrapString(uiLabelMap.BPOSAmountCreditCardBiggerTotalDue)}";
$(document).ready(function(){
	$("#alterpopupWindowPayCash").jqxWindow({theme: 'olbius', modalZIndex: 10000, zIndex:10000, width: 550, height: 250, resizable: false, draggable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, showCloseButton: true,});
	$('#alterpopupWindowPayCash').on('open', function (event) { 
		flagPopup = false;
		$("#amountCash").focus();
		$("#amountCreditCard").maskMoney('mask', 0);
		$("#continueNotPrint").removeAttr("disabled");
	});
	
	$('#alterpopupWindowPayCash').on('close', function (event) { 
		flagPopup = true;
		flagDupPayCash = true;
	});
});

$("#continueNotPrint").click(function(){
	$("#continueNotPrint").attr("disabled", "disabled");
});
</script>