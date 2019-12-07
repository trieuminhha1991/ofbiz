<div id="editPaymentPopup" class="hide">
	<div>${uiLabelMap.BACCPaymentEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form id="formNewPayment">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class=''>${uiLabelMap.BACCPaymentId}</label>
									</div>
									<div class="span7">
										<div class="div-inline-block">
											<label style="color: #037c07"><b>
												<#if payment.paymentCode?exists>
													${payment.paymentCode}
												<#else>
													${parameters.paymentId}
												</#if>
											</b></label>
										</div>
							   		</div>
								</div>
								<#if businessType == "AR">
									<div id="updatePartyFromContainerOld" class='row-fluid' style="position: relative;">
										<div class="row-fluid margin-bottom5">
											<div class='span5 text-algin-right'>
												<label class=''>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
											</div>
											<div class="span7">
												<input type="text" id="updatePayPartyFrom">
												<a id="partyFromEditBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
													title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}" style="right: 0px;">
													<i class="fa fa-pencil blue"></i></a>
									   		</div>
										</div>
									</div>
									<div id="updatePartyFromContainerNew" style="position: relative;" class="hide">
										<div class='row-fluid margin-bottom5'>
											<div class='span5 text-algin-right'>
												<label class=''>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
											</div>
											<div class="span7">
												<div id="updateEnumPartyTypeId"></div>
												<a id="cancelEditPartyFromBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
													title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}" style="right: 0px;">
													<i class="icon-remove blue"></i></a>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom5'>
											<div class='span5 text-algin-right'>
												<label class=''></label>
											</div>
											<div class="span7">
												<div id="payPartyFromDropDown">
													<div id="payPartyFromGrid"></div>
												</div>
									   		</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class=''>${uiLabelMap.BACCPaymentPartyIdTo}</label>
										</div>
										<div class="span7">
											<input type="text" id="updatePayPartyTo">
								   		</div>
									</div>
								<#else>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class=''>${uiLabelMap.BACCPaymentPartyIdFrom}</label>
										</div>
										<div class="span7">
											<input type="text" id="updatePayPartyFrom">
								   		</div>
									</div>	
									<div id="updatePartyToContainerOld" class='row-fluid' style="position: relative;">
										<div class='row-fluid margin-bottom5'>
											<div class='span5 text-algin-right'>
												<label class=''>${uiLabelMap.BACCPaymentPartyIdTo}</label>
											</div>
											<div class="span7">
												<input type="text" id="updatePayPartyTo">	
												<a id="partyToEditBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
													title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}" style="right: 0px;">
													<i class="fa fa-pencil blue"></i></a>								
									   		</div>
										</div>
									</div>
									<div id="updatePartyToContainerNew" style="position: relative;" class="hide">
										<div class='row-fluid margin-bottom5'>
											<div class='span5 text-algin-right'>
												<label class=''>${uiLabelMap.BACCPaymentPartyIdTo}</label>
											</div>
											<div class="span7">
												<div id="updateEnumPartyTypeId"></div>
												<a id="cancelEditPartyToBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
													title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}" style="right: 0px;">
													<i class="icon-remove blue"></i></a>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom5'>
											<div class='span5 text-algin-right'>
												<label class=''></label>
											</div>
											<div class="span7">
												<div id="payPartyToDropDown">
													<div id="payPartyToGrid"></div>
												</div>
									   		</div>
										</div>
									</div>
								</#if>
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.BACCAmount}</label>
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
                                <div class='row-fluid margin-bottom5 hide' id="divExchangedRateEdit">
                                    <div class='span5 text-algin-right'>
                                        <label class="">${uiLabelMap.BACCExchangedRate}</label>
                                    </div>
                                    <div class='span7'>
                                        <div class="row-fluid">
                                            <div class="span12">
                                                <div id="conversionFactorEdit"></div>
                                            </div>
                                        </div>
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
										<label class=''>${uiLabelMap.BACCEffectiveDate}</label>
									</div>
									<div class="span7">
										<div id="effectiveDate"></div>
							   		</div>
								</div>
                                <div class='row-fluid margin-bottom5'>
                                    <div class='span5 text-algin-right'>
                                        <label class=''>${uiLabelMap.BACCPaidDate}</label>
                                    </div>
                                    <div class="span7">
                                        <div id="paidDate"></div>
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
										<label class=''>${uiLabelMap.CommonStatus}</label>
									</div>
									<div class="span7">
										<div class="div-inline-block">
											<label style="color: #037c07"><b>
												<#if payment.statusId?has_content>
													<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", payment.statusId), false)/>
													${statusItem.get('description',locale)?default('')}
												</#if>
											</b></label>
										</div>
							   		</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.BACCPaymentType}</label>
									</div>
									<div class='span7'>
										<div id="paymentTypeId"></div>
							   		</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.BACCPaymentMethodType}</label>
									</div>
									<div class='span7'>
										<div id="paymentMethodId"></div>
							   		</div>
								</div>
								<div class="taxCodeContainer hide">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.accTaxAuthorityRateTypeId}</label>
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
										<textarea id="comments" class="text-popup" style="width: 92% !important; height: 80px"></textarea>
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
										<label class="">${uiLabelMap.BACCIssuedDate}</label>
									</div>
									<div class='span7'>
										<div id="issuedDate"></div>
							   		</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.BACCIssuePlace}</label>
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
	   			<button id="saveUpdatePayment" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="cancelUpdatePayment" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxformattedinput.js"></script>
<script type="text/javascript" src="/accresources/js/payment/paymentEdit.js?v=0.0.2"></script>