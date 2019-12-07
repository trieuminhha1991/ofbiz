<style type="text/css">
.jqx-widget-content{
	font-family: 'Roboto';
}
#endingDrawerCashAmount{
	height: 30px;
	font-size: 20px;
  	font-weight: bold;
  	color: #cc0000;
  	text-align: right;
}
</style>
<div id="alterpopupWindowClose" style="display: none;">
    <div style="background-color: #438EB9; border-color: #0077BC;">${uiLabelMap.BPOSFinsihShift}</div>
    <div style="overflow: hidden;">
	<div class='row-fluid form-window-content'>
		<div class='span12'>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right asterisk' style="padding-top: 5px; font-size: 14px;">
					${uiLabelMap.BPOSAmount}
				</div>
				<div class='span8'>
					<input type="text" id="endingDrawerCashAmount" name="endingDrawerCashAmount" onclick="this.select()" style="width: 100%">
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" type="button" class='btn btn-mini btn-danger form-action-button pull-right' onclick="cancelCloseterminal()"><i class='icon-remove'></i> ${uiLabelMap.BPOSCancel}</button>
					<button id="alterSave" type="button" class='btn btn-mini btn-primary form-action-button pull-right'  onclick="checkAmountCloseTerminal()"><i class='icon-ok'></i> ${uiLabelMap.BPOSSubmit}</button>
				</div>
			</div>
		</div>
        </div>
    </div>
</div>
<script type="text/javascript">
	var BPOSAreYouSureClose = "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureCloseTerminal)}";
	var BPOSEnterAmountClose = "${StringUtil.wrapString(uiLabelMap.BPOSEnterAmountClose)}";
	$(document).ready(function(){
		$("#alterpopupWindowClose").jqxWindow({theme: 'olbius',width: 450, height: 135, resizable: false, draggable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, showCloseButton: false,});
		$('#alterpopupWindowClose').on('open', function (event) { 
			flagPopup = false;
			$("#endingDrawerCashAmount").focus();
		});
		$('#alterpopupWindowClose').on('close', function (event) { 
			flagPopup = true;
		});
	});
</script>