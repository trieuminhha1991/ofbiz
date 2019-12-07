<#if policyAction?exists && policyAction?has_content>
	<#assign actionSalePolicyCategories = policyAction.getRelated("SalesPolicyCategory", null, null, false)>
    <#-- ================Categories promo action================= -->
    <div class="control-group form-horizontal">
 	 	<label class="control-label" for="">${uiLabelMap.DelysCategoryName}:</label>
 	 	<div class="controls">
		 	<form method="post" action="<@ofbizUrl>createSalesPolicyCategory</@ofbizUrl>" name="createPPCatAction">
                <input type="hidden" name="salesPolicyId" value="${salesPolicyId}" />
                <input type="hidden" name="salesPolicyRuleId" value="${policyAction.salesPolicyRuleId}" />
                <input type="hidden" name="salesPolicyActionSeqId" value="${policyAction.salesPolicyActionSeqId}" />
                <input type="hidden" name="salesPolicyCondSeqId" value="_NA_" />
                <input type="hidden" name="andGroupId" value="_NA_"/>
                <@htmlTemplate.lookupField formName="createPPCatAction" name="productCategoryId" id="productCategoryId_act" fieldFormName="LookupProductCategory"/>
                <input type="hidden" name="salesPolicyApplEnumId" value="SPPA_INCLUDE" />
              	<input type="hidden" name="includeSubCategories" value="N" />
              	<#--
              	<select name="salesPolicyApplEnumId">
	      			<#list salesPolicyApplEnums as salesPolicyApplEnum>
	                  	<option value="${salesPolicyApplEnum.enumId}">${salesPolicyApplEnum.get("description",locale)}</option>
	      			</#list>
              	</select>
              	<select name="includeSubCategories">
                	<option value="N">${uiLabelMap.CommonN}</option>
                	<option value="Y">${uiLabelMap.CommonY}</option>
              	</select>
              	-->
                <button type="submit" class="btn btn-mini btn-primary"><i class="icon-plus"></i>${uiLabelMap.CommonAdd}</button>
          	</form>
			<#list actionSalePolicyCategories as actionSalesPolicyCategory>
		        <#assign actionProductCategory = actionSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
		        <#--<#assign actionApplEnumeration = actionSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>-->
               	<div>
                  	${(actionProductCategory.description)?if_exists} [${actionSalesPolicyCategory.productCategoryId}]
                  	- ${uiLabelMap.ProductSubCats}? ${actionSalesPolicyCategory.includeSubCategories?default("N")}
                  	- ${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionSalesPolicyCategory.andGroupId}
                  	<#--- ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyCategory.salesPolicyApplEnumId)}-->
                  	<form name="deleteSalesPolicyCategoryAction_${actionSalesPolicyCategory_index}" action="<@ofbizUrl>deleteSalesPolicyCategory</@ofbizUrl>" method="post">
                		<input type="hidden" name="salesPolicyId" value="${(actionSalesPolicyCategory.salesPolicyId)?if_exists}" />
	                    <input type="hidden" name="salesPolicyRuleId" value="${(actionSalesPolicyCategory.salesPolicyRuleId)?if_exists}" />
	                    <input type="hidden" name="salesPolicyCondSeqId" value="${(actionSalesPolicyCategory.salesPolicyCondSeqId)?if_exists}" />
	                    <input type="hidden" name="salesPolicyActionSeqId" value="${(actionSalesPolicyCategory.salesPolicyActionSeqId)?if_exists}" />
	                    <input type="hidden" name="productCategoryId" value="${(actionSalesPolicyCategory.productCategoryId)?if_exists}" />
	                    <input type="hidden" name="andGroupId" value="${(actionSalesPolicyCategory.andGroupId)?if_exists}" />
	                    <a href="javascript:document.deleteSalesPolicyCategoryAction_${actionSalesPolicyCategory_index}.submit()" class="btn btn-mini btn-danger open-sans icon-trash">
	                    	${uiLabelMap.CommonDelete}
                    	</a>
                  	</form>
               	</div>
	      	</#list>
      	</div>
    </div>	
  		
  	<#-- end categories promo action -->
  	<#-- product promo action -->
  	<#assign actionSalesPolicyProducts = policyAction.getRelated("SalesPolicyProduct", null, null, false)>
	<div class="control-group form-horizontal">
  	 	<label class="control-label">${uiLabelMap.ProductProductName}:</label>
  	 	<div class="controls">
  	  		<form method="post" action="<@ofbizUrl>createSalesPolicyProduct</@ofbizUrl>" name="createPPPAction">
                <input type="hidden" name="salesPolicyId" value="${salesPolicyId}" />
                <input type="hidden" name="salesPolicyRuleId" value="${policyAction.salesPolicyRuleId}" />
                <input type="hidden" name="salesPolicyActionSeqId" value="${policyAction.salesPolicyActionSeqId}" />
                <input type="hidden" name="salesPolicyCondSeqId" value="_NA_" />
                <@htmlTemplate.lookupField formName="createPPPAction" name="productId" id="add_product_id" fieldFormName="LookupProduct"/>
                <input type="hidden" name="salesPolicyApplEnumId" value="SPPA_INCLUDE" />
              	<#--
              	<select name="salesPolicyApplEnumId">
	      			<#list salesPolicyApplEnums as salesPolicyApplEnum>
	                  	<option value="${salesPolicyApplEnum.enumId}">${salesPolicyApplEnum.get("description",locale)}</option>
	      			</#list>
              	</select>
              	-->
               	<button type="submit" class="btn btn-mini btn-primary"><i class="icon-plus"></i>${uiLabelMap.CommonAdd}</button>
         	</form>
           	<#if actionSalesPolicyProducts?has_content>
	      		<ul class="unstyled">
	      		<#list actionSalesPolicyProducts as actionSalesPolicyProduct>
		        	<#assign actionProduct = actionSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
		        	<#--<#assign actionApplEnumeration = actionSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
                  	<li>
                  		<form name="deleteSalesPolicyProductAction_${actionSalesPolicyProduct_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyProduct</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
	                    	<input type="hidden" name="salesPolicyId" value="${(actionSalesPolicyProduct.salesPolicyId)?if_exists}" />
	                    	<input type="hidden" name="salesPolicyRuleId" value="${(actionSalesPolicyProduct.salesPolicyRuleId)?if_exists}" />
	                    	<input type="hidden" name="salesPolicyCondSeqId" value="${(actionSalesPolicyProduct.salesPolicyCondSeqId)?if_exists}" />
	                    	<input type="hidden" name="salesPolicyActionSeqId" value="${(actionSalesPolicyProduct.salesPolicyActionSeqId)?if_exists}" />
	                    	<input type="hidden" name="productId" value="${(actionSalesPolicyProduct.productId)?if_exists}" />
	                   	 	<a href="javascript:document.deleteSalesPolicyProductAction_${actionSalesPolicyProduct_index}.submit()" class="btn btn-minier btn-danger icon-trash open-sans"></a>
	                  	</form>
	                  	${(actionProduct.internalName)?if_exists} [${actionSalesPolicyProduct.productId}]<#--- ${(actionApplEnumeration.get("description",locale))?default(actionSalesPolicyProduct.salesPolicyApplEnumId)-->
                  	</li>
		      	</#list>
		      	</ul>
		    </#if>  
      	</div>
  	</div>
  	<#-- end product promo action -->
  	<form method="post" action="<@ofbizUrl>updateSalesPolicyAction</@ofbizUrl>" class="form-horizontal basic-custom-form">
      	<input type="hidden" name="salesPolicyId" value="${(policyAction.salesPolicyId)?if_exists}" />
      	<input type="hidden" name="salesPolicyRuleId" value="${(policyAction.salesPolicyRuleId)?if_exists}" />
      	<input type="hidden" name="salesPolicyActionSeqId" value="${(policyAction.salesPolicyActionSeqId)?if_exists}" />
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductAction}:</label>
      		<div class="controls">
              	<select name="salesPolicyActionEnumId" size="1">
			      	<#if (policyAction.salesPolicyActionEnumId)?exists>
			        	<#assign salesPolicyActionCurEnum = policyAction.getRelatedOne("ActionEnumeration", true)>
	                  	<option value="${policyAction.salesPolicyActionEnumId?if_exists}"><#if salesPolicyActionCurEnum?exists>${(salesPolicyActionCurEnum.get("description",locale))?if_exists}<#else>[${(policyAction.salesPolicyActionEnumId)?if_exists}]</#if></option>
	                  	<option value="${(policyAction.salesPolicyActionEnumId)?if_exists}">&nbsp;</option>
			      	<#else>
	                  	<option value="">&nbsp;</option>
			      	</#if>
			      	<#list salesPolicyActionEnums as salesPolicyActionEnum>
	                  	<option value="${(salesPolicyActionEnum.enumId)?if_exists}">${(salesPolicyActionEnum.get("description",locale))?if_exists}</option>
			      	</#list>
        		</select>
            </div>
        </div>
    	<input type="hidden" name="orderAdjustmentTypeId" value="${(policyAction.orderAdjustmentTypeId)?if_exists}" />
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductQuantity}:</label>
          	<div class="controls">
          		<input type="text" size="5" name="quantity" value="${(policyAction.quantity)?if_exists}" />
          	</div>
      	</div>
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.ProductAmount}:</label>
          	<div class="controls">
          		<input type="text" size="5" name="amount" value="${(policyAction.amount)?if_exists}" />
          	</div>
      	</div>
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductItemId}:</label>
      		<div class="controls">
      			<input type="text" size="15" name="productId" value="${(policyAction.productId)?if_exists}" />
      		</div>
      	</div>
      	<#--
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.PartyParty}:</label>
          	<div class="controls">
          		<input type="text" size="10" name="partyId" value="${(policyAction.partyId)?if_exists}" /><br />
          	</div>
      	</div>
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.ProductServiceName}:</label>
          	<div class="controls">
          		<input type="text" size="20" name="serviceName" value="${(policyAction.serviceName)?if_exists}" />
          	</div>
      	</div>
      	-->
       	<div class="control-group">
        	<label class="control-label">&nbsp;</label>
           	<div class="controls">
               	<button type="submit" class="btn btn-mini btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
           		<a class="btn btn-mini btn-warning icon-remove" id="cancelUpdateAction" href="javascript:void(0)">${uiLabelMap.CommonCancel}</a>    	
           	</div>
       	</div>
    </form>
    <script type="text/javascript">
    	$("#cancelUpdateAction").click(function(){
    		$.ajax({
    			url: '<@ofbizUrl>updateSalesPolicyActionAjax</@ofbizUrl>',
				type: 'GET',
				data: {
					salesPolicyId:'${policyAction.salesPolicyId}',
					salesPolicyRuleId:'${policyAction.salesPolicyRuleId}'
				},
				success: function(data){
					$("#createUpdateRuleAction").html(data);
				}  
        	});	
    	});
    </script>                 
<#else>
	<form action="<@ofbizUrl>createSalesPolicyAction</@ofbizUrl>" method="post" class="form-horizontal basic-custom-form">
		<input type="hidden" name="salesPolicyId" value="${salesPolicyId?if_exists}" />
     	<input type="hidden" name="salesPolicyRuleId" value="${salesPolicyRuleId?if_exists}" />
     	<input type="hidden" name="salesPolicyCondSeqId" value="_NA_" />
     	<input type="hidden" name="andGroupId" value="_NA_">
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
			     			<input type="hidden" name="salesPolicyApplEnumId" value="SPPA_INCLUDE" />
			              	<input type="hidden" name="includeSubCategories" value="N" />
			              	<#--
			              	<select name="salesPolicyApplEnumId" class="span8">
						      <#list salesPolicyApplEnums as salesPolicyApplEnum>
						         <option value="${salesPolicyApplEnum.enumId}">${salesPolicyApplEnum.get("description",locale)}</option>
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
		<div style="clear:both"></div>
		<input type="hidden" name="orderAdjustmentTypeId" value="PROMOTION_ADJUSTMENT" />
		<div class="row-fluid">
        	<div class="span12">
        		<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductAction}</label>
					<div class="controls">
						<span class="span12">
							<select name="salesPolicyActionEnumId">
							    <#list salesPolicyActionEnums as salesPolicyActionEnum>
							       	<option value="${(salesPolicyActionEnum.enumId)?if_exists}">${(salesPolicyActionEnum.get("description",locale))?if_exists}</option>
							    </#list>
			                </select>
						</span>
					</div>
				</div>
        	</div>
        </div>
        <div class="row-fluid">
        	<div class="span6">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductAmount}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="amount" />
						</span>												
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductQuantity}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="quantity" />
						</span>												
					</div>
				</div>
        	</div>
        	<div class="span6">
        		<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductItemId}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="productId" />
						</span>												
					</div>
				</div>
				<#--
				<div class="control-group">
					<label class="control-label">${uiLabelMap.PartyParty}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="partyId" />
						</span>												
					</div>
				</div>
				-->
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button type="submit" class="btn btn-mini btn-info margin-top-nav-10"><i class="icon-ok"></i>
							${uiLabelMap.DelysProductCreateAction}</button>
						<!-- <button type="submit" class="btn btn-mini btn-info margin-top-nav-10"><i class="icon-ok"></i>Xóa chính sách</button> -->
					</div>
				</div>
        	</div>
        </div>
	</form>
	<script type="text/javascript">
		$(".chzn-select").chosen({
			search_contains: true
		}); 		
	</script>
</#if>
<hr />