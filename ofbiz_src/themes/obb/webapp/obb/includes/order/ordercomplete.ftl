<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title">
					<h1>${uiLabelMap.ObbOrderConfirmation}?</h1>
					  </div>
						<#if !isDemoStore?exists || isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>
						<#if orderHeader?has_content>
						  ${screens.render("component://obb/widget/OrderScreens.xml#orderheader")}
						  ${screens.render("component://obb/widget/OrderScreens.xml#orderitems")}
						  <table width="100%" style="margin-top:20px;">
							  <tr valign="top">
							    <td>
							    </td>
							    <td align="right" style="text-align:right;">
								<button type="button" name="update_cart_action" value="" onclick="setLocation('<@ofbizUrl>main</@ofbizUrl>')" title="${uiLabelMap.EcommerceContinueShopping}" class="button btn-empty" id="empty_cart_button">
									<span><span>${uiLabelMap.ObbContinueShopping}</span></span>
								</button>
							    </td>
							  </tr>
						  </table>
						<#else>
						  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
						</#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>