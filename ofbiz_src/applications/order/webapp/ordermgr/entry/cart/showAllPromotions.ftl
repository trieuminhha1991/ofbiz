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
   <div class="widget-box olbius-extra ">
        <div class="widget-header widget-header-small header-color-blue2">
            <h6>${uiLabelMap.OrderSpecialOffers}</h6>
            <div class="widget-toolbar">
            	<a href="#" data-action="collapse">
            		<i class="icon-chevron-up"></i>
            	</a>
            </div>
        </div>
        <div class="widget-body">
        <div class="widget-body-inner">
    	<div class="widget-main">
    	<div>
        <ul>
        <#-- show promotions text -->
        <#list productPromosAllShowable as productPromo>
            <li class="margin-top5" style="list-style: none;"> <i class="icon-caret-right blue"></i><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDetails}</a>&nbsp;${StringUtil.wrapString(productPromo.promoText?if_exists)}</li>
        </#list>
        </ul>
    	</div>
	</div>
	</div>
	</div>
	</div>

<#if (shoppingCartSize > 0)>
  ${screens.render(promoUseDetailsInlineScreen)}
</#if>
