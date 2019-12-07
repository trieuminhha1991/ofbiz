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
<#if product?has_content>
  <#assign productAdditionalImage1 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_1", locale, dispatcher))?if_exists />
  <#assign productAdditionalImage2 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_2", locale, dispatcher))?if_exists />
  <#assign productAdditionalImage3 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_3", locale, dispatcher))?if_exists />
  <#assign productAdditionalImage4 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_4", locale, dispatcher))?if_exists />
</#if>
<div class="row-fluid">
<div class="span12">
<div class="span6">
<h3 class="header smaller lighter blue">${uiLabelMap.ProductUploadImage}</h3>
    <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadProductImage</@ofbizUrl>" name="imageUploadForm" id="imageUploadForm">
        <table cellspacing="0" class="basic-table" style="margin-top:20px;">
            <tr>
                <td width="20%" align="left" valign="top" colspan="6">
            		<div class="ace-file-input">
						<input type="hidden" id="imageUploadForm_upload_file_type" name="upload_file_type" value="original"/>
			        	<input type="hidden" id="imageUploadForm_productId" name="productId" value="${productId}"/>
			        	<input id="fname" type="file" size="20" name="fname" />
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
        <span class="tooltipob">${uiLabelMap.ProductOriginalImageMessage} : {ofbiz.home}/applications/product/config/ImageProperties.xml&quot;</span>
    </form>
    </div>
    <div class="span6">
    <h3 class="header smaller lighter blue">${uiLabelMap.ProductAddAdditionalImages}</h3>
<form class="height230 padding-top8" id="addAdditionalImagesForm" method="post" action="<@ofbizUrl>addAdditionalImagesForProduct</@ofbizUrl>" enctype="multipart/form-data">
  <input id="additionalImageProductId" type="hidden" name="productId" value="${productId?if_exists}" />
  <table class="width20pc">
    <tbody>
      <tr>
        <td><#if productAdditionalImage1?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
        <td>
			<div class="ace-file-input" style="width:500px;margin-top: 12px;">
	        	<input id="additionalImageOne" type="file" size="20" name="additionalImageOne" />
        	</div>
        </td>
      </tr>
      <tr>
        <td><#if productAdditionalImage2?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" ><img src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
        <td>
			<div class="ace-file-input">
	        	<input id="additionalImageTwo" type="file" size="20" name="additionalImageTwo" />
        	</div>
        </td>
      </tr>
      <tr>
        <td><#if productAdditionalImage3?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
        <td>
			<div class="ace-file-input">
	        	<input id="additionalImageThree" type="file" size="20" name="additionalImageThree" />
        	</div>
        </td>
      </tr>
      <tr>
        <td><#if productAdditionalImage4?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
        <td>
			<div class="ace-file-input">
	        	<input id="additionalImageFour" type="file" size="20" name="additionalImageFour" />
        	</div>
        </td>
      </tr>
      <tr>
        <td></td>
        <td><button style="display:block; margin-left:auto;" class="btn btn-primary btn-small" onclick="addAdditionalImagesForm.submit();" type="submit" ><i class="icon-upload-alt"></i>${uiLabelMap.CommonUpload}</button></td>
      </tr>
    </tbody>
  </table>
  <div class="right" style='margin-top:-250px;'>
    <a href="javascript:void(0);"><img id="detailImage" name="mainImage" vspace="5" hspace="5" width="150" height="150" style='margin-left:50px' src="" alt="" /></a>
    <input type="hidden" id="originalImage" name="originalImage" />
  </div>
</form>
  <script type="text/javascript">
  	function addadditionImages(){$('#additionalImageOne , #additionalImageTwo , #additionalImageThree , #additionalImageFour , #fname').ace_file_input({
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
	$( document ).ready(function() {
	  addadditionImages();
	});
	function setUploadUrl(newUrl) {
        //var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
        //eval(toExec);
        var fileType = document.getElementById("imageUploadForm_upload_file_type");
        fileType.value = newUrl;
    };
  </script>
</div>
</div>
</div>
