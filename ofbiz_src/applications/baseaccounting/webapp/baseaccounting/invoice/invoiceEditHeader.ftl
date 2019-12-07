<div id="editInvoiceWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class="row-fluid">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCInvoiceId}</label>
								</div>
								<div class="span7">
									<label id="updateInvoiceId" style="color: #037c07" class="align-left"><b>${parameters.invoiceId}</b></label>
						   		</div>
							</div>					
							<div class="row-fluid">
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCInvoiceDateShort}</label>
								</div>
								<div class="span7">
									<div id="updateInvoiceDate"></div>
						   		</div>
							</div>			
							<div class="row-fluid" style="position: relative;">
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCPaidDate}</label>
								</div>
								<div class="span7">
									<div id="updateInvoicePaidDate"></div>
						   		</div>
							</div>			
							<div class="row-fluid">
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCDueDate}</label>
								</div>
								<div class="span7">
									<div id="updateInvoiceDueDate"></div>
						   		</div>
							</div>			
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.BACCInvoiceTypeId}</label>
								</div>
								<div class="span8">
									<label id="updateInvoiceType" style="color: #037c07" class="align-left">
										<#if !invoiceType?exists>
											<#assign invoiceType = delegator.findOne("InvoiceType",{"invoiceTypeId" : "${invoice.invoiceTypeId?if_exists}"},false)!>
										</#if>
										<b>${StringUtil.wrapString(invoiceType.get("description", locale)?if_exists)}</b>
									</label>
						   		</div>
							</div>
							<#if businessType == "AP">
								<div class='row-fluid' style="position: relative;" id="updatePartyFromContainerOld">
									<div class='span4'>
										<label class=''>${uiLabelMap.BACCInvoiceFromParty}</label>
									</div>
									<div class="span8">
										<input type="text" id="updateInvoicePartyFrom">
										<a id="partyFromEditBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
											title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}" style="right: 0px;">
											<i class="fa fa-pencil blue"></i></a>
							   		</div>
								</div>
								<div id="updatePartyFromContainerNew" class="hide">
									<div class='row-fluid' style="position: relative;">
										<div class='span4'>
											<label class=''>${uiLabelMap.BACCOrganization}</label>
										</div>
										<div class="span8">
											<div id="updateEnumPartyTypeId"></div>
											<a id="cancelEditPartyFromBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
												title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}" style="right: 0px;">
												<i class="icon-remove blue"></i></a>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class='span4'>
											<label class=''></label>
										</div>
										<div class="span8">
											<div id="updateInvOrgId">
												<div id="updateInvOrgGrid"></div>
											</div>
								   		</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class='span4'>
										<label class=''>${uiLabelMap.BACCInvoiceToParty}</label>
									</div>
									<div class="span8">
										<input type="text" id="updateInvoicePartyTo">
							   		</div>
								</div>
                                <div class='row-fluid'>
                                    <div class='span4'>
                                        <label class=''>${uiLabelMap.BACCExchangedRate}</label>
                                    </div>
                                    <div class="span8">
                                        <div id="conversionFactorEdit"></div>
                                    </div>
                                </div>
							<#elseif businessType == "AR">
								<div class='row-fluid'>
									<div class='span4'>
										<label class=''>${uiLabelMap.BACCInvoiceFromParty}</label>
									</div>
									<div class="span8">
										<input type="text" id="updateInvoicePartyFrom">
							   		</div>
								</div>
								<div class='row-fluid' style="position: relative;" id="updatePartyToContainerOld">
									<div class='span4'>
										<label class=''>${uiLabelMap.BACCInvoiceToParty}</label>
									</div>
									<div class="span8">
										<input type="text" id="updateInvoicePartyTo">
										<a id="partyToEditBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
											title="${StringUtil.wrapString(uiLabelMap.CommonEdit)}" style="right: 0px;">
											<i class="fa fa-pencil blue"></i></a>
							   		</div>
								</div>	
								<div id="updatePartyToContainerNew" class="hide">
									<div class='row-fluid' style="position: relative;">
										<div class='span4'>
											<label class=''>${uiLabelMap.BACCInvoiceToParty}</label>
										</div>
										<div class="span8">
											<div id="updateEnumPartyTypeId"></div>
											<a id="cancelEditPartyToBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
												title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}" style="right: 0px;">
												<i class="icon-remove blue"></i></a>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class='span4'>
											<label class=''></label>
										</div>
										<div class="span8">
											<div id="updateInvCustomerId">
												<div id="updateInvCusGrid"></div>
											</div>
								   		</div>
									</div>
								</div>						
							</#if>	
						</div><!-- ./span6 -->
					</div>
				</div><!-- ./row-fluid -->
				<div class="legend-container">
					<span>${StringUtil.wrapString(uiLabelMap.BSOtherInfo)}</span>
					<hr>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="span6">
							<div class="row-fluid">
								<div class="span5">
									<label>
									<#if invoiceRootType == "SALES_INVOICE">
										${uiLabelMap.BuyerName}
									<#elseif invoiceRootType == "PURCHASE_INVOICE">	
										${uiLabelMap.SellerName}
									</#if>
									</label>
								</div>
								<div class="span7">
									<input type="text" id="updateInvoicePartyName">
								</div>
							</div>
							<div class="row-fluid">
								<div class="span5">
									<label>${uiLabelMap.BACCTaxCode}</label>
								</div>
								<div class="span7">
									<input type="text" id="updateInvoiceTaxCode">
								</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class="row-fluid">
								<div class="span4">
									<label>${uiLabelMap.CommonAddress1}</label>
								</div>
								<div class="span8">
									<input type="text" id="updateInvoiceAddress">
								</div>
							</div>
							<div class="row-fluid">
								<div class="span4">
									<label>${uiLabelMap.PartyPhoneNumber}</label>
								</div>
								<div class="span8">
									<input type="text" id="updateInvoicePhoneNbr">
								</div>
							</div>
						</div><!-- ./span6 -->
					</div>
				</div><!-- ./row-fluid -->
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelUpdateInvoice">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdateInvoice">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/invoice/invoiceEditHeader.js"></script>