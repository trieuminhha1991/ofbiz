<div class="account-create">
	<h1 class='account-title'><i class='fa fa-user'></i>&nbsp;&nbsp;${uiLabelMap.BECreateAccount}</h1>
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
										<input type="text" class="full-width-input " name="fullName" id="fullName" placeholder="${uiLabelMap.BEPartyFullName}">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class="col-lg-12">
							<div class='row no-margin-bottom'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEDateOfBirth}</label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class='row'>
										<div class='col-lg-5 col-md-5 no-padding-right'>
											<div id="birthDateSelect"></div>
											<input id="birthDate" name="birthDate" type="hidden" />
										</div>
										<div class='col-lg-7 col-md-7'>
											<div class="choosegender" id="choosegender">
												<div class="gender">
													<input id="Radio1" type="radio" name="gender" value="M" <#if (basicInfo.gender)?exists && basicInfo.gender == "M" >checked</#if>>
													<label>${uiLabelMap.BEMale}</label>
												</div>
												<div class="gender no-padding-right">
													<input id="Radio2" type="radio" name="gender" value="F" <#if (basicInfo.gender)?exists && basicInfo.gender == "F" >checked</#if>>
													<label>${uiLabelMap.BEFemale}</label>
												</div>
											</div>
										</div>
									</div>

								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEUsername}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="text" class="full-width-input no-space" name="username" id="username" placeholder="${uiLabelMap.BEUsername}">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BEPassword}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="password" class="full-width-input " name="password" id="password" placeholder="${uiLabelMap.BEPasswordHint}">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-12">
							<div class='row'>
								<div class="col-lg-4 col-md-5">
									<label class='title pull-right mobile-pull-left'>${uiLabelMap.BERepeatPassword}&nbsp;<i class='fa fa-asterisk asterisk'></i></label>
								</div>
								<div class="col-lg-8 col-md-7">
									<div class="input-container">
										<input type="password" class="full-width-input " name="passwordVerify" id="RepeatPassword" placeholder="${uiLabelMap.BERepeatPassword}">
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
										<input type="text" class="full-width-input" name="phone" id="phone" placeholder="${uiLabelMap.BEPhone}">
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
										<input type="text" class="full-width-input" name="email" id="email" placeholder="${uiLabelMap.BEEmail}">
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
											<option <#if (profile.city)?exists && profile.city == city.geoId>selected</#if>
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
												<option <#if (profile.district)?exists && profile.district == district.geoId>selected</#if> value="${district.geoId}">${district.geoName}</option>
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
										<input type="text" class="full-width-input" id="address" name="address" placeholder="${uiLabelMap.BEAddress}">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class='pull-right clearfix'>
						<button class='btn-submit'>
							<i class='fa fa-check'>&nbsp;</i>&nbsp;${uiLabelMap.EcommerceRegister}
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
