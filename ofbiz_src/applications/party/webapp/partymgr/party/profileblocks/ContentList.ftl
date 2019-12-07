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
  <div id="partyContentList" style="overflow-x:scroll;width:99%;display:inline-block;">
      <#if partyContent?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
          <#list partyContent as pContent>
            <#assign content = pContent.getRelatedOne("Content", false)>
            <#assign contentType = content.getRelatedOne("ContentType", true)>
            <#assign mimeType = content.getRelatedOne("MimeType", true)?if_exists>
            <#assign status = content.getRelatedOne("StatusItem", true)?if_exists>
            <#assign pcType = pContent.getRelatedOne("PartyContentType", false)>
            <tr>
              <td><a class="btn btn-mini btn-primary" href="<@ofbizUrl>EditPartyContents?contentId=${pContent.contentId}&amp;partyId=${pContent.partyId}&amp;partyContentTypeId=${pContent.partyContentTypeId}&amp;fromDate=${pContent.fromDate}</@ofbizUrl>">${content.contentId}</a></td>
              <td>${(pcType.get("description", locale))?if_exists}</td>
              <td>${content.contentName?if_exists}</td>
              <td>${(contentType.get("description",locale))?if_exists}</td>
              <td>${(mimeType.description)?if_exists}</td>
              <td>${(status.get("description",locale))?if_exists}</td>
              <td>${pContent.fromDate?if_exists}</td>
              <td>
                <#if (content.contentName?has_content)>
                    <a class="btn btn-mini btn-primary" href="<@ofbizUrl>img/${content.contentName}?imgId=${content.dataResourceId}</@ofbizUrl>">${uiLabelMap.CommonView}&nbsp;<i class="icon-info-sign"></i></a>
                </#if>
                <form name="removePartyContent_${pContent_index}" method="post" action="<@ofbizUrl>removePartyContent/viewprofile</@ofbizUrl>">
                  <input type="hidden" name="contentId" value="${pContent.contentId}" />
                  <input type="hidden" name="partyId" value="${pContent.partyId}" />
                  <input type="hidden" name="partyContentTypeId" value="${pContent.partyContentTypeId}" />
                  <input type="hidden" name="fromDate" value="${pContent.fromDate}" />
                  <a class="btn btn-mini btn-danger margin-top8" href="javascript:document.removePartyContent_${pContent_index}.submit()"><i class="icon-trash"></i>${uiLabelMap.CommonRemove}</a>
                </form>
              </td>
            </tr>
          </#list>
        </table>
      <#else>
      <div class="padding-top8">
        <span class="p">${uiLabelMap.PartyNoContent}</span>
		</div>  
      </#if>
  </div>