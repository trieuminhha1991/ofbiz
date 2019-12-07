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

<div class="page-title" style="margin-left:10px"><h4 class="blue">${uiLabelMap.WebtoolsImportToDataSource}</h4></div>
<p class="alert alert-info">${uiLabelMap.WebtoolsXMLImportInfo}</p>
<hr />
  <form method="post" action="<@ofbizUrl>entityImportReaders</@ofbizUrl>">
    <p style="margin-left: 20px !important">Enter Readers (comma separated, no spaces; from entityengine.xml and ofbiz-component.xml files; common ones include seed,ext,demo):</p>
    <label style="margin-left:20px">
    <input type="text" size="60" name="readers" value="${readers?default("seed")}"/>
    <span class="lbl">
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="mostlyInserts" <#if mostlyInserts?exists>checked="checked"</#if> value="true"/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMostlyInserts}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="maintainTimeStamps" <#if keepStamps?exists>checked="checked"</#if> value="true"/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMaintainTimestamps}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="createDummyFks" <#if createDummyFks?exists>checked="checked"</#if> value="true"/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCreateDummyFks}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="checkDataOnly" <#if checkDataOnly?exists>checked="checked"</#if> value="true"/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCheckDataOnly}
    </span>
    </label>
    <p style="margin-left: 20px !important">${uiLabelMap.WebtoolsTimeoutSeconds}:</p>
    <div style="margin-left: 20px !important">
    <input type="text" size="6" value="${txTimeoutStr?default("7200")}" name="txTimeout"/>
    <button  class="btn btn-purple btn-small" type="submit">
    <i class="icon-download"></i>
    ${uiLabelMap.WebtoolsImport}
    </button>
    </div>
    <br />
    <div class="button-bar" style="margin-left: 20px !important">
    
    </div>
  </form>
  <#if messages?exists>
      <hr />
      <div style="margin-left: 20px !important">
      <h4 class="blue">${uiLabelMap.WebtoolsResults}:</h4>
      </div>
      <#list messages as message>
          <p style="margin-left: 20px !important">${message}</p>
      </#list>
  </#if>
