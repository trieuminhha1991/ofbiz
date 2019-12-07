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
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.PageTitleRequestItems}</li>
        </ul>
        <br class="clear"/>
    </div>
    <table class="table table-striped table-hover table-bordered dataTable"  >
    <tr class="header-row">
           <td style="width:20%">
              ${uiLabelMap.CommonNbr}
           </td>
           <td colspan="2" style="width:100%">
              ${uiLabelMap.CommonDescription}
           </td>
       </tr>
    <#list custRequestItems as custRequestItemList>
    <#if custRequestItemList.custRequestItemSeqId?has_content>
      
        <tr>
            <td>
              <a href="<@ofbizUrl>requestitem?custRequestId=${custRequestItemList.custRequestId}&amp;custRequestItemSeqId=${custRequestItemList.custRequestItemSeqId}</@ofbizUrl>" class="linktext">${custRequestItemList.custRequestItemSeqId}</a>
            </td>
            <td colspan="2">
              <#if custRequestItemList.story?has_content>
                <textarea readonly="readonly" rows="10" cols="100" style="width:90%;" >${custRequestItemList.story}</textarea>
              </#if>
            </td>
            
            <#-- now show notes details per line item -->
            <td colspan="1" align="right" valign="top" width="30%" nowrap="nowrap" style="background-color:white; vertical-align: top;">
                <#if custRequestItemNoteViews?has_content>
                    <table class="table table-striped table-hover table-bordered dataTable" >
                        <tr class="header-row">
                            <td>
                            </td>
                            <td>
                                ${uiLabelMap.CommonNbr}
                            </td>
                            <td>
                                ${uiLabelMap.CommonNote}
                            </td>
                            <td>
                                ${uiLabelMap.PartyParty} ${uiLabelMap.PartyName}
                            </td>
                            <td>
                                ${uiLabelMap.CommonDate}
                            </td>
                        </tr>
                        <#list custRequestItemNoteViews as custRequestItemNoteViewList>
                            <#if custRequestItemNoteViewList.custRequestItemSeqId == custRequestItemList.custRequestItemSeqId>
                            <#if row?has_content>
                                 <#assign row="">
                                 <#else>
                                     <#assign row="alternate-row">
                            </#if>
                            <#assign partyNameView = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", custRequestItemNoteViewList.partyId), false)?if_exists/>
                            <tr class="${row}">
                                <td>
                                </td>
                                <td>
                                   ${custRequestItemNoteViewList.noteId}
                                </td>
                                <td >
                                   ${custRequestItemNoteViewList.noteInfo}
                                </td>
                                <td >
                                   ${partyNameView.groupName?if_exists} ${partyNameView.firstName?if_exists} ${partyNameView.lastName?if_exists}
                                </td>
                                <td>
                                   ${custRequestItemNoteViewList.noteDateTime.toString().substring(0,10)}
                                </td>
                            </tr>
                            </#if>
                        </#list>
                    </table>
                </#if>
                <div style="text-align:center">
                <a  href="<@ofbizUrl>requestitemnotes?custRequestId=${custRequestItemList.custRequestId}&amp;custRequestItemSeqId=${custRequestItemList.custRequestItemSeqId}</@ofbizUrl>" class="linktext open-sans icon-plus-sign btn btn-small btn-primary">${uiLabelMap.OrderAddNote}</a>
            	</div>
            </td>
        </tr>
    </#if>
    </#list>
    </table>
</div>