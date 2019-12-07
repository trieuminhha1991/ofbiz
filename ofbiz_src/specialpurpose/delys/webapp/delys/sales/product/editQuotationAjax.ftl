<form class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotationRule" name="updateQuotationRule" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
	<input type="hidden" id="update_inputParamEnumId" name="update_inputParamEnumId" value="${parameters.update_inputParamEnumId?if_exists}" />
	<input type="hidden" id="update_productPriceRuleId" name="update_productPriceRuleId" value="${parameters.update_productPriceRuleId?if_exists}" />
	<input type="hidden" id="update_ppa_productPriceActionSeqId" name="update_ppa_productPriceActionSeqId" value="${parameters.update_ppa_productPriceActionSeqId?if_exists}" />
	<input type="hidden" id="update_ppam_productPriceActionSeqId" name="update_ppam_productPriceActionSeqId" value="${parameters.update_ppam_productPriceActionSeqId?if_exists}" />
	<input type="hidden" id="update_ppac_productPriceActionSeqId" name="update_ppac_productPriceActionSeqId" value="${parameters.update_ppac_productPriceActionSeqId?if_exists}" />
	<input type="hidden" id="update_pq_ProductQuotationId" name="update_pq_ProductQuotationId" value="${parameters.update_pq_ProductQuotationId?if_exists}" />
	<div class="row-fluid" id="updateQuotationItemToList">
		<div class="span12">
			<div class="control-group" style="margin:0 !important; font-size:9pt;">
				<span style="color:#666"><i>(${uiLabelMap.DAThisPriceApplyFor1Packing})</i></span>
			</div>
				<div class="control-group">
					<label class="control-label" for="update_productId">${uiLabelMap.DAProduct} <span style="color:red">*</span>:</label>
					<div class="controls">
						<div class="span12">
	                      	<input type="text" name="update_productId" id="update_productId" readonly="readonly" value="${parameters.update_productId?if_exists}"/>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="update_inputCategory">${uiLabelMap.DAInputCategoryVAT}:</label>
					<div class="controls">
						<div class="span12">
	                      	<select name="update_inputCategory" id="update_inputCategory">
				              	<option value="">${uiLabelMap.DABeforeVAT}</option>
				              	<#--<option value="">${uiLabelMap.DAAfterVAT}</option>-->
				            </select>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="update_priceToDist">${uiLabelMap.DAPriceToDistributor} <span style="color:red">*</span>:</label>
					<div class="controls">
						<div class="span12">
							<input type="text" name="update_priceToDist" id="update_priceToDist" size="25" value="${parameters.update_priceToDist?if_exists}">
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="update_priceToMarket">${uiLabelMap.DATheMarketPriceOfDistributor}:</label>
					<div class="controls">
						<div class="span12">
							<input type="text" name="update_priceToMarket" id="update_priceToMarket" size="25" value="${parameters.update_priceToMarket?if_exists}">
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="update_priceToConsumer">${uiLabelMap.DAPricesProposalForConsumer}:</label>
					<div class="controls">
						<div class="span12">
							<input type="text" name="update_priceToConsumer" id="update_priceToConsumer" size="25" value="${parameters.update_priceToConsumer?if_exists}">
						</div>
					</div>
				</div>
		</div>
	</div>
</form>