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

<br />
<div class="widget-box no-border-bottom transparent">

  <div class="widget-header">
      <h3>${uiLabelMap.PartyShoppingLists}</h3>
       <div class="widget-toolbar">
        <form id="createEmptyShoppingList" action="<@ofbizUrl>createEmptyShoppingList</@ofbizUrl>" method="post" style="margin-bottom: 0px !important;">
          <input type="hidden" name="partyId" value="${partyId?if_exists}" />
          <a href="javascript:document.getElementById('createEmptyShoppingList').submit();" class="icon-plus-sign open-sans">${uiLabelMap.CommonCreateNew}</a>
        </form>
      </div>
    <br class="clear"/>
  </div>
  <div>
    <#if shoppingLists?has_content>
      <form class="form-padding" name="selectShoppingList" method="post" action="<@ofbizUrl>editShoppingList</@ofbizUrl>">
        <select name="shoppingListId" style="margin-top: 10px;">
          <#if shoppingList?has_content>
            <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
            <option value="${shoppingList.shoppingListId}">--</option>
          </#if>
          <#list allShoppingLists as list>
            <option value="${list.shoppingListId}">${list.listName}</option>
          </#list>
        </select>
        <input type="hidden" name="partyId" value="${partyId?if_exists}" />
        <a href="javascript:document.selectShoppingList.submit();" class="btn btn-small btn-info open-sans icon-edit">&nbsp${uiLabelMap.CommonEdit}</a>
      </form>
    <#else>
      ${uiLabelMap.PartyNoShoppingListsParty}.
    </#if>
  </div>
</div>
<br />
<#if shoppingList?has_content>
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyShoppingListDetail} - ${shoppingList.listName}</h3>
      <div class="widget-toolbar">  
      <form method="post" name="createQuoteFromShoppingListForm" action="/ordermgr/control/createQuoteFromShoppingList" style="margin-bottom: 0px !important;">
        <input type= "hidden" name= "applyStorePromotions" value= "N"/>
        <input type= "hidden" name= "shoppingListId" value= "${shoppingList.shoppingListId?if_exists}"/>
      </form>
        <a class="open-sans icon-save" href="javascript:document.updateList.submit();">${uiLabelMap.CommonSave}</a>
        <a class="open-sans icon-plus-sign" href="javascript:document.createQuoteFromShoppingListForm.submit()">${uiLabelMap.PartyCreateNewQuote}</a>
        <a class="open-sans icon-plus-sign" href="/ordermgr/control/createCustRequestFromShoppingList?shoppingListId=${shoppingList.shoppingListId?if_exists}">${uiLabelMap.PartyCreateNewCustRequest}</a>
        <a class="open-sans icon-plus-sign" href="/ordermgr/control/loadCartFromShoppingList?shoppingListId=${shoppingList.shoppingListId?if_exists}">${uiLabelMap.OrderNewOrder}</a>
    <br class="clear"/>
    </div>
  </div>
  <div class="widget-body">
    <form name="updateList" class="form-padding" method="post" action="<@ofbizUrl>updateShoppingList</@ofbizUrl>">
      <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}" />
      <input type="hidden" name="partyId" value="${shoppingList.partyId?if_exists}" />
      <table class="basic-table" cellspacing='0'>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyListName}</td>
          <td><input type="text" size="25" name="listName" value="${shoppingList.listName}" <#if shoppingList.listName?default("") == "auto-save">disabled="disabled"</#if> />
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.CommonDescription}</td>
          <td><input type="text" size="70" name="description" value="${shoppingList.description?if_exists}" <#if shoppingList.listName?default("") == "auto-save">disabled="disabled"</#if> />
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyListType}</td>
          <td>
            <select name="shoppingListTypeId" <#if shoppingList.listName?default("") == "auto-save">disabled</#if>>
              <#if shoppingListType?exists>
                <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
                <option value="${shoppingListType.shoppingListTypeId}">--</option>
              </#if>
              <#list shoppingListTypes as shoppingListType>
                <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
              </#list>
            </select>
          </td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyPublic}?</td>
          <td>
            <select name="isPublic" <#if shoppingList.listName?default("") == "auto-save">disabled</#if>>
              <option>${shoppingList.isPublic}</option>
              <option value="${shoppingList.isPublic}">--</option>
              <option>${uiLabelMap.CommonYes}</option>
              <option>${uiLabelMap.CommonNo}</option>
            </select>
          </td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyParentList}</td>
          <td>
            <select name="parentShoppingListId" <#if shoppingList.listName?default("") == "auto-save">disabled</#if>>
              <#if parentShoppingList?exists>
                <option value="${parentShoppingList.shoppingListId}">${parentShoppingList.listName?default(parentShoppingList.shoppingListId)}</option>
              </#if>
              <option value="">${uiLabelMap.PartyNoParent}</option>
              <#list allShoppingLists as newParShoppingList>
                <option value="${newParShoppingList.shoppingListId}">${newParShoppingList.listName?default(newParShoppingList.shoppingListId)}</option>
              </#list>
            </select>
            <#if parentShoppingList?exists>
              <a href="<@ofbizUrl>editShoppingList?shoppingListId=${parentShoppingList.shoppingListId}</@ofbizUrl>" class="btn btn-small btn-info open-sans">${uiLabelMap.CommonGotoParent} (${parentShoppingList.listName?default(parentShoppingList.shoppingListId)})</a>
            </#if>
          </td>
        </tr>
        <#if shoppingList.listName?default("") != "auto-save">
          <tr>
            <td>&nbsp;</td>
            <td><a href="javascript:document.updateList.submit();" class="btn btn-small btn-info open-sans icon-save">&nbsp${uiLabelMap.CommonSave}</a></td>
          </tr>
        </#if>
      </table>
    </form>
  </div>
</div>
<#if childShoppingListDatas?has_content>
<br />
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyChildShoppingList} - ${shoppingList.listName}</h3>
      <a class="btn btn-small btn-info" href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}&amp;includeChild=yes</@ofbizUrl>">${uiLabelMap.PartyAddChildListsToCart}</a>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <table class="basic-table" cellspacing="0">
      <tr class="header-row">
        <td>${uiLabelMap.PartyListName}</td>
        <td>&nbsp;</td>
      </tr>
      <#list childShoppingListDatas as childShoppingListData>
        <#assign childShoppingList = childShoppingListData.childShoppingList>
        <tr>
          <td class="button-col"><a class="btn btn-mini btn-info" href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a></li>
          <td class="button-col align-float">
            <a class="btn btn-mini btn-info" href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyGotoList}</a>
            <a class="btn btn-mini btn-info" href="<@ofbizUrl>addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyAddListToCart}</a>
          </td>
        </tr>
      </#list>
    </table>
  </div>
</div>
</#if>
<br />
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyListItems} - ${shoppingList.listName}</h3>
        <#-- <li><a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyAddListToCart}</a></li> -->
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <#if shoppingListItemDatas?has_content>
        <#-- Pagination -->
        <#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
        <#assign commonUrl = "editShoppingList?partyId=" + partyId + "&shoppingListId="+shoppingListId?if_exists+"&"/>
        <#assign viewIndexFirst = 0/>
        <#assign viewIndexPrevious = viewIndex - 1/>
        <#assign viewIndexNext = viewIndex + 1/>
        <#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
        <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
        <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
        <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="nav-pager" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
      <table class="table table-hover table-bordered table-striped dataTable" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.PartyProduct}</td>
          <td>${uiLabelMap.PartyQuantity}</td>
          <td>${uiLabelMap.PartyQuantityPurchased}</td>
          <td>${uiLabelMap.PartyPrice}</td>
          <td>${uiLabelMap.PartyTotal}</td>
          <td>&nbsp;</td>
        </tr>
        <#assign alt_row = false>
        <#list shoppingListItemDatas[lowIndex..highIndex-1] as shoppingListItemData>
          <#assign shoppingListItem = shoppingListItemData.shoppingListItem>
          <#assign product = shoppingListItemData.product>
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)>
          <#assign unitPrice = shoppingListItemData.unitPrice>
          <#assign totalPrice = shoppingListItemData.totalPrice>
          <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists>
          <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")>
          <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            <td><a class="btn btn-mini btn-info open-sans" href="/catalog/control/EditProduct?productId=${shoppingListItem.productId}&amp;externalLoginKey=${requestAttributes.externalLoginKey}">${shoppingListItem.productId} -
              ${productContentWrapper.get("PRODUCT_NAME")?default("No Name")}</a> : ${productContentWrapper.get("DESCRIPTION")?if_exists}
            </td>
            <form method="post" action="<@ofbizUrl>removeFromShoppingList</@ofbizUrl>" name='removeform_${shoppingListItem.shoppingListItemSeqId}'>
              <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}" />
              <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}" />
            </form>
            <form method="post" action="<@ofbizUrl>updateShoppingListItem</@ofbizUrl>" name='listform_${shoppingListItem.shoppingListItemSeqId}'>
              <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}" />
              <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}" />
              <td>
                <input size="6" type="text" name="quantity" value="${shoppingListItem.quantity?string.number}" />
              </td>
              <td>
                <input size="6" type="text" name="quantityPurchased"
                  <#if shoppingListItem.quantityPurchased?has_content>
                    value="${shoppingListItem.quantityPurchased?if_exists?string.number}"
                  </#if> />
              </td>
            </form>
            <td class="align-float"><@ofbizCurrency amount=unitPrice isoCode=currencyUomId/></td>
            <td class="align-float"><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></td>
            <td class="button-col align-float">
              <a class="btn btn-small btn-info open-sans icon-edit" href="javascript:document.listform_${shoppingListItem.shoppingListItemSeqId}.submit();">&nbsp${uiLabelMap.CommonUpdate}</a>
              <a class="btn btn-small btn-danger open-sans icon-trash" href="javascript:document.removeform_${shoppingListItem.shoppingListItemSeqId}.submit();">&nbsp${uiLabelMap.CommonRemove}</a>
            </td>
          </tr>
          <#-- toggle the row color -->

          <#assign alt_row = !alt_row>
        </#list>
      </table>
    <#else>
      ${uiLabelMap.PartyShoppingListEmpty}.
    </#if>
  </div>
</div>
<br />
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3 class="h3">${uiLabelMap.PartyQuickAddList}</h3>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <form class="form-padding" name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
      <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}" />
      <input type="hidden" name="partyId" value="${shoppingList.partyId?if_exists}" />
      <input style="margin-top: 10px;" type="text" name="productId" value="" />
      <input style="margin-top: 10px;" type="text" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" />
      <button type="submit" class="btn btn-small btn-info">
      <i class="icon-ok">
      </i>
      ${uiLabelMap.PartyAddToShoppingList}
      </button>
    </form>
  </div>
</div>
</#if>
<!-- begin editShoppingList.ftl -->