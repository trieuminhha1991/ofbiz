<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>

<#if productPromoId?exists && productPromo?exists>
<#if (productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId != "ACCUMULATE") || !(productPromo.productPromoTypeId?exists)> 
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header header-color-blue2">
		<h4>${uiLabelMap.DelysPromoEditProductPromo}: ${uiLabelMap.DelysCreateRulePromotionDetails}</h4>
		<div class="loading-image" style="width:20px;height:20px;float:left;padding-top:8px;"></div>
		<span class="widget-toolbar none-content">
			<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
				<a href="#modal-table" role="button" data-toggle="modal"><i class="icon-plus-sign open-sans">${uiLabelMap.createNewRule}</i></a>
			</#if>
		</span>
	</div>
	<div class="widget-body">
		<div class="widget-body-inner">
			<div class="widget-main">
			<#if productPromoRules?has_content>
				<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
					<div class="row-fluid">
						<#if productPromoRule?exists>
							<#assign productPromoConds = productPromoRule.getRelated("ProductPromoCond", null, null, false)>
			    			<#assign productPromoActions = productPromoRule.getRelated("ProductPromoAction", null, null, false)>
			    			<div style="width: 100%">
								<h4 style="font-weight: bold; display: inline-block">
									${uiLabelMap.DelysPromoRule} ${(productPromoRule.productPromoRuleId)?if_exists}: ${(productPromoRule.ruleName)?if_exists}
								</h4>
							</div>
			    			<h5 class="label label-large label-important" style="margin: 0; padding-top:7px; padding-bottom:7px; color:#D15B47;">
			    				<b>I.</b> ${uiLabelMap.ProductConditionsForRule} ${(productPromoRule.productPromoRuleId)?if_exists}
			    			</h5>
			    			<hr style="border-top: 1px solid #D15B47; margin:0; margin-top: 16px;" />
							<br />
							<#assign maxCondSeqId = 1>
							<#if productPromoConds?has_content>	
								<div style="margin-top: 15px; margin-left: 20px" class="span12">
									<table cellspacing="0" style="width: 100%" cellpadding="1" border="0" class="table table-bordered" >
										<thead>
											<tr>
												<th>${uiLabelMap.ProductCondition}</th>
												<th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
												<th>${uiLabelMap.DelysPromoCondition}</th>
												<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId=="PROMO_CREATED">
													<th>${uiLabelMap.CommonAction}</th>																
												</#if>
											</tr>
										</thead>
										<tbody>
										<#list productPromoConds as productPromoCond>
											  <#if (productPromoCond.productPromoCondSeqId)?exists>
										        <#assign curCondSeqId = Static["java.lang.Integer"].valueOf(productPromoCond.getString("productPromoCondSeqId"))>
										        <#if (curCondSeqId >= maxCondSeqId)>
										          <#assign maxCondSeqId = curCondSeqId + 1>
										        </#if>
										      </#if>
										     <tr align="center">
										     	<td>
										     		${uiLabelMap.ProductCondition} ${(productPromoCond.productPromoCondSeqId)?if_exists}
										     	</td>
										     	<td>
									     			<#assign condProductPromoCategories = productPromoCond.getRelated("ProductPromoCategory", null, null, false)>
									     			<#if condProductPromoCategories?has_content>
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
			        								</#if>
			        								
			        								<#assign condProductPromoProducts = productPromoCond.getRelated("ProductPromoProduct", null, null, false)>
											      	<#if condProductPromoProducts?has_content>
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
											      	</#if>
										     	</td>
										     	<td>
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
			        										${(operatorEnum.get("description",locale))?if_exists}:
			       										<#else>
			       											[${(productPromoCond.operatorEnumId)?if_exists}]:
			     										</#if>
			        								</#if>
			        								${(productPromoCond.condValue)?if_exists}
	                                                <#if productPromoCond.condExhibited?exists>
	                                                    ${uiLabelMap.DelysExhibitedAt} ${productPromoCond.condExhibited?if_exists} ${productPromoCond.notes?if_exists}
	                                                </#if>
										     	</td>
										     	<td>
									                <div class="hidden-phone visible-desktop btn-group">
														<#--form update condition-->
										     			<form id="updateProductPromoCondition_${productPromoCond_index}" method="post" action="<@ofbizUrl>EditProductPromoRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
										     				<input type="hidden" name="productPromoId" value="${(productPromoCond.productPromoId)?if_exists}" />
											                <input type="hidden" name="productPromoRuleId" value="${(productPromoCond.productPromoRuleId)?if_exists}" />
											                <input type="hidden" name="productPromoCondSeqId" value="${(productPromoCond.productPromoCondSeqId)?if_exists}"/>
															<button type="button" class="btn btn-mini btn-info" style="margin-bottom:0" onclick="javascript:updateProductPromoCond('updateProductPromoCondition_${productPromoCond_index}');">
																<i class="icon-edit bigger-120"></i>
															</button>
										     			</form>
								                		<#--end form update condition-->
								                		<form name="deleteProductPromoCondition_${productPromoCond_index}" method="post" action="<@ofbizUrl>deleteProductPromoCond</@ofbizUrl>" style="display:inline-block">
										                  	<input type="hidden" name="productPromoId" value="${(productPromoCond.productPromoId)?if_exists}" />
										                  	<input type="hidden" name="productPromoRuleId" value="${(productPromoCond.productPromoRuleId)?if_exists}" />
										                  	<input type="hidden" name="productPromoCondSeqId" value="${(productPromoCond.productPromoCondSeqId)?if_exists}" />
										                  	<button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteProductPromoCondition_${productPromoCond_index}.submit();">
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
																	<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:updateProductPromoCond('updateProductPromoCondition_${productPromoCond_index}');">
																		<span class="green">
																			<i class="icon-edit bigger-120"></i>
																		</span>
																	</a>
																</li>
																<li>
																	<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteProductPromoCondition_${productPromoCond_index}.submit();">
																		<span class="red">
																			<i class="icon-trash bigger-120"></i>
																		</span>
																	</a>
																</li>
															</ul>
														</div>
													</div>
										     	</td>
										     </tr> 
										</#list>
									</tbody>
								</table>
								</div>	
								<script type="text/javascript">
									function updateProductPromoCond(formId){
										data = $("#" + formId).serialize();
										$.ajax({
											url: '<@ofbizUrl>updateProductPromoCondAjax</@ofbizUrl>',
											type: 'GET',
											data: data,
											success: function(data){
												$("#createUpdateRuleCondition").html(data);
											}
										});
									}
								</script>
							<#else>
								<h4>
									${uiLabelMap.NoConditionApplyForRule}
								</h4>	
							</#if>
			 				<#-- ======================= Create/update condition for rule ======================== -->
							<#-- update condition for rule -->
							<div id="createUpdateRuleCondition">
								 ${screens.render("component://delys/widget/sales/promotions/SalesPromotionScreens.xml#createUpdateRuleCond")}		
							</div>
							
							<#-- action for rule-->
							<h5 id="rule-action-contain" class="label label-large label-important" style="margin: 0; padding-top:7px; padding-bottom:7px; color:#D15B47;">
			    				<b>II.</b> ${uiLabelMap.ProductActionForRule} ${(productPromoRule.productPromoRuleId)?if_exists}
			    			</h5>
			    			<hr style="border-top: 1px solid #D15B47; margin:0; margin-top: 18px;" />
							<br />
							<#if productPromoActions?has_content>
								<div style="margin-top: 15px; margin-left: 20px">
								<table cellspacing="0" style="width: 100%" cellpadding="1" border="0" class="table table-bordered" >
									<thead>
										<th>${uiLabelMap.ProductCondition}</th>
										<th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
										<th>${uiLabelMap.DelysPromoAction}</th>
										<th>${uiLabelMap.CommonAction}</th>
									</thead>
									<tbody>
									<#list productPromoActions as productPromoAction>
										<tr>
											<td>${uiLabelMap.ProductAction} ${(productPromoAction.productPromoActionSeqId)?if_exists}</td>
											<td>
											  	<#assign actionProductPromoCategories = productPromoAction.getRelated("ProductPromoCategory", null, null, false)>
										      	<#if actionProductPromoCategories?has_content>
										         	<#assign catActList = []>
										      	 	<#list actionProductPromoCategories as actionProductPromoCategory>
											        	<#assign actionProductCategory = actionProductPromoCategory.getRelatedOne("ProductCategory", true)>
											        	<#assign actionApplEnumeration = actionProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
											        	<#assign catActList = catActList + [actionProductCategory.productCategoryId] >
											        	<div class="margin-top2">
	                                            			<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
											        		${(actionProductCategory.description)?if_exists} [${actionProductPromoCategory.productCategoryId}]
									                  	</div>
									                  	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoCategory.productPromoApplEnumId)} -->
									                  	<#--- - ${uiLabelMap.ProductSubCats}? ${actionProductPromoCategory.includeSubCategories?default("N")}
									                  	${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionProductPromoCategory.andGroupId} -->
								                 	</#list> 
										      	</#if>
										      
										      	<#assign actionProductPromoProducts = productPromoAction.getRelated("ProductPromoProduct", null, null, false)>
										      	<#if actionProductPromoProducts?has_content>
											      	<#assign productActList = []>
											      	<#list actionProductPromoProducts as actionProductPromoProduct>
												      	<#assign actionProduct = actionProductPromoProduct.getRelatedOne("Product", true)?if_exists>
												        <#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
												        <#assign productActList = productActList + [actionProduct.productId]>
												        <div class="margin-top2">
	                                            			<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
												        	${(actionProduct.internalName)?if_exists} [${actionProductPromoProduct.productId}]
									                    </div>
									                    <#--- - ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoProduct.productPromoApplEnumId)} -->
											      	</#list>
										      	</#if>
											</td>
											<td>
												<#if (productPromoAction.productPromoActionEnumId)?exists>
	                                                <#assign productPromoActionCurEnum = productPromoAction.getRelatedOne("ActionEnumeration", true)>
	                                                <#if productPromoActionCurEnum?exists>
	                                                    ${(productPromoActionCurEnum.get("description",locale))?if_exists}:
	                                                <#else>
	                                                    [${(productPromoAction.productPromoActionEnumId)?if_exists}]:
	                                                </#if>
	                                                <br/>
			       								</#if>
	
			       								<input type="hidden" name="orderAdjustmentTypeId" value="${(productPromoAction.orderAdjustmentTypeId)?if_exists}" />
                                               	<ul class="unstyled no-bottom-margin">
                                                <#if productPromoAction.quantity?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysQuantity}: ${productPromoAction.quantity}<br/></li>
                                                </#if>
                                                <#if productPromoAction.amount?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysAmount}: ${productPromoAction.amount}<br/></li>
                                                </#if>
                                                <#if productPromoAction.productId?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysProductId}: ${productPromoAction.productId}<br/></li>
                                                </#if>
                                                <#if productPromoAction.partyId?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysPartyId}: ${productPromoAction.partyId}<br/></li>
                                                </#if>
											</td>
											<td>
							                  	<div class="hidden-phone visible-desktop btn-group">
													<#--form update action-->
												  	<div class="span6">		 	
													  	<form name="updateProductPromoAction_${productPromoAction_index}" id="updateProductPromoAction_${productPromoAction_index}" action="<@ofbizUrl>EditProductPromoRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
										                    <input type="hidden" name="productPromoId" value="${(productPromoAction.productPromoId)?if_exists}" />
										                    <input type="hidden" name="productPromoRuleId" value="${(productPromoAction.productPromoRuleId)?if_exists}" />
										                    <input type="hidden" name="productPromoActionSeqId" value="${(productPromoAction.productPromoActionSeqId)?if_exists}" />
										                   	<button type="button" class="btn btn-mini btn-info" style="margin-bottom:0" onclick="javascript:updateProductPromoAction('updateProductPromoAction_${productPromoAction_index}');">
																<i class="icon-edit bigger-120"></i>
															</button>
									                  	</form>
								                  	</div>	
											      	<#--end form update action-->
											      	<form name="deleteProductPromoAction_${productPromoAction_index}" method="post" action="<@ofbizUrl>deleteProductPromoAction</@ofbizUrl>" style="display:inline-block">
									                    <input type="hidden" name="productPromoId" value="${(productPromoAction.productPromoId)?if_exists}" />
									                    <input type="hidden" name="productPromoRuleId" value="${(productPromoAction.productPromoRuleId)?if_exists}" />
									                    <input type="hidden" name="productPromoActionSeqId" value="${(productPromoAction.productPromoActionSeqId)?if_exists}" />
									                    <button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteProductPromoAction_${productPromoAction_index}.submit();">
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
																<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:updateProductPromoAction('updateProductPromoAction_${productPromoAction_index}');">
																	<span class="green">
																		<i class="icon-edit bigger-120"></i>
																	</span>
																</a>
															</li>
															<li>
																<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteProductPromoAction_${productPromoAction_index}.submit()">
																	<span class="red">
																		<i class="icon-trash bigger-120"></i>
																	</span>
																</a>
															</li>
														</ul>
													</div>
												</div>
											</td>
										</tr>
									</#list>
									</tbody>
								</table>
								</div>
								<script type="text/javascript">
									function updateProductPromoAction(formId){
										dataForm = $("#" + formId).serialize();
										$.ajax({
											url: '<@ofbizUrl>updateProductPromoActionAjax</@ofbizUrl>',
											type: 'GET',
											data: dataForm,
											success: function(data){
												$("#createUpdateRuleAction").html(data);
											}
										});
									}					
								</script>
							<#else>
								<h4>
									${uiLabelMap.NoActionApplyForRule}
								</h4>	
							</#if>
							
						 	<#-- ======================= Create/update action for rule ======================== -->
							<div id="createUpdateRuleAction">
								${screens.render("component://delys/widget/sales/promotions/SalesPromotionScreens.xml#createUpdateRuleAction")}		
							</div>
						</#if>
					</div>
				</#if> <#-- end if check promotion status -->
				
<#-- List promotion rules ----------------------------- -->
				<div class="row-fluid">
					<div style="width: 100%">
						<h4 style="font-weight: bold; display: inline-block">${uiLabelMap.PromotionRuleAppyDelys}</h4>
					</div>
					
					<table cellspacing="0" cellpadding="1" border="0" class="table table-striped table-bordered">
						<thead>
							<tr>
								<th rowspan="2">${uiLabelMap.DelysSequenceOrder}</th>
								<th rowspan="2">${uiLabelMap.DelysRuleName}</th>
								<th colspan="2" class="align-center">${uiLabelMap.DelysConditon}</th>
								<th colspan="2" class="align-center">${uiLabelMap.DelysPromoAction}</th>
								<th rowspan="2">&nbsp;</th>
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
	                            			<td <#if  i == (productPromoConds?size - 1)>rowspan="${rowSpanCondLast?if_exists}" </#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
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
	                                        <td <#if  i == (productPromoConds?size - 1)>rowspan="${rowSpanCondLast?if_exists}" </#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>">
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
	                                	<#elseif productPromoConds?size == 0>
	                                    	<td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoConditionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; productPromoActions?size) && (productPromoActions?size != 0)>
	                            			<#assign productPromoActionListOne = productPromoActions[i..i] />
                                        	<#assign productPromoAction = productPromoActionListOne?first />
	                            			<td <#if i == (productPromoActions?size - 1)>rowspan="${rowSpanActionLast?if_exists}" </#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
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
		                                                <#assign actionProduct = actionProductPromoProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
		                                                <#assign productActList = productActList + [actionProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
		                                                	${(actionProduct.internalName)?if_exists} [${actionProductPromoProduct.productId}]
		                                                </div>
		                                                <#-- - ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoProduct.productPromoApplEnumId)} -->
	                                              	</#list>
                                              	</#if>
	                                        </td>
	                                        <td <#if i == (productPromoActions?size - 1)>rowspan="${rowSpanActionLast?if_exists}" </#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>" nowrap>
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
		                                <#elseif productPromoActions?size == 0>
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoActionApplyForRule}</td>
	                            		</#if>
	                            		
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
		                            </tr>
	                            </#list>
	                            
	                            <#--asdsadasdsa2-->
	                        </#list>
						</tbody>
					</table>
				</div>	
                    <script type="text/javascript" language="JavaScript" >
	                    /* $(".chzn-select").chosen();
	                    $(".chzn-select-deselect").chosen({allow_single_deselect:true}); */
                    </script>
				<#else>
					<h4>${uiLabelMap.NoRuleApplyForPromotion}!</h4>
				</#if>
			</div>
		</div>
	</div>
	<div id="modal-table" class="modal hide fade" tabindex="-1">
		<form method="post" action="<@ofbizUrl>createProductPromoRule</@ofbizUrl>" class="form-horizontal basic-custom-form form-small">
			<div class="modal-header no-padding">
				<div class="table-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					${uiLabelMap.createNewRule}
				</div>
			</div>
			<div class="modal-body no-padding">
				<div class="row-fluid">
					<input type="hidden" name="productPromoId" value="${productPromoId?if_exists}" />
					<div class="control-group">
						<label class="control-label" for="ruleName">${uiLabelMap.DelysRuleName} *:</label>
						<div class="controls">
							<div class="span12">
								<input type="text" size="30" name="ruleName" id="ruleName" />
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn btn-small btn-info pull-right">
					<i class="icon-ok"></i>${uiLabelMap.CommonAdd}
				</button>
				<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
					<i class="icon-remove"></i>${uiLabelMap.CommonClose}
				</button>
				<div class="pagination pull-right no-margin"></div>
			</div>
		</form>
	</div><!--.modal-table-->
</div>
<#else>
	<h3>${uiLabelMap.DelysProductPromoCannotCreateRule}</h3>
</#if>	
<script type="text/javascript">
	$(".chzn-select").chosen({
		search_contains: true
	});
</script>
</#if>