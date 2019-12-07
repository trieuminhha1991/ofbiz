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

  <div id="partyVisits" class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.PartyVisits}</h4>
        <span class="widget-toolbar">
        <li><a class="open-sans icon-list" style="text-decoration: blink;" href="<@ofbizUrl>findVisits?partyId=${partyId}</@ofbizUrl>">&nbsp${uiLabelMap.CommonListAll}</a></li>
        </span>
      <br class="clear" />
    </div>
    <div class="widget-body">
      <#if visits?has_content>
        <table class="table table-hover table-bordered table-striped dataTable" cellspacing="0">
          <tr class="header-row">
            <td>${uiLabelMap.PartyVisitId}</td>
            <td>${uiLabelMap.PartyUserLogin}</td>
            <td>${uiLabelMap.PartyNewUser}</td>
            <td>${uiLabelMap.PartyWebApp}</td>
            <td>${uiLabelMap.PartyClientIP}</td>
            <td>${uiLabelMap.CommonFromDate}</td>
            <td>${uiLabelMap.CommonThruDate}</td>
          </tr>
          <#list visits as visitObj>
            <#if (visitObj_index > 4)><#break></#if>
              <tr>
                <td class="button-col">
                  <a  href="<@ofbizUrl>visitdetail?visitId=${visitObj.visitId?if_exists}</@ofbizUrl>">${visitObj.visitId?if_exists}</a>
                </td>
                <td>${visitObj.userLoginId?if_exists}</td>
                <td>${visitObj.userCreated?if_exists}</td>
                <td>${visitObj.webappName?if_exists}</td>
                <td>${visitObj.clientIpAddress?if_exists}</td>
                <td>${(visitObj.fromDate.toString())?if_exists}</td>
                <td>${(visitObj.thruDate.toString())?if_exists}</td>
              </tr>
          </#list>
        </table>
      <#else>
      <div class="padding-top8">
        <span type="p">${uiLabelMap.PartyNoVisitFound}</span>
      </div>
      </#if>
    </div>
  </div>