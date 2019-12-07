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

<script language="JavaScript" type="text/javascript">
    function showQohAtp() {
        document.qohAtpForm.productId.value = document.quickaddform.add_product_id.value;
        document.qohAtpForm.submit();
    }
    function quicklookupGiftCertificate() {
        window.location='AddGiftCertificate';
    }
</script>
<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
  <#assign target="productAvailabalityByFacility">
<#else>
  <#assign target="getProductInventoryAvailable">
</#if>
<div class="widget-box olbius-extra ">
<div class="widget-box transparent no-bottom-border">
	<!--<div class="widget-header header smaller lighter blue">
		<h3>${uiLabelMap.CommonCreate}&nbsp;
		<#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
            ${uiLabelMap.OrderPurchaseOrder}
        <#else>
            ${uiLabelMap.OrderSalesOrder}
        </#if>
        </h3>
   </div>-->
	
    <div class="widget-body">
    <div class="widget-body-inner">
   		<div class="widget-main">
      <#if shoppingCart.getOrderType() == "SALES_ORDER">
        <div>
          <#if quantityOnHandTotal?exists && availableToPromiseTotal?exists && (productId)?exists>
            <ul>
              <li>
                <label>${uiLabelMap.ProductQuantityOnHand}</label>: ${quantityOnHandTotal}
              </li>
              <li>
                <label>${uiLabelMap.ProductAvailableToPromise}</label>: ${availableToPromiseTotal}
              </li>
            </ul>
          </#if>
        </div>
      <#else>
        <#if parameters.availabalityList?has_content>
          <table>
            <tr>
              <td>${uiLabelMap.Facility}</td>
              <td>${uiLabelMap.ProductQuantityOnHand}</td>
              <td>${uiLabelMap.ProductAvailableToPromise}</td>
            </tr>
            <#list parameters.availabalityList as availabality>
               <tr>
                 <td>${availabality.facilityId}</td>
                 <td>${availabality.quantityOnHandTotal}</td>
                 <td>${availabality.availableToPromiseTotal}</td>
               </tr>
            </#list>
          </table>
        </#if>
      </#if>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>
            <form name="qohAtpForm" method="post" action="<@ofbizUrl>${target}</@ofbizUrl>">
              <fieldset>
                <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
                <input type="hidden" name="productId"/>
                <input type="hidden" id="ownerPartyId" name="ownerPartyId" value="${shoppingCart.getBillToCustomerPartyId()?if_exists}" />
              </fieldset>
            </form>
            <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="quickaddform" style="margin: 0;">
              <table border="0">
                <tr>
                  <td align="right"><div>${uiLabelMap.ProductProductId} :</div></td>
                  <td>
                    <span class='tabletext'>
                      <#if orderType=="PURCHASE_ORDER">                        
                        <#if partyId?has_content>                                               
                          <#assign fieldFormName="LookupSupplierProduct?partyId=${partyId}">
                        <#else>
                          <#assign fieldFormName="LookupSupplierProduct">
                        </#if>
                      <#else>
                        <#assign fieldFormName="LookupProduct">
                      </#if>
                      <@htmlTemplate.lookupField formName="quickaddform" name="add_product_id" id="add_product_id" fieldFormName="${fieldFormName}"/>
                      <a href="javascript:quicklookup(document.quickaddform.add_product_id)" class="icon-search open-sans btn btn-small btn-primary">${uiLabelMap.OrderQuickLookup}</a>
                      <a href="javascript:quicklookupGiftCertificate()" class="icon-plus-sign open-sans btn btn-small btn-primary">${uiLabelMap.OrderAddGiftCertificate}</a>
                      <#if "PURCHASE_ORDER" == shoppingCart.getOrderType()>
                        <a href="javascript:showQohAtp()" class="btn btn-primary btn-small open-sans">${uiLabelMap.ProductAtpQoh}</a>
                      </#if>
                    </span>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderQuantity} :</div></td>
                  <td><input type="text" size="6" name="quantity" value=""/></td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderDesiredDeliveryDate} :</div></td>
                  <td>
                    <div>
                      <#if useAsDefaultDesiredDeliveryDate?exists> 
                        <#assign value = defaultDesiredDeliveryDate>
                      </#if>
                      <@htmlTemplate.renderDateTimeField name="itemDesiredDeliveryDate" value="${value!''}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="item1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                      <label>
						<input type="checkbox" name="useAsDefaultDesiredDeliveryDate" value="true"<#if useAsDefaultDesiredDeliveryDate?exists> checked="checked"</#if>/><span class="lbl"> ${uiLabelMap.OrderUseDefaultDesiredDeliveryDate}</span>
					  </label>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderShipAfterDate} :</div></td>
                  <td>
                    <div>
                      <@htmlTemplate.renderDateTimeField name="shipAfterDate" value="${shoppingCart.getDefaultShipAfterDate()!''}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="item2" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderShipBeforeDate} :</div></td>
                  <td>
                    <div>
                      <@htmlTemplate.renderDateTimeField name="shipBeforeDate" value="${shoppingCart.getDefaultShipBeforeDate()!''}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="item3" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                    </div>
                  </td>
                </tr>
                <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
                <tr>
                  <td align="right"><div>${uiLabelMap.OrderOrderItemType} :</div></td>
                  <td>
                    <div>
                      <select name="add_item_type">
                        <option value="">&nbsp;</option>
                        <#list purchaseOrderItemTypeList as orderItemType>
                        <option value="${orderItemType.orderItemTypeId}">${orderItemType.description}</option>
                        </#list>
                      </select>
                    </div>
                  </td>
                </tr>
                </#if>
                <tr>
                  <td align="right"><div>${uiLabelMap.CommonComment} :</div></td>
                  <td>
                    <div>
                      <input type="text" size="25" name="itemComment" value="${defaultComment?if_exists}" />
                      <label>
						<input type="checkbox" name="useAsDefaultComment" value="true" <#if useAsDefaultComment?exists>checked="checked"</#if> /><span class="lbl"> ${uiLabelMap.OrderUseDefaultComment}</span>
					  </label>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td></td>
                  <td>
                  	<button class="btn btn-primary btn-small open-sans" type="submit"><i class="icon-ok"></i>${uiLabelMap.OrderAddToOrder}</button>
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
        <#if shoppingCart.getOrderType() == "PURCHASE_ORDER">
        <tr><td><hr /></td></tr>
        <tr>
          <td>
            <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="bulkworkaddform" >
               <table class="table-basic margin-left56">
                		<tr>
                			<td>
                    			${uiLabelMap.OrderOrderItemType}:&nbsp;
                    		</td>
                    		<td>
                    			<select name="add_item_type"><option value="BULK_ORDER_ITEM">${uiLabelMap.ProductBulkItem}</option><option value="WORK_ORDER_ITEM">${uiLabelMap.ProductWorkItem}</option></select>
                    		</td>
                    		<td>
                    			${uiLabelMap.ProductProductCategory}:
                    		</td>
                    		<td>
                    			<@htmlTemplate.lookupField formName="bulkworkaddform" value="${requestParameters.add_category_id?if_exists}" name="add_category_id" id="add_category_id" fieldFormName="LookupProductCategory"/>
                    		</td>
                    	</tr>
                    <tr>
                    <td>
                    ${uiLabelMap.CommonDescription}:
                    </td>
                    <td>
                    	<input type="text" size="25" name="add_item_description" value=""/>
                    </td>
                    <td>
                    ${uiLabelMap.OrderQuantity}:
                    </td>
                    <td>
                    <input type="text" size="3" name="quantity" value="${requestParameters.quantity?default("1")}"/>
                    </td>
                    </tr>
                    <tr>
                    	<td>
                    	${uiLabelMap.OrderPrice}:
						</td>
						<td>
						<input type="text" size="6" name="price" value="${requestParameters.price?if_exists}"/>
						</td>
						<td>
						</td>
						<td>
                    		<button class="btn btn-primary btn-small margin-top-nav-10 open-sans" type="submit"><i class="icon-ok"></i>${uiLabelMap.OrderAddToOrder}</button>
                    	</td>
                    </tr>
                    </table>
            </form>
          </td>
        </tr>
        </#if>
      </table>
    </div>
    </div>
    </div>
    </div>
</div>

<script language="JavaScript" type="text/javascript">
  document.quickaddform.add_product_id.focus();
</script>

<!-- Internal cart info: productStoreId=${shoppingCart.getProductStoreId()?if_exists} locale=${shoppingCart.getLocale()?if_exists} currencyUom=${shoppingCart.getCurrency()?if_exists} userLoginId=${(shoppingCart.getUserLogin().getString("userLoginId"))?if_exists} autoUserLogin=${(shoppingCart.getAutoUserLogin().getString("userLoginId"))?if_exists} -->

