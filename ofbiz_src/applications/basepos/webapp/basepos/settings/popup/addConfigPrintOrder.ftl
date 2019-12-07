<script type="text/javascript" src="/posresources/js/setting/addConfigPrintOrder.js"></script>


<div id="alterpopupWindow" class="hide">
<div>${uiLabelMap.BPOSPrintOrderPOS}</div>
<div style="overflow: hidden;">
	<div id="splitterConfigPrint">
		<div style="overflow: hidden !important;">
			<div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSProductStore}</label></div>
					<div class="span7"><div id="txtProductStore"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSFontFamily}</label></div>
					<div class="span7"><div id="txtFontFamily"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSHeaderFontSize}</label></div>
					<div class="span7"><div id="txtHeaderFontSize"></div></div>
				</div>
				
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSInfoFontSize}</label></div>
					<div class="span7"><div id="txtInfoFontSize"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSContentFontSize}</label></div>
					<div class="span7"><div id="txtContentFontSize"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right asterisk margin-top2">${uiLabelMap.BPOSSelectTypePrint}</label></div>
					<div class="span7"><div id="txtTypePrint"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5"><label class="align-right margin-top2">${uiLabelMap.BPOSLogo}</label></div>
					<div class="span7"><input type="file" id="txtLogo" accept="image/*"/></div>
				</div>
			</div>
			<div class="form-action">
				<button id="alterCancel" class="btn btn-danger form-action-button pull-right" style="height: 30px; line-height: 1px; margin-right: 5px;"><i class="fa-remove"></i> ${uiLabelMap.BPOSCancel}</button>
				<button id="alterSave" class="btn btn-primary form-action-button pull-right" style="height: 30px; line-height: 1px; margin-right: 5px;"><i class="fa-check"></i> ${uiLabelMap.BPOSSave}</button>
				<button id="alterPrint" class="btn btn-info form-action-button pull-right" style="height: 30px; line-height: 1px; margin-right: 5px;"><i class="fa-print"></i> ${uiLabelMap.POSPrint}</button>
			</div>
		</div>
		<div>
			<#include "previewPrintOrder.ftl"/>
		</div>
	</div>
</div>
</div>