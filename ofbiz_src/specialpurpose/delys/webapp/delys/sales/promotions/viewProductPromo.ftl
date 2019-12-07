<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>

<#if security.hasPermission("DELYS_PROMOS_APPROVE", session)>
	<#assign hasApproved = true>
<#else>
	<#assign hasApproved = false>
</#if>
<#assign currentStatusId = productPromo.productPromoStatusId?if_exists>
<#--
<div class="widget-header widget-header-blue widget-header-flat">
	<h4 class="lighter">${uiLabelMap.DelysPromoViewPromotion}: ${productPromo.promoName?if_exists}
		<#if productPromo.productPromoId?exists>
			(<a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromo.productPromoId}</@ofbizUrl>">${productPromo.productPromoId}</a>)
		</h4>
		<span class="widget-toolbar none-content">
			<#if currentStatusId?exists && currentStatusId == "PROMO_CREATED">
				<a href="<@ofbizUrl>editProductPromotion?productPromoId=${productPromo.productPromoId}</@ofbizUrl>">
					<i class="icon-pencil open-sans">${uiLabelMap.DelysPromoEditPromotion}</i>
				</a>
			</#if>
			<a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i>
			</a>
		</span>
	<#else>
		</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i>
			</a>
		</span>
	</#if>
</div>
-->
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
				
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysPromoProductPromoId}:</label>
							<div class="controls-desc">
								<b>${productPromo.productPromoId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysPromoPromotionName}:</label>
							<div class="controls-desc">
								${productPromo.promoName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysPromotionType}:</label>
							<div class="controls-desc">
								<#assign productPromoTypeSelected = productPromo.getRelatedOne("ProductPromoType", true)!/>
								<#if productPromoTypeSelected.description?exists>
									${productPromoTypeSelected.get("description", locale)}
								<#else>
									${productPromo.productPromoTypeId?if_exists}
								</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFromDate}:</label>
							<div class="controls-desc">
								<#if productPromo.fromDate?exists>${productPromo.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAThroughDate}:</label>
							<div class="controls-desc">
								<#if productPromo.thruDate?exists>${productPromo.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysProductPromoStatusId}:</label>
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
							</div>
						</div>
						<#if security.hasPermission("DELYS_PROMOS_ADMIN", session)>
						<div class="control-group">
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign promoStatuses = productPromo.getRelated("ProductPromoStatus", null, null, false)>
									<#if promoStatuses?has_content>
					                  	<#list promoStatuses as promoStatus>
						                    <#assign loopStatusItem = promoStatus.getRelatedOne("StatusItem", false)>
						                    <#assign userlogin = promoStatus.getRelatedOne("UserLogin", false)>
						                    <div>
						                      	${loopStatusItem.get("description",locale)} <#if promoStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(promoStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
						                      	&nbsp;
						                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${promoStatus.statusUserLogin}]
						                      	<#if promoStatus.statusId == "PROMO_CANCELLED">
						                      		&nbsp;
						                      		${uiLabelMap.DAReason} - ${promoStatus.changeReason?if_exists}
						                      	</#if>
						                    </div>
					                  	</#list>
				                	</#if>
								</#if>
							</div>
						</div>
						</#if>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.PromotionContentDelys}:</label>
							<div class="controls-desc">
								${productPromo.promoText?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<#--
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACurrencyUomId}:</label>
							<div class="controls-desc">
								${quotationSelected.currencyUomId?if_exists}
							</div>
						</div>
						-->
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysPromotionStore}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStorePromoAppl as itemProductStoreAppl>
										<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-plus green"></i>
											<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysRoleTypeApply}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list promoRoleTypeApply as itemRoleTypeAppl>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-user green"></i>
											<#if itemRoleTypeAppl.description?exists>${itemRoleTypeAppl.description}<#else>${itemRoleTypeAppl.roleTypeId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</div>
						<#if security.hasPermission("DELYS_PROMOS_ADMIN", session)>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DelysBudgetTotal}:</label>
								<div class="controls-desc">
									<#if promoBudgetDist?exists>
										<#--
										<#assign promoBudgetDistSelected = promoBudgetDist.getRelatedOne("BudgetType", true)/>
										-->
										<#if promoBudgetDist.budgetId?exists>
											<a href="<@ofbizUrl>editBudgetPromo?budgetId=${promoBudgetDist.budgetId}</@ofbizUrl>">${promoBudgetDist.budgetId}</a>
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</#if>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DelysMiniRevenue}:</label>
								<div class="controls-desc">
									<#if promoMiniRevenue?exists>
										<#--
										<#assign promoBudgetDistSelected = promoBudgetDist.getRelatedOne("BudgetType", true)/>
										-->
										<#if promoMiniRevenue.budgetId?exists>
											<a href="<@ofbizUrl>editBudgetPromo?budgetId=${promoMiniRevenue.budgetId}</@ofbizUrl>">${promoMiniRevenue.budgetId}</a>
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</#if>
								</div>
							</div>
						<#else>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DelysBudgetTotal}:</label>
								<div class="controls-desc">
									<#if promoBudgetDist?exists>
										<#if promoBudgetDist.budgetId?exists>
											${promoBudgetDist.budgetId}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</#if>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.DelysMiniRevenue}:</label>
								<div class="controls-desc">
									<#if promoMiniRevenue?exists>
										<#if promoMiniRevenue.budgetId?exists>
											${promoMiniRevenue.budgetId}
										<#else>
											${uiLabelMap.DANotData}
										</#if>
									</#if>
								</div>
							</div>
						</#if>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysSalesTargets}:</label>
							<div class="controls-desc">
								<#if productPromo.promoSalesTargets?exists>
									${productPromo.promoSalesTargets}
								</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DelysPaymentMethod}:</label>
							<div class="controls-desc">
								${productPromo.paymentMethod?if_exists}
							</div>
						</div>
						<#if productPromo.useLimitPerOrder?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbUseLimitPerOrder}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerOrder?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if productPromo.useLimitPerCustomer?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbUseLimitPerCustomer}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerCustomer?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if productPromo.useLimitPerPromotion?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbUseLimitPerPromotion}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerPromotion?string(",##0.##")}
							</div>
						</div>
						</#if>
					</div><!--.span6-->
				</div><!--.row-->
			</div>
			<div style="clear:both"></div>
			
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAAbbContentPromotion}</b></h5><#--DelysPromoRuleListInPromos-->
			</div>
			<div id="list-product-price-rules">
				<#if productPromoRules?exists && productPromoRules?has_content>
					<table cellspacing="0" cellpadding="1" border="0" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th rowspan="2">${uiLabelMap.DelysSequenceOrder}</th>
								<th rowspan="2">${uiLabelMap.DelysRuleName}</th>
								<th colspan="2" class="align-center">${uiLabelMap.DelysConditon}</th>
								<th colspan="2" class="align-center">${uiLabelMap.DelysPromoAction}</th>
								<#--<th rowspan="2">&nbsp;</th>-->
							</tr>
	                        <tr>
	                            <#--<th>${uiLabelMap.DelysSequenceOrder}</th>-->
	                            <th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
	                            <th>${uiLabelMap.DelysPromoCondition} </th>
	                            <#--<th>${uiLabelMap.DelysSequenceOrder}</th>-->
	                            <th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
	                            <th>${uiLabelMap.DelysPromoAction}</th>
	                        </tr>
						</thead>
						<tbody>
	                        <#list productPromoRules as rule>
	                            <#assign productPromoConds = rule.getRelated("ProductPromoCond", null, null, false)>
	                            <#assign productPromoActions = rule.getRelated("ProductPromoAction", null, null, false)>
	                            <#assign maxCondSeqId = 1>
	                            <#if productPromoConds?has_content>
	                                <#assign condSize = productPromoConds?size>
	                            <#else>
	                                <#assign condSize = 1>
	                            </#if>
	                            <#if productPromoActions?has_content>
	                                <#assign actionSize = productPromoActions?size>
	                                <#assign actionFist = productPromoActions?first>
	                            <#else>
	                                <#assign actionSize = 1>
	                                <#assign actionFist = "">
	                            </#if>
	                            <#assign rowSpan = max(condSize, actionSize)>
	                            <#if (condSize > 1) >
	                                <#assign rowSpanCondFirst = 1>
	                                <#assign rowSpanCondLast = (rowSpan - condSize + 1)>
	                            <#else>
	                                <#assign rowSpanCondFirst = (rowSpan - condSize + 1)>
	                            </#if>
	                            <#if (actionSize > 1)>
	                                <#assign rowSpanActionFirst = 1>
	                                <#assign rowSpanActionLast = (rowSpan - actionSize + 1)>
	                            <#else>
	                                <#assign rowSpanActionFirst = (rowSpan - actionSize + 1)>
	                            </#if>
	                            
	                            <#if condSize &gt; actionSize>
	                            	<#assign sizeBrowse = productPromoConds?size >
	                            <#else>
	                            	<#assign sizeBrowse = productPromoActions?size >
	                            </#if>
	                            <#list 0..sizeBrowse as i>
	                            	<tr>
	                            		<#if i == 0>
	                            			<td rowspan="${rowSpan}">${rule_index + 1}</td>
	                                		<td rowspan="${rowSpan}">${rule.ruleName}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; productPromoConds?size) && (productPromoConds?size != 0)>
	                            			<#assign productPromoCondListOne = productPromoConds[i..i] />
                                        	<#assign productPromoCond = productPromoCondListOne?first />
	                            			<td <#if  i == (productPromoConds?size - 1)><#if i == 0>rowspan="${rowSpanCondFirst?if_exists}" <#else>rowspan="${rowSpanCondLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoCategory", null, null, false)>
	                                            <#if condProductPromoCategories?has_content>
	                                              	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoCategory}: </span>-->
	                                              	<#assign listCondCat = []>
	                                              	<#list condProductPromoCategories as condProductPromoCategory>
	                                                   	<#assign condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true)>
	                                                  	<#assign condApplEnumeration = condProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
	                                                   	<#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
	                                                   	<#assign includeSubCategoriesCond = condProductPromoCategory.includeSubCategories?default("N")>
                                                    	<div class="margin-top2">
                                                    		<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
                                                    		${(condProductCategory.get("description",locale))?if_exists} [${condProductPromoCategory.productCategoryId}]
	                                                  	</div>
	                                                  	<#--- ${(condApplEnumeration.get("description",locale))?default(condProductPromoCategory.productPromoApplEnumId)}
	                                                  	- ${uiLabelMap.DelysProductSubCats}: ${condProductPromoCategory.includeSubCategories?default("N")} -->
                                             	 	</#list>
	                                            <#--
	                                             <#else>
	                                                ${uiLabelMap.DelysProductNoConditionCategories}
	                                            -->
	                                            </#if>
	                                            
	                                            <#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoProduct", null, null, false)>
                                              	<#if condProductPromoProducts?has_content>
                                              		<#--<span class="text-info display-block">${uiLabelMap.DelysPromoProduct}: </span>-->
                                              		<#assign productCondList = []>
	                                              	<#list condProductPromoProducts as condProductPromoProduct>
		                                                <#assign condProduct = condProductPromoProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#assign condApplEnumeration = condProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
		                                                <#assign productCondList = productCondList + [condProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
		                                                	${(condProduct.internalName)?if_exists} [${condProductPromoProduct.productId}]
		                                                </div>
		                                                <#-- - ${(condApplEnumeration.get("description",locale))?default(condProductPromoProduct.productPromoApplEnumId)} -->
	                                              	</#list>
                                              	<#--
                                              	<#else>
	                                                ${uiLabelMap.DelysProductNoConditionProducts}
                                              	-->
                                              	</#if>
	                                        </td>
	                                        <td <#if  i == (productPromoConds?size - 1)><#if i == 0>rowspan="${rowSpanCondFirst?if_exists}" <#else>rowspan="${rowSpanCondLast?if_exists}" </#if></#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>">
                                              	<#if (productPromoCond.inputParamEnumId)?exists>
	                                                <#assign inputParamEnum = productPromoCond.getRelatedOne("InputParamEnumeration", true)>
	                                                <#if inputParamEnum?exists>
	                                                    ${(inputParamEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(productPromoCond.inputParamEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            <#if (productPromoCond.operatorEnumId)?exists>
	                                                <#assign operatorEnum = productPromoCond.getRelatedOne("OperatorEnumeration", true)>
	                                                <#if operatorEnum?exists>
	                                                    ${(operatorEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(productPromoCond.operatorEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            ${(productPromoCond.condValue)?if_exists}
	                                            <#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "EXHIBITED">
	                                                <br/>
	                                                ${uiLabelMap.DelysExhibitedAt}: ${productPromoCond.condExhibited?if_exists} ${productPromoCond.notes?if_exists}
	                                            </#if>
	                                        </td>
	                                	<#elseif productPromoConds?size == 0 && (i &lt; productPromoActions?size)>
	                                    	<td colspan="2" rowspan="${rowSpan}">111${uiLabelMap.NoConditionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; productPromoActions?size) && (productPromoActions?size != 0)>
	                            			<#assign productPromoActionListOne = productPromoActions[i..i] />
                                        	<#assign productPromoAction = productPromoActionListOne?first />
	                            			<td <#if i == (productPromoActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign actionProductPromoCategories = productPromoAction.getRelated("ProductPromoCategory", null, null, false)>
	                                          	<#if actionProductPromoCategories?has_content>
	                                             	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoCategory}: </span>-->
	                                             	<#list actionProductPromoCategories as actionProductPromoCategory>
		                                                <#assign actionProductCategory = actionProductPromoCategory.getRelatedOne("ProductCategory", true)>
		                                                <#assign actionApplEnumeration = actionProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
		                                                	${(actionProductCategory.description)?if_exists} [${actionProductPromoCategory.productCategoryId}]
		                                              	</div>
		                                              	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoCategory.productPromoApplEnumId)}
		                                              	- ${uiLabelMap.ProductSubCats}? ${actionProductPromoCategory.includeSubCategories?default("N")} -->
		                                              	<#--- ${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionProductPromoCategory.andGroupId}-->
	                                             	</#list>
	                                          	</#if>
	                                          	
	                                          	<#assign actionProductPromoProducts = productPromoAction.getRelated("ProductPromoProduct", null, null, false)>
                                              	<#if actionProductPromoProducts?has_content>
	                                              	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoProduct}: </span>-->
	                                              	<#assign productActList = []>
	                                              	<#list actionProductPromoProducts as actionProductPromoProduct>
		                                                <#assign actionProduct = actionProductPromoProduct.getRelatedOne("Product", true)!>
		                                                <#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOne("ApplEnumeration", true)!>
		                                                <#assign productActList = productActList + [actionProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
		                                                	${(actionProduct.internalName)?if_exists} [${actionProductPromoProduct.productId}]
		                                                </div>
		                                                <#-- - ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoProduct.productPromoApplEnumId)} -->
	                                              	</#list>
                                              	</#if>
	                                        </td>
	                                        <td <#if i == (productPromoActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>" nowrap>
	                                            <#if (productPromoAction.productPromoActionEnumId)?exists>
	                                            	<#assign productPromoActionCurEnum = productPromoAction.getRelatedOne("ActionEnumeration", true)!>
		                                            <#if productPromoActionCurEnum?exists>
		                                                ${(productPromoActionCurEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                	[${(productPromoAction.productPromoActionEnumId)?if_exists}]
	                                            	</#if>
	                                            </#if>
	                                            &nbsp;
	                                            <input type="hidden" name="orderAdjustmentTypeId" value="${(productPromoAction.orderAdjustmentTypeId)?if_exists}" />
	                                            <ul class="unstyled no-bottom-margin">
	                                            <#if actionFist.quantity?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.DelysQuantity}: ${actionFist.quantity}</li>
	                                            </#if>
	                                            <#if actionFist.amount?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.DelysAmount}: ${actionFist.amount}</li>
	                                            </#if>
	                                            <#if actionFist.productId?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.DelysProductId}: ${actionFist.productId}</li>
	                                            </#if>
	                                            <#if actionFist.partyId?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.DelysPartyId}: ${actionFist.partyId}</li>
	                                            </#if>
	                                            </ul>
	                                        </td>
		                                <#elseif productPromoActions?size == 0 && (i &lt; productPromoConds?size)>
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoActionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#--
	                            		<#if i == 0>
	                            			<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId=="PROMO_CREATED">
			                                    <td rowspan="${rowSpan}" nowrap>
			                                    	<div class="hidden-phone visible-desktop btn-group">
														<form method="post" name="editProductPromoRulesForm" id="editProductPromoRulesForm" action="<@ofbizUrl>editProductPromoRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
				                                            <input type="hidden" name="productPromoId" value="${(rule.productPromoId)?if_exists}" />
				                                            <input type="hidden" name="productPromoRuleId" value="${(rule.productPromoRuleId)?if_exists}" />
				                                            <button type="submit" class="btn btn-mini btn-primary" style="margin-bottom:0">
																<i class="icon-edit bigger-120"></i>
															</button>
				                                        </form>
				                                        <form name="deleteProductPromoRule_${rule_index}" method="post" action="<@ofbizUrl>deleteProductPromoRule</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
				                                            <input type="hidden" name="productPromoId" value="${(rule.productPromoId)?if_exists}" />
				                                            <input type="hidden" name="productPromoRuleId" value="${(rule.productPromoRuleId)?if_exists}" />
				                                            <button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteProductPromoRule_${rule_index}.submit();">
																<i class="icon-trash bigger-120"></i>
															</button>
				                                        </form>
													</div>
													<div class="hidden-desktop visible-phone">
														<div class="inline position-relative">
															<button class="btn btn-minier btn-primary dropdown-toggle" data-toggle="dropdown">
																<i class="icon-cog icon-only bigger-110"></i>
															</button>
															<ul class="dropdown-menu dropdown-icon-only dropdown-yellow pull-right dropdown-caret dropdown-close">
																<li>
																	<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:document.editProductPromoRulesForm.submit();">
																		<span class="green">
																			<i class="icon-edit bigger-120"></i>
																		</span>
																	</a>
																</li>
																<li>
																	<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteProductPromoRule_${rule_index}.submit();">
																		<span class="red">
																			<i class="icon-trash bigger-120"></i>
																		</span>
																	</a>
																</li>
															</ul>
														</div>
													</div>
			                                    </td>
			                                <#else>
			                                    <td rowspan="${rowSpan}"></td>
			                                </#if>
	                            		</#if>
	                            		-->
		                            </tr>
	                            </#list>
	                            
	                            <#--asdsadasdsa2-->
	                        </#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoRuleInPromotionToDisplay}</div>
				</#if>
			</div>
			<div style="text-align:right">
				<#if security.hasPermission("DELYS_PROMOS_APPROVE", session) && ("PROMO_CANCELLED" != productPromo.productPromoStatusId)>
					<#if (productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp) || !(productPromo.thruDate?exists)>
						<#if currentStatusId?exists && currentStatusId == "PROMO_CREATED">
							<#if hasApproved>
								<span class="widget-toolbar none-content">
									<a class="btn btn-primary btn-mini" href="javascript:acceptProductPromo();" style="font-size:13px; padding:0 8px">
										<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
					              	<a class="btn btn-primary btn-mini" href="javascript:cancelProductPromo();" style="font-size:13px; padding:0 8px">
										<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
									<form name="PromoAccept" method="post" action="<@ofbizUrl>changePromoStatus</@ofbizUrl>">
					                	<input type="hidden" name="productPromoStatusId" value="PROMO_ACCEPTED">
						                <input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}">
					              	</form>
									<form name="PromoCancel" method="post" action="<@ofbizUrl>changePromoStatus</@ofbizUrl>">
					                	<input type="hidden" name="productPromoStatusId" value="PROMO_CANCELLED">
						                <input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}">
						                <input type="hidden" name="changeReason" id="changeReason" value="" />
					              	</form>
								</span>
							</#if>
						</#if>
						<span class="widget-toolbar none-content">
							<form name="updatePromoThruDate" id="updatePromoThruDate" method="POST" action="<@ofbizUrl>updatePromoThruDate</@ofbizUrl>">
								<input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}" />
								<label for="thruDate">${uiLabelMap.DAThroughDate}:&nbsp;&nbsp;&nbsp;</label>
								<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${productPromo.thruDate?if_exists}" event="" action="" className="" alert="" 
									title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
									timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
									classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
									pmSelected="" compositeType="" formName=""/>
								<button class="btn btn-primary btn-mini" type="button" onclick="javascript:updatePromoThruDate2();">${uiLabelMap.DAUpdate}</button>
							</form>
						</span>
					<#else>
						<span style="color:#D7432E">${uiLabelMap.DAThisPromotionHasExpired}</span>
					</#if>
				</#if>
			</div>
		</div>	
	</div>		
</div>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js">
</script>
<script type="text/javascript">
	function acceptProductPromo() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureAccept}", function(result) {
			if(result) {
				document.PromoAccept.submit();
			}
		});
	}
	function cancelProductPromo() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureCancelNotAccept}", function(result) {
			if(result) {
				document.PromoCancel.submit();
			}
		});
	}
	function updatePromoThruDate2() {
		if(!$('#updatePromoThruDate').valid()) {
			return false;
		} else {
			var thruDate = $("#thruDate_i18n").val();
			if ((/^\s*$/.test(thruDate))) {
				bootbox.dialog("${uiLabelMap.DAThruDateMustNotBeEmpty}!", [{
					"label" : "OK",
					"class" : "btn-small btn-primary",
					}]
				);
				return false;
			}
			bootbox.confirm("${uiLabelMap.DAAreYouSureWantCreateThruDate}", function(result){
				if(result){
					document.getElementById("updatePromoThruDate").submit();
				}
			});
		}
	}
	<!--
	function deleteQuotation(productQuotationId) {
		bootbox.confirm("${uiLabelMap.DAAreYouSureDelete}", function(result) {
			if(result) {
				$('#productQuotationId').val(productQuotationId);
				$('#deleteProductQuotation').submit();
			}
		});
	}
	-->
	function enterCancelPromo() {
		bootbox.prompt("${uiLabelMap.DelysPromoReasonCancelPromo}:", function(result) {
			if(result === null) {
			} else {
				document.getElementById('changeReason').value = "" + result;
				document.PromoCancel.submit();
			}
		});
	}
$(function() {
	$('#updatePromoThruDate').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			thruDate: {
				required: true
			}
		},

		messages: {
			thruDate: {
				required: "${uiLabelMap.DAThisFieldIsRequired}"
			}
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		errorPlacement: function (error, element) {
			if(element.is(':checkbox') || element.is(':radio')) {
				var controls = element.closest('.controls');
				if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
				else error.insertAfter(element.nextAll('.lbl').eq(0));
			} 
			else if(element.is('.chzn-select')) {
				error.insertAfter(element.nextAll('[class*="chzn-container"]').eq(0));
			}
			else error.insertAfter(element);
		},
		submitHandler: function (form) {
			if(!$('#updatePromoThruDate').valid()) return false;
		},
		invalidHandler: function (form) {
		}
	});
});
</script>
