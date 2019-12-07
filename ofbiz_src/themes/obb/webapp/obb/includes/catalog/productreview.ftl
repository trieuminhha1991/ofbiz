<div class="skin-default contacts-index-index">
	<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
		<div class="main clearfix">
			<div id="jm-mainbody" class="clearfix">
				<div id="jm-main">
					<div class="inner clearfix">
						<div id="jm-current-content" class="clearfix">
						  <div class="jm-contacts" style="margin:0px;">
							  <div style="margin-top:10px;margin-left:10px;margin-bottom:10px;">
								<#if requestParameters.product_id?exists>
								  <form id="reviewProduct" method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
								    <fieldset class="inline">
								      <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
								      <input type="hidden" name="productId" value="${requestParameters.product_id}" />
								      <input type="hidden" name="product_id" value="${requestParameters.product_id}" />
								      <input type="hidden" name="category_id" value="${requestParameters.category_id}" />
								      <div>
								        <span><label for="one">${uiLabelMap.ObbRating}:</label></span>
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
								      </div>
								      <div>
								        <span><label for="yes">${uiLabelMap.ObbPostAnonymous}:</label></span>
								        <span>
								          <label for="yes">${uiLabelMap.CommonYes}</label>
								          <input type="radio" id="yes" name="postedAnonymous" value="Y" />
								        </span>
								        <span>
								          <label for="no">${uiLabelMap.CommonNo}</label>
								          <input type="radio" id="no" name="postedAnonymous" value="N" checked="checked" />
								        </span>
								      </div>
								      <div>
								        <label for="review">${uiLabelMap.CommonReview}:</label>
								        <textarea class="textAreaBox" name="productReview" cols="40"></textarea>
								      </div>
								      <div>
								        <a href="javascript:document.getElementById('reviewProduct').submit();" class="button">[${uiLabelMap.CommonSave}]</a>
								        <a href="<@ofbizUrl>product?product_id=${requestParameters.product_id}</@ofbizUrl>" class="button">[${uiLabelMap.CommonCancel}]</a>
								      </div>
								    </fieldset>
								  </form>
								<#else>
								  <h2>${uiLabelMap.ProductCannotReviewUnKnownProduct}.</h2>
								</#if>
							</div>
						  </div>
					  </div>
				  </div>
			  </div>
		  </div>
	  </div>
  </div>
</div>