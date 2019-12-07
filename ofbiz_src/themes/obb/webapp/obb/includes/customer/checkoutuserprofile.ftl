<div class='widget-wrapper checkout-userprofile'>
	<div class='widget-header'>
		<i class='fa fa-map-marker marker'>&nbsp;</i>
		<h3>${uiLabelMap.BEShippingAddress}</h3>
	</div>
	<div class='widget-body'>
		<form action="<@ofbizUrl>submitUserInfo</@ofbizUrl>" method="POST" id="confirmcart" class="createUserForm">
			<input type="hidden" name="productStoreId" value="${productStore.productStoreId}"/>
			<input type="hidden" name="shipBeforeDate"/>
			<input type="hidden" name="checkoutpage" value="shippinginfo" />
			<div class='row'>
				<div class='col-lg-3 col-md-3'>
					<div class='pull-left'>
						<label class='title pull-left' style="margin-top: 3px;padding-right: 22px;">${uiLabelMap.BEGender}</label>
					</div>
				</div>
				<div class='col-lg-9 col-md-9'>
					<div class="choosegender" id="choosegender">
						<div class="gender">
							<input id="Radio1" type="radio" name="gender" value="M" <#if (party.gender)?exists && party.gender == "M" >checked</#if>>
							<label>${uiLabelMap.BEMale}</label>
						</div>
						<div class="gender">
							<input id="Radio2" type="radio" name="gender" value="F" <#if (party.gender)?exists && party.gender == "F" >checked</#if>>
							<label>${uiLabelMap.BEFemale}</label>
						</div>
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BEPartyFullName}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<input type="text" class="full-width-input" value="${(party.partyFullName)?if_exists}"
							name="name" id="fullname" placeholder="${uiLabelMap.BEPartyFullName}">
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BEPhone}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<input type="text" class="full-width-input" value="<#if contactNumber?exists>${contactNumber}<#else>${request.getAttribute("contact")?if_exists}</#if>"
							name="phone" id="phone" placeholder="${uiLabelMap.BEPhone}">
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BEEmail}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<input type="text" class="full-width-input" value="<#if infoString?exists>${infoString}<#else>${request.getAttribute("email")?if_exists}</#if>"
							name="email" id="email" placeholder="${uiLabelMap.BEEmail}">
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BECity}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<select name="city" id="city" class="full-width-input">
							<#if province?exists>
								<#list province as city>
									<option <#if (stateProvinceGeoId)?exists && stateProvinceGeoId == city.geoId>selected</#if>
									value="${city.geoId}">${city.geoName}</option>
								</#list>
							</#if>
						</select>
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BEDistrict}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<select class="full-width-input" name="district" id="district">
							<#if defaultDistrict?exists>
								<#list defaultDistrict as district>
									<option <#if (districtGeoId)?exists && districtGeoId == district.geoId>selected</#if> value="${district.geoId}">${district.geoName}</option>
								</#list>
							</#if>
						</select>
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left'>${uiLabelMap.BEAddress}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<input type="text" class="full-width-input" value="${address1?if_exists}"
							id="address" name="address" placeholder="${uiLabelMap.BEAddress}">
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left long-title'>${uiLabelMap.BEShippingBeforeDate}</label>
				</div>
				<div class="col-lg-9 col-md-9">
					<div class="input-container">
						<div id="ShippingBeforeDate"></div>
					</div>
				</div>
			</div>
			<div class='row'>
				<div class="col-lg-3 col-md-3">
					<label class='title pull-left long-title'>${uiLabelMap.BEExtraInformation}</label>
				</div>
				<div class='col-lg-9 col-md-9'>
					<input type="text" placeholder="${uiLabelMap.BEExtraInformation}" class="full-width-input" name="shipping_instructions">
				</div>
			</div>
			<div class='row no-margin'>
				<div class='pull-right'>
					<button class='btn-submit margin-top-10'>
						<i class='fa fa-check marginright-5'></i>
						${uiLabelMap.BESubmit}
					</button>
				</div>
			</div>
		</form>
	</div>
</div>
