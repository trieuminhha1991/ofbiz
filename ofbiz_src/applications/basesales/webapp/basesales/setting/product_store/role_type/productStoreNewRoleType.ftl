<div id="popupProdStoreNewRole" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewRoleTypeForStore)}</div>
	<div style="overflow: hidden;">
		<form id="ProductStoreRoleForm" class="form-horizontal">
			<div class="row-fluid">
				<div class="row-fluid">
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSPartyId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="wn_psrole_partyId">
								<div id="wn_psrole_partyGrid"></div>
							</div>
						</div>
					</div>

					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSRoleTypeId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="wn_psrole_roleTypeId"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.BSFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="wn_psrole_fromDate"></div>
						</div>
					</div>
					
					<div class="row-fluid no-left-margin m-bot-5">
						<label class="span5 line-height-25 align-right">${uiLabelMap.BSThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="wn_psrole_thruDate"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	var productStoreId = '${productStoreId?if_exists}';
</script>
<script type="text/javascript" src="/salesresources/js/setting/product_store/productStoreNewRoleType.js?v=0.001"></script>
