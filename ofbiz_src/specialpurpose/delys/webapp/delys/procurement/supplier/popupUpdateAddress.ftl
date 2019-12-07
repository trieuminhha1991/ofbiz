<div id="updateAddressPopup" class='hide'>
	<div>${uiLabelMap.updateAddress}</div>
	<div>
		<input id="partyIdUpdateAddress" type="hidden"/>
		<div class="row-fluid">
			<div class='span12'>
				<div class='row-fluid margin-bottom8'>
					<div class='span5 align-right asterisk'>${uiLabelMap.DACountry}:</div>
					<div class='span7'>
						<div id="countryUpdate" class='country'></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom8'>
					<div class='span5 align-right'>${uiLabelMap.DAStateProvince}:</div>
					<div class='span7'>
						<div id="provinceUpdate" class='province'></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom8'>
					<div class='span5 align-right'>${uiLabelMap.DACountyGeoId}:</div>
					<div class='span7'>
						<div id="districtUpdate" class='district'></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom8'>
					<div class='span5 align-right'>${uiLabelMap.DAArea}:</div>
					<div class='span7'>
						<div id="wardUpdate" class='ward'></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom8'>
					<div class='span5 align-right asterisk'>${uiLabelMap.Address}:</div>
					<div class='span7'>
						<input id="addressUpdate" class='address'/>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12">
				<button id="saveAddress" class='btn btn-primary btn-mini pull-right'>${uiLabelMap.save}</button>
			</div>
		</div>
	</div>
</div>