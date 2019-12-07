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
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.ProductEditFeaturesForFeatureCategory} "${(curProductFeatureCategory.description)?if_exists}"</h4>
        <span class="widget-toolbar none-content">
        ${screens.render("component://product/widget/catalog/FeatureScreens.xml#CreatFeatureCategorySubTabBar")}
        </span>
    </div>
    <div class="widget-body">     
        <form action="<@ofbizUrl>QuickAddProductFeatures</@ofbizUrl>" method="post">
          <div style="padding:10px;">
            ${uiLabelMap.CommonAdd}
            <input type="text" name="featureNum" value="1" size="3" style="margin-bottom: 0px;"/>
            ${uiLabelMap.ProductAddFeatureToCategory}
            <br/>
            <button class="btn btn-primary btn-small margin-top10" type="submit">
            	<i class="icon-ok">
            	</i>
            	${uiLabelMap.CommonCreate}
            </button>
          </div>
          <input type="hidden" name="productFeatureCategoryId" value="${productFeatureCategoryId}" />
        </form>
        <br />
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.ProductProductFeatureMaintenance}</h4>
    </div>
    <div class="widget-body">
        <#if (listSize > 0)>
            <#if productId?has_content>
              <#assign productString = "&amp;productId=" + productId>
            </#if>
            <table border="0" width="100%" cellpadding="2">
                <tr>
                <td align="right">
                    <span class="label">
                    <b>
                    <#if (viewIndex > 0)>
                    <a href="<@ofbizUrl>EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategoryId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}${productString?if_exists}</@ofbizUrl>" class="btn btn-mini btn-primary">[${uiLabelMap.CommonPrevious}]</a> |
                    </#if>
                    ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
                    <#if (listSize > highIndex)>
                    | <a href="<@ofbizUrl>EditFeatureCategoryFeatures?productFeatureCategoryId=${productFeatureCategoryId?if_exists}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}${productString?if_exists}</@ofbizUrl>" class="btn btn-mini btn-primary">[${uiLabelMap.CommonNext}]</a>
                    </#if>
                    </b>
                    </span>
                </td>
                </tr>
            </table>
        </#if>
        <br />
        <form method='post' action='<@ofbizUrl>UpdateProductFeatureInCategory</@ofbizUrl>' name="selectAllForm" style="margin:0px">
        <input type="hidden" name="_useRowSubmit" value="Y" />
        <input type="hidden" name="_checkGlobalScope" value="N" />
        <input type="hidden" name="productFeatureCategoryId" value="${productFeatureCategoryId}" />
    		<div style="display:block;overflow-x:scroll;position:relative">
		        <table class="table table-bordered table-hovered table-striped dataTable">
		              <tr class="header-row">
		                <td><b>${uiLabelMap.CommonId}</b></td>
		                <td><b>${uiLabelMap.CommonDescription}</b></td>
		                <td><b>${uiLabelMap.ProductFeatureType}</b></td>
		                <td><b>${uiLabelMap.ProductFeatureCategory}</b></td>
		                <td><b>${uiLabelMap.ProductUnitOfMeasureId}</b></td>
		                <td><b>${uiLabelMap.ProductQuantity}</b></td>
		                <td><b>${uiLabelMap.ProductAmount}</b></td>
		                <td><b>${uiLabelMap.ProductIdSeqNum}</b></td>
		                <td><b>${uiLabelMap.ProductIdCode}</b></td>
		                <td><b>${uiLabelMap.ProductAbbrev}</b></td>
		                <td align="right"><b>${uiLabelMap.CommonAll}
		                	<label>
								<input onclick="javascript:toggleAll(this, 'selectAllForm');highlightAllRows(this, 'productFeatureId_tableRow_', 'selectAllForm');" name="selectAll" type="checkbox" value="Y"><span class="lbl"></span>
							</label>
		                	</b>
		            	</td>
		             </tr>
		        <#if (listSize > 0)>
		            <#assign rowCount = 0>
		            <#assign rowClass = "2">
		            <#list productFeatures as productFeature>
		            <#assign curProductFeatureType = productFeature.getRelatedOne("ProductFeatureType", true)>
		            <tr id="productFeatureId_tableRow_${rowCount}" valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
		              <td><input type="hidden" name="productFeatureId_o_${rowCount}" value="${productFeature.productFeatureId}" />
		              <a href="<@ofbizUrl>EditFeature?productFeatureId=${productFeature.productFeatureId}</@ofbizUrl>" class="">${productFeature.productFeatureId}</a></td>
		              <td><input type="text" size='15' name="description_o_${rowCount}" value="${productFeature.description}" /></td>
		              <td><select name='productFeatureTypeId_o_${rowCount}' size="1">
		                <#if productFeature.productFeatureTypeId?has_content>
		                  <option value='${productFeature.productFeatureTypeId}'><#if curProductFeatureType?exists>${curProductFeatureType.get("description",locale)?if_exists}<#else> [${productFeature.productFeatureTypeId}]</#if></option>
		                  <option value='${productFeature.productFeatureTypeId}'>---</option>
		                </#if>
		                <#list productFeatureTypes as productFeatureType>
		                  <option value='${productFeatureType.productFeatureTypeId}'>${productFeatureType.get("description",locale)?if_exists}</option>
		                </#list>
		              </select></td>
		              <td><select name='productFeatureCategoryId_o_${rowCount}' size="1">
		                <#if productFeature.productFeatureCategoryId?has_content>
		                  <#assign curProdFeatCat = productFeature.getRelatedOne("ProductFeatureCategory", false)>
		                  <option value='${productFeature.productFeatureCategoryId}'>${(curProdFeatCat.description)?if_exists} [${productFeature.productFeatureCategoryId}]</option>
		                  <option value='${productFeature.productFeatureCategoryId}'>---</option>
		                </#if>
		                <#list productFeatureCategories as productFeatureCategory>
		                  <option value='${productFeatureCategory.productFeatureCategoryId}'>${productFeatureCategory.get("description",locale)?if_exists} [${productFeatureCategory.productFeatureCategoryId}]</option>
		                </#list>
		              </select></td>
		              <td><input type="text" size='10' name="uomId_o_${rowCount}" value="${productFeature.uomId?if_exists}" /></td>
		              <td><input type="text" size='5' name="numberSpecified_o_${rowCount}" value="${productFeature.numberSpecified?if_exists}" /></td>
		              <td><input type="text" size='5' name="defaultAmount_o_${rowCount}" value="${productFeature.defaultAmount?if_exists}" /></td>
		              <td><input type="text" size='5' name="defaultSequenceNum_o_${rowCount}" value="${productFeature.defaultSequenceNum?if_exists}" /></td>
		              <td><input type="text" size='5' name="idCode_o_${rowCount}" value="${productFeature.idCode?if_exists}" /></td>
		              <td><input type="text" size='5' name="abbrev_o_${rowCount}" value="${productFeature.abbrev?if_exists}" /></td>
		              <td align="right">
		              	<label>
							<input name="_rowSubmit_o_${rowCount}" type="checkbox" value="Y" onclick="javascript:checkToggle(this, 'selectAllForm');highlightRow(this,'productFeatureId_tableRow_${rowCount}');"><span class="lbl"></span>
						</label>
		              </td>
		            </tr>
		            <#assign rowCount = rowCount + 1>
		            <#-- toggle the row color -->
		            <#if rowClass == "2">
		              <#assign rowClass = "1">
		            <#else>
		              <#assign rowClass = "2">
		            </#if>
		            </#list>
		            <tr><td colspan="11" align="center">
		            <input type="hidden" name="_rowCount" value="${rowCount}" />
		        	<button type="submit" class="btn btn-small btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
		            </td></tr>
		        </#if>
		        </table>
	        </div>
        </form>
    </div>
</div>
