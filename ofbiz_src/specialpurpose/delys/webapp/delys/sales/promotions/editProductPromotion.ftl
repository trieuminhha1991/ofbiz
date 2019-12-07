<div class="widget-box transparent no-bottom-border" id="screenlet_1">
	<div class="widget-header">
		<#if productPromoId?has_content && productPromo?exists>
			<h4>${uiLabelMap.DelysPromoEditProductPromo}: ${productPromo.promoName?if_exists} 
				(<a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromo.productPromoId?if_exists}</@ofbizUrl>">${productPromo.productPromoId?if_exists}</a>)
			</h4>
			<span class="widget-toolbar none-content">
				<a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromo.productPromoId?if_exists}</@ofbizUrl>">
					<i class="icon-zoom-in open-sans">${uiLabelMap.DelysPromoViewPromotion}</i>
				</a>
				<a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>">
					<i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i>
				</a>
			</span>
		<#else>
			<h4>${uiLabelMap.DAInputInfoProductPromotion}</h4>
			<span class="widget-toolbar none-content">
				<#--
				<a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>">
					<i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i>
				</a>
				-->
			</span>
		</#if>
	</div>
	<div class="widget-body">
		<div id="screenlet_1_col" class="widget-body-inner">
			<div class="row-fluid">
				<#if productPromoId?has_content && productPromo?exists>
					<#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
						<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateProductPromo</@ofbizUrl>" name="editProductPromo" id="editProductPromo">								
							<input type="hidden" name="productPromoId" value="${productPromoId}">
					<#else>
						<form class="form-horizontal basic-custom-form" name="editProductPromo">	
					</#if>
				<#else>
					<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>createProductPromotion</@ofbizUrl>" name="editProductPromo" id="editProductPromo">								
						<input type="hidden" name="productPromoStatusId" value="PROMO_CREATED">		 	
				</#if>
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
											<input type="text" class="span12" name="promoName" id="promoName" value="${productPromo.promoName?if_exists}" <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">disabled="disabled"</#if>>
										<#else>
											<input type="text" class="span12" name="promoName" id="promoName" value="${parameters.promoName?if_exists}"/>
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
										<select id="productPromoTypeId" class="span12" name="productPromoTypeId" data-placeholder="Click to Choose..." disabled="disabled" style="background-color:#eee">
											<option></option>
											<#list promoTypes as promoType>
												<option value="${promoType.productPromoTypeId}" 
													<#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == promoType.productPromoTypeId>selected="selected"</#if>>
													${promoType.get("description",locale)}
												</option>
											</#list>
										</select>
									<#else>
										<select id="productPromoTypeId" class="span12" name="productPromoTypeId" data-placeholder="Click to Choose...">
											<option></option>
											<#list promoTypes as promoType>
												<option value="${promoType.productPromoTypeId}" 
													<#if parameters.productPromoTypeId?exists && (promoType.productPromoTypeId == parameters.productPromoTypeId)>selected="selected"</#if>>
													${promoType.get("description",locale)}
												</option>
											</#list>
										</select>
									</#if>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="promoText">${uiLabelMap.DAContent}</label>
								<div class="controls">
									<div class="span12">
										<textarea id="promoText" name="promoText" data-maxlength="50" rows="2" class="span12" <#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">disabled="disabled"</#if>><#if productPromo?exists>${productPromo.promoText?if_exists}</#if></textarea>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DelysPaymentMethod}</label>
								<div class="controls">
									<textarea id="" data-maxlength="50" name="paymentMethod" class="span12" <#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId!="PROMO_CREATED">disabled="disabled"</#if>><#if productPromo?exists>${productPromo.paymentMethod?if_exists}</#if></textarea>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label required" for="fromDate">${uiLabelMap.CommonFromDate}</label>
								<div class="controls">
									<div class="span12">
										<#if productPromo?exists && productPromo.fromDate?exists>
											 ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.fromDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
										<#else>
											<#assign fromDate = "">
											<#if productPromo?exists && productPromo.fromDate?exists><#assign fromDate = productPromo.fromDate></#if>
											<@htmlTemplate.renderDateTimeField id="fromDate" name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"  
												value="${fromDate}" size="25" maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
												timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
										</#if>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="thruDate">${uiLabelMap.CommonThruDate}</label>
								<div class="controls">
									<div class="span12">
										<#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">
											 ${productPromo.thruDate?if_exists}
										<#else>
											<#assign thruDate = "">
											<#if productPromo?exists && productPromo.thruDate?exists><#assign thruDate = productPromo.thruDate></#if>
											<@htmlTemplate.renderDateTimeField id="thruDate" name="thruDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"  
												value="${thruDate}" size="25" maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
												timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
										</#if>												
									</div>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
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
														<li style="margin-bottom: 0; margin-top:0"><i class="icon-user green green"></i>
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
									 	<select name="roleTypeIds" id="roleTypeIds" multiple="multiple" class="chzn-select" data-placeholder="${uiLabelMap.ClickToChoose}">
									 		<option value="">
									 		<#assign roleTypes = Static["com.olbius.util.SalesPartyUtil"].getListGVRoleMemberDescendantInGroup("DELYS_ROLE", delegator)/>
									 		<#list roleTypes as roleType>
									 			<option value="${roleType.roleTypeId}">${roleType.description} [${roleType.roleTypeId}]</option>
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
														<li style="margin-bottom: 0; margin-top:0"><i class="icon-plus green"></i>${productStore.storeName}</li>
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
										<select id="productStoreId" name="productStoreIds" class="chzn-select" multiple="multiple"
											data-placeholder="${uiLabelMap.ClickToChoose}">
											<#list productStores as store>
												<option value="${store.productStoreId}">${store.storeName}</option>
											</#list>													
										</select>
									</#if>			
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="productStoreId">${uiLabelMap.DAShowToCustomer}</label>
								<div class="controls">
									<select id="showToCustomer" name="showToCustomer">
										<option value="N" <#if productPromoId?has_content && productPromo?exists && productPromo.showToCustomer == "N">selected="selected"</#if>>${uiLabelMap.DACHNo}</option>
										<option value="Y" <#if productPromoId?has_content && productPromo?exists && productPromo.showToCustomer == "Y">selected="selected"</#if>>${uiLabelMap.DACHYes}</option>
									</select>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DelysBudgetTotal}</label>
								<div class="controls">
									<div class="span12">
										<#if promoBudgetDist?exists>
											<#assign budgetTotal = promoBudgetDist.budgetId?if_exists> 
										</#if>
										<div>
											<#if productPromoId?has_content && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">
												<#if promoBudgetDist?has_content>
													${promoBudgetDist.budgetId}
												<#else>
													${uiLabelMap.DelysNoBudgetDistributionApply}
												</#if>
											<#else>
												<#if productPromo?exists>
													<@htmlTemplate.lookupField formName="editProductPromo" name="budgetId" id="budgetId"
														value="${budgetTotal?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>	
												<#else>
													<@htmlTemplate.lookupField formName="editProductPromo" name="budgetId" id="budgetId"
														value="${parameters.budgetId?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>	
												</#if>
											</#if>
										</div>
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
										<label class="control-label" for="promoSalesTargets">${uiLabelMap.DelysSalesTargets}</label>
										<div class="controls">
											<div class="span12 input-prepend">														
												<input type="text" name="promoSalesTargets" id="promoSalesTargets" value="${productPromo.promoSalesTargets?if_exists}" disabled="disabled">
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
						</div><!--.span6-->
					</div><!--.row-->
					
					<div class="row-fluid wizard-actions">
						<#if productPromo?exists && productPromoId?has_content && productPromo.productPromoStatusId?exists>
							<#if productPromo.productPromoStatusId == "PROMO_CREATED">
								<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
									<i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate} 
								</button>
							</#if>
						<#else>
							<a class="btn btn-small" href="productPromoList">
								<i class="icon-remove open-sans"></i>${uiLabelMap.DACancel}
							</a>
							<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
								<i class="icon-ok open-sans"></i>${uiLabelMap.DACreate}
							</button>
						</#if>
					</div>
				</form>
			</div><!--.row-fluid-->
		</div>
	</div>
</div>
<style type="text/css">
	#productStoreId_chzn, #roleTypeIds_chzn {
		margin-bottom:10px;
	}
</style>
<script type="text/javascript">
	$(".chzn-select").chosen({
		search_contains: true
	}); 		
	/* $(document).on('click','.close', function removeTag(){ 
			$(this).parent().remove(); 
	});
	if ( $("#tag").length > 0 ) { addTagBindings('#tag'); }
	$("#editPromotion").click(function(){
		$("#tag-cloud").children().each(function(){
			var roleTypeId = $(this).attr('id');
			$("#roleTypeIds").append(new Option(roleTypeId, roleTypeId, "selected", "selected"));	
		});
		$("#editProductPromo").submit();
	});
	function addTagBindings(id) {
		$('#addParty').click(function(){ addTag(); });
		//$(id + ' > input').keyup(function (e) {  if (e.keyCode == 13) { addTag(id); }  });	

	}
	function addTag() {
		
		var Tag = $('input[name="listPartyId"]').val();
		
		var tagClass = '';
	
		// Setup our class based on what type of container we have everything inside 
		 tagClass = 'tag-cloud btn-success';

		// If there is no value in the input field then don't do anything
		if (Tag != '') {

			// Append tag with proper styling into the tag cloud 
			$('<span class="tag" id="'+ Tag +'">'+Tag+'<button type="button" class="close">×</button></span>').appendTo("#tag-cloud");
			
			// Clear input back to nothing
			$('input[name="listPartyId"]').val('');		
		}	
	} */
	/*
	$("#productPromoTypeId").change(function(){
		var selectPromoType = $(this).val();
		if(selectPromoType == "ACCUMULATE"){
			$("input[name='miniRevenueId']").prop('disabled', true);
			$("input[name='promoSalesTargets']").prop('disabled', false);
		}else if(selectPromoType == "EXHIBITED" || selectPromoType == "PROMOTION"){
			$("input[name='promoSalesTargets']").prop('disabled', true);
			$("input[name='miniRevenueId']").prop('disabled', false);
		}
	});
	*/
	<#if productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "ACCUMULATE">			
		if($("input[name='miniRevenueId']").length > 0){
			$("input[name='miniRevenueId']").prop('disabled', true);
		}
	</#if>
</script>
