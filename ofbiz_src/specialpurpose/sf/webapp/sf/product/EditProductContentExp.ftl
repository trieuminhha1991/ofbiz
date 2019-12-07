<#if product?exists>
<script language="JavaScript" type="text/javascript">
    function insertNowTimestamp(field) {
        eval('document.productForm.' + field + '.value="${nowTimestampString}";');
    };
    function insertImageName(type,nameValue) {
        eval('document.productForm.' + type + 'ImageUrl.value=nameValue;');
    };
</script>

    <div class="row-fluid" style="margin-top:25px;">
    <div class="span12">
    <div class="span6">
    <form action="<@ofbizUrl>updateProductContentExp</@ofbizUrl>" method="post" style="margin: 0;" name="productForm">
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
            </table>
            <div style="text-align: center;">
				        <button type="submit" name="Update" class="btn btn-primary btn-small"><i class="icon-save"></i>${uiLabelMap.CommonUpdate}</button>
				    </div>
            </form>
        </table>
    </form>

    </div>
    <script language="JavaScript" type="text/javascript">
        function setUploadUrl(newUrl) {
            var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
            eval(toExec);
        };
    </script>
	<h5><b>${uiLabelMap.sfOriginImage}</b></h5>
    <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadProductImage?productId=${productId}&amp;upload_file_type=original</@ofbizUrl>" name="imageUploadForm" id="imageUploadForm">
        <table cellspacing="0" class="basic-table">
            <tr>
		<td>
				    <#if fileType?has_content>
					<a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>">
						<img onmouseover="changeDivDisplay('<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>');" onmouseout="changeDivDisplay('<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>');" src="<@ofbizContentUrl>${imageUrl?if_exists}</@ofbizContentUrl>" class="cssImgSmall" alt="" />
					</a>
				</#if>
		</td>
                <td width="" align="right" valign="top" colspan="6">
			<div class="ace-file-input" style="width:500px;margin-top: 18px;">
					<input id="fname" type="file" size="50" name="fname" />
				</div>
                </td>
                <tr>
                <td colspan="2">
                    <td><button style="width: 130px;display: block;margin-left: auto;" type="submit" onclick="imageUploadForm.submit();" class="btn btn-primary btn-small"><i class="icon-upload-alt"></i>${uiLabelMap.ProductUploadImage}</button>
                </td>
            </tr>
        </table>
    </form>
    <style type="text/css">
	#divImage{
		    position: absolute;
		    width: auto;
		    height: auto;
		    z-index:999;
		}​
    </style>
    <div id="divImage" style="display:none;top: 50%;left: 50%;">
	<img src="" id="imgImage" style="width:300px;"/>
    </div>
    <script type="text/javascript">
	var cliX = 0;
	var cliY = 0;
		window.onmousemove = function (e) {
		    cliX = e.clientX;
	        cliY = e.clientY;
		};
	function changeDivDisplay(imgSrc){
		var tmpDP = document.getElementById('divImage').style.display;
		if(tmpDP=="none"){
			document.getElementById('divImage').style.display = "block";
			document.getElementById('divImage').style.top = (cliY - 50) + "px";
			document.getElementById('divImage').style.left = (cliX + 50) + "px";
			document.getElementById('imgImage').src = imgSrc;
		}else{
			document.getElementById('divImage').style.display = "none";
		}
	}
    </script>
    <#if product?has_content>
	<#assign productAdditionalImage1 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_1", locale, dispatcher))?if_exists />
	<#assign productAdditionalImage2 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_2", locale, dispatcher))?if_exists />
	<#assign productAdditionalImage3 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_3", locale, dispatcher))?if_exists />
	<#assign productAdditionalImage4 = (Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "ADDITIONAL_IMAGE_4", locale, dispatcher))?if_exists />
	</#if>
	<h5><b>${uiLabelMap.sfAdditionalImages}</b></h5>
    <form class="height230 padding-top8" id="addAdditionalImagesForm" method="post" action="<@ofbizUrl>addAdditionalImagesForProduct</@ofbizUrl>" enctype="multipart/form-data">
		  <input id="additionalImageProductId" type="hidden" name="productId" value="${productId?if_exists}" />
		  <table class="width20pc">
		    <tbody>
		      <tr>
		        <td><#if productAdditionalImage1?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>"><img onmouseover="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>');" onmouseout="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>');" src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
		        <td>
					<div class="ace-file-input" style="width:500px;margin-top: 18px;">
					<input id="additionalImageOne" type="file" size="20" name="additionalImageOne" />
				</div>
		        </td>
		      </tr>
		      <tr>
		        <td><#if productAdditionalImage2?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" ><img onmouseover="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>');" onmouseout="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>');" src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
		        <td>
					<div class="ace-file-input" style="width:500px;margin-top: 18px;">
					<input id="additionalImageTwo" type="file" size="20" name="additionalImageTwo" />
				</div>
		        </td>
		      </tr>
		      <tr>
		        <td><#if productAdditionalImage3?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>"><img onmouseover="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>');" onmouseout="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>');" src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
		        <td>
					<div class="ace-file-input" style="width:500px;margin-top: 18px;">
					<input id="additionalImageThree" type="file" size="20" name="additionalImageThree" />
				</div>
		        </td>
		      </tr>
		      <tr>
		        <td><#if productAdditionalImage4?has_content><a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>"><img onmouseover="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>');" onmouseout="changeDivDisplay('<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>');" src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" class="cssImgSmall" alt="" /></a></#if></td>
		        <td>
					<div class="ace-file-input" style="width:500px;margin-top: 18px;">
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
		        var toExec = 'document.imageUploadForm.action="' + newUrl + '";';
		        eval(toExec);
		    };
		  </script>
    </div>
    </div>
</#if>
