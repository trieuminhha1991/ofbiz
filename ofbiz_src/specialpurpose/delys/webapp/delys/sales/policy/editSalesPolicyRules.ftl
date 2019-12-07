<#function max value1 value2>
    <#if (value1 > value2)>
        <#return value1>
    <#else>
        <#return value2>
    </#if>
</#function>

<#if salesPolicyId?exists && salesPolicy?exists>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header header-color-blue2">
		<h4>${uiLabelMap.DAEditSalesPolicy}: ${uiLabelMap.DACreateRuleDetails}</h4>
		<div class="loading-image" style="width:20px;height:20px;float:left;padding-top:8px;"></div>
		<span class="widget-toolbar none-content">
			<#if salesPolicy.statusId == "SALES_PL_CREATED">
				<a href="#modal-table" role="button" data-toggle="modal"><i class="icon-plus-sign open-sans">${uiLabelMap.createNewRule}</i></a>
			</#if>
		</span>
	</div>
	<div class="widget-body">
		<div class="widget-body-inner">
			<div class="widget-main">
			<#if salesPolicyRules?has_content>
				<#if salesPolicy.statusId?exists && salesPolicy.statusId == "SALES_PL_CREATED">
					<div class="row-fluid">
						<#if salesPolicyRule?exists>
							<#assign salesPolicyConds = salesPolicyRule.getRelated("SalesPolicyCond", null, null, false)>
			    			<#assign salesPolicyActions = salesPolicyRule.getRelated("SalesPolicyAction", null, null, false)>
			    			<div style="width: 100%">
								<h4 style="font-weight: bold; display: inline-block">
									${uiLabelMap.DelysPromoRule} ${(salesPolicyRule.salesPolicyRuleId)?if_exists}: ${(salesPolicyRule.ruleName)?if_exists}
								</h4>
							</div>
			    			<h5 class="label label-large label-important" style="margin: 0; padding-top:7px; padding-bottom:7px; color:#D15B47;">
			    				<b>I.</b> ${uiLabelMap.ProductConditionsForRule} ${(salesPolicyRule.salesPolicyRuleId)?if_exists}
			    			</h5>
			    			<hr style="border-top: 1px solid #D15B47; margin:0; margin-top: 16px;" />
							<br />
							<#assign maxCondSeqId = 1>
							<#if salesPolicyConds?has_content>	
								<div style="margin-top: 15px; margin-left: 20px" class="span12">
									<table cellspacing="0" style="width: 100%" cellpadding="1" border="0" class="table table-bordered" >
										<thead>
											<tr>
												<th>${uiLabelMap.ProductCondition}</th>
												<th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
												<th>${uiLabelMap.DelysPromoCondition}</th>
												<#if salesPolicy.statusId?exists && salesPolicy.statusId=="SALES_PL_CREATED">
													<th>${uiLabelMap.CommonAction}</th>																
												</#if>
											</tr>
										</thead>
										<tbody>
										<#list salesPolicyConds as salesPolicyCond>
											  <#if (salesPolicyCond.salesPolicyCondSeqId)?exists>
										        <#assign curCondSeqId = Static["java.lang.Integer"].valueOf(salesPolicyCond.getString("salesPolicyCondSeqId"))>
										        <#if (curCondSeqId >= maxCondSeqId)>
										          <#assign maxCondSeqId = curCondSeqId + 1>
										        </#if>
										      </#if>
										     <tr align="center">
										     	<td>
										     		${uiLabelMap.ProductCondition} ${(salesPolicyCond.salesPolicyCondSeqId)?if_exists}
										     	</td>
										     	<td>
									     			<#assign condSalesPolicyCategories = salesPolicyCond.getRelated("SalesPolicyCategory", null, null, false)>
									     			<#if condSalesPolicyCategories?has_content>
										     		  	<#assign listCondCat = []>
												      	<#list condSalesPolicyCategories as condSalesPolicyCategory>
													       	<#assign condProductCategory = condSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
													       	<#assign condApplEnumeration = condSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>
													       	<#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
													       	<#assign includeSubCategoriesCond = condSalesPolicyCategory.includeSubCategories?default("N")>
													        <div class="margin-top2">
		                                            			<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
		                                            			${(condProductCategory.get("description",locale))?if_exists} [${condSalesPolicyCategory.productCategoryId}]
										                  	</div>
												      	</#list>
			        								</#if>
			        								
			        								<#assign condSalesPolicyProducts = salesPolicyCond.getRelated("SalesPolicyProduct", null, null, false)>
											      	<#if condSalesPolicyProducts?has_content>
											      		<#assign productCondList = []>
												      	<#list condSalesPolicyProducts as condSalesPolicyProduct>
												        	<#assign condProduct = condSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
												        	<#--<#assign condApplEnumeration = condSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
												       		<#assign productCondList = productCondList + [condProduct.productId]>
												        	<div class="margin-top2">
	                                            				<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
												        		${(condProduct.internalName)?if_exists} [${condSalesPolicyProduct.productId}]
			                 								</div>
												      	</#list>
											      	</#if>  
										     	</td>
										     	<td>
										     		<#if (salesPolicyCond.inputParamEnumId)?exists>
										     			<#assign inputParamEnum = salesPolicyCond.getRelatedOne("InputParamEnumeration", true)>
										     			<#if inputParamEnum?exists>
										     				${(inputParamEnum.get("description",locale))?if_exists}
										     			<#else>
										     				[${(salesPolicyCond.inputParamEnumId)?if_exists}]
										     			</#if> 
										     		</#if>
										     		&nbsp;
										     		<#if (salesPolicyCond.operatorEnumId)?exists>
			        									<#assign operatorEnum = salesPolicyCond.getRelatedOne("OperatorEnumeration", true)>
			        									<#if operatorEnum?exists>
			        										${(operatorEnum.get("description",locale))?if_exists}:
			       										<#else>
			       											[${(salesPolicyCond.operatorEnumId)?if_exists}]:
			     										</#if>
			        								</#if>
			        								${(salesPolicyCond.condValue)?if_exists}
	                                                <#if salesPolicyCond.condExhibited?exists>
	                                                    ${uiLabelMap.DelysExhibitedAt} ${salesPolicyCond.condExhibited} ${salesPolicyCond.notes}
	                                                </#if>
										     	</td>
										     	<td>
									                <div class="hidden-phone visible-desktop btn-group">
														<#--form update condition-->
										     			<form id="updateSalesPolicyCondition_${salesPolicyCond_index}" method="post" action="<@ofbizUrl>editSalesPolicyRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
										     				<input type="hidden" name="salesPolicyId" value="${(salesPolicyCond.salesPolicyId)?if_exists}" />
											                <input type="hidden" name="salesPolicyRuleId" value="${(salesPolicyCond.salesPolicyRuleId)?if_exists}" />
											                <input type="hidden" name="salesPolicyCondSeqId" value="${(salesPolicyCond.salesPolicyCondSeqId)?if_exists}"/>
															<button type="button" class="btn btn-mini btn-info" style="margin-bottom:0" onclick="javascript:updateSalesPolicyCond('updateSalesPolicyCondition_${salesPolicyCond_index}');">
																<i class="icon-edit bigger-120"></i>
															</button>
										     			</form>
								                		<#--end form update condition-->
								                		<form name="deleteSalesPolicyCondition_${salesPolicyCond_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyCond</@ofbizUrl>" style="display:inline-block">
										                  	<input type="hidden" name="salesPolicyId" value="${(salesPolicyCond.salesPolicyId)?if_exists}" />
										                  	<input type="hidden" name="salesPolicyRuleId" value="${(salesPolicyCond.salesPolicyRuleId)?if_exists}" />
										                  	<input type="hidden" name="salesPolicyCondSeqId" value="${(salesPolicyCond.salesPolicyCondSeqId)?if_exists}" />
										                  	<button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteSalesPolicyCondition_${salesPolicyCond_index}.submit();">
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
																	<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:updateSalesPolicyCond('updateSalesPolicyCondition_${salesPolicyCond_index}');">
																		<span class="green">
																			<i class="icon-edit bigger-120"></i>
																		</span>
																	</a>
																</li>
																<li>
																	<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteSalesPolicyCondition_${salesPolicyCond_index}.submit();">
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
									function updateSalesPolicyCond(formId){
										data = $("#" + formId).serialize();
										$.ajax({
											url: '<@ofbizUrl>updateSalesPolicyCondAjax</@ofbizUrl>',
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
								 ${screens.render("component://delys/widget/sales/SalesScreens.xml#CreateUpdateRuleCond")}		
							</div>
							
							<#-- action for rule-->
							<h5 id="rule-action-contain" class="label label-large label-important" style="margin: 0; padding-top:7px; padding-bottom:7px; color:#D15B47;">
			    				<b>II.</b> ${uiLabelMap.ProductActionForRule} ${(salesPolicyRule.salesPolicyRuleId)?if_exists}
			    			</h5>
			    			<hr style="border-top: 1px solid #D15B47; margin:0; margin-top: 18px;" />
							<br />
							<#if salesPolicyActions?has_content>
								<div style="margin-top: 15px; margin-left: 20px">
								<table cellspacing="0" style="width: 100%" cellpadding="1" border="0" class="table table-bordered" >
									<thead>
										<th>${uiLabelMap.ProductCondition}</th>
										<th>${uiLabelMap.DelysPromoCategoryProductApply}</th>
										<th>${uiLabelMap.DelysPromoAction}</th>
										<th>${uiLabelMap.CommonAction}</th>
									</thead>
									<tbody>
									<#list salesPolicyActions as salesPolicyAction>
										<tr>
											<td>${uiLabelMap.ProductAction} ${(salesPolicyAction.salesPolicyActionSeqId)?if_exists}</td>
											<td>
											  	<#assign actionSalesPolicyCategories = salesPolicyAction.getRelated("SalesPolicyCategory", null, null, false)>
										      	<#if actionSalesPolicyCategories?has_content>
										         	<#assign catActList = []>
										      	 	<#list actionSalesPolicyCategories as actionSalesPolicyCategory>
											        	<#assign actionProductCategory = actionSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
											        	<#--<#assign actionApplEnumeration = actionSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>-->
											        	<#assign catActList = catActList + [actionProductCategory.productCategoryId] >
											        	<div class="margin-top2">
	                                            			<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
											        		${(actionProductCategory.description)?if_exists} [${actionSalesPolicyCategory.productCategoryId}]
									                  	</div>
									                  	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyCategory.salesPolicyApplEnumId)} -->
									                  	<#--- - ${uiLabelMap.ProductSubCats}? ${actionSalesPolicyCategory.includeSubCategories?default("N")}
									                  	${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionSalesPolicyCategory.andGroupId} -->
								                 	</#list> 
										      	</#if>
										      
										      	<#assign actionSalesPolicyProducts = salesPolicyAction.getRelated("SalesPolicyProduct", null, null, false)>
										      	<#if actionSalesPolicyProducts?has_content>
											      	<#assign productActList = []>
											      	<#list actionSalesPolicyProducts as actionSalesPolicyProduct>
												      	<#assign actionProduct = actionSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
												        <#--<#assign actionApplEnumeration = actionSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
												        <#assign productActList = productActList + [actionProduct.productId]>
												        <div class="margin-top2">
	                                            			<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
												        	${(actionProduct.internalName)?if_exists} [${actionSalesPolicyProduct.productId}]
									                    </div>
									                    <#--- - ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyProduct.salesPolicyApplEnumId)} -->
											      	</#list>
										      	</#if>
											</td>
											<td>
												<#if (salesPolicyAction.salesPolicyActionEnumId)?exists>
	                                                <#assign salesPolicyActionCurEnum = salesPolicyAction.getRelatedOne("ActionEnumeration", true)>
	                                                <#if salesPolicyActionCurEnum?exists>
	                                                    ${(salesPolicyActionCurEnum.get("description",locale))?if_exists}:
	                                                <#else>
	                                                    [${(salesPolicyAction.salesPolicyActionEnumId)?if_exists}]:
	                                                </#if>
	                                                <br/>
			       								</#if>
	
			       								<input type="hidden" name="orderAdjustmentTypeId" value="${(salesPolicyAction.orderAdjustmentTypeId)?if_exists}" />
                                               	<ul class="unstyled no-bottom-margin">
                                                <#if salesPolicyAction.quantity?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysQuantity}: ${salesPolicyAction.quantity}<br/></li>
                                                </#if>
                                                <#if salesPolicyAction.amount?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysAmount}: ${salesPolicyAction.amount}<br/></li>
                                                </#if>
                                                <#if salesPolicyAction.productId?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysProductId}: ${salesPolicyAction.productId}<br/></li>
                                                </#if>
                                                <#if salesPolicyAction.partyId?exists>
                                                    <li><i class="icon-angle-right"></i>${uiLabelMap.DelysPartyId}: ${salesPolicyAction.partyId}<br/></li>
                                                </#if>
											</td>
											<td>
							                  	<div class="hidden-phone visible-desktop btn-group">
													<#--form update action-->
												  	<div class="span6">		 	
													  	<form name="updateSalesPolicyAction_${salesPolicyAction_index}" id="updateSalesPolicyAction_${salesPolicyAction_index}" action="<@ofbizUrl>editSalesPolicyRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
										                    <input type="hidden" name="salesPolicyId" value="${(salesPolicyAction.salesPolicyId)?if_exists}" />
										                    <input type="hidden" name="salesPolicyRuleId" value="${(salesPolicyAction.salesPolicyRuleId)?if_exists}" />
										                    <input type="hidden" name="salesPolicyActionSeqId" value="${(salesPolicyAction.salesPolicyActionSeqId)?if_exists}" />
										                   	<button type="button" class="btn btn-mini btn-info" style="margin-bottom:0" onclick="javascript:updateSalesPolicyAction('updateSalesPolicyAction_${salesPolicyAction_index}');">
																<i class="icon-edit bigger-120"></i>
															</button>
									                  	</form>
								                  	</div>	
											      	<#--end form update action-->
											      	<form name="deleteSalesPolicyAction_${salesPolicyAction_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyAction</@ofbizUrl>" style="display:inline-block">
									                    <input type="hidden" name="salesPolicyId" value="${(salesPolicyAction.salesPolicyId)?if_exists}" />
									                    <input type="hidden" name="salesPolicyRuleId" value="${(salesPolicyAction.salesPolicyRuleId)?if_exists}" />
									                    <input type="hidden" name="salesPolicyActionSeqId" value="${(salesPolicyAction.salesPolicyActionSeqId)?if_exists}" />
									                    <button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteSalesPolicyAction_${salesPolicyAction_index}.submit();">
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
																<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:updateSalesPolicyAction('updateSalesPolicyAction_${salesPolicyAction_index}');">
																	<span class="green">
																		<i class="icon-edit bigger-120"></i>
																	</span>
																</a>
															</li>
															<li>
																<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteSalesPolicyAction_${salesPolicyAction_index}.submit()">
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
									function updateSalesPolicyAction(formId){
										dataForm = $("#" + formId).serialize();
										$.ajax({
											url: '<@ofbizUrl>updateSalesPolicyActionAjax</@ofbizUrl>',
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
								${screens.render("component://delys/widget/sales/SalesScreens.xml#CreateUpdateRuleAction")}		
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
								<th rowspan="2">${uiLabelMap.DAPaymentPartyId}</th>
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
	                        <#list salesPolicyRules as rule>
	                            <#assign salesPolicyConds = rule.getRelated("SalesPolicyCond", null, null, false)>
	                            <#assign salesPolicyActions = rule.getRelated("SalesPolicyAction", null, null, false)>
	                            <#assign maxCondSeqId = 1>
	                            <#if salesPolicyConds?has_content>
	                                <#assign condSize = salesPolicyConds?size>
	                            <#else>
	                                <#assign condSize = 1>
	                            </#if>
	                            <#if salesPolicyActions?has_content>
	                                <#assign actionSize = salesPolicyActions?size>
	                                <#assign actionFist = salesPolicyActions?first>
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
	                            	<#assign sizeBrowse = salesPolicyConds?size >
	                            <#else>
	                            	<#assign sizeBrowse = salesPolicyActions?size >
	                            </#if>
	                            <#list 0..sizeBrowse as i>
	                            	<tr>
	                            		<#if i == 0>
	                            			<td rowspan="${rowSpan}">${rule_index + 1}</td>
	                            			<td rowspan="${rowSpan}">${rule.paymentParty?if_exists}</td>
	                                		<td rowspan="${rowSpan}">${rule.ruleName}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; salesPolicyConds?size) && (salesPolicyConds?size != 0)>
	                            			<#assign salesPolicyCondListOne = salesPolicyConds[i..i] />
                                        	<#assign salesPolicyCond = salesPolicyCondListOne?first />
	                            			<td <#if  i == (salesPolicyConds?size - 1)>rowspan="${rowSpanCondLast?if_exists}" </#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign condSalesPolicyCategories = salesPolicyCond.getRelated("SalesPolicyCategory", null, null, false)>
	                                            <#if condSalesPolicyCategories?has_content>
	                                              	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoCategory}: </span>-->
	                                              	<#assign listCondCat = []>
	                                              	<#list condSalesPolicyCategories as condSalesPolicyCategory>
	                                                   	<#assign condProductCategory = condSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
	                                                  	<#assign condApplEnumeration = condSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>
	                                                   	<#assign listCondCat = listCondCat + [condProductCategory.productCategoryId] >
	                                                   	<#assign includeSubCategoriesCond = condSalesPolicyCategory.includeSubCategories?default("N")>
                                                    	<div class="margin-top2">
                                                    		<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
                                                    		${(condProductCategory.get("description",locale))?if_exists} [${condSalesPolicyCategory.productCategoryId}]
	                                                  	</div>
	                                                  	<#--- ${(condApplEnumeration.get("description",locale))?default(condSalesPolicyCategory.salesPolicyApplEnumId)}
	                                                  	- ${uiLabelMap.DelysProductSubCats}: ${condSalesPolicyCategory.includeSubCategories?default("N")} -->
                                             	 	</#list>
	                                            <#--
	                                             <#else>
	                                                ${uiLabelMap.DelysProductNoConditionCategories}
	                                            -->
	                                            </#if>
	                                            
	                                            <#assign condSalesPolicyProducts = salesPolicyCond.getRelated("SalesPolicyProduct", null, null, false)>
                                              	<#if condSalesPolicyProducts?has_content>
                                              		<#--<span class="text-info display-block">${uiLabelMap.DelysPromoProduct}: </span>-->
                                              		<#assign productCondList = []>
	                                              	<#list condSalesPolicyProducts as condSalesPolicyProduct>
		                                                <#assign condProduct = condSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#--<#assign condApplEnumeration = condSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
		                                                <#assign productCondList = productCondList + [condProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
		                                                	${(condProduct.internalName)?if_exists} [${condSalesPolicyProduct.productId}]
		                                                </div>
		                                                <#-- - ${(condApplEnumeration.get("description",locale))?default(condSalesPolicyProduct.salesPolicyApplEnumId)} -->
	                                              	</#list>
                                              	<#--
                                              	<#else>
	                                                ${uiLabelMap.DelysProductNoConditionProducts}
                                              	-->
                                              	</#if>
	                                        </td>
	                                        <td <#if  i == (salesPolicyConds?size - 1)>rowspan="${rowSpanCondLast?if_exists}" </#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>">
                                              	<#if (salesPolicyCond.inputParamEnumId)?exists>
	                                                <#assign inputParamEnum = salesPolicyCond.getRelatedOne("InputParamEnumeration", true)>
	                                                <#if inputParamEnum?exists>
	                                                    ${(inputParamEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(salesPolicyCond.inputParamEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            <#if (salesPolicyCond.operatorEnumId)?exists>
	                                                <#assign operatorEnum = salesPolicyCond.getRelatedOne("OperatorEnumeration", true)>
	                                                <#if operatorEnum?exists>
	                                                    ${(operatorEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                    [${(salesPolicyCond.operatorEnumId)?if_exists}]
	                                                </#if>
	                                            </#if>
	                                            &nbsp;
	                                            ${(salesPolicyCond.condValue)?if_exists}
	                                        </td>
	                                	<#elseif salesPolicyConds?size == 0 && (i == 0)>
	                                    	<td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoConditionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if (i &lt; salesPolicyActions?size) && (salesPolicyActions?size != 0)>
	                            			<#assign salesPolicyActionListOne = salesPolicyActions[i..i] />
                                        	<#assign salesPolicyAction = salesPolicyActionListOne?first />
	                            			<td <#if i == (salesPolicyActions?size - 1)>rowspan="${rowSpanActionLast?if_exists}" </#if><#if i != 0>style="border-top: 1px dashed #e7e7e7;"</#if>>
	                                            <#assign actionSalesPolicyCategories = salesPolicyAction.getRelated("SalesPolicyCategory", null, null, false)>
	                                          	<#if actionSalesPolicyCategories?has_content>
	                                             	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoCategory}: </span>-->
	                                             	<#list actionSalesPolicyCategories as actionSalesPolicyCategory>
		                                                <#assign actionProductCategory = actionSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
		                                                <#--<#assign actionApplEnumeration = actionSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>-->
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoC} </span>
		                                                	${(actionProductCategory.description)?if_exists} [${actionSalesPolicyCategory.productCategoryId}]
		                                              	</div>
		                                              	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyCategory.salesPolicyApplEnumId)}
		                                              	- ${uiLabelMap.ProductSubCats}? ${actionSalesPolicyCategory.includeSubCategories?default("N")} -->
		                                              	<#--- ${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionSalesPolicyCategory.andGroupId}-->
	                                             	</#list>
	                                          	</#if>
	                                          	
	                                          	<#assign actionSalesPolicyProducts = salesPolicyAction.getRelated("SalesPolicyProduct", null, null, false)>
                                              	<#if actionSalesPolicyProducts?has_content>
	                                              	<#--<span class="text-info display-block">${uiLabelMap.DelysPromoProduct}: </span>-->
	                                              	<#assign productActList = []>
	                                              	<#list actionSalesPolicyProducts as actionSalesPolicyProduct>
		                                                <#assign actionProduct = actionSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
		                                                <#--<#assign actionApplEnumeration = actionSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
		                                                <#assign productActList = productActList + [actionProduct.productId]>
		                                                <div class="margin-top2">
		                                                	<span class="text-success span-product-category">${uiLabelMap.DelysPromoP} </span>
		                                                	${(actionProduct.internalName)?if_exists} [${actionSalesPolicyProduct.productId}]
		                                                </div>
		                                                <#-- - ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyProduct.salesPolicyApplEnumId)} -->
	                                              	</#list>
                                              	</#if>
	                                        </td>
	                                        <td <#if i == (salesPolicyActions?size - 1)>rowspan="${rowSpanActionLast?if_exists}" </#if>style="border-left: 1px dashed #e7e7e7;<#if i != 0> border-top: 1px dashed #e7e7e7;</#if>" nowrap>
	                                            <#if (salesPolicyAction.salesPolicyActionEnumId)?exists>
	                                            	<#assign salesPolicyActionCurEnum = salesPolicyAction.getRelatedOne("ActionEnumeration", true)>
		                                            <#if salesPolicyActionCurEnum?exists>
		                                                ${(salesPolicyActionCurEnum.get("description",locale))?if_exists}
	                                                <#else>
	                                                	[${(salesPolicyAction.salesPolicyActionEnumId)?if_exists}]
	                                            	</#if>
	                                            </#if>
	                                            &nbsp;
	                                            <input type="hidden" name="orderAdjustmentTypeId" value="${(salesPolicyAction.orderAdjustmentTypeId)?if_exists}" />
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
		                                <#elseif salesPolicyActions?size == 0 && (i == 0)>
		                                    <td colspan="2" rowspan="${rowSpan}">${uiLabelMap.NoActionApplyForRule}</td>
	                            		</#if>
	                            		
	                            		<#if i == 0>
	                            			<#if salesPolicy?exists && salesPolicy.statusId?exists && salesPolicy.statusId=="SALES_PL_CREATED">
			                                    <td rowspan="${rowSpan}" nowrap>
			                                    	<div class="hidden-phone visible-desktop btn-group">
														<form method="post" name="editSalesPolicyRulesForm" id="editSalesPolicyRulesForm" action="<@ofbizUrl>editSalesPolicyRules</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
				                                            <input type="hidden" name="salesPolicyId" value="${(rule.salesPolicyId)?if_exists}" />
				                                            <input type="hidden" name="salesPolicyRuleId" value="${(rule.salesPolicyRuleId)?if_exists}" />
				                                            <button type="submit" class="btn btn-mini btn-primary" style="margin-bottom:0">
																<i class="icon-edit bigger-120"></i>
															</button>
				                                        </form>
				                                        <form name="deleteSalesPolicyRule_${rule_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyRule</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
				                                            <input type="hidden" name="salesPolicyId" value="${(rule.salesPolicyId)?if_exists}" />
				                                            <input type="hidden" name="salesPolicyRuleId" value="${(rule.salesPolicyRuleId)?if_exists}" />
				                                            <button class="btn btn-mini btn-danger" style="margin-bottom:0" onclick="javascript:document.deleteSalesPolicyRule_${rule_index}.submit();">
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
																	<a href="#" class="tooltip-success" data-rel="tooltip" title="Edit" onclick="javascript:document.editSalesPolicyRulesForm.submit();">
																		<span class="green">
																			<i class="icon-edit bigger-120"></i>
																		</span>
																	</a>
																</li>
																<li>
																	<a href="#" class="tooltip-error" data-rel="tooltip" title="Delete" onclick="javascript:document.deleteSalesPolicyRule_${rule_index}.submit();">
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
					<h4>${uiLabelMap.DANoRuleApply}!</h4>
				</#if>
			</div>
		</div>
	</div>
	<div id="modal-table" class="modal hide fade" tabindex="-1">
		<form name="createSalesPolicyRule" method="post" action="<@ofbizUrl>createSalesPolicyRule</@ofbizUrl>" class="form-horizontal basic-custom-form form-small">
			<div class="modal-header no-padding">
				<div class="table-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					${uiLabelMap.createNewRule}
				</div>
			</div>
			<div class="modal-body no-padding">
				<div class="row-fluid">
					<input type="hidden" name="salesPolicyId" value="${salesPolicyId?if_exists}" />
					<div class="control-group">
						<label class="control-label" for="ruleName">${uiLabelMap.DelysRuleName} *:</label>
						<div class="controls">
							<div class="span12">
								<input type="text" size="30" name="ruleName" id="ruleName" />
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="paymentParty">${uiLabelMap.DAPaymentPartyId} *:</label>
						<div class="controls">
							<div class="span12">
								<@htmlTemplate.lookupField name="paymentParty" id="paymentParty" value='${parameters.paymentParty?if_exists}' 
											formName="createSalesPolicyRule" fieldFormName="LookupPartyName"/>
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
<script type="text/javascript">
	$(".chzn-select").chosen({
		search_contains: true
	});
</script>
</#if>