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
<script language="JavaScript" type="text/javascript">
function insertImageName(type,nameValue) {
  eval('document.productCategoryForm.' + type + 'ImageUrl.value=nameValue;');
};
</script>
<#if fileType?has_content>
    <div class="widget-box transparent no-bottom-border">
        <div class="widget-header">
            <h4>${uiLabelMap.ProductResultOfImageUpload}</h4>
        </div>
        <div class="widget-body">
            <#if !(clientFileName?has_content)>
                <div>${uiLabelMap.ProductNoFileSpecifiedForUpload}.</div>
            <#else>
                <div>${uiLabelMap.ProductTheFileOnYourComputer}: <b>${clientFileName?if_exists}</b></div>
                <div>${uiLabelMap.ProductServerFileName}: <b>${fileNameToUse?if_exists}</b></div>
                <div>${uiLabelMap.ProductServerDirectory}: <b>${imageServerPath?if_exists}</b></div>
                <div>${uiLabelMap.ProductTheUrlOfYourUploadedFile}: <b><a href="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">${imageUrl?if_exists}</a></b></div>
            </#if>
        </div>
    </div>
</#if>
<div class="widget-box transparent no-bottom-border">
<#if ! productCategory?has_content>
    <#if productCategoryId?has_content>
        <div class="widget-header">
          <h4>${uiLabelMap.ProductCouldNotFindProductCategoryWithId} "${productCategoryId}".</h4>
          <br class="clear" />
        </div>
        <div class="widget-body">
        	<div style="padding-left:10px;padding-top:10px;">
	            <form action="<@ofbizUrl>createProductCategory</@ofbizUrl>" method="post" style="margin: 0;" name="productCategoryForm">
	                <table cellspacing="0" class="basic-table">
	                    <tr>
	                        <td align="right" >${uiLabelMap.ProductProductCategoryId}</td>
	                        <td>&nbsp;</td>
	                        <td>
	                            <input type="text" name="productCategoryId" size="20" maxlength="40" value="${productCategoryId}"/>
	                        </td>
	                    </tr>
    <#else>
        <div class="widget-header">
            <h4>${uiLabelMap.PageTitleCreateProductCategory}</h4>
          <br class="clear" />
        </div>
        <div class="widget-body">
        	<div style="padding-left:10px;padding-top:10px;">
	            <form action="<@ofbizUrl>createProductCategory</@ofbizUrl>" method="post" style="margin: 0;" name="productCategoryForm">
	                <table cellspacing="0" class="basic-table">
	                    <tr>
	                        <td align="right" >${uiLabelMap.ProductProductCategoryId}</td>
	                        <td>&nbsp;</td>
	                        <td>
	                            <input type="text" name="productCategoryId" size="20" maxlength="40" value=""/>
	                        </td>
	                    </tr>
    </#if>
<#else>
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.PageTitleEditProductCategories}</h4>
        <span class="widget-toolbar none-content">
        	${screens.render("component://product/widget/catalog/CategoryScreens.xml#EditCategoriesSubTabBarSr1")}
        </span>
    </div>
    <div class="widget-body">
    	<div style="padding-left:10px;padding-top:10px;">
	        <form action="<@ofbizUrl>updateProductCategory</@ofbizUrl>" method="post" style="margin: 0;" class="margin-top10" name="productCategoryForm" id="productCategoryForm">
	            <input type="hidden" name="productCategoryId" value="${productCategoryId}"/>
	            <table cellspacing="0" class="basic-table">
	                <tr>
	                    <td align="right" >${uiLabelMap.ProductProductCategoryId}</td>
	                    <td>&nbsp;</td>
	                    <td>
	                      <b>${productCategoryId}</b> (${uiLabelMap.ProductNotModificationRecreationCategory}.)
	                    </td>
	                </tr>
	</#if>
	                <tr>
	                    <td align="right" >${uiLabelMap.ProductProductCategoryType}</td>
	                    <td>&nbsp;</td>
	                    <td>
	                        <select name="productCategoryTypeId" size="1">
	                            <#assign selectedKey = "">
	                            <#list productCategoryTypes as productCategoryTypeData>
	                                <#if requestParameters.productCategoryTypeId?has_content>
	                                    <#assign selectedKey = requestParameters.productCategoryTypeId>
	                                <#elseif (productCategory?has_content && productCategory.productCategoryTypeId?if_exists == productCategoryTypeData.productCategoryTypeId)>
	                                    <#assign selectedKey = productCategory.productCategoryTypeId>
	                                </#if>
	                                <option <#if selectedKey == productCategoryTypeData.productCategoryTypeId?if_exists>selected="selected"</#if> value="${productCategoryTypeData.productCategoryTypeId}">${productCategoryTypeData.get("description",locale)}</option>
	                            </#list>
	                        </select>
	                    </td>
	                </tr>
	                <tr>
	                    <td align="right" >${uiLabelMap.ProductProductCategoryName}</td>
	                    <td>&nbsp;</td>
	                    <td ><input type="text" value="${(productCategory.categoryName)?if_exists}" name="categoryName" size="60" maxlength="60"/></td>
	                </tr>
	                <tr>
	                    <td  align="right" >${uiLabelMap.ProductProductCategoryDescription}</td>
	                    <td>&nbsp;</td>
	                    <td ><textarea name="description" cols="60" rows="2"><#if productCategory?has_content>${(productCategory.description)?if_exists}</#if></textarea></td>
	                </tr>
	                <tr>
	                    <td  align="right" valign="top" >
	                        ${uiLabelMap.ProductCategoryImageUrl}
	                        <#if (productCategory.categoryImageUrl)?exists>
	                            <a class="btn btn-mini btn-primary" href="<@ofbizContentUrl>${(productCategory.categoryImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Category Image" src="<@ofbizContentUrl>${(productCategory.categoryImageUrl)?if_exists}</@ofbizContentUrl>" class="cssImgSmall" /></a>
	                        </#if>
	                    </td>
	                    <td>&nbsp;</td>
	                    <td  colspan="4" valign="top">
	                        <input type="text" name="categoryImageUrl" value="${(productCategory.categoryImageUrl)?default('')}" size="60" maxlength="255"/>
	                        <#if productCategory?has_content>
	                            <div>
	                            ${uiLabelMap.ProductInsertDefaultImageUrl}:
	                            <a href="javascript:insertImageName('category','${imageNameCategory}.jpg');" class="btn btn-mini btn-primary">.jpg</a>
	                            <a href="javascript:insertImageName('category','${imageNameCategory}.gif');" class="btn btn-mini btn-primary">.gif</a>
	                            <a href="javascript:insertImageName('category','');" class="btn btn-mini btn-primary">${uiLabelMap.CommonClear}</a>
	                            </div>
	                        </#if>
	                    </td>
	                </tr>
	                <tr>
	                    <td  align="right" valign="top" >
	                        ${uiLabelMap.ProductLinkOneImageUrl}
	                        <#if (productCategory.linkOneImageUrl)?exists>
	                            <a href="<@ofbizContentUrl>${(productCategory.linkOneImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Link One Image" src="<@ofbizContentUrl>${(productCategory.linkOneImageUrl)?if_exists}</@ofbizContentUrl>" class="cssImgSmall" /></a>
	                        </#if>
	                    </td>
	                    <td>&nbsp;</td>
	                    <td  colspan="4" valign="top">
	                        <input type="text" name="linkOneImageUrl" value="${(productCategory.linkOneImageUrl)?default('')}" size="60" maxlength="255"/>
	                        <#if productCategory?has_content>
	                            <div>
	                                ${uiLabelMap.ProductInsertDefaultImageUrl}:
	                                <a href="javascript:insertImageName('linkOne','${imageNameLinkOne}.jpg');" class="btn btn-mini btn-primary">.jpg</a>
	                                <a href="javascript:insertImageName('linkOne','${imageNameLinkOne}.gif');" class="btn btn-mini btn-primary">.gif</a>
	                                <a href="javascript:insertImageName('linkOne','');" class="btn btn-mini btn-primary">${uiLabelMap.CommonClear}</a>
	                            </div>
	                        </#if>
	                    </td>
	                </tr>
	                <tr>
	                    <td  align="right" valign="top" >
	                        ${uiLabelMap.ProductLinkTwoImageUrl}
	                        <#if (productCategory.linkTwoImageUrl)?exists>
	                            <a href="<@ofbizContentUrl>${(productCategory.linkTwoImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Link One Image" src="<@ofbizContentUrl>${(productCategory.linkTwoImageUrl)?if_exists}</@ofbizContentUrl>" class="cssImgSmall" /></a>
	                        </#if>
	                    </td>
	                    <td>&nbsp;</td>
	                    <td  colspan="4" valign="top">
	                        <input type="text" name="linkTwoImageUrl" value="${(productCategory.linkTwoImageUrl)?default('')}" size="60" maxlength="255"/>
	                        <#if productCategory?has_content>
	                            <div>
	                                ${uiLabelMap.ProductInsertDefaultImageUrl}:
	                                <a href="javascript:insertImageName('linkTwo','${imageNameLinkTwo}.jpg');" class="btn btn-mini btn-primary">.jpg</a>
	                                <a href="javascript:insertImageName('linkTwo','${imageNameLinkTwo}.gif');" class="btn btn-mini btn-primary">.gif</a>
	                                <a href="javascript:insertImageName('linkTwo','');" class="btn btn-mini btn-primary">${uiLabelMap.CommonClear}</a>
	                            </div>
	                        </#if>
	                    </td>
	                </tr>
	                <tr>
	                    <td  align="right" >${uiLabelMap.ProductDetailScreen}</td>
	                    <td>&nbsp;</td>
	                    <td >
	                        <input type="text" <#if productCategory?has_content>value="${productCategory.detailScreen?if_exists}"</#if> name="detailScreen" size="60" maxlength="250"/>
	                        <br /><span class="tooltipob">${uiLabelMap.ProductDefaultsTo} &quot;categorydetail&quot;, ${uiLabelMap.ProductDetailScreenMessage}: &quot;component://ecommerce/widget/CatalogScreens.xml#categorydetail&quot;</span>
	                    </td>
	                </tr>
	                <tr>
	                    <td  align="right" >${uiLabelMap.ProductPrimaryParentCategory}</td>
	                    <td>&nbsp;</td>
	                    <td >
	                        <@htmlTemplate.lookupField value="${(productCategory.primaryParentCategoryId)?default('')}" formName="productCategoryForm" name="primaryParentCategoryId" id="primaryParentCategoryId" fieldFormName="LookupProductCategory"/>
	                    </td>
	                </tr>
	                <tr>
	                    <td colspan="2">&nbsp;</td>
	                    <td><button class="btn btn-primary btn-small" onclick="productCategoryForm.submit();" type="submit" name="Update" ><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button></td>
	                </tr>
	            </table>
	        </form>
        <div class="margin-top10">
    </div>
</div>
</div>
<#--
<#if productCategoryId?has_content>
    <script language="JavaScript" type="text/javascript">
        function setUploadUrl(newUrl) {
        var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
        eval(toExec);
        };
    </script>
    <script type="text/javascript">
  	function addadditionImages(){$('#fname').ace_file_input({
				no_file:'No File ...',
				btn_choose:'Choose',
				btn_change:'Change',
				droppable:false,
				onchange:null,
				thumbnail:false //| true | large
				//whitelist:'gif|png|jpg|jpeg'
				//blacklist:'exe|php'
				//onchange:''
				//
			});
			}
  </script>
    <div class="widget">
        <div class="widget-header">
            <h4>${uiLabelMap.ProductCategoryUploadImage}</h4>
        </div>
        <div class="widget-body">
        	<div style="padding-left:10px;padding-top:10px;">
	            <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadCategoryImage?productCategoryId=${productCategoryId?if_exists}&amp;upload_file_type=category</@ofbizUrl>" name="imageUploadForm" id="imageUploadForm">
	                <table cellspacing="0" class="basic-table">
	                    <tr><td>
	                        <input type="file" size="30" name="fname" id="fname"/>
	                        <br />
	                        <span>
	                        	<label>
	                            	<input type="radio" name="upload_file_type_bogus" value="category" checked="checked" onclick='setUploadUrl("<@ofbizUrl>UploadCategoryImage?productCategoryId=${productCategoryId}&amp;upload_file_type=category</@ofbizUrl>");'/>
	                            	<span class="lbl"> ${uiLabelMap.ProductCategoryImageUrl}</span>
	                            </label>
	                            <label>
	                            	<input type="radio" name="upload_file_type_bogus" value="linkOne" onclick='setUploadUrl("<@ofbizUrl>UploadCategoryImage?productCategoryId=${productCategoryId}&amp;upload_file_type=linkOne</@ofbizUrl>");'/>
	                            	<span class="lbl"> ${uiLabelMap.ProductLinkOneImageUrl}</span>
	                            <label>
	                            <label>
	                            	<input type="radio" name="upload_file_type_bogus" value="linkTwo"onclick='setUploadUrl("<@ofbizUrl>UploadCategoryImage?productCategoryId=${productCategoryId}&amp;upload_file_type=linkTwo</@ofbizUrl>");'/>
	                        		<span class="lbl"> ${uiLabelMap.ProductLinkTwoImageUrl}</span>
	                        	</label>
	                        </span>
	                        <button onclick="imageUploadForm.submit();" type="submit" class="btn btn-small btn-primary" ><i class="icon-upload-alt"></i>${uiLabelMap.ProductUploadImage}</button>
	                    </td></tr>
	                </table>
	            </form>
            </div>
        </div>
    </div>
    <div class="widget">
        <div class="widget-header">
            <h4>${uiLabelMap.ProductDuplicateProductCategory}</h4>
        </div>
        <div class="widget-body">
        	<div style="padding-left:10px;padding-top:10px;">
	            <form action="<@ofbizUrl>DuplicateProductCategory</@ofbizUrl>" method="post" style="margin: 0;" id="DuplicateProductCategoryob1">
	                <table class="margin-top10;margin-left10;" cellspacing="0" class="basic-table">
	                    <tr><td>
	                        ${uiLabelMap.ProductDuplicateProductCategorySelected}:
	                        <input type="hidden" name="oldProductCategoryId" value="${productCategoryId}"/>
	                        <div>
	                            <input type="text" size="20" maxlength="20" name="productCategoryId"/>&nbsp;<button class="btn btn-small btn-primary margin-top-nav-10" onclick="DuplicateProductCategoryob1.submit();" type="submit" class="smallSubmit" ><i class="icon-copy"></i>${uiLabelMap.CommonGo}</button>
	                        </div>
	                        <div>
	                            <h4 class="blue">${uiLabelMap.CommonDuplicate}:</h4>
	                            <label>
	                            	<input type="checkbox" name="duplicateContent" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.ProductCategoryContent}&nbsp;</span>
	                        	</label>
	                        	<label>
	                            	<input type="checkbox" name="duplicateParentRollup" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.ProductCategoryRollupParentCategories}&nbsp;</span>
	                            </label>
	                            <label>
	                            	<input type="checkbox" name="duplicateChildRollup" value="Y" />
	                        		<span class="lbl"> ${uiLabelMap.ProductCategoryRollupChildCategories}&nbsp;</span>
	                        	</label>
	                        	<label>
	                            	<input type="checkbox" name="duplicateMembers" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.ProductProducts}&nbsp;</span>
	                            </label>
	                            <label>
	                            	<input type="checkbox" name="duplicateCatalogs" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.ProductCatalogs}&nbsp;</span>
	                            </label>
	                       	    <label>
	                            	<input type="checkbox" name="duplicateFeatures" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.ProductFeatures}&nbsp;</span>
	                            </label>
	                            <label>
	                            	<input type="checkbox" name="duplicateRoles" value="Y" checked="checked" />
	                            	<span class="lbl"> ${uiLabelMap.PartyParties}&nbsp;</span>
	                            </label>
	                            <label>
	                            	<input type="checkbox" name="duplicateAttributes" value="Y" checked="checked" />
	                        		<span class="lbl"> ${uiLabelMap.ProductAttributes}&nbsp;</span>
	                        	<label>
	                        </div>
	                    </td></tr>
	                </table>
	            </form>
            </div>
        </div>
    </div>
</#if>
-->
