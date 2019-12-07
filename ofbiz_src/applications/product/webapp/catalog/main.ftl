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
<#if !sessionAttributes.userLogin?exists>
  <div class='label'> ${uiLabelMap.ProductGeneralMessage}.</div>
</#if>
<br />
<#if security.hasEntityPermission("CATALOG", "_VIEW", session)>
<div class="margin-left15">
<label for="prodCatalogId">${uiLabelMap.ProductEditCatalogWithCatalogId}</label>
	  <form method="post" action="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" style="margin: 0;" name="EditProdCatalogForm" id="EditProdCatalogForm">
		
	    <input type="text" maxlength="20" name="prodCatalogId" id="prodCatalogId" value=""/>
	    <button class="btn btn-primary btn-mini" type="button" onclick="EditProdCatalogForm.submit();">
	    	<i class="icon-edit"></i>
	    	${uiLabelMap.ProductEditCatalog}
	    </button>	    
	     <label style='display:inline-block;vertical-align: top;padding-top: 2px;}'>${uiLabelMap.CommonOr}: <span ><a href="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" class="btn btn-mini btn-primary icon-plus-sign open-sans">${uiLabelMap.ProductCreateNewCatalog}</a></span></label>
	  </form>
	 
	  <hr>
	  
	  <form method="post" action="<@ofbizUrl>EditCategory</@ofbizUrl>" style="margin: 0;" name="EditCategoryForm" id="EditCategoryForm">
	    <label for="0_lookupId_productCategoryId">${uiLabelMap.ProductEditCategoryWithCategoryId}</label>
	    <@htmlTemplate.lookupField name="productCategoryId" id="productCategoryId" formName="EditCategoryForm" fieldFormName="LookupProductCategory"/>
	    <button class="btn btn-primary btn-mini" type="button" onclick="EditCategoryForm.submit();">
	    	<i class="icon-edit"></i>
	    	${uiLabelMap.ProductEditCategory}
	    </button>
	    <label style='display:inline-block;vertical-align: top;padding-top: 2px;}'>${uiLabelMap.CommonOr}: <span ><a href="<@ofbizUrl>EditCategory</@ofbizUrl>" class="btn btn-mini btn-primary icon-plus-sign open-sans">${uiLabelMap.ProductCreateNewCategory}</a></span></label>
	  </form>
	  <hr>
	  
	  <form method="post" action="<@ofbizUrl>EditProduct</@ofbizUrl>" style="margin: 0;" name="EditProductForm" id="EditProductForm">
	  	<label for="1_lookupId_productId">${uiLabelMap.ProductEditProductWithProductId}</label>
	    <@htmlTemplate.lookupField name="productId" id="productId" formName="EditProductForm" fieldFormName="LookupProduct"/>
	    <button class="btn btn-primary btn-mini" type="button" onclick="EditProductForm.submit();">
	    	<i class="icon-edit"></i>
	    	${uiLabelMap.ProductEditProduct}
	    </button>
	   
	    	<label style='display:inline-block;vertical-align: top;padding-top: 2px;}'>${uiLabelMap.CommonOr}: <span ><a href="<@ofbizUrl>EditProduct</@ofbizUrl>" class="btn btn-mini btn-primary icon-plus-sign open-sans">${uiLabelMap.ProductCreateNewProduct}</a></span></label>
	  		<label style='display:inline-block;vertical-align: top;padding-top: 2px;}'>${uiLabelMap.CommonOr}: <span ><a href="<@ofbizUrl>CreateVirtualWithVariantsForm</@ofbizUrl>" class="btn btn-mini btn-primary icon-plus-sign open-sans">${uiLabelMap.ProductQuickCreateVirtualFromVariants}</a></span></label>
	    
	  </form>
	  
	  <hr>
	  
	  <form method="post" action="<@ofbizUrl>FindProductById</@ofbizUrl>" style="margin: 0;" id="FindProductByIdForm">
	  	<label for="idValue">${uiLabelMap.ProductFindProductWithIdValue}</label>
	    <input type="text" size="20" maxlength="20" name="idValue" value="" id="idValue"/>
	    <button class="btn btn-primary btn-mini" type="button" onclick="FindProductByIdForm.submit();">
	    	<i class="icon-search"></i>
	    	${uiLabelMap.ProductFindProduct}
	    </button>
	  </form>
	  <hr>
	  <div><span ><a href="<@ofbizUrl>UpdateAllKeywords</@ofbizUrl>" class="btn btn-mini btn-primary icon-cogs open-sans"> ${uiLabelMap.ProductAutoCreateKeywordsForAllProducts}</a></span></div>
	  <div><span ><a href="<@ofbizUrl>FastLoadCache</@ofbizUrl>" class="btn btn-mini btn-primary margin-top2 open-sans icon-forward"> ${uiLabelMap.ProductFastLoadCatalogIntoCache}</a></span></div>
	  <br />
  </div>
</#if>
