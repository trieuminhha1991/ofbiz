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

<#if requestParameters.product_id?exists>
  <form id="reviewProduct" method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
      <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
      <input type="hidden" name="productId" value="${requestParameters.product_id}" />
      <input type="hidden" name="product_id" value="${requestParameters.product_id}" />
      <input type="hidden" name="category_id" value="${requestParameters.category_id}" />
      <h2 id="review-title">Write a review</h2><br>
		<div class="control-group">
		    <label class="control-label">${uiLabelMap.EcommercePostAnonymous} <span class="text-error">*</span></label>
		    <div class="controls">
		        <span>
		          <label for="yes">${uiLabelMap.CommonYes}</label>
		          <input type="radio" id="yes" name="postedAnonymous" value="Y" />
		        </span>
		        <span>
		          <label for="no">${uiLabelMap.CommonNo}</label>
		          <input type="radio" id="no" name="postedAnonymous" value="N" checked="checked" />
		        </span>
		    </div>
		</div>
		<div class="control-group">
			<label class="control-label" for="productReview">${uiLabelMap.CommonReview} <span class="text-error">*</span></label>
		    <div class="controls">
		      <textarea name="productReview" id="productReview" placeholder="${uiLabelMap.CommonReview}..."  cols="40" rows="8" style="width: 98%;"></textarea>
		    </div>
		</div>
		<div class="control-group">
		    <div class="controls" id="review">
			<b>${uiLabelMap.EcommerceRating}*:</b> <span>Bad</span>&nbsp;
		        <span>
		          <label for="one">1</label>
		          <input type="radio" id="one" name="productRating" value="1.0" />
		        </span>
		        <span>
		          <label for="two">2</label>
		          <input type="radio" id="two" name="productRating" value="2.0" />
		        </span>
		        <span>
		          <label for="three">3</label>
		          <input type="radio" id="three" name="productRating" value="3.0" />
		        </span>
		        <span>
		          <label for="four">4</label>
		          <input type="radio" id="four" name="productRating" value="4.0" />
		        </span>
		        <span>
		          <label for="five">5</label>
		          <input type="radio" id="five" name="productRating" value="5.0" />
		        </span>
		        &nbsp;<span>Good</span><br>
		    </div>
		</div>
		<div class="control-group">
		    <div class="controls">
				<input type="button" value="${uiLabelMap.CommonSave}" id="savereview" class="button" onclick="javascript:document.getElementById('reviewProduct').submit();">
		    </div>
		</div>
  </form>
<#else>
  <h2>${uiLabelMap.ProductCannotReviewUnKnownProduct}.</h2>
</#if>