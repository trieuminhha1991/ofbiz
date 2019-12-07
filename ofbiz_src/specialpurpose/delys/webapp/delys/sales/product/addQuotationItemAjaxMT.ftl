<#if productPriceRules?exists && productPriceRules?has_content>
	<table class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th style="width:10px">${uiLabelMap.DANo}</th>
				<th class="center">${uiLabelMap.DABarcode}</th>
				<th class="center">${uiLabelMap.DAProductName}</th>
				<th class="center">${uiLabelMap.DAMateBy}</th>
				<th class="center">${uiLabelMap.DATax}</th>
				<th class="center" style="width:60px">${uiLabelMap.DAPacking}</th>
				<th class="center" style="width:40px">${uiLabelMap.DAPackingPerTray}</th>
				<th class="center">${uiLabelMap.DABeforeVATPerPacking}</th>
				<th class="center">${uiLabelMap.DAAfterVATPerPacking}</th>
				<th class="center">${uiLabelMap.DAPriceToConsumer}</th>
				<th style="width:40px"></th>
			</tr>
		</thead>
		<tbody>
		<#list productPriceRules as productPriceRule>
			<#if productPriceRule.productId?exists>
				<#assign product = delegator.findOne("Product", {"productId": productPriceRule.productId}, true)>
				<#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)>
				<#assign productWeight = productContentWrapper.get("PRODUCT_WEIGHT")>
				<#assign productWeightUomId	= productContentWrapper.get("WEIGHT_UOM_ID")>
				<#if productWeight?exists && productWeight?has_content && productWeightUomId?exists && productWeightUomId?has_content>
					<#assign productWeightUom = delegator.findByAnd("UomAndType", {"uomId": "WT_g"}, null, false)>
					<#if productWeightUom?exists && productWeightUom?has_content>
						<#assign productWeightUomName = productWeightUom[0].abbreviation>
					<#else>
						<#assign productWeightUomName = "">
					</#if>
				<#else>
					<#assign productWeightUomName = "">
				</#if>
				<#assign productBarcodes = delegator.findByAnd("GoodIdentification", {"goodIdentificationTypeId": "SKU", "productId": productPriceRule.productId}, null, false)>
				<tr>
					<td>${productPriceRule_index + 1}</td>
					<td>
						<#list productBarcodes as productBarcode>
							${productBarcode.idValue?if_exists}
						</#list>
					</td>
					<td>[${productPriceRule.productId?if_exists}] - ${productContentWrapper.get("PRODUCT_NAME")?default(uiLabelMap.DANoName)}</td>
					<td><#--xuat xu--></td>
					<td><#--thue--></td>
					<td style="width:60px">
						<#if productWeight?exists && productWeight?has_content && productWeight?trim != "">
							${productWeight?number?string("00.00")} (${productWeightUomName?if_exists})
						</#if>
					</td>
					<td><#--quy cach / khay--></td>
					<td style="text-align:right">
						<#if productPriceRule.priceToDist?exists && productPriceRule.priceToDist?has_content && productPriceRule.priceToDist?trim != "">
							<@ofbizCurrency amount=productPriceRule.priceToDist isoCode=quotationSelected.currencyUomId/>
						</#if>
					</td>
					<td style="text-align:right"></td>
					<td style="text-align:right">
						<#if productPriceRule.priceToConsumer?exists && productPriceRule.priceToConsumer?has_content && productPriceRule.priceToConsumer?trim != "">
							<@ofbizCurrency amount=productPriceRule.priceToConsumer isoCode=quotationSelected.currencyUomId/>
						</#if>
					</td>
					<td>
						<input type="hidden" id="hidden_inputParamEnumId_${productPriceRule_index}" value="${productPriceRule.inputParamEnumId?if_exists}" />
						<input type="hidden" id="hidden_productId_${productPriceRule_index}" value="${productPriceRule.productId?if_exists}" />
						<input type="hidden" id="hidden_productPriceRuleId_${productPriceRule_index}" value="${productPriceRule.productPriceRuleId?if_exists}" />
						<input type="hidden" id="hidden_ppa_productPriceActionSeqId_${productPriceRule_index}" value="${productPriceRule.ppa_productPriceActionSeqId?if_exists}" />
						<input type="hidden" id="hidden_priceToDist_${productPriceRule_index}" value="${productPriceRule.priceToDist?if_exists}" />
						<input type="hidden" id="hidden_ppam_productPriceActionSeqId_${productPriceRule_index}" value="${productPriceRule.ppam_productPriceActionSeqId?if_exists}" />
						<input type="hidden" id="hidden_priceToMarket_${productPriceRule_index}" value="${productPriceRule.priceToMarket?if_exists}" />
						<input type="hidden" id="hidden_ppac_productPriceActionSeqId_${productPriceRule_index}" value="${productPriceRule.ppac_productPriceActionSeqId?if_exists}" />
						<input type="hidden" id="hidden_priceToConsumer_${productPriceRule_index}" value="${productPriceRule.priceToConsumer?if_exists}" />
						<input type="hidden" id="hidden_pq_ProductQuotationId_${productPriceRule_index}" value="${productPriceRule.pq_ProductQuotationId?if_exists}" />
						
						<div class="hidden-phone visible-desktop btn-group">
							<button class="btn btn-mini btn-info" onclick="javascript:updateProductQuotationItem(${productPriceRule_index});">
								<i class="icon-edit bigger-120"></i>
							</button>
	
							<button class="btn btn-mini btn-danger" onclick="javascript:deleteProductPriceRule(${productPriceRule_index});">
								<i class="icon-trash bigger-120"></i>
							</button>
						</div>
	
						<div class="hidden-desktop visible-phone">
							<div class="inline position-relative">
								<button class="btn btn-minier btn-primary dropdown-toggle" data-toggle="dropdown">
									<i class="icon-cog icon-only bigger-110"></i>
								</button>
	
								<ul class="dropdown-menu dropdown-icon-only dropdown-yellow pull-right dropdown-caret dropdown-close">
									<li>
										<a href="#" class="tooltip-info" data-rel="tooltip" title="View">
											<span class="blue">
												<i class="icon-zoom-in bigger-120"></i>
											</span>
										</a>
									</li>
	
									<li>
										<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit">
											<span class="green">
												<i class="icon-edit bigger-120"></i>
											</span>
										</a>
									</li>
	
									<li>
										<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete">
											<span class="red">
												<i class="icon-trash bigger-120"></i>
											</span>
										</a>
									</li>
								</ul>
							</div>
						</div>
					</td>
				</tr>
			</#if>
		</#list>
		</tbody>
	</table>
<#else>
	<div class="alert alert-info">${uiLabelMap.DANoQuotationItemsToDisplay}</div>
</#if>