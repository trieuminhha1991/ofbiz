<div class='widget-wrapper checkout-paymemt'>
	<div class='widget-header'>
		<i class='fa fa-money marker hidden-mobile'>&nbsp;</i>
		<h3>${uiLabelMap.BEChoosePaymentMethod}</h3>
	</div>
	<div class='widget-body'>
		<#if shoppingCart?exists>
		<#assign cart = shoppingCart?if_exists />
		<#assign items = cart.items()/>
		<div id="step2" class='step-container'>
			<div class="infocart">
				<form name='finalcheckout' action="<@ofbizUrl>checkoutorder</@ofbizUrl>" id="checkoutInfoForm" method="POST">
					<input type="hidden" name="checkoutpage" value="confirm" />
					<input type="hidden" name="partyId" value="${request.getAttribute("partyId")?if_exists}" />
					<input type="hidden" name="contactMechId" value="${request.getAttribute("contactMechId")?if_exists}" />
					<!-- <input type="hidden" name="shipBeforeDate" value=""/> -->
					<input type="hidden" name="shipBeforeDate" value="${request.getAttribute("shipBeforeDate")?if_exists}"/>
					<input type="hidden"  class="full-width-input" name="shipping_instructions" value="${request.getAttribute("shipping_instructions")?if_exists}">
					<div class='row payment-method'>
						<div class='col-lg-1 col-md-1 col-xs-2 payment-input'>
							<input type="radio" id="checkOutPaymentId_COD" name="checkOutPaymentId" value="EXT_COD" checked/>
						</div>
						<div class='col-lg-2 col-md-3 col-xs-0 payment-logo hidden-mobile'>
							<img src="/obbresources/images/cod.png"/>
						</div>
						<div class='col-lg-9 col-md-8 col-xs-10 payment-title'>
							<div class=' payment-title-content'>
								<label for="checkOutPaymentId_COD">${uiLabelMap.BECod}</label>
								<p>${uiLabelMap.BECodDescription}</p>
							</div>
						</div>
					</div>
					<div class='row payment-method'>
						<div class='col-lg-1 col-md-1 col-xs-2 payment-input'>
							<input type="radio" id="checkOutPaymentId_OFFLINE" name="checkOutPaymentId" value="EXT_OFFLINE"/>
						</div>
						<div class='col-lg-2 col-md-3 col-xs-0 payment-logo hidden-mobile'>
							<img src="/obbresources/images/atm.png"/>
						</div>
						<div class='col-lg-9 col-md-8 col-xs-10 payment-title'>
							<div class=' payment-title-content'>
								<label for="checkOutPaymentId_OFFLINE">${uiLabelMap.BEInternetBanking}</label>
								<p>${uiLabelMap.BEInternetBankingDescription}</p>
							</div>
						</div>
					</div>
					<div class='row'>
						<div class='col-lg-12 col-md-12 col-xs-12'>
							<button class='btn-primary pull-right'>
								${uiLabelMap.BECheckout}
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		</#if>
	</div>
</div>
