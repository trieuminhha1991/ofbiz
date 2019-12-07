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

  <div id="partyNotes" class="widget-box transparent no-bottom-border">
    <div class="widget-header">
        <h4>${uiLabelMap.CommonNotes}</h4>
        
        <#if security.hasEntityPermission("PARTYMGR", "_NOTE", session)>
        <span class="widget-toolbar">
          <li><a class="icon-plus-sign open-sans" style="text-decoration: blink; font-size:14px;" href="<@ofbizUrl>AddPartyNote?partyId=${partyId}</@ofbizUrl>">&nbsp${uiLabelMap.CommonCreateNew}</a></li>
         </span>
        </#if>
      <br class="clear" />
    </div>
    <div class="widget-body">
      <#if notes?has_content>
        <table width="100%" border="0" cellpadding="1" class="table table-hover table-bordered table-striped dataTable">
          <#list notes as noteRef>
            <tr>
              <td>
                <div><b>${uiLabelMap.FormFieldTitle_noteName}: </b>${noteRef.noteName?if_exists}</div>
                <#if noteRef.noteParty?has_content>
                  <div><b>${uiLabelMap.CommonBy}: </b>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, noteRef.noteParty, true)}</div>
                </#if>
                <div><b>${uiLabelMap.CommonAt}: </b>${noteRef.noteDateTime.toString()}</div>
              </td>
              <td>
                ${noteRef.noteInfo}
              </td>
            </tr>
            <#if noteRef_has_next>
            </#if>
          </#list>
        </table>
      <#else>
      <div class="padding-top8">
       <span class="p"> ${uiLabelMap.PartyNoNotesForParty}</span>
       </div>
      </#if>
    </div>
  </div>
