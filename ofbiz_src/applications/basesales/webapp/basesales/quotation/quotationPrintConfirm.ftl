<div>
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productQuotation.productQuotationId?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationName}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.quotationName?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block" style="width:75px; vertical-align: top">
						<label style="line-height: 20px;">${uiLabelMap.BSStatus}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
								<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
			                </#if>
						</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.description?if_exists}</span>
					</div>
				</div>
				<#--
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productQuotation.salesMethodChannelEnumId?exists>
								<#assign salesMethodChannel = delegator.findOne("Enumeration", {"enumId" : productQuotation.salesMethodChannelEnumId}, false)/>
								<#if salesMethodChannel?exists>
									${salesMethodChannel.get("description", locale)}
								</#if>
							</#if>
						</span>
					</div>
				</div>
				-->
			</div><!--.span6-->
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSCurrencyUomId}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.currencyUomId?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSFromDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.fromDate?exists>${productQuotation.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSThruDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.thruDate?exists>${productQuotation.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<#--
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#list roleTypesSelected as roleTypeSelected>
								<#if roleTypeSelected_index &gt; 0 && roleTypeSelected_index &lt; roleTypesSelected?size>, </#if>
								<#if roleTypeSelected.description?exists>${roleTypeSelected.description}<#else>${roleTypeSelected.roleTypeId}</#if>
							</#list>
						</span>
					</div>
				</div>
				<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyIdApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list listPartyIdApply as partyIdSelected>
									<li style="margin-bottom: 0; margin-top:0">
										<i class="icon-user green"></i>
										${partyIdSelected}
									</li>
								</#list>
							</ul>
						</span>
					</div>
				</div>
				</#if>
				-->
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productStoreAppls?exists>
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStoreAppls as itemProductStoreAppl>
										<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
										<li style="margin-bottom: 0; margin-top:0">
											<#--<i class="icon-angle-right green"></i>-->
											<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
										</li>
										<#if itemProductStoreAppl_index &gt; 1 && productStoreAppls?size &gt; 2>
											<a href="javascript:void(0)" id="showProductStoreViewMore">${uiLabelMap.BSViewMore} (${productStoreAppls?size - 3})</a>
											<#break/>
										</#if>
									</#list>
								</ul>
								<div style="display:none" id="productStoreViewMore">
									<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
										<#list productStoreAppls as itemProductStoreAppl>
											<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
											<li style="margin-bottom: 0; margin-top:0">
												<i class="icon-angle-right green"></i>
												<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
											</li>
										</#list>
									</ul>
								</div>
							</#if>
						</span>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-fluid-->
	</div><!--.form-horizontal-->
				
	<div class="row-fluid">
		<div class="span12">
			<div id="jqxgridProdSelected" style="width: 100%">
			</div>
			<script type="text/javascript">
				$(document).ready(function () {
					var sourceSuccess = {
							localdata: dataSelected,
							dataType: "array",
							datafields: ${dataField}
						};
					var dataAdapterSuccess = new jQuery.jqx.dataAdapter(sourceSuccess);
					jQuery("#jqxgridProdSelected").jqxGrid({
							width: '100%',
							source:dataAdapterSuccess,
							pageable: true,
					        autoheight: true,
					        sortable: false,
					        altrows: true,
					        showaggregates: false,
					        showstatusbar: false,
					        enabletooltips: true,
					        editable: false,
					        selectionmode: 'singlerow',
					        columns: [${columnlist}], 
					        columngroups: [${columngrouplist}]
					});
				});
			</script>
		</div>
	</div>
</div>