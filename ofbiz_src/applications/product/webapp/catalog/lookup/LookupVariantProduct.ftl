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
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<br />
<div class="label">[${product.productId}] ${product.internalName}</div>
<br />
<#if searchFeatures?has_content>
    <table cellspacing="0" class="basic-table">
        <form method="post" action="<@ofbizUrl>LookupVariantProduct</@ofbizUrl>" name="selectAllForm">
        <input type="hidden" name="productId" value="${product.productId}" />
        <#list searchFeatures as searchFeature>
            <tr>
                <td class="label"><b>${searchFeature.featureType}</b></td>
                <td><select name="${searchFeature.featureType}">
                    <#assign features = searchFeature.features>
                    <option value=""></option>
                    <#list features as feature>
                        <#if searchFeature.selectedFeatureId?has_content && searchFeature.selectedFeatureId == feature.productFeatureId>
                            <option value="${feature.productFeatureId}" selected>${feature.get("description",locale)}</option>
                        <#else>
                            <option value="${feature.productFeatureId}">${feature.get("description",locale)}</option>
                        </#if>
                    </#list>
                    </select>
                </td>
            </tr>
        </#list>
        <tr>
            <td>
            	<button class="btn btn-mini btn-primary" type="submit"><i class="icon-search"></i>&nbsp;${uiLabelMap.CommonSearch}</button>
            </td>
        </form>
        <form method="post" action="<@ofbizUrl>LookupProduct</@ofbizUrl>" name="">
            <td>
            	<button class="btn btn-mini btn-info" type="submit"><i class="icon-reply"></i>&nbsp;${uiLabelMap.CommonBack}</button>
            </td>
        </form>
    </tr>
    </table>
</#if>
<style type="text/css">
	button{	
		font-size:12px;
		margin-left: 0px !important;
	}
</style>
<br />
<#if variantProducts?exists>
    <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
        <tr class="header-row">
            <td><b>${uiLabelMap.ProductProductId}</b></td>
            <td><b>${uiLabelMap.ProductBrandName}</b></td>
            <td><b>${uiLabelMap.ProductInternalName}</b></td>
        </tr>
        <#list variantProducts as variant>
            <tr>
                <td><a class="btn btn-mini btn-info" href="javascript:set_value('${variant.productId}')">${variant.productId}</a></td>
                <td>${variant.brandName?if_exists}</td>
                <td>${variant.internalName?if_exists}</td>
            </tr>
        </#list>
    </table>
</#if>
<#if productFeatureIds?exists>
    <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
        <form method="post" action="<@ofbizUrl>LookupVariantProduct</@ofbizUrl>" name="createNewVariant">
        <input type="hidden" name="productId" value="${product.productId}" />
        <input type="hidden" name="productFeatureIds" value="${productFeatureIds}" />
        <input type="text" name="productVariantId" value="${productVariantId}" />
        <button class="btn btn-mini btn-info" type="submit"><i class="icon-reply"></i>&nbsp;${uiLabelMap.ProductQuickAddVariants}</button>
        </form>
    </table>
</#if>
