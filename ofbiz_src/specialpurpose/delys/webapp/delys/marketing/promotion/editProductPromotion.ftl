<div class="widget-box transparent no-bottom-border" id="screenlet_1">
	<div class="widget-header">
		<#if productPromoId?has_content && productPromo?exists>
		<h4>${uiLabelMap.DelysPromoEditProductPromo}: ${productPromo.promoName?if_exists}
		(<a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromo.productPromoId?if_exists}</@ofbizUrl>">${productPromo.productPromoId?if_exists}</a>) </h4>
		<span class="widget-toolbar none-content"> <a href="<@ofbizUrl>ViewMarketingPromos?productPromoId=${productPromo.productPromoId?if_exists}</@ofbizUrl>"> <i class="icon-zoom-in open-sans">${uiLabelMap.DelysPromoViewPromotion}</i> </a> <a href="<@ofbizUrl>EditPromosMarketing</@ofbizUrl>"> <i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i> </a> </span>
		<#else>
		<h4>${uiLabelMap.DAInputInfoProductPromotion}</h4>
		<span class="widget-toolbar none-content"> <#-- <a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>"> <i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i> </a> --> </span>
		</#if>
	</div>
	<div class="widget-body">
		<div id="screenlet_1_col" class="widget-body-inner">
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide margin-bottom-30" data-target="#step-container">
					<ul class="wizard-steps">
						<li data-target="#step1" class="active">
							<span class="step">1</span>
							<span class="title">${uiLabelMap.generalInformation}</span>
						</li>
						<li data-target="#step2">
							<span class="step">2</span>
							<span class="title">${uiLabelMap.DACreateRuleDetails}</span>
						</li>
					</ul>
				</div>
				<div class="step-content row-fluid position-relative" id="step-container">
					<#if productPromoId?has_content && productPromo?exists>
					<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
					<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateMarketingPromos</@ofbizUrl>" name="editProductPromo" id="editProductPromo">
						<input type="hidden" name="productPromoId" value="${productPromoId}">
						<#else>
						<form class="form-horizontal basic-custom-form" name="editMarketingPromos">
							</#if>
							<#else>
							<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>createMarketingPromos</@ofbizUrl>" name="editProductPromo" id="editProductPromo">
								<input type="hidden" name="productPromoStatusId" value="PROMO_CREATED">
								</#if>
								<div class="step-pane active" id="step1">
									<div class="row-fluid">
										<div class="span6">
											<div class="control-group">
												<label class="control-label" for="productPromoId">${uiLabelMap.DAProgramId}</label>
												<div class="controls">
													<div class="span12">
														<#if productPromo?exists && productPromoId?has_content>
														${productPromo.productPromoId?if_exists}
														<#else>
														<input type="text" class="span12" name="productPromoId" id="productPromoId" value="${parameters.productPromoId?if_exists}"/>
														</#if>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label required" for="promoName">${uiLabelMap.DAProgramName}</label>
												<div class="controls">
													<div class="span12">
														<#if productPromo?exists && productPromoId?has_content>
														<input type="text" class="span12" name="promoName" required id="promoName" value="${productPromo.promoName?if_exists}" <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">disabled="disabled"</#if>>
														<#else>
														<input type="text" class="span12" name="promoName" required id="promoName" value="${parameters.promoName?if_exists}"/>
														</#if>
													</div>
												</div>
											</div>
											<#if productPromo?exists && productPromo.productPromoStatusId?exists>
											<div class="control-group">
												<label class="control-label">${uiLabelMap.DelysProductPromoStatusId}</label>
												<div class="controls">
													<div class="span12">
														<#if productPromo.productPromoStatusId?exists>
														<#assign currentStatus = productPromo.getRelatedOne("StatusItem", true)/>
														<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
														</#if>
													</div>
												</div>
											</div>
											</#if>
											<div class="control-group">
												<label class="control-label required" for="productPromoTypeId">${uiLabelMap.DelysPromotionType}</label>
												<div class="controls">
													<#--<#if productPromo.productPromoTypeId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">-->
													<#if productPromo?exists && productPromo.productPromoTypeId?exists>
													<select id="productPromoTypeId" required class="span12" name="productPromoTypeId"disabled style="background-color:#eee">
														<option></option>
														<#list promoTypes as promoType>
														<option value="${promoType.productPromoTypeId}"
														<#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == promoType.productPromoTypeId>selected="selected"</#if>> ${promoType.get("description",locale)} </option>
														</#list>
													</select>
													<#else>
													<select id="productPromoTypeId" required class="span12" name="productPromoTypeId">
														<option></option>
														<#list promoTypes as promoType>
														<option value="${promoType.productPromoTypeId}"
														<#if parameters.productPromoTypeId?exists && (promoType.productPromoTypeId == parameters.productPromoTypeId)>selected="selected"</#if>> ${promoType.get("description",locale)} </option>
														</#list>
													</select>
													</#if>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label required" for="roleTypeIds">${uiLabelMap.DelysRoleTypeApply}</label>
												<div class="controls" >
													<#if productPromo?exists && productPromoId?has_content>
													<div class="row-fluid">
														<div class="span8">
															<!-- <input type="text" name="tags" id="listPartyId" value="" placeholder="Enter tags ..." /> -->
															<#if promoRoleTypeApply?has_content>
															<ul class="unstyled spaced2" style="margin: 0">
																<#list promoRoleTypeApply as role>
																<li style="margin-bottom: 0; margin-top:0">
																	<i class="icon-user green green"></i>
																	<span id="${role.roleTypeId}">${role.roleTypeId}</span>
																</li>
																</#list>
															</ul>
															<#else>
															${uiLabelMap.DelysNoRoleTypeApply}
															</#if>
														</div>
														<div class="span4">
															<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
															<a href="<@ofbizUrl>EditProductPromoRoleApply?productPromoId=${productPromoId}</@ofbizUrl>"><i class="icon-edit open-sans"></i>(${uiLabelMap.CommonEdit})</a>
															</#if>
														</div>
													</div>
													<#else>
													<select name="roleTypeIds" id="roleTypeIds" multiple="multiple" class="chzn-select fullwidth" data-placeholder="${uiLabelMap.ClickToChoose}">
														<option value=""> <#list roleTypeList as roleType> <option value="${roleType.roleTypeId}">${roleType.description} [${roleType.roleTypeId}]</option>
														</#list>
													</select>
													</#if>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label required" for="productStoreId">${uiLabelMap.DelysPromotionStore}</label>
												<div class="controls">
													<#if productPromoId?has_content && productPromo?exists>
													<div class="row-fluid">
														<div class="span8">
															<#if productStorePromoAppl?has_content>
															<ul class="unstyled spaced2" style="margin: 0">
																<#list productStorePromoAppl as store>
																<#assign productStore = delegator.findOne("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", store.productStoreId), false)>
																<li style="margin-bottom: 0; margin-top:0">
																	<i class="icon-plus green"></i>${productStore.storeName}
																</li>
																</#list>
															</ul>
															<#else>
															${uiLabelMap.DelysNoStoreApply}
															</#if>
														</div>
														<div class="span4">
															<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
															<a href="<@ofbizUrl>EditProductPromoStores?productPromoId=${productPromoId}</@ofbizUrl>"><i class="icon-edit open-sans"></i>(${uiLabelMap.CommonEdit})</a>
															</#if>
														</div>
													</div>
													<#else>
													<select id="productStoreId" name="productStoreIds" class="chzn-select fullwidth" multiple="multiple"
													data-placeholder="${uiLabelMap.ClickToChoose}">
														<#list productStores as store>
														<option value="${store.productStoreId}">${store.storeName}</option>
														</#list>
													</select>
													</#if>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="promoText">${uiLabelMap.DAContent}</label>
												<div class="controls">
													<div class="span12">
														<textarea id="promoText" name="promoText" data-maxlength="50" rows="2" class="span12 no-resize fullwidth" <#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">disabled="disabled"</#if>><#if productPromo?exists>${productPromo.promoText?if_exists}</#if></textarea>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">${uiLabelMap.DelysPaymentMethod}</label>
												<div class="controls">
													<textarea id="" data-maxlength="50" name="paymentMethod" class="span12 no-resize" <#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId!="PROMO_CREATED">disabled="disabled"</#if>><#if productPromo?exists>${productPromo.paymentMethod?if_exists}</#if></textarea>
												</div>
											</div>
										</div><!--.span6-->
										<div class="span6">
											<div class="control-group">
												<label class="control-label">${uiLabelMap.DelysBudgetTotal}</label>
												<div class="controls">
													<div class="span12">
														<input type="hidden" id="budgetId" name="budgetId"/>
														<div id='budgetSelected'></div>
														<script>
															var budgets = [
															<#if budgets?exists>
															<#list budgets as budget>
															{
															budgetId : "${budget.budgetId?if_exists}",
															budgetName : "${StringUtil.wrapString(budget.comments?default(''))}",
															},
															</#list>
															</#if>
															];
														</script>
													</div>
												</div>
											</div>
											<#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">
											<#if productPromo.productPromoTypeId?exists && (productPromo.productPromoTypeId == "EXHIBITED" || productPromo.productPromoTypeId == "PROMOTION")>
											<div class="control-group">
												<div class="span12">
													<label class="control-label">${uiLabelMap.DelysMiniRevenue}</label>
													<div class="controls">
														<div class="span12">
															<div>
																<#if promoMiniRevenue?has_content>
																${promoMiniRevenue.budgetId?if_exists}
																<#else>
																${uiLabelMap.DelysNoMiniRevenueApply}
																</#if>
															</div>
														</div>
													</div>
												</div>
											</div>
											<#elseif productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId=="ACCUMULATE">
											<div class="control-group">
												<label class="control-label" for="promoSalesTargets">${uiLabelMap.marketingPlace}</label>
												<div class="controls">
													<div class="span12 input-prepend">
														<input type="number" name="promoSalesTargets" id="promoSalesTargets" value="${productPromo.promoSalesTargets?if_exists}" disabled="disabled">
													</div>
												</div>
											</div>
											</#if>
											<#else>
											<div class="control-group">
												<div class="span12">
													<label class="control-label">${uiLabelMap.DelysMiniRevenue}</label>
													<div class="controls">
														<div class="span12">
															<div>
																<#if promoMiniRevenue?exists>
																<#assign miniRevenue = promoMiniRevenue.budgetId?if_exists>
																</#if>
																<@htmlTemplate.lookupField formName="editProductPromo" name="miniRevenueId" id="miniRevenueId"
																value="${miniRevenue?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="promoSalesTargets">${uiLabelMap.DelysSalesTargets}</label>
												<div class="controls">
													<div class="span12 input-prepend">
														<input type="text" name="promoSalesTargets" id="promoSalesTargets" value="<#if productPromo?exists>${productPromo.promoSalesTargets?if_exists}</#if>" id="salesTarget"
														<#if productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId != "ACCUMULATE">disabled="disabled"</#if>>
													</div>
												</div>
											</div>
											</#if>	<#-- end if productPromo.productPromoStatusId -->
											<#if productPromo?exists && productPromo.createdDate?exists>
											<div class="control-group">
												<label class="control-label">${uiLabelMap.DACreatedDate}</label>
												<div class="controls" style="padding-top: 3px;">
													<div class="span12">
														${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.createdDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
													</div>
												</div>
											</div>
											</#if>
											<div class="control-group">
												<label class="control-label required" for="promoSalesTargets">${uiLabelMap.marketingPlace}</label>
												<div class="controls">
													<div class="span12 input-prepend">
														<input type="hidden" id="geoId" name="geoId" required/>
														<div id="chooseProvince">
															<script>
																var province = [
																<#if provinces?exists>
																<#list provinces as geo>
																{
																geoId : "${geo.geoId?if_exists}",
																geoName : "${StringUtil.wrapString(geo.geoName?default(''))}",
																geoNameFrom: "${StringUtil.wrapString(geo.geoNameFrom?default(''))}"
																},
																</#list>
																</#if>
																];
															</script>
														</div>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label required" for="fromDate">${uiLabelMap.CommonFromDate}</label>
												<div class="controls">
													<div class="span12">
														<div id="fromDate"></div>
														<input name="fromDate" type="hidden"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="thruDate">${uiLabelMap.CommonThruDate}</label>
												<div class="controls">
													<div class="span12">
														<div id="thruDate"></div>
														<input name="thruDate" type="hidden"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="thruDate">${uiLabelMap.isActive}</label>
												<div class="controls">
													<div class="span12">
														<select id="isActive" name="isActive">
															<option value='N'>N</option>
															<option value='Y'>Y</option>
														</select>
													</div>
												</div>
											</div>
										</div><!--.span6-->
									</div><!--.row-->
								</div>
								<div class="step-pane" id="step2">
									<div class='row-fluid'>
										<div class='span6'>
											<div class="control-group">
												<label class="control-label required" for="">${uiLabelMap.DelysRuleName}</label>
												<div class="controls">
													<div class="span12">
														<input type="text" name='ruleName'/>
													</div>
												</div>
											</div>
										</div>
										<div class='span6'>
											<button class="btn btn-info" id="createRuleBt" type="button"><i class="icon-ok"></i>
												${uiLabelMap.DACreateRuleDetails}
											</button>
										</div>
									</div>
									<h3>${uiLabelMap.DelysProductPromoCondition}</h3>
									<div class="row-fluid">
										<div class="span6">
											<div class="control-group">
												<label class="control-label">${uiLabelMap.ProductProductName}:</label>
												<div class="controls">
													<select name="productIdListCond" id="productIdListCond" multiple class="chzn-select" data-placeholder="${uiLabelMap.ClickToChoose}">
														<option value=""></option>
														<#list productList as product>
														<option value="${product.productId}">${product.internalName} [${product.productId}]</option>
														</#list>
													</select>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">${uiLabelMap.ProductCondition}</label>
												<div class="controls">
													<span class="span12">
														<select class="span8" id="" name="inputParamEnumId">
															<#list inputParamEnums as inputParamEnum>
															<option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.get("description",locale))?if_exists}</option>
															</#list>
														</select>
														<select class="span4" id="" name="operatorEnumId">
															<#list condOperEnums as condOperEnum>
															<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
															</#list>
														</select> </span>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label required">${uiLabelMap.ProductConditionValue}:</label>
												<div class="controls">
													<span class="span12">
														<input type="number" size="25" name="condValue"/>
													</span>
												</div>
											</div>
										</div>
										<div class="span6">
											<div class="control-group">
												<label class="control-label">${uiLabelMap.DelysCategoryName}:</label>
												<div class="controls">
													<div class="row-fluid">
														<select name="productCatIdListCond" multiple class="chzn-select"
														id="productCatIdListCond" data-placeholder="${uiLabelMap.ClickToChoose}">
															<option value=""></option>
															<#list productCategoryList as category>
															<option value="${category.productCategoryId}">${category.categoryName}</option>
															</#list>
														</select>
													</div>
													<div class="row-fluid" style="margin-top: 10px">
														<input type="hidden" name="productPromoApplEnumId" value="PPPA_INCLUDE" />
														<input type="hidden" name="includeSubCategories" value="N" />
														<#--
														<select name="productPromoApplEnumId">
															<#list productPromoApplEnums as productPromoApplEnum>
															<option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.get("description",locale)}</option>
															</#list>
														</select>
														<select name="includeSubCategories">
															<option value="N">${uiLabelMap.CommonN}</option>
															<option value="Y">${uiLabelMap.CommonY}</option>
														</select>
														-->
													</div>
												</div>
											</div>
										</div><!-- .span6 -->
									</div>
									<h3>${uiLabelMap.DelysProductPromoAction}</h3>
									<div class="row-fluid">       
							         	<div class="span6">
							         		<div class="control-group">
												<label class="control-label">${uiLabelMap.ProductProductName}:</label>
												<div class="controls">
													<select name="productIdListAction" id="productIdListAction" multiple class="chzn-select" data-placeholder="${uiLabelMap.ClickToChoose}">
												      	<option value=""></option>	
													  	<#list productList as product>
													  		<option value="${product.productId}">${product.internalName} [${product.productId}]</option>
													  	</#list> 			      	
									     			</select>
												</div>												
											</div>
							           	</div>
							     		<div class="span6">
											<div class="control-group">
							           			<label class="control-label">${uiLabelMap.DelysCategoryName}:</label>
												<div class="controls">
													<div class="row-fluid">
														<select name="productCatIdListAction" multiple class="chzn-select" 
											      			id="productCatIdListAction" data-placeholder="${uiLabelMap.ClickToChoose}">
													      	<option value=""></option>	
														  	<#list productCategoryList as category>
														  		<option value="${category.productCategoryId}">${category.categoryName}</option>
														  	</#list> 			      	
											     		</select>
										     		</div>
										     		<div class="row-fluid" style="margin-top: 10px">
										     			<input type="hidden" name="productPromoApplEnumId" value="PPPA_INCLUDE" />
										              	<input type="hidden" name="includeSubCategories" value="N" />
										              	<#--
										              	<select name="productPromoApplEnumId" class="span8">
													      <#list productPromoApplEnums as productPromoApplEnum>
													         <option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.get("description",locale)}</option>
													      </#list>
									                    </select>
									                    <select name="includeSubCategories" class="span4">
									                      <option value="N">${uiLabelMap.CommonN}</option>
									                      <option value="Y">${uiLabelMap.CommonY}</option>
									                    </select>
										              	-->
								                    </div>
												</div>
							             	</div>
										</div><!-- .span6 -->
									</div>
									<div class="row-fluid">
							        	<div class="span12">
							        		<div class="control-group">
												<label class="control-label">${uiLabelMap.ProductAction}</label>
												<div class="controls">
													<span class="span12">
														<select name="productPromoActionEnumId">
														    <#list productPromoActionEnums as productPromoActionEnum>
														       	<option value="${(productPromoActionEnum.enumId)?if_exists}">${(productPromoActionEnum.get("description",locale))?if_exists}</option>
														    </#list>
										                </select>
													</span>
												</div>
											</div>
							        	</div>
							        </div>
									<div class="row-fluid">
							        	<div class="span6">
							        		<div class="control-group" id="quantityContainer">
												<label class="control-label required">${uiLabelMap.ProductQuantity}</label>
												<div class="controls">
													<span class="span12">
														<input type="number" name="quantity" />
													</span>												
												</div>
											</div>
											<div class="control-group hide" id="amountContainer">
												<label class="control-label required">
													<span class='hide' id="percent">
														${uiLabelMap.DAPercent}
													</span>
													<span class='show' id='amount'>
														${uiLabelMap.ProductAmount}
													</span>
												</label>
												<div class="controls">
													<span class="span12">
														<input type="text" name="amount" />
													</span>												
												</div>
											</div>
							        	</div>
							        	<div class="span6 hide" >
							        		<div class="control-group">
												<label class="control-label">${uiLabelMap.ProductItemId}</label>
												<div class="controls">
													<span class="span12">
														<input  name="productId" type="hidden"/>
													</span>												
												</div>
											</div>
											
							        	</div>
							        </div>
							        <input type="hidden" name="rules"/>
								</div>
								<!-- <div class="row-fluid wizard-actions control-action">
									<#if productPromo?exists && productPromoId?has_content && productPromo.productPromoStatusId?exists>
									<#if productPromo.productPromoStatusId == "PROMO_CREATED">
									<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
										<i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate}
									</button>
									</#if>
									<#else>
									<a class="btn btn-small" href="productPromoList"> <i class="icon-remove open-sans"></i>${uiLabelMap.DACancel} </a>
									<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
										<i class="icon-ok open-sans"></i>${uiLabelMap.DACreate}
									</button>
									</#if>
								</div> -->
							</form>

				</div>
				<div class="row-fluid wizard-actions control-action">
					<button class="btn btn-prev btn-success">
						<i class="icon-arrow-left"></i>
						Prev
					</button>

					<button class="btn btn-next btn-primary" data-last="Finish ">
						Next
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
				</div>
			</div><!--.row-fluid-->
		</div>
	</div>
</div>
<style type="text/css">
	#productStoreId_chzn, #roleTypeIds_chzn {
		margin-bottom: 10px;
	}
</style>
<script type="text/javascript">
	var rules = [];
	$(document).ready(function(){
		$("#createRuleBt").click(function(){
			createRule();
		});
  	    $("#fromDate").jqxDateTimeInput({
		     height: '30px',
		     theme: 'olbius',
		     value: null
		});
		$("#fromDate").on("change", function(event){
			var val = event.timeStamp;
			var format = Utils.formatDateYMD(val);
			$("input[name='fromDate']").val(format);
		});
		$("#thruDate").jqxDateTimeInput({
		     height: '30px',
		     theme: 'olbius',
		     value: null
		});
		$("#thruDate").on("change", function(event){
			var val = event.timeStamp;
			var format = Utils.formatDateYMD(val);
			$("input[name='thruDate']").val(format);
		});
		$("select[name='productPromoActionEnumId']").change(function(){
			var val = $(this).val();
			switch(val){
				case "PROMO_GWP" : 
					changeState(0);
					break;
				case "PROMO_PROD_DISC" :
					changeState(3);
				 	break;
				case "PROMO_PROD_AMDISC":
					changeState(4);
					break;
				case "PROMO_PROD_PRICE":
					changeState(3);
					break;
				case "PROMO_ORDER_PERCENT":
					changeState(2);
					break;
				case "PROMO_ORDER_AMOUNT":
					changeState(1);
					break;
				case "PROMO_PROD_SPPRC":
					changeState(1);
					break;
				case "PROMO_SHIP_CHARGE":
					changeState(2);
					break;
				case "PROMO_SERVICE":
					changeState();
					break;
				case "PROMO_TAX_PERCENT":
					changeState(2);
					break;
				default:
					changeState();
					break;
			};
		});
		$('#editProductPromo').submit(function() {
		  $("input[name='rules']").val(JSON.stringify(rules));
		});
		$('#chooseProvince').jqxDropDownList({
			theme: 'olbius',
			source: province,
			displayMember: "geoName",
			dropDownHeight: 200,
			// dropDownWidth: 400,
			filterable: true,
			incrementalSearch:true,
			searchMode: "containsignorecase",
			renderer: function (index, label, value) {
			var datarecord = province[index];
			var table = "<div style='padding-top:5px'>"+datarecord.geoName+"</div>"
			+ "<div style='padding'><b>${uiLabelMap.Country}</b>:&nbsp;" + datarecord.geoNameFrom + "</div>";
			return table;
			}
		});
		$("#chooseProvince").on("change", function(event){
			var args = event.args;
		   	if (args) {
		       var index = args.index;
		       var value = province[index];
		       $("#geoId").val(value.geoId);
		   	}
		});
		$("#budgetSelected").jqxDropDownList({
			theme: 'olbius',
			source: budgets,
			displayMember: "budgetId",
			dropDownHeight: 300,
			dropDownWidth: 300,
			filterable: true,
			incrementalSearch:true,
			searchMode: "containsignorecase",
			renderer: function (index, label, value) {
			var datarecord = budgets[index];
			var table = "<div style='padding-top: 5px;'><b>${uiLabelMap.BudgetId}:&nbsp;</b>"+datarecord.budgetId+"</div>"
			+ "<div style='padding: 5px 0;'>" + datarecord.budgetName + "</div>";
			return table;
			}
		});
		$("#budgetSelected").on("change", function(event){
			var args = event.args;
		   	if (args) {
		       var index = args.index;
		       var value = budgets[index];
		       $("#budgetId").val(value.budgetId);
		   	}
		});
		$('#fuelux-wizard').ace_wizard().on('change', function(e, info){
			if(info.step == 1 && info.direction == "next"){
				var valid = checkFormState1();
				if(!valid){
					return false
				}
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.confirmCreateMessage}", function(e){
				if(e){
					$('#editProductPromo').removeAttr("novalidate");
					$('#editProductPromo').submit();
				}
			});
		});
		<#if productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "ACCUMULATE">
		if($("input[name='miniRevenueId']").length > 0){
		$("input[name='miniRevenueId']").prop('disabled', true);
		}
		</#if>
		// $('#editProductPromo').jqxValidator({
            // rules: [{ input: '#promoName', message: 'Promo name is required', action: 'keyup, blur', rule: 'required' },
                   // { input: '#productPromoTypeId', message: 'Promotion type is required', action: 'keyup, blur', rule: 'required' },
                   // { input: '#roleTypeIds', message: 'Roles apply is required!', action: 'keyup, blur', rule: 'required' },
                   // { input: '#productStoreId', message: 'Scopes apply is required', action: 'keyup, blur', rule: 'required' },
                   // { input: '#chooseProvince', message: 'Place apply is required', action: 'select', rule: 'required' }]
		// });
	});
	function createRule(){
		var rule = getRule();
		rules.push(rule);
	}
	function getRule(){
		return {
			ruleName: $("input[name='ruleName']").val(),
			cond: {
				products : $("#productIdListCond").val(),
				categories: $("#productCatIdListCond").val(),
				inputParamEnumId: $("select[name='inputParamEnumId']").val(),
				operatorEnumId: $("select[name='operatorEnumId']").val(),
				condValue : $("input[name='condValue']").val()
			},
			action: {
				products : $("#productIdListAction").val(),
				categories: $("#productCatIdListAction").val(),
				productPromoActionEnumId: $("select[name='productPromoActionEnumId']").val(),
				quantity: $("input[name='quantity']").val(),
				amount: $("input[name='amount']").val(),
				productId: $("input[name='productId']").val()
			}
		};
	}
	function resetRuleForm(){
		$("input[name='ruleName']").val("");
		$("#productIdListCond").val("");
		$("#productIdListCond").trigger("liszt:updated");
		$("select[name='inputParamEnumId']").val("");
		$("select[name='operatorEnumId']").val("");
		$("input[name='condValue']").val("condValue");
		$("#productCatIdListCond").val("");
		$("#productCatIdListCond").trigger("liszt:updated");
		$("#productIdListAction").val("");
		$("#productIdListAction").trigger("liszt:updated");
		$("#productCatIdListAction").val("");
		$("#productCatIdListAction").trigger("liszt:updated");
		$("select[name='productPromoActionEnumId']").val("");
		$("input[name='quantity']").val("");
		$("input[name='productId']").val("");
		$("input[name='amount']").val("");
	}
	function renderListRule(){
		
	}
	function checkFormState1(){
		var valid = $("#promoName").valid() &&
					$("#productPromoTypeId").valid() &&
					$("#roleTypeIds").valid() &&
					$("#productStoreId").val() &&
					$("#geoId").val() ? true : false;
		return valid;
	}
	function checkFormState2(){
		
	}
	function changeState(state){
		switch(state){
			case 0 : 
				$("#quantityContainer").show();
				$("#amountContainer").hide();
				break;
			case 1 :
				$("#quantityContainer").hide();
				$("#amountContainer").show();
				$("#percent").hide();
				$("#amount").show();
				break;
			case 2 :
				$("#quantityContainer").hide();
				$("#amountContainer").show();
				$("#percent").show();
				$("#amount").hide();
				break;
			case 3 :
				$("#quantityContainer").show();
				$("#amountContainer").show();
				$("#percent").show();
				$("#amount").hide();
				break;
			case 4 :
				$("#quantityContainer").show();
				$("#amountContainer").show();
				$("#percent").hide();
				$("#amount").show();
				break;
			default : 
				$("#quantityContainer").hide();
				$("#amountContainer").show();
				$("#percent").hide();
				$("#amount").show();
		}
	}
</script>
