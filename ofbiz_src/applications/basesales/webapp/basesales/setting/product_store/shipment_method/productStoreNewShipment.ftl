<div id="alterpopupWindowShipmentMethodNew" style="display: none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewShipmentMethodForStore)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSShipmentMethodTypeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_shipmentMethodTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSequenceNumber}</label>
						</div>
						<div class='span7'>
							<div id="wn_sequenceNumber"></div>
				   		</div>
					</div>
				</div><!-- .span12 -->
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="wn_alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	var productStoreId2 = '${productStoreId?if_exists}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var validateEmpty = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var validateSquenceNum = '${uiLabelMap.BSSequenceNumValidate}';
	var validateSequence = '${StringUtil.wrapString(uiLabelMap.BSValidateSequence)}';
	var validateValue = '${StringUtil.wrapString(uiLabelMap.BSValidateValue)}';
	var validateMinmax = '${StringUtil.wrapString(uiLabelMap.BSValidateMinMax)}';
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var addNew4 = '${StringUtil.wrapString(uiLabelMap.BSAddNewShipmentMethodForStore)}';
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
</script>
<script type="text/javascript" src="/salesresources/js/setting/productStoreNewShipment.js"></script>