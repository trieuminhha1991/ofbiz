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
<div class="row-fluid">
<div class="span12">
<div class="span6">
  <form method="post" action="<@ofbizUrl>entityImportDir</@ofbizUrl>">
    <p style="margin-left:20px">${uiLabelMap.WebtoolsAbsolutePath}:</p>
    <label style="margin-left:20px">
    <input type="text" size="60" name="path" value="${path?if_exists}"/>
    <span class="lbl">
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="mostlyInserts" <#if mostlyInserts?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMostlyInserts}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="maintainTimeStamps" <#if keepStamps?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsMaintainTimestamps}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="createDummyFks" <#if createDummyFks?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCreateDummyFks}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="deleteFiles" <#if (deleteFiles?exists)>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsDeleteFiles}
    </span>
    </label>
    <label style="margin-left:20px">
    <input type="checkbox" name="checkDataOnly" <#if checkDataOnly?exists>checked="checked"</#if>/>
    <span class="lbl">
    ${uiLabelMap.WebtoolsCheckDataOnly}
    </span>
    </label>
    </div>
    <div class="span6">
    <p style="margin-left: 20px">${uiLabelMap.WebtoolsTimeoutSeconds}:</p>
    <input style="margin-left: 20px" type="text" size="6" value="${txTimeoutStr?default("7200")}" name="txTimeout"/><br />
    <p style="margin-left: 20px">${uiLabelMap.WebtoolsPause}:</p>
    <input style="margin-left: 20px" type="text" size="6" value="${filePauseStr?default("0")}" name="filePause"/>
    <button class="btn btn-purple btn-small" style="" type="submit" >
    <i class="icon-download"></i>
    ${uiLabelMap.WebtoolsImportFile}
    </button><br />
    <div class="button-bar">
    
    </div>
  </form>
  </div>
  </div>
  </div>
  <#if messages?exists>
    <hr />
    <div style="margin-left: 10px">
    <h4 class="blue">${uiLabelMap.WebtoolsResults}:</h4>
    </div>
    <#list messages as message>
        <p style="margin-left: 20px">${message}</p>
    </#list>
  </#if>
