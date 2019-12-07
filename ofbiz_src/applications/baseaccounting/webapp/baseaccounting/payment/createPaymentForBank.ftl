<div id="createPaymentWindow" class="hide">
	<div>${uiLabelMap.BACCNewPayment}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
					<ul class="wizard-steps wizard-steps-square">
						<li data-target="#step1" class="active">
					        <span class="step">1. ${uiLabelMap.BACCPaymentInformation}</span>
					    </li>
					    <li data-target="#step2">
					        <span class="step">2. ${uiLabelMap.BACCPaymentInvoiceApplied}</span>
					    </li>
					</ul>
				</div><!--#fuelux-wizard-->
				<div class="step-content row-fluid position-relative" id="step-container">
					<div class="step-pane active" id="step1">
						<div class="row-fluid" style="margin-top: 10px">
							<div class="row-fluid">
								<form class="form-horizontal form-window-content-custom">
									<div class="span12">
										<div class="span6">
											<div class='row-fluid'>
												<div class='span5'>
													<label class='asterisk'>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
												</div>
												<div class="span7">
													<div id="partyIdFromDropDownBtn">
														<div id="partyIdFromGrid" style="border-color: transparent;"></div>
													</div>
													<input type="hidden" id="partyIdFromHidden" />
										   		</div>
											</div>
											<div class='row-fluid'>
												<div class='span5'>
													<label class='asterisk'>${uiLabelMap.BACCPaymentPartyIdTo}</label>
												</div>
												<div class="span7">
													<div id="partyIdTo"></div>
										   		</div>
											</div>
											<div class='row-fluid margin-bottom5'>
												<div class='span5 text-algin-right'>
													<label class="asterisk">${uiLabelMap.BACCAmount}</label>
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
											<div class='row-fluid'>
												<div class='span5'>
													<label class='asterisk'>${uiLabelMap.BACCEffectiveDate}</label>
												</div>
												<div class="span7">
													<div id="effectiveDate"></div>
										   		</div>
											</div>
										</div><!-- ./span6 -->
										<div class="span6">
											<div class='row-fluid'>
												<div class='span5'>
													<label class="asterisk">${uiLabelMap.BACCPaymentType}</label>
												</div>
												<div class='span7'>
													<div id="paymentTypeId"></div>
										   		</div>
											</div>
											<div class='row-fluid'>
												<div class='span5'>
													<label class="asterisk">${uiLabelMap.BACCPaymentMethodTypeShort}</label>
												</div>
												<div class='span7'>
													<div id="paymentMethodId"></div>
										   		</div>
											</div>
											<div class='row-fluid'>
												<div class='span5'>
													<label>${uiLabelMap.BACCDescription}</label>
												</div>
												<div class='span7'>
													<textarea id="comments" class="text-popup" style="width: 93% !important; height: 60px"></textarea>
										   		</div>
											</div>
										</div><!-- ./span6 -->
									</div><!-- ./span12 -->
								</form>
							</div><!-- ./row-fluid -->
							<div class="legend-container">
								<span>${uiLabelMap.BSOtherInfo}</span>
								<hr/>
							</div>
							<div class="row-fluid">
								<form class="form-horizontal form-window-content-custom">
									<div class="span12">
										<div class="span6">
											<div class='row-fluid '>
												<div class='span5 '>
													<label class="" id="payerPayeeInfo"></label>
												</div>
												<div class='span7'>
													<input type="text" id="organizationName">
										   		</div>
											</div>
											<div class='row-fluid '>
												<div class='span5 '>
													<label class="">${uiLabelMap.IdentifyCard}</label>
												</div>
												<div class='span7'>
													<input type="text" id="identifyCard">
										   		</div>
											</div>
										</div><!-- ./span6 -->
										<div class="span6">
											<div class='row-fluid '>
												<div class='span5 '>
													<label class="">${uiLabelMap.IssuedDate}</label>
												</div>
												<div class='span7'>
													<div id="issuedDate"></div>
										   		</div>
											</div>
											<div class='row-fluid '>
												<div class='span5 '>
													<label class="">${uiLabelMap.IssuePlace}</label>
												</div>
												<div class='span7'>
													<input type="text" id="issuedPlace">
										   		</div>
											</div>
										</div><!-- ./span6 -->
									</div><!-- ./span12 -->
								</form>
							</div><!-- ./row-fluid -->
						</div>
					</div><!-- ./step1 -->
					<div class="step-pane" id="step2">
						<div class="row-fluid">
							<div id="invoiceListGrid"></div>
						</div>	
					</div><!-- ./step2 -->
				</div><!-- ./step-container -->
				<div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.BACCSave)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div><!-- ./form-action -->
			</div>
		</div>
	</div>	
</div>
<div id="invoiceCtxMenu" class="hide">
	<ul>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>
<script type="text/javascript" src="/accresources/js/payment/createPaymentForBank.js?v=0.0.3"></script>