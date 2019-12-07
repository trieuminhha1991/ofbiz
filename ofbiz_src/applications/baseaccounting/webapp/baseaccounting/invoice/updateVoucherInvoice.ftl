<div id="editVoucherWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}</label>
				</div>
				<div class="span7">
					<input type="text" id="invoiceIdVoucherEdit" value="${parameters.invoiceId}">
					<input type="hidden" id="voucherIdEdit">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherForm)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherFormEdit" >
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherSerial)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherSerialEdit">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.VoucherNumber)}</label>
				</div>
				<div class="span7">
					<input type="text" id="voucherNumberEdit">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}</label>
				</div>
				<div class="span7">
					<div id="issuedDateEdit"></div>
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
                    <div id="voucherCreatedDateEdit"></div>
                </div>
            </div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}</label>
				</div>
				<div class="span7">
					<div id="taxProductCategoryIdEdit"></div>
				</div>
			</div>			
			<div class='row-fluid margin-bottom10' style="position: relative;">
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}</label>
				</div>
				<div class="span7">
					<div id="amountVoucherEdit"></div>
					<a id="getInvTotalAmountBtnEdit" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" style="top: 0px; right: 1px" title="${uiLabelMap.BACCGetValueFromSystem}">
						<i class="fa fa-paint-brush blue" aria-hidden="true"></i>
					</a>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonTax)}</label>
				</div>
				<div class="span7">
					<div id="taxAmountVoucherEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCTotalAmountVoucher)}</label>
				</div>
				<div class="span7">
					<div id="totalAmountVoucherEdit"></div>
				</div>
			</div>			
			<div class='row-fluid'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.ImageUpload)}</label>
				</div>
				<div class="span7" id="uploadFileContainerEdit">
					<div id="displayImg"></div>
					<form class="no-margin" action="" class="row-fluid" id="upLoadFileFormEdit"  method="post" enctype="multipart/form-data">
						<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
						<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
						<div class="row-fluid">
							<div class="span12" style="margin-bottom: 0px !important; height: 0px !important">
					 			<input type="file" id="voucherImgUploadEdit" accept="image/*" name="uploadedFile"/>
					 		</div>
						</div>
				 	</form>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditVoucher">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditVoucher">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/updateVoucherInvoice.js?v=0.0.1"></script>