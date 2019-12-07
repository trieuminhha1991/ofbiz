<div id="alterpopupWindowEdit" style="display : none;">
	<div>
		${uiLabelMap.CommonEdit}
	</div>
	<div style="overflow: hidden;">
		<form id="ProductStoreShipmentEditForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.BSShipmentMethodTypeId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="productStoreShipMethIdEdit"></div>
						</div>
					</div>
				</div>	
				<div class="row-fluid">
					<div class="row-fluid span6">
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMinWeight}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="minWeightEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMinSize}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="minSizeEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMinTotal}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="minTotalEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSConfigProps}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<input id="configPropsEdit" />
							</div>
						</div>	
					</div>
					<div class="row-fluid span6">
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMaxWeight}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="maxWeightEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMaxSize}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="maxSizeEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSMaxTotal}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="maxTotalEdit"></div>
							</div>
						</div>
						
						<div class="row-fluid no-left-margin m-bot-5">
							<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSSequenceNumber}</label>
							<div class="span7" style="margin-bottom: 10px;">
								<div id="sequenceNumberEdit"></div> 
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Edit}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script>
	var productIdd = '${productStoreId?if_exists}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var addNew1 = "${StringUtil.wrapString(uiLabelMap.BSAddNewRoleTypeForStore)}";
	var updatePopup2 = "${StringUtil.wrapString(uiLabelMap.BSUpdateShipmentMethodForStore)}";
</script>
<script type="text/javascript" src="/salesresources/js/setting/settingUpdateProductStoreShipment.js"></script>