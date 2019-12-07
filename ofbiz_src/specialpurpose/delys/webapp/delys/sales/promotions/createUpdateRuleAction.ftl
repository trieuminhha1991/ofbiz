<#if promoAction?exists && promoAction?has_content>
	<#assign actionProductPromoCategories = promoAction.getRelated("ProductPromoCategory", null, null, false)>
    <#-- ================Categories promo action================= -->
    <div class="control-group form-horizontal">
 	 	<label class="control-label" for="">${uiLabelMap.DelysCategoryName}:</label>
 	 	<div class="controls">
		 	<form method="post" action="<@ofbizUrl>createProductPromoCategory</@ofbizUrl>" name="createPPCatAction">
                <input type="hidden" name="productPromoId" value="${productPromoId}" />
                <input type="hidden" name="productPromoRuleId" value="${promoAction.productPromoRuleId}" />
                <input type="hidden" name="productPromoActionSeqId" value="${promoAction.productPromoActionSeqId}" />
                <input type="hidden" name="productPromoCondSeqId" value="_NA_" />
                <input type="hidden" name="andGroupId" value="_NA_"/>
                <@htmlTemplate.lookupField formName="createPPCatAction" name="productCategoryId" id="productCategoryId_act" fieldFormName="LookupProductCategory"/>
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
                <button type="submit" class="btn btn-mini btn-primary"><i class="icon-plus"></i>${uiLabelMap.CommonAdd}</button>
          	</form>
			<#list actionProductPromoCategories as actionProductPromoCategory>
		        <#assign actionProductCategory = actionProductPromoCategory.getRelatedOne("ProductCategory", true)>
		        <#assign actionApplEnumeration = actionProductPromoCategory.getRelatedOne("ApplEnumeration", true)>
               	<div>
                  	${(actionProductCategory.description)?if_exists} [${actionProductPromoCategory.productCategoryId}]
                  	- ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoCategory.productPromoApplEnumId)}
                  	- ${uiLabelMap.ProductSubCats}? ${actionProductPromoCategory.includeSubCategories?default("N")}
                  	- ${uiLabelMap.CommonAnd} ${uiLabelMap.CommonGroup}: ${actionProductPromoCategory.andGroupId}
                  	<form name="deleteProductPromoCategoryAction_${actionProductPromoCategory_index}" action="<@ofbizUrl>deleteProductPromoCategory</@ofbizUrl>" method="post">
                		<input type="hidden" name="productPromoId" value="${(actionProductPromoCategory.productPromoId)?if_exists}" />
	                    <input type="hidden" name="productPromoRuleId" value="${(actionProductPromoCategory.productPromoRuleId)?if_exists}" />
	                    <input type="hidden" name="productPromoCondSeqId" value="${(actionProductPromoCategory.productPromoCondSeqId)?if_exists}" />
	                    <input type="hidden" name="productPromoActionSeqId" value="${(actionProductPromoCategory.productPromoActionSeqId)?if_exists}" />
	                    <input type="hidden" name="productCategoryId" value="${(actionProductPromoCategory.productCategoryId)?if_exists}" />
	                    <input type="hidden" name="andGroupId" value="${(actionProductPromoCategory.andGroupId)?if_exists}" />
	                    <a href="javascript:document.deleteProductPromoCategoryAction_${actionProductPromoCategory_index}.submit()" class="btn btn-mini btn-danger open-sans icon-trash">
	                    	${uiLabelMap.CommonDelete}
                    	</a>
                  	</form>
               	</div>
	      	</#list>
      	</div>
    </div>	
  		
  	<#-- end categories promo action -->
  	<#-- product promo action -->
  	<#assign actionProductPromoProducts = promoAction.getRelated("ProductPromoProduct", null, null, false)>
	<div class="control-group form-horizontal">
  	 	<label class="control-label">${uiLabelMap.ProductProductName}:</label>
  	 	<div class="controls">
  	  		<form method="post" action="<@ofbizUrl>createProductPromoProduct</@ofbizUrl>" name="createPPPAction">
                <input type="hidden" name="productPromoId" value="${productPromoId}" />
                <input type="hidden" name="productPromoRuleId" value="${promoAction.productPromoRuleId}" />
                <input type="hidden" name="productPromoActionSeqId" value="${promoAction.productPromoActionSeqId}" />
                <input type="hidden" name="productPromoCondSeqId" value="_NA_" />
                <@htmlTemplate.lookupField formName="createPPPAction" name="productId" id="add_product_id" fieldFormName="LookupProduct"/>
                <input type="hidden" name="productPromoApplEnumId" value="PPPA_INCLUDE" />
              	<#--
              	<select name="productPromoApplEnumId">
	      			<#list productPromoApplEnums as productPromoApplEnum>
	                  	<option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.get("description",locale)}</option>
	      			</#list>
              	</select>
              	-->
               	<button type="submit" class="btn btn-mini btn-primary"><i class="icon-plus"></i>${uiLabelMap.CommonAdd}</button>
         	</form>
           	<#if actionProductPromoProducts?has_content>
	      		<ul class="unstyled">
	      		<#list actionProductPromoProducts as actionProductPromoProduct>
		        	<#assign actionProduct = actionProductPromoProduct.getRelatedOne("Product", true)?if_exists>
		        	<#assign actionApplEnumeration = actionProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
                  	<li>
                  		<form name="deleteProductPromoProductAction_${actionProductPromoProduct_index}" method="post" action="<@ofbizUrl>deleteProductPromoProduct</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
	                    	<input type="hidden" name="productPromoId" value="${(actionProductPromoProduct.productPromoId)?if_exists}" />
	                    	<input type="hidden" name="productPromoRuleId" value="${(actionProductPromoProduct.productPromoRuleId)?if_exists}" />
	                    	<input type="hidden" name="productPromoCondSeqId" value="${(actionProductPromoProduct.productPromoCondSeqId)?if_exists}" />
	                    	<input type="hidden" name="productPromoActionSeqId" value="${(actionProductPromoProduct.productPromoActionSeqId)?if_exists}" />
	                    	<input type="hidden" name="productId" value="${(actionProductPromoProduct.productId)?if_exists}" />
	                   	 	<a href="javascript:document.deleteProductPromoProductAction_${actionProductPromoProduct_index}.submit()" class="btn btn-minier btn-danger icon-trash open-sans"></a>
	                  	</form>
	                  	${(actionProduct.internalName)?if_exists} [${actionProductPromoProduct.productId}]<#--- ${(actionApplEnumeration.get("description",locale))?default(actionProductPromoProduct.productPromoApplEnumId)-->
                  	</li>
		      	</#list>
		      	</ul>
		    </#if>  
      	</div>
  	</div>
  	<#-- end product promo action -->
  	<form method="post" action="<@ofbizUrl>updateProductPromoAction</@ofbizUrl>" class="form-horizontal basic-custom-form">
      	<input type="hidden" name="productPromoId" value="${(promoAction.productPromoId)?if_exists}" />
      	<input type="hidden" name="productPromoRuleId" value="${(promoAction.productPromoRuleId)?if_exists}" />
      	<input type="hidden" name="productPromoActionSeqId" value="${(promoAction.productPromoActionSeqId)?if_exists}" />
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductAction}:</label>
      		<div class="controls">
              	<select name="productPromoActionEnumId" size="1">
			      	<#if (promoAction.productPromoActionEnumId)?exists>
			        	<#assign productPromoActionCurEnum = promoAction.getRelatedOne("ActionEnumeration", true)>
	                  	<option value="${promoAction.productPromoActionEnumId?if_exists}"><#if productPromoActionCurEnum?exists>${(productPromoActionCurEnum.get("description",locale))?if_exists}<#else>[${(promoAction.productPromoActionEnumId)?if_exists}]</#if></option>
	                  	<option value="${(promoAction.productPromoActionEnumId)?if_exists}">&nbsp;</option>
			      	<#else>
	                  	<option value="">&nbsp;</option>
			      	</#if>
			      	<#list productPromoActionEnums as productPromoActionEnum>
	                  	<option value="${(productPromoActionEnum.enumId)?if_exists}">${(productPromoActionEnum.get("description",locale))?if_exists}</option>
			      	</#list>
        		</select>
            </div>
        </div>
    	<input type="hidden" name="orderAdjustmentTypeId" value="${(promoAction.orderAdjustmentTypeId)?if_exists}" />
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductQuantity}:</label>
          	<div class="controls">
          		<input type="text" size="5" name="quantity" value="${(promoAction.quantity)?if_exists}" />
          	</div>
      	</div>
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.ProductAmount}:</label>
          	<div class="controls">
          		<input type="text" size="5" name="amount" value="${(promoAction.amount)?if_exists}" />
          	</div>
      	</div>
      	<div class="control-group">
      		<label class="control-label">${uiLabelMap.ProductItemId}:</label>
      		<div class="controls">
      			<input type="text" size="15" name="productId" value="${(promoAction.productId)?if_exists}" />
      		</div>
      	</div>
      	<#--
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.PartyParty}:</label>
          	<div class="controls">
          		<input type="text" size="10" name="partyId" value="${(promoAction.partyId)?if_exists}" /><br />
          	</div>
      	</div>
      	<div class="control-group">
          	<label class="control-label">${uiLabelMap.ProductServiceName}:</label>
          	<div class="controls">
          		<input type="text" size="20" name="serviceName" value="${(promoAction.serviceName)?if_exists}" />
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
    			url: '<@ofbizUrl>updateProductPromoActionAjax</@ofbizUrl>',
				type: 'GET',
				data: {
					productPromoId:${promoAction.productPromoId}, 
					productPromoRuleId:${promoAction.productPromoRuleId}
				},
				success: function(data){
					$("#createUpdateRuleAction").html(data);
				}  
        	});	
    	});
    </script>                 
<#else>
	<form action="<@ofbizUrl>createProductPromoAction</@ofbizUrl>" method="post" class="form-horizontal basic-custom-form">
		<input type="hidden" name="productPromoId" value="${(productPromoRule.productPromoId)?if_exists}" />
     	<input type="hidden" name="productPromoRuleId" value="${(productPromoRule.productPromoRuleId)?if_exists}" />
     	<input type="hidden" name="productPromoCondSeqId" value="_NA_" />
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
		<div style="clear:both"></div>
		<input type="hidden" name="orderAdjustmentTypeId" value="PROMOTION_ADJUSTMENT" />
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
        		<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductQuantity}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="quantity" />
						</span>												
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ProductAmount}</label>
					<div class="controls">
						<span class="span12">
							<input type="text" name="amount" />
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