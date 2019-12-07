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


<div >

  <div class="widget-body">
      <#if addressMatchMaps?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
          <tr class="header-row">
            <td>${uiLabelMap.PartyAddressMatchKey}</td>
            <td>=></td>
            <td>${uiLabelMap.PartyAddressMatchValue}</td>
            <td>${uiLabelMap.CommonSequence}</td>
            <td class="button-col"><a class="btn btn-mini btn-danger open-sans icon-remove" href="<@ofbizUrl>clearAddressMatchMap</@ofbizUrl>">${uiLabelMap.CommonClear} ${uiLabelMap.CommonAll}</a></td>
          </tr>
          <#assign alt_row = false>
          <#list addressMatchMaps as map>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${map.mapKey}</td>
              <td>=></td>
              <td>${map.mapValue}</td>
              <td>${map.sequenceNum?if_exists}</td>
              <td class="button-col"><a class="btn btn-mini btn-danger icon-trash open-sans" href="<@ofbizUrl>removeAddressMatchMap?mapKey=${map.mapKey}&amp;mapValue=${map.mapValue}</@ofbizUrl>">${uiLabelMap.CommonDelete}</a></td>
            </tr>
            

            <#assign alt_row = !alt_row>
          </#list>
        </table>
      </#if>
  </div>
</div>