<form class="form-horizontal form-window-content-custom" id="initInvoiceEntry" name="initInvoiceEntry">
	<div class="row-fluid">
		<div class="span12">
			<div class="span5">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCInvoiceType}</label>
					</div>
					<div class="span7">
						<div id="invoiceTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid hide' id="glAccountTypeIdDiv">
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCGlAccountTypeLiability}</label>
					</div>
					<div class="span7">
						<div id="glAccountTypeId">
							<div id="glAccountTypeGrid"></div>
						</div>
			   		</div>
				</div>
                <div class='row-fluid hide' id="conversionFactorDiv">
                    <div class='span5'>
                        <label class='required'>${uiLabelMap.BACCExchangedRate}</label>
                    </div>
                    <div class="span7">
                        <div id="conversionFactor">
                        </div>
                    </div>
                </div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCInvoiceDate}</label>
					</div>
					<div class="span7">
						<div id="invoiceDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCInvoiceDueDate}</label>
					</div>
					<div class="span7">
						<div id="dueDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCDescription}</label>
					</div>
					<div class="span7">
						<div class="row-fluid">
							<textarea id="description" style="width: 91%; float: left;"></textarea>
						</div>
			   		</div>
				</div>			
			</div>
			<div class="span7">
				<#if businessType == "AP">
					<div class='row-fluid'>
						<div class='span5'>
							<label class='required'>${uiLabelMap.BACCOrganization}</label>
						</div>
						<div class="span7">
							<div id="enumPartyTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class=''></label>
						</div>
						<div class="span7">
							<div id="organizationId"  style="display: inline-block; float: left;">
								<div id="organizationGrid"></div>
							</div>
							<button type="button" class="btn btn-primary btn-mini" id="addInvoiceInfoBtn" style="float: left; margin-left: 3px" 
								title="${uiLabelMap.AdditionalInformationInvoice}"><i class="icon-only fa-info-circle"></i></button>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class='required'>${uiLabelMap.BACCInvoiceToParty}</label>
						</div>
						<div class="span7">
							<div id="customerId">
								<div id="customerGrid"></div>
							</div>
				   		</div>
					</div>
				<#else>
					<div class='row-fluid'>
						<div class='span5'>
							<label class='required'>${uiLabelMap.BACCOrganization}</label>
						</div>
						<div class="span7">
							<div id="organizationId">
								<div id="organizationGrid">
								</div>
							</div>
				   		</div>
					</div>	
					<div class='row-fluid'>
						<div class='span5'>
							<label class='required'>${uiLabelMap.BACCInvoiceToParty}</label>
						</div>
						<div class="span7">
							<div id="enumPartyTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class=''></label>
						</div>
						<div class="span7">
							<div id="customerId" style="float: left; display: inline-block;">
								<div id="customerGrid"></div>
							</div>
							<button type="button" class="btn btn-primary btn-mini" id="addInvoiceInfoBtn" style="float: left; margin-left: 3px" 
								title="${uiLabelMap.AdditionalInformationInvoice}"><i class="icon-only fa-info-circle"></i></button>
				   		</div>
					</div>
				</#if>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCCurrencyUom}</label>
					</div>
					<div class='span7'>
						<div id="currencyUomId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.AccountingAddTax}</label>
					</div>
					<div class="span7">
						<label class="pull-left" style="padding : 5px 0 0"><input name="switch-field-1" class="ace-switch ace-switch-6" type="checkbox" checked/><span class="lbl"></span></label>
			   		</div>
				</div>
			</div>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</form>
<div id="AddInfoInvoiceWindow" class="hide">
	<div>${uiLabelMap.AdditionalInformationInvoice}</div>
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
					<input type="text" id="infoInvoicePartyName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoInvoiceTaxCode">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonCountry)}</label>
				</div>
				<div class="span8">
					<div id="infoInvoiceCountry"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCStateProviceShort)}</label>
				</div>
				<div class="span8">
					<div id="infoInvoiceState"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoInvoiceAddress">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}</label>
				</div>
				<div class="span8">
					<input type="text" id="infoInvoicePhoneNbr">
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddInfoInvoice">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddInfoInvoice">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			<button type="button" class='btn btn-success form-action-button pull-right' id="clearAddInfoInvoice">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.wgdeletedata}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/invoiceNewStep1.js?v=0.0.2"></script>
<script type="text/javascript" src="/accresources/js/invoice/invoiceNewPartyInfo.js"></script>