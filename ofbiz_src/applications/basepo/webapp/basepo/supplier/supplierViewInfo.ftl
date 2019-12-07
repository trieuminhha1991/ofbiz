<div id="info-tab" class="tab-pane active">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div>
					<div class="row-fluid">
						<div class="span12">
							<div class="row-fluid" style="margin-top:-12px">
								<div class="form-window-content-custom content-description-left">
									<div class="span6">
										<#-- general information -->
										<div class="row-fluid title-description">
											<label>${uiLabelMap.DmsGeneralInformation}</label>
											<div><span></span></div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.POSupplierId}:</label>
											<div>
												<span>
													${(party.partyCode)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.POSupplierName}:</label>
											<div>
												<span>
												${(supplier.groupName)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.PreferredCurrencyUomId}:</label>
											<div>
												<span>
													${(supplier.preferredCurrencyUomId)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BACCTaxCode}:</label>
											<div>
												<span>
													${(partyTax.taxCode)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BACCTaxAuthPartyId}:</label>
											<div>
												<span>
													${(partyTax.taxAuth)?if_exists}
												</span>
											</div>
										</div>
									</div><!--.span6-->
									<div class="span6">
										<#-- detail information -->
										<div class="row-fluid title-description">
											<label>${uiLabelMap.PartyContactMechs}</label>
											<div><span></span></div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.POEmailAddr}:</label>
											<div>
												<span>
												${(supplier.infoString)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.POTelecomNumber}:</label>
											<div>
												<span>
													${(supplier.contactNumber)?if_exists}
												</span>
											</div>
										</div>
										<div class="row-fluid">
											<label>${uiLabelMap.BPOAddress1}:</label>
											<div>
												<span>
													${(supplier.addressDetail)?if_exists}
												</span>
											</div>
										</div>
									</div><!--.span6-->
								</div><!--.form-window-content-custom-->
							</div>
						</div><!--.span9-->
					</div><!--.row-fluid-->
				</div><!--.form-horizontal-->
			</div><!--.row-fluid-->
		</div>
	</div>
</div>