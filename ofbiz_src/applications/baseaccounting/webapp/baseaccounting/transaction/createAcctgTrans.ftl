<div id="CreateAcctgTransWindow" class="hide">
	<div>${uiLabelMap.CreateNewTransaction}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="form-legend" style="margin-bottom: 10px">
					<div class="contain-legend">
						<span class="content-legend" >
							${StringUtil.wrapString(uiLabelMap.BACCGeneralInfo)}
						</span>
					</div>
					<div class="row-fluid">
						<div class='span12 margin-top5'>
							<div class='span6'>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.BACCAcctgTransId)}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div class="span6">
												<input type="text" id="addAcctgTransId">
											</div>
											<div class="span6">
												<div id="isAutoIncrement" style="margin-left: 5px !important; margin-top: 5px"><span style="font-size: 14px">${uiLabelMap.BACCAutoIncrement}</span></div>
											</div>
										</div>
									</div>
								</div>	
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCAcctgTransTypeId)}</label>
									</div>
									<div class="span8">
										<div id="addAcctgTransType"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCPostedDate)}</label>
									</div>
									<div class="span8">
										<div id="addPostedDate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.BACCGlJournalId)}</label>
									</div>
									<div class="span8">
										<div id="addGlJournalId"></div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.CommonStatus)}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div class="span6">
												<div id="transPosted" style="margin-top: 2px">${StringUtil.wrapString(uiLabelMap.BACCPostted)}</div>
											</div>
											<div class="span6">
												<div id="transNotPosted" style="margin-top: 2px">${StringUtil.wrapString(uiLabelMap.BACCNotPostted)}</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							
							<div class='span6'>
								<div class='row-fluid margin-bottom5'>
									<div class='span4 text-algin-right'>
										<label class=''>${uiLabelMap.DAParty}</label>
									</div>
									<div class="span8">
										<div id="enumPartyTypeId"></div>
							   		</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class='span4'>
										<label class=''></label>
									</div>
									<div class="span8">
										<div id="partyDropDownBtn">
											<div id="addPartyGrid"></div>
										</div>
							   		</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}</label>
									</div>
									<div class="span8">
										<div id="invoiceDropDownBtn">
											<div id="addInvoiceGrid" style="border-color: transparent;"></div>
										</div>
									</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.BACCPaymentId)}</label>
									</div>
									<div class="span8">
										<div id="paymentDropDownBtn">
											<div id="addPaymentGrid" style="border-color: transparent;"></div>
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${StringUtil.wrapString(uiLabelMap.Shipment)}</label>
									</div>
									<div class="span8">
										<div id="shipmentDropDownBtn">
											<div id="addShipmentGrid" style="border-color: transparent;"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span12 margin-top5'>
								<div class='span2 text-algin-right'>
									<label>${uiLabelMap.BACCDescription}</label>
								</div>
								<div class='span10'>
									<textarea id="description" class="text-popup" style="width: 96.5% !important; height: 40px"></textarea>
						   		</div>
						   	</div>
					   	</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="acctgTransEntryGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddTransaction">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddTransaction">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddTransaction">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="addNewAcctgTranEntryWindow" class="hide">
	<div>${uiLabelMap.wgaddnew}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCDebitAccount)}</label>
				</div>
				<div class="span8">
					<div id="transEntryDebitGlAccDropDown">
						<div id="transEntryDebitGlAccGrid"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.AccountingComments)}</label>
				</div>
				<div class="span8">
					<input type="text" id="acctgTransEntryDebitDesc">
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCCreditAccount)}</label>
				</div>
				<div class="span8">
					<div id="transEntryCreditGlAccDropDown">
						<div id="transEntryCreditGlAccGrid"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.AccountingComments)}</label>
				</div>
				<div class="span8">
					<input type="text" id="acctgTransEntryCreditDesc">
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCAmount)}</label>
				</div>
				<div class="span8">
					<div id="transEntryAmount"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class='span4 text-algin-right'>
					<label class=''>${uiLabelMap.DAParty}</label>
				</div>
				<div class="span8">
					<div id="transEntryEnumPartyTypeId"></div>
		   		</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class=""></label>
				</div>
				<div class="span8">
					<div id="partyTransEntryDropDownBtn">
						<div id="addPartyTransEntryGrid"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom5'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCProduct)}</label>
				</div>
				<div class="span8">
					<div id="prodTransEntryDropDownBtn">
						<div id="addProdTransEntryGrid" style="border-color: transparent;"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddTransEntry">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddTransEntry">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddTransEntry">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/transaction/createAcctgTrans.js?v=0.0.2"></script>