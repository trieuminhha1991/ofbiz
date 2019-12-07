<form class="form-horizontal form-window-content-custom" id="fixedAssetGeneralInfoForm">
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCFixedAssetId}</label>
					</div>
					<div class="span7">
						<div class="row-fluid" style="margin-bottom: 0">
							<div class="span12">
								<div class="span6">
									<input id="fixedAssetId" type="text"/>
								</div>
								<div class="span6 align-left">
									<a href="javascript:void(0)" id="autoGenerateIdBtn"><i class="fa fa-arrow-down open-sans"></i>${StringUtil.wrapString(uiLabelMap.BACCGetAutoGenerateId)}</a>
								</div>
							</div>
						</div>
			   		</div>
				</div>	
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCCurrencyUomId}</label>
					</div>
					<div class="span7">
						<div id="currencyUomId"></div>
			   		</div>
				</div>		
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCQuantity}</label>
					</div>
					<div class="span7">
						<div class="row-fluid" style="margin-bottom: 0px">
							<div class="span3">
								<div id="quantity"></div>
							</div>
							<div class="span9">
								<div class='row-fluid' style="margin-bottom: 0px">
									<div class='span5'>
										<label class=''>${uiLabelMap.BACCYearMade}</label>
									</div>
									<div class="span7">
										<div id="yearMade"></div>
									</div>
								</div>
							</div>
						</div>
			   		</div>
				</div>		
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCMark}</label>
					</div>
					<div class="span7">
						<input type="text" id="serialNumber">
			   		</div>
				</div>		
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCWarrantyPeriod}</label>
					</div>
					<div class="span7">
						<input type="text" id="warrantyPeriod">
			   		</div>
				</div>		
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BSSupplier}</label>
					</div>
					<div class="span7">
						<div id="supplierDropDown">
							<div id="supplierGrid"></div>
						</div>
			   		</div>
				</div>		
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BSState}</label>
					</div>
					<div class="span7">
						<div id="statusId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCReceiptNumber}</label>
					</div>
					<div class="span7">
						<input type="text" id="receiptNumber">
			   		</div>
				</div>	
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCReceiptDate}</label>
					</div>
					<div class="span7">
						<div id="receiptDate"></div>
			   		</div>
				</div>			
			</div><!-- ./span6 -->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class="asterisk">${uiLabelMap.BACCFixedAssetName}</label>
					</div>
					<div class="span7">
						<input id="fixedAssetName"></input>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCFixedAssetTypeId}</label>
					</div>
					<div class="span7">
						<div id="fixedAssetTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.OrganizationUsed}</label>
					</div>
					<div class="span7">
						<div id="orgUseTypeDropDown"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''></label>
					</div>
					<div class="span7">
						<div id="partyDropDown" class="hide">
							<div id="partyTree"></div>
						</div>
						<div id="productStoreDropDown" class="hide">
							<div id="productStoreGrid"></div>
						</div>
						<div id="partnerDropdown" class="hide">
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.ProductCountryOfOrigin}</label>
					</div>
					<div class="span7">
						<input id="countryOrigin" type="text"/>
			   		</div>
				</div>	
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCWarrantyCondition}</label>
					</div>
					<div class="span7">
						<input type="text" id="warrantyCondition">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCDescription}</label>
					</div>
					<div class="span7">
						<textarea id="description" class="" style="width: 93% !important; height: 95px; margin-top: 0px"></textarea>
			   		</div>
				</div>
			</div><!-- ./span6 -->
		</div><!-- ./span12 -->
	</div><!-- ./row-fluid -->
</form>