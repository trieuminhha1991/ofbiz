<div id="alterpopupWindowNewProdCatalog" style="display:none">
	<div>${uiLabelMap.BSAddNewCatalogForStore}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSProdCatalogId}</label>
						</div>
						<div class='span7'>
							<div id="wn_pscata_prodCatalogId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_pscata_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_pscata_thruDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSequenceNumber}</label>
						</div>
						<div class='span7'>
							<div id="wn_pscata_sequenceNum"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_pscata_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_pscata_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var productStoreId = "${productStoreId?if_exists}";
</script>
<script type="text/javascript" src="/salesresources/js/setting/catalog/productStoreNewCatalog.js"></script>