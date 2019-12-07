<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>

<#assign hasApproved = false>
<#if hasOlbPermission("MODULE", "SALES_LOYALTY_APPROVE", "")>
	<#assign hasApproved = true>
</#if>
<#assign currentStatusId = loyalty.statusId?if_exists>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSLoyaltyId}:</label>
							<div class="controls-desc">
								<b>${loyalty.loyaltyId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSLoyaltyName}:</label>
							<div class="controls-desc">
								${loyalty.loyaltyName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSStatusId}:</label>
							<div class="controls-desc">
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSContent}:</label>
							<div class="controls-desc">
								${loyalty.loyaltyText?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSFromDate}:</label>
							<div class="controls-desc">
								<#if loyalty.fromDate?exists>${loyalty.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSThruDate}:</label>
							<div class="controls-desc">
								<#if loyalty.thruDate?exists>${loyalty.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSSalesChannel}:</label>
							<div class="controls-desc">
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStoreLoyaltyAppl as itemProductStoreAppl>
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
									<#list loyaltyRoleTypeApply as itemRoleTypeAppl>
										<li style="margin-bottom: 0; margin-top:0">
											<i class="icon-user green"></i>
											<#if itemRoleTypeAppl.description?exists>${itemRoleTypeAppl.description}<#else>${itemRoleTypeAppl.roleTypeId}</#if>
										</li>
									</#list>
								</ul>
							</div>
						</div>
						<#--<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSRequireVoucherCode}:</label>
							<div class="controls-desc">
								${loyalty.requireCode?default("N")}
							</div>
						</div>
						<#if loyalty.useLimitPerOrder?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerOrder}:</label>
							<div class="controls-desc">
								${loyalty.useLimitPerOrder?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if loyalty.useLimitPerCustomer?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerCustomer}:</label>
							<div class="controls-desc">
								${loyalty.useLimitPerCustomer?string(",##0.##")}
							</div>
						</div>
						</#if>
						<#if loyalty.useLimitPerPromotion?exists>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.BSAbbUseLimitPerPromotion}:</label>
							<div class="controls-desc">
								${loyalty.useLimitPerPromotion?string(",##0.##")}
							</div>
						</div>
						</#if>-->
					</div><!--.span6-->
				</div><!--.row-->
			</div>
			<div style="clear:both"></div>
			
			<h5 class="block green"><b>${uiLabelMap.BSContent}</b></h5>
			<div id="list-product-price-rules">
				<#if loyaltyRules?exists && loyaltyRules?has_content>
					<table cellspacing="0" cellpadding="1" border="0" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th rowspan="2">${uiLabelMap.BSSTT}</th>
								<th rowspan="2">${uiLabelMap.BSRuleName}</th>
								<th colspan="2" class="align-center">${uiLabelMap.BSCondition}</th>
								<th rowspan="2" class="align-center">${uiLabelMap.BSAction}</th>
							</tr>
	                        <tr>
	                            <th>${uiLabelMap.BSCategoryProductApply}</th>
	                            <th>${uiLabelMap.BSCondition} </th>
	                            <#--<th>${uiLabelMap.BSCategoryProductApply}</th>
	                            <th>${uiLabelMap.BSAction}</th>-->
	                        </tr>
						</thead>
						<tbody>
	                        <#list loyaltyRules as rule>
	                            <#assign loyaltyConds = rule.getRelated("LoyaltyCondition", null, Static["org.ofbiz.base.util.UtilMisc"].toList("loyaltyCondSeqId"), false)>
	                            <#assign loyaltyActions = rule.getRelated("LoyaltyAction", null, Static["org.ofbiz.base.util.UtilMisc"].toList("loyaltyActionSeqId"), false)>
	                            <#assign maxCondSeqId = 1>
	                            <#if loyaltyConds?has_content>
	                                <#assign condSize = loyaltyConds?size>
	                            <#else>
	                                <#assign condSize = 1>
	                            </#if>
	                            <#if loyaltyActions?has_content>
	                                <#assign actionSize = loyaltyActions?size>
	                                <#assign actionFist = loyaltyActions?first>
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
	                            	<#assign sizeBrowse = loyaltyConds?size >
	                            <#else>
	                            	<#assign sizeBrowse = loyaltyActions?size >
	                            </#if>
	                            <#list 0..sizeBrowse as i>
	                            	<tr>
	                            		<#if i == 0>
	                            			<td rowspan="${rowSpan}">${rule_index + 1}</td>
	                                		<td rowspan="${rowSpan}">${rule.ruleName}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; loyaltyConds?size) && (loyaltyConds?size != 0)>
	                            			<#assign loyaltyCondListOne = loyaltyConds[i..i] />
                                        	<#assign loyaltyCond = loyaltyCondListOne?first />
	                            			<td <#if  i == (loyaltyConds?size - 1)><#if i == 0>rowspan="${rowSpanCondFirst?if_exists}" <#else>rowspan="${rowSpanCondLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign condLoyaltyCategories = loyaltyCond.getRelated("LoyaltyCategory", null, null, false)>
	                                            <#if condLoyaltyCategories?has_content>
	                                              	<#assign listCondCat = []>
	                                              	<#list condLoyaltyCategories as condLoyaltyCategory>
	                                                   	<#assign condProductCategory = condLoyaltyCategory.getRelatedOne("ProductCategory", true)>
	                                                  	<#assign condApplEnumeration = condLoyaltyCategory.getRelatedOne("ApplEnumeration", true)>
	                                                   	<#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
	                                                   	<#assign includeSubCategoriesCond = condLoyaltyCategory.includeSubCategories?default("N")>
                                                    	<div class="margin-top2">
                                                    		<span class="text-success span-product-category">C </span>
                                                    		${(condProductCategory.get("description",locale))?if_exists} [${condLoyaltyCategory.productCategoryId}]
	                                                  	</div>
                                             	 	</#list>
	                                            </#if>
	                                            
	                                            <#assign condLoyaltyProducts = loyaltyCond.getRelated("LoyaltyProduct", null, null, false)>
                                              	<#if condLoyaltyProducts?has_content>
                                              		<#assign productCondList = []>
	                                              	<#list condLoyaltyProducts as condLoyaltyProduct>
		                                                <#assign condProduct = condLoyaltyProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#assign condApplEnumeration = condLoyaltyProduct.getRelatedOne("ApplEnumeration", true)>
		                                                <#assign productCondList = productCondList + [condProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">P </span>
		                                                	${(condProduct.productName)?if_exists} [${condProduct.productCode?default(condLoyaltyProduct.productId)}]
		                                                </div>
	                                              	</#list>
                                              	</#if>
	                                        </td>
	                                        <td <#if  i == (loyaltyConds?size - 1)><#if i == 0>rowspan="${rowSpanCondFirst?if_exists}" <#else>rowspan="${rowSpanCondLast?if_exists}" </#if></#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>">
                                              	<#if (loyaltyCond.inputParamEnumId)?exists>
	                                                <#assign inputParamEnum = loyaltyCond.getRelatedOne("InputParamEnumeration", true)>
	                                                <#if inputParamEnum?exists>
	                                                    ${(inputParamEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(loyaltyCond.inputParamEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            <#if (loyaltyCond.operatorEnumId)?exists>
	                                                <#assign operatorEnum = loyaltyCond.getRelatedOne("OperatorEnumeration", true)>
	                                                <#if operatorEnum?exists>
	                                                    ${(operatorEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(loyaltyCond.operatorEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            ${(loyaltyCond.condValue)?if_exists}
												&nbsp;
												<ul class="unstyled no-bottom-margin">
	                                            <#if loyaltyCond.usePriceWithTax?exists>
	                                            	<#assign usePriceWithTax = loyaltyCond.usePriceWithTax />
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSUsePriceWithTax}: <#if usePriceWithTax == 'Y'>${StringUtil.wrapString(uiLabelMap.BSYes)}<#else>${StringUtil.wrapString(uiLabelMap.BSNo)}</#if></li>
	                                            </#if>
	                                            <#if loyaltyCond.isReturnOrder?exists>
	                                            	<#assign isReturnOrder = loyaltyCond.isReturnOrder />
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSIsReturnOrder}: <#if isReturnOrder == 'Y'>${StringUtil.wrapString(uiLabelMap.BSYes)}<#else>${StringUtil.wrapString(uiLabelMap.BSNo)}</#if></li>
	                                            </#if>
												</ul>
	                                        </td>
	                                	<#elseif loyaltyConds?size == 0 && (i &lt; loyaltyActions?size)>
	                                    	<td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoConditionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; loyaltyActions?size) && (loyaltyActions?size != 0)>
	                            			<#assign loyaltyActionListOne = loyaltyActions[i..i] />
                                        	<#assign loyaltyAction = loyaltyActionListOne?first />
	                            			<#--<td <#if i == (loyaltyActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign actionLoyaltyCategories = loyaltyAction.getRelated("LoyaltyCategory", null, null, false)>
	                                          	<#if actionLoyaltyCategories?has_content>
	                                             	<#list actionLoyaltyCategories as actionLoyaltyCategory>
		                                                <#assign actionProductCategory = actionLoyaltyCategory.getRelatedOne("ProductCategory", true)>
		                                                <#assign actionApplEnumeration = actionLoyaltyCategory.getRelatedOne("ApplEnumeration", true)>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">C </span>
		                                                	${(actionProductCategory.description)?if_exists} [${actionLoyaltyCategory.productCategoryId}]
		                                              	</div>
	                                             	</#list>
	                                          	</#if>
	                                          	
	                                          	<#assign actionLoyaltyProducts = loyaltyAction.getRelated("LoyaltyProduct", null, null, false)>
                                              	<#if loyaltyActions?has_content>
	                                              	<#assign productActList = []>
	                                              	<#list actionLoyaltyProducts as actionLoyaltyProduct>
		                                                <#assign actionProduct = actionLoyaltyProduct.getRelatedOne("Product", true)!>
		                                                <#assign actionApplEnumeration = actionLoyaltyProduct.getRelatedOne("ApplEnumeration", true)!>
		                                                <#assign productActList = productActList + [actionProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">P </span>
		                                                	${(actionProduct.productName)?if_exists} [${actionProduct.productCode?default(actionLoyaltyProduct.productId)}]
		                                                </div>
	                                              	</#list>
                                              	</#if>
	                                        </td>-->
	                                        <td <#if i == (loyaltyActions?size - 1)><#if i == 0>rowspan="${rowSpanActionFirst?if_exists}" <#else>rowspan="${rowSpanActionLast?if_exists}" </#if></#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>" nowrap>
	                                            <#if (loyaltyAction.loyaltyActionEnumId)?exists>
	                                            	<#assign loyaltyActionCurEnum = loyaltyAction.getRelatedOne("ActionEnumeration", true)!>
		                                            <#if loyaltyActionCurEnum?exists>
		                                                ${(loyaltyActionCurEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                	[${(loyaltyAction.loyaltyActionEnumId)?if_exists}]
	                                            	</#if>
	                                            </#if>
	                                            &nbsp;
	                                            <#--<input type="hidden" name="orderAdjustmentTypeId" value="${(loyaltyAction.orderAdjustmentTypeId)?if_exists}" />-->
	                                            <ul class="unstyled no-bottom-margin">
	                                            <#if loyaltyAction.quantity?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSValue}: ${loyaltyAction.quantity}</li>
	                                            </#if>
	                                            <#if loyaltyAction.amount?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSAmountOrPercent}: ${loyaltyAction.amount}</li>
	                                            </#if>
	                                            <#if loyaltyAction.actionValue?exists>
	                                            	<#assign actionValue = loyaltyAction.actionValue />
	                                            	<#assign partyClassificationGroup = delegator.findOne("PartyClassificationGroup", {"partyClassificationGroupId" : actionValue}, true) />
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSValue}: [${actionValue}]
	                                                	<#if partyClassificationGroup?exists>
	                                                		${StringUtil.wrapString(partyClassificationGroup.description)}
	                                                	</#if>
	                                                </li>
	                                            </#if>
	                                            <#--<#if loyaltyAction.productId?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSProductId}: ${loyaltyAction.productId}</li>
	                                            </#if>
	                                            <#if loyaltyAction.partyId?exists>
	                                                <li><i class="icon-angle-right"></i>${uiLabelMap.BSPartyId}: ${loyaltyAction.partyId}</li>
	                                            </#if>-->
	                                            <#if loyaltyAction.operatorEnumId?exists>
	                                            	<#assign actionOperEnum = loyaltyAction.getRelatedOne("OperatorEnumeration", true)!>
	                                                <#if actionOperEnum?exists && actionOperEnum?has_content>
	                                                	<li><i class="icon-angle-right"></i>${uiLabelMap.BSOperator}: ${actionOperEnum.get("description",locale)}</li>
	                                                </#if>
	                                            </#if>
												<#--<#if loyaltyAction.isCheckInv?exists>
                                                	<li><i class="icon-angle-right"></i>${uiLabelMap.BSCheckInventoryItem}: ${loyaltyAction.isCheckInv}</li>
	                                            </#if>-->
	                                            </ul>
	                                        </td>
		                                <#elseif loyaltyActions?size == 0 && (i &lt; loyaltyConds?size)>
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoActionApplyForRule}</td>
	                            		</#if>
		                            </tr>
	                            </#list>
	                        </#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.BSNoRuleInLoyaltyToDisplay}</div>
				</#if>
			</div>
			<div style="text-align:right">
				<#if hasOlbPermission("MODULE", "SALES_LOYALTY_APPROVE", "") && ("LOYALTY_CANCELLED" != loyalty.statusId)>
					<#if (loyalty.thruDate?exists && loyalty.thruDate &gt; nowTimestamp) || !(loyalty.thruDate?exists)>
						<div class="row-fluid container-approve">
							<div class="span6">
								<#if currentStatusId?exists && currentStatusId == "LOYALTY_CREATED">
									<#if hasApproved>
										<span class="widget-toolbar none-content">
											<a class="btn btn-primary btn-mini" href="javascript:OlbLoyaltyView.acceptLoyalty();">
												<i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
							              	<a class="btn btn-danger btn-mini" href="javascript:OlbLoyaltyView.cancelLoyalty();">
												<i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
											<form name="LoyaltyAccept" method="post" action="<@ofbizUrl>changeLoyaltyStatus</@ofbizUrl>">
							                	<input type="hidden" name="statusId" value="LOYALTY_ACCEPTED">
								                <input type="hidden" name="loyaltyId" value="${loyalty.loyaltyId?if_exists}">
							              	</form>
											<form name="LoyaltyCancel" method="post" action="<@ofbizUrl>changeLoyaltyStatus</@ofbizUrl>">
							                	<input type="hidden" name="statusId" value="LOYALTY_CANCELLED">
								                <input type="hidden" name="loyaltyId" value="${loyalty.loyaltyId?if_exists}">
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
										<form name="updateLoyaltyThruDate" id="updateLoyaltyThruDate" method="POST" action="<@ofbizUrl>updateLoyaltyThruDate</@ofbizUrl>" style="float:left">
											<input type="hidden" name="loyaltyId" value="${loyalty.loyaltyId?if_exists}" />
											<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${loyalty.thruDate?if_exists}" event="" action="" className="" alert="" 
												title="Format: yyyy-MM-dd HH:mm:ss" size="25" maxlength="30" dateType="date" shortDateInput=false 
												timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
												classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
												pmSelected="" compositeType="" formName=""/>
										</form>
										<button class="btn btn-primary btn-mini" type="button" onclick="javascript:OlbLoyaltyView.updateLoyaltyThruDate();" style="float:left; margin-left:5px">
											<i class="icon-ok open-sans"></i>${uiLabelMap.BSUpdate}
										</button>
									</div>
								</div>
							</div><!--.span6-->
						</div>
					<#else>
						<span style="color:#D7432E">${uiLabelMap.BSThisLoyaltyHasExpired}</span>
					</#if>
				</#if>
			</div>
		</div>	
	</div>		
</div>

<#if hasOlbPermission("MODULE", "SALES_LOYALTY_APPROVE", "") && ("LOYALTY_CANCELLED" != loyalty.statusId)>
	<#if (loyalty.thruDate?exists && loyalty.thruDate &gt; nowTimestamp) || !(loyalty.thruDate?exists)>
		<#include "loyaltyViewScript.ftl"/>
	</#if>
</#if>