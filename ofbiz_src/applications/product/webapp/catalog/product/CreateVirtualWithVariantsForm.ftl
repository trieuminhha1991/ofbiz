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
<form action="<@ofbizUrl>quickCreateVirtualWithVariants</@ofbizUrl>" method="post" name="quickCreateVirtualWithVariants">
<table cellspacing="0">
    <tr class="header-row">
        <td><h3 class="header smaller lighter blue">${uiLabelMap.ProductQuickCreateVirtualFromVariants}</h3></td>
    </tr>
    <tr>
        <td>
            <br />
            <span>${uiLabelMap.ProductVariantProductIds}:</span>
            <textarea name="variantProductIdsBag" rows="6" cols="20"></textarea>
            <span>Hazmat:</span>
            <select name="productFeatureIdOne">
                <option value="">- ${uiLabelMap.CommonNone} -</option>
                <#list hazmatFeatures as hazmatFeature>
                    <option value="${hazmatFeature.productFeatureId}">${hazmatFeature.description}</option>
                </#list>
            </select>
            <button type="submit" class="btn btn-mini btn-primary" style="margin-bottom:10px;">
            	<i class="icon-ok"></i>
            	${uiLabelMap.ProductCreateVirtualProduct}
            </button>
        </td>
    </tr>
</table>
</form>