<#--
<div class="widget-header widget-header-blue widget-header-flat">
	<#if salesPolicyId?has_content && salesPolicy?exists>
		<h4 class="lighter">${uiLabelMap.DAEditSalesPolicy}: ${salesPolicy.policyName?if_exists} 
			(<a href="<@ofbizUrl>editSalesPolicy?salesPolicyId=${salesPolicy.salesPolicyId?if_exists}</@ofbizUrl>">${salesPolicy.salesPolicyId?if_exists}</a>)
		</h4>
		<span class="widget-toolbar none-content">
			// <a href="<@ofbizUrl>editSalesPolicy?salesPolicyId=${salesPolicy.salesPolicyId?if_exists}</@ofbizUrl>">
			//	<i class="icon-zoom-in open-sans">${uiLabelMap.DelysPromoViewPromotion}</i>
			// </a>
			<a href="<@ofbizUrl>editSalesPolicy</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.CreateSalesPolicy}</i>
			</a>
		</span>
	<#else>
		<h4 class="lighter">${uiLabelMap.DACreateSalesPolicy}: ${uiLabelMap.DelysStep} 1: ${uiLabelMap.DelysInitInformation}</h4>
		<span class="widget-toolbar none-content">
			// <a href="<@ofbizUrl>editProductPromotion</@ofbizUrl>">
			//	<i class="icon-plus open-sans">${uiLabelMap.DelysPromoCreateNewPromotion}</i>
			// </a>
		</span>
	</#if>
</div>
-->
		<div class="row-fluid">
			<#if salesPolicyId?has_content && salesPolicy?exists>
				<#if salesPolicy.statusId?exists && salesPolicy.statusId == "SALES_PL_CREATED">
					<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>editSalesPolicy</@ofbizUrl>" name="editSalesPolicy" id="editSalesPolicy"><#--<@ofbizUrl>updateProductPromo</@ofbizUrl>-->						
						<input type="hidden" name="salesPolicyId" value="${salesPolicyId}">
				<#else>
					<form class="form-horizontal basic-custom-form" name="editSalesPolicy">	
				</#if>
			<#else>
				<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>createSalesPolicy</@ofbizUrl>" name="editSalesPolicy" id="editSalesPolicy">								
					<input type="hidden" name="statusId" value="SALES_PL_CREATED">
			</#if>
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="salesPolicyId">${uiLabelMap.DASalesPolicyId}: </label>
							<div class="controls" style="padding-top:5px">
								<div class="span12">
									<#if salesPolicy?exists && salesPolicyId?has_content>
										${salesPolicy.salesPolicyId?if_exists}
									<#else>
										<input type="text" class="span12" name="salesPolicyId" id="salesPolicyId" value="${parameters.salesPolicyId?if_exists}"/>
									</#if>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="policyName">${uiLabelMap.DASalesPolicyName} <span class="red">*</span>: </label>
							<div class="controls">
								<div class="span12">
									<#if salesPolicy?exists && salesPolicyId?has_content>
										<input type="text" class="span12" name="policyName" id="policyName" value="${salesPolicy.policyName?if_exists}" <#if salesPolicy.salesPolicyStatusId?exists && salesPolicy.salesPolicyStatusId != "SALES_PL_CREATED">disabled="disabled"</#if>>
									<#else>
										<input type="text" class="span12" name="policyName" id="policyName" value="${parameters.policyName?if_exists}"/>
									</#if>
								</div>
							</div>
						</div>
						<#if salesPolicy?exists && salesPolicy.statusId?exists>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAStatus}: </label>
								<div class="controls" style="padding-top:5px">
									<div class="span12">
										<#if salesPolicy.statusId?exists>
											<#assign currentStatus = salesPolicy.getRelatedOne("StatusItem", true)/>
											<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
										</#if>
									</div>
								</div>
							</div>
						</#if>
						<div class="control-group">
							<label class="control-label" for="salesStatementId">${uiLabelMap.DASalesStatementId}: </label>
							<div class="controls">
								<div class="span12">
									<input type="text" id="salesStatementId" name="salesStatementId" data-maxlength="20" class="span12" 
										<#if salesPolicy?exists && salesPolicy.statusId?exists && salesPolicy.statusId != "SALES_PL_CREATED">disabled="disabled"</#if> 
										value="<#if salesPolicy?exists>${salesPolicy.salesStatementId?if_exists}</#if>"/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="policyText">${uiLabelMap.DASalesPolicyContent}: </label>
							<div class="controls">
								<div class="span12">
									<textarea id="policyText" name="policyText" data-maxlength="50" rows="2" class="span12" 
										<#if salesPolicy?exists && salesPolicy.statusId?exists && salesPolicy.statusId != "SALES_PL_CREATED">disabled="disabled"</#if>>
										<#if salesPolicy?exists>${salesPolicy.policyText?if_exists}</#if></textarea>
								</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="roleTypeIds">${uiLabelMap.DelysRoleTypeApply} <span class="red">*</span>: </label>
							<div class="controls" >
								<#if salesPolicyId?has_content && salesPolicy?exists>
									<div class="row-fluid">
										<div class="span8" style="border: 1px solid #eee">
											<!-- <input type="text" name="tags" id="listPartyId" value="" placeholder="Enter tags ..." /> -->
								          	<#if policyRoleTypeApply?has_content>
								          		<ul class="unstyled spaced2" style="margin: 0">
								             	<#list policyRoleTypeApply as role>
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
											<#if salesPolicy.statusId?exists && salesPolicy.statusId == "SALES_PL_CREATED">
									          	<a class="btn btn-mini btn-primary" href="<@ofbizUrl>EditProductPromoRoleApply?salesPolicyId=${salesPolicyId}</@ofbizUrl>"><i class="icon-edit open-sans"></i>${uiLabelMap.CommonEdit}</a>
								         	</#if>
								        </div>
							        </div>
						        <#else>									    		
								 	<select name="roleTypeIds" id="roleTypeIds" multiple="multiple" class="chzn-select" data-placeholder="${uiLabelMap.ClickToChoose}">
								 		<option value="">
								 		<#list roleTypeList as roleType>
								 			<option value="${roleType.roleTypeId}">${roleType.description} [${roleType.roleTypeId}]</option>
								 		</#list>
								 	</select>		
								</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="productStoreId">${uiLabelMap.DelysPromotionStore} <span class="red">*</span>: </label>
							<div class="controls">
								<#if salesPolicyId?has_content && salesPolicy?exists>
									<div class="row-fluid">
										<div class="span8" style="border: 1px solid #eee">
											<#if productStoreSalesPolicyAppl?has_content>
												<ul class="unstyled spaced2" style="margin: 0">
												<#list productStoreSalesPolicyAppl as store>
													<#assign productStore = delegator.findOne("ProductStore", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId", store.productStoreId), false)>
													<li style="margin-bottom: 0; margin-top:0"><i class="icon-plus green"></i>${productStore.storeName}</li>
												</#list>
												</ul>
											<#else>
												${uiLabelMap.DelysNoStoreApply}
											</#if>
										</div>
										<div class="span4">
											<#if salesPolicy.statusId?exists && salesPolicy.statusId == "SALES_PL_CREATED">
												<a class="btn btn-mini btn-primary" href="<@ofbizUrl>EditProductPromoStores?salesPolicyId=${salesPolicyId}</@ofbizUrl>">
													<i class="icon-edit open-sans"></i>${uiLabelMap.CommonEdit}
												</a>
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
							<label class="control-label" for="fromDate">${uiLabelMap.CommonFromDate} <span class="red">*</span>:</label>
							<div class="controls">
								<div class="span12">
									<#if salesPolicy?exists && salesPolicy.statusId?exists>
										 ${salesPolicy.fromDate?if_exists}
									<#else>
										<#if salesPolicy?exists><#assign salesPolicyFromDate = salesPolicy.fromDate?if_exists></#if>
										<@htmlTemplate.renderDateTimeField id="fromDate" name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"  
											value="${salesPolicyFromDate?if_exists}" size="25" maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									</#if>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="thruDate">${uiLabelMap.CommonThruDate}:</label>
							<div class="controls">
								<div class="span12">
									<#if salesPolicy?exists && salesPolicy.statusId?exists && salesPolicy.statusId != "SALES_PL_CREATED">
										 ${salesPolicy.thruDate?if_exists}
									<#else>
										<#if salesPolicy?exists><#assign salesPolicyThruDate = salesPolicy.thruDate?if_exists></#if>
										<@htmlTemplate.renderDateTimeField id="thruDate" name="thruDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS"  
											value="${salesPolicyThruDate?if_exists}" size="25" maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									</#if>												
								</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="clear-all"></div>
					<div class="control-group">
						<div class="span12">
							<div class="row-fluid wizard-actions">
								<#if salesPolicy?exists && salesPolicyId?has_content && salesPolicy.statusId?exists>
									<#if salesPolicy.statusId == "SALES_PL_CREATED">
										<button type="submit" id="editPromotion" class="btn btn-primary btn-small" data-last="Finish ">
											<i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate} 
										</button>
									</#if>
								<#else>
									<button type="submit" id="editPromotion" class="btn btn-primary btn-small" data-last="Finish ">
										${uiLabelMap.CommonNext} <i class="icon-arrow-right icon-on-right"></i>
									</button>
								</#if>
							</div>
						</div>
					</div>
				</div><!--.row-->
			</form>
		</div><!--.row-fluid-->
<div style="clear:both"></div>
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
		$("#editSalesPolicy").submit();
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
	<#--
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
	<#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "ACCUMULATE">			
		if($("input[name='miniRevenueId']").length > 0){
			$("input[name='miniRevenueId']").prop('disabled', true);
		}
	</#if>
	-->
</script>
