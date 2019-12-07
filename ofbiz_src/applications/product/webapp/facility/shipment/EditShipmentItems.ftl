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
<#if shipment?exists>
<div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
    <h3>${uiLabelMap.PageTitleEditShipmentItems}</h3>
     <span class="widget-toolbar none-content">
            	 ${screens.render("component://product/widget/facility/ShipmentScreens.xml#ProductGenerateShipmentManifestReportSubTabBar")}
        </span>
        <br class="clear"/>
    </div>
    <div class="widget-body">
        <table cellspacing="0" class="table-bordered table table-striped table-hover dataTable">
            <thead>
            <tr class="header-row">
                <td>${uiLabelMap.ShipmentItemSeqId}</td>
                <td>${uiLabelMap.ProductProductId}</td>
                <td>${uiLabelMap.ProductProductName}</td>
                <td>${uiLabelMap.ProductQuantity}</td>
                <td colspan="2">${uiLabelMap.CommonDescription}</td>
                <td>${uiLabelMap.CommonDelete}</td>
            </tr>
            </thead>
        <#assign alt_row = false>
        <#list shipmentItemDatas as shipmentItemData>
            <#assign shipmentItem = shipmentItemData.shipmentItem>
            <#assign itemIssuances = shipmentItemData.itemIssuances>
            <#assign orderShipments = shipmentItemData.orderShipments>
            <#assign shipmentPackageContents = shipmentItemData.shipmentPackageContents>
            <#assign product = shipmentItemData.product?if_exists>
            <#assign totalQuantityPackaged = shipmentItemData.totalQuantityPackaged>
            <#assign totalQuantityToPackage = shipmentItemData.totalQuantityToPackage>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td>${shipmentItem.shipmentItemSeqId}</td>
                <td>${shipmentItem.productId?if_exists}</td>
                <td>${(product.internalName)?if_exists} </td>
				<td>${shipmentItem.quantity?default("&nbsp;")}</td>
                <td colspan="2">${shipmentItem.shipmentContentDescription?default("&nbsp;")}</td>
                <td><a href="javascript:document.deleteShipmentItem${shipmentItemData_index}.submit();" ><i class="icon-trash" style="color: red"></i></a></td>
            </tr>
            <form name="deleteShipmentItem${shipmentItemData_index}" method="post" action="<@ofbizUrl>deleteShipmentItem</@ofbizUrl>">
                <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
            </form>
<!--            <#list orderShipments as orderShipment>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td>&nbsp;</td>
                    <td><span class="label">${uiLabelMap.ProductOrderItem}</span> <a href="/ordermgr/control/orderview?orderId=${orderShipment.orderId?if_exists}" class="btn btn-info btn-mini">${orderShipment.orderId?if_exists}</a> ${orderShipment.orderItemSeqId?if_exists}</td>
                    <td>&nbsp;</td>
                    <td>${orderShipment.quantity?if_exists}</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;<#-- don't allow a delete, need to implement a cancel issuance <a href="<@ofbizUrl>deleteShipmentItemIssuance?shipmentId=${shipmentId}&amp;itemIssuanceId=${itemIssuance.itemIssuanceId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDelete}</a> --></td>
                </tr>
            </#list>
            <#list itemIssuances as itemIssuance>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td>&nbsp;</td>
                    <td><span class="label">${uiLabelMap.ProductOrderItem}</span> <a href="/ordermgr/control/orderview?orderId=${itemIssuance.orderId?if_exists}" class="btn btn-info btn-mini">${itemIssuance.orderId?if_exists}</a> ${itemIssuance.orderItemSeqId?if_exists}</td>
                    <td><span class="label">${uiLabelMap.ProductInventory}</span> <a href="<@ofbizUrl>EditInventoryItem?inventoryItemId=${itemIssuance.inventoryItemId?if_exists}</@ofbizUrl>" class="btn btn-info btn-mini">${itemIssuance.inventoryItemId?if_exists}</a></td>
                    <td>${itemIssuance.quantity?if_exists}</td>
                    <td>${itemIssuance.issuedDateTime?if_exists}</td>
                    <td class="label">${uiLabelMap.ProductFuturePartyRoleList}</td>
                    <td>&nbsp;<#-- don't allow a delete, need to implement a cancel issuance <a href="<@ofbizUrl>deleteShipmentItemIssuance?shipmentId=${shipmentId}&amp;itemIssuanceId=${itemIssuance.itemIssuanceId}</@ofbizUrl>" class="btn btn-info btn-mini">${uiLabelMap.CommonDelete}</a> --></td>
                </tr>
            </#list>
-->
            <#list shipmentPackageContents as shipmentPackageContent>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <td>&nbsp;</td>
                    <td colspan="2">${uiLabelMap.ProductPackage} ${shipmentPackageContent.shipmentPackageSeqId}</td>
                    <td>${shipmentPackageContent.quantity?if_exists}&nbsp;</td>
                    <#if shipmentPackageContent.subProductId?has_content>
                    <td><span class="label">${uiLabelMap.ProductSubProduct}</span> ${shipmentPackageContent.subProductId}</td>
                    <td>${shipmentPackageContent.subProductQuantity?if_exists}</td>
                    <#else>
                    <td colspan="2">&nbsp;</td>
                    </#if>
                    <td><a href="javascript:document.deleteShipmentItemPackageContent${shipmentItemData_index}${shipmentPackageContent_index}.submit();"><i class="icon-trash" style="color: red"></i></a></td>
                </tr>
                <form name="deleteShipmentItemPackageContent${shipmentItemData_index}${shipmentPackageContent_index}" method="post" action="<@ofbizUrl>deleteShipmentItemPackageContent</@ofbizUrl>">
                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                    <input type="hidden" name="shipmentItemSeqId" value="${shipmentPackageContent.shipmentItemSeqId}"/>
                    <input type="hidden" name="shipmentPackageSeqId" value="${shipmentPackageContent.shipmentPackageSeqId}"/>
                </form>
            </#list>
            <#if (totalQuantityToPackage > 0)>
                <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                    <form action="<@ofbizUrl>createShipmentItemPackageContent</@ofbizUrl>" method="post" name="createShipmentPackageContentForm${shipmentItemData_index}">
                    <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                    <input type="hidden" name="shipmentItemSeqId" value="${shipmentItem.shipmentItemSeqId}"/>
                    <td>&nbsp;</td>
                    <td colspan="2">
                        <div><span class="label" style="color: green;">${uiLabelMap.ProductAddToPackage}</span>
                        <select name="shipmentPackageSeqId">
                            <#list shipmentPackages as shipmentPackage>
                                <option>${shipmentPackage.shipmentPackageSeqId}</option>
                            </#list>
                            <option value="New">${uiLabelMap.CommonNew}</option><!-- Warning: the "New" value cannot be translated because it is used in secas -->
                        </select>
                        </div>
                    </td>
                    <td>
                        <div>
                            <input type="text" name="quantity" size="5" value="${totalQuantityToPackage}"/>
                            <a href="javascript:document.createShipmentPackageContentForm${shipmentItemData_index}.submit()"><i class="icon-plus open-sans"></i></a>
                        </div>
                    </td>
                    <td colspan="2">&nbsp;</td>
                    <td>&nbsp;</td>
                    </form>
                </tr>
            </#if>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
<!--        <tr>
            <form action="<@ofbizUrl>createShipmentItem</@ofbizUrl>" method="post" name="createShipmentItemForm">
                <input type="hidden" name="shipmentId" value="${shipmentId}"/>
                <td><span class="label">${uiLabelMap.ProductNewItem}</span></td>
                <td colspan="2"><span class="label">${uiLabelMap.ProductProductId}</span> 
<#--                 <@htmlTemplate.lookupField formName="createShipmentItemForm" name="productId" id="productId" fieldFormName="LookupProduct"/> -->
                </td>
                <td><input type="text" name="quantity" size="5" value="0"/></td>
                <td colspan="2"><span class="label">${uiLabelMap.ProductProductDescription}</span> <input name="shipmentContentDescription" size="30" maxlength="255"/></td>
                <td><a href="javascript:document.createShipmentItemForm.submit()" class="btn btn-info btn-mini">${uiLabelMap.CommonCreate}</a></td>
            </form>
        </tr>
-->
        </table>
    </div>
</div>
<#else>
<div class="widget-box">
    <div class="widget-header">
    <h3>${uiLabelMap.ProductShipmentNotFoundId} : [${shipmentId?if_exists}]</h3>
        <br class="clear"/>
    </div>
</div>
</#if>