<div id="addNewVoucherWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}</label>
				</div>
				<div class="span7">
					<input type="text" id="invoiceIdVoucher" value="${parameters.invoiceId}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherForm" >
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherSerial">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherNumber">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}</label>
				</div>
				<div class="span7">
					<div id="issuedDate"></div>
				</div>
			</div>
			<div <#if businessType == 'AR'> class="row-fluid margin-bottom10 hide" <#else> class='row-fluid margin-bottom10' </#if>>
				<div class="span5 text-algin-right">
					<label class="asterisk">
						<#if businessType == 'AP'>
							${StringUtil.wrapString(uiLabelMap.ReceivingVoucherDate)}
						<#else>
							${StringUtil.wrapString(uiLabelMap.PublicationVoucherDate)}
						</#if>
					</label>
				</div>
				<div class="span7">
					<div id="voucherCreatedDate"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}</label>
				</div>
				<div class="span7">
					<div id="taxProductCategoryId"></div>
				</div>
			</div>			
			<div class='row-fluid margin-bottom10' style="position: relative;">
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}</label>
				</div>
				<div class="span7">
					<div id="amountVoucher"></div>
					<a id="getInvTotalAmountBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" style="top: 0px; right: 1px" title="${uiLabelMap.BACCGetValueFromSystem}">
						<i class="fa fa-paint-brush blue" aria-hidden="true"></i>
					</a>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonTax)}</label>
				</div>
				<div class="span7">
					<div id="taxAmountVoucher"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCTotalAmountVoucher)}</label>
				</div>
				<div class="span7">
					<div id="totalAmountVoucher"></div>
				</div>
			</div>			
			<div class='row-fluid'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.ImageUpload)}</label>
				</div>
				<div class="span7" id="uploadFileContainer">
					<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
						<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
						<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
						<div class="row-fluid">
							<div class="span12" style="margin-bottom: 0px !important; height: 0px !important">
					 			<input type="file" id="voucherImgUpload" accept="image/*" name="uploadedFile"/>
					 		</div>
						</div>
				 	</form>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddVoucher">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddVoucher">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddVoucher">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/createVoucherInvoice.js?v=20170819"></script>