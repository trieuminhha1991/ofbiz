<#if salesPolicyId?has_content && salesPolicy?exists>
	<#assign currencyUomId = Static["org.ofbiz.entity.util.EntityUtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator) />
	<div class="widget-box transparent no-bottom-border">
		<div class="widget-header widget-header-small header-color-blue2">
			<h6>${uiLabelMap.DAOverview}</h6>		
		</div>
		<div class="widget-body">
			<div class="widget-body-inner">
				<div class="widget-main">
					<table class="table table-striped table-bordered dataTable table-hover" cellspacing='0'>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DAStatusId}</td>
							<td valign="top">
								${salesPolicy.statusId?if_exists}	
								<hr/>
								<#if salesPolicy.statusId == "SALES_PL_CREATED" || salesPolicy.statusId == "SALES_PL_PROCESSING">
									<form action="<@ofbizUrl>updateSalesPolicyStatus</@ofbizUrl>" method="post" name="promotionCancelStt">
										<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
										<input type="hidden" name="statusId" value="SALES_PL_CANCELLED">
									</form>
									<a class="btn btn-warning btn-mini icon-remove" style="float: left;" href="javascript:document.promotionCancelStt.submit()">${uiLabelMap.CommonCancel}</a>
									<form action="<@ofbizUrl>updateSalesPolicyStatus</@ofbizUrl>" name="promotionAcceptStt" method="post">
										<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
										<input type="hidden" name="statusId" value="SALES_PL_ACCEPTED">										
									</form>	
									<a class="btn btn-mini btn-primary icon-ok" style="float: left;" href="javascript:document.promotionAcceptStt.submit()">${uiLabelMap.DelysCommonAccept}</a>	
								<#--
									<#elseif salesPolicy.statusId == "SALES_PL_ACCEPTED">
										<form action="<@ofbizUrl>updateSalesPolicyStatus</@ofbizUrl>" method="post" name="promotionCancelStt">
											<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
											<input type="hidden" name="statusId" value="SALES_PL_CANCELLED">
										</form>
										<a class="btn btn-warning btn-mini icon-remove" style="float: left;" href="javascript:document.promotionCancelStt.submit()">${uiLabelMap.CommonCancel}</a>
									<#elseif salesPolicy.statusId == "SALES_PL_CANCELLED">
										<form action="<@ofbizUrl>updateSalesPolicyStatus</@ofbizUrl>" name="promotionAcceptStt" method="post" class="form-horizontal">
											<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
											<input type="hidden" name="statusId" value="SALES_PL_ACCEPTED">										
										</form>	
									<a class="btn btn-mini btn-primary icon-ok" style="float: left;" href="javascript:document.promotionAcceptStt.submit()">${uiLabelMap.CommonAccept}</a>
								-->
								</#if>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DASalesPolicyId}</td>
							<td valign="top">
								<span>${salesPolicy.salesPolicyId}&nbsp;</span>
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DASalesPolicyName}</td>
							<td valign="top">
								${salesPolicy.policyName?if_exists}&nbsp;
							</td>
						</tr>	
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DASalesPolicyContent}</td>
							<td valign="top">
								${salesPolicy.policyText?if_exists}&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysProductPromoCondition} ${uiLabelMap.CommonAnd} ${uiLabelMap.DelysProductPromoAction}</td>
							<td valign="top">
								<a href="<@ofbizUrl>editSalesPolicyRules?salesPolicyId=${salesPolicy.salesPolicyId}</@ofbizUrl>">${uiLabelMap.CommonView} ${uiLabelMap.DelysProductPromoCondition} ${uiLabelMap.CommonAnd} ${uiLabelMap.DelysProductPromoAction}</a>
							</td>
						</tr>
						<tr>							
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysRoleTypeApply}</td>
							<td valign="top">
								<#if policyRoleTypeApply?exists>
									<#list policyRoleTypeApply as roleType>
										<#assign role = delegator.findOne("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", roleType.roleTypeId), false)>
										${role.description?if_exists} [${role.roleTypeId}] <br/>
									</#list> 
								<#else>
									&nbsp;	
								</#if>
							</td>
						</tr>
						<#--
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPaymentMethod}</td>
							<td valign="top">
								${salesPolicy.paymentMethod?if_exists}&nbsp;
							</td>
						</tr>
						-->
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.CommonFromDate}</td>
							<td valign="top">
								${salesPolicy.fromDate?if_exists}&nbsp;
							</td>
						</tr>
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.CommonThruDate}</td>
							<td valign="top">
								${salesPolicy.thruDate?if_exists}&nbsp;
							</td>
						</tr>
						<#--
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
						-->
						<tr>
							<td align="right" valign="top" width="25%">${uiLabelMap.DelysPromotionStore}</td>
							<td valign="top">
								<#if salesPolicyStores?exists>
									<#list salesPolicyStores as salesPolicyStore>
										<#assign store = delegator.findOne("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", salesPolicyStore.productStoreId), false)>
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