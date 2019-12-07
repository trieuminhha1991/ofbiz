<#if productPromoId?has_content && productPromo?exists>
	<#assign currencyUomId = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator) />
	1111,${productPromo.productPromoStatusId?if_exists}
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-header widget-header-small header-color-blue2">
			<h6>${uiLabelMap.DelysProductPromoOverview}</h6>		
		</div>
		<div class="widget-body">
			<div class="widget-body-inner">
				<div class="widget-main">
					<table class="table table-striped table-bordered dataTable table-hover" cellspacing='0'>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysProductPromoStatusId}</td>
							<td valign="top">
								${productPromo.productPromoStatusId?if_exists}	
								<hr/>
								<#if productPromo.productPromoStatusId?exists || productPromo.productPromoStatusId != undefined && productPromo.productPromoStatusId == "PROMO_CREATED" || productPromo.productPromoStatusId == "PROMO_PROCESSING">
									<form action="<@ofbizUrl>updateProductPromoStatus</@ofbizUrl>" method="post" name="promotionCancelStt">
										<input type="hidden" name="productPromoId" value="${productPromoId}">
										<input type="hidden" name="productPromoStatusId" value="PROMO_CANCELLED">
									</form>
									<a class="btn btn-warning btn-mini" style="float: left;" href="javascript:document.promotionCancelStt.submit()"><i class="icon-remove open-sans"></i>${uiLabelMap.CommonCancel}</a>
									<form action="<@ofbizUrl>updateProductPromoStatus</@ofbizUrl>" name="promotionAcceptStt" method="post">
										<input type="hidden" name="productPromoId" value="${productPromoId}">
										<input type="hidden" name="productPromoStatusId" value="PROMO_ACCEPTED">										
									</form>	
									<a class="btn btn-mini btn-primary" style="float: left; margin-left:5px" href="javascript:document.promotionAcceptStt.submit()"><i class="icon-ok open-sans"></i>${uiLabelMap.DelysCommonAccept}</a>	
								<#--
									<#elseif productPromo.productPromoStatusId == "PROMO_ACCEPTED">
										<form action="<@ofbizUrl>updateProductPromoStatus</@ofbizUrl>" method="post" name="promotionCancelStt">
											<input type="hidden" name="productPromoId" value="${productPromoId}">
											<input type="hidden" name="productPromoStatusId" value="PROMO_CANCELLED">
										</form>
										<a class="btn btn-warning btn-mini icon-remove" style="float: left;" href="javascript:document.promotionCancelStt.submit()">${uiLabelMap.CommonCancel}</a>
									<#elseif productPromo.productPromoStatusId == "PROMO_CANCELLED">
										<form action="<@ofbizUrl>updateProductPromoStatus</@ofbizUrl>" name="promotionAcceptStt" method="post" class="form-horizontal">
											<input type="hidden" name="productPromoId" value="${productPromoId}">
											<input type="hidden" name="productPromoStatusId" value="PROMO_ACCEPTED">										
										</form>	
									<a class="btn btn-mini btn-primary icon-ok" style="float: left;" href="javascript:document.promotionAcceptStt.submit()">${uiLabelMap.CommonAccept}</a>
								-->
								<#else>
									<span></span>
								</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPromoProductPromoId}</td>
							<td valign="top">
								<span>${productPromo.productPromoId}&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.PromotionNameDelys}</td>
							<td valign="top">
								${productPromo.promoName?if_exists}&nbsp;
							</td>
						</tr>	
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.PromotionContentDelys}</td>
							<td valign="top">
								${productPromo.promoText?if_exists}&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPromotionType}</td>
							<td valign="top">
								${productPromo.productPromoTypeId?if_exists}&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysProductPromoCondition} ${uiLabelMap.CommonAnd} ${uiLabelMap.DelysProductPromoAction}</td>
							<td valign="top">
								<a href="<@ofbizUrl>editProductPromoRules?productPromoId=${productPromo.productPromoId}</@ofbizUrl>">${uiLabelMap.CommonView} ${uiLabelMap.DelysProductPromoCondition} ${uiLabelMap.CommonAnd} ${uiLabelMap.DelysProductPromoAction}</a>
							</td>
						</tr>
						<tr>							
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysRoleTypeApply}</td>
							<td valign="top">
								<#if productPromoRoleTypeApplys?exists>
									<#list productPromoRoleTypeApplys as roleType>
										<#assign role = delegator.findOne("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", roleType.roleTypeId), false)>
										${role.description?if_exists} [${role.roleTypeId}] <br/>
									</#list> 
								<#else>
									&nbsp;
								</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPaymentMethod}</td>
							<td valign="top">
								${productPromo.paymentMethod?if_exists}&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DACreatedDate}</td>
							<td valign="top">
								<#if productPromo.createdDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.createdDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.CommonFromDate}</td>
							<td valign="top">
								<#if productPromo.fromDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.fromDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.CommonThruDate}</td>
							<td valign="top">
								<#if productPromo.thruDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.thruDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysBudgetTotal}</td>
							<td valign="top">
								<@ofbizCurrency amount=budgetTotal isoCode=currencyUomId />
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysMiniRevenue}</td>
							<td valign="top">
								<@ofbizCurrency amount=revenueMini isoCode=currencyUomId />
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPromotionStore}</td>
							<td valign="top">
								<#if productPromoStores?exists>
									<#list productPromoStores as productPromoStore>
										<#assign store = delegator.findOne("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", productPromoStore.productStoreId), false)>
										${store.storeName} [${store.productStoreId}] <br/>
									</#list>
								</#if>
								&nbsp;
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
</#if>