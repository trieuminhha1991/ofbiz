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

  <#if monthsToInclude?exists && totalSubRemainingAmount?exists && totalOrders?exists>
    <div id="totalOrders" class="widget-box transparent no-bottom-border">
      <div class="widget-header">
          <h4>${uiLabelMap.PartyLoyaltyPoints}</h4>
        <br class="clear" />
      </div>
      <div class="widget-body">
      <div class="padding-top8">
      <span type="p">
        ${uiLabelMap.PartyYouHave} ${totalSubRemainingAmount} ${uiLabelMap.PartyPointsFrom} ${totalOrders} ${uiLabelMap.PartyOrderInLast} ${monthsToInclude} ${uiLabelMap.CommonMonths}.
      </span>
      </div>
      </div>
    </div>
  </#if>