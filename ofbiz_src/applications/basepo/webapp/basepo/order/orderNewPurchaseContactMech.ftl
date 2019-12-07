<div style="position:relative">
	<div id="loader_page_common_load" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
			</div>
		</div>
	</div>
</div>

<div id="alterpopupWindowContactMechNew" style="display:none">
	<div id="headerAddress">${uiLabelMap.BPOCreateNewShippingAddress}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<input type="hidden" id="wn_partyId" value="">
			<input type="hidden" id="wn_contactMechTypeId" value="POSTAL_ADDRESS">
			<input type="hidden" id="wn_contactMechPurposeTypeId" value="SHIPPING_LOCATION">
			<div id="containerContactMech" style="background-color: transparent; overflow: auto;"></div>		
			<div id="jqxNotificationContactMech" style="margin-bottom:5px">		
				<div id="notificationContentContactMech">		
				</div>		
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class="row-fluid hideCTM">
						<div class="span5">
							<label for="wn_toName" style="margin-top: -1px;">${uiLabelMap.BPOPartyReceive}</label>
						</div>
						<div class="span7">
							<input type="text" id="wn_toName" class="span12" maxlength="100" value=""/>
						</div>
					</div>
					<div class="row-fluid hideCTM">
						<div class="span5">
							<label for="wn_attnName" style="margin-top: -1px;">${uiLabelMap.BPOOtherInfo}</label>
						</div>
						<div class="span7">
							<input type="text" id="wn_attnName" class="span12" maxlength="100" value=""/>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_countryGeoId" class="required" style="margin-top: -2px;">${uiLabelMap.BPOCountry}</label>
						</div>
						<div class="span7">
							<div id="wn_countryGeoId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label class="required" style="margin-top: -2px;">${uiLabelMap.BPOStateProvince}</label>
						</div>
						<div class="span7">
							<div id="wn_stateProvinceGeoId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_countyGeoId" class="required" style="margin-top: -2px;">${uiLabelMap.BPOCounty}</label>
						</div>
						<div class="span7">
							<div id="wn_countyGeoId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_wardGeoId" class="required" style="margin-top: -2px;">${uiLabelMap.BPOWard}</label>
						</div>
						<div class="span7">
							<div id="wn_wardGeoId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label class="required" for="wn_address1" style="margin-top: -2px;">${uiLabelMap.BPOAddress1}</label>
						</div>
						<div class="span7">
							<input type="text" id="wn_address1" class="span12" maxlength="255" value=""/>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_postalCode" style="margin-top: -2px;">${uiLabelMap.BPOZipCode}</label>
						</div>
						<div class="span7">
							<input type="text" id="wn_postalCode" class="span12" maxlength="60" value=""/>
						</div>
					</div>
				</div>
				<hr/>
				<div class="form-action">
					<div class="pull-right form-window-content-custom">
						<button id="alterSaveCTM" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
						<button id="alterCancelCTM" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/poresources/js/order/orderNewPurchaseContactMech.js"></script>