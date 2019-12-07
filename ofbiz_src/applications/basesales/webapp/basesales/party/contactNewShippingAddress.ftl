<#if !themePortrait?exists><#assign themePortrait = ""/></#if>
<#if !prefixName?exists><#assign prefixName = "wn"/></#if>
<div class="row-fluid">
	<div class="span12">
		<input type="hidden" id="${prefixName}_partyId" value="">
        <input type="hidden" id="${prefixName}_contactMechTypeId" value="POSTAL_ADDRESS">
        <input type="hidden" id="${prefixName}_contactMechPurposeTypeId" value="SHIPPING_LOCATION">
	    <#if themePortrait != "landscape">
	    	<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_toName">${uiLabelMap.BSPartyReceive}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_toName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_attnName">${uiLabelMap.BSOtherInfo}</label><#--DAOtherName-->
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_attnName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_countryGeoId" class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_stateProvinceGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_countyGeoId">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_wardGeoId">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required" for="${prefixName}_address1">${uiLabelMap.BSAddress}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_postalCode">${uiLabelMap.BSZipCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_postalCode" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid' style="display:none">
						<div class='span5'>
							<label for="${prefixName}_allowSolicitation">${uiLabelMap.BSContactAllowSolicitation}?</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_allowSolicitation"></div>
				   		</div>
					</div>
				</div><!--.span12-->
			</div><!--.row-fluid-->
	    <#else>
	    	<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_toName">${uiLabelMap.BSPartyReceive}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_toName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_attnName">${uiLabelMap.BSOtherInfo}</label><#--DAOtherName-->
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_attnName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_countryGeoId" class="required">${uiLabelMap.BSCountry}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_countryGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSStateProvince}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_stateProvinceGeoId"></div>
				   		</div>
					</div>
				</div><!--.span6-->
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_countyGeoId">${uiLabelMap.BSCounty}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_countyGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_wardGeoId">${uiLabelMap.BSWard}</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_wardGeoId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required" for="${prefixName}_address1">${uiLabelMap.BSAddress}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_address1" class="span12" maxlength="255" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="${prefixName}_postalCode">${uiLabelMap.BSZipCode}</label>
						</div>
						<div class='span7'>
							<input type="text" id="${prefixName}_postalCode" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid' style="display:none">
						<div class='span5'>
							<label for="${prefixName}_allowSolicitation">${uiLabelMap.BSContactAllowSolicitation}?</label>
						</div>
						<div class='span7'>
							<div id="${prefixName}_allowSolicitation"></div>
				   		</div>
					</div>
				</div><!--.span6-->
			</div><!--.row-fluid-->
	    </#if>
	</div>
</div>

<#include "script/contactNewShippingAddressScript.ftl"/>
