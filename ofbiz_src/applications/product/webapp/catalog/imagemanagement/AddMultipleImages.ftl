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

<form id="addMultipleImagesForm" name="addMultipleImagesForm" method="post" action="<@ofbizUrl>addImageForProduct</@ofbizUrl>" enctype="multipart/form-data">
<br/>
<div class="row-fluid">
<span class="span12">
<span class="span6">
<div class="margin-left10">${uiLabelMap.ProductProductId} <@htmlTemplate.lookupField name="productId" id="productId" formName="addMultipleImagesForm" fieldFormName="LookupProduct"/></div>
<br/>
<br/>
</span>
<span class="span6">
  <div class="margin-left10">
    	<div class="row-fluid">
    	<span class="span12">
    	<span class="span6">
        <input type="file" size="20" name="additionalImageOne" id="additionalImageOne"/>
     	<input type="file" size="20" name="additionalImageTwo" id="additionalImageTwo"/>
        <input type="file" size="20" name="additionalImageThree" id="additionalImageThree"/>
        <input type="file" size="20" name="additionalImageFour" id="additionalImageFour"/>
        <input type="file" size="20" name="additionalImageFive" id="additionalImageFive"/>
     </span>
      <span class="span6">
  	   <input type="file" size="20" name="additionalImageSix" id="additionalImageSix"/>
        <input type="file" size="20" name="additionalImageSeven" id="additionalImageSeven"/>
        <input type="file" size="20" name="additionalImageEight" id="additionalImageEight"/>
        <input type="file" size="20" name="additionalImageNine" id="additionalImageNine"/>
    	<input type="file" size="20" name="additionalImageTen" id="additionalImageTen"/>
         </span>
    </span>
   	</div>
  </div>
  <div class="row-fluid">
  <span class="span12">
  <span class="span6">
   <select name="imageResize" style="margin-left:10px;" >
                <#list productFeatures as productFeature>
                    <option value="${productFeature.abbrev?if_exists}">${productFeature.description?if_exists}</option>
                </#list>
                <option selected="" value="">Do not resize</option>
  	</select>
  	</span>
  	<span class="span6">
  	<button type="submit" class="btn btn-small btn-primary"><i class="icon-upload-alt"></i>${uiLabelMap.CommonUpload}</button>
  	</span>
  	</span>
  	</div>
</form>
</span>
</span></div>

<script type="text/javascript">
  	function addadditionImages(){$('#additionalImageOne,#additionalImageTwo,#additionalImageThree,#additionalImageFour,#additionalImageFive,#additionalImageSix,#additionalImageSeven,#additionalImageEight,#additionalImageNine,#additionalImageTen').ace_file_input({
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
        var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
        eval(toExec);
    };
  </script>