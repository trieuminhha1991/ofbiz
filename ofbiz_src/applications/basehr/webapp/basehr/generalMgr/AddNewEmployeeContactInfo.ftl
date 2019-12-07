<style>
	.boder-all-profile{
		padding-bottom: 10px
	}
</style>
<div class="row-fluid">
	<div class="span12">
		<div class="span6">
			<div class="row-fluid" id="permanentResidence">
				<div class="span12 boder-all-profile">
					<span class="text-header">${uiLabelMap.PermanentResidence}</span>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.CommonCountry}</label>
						</div>
						<div class="span8">
							<div id="countryGeoIdPermRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.CommonCity}</label>
						</div>
						<div class="span8">
							<div id="stateGeoIdPermRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
						</div>
						<div class="span8">
							<div id="countyGeoIdPermRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.DmsWard}</label>
						</div>
						<div class="span8">
							<div id="wardGeoIdPermRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="">${uiLabelMap.CommonAddress1}</label>
						</div>  
						<div class="span8">
							<input id="paddress1${defaultSuffix}" type="text">
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span12 boder-all-profile" id="currentResidence">
					<span class="text-header">
						${uiLabelMap.CurrentResidence}
						<button title="${StringUtil.wrapString(uiLabelMap.CopyPermanentResidence)}" id="copyPermRes" class="grid-action-button fa-files-o" style="margin: 0; padding: 0 !important"></button>
					</span>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.CommonCountry}</label>
						</div>
						<div class="span8">
							<div id="countryGeoIdCurrRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.CommonCity}</label>
						</div>
						<div class="span8">
							<div id="stateGeoIdCurrRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
						</div>
						<div class="span8">
							<div id="countyGeoIdCurrRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.DmsWard}</label>
						</div>
						<div class="span8">
							<div id="wardGeoIdCurrRes${defaultSuffix}"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.CommonAddress1}</label>
						</div>
						<div class="span8">
							<input type="text" id="address1CurrRes${defaultSuffix}">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div class="span12">
		<div class="span6">
			<div class="row-fluid">
				<div class="span12 boder-all-profile" id="phoneNumberContact${defaultSuffix}">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.PhoneNumber}</label>
						</div>
						<div class="span8">
							<input id="phoneNumber${defaultSuffix}" type="text">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span12 boder-all-profile" id="emailContact${defaultSuffix}">
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="">${uiLabelMap.HRCommonEmail}</label>
						</div>
						<div class="span8">
							<input id="email${defaultSuffix}" type="text">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>