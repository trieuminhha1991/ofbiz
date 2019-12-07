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

<#if shoppingCart.getOrderType() == "SALES_ORDER">
<div class="widget-box olbius-extra">
	<div class="widget-header widget-header-small header-color-blue2">
    	<h6>${uiLabelMap.OrderPromotionCouponCodes}</h6>
    	<div class="widget-toolbar">
    		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
    	</div>
    </div>
    <div class="widget-body">
    <div class="widget-body-inner">
   		<div class="widget-main">
      <div>
        <form method="post" action="<@ofbizUrl>addpromocode<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addpromocodeform" style="margin: 0;">
        <table>
        	<tr style="margin-top: 10px">
        	<td>
          <input type="text" size="15" name="productPromoCodeId" value="" />
          </td>
          <td>
          <button type="submit" class="btn btn-primary btn-small margin-top-nav-10" name="submitButton"><i class="icon-ok"></i>${uiLabelMap.OrderAddCode}</button>
          <#assign productPromoCodeIds = (shoppingCart.getProductPromoCodesEntered())?if_exists>
          <#if productPromoCodeIds?has_content>
            ${uiLabelMap.OrderEnteredPromoCodes}:
            <#list productPromoCodeIds as productPromoCodeId>
              ${productPromoCodeId}
            </#list>
          </#if>
          </td>
          </tr>
          </table>
        </form>
      </div>
    </div>
    </div>
    </div>
</div>
</#if>
