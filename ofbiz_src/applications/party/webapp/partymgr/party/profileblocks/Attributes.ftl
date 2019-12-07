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

  <div id="partyAttributes" class="widget-box transparent no-bottom-border">
    <div class="widget-header">
      
        <h4>${uiLabelMap.PartyAttributes}</h4>
        <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
        <span class="widget-toolbar">
          <li><a class=" icon-plus-sign open-sans" style="text-decoration: blink; font-size:14px;" href="<@ofbizUrl>editPartyAttribute?partyId=${party.partyId?if_exists}</@ofbizUrl>">&nbsp${uiLabelMap.CommonCreateNew}</a></li>
        </span>
        </#if>
      <br class="clear"/>
    </div>
    <div class="widget-body">
      <#if attributes?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
            <tr class="header-row">
              <td>${uiLabelMap.CommonName}</td>
              <td>${uiLabelMap.CommonValue}</td>
              <td>&nbsp;</td>
            </tr>
          <#assign alt_row = false>
          <#list attributes as attr>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>
                ${attr.attrName?if_exists}
              </td>
              <td>
                ${attr.attrValue?if_exists}
              </td>
              <td class="button-col">
                <a class="btn btn-mini btn-primary" href="<@ofbizUrl>editPartyAttribute?partyId=${partyId?if_exists}&attrName=${attr.attrName?if_exists}</@ofbizUrl>"><i class="icon-edit"></i>&nbsp;${uiLabelMap.CommonEdit}</a>
              </td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
          </#list>
        </table>
      <#else>
		<div class="padding-top8">
        <span class="p"> ${uiLabelMap.PartyNoPartyAttributesFound}</span>
        </div>
       
      </#if>
    </div>
  </div>
