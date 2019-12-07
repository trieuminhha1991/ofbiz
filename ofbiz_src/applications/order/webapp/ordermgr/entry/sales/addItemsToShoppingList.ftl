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

<!-- Screenlet to add cart to shopping list. The shopping lists are presented in a dropdown box. -->

<#if (shoppingLists?exists) && (shoppingCartSize > 0)>
  <div class="widget-box olbius-extra">
    <div class="widget-header widget-header-small header-color-blue2">
        <h6>${uiLabelMap.OrderAddOrderToShoppingList}</h6>
        <div class="widget-toolbar">
        	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
        </div>
    </div>
    <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>
            <form method="post" name="addBulkToShoppingList" action="<@ofbizUrl>addBulkToShoppingList</@ofbizUrl>" style='margin: 0;'>
              <#assign index = 0/>
              <#list shoppingCart.items() as cartLine>
                <#if (cartLine.getProductId()?exists) && !cartLine.getIsPromo()>
                  <input type="hidden" name="selectedItem" value="${index}"/>
                </#if>
                <#assign index = index + 1/>
              </#list>
              <table border="0">
                <tr>
                  <td>
                    <select name='shoppingListId'>
                      <#list shoppingLists as shoppingList>
                        <option value='${shoppingList.shoppingListId}'>${shoppingList.getString("listName")}</option>
                      </#list>
                        <option value="">---</option>
                        <option value="">${uiLabelMap.OrderNewShoppingList}</option>
                    </select>
                    <input type="submit" class="btn btn-small btn-primary margin-top-nav-10" value="${uiLabelMap.OrderAddToShoppingList}"/>
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </div>
    </div>
    </div>
  </div>
</#if>