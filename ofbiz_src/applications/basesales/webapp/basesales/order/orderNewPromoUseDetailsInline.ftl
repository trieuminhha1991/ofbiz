<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#include "script/orderNewPromoUseDetailsInlineScript.ftl"/>
<#assign hasOrConds = false/>
<div class="row-fluid" style="padding-top:20px">
	<div class="span12">
	<#if productPromoUseInfos?exists>
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.OrderPromotionInformation}</h4> 
		<button id="recalculatePromotion" class="btn btn-mini btn-primary" style="margin-left:10px; display:none"><i class="fa-refresh"></i>${uiLabelMap.BSRecalculatePromotion}</button></h5>
		
		<div class="row-fluid">
			<div class="span6">
				<h5 class="smaller green">${uiLabelMap.OrderPromotionInformation}</h5>
				<form name="formRecalculatePromotion2" id="formRecalculatePromotion2" action="recalculateOrderPromo" method="POST">
					<ul>    
	        		<#list productPromoUseInfos as productPromoUseInfo>
	                	<li style="list-style: none;">
	                    	<#-- TODO: when promo pretty print is done show promo short description here -->
	                      	<div style="display:inline-block; vertical-align:top">
		                      	<i class="icon-caret-right blue"></i>${uiLabelMap.OrderPromotion}: ${productPromoUseInfo.productPromoId?if_exists} 
		                      	<a href="<@ofbizUrl>viewPromotion?productPromoId=${productPromoUseInfo.productPromoId?if_exists}</@ofbizUrl>" target="_blank">${uiLabelMap.CommonDetails}</a>
	                      	</div>
	                      	<div style="display:inline-block; vertical-align:top">
	                      		<#if productPromoUseInfo.productPromoCodeInfos?exists>
		                    		<p><#list productPromoUseInfo.productPromoCodeInfos as productPromoCodeInfo>
		                    			<#if productPromoCodeInfo.productPromoCodeId?has_content> - ${uiLabelMap.OrderWithPromoCode} [${productPromoCodeInfo.productPromoCodeId}]</#if>
				                    	<#if productPromoCodeInfo.totalDiscountAmount?exists && (productPromoCodeInfo.totalDiscountAmount != 0)> - ${uiLabelMap.CommonTotalValue} <@ofbizCurrency amount=(-1*productPromoCodeInfo.totalDiscountAmount) isoCode=shoppingCart.getCurrency()/></#if>
				                    	<#if productPromoCodeInfo.productPromoCodeId?has_content>
				                    		<#--<@ofbizUrl>removePromoCode?promoCode=${productPromoCodeInfo.productPromoCodeId?if_exists}</@ofbizUrl>-->
				                        	<a href="javascript:void(0);" onclick="removePromoCode('${productPromoCodeInfo.productPromoCodeId?if_exists?trim}');" class="btn btn-info btn-minier">${uiLabelMap.BSRemoveApply}</a>
				                    	</#if>
		                    		</p></#list>
		                    	</#if>
	                      	</div>
	                	</li>
	                	<#if productPromoUseInfo.quantityLeftInActions?exists && (productPromoUseInfo.quantityLeftInActions > 0)>
	                    	<li style="list-style: none;"> <i class="icon-caret-right blue"></i>- Could be used for ${productPromoUseInfo.quantityLeftInActions} more discounted item<#if (productPromoUseInfo.quantityLeftInActions > 1)>s</#if> if added to your cart.</li>
	                	</#if>
	            	</#list>
	        		
	        		<#--
	        		<#list productPromoUseInfos as productPromoUseInfo>
	                	<li style="list-style: none;"><i class="icon-caret-right blue"></i>
	                    	//TODO: when promo pretty print is done show promo short description here
	                      	${uiLabelMap.OrderPromotion}: ${productPromoUseInfo.productPromoId?if_exists} <a href="<@ofbizUrl>viewPromotion?productPromoId=${productPromoUseInfo.productPromoId?if_exists}</@ofbizUrl>" target="_blank">${uiLabelMap.CommonDetails}</a>
	                    	<#if productPromoUseInfo.productPromoCodeId?has_content> - ${uiLabelMap.OrderWithPromoCode} [${productPromoUseInfo.productPromoCodeId}]</#if>
	                    	<#if productPromoUseInfo.totalDiscountAmount?exists && (productPromoUseInfo.totalDiscountAmount != 0)> - ${uiLabelMap.CommonTotalValue} <@ofbizCurrency amount=(-1*productPromoUseInfo.totalDiscountAmount) isoCode=shoppingCart.getCurrency()/></#if>
	                    	<#if productPromoUseInfo.productPromoCodeId?has_content>
	                    		//<@ofbizUrl>removePromoCode?promoCode=${productPromoUseInfo.productPromoCodeId?if_exists}</@ofbizUrl>
	                        	<a href="javascript:void(0);" onclick="removePromoCode('${productPromoUseInfo.productPromoCodeId?if_exists?trim}');" class="btn btn-info btn-mini">${uiLabelMap.BSRemoveApply}</a>
	                    	</#if>
	                	</li>
	                	<#if productPromoUseInfo.quantityLeftInActions?exists && (productPromoUseInfo.quantityLeftInActions > 0)>
	                    	<li style="list-style: none;"> <i class="icon-caret-right blue"></i>- Could be used for ${productPromoUseInfo.quantityLeftInActions} more discounted item<#if (productPromoUseInfo.quantityLeftInActions > 1)>s</#if> if added to your cart.</li>
	                	</#if>
	            	</#list>
	        		-->
	        		</ul>
        		</form>
			</div><!--.span6-->
			<div class="span6">
				<h5 class="smaller green">${uiLabelMap.BSPromotionDetails}</h5>
				<form name="formRecalculatePromotion" id="formRecalculatePromotion" action="recalculateOrderPromo" method="POST">
					<ul>
					<#assign actionOrIndex = 0/>
	        		<#list productPromoUseInfos as productPromoUseInfo>
	                	<li style="list-style: none;">
	                    	<#if productPromoUseInfo.productPromoRules?exists>
		                		<#list productPromoUseInfo.productPromoRules.entrySet() as entry>
		                			<#assign ruleId = entry.getKey()/>
		                			<#assign ruleContent = entry.getValue()/>
		                			<#--
		                			<#assign ruleDisplayName = ruleContent.ruleName?default("")/>
		                			<#assign lengthRuleDisplayName = ruleDisplayName?length/>
		                			-->
		                    		<div class="row-fluid">
			                    		<div class="span2"><i class="icon-star blue"></i>${ruleContent.ruleName?if_exists}</div>
			                    		<div class="span10">
			                    			<#if ruleContent.productPromoActions?exists>
			                    			<#list ruleContent.productPromoActions as productPromoAction>
				                    			<#if productPromoAction.operatorEnumId?exists && productPromoAction.operatorEnumId == "PPAOP_OR">
					                    			<#assign hasOrConds = true/>
					                    			<label class="margin-bottom10 margin-top10">
														<input name="actionsOr_o_${actionOrIndex}" type="radio" <#if productPromoAction.statusUse?has_content && productPromoAction.statusUse == "Y">checked="true"</#if> value="${productPromoAction.entityPK?if_exists}">
														<span class="lbl font-common">&nbsp;${StringUtil.wrapString(productPromoAction.description)}</span>
													</label>
												<#else>
													<label class="margin-bottom15 margin-top10">
														<input name="actionsAnd_o_${productPromoUseInfo_index}" type="checkbox" class="disabled" <#if productPromoAction.statusUse?has_content && productPromoAction.statusUse == "Y">checked="true"</#if> value="${productPromoAction.entityPK?if_exists}" onclick="return false" onkeydown="return false">
														<span class="lbl font-common">&nbsp;${StringUtil.wrapString(productPromoAction.description)}</span>
													</label>
												</#if>
											</#list>
											</#if>
			                    		</div>
		                    		</div>
		                    		<#assign actionOrIndex = actionOrIndex + 1/>
		                		</#list>
	                    	</#if>
	                	</li>
	            	</#list>
	        		</ul>
        		</form>
        		<#if hasOrConds>
        		<script type="text/javascript">
        			$("#recalculatePromotion").show();
        		</script>
        		</#if>
				<#-- Old
					<h5 class="smaller green">${uiLabelMap.OrderPromotionInformation} <button id="recalculatePromotion">${uiLabelMap.BSRecalculatePromotion}</button></h5>
					<form name="formRecalculatePromotion2" id="formRecalculatePromotion2" action="recalculateOrderPromo" method="POST">
						<ul>    
		        		<#list productPromoUseInfoIter as productPromoUseInfo>
		                	<li style="list-style: none; margin-top5"> <i class="icon-caret-right blue"></i>
		                    	// TODO: when promo pretty print is done show promo short description here 
		                      		${uiLabelMap.OrderPromotion} <a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromoUseInfo.productPromoId?if_exists}</@ofbizUrl>" class="btn btn-mini btn-info">${uiLabelMap.CommonDetails}</a>
		                    	<#if productPromoUseInfo.productPromoCodeId?has_content> - ${uiLabelMap.OrderWithPromoCode} [${productPromoUseInfo.productPromoCodeId}]</#if>
		                    	<#if (productPromoUseInfo.totalDiscountAmount != 0)> - ${uiLabelMap.CommonTotalValue} <@ofbizCurrency amount=(-1*productPromoUseInfo.totalDiscountAmount) isoCode=shoppingCart.getCurrency()/></#if>
		                    	<#if productPromoUseInfo.productPromoCodeId?has_content>
		                        	<a href="<@ofbizUrl>removePromotion?promoCode=${productPromoUseInfo.productPromoCodeId?if_exists}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.OrderRemovePromotion}</a>
		                    	</#if>
		                    	<#if productPromoUseInfo.statusUsePerCondActual?exists>
			                		<#list productPromoUseInfo.statusUsePerCondActual.entrySet() as entry>
			                			<#assign entityPK = entry.getKey()/>
			                			<#assign statusUse = entry.getValue()/>
			                    		<#assign productPromoAction = delegator.findOne(entityPK.getEntityName(), entityPK, false)/>
			                    		<div class="row-fluid">
				                    		<div class="span1">${productPromoAction?if_exists.productPromoRuleId}</div>
				                    		<div class="span11">
				                    			<#if productPromoAction.operatorEnumId == "PPAOP_OR">
					                    			<label>
														<input name="actionsOr" type="radio" <#if statusUse == "Y">checked="true"</#if> value="${entityPK}">
														<span class="lbl">${productPromoAction.productPromoActionEnumId} ${productPromoAction.quantity}</span>
													</label>
												<#else>
													<label>
														<input name="actionsAnd" type="checkbox" class="disabled" <#if statusUse == "Y">checked="true"</#if> value="${entityPK}" onclick="return false" onkeydown="return false">
														<span class="lbl">${productPromoAction.productPromoActionEnumId} ${productPromoAction.quantity}</span>
													</label>
												</#if>
				                    		</div>
			                    		</div>
			                		</#list>
		                    	</#if>
		                	</li>
		                	<#if (productPromoUseInfo.quantityLeftInActions > 0)>
		                    	<li style="list-style: none;"> <i class="icon-caret-right blue"></i>- Could be used for ${productPromoUseInfo.quantityLeftInActions} more discounted item<#if (productPromoUseInfo.quantityLeftInActions > 1)>s</#if> if added to your cart.</li>
		                	</#if>
		            	</#list>
		        		</ul>
	        		</form>
				-->
				<#-- Old
					<h5 class="smaller green">${uiLabelMap.OrderCartItemUseinPromotions}</h5>
					Hien thi thong tin khuyen mai cua tung order item
					Lay lai o file promoUseDetailsInline.ftl
				-->
			</div><!--.span6-->
		</div>
	</#if>
		<#-- voucher info -->
		<div class="row-fluid">
			<div style="display:inline-block; margin-right:5px">
				<h5 class="smaller green">${uiLabelMap.BSVoucherCode}</h5>
			</div>
			<div style="display:inline-block;vertical-align:middle">
				<form method="post" action="<@ofbizUrl>addPromoCode</@ofbizUrl>" id="formAddPromoCode" name="addpromocodeform">
					<div class="row-fluid">
						<div class="span12">
							<input type="text" size="15" id="productPromoCodeId" name="productPromoCodeId" value=""/>
							<button type="button" id="btnAddPromoCode" class="btn btn-primary btn-mini open-sans"><i class="icon-ok"></i>${uiLabelMap.OrderAddCode}</button>
						</div>
					</div>
			        <table>
						<tr>
							<td>
			          			<#assign productPromoCodeIds = (shoppingCart.getProductPromoCodesEntered())?if_exists>
					          	<#if productPromoCodeIds?has_content>
						            ${uiLabelMap.OrderEnteredPromoCodes}:
						            <#list productPromoCodeIds as productPromoCodeId>
						              	${productPromoCodeId}
						            </#list>
					          	</#if>
			          		</td>
			          	</tr>
					</table>
		        </form>
			</div>
		</div>
        <script type="text/javascript">
        	$(function(){
        		jOlbUtil.input.create($("#productPromoCodeId"));
        	});
        </script>
	</div>
</div>
