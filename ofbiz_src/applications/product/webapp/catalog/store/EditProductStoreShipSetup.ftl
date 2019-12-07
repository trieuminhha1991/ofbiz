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
<form name="addscarr" method="post" action="<@ofbizUrl>prepareCreateShipMeth</@ofbizUrl>">
    <input type="hidden" name="newShipMethod" value="Y"/>
    <input type="hidden" name="productStoreId" value="${productStoreId?if_exists}"/>
    <table cellspacing="0" class="basic-table" style="margin-top:10px;">
        <tr>
          <td align="right">${uiLabelMap.ProductCarrierShipmentMethod}</td>
          <td>
            <select name="carrierShipmentString">
              <option>${uiLabelMap.ProductSelectOne}</option>
              <#list carrierShipmentMethods as shipmentMethod>
                <option value="${shipmentMethod.partyId}|${shipmentMethod.roleTypeId}|${shipmentMethod.shipmentMethodTypeId}">${shipmentMethod.shipmentMethodTypeId} (${shipmentMethod.partyId}/${shipmentMethod.roleTypeId})</option>
              </#list>
            </select>
          </td>
        </tr>
        <tr>
        	<td colspan="2"></td>
        </tr>
        <tr>
          <td></td>
          <td>
            <button type="submit" class="btn btn-small btn-primary margin-top-nav-10"><i class="icon-ok"></i>${uiLabelMap.CommonAdd}</button>
          </td>
        </tr>
    </table>
</form>
