<#include 'script/facilityNewFacilityAddressScript.ftl'/>
<form class="form-horizontal form-window-content-custom" id="initFacilityAddress" name="initFacilityAddress">
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span5">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Country} </div>
					</div>
					<div class="span8">	
						<div id="countryGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Provinces} </div>
					</div>
					<div class="span8">	
						<div id="provinceGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.County} </div>
					</div>
					<div class="span8">	
						<div id="districtGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Ward} </div>
					</div>
					<div class="span8">	
						<div id="wardGeoId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.PhoneNumber} </div>
					</div>
					<div class="span8">	
						<input id="phoneNumber"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Address} </div>
					</div>
					<div class="span8" style="text-align: left">
						<textarea id="address" name="address" data-maxlength="250" rows="4" style="resize: vertical; margin-top:0px" class="span12"></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>