<script>
	var GlobalMessagesJS = (function(){
		function globalMessagesJS(){};
		globalMessagesJS.prototype.BPOSCannotConnectThirdPartyService = "${uiLabelMap.BPOSCannotConnectThirdPartyService}";
		globalMessagesJS.prototype.POSorderItemSeqId = "${uiLabelMap.POSorderItemSeqId}";
		globalMessagesJS.prototype.POSproductCode = "${uiLabelMap.POSproductCode}";
		globalMessagesJS.prototype.POSitemDescription = "${uiLabelMap.POSitemDescription}";
		globalMessagesJS.prototype.POSalternativeQuantity = "${uiLabelMap.POSalternativeQuantity}";
		globalMessagesJS.prototype.POSquantityUomId = "${uiLabelMap.POSquantityUomId}";
		globalMessagesJS.prototype.POSalternativeUnitPrice = "${uiLabelMap.POSalternativeUnitPrice}";
		globalMessagesJS.prototype.POSreturnableQuantity = "${uiLabelMap.POSreturnableQuantity}";
		globalMessagesJS.prototype.POSreturnQuantity = "${uiLabelMap.POSreturnQuantity}";
		globalMessagesJS.prototype.POSErrorPopupTitle = "${uiLabelMap.POSErrorPopupTitle}";
		globalMessagesJS.prototype.BPOSOK = "${uiLabelMap.BPOSOK}";
		globalMessagesJS.prototype.BPOSCancel = "${uiLabelMap.BPOSCancel}";
		globalMessagesJS.prototype.POSReturnItemEmpty = "${uiLabelMap.POSReturnItemEmpty}";
		globalMessagesJS.prototype.BPOSAreYouSureReturnThisOrder = "${uiLabelMap.BPOSAreYouSureReturnThisOrder}";
		globalMessagesJS.prototype.POSReturnGreater = "${uiLabelMap.POSReturnGreater}";
		globalMessagesJS.prototype.POSWrongReturnQuantity = "${uiLabelMap.POSWrongReturnQuantity}";
		globalMessagesJS.prototype.POSamount = "${uiLabelMap.POSamount}";
		globalMessagesJS.prototype.POSOrderAdjustmentId = "${uiLabelMap.POSOrderAdjustmentId}";
		globalMessagesJS.prototype.POSTax = "${uiLabelMap.POSTax}";
		globalMessagesJS.prototype.POSPromoName = "${uiLabelMap.POSPromoName}";
		globalMessagesJS.prototype.POSReturnItemList = "${uiLabelMap.POSReturnItemList}";
		globalMessagesJS.prototype.POSPromotionItemList = "${uiLabelMap.POSPromotionItemList}";
		globalMessagesJS.prototype.POSPromotionProductItemList = "${uiLabelMap.POSPromotionProductItemList}";
		return new globalMessagesJS();
	})();
	var userLoginId = '${userLogin.userLoginId}';
</script>
<script type="text/javascript" src="/posresources/js/ParReturn.js?v=1.0.1"></script>
<@jqOlbCoreLib hasGrid=true/>
<div id="jqxwindowParReturn" style="display:none;">
	<div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.POSReturnItemList}</div>
	<div>
		<div class="row-fluid form-window-content">
			<div class="span12">
				<div class='row-fluid'>
					<div id="jqxParReturn"></div>
				</div>
				<br />
				<div class="row-fluid">
					<div class="span3">
						<h2>${uiLabelMap.POSReturnGrandTotal}:</h2>
					</div>
					<div class="span9 total-money-popup">
						<div id="jqxParReturnGrandTotal"></div>
					</div>
				</div>
				<br/>
				<div class='row-fluid'>
					<div class="span6">
						<div id="jqxParAdjustmentReturn"></div>
					</div>
					<div class="span6">
						<div id="jqxParReturnItem"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right">
				<button id="btnOkParReturn" tabindex="9" class="btn btn-primary form-action-button"><i class="fa icon-ok"></i> ${uiLabelMap.BPOSOK}</button>
				<button id="btnCancelParReturn" tabindex="9" class="btn btn-danger form-action-button"><i class="fa icon-remove"></i> ${uiLabelMap.BSClose}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxwindowConfirmReturn" style="display:none;">
	<div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.POSReturnConfirm}</div>
	<div>
		<div class="row-fluid form-window-content">
			<div class="span12">
				<h4>${uiLabelMap.BSCfParReturnChoice}</h4>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right">
				<button id="btncfOkParReturn" class="btn btn-primary form-action-button"><i class="fa icon-ok"></i> ${uiLabelMap.BSCfOkParReturn}</button>
				<button id="btncfParReturn" class="btn btn-success"><i class="fa icon-ok"></i> ${uiLabelMap.BSCfParReturn}</button>
				<button id="btncfCancelParReturn" class="btn btn-danger form-action-button"><i class="fa icon-remove"></i> ${uiLabelMap.BSClose}</button>
			</div>
		</div>
	</div>
</div>
<div id="parReturnWindowPayCash" style="display: none;">
    <div style="background-color: #438EB9; border-color: #0077BC;"><b>${uiLabelMap.BPOSPageTitlePayment}</b></div>
    <div style="overflow: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span12'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCash}
				</div>
				<div class='span8'>
					<input type="text" id="parReturnCashAmount" name="parReturnCashAmount" onclick="this.select()">
					<input type="hidden" id="parReturnCashAmountInput"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCashGiveChange}
				</div>
				<div class='span8'>
					<input type="text" id="parReturnCashBackAmount" name="parReturnCashBackAmount" disabled="disabled">
					<input type="hidden" id="parReturnCashBackAmountInput"/>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="parReturnbtn" type="button" class='btn btn-mini btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSContinuePrint}</button>
					<button id="parReturnPrintbtn" type="button" class='btn btn-mini btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSPageTitlePayment}</button>
				</div>
			</div>
		</div>
        </div>
    </div>
</div>
<div id="wholeReturnWindowPayCash" style="display: none;">
    <div style="background-color: #438EB9; border-color: #0077BC;"><b>${uiLabelMap.BPOSPageTitlePayment}</b></div>
    <div style="overflow: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span12'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right total-money' style="vertical-align: middle;">
					${uiLabelMap.BPOSTotalCost}
				</div>
				<div class='span8 total-money' id="grandTotalLbl">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCash}
				</div>
				<div class='span8'>
					<input type="text" id="wholeReturnCashAmount" name="parReturnCashAmount" onclick="this.select()">
					<input type="hidden" id="wholeReturnCashAmountInput"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right' style="vertical-align: middle;line-height: 40px;font-size: 16px;">
					${uiLabelMap.BPOSPayCashGiveChange}
				</div>
				<div class='span8'>
					<input type="text" id="wholeReturnCashBackAmount" name="parReturnCashBackAmount" disabled="disabled">
					<input type="hidden" id="wholeReturnCashBackAmountInput"/>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="wholeReturnbtn" type="button" class='btn btn-mini btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSContinuePrint}</button>
					<button id="wholeReturnPrintbtn" type="button" class='btn btn-mini btn-primary form-action-button pull-right'><i class='icon-ok'></i> ${uiLabelMap.BPOSPageTitlePayment}</button>
				</div>
			</div>
		</div>
        </div>
    </div>
</div>
<style>
	#wholeReturnWindowPayCash .total-money{
		margin-top: 8px;
	}
	#parReturnCashAmount, #parReturnCashBackAmount, #wholeReturnCashAmount, #wholeReturnCashBackAmount{
		height: 40px;
		width: 100%;
		font-size: 25px;
	  	font-weight: bold;
	  	color: #cc0000;
	  	text-align: right;
	}
	.jqx-grid-olbius div[id^="content"] {
	    border-right: 1px solid #CCC !important;
	    border-left: 1px solid #CCC !important;
	    border-bottom: 1px solid #CCC !important;
	}
	.jqx-grid-olbius .jqx-grid-content {
	    width: calc(100% - 3px) !important;
	}
	.jqx-grid-olbius {
	    border: none;
	}
	.jqx-border-reset {
	    border: none !important;
	}
	.jqx-popup-olbius .jqx-background-reset{
		border: none !important;
	}
	.jqx-popup-olbius .jqx-scrollbar-olbius{
		display: none !important;
	}
	.jqx-popup-olbius .jqx-grid-toolbar-olbius{
		background-image: linear-gradient(to bottom, white, white) !important;
		border: none;
	}
	.total-money-popup{
		color: #cc0000;
	    font-size: 28px;
	    font-weight: bold;
	    text-transform: uppercase;
	    margin-top: 10px;
	    margin-left: -20px !important;
	}
	.innerReturnGridRight{
		padding: 4px 4px 0px 4px;
		text-align: right;
	}
	.innerReturnGridLeft{
		padding: 4px 4px 0px 4px;
		text-align: left;
	}
	.margin-right-3px{
		margin-right: 3px;
	}
	.margin-left10{
		margin-left: 10px;
	}
	.jqx-popup .jqx-grid-pager input{
		color: #333;
		background-color: #fff;
		border-radius: 0!important;
		box-shadow: none;
		border: 1px solid #ccc;
	}
	.jqx-fill-state-pressed-olbius{
		color: #333 !important;
	}
	.jqx-popup .jqx-listbox .jqx-fill-state-pressed-olbius{
		background-color: #4383B4 !important;
	}
	.jqx-popup .jqx-listitem-element{
		color: #333;
	}
	.jqx-popup .jqx-button-olbius{
		background-image: linear-gradient(to bottom, #CCC, #CCC);
	}
	.jqx-popup .jqx-grid-pager{
		color: #444;
		background: #e8e8e8;
	}
	.jqx-popup .jqx-dropdownlist-state-normal-olbius{
		border-color: #CCC;
		color: #333;
		background-image: linear-gradient(to bottom, #CCC, #CCC);
	}
	.jqx-popup .widget-header h4{
		font-size: 18px;
    	font-weight: normal;
    	font-family: inherit;
    	color: inherit;
    	text-rendering: optimizelegibility;
    	display: block;
    	-webkit-margin-before: 1.33em;
	    -webkit-margin-after: 1.33em;
	    -webkit-margin-start: 0px;
	    -webkit-margin-end: 0px;
	    margin-top: 0px !important;
	    float:none !important;
	}
	.jqx-popup .widget-header{
		border: 0;
	    border-bottom: 1px solid #dce8f1;
	    color: #4383b4;
	    padding-left: 3px;
	    height: 30px !important;
	}
</style>