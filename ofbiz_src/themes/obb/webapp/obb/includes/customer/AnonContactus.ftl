<script type="text/javascript" language="JavaScript">
	<!--
	function reloadCaptcha(fieldName) {
		var captchaUri = "<@ofbizUrl>captcha.jpg?captchaCodeId=" + fieldName + "&amp;unique=_PLACEHOLDER_</@ofbizUrl>";
		var unique = Date.now();
		captchaUri = captchaUri.replace("_PLACEHOLDER_", unique);
		document.getElementById(fieldName).src = captchaUri;
	}

	//-->
</script>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div class="inner clearfix">
			<div id="jm-current-content" class="clearfix">
				<div class="jm-contacts">
					<div class="page-title">
						<h3>${uiLabelMap.CommonContactUs}</h3>
					</div>
					<style type="text/css">
						input {
							width: 300px;
						}
						table td {
							padding: 5px;;
						}
						table div {
							width: 120px;
						}
					</style>
					<div class="ct-contacts clearfix">
						<div class="contact-form">
							<div class="contact-inner">
								<form id="contactForm" method="post" action="<@ofbizUrl>submitAnonContact</@ofbizUrl>">
									<input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
									<input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
									<input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
									<input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
									<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
									<input type="hidden" name="emailType" value="CONT_NOTI_EMAIL" />
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="subject" class="required"><em>*</em>${uiLabelMap.ObbSubject}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<input type="text" name="subject" id="subject" class="full-width-input"
												value="${requestParameters.subject?if_exists}"/>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="message" class="required"><em>*</em>${uiLabelMap.CommonMessage}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<textarea name="content" id="message" class="full-width-input" cols="45" rows="5">${requestParameters.content?if_exists}</textarea>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="emailAddress" class="required"><em>*</em>${uiLabelMap.FormFieldTitle_emailAddress}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<input type="text" name="emailAddress" id="emailAddress" class="full-width-input" value="${requestParameters.emailAddress?if_exists}"/>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="firstName" class="required">${uiLabelMap.PartyFirstName}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<input type="text" name="firstName" id="firstName" class="full-width-input" value="${requestParameters.firstName?if_exists}"/>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="lastName" class="required">${uiLabelMap.PartyLastName}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<input type="text" name="lastName" id="lastName" class="full-width-input" value="${requestParameters.lastName?if_exists}"/>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="captchaImage" class="required">${uiLabelMap.CommonCaptchaCode}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<div>
												<img id="captchaImage" src="<@ofbizUrl>captcha.jpg?captchaCodeId=captchaImage&amp;unique=${nowTimestamp.getTime()}</@ofbizUrl>" alt="" />
											</div>
											<a href="javascript:reloadCaptcha('captchaImage');">${uiLabelMap.CommonReloadCaptchaCode}</a>
										</div>
									</div>
									<div class='row contact-row'>
										<div class='col-lg-3 col-md-3 col-sm-4'>
											<label for="captchaImage" class="required"><em>*</em>${uiLabelMap.CommonVerifyCaptchaCode}</label>
										</div>
										<div class='col-lg-9 col-md-9 col-sm-8'>
											<input type="text" autocomplete="off" maxlength="30" name="captcha" class="full-width-input"/>
										</div>
									</div>
									<div class='pull-right button-action clearfix'>
										<button type="submit" title="${uiLabelMap.CommonSubmit}" class="button pull-right">
											<span><span>${uiLabelMap.CommonSubmit}</span></span>
										</button>
										<p class="required  pull-right marginright-10">
											* Trường bắt buộc
										</p>
										<input type="text" name="hideit" id="hideit" value="" style="display:none !important;">
									</div>
								</form>
								<script type="text/javascript">
									//<![CDATA[
									// var dataFormPromo = new VarienForm('contactForm', true);
									//]]>
								</script>
							</div>
						</div>
						<div class="contact-info">
							<div class="inner">
								<center>
								<iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d5248.720782590947!2d2.332451662314348!3d48.87040590815822!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47e66e3bcba3deb1%3A0xa204c2b159245312!2s2+Rue+de+Marivaux%2C+75002+Paris%2C+Ph%C3%A1p!5e0!3m2!1svi!2s!4v1457503620135" width="600" height="450" frameborder="0" style="border:0" allowfullscreen></iframe>
								</center>
								<div class="info-inner">
									<p>
										${uiLabelMap.ObbAboutUsContent2}
									</p>
									<div class="info-inner2">
										<ul class="list-info">
											<li class="address">
												<em class="fa fa-home">&nbsp;</em><span>Địa chỉ: </span>2 rue Marivaux, Paris, France
											</li>
											<li class="phone">
												<em class="fa fa-phone">&nbsp;</em><span>Điện thoại: </span>+33 763 571 929
											</li>
											<li class="email">
												<em class="fa fa-envelope">&nbsp;</em><span>Thư điện tử: </span><a href="mailto:bhappy.vn.contact@gmail.com">bhappy.vn.contact@gmail.com</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
