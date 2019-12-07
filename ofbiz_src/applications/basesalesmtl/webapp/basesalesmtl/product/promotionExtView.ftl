<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>
<#assign isThruDate = true/>
<#if (productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp) || !(productPromo.thruDate?exists)>
	<#assign isThruDate = false/>
</#if>
<#assign hasApproved = false>
<#if hasOlbPermission("MODULE", "PRODPROMOTION_EXT_APPROVE", "")>
	<#assign hasApproved = true>
</#if>
<#assign currentStatusId = productPromo.statusId?if_exists>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSProductPromoId}:</label>
							<div class="controls-desc">
								<b>${productPromo.productPromoId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSPromoName}:</label>
							<div class="controls-desc">
								${productPromo.promoName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSStatusId}:</label>
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
				                <#if isThruDate> (<span style="color:#D7432E">${uiLabelMap.BSThisPromotionHasExpired}</span>)</#if>
							</div>
						</div>
						<#if security.hasPermission("PRODPROMOTION_VIEW", session)>
						<div class="control-group">
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign promoStatuses = productPromo.getRelated("ProductPromoExtStatus", null, null, false)>
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
						                      		${uiLabelMap.BSReason} - ${promoStatus.changeReason?if_exists}
						                      	</#if>
						                    </div>
					                  	</#list>
				                	</#if>
								</#if>
							</div>
						</div>
						</#if>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSContent}:</label>
							<div class="controls-desc">
								${productPromo.promoText?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSFromDate}:</label>
							<div class="controls-desc">
								<#if productPromo.fromDate?exists>${productPromo.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSThruDate}:</label>
							<div class="controls-desc">
								<#if productPromo.thruDate?exists>${productPromo.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSProductStore}:</label>
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
							<label class="control-label-desc">${uiLabelMap.BSPartyApply}:</label>
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
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSRequireVoucherCode}:</label>
							<div class="controls-desc">
								${productPromo.requireCode?default("N")}
							</div>
						</div>
						<#if productPromo.useLimitPerOrder?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerOrder}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerOrder?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if productPromo.useLimitPerCustomer?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerCustomer}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerCustomer?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if productPromo.useLimitPerPromotion?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerPromotion}:</label>
							<div class="controls-desc">
								${productPromo.useLimitPerPromotion?string(",##0.##")}
							</div>
						</div>
						</#if>
					</div><!--.span6-->
				</div><!--.row-->
			</div>
			<div style="clear:both"></div>
			
			<h5 class="block green"><b>${uiLabelMap.BSContent}</b></h5>
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
	                            <#assign productPromoConds = rule.getRelated("ProductPromoExtCond", null, Static["org.ofbiz.base.util.UtilMisc"].toList("productPromoCondSeqId"), false)>
	                            <#assign productPromoActions = rule.getRelated("ProductPromoExtAction", null, Static["org.ofbiz.base.util.UtilMisc"].toList("productPromoActionSeqId"), false)>
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
	                                            <#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoExtCategory", null, null, false)>
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
	                                            
	                                            <#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoExtProduct", null, null, false)>
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
	                                    	<td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoConditionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; productPromoActions?size) && (productPromoActions?size != 0)>
	                            			<#assign productPromoActionListOne = productPromoActions[i..i] />
                                        	<#assign productPromoAction = productPromoActionListOne?first />
	                            			<td <#if i == (productPromoActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign actionProductPromoCategories = productPromoAction.getRelated("ProductPromoExtCategory", null, null, false)>
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
	                                          	
	                                          	<#assign actionProductPromoProducts = productPromoAction.getRelated("ProductPromoExtProduct", null, null, false)>
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
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoActionApplyForRule}</td>
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
				<#if hasOlbPermission("MODULE", "PRODPROMOTION_EXT_APPROVE", "") && ("PROMO_CANCELLED" != productPromo.statusId)>
					<#if (productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp) || !(productPromo.thruDate?exists)>
						<div class="row-fluid container-approve">
							<div class="span6">
								<#if currentStatusId?exists && currentStatusId == "PROMO_CREATED">
									<#if hasApproved>
										<span class="widget-toolbar none-content">
											<a class="btn btn-primary btn-mini" href="javascript:OlbPromoView.acceptProductPromo();">
												<i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
							              	<a class="btn btn-danger btn-mini" href="javascript:OlbPromoView.cancelProductPromo();">
												<i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
											<form name="PromoAccept" method="post" action="<@ofbizUrl>changePromoExtStatus</@ofbizUrl>">
							                	<input type="hidden" name="statusId" value="PROMO_ACCEPTED">
								                <input type="hidden" name="productPromoId" value="${productPromo.productPromoId?if_exists}">
							              	</form>
											<form name="PromoCancel" method="post" action="<@ofbizUrl>changePromoExtStatus</@ofbizUrl>">
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
										<form name="updatePromoThruDate" id="updatePromoThruDate" method="POST" action="<@ofbizUrl>updatePromoExtThruDate</@ofbizUrl>" style="float:left">
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
	</div>		
</div>

<#if hasOlbPermission("MODULE", "PRODPROMOTION_EXT_APPROVE", "") && ("PROMO_CANCELLED" != productPromo.statusId)>
	<#if (productPromo.thruDate?exists && productPromo.thruDate &gt; nowTimestamp) || !(productPromo.thruDate?exists)>
		<#include "script/promotionExtViewScript.ftl"/>
	</#if>
</#if>