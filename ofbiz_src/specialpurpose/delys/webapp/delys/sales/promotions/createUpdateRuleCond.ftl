<#if promoCondition?exists>
	<#if (promoCondition.productPromoCondSeqId)?exists>
        <#assign curCondSeqId = Static["java.lang.Integer"].valueOf(promoCondition.getString("productPromoCondSeqId"))>
        <#assign maxCondSeqId = 1>
        <#if (curCondSeqId >= maxCondSeqId)>
          	<#assign maxCondSeqId = curCondSeqId + 1>
        </#if>
  	</#if>
  	<#-- Categories -->
  	<#assign condProductPromoCategories = promoCondition.getRelated("ProductPromoCategory", null, null, false)>
  	<div class="control-group form-horizontal">
 		<label class="control-label" for="">${uiLabelMap.DelysCategoryName}:</label>
  		<div class="controls">
      		<form method="post" action="<@ofbizUrl>createProductPromoCategory</@ofbizUrl>" name="createPPCatCond">
          		<input type="hidden" name="productPromoId" value="${productPromoId}" />
              	<input type="hidden" name="productPromoRuleId" value="${promoCondition.productPromoRuleId}" />
              	<input type="hidden" name="productPromoActionSeqId" value="_NA_" />
              	<input type="hidden" name="productPromoCondSeqId" value="${promoCondition.productPromoCondSeqId}" />
              	<input type="hidden" name="andGroupId" value="_NA_"/>
              	<@htmlTemplate.lookupField formName="createPPCatCond" name="productCategoryId" id="productCategoryId_cond" fieldFormName="LookupProductCategory"/>
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
       	  	<#list condProductPromoCategories as condProductPromoCategory>
            	<#assign condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true)>
                <#assign condApplEnumeration = condProductPromoCategory.getRelatedOne("ApplEnumeration", true)>

                ${(condProductCategory.get("description",locale))?if_exists} [${condProductPromoCategory.productCategoryId}]
              	- ${(condApplEnumeration.get("description",locale))?default(condProductPromoCategory.productPromoApplEnumId)}
              	- ${uiLabelMap.ProductSubCats}? ${condProductPromoCategory.includeSubCategories?default("N")}
              	<form name="deleteProductPromoCategoryCondition_${condProductPromoCategory_index}" method="post" action="<@ofbizUrl>deleteProductPromoCategory</@ofbizUrl>">
                    <input type="hidden" name="productPromoId" value="${(condProductPromoCategory.productPromoId)?if_exists}" />
                    <input type="hidden" name="productPromoRuleId" value="${(condProductPromoCategory.productPromoRuleId)?if_exists}" />
                    <input type="hidden" name="productPromoActionSeqId" value="${(condProductPromoCategory.productPromoActionSeqId)?if_exists}" />
                    <input type="hidden" name="productPromoCondSeqId" value="${(condProductPromoCategory.productPromoCondSeqId)?if_exists}" />
                    <input type="hidden" name="productCategoryId" value="${(condProductPromoCategory.productCategoryId)?if_exists}" />
                    <input type="hidden" name="andGroupId" value="${(condProductPromoCategory.andGroupId)?if_exists}" />
                    <a href="javascript:document.deleteProductPromoCategoryCondition_${condProductPromoCategory_index}.submit()" class="btn btn-mini btn-danger open-sans icon-trash">${uiLabelMap.CommonDelete}</a>
              	</form>
        	</#list>
        </div>
   	</div>
	<#--End categories-->
   	<#-- create/update Product condition for rule-->                      
 	<div class="control-group form-horizontal">
  		<label class="control-label" for="">${uiLabelMap.ProductProductName}:</label>
     	<div class="controls">
         	<form method="post" action="<@ofbizUrl>createProductPromoProduct</@ofbizUrl>" name="createPPPCond">
                <input type="hidden" name="productPromoId" value="${productPromoId}" />
                <input type="hidden" name="productPromoRuleId" value="${promoCondition.productPromoRuleId}" />
                <input type="hidden" name="productPromoActionSeqId" value="_NA_" />
                <input type="hidden" name="productPromoCondSeqId" value="${promoCondition.productPromoCondSeqId}" />
                <@htmlTemplate.lookupField formName="createPPPCond" name="productId" id="add_product_id" fieldFormName="LookupProduct"/>
                <input type="hidden" name="productPromoApplEnumId" value="PPPA_INCLUDE" />
              	<#--
              	<select name="productPromoApplEnumId">
	      			<#list productPromoApplEnums as productPromoApplEnum>
	                  	<option value="${productPromoApplEnum.enumId}">${productPromoApplEnum.get("description",locale)}</option>
	      			</#list>
              	</select>
              	-->
               	<button type="submit" class="btn btn-mini btn-primary">
               		<i class="icon-plus"></i>${uiLabelMap.CommonAdd}
           		</button>
     		</form>
     		<#-- update -->
         	<#assign condProductPromoProducts = promoCondition.getRelated("ProductPromoProduct", null, null, false)>
         	<#if condProductPromoProducts?has_content>
     			<ul class="unstyled">
     			<#list condProductPromoProducts as condProductPromoProduct>
         	   		<#assign condProduct = condProductPromoProduct.getRelatedOne("Product", true)?if_exists>
			   		<#assign condApplEnumeration = condProductPromoProduct.getRelatedOne("ApplEnumeration", true)>
          			<li>
	              		<form name="deleteProductPromoProductCondition_${condProductPromoProduct_index}" method="post" action="<@ofbizUrl>deleteProductPromoProduct</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
		                    <input type="hidden" name="productPromoId" value="${(condProductPromoProduct.productPromoId)?if_exists}" />
		                    <input type="hidden" name="productPromoRuleId" value="${(condProductPromoProduct.productPromoRuleId)?if_exists}" />
		                    <input type="hidden" name="productPromoActionSeqId" value="${(condProductPromoProduct.productPromoActionSeqId)?if_exists}" />
		                    <input type="hidden" name="productPromoCondSeqId" value="${(condProductPromoProduct.productPromoCondSeqId)?if_exists}" />
		                    <input type="hidden" name="productId" value="${(condProductPromoProduct.productId)?if_exists}" />
		                    <a href="javascript:document.deleteProductPromoProductCondition_${condProductPromoProduct_index}.submit()" class="btn btn-minier btn-danger icon-trash open-sans"></a>
	                  	</form>
	                  	${(condProduct.internalName)?if_exists} [${condProductPromoProduct.productId}]}<#--- ${(condApplEnumeration.get("description",locale))?default(condProductPromoProduct.productPromoApplEnumId)-->
              		</li>
         		</#list>
         		</ul>
         	</#if>
    	</div>
 	</div><#-- end create/update Product condition for rule-->
   	<form method="post" action="<@ofbizUrl>updateProductPromoCond</@ofbizUrl>" class="form-horizontal basic-custom-form">
       	<input type="hidden" name="productPromoId" value="${promoCondition.productPromoId?if_exists}"/>
       	<input type="hidden" name="productPromoRuleId" value="${promoCondition.productPromoRuleId?if_exists}"/>
       	<input type="hidden" name="productPromoCondSeqId" value="${promoCondition.productPromoCondSeqId?if_exists}"/>
       	<div class="control-group">
       		<label class="control-label" for="">${uiLabelMap.Condition}</label>
       		<div class="controls">
       			<div class="span12">
           			<div class="span3">
           				<select name="inputParamEnumId" >
		      				<#if (promoCondition.inputParamEnumId)?exists>
		        				<#assign inputParamEnum = promoCondition.getRelatedOne("InputParamEnumeration", true)>
			                    <option value="${promoCondition.inputParamEnumId?if_exists}"><#if inputParamEnum?exists>${(inputParamEnum.get("description",locale))?if_exists}<#else>[${(promoCondition.inputParamEnumId)?if_exists}]</#if></option>
			                    <option value="${(promoCondition.inputParamEnumId)?if_exists}">&nbsp;</option>
		      				<#else>
                    			<option value="">&nbsp;</option>
		      				</#if>
		      				<#list inputParamEnums as inputParamEnum>
                    			<option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.get("description",locale))?if_exists}</option>
		      				</#list>
          				</select>
         			</div>
	         		<div class="span3">         
	                  	<select name="operatorEnumId" >
		      				<#if (promoCondition.operatorEnumId)?exists>
		        				<#assign operatorEnum = promoCondition.getRelatedOne("OperatorEnumeration", true)>
	                    		<option value="${(promoCondition.operatorEnumId)?if_exists}"><#if operatorEnum?exists>${(operatorEnum.get("description",locale))?if_exists}<#else>[${(promoCondition.operatorEnumId)?if_exists}]</#if></option>
		                    	<option value="${(promoCondition.operatorEnumId)?if_exists}">&nbsp;</option>
		      				<#else>
		                    	<option value="">&nbsp;</option>
		      				</#if>
		      				<#list condOperEnums as condOperEnum>
		                    	<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
		      				</#list>
	                  	</select>
	            	</div>
	            	<div class="span3">      				                 
	                  	<input type="text" name="condValue" value="${(promoCondition.condValue)?if_exists}" />
	            	</div>      
     			</div>
    		</div> 
    	</div>
    	<#if productPromo.productPromoTypeId == "EXHIBITED">
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DelysExhibitedAt}</label>
				<div class="controls">
					<input type="text" size="25" name="condExhibited" value="${promoCondition.condExhibited?if_exists}">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DANote}</label>
				<div class="controls">
					<input type="text" size="25" name="notes" value="${promoCondition.notes?if_exists}">
				</div>
			</div>
		</#if>
	    <div class="control-group"> 
	    	<label class="control-label" for="">&nbsp;</label> 
	    	<div class="controls">
	   			<button type="submit" class="btn btn-mini btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
	   			<a class="btn btn-mini btn-warning icon-remove" id="cancelUpdateCond" href="javascript:void(0)">${uiLabelMap.CommonCancel}</a>
	   		</div>
	    </div>
 	</form>    
 	
 	<script type="text/javascript">
 		$("#cancelUpdateCond").click(function(){
			$.ajax({
	 			url: '<@ofbizUrl>updateProductPromoCondAjax</@ofbizUrl>',
				type: 'GET',
				data: {
					productPromoId:'${promoCondition.productPromoId}',
					productPromoRuleId:'${promoCondition.productPromoRuleId}'
				},
				success: function(data){
					$("#createUpdateRuleCondition").html(data);
				}
 			});	
 		});
 	</script>
<#else>
	<#--create condition for rule -->
	<form method="post" action="<@ofbizUrl>createProductPromoCond</@ofbizUrl>" class="form-horizontal basic-custom-form">
       	<input type="hidden" name="productPromoId" value="${(productPromoRule.productPromoId)?if_exists}" />
   		<input type="hidden" name="productPromoRuleId" value="${(productPromoRule.productPromoRuleId)?if_exists}" />
       	<input type="hidden" name="productPromoActionSeqId" value="_NA_" />
        <input type="hidden" name="andGroupId" value="_NA_">
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
		<div class="row-fluid">
			<div class="span6">
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
							</select>
						</span>	
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="control-group">			
					<label class="control-label">${uiLabelMap.ProductConditionValue}:</label>
					<div class="controls">		
						<span class="span12">
							<input type="text" size="25" name="condValue" />
						</span>
					</div>
				</div>
			</div>
		</div>
		<#if productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "EXHIBITED">
			<div class="control-group">
				<label class="control-label">${uiLabelMap.DelysExhibitedAt}</label>
				<div class="controls">
					<input type="text" size="25" name="condExhibited">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">${uiLabelMap.notes}</label>
				<div class="controls">
					<input type="text" size="25" name="notes">
				</div>
			</div>
		</#if>
		<div class="row-fluid">
			<div class="span6"></div>
			<div class="span6">
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button type="submit" class="btn btn-mini btn-info margin-top-nav-10"><i class="icon-ok"></i>${uiLabelMap.DelysProductCreateCondition}</button>
						<!-- <button type="submit" class="btn btn-mini btn-info margin-top-nav-10"><i class="icon-ok"></i>Xóa di?u ki?n</button> -->
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