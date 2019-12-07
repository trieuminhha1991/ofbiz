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
  <div class="widget-box collapsed">
  <div class="widget-header widget-header-small header-color-blue2">
   <h6>${uiLabelMap.PartyUserName}</h6>
   <div class="widget-toolbar">
   		<a href="#" data-action="collapse">
   			<i class="icon-chevron-up"></i>
   		</a>
   </div>
  </div>
    <div class="widget-body" >
       <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
          <a  style="float:right;margin-top: 10px; margin-bottom: 10px; margin-right: 5px; text-decoration: none; font-size: 14px;" class="icon-plus-sign open-sans" href="<@ofbizUrl>ProfileCreateNewLogin?partyId=${party.partyId}</@ofbizUrl>">&nbsp${uiLabelMap.CommonCreateNew}</a>
        </#if>
      <#if userLogins?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0" style="margin-bottom:10px;">
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
                  <a class="btn btn-primary btn-mini" href="<@ofbizUrl>ProfileEditUserLogin?partyId=${party.partyId}&amp;userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>"><i class="icon-edit"></i>${uiLabelMap.CommonEdit}</a>
                </#if>
                <#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
                  <a class="btn btn-primary btn-mini" href="<@ofbizUrl>ProfileEditUserLoginSecurityGroups?partyId=${party.partyId}&amp;userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>">${uiLabelMap.SecurityGroups}&nbsp;<i class="icon-info-sign"></i></a>
                </#if>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
      <div style="margin-bottom:10px; margin-top:5px;" class="padding-top8">
        <span class="alert alert-warning font-size13">${uiLabelMap.PartyNoUserLogin}</span>
		</div>
      </#if>
    </div>
  </div>	
</div>
