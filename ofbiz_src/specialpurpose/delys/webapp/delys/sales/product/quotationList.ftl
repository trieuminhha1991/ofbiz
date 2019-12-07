<#--
<div class="widget-body">
    <div class="widget-body-inner">
   		<div class="widget-main">-->
			<input type="hidden" name="removeSelected" value="false">
			<div style="overflow:auto; overflow-y:hidden">
				<table cellspacing="0" cellpadding="1" border="0" class="table table-striped dataTable table-hover table-bordered">
					<thead class="align-center">
						<tr>
							<th>${uiLabelMap.DANo}</th>
							<th class="center">${uiLabelMap.DAQuotationId}</th>
							<th class="center">${uiLabelMap.DAQuotationName}</th>
							<th class="center">${uiLabelMap.DAChannel}</th>
							<th class="center">${uiLabelMap.DACurrencyUomId}</th>
							<th class="center">${uiLabelMap.DAPartyApply}</th>
							<th class="center">${uiLabelMap.DAFromDate}</th>
							<th class="center">${uiLabelMap.DAThroughDate}</th>
							<th class="center">${uiLabelMap.DAStatus}</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<#list quotations as quotation>
						<tr>
							<td>${quotation_index + 1}</td>
							<td>${quotation.productQuotationId?if_exists}</td>
							<td>${quotation.quotationName?if_exists}</td>
							<td>
								<#if quotation.salesChannel?exists>
									<#if quotation.salesChannel == "SALES_GT_CHANNEL">
										${uiLabelMap.DAGT}
									<#elseif quotation.salesChannel == "SALES_MT_CHANNEL">
										${uiLabelMap.DAMT}
									</#if>
								</#if>
							</td>
							<td>${quotation.currencyUomId?if_exists}</td>
							<td>
								<#assign roleTypesSelected = delegator.findByAnd("ProductQuotationRoleTypeAndRoleType", {"productQuotationId" : quotation.productQuotationId?if_exists}, null, false)>
								<#list roleTypesSelected as roleTypeSelected>
									<i class="icon-user green"></i>
									<#if roleTypeSelected.description?exists>${roleTypeSelected.description}<#else>${roleTypeSelected.roleTypeId}</#if>
								</#list>
							</td>
							<td>
								<#if quotation.fromDate?exists>${quotation.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</td>
							<td>
								<#if quotation.thruDate?exists>${quotation.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</td>
							<td>
								<#assign currentStatusId = quotation.statusId?if_exists>
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
							</td>
							<td>
								<div class="hidden-phone visible-desktop btn-group">
									<button type="button" class="btn btn-mini btn-success" onclick="window.location.href='<@ofbizUrl>viewQuotation?productQuotationId=${quotation.productQuotationId?if_exists}</@ofbizUrl>';">
										<i class="icon-zoom-in bigger-120"></i>
									</button>
									<button type="button" class="btn btn-mini btn-info" onclick="window.location.href='<@ofbizUrl>editQuotation?productQuotationId=${quotation.productQuotationId?if_exists}</@ofbizUrl>';">
										<i class="icon-edit bigger-120"></i>
									</button>

									<button class="btn btn-mini btn-danger" onclick="javascript:deleteQuotation('${quotation.productQuotationId?if_exists}');">
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
												<a href="#" class="tooltip-info" data-rel="tooltip" title="View" onclick="window.location.href='<@ofbizUrl>viewQuotation?productQuotationId=${quotation.productQuotationId?if_exists}</@ofbizUrl>';">
													<span class="blue">
														<i class="icon-zoom-in bigger-120"></i>
													</span>
												</a>
											</li>

											<li>
												<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="window.location.href='<@ofbizUrl>editQuotation?productQuotationId=${quotation.productQuotationId?if_exists}</@ofbizUrl>';">
													<span class="green">
														<i class="icon-edit bigger-120"></i>
													</span>
												</a>
											</li>

											<li>
												<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:deleteQuotation('${quotation.productQuotationId?if_exists}');">
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
					</#list>
					</tbody>
				</table>
			</div>
		<#--</div>.widget-main
	</div>
</div>-->
<div style="display:none">
	<form name="deleteProductQuotation" id="deleteProductQuotation" action="deleteQuotation" method="POST">
		<input id="productQuotationId" name="productQuotationId" value="" />
	</form>
</div>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js">
</script>
<script type="text/javascript">
	function deleteQuotation(productQuotationId) {
		bootbox.confirm("${uiLabelMap.DAAreYouSureDelete}", function(result) {
			if(result) {
				$('#productQuotationId').val(productQuotationId);
				$('#deleteProductQuotation').submit();
			}
		});
	}
</script>