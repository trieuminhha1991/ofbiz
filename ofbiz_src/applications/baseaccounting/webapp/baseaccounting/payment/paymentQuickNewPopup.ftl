<div id="paymenApplPopup" class="hide">
	<div>${uiLabelMap.BACCNewPayment}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerPayment" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationPayment" style="margin-bottom:5px">
		        <div id="notificationContentPayment">
		        </div>
		    </div>
			<div class="row-fluid">
				<form id="formNewPayment">
					<#if businessType == "AR">
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
							</div>
							<div class="span7">
								<div class="container-add-plus">
									<input type="text" id="payPartyIdFrom">
									<button type="button" class="btn btn-primary btn-mini" id="changePaymentPartyBtn" title="${uiLabelMap.CommonEdit}" disabled><i class="icon-only icon-edit"></i></button>
								</div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentPartyIdTo}</label>
							</div>
							<div class="span7">
								<div id="payPartyIdTo">
									<div id="payPartyToGrid"></div>
								</div>
					   		</div>
						</div>
					<#else>
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
							</div>
							<div class="span7">
								<div id="payPartyIdFrom">
									<div id="payPartyFromGrid"></div>
								</div>
					   		</div>
						</div>	
						<div class='row-fluid margin-bottom5'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentPartyIdTo}</label>
							</div>
							<div class="span7">
								<input type="text" id="payPartyIdTo">
								<button type="button" class="btn btn-primary btn-mini" id="changePaymentPartyBtn" title="${uiLabelMap.CommonEdit}" disabled><i class="icon-only icon-edit"></i></button>
					   		</div>
						</div>
					</#if>
					
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.BACCOrganization}</label>
						</div>
						<div class='span7'>
							<input type="text" id="organizationName">
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.IdentifyCard}</label>
						</div>
						<div class='span7'>
							<input type="text" id="identifyCard">
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.IssuedDate}</label>
						</div>
						<div class='span7'>
							<div id="issuedDatePayment"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.IssuePlace}</label>
						</div>
						<div class='span7'>
							<input type="text" id="issuedPlace">
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="required">${uiLabelMap.BACCPaymentType}</label>
						</div>
						<div class='span7'>
							<div id="paymentTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="required">${uiLabelMap.BACCPaymentMethodType}</label>
						</div>
						<div class='span7'>
							<div id="paymentMethodId"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label class="required">${uiLabelMap.BACCAmount}</label>
						</div>
						<div class='span7'>
							<div class="row-fluid">
								<div class="span12">
									<div class="span7">
										<div id="amount"></div>
									</div>
									<div class="span5">
										<div id="payCurrencyUomId"></div>
									</div>
								</div>
							</div>
				   		</div>
					</div>
                    <div class='row-fluid margin-bottom5 hide' id="divConversionFactor">
                        <div class='span5 text-algin-right'>
                            <label class="required">${uiLabelMap.BACCExchangedRate}</label>
                        </div>
                        <div class='span7'>
                            <div class="row-fluid">
                                <div class="span12">
                                    <div id="conversionFactor"></div>
                                </div>
                            </div>
                        </div>
                    </div>
					<div class='row-fluid margin-bottom5'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.BACCDescription}</label>
						</div>
						<div class='span7'>
							<textarea id="comments" class="text-popup" style="width: 92% !important"></textarea>
				   		</div>
					</div>
				</form>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="saveCreatePaymentAppl" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="cancelCreatePaymentAppl" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="PaymentPartyEditWindow" class="hide">
	<div>
		<#if businessType == "AR">
			${uiLabelMap.BACCPaymentPartyIdFrom}
		<#else>
			${uiLabelMap.BACCPaymentPartyIdTo}
		</#if>
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom5'>
				<div id="enumPartyTypeId"></div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div id="paymentPartyDropDown">
					<div id="paymentPartyGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="saveEditParty" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="cancelEditParty" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/invoice/invoiceViewPaymentAppl.js?v=20170318"></script>
	
