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
function insertNowTimestamp(field) {
  eval('document.productForm.' + field + '.value="${nowTimestamp?string}";');
}
function insertImageName(size,nameValue) {
  eval('document.productForm.' + size + 'ImageUrl.value=nameValue;');
}
</script>
<#if fileType?has_content>
    <h3 class="header smaller lighter blue">${uiLabelMap.ProductResultOfImageUpload}</h3>
    <#if !(clientFileName?has_content)>
        <div>${uiLabelMap.ProductNoFileSpecifiedForUpload}.</div>
    <#else>
        <div>${uiLabelMap.ProductTheFileOnYourComputer}: <b>${clientFileName?if_exists}</b></div>
        <div>${uiLabelMap.ProductServerFileName}: <b>${fileNameToUse?if_exists}</b></div>
        <div>${uiLabelMap.ProductServerDirectory}: <b>${imageServerPath?if_exists}</b></div>
        <div>${uiLabelMap.ProductTheUrlOfYourUploadedFile}: <b><a class="btn btn-mini btn-primary" href="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">${imageUrl?if_exists}</a></b></div>
    </#if>
<br />
</#if>
<#if !(configItem?exists)>
    <h3 class="header smaller lighter blue">${uiLabelMap.ProductCouldNotFindProductConfigItem} "${configItemId}".</h3>
<#else>
    <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable margin-top10">
        <tr class="header-row">
            <td><b>${uiLabelMap.ProductContent}</b></td>
            <td><b>${uiLabelMap.ProductType}</b></td>
            <td><b>${uiLabelMap.CommonFrom}</b></td>
            <td><b>${uiLabelMap.CommonThru}</b></td>
            <td><b>&nbsp;</b></td>
            <td><b>&nbsp;</b></td>
        </tr>
        <#assign rowClass = "2">
        <#list productContentList as entry>
        <#assign productContent=entry.productContent/>
        <#assign productContentType=productContent.getRelatedOne("ProdConfItemContentType", true)/>
        <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
            <td><a class="btn btn-mini btn-primary" href="<@ofbizUrl>EditProductConfigItemContentContent?configItemId=${productContent.configItemId}&amp;contentId=${productContent.contentId}&amp;confItemContentTypeId=${productContent.confItemContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>">${entry.content.description?default("[${uiLabelMap.ProductNoDescription}]")} [${entry.content.contentId}]</td>
            <td>${productContentType.description?default(productContent.confItemContentTypeId)}</td>
            <td>${productContent.fromDate?default("N/A")}</td>
            <td>${productContent.thruDate?default("N/A")}</td>
            <td><a class="btn btn-mini btn-danger" href="<@ofbizUrl>removeContentFromProductConfigItem?configItemId=${productContent.configItemId}&amp;contentId=${productContent.contentId}&amp;confItemContentTypeId=${productContent.confItemContentTypeId}&amp;fromDate=${productContent.fromDate}</@ofbizUrl>">${uiLabelMap.CommonDelete}</a></td>
            <td><a class="btn btn-mini btn-primary" href="/content/control/EditContent?contentId=${productContent.contentId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${uiLabelMap.ProductEditContent} ${entry.content.contentId}</td>
         </tr>
         <#-- toggle the row color -->
         <#if rowClass == "2">
             <#assign rowClass = "1">
         <#else>
             <#assign rowClass = "2">
         </#if>
         </#list>
    </table>
    <br />
    <#if configItemId?has_content && configItem?has_content>
        <div class="widget-box transparent no-bottom-border">
            <div class="widget-header">
                <h3>${uiLabelMap.ProductCreateNewProductConfigItemContent}</h3>
                <span class="widget-toolbar none-content">
                	 ${screens.render("component://product/widget/catalog/ConfigScreens.xml#ConfigMenuSubTabBar")}
                </span>
            </div>
            <div class="screenlet-body">
                ${prepareAddProductContentWrapper.renderFormString(context)}
            </div>
        </div>
        <div class="screenlet">
            <div class="screenlet-title-bar">
                <h3 class="header smaller lighter blue">${uiLabelMap.ProductAddContentProductConfigItem}</h3>
            </div>
            <div class="screenlet-body">
                ${addProductContentWrapper.renderFormString(context)}
            </div>
        </div>
    </#if>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3 class="header smaller lighter blue">${uiLabelMap.ProductOverrideSimpleFields}</h3>
        </div>
        <div class="screenlet-body">
            <form action="<@ofbizUrl>updateProductConfigItemContent</@ofbizUrl>" method="post" style="margin: 0;" name="productForm">
                <input type="hidden" name="configItemId" value="${configItemId?if_exists}" />
                <table cellspacing="0" class="basic-table">
                <tr>
                    <td align="right" valign="top">${uiLabelMap.CommonDescription}</td>
                    <td>&nbsp;</td>
                    <td colspan="4" valign="top">
                        <textarea name="description" cols="60" rows="2">${(configItem.description)?if_exists}</textarea>
                    </td>
                </tr>
                <tr>
                    <td align="right" valign="top">${uiLabelMap.ProductLongDescription}</td>
                    <td>&nbsp;</td>
                    <td colspan="4" valign="top">
                        <textarea name="longDescription" cols="60" rows="7">${(configItem.longDescription)?if_exists}</textarea>
                    </td>
                </tr>
                <tr>
                    <td align="right" valign="top">
                        ${uiLabelMap.ProductSmallImage}
                        <#if (configItem.imageUrl)?exists>
                            <a class="btn btn-mini btn-primary" href="<@ofbizContentUrl>${configItem.imageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Image" src="<@ofbizContentUrl>${configItem.imageUrl}</@ofbizContentUrl>" class="cssImgSmall" /></a>
                        </#if>
                    </td>
                    <td>&nbsp;</td>
                    <td colspan="4" valign="top">
                    <input type="text" name="imageUrl" value="${(configItem.imageUrl)?default(imageNameSmall + '.jpg')}" size="60" maxlength="255" />
                    <#if configItemId?has_content>
                        <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a class="btn btn-mini btn-primary" href="javascript:insertImageName('small','${imageNameSmall}.jpg');">.jpg</a>
                        <a class="btn btn-mini btn-primary" href="javascript:insertImageName('small','${imageNameSmall}.gif');">.gif</a>
                        <a class="btn btn-mini btn-primary" href="javascript:insertImageName('small','');">${uiLabelMap.CommonClear}</a>
                        </div>
                    </#if>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"></td>
                    <td>
                    <button type="submit" name="Update" class="btn btn-mini btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
                    </td>
                    <td colspan="3">&nbsp;</td>
                </tr>
                </table>
            </form>
        </div>
    </div>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3 class="header smaller lighter blue">${uiLabelMap.ProductUploadImage}</h3>
        </div>
        <div class="screenlet-body">
            <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadProductConfigItemImage?configItemId=${configItemId}&amp;upload_file_type=small</@ofbizUrl>" name="imageUploadForm">
                <div class="ace-file-input width20pc">
                	<input type="file" size="50" name="fname" id="fname"/>
                	<label data-title="Choose" for="fname"><span data-title="No File ..."><i class="icon-upload-alt"></i></span></label>
            	</div>
                <button type="submit" class="btn btn-mini btn-primary"><i class="icon-upload-alt"></i>${uiLabelMap.ProductUploadImage}</button>
            </form>
        </div>
    </div>
</#if>
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