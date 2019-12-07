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

<#if productCategoryList?has_content>
	<div class="box">
		<div class="box-heading">
		<span>Popular Categories</span>
		</div>
	    <div class="box-content">
	       <div class="box-product">
            <#list productCategoryList as childCategoryList>
                   <#assign cateCount = 0/>
                   <#list childCategoryList as productCategory>
                       <#if (cateCount > 2)>
                            <#assign cateCount = 0/>
                       </#if>
                       <#assign productCategoryId = productCategory.productCategoryId/>
                       <#assign categoryImageUrl = "/images/defaultImage.jpg">
                       <#assign productCategoryMembers = delegator.findByAnd("ProductCategoryAndMember", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategoryId), Static["org.ofbiz.base.util.UtilMisc"].toList("-quantity"), false)>
                       <#if productCategory.categoryImageUrl?has_content>
                            <#assign categoryImageUrl = productCategory.categoryImageUrl/>
                       <#elseif productCategoryMembers?has_content>
                            <#assign productCategoryMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMembers)/>
                            <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productCategoryMember.productId), false)/>
                            <#if product.smallImageUrl?has_content>
                                <#assign categoryImageUrl = product.smallImageUrl/>
                            </#if>
                       </#if>
                            <div>
                                <div class="image">
                                    <a href="<@ofbizCatalogAltUrl productCategoryId=productCategoryId/>">
                                        <img alt="Small Image" src="${categoryImageUrl}">
                                    </a>
                                </div>
                                <div class="name">
                                    <a href="<@ofbizCatalogAltUrl productCategoryId=productCategoryId/>">${productCategory.categoryName!productCategoryId}</a>
                                </div>
                                <div class="rating">

                                </div>
                                <div class="rating">

                                </div>
                                <div class="cart">
					<input type="button" value="Add to Cart" onclick="addToCart('40');" class="button">
                                </div>
                                <div class="productinfo">
                                    <ul>
                                    <#if productCategoryMembers?exists>
                                        <#assign i = 0/>
                                        <#list productCategoryMembers as productCategoryMember>
                                            <#if (i > 2)>
                                                <#if productCategoryMembers[i]?has_content>
                                                    <a class="linktext" href="<@ofbizCatalogAltUrl productCategoryId=productCategoryId/>">
                                                        <span>More...</span>
                                                    </a>
                                                </#if>
                                                <#break>
                                            </#if>
                                            <#if productCategoryMember?has_content>
                                                <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productCategoryMember.productId), false)>
                                                <li class="browsecategorytext">
                                                    <a class="linktext" href="<@ofbizCatalogAltUrl productCategoryId="PROMOTIONS" productId="${product.productId}"/>">
                                                        ${product.productName!product.productId}
                                                    </a>
                                                </li>
                                            </#if>
                                            <#assign i = i+1/>
                                        </#list>
                                    </#if>
                                    </ul>
                                </div>
                            </div>
                        <#assign cateCount = cateCount + 1/>
                 </#list>
            </#list>
	      </div>
	    </div>
     </div>
</#if>
