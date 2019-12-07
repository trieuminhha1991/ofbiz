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
<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3 class="header smaller lighter blue">${uiLabelMap.ProductOverrideSimpleFields}</h3>
    </div>
    <div class="screenlet-body">
        <form action="<@ofbizUrl>updateCategoryContent</@ofbizUrl>" method="post" style="margin: 0;" name="categoryForm" id="categoryForm">
            <table cellspacing="0" class="basic-table">
                <tr>
                    <td align="right" style="padding-bottom:10px"><input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}" />${uiLabelMap.ProductProductCategoryType}</td>
                    <td>&nbsp;</td>
                    <td>
                        <select name="productCategoryTypeId" size="1">
                        <option value="">&nbsp;</option>
                        <#list productCategoryTypes as productCategoryTypeData>
                            <option <#if productCategory?has_content><#if productCategory.productCategoryTypeId==productCategoryTypeData.productCategoryTypeId> selected="selected"</#if></#if> value="${productCategoryTypeData.productCategoryTypeId}">${productCategoryTypeData.get("description",locale)}</option>
                        </#list>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td align="right" style="padding-bottom:10px">${uiLabelMap.ProductName}</td>
                    <td>&nbsp;</td>
                    <td><input type="text" value="${(productCategory.categoryName)?if_exists}" name="categoryName" size="60" maxlength="60"/></td>
                </tr>
                <tr>
                    <td align="right" style="padding-bottom:10px">${uiLabelMap.ProductCategoryDescription}</td>
                    <td>&nbsp;</td>
                    <td colspan="4" valign="top">
                        <textarea name="description" cols="60" rows="2">${(productCategory.description)?if_exists}</textarea>
                    </td>
                </tr>
                <tr>
                    <td align="right" valign="top" style="padding-bottom:10px">${uiLabelMap.ProductLongDescription}</td>
                    <td>&nbsp;</td>
                    <td colspan="4" valign="top">
                        <textarea name="longDescription" cols="60" rows="7">${(productCategory.longDescription)?if_exists}</textarea>
                    </td>
                </tr>
                <tr>
                    <td align="right" style="padding-bottom:10px">${uiLabelMap.ProductDetailScreen}</td>
                    <td>&nbsp;</td>
                    <td >
                        <input type="text" <#if productCategory?has_content>value="${productCategory.detailScreen?if_exists}"</#if> name="detailScreen" size="60" maxlength="250" />
                        <br />
                        <span class="tooltip">${uiLabelMap.ProductDefaultsTo} &quot;categorydetail&quot;, ${uiLabelMap.ProductDetailScreenMessage}: &quot;component://ecommerce/widget/CatalogScreens.xml#categorydetail&quot;</span>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">&nbsp;</td>
                    <td><button onclick="categoryForm.submit();" class="btn btn-primary btn-small" type="submit" name="Update" ><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button></td>
                    <td colspan="3">&nbsp;</td>
                </tr>
            </table>
        </form>
    </div>
</div>