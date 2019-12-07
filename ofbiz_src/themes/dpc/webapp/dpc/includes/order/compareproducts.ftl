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
<div class="cartdiv">
	<table>
	<#-- Header row, contains product small image, product name, price -->
	    <tr>
	        <td>&nbsp;</td>
	<#list compareList as product>
	    <#assign tdWidth = 100/compareList?size />
	    <#assign productData = productDataMap[product.productId]/>
	    <#assign productContentWrapper = productData.productContentWrapper/>
	    <#assign price = productData.priceMap/>
	    <#assign productUrl><@ofbizCatalogAltUrl productId=product.productId/></#assign>
	    <#assign smallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")/>
	    <#if smallImageUrl!?length == 0>
	        <#assign smallImageUrl = "/images/defaultImage.jpg"/>
	    </#if>
	        <td style="width:${tdWidth?c}%;">
	            <img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Small Image"/><br />
	            ${productContentWrapper.get("PRODUCT_NAME")}<br />
	    <#if totalPrice?exists>
	            <div>${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
	    <#else>
	        <#if price.isSale?exists && price.isSale>
	            <#assign priceStyle = "salePrice">
	        <#else>
	            <#assign priceStyle = "regularPrice">
	        </#if>

	        <#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
	                <#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><span class="${priceStyle}"><@ofbizCurrency amount=price.price isoCode=price.currencyUsed/></span>
	        </#if>
	    </#if>
	            <div class="productbuy">
	    <#-- check to see if introductionDate hasn't passed yet -->
	    <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
	                <div style="color: red;">${uiLabelMap.ProductNotYetAvailable}</div>
	    <#-- check to see if salesDiscontinuationDate has passed -->
	    <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)/>
	                <div style="color: red;">${uiLabelMap.ProductNoLongerAvailable}</div>
	    <#-- check to see if it is a rental item; will enter parameters on the detail screen-->
	    <#elseif product.productTypeId?if_exists == "ASSET_USAGE"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderMakeBooking}...</a>
	    <#elseif product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderRent}...</a>
	    <#-- check to see if it is an aggregated or configurable product; will enter parameters on the detail screen-->
	    <#elseif product.productTypeId?if_exists == "AGGREGATED" || product.productTypeId?if_exists == "AGGREGATED_SERVICE"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderConfigure}...</a>
	    <#-- check to see if the product is a virtual product -->
	    <#elseif product.isVirtual?exists && product.isVirtual == "Y"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderChooseVariations}...</a>
	    <#-- check to see if the product requires an amount -->
	    <#elseif product.requireAmount?exists && product.requireAmount == "Y"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderChooseAmount}...</a>
	    <#else/>
	                <form method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">additem</@ofbizUrl>" name="compareFormAdd${product_index}">
	                    <input type="hidden" name="add_product_id" value="${product.productId}"/>
	                    <input type="text" size="5" name="quantity" value="1"/>
	                    <input type="hidden" name="clearSearch" value="N"/>
	                </form>
	                <a href="javascript:doPostViaParent('compareFormAdd${product_index}');" class="button">${uiLabelMap.OrderAddToCart}</a>

	        <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
	                <form method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">additem</@ofbizUrl>" name="compareFormAddDefault${product_index}" style="margin: 0;">
	                    <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
	                    <input type="hidden" name="quantity" value="${prodCatMem.quantity?if_exists}"/>
	                    <input type="hidden" name="clearSearch" value="N"/>
	                </form>
	                <a href="javascript:doPostViaParent('compareFormAddDefault${product_index}');" class="button">${uiLabelMap.CommonAddDefault}(${prodCatMem.quantity?string.number}) ${uiLabelMap.OrderToCart}</a>
	        </#if>
	    </#if>
	            </div>
	        </td>
	</#list>
	    </tr>
	    <#-- Brand name -->
	    <tr>
	        <td><b>${uiLabelMap.ProductBrandName}</b></td>
	<#list compareList as product>
	        <td>${product.brandName?default("&nbsp;")}</td>
	</#list>
	    </tr>
	    <#-- Description -->
	    <tr>
	        <td><b>${uiLabelMap.ProductProductDescription}</b></td>
	<#list compareList as product>
	    <#assign productData = productDataMap[product.productId]/>
	    <#assign productContentWrapper = productData.productContentWrapper/>
	        <td>${productContentWrapper.get("DESCRIPTION")?default("&nbsp;")}</td>
	</#list>
	    </tr>
	    <#-- Long Description -->
	    <tr>
	        <td><b>${uiLabelMap.ProductLongDescription}</b></td>
	<#list compareList as product>
	    <#assign productData = productDataMap[product.productId]/>
	    <#assign productContentWrapper = productData.productContentWrapper/>
		<style type="text/css">
			ul{
					text-align: left;
					padding: 20px;
				}
		</style>
	        <td style="border-left:solid 1px #DDDDDD;">${productContentWrapper.get("LONG_DESCRIPTION")?default("&nbsp;")}</td>
	</#list>
	    </tr>
	<#list productFeatureTypeIds as productFeatureTypeId>
	    <#assign productFeatureType = productFeatureTypeMap[productFeatureTypeId]/>
	    <tr>
	        <td>${productFeatureType.get("description", locale)}</td>
	    <#list compareList as product>
	        <#assign productData = productDataMap[product.productId]/>
	        <#assign applMap = productData[productFeatureTypeId]?if_exists/>
	        <td>
	        <#if applMap.STANDARD_FEATURE?has_content>
	            <#assign features = applMap.STANDARD_FEATURE/>
	            <#list features as feature>
	            <div>${feature.get("description", locale)}</div>
	            </#list>
	        </#if>
	        <#if applMap.DISTINGUISHING_FEAT?has_content>
	            <#assign features = applMap.DISTINGUISHING_FEAT/>
	            <#list features as feature>
	            <div>${feature.get("description", locale)}</div>
	            </#list>
	        </#if>
	        <#if applMap.SELECTABLE_FEATURE?has_content>
	            <#assign features = applMap.SELECTABLE_FEATURE/>
	            <b>Available Options:</b>
	            <table>
		            <#list features as feature>
				<tr>
					<td>${feature.get("description", locale)}</td>
				</tr>
		            </#list>
	            </table>
	        </#if>
	        </td>
	    </#list>
	    </tr>
	</#list>
	    <tr>
	        <td>&nbsp;</td>
	<#list compareList as product>
	        <td>
	            <div class="productbuy">
	    <#-- check to see if introductionDate hasn't passed yet -->
	    <#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
	                <div style="color: red;">${uiLabelMap.ProductNotYetAvailable}</div>
	    <#-- check to see if salesDiscontinuationDate has passed -->
	    <#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)/>
	                <div style="color: red;">${uiLabelMap.ProductNoLongerAvailable}</div>
	    <#-- check to see if it is a rental item; will enter parameters on the detail screen-->
	    <#elseif product.productTypeId?if_exists == "ASSET_USAGE"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderMakeBooking}...</a>
	    <#elseif product.productTypeId?if_exists == "ASSET_USAGE_OUT_IN"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderRent}...</a>
	    <#-- check to see if it is an aggregated or configurable product; will enter parameters on the detail screen-->
	    <#elseif product.productTypeId?if_exists == "AGGREGATED" || product.productTypeId?if_exists == "AGGREGATED_SERVICE"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderConfigure}...</a>
	    <#-- check to see if the product is a virtual product -->
	    <#elseif product.isVirtual?exists && product.isVirtual == "Y"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderChooseVariations}...</a>
	    <#-- check to see if the product requires an amount -->
	    <#elseif product.requireAmount?exists && product.requireAmount == "Y"/>
	                <a href="javascript:doGetViaParent('${productUrl}');" class="button">${uiLabelMap.OrderChooseAmount}...</a>
	    <#else/>
	                <form method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">additem</@ofbizUrl>" name="compare2FormAdd${product_index}">
	                    <input type="hidden" name="add_product_id" value="${product.productId}"/>
	                    <input type="text" size="5" name="quantity" value="1"/>
	                    <input type="hidden" name="clearSearch" value="N"/>
	                </form>
	                <a href="javascript:doPostViaParent('compare2FormAdd${product_index}');" class="button">${uiLabelMap.OrderAddToCart}</a>
	        <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
	                <a href="javascript:doPostViaParent('compareFormAddDefault${product_index}');" class="button">${uiLabelMap.CommonAddDefault}(${prodCatMem.quantity?string.number}) ${uiLabelMap.OrderToCart}</a>
	        </#if>
	    </#if>
	            </div>
	        </td>
	</#list>
	    </tr>
	</table>
	<br /><br />
</div>