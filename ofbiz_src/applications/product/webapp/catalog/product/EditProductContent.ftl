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
<#if product?exists>
<script language="JavaScript" type="text/javascript">
    function insertNowTimestamp(field) {
        eval('document.productForm.' + field + '.value="${nowTimestampString}";');
    };
    function insertImageName(type,nameValue) {
        eval('document.productForm.' + type + 'ImageUrl.value=nameValue;');
    };
</script>

    <#if fileType?has_content>
        <div class="well">
			<h4 class="blue smaller lighter">${uiLabelMap.ProductResultOfImageUpload}</h4>
			<#if !(clientFileName?has_content)>
			    <div>${uiLabelMap.ProductNoFileSpecifiedForUpload}.</div>
			        <#else>
			    <div>${uiLabelMap.ProductTheFileOnYourComputer}: <b>${clientFileName?if_exists}</b></div>
			    <div>${uiLabelMap.ProductServerFileName}: <b>${fileNameToUse?if_exists}</b></div>
			    <div>${uiLabelMap.ProductServerDirectory}: <b>${imageServerPath?if_exists}</b></div>
			    <div>${uiLabelMap.ProductTheUrlOfYourUploadedFile}: <b><a class="btn btn-mini btn-primary" href="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">${imageUrl?if_exists}</a></b></div>
	        </#if>
		</div>
    <br />
    </#if>
    <div class="row-fluid" style="margin-top:25px;">
    <div class="span12">
    <div class="span6">
    <form action="<@ofbizUrl>updateProductContent</@ofbizUrl>" method="post" style="margin: 0;" name="productForm">
        <input type="hidden" name="productId" value="${productId?if_exists}"/>
        <table cellspacing="0" class="basic-table">
            <tr>
                <td width="20%" align="right" valign="top"><b>${uiLabelMap.ProductProductName}</b></td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="productName" value="${(product.productName?html)?if_exists}" size="30" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top"><b>${uiLabelMap.ProductProductDescription}</b></td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <textarea name="description" cols="60" rows="2">${(product.description)?if_exists}</textarea>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top"><b>${uiLabelMap.ProductLongDescription}</b></td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <textarea class="dojo-ResizableTextArea" name="longDescription" cols="60" rows="7">${(product.longDescription)?if_exists}</textarea>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top"><b>${uiLabelMap.ProductDetailScreen}</b></td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="detailScreen" value="${(product.detailScreen)?if_exists}" size="60" maxlength="250"/>
                    <br /><span class="tooltip">${uiLabelMap.ProductIfNotSpecifiedDefaultsIsProductdetail} &quot;productdetail&quot;, ${uiLabelMap.ProductDetailScreenMessage}: &quot;component://ecommerce/widget/CatalogScreens.xml#productdetail&quot;</span>
                </td>
            </tr>
            </table>
            </form>
            </div>
            <div class="span6">
            <form>
            <table>
            <tr>
                <td width="20%" align="right" valign="top">
                    <div><b>${uiLabelMap.ProductSmallImage}</b></div>
    <#if (product.smallImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" target="_blank"><img alt="Small Image" src="<@ofbizContentUrl>${(product.smallImageUrl)?if_exists}</@ofbizContentUrl>" class="cssImgSmall"/></a>
    </#if>
                </td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="smallImageUrl" value="${(product.smallImageUrl)?default('')}" size="60" maxlength="255"/>
    <#if productId?has_content>
                    <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a href="javascript:insertImageName('small','${imageNameSmall}.jpg');" class="btn btn-primary btn-mini">.jpg</a>
                        <a href="javascript:insertImageName('small','${imageNameSmall}.gif');" class="btn btn-primary btn-mini">.gif</a>
                        <a href="javascript:insertImageName('small','');" class="btn btn-primary btn-mini">${uiLabelMap.CommonClear}</a>
                    </div>
    </#if>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top">
                    <div><b>${uiLabelMap.ProductMediumImage}</b></div>
    <#if (product.mediumImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Medium Image" src="<@ofbizContentUrl>${product.mediumImageUrl}</@ofbizContentUrl>" class="cssImgSmall"/></a>
    </#if>
                </td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="mediumImageUrl" value="${(product.mediumImageUrl)?default('')}" size="60" maxlength="255"/>
    <#if productId?has_content>
                    <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a href="javascript:insertImageName('medium','${imageNameMedium}.jpg');" class="btn btn-primary btn-mini">.jpg</a>
                        <a href="javascript:insertImageName('medium','${imageNameMedium}.gif');" class="btn btn-primary btn-mini">.gif</a>
                        <a href="javascript:insertImageName('medium','');" class="btn btn-primary btn-mini">${uiLabelMap.CommonClear}</a>
                    </div>
    </#if>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top">
                    <div><b>${uiLabelMap.ProductLargeImage}</b></div>
    <#if (product.largeImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Large Image" src="<@ofbizContentUrl>${product.largeImageUrl}</@ofbizContentUrl>" class="cssImgSmall"/></a>
    </#if>
                </td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="largeImageUrl" value="${(product.largeImageUrl)?default('')}" size="60" maxlength="255"/>
    <#if productId?has_content>
                    <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a href="javascript:insertImageName('large','${imageNameLarge}.jpg');" class="btn btn-primary btn-mini">.jpg</a>
                        <a href="javascript:insertImageName('large','${imageNameLarge}.gif');" class="btn btn-primary btn-mini">.gif</a>
                        <a href="javascript:insertImageName('large','');" class="btn btn-primary btn-mini">${uiLabelMap.CommonClear}</a>
                    </div>
    </#if>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top">
                    <div><b>${uiLabelMap.ProductDetailImage}</b></div>
    <#if (product.detailImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Detail Image" src="<@ofbizContentUrl>${product.detailImageUrl}</@ofbizContentUrl>" class="cssImgSmall"/></a>
    </#if>
                </td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="detailImageUrl" value="${(product.detailImageUrl)?default('')}" size="60" maxlength="255"/>
    <#if productId?has_content>
                    <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a href="javascript:insertImageName('detail','${imageNameDetail}.jpg');" class="btn btn-primary btn-mini">.jpg</a>
                        <a href="javascript:insertImageName('detail','${imageNameDetail}.gif');" class="btn btn-primary btn-mini">.gif</a>
                        <a href="javascript:insertImageName('detail','');" class="btn btn-primary btn-mini">${uiLabelMap.CommonClear}</a>
                    </div>
    </#if>
                </td>
            </tr>
            <tr>
                <td width="20%" align="right" valign="top">
                    <div><b>${uiLabelMap.ProductOriginalImage}</b></div>
    <#if (product.originalImageUrl)?exists>
                    <a href="<@ofbizContentUrl>${product.originalImageUrl}</@ofbizContentUrl>" target="_blank"><img alt="Original Image" src="<@ofbizContentUrl>${product.originalImageUrl}</@ofbizContentUrl>" class="cssImgSmall"/></a>
    </#if>
                </td>
                <td>&nbsp;</td>
                <td width="80%" colspan="4" valign="top">
                    <input type="text" name="originalImageUrl" value="${(product.originalImageUrl)?default('')}" size="60" maxlength="255"/>
    <#if productId?has_content>
                    <div>
                        <span>${uiLabelMap.ProductInsertDefaultImageUrl}: </span>
                        <a href="javascript:insertImageName('original','${imageNameOriginal}.jpg');" class="btn btn-primary btn-mini">.jpg</a>
                        <a href="javascript:insertImageName('original','${imageNameOriginal}.gif');" class="btn btn-primary btn-mini">.gif</a>
                        <a href="javascript:insertImageName('original','');" class="btn btn-primary btn-mini">${uiLabelMap.CommonClear}</a>
                    </div>
    </#if>
                </td>
            </tr>
        </table>
    </form>
    <script language="JavaScript" type="text/javascript">
    	function addadditionImages(){$('#profname').ace_file_input({
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
		$(document).ready(function(){
			addadditionImages();	
		});
        function setUploadUrl(newUrl) {
            //var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
            //eval(toExec);
            var fileType = document.getElementById("imageUploadForm_upload_file_type");
            fileType.value = newUrl;
        };
    </script>

    <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadProductImage</@ofbizUrl>" name="imageUploadForm" id="imageUploadForm">
        <table cellspacing="0" class="basic-table" style="margin-top:20px;">
            <tr>
                <td width="" align="left" valign="top" colspan="6">
                	<div class="ace-file-input">
                		<input type="hidden" id="imageUploadForm_upload_file_type" name="upload_file_type" value="original"/>
                		<input type="hidden" id="imageUploadForm_productId" name="productId" value="${productId}"/>
                		<input type="file" size="50" name="uploadedFile" id="profname">
            		</div>
                </td>
                <tr>
                <td>
                	<label>
	                    <input type="radio" name="upload_file_type_bogus" value="small" onclick="setUploadUrl('small')"/>
	                    <span class="lbl">${uiLabelMap.CommonSmall}</span>
                    </label></td>
                    <td><label>
                    	<input type="radio" name="upload_file_type_bogus" value="medium" onclick="setUploadUrl('medium')"/>
                    	<span class="lbl">${uiLabelMap.CommonMedium}</span>
                    </label></td>
                    <td><label>
                    	<input type="radio" name="upload_file_type_bogus" value="large"onclick="setUploadUrl('large')"/>
                    	<span class="lbl">${uiLabelMap.CommonLarge}</span>
                    </label></td>
                    <td><label>
                    	<input type="radio" name="upload_file_type_bogus" value="detail" onclick="setUploadUrl('detail')"/>
                   		<span class="lbl">${uiLabelMap.CommonDetail}</span>
                    </label></td>
                    <td><label>
                    	<input type="radio" name="upload_file_type_bogus" value="original" checked="checked" onclick="setUploadUrl('original')"/>
                    	<span class="lbl">${uiLabelMap.ProductOriginal}</span>
                    </label></td>
                    <td><button style="width: 130px;display: block;margin-left: auto;" type="submit" onclick="imageUploadForm.submit();" class="btn btn-primary btn-small"><i class="icon-upload-alt"></i>${uiLabelMap.ProductUploadImage}</button>
                </td>
            </tr>
        </table>
    </form>
    </div>
    </div>
    </div>
    <div style="text-align: center;">
        <button type="submit" name="Update" class="btn btn-primary btn-small"><i class="icon-save"></i>${uiLabelMap.CommonUpdate}</button>
    </div>
</#if>
