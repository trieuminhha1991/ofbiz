<#if currentStatusId?exists && currentStatusId?has_content>
	<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
</#if>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<#if hasOlbPermission("MODULE", "SALES_ORDER_VIEW", "")>
				<div class="widget-header" style="border-bottom:none">
					<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
						<div class="row-fluid">
							<div class="span10">
								<div class="tabbable">
									<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
										<li<#if !activeTab?exists || activeTab == "" || activeTab == "promoinfo-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#promoinfo-tab">${uiLabelMap.BSOverview}</a>
										</li>
										<#if productPromo.statusId == "PROMO_ACCEPTED">
										<li<#if activeTab?exists && activeTab == "promoorder-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#promoorder-tab">${uiLabelMap.BSOrderHasUsedPromo}</a>
										</li>
										</#if>
									</ul>
								</div><!--.tabbable-->
							</div>
							<div class="span2" style="height:34px; text-align:right">
								<#--<#if currentStatusId?has_content && currentStatusId == "PROMO_CREATED" && hasOlbPermission("MODULE", "PRODPROMOTION", "UPDATE")>
									<a href="<@ofbizUrl>editPromotion?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" data-rel="tooltip" 
						    			title="${uiLabelMap.BSEdit}" data-placement="left" class="button-action">
						    			<i class="icon-edit open-sans"></i>
						    		</a>
								</#if>
								<#if hasOlbPermission("MODULE", "PRODPROMOTION", "CREATE")>
									<a href="<@ofbizUrl>newPromotion</@ofbizUrl>" data-rel="tooltip" 
						    			title="${uiLabelMap.BSAddNew}" data-placement="left" class="button-action">
						    			<i class="icon-plus open-sans"></i>
						    		</a>
								</#if>-->
								<div class="title-status" id="productStatus" style="line-height:24px">
									${currentStatus?if_exists.get("description", locale)?if_exists}
								</div>
							</div>
						</div>
						<script type="text/javascript">
							$('[data-rel=tooltip]').tooltip();
						</script>
					</div>
				</div>
			</#if>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:0">
						<div id="promoinfo-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "promoinfo-tab"> active</#if>">
							<div class="row-fluid">
								<div class="span12">
									<#--${uiLabelMap.BSProductPromo}: ${productPromo.promoName} (${productPromo.productPromoId})-->
									<#include "promotionView.ftl"/>
								</div>
							</div>
						</div>
						<#if productPromo.statusId == "PROMO_ACCEPTED" && hasOlbPermission("MODULE", "SALES_ORDER_VIEW", "")>
						<div id="promoorder-tab" class="tab-pane<#if activeTab?exists && activeTab == "promoorder-tab"> active</#if>">
							<div class="row-fluid">
								<div class="span12">
									${setContextField("showtoolbar", "false")}
									${screens.render("component://basesales/widget/OrderScreens.xml#OrderListInner")}
								</div>
							</div>
						</div>
						</#if>
					</div>
				</div><!--/widget-main-->
			</div><!--/widget-body-->
		</div><!--/widget-box-->
	</div><!-- /span12 -->
</div><!--/row-->
