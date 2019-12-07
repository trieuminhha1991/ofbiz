<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>${uiLabelMap.BEOrderDetail}</h1>
					  </div>
						<#if orderHeader?has_content>
						  <form name="addCommonToCartForm" action="<@ofbizUrl>addordertocart/orderstatus</@ofbizUrl>" method="post">
						    <input type="hidden" name="add_all" value="false" />
						    <input type="hidden" name="orderId" value="${orderHeader.orderId}" />
						    ${screens.render("component://obb/widget/OrderScreens.xml#orderheader")}
						    <br />
						    ${screens.render("component://obb/widget/OrderScreens.xml#orderitems")}
						  </form>

						<#else>
						  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
						</#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
