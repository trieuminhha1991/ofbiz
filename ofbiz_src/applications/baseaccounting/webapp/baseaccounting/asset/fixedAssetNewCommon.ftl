<form class="form-horizontal form-window-content-custom" id="newAsset" name="newAsset">
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCFixedAssetId}</label>
					</div>
					<div class="span7">
						<input id="fixedAssetId" style="padding: 0px !important;"></input>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BACCFixedAssetName}</label>
					</div>
					<div class="span7">
						<input id="fixedAssetName" style="padding: 0px !important;"></input>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCFixedAssetTypeId}</label>
					</div>
					<div class="span7">
						<div id="fixedAssetTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCPurCostAcc}</label>
					</div>
					<div class="span7">
						<div id="costGlAccountId">
							<div id="costGlAccountGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BACCDepAccount}</label>
					</div>
					<div class="span7">
						<div id="depGlAccountId">
							<div id="depGlAccountGrid"></div>
						</div>
			   		</div>
				</div>
			</div><!--span6-->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCAcquireInvoiceId}</label>
					</div>
					<div class="span7">
						<div id="invoiceDropDownBtn">
							<div id="invoiceGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.OrganizationUsed}</label>
					</div>
					<div class="span7">
						<div id="wn_partyId">
							<div id="wn_partyTree"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCDateAcquired}</label>
					</div>
					<div class="span7">
						<div id="dateAcquired"></div>
			   		</div>
		   		</div>	
		   		<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCDatePurchase}</label>
					</div>
					<div class="span7">
						<div id="datePurchase"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCCurrencyUomId}</label>
					</div>
					<div class="span7">
						<div id="uomId">
						</div>
			   		</div>
				</div>
			</div><!--span6-->
		</div><!--.span12-->
	</div><!--.row-fluid-->
</form>
