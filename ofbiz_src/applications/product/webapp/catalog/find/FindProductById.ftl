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


<div id="page-content" class="clearfix">
    <div class="page-header position-relative ">
      <h1 class="margin-left-nav-2">${uiLabelMap.ProductFindProductWithIdValue}</h1>
    </div>
    <div class="screenlet-body">
        <form name="idsearchform" method="post" action="<@ofbizUrl>FindProductById</@ofbizUrl>" style="margin: 0;">
          <span class="label">${uiLabelMap.CommonId} ${uiLabelMap.CommonValue}:</span> <input class="margin-top10" type="text" name="idValue" size="20" maxlength="50" value="${idValue?if_exists}" />&nbsp;<a href="javascript:document.idsearchform.submit()" class="btn btn-primary btn-small open-sans icon-search">${uiLabelMap.CommonFind}</a>
        </form>
        <br />
        <h3 class="header smaller lighter blue">${uiLabelMap.ProductSearchResultsWithIdValue}: ${idValue?if_exists}</h3>
        <#if !goodIdentifications?has_content && !idProduct?has_content>
          <br />
          <h3 class="header smaller lighter blue">&nbsp;${uiLabelMap.ProductNoResultsFound}.</h3>
        <#else/>
          <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
            <#assign rowClass = "1">
            <#if idProduct?has_content>
            <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
                <td>
                    ${idProduct.productId}
                </td>
                <td>&nbsp;&nbsp;</td>
                <td>
                    <a href="<@ofbizUrl>EditProduct?productId=${idProduct.productId}</@ofbizUrl>" class="btn btn-info btn-small">${(idProduct.internalName)?if_exists}</a>
                    (${uiLabelMap.ProductSearchResultsFound})
                </td>
            </tr>
            </#if>
            <#list goodIdentifications as goodIdentification>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                  <#assign rowClass = "1">
                <#else>
                  <#assign rowClass = "2">
                </#if>
                <#assign product = goodIdentification.getRelatedOne("Product", true)/>
                <#assign goodIdentificationType = goodIdentification.getRelatedOne("GoodIdentificationType", true)/>
                <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
                    <td>
                        <p class="margin-top5">${product.productId}</p>
                    </td>
                    <td>&nbsp;&nbsp;</td>
                    <td>
                        <a href="<@ofbizUrl>EditProduct?productId=${product.productId}</@ofbizUrl>" class="btn btn-info btn-small">${(product.internalName)?if_exists}</a>
                        <div class="margin-top5">(${uiLabelMap.ProductSearchResultsFound} ${goodIdentificationType.get("description",locale)?default(goodIdentification.goodIdentificationTypeId)}.)</div>
                    </td>
                </tr>
            </#list>
          </table>
        </#if>
    </div>
</div>
