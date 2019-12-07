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

<div id="address-match-map">
  <div>
  <div class="widget-box transparent no-border-bottom">
      <div class="widget-header">
      <h3>${uiLabelMap.PageTitleAddressMatchMap}</h3>
      <div class="widget-toolbar">
      <a class="icon-search open-sans" href="<@ofbizUrl>findAddressMatch</@ofbizUrl>">${uiLabelMap.PageTitleFindMatches}</a>
    	</div>
    </div>
	</div>
  <div >
    <form class="form-padding" name="addaddrmap" method="post" action="<@ofbizUrl>createAddressMatchMap</@ofbizUrl>">
    <div class="row-fluid">
    <div class="span12">
    <div class="span6">
    <table class="basic-table" cellspacing="0">
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyAddressMatchKey}</td>
          <td><input type="text" name="mapKey"/></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.PartyAddressMatchValue}</td>
          <td><input type="text" name="mapValue"/></td>
        </tr>
        <tr>
          <td class="olbius-label">${uiLabelMap.CommonSequence}</td>
          <td><input type="text" size="5" name="sequenceNum" value="0"/></td>
        </tr>
        <tr>
          <td></td>
          <td><a href="javascript:document.addaddrmap.submit()" class=" btn btn-mini btn-info open-sans icon-plus-sign">&nbsp${uiLabelMap.CommonCreate}</a></td>
        </tr>
    </table>
    </form>
    </div>
    <div class="span6">
    <table class="basic-table" cellspacing="0">
      <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td></td>
          <td>
            <form name="importaddrmap" method="post" enctype="multipart/form-data" action="<@ofbizUrl>importAddressMatchMapCsv</@ofbizUrl>">
            <div class="ace-file-input" style="display: block">
            <input type="file" name="uploadedFile" size="14" id="uploadedFile"/>
            <label style="width: 150px" data-title="Choose" for="uploadedFile"><span data-title="No File ..."><i class="icon-upload-alt"></i></span></label>
            <a class="remove" href="#"><i class="icon-remove"></i></a>
             <button type="submit" class="btn btn-small btn-info" style="float: right">
            <i class="icon-cloud-upload">
            </i>
            ${uiLabelMap.CommonUpload} CSV
            </button>
            </div>
            <p class="alert alert-info">${uiLabelMap.PartyAddressMatchMessage1}</p>
            </form>
          </td>
        </tr>
    </table>
    </div>
    </div>
    </div>
  </div>
</div>
<!--<div class="widget-box">
<div class="widget-box transparent no-border-bottom">
  <div class="widget-header">
      <h3>${uiLabelMap.PageTitleAddressMatchMap}</h3>
    <br class="clear"/>
  </div>
  </div>
  <div class="widget-body">
      <#if addressMatchMaps?has_content>
        <table class="table table-hover table-striped table-bordered dataTable" cellspacing="0">
          <tr class="header-row">
            <td>${uiLabelMap.PartyAddressMatchKey}</td>
            <td>=></td>
            <td>${uiLabelMap.PartyAddressMatchValue}</td>
            <td>${uiLabelMap.CommonSequence}</td>
            <td class="button-col"><a class="btn btn-mini btn-danger" href="<@ofbizUrl>clearAddressMatchMap</@ofbizUrl>">${uiLabelMap.CommonClear} ${uiLabelMap.CommonAll}</a></td>
          </tr>
          <#assign alt_row = false>
          <#list addressMatchMaps as map>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${map.mapKey}</td>
              <td>=></td>
              <td>${map.mapValue}</td>
              <td>${map.sequenceNum?if_exists}</td>
              <td class="button-col"><a class="btn btn-mini btn-danger" href="<@ofbizUrl>removeAddressMatchMap?mapKey=${map.mapKey}&amp;mapValue=${map.mapValue}</@ofbizUrl>">${uiLabelMap.CommonDelete}</a></td>
            </tr>
            

            <#assign alt_row = !alt_row>
          </#list>
        </table>
      </#if>
  </div>
</div>
<!-- end addressMatchMap.ftl -->