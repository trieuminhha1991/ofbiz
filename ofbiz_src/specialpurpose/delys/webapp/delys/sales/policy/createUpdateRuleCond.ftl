<#if policyCond?exists>
	<#if (policyCond.salesPolicyCondSeqId)?exists>
        <#assign curCondSeqId = Static["java.lang.Integer"].valueOf(policyCond.getString("salesPolicyCondSeqId"))>
        <#assign maxCondSeqId = 1>
        <#if (curCondSeqId >= maxCondSeqId)>
          	<#assign maxCondSeqId = curCondSeqId + 1>
        </#if>
  	</#if>
  	<#-- Categories -->
  	<#assign condSalesPolicyCategories = policyCond.getRelated("SalesPolicyCategory", null, null, false)>
  	<div class="control-group form-horizontal">
 		<label class="control-label" for="">${uiLabelMap.DelysCategoryName}:</label>
  		<div class="controls">
      		<form method="post" action="<@ofbizUrl>createSalesPolicyCategory</@ofbizUrl>" name="createPPCatCond">
          		<input type="hidden" name="salesPolicyId" value="${salesPolicyId}" />
              	<input type="hidden" name="salesPolicyRuleId" value="${policyCond.salesPolicyRuleId}" />
              	<input type="hidden" name="salesPolicyActionSeqId" value="_NA_" />
              	<input type="hidden" name="salesPolicyCondSeqId" value="${policyCond.salesPolicyCondSeqId}" />
              	<input type="hidden" name="andGroupId" value="_NA_"/>
              	<@htmlTemplate.lookupField formName="createPPCatCond" name="productCategoryId" id="productCategoryId_cond" fieldFormName="LookupProductCategory"/>
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
       	  	<#list condSalesPolicyCategories as condSalesPolicyCategory>
            	<#assign condProductCategory = condSalesPolicyCategory.getRelatedOne("ProductCategory", true)>
                <#assign condApplEnumeration = condSalesPolicyCategory.getRelatedOne("ApplEnumeration", true)>

                ${(condProductCategory.get("description",locale))?if_exists} [${condSalesPolicyCategory.productCategoryId}]
              	- ${(condApplEnumeration.get("description",locale))?default(condSalesPolicyCategory.salesPolicyApplEnumId)}
              	- ${uiLabelMap.ProductSubCats}? ${condSalesPolicyCategory.includeSubCategories?default("N")}
              	<form name="deleteSalesPolicyCategoryCondition_${condSalesPolicyCategory_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyCategory</@ofbizUrl>">
                    <input type="hidden" name="salesPolicyId" value="${(condSalesPolicyCategory.salesPolicyId)?if_exists}" />
                    <input type="hidden" name="salesPolicyRuleId" value="${(condSalesPolicyCategory.salesPolicyRuleId)?if_exists}" />
                    <input type="hidden" name="salesPolicyActionSeqId" value="${(condSalesPolicyCategory.salesPolicyActionSeqId)?if_exists}" />
                    <input type="hidden" name="salesPolicyCondSeqId" value="${(condSalesPolicyCategory.salesPolicyCondSeqId)?if_exists}" />
                    <input type="hidden" name="productCategoryId" value="${(condSalesPolicyCategory.productCategoryId)?if_exists}" />
                    <input type="hidden" name="andGroupId" value="${(condSalesPolicyCategory.andGroupId)?if_exists}" />
                    <a href="javascript:document.deleteSalesPolicyCategoryCondition_${condSalesPolicyCategory_index}.submit()" class="btn btn-mini btn-danger open-sans icon-trash">${uiLabelMap.CommonDelete}</a>
              	</form>
        	</#list>
        </div>
   	</div>
	<#--End categories-->
   	<#-- create/update Product condition for rule-->                      
 	<div class="control-group form-horizontal">
  		<label class="control-label" for="">${uiLabelMap.ProductProductName}:</label>
     	<div class="controls">
         	<form method="post" action="<@ofbizUrl>createSalesPolicyProduct</@ofbizUrl>" name="createPPPCond">
                <input type="hidden" name="salesPolicyId" value="${salesPolicyId}" />
                <input type="hidden" name="salesPolicyRuleId" value="${policyCond.salesPolicyRuleId}" />
                <input type="hidden" name="salesPolicyActionSeqId" value="_NA_" />
                <input type="hidden" name="salesPolicyCondSeqId" value="${policyCond.salesPolicyCondSeqId}" />
                <@htmlTemplate.lookupField formName="createPPPCond" name="productId" id="add_product_id" fieldFormName="LookupProduct"/>
                <input type="hidden" name="salesPolicyApplEnumId" value="SPPA_INCLUDE" />
              	<#--
              	<select name="salesPolicyApplEnumId">
	      			<#list salesPolicyApplEnums as salesPolicyApplEnum>
	                  	<option value="${salesPolicyApplEnum.enumId}">${salesPolicyApplEnum.get("description",locale)}</option>
	      			</#list>
              	</select>
              	-->
               	<button type="submit" class="btn btn-mini btn-primary">
               		<i class="icon-plus"></i>${uiLabelMap.CommonAdd}
           		</button>
     		</form>
     		<#-- update -->
         	<#assign condSalesPolicyProducts = policyCond.getRelated("SalesPolicyProduct", null, null, false)>
         	<#if condSalesPolicyProducts?has_content>
     			<ul class="unstyled">
     			<#list condSalesPolicyProducts as condSalesPolicyProduct>
         	   		<#assign condProduct = condSalesPolicyProduct.getRelatedOne("Product", true)?if_exists>
			   		<#--<#assign condApplEnumeration = condSalesPolicyProduct.getRelatedOne("ApplEnumeration", true)>-->
          			<li>
	              		<form name="deleteSalesPolicyProductCondition_${condSalesPolicyProduct_index}" method="post" action="<@ofbizUrl>deleteSalesPolicyProduct</@ofbizUrl>" style="display:inline-block; margin-right:2px !important">
		                    <input type="hidden" name="salesPolicyId" value="${(condSalesPolicyProduct.salesPolicyId)?if_exists}" />
		                    <input type="hidden" name="salesPolicyRuleId" value="${(condSalesPolicyProduct.salesPolicyRuleId)?if_exists}" />
		                    <input type="hidden" name="salesPolicyActionSeqId" value="${(condSalesPolicyProduct.salesPolicyActionSeqId)?if_exists}" />
		                    <input type="hidden" name="salesPolicyCondSeqId" value="${(condSalesPolicyProduct.salesPolicyCondSeqId)?if_exists}" />
		                    <input type="hidden" name="productId" value="${(condSalesPolicyProduct.productId)?if_exists}" />
		                    <a href="javascript:document.deleteSalesPolicyProductCondition_${condSalesPolicyProduct_index}.submit()" class="btn btn-minier btn-danger icon-trash open-sans"></a>
	                  	</form>
	                  	${(condProduct.internalName)?if_exists} [${condSalesPolicyProduct.productId}]}<#--- ${(condApplEnumeration.get("description",locale))?default(condSalesPolicyProduct.salesPolicyApplEnumId)-->
              		</li>
         		</#list>
         		</ul>
         	</#if>
    	</div>
 	</div><#-- end create/update Product condition for rule-->
   	<form method="post" action="<@ofbizUrl>updateSalesPolicyCond</@ofbizUrl>" class="form-horizontal basic-custom-form">
       	<input type="hidden" name="salesPolicyId" value="${(policyCond.salesPolicyId)?if_exists}"/>
       	<input type="hidden" name="salesPolicyRuleId" value="${(policyCond.salesPolicyRuleId)?if_exists}"/>
       	<input type="hidden" name="salesPolicyCondSeqId" value="${(policyCond.salesPolicyCondSeqId)?if_exists}"/>
       	<div class="control-group">
       		<label class="control-label" for="">${uiLabelMap.Condition}</label>
       		<div class="controls">
       			<div class="span12">
           			<div class="span3">
           				<select name="inputParamEnumId" >
		      				<#if (policyCond.inputParamEnumId)?exists>
		        				<#assign inputParamEnum = policyCond.getRelatedOne("InputParamEnumeration", true)>
			                    <option value="${policyCond.inputParamEnumId?if_exists}"><#if inputParamEnum?exists>${(inputParamEnum.get("description",locale))?if_exists}<#else>[${(policyCond.inputParamEnumId)?if_exists}]</#if></option>
			                    <option value="${(policyCond.inputParamEnumId)?if_exists}">&nbsp;</option>
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
		      				<#if (policyCond.operatorEnumId)?exists>
		        				<#assign operatorEnum = policyCond.getRelatedOne("OperatorEnumeration", true)>
	                    		<option value="${(policyCond.operatorEnumId)?if_exists}"><#if operatorEnum?exists>${(operatorEnum.get("description",locale))?if_exists}<#else>[${(policyCond.operatorEnumId)?if_exists}]</#if></option>
		                    	<option value="${(policyCond.operatorEnumId)?if_exists}">&nbsp;</option>
		      				<#else>
		                    	<option value="">&nbsp;</option>
		      				</#if>
		      				<#list condOperEnums as condOperEnum>
		                    	<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
		      				</#list>
	                  	</select>
	            	</div>
	            	<div class="span3">      				                 
	                  	<input type="text" name="condValue" value="${(policyCond.condValue)?if_exists}" />
	            	</div>      
     			</div>
    		</div> 
    	</div>
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
	 			url: '<@ofbizUrl>updateSalesPolicyCondAjax</@ofbizUrl>',
				type: 'GET',
				data: {
					salesPolicyId:'${policyCond.salesPolicyId}',
					salesPolicyRuleId:'${policyCond.salesPolicyRuleId}'
				},
				success: function(data){
					$("#createUpdateRuleCondition").html(data);
				}
 			});	
 		});
 	</script>
<#else>
	<#--create condition for rule -->
	<form method="post" action="<@ofbizUrl>createSalesPolicyCond</@ofbizUrl>" class="form-horizontal basic-custom-form">
       	<input type="hidden" name="salesPolicyId" value="${salesPolicyId?if_exists}" />
   		<input type="hidden" name="salesPolicyRuleId" value="${salesPolicyRuleId?if_exists}" />
       	<input type="hidden" name="salesPolicyActionSeqId" value="_NA_" />
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