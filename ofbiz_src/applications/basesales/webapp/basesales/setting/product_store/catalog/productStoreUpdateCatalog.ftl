<div id="alterpopupWindowEdit" style="display : none;">
	<div>
		${uiLabelMap.BSEdit}
	</div>
	<div style="overflow: hidden;">
		<form id="ProStoCatEditForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.BSProdCatalogId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="prodCatalogIdEdit"></div>
						</div>
					</div>

					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right line-height-25">${uiLabelMap.BSFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="fromDateEdit"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="thruDateEdit"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 align-right line-height-25">${uiLabelMap.BSSequenceNum}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="sequenceNumEdit"></div>
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
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	var validateFromDate1 = '${uiLabelMap.BSFromDateNotBeEmpty}';
	var validateFromDate2 = '${uiLabelMap.BSFromDateValidate}';
	var validateSquenceNum = '${uiLabelMap.BSSequenceNumValidate}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var validateProductCatalog = '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmpty)}';
	var productStoreId = "${productStoreId?if_exists}";
	var addNew3 = '${StringUtil.wrapString(uiLabelMap.BSAddNewCatalogForStore)}';
	var updatePopup = '${StringUtil.wrapString(uiLabelMap.BSUpdateCatalogForStore)}';
</script>
<script type="text/javascript" src="/salesresources/js/setting/settingUpdateProductStoreCatalog.js"></script>