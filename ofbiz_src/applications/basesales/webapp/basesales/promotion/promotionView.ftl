<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>

<#assign hasApproved = false>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_APPROVE", "")>
	<#assign hasApproved = true>
</#if>
<#assign currentStatusId = productPromo.statusId?if_exists>

<#assign hasThruDate = false/>
<#assign isThruDate = false/>
<#if ("PROMO_CANCELLED" == productPromo.statusId) || (productPromo.thruDate?exists && productPromo.thruDate &lt; nowTimestamp)>
	<#assign isThruDate = true/>
</#if>
<#if hasApproved && !isThruDate>
	<#assign hasThruDate = true/>
</#if>

<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:0 10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSProductPromoId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productPromo.productPromoId?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPromoName}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productPromo.promoName?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSContent}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productPromo.promoText?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSStatus}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#--<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
								<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
			                </#if>-->
							${currentStatus?if_exists.get("description", locale)?if_exists}
						</span>
						<#if isThruDate> (<span style="color:#D7432E">${uiLabelMap.BSThisPromotionHasExpired}</span>)</#if>
					</div>
				</div>
				<#if hasApproved>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label></label>
						</div>
						<div class="div-inline-block">
							<span>
								<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign promoStatuses = productPromo.getRelated("ProductPromoStatus", null, null, false)>
								<#if promoStatuses?has_content>
				                  	<#list promoStatuses as promoStatus>
					                    <#assign loopStatusItem = promoStatus.getRelatedOne("StatusItem", false)>
					                    <#assign userlogin = promoStatus.getRelatedOne("UserLogin", false)>
					                    <div class="margin-left20">
					                      	${loopStatusItem.get("description",locale)} <#if promoStatus.statusDatetime?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(promoStatus.statusDatetime, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
					                      	&nbsp;
					                      	${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${promoStatus.statusUserLogin}]
					                      	<#if promoStatus.statusId == "PROMO_CANCELLED">
					                      		&nbsp;
					                      		${uiLabelMap.BSReason} - ${promoStatus.changeReason?if_exists}
					                      	</#if>
					                    </div>
				                  	</#list>
			                	</#if>
							</#if>
							</span>
						</div>
					</div>
				</#if>
				<#if productPromo.stateId?exists>
					<#assign promoState = delegator.findOne("StatusItem", {"statusId" : productPromo.stateId}, true)!>
					<#if promoState?has_content>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BSState}:</label>
							</div>
							<div class="div-inline-block">
								<span class="red"><b>${promoState.get("description", locale)}</b></span>
							</div>
						</div>
					</#if>
                </#if>
			</div>
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSFromDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productPromo.fromDate?exists>${productPromo.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSThruDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productPromo.thruDate?exists>${productPromo.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<#if productStorePromoAppl?exists>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list productStorePromoAppl as itemProductStoreAppl>
									<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
									<li style="margin-bottom: 0; margin-top:0">
										<#--<i class="icon-angle-right green"></i>-->
										<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
									</li>
									<#if itemProductStoreAppl_index &gt; 1 && productStorePromoAppl?size &gt; 2>
										<a href="javascript:void(0)" id="showProductStoreViewMore">${uiLabelMap.BSViewMore} (${productStorePromoAppl?size - 3})</a>
										<#break/>
									</#if>
								</#list>
							</ul>
							<div style="display:none" id="productStoreViewMore">
								<label>${uiLabelMap.BSTotal}: ${productStorePromoAppl?size}</label>
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStorePromoAppl as itemProductStoreAppl>
										<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-angle-right green"></i>
											<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</#if>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyApply}:</label>
					</div>
					<div class="div-inline-block">
						<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
							<#list promoRoleTypeApply as itemRoleTypeAppl>
								<li style="margin-bottom: 0; margin-top:0">
									<i class="icon-user green"></i>
									<#assign roleType = delegator.findOne("RoleType", {"roleTypeId": itemRoleTypeAppl.roleTypeId}, true)!/>
									<#if roleType?exists>${roleType.description?if_exists}<#else>${itemRoleTypeAppl?if_exists.roleTypeId}</#if>
								</li>
							</#list>
						</ul>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSRequireVoucherCode}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productPromo.requireCode?default("N")}</span>
					</div>
				</div>
				<#if productPromo.useLimitPerOrder?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSAbbUseLimitPerOrder}:</label>
						</div>
						<div class="div-inline-block">
							<span>${productPromo.useLimitPerOrder?string(",##0.##")}</span>
						</div>
					</div>
				</#if>
				<#if productPromo.useLimitPerCustomer?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSAbbUseLimitPerCustomer}:</label>
						</div>
						<div class="div-inline-block">
							<span>${productPromo.useLimitPerCustomer?string(",##0.##")}</span>
						</div>
					</div>
				</#if>
				<#if productPromo.useLimitPerPromotion?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSAbbUseLimitPerPromotion}:</label>
						</div>
						<div class="div-inline-block">
							<span>${productPromo.useLimitPerPromotion?string(",##0.##")}</span>
						</div>
					</div>
				<#assign numberUsed = delegator.findCountByCondition("ProductPromoUse", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productPromoId", productPromo.productPromoId), null, null)!>
				<#if numberUsed?exists>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSNumberUsed}:</label>
						</div>
						<div class="div-inline-block">
							<span class="text-strong red"><b>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(numberUsed, "#,###.##", locale)}</b></span>
						</div>
					</div>
				</#if>
				</#if>
			</div><!--.span6-->
		</div><!--.row-->
	</div>
	<#--<h5 class="block green"><b>${uiLabelMap.BSContent}</b></h5>-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="list-product-price-rules">
				<#if productPromoRules?exists && productPromoRules?has_content>
					<table cellspacing="0" cellpadding="1" border="0" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th rowspan="2">${uiLabelMap.BSSTT}</th>
								<th rowspan="2">${uiLabelMap.BSRuleName} - ${uiLabelMap.BSRuleText}</th>
								<th colspan="2" class="align-center">${uiLabelMap.BSCondition}</th>
								<th colspan="2" class="align-center">${uiLabelMap.BSAction}</th>
							</tr>
		                    <tr>
		                        <th>${uiLabelMap.BSCategoryProductApply}</th>
		                        <th>${uiLabelMap.BSCondition} </th>
		                        <th>${uiLabelMap.BSCategoryProductApply}</th>
		                        <th>${uiLabelMap.BSAction}</th>
		                    </tr>
						</thead>
						<tbody>
		                    <#list productPromoRules as rule>
		                        <#assign productPromoConds = rule.getRelated("ProductPromoCond", null, Static["org.ofbiz.base.util.UtilMisc"].toList("productPromoCondSeqId"), false)>
		                        <#assign productPromoActions = rule.getRelated("ProductPromoAction", null, Static["org.ofbiz.base.util.UtilMisc"].toList("productPromoActionSeqId"), false)>
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
		                            		<td rowspan="${rowSpan}"><div style="max-width:220px">${rule.ruleName}<#if rule.ruleText?has_content><br/>- ${rule.ruleText}</#if></div></td>
		                        		</#if>
		                        		
		                        		<#if (i &lt; productPromoConds?size) && (productPromoConds?size != 0)>
		                        			<#assign productPromoCondListOne = productPromoConds[i..i] />
		                                	<#assign productPromoCond = productPromoCondListOne?first />
		                        			<td <#if  i == (productPromoConds?size - 1)><#if i == 0>rowspan="${rowSpanCondFirst?if_exists}" <#else>rowspan="${rowSpanCondLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
		                                        <#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoCategory", null, null, false)>
		                                        <#if condProductPromoCategories?has_content>
		                                          	<#assign listCondCat = []>
		                                          	<#list condProductPromoCategories as condProductPromoCategory>
		                                               	<#assign condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true)>
		                                              	<#assign condApplEnumeration = condProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
		                                               	<#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
		                                               	<#assign includeSubCategoriesCond = condProductPromoCategory.includeSubCategories?default("N")>
		                                            	<div class="margin-top2">
		                                            		<span class="text-success span-product-category">C </span>
		                                            		${(condProductCategory.get("description",locale))?if_exists} [${condProductPromoCategory.productCategoryId}]
		                                              	</div>
		                                              	<#--- ${(condApplEnumeration.get("description",locale))?default(condProductPromoCategory.productPromoApplEnumId)}
		                                              	- ${uiLabelMap.BSProductSubCats}: ${condProductPromoCategory.includeSubCategories?default("N")} -->
		                                     	 	</#list>
		                                        </#if>
		                                        
		                                        <#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoProduct", null, null, false)>
		                                      	<#if condProductPromoProducts?has_content>
		                                      		<#assign productCondList = []>
		                                          	<#list condProductPromoProducts as condProductPromoProduct>
		                                                <#assign condProduct = condProductPromoProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#assign condApplEnumeration = condProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
		                                                <#assign productCondList = productCondList + [condProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">P </span>
		                                                	${(condProduct.productName)?if_exists} [${condProduct.productCode?default(condProductPromoProduct.productId)}]
		                                                </div>
		                                                <#-- - ${(condApplEnumeration.get("description",locale))?default(condProductPromoProduct.productPromoApplEnumId)} -->
		                                          	</#list>
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
												<#if productPromoCond.inputParamEnumId == "PPIP_RECURRENCE">
													<#assign recurrenceInfo = delegator.findOne("RecurrenceInfo", {"recurrenceInfoId": productPromoCond.condValue}, false)!>
													<#if recurrenceInfo.recurrenceRuleId?exists>
														<#assign recurrenceRule = delegator.findOne("RecurrenceRule", {"recurrenceRuleId": recurrenceInfo.recurrenceRuleId}, false)!>
														<#if recurrenceRule?exists>
															<br/>
															${uiLabelMap.BSHourList}: ${recurrenceRule.byHourList}<br/>
															${uiLabelMap.BSDayList}: ${recurrenceRule.byDayList}
														</#if>
													</#if>
												</#if>
		                                        <#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "EXHIBITED">
		                                            <br/>
		                                            ${uiLabelMap.BSExhibitedAt}: ${productPromoCond.condExhibited?if_exists} ${productPromoCond.notes?if_exists}
		                                        </#if>
												&nbsp;
												<ul class="unstyled no-bottom-margin">
		                                        <#if productPromoCond.usePriceWithTax?exists>
		                                            <li><i class="icon-angle-right"></i>${uiLabelMap.BSUsePriceWithTax}: ${productPromoCond.usePriceWithTax}</li>
		                                        </#if>
												</ul>
		                                    </td>
		                            	<#elseif productPromoConds?size == 0 && (i &lt; productPromoActions?size)>
		                                	<td colspan="2" rowspan="${rowSpan}">${uiLabelMap.BSNoConditionApplyForRule}</td>
		                        		</#if>
		                        		
		                        		<#if (i &lt; productPromoActions?size) && (productPromoActions?size != 0)>
		                        			<#assign productPromoActionListOne = productPromoActions[i..i] />
		                                	<#assign productPromoAction = productPromoActionListOne?first />
		                        			<td <#if i == (productPromoActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
		                                        <#assign actionProductPromoCategories = productPromoAction.getRelated("ProductPromoCategory", null, null, false)>
		                                      	<#if actionProductPromoCategories?has_content>
		                                         	<#list actionProductPromoCategories as actionProductPromoCategory>
		                                                <#assign actionProductCategory = actionProductPromoCategory.getRelatedOne("ProductCategory", true)>
		                                                <#assign actionApplEnumeration = actionProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">C </span>
		                                                	${(actionProductCategory.description)?if_exists} [${actionProductPromoCategory.productCategoryId}]
		                                              	</div>
		                                              	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoCategory.productPromoApplEnumId)}
		                                              	- ${uiLabelMap.ProductSubCats}? ${actionProductPromoCategory.includeSubCategories?default("N")} -->
		                                              	<#--- ${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionProductPromoCategory.andGroupId}-->
		                                         	</#list>
		                                      	</#if>
		                                      	
		                                      	<#assign actionProductPromoProducts = productPromoAction.getRelated("ProductPromoProduct", null, null, false)>
		                                      	<#if actionProductPromoProducts?has_content>
		                                          	<#assign productActList = []>
		                                          	<#list actionProductPromoProducts as actionProductPromoProduct>
		                                                <#assign actionProduct = actionProductPromoProduct.getRelatedOne("Product", true)!>
		                                                <#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOne("ApplEnumeration", true)!>
		                                                <#assign productActList = productActList + [actionProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">P </span>
		                                                	${(actionProduct.productName)?if_exists} [${actionProduct.productCode?default(actionProductPromoProduct.productId)}]
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
		                                        <#if productPromoAction.quantity?exists>
		                                            <li><i class="icon-angle-right"></i>${uiLabelMap.Quantity}: ${productPromoAction.quantity}</li>
		                                        </#if>
		                                        <#if productPromoAction.amount?exists>
		                                            <li><i class="icon-angle-right"></i>${uiLabelMap.BSAmountOrPercent}: ${productPromoAction.amount}</li>
		                                        </#if>
		                                        <#if productPromoAction.productId?exists>
		                                            <li><i class="icon-angle-right"></i>${uiLabelMap.BSProductId}: ${productPromoAction.productId}</li>
		                                        </#if>
		                                        <#if productPromoAction.partyId?exists>
		                                            <li><i class="icon-angle-right"></i>${uiLabelMap.BSPartyId}: ${productPromoAction.partyId}</li>
		                                        </#if>
		                                        <#if productPromoAction.operatorEnumId?exists>
		                                        	<#assign actionOperEnum = productPromoAction.getRelatedOne("OperatorEnumeration", true)!>
		                                            <#if actionOperEnum?exists && actionOperEnum?has_content>
		                                            	<li><i class="icon-angle-right"></i>${uiLabelMap.BSOperator}: ${actionOperEnum.get("description",locale)}</li>
		                                            </#if>
		                                        </#if>
												<#if productPromoAction.isCheckInv?exists>
		                                        	<li><i class="icon-angle-right"></i>${uiLabelMap.BSCheckInventoryItem}: ${productPromoAction.isCheckInv}</li>
		                                        </#if>
		                                        </ul>
		                                    </td>
		                                <#elseif productPromoActions?size == 0 && (i &lt; productPromoConds?size)>
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.BSNoActionApplyForRule}</td>
		                        		</#if>
		                            </tr>
		                        </#list>
		                    </#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.BSNoRuleInPromotionToDisplay}</div>
				</#if>
			</div>
			<div style="text-align:right">
				<#if hasOlbPermission("MODULE", "PRODPROMOTION_APPROVE", "") && (productPromo.statusId?exists && "PROMO_CANCELLED" != productPromo.statusId)>
					<#if hasThruDate>
						<div class="row-fluid container-approve">
							<div class="span6">
								<#if currentStatusId?exists && currentStatusId == "PROMO_CREATED">
									<#if hasApproved>
										<span class="widget-toolbar none-content">
											<a class="btn btn-primary btn-mini" href="javascript:OlbPromoView.acceptProductPromo();">
												<i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
							              	<a class="btn btn-danger btn-mini" href="javascript:OlbPromoView.cancelProductPromo();">
												<i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
											<form name="PromoAccept" method="post" action="<@ofbizUrl>changePromoStatus</@ofbizUrl>">
							                	<input type="hidden" name="statusId" value="PROMO_ACCEPTED">
								                <input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}">
							              	</form>
											<form name="PromoCancel" method="post" action="<@ofbizUrl>changePromoStatus</@ofbizUrl>">
							                	<input type="hidden" name="statusId" value="PROMO_CANCELLED">
								                <input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}">
								                <input type="hidden" name="changeReason" id="changeReason" value="" />
							              	</form>
										</span>
									</#if>
								</#if>
							</div><!--.span6-->
							<div class="span6" style="padding-top: 5px">
								<div class="row-fluid">
									<div class="span3 text-right">
										<label for="thruDate" style="line-height: 30px;">${uiLabelMap.BSThruDate}:</label>
									</div>
									<div class="span9">
										<form name="updatePromoThruDate" id="updatePromoThruDate" method="POST" action="<@ofbizUrl>updatePromoThruDate</@ofbizUrl>" style="float:left">
											<input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}" />
											<#--<input type="hidden" id="thruDateHide" name="thruDate"/>
											<div id="thruDate"></div>-->
											<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${productPromo.thruDate?if_exists}" event="" action="" className="" alert="" 
												title="Format: yyyy-MM-dd HH:mm:ss" size="25" maxlength="30" dateType="date" shortDateInput=false 
												timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
												classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
												pmSelected="" compositeType="" formName=""/>
										</form>
										<button class="btn btn-primary btn-mini" type="button" onclick="javascript:OlbPromoView.updatePromoThruDate();" style="float:left; margin-left:5px">
											<i class="icon-ok open-sans"></i>${uiLabelMap.BSUpdate}
										</button>
									</div>
								</div>
							</div><!--.span6-->
						</div>
					</#if>
				</#if>
			</div>
		</div>
	</div><!--.row-fluid-->
</div>	

<#if hasThruDate>
	<#include "script/promotionViewScript.ftl"/>
<#else>
<script type="text/javascript">
	$("#showProductStoreViewMore").click(function(){
		var dataViewMore = $("#productStoreViewMore").html();
		jOlbUtil.alert.info(dataViewMore);
	})
</script>
</#if>