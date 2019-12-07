<style type="text/css">
	.cio-custom {
		margin-left:5px;
		padding-left:22px; 
		padding-top:10px; 
		position:relative;
		margin-bottom:10px;
	}
	.cio-custom .cio-title {
		position:absolute; left:0
	}
	.cio-custom h4.cio-title {
		position:relative; margin-left:-22px;
		margin-top:0
	}
	.cio-custom label {
		font-size: 13.5px;
	    line-height: 26px;
	    text-align: left;
	}
	.cio-block-container .cio-inblock-container .widget-header {
		min-height:31px;
	}
	.cio-block-container .cio-inblock-container .widget-header h5 {
		line-height:31px;
	}
	.cio-block-container .cio-inblock-container .widget-header .widget-toolbar {
		line-height: 31px
	}
</style>
<div class="info-container cio-custom" id="generalInformation">
	<div class="cio-title">
		<i class="fa fa-phone-square fa-lg" title="${uiLabelMap.PhoneNumber}"></i>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="hide hide-group">${uiLabelMap.HomePhone}:</label>
					<label class="hide-person">${uiLabelMap.Phone1}:</label>
				</div>
				<div class="span7">
					<input type="number" id="txtHomePhone" class="info-input no-margin no-space" style="width: 95%" tabindex="15"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="hide hide-group">${uiLabelMap.OfficePhone}:</label>
					<label class="hide-person">${uiLabelMap.Phone3}:</label>
				</div>
				<div class="span7">
					<input type="number" id="txtOfficePhone" class="info-input no-margin" style="width: 95%" tabindex="17"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.DmsPrimaryPhone}:</label>
				</div>
				<div class="span7">
					<div id="txtPrimaryPhone" tabindex="19"></div>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="hide hide-group">${uiLabelMap.MobilePhone}:</label>
					<label class="hide-person">${uiLabelMap.Phone2}:</label>
				</div>
				<div class="span7">
					<input type="number" id="txtMobilePhone" class="info-input no-margin" style="width: 95%" tabindex="16"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.DmsEmail}:</label>
				</div>
				<div class="span7">
					<input type="email" id="txtEmail" class="info-input no-margin" tabindex="18"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label class="asterisk">${uiLabelMap.DmsShippingPhone}:</label>
				</div>
				<div class="span7">
					<div id="txtShippingPhone" tabindex="20"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="info-container cio-custom" id="generalInformation">
	<div class="cio-title">
		<i class="fa fa-facebook-square fa-lg" title="${uiLabelMap.SocialNetwork}"></i>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5">
					<label>${uiLabelMap.Facebook}:</label>
				</div>
				<div class="span7">
					<input id="txtFacebook" tabindex="9"/>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="info-container cio-custom" id="addressDetails">
	<div class="cio-title">
		<i class="fa fa-map-marker fa-lg" title="${uiLabelMap.Address}"></i>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5"><label>${uiLabelMap.DmsCountry}:</label></div>
				<div class="span7 relative">
					<div id="txtCountry2"></div>
					<i class="geo-created green hidden" id="txtCountryi" title="${uiLabelMap.DmsClickToAddNewRegion}"></i>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5"><label class="asterisk">${uiLabelMap.DmsProvince}:</label></div>
				<div class="span7 relative">
					<div id="txtProvince2"></div>
					<i class="geo-created green hidden" id="txtProvincei" title="${uiLabelMap.DmsClickToAddNewRegion}"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5"><label>${uiLabelMap.DmsCounty}:</label></div>
				<div class="span7 relative">
					<div id="txtCounty2"></div>
					<i class="geo-created green hidden" id="txtCountyi" title="${uiLabelMap.DmsClickToAddNewRegion}"></i>
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5"><label>${uiLabelMap.DmsWard}:</label></div>
				<div class="span7 relative">
					<div id="txtWard2"></div>
					<i class="geo-created green hidden" id="txtWardi" title="${uiLabelMap.DmsClickToAddNewRegion}"></i>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid" id="addressContainer">
		<div class="span6">
			<div class="row-fluid margin-bottom5">
				<div class="span5" id="addressInfoLabel"><label class="asterisk">${uiLabelMap.DmsAddress1}:</label></div>
				<div class="span7" id="addressInfo">
					<textarea rows="2" cols="50" id="tarAddress12" style="resize: none;margin: 0px !important;width: 272%;"></textarea>
				</div>
			</div>
		</div>
		<div class="span5 hide">
			<div class="row-fluid margin-bottom5">
				<div class="span5"><label>${uiLabelMap.CityPostalCode}:</label></div>
				<div class="span7"><input type="text" id="txtPostalCode" style="width: 169px;height: 16px;" /></div>
			</div>
		</div>
		<input type="hidden" id="postalCtm"/>
	</div>
	<div class="row-fluid margin-bottom5">
		<div class="span12">
			<div class="widget-box cio-inblock-container margin-right5">
				<div class="widget-header widget-header-blue widget-header-flat">
					<h5 class="smaller">${uiLabelMap.DmsListAddress}</h5>
					<div class="widget-toolbar">
						<a class="blue fa-check" style="cursor:pointer" id="savePrimaryAddress">&nbsp;</a>
						<a class="blue fa-plus" style="cursor:pointer" id="addAddress">&nbsp;</a>
						<a href="#otherAddress" role="button" data-toggle="collapse">
							<i class="fa-chevron-left"></i>
						</a>
					</div>
				</div>
				<div class="widget-body no-padding-top collapse" id="otherAddress">
					<div class="widget-main">
						<div class="zone info-zone" id="listAddress" style="margin-bottom: 0">
							<div id="jqxgridAddress"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="contextMenuAddress" style="display:none;">
	<ul>
		<li id="editAddress"><i class="fa-pencil"></i>&nbsp;&nbsp;${uiLabelMap.CommonEdit}</li>
		<li id="deleteAddress"><i class="red fa-trash-o"></i><a class="red">&nbsp;&nbsp;${uiLabelMap.CommonDelete}</a></li>
		<li id="setAsPrimary"><i class="fa-key"></i>&nbsp;&nbsp;${uiLabelMap.DmsSetAsPrimary}</li>
	</ul>
</div>