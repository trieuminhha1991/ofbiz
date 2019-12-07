<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>

<div class="account-create">
	<h1 class='account-title'><i class='fa fa-user'></i>&nbsp;&nbsp;${uiLabelMap.PartyEditPersonalInformation}</h1>
	<div class="account-form">
		<form method="post" action="<@ofbizUrl>createCustomerProfile</@ofbizUrl>" id="newuserform" name="newuserform" class='createUserForm'>
		<input type="hidden" name="productStoreId" value="${productStore.productStoreId}"/>	
			<div class='row no-margin-bottom'>
				<div class='col-lg-6 col-md-6'>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEPartyFullName}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="text" class="full-width-input " name="fullName" id="fullName" value="${(party.partyFullName)?if_exists}" />
										<input type="hidden" name="partyId" value="${partyId?if_exists}" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEDateOfBirth}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<div id="birthDateSelect"></div>
										<input id="birthDate" name="birthDate" type="hidden" value="${(party.birthDate)?if_exists}" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEGender}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<div class="choosegender" id="choosegender">
											<div class="gender">
												<input id="Radio1" type="radio" name="gender" value="M" <#if (party.gender)?exists && party.gender == "M" >checked</#if>>
												<label>${uiLabelMap.BEMale}</label>
											</div>
											<div class="gender no-padding-right">
												<input id="Radio2" type="radio" name="gender" value="F" <#if (party.gender)?exists && party.gender == "F" >checked</#if>>
												<label>${uiLabelMap.BEFemale}</label>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='col-lg-6 col-md-6'>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEPhone}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="text" class="full-width-input" name="phone" id="phone" placeholder="${uiLabelMap.BEPhone}" value="${contactNumber?if_exists}">
										<input type="hidden" name="contactNumberId" value="${contactNumberId?if_exists}" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEEmail}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="text" class="full-width-input" name="email" id="email" placeholder="${uiLabelMap.BEEmail}" value="${infoString?if_exists}">
										<input type="hidden" name="infoStringId" value="${infoStringId?if_exists}" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BECity}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<select name="city" id="city" class="full-width-input">
											<option value="">${uiLabelMap.BEChooseCity}</option>
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
						</div>
					</div>
					<div class='row'>
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEDistrict}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<select class="full-width-input" name="district" id="district">
											<option value="">${uiLabelMap.BEChooseDistrict}</option>
											<#if defaultDistrict?exists>
												<#list defaultDistrict as district>
													<option <#if (districtGeoId)?exists && districtGeoId == district.geoId>selected</#if> value="${district.geoId}">${district.geoName}</option>
												</#list>
											</#if>
										</select>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEAddress}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="text" class="full-width-input" id="address" name="address" placeholder="${uiLabelMap.BEAddress}" value="${address1?if_exists}" />
										<input type="hidden" name="postalAddressId" value="${postalAddressId?if_exists}" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='pull-right clearfix'>
						<button class='btn-submit'>
							<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.BESubmit}
						</button>
					</div>
				</div>
			</div>
			<div class='pull-right clearfix margin-top10'>
				(*)&nbsp;&nbsp;${uiLabelMap.BEEmailCanHelp}
			</div>
		</form>
	</div>
</div>
