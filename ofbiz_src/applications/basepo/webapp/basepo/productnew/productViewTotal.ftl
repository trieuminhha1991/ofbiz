<#assign hasProductUPC = false/>
<#if product.amountUomTypeId?has_content><#assign hasProductUPC = true/></#if>

<#assign listQuantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)!/>
<#assign listCurrencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)!/>
<script type="text/javascript">
	var quantityUomsData = [
	<#if listQuantityUoms?exists>
		<#list listQuantityUoms as item>
		{	uomId: '${item.uomId?if_exists}',
	    	description: "${StringUtil.wrapString(item.get("description", locale))}"
	    },
		</#list>
	</#if>
	];
	var quantityUomMap = {
	<#if listQuantityUoms?exists>
		<#list listQuantityUoms as item>"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",</#list>
	</#if>
	};
	
	var currencyUomsData = [
	<#if listCurrencyUoms?exists>
		<#list listCurrencyUoms as item>
		{	uomId: '${item.uomId?if_exists}',
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var currencyUomMap = {
	<#if listCurrencyUoms?exists>
		<#list listCurrencyUoms as item>"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",</#list>
	</#if>
	};
</script>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if product?has_content>
										<li<#if !activeTab?exists || activeTab == "" || activeTab == "prodinfo-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#prodinfo-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
										</li>
										<#if product.productTypeId == "AGGREGATED">
											<li<#if activeTab?exists && activeTab == "prodchildren-tab"> class="active"</#if>>
												<a data-toggle="tab" href="#prodchildren-tab">${uiLabelMap.BSProductItem}</a>
											</li>
										<#else>
											<#if product.isVirtual == "Y">
												<li<#if activeTab?exists && activeTab == "prodchildren-tab"> class="active"</#if>>
													<a data-toggle="tab" href="#prodchildren-tab">${uiLabelMap.BSProductChild}</a>
												</li>
											</#if>
										</#if>

										<li<#if activeTab?exists && activeTab == "prodprice-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#prodprice-tab">${uiLabelMap.BSSalesPrice}</a>
										</li>
										
										<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW_SUPPL", "")>
										<li<#if activeTab?exists && activeTab == "prodsupplier-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#prodsupplier-tab">${uiLabelMap.BSSupplier}</a>
										</li>
										</#if>

										<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW_PROMO", "")>
										<li<#if activeTab?exists && activeTab == "prodpromo-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#prodpromo-tab">${uiLabelMap.BSSalesProductPromo}</a>
										</li>
										</#if>

										<li<#if activeTab?exists && activeTab == "alteruom-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#alteruom-tab">${uiLabelMap.BSAlternativeUom}</a>
										</li>
										<#if hasProductUPC>
										<li<#if activeTab?exists && activeTab == "alteridupc-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#alteridupc-tab">${uiLabelMap.BSUPCCode}</a>
										</li>
										</#if>
										<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW_OTHERTAX", "VIEW")>
										<li<#if activeTab?exists && activeTab == "othertax-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#othertax-tab">${uiLabelMap.BPOtherTax}</a>
										</li>
										</#if>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#--
							<#if orderHeader?has_content>
					    		<#if orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" &&  orderHeader.statusId != "ORDER_IN_TRANSIT" 
					    			&& editLogAllow?exists && editLogAllow>
									<#if hasOlbEntityPermission("SALESORDER", "UPDATE")>
						    		<a href="<@ofbizUrl>editSalesOrder?${paramString}</@ofbizUrl>" data-rel="tooltip" 
						    			title="${uiLabelMap.BSEdit}" data-placement="left" class="button-action">
						    			<i class="icon-edit open-sans"></i>
						    		</a>
						    		</#if>
						    	</#if>
							</#if>
							-->
							<#assign productStatusMgs = ""/>
							<#if product.salesDiscontinuationDate?exists && nowTimestamp.compareTo(product.salesDiscontinuationDate) &gt; 0>
								<#assign productStatusMgs = uiLabelMap.BSProductDiscountinueSales/>
							</#if>
							<#if product.purchaseDiscontinuationDate?exists && nowTimestamp.compareTo(product.purchaseDiscontinuationDate) &gt; 0>
								<#if productStatusMgs != ""><#assign productStatusMgs = productStatusMgs + ", "/></#if>
								<#assign productStatusMgs = productStatusMgs + uiLabelMap.BSProductDiscountinuePurchase/>
							</#if>
							<#if productStatusMgs?exists && productStatusMgs != "">
							<div class="title-status" id="productStatus" style="line-height:24px">
								${productStatusMgs}
							</div>
							</#if>
						</div>
					</div>
					<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
						$(function(){
							var data = {"productId": "${product.productId}"};
							<#if product.isVirtual == "Y">
								$("#dpcolor").removeClass("hide");
								$(".product-feature").removeClass("hide");
								<#--$("#productStatus").text("${StringUtil.wrapString(uiLabelMap.DmsIsVirtual)}");-->
								$("#DisplayColor").css('background-color', data.displayColor);
							<#elseif product.isVariant == "Y">
								$(".product-feature").removeClass("hide");
							</#if>
								<#--
									$("#productStatus").text(ProductType.getProductStatusByDepartment(data));
								<#else>
									$("#productStatus").text(ProductType.getProductStatusByDepartment(data));
								-->
						});
						<#--
						var ProductType = (function(){
							var getProductStatusByDepartment = function(data) {
								return DataAccess.getData({
									url: "getProductStatusByDepartment",
									data:{productId: data.productId},
									source: "status"});
							};
							return {
								getProductStatusByDepartment: getProductStatusByDepartment
							}
						}());
						-->
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0; z-index: auto">
						
						<#include "productViewInfo.ftl"/>
						
						<#if product.productTypeId == "AGGREGATED" || product.isVirtual == "Y">
							<div id="prodchildren-tab" class="tab-pane<#if activeTab?exists && activeTab == "prodchildren-tab"> active</#if>">
								<div style="position:relative"><!-- class="widget-body"-->
									<div><!--class="widget-main"-->
										<#include "productViewChildren.ftl"/>
									</div>
								</div>
							</div>
						</#if>
						
						<#include "productViewPrice.ftl"/>
						
						<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW_SUPPL", "")>
						<#include "productViewSupplier.ftl"/>
						</#if>
						
						<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW_PROMO", "")>
						<#include "productViewPromo.ftl"/>
						</#if>
						
						<#include "productViewAlterUom.ftl"/>
						
						<#if hasProductUPC>
						<#include "productViewUpcCode.ftl"/>
						</#if>
						
						<#include "productViewOtherTax.ftl"/>
						
						<div class="container_loader">
							<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
								<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
									<div>
										<div class="jqx-grid-load"></div>
										<span>${uiLabelMap.BSLoading}...</span>
									</div>
								</div>
							</div>
						</div>
						
					</div>
				</div><!--/widget-main-->
			</div><!--/widget-body-->
		</div><!--/widget-box-->
	</div><!-- /span12 -->
</div><!--/row-->

<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
