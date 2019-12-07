<div id="newInvoiceItemGrid"></div>

<div id="addNewIITypeWindow" class="hide">
	<div>${uiLabelMap.accAddNewRow}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="asterisk">${uiLabelMap.BACCInvoiceItemSeqId}</label>
				</div>
				<div class="span7">
					<input type="text" id="itemSeqId">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="asterisk">${uiLabelMap.BACCInvoiceItemType}</label>
				</div>
				<div class="span7">
					<div id="invoiceItemTypeDropDown">
						<div id="invoiceItemTypeGrid"></div>
					</div>	
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="asterisk">${uiLabelMap.BACCProduct}</label>
				</div>
				<div class="span7">
					<div id="productDropDownBtn">
						<div id="productIdGrid" style="border-color: transparent;"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="">${uiLabelMap.BACCQuantity}</label>
				</div>
				<div class="span7">
					<div class="row-fluid">
						<div class="span7">
							<div id="quantity"></div>
						</div>
						<div class="span5">
							<div id="quantityUomList"></div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="">${uiLabelMap.BACCUnitPrice}</label>
				</div>
				<div class="span7">
					<div id="unitPrice" style="display: inline-block; float: left;"></div>
					<button class="btn btn-mini" onclick="javascript: addNewIITObj.getProductPrice()" 
						style="float: left; margin-left: 4px" title="${uiLabelMap.GetProductPrice}">
						<i class="icon-only fa fa-money open-sans" style="font-size: 15px"></i></button>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>
					<label class="">${uiLabelMap.BACCDescription}</label>
				</div>
				<div class="span7">
					<input type="text" id="descriptionInvoiceItemType">
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/invoice/invoiceNewStep2.js?v=0.0.2"></script>