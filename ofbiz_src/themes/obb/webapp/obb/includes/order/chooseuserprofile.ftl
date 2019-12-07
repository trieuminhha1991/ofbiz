<div class='chooseprofile'>	
<#include "/obb/webapp/obb/includes/common/messageError.ftl"/>
	<div class='row'>
		<div class='col-lg-12 col-md-12'>
			<div class="customer">
				<input id="Radio1" type="radio" class='customer-chosen' name="customerType" value="NEW">
				<label>${uiLabelMap.BENewCustomer}</label>
			</div>
			<div class="customer">
				<input id="Radio2" type="radio" class='customer-chosen' name="customerType"  value="EXIST" checked>
				<label>${uiLabelMap.BEAlreadyBeCustomer}</label>
			</div>
		</div>
	</div>
	<form action="<@ofbizUrl>checkoutorder</@ofbizUrl>" method="post" id="NoLoginForm"  style="display: none">
		<input type="hidden" name="checkoutpage" value="shippinginfo"/>
		<div class='row'>
			<div class='col-lg-12'>
				<label class='title'>${uiLabelMap.BEPleaseEnterPhoneEmail}</label>
				<input class="full-width-input" name="contact" placeholder="${uiLabelMap.BEPhoneEmail}"/>
				<input class="full-width-input" name="email" type="hidden"/>
				<input class="full-width-input" name="phone" type="hidden"/>
			</div>
		</div>
		<button class='btn-primary btn-login' id="continueCheckout">
			${uiLabelMap.BEContinueCheckout}
		</button>
	</form>
	<form action="<@ofbizUrl>checkoutlogin</@ofbizUrl>" method="post" id="LoginForm">
		<input type="hidden" name="isSubmit" value="Y" />	
		<div class='row'>
			<div class='col-lg-12'>
				<input class="full-width-input" name="USERNAME" placeholder="${uiLabelMap.BEUsername}"/>
			</div>
		</div>
		<div class='row'>
			<div class='col-lg-12 col-md-12'>
				<input class="full-width-input title" name="PASSWORD" id="password" type="password" placeholder="${uiLabelMap.BEPassword}"/>
				<a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>">${uiLabelMap.BEForgotPassword}</a>
			</div>
		</div>
		<button class='btn-primary btn-login' id="Login">
			${uiLabelMap.BELogin}
		</button>
	</form>
</div>