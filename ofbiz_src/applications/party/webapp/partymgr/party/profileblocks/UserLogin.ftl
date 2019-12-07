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

  <div id="partyUserLogins" class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.PartyUserName}</h4>
        <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
        <span class="widget-toolbar ">
          <li><a  class="icon-plus-sign open-sans" style="text-decoration: blink;" href="<@ofbizUrl>ProfileCreateNewLogin?partyId=${party.partyId}</@ofbizUrl>">&nbsp${uiLabelMap.CommonCreateNew}</a></li>
        </span>
        </#if>
      <br class="clear" />
    </div>
    <div class="widget-body">
      <#if userLogins?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
          <#list userLogins as userUserLogin>
            <tr>
              <td>${uiLabelMap.PartyUserLogin}</td>
              <td>${userUserLogin.userLoginId}</td>
              <td>
                <#assign enabled = uiLabelMap.PartyEnabled>
                <#if (userUserLogin.enabled)?default("Y") == "N">
                  <#if userUserLogin.disabledDateTime?exists>
                    <#assign disabledTime = userUserLogin.disabledDateTime.toString()>
                  <#else>
                    <#assign disabledTime = "??">
                  </#if>
                  <#assign enabled = uiLabelMap.PartyDisabled>
                </#if>
                <#if disabledTime?has_content>
                	<span class="label label-inverse arrowed">${enabled}</span>
                	<br/><div style="background-color:#f2dede;border-color:#eed3d7;color:#b94a48;padding:4px 0 4px 4px;">${disabledTime?trim}</div>
            	<#else>
            		<span class="label label-info arrowed arrowed-right">${enabled}</span>
                </#if>
              </td>
              <td class="button-col">
                <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
                  <a class="open-sans icon-edit" href="<@ofbizUrl>ProfileEditUserLogin?partyId=${party.partyId}&amp;userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
                </#if>
                <#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
                  <a class="open-sans icon-security " href="<@ofbizUrl>ProfileEditUserLoginSecurityGroups?partyId=${party.partyId}&amp;userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>">${uiLabelMap.SecurityGroups}</a>
                </#if>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
      <div class="padding-top8">
        <span class="p">${uiLabelMap.PartyNoUserLogin}</span>
		</div>
      </#if>
    </div>
  </div>	
