<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right">
                	<form mode="post" name="productDamaged" action="<@ofbizUrl>updateDamagedQuantity</@ofbizUrl>">
	                	<table id="sale-forecast" class="table table-striped table-bordered table-hover">
			            	<thead>
			            		<tr class="sf-product">
			            			<td>${uiLabelMap.Product}</td>
			            			<#if listVarianceReason?has_content>
										<#list listVarianceReason as reason>
											<td>${reason.get("description", locale)} (${uiLabelMap.Quantity}/${uiLabelMap.Party})</d>
										</#list>
									</#if>
			            		</tr>
			            	</thead>
			            	<tbody>
		            			<#if listItemToReceives?has_content>
									<#assign rowCount = 0 />
									<#list listItemToReceives as product>
										<tr>	
											<td>${product.productId}</td>
											<input name="productId_o_${rowCount}" type="hidden" value="${product.productId}"/>
											<input name="receiptId_o_${rowCount}" type="hidden" value="${product.receiptId}"/>
											<input name="receiptItemSeqId_o_${rowCount}" type="hidden" value="${product.receiptItemSeqId}"/>
		            			 			<#if listVarianceReason?has_content>	
												<#list listVarianceReason as reason>	
													<#assign receiptId = product.receiptId/>
													<#assign receiptItemSeqId = product.receiptItemSeqId/>
													<#assign varianceReasonId = reason.varianceReasonId/>
													<#assign receiptItemVar = delegator.findOne("ReceiptItemAndVariance", Static["org.ofbiz.base.util.UtilMisc"].toMap("receiptId", receiptId, "receiptItemSeqId", receiptItemSeqId, "varianceReasonId", varianceReasonId), false)?if_exists>
													<td>
														<#if receiptItemVar?has_content>	
															<input name="${reason.varianceReasonId}_o_${rowCount}" type="text" value="${receiptItemVar.quantity}" />
															<@htmlTemplate.lookupField value="${receiptItemVar.partyId?if_exists}" formName="productDamaged" name="${reason.varianceReasonId}_partyId_o_${rowCount}" id="${reason.varianceReasonId}_partyId_o_${rowCount}" fieldFormName="lookupPartyInfo"/>
														<#else>
															<input name="${reason.varianceReasonId}_o_${rowCount}" type="text" value="0"/>
															<@htmlTemplate.lookupField formName="productDamaged" name="${reason.varianceReasonId}_partyId_o_${rowCount}" id="${reason.varianceReasonId}_partyId_o_${rowCount}" fieldFormName="lookupPartyInfo"/>
														</#if>
													</td>
												</#list>
											</#if>
										</tr>
										<#assign rowCount=rowCount + 1/>
									</#list>
								</#if>
			            	</tbody>
		            	</table>
						<button type="submit" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSubmit}</button>
	            	</form>
				</div>
			</div>
		</div>
	</div>
</div>