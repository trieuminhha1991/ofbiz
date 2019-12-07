<div id="newPaymentPopup" class="hide">
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
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<#if businessType == "AR">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class='required'>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
										</div>
										<div class="span7">
											<div id="enumPartyTypeId"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class=''></label>
										</div>
										<div class="span7">
											<div id="payPartyIdFrom" style="float: left; display: inline-block;">
												<div id="payPartyFromGrid"></div>
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
											<div id="enumPartyTypeId"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class=''></label>
										</div>
										<div class="span6">
											<div id="payPartyIdTo" style="float: left; display: inline-block;">
												<div id="payPartyToGrid"></div>
											</div>											
								   		</div>
								   		<div class="span1 no-left-margin">
											<button type="button" class="btn btn-primary btn-mini" id="addPaymentInfoBtn" style="float: left; margin-left: 3px" 
												title="${uiLabelMap.AdditionalInformationPayment}"><i class="icon-only fa-info-circle"></i></button>
										</div>								   		
									</div>
								</#if>
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
                                <div class='row-fluid margin-bottom5 hide' id="conversionFactorDiv">
                                    <div class='span5 text-algin-right'>
                                        <label class="required">${uiLabelMap.BACCConversionFactor}</label>
                                    </div>
                                    <div class='span7'>
                                        <div id="conversionFactor"></div>
                                    </div>
                                </div>
								<div class="taxCodeContainer hide">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.AmountNotTaxInclude}</label>
										</div>
										<div class='span7'>
											<div id="amountNotTaxInc"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.BACCPercent}</label>
										</div>
										<div class='span7'>
											<div id="taxRate"></div>
								   		</div>
									</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class='asterisk'>${uiLabelMap.BACCEffectiveDate}</label>
									</div>
									<div class="span7">
										<div id="effectiveDate"></div>
							   		</div>
								</div>
								<div class="taxCodeContainer hide">
									<div class='row-fluid margin-bottom5'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
										</div>
										<div class="span7">
											<input type="text" id="voucherForm" >
										</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
										</div>
										<div class="span7">
											<input type="text" id="voucherSerial">
										</div>
									</div>
								</div>
							</div><!-- ./span6 -->
							<div class="span6">
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
								<div class="taxCodeContainer hide">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class="required">${uiLabelMap.accTaxAuthorityRateTypeId}</label>
										</div>
										<div class='span7'>
											<div id="productTaxDropDownBtn">
												<div id="productIdTaxGrid" style="border-color: transparent;"></div>
											</div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.BACCTaxAmount}</label>
										</div>
										<div class='span7'>
											<div id="taxAmount"></div>
								   		</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class='span5 text-algin-right'>
										<label>${uiLabelMap.BACCDescription}</label>
									</div>
									<div class='span7'>
										<textarea id="comments" class="text-popup" style="width: 93% !important; height: 80px"></textarea>
							   		</div>
								</div>
								<div class="taxCodeContainer hide">
									<div class='row-fluid margin-bottom5'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
										</div>
										<div class="span7">
											<input type="text" id="voucherNumber">
										</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}</label>
										</div>
										<div class="span7">
											<div id="issuedDateVoucher"></div>
										</div>
									</div>
								</div>
							</div><!-- ./span6 -->
						</div><!-- ./span12 -->
					</div><!-- ./row-fluid -->
					<div class="legend-container">
						<span>${uiLabelMap.BSOtherInfo}</span>
						<hr/>
					</div>
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<#if businessType == "AR">
											<label class="">${uiLabelMap.BACCPayers}</label>											
										<#else>
											<label class="">${uiLabelMap.BACCPayeeName}</label>
										</#if>
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
							</div><!-- ./span6 -->
							<div class="span6">
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.IssuedDate}</label>
									</div>
									<div class='span7'>
										<div id="issuedDate"></div>
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
							</div><!-- ./span6 -->
						</div><!-- ./span12 -->
					</div><!-- ./row-fluid -->
				</form>
			</div><!-- ./row-fluid -->
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<div id="AddInfoPaymentWindow" class="hide">
	<div>${uiLabelMap.AdditionalInformationPayment}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">
						<#if businessType == "AP">
							${StringUtil.wrapString(uiLabelMap.SellerName)}
						<#else>	
							${StringUtil.wrapString(uiLabelMap.BuyerName)}
						</#if>
					</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentPartyName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentTaxCode">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentBankCode">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AtTheBank)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentAtTheBank">
				</div>
			</div>		
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonCountry)}</label>
				</div>
				<div class="span8">
					<div id="infoPaymentCountry"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCStateProviceShort)}</label>
				</div>
				<div class="span8">
					<div id="infoPaymentState"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentAddress">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoPaymentPhoneNbr">
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddInfoPayment">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddInfoPayment">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			<button type="button" class='btn btn-success form-action-button pull-right' id="clearAddInfoPayment">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.wgdeletedata}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxformattedinput.js"></script>
<script type="text/javascript" src="/accresources/js/payment/paymentNew.js?v=0.0.2"></script>
<script type="text/javascript" src="/accresources/js/payment/paymentNewPartyInfo.js"></script>