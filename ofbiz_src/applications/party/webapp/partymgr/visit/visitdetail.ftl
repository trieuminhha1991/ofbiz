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

<!-- begin visitdetail.ftl -->
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyVisitDetail}</h3>
    <br class="clear"/>
  </div>
  <div >
      <table class="basic-table" cellspacing="0">
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyVisitIDSessionID}</td>
          <td>${visit.visitId?if_exists} / ${visit.sessionId?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyVisitorId}</td>
          <td>${visit.visitorId?default("${uiLabelMap.CommonNot} ${uiLabelMap.CommonFound}")}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyPartyIDUserLoginID}</td>
          <td><a  href="<@ofbizUrl>viewprofile?partyId=${visit.partyId?if_exists}</@ofbizUrl>">${visit.partyId?if_exists}</a> / <a  href="<@ofbizUrl>viewprofile?partyId=${visit.partyId?if_exists}</@ofbizUrl>">${visit.userLoginId?if_exists}</a></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyUserCreated}</td>
          <td>${visit.userCreated?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyWebApp}</td>
          <td>${visit.webappName?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyServer}</td>
          <td><a  href="http://uptime.netcraft.com/up/graph/?site=${visit.serverIpAddress?if_exists}" target="_blank">${visit.serverIpAddress?if_exists}</a> / <a href="http://uptime.netcraft.com/up/graph/?site=${visit.serverIpAddress?if_exists}" target="_blank">${visit.serverHostName?if_exists}</a></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyClient}</td>
          <td><a  href="http://ws.arin.net/cgi-bin/whois.pl?queryinput=${visit.clientIpAddress?if_exists}" target="_blank">${visit.clientIpAddress?if_exists}</a> / <a href="http://www.networksolutions.com/cgi-bin/whois/whois?STRING=${visit.clientHostName?if_exists}&amp;SearchType=do" target="_blank">${visit.clientHostName?if_exists}</a></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyClientUser}</td>
          <td>${visit.clientUser?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyInitialLocale}</td>
          <td>${visit.initialLocale?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyInitialRequest}</td>
          <td><a  href="${visit.initialRequest?if_exists}" >${visit.initialRequest?if_exists}</a></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyInitialReferer}</td>
          <td><a  href="${visit.initialReferrer?if_exists}" >${visit.initialReferrer?if_exists}</a></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyInitialUserAgent}</td>
          <td>${visit.initialUserAgent?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyCookie}</td>
          <td>${visit.cookie?if_exists}</td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.CommonFromDateThruDate}</td>
          <td>${(visit.fromDate?string)?if_exists} / ${(visit.thruDate?string)?default(uiLabelMap.PartyStillActive)}</td>
        </tr>
      </table>
  </div>
</div>
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyHitTracker}</h3>
    <br class="clear"/>
  </div>
  <div class="widget-body">
      <#if serverHits?has_content>
        <div class="align-float">
          <span class="olbius-label">
            <#if 0 < viewIndex>
              <a  href="<@ofbizUrl>visitdetail?visitId=${visitId}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="smallSubmit">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if 0 < listSize>
              ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
            </#if>
            <#if highIndex < listSize>
              | <a  href="<@ofbizUrl>visitdetail?visitId=${visitId}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="smallSubmit">${uiLabelMap.CommonNext}</a>
            </#if>
          </span>
        </div>
        <br class="clear"/>
      </#if>
      <table class="table table-hover table-bordered table-striped dataTable" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.PartyContentId}</td>
          <td>${uiLabelMap.PartyType}</td>
          <td>${uiLabelMap.PartySize}</td>
          <td>${uiLabelMap.PartyStartTime}</td>
          <td>${uiLabelMap.PartyTime}</td>
          <td>${uiLabelMap.PartyURI}</td>
        </tr>
        <#-- set initial row color -->
        <#assign alt_row = false>
        <#if serverHits?has_content>
        <#list serverHits[lowIndex..highIndex-1] as hit>
          <#assign serverHitType = hit.getRelatedOne("ServerHitType", false)?if_exists>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <td>${hit.contentId?if_exists}</td>
            <td>${serverHitType.get("description",locale)?if_exists}</td>
            <td>&nbsp;&nbsp;${hit.numOfBytes?default("?")}</td>
            <td>${hit.hitStartDateTime?string?if_exists}</td>
            <td>${hit.runningTimeMillis?if_exists}</td>
            <td>
              <#assign url = (hit.requestUrl)?if_exists>
              <#if url?exists>
                <#assign len = url?length>
                <#if 45 < len>
                  <#assign url = url[0..45] + "...">
                </#if>
              </#if>
              <a  href="${hit.requestUrl?if_exists}" target="_blank">${url}</a>
            </td>
          </tr>
          <#-- toggle the row color -->
          <#assign alt_row = !alt_row>
        </#list>
        <#else/>
          <tr>
            <td colspan="6">${uiLabelMap.PartyNoServerHitsFound}</td>
          </tr>
        </#if>
      </table>
      <#if serverHits?has_content>
        <div class="align-float">
          <span class="olbius-label">
            <#if 0 < viewIndex>
              <a  href="<@ofbizUrl>visitdetail?visitId=${visitId}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex-1}</@ofbizUrl>" class="smallSubmit">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if 0 < listSize>
              ${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}
            </#if>
            <#if highIndex < listSize>
              | <a  href="<@ofbizUrl>visitdetail?visitId=${visitId}&amp;VIEW_SIZE=${viewSize}&amp;VIEW_INDEX=${viewIndex+1}</@ofbizUrl>" class="smallSubmit">${uiLabelMap.CommonNext}</a>
            </#if>
          </span>
        </div>
        <br class="clear"/>
      </#if>
  </div>
</div>

<!--
*******************************************************************************
JIRA OFBIZ-4488: BEGIN
https://issues.apache.org/jira/browse/OFBIZ-4488
*******************************************************************************
<div class="widget-box">
  <div class="widget-header">
      <h3>${uiLabelMap.PartyPagePushFollowing}</h3>
    <br class="clear"/>
  </div>  
  <div class="widget-body">
      <#if security.hasPermission("SEND_CONTROL_APPLET", session)>
        <table class="basic-table" cellspacing="0">
            <tr>
              <th>${uiLabelMap.PartyPushURL}</th>
              <td>
                <form name="pushPage" method="get" action="<@ofbizUrl>pushPage</@ofbizUrl>">
                  <input type="hidden" name="followerSid" value="${visit.sessionId}" />
                  <input type="hidden" name="visitId" value="${visit.visitId}" />
                  <input type="text" name="pageUrl" />
                  <button type="submit" class="btn btn-small btn-info">
                  <i class="icon-ok">
                  ${uiLabelMap.CommonSubmit}
                  </button>
                </form>
              </td>
            </tr>
            <tr>
              <td colspan="3"><hr /></td>
            </tr>
            <tr>
              <th>${uiLabelMap.PartyFollowSession}</th>
              <td>
                <form name="setFollower" method="get" action="<@ofbizUrl>setAppletFollower</@ofbizUrl>">
                  <input type="hidden" name="followerSid" value="${visit.sessionId}" />
                  <input type="hidden" name="visitId" value="${visit.visitId}" />
                  <input type="text" name="followSid" />
                  <button type="submit" class="btn btn-small btn-info">
                  <i class="icon-ok">
                  </i>
                  ${uiLabelMap.CommonSubmit}
                  </button>
                </form>
              </td>
            </tr>
        </table>
      </#if>
  </div>
</div>
*******************************************************************************
JIRA OFBIZ-4488: END
*******************************************************************************
-->

